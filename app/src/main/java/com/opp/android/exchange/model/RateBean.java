package com.opp.android.exchange.model;

/**
 * Created by OPP on 2017/11/12.
 */

public class RateBean {
    private String mCurrencyName;
    private double mRate;

    public String getCurrencyName() {
        return mCurrencyName;
    }

    public void setCurrencyName(String currencyName) {
        mCurrencyName = currencyName;
    }

    public double getRate() {
        return mRate;
    }

    public void setRate(double rate) {
        mRate = rate;
    }
}
