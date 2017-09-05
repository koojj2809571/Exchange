package com.opp.android.exchange.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.opp.android.exchange.Country;
import com.opp.android.exchange.database.CountryDBSChema.CountryTable;

/**
 * Created by OPP on 2017/9/6.
 */

public class CountryCursorWrapper extends CursorWrapper {
    public CountryCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Country getCountry(){
        int timeStamp = getInt(getColumnIndex(CountryTable.Cols.TIMESTAMP));
        String countryName = getString(getColumnIndex(CountryTable.Cols.COUNTRY_NAME));
        String currency = getString(getColumnIndex(CountryTable.Cols.CURRENCY));
        double rate = getDouble(getColumnIndex(CountryTable.Cols.RATE));


    }
}
