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

package io.phonk.runner.apprunner.api.other;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;

@PhonkClass
public class PSqLite extends ProtoBase {
    final String TAG = PSqLite.class.getSimpleName();

    private SQLiteDatabase db;

    public PSqLite(AppRunner appRunner, String dbName) {
        super(appRunner);

        open(dbName);
    }


    @PhonkMethod(description = "Open a SQLite ", example = "")
    @PhonkMethodParam(params = {"dirName"})
    public void open(String dbName) {
        db = getContext().openOrCreateDatabase(
                getAppRunner().getProject().getFullPathForFile(dbName),
                Context.MODE_PRIVATE,
                null
        );
    }

    @PhonkMethod(description = "Executes a SQL sentence", example = "")
    @PhonkMethodParam(params = {"sql"})
    public void execSql(String sql) {
        db.execSQL(sql);
    }

    // http://stackoverflow.com/questions/8830753/android-sqlite-which-query-query-or-rawquery-is-faster

    @PhonkMethod(description = "Querys the database in the given array of columns and returns a cursor", example = "")
    @PhonkMethodParam(params = {"table", "colums[]"})
    public Cursor query(String table, String[] columns) {
        for (String column : columns) {
        }

        return db.query(table, columns, null, null, null, null, null);
    }

    @PhonkMethod(description = "Close the database connection", example = "")
    @PhonkMethodParam(params = {})
    public void close() {
        db.close();
    }

    @PhonkMethod(description = "Deletes a table in the database", example = "")
    @PhonkMethodParam(params = {"tableName"})
    public void delete(String table) {
        this.db.delete(table, null, null);
    }

    @PhonkMethod(description = "Exectutes SQL sentence in the database", example = "")
    @PhonkMethodParam(params = {"table", "fields"})
    public void insert(String table, ArrayList<DBDataType> fields) {

        StringBuilder names = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (int i = 0; i < fields.size(); i++) {
            if (i != fields.size() - 1) {
                names.append(" ").append(fields.get(i).name).append(",");
                values.append(" ").append(fields.get(i).obj.toString());
            } else {
                names.append(fields.get(i).name);
                values.append(" ").append(fields.get(i).obj.toString());
            }
        }

        Log.d(TAG, " " + names + " " + values);

        db.execSQL("INSERT INTO " + table + " (" + names + ")" + " VALUES (" + values + ")");

    }

    public void stop() {
        if (db != null) {
            db.close();
        }
    }

    @Override
    public void __stop() {

    }

    public static class DBDataType {
        final String name;
        final Object obj;

        public DBDataType(String name, Object obj) {
            this.name = name;
            this.obj = obj;
        }
    }
}
