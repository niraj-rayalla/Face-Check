package com.lazerpower.facecheck.dispatcher.entity;

import android.content.ContentValues;

import com.lazerpower.facecheck.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class Entity {

    public interface MergePolicy {
        public void add(String key, ContentValues object);
        public void set(ContentValues values);
        public ContentValues getResult();
        public void reset();
    }

    public abstract ContentValues parse(JSONObject json, MergePolicy merge) throws JSONException;

    public List<ContentValues> parse(JSONArray jsonArray, MergePolicy merge) throws JSONException {
        ArrayList<ContentValues> result = new ArrayList<ContentValues>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            Object object = jsonArray.get(i);
            if(object instanceof JSONObject) {
                try {
                    if(merge != null) {
                        merge.reset();
                    }
                    ContentValues values = parse((JSONObject) object, merge);
                    if(merge != null) {
                        merge.set(values);
                        result.add(merge.getResult());
                    } else {
                        result.add(values);
                    }
                } catch(JSONException exception) {
                    Log.w("Failed to parse JSONArray item. " + exception);
                }
            } else {
                throw new JSONException("Expected JSONObject");
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> T parse(Object json, MergePolicy merge) throws JSONException {
        if(json instanceof JSONObject) {
            try {
                ContentValues result = parse((JSONObject) json, merge);

                if (result == null || result.size() == 0) {
                    //Try to check for error
                    result = new ContentValues();
                    JSONObject jsonObject = (JSONObject) json;
                    try {
                        result.put("error", jsonObject.getString("error"));
                    }
                    catch (JSONException f) {}
                    try {
                        result.put("meta", jsonObject.getString("meta"));
                    }
                    catch (JSONException f) {}
                }

                return (T)result;
            }
            catch (Exception e) {
                Log.e("Entity parse error", e);
                return null;
            }
        } else if(json instanceof JSONArray) {
            try {
                return (T)parse((JSONArray)json, merge);
            }
            catch (Exception e) {
                Log.e("Entity array parse error", e);
                return null;
            }
        } else {
            throw new JSONException("Expected JSONObject or JSONArray");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
