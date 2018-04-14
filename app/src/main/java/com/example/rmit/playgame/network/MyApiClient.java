package com.example.rmit.playgame.network;

import com.example.rmit.playgame.models.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by RMIT on 30/12/2017.
 */

public class MyApiClient {

    static Retrofit retrofit;
    public static final long CONNECTION_TIMEOUT = 60;
    public static final long READ_TIMEOUT = 60;
    public static final String API_END_POINT = "http://www.alchemyeducation.org/";



    public static Retrofit buildRetrofit() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS).build();

        return retrofit = new Retrofit.Builder()
                .baseUrl(API_END_POINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();

    }


}
