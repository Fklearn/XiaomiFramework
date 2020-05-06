package com.google.android.exoplayer2.ui;

import android.content.res.Resources;
import android.text.TextUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.util.Locale;

public class DefaultTrackNameProvider implements TrackNameProvider {
    private final Resources resources;

    public DefaultTrackNameProvider(Resources resources2) {
        Assertions.checkNotNull(resources2);
        this.resources = resources2;
    }

    private String buildAudioChannelString(Format format) {
        Resources resources2;
        int i;
        int i2 = format.channelCount;
        if (i2 == -1 || i2 < 1) {
            return "";
        }
        if (i2 == 1) {
            resources2 = this.resources;
            i = R.string.exo_track_mono;
        } else if (i2 == 2) {
            resources2 = this.resources;
            i = R.string.exo_track_stereo;
        } else if (i2 == 6 || i2 == 7) {
            resources2 = this.resources;
            i = R.string.exo_track_surround_5_point_1;
        } else if (i2 != 8) {
            resources2 = this.resources;
            i = R.string.exo_track_surround;
        } else {
            resources2 = this.resources;
            i = R.string.exo_track_surround_7_point_1;
        }
        return resources2.getString(i);
    }

    private String buildBitrateString(Format format) {
        int i = format.bitrate;
        if (i == -1) {
            return "";
        }
        return this.resources.getString(R.string.exo_track_bitrate, new Object[]{Float.valueOf(((float) i) / 1000000.0f)});
    }

    private String buildLanguageString(Format format) {
        String str = format.language;
        return (TextUtils.isEmpty(str) || C.LANGUAGE_UNDETERMINED.equals(str)) ? "" : buildLanguageString(str);
    }

    private String buildLanguageString(String str) {
        return (Util.SDK_INT >= 21 ? Locale.forLanguageTag(str) : new Locale(str)).getDisplayLanguage();
    }

    private String buildResolutionString(Format format) {
        int i = format.width;
        int i2 = format.height;
        if (i == -1 || i2 == -1) {
            return "";
        }
        return this.resources.getString(R.string.exo_track_resolution, new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
    }

    private static int inferPrimaryTrackType(Format format) {
        int trackType = MimeTypes.getTrackType(format.sampleMimeType);
        if (trackType != -1) {
            return trackType;
        }
        if (MimeTypes.getVideoMediaMimeType(format.codecs) != null) {
            return 2;
        }
        if (MimeTypes.getAudioMediaMimeType(format.codecs) != null) {
            return 1;
        }
        if (format.width == -1 && format.height == -1) {
            return (format.channelCount == -1 && format.sampleRate == -1) ? -1 : 1;
        }
        return 2;
    }

    private String joinWithSeparator(String... strArr) {
        String str = "";
        for (String str2 : strArr) {
            if (str2.length() > 0) {
                if (TextUtils.isEmpty(str)) {
                    str = str2;
                } else {
                    str = this.resources.getString(R.string.exo_item_list, new Object[]{str, str2});
                }
            }
        }
        return str;
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x003e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getTrackName(com.google.android.exoplayer2.Format r6) {
        /*
            r5 = this;
            int r0 = inferPrimaryTrackType(r6)
            r1 = 0
            r2 = 1
            r3 = 2
            if (r0 != r3) goto L_0x001c
            java.lang.String[] r0 = new java.lang.String[r3]
            java.lang.String r3 = r5.buildResolutionString(r6)
            r0[r1] = r3
            java.lang.String r6 = r5.buildBitrateString(r6)
            r0[r2] = r6
        L_0x0017:
            java.lang.String r6 = r5.joinWithSeparator(r0)
            goto L_0x0038
        L_0x001c:
            if (r0 != r2) goto L_0x0034
            r0 = 3
            java.lang.String[] r0 = new java.lang.String[r0]
            java.lang.String r4 = r5.buildLanguageString((com.google.android.exoplayer2.Format) r6)
            r0[r1] = r4
            java.lang.String r1 = r5.buildAudioChannelString(r6)
            r0[r2] = r1
            java.lang.String r6 = r5.buildBitrateString(r6)
            r0[r3] = r6
            goto L_0x0017
        L_0x0034:
            java.lang.String r6 = r5.buildLanguageString((com.google.android.exoplayer2.Format) r6)
        L_0x0038:
            int r0 = r6.length()
            if (r0 != 0) goto L_0x0046
            android.content.res.Resources r6 = r5.resources
            int r0 = com.google.android.exoplayer2.ui.R.string.exo_track_unknown
            java.lang.String r6 = r6.getString(r0)
        L_0x0046:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ui.DefaultTrackNameProvider.getTrackName(com.google.android.exoplayer2.Format):java.lang.String");
    }
}
