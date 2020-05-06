package com.android.server.pm;

import android.content.ComponentName;
import android.metrics.LogMaker;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.Preconditions;
import com.android.server.pm.ShortcutService;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.function.Consumer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class ShortcutUser {
    private static final String ATTR_KNOWN_LOCALES = "locales";
    private static final String ATTR_LAST_APP_SCAN_OS_FINGERPRINT = "last-app-scan-fp";
    private static final String ATTR_LAST_APP_SCAN_TIME = "last-app-scan-time2";
    private static final String ATTR_RESTORE_SOURCE_FINGERPRINT = "restore-from-fp";
    private static final String ATTR_VALUE = "value";
    private static final String KEY_LAUNCHERS = "launchers";
    private static final String KEY_PACKAGES = "packages";
    private static final String KEY_USER_ID = "userId";
    private static final String TAG = "ShortcutService";
    private static final String TAG_LAUNCHER = "launcher";
    static final String TAG_ROOT = "user";
    private ComponentName mCachedLauncher;
    private String mKnownLocales;
    private String mLastAppScanOsFingerprint;
    private long mLastAppScanTime;
    private ComponentName mLastKnownLauncher;
    private final ArrayMap<PackageWithUser, ShortcutLauncher> mLaunchers = new ArrayMap<>();
    private final ArrayMap<String, ShortcutPackage> mPackages = new ArrayMap<>();
    private String mRestoreFromOsFingerprint;
    final ShortcutService mService;
    private final int mUserId;

    static final class PackageWithUser {
        final String packageName;
        final int userId;

        private PackageWithUser(int userId2, String packageName2) {
            this.userId = userId2;
            this.packageName = (String) Preconditions.checkNotNull(packageName2);
        }

        public static PackageWithUser of(int userId2, String packageName2) {
            return new PackageWithUser(userId2, packageName2);
        }

        public static PackageWithUser of(ShortcutPackageItem spi) {
            return new PackageWithUser(spi.getPackageUserId(), spi.getPackageName());
        }

        public int hashCode() {
            return this.packageName.hashCode() ^ this.userId;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof PackageWithUser)) {
                return false;
            }
            PackageWithUser that = (PackageWithUser) obj;
            if (this.userId != that.userId || !this.packageName.equals(that.packageName)) {
                return false;
            }
            return true;
        }

        public String toString() {
            return String.format("[Package: %d, %s]", new Object[]{Integer.valueOf(this.userId), this.packageName});
        }
    }

    public ShortcutUser(ShortcutService service, int userId) {
        this.mService = service;
        this.mUserId = userId;
    }

    public int getUserId() {
        return this.mUserId;
    }

    public long getLastAppScanTime() {
        return this.mLastAppScanTime;
    }

    public void setLastAppScanTime(long lastAppScanTime) {
        this.mLastAppScanTime = lastAppScanTime;
    }

    public String getLastAppScanOsFingerprint() {
        return this.mLastAppScanOsFingerprint;
    }

    public void setLastAppScanOsFingerprint(String lastAppScanOsFingerprint) {
        this.mLastAppScanOsFingerprint = lastAppScanOsFingerprint;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public ArrayMap<String, ShortcutPackage> getAllPackagesForTest() {
        return this.mPackages;
    }

    public boolean hasPackage(String packageName) {
        return this.mPackages.containsKey(packageName);
    }

    private void addPackage(ShortcutPackage p) {
        p.replaceUser(this);
        this.mPackages.put(p.getPackageName(), p);
    }

    public ShortcutPackage removePackage(String packageName) {
        ShortcutPackage removed = this.mPackages.remove(packageName);
        this.mService.cleanupBitmapsForPackage(this.mUserId, packageName);
        return removed;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public ArrayMap<PackageWithUser, ShortcutLauncher> getAllLaunchersForTest() {
        return this.mLaunchers;
    }

    private void addLauncher(ShortcutLauncher launcher) {
        launcher.replaceUser(this);
        this.mLaunchers.put(PackageWithUser.of(launcher.getPackageUserId(), launcher.getPackageName()), launcher);
    }

    public ShortcutLauncher removeLauncher(int packageUserId, String packageName) {
        return this.mLaunchers.remove(PackageWithUser.of(packageUserId, packageName));
    }

    public ShortcutPackage getPackageShortcutsIfExists(String packageName) {
        ShortcutPackage ret = this.mPackages.get(packageName);
        if (ret != null) {
            ret.attemptToRestoreIfNeededAndSave();
        }
        return ret;
    }

    public ShortcutPackage getPackageShortcuts(String packageName) {
        ShortcutPackage ret = getPackageShortcutsIfExists(packageName);
        if (ret != null) {
            return ret;
        }
        ShortcutPackage ret2 = new ShortcutPackage(this, this.mUserId, packageName);
        this.mPackages.put(packageName, ret2);
        return ret2;
    }

    public ShortcutLauncher getLauncherShortcuts(String packageName, int launcherUserId) {
        PackageWithUser key = PackageWithUser.of(launcherUserId, packageName);
        ShortcutLauncher ret = this.mLaunchers.get(key);
        if (ret == null) {
            ShortcutLauncher ret2 = new ShortcutLauncher(this, this.mUserId, packageName, launcherUserId);
            this.mLaunchers.put(key, ret2);
            return ret2;
        }
        ret.attemptToRestoreIfNeededAndSave();
        return ret;
    }

    public void forAllPackages(Consumer<? super ShortcutPackage> callback) {
        int size = this.mPackages.size();
        for (int i = 0; i < size; i++) {
            callback.accept(this.mPackages.valueAt(i));
        }
    }

    public void forAllLaunchers(Consumer<? super ShortcutLauncher> callback) {
        int size = this.mLaunchers.size();
        for (int i = 0; i < size; i++) {
            callback.accept(this.mLaunchers.valueAt(i));
        }
    }

    public void forAllPackageItems(Consumer<? super ShortcutPackageItem> callback) {
        forAllLaunchers(callback);
        forAllPackages(callback);
    }

    public void forPackageItem(String packageName, int packageUserId, Consumer<ShortcutPackageItem> callback) {
        forAllPackageItems(new Consumer(packageUserId, packageName, callback) {
            private final /* synthetic */ int f$0;
            private final /* synthetic */ String f$1;
            private final /* synthetic */ Consumer f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void accept(Object obj) {
                ShortcutUser.lambda$forPackageItem$0(this.f$0, this.f$1, this.f$2, (ShortcutPackageItem) obj);
            }
        });
    }

    static /* synthetic */ void lambda$forPackageItem$0(int packageUserId, String packageName, Consumer callback, ShortcutPackageItem spi) {
        if (spi.getPackageUserId() == packageUserId && spi.getPackageName().equals(packageName)) {
            callback.accept(spi);
        }
    }

    public void onCalledByPublisher(String packageName) {
        detectLocaleChange();
        rescanPackageIfNeeded(packageName, false);
    }

    private String getKnownLocales() {
        if (TextUtils.isEmpty(this.mKnownLocales)) {
            this.mKnownLocales = this.mService.injectGetLocaleTagsForUser(this.mUserId);
            this.mService.scheduleSaveUser(this.mUserId);
        }
        return this.mKnownLocales;
    }

    public void detectLocaleChange() {
        String currentLocales = this.mService.injectGetLocaleTagsForUser(this.mUserId);
        if (TextUtils.isEmpty(this.mKnownLocales) || !this.mKnownLocales.equals(currentLocales)) {
            this.mKnownLocales = currentLocales;
            forAllPackages($$Lambda$ShortcutUser$6rBk7xJFaM9dXyyKHFsDCus0iM.INSTANCE);
            this.mService.scheduleSaveUser(this.mUserId);
        }
    }

    static /* synthetic */ void lambda$detectLocaleChange$1(ShortcutPackage pkg) {
        pkg.resetRateLimiting();
        pkg.resolveResourceStrings();
    }

    public void rescanPackageIfNeeded(String packageName, boolean forceRescan) {
        boolean isNewApp = !this.mPackages.containsKey(packageName);
        if (!getPackageShortcuts(packageName).rescanPackageIfNeeded(isNewApp, forceRescan) && isNewApp) {
            this.mPackages.remove(packageName);
        }
    }

    public void attemptToRestoreIfNeededAndSave(ShortcutService s, String packageName, int packageUserId) {
        forPackageItem(packageName, packageUserId, $$Lambda$ShortcutUser$bsc89E_40a5X2amehalpqawQ5hY.INSTANCE);
    }

    public void saveToXml(XmlSerializer out, boolean forBackup) throws IOException, XmlPullParserException {
        out.startTag((String) null, TAG_ROOT);
        if (!forBackup) {
            ShortcutService.writeAttr(out, ATTR_KNOWN_LOCALES, (CharSequence) this.mKnownLocales);
            ShortcutService.writeAttr(out, ATTR_LAST_APP_SCAN_TIME, this.mLastAppScanTime);
            ShortcutService.writeAttr(out, ATTR_LAST_APP_SCAN_OS_FINGERPRINT, (CharSequence) this.mLastAppScanOsFingerprint);
            ShortcutService.writeAttr(out, ATTR_RESTORE_SOURCE_FINGERPRINT, (CharSequence) this.mRestoreFromOsFingerprint);
            ShortcutService.writeTagValue(out, TAG_LAUNCHER, this.mLastKnownLauncher);
        } else {
            ShortcutService.writeAttr(out, ATTR_RESTORE_SOURCE_FINGERPRINT, (CharSequence) this.mService.injectBuildFingerprint());
        }
        int size = this.mLaunchers.size();
        for (int i = 0; i < size; i++) {
            saveShortcutPackageItem(out, this.mLaunchers.valueAt(i), forBackup);
        }
        int size2 = this.mPackages.size();
        for (int i2 = 0; i2 < size2; i2++) {
            saveShortcutPackageItem(out, this.mPackages.valueAt(i2), forBackup);
        }
        out.endTag((String) null, TAG_ROOT);
    }

    private void saveShortcutPackageItem(XmlSerializer out, ShortcutPackageItem spi, boolean forBackup) throws IOException, XmlPullParserException {
        if (!forBackup || spi.getPackageUserId() == spi.getOwnerUserId()) {
            spi.saveToXml(out, forBackup);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x009b A[Catch:{ RuntimeException -> 0x00c9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00b8 A[Catch:{ RuntimeException -> 0x00c9 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.android.server.pm.ShortcutUser loadFromXml(com.android.server.pm.ShortcutService r16, org.xmlpull.v1.XmlPullParser r17, int r18, boolean r19) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException, com.android.server.pm.ShortcutService.InvalidFileFormatException {
        /*
            r1 = r16
            r2 = r17
            r3 = r18
            r4 = r19
            com.android.server.pm.ShortcutUser r0 = new com.android.server.pm.ShortcutUser
            r0.<init>(r1, r3)
            r5 = r0
            java.lang.String r0 = "locales"
            java.lang.String r0 = com.android.server.pm.ShortcutService.parseStringAttribute(r2, r0)     // Catch:{ RuntimeException -> 0x00c9 }
            r5.mKnownLocales = r0     // Catch:{ RuntimeException -> 0x00c9 }
            java.lang.String r0 = "last-app-scan-time2"
            long r6 = com.android.server.pm.ShortcutService.parseLongAttribute(r2, r0)     // Catch:{ RuntimeException -> 0x00c9 }
            long r8 = r16.injectCurrentTimeMillis()     // Catch:{ RuntimeException -> 0x00c9 }
            int r0 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r0 >= 0) goto L_0x0028
            r10 = r6
            goto L_0x002a
        L_0x0028:
            r10 = 0
        L_0x002a:
            r5.mLastAppScanTime = r10     // Catch:{ RuntimeException -> 0x00c9 }
            java.lang.String r0 = "last-app-scan-fp"
            java.lang.String r0 = com.android.server.pm.ShortcutService.parseStringAttribute(r2, r0)     // Catch:{ RuntimeException -> 0x00c9 }
            r5.mLastAppScanOsFingerprint = r0     // Catch:{ RuntimeException -> 0x00c9 }
            java.lang.String r0 = "restore-from-fp"
            java.lang.String r0 = com.android.server.pm.ShortcutService.parseStringAttribute(r2, r0)     // Catch:{ RuntimeException -> 0x00c9 }
            r5.mRestoreFromOsFingerprint = r0     // Catch:{ RuntimeException -> 0x00c9 }
            int r0 = r17.getDepth()     // Catch:{ RuntimeException -> 0x00c9 }
        L_0x0042:
            int r10 = r17.next()     // Catch:{ RuntimeException -> 0x00c9 }
            r11 = r10
            r12 = 1
            if (r10 == r12) goto L_0x00c7
            r10 = 3
            if (r11 != r10) goto L_0x0053
            int r10 = r17.getDepth()     // Catch:{ RuntimeException -> 0x00c9 }
            if (r10 <= r0) goto L_0x00c7
        L_0x0053:
            r10 = 2
            if (r11 == r10) goto L_0x0057
            goto L_0x0042
        L_0x0057:
            int r13 = r17.getDepth()     // Catch:{ RuntimeException -> 0x00c9 }
            java.lang.String r14 = r17.getName()     // Catch:{ RuntimeException -> 0x00c9 }
            int r15 = r0 + 1
            if (r13 != r15) goto L_0x00c2
            int r15 = r14.hashCode()     // Catch:{ RuntimeException -> 0x00c9 }
            r10 = -1407250528(0xffffffffac1f0fa0, float:-2.2603933E-12)
            if (r15 == r10) goto L_0x008d
            r10 = -1146595445(0xffffffffbba8578b, float:-0.005137389)
            if (r15 == r10) goto L_0x0082
            r10 = -807062458(0xffffffffcfe53446, float:-7.6908165E9)
            if (r15 == r10) goto L_0x0077
        L_0x0076:
            goto L_0x0098
        L_0x0077:
            java.lang.String r10 = "package"
            boolean r10 = r14.equals(r10)     // Catch:{ RuntimeException -> 0x00c9 }
            if (r10 == 0) goto L_0x0076
            r10 = r12
            goto L_0x0099
        L_0x0082:
            java.lang.String r10 = "launcher-pins"
            boolean r10 = r14.equals(r10)     // Catch:{ RuntimeException -> 0x00c9 }
            if (r10 == 0) goto L_0x0076
            r10 = 2
            goto L_0x0099
        L_0x008d:
            java.lang.String r10 = "launcher"
            boolean r10 = r14.equals(r10)     // Catch:{ RuntimeException -> 0x00c9 }
            if (r10 == 0) goto L_0x0076
            r10 = 0
            goto L_0x0099
        L_0x0098:
            r10 = -1
        L_0x0099:
            if (r10 == 0) goto L_0x00b8
            if (r10 == r12) goto L_0x00aa
            r12 = 2
            if (r10 == r12) goto L_0x00a1
            goto L_0x00c2
        L_0x00a1:
            com.android.server.pm.ShortcutLauncher r10 = com.android.server.pm.ShortcutLauncher.loadFromXml(r2, r5, r3, r4)     // Catch:{ RuntimeException -> 0x00c9 }
            r5.addLauncher(r10)     // Catch:{ RuntimeException -> 0x00c9 }
            goto L_0x0042
        L_0x00aa:
            com.android.server.pm.ShortcutPackage r10 = com.android.server.pm.ShortcutPackage.loadFromXml(r1, r5, r2, r4)     // Catch:{ RuntimeException -> 0x00c9 }
            android.util.ArrayMap<java.lang.String, com.android.server.pm.ShortcutPackage> r12 = r5.mPackages     // Catch:{ RuntimeException -> 0x00c9 }
            java.lang.String r15 = r10.getPackageName()     // Catch:{ RuntimeException -> 0x00c9 }
            r12.put(r15, r10)     // Catch:{ RuntimeException -> 0x00c9 }
            goto L_0x0042
        L_0x00b8:
            java.lang.String r10 = "value"
            android.content.ComponentName r10 = com.android.server.pm.ShortcutService.parseComponentNameAttribute(r2, r10)     // Catch:{ RuntimeException -> 0x00c9 }
            r5.mLastKnownLauncher = r10     // Catch:{ RuntimeException -> 0x00c9 }
            goto L_0x0042
        L_0x00c2:
            com.android.server.pm.ShortcutService.warnForInvalidTag(r13, r14)     // Catch:{ RuntimeException -> 0x00c9 }
            goto L_0x0042
        L_0x00c7:
            return r5
        L_0x00c9:
            r0 = move-exception
            com.android.server.pm.ShortcutService$InvalidFileFormatException r6 = new com.android.server.pm.ShortcutService$InvalidFileFormatException
            java.lang.String r7 = "Unable to parse file"
            r6.<init>(r7, r0)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.ShortcutUser.loadFromXml(com.android.server.pm.ShortcutService, org.xmlpull.v1.XmlPullParser, int, boolean):com.android.server.pm.ShortcutUser");
    }

    public ComponentName getLastKnownLauncher() {
        return this.mLastKnownLauncher;
    }

    public void setLauncher(ComponentName launcherComponent) {
        setLauncher(launcherComponent, false);
    }

    public void clearLauncher() {
        setLauncher((ComponentName) null);
    }

    public void forceClearLauncher() {
        setLauncher((ComponentName) null, true);
    }

    private void setLauncher(ComponentName launcherComponent, boolean allowPurgeLastKnown) {
        this.mCachedLauncher = launcherComponent;
        if (!Objects.equals(this.mLastKnownLauncher, launcherComponent)) {
            if (allowPurgeLastKnown || launcherComponent != null) {
                this.mLastKnownLauncher = launcherComponent;
                this.mService.scheduleSaveUser(this.mUserId);
            }
        }
    }

    public ComponentName getCachedLauncher() {
        return this.mCachedLauncher;
    }

    public void resetThrottling() {
        for (int i = this.mPackages.size() - 1; i >= 0; i--) {
            this.mPackages.valueAt(i).resetThrottling();
        }
    }

    public void mergeRestoredFile(ShortcutUser restored) {
        ShortcutService s = this.mService;
        int[] restoredLaunchers = new int[1];
        int[] restoredPackages = new int[1];
        int[] restoredShortcuts = new int[1];
        this.mLaunchers.clear();
        restored.forAllLaunchers(new Consumer(s, restoredLaunchers) {
            private final /* synthetic */ ShortcutService f$1;
            private final /* synthetic */ int[] f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void accept(Object obj) {
                ShortcutUser.this.lambda$mergeRestoredFile$3$ShortcutUser(this.f$1, this.f$2, (ShortcutLauncher) obj);
            }
        });
        restored.forAllPackages(new Consumer(s, restoredPackages, restoredShortcuts) {
            private final /* synthetic */ ShortcutService f$1;
            private final /* synthetic */ int[] f$2;
            private final /* synthetic */ int[] f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void accept(Object obj) {
                ShortcutUser.this.lambda$mergeRestoredFile$4$ShortcutUser(this.f$1, this.f$2, this.f$3, (ShortcutPackage) obj);
            }
        });
        restored.mLaunchers.clear();
        restored.mPackages.clear();
        this.mRestoreFromOsFingerprint = restored.mRestoreFromOsFingerprint;
        Slog.i(TAG, "Restored: L=" + restoredLaunchers[0] + " P=" + restoredPackages[0] + " S=" + restoredShortcuts[0]);
    }

    public /* synthetic */ void lambda$mergeRestoredFile$3$ShortcutUser(ShortcutService s, int[] restoredLaunchers, ShortcutLauncher sl) {
        if (!s.isPackageInstalled(sl.getPackageName(), getUserId()) || s.shouldBackupApp(sl.getPackageName(), getUserId())) {
            addLauncher(sl);
            restoredLaunchers[0] = restoredLaunchers[0] + 1;
        }
    }

    public /* synthetic */ void lambda$mergeRestoredFile$4$ShortcutUser(ShortcutService s, int[] restoredPackages, int[] restoredShortcuts, ShortcutPackage sp) {
        if (!s.isPackageInstalled(sp.getPackageName(), getUserId()) || s.shouldBackupApp(sp.getPackageName(), getUserId())) {
            ShortcutPackage previous = getPackageShortcutsIfExists(sp.getPackageName());
            if (previous != null && previous.hasNonManifestShortcuts()) {
                Log.w(TAG, "Shortcuts for package " + sp.getPackageName() + " are being restored. Existing non-manifeset shortcuts will be overwritten.");
            }
            addPackage(sp);
            restoredPackages[0] = restoredPackages[0] + 1;
            restoredShortcuts[0] = restoredShortcuts[0] + sp.getShortcutCount();
        }
    }

    public void dump(PrintWriter pw, String prefix, ShortcutService.DumpFilter filter) {
        if (filter.shouldDumpDetails()) {
            pw.print(prefix);
            pw.print("User: ");
            pw.print(this.mUserId);
            pw.print("  Known locales: ");
            pw.print(this.mKnownLocales);
            pw.print("  Last app scan: [");
            pw.print(this.mLastAppScanTime);
            pw.print("] ");
            pw.println(ShortcutService.formatTime(this.mLastAppScanTime));
            prefix = prefix + prefix + "  ";
            pw.print(prefix);
            pw.print("Last app scan FP: ");
            pw.println(this.mLastAppScanOsFingerprint);
            pw.print(prefix);
            pw.print("Restore from FP: ");
            pw.print(this.mRestoreFromOsFingerprint);
            pw.println();
            pw.print(prefix);
            pw.print("Cached launcher: ");
            pw.print(this.mCachedLauncher);
            pw.println();
            pw.print(prefix);
            pw.print("Last known launcher: ");
            pw.print(this.mLastKnownLauncher);
            pw.println();
        }
        for (int i = 0; i < this.mLaunchers.size(); i++) {
            ShortcutLauncher launcher = this.mLaunchers.valueAt(i);
            if (filter.isPackageMatch(launcher.getPackageName())) {
                launcher.dump(pw, prefix, filter);
            }
        }
        for (int i2 = 0; i2 < this.mPackages.size(); i2++) {
            ShortcutPackage pkg = this.mPackages.valueAt(i2);
            if (filter.isPackageMatch(pkg.getPackageName())) {
                pkg.dump(pw, prefix, filter);
            }
        }
        if (filter.shouldDumpDetails() != 0) {
            pw.println();
            pw.print(prefix);
            pw.println("Bitmap directories: ");
            dumpDirectorySize(pw, prefix + "  ", this.mService.getUserBitmapFilePath(this.mUserId));
        }
    }

    private void dumpDirectorySize(PrintWriter pw, String prefix, File path) {
        int numFiles = 0;
        long size = 0;
        if (path.listFiles() != null) {
            for (File child : path.listFiles()) {
                if (child.isFile()) {
                    numFiles++;
                    size += child.length();
                } else if (child.isDirectory()) {
                    dumpDirectorySize(pw, prefix + "  ", child);
                }
            }
        }
        pw.print(prefix);
        pw.print("Path: ");
        pw.print(path.getName());
        pw.print("/ has ");
        pw.print(numFiles);
        pw.print(" files, size=");
        pw.print(size);
        pw.print(" (");
        pw.print(Formatter.formatFileSize(this.mService.mContext, size));
        pw.println(")");
    }

    public JSONObject dumpCheckin(boolean clear) throws JSONException {
        JSONObject result = new JSONObject();
        result.put(KEY_USER_ID, this.mUserId);
        JSONArray launchers = new JSONArray();
        for (int i = 0; i < this.mLaunchers.size(); i++) {
            launchers.put(this.mLaunchers.valueAt(i).dumpCheckin(clear));
        }
        result.put(KEY_LAUNCHERS, launchers);
        JSONArray packages = new JSONArray();
        for (int i2 = 0; i2 < this.mPackages.size(); i2++) {
            packages.put(this.mPackages.valueAt(i2).dumpCheckin(clear));
        }
        result.put(KEY_PACKAGES, packages);
        return result;
    }

    /* access modifiers changed from: package-private */
    public void logSharingShortcutStats(MetricsLogger logger) {
        int packageWithShareTargetsCount = 0;
        int totalSharingShortcutCount = 0;
        for (int i = 0; i < this.mPackages.size(); i++) {
            if (this.mPackages.valueAt(i).hasShareTargets()) {
                packageWithShareTargetsCount++;
                totalSharingShortcutCount += this.mPackages.valueAt(i).getSharingShortcutCount();
            }
        }
        LogMaker logMaker = new LogMaker(1717);
        logger.write(logMaker.setType(1).setSubtype(this.mUserId));
        logger.write(logMaker.setType(2).setSubtype(packageWithShareTargetsCount));
        logger.write(logMaker.setType(3).setSubtype(totalSharingShortcutCount));
    }
}
