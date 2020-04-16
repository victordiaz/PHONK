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

package io.phonk.helpers;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.phonk.App;
import io.phonk.R;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.runner.AppRunnerActivity;
import io.phonk.runner.base.models.Folder;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.utils.FileIO;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.base.utils.TimeUtils;
import io.phonk.server.model.ProtoFile;

public class PhonkScriptHelper {

    private static final String TAG = PhonkScriptHelper.class.getSimpleName();

    private static String getBaseDir() {
        String baseDir;

        baseDir = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + PhonkSettings.PHONK_FOLDER + File.separator;

        return baseDir;
    }

    public static String getExportFolderPath() {
        File p = new File(getProjectFolderPath(PhonkSettings.EXPORTED_FOLDER));
        p.mkdirs();

        return p.getAbsolutePath();
    }

    //
    public static String getProjectFolderPath(String folder) {
        return getBaseDir() + folder;
    }

    public static String[] listTemplates(Context c) {
        return FileIO.listFilesInAssets(c, PhonkSettings.TEMPLATES_FOLDER);
    }

    // Create Project
    public static Project createNewProject(Context c, String template, String where, String newProjectName) {
        Project newProject = null;
        String fullPath = PhonkScriptHelper.getProjectFolderPath(where + newProjectName);
        MLog.d(TAG, "--> " + fullPath);

        // check if is existing
        if (new File(fullPath).exists()) {
            MLog.d(TAG, "Project already exists");
            return newProject;
        } else {
            FileIO.copyAssetFolder(c.getAssets(), PhonkSettings.TEMPLATES_FOLDER + "/" + template, fullPath);
            MLog.d(TAG, "creating project in " + where);

            newProject = new Project(where, newProjectName);
        }


        return newProject;
    }

    public static void deleteFileInProject(Project p, String path) {
        deleteFileOrFolder(p.getFullPathForFile(path));
    }

    // Delete Project
    public static void deleteFileOrFolder(String path) {
        File dir = new File(path);

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String element : children) {
                new File(dir, element).delete();
            }
        }
        dir.delete();
    }

    // Write a file with code
    public static void saveCodeFromSandboxPath(String relativePath, String code) {
        String absolutePath = getAbsolutePathFromRelative(relativePath);
        saveCodeFromAbsolutePath(absolutePath, code);
    }

    public static void saveCodeFromAbsolutePath(String filepath, String code) {
        FileIO.saveStringToFile(code, filepath);
    }

    // Get code from sdcard
    public static String getCode(Project p) {
        return getCode(p, PhonkSettings.MAIN_FILENAME);
    }

    public static String getCode(Project p, String name) {
        String path = p.getFullPath() + name;

        return FileIO.loadStringFromFile(path);
    }

    public static ArrayList<Folder> listFoldersOrdered(String folder) {
        return listFolders(folder, true);
    }

    // List folders
    public static ArrayList<Folder> listFolders(String folder, boolean orderByName) {
        ArrayList<Folder> folders = new ArrayList<Folder>();
        File dir = new File(PhonkSettings.getFolderPath(folder));
        MLog.d(TAG, "nn1 path ->" + folder + " " + dir.getAbsolutePath() + " " + dir.exists());

        if (!dir.exists()) {
            dir.mkdir();
        }

        File[] all_projects = dir.listFiles();
        MLog.d(TAG, "nn1 1" + all_projects);

        // if folder or not existing is empty return
        if (all_projects == null) return folders;

        // order projects
        if (orderByName) {
            Arrays.sort(all_projects);
        }

        MLog.d(TAG, "nn1 2");
        // create returning structure
        for (File file : all_projects) {
            String folderUrl = file.getAbsolutePath();
            String projectName = file.getName();

            MLog.d("nn1 3", folderUrl + " " + " " + folder + " " + projectName);
            folders.add(new Folder(folderUrl, folder, projectName));
        }

        return folders;
    }


    public static ArrayList<ProtoFile> listFilesForFileManager(String folder) {
        ArrayList<ProtoFile> protoFiles = new ArrayList();

        File[] files = new File(folder).listFiles();

        for (File f : files) {
            ProtoFile protoFile = new ProtoFile();
            protoFile.name = f.getName();
            protoFile.path = f.getParent() + File.separator;
            protoFile.size = f.length();
            protoFile.type = f.isDirectory() ? "folder" : "file";
            protoFile.isDir = f.isDirectory();
            protoFiles.add(protoFile);
        }

        Collections.sort(protoFiles, (l, r) -> {

            // order by folder first and alphabetically

            if (l.isDir && !r.isDir){
                return -1;
            } else if (!l.isDir && r.isDir){
                return 1;
            } else {
                return l.name.compareToIgnoreCase(r.name);
            }

        });

        return protoFiles;
    }



    // List folders in a tree structure
    public static ArrayList<ProtoFile> listFilesInProjectFolder(Project p, String folder, int levels) {
        ArrayList<ProtoFile> files = listFilesInFolder(p.getSandboxPath() + folder, levels, "*");

        // remove the project folder
        for (ProtoFile file : files) {
            String[] splittedPath = file.path.split(p.getSandboxPath());
            file.path = file.path.replace(p.getSandboxPath(), "");
        }

        return files;
    }

    // List folders in a tree structure
    public static ArrayList<ProtoFile> listProjectsInFolder(String folder, int levels) {
        ArrayList<ProtoFile> files = listFilesInFolder(folder, levels, "*");

        return files;
    }

    public static ArrayList<ProtoFile> listFilesInFolder(String folder, int levels, String extensionFilter) {
        ArrayList<ProtoFile> foldersArray = new ArrayList<ProtoFile>();
        File dir = new File(PhonkSettings.getFolderPath(folder));

        fileWalker(foldersArray, dir, levels, extensionFilter);

        return foldersArray;
    }

    private static void fileWalker(ArrayList<ProtoFile> tree, File dir, int levels, final String extensionFilter) {
        File[] all_projects = dir.listFiles(pathname -> {
            if (extensionFilter == "*") return true;

            return pathname.getName().endsWith(extensionFilter);
        });

        for (File f : all_projects) {

            ProtoFile protoFile = new ProtoFile();
            // MLog.d( TAG, f.getName() + " is a dir " + f.isDirectory() );

            protoFile.isDir = f.isDirectory();
            if (f.isDirectory()) {
                protoFile.type = "folder";
                protoFile.files = new ArrayList<ProtoFile>();
            } else {
                protoFile.type = "file";
                protoFile.size = f.length();
            }
            protoFile.name = f.getName();
            protoFile.path = PhonkScriptHelper.getRelativePathFromAbsolute(f.getAbsolutePath());

            if (f.isDirectory() && levels > 0) fileWalker(protoFile.files, f, levels - 1, extensionFilter);

            tree.add(protoFile);
        }
    }

    public static String getRelativePathFromAbsolute(String path) {
        // get relative uri
        String[] splitted = path.split(PhonkSettings.getBaseDir());
        return splitted[1];
    }

    public static String getAbsolutePathFromRelative(String relativePath) {
        String absolutePath = PhonkSettings.getBaseDir() + relativePath;
        return absolutePath;
    }

    // List projects
    public static ArrayList<Project> listProjects(String folder, boolean orderByName) {
        ArrayList<Project> projects = new ArrayList<Project>();
        File dir = new File(PhonkSettings.getFolderPath(folder));

        if (!dir.exists()) {
            dir.mkdir();
        }

        File[] all_projects = dir.listFiles();

        if (orderByName) {
            Arrays.sort(all_projects);
        }

        for (File file : all_projects) {
            String projectURL = file.getAbsolutePath();
            String projectName = file.getName();

            projects.add(new Project(folder, projectName));
        }

        return projects;
    }

    public static String exportProjectAsProtoFile(Project p) {
        File f = new File(getExportFolderPath() + File.separator + p.getName() + "_" + TimeUtils.getCurrentTime() + PhonkSettings.PROTO_FILE_EXTENSION);

        MLog.d(TAG, "compress " + p.getFullPath());
        MLog.d(TAG, "to " + f.getAbsolutePath());

        // compress
        try {
            FileIO.zipFolder(p.getFullPath(), f.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return the filepath of the backup
        return f.getAbsolutePath();
    }

    public static boolean importProtoFile(String folder, String zipFilePath) {
        // TODO: Use thread
        // extract files
        try {
            FileIO.extractZip(zipFilePath, getProjectFolderPath(folder));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Get code from assets
    // public static String getCodeFromAssets(Context c, Project p) {
    //    return FileIO.readAssetFile(c, getProjectPath() + File.separator + PhonkSettings.PROTO_FILE_EXTENSION);
    // }


    public static ArrayList<ProtoFile> listFilesInProject(Project p) {
        File f = new File(p.getSandboxPath());
        File[] file = f.listFiles();

        ArrayList<ProtoFile> protoFiles = new ArrayList<>();

        for (File element : file) {
            ProtoFile protoFile = new ProtoFile();
            protoFile.project_parent = p.getName();
            protoFile.name = element.getName();
            protoFile.size = element.length();
            protoFile.path = element.getPath();
            protoFile.type = "file";

            protoFiles.add(protoFile);
        }

        return protoFiles;
    }

    public void listProcesses(Context context) {
        ActivityManager actvityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = actvityManager.getRunningAppProcesses();

        for(ActivityManager.RunningAppProcessInfo runningProInfo:procInfos) {
            MLog.d("Running Processes", "()()"+runningProInfo.processName);
        }
    }

    public static String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf("."));
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }

            return ext.toLowerCase();
        }
    }

    public static void addShortcut(Context c, String folder, String name) {
        Project p = new Project(folder, name);

        Intent.ShortcutIconResource icon;
        icon = Intent.ShortcutIconResource.fromContext(c, R.drawable.app_icon);

        if (ShortcutManagerCompat.isRequestPinShortcutSupported(c)) {
            Intent shortcutIntent = new Intent(c, AppRunnerActivity.class);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            shortcutIntent.putExtra(Project.NAME, p.getName());
            shortcutIntent.putExtra(Project.FOLDER, p.getFolder());
            shortcutIntent.setAction(Intent.ACTION_MAIN);

            ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(c, folder + "/" + name)
                    .setIntent(shortcutIntent) // !!! intent's action must be set on oreo
                    .setShortLabel(name)
                    .setIcon(IconCompat.createWithResource(c, R.drawable.app_icon))
                    .build();
            ShortcutManagerCompat.requestPinShortcut(c, shortcutInfo, null);
        }
    }

    public static void shareMainJsDialog(Context c, String folder, String name) {
        Project p = new Project(folder, name);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getCode(p));
        sendIntent.setType("text/plain");
        c.startActivity(Intent.createChooser(sendIntent, c.getResources().getText(R.string.send_to)));
    }

    public static void shareProtoFileDialog(Context c, String folder, String name) {
        final ProgressDialog progress = new ProgressDialog(c);
        progress.setTitle("Exporting .proto");
        progress.setMessage("Your project will be ready soon!");
        progress.setCancelable(true);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        Project p = new Project(folder, name);
        String zipFilePath = exportProjectAsProtoFile(p);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(zipFilePath)));
        shareIntent.setType("application/zip");

        progress.dismiss();

        c.startActivity(Intent.createChooser(shareIntent, c.getResources().getText(R.string.share_phonk_file)));
    }


    public static boolean installProject(Context c, String from, String to) {
        try {
            FileIO.unZipFile(from, to);
            return true;
        } catch (ZipException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void stop_all_scripts() {
        App.myLifecycleHandler.closeAllScripts();
    }

    public static void moveFileFromTo(Project p, String oldPath, String newPath) {
        File oldFile = new File(p.getFullPathForFile(oldPath));
        File newFile = new File(p.getFullPathForFile(newPath));

        MLog.d("ww", oldFile + " --- " + newFile);

        try {
            FileUtils.moveFile(oldFile, newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
