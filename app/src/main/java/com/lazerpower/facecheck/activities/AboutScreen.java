package com.lazerpower.facecheck.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.lazerpower.facecheck.R;

/**
 * Created by Niraj on 4/17/2015.
 */
public class AboutScreen extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_about);
    }
}
