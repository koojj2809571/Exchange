package com.opp.android.exchange.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OPP on 2017/11/12.
 */

public class Rate {
    private String mTimestamp;
    private List<RateBean> mRateBean;

    public String getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(String timestamp) {
        mTimestamp = timestamp;
    }

    public List<RateBean> getRateBeans() {
        return mRateBean;
    }

    public void setRateBeans(List<RateBean> rateBean) {
        mRateBean = rateBean;
    }
}
