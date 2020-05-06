package com.miui.networkassistant.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.c.b.g;
import b.b.c.h.f;
import b.b.o.g.c;
import com.miui.common.customview.MovableLayout;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsCallback;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsDialView;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsManager;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils;
import com.miui.networkassistant.ui.dialog.MessageDialog;
import com.miui.networkassistant.ui.dialog.OptionTipDialog;
import com.miui.networkassistant.ui.fragment.NetworkDiagnosticsResultFragment;
import com.miui.networkassistant.ui.fragment.NetworkDiagnosticsScanningFragment;
import com.miui.networkassistant.ui.fragment.NetworkDiagnosticsSettingFragment;
import com.miui.networkassistant.ui.fragment.NetworkSpeedForAppsFragment;
import com.miui.networkassistant.ui.view.NetworkSpeedView;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import miui.app.ActionBar;
import miui.provider.ExtraNetwork;

public class NetworkDiagnosticsActivity extends BaseStatsActivity implements NetworkDiagnosticsCallback {
    private static final String EXTRA_FROM = "extra_from";
    private static final float M = 1048576.0f;
    static final int MSG_ID_REFRESH_UI = 1;
    private static final long NETWORK_SPEED_REFRESH_INTERVAL = 3000;
    private static final String TAG = "NetworkDiagnostics_main";
    public static final String TOTAL_RX_BYTE = "total_rx_byte";
    public static final String TOTAL_TX_BYTE = "total_tx_byte";
    private static final String URI_NA_TRAFFIC_STATS = "content://com.miui.networkassistant.provider/na_traffic_stats";
    private static final String VALUE_FROM_SETTINGS = "com.android.settings";
    /* access modifiers changed from: private */
    public static NetworkDiagnosticsManager mNetworkDiagnosticsManager;
    private int lastStr;
    CheckNetworkStateTask mCheckNetworkStateTask = null;
    private Animation.AnimationListener mCollapseListener = new Animation.AnimationListener() {
        public void onAnimationEnd(Animation animation) {
            if (NetworkDiagnosticsActivity.this.mLastShowViewId == R.id.network_diagnostics_network_speed_for_apps) {
                NetworkDiagnosticsActivity.this.mRootLayout.setScrollable(true);
                NetworkDiagnosticsActivity.this.mDiagnosticsBtn.setVisibility(0);
            }
            if (NetworkDiagnosticsActivity.this.mLastHideView != null) {
                NetworkDiagnosticsActivity.this.mLastHideView.setVisibility(8);
            }
            switch (NetworkDiagnosticsActivity.this.mLastShowViewId) {
                case R.id.network_diagnostics_result_zone /*2131297370*/:
                    NetworkDiagnosticsActivity.this.mNetworkDiagnosticsResultFragment.showResult();
                    return;
                case R.id.network_diagnostics_scanning_zone /*2131297371*/:
                    NetworkDiagnosticsActivity.this.mNetworkDiagnosticsScanningFragment.startDiagnostic();
                    return;
                default:
                    return;
            }
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationStart(Animation animation) {
        }
    };
    private BroadcastReceiver mConnectionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(NetworkDiagnosticsActivity.TAG, "mConnectionReceiver : network changed broadcast");
            NetworkDiagnosticsActivity.this.checkNetworkStatus();
            if (TextUtils.equals(intent.getAction(), "android.net.conn.CONNECTIVITY_CHANGE")) {
                NetworkInfo activeNetworkInfo = ((ConnectivityManager) NetworkDiagnosticsActivity.this.mAppContext.getSystemService("connectivity")).getActiveNetworkInfo();
                boolean unused = NetworkDiagnosticsActivity.this.mIsNetworkConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }
    };
    /* access modifiers changed from: private */
    public AtomicLong mCurrentSpeed = new AtomicLong();
    /* access modifiers changed from: private */
    public Button mDiagnosticsBtn;
    private NetworkDiagnosticsDialView mDialView;
    private String mFrom;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                NetworkDiagnosticsActivity.this.refreshUI();
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mIsNetworkConnected;
    /* access modifiers changed from: private */
    public AtomicBoolean mIsShowing = new AtomicBoolean();
    /* access modifiers changed from: private */
    public View mLastHideView;
    private long mLastRefreshSpeedTime;
    private long mLastRefreshSpeedTotalBytes;
    /* access modifiers changed from: private */
    public int mLastShowViewId;
    private MovableLayout.b mMovableLayoutOnScrollListener = new MovableLayout.b() {
        public boolean onContentScrolled() {
            if (NetworkDiagnosticsActivity.this.mLastShowViewId == R.id.network_diagnostics_network_speed_for_apps) {
                return NetworkDiagnosticsActivity.this.mNetworkSpeedForAppsFragment.onContentScrolled();
            }
            return false;
        }

        public void onScroll(int i, float f) {
        }

        public void onStartScroll() {
        }

        public void onStopScroll() {
        }
    };
    /* access modifiers changed from: private */
    public NetworkDiagnosticsResultFragment mNetworkDiagnosticsResultFragment;
    /* access modifiers changed from: private */
    public NetworkDiagnosticsScanningFragment mNetworkDiagnosticsScanningFragment;
    /* access modifiers changed from: private */
    public NetworkSpeedForAppsFragment mNetworkSpeedForAppsFragment;
    private Uri mNetworkUri;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (view.getId() == R.id.start_diagnostic_btn) {
                NetworkDiagnosticsActivity.this.onStartDiagnosticsBtnClick();
            }
        }
    };
    private RefreshTask mRefreshTask;
    /* access modifiers changed from: private */
    public MovableLayout mRootLayout;
    private ArrayList<String> mServerList;
    private NetworkSpeedView mSpeedView;
    private int mStatusBarShowNetworkSpeed;
    private TextView mTvCurrentActiveNetworkType;

    /* renamed from: com.miui.networkassistant.ui.activity.NetworkDiagnosticsActivity$8  reason: invalid class name */
    static /* synthetic */ class AnonymousClass8 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$networkassistant$netdiagnose$NetworkDiagnosticsUtils$NetworkState = new int[NetworkDiagnosticsUtils.NetworkState.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|8) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        static {
            /*
                com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState[] r0 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$networkassistant$netdiagnose$NetworkDiagnosticsUtils$NetworkState = r0
                int[] r0 = $SwitchMap$com$miui$networkassistant$netdiagnose$NetworkDiagnosticsUtils$NetworkState     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState r1 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.CONNECTED     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$networkassistant$netdiagnose$NetworkDiagnosticsUtils$NetworkState     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState r1 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.BLOCKED     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$networkassistant$netdiagnose$NetworkDiagnosticsUtils$NetworkState     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState r1 = com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState.CAPTIVEPORTAL     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.activity.NetworkDiagnosticsActivity.AnonymousClass8.<clinit>():void");
        }
    }

    private static class CheckNetworkStateTask extends AsyncTask<ArrayList<String>, Void, NetworkDiagnosticsUtils.NetworkState> {
        private WeakReference<NetworkDiagnosticsActivity> mActivityRef;

        public CheckNetworkStateTask(NetworkDiagnosticsActivity networkDiagnosticsActivity) {
            this.mActivityRef = new WeakReference<>(networkDiagnosticsActivity);
        }

        /* JADX WARNING: type inference failed for: r1v2, types: [android.content.Context, com.miui.networkassistant.ui.activity.NetworkDiagnosticsActivity] */
        /* access modifiers changed from: protected */
        public NetworkDiagnosticsUtils.NetworkState doInBackground(ArrayList<String>... arrayListArr) {
            ? r1 = (NetworkDiagnosticsActivity) this.mActivityRef.get();
            if (r1 == 0) {
                return NetworkDiagnosticsUtils.NetworkState.UNKNOWN;
            }
            NetworkDiagnosticsUtils.NetworkState networkState = NetworkDiagnosticsUtils.NetworkState.UNKNOWN;
            try {
                Log.i(NetworkDiagnosticsActivity.TAG, "CheckNetworkStateTask beg ");
                ArrayList<String> arrayList = arrayListArr[0];
                for (int i = 0; i < arrayList.size(); i++) {
                    String str = arrayList.get(i);
                    if (!TextUtils.isEmpty(str)) {
                        networkState = NetworkDiagnosticsUtils.CheckNetworkState(r1, str);
                        Log.i(NetworkDiagnosticsActivity.TAG, "CheckNetworkStateTask ret= " + networkState);
                        if (networkState == NetworkDiagnosticsUtils.NetworkState.CONNECTED || networkState == NetworkDiagnosticsUtils.NetworkState.CAPTIVEPORTAL) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(NetworkDiagnosticsActivity.TAG, "CheckNetworkStateTask end ");
            return networkState;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(NetworkDiagnosticsUtils.NetworkState networkState) {
            NetworkDiagnosticsActivity networkDiagnosticsActivity = (NetworkDiagnosticsActivity) this.mActivityRef.get();
            if (networkDiagnosticsActivity != null) {
                networkDiagnosticsActivity.showNetworkStatus(networkState);
                networkDiagnosticsActivity.mCheckNetworkStateTask = null;
            }
        }
    }

    private static class RefreshTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<NetworkDiagnosticsActivity> activityRef;

        public RefreshTask(NetworkDiagnosticsActivity networkDiagnosticsActivity) {
            this.activityRef = new WeakReference<>(networkDiagnosticsActivity);
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... voidArr) {
            NetworkDiagnosticsActivity networkDiagnosticsActivity;
            while (!isCancelled() && (networkDiagnosticsActivity = (NetworkDiagnosticsActivity) this.activityRef.get()) != null) {
                Long valueOf = Long.valueOf(networkDiagnosticsActivity.mCurrentSpeed.get());
                networkDiagnosticsActivity.refreshNetworkSpeed();
                if (networkDiagnosticsActivity.mLastShowViewId == R.id.network_diagnostics_network_speed_for_apps && networkDiagnosticsActivity.mIsShowing.get()) {
                    networkDiagnosticsActivity.mNetworkSpeedForAppsFragment.refresh();
                }
                if (valueOf.longValue() != networkDiagnosticsActivity.mCurrentSpeed.get()) {
                    networkDiagnosticsActivity.mHandler.sendEmptyMessage(1);
                }
                try {
                    Thread.sleep(NetworkDiagnosticsActivity.NETWORK_SPEED_REFRESH_INTERVAL);
                } catch (InterruptedException unused) {
                }
            }
            return null;
            return null;
        }
    }

    private static class SignalStrengthListener extends PhoneStateListener {
        private SignalStrengthListener() {
        }

        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);
            NetworkDiagnosticsActivity.mNetworkDiagnosticsManager.setCurrentDataRat(NetworkDiagnosticsUtils.getDataRat(serviceState));
        }

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            NetworkDiagnosticsActivity.mNetworkDiagnosticsManager.setCurrentSignalStrength(NetworkDiagnosticsUtils.getSignalLevel(signalStrength));
            NetworkDiagnosticsActivity.mNetworkDiagnosticsManager.setCurrentLteRsrq(NetworkDiagnosticsUtils.getSignalLteRsrq(signalStrength));
        }
    }

    /* access modifiers changed from: private */
    public void checkNetworkStatus() {
        if (this.mCheckNetworkStateTask == null) {
            this.mCheckNetworkStateTask = new CheckNetworkStateTask(this);
            this.mCheckNetworkStateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new ArrayList[]{this.mServerList});
        }
    }

    private long getTotalByte() {
        long j = 0;
        if (!this.mIsNetworkConnected) {
            return 0;
        }
        Cursor query = getContentResolver().query(this.mNetworkUri, (String[]) null, (String) null, (String[]) null, (String) null);
        if (query == null) {
            return TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
        }
        if (query.moveToFirst()) {
            j = query.getLong(query.getColumnIndex("total_tx_byte")) + query.getLong(query.getColumnIndex("total_rx_byte"));
        }
        query.close();
        return j;
    }

    private void initView() {
        this.mTvCurrentActiveNetworkType = (TextView) findViewById(R.id.current_network_type);
        this.mSpeedView = (NetworkSpeedView) findViewById(R.id.netspeed);
        this.mDialView = (NetworkDiagnosticsDialView) findViewById(R.id.nd_dial_view);
        this.mDiagnosticsBtn = (Button) findViewById(R.id.start_diagnostic_btn);
        this.mDiagnosticsBtn.setOnClickListener(this.mOnClickListener);
        this.mNetworkSpeedForAppsFragment = (NetworkSpeedForAppsFragment) getFragmentManager().findFragmentById(R.id.network_diagnostics_network_speed_for_apps);
        this.mNetworkDiagnosticsScanningFragment = (NetworkDiagnosticsScanningFragment) getFragmentManager().findFragmentById(R.id.network_diagnostics_scanning_zone);
        this.mNetworkDiagnosticsResultFragment = (NetworkDiagnosticsResultFragment) getFragmentManager().findFragmentById(R.id.network_diagnostics_result_zone);
        this.mRootLayout = (MovableLayout) findViewById(R.id.root_layout);
        this.mRootLayout.setScrollable(true);
        this.mRootLayout.setScrollListener(this.mMovableLayoutOnScrollListener);
        this.mLastShowViewId = R.id.network_diagnostics_network_speed_for_apps;
    }

    /* access modifiers changed from: private */
    public void onStartDiagnosticsBtnClick() {
        f.a c2 = f.c(this.mAppContext);
        if (c2 != f.a.Diconnected && c2 != f.a.Inited) {
            if (c2 == f.a.MobileConnected) {
                SimCardHelper instance = SimCardHelper.getInstance(this.mAppContext);
                if (instance == null || !TelephonyUtil.isNetworkRoaming(this.mAppContext, instance.getCurrentMobileSlotNum())) {
                    Context context = this.mAppContext;
                    if (ExtraNetwork.isMobileRestrict(context, context.getPackageName())) {
                        new OptionTipDialog(this.mActivity, new OptionTipDialog.OptionDialogListener() {
                            public void onOptionUpdated(boolean z) {
                                if (z) {
                                    Intent intent = new Intent(NetworkDiagnosticsActivity.this.mActivity, FirewallActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("VisibleItemIndex", 1);
                                    intent.putExtras(bundle);
                                    NetworkDiagnosticsActivity.this.startActivity(intent);
                                }
                            }
                        }).buildShowDialog(this.mActivity.getString(R.string.dialog_title_attention), this.mActivity.getString(R.string.network_permission_exception_tips), this.mActivity.getString(R.string.cancel_button), this.mActivity.getString(R.string.open_now));
                        return;
                    }
                } else {
                    new MessageDialog(this.mActivity).buildShowDialog(this.mActivity.getString(R.string.dialog_title_attention), this.mActivity.getString(R.string.network_roaming_exception_tips));
                    return;
                }
            }
            startDiagnostic();
        } else if (TelephonyUtil.isAirModeOn(this.mAppContext)) {
            new MessageDialog(this.mActivity).buildShowDialog(this.mActivity.getString(R.string.dialog_title_attention), this.mActivity.getString(R.string.disable_air_mode));
            return;
        } else if (!mNetworkDiagnosticsManager.isMobileDataEnable()) {
            if (mNetworkDiagnosticsManager.isWifiEnable()) {
                startDiagnostic();
                return;
            } else {
                new MessageDialog(this.mActivity).buildShowDialog(this.mActivity.getString(R.string.dialog_title_attention), this.mActivity.getString(R.string.enable_network));
                return;
            }
        } else if (!SimUserInfo.getInstance(this.mAppContext, Sim.getCurrentActiveSlotNum()).isSimInserted()) {
            new MessageDialog(this.mActivity).buildShowDialog(this.mActivity.getString(R.string.dialog_title_attention), this.mActivity.getString(R.string.main_alert_message_no_imsi));
            return;
        }
        startDiagnostic();
    }

    /* access modifiers changed from: private */
    public void refreshNetworkSpeed() {
        long totalByte = getTotalByte();
        long currentTimeMillis = System.currentTimeMillis();
        int i = (totalByte > 0 ? 1 : (totalByte == 0 ? 0 : -1));
        if (i == 0) {
            this.mLastRefreshSpeedTime = 0;
            this.mLastRefreshSpeedTotalBytes = 0;
        }
        this.mCurrentSpeed.set(0);
        long j = this.mLastRefreshSpeedTime;
        if (j != 0 && currentTimeMillis > j) {
            long j2 = this.mLastRefreshSpeedTotalBytes;
            if (!(j2 == 0 || i == 0 || totalByte <= j2)) {
                this.mCurrentSpeed.set(((totalByte - j2) * 1000) / (currentTimeMillis - j));
            }
        }
        this.mLastRefreshSpeedTime = currentTimeMillis;
        this.mLastRefreshSpeedTotalBytes = totalByte;
    }

    /* access modifiers changed from: private */
    public void refreshUI() {
        float f;
        float f2;
        float f3;
        this.mSpeedView.updateNetworkSpeed(this.mCurrentSpeed.get());
        float f4 = (float) this.mCurrentSpeed.get();
        if (f4 > 1.6777216E7f) {
            f = 1.0f;
        } else {
            if (f4 > 8388608.0f) {
                f2 = ((f4 - 8388608.0f) / 8388608.0f) * 0.2f;
                f3 = 0.8f;
            } else if (f4 > 4194304.0f) {
                f2 = ((f4 - 4194304.0f) / 4194304.0f) * 0.2f;
                f3 = 0.6f;
            } else if (f4 > 2097152.0f) {
                f2 = ((f4 - 2097152.0f) / 2097152.0f) * 0.2f;
                f3 = 0.4f;
            } else {
                f = f4 > M ? (((f4 - M) / M) * 0.2f) + 0.2f : (f4 / M) * 0.2f;
            }
            f = f2 + f3;
        }
        this.mDialView.setProgress(f);
    }

    private void registerConnectionReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        intentFilter.addAction(Constants.System.ACTION_SIM_STATE_CHANGED);
        registerReceiver(this.mConnectionReceiver, intentFilter);
    }

    private void registerSignalStrengthListener() {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        Class cls = Integer.TYPE;
        a2.a("listenForSlot", new Class[]{cls, PhoneStateListener.class, cls}, Integer.valueOf(Sim.getCurrentActiveSlotNum()), new SignalStrengthListener(), 257);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, com.miui.networkassistant.ui.activity.NetworkDiagnosticsActivity] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0012, code lost:
        if (r3 != 3) goto L_0x0047;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showNetworkStatus(com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils.NetworkState r3) {
        /*
            r2 = this;
            int[] r0 = com.miui.networkassistant.ui.activity.NetworkDiagnosticsActivity.AnonymousClass8.$SwitchMap$com$miui$networkassistant$netdiagnose$NetworkDiagnosticsUtils$NetworkState
            int r3 = r3.ordinal()
            r3 = r0[r3]
            r0 = 1
            r1 = 2131758456(0x7f100d78, float:1.9147877E38)
            if (r3 == r0) goto L_0x0027
            r0 = 2
            if (r3 == r0) goto L_0x0015
            r0 = 3
            if (r3 == r0) goto L_0x0015
            goto L_0x0047
        L_0x0015:
            r2.lastStr = r1
        L_0x0017:
            android.widget.TextView r3 = r2.mTvCurrentActiveNetworkType
            android.content.res.Resources r0 = r2.getResources()
            int r1 = r2.lastStr
            java.lang.String r0 = r0.getString(r1)
            r3.setText(r0)
            goto L_0x0047
        L_0x0027:
            boolean r3 = b.b.c.h.f.i(r2)
            if (r3 == 0) goto L_0x0033
            r3 = 2131755812(0x7f100324, float:1.9142514E38)
        L_0x0030:
            r2.lastStr = r3
            goto L_0x0017
        L_0x0033:
            boolean r3 = b.b.c.h.f.l(r2)
            if (r3 == 0) goto L_0x003d
            r3 = 2131755814(0x7f100326, float:1.9142518E38)
            goto L_0x0030
        L_0x003d:
            boolean r3 = b.b.c.h.f.h(r2)
            if (r3 == 0) goto L_0x0015
            r3 = 2131755813(0x7f100325, float:1.9142516E38)
            goto L_0x0030
        L_0x0047:
            android.widget.TextView r3 = r2.mTvCurrentActiveNetworkType
            r0 = 0
            r3.setVisibility(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.activity.NetworkDiagnosticsActivity.showNetworkStatus(com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils$NetworkState):void");
    }

    private void startDiagnostic() {
        AnalyticsHelper.trackNetworkDiagnosticsStart();
        this.mDiagnosticsBtn.setVisibility(8);
        TextView textView = this.mTvCurrentActiveNetworkType;
        textView.setText(getString(R.string.na_text_checking) + "0%");
        if (this.mActivity.findViewById(R.id.network_diagnostics_scanning_zone).getVisibility() == 8) {
            switchView(R.id.network_diagnostics_scanning_zone, R.id.network_diagnostics_network_speed_for_apps, true);
        } else {
            this.mNetworkDiagnosticsScanningFragment.startDiagnostic();
        }
    }

    public boolean isFromSettings() {
        return VALUE_FROM_SETTINGS.equals(this.mFrom);
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.networkassistant.ui.activity.NetworkDiagnosticsActivity, com.miui.networkassistant.ui.activity.BaseStatsActivity, b.b.c.c.b.a] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mServerList = new ArrayList<>();
        this.mServerList.add(NetworkDiagnosticsUtils.getCaptivePortalServer(this));
        this.mServerList.add(NetworkDiagnosticsUtils.getDefaultCaptivePortalServer());
        this.mNetworkUri = Uri.parse(URI_NA_TRAFFIC_STATS);
        mNetworkDiagnosticsManager = NetworkDiagnosticsManager.getInstance(this.mAppContext);
        initView();
        registerConnectionReceiver();
        registerSignalStrengthListener();
    }

    /* access modifiers changed from: protected */
    public int onCreateContentView() {
        return R.layout.activity_network_diagnostics;
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, miui.app.Activity, com.miui.networkassistant.ui.activity.NetworkDiagnosticsActivity] */
    /* access modifiers changed from: protected */
    public void onCustomizeActionBar(ActionBar actionBar) {
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.pc_main_activity_background_color)));
        actionBar.setDisplayOptions(28);
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(R.drawable.v_setting_icon);
        imageView.setContentDescription(getString(R.string.activity_title_settings));
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                g.startWithFragment(NetworkDiagnosticsActivity.this.mActivity, NetworkDiagnosticsSettingFragment.class);
            }
        });
        actionBar.setEndView(imageView);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        BroadcastReceiver broadcastReceiver = this.mConnectionReceiver;
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages((Object) null);
        }
        NetworkDiagnosticsActivity.super.onDestroy();
    }

    public void onNetworkDiagnosticsDone(NetworkDiagnosticsUtils.NetworkState networkState) {
        showNetworkStatus(networkState);
    }

    public void onNetworkDiagnosticsProcessChanged(int i) {
        String str;
        if (i < 0) {
            i = 0;
        } else if (i > 100) {
            i = 100;
        }
        TextView textView = this.mTvCurrentActiveNetworkType;
        if (i == 100) {
            str = getString(this.lastStr);
        } else {
            str = getString(R.string.na_text_checking) + i + "%";
        }
        textView.setText(str);
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        NetworkDiagnosticsActivity.super.onNewIntent(intent);
        this.mFrom = getIntent().getStringExtra(EXTRA_FROM);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        Settings.System.putInt(this.mActivity.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_SPEED, this.mStatusBarShowNetworkSpeed);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        RefreshTask refreshTask = this.mRefreshTask;
        if (refreshTask != null) {
            refreshTask.cancel(true);
            this.mRefreshTask = null;
        }
        this.mRefreshTask = new RefreshTask(this);
        this.mRefreshTask.execute(new Void[0]);
        this.mIsShowing.set(true);
        checkNetworkStatus();
        this.mStatusBarShowNetworkSpeed = Settings.System.getInt(this.mActivity.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_SPEED, 0);
        if (this.mStatusBarShowNetworkSpeed == 1) {
            Settings.System.putInt(this.mActivity.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_SPEED, 0);
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        this.mIsShowing.set(false);
        RefreshTask refreshTask = this.mRefreshTask;
        if (refreshTask != null) {
            refreshTask.cancel(true);
            this.mRefreshTask = null;
        }
        NetworkDiagnosticsActivity.super.onStop();
    }

    public void switchView(int i, int i2, boolean z) {
        this.mLastShowViewId = i;
        if (i != R.id.network_diagnostics_network_speed_for_apps) {
            this.mRootLayout.setScrollable(false);
        }
        View findViewById = findViewById(i2);
        View findViewById2 = findViewById(i);
        if (findViewById.getVisibility() == 0 || findViewById2.getVisibility() != 0) {
            this.mLastHideView = findViewById;
            b.b.c.a.c cVar = new b.b.c.a.c(this.mActivity);
            cVar.a(findViewById, AnimationUtils.loadAnimation(this.mActivity, R.anim.collapse_from_top), this.mCollapseListener);
            cVar.a(findViewById2, AnimationUtils.loadAnimation(this.mActivity, R.anim.expand_to_top), (Animation.AnimationListener) null);
            cVar.a();
        }
    }
}
