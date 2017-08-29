package com.opp.android.exchange;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by OPP on 2017/8/29.
 */

public class CountryListFragment extends Fragment {
    private RecyclerView mCountryRecyclerView;
    private CountryAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.country_list,container,false);
        mCountryRecyclerView = v.findViewById(R.id.country_recycler_view);
        mCountryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    private class CountryHolder extends RecyclerView.ViewHolder{
        private ImageView mFlagImageView;
        private TextView mCountryNameTextView;
        private TextView mCountryCurrencyTextView;
        private Country mCountry;

        public CountryHolder(View itemView) {
            super(itemView);
            mFlagImageView = itemView.findViewById(R.id.list_item_flag_image_view);
            mCountryNameTextView = itemView.findViewById(R.id.list_item_country_chinese_text_view);
            mCountryCurrencyTextView = itemView.findViewById(R.id.list_item_country_english_text_view);
        }

        public void bindCountry(Country country){
            mCountry = country;
            mFlagImageView.setImageResource(R.mipmap.ic_launcher);
            mCountryNameTextView.setText(mCountry.getName());
            mCountryCurrencyTextView.setText(mCountry.getCurrency());
        }
    }

    private class CountryAdapter extends RecyclerView.Adapter<CountryHolder>{

        private List<Country> mCountries;

        public CountryAdapter(List<Country> countries){
            mCountries = countries;
        }

        @Override
        public CountryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(R.layout.list_item_country,parent,false);
            return new CountryHolder(v);
        }

        @Override
        public void onBindViewHolder(CountryHolder holder, int position) {
            Country country = mCountries.get(position);
            holder.bindCountry(country);
        }

        @Override
        public int getItemCount() {
            return mCountries.size();
        }
    }
}
