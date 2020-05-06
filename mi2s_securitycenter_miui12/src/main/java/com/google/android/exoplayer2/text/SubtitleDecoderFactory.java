package com.google.android.exoplayer2.text;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.MimeTypes;

public interface SubtitleDecoderFactory {
    public static final SubtitleDecoderFactory DEFAULT = new SubtitleDecoderFactory() {
        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.google.android.exoplayer2.text.SubtitleDecoder createDecoder(com.google.android.exoplayer2.Format r3) {
            /*
                r2 = this;
                java.lang.String r0 = r3.sampleMimeType
                int r1 = r0.hashCode()
                switch(r1) {
                    case -1351681404: goto L_0x0072;
                    case -1248334819: goto L_0x0067;
                    case -1026075066: goto L_0x005d;
                    case -1004728940: goto L_0x0053;
                    case 691401887: goto L_0x0049;
                    case 822864842: goto L_0x003f;
                    case 930165504: goto L_0x0035;
                    case 1566015601: goto L_0x002b;
                    case 1566016562: goto L_0x0020;
                    case 1668750253: goto L_0x0016;
                    case 1693976202: goto L_0x000b;
                    default: goto L_0x0009;
                }
            L_0x0009:
                goto L_0x007d
            L_0x000b:
                java.lang.String r1 = "application/ttml+xml"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x007d
                r0 = 3
                goto L_0x007e
            L_0x0016:
                java.lang.String r1 = "application/x-subrip"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x007d
                r0 = 4
                goto L_0x007e
            L_0x0020:
                java.lang.String r1 = "application/cea-708"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x007d
                r0 = 8
                goto L_0x007e
            L_0x002b:
                java.lang.String r1 = "application/cea-608"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x007d
                r0 = 6
                goto L_0x007e
            L_0x0035:
                java.lang.String r1 = "application/x-mp4-cea-608"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x007d
                r0 = 7
                goto L_0x007e
            L_0x003f:
                java.lang.String r1 = "text/x-ssa"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x007d
                r0 = 1
                goto L_0x007e
            L_0x0049:
                java.lang.String r1 = "application/x-quicktime-tx3g"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x007d
                r0 = 5
                goto L_0x007e
            L_0x0053:
                java.lang.String r1 = "text/vtt"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x007d
                r0 = 0
                goto L_0x007e
            L_0x005d:
                java.lang.String r1 = "application/x-mp4-vtt"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x007d
                r0 = 2
                goto L_0x007e
            L_0x0067:
                java.lang.String r1 = "application/pgs"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x007d
                r0 = 10
                goto L_0x007e
            L_0x0072:
                java.lang.String r1 = "application/dvbsubs"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x007d
                r0 = 9
                goto L_0x007e
            L_0x007d:
                r0 = -1
            L_0x007e:
                switch(r0) {
                    case 0: goto L_0x00cb;
                    case 1: goto L_0x00c3;
                    case 2: goto L_0x00bd;
                    case 3: goto L_0x00b7;
                    case 4: goto L_0x00b1;
                    case 5: goto L_0x00a9;
                    case 6: goto L_0x009f;
                    case 7: goto L_0x009f;
                    case 8: goto L_0x0097;
                    case 9: goto L_0x008f;
                    case 10: goto L_0x0089;
                    default: goto L_0x0081;
                }
            L_0x0081:
                java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException
                java.lang.String r0 = "Attempted to create decoder for unsupported format"
                r3.<init>(r0)
                throw r3
            L_0x0089:
                com.google.android.exoplayer2.text.pgs.PgsDecoder r3 = new com.google.android.exoplayer2.text.pgs.PgsDecoder
                r3.<init>()
                return r3
            L_0x008f:
                com.google.android.exoplayer2.text.dvb.DvbDecoder r0 = new com.google.android.exoplayer2.text.dvb.DvbDecoder
                java.util.List<byte[]> r3 = r3.initializationData
                r0.<init>(r3)
                return r0
            L_0x0097:
                com.google.android.exoplayer2.text.cea.Cea708Decoder r0 = new com.google.android.exoplayer2.text.cea.Cea708Decoder
                int r3 = r3.accessibilityChannel
                r0.<init>(r3)
                return r0
            L_0x009f:
                com.google.android.exoplayer2.text.cea.Cea608Decoder r0 = new com.google.android.exoplayer2.text.cea.Cea608Decoder
                java.lang.String r1 = r3.sampleMimeType
                int r3 = r3.accessibilityChannel
                r0.<init>(r1, r3)
                return r0
            L_0x00a9:
                com.google.android.exoplayer2.text.tx3g.Tx3gDecoder r0 = new com.google.android.exoplayer2.text.tx3g.Tx3gDecoder
                java.util.List<byte[]> r3 = r3.initializationData
                r0.<init>(r3)
                return r0
            L_0x00b1:
                com.google.android.exoplayer2.text.subrip.SubripDecoder r3 = new com.google.android.exoplayer2.text.subrip.SubripDecoder
                r3.<init>()
                return r3
            L_0x00b7:
                com.google.android.exoplayer2.text.ttml.TtmlDecoder r3 = new com.google.android.exoplayer2.text.ttml.TtmlDecoder
                r3.<init>()
                return r3
            L_0x00bd:
                com.google.android.exoplayer2.text.webvtt.Mp4WebvttDecoder r3 = new com.google.android.exoplayer2.text.webvtt.Mp4WebvttDecoder
                r3.<init>()
                return r3
            L_0x00c3:
                com.google.android.exoplayer2.text.ssa.SsaDecoder r0 = new com.google.android.exoplayer2.text.ssa.SsaDecoder
                java.util.List<byte[]> r3 = r3.initializationData
                r0.<init>(r3)
                return r0
            L_0x00cb:
                com.google.android.exoplayer2.text.webvtt.WebvttDecoder r3 = new com.google.android.exoplayer2.text.webvtt.WebvttDecoder
                r3.<init>()
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.SubtitleDecoderFactory.AnonymousClass1.createDecoder(com.google.android.exoplayer2.Format):com.google.android.exoplayer2.text.SubtitleDecoder");
        }

        public boolean supportsFormat(Format format) {
            String str = format.sampleMimeType;
            return MimeTypes.TEXT_VTT.equals(str) || MimeTypes.TEXT_SSA.equals(str) || MimeTypes.APPLICATION_TTML.equals(str) || MimeTypes.APPLICATION_MP4VTT.equals(str) || MimeTypes.APPLICATION_SUBRIP.equals(str) || MimeTypes.APPLICATION_TX3G.equals(str) || MimeTypes.APPLICATION_CEA608.equals(str) || MimeTypes.APPLICATION_MP4CEA608.equals(str) || MimeTypes.APPLICATION_CEA708.equals(str) || MimeTypes.APPLICATION_DVBSUBS.equals(str) || MimeTypes.APPLICATION_PGS.equals(str);
        }
    };

    SubtitleDecoder createDecoder(Format format);

    boolean supportsFormat(Format format);
}
