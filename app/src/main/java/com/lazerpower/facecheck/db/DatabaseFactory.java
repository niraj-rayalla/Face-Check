package com.lazerpower.facecheck.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.lazerpower.facecheck.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Created by Niraj on 4/5/2015.
 */
public class DatabaseFactory {

    private final Object mLock;
    private final Context mContext;
    private DatabaseOpenHelper mOpenHelper;

    public DatabaseFactory(Context context) {
        mLock = new Object();
        mContext = context;
    }

    /**
     * Open a new handle to the database.
     *
     * The caller is responsible for closing the returned handle.
     *
     * @return A database handle.
     */
    public SQLiteDatabase openDatabase() {
        synchronized (mLock) {
            Log.d("Opening database.");
            if (mOpenHelper == null) {
                mOpenHelper = new DatabaseOpenHelper(mContext);
            }
            return mOpenHelper.getWritableDatabase();
        }
    }

    /**
     * Delete all public databases. Current database handles will remain valid
     * as the file isn't removed until all open file handles are closed.
     */
    public void deleteDatabase() {
        synchronized (mLock) {
            Log.d("Deleting database.");
            File path = mContext.getDatabasePath(DatabaseOpenHelper.NAME);
            FileUtils.deleteQuietly(path);
            mOpenHelper = null;
        }
    }

}
