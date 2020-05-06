package com.android.server;

import android.content.Context;
import android.content.Intent;
import android.miui.Manifest;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.server.am.SplitScreenReporter;
import android.util.Log;
import com.android.internal.net.IOemNetd;
import com.android.internal.net.IOemNetdUnsolicitedEventListener;
import com.android.server.wm.ActivityStackSupervisorInjector;
import java.util.HashSet;
import java.util.Set;

public class MiuiNetworkManagementService {
    private static final int MIUI_FIREWALL_RESPONSE_CODE = 699;
    private static final int POWER_SAVE_IDLETIMER_LABEL = 118;
    private static final String TAG = "NetworkManagement";
    private static MiuiNetworkManagementService sInstance;
    /* access modifiers changed from: private */
    public final Context mContext;
    private Set<Integer> mListenedIdleTimerType = new HashSet();
    private OemNetdUnsolicitedEventListener mListenter;
    private IOemNetd mNetd;
    private NetworkEventObserver mObserver;

    public interface NetworkEventObserver {
        void uidDataActivityChanged(String str, int i, boolean z, long j);
    }

    static synchronized MiuiNetworkManagementService Init(Context context) {
        MiuiNetworkManagementService miuiNetworkManagementService;
        synchronized (MiuiNetworkManagementService.class) {
            sInstance = new MiuiNetworkManagementService(context);
            miuiNetworkManagementService = sInstance;
        }
        return miuiNetworkManagementService;
    }

    public static synchronized MiuiNetworkManagementService getInstance() {
        MiuiNetworkManagementService miuiNetworkManagementService;
        synchronized (MiuiNetworkManagementService.class) {
            miuiNetworkManagementService = sInstance;
        }
        return miuiNetworkManagementService;
    }

    private MiuiNetworkManagementService(Context context) {
        this.mContext = context;
        this.mListenedIdleTimerType.add(1);
    }

    /* access modifiers changed from: protected */
    public void setOemNetd(IBinder ib) throws RemoteException {
        this.mNetd = IOemNetd.Stub.asInterface(ib);
        this.mListenter = new OemNetdUnsolicitedEventListener();
        this.mNetd.registerOemUnsolicitedEventListener(this.mListenter);
    }

    public boolean enableWmmer(boolean enabled) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetd.enableWmmer(enabled);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("enableWmmer");
        }
    }

    public boolean enableLimitter(boolean enabled) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetd.enableLimitter(enabled);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("enableLimitter");
        }
    }

    public boolean updateWmm(int uid, int wmm) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetd.updateWmm(uid, wmm);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("updateWmm");
        }
    }

    public boolean whiteListUid(int uid, boolean add) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetd.whiteListUid(uid, add);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("whiteListUid");
        }
    }

    public boolean setLimit(boolean enabled, long rate) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetd.setLimit(enabled, rate);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("setLimit");
        }
    }

    public boolean enableIptablesRestore(boolean enabled) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetd.enableIptablesRestore(enabled);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("enableIptablesRestore");
        }
    }

    public boolean listenUidDataActivity(int protocol, int uid, int type, int timeout, boolean listen) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            boolean res = this.mNetd.listenUidDataActivity(protocol, uid, type, timeout, listen);
            if (listen) {
                this.mListenedIdleTimerType.add(Integer.valueOf(type));
            } else {
                this.mListenedIdleTimerType.remove(Integer.valueOf(type));
            }
            return res;
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("enableIptablesRestore");
        }
    }

    public boolean updateIface(String iface) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            boolean res = this.mNetd.updateIface(iface);
            if (iface.startsWith("wlan")) {
                this.mListenedIdleTimerType.add(1);
            }
            return res;
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("enableIptablesRestore");
        }
    }

    public boolean addMiuiFirewallSharedUid(int uid) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetd.addMiuiFirewallSharedUid(uid);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("addMiuiFirewallSharedUid");
        }
    }

    public void setPidForPackage(String packageName, int pid, int uid) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetd.setPidForPackage(packageName, pid, uid);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("setPidForPackage");
        }
    }

    public boolean setMiuiFirewallRule(String packageName, int uid, int rule, int type) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetd.setMiuiFirewallRule(packageName, uid, rule, type);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("setMiuiFirewallRule");
        }
    }

    public void setGmsBlockerEnable(int uid, boolean enable) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            this.mNetd.setGmsBlockerEnable(uid, enable);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("setGmsBlockerEnable");
        }
    }

    public boolean initGmsChain(String name, int uid, String rule) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetd.initGmsChain(name, uid, rule);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("initGmsChain");
        }
    }

    public boolean setGmsChainState(String name, boolean enable) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetd.setGmsChainState(name, enable);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("setGmsChainState");
        }
    }

    public boolean setCurrentNetworkState(int state) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetd.setCurrentNetworkState(state);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("setCurrentNetworkState");
        }
    }

    public boolean enableRps(String iface, boolean enable) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetd.enableRps(iface, enable);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("enableRps");
        }
    }

    public void setNetworkEventObserver(NetworkEventObserver observer) {
        this.mObserver = observer;
    }

    /* access modifiers changed from: package-private */
    public boolean filterExtendEvent(int code, String raw, String[] cooked) {
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean miuiNotifyInterfaceClassActivity(int type, boolean isActive, long tsNanos, int uid, boolean fromRadio) {
        String lable;
        if (!this.mListenedIdleTimerType.contains(Integer.valueOf(type)) || (lable = convertTypeToLable(type)) == null) {
            return false;
        }
        Log.d(TAG, lable + " <====> " + type);
        NetworkEventObserver networkEventObserver = this.mObserver;
        if (networkEventObserver == null) {
            return true;
        }
        networkEventObserver.uidDataActivityChanged(lable, uid, isActive, tsNanos);
        return true;
    }

    private String convertTypeToLable(int type) {
        if (type == 1) {
            return SystemProperties.get("wifi.interface", "wlan0");
        }
        if (type > 118) {
            return String.valueOf(type);
        }
        return null;
    }

    private class OemNetdUnsolicitedEventListener extends IOemNetdUnsolicitedEventListener.Stub {
        private OemNetdUnsolicitedEventListener() {
        }

        public void onRegistered() throws RemoteException {
            Log.d(MiuiNetworkManagementService.TAG, "onRegistered");
        }

        public void onFirewallBlocked(int code, String packageName) throws RemoteException {
            Log.d(MiuiNetworkManagementService.TAG, String.format("code=%d, pkg=%s", new Object[]{Integer.valueOf(code), packageName}));
            if (MiuiNetworkManagementService.MIUI_FIREWALL_RESPONSE_CODE == code && packageName != null) {
                Intent intent = new Intent("miui.intent.action.FIREWALL");
                intent.setPackage(ActivityStackSupervisorInjector.MIUI_APP_LOCK_PACKAGE_NAME);
                intent.putExtra(SplitScreenReporter.STR_PKG, packageName);
                MiuiNetworkManagementService.this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, Manifest.permission.FIREWALL);
            }
        }
    }

    public boolean enableQos(boolean enabled) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetd.enableQos(enabled);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("enableQos");
        }
    }

    public boolean setQos(int protocol, int uid, int tos, boolean add) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
        try {
            return this.mNetd.setQos(protocol, uid, tos, add);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException("setQos");
        }
    }
}
