package com.example.korji.mobilecurrencyconverter.domain;

/**
 * Created by korji on 6/26/15.
 */
public class Currency {
    String code;
    double rate;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
