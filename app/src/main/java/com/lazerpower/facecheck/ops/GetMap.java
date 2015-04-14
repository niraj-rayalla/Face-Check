package com.lazerpower.facecheck.ops;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lazerpower.facecheck.dispatcher.entity.Champion;
import com.lazerpower.facecheck.dispatcher.entity.Map;
import com.lazerpower.facecheck.dispatcher.ops.DbOp;

import org.json.JSONException;

/**
 * Created by Niraj on 4/13/2015.
 */
public class GetMap implements DbOp {

    private String mMapId;

    public GetMap(String mapId) {
        mMapId = mapId;
    }

    @Override
    public Map.MapModel run(SQLiteDatabase db, Object arg) throws JSONException {
        Map.MapModel map = null;

        Cursor c = db.query("map",
                new String[]{"id", "name", "image"},
                "id=?", new String[]{mMapId},
                null, null,
                null);
        try {
            if (c.moveToFirst()) {
                map = new Map.MapModel(
                        c.getString(0),                     //id
                        c.getString(1),                     //name
                        c.getString(2)                      //image
                );
            }
        } finally {
            c.close();
        }

        return map;
    }
}