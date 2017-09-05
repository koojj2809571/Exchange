package com.opp.android.exchange;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by OPP on 2017/8/30.
 */

public class Country {
    private static final String TAG = "Country";
    private String mName;
    private String mCurrency;
    private String mFlagPath;
    private Bitmap mFlag;
    private Double mRate;
    private Integer mTimeStamp;

    public Country(String assetsPath, Bitmap bitmap, Context context,double rate){
        mFlagPath = assetsPath;
        String[] components = assetsPath.split("/");
        String filename = components[components.length-1];
        mCurrency = filename.replace(".png","").toUpperCase();
        Integer mNameId = context.getResources().getIdentifier(mCurrency.toLowerCase() + "_name","string",context.getPackageName());
        Log.d(TAG, mCurrency + "序列号为"+mNameId);
        mName = context.getString(mNameId);
        mFlag = bitmap;
        mRate = rate;
    }

    public String getName() {
        return mName;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public String getFlagPath() {
        return mFlagPath;
    }

    public Double getRate() {
        return mRate;
    }

    public void setRate(Double rate) {
        mRate = rate;
    }

    public Integer getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(Integer timeStamp) {
        mTimeStamp = timeStamp;
    }

}
