package com.lazerpower.facecheck.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Niraj on 4/5/2015.
 */
public class InitialDatabasePatch implements Patch {

    private static final String CREATE_MATCH_TABLE =
            "CREATE TABLE MATCH (" +
                    "ID VARCHAR PRIMARY KEY," +         //Match id
                    "MATCHCREATION LONG NOT NULL," +    //Match creation unix time (milliseconds)
                    "MATCHDURATION LONG NOT NULL," +    //Duration in seconds
                    "JSON VARCHAR NOT NULL);";          //Entire JSON string of the match

    private static final String CREATE_CHAMPIONS_TABLE =
            "CREATE TABLE CHAMPION (" +
                    "ID VARCHAR PRIMARY KEY," +         //Champion id
                    "KEY VARCHAR  NOT NULL," +          //Champion key
                    "NAME VARCHAR  NOT NULL," +         //Champion name
                    "TITLE VARCHAR  NOT NULL," +        //Champion title
                    "IMAGE VARCHAR NOT NULL);";         //Champion image json object

    private static final String CREATE_ITEMS_TABLE =
            "CREATE TABLE ITEM (" +
                    "ID VARCHAR PRIMARY KEY," +         //Item id
                    "NAME VARCHAR NOT NULL," +          //Item name
                    "ITEM_GROUP VARCHAR NOT NULL," +    //Item group name
                    "DESCRIPTION VARCHAR NOT NULL," +   //Item description
                    "IMAGE VARCHAR NOT NULL);";         //Item image json object

    private static final String CREATE_SUMMONER_SPELLS_TABLE =
            "CREATE TABLE SUMMONER (" +
                    "ID VARCHAR PRIMARY KEY," +         //Summoner spell id
                    "NAME VARCHAR NOT NULL," +          //Summoner spell name
                    "DESCRIPTION VARCHAR NOT NULL," +   //Summoner spell description
                    "SUMMONER_LEVEL INTEGER NOT NULL," +    //Summoner spell - summoner level
                    "KEY VARCHAR NOT NULL," +           //Summoner spell key
                    "IMAGE VARCHAR NOT NULL);";         //Summoner spell image json object

    private static final String CREATE_TABLE_SQL_STRINGS[] = {
            CREATE_MATCH_TABLE,
            CREATE_CHAMPIONS_TABLE,
            CREATE_ITEMS_TABLE,
            CREATE_SUMMONER_SPELLS_TABLE
    };

    @Override
    public void up(SQLiteDatabase db) {
        for (String sql : CREATE_TABLE_SQL_STRINGS) {
            db.execSQL(sql);
        }
    }
}
