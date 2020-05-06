package com.android.server.utils;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Slog;
import com.android.server.utils.ManagedApplicationService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ManagedApplicationService {
    private static final int MAX_RETRY_COUNT = 4;
    private static final long MAX_RETRY_DURATION_MS = 16000;
    private static final long MIN_RETRY_DURATION_MS = 2000;
    public static final int RETRY_BEST_EFFORT = 3;
    public static final int RETRY_FOREVER = 1;
    public static final int RETRY_NEVER = 2;
    private static final long RETRY_RESET_TIME_MS = 64000;
    /* access modifiers changed from: private */
    public final String TAG = getClass().getSimpleName();
    /* access modifiers changed from: private */
    public IInterface mBoundInterface;
    /* access modifiers changed from: private */
    public final BinderChecker mChecker;
    private final int mClientLabel;
    /* access modifiers changed from: private */
    public final ComponentName mComponent;
    /* access modifiers changed from: private */
    public ServiceConnection mConnection;
    private final Context mContext;
    /* access modifiers changed from: private */
    public final EventCallback mEventCb;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private final boolean mIsImportant;
    private long mLastRetryTimeMs;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private long mNextRetryDurationMs = MIN_RETRY_DURATION_MS;
    /* access modifiers changed from: private */
    public PendingEvent mPendingEvent;
    private int mRetryCount;
    private final Runnable mRetryRunnable = new Runnable() {
        public final void run() {
            ManagedApplicationService.this.doRetry();
        }
    };
    private final int mRetryType;
    private boolean mRetrying;
    private final String mSettingsAction;
    private final int mUserId;

    public interface BinderChecker {
        IInterface asInterface(IBinder iBinder);

        boolean checkType(IInterface iInterface);
    }

    public interface EventCallback {
        void onServiceEvent(LogEvent logEvent);
    }

    public interface LogFormattable {
        String toLogString(SimpleDateFormat simpleDateFormat);
    }

    public interface PendingEvent {
        void runEvent(IInterface iInterface) throws RemoteException;
    }

    public static class LogEvent implements LogFormattable {
        public static final int EVENT_BINDING_DIED = 3;
        public static final int EVENT_CONNECTED = 1;
        public static final int EVENT_DISCONNECTED = 2;
        public static final int EVENT_STOPPED_PERMANENTLY = 4;
        public final ComponentName component;
        public final int event;
        public final long timestamp;

        public LogEvent(long timestamp2, ComponentName component2, int event2) {
            this.timestamp = timestamp2;
            this.component = component2;
            this.event = event2;
        }

        public String toLogString(SimpleDateFormat dateFormat) {
            StringBuilder sb = new StringBuilder();
            sb.append(dateFormat.format(new Date(this.timestamp)));
            sb.append("   ");
            sb.append(eventToString(this.event));
            sb.append(" Managed Service: ");
            ComponentName componentName = this.component;
            sb.append(componentName == null ? "None" : componentName.flattenToString());
            return sb.toString();
        }

        public static String eventToString(int event2) {
            if (event2 == 1) {
                return "Connected";
            }
            if (event2 == 2) {
                return "Disconnected";
            }
            if (event2 == 3) {
                return "Binding Died For";
            }
            if (event2 != 4) {
                return "Unknown Event Occurred";
            }
            return "Permanently Stopped";
        }
    }

    private ManagedApplicationService(Context context, ComponentName component, int userId, int clientLabel, String settingsAction, BinderChecker binderChecker, boolean isImportant, int retryType, Handler handler, EventCallback eventCallback) {
        this.mContext = context;
        this.mComponent = component;
        this.mUserId = userId;
        this.mClientLabel = clientLabel;
        this.mSettingsAction = settingsAction;
        this.mChecker = binderChecker;
        this.mIsImportant = isImportant;
        this.mRetryType = retryType;
        this.mHandler = handler;
        this.mEventCb = eventCallback;
    }

    public static ManagedApplicationService build(Context context, ComponentName component, int userId, int clientLabel, String settingsAction, BinderChecker binderChecker, boolean isImportant, int retryType, Handler handler, EventCallback eventCallback) {
        return new ManagedApplicationService(context, component, userId, clientLabel, settingsAction, binderChecker, isImportant, retryType, handler, eventCallback);
    }

    public int getUserId() {
        return this.mUserId;
    }

    public ComponentName getComponent() {
        return this.mComponent;
    }

    public boolean disconnectIfNotMatching(ComponentName componentName, int userId) {
        if (matches(componentName, userId)) {
            return false;
        }
        disconnect();
        return true;
    }

    public void sendEvent(PendingEvent event) {
        IInterface iface;
        synchronized (this.mLock) {
            iface = this.mBoundInterface;
            if (iface == null) {
                this.mPendingEvent = event;
            }
        }
        if (iface != null) {
            try {
                event.runEvent(iface);
            } catch (RemoteException | RuntimeException ex) {
                Slog.e(this.TAG, "Received exception from user service: ", ex);
            }
        }
    }

    public void disconnect() {
        synchronized (this.mLock) {
            if (this.mConnection != null) {
                this.mContext.unbindService(this.mConnection);
                this.mConnection = null;
                this.mBoundInterface = null;
            }
        }
    }

    public void connect() {
        synchronized (this.mLock) {
            if (this.mConnection == null) {
                Intent intent = new Intent().setComponent(this.mComponent);
                if (this.mClientLabel != 0) {
                    intent.putExtra("android.intent.extra.client_label", this.mClientLabel);
                }
                if (this.mSettingsAction != null) {
                    intent.putExtra("android.intent.extra.client_intent", PendingIntent.getActivity(this.mContext, 0, new Intent(this.mSettingsAction), 0));
                }
                this.mConnection = new ServiceConnection() {
                    public void onBindingDied(ComponentName componentName) {
                        long timestamp = System.currentTimeMillis();
                        String access$000 = ManagedApplicationService.this.TAG;
                        Slog.w(access$000, "Service binding died: " + componentName);
                        synchronized (ManagedApplicationService.this.mLock) {
                            if (ManagedApplicationService.this.mConnection == this) {
                                ManagedApplicationService.this.mHandler.post(new Runnable(timestamp) {
                                    private final /* synthetic */ long f$1;

                                    {
                                        this.f$1 = r2;
                                    }

                                    public final void run() {
                                        ManagedApplicationService.AnonymousClass1.this.lambda$onBindingDied$0$ManagedApplicationService$1(this.f$1);
                                    }
                                });
                                IInterface unused = ManagedApplicationService.this.mBoundInterface = null;
                                ManagedApplicationService.this.startRetriesLocked();
                            }
                        }
                    }

                    public /* synthetic */ void lambda$onBindingDied$0$ManagedApplicationService$1(long timestamp) {
                        ManagedApplicationService.this.mEventCb.onServiceEvent(new LogEvent(timestamp, ManagedApplicationService.this.mComponent, 3));
                    }

                    /* JADX WARNING: Code restructure failed: missing block: B:16:0x00ad, code lost:
                        if (r2 == null) goto L_?;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:17:0x00af, code lost:
                        if (r3 == null) goto L_?;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
                        r3.runEvent(r2);
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:20:0x00b5, code lost:
                        r4 = move-exception;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00b6, code lost:
                        android.util.Slog.e(com.android.server.utils.ManagedApplicationService.access$000(r8.this$0), "Received exception from user service: ", r4);
                        com.android.server.utils.ManagedApplicationService.access$500(r8.this$0);
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
                        return;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
                        return;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
                        return;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
                        return;
                     */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void onServiceConnected(android.content.ComponentName r9, android.os.IBinder r10) {
                        /*
                            r8 = this;
                            long r0 = java.lang.System.currentTimeMillis()
                            com.android.server.utils.ManagedApplicationService r2 = com.android.server.utils.ManagedApplicationService.this
                            java.lang.String r2 = r2.TAG
                            java.lang.StringBuilder r3 = new java.lang.StringBuilder
                            r3.<init>()
                            java.lang.String r4 = "Service connected: "
                            r3.append(r4)
                            r3.append(r9)
                            java.lang.String r3 = r3.toString()
                            android.util.Slog.i(r2, r3)
                            r2 = 0
                            r3 = 0
                            com.android.server.utils.ManagedApplicationService r4 = com.android.server.utils.ManagedApplicationService.this
                            java.lang.Object r4 = r4.mLock
                            monitor-enter(r4)
                            com.android.server.utils.ManagedApplicationService r5 = com.android.server.utils.ManagedApplicationService.this     // Catch:{ all -> 0x00c7 }
                            android.content.ServiceConnection r5 = r5.mConnection     // Catch:{ all -> 0x00c7 }
                            if (r5 == r8) goto L_0x0031
                            monitor-exit(r4)     // Catch:{ all -> 0x00c7 }
                            return
                        L_0x0031:
                            com.android.server.utils.ManagedApplicationService r5 = com.android.server.utils.ManagedApplicationService.this     // Catch:{ all -> 0x00c7 }
                            android.os.Handler r5 = r5.mHandler     // Catch:{ all -> 0x00c7 }
                            com.android.server.utils.-$$Lambda$ManagedApplicationService$1$IyJ0KZQns9OXjnHsop6Gzx7uhvA r6 = new com.android.server.utils.-$$Lambda$ManagedApplicationService$1$IyJ0KZQns9OXjnHsop6Gzx7uhvA     // Catch:{ all -> 0x00c7 }
                            r6.<init>(r0)     // Catch:{ all -> 0x00c7 }
                            r5.post(r6)     // Catch:{ all -> 0x00c7 }
                            com.android.server.utils.ManagedApplicationService r5 = com.android.server.utils.ManagedApplicationService.this     // Catch:{ all -> 0x00c7 }
                            r5.stopRetriesLocked()     // Catch:{ all -> 0x00c7 }
                            com.android.server.utils.ManagedApplicationService r5 = com.android.server.utils.ManagedApplicationService.this     // Catch:{ all -> 0x00c7 }
                            r6 = 0
                            android.os.IInterface unused = r5.mBoundInterface = r6     // Catch:{ all -> 0x00c7 }
                            com.android.server.utils.ManagedApplicationService r5 = com.android.server.utils.ManagedApplicationService.this     // Catch:{ all -> 0x00c7 }
                            com.android.server.utils.ManagedApplicationService$BinderChecker r5 = r5.mChecker     // Catch:{ all -> 0x00c7 }
                            if (r5 == 0) goto L_0x00ac
                            com.android.server.utils.ManagedApplicationService r5 = com.android.server.utils.ManagedApplicationService.this     // Catch:{ all -> 0x00c7 }
                            com.android.server.utils.ManagedApplicationService r7 = com.android.server.utils.ManagedApplicationService.this     // Catch:{ all -> 0x00c7 }
                            com.android.server.utils.ManagedApplicationService$BinderChecker r7 = r7.mChecker     // Catch:{ all -> 0x00c7 }
                            android.os.IInterface r7 = r7.asInterface(r10)     // Catch:{ all -> 0x00c7 }
                            android.os.IInterface unused = r5.mBoundInterface = r7     // Catch:{ all -> 0x00c7 }
                            com.android.server.utils.ManagedApplicationService r5 = com.android.server.utils.ManagedApplicationService.this     // Catch:{ all -> 0x00c7 }
                            com.android.server.utils.ManagedApplicationService$BinderChecker r5 = r5.mChecker     // Catch:{ all -> 0x00c7 }
                            com.android.server.utils.ManagedApplicationService r7 = com.android.server.utils.ManagedApplicationService.this     // Catch:{ all -> 0x00c7 }
                            android.os.IInterface r7 = r7.mBoundInterface     // Catch:{ all -> 0x00c7 }
                            boolean r5 = r5.checkType(r7)     // Catch:{ all -> 0x00c7 }
                            if (r5 != 0) goto L_0x0099
                            com.android.server.utils.ManagedApplicationService r5 = com.android.server.utils.ManagedApplicationService.this     // Catch:{ all -> 0x00c7 }
                            android.os.IInterface unused = r5.mBoundInterface = r6     // Catch:{ all -> 0x00c7 }
                            com.android.server.utils.ManagedApplicationService r5 = com.android.server.utils.ManagedApplicationService.this     // Catch:{ all -> 0x00c7 }
                            java.lang.String r5 = r5.TAG     // Catch:{ all -> 0x00c7 }
                            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c7 }
                            r6.<init>()     // Catch:{ all -> 0x00c7 }
                            java.lang.String r7 = "Invalid binder from "
                            r6.append(r7)     // Catch:{ all -> 0x00c7 }
                            r6.append(r9)     // Catch:{ all -> 0x00c7 }
                            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00c7 }
                            android.util.Slog.w(r5, r6)     // Catch:{ all -> 0x00c7 }
                            com.android.server.utils.ManagedApplicationService r5 = com.android.server.utils.ManagedApplicationService.this     // Catch:{ all -> 0x00c7 }
                            r5.startRetriesLocked()     // Catch:{ all -> 0x00c7 }
                            monitor-exit(r4)     // Catch:{ all -> 0x00c7 }
                            return
                        L_0x0099:
                            com.android.server.utils.ManagedApplicationService r5 = com.android.server.utils.ManagedApplicationService.this     // Catch:{ all -> 0x00c7 }
                            android.os.IInterface r5 = r5.mBoundInterface     // Catch:{ all -> 0x00c7 }
                            r2 = r5
                            com.android.server.utils.ManagedApplicationService r5 = com.android.server.utils.ManagedApplicationService.this     // Catch:{ all -> 0x00c7 }
                            com.android.server.utils.ManagedApplicationService$PendingEvent r5 = r5.mPendingEvent     // Catch:{ all -> 0x00c7 }
                            r3 = r5
                            com.android.server.utils.ManagedApplicationService r5 = com.android.server.utils.ManagedApplicationService.this     // Catch:{ all -> 0x00c7 }
                            com.android.server.utils.ManagedApplicationService.PendingEvent unused = r5.mPendingEvent = r6     // Catch:{ all -> 0x00c7 }
                        L_0x00ac:
                            monitor-exit(r4)     // Catch:{ all -> 0x00c7 }
                            if (r2 == 0) goto L_0x00c6
                            if (r3 == 0) goto L_0x00c6
                            r3.runEvent(r2)     // Catch:{ RemoteException | RuntimeException -> 0x00b5 }
                            goto L_0x00c6
                        L_0x00b5:
                            r4 = move-exception
                            com.android.server.utils.ManagedApplicationService r5 = com.android.server.utils.ManagedApplicationService.this
                            java.lang.String r5 = r5.TAG
                            java.lang.String r6 = "Received exception from user service: "
                            android.util.Slog.e(r5, r6, r4)
                            com.android.server.utils.ManagedApplicationService r5 = com.android.server.utils.ManagedApplicationService.this
                            r5.startRetriesLocked()
                        L_0x00c6:
                            return
                        L_0x00c7:
                            r5 = move-exception
                            monitor-exit(r4)     // Catch:{ all -> 0x00c7 }
                            throw r5
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.android.server.utils.ManagedApplicationService.AnonymousClass1.onServiceConnected(android.content.ComponentName, android.os.IBinder):void");
                    }

                    public /* synthetic */ void lambda$onServiceConnected$1$ManagedApplicationService$1(long timestamp) {
                        ManagedApplicationService.this.mEventCb.onServiceEvent(new LogEvent(timestamp, ManagedApplicationService.this.mComponent, 1));
                    }

                    public void onServiceDisconnected(ComponentName componentName) {
                        long timestamp = System.currentTimeMillis();
                        String access$000 = ManagedApplicationService.this.TAG;
                        Slog.w(access$000, "Service disconnected: " + componentName);
                        synchronized (ManagedApplicationService.this.mLock) {
                            if (ManagedApplicationService.this.mConnection == this) {
                                ManagedApplicationService.this.mHandler.post(new Runnable(timestamp) {
                                    private final /* synthetic */ long f$1;

                                    {
                                        this.f$1 = r2;
                                    }

                                    public final void run() {
                                        ManagedApplicationService.AnonymousClass1.this.lambda$onServiceDisconnected$2$ManagedApplicationService$1(this.f$1);
                                    }
                                });
                                IInterface unused = ManagedApplicationService.this.mBoundInterface = null;
                                ManagedApplicationService.this.startRetriesLocked();
                            }
                        }
                    }

                    public /* synthetic */ void lambda$onServiceDisconnected$2$ManagedApplicationService$1(long timestamp) {
                        ManagedApplicationService.this.mEventCb.onServiceEvent(new LogEvent(timestamp, ManagedApplicationService.this.mComponent, 2));
                    }
                };
                int flags = 67108865;
                if (this.mIsImportant) {
                    flags = 67108865 | 64;
                }
                try {
                    if (!this.mContext.bindServiceAsUser(intent, this.mConnection, flags, new UserHandle(this.mUserId))) {
                        String str = this.TAG;
                        Slog.w(str, "Unable to bind service: " + intent);
                        startRetriesLocked();
                    }
                } catch (SecurityException e) {
                    String str2 = this.TAG;
                    Slog.w(str2, "Unable to bind service: " + intent, e);
                    startRetriesLocked();
                }
            }
        }
    }

    private boolean matches(ComponentName component, int userId) {
        return Objects.equals(this.mComponent, component) && this.mUserId == userId;
    }

    /* access modifiers changed from: private */
    public void startRetriesLocked() {
        if (checkAndDeliverServiceDiedCbLocked()) {
            disconnect();
        } else if (!this.mRetrying) {
            this.mRetrying = true;
            queueRetryLocked();
        }
    }

    /* access modifiers changed from: private */
    public void stopRetriesLocked() {
        this.mRetrying = false;
        this.mHandler.removeCallbacks(this.mRetryRunnable);
    }

    private void queueRetryLocked() {
        long now = SystemClock.uptimeMillis();
        if (now - this.mLastRetryTimeMs > RETRY_RESET_TIME_MS) {
            this.mNextRetryDurationMs = MIN_RETRY_DURATION_MS;
            this.mRetryCount = 0;
        }
        this.mLastRetryTimeMs = now;
        this.mHandler.postDelayed(this.mRetryRunnable, this.mNextRetryDurationMs);
        this.mNextRetryDurationMs = Math.min(this.mNextRetryDurationMs * 2, MAX_RETRY_DURATION_MS);
        this.mRetryCount++;
    }

    private boolean checkAndDeliverServiceDiedCbLocked() {
        int i = this.mRetryType;
        if (i != 2 && (i != 3 || this.mRetryCount < 4)) {
            return false;
        }
        String str = this.TAG;
        Slog.e(str, "Service " + this.mComponent + " has died too much, not retrying.");
        if (this.mEventCb == null) {
            return true;
        }
        this.mHandler.post(new Runnable(System.currentTimeMillis()) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ManagedApplicationService.this.lambda$checkAndDeliverServiceDiedCbLocked$0$ManagedApplicationService(this.f$1);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$checkAndDeliverServiceDiedCbLocked$0$ManagedApplicationService(long timestamp) {
        this.mEventCb.onServiceEvent(new LogEvent(timestamp, this.mComponent, 4));
    }

    /* access modifiers changed from: private */
    public void doRetry() {
        synchronized (this.mLock) {
            if (this.mConnection != null) {
                if (this.mRetrying) {
                    String str = this.TAG;
                    Slog.i(str, "Attempting to reconnect " + this.mComponent + "...");
                    disconnect();
                    if (!checkAndDeliverServiceDiedCbLocked()) {
                        queueRetryLocked();
                        connect();
                    }
                }
            }
        }
    }
}
