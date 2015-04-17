package com.lazerpower.facecheck.models;

import android.util.Pair;

import com.lazerpower.facecheck.Log;
import com.lazerpower.facecheck.dispatcher.entity.Match;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Niraj on 4/16/2015.
 */
public class BucketedTimeline {
    private static final int BUCKET_DURATION = 30000;

    private Match.MatchModel.Timeline mTimeline;

    private HashMap<Integer, TimeBucketedObjects<Match.MatchModel.ParticipantFrame>> mBucketedParticipantFrames;
    private HashMap<Integer, TimeBucketedObjects<LinkedList<Integer>>> mBucketedItemFrames;

    public BucketedTimeline(Match.MatchModel matchModel) {
        mTimeline = matchModel.getTimeline();

        setupParticipantFrames(matchModel);

        setupItemFrames(matchModel);
    }

    private void setupParticipantFrames(Match.MatchModel matchModel) {
        Match.MatchModel.Participant[] participants = matchModel.getParticipants();
        mBucketedParticipantFrames = new HashMap<Integer, TimeBucketedObjects<Match.MatchModel.ParticipantFrame>>();
        for (int i = 0; i < participants.length; ++i) {
            mBucketedParticipantFrames.put(
                    participants[i].mParticipantId,
                    new TimeBucketedObjects<Match.MatchModel.ParticipantFrame>(matchModel, BUCKET_DURATION));
        }

        Match.MatchModel.TimelineFrame[] timelineFrames = matchModel.getTimeline().mFrames;
        for (Match.MatchModel.TimelineFrame timelineFrame : timelineFrames) {
            for (Match.MatchModel.ParticipantFrame participantFrame : timelineFrame.mParticipantFramesArray) {
                mBucketedParticipantFrames.get(participantFrame.mId).addObject(timelineFrame.mTimestamp, participantFrame);
            }
        }
    }

    private void setupItemFrames(Match.MatchModel matchModel) {
        Match.MatchModel.Participant[] participants = matchModel.getParticipants();
        mBucketedItemFrames = new HashMap<Integer, TimeBucketedObjects<LinkedList<Integer>>>();
        for (int i = 0; i < participants.length; ++i) {
            TimeBucketedObjects<LinkedList<Integer>> bucket = new TimeBucketedObjects<LinkedList<Integer>>(matchModel, BUCKET_DURATION);
            mBucketedItemFrames.put(participants[i].mParticipantId, bucket);
            //Start with no items
            bucket.addObject(0, new LinkedList<Integer>());
        }

        Match.MatchModel.TimelineFrame[] timelineFrames = matchModel.getTimeline().mFrames;
        for (Match.MatchModel.TimelineFrame timelineFrame : timelineFrames) {
            if (timelineFrame.mEvents != null) {
                for (Match.MatchModel.Event event : timelineFrame.mEvents) {
                    if (event instanceof Match.MatchModel.ItemEvent) {
                        Match.MatchModel.ItemEvent itemEvent = (Match.MatchModel.ItemEvent) event;
                        if (mBucketedItemFrames.get(itemEvent.mParticipantId) == null) {
                            continue;
                        }
                        LinkedList<Integer> currentItemState =
                                mBucketedItemFrames.get(itemEvent.mParticipantId).getLastAddedObject();
                        LinkedList<Integer> newItemsState = new LinkedList<Integer>();

                        //Copy current state to new state and edit the new state
                        for (Integer itemId : currentItemState) {
                            newItemsState.add(itemId);
                        }

                        if (event instanceof Match.MatchModel.ItemUndoEvent) {
                            //Replace item = remove then add
                            Match.MatchModel.ItemUndoEvent itemUndoEvent = (Match.MatchModel.ItemUndoEvent) event;
                            newItemsState.remove((Integer)itemUndoEvent.mItemBefore);
                            newItemsState.add(itemUndoEvent.mItemAfter);
                        } else {
                            if (itemEvent.mEventType.equals(Match.MatchModel.Event.EVENT_TYPE_ITEM_PURCHASED)) {
                                //Add an item because purchased
                                newItemsState.add(itemEvent.mItemId);
                            } else {
                                //Remove an item because destroyed or sold
                                newItemsState.remove((Integer)itemEvent.mItemId);
                            }
                        }
                        mBucketedItemFrames.get(itemEvent.mParticipantId).addObject(itemEvent.mTimestamp, newItemsState);
                    }
                }
            }
        }
    }

    //
    // Getters
    //

    public Match.MatchModel.ParticipantFrame getParticipantFrameForTimestamp(int participantId, int timestamp) {
        TimeBucketedObjects<Match.MatchModel.ParticipantFrame> bucket = mBucketedParticipantFrames.get(participantId);
        if (bucket != null) {
            return bucket.getObjectAtTime(timestamp);
        }

        return null;
    }

    public LinkedList<Integer> getItemsFrameForTimestamp(int participantId, int timestamp) {
        TimeBucketedObjects<LinkedList<Integer>> bucket = mBucketedItemFrames.get(participantId);
        if (bucket != null) {
            return bucket.getObjectAtTime(timestamp);
        }

        return null;
    }

    public static class TimeBucketedObjects<T> {
        private final int mBucketDuration;
        private final int mMatchDuration;
        //Bucketed data
        //One bucket has all objects that happened in mBucketDuration
        //Each pair is the time the object happened and the object itself
        private LinkedList<Pair<Integer, T>>[] mData;

        private T mLastData;

        public TimeBucketedObjects(Match.MatchModel match, int bucketDuration) {
            mBucketDuration = bucketDuration;
            mMatchDuration = match.getMatchDurationInSeconds() * 1000;

            mData = new LinkedList[(int)Math.ceil((double) mMatchDuration / (double) mBucketDuration)];
            for (int i = 0; i < mData.length; ++i) {
                mData[i] = new LinkedList<>();
            }
        }

        public void addObject(int timestamp, T timedObject) {
            int index = timestamp/mMatchDuration;
            mData[index].add(new Pair<Integer, T>(timestamp, timedObject));
            mLastData = timedObject;
        }

        public T getLastAddedObject() {
            return mLastData;
        }

        /**
         * Return the last timed object that occured before the given timestamp
         * @param timestamp
         * @return
         */
        public T getObjectAtTime(int timestamp) {
            int index = timestamp/mMatchDuration;
            if (index < mData.length) {
                LinkedList<Pair<Integer, T>> timedObjects = mData[index];
                if (timedObjects.size() > 0) {
                    Pair<Integer, T> lastTimedObject = null;
                    for (Pair<Integer, T> timedObject : timedObjects) {
                        if (lastTimedObject == null
                            || (timedObject.first <= timestamp && timedObject.first >= lastTimedObject.first)) {
                            lastTimedObject = timedObject;
                        } else {
                            break;
                        }
                    }

                    return lastTimedObject.second;
                }
            }

            return null;
        }
    }
}
