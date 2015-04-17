package com.lazerpower.facecheck.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lazerpower.facecheck.R;

/**
 * Created by Niraj on 4/17/2015.
 */
public class CustomSeekBar extends FrameLayout implements View.OnTouchListener {
    private static final float UNFOCUSED_ALPHA = 0.5f;
    private static final long FOCUS_ANIMATION_DURATION = 300;

    private float mMaxProgress = 100.0f;
    private float mCurrentProgress = 0.0f;

    private int mMaxWidth;
    private int mViewWidth;

    private View bgView;
    private View progressView;
    private View indicatorView;

    private boolean isSeekIndicatorTouched = false;
    private float xTouch;

    private OnCustomSeekBarSeekChanged mListener = null;

    private float thumbRadius;
    private float barHeight;

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        int indicatorSize = context.getResources().getDimensionPixelSize(R.dimen.custom_seekbar_handle_size);
        barHeight = context.getResources().getDimensionPixelSize(R.dimen.custom_seekbar_background_height);

        thumbRadius = indicatorSize/2;

        bgView = new View(context);
        bgView.setBackgroundResource(R.drawable.custom_seekbar_background);
        addView(bgView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)barHeight));

        progressView = new View(context);
        progressView.setBackgroundResource(R.drawable.custom_seekbar_progress_background);
        addView(progressView, new LayoutParams(0, (int)barHeight));

        indicatorView = new View(context);
        indicatorView.setBackgroundResource(R.drawable.syndra_ball);
        addView(indicatorView, new LayoutParams(indicatorSize, indicatorSize));

        setOnTouchListener(this);

        bgView.setTranslationX(thumbRadius);
        bgView.setTranslationY(thumbRadius - barHeight/2);
        progressView.setTranslationX(thumbRadius);
        progressView.setTranslationY(thumbRadius - barHeight/2);

        setAlpha(UNFOCUSED_ALPHA);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //Check to see if animating otherwise mMaxWidth might get changed to 0 which
        //will cause the progress bar width to always stay at 0.
        mMaxWidth = (int)(w - thumbRadius*2.0f);
        mViewWidth = w;

        bgView.getLayoutParams().width = mMaxWidth;
        bgView.requestLayout();
        setProgress(mCurrentProgress);
    }

    public void setProgress(float progress) {
        if (progress > mMaxProgress) {
            progress = mMaxProgress;
        }
        else if (progress < 0.0f) {
            progress = 0.0f;
        }

        if (mCurrentProgress != progress) {
            mCurrentProgress = progress;
            if (mListener != null) {
                mListener.OnSeekChanged(mCurrentProgress);
            }
        }

        int newWidth = (int)(mCurrentProgress/mMaxProgress * mMaxWidth);
        progressView.getLayoutParams().width = newWidth;
        progressView.requestLayout();
        indicatorView.setTranslationX(newWidth);
    }

    public void setMaxValue(float value) {
        mCurrentProgress = mCurrentProgress / mMaxProgress * value;
        mMaxProgress = value;
    }

    private float getProgressWithThumbPadding(float newXTouch) {
        if (newXTouch < thumbRadius) {
            return 0.0f;
        }
        else if (newXTouch > mViewWidth - thumbRadius) {
            return mMaxProgress;
        }
        else {
            return (newXTouch-thumbRadius) / mMaxWidth * mMaxProgress;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
            {
                if (isSeekIndicatorTouched) {
                    //Don't want more than one finger manipulating the seekbar
                    return false;
                }
                isSeekIndicatorTouched = true;
                xTouch = motionEvent.getX();

                if (mListener != null) {
                    mListener.OnSeekDown();
                }

                float newProgress = getProgressWithThumbPadding(xTouch);
                setProgress(newProgress);
                getParent().requestDisallowInterceptTouchEvent(true);

                //Fade in
                focusIn();
            }
            break;
            case MotionEvent.ACTION_MOVE:

                if (isSeekIndicatorTouched) {
                    float newXTouch = motionEvent.getX();
                    float newProgress = getProgressWithThumbPadding(newXTouch);
                    setProgress(newProgress);
                }

                break;
            case MotionEvent.ACTION_UP:

                isSeekIndicatorTouched = false;

                if (mListener != null) {
                    mListener.OnSeekUp();
                }

                //Fade it partially out
                focusOut();

                break;
        }

        return true;
    }

    private void focusIn() {
        animate()
            .alpha(1.0f)
            .setDuration(FOCUS_ANIMATION_DURATION)
            .start();
    }

    private void focusOut() {
        animate()
                .alpha(UNFOCUSED_ALPHA)
                .setDuration(FOCUS_ANIMATION_DURATION)
                .start();
    }

    public void setListener(OnCustomSeekBarSeekChanged listener) {
        mListener = listener;
    }

    public interface OnCustomSeekBarSeekChanged {
        public void OnSeekDown();
        public void OnSeekChanged(float progress);
        public void OnSeekUp();
    }
}
