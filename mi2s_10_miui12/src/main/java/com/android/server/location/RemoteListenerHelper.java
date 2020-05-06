package com.android.server.location;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.util.Preconditions;
import com.android.server.location.RemoteListenerHelper;
import java.util.HashMap;
import java.util.Map;

public abstract class RemoteListenerHelper<TListener extends IInterface> {
    protected static final int RESULT_GPS_LOCATION_DISABLED = 3;
    protected static final int RESULT_INTERNAL_ERROR = 4;
    protected static final int RESULT_NOT_ALLOWED = 6;
    protected static final int RESULT_NOT_AVAILABLE = 1;
    protected static final int RESULT_NOT_SUPPORTED = 2;
    protected static final int RESULT_SUCCESS = 0;
    protected static final int RESULT_UNKNOWN = 5;
    protected final AppOpsManager mAppOps;
    protected final Context mContext;
    protected final Handler mHandler;
    private boolean mHasIsSupported;
    /* access modifiers changed from: private */
    public volatile boolean mIsRegistered;
    private boolean mIsSupported;
    private int mLastReportedResult = 5;
    /* access modifiers changed from: private */
    public final Map<IBinder, RemoteListenerHelper<TListener>.IdentifiedListener> mListenerMap = new HashMap();
    /* access modifiers changed from: private */
    public final String mTag;

    protected interface ListenerOperation<TListener extends IInterface> {
        void execute(TListener tlistener, CallerIdentity callerIdentity) throws RemoteException;
    }

    /* access modifiers changed from: protected */
    public abstract ListenerOperation<TListener> getHandlerOperation(int i);

    /* access modifiers changed from: protected */
    public abstract boolean isAvailableInPlatform();

    /* access modifiers changed from: protected */
    public abstract boolean isGpsEnabled();

    /* access modifiers changed from: protected */
    public abstract int registerWithService();

    /* access modifiers changed from: protected */
    public abstract void unregisterFromService();

    protected RemoteListenerHelper(Context context, Handler handler, String name) {
        Preconditions.checkNotNull(name);
        this.mHandler = handler;
        this.mTag = name;
        this.mContext = context;
        this.mAppOps = (AppOpsManager) context.getSystemService("appops");
    }

    public boolean isRegistered() {
        return this.mIsRegistered;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0051, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addListener(TListener r6, com.android.server.location.CallerIdentity r7) {
        /*
            r5 = this;
            java.lang.String r0 = "Attempted to register a 'null' listener."
            com.android.internal.util.Preconditions.checkNotNull(r6, r0)
            android.os.IBinder r0 = r6.asBinder()
            java.util.Map<android.os.IBinder, com.android.server.location.RemoteListenerHelper<TListener>$IdentifiedListener> r1 = r5.mListenerMap
            monitor-enter(r1)
            java.util.Map<android.os.IBinder, com.android.server.location.RemoteListenerHelper<TListener>$IdentifiedListener> r2 = r5.mListenerMap     // Catch:{ all -> 0x0052 }
            boolean r2 = r2.containsKey(r0)     // Catch:{ all -> 0x0052 }
            if (r2 == 0) goto L_0x0016
            monitor-exit(r1)     // Catch:{ all -> 0x0052 }
            return
        L_0x0016:
            com.android.server.location.RemoteListenerHelper$IdentifiedListener r2 = new com.android.server.location.RemoteListenerHelper$IdentifiedListener     // Catch:{ all -> 0x0052 }
            r3 = 0
            r2.<init>(r6, r7)     // Catch:{ all -> 0x0052 }
            java.util.Map<android.os.IBinder, com.android.server.location.RemoteListenerHelper<TListener>$IdentifiedListener> r3 = r5.mListenerMap     // Catch:{ all -> 0x0052 }
            r3.put(r0, r2)     // Catch:{ all -> 0x0052 }
            boolean r3 = r5.isAvailableInPlatform()     // Catch:{ all -> 0x0052 }
            if (r3 != 0) goto L_0x0029
            r3 = 1
            goto L_0x0047
        L_0x0029:
            boolean r3 = r5.mHasIsSupported     // Catch:{ all -> 0x0052 }
            if (r3 == 0) goto L_0x0033
            boolean r3 = r5.mIsSupported     // Catch:{ all -> 0x0052 }
            if (r3 != 0) goto L_0x0033
            r3 = 2
            goto L_0x0047
        L_0x0033:
            boolean r3 = r5.isGpsEnabled()     // Catch:{ all -> 0x0052 }
            if (r3 != 0) goto L_0x003b
            r3 = 3
            goto L_0x0047
        L_0x003b:
            boolean r3 = r5.mHasIsSupported     // Catch:{ all -> 0x0052 }
            if (r3 == 0) goto L_0x0050
            boolean r3 = r5.mIsSupported     // Catch:{ all -> 0x0052 }
            if (r3 == 0) goto L_0x0050
            r5.tryRegister()     // Catch:{ all -> 0x0052 }
            r3 = 0
        L_0x0047:
            com.android.server.location.RemoteListenerHelper$ListenerOperation r4 = r5.getHandlerOperation(r3)     // Catch:{ all -> 0x0052 }
            r5.post(r2, r4)     // Catch:{ all -> 0x0052 }
            monitor-exit(r1)     // Catch:{ all -> 0x0052 }
            return
        L_0x0050:
            monitor-exit(r1)     // Catch:{ all -> 0x0052 }
            return
        L_0x0052:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0052 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.location.RemoteListenerHelper.addListener(android.os.IInterface, com.android.server.location.CallerIdentity):void");
    }

    public void removeListener(TListener listener) {
        Preconditions.checkNotNull(listener, "Attempted to remove a 'null' listener.");
        synchronized (this.mListenerMap) {
            this.mListenerMap.remove(listener.asBinder());
            if (this.mListenerMap.isEmpty()) {
                tryUnregister();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void foreach(ListenerOperation<TListener> operation) {
        synchronized (this.mListenerMap) {
            foreachUnsafe(operation);
        }
    }

    /* access modifiers changed from: protected */
    public void setSupported(boolean value) {
        synchronized (this.mListenerMap) {
            this.mHasIsSupported = true;
            this.mIsSupported = value;
        }
    }

    /* access modifiers changed from: protected */
    public void tryUpdateRegistrationWithService() {
        synchronized (this.mListenerMap) {
            if (!isGpsEnabled()) {
                tryUnregister();
            } else if (!this.mListenerMap.isEmpty()) {
                tryRegister();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void updateResult() {
        synchronized (this.mListenerMap) {
            int newResult = calculateCurrentResultUnsafe();
            if (this.mLastReportedResult != newResult) {
                foreachUnsafe(getHandlerOperation(newResult));
                this.mLastReportedResult = newResult;
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasPermission(Context context, CallerIdentity callerIdentity) {
        if (LocationPermissionUtil.doesCallerReportToAppOps(context, callerIdentity)) {
            if (this.mAppOps.checkOpNoThrow(1, callerIdentity.mUid, callerIdentity.mPackageName) == 0) {
                return true;
            }
            return false;
        } else if (this.mAppOps.noteOpNoThrow(1, callerIdentity.mUid, callerIdentity.mPackageName) == 0) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public void logPermissionDisabledEventNotReported(String tag, String packageName, String event) {
        if (Log.isLoggable(tag, 3)) {
            Log.d(tag, "Location permission disabled. Skipping " + event + " reporting for app: " + packageName);
        }
    }

    /* access modifiers changed from: private */
    public void foreachUnsafe(ListenerOperation<TListener> operation) {
        for (RemoteListenerHelper<TListener>.IdentifiedListener identifiedListener : this.mListenerMap.values()) {
            post(identifiedListener, operation);
        }
    }

    private void post(RemoteListenerHelper<TListener>.IdentifiedListener identifiedListener, ListenerOperation<TListener> operation) {
        if (operation != null) {
            this.mHandler.post(new HandlerRunnable(identifiedListener, operation));
        }
    }

    private void tryRegister() {
        this.mHandler.post(new Runnable() {
            int registrationState = 4;

            public void run() {
                if (!RemoteListenerHelper.this.mIsRegistered) {
                    this.registrationState = RemoteListenerHelper.this.registerWithService();
                    boolean unused = RemoteListenerHelper.this.mIsRegistered = this.registrationState == 0;
                }
                if (!RemoteListenerHelper.this.mIsRegistered) {
                    RemoteListenerHelper.this.mHandler.post(new Runnable() {
                        public final void run() {
                            RemoteListenerHelper.AnonymousClass1.this.lambda$run$0$RemoteListenerHelper$1();
                        }
                    });
                }
            }

            public /* synthetic */ void lambda$run$0$RemoteListenerHelper$1() {
                synchronized (RemoteListenerHelper.this.mListenerMap) {
                    RemoteListenerHelper.this.foreachUnsafe(RemoteListenerHelper.this.getHandlerOperation(this.registrationState));
                }
            }
        });
    }

    private void tryUnregister() {
        this.mHandler.post(new Runnable() {
            public final void run() {
                RemoteListenerHelper.this.lambda$tryUnregister$0$RemoteListenerHelper();
            }
        });
    }

    public /* synthetic */ void lambda$tryUnregister$0$RemoteListenerHelper() {
        if (this.mIsRegistered) {
            unregisterFromService();
            this.mIsRegistered = false;
        }
    }

    private int calculateCurrentResultUnsafe() {
        if (!isAvailableInPlatform()) {
            return 1;
        }
        if (!this.mHasIsSupported || this.mListenerMap.isEmpty()) {
            return 5;
        }
        if (!this.mIsSupported) {
            return 2;
        }
        if (!isGpsEnabled()) {
            return 3;
        }
        return 0;
    }

    private class IdentifiedListener {
        /* access modifiers changed from: private */
        public final CallerIdentity mCallerIdentity;
        /* access modifiers changed from: private */
        public final TListener mListener;

        private IdentifiedListener(TListener listener, CallerIdentity callerIdentity) {
            this.mListener = listener;
            this.mCallerIdentity = callerIdentity;
        }
    }

    private class HandlerRunnable implements Runnable {
        private final RemoteListenerHelper<TListener>.IdentifiedListener mIdentifiedListener;
        private final ListenerOperation<TListener> mOperation;

        private HandlerRunnable(RemoteListenerHelper<TListener>.IdentifiedListener identifiedListener, ListenerOperation<TListener> operation) {
            this.mIdentifiedListener = identifiedListener;
            this.mOperation = operation;
        }

        public void run() {
            try {
                this.mOperation.execute(this.mIdentifiedListener.mListener, this.mIdentifiedListener.mCallerIdentity);
            } catch (RemoteException e) {
                Log.v(RemoteListenerHelper.this.mTag, "Error in monitored listener.", e);
            }
        }
    }
}
