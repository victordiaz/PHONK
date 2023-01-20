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

package io.phonk.runner.base.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.storage.StorageManager;
import android.util.Log;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileIO {
    private static final String TAG = FileIO.class.getSimpleName();

    public static boolean copyAssetFolder(AssetManager assetManager, String fromAssetPath, String toPath) {
        try {
            String[] files = assetManager.list(fromAssetPath);
            new File(toPath).mkdirs();
            boolean res = true;
            for (String file : files) {
                if (file.contains(".")) {
                    res &= copyAsset(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
                } else {
                    res &= copyAssetFolder(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
                }
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean copyAsset(AssetManager assetManager, String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetPath);
            new File(toPath).createNewFile();
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }


    public static void deleteFileDir(String path) {
        // MLog.d(TAG, "deleting directory " + path);
        File dir = new File(path);

        if (dir.isDirectory()) {
            try {
                FileUtils.deleteDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            dir.delete();
        }
        MLog.d(TAG, "deleting directory done" + path);
    }

    public static void deleteDir(File dir) {
        // MLog.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.getPath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String element : children) {
                File temp = new File(dir, element);
                if (temp.isDirectory()) {
                    // MLog.d("DeleteRecursive", "Recursive Call" + temp.getPath());
                    deleteDir(temp);
                } else {
                    // MLog.d("DeleteRecursive", "Delete File" + temp.getPath());
                    boolean b = temp.delete();
                    if (!b) {
                        // MLog.d("DeleteRecursive", "DELETE FAIL");
                    }
                }
            }

        }
        dir.delete();
    }

    public static void copyFile(File src, File dst) throws IOException {
        try (FileChannel inChannel = new FileInputStream(src).getChannel(); FileChannel outChannel =
                new FileOutputStream(
                dst).getChannel()) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
    }

    public static String loadStringFromFile(String path) {
        String out = null;
        File f = new File(path);

        try {
            InputStream in = new FileInputStream(f);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int i;
            try {
                i = in.read();
                while (i != -1) {
                    buf.write(i);
                    i = in.read();
                }

                in.close();
            } catch (IOException ignored) {
            }
            out = buf.toString();
        } catch (IOException ignored) {
        }
        return out;
    }

    static public void zipFolder(String src, String dst) throws Exception {
        File f = new File(dst);
        //make dirs if necessary
        f.getParentFile().mkdirs();

        ZipFile zipfile = new ZipFile(f.getAbsolutePath());
        ZipParameters parameters = new ZipParameters();
        parameters.setIncludeRootFolder(false);
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        zipfile.addFolder(src, parameters);
    }

    static public void unZipFile(String src, String dst) throws ZipException {
        ZipFile zipFile = new ZipFile(src);
        zipFile.extractAll(dst);
    }

    static public List seeZipContent(String file) throws ZipException {
        ZipFile zipFile = new ZipFile(file);
        return zipFile.getFileHeaders();
    }

    static public void extractZip(String zipFile, String location) throws IOException {
        int size;
        int BUFFER_SIZE = 1024;

        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            if (!location.endsWith("/")) {
                location += "/";
            }
            File f = new File(location);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
            try (ZipInputStream zin = new ZipInputStream(new BufferedInputStream(
                    new FileInputStream(zipFile),
                    BUFFER_SIZE
            ))) {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();
                    File unzipFile = new File(path);

                    if (ze.isDirectory()) {
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        // check for and create parent directories if they don't exists
                        File parentDir = unzipFile.getParentFile();
                        if (null != parentDir) {
                            if (!parentDir.isDirectory()) {
                                parentDir.mkdirs();
                            }
                        }

                        // unzip the file
                        FileOutputStream out = new FileOutputStream(unzipFile, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                        try {
                            while ((size = zin.read(buffer, 0, BUFFER_SIZE)) != -1) {
                                fout.write(buffer, 0, size);
                            }

                            zin.closeEntry();
                        } finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Unzip exception", e);
        }
    }

    public static File[] listFiles(String path, final String extension) {
        File f = new File(path);

        return f.listFiles((dir, fileName) -> fileName.endsWith(extension));
    }

    public static String getFileExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }

        return extension;
    }

    public static void appendString(String fileUrl, String line) {
        try {
            File f = new File(fileUrl);
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fo = new FileOutputStream(f, true);
            fo.write(line.getBytes());
            fo.flush();
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String[] listFilesInAssets(Context c, String path) {
        String[] ret = null;
        try {
            ret = c.getAssets().list(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }


    public static void saveStringToFile(String code, String filepath) {
        File f = new File(filepath);

        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fo = new FileOutputStream(f);
            byte[] data = code.getBytes();
            fo.write(data);
            fo.flush();
            fo.close();

        } catch (FileNotFoundException ex) {
            MLog.e(TAG, ex.toString());
        } catch (IOException e) {
            e.printStackTrace();
            // Log.e("Project", e.toString());
        }
    }

    public static void getDiskSpaceAvailability(Context c) {
        StorageManager storageManager = c.getApplicationContext().getSystemService(StorageManager.class);
        UUID appSpecificInternalDirUuid = null;
        try {
            appSpecificInternalDirUuid = storageManager.getUuidForPath(c.getFilesDir());
            long availableBytes = storageManager.getAllocatableBytes(appSpecificInternalDirUuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
