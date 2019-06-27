/*
 * Part of Phonk http://www.phonk.io
 * A prototyping platform for Android devices
 *
 * Copyright (C) 2013 - 2017 Victor Diaz Barrales @victordiaz (Protocoder)
 * Copyright (C) 2017 - Victor Diaz Barrales @victordiaz (Phonk)
 *
 * Phonk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Phonk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Phonk. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.phonk.runner.base.gui;

import android.graphics.SurfaceTexture;
import android.view.TextureView;

import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.LifecycleOwner;

import io.phonk.runner.apprunner.AppRunner;

public class Camera extends TextureView implements TextureView.SurfaceTextureListener {

    protected String TAG = CameraNew.class.getSimpleName();
    AppRunner mAppRunner;

    public Camera(AppRunner appRunner) {
        super(appRunner.getAppContext());
        this.setSurfaceTextureListener(this);
    }

    private void init() {
        PreviewConfig config = new PreviewConfig.Builder().build();
        Preview preview = new Preview(config);

        final TextureView textureView = this;

        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    @Override
                    public void onUpdated(Preview.PreviewOutput previewOutput) {
                        // The output data-handling is configured in a listener.
                        textureView.setSurfaceTexture(previewOutput.getSurfaceTexture());
                        // Your custom code here.
                    }
                });

        // The use case is bound to an Android Lifecycle with the following code.
        CameraX.bindToLifecycle((LifecycleOwner) this, preview);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        init();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
