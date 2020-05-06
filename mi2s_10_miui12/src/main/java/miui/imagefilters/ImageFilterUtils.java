package miui.imagefilters;

import com.android.server.wifi.hotspot2.anqp.Constants;

public class ImageFilterUtils {
    static final float COLOR_TO_GRAYSCALE_FACTOR_B = 0.114f;
    static final float COLOR_TO_GRAYSCALE_FACTOR_G = 0.587f;
    static final float COLOR_TO_GRAYSCALE_FACTOR_R = 0.299f;
    static final String TAG = "ImageFilterUtils";

    public static int HslToRgb(float[] hsl) {
        return HslToRgb(hsl[0], hsl[1], hsl[2]);
    }

    public static int HslToRgb(float h, float s, float l) {
        int g;
        int b;
        int r;
        if (s == 0.0f) {
            r = (int) (255.0f * l);
            b = r;
            g = r;
        } else {
            float q = l < 0.5f ? (s + 1.0f) * l : (l + s) - (l * s);
            float p = (2.0f * l) - q;
            float Hk = h / 360.0f;
            float[] T = {Hk + 0.33333334f, Hk, Hk - 0.33333334f};
            for (int i = 0; i < 3; i++) {
                if (T[i] < 0.0f) {
                    T[i] = (float) (((double) T[i]) + 1.0d);
                } else if (T[i] > 1.0f) {
                    T[i] = (float) (((double) T[i]) - 1.0d);
                }
                if (T[i] * 6.0f < 1.0f) {
                    T[i] = ((q - p) * 6.0f * T[i]) + p;
                } else if (((double) T[i]) * 2.0d < 1.0d) {
                    T[i] = q;
                } else if (((double) T[i]) * 3.0d < 2.0d) {
                    T[i] = ((q - p) * (0.6666667f - T[i]) * 6.0f) + p;
                } else {
                    T[i] = p;
                }
            }
            r = (int) (((double) T[0]) * 255.0d);
            g = (int) (((double) T[1]) * 255.0d);
            b = (int) (((double) T[2]) * 255.0d);
        }
        return -16777216 | (clamp(0, r, (int) Constants.BYTE_MASK) << 16) | (clamp(0, g, (int) Constants.BYTE_MASK) << 8) | clamp(0, b, (int) Constants.BYTE_MASK);
    }

    public static int HsvToRgb(float h, float s, float v) {
        float r = 0.0f;
        float g = 0.0f;
        float b = 0.0f;
        if (s == 0.0f) {
            b = v;
            g = v;
            r = v;
        } else {
            float sectorPos = h / 60.0f;
            int sectorNumber = (int) Math.floor((double) sectorPos);
            float fractionalSector = sectorPos - ((float) sectorNumber);
            float p = (1.0f - s) * v;
            float q = (1.0f - (s * fractionalSector)) * v;
            float t = (1.0f - ((1.0f - fractionalSector) * s)) * v;
            if (sectorNumber == 0) {
                r = v;
                g = t;
                b = p;
            } else if (sectorNumber == 1) {
                r = q;
                g = v;
                b = p;
            } else if (sectorNumber == 2) {
                r = p;
                g = v;
                b = t;
            } else if (sectorNumber == 3) {
                r = p;
                g = q;
                b = v;
            } else if (sectorNumber == 4) {
                r = t;
                g = p;
                b = v;
            } else if (sectorNumber == 5) {
                r = v;
                g = p;
                b = q;
            }
        }
        return -16777216 | (((int) (clamp(0.0f, r, 1.0f) * 255.0f)) << 16) | (((int) (clamp(0.0f, g, 1.0f) * 255.0f)) << 8) | ((int) (255.0f * clamp(0.0f, b, 1.0f)));
    }

    public static void RgbToHsl(int color, float[] hsl) {
        RgbToHsl((color >>> 16) & Constants.BYTE_MASK, (color >>> 8) & Constants.BYTE_MASK, color & Constants.BYTE_MASK, hsl);
    }

    public static void RgbToHsl(int red, int green, int blue, float[] hsl) {
        float h = 0.0f;
        float s = 0.0f;
        float r = ((float) red) / 255.0f;
        float g = ((float) green) / 255.0f;
        float b = ((float) blue) / 255.0f;
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        if (max == min) {
            h = 0.0f;
        } else if (max == r && g >= b) {
            h = ((g - b) * 60.0f) / (max - min);
        } else if (max == r && g < b) {
            h = (((g - b) * 60.0f) / (max - min)) + 360.0f;
        } else if (max == g) {
            h = (((b - r) * 60.0f) / (max - min)) + 120.0f;
        } else if (max == b) {
            h = (((r - g) * 60.0f) / (max - min)) + 240.0f;
        }
        float l = (max + min) / 2.0f;
        if (l == 0.0f || max == min) {
            s = 0.0f;
        } else if (0.0f < l && ((double) l) <= 0.5d) {
            s = (max - min) / (max + min);
        } else if (((double) l) > 0.5d) {
            s = (max - min) / (2.0f - (max + min));
        }
        hsl[0] = clamp(0.0f, h, 360.0f);
        hsl[1] = clamp(0.0f, s, 1.0f);
        hsl[2] = clamp(0.0f, l, 1.0f);
    }

    public static void RgbToHsv(int color, float[] hsv) {
        RgbToHsv((color >>> 16) & Constants.BYTE_MASK, (color >>> 8) & Constants.BYTE_MASK, color & Constants.BYTE_MASK, hsv);
    }

    public static void RgbToHsv(int red, int green, int blue, float[] hsv) {
        float s;
        float h = 0.0f;
        float r = ((float) red) / 255.0f;
        float g = ((float) green) / 255.0f;
        float b = ((float) blue) / 255.0f;
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        if (max == min) {
            h = 0.0f;
        } else if (max == r && g >= b) {
            h = ((g - b) * 60.0f) / (max - min);
        } else if (max == r && g < b) {
            h = (((g - b) * 60.0f) / (max - min)) + 360.0f;
        } else if (max == g) {
            h = (((b - r) * 60.0f) / (max - min)) + 120.0f;
        } else if (max == b) {
            h = (((r - g) * 60.0f) / (max - min)) + 240.0f;
        }
        float v = max;
        if (max == 0.0f) {
            s = 0.0f;
        } else {
            s = (max - min) / max;
        }
        hsv[0] = clamp(0.0f, h, 360.0f);
        hsv[1] = clamp(0.0f, s, 1.0f);
        hsv[2] = clamp(0.0f, v, 1.0f);
    }

    public static void interpolate(float[] hsl1, float[] hsl2, float amount, float[] hslOut) {
        int size = Math.min(hsl1.length, hsl2.length);
        for (int i = 0; i < size; i++) {
            hslOut[i] = hsl1[i] + ((hsl2[i] - hsl1[i]) * amount);
        }
    }

    public static int interpolate(int inMin, int inMax, int outMin, int outMax, int value) {
        return (int) (((float) outMin) + ((((float) value) * ((float) (outMax - outMin))) / ((float) (inMax - inMin))));
    }

    public static int clamp(int min, int value, int max) {
        if (value <= min) {
            return min;
        }
        if (value >= max) {
            return max;
        }
        return value;
    }

    public static float clamp(float min, float value, float max) {
        if (value <= min) {
            return min;
        }
        if (value >= max) {
            return max;
        }
        return value;
    }

    public static int convertColorToGrayscale(int color) {
        return (int) ((((float) ((16711680 & color) >>> 16)) * COLOR_TO_GRAYSCALE_FACTOR_R) + (((float) ((65280 & color) >>> 8)) * COLOR_TO_GRAYSCALE_FACTOR_G) + (((float) (color & Constants.BYTE_MASK)) * COLOR_TO_GRAYSCALE_FACTOR_B));
    }

    public static void checkChannelParam(String paramString, boolean[] outRgba) {
        if (paramString.equalsIgnoreCase("r") || paramString.equalsIgnoreCase("red")) {
            outRgba[0] = true;
        } else if (paramString.equalsIgnoreCase("g") || paramString.equalsIgnoreCase("green")) {
            outRgba[1] = true;
        } else if (paramString.equalsIgnoreCase("b") || paramString.equalsIgnoreCase("blue")) {
            outRgba[2] = true;
        } else if (!paramString.equalsIgnoreCase("a") && !paramString.equalsIgnoreCase("alpha")) {
            for (int i = 0; i < outRgba.length; i++) {
                outRgba[i] = true;
            }
        } else if (outRgba.length >= 4) {
            outRgba[3] = true;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v0, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v1, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v3, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v4, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v5, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v6, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v7, resolved type: java.lang.Enum} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v8, resolved type: android.graphics.Bitmap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v9, resolved type: java.lang.Boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v10, resolved type: java.lang.Double} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v11, resolved type: java.lang.Float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v12, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v13, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean setProperty(java.lang.Object r21, java.lang.String r22, java.lang.Object r23) {
        /*
            r1 = r21
            r2 = r22
            r3 = r23
            java.lang.String r4 = ",property:"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "set"
            r0.append(r5)
            r0.append(r2)
            java.lang.String r5 = r0.toString()
            r0 = 0
            java.lang.Class r6 = r21.getClass()
            java.lang.reflect.Method[] r6 = r6.getMethods()
            int r7 = r6.length
            r8 = 0
            r9 = r8
        L_0x0025:
            r10 = 1
            if (r9 >= r7) goto L_0x0041
            r11 = r6[r9]
            java.lang.String r12 = r11.getName()
            boolean r12 = r5.equalsIgnoreCase(r12)
            if (r12 == 0) goto L_0x003e
            java.lang.Class[] r12 = r11.getParameterTypes()
            int r12 = r12.length
            if (r12 != r10) goto L_0x003e
            r0 = r11
            r7 = r0
            goto L_0x0042
        L_0x003e:
            int r9 = r9 + 1
            goto L_0x0025
        L_0x0041:
            r7 = r0
        L_0x0042:
            java.lang.String r0 = ",obj:"
            java.lang.String r9 = "ImageFilterUtils"
            if (r7 != 0) goto L_0x0063
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r10 = "unknown property:"
            r4.append(r10)
            r4.append(r2)
            r4.append(r0)
            r4.append(r1)
            java.lang.String r0 = r4.toString()
            android.util.Log.w(r9, r0)
            return r8
        L_0x0063:
            java.lang.Class<miui.imagefilters.FilterParamType> r11 = miui.imagefilters.FilterParamType.class
            java.lang.annotation.Annotation r11 = r7.getAnnotation(r11)
            miui.imagefilters.FilterParamType r11 = (miui.imagefilters.FilterParamType) r11
            if (r11 != 0) goto L_0x0070
            miui.imagefilters.FilterParamType$ParamType r12 = miui.imagefilters.FilterParamType.ParamType.DEFAULT
            goto L_0x0074
        L_0x0070:
            miui.imagefilters.FilterParamType$ParamType r12 = r11.value()
        L_0x0074:
            java.lang.Class[] r13 = r7.getParameterTypes()
            r13 = r13[r8]
            r14 = 0
            boolean r15 = r3 instanceof java.lang.String     // Catch:{ Exception -> 0x025d }
            if (r15 == 0) goto L_0x0241
            r15 = r3
            java.lang.String r15 = (java.lang.String) r15     // Catch:{ Exception -> 0x025d }
            java.lang.Class<java.lang.String> r10 = java.lang.String.class
            boolean r10 = r10.equals(r13)     // Catch:{ Exception -> 0x025d }
            if (r10 == 0) goto L_0x0094
            r0 = r15
            r14 = r0
            r18 = r5
            r17 = r6
            r19 = r11
            goto L_0x024e
        L_0x0094:
            java.lang.Class r10 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x025d }
            boolean r10 = r10.equals(r13)     // Catch:{ Exception -> 0x025d }
            if (r10 == 0) goto L_0x00c0
            int r0 = java.lang.Integer.parseInt(r15)     // Catch:{ Exception -> 0x00b7 }
            miui.imagefilters.FilterParamType$ParamType r10 = miui.imagefilters.FilterParamType.ParamType.ICON_SIZE     // Catch:{ Exception -> 0x00b7 }
            if (r12 != r10) goto L_0x00a9
            int r10 = miui.content.res.IconCustomizer.hdpiIconSizeToCurrent((int) r0)     // Catch:{ Exception -> 0x00b7 }
            r0 = r10
        L_0x00a9:
            java.lang.Integer r10 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x00b7 }
            r0 = r10
            r14 = r0
            r18 = r5
            r17 = r6
            r19 = r11
            goto L_0x024e
        L_0x00b7:
            r0 = move-exception
            r18 = r5
            r17 = r6
            r19 = r11
            goto L_0x0264
        L_0x00c0:
            java.lang.Class r10 = java.lang.Float.TYPE     // Catch:{ Exception -> 0x025d }
            boolean r10 = r10.equals(r13)     // Catch:{ Exception -> 0x025d }
            if (r10 == 0) goto L_0x00e3
            float r0 = java.lang.Float.parseFloat(r15)     // Catch:{ Exception -> 0x00b7 }
            miui.imagefilters.FilterParamType$ParamType r10 = miui.imagefilters.FilterParamType.ParamType.ICON_SIZE     // Catch:{ Exception -> 0x00b7 }
            if (r12 != r10) goto L_0x00d5
            float r10 = miui.content.res.IconCustomizer.hdpiIconSizeToCurrent((float) r0)     // Catch:{ Exception -> 0x00b7 }
            r0 = r10
        L_0x00d5:
            java.lang.Float r10 = java.lang.Float.valueOf(r0)     // Catch:{ Exception -> 0x00b7 }
            r0 = r10
            r14 = r0
            r18 = r5
            r17 = r6
            r19 = r11
            goto L_0x024e
        L_0x00e3:
            java.lang.Class r10 = java.lang.Double.TYPE     // Catch:{ Exception -> 0x025d }
            boolean r10 = r10.equals(r13)     // Catch:{ Exception -> 0x025d }
            if (r10 == 0) goto L_0x0106
            double r16 = java.lang.Double.parseDouble(r15)     // Catch:{ Exception -> 0x00b7 }
            miui.imagefilters.FilterParamType$ParamType r0 = miui.imagefilters.FilterParamType.ParamType.ICON_SIZE     // Catch:{ Exception -> 0x00b7 }
            if (r12 != r0) goto L_0x00f9
            double r18 = miui.content.res.IconCustomizer.hdpiIconSizeToCurrent((double) r16)     // Catch:{ Exception -> 0x00b7 }
            r16 = r18
        L_0x00f9:
            java.lang.Double r0 = java.lang.Double.valueOf(r16)     // Catch:{ Exception -> 0x00b7 }
            r14 = r0
            r18 = r5
            r17 = r6
            r19 = r11
            goto L_0x024e
        L_0x0106:
            java.lang.Class r10 = java.lang.Boolean.TYPE     // Catch:{ Exception -> 0x025d }
            boolean r10 = r10.equals(r13)     // Catch:{ Exception -> 0x025d }
            if (r10 == 0) goto L_0x011f
            boolean r0 = java.lang.Boolean.parseBoolean(r15)     // Catch:{ Exception -> 0x00b7 }
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)     // Catch:{ Exception -> 0x00b7 }
            r14 = r0
            r18 = r5
            r17 = r6
            r19 = r11
            goto L_0x024e
        L_0x011f:
            java.lang.Class<android.graphics.Bitmap> r10 = android.graphics.Bitmap.class
            boolean r10 = r10.equals(r13)     // Catch:{ Exception -> 0x025d }
            if (r10 == 0) goto L_0x0134
            android.graphics.Bitmap r0 = miui.content.res.IconCustomizer.getRawIcon(r15)     // Catch:{ Exception -> 0x00b7 }
            r14 = r0
            r18 = r5
            r17 = r6
            r19 = r11
            goto L_0x024e
        L_0x0134:
            boolean r10 = r13.isEnum()     // Catch:{ Exception -> 0x025d }
            if (r10 == 0) goto L_0x0147
            java.lang.Enum r0 = java.lang.Enum.valueOf(r13, r15)     // Catch:{ Exception -> 0x00b7 }
            r14 = r0
            r18 = r5
            r17 = r6
            r19 = r11
            goto L_0x024e
        L_0x0147:
            boolean r10 = r13.isArray()     // Catch:{ Exception -> 0x025d }
            if (r10 == 0) goto L_0x0215
            java.lang.Class r0 = r13.getComponentType()     // Catch:{ Exception -> 0x025d }
            java.lang.String r10 = ","
            java.lang.String[] r10 = r15.split(r10)     // Catch:{ Exception -> 0x025d }
            java.lang.Class r8 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x025d }
            boolean r8 = r8.equals(r0)     // Catch:{ Exception -> 0x025d }
            if (r8 == 0) goto L_0x019a
            int r8 = r10.length     // Catch:{ Exception -> 0x0191 }
            int[] r8 = new int[r8]     // Catch:{ Exception -> 0x0191 }
            r16 = 0
            r17 = r16
            r18 = r5
            r5 = r17
        L_0x016a:
            r17 = r6
            int r6 = r8.length     // Catch:{ Exception -> 0x01cd }
            if (r5 >= r6) goto L_0x018c
            r6 = r10[r5]     // Catch:{ Exception -> 0x01cd }
            java.lang.String r6 = r6.trim()     // Catch:{ Exception -> 0x01cd }
            int r6 = java.lang.Integer.parseInt(r6)     // Catch:{ Exception -> 0x01cd }
            r8[r5] = r6     // Catch:{ Exception -> 0x01cd }
            miui.imagefilters.FilterParamType$ParamType r6 = miui.imagefilters.FilterParamType.ParamType.ICON_SIZE     // Catch:{ Exception -> 0x01cd }
            if (r12 != r6) goto L_0x0187
            r6 = r8[r5]     // Catch:{ Exception -> 0x01cd }
            int r6 = miui.content.res.IconCustomizer.hdpiIconSizeToCurrent((int) r6)     // Catch:{ Exception -> 0x01cd }
            r8[r5] = r6     // Catch:{ Exception -> 0x01cd }
        L_0x0187:
            int r5 = r5 + 1
            r6 = r17
            goto L_0x016a
        L_0x018c:
            r14 = r8
            r19 = r11
            goto L_0x0210
        L_0x0191:
            r0 = move-exception
            r18 = r5
            r17 = r6
            r19 = r11
            goto L_0x0264
        L_0x019a:
            r18 = r5
            r17 = r6
            java.lang.Class r5 = java.lang.Float.TYPE     // Catch:{ Exception -> 0x0211 }
            boolean r5 = r5.equals(r0)     // Catch:{ Exception -> 0x0211 }
            if (r5 == 0) goto L_0x01d2
            int r5 = r10.length     // Catch:{ Exception -> 0x01cd }
            float[] r5 = new float[r5]     // Catch:{ Exception -> 0x01cd }
            r6 = 0
            r8 = r6
        L_0x01ab:
            int r6 = r5.length     // Catch:{ Exception -> 0x01cd }
            if (r8 >= r6) goto L_0x01c9
            r6 = r10[r8]     // Catch:{ Exception -> 0x01cd }
            java.lang.String r6 = r6.trim()     // Catch:{ Exception -> 0x01cd }
            float r6 = java.lang.Float.parseFloat(r6)     // Catch:{ Exception -> 0x01cd }
            r5[r8] = r6     // Catch:{ Exception -> 0x01cd }
            miui.imagefilters.FilterParamType$ParamType r6 = miui.imagefilters.FilterParamType.ParamType.ICON_SIZE     // Catch:{ Exception -> 0x01cd }
            if (r12 != r6) goto L_0x01c6
            r6 = r5[r8]     // Catch:{ Exception -> 0x01cd }
            float r6 = miui.content.res.IconCustomizer.hdpiIconSizeToCurrent((float) r6)     // Catch:{ Exception -> 0x01cd }
            r5[r8] = r6     // Catch:{ Exception -> 0x01cd }
        L_0x01c6:
            int r8 = r8 + 1
            goto L_0x01ab
        L_0x01c9:
            r14 = r5
            r19 = r11
            goto L_0x0210
        L_0x01cd:
            r0 = move-exception
            r19 = r11
            goto L_0x0264
        L_0x01d2:
            java.lang.Class r5 = java.lang.Double.TYPE     // Catch:{ Exception -> 0x0211 }
            boolean r5 = r5.equals(r0)     // Catch:{ Exception -> 0x0211 }
            if (r5 == 0) goto L_0x020c
            int r5 = r10.length     // Catch:{ Exception -> 0x0211 }
            double[] r5 = new double[r5]     // Catch:{ Exception -> 0x0211 }
            r6 = 0
            r8 = r6
        L_0x01df:
            int r6 = r5.length     // Catch:{ Exception -> 0x0211 }
            if (r8 >= r6) goto L_0x0206
            r6 = r10[r8]     // Catch:{ Exception -> 0x0211 }
            java.lang.String r6 = r6.trim()     // Catch:{ Exception -> 0x0211 }
            float r6 = java.lang.Float.parseFloat(r6)     // Catch:{ Exception -> 0x0211 }
            r20 = r10
            r19 = r11
            double r10 = (double) r6
            r5[r8] = r10     // Catch:{ Exception -> 0x025b }
            miui.imagefilters.FilterParamType$ParamType r6 = miui.imagefilters.FilterParamType.ParamType.ICON_SIZE     // Catch:{ Exception -> 0x025b }
            if (r12 != r6) goto L_0x01ff
            r10 = r5[r8]     // Catch:{ Exception -> 0x025b }
            double r10 = miui.content.res.IconCustomizer.hdpiIconSizeToCurrent((double) r10)     // Catch:{ Exception -> 0x025b }
            r5[r8] = r10     // Catch:{ Exception -> 0x025b }
        L_0x01ff:
            int r8 = r8 + 1
            r11 = r19
            r10 = r20
            goto L_0x01df
        L_0x0206:
            r20 = r10
            r19 = r11
            r14 = r5
            goto L_0x0210
        L_0x020c:
            r20 = r10
            r19 = r11
        L_0x0210:
            goto L_0x024e
        L_0x0211:
            r0 = move-exception
            r19 = r11
            goto L_0x0264
        L_0x0215:
            r18 = r5
            r17 = r6
            r19 = r11
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x025b }
            r5.<init>()     // Catch:{ Exception -> 0x025b }
            java.lang.String r6 = "unknown param type:"
            r5.append(r6)     // Catch:{ Exception -> 0x025b }
            java.lang.String r6 = r13.getName()     // Catch:{ Exception -> 0x025b }
            r5.append(r6)     // Catch:{ Exception -> 0x025b }
            r5.append(r0)     // Catch:{ Exception -> 0x025b }
            r5.append(r1)     // Catch:{ Exception -> 0x025b }
            r5.append(r4)     // Catch:{ Exception -> 0x025b }
            r5.append(r2)     // Catch:{ Exception -> 0x025b }
            java.lang.String r0 = r5.toString()     // Catch:{ Exception -> 0x025b }
            android.util.Log.w(r9, r0)     // Catch:{ Exception -> 0x025b }
            r4 = 0
            return r4
        L_0x0241:
            r18 = r5
            r17 = r6
            r19 = r11
            boolean r0 = r3 instanceof miui.imagefilters.IImageFilter.ImageFilterGroup     // Catch:{ Exception -> 0x025b }
            if (r0 == 0) goto L_0x024e
            r14 = r23
            goto L_0x024f
        L_0x024e:
        L_0x024f:
            r0 = 1
            java.lang.Object[] r5 = new java.lang.Object[r0]     // Catch:{ Exception -> 0x025b }
            r6 = 0
            r5[r6] = r14     // Catch:{ Exception -> 0x025b }
            r7.invoke(r1, r5)     // Catch:{ Exception -> 0x025b }
            r0 = 1
            return r0
        L_0x025b:
            r0 = move-exception
            goto L_0x0264
        L_0x025d:
            r0 = move-exception
            r18 = r5
            r17 = r6
            r19 = r11
        L_0x0264:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "set property fail. obj:"
            r5.append(r6)
            r5.append(r1)
            r5.append(r4)
            r5.append(r2)
            java.lang.String r4 = ",value:"
            r5.append(r4)
            r5.append(r3)
            java.lang.String r4 = r5.toString()
            android.util.Log.e(r9, r4, r0)
            r4 = 0
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.imagefilters.ImageFilterUtils.setProperty(java.lang.Object, java.lang.String, java.lang.Object):boolean");
    }
}
