package io.phonk.runner.apprunner.api.media.midi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.List;

public class ChordBuilderTest {

    @Test
    public void usingMode() {
        final List<MidiNote> d4Chord = ChordBuilder.usingMode("D4", "III", "ionian", 4);
        assertEquals(new MidiNote("F#4"), d4Chord.get(0));
        assertEquals(new MidiNote("A4"), d4Chord.get(1));
        assertEquals(new MidiNote("C#5"), d4Chord.get(2));
        assertEquals(new MidiNote("E4"), d4Chord.get(3));

        final List<MidiNote> bb2Chord = ChordBuilder.usingMode("Bb2", "I", "V", 3);
        assertEquals(new MidiNote("Bb2"), bb2Chord.get(0));
        assertEquals(new MidiNote("D3"), bb2Chord.get(1));
        assertEquals(new MidiNote("F3"), bb2Chord.get(2));

        final List<MidiNote> fSharp6Chord = ChordBuilder.usingMode("F#6", "VII", "minor", 3);
        assertEquals(new MidiNote("E7"), fSharp6Chord.get(0));
        assertEquals(new MidiNote("G#6"), fSharp6Chord.get(1));
        assertEquals(new MidiNote("B6"), fSharp6Chord.get(2));
    }

    @Test
    public void usingChordName() {
        final List<MidiNote> c4Chord = ChordBuilder.usingChordName("C", 4);
        assertEquals(new MidiNote("C4"), c4Chord.get(0));
        assertEquals(new MidiNote("E4"), c4Chord.get(1));
        assertEquals(new MidiNote("G4"), c4Chord.get(2));

        final List<MidiNote> g3Chord = ChordBuilder.usingChordName("Gminmaj7", 3);
        assertEquals(new MidiNote("G3"), g3Chord.get(0));
        assertEquals(new MidiNote("Bb3"), g3Chord.get(1));
        assertEquals(new MidiNote("D4"), g3Chord.get(2));
        assertEquals(new MidiNote("F#4"), g3Chord.get(3));
    }
}
