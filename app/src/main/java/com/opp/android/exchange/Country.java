package com.opp.android.exchange;

/**
 * Created by OPP on 2017/8/30.
 */

public class Country {
    private String name;
    private String currency;
    private String flagResource;
    private double rate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFlagResource() {
        return flagResource;
    }

    public void setFlagResource(String flagResource) {
        this.flagResource = flagResource;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
