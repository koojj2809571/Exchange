package com.opp.android.exchange;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.opp.android.exchange.utils.CountryRates;
import com.opp.android.exchange.utils.UpdateRateUtils;

import java.io.IOException;
import java.util.List;


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
    private Button mUpdateButton;
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
        View v = inflater.inflate(R.layout.fragment_exchange, container, false);
        originalAmount = v.findViewById(R.id.original_amount);
        resultAmount = v.findViewById(R.id.result_amount);
        mOriginalButton = v.findViewById(R.id.original_button);
        mOriginalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CountryListActivity.class);
                startActivity(intent);
            }
        });
        mResultButton = v.findViewById(R.id.result_button);
        mResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CountryListActivity.class);
                startActivity(intent);
            }
        });
        mCalculationImageButton = v.findViewById(R.id.calculation_button);
        mUpdateButton = v.findViewById(R.id.update_button);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UpdateTask().execute();
            }
        });
        return v;
    }

    //调用CountryStore的loadFlags方法更新数据库
    class UpdateTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                CountryRates countryRates = new UpdateRateUtils().fetchCountry();
                CountryStore countryStore = CountryStore.get(getActivity());
                List<Country> countries = countryStore.loadCountry(getActivity(), countryRates);
            }catch (Exception e){
                Log.d(TAG, "出问题啦：" + e.toString());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result){
                Toast.makeText(getActivity(),"更新成功",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getActivity(),"更新失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean IsTableExist(Context context, String tableName) {
        boolean isTableExist = false;
        if (tableName == null) {
            return false;
        }
        SQLiteDatabase db = null;
        Cursor cursor = null;
        return isTableExist;
    }

}
