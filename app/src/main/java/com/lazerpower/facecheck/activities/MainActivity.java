package com.lazerpower.facecheck.activities;

import android.app.Activity;
import android.os.Bundle;

import com.lazerpower.facecheck.R;
import com.lazerpower.facecheck.api.ApiHelper;
import com.lazerpower.facecheck.dispatcher.entity.Champions;
import com.lazerpower.facecheck.dispatcher.entity.Items;
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
        final int numStaticDataToFetch =
                (hasChampionsData ? 0 : 1)
                + (hasItemsData ? 0 : 1);

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
        }
        else {
            doAfterStaticDataCheck();
        }

        /*
        App.getInstance().getDispatcher().dispatch(
                new OpCallback() {
                    @Override
                    public void onOperationStarted() {

                    }

                    @Override
                    public void onOperationResultChanged(Object result) {
                        Log.d("");
                        Log.d(result.toString());
                    }

                    @Override
                    public void onOperationFinished(Exception exception) {

                    }
                },
                new HttpGetOp(ApiPaths.getApiChallengePath(),
                              Param.withKeysAndValues("beginDate", Long.toString(TimeUtils.getLastFullFiveMinEpoch()))),
                new EntityParseOp(new Match()),
                new GetMatches(),
                new DispatchResultOp()
        );
        */
    }

    private void doAfterStaticDataCheck() {

    }
}
