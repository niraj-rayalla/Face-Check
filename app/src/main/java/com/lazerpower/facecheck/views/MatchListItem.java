package com.lazerpower.facecheck.views;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lazerpower.facecheck.R;

import org.w3c.dom.Text;

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
}
