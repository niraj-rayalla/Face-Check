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
public class SummonerSpell extends Entity {
    @Override
    public ContentValues parse(SQLiteDatabase db, JSONObject json, MergePolicy merge) throws JSONException {
        ContentValues contentValues = new ContentValues();
        new Parser(json, contentValues)
                .parseString("id")
                .parseString("name")
                .parseString("description")
                .parseInt("summonerLevel", "summoner_level")
                .parseString("key")
                .parseJsonObjectAsString("image");

        long rowId = DatabaseUtils.upsert(
                db, "summoner", contentValues,
                "id", contentValues.getAsString("id"));

        return contentValues;
    }

    public static class SummonerSpellModel extends ImageRelatedModel {
        private String mId;
        private String mName;
        private String mDescription;
        private int mSummonerLevel;
        private String mKey;

        public SummonerSpellModel(String id, String name, String description, int summonerLevel, String key, String imageJsonString) {
            super(imageJsonString);
            mId = id;
            mName = name;
            mDescription = description;
            mSummonerLevel = summonerLevel;
            mKey = key;
        }

        @Override
        protected String getImageLocationServerPathPrefix() {
            return new Items().getImageServerPathPrefix();
        }

        public String getId() {
            return mId;
        }

        public String getName() {
            return mName;
        }

        public String getDescription() {
            return mDescription;
        }

        public int getSummonerLevel() {
            return mSummonerLevel;
        }

        public String getKey() {
            return mKey;
        }
    }
}