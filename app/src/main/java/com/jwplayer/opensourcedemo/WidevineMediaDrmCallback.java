package com.jwplayer.opensourcedemo;

import com.google.android.exoplayer2.drm.ExoMediaDrm;
import com.longtailvideo.jwplayer.media.drm.MediaDrmCallback;

import java.util.UUID;

public class WidevineMediaDrmCallback implements MediaDrmCallback {

    @Override
    public byte[] executeProvisionRequest(UUID uuid, ExoMediaDrm.ProvisionRequest provisionRequest) throws Exception {
        return MslNativeSession.getInstance().doWidevineProvisioning(provisionRequest.getDefaultUrl(), provisionRequest.getData());
    }

    @Override
    public byte[] executeKeyRequest(UUID uuid, ExoMediaDrm.KeyRequest request) throws Exception {
        return MslNativeSession.getInstance().doWidevineDrm(request.getData());
    }
}
