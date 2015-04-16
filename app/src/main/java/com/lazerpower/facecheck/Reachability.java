package com.lazerpower.facecheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Niraj on 4/14/2015.
 */

public class Reachability  {

    private final Context mContext;
    private AtomicBoolean mIsReachable;

    public Reachability(Context context) {
        mContext = context;
        mIsReachable = new AtomicBoolean();

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        mIsReachable.set(info != null && info.isConnected());

        context.registerReceiver(mReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public boolean isReachable() {
        return mIsReachable.get();
    }

    public void close() {
        mContext.unregisterReceiver(mReceiver);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mIsReachable.set(!intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false));
        }
    };

}
