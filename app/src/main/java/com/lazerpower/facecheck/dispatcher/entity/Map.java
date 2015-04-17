package com.lazerpower.facecheck.dispatcher.entity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Pair;

import com.lazerpower.facecheck.db.DatabaseUtils;
import com.lazerpower.facecheck.http.Parser;
import com.lazerpower.facecheck.models.Position;

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

    /**
     * Map's origin starts at bottom left
     */
    public static class MapModel extends ImageRelatedModel {
        private static final Rect SUMMONER_RIFT_MINI_MAP_OFFSETS = new Rect(-120, -120, 14870, 14980);

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

        public static Rect getMapsOffset(String mapId) {
            //Summoner's rift mini-map adjusted bounds after November 20, 2014
            return SUMMONER_RIFT_MINI_MAP_OFFSETS;
        }

        public static Pair<Float, Float> getCanvasPositionFromInGamePosition(String mapId, Canvas canvas, Position position) {
            Rect offsets = getMapsOffset(mapId);
            float x = ((float)(position.X-offsets.left)/(float)(offsets.right-offsets.left)) * canvas.getWidth();
            //Flip the y axis because the map y coordinate starts on the bottom and goes up
            float y = (1.0f - (float)(position.Y-offsets.top)/(float)(offsets.bottom-offsets.top)) * canvas.getHeight();

            return new Pair<Float, Float>(x, y);
        }
    }
}