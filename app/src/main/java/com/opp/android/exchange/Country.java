package com.opp.android.exchange;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by OPP on 2017/8/30.
 */

public class Country {
    private static final String TAG = "Country";
    private Integer mTimeStamp;
    private Integer mNameId;
    private String mName;
    private String mCurrency;
    private String mFlagName;
    private Bitmap mFlag;
    private Double mRate;

    public Country(){
    }

    public Country(Context context,int timeStamp, String currency, int countryNameID, String flagName, double rate){
        mTimeStamp = timeStamp;
        mCurrency = currency;
        mNameId = countryNameID;
        mName = context.getResources().getString(mNameId);
        mFlagName = flagName;
        try {
            mFlag = BitmapFactory.decodeStream(context.getAssets().open(flagName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRate = rate;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Bitmap getFlag() {
        return mFlag;
    }

    public void setFlag(Bitmap flag) {
        mFlag = flag;
    }

    public Integer getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(Integer timeStamp) {
        mTimeStamp = timeStamp;
    }

    public Integer getNameId() {
        return mNameId;
    }

    public void setNameId(Integer nameId) {
        mNameId = nameId;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public String getFlagName() {
        return mFlagName;
    }

    public void setFlagName(String flagName) {
        mFlagName = flagName;
    }

    public Double getRate() {
        return mRate;
    }

    public void setRate(Double rate) {
        mRate = rate;
    }
}
