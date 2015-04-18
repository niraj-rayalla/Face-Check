package com.lazerpower.facecheck.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lazerpower.facecheck.App;
import com.lazerpower.facecheck.R;
import com.lazerpower.facecheck.api.ApiHelper;
import com.lazerpower.facecheck.dispatcher.entity.Champions;
import com.lazerpower.facecheck.dispatcher.entity.Items;
import com.lazerpower.facecheck.dispatcher.entity.Maps;
import com.lazerpower.facecheck.dispatcher.entity.Match;
import com.lazerpower.facecheck.dispatcher.entity.SummonerSpells;
import com.lazerpower.facecheck.dispatcher.ops.DispatchResultOp;
import com.lazerpower.facecheck.dispatcher.ops.EmptyOpCallback;
import com.lazerpower.facecheck.ops.GetMatches;
import com.lazerpower.facecheck.views.MatchListItem;

import java.util.ArrayList;

/**
 * Created by Niraj on 4/3/2015.
 */
public class MainActivity extends Activity {

    private TextView mAboutButton;
    private RecyclerView mGamesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mAboutButton = (TextView)findViewById(R.id.about_button);
        mGamesList = (RecyclerView)findViewById(R.id.games_list);

        mGamesList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAbout();
            }
        });

        //
        // Get static data
        //
        boolean hasChampionsData = (new Champions()).hasStaticDataStored();
        boolean hasItemsData = (new Items()).hasStaticDataStored();
        boolean hasSummonerSpellsData = (new SummonerSpells()).hasStaticDataStored();
        boolean hasMapsData = (new Maps()).hasStaticDataStored();
        final int numStaticDataToFetch =
                (hasChampionsData ? 0 : 1)
                + (hasItemsData ? 0 : 1)
                + (hasSummonerSpellsData ? 0 : 1)
                + (hasMapsData ? 0 : 1);

        if (numStaticDataToFetch > 0) {
            EmptyOpCallback fetchAllNeededStaticDataCallback = new EmptyOpCallback() {

                int numFetchesLeft = numStaticDataToFetch;

                @Override
                public void onOperationFinished(Exception exception) {
                    super.onOperationFinished(exception);

                    if (--numFetchesLeft == 0) {
                        //Done fetching
                        doAfterStaticDataCheck();
                    }
                }
            };

            if (!hasChampionsData) {
                ApiHelper.getAllChampions(fetchAllNeededStaticDataCallback);
            }
            if (!hasItemsData) {
                ApiHelper.getAllItems(fetchAllNeededStaticDataCallback);
            }
            if (!hasSummonerSpellsData) {
                ApiHelper.getAllSummonerSpells(fetchAllNeededStaticDataCallback);
            }
            if (!hasMapsData) {
                ApiHelper.getAllMaps(fetchAllNeededStaticDataCallback);
            }
        }
        else {
            doAfterStaticDataCheck();
        }
    }

    private void doAfterStaticDataCheck() {
        App.getInstance().getDispatcher().dispatch(
                new EmptyOpCallback() {
                    @Override
                    public void onOperationResultChanged(Object result) {
                        super.onOperationResultChanged(result);

                        mGamesList.setAdapter(new MatchesAdapter((ArrayList<Match.MatchModel>) result));
                    }
                },
                new GetMatches(),
                new DispatchResultOp()
        );
    }

    private void onAbout() {
        Intent aboutIntent = new Intent(this, AboutScreen.class);
        startActivity(aboutIntent);
    }

    private class MatchesAdapter extends RecyclerView.Adapter {

        private ArrayList<Match.MatchModel> mMatches;

        public MatchesAdapter(ArrayList<Match.MatchModel> matches) {
            mMatches = matches;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new MatchItemViewHolder(createView(viewGroup));
        }

        protected View createView(ViewGroup viewGroup) {
            View itemView = new MatchListItem(viewGroup.getContext(), null);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    viewGroup.getContext().getResources().getDimensionPixelSize(R.dimen.match_item_height));
            layoutParams.bottomMargin = viewGroup.getContext().getResources().getDimensionPixelSize(R.dimen.medium_spacing);
            itemView.setLayoutParams(layoutParams);

            return itemView;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            ((MatchItemViewHolder)viewHolder).updateContents(getItem(position));
        }

        @Override
        public int getItemCount() {
            return mMatches == null ? 0 : mMatches.size();
        }

        public Match.MatchModel getItem(int position) {
            return mMatches.get(position);
        }

        private class MatchItemViewHolder extends RecyclerView.ViewHolder {

            private MatchListItem mMatchItem;

            public MatchItemViewHolder(View itemView) {
                super(itemView);
                mMatchItem = (MatchListItem)itemView;

                mMatchItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = MatchDetailsActivity.getIntent(
                                mMatchItem.getContext(), getItem(getPosition()).getId());
                        startActivity(intent);
                    }
                });
            }

            public void updateContents(Match.MatchModel match) {
                mMatchItem.setMatch(match);
            }
        }
    }
}
