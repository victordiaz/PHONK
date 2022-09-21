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
package io.phonk.runner.apprunner.api.network;

import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.base.utils.MLog;

@PhonkClass
public class PFtpServer {
    final String TAG = PFtpServer.class.getSimpleName();

    private final int mPort;
    private final FtpServerCb mCallback;

    final PropertiesUserManagerFactory userManagerFactory;
    final UserManager um;
    org.apache.ftpserver.FtpServer server;


    public interface FtpServerCb {
        void event(String status);
    }

    public PFtpServer(int port, FtpServerCb callback) {
        mCallback = callback;
        mPort = port;

        userManagerFactory = new PropertiesUserManagerFactory();
        //userManagerFactory.setAdminName(mUserName);
        userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());

        um = userManagerFactory.createUserManager();
    }


    //we have to pass the protocoder project folder
    public void addUser(String name, String pass, String directory, boolean canWrite) {
        BaseUser user = new BaseUser();
        user.setName(name);
        user.setPassword(pass);

        //String root = ProjectManager.getInstance().getCurrentProject().getStoragePath() + "/" + directory;
        user.setHomeDirectory(directory);

        //check if user can write
        if (canWrite) {
            List<Authority> auths = new ArrayList<>();
            Authority auth = new WritePermission();
            auths.add(auth);
            user.setAuthorities(auths);
        }

        try {
            um.save(user);
        } catch (FtpException e) {
            e.printStackTrace();
        }

    }


    public void start() {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(mPort);

        // replace the default listener
        serverFactory.addListener("default", factory.createListener());
        serverFactory.setUserManager(um);

        Map ftpLets = new HashMap<>();
        ftpLets.put("ftpLet", new CallbackFTP(mCallback));


        serverFactory.setFtplets(ftpLets);

        // start the server
        try {
            server = serverFactory.createServer();
            server.start();
            MLog.d(TAG, "server started");
        } catch (FtpException e) {
            e.printStackTrace();
            MLog.d(TAG, "server not started");
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            MLog.d(TAG, e.toString());
        }
    }

    private static class CallbackFTP extends DefaultFtplet {
        final FtpServerCb callback;

        CallbackFTP(FtpServerCb callback) {
            this.callback = callback;
        }

        @Override
        public void init(FtpletContext ftpletContext) {

        }

        @Override
        public void destroy() {

        }

        @Override
        public FtpletResult beforeCommand(FtpSession ftpSession, FtpRequest ftpRequest) throws IOException {
            if (callback != null)
                callback.event("Requested command: " + ftpRequest.getCommand() + " " + ftpRequest.getArgument() + " " + ftpRequest.getRequestLine());
            return FtpletResult.DEFAULT;
        }

        @Override
        public FtpletResult afterCommand(FtpSession ftpSession, FtpRequest ftpRequest, FtpReply ftpReply) throws IOException {
            return null;
        }

        @Override
        public FtpletResult onConnect(FtpSession ftpSession) throws IOException {
            if (callback != null)
                callback.event("Connected from " + ftpSession.getClientAddress());
            return FtpletResult.DEFAULT;
        }

        @Override
        public FtpletResult onLogin(FtpSession session, FtpRequest request) throws IOException {
            if (callback != null)
                callback.event("Logged in: " + session.getUser().getName());
            return FtpletResult.DEFAULT;
        }

        @Override
        public FtpletResult onDisconnect(FtpSession ftpSession) throws IOException {
            if (callback != null)
                callback.event("Disconnected: " + ftpSession.getUser().getName());

            return FtpletResult.DEFAULT;
        }
    }
}
