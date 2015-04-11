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
public class Item extends Entity {
    @Override
    public ContentValues parse(SQLiteDatabase db, JSONObject json, MergePolicy merge) throws JSONException {
        ContentValues contentValues = new ContentValues();
        new Parser(json, contentValues)
                .parseString("id")
                .parseString("name")
                .parseString("group", "item_group")
                .parseString("description")
                .parseJsonObjectAsString("image");

        long rowId = DatabaseUtils.upsert(
                db, "item", contentValues,
                "id", contentValues.getAsString("id"));

        return contentValues;
    }

    public static class ItemModel extends ImageRelatedModel {
        private String mId;
        private String mName;
        private String mGroup;
        private String mDescription;

        public ItemModel(String id, String name, String group, String description, String imageJsonString) {
            super(imageJsonString);
            mId = id;
            mName = name;
            mGroup = group;
            mDescription = description;
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

        public String getGroup() {
            return mGroup;
        }

        public String getDescription() {
            return mDescription;
        }
    }
}
