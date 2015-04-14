package com.lazerpower.facecheck.dispatcher.entity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.lazerpower.facecheck.db.DatabaseUtils;
import com.lazerpower.facecheck.http.Parser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Niraj on 4/13/2015.
 */
public class Map extends Entity {
    @Override
    public ContentValues parse(SQLiteDatabase db, JSONObject json, MergePolicy merge) throws JSONException {
        ContentValues contentValues = new ContentValues();
        new Parser(json, contentValues)
                .parseString("mapId", "id")
                .parseString("mapName", "name")
                .parseJsonObjectAsString("image");

        long rowId = DatabaseUtils.upsert(
                db, "map", contentValues,
                "id", contentValues.getAsString("id"));

        return contentValues;
    }

    public static class MapModel extends ImageRelatedModel {
        private String mId;
        private String mName;

        public MapModel(String id, String name, String imageJsonString) {
            super(imageJsonString);
            mId = id;
            mName = name;
        }

        @Override
        protected String getImageLocationServerPathPrefix() {
            return new Maps().getImageServerPathPrefix();
        }

        public String getId() {
            return mId;
        }

        public String getName() {
            return mName;
        }
    }
}