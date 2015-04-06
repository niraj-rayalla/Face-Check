package com.lazerpower.facecheck.dispatcher.ops;

import java.util.Collection;

import com.lazerpower.facecheck.http.Param;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

public class HttpPutOp extends HttpRestOp {

    private final Collection<Param> mPutParams;
    private final RequestBody mPutRequestBody;

    public HttpPutOp(String path) {
        super(path);
        mPutParams = null;
        mPutRequestBody = null;
    }

    public HttpPutOp(String path, Collection<Param> putParams) {
        super(path);
        mPutParams = putParams;
        mPutRequestBody = null;
    }

    public HttpPutOp(String path, RequestBody putRequestBody) {
        super(path);
        mPutParams = null;
        mPutRequestBody = putRequestBody;
    }

    @Override
    protected Request.Builder createRequest(Object arg, String uri) {
        if (mPutRequestBody != null) {
            return new Request.Builder()
                    .url(uri)
                    .put(mPutRequestBody);
        }

        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();

        if (mPutParams != null && mPutParams.size() > 0) {
            for (Param entry : mPutParams) {
                formEncodingBuilder.add(entry.getKey(), entry.getValue());
            }
        }

        return new Request.Builder()
                .url(uri)
                .put(formEncodingBuilder.build());
    }

    @Override
    protected String getMethod() {
        return "PUT";
    }

}
