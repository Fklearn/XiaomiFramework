package com.miui.maml;

import android.text.TextUtils;
import java.io.InputStream;
import java.util.Locale;
import org.w3c.dom.Element;

public abstract class ResourceLoader {
    private static final String CONFIG_FILE_NAME = "config.xml";
    private static final String IMAGES_FOLDER_NAME = "images";
    private static final String LOG_TAG = "ResourceLoader";
    private static final String MANIFEST_FILE_NAME = "manifest.xml";
    protected String mConfigName = CONFIG_FILE_NAME;
    protected String mLanguageCountrySuffix;
    protected String mLanguageSuffix;
    protected Locale mLocale;
    protected String mManifestName = MANIFEST_FILE_NAME;
    private String mThemeName;

    private String getPathForLanguage(String str, String str2) {
        if (!TextUtils.isEmpty(this.mLanguageCountrySuffix)) {
            String str3 = str2 + "_" + this.mLanguageCountrySuffix + "/" + str;
            if (resourceExists(str3)) {
                return str3;
            }
        }
        if (!TextUtils.isEmpty(this.mLanguageSuffix)) {
            String str4 = str2 + "_" + this.mLanguageSuffix + "/" + str;
            if (resourceExists(str4)) {
                return str4;
            }
        }
        if (!TextUtils.isEmpty(str2)) {
            String str5 = str2 + "/" + str;
            if (resourceExists(str5)) {
                return str5;
            }
        }
        if (resourceExists(str)) {
            return str;
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0033, code lost:
        if (r4 == null) goto L_0x0065;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0062, code lost:
        if (r4 == null) goto L_0x0065;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.w3c.dom.Element getXmlRoot(java.lang.String r4) {
        /*
            r3 = this;
            java.lang.String r4 = r3.getPathForLanguage(r4)
            java.io.InputStream r4 = r3.getInputStream(r4)
            r0 = 0
            java.lang.String r1 = "ResourceLoader"
            if (r4 != 0) goto L_0x0013
            java.lang.String r4 = "getXmlRoot local inputStream is null"
            android.util.Log.e(r1, r4)
            return r0
        L_0x0013:
            javax.xml.parsers.DocumentBuilderFactory r2 = javax.xml.parsers.DocumentBuilderFactory.newInstance()     // Catch:{ IOException -> 0x005a, OutOfMemoryError -> 0x004f, ParserConfigurationException -> 0x0044, SAXException -> 0x0039, Exception -> 0x002b }
            javax.xml.parsers.DocumentBuilder r2 = r2.newDocumentBuilder()     // Catch:{ IOException -> 0x005a, OutOfMemoryError -> 0x004f, ParserConfigurationException -> 0x0044, SAXException -> 0x0039, Exception -> 0x002b }
            org.w3c.dom.Document r2 = r2.parse(r4)     // Catch:{ IOException -> 0x005a, OutOfMemoryError -> 0x004f, ParserConfigurationException -> 0x0044, SAXException -> 0x0039, Exception -> 0x002b }
            org.w3c.dom.Element r0 = r2.getDocumentElement()     // Catch:{ IOException -> 0x005a, OutOfMemoryError -> 0x004f, ParserConfigurationException -> 0x0044, SAXException -> 0x0039, Exception -> 0x002b }
            if (r4 == 0) goto L_0x0028
            r4.close()     // Catch:{ IOException -> 0x0028 }
        L_0x0028:
            return r0
        L_0x0029:
            r0 = move-exception
            goto L_0x0066
        L_0x002b:
            r2 = move-exception
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0029 }
            android.util.Log.e(r1, r2)     // Catch:{ all -> 0x0029 }
            if (r4 == 0) goto L_0x0065
        L_0x0035:
            r4.close()     // Catch:{ IOException -> 0x0065 }
            goto L_0x0065
        L_0x0039:
            r2 = move-exception
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0029 }
            android.util.Log.e(r1, r2)     // Catch:{ all -> 0x0029 }
            if (r4 == 0) goto L_0x0065
            goto L_0x0035
        L_0x0044:
            r2 = move-exception
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0029 }
            android.util.Log.e(r1, r2)     // Catch:{ all -> 0x0029 }
            if (r4 == 0) goto L_0x0065
            goto L_0x0035
        L_0x004f:
            r2 = move-exception
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0029 }
            android.util.Log.e(r1, r2)     // Catch:{ all -> 0x0029 }
            if (r4 == 0) goto L_0x0065
            goto L_0x0035
        L_0x005a:
            r2 = move-exception
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0029 }
            android.util.Log.e(r1, r2)     // Catch:{ all -> 0x0029 }
            if (r4 == 0) goto L_0x0065
            goto L_0x0035
        L_0x0065:
            return r0
        L_0x0066:
            if (r4 == 0) goto L_0x006b
            r4.close()     // Catch:{ IOException -> 0x006b }
        L_0x006b:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ResourceLoader.getXmlRoot(java.lang.String):org.w3c.dom.Element");
    }

    public void finish() {
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x00a1, code lost:
        if (r0 != null) goto L_0x00a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00bc, code lost:
        if (r0 == null) goto L_0x00bf;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.miui.maml.ResourceManager.BitmapInfo getBitmapInfo(java.lang.String r7, android.graphics.BitmapFactory.Options r8) {
        /*
            r6 = this;
            java.lang.String r0 = "images"
            java.lang.String r1 = r6.getPathForLanguage(r7, r0)
            r2 = 0
            java.lang.String r3 = "ResourceLoader"
            if (r1 != 0) goto L_0x003a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "TRY AGAIN to get getPathForLanguage: "
            r1.append(r4)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r3, r1)
            java.lang.String r1 = r6.getPathForLanguage(r7, r0)
            if (r1 != 0) goto L_0x003a
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r0 = "fail to get getPathForLanguage: "
        L_0x002c:
            r8.append(r0)
            r8.append(r7)
            java.lang.String r7 = r8.toString()
            android.util.Log.e(r3, r7)
            return r2
        L_0x003a:
            java.io.InputStream r0 = r6.getInputStream(r1)
            if (r0 != 0) goto L_0x0062
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "TRY AGAIN to get InputStream: "
            r0.append(r4)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r3, r0)
            java.io.InputStream r0 = r6.getInputStream(r1)
            if (r0 != 0) goto L_0x0062
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r0 = "fail to get InputStream: "
            goto L_0x002c
        L_0x0062:
            android.graphics.Rect r1 = new android.graphics.Rect     // Catch:{ OutOfMemoryError -> 0x00b4 }
            r1.<init>()     // Catch:{ OutOfMemoryError -> 0x00b4 }
            android.graphics.Bitmap r4 = android.graphics.BitmapFactory.decodeStream(r0, r1, r8)     // Catch:{ OutOfMemoryError -> 0x00b4 }
            if (r4 != 0) goto L_0x00a7
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ OutOfMemoryError -> 0x00b4 }
            r4.<init>()     // Catch:{ OutOfMemoryError -> 0x00b4 }
            java.lang.String r5 = "TRY AGAIN to decode bitmap: "
            r4.append(r5)     // Catch:{ OutOfMemoryError -> 0x00b4 }
            r4.append(r7)     // Catch:{ OutOfMemoryError -> 0x00b4 }
            java.lang.String r4 = r4.toString()     // Catch:{ OutOfMemoryError -> 0x00b4 }
            android.util.Log.d(r3, r4)     // Catch:{ OutOfMemoryError -> 0x00b4 }
            android.graphics.Bitmap r8 = android.graphics.BitmapFactory.decodeStream(r0, r1, r8)     // Catch:{ OutOfMemoryError -> 0x00b4 }
            if (r8 != 0) goto L_0x00a1
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ OutOfMemoryError -> 0x00b4 }
            r8.<init>()     // Catch:{ OutOfMemoryError -> 0x00b4 }
            java.lang.String r1 = "fail to decode bitmap: "
            r8.append(r1)     // Catch:{ OutOfMemoryError -> 0x00b4 }
            r8.append(r7)     // Catch:{ OutOfMemoryError -> 0x00b4 }
            java.lang.String r7 = r8.toString()     // Catch:{ OutOfMemoryError -> 0x00b4 }
            android.util.Log.e(r3, r7)     // Catch:{ OutOfMemoryError -> 0x00b4 }
            if (r0 == 0) goto L_0x00a0
            r0.close()     // Catch:{ IOException -> 0x00a0 }
        L_0x00a0:
            return r2
        L_0x00a1:
            if (r0 == 0) goto L_0x00bf
        L_0x00a3:
            r0.close()     // Catch:{ IOException -> 0x00bf }
            goto L_0x00bf
        L_0x00a7:
            com.miui.maml.ResourceManager$BitmapInfo r7 = new com.miui.maml.ResourceManager$BitmapInfo     // Catch:{ OutOfMemoryError -> 0x00b4 }
            r7.<init>(r4, r1)     // Catch:{ OutOfMemoryError -> 0x00b4 }
            if (r0 == 0) goto L_0x00b1
            r0.close()     // Catch:{ IOException -> 0x00b1 }
        L_0x00b1:
            return r7
        L_0x00b2:
            r7 = move-exception
            goto L_0x00c0
        L_0x00b4:
            r7 = move-exception
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x00b2 }
            android.util.Log.e(r3, r7)     // Catch:{ all -> 0x00b2 }
            if (r0 == 0) goto L_0x00bf
            goto L_0x00a3
        L_0x00bf:
            return r2
        L_0x00c0:
            if (r0 == 0) goto L_0x00c5
            r0.close()     // Catch:{ IOException -> 0x00c5 }
        L_0x00c5:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ResourceLoader.getBitmapInfo(java.lang.String, android.graphics.BitmapFactory$Options):com.miui.maml.ResourceManager$BitmapInfo");
    }

    public Element getConfigRoot() {
        return getXmlRoot(this.mConfigName);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0032, code lost:
        if (r10 != null) goto L_0x0034;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x004d, code lost:
        if (r10 == null) goto L_0x0050;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.os.MemoryFile getFile(java.lang.String r10) {
        /*
            r9 = this;
            java.lang.String r0 = "ResourceLoader"
            r1 = 1
            long[] r1 = new long[r1]
            java.io.InputStream r10 = r9.getInputStream(r10, r1)
            r2 = 0
            if (r10 != 0) goto L_0x000d
            return r2
        L_0x000d:
            r3 = 65536(0x10000, float:9.18355E-41)
            byte[] r4 = new byte[r3]     // Catch:{ IOException -> 0x0045, OutOfMemoryError -> 0x003a }
            android.os.MemoryFile r5 = new android.os.MemoryFile     // Catch:{ IOException -> 0x0045, OutOfMemoryError -> 0x003a }
            r6 = 0
            r7 = r1[r6]     // Catch:{ IOException -> 0x0045, OutOfMemoryError -> 0x003a }
            int r1 = (int) r7     // Catch:{ IOException -> 0x0045, OutOfMemoryError -> 0x003a }
            r5.<init>(r2, r1)     // Catch:{ IOException -> 0x0045, OutOfMemoryError -> 0x003a }
            r1 = r6
        L_0x001b:
            int r7 = r10.read(r4, r6, r3)     // Catch:{ IOException -> 0x0045, OutOfMemoryError -> 0x003a }
            if (r7 <= 0) goto L_0x0026
            r5.writeBytes(r4, r6, r1, r7)     // Catch:{ IOException -> 0x0045, OutOfMemoryError -> 0x003a }
            int r1 = r1 + r7
            goto L_0x001b
        L_0x0026:
            int r0 = r5.length()     // Catch:{ IOException -> 0x0045, OutOfMemoryError -> 0x003a }
            if (r0 <= 0) goto L_0x0032
            if (r10 == 0) goto L_0x0031
            r10.close()     // Catch:{ IOException -> 0x0031 }
        L_0x0031:
            return r5
        L_0x0032:
            if (r10 == 0) goto L_0x0050
        L_0x0034:
            r10.close()     // Catch:{ IOException -> 0x0050 }
            goto L_0x0050
        L_0x0038:
            r0 = move-exception
            goto L_0x0051
        L_0x003a:
            r1 = move-exception
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0038 }
            android.util.Log.e(r0, r1)     // Catch:{ all -> 0x0038 }
            if (r10 == 0) goto L_0x0050
            goto L_0x0034
        L_0x0045:
            r1 = move-exception
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0038 }
            android.util.Log.e(r0, r1)     // Catch:{ all -> 0x0038 }
            if (r10 == 0) goto L_0x0050
            goto L_0x0034
        L_0x0050:
            return r2
        L_0x0051:
            if (r10 == 0) goto L_0x0056
            r10.close()     // Catch:{ IOException -> 0x0056 }
        L_0x0056:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ResourceLoader.getFile(java.lang.String):android.os.MemoryFile");
    }

    public final InputStream getInputStream(String str) {
        return getInputStream(str, (long[]) null);
    }

    public abstract InputStream getInputStream(String str, long[] jArr);

    public Locale getLocale() {
        return this.mLocale;
    }

    public Element getManifestRoot() {
        return getXmlRoot(this.mManifestName);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0013, code lost:
        if (resourceExists(r0) == false) goto L_0x0015;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getPathForLanguage(java.lang.String r4) {
        /*
            r3 = this;
            java.lang.String r0 = r3.mLanguageCountrySuffix
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            r1 = 0
            if (r0 != 0) goto L_0x0015
            java.lang.String r0 = r3.mLanguageCountrySuffix
            java.lang.String r0 = com.miui.maml.util.Utils.addFileNameSuffix(r4, r0)
            boolean r2 = r3.resourceExists(r0)
            if (r2 != 0) goto L_0x0016
        L_0x0015:
            r0 = r1
        L_0x0016:
            if (r0 != 0) goto L_0x002d
            java.lang.String r2 = r3.mLanguageSuffix
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x002d
            java.lang.String r0 = r3.mLanguageSuffix
            java.lang.String r0 = com.miui.maml.util.Utils.addFileNameSuffix(r4, r0)
            boolean r2 = r3.resourceExists(r0)
            if (r2 != 0) goto L_0x002d
            r0 = r1
        L_0x002d:
            if (r0 == 0) goto L_0x0030
            r4 = r0
        L_0x0030:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ResourceLoader.getPathForLanguage(java.lang.String):java.lang.String");
    }

    public void init() {
    }

    public abstract boolean resourceExists(String str);

    public ResourceLoader setLocal(Locale locale) {
        if (locale != null) {
            this.mLanguageSuffix = locale.getLanguage();
            this.mLanguageCountrySuffix = locale.toString();
            if (TextUtils.equals(this.mLanguageSuffix, this.mLanguageCountrySuffix)) {
                this.mLanguageSuffix = null;
            }
        }
        this.mLocale = locale;
        return this;
    }
}
