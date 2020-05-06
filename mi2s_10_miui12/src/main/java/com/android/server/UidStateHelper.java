package com.android.server;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.IProcessObserver;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.app.IUidStateChangeCallback;
import com.android.internal.os.BackgroundThread;

public class UidStateHelper {
    /* access modifiers changed from: private */
    public static boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final int MSG_DISPATCH_UID_STATE_CHANGE = 1;
    /* access modifiers changed from: private */
    public static String TAG = "UidProcStateHelper";
    private static UidStateHelper sInstance;
    private final IActivityManager mActivityManager = ActivityManagerNative.getDefault();
    private final Handler mHandler = new Handler(BackgroundThread.get().getLooper(), this.mHandlerCallback);
    private Handler.Callback mHandlerCallback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            if (msg.what != 1) {
                return false;
            }
            UidStateHelper.this.dispatchUidStateChange(msg.arg1, msg.arg2);
            return true;
        }
    };
    private boolean mObserverInstalled = false;
    private IProcessObserver mProcessObserver = new IProcessObserver.Stub() {
        public void onForegroundActivitiesChanged(int pid, int uid, boolean foregroundActivities) {
            if (UidStateHelper.DEBUG) {
                String access$100 = UidStateHelper.TAG;
                Slog.v(access$100, "foreground changed:[" + pid + "," + uid + "," + foregroundActivities + "]");
            }
            synchronized (UidStateHelper.this.mStateLock) {
                SparseBooleanArray pidForeground = (SparseBooleanArray) UidStateHelper.this.mUidPidForeground.get(uid);
                if (pidForeground == null) {
                    pidForeground = new SparseBooleanArray(2);
                    UidStateHelper.this.mUidPidForeground.put(uid, pidForeground);
                }
                pidForeground.put(pid, foregroundActivities);
                UidStateHelper.this.computeUidForegroundLocked(uid);
            }
        }

        public void onForegroundServicesChanged(int pid, int uid, int serviceTypes) {
            if (UidStateHelper.DEBUG) {
                String access$100 = UidStateHelper.TAG;
                Slog.v(access$100, "foreground changed:[" + pid + "," + uid + "," + serviceTypes + "]");
            }
        }

        public void onProcessDied(int pid, int uid) {
            if (UidStateHelper.DEBUG) {
                String access$100 = UidStateHelper.TAG;
                Slog.v(access$100, "process died:[" + pid + "," + uid + "]");
            }
            synchronized (UidStateHelper.this.mStateLock) {
                SparseBooleanArray pidForeground = (SparseBooleanArray) UidStateHelper.this.mUidPidForeground.get(uid);
                if (pidForeground != null) {
                    pidForeground.delete(pid);
                    UidStateHelper.this.computeUidForegroundLocked(uid);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public Object mStateLock = new Object();
    private final SparseBooleanArray mUidForeground = new SparseBooleanArray();
    /* access modifiers changed from: private */
    public final SparseArray<SparseBooleanArray> mUidPidForeground = new SparseArray<>();
    private RemoteCallbackList<IUidStateChangeCallback> mUidStateObervers = new RemoteCallbackList<>();

    public static UidStateHelper get() {
        if (sInstance == null) {
            sInstance = new UidStateHelper();
        }
        return sInstance;
    }

    private UidStateHelper() {
        try {
            this.mActivityManager.registerProcessObserver(this.mProcessObserver);
            this.mObserverInstalled = true;
        } catch (RemoteException e) {
        }
    }

    public void registerUidStateObserver(IUidStateChangeCallback callback) {
        if (this.mObserverInstalled) {
            synchronized (this) {
                this.mUidStateObervers.register(callback);
            }
            return;
        }
        throw new IllegalStateException("ProcessObserver not installed");
    }

    public void unregisterUidStateObserver(IUidStateChangeCallback callback) {
        if (this.mObserverInstalled) {
            synchronized (this) {
                this.mUidStateObervers.unregister(callback);
            }
            return;
        }
        throw new IllegalStateException("ProcessObserver not installed");
    }

    public boolean isUidForeground(int uid) {
        boolean z;
        if (!UserHandle.isApp(uid)) {
            return true;
        }
        synchronized (this.mStateLock) {
            z = this.mUidForeground.get(uid, false);
        }
        return z;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0059, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isUidForeground(int r10, boolean r11) {
        /*
            r9 = this;
            boolean r0 = android.os.UserHandle.isApp(r10)
            r1 = 1
            if (r0 != 0) goto L_0x0008
            return r1
        L_0x0008:
            java.lang.Object r0 = r9.mStateLock
            monitor-enter(r0)
            android.util.SparseBooleanArray r2 = r9.mUidForeground     // Catch:{ all -> 0x005c }
            r3 = 0
            boolean r2 = r2.get(r10, r3)     // Catch:{ all -> 0x005c }
            if (r11 == 0) goto L_0x005a
            r4 = 0
            android.util.SparseArray<android.util.SparseBooleanArray> r5 = r9.mUidPidForeground     // Catch:{ all -> 0x005c }
            java.lang.Object r5 = r5.get(r10)     // Catch:{ all -> 0x005c }
            android.util.SparseBooleanArray r5 = (android.util.SparseBooleanArray) r5     // Catch:{ all -> 0x005c }
            if (r5 == 0) goto L_0x0032
            r6 = r3
        L_0x0020:
            int r7 = r5.size()     // Catch:{ all -> 0x005c }
            if (r6 >= r7) goto L_0x0032
            int r7 = r5.keyAt(r6)     // Catch:{ all -> 0x005c }
            boolean r8 = com.android.server.am.ExtraActivityManagerService.hasForegroundActivities(r7)     // Catch:{ all -> 0x005c }
            r4 = r4 | r8
            int r6 = r6 + 1
            goto L_0x0020
        L_0x0032:
            if (r2 == r4) goto L_0x0052
            java.lang.String r6 = TAG     // Catch:{ all -> 0x005c }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x005c }
            r7.<init>()     // Catch:{ all -> 0x005c }
            java.lang.String r8 = "ProcessObserver may miss callback, isUidFg="
            r7.append(r8)     // Catch:{ all -> 0x005c }
            r7.append(r2)     // Catch:{ all -> 0x005c }
            java.lang.String r8 = " isFgByPids="
            r7.append(r8)     // Catch:{ all -> 0x005c }
            r7.append(r4)     // Catch:{ all -> 0x005c }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x005c }
            android.util.Slog.wtf(r6, r7)     // Catch:{ all -> 0x005c }
        L_0x0052:
            if (r2 != 0) goto L_0x0058
            if (r4 == 0) goto L_0x0057
            goto L_0x0058
        L_0x0057:
            r1 = r3
        L_0x0058:
            monitor-exit(r0)     // Catch:{ all -> 0x005c }
            return r1
        L_0x005a:
            monitor-exit(r0)     // Catch:{ all -> 0x005c }
            return r2
        L_0x005c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x005c }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.UidStateHelper.isUidForeground(int, boolean):boolean");
    }

    /* access modifiers changed from: private */
    public void computeUidForegroundLocked(int uid) {
        SparseBooleanArray pidForeground = this.mUidPidForeground.get(uid);
        boolean uidForeground = false;
        int size = pidForeground.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                break;
            } else if (pidForeground.valueAt(i)) {
                uidForeground = true;
                break;
            } else {
                i++;
            }
        }
        int i2 = 0;
        if (this.mUidForeground.get(uid, false) != uidForeground) {
            this.mUidForeground.put(uid, uidForeground);
            Handler handler = this.mHandler;
            if (uidForeground) {
                i2 = 1;
            }
            handler.obtainMessage(1, uid, i2).sendToTarget();
        }
    }

    /* access modifiers changed from: private */
    public void dispatchUidStateChange(int uid, int state) {
        int length = this.mUidStateObervers.beginBroadcast();
        for (int i = 0; i < length; i++) {
            IUidStateChangeCallback callback = this.mUidStateObervers.getBroadcastItem(i);
            if (callback != null) {
                try {
                    callback.onUidStateChange(uid, state);
                } catch (RemoteException e) {
                }
            }
        }
        this.mUidStateObervers.finishBroadcast();
    }
}
