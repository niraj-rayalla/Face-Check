package com.lazerpower.facecheck.dispatcher.entity;

/**
 * Created by Niraj on 4/13/2015.
 */
public class Maps extends StaticDataEntity {
    @Override
    protected String getType() {
        return "map";
    }

    @Override
    protected Entity getEntity() {
        return new Map();
    }
}