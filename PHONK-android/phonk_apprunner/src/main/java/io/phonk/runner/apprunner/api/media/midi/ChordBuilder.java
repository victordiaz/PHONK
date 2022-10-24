package io.phonk.runner.apprunner.api.media.midi;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ChordBuilder {

    private static final Pattern CHORD_NAME_PATTERN = Pattern.compile("^([A-G][#b]?)([a-z0-9]+)?$");
    private static final List<Integer> MAJOR_SCALE_SEMITONE_INTERVALS = Arrays.asList(2, 2, 1, 2, 2, 2, 1);

    /**
     * Builds a chord using a scale defined by it's root note and mode, and the degree of the chord you want
     * @param scaleRootNoteName the root note of the scale (see {@link io.phonk.runner.apprunner.api.media.midi.NoteNamePitchMapper})
     * @param degree the degree of chord, eg first note of the chord you want
     * @param scaleMode the mode of the scale: (I, ionian, major), (II, dorian), (III, phrygian), (IV, lydian), (V, mixolydian),
     *                  (VI, aeolian, minor), (VII, locrian)
     * @param chordSize the number of notes you want in the chord
     * @return the chord
     */
    public static List<MidiNote> usingMode(final String scaleRootNoteName, final String degree, final String scaleMode, final int chordSize) {
        final List<MidiNote> chord = new ArrayList<>();

        final List<Integer> scalePitches = getScalePitches(scaleRootNoteName, scaleMode);
        final int degreeIndex = getDegreeIndex(degree);  // first note in the chord
        for (int i = 0; i < chordSize; i++) {
            // Starting from degreeIndex, the next note in the chord will be 2 indexes away.
            // "% scalePitches.size()" does inversions if we are out of the scale.
            final int noteToAddIndex = (degreeIndex + 2 * i) % scalePitches.size();
            chord.add(new MidiNote(scalePitches.get(noteToAddIndex)));
        }
        return chord;
    }

    /**
     * Builds a chord using a chord name and an octave
     * @param chordName the name of the chord ("Gminmaj7", "Cm"...)
     * @param octave the octave of the chord (-1 to 9)
     * @return the chord
     */
    public static List<MidiNote> usingChordName(final String chordName, final int octave) {
        final Matcher m = CHORD_NAME_PATTERN.matcher(chordName);
        if (!m.matches()) {
            throw new RuntimeException(chordName + " is not a valid chordName");
        }

        // First resolve the pitch. This is the root of the chordName.
        final String note = m.group(1);
        final int rootPitch = NoteNamePitchMapper.getPitch(note + octave);

        final String name = m.group(2);
        if (name == null) {
            // By default a chordName is a major chordName, so if it is missing a name then it is assumed to mean `maj`.
            return majorChord(rootPitch);
        }

        switch (name) {
            case "maj":     return majorChord(rootPitch);
            case "m":
            case "min":     return minorChord(rootPitch);
            case "aug":     return augmentedChord(rootPitch);
            case "dim":     return diminishedChord(rootPitch);
            case "dim7":    return diminished7thChord(rootPitch);
            case "maj7b5":  return majorSeventhFlatFiveChord(rootPitch);
            case "m7":
            case "min7":    return minorSeventhChord(rootPitch);
            case "minmaj7": return minorMajorSeventhChord(rootPitch);
            case "7":
            case "dom7":    return dominantSeventhChord(rootPitch);
            case "maj7":    return majorSeventhChord(rootPitch);
            case "aug7":    return augmentedSeventhChord(rootPitch);
            case "maj7s5":  return majorSeventhSharpFiveChord(rootPitch);
            case "6":
            case "maj6":    return majorSixthChord(rootPitch);
            case "min6":    return minorSixthChord(rootPitch);
            case "sus2":    return suspendedTwoChord(rootPitch);
            case "sus4":    return suspendedFourthChord(rootPitch);
        }
        throw new RuntimeException(chordName + " is not a valid chordName");
    }

    @NonNull
    private static List<Integer> getScaleSemitoneIntervalsForMode(final String mode) {
        final int rotationNumber;
        switch (mode) {
            case "I":
            case "ionian":
            case "major":
                rotationNumber = 0;
                break;
            case "II":
            case "dorian":
                rotationNumber = -1;
                break;
            case "III":
            case "phrygian":
                rotationNumber = -2;
                break;
            case "IV":
            case "lydian":
                rotationNumber = -3;
                break;
            case "V":
            case "mixolydian":
                rotationNumber = -4;
                break;
            case "VI":
            case "aeolian":
            case "minor":
                rotationNumber = -5;
                break;
            case "VII":
            case "locrian":
                rotationNumber = -6;
                break;
            default:
                throw new RuntimeException(mode + " is un unkonwn mode");
        }
        final List<Integer> scaleSemitoneIntervals = new ArrayList<>(MAJOR_SCALE_SEMITONE_INTERVALS);
        Collections.rotate(scaleSemitoneIntervals, rotationNumber);
        return scaleSemitoneIntervals;
    }

    private static int getDegreeIndex(final String degree) {
        final int degreeIndex;
        switch (degree) {
            case "I":
                degreeIndex = 0;
                break;
            case "II":
                degreeIndex = 1;
                break;
            case "III":
                degreeIndex = 2;
                break;
            case "IV":
                degreeIndex = 3;
                break;
            case "V":
                degreeIndex = 4;
                break;
            case "VI":
                degreeIndex = 5;
                break;
            case "VII":
                degreeIndex = 6;
                break;
            default:
                throw new RuntimeException(degree + " is un unkonwn degree");
        }
        return degreeIndex;
    }

    @NonNull
    private static List<Integer> getScalePitches(final String scaleRootNoteName, final String scaleMode) {
        final List<Integer> scalePitches = new ArrayList<>();

        final List<Integer> scaleSemitoneIntervals = getScaleSemitoneIntervalsForMode(scaleMode);
        int pitch = NoteNamePitchMapper.getPitch(scaleRootNoteName);
        for (final int pitchIntervalToNextNoteInScale : scaleSemitoneIntervals) {
            scalePitches.add(pitch);
            pitch += pitchIntervalToNextNoteInScale;
        }
        return scalePitches;
    }

    // Triads
    //
    // Three note chords. These consists of the root, a third and a fifth.
    // The third and fifth intervals are shifted slightly to vary the chord's sound.

    // Major: for example Cmaj or C (root, major 3rd, perfect 5th)
    private static List<MidiNote> majorChord(final int rootPitch) {
        return Arrays.asList(new MidiNote(rootPitch), new MidiNote(majorThird(rootPitch)), new MidiNote(perfectFifth(rootPitch)));
    }

    // Minor: for example Cmin (root, minor 3rd, perfect 5th)
    private static List<MidiNote> minorChord(final int rootPitch) {
        return Arrays.asList(new MidiNote(rootPitch), new MidiNote(minorThird(rootPitch)), new MidiNote(perfectFifth(rootPitch)));
    }

    // Augmented: for example Caug (root, major 3rd, augmented 5th)
    private static List<MidiNote> augmentedChord(final int rootPitch) {
        return Arrays.asList(new MidiNote(rootPitch), new MidiNote(majorThird(rootPitch)), new MidiNote(augmentedFifth(rootPitch)));
    }

    // Diminished: for example Cdim (root, minor 3rd, diminished 5th)
    private static List<MidiNote> diminishedChord(final int rootPitch) {
        return Arrays.asList(new MidiNote(rootPitch), new MidiNote(minorThird(rootPitch)), new MidiNote(diminishedFifth(rootPitch)));
    }

    // Suspended 2: for example Csus2 (root, major 2nd, perfect 5th)
    private static List<MidiNote> suspendedTwoChord(final int rootPitch) {
        return Arrays.asList(new MidiNote(rootPitch), new MidiNote(majorSecond(rootPitch)), new MidiNote(perfectFifth(rootPitch)));
    }

    // Suspended 4: for example Csus4 (root, perfect 4th, perfect 5th)
    private static List<MidiNote> suspendedFourthChord(final int rootPitch) {
        return Arrays.asList(new MidiNote(rootPitch), new MidiNote(perfectFourth(rootPitch)), new MidiNote(perfectFifth(rootPitch)));
    }

    // Sixths
    //
    // Triads with an added fourth note that is a sixth interval
    // above the root.

    // Major Sixth: for example Cmaj6 (root, major 3rd, perfect 5th, major 6th)
    private static List<MidiNote> majorSixthChord(final int rootPitch) {
        return Arrays.asList(new MidiNote(rootPitch), new MidiNote(majorThird(rootPitch)), new MidiNote(perfectFifth(rootPitch)), new MidiNote(majorSixth(rootPitch)));
    }

    // Minor Sixth: for example Cmin6 (root, minor 3rd, perfect 5th, major 6th)
    private static List<MidiNote> minorSixthChord(final int rootPitch) {
        return Arrays.asList(new MidiNote(rootPitch), new MidiNote(minorThird(rootPitch)), new MidiNote(perfectFifth(rootPitch)), new MidiNote(majorSixth(rootPitch)));
    }

    // Sevenths
    //
    // Triads with an added fourth note that is a seventh interval
    // above the root.

    // Diminished Seventh: for example Cdim7 (root, minor 3rd, diminished 5th, diminished 7th)
    private static List<MidiNote> diminished7thChord(final int rootPitch) {
        return Arrays.asList(new MidiNote(rootPitch), new MidiNote(minorThird(rootPitch)), new MidiNote(diminishedFifth(rootPitch)), new MidiNote(diminishedSeventh(rootPitch)));
    }

    // Major Seventh Flat Five: for example Cmaj7b5 (root, minor 3rd, diminished 5th, minor 7th)
    private static List<MidiNote> majorSeventhFlatFiveChord(final int rootPitch) {
        return Arrays.asList(new MidiNote(rootPitch), new MidiNote(minorThird(rootPitch)), new MidiNote(diminishedFifth(rootPitch)), new MidiNote(minorSeventh(rootPitch)));
    }

    // Minor Seventh: for example Cmin7 (root, minor 3rd, perfect 5th, minor 7th)
    private static List<MidiNote> minorSeventhChord(final int rootPitch) {
        return Arrays.asList(new MidiNote(rootPitch), new MidiNote(minorThird(rootPitch)), new MidiNote(perfectFifth(rootPitch)), new MidiNote(minorSeventh(rootPitch)));
    }

    // Minor Major Seventh: for example Cminmaj7 (root, minor 3rd, perfect 5th, major 7th)
    private static List<MidiNote> minorMajorSeventhChord(final int rootPitch) {
        return Arrays.asList(new MidiNote(rootPitch), new MidiNote(minorThird(rootPitch)), new MidiNote(perfectFifth(rootPitch)), new MidiNote(majorSeventh(rootPitch)));
    }

    // Dominant Seventh: for example Cdom7 (root, major 3rd, perfect 5th, minor 7th)
    private static List<MidiNote> dominantSeventhChord(final int rootPitch) {
        return Arrays.asList(new MidiNote(rootPitch), new MidiNote(majorThird(rootPitch)), new MidiNote(perfectFifth(rootPitch)), new MidiNote(minorSeventh(rootPitch)));
    }

    // Major Seventh: for example Cmaj7 (root, major 3rd, perfect 5th, major 7th)
    private static List<MidiNote> majorSeventhChord(final int rootPitch) {
        return Arrays.asList(new MidiNote(rootPitch), new MidiNote(majorThird(rootPitch)), new MidiNote(perfectFifth(rootPitch)), new MidiNote(majorSeventh(rootPitch)));
    }

    // Augmented Seventh: for example Caug7 (root, major 3rd, augmented 5th, minor 7th)
    private static List<MidiNote> augmentedSeventhChord(final int rootPitch) {
        return Arrays.asList(new MidiNote(rootPitch), new MidiNote(majorThird(rootPitch)), new MidiNote(augmentedFifth(rootPitch)), new MidiNote(minorSeventh(rootPitch)));
    }

    // Augmented Major Seventh: for example Cmaj7s5 (root, major 3rd, augmented 5th, major 7th)
    private static List<MidiNote> majorSeventhSharpFiveChord(final int rootPitch) {
        return Arrays.asList(new MidiNote(rootPitch), new MidiNote(majorThird(rootPitch)), new MidiNote(augmentedFifth(rootPitch)), new MidiNote(majorSeventh(rootPitch)));
    }

    // Intervals
    private static int majorSecond(final int pitch) {
        return pitch + 2;
    }

    private static int majorThird(final int pitch) {
        return pitch + 4;
    }

    private static int minorThird(final int pitch) {
        return pitch + 3;
    }

    private static int perfectFourth(final int pitch) {
        return pitch + 5;
    }

    private static int perfectFifth(final int pitch) {
        return pitch + 7;
    }

    private static int augmentedFifth(final int pitch) {
        return pitch + 8;
    }

    private static int diminishedFifth(final int pitch) {
        return pitch + 6;
    }

    private static int diminishedSeventh(final int pitch) {
        return pitch + 9;
    }

    private static int majorSixth(final int pitch) {
        return pitch + 9;
    }

    private static int minorSeventh(final int pitch) {
        return pitch + 10;
    }

    private static int majorSeventh(final int pitch) {
        return pitch + 11;
    }
}
