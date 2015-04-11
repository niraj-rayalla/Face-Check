package com.lazerpower.facecheck.dispatcher.entity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.lazerpower.facecheck.db.DatabaseUtils;
import com.lazerpower.facecheck.http.Parser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Niraj on 4/9/2015.
 */
public class Champion extends Entity {
    @Override
    public ContentValues parse(SQLiteDatabase db, JSONObject json, MergePolicy merge) throws JSONException {
        ContentValues contentValues = new ContentValues();
        new Parser(json, contentValues)
                .parseString("id")
                .parseString("key")
                .parseString("name")
                .parseString("title")
                .parseJsonObjectAsString("image");

        long rowId = DatabaseUtils.upsert(
                db, "champion", contentValues,
                "id", contentValues.getAsString("id"));

        return contentValues;
    }

    public static class ChampionModel extends ImageRelatedModel {
        private String mId;
        private String mKey;
        private String mName;
        private String mTitle;

        public ChampionModel(String id, String key, String name, String title, String imageJsonString) {
            super(imageJsonString);
            mId = id;
            mKey = key;
            mName = name;
            mTitle = title;
        }

        @Override
        protected String getImageLocationServerPathPrefix() {
            return new Champions().getImageServerPathPrefix();
        }

        public String getId() {
            return mId;
        }

        public String getKey() {
            return mKey;
        }

        public String getName() {
            return mName;
        }

        public String getTitle() {
            return mTitle;
        }
    }
}
