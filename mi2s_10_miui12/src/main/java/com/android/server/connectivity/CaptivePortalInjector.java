package com.android.server.connectivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.IMiuiCaptivePortal;
import android.net.INetworkMonitor;
import android.net.Network;
import android.net.NetworkMonitorManager;
import android.os.Binder;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import com.android.server.MiuiConfigCaptivePortal;

public class CaptivePortalInjector {
    private static final String TAG = "CaptivePortalInjector";
    private static final int TYPE_SLAVE_WIFI = 30;

    public static class NetworkAgentInfoInner {
        /* access modifiers changed from: private */
        public static boolean mExplicitlySelected;
        /* access modifiers changed from: private */
        public static Object mNM;
        /* access modifiers changed from: private */
        public Network mNetwork;
        /* access modifiers changed from: private */
        public int mNetworkType;

        public NetworkAgentInfoInner(NetworkAgentInfo nai) {
            mNM = nai.networkMonitor();
            this.mNetworkType = nai.networkInfo.getType();
            this.mNetwork = nai.network;
            mExplicitlySelected = nai.networkMisc.explicitlySelected;
        }

        public static void setNetworkMonitor(Object obj) {
            mNM = obj;
        }

        public static void setExplicitlySelected(boolean explicitlySelected) {
            mExplicitlySelected = explicitlySelected;
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [com.android.server.connectivity.CaptivePortalInjector$1, android.os.IBinder] */
    public static final PendingIntent getCaptivePortalPendingIntent(Context context, PendingIntent pi, final NetworkAgentInfoInner naii) {
        boolean isSupportDualWifi = supportDualWifi(context);
        if (naii == null) {
            return pi;
        }
        boolean z = true;
        if ((naii.mNetworkType != 1 && (!isSupportDualWifi || naii.mNetworkType != 30)) || pi == null || pi.getIntent() == null) {
            return pi;
        }
        Intent intent = pi.getIntent();
        intent.putExtra("miui.intent.extra.CAPTIVE_PORTAL", new IMiuiCaptivePortal.Stub() {
            public void appResponse(int response) {
                NetworkAgentInfoInner networkAgentInfoInner = NetworkAgentInfoInner.this;
                if (NetworkAgentInfoInner.mNM instanceof INetworkMonitor) {
                    long token2 = Binder.clearCallingIdentity();
                    try {
                        NetworkAgentInfoInner networkAgentInfoInner2 = NetworkAgentInfoInner.this;
                        ((INetworkMonitor) NetworkAgentInfoInner.mNM).notifyCaptivePortalAppFinished(response);
                    } catch (RemoteException e) {
                        Log.d(CaptivePortalInjector.TAG, "notifyCaptivePortalAppFinished failure");
                        e.printStackTrace();
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(token2);
                        throw th;
                    }
                    Binder.restoreCallingIdentity(token2);
                    return;
                }
                NetworkAgentInfoInner networkAgentInfoInner3 = NetworkAgentInfoInner.this;
                if (NetworkAgentInfoInner.mNM instanceof NetworkMonitorManager) {
                    NetworkAgentInfoInner networkAgentInfoInner4 = NetworkAgentInfoInner.this;
                    ((NetworkMonitorManager) NetworkAgentInfoInner.mNM).notifyCaptivePortalAppFinished(response);
                    return;
                }
                Log.e(CaptivePortalInjector.TAG, "Unknown type of nai.networkMonitor()");
            }
        });
        intent.putExtra("miui.intent.extra.NETWORK", naii.mNetwork);
        intent.putExtra("miui.intent.extra.EXPLICIT_SELECTED", NetworkAgentInfoInner.mExplicitlySelected);
        if (isSupportDualWifi) {
            if (naii.mNetworkType != 30) {
                z = false;
            }
            intent.putExtra("miui.intent.extra.IS_SLAVE", z);
        }
        if (pi.isActivity()) {
            return PendingIntent.getActivity(context, 0, intent, 134217728);
        }
        return PendingIntent.getBroadcast(context, 0, intent, 134217728);
    }

    static final boolean enableDataAndWifiRoam(Context context) {
        return MiuiConfigCaptivePortal.enableDataAndWifiRoam(context);
    }

    private static boolean supportDualWifi(Context context) {
        boolean support = false;
        if (context == null) {
            return false;
        }
        if ("off".equals(Settings.System.getString(context.getContentResolver(), "cloud_slave_wifi_support"))) {
            support = false;
        } else if (SystemProperties.getInt("ro.vendor.net.enable_dual_wifi", 0) == 1) {
            support = true;
        }
        Log.d(TAG, "supportDualWifi:" + support);
        return support;
    }
}
