package com.lazerpower.facecheck.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.lazerpower.facecheck.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
            File path = mContext.getDatabasePath(DatabaseOpenHelper.NAME);
            //Check if database exists and if not copy from assests folder
            if (!path.exists()) {
                try {
                    mOpenHelper = new DatabaseOpenHelper(mContext);
                    mOpenHelper.getWritableDatabase();

                    copyDataBase();
                }
                catch (IOException e) {
                    Log.e("Could not copy dictionary.", e);
                }
            }
            if (mOpenHelper == null) {
                mOpenHelper = new DatabaseOpenHelper(mContext);
            }
            return mOpenHelper.getWritableDatabase();
        }
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = mContext.getAssets().open(DatabaseOpenHelper.NAME);
        String outFileName = mContext.getDatabasePath(DatabaseOpenHelper.NAME).getAbsolutePath();

        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
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
