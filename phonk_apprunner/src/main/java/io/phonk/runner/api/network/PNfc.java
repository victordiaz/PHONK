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

package io.phonk.runner.api.network;

import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import io.phonk.runner.api.ProtoBase;
import io.phonk.runner.api.common.ReturnInterface;
import io.phonk.runner.api.common.ReturnObject;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

public class PNfc extends ProtoBase {

    public static String nfcMsg = null;
    private NdefMessage messageToWrite;

    public PNfc(AppRunner appRunner) {
        super(appRunner);
    }


    @ProtoMethod(description = "Gives back data when mContext NFC tag is approached", example = "")
    @ProtoMethodParam(params = {"function(id, data)"})
    public void onNewData(final ReturnInterface callback) {

        getActivity().addNFCReadListener(new onNFCListener() {
            @Override
            public void onNewTag(String id, String data) {
                ReturnObject o = new ReturnObject();
                o.put("id", id);
                o.put("data", data);
                callback.event(o);
            }
        });

        getActivity().initializeNFC();
    }

    // --------- nfc ---------//
    interface writeNFCCB {
        void event(boolean b);
    }

    @ProtoMethod(description = "Write text into a NFC tag", example = "")
    @ProtoMethodParam(params = {"function()"})
    public void write(String data, final writeNFCCB fn) {
        PNfc.nfcMsg = data;
        getActivity().initializeNFC();
        
        getActivity().addNFCWrittenListener(new onNFCWrittenListener() {
            @Override
            public void onNewTag() {
                fn.event(true);
            }
        });

        // Construct the data to write to the tag
        // Should be of the form [relay/group]-[rid/gid]-[cmd]
        // String nfcMessage = data;

        // When an NFC tag comes into range, call the main activity which
        // handles writing the data to the tag
        // NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(mContext);

        // Intent nfcIntent = new Intent(mContext,
        // AppRunnerActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // nfcIntent.putExtra("nfcMessage", nfcMessage);
        // PendingIntent pi = PendingIntent.getActivity(mContext, 0, nfcIntent,
        // PendingIntent.FLAG_UPDATE_CURRENT);
        // IntentFilter tagDetected = new
        // IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        // nfcAdapter.enableForegroundDispatch((Activity) mContext, pi, new
        // IntentFilter[] {tagDetected}, null);
    }

    public interface onNFCWrittenListener {
        public void onNewTag();
    }

    public interface onNFCListener {
        public void onNewTag(String id, String nfcMessage);
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"function(msg)"})
    public void onDataWritten(ReturnInterface callback) {
        getActivity().initializeNFC();

        callback.event(null);
    }


    public static boolean writeTag(Context context, Tag tag, String data) {
        // Record to launch Play Store if app is not installed
        NdefRecord appRecord = NdefRecord.createApplicationRecord(context.getPackageName());

        // Record with actual data we care about
        NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, new String("application/"
                + context.getPackageName()).getBytes(Charset.forName("US-ASCII")), null, data.getBytes());

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

    /**
     * Write text to a tag
     *
     * @param textToWrite the text to write
     */
    public void write(String textToWrite) {

        Locale locale = Locale.US;
        final byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("UTF-8"));
        final byte[] textBytes = textToWrite.getBytes(Charset.forName("UTF-8"));

        final int utfBit = 0;
        final char status = (char) (utfBit + langBytes.length);
        final byte[] data = new byte[1 + langBytes.length + textBytes.length];

        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
        NdefRecord[] records = {record};
        messageToWrite = new NdefMessage(records);
    }

    @Override
    public void __stop() {

    }
}
