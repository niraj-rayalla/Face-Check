package com.lazerpower.facecheck.dispatcher.ops;

import java.io.IOException;
import java.util.Collection;

import org.apache.http.client.ClientProtocolException;

import com.lazerpower.facecheck.Log;
import com.lazerpower.facecheck.http.OperationHttpClient;
import com.lazerpower.facecheck.http.Param;
import com.squareup.okhttp.Request;

public abstract class HttpRestOp implements HttpOp {

    public final String mPath;
    public Collection<Param> mUrlParams;
    public final boolean mUsePicNowUri;
    public final boolean mUseHttps;
    public final String mServerName;

    public HttpRestOp(String path) {
        this(path, null, true, true, null);
    }

    public HttpRestOp(String path, Collection<Param> urlParams) {
        this(path, urlParams, true, true, null);
    }

    public HttpRestOp(String path, Collection<Param> urlParams, boolean usePicNowUri,
                      boolean useHttps, String serverName) {
        mPath = path;
        mUrlParams = urlParams;
        mUsePicNowUri = usePicNowUri;
        mUseHttps = useHttps;
        mServerName = serverName;
    }

    public final Object run(OperationHttpClient httpClient, Object arg)
            throws ClientProtocolException, IOException {
        Collection<Param> calculatedUrlParams = mUrlParams;
        Collection<Param> runtimeUrlParams = getRuntimeUrlParams();
        if (runtimeUrlParams != null) {
            if (calculatedUrlParams != null) {
                calculatedUrlParams.addAll(runtimeUrlParams);
            }
            else {
                calculatedUrlParams = runtimeUrlParams;
            }
        }

        String uri = httpClient.getUri(mPath, calculatedUrlParams);
        Object result = httpClient.execute(createRequest(arg, uri)).parsedJSON;
        Log.v("JSON result: " + result);
        return result;
    }
    /**
     *
     * @return - Any parameters that can only be determined at operation run time
     */
    public Collection<Param> getRuntimeUrlParams() {
        return null;
    }

    public Collection<Param> getUrlParams() {
        return mUrlParams;
    }

    public void setUrlParams(Collection<Param> params) {
        mUrlParams = params;
    }

    protected abstract Request.Builder createRequest(Object arg, String uri);

    protected abstract String getMethod();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append(getClass().getSimpleName());
        sb.append(": ");
        sb.append(getMethod());
        sb.append(" ");
        sb.append(mPath);
        if (mUrlParams != null) {
            sb.append(" params=");
            sb.append(mUrlParams.toString());
        }
        return sb.toString();
    }

}
