package com.jwplayer.opensourcedemo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NetflixError {
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("errorDetails")
    @Expose
    private String errorDetails;
    @SerializedName("errorDisplayMessage")
    @Expose
    private String errorDisplayMessage;

    private class Data {
        /* access modifiers changed from: private */
        @SerializedName("LOCAL_MESSAGE")
        @Expose
        public String localMessage;

        private Data() {
        }
    }

    public NetflixError(String str, String str2) {
        this.errorDisplayMessage = str;
        this.errorDetails = str2;
    }

    public String getErrorDetails() {
        Data data2 = this.data;
        if (data2 == null || data2.localMessage == null || this.data.localMessage.isEmpty()) {
            return this.errorDetails;
        }
        return this.data.localMessage;
    }

    public String getErrorDisplayMessage() {
        return this.errorDisplayMessage;
    }
}
