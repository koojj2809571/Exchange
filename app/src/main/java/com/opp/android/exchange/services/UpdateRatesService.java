package com.opp.android.exchange.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.opp.android.exchange.model.CountryRates;
import com.opp.android.exchange.utils.UpdateRateUtils;

import java.io.IOException;

public class UpdateRatesService extends IntentService {
    private static final String TAG = "UpdateRatesService";


    public static Intent newIntent(Context context){
        return new Intent(context,UpdateRatesService.class);
    }

    public UpdateRatesService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        CountryRates mCountryRates;
        if(!isNetworkAvailableAndConnected()){
            return;
        }

        UpdateRateUtils utils = new UpdateRateUtils();
        String url = utils.buildUrl();
        Log.d(TAG, "接口为" + url);
        try {
            String jsonData = utils.getUrlString(url);
            Log.d(TAG, "返回数据为" + jsonData);
            mCountryRates = utils.parseItems(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private boolean isNetworkAvailableAndConnected(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
}
