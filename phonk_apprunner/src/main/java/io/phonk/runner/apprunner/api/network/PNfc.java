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

package io.phonk.runner.apprunner.api.network;

import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.MLog;

@PhonkClass
public class PNfc extends ProtoBase {
    public static String nfcMsg = null;
    private NdefMessage messageToWrite;

    public PNfc(AppRunner appRunner) {
        super(appRunner);
    }

    /**
     * Starts NFC
     */
    @PhonkMethod
    public PNfc start() {
        getActivity().initializeNFC();
        return this;
    }

    @PhonkMethod(description = "Gives back data when mContext NFC tag is approached", example = "")
    @PhonkMethodParam(params = {"function(id, data)"})
    public void onNewData(final ReturnInterface callback) {
        getActivity().addNFCReadListener(o -> {
            callback.event(o);
        });

    }

    public interface onNFCWrittenListener {
        void onDataWritten(ReturnObject o);
    }

    public interface onNFCListener {
        void onNewTag(ReturnObject o);
    }

    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {"function(msg)"})
    public void onDataWritten(ReturnInterface callback) {
        getActivity().addNFCWrittenListener(new onNFCWrittenListener() {
            @Override
            public void onDataWritten(ReturnObject o) {
                MLog.d(TAG, "data written");
                callback.event(o);
            }
        });
    }

    /**
     * Write text to a tag
     *
     * @param textToWrite the text to write
     */
    public PNfc write(String textToWrite) {
        nfcMsg = textToWrite;
        return this;
    }

    public static boolean writeTag(Context context, Tag tag, String data) {
        // Record to launch Play Store if app is not installed
        NdefRecord appRecord = NdefRecord.createApplicationRecord(context.getPackageName());

        // Record with actual data we care about
        NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, ("application/"
                + context.getPackageName()).getBytes(StandardCharsets.US_ASCII), null, data.getBytes());

        // Complete NDEF message with both records
        NdefMessage message = new NdefMessage(new NdefRecord[]{relayRecord, appRecord});

        try {
            // If the tag is already formatted, just write the message to it
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                // Make sure the tag is writable
                if (!ndef.isWritable()) {
                    return false;
                }

                // Check if there's enough space on the tag for the message
                int size = message.toByteArray().length;
                if (ndef.getMaxSize() < size) {
                    return false;
                }

                try {
                    // Write the data to the tag
                    ndef.writeNdefMessage(message);
                    return true;
                } catch (TagLostException tle) {
                    return false;
                } catch (IOException ioe) {
                    return false;
                } catch (FormatException fe) {
                    return false;
                }
                // If the tag is not formatted, format it with the message
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        return true;
                    } catch (TagLostException tle) {
                        return false;
                    } catch (IOException ioe) {
                        return false;
                    } catch (FormatException fe) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
        }

        return false;
    }

    @Override
    public void __stop() {

    }
}
