package com.miui.networkassistant.service.tm;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.h.f;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils;
import com.miui.networkassistant.utils.NotificationUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import java.net.HttpURLConnection;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.provider.ExtraNetwork;

public class NetworkCheckStateManager {
    private static final int MSG_NETWORK_BLOCKED = 2;
    private static final int MSG_NETWORK_CONNECTED = 1;
    private static final int MSG_NETWORK_DIAGNOSTICS_RESULT = 16;
    private static final long NETWORK_CHECK_INTERVAL = 300000;
    private static final String TAG = "NetworkDiagnostics_CheckStateManager";
    private static final String XM_SF_PACKAGE_NAME = "com.xiaomi.xmsf";
    /* access modifiers changed from: private */
    public int mCallState = 0;
    private CheckNetworkThread mCheckNetworkThread;
    private f.a mCheckingNetworkType;
    private CommonConfig mCommonConfig;
    /* access modifiers changed from: private */
    public Context mContext;
    private String mCurNetworkInterface = null;
    private NetworkDiagnosticsUtils.NetworkState mCurNetworkState;
    private f.a mCurNetworkType = f.a.Inited;
    Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                NetworkCheckStateManager.this.networkConnected();
            } else if (i != 2) {
                if (i == 16) {
                    NetworkCheckStateManager.this.onDiagnosticsResult(NetworkDiagnosticsUtils.NetworkState.values()[message.arg1]);
                }
            } else if (NetworkCheckStateManager.this.mNeedCheckedInBackground.get()) {
                NetworkCheckStateManager networkCheckStateManager = NetworkCheckStateManager.this;
                new CheckNetworkLooper(networkCheckStateManager.mServers).start();
            }
        }
    };
    /* access modifiers changed from: private */
    public AtomicBoolean mNeedCheckedInBackground;
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int i, String str) {
            super.onCallStateChanged(i, str);
            int unused = NetworkCheckStateManager.this.mCallState = i;
        }
    };
    /* access modifiers changed from: private */
    public String[] mServers;
    private int mSignalStrength = 4;
    /* access modifiers changed from: private */
    public SimCardHelper mSimCardHelper;

    /* renamed from: com.miui.networkassistant.service.tm.NetworkCheckStateManager$3  reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$networkassistant$netdiagnose$NetworkDiagnosticsUtils$NetworkState = new int[NetworkDiagnosticsUtils.NetworkState.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(12:0|1|2|3|4|5|6|7|8|9|10|12) */
        /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0035 */
        static {
            /*
                com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState[] r0 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$networkassistant$netdiagnose$NetworkDiagnosticsUtils$NetworkState = r0
                int[] r0 = $SwitchMap$com$miui$networkassistant$netdiagnose$NetworkDiagnosticsUtils$NetworkState     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState r1 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.UNKNOWN     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$networkassistant$netdiagnose$NetworkDiagnosticsUtils$NetworkState     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState r1 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.CANCELLED     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$networkassistant$netdiagnose$NetworkDiagnosticsUtils$NetworkState     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState r1 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.CONNECTED     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$com$miui$networkassistant$netdiagnose$NetworkDiagnosticsUtils$NetworkState     // Catch:{ NoSuchFieldError -> 0x0035 }
                com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState r1 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.CAPTIVEPORTAL     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                int[] r0 = $SwitchMap$com$miui$networkassistant$netdiagnose$NetworkDiagnosticsUtils$NetworkState     // Catch:{ NoSuchFieldError -> 0x0040 }
                com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState r1 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.BLOCKED     // Catch:{ NoSuchFieldError -> 0x0040 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0040 }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0040 }
            L_0x0040:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.service.tm.NetworkCheckStateManager.AnonymousClass3.<clinit>():void");
        }
    }

    private class CheckNetworkLooper extends Thread {
        private String[] mServers;
        private NetworkDiagnosticsUtils.NetworkState ret = NetworkDiagnosticsUtils.NetworkState.UNKNOWN;
        private HttpURLConnection urlConnection = null;

        CheckNetworkLooper(String[] strArr) {
            this.mServers = strArr;
        }

        public void run() {
            int i = 0;
            while (true) {
                String[] strArr = this.mServers;
                if (i >= strArr.length) {
                    break;
                }
                String str = strArr[i];
                Log.i(NetworkCheckStateManager.TAG, "CheckNetworkLooper url=" + str);
                if (!TextUtils.isEmpty(str)) {
                    this.ret = NetworkDiagnosticsUtils.CheckNetworkState(NetworkCheckStateManager.this.mContext, str);
                    Log.i(NetworkCheckStateManager.TAG, "CheckNetworkLooper ret= " + this.ret);
                    NetworkDiagnosticsUtils.NetworkState networkState = this.ret;
                    if (networkState == NetworkDiagnosticsUtils.NetworkState.CONNECTED || networkState == NetworkDiagnosticsUtils.NetworkState.CAPTIVEPORTAL) {
                        break;
                    }
                }
                i++;
            }
            NetworkDiagnosticsUtils.NetworkState networkState2 = this.ret;
            if (networkState2 == NetworkDiagnosticsUtils.NetworkState.CONNECTED || networkState2 == NetworkDiagnosticsUtils.NetworkState.CAPTIVEPORTAL) {
                NetworkCheckStateManager.this.mHandler.sendMessage(NetworkCheckStateManager.this.mHandler.obtainMessage(1));
            } else if (!NetworkCheckStateManager.this.mHandler.hasMessages(2)) {
                NetworkCheckStateManager.this.mHandler.sendMessageDelayed(NetworkCheckStateManager.this.mHandler.obtainMessage(2), 300000);
            }
        }
    }

    private class CheckNetworkThread extends Thread {
        private AtomicBoolean mCancelled;
        private String[] mServers;
        private NetworkDiagnosticsUtils.NetworkState ret = NetworkDiagnosticsUtils.NetworkState.UNKNOWN;
        private HttpURLConnection urlConnection = null;

        CheckNetworkThread(String[] strArr) {
            this.mServers = strArr;
            this.mCancelled = new AtomicBoolean();
            this.mCancelled.set(false);
        }

        public void cancel() {
            this.mCancelled.set(true);
        }

        public void run() {
            Log.i(NetworkCheckStateManager.TAG, "CheckNetworkThread run.");
            try {
                if (f.i(NetworkCheckStateManager.this.mContext)) {
                    if (ExtraNetwork.isMobileRestrict(NetworkCheckStateManager.this.mContext, NetworkCheckStateManager.this.mContext.getPackageName())) {
                        this.ret = NetworkDiagnosticsUtils.NetworkState.CONNECTED;
                        Log.i(NetworkCheckStateManager.TAG, "networkassistant not have permission to access network!");
                    }
                    if (TelephonyUtil.isNetworkRoaming(NetworkCheckStateManager.this.mContext, NetworkCheckStateManager.this.mSimCardHelper.getCurrentMobileSlotNum())) {
                        this.ret = NetworkDiagnosticsUtils.NetworkState.CONNECTED;
                        Log.i(NetworkCheckStateManager.TAG, "network roaming!!");
                    }
                } else if (f.l(NetworkCheckStateManager.this.mContext)) {
                    this.ret = NetworkDiagnosticsUtils.NetworkState.CONNECTED;
                }
                if (this.ret == NetworkDiagnosticsUtils.NetworkState.UNKNOWN) {
                    int i = 0;
                    while (true) {
                        if (i >= this.mServers.length) {
                            break;
                        }
                        String str = this.mServers[i];
                        Log.i(NetworkCheckStateManager.TAG, "CheckNetworkThread url=" + str);
                        if (this.mCancelled.get()) {
                            this.ret = NetworkDiagnosticsUtils.NetworkState.CANCELLED;
                            break;
                        }
                        if (!TextUtils.isEmpty(str)) {
                            this.ret = NetworkDiagnosticsUtils.CheckNetworkState(NetworkCheckStateManager.this.mContext, str);
                            Log.i(NetworkCheckStateManager.TAG, "CheckNetworkThread ret= " + this.ret);
                            if (this.ret == NetworkDiagnosticsUtils.NetworkState.CONNECTED) {
                                break;
                            } else if (this.ret == NetworkDiagnosticsUtils.NetworkState.CAPTIVEPORTAL) {
                                break;
                            }
                        }
                        i++;
                    }
                }
                if (this.mCancelled.get()) {
                    this.ret = NetworkDiagnosticsUtils.NetworkState.CANCELLED;
                }
                Message obtainMessage = NetworkCheckStateManager.this.mHandler.obtainMessage(16);
                obtainMessage.arg1 = this.ret.ordinal();
                NetworkCheckStateManager.this.mHandler.sendMessage(obtainMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    NetworkCheckStateManager(Context context) {
        this.mContext = context;
        this.mSimCardHelper = SimCardHelper.getInstance(this.mContext);
        this.mCheckNetworkThread = null;
        this.mCurNetworkState = NetworkDiagnosticsUtils.NetworkState.UNKNOWN;
        this.mServers = new String[2];
        this.mServers[0] = NetworkDiagnosticsUtils.getCaptivePortalServer(this.mContext);
        this.mServers[1] = NetworkDiagnosticsUtils.getDefaultCaptivePortalServer();
        this.mNeedCheckedInBackground = new AtomicBoolean();
        this.mNeedCheckedInBackground.set(false);
        this.mCommonConfig = CommonConfig.getInstance(this.mContext);
        registerPhoneStateListener();
    }

    private void checkNetworkState() {
        Log.i(TAG, "checkNetworkState");
        if (this.mCallState == 0) {
            this.mCheckingNetworkType = f.c(this.mContext);
            if (f.j(this.mContext)) {
                try {
                    if (this.mCheckNetworkThread != null) {
                        this.mCheckNetworkThread.cancel();
                    }
                    this.mCheckNetworkThread = null;
                    this.mCheckNetworkThread = new CheckNetworkThread(this.mServers);
                    this.mCheckNetworkThread.start();
                } catch (Exception e) {
                    Log.i(TAG, "NetworkChanged: an exception occured!! ");
                    e.printStackTrace();
                }
            } else {
                CheckNetworkThread checkNetworkThread = this.mCheckNetworkThread;
                if (checkNetworkThread != null) {
                    checkNetworkThread.cancel();
                }
                this.mCheckNetworkThread = null;
                NotificationUtil.cancelNetworkBlockedNotify(this.mContext);
                this.mNeedCheckedInBackground.set(false);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x008f  */
    /* JADX WARNING: Removed duplicated region for block: B:35:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDiagnosticsResult(com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState r5) {
        /*
            r4 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onDiagnosticsResult ret="
            r0.append(r1)
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "NetworkDiagnostics_CheckStateManager"
            android.util.Log.i(r1, r0)
            r4.mCurNetworkState = r5
            b.b.c.h.f$a r0 = r4.mCheckingNetworkType
            b.b.c.h.f$a r1 = b.b.c.h.f.a.MobileConnected
            if (r0 != r1) goto L_0x0029
            java.util.concurrent.atomic.AtomicBoolean r0 = r4.mNeedCheckedInBackground
            android.content.Context r1 = r4.mContext
            java.lang.String r2 = "com.xiaomi.xmsf"
            boolean r1 = miui.provider.ExtraNetwork.isMobileRestrict(r1, r2)
            goto L_0x002c
        L_0x0029:
            java.util.concurrent.atomic.AtomicBoolean r0 = r4.mNeedCheckedInBackground
            r1 = 0
        L_0x002c:
            r0.set(r1)
            int[] r0 = com.miui.networkassistant.service.tm.NetworkCheckStateManager.AnonymousClass3.$SwitchMap$com$miui$networkassistant$netdiagnose$NetworkDiagnosticsUtils$NetworkState
            int r5 = r5.ordinal()
            r5 = r0[r5]
            r0 = 1
            if (r5 == r0) goto L_0x009e
            r1 = 2
            if (r5 == r1) goto L_0x009e
            r2 = 3
            if (r5 == r2) goto L_0x009e
            r2 = 4
            if (r5 == r2) goto L_0x009e
            r2 = 5
            if (r5 == r2) goto L_0x0047
            goto L_0x00a3
        L_0x0047:
            android.content.Context r5 = r4.mContext
            b.b.c.h.f$a r5 = b.b.c.h.f.c(r5)
            b.b.c.h.f$a r2 = b.b.c.h.f.a.Diconnected
            if (r5 == r2) goto L_0x009e
            b.b.c.h.f$a r5 = r4.mCheckingNetworkType
            b.b.c.h.f$a r2 = r4.mCurNetworkType
            if (r5 == r2) goto L_0x0058
            goto L_0x009e
        L_0x0058:
            com.miui.networkassistant.config.CommonConfig r5 = r4.mCommonConfig
            boolean r5 = r5.isNetworkDiagnosticsFloatNotificationEnabled()
            b.b.c.h.f$a r2 = r4.mCheckingNetworkType
            b.b.c.h.f$a r3 = b.b.c.h.f.a.WifiConnected
            if (r2 != r3) goto L_0x006f
            android.content.Context r0 = r4.mContext
            com.miui.networkassistant.utils.NotificationUtil.sendWifiNetworkBlockedNotify(r0, r5)
            java.lang.String r5 = "wifi"
        L_0x006b:
            com.miui.networkassistant.utils.AnalyticsHelper.trackNetworkDiagnosticsNotificationShow(r5)
            goto L_0x007f
        L_0x006f:
            b.b.c.h.f$a r3 = b.b.c.h.f.a.MobileConnected
            if (r2 != r3) goto L_0x007f
            int r2 = r4.mSignalStrength
            if (r2 <= r0) goto L_0x007f
            android.content.Context r0 = r4.mContext
            com.miui.networkassistant.utils.NotificationUtil.sendOtherNetworkBlockedNotify(r0, r5)
            java.lang.String r5 = "other"
            goto L_0x006b
        L_0x007f:
            java.util.concurrent.atomic.AtomicBoolean r5 = r4.mNeedCheckedInBackground
            boolean r5 = r5.get()
            if (r5 == 0) goto L_0x00a3
            android.os.Handler r5 = r4.mHandler
            boolean r5 = r5.hasMessages(r1)
            if (r5 != 0) goto L_0x00a3
            android.os.Handler r5 = r4.mHandler
            android.os.Message r5 = r5.obtainMessage(r1)
            android.os.Handler r0 = r4.mHandler
            r1 = 300000(0x493e0, double:1.482197E-318)
            r0.sendMessageDelayed(r5, r1)
            goto L_0x00a3
        L_0x009e:
            android.content.Context r5 = r4.mContext
            com.miui.networkassistant.utils.NotificationUtil.cancelNetworkBlockedNotify(r5)
        L_0x00a3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.service.tm.NetworkCheckStateManager.onDiagnosticsResult(com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState):void");
    }

    private void registerPhoneStateListener() {
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        if (telephonyManager != null) {
            telephonyManager.listen(this.mPhoneStateListener, 32);
        }
    }

    private void unregisterPhoneStateListener() {
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        if (telephonyManager != null) {
            telephonyManager.listen(this.mPhoneStateListener, 0);
        }
    }

    public void networkBlocked() {
        Log.i(TAG, "networkBlocked");
        NetworkDiagnosticsUtils.NetworkState networkState = this.mCurNetworkState;
        if (networkState != NetworkDiagnosticsUtils.NetworkState.BLOCKED && networkState != NetworkDiagnosticsUtils.NetworkState.CAPTIVEPORTAL && !this.mNeedCheckedInBackground.get()) {
            checkNetworkState();
        }
    }

    public void networkChanged() {
        Log.i(TAG, "networkChanged");
        f.a c2 = f.c(this.mContext);
        String g = c2 != f.a.Diconnected ? c2 == f.a.WifiConnected ? f.g(this.mContext) : f.f(this.mContext) : "";
        if (c2 != this.mCurNetworkType || !TextUtils.equals(g, this.mCurNetworkInterface)) {
            Log.i(TAG, "NetworkChanged newType=" + c2 + " CurType=" + this.mCurNetworkType);
            this.mCurNetworkState = NetworkDiagnosticsUtils.NetworkState.UNKNOWN;
            this.mCurNetworkType = c2;
            this.mCurNetworkInterface = g;
            this.mCheckingNetworkType = f.a.Inited;
            NotificationUtil.cancelNetworkBlockedNotify(this.mContext);
            this.mNeedCheckedInBackground.set(false);
        } else if (c2 == f.a.Diconnected) {
            this.mCheckingNetworkType = f.a.Inited;
        }
    }

    public void networkConnected() {
        Log.i(TAG, "networkConnected");
        if (this.mCurNetworkState != NetworkDiagnosticsUtils.NetworkState.CONNECTED) {
            NotificationUtil.cancelNetworkBlockedNotify(this.mContext);
            this.mNeedCheckedInBackground.set(false);
            this.mCurNetworkState = NetworkDiagnosticsUtils.NetworkState.CONNECTED;
        }
    }

    public void onDestroy() {
        unregisterPhoneStateListener();
    }

    public void onLockScreenChange(Intent intent) {
        if (intent != null && TextUtils.equals(intent.getAction(), Constants.System.ACTION_USER_PRESENT) && this.mCurNetworkState == NetworkDiagnosticsUtils.NetworkState.BLOCKED) {
            checkNetworkState();
        }
    }

    public void onSignalStrengthChanged(SignalStrength signalStrength) {
        if (signalStrength != null) {
            this.mSignalStrength = NetworkDiagnosticsUtils.getSignalLevel(signalStrength);
        }
    }
}
