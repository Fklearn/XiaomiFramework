package com.android.server.infra;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.server.infra.ServiceNameResolver;
import java.io.PrintWriter;

public final class FrameworkResourcesServiceNameResolver implements ServiceNameResolver {
    private static final int MSG_RESET_TEMPORARY_SERVICE = 0;
    /* access modifiers changed from: private */
    public static final String TAG = FrameworkResourcesServiceNameResolver.class.getSimpleName();
    private final Context mContext;
    @GuardedBy({"mLock"})
    private final SparseBooleanArray mDefaultServicesDisabled = new SparseBooleanArray();
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private ServiceNameResolver.NameResolverListener mOnSetCallback;
    private final int mResourceId;
    @GuardedBy({"mLock"})
    private Handler mTemporaryHandler;
    @GuardedBy({"mLock"})
    private long mTemporaryServiceExpiration;
    @GuardedBy({"mLock"})
    private final SparseArray<String> mTemporaryServiceNames = new SparseArray<>();

    public FrameworkResourcesServiceNameResolver(Context context, int resourceId) {
        this.mContext = context;
        this.mResourceId = resourceId;
    }

    public void setOnTemporaryServiceNameChangedCallback(ServiceNameResolver.NameResolverListener callback) {
        synchronized (this.mLock) {
            this.mOnSetCallback = callback;
        }
    }

    public String getDefaultServiceName(int userId) {
        String str;
        synchronized (this.mLock) {
            String name = this.mContext.getString(this.mResourceId);
            str = TextUtils.isEmpty(name) ? null : name;
        }
        return str;
    }

    public String getServiceName(int userId) {
        synchronized (this.mLock) {
            String temporaryName = this.mTemporaryServiceNames.get(userId);
            if (temporaryName != null) {
                String str = TAG;
                Slog.w(str, "getServiceName(): using temporary name " + temporaryName + " for user " + userId);
                return temporaryName;
            } else if (this.mDefaultServicesDisabled.get(userId)) {
                String str2 = TAG;
                Slog.w(str2, "getServiceName(): temporary name not set and default disabled for user " + userId);
                return null;
            } else {
                String defaultServiceName = getDefaultServiceName(userId);
                return defaultServiceName;
            }
        }
    }

    public boolean isTemporary(int userId) {
        boolean z;
        synchronized (this.mLock) {
            z = this.mTemporaryServiceNames.get(userId) != null;
        }
        return z;
    }

    public void setTemporaryService(int userId, String componentName, int durationMs) {
        synchronized (this.mLock) {
            this.mTemporaryServiceNames.put(userId, componentName);
            if (this.mTemporaryHandler == null) {
                final int i = userId;
                this.mTemporaryHandler = new Handler(Looper.getMainLooper(), (Handler.Callback) null, true) {
                    public void handleMessage(Message msg) {
                        if (msg.what == 0) {
                            synchronized (FrameworkResourcesServiceNameResolver.this.mLock) {
                                FrameworkResourcesServiceNameResolver.this.resetTemporaryService(i);
                            }
                            return;
                        }
                        String access$100 = FrameworkResourcesServiceNameResolver.TAG;
                        Slog.wtf(access$100, "invalid handler msg: " + msg);
                    }
                };
            } else {
                this.mTemporaryHandler.removeMessages(0);
            }
            this.mTemporaryServiceExpiration = SystemClock.elapsedRealtime() + ((long) durationMs);
            this.mTemporaryHandler.sendEmptyMessageDelayed(0, (long) durationMs);
            notifyTemporaryServiceNameChangedLocked(userId, componentName, true);
        }
    }

    public void resetTemporaryService(int userId) {
        synchronized (this.mLock) {
            String str = TAG;
            Slog.i(str, "resetting temporary service for user " + userId + " from " + this.mTemporaryServiceNames.get(userId));
            this.mTemporaryServiceNames.remove(userId);
            if (this.mTemporaryHandler != null) {
                this.mTemporaryHandler.removeMessages(0);
                this.mTemporaryHandler = null;
            }
            notifyTemporaryServiceNameChangedLocked(userId, (String) null, false);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0066, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean setDefaultServiceEnabled(int r7, boolean r8) {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            boolean r1 = r6.isDefaultServiceEnabledLocked(r7)     // Catch:{ all -> 0x0067 }
            if (r1 != r8) goto L_0x002b
            java.lang.String r2 = TAG     // Catch:{ all -> 0x0067 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0067 }
            r3.<init>()     // Catch:{ all -> 0x0067 }
            java.lang.String r4 = "setDefaultServiceEnabled("
            r3.append(r4)     // Catch:{ all -> 0x0067 }
            r3.append(r7)     // Catch:{ all -> 0x0067 }
            java.lang.String r4 = "): already "
            r3.append(r4)     // Catch:{ all -> 0x0067 }
            r3.append(r8)     // Catch:{ all -> 0x0067 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0067 }
            android.util.Slog.i(r2, r3)     // Catch:{ all -> 0x0067 }
            r2 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x0067 }
            return r2
        L_0x002b:
            r2 = 1
            if (r8 == 0) goto L_0x004a
            java.lang.String r3 = TAG     // Catch:{ all -> 0x0067 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0067 }
            r4.<init>()     // Catch:{ all -> 0x0067 }
            java.lang.String r5 = "disabling default service for user "
            r4.append(r5)     // Catch:{ all -> 0x0067 }
            r4.append(r7)     // Catch:{ all -> 0x0067 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0067 }
            android.util.Slog.i(r3, r4)     // Catch:{ all -> 0x0067 }
            android.util.SparseBooleanArray r3 = r6.mDefaultServicesDisabled     // Catch:{ all -> 0x0067 }
            r3.removeAt(r7)     // Catch:{ all -> 0x0067 }
            goto L_0x0065
        L_0x004a:
            java.lang.String r3 = TAG     // Catch:{ all -> 0x0067 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0067 }
            r4.<init>()     // Catch:{ all -> 0x0067 }
            java.lang.String r5 = "enabling default service for user "
            r4.append(r5)     // Catch:{ all -> 0x0067 }
            r4.append(r7)     // Catch:{ all -> 0x0067 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0067 }
            android.util.Slog.i(r3, r4)     // Catch:{ all -> 0x0067 }
            android.util.SparseBooleanArray r3 = r6.mDefaultServicesDisabled     // Catch:{ all -> 0x0067 }
            r3.put(r7, r2)     // Catch:{ all -> 0x0067 }
        L_0x0065:
            monitor-exit(r0)     // Catch:{ all -> 0x0067 }
            return r2
        L_0x0067:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0067 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.infra.FrameworkResourcesServiceNameResolver.setDefaultServiceEnabled(int, boolean):boolean");
    }

    public boolean isDefaultServiceEnabled(int userId) {
        boolean isDefaultServiceEnabledLocked;
        synchronized (this.mLock) {
            isDefaultServiceEnabledLocked = isDefaultServiceEnabledLocked(userId);
        }
        return isDefaultServiceEnabledLocked;
    }

    private boolean isDefaultServiceEnabledLocked(int userId) {
        return !this.mDefaultServicesDisabled.get(userId);
    }

    public String toString() {
        return "FrameworkResourcesServiceNamer[temps=" + this.mTemporaryServiceNames + "]";
    }

    public void dumpShort(PrintWriter pw) {
        synchronized (this.mLock) {
            pw.print("FrameworkResourcesServiceNamer: resId=");
            pw.print(this.mResourceId);
            pw.print(", numberTemps=");
            pw.print(this.mTemporaryServiceNames.size());
            pw.print(", enabledDefaults=");
            pw.print(this.mDefaultServicesDisabled.size());
        }
    }

    public void dumpShort(PrintWriter pw, int userId) {
        synchronized (this.mLock) {
            String temporaryName = this.mTemporaryServiceNames.get(userId);
            if (temporaryName != null) {
                pw.print("tmpName=");
                pw.print(temporaryName);
                pw.print(" (expires in ");
                TimeUtils.formatDuration(this.mTemporaryServiceExpiration - SystemClock.elapsedRealtime(), pw);
                pw.print("), ");
            }
            pw.print("defaultName=");
            pw.print(getDefaultServiceName(userId));
            pw.println(this.mDefaultServicesDisabled.get(userId) ? " (disabled)" : " (enabled)");
        }
    }

    private void notifyTemporaryServiceNameChangedLocked(int userId, String newTemporaryName, boolean isTemporary) {
        ServiceNameResolver.NameResolverListener nameResolverListener = this.mOnSetCallback;
        if (nameResolverListener != null) {
            nameResolverListener.onNameResolved(userId, newTemporaryName, isTemporary);
        }
    }
}
