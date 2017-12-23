//package com.opp.android.exchange.deleteClass;
//
//import android.content.Intent;
//import android.content.res.AssetManager;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Bitmap;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.opp.android.exchange.model.Country;
//import com.opp.android.exchange.model.CountryStore;
//import com.opp.android.exchange.ExchangeActivity;
//import com.opp.android.exchange.R;
//import com.opp.android.exchange.database.CountryDBSchema;
//import com.opp.android.exchange.model.CountryRates;
//import com.opp.android.exchange.utils.UpdateRateUtils;
//import com.opp.android.exchange.utils.Utils;
//
//import java.io.IOException;
//import java.util.List;
//
//import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;
//
//
///**
// * Created by OPP on 2017/8/26.
// */
//
//public class ExchangeFragment extends Fragment implements View.OnClickListener{
//    private static final String TAG = "ExchangeFragment";
//    public static final int ORIGINAL_RESPONSE_CODE = 1;
//    public static final int RESULT_RESPONSE_CODE = 2;
//    private double mRate1;
//    private double mRate2;
//    private EditText originalAmount;
//    private TextView resultAmount;
//    private LinearLayout mOriginalLayout;
//    private ImageView mOriginalFlag;
//    private TextView mOriginalRate;
//    private LinearLayout mResultLayout;
//    private ImageView mResultFlag;
//    private TextView mResultRate;
//    private ImageButton mCalculationImageButton;
//    private Button mUpdateButton;
//    private static final String FLAG_FOLDER = "flag_pngs";
//
////    public static ExchangeFragment newInstance(String currency){
////        Bundle args = new Bundle();
////        args.putSerializable(currency);
////    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        try {
//            AssetManager mAssets = getActivity().getAssets();
//            String[] flagNames = mAssets.list(FLAG_FOLDER);
//            Log.d(TAG, "" + flagNames.length);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.activity_exchange, container, false);
//
//        originalAmount = v.findViewById(R.id.original_amount);
//        originalAmount.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//
//        resultAmount = v.findViewById(R.id.result_amount);
//
//        mOriginalLayout = v.findViewById(R.id.original_layout);
//        mOriginalLayout.setOnClickListener(this);
//
//        mOriginalFlag = v.findViewById(R.id.original_amount_flag);
//
//        mOriginalRate = v.findViewById(R.id.original_amount_rate);
//        mOriginalRate.setText("点击选择持有货币");
//
//        mResultLayout = v.findViewById(R.id.result_layout);
//        mResultLayout.setOnClickListener(this);
//
//        mResultFlag = v.findViewById(R.id.result_amount_flag);
//
//        mResultRate = v.findViewById(R.id.result_amount_rate);
//        mResultRate.setText("点击选择兑换货币");
//
//        mCalculationImageButton = v.findViewById(R.id.calculation_button);
//        mCalculationImageButton.setOnClickListener(this);
//
//        mUpdateButton = v.findViewById(R.id.update_button);
//        mUpdateButton.setOnClickListener(this);
//        return v;
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        String currency;
//        Bitmap flag;
//        switch (requestCode){
//            case ORIGINAL_RESPONSE_CODE:
//                currency = data.getStringExtra("currency_return");
//                mRate1 = data.getDoubleExtra("rate_return",0.0);
//                flag = Utils.getFlagByName(getActivity(),Utils.getFlagNameByCurrencyName(currency));
//                mOriginalFlag.setImageBitmap(flag);
//                mOriginalRate.setText(mRate1 + "");
//                break;
//            case RESULT_RESPONSE_CODE:
//                currency = data.getStringExtra("currency_return");
//                mRate2 = data.getDoubleExtra("rate_return",0.0);
//                flag = Utils.getFlagByName(getActivity(),Utils.getFlagNameByCurrencyName(currency));
//                mResultFlag.setImageBitmap(flag);
//                mResultRate.setText(mRate2 + "");
//                break;
//            default:
//        }
//    }
//
//    @Override
//    public void onClick(View view) {
//        Intent intent = ExchangeActivity.newIntent(getActivity());
//        switch (view.getId()){
//            case R.id.original_layout:
//                startActivityForResult(intent,ORIGINAL_RESPONSE_CODE);
//                break;
//            case R.id.result_layout:
//                startActivityForResult(intent,RESULT_RESPONSE_CODE);
//                break;
//            case R.id.calculation_button:
//                String inputText = originalAmount.getText().toString();
//                if (inputText == null || inputText.equals("")){
//                    Toast.makeText(getActivity(),"请输入持有货币金额",Toast.LENGTH_SHORT).show();
//                }else{
//                    double origianlAmount = Double.parseDouble(inputText);
//                    resultAmount.setText((origianlAmount / mRate1 * mRate2) + "");
//                }
//                break;
//            case R.id.update_button:
//                new UpdateTask().execute();
//                break;
//        }
//    }
//
//
//    //调用CountryStore的loadFlags方法更新数据库
//    class UpdateTask extends AsyncTask<Void, Void, Boolean> {
//
//        @Override
//        protected Boolean doInBackground(Void... voids) {
//            try {
//                CountryRates countryRates = new UpdateRateUtils().fetchCountry();
//                CountryStore countryStore = CountryStore.get(getActivity());
//                List<Country> countries = countryStore.loadRates(getActivity(), countryRates);
//            }catch (Exception e){
//                Log.d(TAG, "有问题！！！！：" + e.toString());
//                return false;
//            }
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            if (result){
//                Toast.makeText(getActivity(),"更新成功",Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(getActivity(),"更新失败",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private boolean IsTableExist() {
//        boolean isTableExist = true;
//        SQLiteDatabase db = openOrCreateDatabase("countryBase.db",null, null);
//        Cursor c=db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name = "+ CountryDBSchema.CountryTable.NAME, null);
//        if (c.getInt(0)==0) {
//            isTableExist=false;
//        }
//        c.close();
//        db.close();
//        return isTableExist;
//    }
//
//}
