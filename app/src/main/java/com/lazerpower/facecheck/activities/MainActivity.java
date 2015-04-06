package com.lazerpower.facecheck.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.lazerpower.facecheck.App;
import com.lazerpower.facecheck.Log;
import com.lazerpower.facecheck.R;
import com.lazerpower.facecheck.dispatcher.ops.DispatchResultOp;
import com.lazerpower.facecheck.dispatcher.ops.HttpGetOp;
import com.lazerpower.facecheck.dispatcher.ops.OpCallback;
import com.lazerpower.facecheck.http.ApiPaths;
import com.lazerpower.facecheck.http.Param;
import com.lazerpower.facecheck.utils.TimeUtils;

/**
 * Created by Niraj on 4/3/2015.
 */
public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }
}
