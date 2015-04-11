package com.lazerpower.facecheck.ops;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lazerpower.facecheck.dispatcher.entity.Match;
import com.lazerpower.facecheck.dispatcher.ops.DbOp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Niraj on 4/6/2015.
 * Get all matches in descending time order
 */
public class GetMatches implements DbOp{
    @Override
    public ArrayList<Match.MatchModel> run(SQLiteDatabase db, Object arg) throws JSONException {
        ArrayList<Match.MatchModel> matches = new ArrayList<Match.MatchModel>();

        Cursor c = db.query("match",
                new String[]{"json"},
                null, null,
                null, null,
                "matchcreation DESC");
        try {
            while (c.moveToNext()) {
                matches.add(new Match.MatchModel(new JSONObject(c.getString(0))));
            }
        } finally {
            c.close();
        }

        return matches;
    }
}
