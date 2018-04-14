package com.example.rmit.playgame;

import android.app.Application;

import com.example.rmit.playgame.network.ApiClient;
import com.example.rmit.playgame.network.MyApiClient;

import retrofit2.Retrofit;

/**
 * Created by RMIT on 28/12/2017.
 */

public class PlayGameApplication extends Application {

    private Retrofit mRetrofit;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public Retrofit getRetrofit() {
        return this.mRetrofit;
    }



    public void setRetrofit() {
        mRetrofit = MyApiClient.buildRetrofit();
    }


}
