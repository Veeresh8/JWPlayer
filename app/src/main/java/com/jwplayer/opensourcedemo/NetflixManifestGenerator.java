package com.jwplayer.opensourcedemo;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetflixManifestGenerator {

    private static final String LOG_TAG = NetflixManifestGenerator.class.getSimpleName();
    public static final String NETFLIX_MANIFEST_FILE_NAME = "netflix_dash_manifest.xml";
    private static AtomicBoolean lastManifestSticklyUsedIps = new AtomicBoolean(false);


    private static final class Codecs {
        public static final String AUDIO_MP4 = "mp4a.40.29";
        public static final String VIDEO_H264 = "avc1";
        public static final String VIDEO_VP9 = "vp9";

        private Codecs() {
        }
    }

    private static final class MimeTypes {
        public static final String APPLICATION_TTAF = "application/ttaf+xml";
        public static final String APPLICATION_TTML = "application/ttml+xml";
        public static final String AUDIO_AAC = "audio/mp4a-latm";
        public static final String AUDIO_MP4 = "audio/mp4";
        public static final String TEXT_VTT = "text/vtt";
        public static final String VIDEO_H263 = "video/3gpp";
        public static final String VIDEO_H264 = "video/avc";
        public static final String VIDEO_H265 = "video/hevc";
        public static final String VIDEO_MP4 = "video/mp4";
        public static final String VIDEO_VP8 = "video/x-vnd.on2.vp8";
        public static final String VIDEO_VP9 = "video/x-vnd.on2.vp9";

        private MimeTypes() {
        }
    }


    public static String getDashManifestPath() {
        return App.getInstance().getApplicationContext().getCacheDir().getAbsolutePath().concat("/").concat(NETFLIX_MANIFEST_FILE_NAME);
    }

    public static boolean writeJSONManifest(NetflixManifest netflixManifest) {
        try {
            return saveDashManifest(getJSONManifest(netflixManifest));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean saveDashManifest(String str) {
        deleteDashManifest();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(getDashManifestPath()));
            fileOutputStream.write(str.getBytes());
            fileOutputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void deleteDashManifest() {
        File file = new File(getDashManifestPath());
        if (file.exists()) {
            try {
                if (file.delete()) {
                    Log.d(LOG_TAG, "Successfully deleted DASH manifest file");
                } else {
                    Log.d(LOG_TAG, "Unable to delete manifest file");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getJSONManifest(NetflixManifest netflixManifest) throws Exception {
        lastManifestSticklyUsedIps.set(true);
        long runtime = netflixManifest.getRuntime() / 1000;
        XmlSerializer newSerializer = Xml.newSerializer();
        StringWriter stringWriter = new StringWriter();
        newSerializer.setOutput(stringWriter);
        newSerializer.startDocument("UTF-8", Boolean.valueOf(true));
        String str = "MPD";
        String str2 = "";
        newSerializer.startTag(str2, str);
        newSerializer.attribute(str2, "xmlns", "urn:mpeg:DASH:schema:MPD:2011");
        newSerializer.attribute(str2, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        newSerializer.attribute(str2, "xmlns:ns2", "http://www.w3.org/2001/XMLSchema");
        newSerializer.attribute(str2, "xsi:schemaLocation", "urn:mpeg:DASH:schema:MPD:2011 DASH-MPD.xsd");
        newSerializer.attribute(str2, "profiles", "urn:mpeg:dash:profile:isoff-on-demand:2011");
        newSerializer.attribute(str2, "minBufferTime", "PT1.500S");
        newSerializer.attribute(str2, "type", "static");
        newSerializer.attribute(str2, "mediaPresentationDuration", String.format(Locale.US, "PT%dS", new Object[]{Long.valueOf(runtime)}));
        String str3 = "Period";
        newSerializer.startTag(str2, str3);
        makeAdaptation(newSerializer, netflixManifest.getVideoTracks(), runtime, makeAdaptation(newSerializer, netflixManifest.getTextTracks(), runtime, makeAdaptation(newSerializer, netflixManifest.getAudioTracks(), runtime, 0)));
        newSerializer.endTag(str2, str3);
        newSerializer.endTag(str2, str);
        newSerializer.endDocument();
        return stringWriter.toString();
    }

    private static boolean isLanguageAlreadyAdded(List<String> list, String str) {
        return str != null && list.contains(str);
    }

    private static boolean isOffTrack(String str) {
        Matcher matcher = Pattern.compile("\\d:\\d;\\d;(?:[^;]*);(\\d;\\d)").matcher(str);
        if (matcher.find()) {
            return !matcher.group(1).equals("0;0");
        }
        return false;
    }

    private static int makeAdaptation(XmlSerializer xmlSerializer, NetflixManifest.Track[] trackArr, long j, int i) throws Exception {
        int i2;
        ArrayList arrayList;
        ArrayList arrayList2;
        int i3;
        String str;
        String str2;
        NetflixManifest.Downloadable[] downloadableArr;
        int i4;
        ArrayList arrayList3;
        String str3;
        String str4;
        String str5;
        String str6;
        XmlSerializer xmlSerializer2 = xmlSerializer;
        NetflixManifest.Track[] trackArr2 = trackArr;
        if (trackArr2 == null || trackArr2.length == 0) {
            return i;
        }
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = new ArrayList();
        int length = trackArr2.length;
        int i5 = i;
        int i6 = 0;
        while (i6 < length) {
            NetflixManifest.Track track = trackArr2[i6];
            String type = track.getType();
            if (!(track.getDownloadables() == null || track.getDownloadables().length == 0)) {
                String str7 = "AUDIO";
                if (!type.equals(str7) || !isLanguageAlreadyAdded(arrayList4, track.getLanguage())) {
                    String str8 = "TEXT";
                    if (!type.equals(str8) || (!isLanguageAlreadyAdded(arrayList5, track.getLanguage()) && !isOffTrack(track.getId()))) {
                        String str9 = "AdaptationSet";
                        String str10 = "";
                        xmlSerializer2.startTag(str10, str9);
                        xmlSerializer2.attribute(str10, "id", Integer.toString(i5));
                        xmlSerializer2.attribute(str10, "contentType", type.toLowerCase(Locale.US));
                        xmlSerializer2.attribute(str10, "subsegmentAlignment", NetflixApiUtils.Queries.Values.TRUE);
                        if (track.getLanguage() != null) {
                            xmlSerializer2.attribute(str10, "lang", track.getLanguage());
                            if (type.equals(str7)) {
                                arrayList4.add(track.getLanguage());
                            } else if (type.equals(str8)) {
                                arrayList5.add(track.getLanguage());
                            }
                        }
                        NetflixManifest.Downloadable[] downloadables = track.getDownloadables();
                        String str11 = "mimeType";
                        if (type.equals(str8)) {
                            xmlSerializer2.attribute(str10, str11, MimeTypes.APPLICATION_TTML);
                        }
                        boolean equals = type.equals(str7);
                        String str12 = str9;
                        String str13 = "value";
                        String str14 = str7;
                        String str15 = "schemeIdUri";
                        if (equals) {
                            xmlSerializer2.attribute(str10, str11, MimeTypes.AUDIO_MP4);
                            String str16 = "AudioChannelConfiguration";
                            xmlSerializer2.startTag(str10, str16);
                            xmlSerializer2.attribute(str10, str15, "urn:mpeg:dash:23003:3:audio_channel_configuration:2011");
                            arrayList = arrayList4;
                            xmlSerializer2.attribute(str10, str13, String.valueOf((int) track.getChannels()));
                            xmlSerializer2.endTag(str10, str16);
                        } else {
                            arrayList = arrayList4;
                        }
                        String str17 = "VIDEO";
                        if (type.equals(str17)) {
                            xmlSerializer2.attribute(str10, str11, MimeTypes.VIDEO_MP4);
                            String str18 = "ContentProtection";
                            xmlSerializer2.startTag(str10, str18);
                            xmlSerializer2.attribute(str10, str15, "urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed");
                            xmlSerializer2.endTag(str10, str18);
                        }
                        String str19 = "Role";
                        xmlSerializer2.startTag(str10, str19);
                        xmlSerializer2.attribute(str10, str15, "urn:mpeg:DASH:role:2011");
                        xmlSerializer2.attribute(str10, str13, "main");
                        xmlSerializer2.endTag(str10, str19);
                        int length2 = downloadables.length;
                        int i7 = 0;
                        while (i7 < length2) {
                            NetflixManifest.Downloadable downloadable = downloadables[i7];
                            if (type.equals(str8)) {
                                makeTextRepresentation(xmlSerializer2, (NetflixManifest.TTDownloadable) downloadable, ((NetflixManifest.TimedTextTrack) track).getDownloadableId(downloadable.getContentProfile()), track.getId());
                                downloadableArr = downloadables;
                                str = str8;
                                str2 = str17;
                                i4 = length2;
                                i3 = length;
                                str3 = str14;
                                str6 = str10;
                                str5 = type;
                                arrayList3 = arrayList5;
                                str4 = str12;
                            } else if (!type.equals(str17) || !track.isHighestBitrate(new Comparator<NetflixManifest.Downloadable>() {
                                public int compare(NetflixManifest.Downloadable downloadable, NetflixManifest.Downloadable downloadable2) {
                                    return (downloadable.getHeight() != downloadable2.getHeight() || downloadable.getDownloadableId().equals(downloadable2.getDownloadableId())) ? -1 : 0;
                                }
                            }, downloadable)) {
                                downloadableArr = downloadables;
                                str = str8;
                                str2 = str17;
                                i4 = length2;
                                i3 = length;
                                str3 = str14;
                                str6 = str10;
                                str5 = type;
                                arrayList3 = arrayList5;
                                str4 = str12;
                                if (str5.equals(str3) && track.isHighestBitrate(new Comparator<NetflixManifest.Downloadable>() {
                                    public int compare(NetflixManifest.Downloadable downloadable, NetflixManifest.Downloadable downloadable2) {
                                        return !downloadable.getDownloadableId().equals(downloadable2.getDownloadableId()) ? 0 : -1;
                                    }
                                }, downloadable)) {
                                    makeRepresentation(xmlSerializer, str5, (int) track.getChannels(), downloadable, track.getId(), j);
                                }
                            } else {
                                NetflixManifest.Downloadable[] downloadableArr2 = downloadables;
                                downloadableArr = downloadableArr2;
                                str2 = str17;
                                str6 = str10;
                                str = str8;
                                i4 = length2;
                                i3 = length;
                                str3 = str14;
                                str5 = type;
                                arrayList3 = arrayList5;
                                str4 = str12;
                                makeRepresentation(xmlSerializer, type, (int) track.getChannels(), downloadable, track.getId(), j);
                            }
                            i7++;
                            str10 = str6;
                            type = str5;
                            str12 = str4;
                            arrayList5 = arrayList3;
                            length2 = i4;
                            downloadables = downloadableArr;
                            str17 = str2;
                            str8 = str;
                            str14 = str3;
                            length = i3;
                        }
                        arrayList2 = arrayList5;
                        i2 = length;
                        xmlSerializer2.endTag(str10, str12);
                        i5++;
                        i6++;
                        trackArr2 = trackArr;
                        arrayList5 = arrayList2;
                        arrayList4 = arrayList;
                        length = i2;
                    }
                }
            }
            arrayList = arrayList4;
            arrayList2 = arrayList5;
            i2 = length;
            i6++;
            trackArr2 = trackArr;
            arrayList5 = arrayList2;
            arrayList4 = arrayList;
            length = i2;
        }
        return i5;
    }

    private static void makeRepresentation(XmlSerializer xmlSerializer, String str, int i, NetflixManifest.Downloadable downloadable, String str2, long j) throws IOException {
        String mimeType = getMimeType(downloadable.getContentProfile());
        if (mimeType != null) {
            String str3 = "Representation";
            String str4 = "";
            xmlSerializer.startTag(str4, str3);
            xmlSerializer.attribute(str4, "id", buildRepId(str2, downloadable.getDownloadableId()));
            xmlSerializer.attribute(str4, "bandwidth", String.valueOf(downloadable.getBitrate() * 1000));
            xmlSerializer.attribute(str4, "mimeType", mimeType);
            xmlSerializer.attribute(str4, "codecs", getCodec(downloadable.getContentProfile()));
            String str5 = "1";
            xmlSerializer.attribute(str4, "startWithSAP", str5);
            if (str.equals("VIDEO")) {
                xmlSerializer.attribute(str4, "maxPlayoutRate", str5);
                xmlSerializer.attribute(str4, "width", String.valueOf(downloadable.getWidth()));
                xmlSerializer.attribute(str4, "height", String.valueOf(downloadable.getHeight()));
            }
            if (str.equals("AUDIO")) {
                xmlSerializer.attribute(str4, "audioSamplingRate", String.valueOf((downloadable.getBitrate() / ((long) i)) * 1000));
            }
            String str6 = "BaseURL";
            xmlSerializer.startTag(str4, str6);
            if (downloadable.getSize() != 0) {
                xmlSerializer.attribute(str4, "ns2:contentLength", String.valueOf(downloadable.getSize()));
            }
            xmlSerializer.text(chooseURL(downloadable.getUrls(), downloadable.getContentProfile()));
            xmlSerializer.endTag(str4, str6);
            long j2 = ((j / 2) * 12) + 20000;
            if (downloadable.getSize() != 0) {
                String str7 = "SegmentBase";
                xmlSerializer.startTag(str4, str7);
                StringBuilder sb = new StringBuilder();
                String str8 = "0-";
                sb.append(str8);
                sb.append(j2);
                xmlSerializer.attribute(str4, "indexRange", sb.toString());
                xmlSerializer.attribute(str4, "indexRangeExact", NetflixApiUtils.Queries.Values.TRUE);
                String str9 = "Initialization";
                xmlSerializer.startTag(str4, str9);
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str8);
                sb2.append(j2);
                xmlSerializer.attribute(str4, "range", sb2.toString());
                xmlSerializer.endTag(str4, str9);
                xmlSerializer.endTag(str4, str7);
            }
            xmlSerializer.endTag(str4, str3);
        }
    }

    private static String getCodec(String str) {
        char c;
        switch (str.hashCode()) {
            case -1885963590:
                if (str.equals("playready-h264mpl13-dash")) {
                    c = 1;
                    break;
                }
            case -756557066:
                if (str.equals("vp9-profile0-L30-dash-cenc")) {
                    c = 6;
                    break;
                }
            case -196843681:
                if (str.equals("playready-h264mpl30-dash")) {
                    c = 2;
                    break;
                }
            case -168214530:
                if (str.equals("playready-h264mpl31-dash")) {
                    c = 3;
                    break;
                }
            case 690660000:
                if (str.equals("playready-h264mpl40-dash")) {
                    c = 4;
                    break;
                }
            case 1612376152:
                if (str.equals("vp9-profile0-L21-dash-cenc")) {
                    c = 5;
                    break;
                }
            case 1741458871:
                if (str.equals("vp9-profile0-L31-dash-cenc")) {
                    c = 7;
                    break;
                }
            case 1823458772:
                if (str.equals("heaac-2-dash")) {
                    c = 0;
                    break;
                }
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return Codecs.AUDIO_MP4;
            case 1:
            case 2:
            case 3:
            case 4:
                return Codecs.VIDEO_H264;
            case 5:
            case 6:
            case 7:
                return Codecs.VIDEO_VP9;
            default:
                Log.e(LOG_TAG, "codec not found for profile: ");
                return null;
        }
    }

    private static String getMimeType(String str) {
        char c;
        switch (str.hashCode()) {
            case -2124360379:
                if (str.equals("simplesdh")) {
                    c = 9;
                    break;
                }
            case -1885963590:
                if (str.equals("playready-h264mpl13-dash")) {
                    c = 1;
                    break;
                }
            case -756557066:
                if (str.equals("vp9-profile0-L30-dash-cenc")) {
                    c = 6;
                    break;
                }
            case -196843681:
                if (str.equals("playready-h264mpl30-dash")) {
                    c = 2;
                    break;
                }
            case -168214530:
                if (str.equals("playready-h264mpl31-dash")) {
                    c = 3;
                    break;
                }
            case 690660000:
                if (str.equals("playready-h264mpl40-dash")) {
                    c = 4;
                    break;
                }
            case 1612376152:
                if (str.equals("vp9-profile0-L21-dash-cenc")) {
                    c = 5;
                    break;
                }
            case 1741458871:
                if (str.equals("vp9-profile0-L31-dash-cenc")) {
                    c = 7;
                    break;
                }
            case 1823458772:
                if (str.equals("heaac-2-dash")) {
                    c = 0;
                    break;
                }
            case 1841370419:
                if (str.equals("webvtt-lssdh-ios8")) {
                    c = 10;
                    break;
                }
            case 2086608164:
                if (str.equals("dfxp-ls-sdh")) {
                    c = 8;
                    break;
                }
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return MimeTypes.AUDIO_AAC;
            case 1:
            case 2:
            case 3:
            case 4:
                return MimeTypes.VIDEO_MP4;
            case 5:
            case 6:
            case 7:
                return MimeTypes.VIDEO_VP9;
            case 8:
            case 9:
                return MimeTypes.APPLICATION_TTML;
            case 10:
                return MimeTypes.TEXT_VTT;
            default:
                Log.e(LOG_TAG, "mime type not found for profile: ");
                return null;
        }
    }

    private static void makeTextRepresentation(XmlSerializer xmlSerializer, NetflixManifest.TTDownloadable tTDownloadable, String str, String str2) throws IOException {
        String mimeType = getMimeType(tTDownloadable.getContentProfile());
        if (mimeType != null) {
            String str3 = "Representation";
            String str4 = "";
            xmlSerializer.startTag(str4, str3);
            xmlSerializer.attribute(str4, "id", buildRepId(str2, str));
            xmlSerializer.attribute(str4, "bandwidth", "0");
            xmlSerializer.attribute(str4, "mimeType", mimeType);
            String str5 = "BaseURL";
            xmlSerializer.startTag(str4, str5);
            if (tTDownloadable.getSize() != 0) {
                xmlSerializer.attribute(str4, "ns2:contentLength", String.valueOf(tTDownloadable.getSize()));
            }
            xmlSerializer.text(chooseURL(tTDownloadable.getUrls(), tTDownloadable.getContentProfile()));
            xmlSerializer.endTag(str4, str5);
            xmlSerializer.endTag(str4, str3);
        }
    }

    private static String chooseURL(NetflixManifest.Url[] urlArr, String str) {
        Pattern compile = Pattern.compile("http.*[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}\\/");
        for (NetflixManifest.Url url : urlArr) {
            if (compile.matcher(url.getUrl()).find()) {
                return url.getUrl();
            }
        }
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Failed to find preferred (ip) stream for content profile: ");
        sb.append(str);

        lastManifestSticklyUsedIps.set(false);
        return urlArr[new Random().nextInt(urlArr.length)].getUrl();
    }

    private static String buildRepId(String str, String str2) {
        return String.format("%s?%s", new Object[]{str2, str});
    }
}
