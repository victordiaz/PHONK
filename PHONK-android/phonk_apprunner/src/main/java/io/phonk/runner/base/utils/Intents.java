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

package io.phonk.runner.base.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Intents {

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     *
     * @param context The application's environment.
     * @param action  The Intent action to check for availability. such as
     *                "com.google.zxing.client.android.SCAN"
     * @return True if an Intent with the specified action can be sent and
     * responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    // URIs for Google Play intents
    private static final String market_root = "market://details?id=";
    // Twitter
    private static final String uri_market_twitter = market_root + "com.twitter.android";
    public static final Uri GOOGLE_PLAY_TWITTER_URI = Uri.parse(uri_market_twitter);

    // Facebook
    private static final String uri_market_facebook = market_root + "com.facebook.katana";
    public static final Uri GOOGLE_PLAY_FACEBOOK_URI = Uri.parse(uri_market_facebook);

    // com.google.android.googlequicksearchbox.MUSIC_SEARCH
    //

    public static void launchGooglePlaytIntent(final Context c, int string_title_res_id, final Uri google_play_uri) {
        AlertDialog.Builder adb = new AlertDialog.Builder(c);

        adb.setTitle(string_title_res_id);

        // Go to Market for Install or Update
        adb.setPositiveButton(android.R.string.yes, (dialog, which) -> {
            Intent market_intent = new Intent(Intent.ACTION_VIEW, google_play_uri);
            c.startActivity(market_intent);
        });

        // Nothing
        adb.setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel());

        AlertDialog ad = adb.create();

        ad.show();
    }

    static public void shareWithMail(Context context, String emailTo, String emailCC, String subject, String emailText,
                                     List<String> filePaths) {
        // need to "send multiple" to get more than one attachment
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{emailTo});
        emailIntent.putExtra(android.content.Intent.EXTRA_CC, new String[]{emailCC});
        // has to be an ArrayList
        ArrayList<Uri> uris = new ArrayList<>();
        // convert from paths to Android friendly Parcelable Uri's
        for (String file : filePaths) {
            File fileIn = new File(file);
            Uri u = Uri.fromFile(fileIn);
            uris.add(u);
        }
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    static public void openWeb(Context c, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(url));
        c.startActivity(intent);

    }

    public static void webSearch(Context c, String text) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.setData(Uri.parse(text));
        c.startActivity(intent);
    }

    static public void openMap(Context c, double longitude, double latitude) {

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:" + longitude + "," + latitude));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);

    }

    static public void openSMSThread(Context c, String url) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("address", "+34645865008");
        intent.setType("vnd.android-dir/mms-sms");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);

    }

    static public void sendEmail(Context c, String recipient, String subject, String msg) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, recipient);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
        c.startActivity(Intent.createChooser(intent, "Send mail..."));

    }

    static public void sendEmail(Context c, String[] recipient, String subject, String msg) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, recipient);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
        c.startActivity(Intent.createChooser(intent, "Send mail..."));

    }

    public static void openDial(Context c) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        c.startActivity(intent);
    }

    public static void call(Context c, String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        c.startActivity(intent);
    }

    public static void recordAudio(Context c, int requestCode) {

        Intent recordIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        // c.startActivityForResult(recordIntent, requestCode);

    }

}
