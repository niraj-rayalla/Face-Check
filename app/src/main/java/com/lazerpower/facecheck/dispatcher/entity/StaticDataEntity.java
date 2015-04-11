package com.lazerpower.facecheck.dispatcher.entity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.lazerpower.facecheck.App;
import com.lazerpower.facecheck.http.ApiPaths;
import com.lazerpower.facecheck.http.Parser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by Niraj on 4/10/2015.
 *
 * This is an entity representing static data object
 */
public abstract class StaticDataEntity extends Entity {
    public static final String API_VERSION = "v1.2";
    public static final String NOT_STORED_VERSION = "None";

    @Override
    public ContentValues parse(SQLiteDatabase db, JSONObject json, MergePolicy merge) throws JSONException {
        ContentValues contentValues = new ContentValues();
        new Parser(json, contentValues)
                .parseString("version");

        JSONObject data = json.getJSONObject("data");
        Iterator<String> dataNames = data.keys();
        Entity parser = getEntity();
        while(dataNames.hasNext()) {
            String dataName = dataNames.next();
            parser.parse(db, data.getJSONObject(dataName), null);
        }

        //Store the version of the static data
        PreferenceManager.getDefaultSharedPreferences(App.getInstance()).edit()
                .putString(getPreferenceName(), contentValues.getAsString("version"))
                .commit();

        return contentValues;
    }

    protected String getTypeVersion()  {
        String version = PreferenceManager.getDefaultSharedPreferences(App.getInstance())
                .getString(getPreferenceName(), NOT_STORED_VERSION);
        return version;
    }

    protected abstract String getType();

    protected abstract Entity getEntity();

    protected String getPreferenceName() {
        return "pref_"+getType()+"_version";
    }

    public String getApiPath() {
        return ApiPaths.getStaticDataPath(getType());
    }

    public String getImageServerPathPrefix() {
        return String.format("http://ddragon.leagueoflegends.com/cdn/%s/img/%s/", getTypeVersion(), getType());
    }

    public boolean hasStaticDataStored() {
        return !getTypeVersion().equals(NOT_STORED_VERSION);
    }
}
