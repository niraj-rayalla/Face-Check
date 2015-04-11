package com.lazerpower.facecheck.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lazerpower.facecheck.Log;

import java.util.Date;

/**
 * Created by Niraj on 4/6/2015.
 */
public class DatabaseUtils {

    /**
     * Update a row in the database with the new data if it exists otherwise insert
     * @param db
     * @param table
     * @param values
     * @param keyName
     * @param keyValue
     * @return
     */
    public static long upsert(SQLiteDatabase db, String table, ContentValues values, String keyName, String keyValue) {
        long resultRowId = -1;
        if (values.size() == 0) {
            return resultRowId;
        }

        String where = keyName + "=?";
        String[] args = new String[] { keyValue };
        Cursor cursor = db.query(table, new String[] { keyName }, where, args, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                //Found an entry with the given key value so update it
                if (db.update(table, values, where, args) == 0) {
                    Log.d("Failed to update table " + table + " with values " + values);
                }
            }
            else {
                // No object in our database yet, insert a new one
                resultRowId = db.insert(table, null, values);
                if (resultRowId < 0) {
                    Log.d("Failed to insert into table " + table);
                }
            }
        }
        finally {
            cursor.close();
        }
        return resultRowId;
    }
}
