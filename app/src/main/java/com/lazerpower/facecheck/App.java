package com.lazerpower.facecheck;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.lazerpower.facecheck.db.DatabaseFactory;
import com.lazerpower.facecheck.dispatcher.Dispatcher;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Niraj on 4/4/2015.
 */
public class App extends Application {
    private static App mInstance = null;

    public static App getInstance() {
        return mInstance;
    }

    private Features mFeatures;
    private DatabaseFactory mDatabaseFactory;
    private Dispatcher mDispatcher;
    private Reachability mReachability;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        mFeatures = createFeatures();
        mDatabaseFactory = new DatabaseFactory(this);
        mDispatcher = new Dispatcher(new DatabaseFactory(this));
        mReachability = new Reachability(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        mFeatures = null;
        mDatabaseFactory = null;
        mDispatcher = null;
        mReachability.close();
        mReachability = null;

        mInstance = null;
    }

    private Features createFeatures() {
        Features result = null;
        InputStream featuresStream = getResources().openRawResource(R.raw.facecheck_features);
        try {
            result = new Features(featuresStream);
        } catch (IOException e) {
            Log.d("Could not read application features.", e);
        } catch (JSONException e) {
            Log.d("Could not parse application features.", e);
        } finally {
            IOUtils.closeQuietly(featuresStream);
        }
        return result;
    }

    public Features getFeatures() {
        return mFeatures;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabaseFactory.openDatabase();
    }

    public Dispatcher getDispatcher() {
        return mDispatcher;
    }

    public Reachability getReachability() {
        return mReachability;
    }
}
