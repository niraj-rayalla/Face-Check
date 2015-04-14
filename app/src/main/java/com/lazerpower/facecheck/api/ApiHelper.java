package com.lazerpower.facecheck.api;

import com.lazerpower.facecheck.App;
import com.lazerpower.facecheck.dispatcher.entity.Champions;
import com.lazerpower.facecheck.dispatcher.entity.Items;
import com.lazerpower.facecheck.dispatcher.entity.SummonerSpells;
import com.lazerpower.facecheck.dispatcher.ops.DispatchResultOp;
import com.lazerpower.facecheck.dispatcher.ops.EmptyOpCallback;
import com.lazerpower.facecheck.dispatcher.ops.EntityParseOp;
import com.lazerpower.facecheck.dispatcher.ops.HttpGetOp;
import com.lazerpower.facecheck.dispatcher.ops.OpCallback;
import com.lazerpower.facecheck.http.Param;
import com.lazerpower.facecheck.ops.GetChampion;
import com.lazerpower.facecheck.ops.GetItem;
import com.lazerpower.facecheck.ops.GetSummonerSpell;

/**
 * Created by Niraj on 4/10/2015.
 */
public class ApiHelper {

    /**
     * Gets a champion according to its id in the sql database.
     * If a champion doesn't exist with that id, then it's likely it's a new champion that
     * does not exist and so the entire champion list along with the version is fetched.
     * After the server fetch, the local database fetch is retried.
     * @param callback
     */
    public static void getChampion(final String championId, final OpCallback callback) {
        OpCallback twoStepCallback = new EmptyOpCallback() {
            @Override
            public void onOperationResultChanged(Object result) {
                super.onOperationResultChanged(result);

                if (result == null) {
                    //Fetch the latest entire champions list
                    getAllChampions(new EmptyOpCallback() {
                        @Override
                        public void onOperationFinished(Exception exception) {
                            super.onOperationFinished(exception);

                            //Get from local database
                            App.getInstance().getDispatcher().dispatch(
                                    callback,
                                    new GetChampion(championId),
                                    new DispatchResultOp()
                            );
                        }
                    });
                }
                else {
                    //Tell the callback
                    if (callback != null) {
                        callback.onOperationResultChanged(result);
                    }
                }
            }
        };

        //Get from local database
        App.getInstance().getDispatcher().dispatch(
                twoStepCallback,
                new GetChampion(championId),
                new DispatchResultOp()
        );
    }

    /**
     * Gets an item according to its id in the sql database.
     * If an item doesn't exist with that id, then it's likely it's a new item that
     * does not exist and so the entire item list along with the version is fetched.
     * After the server fetch, the local database fetch is retried.
     * @param callback
     */
    public static void getItem(final String itemId, final OpCallback callback) {
        OpCallback twoStepCallback = new EmptyOpCallback() {
            @Override
            public void onOperationResultChanged(Object result) {
                super.onOperationResultChanged(result);

                if (result == null) {
                    //Fetch the latest entire items list
                    getAllItems(new EmptyOpCallback() {
                        @Override
                        public void onOperationFinished(Exception exception) {
                            super.onOperationFinished(exception);

                            //Get from local database
                            App.getInstance().getDispatcher().dispatch(
                                    callback,
                                    new GetItem(itemId),
                                    new DispatchResultOp()
                            );
                        }
                    });
                }
                else {
                    //Tell the callback
                    if (callback != null) {
                        callback.onOperationResultChanged(result);
                    }
                }
            }
        };

        //Get from local database
        App.getInstance().getDispatcher().dispatch(
                twoStepCallback,
                new GetItem(itemId),
                new DispatchResultOp()
        );
    }

    /**
     * Gets an summoner spell according to its id in the sql database.
     * If a summoner spell doesn't exist with that id, then it's likely it's a new summoner spell that
     * does not exist and so the entire summoner spell list along with the version is fetched.
     * After the server fetch, the local database fetch is retried.
     * @param callback
     */
    public static void getSummonerSpell(final String summonerSpellId, final OpCallback callback) {
        OpCallback twoStepCallback = new EmptyOpCallback() {
            @Override
            public void onOperationResultChanged(Object result) {
                super.onOperationResultChanged(result);

                if (result == null) {
                    //Fetch the latest entire items list
                    getAllSummonerSpells(new EmptyOpCallback() {
                        @Override
                        public void onOperationFinished(Exception exception) {
                            super.onOperationFinished(exception);

                            //Get from local database
                            App.getInstance().getDispatcher().dispatch(
                                    callback,
                                    new GetSummonerSpell(summonerSpellId),
                                    new DispatchResultOp()
                            );
                        }
                    });
                }
                else {
                    //Tell the callback
                    if (callback != null) {
                        callback.onOperationResultChanged(result);
                    }
                }
            }
        };

        //Get from local database
        App.getInstance().getDispatcher().dispatch(
                twoStepCallback,
                new GetSummonerSpell(summonerSpellId),
                new DispatchResultOp()
        );
    }

    /**
     * Gets the entire list of champions from the server
     * Gets the basic champion data with image data
     * parameters champData=image
     * @param callback
     */
    public static void getAllChampions(OpCallback callback) {
        Champions championsEntity = new Champions();
        App.getInstance().getDispatcher().dispatch(
                callback,
                new HttpGetOp(championsEntity.getApiPath(), Param.withKeysAndValues("champData", "image")),
                new EntityParseOp(championsEntity),
                new DispatchResultOp()
        );
    }

    /**
     * Gets the entire list of items from the server
     * Gets the basic item data with image data
     * parameters itemListData=image
     * @param callback
     */
    public static void getAllItems(OpCallback callback) {
        Items itemsEntity = new Items();
        App.getInstance().getDispatcher().dispatch(
                callback,
                new HttpGetOp(itemsEntity.getApiPath(), Param.withKeysAndValues("itemListData", "image")),
                new EntityParseOp(itemsEntity),
                new DispatchResultOp()
        );
    }

    /**
     * Gets the entire list of summoner spells from the server
     * Gets the basic summoner spell data with image data
     * parameters spellData=image
     * @param callback
     */
    public static void getAllSummonerSpells(OpCallback callback) {
        SummonerSpells summonerSpellsEntity = new SummonerSpells();
        App.getInstance().getDispatcher().dispatch(
                callback,
                new HttpGetOp(summonerSpellsEntity.getApiPath(), Param.withKeysAndValues("spellData", "image")),
                new EntityParseOp(summonerSpellsEntity),
                new DispatchResultOp()
        );
    }
}
