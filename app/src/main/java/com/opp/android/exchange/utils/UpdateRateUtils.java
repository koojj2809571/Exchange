package com.opp.android.exchange.utils;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.opp.android.exchange.Country;
import com.opp.android.exchange.CountryStore;
import com.opp.android.exchange.utils.CountryRates.QuotesBean;

import java.io.IOException;
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
            .appendQueryParameter("access_key",API_KEY)
            .appendQueryParameter("format",1+"")
            .build();
    private static final Uri HISTORICAL_RATES_ENDPOINT = Uri
            .parse("http://apilayer.net/api/historical")
            .buildUpon()
            .appendQueryParameter("access_key",API_KEY)
            .appendQueryParameter("format",1+"")
            .build();

    public byte[] getUrlBytes(String url) throws IOException{
        Log.d(TAG, "接口为: " + url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().bytes();
    }

    public String getUrlString(String url)throws IOException{
        String jsonData = new String(getUrlBytes(url));
        Log.d(TAG, "返回数据为：" + jsonData);
        return jsonData;
    }

    public String buildUrl(){
        return LIVE_RATES_ENDPOINT.toString();
    }

    public String buildUrl(String timeStamp){
       Uri.Builder uriBuilder =  HISTORICAL_RATES_ENDPOINT.buildUpon()
                .appendQueryParameter("date",timeStamp);
        return uriBuilder.build().toString();
    }

    public CountryRates parseItems(String jsonData){
        CountryRates countryRates = new Gson().fromJson(jsonData,CountryRates.class);
        return countryRates;
    }

    public CountryRates fetchCountry(){
        CountryRates countryRates = null;
        try {
            countryRates = parseItems(getUrlString(buildUrl()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return countryRates;
    }
}
