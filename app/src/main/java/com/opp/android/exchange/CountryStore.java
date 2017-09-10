package com.opp.android.exchange;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.opp.android.exchange.database.CountryBaseHelper;
import com.opp.android.exchange.database.CountryCursorWrapper;
import com.opp.android.exchange.database.CountryDBSChema.CountryTable;
import com.opp.android.exchange.utils.CountryRates;
import com.opp.android.exchange.utils.CountryRates.QuotesBean;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by OPP on 2017/8/31.
 */

public class CountryStore {
    private static final String TAG = "CountryStore";
    private static final String FLAG_FOLDER = "flag_pngs";
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
        mDatabase = new CountryBaseHelper(mContext).getWritableDatabase();
    }
    
    public List<Country> loadCountry(Context context,CountryRates countryRates) {
        List<Country> countries = new ArrayList<>();
        int timestamp = countryRates.getTimestamp();
        Class<QuotesBean> clz = (Class<QuotesBean>) countryRates.getQuotes().getClass();
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.equals("$change")){
                return null;
            }else {
                Log.d(TAG, "字段名为" + fieldName);
                String currencyName;
                if (fieldName.equals("USDUSD")) {
                    currencyName = "USD";
                } else {
                    currencyName = fieldName.replace("USD", "");
                }
                Log.d(TAG, "货币缩写为：" + currencyName);
                int countryNameId = context.getResources().getIdentifier(currencyName.toLowerCase() + "_name", "string", context.getPackageName());
                String countryName = context.getResources().getString(countryNameId);
                Log.d(TAG, "国家名称为：" + countryName);
                String flagName = FLAG_FOLDER + "/" + currencyName.toLowerCase() + ".png";
                Log.d(TAG, "国旗名称为" + flagName);
                try {
                    InputStream inputStream = context.getAssets().open(flagName);
                    Log.d(TAG, "图片字节流为：" + inputStream.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                double rate = 0.0;
                try {
                    Method method = clz.getDeclaredMethod("get" + fieldName, new Class[]{});
                    rate = (double) method.invoke(countryRates.getQuotes(), new Object[]{});
                    Log.d(TAG, "汇率为：" + rate);
                    Log.d(TAG, "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Country country = new Country(context, timestamp, currencyName, countryNameId, flagName, rate);
                addCountry(country);
                countries.add(country);
            }
        }
        return countries;
    }

    public void addCountry(Country country) {
        Log.d(TAG, "添加" + country.getCurrency());
        ContentValues values = getContentValues(country);
        mDatabase.insert(CountryTable.NAME,null,values);
    }

    public void upDateCountry(Country country) {
        String currencyString = country.getCurrency().toString();
        ContentValues values = getContentValues(country);
        mDatabase.update(CountryTable.NAME,values,CountryTable.Cols.CURRENCY + " = ?",new String[]{currencyString});
    }

    private CountryCursorWrapper queryCountries(String whereClause, String[] whereArgs){
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
        CountryCursorWrapper cursor = queryCountries(null,null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                countries.add(cursor.getCountry(mContext));
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return countries;
    }

    public Country getCountry(String currencyName) {
        CountryCursorWrapper cursor = queryCountries(CountryTable.Cols.CURRENCY + " + ?",new String[]{currencyName});
        try {
            if (cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCountry(mContext);
        }finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Country country) {
        ContentValues values = new ContentValues();
        values.put(CountryTable.Cols.TIMESTAMP,country.getTimeStamp().toString());
        values.put(CountryTable.Cols.COUNTRY_NAME, country.getNameId().toString());
        values.put(CountryTable.Cols.CURRENCY, country.getCurrency().toString());
        values.put(CountryTable.Cols.FLAG_NAME,country.getFlagName());
        values.put(CountryTable.Cols.RATE,country.getRate().toString());

        return values;
    }
}
