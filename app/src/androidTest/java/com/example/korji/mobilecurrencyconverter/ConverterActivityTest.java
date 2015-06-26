package com.example.korji.mobilecurrencyconverter;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.korji.mobilecurrencyconverter.domain.Currency;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

/**
 * Created by korji on 6/26/15.
 */
public class ConverterActivityTest extends ActivityInstrumentationTestCase2<ConverterActivity> {

    private ConverterActivity converterActivity;
    private Button btnConvert;
    private EditText etAmount;
    private TextView tvResult;
    private BarChart bcConversion;

    public ConverterActivityTest() {
        super(ConverterActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        converterActivity = getActivity();
        btnConvert = (Button) converterActivity.findViewById(R.id.btnConvert);
        etAmount = (EditText) converterActivity.findViewById(R.id.etAmountUsd);
        tvResult = (TextView) converterActivity.findViewById(R.id.tvResult);
        bcConversion = (BarChart) converterActivity.findViewById(R.id.bcConversion);
    }

    @MediumTest
    public void testPreconditions() {
        assertNotNull("converterActivity is null", converterActivity);
        assertNotNull("btnConvert is null", btnConvert);
        assertNotNull("etAmount is null", etAmount);
        assertNotNull("tvResult is null", tvResult);
        assertNotNull("bcConversion is null", bcConversion);
    }

    @MediumTest
    public void testCheckCurrency() {
        List<Currency> currencies = converterActivity.currencies;
        assertNotNull("currencies is null", currencies);
        assertEquals(4, currencies.size());

        Currency poundCurrency = currencies.get(0);
        assertEquals("GBP", poundCurrency.getCode());
        assertNotNull("poundCurrency.getRate() is null", poundCurrency.getRate());
    }

    @MediumTest
    public void testCheckChartSetup() {
        BarData barData = bcConversion.getBarData();
        assertNotNull("barData is null", barData);

        BarDataSet barDataSet = barData.getDataSetByIndex(0);
        assertNotNull("barDataSet is null", barDataSet);

        List<String> xVals = barData.getXVals();
        assertNotNull("xVals is null", xVals);
        assertEquals(4, xVals.size());
        assertEquals("GBP", xVals.get(0));
    }

    @MediumTest
    public void testButtonClickNoInput() {
        String fill_amount = converterActivity.getString(R.string.fill_amount);

        TouchUtils.clickView(this, btnConvert);
        assertEquals(fill_amount, tvResult.getText());

        List<BarEntry> barEntries = bcConversion.getBarData().getDataSetByIndex(0).getYVals();
        assertNotNull("barEntries is null", barEntries);

        BarEntry barEntry = barEntries.get(0);
        assertNotNull("barEntry is null", barEntry);

        float gbpConversion = barEntry.getVal();
        assertNotNull("gbpConversion is null", gbpConversion);
        assertEquals(0.0f, gbpConversion);
    }

    @MediumTest
    public void testButtonClickWithInput() {
        String amount = "10";
        etAmount.setText(amount);
        String fill_amount = converterActivity.getString(R.string.fill_amount);

        TouchUtils.clickView(this, btnConvert);
        assertNotSame(fill_amount, tvResult.getText());

        List<BarEntry> barEntries = bcConversion.getBarData().getDataSetByIndex(0).getYVals();
        assertNotNull("barEntries is null", barEntries);

        BarEntry barEntry = barEntries.get(0);
        assertNotNull("barEntry is null", barEntry);

        float gbpConversion = barEntry.getVal();
        assertNotNull("gbpConversion is null", gbpConversion);

        Currency poundCurrency = converterActivity.currencies.get(0);
        double conversion = converterActivity.getConversion(amount, poundCurrency);
        assertEquals((float) conversion, gbpConversion);
    }
}
