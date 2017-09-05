package com.opp.android.exchange.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.opp.android.exchange.Country;
import com.opp.android.exchange.database.CountryDBSChema.CountryTable;

/**
 * Created by OPP on 2017/8/31.
 */

public class CountryBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "countryBase.db";

    public CountryBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + CountryTable.NAME
                + "(" + " _id integer primary key autoincrement, "
                + CountryTable.Cols.TIMESTAMP + ", "
                + CountryTable.Cols.COUNTRY_NAME + ", "
                + CountryTable.Cols.CURRENCY + ", "
                + CountryTable.Cols.RATE + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
