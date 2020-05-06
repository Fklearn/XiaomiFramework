package com.android.server.pm;

import android.content.Intent;
import android.content.pm.InstantAppInfo;
import android.content.pm.PackageParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.PackageUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.XmlUtils;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.InstantAppRegistry;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class InstantAppRegistry {
    private static final String ATTR_GRANTED = "granted";
    private static final String ATTR_LABEL = "label";
    private static final String ATTR_NAME = "name";
    private static final boolean DEBUG = false;
    private static final long DEFAULT_INSTALLED_INSTANT_APP_MAX_CACHE_PERIOD = 15552000000L;
    static final long DEFAULT_INSTALLED_INSTANT_APP_MIN_CACHE_PERIOD = 604800000;
    private static final long DEFAULT_UNINSTALLED_INSTANT_APP_MAX_CACHE_PERIOD = 15552000000L;
    static final long DEFAULT_UNINSTALLED_INSTANT_APP_MIN_CACHE_PERIOD = 604800000;
    private static final String INSTANT_APPS_FOLDER = "instant";
    private static final String INSTANT_APP_ANDROID_ID_FILE = "android_id";
    private static final String INSTANT_APP_COOKIE_FILE_PREFIX = "cookie_";
    private static final String INSTANT_APP_COOKIE_FILE_SIFFIX = ".dat";
    private static final String INSTANT_APP_ICON_FILE = "icon.png";
    private static final String INSTANT_APP_METADATA_FILE = "metadata.xml";
    private static final String LOG_TAG = "InstantAppRegistry";
    private static final String TAG_PACKAGE = "package";
    private static final String TAG_PERMISSION = "permission";
    private static final String TAG_PERMISSIONS = "permissions";
    private final CookiePersistence mCookiePersistence = new CookiePersistence(BackgroundThread.getHandler().getLooper());
    @GuardedBy({"mService.mPackages"})
    private SparseArray<SparseBooleanArray> mInstalledInstantAppUids;
    @GuardedBy({"mService.mPackages"})
    private SparseArray<SparseArray<SparseBooleanArray>> mInstantGrants;
    private final PackageManagerService mService;
    @GuardedBy({"mService.mPackages"})
    private SparseArray<List<UninstalledInstantAppState>> mUninstalledInstantApps;

    public InstantAppRegistry(PackageManagerService service) {
        this.mService = service;
    }

    @GuardedBy({"mService.mPackages"})
    public byte[] getInstantAppCookieLPw(String packageName, int userId) {
        PackageParser.Package pkg = this.mService.mPackages.get(packageName);
        if (pkg == null) {
            return null;
        }
        byte[] pendingCookie = this.mCookiePersistence.getPendingPersistCookieLPr(pkg, userId);
        if (pendingCookie != null) {
            return pendingCookie;
        }
        File cookieFile = peekInstantCookieFile(packageName, userId);
        if (cookieFile != null && cookieFile.exists()) {
            try {
                return IoUtils.readFileAsByteArray(cookieFile.toString());
            } catch (IOException e) {
                Slog.w(LOG_TAG, "Error reading cookie file: " + cookieFile);
            }
        }
        return null;
    }

    @GuardedBy({"mService.mPackages"})
    public boolean setInstantAppCookieLPw(String packageName, byte[] cookie, int userId) {
        int maxCookieSize;
        if (cookie == null || cookie.length <= 0 || cookie.length <= (maxCookieSize = this.mService.mContext.getPackageManager().getInstantAppCookieMaxBytes())) {
            PackageParser.Package pkg = this.mService.mPackages.get(packageName);
            if (pkg == null) {
                return false;
            }
            this.mCookiePersistence.schedulePersistLPw(userId, pkg, cookie);
            return true;
        }
        Slog.e(LOG_TAG, "Instant app cookie for package " + packageName + " size " + cookie.length + " bytes while max size is " + maxCookieSize);
        return false;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0049, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        $closeResource(r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x004d, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void persistInstantApplicationCookie(byte[] r5, java.lang.String r6, java.io.File r7, int r8) {
        /*
            r4 = this;
            com.android.server.pm.PackageManagerService r0 = r4.mService
            android.util.ArrayMap<java.lang.String, android.content.pm.PackageParser$Package> r0 = r0.mPackages
            monitor-enter(r0)
            java.io.File r1 = getInstantApplicationDir(r6, r8)     // Catch:{ all -> 0x0068 }
            boolean r2 = r1.exists()     // Catch:{ all -> 0x0068 }
            if (r2 != 0) goto L_0x001e
            boolean r2 = r1.mkdirs()     // Catch:{ all -> 0x0068 }
            if (r2 != 0) goto L_0x001e
            java.lang.String r2 = "InstantAppRegistry"
            java.lang.String r3 = "Cannot create instant app cookie directory"
            android.util.Slog.e(r2, r3)     // Catch:{ all -> 0x0068 }
            monitor-exit(r0)     // Catch:{ all -> 0x0068 }
            return
        L_0x001e:
            boolean r2 = r7.exists()     // Catch:{ all -> 0x0068 }
            if (r2 == 0) goto L_0x0031
            boolean r2 = r7.delete()     // Catch:{ all -> 0x0068 }
            if (r2 != 0) goto L_0x0031
            java.lang.String r2 = "InstantAppRegistry"
            java.lang.String r3 = "Cannot delete instant app cookie file"
            android.util.Slog.e(r2, r3)     // Catch:{ all -> 0x0068 }
        L_0x0031:
            if (r5 == 0) goto L_0x0066
            int r2 = r5.length     // Catch:{ all -> 0x0068 }
            if (r2 > 0) goto L_0x0037
            goto L_0x0066
        L_0x0037:
            monitor-exit(r0)     // Catch:{ all -> 0x0068 }
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x004e }
            r0.<init>(r7)     // Catch:{ IOException -> 0x004e }
            r1 = 0
            r2 = 0
            int r3 = r5.length     // Catch:{ all -> 0x0047 }
            r0.write(r5, r2, r3)     // Catch:{ all -> 0x0047 }
            $closeResource(r1, r0)     // Catch:{ IOException -> 0x004e }
            goto L_0x0065
        L_0x0047:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0049 }
        L_0x0049:
            r2 = move-exception
            $closeResource(r1, r0)     // Catch:{ IOException -> 0x004e }
            throw r2     // Catch:{ IOException -> 0x004e }
        L_0x004e:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Error writing instant app cookie file: "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "InstantAppRegistry"
            android.util.Slog.e(r2, r1, r0)
        L_0x0065:
            return
        L_0x0066:
            monitor-exit(r0)     // Catch:{ all -> 0x0068 }
            return
        L_0x0068:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0068 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.InstantAppRegistry.persistInstantApplicationCookie(byte[], java.lang.String, java.io.File, int):void");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    public Bitmap getInstantAppIconLPw(String packageName, int userId) {
        File iconFile = new File(getInstantApplicationDir(packageName, userId), INSTANT_APP_ICON_FILE);
        if (iconFile.exists()) {
            return BitmapFactory.decodeFile(iconFile.toString());
        }
        return null;
    }

    public String getInstantAppAndroidIdLPw(String packageName, int userId) {
        File idFile = new File(getInstantApplicationDir(packageName, userId), INSTANT_APP_ANDROID_ID_FILE);
        if (idFile.exists()) {
            try {
                return IoUtils.readFileAsString(idFile.getAbsolutePath());
            } catch (IOException e) {
                Slog.e(LOG_TAG, "Failed to read instant app android id file: " + idFile, e);
            }
        }
        return generateInstantAppAndroidIdLPw(packageName, userId);
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004c, code lost:
        r7 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        $closeResource(r6, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0050, code lost:
        throw r7;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String generateInstantAppAndroidIdLPw(java.lang.String r9, int r10) {
        /*
            r8 = this;
            r0 = 8
            byte[] r0 = new byte[r0]
            java.security.SecureRandom r1 = new java.security.SecureRandom
            r1.<init>()
            r1.nextBytes(r0)
            java.lang.String r1 = android.util.ByteStringUtils.toHexString(r0)
            java.util.Locale r2 = java.util.Locale.US
            java.lang.String r1 = r1.toLowerCase(r2)
            java.io.File r2 = getInstantApplicationDir(r9, r10)
            boolean r3 = r2.exists()
            java.lang.String r4 = "InstantAppRegistry"
            if (r3 != 0) goto L_0x002e
            boolean r3 = r2.mkdirs()
            if (r3 != 0) goto L_0x002e
            java.lang.String r3 = "Cannot create instant app cookie directory"
            android.util.Slog.e(r4, r3)
            return r1
        L_0x002e:
            java.io.File r3 = new java.io.File
            java.io.File r5 = getInstantApplicationDir(r9, r10)
            java.lang.String r6 = "android_id"
            r3.<init>(r5, r6)
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0051 }
            r5.<init>(r3)     // Catch:{ IOException -> 0x0051 }
            r6 = 0
            byte[] r7 = r1.getBytes()     // Catch:{ all -> 0x004a }
            r5.write(r7)     // Catch:{ all -> 0x004a }
            $closeResource(r6, r5)     // Catch:{ IOException -> 0x0051 }
            goto L_0x0066
        L_0x004a:
            r6 = move-exception
            throw r6     // Catch:{ all -> 0x004c }
        L_0x004c:
            r7 = move-exception
            $closeResource(r6, r5)     // Catch:{ IOException -> 0x0051 }
            throw r7     // Catch:{ IOException -> 0x0051 }
        L_0x0051:
            r5 = move-exception
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Error writing instant app android id file: "
            r6.append(r7)
            r6.append(r3)
            java.lang.String r6 = r6.toString()
            android.util.Slog.e(r4, r6, r5)
        L_0x0066:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.InstantAppRegistry.generateInstantAppAndroidIdLPw(java.lang.String, int):java.lang.String");
    }

    @GuardedBy({"mService.mPackages"})
    public List<InstantAppInfo> getInstantAppsLPr(int userId) {
        List<InstantAppInfo> installedApps = getInstalledInstantApplicationsLPr(userId);
        List<InstantAppInfo> uninstalledApps = getUninstalledInstantApplicationsLPr(userId);
        if (installedApps == null) {
            return uninstalledApps;
        }
        if (uninstalledApps != null) {
            installedApps.addAll(uninstalledApps);
        }
        return installedApps;
    }

    @GuardedBy({"mService.mPackages"})
    public void onPackageInstalledLPw(PackageParser.Package pkg, int[] userIds) {
        PackageParser.Package packageR = pkg;
        int[] iArr = userIds;
        PackageSetting ps = (PackageSetting) packageR.mExtras;
        if (ps != null) {
            for (int userId : iArr) {
                if (this.mService.mPackages.get(packageR.packageName) != null && ps.getInstalled(userId)) {
                    propagateInstantAppPermissionsIfNeeded(packageR, userId);
                    if (ps.getInstantApp(userId)) {
                        addInstantAppLPw(userId, ps.appId);
                    }
                    removeUninstalledInstantAppStateLPw(new Predicate(packageR) {
                        private final /* synthetic */ PackageParser.Package f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final boolean test(Object obj) {
                            return ((InstantAppRegistry.UninstalledInstantAppState) obj).mInstantAppInfo.getPackageName().equals(this.f$0.packageName);
                        }
                    }, userId);
                    File instantAppDir = getInstantApplicationDir(packageR.packageName, userId);
                    new File(instantAppDir, INSTANT_APP_METADATA_FILE).delete();
                    new File(instantAppDir, INSTANT_APP_ICON_FILE).delete();
                    File currentCookieFile = peekInstantCookieFile(packageR.packageName, userId);
                    if (currentCookieFile == null) {
                        continue;
                    } else {
                        String cookieName = currentCookieFile.getName();
                        String currentCookieSha256 = cookieName.substring(INSTANT_APP_COOKIE_FILE_PREFIX.length(), cookieName.length() - INSTANT_APP_COOKIE_FILE_SIFFIX.length());
                        if (!packageR.mSigningDetails.checkCapability(currentCookieSha256, 1)) {
                            String[] signaturesSha256Digests = PackageUtils.computeSignaturesSha256Digests(packageR.mSigningDetails.signatures);
                            int length = signaturesSha256Digests.length;
                            int i = 0;
                            while (i < length) {
                                if (!signaturesSha256Digests[i].equals(currentCookieSha256)) {
                                    i++;
                                } else {
                                    return;
                                }
                            }
                            Slog.i(LOG_TAG, "Signature for package " + packageR.packageName + " changed - dropping cookie");
                            this.mCookiePersistence.cancelPendingPersistLPw(packageR, userId);
                            currentCookieFile.delete();
                        } else {
                            return;
                        }
                    }
                }
            }
        }
    }

    @GuardedBy({"mService.mPackages"})
    public void onPackageUninstalledLPw(PackageParser.Package pkg, int[] userIds) {
        PackageSetting ps = (PackageSetting) pkg.mExtras;
        if (ps != null) {
            for (int userId : userIds) {
                if (this.mService.mPackages.get(pkg.packageName) == null || !ps.getInstalled(userId)) {
                    if (ps.getInstantApp(userId)) {
                        addUninstalledInstantAppLPw(pkg, userId);
                        removeInstantAppLPw(userId, ps.appId);
                    } else {
                        deleteDir(getInstantApplicationDir(pkg.packageName, userId));
                        this.mCookiePersistence.cancelPendingPersistLPw(pkg, userId);
                        removeAppLPw(userId, ps.appId);
                    }
                }
            }
        }
    }

    @GuardedBy({"mService.mPackages"})
    public void onUserRemovedLPw(int userId) {
        SparseArray<List<UninstalledInstantAppState>> sparseArray = this.mUninstalledInstantApps;
        if (sparseArray != null) {
            sparseArray.remove(userId);
            if (this.mUninstalledInstantApps.size() <= 0) {
                this.mUninstalledInstantApps = null;
            }
        }
        SparseArray<SparseBooleanArray> sparseArray2 = this.mInstalledInstantAppUids;
        if (sparseArray2 != null) {
            sparseArray2.remove(userId);
            if (this.mInstalledInstantAppUids.size() <= 0) {
                this.mInstalledInstantAppUids = null;
            }
        }
        SparseArray<SparseArray<SparseBooleanArray>> sparseArray3 = this.mInstantGrants;
        if (sparseArray3 != null) {
            sparseArray3.remove(userId);
            if (this.mInstantGrants.size() <= 0) {
                this.mInstantGrants = null;
            }
        }
        deleteDir(getInstantApplicationsDir(userId));
    }

    public boolean isInstantAccessGranted(int userId, int targetAppId, int instantAppId) {
        SparseArray<SparseBooleanArray> targetAppList;
        SparseBooleanArray instantGrantList;
        SparseArray<SparseArray<SparseBooleanArray>> sparseArray = this.mInstantGrants;
        if (sparseArray == null || (targetAppList = sparseArray.get(userId)) == null || (instantGrantList = targetAppList.get(targetAppId)) == null) {
            return false;
        }
        return instantGrantList.get(instantAppId);
    }

    @GuardedBy({"mService.mPackages"})
    public void grantInstantAccessLPw(int userId, Intent intent, int targetAppId, int instantAppId) {
        SparseBooleanArray instantAppList;
        Set<String> categories;
        SparseArray<SparseBooleanArray> sparseArray = this.mInstalledInstantAppUids;
        if (sparseArray == null || (instantAppList = sparseArray.get(userId)) == null || !instantAppList.get(instantAppId) || instantAppList.get(targetAppId)) {
            return;
        }
        if (intent == null || !"android.intent.action.VIEW".equals(intent.getAction()) || (categories = intent.getCategories()) == null || !categories.contains("android.intent.category.BROWSABLE")) {
            if (this.mInstantGrants == null) {
                this.mInstantGrants = new SparseArray<>();
            }
            SparseArray<SparseBooleanArray> targetAppList = this.mInstantGrants.get(userId);
            if (targetAppList == null) {
                targetAppList = new SparseArray<>();
                this.mInstantGrants.put(userId, targetAppList);
            }
            SparseBooleanArray instantGrantList = targetAppList.get(targetAppId);
            if (instantGrantList == null) {
                instantGrantList = new SparseBooleanArray();
                targetAppList.put(targetAppId, instantGrantList);
            }
            instantGrantList.put(instantAppId, true);
        }
    }

    @GuardedBy({"mService.mPackages"})
    public void addInstantAppLPw(int userId, int instantAppId) {
        if (this.mInstalledInstantAppUids == null) {
            this.mInstalledInstantAppUids = new SparseArray<>();
        }
        SparseBooleanArray instantAppList = this.mInstalledInstantAppUids.get(userId);
        if (instantAppList == null) {
            instantAppList = new SparseBooleanArray();
            this.mInstalledInstantAppUids.put(userId, instantAppList);
        }
        instantAppList.put(instantAppId, true);
    }

    @GuardedBy({"mService.mPackages"})
    private void removeInstantAppLPw(int userId, int instantAppId) {
        SparseBooleanArray instantAppList;
        SparseArray<SparseBooleanArray> targetAppList;
        SparseArray<SparseBooleanArray> sparseArray = this.mInstalledInstantAppUids;
        if (sparseArray != null && (instantAppList = sparseArray.get(userId)) != null) {
            instantAppList.delete(instantAppId);
            SparseArray<SparseArray<SparseBooleanArray>> sparseArray2 = this.mInstantGrants;
            if (sparseArray2 != null && (targetAppList = sparseArray2.get(userId)) != null) {
                for (int i = targetAppList.size() - 1; i >= 0; i--) {
                    targetAppList.valueAt(i).delete(instantAppId);
                }
            }
        }
    }

    @GuardedBy({"mService.mPackages"})
    private void removeAppLPw(int userId, int targetAppId) {
        SparseArray<SparseBooleanArray> targetAppList;
        SparseArray<SparseArray<SparseBooleanArray>> sparseArray = this.mInstantGrants;
        if (sparseArray != null && (targetAppList = sparseArray.get(userId)) != null) {
            targetAppList.delete(targetAppId);
        }
    }

    @GuardedBy({"mService.mPackages"})
    private void addUninstalledInstantAppLPw(PackageParser.Package pkg, int userId) {
        InstantAppInfo uninstalledApp = createInstantAppInfoForPackage(pkg, userId, false);
        if (uninstalledApp != null) {
            if (this.mUninstalledInstantApps == null) {
                this.mUninstalledInstantApps = new SparseArray<>();
            }
            List<UninstalledInstantAppState> uninstalledAppStates = this.mUninstalledInstantApps.get(userId);
            if (uninstalledAppStates == null) {
                uninstalledAppStates = new ArrayList<>();
                this.mUninstalledInstantApps.put(userId, uninstalledAppStates);
            }
            uninstalledAppStates.add(new UninstalledInstantAppState(uninstalledApp, System.currentTimeMillis()));
            writeUninstalledInstantAppMetadata(uninstalledApp, userId);
            writeInstantApplicationIconLPw(pkg, userId);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x006a, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        $closeResource(r5, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x006e, code lost:
        throw r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void writeInstantApplicationIconLPw(android.content.pm.PackageParser.Package r9, int r10) {
        /*
            r8 = this;
            java.lang.String r0 = r9.packageName
            java.io.File r0 = getInstantApplicationDir(r0, r10)
            boolean r1 = r0.exists()
            if (r1 != 0) goto L_0x000d
            return
        L_0x000d:
            android.content.pm.ApplicationInfo r1 = r9.applicationInfo
            com.android.server.pm.PackageManagerService r2 = r8.mService
            android.content.Context r2 = r2.mContext
            android.content.pm.PackageManager r2 = r2.getPackageManager()
            android.graphics.drawable.Drawable r1 = r1.loadIcon(r2)
            boolean r2 = r1 instanceof android.graphics.drawable.BitmapDrawable
            if (r2 == 0) goto L_0x0027
            r2 = r1
            android.graphics.drawable.BitmapDrawable r2 = (android.graphics.drawable.BitmapDrawable) r2
            android.graphics.Bitmap r2 = r2.getBitmap()
            goto L_0x0049
        L_0x0027:
            int r2 = r1.getIntrinsicWidth()
            int r3 = r1.getIntrinsicHeight()
            android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r2 = android.graphics.Bitmap.createBitmap(r2, r3, r4)
            android.graphics.Canvas r3 = new android.graphics.Canvas
            r3.<init>(r2)
            int r4 = r1.getIntrinsicWidth()
            int r5 = r1.getIntrinsicHeight()
            r6 = 0
            r1.setBounds(r6, r6, r4, r5)
            r1.draw(r3)
        L_0x0049:
            java.io.File r3 = new java.io.File
            java.lang.String r4 = r9.packageName
            java.io.File r4 = getInstantApplicationDir(r4, r10)
            java.lang.String r5 = "icon.png"
            r3.<init>(r4, r5)
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x006f }
            r4.<init>(r3)     // Catch:{ Exception -> 0x006f }
            r5 = 0
            android.graphics.Bitmap$CompressFormat r6 = android.graphics.Bitmap.CompressFormat.PNG     // Catch:{ all -> 0x0068 }
            r7 = 100
            r2.compress(r6, r7, r4)     // Catch:{ all -> 0x0068 }
            $closeResource(r5, r4)     // Catch:{ Exception -> 0x006f }
            goto L_0x0077
        L_0x0068:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x006a }
        L_0x006a:
            r6 = move-exception
            $closeResource(r5, r4)     // Catch:{ Exception -> 0x006f }
            throw r6     // Catch:{ Exception -> 0x006f }
        L_0x006f:
            r4 = move-exception
            java.lang.String r5 = "InstantAppRegistry"
            java.lang.String r6 = "Error writing instant app icon"
            android.util.Slog.e(r5, r6, r4)
        L_0x0077:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.InstantAppRegistry.writeInstantApplicationIconLPw(android.content.pm.PackageParser$Package, int):void");
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService.mPackages"})
    public boolean hasInstantApplicationMetadataLPr(String packageName, int userId) {
        return hasUninstalledInstantAppStateLPr(packageName, userId) || hasInstantAppMetadataLPr(packageName, userId);
    }

    @GuardedBy({"mService.mPackages"})
    public void deleteInstantApplicationMetadataLPw(String packageName, int userId) {
        removeUninstalledInstantAppStateLPw(new Predicate(packageName) {
            private final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return ((InstantAppRegistry.UninstalledInstantAppState) obj).mInstantAppInfo.getPackageName().equals(this.f$0);
            }
        }, userId);
        File instantAppDir = getInstantApplicationDir(packageName, userId);
        new File(instantAppDir, INSTANT_APP_METADATA_FILE).delete();
        new File(instantAppDir, INSTANT_APP_ICON_FILE).delete();
        new File(instantAppDir, INSTANT_APP_ANDROID_ID_FILE).delete();
        File cookie = peekInstantCookieFile(packageName, userId);
        if (cookie != null) {
            cookie.delete();
        }
    }

    @GuardedBy({"mService.mPackages"})
    private void removeUninstalledInstantAppStateLPw(Predicate<UninstalledInstantAppState> criteria, int userId) {
        List<UninstalledInstantAppState> uninstalledAppStates;
        SparseArray<List<UninstalledInstantAppState>> sparseArray = this.mUninstalledInstantApps;
        if (sparseArray != null && (uninstalledAppStates = sparseArray.get(userId)) != null) {
            for (int i = uninstalledAppStates.size() - 1; i >= 0; i--) {
                if (criteria.test(uninstalledAppStates.get(i))) {
                    uninstalledAppStates.remove(i);
                    if (uninstalledAppStates.isEmpty()) {
                        this.mUninstalledInstantApps.remove(userId);
                        if (this.mUninstalledInstantApps.size() <= 0) {
                            this.mUninstalledInstantApps = null;
                            return;
                        }
                        return;
                    }
                }
            }
        }
    }

    @GuardedBy({"mService.mPackages"})
    private boolean hasUninstalledInstantAppStateLPr(String packageName, int userId) {
        List<UninstalledInstantAppState> uninstalledAppStates;
        SparseArray<List<UninstalledInstantAppState>> sparseArray = this.mUninstalledInstantApps;
        if (sparseArray == null || (uninstalledAppStates = sparseArray.get(userId)) == null) {
            return false;
        }
        int appCount = uninstalledAppStates.size();
        for (int i = 0; i < appCount; i++) {
            if (packageName.equals(uninstalledAppStates.get(i).mInstantAppInfo.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasInstantAppMetadataLPr(String packageName, int userId) {
        File instantAppDir = getInstantApplicationDir(packageName, userId);
        return new File(instantAppDir, INSTANT_APP_METADATA_FILE).exists() || new File(instantAppDir, INSTANT_APP_ICON_FILE).exists() || new File(instantAppDir, INSTANT_APP_ANDROID_ID_FILE).exists() || peekInstantCookieFile(packageName, userId) != null;
    }

    /* access modifiers changed from: package-private */
    public void pruneInstantApps() {
        try {
            pruneInstantApps(JobStatus.NO_LATEST_RUNTIME, Settings.Global.getLong(this.mService.mContext.getContentResolver(), "installed_instant_app_max_cache_period", 15552000000L), Settings.Global.getLong(this.mService.mContext.getContentResolver(), "uninstalled_instant_app_max_cache_period", 15552000000L));
        } catch (IOException e) {
            Slog.e(LOG_TAG, "Error pruning installed and uninstalled instant apps", e);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean pruneInstalledInstantApps(long neededSpace, long maxInstalledCacheDuration) {
        try {
            return pruneInstantApps(neededSpace, maxInstalledCacheDuration, JobStatus.NO_LATEST_RUNTIME);
        } catch (IOException e) {
            Slog.e(LOG_TAG, "Error pruning installed instant apps", e);
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean pruneUninstalledInstantApps(long neededSpace, long maxUninstalledCacheDuration) {
        try {
            return pruneInstantApps(neededSpace, JobStatus.NO_LATEST_RUNTIME, maxUninstalledCacheDuration);
        } catch (IOException e) {
            Slog.e(LOG_TAG, "Error pruning uninstalled instant apps", e);
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00af, code lost:
        if (r7 == null) goto L_0x00dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00b1, code lost:
        r0 = r7.size();
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00b6, code lost:
        if (r6 >= r0) goto L_0x00dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00cf, code lost:
        if (r1.mService.deletePackageX((java.lang.String) r7.get(r6), -1, 0, 2) != 1) goto L_0x00da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00d7, code lost:
        if (r5.getUsableSpace() < r27) goto L_0x00da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00d9, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00da, code lost:
        r6 = r6 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00dd, code lost:
        r6 = r1.mService.mPackages;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00e1, code lost:
        monitor-enter(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:?, code lost:
        r0 = com.android.server.pm.UserManagerService.getInstance().getUserIds();
        r10 = r0.length;
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00ec, code lost:
        if (r12 >= r10) goto L_0x0187;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00ee, code lost:
        r13 = r0[r12];
        r1.removeUninstalledInstantAppStateLPw(new com.android.server.pm.$$Lambda$InstantAppRegistry$BuKCbLr_MGBazMPl54pWTuGHYY(r2), r13);
        r14 = getInstantApplicationsDir(r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0100, code lost:
        if (r14.exists() != false) goto L_0x010b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0102, code lost:
        r16 = r0;
        r20 = r4;
        r21 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x010b, code lost:
        r15 = r14.listFiles();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x010f, code lost:
        if (r15 != null) goto L_0x011a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0111, code lost:
        r16 = r0;
        r20 = r4;
        r21 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x011a, code lost:
        r16 = r0;
        r0 = r15.length;
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x011e, code lost:
        if (r1 >= r0) goto L_0x0176;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0120, code lost:
        r19 = r15[r1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0128, code lost:
        if (r19.isDirectory() != false) goto L_0x0132;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x012a, code lost:
        r18 = r0;
        r20 = r4;
        r21 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0132, code lost:
        r18 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0136, code lost:
        r20 = r4;
        r21 = r7;
        r7 = r19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:?, code lost:
        r0 = new java.io.File(r7, INSTANT_APP_METADATA_FILE);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0146, code lost:
        if (r0.exists() != false) goto L_0x014a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0156, code lost:
        if ((java.lang.System.currentTimeMillis() - r0.lastModified()) <= r2) goto L_0x0168;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0158, code lost:
        deleteDir(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0161, code lost:
        if (r5.getUsableSpace() < r27) goto L_0x0166;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0163, code lost:
        monitor-exit(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0164, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0169, code lost:
        r1 = r1 + 1;
        r0 = r18;
        r4 = r20;
        r7 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0176, code lost:
        r20 = r4;
        r21 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x017b, code lost:
        r12 = r12 + 1;
        r1 = r26;
        r0 = r16;
        r4 = r20;
        r7 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0187, code lost:
        r20 = r4;
        r21 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x018b, code lost:
        monitor-exit(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x018c, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x018e, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x018f, code lost:
        r20 = r4;
        r21 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x0193, code lost:
        monitor-exit(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0194, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0195, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean pruneInstantApps(long r27, long r29, long r31) throws java.io.IOException {
        /*
            r26 = this;
            r1 = r26
            r2 = r31
            com.android.server.pm.PackageManagerService r0 = r1.mService
            android.content.Context r0 = r0.mContext
            java.lang.Class<android.os.storage.StorageManager> r4 = android.os.storage.StorageManager.class
            java.lang.Object r0 = r0.getSystemService(r4)
            r4 = r0
            android.os.storage.StorageManager r4 = (android.os.storage.StorageManager) r4
            java.lang.String r0 = android.os.storage.StorageManager.UUID_PRIVATE_INTERNAL
            java.io.File r5 = r4.findPathForUuid(r0)
            long r6 = r5.getUsableSpace()
            int r0 = (r6 > r27 ? 1 : (r6 == r27 ? 0 : -1))
            r6 = 1
            if (r0 < 0) goto L_0x0021
            return r6
        L_0x0021:
            r7 = 0
            long r8 = java.lang.System.currentTimeMillis()
            com.android.server.pm.PackageManagerService r0 = r1.mService
            android.util.ArrayMap<java.lang.String, android.content.pm.PackageParser$Package> r10 = r0.mPackages
            monitor-enter(r10)
            com.android.server.pm.UserManagerService r0 = com.android.server.pm.PackageManagerService.sUserManager     // Catch:{ all -> 0x019d }
            int[] r0 = r0.getUserIds()     // Catch:{ all -> 0x019d }
            r11 = r0
            com.android.server.pm.PackageManagerService r0 = r1.mService     // Catch:{ all -> 0x019d }
            android.util.ArrayMap<java.lang.String, android.content.pm.PackageParser$Package> r0 = r0.mPackages     // Catch:{ all -> 0x019d }
            int r0 = r0.size()     // Catch:{ all -> 0x019d }
            r12 = 0
        L_0x003b:
            if (r12 >= r0) goto L_0x00a2
            com.android.server.pm.PackageManagerService r14 = r1.mService     // Catch:{ all -> 0x009d }
            android.util.ArrayMap<java.lang.String, android.content.pm.PackageParser$Package> r14 = r14.mPackages     // Catch:{ all -> 0x009d }
            java.lang.Object r14 = r14.valueAt(r12)     // Catch:{ all -> 0x009d }
            android.content.pm.PackageParser$Package r14 = (android.content.pm.PackageParser.Package) r14     // Catch:{ all -> 0x009d }
            long r15 = r14.getLatestPackageUseTimeInMills()     // Catch:{ all -> 0x009d }
            long r15 = r8 - r15
            int r15 = (r15 > r29 ? 1 : (r15 == r29 ? 0 : -1))
            if (r15 >= 0) goto L_0x0054
            r19 = r0
            goto L_0x0097
        L_0x0054:
            java.lang.Object r15 = r14.mExtras     // Catch:{ all -> 0x009d }
            boolean r15 = r15 instanceof com.android.server.pm.PackageSetting     // Catch:{ all -> 0x009d }
            if (r15 != 0) goto L_0x005d
            r19 = r0
            goto L_0x0097
        L_0x005d:
            java.lang.Object r15 = r14.mExtras     // Catch:{ all -> 0x009d }
            com.android.server.pm.PackageSetting r15 = (com.android.server.pm.PackageSetting) r15     // Catch:{ all -> 0x009d }
            r16 = 0
            int r13 = r11.length     // Catch:{ all -> 0x009d }
            r6 = 0
        L_0x0065:
            if (r6 >= r13) goto L_0x0086
            r17 = r11[r6]     // Catch:{ all -> 0x009d }
            r18 = r17
            r19 = r0
            r0 = r18
            boolean r17 = r15.getInstalled(r0)     // Catch:{ all -> 0x009d }
            if (r17 == 0) goto L_0x0081
            boolean r17 = r15.getInstantApp(r0)     // Catch:{ all -> 0x009d }
            if (r17 == 0) goto L_0x007e
            r16 = 1
            goto L_0x0081
        L_0x007e:
            r16 = 0
            goto L_0x0088
        L_0x0081:
            int r6 = r6 + 1
            r0 = r19
            goto L_0x0065
        L_0x0086:
            r19 = r0
        L_0x0088:
            if (r16 == 0) goto L_0x0097
            if (r7 != 0) goto L_0x0092
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x009d }
            r0.<init>()     // Catch:{ all -> 0x009d }
            r7 = r0
        L_0x0092:
            java.lang.String r0 = r14.packageName     // Catch:{ all -> 0x009d }
            r7.add(r0)     // Catch:{ all -> 0x009d }
        L_0x0097:
            int r12 = r12 + 1
            r0 = r19
            r6 = 1
            goto L_0x003b
        L_0x009d:
            r0 = move-exception
            r20 = r4
            goto L_0x01a0
        L_0x00a2:
            r19 = r0
            if (r7 == 0) goto L_0x00ae
            com.android.server.pm.-$$Lambda$InstantAppRegistry$UOn4sUy4zBQuofxUbY8RBYhkNSE r0 = new com.android.server.pm.-$$Lambda$InstantAppRegistry$UOn4sUy4zBQuofxUbY8RBYhkNSE     // Catch:{ all -> 0x009d }
            r0.<init>()     // Catch:{ all -> 0x009d }
            r7.sort(r0)     // Catch:{ all -> 0x009d }
        L_0x00ae:
            monitor-exit(r10)     // Catch:{ all -> 0x0197 }
            if (r7 == 0) goto L_0x00dd
            int r0 = r7.size()
            r6 = 0
        L_0x00b6:
            if (r6 >= r0) goto L_0x00dd
            java.lang.Object r10 = r7.get(r6)
            java.lang.String r10 = (java.lang.String) r10
            com.android.server.pm.PackageManagerService r12 = r1.mService
            r20 = -1
            r22 = 0
            r23 = 2
            r18 = r12
            r19 = r10
            int r12 = r18.deletePackageX(r19, r20, r22, r23)
            r13 = 1
            if (r12 != r13) goto L_0x00da
            long r14 = r5.getUsableSpace()
            int r12 = (r14 > r27 ? 1 : (r14 == r27 ? 0 : -1))
            if (r12 < 0) goto L_0x00da
            return r13
        L_0x00da:
            int r6 = r6 + 1
            goto L_0x00b6
        L_0x00dd:
            com.android.server.pm.PackageManagerService r0 = r1.mService
            android.util.ArrayMap<java.lang.String, android.content.pm.PackageParser$Package> r6 = r0.mPackages
            monitor-enter(r6)
            com.android.server.pm.UserManagerService r0 = com.android.server.pm.UserManagerService.getInstance()     // Catch:{ all -> 0x018e }
            int[] r0 = r0.getUserIds()     // Catch:{ all -> 0x018e }
            int r10 = r0.length     // Catch:{ all -> 0x018e }
            r12 = 0
        L_0x00ec:
            if (r12 >= r10) goto L_0x0187
            r13 = r0[r12]     // Catch:{ all -> 0x018e }
            com.android.server.pm.-$$Lambda$InstantAppRegistry$BuKCbLr_MGBazMPl54-pWTuGHYY r14 = new com.android.server.pm.-$$Lambda$InstantAppRegistry$BuKCbLr_MGBazMPl54-pWTuGHYY     // Catch:{ all -> 0x018e }
            r14.<init>(r2)     // Catch:{ all -> 0x018e }
            r1.removeUninstalledInstantAppStateLPw(r14, r13)     // Catch:{ all -> 0x018e }
            java.io.File r14 = getInstantApplicationsDir(r13)     // Catch:{ all -> 0x018e }
            boolean r15 = r14.exists()     // Catch:{ all -> 0x018e }
            if (r15 != 0) goto L_0x010b
            r16 = r0
            r20 = r4
            r21 = r7
            r4 = 1
            goto L_0x017b
        L_0x010b:
            java.io.File[] r15 = r14.listFiles()     // Catch:{ all -> 0x018e }
            if (r15 != 0) goto L_0x011a
            r16 = r0
            r20 = r4
            r21 = r7
            r4 = 1
            goto L_0x017b
        L_0x011a:
            r16 = r0
            int r0 = r15.length     // Catch:{ all -> 0x018e }
            r1 = 0
        L_0x011e:
            if (r1 >= r0) goto L_0x0176
            r18 = r15[r1]     // Catch:{ all -> 0x018e }
            r19 = r18
            boolean r18 = r19.isDirectory()     // Catch:{ all -> 0x018e }
            if (r18 != 0) goto L_0x0132
            r18 = r0
            r20 = r4
            r21 = r7
            r4 = 1
            goto L_0x0169
        L_0x0132:
            r18 = r0
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x018e }
            r20 = r4
            java.lang.String r4 = "metadata.xml"
            r21 = r7
            r7 = r19
            r0.<init>(r7, r4)     // Catch:{ all -> 0x0195 }
            boolean r4 = r0.exists()     // Catch:{ all -> 0x0195 }
            if (r4 != 0) goto L_0x014a
            r4 = 1
            goto L_0x0169
        L_0x014a:
            long r22 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0195 }
            long r24 = r0.lastModified()     // Catch:{ all -> 0x0195 }
            long r22 = r22 - r24
            int r4 = (r22 > r2 ? 1 : (r22 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x0168
            deleteDir(r7)     // Catch:{ all -> 0x0195 }
            long r24 = r5.getUsableSpace()     // Catch:{ all -> 0x0195 }
            int r4 = (r24 > r27 ? 1 : (r24 == r27 ? 0 : -1))
            if (r4 < 0) goto L_0x0166
            monitor-exit(r6)     // Catch:{ all -> 0x0195 }
            r4 = 1
            return r4
        L_0x0166:
            r4 = 1
            goto L_0x0169
        L_0x0168:
            r4 = 1
        L_0x0169:
            int r1 = r1 + 1
            r0 = r18
            r4 = r20
            r7 = r21
            goto L_0x011e
        L_0x0172:
            r0 = move-exception
            r21 = r7
            goto L_0x0193
        L_0x0176:
            r20 = r4
            r21 = r7
            r4 = 1
        L_0x017b:
            int r12 = r12 + 1
            r1 = r26
            r0 = r16
            r4 = r20
            r7 = r21
            goto L_0x00ec
        L_0x0187:
            r20 = r4
            r21 = r7
            monitor-exit(r6)     // Catch:{ all -> 0x0195 }
            r0 = 0
            return r0
        L_0x018e:
            r0 = move-exception
            r20 = r4
            r21 = r7
        L_0x0193:
            monitor-exit(r6)     // Catch:{ all -> 0x0195 }
            throw r0
        L_0x0195:
            r0 = move-exception
            goto L_0x0193
        L_0x0197:
            r0 = move-exception
            r20 = r4
            r21 = r7
            goto L_0x01a0
        L_0x019d:
            r0 = move-exception
            r20 = r4
        L_0x01a0:
            monitor-exit(r10)     // Catch:{ all -> 0x01a2 }
            throw r0
        L_0x01a2:
            r0 = move-exception
            goto L_0x01a0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.InstantAppRegistry.pruneInstantApps(long, long, long):boolean");
    }

    public /* synthetic */ int lambda$pruneInstantApps$2$InstantAppRegistry(String lhs, String rhs) {
        PackageParser.Package lhsPkg = this.mService.mPackages.get(lhs);
        PackageParser.Package rhsPkg = this.mService.mPackages.get(rhs);
        if (lhsPkg == null && rhsPkg == null) {
            return 0;
        }
        if (lhsPkg == null) {
            return -1;
        }
        if (rhsPkg == null || lhsPkg.getLatestPackageUseTimeInMills() > rhsPkg.getLatestPackageUseTimeInMills()) {
            return 1;
        }
        if (lhsPkg.getLatestPackageUseTimeInMills() < rhsPkg.getLatestPackageUseTimeInMills()) {
            return -1;
        }
        if (!(lhsPkg.mExtras instanceof PackageSetting) || !(rhsPkg.mExtras instanceof PackageSetting)) {
            return 0;
        }
        if (((PackageSetting) lhsPkg.mExtras).firstInstallTime > ((PackageSetting) rhsPkg.mExtras).firstInstallTime) {
            return 1;
        }
        return -1;
    }

    static /* synthetic */ boolean lambda$pruneInstantApps$3(long maxUninstalledCacheDuration, UninstalledInstantAppState state) {
        return System.currentTimeMillis() - state.mTimestamp > maxUninstalledCacheDuration;
    }

    @GuardedBy({"mService.mPackages"})
    private List<InstantAppInfo> getInstalledInstantApplicationsLPr(int userId) {
        InstantAppInfo info;
        List<InstantAppInfo> result = null;
        int packageCount = this.mService.mPackages.size();
        for (int i = 0; i < packageCount; i++) {
            PackageParser.Package pkg = this.mService.mPackages.valueAt(i);
            PackageSetting ps = (PackageSetting) pkg.mExtras;
            if (!(ps == null || !ps.getInstantApp(userId) || (info = createInstantAppInfoForPackage(pkg, userId, true)) == null)) {
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(info);
            }
        }
        return result;
    }

    private InstantAppInfo createInstantAppInfoForPackage(PackageParser.Package pkg, int userId, boolean addApplicationInfo) {
        PackageSetting ps = (PackageSetting) pkg.mExtras;
        if (ps == null || !ps.getInstalled(userId)) {
            return null;
        }
        String[] requestedPermissions = new String[pkg.requestedPermissions.size()];
        pkg.requestedPermissions.toArray(requestedPermissions);
        Set<String> permissions = ps.getPermissionsState().getPermissions(userId);
        String[] grantedPermissions = new String[permissions.size()];
        permissions.toArray(grantedPermissions);
        if (addApplicationInfo) {
            return new InstantAppInfo(pkg.applicationInfo, requestedPermissions, grantedPermissions);
        }
        return new InstantAppInfo(pkg.applicationInfo.packageName, pkg.applicationInfo.loadLabel(this.mService.mContext.getPackageManager()), requestedPermissions, grantedPermissions);
    }

    @GuardedBy({"mService.mPackages"})
    private List<InstantAppInfo> getUninstalledInstantApplicationsLPr(int userId) {
        List<UninstalledInstantAppState> uninstalledAppStates = getUninstalledInstantAppStatesLPr(userId);
        if (uninstalledAppStates == null || uninstalledAppStates.isEmpty()) {
            return null;
        }
        List<InstantAppInfo> uninstalledApps = null;
        int stateCount = uninstalledAppStates.size();
        for (int i = 0; i < stateCount; i++) {
            UninstalledInstantAppState uninstalledAppState = uninstalledAppStates.get(i);
            if (uninstalledApps == null) {
                uninstalledApps = new ArrayList<>();
            }
            uninstalledApps.add(uninstalledAppState.mInstantAppInfo);
        }
        return uninstalledApps;
    }

    private void propagateInstantAppPermissionsIfNeeded(PackageParser.Package pkg, int userId) {
        InstantAppInfo appInfo = peekOrParseUninstalledInstantAppInfo(pkg.packageName, userId);
        if (appInfo != null && !ArrayUtils.isEmpty(appInfo.getGrantedPermissions())) {
            long identity = Binder.clearCallingIdentity();
            try {
                for (String grantedPermission : appInfo.getGrantedPermissions()) {
                    if (this.mService.mSettings.canPropagatePermissionToInstantApp(grantedPermission) && pkg.requestedPermissions.contains(grantedPermission)) {
                        this.mService.grantRuntimePermission(pkg.packageName, grantedPermission, userId);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    private InstantAppInfo peekOrParseUninstalledInstantAppInfo(String packageName, int userId) {
        List<UninstalledInstantAppState> uninstalledAppStates;
        SparseArray<List<UninstalledInstantAppState>> sparseArray = this.mUninstalledInstantApps;
        if (!(sparseArray == null || (uninstalledAppStates = sparseArray.get(userId)) == null)) {
            int appCount = uninstalledAppStates.size();
            for (int i = 0; i < appCount; i++) {
                UninstalledInstantAppState uninstalledAppState = uninstalledAppStates.get(i);
                if (uninstalledAppState.mInstantAppInfo.getPackageName().equals(packageName)) {
                    return uninstalledAppState.mInstantAppInfo;
                }
            }
        }
        UninstalledInstantAppState uninstalledAppState2 = parseMetadataFile(new File(getInstantApplicationDir(packageName, userId), INSTANT_APP_METADATA_FILE));
        if (uninstalledAppState2 == null) {
            return null;
        }
        return uninstalledAppState2.mInstantAppInfo;
    }

    @GuardedBy({"mService.mPackages"})
    private List<UninstalledInstantAppState> getUninstalledInstantAppStatesLPr(int userId) {
        File[] files;
        UninstalledInstantAppState uninstalledAppState;
        List<UninstalledInstantAppState> uninstalledAppStates = null;
        SparseArray<List<UninstalledInstantAppState>> sparseArray = this.mUninstalledInstantApps;
        if (sparseArray != null && (uninstalledAppStates = sparseArray.get(userId)) != null) {
            return uninstalledAppStates;
        }
        File instantAppsDir = getInstantApplicationsDir(userId);
        if (instantAppsDir.exists() && (files = instantAppsDir.listFiles()) != null) {
            for (File instantDir : files) {
                if (instantDir.isDirectory() && (uninstalledAppState = parseMetadataFile(new File(instantDir, INSTANT_APP_METADATA_FILE))) != null) {
                    if (uninstalledAppStates == null) {
                        uninstalledAppStates = new ArrayList<>();
                    }
                    uninstalledAppStates.add(uninstalledAppState);
                }
            }
        }
        if (uninstalledAppStates != null) {
            if (this.mUninstalledInstantApps == null) {
                this.mUninstalledInstantApps = new SparseArray<>();
            }
            this.mUninstalledInstantApps.put(userId, uninstalledAppStates);
        }
        return uninstalledAppStates;
    }

    private static UninstalledInstantAppState parseMetadataFile(File metadataFile) {
        if (!metadataFile.exists()) {
            return null;
        }
        try {
            FileInputStream in = new AtomicFile(metadataFile).openRead();
            File instantDir = metadataFile.getParentFile();
            long timestamp = metadataFile.lastModified();
            String packageName = instantDir.getName();
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(in, StandardCharsets.UTF_8.name());
                UninstalledInstantAppState uninstalledInstantAppState = new UninstalledInstantAppState(parseMetadata(parser, packageName), timestamp);
                IoUtils.closeQuietly(in);
                return uninstalledInstantAppState;
            } catch (IOException | XmlPullParserException e) {
                throw new IllegalStateException("Failed parsing instant metadata file: " + metadataFile, e);
            } catch (Throwable th) {
                IoUtils.closeQuietly(in);
                throw th;
            }
        } catch (FileNotFoundException e2) {
            Slog.i(LOG_TAG, "No instant metadata file");
            return null;
        }
    }

    /* access modifiers changed from: private */
    public static File computeInstantCookieFile(String packageName, String sha256Digest, int userId) {
        File appDir = getInstantApplicationDir(packageName, userId);
        return new File(appDir, INSTANT_APP_COOKIE_FILE_PREFIX + sha256Digest + INSTANT_APP_COOKIE_FILE_SIFFIX);
    }

    /* access modifiers changed from: private */
    public static File peekInstantCookieFile(String packageName, int userId) {
        File[] files;
        File appDir = getInstantApplicationDir(packageName, userId);
        if (!appDir.exists() || (files = appDir.listFiles()) == null) {
            return null;
        }
        for (File file : files) {
            if (!file.isDirectory() && file.getName().startsWith(INSTANT_APP_COOKIE_FILE_PREFIX) && file.getName().endsWith(INSTANT_APP_COOKIE_FILE_SIFFIX)) {
                return file;
            }
        }
        return null;
    }

    private static InstantAppInfo parseMetadata(XmlPullParser parser, String packageName) throws IOException, XmlPullParserException {
        int outerDepth = parser.getDepth();
        while (XmlUtils.nextElementWithin(parser, outerDepth)) {
            if ("package".equals(parser.getName())) {
                return parsePackage(parser, packageName);
            }
        }
        return null;
    }

    private static InstantAppInfo parsePackage(XmlPullParser parser, String packageName) throws IOException, XmlPullParserException {
        String label = parser.getAttributeValue((String) null, ATTR_LABEL);
        List<String> outRequestedPermissions = new ArrayList<>();
        List<String> outGrantedPermissions = new ArrayList<>();
        int outerDepth = parser.getDepth();
        while (XmlUtils.nextElementWithin(parser, outerDepth)) {
            if (TAG_PERMISSIONS.equals(parser.getName())) {
                parsePermissions(parser, outRequestedPermissions, outGrantedPermissions);
            }
        }
        String[] requestedPermissions = new String[outRequestedPermissions.size()];
        outRequestedPermissions.toArray(requestedPermissions);
        String[] grantedPermissions = new String[outGrantedPermissions.size()];
        outGrantedPermissions.toArray(grantedPermissions);
        return new InstantAppInfo(packageName, label, requestedPermissions, grantedPermissions);
    }

    private static void parsePermissions(XmlPullParser parser, List<String> outRequestedPermissions, List<String> outGrantedPermissions) throws IOException, XmlPullParserException {
        int outerDepth = parser.getDepth();
        while (XmlUtils.nextElementWithin(parser, outerDepth)) {
            if (TAG_PERMISSION.equals(parser.getName())) {
                String permission = XmlUtils.readStringAttribute(parser, "name");
                outRequestedPermissions.add(permission);
                if (XmlUtils.readBooleanAttribute(parser, ATTR_GRANTED)) {
                    outGrantedPermissions.add(permission);
                }
            }
        }
    }

    private void writeUninstalledInstantAppMetadata(InstantAppInfo instantApp, int userId) {
        boolean z;
        File appDir = getInstantApplicationDir(instantApp.getPackageName(), userId);
        if (appDir.exists() || appDir.mkdirs()) {
            AtomicFile destination = new AtomicFile(new File(appDir, INSTANT_APP_METADATA_FILE));
            FileOutputStream out = null;
            try {
                out = destination.startWrite();
                XmlSerializer serializer = Xml.newSerializer();
                serializer.setOutput(out, StandardCharsets.UTF_8.name());
                serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                String str = null;
                serializer.startDocument((String) null, true);
                serializer.startTag((String) null, "package");
                try {
                } catch (Throwable th) {
                    t = th;
                    InstantAppInfo instantAppInfo = instantApp;
                    File file = appDir;
                    try {
                        Slog.wtf(LOG_TAG, "Failed to write instant state, restoring backup", t);
                        destination.failWrite(out);
                        IoUtils.closeQuietly(out);
                    } catch (Throwable th2) {
                        IoUtils.closeQuietly(out);
                        throw th2;
                    }
                }
                try {
                    serializer.attribute((String) null, ATTR_LABEL, instantApp.loadLabel(this.mService.mContext.getPackageManager()).toString());
                    serializer.startTag((String) null, TAG_PERMISSIONS);
                    String[] requestedPermissions = instantApp.getRequestedPermissions();
                    int length = requestedPermissions.length;
                    int i = 0;
                    while (i < length) {
                        String permission = requestedPermissions[i];
                        serializer.startTag(str, TAG_PERMISSION);
                        File appDir2 = appDir;
                        String permission2 = permission;
                        try {
                            serializer.attribute(str, "name", permission2);
                            if (ArrayUtils.contains(instantApp.getGrantedPermissions(), permission2)) {
                                z = true;
                                String str2 = permission2;
                                serializer.attribute((String) null, ATTR_GRANTED, String.valueOf(true));
                            } else {
                                z = true;
                            }
                            serializer.endTag((String) null, TAG_PERMISSION);
                            i++;
                            boolean z2 = z;
                            appDir = appDir2;
                            str = null;
                        } catch (Throwable th3) {
                            t = th3;
                            Slog.wtf(LOG_TAG, "Failed to write instant state, restoring backup", t);
                            destination.failWrite(out);
                            IoUtils.closeQuietly(out);
                        }
                    }
                    serializer.endTag((String) null, TAG_PERMISSIONS);
                    serializer.endTag((String) null, "package");
                    serializer.endDocument();
                    destination.finishWrite(out);
                } catch (Throwable th4) {
                    t = th4;
                    File file2 = appDir;
                    Slog.wtf(LOG_TAG, "Failed to write instant state, restoring backup", t);
                    destination.failWrite(out);
                    IoUtils.closeQuietly(out);
                }
            } catch (Throwable th5) {
                t = th5;
                InstantAppInfo instantAppInfo2 = instantApp;
                File file22 = appDir;
                Slog.wtf(LOG_TAG, "Failed to write instant state, restoring backup", t);
                destination.failWrite(out);
                IoUtils.closeQuietly(out);
            }
            IoUtils.closeQuietly(out);
        }
    }

    private static File getInstantApplicationsDir(int userId) {
        return new File(Environment.getUserSystemDirectory(userId), INSTANT_APPS_FOLDER);
    }

    private static File getInstantApplicationDir(String packageName, int userId) {
        return new File(getInstantApplicationsDir(userId), packageName);
    }

    private static void deleteDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteDir(file);
            }
        }
        dir.delete();
    }

    private static final class UninstalledInstantAppState {
        final InstantAppInfo mInstantAppInfo;
        final long mTimestamp;

        public UninstalledInstantAppState(InstantAppInfo instantApp, long timestamp) {
            this.mInstantAppInfo = instantApp;
            this.mTimestamp = timestamp;
        }
    }

    private final class CookiePersistence extends Handler {
        private static final long PERSIST_COOKIE_DELAY_MILLIS = 1000;
        private final SparseArray<ArrayMap<String, SomeArgs>> mPendingPersistCookies = new SparseArray<>();

        public CookiePersistence(Looper looper) {
            super(looper);
        }

        public void schedulePersistLPw(int userId, PackageParser.Package pkg, byte[] cookie) {
            File newCookieFile = InstantAppRegistry.computeInstantCookieFile(pkg.packageName, PackageUtils.computeSignaturesSha256Digest(pkg.mSigningDetails.signatures), userId);
            if (!pkg.mSigningDetails.hasSignatures()) {
                Slog.wtf(InstantAppRegistry.LOG_TAG, "Parsed Instant App contains no valid signatures!");
            }
            File oldCookieFile = InstantAppRegistry.peekInstantCookieFile(pkg.packageName, userId);
            if (oldCookieFile != null && !newCookieFile.equals(oldCookieFile)) {
                oldCookieFile.delete();
            }
            cancelPendingPersistLPw(pkg, userId);
            addPendingPersistCookieLPw(userId, pkg, cookie, newCookieFile);
            sendMessageDelayed(obtainMessage(userId, pkg), 1000);
        }

        public byte[] getPendingPersistCookieLPr(PackageParser.Package pkg, int userId) {
            SomeArgs state;
            ArrayMap<String, SomeArgs> pendingWorkForUser = this.mPendingPersistCookies.get(userId);
            if (pendingWorkForUser == null || (state = pendingWorkForUser.get(pkg.packageName)) == null) {
                return null;
            }
            return (byte[]) state.arg1;
        }

        public void cancelPendingPersistLPw(PackageParser.Package pkg, int userId) {
            removeMessages(userId, pkg);
            SomeArgs state = removePendingPersistCookieLPr(pkg, userId);
            if (state != null) {
                state.recycle();
            }
        }

        private void addPendingPersistCookieLPw(int userId, PackageParser.Package pkg, byte[] cookie, File cookieFile) {
            ArrayMap<String, SomeArgs> pendingWorkForUser = this.mPendingPersistCookies.get(userId);
            if (pendingWorkForUser == null) {
                pendingWorkForUser = new ArrayMap<>();
                this.mPendingPersistCookies.put(userId, pendingWorkForUser);
            }
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = cookie;
            args.arg2 = cookieFile;
            pendingWorkForUser.put(pkg.packageName, args);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: com.android.internal.os.SomeArgs} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private com.android.internal.os.SomeArgs removePendingPersistCookieLPr(android.content.pm.PackageParser.Package r4, int r5) {
            /*
                r3 = this;
                android.util.SparseArray<android.util.ArrayMap<java.lang.String, com.android.internal.os.SomeArgs>> r0 = r3.mPendingPersistCookies
                java.lang.Object r0 = r0.get(r5)
                android.util.ArrayMap r0 = (android.util.ArrayMap) r0
                r1 = 0
                if (r0 == 0) goto L_0x001f
                java.lang.String r2 = r4.packageName
                java.lang.Object r2 = r0.remove(r2)
                r1 = r2
                com.android.internal.os.SomeArgs r1 = (com.android.internal.os.SomeArgs) r1
                boolean r2 = r0.isEmpty()
                if (r2 == 0) goto L_0x001f
                android.util.SparseArray<android.util.ArrayMap<java.lang.String, com.android.internal.os.SomeArgs>> r2 = r3.mPendingPersistCookies
                r2.remove(r5)
            L_0x001f:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.InstantAppRegistry.CookiePersistence.removePendingPersistCookieLPr(android.content.pm.PackageParser$Package, int):com.android.internal.os.SomeArgs");
        }

        public void handleMessage(Message message) {
            int userId = message.what;
            PackageParser.Package pkg = (PackageParser.Package) message.obj;
            SomeArgs state = removePendingPersistCookieLPr(pkg, userId);
            if (state != null) {
                state.recycle();
                InstantAppRegistry.this.persistInstantApplicationCookie((byte[]) state.arg1, pkg.packageName, (File) state.arg2, userId);
            }
        }
    }
}
