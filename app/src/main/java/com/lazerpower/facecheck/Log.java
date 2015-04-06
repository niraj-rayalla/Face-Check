package com.lazerpower.facecheck;

import android.text.format.DateFormat;

/**
 * Created by Niraj on 4/5/2015.
 */
public class Log {

    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = android.util.Log.VERBOSE;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = android.util.Log.DEBUG;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = android.util.Log.INFO;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = android.util.Log.WARN;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = android.util.Log.ERROR;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = android.util.Log.ASSERT;

    private static final String LOG_TAG = "FaceCheck";

    /**
     * Checks to see whether or not a log for the specified tag is loggable at
     * the specified level.
     *
     * The default level of any tag is set to INFO. This means that any level
     * above and including INFO will be logged. Before you make any calls to a
     * logging method you should check to see if your tag should be logged. You
     * can change the level by changing the value of Features.LOG_LEVEL.
     * LOG_LEVEL is either VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT, or
     * SUPPRESS. SUPPRESS will turn off all logging for your tag.
     *
     * @param level The level to check.
     * @return Whether or not that this is allowed to be logged.
     */
    public static boolean isLoggable(int level) {
        if (BuildConfig.DEBUG) {
            return true;
        }
        else {
            //Only if error
            return level >= ERROR;
        }
    }

    /**
     * Send a {@link #VERBOSE} log message.
     * @param msg The message you would like logged.
     */
    public static void v(String msg) {
        log(VERBOSE, null, msg, null);
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void v(String msg, Throwable tr) {
        log(VERBOSE, null, msg, tr);
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     * @param reporter The class doing the reporting.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static <T> void v(T reporter, String msg, Throwable tr) {
        log(VERBOSE, reporter, msg, tr);
    }

    /**
     * Send a {@link #DEBUG} log message.
     * @param msg The message you would like logged.
     */
    public static void d(String msg) {
        log(DEBUG, null, msg, null);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void d(String msg, Throwable tr) {
        log(DEBUG, null, msg, tr);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     * @param reporter The class doing the reporting.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static <T> void d(T reporter, String msg, Throwable tr) {
        log(DEBUG, reporter, msg, tr);
    }

    /**
     * Send an {@link #INFO} log message.
     * @param msg The message you would like logged.
     */
    public static void i(String msg) {
        log(INFO, null, msg, null);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void i(String msg, Throwable tr) {
        log(INFO, null, msg, tr);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     * @param reporter The class doing the reporting.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static <T> void i(T reporter, String msg, Throwable tr) {
        log(INFO, reporter, msg, tr);
    }

    /**
     * Send a {@link #WARN} log message.
     * @param msg The message you would like logged.
     */
    public static void w(String msg) {
        log(WARN, null, msg, null);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void w(String msg, Throwable tr) {
        log(WARN, null, msg, tr);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     * @param reporter The class doing the reporting.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static <T> void w(T reporter, String msg, Throwable tr) {
        log(WARN, reporter, msg, tr);
    }

    /**
     * Send an {@link #ERROR} log message.
     * @param msg The message you would like logged.
     */
    public static void e(String msg) {
        log(ERROR, null, msg, null);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void e(String msg, Throwable tr) {
        log(ERROR, null, msg, tr);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     * @param reporter The class doing the reporting.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static <T> void e(T reporter, String msg, Throwable tr) {
        log(ERROR, reporter, msg, tr);
    }

    /**
     * What a Terrible Failure: Report a condition that should never happen.
     * The error will always be logged at level ASSERT with the call stack.
     * Depending on system configuration, a report may be added to the
     * {@link android.os.DropBoxManager} and/or the process may be terminated
     * immediately with an error dialog.
     * @param msg The message you would like logged.
     */
    public static void wtf(String msg) {
        android.util.Log.wtf(LOG_TAG, msg);
    }

    /**
     * What a Terrible Failure: Report an exception that should never happen.
     * Similar to {@link #wtf(String, Throwable)}, with a message as well.
     * @param msg The message you would like logged.
     * @param tr An exception to log.  May be null.
     */
    public static void wtf(String msg, Throwable tr) {
        android.util.Log.wtf(LOG_TAG, msg, tr);
    }

    /**
     * Generic logging method.
     *
     * @param level The logging level, one of {@link #ASSERT}, {@link #ERROR},
     *              {@link #WARN}, {@link #INFO}, {@link #DEBUG},
     *              {@link #VERBOSE}.
     * @param reporter The class performing the reporting. May be null.
     * @param msg The message you would like logged.
     * @param tr An exception to log. May be null.
     */
    public static <T> void log(int level, T reporter, String msg, Throwable tr) {
        if (isLoggable(level)) {
            String tag = LOG_TAG;
            if (reporter != null) {
                tag = tag + '.' + reporter.getClass().getSimpleName();
            }

            msg = DateFormat.format("[yyyy-MM-ddTkk:mm:ss] ", System.currentTimeMillis()) + msg;
            if (tr != null) {
                msg = msg + '\n' + android.util.Log.getStackTraceString(tr);
            }

            android.util.Log.println(level, tag, msg);
        }
    }

    private Log() {}
}
