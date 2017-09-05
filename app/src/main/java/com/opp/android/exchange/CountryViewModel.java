package com.opp.android.exchange;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by OPP on 2017/9/2.
 */

public class CountryViewModel extends BaseObservable{
    private Country mCountry;
    private CountryStore mCountryStore;

    public CountryViewModel(CountryStore countryStore){
        mCountryStore = countryStore;
    }

    public Country getCountry(){
        return mCountry;
    }

    public void setCountry(Country country){
        mCountry = country;
        notifyChange();
    }

    @BindingAdapter("flagpng")
    public static void showFlag(ImageView imageView,Bitmap flag){
        imageView.setImageBitmap(flag);
    }

    public Bitmap getFlag(){
        return mCountry.getFlag();
    }

    public String getName(){
        return mCountry.getName();
    }

    public String getCurrency(){
        return mCountry.getCurrency();
    }

    public double getRate(){
        return mCountry.getRate();
    }
}
