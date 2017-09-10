package com.opp.android.exchange.database;

import android.content.Context;
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

    public Country getCountry(Context context){
        int timeStamp = getInt(getColumnIndex(CountryTable.Cols.TIMESTAMP));
        String currency = getString(getColumnIndex(CountryTable.Cols.CURRENCY));
        int countryNameID = getInt(getColumnIndex(CountryTable.Cols.COUNTRY_NAME));
        String flagName = getString(getColumnIndex(CountryTable.Cols.FLAG_NAME));
        double rate = getDouble(getColumnIndex(CountryTable.Cols.RATE));

        Country country = new Country(context,timeStamp,currency,countryNameID,flagName,rate);
        return country;
    }
}
