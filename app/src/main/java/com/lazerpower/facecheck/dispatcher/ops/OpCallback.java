package com.lazerpower.facecheck.dispatcher.ops;

public interface OpCallback {

    void onOperationStarted();

    void onOperationResultChanged(Object result);

    void onOperationFinished(Exception exception);

}
