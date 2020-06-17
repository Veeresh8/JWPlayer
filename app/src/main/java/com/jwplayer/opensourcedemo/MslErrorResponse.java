package com.jwplayer.opensourcedemo;

public class MslErrorResponse {
    private int errorcode;

    public static class ErrorTypes {
        public static final int ERROR_EXPIRED_IDENTITY = 7;
        public static final int GENERIC = -2;
    }

    public int getErrorcode() {
        return this.errorcode;
    }
}
