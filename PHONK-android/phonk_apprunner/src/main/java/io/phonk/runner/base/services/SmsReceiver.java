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

package io.phonk.runner.base.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/*
 * http://shreymalhotra.me/blog/tutorial/receive-sms-using-android-broadcastreceiver-inside-an-activity/
 *
 */

public class SmsReceiver extends BroadcastReceiver {

    public static final String ACTION = "SmsMessage.intent.MAIN";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;

        Object[] pdus = (Object[]) extras.get("pdus");
        for (Object o : pdus) {
            SmsMessage SMessage = SmsMessage.createFromPdu((byte[]) o);
            String sender = SMessage.getOriginatingAddress();
            String body = SMessage.getMessageBody();

            Intent in = new Intent(ACTION).putExtra("get_msg", sender + ":" + body);
            context.sendBroadcast(in);
        }
    }
}
