package com.jwplayer.opensourcedemo;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class MslNativeSession {

    private static final String TAG = "MslNativeSession";
    private static final String CHARSET_UTF8 = "UTF-8";
    public static final String MSL_API_URL = NetflixApiUtils.ApiBaseUrls.MSL_API_URL;

    private static String NETFLIX_APPBOOT_URL;
    private static MslNativeSession mslNativeSession;
    private final IdentityProvider identityProvider;
    private final ExecutorService mslHelperExecutor;
    private final Gson gson;
    private final RequestQueue mslRequestQueue;
    private int cnetStatus = -1;
    private String identity;
    private NetflixManifest netflixManifest;


    public interface MslCallback<T> {
        void onFailure();

        void onFailure(MslErrorResponse mslErrorResponse);

        void onFailure(NetflixError netflixError);

        void onSuccess(T t);
    }

    public interface Callback<E> {
        void result(E e);
    }

    public static boolean init() {
        synchronized (MslNativeSession.class) {
            try {
                IdentityProvider createEsnProvider = IdentityProvider.createEsnProvider();
                if (mslNativeSession != null) {
                    if (mslNativeSession.cnetStatus != -1) {
                        mslNativeSession.reinitialize(createEsnProvider);
                    }
                }
                mslNativeSession = new MslNativeSession(createEsnProvider);
            } catch (Exception exception) {
                exception.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static synchronized MslNativeSession getInstance() {
        MslNativeSession mslNativeSession2;
        synchronized (MslNativeSession.class) {
            if (mslNativeSession == null) {
                Log.d(TAG, "getInstance() called!");
            }
            mslNativeSession2 = mslNativeSession;
        }
        return mslNativeSession2;
    }

    private void reinitialize(IdentityProvider identityProvider) {

    }

    private String getIdentity() {
        if (this.identity == null) {
            this.identity = this.identityProvider.getCurrentIdentity();
        }
        return this.identity;
    }


    private MslNativeSession(IdentityProvider identityProvider2) {
        this.identityProvider = identityProvider2;
        this.NETFLIX_APPBOOT_URL = NetflixApiUtils.ApiBaseUrls.getAppbootApiUrl(identityProvider2.getESNPrefix());
        this.mslHelperExecutor = App.getInstance().getTaskExecutor();
        this.mslRequestQueue = Volley.newRequestQueue(App.getInstance());
        this.gson = new Gson();
        NetflixRequestInterceptor.setEsnPrefix(identityProvider2.getESNPrefix());
        initState(getIdentity(), NetflixApiUtils.AppVersions.MSL_API_FULL_VERSION, NetflixApiUtils.Queries.Values.LANGUAGES_DEVICE, NetflixApiUtils.Queries.Values.getFfbc());
        doKeyExchange(null);
    }

    public void doKeyExchange(final Callback<Boolean> callback) {
        final int[] keyExchangeReponse = {-1};

        this.mslHelperExecutor.execute(new Runnable() {
            public void run() {
                String str = "errordata";
                try {
                    Log.d(TAG, "Begin doKeyExchange");
                    Bridge.getState().openSession();
                    RequestFuture<String> requestFuture = MslNativeSession.this.post(MslNativeSession.this.NETFLIX_APPBOOT_URL, Bridge.startSession().getBytes(MslNativeSession.CHARSET_UTF8), MslNativeSession.this.getMslHeaders());
                    String str2 = (String) requestFuture.get();
                    if (str2.contains(str)) {
                        Exception exception = new Exception(new String(Base64Wrapper.decode(new JSONObject(str2).getString(str))));
                        Log.e(TAG, "doKeyExchange failed due to errordata: " + exception.getMessage());
                    } else {
                        keyExchangeReponse[0] = Bridge.replyStartSession(str2);
                        Log.e(TAG, "doKeyExchange success");
                    }
                    MslNativeSession.this.runOnUiThread(new Runnable() {
                        public void run() {
                            if (callback != null) {
                                callback.result(Boolean.valueOf(keyExchangeReponse[0] == 0));
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "doKeyExchange failed due to an exception: " + e.getMessage());
                    Callback callback = null;
                    if (callback != null) {
                        callback.result(Boolean.valueOf(false));
                    }
                }
            }
        });
    }

    private void initState(String str, String str2, String str3, String str4) {
        this.cnetStatus = Bridge.init(str, str2, str3, str4);
    }


    public synchronized byte[] doWidevineProvisioning(String str, byte[] bArr) throws Exception {
        Log.d(TAG, "Begin doWidevineProvisioning");
        if (Looper.myLooper() != Looper.getMainLooper()) {
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalThreadStateException("doWidevineProvisioning cannot be called on Main Thread!");
        }
        return ((String) post(str.concat("&signedRequest=").concat(new String(bArr)), new byte[0], getProvisioningHeaders()).get()).getBytes();
    }

    private RequestFuture<String> post(String url, byte[] bytes, Map<String, String> headers) throws IOException {
        final byte[] bArr2 = bytes;
        final Map<String, String> map2 = headers;
        RequestFuture<String> future = RequestFuture.newFuture();
        Log.d(TAG, "Requesting - " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, future, future) {
            public byte[] getBody() throws AuthFailureError {
                return bArr2;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                return map2;
            }

            public Response<String> parseNetworkResponse(NetworkResponse networkResponse) {
                Log.d(TAG, "Network response: " + networkResponse.toString());
                return super.parseNetworkResponse(networkResponse);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1.0f));
        mslRequestQueue.add(stringRequest);

        return future;
    }

    public boolean getManifest(final String videoID, final MslCallback<NetflixManifest> mslCallback) {
        this.netflixManifest = null;
        this.mslHelperExecutor.execute(new Runnable() {
            public void run() {
                String error = "errordata";
                try {
                    if (Bridge.getState().getCryptoSession() == null) {
                        NetflixError netflixError = new NetflixError("getCryptoSession", "getCryptoSession was NULL");
                        mslCallback.onFailure(netflixError);
                        return;
                    }
                    final NetflixError netflixError2 = null;
                    String manifestResult = (String) MslNativeSession.this.post(MslNativeSession.MSL_API_URL, Bridge.getManifest(videoID).getBytes(MslNativeSession.CHARSET_UTF8), MslNativeSession.this.getMslHeaders()).get();
                    if (manifestResult.contains(error)) {
                        Log.e(TAG, "Manifest failed");
                        String str3 = new String(Base64Wrapper.decode(new JSONObject(manifestResult).getString(error)));
                        mslCallback.onFailure(new Gson().fromJson(manifestResult, MslErrorResponse.class));
                        return;
                    }
                    String replyGetManifest = Bridge.replyGetManifest(manifestResult, videoID);
                    if (replyGetManifest.contains("errorDisplayMessage")) {
                        Log.e(TAG, "replyGetManifest failed");
                    } else {
                        MslNativeSession.this.netflixManifest = new Gson().fromJson(replyGetManifest, NetflixManifest.class);
                        Log.d(TAG, "Manifest success");
                    }
                    MslNativeSession.this.runOnUiThread(new Runnable() {
                        public void run() {
                            if (MslNativeSession.this.netflixManifest != null) {
                                mslCallback.onSuccess(MslNativeSession.this.netflixManifest);
                            } else {
                                mslCallback.onFailure(netflixError2);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    mslCallback.onFailure();
                }
            }
        });
        return true;
    }

    private Map<String, String> getProvisioningHeaders() {
        HashMap hashMap = new HashMap();
        hashMap.put("Accept", "*/*");
        hashMap.put("User-Agent", "Widevine CDM v1.0");
        hashMap.put("Content-Type", "application/json");
        return hashMap;
    }

    public Map<String, String> getMslHeaders() {
        HashMap hashMap = new HashMap();
        hashMap.put("Content-Type", "application/x-www-form-urlencoded");
        hashMap.put("User-Agent", NetflixApiUtils.UserAgent.getFromApiUrl(MSL_API_URL));
        return hashMap;
    }

    public synchronized void runOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).postAtFrontOfQueue(runnable);
    }

    public NetflixManifest getCurrentNetflixManifest() {
        return this.netflixManifest;
    }


    public synchronized byte[] doWidevineDrm(byte[] bArr) throws Exception {
        String replyGetLicense;
        if (Looper.myLooper() != Looper.getMainLooper()) {
            try {
                replyGetLicense = Bridge.replyGetLicense((String) post(MSL_API_URL, Bridge.getLicense(Base64Wrapper.encode(bArr)).getBytes(CHARSET_UTF8), getMslHeaders()).get());
                if (!replyGetLicense.contains("errorDisplayMessage")) {
                    Log.d(TAG, "doWidevineDrm: replyGetLicense was successful");
                } else {
                    Log.e(TAG, "doWidevineDrm: replyGetLicense failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            throw new IllegalThreadStateException("doWidevineDrm cannot be called on Main Thread!");
        }
        return Base64Wrapper.decode(replyGetLicense);
    }
}
