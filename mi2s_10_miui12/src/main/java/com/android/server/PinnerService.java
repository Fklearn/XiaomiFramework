package com.android.server;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.IActivityManager;
import android.app.IUidObserver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserManager;
import android.provider.Settings;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.ResolverActivity;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.pm.Settings;
import com.android.server.wm.ActivityTaskManagerInternal;
import dalvik.system.DexFile;
import dalvik.system.VMRuntime;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class PinnerService extends SystemService {
    private static final boolean DEBUG = false;
    private static final int KEY_CAMERA = 0;
    private static final int KEY_HOME = 1;
    private static final int MATCH_FLAGS = 851968;
    private static final int MAX_CAMERA_PIN_SIZE = 83886080;
    private static final int MAX_HOME_PIN_SIZE = 6291456;
    private static final int PAGE_SIZE = ((int) Os.sysconf(OsConstants._SC_PAGESIZE));
    private static final String PIN_META_FILENAME = "pinlist.meta";
    private static final String TAG = "PinnerService";
    private final IActivityManager mAm;
    /* access modifiers changed from: private */
    public final ActivityManagerInternal mAmInternal;
    private final ActivityTaskManagerInternal mAtmInternal;
    private BinderService mBinderService;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.PACKAGE_REPLACED".equals(intent.getAction())) {
                String packageName = intent.getData().getSchemeSpecificPart();
                ArraySet<String> updatedPackages = new ArraySet<>();
                updatedPackages.add(packageName);
                PinnerService.this.update(updatedPackages, true);
            }
        }
    };
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    @GuardedBy({"this"})
    public final ArrayMap<Integer, Integer> mPendingRepin = new ArrayMap<>();
    private final ArraySet<Integer> mPinKeys = new ArraySet<>();
    /* access modifiers changed from: private */
    @GuardedBy({"this"})
    public final ArrayMap<Integer, PinnedApp> mPinnedApps = new ArrayMap<>();
    /* access modifiers changed from: private */
    @GuardedBy({"this"})
    public final ArrayList<PinnedFile> mPinnedFiles = new ArrayList<>();
    /* access modifiers changed from: private */
    public PinnerHandler mPinnerHandler = null;
    private final UserManager mUserManager;

    @Retention(RetentionPolicy.SOURCE)
    public @interface AppKey {
    }

    public PinnerService(Context context) {
        super(context);
        this.mContext = context;
        boolean shouldPinCamera = context.getResources().getBoolean(17891496);
        boolean shouldPinHome = context.getResources().getBoolean(17891497);
        if (shouldPinCamera) {
            this.mPinKeys.add(0);
        }
        if (shouldPinHome) {
            this.mPinKeys.add(1);
        }
        this.mPinnerHandler = new PinnerHandler(BackgroundThread.get().getLooper());
        this.mAtmInternal = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
        this.mAmInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        this.mAm = ActivityManager.getService();
        this.mUserManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        filter.addDataScheme(Settings.ATTR_PACKAGE);
        this.mContext.registerReceiver(this.mBroadcastReceiver, filter);
        registerUidListener();
        registerUserSetupCompleteListener();
    }

    public void onStart() {
        this.mBinderService = new BinderService();
        publishBinderService("pinner", this.mBinderService);
        publishLocalService(PinnerService.class, this);
        this.mPinnerHandler.obtainMessage(4001).sendToTarget();
        sendPinAppsMessage(0);
    }

    public void onSwitchUser(int userHandle) {
        if (!this.mUserManager.isManagedProfile(userHandle)) {
            sendPinAppsMessage(userHandle);
        }
    }

    public void onUnlockUser(int userHandle) {
        if (!this.mUserManager.isManagedProfile(userHandle)) {
            sendPinAppsMessage(userHandle);
        }
    }

    public void update(ArraySet<String> updatedPackages, boolean force) {
        int currentUser = ActivityManager.getCurrentUser();
        for (int i = this.mPinKeys.size() - 1; i >= 0; i--) {
            int key = this.mPinKeys.valueAt(i).intValue();
            ApplicationInfo info = getInfoForKey(key, currentUser);
            if (info != null && updatedPackages.contains(info.packageName)) {
                Slog.i(TAG, "Updating pinned files for " + info.packageName + " force=" + force);
                sendPinAppMessage(key, currentUser, force);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handlePinOnStart() {
        String[] filesToPin;
        if (SystemProperties.get("dalvik.vm.boot-image", "").endsWith("apex.art")) {
            filesToPin = this.mContext.getResources().getStringArray(17235983);
        } else {
            filesToPin = this.mContext.getResources().getStringArray(17236007);
        }
        for (String fileToPin : filesToPin) {
            PinnedFile pf = pinFile(fileToPin, Integer.MAX_VALUE, false);
            if (pf == null) {
                Slog.e(TAG, "Failed to pin file = " + fileToPin);
            } else {
                synchronized (this) {
                    this.mPinnedFiles.add(pf);
                }
            }
        }
    }

    private void registerUserSetupCompleteListener() {
        final Uri userSetupCompleteUri = Settings.Secure.getUriFor("user_setup_complete");
        this.mContext.getContentResolver().registerContentObserver(userSetupCompleteUri, false, new ContentObserver((Handler) null) {
            public void onChange(boolean selfChange, Uri uri) {
                if (userSetupCompleteUri.equals(uri)) {
                    PinnerService.this.sendPinAppMessage(1, ActivityManager.getCurrentUser(), true);
                }
            }
        }, -1);
    }

    private void registerUidListener() {
        try {
            this.mAm.registerUidObserver(new IUidObserver.Stub() {
                public void onUidGone(int uid, boolean disabled) throws RemoteException {
                    PinnerService.this.mPinnerHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$PinnerService$3$RQBbrt9b8esLBxJImxDgVTsP34I.INSTANCE, PinnerService.this, Integer.valueOf(uid)));
                }

                public void onUidActive(int uid) throws RemoteException {
                    PinnerService.this.mPinnerHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$PinnerService$3$3Ta6TX4Jq9YbpUYE5Y0r8Xt8rBw.INSTANCE, PinnerService.this, Integer.valueOf(uid)));
                }

                public void onUidIdle(int uid, boolean disabled) throws RemoteException {
                }

                public void onUidStateChanged(int uid, int procState, long procStateSeq) throws RemoteException {
                }

                public void onUidCachedChanged(int uid, boolean cached) throws RemoteException {
                }
            }, 10, 0, "system");
        } catch (RemoteException e) {
            Slog.e(TAG, "Failed to register uid observer", e);
        }
    }

    /* access modifiers changed from: private */
    public void handleUidGone(int uid) {
        updateActiveState(uid, false);
        synchronized (this) {
            int key = ((Integer) this.mPendingRepin.getOrDefault(Integer.valueOf(uid), -1)).intValue();
            if (key != -1) {
                this.mPendingRepin.remove(Integer.valueOf(uid));
                pinApp(key, ActivityManager.getCurrentUser(), false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleUidActive(int uid) {
        updateActiveState(uid, true);
    }

    private void updateActiveState(int uid, boolean active) {
        synchronized (this) {
            for (int i = this.mPinnedApps.size() - 1; i >= 0; i--) {
                PinnedApp app = this.mPinnedApps.valueAt(i);
                if (app.uid == uid) {
                    app.active = active;
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x002b, code lost:
        if (r1.hasNext() == false) goto L_0x0037;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002d, code lost:
        ((com.android.server.PinnerService.PinnedFile) r1.next()).close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0037, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0023, code lost:
        r1 = r0.iterator();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void unpinApp(int r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            android.util.ArrayMap<java.lang.Integer, com.android.server.PinnerService$PinnedApp> r0 = r3.mPinnedApps     // Catch:{ all -> 0x0038 }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0038 }
            java.lang.Object r0 = r0.get(r1)     // Catch:{ all -> 0x0038 }
            com.android.server.PinnerService$PinnedApp r0 = (com.android.server.PinnerService.PinnedApp) r0     // Catch:{ all -> 0x0038 }
            if (r0 != 0) goto L_0x0011
            monitor-exit(r3)     // Catch:{ all -> 0x0038 }
            return
        L_0x0011:
            android.util.ArrayMap<java.lang.Integer, com.android.server.PinnerService$PinnedApp> r1 = r3.mPinnedApps     // Catch:{ all -> 0x0038 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0038 }
            r1.remove(r2)     // Catch:{ all -> 0x0038 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x0038 }
            java.util.ArrayList<com.android.server.PinnerService$PinnedFile> r2 = r0.mFiles     // Catch:{ all -> 0x0038 }
            r1.<init>(r2)     // Catch:{ all -> 0x0038 }
            r0 = r1
            monitor-exit(r3)     // Catch:{ all -> 0x0038 }
            java.util.Iterator r1 = r0.iterator()
        L_0x0027:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0037
            java.lang.Object r2 = r1.next()
            com.android.server.PinnerService$PinnedFile r2 = (com.android.server.PinnerService.PinnedFile) r2
            r2.close()
            goto L_0x0027
        L_0x0037:
            return
        L_0x0038:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0038 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.PinnerService.unpinApp(int):void");
    }

    private boolean isResolverActivity(ActivityInfo info) {
        return ResolverActivity.class.getName().equals(info.name);
    }

    private ApplicationInfo getCameraInfo(int userHandle) {
        ApplicationInfo info = getApplicationInfoForIntent(new Intent("android.media.action.STILL_IMAGE_CAMERA"), userHandle, false);
        if (info == null) {
            info = getApplicationInfoForIntent(new Intent("android.media.action.STILL_IMAGE_CAMERA_SECURE"), userHandle, false);
        }
        if (info == null) {
            return getApplicationInfoForIntent(new Intent("android.media.action.STILL_IMAGE_CAMERA"), userHandle, true);
        }
        return info;
    }

    private ApplicationInfo getHomeInfo(int userHandle) {
        return getApplicationInfoForIntent(this.mAtmInternal.getHomeIntent(), userHandle, false);
    }

    private ApplicationInfo getApplicationInfoForIntent(Intent intent, int userHandle, boolean defaultToSystemApp) {
        ResolveInfo resolveInfo;
        if (intent == null || (resolveInfo = this.mContext.getPackageManager().resolveActivityAsUser(intent, MATCH_FLAGS, userHandle)) == null) {
            return null;
        }
        if (!isResolverActivity(resolveInfo.activityInfo)) {
            return resolveInfo.activityInfo.applicationInfo;
        }
        if (!defaultToSystemApp) {
            return null;
        }
        ApplicationInfo systemAppInfo = null;
        for (ResolveInfo info : this.mContext.getPackageManager().queryIntentActivitiesAsUser(intent, MATCH_FLAGS, userHandle)) {
            if ((info.activityInfo.applicationInfo.flags & 1) != 0) {
                if (systemAppInfo != null) {
                    return null;
                }
                systemAppInfo = info.activityInfo.applicationInfo;
            }
        }
        return systemAppInfo;
    }

    private void sendPinAppsMessage(int userHandle) {
        this.mPinnerHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$PinnerService$GeEX8XoHeV0LEszxat7jOSlrs4.INSTANCE, this, Integer.valueOf(userHandle)));
    }

    /* access modifiers changed from: private */
    public void pinApps(int userHandle) {
        for (int i = this.mPinKeys.size() - 1; i >= 0; i--) {
            pinApp(this.mPinKeys.valueAt(i).intValue(), userHandle, true);
        }
    }

    /* access modifiers changed from: private */
    public void sendPinAppMessage(int key, int userHandle, boolean force) {
        this.mPinnerHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$PinnerService$6bekYOn4YXi0x7vYNWO40QyAs8.INSTANCE, this, Integer.valueOf(key), Integer.valueOf(userHandle), Boolean.valueOf(force)));
    }

    /* access modifiers changed from: private */
    public void pinApp(int key, int userHandle, boolean force) {
        int uid = getUidForKey(key);
        if (force || uid == -1) {
            unpinApp(key);
            ApplicationInfo info = getInfoForKey(key, userHandle);
            if (info != null) {
                pinApp(key, info);
                return;
            }
            return;
        }
        synchronized (this) {
            this.mPendingRepin.put(Integer.valueOf(uid), Integer.valueOf(key));
        }
    }

    private int getUidForKey(int key) {
        int i;
        synchronized (this) {
            PinnedApp existing = this.mPinnedApps.get(Integer.valueOf(key));
            if (existing == null || !existing.active) {
                i = -1;
            } else {
                i = existing.uid;
            }
        }
        return i;
    }

    private ApplicationInfo getInfoForKey(int key, int userHandle) {
        if (key == 0) {
            return getCameraInfo(userHandle);
        }
        if (key != 1) {
            return null;
        }
        return getHomeInfo(userHandle);
    }

    /* access modifiers changed from: private */
    public String getNameForKey(int key) {
        if (key == 0) {
            return "Camera";
        }
        if (key != 1) {
            return null;
        }
        return "Home";
    }

    private int getSizeLimitForKey(int key) {
        if (key == 0) {
            return MAX_CAMERA_PIN_SIZE;
        }
        if (key != 1) {
            return 0;
        }
        return MAX_HOME_PIN_SIZE;
    }

    private void pinApp(int key, ApplicationInfo appInfo) {
        if (appInfo != null) {
            PinnedApp pinnedApp = new PinnedApp(appInfo);
            synchronized (this) {
                this.mPinnedApps.put(Integer.valueOf(key), pinnedApp);
            }
            int pinSizeLimit = getSizeLimitForKey(key);
            String apk = appInfo.sourceDir;
            PinnedFile pf = pinFile(apk, pinSizeLimit, true);
            if (pf == null) {
                Slog.e(TAG, "Failed to pin " + apk);
                return;
            }
            synchronized (this) {
                pinnedApp.mFiles.add(pf);
            }
            String arch = "arm";
            if (appInfo.primaryCpuAbi != null) {
                if (VMRuntime.is64BitAbi(appInfo.primaryCpuAbi)) {
                    arch = arch + "64";
                }
            } else if (VMRuntime.is64BitAbi(Build.SUPPORTED_ABIS[0])) {
                arch = arch + "64";
            }
            String[] files = null;
            try {
                files = DexFile.getDexFileOutputPaths(appInfo.getBaseCodePath(), arch);
            } catch (IOException e) {
            }
            if (files != null) {
                PinnedFile pinnedFile = pf;
                for (String file : files) {
                    PinnedFile pf2 = pinFile(file, pinSizeLimit, false);
                    if (pf2 != null) {
                        synchronized (this) {
                            pinnedApp.mFiles.add(pf2);
                        }
                    }
                }
            }
        }
    }

    private static PinnedFile pinFile(String fileToPin, int maxBytesToPin, boolean attemptPinIntrospection) {
        PinRangeSource pinRangeSource;
        ZipFile fileAsZip = null;
        InputStream pinRangeStream = null;
        if (attemptPinIntrospection) {
            try {
                fileAsZip = maybeOpenZip(fileToPin);
            } catch (Throwable th) {
                safeClose((Closeable) null);
                safeClose((Closeable) null);
                throw th;
            }
        }
        if (fileAsZip != null) {
            pinRangeStream = maybeOpenPinMetaInZip(fileAsZip, fileToPin);
        }
        Slog.d(TAG, "pinRangeStream: " + pinRangeStream);
        if (pinRangeStream != null) {
            pinRangeSource = new PinRangeSourceStream(pinRangeStream);
        } else {
            pinRangeSource = new PinRangeSourceStatic(0, Integer.MAX_VALUE);
        }
        PinnedFile pinFileRanges = pinFileRanges(fileToPin, maxBytesToPin, pinRangeSource);
        safeClose((Closeable) pinRangeStream);
        safeClose((Closeable) fileAsZip);
        return pinFileRanges;
    }

    private static ZipFile maybeOpenZip(String fileName) {
        try {
            return new ZipFile(fileName);
        } catch (IOException ex) {
            Slog.w(TAG, String.format("could not open \"%s\" as zip: pinning as blob", new Object[]{fileName}), ex);
            return null;
        }
    }

    private static InputStream maybeOpenPinMetaInZip(ZipFile zipFile, String fileName) {
        ZipEntry pinMetaEntry = zipFile.getEntry(PIN_META_FILENAME);
        if (pinMetaEntry == null) {
            return null;
        }
        try {
            return zipFile.getInputStream(pinMetaEntry);
        } catch (IOException ex) {
            Slog.w(TAG, String.format("error reading pin metadata \"%s\": pinning as blob", new Object[]{fileName}), ex);
            return null;
        }
    }

    private static abstract class PinRangeSource {
        /* access modifiers changed from: package-private */
        public abstract boolean read(PinRange pinRange);

        private PinRangeSource() {
        }
    }

    private static final class PinRangeSourceStatic extends PinRangeSource {
        private boolean mDone = false;
        private final int mPinLength;
        private final int mPinStart;

        PinRangeSourceStatic(int pinStart, int pinLength) {
            super();
            this.mPinStart = pinStart;
            this.mPinLength = pinLength;
        }

        /* access modifiers changed from: package-private */
        public boolean read(PinRange outPinRange) {
            outPinRange.start = this.mPinStart;
            outPinRange.length = this.mPinLength;
            boolean done = this.mDone;
            this.mDone = true;
            return !done;
        }
    }

    private static final class PinRangeSourceStream extends PinRangeSource {
        private boolean mDone = false;
        private final DataInputStream mStream;

        PinRangeSourceStream(InputStream stream) {
            super();
            this.mStream = new DataInputStream(stream);
        }

        /* access modifiers changed from: package-private */
        public boolean read(PinRange outPinRange) {
            if (!this.mDone) {
                try {
                    outPinRange.start = this.mStream.readInt();
                    outPinRange.length = this.mStream.readInt();
                } catch (IOException e) {
                    this.mDone = true;
                }
            }
            return !this.mDone;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:73:0x014d  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0160  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.android.server.PinnerService.PinnedFile pinFileRanges(java.lang.String r19, int r20, com.android.server.PinnerService.PinRangeSource r21) {
        /*
            r7 = r19
            java.io.FileDescriptor r0 = new java.io.FileDescriptor
            r0.<init>()
            r1 = r0
            r2 = -1
            r4 = 0
            int r0 = android.system.OsConstants.O_RDONLY     // Catch:{ ErrnoException -> 0x0128, all -> 0x011e }
            int r5 = android.system.OsConstants.O_CLOEXEC     // Catch:{ ErrnoException -> 0x0128, all -> 0x011e }
            r0 = r0 | r5
            int r5 = android.system.OsConstants.O_NOFOLLOW     // Catch:{ ErrnoException -> 0x0128, all -> 0x011e }
            r0 = r0 | r5
            r5 = 0
            java.io.FileDescriptor r16 = android.system.Os.open(r7, r0, r5)     // Catch:{ ErrnoException -> 0x0128, all -> 0x011e }
            android.system.StructStat r1 = android.system.Os.fstat(r16)     // Catch:{ ErrnoException -> 0x0116, all -> 0x010e }
            long r10 = r1.st_size     // Catch:{ ErrnoException -> 0x0116, all -> 0x010e }
            r12 = 2147483647(0x7fffffff, double:1.060997895E-314)
            long r10 = java.lang.Math.min(r10, r12)     // Catch:{ ErrnoException -> 0x0116, all -> 0x010e }
            int r6 = (int) r10
            r10 = 0
            long r12 = (long) r6
            int r14 = android.system.OsConstants.PROT_READ     // Catch:{ ErrnoException -> 0x0104, all -> 0x00fc }
            int r15 = android.system.OsConstants.MAP_SHARED     // Catch:{ ErrnoException -> 0x0104, all -> 0x00fc }
            r17 = 0
            long r10 = android.system.Os.mmap(r10, r12, r14, r15, r16, r17)     // Catch:{ ErrnoException -> 0x0104, all -> 0x00fc }
            com.android.server.PinnerService$PinRange r1 = new com.android.server.PinnerService$PinRange     // Catch:{ ErrnoException -> 0x00f1, all -> 0x00e9 }
            r1.<init>()     // Catch:{ ErrnoException -> 0x00f1, all -> 0x00e9 }
            r12 = r1
            r1 = 0
            int r2 = PAGE_SIZE     // Catch:{ ErrnoException -> 0x00f1, all -> 0x00e9 }
            int r2 = r20 % r2
            if (r2 == 0) goto L_0x005b
            int r2 = PAGE_SIZE     // Catch:{ ErrnoException -> 0x0050, all -> 0x0049 }
            int r2 = r20 % r2
            int r2 = r20 - r2
            r13 = r1
            r14 = r2
            goto L_0x005e
        L_0x0049:
            r0 = move-exception
            r14 = r20
            r15 = r21
            goto L_0x0157
        L_0x0050:
            r0 = move-exception
            r14 = r20
            r15 = r21
            r4 = r6
            r2 = r10
            r1 = r16
            goto L_0x012d
        L_0x005b:
            r14 = r20
            r13 = r1
        L_0x005e:
            if (r13 >= r14) goto L_0x00b2
            r15 = r21
            boolean r1 = r15.read(r12)     // Catch:{ ErrnoException -> 0x00ab, all -> 0x00a8 }
            if (r1 == 0) goto L_0x00b4
            int r1 = r12.start     // Catch:{ ErrnoException -> 0x00ab, all -> 0x00a8 }
            int r2 = r12.length     // Catch:{ ErrnoException -> 0x00ab, all -> 0x00a8 }
            int r3 = clamp(r5, r1, r6)     // Catch:{ ErrnoException -> 0x00ab, all -> 0x00a8 }
            r1 = r3
            int r3 = r6 - r1
            int r3 = clamp(r5, r2, r3)     // Catch:{ ErrnoException -> 0x00ab, all -> 0x00a8 }
            r2 = r3
            int r3 = r14 - r13
            int r3 = java.lang.Math.min(r3, r2)     // Catch:{ ErrnoException -> 0x00ab, all -> 0x00a8 }
            r2 = r3
            int r3 = PAGE_SIZE     // Catch:{ ErrnoException -> 0x00ab, all -> 0x00a8 }
            int r3 = r1 % r3
            int r2 = r2 + r3
            int r3 = PAGE_SIZE     // Catch:{ ErrnoException -> 0x00ab, all -> 0x00a8 }
            int r3 = r1 % r3
            int r1 = r1 - r3
            int r3 = PAGE_SIZE     // Catch:{ ErrnoException -> 0x00ab, all -> 0x00a8 }
            int r3 = r2 % r3
            if (r3 == 0) goto L_0x0097
            int r3 = PAGE_SIZE     // Catch:{ ErrnoException -> 0x00ab, all -> 0x00a8 }
            int r4 = PAGE_SIZE     // Catch:{ ErrnoException -> 0x00ab, all -> 0x00a8 }
            int r4 = r2 % r4
            int r3 = r3 - r4
            int r2 = r2 + r3
        L_0x0097:
            int r3 = r14 - r13
            int r3 = clamp(r5, r2, r3)     // Catch:{ ErrnoException -> 0x00ab, all -> 0x00a8 }
            r2 = r3
            if (r2 <= 0) goto L_0x00a6
            long r3 = (long) r1     // Catch:{ ErrnoException -> 0x00ab, all -> 0x00a8 }
            long r3 = r3 + r10
            long r8 = (long) r2     // Catch:{ ErrnoException -> 0x00ab, all -> 0x00a8 }
            android.system.Os.mlock(r3, r8)     // Catch:{ ErrnoException -> 0x00ab, all -> 0x00a8 }
        L_0x00a6:
            int r13 = r13 + r2
            goto L_0x005e
        L_0x00a8:
            r0 = move-exception
            goto L_0x0157
        L_0x00ab:
            r0 = move-exception
            r4 = r6
            r2 = r10
            r1 = r16
            goto L_0x012d
        L_0x00b2:
            r15 = r21
        L_0x00b4:
            com.android.server.PinnerService$PinnedFile r8 = new com.android.server.PinnerService$PinnedFile     // Catch:{ ErrnoException -> 0x00e1, all -> 0x00dd }
            r1 = r8
            r2 = r10
            r4 = r6
            r5 = r19
            r9 = r6
            r6 = r13
            r1.<init>(r2, r4, r5, r6)     // Catch:{ ErrnoException -> 0x00d6, all -> 0x00d2 }
            r1 = r8
            r2 = -1
            safeClose((java.io.FileDescriptor) r16)
            r4 = 0
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 < 0) goto L_0x00d1
            long r4 = (long) r9
            safeMunmap(r2, r4)
        L_0x00d1:
            return r1
        L_0x00d2:
            r0 = move-exception
            r6 = r9
            goto L_0x0157
        L_0x00d6:
            r0 = move-exception
            r4 = r9
            r2 = r10
            r1 = r16
            goto L_0x012d
        L_0x00dd:
            r0 = move-exception
            r9 = r6
            goto L_0x0157
        L_0x00e1:
            r0 = move-exception
            r9 = r6
            r4 = r9
            r2 = r10
            r1 = r16
            goto L_0x012d
        L_0x00e9:
            r0 = move-exception
            r15 = r21
            r9 = r6
            r14 = r20
            goto L_0x0157
        L_0x00f1:
            r0 = move-exception
            r15 = r21
            r9 = r6
            r14 = r20
            r4 = r9
            r2 = r10
            r1 = r16
            goto L_0x012d
        L_0x00fc:
            r0 = move-exception
            r15 = r21
            r9 = r6
            r14 = r20
            r10 = r2
            goto L_0x0157
        L_0x0104:
            r0 = move-exception
            r15 = r21
            r9 = r6
            r14 = r20
            r4 = r9
            r1 = r16
            goto L_0x012d
        L_0x010e:
            r0 = move-exception
            r15 = r21
            r14 = r20
            r10 = r2
            r6 = r4
            goto L_0x0157
        L_0x0116:
            r0 = move-exception
            r15 = r21
            r14 = r20
            r1 = r16
            goto L_0x012d
        L_0x011e:
            r0 = move-exception
            r15 = r21
            r14 = r20
            r16 = r1
            r10 = r2
            r6 = r4
            goto L_0x0157
        L_0x0128:
            r0 = move-exception
            r15 = r21
            r14 = r20
        L_0x012d:
            java.lang.String r5 = "PinnerService"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0152 }
            r6.<init>()     // Catch:{ all -> 0x0152 }
            java.lang.String r8 = "Could not pin file "
            r6.append(r8)     // Catch:{ all -> 0x0152 }
            r6.append(r7)     // Catch:{ all -> 0x0152 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0152 }
            android.util.Slog.e(r5, r6, r0)     // Catch:{ all -> 0x0152 }
            r5 = 0
            safeClose((java.io.FileDescriptor) r1)
            r8 = 0
            int r6 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r6 < 0) goto L_0x0151
            long r8 = (long) r4
            safeMunmap(r2, r8)
        L_0x0151:
            return r5
        L_0x0152:
            r0 = move-exception
            r16 = r1
            r10 = r2
            r6 = r4
        L_0x0157:
            safeClose((java.io.FileDescriptor) r16)
            r1 = 0
            int r1 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r1 < 0) goto L_0x0164
            long r1 = (long) r6
            safeMunmap(r10, r1)
        L_0x0164:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.PinnerService.pinFileRanges(java.lang.String, int, com.android.server.PinnerService$PinRangeSource):com.android.server.PinnerService$PinnedFile");
    }

    private static int clamp(int min, int value, int max) {
        return Math.max(min, Math.min(value, max));
    }

    /* access modifiers changed from: private */
    public static void safeMunmap(long address, long mapSize) {
        try {
            Os.munmap(address, mapSize);
        } catch (ErrnoException ex) {
            Slog.w(TAG, "ignoring error in unmap", ex);
        }
    }

    private static void safeClose(FileDescriptor fd) {
        if (fd != null && fd.valid()) {
            try {
                Os.close(fd);
            } catch (ErrnoException ex) {
                if (ex.errno == OsConstants.EBADF) {
                    throw new AssertionError(ex);
                }
            }
        }
    }

    private static void safeClose(Closeable thing) {
        if (thing != null) {
            try {
                thing.close();
            } catch (IOException ex) {
                Slog.w(TAG, "ignoring error closing resource: " + thing, ex);
            }
        }
    }

    private final class BinderService extends Binder {
        private BinderService() {
        }

        /* access modifiers changed from: protected */
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            PrintWriter printWriter = pw;
            if (DumpUtils.checkDumpPermission(PinnerService.this.mContext, PinnerService.TAG, printWriter)) {
                synchronized (PinnerService.this) {
                    long totalSize = 0;
                    Iterator it = PinnerService.this.mPinnedFiles.iterator();
                    while (it.hasNext()) {
                        PinnedFile pinnedFile = (PinnedFile) it.next();
                        printWriter.format("%s %s\n", new Object[]{pinnedFile.fileName, Integer.valueOf(pinnedFile.bytesPinned)});
                        totalSize += (long) pinnedFile.bytesPinned;
                    }
                    pw.println();
                    for (Integer intValue : PinnerService.this.mPinnedApps.keySet()) {
                        int key = intValue.intValue();
                        PinnedApp app = (PinnedApp) PinnerService.this.mPinnedApps.get(Integer.valueOf(key));
                        printWriter.print(PinnerService.this.getNameForKey(key));
                        printWriter.print(" uid=");
                        printWriter.print(app.uid);
                        printWriter.print(" active=");
                        printWriter.print(app.active);
                        pw.println();
                        Iterator<PinnedFile> it2 = ((PinnedApp) PinnerService.this.mPinnedApps.get(Integer.valueOf(key))).mFiles.iterator();
                        while (it2.hasNext()) {
                            PinnedFile pf = it2.next();
                            printWriter.print("  ");
                            printWriter.format("%s %s\n", new Object[]{pf.fileName, Integer.valueOf(pf.bytesPinned)});
                            totalSize += (long) pf.bytesPinned;
                        }
                    }
                    printWriter.format("Total size: %s\n", new Object[]{Long.valueOf(totalSize)});
                    pw.println();
                    if (!PinnerService.this.mPendingRepin.isEmpty()) {
                        printWriter.print("Pending repin: ");
                        for (Integer intValue2 : PinnerService.this.mPendingRepin.values()) {
                            printWriter.print(PinnerService.this.getNameForKey(intValue2.intValue()));
                            printWriter.print(' ');
                        }
                        pw.println();
                    }
                }
            }
        }
    }

    private static final class PinnedFile implements AutoCloseable {
        final int bytesPinned;
        final String fileName;
        private long mAddress;
        final int mapSize;

        PinnedFile(long address, int mapSize2, String fileName2, int bytesPinned2) {
            this.mAddress = address;
            this.mapSize = mapSize2;
            this.fileName = fileName2;
            this.bytesPinned = bytesPinned2;
        }

        public void close() {
            long j = this.mAddress;
            if (j >= 0) {
                PinnerService.safeMunmap(j, (long) this.mapSize);
                this.mAddress = -1;
            }
        }

        public void finalize() {
            close();
        }
    }

    static final class PinRange {
        int length;
        int start;

        PinRange() {
        }
    }

    private final class PinnedApp {
        boolean active;
        final ArrayList<PinnedFile> mFiles;
        final int uid;

        private PinnedApp(ApplicationInfo appInfo) {
            this.mFiles = new ArrayList<>();
            this.uid = appInfo.uid;
            this.active = PinnerService.this.mAmInternal.isUidActive(this.uid);
        }
    }

    final class PinnerHandler extends Handler {
        static final int PIN_ONSTART_MSG = 4001;

        public PinnerHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            if (msg.what != PIN_ONSTART_MSG) {
                super.handleMessage(msg);
            } else {
                PinnerService.this.handlePinOnStart();
            }
        }
    }
}
