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

import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.api.widgets.PropertiesProxy;
import io.phonk.runner.apprunner.api.widgets.Styler;
import io.phonk.runner.base.gui.CustomVideoTextureView;
import io.phonk.runner.base.utils.MLog;

@PhonkClass(mergeFrom = "AudioPlayer")
public class PVideo extends PAudioPlayer {
    private static final java.lang.String TAG = PVideo.class.getSimpleName();
    private final CustomVideoTextureView mTextureView;

    public PVideo(AppRunner appRunner) {
        super(appRunner);

        MLog.d(TAG, "trying to put surface");

        mTextureView = new CustomVideoTextureView(appRunner.getAppContext());
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                MLog.d(TAG, "surface available");
                mMediaPlayer.setSurface(new Surface(surfaceTexture));
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });

    }

    public View getPreview() {
        return mTextureView;
    }

    @PhonkMethod(description = "Gets the video width and height", example = "")
    @PhonkMethodParam(params = {""})
    public ReturnObject getClipSize() {
        ReturnObject r = new ReturnObject();
        r.put("width", mMediaPlayer.getVideoWidth());
        r.put("height", mMediaPlayer.getVideoHeight());
        return r;
    }

    @PhonkMethod(description = "Gets the video aspect ratio", example = "")
    @PhonkMethodParam(params = {""})
    public float getClipAspectRatio() {
        return (float) mMediaPlayer.getVideoWidth() / (float) mMediaPlayer.getVideoHeight();
    }

}
