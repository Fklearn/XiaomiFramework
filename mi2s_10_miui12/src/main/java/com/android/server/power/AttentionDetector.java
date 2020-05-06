package com.android.server.power;

import android.app.ActivityManager;
import android.app.SynchronousUserSwitchObserver;
import android.attention.AttentionManagerInternal;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Slog;
import android.util.StatsLog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.LocalServices;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class AttentionDetector {
    private static final boolean DEBUG = false;
    private static final String TAG = "AttentionDetector";
    @VisibleForTesting
    protected AttentionManagerInternal mAttentionManager;
    @VisibleForTesting
    AttentionCallbackInternalImpl mCallback;
    private AtomicLong mConsecutiveTimeoutExtendedCount = new AtomicLong(0);
    @VisibleForTesting
    protected ContentResolver mContentResolver;
    /* access modifiers changed from: private */
    public Context mContext;
    private boolean mIsSettingEnabled;
    private long mLastActedOnNextScreenDimming;
    private long mLastUserActivityTime;
    /* access modifiers changed from: private */
    public final Object mLock;
    private long mMaxAttentionApiTimeoutMillis;
    @VisibleForTesting
    protected long mMaximumExtensionMillis;
    /* access modifiers changed from: private */
    public final Runnable mOnUserAttention;
    @VisibleForTesting
    protected PackageManager mPackageManager;
    @VisibleForTesting
    protected int mRequestId;
    /* access modifiers changed from: private */
    public final AtomicBoolean mRequested;
    /* access modifiers changed from: private */
    public int mWakefulness;

    public AttentionDetector(Runnable onUserAttention, Object lock) {
        this.mOnUserAttention = onUserAttention;
        this.mLock = lock;
        this.mRequested = new AtomicBoolean(false);
        this.mRequestId = 0;
        this.mWakefulness = 1;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateEnabledFromSettings(Context context) {
        boolean z = false;
        if (Settings.System.getIntForUser(context.getContentResolver(), "adaptive_sleep", 0, -2) == 1) {
            z = true;
        }
        this.mIsSettingEnabled = z;
    }

    public void systemReady(final Context context) {
        this.mContext = context;
        updateEnabledFromSettings(context);
        this.mPackageManager = context.getPackageManager();
        this.mContentResolver = context.getContentResolver();
        this.mAttentionManager = (AttentionManagerInternal) LocalServices.getService(AttentionManagerInternal.class);
        this.mMaximumExtensionMillis = (long) context.getResources().getInteger(17694735);
        this.mMaxAttentionApiTimeoutMillis = (long) context.getResources().getInteger(17694734);
        try {
            ActivityManager.getService().registerUserSwitchObserver(new UserSwitchObserver(), TAG);
        } catch (RemoteException e) {
        }
        context.getContentResolver().registerContentObserver(Settings.System.getUriFor("adaptive_sleep"), false, new ContentObserver(new Handler()) {
            public void onChange(boolean selfChange) {
                AttentionDetector.this.updateEnabledFromSettings(context);
            }
        }, -1);
    }

    public long updateUserActivity(long nextScreenDimming) {
        if (nextScreenDimming == this.mLastActedOnNextScreenDimming || !this.mIsSettingEnabled || !isAttentionServiceSupported()) {
            return nextScreenDimming;
        }
        if (!serviceHasSufficientPermissions()) {
            Settings.System.putInt(this.mContentResolver, "adaptive_sleep", 0);
            return nextScreenDimming;
        }
        long now = SystemClock.uptimeMillis();
        long whenToCheck = nextScreenDimming - getAttentionTimeout();
        long whenToStopExtending = this.mLastUserActivityTime + this.mMaximumExtensionMillis;
        if (now < whenToCheck) {
            return whenToCheck;
        }
        if (whenToStopExtending < whenToCheck) {
            return nextScreenDimming;
        }
        if (this.mRequested.get()) {
            return whenToCheck;
        }
        this.mRequested.set(true);
        this.mRequestId++;
        this.mLastActedOnNextScreenDimming = nextScreenDimming;
        this.mCallback = new AttentionCallbackInternalImpl(this.mRequestId);
        Slog.v(TAG, "Checking user attention, ID: " + this.mRequestId);
        if (!this.mAttentionManager.checkAttention(getAttentionTimeout(), this.mCallback)) {
            this.mRequested.set(false);
        }
        return whenToCheck;
    }

    public int onUserActivity(long eventTime, int event) {
        if (event == 0 || event == 1 || event == 2 || event == 3) {
            cancelCurrentRequestIfAny();
            this.mLastUserActivityTime = eventTime;
            resetConsecutiveExtensionCount();
            return 1;
        } else if (event != 4) {
            return -1;
        } else {
            this.mConsecutiveTimeoutExtendedCount.incrementAndGet();
            return 0;
        }
    }

    public void onWakefulnessChangeStarted(int wakefulness) {
        this.mWakefulness = wakefulness;
        if (wakefulness != 1) {
            cancelCurrentRequestIfAny();
            resetConsecutiveExtensionCount();
        }
    }

    private void cancelCurrentRequestIfAny() {
        if (this.mRequested.get()) {
            this.mAttentionManager.cancelAttentionCheck(this.mCallback);
            this.mRequested.set(false);
        }
    }

    /* access modifiers changed from: private */
    public void resetConsecutiveExtensionCount() {
        long previousCount = this.mConsecutiveTimeoutExtendedCount.getAndSet(0);
        if (previousCount > 0) {
            StatsLog.write(168, previousCount);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public long getAttentionTimeout() {
        return this.mMaxAttentionApiTimeoutMillis;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isAttentionServiceSupported() {
        AttentionManagerInternal attentionManagerInternal = this.mAttentionManager;
        return attentionManagerInternal != null && attentionManagerInternal.isAttentionServiceSupported();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean serviceHasSufficientPermissions() {
        String attentionPackage = this.mPackageManager.getAttentionServicePackageName();
        return attentionPackage != null && this.mPackageManager.checkPermission("android.permission.CAMERA", attentionPackage) == 0;
    }

    public void dump(PrintWriter pw) {
        pw.println("AttentionDetector:");
        pw.println(" mMaximumExtensionMillis=" + this.mMaximumExtensionMillis);
        pw.println(" mMaxAttentionApiTimeoutMillis=" + this.mMaxAttentionApiTimeoutMillis);
        pw.println(" mLastUserActivityTime(excludingAttention)=" + this.mLastUserActivityTime);
        pw.println(" mAttentionServiceSupported=" + isAttentionServiceSupported());
        pw.println(" mRequested=" + this.mRequested);
    }

    @VisibleForTesting
    final class AttentionCallbackInternalImpl extends AttentionManagerInternal.AttentionCallbackInternal {
        private final int mId;

        AttentionCallbackInternalImpl(int id) {
            this.mId = id;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onSuccess(int r4, long r5) {
            /*
                r3 = this;
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r1 = "onSuccess: "
                r0.append(r1)
                r0.append(r4)
                java.lang.String r1 = ", ID: "
                r0.append(r1)
                int r1 = r3.mId
                r0.append(r1)
                java.lang.String r0 = r0.toString()
                java.lang.String r1 = "AttentionDetector"
                android.util.Slog.v(r1, r0)
                int r0 = r3.mId
                com.android.server.power.AttentionDetector r1 = com.android.server.power.AttentionDetector.this
                int r1 = r1.mRequestId
                if (r0 != r1) goto L_0x005e
                com.android.server.power.AttentionDetector r0 = com.android.server.power.AttentionDetector.this
                java.util.concurrent.atomic.AtomicBoolean r0 = r0.mRequested
                r1 = 0
                boolean r0 = r0.getAndSet(r1)
                if (r0 == 0) goto L_0x005e
                com.android.server.power.AttentionDetector r0 = com.android.server.power.AttentionDetector.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.power.AttentionDetector r1 = com.android.server.power.AttentionDetector.this     // Catch:{ all -> 0x005b }
                int r1 = r1.mWakefulness     // Catch:{ all -> 0x005b }
                r2 = 1
                if (r1 == r2) goto L_0x0048
                monitor-exit(r0)     // Catch:{ all -> 0x005b }
                return
            L_0x0048:
                if (r4 != r2) goto L_0x0054
                com.android.server.power.AttentionDetector r1 = com.android.server.power.AttentionDetector.this     // Catch:{ all -> 0x005b }
                java.lang.Runnable r1 = r1.mOnUserAttention     // Catch:{ all -> 0x005b }
                r1.run()     // Catch:{ all -> 0x005b }
                goto L_0x0059
            L_0x0054:
                com.android.server.power.AttentionDetector r1 = com.android.server.power.AttentionDetector.this     // Catch:{ all -> 0x005b }
                r1.resetConsecutiveExtensionCount()     // Catch:{ all -> 0x005b }
            L_0x0059:
                monitor-exit(r0)     // Catch:{ all -> 0x005b }
                goto L_0x005e
            L_0x005b:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x005b }
                throw r1
            L_0x005e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.AttentionDetector.AttentionCallbackInternalImpl.onSuccess(int, long):void");
        }

        public void onFailure(int error) {
            Slog.i(AttentionDetector.TAG, "Failed to check attention: " + error + ", ID: " + this.mId);
            AttentionDetector.this.mRequested.set(false);
        }
    }

    private final class UserSwitchObserver extends SynchronousUserSwitchObserver {
        private UserSwitchObserver() {
        }

        public void onUserSwitching(int newUserId) throws RemoteException {
            AttentionDetector attentionDetector = AttentionDetector.this;
            attentionDetector.updateEnabledFromSettings(attentionDetector.mContext);
        }
    }
}
