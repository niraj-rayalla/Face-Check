package com.lazerpower.facecheck.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Niraj on 4/12/2015.
 */
public class Position {
    public final int X;
    public final int Y;

    public Position(JSONObject positionJsonObject) throws JSONException {
        X = positionJsonObject.getInt("x");
        Y = positionJsonObject.getInt("y");
    }
}
