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

package io.phonk.runner.api;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.FileObserver;

import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;

import io.phonk.runner.api.common.ReturnInterface;
import io.phonk.runner.api.common.ReturnObject;
import io.phonk.runner.api.other.PSqLite;
import io.phonk.runner.api.other.PhonkNativeArray;
import io.phonk.runner.api.other.WhatIsRunningInterface;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apidoc.annotation.ProtoObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.FileIO;
import io.phonk.runner.base.utils.Image;

import java.io.File;
import java.io.IOException;

@ProtoObject
public class PFileIO extends ProtoBase {

    final private String TAG = PFileIO.class.getSimpleName();

    public PFileIO(AppRunner appRunner) {
        super(appRunner);
    }

    @ProtoMethod(description = "Move a file or directory", example = "")
    @ProtoMethodParam(params = {"name", "destination"})
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

    @ProtoMethod(description = "Create a directory", example = "")
    @ProtoMethodParam(params = {"dirName"})
    public boolean createFolder(String name) {
        File file = new File(getAppRunner().getProject().getFullPathForFile(name));
        return file.mkdirs();
    }

    @ProtoMethod(description = "Delete a filename", example = "")
    @ProtoMethodParam(params = {"fileName"})
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

    @ProtoMethod(description = "Get 1 is is a file, 2 if is a directory and -1 if the file doesnt exists", example = "")
    @ProtoMethodParam(params = {"fileName"})
    public String type(String name) {
        String ret = "none";

        File file = new File(getAppRunner().getProject().getFullPathForFile(name));

        if (!file.exists()) ret = "none";
        else if (file.isFile()) ret = "file";
        else if (file.isDirectory()) ret = "folder";

        return ret;
    }

    @ProtoMethod(description = "Move a file to a directory", example = "")
    @ProtoMethodParam(params = {"name", "destination"})
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

    @ProtoMethod(description = "Copy a file or directory", example = "")
    @ProtoMethodParam(params = {"name", "destination"})
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

    @ProtoMethod(description = "Rename a file or directory", example = "")
    @ProtoMethodParam(params = {"name", "destination"})
    public void rename(String oldName, String newName) {
        //File file = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + name);
        //file.mkdirs();

        File origin = new File(getAppRunner().getProject().getFullPathForFile(oldName));
        String path = origin.getParentFile().toString();

        origin.renameTo(new File(path + File.separator + newName));
    }

    @ProtoMethod(description = "Save an array with text into a file", example = "")
    @ProtoMethodParam(params = {"fileName", "lines[]"})
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

    @ProtoMethod(description = "Append an array of text into a file", example = "")
    @ProtoMethodParam(params = {"fileName", "lines[]"})
    public void appendTextToFile(String line, String fileName) {
        FileIO.appendString(getAppRunner().getProject().getFullPathForFile(fileName), line);
    }


    @ProtoMethod(description = "Append an array of text into a file", example = "")
    @ProtoMethodParam(params = {"fileName", "lines[]"})
    public void appendTextToFileAsync(final String line, final String fileName, final ReturnInterface callback) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                appendTextToFile(line, fileName);
                returnValues(null, callback);
            }
        });
    }



    @ProtoMethod(description = "Load the Strings of a text file into an array", example = "")
    @ProtoMethodParam(params = {"fileName"})
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

    @ProtoMethod(description = "Loads a font", example = "")
    @ProtoMethodParam(params = {"fontFile"})
    public Typeface loadFont(String fontName) {
        return Typeface.createFromFile(getAppRunner().getProject().getFullPathForFile(fontName));
    }

    @ProtoMethod(description = "Loads a bitmap", example = "")
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

    @ProtoMethod(description = "List all the files in the directory", example = "")
    @ProtoMethodParam(params = {"path"})
    public PhonkNativeArray list(String path) {
        return list(path, "");
    }

    @ProtoMethod(description = "List all the files with a given extension", example = "")
    @ProtoMethodParam(params = {"fileName"})
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

    @ProtoMethod(description = "Open a sqlite database", example = "")
    @ProtoMethodParam(params = {"filename"})
    public PSqLite openSqlLite(String db) {
        return new PSqLite(getAppRunner(), db);
    }


    @ProtoMethod(description = "Zip a file/folder into a zip", example = "")
    @ProtoMethodParam(params = {"folder", "filename"})
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

    @ProtoMethod(description = "Unzip a file into a folder", example = "")
    @ProtoMethodParam(params = {"zipFile", "folder"})
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

    @ProtoMethod(description = "Observer file changes in a folder", example = "")
    @ProtoMethodParam(params = {"path", "function(action, file"})
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
