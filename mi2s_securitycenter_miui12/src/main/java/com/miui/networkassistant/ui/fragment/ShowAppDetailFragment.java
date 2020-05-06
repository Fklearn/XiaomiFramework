package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.c.j.x;
import b.b.o.c.a;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.firewall.BackgroundPolicyService;
import com.miui.networkassistant.model.AppDataUsage;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.model.DataUsageConstants;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.networkassistant.model.FirewallRuleSet;
import com.miui.networkassistant.service.FirewallService;
import com.miui.networkassistant.service.IFirewallBinder;
import com.miui.networkassistant.service.wrapper.AppMonitorWrapper;
import com.miui.networkassistant.traffic.statistic.PreSetGroup;
import com.miui.networkassistant.traffic.statistic.StatisticAppTraffic;
import com.miui.networkassistant.ui.base.TrafficRelatedFragment;
import com.miui.networkassistant.ui.dialog.OptionTipDialog;
import com.miui.networkassistant.ui.view.AppDetailTrafficView;
import com.miui.networkassistant.ui.view.SlideButtonToolbarItemView;
import com.miui.networkassistant.ui.view.ToolbarItemView;
import com.miui.networkassistant.ui.view.TrafficDragView;
import com.miui.networkassistant.utils.DateUtil;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.networkassistant.utils.IconCacheHelper;
import com.miui.networkassistant.utils.LabelLoadHelper;
import com.miui.networkassistant.utils.PackageUtil;
import com.miui.networkassistant.utils.TextPrepareUtil;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import miui.widget.DropDownSingleChoiceMenu;

public class ShowAppDetailFragment extends TrafficRelatedFragment implements AppDetailTrafficView.ChartDragListener, View.OnClickListener {
    private static final int MSG_FW_CONNECTED = 2;
    private static final int MSG_TM_CONNECTED = 3;
    private static final int MSG_UPDATE_DATA = 1;
    private static final String TAG = "ShowAppDetailFragment";
    private static final int TITLE_FILED = 2131755315;
    private static final int X_MARGIN_HOUR = 182;
    private static final int X_MARGIN_MONTH = 126;
    private static final int Y_MARGIN = 70;
    private AppDetailTrafficView mAppDetailTrafficView;
    /* access modifiers changed from: private */
    public AppInfo mAppInfo;
    private AppMonitorWrapper.AppMonitorListener mAppMonitorListener = new AppMonitorWrapper.AppMonitorListener() {
        public void onAppListUpdated() {
            ShowAppDetailFragment.this.mHandler.sendEmptyMessage(1);
        }
    };
    private AppMonitorWrapper mAppMonitorWrapper;
    /* access modifiers changed from: private */
    public BackgroundPolicyService mBackgroundPolicyService;
    private SlideButtonToolbarItemView.ToolbarItemClickListener mBackgroundRestrictChangedListener = new BackgroundRestrictChangedListener(this);
    /* access modifiers changed from: private */
    public SlideButtonToolbarItemView mBackgroundRestrictLayout;
    /* access modifiers changed from: private */
    public boolean mDataReady = false;
    protected float mDensity;
    /* access modifiers changed from: private */
    public IFirewallBinder mFirewallBinder;
    private ServiceConnection mFirewallServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IFirewallBinder unused = ShowAppDetailFragment.this.mFirewallBinder = IFirewallBinder.Stub.asInterface(iBinder);
            ShowAppDetailFragment.this.mHandler.sendEmptyMessage(2);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            IFirewallBinder unused = ShowAppDetailFragment.this.mFirewallBinder = null;
        }
    };
    /* access modifiers changed from: private */
    public Handler mHandler = new MainHandler(this);
    private ImageView mIcon;
    private boolean mInited = false;
    private boolean mIsFromAM;
    private boolean mIsManagedProfileApp;
    private TextView mLabel;
    private long[] mLastMonthMobileTrafficPreDayList;
    private long mLastMonthStart;
    private long[] mLastMonthWlanTrafficPreDayList;
    private WindowManager.LayoutParams mLayoutParams;
    private int[] mLocations = new int[2];
    private ToolbarItemView mMiServiceAppDetailItem;
    private SlideButtonToolbarItemView.ToolbarItemClickListener mMobileFirewallChangedListener1 = new MobileFirewallChangedListener1(this);
    private SlideButtonToolbarItemView.ToolbarItemClickListener mMobileFirewallChangedListener2 = new MobileFirewallChangedListener2(this);
    /* access modifiers changed from: private */
    public SlideButtonToolbarItemView[] mMobileFirewallLayout;
    private SparseArray<AppDataUsage[]> mMobileTraffic;
    private long[] mMonthMobileTrafficPerDayList;
    private long[] mMonthWlanTrafficPerDayList;
    private LinearLayout mNetworkTrafficWarningLayout;
    private String mPackageName;
    private String mRealPackageName;
    private int mScreenWidth;
    /* access modifiers changed from: private */
    public StatisticAppTraffic mStatisticAppTraffic;
    private long mThisMonthEnd;
    private long mThisMonthStart;
    private View mTitleLayout;
    private String[] mTitleStrings;
    /* access modifiers changed from: private */
    public int mTitleType = 1;
    private TextView mTitleView;
    private long[] mTodayMobileTrafficPerHourList;
    private long mTodayStart;
    private long[] mTodayWlanTrafficPerHourList;
    private TrafficDragView mTrafficDragView;
    private TextView mVersion;
    private String mVersionStr;
    private SparseArray<AppDataUsage[]> mWifiTraffic;
    private WindowManager mWindowManager;
    private SlideButtonToolbarItemView.ToolbarItemClickListener mWlanFirewallChangedListener = new SlideButtonToolbarItemView.ToolbarItemClickListener() {
        public void onToolbarItemClick(View view, boolean z) {
            if (ShowAppDetailFragment.this.mFirewallBinder != null) {
                try {
                    if (!ShowAppDetailFragment.this.mFirewallBinder.setWifiRule(ShowAppDetailFragment.this.mAppInfo.packageName.toString(), z ? FirewallRule.Allow : FirewallRule.Restrict)) {
                        ShowAppDetailFragment.this.mWlanFirewallLayout.setChecked(false);
                    }
                } catch (RemoteException e) {
                    Log.i(ShowAppDetailFragment.TAG, "mWlanFirewallChangedListener", e);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public SlideButtonToolbarItemView mWlanFirewallLayout;
    private long[] mYesterdayMobileTrafficPerHourList;
    private long[] mYesterdayWlanTrafficPerHourList;

    private static class BackgroundRestrictChangedListener implements SlideButtonToolbarItemView.ToolbarItemClickListener {
        private WeakReference<ShowAppDetailFragment> mFragmentRef;

        public BackgroundRestrictChangedListener(ShowAppDetailFragment showAppDetailFragment) {
            this.mFragmentRef = new WeakReference<>(showAppDetailFragment);
        }

        public void onToolbarItemClick(View view, boolean z) {
            ShowAppDetailFragment showAppDetailFragment = (ShowAppDetailFragment) this.mFragmentRef.get();
            if (showAppDetailFragment != null) {
                try {
                    showAppDetailFragment.showRestrictBackgroundNetDialog(!z, showAppDetailFragment.mAppInfo.uid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class MainHandler extends Handler {
        private WeakReference<ShowAppDetailFragment> mFragmentRef;

        public MainHandler(ShowAppDetailFragment showAppDetailFragment) {
            this.mFragmentRef = new WeakReference<>(showAppDetailFragment);
        }

        public void handleMessage(Message message) {
            ShowAppDetailFragment showAppDetailFragment = (ShowAppDetailFragment) this.mFragmentRef.get();
            if (showAppDetailFragment != null) {
                int i = message.what;
                if (i == 1) {
                    boolean unused = showAppDetailFragment.mDataReady = true;
                } else if (!(i == 2 || i == 3)) {
                    return;
                }
                showAppDetailFragment.initData();
            }
        }
    }

    private static class MobileFirewallChangedListener1 implements SlideButtonToolbarItemView.ToolbarItemClickListener {
        private WeakReference<ShowAppDetailFragment> mFragmentRef;

        public MobileFirewallChangedListener1(ShowAppDetailFragment showAppDetailFragment) {
            this.mFragmentRef = new WeakReference<>(showAppDetailFragment);
        }

        public void onToolbarItemClick(View view, boolean z) {
            ShowAppDetailFragment showAppDetailFragment = (ShowAppDetailFragment) this.mFragmentRef.get();
            if (showAppDetailFragment != null && !showAppDetailFragment.setMobileRule(0, z)) {
                showAppDetailFragment.mMobileFirewallLayout[0].setChecked(false);
            }
        }
    }

    private static class MobileFirewallChangedListener2 implements SlideButtonToolbarItemView.ToolbarItemClickListener {
        private WeakReference<ShowAppDetailFragment> mFragmentRef;

        public MobileFirewallChangedListener2(ShowAppDetailFragment showAppDetailFragment) {
            this.mFragmentRef = new WeakReference<>(showAppDetailFragment);
        }

        public void onToolbarItemClick(View view, boolean z) {
            ShowAppDetailFragment showAppDetailFragment = (ShowAppDetailFragment) this.mFragmentRef.get();
            if (showAppDetailFragment != null && !showAppDetailFragment.setMobileRule(1, z)) {
                showAppDetailFragment.mMobileFirewallLayout[1].setChecked(false);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0107, code lost:
        r4.setData(r7, 1);
        r13.mAppDetailTrafficView.setDurations(r13.mThisMonthStart, r13.mThisMonthEnd);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0141, code lost:
        r2.setData(r9, 1);
        r13.mAppDetailTrafficView.setDurations(r13.mLastMonthStart, r13.mThisMonthStart - 60000);
        r2 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x01a9, code lost:
        r4.setData(r7, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x01ac, code lost:
        updateSpinnerHead(r13.mTitleStrings[r13.mTitleType], r5);
        showBackgroundTraffic(r2, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x01b8, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void applyTrafficData() {
        /*
            r13 = this;
            boolean r0 = r13.isAttatched()
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r0 = r13.mWifiTraffic
            if (r0 == 0) goto L_0x01b9
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r1 = r13.mMobileTraffic
            if (r1 != 0) goto L_0x0011
            goto L_0x01b9
        L_0x0011:
            int r2 = r13.mTitleType
            r3 = 60000(0xea60, double:2.9644E-319)
            r5 = 0
            r7 = 3
            r8 = 2
            r9 = 0
            r10 = 1
            switch(r2) {
                case 0: goto L_0x017d;
                case 1: goto L_0x0150;
                case 2: goto L_0x0115;
                case 3: goto L_0x00db;
                case 4: goto L_0x00ad;
                case 5: goto L_0x007f;
                case 6: goto L_0x0051;
                case 7: goto L_0x0023;
                default: goto L_0x001f;
            }
        L_0x001f:
            r0 = r5
            r2 = r0
            goto L_0x01ac
        L_0x0023:
            java.lang.Object r0 = r0.get(r7)
            com.miui.networkassistant.model.AppDataUsage[] r0 = (com.miui.networkassistant.model.AppDataUsage[]) r0
            r0 = r0[r9]
            long r5 = r0.getTotal()
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r0 = r13.mWifiTraffic
            java.lang.Object r0 = r0.get(r7)
            com.miui.networkassistant.model.AppDataUsage[] r0 = (com.miui.networkassistant.model.AppDataUsage[]) r0
            r0 = r0[r8]
            long r0 = r0.getTotal()
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r2 = r13.mWifiTraffic
            java.lang.Object r2 = r2.get(r7)
            com.miui.networkassistant.model.AppDataUsage[] r2 = (com.miui.networkassistant.model.AppDataUsage[]) r2
            r2 = r2[r10]
            long r2 = r2.getTotal()
            com.miui.networkassistant.ui.view.AppDetailTrafficView r4 = r13.mAppDetailTrafficView
            long[] r7 = r13.mMonthWlanTrafficPerDayList
            goto L_0x0107
        L_0x0051:
            java.lang.Object r0 = r0.get(r8)
            com.miui.networkassistant.model.AppDataUsage[] r0 = (com.miui.networkassistant.model.AppDataUsage[]) r0
            r0 = r0[r9]
            long r5 = r0.getTotal()
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r0 = r13.mWifiTraffic
            java.lang.Object r0 = r0.get(r8)
            com.miui.networkassistant.model.AppDataUsage[] r0 = (com.miui.networkassistant.model.AppDataUsage[]) r0
            r0 = r0[r8]
            long r0 = r0.getTotal()
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r2 = r13.mWifiTraffic
            java.lang.Object r2 = r2.get(r8)
            com.miui.networkassistant.model.AppDataUsage[] r2 = (com.miui.networkassistant.model.AppDataUsage[]) r2
            r2 = r2[r10]
            long r7 = r2.getTotal()
            com.miui.networkassistant.ui.view.AppDetailTrafficView r2 = r13.mAppDetailTrafficView
            long[] r9 = r13.mLastMonthWlanTrafficPreDayList
            goto L_0x0141
        L_0x007f:
            java.lang.Object r0 = r0.get(r10)
            com.miui.networkassistant.model.AppDataUsage[] r0 = (com.miui.networkassistant.model.AppDataUsage[]) r0
            r0 = r0[r9]
            long r5 = r0.getTotal()
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r0 = r13.mWifiTraffic
            java.lang.Object r0 = r0.get(r10)
            com.miui.networkassistant.model.AppDataUsage[] r0 = (com.miui.networkassistant.model.AppDataUsage[]) r0
            r0 = r0[r8]
            long r0 = r0.getTotal()
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r2 = r13.mWifiTraffic
            java.lang.Object r2 = r2.get(r10)
            com.miui.networkassistant.model.AppDataUsage[] r2 = (com.miui.networkassistant.model.AppDataUsage[]) r2
            r2 = r2[r10]
            long r2 = r2.getTotal()
            com.miui.networkassistant.ui.view.AppDetailTrafficView r4 = r13.mAppDetailTrafficView
            long[] r7 = r13.mTodayWlanTrafficPerHourList
            goto L_0x01a9
        L_0x00ad:
            java.lang.Object r0 = r0.get(r9)
            com.miui.networkassistant.model.AppDataUsage[] r0 = (com.miui.networkassistant.model.AppDataUsage[]) r0
            r0 = r0[r9]
            long r5 = r0.getTotal()
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r0 = r13.mWifiTraffic
            java.lang.Object r0 = r0.get(r9)
            com.miui.networkassistant.model.AppDataUsage[] r0 = (com.miui.networkassistant.model.AppDataUsage[]) r0
            r0 = r0[r8]
            long r0 = r0.getTotal()
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r2 = r13.mWifiTraffic
            java.lang.Object r2 = r2.get(r9)
            com.miui.networkassistant.model.AppDataUsage[] r2 = (com.miui.networkassistant.model.AppDataUsage[]) r2
            r2 = r2[r10]
            long r2 = r2.getTotal()
            com.miui.networkassistant.ui.view.AppDetailTrafficView r4 = r13.mAppDetailTrafficView
            long[] r7 = r13.mYesterdayWlanTrafficPerHourList
            goto L_0x01a9
        L_0x00db:
            java.lang.Object r0 = r1.get(r7)
            com.miui.networkassistant.model.AppDataUsage[] r0 = (com.miui.networkassistant.model.AppDataUsage[]) r0
            r0 = r0[r9]
            long r5 = r0.getTotal()
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r0 = r13.mMobileTraffic
            java.lang.Object r0 = r0.get(r7)
            com.miui.networkassistant.model.AppDataUsage[] r0 = (com.miui.networkassistant.model.AppDataUsage[]) r0
            r0 = r0[r8]
            long r0 = r0.getTotal()
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r2 = r13.mMobileTraffic
            java.lang.Object r2 = r2.get(r10)
            com.miui.networkassistant.model.AppDataUsage[] r2 = (com.miui.networkassistant.model.AppDataUsage[]) r2
            r2 = r2[r10]
            long r2 = r2.getTotal()
            com.miui.networkassistant.ui.view.AppDetailTrafficView r4 = r13.mAppDetailTrafficView
            long[] r7 = r13.mMonthMobileTrafficPerDayList
        L_0x0107:
            r4.setData(r7, r10)
            com.miui.networkassistant.ui.view.AppDetailTrafficView r4 = r13.mAppDetailTrafficView
            long r7 = r13.mThisMonthStart
            long r9 = r13.mThisMonthEnd
            r4.setDurations(r7, r9)
            goto L_0x01ac
        L_0x0115:
            java.lang.Object r0 = r1.get(r8)
            com.miui.networkassistant.model.AppDataUsage[] r0 = (com.miui.networkassistant.model.AppDataUsage[]) r0
            r0 = r0[r9]
            long r5 = r0.getTotal()
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r0 = r13.mMobileTraffic
            java.lang.Object r0 = r0.get(r8)
            com.miui.networkassistant.model.AppDataUsage[] r0 = (com.miui.networkassistant.model.AppDataUsage[]) r0
            r0 = r0[r8]
            long r0 = r0.getTotal()
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r2 = r13.mMobileTraffic
            java.lang.Object r2 = r2.get(r8)
            com.miui.networkassistant.model.AppDataUsage[] r2 = (com.miui.networkassistant.model.AppDataUsage[]) r2
            r2 = r2[r10]
            long r7 = r2.getTotal()
            com.miui.networkassistant.ui.view.AppDetailTrafficView r2 = r13.mAppDetailTrafficView
            long[] r9 = r13.mLastMonthMobileTrafficPreDayList
        L_0x0141:
            r2.setData(r9, r10)
            com.miui.networkassistant.ui.view.AppDetailTrafficView r2 = r13.mAppDetailTrafficView
            long r9 = r13.mLastMonthStart
            long r11 = r13.mThisMonthStart
            long r11 = r11 - r3
            r2.setDurations(r9, r11)
            r2 = r7
            goto L_0x01ac
        L_0x0150:
            java.lang.Object r0 = r1.get(r10)
            com.miui.networkassistant.model.AppDataUsage[] r0 = (com.miui.networkassistant.model.AppDataUsage[]) r0
            r0 = r0[r9]
            long r5 = r0.getTotal()
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r0 = r13.mMobileTraffic
            java.lang.Object r0 = r0.get(r10)
            com.miui.networkassistant.model.AppDataUsage[] r0 = (com.miui.networkassistant.model.AppDataUsage[]) r0
            r0 = r0[r8]
            long r0 = r0.getTotal()
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r2 = r13.mMobileTraffic
            java.lang.Object r2 = r2.get(r10)
            com.miui.networkassistant.model.AppDataUsage[] r2 = (com.miui.networkassistant.model.AppDataUsage[]) r2
            r2 = r2[r10]
            long r2 = r2.getTotal()
            com.miui.networkassistant.ui.view.AppDetailTrafficView r4 = r13.mAppDetailTrafficView
            long[] r7 = r13.mTodayMobileTrafficPerHourList
            goto L_0x01a9
        L_0x017d:
            java.lang.Object r0 = r1.get(r9)
            com.miui.networkassistant.model.AppDataUsage[] r0 = (com.miui.networkassistant.model.AppDataUsage[]) r0
            r0 = r0[r9]
            long r5 = r0.getTotal()
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r0 = r13.mMobileTraffic
            java.lang.Object r0 = r0.get(r9)
            com.miui.networkassistant.model.AppDataUsage[] r0 = (com.miui.networkassistant.model.AppDataUsage[]) r0
            r0 = r0[r8]
            long r0 = r0.getTotal()
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r2 = r13.mMobileTraffic
            java.lang.Object r2 = r2.get(r9)
            com.miui.networkassistant.model.AppDataUsage[] r2 = (com.miui.networkassistant.model.AppDataUsage[]) r2
            r2 = r2[r10]
            long r2 = r2.getTotal()
            com.miui.networkassistant.ui.view.AppDetailTrafficView r4 = r13.mAppDetailTrafficView
            long[] r7 = r13.mYesterdayMobileTrafficPerHourList
        L_0x01a9:
            r4.setData(r7, r9)
        L_0x01ac:
            java.lang.String[] r4 = r13.mTitleStrings
            int r7 = r13.mTitleType
            r4 = r4[r7]
            r13.updateSpinnerHead(r4, r5)
            r13.showBackgroundTraffic(r2, r0)
            return
        L_0x01b9:
            java.lang.String r0 = "ShowAppDetailFragment"
            java.lang.String r1 = "data is null, initialization data..."
            android.util.Log.w(r0, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.fragment.ShowAppDetailFragment.applyTrafficData():void");
    }

    private void bindFirewallService() {
        Activity activity = this.mActivity;
        g.a((Context) activity, new Intent(activity, FirewallService.class), this.mFirewallServiceConnection, 1, B.k());
    }

    private void buildRestrictAndroidTipDialog(final String str, final int i) {
        new OptionTipDialog(this.mActivity, new OptionTipDialog.OptionDialogListener() {
            public void onOptionUpdated(boolean z) {
                if (z) {
                    try {
                        ShowAppDetailFragment.this.mFirewallBinder.setMobileRule(str, FirewallRule.Restrict, ShowAppDetailFragment.this.mSlotNum);
                        ShowAppDetailFragment.this.mMobileFirewallLayout[i].setChecked(false);
                    } catch (RemoteException e) {
                        Log.i(ShowAppDetailFragment.TAG, "buildRestrictAndroidTipDialog", e);
                    }
                } else {
                    ShowAppDetailFragment.this.mMobileFirewallLayout[i].setChecked(true);
                }
            }
        }).buildShowDialog(this.mAppContext.getString(R.string.firewall_restrict_android_dialog_title), this.mAppContext.getString(R.string.firewall_restrict_android_dialog_content));
    }

    private void checkPackageAvailable() {
        if (!PackageUtil.isInstalledPackage(this.mActivity, this.mRealPackageName) && !this.mIsManagedProfileApp) {
            this.mActivity.finish();
        }
    }

    private double getScreenX(float f) {
        double d2 = (double) f;
        double xMargin = ((double) getXMargin()) / 2.0d;
        int i = this.mScreenWidth;
        if (d2 + xMargin > ((double) i)) {
            d2 = ((double) i) - xMargin;
        } else if (d2 - xMargin < 0.0d) {
            d2 = xMargin;
        }
        return d2 - xMargin;
    }

    private String getTimeInterval(int i) {
        return isMonthTrafficType() ? DateUtil.dayInterval(this.mThisMonthStart, i) : isLastMonthTrafficType() ? DateUtil.dayInterval(this.mLastMonthStart, i) : DateUtil.timeInterval(this.mTodayStart, i);
    }

    private long getTraffic(int i) {
        long[] jArr;
        switch (this.mTitleType) {
            case 0:
                jArr = this.mYesterdayMobileTrafficPerHourList;
                break;
            case 1:
                jArr = this.mTodayMobileTrafficPerHourList;
                break;
            case 2:
                jArr = this.mLastMonthMobileTrafficPreDayList;
                break;
            case 3:
                jArr = this.mMonthMobileTrafficPerDayList;
                break;
            case 4:
                jArr = this.mYesterdayWlanTrafficPerHourList;
                break;
            case 5:
                jArr = this.mTodayWlanTrafficPerHourList;
                break;
            case 6:
                jArr = this.mLastMonthWlanTrafficPreDayList;
                break;
            case 7:
                jArr = this.mMonthWlanTrafficPerDayList;
                break;
            default:
                return 0;
        }
        if (jArr == null || i < 0 || i >= jArr.length) {
            return 0;
        }
        return jArr[i];
    }

    private int getXMargin() {
        return (int) (this.mDensity * ((isMonthTrafficType() || isLastMonthTrafficType()) ? 126.0f : 182.0f));
    }

    private void initBundleData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mPackageName = arguments.getString("package_name");
            this.mRealPackageName = PackageUtil.getRealPackageName(this.mPackageName);
            this.mIsManagedProfileApp = "magaged_profile_package".equals(this.mRealPackageName);
            this.mTitleType = arguments.getInt(DataUsageConstants.BUNDLE_TITLE_TYPE, 1) + (arguments.getInt(DataUsageConstants.BUNDLE_SORT_TYPE, 0) * 4);
        }
    }

    /* access modifiers changed from: private */
    public void initData() {
        Activity activity = this.mActivity;
        if (activity != null && !activity.isFinishing() && !this.mInited && this.mDataReady && this.mServiceConnected && this.mFirewallBinder != null) {
            this.mInited = true;
            if (PackageUtil.isManagedProfileApp(this.mPackageName)) {
                this.mAppInfo = this.mIsManagedProfileApp ? new AppInfo((CharSequence) this.mRealPackageName) : this.mAppMonitorWrapper.getAppInfoByPackageName(this.mRealPackageName);
                this.mAppInfo.uid = PackageUtil.parseUidByPackageName(this.mPackageName);
            } else {
                this.mAppInfo = this.mAppMonitorWrapper.getAppInfoByPackageName(this.mPackageName);
            }
            AppInfo appInfo = this.mAppInfo;
            if (appInfo == null) {
                finish();
                return;
            }
            String charSequence = appInfo.packageName.toString();
            IconCacheHelper.getInstance().setIconToImageView(this.mIcon, charSequence);
            this.mLabel.setText(LabelLoadHelper.loadLabel(this.mAppContext, charSequence));
            String f = x.f(this.mAppContext, charSequence);
            TextView textView = this.mVersion;
            textView.setText(this.mVersionStr + f);
            this.mStatisticAppTraffic = new StatisticAppTraffic(this.mAppContext, this.mSimUserInfos[this.mSlotNum].getImsi());
            updateTraffic();
            initFirewallData();
            if (this.mIsFromAM) {
                this.mNetworkTrafficWarningLayout.setVisibility(PreSetGroup.isGroupUid(this.mAppInfo.uid) ? 0 : 8);
            }
        }
    }

    private void initDrag() {
        this.mDensity = getResources().getDisplayMetrics().density;
        this.mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        if (this.mWindowManager == null) {
            this.mWindowManager = (WindowManager) this.mActivity.getSystemService("window");
        }
        int[] iArr = this.mLocations;
        if (iArr == null) {
            this.mAppDetailTrafficView.getLocationOnScreen(iArr);
        }
    }

    private void initDragView(float f, float f2, int i) {
        if (getTraffic(i) != 0) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.x = (int) (f - (((float) getXMargin()) / 2.0f));
            layoutParams.y = (int) (f2 - (this.mDensity * 70.0f));
            layoutParams.type = 1002;
            layoutParams.flags = 768;
            layoutParams.format = -3;
            layoutParams.gravity = 8388659;
            layoutParams.width = getXMargin();
            this.mLayoutParams = layoutParams;
            this.mTrafficDragView = new TrafficDragView(this.mActivity);
            String timeInterval = getTimeInterval(i);
            this.mTrafficDragView.setText(String.format("%s %s", new Object[]{timeInterval, FormatBytesUtil.formatBytes(this.mAppContext, getTraffic(i))}));
            this.mWindowManager.addView(this.mTrafficDragView, layoutParams);
        }
    }

    private void initFirewallData() {
        SlideButtonToolbarItemView slideButtonToolbarItemView;
        this.mMobileFirewallLayout = new SlideButtonToolbarItemView[2];
        this.mMobileFirewallLayout[0] = (SlideButtonToolbarItemView) findViewById(R.id.layout_mobile_setting1);
        this.mMobileFirewallLayout[1] = (SlideButtonToolbarItemView) findViewById(R.id.layout_mobile_setting2);
        if (!DeviceUtil.IS_DUAL_CARD || !this.mSimCardHelper.isDualSimInserted()) {
            int currentMobileSlotNum = this.mSimCardHelper.getCurrentMobileSlotNum();
            if (currentMobileSlotNum == 0) {
                this.mMobileFirewallLayout[0].setName((int) R.string.app_mobile);
                this.mMobileFirewallLayout[0].setToolbarItemClickListener(this.mMobileFirewallChangedListener1);
                slideButtonToolbarItemView = this.mMobileFirewallLayout[1];
            } else if (currentMobileSlotNum == 1) {
                this.mMobileFirewallLayout[1].setName((int) R.string.app_mobile);
                this.mMobileFirewallLayout[1].setToolbarItemClickListener(this.mMobileFirewallChangedListener2);
                slideButtonToolbarItemView = this.mMobileFirewallLayout[0];
            }
            slideButtonToolbarItemView.setVisibility(8);
        } else {
            setMobileFirewallTile(0);
            this.mMobileFirewallLayout[0].setToolbarItemClickListener(this.mMobileFirewallChangedListener1);
            setMobileFirewallTile(1);
            this.mMobileFirewallLayout[1].setToolbarItemClickListener(this.mMobileFirewallChangedListener2);
        }
        this.mWlanFirewallLayout = (SlideButtonToolbarItemView) findViewById(R.id.layout_wifi_setting);
        this.mWlanFirewallLayout.setToolbarItemClickListener(this.mWlanFirewallChangedListener);
        this.mWlanFirewallLayout.setName((int) R.string.app_wifi);
        this.mBackgroundRestrictLayout = (SlideButtonToolbarItemView) findViewById(R.id.layout_network_setting);
        this.mBackgroundRestrictLayout.setToolbarItemClickListener(this.mBackgroundRestrictChangedListener);
        this.mBackgroundRestrictLayout.setName((int) R.string.permit_running_network);
        this.mBackgroundRestrictLayout.setSummaryVisibility(0);
        if (this.mIsManagedProfileApp) {
            this.mMobileFirewallLayout[0].setVisibility(8);
            this.mMobileFirewallLayout[1].setVisibility(8);
            this.mWlanFirewallLayout.setVisibility(8);
            this.mBackgroundRestrictLayout.setVisibility(8);
            return;
        }
        AppInfo appInfo = this.mAppInfo;
        if (appInfo == null) {
            finish();
            return;
        }
        FirewallRuleSet firewallRuleSet = null;
        try {
            if (this.mFirewallBinder != null) {
                firewallRuleSet = this.mFirewallBinder.getRule(appInfo.packageName.toString());
            }
        } catch (RemoteException e) {
            Log.i(TAG, "get firewall rule set", e);
        }
        if (firewallRuleSet == null) {
            finish();
            return;
        }
        this.mMobileFirewallLayout[0].setChecked(firewallRuleSet.mobileRule == FirewallRule.Allow);
        if (DeviceUtil.IS_DUAL_CARD) {
            this.mMobileFirewallLayout[1].setChecked(firewallRuleSet.mobileRule2 == FirewallRule.Allow);
        }
        this.mWlanFirewallLayout.setChecked(firewallRuleSet.wifiRule == FirewallRule.Allow);
        this.mBackgroundPolicyService = BackgroundPolicyService.getInstance(this.mActivity.getApplicationContext());
        boolean isAppRestrictBackground = this.mBackgroundPolicyService.isAppRestrictBackground(this.mRealPackageName, this.mAppInfo.uid);
        this.mBackgroundRestrictLayout.setChecked(!isAppRestrictBackground);
        this.mWlanFirewallLayout.setToolbarItemEnable(true);
        this.mBackgroundRestrictLayout.setToolbarItemEnable(true);
        if (this.mAppInfo.isSystemApp) {
            this.mWlanFirewallLayout.setToolbarItemEnable(false);
            if (firewallRuleSet.wifiRule != FirewallRule.Allow) {
                try {
                    if (this.mFirewallBinder != null) {
                        this.mFirewallBinder.setWifiRule(this.mAppInfo.packageName.toString(), FirewallRule.Allow);
                    }
                } catch (RemoteException e2) {
                    Log.i(TAG, "set firewall rule", e2);
                }
                this.mWlanFirewallLayout.setChecked(true);
            }
            if (B.a(this.mAppInfo.uid) < 10000 || PreSetGroup.isPrePolicyPackage(this.mRealPackageName)) {
                this.mBackgroundRestrictLayout.setToolbarItemEnable(false);
                if (isAppRestrictBackground) {
                    try {
                        this.mBackgroundPolicyService.setAppRestrictBackground(this.mAppInfo.uid, false);
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
            }
        }
        if (PreSetGroup.getPreFirewallWhiteList().contains(this.mRealPackageName)) {
            this.mMobileFirewallLayout[0].setToolbarItemEnable(false);
            this.mMobileFirewallLayout[1].setToolbarItemEnable(false);
            this.mWlanFirewallLayout.setToolbarItemEnable(false);
            this.mBackgroundRestrictLayout.setToolbarItemEnable(false);
        }
        if (a.a(getContext(), this.mPackageName)) {
            Log.d("Enterprise", "Net config is restricted for package" + this.mPackageName);
            SlideButtonToolbarItemView[] slideButtonToolbarItemViewArr = this.mMobileFirewallLayout;
            int length = slideButtonToolbarItemViewArr.length;
            for (int i = 0; i < length; i++) {
                slideButtonToolbarItemViewArr[i].setToolbarItemEnable(false);
            }
            this.mWlanFirewallLayout.setToolbarItemEnable(false);
            this.mBackgroundRestrictLayout.setToolbarItemEnable(false);
        }
    }

    private boolean isLastMonthTrafficType() {
        int i = this.mTitleType;
        return i == 2 || i == 6;
    }

    private boolean isMonthTrafficType() {
        int i = this.mTitleType;
        return i == 3 || i == 7;
    }

    private boolean isRestrictAndroidSystemApp(String str) {
        FirewallRule firewallRule;
        try {
            firewallRule = this.mFirewallBinder.getMobileRule(str, this.mSlotNum);
        } catch (RemoteException e) {
            Log.i(TAG, "isRestrictAndroidSystemApp", e);
            firewallRule = null;
        }
        if (TextUtils.equals(str, Constants.System.ANDROID_PACKAGE_NAME)) {
            return firewallRule == null || firewallRule == FirewallRule.Allow;
        }
        return false;
    }

    private void setMobileFirewallTile(int i) {
        Context context = this.mAppContext;
        this.mMobileFirewallLayout[i].setName(TextPrepareUtil.getDualCardTitle(context, context.getString(R.string.app_mobile), i));
    }

    /* access modifiers changed from: private */
    public boolean setMobileRule(int i, boolean z) {
        if (this.mFirewallBinder == null) {
            return false;
        }
        try {
            String charSequence = this.mAppInfo.packageName.toString();
            if (!isRestrictAndroidSystemApp(charSequence) || z) {
                return this.mFirewallBinder.setMobileRule(charSequence, z ? FirewallRule.Allow : FirewallRule.Restrict, i);
            }
            buildRestrictAndroidTipDialog(charSequence, i);
            return false;
        } catch (RemoteException e) {
            Log.i(TAG, "setMobileRule", e);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void showAppDetail() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("package", this.mRealPackageName, (String) null));
        intent.setClassName("com.android.settings", "com.android.settings.applications.InstalledAppDetails");
        startActivity(intent);
    }

    private void showBackgroundTraffic(long j, long j2) {
        this.mBackgroundRestrictLayout.setSummary(String.format(getString(R.string.foreground_traffic), new Object[]{FormatBytesUtil.formatBytes(this.mAppContext, j)}) + "  " + String.format(getString(R.string.background_traffic), new Object[]{FormatBytesUtil.formatBytes(this.mAppContext, j2)}));
    }

    /* access modifiers changed from: private */
    public void showRestrictBackgroundNetDialog(boolean z, final int i) {
        if (z) {
            new OptionTipDialog(this.mActivity, new OptionTipDialog.OptionDialogListener() {
                public void onOptionUpdated(boolean z) {
                    ShowAppDetailFragment.this.mBackgroundRestrictLayout.setChecked(!z);
                    ShowAppDetailFragment.this.mBackgroundPolicyService.setAppRestrictBackground(i, z);
                }
            }).buildShowDialog(this.mAppContext.getString(R.string.firewall_restrict_android_dialog_title), this.mAppContext.getString(R.string.background_restrict_app_dialog_message));
            return;
        }
        this.mBackgroundPolicyService.setAppRestrictBackground(this.mAppInfo.uid, false);
        this.mBackgroundRestrictLayout.setChecked(true);
    }

    private void showTrafficMenuItem() {
        DropDownSingleChoiceMenu dropDownSingleChoiceMenu = new DropDownSingleChoiceMenu(this.mActivity);
        dropDownSingleChoiceMenu.setItems(this.mTitleStrings);
        dropDownSingleChoiceMenu.setSelectedItem(this.mTitleType);
        dropDownSingleChoiceMenu.setAnchorView(this.mTitleLayout);
        dropDownSingleChoiceMenu.setOnMenuListener(new DropDownSingleChoiceMenu.OnMenuListener() {
            public void onDismiss() {
            }

            public void onItemSelected(DropDownSingleChoiceMenu dropDownSingleChoiceMenu, int i) {
                int unused = ShowAppDetailFragment.this.mTitleType = i;
                ShowAppDetailFragment.this.applyTrafficData();
                dropDownSingleChoiceMenu.dismiss();
            }

            public void onShow() {
            }
        });
        dropDownSingleChoiceMenu.show();
    }

    public static void startAppDetailFragment(Activity activity, String str) {
        startAppDetailFragment(activity, str, 1, 0);
    }

    public static void startAppDetailFragment(Activity activity, String str, int i, int i2) {
        Bundle bundle = new Bundle();
        bundle.putString("package_name", str);
        bundle.putInt(DataUsageConstants.BUNDLE_TITLE_TYPE, i);
        bundle.putInt(DataUsageConstants.BUNDLE_SORT_TYPE, i2);
        b.b.c.c.b.g.startWithFragment(activity, ShowAppDetailFragment.class, bundle);
    }

    private void unBindFirewallService() {
        this.mActivity.unbindService(this.mFirewallServiceConnection);
    }

    /* access modifiers changed from: private */
    public void updateAppTraffic() {
        boolean z;
        int i;
        StatisticAppTraffic statisticAppTraffic;
        if (this.mIsManagedProfileApp) {
            statisticAppTraffic = this.mStatisticAppTraffic;
            i = this.mAppInfo.uid;
            z = true;
        } else {
            statisticAppTraffic = this.mStatisticAppTraffic;
            i = this.mAppInfo.uid;
            z = false;
        }
        this.mMobileTraffic = statisticAppTraffic.buildMobileDataUsage(i, z);
        this.mWifiTraffic = this.mStatisticAppTraffic.buildWifiDataUsage(this.mAppInfo.uid, z);
    }

    private void updateDragView(float f, float f2, int i) {
        String timeInterval = getTimeInterval(i);
        this.mTrafficDragView.setText(String.format("%s %s", new Object[]{timeInterval, FormatBytesUtil.formatBytes(this.mAppContext, getTraffic(i))}));
        this.mLayoutParams.width = getXMargin();
        this.mLayoutParams.x = (int) (f - (((float) getXMargin()) / 2.0f));
        this.mLayoutParams.y = (int) (f2 - (this.mDensity * 70.0f));
        if (getTraffic(i) == 0) {
            this.mWindowManager.removeView(this.mTrafficDragView);
            this.mTrafficDragView = null;
            return;
        }
        this.mWindowManager.updateViewLayout(this.mTrafficDragView, this.mLayoutParams);
    }

    /* access modifiers changed from: private */
    public void updateLastMonthTraffic() {
        SparseArray<AppDataUsage[]> appLastMonthPerDayTraffic = this.mStatisticAppTraffic.getAppLastMonthPerDayTraffic(this.mAppInfo.uid);
        if (this.mLastMonthWlanTrafficPreDayList == null) {
            this.mLastMonthWlanTrafficPreDayList = new long[31];
            this.mLastMonthMobileTrafficPreDayList = new long[31];
        }
        if (appLastMonthPerDayTraffic != null) {
            for (int i = 0; i < 31; i++) {
                AppDataUsage[] appDataUsageArr = appLastMonthPerDayTraffic.get(i);
                if (appDataUsageArr != null) {
                    this.mLastMonthMobileTrafficPreDayList[i] = appDataUsageArr[0].getTotal();
                    this.mLastMonthWlanTrafficPreDayList[i] = appDataUsageArr[1].getTotal();
                } else {
                    this.mLastMonthMobileTrafficPreDayList[i] = 0;
                    this.mLastMonthWlanTrafficPreDayList[i] = 0;
                }
            }
        }
    }

    private void updateSpinnerHead(String str, long j) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("( ");
        sb.append(FormatBytesUtil.formatBytes(this.mAppContext, j));
        sb.append(" )");
        this.mTitleView.setText(sb);
    }

    /* access modifiers changed from: private */
    public void updateThisMonthTraffic() {
        SparseArray<AppDataUsage[]> appThisMonthPerDayTraffic = this.mStatisticAppTraffic.getAppThisMonthPerDayTraffic(this.mAppInfo.uid);
        if (this.mMonthWlanTrafficPerDayList == null) {
            this.mMonthWlanTrafficPerDayList = new long[31];
            this.mMonthMobileTrafficPerDayList = new long[31];
        }
        if (appThisMonthPerDayTraffic != null) {
            for (int i = 0; i < 31; i++) {
                AppDataUsage[] appDataUsageArr = appThisMonthPerDayTraffic.get(i);
                if (appDataUsageArr != null) {
                    this.mMonthMobileTrafficPerDayList[i] = appDataUsageArr[0].getTotal();
                    this.mMonthWlanTrafficPerDayList[i] = appDataUsageArr[1].getTotal();
                } else {
                    this.mMonthMobileTrafficPerDayList[i] = 0;
                    this.mMonthWlanTrafficPerDayList[i] = 0;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateTodayTraffic() {
        SparseArray<AppDataUsage[]> appTodayPerHourTraffic = this.mStatisticAppTraffic.getAppTodayPerHourTraffic(this.mAppInfo.uid);
        if (this.mTodayMobileTrafficPerHourList == null) {
            this.mTodayMobileTrafficPerHourList = new long[24];
            this.mTodayWlanTrafficPerHourList = new long[24];
        }
        if (appTodayPerHourTraffic != null) {
            for (int i = 0; i < 24; i++) {
                AppDataUsage[] appDataUsageArr = appTodayPerHourTraffic.get(i);
                if (appDataUsageArr != null) {
                    this.mTodayMobileTrafficPerHourList[i] = appDataUsageArr[0].getTotal();
                    this.mTodayWlanTrafficPerHourList[i] = appDataUsageArr[1].getTotal();
                } else {
                    this.mTodayMobileTrafficPerHourList[i] = 0;
                    this.mTodayWlanTrafficPerHourList[i] = 0;
                }
            }
        }
    }

    private void updateTraffic() {
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                ShowAppDetailFragment.this.updateAppTraffic();
                ShowAppDetailFragment.this.updateYesterdayTraffic();
                ShowAppDetailFragment.this.updateTodayTraffic();
                ShowAppDetailFragment.this.updateThisMonthTraffic();
                ShowAppDetailFragment.this.updateLastMonthTraffic();
                return null;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Void voidR) {
                super.onPostExecute(voidR);
                if (ShowAppDetailFragment.this.isAttatched()) {
                    ShowAppDetailFragment.this.applyTrafficData();
                }
                if (ShowAppDetailFragment.this.mStatisticAppTraffic != null) {
                    ShowAppDetailFragment.this.mStatisticAppTraffic.closeSession();
                }
            }

            /* access modifiers changed from: protected */
            public void onPreExecute() {
                super.onPreExecute();
            }
        }.execute(new Void[0]);
    }

    /* access modifiers changed from: private */
    public void updateYesterdayTraffic() {
        SparseArray<AppDataUsage[]> appYesterdayPerHourTraffic = this.mStatisticAppTraffic.getAppYesterdayPerHourTraffic(this.mAppInfo.uid);
        if (this.mYesterdayMobileTrafficPerHourList == null) {
            this.mYesterdayMobileTrafficPerHourList = new long[24];
            this.mYesterdayWlanTrafficPerHourList = new long[24];
        }
        if (appYesterdayPerHourTraffic != null) {
            for (int i = 0; i < 24; i++) {
                AppDataUsage[] appDataUsageArr = appYesterdayPerHourTraffic.get(i);
                if (appDataUsageArr != null) {
                    this.mYesterdayMobileTrafficPerHourList[i] = appDataUsageArr[0].getTotal();
                    this.mYesterdayWlanTrafficPerHourList[i] = appDataUsageArr[1].getTotal();
                } else {
                    this.mYesterdayMobileTrafficPerHourList[i] = 0;
                    this.mYesterdayWlanTrafficPerHourList[i] = 0;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.mIcon = (ImageView) findViewById(R.id.imageview_icon);
        this.mLabel = (TextView) findViewById(R.id.textview_appname);
        this.mVersion = (TextView) findViewById(R.id.textview_version);
        this.mVersionStr = this.mAppContext.getString(R.string.app_version);
        if ("com.xiaomi.xmsf".equals(this.mRealPackageName)) {
            this.mMiServiceAppDetailItem = (ToolbarItemView) findViewById(R.id.mi_service_app_detail);
            this.mMiServiceAppDetailItem.setVisibility(0);
            this.mMiServiceAppDetailItem.setName((int) R.string.mi_service_app_detail);
            this.mMiServiceAppDetailItem.setOnClickListener(this);
        }
        this.mAppDetailTrafficView = (AppDetailTrafficView) findViewById(R.id.detail_view);
        this.mAppDetailTrafficView.setChartDragListener(this);
        this.mNetworkTrafficWarningLayout = (LinearLayout) findViewById(R.id.network_traffic_warning_layout);
        this.mTitleLayout = findViewById(R.id.layout_show);
        this.mTitleView = (TextView) findViewById(R.id.list_spinner_title);
        this.mTitleLayout.setOnClickListener(this);
        this.mTitleStrings = getResources().getStringArray(R.array.date_of_app_all);
        this.mTodayStart = DateUtil.getTodayTimeMillis();
        this.mThisMonthStart = DateUtil.getThisMonthBeginTimeMillis(1);
        this.mThisMonthEnd = DateUtil.getThisMonthEndTimeMillis(1) - 86400000;
        this.mLastMonthStart = DateUtil.getLastMonthBeginTimeMillis(1);
    }

    public void onClick(View view) {
        if (view == this.mTitleLayout) {
            showTrafficMenuItem();
        } else if (view == this.mMiServiceAppDetailItem) {
            Bundle bundle = new Bundle();
            bundle.putString("package_name", this.mAppInfo.packageName.toString());
            bundle.putInt(DataUsageConstants.BUNDLE_TITLE_TYPE, this.mTitleType);
            b.b.c.c.b.g.startWithFragment(this.mActivity, ShowMiServiceAppDetailFragment.class, bundle);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        initBundleData();
        this.mIsFromAM = this.mActivity.getIntent().getBooleanExtra("from_appmanager", false);
        this.mAppMonitorWrapper = AppMonitorWrapper.getInstance(this.mAppContext);
        this.mAppMonitorWrapper.registerLisener(this.mAppMonitorListener);
        bindFirewallService();
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.fragment_app_detail;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        if (this.mIsFromAM || this.mIsManagedProfileApp) {
            return 0;
        }
        actionBar.setDisplayOptions(16, 16);
        ImageView imageView = new ImageView(this.mActivity);
        imageView.setBackgroundResource(R.drawable.app_manager_info_icon);
        imageView.setContentDescription(this.mAppContext.getString(R.string.sorted_dialog_title));
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ShowAppDetailFragment.this.showAppDetail();
            }
        });
        if (!(actionBar instanceof miui.app.ActionBar)) {
            return 0;
        }
        ((miui.app.ActionBar) actionBar).setEndView(imageView);
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        unBindFirewallService();
        this.mAppMonitorWrapper.unRegisterLisener(this.mAppMonitorListener);
        this.mHandler.removeCallbacksAndMessages((Object) null);
    }

    public void onDragEnd() {
        TrafficDragView trafficDragView = this.mTrafficDragView;
        if (trafficDragView != null) {
            this.mWindowManager.removeView(trafficDragView);
            this.mTrafficDragView = null;
        }
    }

    public void onDragStart(float f, float f2, int i) {
        initDrag();
        if (this.mTrafficDragView == null) {
            initDragView(f, f2, i);
        } else {
            updateDragView(f, f2, i);
        }
    }

    public void onResume() {
        super.onResume();
        checkPackageAvailable();
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.app_detail;
    }

    /* access modifiers changed from: protected */
    public void onTrafficManageServiceConnected() {
        this.mHandler.sendEmptyMessage(3);
    }
}
