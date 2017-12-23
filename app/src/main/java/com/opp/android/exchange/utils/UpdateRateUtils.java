package com.opp.android.exchange.utils;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.opp.android.exchange.model.CountryRates;
import com.opp.android.exchange.model.Rate;
import com.opp.android.exchange.model.Rate.*;
import com.opp.android.exchange.model.RateBean;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by OPP on 2017/9/3.
 */

public class UpdateRateUtils {
    private static final String TAG = "UpdateRateUtils";
    private static final String API_KEY = "902f43b50d1824eda6a166df4f6a6dde";
    private static final Uri LIVE_RATES_ENDPOINT = Uri
            .parse("http://apilayer.net/api/live")
            .buildUpon()
            .appendQueryParameter("access_key", API_KEY)
            .appendQueryParameter("format", 1 + "")
            .build();
    private static final Uri HISTORICAL_RATES_ENDPOINT = Uri
            .parse("http://apilayer.net/api/historical")
            .buildUpon()
            .appendQueryParameter("access_key", API_KEY)
            .appendQueryParameter("format", 1 + "")
            .build();

    public byte[] getUrlBytes(String url) throws IOException {
        Log.d(TAG, "接口为: " + url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().bytes();
    }

    public String getUrlString(String url) throws IOException {
        String jsonData = new String(getUrlBytes(url));
        return jsonData;
    }

    public String buildUrl() {
        return LIVE_RATES_ENDPOINT.toString();
    }

    public String buildUrl(long timeStamp) {
        Date d = new Date(timeStamp);
        DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Uri.Builder uriBuilder = HISTORICAL_RATES_ENDPOINT.buildUpon()
                .appendQueryParameter("date", f.format(d));
        return uriBuilder.build().toString();
    }

    public CountryRates parseItems(String jsonData) {
        CountryRates countryRates = new Gson().fromJson(jsonData, CountryRates.class);
        return countryRates;
    }

    public Rate fetchCountry() {
        return this.fetchCountry(0);
    }
    public Rate fetchCountry(long date) {
        Rate rate = new Rate();
        CountryRates countryRates = null;
        String timestamp = null;
        try {
            if (date == 0){
                countryRates = parseItems(getUrlString(buildUrl()));
                timestamp = Utils.timestampToDateString(System.currentTimeMillis());
            }else {
                countryRates = parseItems(getUrlString(buildUrl(date)));
                timestamp = Utils.timestampToDateString(date);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        rate.setTimestamp(timestamp);
        List<RateBean> rateBeans = new ArrayList<>();
        Class<CountryRates.QuotesBean> clz = (Class<CountryRates.QuotesBean>) countryRates.getQuotes().getClass();
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            Log.d(TAG, "——网络解析中——：" + fieldName);
            if (!fieldName.equals("$change")&&!fieldName.equals("serialVersionUID")) {
                RateBean rateBean = new RateBean();
                rateBean.setCurrencyName(Utils.getCurrencyNameByFieldName(fieldName));
                rateBean.setRate(Utils.getValueByFieldName(clz, fieldName, countryRates));
                rateBeans.add(rateBean);
            }
        }
        rate.setRateBeans(rateBeans);
        Log.d(TAG, "——网络解析中——：" + rate.toString());
        return rate;
    }
}
