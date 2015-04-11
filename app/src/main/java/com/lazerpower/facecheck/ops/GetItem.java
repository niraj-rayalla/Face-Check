package com.lazerpower.facecheck.ops;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lazerpower.facecheck.dispatcher.entity.Item;
import com.lazerpower.facecheck.dispatcher.ops.DbOp;

import org.json.JSONException;

/**
 * Created by Niraj on 4/9/2015.
 */
public class GetItem implements DbOp {

    private String mItemId;

    public GetItem(String itemId) {
        mItemId = itemId;
    }

    @Override
    public Item.ItemModel run(SQLiteDatabase db, Object arg) throws JSONException {
        Item.ItemModel item = null;

        Cursor c = db.query("items",
                new String[]{"id", "name", "item_group", "description", "image"},
                "id=?", new String[]{mItemId},
                null, null,
                null);
        try {
            if (c.moveToFirst()) {
                item = new Item.ItemModel(
                        c.getString(0),                     //id
                        c.getString(1),                     //name
                        c.getString(2),                     //group
                        c.getString(3),                     //description
                        c.getString(4)                      //image
                );
            }
        } finally {
            c.close();
        }

        return item;
    }
}
