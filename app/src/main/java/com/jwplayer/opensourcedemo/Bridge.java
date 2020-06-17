package com.jwplayer.opensourcedemo;

import android.media.DeniedByServerException;
import android.media.NotProvisionedException;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;

public class Bridge {
    private static final String LOG = Bridge.class.getSimpleName();
    private static State state;

    public static int init(String str, String str2, String str3, String str4) {
        State state2 = new State();
        state = state2;
        state2.setIdentity(str);
        state.setLanguage(str3);
        StringBuilder sb = new StringBuilder("method=get&responseFormat=json&progressive=false&routing=reject&dlEnabled=true&pathFormat=hierarchical&res=high&imgpref=webp");
        sb.append("&languages=");
        sb.append(str3);
        sb.append("&appversion=");
        sb.append(str2);
        sb.append("&ffbc=");
        sb.append(str4);
        state.setBaseQueryParams(sb.toString());
        return 0;
    }

    public static State getState() {
        return state;
    }

    public static String getLicense(String str) {
        try {
            return MSLRequestGenerator.makeLicensePayload(state, str);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String replyGetLicense(String str) {
        try {
            return MSLResponseExtractor.extractLicenseData(state, str);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String startSession() {
        try {
            return MSLRequestGenerator.makeKeyExchangePayload(state);
        } catch (StackOverflowError | NotProvisionedException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int replyStartSession(String response) {
        try {
            return MSLResponseExtractor.extractKeyExchangeData(state, response);
        } catch (DeniedByServerException | NotProvisionedException | JSONException e) {
            Log.e(LOG, "replyStartSession failed " + e.getMessage());
            return -1;
        }
    }

    public static String getManifest(String videoID) {
        try {
            return MSLRequestGenerator.makeManifestPayload(state, videoID);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String replyGetManifest(String str, String str2) {
        try {
            return MSLResponseExtractor.extractManifestData(state, str, str2);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
