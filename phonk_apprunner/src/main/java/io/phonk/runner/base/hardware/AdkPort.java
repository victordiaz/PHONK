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

/*
 * AdkPort library
 * 
 * Written by Giles Barton-Owen
 * 
 * This library helps abstract the ADK interface into a simple sudo-serial port, the libary makes it simpler to use the ADK.
 * It is tested with mbed, not any other system.
 * To use the library one must import it into your Activity/class, make an instance of it, initialise it passing the context of the activity to it
 * If one wants a function called on a message received use a MessageNotifier listener implementing the onNew function.
 * Reading is done with read() read(byte[] toRead) or bytes[]<-readB()
 * Writing is done with sendString(String string) or sendBytes(byte[] bytes)
 * 
 * 
 */

package io.phonk.runner.base.hardware;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class AdkPort implements Runnable {

    // The permission action
    private static final String ACTION_USB_PERMISSION = "mbed.mbedwrapper.action.USB_PERMISSION";

    // mHandler what values
    private static final int IOERROR = -1;
    private static final int BYTES = 1;

    // Buffer length, USB standard
    private static final int Buflen = 16384;

    // An instance of accessory and manager
    private UsbAccessory mAccessory;
    private UsbManager mManager;

    // The file bits
    private ParcelFileDescriptor mFileDescriptor;
    private FileInputStream mInputStream;
    private FileOutputStream mOutputStream;

    // An instance of the ring buffer class
    private RingBuffer buffer = new RingBuffer(Buflen);


    // Where the notifier is put
    private MessageNotifier mMessage;

    // Has the accessory been opened? find out with this variable
    private boolean isOpen = false;

    // A receiver for events on the UsbManager, when permission is given or when the cable is pulled
    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        openAccessory(accessory);
                    } else {

                    }

                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (accessory != null && accessory.equals(mAccessory)) {
                    closeAccessory();
                }
            }

        }
    };

    //Initialiser
    public AdkPort(Context context) {
        setup(context);
    }


    // Sets up all the requests for permission and attaches the USB accessory if permission is already granted
    public void setup(Context context) {

        mManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        UsbAccessory[] accessoryList = mManager.getAccessoryList();
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0,
                new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(mUsbReceiver, filter);
        mManager.requestPermission(accessoryList[0], mPermissionIntent);


        if (accessoryList[0] != null) {

            mAccessory = accessoryList[0];
            if (mManager.hasPermission(mAccessory)) {
                openAccessory(mAccessory);
            }
        }

    }

    // Gets a list of the accessories attached, at this point it only supports one
    public String[] getList(Context context) {

        mManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        UsbAccessory[] accessoryList = mManager.getAccessoryList();
        String[] accessoryLists = new String[1];
        for (int i = 0; i < accessoryLists.length; i++) {
            accessoryLists[i] = accessoryList[i].getModel()
                    + " v" + accessoryList[i].getVersion()
                    + " by " + accessoryList[i].getManufacturer();


        }
        return accessoryLists;
    }


    // This loop runs all the time, looking for new data in from the Accessory
    @Override
    public void run() {
        byte[] lbuffer = new byte[16384];
        while (true) {
            try {
                int ret = mInputStream.read(lbuffer);
                buffer.add(lbuffer, 0, ret);
                Message m = Message.obtain(mHandler, BYTES);
                mHandler.sendMessage(m);

            } catch (IOException e) {
                Message m = Message.obtain(mHandler, IOERROR);
                mHandler.sendMessage(m);
                try {
                    mInputStream.close();
                    openAccessory();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

        }
    }

    public void onDestroy(Context context) {
        context.unregisterReceiver(mUsbReceiver);
        closeAccessory();
    }

    public void openAccessory() {
        openAccessory(mAccessory);
    }

    // Attaches the file streams to their pointers
    private void openAccessory(UsbAccessory accessory) {
        mAccessory = accessory;
        mFileDescriptor = mManager.openAccessory(accessory);
        if (mFileDescriptor != null) {
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            mInputStream = new FileInputStream(fd);
            mOutputStream = new FileOutputStream(fd);
        }
        isOpen = true;
    }

    public void closeAccessory() {
        try {
            if (mFileDescriptor != null) {
                mFileDescriptor.close();
            }
        } catch (IOException e) {
        } finally {
            mFileDescriptor = null;
            mAccessory = null;
        }
        isOpen = false;

    }

    // Notifiers
    public interface MessageNotifier {
        void onNew();
    }

    // Attach a notifier
    public void attachOnNew(MessageNotifier in) {
        mMessage = in;

    }

    // Packetizes the string into 32 byte blocks, to keep the port from restricting flow, not sure why this happens
    public void sendString(String toSend) {
        int i = 0;

        while (i < toSend.length()) {

            byte[] buffer = new byte[32];
            String temp = toSend.substring(i,
                    constrain(i + 32, toSend.length(), 0));
            buffer = temp.getBytes();
            byte[] newbuf = new byte[32];
            for (int m = 0; m < 32; m++) {
                if (m < toSend.length() - i) {
                    newbuf[m] = buffer[m];
                } else {
                    newbuf[m] = 0;
                }
            }
            if (mOutputStream != null) {
                try {
                    if (isOpen) {
                        mOutputStream.write(newbuf);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    //writeToConsole("Failed to send\n\r");
                }
            }
            i = i + 32;
        }
    }

    // Packetizes the byte array into 32 byte blocks, to keep the port from restricting flow, not sure why this happens
    public void sendBytes(byte[] toSend) {
        int i = 0;
        ByteBuffer temp = ByteBuffer.wrap(toSend);
        while (i < toSend.length) {

            byte[] buffer = new byte[32];
            temp.get(buffer, i, constrain(32, toSend.length - i, 0));

            byte[] newbuf = new byte[32];
            for (int m = 0; m < 32; m++) {
                if (m < toSend.length - i) {
                    newbuf[m] = buffer[m];
                } else {
                    newbuf[m] = 0;
                }
            }
            if (mOutputStream != null) {
                try {
                    if (isOpen) {
                        mOutputStream.write(newbuf);
                        mOutputStream.flush();
                    }
                } catch (IOException e) {
                    //writeToConsole("Failed to send\n\r");
                }
            }
            i = i + 32;
        }
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == BYTES) {
                mMessage.onNew();

            } else {

            }
        }
    };

    private int constrain(int in, int hi, int low) {
        if (hi < low) {
            int temp = hi;

            hi = low;
            low = temp;
        }
        if (in > hi)
            in = hi;
        if (in < low)
            in = low;
        return in;
    }

    public int available() {
        return buffer.available();

    }

    public byte[] getAll() {

        return buffer.getAll();
    }


    public int read(byte[] b) {
        b = buffer.get(b.length);
        return buffer.available();
    }

    public byte[] readB() {
        return buffer.getAll();
    }

    public int read() {

        return buffer.getC();
    }

}
