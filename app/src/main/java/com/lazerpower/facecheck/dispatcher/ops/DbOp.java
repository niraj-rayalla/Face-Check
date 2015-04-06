package com.lazerpower.facecheck.dispatcher.ops;

import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;

public interface DbOp extends Op {

    Object run(SQLiteDatabase db, Object arg) throws JSONException;

}
