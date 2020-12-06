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

package io.phonk.gui.projectbrowser.projectlist;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;

import org.greenrobot.eventbus.EventBus;

import io.phonk.R;
import io.phonk.events.Events;
import io.phonk.helpers.PhonkAppHelper;
import io.phonk.helpers.PhonkScriptHelper;
import io.phonk.runner.base.models.Project;

public class ProjectItem extends LinearLayout {

    private static final String TAG = ProjectItem.class.getSimpleName();

    public static int MODE_NORMAL = 0;
    public static int MODE_SINGLE_PICK = 1;
    public static int MODE_MULTIPLE_PICK = 2;

    public final CheckBox checkbox;
    private View mItemView;
    private final Context mContext;

    private String t;
    private boolean highlighted = false;
    private Project mProject;
    private TextView txtProjectIcon;
    private TextView textViewName;
    private ImageView mMenuButton;
    private final ImageView customIcon;

    public ProjectItem(Context context, boolean listMode, int pickMode) {
        super(context);
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (listMode) {
            this.mItemView = inflater.inflate(R.layout.projectlist_item_list, this, true);
            this.txtProjectIcon = findViewById(R.id.txtProjectIcon);
        } else {
            this.mItemView = inflater.inflate(R.layout.projectlist_item_grid, this, true);
        }

        checkbox = (CheckBox) findViewById(R.id.checkBoxPicker);
        mMenuButton = findViewById(R.id.card_menu_button);

        if (pickMode == MODE_MULTIPLE_PICK) {
            checkbox.setVisibility(View.VISIBLE);
            mMenuButton.setVisibility(View.GONE);
        } else if (pickMode == MODE_SINGLE_PICK) {
            mMenuButton.setVisibility(View.GONE);
        }

        textViewName = mItemView.findViewById(R.id.customViewText);
        customIcon = mItemView.findViewById(R.id.iconImg);
    }

    public void setImage(Bitmap bmp) {
        customIcon.setVisibility(View.VISIBLE);
        customIcon.setImageBitmap(bmp);
        txtProjectIcon.setVisibility(View.INVISIBLE);
    }

    public void setText(String text) {
        this.t = text;
        textViewName.setText(text);
    }

    public void setMenu() {
        // setting menu for mProject.getName());
        mItemView.setOnLongClickListener(v -> {
            showMenu(mMenuButton);

            return true;
        });

        mMenuButton.setOnClickListener(v -> showMenu(mMenuButton));
    }

    private void showMenu(View fromView) {
        Context wrapper = new ContextThemeWrapper(mContext, R.style.phonk_PopupMenu);
        PopupMenu myPopup = new PopupMenu(wrapper, fromView);
        myPopup.inflate(R.menu.project_actions);
        myPopup.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();

            switch (itemId) {
                case R.id.menu_project_list_run:
                    PhonkAppHelper.launchScript(getContext(), mProject);
                    return true;
                case R.id.menu_project_list_edit:
                    PhonkAppHelper.launchEditor(getContext(), mProject);
                    return true;
                case R.id.menu_project_webeditor:
                    PhonkAppHelper.openInWebEditor(getContext(), mProject);
                    return true;

                case R.id.menu_project_list_delete:
                    DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_DELETE, mProject));
                                //mPlf.removeItem(mProject);
                                Toast.makeText(getContext(), mProject.getName() + " Deleted", Toast.LENGTH_LONG).show();
                                PhonkScriptHelper.deleteFileOrFolder(mProject.getFullPath());

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                    return true;
                case R.id.menu_project_list_add_shortcut:
                    PhonkScriptHelper.addShortcut(mContext, mProject.getFolder(), mProject.getName());
                    return true;
                case R.id.menu_project_list_share_with:
                    PhonkScriptHelper.shareMainJsDialog(mContext, mProject.getFolder(), mProject.getName());
                    return true;
                case R.id.menu_project_list_share_proto_file:
                    PhonkScriptHelper.sharePhonkFileDialog(mContext, mProject.getFolder(), mProject.getName());
                    return true;
                case R.id.menu_project_list_show_info:
                    PhonkAppHelper.launchScriptInfoActivity(mContext, mProject);
                    return true;
                default:
                    return true;
            }
        });
        myPopup.show();

    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setProjectInfo(Project p) {
        mProject = p;

        if (p.getName().length() > 2) {
            setIcon(p.getName().substring(0, 2));
        } else {
            setIcon(p.getName().substring(0, 1));
        }
        setText(p.getName());
        setTag(p.getName());

        Bitmap icon = p.getIcon();
        if (icon != null) setImage(icon);
        setMenu();
    }

    public void setIcon(String text) {
        this.txtProjectIcon.setText(text);
    }
}
