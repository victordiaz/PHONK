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

package io.phonk.runner.apprunner.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.ArrayList;
import java.util.Hashtable;

import io.phonk.runner.AppRunnerActivity;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apidoc.annotation.PhonkObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.api.media.PAudioPlayer;
import io.phonk.runner.apprunner.api.media.PAudioRecorder;
import io.phonk.runner.apprunner.api.media.PMidi;
import io.phonk.runner.apprunner.api.media.PPureData;
import io.phonk.runner.apprunner.api.media.PTextToSpeech;
import io.phonk.runner.apprunner.api.media.PVideo;
import io.phonk.runner.apprunner.api.media.PWave;
import io.phonk.runner.apprunner.interpreter.PhonkNativeArray;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;

@PhonkObject
public class PMedia extends ProtoBase {

    String TAG = PMedia.class.getSimpleName();

    private HeadSetReceiver headsetPluggedReceiver;
    private ReturnInterface headsetCallbackfn;

    boolean recording = false;

    public PMedia(AppRunner appRunner) {
        super(appRunner);
    }

    @PhonkMethod(description = "Set the main volume", example = "")
    @PhonkMethodParam(params = {"volume"})
    public void volume(int volume) {
        AndroidUtils.setVolume(getContext(), volume);
    }

    @PhonkMethod(description = "Routes the audio through the speakers", example = "media.playSound(fileName);")
    @PhonkMethodParam(params = {""})
    public void audioOnSpeakers(boolean b) {
        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(!b);
    }

    @PhonkMethod(description = "Enable sounds effects (default false)", example = "")
    @PhonkMethodParam(params = {"boolean"})
    public void enableSoundEffects(boolean b) {
        AndroidUtils.setEnableSoundEffects(getContext(), b);
    }

    @PhonkMethod(description = "Play a sound file giving its filename", example = "media.playSound(fileName);")
    @PhonkMethodParam(params = {"fileName"})
    public PAudioPlayer createSoundPlayer() {
        PAudioPlayer pAudioPlayer = new PAudioPlayer(getAppRunner());

        return pAudioPlayer;
    }

    @PhonkMethod(description = "Adds a video view and starts playing the fileName", example = "")
    @PhonkMethodParam(params = {"fileName"})
    public PVideo createVideoPlayer() {
        final PVideo video = new PVideo(getAppRunner());
        return video;
    }

    @PhonkMethod(description = "Loads and initializes a PureData patch http://www.puredata.info using libpd", example = "")
    @PhonkMethodParam(params = {"fileName"})
    public PPureData initLibPd() {
        PPureData pPureData = new PPureData(getAppRunner());

        return pPureData;
    }

    @PhonkMethod(description = "Record a sound with the microphone", example = "")
    @PhonkMethodParam(params = {"fileName", "showProgressBoolean"})
    public PAudioRecorder createRecorder() {
        return new PAudioRecorder(getAppRunner());
    }

    /*
    @ProtoMethod(description = "Says a text with voice", example = "media.textToSpeech('hello world');")
    @ProtoMethodParam(params = {"text"})
    public PTextToSpeech textToSpeech(String text) {
        return createTextToSpeech().speak(text);
    }
    */

    @PhonkMethod(description = "Says a text with voice using a defined locale", example = "media.textToSpeech('hello world');")
    @PhonkMethodParam(params = {"text", "Locale"})
    public PTextToSpeech createTextToSpeech() {
        PTextToSpeech tts = null;
        try {
            tts = new PTextToSpeech(getAppRunner());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return tts;
    }


    @PhonkMethod(description = "Fires the voice recognition and returns the best match", example = "media.startVoiceRecognition(function(text) { console.log(text) } );")
    @PhonkMethodParam(params = {"function(recognizedText)"})
    public void startVoiceRecognition(ReturnInterface callbackfn) {
        startVoiceRecognition(callbackfn, true);

    }

    public void startVoiceRecognition(final ReturnInterface callbackfn, boolean withUI) {
        final ReturnObject o = new ReturnObject();

        if (withUI) {
            if (getActivity() == null) return;

            (getActivity()).addVoiceRecognitionListener(matches -> {
                PhonkNativeArray retMatches = new PhonkNativeArray(matches.size());

                for (int i = 0; i < matches.size(); i++) {
                    retMatches.addPE(i, matches.get(i));
                }

                o.put("action", "recognized");
                o.put("results", retMatches);
                callbackfn.event(o);
            });

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell me something!");
            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, "Tell me something!");
            getActivity().startActivityForResult(intent, AppRunnerActivity.VOICE_RECOGNITION_REQUEST_CODE);
        } else {
            final SpeechRecognizer sr = SpeechRecognizer.createSpeechRecognizer(getContext());

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            // intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
            // intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, 10);
            // intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getClass().getPackage().getName());
            sr.startListening(intent);


            sr.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {
                    // o.put("action", "ended");
                    // callbackfn.event(o);
                }

                @Override
                public void onError(int error) {
                    o.put("action", "error");
                    callbackfn.event(o);
                }

                @Override
                public void onResults(Bundle results) {

                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                    PhonkNativeArray retMatches = new PhonkNativeArray(matches.size());

                    for (int i = 0; i < matches.size(); i++) {
                        retMatches.addPE(i, matches.get(i));

                        MLog.d(TAG, "results " + i + " " + matches.get(i));
                    }
                    o.put("action", "recognized");
                    o.put("result", retMatches);
                    callbackfn.event(o);
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                    ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                    for (int i = 0; i < matches.size(); i++) {
                        MLog.d(TAG, "partialResult " + i + " " + matches.get(i));
                    }
                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });

        }
        // sr.stopListening();
    }

    public interface onVoiceRecognitionListener {
        void onNewResult(ArrayList<String> text);
    }

    @PhonkMethod(description = "Start a connected midi device", example = "media.startVoiceRecognition(function(text) { console.log(text) } );")
    @PhonkMethodParam(params = {"function(recognizedText)"})
    public PMidi startMidi() {
        PMidi pMidi = new PMidi(getAppRunner());

        return pMidi;
    }

    public boolean isHeadsetPlugged() {
        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        return audioManager.isWiredHeadsetOn();
    }

    public void onHeadsetConnection(ReturnInterface callbackfn) {
        headsetCallbackfn = callbackfn;

        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        headsetPluggedReceiver = new HeadSetReceiver();
        getContext().registerReceiver(headsetPluggedReceiver, filter);
    }

    public PWave createWave() {
        PWave pWave = new PWave(getAppRunner());
        return pWave;
    }

    private class HeadSetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);

                ReturnObject ret = new ReturnObject();
                switch (state) {
                    case 0:
                        Log.d(TAG, "Headset unplugged");
                        ret.put("plugged", false);
                        headsetCallbackfn.event(ret);
                        break;
                    case 1:
                        Log.d(TAG, "Headset plugged");
                        ret.put("plugged", false);
                        headsetCallbackfn.event(ret);
                        break;
                }
            }
        }
    }


    public Bitmap generateQRCode(String text) {
        Bitmap bmp = null;

        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // H = 30% damage

        int size = 256;

        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hintMap);

            int width = bitMatrix.getWidth();
            bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < width; y++) {
                    bmp.setPixel(y, x, bitMatrix.get(x, y) == true ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bmp;
    }

    public void scanQRcode(byte[] data, Camera camera) {
        Camera.Size size = camera.getParameters().getPreviewSize();

        // Create BinaryBitmap
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, size.width, size.height, 0, 0, size.width, size.height, false);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        // Read QR Code
        Reader reader = new MultiFormatReader();
        Result result = null;
        try {
            result = reader.decode(bitmap);
            String text = result.getText();

            MLog.d(TAG, "result: " + text);
        } catch (NotFoundException e) {
        } catch (ChecksumException e) {
        } catch (FormatException e) {
        }
    }

    @Override
    public void __stop() {
        getContext().unregisterReceiver(headsetPluggedReceiver);
    }
}

