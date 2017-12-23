package com.opp.android.exchange.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import com.opp.android.exchange.model.Country;
import com.opp.android.exchange.database.CountryDBSchema.CountryTable;

/**
 * Created by OPP on 2017/9/6.
 */

public class CountryCursorWrapper extends CursorWrapper {
    public CountryCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Country getCountry(){
        String currency = getString(getColumnIndex(CountryTable.Cols.CURRENCY));
        int countryName = getInt(getColumnIndex(CountryTable.Cols.COUNTRY_NAME));
        String flagPath = getString(getColumnIndex(CountryTable.Cols.FLAG_PATH));
        Log.d("+=+Cursor+=+", "getCountry: " + currency + "___" + countryName + "___" + flagPath);

        Country country = new Country(currency,countryName,flagPath);
        return country;
    }

    public double getRate(String timestamp){
        return getDouble(getColumnIndex(timestamp));
    }
}
