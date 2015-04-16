package com.lazerpower.facecheck.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

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

        startActivity(new Intent(this, MatchDetailsActivity.class));
    }

    private void doAfterStaticDataCheck() {

    }
}
