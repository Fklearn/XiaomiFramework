package com.google.android.exoplayer2.source.hls;

public final class DefaultHlsExtractorFactory implements HlsExtractorFactory {
    public static final String AAC_FILE_EXTENSION = ".aac";
    public static final String AC3_FILE_EXTENSION = ".ac3";
    public static final String EC3_FILE_EXTENSION = ".ec3";
    public static final String M4_FILE_EXTENSION_PREFIX = ".m4";
    public static final String MP3_FILE_EXTENSION = ".mp3";
    public static final String MP4_FILE_EXTENSION = ".mp4";
    public static final String MP4_FILE_EXTENSION_PREFIX = ".mp4";
    public static final String VTT_FILE_EXTENSION = ".vtt";
    public static final String WEBVTT_FILE_EXTENSION = ".webvtt";

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v1, resolved type: com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: com.google.android.exoplayer2.source.hls.WebvttExtractor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v12, resolved type: com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v18, resolved type: com.google.android.exoplayer2.extractor.Extractor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v21, resolved type: com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v22, resolved type: com.google.android.exoplayer2.extractor.Extractor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v23, resolved type: com.google.android.exoplayer2.extractor.Extractor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v24, resolved type: com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v25, resolved type: com.google.android.exoplayer2.extractor.Extractor} */
    /* JADX WARNING: type inference failed for: r10v6, types: [com.google.android.exoplayer2.extractor.ts.TsExtractor] */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0058, code lost:
        if (r9 == null) goto L_0x005c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0062, code lost:
        if (r10.endsWith(".mp4") != false) goto L_0x00bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0070, code lost:
        if (r10.startsWith(M4_FILE_EXTENSION_PREFIX, r10.length() - 4) != false) goto L_0x00bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x007c, code lost:
        if (r10.startsWith(".mp4", r10.length() - 5) == false) goto L_0x007f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x007f, code lost:
        r9 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0081, code lost:
        if (r12 == null) goto L_0x0086;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0083, code lost:
        r9 = 48;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0086, code lost:
        r12 = java.util.Collections.emptyList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x008a, code lost:
        r10 = r11.codecs;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0090, code lost:
        if (android.text.TextUtils.isEmpty(r10) != false) goto L_0x00ae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x009c, code lost:
        if (com.google.android.exoplayer2.util.MimeTypes.AUDIO_AAC.equals(com.google.android.exoplayer2.util.MimeTypes.getAudioMediaMimeType(r10)) != false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x009e, code lost:
        r9 = r9 | 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00aa, code lost:
        if (com.google.android.exoplayer2.util.MimeTypes.VIDEO_H264.equals(com.google.android.exoplayer2.util.MimeTypes.getVideoMediaMimeType(r10)) != false) goto L_0x00ae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00ac, code lost:
        r9 = r9 | 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00ae, code lost:
        r9 = new com.google.android.exoplayer2.extractor.ts.TsExtractor(2, r14, new com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory(r9, r12));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00bf, code lost:
        if (r12 == null) goto L_0x00c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00c2, code lost:
        r12 = java.util.Collections.emptyList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00c6, code lost:
        r2 = new com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor(0, r14, (com.google.android.exoplayer2.extractor.mp4.Track) null, r13, r12);
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.util.Pair<com.google.android.exoplayer2.extractor.Extractor, java.lang.Boolean> createExtractor(com.google.android.exoplayer2.extractor.Extractor r9, android.net.Uri r10, com.google.android.exoplayer2.Format r11, java.util.List<com.google.android.exoplayer2.Format> r12, com.google.android.exoplayer2.drm.DrmInitData r13, com.google.android.exoplayer2.util.TimestampAdjuster r14) {
        /*
            r8 = this;
            java.lang.String r10 = r10.getLastPathSegment()
            if (r10 != 0) goto L_0x0008
            java.lang.String r10 = ""
        L_0x0008:
            java.lang.String r0 = r11.sampleMimeType
            java.lang.String r1 = "text/vtt"
            boolean r0 = r1.equals(r0)
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x00d5
            java.lang.String r0 = ".webvtt"
            boolean r0 = r10.endsWith(r0)
            if (r0 != 0) goto L_0x00d5
            java.lang.String r0 = ".vtt"
            boolean r0 = r10.endsWith(r0)
            if (r0 == 0) goto L_0x0026
            goto L_0x00d5
        L_0x0026:
            java.lang.String r0 = ".aac"
            boolean r0 = r10.endsWith(r0)
            if (r0 == 0) goto L_0x0036
            com.google.android.exoplayer2.extractor.ts.AdtsExtractor r9 = new com.google.android.exoplayer2.extractor.ts.AdtsExtractor
            r9.<init>()
        L_0x0033:
            r1 = r2
            goto L_0x00dc
        L_0x0036:
            java.lang.String r0 = ".ac3"
            boolean r0 = r10.endsWith(r0)
            if (r0 != 0) goto L_0x00ce
            java.lang.String r0 = ".ec3"
            boolean r0 = r10.endsWith(r0)
            if (r0 == 0) goto L_0x0048
            goto L_0x00ce
        L_0x0048:
            java.lang.String r0 = ".mp3"
            boolean r0 = r10.endsWith(r0)
            if (r0 == 0) goto L_0x0058
            com.google.android.exoplayer2.extractor.mp3.Mp3Extractor r9 = new com.google.android.exoplayer2.extractor.mp3.Mp3Extractor
            r10 = 0
            r9.<init>(r1, r10)
            goto L_0x0033
        L_0x0058:
            if (r9 == 0) goto L_0x005c
            goto L_0x00dc
        L_0x005c:
            java.lang.String r9 = ".mp4"
            boolean r0 = r10.endsWith(r9)
            if (r0 != 0) goto L_0x00bb
            int r0 = r10.length()
            int r0 = r0 + -4
            java.lang.String r2 = ".m4"
            boolean r0 = r10.startsWith(r2, r0)
            if (r0 != 0) goto L_0x00bb
            int r0 = r10.length()
            int r0 = r0 + -5
            boolean r9 = r10.startsWith(r9, r0)
            if (r9 == 0) goto L_0x007f
            goto L_0x00bb
        L_0x007f:
            r9 = 16
            if (r12 == 0) goto L_0x0086
            r9 = 48
            goto L_0x008a
        L_0x0086:
            java.util.List r12 = java.util.Collections.emptyList()
        L_0x008a:
            java.lang.String r10 = r11.codecs
            boolean r11 = android.text.TextUtils.isEmpty(r10)
            if (r11 != 0) goto L_0x00ae
            java.lang.String r11 = com.google.android.exoplayer2.util.MimeTypes.getAudioMediaMimeType(r10)
            java.lang.String r13 = "audio/mp4a-latm"
            boolean r11 = r13.equals(r11)
            if (r11 != 0) goto L_0x00a0
            r9 = r9 | 2
        L_0x00a0:
            java.lang.String r10 = com.google.android.exoplayer2.util.MimeTypes.getVideoMediaMimeType(r10)
            java.lang.String r11 = "video/avc"
            boolean r10 = r11.equals(r10)
            if (r10 != 0) goto L_0x00ae
            r9 = r9 | 4
        L_0x00ae:
            com.google.android.exoplayer2.extractor.ts.TsExtractor r10 = new com.google.android.exoplayer2.extractor.ts.TsExtractor
            com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory r11 = new com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory
            r11.<init>(r9, r12)
            r9 = 2
            r10.<init>(r9, r14, r11)
            r9 = r10
            goto L_0x00dc
        L_0x00bb:
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor r9 = new com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor
            r3 = 0
            r5 = 0
            if (r12 == 0) goto L_0x00c2
            goto L_0x00c6
        L_0x00c2:
            java.util.List r12 = java.util.Collections.emptyList()
        L_0x00c6:
            r7 = r12
            r2 = r9
            r4 = r14
            r6 = r13
            r2.<init>(r3, r4, r5, r6, r7)
            goto L_0x00dc
        L_0x00ce:
            com.google.android.exoplayer2.extractor.ts.Ac3Extractor r9 = new com.google.android.exoplayer2.extractor.ts.Ac3Extractor
            r9.<init>()
            goto L_0x0033
        L_0x00d5:
            com.google.android.exoplayer2.source.hls.WebvttExtractor r9 = new com.google.android.exoplayer2.source.hls.WebvttExtractor
            java.lang.String r10 = r11.language
            r9.<init>(r10, r14)
        L_0x00dc:
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r1)
            android.util.Pair r9 = android.util.Pair.create(r9, r10)
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory.createExtractor(com.google.android.exoplayer2.extractor.Extractor, android.net.Uri, com.google.android.exoplayer2.Format, java.util.List, com.google.android.exoplayer2.drm.DrmInitData, com.google.android.exoplayer2.util.TimestampAdjuster):android.util.Pair");
    }
}
