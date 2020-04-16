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

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import io.phonk.runner.AppRunnerFragment;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.api.network.PBluetooth;
import io.phonk.runner.apprunner.api.network.PBluetoothLE;
import io.phonk.runner.apprunner.api.network.PFtpClient;
import io.phonk.runner.apprunner.api.network.PFtpServer;
import io.phonk.runner.apprunner.api.network.PMqtt;
import io.phonk.runner.apprunner.api.network.PNfc;
import io.phonk.runner.apprunner.api.network.PSimpleHttpServer;
import io.phonk.runner.apprunner.api.network.PSocketIOClient;
import io.phonk.runner.apprunner.api.network.PWebSocketClient;
import io.phonk.runner.apprunner.api.network.PWebSocketServer;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apidoc.annotation.ProtoObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.network.NetworkUtils;
import io.phonk.runner.base.network.OSC;
import io.phonk.runner.base.network.ServiceDiscovery;
import io.phonk.runner.base.utils.ExecuteCmd;
import io.phonk.runner.base.utils.MLog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@ProtoObject
public class PNetwork extends ProtoBase {

    private final String TAG = PNetwork.class.getSimpleName();

    public PBluetooth bluetooth = null;
    public PBluetoothLE bluetoothLE;
    public ServiceDiscovery mDNS = null;
    private PWebSocketServer PWebsockerServer;

    public PNetwork(AppRunner appRunner) {
        super(appRunner);

        bluetooth = new PBluetooth(appRunner);
        bluetoothLE = new PBluetoothLE(appRunner);

        mDNS = new ServiceDiscovery(appRunner);
    }

    public void initForParentFragment(AppRunnerFragment fragment) {
        super.initForParentFragment(fragment);

        //prevent crashing in protocoder app
        MLog.d(TAG, "is getActivity() " + getActivity());

        if (getFragment() != null) {
            bluetooth.initForParentFragment(getFragment());
        }
    }

    public PNfc startNFC() {
        PNfc nfc = new PNfc(getAppRunner());
        nfc.initForParentFragment(getFragment());
        return nfc;
    }

    @ProtoMethod(description = "Downloads a file from a given Uri. Returns the progress", example = "")
    @ProtoMethodParam(params = {"url", "fileName", "function(progress)"})
    public void download(String url, String fileName, final ReturnInterface callbackfn) {

        NetworkUtils.DownloadTask downloadTask = new NetworkUtils.DownloadTask(getAppRunner().getAppContext(), url, getAppRunner().getProject().getFullPathForFile(fileName));
        downloadTask.addListener(new NetworkUtils.DownloadTask.DownloadListener() {

            @Override
            public void onUpdate(int progress) {
                ReturnObject ret = new ReturnObject();
                ret.put("progress", progress);
                if (callbackfn != null) callbackfn.event(ret);
            }
        });
        downloadTask.execute(url);

    }

    @ProtoMethod(description = "Downloads a file from a given Uri. Returns the progress", example = "")
    public void downloadWithSystemManager(String url) {
        downloadWithSystemManager(url, null);
    }

    @ProtoMethod(description = "Downloads a file from a given Uri. Returns the progress", example = "")
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

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

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

            }
        });
        t.start();

    }

    @ProtoMethod(description = "Check if internet connection is available", example = "")
    @ProtoMethodParam(params = {""})
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @ProtoMethod(description = "Returns the current device Ip address", example = "")
    @ProtoMethodParam(params = {""})
    public ReturnObject networkInfo() {
        return NetworkUtils.getLocalIpAddress(getContext());
    }

    @ProtoMethod(description = "Get the wifi ap information", example = "")
    @ProtoMethodParam(params = {""})
    public WifiInfo wifiInfo() {
        return NetworkUtils.getWifiInfo(getContext());
    }

    @ProtoMethod(description = "Starts an OSC server", example = "")
    @ProtoMethodParam(params = {"port", "function(jsonData)"})
    public OSC.Server createOSCServer(String port) {
        OSC osc = new OSC();
        OSC.Server server = osc.new Server();

        server.start(port);
        getAppRunner().whatIsRunning.add(server);

        return server;
    }

    @ProtoMethod(description = "Connects to a OSC server. Returns an object that allow sending messages", example = "")
    @ProtoMethodParam(params = {"address", "port"})
    public OSC.Client connectOSC(String address, int port) {
        OSC osc = new OSC();
        OSC.Client client = osc.new Client(address, port);
        getAppRunner().whatIsRunning.add(client);

        return client;
    }


    WifiManager.MulticastLock wifiLock;

    @ProtoMethod(description = "Enable multicast networking", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void multicast(boolean b) {
        WifiManager wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
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
            WifiManager wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
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


    @ProtoMethod(description = "Start a websocket server", example = "")
    @ProtoMethodParam(params = {"port", "function(status, socket, data)"})
    public PWebSocketServer createWebsocketServer(int port) {
        PWebSocketServer pWebSocketServer = new PWebSocketServer(getAppRunner(), port);

        return pWebSocketServer;
    }

    @ProtoMethod(description = "Connect to a websocket server", example = "")
    @ProtoMethodParam(params = {"uri", "function(status, data)"})
    public PWebSocketClient connectWebsocket(String uri) {
        PWebSocketClient pWebSocketClient = new PWebSocketClient(getAppRunner(), uri);

        return pWebSocketClient;
    }

    @ProtoMethod(description = "Connect to a SocketIO server", example = "")
    @ProtoMethodParam(params = {"uri", "function(status, message, data)"})
    public PSocketIOClient connectSocketIO(String uri) {
        PSocketIOClient socketIOClient = new PSocketIOClient(getAppRunner(), uri);

        return socketIOClient;
    }


    // http://stackoverflow.com/questions/3303805/are-there-any-good-short-code-examples-that-simply-read-a-new-gmail-message
    public void getEmail() throws MessagingException, IOException {
        Session session = Session.getDefaultInstance(System.getProperties(),null);
        Store store = null;
        store = session.getStore("imaps");

        // store.connect(this.host, this.userName, this.password);

        // Get default folder
        Folder folder = store.getDefaultFolder();
        folder.getMessages();
        folder.getNewMessageCount();
        Message m = folder.getMessage(0);
        m.getMessageNumber();
        m.getAllRecipients();
        m.getReceivedDate();
        m.getFrom();
        m.getSubject();
        m.getReplyTo();
        m.getContent();
        m.getSize();

        // Get any folder by name
        Folder[] folderList = folder.list();
    }

    // http://mrbool.com/how-to-work-with-java-mail-api-in-android/27800#ixzz2tulYAG00
    @ProtoMethod(description = "Send an E-mail. It requires passing a configuration object with (host, user, password, port, auth, ttl) parameters", example = "")
    @ProtoMethodParam(params = {"url", "function(data)"})
    public void sendEmail(String from, String to, String subject, String text, final HashMap<String, String> emailSettings)
            throws MessagingException {

        if (emailSettings == null) {
            return;
        }

        // final String host = "smtp.gmail.com";
        // final String address = "@gmail.com";
        // final String pass = "";

        Multipart multiPart;
        String finalString = "";

        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", emailSettings.get("ttl"));
        props.put("mail.smtp.host", emailSettings.get("host"));
        props.put("mail.smtp.user", emailSettings.get("user"));
        props.put("mail.smtp.password", emailSettings.get("password"));
        props.put("mail.smtp.port", emailSettings.get("port"));
        props.put("mail.smtp.auth", emailSettings.get("auth"));

        Log.i("Check", "done pops");
        final Session session = Session.getDefaultInstance(props, null);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(finalString.getBytes(), "text/plain"));
        final MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setDataHandler(handler);
        Log.i("Check", "done sessions");

        multiPart = new MimeMultipart();

        InternetAddress toAddress;
        toAddress = new InternetAddress(to);
        message.addRecipient(Message.RecipientType.TO, toAddress);
        Log.i("Check", "added recipient");
        message.setSubject(subject);
        message.setContent(multiPart);
        message.setText(text);

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //MLog.i("check", "transport");
                    Transport transport = session.getTransport("smtp");
                    //MLog.i("check", "connecting");
                    transport.connect(emailSettings.get("host"), emailSettings.get("user"), emailSettings.get("password"));
                    //MLog.i("check", "wana send");
                    transport.sendMessage(message, message.getAllRecipients());
                    transport.close();
                    //MLog.i("check", "sent");
                } catch (AddressException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

            }
        });
        t.start();

    }


    @ProtoMethod(description = "Simple http get. It returns the data using the callback", example = "")
    @ProtoMethodParam(params = {"url", "function(eventType, responseString)"})
    public void httpGet(String url, final ReturnInterface callbackfn) {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(url).build();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final ReturnObject ret = new ReturnObject();
                try {
                    Response response = client.newCall(request).execute();
                    ret.put("response", response.body().string());
                    ret.put("status", response.code());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callbackfn.event(ret);
                    }
                });
            }
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


    @ProtoMethod(description = "Simple http post request. It needs an object to be sent. If an element of the object contains the key file then it will try to upload the resource indicated in the value as Uri ", example = "")
    @ProtoMethodParam(params = {"url", "params", "function(responseString)"})
    public void httpPost(final String url, final NativeArray parts, final ReturnInterface callbackfn) {
        final OkHttpClient client = new OkHttpClient();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

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
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callbackfn.event(ret);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }


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


    @ProtoMethod(description = "Simple http server, serving the content of the project folder", example = "")
    @ProtoMethodParam(params = {"port", "function(responseString)"})
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


        new Thread(new Runnable() {
            @Override
            public void run() {
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
                    java.lang.Thread.sleep(500);   // this kludge seemed to be required.
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

            }
        }).start();


        return "";
    }

    @ProtoMethod(description = "Enable/Disable the Wifi adapter", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void enableWifi(boolean enabled) {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enabled);
    }


    @ProtoMethod(description = "Check if the Wifi adapter is enabled", example = "")
    @ProtoMethodParam(params = {})
    public boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    // http://stackoverflow.com/questions/3213205/how-to-detect-system-information-like-os-or-device-type
    @ProtoMethod(description = "Get the network type", example = "")
    @ProtoMethodParam(params = {})
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

    // http://stackoverflow.com/questions/8818290/how-to-connect-to-mContext-specific-wifi-network-in-android-programmatically

    @ProtoMethod(description = "Connect to mContext given Wifi network with mContext given 'wpa', 'wep', 'open' type and mContext password", example = "")
    @ProtoMethodParam(params = {"ssidName", "type", "password"})
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

        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
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

    private Object mIsWifiAPEnabled = true;


    @ProtoMethod(description = "Enable/Disable mContext Wifi access point", example = "")
    @ProtoMethodParam(params = {"boolean, apName"})
    public void wifiAP(boolean enabled, String wifiName) {

        WifiManager wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
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


    // --------- RegisterServiceCB ---------//
    public interface RegisterServiceCB {
        void event();
    }

    @ProtoMethod(description = "Ping mContext Ip address", example = "")
    @ProtoMethodParam(params = {"ip", "function(time)"})
    public void ping(final String where, final int num, final ReturnInterface callbackfn) {
       mHandler.post(new Runnable() {
           @Override
           public void run() {
               final Pattern pattern = Pattern.compile("time=(\\d.+)\\s*ms");
               final Matcher[] m = {null};

               new ExecuteCmd("/system/bin/ping -c " + num + " " + where, new ReturnInterface() {
                   @Override
                   public void event(ReturnObject r) {
                       //MLog.d(TAG, pattern.toString() + "" + buffer);

                       ReturnObject ret = new ReturnObject();
                       m[0] = pattern.matcher((CharSequence) r.get("value"));
                       if (m[0].find()) {
                           ret.put("time", Float.parseFloat(m[0].group(1)));
                       } else {
                           ret.put("time", -1);
                       }
                       callbackfn.event(ret);


                   }
               }).start();
           }
       });
    }


    @ProtoMethod(description = "Start a ftp server in the given port", example = "")
    @ProtoMethodParam(params = {"port", "function(activity)"})
    public PFtpServer createFtpServer(final int port, PFtpServer.FtpServerCb callback) {
        PFtpServer ftpServer = new PFtpServer(port, callback);
        getAppRunner().whatIsRunning.add(ftpServer);

        return ftpServer;
    }


    @ProtoMethod(description = "Connect to ftp", example = "")
    @ProtoMethodParam(params = {})
    public PFtpClient createFtpConnection() {
        PFtpClient ftpClient = new PFtpClient(getAppRunner());

        return ftpClient;
    }

    @ProtoMethod(description = "Connect to a MQTT service", example = "")
    @ProtoMethodParam(params = {})
    public PMqtt createMQTTClient() {
        PMqtt pMqtt = new PMqtt(getAppRunner());

        return pMqtt;
    }


    @Override
    public void __stop() {

    }


}
