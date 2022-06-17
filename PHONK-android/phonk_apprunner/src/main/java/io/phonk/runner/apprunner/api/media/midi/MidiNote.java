package io.phonk.runner.apprunner.api.media.midi;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Objects;

/**
 * Represents a note from the midi perspective
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class MidiNote {
    private final int channel;
    private final int pitch;
    private final int velocity;

    public MidiNote(final int channel, final int pitch, final int velocity) {
        this.channel = channel;
        this.pitch = pitch;
        this.velocity = velocity;
    }

    public MidiNote(final int channel, final String noteName, final int velocity) {
        this(channel, NoteNamePitchMapper.getPitch(noteName), velocity);
    }

    public MidiNote(final String noteName) {
        this(0, NoteNamePitchMapper.getPitch(noteName), PMidiController.MAX_VELOCITY_VALUE);
    }

    public MidiNote(final int pitch) {
        this(0, pitch, PMidiController.MAX_VELOCITY_VALUE);
    }

    public int getChannel() {
        return channel;
    }

    public int getPitch() {
        return pitch;
    }

    public int getVelocity() {
        return velocity;
    }

    @NonNull
    @Override
    public String toString() {
        return NoteNamePitchMapper.getNoteName(pitch);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MidiNote midiNote = (MidiNote) o;
        return channel == midiNote.channel && pitch == midiNote.pitch && velocity == midiNote.velocity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, pitch, velocity);
    }
}
