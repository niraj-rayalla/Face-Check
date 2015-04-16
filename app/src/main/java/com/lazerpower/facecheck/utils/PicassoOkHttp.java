package com.lazerpower.facecheck.utils;

/**
 * Created by Niraj on 4/14/2015.
 */

import android.content.Context;
import android.net.Uri;

import com.lazerpower.facecheck.App;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class PicassoOkHttp {

    private static OkHttpClient okHttpClient = null;
    private static Picasso picasso = null;

    public static Picasso getPicassoInstance() {
        if (okHttpClient == null || picasso == null) {
            okHttpClient = new OkHttpClient();
            picasso = new Picasso.Builder(App.getInstance().getApplicationContext())
                    .downloader(new OkHttpDownloader(okHttpClient))
                    .build();
        }

        return picasso;
    }

    public static RequestCreator getRequest(Context context, Uri uri) {
        RequestCreator requestCreator = PicassoOkHttp.getPicassoInstance().with(context)
                .load(uri);

        if (!App.getInstance().getReachability().isReachable()) {
            //Just load from local cache if available
            requestCreator.networkPolicy(NetworkPolicy.OFFLINE);
        }

        return requestCreator;
    }
}
