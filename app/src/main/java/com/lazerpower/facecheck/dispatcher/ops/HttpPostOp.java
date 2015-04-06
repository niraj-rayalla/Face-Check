package com.lazerpower.facecheck.dispatcher.ops;

import java.util.Collection;

import com.lazerpower.facecheck.http.OperationHttpClient;
import com.lazerpower.facecheck.http.Param;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

public class HttpPostOp extends HttpRestOp {

    private final Collection<Param> mPostParams;
    private final RequestBody mPostRequestBody;

    public HttpPostOp(String path) {
        super(path);
        mPostParams = null;
        mPostRequestBody = null;
    }

    public HttpPostOp(String path, Collection<Param> postParams) {
        super(path);
        mPostParams = postParams;
        mPostRequestBody = null;
    }

    public HttpPostOp(String path, RequestBody postRequestBody) {
        super(path);
        mPostParams = null;
        mPostRequestBody = postRequestBody;
    }

    public HttpPostOp(String path, Collection<Param> postParams, RequestBody postRequestBody) {
        super(path);
        mPostParams = postParams;
        mPostRequestBody = postRequestBody;
    }

    @Override
    protected Request.Builder createRequest(Object arg, String uri) {
        if (mPostRequestBody != null) {
            return new Request.Builder()
                    .url(uri)
                    .post(mPostRequestBody);
        }

        if (mPostParams != null && mPostParams.size() > 0) {
            FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();

            for (Param entry : mPostParams) {
                formEncodingBuilder.add(entry.getKey(), entry.getValue());
            }

            return new Request.Builder()
                    .url(uri)
                    .post(formEncodingBuilder.build());
        }
        else {
            return new Request.Builder()
                    .url(uri)
                    .post(null);
        }
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

}
