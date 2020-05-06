package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.B;
import b.b.c.j.g;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.model.DataUsageConstants;
import com.miui.networkassistant.model.TrafficInfo;
import com.miui.networkassistant.service.FirewallService;
import com.miui.networkassistant.service.IFirewallBinder;
import com.miui.networkassistant.service.ts.TrafficStatisticManager;
import com.miui.networkassistant.ui.activity.TrafficSortedActivity;
import com.miui.networkassistant.ui.adapter.TrafficSortedAppListAdapter;
import com.miui.networkassistant.ui.base.ListFragment;
import com.miui.networkassistant.ui.dialog.SingleChoiceItemsDialog;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.networkassistant.utils.TextPrepareUtil;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.List;
import miui.widget.DropDownSingleChoiceMenu;

public class TrafficSortedFragment extends ListFragment implements View.OnClickListener, TrafficStatisticManager.TrafficStatisticListener, TrafficSortedAppListAdapter.OnItemClickListener {
    private static final String BUNDLE_SLOT_NUM_TAG = "slot_num_tag";
    private static final int MSG_FIREWALL_SERVICE_CONNECTED = 3;
    private static final int MSG_TRAFFIC_DATA_UPDATE = 2;
    private int mAdapterType;
    private long[] mAllAppDataUsageTotal;
    private List<TrafficInfo> mAppTrafficInfoList;
    /* access modifiers changed from: private */
    public boolean mDataReady = false;
    private String[] mDateTypePrefix;
    /* access modifiers changed from: private */
    public IFirewallBinder mFirewallBinder;
    private ServiceConnection mFirewallServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IFirewallBinder unused = TrafficSortedFragment.this.mFirewallBinder = IFirewallBinder.Stub.asInterface(iBinder);
            TrafficSortedFragment.this.mHandler.sendEmptyMessage(3);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            IFirewallBinder unused = TrafficSortedFragment.this.mFirewallBinder = null;
        }
    };
    /* access modifiers changed from: private */
    public UIHandler mHandler = new UIHandler(this);
    private String mImsi;
    private SimCardHelper mSimCardHelper;
    private MySingleChoiceItemsDialogListener mSingleChoiceItemsDialogListener;
    private int mSlotNum = 0;
    private SingleChoiceItemsDialog mSortChoiceDialog;
    private ImageView mSortedButton;
    /* access modifiers changed from: private */
    public int mSortedType;
    private String[] mSpinnerTitleTxt;
    private View mTitleLayout;
    /* access modifiers changed from: private */
    public int mTitleType;
    private TextView mTitleView;
    /* access modifiers changed from: private */
    public TrafficSortedAppListAdapter mTrafficAdapter;
    /* access modifiers changed from: private */
    public TrafficStatisticManager mTrafficStatisticManager;
    private String[] mTrafficType;

    private static class MySingleChoiceItemsDialogListener implements SingleChoiceItemsDialog.SingleChoiceItemsDialogListener {
        private WeakReference<TrafficSortedFragment> fragmentRef;

        MySingleChoiceItemsDialogListener(TrafficSortedFragment trafficSortedFragment) {
            this.fragmentRef = new WeakReference<>(trafficSortedFragment);
        }

        public void onSelectItemUpdate(int i, int i2) {
            TrafficSortedFragment trafficSortedFragment = (TrafficSortedFragment) this.fragmentRef.get();
            if (trafficSortedFragment != null) {
                int unused = trafficSortedFragment.mSortedType = i;
                trafficSortedFragment.mTrafficStatisticManager.setDataUsageType(trafficSortedFragment.mSortedType);
            }
        }
    }

    static class UIHandler extends Handler {
        private WeakReference<TrafficSortedFragment> mFragmentRef;

        UIHandler(TrafficSortedFragment trafficSortedFragment) {
            this.mFragmentRef = new WeakReference<>(trafficSortedFragment);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            TrafficSortedFragment trafficSortedFragment = (TrafficSortedFragment) this.mFragmentRef.get();
            if (trafficSortedFragment != null) {
                int i = message.what;
                if (i == 2) {
                    boolean unused = trafficSortedFragment.mDataReady = true;
                } else if (i != 3) {
                    return;
                }
                trafficSortedFragment.updateData();
            }
        }
    }

    private void bindFirewallService() {
        Activity activity = this.mActivity;
        g.a((Context) activity, new Intent(activity, FirewallService.class), this.mFirewallServiceConnection, 1, B.k());
    }

    private void bindTrafficStatisticService() {
        this.mTrafficStatisticManager = new TrafficStatisticManager(this.mAppContext, this.mImsi, this.mSortedType);
        this.mTrafficStatisticManager.registerListener(this);
    }

    private String getSpinnerTitleText(int i) {
        long j = this.mAllAppDataUsageTotal[i];
        return this.mDateTypePrefix[i] + this.mTrafficType[this.mSortedType] + FormatBytesUtil.formatBytes(this.mAppContext, j);
    }

    private void initData() {
        int i;
        Resources resources;
        if (DeviceUtil.IS_CM_CUSTOMIZATION_TEST) {
            this.mSpinnerTitleTxt = getResources().getStringArray(R.array.date_of_traffic_cmcc);
            resources = getResources();
            i = R.array.date_of_traffic_prefix_cmcc;
        } else {
            this.mSpinnerTitleTxt = getResources().getStringArray(R.array.date_of_traffic);
            resources = getResources();
            i = R.array.date_of_traffic_prefix;
        }
        this.mDateTypePrefix = resources.getStringArray(i);
        this.mTrafficType = getResources().getStringArray(R.array.traffic_type);
        Intent intent = getActivity().getIntent();
        this.mAdapterType = intent.getIntExtra(DataUsageConstants.BUNDLE_SYSTEM_APP, 0);
        this.mTitleType = intent.getIntExtra(DataUsageConstants.BUNDLE_TITLE_TYPE, 1);
        this.mSortedType = intent.getIntExtra(DataUsageConstants.BUNDLE_SORT_TYPE, 0);
        parseSlotNum(intent.getBooleanExtra(BUNDLE_SLOT_NUM_TAG, false));
        this.mImsi = this.mSimCardHelper.getSimImsi(this.mSlotNum);
    }

    private void initViewDelay() {
        this.mTitleLayout = findViewById(R.id.layout_show);
        this.mTitleView = (TextView) findViewById(R.id.list_spinner_title);
        this.mTitleLayout.setOnClickListener(this);
        if (this.mSingleChoiceItemsDialogListener == null) {
            this.mSingleChoiceItemsDialogListener = new MySingleChoiceItemsDialogListener(this);
        }
        this.mSortChoiceDialog = new SingleChoiceItemsDialog(this.mActivity, this.mSingleChoiceItemsDialogListener);
        initData();
        onResetTitle();
        bindTrafficStatisticService();
        bindFirewallService();
    }

    private void onResetTitle() {
        if (this.mSimCardHelper.isDualSimInserted()) {
            setTitle(TextPrepareUtil.getDualCardTitle(this.mAppContext, getTitle(), this.mSlotNum));
        }
    }

    private void parseSlotNum(boolean z) {
        if (z) {
            this.mSlotNum = this.mSimCardHelper.getCurrentMobileSlotNum();
            Sim.operateOnSlotNum(this.mSlotNum);
        } else if (DeviceUtil.IS_DUAL_CARD) {
            this.mSlotNum = Sim.getCurrentOptSlotNum();
            Bundle arguments = getArguments();
            if (arguments != null && arguments.containsKey(Sim.SIM_SLOT_NUM_TAG)) {
                this.mSlotNum = arguments.getInt(Sim.SIM_SLOT_NUM_TAG, 0);
                Sim.operateOnSlotNum(this.mSlotNum);
            }
        }
    }

    private void showTrafficMenuItem() {
        DropDownSingleChoiceMenu dropDownSingleChoiceMenu = new DropDownSingleChoiceMenu(this.mActivity);
        dropDownSingleChoiceMenu.setItems(this.mSpinnerTitleTxt);
        dropDownSingleChoiceMenu.setSelectedItem(this.mTitleType);
        dropDownSingleChoiceMenu.setAnchorView(this.mTitleLayout);
        dropDownSingleChoiceMenu.setOnMenuListener(new DropDownSingleChoiceMenu.OnMenuListener() {
            public void onDismiss() {
            }

            public void onItemSelected(DropDownSingleChoiceMenu dropDownSingleChoiceMenu, int i) {
                if (TrafficSortedFragment.this.mDataReady) {
                    int unused = TrafficSortedFragment.this.mTitleType = i;
                    TrafficSortedFragment.this.mTrafficAdapter.setMode(TrafficSortedFragment.this.mTitleType);
                    TrafficSortedFragment.this.updateSpinnerTitle();
                    dropDownSingleChoiceMenu.dismiss();
                }
            }

            public void onShow() {
            }
        });
        dropDownSingleChoiceMenu.show();
    }

    private void startSystemAppActivity() {
        Intent intent = new Intent();
        intent.putExtra(DataUsageConstants.BUNDLE_SYSTEM_APP, 1);
        intent.putExtra(DataUsageConstants.BUNDLE_TITLE_TYPE, this.mTitleType);
        intent.putExtra(DataUsageConstants.BUNDLE_SORT_TYPE, this.mSortedType);
        intent.setClass(this.mActivity, TrafficSortedActivity.class);
        this.mActivity.startActivity(intent);
    }

    private void unBindFirewallService() {
        if (this.mFirewallBinder != null) {
            this.mActivity.unbindService(this.mFirewallServiceConnection);
        }
    }

    private void unBindTrafficStatisticService() {
        TrafficStatisticManager trafficStatisticManager = this.mTrafficStatisticManager;
        if (trafficStatisticManager != null) {
            trafficStatisticManager.unRegisterListener(this);
            this.mTrafficStatisticManager.quitStatistic();
        }
    }

    private void updateAppTotalTraffic() {
        long[] systemAppDataUsageTotal;
        int i = this.mAdapterType;
        boolean z = true;
        if (i == 0) {
            this.mAppTrafficInfoList = this.mTrafficStatisticManager.getNonSystemAppsListLocked();
            systemAppDataUsageTotal = this.mTrafficStatisticManager.getAllAppDataUsageTotal();
        } else {
            if (i == 1) {
                this.mAppTrafficInfoList = this.mTrafficStatisticManager.getSystemAppListLocked();
                systemAppDataUsageTotal = this.mTrafficStatisticManager.getSystemAppDataUsageTotal();
            }
            this.mTrafficAdapter.setFirewall(this.mFirewallBinder);
            this.mTrafficAdapter.setData(this.mAppTrafficInfoList, this.mTitleType, this.mSortedType, this.mSlotNum);
            List<TrafficInfo> list = this.mAppTrafficInfoList;
            if (!(list == null || list.size() == 0)) {
                z = false;
            }
            showEmptyView(z);
        }
        this.mAllAppDataUsageTotal = systemAppDataUsageTotal;
        this.mTrafficAdapter.setFirewall(this.mFirewallBinder);
        this.mTrafficAdapter.setData(this.mAppTrafficInfoList, this.mTitleType, this.mSortedType, this.mSlotNum);
        List<TrafficInfo> list2 = this.mAppTrafficInfoList;
        z = false;
        showEmptyView(z);
    }

    /* access modifiers changed from: private */
    public void updateData() {
        if (isAttatched() && this.mDataReady && this.mFirewallBinder != null && this.mTrafficStatisticManager != null) {
            updateAppTotalTraffic();
            updateSpinnerTitle();
        }
    }

    /* access modifiers changed from: private */
    public void updateSpinnerTitle() {
        String spinnerTitleText = getSpinnerTitleText(this.mTitleType);
        TextView textView = this.mTitleView;
        if (textView != null) {
            textView.setText(spinnerTitleText);
        }
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.mSimCardHelper = SimCardHelper.getInstance(this.mAppContext);
        initViewDelay();
    }

    public void onAppTrafficStatisticUpdated() {
        this.mHandler.sendEmptyMessage(2);
    }

    public void onClick(View view) {
        if (view == this.mSortedButton) {
            this.mSortChoiceDialog.buildDialog(this.mAppContext.getString(R.string.sorted_dialog_title), this.mTrafficType, this.mSortedType, 0);
        } else if (view == this.mTitleLayout) {
            showTrafficMenuItem();
        }
    }

    /* access modifiers changed from: protected */
    public View onCreateFooterView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return null;
    }

    /* access modifiers changed from: protected */
    public View onCreateHeaderView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        View inflate = layoutInflater.inflate(R.layout.listitem_traffic_sorted_header, viewGroup, false);
        inflate.setBackgroundColor(getResources().getColor(R.color.na_bg));
        return inflate;
    }

    /* access modifiers changed from: protected */
    public RecyclerView.a onCreateListAdapter() {
        this.mTrafficAdapter = new TrafficSortedAppListAdapter(this.mActivity, (List<TrafficInfo>) null, this.mAdapterType, this.mSlotNum);
        this.mTrafficAdapter.setOnItemClickListener(this);
        return this.mTrafficAdapter;
    }

    /* access modifiers changed from: protected */
    public void onCreateView2(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView2(layoutInflater, viewGroup, bundle);
        findViewById(R.id.view_root).setBackgroundColor(getResources().getColor(R.color.na_nd_bg));
        showEmptyView(false);
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        actionBar.setDisplayOptions(16, 16);
        this.mSortedButton = new ImageView(this.mActivity);
        this.mSortedButton.setBackgroundResource(R.drawable.selector_actionbar_switch);
        this.mSortedButton.setContentDescription(this.mAppContext.getString(R.string.sorted_dialog_title));
        this.mSortedButton.setOnClickListener(this);
        if (!(actionBar instanceof miui.app.ActionBar)) {
            return 0;
        }
        ((miui.app.ActionBar) actionBar).setEndView(this.mSortedButton);
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        unBindTrafficStatisticService();
        unBindFirewallService();
        this.mHandler.removeCallbacksAndMessages((Object) null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0013, code lost:
        r0 = r9.mAppInfo;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onItemClick(int r9) {
        /*
            r8 = this;
            boolean r0 = r8.mDataReady
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            com.miui.networkassistant.ui.adapter.TrafficSortedAppListAdapter r0 = r8.mTrafficAdapter
            java.util.List r0 = r0.getData()
            java.lang.Object r9 = r0.get(r9)
            com.miui.networkassistant.model.TrafficInfo r9 = (com.miui.networkassistant.model.TrafficInfo) r9
            if (r9 == 0) goto L_0x0061
            com.miui.networkassistant.model.AppInfo r0 = r9.mAppInfo
            int r1 = r0.uid
            r2 = -5
            if (r1 == r2) goto L_0x0061
            r2 = -4
            if (r1 != r2) goto L_0x001e
            goto L_0x0061
        L_0x001e:
            java.lang.CharSequence r0 = r0.packageName
            java.lang.String r0 = r0.toString()
            int r1 = r8.mAdapterType
            if (r1 != 0) goto L_0x0048
            boolean r1 = com.miui.networkassistant.utils.HybirdServiceUtil.isHybirdService(r0)
            if (r1 == 0) goto L_0x0048
            android.content.Context r1 = r8.mAppContext
            boolean r1 = com.miui.networkassistant.utils.HybirdServiceUtil.isHybirdIntentExist(r1)
            if (r1 == 0) goto L_0x0058
            android.content.Context r2 = r8.mAppContext
            int r3 = r8.mTitleType
            com.miui.networkassistant.model.TrafficInfo$AppStatistic r9 = r9.mAppStats
            long[] r9 = r9.mTotalBytes
            r4 = r9[r3]
            int r6 = r8.mSortedType
            java.lang.String r7 = r8.mImsi
            com.miui.networkassistant.utils.HybirdServiceUtil.startHybirdTrafficSortActivity(r2, r3, r4, r6, r7)
            goto L_0x0061
        L_0x0048:
            int r1 = r8.mAdapterType
            if (r1 != 0) goto L_0x0058
            com.miui.networkassistant.model.AppInfo r9 = r9.mAppInfo
            int r9 = r9.uid
            r1 = -10
            if (r9 != r1) goto L_0x0058
            r8.startSystemAppActivity()
            goto L_0x0061
        L_0x0058:
            android.app.Activity r9 = r8.mActivity
            int r1 = r8.mTitleType
            int r2 = r8.mSortedType
            com.miui.networkassistant.ui.fragment.ShowAppDetailFragment.startAppDetailFragment(r9, r0, r1, r2)
        L_0x0061:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.fragment.TrafficSortedFragment.onItemClick(int):void");
    }

    public void onResume() {
        super.onResume();
        this.mTrafficAdapter.notifyDataSetChanged();
    }
}
