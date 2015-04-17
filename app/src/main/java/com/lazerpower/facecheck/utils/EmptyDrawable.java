package com.lazerpower.facecheck.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

/**
 * Created by Niraj on 4/17/2015.
 */
public class EmptyDrawable extends ColorDrawable {

    private static ColorDrawable mEmptyDrawable = null;

    public static ColorDrawable getDrawable() {
        if (mEmptyDrawable == null) {
            mEmptyDrawable = new ColorDrawable(Color.argb(100, 0, 0, 0));
        }

        return mEmptyDrawable;
    }
}
