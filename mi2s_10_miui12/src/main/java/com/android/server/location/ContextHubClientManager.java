package com.android.server.location;

import android.app.PendingIntent;
import android.content.Context;
import android.hardware.contexthub.V1_0.ContextHubMsg;
import android.hardware.contexthub.V1_0.IContexthub;
import android.hardware.location.ContextHubInfo;
import android.hardware.location.IContextHubClient;
import android.hardware.location.IContextHubClientCallback;
import android.hardware.location.NanoAppMessage;
import android.os.RemoteException;
import android.util.Log;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

class ContextHubClientManager {
    private static final boolean DEBUG_LOG_ENABLED = false;
    private static final int MAX_CLIENT_ID = 32767;
    private static final String TAG = "ContextHubClientManager";
    private final Context mContext;
    private final IContexthub mContextHubProxy;
    private final ConcurrentHashMap<Short, ContextHubClientBroker> mHostEndPointIdToClientMap = new ConcurrentHashMap<>();
    private int mNextHostEndPointId = 0;

    ContextHubClientManager(Context context, IContexthub contextHubProxy) {
        this.mContext = context;
        this.mContextHubProxy = contextHubProxy;
    }

    /* JADX WARNING: type inference failed for: r0v2, types: [com.android.server.location.ContextHubClientBroker, java.lang.Object, android.os.IBinder] */
    /* access modifiers changed from: package-private */
    public IContextHubClient registerClient(ContextHubInfo contextHubInfo, IContextHubClientCallback clientCallback) {
        ? contextHubClientBroker;
        synchronized (this) {
            short hostEndPointId = getHostEndPointId();
            contextHubClientBroker = new ContextHubClientBroker(this.mContext, this.mContextHubProxy, this, contextHubInfo, hostEndPointId, clientCallback);
            this.mHostEndPointIdToClientMap.put(Short.valueOf(hostEndPointId), contextHubClientBroker);
        }
        try {
            contextHubClientBroker.attachDeathRecipient();
            Log.d(TAG, "Registered client with host endpoint ID " + contextHubClientBroker.getHostEndPointId());
            return IContextHubClient.Stub.asInterface(contextHubClientBroker);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to attach death recipient to client");
            contextHubClientBroker.close();
            return null;
        }
    }

    /* JADX WARNING: type inference failed for: r0v5, types: [com.android.server.location.ContextHubClientBroker, android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r0v7 */
    /* JADX WARNING: type inference failed for: r0v8 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0037, code lost:
        android.util.Log.d(TAG, r11 + " client with host endpoint ID " + r0.getHostEndPointId());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0058, code lost:
        return android.hardware.location.IContextHubClient.Stub.asInterface(r0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.hardware.location.IContextHubClient registerClient(android.hardware.location.ContextHubInfo r17, android.app.PendingIntent r18, long r19) {
        /*
            r16 = this;
            r10 = r16
            java.lang.String r11 = "Regenerated"
            monitor-enter(r16)
            int r0 = r17.getId()     // Catch:{ all -> 0x005b }
            r12 = r18
            r13 = r19
            com.android.server.location.ContextHubClientBroker r0 = r10.getClientBroker(r0, r12, r13)     // Catch:{ all -> 0x0059 }
            if (r0 != 0) goto L_0x0036
            short r6 = r16.getHostEndPointId()     // Catch:{ all -> 0x0059 }
            com.android.server.location.ContextHubClientBroker r15 = new com.android.server.location.ContextHubClientBroker     // Catch:{ all -> 0x0059 }
            android.content.Context r2 = r10.mContext     // Catch:{ all -> 0x0059 }
            android.hardware.contexthub.V1_0.IContexthub r3 = r10.mContextHubProxy     // Catch:{ all -> 0x0059 }
            r1 = r15
            r4 = r16
            r5 = r17
            r7 = r18
            r8 = r19
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x0059 }
            r0 = r15
            java.util.concurrent.ConcurrentHashMap<java.lang.Short, com.android.server.location.ContextHubClientBroker> r1 = r10.mHostEndPointIdToClientMap     // Catch:{ all -> 0x0059 }
            java.lang.Short r2 = java.lang.Short.valueOf(r6)     // Catch:{ all -> 0x0059 }
            r1.put(r2, r0)     // Catch:{ all -> 0x0059 }
            java.lang.String r1 = "Registered"
            r11 = r1
        L_0x0036:
            monitor-exit(r16)     // Catch:{ all -> 0x0059 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r11)
            java.lang.String r2 = " client with host endpoint ID "
            r1.append(r2)
            short r2 = r0.getHostEndPointId()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "ContextHubClientManager"
            android.util.Log.d(r2, r1)
            android.hardware.location.IContextHubClient r1 = android.hardware.location.IContextHubClient.Stub.asInterface(r0)
            return r1
        L_0x0059:
            r0 = move-exception
            goto L_0x0060
        L_0x005b:
            r0 = move-exception
            r12 = r18
            r13 = r19
        L_0x0060:
            monitor-exit(r16)     // Catch:{ all -> 0x0059 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.location.ContextHubClientManager.registerClient(android.hardware.location.ContextHubInfo, android.app.PendingIntent, long):android.hardware.location.IContextHubClient");
    }

    /* access modifiers changed from: package-private */
    public void onMessageFromNanoApp(int contextHubId, ContextHubMsg message) {
        NanoAppMessage clientMessage = ContextHubServiceUtil.createNanoAppMessage(message);
        if (clientMessage.isBroadcastMessage()) {
            broadcastMessage(contextHubId, clientMessage);
            return;
        }
        ContextHubClientBroker proxy = this.mHostEndPointIdToClientMap.get(Short.valueOf(message.hostEndPoint));
        if (proxy != null) {
            proxy.sendMessageToClient(clientMessage);
            return;
        }
        Log.e(TAG, "Cannot send message to unregistered client (host endpoint ID = " + message.hostEndPoint + ")");
    }

    /* access modifiers changed from: package-private */
    public void unregisterClient(short hostEndPointId) {
        if (this.mHostEndPointIdToClientMap.remove(Short.valueOf(hostEndPointId)) != null) {
            Log.d(TAG, "Unregistered client with host endpoint ID " + hostEndPointId);
            return;
        }
        Log.e(TAG, "Cannot unregister non-existing client with host endpoint ID " + hostEndPointId);
    }

    /* access modifiers changed from: package-private */
    public void onNanoAppLoaded(int contextHubId, long nanoAppId) {
        forEachClientOfHub(contextHubId, new Consumer(nanoAppId) {
            private final /* synthetic */ long f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                ((ContextHubClientBroker) obj).onNanoAppLoaded(this.f$0);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void onNanoAppUnloaded(int contextHubId, long nanoAppId) {
        forEachClientOfHub(contextHubId, new Consumer(nanoAppId) {
            private final /* synthetic */ long f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                ((ContextHubClientBroker) obj).onNanoAppUnloaded(this.f$0);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void onHubReset(int contextHubId) {
        forEachClientOfHub(contextHubId, $$Lambda$ContextHubClientManager$aRAV9Gn84ao4XOiN6tFizfZjHo.INSTANCE);
    }

    /* access modifiers changed from: package-private */
    public void onNanoAppAborted(int contextHubId, long nanoAppId, int abortCode) {
        forEachClientOfHub(contextHubId, new Consumer(nanoAppId, abortCode) {
            private final /* synthetic */ long f$0;
            private final /* synthetic */ int f$1;

            {
                this.f$0 = r1;
                this.f$1 = r3;
            }

            public final void accept(Object obj) {
                ((ContextHubClientBroker) obj).onNanoAppAborted(this.f$0, this.f$1);
            }
        });
    }

    private short getHostEndPointId() {
        if (this.mHostEndPointIdToClientMap.size() != 32768) {
            int id = this.mNextHostEndPointId;
            int i = 0;
            while (true) {
                if (i > MAX_CLIENT_ID) {
                    break;
                }
                int i2 = 0;
                if (!this.mHostEndPointIdToClientMap.containsKey(Short.valueOf((short) id))) {
                    if (id != MAX_CLIENT_ID) {
                        i2 = id + 1;
                    }
                    this.mNextHostEndPointId = i2;
                } else {
                    if (id != MAX_CLIENT_ID) {
                        i2 = id + 1;
                    }
                    id = i2;
                    i++;
                }
            }
            return (short) id;
        }
        throw new IllegalStateException("Could not register client - max limit exceeded");
    }

    private void broadcastMessage(int contextHubId, NanoAppMessage message) {
        forEachClientOfHub(contextHubId, new Consumer(message) {
            private final /* synthetic */ NanoAppMessage f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                ((ContextHubClientBroker) obj).sendMessageToClient(this.f$0);
            }
        });
    }

    private void forEachClientOfHub(int contextHubId, Consumer<ContextHubClientBroker> callback) {
        for (ContextHubClientBroker broker : this.mHostEndPointIdToClientMap.values()) {
            if (broker.getAttachedContextHubId() == contextHubId) {
                callback.accept(broker);
            }
        }
    }

    private ContextHubClientBroker getClientBroker(int contextHubId, PendingIntent pendingIntent, long nanoAppId) {
        for (ContextHubClientBroker broker : this.mHostEndPointIdToClientMap.values()) {
            if (broker.hasPendingIntent(pendingIntent, nanoAppId) && broker.getAttachedContextHubId() == contextHubId) {
                return broker;
            }
        }
        return null;
    }
}
