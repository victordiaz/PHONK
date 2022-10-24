package io.phonk.runner.apprunner.api.media.midi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class NoteNamePitchMapperTest {

    @Test
    public void getPitch() {
        assertEquals(0, NoteNamePitchMapper.getPitch("C-1"));
        assertEquals(0, NoteNamePitchMapper.getPitch("B#-1"));
        assertEquals(12, NoteNamePitchMapper.getPitch("C0"));
        assertEquals(59, NoteNamePitchMapper.getPitch("B3"));
        assertEquals(60, NoteNamePitchMapper.getPitch("C4"));
        assertEquals(61, NoteNamePitchMapper.getPitch("C#4"));
        assertEquals(127, NoteNamePitchMapper.getPitch("G9"));
        final RuntimeException exception = assertThrows(RuntimeException.class, () -> NoteNamePitchMapper.getPitch("G10"));
        assertEquals("G10 is not a valid note", exception.getMessage());
    }

    @Test
    public void getNoteName() {
        assertEquals("D-1", NoteNamePitchMapper.getNoteName(2));
        assertEquals("G9", NoteNamePitchMapper.getNoteName(127));
        final RuntimeException exception = assertThrows(RuntimeException.class, () -> NoteNamePitchMapper.getNoteName(128));
        assertEquals("128 is not a valid pitch", exception.getMessage());
    }
}