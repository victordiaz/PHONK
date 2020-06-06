/*
 * Part of Phonk http://www.phonk.io
 * A prototyping platform for Android devices
 *
 * Copyright (C) 2013 - 2017 Victor Diaz Barrales @victordiaz (Protocoder)
 * Copyright (C) 2017 - Victor Diaz Barrales @victordiaz (Phonk)
 *
 * Phonk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Phonk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Phonk. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.phonk.runner.apprunner.api.media;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;

import java.io.IOException;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.MLog;

@PhonkClass
public class PAudioPlayer extends ProtoBase {
    private final String TAG = PAudioPlayer.class.getSimpleName();

    protected MediaPlayer mMediaPlayer;
    private ReturnInterface callbackfn;

    public interface OnFinishCB {
        void event();
    }

    public PAudioPlayer(AppRunner appRunner) {
        super(appRunner);
        getAppRunner().whatIsRunning.add(this);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setLooping(false);
        mMediaPlayer.setOnPreparedListener(mp -> {
            MLog.d(TAG, "prepared");

            if (callbackfn != null) {
                ReturnObject r = new ReturnObject();
                callbackfn.event(r);
            }
            //mMediaPlayer.start();

        });

        mMediaPlayer.setOnBufferingUpdateListener((mp, percent) -> MLog.d(TAG, "buffering: " + percent));

        mMediaPlayer.setOnCompletionListener(mp -> MLog.d(TAG, "completed"));

    }

    public PAudioPlayer load(String uri) {

        try {
            if (uri.startsWith("http://")) {
                mMediaPlayer.setDataSource(uri);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.prepareAsync();
            } else {
                uri = getAppRunner().getProject().getFullPathForFile(uri);
                mMediaPlayer.setDataSource(uri);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.prepare();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public PAudioPlayer onLoaded(ReturnInterface callbackfn) {
        this.callbackfn = callbackfn;
        return this;
    }

    public PAudioPlayer play() {
        mMediaPlayer.start();

        return this;
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public PAudioPlayer loop(boolean b) {
        mMediaPlayer.setLooping(b);

        return this;
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public PAudioPlayer pause() {
        mMediaPlayer.pause();

        return this;
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public PAudioPlayer seekTo(int ms) {
        mMediaPlayer.seekTo(ms);

        return this;
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public boolean isLooping() {
        return mMediaPlayer.isLooping();
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public int position() {
        return mMediaPlayer.getCurrentPosition();
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public int duration() {
        return mMediaPlayer.getDuration();
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public PAudioPlayer volume(float vol) {
        mMediaPlayer.setVolume(vol, vol);

        return this;
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public PAudioPlayer volume(float volLeft, float volRight) {
        mMediaPlayer.setVolume(volLeft, volRight);

        return this;
    }

    public MediaPlayer.TrackInfo[] getInfo() {
        return mMediaPlayer.getTrackInfo();
    }

    public PAudioPlayer stop() {
        mMediaPlayer.stop();

        // mMediaPlayer.release();

        return this;
    }


    public PAudioPlayer speed(float speed) {
        PlaybackParams params = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            params = new PlaybackParams();
            params.setSpeed(speed);
            mMediaPlayer.setPlaybackParams(params);
        }

        return this;
    }

    public PAudioPlayer pitch(float pitch) {
        PlaybackParams params = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            params = new PlaybackParams();
            params.setPitch(pitch);
            mMediaPlayer.setPlaybackParams(params);
        }

        return this;
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public PAudioPlayer finish() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;

        return this;
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {""})
    public PAudioPlayer onFinish(PAudioPlayer.OnFinishCB callbackfn) {

        return this;
    }

    @Override
    public void __stop() {
        finish();
    }
}
