//package com.opp.android.exchange._deleteClass;
//
//import android.databinding.BaseObservable;
//import android.databinding.Bindable;
//import android.databinding.BindingAdapter;
//import android.graphics.Bitmap;
//import android.widget.ImageView;
//
//import com.opp.android.exchange.model.Country;
//import com.opp.android.exchange.model.CountryStore;
//
///**
// * Created by OPP on 2017/9/2.
// */
//
//public class CountryViewModel extends BaseObservable{
//    private Country mCountry;
//    private CountryStore mCountryStore;
//
//    public CountryViewModel(CountryStore countryStore){
//        mCountryStore = countryStore;
//    }
//
//    public Country getCountry(){
//        return mCountry;
//    }
//
//    public void setCountry(Country country){
//        mCountry = country;
//        notifyChange();
//    }
//
//    @BindingAdapter("flagpng")
//    public static void showFlag(ImageView imageView,Bitmap flag){
//        imageView.setImageBitmap(flag);
//    }
//
//    @Bindable
//    public Bitmap getFlag(){
//        return mCountry.getFlag();
//    }
//
//    @Bindable
//    public String getName(){
//        return mCountry.getName();
//    }
//
//    @Bindable
//    public String getCurrency(){
//        return mCountry.getCurrency();
//    }
//
//    @Bindable
//    public double getRate(){
//        return mCountry.getRate();
//    }
//}
