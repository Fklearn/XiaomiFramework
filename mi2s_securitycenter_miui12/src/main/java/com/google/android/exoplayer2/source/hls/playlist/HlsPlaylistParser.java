package com.google.android.exoplayer2.source.hls.playlist;

import android.net.Uri;
import android.util.Base64;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.source.UnrecognizedInputFormatException;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HlsPlaylistParser implements ParsingLoadable.Parser<HlsPlaylist> {
    private static final String ATTR_CLOSED_CAPTIONS_NONE = "CLOSED-CAPTIONS=NONE";
    private static final String BOOLEAN_FALSE = "NO";
    private static final String BOOLEAN_TRUE = "YES";
    private static final String KEYFORMAT_IDENTITY = "identity";
    private static final String KEYFORMAT_WIDEVINE_PSSH_BINARY = "urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed";
    private static final String KEYFORMAT_WIDEVINE_PSSH_JSON = "com.widevine";
    private static final String METHOD_AES_128 = "AES-128";
    private static final String METHOD_NONE = "NONE";
    private static final String METHOD_SAMPLE_AES = "SAMPLE-AES";
    private static final String METHOD_SAMPLE_AES_CENC = "SAMPLE-AES-CENC";
    private static final String METHOD_SAMPLE_AES_CTR = "SAMPLE-AES-CTR";
    private static final String PLAYLIST_HEADER = "#EXTM3U";
    private static final Pattern REGEX_ATTR_BYTERANGE = Pattern.compile("BYTERANGE=\"(\\d+(?:@\\d+)?)\\b\"");
    private static final Pattern REGEX_AUDIO = Pattern.compile("AUDIO=\"(.+?)\"");
    private static final Pattern REGEX_AUTOSELECT = compileBooleanAttrPattern("AUTOSELECT");
    private static final Pattern REGEX_AVERAGE_BANDWIDTH = Pattern.compile("AVERAGE-BANDWIDTH=(\\d+)\\b");
    private static final Pattern REGEX_BANDWIDTH = Pattern.compile("[^-]BANDWIDTH=(\\d+)\\b");
    private static final Pattern REGEX_BYTERANGE = Pattern.compile("#EXT-X-BYTERANGE:(\\d+(?:@\\d+)?)\\b");
    private static final Pattern REGEX_CODECS = Pattern.compile("CODECS=\"(.+?)\"");
    private static final Pattern REGEX_DEFAULT = compileBooleanAttrPattern("DEFAULT");
    private static final Pattern REGEX_FORCED = compileBooleanAttrPattern("FORCED");
    private static final Pattern REGEX_FRAME_RATE = Pattern.compile("FRAME-RATE=([\\d\\.]+)\\b");
    private static final Pattern REGEX_GROUP_ID = Pattern.compile("GROUP-ID=\"(.+?)\"");
    private static final Pattern REGEX_INSTREAM_ID = Pattern.compile("INSTREAM-ID=\"((?:CC|SERVICE)\\d+)\"");
    private static final Pattern REGEX_IV = Pattern.compile("IV=([^,.*]+)");
    private static final Pattern REGEX_KEYFORMAT = Pattern.compile("KEYFORMAT=\"(.+?)\"");
    private static final Pattern REGEX_LANGUAGE = Pattern.compile("LANGUAGE=\"(.+?)\"");
    private static final Pattern REGEX_MEDIA_DURATION = Pattern.compile("#EXTINF:([\\d\\.]+)\\b");
    private static final Pattern REGEX_MEDIA_SEQUENCE = Pattern.compile("#EXT-X-MEDIA-SEQUENCE:(\\d+)\\b");
    private static final Pattern REGEX_METHOD = Pattern.compile("METHOD=(NONE|AES-128|SAMPLE-AES|SAMPLE-AES-CENC|SAMPLE-AES-CTR)\\s*(,|$)");
    private static final Pattern REGEX_NAME = Pattern.compile("NAME=\"(.+?)\"");
    private static final Pattern REGEX_PLAYLIST_TYPE = Pattern.compile("#EXT-X-PLAYLIST-TYPE:(.+)\\b");
    private static final Pattern REGEX_RESOLUTION = Pattern.compile("RESOLUTION=(\\d+x\\d+)");
    private static final Pattern REGEX_TARGET_DURATION = Pattern.compile("#EXT-X-TARGETDURATION:(\\d+)\\b");
    private static final Pattern REGEX_TIME_OFFSET = Pattern.compile("TIME-OFFSET=(-?[\\d\\.]+)\\b");
    private static final Pattern REGEX_TYPE = Pattern.compile("TYPE=(AUDIO|VIDEO|SUBTITLES|CLOSED-CAPTIONS)");
    private static final Pattern REGEX_URI = Pattern.compile("URI=\"(.+?)\"");
    private static final Pattern REGEX_VERSION = Pattern.compile("#EXT-X-VERSION:(\\d+)\\b");
    private static final String TAG_BYTERANGE = "#EXT-X-BYTERANGE";
    private static final String TAG_DISCONTINUITY = "#EXT-X-DISCONTINUITY";
    private static final String TAG_DISCONTINUITY_SEQUENCE = "#EXT-X-DISCONTINUITY-SEQUENCE";
    private static final String TAG_ENDLIST = "#EXT-X-ENDLIST";
    private static final String TAG_GAP = "#EXT-X-GAP";
    private static final String TAG_INDEPENDENT_SEGMENTS = "#EXT-X-INDEPENDENT-SEGMENTS";
    private static final String TAG_INIT_SEGMENT = "#EXT-X-MAP";
    private static final String TAG_KEY = "#EXT-X-KEY";
    private static final String TAG_MEDIA = "#EXT-X-MEDIA";
    private static final String TAG_MEDIA_DURATION = "#EXTINF";
    private static final String TAG_MEDIA_SEQUENCE = "#EXT-X-MEDIA-SEQUENCE";
    private static final String TAG_PLAYLIST_TYPE = "#EXT-X-PLAYLIST-TYPE";
    private static final String TAG_PREFIX = "#EXT";
    private static final String TAG_PROGRAM_DATE_TIME = "#EXT-X-PROGRAM-DATE-TIME";
    private static final String TAG_START = "#EXT-X-START";
    private static final String TAG_STREAM_INF = "#EXT-X-STREAM-INF";
    private static final String TAG_TARGET_DURATION = "#EXT-X-TARGETDURATION";
    private static final String TAG_VERSION = "#EXT-X-VERSION";
    private static final String TYPE_AUDIO = "AUDIO";
    private static final String TYPE_CLOSED_CAPTIONS = "CLOSED-CAPTIONS";
    private static final String TYPE_SUBTITLES = "SUBTITLES";
    private static final String TYPE_VIDEO = "VIDEO";

    private static class LineIterator {
        private final Queue<String> extraLines;
        private String next;
        private final BufferedReader reader;

        public LineIterator(Queue<String> queue, BufferedReader bufferedReader) {
            this.extraLines = queue;
            this.reader = bufferedReader;
        }

        public boolean hasNext() {
            if (this.next != null) {
                return true;
            }
            if (!this.extraLines.isEmpty()) {
                this.next = this.extraLines.poll();
                return true;
            }
            do {
                String readLine = this.reader.readLine();
                this.next = readLine;
                if (readLine == null) {
                    return false;
                }
                this.next = this.next.trim();
            } while (this.next.isEmpty());
            return true;
        }

        public String next() {
            if (!hasNext()) {
                return null;
            }
            String str = this.next;
            this.next = null;
            return str;
        }
    }

    private static boolean checkPlaylistHeader(BufferedReader bufferedReader) {
        int read = bufferedReader.read();
        if (read == 239) {
            if (bufferedReader.read() != 187 || bufferedReader.read() != 191) {
                return false;
            }
            read = bufferedReader.read();
        }
        int skipIgnorableWhitespace = skipIgnorableWhitespace(bufferedReader, true, read);
        for (int i = 0; i < 7; i++) {
            if (skipIgnorableWhitespace != PLAYLIST_HEADER.charAt(i)) {
                return false;
            }
            skipIgnorableWhitespace = bufferedReader.read();
        }
        return Util.isLinebreak(skipIgnorableWhitespace(bufferedReader, false, skipIgnorableWhitespace));
    }

    private static Pattern compileBooleanAttrPattern(String str) {
        return Pattern.compile(str + "=(" + BOOLEAN_FALSE + "|" + BOOLEAN_TRUE + ")");
    }

    private static boolean parseBooleanAttribute(String str, Pattern pattern, boolean z) {
        Matcher matcher = pattern.matcher(str);
        return matcher.find() ? matcher.group(1).equals(BOOLEAN_TRUE) : z;
    }

    private static double parseDoubleAttr(String str, Pattern pattern) {
        return Double.parseDouble(parseStringAttr(str, pattern));
    }

    private static int parseIntAttr(String str, Pattern pattern) {
        return Integer.parseInt(parseStringAttr(str, pattern));
    }

    private static long parseLongAttr(String str, Pattern pattern) {
        return Long.parseLong(parseStringAttr(str, pattern));
    }

    /* JADX WARNING: Removed duplicated region for block: B:53:0x0151  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x01b8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist parseMasterPlaylist(com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser.LineIterator r26, java.lang.String r27) {
        /*
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r3 = 0
            r8 = r3
        L_0x0025:
            boolean r9 = r26.hasNext()
            r10 = 1
            if (r9 == 0) goto L_0x00e6
            java.lang.String r9 = r26.next()
            java.lang.String r12 = "#EXT"
            boolean r12 = r9.startsWith(r12)
            if (r12 == 0) goto L_0x003b
            r4.add(r9)
        L_0x003b:
            java.lang.String r12 = "#EXT-X-MEDIA"
            boolean r12 = r9.startsWith(r12)
            if (r12 == 0) goto L_0x0047
            r2.add(r9)
            goto L_0x0025
        L_0x0047:
            java.lang.String r12 = "#EXT-X-STREAM-INF"
            boolean r12 = r9.startsWith(r12)
            if (r12 == 0) goto L_0x0025
            java.lang.String r12 = "CLOSED-CAPTIONS=NONE"
            boolean r12 = r9.contains(r12)
            r8 = r8 | r12
            java.util.regex.Pattern r12 = REGEX_BANDWIDTH
            int r12 = parseIntAttr(r9, r12)
            java.util.regex.Pattern r13 = REGEX_AVERAGE_BANDWIDTH
            java.lang.String r13 = parseOptionalStringAttr(r9, r13)
            if (r13 == 0) goto L_0x0068
            int r12 = java.lang.Integer.parseInt(r13)
        L_0x0068:
            r17 = r12
            java.util.regex.Pattern r12 = REGEX_CODECS
            java.lang.String r12 = parseOptionalStringAttr(r9, r12)
            java.util.regex.Pattern r13 = REGEX_RESOLUTION
            java.lang.String r13 = parseOptionalStringAttr(r9, r13)
            if (r13 == 0) goto L_0x0098
            java.lang.String r14 = "x"
            java.lang.String[] r13 = r13.split(r14)
            r14 = r13[r3]
            int r14 = java.lang.Integer.parseInt(r14)
            r13 = r13[r10]
            int r13 = java.lang.Integer.parseInt(r13)
            if (r14 <= 0) goto L_0x0091
            if (r13 > 0) goto L_0x008f
            goto L_0x0091
        L_0x008f:
            r11 = r14
            goto L_0x0093
        L_0x0091:
            r11 = -1
            r13 = -1
        L_0x0093:
            r18 = r11
            r19 = r13
            goto L_0x009c
        L_0x0098:
            r18 = -1
            r19 = -1
        L_0x009c:
            r11 = -1082130432(0xffffffffbf800000, float:-1.0)
            java.util.regex.Pattern r13 = REGEX_FRAME_RATE
            java.lang.String r13 = parseOptionalStringAttr(r9, r13)
            if (r13 == 0) goto L_0x00aa
            float r11 = java.lang.Float.parseFloat(r13)
        L_0x00aa:
            r20 = r11
            java.util.regex.Pattern r11 = REGEX_AUDIO
            java.lang.String r9 = parseOptionalStringAttr(r9, r11)
            if (r9 == 0) goto L_0x00bd
            if (r12 == 0) goto L_0x00bd
            java.lang.String r10 = com.google.android.exoplayer2.util.Util.getCodecsOfType(r12, r10)
            r1.put(r9, r10)
        L_0x00bd:
            java.lang.String r9 = r26.next()
            boolean r10 = r0.add(r9)
            if (r10 == 0) goto L_0x0025
            int r10 = r5.size()
            java.lang.String r13 = java.lang.Integer.toString(r10)
            r15 = 0
            r21 = 0
            r22 = 0
            java.lang.String r14 = "application/x-mpegURL"
            r16 = r12
            com.google.android.exoplayer2.Format r10 = com.google.android.exoplayer2.Format.createVideoContainerFormat(r13, r14, r15, r16, r17, r18, r19, r20, r21, r22)
            com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl r11 = new com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl
            r11.<init>(r9, r10)
            r5.add(r11)
            goto L_0x0025
        L_0x00e6:
            r9 = r3
            r12 = 0
            r13 = 0
        L_0x00e9:
            int r14 = r2.size()
            if (r9 >= r14) goto L_0x01f0
            java.lang.Object r14 = r2.get(r9)
            java.lang.String r14 = (java.lang.String) r14
            int r23 = parseSelectionFlags(r14)
            java.util.regex.Pattern r15 = REGEX_URI
            java.lang.String r15 = parseOptionalStringAttr(r14, r15)
            java.util.regex.Pattern r0 = REGEX_NAME
            java.lang.String r0 = parseStringAttr(r14, r0)
            java.util.regex.Pattern r3 = REGEX_LANGUAGE
            java.lang.String r24 = parseOptionalStringAttr(r14, r3)
            java.util.regex.Pattern r3 = REGEX_GROUP_ID
            java.lang.String r3 = parseOptionalStringAttr(r14, r3)
            java.util.regex.Pattern r11 = REGEX_TYPE
            java.lang.String r11 = parseStringAttr(r14, r11)
            int r10 = r11.hashCode()
            r25 = r2
            r2 = -959297733(0xffffffffc6d2473b, float:-26915.615)
            r16 = r15
            r15 = 2
            if (r10 == r2) goto L_0x0144
            r2 = -333210994(0xffffffffec239a8e, float:-7.911391E26)
            if (r10 == r2) goto L_0x013a
            r2 = 62628790(0x3bba3b6, float:1.1028458E-36)
            if (r10 == r2) goto L_0x0130
            goto L_0x014e
        L_0x0130:
            java.lang.String r2 = "AUDIO"
            boolean r2 = r11.equals(r2)
            if (r2 == 0) goto L_0x014e
            r2 = 0
            goto L_0x014f
        L_0x013a:
            java.lang.String r2 = "CLOSED-CAPTIONS"
            boolean r2 = r11.equals(r2)
            if (r2 == 0) goto L_0x014e
            r2 = r15
            goto L_0x014f
        L_0x0144:
            java.lang.String r2 = "SUBTITLES"
            boolean r2 = r11.equals(r2)
            if (r2 == 0) goto L_0x014e
            r2 = 1
            goto L_0x014f
        L_0x014e:
            r2 = -1
        L_0x014f:
            if (r2 == 0) goto L_0x01b8
            r10 = 1
            if (r2 == r10) goto L_0x019a
            if (r2 == r15) goto L_0x0158
            goto L_0x01e9
        L_0x0158:
            java.util.regex.Pattern r2 = REGEX_INSTREAM_ID
            java.lang.String r2 = parseStringAttr(r14, r2)
            java.lang.String r3 = "CC"
            boolean r3 = r2.startsWith(r3)
            if (r3 == 0) goto L_0x0171
            java.lang.String r2 = r2.substring(r15)
            int r2 = java.lang.Integer.parseInt(r2)
            java.lang.String r3 = "application/cea-608"
            goto L_0x017c
        L_0x0171:
            r3 = 7
            java.lang.String r2 = r2.substring(r3)
            int r2 = java.lang.Integer.parseInt(r2)
            java.lang.String r3 = "application/cea-708"
        L_0x017c:
            r22 = r2
            r17 = r3
            if (r13 != 0) goto L_0x0187
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
        L_0x0187:
            r16 = 0
            r18 = 0
            r19 = -1
            r15 = r0
            r20 = r23
            r21 = r24
            com.google.android.exoplayer2.Format r0 = com.google.android.exoplayer2.Format.createTextContainerFormat(r15, r16, r17, r18, r19, r20, r21, r22)
            r13.add(r0)
            goto L_0x01e9
        L_0x019a:
            r18 = 0
            r19 = -1
            java.lang.String r2 = "application/x-mpegURL"
            java.lang.String r17 = "text/vtt"
            r11 = r16
            r15 = r0
            r16 = r2
            r20 = r23
            r21 = r24
            com.google.android.exoplayer2.Format r0 = com.google.android.exoplayer2.Format.createTextContainerFormat(r15, r16, r17, r18, r19, r20, r21)
            com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl r2 = new com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl
            r2.<init>(r11, r0)
            r7.add(r2)
            goto L_0x01e9
        L_0x01b8:
            r11 = r16
            r10 = 1
            java.lang.Object r2 = r1.get(r3)
            r18 = r2
            java.lang.String r18 = (java.lang.String) r18
            if (r18 == 0) goto L_0x01cc
            java.lang.String r2 = com.google.android.exoplayer2.util.MimeTypes.getMediaMimeType(r18)
            r17 = r2
            goto L_0x01ce
        L_0x01cc:
            r17 = 0
        L_0x01ce:
            r19 = -1
            r20 = -1
            r21 = -1
            r22 = 0
            java.lang.String r16 = "application/x-mpegURL"
            r15 = r0
            com.google.android.exoplayer2.Format r0 = com.google.android.exoplayer2.Format.createAudioContainerFormat(r15, r16, r17, r18, r19, r20, r21, r22, r23, r24)
            if (r11 != 0) goto L_0x01e1
            r12 = r0
            goto L_0x01e9
        L_0x01e1:
            com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl r2 = new com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl
            r2.<init>(r11, r0)
            r6.add(r2)
        L_0x01e9:
            int r9 = r9 + 1
            r2 = r25
            r3 = 0
            goto L_0x00e9
        L_0x01f0:
            if (r8 == 0) goto L_0x01f8
            java.util.List r0 = java.util.Collections.emptyList()
            r9 = r0
            goto L_0x01f9
        L_0x01f8:
            r9 = r13
        L_0x01f9:
            com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist r0 = new com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist
            r2 = r0
            r3 = r27
            r8 = r12
            r2.<init>(r3, r4, r5, r6, r7, r8, r9)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser.parseMasterPlaylist(com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser$LineIterator, java.lang.String):com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist");
    }

    private static HlsMediaPlaylist parseMediaPlaylist(LineIterator lineIterator, String str) {
        DrmInitData.SchemeData parseWidevineSchemeData;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        long j = -9223372036854775807L;
        long j2 = -9223372036854775807L;
        int i = 0;
        int i2 = 0;
        boolean z = false;
        int i3 = 0;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        int i4 = 1;
        long j3 = 0;
        long j4 = 0;
        DrmInitData drmInitData = null;
        long j5 = 0;
        long j6 = 0;
        long j7 = -1;
        long j8 = 0;
        String str2 = null;
        String str3 = null;
        long j9 = 0;
        HlsMediaPlaylist.Segment segment = null;
        while (lineIterator.hasNext()) {
            String next = lineIterator.next();
            if (next.startsWith(TAG_PREFIX)) {
                arrayList2.add(next);
            }
            if (next.startsWith(TAG_PLAYLIST_TYPE)) {
                String parseStringAttr = parseStringAttr(next, REGEX_PLAYLIST_TYPE);
                if ("VOD".equals(parseStringAttr)) {
                    i2 = 1;
                } else if ("EVENT".equals(parseStringAttr)) {
                    i2 = 2;
                }
            } else if (next.startsWith(TAG_START)) {
                j = (long) (parseDoubleAttr(next, REGEX_TIME_OFFSET) * 1000000.0d);
            } else if (next.startsWith(TAG_INIT_SEGMENT)) {
                String parseStringAttr2 = parseStringAttr(next, REGEX_URI);
                String parseOptionalStringAttr = parseOptionalStringAttr(next, REGEX_ATTR_BYTERANGE);
                if (parseOptionalStringAttr != null) {
                    String[] split = parseOptionalStringAttr.split("@");
                    j7 = Long.parseLong(split[0]);
                    if (split.length > 1) {
                        j5 = Long.parseLong(split[1]);
                    }
                }
                segment = new HlsMediaPlaylist.Segment(parseStringAttr2, j5, j7);
                j5 = 0;
                j7 = -1;
            } else if (next.startsWith(TAG_TARGET_DURATION)) {
                j2 = 1000000 * ((long) parseIntAttr(next, REGEX_TARGET_DURATION));
            } else if (next.startsWith(TAG_MEDIA_SEQUENCE)) {
                j6 = parseLongAttr(next, REGEX_MEDIA_SEQUENCE);
                j4 = j6;
            } else if (next.startsWith(TAG_VERSION)) {
                i4 = parseIntAttr(next, REGEX_VERSION);
            } else if (next.startsWith(TAG_MEDIA_DURATION)) {
                j9 = (long) (parseDoubleAttr(next, REGEX_MEDIA_DURATION) * 1000000.0d);
            } else if (next.startsWith(TAG_KEY)) {
                String parseOptionalStringAttr2 = parseOptionalStringAttr(next, REGEX_METHOD);
                String parseOptionalStringAttr3 = parseOptionalStringAttr(next, REGEX_KEYFORMAT);
                if (!METHOD_NONE.equals(parseOptionalStringAttr2)) {
                    String parseOptionalStringAttr4 = parseOptionalStringAttr(next, REGEX_IV);
                    if (KEYFORMAT_IDENTITY.equals(parseOptionalStringAttr3) || parseOptionalStringAttr3 == null) {
                        if (METHOD_AES_128.equals(parseOptionalStringAttr2)) {
                            str2 = parseStringAttr(next, REGEX_URI);
                            str3 = parseOptionalStringAttr4;
                        }
                    } else if (!(parseOptionalStringAttr2 == null || (parseWidevineSchemeData = parseWidevineSchemeData(next, parseOptionalStringAttr3)) == null)) {
                        drmInitData = new DrmInitData((METHOD_SAMPLE_AES_CENC.equals(parseOptionalStringAttr2) || METHOD_SAMPLE_AES_CTR.equals(parseOptionalStringAttr2)) ? C.CENC_TYPE_cenc : C.CENC_TYPE_cbcs, parseWidevineSchemeData);
                    }
                    str3 = parseOptionalStringAttr4;
                    str2 = null;
                } else {
                    str2 = null;
                    str3 = null;
                }
            } else if (next.startsWith(TAG_BYTERANGE)) {
                String[] split2 = parseStringAttr(next, REGEX_BYTERANGE).split("@");
                j7 = Long.parseLong(split2[0]);
                if (split2.length > 1) {
                    j5 = Long.parseLong(split2[1]);
                }
            } else if (next.startsWith(TAG_DISCONTINUITY_SEQUENCE)) {
                i3 = Integer.parseInt(next.substring(next.indexOf(58) + 1));
                z = true;
            } else if (next.equals(TAG_DISCONTINUITY)) {
                i++;
            } else if (next.startsWith(TAG_PROGRAM_DATE_TIME)) {
                if (j3 == 0) {
                    j3 = C.msToUs(Util.parseXsDateTime(next.substring(next.indexOf(58) + 1))) - j8;
                }
            } else if (next.equals(TAG_GAP)) {
                z4 = true;
            } else if (next.equals(TAG_INDEPENDENT_SEGMENTS)) {
                z2 = true;
            } else if (next.equals(TAG_ENDLIST)) {
                z3 = true;
            } else if (!next.startsWith("#")) {
                String hexString = str2 == null ? null : str3 != null ? str3 : Long.toHexString(j6);
                long j10 = j6 + 1;
                int i5 = (j7 > -1 ? 1 : (j7 == -1 ? 0 : -1));
                if (i5 == 0) {
                    j5 = 0;
                }
                arrayList.add(new HlsMediaPlaylist.Segment(next, segment, j9, i, j8, str2, hexString, j5, j7, z4));
                j8 += j9;
                if (i5 != 0) {
                    j5 += j7;
                }
                j6 = j10;
                z4 = false;
                j7 = -1;
                j9 = 0;
            }
        }
        return new HlsMediaPlaylist(i2, str, arrayList2, j, j3, z, i3, j4, i4, j2, z2, z3, j3 != 0, drmInitData, arrayList);
    }

    private static String parseOptionalStringAttr(String str, Pattern pattern) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static int parseSelectionFlags(String str) {
        char c2 = 0;
        boolean parseBooleanAttribute = parseBooleanAttribute(str, REGEX_DEFAULT, false) | (parseBooleanAttribute(str, REGEX_FORCED, false) ? (char) 2 : 0);
        if (parseBooleanAttribute(str, REGEX_AUTOSELECT, false)) {
            c2 = 4;
        }
        return parseBooleanAttribute | c2 ? 1 : 0;
    }

    private static String parseStringAttr(String str, Pattern pattern) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find() && matcher.groupCount() == 1) {
            return matcher.group(1);
        }
        throw new ParserException("Couldn't match " + pattern.pattern() + " in " + str);
    }

    private static DrmInitData.SchemeData parseWidevineSchemeData(String str, String str2) {
        if (KEYFORMAT_WIDEVINE_PSSH_BINARY.equals(str2)) {
            String parseStringAttr = parseStringAttr(str, REGEX_URI);
            return new DrmInitData.SchemeData(C.WIDEVINE_UUID, MimeTypes.VIDEO_MP4, Base64.decode(parseStringAttr.substring(parseStringAttr.indexOf(44)), 0));
        } else if (!KEYFORMAT_WIDEVINE_PSSH_JSON.equals(str2)) {
            return null;
        } else {
            try {
                return new DrmInitData.SchemeData(C.WIDEVINE_UUID, "hls", str.getBytes(C.UTF8_NAME));
            } catch (UnsupportedEncodingException e) {
                throw new ParserException((Throwable) e);
            }
        }
    }

    private static int skipIgnorableWhitespace(BufferedReader bufferedReader, boolean z, int i) {
        while (i != -1 && Character.isWhitespace(i) && (z || !Util.isLinebreak(i))) {
            i = bufferedReader.read();
        }
        return i;
    }

    public HlsPlaylist parse(Uri uri, InputStream inputStream) {
        String trim;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ArrayDeque arrayDeque = new ArrayDeque();
        try {
            if (checkPlaylistHeader(bufferedReader)) {
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine != null) {
                        trim = readLine.trim();
                        if (!trim.isEmpty()) {
                            if (!trim.startsWith(TAG_STREAM_INF)) {
                                if (trim.startsWith(TAG_TARGET_DURATION) || trim.startsWith(TAG_MEDIA_SEQUENCE) || trim.startsWith(TAG_MEDIA_DURATION) || trim.startsWith(TAG_KEY) || trim.startsWith(TAG_BYTERANGE) || trim.equals(TAG_DISCONTINUITY) || trim.equals(TAG_DISCONTINUITY_SEQUENCE)) {
                                    break;
                                } else if (trim.equals(TAG_ENDLIST)) {
                                    break;
                                } else {
                                    arrayDeque.add(trim);
                                }
                            } else {
                                arrayDeque.add(trim);
                                HlsMasterPlaylist parseMasterPlaylist = parseMasterPlaylist(new LineIterator(arrayDeque, bufferedReader), uri.toString());
                                Util.closeQuietly((Closeable) bufferedReader);
                                return parseMasterPlaylist;
                            }
                        }
                    } else {
                        Util.closeQuietly((Closeable) bufferedReader);
                        throw new ParserException("Failed to parse the playlist, could not identify any tags.");
                    }
                }
                arrayDeque.add(trim);
                return parseMediaPlaylist(new LineIterator(arrayDeque, bufferedReader), uri.toString());
            }
            throw new UnrecognizedInputFormatException("Input does not start with the #EXTM3U header.", uri);
        } finally {
            Util.closeQuietly((Closeable) bufferedReader);
        }
    }
}
