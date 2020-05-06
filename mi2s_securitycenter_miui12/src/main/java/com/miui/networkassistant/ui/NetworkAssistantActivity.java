package com.miui.networkassistant.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import b.b.c.c.b.g;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.model.TrafficUsedStatus;
import com.miui.networkassistant.model.VirtualSimInfo;
import com.miui.networkassistant.service.ITrafficCornBinder;
import com.miui.networkassistant.service.ITrafficManageBinder;
import com.miui.networkassistant.service.wrapper.TmBinderCacher;
import com.miui.networkassistant.service.wrapper.TrafficCornBinderListenerHost;
import com.miui.networkassistant.traffic.purchase.CooperationManager;
import com.miui.networkassistant.ui.activity.BaseStatsActivity;
import com.miui.networkassistant.ui.adapter.MainTrafficViewPagerAdapter;
import com.miui.networkassistant.ui.dialog.CommonDialog;
import com.miui.networkassistant.ui.dialog.TrafficInputDialog;
import com.miui.networkassistant.ui.fragment.OperatorSettingFragment;
import com.miui.networkassistant.ui.fragment.PackageSettingFragment;
import com.miui.networkassistant.ui.fragment.SettingFragment;
import com.miui.networkassistant.ui.view.MainToolbarItemView;
import com.miui.networkassistant.ui.view.MainTrafficUsedView;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.MiSimUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.networkassistant.utils.TrafficUpdateUtil;
import com.miui.networkassistant.utils.VirtualSimUtil;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import miui.app.ActionBar;

public class NetworkAssistantActivity extends BaseStatsActivity {
    private static final String TAG = "NAMainActivity";
    /* access modifiers changed from: private */
    public int mCurrentOperateSlotNum = 0;
    /* access modifiers changed from: private */
    public boolean mDataReady = false;
    /* access modifiers changed from: private */
    public UiHandler mHandler;
    private TrafficInputDialog mInputDialog;
    /* access modifiers changed from: private */
    public boolean mIsDualCard = DeviceUtil.IS_DUAL_CARD;
    private ViewPager mMainViewPager;
    /* access modifiers changed from: private */
    public MobileStatus[] mMobileStatus;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x009a, code lost:
            com.miui.networkassistant.utils.AnalyticsHelper.trackMainButtonClickCountEvent(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:4:0x0024, code lost:
            com.miui.networkassistant.ui.activity.ShowSmsDetailActivity.startSmsDetailActivity(r4, r0, r1);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onClick(android.view.View r4) {
            /*
                r3 = this;
                com.miui.networkassistant.ui.NetworkAssistantActivity r0 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                int r0 = r0.mCurrentOperateSlotNum
                com.miui.networkassistant.dual.Sim.operateOnSlotNum(r0)
                int r4 = r4.getId()
                r0 = 0
                switch(r4) {
                    case 2131296563: goto L_0x00c7;
                    case 2131297168: goto L_0x009e;
                    case 2131297301: goto L_0x0082;
                    case 2131297302: goto L_0x0042;
                    case 2131297303: goto L_0x0029;
                    case 2131297393: goto L_0x001c;
                    case 2131297800: goto L_0x0013;
                    default: goto L_0x0011;
                }
            L_0x0011:
                goto L_0x00cc
            L_0x0013:
                com.miui.networkassistant.ui.NetworkAssistantActivity r4 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                android.app.Activity r4 = r4.mActivity
                java.lang.String r1 = "na_main_text_error"
                goto L_0x0024
            L_0x001c:
                com.miui.networkassistant.ui.NetworkAssistantActivity r4 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                android.app.Activity r4 = r4.mActivity
                java.lang.String r1 = "na_main_traffic_remained"
            L_0x0024:
                com.miui.networkassistant.ui.activity.ShowSmsDetailActivity.startSmsDetailActivity(r4, r0, r1)
                goto L_0x00cc
            L_0x0029:
                com.miui.networkassistant.ui.NetworkAssistantActivity r4 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                android.app.Activity r4 = r4.mActivity
                android.content.Intent r0 = new android.content.Intent
                com.miui.networkassistant.ui.NetworkAssistantActivity r1 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                android.content.Context r1 = r1.mAppContext
                java.lang.Class<com.miui.networkassistant.ui.activity.TrafficSortedActivity> r2 = com.miui.networkassistant.ui.activity.TrafficSortedActivity.class
                r0.<init>(r1, r2)
                r4.startActivity(r0)
                java.lang.String r4 = "flow_list"
                goto L_0x009a
            L_0x0042:
                com.miui.networkassistant.ui.NetworkAssistantActivity r4 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                boolean r4 = r4.mDataReady
                if (r4 != 0) goto L_0x004c
                goto L_0x00cc
            L_0x004c:
                com.miui.networkassistant.ui.NetworkAssistantActivity r4 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                android.content.Context r4 = r4.mAppContext
                com.miui.networkassistant.ui.NetworkAssistantActivity r0 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                int r0 = r0.mCurrentOperateSlotNum
                boolean r4 = com.miui.networkassistant.utils.MiSimUtil.isMiSimEnable(r4, r0)
                if (r4 == 0) goto L_0x006a
                com.miui.networkassistant.ui.NetworkAssistantActivity r4 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                android.content.Context r4 = r4.mAppContext
                java.lang.String r0 = "assistInfo"
                com.miui.networkassistant.utils.VirtualSimUtil.startVirtualSimActivity(r4, r0)
                goto L_0x00cc
            L_0x006a:
                com.miui.networkassistant.ui.NetworkAssistantActivity r4 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                android.app.Activity r4 = r4.mActivity
                com.miui.networkassistant.ui.NetworkAssistantActivity r0 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                int r1 = r0.mCurrentOperateSlotNum
                com.miui.networkassistant.config.SimUserInfo r0 = r0.getSimUserInfo(r1)
                java.lang.String r1 = "100001"
                com.miui.networkassistant.traffic.purchase.CooperationManager.navigationToTrafficPurchasePage(r4, r0, r1)
                java.lang.String r4 = "flow_buy"
                goto L_0x009a
            L_0x0082:
                com.miui.networkassistant.ui.NetworkAssistantActivity r4 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                android.app.Activity r4 = r4.mActivity
                android.content.Intent r0 = new android.content.Intent
                com.miui.networkassistant.ui.NetworkAssistantActivity r1 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                android.content.Context r1 = r1.mAppContext
                java.lang.Class<com.miui.networkassistant.ui.activity.FirewallActivity> r2 = com.miui.networkassistant.ui.activity.FirewallActivity.class
                r0.<init>(r1, r2)
                r4.startActivity(r0)
                java.lang.String r4 = "net_control"
            L_0x009a:
                com.miui.networkassistant.utils.AnalyticsHelper.trackMainButtonClickCountEvent(r4)
                goto L_0x00cc
            L_0x009e:
                com.miui.networkassistant.ui.NetworkAssistantActivity r4 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                android.content.Context r4 = r4.mAppContext
                com.miui.networkassistant.ui.NetworkAssistantActivity r0 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                int r0 = r0.mCurrentOperateSlotNum
                boolean r4 = com.miui.networkassistant.utils.MiSimUtil.isMiSimEnable(r4, r0)
                if (r4 == 0) goto L_0x00bc
                com.miui.networkassistant.ui.NetworkAssistantActivity r4 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                android.app.Activity r4 = r4.mActivity
                java.lang.String r0 = "misim://router?launchfrom=netasistant"
                com.miui.networkassistant.utils.MiSimUtil.startMiSimMainActivity(r4, r0)
                goto L_0x00cc
            L_0x00bc:
                com.miui.networkassistant.ui.NetworkAssistantActivity r4 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                android.app.Activity r4 = r4.mActivity
                r0 = 1
                java.lang.String r1 = "na_main_bill_remained"
                goto L_0x0024
            L_0x00c7:
                com.miui.networkassistant.ui.NetworkAssistantActivity r4 = com.miui.networkassistant.ui.NetworkAssistantActivity.this
                r4.onMainButtonClick()
            L_0x00cc:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.NetworkAssistantActivity.AnonymousClass4.onClick(android.view.View):void");
        }
    };
    private DialogInterface.OnClickListener mOnDailyCardGuideDialogClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                Sim.operateOnSlotNum(NetworkAssistantActivity.this.mCurrentOperateSlotNum);
                Bundle bundle = new Bundle();
                bundle.putBoolean(OperatorSettingFragment.BUNDLE_KEY_TRAFFIC_GUIDE, true);
                g.startWithFragment(NetworkAssistantActivity.this.mActivity, OperatorSettingFragment.class, bundle);
            }
        }
    };
    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        public boolean onLongClick(View view) {
            if (NetworkAssistantActivity.this.mMobileStatus[NetworkAssistantActivity.this.mCurrentOperateSlotNum] != MobileStatus.Normal) {
                return false;
            }
            Sim.operateOnSlotNum(NetworkAssistantActivity.this.mCurrentOperateSlotNum);
            g.startWithFragment(NetworkAssistantActivity.this.mActivity, PackageSettingFragment.class);
            AnalyticsHelper.trackMainButtonClickCountEvent(AnalyticsHelper.TRACK_ITEM_LONG_CORRECTION_SETTING);
            return true;
        }
    };
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        public void onPageScrollStateChanged(int i) {
        }

        public void onPageScrolled(int i, float f, int i2) {
        }

        public void onPageSelected(int i) {
            int unused = NetworkAssistantActivity.this.mCurrentOperateSlotNum = i;
            NetworkAssistantActivity networkAssistantActivity = NetworkAssistantActivity.this;
            networkAssistantActivity.updateDataAndBg(networkAssistantActivity.mCurrentOperateSlotNum);
        }
    };
    private ArrayList<View> mPagerViews;
    private SimCardHelper mSimCardHelper;
    /* access modifiers changed from: private */
    public SimUserInfo[] mSimUserInfo = new SimUserInfo[2];
    private MainToolbarItemView mToolbarFirewall;
    private MainToolbarItemView mToolbarUsagePurchase;
    private MainToolbarItemView mToolbarUsageSorted;
    /* access modifiers changed from: private */
    public TrafficCornBinderListenerHost mTrafficCornBinderListenerHost = new TrafficCornBinderListenerHost() {
        public void onTrafficCorrected(TrafficUsedStatus trafficUsedStatus) {
            NetworkAssistantActivity.this.mHandler.sendEmptyMessageDelayed(trafficUsedStatus.getSlotNum() == 0 ? 2 : 3, 200);
            NetworkAssistantActivity.this.mHandler.sendEmptyMessageDelayed(trafficUsedStatus.getSlotNum() == 0 ? 4 : 5, 300);
        }
    };
    /* access modifiers changed from: private */
    public ITrafficCornBinder[] mTrafficCornBinders = new ITrafficCornBinder[2];
    private TrafficInputDialog.TrafficInputDialogListener mTrafficInputDialogListener;
    /* access modifiers changed from: private */
    public ITrafficManageBinder mTrafficManageBinder;
    private ServiceConnection mTrafficManageConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ITrafficManageBinder unused = NetworkAssistantActivity.this.mTrafficManageBinder = ITrafficManageBinder.Stub.asInterface(iBinder);
            try {
                NetworkAssistantActivity.this.mSimUserInfo[0] = SimUserInfo.getInstance(NetworkAssistantActivity.this.mAppContext, 0);
                NetworkAssistantActivity.this.mTrafficCornBinders[0] = NetworkAssistantActivity.this.mTrafficManageBinder.getTrafficCornBinder(0);
                if (NetworkAssistantActivity.this.mTrafficCornBinders[0] != null) {
                    NetworkAssistantActivity.this.mTrafficCornBinders[0].registerLisener(NetworkAssistantActivity.this.mTrafficCornBinderListenerHost.getStub());
                }
                if (NetworkAssistantActivity.this.mIsDualCard) {
                    NetworkAssistantActivity.this.mSimUserInfo[1] = SimUserInfo.getInstance(NetworkAssistantActivity.this.mAppContext, 1);
                    NetworkAssistantActivity.this.mTrafficCornBinders[1] = NetworkAssistantActivity.this.mTrafficManageBinder.getTrafficCornBinder(1);
                    if (NetworkAssistantActivity.this.mTrafficCornBinders[1] != null) {
                        NetworkAssistantActivity.this.mTrafficCornBinders[1].registerLisener(NetworkAssistantActivity.this.mTrafficCornBinderListenerHost.getStub());
                    }
                }
            } catch (RemoteException e) {
                Log.i(NetworkAssistantActivity.TAG, "register traffic corn", e);
            }
            if (NetworkAssistantActivity.this.mTrafficCornBinders[0] != null) {
                NetworkAssistantActivity.this.mHandler.sendEmptyMessage(1);
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            ITrafficManageBinder unused = NetworkAssistantActivity.this.mTrafficManageBinder = null;
        }
    };
    /* access modifiers changed from: private */
    public MainTrafficUsedView[] mTrafficUsedViews = new MainTrafficUsedView[2];

    /* renamed from: com.miui.networkassistant.ui.NetworkAssistantActivity$8  reason: invalid class name */
    static /* synthetic */ class AnonymousClass8 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$networkassistant$ui$NetworkAssistantActivity$MobileStatus = new int[MobileStatus.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(22:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|(3:21|22|24)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(24:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|24) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0040 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x004b */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0056 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0062 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x006e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x007a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0035 */
        static {
            /*
                com.miui.networkassistant.ui.NetworkAssistantActivity$MobileStatus[] r0 = com.miui.networkassistant.ui.NetworkAssistantActivity.MobileStatus.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$networkassistant$ui$NetworkAssistantActivity$MobileStatus = r0
                int[] r0 = $SwitchMap$com$miui$networkassistant$ui$NetworkAssistantActivity$MobileStatus     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.networkassistant.ui.NetworkAssistantActivity$MobileStatus r1 = com.miui.networkassistant.ui.NetworkAssistantActivity.MobileStatus.NoSimCardInfo     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$networkassistant$ui$NetworkAssistantActivity$MobileStatus     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.networkassistant.ui.NetworkAssistantActivity$MobileStatus r1 = com.miui.networkassistant.ui.NetworkAssistantActivity.MobileStatus.Oversea     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$networkassistant$ui$NetworkAssistantActivity$MobileStatus     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.networkassistant.ui.NetworkAssistantActivity$MobileStatus r1 = com.miui.networkassistant.ui.NetworkAssistantActivity.MobileStatus.OverseaRoaming     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$com$miui$networkassistant$ui$NetworkAssistantActivity$MobileStatus     // Catch:{ NoSuchFieldError -> 0x0035 }
                com.miui.networkassistant.ui.NetworkAssistantActivity$MobileStatus r1 = com.miui.networkassistant.ui.NetworkAssistantActivity.MobileStatus.NormalRoaming     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                int[] r0 = $SwitchMap$com$miui$networkassistant$ui$NetworkAssistantActivity$MobileStatus     // Catch:{ NoSuchFieldError -> 0x0040 }
                com.miui.networkassistant.ui.NetworkAssistantActivity$MobileStatus r1 = com.miui.networkassistant.ui.NetworkAssistantActivity.MobileStatus.TrafficCtrlClosed     // Catch:{ NoSuchFieldError -> 0x0040 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0040 }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0040 }
            L_0x0040:
                int[] r0 = $SwitchMap$com$miui$networkassistant$ui$NetworkAssistantActivity$MobileStatus     // Catch:{ NoSuchFieldError -> 0x004b }
                com.miui.networkassistant.ui.NetworkAssistantActivity$MobileStatus r1 = com.miui.networkassistant.ui.NetworkAssistantActivity.MobileStatus.NoTotalPackage     // Catch:{ NoSuchFieldError -> 0x004b }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x004b }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x004b }
            L_0x004b:
                int[] r0 = $SwitchMap$com$miui$networkassistant$ui$NetworkAssistantActivity$MobileStatus     // Catch:{ NoSuchFieldError -> 0x0056 }
                com.miui.networkassistant.ui.NetworkAssistantActivity$MobileStatus r1 = com.miui.networkassistant.ui.NetworkAssistantActivity.MobileStatus.NoOperatorSet     // Catch:{ NoSuchFieldError -> 0x0056 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0056 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0056 }
            L_0x0056:
                int[] r0 = $SwitchMap$com$miui$networkassistant$ui$NetworkAssistantActivity$MobileStatus     // Catch:{ NoSuchFieldError -> 0x0062 }
                com.miui.networkassistant.ui.NetworkAssistantActivity$MobileStatus r1 = com.miui.networkassistant.ui.NetworkAssistantActivity.MobileStatus.UnLimitedCard     // Catch:{ NoSuchFieldError -> 0x0062 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0062 }
                r2 = 8
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0062 }
            L_0x0062:
                int[] r0 = $SwitchMap$com$miui$networkassistant$ui$NetworkAssistantActivity$MobileStatus     // Catch:{ NoSuchFieldError -> 0x006e }
                com.miui.networkassistant.ui.NetworkAssistantActivity$MobileStatus r1 = com.miui.networkassistant.ui.NetworkAssistantActivity.MobileStatus.Normal     // Catch:{ NoSuchFieldError -> 0x006e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x006e }
                r2 = 9
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x006e }
            L_0x006e:
                int[] r0 = $SwitchMap$com$miui$networkassistant$ui$NetworkAssistantActivity$MobileStatus     // Catch:{ NoSuchFieldError -> 0x007a }
                com.miui.networkassistant.ui.NetworkAssistantActivity$MobileStatus r1 = com.miui.networkassistant.ui.NetworkAssistantActivity.MobileStatus.MiSimPending     // Catch:{ NoSuchFieldError -> 0x007a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x007a }
                r2 = 10
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x007a }
            L_0x007a:
                int[] r0 = $SwitchMap$com$miui$networkassistant$ui$NetworkAssistantActivity$MobileStatus     // Catch:{ NoSuchFieldError -> 0x0086 }
                com.miui.networkassistant.ui.NetworkAssistantActivity$MobileStatus r1 = com.miui.networkassistant.ui.NetworkAssistantActivity.MobileStatus.VirtualCard     // Catch:{ NoSuchFieldError -> 0x0086 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0086 }
                r2 = 11
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0086 }
            L_0x0086:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.NetworkAssistantActivity.AnonymousClass8.<clinit>():void");
        }
    }

    private enum MobileStatus {
        Init,
        NoSimCardInfo,
        Oversea,
        OverseaRoaming,
        NormalRoaming,
        NoTotalPackage,
        NoOperatorSet,
        Normal,
        TrafficCtrlClosed,
        MiSimPending,
        VirtualCard,
        UnLimitedCard
    }

    private static class MyTrafficInputDialogListener implements TrafficInputDialog.TrafficInputDialogListener {
        private WeakReference<NetworkAssistantActivity> activityRef;

        public MyTrafficInputDialogListener(NetworkAssistantActivity networkAssistantActivity) {
            this.activityRef = new WeakReference<>(networkAssistantActivity);
        }

        public void onTrafficUpdated(long j, int i) {
            NetworkAssistantActivity networkAssistantActivity = (NetworkAssistantActivity) this.activityRef.get();
            if (networkAssistantActivity != null && networkAssistantActivity.mTrafficManageBinder != null) {
                try {
                    networkAssistantActivity.mTrafficManageBinder.manualCorrectNormalDataUsage(j, networkAssistantActivity.mCurrentOperateSlotNum);
                    networkAssistantActivity.mTrafficManageBinder.updateGlobleDataUsage(networkAssistantActivity.mCurrentOperateSlotNum);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                networkAssistantActivity.mHandler.sendEmptyMessage(networkAssistantActivity.mCurrentOperateSlotNum == 0 ? 2 : 3);
            }
        }
    }

    private static class UiHandler extends Handler {
        static final int MSG_START_ANIM_0 = 4;
        static final int MSG_START_ANIM_1 = 5;
        static final int MSG_TRAFFIC_INIT_DATA = 1;
        static final int MSG_TRAFFIC_UPDATE_DATA_0 = 2;
        static final int MSG_TRAFFIC_UPDATE_DATA_1 = 3;
        private WeakReference<NetworkAssistantActivity> activityRef;

        public UiHandler(NetworkAssistantActivity networkAssistantActivity) {
            this.activityRef = new WeakReference<>(networkAssistantActivity);
        }

        public void handleMessage(Message message) {
            NetworkAssistantActivity networkAssistantActivity = (NetworkAssistantActivity) this.activityRef.get();
            if (networkAssistantActivity != null) {
                int i = message.what;
                int i2 = 1;
                if (i == 1) {
                    networkAssistantActivity.initData();
                } else if (i == 2) {
                    networkAssistantActivity.updateDataAndBg(0);
                } else if (i != 3) {
                    if (i == 4) {
                        i2 = 0;
                    } else if (i != 5) {
                        return;
                    }
                    try {
                        ITrafficCornBinder iTrafficCornBinder = networkAssistantActivity.mTrafficCornBinders[i2];
                        if (iTrafficCornBinder.isFinished() && iTrafficCornBinder.isConfigUpdated() && networkAssistantActivity.mSimUserInfo[i2].getTrafficTcResultCode() == 0) {
                            networkAssistantActivity.mTrafficUsedViews[i2].startAnim();
                            Log.w(NetworkAssistantActivity.TAG, "onTrafficCorrected:isFinished  startAnim() slotNum:" + i2);
                        }
                    } catch (Exception e) {
                        Log.e(NetworkAssistantActivity.TAG, "Exception", e);
                    }
                } else {
                    networkAssistantActivity.updateDataAndBg(1);
                }
            }
        }
    }

    public NetworkAssistantActivity() {
        MobileStatus mobileStatus = MobileStatus.Init;
        this.mMobileStatus = new MobileStatus[]{mobileStatus, mobileStatus};
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.networkassistant.ui.NetworkAssistantActivity] */
    private void addMainTrafficUsedView(int i) {
        this.mTrafficUsedViews[i] = new MainTrafficUsedView(this);
        this.mTrafficUsedViews[i].setDataUsageClickListener(this.mOnClickListener);
        this.mTrafficUsedViews[i].setDataUsageLongClickListener(this.mOnLongClickListener);
        this.mPagerViews.add(this.mTrafficUsedViews[i]);
    }

    private void bindTrafficManageService() {
        TmBinderCacher.getInstance().bindTmService(this.mTrafficManageConnection);
    }

    private void checkMobileStatus(int i) {
        SimUserInfo simUserInfo = getSimUserInfo(i);
        if (MiSimUtil.isMiSimEnable(this.mAppContext, i)) {
            this.mMobileStatus[i] = MobileStatus.VirtualCard;
        } else if (!simUserInfo.hasImsi()) {
            if (isDualCardSupport()) {
                this.mMobileStatus[i] = MobileStatus.MiSimPending;
            } else {
                this.mMobileStatus[i] = MobileStatus.NoSimCardInfo;
            }
        } else if (!simUserInfo.isTrafficManageControlEnable()) {
            this.mMobileStatus[i] = MobileStatus.TrafficCtrlClosed;
        } else if (simUserInfo.isOversea()) {
            if (TelephonyUtil.isNetworkRoaming(this.mAppContext, i)) {
                this.mMobileStatus[i] = MobileStatus.OverseaRoaming;
            } else {
                this.mMobileStatus[i] = MobileStatus.Oversea;
            }
        } else if (!simUserInfo.isSmsAvailable()) {
            this.mMobileStatus[i] = MobileStatus.NormalRoaming;
        } else if (simUserInfo.isNotLimitCardEnable()) {
            this.mMobileStatus[i] = MobileStatus.UnLimitedCard;
        } else if (!isTotalDataUsageSetted(i)) {
            this.mMobileStatus[i] = MobileStatus.NoTotalPackage;
        } else if (!simUserInfo.isOperatorSetted()) {
            this.mMobileStatus[i] = MobileStatus.NoOperatorSet;
        } else {
            this.mMobileStatus[i] = MobileStatus.Normal;
        }
    }

    private void checkTrafficPurchaseEnable(int i) {
        SimUserInfo simUserInfo = getSimUserInfo(i);
        int i2 = 0;
        if (simUserInfo.isNATipsEnable()) {
            simUserInfo.setNATipsEnable(false);
            TrafficUpdateUtil.broadCastTrafficUpdated(this.mAppContext);
        }
        CooperationManager.isTrafficPurchaseAvailable(getApplicationContext(), simUserInfo, true);
        MainToolbarItemView mainToolbarItemView = this.mToolbarUsagePurchase;
        if (DeviceUtil.IS_INTERNATIONAL_BUILD) {
            i2 = 8;
        }
        mainToolbarItemView.setVisibility(i2);
        if (DeviceUtil.isCNLanguage() && !MiSimUtil.isMiSimEnable(this.mAppContext, i)) {
            this.mToolbarUsagePurchase.setDescFromHtml(simUserInfo.getNATrafficPurchaseOrderTips());
        }
    }

    private void doUpdateBgView(int i, long j, long j2, float f) {
        int i2;
        String str;
        MainTrafficUsedView trafficUsedView = getTrafficUsedView(i);
        float f2 = (float) j2;
        float f3 = (float) j;
        float f4 = 1.0f - (f2 / f3);
        if (f2 < f3 * f || j < 0) {
            str = getTitleOperatorName(i);
            i2 = 0;
        } else if (j2 < j) {
            str = getTitleOperatorName(i);
            i2 = 1;
        } else {
            str = getTitleOperatorName(i);
            i2 = 2;
        }
        trafficUsedView.setCardStyle(str, i2, f4, i);
    }

    /* access modifiers changed from: private */
    public SimUserInfo getSimUserInfo(int i) {
        return this.mSimUserInfo[i];
    }

    private String getTitleOperatorName(int i) {
        String string = getString(i == 0 ? R.string.dual_setting_simcard1 : R.string.dual_setting_simcard2);
        SimUserInfo[] simUserInfoArr = this.mSimUserInfo;
        return String.format("%s-%s ", new Object[]{string, (simUserInfoArr[i] == null || !simUserInfoArr[i].hasImsi()) ? this.mAppContext.getString(R.string.main_indicator_title) : this.mSimUserInfo[i].getSimName()});
    }

    private ITrafficCornBinder getTrafficCorrection(int i) {
        return this.mTrafficCornBinders[i];
    }

    private MainTrafficUsedView getTrafficUsedView(int i) {
        return this.mTrafficUsedViews[i];
    }

    /* access modifiers changed from: private */
    public void initData() {
        if (this.mTrafficManageBinder != null) {
            this.mSimCardHelper = SimCardHelper.getInstance(this.mAppContext);
            this.mDataReady = true;
            initPagerView();
            this.mSimUserInfo[0] = SimUserInfo.getInstance(this.mAppContext, 0);
            if (this.mIsDualCard) {
                this.mSimUserInfo[1] = SimUserInfo.getInstance(this.mAppContext, 1);
            }
            updateDataAndBg(1 - this.mCurrentOperateSlotNum);
            updateDataAndBg(this.mCurrentOperateSlotNum);
            this.mMainViewPager.setCurrentItem(this.mCurrentOperateSlotNum);
            tryShowDailyCardSettingGuideDialog();
        }
    }

    private void initPagerView() {
        int i;
        this.mPagerViews = new ArrayList<>();
        if (isDualCardSupport()) {
            addMainTrafficUsedView(0);
            i = 1;
        } else {
            i = this.mCurrentOperateSlotNum;
        }
        addMainTrafficUsedView(i);
        this.mMainViewPager = findViewById(R.id.main_view_pager);
        this.mMainViewPager.setAdapter(new MainTrafficViewPagerAdapter(this.mPagerViews));
        this.mMainViewPager.setPageMargin(20);
        this.mMainViewPager.setOffscreenPageLimit(2);
        this.mMainViewPager.addOnPageChangeListener(this.mPageChangeListener);
    }

    private void initView() {
        findViewById(R.id.main_toolbar_purchase).setOnClickListener(this.mOnClickListener);
        findViewById(R.id.main_toolbar_firewall).setOnClickListener(this.mOnClickListener);
        findViewById(R.id.main_toolbar_statistic).setOnClickListener(this.mOnClickListener);
        this.mToolbarFirewall = (MainToolbarItemView) findViewById(R.id.main_toolbar_firewall);
        this.mToolbarUsageSorted = (MainToolbarItemView) findViewById(R.id.main_toolbar_statistic);
        this.mToolbarUsagePurchase = (MainToolbarItemView) findViewById(R.id.main_toolbar_purchase);
        this.mToolbarFirewall.setName((int) R.string.main_toolbar_firewall);
        this.mToolbarUsageSorted.setName((int) R.string.main_toolbar_statistic);
        this.mToolbarUsagePurchase.setName((int) R.string.main_toolbar_purchase);
        this.mToolbarFirewall.setIcon(R.drawable.na_netd);
        this.mToolbarUsageSorted.setIcon(R.drawable.na_traffic_sort);
        this.mToolbarUsagePurchase.setIcon(R.drawable.na_traffic);
        this.mToolbarFirewall.setOnClickListener(this.mOnClickListener);
        this.mToolbarUsageSorted.setOnClickListener(this.mOnClickListener);
        this.mToolbarUsagePurchase.setOnClickListener(this.mOnClickListener);
        this.mToolbarUsagePurchase.setVisibility(DeviceUtil.IS_INTERNATIONAL_BUILD ? 8 : 0);
    }

    private boolean isDualCardSupport() {
        return this.mIsDualCard && (this.mSimCardHelper.isDualSimInserted() || (this.mSimCardHelper.isDualSimInsertedOne() && MiSimUtil.isSupportGlobalVirtualSim(this.mAppContext) && MiSimUtil.isMiSimCloudEnable(this.mAppContext)));
    }

    private boolean isTotalDataUsageSetted(int i) {
        return this.mSimUserInfo[i].isTotalDataUsageSetted();
    }

    private void odUpdateNormalTraffic(int i, boolean z) {
        showTrafficAdjusting(i);
        try {
            updateNormalTraffic(i, z);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void onMainButtonClick() {
        String str;
        Log.d(TAG, String.format("onMainButtonClick mMobileStatus[%s]: %s", new Object[]{Integer.valueOf(this.mCurrentOperateSlotNum), this.mMobileStatus[this.mCurrentOperateSlotNum]}));
        switch (AnonymousClass8.$SwitchMap$com$miui$networkassistant$ui$NetworkAssistantActivity$MobileStatus[this.mMobileStatus[this.mCurrentOperateSlotNum].ordinal()]) {
            case 2:
            case 3:
            case 4:
                setTrafficManually();
                return;
            case 5:
                openTrafficCtrl(this.mCurrentOperateSlotNum);
                return;
            case 6:
            case 7:
                Bundle bundle = new Bundle();
                bundle.putBoolean(OperatorSettingFragment.BUNDLE_KEY_TRAFFIC_GUIDE, true);
                g.startWithFragment(this.mActivity, OperatorSettingFragment.class, bundle);
                str = AnalyticsHelper.TRACK_ITEM_PACKAGE_SETTING;
                break;
            case 8:
            case 9:
                if (this.mSimUserInfo[this.mCurrentOperateSlotNum].isSupportCorrection()) {
                    try {
                        if (this.mTrafficManageBinder != null && this.mTrafficManageBinder.startCorrection(false, this.mCurrentOperateSlotNum, true, 7)) {
                            MainTrafficUsedView trafficUsedView = getTrafficUsedView(this.mCurrentOperateSlotNum);
                            if (!getSimUserInfo(this.mCurrentOperateSlotNum).isDailyUsedCardEffective()) {
                                if (this.mMobileStatus[this.mCurrentOperateSlotNum] != MobileStatus.UnLimitedCard) {
                                    trafficUsedView.setDataUsageButtonText((int) R.string.main_button_usage_adjusting);
                                    trafficUsedView.setDataUsageButtonEnable(false);
                                    trafficUsedView.setErrorTextVisibility(8);
                                }
                            }
                            trafficUsedView.setDataUsageButtonText((int) R.string.main_button_usage_adjusting_bill);
                            trafficUsedView.setDataUsageButtonEnable(false);
                            trafficUsedView.setErrorTextVisibility(8);
                        }
                    } catch (RemoteException e) {
                        Log.i(TAG, "startCorrection ", e);
                    }
                    str = AnalyticsHelper.TRACK_ITEM_TRAFFIC_CORRECTION;
                    break;
                } else {
                    setAdjustTrafficManually();
                    return;
                }
            case 10:
                MiSimUtil.startMiSimMainActivity(this.mAppContext, Constants.External.MISIM_MAIN_URL);
                return;
            case 11:
                getTrafficUsedView(this.mCurrentOperateSlotNum).setDataUsageButtonText((int) R.string.main_button_mi_sim_open);
                VirtualSimUtil.startVirtualSimActivity(this.mAppContext, VirtualSimUtil.ACTION_DETAIL_PAGE);
                return;
            default:
                return;
        }
        AnalyticsHelper.trackMainButtonClickCountEvent(str);
    }

    private void openTrafficCtrl(int i) {
        this.mSimUserInfo[i].setTrafficManageControlEnable(true);
        this.mHandler.sendEmptyMessage(i == 0 ? 2 : 3);
    }

    private void parseSlotNum() {
        if (this.mIsDualCard) {
            this.mCurrentOperateSlotNum = Sim.getCurrentActiveSlotNum();
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(Sim.SIM_SLOT_NUM_TAG)) {
                this.mCurrentOperateSlotNum = intent.getIntExtra(Sim.SIM_SLOT_NUM_TAG, this.mCurrentOperateSlotNum);
            }
        }
    }

    private void setAdjustTrafficManually() {
        if (getSimUserInfo(this.mCurrentOperateSlotNum) != null) {
            if (this.mTrafficInputDialogListener == null) {
                this.mTrafficInputDialogListener = new MyTrafficInputDialogListener(this);
            }
            TrafficInputDialog trafficInputDialog = this.mInputDialog;
            if (trafficInputDialog == null) {
                this.mInputDialog = new TrafficInputDialog(this.mActivity, this.mTrafficInputDialogListener);
            } else {
                trafficInputDialog.clearInputText();
            }
            this.mInputDialog.buildInputDialog(getString(R.string.manual_input_traffic), getString(R.string.input_used_hint));
        }
    }

    private void setTrafficManually() {
        SimUserInfo simUserInfo = getSimUserInfo(this.mCurrentOperateSlotNum);
        if (simUserInfo == null) {
            return;
        }
        if (simUserInfo.isTotalDataUsageSetted()) {
            setAdjustTrafficManually();
        } else {
            g.startWithFragment(this.mActivity, PackageSettingFragment.class);
        }
    }

    private void showDailyCardSettingGuideDialog() {
        CommonDialog commonDialog = new CommonDialog(this.mActivity, this.mOnDailyCardGuideDialogClickListener);
        commonDialog.setWeakReferenceEnabled(false);
        commonDialog.setTitle(this.mAppContext.getString(R.string.main_daily_card_guide_dialog_title));
        commonDialog.setMessage(this.mAppContext.getString(R.string.main_daily_card_guide_dialog_summary));
        commonDialog.setPostiveText(this.mAppContext.getString(R.string.main_daily_card_guide_dialog_ok));
        commonDialog.setNagetiveText(this.mAppContext.getString(17039360));
        commonDialog.show();
    }

    private void showNoTotalPackageView(int i) {
        MainTrafficUsedView trafficUsedView = getTrafficUsedView(i);
        showTrafficAdjusting(i);
        trafficUsedView.showPrimaryMessage((int) R.string.main_normal_data_usage);
        updateTrafficUsedOnly(trafficUsedView, i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0026 A[SYNTHETIC, Splitter:B:13:0x0026] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x004e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showTrafficAdjusting(int r8) {
        /*
            r7 = this;
            com.miui.networkassistant.service.ITrafficCornBinder r0 = r7.getTrafficCorrection(r8)
            java.lang.String r1 = "trafficCornBinder"
            java.lang.String r2 = "NAMainActivity"
            r3 = 0
            r4 = 1
            if (r0 == 0) goto L_0x001f
            boolean r5 = r0.isFinished()     // Catch:{ RemoteException -> 0x001b }
            if (r5 == 0) goto L_0x0019
            boolean r5 = r0.isConfigUpdated()     // Catch:{ RemoteException -> 0x001b }
            if (r5 == 0) goto L_0x0019
            goto L_0x001f
        L_0x0019:
            r5 = r3
            goto L_0x0020
        L_0x001b:
            r5 = move-exception
            android.util.Log.i(r2, r1, r5)
        L_0x001f:
            r5 = r4
        L_0x0020:
            com.miui.networkassistant.ui.view.MainTrafficUsedView r6 = r7.getTrafficUsedView(r8)
            if (r5 != 0) goto L_0x004e
            int r0 = r0.getTcType()     // Catch:{ RemoteException -> 0x0046 }
            r4 = 2
            if (r0 == r4) goto L_0x003f
            com.miui.networkassistant.config.SimUserInfo r8 = r7.getSimUserInfo(r8)     // Catch:{ RemoteException -> 0x0046 }
            boolean r8 = r8.isDailyUsedCardEffective()     // Catch:{ RemoteException -> 0x0046 }
            if (r8 == 0) goto L_0x0038
            goto L_0x003f
        L_0x0038:
            r8 = 2131756745(0x7f1006c9, float:1.9144406E38)
            r6.setDataUsageButtonText((int) r8)     // Catch:{ RemoteException -> 0x0046 }
            goto L_0x004a
        L_0x003f:
            r8 = 2131756746(0x7f1006ca, float:1.9144408E38)
            r6.setDataUsageButtonText((int) r8)     // Catch:{ RemoteException -> 0x0046 }
            goto L_0x004a
        L_0x0046:
            r8 = move-exception
            android.util.Log.i(r2, r1, r8)
        L_0x004a:
            r6.setDataUsageButtonEnable(r3)
            goto L_0x007a
        L_0x004e:
            boolean r0 = r7.isTotalDataUsageSetted(r8)
            if (r0 != 0) goto L_0x005e
            r8 = 2131756747(0x7f1006cb, float:1.914441E38)
            r6.setDataUsageButtonText((int) r8)
            r6.setDataUsageButtonEnable(r4)
            goto L_0x007a
        L_0x005e:
            r0 = 2131756743(0x7f1006c7, float:1.9144402E38)
            r6.setDataUsageButtonText((int) r0)
            r6.setDataUsageButtonEnable(r4)
            com.miui.networkassistant.config.SimUserInfo[] r0 = r7.mSimUserInfo
            r8 = r0[r8]
            int r8 = r8.getTrafficTcResultCode()
            if (r8 == 0) goto L_0x0075
            r6.setErrorTextVisibility(r3)
            goto L_0x007a
        L_0x0075:
            r8 = 8
            r6.setErrorTextVisibility(r8)
        L_0x007a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.NetworkAssistantActivity.showTrafficAdjusting(int):void");
    }

    private void tryShowDailyCardSettingGuideDialog() {
        SimUserInfo simUserInfo = getSimUserInfo(this.mCurrentOperateSlotNum);
        if (simUserInfo != null && simUserInfo.isDailyCardSettingGuideEnable() && !simUserInfo.isOversea() && simUserInfo.isSimInserted()) {
            simUserInfo.setDailyCardSettingGuideEnable(false);
            showDailyCardSettingGuideDialog();
            SimUserInfo simUserInfo2 = getSimUserInfo(1 - this.mCurrentOperateSlotNum);
            if (simUserInfo2 != null) {
                simUserInfo2.setDailyCardSettingGuideEnable(false);
            }
        }
    }

    private void unbindTrafficManageService() {
        TmBinderCacher.getInstance().unbindTmService(this.mTrafficManageConnection);
    }

    /* access modifiers changed from: private */
    public void updateDataAndBg(int i) {
        MainTrafficUsedView trafficUsedView = getTrafficUsedView(i);
        if (this.mTrafficManageBinder != null && this.mDataReady && trafficUsedView != null) {
            checkMobileStatus(i);
            checkTrafficPurchaseEnable(i);
            trafficUsedView.resetView();
            switch (AnonymousClass8.$SwitchMap$com$miui$networkassistant$ui$NetworkAssistantActivity$MobileStatus[this.mMobileStatus[i].ordinal()]) {
                case 1:
                    updateNoInsertSimCard(i);
                    return;
                case 2:
                case 3:
                    updateOverSeaTraffic(i, true);
                    return;
                case 4:
                    updateNormalRoamingTraffic(i, true);
                    return;
                case 5:
                    updateTrafficCtl(i);
                    return;
                case 6:
                    showNoTotalPackageView(i);
                    return;
                case 7:
                case 9:
                    odUpdateNormalTraffic(i, true);
                    return;
                case 8:
                    updateUnLimitedCardTraffic(i, true);
                    return;
                case 10:
                    updateMiSimCardAdded(i);
                    return;
                case 11:
                    updateVirtualSimTraffic(i);
                    return;
                default:
                    return;
            }
        }
    }

    private void updateMiSimCardAdded(int i) {
        MainTrafficUsedView trafficUsedView = getTrafficUsedView(i);
        trafficUsedView.setDataUsageButtonVisible(true);
        Context context = this.mAppContext;
        trafficUsedView.setDataUsageButtonText(MiSimUtil.getMiSimActiveBtnTxt(context, context.getString(R.string.main_button_mi_sim_close)));
        trafficUsedView.setDataUsageButtonEnable(true);
        trafficUsedView.setMonthPackageInfo(0, 0, 0.0f, false);
        trafficUsedView.setLeisureTrafficRemained(false, 0);
        trafficUsedView.setMonthRemainedViewVisible(false);
        trafficUsedView.setUnitTextViewVisible(false);
        trafficUsedView.setPrimaryTextLayoutVisible(false);
        trafficUsedView.setCardStyle(getTitleOperatorName(i), 3, 0.0f, i);
        if (DeviceUtil.IS_INTERNATIONAL_BUILD) {
            trafficUsedView.setBillLayoutVisible(false);
        }
    }

    private void updateNoInsertSimCard(int i) {
        MainTrafficUsedView trafficUsedView = getTrafficUsedView(i);
        trafficUsedView.setDataUsageButtonVisible(false);
        trafficUsedView.setMonthPackageInfo(0, 0, 0.0f, false);
        trafficUsedView.setLeisureTrafficRemained(false, 0);
        trafficUsedView.setMonthRemainedViewVisible(false);
        trafficUsedView.setUnitTextViewVisible(false);
        trafficUsedView.setPrimaryTextLayoutVisible(false);
        trafficUsedView.setCardStyle(getTitleOperatorName(i), 4, 0.0f, i);
        if (DeviceUtil.IS_INTERNATIONAL_BUILD) {
            trafficUsedView.setBillLayoutVisible(false);
        }
    }

    private void updateNormalRoamingTraffic(int i, boolean z) {
        MainTrafficUsedView trafficUsedView = getTrafficUsedView(i);
        if (getSimUserInfo(i).isTotalDataUsageSetted()) {
            trafficUsedView.setDataUsageButtonText((int) R.string.main_button_usage_adjust_manual);
            try {
                updateNormalTraffic(i, z);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            showNoTotalPackageView(i);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:46:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateNormalTraffic(int r22, boolean r23) {
        /*
            r21 = this;
            r7 = r21
            r8 = r22
            com.miui.networkassistant.service.ITrafficManageBinder r0 = r7.mTrafficManageBinder
            if (r0 != 0) goto L_0x0009
            return
        L_0x0009:
            com.miui.networkassistant.ui.view.MainTrafficUsedView r15 = r21.getTrafficUsedView(r22)
            com.miui.networkassistant.config.SimUserInfo r16 = r21.getSimUserInfo(r22)
            com.miui.networkassistant.service.ITrafficManageBinder r0 = r7.mTrafficManageBinder
            long r17 = r0.getCurrentMonthTotalPackage(r8)
            float r19 = r16.getDataUsageWarning()
            com.miui.networkassistant.service.ITrafficManageBinder r0 = r7.mTrafficManageBinder
            long r0 = r0.getTodayDataUsageUsed(r8)
            r15.setTodayUsed(r0)
            r9 = 0
            r15.setHasLeisure(r9)
            boolean r0 = r16.isLeisureDataUsageEffective()
            if (r0 == 0) goto L_0x008d
            boolean r0 = r21.isTotalDataUsageSetted(r22)
            if (r0 == 0) goto L_0x008d
            boolean r20 = com.miui.networkassistant.traffic.statistic.LeisureTrafficHelper.isLeisureTime(r16)
            com.miui.networkassistant.service.ITrafficManageBinder r0 = r7.mTrafficManageBinder
            long[] r10 = r0.getCorrectedNormalAndLeisureMonthTotalUsed(r8)
            long r12 = r16.getLeisureDataUsageTotal()
            r11 = 1
            r0 = r10[r11]
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 < 0) goto L_0x004b
            r0 = r11
            goto L_0x004c
        L_0x004b:
            r0 = r9
        L_0x004c:
            r1 = r10[r11]
            long r1 = r12 - r1
            r15.setLeisureTrafficRemained(r11, r1)
            if (r20 == 0) goto L_0x008b
            if (r23 == 0) goto L_0x008b
            if (r0 == 0) goto L_0x006e
            r4 = r10[r9]
            r0 = r21
            r1 = r22
            r2 = r17
            r6 = r19
            r0.doUpdateBgView(r1, r2, r4, r6)
            r0 = r10[r9]
            r2 = 1
            r9 = r15
            r10 = r0
            r12 = r17
            goto L_0x0083
        L_0x006e:
            r6 = r15
            r6.setHasLeisure(r11)
            r4 = r10[r11]
            r0 = r21
            r1 = r22
            r2 = r12
            r6 = r19
            r0.doUpdateBgView(r1, r2, r4, r6)
            r0 = r10[r11]
            r2 = 1
            r9 = r15
            r10 = r0
        L_0x0083:
            r14 = r19
            r6 = r15
            r15 = r2
            r9.setMonthPackageInfo(r10, r12, r14, r15)
            goto L_0x0095
        L_0x008b:
            r6 = r15
            goto L_0x0095
        L_0x008d:
            r6 = r15
            r0 = 0
            r6.setLeisureTrafficRemained(r9, r0)
            r20 = r9
        L_0x0095:
            if (r20 != 0) goto L_0x00c6
            com.miui.networkassistant.service.ITrafficManageBinder r0 = r7.mTrafficManageBinder
            long r4 = r0.getCorrectedNormalMonthDataUsageUsed(r8)
            boolean r0 = r16.isDailyUsedCardEffective()
            if (r0 == 0) goto L_0x00a7
            r0 = 2131756751(0x7f1006cf, float:1.9144418E38)
            goto L_0x00aa
        L_0x00a7:
            r0 = 2131756755(0x7f1006d3, float:1.9144426E38)
        L_0x00aa:
            r6.setMonthUsedText(r0)
            r15 = 1
            r9 = r6
            r10 = r4
            r12 = r17
            r14 = r19
            r9.setMonthPackageInfo(r10, r12, r14, r15)
            if (r23 == 0) goto L_0x00c6
            r0 = r21
            r1 = r22
            r2 = r17
            r9 = r6
            r6 = r19
            r0.doUpdateBgView(r1, r2, r4, r6)
            goto L_0x00c7
        L_0x00c6:
            r9 = r6
        L_0x00c7:
            long r0 = r16.getBillPackageRemained()
            r9.setBillRemainedTextView(r0)
            com.miui.networkassistant.config.SimUserInfo r0 = r21.getSimUserInfo(r22)
            long r0 = r0.getDataUsageCorrectedTime()
            r9.setPreAdjustTime(r0)
            r9.unRegisterMonthRemainedClickListener()
            r9.unRegisterBillLayoutClickListener()
            boolean r0 = r16.isDataRoaming()
            if (r0 != 0) goto L_0x0119
            boolean r0 = r21.isTotalDataUsageSetted(r22)
            if (r0 == 0) goto L_0x0119
            boolean r0 = r16.isOperatorSetted()
            if (r0 == 0) goto L_0x0119
            java.lang.String r0 = r16.getTrafficSmsDetail()
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x010a
            com.miui.networkassistant.config.SimUserInfo[] r0 = r7.mSimUserInfo
            r0 = r0[r8]
            int r0 = r0.getBrand()
            if (r0 != 0) goto L_0x010a
            android.view.View$OnClickListener r0 = r7.mOnClickListener
            r9.setMonthRemainedClickListener(r0)
        L_0x010a:
            java.lang.String r0 = r16.getBillSmsDetail()
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0119
            android.view.View$OnClickListener r0 = r7.mOnClickListener
            r9.setBillLayoutClickListener(r0)
        L_0x0119:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.NetworkAssistantActivity.updateNormalTraffic(int, boolean):void");
    }

    private void updateOverSeaTraffic(int i, boolean z) {
        MainTrafficUsedView trafficUsedView = getTrafficUsedView(i);
        SimUserInfo simUserInfo = getSimUserInfo(i);
        trafficUsedView.setBillLayoutVisible(false);
        if (simUserInfo.isTotalDataUsageSetted()) {
            trafficUsedView.setDataUsageButtonText((int) R.string.main_button_usage_adjust_manual);
            updateOverseaTraffic(i, z);
            return;
        }
        showNoTotalPackageView(i);
    }

    private void updateOverseaTraffic(int i, boolean z) {
        int i2 = i;
        if (this.mTrafficManageBinder != null) {
            MainTrafficUsedView trafficUsedView = getTrafficUsedView(i);
            SimUserInfo simUserInfo = getSimUserInfo(i);
            try {
                long currentMonthTotalPackage = this.mTrafficManageBinder.getCurrentMonthTotalPackage(i2);
                float dataUsageWarning = simUserInfo.getDataUsageWarning();
                long correctedNormalMonthDataUsageUsed = this.mTrafficManageBinder.getCorrectedNormalMonthDataUsageUsed(i2);
                long j = correctedNormalMonthDataUsageUsed;
                long todayDataUsageUsed = this.mTrafficManageBinder.getTodayDataUsageUsed(i2);
                trafficUsedView.setMonthPackageInfo(correctedNormalMonthDataUsageUsed, currentMonthTotalPackage, dataUsageWarning, true);
                trafficUsedView.setTodayUsed(todayDataUsageUsed);
                trafficUsedView.setLeisureTrafficRemained(false, 0);
                if (z) {
                    doUpdateBgView(i, currentMonthTotalPackage, j, dataUsageWarning);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTrafficCtl(int i) {
        MainTrafficUsedView trafficUsedView = getTrafficUsedView(i);
        updateTrafficUsedOnly(trafficUsedView, i);
        trafficUsedView.showPrimaryMessage((int) R.string.main_alert_message_open_traffic_ctrl);
        trafficUsedView.setDataUsageButtonText((int) R.string.main_open_traffic_ctrl_button);
    }

    private void updateTrafficUsedOnly(MainTrafficUsedView mainTrafficUsedView, int i) {
        ITrafficManageBinder iTrafficManageBinder = this.mTrafficManageBinder;
        if (iTrafficManageBinder != null) {
            try {
                long correctedNormalMonthDataUsageUsed = iTrafficManageBinder.getCorrectedNormalMonthDataUsageUsed(i);
                long todayDataUsageUsed = this.mTrafficManageBinder.getTodayDataUsageUsed(i);
                mainTrafficUsedView.setMonthPackageInfo(correctedNormalMonthDataUsageUsed, 0, 0.0f, false);
                mainTrafficUsedView.setTodayUsed(todayDataUsageUsed);
                mainTrafficUsedView.setLeisureTrafficRemained(false, 0);
                mainTrafficUsedView.setCardStyle(getTitleOperatorName(i), 0, 1.0f - (((float) correctedNormalMonthDataUsageUsed) / ((float) this.mTrafficManageBinder.getCurrentMonthTotalPackage(i))), i);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUnLimitedCardTraffic(int i, boolean z) {
        if (this.mTrafficManageBinder != null) {
            showTrafficAdjusting(i);
            MainTrafficUsedView trafficUsedView = getTrafficUsedView(i);
            SimUserInfo simUserInfo = getSimUserInfo(i);
            try {
                trafficUsedView.setTodayUsed(this.mTrafficManageBinder.getTodayDataUsageUsed(i));
                trafficUsedView.setHasLeisure(false);
                trafficUsedView.setLeisureTrafficRemained(false, 0);
                trafficUsedView.setUnlimitedMonthPackageInfo(this.mTrafficManageBinder.getCorrectedNormalMonthDataUsageUsed(i), this.mAppContext.getResources().getString(R.string.not_limited_brand));
                if (z) {
                    doUpdateBgView(i, Long.MAX_VALUE, 0, 1.0f);
                }
                trafficUsedView.setBillRemainedTextView(simUserInfo.getBillPackageRemained());
                trafficUsedView.setPreAdjustTime(simUserInfo.getDataUsageCorrectedTime());
                trafficUsedView.unRegisterMonthRemainedClickListener();
                trafficUsedView.unRegisterBillLayoutClickListener();
                if (!simUserInfo.isDataRoaming() && !TextUtils.isEmpty(simUserInfo.getBillSmsDetail())) {
                    trafficUsedView.setBillLayoutClickListener(this.mOnClickListener);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateVirtualSimTraffic(int i) {
        MainTrafficUsedView trafficUsedView = getTrafficUsedView(i);
        trafficUsedView.setHasLeisure(false);
        trafficUsedView.setDataUsageButtonText((int) R.string.main_button_mi_sim_open);
        trafficUsedView.setLeisureTrafficRemained(false, 0);
        VirtualSimInfo parseVirtualSimInfo = VirtualSimUtil.parseVirtualSimInfo(this.mAppContext);
        if (parseVirtualSimInfo != null) {
            trafficUsedView.setTodayUsed(parseVirtualSimInfo.getAssistKey1());
            trafficUsedView.setMainTodayUsedTextView(parseVirtualSimInfo.getAssistKey1Title());
            trafficUsedView.setMonthRemain(parseVirtualSimInfo.getAssistCenter());
            trafficUsedView.showPrimaryMessage(parseVirtualSimInfo.getAssistCenter() == -1 ? getString(R.string.main_primary_message_traffic_remain) : parseVirtualSimInfo.getAssistCenterTitle());
            if (TextUtils.isEmpty(parseVirtualSimInfo.getAssistKey2Title())) {
                trafficUsedView.setMonthPackageViewVisible(false);
            } else {
                trafficUsedView.setMonthPackage(parseVirtualSimInfo.getAssistKey2());
                trafficUsedView.setMainMonthPackageTextView(parseVirtualSimInfo.getAssistKey2Title());
            }
            trafficUsedView.setBillRemainedTextView(parseVirtualSimInfo.getAssistBalance());
            trafficUsedView.setMainBillRemainTextView(parseVirtualSimInfo.getAssistBalanceTitle());
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mHandler = new UiHandler(this);
        parseSlotNum();
        initView();
        bindTrafficManageService();
    }

    /* access modifiers changed from: protected */
    public int onCreateContentView() {
        return R.layout.activity_networkassistant;
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.networkassistant.ui.NetworkAssistantActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCustomizeActionBar(ActionBar actionBar) {
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.pc_main_activity_background_color)));
        actionBar.setDisplayOptions(28);
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(R.drawable.v_setting_icon);
        imageView.setContentDescription(getString(R.string.activity_title_settings));
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                g.startWithFragment(NetworkAssistantActivity.this.mActivity, SettingFragment.class);
                AnalyticsHelper.trackMainButtonClickCountEvent("settings");
            }
        });
        actionBar.setEndView(imageView);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        NetworkAssistantActivity.super.onDestroy();
        MainTrafficUsedView trafficUsedView = getTrafficUsedView(this.mCurrentOperateSlotNum);
        if (trafficUsedView != null) {
            trafficUsedView.onDestroy();
        }
        this.mHandler.removeCallbacksAndMessages((Object) null);
        ITrafficCornBinder[] iTrafficCornBinderArr = this.mTrafficCornBinders;
        if (iTrafficCornBinderArr[0] != null) {
            try {
                iTrafficCornBinderArr[0].unRegisterLisener(this.mTrafficCornBinderListenerHost.getStub());
            } catch (RemoteException e) {
                Log.i(TAG, "unRegisterListener slot 1", e);
            }
        }
        ITrafficCornBinder[] iTrafficCornBinderArr2 = this.mTrafficCornBinders;
        if (iTrafficCornBinderArr2[1] != null) {
            try {
                iTrafficCornBinderArr2[1].unRegisterLisener(this.mTrafficCornBinderListenerHost.getStub());
            } catch (RemoteException e2) {
                Log.i(TAG, "unRegisterListener slot 2", e2);
            }
        }
        unbindTrafficManageService();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        MainTrafficUsedView trafficUsedView = getTrafficUsedView(this.mCurrentOperateSlotNum);
        if (trafficUsedView != null) {
            trafficUsedView.onPause();
        }
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        NetworkAssistantActivity.super.onRestart();
        initData();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        MainTrafficUsedView trafficUsedView = getTrafficUsedView(this.mCurrentOperateSlotNum);
        if (trafficUsedView != null) {
            trafficUsedView.onResume();
        }
    }
}
