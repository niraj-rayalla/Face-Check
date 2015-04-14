package com.lazerpower.facecheck.ops;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lazerpower.facecheck.dispatcher.entity.Item;
import com.lazerpower.facecheck.dispatcher.entity.SummonerSpell;
import com.lazerpower.facecheck.dispatcher.ops.DbOp;

import org.json.JSONException;

/**
 * Created by Niraj on 4/13/2015.
 */
public class GetSummonerSpell implements DbOp {

    private String mSummonerSpellId;

    public GetSummonerSpell(String summonerSpellId) {
        mSummonerSpellId = summonerSpellId;
    }

    @Override
    public SummonerSpell.SummonerSpellModel run(SQLiteDatabase db, Object arg) throws JSONException {
        SummonerSpell.SummonerSpellModel summonerLevel = null;

        Cursor c = db.query("summoner",
                new String[]{"id", "name", "description", "summoner_level", "key", "image"},
                "id=?", new String[]{mSummonerSpellId},
                null, null,
                null);
        try {
            if (c.moveToFirst()) {
                summonerLevel = new SummonerSpell.SummonerSpellModel(
                        c.getString(0),                     //id
                        c.getString(1),                     //name
                        c.getString(2),                     //group
                        c.getInt(3),                        //description
                        c.getString(4),                     //image
                        c.getString(5)                      //image
                );
            }
        } finally {
            c.close();
        }

        return summonerLevel;
    }
}