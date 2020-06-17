package com.jwplayer.opensourcedemo;

import android.media.MediaDrm;
import android.util.Log;

public class MediaDrmUtils {

    private static final String TAG = "MediaDrmUtils";

    public static int getWidevineSecurityLevel(MediaDrm mediaDrm) {
        Log.d(TAG, "getWidevineSecurityLevel() called with: mediaDrm = [" + mediaDrm + "]");
        String propertyString = mediaDrm.getPropertyString("securityLevel");
        if (propertyString.equals("L1")) {
            return 1;
        }
        return propertyString.equals("L3") ? 3 : -1;
    }

    public static void setAppId(MediaDrm mediaDrm) {
        Log.d(TAG, "setAppId() called with: mediaDrm = [" + mediaDrm + "]");
        try {
            mediaDrm.setPropertyString("appId", "com.netflix.mediaclient");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSecurityLevelL3(MediaDrm mediaDrm) {
        Log.d(TAG, "setSecurityLevelL3() called with: mediaDrm = [" + mediaDrm + "]");
        try {
            mediaDrm.setPropertyString("securityLevel", "L3");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] getDeviceId(MediaDrm mediaDrm) {
        return mediaDrm.getPropertyByteArray("deviceUniqueId");
    }

    public static String getDeviceType(MediaDrm mediaDrm) {
        return mediaDrm.getPropertyString("systemId");
    }
}
