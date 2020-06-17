package com.jwplayer.opensourcedemo;

import android.media.MediaDrm;
import android.media.UnsupportedSchemeException;
import android.util.Log;

import static com.google.android.exoplayer2.C.WIDEVINE_UUID;

public enum WidevineLevel {

    LEGACY("L0-", 0),
    WIDEVINE_L1("L1-", 1),
    WIDEVINE_L3("L3-", 3);

    static {
        final String TAG = "WidevineLevel";
    }

    private static final String LOG_TAG = null;
    public final String identityValue;
    public final int nccpValue;

    private WidevineLevel(String str, int i) {
        this.identityValue = str;
        this.nccpValue = i;
    }

    public static WidevineLevel determine() {
        try {
            int widevineSecurityLevel = MediaDrmUtils.getWidevineSecurityLevel(new MediaDrm(WIDEVINE_UUID));
            Log.d("WidevineLevel", "determine() called with - " + widevineSecurityLevel);
            if (widevineSecurityLevel == 1) {
                return WIDEVINE_L1;
            }
            if (widevineSecurityLevel == 3) {
                return WIDEVINE_L3;
            }
            return LEGACY;
        } catch (UnsupportedSchemeException e) {
            return LEGACY;
        }
    }

    public String getIdentityValue() {
        return this.identityValue;
    }
}
