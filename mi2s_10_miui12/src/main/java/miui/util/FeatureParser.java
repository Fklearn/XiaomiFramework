package miui.util;

import java.util.ArrayList;
import java.util.HashMap;
import miui.os.Build;
import miui.os.SystemProperties;

public class FeatureParser {
    private static final String ASSET_DIR = "device_features/";
    private static final String SYSTEM_DIR = "/system/etc/device_features";
    private static final String TAG = "FeatureParser";
    private static final String TAG_BOOL = "bool";
    private static final String TAG_FLOAT = "float";
    private static final String TAG_INTEGER = "integer";
    private static final String TAG_INTEGER_ARRAY = "integer-array";
    private static final String TAG_ITEM = "item";
    private static final String TAG_STRING = "string";
    private static final String TAG_STRING_ARRAY = "string-array";
    public static final int TYPE_BOOL = 1;
    public static final int TYPE_FLOAT = 6;
    public static final int TYPE_INTEGER = 2;
    public static final int TYPE_INTEGER_ARRAY = 5;
    public static final int TYPE_STRING = 3;
    public static final int TYPE_STRING_ARRAY = 4;
    private static final String VENDOR_DIR = "/vendor/etc/device_features";
    private static HashMap<String, Boolean> sBooleanMap = new HashMap<>();
    private static HashMap<String, Float> sFloatMap = new HashMap<>();
    private static HashMap<String, ArrayList<Integer>> sIntArrMap = new HashMap<>();
    private static HashMap<String, Integer> sIntMap = new HashMap<>();
    private static HashMap<String, ArrayList<String>> sStrArrMap = new HashMap<>();
    private static HashMap<String, String> sStrMap = new HashMap<>();

    static {
        read();
    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        Boolean value = sBooleanMap.get(name);
        if (value != null) {
            return value.booleanValue();
        }
        return defaultValue;
    }

    public static String getString(String name) {
        return sStrMap.get(name);
    }

    public static String getDeviceFeaturesDir() {
        if (SystemProperties.getInt("ro.product.first_api_level", 0) < 29 || "phoenix".equals(Build.DEVICE) || "phoenixin".equals(Build.DEVICE)) {
            return SYSTEM_DIR;
        }
        return VENDOR_DIR;
    }

    public static int getInteger(String name, int defaultValue) {
        Integer value = sIntMap.get(name);
        if (value != null) {
            return value.intValue();
        }
        return defaultValue;
    }

    public static int[] getIntArray(String name) {
        ArrayList<Integer> intList = sIntArrMap.get(name);
        if (intList == null) {
            return null;
        }
        int length = intList.size();
        int[] intArr = new int[length];
        for (int i = 0; i < length; i++) {
            intArr[i] = intList.get(i).intValue();
        }
        return intArr;
    }

    public static String[] getStringArray(String name) {
        ArrayList<String> strList = sStrArrMap.get(name);
        if (strList != null) {
            return (String[]) strList.toArray(new String[0]);
        }
        return null;
    }

    public static Float getFloat(String name, float defaultValue) {
        Float value = sFloatMap.get(name);
        return Float.valueOf(value != null ? value.floatValue() : defaultValue);
    }

    public static boolean hasFeature(String name, int type) {
        switch (type) {
            case 1:
                return sBooleanMap.containsKey(name);
            case 2:
                return sIntMap.containsKey(name);
            case 3:
                return sStrMap.containsKey(name);
            case 4:
                return sStrArrMap.containsKey(name);
            case 5:
                return sIntArrMap.containsKey(name);
            case 6:
                return sFloatMap.containsKey(name);
            default:
                return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        android.util.Log.i(TAG, "can't find " + r3 + " in assets/" + ASSET_DIR + ",it may be in " + getDeviceFeaturesDir());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x01bc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x01bd, code lost:
        if (r2 != null) goto L_0x01bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x01c6, code lost:
        if (r2 != null) goto L_0x01c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01bc A[ExcHandler: all (r0v3 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r2 
      PHI: (r2v3 'inputStream' java.io.InputStream) = (r2v0 'inputStream' java.io.InputStream), (r2v0 'inputStream' java.io.InputStream), (r2v5 'inputStream' java.io.InputStream), (r2v5 'inputStream' java.io.InputStream), (r2v0 'inputStream' java.io.InputStream) binds: [B:1:0x0008, B:12:0x0040, B:27:0x00c6, B:36:0x00ea, B:16:0x005e] A[DONT_GENERATE, DONT_INLINE], Splitter:B:12:0x0040] */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x01c5 A[ExcHandler: XmlPullParserException (e org.xmlpull.v1.XmlPullParserException), PHI: r2 
      PHI: (r2v2 'inputStream' java.io.InputStream) = (r2v0 'inputStream' java.io.InputStream), (r2v0 'inputStream' java.io.InputStream), (r2v5 'inputStream' java.io.InputStream), (r2v5 'inputStream' java.io.InputStream), (r2v0 'inputStream' java.io.InputStream) binds: [B:1:0x0008, B:12:0x0040, B:27:0x00c6, B:36:0x00ea, B:16:0x005e] A[DONT_GENERATE, DONT_INLINE], Splitter:B:1:0x0008] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void read() {
        /*
            java.lang.String r0 = "device_features/"
            java.lang.String r1 = "FeatureParser"
            r2 = 0
            r3 = 0
            java.lang.String r4 = "cancro"
            java.lang.String r5 = miui.os.Build.DEVICE     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            boolean r4 = r4.equals(r5)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            if (r4 == 0) goto L_0x002c
            java.lang.String r4 = miui.os.Build.MODEL     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r5 = "MI 3"
            boolean r4 = r4.startsWith(r5)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            if (r4 == 0) goto L_0x001e
            java.lang.String r4 = "cancro_MI3.xml"
            r3 = r4
            goto L_0x0040
        L_0x001e:
            java.lang.String r4 = miui.os.Build.MODEL     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r5 = "MI 4"
            boolean r4 = r4.startsWith(r5)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            if (r4 == 0) goto L_0x0040
            java.lang.String r4 = "cancro_MI4.xml"
            r3 = r4
            goto L_0x0040
        L_0x002c:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r4.<init>()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r5 = miui.os.Build.DEVICE     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r4.append(r5)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r5 = ".xml"
            r4.append(r5)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r4 = r4.toString()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r3 = r4
        L_0x0040:
            android.content.res.Resources r4 = android.content.res.Resources.getSystem()     // Catch:{ IOException -> 0x005d, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            android.content.res.AssetManager r4 = r4.getAssets()     // Catch:{ IOException -> 0x005d, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x005d, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r5.<init>()     // Catch:{ IOException -> 0x005d, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r5.append(r0)     // Catch:{ IOException -> 0x005d, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r5.append(r3)     // Catch:{ IOException -> 0x005d, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r5 = r5.toString()     // Catch:{ IOException -> 0x005d, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.io.InputStream r0 = r4.open(r5)     // Catch:{ IOException -> 0x005d, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r2 = r0
            goto L_0x0086
        L_0x005d:
            r4 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r5.<init>()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r6 = "can't find "
            r5.append(r6)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r5.append(r3)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r6 = " in assets/"
            r5.append(r6)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r5.append(r0)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r0 = ",it may be in "
            r5.append(r0)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r0 = getDeviceFeaturesDir()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r5.append(r0)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r0 = r5.toString()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            android.util.Log.i(r1, r0)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
        L_0x0086:
            if (r2 != 0) goto L_0x00c6
            java.io.File r0 = new java.io.File     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r4 = getDeviceFeaturesDir()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r0.<init>(r4, r3)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            boolean r4 = r0.exists()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            if (r4 == 0) goto L_0x009e
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r1.<init>(r0)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r2 = r1
            goto L_0x00c6
        L_0x009e:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r4.<init>()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r5 = "both assets/device_features/ and "
            r4.append(r5)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r5 = getDeviceFeaturesDir()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r4.append(r5)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r5 = " don't exist "
            r4.append(r5)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r4.append(r3)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r4 = r4.toString()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            android.util.Log.e(r1, r4)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            if (r2 == 0) goto L_0x00c5
            r2.close()     // Catch:{ IOException -> 0x00c4 }
            goto L_0x00c5
        L_0x00c4:
            r1 = move-exception
        L_0x00c5:
            return
        L_0x00c6:
            org.xmlpull.v1.XmlPullParserFactory r0 = org.xmlpull.v1.XmlPullParserFactory.newInstance()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            org.xmlpull.v1.XmlPullParser r1 = r0.newPullParser()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r4 = "UTF-8"
            r1.setInput(r2, r4)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            int r4 = r1.getEventType()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
        L_0x00db:
            r9 = 1
            if (r9 == r4) goto L_0x01b5
            r9 = 2
            java.lang.String r10 = "string-array"
            java.lang.String r11 = "integer-array"
            if (r4 == r9) goto L_0x010a
            r9 = 3
            if (r4 == r9) goto L_0x00ea
            goto L_0x01ae
        L_0x00ea:
            java.lang.String r9 = r1.getName()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            boolean r11 = r11.equals(r9)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            if (r11 == 0) goto L_0x00fc
            java.util.HashMap<java.lang.String, java.util.ArrayList<java.lang.Integer>> r10 = sIntArrMap     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r10.put(r6, r7)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r7 = 0
            goto L_0x01ae
        L_0x00fc:
            boolean r10 = r10.equals(r9)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            if (r10 == 0) goto L_0x01ae
            java.util.HashMap<java.lang.String, java.util.ArrayList<java.lang.String>> r10 = sStrArrMap     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r10.put(r6, r8)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r8 = 0
            goto L_0x01ae
        L_0x010a:
            java.lang.String r9 = r1.getName()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r5 = r9
            int r9 = r1.getAttributeCount()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            if (r9 <= 0) goto L_0x011b
            r9 = 0
            java.lang.String r9 = r1.getAttributeValue(r9)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r6 = r9
        L_0x011b:
            boolean r9 = r11.equals(r5)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            if (r9 == 0) goto L_0x0129
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r9.<init>()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r7 = r9
            goto L_0x01ae
        L_0x0129:
            boolean r9 = r10.equals(r5)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            if (r9 == 0) goto L_0x0137
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r9.<init>()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r8 = r9
            goto L_0x01ae
        L_0x0137:
            java.lang.String r9 = "bool"
            boolean r9 = r9.equals(r5)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            if (r9 == 0) goto L_0x014d
            java.util.HashMap<java.lang.String, java.lang.Boolean> r9 = sBooleanMap     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r10 = r1.nextText()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r10)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r9.put(r6, r10)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            goto L_0x01ae
        L_0x014d:
            java.lang.String r9 = "integer"
            boolean r9 = r9.equals(r5)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            if (r9 == 0) goto L_0x0163
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = sIntMap     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r10 = r1.nextText()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r9.put(r6, r10)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            goto L_0x01ae
        L_0x0163:
            java.lang.String r9 = "string"
            boolean r9 = r9.equals(r5)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            if (r9 == 0) goto L_0x0175
            java.util.HashMap<java.lang.String, java.lang.String> r9 = sStrMap     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r10 = r1.nextText()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r9.put(r6, r10)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            goto L_0x01ae
        L_0x0175:
            java.lang.String r9 = "float"
            boolean r9 = r9.equals(r5)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            if (r9 == 0) goto L_0x018f
            java.util.HashMap<java.lang.String, java.lang.Float> r9 = sFloatMap     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.String r10 = r1.nextText()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            float r10 = java.lang.Float.parseFloat(r10)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.Float r10 = java.lang.Float.valueOf(r10)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r9.put(r6, r10)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            goto L_0x01ae
        L_0x018f:
            java.lang.String r9 = "item"
            boolean r9 = r9.equals(r5)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            if (r9 == 0) goto L_0x01ae
            if (r7 == 0) goto L_0x01a5
            java.lang.String r9 = r1.nextText()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r7.add(r9)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            goto L_0x01ae
        L_0x01a5:
            if (r8 == 0) goto L_0x01ae
            java.lang.String r9 = r1.nextText()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r8.add(r9)     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
        L_0x01ae:
            int r9 = r1.next()     // Catch:{ IOException -> 0x01cc, XmlPullParserException -> 0x01c5, all -> 0x01bc }
            r4 = r9
            goto L_0x00db
        L_0x01b5:
            r2.close()     // Catch:{ IOException -> 0x01ba }
        L_0x01b9:
            goto L_0x01d3
        L_0x01ba:
            r0 = move-exception
            goto L_0x01b9
        L_0x01bc:
            r0 = move-exception
            if (r2 == 0) goto L_0x01c4
            r2.close()     // Catch:{ IOException -> 0x01c3 }
            goto L_0x01c4
        L_0x01c3:
            r1 = move-exception
        L_0x01c4:
            throw r0
        L_0x01c5:
            r0 = move-exception
            if (r2 == 0) goto L_0x01d3
            r2.close()     // Catch:{ IOException -> 0x01ba }
            goto L_0x01b9
        L_0x01cc:
            r0 = move-exception
            if (r2 == 0) goto L_0x01d3
            r2.close()     // Catch:{ IOException -> 0x01ba }
            goto L_0x01b9
        L_0x01d3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.util.FeatureParser.read():void");
    }
}
