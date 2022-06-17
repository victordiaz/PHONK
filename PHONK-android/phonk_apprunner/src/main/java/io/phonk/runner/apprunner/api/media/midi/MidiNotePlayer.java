package io.phonk.runner.apprunner.api.media.midi;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manage the playing and stopping of midi notes in case of parallel triggers for the same note.
 * For example, if "C4" is played simultaneously using button A and button B, if you release button A
 * while maintaining button B triggered, "C4" will not stop using the MidiNotePlayer.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class MidiNotePlayer {

    final Map<MidiNote, Integer> playedNotesCount = new ConcurrentHashMap<>();
    private final PMidiController pMidiController;

    public MidiNotePlayer(final PMidiController pMidiController) {
        this.pMidiController = pMidiController;
    }

    /**
     * Plays a note. If the note is already played, it increment a counter to count the number of time
     * the note is played.
     * @param noteToPlay the note to play
     */
    public void playNote(final MidiNote noteToPlay) {
        Integer currentCount = playedNotesCount.get(noteToPlay);
        if (currentCount == null) {
            currentCount = 0;
        }
        currentCount++;
        playedNotesCount.put(noteToPlay, currentCount);
        // if not is already played, stop it before playing again to avoid some strange bugs in synths
        if (currentCount > 1) {
            pMidiController.noteOff(noteToPlay.getChannel(), noteToPlay.getPitch(), noteToPlay.getVelocity());
        }
        pMidiController.noteOn(noteToPlay.getChannel(), noteToPlay.getPitch(), noteToPlay.getVelocity());
    }

    /**
     * Stops a note if required. If the note is played more than once, it decrements a counter to
     * count the number of time the note is played and it doesn't stop it.
     * @param noteToStop the note to stop
     */
    public void stopPlayingNote(final MidiNote noteToStop) {
        Integer currentCount = playedNotesCount.get(noteToStop);
        if (currentCount != null && currentCount > 0) {
            currentCount--;
            playedNotesCount.put(noteToStop, currentCount);
            if (currentCount == 0) {
                pMidiController.noteOff(noteToStop.getChannel(), noteToStop.getPitch(), noteToStop.getVelocity());
            }
        }
    }
}
