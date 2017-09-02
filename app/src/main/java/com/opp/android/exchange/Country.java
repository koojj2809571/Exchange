package com.opp.android.exchange;

import android.content.Context;
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
    private double mRate;

    public Country(String assetsPath, Bitmap bitmap, Context context){
        mFlagPath = assetsPath;
        String[] components = assetsPath.split("/");
        String filename = components[components.length-1];
        mCurrency = filename.replace(".png","").toUpperCase();
        Integer mNameId = context.getResources().getIdentifier(mCurrency.toLowerCase() + "_name","string",context.getPackageName());
        Log.d(TAG, mCurrency + "序列号为"+mNameId);
        mName = context.getString(mNameId);
        mFlag = bitmap;
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

    public Bitmap getFlag(){
        return mFlag;
    }

    public void setFlag(Bitmap flag){
        mFlag = flag;
    }

    public double getRate() {
        return mRate;
    }

    public void setRate(double rate) {
        mRate = rate;
    }
}
