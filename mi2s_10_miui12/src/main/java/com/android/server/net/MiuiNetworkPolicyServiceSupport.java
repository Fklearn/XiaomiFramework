package com.android.server.net;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.IUidObserver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.wifi.MiuiWifiManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;
import com.android.server.MiuiNetworkManagementService;

public class MiuiNetworkPolicyServiceSupport {
    private static final boolean DEBUG = false;
    private static final String TAG = "MiuiNetworkPolicySupport";
    private final IActivityManager mActivityManager;
    private final Context mContext;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private MiuiWifiManager mMiuiWifiManager;
    private final IUidObserver mUidObserver = new IUidObserver.Stub() {
        public void onUidStateChanged(int uid, int procState, long procStateSeq) throws RemoteException {
            MiuiNetworkPolicyServiceSupport.this.mHandler.sendMessage(MiuiNetworkPolicyServiceSupport.this.mHandler.obtainMessage(1, uid, procState));
        }

        public void onUidGone(int uid, boolean disabled) throws RemoteException {
            MiuiNetworkPolicyServiceSupport.this.mHandler.sendMessage(MiuiNetworkPolicyServiceSupport.this.mHandler.obtainMessage(2, uid, 0));
        }

        public void onUidActive(int uid) throws RemoteException {
        }

        public void onUidIdle(int uid, boolean disabled) throws RemoteException {
        }

        public void onUidCachedChanged(int uid, boolean cached) {
        }
    };

    public MiuiNetworkPolicyServiceSupport(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        this.mActivityManager = ActivityManagerNative.getDefault();
    }

    public void registerUidObserver() {
        try {
            this.mActivityManager.registerUidObserver(this.mUidObserver, 3, -1, (String) null);
        } catch (RemoteException e) {
        }
    }

    public void enablePowerSave(boolean enabled) {
        if (this.mMiuiWifiManager == null) {
            this.mMiuiWifiManager = MiuiWifiManager.getInstance(this.mContext);
        }
        this.mMiuiWifiManager.enablePowerSave(enabled);
    }

    public String updateIface(String iface) {
        String newIface;
        LinkProperties lp = ((ConnectivityManager) this.mContext.getSystemService("connectivity")).getLinkProperties(((WifiManager) this.mContext.getSystemService("wifi")).getCurrentNetwork());
        if (lp == null || (newIface = lp.getInterfaceName()) == null || TextUtils.equals(iface, newIface)) {
            return iface;
        }
        MiuiNetworkManagementService.getInstance().updateIface(newIface);
        return newIface;
    }
}
