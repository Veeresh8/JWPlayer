package com.jwplayer.opensourcedemo;

import android.media.MediaDrm;
import android.media.MediaDrm.CryptoSession;
import android.media.MediaDrm.ProvisionRequest;
import android.media.NotProvisionedException;
import android.media.ResourceBusyException;
import android.media.UnsupportedSchemeException;
import android.util.Log;

import org.json.JSONObject;

import static com.google.android.exoplayer2.C.WIDEVINE_UUID;

public class State {
    private static final String LOG_TAG = State.class.getSimpleName();
    private String baseQueryParams;
    private CryptoSession cryptoSession;
    private String drmContextId;
    private byte[] encryptionKeyId;
    private byte[] hmacKeyId;
    private String identity;
    private String keyId;
    private String language;
    private JSONObject masterToken;
    private MediaDrm mediaDrm;
    private String playbackContextId;
    private int sequenceNumber;
    private byte[] sessionId;
    private JSONObject userIdToken;

    public void openSession() {
        String str = "HmacSHA256";
        String str2 = "AES/CBC/NoPadding";
        try {
            MediaDrm mediaDrm2 = new MediaDrm(WIDEVINE_UUID);
            this.mediaDrm = mediaDrm2;
            MediaDrmUtils.setSecurityLevelL3(mediaDrm2);
            MediaDrmUtils.setAppId(this.mediaDrm);
            try {
                byte[] openSession = this.mediaDrm.openSession();
                this.sessionId = openSession;
                this.cryptoSession = this.mediaDrm.getCryptoSession(openSession, str2, str);
            } catch (NotProvisionedException | ResourceBusyException e) {
                if (e instanceof NotProvisionedException) {
                    try {
                        ProvisionRequest provisionRequest = this.mediaDrm.getProvisionRequest();
                        this.mediaDrm.provideProvisionResponse(MslNativeSession.getInstance().doWidevineProvisioning(provisionRequest.getDefaultUrl(), provisionRequest.getData()));
                        byte[] openSession2 = this.mediaDrm.openSession();
                        this.sessionId = openSession2;
                        this.cryptoSession = this.mediaDrm.getCryptoSession(openSession2, str2, str);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } else {
                    Log.d(LOG_TAG, "openSession failed");
                }
            }
        } catch (UnsupportedSchemeException e3) {
            e3.printStackTrace();
        }
    }

    public byte[] getHmacKeyId() {
        return this.hmacKeyId;
    }

    public void setHmacKeyId(byte[] bArr) {
        this.hmacKeyId = bArr;
    }

    public byte[] getEncryptionKeyId() {
        return this.encryptionKeyId;
    }

    public void setEncryptionKeyId(byte[] bArr) {
        this.encryptionKeyId = bArr;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public void setKeyId(String str) {
        this.keyId = str;
    }

    public String getBaseQueryParams() {
        return this.baseQueryParams;
    }

    public void setBaseQueryParams(String str) {
        this.baseQueryParams = str;
    }

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(int i) {
        this.sequenceNumber = i;
    }

    public String getIdentity() {
        return this.identity;
    }

    public void setIdentity(String str) {
        this.identity = str;
    }

    public void setLanguage(String str) {
        this.language = str;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getDrmContextId() {
        return this.drmContextId;
    }

    public void setDrmContextId(String str) {
        this.drmContextId = str;
    }

    public String getPlaybackContextId() {
        return this.playbackContextId;
    }

    public void setPlaybackContextId(String str) {
        this.playbackContextId = str;
    }

    public JSONObject getMasterToken() {
        return this.masterToken;
    }

    public void setMasterToken(JSONObject jSONObject) {
        this.masterToken = jSONObject;
    }

    public JSONObject getUserIdToken() {
        return this.userIdToken;
    }

    public void setUserIdToken(JSONObject jSONObject) {
        this.userIdToken = jSONObject;
    }

    public MediaDrm getMediaDrm() {
        return this.mediaDrm;
    }

    public byte[] getSessionId() {
        return this.sessionId;
    }

    public CryptoSession getCryptoSession() {
        return this.cryptoSession;
    }
}
