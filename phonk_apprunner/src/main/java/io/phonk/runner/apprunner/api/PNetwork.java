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

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.phonk.runner.AppRunnerFragment;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apidoc.annotation.PhonkObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.api.network.PBluetooth;
import io.phonk.runner.apprunner.api.network.PBluetoothLE;
import io.phonk.runner.apprunner.api.network.PFtpClient;
import io.phonk.runner.apprunner.api.network.PFtpServer;
import io.phonk.runner.apprunner.api.network.PMqtt;
import io.phonk.runner.apprunner.api.network.PNfc;
import io.phonk.runner.apprunner.api.network.PSimpleHttpServer;
import io.phonk.runner.apprunner.api.network.PWebSocketClient;
import io.phonk.runner.apprunner.api.network.PWebSocketServer;
import io.phonk.runner.apprunner.interpreter.PhonkNativeArray;
import io.phonk.runner.base.network.NetworkUtils;
import io.phonk.runner.base.network.OSC;
import io.phonk.runner.base.network.ServiceDiscovery;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.ExecuteCmd;
import io.phonk.runner.base.utils.MLog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@PhonkObject
public class PNetwork extends ProtoBase {

    private final String TAG = PNetwork.class.getSimpleName();

    public PBluetooth bluetooth = null;
    public PBluetoothLE bluetoothLE;
    public ServiceDiscovery mDNS = null;
    public PNfc nfc = null;

    private PWebSocketServer PWebsockerServer;

    public PNetwork(AppRunner appRunner) {
        super(appRunner);

        bluetooth = new PBluetooth(appRunner);
        if (AndroidUtils.isVersionLollipop()) bluetoothLE = new PBluetoothLE(appRunner);
        mDNS = new ServiceDiscovery(appRunner);
        nfc = new PNfc(appRunner);
    }

    public void initForParentFragment(AppRunnerFragment fragment) {
        super.initForParentFragment(fragment);

        // prevent crashing in phonk app
        if (getFragment() != null) {
            bluetooth.initForParentFragment(getFragment());
            nfc.initForParentFragment(getFragment());
        }
    }

    @PhonkMethod(description = "Downloads a file from a given Uri. Returns the progress", example = "")
    @PhonkMethodParam(params = {"url", "fileName", "function(progress)"})
    public void download(String url, String fileName, final ReturnInterface callbackfn) {

        NetworkUtils.DownloadTask downloadTask = new NetworkUtils.DownloadTask(getAppRunner().getAppContext(), url, getAppRunner().getProject().getFullPathForFile(fileName));
        downloadTask.addListener(progress -> {
            ReturnObject ret = new ReturnObject();
            ret.put("progress", progress);
            if (callbackfn != null) callbackfn.event(ret);
        });
        downloadTask.execute(url);

    }

    @PhonkMethod(description = "Downloads a file from a given Uri. Returns the progress", example = "")
    public void downloadWithSystemManager(String url) {
        downloadWithSystemManager(url, null);
    }

    @PhonkMethod(description = "Downloads a file from a given Uri. Returns the progress", example = "")
    public void downloadWithSystemManager(String url, final ReturnInterface callback) {
        final DownloadManager dm = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        final long enqueue = dm.enqueue(request);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                            if (callback != null) callback.event(null);
                            // callback successful
                        }
                    }
                }
            }
        };

        getContext().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


    }

    // @JavascriptInterface
    // @APIMethod(description = "", example = "")
    // @APIParam( params = {"file", "function()"} )
    public void isReachable(final String host, final String callbackfn) {

        Thread t = new Thread(() -> {

            // doesnt work! isReachable
            //
            // try {
            // InetAddress in = InetAddress.getByName(host);
            // boolean isReacheable = in.isReachable(5000);
            // callback(callbackfn, isReacheable);
            // } catch (UnknownHostException e) {
            // e.printStackTrace();
            // } catch (IOException e) {
            // e.printStackTrace();
            // }

        });
        t.start();

    }

    @PhonkMethod(description = "Check if internet connection is available", example = "")
    @PhonkMethodParam(params = {""})
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @PhonkMethod(description = "Returns the current device Ip address", example = "")
    @PhonkMethodParam(params = {""})
    public ReturnObject networkInfo() {
        return NetworkUtils.getLocalIpAddress(getContext());
    }

    @PhonkMethod(description = "Get the wifi ap information", example = "")
    @PhonkMethodParam(params = {""})
    public WifiInfo wifiInfo() {
        return NetworkUtils.getWifiInfo(getContext());
    }

    @PhonkMethod(description = "Starts an OSC server", example = "")
    @PhonkMethodParam(params = {"port", "function(jsonData)"})
    public OSC.Server createOSCServer(String port) {
        OSC osc = new OSC();
        OSC.Server server = osc.new Server();

        server.start(port);
        getAppRunner().whatIsRunning.add(server);

        return server;
    }

    @PhonkMethod(description = "Connects to a OSC server. Returns an object that allow sending messages", example = "")
    @PhonkMethodParam(params = {"address", "port"})
    public OSC.Client connectOSC(String address, int port) {
        OSC osc = new OSC();
        OSC.Client client = osc.new Client(address, port);
        getAppRunner().whatIsRunning.add(client);

        return client;
    }


    WifiManager.MulticastLock wifiLock;

    @PhonkMethod(description = "Enable multicast networking", example = "")
    @PhonkMethodParam(params = {"boolean"})
    public void multicast(boolean b) {
        WifiManager wifi = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            if (b) {
                wifiLock = wifi.createMulticastLock("mylock");
                wifiLock.acquire();
            } else {
                wifiLock.release();
            }
        }
    }

    class MulticastEnabler {
        WifiManager.MulticastLock wifiLock;

        MulticastEnabler(boolean b) {
            WifiManager wifi = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                if (b) {
                    wifiLock = wifi.createMulticastLock("mylock");
                    wifiLock.acquire();
                    getAppRunner().whatIsRunning.add(this);

                } else {
                    wifiLock.release();
                }
            }
        }

        public void stop() {
            if (wifiLock != null) {
                wifiLock.release();
            }
        }

    }

    @PhonkMethod(description = "Start a websocket server", example = "")
    @PhonkMethodParam(params = {"port", "function(status, socket, data)"})
    public PWebSocketServer createWebsocketServer(int port) {
        PWebSocketServer pWebSocketServer = new PWebSocketServer(getAppRunner(), port);

        return pWebSocketServer;
    }

    @PhonkMethod(description = "Connect to a websocket server", example = "")
    @PhonkMethodParam(params = {"uri", "function(status, data)"})
    public PWebSocketClient connectWebsocket(String uri) {
        PWebSocketClient pWebSocketClient = new PWebSocketClient(getAppRunner(), uri);

        return pWebSocketClient;
    }

    @PhonkMethod(description = "Simple http get. It returns the data using the callback", example = "")
    @PhonkMethodParam(params = {"url", "function(eventType, responseString)"})
    public void httpGet(String url, final ReturnInterface callbackfn) {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(url).build();

        Thread t = new Thread(() -> {
            final ReturnObject ret = new ReturnObject();
            try {
                Response response = client.newCall(request).execute();
                ret.put("response", response.body().string());
                ret.put("status", response.code());
            } catch (IOException e) {
                e.printStackTrace();
            }

            mHandler.post(() -> callbackfn.event(ret));
        });
        t.start();

            /*
        class RequestTask extends AsyncTask<String, String, String> {
            String responseString = null;

            @Override
            protected String doInBackground(String... uri) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;
                try {
                    URL url = new URL(uri[0]);
                    response = httpclient.execute(new HttpGet(url.toString()));
                    final StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        responseString = out.toString();
                    } else {
                        // Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                    MLog.d(TAG, "downloading ");

                    final ReturnObject ret = new ReturnObject();
                    ret.put("status", statusLine.getStatusCode());
                    ret.put("response", responseString);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callbackfn.event(ret);
                        }
                    });

                } catch (ClientProtocolException e) {
                    MLog.e(TAG, e.toString());
                } catch (IOException e) {
                    MLog.e(TAG, e.toString());
                } finally {
                    MLog.e(TAG, "error");
                }
                return responseString;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                // Do anything with response..
            }
        }

        MLog.d(TAG, "" + new RequestTask().execute(url));
        */
    }

    // --------- postRequest ---------//
    interface HttpPostCB {
        void event(String string);
    }


    @PhonkMethod(description = "Simple http post request. It needs an object to be sent. If an element of the object contains the key file then it will try to upload the resource indicated in the value as Uri ", example = "")
    @PhonkMethodParam(params = {"url", "params", "function(responseString)"})
    public void httpPost(final String url, final NativeArray parts, final ReturnInterface callbackfn) {
        final OkHttpClient client = new OkHttpClient();

        Thread t = new Thread(() -> {

            MultipartBody.Builder formBody = new MultipartBody.Builder();
            formBody.setType(MultipartBody.FORM);

            for (int i = 0; i < parts.size(); i++) {
                NativeObject o = (NativeObject) parts.get(i);

                // go through elements
                String name = o.get("name").toString();
                String content = o.get("content").toString();
                String type = o.get("type").toString();

                if (type.equals("file")) {
                    String mediaType = (String) o.get("mediaType");
                    File f = new File(getAppRunner().getProject().getFullPathForFile(content));
                    MLog.d("nn1", f.getAbsolutePath() + " " + content + " " + name + " " + mediaType);
                    formBody.addFormDataPart(name, content, RequestBody.create(MediaType.parse(mediaType), f));
                } else {
                    formBody.addFormDataPart(name, content);
                }
            }
            MultipartBody body = formBody.build();

            Request request = new Request.Builder().url(url).post(body).build();
            Response response = null;
            final ReturnObject ret = new ReturnObject();
            try {
                response = client.newCall(request).execute();
                ret.put("response", response.body().string());
                ret.put("status", response.code());
                mHandler.post(() -> callbackfn.event(ret));
            } catch (IOException e) {
                e.printStackTrace();
            }


        });
        t.start();


        /*
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpContext localContext = new BasicHttpContext();
        final HttpPost httpPost = new HttpPost(url);

        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        for (int i = 0; i < parts.size(); i++) {
            NativeObject o = (NativeObject) parts.get(i);

            // go through elements
            String name = (String) o.get("name");
            String content = (String) o.get("content");
            String type = (String) o.get("type");

            // create the multipart
            if (type.contains("file")) {
                File f = new File(getAppRunner().getProject().getFullPathForFile(content));
                ContentBody cbFile = new FileBody(f);
                entity.addPart(name, cbFile);
            } else if (type.equals("text")){ // Normal string data
                entity.addPart(name, new StringBody(content, ContentType.TEXT_PLAIN));
            } else if (type.equals("json")){ // Normal string data
                entity.addPart(name, new StringBody(content, ContentType.APPLICATION_JSON));
            }
        }

        // send
        httpPost.setEntity(entity);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    HttpResponse response = httpClient.execute(httpPost, localContext);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    String responseString = out.toString();

                    ReturnObject o = new ReturnObject();
                    o.put("status", response.getStatusLine().toString());
                    o.put("response", responseString);
                    callbackfn.event(o);
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        */
    }

    //gives the url trying to access
    //if (url == "") {
    //} else {
    //server.serveFiles()
    //
    //}


    @PhonkMethod(description = "Simple http server, serving the content of the project folder", example = "")
    @PhonkMethodParam(params = {"port", "function(responseString)"})
    public PSimpleHttpServer createSimpleHttpServer(int port) {
        PSimpleHttpServer httpServer = null;
        try {
            httpServer = new PSimpleHttpServer(getAppRunner(), port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return httpServer;
    }

    public String ssh(final String serverAddress, final int port, final String username, final String password) {
        MLog.d(TAG, "trying to connect");


        new Thread(() -> {
            com.jcraft.jsch.Session session = null;
            try {
                JSch jsch = new JSch();
                String result = "";

                session = jsch.getSession(username, serverAddress, port);
                session.setPassword(password);

                // Avoid asking for key confirmation
                Properties prop = new Properties();
                prop.put("StrictHostKeyChecking", "no");
                session.setConfig(prop);
                session.connect();

                // SSH Channel
                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                channel.setOutputStream(stream);

                // Execute command
                channel.setCommand("ls -ltr");
                channel.connect(1000);
                Thread.sleep(500);   // this kludge seemed to be required.
                channel.disconnect();

                result = stream.toString();
                MLog.d(TAG, "result: " + result);

            } catch (JSchException ex) {
                String s = ex.toString();
                System.out.println(s);
            } catch (InterruptedException ex) {
                String s = ex.toString();
                System.out.println(s);
            } finally {
                MLog.d(TAG, "disconnected");
                if (session != null) session.disconnect();
            }

        }).start();


        return "";
    }

    @PhonkMethod(description = "Enable/Disable the Wifi adapter", example = "")
    @PhonkMethodParam(params = {"boolean"})
    public void enableWifi(boolean enabled) {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enabled);
    }


    @PhonkMethod(description = "Check if the Wifi adapter is enabled", example = "")
    @PhonkMethodParam(params = {})
    public boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    // http://stackoverflow.com/questions/3213205/how-to-detect-system-information-like-os-or-device-type
    @PhonkMethod(description = "Get the network type", example = "")
    @PhonkMethodParam(params = {})
    public String getNetworkType() {
        String type = "none";
        TelephonyManager tm = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        switch (tm.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                type = "4G";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                type = "3G";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                type = "GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                type = "2G";
                break;
        }

        return type;
    }

    // http://stackoverflow.com/questions/8818290/how-to-connect-to-a-specific-wifi-network-in-android-programmatically
    @PhonkMethod(description = "Connect to a given Wifi network with a given 'wpa', 'wep', 'open' type and mContext password", example = "")
    @PhonkMethodParam(params = {"ssidName", "type", "password"})
    public void connectWifi(String networkSSID, String type, String networkPass) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\""; // Please note the quotes. String
        // should contain ssid in quotes

        if (type.equals("wep")) {
            // wep
            conf.wepKeys[0] = "\"" + networkPass + "\"";
            conf.wepTxKeyIndex = 0;
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        } else if (type.equals("wpa")) {
            // wpa
            conf.preSharedKey = "\"" + networkPass + "\"";
        } else if (type.equals("open")) {
            // open
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }
    }

    private boolean mIsWifiAPEnabled = true;

    @PhonkMethod(description = "Enable/Disable a Wifi access point", example = "")
    @PhonkMethodParam(params = {"AP name, enabled"})
    public void wifiAP(String wifiName, boolean enabled) {

        WifiManager wifi = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Method[] wmMethods = wifi.getClass().getDeclaredMethods();
        Log.d(TAG, "enableMobileAP methods " + wmMethods.length);
        for (Method method : wmMethods) {
            Log.d(TAG, "enableMobileAP method.getName() " + method.getName());
            if (method.getName().equals("setWifiApEnabled")) {
                WifiConfiguration netConfig = new WifiConfiguration();
                netConfig.SSID = wifiName;
                netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                //
                try {
                    //MLog.d(TAG, "enableMobileAP try: ");
                    method.invoke(wifi, netConfig, enabled);
                    if (netConfig.wepKeys != null && netConfig.wepKeys.length >= 1) {
                        Log.d(TAG, "enableMobileAP key : " + netConfig.wepKeys[0]);
                    }
                    //MLog.d(TAG, "enableMobileAP enabled: ");
                    mIsWifiAPEnabled = enabled;
                } catch (Exception e) {
                    //MLog.e(TAG, "enableMobileAP failed: ", e);
                }
            }
        }
    }

    BroadcastReceiver wifiReceiver;
    public void wifiScan(ReturnInterface callback) {
        if (wifiReceiver != null) getAppRunner().getAppContext().unregisterReceiver(wifiReceiver);

        WifiManager wifi = (WifiManager) getAppRunner().getAppContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                List<ScanResult> results = wifi.getScanResults();

                final PhonkNativeArray valuesArray = new PhonkNativeArray(0);
                for (int i = 0; i < results.size(); i++) {

                    ReturnObject result = new ReturnObject();

                    result.put("SSID", results.get(i).SSID);
                    result.put("BSSID", results.get(i).BSSID);
                    result.put("frequency", results.get(i).frequency);
                    result.put("level", results.get(i).level);
                    result.put("capabilities", results.get(i).capabilities);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        result.put("centerFreq0", results.get(i).centerFreq0);
                        result.put("centerFreq1", results.get(i).centerFreq1);
                        result.put("channelWidth", results.get(i).channelWidth);
                        result.put("timestamp", results.get(i).timestamp);
                        result.put("venueName", results.get(i).venueName);
                    }

                    valuesArray.put(valuesArray.size(), valuesArray, result);
                }
                ReturnObject ret = new ReturnObject();
                ret.put("networks", valuesArray);
                callback.event(ret);

                if (wifiReceiver != null) getAppRunner().getAppContext().unregisterReceiver(wifiReceiver);
            }
        };

        wifi.startScan();
        getAppRunner().getAppContext().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    /*
    @SuppressLint("MissingPermission")
    public void getCellNeightbours() {
        TelephonyManager manager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfoList = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return cellInfoList;
        }
        cellInfoList = manager.getAllCellInfo();
        if (cellInfoList != null) {
            for (CellInfo info : cellInfoList) {
                //
            }
        }
    }
     */

    // --------- RegisterServiceCB ---------//
    public interface RegisterServiceCB {
        void event();
    }

    @PhonkMethod(description = "Ping mContext Ip address", example = "")
    @PhonkMethodParam(params = {"ip", "function(time)"})
    public void ping(final String where, final int num, final ReturnInterface callbackfn) {
        mHandler.post(() -> {
            final Pattern pattern = Pattern.compile("time=(\\d.+)\\s*ms");
            final Matcher[] m = {null};

            new ExecuteCmd("/system/bin/ping -c " + num + " " + where, r -> {
                //MLog.d(TAG, pattern.toString() + "" + buffer);

                ReturnObject ret = new ReturnObject();
                m[0] = pattern.matcher((CharSequence) r.get("value"));
                if (m[0].find()) {
                    ret.put("time", Float.parseFloat(m[0].group(1)));
                } else {
                    ret.put("time", -1);
                }
                callbackfn.event(ret);


            }).start();
        });
    }


    @PhonkMethod(description = "Start a ftp server in the given port", example = "")
    @PhonkMethodParam(params = {"port", "function(activity)"})
    public PFtpServer createFtpServer(final int port, PFtpServer.FtpServerCb callback) {
        PFtpServer ftpServer = new PFtpServer(port, callback);
        getAppRunner().whatIsRunning.add(ftpServer);

        return ftpServer;
    }


    @PhonkMethod(description = "Connect to ftp", example = "")
    @PhonkMethodParam(params = {})
    public PFtpClient createFtpConnection() {
        PFtpClient ftpClient = new PFtpClient(getAppRunner());

        return ftpClient;
    }

    @PhonkMethod(description = "Connect to a MQTT service", example = "")
    @PhonkMethodParam(params = {})
    public PMqtt createMQTTClient() {
        PMqtt pMqtt = new PMqtt(getAppRunner());

        return pMqtt;
    }

    @Override
    public void __stop() {
        if (wifiReceiver != null) getAppRunner().getAppContext().unregisterReceiver(wifiReceiver);
    }

}
