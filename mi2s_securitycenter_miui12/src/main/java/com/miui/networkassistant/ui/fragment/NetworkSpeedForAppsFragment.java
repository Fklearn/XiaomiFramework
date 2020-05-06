package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import b.b.c.c.b.h;
import b.b.c.h.f;
import b.b.c.j.B;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.service.wrapper.AppMonitorWrapper;
import com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter;
import com.miui.networkassistant.ui.dialog.CommonDialog;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.networkassistant.utils.LabelLoadHelper;
import com.miui.networkassistant.utils.PackageUtil;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;
import miui.app.ProgressDialog;
import miuix.recyclerview.widget.RecyclerView;

public class NetworkSpeedForAppsFragment extends h {
    private static final int MSG_ID_HIDE_LOADING = 2;
    private static final int MSG_ID_REFRESH_UI = 1;
    private static final String TAG = "NetworkSpeedForApps";
    /* access modifiers changed from: private */
    public NetworkSpeedForAppsListAdapter mAdapter;
    /* access modifiers changed from: private */
    public CommonDialog mConfirmForceStopDialog;
    /* access modifiers changed from: private */
    public AppInfo mCurAppInfo;
    /* access modifiers changed from: private */
    public String mCurrentActiveIface;
    private long mCurrentRefreshTime = 0;
    /* access modifiers changed from: private */
    public TextView mEmptyView;
    private RefreshHandler mHandler = new RefreshHandler(this);
    private long mLastRefreshTime = 0;
    private ProgressDialog mLoadingDialog;
    /* access modifiers changed from: private */
    public AppMonitorWrapper mMonitorCenter;
    private SpeedAppMonitorListener mMonitorCenterListener = new SpeedAppMonitorListener(this);
    private BroadcastReceiver mNetworkConnectivityReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            synchronized (this) {
                String unused = NetworkSpeedForAppsFragment.this.mCurrentActiveIface = f.b(NetworkSpeedForAppsFragment.this.mAppContext);
            }
        }
    };
    private RecyclerView mRecyclerView;
    private TextView mRxTextView;
    ArrayList<Map<String, String>> mSpeedDetail;
    private NetworkSpeedForAppsListAdapter.NetworkSpeedTotalInfo mSpeedTotalInfo;
    private int mStatusBarShowNetworkSpeed;
    private TextView mTxTextView;

    private static class RefreshHandler extends Handler {
        private final WeakReference<NetworkSpeedForAppsFragment> mFragment;

        public RefreshHandler(NetworkSpeedForAppsFragment networkSpeedForAppsFragment) {
            this.mFragment = new WeakReference<>(networkSpeedForAppsFragment);
        }

        public void handleMessage(Message message) {
            NetworkSpeedForAppsFragment networkSpeedForAppsFragment = (NetworkSpeedForAppsFragment) this.mFragment.get();
            int i = message.what;
            if (i != 1) {
                if (i == 2 && networkSpeedForAppsFragment != null) {
                    networkSpeedForAppsFragment.hideLoadingView();
                }
            } else if (networkSpeedForAppsFragment != null) {
                networkSpeedForAppsFragment.refreshUI();
            }
        }
    }

    static class SpeedAppMonitorListener implements AppMonitorWrapper.AppMonitorListener {
        private final WeakReference<NetworkSpeedForAppsFragment> mFragment;

        public SpeedAppMonitorListener(NetworkSpeedForAppsFragment networkSpeedForAppsFragment) {
            this.mFragment = new WeakReference<>(networkSpeedForAppsFragment);
        }

        public void onAppListUpdated() {
            NetworkSpeedForAppsFragment networkSpeedForAppsFragment = (NetworkSpeedForAppsFragment) this.mFragment.get();
            if (networkSpeedForAppsFragment != null) {
                networkSpeedForAppsFragment.mAdapter.setAppList(networkSpeedForAppsFragment.mMonitorCenter.getFilteredAppInfosList());
                networkSpeedForAppsFragment.mEmptyView.setVisibility(8);
            }
        }
    }

    private boolean isTop(RecyclerView recyclerView) {
        return recyclerView.getChildCount() == 0 || recyclerView.getChildAt(0).getTop() >= recyclerView.getPaddingTop();
    }

    /* access modifiers changed from: private */
    public void refreshUI() {
        TextView textView;
        String str;
        synchronized (this) {
            this.mSpeedTotalInfo = this.mAdapter.refresh(this.mSpeedDetail, this.mCurrentActiveIface, (this.mLastRefreshTime == 0 || this.mCurrentRefreshTime <= this.mLastRefreshTime) ? 0 : this.mCurrentRefreshTime - this.mLastRefreshTime);
            this.mLastRefreshTime = this.mCurrentRefreshTime;
            if (this.mSpeedTotalInfo.total != 0) {
                String[] formatSpeed = FormatBytesUtil.formatSpeed(this.mAppContext, this.mSpeedTotalInfo.rxTotal);
                if (formatSpeed != null) {
                    this.mRxTextView.setText(formatSpeed[0] + formatSpeed[1]);
                }
                String[] formatSpeed2 = FormatBytesUtil.formatSpeed(this.mAppContext, this.mSpeedTotalInfo.txTotal);
                if (formatSpeed2 != null) {
                    textView = this.mTxTextView;
                    str = formatSpeed2[0] + formatSpeed2[1];
                }
            } else {
                String[] formatSpeed3 = FormatBytesUtil.formatSpeed(this.mAppContext, 0);
                if (formatSpeed3 != null) {
                    this.mRxTextView.setText(formatSpeed3[0] + formatSpeed3[1]);
                    textView = this.mTxTextView;
                    str = formatSpeed3[0] + formatSpeed3[1];
                }
            }
            textView.setText(str);
        }
        if (this.mLoadingDialog != null) {
            this.mHandler.sendEmptyMessageDelayed(2, 500);
        }
    }

    private void registerNetworkConnectivityReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.mActivity.registerReceiver(this.mNetworkConnectivityReceiver, intentFilter);
    }

    private void unRegisterNetworkConnectivityRececiver() {
        this.mActivity.unregisterReceiver(this.mNetworkConnectivityReceiver);
    }

    public void hideLoadingView() {
        try {
            if (this.mLoadingDialog != null && this.mLoadingDialog.isShowing() && this.mActivity != null) {
                this.mLoadingDialog.dismiss();
                this.mLoadingDialog = null;
                Log.i(TAG, "hideLoadingView,mLoadingDialog.dismiss");
            }
        } catch (Exception e) {
            Log.e(TAG, "hideLoadingView", e);
        }
    }

    /* access modifiers changed from: protected */
    public void initView() {
        setTitle(getResources().getString(R.string.network_speed_for_apps_title));
        this.mCurrentActiveIface = f.b(this.mAppContext);
        this.mRxTextView = (TextView) findViewById(R.id.textview_traffic_rxTotal);
        this.mTxTextView = (TextView) findViewById(R.id.textview_traffic_txTotal);
        String[] formatSpeed = FormatBytesUtil.formatSpeed(this.mAppContext, 0);
        if (formatSpeed != null) {
            TextView textView = this.mRxTextView;
            textView.setText(formatSpeed[0] + formatSpeed[1]);
            TextView textView2 = this.mTxTextView;
            textView2.setText(formatSpeed[0] + formatSpeed[1]);
        }
        this.mMonitorCenter = AppMonitorWrapper.getInstance(this.mAppContext);
        this.mMonitorCenter.registerLisener(this.mMonitorCenterListener);
        registerNetworkConnectivityReceiver();
        showLoadingView();
        this.mAdapter.setOnItemClickListener(new NetworkSpeedForAppsListAdapter.OnItemClickListener() {
            public void onItemClick(int i) {
                AppInfo unused = NetworkSpeedForAppsFragment.this.mCurAppInfo = null;
                NetworkSpeedForAppsListAdapter.AppSpeedInfo appSpeedInfo = NetworkSpeedForAppsFragment.this.mAdapter.getData().get(i);
                if (appSpeedInfo != null && (appSpeedInfo instanceof NetworkSpeedForAppsListAdapter.AppSpeedInfo)) {
                    AppInfo appInfo = appSpeedInfo.getAppInfo();
                    if (appInfo == null) {
                        Log.i(NetworkSpeedForAppsFragment.TAG, "OnItemClickListener appInfo is null!!");
                        return;
                    }
                    AppInfo unused2 = NetworkSpeedForAppsFragment.this.mCurAppInfo = appInfo;
                    NetworkSpeedForAppsFragment networkSpeedForAppsFragment = NetworkSpeedForAppsFragment.this;
                    CommonDialog unused3 = networkSpeedForAppsFragment.mConfirmForceStopDialog = new CommonDialog(networkSpeedForAppsFragment.mActivity, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == -1 && NetworkSpeedForAppsFragment.this.mCurAppInfo != null) {
                                try {
                                    String charSequence = NetworkSpeedForAppsFragment.this.mCurAppInfo.packageName.toString();
                                    int c2 = B.c();
                                    if (PackageUtil.isSpecialApp(charSequence)) {
                                        c2 = B.c(PackageUtil.parseUidByPackageName(charSequence));
                                    }
                                    PackageUtil.forceStopPackage(NetworkSpeedForAppsFragment.this.mAppContext, PackageUtil.getRealPackageName(charSequence), c2);
                                    NetworkSpeedForAppsFragment.this.mAdapter.packageStoped(NetworkSpeedForAppsFragment.this.mCurAppInfo.uid);
                                    AppInfo unused = NetworkSpeedForAppsFragment.this.mCurAppInfo = null;
                                } catch (Exception e) {
                                    Log.e(NetworkSpeedForAppsFragment.TAG, "mConfirmForceStopDialog.onClick", e);
                                }
                            }
                            CommonDialog unused2 = NetworkSpeedForAppsFragment.this.mConfirmForceStopDialog = null;
                        }
                    });
                    NetworkSpeedForAppsFragment.this.mConfirmForceStopDialog.setTitle(LabelLoadHelper.loadLabel(NetworkSpeedForAppsFragment.this.mAppContext, appInfo.packageName).toString());
                    if (!NetworkSpeedForAppsFragment.this.mAdapter.isAppCanClose(appInfo)) {
                        NetworkSpeedForAppsFragment.this.mConfirmForceStopDialog.setMessage(NetworkSpeedForAppsFragment.this.getResources().getString(R.string.network_speed_for_apps_close_system_app_dlg_msg));
                        NetworkSpeedForAppsFragment.this.mConfirmForceStopDialog.setPostiveText("");
                        NetworkSpeedForAppsFragment.this.mConfirmForceStopDialog.setNagetiveText(NetworkSpeedForAppsFragment.this.getResources().getString(17039370));
                    } else {
                        NetworkSpeedForAppsFragment.this.mConfirmForceStopDialog.setMessage(NetworkSpeedForAppsFragment.this.getResources().getString(R.string.network_speed_for_apps_close_app_dlg_msg));
                        NetworkSpeedForAppsFragment.this.mConfirmForceStopDialog.setNagetiveText(NetworkSpeedForAppsFragment.this.getResources().getString(17039360));
                        NetworkSpeedForAppsFragment.this.mConfirmForceStopDialog.setPostiveText(NetworkSpeedForAppsFragment.this.getResources().getString(R.string.network_speed_for_apps_close));
                    }
                    NetworkSpeedForAppsFragment.this.mConfirmForceStopDialog.show();
                }
            }
        });
    }

    public boolean onContentScrolled() {
        if (this.mAdapter.getData() == null || this.mAdapter.getData().size() == 0) {
            return true;
        }
        return !isTop(this.mRecyclerView);
    }

    /* access modifiers changed from: protected */
    public View onCreateFooterView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return null;
    }

    /* access modifiers changed from: protected */
    public View onCreateHeaderView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.listfragment_header_network_speed_for_apps, (ViewGroup) null);
    }

    /* access modifiers changed from: protected */
    public NetworkSpeedForAppsListAdapter onCreateListAdapter() {
        this.mAdapter = new NetworkSpeedForAppsListAdapter(this.mActivity);
        return this.mAdapter;
    }

    /* access modifiers changed from: protected */
    public void onCreateView2(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.header_layout);
        View onCreateHeaderView = onCreateHeaderView(layoutInflater, frameLayout);
        if (onCreateHeaderView != null && onCreateHeaderView.getParent() == null) {
            frameLayout.addView(onCreateHeaderView);
        }
        FrameLayout frameLayout2 = (FrameLayout) findViewById(R.id.footer_layout);
        View onCreateFooterView = onCreateFooterView(layoutInflater, viewGroup);
        if (onCreateFooterView != null && onCreateFooterView.getParent() == null) {
            frameLayout2.addView(onCreateFooterView);
        }
        this.mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        this.mEmptyView = (TextView) findViewById(16908292);
        this.mRecyclerView.setSpringEnabled(false);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.mAdapter = onCreateListAdapter();
        this.mRecyclerView.setAdapter(this.mAdapter);
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.listfragment_root;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        unRegisterNetworkConnectivityRececiver();
        this.mMonitorCenter.unRegisterLisener(this.mMonitorCenterListener);
        this.mHandler.removeCallbacksAndMessages((Object) null);
    }

    public void onDestroyView() {
        hideLoadingView();
        super.onDestroyView();
    }

    public void onPause() {
        super.onPause();
        this.mHandler.removeCallbacksAndMessages((Object) null);
        Settings.System.putInt(this.mActivity.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_SPEED, this.mStatusBarShowNetworkSpeed);
    }

    public void onResume() {
        super.onResume();
        this.mStatusBarShowNetworkSpeed = Settings.System.getInt(this.mActivity.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_SPEED, 0);
        if (this.mStatusBarShowNetworkSpeed == 1) {
            Settings.System.putInt(this.mActivity.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_SPEED, 0);
        }
    }

    public void onStop() {
        super.onStop();
        try {
            if (this.mConfirmForceStopDialog != null) {
                this.mConfirmForceStopDialog.getDialog().dismiss();
            }
        } catch (Exception e) {
            Log.e(TAG, "onStop", e);
        }
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    public synchronized void refresh() {
        /*
            r2 = this;
            monitor-enter(r2)
            monitor-enter(r2)     // Catch:{ all -> 0x001a }
            java.util.ArrayList r0 = miui.securitycenter.net.NetworkStatWrapper.getStatsInfo()     // Catch:{ all -> 0x0017 }
            r2.mSpeedDetail = r0     // Catch:{ all -> 0x0017 }
            long r0 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0017 }
            r2.mCurrentRefreshTime = r0     // Catch:{ all -> 0x0017 }
            monitor-exit(r2)     // Catch:{ all -> 0x0017 }
            com.miui.networkassistant.ui.fragment.NetworkSpeedForAppsFragment$RefreshHandler r0 = r2.mHandler     // Catch:{ all -> 0x001a }
            r1 = 1
            r0.sendEmptyMessage(r1)     // Catch:{ all -> 0x001a }
            monitor-exit(r2)
            return
        L_0x0017:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0017 }
            throw r0     // Catch:{ all -> 0x001a }
        L_0x001a:
            r0 = move-exception
            monitor-exit(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.fragment.NetworkSpeedForAppsFragment.refresh():void");
    }

    public void showLoadingView() {
        Activity activity = this.mActivity;
        if (activity != null && !activity.isFinishing() && this.mLoadingDialog == null) {
            this.mLoadingDialog = ProgressDialog.show(this.mActivity, (CharSequence) null, (CharSequence) null, true, false);
            this.mLoadingDialog.setMessage(getResources().getString(R.string.usage_sorted_loading_text));
            this.mLoadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i != 4) {
                        return true;
                    }
                    NetworkSpeedForAppsFragment.this.hideLoadingView();
                    NetworkSpeedForAppsFragment.this.mActivity.finish();
                    return true;
                }
            });
        }
    }
}
