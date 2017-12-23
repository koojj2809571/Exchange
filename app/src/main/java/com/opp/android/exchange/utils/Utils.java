package com.opp.android.exchange.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.opp.android.exchange.R;
import com.opp.android.exchange.model.CountryRates;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by OPP on 2017/9/17.
 */

public class Utils {
    private static final String FLAG_FOLDER = "flag_pngs";
    public static String getCurrencyNameByFieldName(String fieldName) {
        String currencyName;
        if (fieldName.equals("USDUSD")) {
            currencyName = "USD";
        } else {
            currencyName = fieldName.replace("USD", "");
        }
        return currencyName;
    }

    public static int getCountryNameIdByCurrencyName(Context context,String currencyName){
        return context.getResources().getIdentifier(currencyName.toLowerCase() + "_name", "string", context.getPackageName());
    }

    public static String getCountryNameById(Context context,int nameId){
        return context.getResources().getString(nameId);
    }

//    public static String getFlagNameByCurrencyName(String currencyName){
//        return FLAG_FOLDER + "/" + currencyName.toLowerCase() + ".png";
//    }
//
//    public static Bitmap getFlagByName(Context context,String flagName){
//        try {
//            return BitmapFactory.decodeStream(context.getAssets().open(flagName));
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static String getFlagAssetsPath(String currencyName){
        return "file:///android_asset/flag_pngs/" + currencyName.toLowerCase() + ".png";
    }

    public static double getValueByFieldName(Class clz, String fieldName,CountryRates countryRates){
        try {
            Method method = clz.getDeclaredMethod("get" + fieldName, new Class[]{});
            return (double) method.invoke(countryRates.getQuotes(), new Object[]{});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return 0.0;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return 0.0;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    
    public static String timestampToDateString(long timestamp){
        Date date = new Date(timestamp);
        DateFormat f = new SimpleDateFormat("_yyyy_MM_dd");
        return f.format(date);
    }

    public static String timestampToLocalString(Context context,long timestamp){
        Date date = new Date(timestamp);
        DateFormat f = new SimpleDateFormat(context.getResources().getString(R.string.local_date_format));
        return f.format(date);
    }

    public static boolean IsFieldExist(SQLiteDatabase db, String tableName, String fieldName) {
        boolean isFieldExist = false;
        String queryStr = "select sql from sqlite_master where type = 'table' and name = '%s'";
        queryStr = String.format(queryStr, tableName);
        Cursor cursor = db.rawQuery(queryStr, null);
        String tableCreateSql = null;
        try {
            if (cursor != null && cursor.moveToFirst()) {
                tableCreateSql = cursor.getString(cursor.getColumnIndex("sql"));
            }
        }finally {
            if (cursor != null){
                cursor.close();
            }
        }
        if (tableCreateSql != null&&tableCreateSql.contains(fieldName)){
            isFieldExist = true;
        }
        return isFieldExist;
    }

    public static boolean IsTableExist(Context context) {
        boolean isTableExist = false;
        SQLiteDatabase db = context.openOrCreateDatabase("countryBase.db", 0, null);
        Cursor c = db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type ='table' AND name='countryInfo' ", null);
        if (c.moveToFirst()){
            int index = c.getInt(0);
            if (index > 0) {
                isTableExist = true;
            }
        }
        c.close();
        db.close();
        return isTableExist;
    }
}
