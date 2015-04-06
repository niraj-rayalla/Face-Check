package com.lazerpower.facecheck.dispatcher.ops;

public class DispatchResultOp implements Op {

    public Object run(OpCallback callback, Object arg) {
        if (callback != null) {
            callback.onOperationResultChanged(arg);
        }
        return arg;
    }

}
