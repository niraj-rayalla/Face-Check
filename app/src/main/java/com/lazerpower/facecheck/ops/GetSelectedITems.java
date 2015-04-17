package com.lazerpower.facecheck.ops;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lazerpower.facecheck.dispatcher.entity.Item;
import com.lazerpower.facecheck.dispatcher.ops.DbOp;

import org.json.JSONException;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Niraj on 4/17/2015.
 */
public class GetSelectedItems implements DbOp {

    private Collection<String> mItemIds;

    public GetSelectedItems(Collection<String> itemIds) {
        mItemIds = itemIds;
    }

    @Override
    public LinkedList<Item.ItemModel> run(SQLiteDatabase db, Object arg) throws JSONException {
        LinkedList<Item.ItemModel> items = new LinkedList<>();

        for (String itemId : mItemIds) {
            Cursor c = db.query("item",
                    new String[]{"id", "name", "item_group", "description", "stacks", "image"},
                    "id=?", new String[]{itemId},
                    null, null,
                    null);
            try {
                if (c.moveToFirst()) {
                    items.add(new Item.ItemModel(
                            c.getString(0),                     //id
                            c.getString(1),                     //name
                            c.getString(2),                     //group
                            c.getString(3),                     //description
                            c.getInt(4),                        //stacks
                            c.getString(5)                      //image
                    ));
                }
            } finally {
                c.close();
            }
        }

        return items;
    }
}