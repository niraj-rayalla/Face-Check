package com.lazerpower.facecheck.http;

import java.io.IOException;
import java.util.Collection;

import android.net.Uri;

import com.lazerpower.facecheck.App;
import com.lazerpower.facecheck.Log;
import com.squareup.okhttp.OkHttpClient;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class OperationHttpClient {

    private final OkHttpClient mHttpClient;

    public OperationHttpClient() {
        mHttpClient = new OkHttpClient();
    }

    public String getUri(String path, Collection<Param> params) {
        Uri.Builder result = App.getInstance().getFeatures().getServerBaseUri();
        result.appendEncodedPath(path);

        if(params != null) {
            for (Param p : params) {
                result.appendQueryParameter(p.getKey(), p.getValue());
            }
        }

        //Append api key
        result.appendQueryParameter("api_key", App.getInstance().getFeatures().getApiKey());

        return result.toString();
    }

    public JSONResponseHandler.Response execute(Request.Builder requestBuilder)
            throws IOException {

        Request request = requestBuilder.build();

        Log.d("Executing " + request.getClass().getSimpleName() + " uri=" + request.urlString());
        Response okHttpResponse = mHttpClient.newCall(request).execute();
        Log.v("Executed result " + request.getClass().getSimpleName() + " uri=" + request.urlString()
            + "\nresponse= " + okHttpResponse.toString());

        return new JSONResponseHandler().handleResponse(okHttpResponse);
    }

}
