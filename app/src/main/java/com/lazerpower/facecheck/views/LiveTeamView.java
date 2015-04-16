package com.lazerpower.facecheck.views;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.lazerpower.facecheck.R;
import com.lazerpower.facecheck.api.ApiHelper;
import com.lazerpower.facecheck.dispatcher.entity.Champion;
import com.lazerpower.facecheck.dispatcher.entity.Match;
import com.lazerpower.facecheck.dispatcher.entity.SummonerSpell;
import com.lazerpower.facecheck.dispatcher.ops.EmptyOpCallback;
import com.lazerpower.facecheck.utils.PicassoOkHttp;

import java.util.ArrayList;

/**
 * Created by niraj on 4/15/15.
 * A view that will show all the participants of a teams and when a participants is tapped,
 * that participants gain focus and the current data on the participant is shown.
 */
public class LiveTeamView extends RelativeLayout {
    private static final long ANIMATION_DURATION = 200;

    private View mPlayerDetailsSection;
    private Space mFocusedPlayerPlaceholder;
    private ImageView[] mPlayerImageViews;

    private TextView mChampionName;
    private ImageView mSummonerSpell1;
    private ImageView mSummonerSpell2;

    private TextView mPlayerKda;
    private TextView mPlayerTotalGold;
    private TextView mPlayerJungleCreep;
    private TextView mPlayerTotalCreep;
    private ImageView[] mItemImageViews;

    private Match.MatchModel.Participant[] mParticipants;
    private Champion.ChampionModel[] mChampions;

    private boolean mAnimating = false;
    private int mFocusedPlayerIndex = -1;

    public LiveTeamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_live_team, this, true);

        mPlayerDetailsSection = findViewById(R.id.player_details_section);
        mFocusedPlayerPlaceholder = (Space)findViewById(R.id.focused_player_placeholder);

        //Create the summoner/player image views
        mPlayerImageViews = createImageViews(
                (LinearLayout)findViewById(R.id.player_image_views),
                5,
                getContext().getResources().getDimensionPixelSize(R.dimen.live_player_image),
                getContext().getResources().getDimensionPixelSize(R.dimen.medium_spacing)
        );

        mChampionName = (TextView)findViewById(R.id.champion_name);
        mSummonerSpell1 = (ImageView)findViewById(R.id.summoner_spell_1);
        mSummonerSpell2 = (ImageView)findViewById(R.id.summoner_spell_2);

        mPlayerKda = (TextView)findViewById(R.id.player_kda);
        mPlayerTotalGold = (TextView)findViewById(R.id.player_total_gold);
        mPlayerJungleCreep = (TextView)findViewById(R.id.jungle_creep);
        mPlayerTotalCreep = (TextView)findViewById(R.id.total_creep);

        //Create the item image views
        mItemImageViews = createImageViews(
                (LinearLayout)findViewById(R.id.item_image_views),
                7,
                getContext().getResources().getDimensionPixelSize(R.dimen.half_focused_player_image),
                getContext().getResources().getDimensionPixelSize(R.dimen.medium_spacing)
        );

        for (int i = 0; i < mPlayerImageViews.length; ++i) {
            mPlayerImageViews[i].setOnClickListener(mParticipantClickListener);
        }
        setOnClickListener(mWholeViewClickListener);
    }

    private ImageView[] createImageViews(LinearLayout container, int numViews, int size, int spacing) {
        ImageView[] imageViews = new ImageView[numViews];

        LinearLayout.LayoutParams firstLayoutParams = new LinearLayout.LayoutParams(size, size);
        LinearLayout.LayoutParams othersLayoutParams = new LinearLayout.LayoutParams(size, size);
        othersLayoutParams.leftMargin = spacing;

        for (int i = 0; i < numViews; ++i) {
            imageViews[i] = new ImageView(getContext());
            imageViews[i].setScaleType(ImageView.ScaleType.FIT_XY);
            imageViews[i].setTag(i);
            imageViews[i].setPivotX(0f);
            imageViews[i].setPivotY(0f);

            container.addView(imageViews[i], i == 0 ? firstLayoutParams : othersLayoutParams);
        }

        return imageViews;
    }

    public void setParticipants(Match.MatchModel.Participant[] participants) {
        mParticipants = participants;
        mChampions = new Champion.ChampionModel[participants.length];

        if (mParticipants != null) {
            int i = 0;
            for (Match.MatchModel.Participant participant : mParticipants) {
                final int participantIndex = i;
                ApiHelper.getChampion(participant.mChampionId, new EmptyOpCallback() {
                    @Override
                    public void onOperationResultChanged(Object result) {
                        super.onOperationResultChanged(result);

                        Champion.ChampionModel championModel = (Champion.ChampionModel)result;
                        mChampions[participantIndex] = championModel;

                        PicassoOkHttp.getRequest(getContext(), Uri.parse(championModel.getImageUrl()))
                                .into(mPlayerImageViews[participantIndex]);
                    }
                });
                ++i;
            }
        }
    }

    private void refreshPlayerDetailViews() {
        if (mFocusedPlayerIndex != -1) {
            mChampionName.setText(mChampions[mFocusedPlayerIndex].getName());

            //Summoner spells
            ApiHelper.getSummonerSpell(mParticipants[mFocusedPlayerIndex].mSpell1Id, new EmptyOpCallback() {
                @Override
                public void onOperationResultChanged(Object result) {
                    super.onOperationResultChanged(result);

                    String spellImageUrl = ((SummonerSpell.SummonerSpellModel)result).getImageUrl();
                    PicassoOkHttp.getRequest(getContext(), Uri.parse(spellImageUrl))
                            .into(mSummonerSpell1);
                    for (int i = 0; i < mItemImageViews.length; ++i) {
                        PicassoOkHttp.getRequest(getContext(), Uri.parse(spellImageUrl))
                                .into(mItemImageViews[i]);
                    }
                }
            });
            ApiHelper.getSummonerSpell(mParticipants[mFocusedPlayerIndex].mSpell2Id, new EmptyOpCallback() {
                @Override
                public void onOperationResultChanged(Object result) {
                    super.onOperationResultChanged(result);

                    String spellImageUrl = ((SummonerSpell.SummonerSpellModel) result).getImageUrl();
                    PicassoOkHttp.getRequest(getContext(), Uri.parse(spellImageUrl))
                            .into(mSummonerSpell2);
                }
            });
        }
    }

    private ObjectAnimator getAlphaAnimation(View viewToAnimate, float endAlpha) {
        PropertyValuesHolder alphaProp = PropertyValuesHolder.ofFloat(View.ALPHA, endAlpha);
        return ObjectAnimator.ofPropertyValuesHolder(viewToAnimate, alphaProp);
    }

    private void startFocusOrUnfocusAnimation(final boolean isFocusing, int playerViewToAnimateIndex) {
        AnimatorSet animatorSet = new AnimatorSet();

        ImageView playerViewToAnimate = mPlayerImageViews[playerViewToAnimateIndex];

        ArrayList<Animator> animators = new ArrayList<Animator>();

        //Translating and scaling of the champion/player image view
        PropertyValuesHolder transXProp = PropertyValuesHolder.ofFloat(View.TRANSLATION_X,
                isFocusing ? mFocusedPlayerPlaceholder.getLeft() - playerViewToAnimate.getLeft() : 0f);
        PropertyValuesHolder transYProp = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y,
                isFocusing ? mFocusedPlayerPlaceholder.getTop() - playerViewToAnimate.getTop() : 0f);
        PropertyValuesHolder scaleXProp = PropertyValuesHolder.ofFloat(View.SCALE_X,
                isFocusing ? (float)mFocusedPlayerPlaceholder.getWidth()/(float)playerViewToAnimate.getWidth() : 1.0f);
        PropertyValuesHolder scaleYProp = PropertyValuesHolder.ofFloat(View.SCALE_Y,
                isFocusing ? (float)mFocusedPlayerPlaceholder.getHeight()/(float)playerViewToAnimate.getHeight() : 1.0f);

        ObjectAnimator playerViewAnimator = ObjectAnimator.ofPropertyValuesHolder(
                playerViewToAnimate,
                transXProp, transYProp,
                scaleXProp, scaleYProp);

        animators.add(playerViewAnimator);

        float playerViewsEndAlpha = isFocusing ? 0.0f : 1.0f;
        //Alpha of other player views
        for (int i = 0; i < mParticipants.length; ++i) {
            if (i != mFocusedPlayerIndex) {
                animators.add(getAlphaAnimation(mPlayerImageViews[i], playerViewsEndAlpha));
            }
        }

        //Alpha of player details section
        animators.add(getAlphaAnimation(mPlayerDetailsSection, 1.0f - playerViewsEndAlpha));


        animatorSet.playTogether(animators);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new Animator.AnimatorListener() {

            private void onAnimationFinished() {
                mAnimating = false;
                if (!isFocusing) {
                    mPlayerDetailsSection.setVisibility(INVISIBLE);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mPlayerDetailsSection.setVisibility(VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                onAnimationFinished();
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationFinished();
            }
            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
        animatorSet
                .setDuration(ANIMATION_DURATION)
                .start();
    }

    private void tryUnfocus() {
        if (!mAnimating && mFocusedPlayerIndex >= 0) {
            mAnimating = true;

            startFocusOrUnfocusAnimation(false, mFocusedPlayerIndex);
            mFocusedPlayerIndex = -1;
        }
    }

    OnClickListener mParticipantClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mAnimating) {
                if (mFocusedPlayerIndex == -1) {
                    mAnimating = true;
                    mFocusedPlayerIndex = (Integer)v.getTag();

                    refreshPlayerDetailViews();

                    startFocusOrUnfocusAnimation(true, (Integer) v.getTag());
                }
                else {
                    tryUnfocus();
                }
            }
        }
    };

    //Used to remove focus from a player if a player has a focus
    OnClickListener mWholeViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            tryUnfocus();
        }
    };
}
