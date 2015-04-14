package com.lazerpower.facecheck.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.lazerpower.facecheck.dispatcher.entity.Match;

/**
 * Created by Niraj on 4/11/2015.
 */
public class MapTimelineView extends View {

    private Match.MatchModel mMatch;

    public MapTimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setMatch(Match.MatchModel match) {
        mMatch = match;

        
    }
}
