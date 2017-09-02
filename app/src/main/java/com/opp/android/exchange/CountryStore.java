package com.opp.android.exchange;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.opp.android.exchange.database.CountryBaseHelper;
import com.opp.android.exchange.database.CountryDBSChema.CountryTable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by OPP on 2017/8/31.
 */

public class CountryStore {
    private static final String TAG = "CountryStore";
    private static final String FLAG_FOLDER = "flag_pngs";
    private AssetManager mAssets;
    private static CountryStore sCountryStore;
    private Context mContext;
    private List<Country> mCountries = new ArrayList<>();
//    private SQLiteDatabase mDatabase;

    public static CountryStore get(Context context){
        if (sCountryStore == null){
            sCountryStore = new CountryStore(context);
        }
        return sCountryStore;
    }

    private CountryStore(Context context){
        mContext = context.getApplicationContext();
        mAssets = mContext.getAssets();
        loadFlags(context);
//        mDatabase = new CountryBaseHelper(mContext).getWritableDatabase();
    }

    private void loadFlags(Context context){
        try {
            String[] flagNames = mAssets.list(FLAG_FOLDER);
            Log.d(TAG, "找到 " + flagNames.length + " 个图片");
            for (String filename : flagNames) {
                String assetPath = FLAG_FOLDER + "/" + filename;
                InputStream inputStream = mAssets.open(assetPath);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Country country = new Country(assetPath,bitmap,context);
                mCountries.add(country);
            }
        }catch (IOException ioe){
            Log.e(TAG, "无法列示资源文件",ioe);
        }
    }

    private void load(Country country) throws IOException{
        InputStream is = mAssets.open(country.getFlagPath());
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        country.setFlag(bitmap);
    }

    public void addCountry(Country c){
        ContentValues values = getContentValues(c);

//        mDatabase.insert(CountryTable.NAME,null,values);
    }

    public List<Country> getCountries(){
        return mCountries;
    }

    public Country getCountry(){
        return null;
    }

    private static ContentValues getContentValues(Country country){
        ContentValues values = new ContentValues();
        values.put(CountryTable.Cols.COUNTRY_NAME,country.getName().toString());
        values.put(CountryTable.Cols.CURRENCY,country.getCurrency().toString());
        values.put(CountryTable.Cols.FLAG_RESOURCE,country.getFlagPath().toString());
//        values.put(CountryTable.Cols.RATE,country.getRate().toString());

        return values;
    }

    public void upDateCountry(Country country){
        ContentValues values = getContentValues(country);

//        mDatabase.update(CountryTable.NAME,values,CountryTable.Cols.UUID + " = ?",new String[]{uuidString});
    }
}
