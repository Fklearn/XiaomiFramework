package com.miui.networkassistant.service.wrapper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import b.b.c.c.a.a;
import b.b.c.j.B;
import b.b.c.j.g;
import java.util.ArrayList;
import java.util.Iterator;

public class TmBinderCacher {
    private static final String TAG = "TmBinderCacher";
    private static TmBinderCacher sInstance;
    /* access modifiers changed from: private */
    public IBinder mBinder = null;
    /* access modifiers changed from: private */
    public Object mBinderAndConnLock = new Object();
    /* access modifiers changed from: private */
    public ComponentName mComponentName = null;
    private ServiceConnection mConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TmBinderCacher.TAG, "onServiceConnected name=" + componentName);
            synchronized (TmBinderCacher.this.mBinderAndConnLock) {
                ComponentName unused = TmBinderCacher.this.mComponentName = componentName;
                IBinder unused2 = TmBinderCacher.this.mBinder = iBinder;
                synchronized (TmBinderCacher.this.mConnections) {
                    Iterator it = TmBinderCacher.this.mConnections.iterator();
                    while (it.hasNext()) {
                        ((ServiceConnection) it.next()).onServiceConnected(TmBinderCacher.this.mComponentName, TmBinderCacher.this.mBinder);
                    }
                }
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TmBinderCacher.TAG, "onServiceDisconnected name=" + componentName);
            synchronized (TmBinderCacher.this.mBinderAndConnLock) {
                TmBinderCacher.this.bindTrafficManageService();
            }
        }
    };
    /* access modifiers changed from: private */
    public ArrayList<ServiceConnection> mConnections = new ArrayList<>();
    private Context mContext = null;

    private TmBinderCacher(Context context) {
        this.mContext = context;
        bindTrafficManageService();
    }

    /* access modifiers changed from: private */
    public void bindTrafficManageService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.networkassistant.service.tm.TrafficManageService"));
        g.a(this.mContext, intent, this.mConn, 1, B.k());
    }

    public static synchronized TmBinderCacher getInstance() {
        TmBinderCacher tmBinderCacher;
        synchronized (TmBinderCacher.class) {
            if (sInstance != null) {
                tmBinderCacher = sInstance;
            } else {
                throw new RuntimeException("Please invoke init before call getInstance");
            }
        }
        return tmBinderCacher;
    }

    public static synchronized void initForUIProcess(Context context) {
        synchronized (TmBinderCacher.class) {
            if (sInstance == null) {
                sInstance = new TmBinderCacher(context);
            }
        }
    }

    private void unBindTrafficManageService() {
        this.mContext.unbindService(this.mConn);
    }

    public void bindTmService(final ServiceConnection serviceConnection) {
        if (serviceConnection != null) {
            synchronized (this.mBinderAndConnLock) {
                if (this.mBinder != null) {
                    a.a(new Runnable() {
                        public void run() {
                            serviceConnection.onServiceConnected(TmBinderCacher.this.mComponentName, TmBinderCacher.this.mBinder);
                        }
                    });
                }
                synchronized (this.mConnections) {
                    this.mConnections.add(serviceConnection);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        super.finalize();
        unBindTrafficManageService();
    }

    public void unbindTmService(ServiceConnection serviceConnection) {
        if (serviceConnection != null) {
            synchronized (this.mConnections) {
                if (this.mConnections.contains(serviceConnection)) {
                    this.mConnections.remove(serviceConnection);
                }
            }
        }
    }
}
