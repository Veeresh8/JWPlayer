package com.jwplayer.opensourcedemo;

import android.os.Build;
import android.os.Build.VERSION;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class NetflixApiUtils {

    public static class ApiBaseUrls {
        public static final String API_BASE_URL = "https://api-global.netflix.com/";
        private static final String APPBOOT_API_BASE_URL = "https://android-appboot.netflix.com/appboot/";
        public static final String BASE_VIDEO_URL = "http://www.netflix.com/video?v=";
        public static final String CONFIG_API_URL;
        public static final String META_DATA_API_URL;
        public static final String MSL_API_URL;

        static {
            String str = API_BASE_URL;
            String str2 = "/api/";
            META_DATA_API_URL = str.concat("android/").concat(AppVersions.META_DATA_API_VERSION).concat(str2);
            MSL_API_URL = str.concat("msl/android/").concat(AppVersions.MSL_API_TRUNC_VERSION).concat(str2);
            CONFIG_API_URL = str.concat("android/samurai/config/");
        }

        public static String getAppbootApiUrl(String str) {
            return APPBOOT_API_BASE_URL.concat(str);
        }
    }

    public static class AppVersions {
        public static final String META_DATA_API_VERSION = "4.16.4";
        public static final String MSL_API_FULL_VERSION = "5.1.2";
        public static final String MSL_API_TRUNC_VERSION = "5.1";
    }

    private static class BuildNumbers {
        public static final int CONFIG_API_BUILD_NUMBER = 200217;
        public static final int META_DATA_API_BUILD_NUMBER = 5294;
        public static final int MSL_API_BUILD_NUMBER = 17106;

        private BuildNumbers() {
        }
    }

    public static class Headers {
        public static final String APP_VERSION = "X-Netflix.appVer";
        public static final String ESN_PREFIX = "X-Netflix.esnPrefix";
        public static final String HOST = "Host";
        public static final String REQUEST_UUID = "X-Netflix.request.uuid";
        public static final String USER_AGENT = "User-Agent";

        private static class Values {
            public static final String VALUE_HOST = "api-global.netflix.com";

            private Values() {
            }
        }

        public static String getHost() {
            return Values.VALUE_HOST;
        }

        public static String getAppVersion(String str) {
            if (str.contains(AppVersions.MSL_API_TRUNC_VERSION)) {
                return AppVersions.MSL_API_FULL_VERSION;
            }
            String str2 = AppVersions.META_DATA_API_VERSION;
            if (str.contains(str2)) {
            }
            return str2;
        }
    }

    public static class Queries {

        public static class Keys {
            public static final String API = "api";
            public static final String APP_TYPE = "appType";
            public static final String APP_VER = "appVer";
            public static final String APP_VERSION = "appVersion";
            public static final String CHIPSET = "chipset";
            public static final String DBG = "dbg";
            public static final String DEVICE_LOCALE = "deviceLocale";
            public static final String DL_ENABLED = "dlEnabled";
            public static final String ESN = "esn";
            public static final String FFBC = "ffbc";
            public static final String IMGPREF = "imgpref";
            public static final String IS_NETFLIX_PRELOADED = "isNetflixPreloaded";
            public static final String LACK_LOCALE = "lackLocale";
            public static final String LANGUAGES = "languages";
            public static final String MATERIALIZE = "materialize";
            public static final String MEM_LEVEL = "memLevel";
            public static final String METHOD = "method";
            public static final String MNF = "mnf";
            public static final String M_ID = "mId";
            public static final String OS_BOARD = "osBoard";
            public static final String OS_DEVICE = "osDevice";
            public static final String OS_DISPLAY = "osDisplay";
            public static final String PATH = "path";
            public static final String PATH_FORMAT = "pathFormat";
            public static final String PATH_SUFFIX = "pathSuffix";
            public static final String PRF_TYPE = "prfType";
            public static final String PROGRESSIVE = "progressive";
            public static final String QLTY = "qlty";
            public static final String RES = "res";
            public static final String RESPONSE_FORMAT = "responseFormat";
            public static final String ROUTING = "routing";
            public static final String STORE = "store";
        }

        public static class Values {
            public static final String FALSE = "false";
            public static final String FFBC_PHONE = "phone";
            public static final String FFBC_TABLET = "tablet";
            public static final String IMGPREF_JPEG = "jpeg";
            public static final String IMGPREF_PNG = "png";
            public static final String IMGPREF_WEBP = "webp";
            public static String LANGUAGES_DEVICE = null;
            public static final String METHOD_CALL = "call";
            public static final String METHOD_GET = "get";
            public static final String PATH_FORMAT_GRAPH = "graph";
            public static final String PATH_FORMAT_HIERARCHICAL = "hierarchical";
            public static final String PRF_TYPE_JFK = "jfk";
            public static final String RESPONSE_FORMAT_JSON = "json";
            public static final String RES_HIGH = "high";
            public static final String ROUTING_REDIRECT = "redirect";
            public static final String ROUTING_REJECT = "reject";
            public static final String TRUE = "true";

            static {
                StringBuilder sb = new StringBuilder();
                sb.append(Locale.getDefault().getLanguage());
                sb.append("-");
                sb.append(Locale.getDefault().getCountry());
                LANGUAGES_DEVICE = sb.toString();
            }

            public static String getFfbc() {
                return FFBC_PHONE;
            }

            public static String getBoardPlatform() {
                try {
                    return (String) Class.forName("android.os.SystemProperties").getMethod(METHOD_GET, new Class[]{String.class}).invoke(null, new Object[]{"ro.board.platform"});
                } catch (Exception e) {
                    return Build.BOARD;
                }
            }
        }

        public static Map<String, String> getBaseQueryMap() {
            HashMap hashMap = new HashMap();
            hashMap.put(Keys.RESPONSE_FORMAT, Values.RESPONSE_FORMAT_JSON);
            hashMap.put(Keys.FFBC, Values.getFfbc());
            hashMap.put(Keys.ROUTING, Values.ROUTING_REJECT);
            hashMap.put(Keys.RES, Values.RES_HIGH);
            hashMap.put(Keys.IMGPREF, Values.IMGPREF_WEBP);
            hashMap.put(Keys.PROGRESSIVE, Values.FALSE);
            hashMap.put(Keys.LANGUAGES, Values.LANGUAGES_DEVICE);
            hashMap.put("method", Values.METHOD_GET);
            String str = Values.TRUE;
            hashMap.put(Keys.MATERIALIZE, str);
            hashMap.put(Keys.PATH_FORMAT, Values.PATH_FORMAT_HIERARCHICAL);
            hashMap.put(Keys.APP_VERSION, AppVersions.META_DATA_API_VERSION);
            hashMap.put(Keys.DL_ENABLED, str);
            return hashMap;
        }

        public static Map<String, String> getConfigParams() {
            HashMap hashMap = new HashMap();
            hashMap.put(Keys.RESPONSE_FORMAT, Values.RESPONSE_FORMAT_JSON);
            hashMap.put(Keys.FFBC, Values.getFfbc());
            hashMap.put(Keys.ROUTING, Values.ROUTING_REDIRECT);
            String str = Values.FALSE;
            hashMap.put(Keys.PROGRESSIVE, str);
            hashMap.put(Keys.PATH_FORMAT, Values.PATH_FORMAT_HIERARCHICAL);
            hashMap.put(Keys.APP_TYPE, "samurai");
            hashMap.put(Keys.DBG, str);
            hashMap.put(Keys.QLTY, "hd");
            hashMap.put(Keys.OS_BOARD, Build.BOARD);
            hashMap.put(Keys.OS_DEVICE, Build.DEVICE);
            hashMap.put(Keys.OS_DISPLAY, Build.DISPLAY);
            hashMap.put(Keys.APP_VER, Integer.toString(BuildNumbers.CONFIG_API_BUILD_NUMBER));
            hashMap.put(Keys.APP_VERSION, AppVersions.META_DATA_API_VERSION);
            hashMap.put(Keys.M_ID, IdentityProvider.validateChars(IdentityProvider.findBaseModelId()));
            hashMap.put(Keys.API, Integer.toString(VERSION.SDK_INT));
            hashMap.put(Keys.MNF, Build.MANUFACTURER.trim());
            hashMap.put(Keys.STORE, "google");
            hashMap.put(Keys.MEM_LEVEL, Values.RES_HIGH);
            hashMap.put(Keys.LACK_LOCALE, str);
            hashMap.put(Keys.DEVICE_LOCALE, Locale.getDefault().getLanguage());
            return hashMap;
        }
    }

    public static class UserAgent {
        private static final String BASE_USER_AGENT = "com.netflix.mediaclient/%d (Linux; U; Android %s; %s; %S; Build/MPI24.65-33.1-2-2-11; Cronet/58.0.3029.83)";

        public static String getFromApiUrl(String str) {
            String str2 = VERSION.RELEASE;
            String str3 = Build.MODEL;
            StringBuilder sb = new StringBuilder();
            sb.append(Locale.getDefault().getLanguage());
            sb.append("-");
            sb.append(Locale.getDefault().getCountry());
            String sb2 = sb.toString();
            int i = str.contains(AppVersions.MSL_API_TRUNC_VERSION) ? BuildNumbers.MSL_API_BUILD_NUMBER : str.contains(AppVersions.META_DATA_API_VERSION) ? BuildNumbers.META_DATA_API_BUILD_NUMBER : BuildNumbers.CONFIG_API_BUILD_NUMBER;
            return String.format(Locale.US, BASE_USER_AGENT, new Object[]{Integer.valueOf(i), str2, sb2, str3});
        }
    }
}
