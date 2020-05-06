package com.miui.securitycenter.dynamic;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.dynamic.app.AppActivityManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class DynamicServiceManager {
    private static final boolean DEBUG = false;
    private static final String KEY_UPDATE_COUNT = "dynamic_update_count";
    private static final String KEY_UPDATE_FORCE = "dynamic_update_force";
    private static final String TAG = "DynamicServiceManager";
    private static final int WHAT_CONNECT = 1;
    private static final int WHAT_CONNECTED = 1;
    private static final int WHAT_CONNECT_FAIL = 2;
    private static final int WHAT_INIT_APK_LOADER = 3;
    private static final int WHAT_UPDATE = 2;
    private static DynamicServiceManager sDynamicManager;
    /* access modifiers changed from: private */
    public ApkLoader mApkLoader;
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                ConnectionInfo connectionInfo = (ConnectionInfo) message.obj;
                connectionInfo.mConn.onServiceConnected(connectionInfo.mManager);
            } else if (i == 2) {
                ((ConnectionInfo) message.obj).mConn.onServiceConnectionFail(0);
            }
        }
    };
    private HashMap<String, AbsDynamicManager<?>> mManagers = new HashMap<>();
    private Handler mWorkHandler;

    private static class ConnectionInfo {
        /* access modifiers changed from: private */
        public ServiceConnection mConn;
        /* access modifiers changed from: private */
        public AbsDynamicManager<?> mManager;
        /* access modifiers changed from: private */
        public String mService;

        private ConnectionInfo() {
        }
    }

    private class DynamicWorkHandler extends Handler {
        public DynamicWorkHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            int i = message.what;
            boolean z = true;
            if (i == 1) {
                DynamicServiceManager.this.connectWT((ConnectionInfo) message.obj);
            } else if (i == 2) {
                DynamicServiceManager dynamicServiceManager = DynamicServiceManager.this;
                if (message.arg1 != 1) {
                    z = false;
                }
                dynamicServiceManager.internalUpdateWT(z);
            } else if (i == 3) {
                DynamicServiceManager.this.mApkLoader.init();
            }
        }
    }

    private DynamicServiceManager(Context context) {
        if (Application.d().e()) {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                this.mContext = applicationContext;
            } else {
                this.mContext = context;
            }
            HandlerThread handlerThread = new HandlerThread(TAG);
            handlerThread.start();
            this.mWorkHandler = new DynamicWorkHandler(handlerThread.getLooper());
            this.mApkLoader = new ApkLoader(this.mContext, this, this.mWorkHandler);
            this.mWorkHandler.sendEmptyMessage(3);
            return;
        }
        throw new RuntimeException("Dynamic must be run remote process");
    }

    /* access modifiers changed from: private */
    public void connectWT(ConnectionInfo connectionInfo) {
        Handler handler;
        int i;
        AbsDynamicManager<?> absDynamicManager = this.mManagers.get(connectionInfo.mService);
        if (absDynamicManager == null) {
            absDynamicManager = createOrUpdateManagerWT(connectionInfo.mService, (AbsDynamicManager<?>) null);
        }
        if (absDynamicManager == null) {
            handler = this.mHandler;
            i = 2;
        } else {
            AbsDynamicManager unused = connectionInfo.mManager = absDynamicManager;
            handler = this.mHandler;
            i = 1;
        }
        handler.obtainMessage(i, connectionInfo).sendToTarget();
    }

    private AbsDynamicManager<?> createOrUpdateManagerWT(String str, AbsDynamicManager<?> absDynamicManager) {
        try {
            Context dynamicContext = this.mApkLoader.getDynamicContext();
            DynamicService dynamicService = (DynamicService) dynamicContext.getClassLoader().loadClass("com.miui.securitycenter.dynamic.Configuration").getMethod("newService", new Class[]{String.class}).invoke((Object) null, new Object[]{str});
            dynamicService.attach(dynamicContext);
            dynamicService.onCreate();
            if (absDynamicManager == null) {
                AbsDynamicManager<?> absDynamicManager2 = (AbsDynamicManager) ServiceRegistry.getServcieClass(str).newInstance();
                try {
                    this.mManagers.put(str, absDynamicManager2);
                    absDynamicManager = absDynamicManager2;
                } catch (Throwable th) {
                    th = th;
                    absDynamicManager = absDynamicManager2;
                    Log.e(TAG, "ensureDynamicManager ", th);
                    EventTrack.track(th);
                    return absDynamicManager;
                }
            }
            absDynamicManager.attach(dynamicService);
        } catch (Throwable th2) {
            th = th2;
        }
        return absDynamicManager;
    }

    public static synchronized DynamicServiceManager getInstance(Context context) {
        DynamicServiceManager dynamicServiceManager;
        synchronized (DynamicServiceManager.class) {
            if (sDynamicManager == null) {
                sDynamicManager = new DynamicServiceManager(context);
            }
            dynamicServiceManager = sDynamicManager;
        }
        return dynamicServiceManager;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x001c, code lost:
        if (r3 > 10) goto L_0x000c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void internalUpdateWT(boolean r5) {
        /*
            r4 = this;
            r0 = 0
            java.lang.String r1 = "dynamic_update_force"
            boolean r2 = com.miui.common.persistence.b.a((java.lang.String) r1, (boolean) r0)
            if (r2 != 0) goto L_0x001f
            r2 = 1
            if (r5 == 0) goto L_0x0010
        L_0x000c:
            com.miui.common.persistence.b.b((java.lang.String) r1, (boolean) r2)
            goto L_0x001f
        L_0x0010:
            java.lang.String r5 = "dynamic_update_count"
            int r3 = com.miui.common.persistence.b.a((java.lang.String) r5, (int) r0)
            int r3 = r3 + r2
            com.miui.common.persistence.b.b((java.lang.String) r5, (int) r3)
            r5 = 10
            if (r3 <= r5) goto L_0x001f
            goto L_0x000c
        L_0x001f:
            com.miui.securitycenter.dynamic.ApkLoader r5 = r4.mApkLoader
            boolean r0 = com.miui.common.persistence.b.a((java.lang.String) r1, (boolean) r0)
            r5.update(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securitycenter.dynamic.DynamicServiceManager.internalUpdateWT(boolean):void");
    }

    public static void main(Context context) {
        getInstance(context).getService(DynamicContext.APP_ACTIVITY, new ServiceConnection() {
            public void onServiceConnected(Object obj) {
                ((AppActivityManager) obj).init();
            }

            public void onServiceConnectionFail(int i) {
                Log.d(DynamicServiceManager.TAG, "onServiceConnectionFail");
            }
        });
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("DynamicServiceManager dump");
        printWriter.println("");
        this.mApkLoader.dump(fileDescriptor, printWriter, strArr);
    }

    public void getService(String str, ServiceConnection serviceConnection) {
        if (serviceConnection == null) {
            throw new IllegalArgumentException("connection is null");
        } else if (ServiceRegistry.getServcieClass(str) != null) {
            ConnectionInfo connectionInfo = new ConnectionInfo();
            String unused = connectionInfo.mService = str;
            ServiceConnection unused2 = connectionInfo.mConn = serviceConnection;
            this.mWorkHandler.obtainMessage(1, connectionInfo).sendToTarget();
        } else {
            throw new IllegalArgumentException("service not found");
        }
    }

    /* access modifiers changed from: package-private */
    public void onDynamicContextChangeWT() {
        for (Map.Entry next : this.mManagers.entrySet()) {
            AbsDynamicManager absDynamicManager = (AbsDynamicManager) next.getValue();
            ((DynamicService) absDynamicManager.getService()).performDestroy();
            createOrUpdateManagerWT((String) next.getKey(), absDynamicManager);
        }
    }

    public void track() {
        this.mApkLoader.track();
    }

    public void update(boolean z) {
        Message obtainMessage = this.mWorkHandler.obtainMessage(2);
        obtainMessage.arg1 = z ? 1 : 0;
        obtainMessage.sendToTarget();
    }
}
