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

package io.phonk.runner.base.media;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Audio {

    public final static int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    public static void setSpeakerOn(boolean b) {

        Class audioSystemClass;
        try {
            audioSystemClass = Class.forName("android.media.AudioSystem");
            Method setForceUse = audioSystemClass.getMethod("setForceUse", int.class, int.class);
            // First 1 == FOR_MEDIA, second 1 == FORCE_SPEAKER. To go back to
            // the default
            // behavior, use FORCE_NONE (0).
            setForceUse.invoke(null, 1, 1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    /**
     * Fire an intent to start the speech recognition activity. onActivityResult
     * is handled in BaseActivity
     */
    private void startVoiceRecognitionActivity(Activity a) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell me something!");
        a.startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

}
