package com.opp.android.exchange;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.opp.android.exchange.PullToRefreshView.PullToRefreshListener;
import com.opp.android.exchange.PullToRefreshView.RefreshableView;
import com.opp.android.exchange.database.CountryBaseHelper;
import com.opp.android.exchange.database.CountryDBSchema;
import com.opp.android.exchange.model.Country;
import com.opp.android.exchange.model.CountryStore;
import com.opp.android.exchange.model.CountryRates;
import com.opp.android.exchange.utils.UpdateRateUtils;
import com.opp.android.exchange.utils.Utils;

import java.util.List;

/**
 * Created by OPP on 2017/8/29.
 */

public class CountryListActivity extends AppCompatActivity {
    private static final String TAG = "CountryListActivity";
    private Context mContext = CountryListActivity.this;

    //------字段、变量------
    public static final String COUNTRY_PREFERENCE = "countryCurrency";
    public static final String ORIGINAL_CURRENCY = "originalCurrency";
    public static final String RESULT_CURRENCY = "resultCurrency";
    private RecyclerView mRecyclerView;
    private RefreshableView mRefreshableView;
    private Country mCountry;
    private CountryStore mCountryStore;
    private CountryAdapter mCountryAdapter;
    private List<Country> mCountries;
    private SharedPreferences mPreferences;
    private int mAmount;
//    private TextView mNoData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_list);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mAmount = getIntent().getIntExtra(ExchangeActivity.AMOUNT,0);
        mPreferences = getSharedPreferences(COUNTRY_PREFERENCE,MODE_PRIVATE);
        mRecyclerView = (RecyclerView) findViewById(R.id.country_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mCountryStore = CountryStore.get(mContext);
        mCountries = mCountryStore.getCountries();
//        mNoData = (TextView) findViewById(R.id.no_data_hint);
//        mRefreshableView = (RefreshableView)findViewById(R.id.refresh_view);
//        if (mRecyclerView == null){
//            mNoData.setVisibility(View.VISIBLE);
//        }
//        mRefreshableView.setOnRefreshListener(new PullToRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mRefreshableView.finishRefreshing();
//                updateUI();
//            }
//        },1);
        updateUI();
    }

    private void updateUI(){
        mCountryAdapter = new CountryAdapter(mCountries);
        mRecyclerView.setAdapter(mCountryAdapter);
    }

    private class CountryViewHolder extends RecyclerView.ViewHolder{
        private View countryView;
        private ImageView mFlagImageView;
        private TextView mCountryName;
        private TextView mCountryCurrency;

        public CountryViewHolder(View itemView) {
            super(itemView);
            countryView = itemView;
            mFlagImageView = itemView.findViewById(R.id.list_item_flag_image_view);
            mCountryName = itemView.findViewById(R.id.list_item_country_chinese_text_view);
            mCountryCurrency = itemView.findViewById(R.id.list_item_country_english_text_view);
        }
    }

    private class CountryAdapter extends RecyclerView.Adapter<CountryViewHolder>{

        public CountryAdapter(List<Country> countries) {
            mCountries = countries;
        }

        @Override
        public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (mContext == null){
                mContext = parent.getContext();
            }
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_country,parent,false);
            final CountryViewHolder holder = new CountryViewHolder(view);
            holder.countryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLiteDatabase database = new CountryBaseHelper(mContext).getWritableDatabase();
                    int position = holder.getAdapterPosition();
                    Country country = mCountries.get(position);
                    String currency = country.getCurrency();
                    Log.d(TAG, "===点击了===：" + country.getName() + "，" + country.getCurrency());
                    saveStringToSP(currency);
                    finish();
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(CountryViewHolder holder, int position) {
            Country country = mCountries.get(position);
            holder.mCountryCurrency.setText(country.getCurrency());
            holder.mCountryName.setText(Utils.getCountryNameById(mContext,country.getName()));
            Glide.with(mContext).load(country.getFlagPath()).into(holder.mFlagImageView);
        }

        @Override
        public int getItemCount() {
            return mCountries.size();
        }
    }

    private void saveStringToSP(String currency) {
        SharedPreferences.Editor editor = mPreferences.edit();
        switch (mAmount){
            case ExchangeActivity.ORIGINAL_RESPONSE_CODE:
                editor.putString(ORIGINAL_CURRENCY,currency);
                editor.apply();
                break;
            case ExchangeActivity.RESULT_RESPONSE_CODE:
                editor.putString(RESULT_CURRENCY,currency);
                editor.apply();
                break;
            default:
                break;
        }
    }
}

