package com.miui.networkassistant.service.wrapper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import b.b.c.c.a.a;
import b.b.c.j.B;
import b.b.c.j.g;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.service.IAppMonitorBinder;
import com.miui.networkassistant.service.IAppMonitorBinderListener;
import com.miui.networkassistant.service.ITrafficManageBinder;
import com.miui.networkassistant.service.tm.TrafficManageService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AppMonitorWrapper {
    private static final String TAG = "AppMonitorWrapper";
    private static AppMonitorWrapper sInstance;
    /* access modifiers changed from: private */
    public IAppMonitorBinder mAppMonitorBinder = null;
    /* access modifiers changed from: private */
    public IAppMonitorBinderListener.Stub mAppMonitorBinderListener = new IAppMonitorBinderListener.Stub() {
        public void onAppListUpdated() {
            boolean unused = AppMonitorWrapper.this.mIsInited = true;
            AppMonitorWrapper.this.broadcastAppListUpdated();
        }
    };
    private Context mContext;
    /* access modifiers changed from: private */
    public boolean mIsInited = false;
    private ArrayList<AppMonitorListener> mListeners = new ArrayList<>();
    /* access modifiers changed from: private */
    public ITrafficManageBinder mTrafficManageBinder = null;
    private ServiceConnection mTrafficManageServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ITrafficManageBinder unused = AppMonitorWrapper.this.mTrafficManageBinder = ITrafficManageBinder.Stub.asInterface(iBinder);
            try {
                IAppMonitorBinder unused2 = AppMonitorWrapper.this.mAppMonitorBinder = AppMonitorWrapper.this.mTrafficManageBinder.getAppMonitorBinder();
                AppMonitorWrapper.this.mAppMonitorBinder.registerLisener(AppMonitorWrapper.this.mAppMonitorBinderListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            ITrafficManageBinder unused = AppMonitorWrapper.this.mTrafficManageBinder = null;
            IAppMonitorBinder unused2 = AppMonitorWrapper.this.mAppMonitorBinder = null;
        }
    };

    public interface AppMonitorListener {
        void onAppListUpdated();
    }

    private AppMonitorWrapper(Context context) {
        Log.i(TAG, "mina MonitorCenter created");
        this.mContext = context.getApplicationContext();
        bindTrafficManageService();
    }

    private void bindTrafficManageService() {
        Context context = this.mContext;
        g.a(context, new Intent(context, TrafficManageService.class), this.mTrafficManageServiceConnection, 1, B.k());
    }

    /* access modifiers changed from: private */
    public void broadcastAppListUpdated() {
        synchronized (this.mListeners) {
            Iterator<AppMonitorListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onAppListUpdated();
            }
        }
    }

    public static synchronized AppMonitorWrapper getInstance(Context context) {
        AppMonitorWrapper appMonitorWrapper;
        synchronized (AppMonitorWrapper.class) {
            if (sInstance == null) {
                sInstance = new AppMonitorWrapper(context);
            }
            appMonitorWrapper = sInstance;
        }
        return appMonitorWrapper;
    }

    private void unBindTrafficManageService() {
        IAppMonitorBinder iAppMonitorBinder = this.mAppMonitorBinder;
        if (iAppMonitorBinder != null) {
            try {
                iAppMonitorBinder.unRegisterLisener(this.mAppMonitorBinderListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        this.mContext.unbindService(this.mTrafficManageServiceConnection);
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        super.finalize();
        unBindTrafficManageService();
    }

    public AppInfo getAppInfoByPackageName(String str) {
        if (!this.mIsInited) {
            return null;
        }
        try {
            return this.mAppMonitorBinder.getAppInfoByPackageName(str);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<AppInfo> getFilteredAppInfosList() {
        if (!this.mIsInited) {
            return null;
        }
        try {
            return (ArrayList) this.mAppMonitorBinder.getFilteredAppInfosList();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<AppInfo> getNetworkAccessedAppList() {
        if (!this.mIsInited) {
            return null;
        }
        try {
            return (ArrayList) this.mAppMonitorBinder.getNetworkAccessedAppList();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HashMap<String, AppInfo> getNetworkAccessedAppsMap() {
        if (!this.mIsInited) {
            return null;
        }
        try {
            return (HashMap) this.mAppMonitorBinder.getNetworkAccessedAppsMap();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<AppInfo> getNonSystemAppList() {
        if (!this.mIsInited) {
            return null;
        }
        try {
            return (ArrayList) this.mAppMonitorBinder.getNonSystemAppList();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<AppInfo> getSystemAppList() {
        if (!this.mIsInited) {
            return null;
        }
        try {
            return (ArrayList) this.mAppMonitorBinder.getSystemAppList();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void registerLisener(final AppMonitorListener appMonitorListener) {
        synchronized (this.mListeners) {
            if (appMonitorListener != null) {
                if (!this.mListeners.contains(appMonitorListener)) {
                    this.mListeners.add(appMonitorListener);
                    if (this.mIsInited) {
                        a.a(new Runnable() {
                            public void run() {
                                appMonitorListener.onAppListUpdated();
                            }
                        });
                    }
                }
            }
        }
    }

    public void unRegisterLisener(AppMonitorListener appMonitorListener) {
        synchronized (this.mListeners) {
            if (appMonitorListener != null) {
                if (this.mListeners.contains(appMonitorListener)) {
                    this.mListeners.remove(appMonitorListener);
                }
            }
        }
    }
}
