package com.lazerpower.facecheck.dispatcher.entity;

/**
 * Created by Niraj on 4/13/2015.
 */
public class SummonerSpells extends StaticDataEntity {
    @Override
    protected String getType() {
        return "summoner-spell";
    }

    @Override
    protected String getImagePathType() {
        return "spell";
    }

    @Override
    protected Entity getEntity() {
        return new SummonerSpell();
    }
}
