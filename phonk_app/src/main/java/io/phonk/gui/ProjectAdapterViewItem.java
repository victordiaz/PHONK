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

package io.phonk.gui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import io.phonk.R;
import io.phonk.events.Events;
import io.phonk.helpers.PhonkAppHelper;
import io.phonk.helpers.PhonkScriptHelper;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.models.Project;

import java.io.File;

public class ProjectAdapterViewItem extends LinearLayout {

    private static final String TAG = ProjectAdapterViewItem.class.getSimpleName();

    private View mItemView;
    private final Context c;

    private String t;
    private Project mProject;
    private TextView txtProjectIcon;
    private TextView textViewName;
    private ImageView mMenuButton;
    private final ImageView customIcon;

    public ProjectAdapterViewItem(Context context, boolean listMode) {
        super(context);
        this.c = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (listMode) {
            this.mItemView = inflater.inflate(R.layout.projectlist_item_list, this, true);
            this.txtProjectIcon = (TextView) findViewById(R.id.txtProjectIcon);
        } else {
            this.mItemView = inflater.inflate(R.layout.projectlist_item_grid, this, true);
        }

        textViewName = (TextView) mItemView.findViewById(R.id.customViewText);
        customIcon = (ImageView) mItemView.findViewById(R.id.iconImg);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMenu(v);

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        PhonkAppHelper.launchScript(getContext(), mProject);
                    }
                };

                Handler handler = new Handler();
                handler.postDelayed(r, 0);
            }
        });

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

    boolean isPlayOnPress = true;

    public void setMenu() {
        mMenuButton = (ImageView) findViewById(R.id.card_menu_button);

        // show / hide right menu
        if (!isPlayOnPress) mMenuButton.setVisibility(View.GONE);

        mItemView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showMenu(mMenuButton);

                return true;
            }
        });

        mMenuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(mMenuButton);
            }
        });
    }

    private void showMenu(View fromView) {
        PopupMenu myPopup = new PopupMenu(c, fromView);
        myPopup.inflate(R.menu.project_actions);
        myPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem menuItem) {

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
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_DELETE, mProject));
                                        MLog.d(TAG, "deleting " + mProject.getFullPath());
                                        Toast.makeText(getContext(), mProject.getName() + " Deleted", Toast.LENGTH_LONG).show();
                                        PhonkScriptHelper.deleteFolder(mProject.getFullPath());

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(c);
                        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                        return true;
                    case R.id.menu_project_list_add_shortcut:
                        PhonkScriptHelper.addShortcut(c, mProject.getFolder(), mProject.getName());
                        return true;
                    case R.id.menu_project_list_share_with:
                        PhonkScriptHelper.shareMainJsDialog(c, mProject.getFolder(), mProject.getName());
                        return true;
                    case R.id.menu_project_list_share_proto_file:
                        PhonkScriptHelper.shareProtoFileDialog(c, mProject.getFolder(), mProject.getName());
                        return true;
                    case R.id.menu_project_list_show_info:
                        PhonkAppHelper.launchScriptInfoActivity(c, mProject);
                        return true;
                    default:
                        return true;
                }
            }
        });
        myPopup.show();

    }

    public void setInfo(Project p) {
        mProject = p;

        MLog.d("qq1", p.getFolder() + " " + p.getName());

        if (p.getName().length() > 2) {
            setIcon(p.getName().substring(0, 2));
        } else {
            setIcon(p.getName().substring(0, 1));
        }
        setText(p.getName());
        setTag(p.getName());

        File f = new File(p.getFullPathForFile("icon.png"));
        // setImage(R.drawable.primarycolor_rounded_rect);

        if (f.exists()) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(f.getPath(), bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);

            setImage(bitmap);
        }
        setMenu();
    }

    public void setIcon(String text) {
        this.txtProjectIcon.setText(text);
    }
}
