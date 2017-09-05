package com.opp.android.exchange;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.opp.android.exchange.databinding.CountryListBinding;
import com.opp.android.exchange.databinding.ListItemCountryBinding;

import java.util.List;

/**
 * Created by OPP on 2017/8/29.
 */

public class CountryListFragment extends Fragment {
    private static final String TAG = "CountryListFragment";
    private RecyclerView mCountryRecyclerView;
    private CountryAdapter mAdapter;
    private List<Country> mCountries;
    private CountryStore mCountryStore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CountryListBinding binding = DataBindingUtil.inflate(inflater,R.layout.country_list,container,false);
        mCountryRecyclerView = binding.countryRecyclerView;
        mCountryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return binding.getRoot();
    }

    private class CountryHolder extends RecyclerView.ViewHolder{
        private ListItemCountryBinding mBinding;
//        private ImageView mFlag;

        public CountryHolder(ListItemCountryBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setViewModel(new CountryViewModel(mCountryStore));
//            mFlag = binding.getRoot().findViewById(R.id.list_item_flag_image_view);
        }

        public void bindCountry(Country country){
            mBinding.getViewModel().setCountry(country);
            mBinding.executePendingBindings();
//            mFlag.setImageBitmap(country.getFlag());
        }
    }

    private class CountryAdapter extends RecyclerView.Adapter<CountryHolder>{
        public CountryAdapter(List<Country> countries){
            mCountries = countries;
        }

        @Override
        public CountryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            ListItemCountryBinding binding= DataBindingUtil.inflate(layoutInflater,R.layout.list_item_country,parent,false);
            return new CountryHolder(binding);
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

    private void updateUI(){
        mCountryStore = CountryStore.get(getActivity(),);
        mCountries = mCountryStore.getCountries();
        mAdapter = new CountryAdapter(mCountries);
        mCountryRecyclerView.setAdapter(mAdapter);
    }
}
