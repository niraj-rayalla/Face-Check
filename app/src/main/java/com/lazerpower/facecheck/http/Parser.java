package com.lazerpower.facecheck.http;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

public  class Parser {

    private final JSONObject mJson;
    private final ContentValues mValues;

    public Parser(JSONObject json, ContentValues values) {
        mJson = json;
        mValues = values;
    }

    public Parser parseString(final String key) throws JSONException {
        if(mJson.has(key) && !mJson.isNull(key)) {
            String value = mJson.getString(key);
            mValues.put(key, value);
        }
        return this;
    }

    public Parser parseInt(final String key) throws JSONException {
        if(mJson.has(key) && !mJson.isNull(key)) {
            try {
                int value = mJson.getInt(key);
                mValues.put(key, value);
            }
            catch (JSONException e) {
                mValues.put(key, 0);
            }
        }
        return this;
    }

    public Parser parseBool(final String key) throws JSONException {
        if(mJson.has(key) && !mJson.isNull(key)) {
            boolean value = mJson.getBoolean(key);
            mValues.put(key, value);
        }
        return this;
    }

    public Parser parseDouble(String key) throws JSONException {
        if (mJson.has(key) && !mJson.isNull(key)) {
            double value = mJson.getDouble(key);
            mValues.put(key, value);
        }
        return this;
    }

};
