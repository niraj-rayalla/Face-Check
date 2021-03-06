package com.lazerpower.facecheck.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.lazerpower.facecheck.App;
import com.lazerpower.facecheck.Log;
import com.lazerpower.facecheck.R;
import com.lazerpower.facecheck.dispatcher.entity.Match;
import com.lazerpower.facecheck.dispatcher.ops.DispatchResultOp;
import com.lazerpower.facecheck.dispatcher.ops.EmptyOpCallback;
import com.lazerpower.facecheck.dispatcher.ops.EntityParseOp;
import com.lazerpower.facecheck.dispatcher.ops.HttpGetOp;
import com.lazerpower.facecheck.http.ApiPaths;
import com.lazerpower.facecheck.http.Param;
import com.lazerpower.facecheck.models.BucketedTimeline;
import com.lazerpower.facecheck.models.MatchTimeKeeper;
import com.lazerpower.facecheck.ops.GetMatch;
import com.lazerpower.facecheck.utils.TimeUtils;
import com.lazerpower.facecheck.views.EndGameTeamStatsView;
import com.lazerpower.facecheck.views.LiveTeamView;
import com.lazerpower.facecheck.views.MapTimelineView;

import org.json.JSONArray;

/**
 * Created by Niraj what on 4/14/2015.
 */
public class MatchDetailsActivity extends Activity {
    private static final String EXTRA_MATCH_ID = "extra_match_id";

    public static Intent getIntent(Context context, String matchId) {
        Intent intent = new Intent(context, MatchDetailsActivity.class);

        intent.putExtra(EXTRA_MATCH_ID, matchId);

        return intent;
    }

    private LiveTeamView mBlueTeamLiveView;
    private MapTimelineView mMapTimelineView;
    private LiveTeamView mRedTeamLiveView;
    private EndGameTeamStatsView mBlueFinalStats;
    private EndGameTeamStatsView mRedFinalStats;

    private BucketedTimeline mBucketedTimeline;
    private MatchTimeKeeper mMatchTimeKeeper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        String matchId = getIntent().getStringExtra(EXTRA_MATCH_ID);

        mBlueTeamLiveView = (LiveTeamView)findViewById(R.id.blue_team_live_view);
        mMapTimelineView = (MapTimelineView)findViewById(R.id.map_timeline_view);
        mRedTeamLiveView = (LiveTeamView)findViewById(R.id.red_team_live_view);
        mBlueFinalStats = (EndGameTeamStatsView)findViewById(R.id.blue_team_end_stats);
        mRedFinalStats = (EndGameTeamStatsView)findViewById(R.id.red_team_end_stats);

        App.getInstance().getDispatcher().dispatch(
                new EmptyOpCallback() {
                    @Override
                    public void onOperationResultChanged(Object result) {
                        super.onOperationResultChanged(result);

                        Match.MatchModel matchModel = (Match.MatchModel) result;
                        mMapTimelineView.setMatch(matchModel);
                        mBlueFinalStats.setMatch(matchModel, matchModel.getTeams()[0]);
                        mRedFinalStats.setMatch(matchModel, matchModel.getTeams()[1]);

                        int blueTeamId = matchModel.getTeams()[0].mTeamId;
                        int blueIndex = 0;
                        Match.MatchModel.Participant[] blueParticipants = new Match.MatchModel.Participant[5];
                        int redTeamId = matchModel.getTeams()[1].mTeamId;
                        int redIndex = 0;
                        Match.MatchModel.Participant[] redParticipants = new Match.MatchModel.Participant[5];

                        Match.MatchModel.Participant[] allParticipants = matchModel.getParticipants();
                        for (Match.MatchModel.Participant participant : allParticipants) {
                            if (participant.mTeamId == blueTeamId) {
                                blueParticipants[blueIndex++] = participant;
                            }
                            else {
                                redParticipants[redIndex++] = participant;
                            }
                        }

                        mBlueTeamLiveView.setParticipants(blueParticipants);
                        mRedTeamLiveView.setParticipants(redParticipants);

                        mBucketedTimeline = new BucketedTimeline(matchModel);
                        mMatchTimeKeeper = new MatchDetailsTimeKeeper(matchModel, mBucketedTimeline);

                        mMapTimelineView.setTimeKeeper(mMatchTimeKeeper);
                    }
                },
                /*new HttpGetOp(ApiPaths.getMatchPath(matchId),
                        Param.withKeysAndValues("includeTimeline", "true")),
                new EntityParseOp(new Match()),*/
                new GetMatch(matchId),
                new DispatchResultOp()
        );
    }

    public class MatchDetailsTimeKeeper extends MatchTimeKeeper {
        private static final int TIMER_INTERVAL_DURATION = 250;

        private CountDownTimer mCountDownTimer;

        public MatchDetailsTimeKeeper(Match.MatchModel matchModel, BucketedTimeline bucketedTimeline) {
            super(matchModel, bucketedTimeline);
            startTiming();
        }

        @Override
        public void startTiming() {
            stopTiming();

            long timeRemainingTillEnd = mMatchModel.getMatchDurationInSeconds()*1000 - getCurrentTime();
            mCountDownTimer = new CountDownTimer(timeRemainingTillEnd, TIMER_INTERVAL_DURATION) {
                @Override
                public void onTick(long millisUntilFinished) {
                    setCurrentTime(getCurrentTime() + TIMER_INTERVAL_DURATION);
                    refreshTimedViews();
                }

                @Override
                public void onFinish() {

                }
            }.start();

            refreshTimedViews();
        }

        @Override
        public void stopTiming() {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
        }

        private void refreshTimedViews() {
            mBlueTeamLiveView.setCurrentTime(mBucketedTimeline, getCurrentTime());
            mRedTeamLiveView.setCurrentTime(mBucketedTimeline, getCurrentTime());
            mMapTimelineView.setCurrentTime(getCurrentTime());
        }
    }
}
