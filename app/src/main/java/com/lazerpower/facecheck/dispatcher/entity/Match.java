package com.lazerpower.facecheck.dispatcher.entity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.lazerpower.facecheck.Log;
import com.lazerpower.facecheck.db.DatabaseUtils;
import com.lazerpower.facecheck.http.Parser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Niraj on 4/6/2015.
 */
public class Match extends Entity {
    @Override
    public ContentValues parse(SQLiteDatabase db, JSONObject json, MergePolicy merge) throws JSONException {
        ContentValues contentValues = new ContentValues();
        new Parser(json, contentValues)
                .parseString("matchId")
                .parseLong("matchCreation")
                .parseInt("matchDuration");

        contentValues.put("json", json.toString());

        long rowId = DatabaseUtils.upsert(
                db, "match", contentValues,
                "matchId", contentValues.getAsString("matchId"));

        return contentValues;
    }

    public static class MatchModel {
        private JSONObject mJson;

        private Date mCreationTime;
        private int mMatchDurationInSeconds;

        public MatchModel(JSONObject json) {
            mJson = json;

            try {
                mCreationTime = new Date(json.getLong("matchCreation"));
                mMatchDurationInSeconds = json.getInt("matchDuration");
            }
            catch (Exception e) {
                Log.d("Problem creating Match model", e);
            }
        }

        public Date getCreationTime() {
            return mCreationTime;
        }

        public int getMatchDurationInSeconds() {
            return mMatchDurationInSeconds;
        }
    }
}
