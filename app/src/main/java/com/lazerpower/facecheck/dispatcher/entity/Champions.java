package com.lazerpower.facecheck.dispatcher.entity;

/**
 * Created by Niraj on 4/10/2015.
 */
public class Champions extends StaticDataEntity {
    @Override
    protected String getType() {
        return "champion";
    }

    @Override
    protected Entity getEntity() {
        return new Champion();
    }
}
