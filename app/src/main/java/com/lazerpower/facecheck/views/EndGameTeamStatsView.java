package com.lazerpower.facecheck.views;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lazerpower.facecheck.R;
import com.lazerpower.facecheck.api.ApiHelper;
import com.lazerpower.facecheck.dispatcher.entity.Champion;
import com.lazerpower.facecheck.dispatcher.entity.Match;
import com.lazerpower.facecheck.dispatcher.ops.EmptyOpCallback;
import com.lazerpower.facecheck.utils.PicassoOkHttp;

/**
 * Created by Brian on 4/17/2015.
 */
public class EndGameTeamStatsView extends LinearLayout {

    private ImageView mMVP;

    private TextView mKDA;
    private TextView mGold;
    private TextView mTower;
    private TextView mDragon;
    private TextView mBarron;
    private TextView mWin;

    public EndGameTeamStatsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_end_game_team_stats, this, true);

        mMVP = (ImageView)findViewById(R.id.team_mvp);

        mKDA = (TextView)findViewById(R.id.team_kda);
        mGold = (TextView)findViewById(R.id.team_gold);
        mTower = (TextView)findViewById(R.id.team_tower);
        mDragon = (TextView)findViewById(R.id.team_dragon);
        mBarron = (TextView)findViewById(R.id.team_baron);
        mWin = (TextView)findViewById(R.id.team_winner);


    }

////////////////////////////

    public void setMatch(Match.MatchModel game, Match.MatchModel.Team team, int teamIndex){



        int Money = 0;
        int Kill = 0;
        int Death = 0;
        int Assist = 0;

        if(team.mWinner)
        {
            mWin.setText("Win");
        }
        else
        {
            mWin.setText("Lose");
        }



        ApiHelper.getChampion(game.getTeamMVP(team.mTeamId).mChampionId, new EmptyOpCallback() {
            @Override
            public void onOperationResultChanged(Object result) {
                super.onOperationResultChanged(result);

                Champion.ChampionModel championModel = (Champion.ChampionModel) result;

                PicassoOkHttp.getRequest(getContext(), Uri.parse(championModel.getImageUrl()))
                        .into(mMVP);
            }
        });



        mTower.setText(game.getTeams()[teamIndex].mTowerKills);
        mDragon.setText(game.getTeams()[teamIndex].mDragonKills);
        mBarron.setText(game.getTeams()[teamIndex].mBaronKills);


        //Gold for the teams
        for(int i = 0; i < game.getParticipants().length; i++)
        {
            if(game.getParticipants()[i].mTeamId == team.mTeamId)
            {
                Money = Money + game.getParticipants()[i].mStats.mGoldEarn;
                Kill = Kill + game.getParticipants()[i].mStats.mKills;
                Death = Death + game.getParticipants()[i].mStats.mDeaths;
                Assist = Assist + game.getParticipants()[i].mStats.mAssists;
            }

        }

        mKDA.setText(Kill+"/"+Death+"/"+Assist);
        mGold.setText("Gold: "+Money+"g");


    }
}
