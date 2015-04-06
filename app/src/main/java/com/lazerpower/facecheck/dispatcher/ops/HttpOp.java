package com.lazerpower.facecheck.dispatcher.ops;

import com.lazerpower.facecheck.http.OperationHttpClient;

import java.io.IOException;

import org.json.JSONException;

public interface HttpOp extends Op {

    Object run(OperationHttpClient httpClient, Object arg)
            throws IOException, JSONException;

}
