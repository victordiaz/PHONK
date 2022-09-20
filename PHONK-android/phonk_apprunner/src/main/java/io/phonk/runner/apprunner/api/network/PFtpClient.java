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

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;
import io.phonk.runner.base.utils.MLog;

//current source :http://androiddev.orkitra.com/?p=28

@PhonkClass
public class PFtpClient extends ProtoBase {
    final String TAG = PFtpClient.class.getSimpleName();

    public static String workDir;

    private FTPClient mFTPClient;
    Boolean isConnected = false;

    public PFtpClient(AppRunner appRunner) {
        super(appRunner);
    }

    public interface FtpConnectedCb {
        void event(boolean connected);
    }


    @PhonkMethod(description = "Connect to a ftp server", example = "")
    @PhonkMethodParam(params = {"host", "port", "username", "password", "function(connected)"})
    public void connect(final String host, final int port, final String username, final String password, final FtpConnectedCb callback) {
        mFTPClient = new FTPClient();

        Thread t = new Thread(() -> {
            try {
                mFTPClient.connect(host, port);

                MLog.d(TAG, "1");

                if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                    boolean logged = mFTPClient.login(username, password);
                    mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                    mFTPClient.enterLocalPassiveMode();
                    isConnected = logged;

                    callback.event(logged);
                }
                MLog.d(TAG, "" + isConnected);

            } catch (Exception e) {
                MLog.d(TAG, "connection failed error:" + e);
            }
        });
        t.start();
    }

    public interface GetCurrentDirCb {
        void event(String msg);
    }


    @PhonkMethod(description = "Get the current directory", example = "")
    @PhonkMethodParam(params = {""})
    public void getCurrentDir(final GetCurrentDirCb callback) {

        Thread t = new Thread(() -> {
            try {
                MLog.d(TAG, "" + mFTPClient);
                String workingDir = mFTPClient.printWorkingDirectory();
                callback.event(workingDir);
            } catch (Exception e) {
                MLog.d(TAG, "Error: could not get current working directory. " + e);
            }

        });
        t.start();

    }

    public interface ChangeDirectoryCb {
        void event(boolean msg);
    }


    @PhonkMethod(description = "Change the directory", example = "")
    @PhonkMethodParam(params = {"dirname"})
    public void changeDir(final String directory_path, final ChangeDirectoryCb callback) {
        Thread t = new Thread(() -> {
            try {
                callback.event(mFTPClient.changeWorkingDirectory(directory_path));
            } catch (Exception e) {
                MLog.d(TAG, "Error:" + e);
            }
        });
        t.start();
    }


    public interface GetFileListCb {
        void event(ArrayList<ListDir> msg);
    }

    static class ListDir {
        public String type;
        public String name;
    }


    @PhonkMethod(description = "Get list of files in the given dir", example = "")
    @PhonkMethodParam(params = {"dirname"})
    public void getFileList(final String dir_path, final GetFileListCb callback) {
        Thread t = new Thread(() -> {
            ArrayList<ListDir> list = new ArrayList<>();
            try {
                FTPFile[] ftpFiles = mFTPClient.listFiles(dir_path);
                int length = ftpFiles.length;

                for (FTPFile ftpFile : ftpFiles) {
                    String name = ftpFile.getName();
                    boolean isFile = ftpFile.isFile();

                    ListDir listDir = new ListDir();
                    if (isFile) {
                        listDir.type = "file";
                    } else {
                        listDir.type = "dir";
                    }
                    listDir.name = name;
                    list.add(listDir);
                }
                callback.event(list);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    public interface DownloadFiletCb {
        void event(boolean msg);
    }


    @PhonkMethod(description = "Download the file", example = "")
    @PhonkMethodParam(params = {"sourceFilePath", "destinyFilePath"})
    public void download(final String srcFilePath, final String destiny, final DownloadFiletCb callback) {
        Thread t = new Thread(() -> {
            boolean status = false;
            //TODO reenable this
            String desFilePath = null; //ProjectManager.getInstance().getCurrentProject().getStoragePath() + "/" + destiny;

            try {
                FileOutputStream desFileStream = new FileOutputStream(desFilePath);

                status = mFTPClient.retrieveFile(srcFilePath, desFileStream);
                callback.event(status);

                desFileStream.close();
            } catch (Exception e) {
                MLog.d(TAG, "download failed error:" + e);
            }
        });
        t.start();
    }

    public interface UploadCb {
        void event(boolean msg);
    }


    @PhonkMethod(description = "Upload a file", example = "")
    @PhonkMethodParam(params = {"sourceFilePath", "fileName", "destinyPath"})
    public void upload(final String source, final String desFileName, String desDirectory, final UploadCb callback) {
        boolean status = false;

        //TODO reenable this
        final String srcFilePath = null; //ProjectManager.getInstance().getCurrentProject().getStoragePath() + "/" + source;

        Thread t = new Thread(() -> {
            while (!isConnected) {
                try {
                    MLog.d(TAG, "waiting for connection");
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                FileInputStream srcFileStream = new FileInputStream(srcFilePath);
                boolean status1 = mFTPClient.storeFile(desFileName, srcFileStream);
                srcFileStream.close();
                callback.event(status1);
            } catch (Exception e) {
                MLog.d(TAG, "upload failed" + e);
            }
        });
        t.start();

    }

    public interface DeleteFileCb {
        void event(boolean msg);
    }


    @PhonkMethod(description = "Delete a file", example = "")
    @PhonkMethodParam(params = {"filename", "function(boolean)"})
    public void deleteFile(final String filename, final DeleteFileCb callback) {
        Thread t = new Thread(() -> {
            if (isConnected) {
                try {
                    boolean deleted = mFTPClient.deleteFile(filename);
                    callback.event(deleted);
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.event(false);
                }
            }
        });
        t.start();
    }

    public interface DisconnectCb {
        void event(Boolean msg);
    }


    @PhonkMethod(description = "Disconnect from server", example = "")
    @PhonkMethodParam(params = {""})
    public void disconnect(final DisconnectCb callback) {
        Thread t = new Thread(() -> {
            MLog.d(TAG, "" + isConnected);
            if (isConnected) {
                try {
                    mFTPClient.logout();
                    mFTPClient.disconnect();
                } catch (Exception ignored) {

                }
                isConnected = false;

                if (callback != null)
                    callback.event(isConnected);
            }
        });
        t.start();
    }

    @Override
    public void __stop() {
        disconnect(null);
    }
}
