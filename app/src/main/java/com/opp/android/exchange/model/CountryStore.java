package com.opp.android.exchange.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.opp.android.exchange.ExchangeActivity;
import com.opp.android.exchange.database.CountryBaseHelper;
import com.opp.android.exchange.database.CountryCursorWrapper;
import com.opp.android.exchange.database.CountryDBSchema;
import com.opp.android.exchange.database.CountryDBSchema.CountryTable;
import com.opp.android.exchange.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OPP on 2017/8/31.
 */

public class CountryStore {
    private static final String TAG = "CountryStore";
    private static CountryStore sCountryStore;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CountryStore get(Context context) {
        if (sCountryStore == null) {
            sCountryStore = new CountryStore(context);
        }
        return sCountryStore;
    }

    private CountryStore(Context context) {
        mContext = context.getApplicationContext();
//        mDatabase = new CountryBaseHelper(mContext).getWritableDatabase();
    }

    public void loadCountry() {
        mDatabase = new CountryBaseHelper(mContext).getWritableDatabase();
        AssetManager assetManager = mContext.getAssets();
        try {
            String[] flagNames = assetManager.list("flag_pngs");
            for (String name : flagNames) {
                String currencyName = name.replace(".png", "");
                int countryName = Utils.getCountryNameIdByCurrencyName(mContext, currencyName);
                String flagPath = Utils.getFlagAssetsPath(currencyName);
                Country country = new Country(currencyName.toUpperCase(), countryName, flagPath);
                addCountry(country);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mDatabase.close();
        }
    }

    public void loadRates(Rate rate) {
        try {
            mDatabase = new CountryBaseHelper(mContext).getWritableDatabase();
            String timestamp = rate.getTimestamp();
            List<RateBean> rateBeans = rate.getRateBeans();
            if (Utils.IsFieldExist(mDatabase, CountryDBSchema.CountryTable.NAME, timestamp)) {
                for (RateBean rateBean : rateBeans) {
                    upDateRate(rateBean, timestamp);
                }
            } else {
                mDatabase.execSQL("ALTER TABLE " + CountryTable.NAME + " ADD " + timestamp + " REAL DEFAULT 0");
                for (RateBean rateBean : rateBeans) {
                    upDateRate(rateBean, timestamp);
                }
            }
            Log.d(TAG, "——loadRates——: " + Utils.IsFieldExist(mDatabase, CountryTable.NAME, timestamp));
        }finally {
            mDatabase.close();
        }
    }

    public void addCountry(Country country) {
        Log.d(TAG, "添加" + country.getCurrency());
        ContentValues values = getContentValues(country);
        mDatabase.insert(CountryTable.NAME, null, values);
    }

    public void upDateRate(RateBean rateBean, String timestamp) {
        String currencyName = rateBean.getCurrencyName();
        double rate = rateBean.getRate();
        ContentValues values = new ContentValues();
        values.put(timestamp, rate);
        mDatabase.update(CountryTable.NAME, values, CountryTable.Cols.CURRENCY + " = ?", new String[]{currencyName});
    }

    private CountryCursorWrapper queryCountries(String whereClause, String[] whereArgs) {
        mDatabase = new CountryBaseHelper(mContext).getWritableDatabase();
        Cursor cursor = mDatabase.query(
                CountryTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CountryCursorWrapper(cursor);
    }

    public List<Country> getCountries() {
        List<Country> countries = new ArrayList<>();
        CountryCursorWrapper cursor = queryCountries(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                countries.add(cursor.getCountry());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return countries;
    }

    public double getRate(String currencyName, String timestamp) {
        mDatabase = new CountryBaseHelper(mContext).getWritableDatabase();
        CountryCursorWrapper cursor = queryCountries(CountryTable.Cols.CURRENCY + " = ?", new String[]{currencyName});
        try {
            if (cursor.getCount() == 0) {
                return 0.0;
            }
            if (!Utils.IsFieldExist(mDatabase, CountryTable.NAME, timestamp)) {
                Log.d(TAG, "getRate: 数据还未跟新完成,请稍后再试");
                Toast.makeText(mContext, "数据还未跟新完成,请稍后再试", Toast.LENGTH_SHORT);
                return -1.0;
            } else {
                cursor.moveToFirst();
                return cursor.getRate(timestamp);
            }
        } finally {
            mDatabase.close();
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Country country) {
        ContentValues values = new ContentValues();
        values.put(CountryTable.Cols.COUNTRY_NAME, country.getName());
        values.put(CountryTable.Cols.CURRENCY, country.getCurrency().toString());
        values.put(CountryTable.Cols.FLAG_PATH, country.getFlagPath());
        return values;
    }
}
