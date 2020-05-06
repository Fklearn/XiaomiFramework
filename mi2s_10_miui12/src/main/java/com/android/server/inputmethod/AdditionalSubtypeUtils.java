package com.android.server.inputmethod;

import android.os.Environment;
import android.util.AtomicFile;
import java.io.File;

final class AdditionalSubtypeUtils {
    private static final String ADDITIONAL_SUBTYPES_FILE_NAME = "subtypes.xml";
    private static final String ATTR_ICON = "icon";
    private static final String ATTR_ID = "id";
    private static final String ATTR_IME_SUBTYPE_EXTRA_VALUE = "imeSubtypeExtraValue";
    private static final String ATTR_IME_SUBTYPE_ID = "subtypeId";
    private static final String ATTR_IME_SUBTYPE_LANGUAGE_TAG = "languageTag";
    private static final String ATTR_IME_SUBTYPE_LOCALE = "imeSubtypeLocale";
    private static final String ATTR_IME_SUBTYPE_MODE = "imeSubtypeMode";
    private static final String ATTR_IS_ASCII_CAPABLE = "isAsciiCapable";
    private static final String ATTR_IS_AUXILIARY = "isAuxiliary";
    private static final String ATTR_LABEL = "label";
    private static final String INPUT_METHOD_PATH = "inputmethod";
    private static final String NODE_IMI = "imi";
    private static final String NODE_SUBTYPE = "subtype";
    private static final String NODE_SUBTYPES = "subtypes";
    private static final String SYSTEM_PATH = "system";
    private static final String TAG = "AdditionalSubtypeUtils";

    private AdditionalSubtypeUtils() {
    }

    private static File getInputMethodDir(int userId) {
        File systemDir;
        if (userId == 0) {
            systemDir = new File(Environment.getDataDirectory(), SYSTEM_PATH);
        } else {
            systemDir = Environment.getUserSystemDirectory(userId);
        }
        return new File(systemDir, INPUT_METHOD_PATH);
    }

    private static AtomicFile getAdditionalSubtypeFile(File inputMethodDir) {
        return new AtomicFile(new File(inputMethodDir, ADDITIONAL_SUBTYPES_FILE_NAME), "input-subtypes");
    }

    /* JADX WARNING: Removed duplicated region for block: B:65:0x01c2  */
    /* JADX WARNING: Removed duplicated region for block: B:77:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void save(android.util.ArrayMap<java.lang.String, java.util.List<android.view.inputmethod.InputMethodSubtype>> r20, android.util.ArrayMap<java.lang.String, android.view.inputmethod.InputMethodInfo> r21, int r22) {
        /*
            r1 = r21
            java.lang.String r0 = "subtype"
            java.lang.String r2 = "imi"
            java.lang.String r3 = "subtypes"
            java.io.File r4 = getInputMethodDir(r22)
            boolean r5 = r20.isEmpty()
            java.lang.String r6 = "AdditionalSubtypeUtils"
            if (r5 == 0) goto L_0x004d
            boolean r0 = r4.exists()
            if (r0 != 0) goto L_0x001e
            return
        L_0x001e:
            android.util.AtomicFile r0 = getAdditionalSubtypeFile(r4)
            boolean r2 = r0.exists()
            if (r2 == 0) goto L_0x002b
            r0.delete()
        L_0x002b:
            java.io.File[] r2 = android.os.FileUtils.listFilesOrEmpty(r4)
            int r2 = r2.length
            if (r2 != 0) goto L_0x004c
            boolean r2 = r4.delete()
            if (r2 != 0) goto L_0x004c
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Failed to delete the empty parent directory "
            r2.append(r3)
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            android.util.Slog.e(r6, r2)
        L_0x004c:
            return
        L_0x004d:
            boolean r5 = r4.exists()
            if (r5 != 0) goto L_0x006e
            boolean r5 = r4.mkdirs()
            if (r5 != 0) goto L_0x006e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Failed to create a parent directory "
            r0.append(r2)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            android.util.Slog.e(r6, r0)
            return
        L_0x006e:
            r7 = 1
            if (r1 == 0) goto L_0x0079
            int r8 = r21.size()
            if (r8 <= 0) goto L_0x0079
            r8 = r7
            goto L_0x007a
        L_0x0079:
            r8 = 0
        L_0x007a:
            r9 = 0
            android.util.AtomicFile r10 = getAdditionalSubtypeFile(r4)
            java.io.FileOutputStream r11 = r10.startWrite()     // Catch:{ IOException -> 0x01b8 }
            r9 = r11
            com.android.internal.util.FastXmlSerializer r11 = new com.android.internal.util.FastXmlSerializer     // Catch:{ IOException -> 0x01b8 }
            r11.<init>()     // Catch:{ IOException -> 0x01b8 }
            java.nio.charset.Charset r12 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException -> 0x01b8 }
            java.lang.String r12 = r12.name()     // Catch:{ IOException -> 0x01b8 }
            r11.setOutput(r9, r12)     // Catch:{ IOException -> 0x01b8 }
            java.lang.Boolean r12 = java.lang.Boolean.valueOf(r7)     // Catch:{ IOException -> 0x01b8 }
            r13 = 0
            r11.startDocument(r13, r12)     // Catch:{ IOException -> 0x01b8 }
            java.lang.String r12 = "http://xmlpull.org/v1/doc/features.html#indent-output"
            r11.setFeature(r12, r7)     // Catch:{ IOException -> 0x01b8 }
            r11.startTag(r13, r3)     // Catch:{ IOException -> 0x01b8 }
            java.util.Set r12 = r20.keySet()     // Catch:{ IOException -> 0x01b8 }
            java.util.Iterator r12 = r12.iterator()     // Catch:{ IOException -> 0x01b8 }
        L_0x00ab:
            boolean r14 = r12.hasNext()     // Catch:{ IOException -> 0x01b8 }
            if (r14 == 0) goto L_0x01a9
            java.lang.Object r14 = r12.next()     // Catch:{ IOException -> 0x01b8 }
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ IOException -> 0x01b8 }
            if (r8 == 0) goto L_0x00d9
            boolean r15 = r1.containsKey(r14)     // Catch:{ IOException -> 0x00d4 }
            if (r15 != 0) goto L_0x00d9
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00d4 }
            r15.<init>()     // Catch:{ IOException -> 0x00d4 }
            java.lang.String r5 = "IME uninstalled or not valid.: "
            r15.append(r5)     // Catch:{ IOException -> 0x00d4 }
            r15.append(r14)     // Catch:{ IOException -> 0x00d4 }
            java.lang.String r5 = r15.toString()     // Catch:{ IOException -> 0x00d4 }
            android.util.Slog.w(r6, r5)     // Catch:{ IOException -> 0x00d4 }
            goto L_0x00ab
        L_0x00d4:
            r0 = move-exception
            r19 = r4
            goto L_0x01bb
        L_0x00d9:
            r11.startTag(r13, r2)     // Catch:{ IOException -> 0x01b8 }
            java.lang.String r5 = "id"
            r11.attribute(r13, r5, r14)     // Catch:{ IOException -> 0x01b8 }
            r5 = r20
            java.lang.Object r15 = r5.get(r14)     // Catch:{ IOException -> 0x01b8 }
            java.util.List r15 = (java.util.List) r15     // Catch:{ IOException -> 0x01b8 }
            int r16 = r15.size()     // Catch:{ IOException -> 0x01b8 }
            r17 = r16
            r16 = 0
            r7 = r16
        L_0x00f4:
            r13 = r17
            if (r7 >= r13) goto L_0x019b
            java.lang.Object r17 = r15.get(r7)     // Catch:{ IOException -> 0x01b8 }
            android.view.inputmethod.InputMethodSubtype r17 = (android.view.inputmethod.InputMethodSubtype) r17     // Catch:{ IOException -> 0x01b8 }
            r1 = 0
            r11.startTag(r1, r0)     // Catch:{ IOException -> 0x01b8 }
            boolean r1 = r17.hasSubtypeId()     // Catch:{ IOException -> 0x01b8 }
            if (r1 == 0) goto L_0x011a
            java.lang.String r1 = "subtypeId"
            int r18 = r17.getSubtypeId()     // Catch:{ IOException -> 0x01b8 }
            r19 = r4
            java.lang.String r4 = java.lang.String.valueOf(r18)     // Catch:{ IOException -> 0x01b6 }
            r5 = 0
            r11.attribute(r5, r1, r4)     // Catch:{ IOException -> 0x01b6 }
            goto L_0x011c
        L_0x011a:
            r19 = r4
        L_0x011c:
            java.lang.String r1 = "icon"
            int r4 = r17.getIconResId()     // Catch:{ IOException -> 0x01b6 }
            java.lang.String r4 = java.lang.String.valueOf(r4)     // Catch:{ IOException -> 0x01b6 }
            r5 = 0
            r11.attribute(r5, r1, r4)     // Catch:{ IOException -> 0x01b6 }
            java.lang.String r1 = "label"
            int r4 = r17.getNameResId()     // Catch:{ IOException -> 0x01b6 }
            java.lang.String r4 = java.lang.String.valueOf(r4)     // Catch:{ IOException -> 0x01b6 }
            r5 = 0
            r11.attribute(r5, r1, r4)     // Catch:{ IOException -> 0x01b6 }
            java.lang.String r1 = "imeSubtypeLocale"
            java.lang.String r4 = r17.getLocale()     // Catch:{ IOException -> 0x01b6 }
            r11.attribute(r5, r1, r4)     // Catch:{ IOException -> 0x01b6 }
            java.lang.String r1 = "languageTag"
            java.lang.String r4 = r17.getLanguageTag()     // Catch:{ IOException -> 0x01b6 }
            r11.attribute(r5, r1, r4)     // Catch:{ IOException -> 0x01b6 }
            java.lang.String r1 = "imeSubtypeMode"
            java.lang.String r4 = r17.getMode()     // Catch:{ IOException -> 0x01b6 }
            r11.attribute(r5, r1, r4)     // Catch:{ IOException -> 0x01b6 }
            java.lang.String r1 = "imeSubtypeExtraValue"
            java.lang.String r4 = r17.getExtraValue()     // Catch:{ IOException -> 0x01b6 }
            r11.attribute(r5, r1, r4)     // Catch:{ IOException -> 0x01b6 }
            java.lang.String r1 = "isAuxiliary"
            boolean r4 = r17.isAuxiliary()     // Catch:{ IOException -> 0x01b6 }
            if (r4 == 0) goto L_0x016d
            r4 = 1
            goto L_0x016e
        L_0x016d:
            r4 = 0
        L_0x016e:
            java.lang.String r4 = java.lang.String.valueOf(r4)     // Catch:{ IOException -> 0x01b6 }
            r5 = 0
            r11.attribute(r5, r1, r4)     // Catch:{ IOException -> 0x01b6 }
            java.lang.String r1 = "isAsciiCapable"
            boolean r4 = r17.isAsciiCapable()     // Catch:{ IOException -> 0x01b6 }
            if (r4 == 0) goto L_0x0181
            r4 = 1
            goto L_0x0182
        L_0x0181:
            r4 = 0
        L_0x0182:
            java.lang.String r4 = java.lang.String.valueOf(r4)     // Catch:{ IOException -> 0x01b6 }
            r5 = 0
            r11.attribute(r5, r1, r4)     // Catch:{ IOException -> 0x01b6 }
            r11.endTag(r5, r0)     // Catch:{ IOException -> 0x01b6 }
            int r7 = r7 + 1
            r5 = r20
            r1 = r21
            r17 = r13
            r4 = r19
            r13 = 0
            goto L_0x00f4
        L_0x019b:
            r19 = r4
            r1 = 0
            r11.endTag(r1, r2)     // Catch:{ IOException -> 0x01b6 }
            r1 = r21
            r4 = r19
            r7 = 1
            r13 = 0
            goto L_0x00ab
        L_0x01a9:
            r19 = r4
            r0 = 0
            r11.endTag(r0, r3)     // Catch:{ IOException -> 0x01b6 }
            r11.endDocument()     // Catch:{ IOException -> 0x01b6 }
            r10.finishWrite(r9)     // Catch:{ IOException -> 0x01b6 }
            goto L_0x01c5
        L_0x01b6:
            r0 = move-exception
            goto L_0x01bb
        L_0x01b8:
            r0 = move-exception
            r19 = r4
        L_0x01bb:
            java.lang.String r1 = "Error writing subtypes"
            android.util.Slog.w(r6, r1, r0)
            if (r9 == 0) goto L_0x01c5
            r10.failWrite(r9)
        L_0x01c5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.AdditionalSubtypeUtils.save(android.util.ArrayMap, android.util.ArrayMap, int):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:76:0x01ee, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x01ef, code lost:
        r4 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x01f0, code lost:
        if (r3 != null) goto L_0x01f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x01f6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:?, code lost:
        r2.addSuppressed(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x01fc, code lost:
        r0 = e;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:63:0x01d2, B:74:0x01ed, B:79:0x01f2] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void load(android.util.ArrayMap<java.lang.String, java.util.List<android.view.inputmethod.InputMethodSubtype>> r23, int r24) {
        /*
            java.lang.String r0 = "1"
            java.lang.String r1 = "AdditionalSubtypeUtils"
            r23.clear()
            java.io.File r2 = getInputMethodDir(r24)
            android.util.AtomicFile r2 = getAdditionalSubtypeFile(r2)
            boolean r3 = r2.exists()
            if (r3 != 0) goto L_0x0016
            return
        L_0x0016:
            java.io.FileInputStream r3 = r2.openRead()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x01fe }
            org.xmlpull.v1.XmlPullParser r4 = android.util.Xml.newPullParser()     // Catch:{ all -> 0x01e7 }
            java.nio.charset.Charset r5 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ all -> 0x01e7 }
            java.lang.String r5 = r5.name()     // Catch:{ all -> 0x01e7 }
            r4.setInput(r3, r5)     // Catch:{ all -> 0x01e7 }
            int r5 = r4.getEventType()     // Catch:{ all -> 0x01e7 }
        L_0x002b:
            int r6 = r4.next()     // Catch:{ all -> 0x01e7 }
            r5 = r6
            r6 = 1
            r7 = 2
            if (r5 == r7) goto L_0x0036
            if (r5 != r6) goto L_0x002b
        L_0x0036:
            java.lang.String r8 = r4.getName()     // Catch:{ all -> 0x01e7 }
            java.lang.String r9 = "subtypes"
            boolean r9 = r9.equals(r8)     // Catch:{ all -> 0x01e7 }
            if (r9 == 0) goto L_0x01d6
            int r9 = r4.getDepth()     // Catch:{ all -> 0x01e7 }
            r10 = 0
            r11 = 0
            r12 = r11
        L_0x004a:
            int r13 = r4.next()     // Catch:{ all -> 0x01e7 }
            r5 = r13
            r14 = 3
            if (r13 != r14) goto L_0x0067
            int r13 = r4.getDepth()     // Catch:{ all -> 0x005f }
            if (r13 <= r9) goto L_0x0059
            goto L_0x0067
        L_0x0059:
            r14 = r23
            r16 = r2
            goto L_0x01d0
        L_0x005f:
            r0 = move-exception
            r14 = r23
            r16 = r2
            r2 = r0
            goto L_0x01ed
        L_0x0067:
            if (r5 == r6) goto L_0x01c6
            if (r5 == r7) goto L_0x0079
            r14 = r23
            r21 = r0
            r16 = r2
            r17 = r5
            r18 = r8
            r19 = r9
            goto L_0x019a
        L_0x0079:
            java.lang.String r13 = r4.getName()     // Catch:{ all -> 0x01e7 }
            java.lang.String r14 = "imi"
            boolean r14 = r14.equals(r13)     // Catch:{ all -> 0x01e7 }
            if (r14 == 0) goto L_0x00b7
            java.lang.String r14 = "id"
            java.lang.String r14 = r4.getAttributeValue(r11, r14)     // Catch:{ all -> 0x005f }
            r10 = r14
            boolean r14 = android.text.TextUtils.isEmpty(r10)     // Catch:{ all -> 0x005f }
            if (r14 == 0) goto L_0x009a
            java.lang.String r14 = "Invalid imi id found in subtypes.xml"
            android.util.Slog.w(r1, r14)     // Catch:{ all -> 0x005f }
            goto L_0x004a
        L_0x009a:
            java.util.ArrayList r14 = new java.util.ArrayList     // Catch:{ all -> 0x005f }
            r14.<init>()     // Catch:{ all -> 0x005f }
            r12 = r14
            r14 = r23
            r14.put(r10, r12)     // Catch:{ all -> 0x00b1 }
            r21 = r0
            r16 = r2
            r17 = r5
            r18 = r8
            r19 = r9
            goto L_0x01b5
        L_0x00b1:
            r0 = move-exception
            r16 = r2
            r2 = r0
            goto L_0x01ed
        L_0x00b7:
            r14 = r23
            java.lang.String r15 = "subtype"
            boolean r15 = r15.equals(r13)     // Catch:{ all -> 0x01c4 }
            if (r15 == 0) goto L_0x01a9
            boolean r15 = android.text.TextUtils.isEmpty(r10)     // Catch:{ all -> 0x01c4 }
            if (r15 != 0) goto L_0x0179
            if (r12 != 0) goto L_0x00d8
            r21 = r0
            r16 = r2
            r17 = r5
            r18 = r8
            r19 = r9
            r20 = r13
            goto L_0x0185
        L_0x00d8:
            java.lang.String r15 = "icon"
            java.lang.String r15 = r4.getAttributeValue(r11, r15)     // Catch:{ all -> 0x01c4 }
            int r15 = java.lang.Integer.parseInt(r15)     // Catch:{ all -> 0x01c4 }
            java.lang.String r6 = "label"
            java.lang.String r6 = r4.getAttributeValue(r11, r6)     // Catch:{ all -> 0x01c4 }
            int r6 = java.lang.Integer.parseInt(r6)     // Catch:{ all -> 0x01c4 }
            java.lang.String r7 = "imeSubtypeLocale"
            java.lang.String r7 = r4.getAttributeValue(r11, r7)     // Catch:{ all -> 0x01c4 }
            java.lang.String r11 = "languageTag"
            r16 = r2
            r2 = 0
            java.lang.String r11 = r4.getAttributeValue(r2, r11)     // Catch:{ all -> 0x01e4 }
            java.lang.String r2 = "imeSubtypeMode"
            r17 = r5
            r5 = 0
            java.lang.String r2 = r4.getAttributeValue(r5, r2)     // Catch:{ all -> 0x01e4 }
            java.lang.String r5 = "imeSubtypeExtraValue"
            r18 = r8
            r8 = 0
            java.lang.String r5 = r4.getAttributeValue(r8, r5)     // Catch:{ all -> 0x01e4 }
            java.lang.String r8 = "isAuxiliary"
            r19 = r9
            r9 = 0
            java.lang.String r8 = r4.getAttributeValue(r9, r8)     // Catch:{ all -> 0x01e4 }
            java.lang.String r8 = java.lang.String.valueOf(r8)     // Catch:{ all -> 0x01e4 }
            boolean r8 = r0.equals(r8)     // Catch:{ all -> 0x01e4 }
            java.lang.String r9 = "isAsciiCapable"
            r20 = r13
            r13 = 0
            java.lang.String r9 = r4.getAttributeValue(r13, r9)     // Catch:{ all -> 0x01e4 }
            java.lang.String r9 = java.lang.String.valueOf(r9)     // Catch:{ all -> 0x01e4 }
            boolean r9 = r0.equals(r9)     // Catch:{ all -> 0x01e4 }
            android.view.inputmethod.InputMethodSubtype$InputMethodSubtypeBuilder r13 = new android.view.inputmethod.InputMethodSubtype$InputMethodSubtypeBuilder     // Catch:{ all -> 0x01e4 }
            r13.<init>()     // Catch:{ all -> 0x01e4 }
            android.view.inputmethod.InputMethodSubtype$InputMethodSubtypeBuilder r13 = r13.setSubtypeNameResId(r6)     // Catch:{ all -> 0x01e4 }
            android.view.inputmethod.InputMethodSubtype$InputMethodSubtypeBuilder r13 = r13.setSubtypeIconResId(r15)     // Catch:{ all -> 0x01e4 }
            android.view.inputmethod.InputMethodSubtype$InputMethodSubtypeBuilder r13 = r13.setSubtypeLocale(r7)     // Catch:{ all -> 0x01e4 }
            android.view.inputmethod.InputMethodSubtype$InputMethodSubtypeBuilder r13 = r13.setLanguageTag(r11)     // Catch:{ all -> 0x01e4 }
            android.view.inputmethod.InputMethodSubtype$InputMethodSubtypeBuilder r13 = r13.setSubtypeMode(r2)     // Catch:{ all -> 0x01e4 }
            android.view.inputmethod.InputMethodSubtype$InputMethodSubtypeBuilder r13 = r13.setSubtypeExtraValue(r5)     // Catch:{ all -> 0x01e4 }
            android.view.inputmethod.InputMethodSubtype$InputMethodSubtypeBuilder r13 = r13.setIsAuxiliary(r8)     // Catch:{ all -> 0x01e4 }
            android.view.inputmethod.InputMethodSubtype$InputMethodSubtypeBuilder r13 = r13.setIsAsciiCapable(r9)     // Catch:{ all -> 0x01e4 }
            r21 = r0
            java.lang.String r0 = "subtypeId"
            r22 = r2
            r2 = 0
            java.lang.String r0 = r4.getAttributeValue(r2, r0)     // Catch:{ all -> 0x01e4 }
            if (r0 == 0) goto L_0x0171
            int r2 = java.lang.Integer.parseInt(r0)     // Catch:{ all -> 0x01e4 }
            r13.setSubtypeId(r2)     // Catch:{ all -> 0x01e4 }
        L_0x0171:
            android.view.inputmethod.InputMethodSubtype r2 = r13.build()     // Catch:{ all -> 0x01e4 }
            r12.add(r2)     // Catch:{ all -> 0x01e4 }
            goto L_0x01b5
        L_0x0179:
            r21 = r0
            r16 = r2
            r17 = r5
            r18 = r8
            r19 = r9
            r20 = r13
        L_0x0185:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e4 }
            r0.<init>()     // Catch:{ all -> 0x01e4 }
            java.lang.String r2 = "IME uninstalled or not valid.: "
            r0.append(r2)     // Catch:{ all -> 0x01e4 }
            r0.append(r10)     // Catch:{ all -> 0x01e4 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x01e4 }
            android.util.Slog.w(r1, r0)     // Catch:{ all -> 0x01e4 }
        L_0x019a:
            r2 = r16
            r5 = r17
            r8 = r18
            r9 = r19
            r0 = r21
            r6 = 1
            r7 = 2
            r11 = 0
            goto L_0x004a
        L_0x01a9:
            r21 = r0
            r16 = r2
            r17 = r5
            r18 = r8
            r19 = r9
            r20 = r13
        L_0x01b5:
            r2 = r16
            r5 = r17
            r8 = r18
            r9 = r19
            r0 = r21
            r6 = 1
            r7 = 2
            r11 = 0
            goto L_0x004a
        L_0x01c4:
            r0 = move-exception
            goto L_0x01ea
        L_0x01c6:
            r14 = r23
            r16 = r2
            r17 = r5
            r18 = r8
            r19 = r9
        L_0x01d0:
            if (r3 == 0) goto L_0x01d5
            r3.close()     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x01fc }
        L_0x01d5:
            goto L_0x0208
        L_0x01d6:
            r14 = r23
            r16 = r2
            r18 = r8
            org.xmlpull.v1.XmlPullParserException r0 = new org.xmlpull.v1.XmlPullParserException     // Catch:{ all -> 0x01e4 }
            java.lang.String r2 = "Xml doesn't start with subtypes"
            r0.<init>(r2)     // Catch:{ all -> 0x01e4 }
            throw r0     // Catch:{ all -> 0x01e4 }
        L_0x01e4:
            r0 = move-exception
            r2 = r0
            goto L_0x01ed
        L_0x01e7:
            r0 = move-exception
            r14 = r23
        L_0x01ea:
            r16 = r2
            r2 = r0
        L_0x01ed:
            throw r2     // Catch:{ all -> 0x01ee }
        L_0x01ee:
            r0 = move-exception
            r4 = r0
            if (r3 == 0) goto L_0x01fb
            r3.close()     // Catch:{ all -> 0x01f6 }
            goto L_0x01fb
        L_0x01f6:
            r0 = move-exception
            r5 = r0
            r2.addSuppressed(r5)     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x01fc }
        L_0x01fb:
            throw r4     // Catch:{ IOException | NumberFormatException | XmlPullParserException -> 0x01fc }
        L_0x01fc:
            r0 = move-exception
            goto L_0x0203
        L_0x01fe:
            r0 = move-exception
            r14 = r23
            r16 = r2
        L_0x0203:
            java.lang.String r2 = "Error reading subtypes"
            android.util.Slog.w(r1, r2, r0)
        L_0x0208:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.inputmethod.AdditionalSubtypeUtils.load(android.util.ArrayMap, int):void");
    }
}
