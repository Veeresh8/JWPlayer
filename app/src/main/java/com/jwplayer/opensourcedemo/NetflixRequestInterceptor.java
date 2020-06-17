package com.jwplayer.opensourcedemo;


import java.io.IOException;
import java.net.URI;


import okhttp3.Interceptor;
import okhttp3.Request.Builder;
import okhttp3.Response;

public class NetflixRequestInterceptor implements Interceptor {
    private static String esnPrefix;

    public Response intercept(Chain chain) throws IOException {
        URI uri = chain.request().url().uri();
        String str = "User-Agent";
        Builder addHeader = chain.request().newBuilder().addHeader(NetflixApiUtils.Headers.APP_VERSION, NetflixApiUtils.Headers.getAppVersion(uri.toString())).addHeader(NetflixApiUtils.Headers.REQUEST_UUID, "760a5c57-b71d-4fa2-b151-90c37ab8e5ca").addHeader(str, NetflixApiUtils.UserAgent.getFromApiUrl(uri.toString())).addHeader(NetflixApiUtils.Headers.HOST, NetflixApiUtils.Headers.getHost());
        String str2 = esnPrefix;
        if (str2 != null) {
            addHeader.addHeader(NetflixApiUtils.Headers.ESN_PREFIX, str2);
        }
        return chain.proceed(addHeader.build());
    }

    public static void setEsnPrefix(String str) {
        esnPrefix = str;
    }
}
