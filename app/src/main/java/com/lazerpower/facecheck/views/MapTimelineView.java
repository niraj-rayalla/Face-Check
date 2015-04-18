package com.lazerpower.facecheck.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lazerpower.facecheck.App;
import com.lazerpower.facecheck.R;
import com.lazerpower.facecheck.api.ApiHelper;
import com.lazerpower.facecheck.dispatcher.entity.Map;
import com.lazerpower.facecheck.dispatcher.entity.Match;
import com.lazerpower.facecheck.dispatcher.ops.EmptyOpCallback;
import com.lazerpower.facecheck.models.BucketedTimeline;
import com.lazerpower.facecheck.models.MatchTimeKeeper;
import com.lazerpower.facecheck.utils.PicassoOkHttp;
import com.lazerpower.facecheck.utils.TimeUtils;

/**
 * Created by Niraj on 4/11/2015.
 */
public class MapTimelineView extends FrameLayout {

    private ImageView mMapView;
    private DeathsTimelineView mDeathsTimelineView;
    private CustomSeekBar mSeekBar;
    private TextView mCurrentTimeView;

    private Match.MatchModel mMatch;
    private Map.MapModel mMap;

    private int mCurrentTimeStamp;

    private Bitmap mDeathIndicatorBitmap;
    private float mDeathIndicatorHalfWidth;
    private float mDeathIndicatorHalfHeight;

    private MatchTimeKeeper mTimeKeeper;

    public MapTimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);

        int normalSpacing = context.getResources().getDimensionPixelSize(R.dimen.normal_spacing);
        int verySmallSpacing = context.getResources().getDimensionPixelSize(R.dimen.very_small_spacing);

        LayoutParams matchParentLayoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        mMapView = new ImageView(context);
        mMapView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(mMapView, matchParentLayoutParams);

        mDeathsTimelineView = new DeathsTimelineView(context);
        addView(mDeathsTimelineView, matchParentLayoutParams);

        mSeekBar = new CustomSeekBar(context, null);
        LayoutParams seekBarLayoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        seekBarLayoutParams.leftMargin = normalSpacing;
        seekBarLayoutParams.rightMargin = normalSpacing;
        seekBarLayoutParams.gravity = Gravity.BOTTOM;
        addView(mSeekBar, seekBarLayoutParams);

        //Current time view
        mCurrentTimeView = new TextView(context);
        mCurrentTimeView.setPadding(verySmallSpacing, 0, verySmallSpacing, 0);
        mCurrentTimeView.setBackgroundColor(context.getResources().getColor(R.color.translucent_black));
        LayoutParams timeViewLayoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        timeViewLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        addView(mCurrentTimeView, timeViewLayoutParams);
        mCurrentTimeView.setText("00:00");

        mSeekBar.setListener(new CustomSeekBar.OnCustomSeekBarSeekChanged() {
            @Override
            public void OnSeekDown() {
                //Do nothing
            }

            @Override
            public void OnSeekChanged(float progress) {
                if (mTimeKeeper != null) {
                    mTimeKeeper.setCurrentTime((int)progress);
                }
            }

            @Override
            public void OnSeekUp() {
                if (mTimeKeeper != null) {
                    mTimeKeeper.startTiming();
                }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        Bitmap originalDeathIndicatorBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.death_indicator);
        int destWidth = getContext().getResources().getDimensionPixelSize(R.dimen.death_indicator_width);
        float scaleFactor = (float)destWidth/(float)originalDeathIndicatorBitmap.getWidth();
        int destHeight = (int)(originalDeathIndicatorBitmap.getHeight()*scaleFactor);
        //Scale the image so that the width will be "R.dimen.death_indicator_radius" pixels wide
        mDeathIndicatorBitmap = Bitmap.createScaledBitmap(originalDeathIndicatorBitmap,
                destWidth, destHeight, false);
        originalDeathIndicatorBitmap.recycle();

        mDeathIndicatorHalfWidth = destWidth*0.5f;
        mDeathIndicatorHalfHeight = destHeight*0.5f;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mDeathIndicatorBitmap != null) {
            mDeathIndicatorBitmap.recycle();
            mDeathIndicatorBitmap = null;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Make sure the dimensions make a square and fit inside the given size
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width;

        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    public void setMatch(Match.MatchModel match) {
        mMatch = match;
        mSeekBar.setMaxValue(mMatch.getMatchDurationInSeconds()*1000);

        ApiHelper.getMap(match.getMapId(), new EmptyOpCallback() {
            @Override
            public void onOperationResultChanged(Object result) {
                super.onOperationResultChanged(result);

                mMap = (Map.MapModel) result;

                PicassoOkHttp.getRequest(getContext(), Uri.parse(mMap.getImageUrl()))
                        .into(mMapView);
            }
        });
    }

    public void setTimeKeeper(MatchTimeKeeper timeKeeper) {
        mTimeKeeper = timeKeeper;
    }

    public void setCurrentTime(int currentTimestamp) {
        mCurrentTimeStamp = currentTimestamp;
        mSeekBar.setProgress(mCurrentTimeStamp);

        mCurrentTimeView.setText(TimeUtils.getTimeElapsedString(mCurrentTimeStamp));

        refreshTimelineViews();
    }

    private void refreshTimelineViews() {
        mDeathsTimelineView.invalidate();
    }

    private class DeathsTimelineView extends View {

        public DeathsTimelineView(Context context) {
            super(context);

            setWillNotDraw(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (mMatch == null || mMatch.getTimeline() == null) {
                return;
            }
            //Draw all deaths that occurred in the match up to current time
            Match.MatchModel.TimelineFrame[] timelineFrames = mMatch.getTimeline().mFrames;
            if (timelineFrames != null) {
                for (Match.MatchModel.TimelineFrame timelineFrame : timelineFrames) {
                    if (timelineFrame.mTimestamp < mCurrentTimeStamp) {
                        drawDeathsInTimelineFrame(canvas, timelineFrame);
                    }
                    else {
                        //This timeline frame might have some events that are within the current timestamp
                        drawDeathsInTimelineFrame(canvas, timelineFrame);
                        //All events in the rest of the timeline frames would be after the current timestamp
                        //so no point of checking
                        break;
                    }
                }
            }
        }

        private void drawDeathsInTimelineFrame(Canvas canvas, Match.MatchModel.TimelineFrame timelineFrame) {
            Match.MatchModel.Event[] events = timelineFrame.mEvents;
            if (events != null) {
                for (Match.MatchModel.Event event : events) {
                    if (event != null
                        && event.mTimestamp <= mCurrentTimeStamp
                        && event instanceof Match.MatchModel.ChampionKillEvent) {
                        Match.MatchModel.ChampionKillEvent championKillEvent =
                                (Match.MatchModel.ChampionKillEvent)event;

                        if (championKillEvent.mPosition != null) {
                            //Draw the kill location
                            Pair<Float, Float> deathCanvasPos =
                                    Map.MapModel.getCanvasPositionFromInGamePosition(
                                            mMatch.getMapId(), canvas, championKillEvent.mPosition);
                                    if (mDeathIndicatorBitmap != null) {
                                        canvas.drawBitmap(
                                                mDeathIndicatorBitmap,
                                                deathCanvasPos.first - mDeathIndicatorHalfWidth,
                                                deathCanvasPos.second - mDeathIndicatorHalfHeight,
                                                null);
                                    }
                        }
                    }
                }
            }
        }
    }
}
