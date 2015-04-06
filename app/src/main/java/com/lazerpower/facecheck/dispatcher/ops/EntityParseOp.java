package com.lazerpower.facecheck.dispatcher.ops;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.lazerpower.facecheck.dispatcher.entity.Entity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Niraj on 10/26/2014.
 */
public class EntityParseOp implements DbOp {

    private final Entity mEntity;
    private final Entity.MergePolicy mMerge;

    public EntityParseOp(Entity entity) {
        this(entity, null);
    }

    public EntityParseOp(Entity entity, Entity.MergePolicy merge) {
        mEntity = entity;
        mMerge = merge;
    }

    @Override
    public Object run(SQLiteDatabase db, Object arg) throws JSONException {
        Object result = mEntity.parse(arg, mMerge);

        if (result != null) {
            if(mMerge != null && (result instanceof ContentValues)) {
                mMerge.set((ContentValues) result);
                return mMerge.getResult();
            } else {
                return result;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append(getClass().getSimpleName());
        sb.append(" : ");
        sb.append(mEntity);
        return sb.toString();
    }
}
