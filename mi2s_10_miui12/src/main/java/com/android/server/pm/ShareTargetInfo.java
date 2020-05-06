package com.android.server.pm;

import android.text.TextUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

class ShareTargetInfo {
    private static final String ATTR_HOST = "host";
    private static final String ATTR_MIME_TYPE = "mimeType";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_PATH = "path";
    private static final String ATTR_PATH_PATTERN = "pathPattern";
    private static final String ATTR_PATH_PREFIX = "pathPrefix";
    private static final String ATTR_PORT = "port";
    private static final String ATTR_SCHEME = "scheme";
    private static final String ATTR_TARGET_CLASS = "targetClass";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_DATA = "data";
    private static final String TAG_SHARE_TARGET = "share-target";
    final String[] mCategories;
    final String mTargetClass;
    final TargetData[] mTargetData;

    static class TargetData {
        final String mHost;
        final String mMimeType;
        final String mPath;
        final String mPathPattern;
        final String mPathPrefix;
        final String mPort;
        final String mScheme;

        TargetData(String scheme, String host, String port, String path, String pathPattern, String pathPrefix, String mimeType) {
            this.mScheme = scheme;
            this.mHost = host;
            this.mPort = port;
            this.mPath = path;
            this.mPathPattern = pathPattern;
            this.mPathPrefix = pathPrefix;
            this.mMimeType = mimeType;
        }

        public void toStringInner(StringBuilder strBuilder) {
            if (!TextUtils.isEmpty(this.mScheme)) {
                strBuilder.append(" scheme=");
                strBuilder.append(this.mScheme);
            }
            if (!TextUtils.isEmpty(this.mHost)) {
                strBuilder.append(" host=");
                strBuilder.append(this.mHost);
            }
            if (!TextUtils.isEmpty(this.mPort)) {
                strBuilder.append(" port=");
                strBuilder.append(this.mPort);
            }
            if (!TextUtils.isEmpty(this.mPath)) {
                strBuilder.append(" path=");
                strBuilder.append(this.mPath);
            }
            if (!TextUtils.isEmpty(this.mPathPattern)) {
                strBuilder.append(" pathPattern=");
                strBuilder.append(this.mPathPattern);
            }
            if (!TextUtils.isEmpty(this.mPathPrefix)) {
                strBuilder.append(" pathPrefix=");
                strBuilder.append(this.mPathPrefix);
            }
            if (!TextUtils.isEmpty(this.mMimeType)) {
                strBuilder.append(" mimeType=");
                strBuilder.append(this.mMimeType);
            }
        }

        public String toString() {
            StringBuilder strBuilder = new StringBuilder();
            toStringInner(strBuilder);
            return strBuilder.toString();
        }
    }

    ShareTargetInfo(TargetData[] data, String targetClass, String[] categories) {
        this.mTargetData = data;
        this.mTargetClass = targetClass;
        this.mCategories = categories;
    }

    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("targetClass=");
        strBuilder.append(this.mTargetClass);
        for (TargetData stringInner : this.mTargetData) {
            strBuilder.append(" data={");
            stringInner.toStringInner(strBuilder);
            strBuilder.append("}");
        }
        for (String append : this.mCategories) {
            strBuilder.append(" category=");
            strBuilder.append(append);
        }
        return strBuilder.toString();
    }

    /* access modifiers changed from: package-private */
    public void saveToXml(XmlSerializer out) throws IOException {
        out.startTag((String) null, TAG_SHARE_TARGET);
        ShortcutService.writeAttr(out, ATTR_TARGET_CLASS, (CharSequence) this.mTargetClass);
        for (int i = 0; i < this.mTargetData.length; i++) {
            out.startTag((String) null, "data");
            ShortcutService.writeAttr(out, ATTR_SCHEME, (CharSequence) this.mTargetData[i].mScheme);
            ShortcutService.writeAttr(out, "host", (CharSequence) this.mTargetData[i].mHost);
            ShortcutService.writeAttr(out, ATTR_PORT, (CharSequence) this.mTargetData[i].mPort);
            ShortcutService.writeAttr(out, ATTR_PATH, (CharSequence) this.mTargetData[i].mPath);
            ShortcutService.writeAttr(out, ATTR_PATH_PATTERN, (CharSequence) this.mTargetData[i].mPathPattern);
            ShortcutService.writeAttr(out, ATTR_PATH_PREFIX, (CharSequence) this.mTargetData[i].mPathPrefix);
            ShortcutService.writeAttr(out, ATTR_MIME_TYPE, (CharSequence) this.mTargetData[i].mMimeType);
            out.endTag((String) null, "data");
        }
        for (String writeAttr : this.mCategories) {
            out.startTag((String) null, TAG_CATEGORY);
            ShortcutService.writeAttr(out, "name", (CharSequence) writeAttr);
            out.endTag((String) null, TAG_CATEGORY);
        }
        out.endTag((String) null, TAG_SHARE_TARGET);
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0055  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static com.android.server.pm.ShareTargetInfo loadFromXml(org.xmlpull.v1.XmlPullParser r9) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
            java.lang.String r0 = "targetClass"
            java.lang.String r0 = com.android.server.pm.ShortcutService.parseStringAttribute(r9, r0)
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
        L_0x0011:
            int r3 = r9.next()
            r4 = r3
            r5 = 1
            if (r3 == r5) goto L_0x006e
            r3 = 2
            if (r4 != r3) goto L_0x005e
            java.lang.String r3 = r9.getName()
            r6 = -1
            int r7 = r3.hashCode()
            r8 = 3076010(0x2eefaa, float:4.310408E-39)
            if (r7 == r8) goto L_0x003a
            r8 = 50511102(0x302bcfe, float:3.842052E-37)
            if (r7 == r8) goto L_0x0030
        L_0x002f:
            goto L_0x0044
        L_0x0030:
            java.lang.String r7 = "category"
            boolean r3 = r3.equals(r7)
            if (r3 == 0) goto L_0x002f
            r3 = r5
            goto L_0x0045
        L_0x003a:
            java.lang.String r7 = "data"
            boolean r3 = r3.equals(r7)
            if (r3 == 0) goto L_0x002f
            r3 = 0
            goto L_0x0045
        L_0x0044:
            r3 = r6
        L_0x0045:
            if (r3 == 0) goto L_0x0055
            if (r3 == r5) goto L_0x004a
            goto L_0x005d
        L_0x004a:
            java.lang.String r3 = "name"
            java.lang.String r3 = com.android.server.pm.ShortcutService.parseStringAttribute(r9, r3)
            r2.add(r3)
            goto L_0x005d
        L_0x0055:
            com.android.server.pm.ShareTargetInfo$TargetData r3 = parseTargetData(r9)
            r1.add(r3)
        L_0x005d:
            goto L_0x0011
        L_0x005e:
            r3 = 3
            if (r4 != r3) goto L_0x0011
            java.lang.String r3 = r9.getName()
            java.lang.String r5 = "share-target"
            boolean r3 = r3.equals(r5)
            if (r3 == 0) goto L_0x0011
        L_0x006e:
            boolean r3 = r1.isEmpty()
            if (r3 != 0) goto L_0x009b
            if (r0 == 0) goto L_0x009b
            boolean r3 = r2.isEmpty()
            if (r3 == 0) goto L_0x007d
            goto L_0x009b
        L_0x007d:
            com.android.server.pm.ShareTargetInfo r3 = new com.android.server.pm.ShareTargetInfo
            int r5 = r1.size()
            com.android.server.pm.ShareTargetInfo$TargetData[] r5 = new com.android.server.pm.ShareTargetInfo.TargetData[r5]
            java.lang.Object[] r5 = r1.toArray(r5)
            com.android.server.pm.ShareTargetInfo$TargetData[] r5 = (com.android.server.pm.ShareTargetInfo.TargetData[]) r5
            int r6 = r2.size()
            java.lang.String[] r6 = new java.lang.String[r6]
            java.lang.Object[] r6 = r2.toArray(r6)
            java.lang.String[] r6 = (java.lang.String[]) r6
            r3.<init>(r5, r0, r6)
            return r3
        L_0x009b:
            r3 = 0
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.ShareTargetInfo.loadFromXml(org.xmlpull.v1.XmlPullParser):com.android.server.pm.ShareTargetInfo");
    }

    private static TargetData parseTargetData(XmlPullParser parser) {
        XmlPullParser xmlPullParser = parser;
        return new TargetData(ShortcutService.parseStringAttribute(xmlPullParser, ATTR_SCHEME), ShortcutService.parseStringAttribute(xmlPullParser, "host"), ShortcutService.parseStringAttribute(xmlPullParser, ATTR_PORT), ShortcutService.parseStringAttribute(xmlPullParser, ATTR_PATH), ShortcutService.parseStringAttribute(xmlPullParser, ATTR_PATH_PATTERN), ShortcutService.parseStringAttribute(xmlPullParser, ATTR_PATH_PREFIX), ShortcutService.parseStringAttribute(xmlPullParser, ATTR_MIME_TYPE));
    }
}
