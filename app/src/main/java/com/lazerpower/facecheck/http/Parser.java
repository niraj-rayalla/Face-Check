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
        return parseString(key, key);
    }
    public Parser parseString(final String key, final String newKey) throws JSONException {
        if(mJson.has(key) && !mJson.isNull(key)) {
            String value = mJson.getString(key);
            mValues.put(newKey, value);
        }
        return this;
    }

    public Parser parseInt(final String key) throws JSONException {
        return parseInt(key, key);
    }
    public Parser parseInt(final String key, final String newKey) throws JSONException {
        if(mJson.has(key) && !mJson.isNull(key)) {
            try {
                int value = mJson.getInt(key);
                mValues.put(newKey, value);
            }
            catch (JSONException e) {
                mValues.put(newKey, 0);
            }
        }
        return this;
    }


    public Parser parseLong(final String key) throws JSONException {
        return parseLong(key, key);
    }
    public Parser parseLong(final String key, final String newKey) throws JSONException {
        if(mJson.has(key) && !mJson.isNull(key)) {
            try {
                long value = mJson.getLong(key);
                mValues.put(newKey, value);
            }
            catch (JSONException e) {
                mValues.put(newKey, 0);
            }
        }
        return this;
    }

    public Parser parseBool(final String key) throws JSONException {
        return parseBool(key, key);
    }
    public Parser parseBool(final String key, final String newKey) throws JSONException {
        if(mJson.has(key) && !mJson.isNull(key)) {
            boolean value = mJson.getBoolean(key);
            mValues.put(newKey, value);
        }
        return this;
    }

    public Parser parseDouble(String key) throws JSONException {
        return parseDouble(key, key);
    }
    public Parser parseDouble(String key, final String newKey) throws JSONException {
        if (mJson.has(key) && !mJson.isNull(key)) {
            double value = mJson.getDouble(key);
            mValues.put(newKey, value);
        }
        return this;
    }

    public Parser parseJsonObjectAsString(final String key) throws JSONException {
        return parseJsonObjectAsString(key, key);
    }
    public Parser parseJsonObjectAsString(final String key, final String newKey) throws JSONException {
        if(mJson.has(key) && !mJson.isNull(key)) {
            JSONObject value = mJson.getJSONObject(key);
            mValues.put(newKey, value.toString());
        }
        return this;
    }

};
