package com.opp.android.exchange.fragement;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.opp.android.exchange.ExchangeActivity;
import com.opp.android.exchange.R;
import com.opp.android.exchange.database.CountryBaseHelper;
import com.opp.android.exchange.database.CountryDBSchema;
import com.opp.android.exchange.model.CountryStore;
import com.opp.android.exchange.model.Rate;
import com.opp.android.exchange.utils.UpdateRateUtils;
import com.opp.android.exchange.utils.Utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.opp.android.exchange.CountryListActivity.ORIGINAL_CURRENCY;
import static com.opp.android.exchange.CountryListActivity.RESULT_CURRENCY;


/**
 * Created by OPP on 2017/12/3.
 */

public class DatePickerFragment extends DialogFragment {
    private static final String TAG = "--日期选择器--";
    private DatePicker mDatePicker;
    private ExchangeActivity mExchangeActivity;
    long date;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mExchangeActivity = (ExchangeActivity) getActivity();
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
        long current = System.currentTimeMillis();
        final Timestamp timestamp = new java.sql.Timestamp(0);
        long minDate = timestamp.valueOf("2005-01-01 00:00:00").getTime();
        mDatePicker = v.findViewById(R.id.dialog_date_picker);
        mDatePicker.setMaxDate(current);
        mDatePicker.setMinDate(minDate);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.option_date)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String year = String.valueOf(mDatePicker.getYear());
                        int monthInt = mDatePicker.getMonth() + 1;
                        String month;
                        if (monthInt < 10) {
                            month = "0" + monthInt;
                        } else {
                            month = monthInt + "";
                        }
                        int dayInt = mDatePicker.getDayOfMonth();
                        String day;
                        if (dayInt < 10) {
                            day = "0" + dayInt;
                        } else {
                            day = dayInt + "";
                        }
                        String str = year + "-" + month + "-" + day + " 00:00:00";
                        Log.d("选择时间", "onClick: " + str);
                        date = timestamp.valueOf(str).getTime();
                        Log.d("选择时间", "onClick: " + date);
                        final String dateString = Utils.timestampToLocalString(getActivity(), date);
                        Log.d("选择时间", "onClick: " + dateString);
                        mExchangeActivity.setCurrentTimestamp(date);
//                        TextView tv = (TextView) mExchangeActivity.findViewById(R.id.date_text_view);
//                        tv.setText(dateString);
                        new Thread(new Runnable() {
                            SQLiteDatabase database = new CountryBaseHelper(getActivity()).getWritableDatabase();

                            @Override
                            public void run() {
                                try {
                                    if (!Utils.IsFieldExist(database, CountryDBSchema.CountryTable.NAME, Utils.timestampToDateString(date))) {
                                        new DateUpdateTask().execute(date);
                                    }
                                } finally {
                                    database.close();
                                }
                            }
                        }).start();
                    }
                })
                .create();
    }

    class DateUpdateTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... date) {
            Rate rate;
            try {
                rate = new UpdateRateUtils().fetchCountry(date[0]);
                Log.d(TAG, "——————返回数据——————:" + rate.getTimestamp());
                CountryStore countryStore = CountryStore.get(getActivity());
                countryStore.loadRates(rate);
            } catch (Exception e) {
                Log.d(TAG, "有问题！！！！：");
            }
            return null;
        }
    }
}
