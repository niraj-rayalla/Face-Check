package com.lazerpower.facecheck.dispatcher.entity;

/**
 * Created by Niraj on 4/10/2015.
 */
public class Items extends StaticDataEntity {
    @Override
    protected String getType() {
        return "item";
    }

    @Override
    protected Entity getEntity() {
        return new Item();
    }
}
