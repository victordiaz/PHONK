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

package io.phonk.runner.api.sensors;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;

import io.phonk.runner.api.ProtoBase;
import io.phonk.runner.api.common.ReturnInterface;
import io.phonk.runner.api.common.ReturnObject;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.FeatureNotAvailableException;
import io.phonk.runner.base.utils.MLog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class PLocation extends ProtoBase {

    protected static final String TAG = PLocation.class.getSimpleName();

    LocationManager locationManager;
    String provider;

    private boolean isGPSFix;
    private Location mLastLocation;
    private long mLastLocationMillis;
    private LocationListener listener;
    public boolean running;

    private ReturnInterface mCallback;


    // The minimum distance to change Updates in meters
    // private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10
    // meters

    // The minimum time between updates in milliseconds
    // private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1
    // minute

    public PLocation(AppRunner appRunner) {
        super(appRunner);
    }


    @ProtoMethod(description = "Start the location. Returns lat, lon, alt, speed, bearing", example = "")
    @ProtoMethodParam(params = {"function(lat, lon, alt, speed, bearing)"})
    public void start() {

        if (!isAvailable()) {

            try {
                MLog.d(TAG, "try");
                throw new FeatureNotAvailableException();
            } catch (FeatureNotAvailableException e) {
                e.printStackTrace();
                MLog.d(TAG, "catch");
                getAppRunner().pConsole.error("Your device doesn't have a GPS :(");
                // getAppRunner().interp.observingDebugger.setDisconnected(true);
                return;
            }
        }

        if (running) {
            return;
        }

        MLog.d(TAG, "starting GPS");

        // criteria.setSpeedRequired(true);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
            MLog.d(TAG, "GPS not enabled");
            showSettingsAlert();
        } else {
            MLog.d(TAG, "GPS enabled");

        }
        running = true;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // criteria.setBearingAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setSpeedRequired(false);

        provider = locationManager.getBestProvider(criteria, false);

        listener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                MLog.d(TAG, "the location status is: " + status);

                // TODO add listener to see when the GPS is on or not
                switch (status) {
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        if (mLastLocation != null) {
                            isGPSFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 3000;
                        }

                        if (isGPSFix) { // A fix has been acquired.
                            // Do something.
                        } else { // The fix has been lost.
                            // Do something.
                        }

                        //for (CustomSensorListener l : listeners) {
                        //	((GPSListener) l).onGPSStatus(isGPSFix);
                        //}

                        break;
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        // Do something.
                        isGPSFix = true;

                        break;
                }
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getContext().startActivity(intent);
            }

            @Override
            public void onLocationChanged(Location location) {
                MLog.d(TAG, "updated ");

                ReturnObject r = new ReturnObject();
                r.put("latitude", location.getLatitude());
                r.put("longitude", location.getLongitude());
                r.put("altitude", location.getAltitude());
                r.put("speed", location.getSpeed());
                r.put("accuracy", location.getAccuracy());
                r.put("speed", location.getSpeed());
                r.put("accuracy", location.getAccuracy());
                r.put("bearing", location.getBearing());
                r.put("provider", location.getProvider());
                r.put("time", location.getTime());
                mCallback.event(r);

                if (location == null) {
                    return;
                }

                mLastLocationMillis = SystemClock.elapsedRealtime();
                mLastLocation = location;

            }
        };

        locationManager.requestLocationUpdates(provider, 100, 0.1f, listener);
    }


    @ProtoMethod(description = "Start the GPS. Returns x, y, z", example = "")
    @ProtoMethodParam(params = {"function(x, y, z)"})
    public PLocation onChange(final ReturnInterface callbackfn) {
        mCallback = callbackfn;

        return this;
    }


    @ProtoMethod(description = "Get the last known location", example = "")
    @ProtoMethodParam(params = {""})
    public Location getLastKnownLocation() {
        return locationManager.getLastKnownLocation(provider);

    }


    @ProtoMethod(description = "Get the location name of a given latitude and longitude", example = "")
    @ProtoMethodParam(params = {"latitude", "longitude"})
    public String getLocationName(double lat, double lon) {
        String gpsLocation = "";
        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(lat, lon, 1);
            gpsLocation = addresses.get(0).getLocality();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return gpsLocation;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    private void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to the settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getContext().startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @ProtoMethod(description = "Get the distance from two points", example = "")
    @ProtoMethodParam(params = {"startLatitude", "starLongitude", "endLatitude", "endLongitude"})
    public double distance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        float[] results = null;
        // Location.distanceBetween(startLatitude, startLongitude, endLatitude,
        // endLongitude, results);

        Location locationA = new Location("point A");

        locationA.setLatitude(startLatitude);
        locationA.setLongitude(startLongitude);

        Location locationB = new Location("point B");

        locationB.setLatitude(endLatitude);
        locationB.setLongitude(endLongitude);

        float distance = locationA.distanceTo(locationB);

        return distance;
    }

    public boolean isAvailable() {
        PackageManager packageManager = getContext().getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    public void stop() {
        running = false;
        locationManager.removeUpdates(listener);
    }

    @Override
    public void __stop() {
        stop();
    }
}
