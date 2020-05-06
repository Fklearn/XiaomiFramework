package com.android.server.pm;

import android.content.pm.PackageParser;
import android.os.Environment;
import com.android.server.pm.Policy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class SELinuxMMAC {
    private static final boolean DEBUG_POLICY = false;
    private static final boolean DEBUG_POLICY_INSTALL = false;
    private static final boolean DEBUG_POLICY_ORDER = false;
    private static final String DEFAULT_SEINFO = "default";
    private static final String PRIVILEGED_APP_STR = ":privapp";
    private static final String SANDBOX_V2_STR = ":v2";
    static final String TAG = "SELinuxMMAC";
    private static final String TARGETSDKVERSION_STR = ":targetSdkVersion=";
    private static List<File> sMacPermissions = new ArrayList();
    private static List<Policy> sPolicies = new ArrayList();
    private static boolean sPolicyRead;

    static {
        sMacPermissions.add(new File(Environment.getRootDirectory(), "/etc/selinux/plat_mac_permissions.xml"));
        File productMacPermission = new File(Environment.getProductDirectory(), "/etc/selinux/product_mac_permissions.xml");
        if (productMacPermission.exists()) {
            sMacPermissions.add(productMacPermission);
        }
        File vendorMacPermission = new File(Environment.getVendorDirectory(), "/etc/selinux/vendor_mac_permissions.xml");
        if (vendorMacPermission.exists()) {
            sMacPermissions.add(vendorMacPermission);
        } else {
            sMacPermissions.add(new File(Environment.getVendorDirectory(), "/etc/selinux/nonplat_mac_permissions.xml"));
        }
        File odmMacPermission = new File(Environment.getOdmDirectory(), "/etc/selinux/odm_mac_permissions.xml");
        if (odmMacPermission.exists()) {
            sMacPermissions.add(odmMacPermission);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001f, code lost:
        if (r5 >= r4) goto L_0x00df;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0021, code lost:
        r7 = sMacPermissions.get(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r6 = new java.io.FileReader(r7);
        android.util.Slog.d(TAG, "Using policy file " + r7);
        r3.setInput(r6);
        r3.nextTag();
        r3.require(2, (java.lang.String) null, "policy");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0058, code lost:
        if (r3.next() == 3) goto L_0x0089;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x005e, code lost:
        if (r3.getEventType() == 2) goto L_0x0061;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0061, code lost:
        r8 = r3.getName();
        r9 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x006d, code lost:
        if (r8.hashCode() == -902467798) goto L_0x0070;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0077, code lost:
        if (r8.equals("signer") == false) goto L_0x006f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0079, code lost:
        r9 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x007a, code lost:
        if (r9 == 0) goto L_0x0080;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x007c, code lost:
        skip(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0080, code lost:
        r1.add(readSignerOrThrow(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0089, code lost:
        libcore.io.IoUtils.closeQuietly(r6);
        r5 = r5 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0090, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0092, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
        android.util.Slog.w(TAG, "Exception parsing " + r7, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00a9, code lost:
        libcore.io.IoUtils.closeQuietly(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00ad, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00ae, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
        android.util.Slog.w(TAG, "Exception @" + r3.getPositionDescription() + " while parsing " + r7 + ":" + r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00d6, code lost:
        libcore.io.IoUtils.closeQuietly(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00da, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00db, code lost:
        libcore.io.IoUtils.closeQuietly(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00de, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00df, code lost:
        r5 = new com.android.server.pm.PolicyComparator();
        java.util.Collections.sort(r1, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00eb, code lost:
        if (r5.foundDuplicate() == false) goto L_0x00f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00ed, code lost:
        android.util.Slog.w(TAG, "ERROR! Duplicate entries found parsing mac_permissions.xml files");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00f4, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00f5, code lost:
        r7 = sPolicies;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00f7, code lost:
        monitor-enter(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:?, code lost:
        sPolicies.clear();
        sPolicies.addAll(r1);
        sPolicyRead = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0104, code lost:
        monitor-exit(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x0105, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x000b, code lost:
        r1 = new java.util.ArrayList<>();
        r3 = android.util.Xml.newPullParser();
        r4 = sMacPermissions.size();
        r5 = 0;
        r6 = null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean readInstallPolicy() {
        /*
            java.util.List<com.android.server.pm.Policy> r0 = sPolicies
            monitor-enter(r0)
            boolean r1 = sPolicyRead     // Catch:{ all -> 0x0109 }
            r2 = 1
            if (r1 == 0) goto L_0x000a
            monitor-exit(r0)     // Catch:{ all -> 0x0109 }
            return r2
        L_0x000a:
            monitor-exit(r0)     // Catch:{ all -> 0x0109 }
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = r0
            r0 = 0
            org.xmlpull.v1.XmlPullParser r3 = android.util.Xml.newPullParser()
            java.util.List<java.io.File> r4 = sMacPermissions
            int r4 = r4.size()
            r5 = 0
            r6 = r0
        L_0x001e:
            r0 = 0
            if (r5 >= r4) goto L_0x00df
            java.util.List<java.io.File> r7 = sMacPermissions
            java.lang.Object r7 = r7.get(r5)
            java.io.File r7 = (java.io.File) r7
            java.io.FileReader r8 = new java.io.FileReader     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            r8.<init>(r7)     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            r6 = r8
            java.lang.String r8 = "SELinuxMMAC"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            r9.<init>()     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            java.lang.String r10 = "Using policy file "
            r9.append(r10)     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            r9.append(r7)     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            java.lang.String r9 = r9.toString()     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            android.util.Slog.d(r8, r9)     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            r3.setInput(r6)     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            r3.nextTag()     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            r8 = 0
            java.lang.String r9 = "policy"
            r10 = 2
            r3.require(r10, r8, r9)     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
        L_0x0053:
            int r8 = r3.next()     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            r9 = 3
            if (r8 == r9) goto L_0x0089
            int r8 = r3.getEventType()     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            if (r8 == r10) goto L_0x0061
            goto L_0x0053
        L_0x0061:
            java.lang.String r8 = r3.getName()     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            r9 = -1
            int r11 = r8.hashCode()     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            r12 = -902467798(0xffffffffca356f2a, float:-2972618.5)
            if (r11 == r12) goto L_0x0070
        L_0x006f:
            goto L_0x007a
        L_0x0070:
            java.lang.String r11 = "signer"
            boolean r8 = r8.equals(r11)     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            if (r8 == 0) goto L_0x006f
            r9 = r0
        L_0x007a:
            if (r9 == 0) goto L_0x0080
            skip(r3)     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            goto L_0x0088
        L_0x0080:
            com.android.server.pm.Policy r8 = readSignerOrThrow(r3)     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
            r1.add(r8)     // Catch:{ IllegalArgumentException | IllegalStateException | XmlPullParserException -> 0x00ae, IOException -> 0x0092 }
        L_0x0088:
            goto L_0x0053
        L_0x0089:
            libcore.io.IoUtils.closeQuietly(r6)
            int r5 = r5 + 1
            goto L_0x001e
        L_0x0090:
            r0 = move-exception
            goto L_0x00db
        L_0x0092:
            r2 = move-exception
            java.lang.String r8 = "SELinuxMMAC"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x0090 }
            r9.<init>()     // Catch:{ all -> 0x0090 }
            java.lang.String r10 = "Exception parsing "
            r9.append(r10)     // Catch:{ all -> 0x0090 }
            r9.append(r7)     // Catch:{ all -> 0x0090 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x0090 }
            android.util.Slog.w(r8, r9, r2)     // Catch:{ all -> 0x0090 }
            libcore.io.IoUtils.closeQuietly(r6)
            return r0
        L_0x00ae:
            r2 = move-exception
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0090 }
            java.lang.String r9 = "Exception @"
            r8.<init>(r9)     // Catch:{ all -> 0x0090 }
            java.lang.String r9 = r3.getPositionDescription()     // Catch:{ all -> 0x0090 }
            r8.append(r9)     // Catch:{ all -> 0x0090 }
            java.lang.String r9 = " while parsing "
            r8.append(r9)     // Catch:{ all -> 0x0090 }
            r8.append(r7)     // Catch:{ all -> 0x0090 }
            java.lang.String r9 = ":"
            r8.append(r9)     // Catch:{ all -> 0x0090 }
            r8.append(r2)     // Catch:{ all -> 0x0090 }
            java.lang.String r9 = "SELinuxMMAC"
            java.lang.String r10 = r8.toString()     // Catch:{ all -> 0x0090 }
            android.util.Slog.w(r9, r10)     // Catch:{ all -> 0x0090 }
            libcore.io.IoUtils.closeQuietly(r6)
            return r0
        L_0x00db:
            libcore.io.IoUtils.closeQuietly(r6)
            throw r0
        L_0x00df:
            com.android.server.pm.PolicyComparator r5 = new com.android.server.pm.PolicyComparator
            r5.<init>()
            java.util.Collections.sort(r1, r5)
            boolean r7 = r5.foundDuplicate()
            if (r7 == 0) goto L_0x00f5
            java.lang.String r2 = "SELinuxMMAC"
            java.lang.String r7 = "ERROR! Duplicate entries found parsing mac_permissions.xml files"
            android.util.Slog.w(r2, r7)
            return r0
        L_0x00f5:
            java.util.List<com.android.server.pm.Policy> r7 = sPolicies
            monitor-enter(r7)
            java.util.List<com.android.server.pm.Policy> r0 = sPolicies     // Catch:{ all -> 0x0106 }
            r0.clear()     // Catch:{ all -> 0x0106 }
            java.util.List<com.android.server.pm.Policy> r0 = sPolicies     // Catch:{ all -> 0x0106 }
            r0.addAll(r1)     // Catch:{ all -> 0x0106 }
            sPolicyRead = r2     // Catch:{ all -> 0x0106 }
            monitor-exit(r7)     // Catch:{ all -> 0x0106 }
            return r2
        L_0x0106:
            r0 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x0106 }
            throw r0
        L_0x0109:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0109 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.SELinuxMMAC.readInstallPolicy():boolean");
    }

    private static Policy readSignerOrThrow(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(2, (String) null, "signer");
        Policy.PolicyBuilder pb = new Policy.PolicyBuilder();
        String cert = parser.getAttributeValue((String) null, "signature");
        if (cert != null) {
            pb.addSignature(cert);
        }
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                String tagName = parser.getName();
                if ("seinfo".equals(tagName)) {
                    pb.setGlobalSeinfoOrThrow(parser.getAttributeValue((String) null, "value"));
                    readSeinfo(parser);
                } else if (Settings.ATTR_PACKAGE.equals(tagName)) {
                    readPackageOrThrow(parser, pb);
                } else if ("cert".equals(tagName)) {
                    pb.addSignature(parser.getAttributeValue((String) null, "signature"));
                    readCert(parser);
                } else {
                    skip(parser);
                }
            }
        }
        return pb.build();
    }

    private static void readPackageOrThrow(XmlPullParser parser, Policy.PolicyBuilder pb) throws IOException, XmlPullParserException {
        parser.require(2, (String) null, Settings.ATTR_PACKAGE);
        String pkgName = parser.getAttributeValue((String) null, Settings.ATTR_NAME);
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                if ("seinfo".equals(parser.getName())) {
                    pb.addInnerPackageMapOrThrow(pkgName, parser.getAttributeValue((String) null, "value"));
                    readSeinfo(parser);
                } else {
                    skip(parser);
                }
            }
        }
    }

    private static void readCert(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(2, (String) null, "cert");
        parser.nextTag();
    }

    private static void readSeinfo(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(2, (String) null, "seinfo");
        parser.nextTag();
    }

    private static void skip(XmlPullParser p) throws IOException, XmlPullParserException {
        if (p.getEventType() == 2) {
            int depth = 1;
            while (depth != 0) {
                int next = p.next();
                if (next == 2) {
                    depth++;
                } else if (next == 3) {
                    depth--;
                }
            }
            return;
        }
        throw new IllegalStateException();
    }

    public static String getSeInfo(PackageParser.Package pkg, boolean isPrivileged, int targetSandboxVersion, int targetSdkVersion) {
        String seInfo = null;
        synchronized (sPolicies) {
            if (sPolicyRead) {
                Iterator<Policy> it = sPolicies.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    seInfo = it.next().getMatchedSeInfo(pkg);
                    if (seInfo != null) {
                        break;
                    }
                }
            }
        }
        if (seInfo == null) {
            seInfo = "default";
        }
        if (targetSandboxVersion == 2) {
            seInfo = seInfo + SANDBOX_V2_STR;
        }
        if (isPrivileged) {
            seInfo = seInfo + PRIVILEGED_APP_STR;
        }
        return seInfo + TARGETSDKVERSION_STR + targetSdkVersion;
    }
}
