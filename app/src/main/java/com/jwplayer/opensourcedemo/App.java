package com.jwplayer.opensourcedemo;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;

public class App extends Application {

    private static App instance;
    private ExecutorService taskExecutor;
    private OkHttpClient okHttpClient;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        this.taskExecutor = Executors.newSingleThreadExecutor();
        okHttpClient = new OkHttpClient();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public ExecutorService getTaskExecutor() {
        return taskExecutor;
    }
}
