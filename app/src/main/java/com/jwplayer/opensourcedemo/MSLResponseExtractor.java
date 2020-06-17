package com.jwplayer.opensourcedemo;

import android.media.DeniedByServerException;
import android.media.NotProvisionedException;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MSLResponseExtractor {

    private static final String LOG_TAG = MSLResponseExtractor.class.getSimpleName();

    public static int extractKeyExchangeData(State state, String str) throws JSONException, NotProvisionedException, DeniedByServerException {
        StringBuilder sb = new StringBuilder();
        sb.append("extractKeyExchangeData - reply: ");
        sb.append(str);
        JSONObject jSONObject = new JSONObject(new String(Base64.decode(new JSONObject(str).getString("headerdata"), 0)));
        String str3 = "keyresponsedata";
        String str4 = "keydata";
        byte[] decode = Base64.decode(jSONObject.getJSONObject(str3).getJSONObject(str4).getString("encryptionkeyid"), 0);
        byte[] decode2 = Base64.decode(jSONObject.getJSONObject(str3).getJSONObject(str4).getString("hmackeyid"), 0);
        byte[] decode3 = Base64.decode(jSONObject.getJSONObject(str3).getJSONObject(str4).getString("cdmkeyresponse").getBytes(), 0);
        JSONObject jSONObject2 = jSONObject.getJSONObject(str3).getJSONObject("mastertoken");
        state.setEncryptionKeyId(decode);
        state.setHmacKeyId(decode2);
        state.setMasterToken(jSONObject2);
        state.setSequenceNumber(new JSONObject(new String(Base64.decode(jSONObject2.getString("tokendata").getBytes(), 2))).getInt("sequencenumber"));
        StringBuilder sb2 = new StringBuilder();
        sb2.append(state.getIdentity());
        sb2.append("_");
        sb2.append(state.getSequenceNumber());
        state.setKeyId(sb2.toString());
        state.getMediaDrm().provideKeyResponse(state.getSessionId(), decode3);
        return 0;
    }

    public static String extractManifestData(State state, String str, String str2) throws JSONException, IOException {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        int findClosingParen = findClosingParen(str.toCharArray(), 0);
        String substring = str.substring(0, findClosingParen + 1);
        while (findClosingParen < str.length() - 1) {
            int i2 = findClosingParen + 1;
            int findClosingParen2 = findClosingParen(str.toCharArray(), i2);
            arrayList.add(str.substring(i2, findClosingParen2 + 1));
            findClosingParen = findClosingParen2;
        }
        state.setUserIdToken(readResponse(state, substring, "headerdata").getJSONObject("useridtoken"));
        StringBuilder sb = new StringBuilder();
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            sb.append(extractManifestPayloadData(state, (String) arrayList.get(i3)));
        }
        JSONArray jSONArray = new JSONArray(sb.toString());
        JSONObject jSONObject = new JSONObject();
        while (true) {
            if (i >= jSONArray.length()) {
                break;
            }
            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
            String str3 = "payload";
            if (jSONObject2.has(str3)) {
                jSONObject = jSONObject2.getJSONObject(str3);
                break;
            }
            i++;
        }
        JSONObject jSONObject3 = jSONObject.getJSONObject(NetflixApiUtils.Queries.Values.RESPONSE_FORMAT_JSON).getJSONObject("value").getJSONObject("manifests").getJSONObject("result").getJSONObject(str2);
        String str4 = "error";
        if (jSONObject3.has(str4)) {
            return jSONObject3.getJSONObject(str4).toString();
        }
        state.setDrmContextId(jSONObject3.getString("drmContextId"));
        state.setPlaybackContextId(jSONObject3.getString("playbackContextId"));
        return jSONObject3.toString();
    }

    private static String extractManifestPayloadData(State state, String str) throws JSONException, IOException {
        byte[] decode = Base64.decode(readResponse(state, str, "payload").getString("data"), 0);
        if (!isJSONValid(new String(decode))) {
            return new String(LZW.uncompress(decode, 200));
        }
        return new String(decode);
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static int findClosingParen(char[] cArr, int i) {
        int i2 = 1;
        while (i2 > 0) {
            i++;
            char c = cArr[i];
            if (c == '{') {
                i2++;
            } else if (c == '}') {
                i2--;
            }
        }
        return i;
    }

    private static JSONObject readResponse(State state, String str, String str2) throws JSONException {
        return aesDecrpytUnwrap(state, new String(Base64.decode(new JSONObject(str).getString(str2), 2)));
    }


    private static JSONObject aesDecrpytUnwrap(State state, String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        byte[] decode = Base64.decode(jSONObject.getString("iv"), 0);
        byte[] decode2 = Base64.decode(jSONObject.getString("ciphertext"), 0);
        jSONObject.getString("keyid");
        return new JSONObject(new String(unpadPerPKCS5Padding(state.getCryptoSession().decrypt(state.getEncryptionKeyId(), decode2, decode), 16)));
    }

    public static byte[] unpadPerPKCS5Padding(byte[] bArr, int i) {
        if (bArr == null || bArr.length < 1) {
            throw new IllegalArgumentException("Input array is null or 0!");
        }
        int length = bArr.length - bArr[bArr.length - 1];
        byte[] bArr2 = new byte[length];
        for (int i2 = 0; i2 < length; i2++) {
            bArr2[i2] = bArr[i2];
        }
        return bArr2;
    }

    public static String extractLicenseData(State state, String str) throws JSONException, IOException {
        String str2;
        String str3 = "payload";
        String str4 = "data";
        JSONArray jSONArray = new JSONArray(new String(LZW.uncompress(Base64.decode(readResponse(state, str.substring(findClosingParen(str.toCharArray(), 0) + 1, str.length()), str3).getString(str4), 0), 200)));
        int i = 0;
        while (true) {
            if (i >= jSONArray.length()) {
                str2 = "";
                break;
            }
            JSONObject jSONObject = jSONArray.getJSONObject(i);
            if (jSONObject.has(str3)) {
                str2 = jSONObject.getJSONObject(str3).getString(str4);
                break;
            }
            i++;
        }
        JSONObject jSONObject2 = new JSONObject(new String(Base64.decode(str2.getBytes(), 0)));
        String str5 = "result";
        if (jSONObject2.getBoolean("success")) {
            return jSONObject2.getJSONObject(str5).getString("licenseResponseBase64");
        }
        return jSONObject2.getJSONObject(str5).toString();
    }

}
