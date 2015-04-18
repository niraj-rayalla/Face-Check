package com.lazerpower.facecheck.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lazerpower.facecheck.R;
import com.lazerpower.facecheck.api.ApiHelper;
import com.lazerpower.facecheck.dispatcher.entity.Champions;
import com.lazerpower.facecheck.dispatcher.entity.Items;
import com.lazerpower.facecheck.dispatcher.entity.Maps;
import com.lazerpower.facecheck.dispatcher.entity.SummonerSpells;
import com.lazerpower.facecheck.dispatcher.ops.EmptyOpCallback;

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

    }

    private void onAbout() {
        Intent aboutIntent = new Intent(this, AboutScreen.class);
        startActivity(aboutIntent);
    }
}
