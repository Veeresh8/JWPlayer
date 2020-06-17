package com.jwplayer.opensourcedemo;

import android.annotation.TargetApi;

import com.google.android.exoplayer2.drm.ExoMediaDrm;
import com.google.android.exoplayer2.drm.MediaDrmCallback;

import java.util.UUID;

@TargetApi(18)
public class NflxWidevineDrmCallback implements MediaDrmCallback {

    @Override
    public byte[] executeProvisionRequest(UUID uuid, ExoMediaDrm.ProvisionRequest provisionRequest) throws Exception {
        return MslNativeSession.getInstance().doWidevineProvisioning(provisionRequest.getDefaultUrl(), provisionRequest.getData());
    }

    @Override
    public byte[] executeKeyRequest(UUID uuid, ExoMediaDrm.KeyRequest request) throws Exception {
        return MslNativeSession.getInstance().doWidevineDrm(request.getData());
    }
}
