package com.lazerpower.facecheck.dispatcher;

import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.widget.Toast;

import com.lazerpower.facecheck.App;
import com.lazerpower.facecheck.Log;
import com.lazerpower.facecheck.db.DatabaseFactory;
import com.lazerpower.facecheck.dispatcher.ops.DbOp;
import com.lazerpower.facecheck.dispatcher.ops.DispatchResultOp;
import com.lazerpower.facecheck.dispatcher.ops.HttpOp;
import com.lazerpower.facecheck.dispatcher.ops.Op;
import com.lazerpower.facecheck.dispatcher.ops.OpCallback;
import com.lazerpower.facecheck.http.JSONResponseHandler;
import com.lazerpower.facecheck.http.OperationHttpClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

public class Dispatcher {

    private static final int MESSAGE_DISPATCH = 0;
    private static final int MESSAGE_STOP = 1;

    private DatabaseFactory mDbFactory;

    private final CountDownLatch mThreadsReadySignal;
    private final CountDownLatch mThreadsQuitSignal;
    private final Handler mDbHandler;
    private final Handler mHttpHandler;
    private final Handler mEventHandler;

    private final HashMap<OpQueue, OpCallback> mCallbacks;

    public Dispatcher(DatabaseFactory dbFactory) {
        mDbFactory = dbFactory;

        mThreadsReadySignal = new CountDownLatch(1);
        mThreadsQuitSignal = new CountDownLatch(1);

        WorkThread dbThread = new WorkThread("DbThread");
        dbThread.start();
        WorkThread httpThread = new WorkThread("HttpThread");
        httpThread.start();
        try {
            mThreadsReadySignal.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException("Interrupted while waiting for work threads to start.", e);
        }

        mDbHandler = new DbHandler(dbThread.getLooper());
        mHttpHandler = new HttpHandler(httpThread.getLooper());
        mEventHandler = new EventHandler(Looper.myLooper());

        mCallbacks = new HashMap<OpQueue, OpCallback>();
    }

    /**
     * Dispatch a set of operations.
     *
     * The operations will be executed in the order they are passed to this function.
     * The results of each operation are passed as the arguments to the next operation.
     *
     * @param callback The operation callbacks. May be null.
     * @param ops The set of operations to run.
     */
    public void dispatch(OpCallback callback, Op... ops) {
        OpQueue opQueue = new OpQueue(ops);
        if (callback != null) {
            mCallbacks.put(opQueue, callback);
            callback.onOperationStarted();
        }
        internalDispatch(opQueue);
    }

    public void removeCallback(OpCallback callback) {
        while (mCallbacks.values().remove(callback));
    }

    /**
     * Shutdown all event handlers owned by the dispatcher. Blocks until they quit.
     */
    public void shutdown() {
        // Shut down event handler ASAP.
        Message msg = mEventHandler.obtainMessage(MESSAGE_STOP);
        mEventHandler.sendMessageAtFrontOfQueue(msg);

        msg = mHttpHandler.obtainMessage(MESSAGE_STOP);
        mHttpHandler.sendMessageAtFrontOfQueue(msg);

        msg = mDbHandler.obtainMessage(MESSAGE_STOP);
        mDbHandler.sendMessageAtFrontOfQueue(msg);

        // Wait for network and database handlers to quit.
        try {
            mThreadsQuitSignal.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException("Interrupted while waiting for work threads to start.", e);
        }
    }

    public void internalDispatch(OpQueue opQueue) {
        Message msg = mEventHandler.obtainMessage(MESSAGE_DISPATCH, opQueue);
        msg.sendToTarget();
    }

    private class EventHandler extends DispatchHandler {

        private boolean mStopped;

        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override
        protected void dispatch(OpQueue opsAndArg) {
            if (mStopped) {
                return;
            }

            OpCallback callback = mCallbacks.get(opsAndArg);
            Exception exception = opsAndArg.getLastResult() instanceof Exception ?
                    (Exception) opsAndArg.getLastResult() : null;
            if (opsAndArg.isEmpty() || exception != null) {
                if (callback != null) {
                    callback.onOperationFinished(exception);
                }
                mCallbacks.remove(opsAndArg);
                return;
            }

            Op op = opsAndArg.peekFront();
            if (op instanceof DbOp) {
                mDbHandler.obtainMessage(MESSAGE_DISPATCH, opsAndArg)
                        .sendToTarget();
            } else if (op instanceof HttpOp) {
                mHttpHandler.obtainMessage(MESSAGE_DISPATCH, opsAndArg)
                        .sendToTarget();
            } else if (op instanceof DispatchResultOp) {
                DispatchResultOp dispatchOp = (DispatchResultOp) opsAndArg.popFront();
                Log.d("Running dispatch operation " + dispatchOp);
                Object result = dispatchOp.run(callback, opsAndArg.getLastResult());
                opsAndArg.setLastResult(result);
                Log.d("Finished dispatch operation " + dispatchOp);
                internalDispatch(opsAndArg);
            } else {
                throw new IllegalArgumentException("Unhandled operation type: " + op.getClass());
            }
        }

        @Override
        protected void onQuit() {
            Log.d("Shutting down event handler.");
            mStopped = true;
        }

    }

    private class HttpHandler extends DispatchHandler {

        private OperationHttpClient mHttpClient;

        public HttpHandler(Looper looper) {
            super(looper);
        }

        @Override
        protected void dispatch(final OpQueue opsAndArg) {
            if (mHttpClient == null) {
                mHttpClient = new OperationHttpClient();
            }
            HttpOp op = (HttpOp) opsAndArg.peekFront();
            try {
                Log.d("Running network operation " + op);
                Object result = op.run(mHttpClient, opsAndArg.getLastResult());

                //If the League of Legends api us told to wait for a certain amount of time
                //because of api limit restrictions, then wait that amount of time before
                //retrying this same operation again.
                //The response code that determines the rate limit was exceeded is 429
                //and the response contains a header named "Retry-After" with value
                //in number of seconds to wait.
                int timeToWaitInSecs = 0;
                if (result instanceof JSONResponseHandler.Response) {
                    JSONResponseHandler.Response response = (JSONResponseHandler.Response)result;
                    if (response.statusCode == 429) {
                        //Retry after given amount of time
                        try {
                            response.mOkHttpResponse.header("Retry-After", "10");
                        }
                        catch (Exception e) {
                            //Something went wrong with finding the amount of time to wait,
                            //but since a 429 response was received let's just wait for
                            //10 seconds to be safe.
                            timeToWaitInSecs = 10;
                        }
                    }
                }

                if (timeToWaitInSecs == 0) {
                    Log.d("Finished network operation " + op);

                    //Remove the operation that was just executed from the
                    //queue since it didn't fail according to the API rate limit.
                    opsAndArg.popFront();

                    //Execute next operation
                    opsAndArg.setLastResult(result);
                    internalDispatch(opsAndArg);
                }
                else {
                    Log.d("Network operation rate limit hit " + op);
                    Log.d("Retrying after " + timeToWaitInSecs + " seconds");
                    Toast.makeText(App.getInstance().getApplicationContext(),
                                   "Retrying network operation soon due to rate limit",
                                   Toast.LENGTH_LONG)
                            .show();

                    //Wait for the Retry-After time
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            internalDispatch(opsAndArg);
                        }
                    }, timeToWaitInSecs * 1000);
                }
            } catch (Exception e) {
                Log.e(op + " failed: ", e);
                Log.e(op + " arg was: " + opsAndArg.getLastResult());
                opsAndArg.setLastResult(e);
                internalDispatch(opsAndArg);
            }
        }

        @Override
        protected void onQuit() {
            Looper.myLooper().quit();
            if (mHttpClient != null) {
                //mHttpClient.close();
                mHttpClient = null;
            }
            Log.d("Closed HTTP client.");
            mThreadsQuitSignal.countDown();
        }

    }

    private class DbHandler extends DispatchHandler {

        private SQLiteDatabase mDb;

        public DbHandler(Looper looper) {
            super(looper);
        }

        @Override
        protected void dispatch(OpQueue opsAndArg) {
            if (mDb == null) {
                mDb = mDbFactory.openDatabase();
            }

            DbOp op = (DbOp) opsAndArg.popFront();
            try {
                Log.d("Running database operation " + op);
                Object result = op.run(mDb, opsAndArg.getLastResult());
                opsAndArg.setLastResult(result);
                Log.d("Finished database operation " + op);
                internalDispatch(opsAndArg);
            } catch (Exception e) {
                Log.e(op + " failed: ", e);
                Log.e(op + " arg was: " + opsAndArg.getLastResult());
                opsAndArg.setLastResult(e);
                internalDispatch(opsAndArg);
            } finally {
            }
        }

        @Override
        protected void onQuit() {
            Looper.myLooper().quit();
            mThreadsQuitSignal.countDown();
        }

    }

    private abstract class DispatchHandler extends Handler {

        public DispatchHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DISPATCH:
                    dispatch((OpQueue) msg.obj);
                    break;
                case MESSAGE_STOP:
                    onQuit();
                    break;
            }
        }

        protected abstract void dispatch(OpQueue opsAndArg);

        protected abstract void onQuit();

    }

    private class WorkThread extends HandlerThread {

        public WorkThread(String name) {
            super(name, android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);
        }

        @Override
        protected void onLooperPrepared() {
            mThreadsReadySignal.countDown();
        }

    }

    private static class OpQueue {

        private final Queue<Op> mOps;
        private Object mLastResult;

        public OpQueue(Op... ops) {
            mOps = new LinkedList<Op>(Arrays.asList(ops));
        }

        public synchronized Object getLastResult() {
            return mLastResult;
        }

        public synchronized void setLastResult(Object result) {
            mLastResult = result;
        }

        public synchronized boolean isEmpty() {
            return mOps.isEmpty();
        }

        public synchronized Op popFront() {
            return mOps.remove();
        }

        public synchronized Op peekFront() {
            return mOps.peek();
        }

    }

}
