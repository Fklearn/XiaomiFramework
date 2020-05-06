package com.android.server.timezone;

import android.app.timezone.RulesUpdaterContract;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.FileUtils;
import android.os.SystemClock;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Clock;

@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
public class PackageTracker {
    private static final String TAG = "timezone.PackageTracker";
    private int mCheckFailureCount;
    private int mCheckTimeAllowedMillis;
    private boolean mCheckTriggered;
    private final ConfigHelper mConfigHelper;
    private String mDataAppPackageName;
    private int mDelayBeforeReliabilityCheckMillis;
    private final Clock mElapsedRealtimeClock;
    private long mFailedCheckRetryCount;
    private final PackageTrackerIntentHelper mIntentHelper;
    private Long mLastTriggerTimestamp = null;
    private final PackageManagerHelper mPackageManagerHelper;
    private final PackageStatusStorage mPackageStatusStorage;
    private boolean mTrackingEnabled;
    private String mUpdateAppPackageName;

    static PackageTracker create(Context context) {
        Clock elapsedRealtimeClock = SystemClock.elapsedRealtimeClock();
        PackageTrackerHelperImpl helperImpl = new PackageTrackerHelperImpl(context);
        return new PackageTracker(elapsedRealtimeClock, helperImpl, helperImpl, new PackageStatusStorage(FileUtils.createDir(Environment.getDataSystemDirectory(), "timezone")), new PackageTrackerIntentHelperImpl(context));
    }

    PackageTracker(Clock elapsedRealtimeClock, ConfigHelper configHelper, PackageManagerHelper packageManagerHelper, PackageStatusStorage packageStatusStorage, PackageTrackerIntentHelper intentHelper) {
        this.mElapsedRealtimeClock = elapsedRealtimeClock;
        this.mConfigHelper = configHelper;
        this.mPackageManagerHelper = packageManagerHelper;
        this.mPackageStatusStorage = packageStatusStorage;
        this.mIntentHelper = intentHelper;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public synchronized boolean start() {
        this.mTrackingEnabled = this.mConfigHelper.isTrackingEnabled();
        if (!this.mTrackingEnabled) {
            Slog.i(TAG, "Time zone updater / data package tracking explicitly disabled.");
            return false;
        }
        this.mUpdateAppPackageName = this.mConfigHelper.getUpdateAppPackageName();
        this.mDataAppPackageName = this.mConfigHelper.getDataAppPackageName();
        this.mCheckTimeAllowedMillis = this.mConfigHelper.getCheckTimeAllowedMillis();
        this.mFailedCheckRetryCount = (long) this.mConfigHelper.getFailedCheckRetryCount();
        this.mDelayBeforeReliabilityCheckMillis = this.mCheckTimeAllowedMillis + 60000;
        throwIfDeviceSettingsOrAppsAreBad();
        this.mCheckTriggered = false;
        this.mCheckFailureCount = 0;
        try {
            this.mPackageStatusStorage.initialize();
            this.mIntentHelper.initialize(this.mUpdateAppPackageName, this.mDataAppPackageName, this);
            this.mIntentHelper.scheduleReliabilityTrigger((long) this.mDelayBeforeReliabilityCheckMillis);
            Slog.i(TAG, "Time zone updater / data package tracking enabled");
            return true;
        } catch (IOException e) {
            Slog.w(TAG, "PackageTracker storage could not be initialized.", e);
            return false;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    private void throwIfDeviceSettingsOrAppsAreBad() {
        throwRuntimeExceptionIfNullOrEmpty(this.mUpdateAppPackageName, "Update app package name missing.");
        throwRuntimeExceptionIfNullOrEmpty(this.mDataAppPackageName, "Data app package name missing.");
        if (this.mFailedCheckRetryCount < 1) {
            throw logAndThrowRuntimeException("mFailedRetryCount=" + this.mFailedCheckRetryCount, (Throwable) null);
        } else if (this.mCheckTimeAllowedMillis >= 1000) {
            try {
                if (this.mPackageManagerHelper.isPrivilegedApp(this.mUpdateAppPackageName)) {
                    Slog.d(TAG, "Update app " + this.mUpdateAppPackageName + " is valid.");
                    try {
                        if (this.mPackageManagerHelper.isPrivilegedApp(this.mDataAppPackageName)) {
                            Slog.d(TAG, "Data app " + this.mDataAppPackageName + " is valid.");
                            return;
                        }
                        throw logAndThrowRuntimeException("Data app " + this.mDataAppPackageName + " must be a priv-app.", (Throwable) null);
                    } catch (PackageManager.NameNotFoundException e) {
                        throw logAndThrowRuntimeException("Could not determine data app package details for " + this.mDataAppPackageName, e);
                    }
                } else {
                    throw logAndThrowRuntimeException("Update app " + this.mUpdateAppPackageName + " must be a priv-app.", (Throwable) null);
                }
            } catch (PackageManager.NameNotFoundException e2) {
                throw logAndThrowRuntimeException("Could not determine update app package details for " + this.mUpdateAppPackageName, e2);
            }
        } else {
            throw logAndThrowRuntimeException("mCheckTimeAllowedMillis=" + this.mCheckTimeAllowedMillis, (Throwable) null);
        }
    }

    public synchronized void triggerUpdateIfNeeded(boolean packageChanged) {
        if (this.mTrackingEnabled) {
            boolean updaterAppManifestValid = validateUpdaterAppManifest();
            boolean dataAppManifestValid = validateDataAppManifest();
            if (updaterAppManifestValid) {
                if (dataAppManifestValid) {
                    if (!packageChanged) {
                        if (!this.mCheckTriggered) {
                            Slog.d(TAG, "triggerUpdateIfNeeded: First reliability trigger.");
                        } else if (isCheckInProgress()) {
                            if (!isCheckResponseOverdue()) {
                                Slog.d(TAG, "triggerUpdateIfNeeded: checkComplete call is not yet overdue. Not triggering.");
                                this.mIntentHelper.scheduleReliabilityTrigger((long) this.mDelayBeforeReliabilityCheckMillis);
                                return;
                            }
                        } else if (((long) this.mCheckFailureCount) > this.mFailedCheckRetryCount) {
                            Slog.i(TAG, "triggerUpdateIfNeeded: number of allowed consecutive check failures exceeded. Stopping reliability triggers until next reboot or package update.");
                            this.mIntentHelper.unscheduleReliabilityTrigger();
                            return;
                        } else if (this.mCheckFailureCount == 0) {
                            Slog.i(TAG, "triggerUpdateIfNeeded: No reliability check required. Last check was successful.");
                            this.mIntentHelper.unscheduleReliabilityTrigger();
                            return;
                        }
                    }
                    PackageVersions currentInstalledVersions = lookupInstalledPackageVersions();
                    if (currentInstalledVersions == null) {
                        Slog.e(TAG, "triggerUpdateIfNeeded: currentInstalledVersions was null");
                        this.mIntentHelper.unscheduleReliabilityTrigger();
                        return;
                    }
                    PackageStatus packageStatus = this.mPackageStatusStorage.getPackageStatus();
                    if (packageStatus == null) {
                        Slog.i(TAG, "triggerUpdateIfNeeded: No package status data found. Data check needed.");
                    } else if (!packageStatus.mVersions.equals(currentInstalledVersions)) {
                        Slog.i(TAG, "triggerUpdateIfNeeded: Stored package versions=" + packageStatus.mVersions + ", do not match current package versions=" + currentInstalledVersions + ". Triggering check.");
                    } else {
                        Slog.i(TAG, "triggerUpdateIfNeeded: Stored package versions match currently installed versions, currentInstalledVersions=" + currentInstalledVersions + ", packageStatus.mCheckStatus=" + packageStatus.mCheckStatus);
                        if (packageStatus.mCheckStatus == 2) {
                            Slog.i(TAG, "triggerUpdateIfNeeded: Prior check succeeded. No need to trigger.");
                            this.mIntentHelper.unscheduleReliabilityTrigger();
                            return;
                        }
                    }
                    CheckToken checkToken = this.mPackageStatusStorage.generateCheckToken(currentInstalledVersions);
                    if (checkToken == null) {
                        Slog.w(TAG, "triggerUpdateIfNeeded: Unable to generate check token. Not sending check request.");
                        this.mIntentHelper.scheduleReliabilityTrigger((long) this.mDelayBeforeReliabilityCheckMillis);
                        return;
                    }
                    this.mIntentHelper.sendTriggerUpdateCheck(checkToken);
                    this.mCheckTriggered = true;
                    setCheckInProgress();
                    this.mIntentHelper.scheduleReliabilityTrigger((long) this.mDelayBeforeReliabilityCheckMillis);
                    return;
                }
            }
            Slog.e(TAG, "No update triggered due to invalid application manifest entries. updaterApp=" + updaterAppManifestValid + ", dataApp=" + dataAppManifestValid);
            this.mIntentHelper.unscheduleReliabilityTrigger();
            return;
        }
        throw new IllegalStateException("Unexpected call. Tracking is disabled.");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00c9, code lost:
        return;
     */
    @com.android.internal.annotations.VisibleForTesting(visibility = com.android.internal.annotations.VisibleForTesting.Visibility.PACKAGE)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void recordCheckResult(com.android.server.timezone.CheckToken r5, boolean r6) {
        /*
            r4 = this;
            monitor-enter(r4)
            java.lang.String r0 = "timezone.PackageTracker"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ca }
            r1.<init>()     // Catch:{ all -> 0x00ca }
            java.lang.String r2 = "recordOperationResult: checkToken="
            r1.append(r2)     // Catch:{ all -> 0x00ca }
            r1.append(r5)     // Catch:{ all -> 0x00ca }
            java.lang.String r2 = " success="
            r1.append(r2)     // Catch:{ all -> 0x00ca }
            r1.append(r6)     // Catch:{ all -> 0x00ca }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x00ca }
            android.util.Slog.i(r0, r1)     // Catch:{ all -> 0x00ca }
            boolean r0 = r4.mTrackingEnabled     // Catch:{ all -> 0x00ca }
            if (r0 != 0) goto L_0x0055
            if (r5 != 0) goto L_0x0031
            java.lang.String r0 = "timezone.PackageTracker"
            java.lang.String r1 = "recordCheckResult: Tracking is disabled and no token has been provided. Resetting tracking state."
            android.util.Slog.d(r0, r1)     // Catch:{ all -> 0x00ca }
            goto L_0x004e
        L_0x0031:
            java.lang.String r0 = "timezone.PackageTracker"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ca }
            r1.<init>()     // Catch:{ all -> 0x00ca }
            java.lang.String r2 = "recordCheckResult: Tracking is disabled and a token "
            r1.append(r2)     // Catch:{ all -> 0x00ca }
            r1.append(r5)     // Catch:{ all -> 0x00ca }
            java.lang.String r2 = " has been unexpectedly provided. Resetting tracking state."
            r1.append(r2)     // Catch:{ all -> 0x00ca }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x00ca }
            android.util.Slog.w(r0, r1)     // Catch:{ all -> 0x00ca }
        L_0x004e:
            com.android.server.timezone.PackageStatusStorage r0 = r4.mPackageStatusStorage     // Catch:{ all -> 0x00ca }
            r0.resetCheckState()     // Catch:{ all -> 0x00ca }
            monitor-exit(r4)
            return
        L_0x0055:
            r0 = 0
            if (r5 != 0) goto L_0x0071
            java.lang.String r1 = "timezone.PackageTracker"
            java.lang.String r2 = "recordCheckResult: Unexpectedly missing checkToken, resetting storage state."
            android.util.Slog.i(r1, r2)     // Catch:{ all -> 0x00ca }
            com.android.server.timezone.PackageStatusStorage r1 = r4.mPackageStatusStorage     // Catch:{ all -> 0x00ca }
            r1.resetCheckState()     // Catch:{ all -> 0x00ca }
            com.android.server.timezone.PackageTrackerIntentHelper r1 = r4.mIntentHelper     // Catch:{ all -> 0x00ca }
            int r2 = r4.mDelayBeforeReliabilityCheckMillis     // Catch:{ all -> 0x00ca }
            long r2 = (long) r2     // Catch:{ all -> 0x00ca }
            r1.scheduleReliabilityTrigger(r2)     // Catch:{ all -> 0x00ca }
            r4.mCheckFailureCount = r0     // Catch:{ all -> 0x00ca }
            goto L_0x00c8
        L_0x0071:
            com.android.server.timezone.PackageStatusStorage r1 = r4.mPackageStatusStorage     // Catch:{ all -> 0x00ca }
            boolean r1 = r1.markChecked(r5, r6)     // Catch:{ all -> 0x00ca }
            if (r1 == 0) goto L_0x0095
            r4.setCheckComplete()     // Catch:{ all -> 0x00ca }
            if (r6 == 0) goto L_0x0086
            com.android.server.timezone.PackageTrackerIntentHelper r2 = r4.mIntentHelper     // Catch:{ all -> 0x00ca }
            r2.unscheduleReliabilityTrigger()     // Catch:{ all -> 0x00ca }
            r4.mCheckFailureCount = r0     // Catch:{ all -> 0x00ca }
            goto L_0x00c8
        L_0x0086:
            com.android.server.timezone.PackageTrackerIntentHelper r0 = r4.mIntentHelper     // Catch:{ all -> 0x00ca }
            int r2 = r4.mDelayBeforeReliabilityCheckMillis     // Catch:{ all -> 0x00ca }
            long r2 = (long) r2     // Catch:{ all -> 0x00ca }
            r0.scheduleReliabilityTrigger(r2)     // Catch:{ all -> 0x00ca }
            int r0 = r4.mCheckFailureCount     // Catch:{ all -> 0x00ca }
            int r0 = r0 + 1
            r4.mCheckFailureCount = r0     // Catch:{ all -> 0x00ca }
            goto L_0x00c8
        L_0x0095:
            java.lang.String r0 = "timezone.PackageTracker"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ca }
            r2.<init>()     // Catch:{ all -> 0x00ca }
            java.lang.String r3 = "recordCheckResult: could not update token="
            r2.append(r3)     // Catch:{ all -> 0x00ca }
            r2.append(r5)     // Catch:{ all -> 0x00ca }
            java.lang.String r3 = " with success="
            r2.append(r3)     // Catch:{ all -> 0x00ca }
            r2.append(r6)     // Catch:{ all -> 0x00ca }
            java.lang.String r3 = ". Optimistic lock failure"
            r2.append(r3)     // Catch:{ all -> 0x00ca }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00ca }
            android.util.Slog.i(r0, r2)     // Catch:{ all -> 0x00ca }
            com.android.server.timezone.PackageTrackerIntentHelper r0 = r4.mIntentHelper     // Catch:{ all -> 0x00ca }
            int r2 = r4.mDelayBeforeReliabilityCheckMillis     // Catch:{ all -> 0x00ca }
            long r2 = (long) r2     // Catch:{ all -> 0x00ca }
            r0.scheduleReliabilityTrigger(r2)     // Catch:{ all -> 0x00ca }
            int r0 = r4.mCheckFailureCount     // Catch:{ all -> 0x00ca }
            int r0 = r0 + 1
            r4.mCheckFailureCount = r0     // Catch:{ all -> 0x00ca }
        L_0x00c8:
            monitor-exit(r4)
            return
        L_0x00ca:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.timezone.PackageTracker.recordCheckResult(com.android.server.timezone.CheckToken, boolean):void");
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public int getCheckFailureCountForTests() {
        return this.mCheckFailureCount;
    }

    private void setCheckInProgress() {
        this.mLastTriggerTimestamp = Long.valueOf(this.mElapsedRealtimeClock.millis());
    }

    private void setCheckComplete() {
        this.mLastTriggerTimestamp = null;
    }

    private boolean isCheckInProgress() {
        return this.mLastTriggerTimestamp != null;
    }

    private boolean isCheckResponseOverdue() {
        if (this.mLastTriggerTimestamp != null && this.mElapsedRealtimeClock.millis() > this.mLastTriggerTimestamp.longValue() + ((long) this.mCheckTimeAllowedMillis)) {
            return true;
        }
        return false;
    }

    private PackageVersions lookupInstalledPackageVersions() {
        try {
            return new PackageVersions(this.mPackageManagerHelper.getInstalledPackageVersion(this.mUpdateAppPackageName), this.mPackageManagerHelper.getInstalledPackageVersion(this.mDataAppPackageName));
        } catch (PackageManager.NameNotFoundException e) {
            Slog.w(TAG, "lookupInstalledPackageVersions: Unable to resolve installed package versions", e);
            return null;
        }
    }

    private boolean validateDataAppManifest() {
        if (this.mPackageManagerHelper.contentProviderRegistered("com.android.timezone", this.mDataAppPackageName)) {
            return true;
        }
        Slog.w(TAG, "validateDataAppManifest: Data app " + this.mDataAppPackageName + " does not expose the required provider with authority=" + "com.android.timezone");
        return false;
    }

    private boolean validateUpdaterAppManifest() {
        try {
            if (!this.mPackageManagerHelper.usesPermission(this.mUpdateAppPackageName, "android.permission.UPDATE_TIME_ZONE_RULES")) {
                Slog.w(TAG, "validateUpdaterAppManifest: Updater app " + this.mDataAppPackageName + " does not use permission=" + "android.permission.UPDATE_TIME_ZONE_RULES");
                return false;
            } else if (!this.mPackageManagerHelper.receiverRegistered(RulesUpdaterContract.createUpdaterIntent(this.mUpdateAppPackageName), "android.permission.TRIGGER_TIME_ZONE_RULES_CHECK")) {
                return false;
            } else {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Slog.w(TAG, "validateUpdaterAppManifest: Updater app " + this.mDataAppPackageName + " does not expose the required broadcast receiver.", e);
            return false;
        }
    }

    private static void throwRuntimeExceptionIfNullOrEmpty(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw logAndThrowRuntimeException(message, (Throwable) null);
        }
    }

    private static RuntimeException logAndThrowRuntimeException(String message, Throwable cause) {
        Slog.wtf(TAG, message, cause);
        throw new RuntimeException(message, cause);
    }

    public void dump(PrintWriter fout) {
        fout.println("PackageTrackerState: " + toString());
        this.mPackageStatusStorage.dump(fout);
    }

    public String toString() {
        return "PackageTracker{mTrackingEnabled=" + this.mTrackingEnabled + ", mUpdateAppPackageName='" + this.mUpdateAppPackageName + '\'' + ", mDataAppPackageName='" + this.mDataAppPackageName + '\'' + ", mCheckTimeAllowedMillis=" + this.mCheckTimeAllowedMillis + ", mDelayBeforeReliabilityCheckMillis=" + this.mDelayBeforeReliabilityCheckMillis + ", mFailedCheckRetryCount=" + this.mFailedCheckRetryCount + ", mLastTriggerTimestamp=" + this.mLastTriggerTimestamp + ", mCheckTriggered=" + this.mCheckTriggered + ", mCheckFailureCount=" + this.mCheckFailureCount + '}';
    }
}
