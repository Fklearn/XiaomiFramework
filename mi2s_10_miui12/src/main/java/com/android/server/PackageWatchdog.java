package com.android.server;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.VersionedPackage;
import android.net.NetworkStackClient;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.DeviceConfig;
import android.service.watchdog.ExplicitHealthCheckService;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.XmlUtils;
import com.android.server.PackageWatchdog;
import com.android.server.job.controllers.JobStatus;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class PackageWatchdog {
    private static final String ATTR_DURATION = "duration";
    private static final String ATTR_EXPLICIT_HEALTH_CHECK_DURATION = "health-check-duration";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_PASSED_HEALTH_CHECK = "passed-health-check";
    private static final String ATTR_VERSION = "version";
    private static final int DB_VERSION = 1;
    private static final boolean DEFAULT_EXPLICIT_HEALTH_CHECK_ENABLED = true;
    private static final int DEFAULT_TRIGGER_FAILURE_COUNT = 5;
    private static final int DEFAULT_TRIGGER_FAILURE_DURATION_MS = ((int) TimeUnit.MINUTES.toMillis(1));
    public static final int FAILURE_REASON_APP_CRASH = 3;
    public static final int FAILURE_REASON_APP_NOT_RESPONDING = 4;
    public static final int FAILURE_REASON_EXPLICIT_HEALTH_CHECK = 2;
    public static final int FAILURE_REASON_NATIVE_CRASH = 1;
    public static final int FAILURE_REASON_UNKNOWN = 0;
    static final String PROPERTY_WATCHDOG_EXPLICIT_HEALTH_CHECK_ENABLED = "watchdog_explicit_health_check_enabled";
    static final String PROPERTY_WATCHDOG_TRIGGER_DURATION_MILLIS = "watchdog_trigger_failure_duration_millis";
    static final String PROPERTY_WATCHDOG_TRIGGER_FAILURE_COUNT = "watchdog_trigger_failure_count";
    private static final String TAG = "PackageWatchdog";
    private static final String TAG_OBSERVER = "observer";
    private static final String TAG_PACKAGE = "package";
    private static final String TAG_PACKAGE_WATCHDOG = "package-watchdog";
    @GuardedBy({"PackageWatchdog.class"})
    private static PackageWatchdog sPackageWatchdog;
    @GuardedBy({"mLock"})
    private final ArrayMap<String, ObserverInternal> mAllObservers;
    private final Context mContext;
    private final ExplicitHealthCheckController mHealthCheckController;
    @GuardedBy({"mLock"})
    private boolean mIsHealthCheckEnabled;
    @GuardedBy({"mLock"})
    private boolean mIsPackagesReady;
    private final Object mLock;
    private final Handler mLongTaskHandler;
    private final NetworkStackClient mNetworkStackClient;
    private final AtomicFile mPolicyFile;
    private final Handler mShortTaskHandler;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public int mTriggerFailureCount;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public int mTriggerFailureDurationMs;
    @GuardedBy({"mLock"})
    private long mUptimeAtLastStateSync;

    @Retention(RetentionPolicy.SOURCE)
    public @interface FailureReasons {
    }

    public interface PackageHealthObserver {
        boolean execute(VersionedPackage versionedPackage, int i);

        String getName();

        int onHealthCheckFailed(VersionedPackage versionedPackage);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PackageHealthObserverImpact {
        public static final int USER_IMPACT_HIGH = 5;
        public static final int USER_IMPACT_LOW = 1;
        public static final int USER_IMPACT_MEDIUM = 3;
        public static final int USER_IMPACT_NONE = 0;
    }

    private PackageWatchdog(Context context) {
        this(context, new AtomicFile(new File(new File(Environment.getDataDirectory(), "system"), "package-watchdog.xml")), new Handler(Looper.myLooper()), BackgroundThread.getHandler(), new ExplicitHealthCheckController(context), NetworkStackClient.getInstance());
    }

    @VisibleForTesting
    PackageWatchdog(Context context, AtomicFile policyFile, Handler shortTaskHandler, Handler longTaskHandler, ExplicitHealthCheckController controller, NetworkStackClient networkStackClient) {
        this.mLock = new Object();
        this.mAllObservers = new ArrayMap<>();
        this.mIsHealthCheckEnabled = true;
        this.mTriggerFailureDurationMs = DEFAULT_TRIGGER_FAILURE_DURATION_MS;
        this.mTriggerFailureCount = 5;
        this.mContext = context;
        this.mPolicyFile = policyFile;
        this.mShortTaskHandler = shortTaskHandler;
        this.mLongTaskHandler = longTaskHandler;
        this.mHealthCheckController = controller;
        this.mNetworkStackClient = networkStackClient;
        loadFromFile();
    }

    public static PackageWatchdog getInstance(Context context) {
        PackageWatchdog packageWatchdog;
        synchronized (PackageWatchdog.class) {
            if (sPackageWatchdog == null) {
                sPackageWatchdog = new PackageWatchdog(context);
            }
            packageWatchdog = sPackageWatchdog;
        }
        return packageWatchdog;
    }

    public void onPackagesReady() {
        synchronized (this.mLock) {
            this.mIsPackagesReady = true;
            this.mHealthCheckController.setCallbacks(new Consumer() {
                public final void accept(Object obj) {
                    PackageWatchdog.this.lambda$onPackagesReady$0$PackageWatchdog((String) obj);
                }
            }, new Consumer() {
                public final void accept(Object obj) {
                    PackageWatchdog.this.lambda$onPackagesReady$1$PackageWatchdog((List) obj);
                }
            }, new Runnable() {
                public final void run() {
                    PackageWatchdog.this.lambda$onPackagesReady$2$PackageWatchdog();
                }
            });
            setPropertyChangedListenerLocked();
            updateConfigs();
            registerNetworkStackHealthListener();
        }
    }

    public void registerHealthObserver(PackageHealthObserver observer) {
        synchronized (this.mLock) {
            ObserverInternal internalObserver = this.mAllObservers.get(observer.getName());
            if (internalObserver != null) {
                internalObserver.mRegisteredObserver = observer;
            }
        }
    }

    public void startObservingHealth(PackageHealthObserver observer, List<String> packageNames, long durationMs) {
        if (packageNames.isEmpty()) {
            Slog.wtf(TAG, "No packages to observe, " + observer.getName());
        } else if (durationMs >= 1) {
            List<MonitoredPackage> packages = new ArrayList<>();
            for (int i = 0; i < packageNames.size(); i++) {
                packages.add(new MonitoredPackage(this, packageNames.get(i), durationMs, false));
            }
            syncState("observing new packages");
            synchronized (this.mLock) {
                ObserverInternal oldObserver = this.mAllObservers.get(observer.getName());
                if (oldObserver == null) {
                    Slog.d(TAG, observer.getName() + " started monitoring health of packages " + packageNames);
                    this.mAllObservers.put(observer.getName(), new ObserverInternal(observer.getName(), packages));
                } else {
                    Slog.d(TAG, observer.getName() + " added the following packages to monitor " + packageNames);
                    oldObserver.updatePackagesLocked(packages);
                }
            }
            registerHealthObserver(observer);
            syncState("updated observers");
        } else {
            throw new IllegalArgumentException("Invalid duration " + durationMs + "ms for observer " + observer.getName() + ". Not observing packages " + packageNames);
        }
    }

    public void unregisterHealthObserver(PackageHealthObserver observer) {
        synchronized (this.mLock) {
            this.mAllObservers.remove(observer.getName());
        }
        syncState("unregistering observer: " + observer.getName());
    }

    public Set<String> getPackages(PackageHealthObserver observer) {
        synchronized (this.mLock) {
            int i = 0;
            while (i < this.mAllObservers.size()) {
                if (!observer.getName().equals(this.mAllObservers.keyAt(i))) {
                    i++;
                } else if (observer.equals(this.mAllObservers.valueAt(i).mRegisteredObserver)) {
                    Set<String> keySet = this.mAllObservers.valueAt(i).mPackages.keySet();
                    return keySet;
                } else {
                    Set<String> emptySet = Collections.emptySet();
                    return emptySet;
                }
            }
            return null;
        }
    }

    public void onPackageFailure(List<VersionedPackage> packages, int failureReason) {
        this.mLongTaskHandler.post(new Runnable(packages, failureReason) {
            private final /* synthetic */ List f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                PackageWatchdog.this.lambda$onPackageFailure$3$PackageWatchdog(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$onPackageFailure$3$PackageWatchdog(List packages, int failureReason) {
        int impact;
        synchronized (this.mLock) {
            if (!this.mAllObservers.isEmpty()) {
                for (int pIndex = 0; pIndex < packages.size(); pIndex++) {
                    VersionedPackage versionedPackage = (VersionedPackage) packages.get(pIndex);
                    PackageHealthObserver currentObserverToNotify = null;
                    int currentObserverImpact = Integer.MAX_VALUE;
                    for (int oIndex = 0; oIndex < this.mAllObservers.size(); oIndex++) {
                        ObserverInternal observer = this.mAllObservers.valueAt(oIndex);
                        PackageHealthObserver registeredObserver = observer.mRegisteredObserver;
                        if (registeredObserver != null && observer.onPackageFailureLocked(versionedPackage.getPackageName()) && (impact = registeredObserver.onHealthCheckFailed(versionedPackage)) != 0 && impact < currentObserverImpact) {
                            currentObserverToNotify = registeredObserver;
                            currentObserverImpact = impact;
                        }
                    }
                    if (currentObserverToNotify != null) {
                        currentObserverToNotify.execute(versionedPackage, failureReason);
                    }
                }
            }
        }
    }

    public void writeNow() {
        synchronized (this.mLock) {
            if (!this.mAllObservers.isEmpty()) {
                this.mLongTaskHandler.removeCallbacks(new Runnable() {
                    public final void run() {
                        PackageWatchdog.this.saveToFileAsync();
                    }
                });
                pruneObserversLocked();
                saveToFile();
                Slog.i(TAG, "Last write to update package durations");
            }
        }
    }

    private void setExplicitHealthCheckEnabled(boolean enabled) {
        synchronized (this.mLock) {
            this.mIsHealthCheckEnabled = enabled;
            this.mHealthCheckController.setEnabled(enabled);
            StringBuilder sb = new StringBuilder();
            sb.append("health check state ");
            sb.append(enabled ? "enabled" : "disabled");
            syncState(sb.toString());
        }
    }

    /* access modifiers changed from: package-private */
    public long getTriggerFailureCount() {
        long j;
        synchronized (this.mLock) {
            j = (long) this.mTriggerFailureCount;
        }
        return j;
    }

    /* access modifiers changed from: private */
    /* renamed from: syncRequestsAsync */
    public void lambda$onPackagesReady$2$PackageWatchdog() {
        this.mShortTaskHandler.removeCallbacks(new Runnable() {
            public final void run() {
                PackageWatchdog.this.syncRequests();
            }
        });
        this.mShortTaskHandler.post(new Runnable() {
            public final void run() {
                PackageWatchdog.this.syncRequests();
            }
        });
    }

    /* access modifiers changed from: private */
    public void syncRequests() {
        Set<String> packages = null;
        synchronized (this.mLock) {
            if (this.mIsPackagesReady) {
                packages = getPackagesPendingHealthChecksLocked();
            }
        }
        if (packages != null) {
            Slog.i(TAG, "Syncing health check requests for packages: " + packages);
            this.mHealthCheckController.syncRequests(packages);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onHealthCheckPassed */
    public void lambda$onPackagesReady$0$PackageWatchdog(String packageName) {
        Slog.i(TAG, "Health check passed for package: " + packageName);
        boolean isStateChanged = false;
        synchronized (this.mLock) {
            for (int observerIdx = 0; observerIdx < this.mAllObservers.size(); observerIdx++) {
                MonitoredPackage monitoredPackage = this.mAllObservers.valueAt(observerIdx).mPackages.get(packageName);
                if (monitoredPackage != null) {
                    isStateChanged |= monitoredPackage.getHealthCheckStateLocked() != monitoredPackage.tryPassHealthCheckLocked();
                }
            }
        }
        if (isStateChanged) {
            syncState("health check passed for " + packageName);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onSupportedPackages */
    public void lambda$onPackagesReady$1$PackageWatchdog(List<ExplicitHealthCheckService.PackageConfig> supportedPackages) {
        int newState;
        boolean isStateChanged = false;
        Map<String, Long> supportedPackageTimeouts = new ArrayMap<>();
        for (ExplicitHealthCheckService.PackageConfig info : supportedPackages) {
            supportedPackageTimeouts.put(info.getPackageName(), Long.valueOf(info.getHealthCheckTimeoutMillis()));
        }
        synchronized (this.mLock) {
            Slog.d(TAG, "Received supported packages " + supportedPackages);
            for (ObserverInternal observerInternal : this.mAllObservers.values()) {
                for (MonitoredPackage monitoredPackage : observerInternal.mPackages.values()) {
                    String packageName = monitoredPackage.getName();
                    int oldState = monitoredPackage.getHealthCheckStateLocked();
                    if (supportedPackageTimeouts.containsKey(packageName)) {
                        newState = monitoredPackage.setHealthCheckActiveLocked(supportedPackageTimeouts.get(packageName).longValue());
                    } else {
                        newState = monitoredPackage.tryPassHealthCheckLocked();
                    }
                    isStateChanged |= oldState != newState;
                }
            }
        }
        if (isStateChanged) {
            syncState("updated health check supported packages " + supportedPackages);
        }
    }

    @GuardedBy({"mLock"})
    private Set<String> getPackagesPendingHealthChecksLocked() {
        Slog.d(TAG, "Getting all observed packages pending health checks");
        Set<String> packages = new ArraySet<>();
        for (ObserverInternal observer : this.mAllObservers.values()) {
            for (MonitoredPackage monitoredPackage : observer.mPackages.values()) {
                String packageName = monitoredPackage.getName();
                if (monitoredPackage.isPendingHealthChecksLocked()) {
                    packages.add(packageName);
                }
            }
        }
        return packages;
    }

    private void syncState(String reason) {
        synchronized (this.mLock) {
            Slog.i(TAG, "Syncing state, reason: " + reason);
            pruneObserversLocked();
            saveToFileAsync();
            lambda$onPackagesReady$2$PackageWatchdog();
            scheduleNextSyncStateLocked();
        }
    }

    /* access modifiers changed from: private */
    public void syncStateWithScheduledReason() {
        syncState("scheduled");
    }

    @GuardedBy({"mLock"})
    private void scheduleNextSyncStateLocked() {
        long durationMs = getNextStateSyncMillisLocked();
        this.mShortTaskHandler.removeCallbacks(new Runnable() {
            public final void run() {
                PackageWatchdog.this.syncStateWithScheduledReason();
            }
        });
        if (durationMs == JobStatus.NO_LATEST_RUNTIME) {
            Slog.i(TAG, "Cancelling state sync, nothing to sync");
            this.mUptimeAtLastStateSync = 0;
            return;
        }
        Slog.i(TAG, "Scheduling next state sync in " + durationMs + "ms");
        this.mUptimeAtLastStateSync = SystemClock.uptimeMillis();
        this.mShortTaskHandler.postDelayed(new Runnable() {
            public final void run() {
                PackageWatchdog.this.syncStateWithScheduledReason();
            }
        }, durationMs);
    }

    @GuardedBy({"mLock"})
    private long getNextStateSyncMillisLocked() {
        long shortestDurationMs = JobStatus.NO_LATEST_RUNTIME;
        for (int oIndex = 0; oIndex < this.mAllObservers.size(); oIndex++) {
            ArrayMap<String, MonitoredPackage> packages = this.mAllObservers.valueAt(oIndex).mPackages;
            for (int pIndex = 0; pIndex < packages.size(); pIndex++) {
                long duration = packages.valueAt(pIndex).getShortestScheduleDurationMsLocked();
                if (duration < shortestDurationMs) {
                    shortestDurationMs = duration;
                }
            }
        }
        return shortestDurationMs;
    }

    @GuardedBy({"mLock"})
    private void pruneObserversLocked() {
        long elapsedMs = this.mUptimeAtLastStateSync == 0 ? 0 : SystemClock.uptimeMillis() - this.mUptimeAtLastStateSync;
        if (elapsedMs <= 0) {
            Slog.i(TAG, "Not pruning observers, elapsed time: " + elapsedMs + "ms");
            return;
        }
        Slog.i(TAG, "Removing " + elapsedMs + "ms from all packages on all observers");
        Iterator<ObserverInternal> it = this.mAllObservers.values().iterator();
        while (it.hasNext()) {
            ObserverInternal observer = it.next();
            Set<MonitoredPackage> failedPackages = observer.prunePackagesLocked(elapsedMs);
            if (!failedPackages.isEmpty()) {
                onHealthCheckFailed(observer, failedPackages);
            }
            if (observer.mPackages.isEmpty()) {
                Slog.i(TAG, "Discarding observer " + observer.mName + ". All packages expired");
                it.remove();
            }
        }
    }

    private void onHealthCheckFailed(ObserverInternal observer, Set<MonitoredPackage> failedPackages) {
        this.mLongTaskHandler.post(new Runnable(observer, failedPackages) {
            private final /* synthetic */ PackageWatchdog.ObserverInternal f$1;
            private final /* synthetic */ Set f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                PackageWatchdog.this.lambda$onHealthCheckFailed$4$PackageWatchdog(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$onHealthCheckFailed$4$PackageWatchdog(ObserverInternal observer, Set failedPackages) {
        synchronized (this.mLock) {
            PackageHealthObserver registeredObserver = observer.mRegisteredObserver;
            if (registeredObserver != null) {
                Iterator<MonitoredPackage> it = failedPackages.iterator();
                while (it.hasNext()) {
                    String failedPackage = it.next().getName();
                    Slog.i(TAG, "Explicit health check failed for package " + failedPackage);
                    VersionedPackage versionedPkg = getVersionedPackage(failedPackage);
                    if (versionedPkg == null) {
                        Slog.w(TAG, "Explicit health check failed but could not find package " + failedPackage);
                        versionedPkg = new VersionedPackage(failedPackage, 0);
                    }
                    registeredObserver.execute(versionedPkg, 2);
                }
            }
        }
    }

    private VersionedPackage getVersionedPackage(String packageName) {
        PackageManager pm = this.mContext.getPackageManager();
        if (pm == null) {
            return null;
        }
        try {
            return new VersionedPackage(packageName, pm.getPackageInfo(packageName, 0).getLongVersionCode());
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private void loadFromFile() {
        InputStream infile = null;
        this.mAllObservers.clear();
        try {
            infile = this.mPolicyFile.openRead();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(infile, StandardCharsets.UTF_8.name());
            XmlUtils.beginDocument(parser, TAG_PACKAGE_WATCHDOG);
            int outerDepth = parser.getDepth();
            while (XmlUtils.nextElementWithin(parser, outerDepth)) {
                ObserverInternal observer = ObserverInternal.read(parser, this);
                if (observer != null) {
                    this.mAllObservers.put(observer.mName, observer);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException | NumberFormatException | XmlPullParserException e2) {
            Slog.wtf(TAG, "Unable to read monitored packages, deleting file", e2);
            this.mPolicyFile.delete();
        } catch (Throwable th) {
            IoUtils.closeQuietly((AutoCloseable) null);
            throw th;
        }
        IoUtils.closeQuietly(infile);
    }

    private void setPropertyChangedListenerLocked() {
        DeviceConfig.addOnPropertiesChangedListener("rollback", this.mContext.getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() {
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                PackageWatchdog.this.lambda$setPropertyChangedListenerLocked$5$PackageWatchdog(properties);
            }
        });
    }

    public /* synthetic */ void lambda$setPropertyChangedListenerLocked$5$PackageWatchdog(DeviceConfig.Properties properties) {
        if ("rollback".equals(properties.getNamespace())) {
            updateConfigs();
        }
    }

    private void updateConfigs() {
        synchronized (this.mLock) {
            this.mTriggerFailureCount = DeviceConfig.getInt("rollback", PROPERTY_WATCHDOG_TRIGGER_FAILURE_COUNT, 5);
            if (this.mTriggerFailureCount <= 0) {
                this.mTriggerFailureCount = 5;
            }
            this.mTriggerFailureDurationMs = DeviceConfig.getInt("rollback", PROPERTY_WATCHDOG_TRIGGER_DURATION_MILLIS, DEFAULT_TRIGGER_FAILURE_DURATION_MS);
            if (this.mTriggerFailureDurationMs <= 0) {
                this.mTriggerFailureDurationMs = 5;
            }
            setExplicitHealthCheckEnabled(DeviceConfig.getBoolean("rollback", PROPERTY_WATCHDOG_EXPLICIT_HEALTH_CHECK_ENABLED, true));
        }
    }

    private void registerNetworkStackHealthListener() {
        this.mNetworkStackClient.registerHealthListener(new NetworkStackClient.NetworkStackHealthListener() {
            public final void onNetworkStackFailure(String str) {
                PackageWatchdog.this.lambda$registerNetworkStackHealthListener$6$PackageWatchdog(str);
            }
        });
    }

    public /* synthetic */ void lambda$registerNetworkStackHealthListener$6$PackageWatchdog(String packageName) {
        VersionedPackage pkg = getVersionedPackage(packageName);
        if (pkg == null) {
            Slog.wtf(TAG, "NetworkStack failed but could not find its package");
            return;
        }
        List<VersionedPackage> pkgList = Collections.singletonList(pkg);
        long failureCount = getTriggerFailureCount();
        for (int i = 0; ((long) i) < failureCount; i++) {
            onPackageFailure(pkgList, 2);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0064, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0079, code lost:
        libcore.io.IoUtils.closeQuietly(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x007c, code lost:
        throw r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x007d, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x008a, code lost:
        throw r1;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:11:0x005f, B:18:0x006b] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean saveToFile() {
        /*
            r8 = this;
            java.lang.String r0 = "PackageWatchdog"
            java.lang.String r1 = "Saving observer state to file"
            android.util.Slog.i(r0, r1)
            java.lang.Object r0 = r8.mLock
            monitor-enter(r0)
            r1 = 0
            android.util.AtomicFile r2 = r8.mPolicyFile     // Catch:{ IOException -> 0x007f }
            java.io.FileOutputStream r2 = r2.startWrite()     // Catch:{ IOException -> 0x007f }
            com.android.internal.util.FastXmlSerializer r3 = new com.android.internal.util.FastXmlSerializer     // Catch:{ IOException -> 0x0066 }
            r3.<init>()     // Catch:{ IOException -> 0x0066 }
            java.nio.charset.Charset r4 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException -> 0x0066 }
            java.lang.String r4 = r4.name()     // Catch:{ IOException -> 0x0066 }
            r3.setOutput(r2, r4)     // Catch:{ IOException -> 0x0066 }
            r4 = 1
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r4)     // Catch:{ IOException -> 0x0066 }
            r6 = 0
            r3.startDocument(r6, r5)     // Catch:{ IOException -> 0x0066 }
            java.lang.String r5 = "package-watchdog"
            r3.startTag(r6, r5)     // Catch:{ IOException -> 0x0066 }
            java.lang.String r5 = "version"
            java.lang.String r7 = java.lang.Integer.toString(r4)     // Catch:{ IOException -> 0x0066 }
            r3.attribute(r6, r5, r7)     // Catch:{ IOException -> 0x0066 }
            r5 = r1
        L_0x003a:
            android.util.ArrayMap<java.lang.String, com.android.server.PackageWatchdog$ObserverInternal> r7 = r8.mAllObservers     // Catch:{ IOException -> 0x0066 }
            int r7 = r7.size()     // Catch:{ IOException -> 0x0066 }
            if (r5 >= r7) goto L_0x0050
            android.util.ArrayMap<java.lang.String, com.android.server.PackageWatchdog$ObserverInternal> r7 = r8.mAllObservers     // Catch:{ IOException -> 0x0066 }
            java.lang.Object r7 = r7.valueAt(r5)     // Catch:{ IOException -> 0x0066 }
            com.android.server.PackageWatchdog$ObserverInternal r7 = (com.android.server.PackageWatchdog.ObserverInternal) r7     // Catch:{ IOException -> 0x0066 }
            r7.writeLocked(r3)     // Catch:{ IOException -> 0x0066 }
            int r5 = r5 + 1
            goto L_0x003a
        L_0x0050:
            java.lang.String r5 = "package-watchdog"
            r3.endTag(r6, r5)     // Catch:{ IOException -> 0x0066 }
            r3.endDocument()     // Catch:{ IOException -> 0x0066 }
            android.util.AtomicFile r5 = r8.mPolicyFile     // Catch:{ IOException -> 0x0066 }
            r5.finishWrite(r2)     // Catch:{ IOException -> 0x0066 }
            libcore.io.IoUtils.closeQuietly(r2)     // Catch:{ all -> 0x007d }
            monitor-exit(r0)     // Catch:{ all -> 0x007d }
            return r4
        L_0x0064:
            r1 = move-exception
            goto L_0x0079
        L_0x0066:
            r3 = move-exception
            java.lang.String r4 = "PackageWatchdog"
            java.lang.String r5 = "Failed to save monitored packages, restoring backup"
            android.util.Slog.w(r4, r5, r3)     // Catch:{ all -> 0x0064 }
            android.util.AtomicFile r4 = r8.mPolicyFile     // Catch:{ all -> 0x0064 }
            r4.failWrite(r2)     // Catch:{ all -> 0x0064 }
            libcore.io.IoUtils.closeQuietly(r2)     // Catch:{ all -> 0x007d }
            monitor-exit(r0)     // Catch:{ all -> 0x007d }
            return r1
        L_0x0079:
            libcore.io.IoUtils.closeQuietly(r2)     // Catch:{ all -> 0x007d }
            throw r1     // Catch:{ all -> 0x007d }
        L_0x007d:
            r1 = move-exception
            goto L_0x0089
        L_0x007f:
            r2 = move-exception
            java.lang.String r3 = "PackageWatchdog"
            java.lang.String r4 = "Cannot update monitored packages"
            android.util.Slog.w(r3, r4, r2)     // Catch:{ all -> 0x007d }
            monitor-exit(r0)     // Catch:{ all -> 0x007d }
            return r1
        L_0x0089:
            monitor-exit(r0)     // Catch:{ all -> 0x007d }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.PackageWatchdog.saveToFile():boolean");
    }

    /* access modifiers changed from: private */
    public void saveToFileAsync() {
        if (!this.mLongTaskHandler.hasCallbacks(new Runnable() {
            public final void run() {
                boolean unused = PackageWatchdog.this.saveToFile();
            }
        })) {
            this.mLongTaskHandler.post(new Runnable() {
                public final void run() {
                    boolean unused = PackageWatchdog.this.saveToFile();
                }
            });
        }
    }

    private static class ObserverInternal {
        public final String mName;
        @GuardedBy({"mLock"})
        public final ArrayMap<String, MonitoredPackage> mPackages = new ArrayMap<>();
        @GuardedBy({"mLock"})
        public PackageHealthObserver mRegisteredObserver;

        ObserverInternal(String name, List<MonitoredPackage> packages) {
            this.mName = name;
            updatePackagesLocked(packages);
        }

        @GuardedBy({"mLock"})
        public boolean writeLocked(XmlSerializer out) {
            try {
                out.startTag((String) null, PackageWatchdog.TAG_OBSERVER);
                out.attribute((String) null, "name", this.mName);
                for (int i = 0; i < this.mPackages.size(); i++) {
                    this.mPackages.valueAt(i).writeLocked(out);
                }
                out.endTag((String) null, PackageWatchdog.TAG_OBSERVER);
                return true;
            } catch (IOException e) {
                Slog.w(PackageWatchdog.TAG, "Cannot save observer", e);
                return false;
            }
        }

        @GuardedBy({"mLock"})
        public void updatePackagesLocked(List<MonitoredPackage> packages) {
            for (int pIndex = 0; pIndex < packages.size(); pIndex++) {
                MonitoredPackage p = packages.get(pIndex);
                this.mPackages.put(p.mName, p);
            }
        }

        /* access modifiers changed from: private */
        @GuardedBy({"mLock"})
        public Set<MonitoredPackage> prunePackagesLocked(long elapsedMs) {
            Set<MonitoredPackage> failedPackages = new ArraySet<>();
            Iterator<MonitoredPackage> it = this.mPackages.values().iterator();
            while (it.hasNext()) {
                MonitoredPackage p = it.next();
                int oldState = p.getHealthCheckStateLocked();
                int newState = p.handleElapsedTimeLocked(elapsedMs);
                if (oldState != 3 && newState == 3) {
                    Slog.i(PackageWatchdog.TAG, "Package " + p.mName + " failed health check");
                    failedPackages.add(p);
                }
                if (p.isExpiredLocked()) {
                    it.remove();
                }
            }
            return failedPackages;
        }

        @GuardedBy({"mLock"})
        public boolean onPackageFailureLocked(String packageName) {
            MonitoredPackage p = this.mPackages.get(packageName);
            if (p != null) {
                return p.onFailureLocked();
            }
            return false;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:26:0x00a3, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x00a4, code lost:
            android.util.Slog.wtf(com.android.server.PackageWatchdog.TAG, "Unable to read observer " + r2, r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x00b8, code lost:
            return null;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x00a3 A[ExcHandler: IOException | XmlPullParserException (r0v2 'e' java.lang.Exception A[CUSTOM_DECLARE]), Splitter:B:9:0x0033] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static com.android.server.PackageWatchdog.ObserverInternal read(org.xmlpull.v1.XmlPullParser r16, com.android.server.PackageWatchdog r17) {
            /*
                r1 = r16
                r0 = 0
                java.lang.String r2 = r16.getName()
                java.lang.String r3 = "observer"
                boolean r2 = r3.equals(r2)
                java.lang.String r3 = "name"
                java.lang.String r4 = "PackageWatchdog"
                r5 = 0
                if (r2 == 0) goto L_0x0028
                java.lang.String r0 = r1.getAttributeValue(r5, r3)
                boolean r2 = android.text.TextUtils.isEmpty(r0)
                if (r2 == 0) goto L_0x0026
                java.lang.String r2 = "Unable to read observer name"
                android.util.Slog.wtf(r4, r2)
                return r5
            L_0x0026:
                r2 = r0
                goto L_0x0029
            L_0x0028:
                r2 = r0
            L_0x0029:
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                r6 = r0
                int r7 = r16.getDepth()
            L_0x0033:
                boolean r0 = com.android.internal.util.XmlUtils.nextElementWithin(r1, r7)     // Catch:{ IOException | XmlPullParserException -> 0x00a3 }
                if (r0 == 0) goto L_0x0095
                java.lang.String r0 = "package"
                java.lang.String r8 = r16.getName()     // Catch:{ IOException | XmlPullParserException -> 0x00a3 }
                boolean r0 = r0.equals(r8)     // Catch:{ IOException | XmlPullParserException -> 0x00a3 }
                if (r0 == 0) goto L_0x0033
                java.lang.String r10 = r1.getAttributeValue(r5, r3)     // Catch:{ NumberFormatException -> 0x007f, IOException | XmlPullParserException -> 0x00a3 }
                java.lang.String r0 = "duration"
                java.lang.String r0 = r1.getAttributeValue(r5, r0)     // Catch:{ NumberFormatException -> 0x007f, IOException | XmlPullParserException -> 0x00a3 }
                long r11 = java.lang.Long.parseLong(r0)     // Catch:{ NumberFormatException -> 0x007f, IOException | XmlPullParserException -> 0x00a3 }
                java.lang.String r0 = "health-check-duration"
                java.lang.String r0 = r1.getAttributeValue(r5, r0)     // Catch:{ NumberFormatException -> 0x007f, IOException | XmlPullParserException -> 0x00a3 }
                long r13 = java.lang.Long.parseLong(r0)     // Catch:{ NumberFormatException -> 0x007f, IOException | XmlPullParserException -> 0x00a3 }
                java.lang.String r0 = "passed-health-check"
                java.lang.String r0 = r1.getAttributeValue(r5, r0)     // Catch:{ NumberFormatException -> 0x007f, IOException | XmlPullParserException -> 0x00a3 }
                boolean r15 = java.lang.Boolean.parseBoolean(r0)     // Catch:{ NumberFormatException -> 0x007f, IOException | XmlPullParserException -> 0x00a3 }
                boolean r0 = android.text.TextUtils.isEmpty(r10)     // Catch:{ NumberFormatException -> 0x007f, IOException | XmlPullParserException -> 0x00a3 }
                if (r0 != 0) goto L_0x007e
                com.android.server.PackageWatchdog$MonitoredPackage r0 = new com.android.server.PackageWatchdog$MonitoredPackage     // Catch:{ NumberFormatException -> 0x007f, IOException | XmlPullParserException -> 0x00a3 }
                java.util.Objects.requireNonNull(r17)     // Catch:{ NumberFormatException -> 0x007f, IOException | XmlPullParserException -> 0x00a3 }
                r8 = r0
                r9 = r17
                r8.<init>(r10, r11, r13, r15)     // Catch:{ NumberFormatException -> 0x007f, IOException | XmlPullParserException -> 0x00a3 }
                r6.add(r0)     // Catch:{ NumberFormatException -> 0x007f, IOException | XmlPullParserException -> 0x00a3 }
            L_0x007e:
                goto L_0x0033
            L_0x007f:
                r0 = move-exception
                java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ IOException | XmlPullParserException -> 0x00a3 }
                r8.<init>()     // Catch:{ IOException | XmlPullParserException -> 0x00a3 }
                java.lang.String r9 = "Skipping package for observer "
                r8.append(r9)     // Catch:{ IOException | XmlPullParserException -> 0x00a3 }
                r8.append(r2)     // Catch:{ IOException | XmlPullParserException -> 0x00a3 }
                java.lang.String r8 = r8.toString()     // Catch:{ IOException | XmlPullParserException -> 0x00a3 }
                android.util.Slog.wtf(r4, r8, r0)     // Catch:{ IOException | XmlPullParserException -> 0x00a3 }
                goto L_0x0033
            L_0x0095:
                boolean r0 = r6.isEmpty()
                if (r0 == 0) goto L_0x009d
                return r5
            L_0x009d:
                com.android.server.PackageWatchdog$ObserverInternal r0 = new com.android.server.PackageWatchdog$ObserverInternal
                r0.<init>(r2, r6)
                return r0
            L_0x00a3:
                r0 = move-exception
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r8 = "Unable to read observer "
                r3.append(r8)
                r3.append(r2)
                java.lang.String r3 = r3.toString()
                android.util.Slog.wtf(r4, r3, r0)
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.PackageWatchdog.ObserverInternal.read(org.xmlpull.v1.XmlPullParser, com.android.server.PackageWatchdog):com.android.server.PackageWatchdog$ObserverInternal");
        }
    }

    class MonitoredPackage {
        public static final int STATE_ACTIVE = 0;
        public static final int STATE_FAILED = 3;
        public static final int STATE_INACTIVE = 1;
        public static final int STATE_PASSED = 2;
        @GuardedBy({"mLock"})
        private long mDurationMs;
        @GuardedBy({"mLock"})
        private int mFailures;
        @GuardedBy({"mLock"})
        private boolean mHasPassedHealthCheck;
        @GuardedBy({"mLock"})
        private long mHealthCheckDurationMs;
        private int mHealthCheckState;
        /* access modifiers changed from: private */
        public final String mName;
        @GuardedBy({"mLock"})
        private long mUptimeStartMs;

        MonitoredPackage(PackageWatchdog this$02, String name, long durationMs, boolean hasPassedHealthCheck) {
            this(name, durationMs, JobStatus.NO_LATEST_RUNTIME, hasPassedHealthCheck);
        }

        MonitoredPackage(String name, long durationMs, long healthCheckDurationMs, boolean hasPassedHealthCheck) {
            this.mHealthCheckState = 1;
            this.mHealthCheckDurationMs = JobStatus.NO_LATEST_RUNTIME;
            this.mName = name;
            this.mDurationMs = durationMs;
            this.mHealthCheckDurationMs = healthCheckDurationMs;
            this.mHasPassedHealthCheck = hasPassedHealthCheck;
            updateHealthCheckStateLocked();
        }

        @GuardedBy({"mLock"})
        public void writeLocked(XmlSerializer out) throws IOException {
            out.startTag((String) null, "package");
            out.attribute((String) null, "name", this.mName);
            out.attribute((String) null, PackageWatchdog.ATTR_DURATION, String.valueOf(this.mDurationMs));
            out.attribute((String) null, PackageWatchdog.ATTR_EXPLICIT_HEALTH_CHECK_DURATION, String.valueOf(this.mHealthCheckDurationMs));
            out.attribute((String) null, PackageWatchdog.ATTR_PASSED_HEALTH_CHECK, String.valueOf(this.mHasPassedHealthCheck));
            out.endTag((String) null, "package");
        }

        @GuardedBy({"mLock"})
        public boolean onFailureLocked() {
            long now = SystemClock.uptimeMillis();
            boolean z = true;
            if (now - this.mUptimeStartMs > ((long) PackageWatchdog.this.mTriggerFailureDurationMs)) {
                this.mFailures = 1;
                this.mUptimeStartMs = now;
            } else {
                this.mFailures++;
            }
            if (this.mFailures < PackageWatchdog.this.mTriggerFailureCount) {
                z = false;
            }
            boolean failed = z;
            if (failed) {
                this.mFailures = 0;
            }
            return failed;
        }

        @GuardedBy({"mLock"})
        public int setHealthCheckActiveLocked(long initialHealthCheckDurationMs) {
            if (initialHealthCheckDurationMs <= 0) {
                Slog.wtf(PackageWatchdog.TAG, "Cannot set non-positive health check duration " + initialHealthCheckDurationMs + "ms for package " + this.mName + ". Using total duration " + this.mDurationMs + "ms instead");
                initialHealthCheckDurationMs = this.mDurationMs;
            }
            if (this.mHealthCheckState == 1) {
                this.mHealthCheckDurationMs = initialHealthCheckDurationMs;
            }
            return updateHealthCheckStateLocked();
        }

        @GuardedBy({"mLock"})
        public int handleElapsedTimeLocked(long elapsedMs) {
            if (elapsedMs <= 0) {
                Slog.w(PackageWatchdog.TAG, "Cannot handle non-positive elapsed time for package " + this.mName);
                return this.mHealthCheckState;
            }
            this.mDurationMs -= elapsedMs;
            if (this.mHealthCheckState == 0) {
                this.mHealthCheckDurationMs -= elapsedMs;
            }
            return updateHealthCheckStateLocked();
        }

        @GuardedBy({"mLock"})
        public int tryPassHealthCheckLocked() {
            if (this.mHealthCheckState != 3) {
                this.mHasPassedHealthCheck = true;
            }
            return updateHealthCheckStateLocked();
        }

        /* access modifiers changed from: private */
        public String getName() {
            return this.mName;
        }

        @GuardedBy({"mLock"})
        public int getHealthCheckStateLocked() {
            return this.mHealthCheckState;
        }

        @GuardedBy({"mLock"})
        public long getShortestScheduleDurationMsLocked() {
            return Math.min(toPositive(this.mDurationMs), isPendingHealthChecksLocked() ? toPositive(this.mHealthCheckDurationMs) : JobStatus.NO_LATEST_RUNTIME);
        }

        @GuardedBy({"mLock"})
        public boolean isExpiredLocked() {
            return this.mDurationMs <= 0;
        }

        @GuardedBy({"mLock"})
        public boolean isPendingHealthChecksLocked() {
            int i = this.mHealthCheckState;
            return i == 0 || i == 1;
        }

        @GuardedBy({"mLock"})
        private int updateHealthCheckStateLocked() {
            int oldState = this.mHealthCheckState;
            if (this.mHasPassedHealthCheck) {
                this.mHealthCheckState = 2;
            } else {
                long j = this.mHealthCheckDurationMs;
                if (j <= 0 || this.mDurationMs <= 0) {
                    this.mHealthCheckState = 3;
                } else if (j == JobStatus.NO_LATEST_RUNTIME) {
                    this.mHealthCheckState = 1;
                } else {
                    this.mHealthCheckState = 0;
                }
            }
            Slog.i(PackageWatchdog.TAG, "Updated health check state for package " + this.mName + ": " + toString(oldState) + " -> " + toString(this.mHealthCheckState));
            return this.mHealthCheckState;
        }

        private String toString(int state) {
            if (state == 0) {
                return "ACTIVE";
            }
            if (state == 1) {
                return "INACTIVE";
            }
            if (state == 2) {
                return "PASSED";
            }
            if (state != 3) {
                return "UNKNOWN";
            }
            return "FAILED";
        }

        private long toPositive(long value) {
            return value > 0 ? value : JobStatus.NO_LATEST_RUNTIME;
        }
    }
}
