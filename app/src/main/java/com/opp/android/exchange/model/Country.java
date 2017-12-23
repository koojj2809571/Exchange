package com.opp.android.exchange.model;

import android.content.Context;

import com.opp.android.exchange.utils.Utils;

/**
 * Created by OPP on 2017/8/30.
 */

public class Country {
    private static final String TAG = "Country";
    private int mName;
    private String mCurrency;
    private String mFlagPath;

    public Country(String currency, int countryName, String flagPath) {
        mCurrency = currency;
        mName = countryName;
        mFlagPath = flagPath;
    }

    public int getName() {
        return mName;
    }

    public void setName(int name) {
        mName = name;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public String getFlagPath() {
        return mFlagPath;
    }

    public void setFlagPath(String flagPath) {
        mFlagPath = flagPath;
    }
}
