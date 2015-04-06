package com.lazerpower.facecheck.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lazerpower.facecheck.Log;

/**
 * Created by Niraj on 4/5/2015.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String NAME = "FaceCheck.sqlite";

    // Don't change the order of these patches. The only thing you should do is
    // add new patches to the end of the array.
    private static final Patch[] PATCHES = new Patch[] {
            new InitialDatabasePatch()
    };

    public static int getCurrentDatabaseVersion() {
        return PATCHES.length;
    }

    public DatabaseOpenHelper(Context context) {
        super(context, NAME, null, getCurrentDatabaseVersion());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Creating new database with version=" + getCurrentDatabaseVersion());
        up(db, 0, getCurrentDatabaseVersion());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Upgrading existing database with oldVersion=" + oldVersion + " to newVersion=" + newVersion);
        up(db, oldVersion, newVersion);
    }

    private void up(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; ++i) {
            Patch patch = PATCHES[i];
            Log.d("Executing patch version=" + i + " class=" + patch.getClass().getSimpleName());
            patch.up(db);
        }
    }
}