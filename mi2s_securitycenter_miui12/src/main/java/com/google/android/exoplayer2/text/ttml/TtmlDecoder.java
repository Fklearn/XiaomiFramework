package com.google.android.exoplayer2.text.ttml;

import android.util.Log;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.util.XmlPullParserUtil;
import com.xiaomi.stat.d;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public final class TtmlDecoder extends SimpleSubtitleDecoder {
    private static final String ATTR_BEGIN = "begin";
    private static final String ATTR_DURATION = "dur";
    private static final String ATTR_END = "end";
    private static final String ATTR_REGION = "region";
    private static final String ATTR_STYLE = "style";
    private static final Pattern CELL_RESOLUTION = Pattern.compile("^(\\d+) (\\d+)$");
    private static final Pattern CLOCK_TIME = Pattern.compile("^([0-9][0-9]+):([0-9][0-9]):([0-9][0-9])(?:(\\.[0-9]+)|:([0-9][0-9])(?:\\.([0-9]+))?)?$");
    private static final CellResolution DEFAULT_CELL_RESOLUTION = new CellResolution(32, 15);
    private static final FrameAndTickRate DEFAULT_FRAME_AND_TICK_RATE = new FrameAndTickRate(30.0f, 1, 1);
    private static final int DEFAULT_FRAME_RATE = 30;
    private static final Pattern FONT_SIZE = Pattern.compile("^(([0-9]*.)?[0-9]+)(px|em|%)$");
    private static final Pattern OFFSET_TIME = Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)(h|m|s|ms|f|t)$");
    private static final Pattern PERCENTAGE_COORDINATES = Pattern.compile("^(\\d+\\.?\\d*?)% (\\d+\\.?\\d*?)%$");
    private static final String TAG = "TtmlDecoder";
    private static final String TTP = "http://www.w3.org/ns/ttml#parameter";
    private final XmlPullParserFactory xmlParserFactory;

    private static final class CellResolution {
        final int columns;
        final int rows;

        CellResolution(int i, int i2) {
            this.columns = i;
            this.rows = i2;
        }
    }

    private static final class FrameAndTickRate {
        final float effectiveFrameRate;
        final int subFrameRate;
        final int tickRate;

        FrameAndTickRate(float f, int i, int i2) {
            this.effectiveFrameRate = f;
            this.subFrameRate = i;
            this.tickRate = i2;
        }
    }

    public TtmlDecoder() {
        super(TAG);
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
            this.xmlParserFactory.setNamespaceAware(true);
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    private TtmlStyle createIfNull(TtmlStyle ttmlStyle) {
        return ttmlStyle == null ? new TtmlStyle() : ttmlStyle;
    }

    private static boolean isSupportedTag(String str) {
        return str.equals(TtmlNode.TAG_TT) || str.equals(TtmlNode.TAG_HEAD) || str.equals(TtmlNode.TAG_BODY) || str.equals(TtmlNode.TAG_DIV) || str.equals("p") || str.equals(TtmlNode.TAG_SPAN) || str.equals(TtmlNode.TAG_BR) || str.equals("style") || str.equals(TtmlNode.TAG_STYLING) || str.equals(TtmlNode.TAG_LAYOUT) || str.equals("region") || str.equals(TtmlNode.TAG_METADATA) || str.equals(TtmlNode.TAG_SMPTE_IMAGE) || str.equals(TtmlNode.TAG_SMPTE_DATA) || str.equals(TtmlNode.TAG_SMPTE_INFORMATION);
    }

    private CellResolution parseCellResolution(XmlPullParser xmlPullParser, CellResolution cellResolution) {
        StringBuilder sb;
        String attributeValue = xmlPullParser.getAttributeValue(TTP, "cellResolution");
        if (attributeValue == null) {
            return cellResolution;
        }
        Matcher matcher = CELL_RESOLUTION.matcher(attributeValue);
        if (!matcher.matches()) {
            sb = new StringBuilder();
        } else {
            try {
                int parseInt = Integer.parseInt(matcher.group(1));
                int parseInt2 = Integer.parseInt(matcher.group(2));
                if (parseInt != 0 && parseInt2 != 0) {
                    return new CellResolution(parseInt, parseInt2);
                }
                throw new SubtitleDecoderException("Invalid cell resolution " + parseInt + " " + parseInt2);
            } catch (NumberFormatException unused) {
                sb = new StringBuilder();
            }
        }
        sb.append("Ignoring malformed cell resolution: ");
        sb.append(attributeValue);
        Log.w(TAG, sb.toString());
        return cellResolution;
    }

    private static void parseFontSize(String str, TtmlStyle ttmlStyle) {
        Matcher matcher;
        String[] split = Util.split(str, "\\s+");
        if (split.length == 1) {
            matcher = FONT_SIZE.matcher(str);
        } else if (split.length == 2) {
            matcher = FONT_SIZE.matcher(split[1]);
            Log.w(TAG, "Multiple values in fontSize attribute. Picking the second value for vertical font size and ignoring the first.");
        } else {
            throw new SubtitleDecoderException("Invalid number of entries for fontSize: " + split.length + ".");
        }
        if (matcher.matches()) {
            String group = matcher.group(3);
            char c2 = 65535;
            int hashCode = group.hashCode();
            if (hashCode != 37) {
                if (hashCode != 3240) {
                    if (hashCode == 3592 && group.equals("px")) {
                        c2 = 0;
                    }
                } else if (group.equals(d.ag)) {
                    c2 = 1;
                }
            } else if (group.equals("%")) {
                c2 = 2;
            }
            if (c2 == 0) {
                ttmlStyle.setFontSizeUnit(1);
            } else if (c2 == 1) {
                ttmlStyle.setFontSizeUnit(2);
            } else if (c2 == 2) {
                ttmlStyle.setFontSizeUnit(3);
            } else {
                throw new SubtitleDecoderException("Invalid unit for fontSize: '" + group + "'.");
            }
            ttmlStyle.setFontSize(Float.valueOf(matcher.group(1)).floatValue());
            return;
        }
        throw new SubtitleDecoderException("Invalid expression for fontSize: '" + str + "'.");
    }

    private FrameAndTickRate parseFrameAndTickRates(XmlPullParser xmlPullParser) {
        String attributeValue = xmlPullParser.getAttributeValue(TTP, "frameRate");
        int parseInt = attributeValue != null ? Integer.parseInt(attributeValue) : 30;
        float f = 1.0f;
        String attributeValue2 = xmlPullParser.getAttributeValue(TTP, "frameRateMultiplier");
        if (attributeValue2 != null) {
            String[] split = Util.split(attributeValue2, " ");
            if (split.length == 2) {
                f = ((float) Integer.parseInt(split[0])) / ((float) Integer.parseInt(split[1]));
            } else {
                throw new SubtitleDecoderException("frameRateMultiplier doesn't have 2 parts");
            }
        }
        int i = DEFAULT_FRAME_AND_TICK_RATE.subFrameRate;
        String attributeValue3 = xmlPullParser.getAttributeValue(TTP, "subFrameRate");
        if (attributeValue3 != null) {
            i = Integer.parseInt(attributeValue3);
        }
        int i2 = DEFAULT_FRAME_AND_TICK_RATE.tickRate;
        String attributeValue4 = xmlPullParser.getAttributeValue(TTP, "tickRate");
        if (attributeValue4 != null) {
            i2 = Integer.parseInt(attributeValue4);
        }
        return new FrameAndTickRate(((float) parseInt) * f, i, i2);
    }

    private Map<String, TtmlStyle> parseHeader(XmlPullParser xmlPullParser, Map<String, TtmlStyle> map, Map<String, TtmlRegion> map2, CellResolution cellResolution) {
        TtmlRegion parseRegionAttributes;
        do {
            xmlPullParser.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "style")) {
                String attributeValue = XmlPullParserUtil.getAttributeValue(xmlPullParser, "style");
                TtmlStyle parseStyleAttributes = parseStyleAttributes(xmlPullParser, new TtmlStyle());
                if (attributeValue != null) {
                    for (String str : parseStyleIds(attributeValue)) {
                        parseStyleAttributes.chain(map.get(str));
                    }
                }
                if (parseStyleAttributes.getId() != null) {
                    map.put(parseStyleAttributes.getId(), parseStyleAttributes);
                }
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "region") && (parseRegionAttributes = parseRegionAttributes(xmlPullParser, cellResolution)) != null) {
                map2.put(parseRegionAttributes.id, parseRegionAttributes);
            }
        } while (!XmlPullParserUtil.isEndTag(xmlPullParser, TtmlNode.TAG_HEAD));
        return map;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.google.android.exoplayer2.text.ttml.TtmlNode parseNode(org.xmlpull.v1.XmlPullParser r20, com.google.android.exoplayer2.text.ttml.TtmlNode r21, java.util.Map<java.lang.String, com.google.android.exoplayer2.text.ttml.TtmlRegion> r22, com.google.android.exoplayer2.text.ttml.TtmlDecoder.FrameAndTickRate r23) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r23
            int r4 = r20.getAttributeCount()
            r5 = 0
            com.google.android.exoplayer2.text.ttml.TtmlStyle r11 = r0.parseStyleAttributes(r1, r5)
            java.lang.String r9 = ""
            r17 = r5
            r16 = r9
            r5 = 0
            r9 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            r12 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            r14 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
        L_0x0027:
            if (r5 >= r4) goto L_0x00af
            java.lang.String r6 = r1.getAttributeName(r5)
            java.lang.String r7 = r1.getAttributeValue(r5)
            int r18 = r6.hashCode()
            switch(r18) {
                case -934795532: goto L_0x0061;
                case 99841: goto L_0x0057;
                case 100571: goto L_0x004d;
                case 93616297: goto L_0x0043;
                case 109780401: goto L_0x0039;
                default: goto L_0x0038;
            }
        L_0x0038:
            goto L_0x006b
        L_0x0039:
            java.lang.String r8 = "style"
            boolean r6 = r6.equals(r8)
            if (r6 == 0) goto L_0x006b
            r6 = 3
            goto L_0x006c
        L_0x0043:
            java.lang.String r8 = "begin"
            boolean r6 = r6.equals(r8)
            if (r6 == 0) goto L_0x006b
            r6 = 0
            goto L_0x006c
        L_0x004d:
            java.lang.String r8 = "end"
            boolean r6 = r6.equals(r8)
            if (r6 == 0) goto L_0x006b
            r6 = 1
            goto L_0x006c
        L_0x0057:
            java.lang.String r8 = "dur"
            boolean r6 = r6.equals(r8)
            if (r6 == 0) goto L_0x006b
            r6 = 2
            goto L_0x006c
        L_0x0061:
            java.lang.String r8 = "region"
            boolean r6 = r6.equals(r8)
            if (r6 == 0) goto L_0x006b
            r6 = 4
            goto L_0x006c
        L_0x006b:
            r6 = -1
        L_0x006c:
            if (r6 == 0) goto L_0x00a4
            r8 = 1
            if (r6 == r8) goto L_0x009c
            r8 = 2
            if (r6 == r8) goto L_0x0094
            r8 = 3
            if (r6 == r8) goto L_0x0088
            r8 = 4
            if (r6 == r8) goto L_0x007d
            r6 = r22
            goto L_0x00ab
        L_0x007d:
            r6 = r22
            boolean r8 = r6.containsKey(r7)
            if (r8 == 0) goto L_0x00ab
            r16 = r7
            goto L_0x00ab
        L_0x0088:
            r6 = r22
            java.lang.String[] r7 = r0.parseStyleIds(r7)
            int r8 = r7.length
            if (r8 <= 0) goto L_0x00ab
            r17 = r7
            goto L_0x00ab
        L_0x0094:
            r6 = r22
            long r7 = parseTimeExpression(r7, r3)
            r14 = r7
            goto L_0x00ab
        L_0x009c:
            r6 = r22
            long r7 = parseTimeExpression(r7, r3)
            r12 = r7
            goto L_0x00ab
        L_0x00a4:
            r6 = r22
            long r7 = parseTimeExpression(r7, r3)
            r9 = r7
        L_0x00ab:
            int r5 = r5 + 1
            goto L_0x0027
        L_0x00af:
            if (r2 == 0) goto L_0x00c9
            long r3 = r2.startTimeUs
            r5 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x00ce
            int r7 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x00c1
            long r9 = r9 + r3
        L_0x00c1:
            int r3 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r3 == 0) goto L_0x00ce
            long r3 = r2.startTimeUs
            long r12 = r12 + r3
            goto L_0x00ce
        L_0x00c9:
            r5 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
        L_0x00ce:
            r7 = r9
            int r3 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r3 != 0) goto L_0x00e4
            int r3 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
            if (r3 == 0) goto L_0x00da
            long r14 = r14 + r7
            r9 = r14
            goto L_0x00e5
        L_0x00da:
            if (r2 == 0) goto L_0x00e4
            long r2 = r2.endTimeUs
            int r4 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r4 == 0) goto L_0x00e4
            r9 = r2
            goto L_0x00e5
        L_0x00e4:
            r9 = r12
        L_0x00e5:
            java.lang.String r6 = r20.getName()
            r12 = r17
            r13 = r16
            com.google.android.exoplayer2.text.ttml.TtmlNode r1 = com.google.android.exoplayer2.text.ttml.TtmlNode.buildNode(r6, r7, r9, r11, r12, r13)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.ttml.TtmlDecoder.parseNode(org.xmlpull.v1.XmlPullParser, com.google.android.exoplayer2.text.ttml.TtmlNode, java.util.Map, com.google.android.exoplayer2.text.ttml.TtmlDecoder$FrameAndTickRate):com.google.android.exoplayer2.text.ttml.TtmlNode");
    }

    private TtmlRegion parseRegionAttributes(XmlPullParser xmlPullParser, CellResolution cellResolution) {
        String str;
        StringBuilder sb;
        String str2;
        String attributeValue = XmlPullParserUtil.getAttributeValue(xmlPullParser, "id");
        if (attributeValue == null) {
            return null;
        }
        String attributeValue2 = XmlPullParserUtil.getAttributeValue(xmlPullParser, "origin");
        if (attributeValue2 != null) {
            Matcher matcher = PERCENTAGE_COORDINATES.matcher(attributeValue2);
            if (matcher.matches()) {
                try {
                    float parseFloat = Float.parseFloat(matcher.group(1)) / 100.0f;
                    int i = 2;
                    float parseFloat2 = Float.parseFloat(matcher.group(2)) / 100.0f;
                    String attributeValue3 = XmlPullParserUtil.getAttributeValue(xmlPullParser, TtmlNode.ATTR_TTS_EXTENT);
                    if (attributeValue3 != null) {
                        Matcher matcher2 = PERCENTAGE_COORDINATES.matcher(attributeValue3);
                        if (matcher2.matches()) {
                            try {
                                float parseFloat3 = Float.parseFloat(matcher2.group(1)) / 100.0f;
                                float parseFloat4 = Float.parseFloat(matcher2.group(2)) / 100.0f;
                                String attributeValue4 = XmlPullParserUtil.getAttributeValue(xmlPullParser, TtmlNode.ATTR_TTS_DISPLAY_ALIGN);
                                if (attributeValue4 != null) {
                                    String lowerInvariant = Util.toLowerInvariant(attributeValue4);
                                    char c2 = 65535;
                                    int hashCode = lowerInvariant.hashCode();
                                    if (hashCode != -1364013995) {
                                        if (hashCode == 92734940 && lowerInvariant.equals("after")) {
                                            c2 = 1;
                                        }
                                    } else if (lowerInvariant.equals(TtmlNode.CENTER)) {
                                        c2 = 0;
                                    }
                                    if (c2 == 0) {
                                        parseFloat2 += parseFloat4 / 2.0f;
                                        i = 1;
                                    } else if (c2 == 1) {
                                        parseFloat2 += parseFloat4;
                                    }
                                    return new TtmlRegion(attributeValue, parseFloat, parseFloat2, 0, i, parseFloat3, 1, 1.0f / ((float) cellResolution.rows));
                                }
                                i = 0;
                                return new TtmlRegion(attributeValue, parseFloat, parseFloat2, 0, i, parseFloat3, 1, 1.0f / ((float) cellResolution.rows));
                            } catch (NumberFormatException unused) {
                                sb = new StringBuilder();
                                str2 = "Ignoring region with malformed extent: ";
                            }
                        } else {
                            sb = new StringBuilder();
                            str2 = "Ignoring region with unsupported extent: ";
                        }
                    } else {
                        str = "Ignoring region without an extent";
                    }
                } catch (NumberFormatException unused2) {
                    sb = new StringBuilder();
                    str2 = "Ignoring region with malformed origin: ";
                }
            } else {
                sb = new StringBuilder();
                str2 = "Ignoring region with unsupported origin: ";
            }
            sb.append(str2);
            sb.append(attributeValue2);
            str = sb.toString();
        } else {
            str = "Ignoring region without an origin";
        }
        Log.w(TAG, str);
        return null;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.google.android.exoplayer2.text.ttml.TtmlStyle parseStyleAttributes(org.xmlpull.v1.XmlPullParser r12, com.google.android.exoplayer2.text.ttml.TtmlStyle r13) {
        /*
            r11 = this;
            int r0 = r12.getAttributeCount()
            r1 = 0
            r2 = r13
            r13 = r1
        L_0x0007:
            if (r13 >= r0) goto L_0x01cf
            java.lang.String r3 = r12.getAttributeValue(r13)
            java.lang.String r4 = r12.getAttributeName(r13)
            int r5 = r4.hashCode()
            r6 = 4
            r7 = -1
            r8 = 2
            r9 = 3
            r10 = 1
            switch(r5) {
                case -1550943582: goto L_0x0070;
                case -1224696685: goto L_0x0066;
                case -1065511464: goto L_0x005c;
                case -879295043: goto L_0x0051;
                case -734428249: goto L_0x0047;
                case 3355: goto L_0x003d;
                case 94842723: goto L_0x0033;
                case 365601008: goto L_0x0029;
                case 1287124693: goto L_0x001f;
                default: goto L_0x001d;
            }
        L_0x001d:
            goto L_0x007a
        L_0x001f:
            java.lang.String r5 = "backgroundColor"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x007a
            r4 = r10
            goto L_0x007b
        L_0x0029:
            java.lang.String r5 = "fontSize"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x007a
            r4 = r6
            goto L_0x007b
        L_0x0033:
            java.lang.String r5 = "color"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x007a
            r4 = r8
            goto L_0x007b
        L_0x003d:
            java.lang.String r5 = "id"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x007a
            r4 = r1
            goto L_0x007b
        L_0x0047:
            java.lang.String r5 = "fontWeight"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x007a
            r4 = 5
            goto L_0x007b
        L_0x0051:
            java.lang.String r5 = "textDecoration"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x007a
            r4 = 8
            goto L_0x007b
        L_0x005c:
            java.lang.String r5 = "textAlign"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x007a
            r4 = 7
            goto L_0x007b
        L_0x0066:
            java.lang.String r5 = "fontFamily"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x007a
            r4 = r9
            goto L_0x007b
        L_0x0070:
            java.lang.String r5 = "fontStyle"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x007a
            r4 = 6
            goto L_0x007b
        L_0x007a:
            r4 = r7
        L_0x007b:
            java.lang.String r5 = "TtmlDecoder"
            switch(r4) {
                case 0: goto L_0x01b7;
                case 1: goto L_0x01a3;
                case 2: goto L_0x018f;
                case 3: goto L_0x0186;
                case 4: goto L_0x0169;
                case 5: goto L_0x015a;
                case 6: goto L_0x014a;
                case 7: goto L_0x00e7;
                case 8: goto L_0x0082;
                default: goto L_0x0080;
            }
        L_0x0080:
            goto L_0x01cb
        L_0x0082:
            java.lang.String r3 = com.google.android.exoplayer2.util.Util.toLowerInvariant(r3)
            int r4 = r3.hashCode()
            switch(r4) {
                case -1461280213: goto L_0x00ac;
                case -1026963764: goto L_0x00a2;
                case 913457136: goto L_0x0098;
                case 1679736913: goto L_0x008e;
                default: goto L_0x008d;
            }
        L_0x008d:
            goto L_0x00b5
        L_0x008e:
            java.lang.String r4 = "linethrough"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x00b5
            r7 = r1
            goto L_0x00b5
        L_0x0098:
            java.lang.String r4 = "nolinethrough"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x00b5
            r7 = r10
            goto L_0x00b5
        L_0x00a2:
            java.lang.String r4 = "underline"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x00b5
            r7 = r8
            goto L_0x00b5
        L_0x00ac:
            java.lang.String r4 = "nounderline"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x00b5
            r7 = r9
        L_0x00b5:
            if (r7 == 0) goto L_0x00dd
            if (r7 == r10) goto L_0x00d3
            if (r7 == r8) goto L_0x00c9
            if (r7 == r9) goto L_0x00bf
            goto L_0x01cb
        L_0x00bf:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r11.createIfNull(r2)
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r2.setUnderline(r1)
            goto L_0x01cb
        L_0x00c9:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r11.createIfNull(r2)
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r2.setUnderline(r10)
            goto L_0x01cb
        L_0x00d3:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r11.createIfNull(r2)
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r2.setLinethrough(r1)
            goto L_0x01cb
        L_0x00dd:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r11.createIfNull(r2)
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r2.setLinethrough(r10)
            goto L_0x01cb
        L_0x00e7:
            java.lang.String r3 = com.google.android.exoplayer2.util.Util.toLowerInvariant(r3)
            int r4 = r3.hashCode()
            switch(r4) {
                case -1364013995: goto L_0x011b;
                case 100571: goto L_0x0111;
                case 3317767: goto L_0x0107;
                case 108511772: goto L_0x00fd;
                case 109757538: goto L_0x00f3;
                default: goto L_0x00f2;
            }
        L_0x00f2:
            goto L_0x0124
        L_0x00f3:
            java.lang.String r4 = "start"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x0124
            r7 = r10
            goto L_0x0124
        L_0x00fd:
            java.lang.String r4 = "right"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x0124
            r7 = r8
            goto L_0x0124
        L_0x0107:
            java.lang.String r4 = "left"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x0124
            r7 = r1
            goto L_0x0124
        L_0x0111:
            java.lang.String r4 = "end"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x0124
            r7 = r9
            goto L_0x0124
        L_0x011b:
            java.lang.String r4 = "center"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x0124
            r7 = r6
        L_0x0124:
            if (r7 == 0) goto L_0x013e
            if (r7 == r10) goto L_0x013e
            if (r7 == r8) goto L_0x0137
            if (r7 == r9) goto L_0x0137
            if (r7 == r6) goto L_0x0130
            goto L_0x01cb
        L_0x0130:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r11.createIfNull(r2)
            android.text.Layout$Alignment r3 = android.text.Layout.Alignment.ALIGN_CENTER
            goto L_0x0144
        L_0x0137:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r11.createIfNull(r2)
            android.text.Layout$Alignment r3 = android.text.Layout.Alignment.ALIGN_OPPOSITE
            goto L_0x0144
        L_0x013e:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r11.createIfNull(r2)
            android.text.Layout$Alignment r3 = android.text.Layout.Alignment.ALIGN_NORMAL
        L_0x0144:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r2.setTextAlign(r3)
            goto L_0x01cb
        L_0x014a:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r11.createIfNull(r2)
            java.lang.String r4 = "italic"
            boolean r3 = r4.equalsIgnoreCase(r3)
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r2.setItalic(r3)
            goto L_0x01cb
        L_0x015a:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r11.createIfNull(r2)
            java.lang.String r4 = "bold"
            boolean r3 = r4.equalsIgnoreCase(r3)
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r2.setBold(r3)
            goto L_0x01cb
        L_0x0169:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r11.createIfNull(r2)     // Catch:{ SubtitleDecoderException -> 0x0171 }
            parseFontSize(r3, r2)     // Catch:{ SubtitleDecoderException -> 0x0171 }
            goto L_0x01cb
        L_0x0171:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r6 = "Failed parsing fontSize value: "
        L_0x0178:
            r4.append(r6)
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            android.util.Log.w(r5, r3)
            goto L_0x01cb
        L_0x0186:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r11.createIfNull(r2)
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r2.setFontFamily(r3)
            goto L_0x01cb
        L_0x018f:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r11.createIfNull(r2)
            int r4 = com.google.android.exoplayer2.util.ColorParser.parseTtmlColor(r3)     // Catch:{ IllegalArgumentException -> 0x019b }
            r2.setFontColor(r4)     // Catch:{ IllegalArgumentException -> 0x019b }
            goto L_0x01cb
        L_0x019b:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r6 = "Failed parsing color value: "
            goto L_0x0178
        L_0x01a3:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r11.createIfNull(r2)
            int r4 = com.google.android.exoplayer2.util.ColorParser.parseTtmlColor(r3)     // Catch:{ IllegalArgumentException -> 0x01af }
            r2.setBackgroundColor(r4)     // Catch:{ IllegalArgumentException -> 0x01af }
            goto L_0x01cb
        L_0x01af:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r6 = "Failed parsing background value: "
            goto L_0x0178
        L_0x01b7:
            java.lang.String r4 = r12.getName()
            java.lang.String r5 = "style"
            boolean r4 = r5.equals(r4)
            if (r4 == 0) goto L_0x01cb
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r11.createIfNull(r2)
            com.google.android.exoplayer2.text.ttml.TtmlStyle r2 = r2.setId(r3)
        L_0x01cb:
            int r13 = r13 + 1
            goto L_0x0007
        L_0x01cf:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.ttml.TtmlDecoder.parseStyleAttributes(org.xmlpull.v1.XmlPullParser, com.google.android.exoplayer2.text.ttml.TtmlStyle):com.google.android.exoplayer2.text.ttml.TtmlStyle");
    }

    private String[] parseStyleIds(String str) {
        String trim = str.trim();
        return trim.isEmpty() ? new String[0] : Util.split(trim, "\\s+");
    }

    /* JADX WARNING: Removed duplicated region for block: B:49:0x00e8  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0105  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static long parseTimeExpression(java.lang.String r14, com.google.android.exoplayer2.text.ttml.TtmlDecoder.FrameAndTickRate r15) {
        /*
            java.util.regex.Pattern r0 = CLOCK_TIME
            java.util.regex.Matcher r0 = r0.matcher(r14)
            boolean r1 = r0.matches()
            r2 = 4696837146684686336(0x412e848000000000, double:1000000.0)
            r4 = 5
            r5 = 4
            r6 = 3
            r7 = 2
            r8 = 1
            if (r1 == 0) goto L_0x0073
            java.lang.String r14 = r0.group(r8)
            long r8 = java.lang.Long.parseLong(r14)
            r10 = 3600(0xe10, double:1.7786E-320)
            long r8 = r8 * r10
            double r8 = (double) r8
            java.lang.String r14 = r0.group(r7)
            long r10 = java.lang.Long.parseLong(r14)
            r12 = 60
            long r10 = r10 * r12
            double r10 = (double) r10
            double r8 = r8 + r10
            java.lang.String r14 = r0.group(r6)
            long r6 = java.lang.Long.parseLong(r14)
            double r6 = (double) r6
            double r8 = r8 + r6
            java.lang.String r14 = r0.group(r5)
            r5 = 0
            if (r14 == 0) goto L_0x0046
            double r10 = java.lang.Double.parseDouble(r14)
            goto L_0x0047
        L_0x0046:
            r10 = r5
        L_0x0047:
            double r8 = r8 + r10
            java.lang.String r14 = r0.group(r4)
            if (r14 == 0) goto L_0x0058
            long r10 = java.lang.Long.parseLong(r14)
            float r14 = (float) r10
            float r1 = r15.effectiveFrameRate
            float r14 = r14 / r1
            double r10 = (double) r14
            goto L_0x0059
        L_0x0058:
            r10 = r5
        L_0x0059:
            double r8 = r8 + r10
            r14 = 6
            java.lang.String r14 = r0.group(r14)
            if (r14 == 0) goto L_0x006f
            long r0 = java.lang.Long.parseLong(r14)
            double r0 = (double) r0
            int r14 = r15.subFrameRate
            double r4 = (double) r14
            double r0 = r0 / r4
            float r14 = r15.effectiveFrameRate
            double r14 = (double) r14
            double r5 = r0 / r14
        L_0x006f:
            double r8 = r8 + r5
            double r8 = r8 * r2
            long r14 = (long) r8
            return r14
        L_0x0073:
            java.util.regex.Pattern r0 = OFFSET_TIME
            java.util.regex.Matcher r0 = r0.matcher(r14)
            boolean r1 = r0.matches()
            if (r1 == 0) goto L_0x010e
            java.lang.String r14 = r0.group(r8)
            double r9 = java.lang.Double.parseDouble(r14)
            java.lang.String r14 = r0.group(r7)
            r0 = -1
            int r1 = r14.hashCode()
            r11 = 102(0x66, float:1.43E-43)
            if (r1 == r11) goto L_0x00db
            r11 = 104(0x68, float:1.46E-43)
            if (r1 == r11) goto L_0x00d1
            r11 = 109(0x6d, float:1.53E-43)
            if (r1 == r11) goto L_0x00c7
            r11 = 3494(0xda6, float:4.896E-42)
            if (r1 == r11) goto L_0x00bd
            r11 = 115(0x73, float:1.61E-43)
            if (r1 == r11) goto L_0x00b3
            r11 = 116(0x74, float:1.63E-43)
            if (r1 == r11) goto L_0x00a9
            goto L_0x00e5
        L_0x00a9:
            java.lang.String r1 = "t"
            boolean r14 = r14.equals(r1)
            if (r14 == 0) goto L_0x00e5
            r14 = r4
            goto L_0x00e6
        L_0x00b3:
            java.lang.String r1 = "s"
            boolean r14 = r14.equals(r1)
            if (r14 == 0) goto L_0x00e5
            r14 = r7
            goto L_0x00e6
        L_0x00bd:
            java.lang.String r1 = "ms"
            boolean r14 = r14.equals(r1)
            if (r14 == 0) goto L_0x00e5
            r14 = r6
            goto L_0x00e6
        L_0x00c7:
            java.lang.String r1 = "m"
            boolean r14 = r14.equals(r1)
            if (r14 == 0) goto L_0x00e5
            r14 = r8
            goto L_0x00e6
        L_0x00d1:
            java.lang.String r1 = "h"
            boolean r14 = r14.equals(r1)
            if (r14 == 0) goto L_0x00e5
            r14 = 0
            goto L_0x00e6
        L_0x00db:
            java.lang.String r1 = "f"
            boolean r14 = r14.equals(r1)
            if (r14 == 0) goto L_0x00e5
            r14 = r5
            goto L_0x00e6
        L_0x00e5:
            r14 = r0
        L_0x00e6:
            if (r14 == 0) goto L_0x0105
            if (r14 == r8) goto L_0x0102
            if (r14 == r7) goto L_0x010b
            if (r14 == r6) goto L_0x00fb
            if (r14 == r5) goto L_0x00f7
            if (r14 == r4) goto L_0x00f3
            goto L_0x010b
        L_0x00f3:
            int r14 = r15.tickRate
            double r14 = (double) r14
            goto L_0x0100
        L_0x00f7:
            float r14 = r15.effectiveFrameRate
            double r14 = (double) r14
            goto L_0x0100
        L_0x00fb:
            r14 = 4652007308841189376(0x408f400000000000, double:1000.0)
        L_0x0100:
            double r9 = r9 / r14
            goto L_0x010b
        L_0x0102:
            r14 = 4633641066610819072(0x404e000000000000, double:60.0)
            goto L_0x010a
        L_0x0105:
            r14 = 4660134898793709568(0x40ac200000000000, double:3600.0)
        L_0x010a:
            double r9 = r9 * r14
        L_0x010b:
            double r9 = r9 * r2
            long r14 = (long) r9
            return r14
        L_0x010e:
            com.google.android.exoplayer2.text.SubtitleDecoderException r15 = new com.google.android.exoplayer2.text.SubtitleDecoderException
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Malformed time expression: "
            r0.append(r1)
            r0.append(r14)
            java.lang.String r14 = r0.toString()
            r15.<init>(r14)
            throw r15
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.ttml.TtmlDecoder.parseTimeExpression(java.lang.String, com.google.android.exoplayer2.text.ttml.TtmlDecoder$FrameAndTickRate):long");
    }

    /* access modifiers changed from: protected */
    public TtmlSubtitle decode(byte[] bArr, int i, boolean z) {
        try {
            XmlPullParser newPullParser = this.xmlParserFactory.newPullParser();
            HashMap hashMap = new HashMap();
            HashMap hashMap2 = new HashMap();
            TtmlSubtitle ttmlSubtitle = null;
            hashMap2.put("", new TtmlRegion((String) null));
            int i2 = 0;
            newPullParser.setInput(new ByteArrayInputStream(bArr, 0, i), (String) null);
            ArrayDeque arrayDeque = new ArrayDeque();
            FrameAndTickRate frameAndTickRate = DEFAULT_FRAME_AND_TICK_RATE;
            CellResolution cellResolution = DEFAULT_CELL_RESOLUTION;
            for (int eventType = newPullParser.getEventType(); eventType != 1; eventType = newPullParser.getEventType()) {
                TtmlNode ttmlNode = (TtmlNode) arrayDeque.peek();
                if (i2 == 0) {
                    String name = newPullParser.getName();
                    if (eventType == 2) {
                        if (TtmlNode.TAG_TT.equals(name)) {
                            frameAndTickRate = parseFrameAndTickRates(newPullParser);
                            cellResolution = parseCellResolution(newPullParser, DEFAULT_CELL_RESOLUTION);
                        }
                        if (!isSupportedTag(name)) {
                            Log.i(TAG, "Ignoring unsupported tag: " + newPullParser.getName());
                        } else if (TtmlNode.TAG_HEAD.equals(name)) {
                            parseHeader(newPullParser, hashMap, hashMap2, cellResolution);
                        } else {
                            try {
                                TtmlNode parseNode = parseNode(newPullParser, ttmlNode, hashMap2, frameAndTickRate);
                                arrayDeque.push(parseNode);
                                if (ttmlNode != null) {
                                    ttmlNode.addChild(parseNode);
                                }
                            } catch (SubtitleDecoderException e) {
                                Log.w(TAG, "Suppressing parser error", e);
                            }
                        }
                    } else if (eventType == 4) {
                        ttmlNode.addChild(TtmlNode.buildTextNode(newPullParser.getText()));
                    } else if (eventType == 3) {
                        if (newPullParser.getName().equals(TtmlNode.TAG_TT)) {
                            ttmlSubtitle = new TtmlSubtitle((TtmlNode) arrayDeque.peek(), hashMap, hashMap2);
                        }
                        arrayDeque.pop();
                    }
                    newPullParser.next();
                } else if (eventType != 2) {
                    if (eventType == 3) {
                        i2--;
                    }
                    newPullParser.next();
                }
                i2++;
                newPullParser.next();
            }
            return ttmlSubtitle;
        } catch (XmlPullParserException e2) {
            throw new SubtitleDecoderException("Unable to decode source", e2);
        } catch (IOException e3) {
            throw new IllegalStateException("Unexpected error when reading input.", e3);
        }
    }
}
