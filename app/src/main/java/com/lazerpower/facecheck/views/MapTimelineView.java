package com.lazerpower.facecheck.views;

import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lazerpower.facecheck.App;
import com.lazerpower.facecheck.api.ApiHelper;
import com.lazerpower.facecheck.dispatcher.entity.Map;
import com.lazerpower.facecheck.dispatcher.entity.Match;
import com.lazerpower.facecheck.dispatcher.ops.EmptyOpCallback;
import com.lazerpower.facecheck.utils.PicassoOkHttp;

/**
 * Created by Niraj on 4/11/2015.
 */
public class MapTimelineView extends FrameLayout {

    private ImageView mMapView;

    private Match.MatchModel mMatch;
    private Map.MapModel mMap;

    public MapTimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutParams matchParentLayoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        mMapView = new ImageView(context);
        mMapView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(mMapView, matchParentLayoutParams);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setMatch(Match.MatchModel match) {
        mMatch = match;

        ApiHelper.getMap(match.getMapId(), new EmptyOpCallback() {
            @Override
            public void onOperationResultChanged(Object result) {
                super.onOperationResultChanged(result);

                mMap = (Map.MapModel)result;

                PicassoOkHttp.getRequest(getContext(), Uri.parse(mMap.getImageUrl()))
                        .into(mMapView);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Make sure the dimensions make a square and fit inside the given size
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width;

        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }
}
