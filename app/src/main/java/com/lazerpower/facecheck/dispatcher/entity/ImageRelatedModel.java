package com.lazerpower.facecheck.dispatcher.entity;

import com.lazerpower.facecheck.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Niraj on 4/9/2015.
 * Can store image info returned in multiple responses in the League of Legends API.
 */
public abstract class ImageRelatedModel {
    private String mImageUrl;

    public ImageRelatedModel(String imageJsonString) {
        try {
            JSONObject jsonObject = new JSONObject(imageJsonString);
            mImageUrl = getImageLocationServerPathPrefix() + jsonObject.getString("full");
        }
        catch (JSONException e) {
            Log.d("Could not convert image json string to json object", e);
        }
    }

    /**
     *
     * @return The url that when prepended to the image file name (given in a json response)
     * will point to the storage location of the image.
     */
    protected abstract String getImageLocationServerPathPrefix();

    public String getImageUrl() {
        return mImageUrl;
    }
}
