package com.jwplayer.opensourcedemo;

import android.media.MediaDrm;
import android.media.NotProvisionedException;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class MSLRequestGenerator {

    private static final String TAG = "MSLRequestGenerator";


    public static String makeLicensePayload(State state, String str) throws JSONException, IOException {
        long generateMessageId = generateMessageId();
        state.setSequenceNumber(1);
        String makeLicenseHeader = makeLicenseHeader(state, generateMessageId);
        String makeLicenseBody = makeLicenseBody(state, str, generateMessageId);
        StringBuilder sb = new StringBuilder();
        sb.append(makeLicenseHeader);
        sb.append(makeLicenseBody);
        return sb.toString();
    }

    private static String makeLicenseHeader(State state, long j) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("compressionalgos", new JSONArray().put("LZW"));
        jSONObject.put(NetflixApiUtils.Queries.Keys.LANGUAGES, new JSONArray().put(state.getLanguage()));
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("sender", state.getIdentity());
        jSONObject2.put("messageid", j);
        jSONObject2.put("renewable", false);
        jSONObject2.put("capabilities", jSONObject);
        jSONObject2.put("nonreplayable", false);
        jSONObject2.put("handshake", false);
        jSONObject2.put("useridtoken", state.getUserIdToken());
        return makeEncodedEncryptedHeader(state, jSONObject2.toString()).toString();
    }

    private static String makeLicenseBody(State state, String str, long j) throws JSONException, IOException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("challengeBase64", str);
        jSONObject.put("playbackContextId", state.getPlaybackContextId());
        jSONObject.put("drmContextIds", new JSONArray().put(state.getDrmContextId()));
        jSONObject.put("licenseType", "");
        jSONObject.put("clientTime", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        jSONObject.put("xid", 14604009226319L);
        jSONObject.put("method", "license");
        jSONObject.put(NetflixApiUtils.Queries.Keys.LANGUAGES, new JSONArray().put(state.getLanguage()));
        jSONObject.put("clientVersion", "4.0004.496.011-canary");
        jSONObject.put("uiVersion", "akira");
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("headers", new JSONObject());
        jSONObject2.put(NetflixApiUtils.Queries.Keys.PATH, "/cbp/cadmium-3");
        jSONObject2.put("payload", new JSONObject().put("data", jSONObject.toString()));
        jSONObject2.put("query", "");
        JSONArray jSONArray = new JSONArray();
        jSONArray.put(new JSONObject());
        jSONArray.put(jSONObject2);
        return makeEncodedEncryptedPayload(state, makeBase(state, new String(Base64.encode(LZW.compress(jSONArray.toString().getBytes()), 2)), j)).toString();
    }

    public static String makeKeyExchangePayload(State state) throws JSONException, StackOverflowError, NotProvisionedException {
        Log.d(TAG, "makeKeyExchangePayload() called");

        JSONObject mainObject = new JSONObject();
        mainObject.put("messageid", generateMessageId());
        mainObject.put("renewable", true);
        mainObject.put("handshake", true);
        mainObject.put("nonreplayable", false);
        mainObject.put("timestamp", System.currentTimeMillis() / 1000);

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("compressionalgos", new JSONArray().put("LZW"));
        jsonObject1.put("encoderformats", new JSONArray().put("JSON"));
        jsonObject1.put(NetflixApiUtils.Queries.Keys.LANGUAGES, new JSONArray().put(state.getLanguage()));

        mainObject.put("capabilities", jsonObject1);

        byte[] data = state.getMediaDrm().getKeyRequest(state.getSessionId(), new byte[]{10, 122, 0, 108, 56, 43}, "application/xml", MediaDrm.KEY_TYPE_OFFLINE, new HashMap()).getData();
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("keyrequest", Base64.encodeToString(data, Base64.DEFAULT));

        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("scheme", "WIDEVINE");
        jsonObject3.put("keydata", jSONObject2);
        JSONArray jSONArray = new JSONArray();
        jSONArray.put(jsonObject3);

        mainObject.put("keyrequestdata", jSONArray);

        String encodeToString = Base64.encodeToString(mainObject.toString().getBytes(), Base64.DEFAULT);
        JSONObject jsonObject4 = new JSONObject();
        jsonObject4.put("identity", state.getIdentity());
        JSONObject jSONObject5 = new JSONObject();
        jSONObject5.put("authdata", jsonObject4);
        jSONObject5.put("scheme", "NONE");
        JSONObject jSONObject6 = new JSONObject();
        jSONObject6.put("headerdata", encodeToString);
        jSONObject6.put("signature", "");
        jSONObject6.put("entityauthdata", jSONObject5);

        Log.d(TAG, "makeKeyExchangePayload() ended with - " + jSONObject6.toString());

        return jSONObject6.toString();
    }

    private static long generateMessageId() {
        return (long) (Math.random() * (Math.pow(2.0d, 52.0d) - 0.0d));
    }

    public static String makeManifestPayload(State state, String videoID) throws JSONException, IOException {
        long generateMessageId = generateMessageId();
        state.setSequenceNumber(1);
        String makeManifestHeader = makeManifestHeader(state, generateMessageId);
        String makeManifestBody = makeManifestBody(state, videoID, generateMessageId);
        StringBuilder sb = new StringBuilder();
        sb.append(makeManifestHeader);
        sb.append(makeManifestBody);
        return sb.toString();
    }

    private static String makeManifestBody(State state, String str, long j) throws JSONException, IOException {
        JSONArray jSONArray = new JSONArray();
        jSONArray.put("playready-h264mpl13-dash");
        jSONArray.put("playready-h264mpl31-dash");
        jSONArray.put("playready-h264mpl30-dash");
        jSONArray.put("playready-h264mpl40-dash");
        jSONArray.put("heaac-2-dash");
        jSONArray.put("dfxp-ls-sdh");
        jSONArray.put("simplesdh");
        jSONArray.put("webvtt-lssdh-ios8");
        jSONArray.put("BIF320");
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("profiles", jSONArray);
        String str2 = "method";
        jSONObject.put(str2, "manifest");
        jSONObject.put("uiversion", NetflixApiUtils.AppVersions.MSL_API_FULL_VERSION);
        jSONObject.put("sdk", "4.1");
        String str3 = "android";
        jSONObject.put("platform", str3);
        jSONObject.put("application", str3);
        jSONObject.put("drmType", "widevine");
        jSONObject.put("nettype", "wifi");
        jSONObject.put("flavor", "STANDARD");
        jSONObject.put("useHttpsStreams", false);
        jSONObject.put("supportsWatermark", true);
        jSONObject.put("supportsPreReleasePin", false);
        jSONObject.put("viewableIds", new JSONArray().put(str));
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("params", jSONObject);
        jSONObject2.put(NetflixApiUtils.Queries.Keys.LANGUAGES, new JSONArray().put(state.getLanguage()));
        jSONObject2.put("url", "/manifest");
        jSONObject2.put("version", 2);
        StringBuilder sb = new StringBuilder(state.getBaseQueryParams());
        sb.append("&path=%5B%27manifests%27%5D");
        sb.append("&bladerunnerParams=");
        sb.append(URLEncoder.encode(jSONObject2.toString(), "UTF-8"));
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put("headers", new JSONObject());
        jSONObject3.put(NetflixApiUtils.Queries.Keys.PATH, "/android/5.1/api");
        jSONObject3.put(str2, "GET");
        jSONObject3.put("query", sb.toString());
        JSONArray jSONArray2 = new JSONArray();
        jSONArray2.put(new JSONObject());
        jSONArray2.put(jSONObject3);
        return makeEncodedEncryptedPayload(state, makeBase(state, Base64.encodeToString(LZW.compress(jSONArray2.toString().getBytes()), 2), j)).toString();
    }


    private static String makeBase(State state, String str, long j) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("sequencenumber", state.getSequenceNumber());
        jSONObject.put("messageid", j);
        jSONObject.put("compressionalgo", "LZW");
        jSONObject.put("data", str);
        state.setSequenceNumber(state.getSequenceNumber() + 1);
        return jSONObject.toString();
    }

    private static JSONObject makeEncodedEncryptedPayload(State state, String str) throws JSONException {
        String jSONObject = aesEncryptWrap(state, str, generateIv()).toString();
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("payload", Base64.encodeToString(jSONObject.getBytes(), 2));
        jSONObject2.put("signature", generateEncodedSignature(state, jSONObject.getBytes()));
        return jSONObject2;
    }

    private static String makeManifestHeader(State state, long j) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("email", "veeresh.charantimath8@gmail.com");
        jSONObject.put("password", "flexlikeveer");
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("authdata", jSONObject);
        jSONObject2.put("scheme", "EMAIL_PASSWORD");
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put("compressionalgos", new JSONArray().put("LZW"));
        jSONObject3.put(NetflixApiUtils.Queries.Keys.LANGUAGES, new JSONArray().put(state.getLanguage()));
        JSONObject jSONObject4 = new JSONObject();
        jSONObject4.put("sender", state.getIdentity());
        jSONObject4.put("messageid", j);
        jSONObject4.put("renewable", true);
        jSONObject4.put("capabilities", jSONObject3);
        jSONObject4.put("userauthdata", jSONObject2);
        jSONObject4.put("handshake", false);
        jSONObject4.put("nonreplayable", false);
        jSONObject4.put("recipient", "Netflix");
        jSONObject4.put("timestamp", System.currentTimeMillis());
        return makeEncodedEncryptedHeader(state, jSONObject4.toString()).toString();
    }


    private static JSONObject makeEncodedEncryptedHeader(State state, String str) throws JSONException {
        String jSONObject = aesEncryptWrap(state, str, generateIv()).toString();
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("mastertoken", state.getMasterToken());
        jSONObject2.put("headerdata", Base64.encodeToString(jSONObject.getBytes(), 2));
        jSONObject2.put("signature", generateEncodedSignature(state, jSONObject.getBytes()));
        return jSONObject2;
    }

    private static JSONObject aesEncryptWrap(State state, String str, byte[] bArr) throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append("Encrypted Text: ");
        sb.append(str);

        JSONObject jSONObject = new JSONObject();
        jSONObject.put("keyid", state.getKeyId());
        jSONObject.put("iv", Base64.encodeToString(bArr, 2));
        jSONObject.put("ciphertext", generateEncodedEncryptedMessage(state, str.getBytes(), bArr));
        jSONObject.put("sha256", "AA==");
        return jSONObject;
    }

    private static String generateEncodedSignature(State state, byte[] bArr) {
        return Base64.encodeToString(state.getCryptoSession().sign(state.getHmacKeyId(), bArr), 2);
    }

    private static byte[] generateIv() {
        byte[] bArr = new byte[16];
        new Random().nextBytes(bArr);
        return bArr;
    }

    private static String generateEncodedEncryptedMessage(State state, byte[] bArr, byte[] bArr2) {
        byte[] bArr3;
        int i;
        byte[] copyOfRange;
        int i2;
        int length = bArr.length;
        if (length <= 16384) {
            bArr3 = state.getCryptoSession().encrypt(state.getEncryptionKeyId(), padPerPKCS5Padding(bArr, 16), bArr2);
        } else {
            byte[] bArr4 = new byte[((16 - (length % 16)) + length)];
            int i3 = 0;
            while (true) {
                i = i3 + 16384;
                byte[] encrypt = state.getCryptoSession().encrypt(state.getEncryptionKeyId(), Arrays.copyOfRange(bArr, i3, i), bArr2);
                copyOfRange = Arrays.copyOfRange(encrypt, 16368, 16384);
                System.arraycopy(encrypt, 0, bArr4, i3, 16384);
                i2 = length - i;
                if (i2 <= 16384) {
                    break;
                }
                bArr2 = copyOfRange;
                i3 = i;
            }
            if (i2 > 0) {
                byte[] encrypt2 = state.getCryptoSession().encrypt(state.getEncryptionKeyId(), padPerPKCS5Padding(Arrays.copyOfRange(bArr, i, length), 16), copyOfRange);
                System.arraycopy(encrypt2, 0, bArr4, i, encrypt2.length);
            }
            bArr3 = bArr4;
        }
        return Base64.encodeToString(bArr3, 2);
    }

    public static byte[] padPerPKCS5Padding(byte[] bArr, int i) {
        if (bArr != null) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Array size: ");
            sb.append(bArr.length);
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Block size: ");
            sb2.append(i);

            byte length = (byte) (i - (bArr.length % i));
            String str3 = TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Padding: ");
            sb3.append(length);

            byte[] bArr2 = new byte[(bArr.length + length)];
            for (int i2 = 0; i2 < bArr.length; i2++) {
                bArr2[i2] = bArr[i2];
            }
            for (byte b = 0; b < length; b = (byte) (b + 1)) {
                bArr2[bArr.length + b] = length;
            }
            return bArr2;
        }
        throw new IllegalArgumentException("Input array is null!");
    }
}
