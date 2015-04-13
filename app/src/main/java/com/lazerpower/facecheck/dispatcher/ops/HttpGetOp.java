package com.lazerpower.facecheck.dispatcher.ops;

import com.lazerpower.facecheck.http.Param;
import com.squareup.okhttp.Request;

import java.util.Collection;

public class HttpGetOp extends HttpRestOp {

    public HttpGetOp(String path) {
        super(path);
    }

    public HttpGetOp(String path, Collection<Param> params) {
        super(path, params);
    }

    @Override
    public Request.Builder createRequest(Object arg, String uri) {
        return new Request.Builder()
                .url(uri)
                .get();
    }

    @Override
    protected String getMethod() {
        return "GET";
    }

}
