package com.opp.android.exchange;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;


/**
 * Created by OPP on 2017/8/26.
 */

public class ExchangeFragment extends Fragment {
    private static final String TAG = "ExchangeFragment";
    private EditText originalAmount;
    private TextView resultAmount;
    private Button mOriginalButton;
    private Button mResultButton;
    private ImageButton mCalculationImageButton;
    private static final String FLAG_FOLDER = "flag_pngs";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            AssetManager mAssets = getActivity().getAssets();
            String[] flagNames = mAssets.list(FLAG_FOLDER);
            Log.d(TAG, "" + flagNames.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_exchange,container,false);
        originalAmount = v.findViewById(R.id.original_amount);
        resultAmount = v.findViewById(R.id.result_amount);
        mOriginalButton = v.findViewById(R.id.original_button);
        mOriginalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),CountryListActivity.class);
                startActivity(intent);
            }
        });
        mResultButton = v.findViewById(R.id.result_button);
        mResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),CountryListActivity.class);
                startActivity(intent);
            }
        });
        mCalculationImageButton = v.findViewById(R.id.calculation_button);
//        int i = R.drawable.ic_aed;
//        mCalculationImageButton.setImageResource(i);
        int i = getResources().getIdentifier("ic_AED","drawable","com.opp.android.exchange");
        return v;
    }

}
