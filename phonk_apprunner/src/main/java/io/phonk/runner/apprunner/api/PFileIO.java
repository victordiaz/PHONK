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

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.FileObserver;

import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.api.other.PSqLite;
import io.phonk.runner.apprunner.interpreter.PhonkNativeArray;
import io.phonk.runner.apprunner.api.other.WhatIsRunningInterface;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apidoc.annotation.PhonkObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.FileIO;
import io.phonk.runner.base.utils.Image;

@PhonkObject
public class PFileIO extends ProtoBase {

    final private String TAG = PFileIO.class.getSimpleName();

    public PFileIO(AppRunner appRunner) {
        super(appRunner);
    }

    @PhonkMethod(description = "Move a file or directory", example = "")
    @PhonkMethodParam(params = {"name", "destination"})
    public boolean createFile(String name) {
        File file = new File(getAppRunner().getProject().getFullPathForFile(name));
        try {
            // file.mkdirs();
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @PhonkMethod(description = "Create a directory", example = "")
    @PhonkMethodParam(params = {"dirName"})
    public boolean createFolder(String name) {
        File file = new File(getAppRunner().getProject().getFullPathForFile(name));
        return file.mkdirs();
    }

    @PhonkMethod(description = "Delete a filename", example = "")
    @PhonkMethodParam(params = {"fileName"})
    public void delete(String name) {
        FileIO.deleteFileDir(getAppRunner().getProject().getFullPathForFile(name));
    }

    public void deleteAsync(final String name, final ReturnInterface callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                delete(name);
                ReturnObject ret = new ReturnObject();
                ret.put("file", name);
                returnValues(ret, callback);
            }
        });
        t.start();
    }

    @PhonkMethod(description = "Get 1 is is a file, 2 if is a directory and -1 if the file doesnt exists", example = "")
    @PhonkMethodParam(params = {"fileName"})
    public String type(String name) {
        String ret = "none";

        File file = new File(getAppRunner().getProject().getFullPathForFile(name));

        if (!file.exists()) ret = "none";
        else if (file.isFile()) ret = "file";
        else if (file.isDirectory()) ret = "folder";

        return ret;
    }

    @PhonkMethod(description = "Move a file to a directory", example = "")
    @PhonkMethodParam(params = {"name", "destination"})
    public void move(String name, String to) {
        File fromFile = new File(getAppRunner().getProject().getFullPathForFile(name));
        File dir = new File(getAppRunner().getProject().getFullPathForFile(to));

        dir.mkdirs();
        try {
            FileUtils.moveFileToDirectory(fromFile, dir, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveAsync(final String name, final String to, final ReturnInterface callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                move(name, to);
                ReturnObject ret = new ReturnObject();
                ret.put("file", name);
                returnValues(ret, callback);
            }
        });
        t.start();
    }

    /*
    @ProtoMethod(description = "Move a directory to another directory", example = "")
    @ProtoMethodParam(params = {"name", "destination"})
    public void moveDirToDir(String name, String to) {
        File fromDir = new File(getAppRunner().getProject().getFullPathForFile(name));
        File dir = new File(getAppRunner().getProject().getFullPathForFile(to));

        dir.mkdirs();
        try {
            FileUtils.moveDirectoryToDirectory(fromDir, dir, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

    @PhonkMethod(description = "Copy a file or directory", example = "")
    @PhonkMethodParam(params = {"name", "destination"})
    public void copy(String name, String to) {
        File file = new File(getAppRunner().getProject().getFullPathForFile(name));
        File dir = new File(getAppRunner().getProject().getFullPathForFile(to));
        dir.mkdirs();

        try {
            FileUtils.copyFileToDirectory(file, dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyAsync(String from, String to, ReturnInterface callback) {

    }

    /*
    @ProtoMethod(description = "Copy a file or directory", example = "")
    @ProtoMethodParam(params = {"name", "destination"})
    public void copyDirToDir(String name, String to) {
        File file = new File(getAppRunner().getProject().getFullPathForFile(name));
        File dir = new File(getAppRunner().getProject().getFullPathForFile(to));
        dir.mkdirs();

        try {
            FileUtils.copyDirectoryToDirectory(file, dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

    @PhonkMethod(description = "Rename a file or directory", example = "")
    @PhonkMethodParam(params = {"name", "destination"})
    public void rename(String oldName, String newName) {
        //File file = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + name);
        //file.mkdirs();

        File origin = new File(getAppRunner().getProject().getFullPathForFile(oldName));
        String path = origin.getParentFile().toString();

        origin.renameTo(new File(path + File.separator + newName));
    }

    @PhonkMethod(description = "Save an array with text into a file", example = "")
    @PhonkMethodParam(params = {"fileName", "lines[]"})
    public void saveTextToFile(String lines, String fileName) {
        FileIO.saveStringToFile(lines, getAppRunner().getProject().getFullPathForFile(fileName));
    }

    public void saveTextToFileAsync(final String lines, final String fileName, final ReturnInterface callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                saveTextToFile(lines, fileName);
                ReturnObject ret = new ReturnObject();
                ret.put("file", fileName);
                returnValues(ret, callback);
            }
        });
        t.start();
    }

    @PhonkMethod(description = "Append an array of text into a file", example = "")
    @PhonkMethodParam(params = {"fileName", "lines[]"})
    public void appendTextToFile(String line, String fileName) {
        FileIO.appendString(getAppRunner().getProject().getFullPathForFile(fileName), line);
    }


    @PhonkMethod(description = "Append an array of text into a file", example = "")
    @PhonkMethodParam(params = {"fileName", "lines[]"})
    public void appendTextToFileAsync(final String line, final String fileName, final ReturnInterface callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                appendTextToFile(line, fileName);
                returnValues(null, callback);
            }
        });
    }



    @PhonkMethod(description = "Load the Strings of a text file into an array", example = "")
    @PhonkMethodParam(params = {"fileName"})
    public String loadTextFromFile(String fileName) {
        return FileIO.loadStringFromFile(getAppRunner().getProject().getFullPathForFile(fileName));
    }

    public void loadTextFromFileAsync(final String fileName, final ReturnInterface callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String content = loadTextFromFile(fileName);
                final ReturnObject ret = new ReturnObject();
                ret.put("file", fileName);
                ret.put("content", content);
                returnValues(ret, callback);
            }
        });
        t.start();
    }

    @PhonkMethod(description = "Loads a font", example = "")
    @PhonkMethodParam(params = {"fontFile"})
    public Typeface loadFont(String fontName) {
        return Typeface.createFromFile(getAppRunner().getProject().getFullPathForFile(fontName));
    }

    @PhonkMethod(description = "Loads a bitmap", example = "")
    public Bitmap loadBitmap(String path) {
        return Image.loadBitmap(getAppRunner().getProject().getFullPathForFile(path));
    }

    public void loadBitmapAsync(final String path, final ReturnInterface callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                loadBitmap(path);
                returnValues(null, callback);
            }
        });
        t.start();
    }

    @PhonkMethod(description = "List all the files in the directory", example = "")
    @PhonkMethodParam(params = {"path"})
    public PhonkNativeArray list(String path) {
        return list(path, "");
    }

    @PhonkMethod(description = "List all the files with a given extension", example = "")
    @PhonkMethodParam(params = {"fileName"})
    public PhonkNativeArray list(String path, String filter) {
        File[] files = FileIO.listFiles(getAppRunner().getProject().getFullPathForFile(path), filter);

        PhonkNativeArray nativeArray = new PhonkNativeArray(files.length);
        for (int i = 0; i < files.length; i++) {
            nativeArray.addPE(i, files[i].getName());
        }

        return nativeArray;
    }

    public long size(String path) {
        File f = new File(getAppRunner().getProject().getFullPathForFile(path));
        return f.length();
    }

    @PhonkMethod(description = "Open a sqlite database", example = "")
    @PhonkMethodParam(params = {"filename"})
    public PSqLite openSqlLite(String db) {
        return new PSqLite(getAppRunner(), db);
    }


    @PhonkMethod(description = "Zip a file/folder into a zip", example = "")
    @PhonkMethodParam(params = {"folder", "filename"})
    public void zip(final String fOrigin, final String fDestiny, final ReturnInterface callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileIO.zipFolder(getAppRunner().getProject().getFullPathForFile(fOrigin), getAppRunner().getProject().getFullPathForFile(fDestiny));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                returnValues(null, callback);
            }
        });
        t.start();

    }

    @PhonkMethod(description = "Unzip a file into a folder", example = "")
    @PhonkMethodParam(params = {"zipFile", "folder"})
    public void unzip(final String src, final String dst, final ReturnInterface callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileIO.unZipFile(getAppRunner().getProject().getFullPathForFile(src), getAppRunner().getProject().getFullPathForFile(dst));
                    returnValues(null, callback);
                } catch (ZipException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    class PFileObserver implements WhatIsRunningInterface {
        private ReturnInterface callback;
        private FileObserver fileObserver;

        PFileObserver(AppRunner appRunner, String path) {
            fileObserver = new FileObserver(appRunner.getProject().getFullPathForFile(path), FileObserver.CREATE | FileObserver.MODIFY | FileObserver.DELETE) {

                @Override
                public void onEvent(int event, String file) {
                    ReturnObject ret = new ReturnObject();
                    if ((FileObserver.CREATE & event) != 0) {
                        ret.put("action", "created");
                    } else if ((FileObserver.DELETE & event) != 0) {
                        ret.put("action", "deleted");
                    } else if ((FileObserver.MODIFY & event) != 0) {
                        ret.put("action", "modified");
                    }
                    ret.put("file", file);
                    if (callback != null) callback.event(ret);
                }

            };
            fileObserver.startWatching();
            getAppRunner().whatIsRunning.add(this);
        }

        public PFileObserver onFileChanged(ReturnInterface callback) {
            this.callback = callback;

            return this;
        }

        public void start() {
            fileObserver.startWatching();
        }

        public void stop() {
            fileObserver.stopWatching();
        }

        @Override
        public void __stop() {
            if (fileObserver != null) {
                fileObserver.stopWatching();
                fileObserver = null;
            }
        }
    }

    @PhonkMethod(description = "Observer file changes in a folder", example = "")
    @PhonkMethodParam(params = {"path", "function(action, file"})
    public PFileObserver createFileObserver(String path) {
        PFileObserver pFileObserver = new PFileObserver(getAppRunner(), path);

        return pFileObserver;
    }

    private void returnValues(final ReturnObject ret, final ReturnInterface callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.event(ret);
            }
        });
    }

    @Override
    public void __stop() {

    }

}
