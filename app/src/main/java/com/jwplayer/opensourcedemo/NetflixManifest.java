package com.jwplayer.opensourcedemo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetflixManifest {
    public static final Pattern REGEX_AUDIO_TRACK_ID = Pattern.compile("((A:.*?)\\||(A:.*))");
    public static final Pattern REGEX_TEXT_TRACK_ID = Pattern.compile("((T:.*?)\\||(T:.*))");
    @SerializedName("audio_tracks")
    private Track[] audioTracks;
    @SerializedName("defaultTrackOrderList")
    private ArrayList<DefaultTrackOrderList> defaultTrackOrderList;
    private NetflixError error;
    private Links links;
    private long movieId;
    @SerializedName("duration")
    private long runtime;
    @SerializedName("timedtexttracks")
    private TimedTextTrack[] textTracks;
    @SerializedName("video_tracks")
    private Track[] videoTracks;

    public class DefaultTrackOrderList {
        private String mediaId;

        public DefaultTrackOrderList() {
        }

        public String getMediaId() {
            return this.mediaId;
        }
    }

    public NetflixError getError() {
        return this.error;
    }

    public static class Downloadable {
        private long bitrate;
        @SerializedName("content_profile")
        protected String contentProfile;
        @SerializedName("downloadable_id")
        private long downloadableId;
        private List<String> hdcpVersions = null;
        @SerializedName("res_h")
        private long height;
        private boolean isEncrypted;
        private long size;
        private long startByteOffest;
        private Url[] urls;
        private long validFor;
        private long vmaf;
        @SerializedName("res_w")
        private long width;

        public static class ContentProfiles {
            public static final String TRACK_TYPE_AUDIO = "AUDIO";
            public static final String TRACK_TYPE_TEXT = "TEXT";
            public static final String TRACK_TYPE_VIDEO = "VIDEO";
        }

        public long getSize() {
            return this.size;
        }

        public String getContentProfile() {
            return this.contentProfile;
        }

        public long getVmaf() {
            return this.vmaf;
        }

        public long getHeight() {
            return this.height;
        }

        public long getBitrate() {
            return this.bitrate;
        }

        public long getStartByteOffest() {
            return this.startByteOffest;
        }

        public long getWidth() {
            return this.width;
        }

        public List<String> getHdcpVersions() {
            return this.hdcpVersions;
        }

        public boolean isEncrypted() {
            return this.isEncrypted;
        }

        public String getDownloadableId() {
            return Long.toString(this.downloadableId);
        }

        public long getValidFor() {
            return this.validFor;
        }

        public Url[] getUrls() {
            return this.urls;
        }
    }

    public static class Link {
        private String href;

        public String getHref() {
            return this.href;
        }
    }

    public static class Links {
        private Link events;
        private Link license;

        public Link getEvents() {
            return this.events;
        }

        public Link getLicense() {
            return this.license;
        }
    }

    public static class TTDownloadable extends Downloadable {
        private Map<Integer, String> downloadUrls;

        public Url[] getUrls() {
            Url[] urlArr = new Url[this.downloadUrls.size()];
            int i = 0;
            for (String str : this.downloadUrls.values()) {
                Url url = new Url();
                url.url = str;
                urlArr[i] = url;
                i++;
            }
            return urlArr;
        }
    }

    public static class TimedTextTrack extends Track {
        private Map<String, Long> downloadableIds;
        private Map<String, TTDownloadable> ttDownloadables;

        public Downloadable[] getDownloadables() {
            TTDownloadable[] tTDownloadableArr = new TTDownloadable[this.ttDownloadables.size()];
            int i = 0;
            for (String str : this.ttDownloadables.keySet()) {
                TTDownloadable tTDownloadable = (TTDownloadable) this.ttDownloadables.get(str);
                tTDownloadable.contentProfile = str;
                tTDownloadableArr[i] = tTDownloadable;
                i++;
            }
            return tTDownloadableArr;
        }

        public String getDownloadableId(String str) {
            Map<String, Long> map = this.downloadableIds;
            if (map == null || map.isEmpty() || !this.downloadableIds.containsKey(str)) {
                return null;
            }
            return Long.toString(((Long) this.downloadableIds.get(str)).longValue());
        }
    }

    public static class Track {
        private double channels;
        @SerializedName("streams")
        private Downloadable[] downloadables;
        @SerializedName("new_track_id")

        /* renamed from: id */
        private String id;
        private boolean isForced;
        private String language;
        private int maxCroppedHeight;
        private int maxCroppedWidth;
        private int maxHeight;
        private int maxWidth;
        private String trackType;
        private Object type;

        public static class TrackTypes {
            public static final String TRACK_TYPE_ASSISTIVE = "ASSISTIVE";
            public static final String TRACK_TYPE_AUDIO = "AUDIO";
            public static final String TRACK_TYPE_PRIMARY = "PRIMARY";
            public static final String TRACK_TYPE_TEXT = "TEXT";
            public static final String TRACK_TYPE_VIDEO = "VIDEO";
        }

        public float getLetterboxPercentage() {
            return ((float) this.maxCroppedHeight) / ((float) this.maxHeight);
        }

        public Downloadable[] getDownloadables() {
            return this.downloadables;
        }

        public boolean isHighestBitrate(Comparator<Downloadable> comparator, Downloadable downloadable) {
            int i = 0;
            boolean z = true;
            while (true) {
                Downloadable[] downloadableArr = this.downloadables;
                if (i >= downloadableArr.length) {
                    return z;
                }
                if (comparator.compare(downloadable, downloadableArr[i]) == 0) {
                    z = downloadable.getBitrate() >= this.downloadables[i].getBitrate();
                }
                i++;
            }
        }

        public boolean isLowestBitrate(Comparator<Downloadable> comparator, Downloadable downloadable) {
            int i = 0;
            boolean z = true;
            while (true) {
                Downloadable[] downloadableArr = this.downloadables;
                if (i >= downloadableArr.length) {
                    return z;
                }
                if (comparator.compare(downloadable, downloadableArr[i]) == 0) {
                    z = downloadable.getBitrate() <= this.downloadables[i].getBitrate();
                }
                i++;
            }
        }

        public String getTrackType() {
            return this.trackType;
        }

        public double getChannels() {
            return this.channels;
        }

        public String getId() {
            return this.id;
        }

        public String getLanguage() {
            if (!this.trackType.equals(TrackTypes.TRACK_TYPE_ASSISTIVE)) {
                return this.language;
            }
            if (getType().equals("AUDIO")) {
                return this.language.concat(" [DA]");
            }
            return this.language.concat(" [CC]");
        }

        public String getType() {
            Object obj = this.type;
            String str = "TEXT";
            if (obj instanceof String) {
                return str;
            }
            if ((obj instanceof Double) && obj.equals(Double.valueOf(0.0d))) {
                return "AUDIO";
            }
            Object obj2 = this.type;
            return (!(obj2 instanceof Double) || !obj2.equals(Double.valueOf(1.0d))) ? str : "VIDEO";
        }
    }

    public static class Url {
        /* access modifiers changed from: private */
        public String url;

        public String getUrl() {
            return this.url;
        }
    }

    public long getMovieId() {
        return this.movieId;
    }


    public long getRuntime() {
        return this.runtime;
    }

    public DefaultTrackOrderList getDefaultTrackOrderList() {
        return (DefaultTrackOrderList) this.defaultTrackOrderList.get(0);
    }

    public String getDefaultTrackId(Pattern pattern) {
        String mediaId = getDefaultTrackOrderList().getMediaId();
        if (mediaId != null) {
            Matcher matcher = pattern.matcher(mediaId);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    public Track[] getTextTracks() {
        return this.textTracks;
    }

    public Track[] getAudioTracks() {
        return this.audioTracks;
    }

    public Track[] getVideoTracks() {
        return this.videoTracks;
    }

    public Links getLinks() {
        return this.links;
    }
}
