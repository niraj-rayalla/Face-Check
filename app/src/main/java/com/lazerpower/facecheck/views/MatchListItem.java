package com.lazerpower.facecheck.views;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lazerpower.facecheck.R;
import com.lazerpower.facecheck.api.ApiHelper;
import com.lazerpower.facecheck.dispatcher.entity.Champion;
import com.lazerpower.facecheck.dispatcher.entity.Match;
import com.lazerpower.facecheck.dispatcher.ops.EmptyOpCallback;
import com.lazerpower.facecheck.utils.PicassoOkHttp;

public class MatchListItem extends RelativeLayout {

    private RecyclerView mMatchList;

    private ImageView mMVP;
    private TextView mTime;

    private TextView mBlueKDA;
    private TextView mBlueGold;
    private TextView mBlueTower;
    private TextView mBlueDragon;
    private TextView mBlueBarron;
    private ImageView mBlueWin;

    private TextView mRedKDA;
    private TextView mRedGold;
    private TextView mRedTower;
    private TextView mRedDragon;
    private TextView mRedBarron;
    private ImageView mRedWin;


    public MatchListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_match_item, this, true);

        mMVP = (ImageView)findViewById(R.id.mvp_image);
        mTime = (TextView)findViewById(R.id.game_time);

        mBlueKDA = (TextView)findViewById(R.id.blue_kda);
        mBlueGold = (TextView)findViewById(R.id.blue_gold);
        mBlueTower = (TextView)findViewById(R.id.blue_tower);
        mBlueDragon = (TextView)findViewById(R.id.blue_dragon);
        mBlueBarron = (TextView)findViewById(R.id.blue_barron);
        mBlueWin = (ImageView)findViewById(R.id.blue_win);

        mRedKDA = (TextView)findViewById(R.id.red_kda);
        mRedGold = (TextView)findViewById(R.id.red_gold);
        mRedTower = (TextView)findViewById(R.id.red_tower);
        mRedDragon = (TextView)findViewById(R.id.red_dragon);
        mRedBarron = (TextView)findViewById(R.id.red_barron);
        mRedWin = (ImageView)findViewById(R.id.red_win);
    }

    public void setMatch(Match.MatchModel game){

      //  String mGamelength;
        int mGameMinutes;
        int mGameSeconds;

        int BlueMoney = 0;
        int BlueKill = 0;
        int BlueDeath = 0;
        int BlueAssist = 0;

        int RedMoney = 0;
        int RedKill = 0;
        int RedDeath = 0;
        int RedAssist = 0;

        ApiHelper.getChampion(game.getMVP().mChampionId, new EmptyOpCallback() {
            @Override
            public void onOperationResultChanged(Object result) {
                super.onOperationResultChanged(result);

                Champion.ChampionModel championModel = (Champion.ChampionModel) result;

                PicassoOkHttp.getRequest(getContext(), Uri.parse(championModel.getImageUrl()))
                        .into(mMVP);
            }
        });

        if(game.getTeams()[0].mWinner)
        {
            mBlueWin.setVisibility(View.VISIBLE);
            mRedWin.setVisibility(View.INVISIBLE);
        }
        else
        {
            mRedWin.setVisibility(View.VISIBLE);
            mBlueWin.setVisibility(View.INVISIBLE);
        }

        mGameMinutes = game.getMatchDurationInSeconds()/60;
        mGameSeconds = game.getMatchDurationInSeconds()%60;
        mTime.setText(mGameMinutes + ":" + mGameSeconds);

        mBlueTower.setText("Towers: " + Integer.toString(game.getTeams()[0].mTowerKills));
        mBlueDragon.setText("Dragons: "+ Integer.toString(game.getTeams()[0].mDragonKills));
        mBlueBarron.setText("Barons: "+ Integer.toString(game.getTeams()[0].mBaronKills));

        mRedTower.setText("Towers: "+ game.getTeams()[1].mTowerKills);
        mRedDragon.setText("Dragons: "+ game.getTeams()[1].mDragonKills);
        mRedBarron.setText("Barons: "+game.getTeams()[1].mBaronKills);


        //Gold for the teams
        for(int i = 0; i < game.getParticipants().length; i++)
        {
            if(game.getParticipants()[i].mTeamId == game.getTeams()[0].mTeamId)
            {
                BlueMoney = BlueMoney + game.getParticipants()[i].mStats.mGoldEarn;
                BlueKill = BlueKill + game.getParticipants()[i].mStats.mKills;
                BlueDeath = BlueDeath + game.getParticipants()[i].mStats.mDeaths;
                BlueAssist = BlueAssist + game.getParticipants()[i].mStats.mAssists;
            }
            else{
                RedMoney = RedMoney + game.getParticipants()[i].mStats.mGoldEarn;
                RedKill = RedKill + game.getParticipants()[i].mStats.mKills;
                RedDeath = RedDeath + game.getParticipants()[i].mStats.mDeaths;
                RedAssist = RedAssist + game.getParticipants()[i].mStats.mAssists;
            }
        }

        mBlueKDA.setText(BlueKill+"/"+BlueDeath+"/"+BlueAssist);
        mBlueGold.setText(BlueMoney+"g");

        mRedKDA.setText(RedKill+"/"+RedDeath+"/"+RedAssist);
        mRedGold.setText(RedMoney+"g");

    }
}
