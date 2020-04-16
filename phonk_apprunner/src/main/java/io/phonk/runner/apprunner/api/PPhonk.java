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

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import io.phonk.runner.AppRunnerActivity;
import io.phonk.runner.apprunner.api.widgets.PWebEditor;
import io.phonk.runner.apidoc.annotation.APIRequires;
import io.phonk.runner.apidoc.annotation.APIVersion;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.models.Project;

/*
 * This class only contains methods used during app creation. These methods shouldnt be used
 * once the script is done and won't be able to be used once the app is exported.
 *
 */
public class PPhonk extends ProtoBase {

    public String id;

    public PPhonk(AppRunner appRunner) {
        super(appRunner);

        // get org.apprunner settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(appRunner.getAppContext());
        id = sharedPrefs.getString("pref_id", "-1");
    }

    //TODO enable this after refactor
//
//	@APIMethod(description = "", example = "")
//	public String getId() {
//		return PrefsFragment.getId(mContext);
//
//	}

//
//	@APIMethod(description = "", example = "")
//	@APIParam(params = { "id" })
//	public void setId(String id) {
//		PrefsFragment.setId(mContext, id);
//	}


    @PhonkMethod(description = "get the current project HTTP URL", example = "")
    public String urlForFiles() {
        Project p = getAppRunner().getProject();
        String url = getAppRunner().getServingUrl() + "api/project/" + p.getSandboxPath() + "files/view/";
        return url;
    }

    //TODO reenable?
    @PhonkMethod(description = "Returns an object to manipulate the device app webIDE", example = "")
    @PhonkMethodParam(params = {})
    public PWebEditor webEditor() {
        PWebEditor pWebEditor = new PWebEditor(getAppRunner());

        return pWebEditor;
    }

//TODO reenable this
//
//	@APIMethod(description = "Launch another script given its name and type", example = "")
//	@APIParam(params = { "folder", "name" })
//	@APIVersion(minLevel = "2")
//	@APIRequires("android.permission.INTERNET")
//	public void launchScript(String folder, String name) {
//		Intent intent = new Intent(mContext, AppRunnerFragment.class);
//		intent.putExtra(Project.FOLDER, name);
//		intent.putExtra(Project.NAME, name);
//
//		// mContext.startActivity(intent);
//		// String code = StrUtils.generateUUID();
//		contextUi.get().startActivityForResult(intent, 22);
//	}

    //TODO this is a place holder
    //
    @PhonkMethod(description = "", example = "")
    @APIVersion(minLevel = "2")
    @APIRequires("android.permission.INTERNET")
    public void returnValueToScript(String returnValue) {
        Intent output = new Intent();
        output.putExtra("return", returnValue);
        //contextUi.get().setResult(22, output);
        //contextUi.get().finish();
    }


    //TODO this doesnt work

    @PhonkMethod(description = "", example = "")
    public void returnResult(String data) {

        Bundle conData = new Bundle();
        conData.putString("param_result", data);
        Intent intent = new Intent();
        intent.putExtras(conData);
        // contextUi.get().setResult(contextUi.get().RESULT_OK, intent);
        // contextUi.get().finish();

    }

    // --------- addDebugger ---------//
    public interface AddDebuggerCB {
        void data(String debuggableScript);
    }

    @PhonkMethod(description = "Get the current Protocoder version name", example = "")
    public String versionName() {
        PackageInfo pInfo = null;
        try {
            pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionName;
    }


    @PhonkMethod(description = "Get the current Protocoder version code", example = "")
    public int versionCode() {
        PackageInfo pInfo = null;
        try {
            pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionCode;
    }


    @PhonkMethod(description = "Install a Proto app programatically", example = "")
    public void installPhonkApp(String src, boolean b) {
        //TODO reenable this
        final String projectPath = null; //ProjectManager.getInstance().getCurrentProject().getStoragePath();

        Intent intent = new Intent();
        intent.setComponent(new ComponentName("io.phonk", "io.phonk.PhonkAppInstallerActivity"));
        intent.setData(Uri.parse(projectPath + "/" + src));
        intent.putExtra("autoInstall", b);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        getContext().startActivity(intent);
    }

    public void runScript(String folder, String scriptName) {
        Intent intent = new Intent(getContext(), AppRunnerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Project.FOLDER, folder);
        intent.putExtra(Project.NAME, scriptName);
        // intent.putExtra(Project.SERVER_PORT, ProtocoderSettings.HTTP_PORT);
        // intent.putExtra("device_id", (String) NewUserPreferences.getInstance().get("device_id"));
        getContext().startActivity(intent);
    }

    @Override
    public void __stop() {
        
    }
}