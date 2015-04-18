package com.lazerpower.facecheck.models;

import com.lazerpower.facecheck.dispatcher.entity.Match;

/**
 * Created by Niraj on 4/17/2015.
 */
public abstract class MatchTimeKeeper {

    protected Match.MatchModel mMatchModel;
    protected BucketedTimeline mBucketedTimeline;
    private int mTimestamp = 0;

    public MatchTimeKeeper(Match.MatchModel matchModel, BucketedTimeline bucketedTimeline) {
        mMatchModel = matchModel;
        mBucketedTimeline = bucketedTimeline;
    }

    public void setCurrentTime(int timestamp) {
        mTimestamp = timestamp;
    }

    public int getCurrentTime() {
        return mTimestamp;
    }

    /**
     * Tell views that depend on the time to redraw themselves
     */
    public abstract void startTiming();

    public abstract void stopTiming();
}
