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

package io.phonk.runner.base.network;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.MLog;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ServiceDiscovery {

    private static final String TAG = ServiceDiscovery.class.getSimpleName();
    private final AppRunner mAppRunner;


    public ServiceDiscovery(AppRunner appRunner) {
        mAppRunner = appRunner;
    }

    public Create register(String serviceName, String serviceType, int port) {
        return new Create(mAppRunner.getAppContext(), serviceName, serviceType, port);
    }

    public Discover discover(String serviceType) {
        return new Discover(mAppRunner.getAppContext(), serviceType);
    }

    public class Create {
        private final NsdManager mNsdManager;
        private final NsdServiceInfo serviceInfo;
        private final NsdManager.RegistrationListener mRegistrationListener;
        private ReturnInterface mCallback;

        Create(Context a, String name, String serviceType, int port) {

            // Create the NsdServiceInfo object, and populate it.
            serviceInfo = new NsdServiceInfo();

            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            serviceInfo.setServiceName(name);
            serviceInfo.setServiceType(serviceType);
            serviceInfo.setPort(port);

            String ip = (String) mAppRunner.pNetwork.networkInfo().get("ip");
            try {
                InetAddress p = InetAddress.getByName(ip);
                serviceInfo.setHost(p);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            mNsdManager = (NsdManager) a.getSystemService(Context.NSD_SERVICE);

            mRegistrationListener = new NsdManager.RegistrationListener() {
                @Override
                public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                    // Registration failed!  Put debugging code here to determine why.
                    ReturnObject ret = new ReturnObject();
                    ret.put("name", serviceInfo.getServiceName());
                    ret.put("status", "registration_failed");
                    if (mCallback != null) mCallback.event(ret);
                }

                @Override
                public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                    // Unregistration failed.  Put debugging code here to determine why.
                    ReturnObject ret = new ReturnObject();
                    ret.put("name", serviceInfo.getServiceName());
                    ret.put("status", "unregistration_failed");
                    if (mCallback != null) mCallback.event(ret);
                }

                @Override
                public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                    // Save the service name.  Android may have changed it in order to
                    // resolve mContext conflict, so update the name you initially requested
                    // with the name Android actually used.

                    ReturnObject ret = new ReturnObject();
                    ret.put("status", "registered");
                    ret.put("host", serviceInfo.getHost());
                    ret.put("port", serviceInfo.getPort());
                    ret.put("type", serviceInfo.getServiceType());
                    ret.put("name", serviceInfo.getServiceName());
                    if (mCallback != null) mCallback.event(ret);
                }

                @Override
                public void onServiceUnregistered(NsdServiceInfo arg0) {
                    // Service has been unregistered.  This only happens when you call
                    // NsdManager.unregisterService() and pass in this listener.
                    ReturnObject ret = new ReturnObject();
                    ret.put("name", serviceInfo.getServiceName());
                    ret.put("status", "unregistered");
                    if (mCallback != null) mCallback.event(ret);
                }
            };
        }

        public Create onNewData(ReturnInterface callback) {
            mCallback = callback;

            return this;
        }

        public Create start() {
            mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

            mAppRunner.whatIsRunning.add(this);

            return this;
        }

        public void stop() {
            mNsdManager.unregisterService(mRegistrationListener);
        }
    }


    public class Discover {
        final NsdManager mNsdManager;
        final NsdManager.DiscoveryListener mDiscoveryListener;
        private final String mServiceType;
        ReturnInterface mCallback;

        Discover(Context a, final String serviceType) {
            mServiceType = serviceType;
            mNsdManager = (NsdManager) a.getSystemService(Context.NSD_SERVICE);

            // Instantiate mContext new DiscoveryListener
            mDiscoveryListener = new NsdManager.DiscoveryListener() {

                @Override
                public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                    Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                    //mNsdManager.stopServiceDiscovery(this);
                    ReturnObject ret = new ReturnObject();
                    ret.put("type", serviceType);
                    ret.put("error", errorCode);
                    ret.put("status", "discovery_failed");
                    if (mCallback != null) mCallback.event(ret);
                }

                @Override
                public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                    Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                    ReturnObject ret = new ReturnObject();
                    ret.put("name", serviceType);
                    ret.put("error", errorCode);
                    ret.put("status", "stop_discovering_failed");
                    if (mCallback != null) mCallback.event(ret);
                }

                //  Called as soon as service discovery begins.
                @Override
                public void onDiscoveryStarted(String regType) {
                    MLog.d(TAG, "Service discovery started");
                    ReturnObject ret = new ReturnObject();
                    ret.put("name", regType);
                    ret.put("status", "started");
                    if (mCallback != null) mCallback.event(ret);
                }

                @Override
                public void onDiscoveryStopped(String serviceType) {
                    Log.i(TAG, "Discovery stopped: " + serviceType);
                    ReturnObject ret = new ReturnObject();
                    ret.put("type", serviceType);
                    ret.put("status", "discovery_stopped");
                    if (mCallback != null) mCallback.event(ret);
                }

                @Override
                public void onServiceFound(final NsdServiceInfo serviceInfo) {
                    // A service was found!  Do something with it.

                    MLog.d(TAG, "1: " + serviceInfo.getServiceType() + " 2: " + mServiceType);
                    if (serviceInfo.getServiceType().equals(mServiceType)) {
                        mNsdManager.resolveService(serviceInfo, new NsdManager.ResolveListener() {
                            @Override
                            public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {

                            }

                            @Override
                            public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
                                ReturnObject ret = new ReturnObject();
                                ret.put("status", "discovered_resolved");
                                ret.put("port", serviceInfo.getPort());
                                ret.put("serviceName", serviceInfo.getServiceName());
                                ret.put("host", serviceInfo.getHost());
                                ret.put("type", serviceInfo.getServiceType());
                                if (mCallback != null) mCallback.event(ret);
                            }
                        });
                    }

                }

                @Override
                public void onServiceLost(NsdServiceInfo serviceInfo) {
                    // When the network service is no longer available.
                    // Internal bookkeeping code goes here.
                    Log.e(TAG, "service lost");
                    ReturnObject ret = new ReturnObject();
                    ret.put("status", "service_lost");
                    ret.put("port", serviceInfo.getPort());
                    ret.put("serviceName", serviceInfo.getServiceName());
                    ret.put("host", serviceInfo.getHost());
                    ret.put("type", serviceInfo.getServiceType());
                    if (mCallback != null) mCallback.event(ret);
                }
            };
        }

        public Discover onNewData(ReturnInterface callback) {
            mCallback = callback;

            return this;
        }

        public Discover start() {
            mNsdManager.discoverServices(mServiceType, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
            mAppRunner.whatIsRunning.add(this);
            return this;
        }

        public void stop() {
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        }
    }
}
