package com.lazerpower.facecheck.http;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Niraj on 4/4/2015.
 */
public class Param {

    private final String mKey;
    private final String mValue;

    public static Collection<Param> withKeysAndValues(String... keysAndValues) {
        if (keysAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("length of keysAndValues must be even");
        }
        ArrayList<Param> result = new ArrayList<Param>(keysAndValues.length / 2);
        for (int i = 0; i < keysAndValues.length; i += 2) {
            result.add(new Param(keysAndValues[i], keysAndValues[i + 1]));
        }
        return result;
    }

    public Param(String key, String value) {
        mKey = key;
        mValue = value;
    }

    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }

    @Override
    public String toString() {
        return mKey + "=" + mValue;
    }
}
