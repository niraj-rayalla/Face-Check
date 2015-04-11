package com.lazerpower.facecheck.ops;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lazerpower.facecheck.dispatcher.entity.Match;
import com.lazerpower.facecheck.dispatcher.ops.DbOp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Niraj on 4/8/2015.
 */
public class GetMatch implements DbOp {

    private String mMatchId;

    public GetMatch(String matchId) {
        mMatchId = matchId;
    }

    @Override
    public Match.MatchModel run(SQLiteDatabase db, Object arg) throws JSONException {
        Match.MatchModel match = null;

        Cursor c = db.query("match",
                new String[]{"json"},
                "id=?", new String[]{mMatchId},
                null, null,
                null);
        try {
            if (c.moveToFirst()) {
                match = new Match.MatchModel(new JSONObject(c.getString(0)));
            }
        } finally {
            c.close();
        }

        return match;
    }
}