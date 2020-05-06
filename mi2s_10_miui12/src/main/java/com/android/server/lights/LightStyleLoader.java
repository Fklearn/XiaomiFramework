package com.android.server.lights;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseArray;

public class LightStyleLoader {
    private static final String BRIGHTNESS_MODE = "brightnessMode";
    private static final String COLOR_ARGB = "colorARGB";
    private static final boolean DEBUG = false;
    private static final int DEFAULT_INTERVAL = 100;
    private static final String FLASH_MODE = "flashMode";
    private static final String LIGHTSTATE = "lightstate";
    private static final String OFFMS = "offMS";
    private static final String ONMS = "onMS";
    private static final String TAG = "LightsService";
    private SparseArray mStyleArray = new SparseArray();
    private final Resources resources;

    public LightStyleLoader(Context context) {
        this.resources = context.getResources();
        this.mStyleArray.append(0, Integer.valueOf(getIdentifierByReflect("lightstyle_default")));
        this.mStyleArray.append(1, Integer.valueOf(getIdentifierByReflect("lightstyle_phone")));
        this.mStyleArray.append(2, Integer.valueOf(getIdentifierByReflect("lightstyle_game")));
        this.mStyleArray.append(3, Integer.valueOf(getIdentifierByReflect("lightstyle_music")));
        this.mStyleArray.append(4, Integer.valueOf(getIdentifierByReflect("lightstyle_alarm")));
        this.mStyleArray.append(5, Integer.valueOf(getIdentifierByReflect("lightstyle_expand")));
        this.mStyleArray.append(6, Integer.valueOf(getIdentifierByReflect("lightstyle_luckymoney")));
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.android.server.lights.LightState> getLightStyle(int r25) {
        /*
            r24 = this;
            r1 = r24
            r2 = r25
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r3 = r0
            r4 = 0
            if (r2 < 0) goto L_0x018e
            android.util.SparseArray r0 = r1.mStyleArray     // Catch:{ Exception -> 0x0159 }
            java.lang.Object r0 = r0.get(r2)     // Catch:{ Exception -> 0x0159 }
            if (r0 != 0) goto L_0x0017
            goto L_0x018e
        L_0x0017:
            javax.xml.parsers.DocumentBuilderFactory r0 = javax.xml.parsers.DocumentBuilderFactory.newInstance()     // Catch:{ Exception -> 0x0159 }
            javax.xml.parsers.DocumentBuilder r5 = r0.newDocumentBuilder()     // Catch:{ Exception -> 0x0159 }
            java.io.BufferedInputStream r6 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x0159 }
            android.content.res.Resources r7 = r1.resources     // Catch:{ Exception -> 0x0159 }
            android.util.SparseArray r8 = r1.mStyleArray     // Catch:{ Exception -> 0x0159 }
            java.lang.Object r8 = r8.get(r2)     // Catch:{ Exception -> 0x0159 }
            java.lang.Integer r8 = (java.lang.Integer) r8     // Catch:{ Exception -> 0x0159 }
            int r8 = r8.intValue()     // Catch:{ Exception -> 0x0159 }
            java.io.InputStream r7 = r7.openRawResource(r8)     // Catch:{ Exception -> 0x0159 }
            r8 = 4096(0x1000, float:5.74E-42)
            r6.<init>(r7, r8)     // Catch:{ Exception -> 0x0159 }
            r4 = r6
            org.w3c.dom.Document r6 = r5.parse(r4)     // Catch:{ Exception -> 0x0159 }
            java.lang.String r7 = "lightstate"
            org.w3c.dom.NodeList r7 = r6.getElementsByTagName(r7)     // Catch:{ Exception -> 0x0159 }
            r8 = -1
            r9 = 1
            r10 = 100
            r11 = 100
            r12 = 0
            r13 = 0
            r14 = r13
        L_0x004d:
            int r15 = r7.getLength()     // Catch:{ Exception -> 0x0159 }
            if (r14 >= r15) goto L_0x0152
            org.w3c.dom.Node r15 = r7.item(r14)     // Catch:{ Exception -> 0x0159 }
            org.w3c.dom.NodeList r15 = r15.getChildNodes()     // Catch:{ Exception -> 0x0159 }
            r16 = r13
            r22 = r12
            r12 = r10
            r10 = r9
            r9 = r8
            r8 = r16
        L_0x0064:
            int r13 = r15.getLength()     // Catch:{ Exception -> 0x0159 }
            if (r8 >= r13) goto L_0x012f
            org.w3c.dom.Node r13 = r15.item(r8)     // Catch:{ Exception -> 0x0159 }
            short r13 = r13.getNodeType()     // Catch:{ Exception -> 0x0159 }
            r23 = r0
            r0 = 1
            if (r13 != r0) goto L_0x0128
            org.w3c.dom.Node r13 = r15.item(r8)     // Catch:{ Exception -> 0x0159 }
            java.lang.String r13 = r13.getNodeName()     // Catch:{ Exception -> 0x0159 }
            r16 = -1
            int r17 = r13.hashCode()     // Catch:{ Exception -> 0x0159 }
            switch(r17) {
                case -1147460173: goto L_0x00b3;
                case 3414981: goto L_0x00a8;
                case 105650005: goto L_0x009d;
                case 1980337839: goto L_0x0093;
                case 1984317844: goto L_0x0089;
                default: goto L_0x0088;
            }     // Catch:{ Exception -> 0x0159 }
        L_0x0088:
            goto L_0x00bd
        L_0x0089:
            java.lang.String r0 = "brightnessMode"
            boolean r0 = r13.equals(r0)     // Catch:{ Exception -> 0x0159 }
            if (r0 == 0) goto L_0x0088
            r0 = 4
            goto L_0x00bf
        L_0x0093:
            java.lang.String r0 = "colorARGB"
            boolean r0 = r13.equals(r0)     // Catch:{ Exception -> 0x0159 }
            if (r0 == 0) goto L_0x0088
            r0 = 0
            goto L_0x00bf
        L_0x009d:
            java.lang.String r0 = "offMS"
            boolean r0 = r13.equals(r0)     // Catch:{ Exception -> 0x0159 }
            if (r0 == 0) goto L_0x0088
            r0 = 2
            goto L_0x00bf
        L_0x00a8:
            java.lang.String r0 = "onMS"
            boolean r0 = r13.equals(r0)     // Catch:{ Exception -> 0x0159 }
            if (r0 == 0) goto L_0x0088
            r0 = 3
            goto L_0x00bf
        L_0x00b3:
            java.lang.String r0 = "flashMode"
            boolean r0 = r13.equals(r0)     // Catch:{ Exception -> 0x0159 }
            if (r0 == 0) goto L_0x0088
            r0 = 1
            goto L_0x00bf
        L_0x00bd:
            r0 = r16
        L_0x00bf:
            if (r0 == 0) goto L_0x0117
            r13 = 1
            if (r0 == r13) goto L_0x0105
            r13 = 2
            if (r0 == r13) goto L_0x00f3
            r13 = 3
            if (r0 == r13) goto L_0x00e1
            r13 = 4
            if (r0 == r13) goto L_0x00ce
            goto L_0x0128
        L_0x00ce:
            org.w3c.dom.Node r0 = r15.item(r8)     // Catch:{ Exception -> 0x0159 }
            org.w3c.dom.Node r0 = r0.getFirstChild()     // Catch:{ Exception -> 0x0159 }
            java.lang.String r0 = r0.getNodeValue()     // Catch:{ Exception -> 0x0159 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0159 }
            r22 = r0
            goto L_0x0128
        L_0x00e1:
            org.w3c.dom.Node r0 = r15.item(r8)     // Catch:{ Exception -> 0x0159 }
            org.w3c.dom.Node r0 = r0.getFirstChild()     // Catch:{ Exception -> 0x0159 }
            java.lang.String r0 = r0.getNodeValue()     // Catch:{ Exception -> 0x0159 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0159 }
            r11 = r0
            goto L_0x0128
        L_0x00f3:
            org.w3c.dom.Node r0 = r15.item(r8)     // Catch:{ Exception -> 0x0159 }
            org.w3c.dom.Node r0 = r0.getFirstChild()     // Catch:{ Exception -> 0x0159 }
            java.lang.String r0 = r0.getNodeValue()     // Catch:{ Exception -> 0x0159 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0159 }
            r12 = r0
            goto L_0x0128
        L_0x0105:
            org.w3c.dom.Node r0 = r15.item(r8)     // Catch:{ Exception -> 0x0159 }
            org.w3c.dom.Node r0 = r0.getFirstChild()     // Catch:{ Exception -> 0x0159 }
            java.lang.String r0 = r0.getNodeValue()     // Catch:{ Exception -> 0x0159 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0159 }
            r10 = r0
            goto L_0x0128
        L_0x0117:
            org.w3c.dom.Node r0 = r15.item(r8)     // Catch:{ Exception -> 0x0159 }
            org.w3c.dom.Node r0 = r0.getFirstChild()     // Catch:{ Exception -> 0x0159 }
            java.lang.String r0 = r0.getNodeValue()     // Catch:{ Exception -> 0x0159 }
            int r0 = android.graphics.Color.parseColor(r0)     // Catch:{ Exception -> 0x0159 }
            r9 = r0
        L_0x0128:
            int r8 = r8 + 1
            r0 = r23
            r13 = 0
            goto L_0x0064
        L_0x012f:
            r23 = r0
            com.android.server.lights.LightState r0 = new com.android.server.lights.LightState     // Catch:{ Exception -> 0x0159 }
            r16 = r0
            r17 = r9
            r18 = r10
            r19 = r11
            r20 = r12
            r21 = r22
            r16.<init>(r17, r18, r19, r20, r21)     // Catch:{ Exception -> 0x0159 }
            r3.add(r0)     // Catch:{ Exception -> 0x0159 }
            int r14 = r14 + 1
            r8 = r9
            r9 = r10
            r10 = r12
            r12 = r22
            r0 = r23
            r13 = 0
            goto L_0x004d
        L_0x0152:
            libcore.io.IoUtils.closeQuietly(r4)
            return r3
        L_0x0157:
            r0 = move-exception
            goto L_0x018a
        L_0x0159:
            r0 = move-exception
            java.lang.String r5 = "LightsService"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0157 }
            r6.<init>()     // Catch:{ all -> 0x0157 }
            java.lang.String r7 = "error style : "
            r6.append(r7)     // Catch:{ all -> 0x0157 }
            android.util.SparseArray r7 = r1.mStyleArray     // Catch:{ all -> 0x0157 }
            java.lang.Object r7 = r7.get(r2)     // Catch:{ all -> 0x0157 }
            r6.append(r7)     // Catch:{ all -> 0x0157 }
            java.lang.String r7 = " -- "
            r6.append(r7)     // Catch:{ all -> 0x0157 }
            java.lang.String r7 = r0.toString()     // Catch:{ all -> 0x0157 }
            r6.append(r7)     // Catch:{ all -> 0x0157 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0157 }
            android.util.Slog.e(r5, r6)     // Catch:{ all -> 0x0157 }
            r0.printStackTrace()     // Catch:{ all -> 0x0157 }
            libcore.io.IoUtils.closeQuietly(r4)
            return r3
        L_0x018a:
            libcore.io.IoUtils.closeQuietly(r4)
            throw r0
        L_0x018e:
            libcore.io.IoUtils.closeQuietly(r4)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.lights.LightStyleLoader.getLightStyle(int):java.util.List");
    }

    private static int getIdentifierByReflect(String src_name) {
        try {
            return Class.forName("android.miui.R$raw").getDeclaredField(src_name).getInt((Object) null);
        } catch (Exception e) {
            e.printStackTrace();
            return -100;
        }
    }
}
