package com.jwplayer.opensourcedemo;

import android.media.MediaDrm;
import android.media.UnsupportedSchemeException;
import android.os.Build;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Locale;

import static com.google.android.exoplayer2.C.WIDEVINE_UUID;

public class IdentityProvider {

    private static final String TAG = "IdentityProvider";

    private static final String APP_ID = Crypto.generateRandomString(14);

    private static final String DEVICE_TYPE_PREFIX = "PRV-";
    private static final String IDENTITY_PREFIX = "NFANDROID1-";
    private static final String IDENTITY_DELIM = "-";
    public static final String KEY_CURRENT_CACHED_ESN = "current_netflix_user_esn_2";

    private final String drmUniqueDeviceId;
    private final String drmSystemId;
    private final String cdmModelId;
    private WidevineLevel widevineLevel;
    private DeviceType deviceType;
    private String identityPrefix;
    private String identity;
    private String currentIdentity;

    public static IdentityProvider createEsnProvider() throws UnsupportedSchemeException {
        DeviceType determine = DeviceType.determine();
        WidevineLevel widevineLevel = WidevineLevel.determine();
        if (widevineLevel.equals(WidevineLevel.LEGACY)) {
            Log.d(TAG, "WidevineLevel.LEGACY found return NULL");
            return null;
        }

        IdentityProvider identityProvider = new IdentityProvider(WidevineLevel.WIDEVINE_L3, determine);
        identityProvider.initialize();
        return identityProvider;
    }

    public void initialize() {
        String findDeviceId = findDeviceId();
        String validateChars = validateChars(findModelId());
        this.identityPrefix = generateEsnPrefix();
        try {
            findDeviceId = Crypto.hashSHA256(findDeviceId, APP_ID);
        } catch (NoSuchAlgorithmException unused) {
            unused.printStackTrace();
        }
        this.identity = IDENTITY_PREFIX.concat(validateChars).concat(IDENTITY_DELIM).concat(validateChars(findDeviceId));
        String identity2 = getIdentity();
        String str = KEY_CURRENT_CACHED_ESN;
        this.currentIdentity = PreferencesUtils.getString(str, identity2);
        PreferencesUtils.save(str, this.currentIdentity);
    }

    private IdentityProvider(WidevineLevel widevineLevel2, DeviceType deviceType2) throws UnsupportedSchemeException {
        this.widevineLevel = widevineLevel2;
        this.deviceType = deviceType2;
        DeviceDescriptor deviceDescriptor = new DeviceDescriptor(widevineLevel2);
        this.drmUniqueDeviceId = new String(deviceDescriptor.getDeviceId());
        this.drmSystemId = deviceDescriptor.getDeviceType();
        this.cdmModelId = findCdmModelId();
    }

    private String findCdmModelId() {
        StringBuilder sb = new StringBuilder(DEVICE_TYPE_PREFIX);
        sb.append(getDeviceType().getIdentityValue());
        sb.append(getWidevineLevel().getIdentityValue());
        String str = Build.MODEL;
        if (str.length() > 45) {
            str = str.substring(0, 45);
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(getManufacturer());
        sb2.append(replaceWhiteSpace(str, "_"));
        sb.append(validateChars(sb2.toString()));
        sb.append(IDENTITY_DELIM);
        sb.append(this.drmSystemId);
        return sb.toString();
    }

    private DeviceType getDeviceType() { return this.deviceType; }

    private WidevineLevel getWidevineLevel() {
        return WidevineLevel.WIDEVINE_L3;
    }

    private String findDeviceId() { return this.drmUniqueDeviceId; }

    private String findModelId() { return this.cdmModelId; }

    private String getIdentity() { return this.identity; }


    public String getCurrentIdentity() {
        return this.currentIdentity;
    }

    private static class DeviceDescriptor {
        private byte[] deviceId;
        private String deviceType;

        private DeviceDescriptor(WidevineLevel widevineLevel) throws UnsupportedSchemeException {
            MediaDrm mediaDrm = new MediaDrm(WIDEVINE_UUID);
            MediaDrmUtils.setAppId(mediaDrm);
            if (widevineLevel == WidevineLevel.WIDEVINE_L3) {
                MediaDrmUtils.setSecurityLevelL3(mediaDrm);
            }
            this.deviceId = MediaDrmUtils.getDeviceId(mediaDrm);
            this.deviceType = MediaDrmUtils.getDeviceType(mediaDrm);
            mediaDrm.release();
        }

        public byte[] getDeviceId() {
            return this.deviceId;
        }

        public String getDeviceType() {
            return this.deviceType;
        }
    }

    private static String getManufacturer() {
        String str = Build.MANUFACTURER;
        if (str.length() < 5) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("       ");
            str = sb.toString();
        }
        return replaceWhiteSpace(str.substring(0, 5), "_", false);
    }

    public static String replaceWhiteSpace(String str, String str2, boolean z) {
        if (str == null) {
            return "";
        }
        String str3 = "\\s";
        if (z) {
            return str.trim().replaceAll(str3, str2);
        }
        return str.replaceAll(str3, str2);
    }

    protected static String validateChars(String str) {
        if (str == null || str.trim().isEmpty()) {
            return "";
        }
        String upperCase = str.toUpperCase(Locale.US);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < upperCase.length(); i++) {
            char charAt = upperCase.charAt(i);
            if ((charAt < 'A' || charAt > 'Z') && !((charAt >= '0' && charAt <= '9') || charAt == '-' || charAt == '=')) {
                sb.append('=');
            } else {
                sb.append(charAt);
            }
        }
        return sb.toString();
    }

    private String generateEsnPrefix() {
        String concat = IDENTITY_PREFIX.concat(DEVICE_TYPE_PREFIX).concat(getDeviceType().getIdentityValue()).concat(getWidevineLevel().getIdentityValue());
        String str = IDENTITY_DELIM;
        if (!concat.endsWith(str)) {
            return concat;
        }
        int lastIndexOf = concat.lastIndexOf(str) + 1;
        return lastIndexOf > 0 ? concat.substring(0, lastIndexOf) : concat;
    }

    private static final class Crypto {
        private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        private static final String HEX = "0123456789ABCDEF";
        private static final String SHA_256 = "SHA-256";
        private static final SecureRandom secureRandom = new SecureRandom();

        private Crypto() {
        }

        /* access modifiers changed from: private */
        public static String generateRandomString(int i) {
            StringBuilder sb;
            synchronized (secureRandom) {
                sb = new StringBuilder(i);
                for (int i2 = 0; i2 < i; i2++) {
                    sb.append(CHARS.charAt(secureRandom.nextInt(62)));
                }
            }
            return sb.toString();
        }

        private static String toHex(byte[] bArr) {
            if (bArr == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder(bArr.length * 2);
            for (byte appendHex : bArr) {
                appendHex(sb, appendHex);
            }
            return sb.toString();
        }

        private static void appendHex(StringBuilder sb, byte b) {
            int i = (b >> 4) & 15;
            String str = HEX;
            sb.append(str.charAt(i));
            sb.append(str.charAt(b & 15));
        }

        /* access modifiers changed from: private */
        public static String hashSHA256(String str, String str2) throws NoSuchAlgorithmException {
            MessageDigest instance = MessageDigest.getInstance(SHA_256);
            instance.update(m2or(str, str2));
            return toHex(instance.digest());
        }

        /* renamed from: or */
        private static byte[] m2or(String str, String str2) {
            byte[] bArr;
            byte[] bArr2;
            if (str.length() >= str2.length()) {
                bArr2 = str.getBytes();
                bArr = str2.getBytes();
            } else {
                byte[] bytes = str2.getBytes();
                bArr = str.getBytes();
                bArr2 = bytes;
            }
            int min = Math.min(bArr2.length, bArr.length);
            for (int i = 0; i < min; i++) {
                bArr2[i] = (byte) (bArr2[i] | bArr[i]);
            }
            return bArr2;
        }
    }

    public static String replaceWhiteSpace(String str, String str2) {
        return replaceWhiteSpace(str, str2, true);
    }

    protected static String findBaseModelId() {
        String str = Build.MODEL;
        if (str.length() > 45) {
            str = str.substring(0, 45);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getManufacturer());
        sb.append(replaceWhiteSpace(str, "_"));
        sb.append("S");
        return sb.toString();
    }

    public String getESNPrefix() {
        return this.identityPrefix;
    }
}
