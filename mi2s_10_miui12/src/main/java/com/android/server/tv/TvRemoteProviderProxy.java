package com.android.server.tv;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.tv.ITvRemoteProvider;
import android.media.tv.ITvRemoteServiceInput;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;

final class TvRemoteProviderProxy implements ServiceConnection {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Log.isLoggable(TAG, 2);
    private static final boolean DEBUG_KEY = false;
    protected static final String SERVICE_INTERFACE = "com.android.media.tv.remoteprovider.TvRemoteProvider";
    private static final String TAG = "TvRemoteProvProxy";
    /* access modifiers changed from: private */
    public Connection mActiveConnection;
    private boolean mBound;
    private final ComponentName mComponentName;
    private boolean mConnectionReady;
    private final Context mContext;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    public ProviderMethods mProviderMethods;
    private boolean mRunning;
    /* access modifiers changed from: private */
    public final int mUid;
    private final int mUserId;

    public interface ProviderMethods {
        void clearInputBridge(TvRemoteProviderProxy tvRemoteProviderProxy, IBinder iBinder);

        void closeInputBridge(TvRemoteProviderProxy tvRemoteProviderProxy, IBinder iBinder);

        void openInputBridge(TvRemoteProviderProxy tvRemoteProviderProxy, IBinder iBinder, String str, int i, int i2, int i3);

        void sendKeyDown(TvRemoteProviderProxy tvRemoteProviderProxy, IBinder iBinder, int i);

        void sendKeyUp(TvRemoteProviderProxy tvRemoteProviderProxy, IBinder iBinder, int i);

        void sendPointerDown(TvRemoteProviderProxy tvRemoteProviderProxy, IBinder iBinder, int i, int i2, int i3);

        void sendPointerSync(TvRemoteProviderProxy tvRemoteProviderProxy, IBinder iBinder);

        void sendPointerUp(TvRemoteProviderProxy tvRemoteProviderProxy, IBinder iBinder, int i);

        void sendTimeStamp(TvRemoteProviderProxy tvRemoteProviderProxy, IBinder iBinder, long j);
    }

    public TvRemoteProviderProxy(Context context, ComponentName componentName, int userId, int uid) {
        this.mContext = context;
        this.mComponentName = componentName;
        this.mUserId = userId;
        this.mUid = uid;
        this.mHandler = new Handler();
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.println(prefix + "Proxy");
        pw.println(prefix + "  mUserId=" + this.mUserId);
        pw.println(prefix + "  mRunning=" + this.mRunning);
        pw.println(prefix + "  mBound=" + this.mBound);
        pw.println(prefix + "  mActiveConnection=" + this.mActiveConnection);
        pw.println(prefix + "  mConnectionReady=" + this.mConnectionReady);
    }

    public void setProviderSink(ProviderMethods provider) {
        this.mProviderMethods = provider;
    }

    public boolean hasComponentName(String packageName, String className) {
        return this.mComponentName.getPackageName().equals(packageName) && this.mComponentName.getClassName().equals(className);
    }

    public void start() {
        if (!this.mRunning) {
            if (DEBUG) {
                Slog.d(TAG, this + ": Starting");
            }
            this.mRunning = true;
            updateBinding();
        }
    }

    public void stop() {
        if (this.mRunning) {
            if (DEBUG) {
                Slog.d(TAG, this + ": Stopping");
            }
            this.mRunning = false;
            updateBinding();
        }
    }

    public void rebindIfDisconnected() {
        synchronized (this.mLock) {
            if (this.mActiveConnection == null && shouldBind()) {
                unbind();
                bind();
            }
        }
    }

    private void updateBinding() {
        if (shouldBind()) {
            bind();
        } else {
            unbind();
        }
    }

    private boolean shouldBind() {
        return this.mRunning;
    }

    private void bind() {
        if (!this.mBound) {
            if (DEBUG) {
                Slog.d(TAG, this + ": Binding");
            }
            Intent service = new Intent(SERVICE_INTERFACE);
            service.setComponent(this.mComponentName);
            try {
                this.mBound = this.mContext.bindServiceAsUser(service, this, 67108865, new UserHandle(this.mUserId));
                if (!this.mBound && DEBUG) {
                    Slog.d(TAG, this + ": Bind failed");
                }
            } catch (SecurityException ex) {
                if (DEBUG) {
                    Slog.d(TAG, this + ": Bind failed", ex);
                }
            }
        }
    }

    private void unbind() {
        if (this.mBound) {
            if (DEBUG) {
                Slog.d(TAG, this + ": Unbinding");
            }
            this.mBound = false;
            disconnect();
            this.mContext.unbindService(this);
        }
    }

    public void onServiceConnected(ComponentName name, IBinder service) {
        if (DEBUG) {
            Slog.d(TAG, this + ": onServiceConnected()");
        }
        if (this.mBound) {
            disconnect();
            ITvRemoteProvider provider = ITvRemoteProvider.Stub.asInterface(service);
            if (provider != null) {
                Connection connection = new Connection(provider);
                if (connection.register()) {
                    synchronized (this.mLock) {
                        this.mActiveConnection = connection;
                    }
                    if (DEBUG) {
                        Slog.d(TAG, this + ": Connected successfully.");
                    }
                } else if (DEBUG) {
                    Slog.d(TAG, this + ": Registration failed");
                }
            } else {
                Slog.e(TAG, this + ": Service returned invalid remote-control provider binder");
            }
        }
    }

    public void onServiceDisconnected(ComponentName name) {
        if (DEBUG) {
            Slog.d(TAG, this + ": Service disconnected");
        }
        disconnect();
    }

    /* access modifiers changed from: private */
    public void onConnectionReady(Connection connection) {
        synchronized (this.mLock) {
            if (DEBUG) {
                Slog.d(TAG, "onConnectionReady");
            }
            if (this.mActiveConnection == connection) {
                if (DEBUG) {
                    Slog.d(TAG, "mConnectionReady = true");
                }
                this.mConnectionReady = true;
            }
        }
    }

    /* access modifiers changed from: private */
    public void onConnectionDied(Connection connection) {
        if (this.mActiveConnection == connection) {
            if (DEBUG) {
                Slog.d(TAG, this + ": Service connection died");
            }
            disconnect();
        }
    }

    private void disconnect() {
        synchronized (this.mLock) {
            if (this.mActiveConnection != null) {
                this.mConnectionReady = false;
                this.mActiveConnection.dispose();
                this.mActiveConnection = null;
            }
        }
    }

    public void inputBridgeConnected(IBinder token) {
        synchronized (this.mLock) {
            if (DEBUG) {
                Slog.d(TAG, this + ": inputBridgeConnected token: " + token);
            }
            if (this.mConnectionReady) {
                this.mActiveConnection.onInputBridgeConnected(token);
            }
        }
    }

    private final class Connection implements IBinder.DeathRecipient {
        private final RemoteServiceInputProvider mServiceInputProvider = new RemoteServiceInputProvider(this);
        private final ITvRemoteProvider mTvRemoteProvider;

        public Connection(ITvRemoteProvider provider) {
            this.mTvRemoteProvider = provider;
        }

        public boolean register() {
            if (TvRemoteProviderProxy.DEBUG) {
                Slog.d(TvRemoteProviderProxy.TAG, "Connection::register()");
            }
            try {
                this.mTvRemoteProvider.asBinder().linkToDeath(this, 0);
                this.mTvRemoteProvider.setRemoteServiceInputSink(this.mServiceInputProvider);
                TvRemoteProviderProxy.this.mHandler.post(new Runnable() {
                    public void run() {
                        TvRemoteProviderProxy.this.onConnectionReady(Connection.this);
                    }
                });
                return true;
            } catch (RemoteException e) {
                binderDied();
                return false;
            }
        }

        public void dispose() {
            if (TvRemoteProviderProxy.DEBUG) {
                Slog.d(TvRemoteProviderProxy.TAG, "Connection::dispose()");
            }
            this.mTvRemoteProvider.asBinder().unlinkToDeath(this, 0);
            this.mServiceInputProvider.dispose();
        }

        public void onInputBridgeConnected(IBinder token) {
            if (TvRemoteProviderProxy.DEBUG) {
                Slog.d(TvRemoteProviderProxy.TAG, this + ": onInputBridgeConnected");
            }
            try {
                this.mTvRemoteProvider.onInputBridgeConnected(token);
            } catch (RemoteException ex) {
                Slog.e(TvRemoteProviderProxy.TAG, "Failed to deliver onInputBridgeConnected. ", ex);
            }
        }

        public void binderDied() {
            TvRemoteProviderProxy.this.mHandler.post(new Runnable() {
                public void run() {
                    TvRemoteProviderProxy.this.onConnectionDied(Connection.this);
                }
            });
        }

        /* Debug info: failed to restart local var, previous not found, register: 15 */
        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x00a0, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void openInputBridge(android.os.IBinder r16, java.lang.String r17, int r18, int r19, int r20) {
            /*
                r15 = this;
                r1 = r15
                com.android.server.tv.TvRemoteProviderProxy r0 = com.android.server.tv.TvRemoteProviderProxy.this
                java.lang.Object r2 = r0.mLock
                monitor-enter(r2)
                com.android.server.tv.TvRemoteProviderProxy r0 = com.android.server.tv.TvRemoteProviderProxy.this     // Catch:{ all -> 0x00a1 }
                com.android.server.tv.TvRemoteProviderProxy$Connection r0 = r0.mActiveConnection     // Catch:{ all -> 0x00a1 }
                if (r0 != r1) goto L_0x007a
                int r0 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x00a1 }
                com.android.server.tv.TvRemoteProviderProxy r3 = com.android.server.tv.TvRemoteProviderProxy.this     // Catch:{ all -> 0x00a1 }
                int r3 = r3.mUid     // Catch:{ all -> 0x00a1 }
                if (r0 != r3) goto L_0x007a
                boolean r0 = com.android.server.tv.TvRemoteProviderProxy.DEBUG     // Catch:{ all -> 0x00a1 }
                if (r0 == 0) goto L_0x004a
                java.lang.String r0 = "TvRemoteProvProxy"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a1 }
                r3.<init>()     // Catch:{ all -> 0x00a1 }
                r3.append(r15)     // Catch:{ all -> 0x00a1 }
                java.lang.String r4 = ": openInputBridge, token="
                r3.append(r4)     // Catch:{ all -> 0x00a1 }
                r4 = r16
                r3.append(r4)     // Catch:{ all -> 0x0048 }
                java.lang.String r5 = ", name="
                r3.append(r5)     // Catch:{ all -> 0x0048 }
                r12 = r17
                r3.append(r12)     // Catch:{ all -> 0x00a8 }
                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00a8 }
                android.util.Slog.d(r0, r3)     // Catch:{ all -> 0x00a8 }
                goto L_0x004e
            L_0x0048:
                r0 = move-exception
                goto L_0x00a4
            L_0x004a:
                r4 = r16
                r12 = r17
            L_0x004e:
                long r5 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x00a8 }
                r13 = r5
                com.android.server.tv.TvRemoteProviderProxy r0 = com.android.server.tv.TvRemoteProviderProxy.this     // Catch:{ all -> 0x0075 }
                com.android.server.tv.TvRemoteProviderProxy$ProviderMethods r0 = r0.mProviderMethods     // Catch:{ all -> 0x0075 }
                if (r0 == 0) goto L_0x0070
                com.android.server.tv.TvRemoteProviderProxy r0 = com.android.server.tv.TvRemoteProviderProxy.this     // Catch:{ all -> 0x0075 }
                com.android.server.tv.TvRemoteProviderProxy$ProviderMethods r5 = r0.mProviderMethods     // Catch:{ all -> 0x0075 }
                com.android.server.tv.TvRemoteProviderProxy r6 = com.android.server.tv.TvRemoteProviderProxy.this     // Catch:{ all -> 0x0075 }
                r7 = r16
                r8 = r17
                r9 = r18
                r10 = r19
                r11 = r20
                r5.openInputBridge(r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x0075 }
            L_0x0070:
                android.os.Binder.restoreCallingIdentity(r13)     // Catch:{ all -> 0x00a8 }
                goto L_0x009f
            L_0x0075:
                r0 = move-exception
                android.os.Binder.restoreCallingIdentity(r13)     // Catch:{ all -> 0x00a8 }
                throw r0     // Catch:{ all -> 0x00a8 }
            L_0x007a:
                r4 = r16
                r12 = r17
                boolean r0 = com.android.server.tv.TvRemoteProviderProxy.DEBUG     // Catch:{ all -> 0x00a8 }
                if (r0 == 0) goto L_0x009f
                java.lang.String r0 = "TvRemoteProvProxy"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a8 }
                r3.<init>()     // Catch:{ all -> 0x00a8 }
                java.lang.String r5 = "openInputBridge, Invalid connection or incorrect uid: "
                r3.append(r5)     // Catch:{ all -> 0x00a8 }
                int r5 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x00a8 }
                r3.append(r5)     // Catch:{ all -> 0x00a8 }
                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00a8 }
                android.util.Slog.w(r0, r3)     // Catch:{ all -> 0x00a8 }
            L_0x009f:
                monitor-exit(r2)     // Catch:{ all -> 0x00a8 }
                return
            L_0x00a1:
                r0 = move-exception
                r4 = r16
            L_0x00a4:
                r12 = r17
            L_0x00a6:
                monitor-exit(r2)     // Catch:{ all -> 0x00a8 }
                throw r0
            L_0x00a8:
                r0 = move-exception
                goto L_0x00a6
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvRemoteProviderProxy.Connection.openInputBridge(android.os.IBinder, java.lang.String, int, int, int):void");
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        /* JADX INFO: finally extract failed */
        /* access modifiers changed from: package-private */
        public void closeInputBridge(IBinder token) {
            synchronized (TvRemoteProviderProxy.this.mLock) {
                if (TvRemoteProviderProxy.this.mActiveConnection == this && Binder.getCallingUid() == TvRemoteProviderProxy.this.mUid) {
                    if (TvRemoteProviderProxy.DEBUG) {
                        Slog.d(TvRemoteProviderProxy.TAG, this + ": closeInputBridge, token=" + token);
                    }
                    long idToken = Binder.clearCallingIdentity();
                    try {
                        if (TvRemoteProviderProxy.this.mProviderMethods != null) {
                            TvRemoteProviderProxy.this.mProviderMethods.closeInputBridge(TvRemoteProviderProxy.this, token);
                        }
                        Binder.restoreCallingIdentity(idToken);
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(idToken);
                        throw th;
                    }
                } else if (TvRemoteProviderProxy.DEBUG) {
                    Slog.w(TvRemoteProviderProxy.TAG, "closeInputBridge, Invalid connection or incorrect uid: " + Binder.getCallingUid());
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        /* JADX INFO: finally extract failed */
        /* access modifiers changed from: package-private */
        public void clearInputBridge(IBinder token) {
            synchronized (TvRemoteProviderProxy.this.mLock) {
                if (TvRemoteProviderProxy.this.mActiveConnection == this && Binder.getCallingUid() == TvRemoteProviderProxy.this.mUid) {
                    if (TvRemoteProviderProxy.DEBUG) {
                        Slog.d(TvRemoteProviderProxy.TAG, this + ": clearInputBridge, token=" + token);
                    }
                    long idToken = Binder.clearCallingIdentity();
                    try {
                        if (TvRemoteProviderProxy.this.mProviderMethods != null) {
                            TvRemoteProviderProxy.this.mProviderMethods.clearInputBridge(TvRemoteProviderProxy.this, token);
                        }
                        Binder.restoreCallingIdentity(idToken);
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(idToken);
                        throw th;
                    }
                } else if (TvRemoteProviderProxy.DEBUG) {
                    Slog.w(TvRemoteProviderProxy.TAG, "clearInputBridge, Invalid connection or incorrect uid: " + Binder.getCallingUid());
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        /* JADX INFO: finally extract failed */
        /* access modifiers changed from: package-private */
        public void sendTimestamp(IBinder token, long timestamp) {
            synchronized (TvRemoteProviderProxy.this.mLock) {
                if (TvRemoteProviderProxy.this.mActiveConnection == this && Binder.getCallingUid() == TvRemoteProviderProxy.this.mUid) {
                    long idToken = Binder.clearCallingIdentity();
                    try {
                        if (TvRemoteProviderProxy.this.mProviderMethods != null) {
                            TvRemoteProviderProxy.this.mProviderMethods.sendTimeStamp(TvRemoteProviderProxy.this, token, timestamp);
                        }
                        Binder.restoreCallingIdentity(idToken);
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(idToken);
                        throw th;
                    }
                } else if (TvRemoteProviderProxy.DEBUG) {
                    Slog.w(TvRemoteProviderProxy.TAG, "sendTimeStamp, Invalid connection or incorrect uid: " + Binder.getCallingUid());
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        /* JADX INFO: finally extract failed */
        /* access modifiers changed from: package-private */
        public void sendKeyDown(IBinder token, int keyCode) {
            synchronized (TvRemoteProviderProxy.this.mLock) {
                if (TvRemoteProviderProxy.this.mActiveConnection == this && Binder.getCallingUid() == TvRemoteProviderProxy.this.mUid) {
                    long idToken = Binder.clearCallingIdentity();
                    try {
                        if (TvRemoteProviderProxy.this.mProviderMethods != null) {
                            TvRemoteProviderProxy.this.mProviderMethods.sendKeyDown(TvRemoteProviderProxy.this, token, keyCode);
                        }
                        Binder.restoreCallingIdentity(idToken);
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(idToken);
                        throw th;
                    }
                } else if (TvRemoteProviderProxy.DEBUG) {
                    Slog.w(TvRemoteProviderProxy.TAG, "sendKeyDown, Invalid connection or incorrect uid: " + Binder.getCallingUid());
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        /* JADX INFO: finally extract failed */
        /* access modifiers changed from: package-private */
        public void sendKeyUp(IBinder token, int keyCode) {
            synchronized (TvRemoteProviderProxy.this.mLock) {
                if (TvRemoteProviderProxy.this.mActiveConnection == this && Binder.getCallingUid() == TvRemoteProviderProxy.this.mUid) {
                    long idToken = Binder.clearCallingIdentity();
                    try {
                        if (TvRemoteProviderProxy.this.mProviderMethods != null) {
                            TvRemoteProviderProxy.this.mProviderMethods.sendKeyUp(TvRemoteProviderProxy.this, token, keyCode);
                        }
                        Binder.restoreCallingIdentity(idToken);
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(idToken);
                        throw th;
                    }
                } else if (TvRemoteProviderProxy.DEBUG) {
                    Slog.w(TvRemoteProviderProxy.TAG, "sendKeyUp, Invalid connection or incorrect uid: " + Binder.getCallingUid());
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 10 */
        /* JADX INFO: finally extract failed */
        /* access modifiers changed from: package-private */
        public void sendPointerDown(IBinder token, int pointerId, int x, int y) {
            synchronized (TvRemoteProviderProxy.this.mLock) {
                if (TvRemoteProviderProxy.this.mActiveConnection == this && Binder.getCallingUid() == TvRemoteProviderProxy.this.mUid) {
                    long idToken = Binder.clearCallingIdentity();
                    try {
                        if (TvRemoteProviderProxy.this.mProviderMethods != null) {
                            TvRemoteProviderProxy.this.mProviderMethods.sendPointerDown(TvRemoteProviderProxy.this, token, pointerId, x, y);
                        }
                        Binder.restoreCallingIdentity(idToken);
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(idToken);
                        throw th;
                    }
                } else if (TvRemoteProviderProxy.DEBUG) {
                    Slog.w(TvRemoteProviderProxy.TAG, "sendPointerDown, Invalid connection or incorrect uid: " + Binder.getCallingUid());
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        /* JADX INFO: finally extract failed */
        /* access modifiers changed from: package-private */
        public void sendPointerUp(IBinder token, int pointerId) {
            synchronized (TvRemoteProviderProxy.this.mLock) {
                if (TvRemoteProviderProxy.this.mActiveConnection == this && Binder.getCallingUid() == TvRemoteProviderProxy.this.mUid) {
                    long idToken = Binder.clearCallingIdentity();
                    try {
                        if (TvRemoteProviderProxy.this.mProviderMethods != null) {
                            TvRemoteProviderProxy.this.mProviderMethods.sendPointerUp(TvRemoteProviderProxy.this, token, pointerId);
                        }
                        Binder.restoreCallingIdentity(idToken);
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(idToken);
                        throw th;
                    }
                } else if (TvRemoteProviderProxy.DEBUG) {
                    Slog.w(TvRemoteProviderProxy.TAG, "sendPointerUp, Invalid connection or incorrect uid: " + Binder.getCallingUid());
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        /* JADX INFO: finally extract failed */
        /* access modifiers changed from: package-private */
        public void sendPointerSync(IBinder token) {
            synchronized (TvRemoteProviderProxy.this.mLock) {
                if (TvRemoteProviderProxy.this.mActiveConnection == this && Binder.getCallingUid() == TvRemoteProviderProxy.this.mUid) {
                    long idToken = Binder.clearCallingIdentity();
                    try {
                        if (TvRemoteProviderProxy.this.mProviderMethods != null) {
                            TvRemoteProviderProxy.this.mProviderMethods.sendPointerSync(TvRemoteProviderProxy.this, token);
                        }
                        Binder.restoreCallingIdentity(idToken);
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(idToken);
                        throw th;
                    }
                } else if (TvRemoteProviderProxy.DEBUG) {
                    Slog.w(TvRemoteProviderProxy.TAG, "sendPointerSync, Invalid connection or incorrect uid: " + Binder.getCallingUid());
                }
            }
        }
    }

    private static final class RemoteServiceInputProvider extends ITvRemoteServiceInput.Stub {
        private final WeakReference<Connection> mConnectionRef;

        public RemoteServiceInputProvider(Connection connection) {
            this.mConnectionRef = new WeakReference<>(connection);
        }

        public void dispose() {
            this.mConnectionRef.clear();
        }

        public void openInputBridge(IBinder token, String name, int width, int height, int maxPointers) throws RemoteException {
            Connection connection = (Connection) this.mConnectionRef.get();
            if (connection != null) {
                connection.openInputBridge(token, name, width, height, maxPointers);
            }
        }

        public void closeInputBridge(IBinder token) throws RemoteException {
            Connection connection = (Connection) this.mConnectionRef.get();
            if (connection != null) {
                connection.closeInputBridge(token);
            }
        }

        public void clearInputBridge(IBinder token) throws RemoteException {
            Connection connection = (Connection) this.mConnectionRef.get();
            if (connection != null) {
                connection.clearInputBridge(token);
            }
        }

        public void sendTimestamp(IBinder token, long timestamp) throws RemoteException {
            Connection connection = (Connection) this.mConnectionRef.get();
            if (connection != null) {
                connection.sendTimestamp(token, timestamp);
            }
        }

        public void sendKeyDown(IBinder token, int keyCode) throws RemoteException {
            Connection connection = (Connection) this.mConnectionRef.get();
            if (connection != null) {
                connection.sendKeyDown(token, keyCode);
            }
        }

        public void sendKeyUp(IBinder token, int keyCode) throws RemoteException {
            Connection connection = (Connection) this.mConnectionRef.get();
            if (connection != null) {
                connection.sendKeyUp(token, keyCode);
            }
        }

        public void sendPointerDown(IBinder token, int pointerId, int x, int y) throws RemoteException {
            Connection connection = (Connection) this.mConnectionRef.get();
            if (connection != null) {
                connection.sendPointerDown(token, pointerId, x, y);
            }
        }

        public void sendPointerUp(IBinder token, int pointerId) throws RemoteException {
            Connection connection = (Connection) this.mConnectionRef.get();
            if (connection != null) {
                connection.sendPointerUp(token, pointerId);
            }
        }

        public void sendPointerSync(IBinder token) throws RemoteException {
            Connection connection = (Connection) this.mConnectionRef.get();
            if (connection != null) {
                connection.sendPointerSync(token);
            }
        }
    }
}
