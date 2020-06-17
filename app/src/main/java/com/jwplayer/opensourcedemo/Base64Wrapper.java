package com.jwplayer.opensourcedemo;

import android.util.Base64;

import java.nio.charset.Charset;

public class Base64Wrapper {
    private static final String LOG_TAG = Base64Wrapper.class.getSimpleName();
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    public static String encode(String str) {
        return encode(str.getBytes(UTF_8));
    }

    public static String encode(byte[] bArr) {
        return Base64.encodeToString(bArr, 2);
    }

    public static byte[] decode(String str) {
        return Base64.decode(str, 2);
    }
}
