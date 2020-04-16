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

package io.phonk.server;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import io.phonk.events.Events;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.helpers.PhonkScriptHelper;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.utils.FileIO;
import io.phonk.runner.base.utils.MLog;
import io.phonk.server.model.ProtoFile;
import io.phonk.server.networkexchangeobjects.NEOProject;

public class PhonkHttpServer extends NanoHTTPD {
    public static final String TAG = PhonkHttpServer.class.getSimpleName();

    private final int COMMAND = 3;
    private final int TYPE = 3;
    private final int FOLDER = 4;
    private final int PROJECT_NAME = 5;
    private final int PROJECT_ACTION = 6;
    private final int FILE_DELIMITER = 6;
    private final int FILE_ACTION = 7;
    private final int FILE_NAME = 8;


    private static WeakReference<Context> mContext;

    private static String WEBAPP_DIR = "webide/";
    Gson gson;
    private String views;
    private ConnectionCallback mConnectionCallback;

    public PhonkHttpServer(Context context, int port) { //} throws IOException {
        super(port);

        mContext = new WeakReference<Context>(context);

        gson = new GsonBuilder().setPrettyPrinting().create();
        //addMappings();

        try {
            // start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (mConnectionCallback != null)
            mConnectionCallback.event(session.getHeaders().get("remote-addr"));

        Response res = null;

        // this 2 lines add utf-8 support
        // https://stackoverflow.com/questions/42504056/why-do-i-get-messy-code-of-chinese-filename-when-i-upload-files-to-nanohttpd-ser
        ContentType ct = new ContentType(session.getHeaders().get("content-type")).tryUTF8();
        session.getHeaders().put("content-type", ct.getContentTypeHeader());

        String uri = session.getUri();

        if (uri.startsWith("/api")) res = serveAPI(session);
        else res = serveWebIDE(session);

        //adding CORS mode for WebIDE debugging from the computer
        if (PhonkSettings.DEBUG) {
            res.addHeader("Access-Control-Allow-Methods", "DELETE, GET, POST, PUT");
            res.addHeader("Access-Control-Allow-Origin", "*");
            res.addHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Access-Control-Allow-Headers, Authorization");
        }

        return res;
    }

    /*
    // project global actions
     0   1   2       3
    "/api/project/list/"
    "/api/project/stop_all"
    "/api/project/execute_code"

    // project dependent actions
    0   1      2   3   4   5    6
    "/api/project/[folder]/[subfolder]/[p]/new"
    "/api/project/[ff]/[sf]/[p]/save/" (POST)
    "/api/project/[ff]/[sf]/[p]/load/"
    "/api/project/[ff]/[sf]/[p]/delete/"
    "/api/project/[ff]/[sf]/[p]/run/"
    "/api/project/[ff]/[sf]/[p]/stop/"

    // file actions
    0   1      2   3   4    5      6    7
    "/api/project/[ff]/[sf]/[p]/files/create" (POST)
    "/api/project/[ff]/[sf]/[p]/files/save" (POST)
    "/api/project/[ff]/[sf]/[p]/files/list"
    "/api/project/[ff]/[sf]/[p]/files/delete/" (POST)
    "/api/project/[ff]/[sf]/[p]/files/load/main.js"         7
    */
    private Response serveAPI(IHTTPSession session) {
        Response res = null;

        String uri = session.getUri();
        String[] uriSplitted = uri.split("/");

        HashMap<String, String> cmd;

        // debug the params
        // for (int i = 0; i < uriSplitted.length; i++) {
        //    MLog.d(TAG, uriSplitted.length + " " + i + " " + uriSplitted[i]);
        // }


        /**
         * Project global actions
         *
         * /api/project/command
         */

        if (uriSplitted.length >= 4 && uriSplitted.length <= 5) {
            if (uriSplitted[COMMAND].equals("list")) {
                HashMap<String, ArrayList> files = new HashMap<>();

                ArrayList<ProtoFile> userFolder = PhonkScriptHelper.listProjectsInFolder(PhonkSettings.USER_PROJECTS_FOLDER, 1);
                ArrayList<ProtoFile> examplesFolder = PhonkScriptHelper.listProjectsInFolder(PhonkSettings.EXAMPLES_FOLDER, 1);

                files.put(PhonkSettings.USER_PROJECTS_FOLDER, userFolder);
                files.put(PhonkSettings.EXAMPLES_FOLDER, examplesFolder);

                String jsonFiles = gson.toJson(files);

                // MLog.d("list", jsonFiles);
                EventBus.getDefault().post(new Events.HTTPServerEvent("project_list_all"));

                res = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_TYPES.get("json"), jsonFiles);
            } else if (uriSplitted[COMMAND].equals("execute_code")) {
                // MLog.d(TAG, "run code");

                // POST DATA
                String json;
                final HashMap<String, String> map = new HashMap<String, String>();
                try {
                    session.parseBody(map);
                    if (map.isEmpty()) return newFixedLengthResponse("BUG");

                    json = map.get("postData");
                    NEOProject neo = gson.fromJson(json, NEOProject.class);
                    EventBus.getDefault().post(new Events.ExecuteCodeEvent(neo.code));

                    res = newFixedLengthResponse("OK");
                } catch (IOException e) {
                    e.printStackTrace();
                    return newFixedLengthResponse("NOP");
                } catch (ResponseException e) {
                    e.printStackTrace();
                    return newFixedLengthResponse("NOP");
                }
            } else if (uriSplitted[COMMAND].equals("stop_all")) {
                // MLog.d(TAG, "stop all");
                EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_STOP_ALL, null));
                res = newFixedLengthResponse("OK");

                /*
                 * This is for the UI editor
                 */
            } else if (uriSplitted[COMMAND].equals("views_list_types")) {
                ArrayList<String> arrayList = new ArrayList();
                arrayList.add("button");
                arrayList.add("slider");
                arrayList.add("knob");
                String json = gson.toJson(arrayList);
                res = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_TYPES.get("json"), json);
            } else if (uriSplitted[COMMAND].equals("views_get_all")) {
                if (views == null) {
                    res = newFixedLengthResponse("NOP");
                } else {
                    res = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_TYPES.get("json"), views);
                }
            } else if (uriSplitted[COMMAND].equals("views_set_all")) {

            }


            /**
             * Project dependent actions
             *
             * /api/project/[ff]/[sf]/[p]/action
             */

        } else if (uriSplitted.length == 7) {
            Project p = new Project(uriSplitted[TYPE] + "/" + uriSplitted[FOLDER], uriSplitted[PROJECT_NAME]);

            if (uriSplitted[PROJECT_ACTION].equals("create")) {
                // MLog.d(TAG, "create project " + p.getFullPath() + " " + p.exists());

                if (!p.exists()) {
                    String template = "default";
                    // MLog.d(TAG, p.getParentPath());
                    PhonkScriptHelper.createNewProject(mContext.get(), template, p.getSandboxPathParent(), p.name);
                    res = newFixedLengthResponse("OK");
                    EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_NEW, p));
                }

            } else if (uriSplitted[PROJECT_ACTION].equals("save")) {
                // MLog.d(TAG, "project save");
                String json;
                final HashMap<String, String> map = new HashMap<String, String>();  // POST DATA

                try {
                    session.parseBody(map);
                    if (map.isEmpty()) return newFixedLengthResponse("BUG");

                    json = map.get("postData");
                    // MLog.d(TAG, "map " + map.toString());
                    // MLog.d(TAG, "post data " + json);
                } catch (IOException e) {
                    e.printStackTrace();
                    return newFixedLengthResponse("NOP");
                } catch (ResponseException e) {
                    e.printStackTrace();
                    return newFixedLengthResponse("NOP");
                }

                // MLog.d(TAG, json);
                NEOProject neo = gson.fromJson(json, NEOProject.class);

                // saving all the files changed
                for (ProtoFile file : neo.files) {
                    PhonkScriptHelper.saveCodeFromSandboxPath(p.getSandboxPath() + file.path, file.code);
                }

                res = newFixedLengthResponse("OK");

            } else if (uriSplitted[PROJECT_ACTION].equals("load")) {
                ArrayList<ProtoFile> files = PhonkScriptHelper.listFilesInProjectFolder(p, "/", 0);

                // only load main.js & app.conf
                for (int i = 0; i < files.size(); i++) {
                    if (files.get(i).name.equals(PhonkSettings.MAIN_FILENAME)) {
                        files.get(i).code = PhonkScriptHelper.getCode(p, files.get(i).name);
                    }
                }

                NEOProject neo = new NEOProject();
                neo.files = files;
                neo.project = p;
                neo.current_folder = "/";

                String json = gson.toJson(neo);

                EventBus.getDefault().post(new Events.HTTPServerEvent("project_load", p));
                res = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_TYPES.get("json"), json);

            } else if (uriSplitted[PROJECT_ACTION].equals("delete")) {
                String path = "";
                // PhonkScriptHelper.deleteFileOrFolder(path);

            } else if (uriSplitted[PROJECT_ACTION].equals("run")) {
                MLog.d(TAG, "run --> " + p.getFolder());
                EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_RUN, p));

                res = newFixedLengthResponse("OK");

            } else if (uriSplitted[PROJECT_ACTION].equals("stop_all_and_run")) {
                MLog.d(TAG, "stop_all_and_run --> ");

                EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_STOP_ALL_AND_RUN, p));

                res = newFixedLengthResponse("STOP_AND_RUN");
            } else if (uriSplitted[PROJECT_ACTION].equals("stop")) {
                MLog.d(TAG, "stop --> ");

                // MLog.d(TAG, "stop");
                EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_STOP_ALL, null));

                res = newFixedLengthResponse("OK");
            }
        } else if (uriSplitted.length >= 8 && uriSplitted[FILE_DELIMITER].equals("files")) {
            // MLog.d(TAG, "-> files ");
            Project p = new Project(uriSplitted[TYPE] + "/" + uriSplitted[FOLDER], uriSplitted[PROJECT_NAME]);
            if (uriSplitted[FILE_ACTION].equals("list")) {

                MLog.d(TAG, "list_files");
                String path = StringUtils.join(uriSplitted, "/", FILE_ACTION + 1, uriSplitted.length);
                MLog.d(TAG, "getting folder -> " + p.getSandboxPath() + "------" + path);

                ArrayList<ProtoFile> files = PhonkScriptHelper.listFilesInProjectFolder(p, path, 0);
                String jsonFiles = gson.toJson(files);

                MLog.d("list", ":/");

                MLog.d("list", jsonFiles);
                EventBus.getDefault().post(new Events.HTTPServerEvent("project_list_all"));

                res = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_TYPES.get("json"), jsonFiles);

            } else if (uriSplitted[FILE_ACTION].equals("view")) {
                String fileName = uriSplitted[FILE_NAME];

                String mime = getMimeType(fileName); // Get MIME type
                InputStream fi = null;
                try {
                    fi = new FileInputStream(p.getFullPathForFile(fileName));
                    res = newFixedLengthResponse(Response.Status.OK, mime, fi, fi.available());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (uriSplitted[FILE_ACTION].equals("load")) {
                // fetch file
                // String fileName = uriSplitted[FILE_NAME];

                String[] fileNameArray = Arrays.copyOfRange(uriSplitted, FILE_NAME, uriSplitted.length);
                String path = TextUtils.join(File.separator, fileNameArray);

                File file = new File(path);

                ProtoFile pFile = new ProtoFile(file.getName(), path);

                // MLog.d("qqqq1",  pFile.path);
                // MLog.d("qqqq1", pFile.name);
                pFile.code = PhonkScriptHelper.getCode(p, pFile.path);
                // MLog.d("qqqq1", pFile.code);

                NEOProject neo = new NEOProject();
                neo.files.add(pFile);

                String json = gson.toJson(neo);
                // MLog.d(TAG, json);

                // EventBus.getDefault().post(new Events.HTTPServerEvent("project_load", p));
                res = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_TYPES.get("json"), json);
            } else if (uriSplitted[FILE_ACTION].equals("create")) {
                final HashMap<String, String> map = new HashMap<String, String>();  // POST DATA

                String json = null;
                try {
                    session.parseBody(map);
                    if (map.isEmpty()) return newFixedLengthResponse("BUG");
                    json = map.get("postData");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ResponseException e) {
                    e.printStackTrace();
                }

                // MLog.d(TAG, json);
                NEOProject neo = gson.fromJson(json, NEOProject.class);

                for (ProtoFile file : neo.files) {
                    // MLog.d(TAG, "creating file in " + file.path + " " + file.name + " " + file.type);
                    String path = p.getFullPathForFile(file.path + File.separator + file.name);
                    // MLog.d(TAG, path);

                    if (!new File(path).exists()) {
                        switch (file.type) {
                            case "folder":
                                new File(path).mkdir();
                                break;
                            case "file":
                                try {
                                    new File(path).createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                        res = newFixedLengthResponse("OK");

                    } else {
                        res = newFixedLengthResponse("NOP");
                    }
                }

            } else if (uriSplitted[FILE_ACTION].equals("upload")) {
                // MLog.d(TAG, "upload for " + p.getFullPath());
                final HashMap<String, String> files = new HashMap<String, String>();  // POST DATA
                try {
                    // MLog.d(TAG, "parsing body");
                    session.parseBody(files);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ResponseException e) {
                    e.printStackTrace();
                }

                // get the file and copy it to the project folder
                String fileName = session.getParameters().get("name").get(0);
                String fileType = session.getParameters().get("type").get(0);
                File fileSrc = new File(files.get("file"));
                String toFolder = session.getParameters().get("toFolder").get(0);
                File fileDst = new File(p.getFullPathForFile(toFolder + File.separator + fileName));

                // MLog.d(TAG, fileSrc.getAbsolutePath());
                // MLog.d(TAG, fileDst.getAbsolutePath());

                // if (fileDst.exists()) {
                //    res = NanoHTTPD.newFixedLengthResponse(Response.Status.CONFLICT, MIME_PLAINTEXT, "File exist already");
                // }

                try {
                    FileIO.copyFile(fileSrc, fileDst);
                } catch (IOException e) {
                    // MLog.d(TAG, "qq5");
                    e.printStackTrace();
                }


                res = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_TYPES.get("txt"), fileName);

                // res = NanoHTTPD.newFixedLengthResponse("OK");

            } else if (uriSplitted[FILE_ACTION].equals("delete")) {
                final HashMap<String, String> map = new HashMap<String, String>();  // POST DATA

                String json = null;
                try {
                    session.parseBody(map);
                    if (map.isEmpty()) return newFixedLengthResponse("BUG");
                    json = map.get("postData");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ResponseException e) {
                    e.printStackTrace();
                }

                // MLog.d(TAG, json);

                NEOProject neo = gson.fromJson(json, NEOProject.class);

                for (ProtoFile file : neo.files) {
                    PhonkScriptHelper.deleteFileInProject(p, file.path);
                }

                // File fileDst = new File(p.getFullPathForFile(fileName));
                // PhonkScriptHelper.deleteFileOrFolder(fileDst.getAbsolutePath());
                res = NanoHTTPD.newFixedLengthResponse("OK");
            } else if (uriSplitted[FILE_ACTION].equals("move")) {
                // MLog.d(TAG, "move");
                final HashMap<String, String> map = new HashMap<String, String>();  // POST DATA

                String json = null;
                try {
                    session.parseBody(map);
                    if (map.isEmpty()) return newFixedLengthResponse("BUG");
                    json = map.get("postData");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ResponseException e) {
                    e.printStackTrace();
                }

                // MLog.d(TAG, json);

                NEOProject neo = gson.fromJson(json, NEOProject.class);

                for (ProtoFile file : neo.files) {
                    PhonkScriptHelper.moveFileFromTo(p, file.formerPath, file.path);
                }

                // File fileDst = new File(p.getFullPathForFile(fileName));
                // PhonkScriptHelper.deleteFileOrFolder(fileDst.getAbsolutePath());
                res = NanoHTTPD.newFixedLengthResponse("OK");
            } else {
                res = NanoHTTPD.newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, ":(");
            }
        }

        /*

            // get files
            ArrayList<ProtoFile> files = PhonkScriptHelper.listProjectsInFolder(p.getSandboxPath(), 0);

            for (int i = 0; i < files.size(); i++) {
                ProtoFile f = files.get(i);
                f.code = PhonkScriptHelper.getCode(p);
            }

            NEOProject neo = new NEOProject();
            neo.files = files;
            neo.project = p;

            String json = gson.toJson(neo);
            // MLog.d(TAG, json);

            */

        // EventBus.getDefault().post(new Events.HTTPServerEvent("project_list_files", p));
        // res = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_TYPES.get("json"), "" /* json.toString() */);

        // serve files

        return res;
    }

    private Response serveWebIDE(IHTTPSession session) {
        Response res = null;

        String uri = session.getUri();

        // Clean up uri
        uri = uri.trim().replace(File.separatorChar, '/');
        if (uri.indexOf('?') >= 0) uri = uri.substring(0, uri.indexOf('?'));
        if (uri.length() == 1) uri = "index.html"; // We never want to request just the '/'
        if (uri.charAt(0) == '/')
            uri = uri.substring(1); // using assets, so we can't have leading '/'

        String mime = getMimeType(uri); // Get MIME type

        // Read file and return it, otherwise NOT_FOUND is returned
        AssetManager am = mContext.get().getAssets();
        try {
            // MLog.d(TAG, WEBAPP_DIR + uri);
            InputStream fi = am.open(WEBAPP_DIR + uri);
            res = newFixedLengthResponse(Response.Status.OK, mime, fi, fi.available());
        } catch (IOException e) {
            e.printStackTrace();
            // MLog.d(TAG, e.getStackTrace().toString());
            NanoHTTPD.newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_TYPES.get("txt"), "ERROR: " + e.getMessage());
        }

        return res; //NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), inp, fontSize);
    }

    /*
     * MIME types
     */
    private static final Map<String, String> MIME_TYPES = new HashMap<String, String>() {
        {
            put("css", "text/css");
            put("htm", "text/html");
            put("html", "text/html");
            put("xml", "text/xml");
            put("txt", "text/plain");
            put("json", "application/json");
            put("asc", "text/plain");
            put("gif", "image/gif");
            put("jpg", "image/jpeg");
            put("jpeg", "image/jpeg");
            put("png", "image/png");
            put("mp3", "audio/mpeg");
            put("m3u", "audio/mpeg-url");
            put("mp4", "video/mp4");
            put("ogv", "video/ogg");
            put("flv", "video/x-flv");
            put("mov", "video/quicktime");
            put("swf", "application/x-shockwave-flash");
            put("js", "application/javascript");
            put("pdf", "application/pdf");
            put("doc", "application/msword");
            put("ogg", "application/x-ogg");
            put("binary", "application/octet-stream");
            put("zip", "application/octet-stream");
            put("exe", "application/octet-stream");
            put("class", "application/octet-stream");
        }
    };

    String getMimeType(String uri) {
        String mime = null;
        int dot = uri.lastIndexOf('.');
        if (dot >= 0) mime = MIME_TYPES.get(uri.substring(dot + 1).toLowerCase());
        if (mime == null) mime = MIME_TYPES.get("binary");

        return mime;
    }

    public void close() {
        stop();
    }

    public void setViews(String views) {
        this.views = views;
    }

    public void connectionCallback(ConnectionCallback connectionCallback) {
        mConnectionCallback = connectionCallback;
    }

    public interface ConnectionCallback {
        void event(String ip);
    }
}
