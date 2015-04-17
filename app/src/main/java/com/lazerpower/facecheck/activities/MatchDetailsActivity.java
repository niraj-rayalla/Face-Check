package com.lazerpower.facecheck.activities;

import android.app.Activity;
import android.os.Bundle;

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
import com.lazerpower.facecheck.ops.GetMatch;
import com.lazerpower.facecheck.utils.TimeUtils;
import com.lazerpower.facecheck.views.LiveTeamView;
import com.lazerpower.facecheck.views.MapTimelineView;

import org.json.JSONArray;

/**
 * Created by Niraj on 4/14/2015.
 */
public class MatchDetailsActivity extends Activity {

    private LiveTeamView mBlueTeamLiveView;
    private MapTimelineView mMapTimelineView;
    private LiveTeamView mRedTeamLiveView;

    private BucketedTimeline mBucketedTimeline;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        mBlueTeamLiveView = (LiveTeamView)findViewById(R.id.blue_team_live_view);
        mMapTimelineView = (MapTimelineView)findViewById(R.id.map_timeline_view);
        mRedTeamLiveView = (LiveTeamView)findViewById(R.id.red_team_live_view);

        App.getInstance().getDispatcher().dispatch(
                new EmptyOpCallback() {
                    @Override
                    public void onOperationResultChanged(Object result) {
                        JSONArray gameIds = (JSONArray)result;

                        try {
                            String matchId = gameIds.getString(0);
                            App.getInstance().getDispatcher().dispatch(
                                    new EmptyOpCallback() {
                                        @Override
                                        public void onOperationResultChanged(Object result) {
                                            super.onOperationResultChanged(result);

                                            Match.MatchModel matchModel = (Match.MatchModel) result;
                                            mMapTimelineView.setMatch(matchModel);

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

                                            mBlueTeamLiveView.setCurrentTime(mBucketedTimeline, 1360000);
                                            mRedTeamLiveView.setCurrentTime(mBucketedTimeline, 1360000);
                                            mMapTimelineView.setCurrentTime(1360000);
                                        }
                                    },
                                    new HttpGetOp(ApiPaths.getMatchPath(matchId),
                                            Param.withKeysAndValues("includeTimeline", "true")),
                                    new EntityParseOp(new Match()),
                                    new GetMatch(matchId),
                                    new DispatchResultOp()
                            );
                        }
                        catch (Exception e) {
                            Log.d("Could not get match", e);
                        }
                    }
                },
                new HttpGetOp(ApiPaths.getApiChallengePath(),
                        Param.withKeysAndValues("beginDate", Long.toString(TimeUtils.getLastFullFiveMinEpoch()))),
                new DispatchResultOp()
        );
    }
}
