package com.lazerpower.facecheck.ops;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lazerpower.facecheck.dispatcher.entity.Champion;
import com.lazerpower.facecheck.dispatcher.ops.DbOp;

import org.json.JSONException;

/**
 * Created by Niraj on 4/9/2015.
 */
public class GetChampion implements DbOp {

    private String mChampionId;

    public GetChampion(String championId) {
        mChampionId = championId;
    }

    @Override
    public Champion.ChampionModel run(SQLiteDatabase db, Object arg) throws JSONException {
        Champion.ChampionModel champion = null;

        Cursor c = db.query("champions",
                new String[]{"id", "key", "name", "title", "image"},
                "id=?", new String[]{mChampionId},
                null, null,
                null);
        try {
            if (c.moveToFirst()) {
                champion = new Champion.ChampionModel(
                        c.getString(0),                     //id
                        c.getString(1),                     //key
                        c.getString(2),                     //name
                        c.getString(3),                     //title
                        c.getString(4)                      //image
                );
            }
        } finally {
            c.close();
        }

        return champion;
    }
}
