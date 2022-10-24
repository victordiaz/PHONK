package io.phonk.runner.apprunner.api.media.midi;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps note names to pitches and pitches to note names
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class NoteNamePitchMapper {

    private final static Map<String, Integer> NOTE_NAME_MIDI_PITCH_MAP;

    // Build all possible note name to midi pitch combinations
    static {
        NOTE_NAME_MIDI_PITCH_MAP = new HashMap<>();
        // all existing notes
        final String[][] noteNames = {
                {"C", "B#"},  // notes with different names but same pitches are in an array
                {"Db", "C#"},
                {"D"},
                {"Eb", "D#"},
                {"E", "Fb"},
                {"F", "E#"},
                {"Gb", "F#"},
                {"G"},
                {"Ab", "G#"},
                {"A"},
                {"Bb", "A#"},
                {"B", "Cb"}
        };
        for (int pitch = 0; pitch <= PMidiController.MAX_PITCH_VALUE; pitch++) {
            final int octave = pitch / noteNames.length - 1;  // first octave is "-1"
            final int noteCursor = pitch % noteNames.length;
            for (final String noteName : noteNames[noteCursor]) {
                NOTE_NAME_MIDI_PITCH_MAP.put(noteName + octave, pitch);
            }
        }
    }

    /**
     * Returns the midi pitch of a given note name
     * @param noteName from "C-1" to "G9"
     * @return the midi pitch (between 0-127)
     * @throws RuntimeException if the note is not valid
     */
    public static int getPitch(final String noteName) {
        final Integer pitch = NOTE_NAME_MIDI_PITCH_MAP.get(noteName);
        if (pitch == null) {
            throw new RuntimeException(noteName + " is not a valid note");
        }
        return pitch;
    }

    /**
     * Returns the note name for a given pitch
     * @param pitch from 0 to 127
     * @return the note name from "C-1" to "G9"
     * @throws RuntimeException if the pitch is not valid
     */
    @NonNull
    public static String getNoteName(final int pitch) {
        for (final Map.Entry<String, Integer> namePitch : NOTE_NAME_MIDI_PITCH_MAP.entrySet()) {
            if (pitch == namePitch.getValue()) {
                return namePitch.getKey();
            }
        }
        throw new RuntimeException(pitch + " is not a valid pitch");
    }
}
