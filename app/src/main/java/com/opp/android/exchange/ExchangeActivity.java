package com.opp.android.exchange;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.stetho.Stetho;
import com.opp.android.exchange.database.CountryBaseHelper;
import com.opp.android.exchange.database.CountryDBSchema;
import com.opp.android.exchange.fragement.DatePickerFragment;
import com.opp.android.exchange.model.CountryStore;
import com.opp.android.exchange.model.Rate;
import com.opp.android.exchange.utils.UpdateRateUtils;
import com.opp.android.exchange.utils.Utils;

import java.text.DecimalFormat;

import static com.opp.android.exchange.CountryListActivity.*;

public class ExchangeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "|- 启动页面 -|";
    private static final String DIALOG_DATE = "DialogDate";
    public static final String AMOUNT = "amount";
    public static final int ORIGINAL_RESPONSE_CODE = 1;
    public static final int RESULT_RESPONSE_CODE = 2;
    public static final String TIME_ARGUMENTS = "timeArguments";

    public Context mAppContext;
    private Context mContext;
    private SharedPreferences mPreferences;
    private double mOriginalRateValue;
    private double mResultRateValue;
    private EditText originalAmount;
    private TextView resultAmount;
    private LinearLayout mOriginalLayout;
    private ImageView mOriginalFlag;
    private TextView mOriginalRate;
    private LinearLayout mResultLayout;
    private ImageView mResultFlag;
    private TextView mResultRate;
    private ImageButton mCalculationImageButton;
    private ImageView mDateImageButton;
    private TextView mDateTextView;
    private CountryStore mCountryStore;
    private SQLiteDatabase mDatabase;
    private long currentTimestamp;
    Bundle arguments;

    public void setCurrentTimestamp(long date){
        currentTimestamp = date;
        mDateTextView.setText(Utils.timestampToLocalString(mContext,currentTimestamp));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arguments = savedInstanceState;
        Log.d(TAG, "onCreate: 被调用了！");
        setContentView(R.layout.activity_exchange);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mAppContext = getApplicationContext();
        mContext = ExchangeActivity.this;
        mCountryStore = CountryStore.get(mContext);
        Stetho.initializeWithDefaults(mContext);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!Utils.IsTableExist(mContext)) {
                    mCountryStore.loadCountry();
                }
            }
        }).start();

        mPreferences = getSharedPreferences(COUNTRY_PREFERENCE, MODE_PRIVATE);

        originalAmount = (EditText) findViewById(R.id.original_amount);
        resultAmount = (TextView) findViewById(R.id.result_amount);

        mOriginalLayout = (LinearLayout) findViewById(R.id.original_layout);
        mOriginalLayout.setOnClickListener(this);
        mOriginalFlag = (ImageView) findViewById(R.id.original_amount_flag);
        mOriginalRate = (TextView) findViewById(R.id.original_amount_rate);
        mResultLayout = (LinearLayout) findViewById(R.id.result_layout);
        mResultLayout.setOnClickListener(this);
        mResultFlag = (ImageView) findViewById(R.id.result_amount_flag);
        mResultRate = (TextView) findViewById(R.id.result_amount_rate);
        mCalculationImageButton = (ImageButton) findViewById(R.id.calculation_button);
        mCalculationImageButton.setOnClickListener(this);
        mDateImageButton = (ImageView) findViewById(R.id.date_image_button);
        Glide.with(mContext).load(R.drawable.calendar).into(mDateImageButton);
        mDateImageButton.setOnClickListener(this);
        mDateTextView = (TextView) findViewById(R.id.date_text_view);
        mDateTextView.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: 被调用了！");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: 被调用了！");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "onSaveInstanceState: 被调用了！");
        savedInstanceState.putLong(TIME_ARGUMENTS,currentTimestamp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: 被调用了！");
        if (currentTimestamp == 0){
            if (arguments == null){
                currentTimestamp = System.currentTimeMillis();
            }else {
                currentTimestamp = arguments.getLong(TIME_ARGUMENTS,0l);
            }

        }
        Log.d(TAG, "时间戳为：" + currentTimestamp);

        setCurrentTimestamp(currentTimestamp);
        mDatabase = new CountryBaseHelper(mContext).getWritableDatabase();

        //判断当前日期的汇率数据在SQLite文件中是否更新
        if (!Utils.IsFieldExist(mDatabase, CountryDBSchema.CountryTable.NAME, Utils.timestampToDateString(currentTimestamp))) {
            //还没更新则启动后台任务更新数据库
            new UpdateTask().execute();
        } else {
            //已更新则显示账户UI
            updateAmount();
        }
        calculate();
    }

    public void updateAmount(){
        setAmount(ORIGINAL_CURRENCY, mOriginalFlag, mOriginalRate);
        setAmount(RESULT_CURRENCY, mResultFlag, mResultRate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.close();
    }

    /**
     * 显示账户上的国家货币名称及国旗
     */
    public void setAmount(String currency, ImageView imageView, TextView textView) {
        if (mPreferences != null) {
            String amountCurrency = mPreferences.getString(currency, "empty");
            if (!amountCurrency.equals("empty")) {
                Glide.with(mContext).load(Utils.getFlagAssetsPath(amountCurrency)).into(imageView);
                String dateString = Utils.timestampToDateString(currentTimestamp);
                Log.d(TAG, "设置账户汇率时日期为" + dateString);
                textView.setText(Utils.getCountryNameIdByCurrencyName(mContext, amountCurrency));
            }
        }
    }

    //    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        String currency;
//        Bitmap flag;
//        switch (requestCode) {
//            case ORIGINAL_RESPONSE_CODE:
//                currency = data.getStringExtra("currency_return");
//                mRate1 = data.getDoubleExtra("rate_return", 0.0);
//                flag = Utils.getFlagByName(mContext, Utils.getFlagNameByCurrencyName(currency));
//                mOriginalFlag.setImageBitmap(flag);
//                mOriginalRate.setText(mRate1 + "");
//                break;
//            case RESULT_RESPONSE_CODE:
//                currency = data.getStringExtra("currency_return");
//                mRate2 = data.getDoubleExtra("rate_return", 0.0);
//                flag = Utils.getFlagByName(mContext, Utils.getFlagNameByCurrencyName(currency));
//                mResultFlag.setImageBitmap(flag);
//                mResultRate.setText(mRate2 + "");
//                break;
//            default:
//        }
//    }

    @Override
    public void onClick(View v) {
        Intent intent = newIntent(mContext);
        if (v.getId() == R.id.original_amount){
        }else {
            hideInputForce(this);
        }
        switch (v.getId()) {
            case R.id.original_layout:
                intent.putExtra(AMOUNT, ORIGINAL_RESPONSE_CODE);
                startActivity(intent);
                break;
            case R.id.result_layout:
                intent.putExtra(AMOUNT, RESULT_RESPONSE_CODE);
                startActivity(intent);
                break;
            case R.id.calculation_button:
                calculate();
                break;
            case R.id.date_image_button:
            case R.id.date_text_view:
                FragmentManager fm = getSupportFragmentManager();
                DatePickerFragment dialog = new DatePickerFragment();
                dialog.show(fm, DIALOG_DATE);
                break;
            default:
                break;
        }
    }

    public static void hideInputForce(Activity activity) {
        if (activity == null || activity.getCurrentFocus() == null)
            return;
        ((InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, CountryListActivity.class);
    }

    /**
     * 计算
     */
    private void calculate(){
        String inputText = originalAmount.getText().toString();
        if (inputText == null || inputText.equals("")) {
            Toast.makeText(mAppContext, "请输入持有货币金额", Toast.LENGTH_SHORT).show();
        } else {
            double originalAmount = Double.parseDouble(inputText);
            DecimalFormat df = new DecimalFormat("#.00000");
            mOriginalRateValue = getRateValue(ORIGINAL_CURRENCY);
            mResultRateValue = getRateValue(RESULT_CURRENCY);
            double resultValue = originalAmount / mOriginalRateValue * mResultRateValue;
            String result = df.format(resultValue);
            if (resultValue < 1) {
                result = "0" + result;
            }
            resultAmount.setText(result);
        }
    }

    /**
     * 根据传入账户名称（原始账户/结果账户，ListActivity中定义）获取当前选中国家
     * ，根据国家名称查询数据库获取对应汇率数值，返回-1时查询失败（暂未更新完成）。
     */
    private double getRateValue(String currency){
        double rate = 0.0;
        if (mPreferences != null) {
            String amountCurrency = mPreferences.getString(currency, "empty");
            if (!amountCurrency.equals("empty")) {
                String dateString = Utils.timestampToDateString(currentTimestamp);
                rate = mCountryStore.getRate(amountCurrency, dateString);
                Log.d(TAG, "setAmount: 选择后返回的汇率为" + rate);
                if (rate < 0.0) {
                    Toast.makeText(mContext, "数据还未跟新完成,请稍后再试", Toast.LENGTH_SHORT);
                }
            }
        }
        return rate;
    }

    class UpdateTask extends AsyncTask<Void, Void, Rate> {

        @Override
        protected Rate doInBackground(Void... voids) {
            Rate rate;
            try {
                rate = new UpdateRateUtils().fetchCountry();
                Log.d(TAG, "——————返回数据——————:" + rate.getTimestamp());

            } catch (Exception e) {
                Log.d(TAG, "有问题！！！！：" + e.toString());
                return null;
            }
            return rate;
        }

        @Override
        protected void onPostExecute(Rate rate) {
            if (rate != null) {
                CountryStore countryStore = CountryStore.get(mContext);
                countryStore.loadRates(rate);
                Toast.makeText(mAppContext, "更新成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mAppContext, "更新失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
