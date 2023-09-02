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

package io.phonk.runner.apprunner.api.network;

import static android.bluetooth.BluetoothGatt.CONNECTION_PRIORITY_HIGH;
import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
import static com.welie.blessed.BluetoothPeripheral.GATT_SUCCESS;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;

import com.welie.blessed.BluetoothCentral;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

import java.util.UUID;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.api.other.WhatIsRunningInterface;
import io.phonk.runner.base.utils.MLog;

@SuppressLint("NewApi")
@PhonkClass
public class PBluetoothLEClient extends ProtoBase implements WhatIsRunningInterface {
    private final static String DISCONNECTED = "disconnected";
    private final static String CONNECTING = "connecting";
    private final static String CONNECTED = "connected";
    private final static String DISCONNECTING = "disconnecting";
    private final Context mContext;
    private final AppRunner mAppRunner;
    private final BluetoothAdapter mBleAdapter;
    private final String mConnectionStatus;
    private final BluetoothCentral central;
    private final Handler handler = new Handler();
    // private Boolean connected = false;
    public boolean autoConnect = false;
    private BluetoothGatt mGatt;
    private ReturnInterface mCallbackDevice;
    private ReturnInterface mCallbackCharacteristic;
    private int mtuSize = 500;
    private ReturnInterface mCallbackMtu;
    // Callback for peripherals
    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered(BluetoothPeripheral peripheral) {
            MLog.i(TAG, "discovered service " + peripheral.getServices());

        }

        @Override
        public void onNotificationStateUpdate(
                BluetoothPeripheral peripheral,
                BluetoothGattCharacteristic characteristic,
                int status
        ) {
            if (status == GATT_SUCCESS) {
                if (peripheral.isNotifying(characteristic)) {
                    MLog.d(TAG, "SUCCESS: Notify set to 'on' for %s " + characteristic.getUuid());
                } else {
                    MLog.d(TAG, "SUCCESS: Notify set to 'off' for %s " + characteristic.getUuid());
                }
            } else {
                MLog.d(TAG, "ERROR: Changing notification state failed for %s" + characteristic.getUuid());
            }
        }

        @Override
        public void onCharacteristicUpdate(
                BluetoothPeripheral peripheral,
                byte[] value,
                BluetoothGattCharacteristic characteristic,
                int status
        ) {
            // MLog.d(TAG, "onCharacteristicUpdate");

            if (status != GATT_SUCCESS) return;
            ReturnObject ret = new ReturnObject();
            ret.put("deviceMac", peripheral.getAddress());
            ret.put("deviceName", peripheral.getName());
            ret.put("serviceUUID", characteristic.getService().getUuid());
            ret.put("characteristicUUID", characteristic.getUuid());
            ret.put("value", value);

            mHandler.post(() -> {
                if (mCallbackCharacteristic != null) mCallbackCharacteristic.event(ret);
            });

            /*
            if (characteristicUUID.equals(BLOOD_PRESSURE_MEASUREMENT_CHARACTERISTIC_UUID)) {
                BloodPressureMeasurement measurement = new BloodPressureMeasurement(value);
                Intent intent = new Intent("BluetoothMeasurement");
                intent.putExtra("BloodPressure", measurement);
                context.sendBroadcast(intent);
                Timber.d("%s", measurement);
            }
            else if(characteristicUUID.equals(TEMPERATURE_MEASUREMENT_CHARACTERISTIC_UUID)) {
                TemperatureMeasurement measurement = new TemperatureMeasurement(value);
                Intent intent = new Intent("TemperatureMeasurement");
                intent.putExtra("Temperature", measurement);
                context.sendBroadcast(intent);
                Timber.d("%s", measurement);
            }
            else if(characteristicUUID.equals(HEARTRATE_MEASUREMENT_CHARACTERISTIC_UUID)) {
                HeartRateMeasurement measurement = new HeartRateMeasurement(value);
                Intent intent = new Intent("HeartRateMeasurement");
                intent.putExtra("HeartRate", measurement);
                context.sendBroadcast(intent);
                Timber.d("%s", measurement);
            }
            else if(characteristicUUID.equals(CURRENT_TIME_CHARACTERISTIC_UUID)) {
                Date currentTime = parser.getDateTime();
                MLog.d(TAG, "Received device time: %s", currentTime);

                // Deal with Omron devices where we can only write currentTime under specific conditions
                if(peripheral.getName().contains("BLEsmart_")) {
                    boolean isNotifying = peripheral.isNotifying(peripheral.getCharacteristic(BLP_SERVICE_UUID,
                    BLOOD_PRESSURE_MEASUREMENT_CHARACTERISTIC_UUID));
                    if(isNotifying) currentTimeCounter++;

                    // We can set device time for Omron devices only if it is the first notification and currentTime
                    is more than 10 min from now
                    long interval = abs(Calendar.getInstance().getTimeInMillis() - currentTime.getTime());
                    if (currentTimeCounter == 1 && interval > 10*60*1000) {
                        parser.setCurrentTime(Calendar.getInstance());6E400001-B5A3-F393-E0A9-E50E24DCCA9E
                        peripheral.writeCharacteristic(characteristic, parser.getValue(), WRITE_TYPE_DEFAULT);
                    }
                }
            }
            else if(characteristicUUID.equals(BATTERY_LEVEL_CHARACTERISTIC_UUID)) {
                int batteryLevel = parser.getIntValue(FORMAT_UINT8);
                MLog.d(TAG, "Received battery level %d%%", batteryLevel);
            }
            else if(characteristicUUID.equals(MANUFACTURER_NAME_CHARACTERISTIC_UUID)) {
                String manufacturer = parser.getStringValue(0);
                MLog.d(TAG, "Received manufacturer: %s", manufacturer);
            }
            else if(characteristicUUID.equals(MODEL_NUMBER_CHARACTERISTIC_UUID)) {
                String modelNumber = parser.getStringValue(0);
                MLog.d(TAG, "Received modelnumber: %s", modelNumber);
            }
             */
        }

        @Override
        public void onCharacteristicWrite(
                BluetoothPeripheral peripheral,
                byte[] value,
                BluetoothGattCharacteristic characteristic,
                int status
        ) {
            MLog.d(TAG, "onCharWrite");
        }

        @Override
        public void onMtuChanged(BluetoothPeripheral peripheral, int mtu, int status) {
            ReturnObject ret = new ReturnObject();
            ret.put("deviceMac", peripheral.getAddress());
            ret.put("deviceName", peripheral.getName());
            ret.put("mtu", mtu);
            ret.put("success", status == GATT_SUCCESS);

            mHandler.post(() -> {
                if (mCallbackMtu != null) mCallbackMtu.event(ret);
            });
        }
    };
    // Callback for central
    private final BluetoothCentralCallback bluetoothCentralCallback = new BluetoothCentralCallback() {

        @Override
        public void onConnectedPeripheral(BluetoothPeripheral peripheral) {
            peripheral.requestMtu(mtuSize);

            MLog.d(TAG, "connected to '%s' " + peripheral.getName());
            ReturnObject ret = new ReturnObject();
            ret.put("deviceMac", peripheral.getAddress());
            ret.put("deviceName", peripheral.getAddress());
            ret.put("status", "connected");
            mHandler.post(() -> {
                if (mCallbackDevice != null) mCallbackDevice.event(ret);
            });
        }

        @Override
        public void onConnectionFailed(BluetoothPeripheral peripheral, final int status) {
            MLog.e(TAG, "connection '%s' failed with status %d " + peripheral.getName() + " " + status);
        }

        @Override
        public void onDisconnectedPeripheral(final BluetoothPeripheral peripheral, final int status) {
            MLog.d(TAG, "disconnected '%s' with status %d" + " " + peripheral.getName() + " " + status);
            ReturnObject ret = new ReturnObject();
            ret.put("deviceMac", peripheral.getAddress());
            ret.put("deviceName", peripheral.getAddress());
            ret.put("status", "disconnected");
            mHandler.post(() -> {
                if (mCallbackDevice != null) mCallbackDevice.event(ret);
            });

            // Reconnect to this device when it becomes available again if autoConnect is true
            if (!autoConnect) return;

            handler.postDelayed(() -> central.autoConnectPeripheral(peripheral, peripheralCallback), 5000);
        }

        @Override
        public void onDiscoveredPeripheral(BluetoothPeripheral peripheral, ScanResult scanResult) {
            MLog.d(TAG, "Found peripheral " + peripheral.getName() + " " + peripheral.getAddress());

        }

        @Override
        public void onBluetoothAdapterStateChanged(int state) {
            MLog.d(TAG, "bluetooth adapter changed state to %d " + state);
            if (state == BluetoothAdapter.STATE_ON) {
                // Bluetooth is on now, start scanning again
                // Scan for peripherals with a certain service UUIDs
                central.startPairingPopupHack();
                // central.scanForPeripheralsWithServices(new UUID[]{BLP_SERVICE_UUID, HTS_SERVICE_UUID, HRS_SERVICE_UUID});
            }
        }
    };

    public PBluetoothLEClient(AppRunner appRunner, BluetoothAdapter bleAdapter) {
        super(appRunner);
        mAppRunner = appRunner;
        mContext = appRunner.getAppContext();
        mBleAdapter = bleAdapter;
        mConnectionStatus = DISCONNECTED;

        central = new BluetoothCentral(appRunner.getAppContext(), bluetoothCentralCallback, new Handler());
        mAppRunner.whatIsRunning.add(this);
    }

    public PBluetoothLEClient connectDevice(String macAddress) {
        BluetoothPeripheral peripheral = central.getPeripheral(macAddress);
        peripheral.requestConnectionPriority(CONNECTION_PRIORITY_HIGH);
        central.connectPeripheral(peripheral, peripheralCallback);
        return this;
    }

    public PBluetoothLEClient connectDevice(String macAddress, int mtuSize) {
        this.mtuSize = mtuSize;
        return this.connectDevice(macAddress);
    }

    public PBluetoothLEClient autoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;

        return this;
    }

    public PBluetoothLEClient disconnectDevice(String macAddress) {
        central.getPeripheral(macAddress).cancelConnection();
        return this;
    }

    public PBluetoothLEClient onNewDeviceStatus(ReturnInterface callback) {
        mCallbackDevice = callback;
        return this;
    }

    public PBluetoothLEClient readFromCharacteristic(String macAddress, String serviceUUID, String charUUID) {
        BluetoothPeripheral peripheral = central.getPeripheral(macAddress);
        BluetoothGattCharacteristic btChar = peripheral.getCharacteristic(
                UUID.fromString(serviceUUID),
                UUID.fromString(charUUID)
        );
        peripheral.readCharacteristic(btChar);
        peripheral.setNotify(btChar, true);
        return this;
    }

    public PBluetoothLEClient onNewData(ReturnInterface callback) {
        mCallbackCharacteristic = callback;
        return this;
    }

    public PBluetoothLEClient onMtuChanged(ReturnInterface callback) {
        this.mCallbackMtu = callback;
        return this;
    }

    public PBluetoothLEClient write(String value, String macAddress, String serviceUUID, String charUUID) {
      return this._write(value.getBytes(), macAddress, serviceUUID, charUUID);
    }

    public PBluetoothLEClient _write(byte[] bytes, String macAddress, String serviceUUID, String charUUID) {
        BluetoothPeripheral peripheral = central.getPeripheral(macAddress);

        UUID sUUID = UUID.fromString(serviceUUID);
        UUID cUUID = UUID.fromString(charUUID);
        BluetoothGattCharacteristic btChar = peripheral.getCharacteristic(sUUID, cUUID);

        // peripheral.writeCharacteristic(btChar, value, type);
        peripheral.writeCharacteristic(btChar, bytes, WRITE_TYPE_DEFAULT);

        return this;
    }

    @Override
    public void __stop() {
        disconnectAllDevices();
        central.close();
    }

    public PBluetoothLEClient disconnectAllDevices() {
        central.getConnectedPeripherals().forEach(bluetoothPeripheral -> {
            bluetoothPeripheral.cancelConnection();
        });
        return this;
    }
}
