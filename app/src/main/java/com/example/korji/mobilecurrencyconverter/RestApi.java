package com.example.korji.mobilecurrencyconverter;


import retrofit.client.Response;
import retrofit.http.GET;

/**
 * Created by korji on 6/25/15.
 */
public interface RestApi {
    @GET("/latest?base=USD")
    Response getConversion();
}
