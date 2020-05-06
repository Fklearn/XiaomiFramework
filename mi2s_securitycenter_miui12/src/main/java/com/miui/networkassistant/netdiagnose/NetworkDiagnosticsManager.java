package com.miui.networkassistant.netdiagnose;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.h.f;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils;
import com.miui.networkassistant.utils.TelephonyUtil;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class NetworkDiagnosticsManager {
    private static final String TAG = "NA_ND_Manager";
    private static NetworkDiagnosticsManager mNetworkDiagnosticManager;
    /* access modifiers changed from: private */
    public Context mContext;
    private NetworkDiagnosticsUtils.NetworkState mCurNetworkState;
    private int mCurrentDataRat;
    private int mCurrentLteRsrq;
    private int mCurrentSignalStrength;
    private String mDiagnosingNetworkInterface;
    private int mDiagnosingNetworkType;
    private boolean mIsInternetByUsbShareNetEnable;
    private String[] mServers = new String[2];

    class CheckNetworkStateTask implements Callable<NetworkDiagnosticsUtils.NetworkState> {
        String[] mServers;

        public CheckNetworkStateTask(String[] strArr) {
            this.mServers = strArr;
        }

        public NetworkDiagnosticsUtils.NetworkState call() {
            Log.i(NetworkDiagnosticsManager.TAG, "CheckNetworkStateTask call.");
            NetworkDiagnosticsUtils.NetworkState networkState = NetworkDiagnosticsUtils.NetworkState.UNKNOWN;
            int i = 0;
            while (i < this.mServers.length) {
                try {
                    String str = this.mServers[i];
                    if (!TextUtils.isEmpty(str)) {
                        networkState = NetworkDiagnosticsUtils.CheckNetworkState(NetworkDiagnosticsManager.this.mContext, str);
                        if (networkState == NetworkDiagnosticsUtils.NetworkState.CONNECTED || networkState == NetworkDiagnosticsUtils.NetworkState.CAPTIVEPORTAL) {
                            break;
                        }
                    }
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return networkState;
        }
    }

    class reopenTask implements Callable<Boolean> {
        long mTimeoutSecs;

        public reopenTask(long j) {
            this.mTimeoutSecs = j;
            if (this.mTimeoutSecs > 10) {
                this.mTimeoutSecs = 10;
            }
        }

        public Boolean call() {
            WifiManager wifiManager = (WifiManager) NetworkDiagnosticsManager.this.mContext.getApplicationContext().getSystemService("wifi");
            boolean z = false;
            if (wifiManager == null) {
                return false;
            }
            wifiManager.setWifiEnabled(false);
            int i = 0;
            while (true) {
                if (((long) i) >= this.mTimeoutSecs) {
                    break;
                }
                Thread.sleep(1000);
                if (wifiManager.getWifiState() == 1) {
                    wifiManager.setWifiEnabled(true);
                    z = true;
                    break;
                }
                i++;
            }
            if (!z) {
                wifiManager.setWifiEnabled(true);
            }
            Thread.sleep(DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            return true;
        }
    }

    public NetworkDiagnosticsManager(Context context) {
        this.mContext = context;
        this.mServers[0] = NetworkDiagnosticsUtils.getCaptivePortalServer(this.mContext);
        this.mServers[1] = NetworkDiagnosticsUtils.getDefaultCaptivePortalServer();
        this.mDiagnosingNetworkType = -1;
        this.mCurNetworkState = NetworkDiagnosticsUtils.NetworkState.UNKNOWN;
    }

    public static synchronized NetworkDiagnosticsManager getInstance(Context context) {
        NetworkDiagnosticsManager networkDiagnosticsManager;
        synchronized (NetworkDiagnosticsManager.class) {
            if (mNetworkDiagnosticManager == null) {
                mNetworkDiagnosticManager = new NetworkDiagnosticsManager(context.getApplicationContext());
            }
            networkDiagnosticsManager = mNetworkDiagnosticManager;
        }
        return networkDiagnosticsManager;
    }

    public NetworkDiagnosticsUtils.NetworkState checkNetworkState() {
        NetworkDiagnosticsUtils.NetworkState networkState = NetworkDiagnosticsUtils.NetworkState.UNKNOWN;
        try {
            CheckNetworkStateTask checkNetworkStateTask = new CheckNetworkStateTask(this.mServers);
            ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
            List invokeAll = newSingleThreadExecutor.invokeAll(Arrays.asList(new Callable[]{checkNetworkStateTask}));
            newSingleThreadExecutor.shutdown();
            Future future = (Future) invokeAll.get(0);
            if (!future.isCancelled()) {
                return (NetworkDiagnosticsUtils.NetworkState) future.get();
            }
            Log.d(TAG, "checkNetworkState isCancelled......task=." + checkNetworkStateTask);
            return NetworkDiagnosticsUtils.NetworkState.CANCELLED;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return networkState;
        } catch (ExecutionException e2) {
            e2.printStackTrace();
            return networkState;
        }
    }

    public boolean checkWlanConnected() {
        return f.l(this.mContext);
    }

    public NetworkInfo.DetailedState getActiveNetworkState() {
        return ((ConnectivityManager) this.mContext.getSystemService("connectivity")).getNetworkInfo(getActiveNetworkType()).getDetailedState();
    }

    public int getActiveNetworkType() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.mContext.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            return activeNetworkInfo.getType();
        }
        return -1;
    }

    public NetworkDiagnosticsUtils.NetworkState getCurNetworkState() {
        return this.mCurNetworkState;
    }

    public int getCurrentDataRat() {
        return this.mCurrentDataRat;
    }

    public int getCurrentLteRsrq() {
        return this.mCurrentLteRsrq;
    }

    public int getCurrentSignalStrength() {
        return this.mCurrentSignalStrength;
    }

    public String getDiagnosingNetworkInterface() {
        return this.mDiagnosingNetworkInterface;
    }

    public int getDiagnosingNetworkType() {
        return this.mDiagnosingNetworkType;
    }

    public boolean isInternetByUsbshareNetEnable() {
        return this.mIsInternetByUsbShareNetEnable;
    }

    public boolean isMobileDataEnable() {
        return f.e(this.mContext) && !TelephonyUtil.isAirModeOn(this.mContext);
    }

    public boolean isWifiEnable() {
        return ((WifiManager) this.mContext.getSystemService("wifi")).isWifiEnabled();
    }

    public Boolean reopenWifi() {
        try {
            reopenTask reopentask = new reopenTask(5);
            ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
            List invokeAll = newSingleThreadExecutor.invokeAll(Arrays.asList(new Callable[]{reopentask}), 10000, TimeUnit.MILLISECONDS);
            newSingleThreadExecutor.shutdown();
            Future future = (Future) invokeAll.get(0);
            if (future.isCancelled()) {
                return false;
            }
            return (Boolean) future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setCurNetworkState(NetworkDiagnosticsUtils.NetworkState networkState) {
        this.mCurNetworkState = networkState;
    }

    public void setCurrentDataRat(int i) {
        this.mCurrentDataRat = i;
    }

    public void setCurrentLteRsrq(int i) {
        this.mCurrentLteRsrq = i;
    }

    public void setCurrentSignalStrength(int i) {
        this.mCurrentSignalStrength = i;
    }

    public void setDiagnosingNetworkInterface(String str) {
        this.mDiagnosingNetworkInterface = str;
    }

    public void setDiagnosingNetworkType(int i) {
        this.mDiagnosingNetworkType = i;
    }

    public void setInternetByUsbShareNetEnable(boolean z) {
        this.mIsInternetByUsbShareNetEnable = z;
    }
}
