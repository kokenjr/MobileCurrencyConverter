package com.example.korji.mobilecurrencyconverter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.korji.mobilecurrencyconverter.domain.Currency;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;


public class ConverterActivity extends Activity {

    public static final String REST_ENDPOINT_URL = "http://api.fixer.io";
    private BarChart bcConversion;

    protected List<Currency> currencies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Currency poundCurrency = new Currency();
        poundCurrency.setCode("GBP");
        Currency euroCurrency = new Currency();
        euroCurrency.setCode("EUR");
        Currency yenCurrency = new Currency();
        yenCurrency.setCode("JPY");
        Currency realCurrency = new Currency();
        realCurrency.setCode("BRL");

        currencies = new ArrayList<>();
        currencies.addAll(Arrays.asList(poundCurrency, euroCurrency, yenCurrency, realCurrency));

        new GetConversionTask(getApplicationContext()).execute();
    }



    public void onConvert(View view) {
        setChart();
        showTextConversions();
        hideKeyboard();
    }

    protected void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showTextConversions() {
        EditText etAmount = (EditText) findViewById(R.id.etAmountUsd);
        String amountText = etAmount.getText().toString();
        TextView tvResult = (TextView) findViewById(R.id.tvResult);

        if (!amountText.isEmpty()) {
            String resultString = "";
            for (Currency currency : currencies) {
                double conversion = getConversion(amountText, currency);
                resultString += String.format("%.2f", conversion) + " " + currency.getCode() + "\n";
            }
            tvResult.setText(resultString);
        } else {
            tvResult.setText(getResources().getString(R.string.fill_amount));
        }
    }

    protected double getConversion(String amountText, Currency currency) {
        double amountUsd = Double.parseDouble(amountText);
        return amountUsd * currency.getRate();
    }

    private class GetConversionTask extends AsyncTask<Void, Void, Response> {
        private Context context;

        public GetConversionTask(Context applicationContext) {
            context = applicationContext;
        }

        @Override
        protected Response doInBackground(Void... params) {
            Response converterResponse = null;
            try {
                RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(REST_ENDPOINT_URL).build();
                RestApi restApi = restAdapter.create(RestApi.class);
                converterResponse = restApi.getConversion();
                TypedInput body = converterResponse.getBody();
                String string = new String(((TypedByteArray) body).getBytes());
                JSONObject jsonResponse = new JSONObject(string);
                JSONObject rates = jsonResponse.getJSONObject("rates");

                for (Currency currency : currencies) {
                    double rate = rates.getDouble(currency.getCode());
                    currency.setRate(rate);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return converterResponse;
        }

        /**
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
//            showLoadingProgressDialog();
        }

        @Override
        protected void onPostExecute(Response response) {
//            dismissProgressDialog();

            //Log.d(TAG, "login response: " + response);
            if (response != null) {
                setContentView(R.layout.activity_converter);
                bcConversion  = (BarChart) findViewById(R.id.bcConversion);
                XAxis xAxis = bcConversion.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setSpaceBetweenLabels(0);
                xAxis.setDrawGridLines(false);
                bcConversion.getAxisLeft().setDrawGridLines(false);
                setChart();
            }
        }
    }

    private void setChart() {

        List<String> xVals = new ArrayList<>();
        for (Currency currency : currencies) {
            xVals.add(currency.getCode());
        }

        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();

        EditText etAmount = (EditText) findViewById(R.id.etAmountUsd);
        String amountText = etAmount.getText().toString();
        if (!amountText.isEmpty()) {
            for (int i = 0; i < xVals.size(); i++) {
                double conversion = getConversion(amountText, currencies.get(i));
                yVals.add(new BarEntry((float) conversion, i));
            }
        } else {
            for (int i = 0; i < xVals.size(); i++) {
                yVals.add(new BarEntry(0f, i));
            }
        }

        BarDataSet barDataSet = new BarDataSet(yVals, "Rates");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);

        BarData data = new BarData(xVals, dataSets);

        bcConversion.animateXY(2500, 2500);
        bcConversion.setData(data);
        bcConversion.invalidate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
}
