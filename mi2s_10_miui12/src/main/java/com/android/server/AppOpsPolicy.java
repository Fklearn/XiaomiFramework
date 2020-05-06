package com.android.server;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.server.am.SplitScreenReporter;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.util.XmlUtils;
import com.android.server.pm.Settings;
import com.android.server.voiceinteraction.DatabaseHelper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AppOpsPolicy {
    public static final int CONTROL_NOSHOW = 1;
    public static final int CONTROL_SHOW = 0;
    public static final int CONTROL_UNKNOWN = 2;
    static final boolean DEBUG = false;
    static final String TAG = "AppOpsPolicy";
    final Context mContext;
    final File mFile;
    HashMap<String, PolicyPkg> mPolicy = new HashMap<>();

    public static int stringToControl(String show) {
        if ("true".equalsIgnoreCase(show)) {
            return 0;
        }
        if ("false".equalsIgnoreCase(show)) {
            return 1;
        }
        return 2;
    }

    public AppOpsPolicy(File file, Context context) {
        this.mFile = file;
        this.mContext = context;
    }

    public static final class PolicyPkg extends SparseArray<PolicyOp> {
        public int mode;
        public String packageName;
        public int show;
        public String type;

        public PolicyPkg(String packageName2, int mode2, int show2, String type2) {
            this.packageName = packageName2;
            this.mode = mode2;
            this.show = show2;
            this.type = type2;
        }

        public String toString() {
            return "PolicyPkg [packageName=" + this.packageName + ", mode=" + this.mode + ", show=" + this.show + ", type=" + this.type + "]";
        }
    }

    public static final class PolicyOp {
        public int mode;
        public int op;
        public int show;

        public PolicyOp(int op2, int mode2, int show2) {
            this.op = op2;
            this.mode = mode2;
            this.show = show2;
        }

        public String toString() {
            return "PolicyOp [op=" + this.op + ", mode=" + this.mode + ", show=" + this.show + "]";
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x0172, code lost:
        if (r2 == false) goto L_0x0174;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:101:0x0174, code lost:
        r11.mPolicy.clear();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:103:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:?, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:107:0x0180, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:114:0x01a7, code lost:
        throw r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0097, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:34:0x0084, B:44:0x009d] */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0023 A[Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0084 A[SYNTHETIC, Splitter:B:34:0x0084] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x008e A[SYNTHETIC, Splitter:B:38:0x008e] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:67:0x0101=Splitter:B:67:0x0101, B:76:0x0124=Splitter:B:76:0x0124, B:85:0x0147=Splitter:B:85:0x0147, B:36:0x0089=Splitter:B:36:0x0089, B:49:0x00b9=Splitter:B:49:0x00b9, B:94:0x016a=Splitter:B:94:0x016a, B:58:0x00dd=Splitter:B:58:0x00dd} */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:97:0x0170=Splitter:B:97:0x0170, B:105:0x017f=Splitter:B:105:0x017f} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void readPolicy() {
        /*
            r11 = this;
            java.io.File r0 = r11.mFile
            monitor-enter(r0)
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x0182 }
            java.io.File r2 = r11.mFile     // Catch:{ FileNotFoundException -> 0x0182 }
            r1.<init>(r2)     // Catch:{ FileNotFoundException -> 0x0182 }
            r2 = 0
            org.xmlpull.v1.XmlPullParser r3 = android.util.Xml.newPullParser()     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            r4 = 0
            r3.setInput(r1, r4)     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            r2 = 1
        L_0x0015:
            int r4 = r3.next()     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            r5 = r4
            r6 = 1
            r7 = 2
            if (r4 == r7) goto L_0x0021
            if (r5 == r6) goto L_0x0021
            goto L_0x0015
        L_0x0021:
            if (r5 != r7) goto L_0x008e
            int r4 = r3.getDepth()     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
        L_0x0027:
            int r7 = r3.next()     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            r5 = r7
            if (r7 == r6) goto L_0x0082
            r7 = 3
            if (r5 != r7) goto L_0x0037
            int r8 = r3.getDepth()     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            if (r8 <= r4) goto L_0x0082
        L_0x0037:
            if (r5 == r7) goto L_0x0027
            r7 = 4
            if (r5 != r7) goto L_0x003d
            goto L_0x0027
        L_0x003d:
            java.lang.String r7 = r3.getName()     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            java.lang.String r8 = "user-app"
            boolean r8 = r7.equals(r8)     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            if (r8 != 0) goto L_0x007e
            java.lang.String r8 = "system-app"
            boolean r8 = r7.equals(r8)     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            if (r8 == 0) goto L_0x0054
            goto L_0x007e
        L_0x0054:
            java.lang.String r8 = "application"
            boolean r8 = r7.equals(r8)     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            if (r8 == 0) goto L_0x0060
            r11.readApplicationPolicy(r3)     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            goto L_0x0081
        L_0x0060:
            java.lang.String r8 = "AppOpsPolicy"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            r9.<init>()     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            java.lang.String r10 = "Unknown element under <appops-policy>: "
            r9.append(r10)     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            java.lang.String r10 = r3.getName()     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            r9.append(r10)     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            java.lang.String r9 = r9.toString()     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            android.util.Slog.w(r8, r9)     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            com.android.internal.util.XmlUtils.skipCurrentTag(r3)     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            goto L_0x0081
        L_0x007e:
            r11.readDefaultPolicy(r3, r7)     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
        L_0x0081:
            goto L_0x0027
        L_0x0082:
            if (r2 != 0) goto L_0x0089
            java.util.HashMap<java.lang.String, com.android.server.AppOpsPolicy$PolicyPkg> r3 = r11.mPolicy     // Catch:{ all -> 0x0180 }
            r3.clear()     // Catch:{ all -> 0x0180 }
        L_0x0089:
            r1.close()     // Catch:{ IOException -> 0x016e }
            goto L_0x016d
        L_0x008e:
            java.lang.IllegalStateException r4 = new java.lang.IllegalStateException     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            java.lang.String r6 = "no start tag found"
            r4.<init>(r6)     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
            throw r4     // Catch:{ IllegalStateException -> 0x014b, NullPointerException -> 0x0128, NumberFormatException -> 0x0105, XmlPullParserException -> 0x00e2, IOException -> 0x00be, IndexOutOfBoundsException -> 0x009a }
        L_0x0097:
            r3 = move-exception
            goto L_0x0172
        L_0x009a:
            r3 = move-exception
            java.lang.String r4 = "AppOpsPolicy"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0097 }
            r5.<init>()     // Catch:{ all -> 0x0097 }
            java.lang.String r6 = "Failed parsing "
            r5.append(r6)     // Catch:{ all -> 0x0097 }
            r5.append(r3)     // Catch:{ all -> 0x0097 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0097 }
            android.util.Slog.w(r4, r5)     // Catch:{ all -> 0x0097 }
            if (r2 != 0) goto L_0x00b9
            java.util.HashMap<java.lang.String, com.android.server.AppOpsPolicy$PolicyPkg> r3 = r11.mPolicy     // Catch:{ all -> 0x0180 }
            r3.clear()     // Catch:{ all -> 0x0180 }
        L_0x00b9:
            r1.close()     // Catch:{ IOException -> 0x016e }
            goto L_0x016d
        L_0x00be:
            r3 = move-exception
            java.lang.String r4 = "AppOpsPolicy"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0097 }
            r5.<init>()     // Catch:{ all -> 0x0097 }
            java.lang.String r6 = "Failed parsing "
            r5.append(r6)     // Catch:{ all -> 0x0097 }
            r5.append(r3)     // Catch:{ all -> 0x0097 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0097 }
            android.util.Slog.w(r4, r5)     // Catch:{ all -> 0x0097 }
            if (r2 != 0) goto L_0x00dd
            java.util.HashMap<java.lang.String, com.android.server.AppOpsPolicy$PolicyPkg> r3 = r11.mPolicy     // Catch:{ all -> 0x0180 }
            r3.clear()     // Catch:{ all -> 0x0180 }
        L_0x00dd:
            r1.close()     // Catch:{ IOException -> 0x016e }
            goto L_0x016d
        L_0x00e2:
            r3 = move-exception
            java.lang.String r4 = "AppOpsPolicy"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0097 }
            r5.<init>()     // Catch:{ all -> 0x0097 }
            java.lang.String r6 = "Failed parsing "
            r5.append(r6)     // Catch:{ all -> 0x0097 }
            r5.append(r3)     // Catch:{ all -> 0x0097 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0097 }
            android.util.Slog.w(r4, r5)     // Catch:{ all -> 0x0097 }
            if (r2 != 0) goto L_0x0101
            java.util.HashMap<java.lang.String, com.android.server.AppOpsPolicy$PolicyPkg> r3 = r11.mPolicy     // Catch:{ all -> 0x0180 }
            r3.clear()     // Catch:{ all -> 0x0180 }
        L_0x0101:
            r1.close()     // Catch:{ IOException -> 0x016e }
            goto L_0x016d
        L_0x0105:
            r3 = move-exception
            java.lang.String r4 = "AppOpsPolicy"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0097 }
            r5.<init>()     // Catch:{ all -> 0x0097 }
            java.lang.String r6 = "Failed parsing "
            r5.append(r6)     // Catch:{ all -> 0x0097 }
            r5.append(r3)     // Catch:{ all -> 0x0097 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0097 }
            android.util.Slog.w(r4, r5)     // Catch:{ all -> 0x0097 }
            if (r2 != 0) goto L_0x0124
            java.util.HashMap<java.lang.String, com.android.server.AppOpsPolicy$PolicyPkg> r3 = r11.mPolicy     // Catch:{ all -> 0x0180 }
            r3.clear()     // Catch:{ all -> 0x0180 }
        L_0x0124:
            r1.close()     // Catch:{ IOException -> 0x016e }
            goto L_0x016d
        L_0x0128:
            r3 = move-exception
            java.lang.String r4 = "AppOpsPolicy"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0097 }
            r5.<init>()     // Catch:{ all -> 0x0097 }
            java.lang.String r6 = "Failed parsing "
            r5.append(r6)     // Catch:{ all -> 0x0097 }
            r5.append(r3)     // Catch:{ all -> 0x0097 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0097 }
            android.util.Slog.w(r4, r5)     // Catch:{ all -> 0x0097 }
            if (r2 != 0) goto L_0x0147
            java.util.HashMap<java.lang.String, com.android.server.AppOpsPolicy$PolicyPkg> r3 = r11.mPolicy     // Catch:{ all -> 0x0180 }
            r3.clear()     // Catch:{ all -> 0x0180 }
        L_0x0147:
            r1.close()     // Catch:{ IOException -> 0x016e }
            goto L_0x016d
        L_0x014b:
            r3 = move-exception
            java.lang.String r4 = "AppOpsPolicy"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0097 }
            r5.<init>()     // Catch:{ all -> 0x0097 }
            java.lang.String r6 = "Failed parsing "
            r5.append(r6)     // Catch:{ all -> 0x0097 }
            r5.append(r3)     // Catch:{ all -> 0x0097 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0097 }
            android.util.Slog.w(r4, r5)     // Catch:{ all -> 0x0097 }
            if (r2 != 0) goto L_0x016a
            java.util.HashMap<java.lang.String, com.android.server.AppOpsPolicy$PolicyPkg> r3 = r11.mPolicy     // Catch:{ all -> 0x0180 }
            r3.clear()     // Catch:{ all -> 0x0180 }
        L_0x016a:
            r1.close()     // Catch:{ IOException -> 0x016e }
        L_0x016d:
            goto L_0x0170
        L_0x016e:
            r3 = move-exception
        L_0x0170:
            monitor-exit(r0)     // Catch:{ all -> 0x0180 }
            return
        L_0x0172:
            if (r2 != 0) goto L_0x0179
            java.util.HashMap<java.lang.String, com.android.server.AppOpsPolicy$PolicyPkg> r4 = r11.mPolicy     // Catch:{ all -> 0x0180 }
            r4.clear()     // Catch:{ all -> 0x0180 }
        L_0x0179:
            r1.close()     // Catch:{ IOException -> 0x017d }
            goto L_0x017e
        L_0x017d:
            r4 = move-exception
        L_0x017e:
            throw r3     // Catch:{ all -> 0x0180 }
        L_0x0180:
            r1 = move-exception
            goto L_0x01a6
        L_0x0182:
            r1 = move-exception
            java.lang.String r2 = "AppOpsPolicy"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0180 }
            r3.<init>()     // Catch:{ all -> 0x0180 }
            java.lang.String r4 = "App ops policy file ("
            r3.append(r4)     // Catch:{ all -> 0x0180 }
            java.io.File r4 = r11.mFile     // Catch:{ all -> 0x0180 }
            java.lang.String r4 = r4.getPath()     // Catch:{ all -> 0x0180 }
            r3.append(r4)     // Catch:{ all -> 0x0180 }
            java.lang.String r4 = ") not found; Skipping."
            r3.append(r4)     // Catch:{ all -> 0x0180 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0180 }
            android.util.Slog.i(r2, r3)     // Catch:{ all -> 0x0180 }
            monitor-exit(r0)     // Catch:{ all -> 0x0180 }
            return
        L_0x01a6:
            monitor-exit(r0)     // Catch:{ all -> 0x0180 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.AppOpsPolicy.readPolicy():void");
    }

    private void readDefaultPolicy(XmlPullParser parser, String packageName) throws NumberFormatException, XmlPullParserException, IOException {
        if ("user-app".equalsIgnoreCase(packageName) || "system-app".equalsIgnoreCase(packageName)) {
            int show = stringToControl(parser.getAttributeValue((String) null, "show"));
            if (0 != 2 || show != 2) {
                PolicyPkg pkg = this.mPolicy.get(packageName);
                if (pkg == null) {
                    this.mPolicy.put(packageName, new PolicyPkg(packageName, 0, show, packageName));
                    return;
                }
                Slog.w(TAG, "Duplicate policy found for package: " + packageName + " of type: " + packageName);
                pkg.mode = 0;
                pkg.show = show;
            }
        }
    }

    private void readApplicationPolicy(XmlPullParser parser) throws NumberFormatException, XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
            if (!(type == 3 || type == 4)) {
                if (parser.getName().equals(SplitScreenReporter.STR_PKG)) {
                    readPkgPolicy(parser);
                } else {
                    Slog.w(TAG, "Unknown element under <application>: " + parser.getName());
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
    }

    private void readPkgPolicy(XmlPullParser parser) throws NumberFormatException, XmlPullParserException, IOException {
        String appType;
        String packageName = parser.getAttributeValue((String) null, Settings.ATTR_NAME);
        if (packageName != null && (appType = parser.getAttributeValue((String) null, DatabaseHelper.SoundModelContract.KEY_TYPE)) != null) {
            int show = stringToControl(parser.getAttributeValue((String) null, "show"));
            String key = packageName + "." + appType;
            PolicyPkg pkg = this.mPolicy.get(key);
            if (pkg == null) {
                pkg = new PolicyPkg(packageName, 0, show, appType);
                this.mPolicy.put(key, pkg);
            } else {
                Slog.w(TAG, "Duplicate policy found for package: " + packageName + " of type: " + appType);
                pkg.mode = 0;
                pkg.show = show;
            }
            int outerDepth = parser.getDepth();
            while (true) {
                int next = parser.next();
                int type = next;
                if (next == 1) {
                    return;
                }
                if (type == 3 && parser.getDepth() <= outerDepth) {
                    return;
                }
                if (!(type == 3 || type == 4)) {
                    if (parser.getName().equals("op")) {
                        readOpPolicy(parser, pkg);
                    } else {
                        Slog.w(TAG, "Unknown element under <pkg>: " + parser.getName());
                        XmlUtils.skipCurrentTag(parser);
                    }
                }
            }
        }
    }

    private void readOpPolicy(XmlPullParser parser, PolicyPkg pkg) throws NumberFormatException, XmlPullParserException, IOException {
        if (pkg != null) {
            String opName = parser.getAttributeValue((String) null, Settings.ATTR_NAME);
            if (opName == null) {
                Slog.w(TAG, "Op name is null");
            } else if (-1 == -1) {
                Slog.w(TAG, "Unknown Op: " + opName);
            } else {
                int show = stringToControl(parser.getAttributeValue((String) null, "show"));
                if (0 != 2 || show != 2) {
                    PolicyOp op = (PolicyOp) pkg.get(-1);
                    if (op == null) {
                        pkg.put(-1, new PolicyOp(-1, 0, show));
                        return;
                    }
                    Slog.w(TAG, "Duplicate policy found for package: " + pkg.packageName + " type: " + pkg.type + " op: " + op.op);
                    op.mode = 0;
                    op.show = show;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void debugPoilcy() {
        for (Map.Entry<String, PolicyPkg> key : this.mPolicy.entrySet()) {
            PolicyPkg pkg = this.mPolicy.get((String) key.getKey());
            if (pkg != null) {
                for (int i = 0; i < pkg.size(); i++) {
                    PolicyOp policyOp = (PolicyOp) pkg.valueAt(i);
                }
            }
        }
    }

    private String getAppType(String packageName) {
        ApplicationInfo appInfo;
        Context context = this.mContext;
        if (context != null) {
            try {
                appInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                appInfo = null;
            }
            if (appInfo == null) {
                return null;
            }
            if ((appInfo.flags & 1) != 0) {
                return "system-app";
            }
            return "user-app";
        }
        Slog.e(TAG, "Context is null");
        return null;
    }

    public boolean isControlAllowed(int code, String packageName) {
        PolicyPkg pkg;
        int show = 2;
        if (this.mPolicy == null) {
            return true;
        }
        String type = getAppType(packageName);
        if (!(type == null || (pkg = this.mPolicy.get(type)) == null || pkg.show == 2)) {
            show = pkg.show;
        }
        String key = packageName;
        if (type != null) {
            key = key + "." + type;
        }
        PolicyPkg pkg2 = this.mPolicy.get(key);
        if (pkg2 != null) {
            if (pkg2.show != 2) {
                show = pkg2.show;
            }
            PolicyOp op = (PolicyOp) pkg2.get(code);
            if (!(op == null || op.show == 2)) {
                show = op.show;
            }
        }
        if (show == 1) {
            return false;
        }
        return true;
    }

    public int getDefualtMode(int code, String packageName) {
        PolicyPkg pkg;
        int mode = 2;
        if (this.mPolicy == null) {
            return 2;
        }
        String type = getAppType(packageName);
        if (!(type == null || (pkg = this.mPolicy.get(type)) == null || pkg.mode == 2)) {
            mode = pkg.mode;
        }
        String key = packageName;
        if (type != null) {
            key = key + "." + type;
        }
        PolicyPkg pkg2 = this.mPolicy.get(key);
        if (pkg2 == null) {
            return mode;
        }
        if (pkg2.mode != 2) {
            mode = pkg2.mode;
        }
        PolicyOp op = (PolicyOp) pkg2.get(code);
        if (op == null || op.mode == 2) {
            return mode;
        }
        return op.mode;
    }
}
