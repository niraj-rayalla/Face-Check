package com.lazerpower.facecheck.dispatcher.ops;

import com.squareup.okhttp.Request;

public class HttpDeleteOp extends HttpRestOp {

    public HttpDeleteOp(String path) {
        super(path);
    }

    @Override
    public Request.Builder createRequest(Object arg, String uri) {
        return new Request.Builder()
                .url(uri)
                .delete();
    }

    @Override
    protected String getMethod() {
        return "DELETE";
    }

}
