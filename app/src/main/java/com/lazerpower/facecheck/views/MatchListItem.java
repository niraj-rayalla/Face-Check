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
import android.widget.RelativeLayout;

import com.lazerpower.facecheck.R;

public class MatchListItem extends RelativeLayout {

    private RecyclerView mMatchList;

    public MatchListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
