package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.model.AppDataUsage;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.model.DataUsageConstants;
import com.miui.networkassistant.service.wrapper.AppMonitorWrapper;
import com.miui.networkassistant.traffic.statistic.MiServiceFrameworkHelper;
import com.miui.networkassistant.traffic.statistic.StatisticAppTraffic;
import com.miui.networkassistant.ui.adapter.MIServiceAppDetailListAdapter;
import com.miui.networkassistant.ui.base.ListFragment;
import com.miui.networkassistant.ui.dialog.SingleChoiceItemsDialog;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.securitycenter.R;
import java.util.List;
import miui.widget.DropDownSingleChoiceMenu;

public class ShowMiServiceAppDetailFragment extends ListFragment implements View.OnClickListener {
    private static final int TITLE_FILED = 2131756886;
    private List<MIServiceAppDetailListAdapter.MiAppInfo> mAllAppList;
    /* access modifiers changed from: private */
    public AppInfo mAppInfo;
    private String mCurrentImsi;
    private String[] mDateTypePrefix;
    /* access modifiers changed from: private */
    public SparseArray<AppDataUsage[]> mMobileTraffic;
    private int mSlotNum = 0;
    private SingleChoiceItemsDialog mSortChoiceDialog;
    private ImageView mSortedButton;
    private SingleChoiceItemsDialog.SingleChoiceItemsDialogListener mSortedChoiceListener = new SingleChoiceItemsDialog.SingleChoiceItemsDialogListener() {
        public void onSelectItemUpdate(int i, int i2) {
            int unused = ShowMiServiceAppDetailFragment.this.mSortedType = i;
            ShowMiServiceAppDetailFragment.this.updateTrafficSorted();
            ShowMiServiceAppDetailFragment.this.updateSpinnerTitle();
        }
    };
    /* access modifiers changed from: private */
    public int mSortedType = 0;
    private String[] mSpinnerTitleTxt;
    /* access modifiers changed from: private */
    public StatisticAppTraffic mStatisticAppTraffic;
    private View mTitleLayout;
    /* access modifiers changed from: private */
    public int mTitleType = 1;
    private TextView mTitleView;
    private long mTotalTraffic;
    private MIServiceAppDetailListAdapter mTrafficAdapter;
    private String[] mTrafficType;
    /* access modifiers changed from: private */
    public SparseArray<AppDataUsage[]> mWifiTraffic;
    private MiServiceFrameworkHelper miHelper;

    private String getSpinnerTitleText(int i) {
        return this.mDateTypePrefix[i] + this.mTrafficType[this.mSortedType] + FormatBytesUtil.formatBytes(this.mAppContext, this.mTotalTraffic);
    }

    /* access modifiers changed from: private */
    public void initData() {
        updateTrafficSorted();
        updateSpinnerTitle();
    }

    private void initViewDelayed() {
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
        this.mTrafficType = getResources().getStringArray(R.array.mi_service_traffic_type);
        this.mTitleLayout = findViewById(R.id.layout_show);
        this.mTitleLayout.setOnClickListener(this);
        this.mTitleView = (TextView) findViewById(R.id.list_spinner_title);
        this.mSortChoiceDialog = new SingleChoiceItemsDialog(this.mActivity, this.mSortedChoiceListener);
    }

    private void onResetTitle() {
        if (DeviceUtil.IS_DUAL_CARD && SimCardHelper.getInstance(this.mAppContext).isDualSimInserted()) {
            Object[] objArr = new Object[2];
            objArr[0] = getTitle();
            objArr[1] = getString(this.mSlotNum == 0 ? R.string.dual_setting_simcard1 : R.string.dual_setting_simcard2);
            setTitle(String.format("%s-%s", objArr));
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
                int unused = ShowMiServiceAppDetailFragment.this.mTitleType = i;
                ShowMiServiceAppDetailFragment.this.updateTrafficData();
                ShowMiServiceAppDetailFragment.this.updateSpinnerTitle();
                dropDownSingleChoiceMenu.dismiss();
            }

            public void onShow() {
            }
        });
        dropDownSingleChoiceMenu.show();
    }

    private void updateAppTraffic() {
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                ShowMiServiceAppDetailFragment showMiServiceAppDetailFragment = ShowMiServiceAppDetailFragment.this;
                SparseArray unused = showMiServiceAppDetailFragment.mMobileTraffic = showMiServiceAppDetailFragment.mStatisticAppTraffic.buildMobileDataUsage(ShowMiServiceAppDetailFragment.this.mAppInfo.uid, false);
                ShowMiServiceAppDetailFragment showMiServiceAppDetailFragment2 = ShowMiServiceAppDetailFragment.this;
                SparseArray unused2 = showMiServiceAppDetailFragment2.mWifiTraffic = showMiServiceAppDetailFragment2.mStatisticAppTraffic.buildWifiDataUsage(ShowMiServiceAppDetailFragment.this.mAppInfo.uid, false);
                return null;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Void voidR) {
                super.onPostExecute(voidR);
                ShowMiServiceAppDetailFragment.this.initData();
                if (ShowMiServiceAppDetailFragment.this.mStatisticAppTraffic != null) {
                    ShowMiServiceAppDetailFragment.this.mStatisticAppTraffic.closeSession();
                }
            }
        }.execute(new Void[0]);
    }

    /* access modifiers changed from: private */
    public void updateSpinnerTitle() {
        String spinnerTitleText = getSpinnerTitleText(this.mTitleType);
        TextView textView = this.mTitleView;
        if (textView != null) {
            textView.setText(spinnerTitleText);
            this.mTrafficAdapter.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0052  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateTrafficData() {
        /*
            r8 = this;
            com.miui.networkassistant.traffic.statistic.MiServiceFrameworkHelper r0 = r8.miHelper
            int r1 = r8.mTitleType
            int r2 = r8.mSortedType
            java.lang.String r3 = r8.mCurrentImsi
            java.util.ArrayList r0 = r0.query(r1, r2, r3)
            r8.mAllAppList = r0
            com.miui.networkassistant.traffic.statistic.MiServiceFrameworkHelper r0 = r8.miHelper
            long r0 = r0.getTotalTraffic()
            com.miui.networkassistant.ui.adapter.MIServiceAppDetailListAdapter$MiAppInfo r2 = new com.miui.networkassistant.ui.adapter.MIServiceAppDetailListAdapter$MiAppInfo
            r2.<init>()
            java.lang.String r3 = "com.xiaomi.xmsf"
            r2.packageName = r3
            int r3 = r8.mSortedType
            r4 = 1
            r5 = 0
            if (r3 == 0) goto L_0x0033
            if (r3 == r4) goto L_0x0026
            goto L_0x0045
        L_0x0026:
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r3 = r8.mWifiTraffic
            int r6 = r8.mTitleType
            java.lang.Object r3 = r3.get(r6)
            com.miui.networkassistant.model.AppDataUsage[] r3 = (com.miui.networkassistant.model.AppDataUsage[]) r3
            r3 = r3[r5]
            goto L_0x003f
        L_0x0033:
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r3 = r8.mMobileTraffic
            int r6 = r8.mTitleType
            java.lang.Object r3 = r3.get(r6)
            com.miui.networkassistant.model.AppDataUsage[] r3 = (com.miui.networkassistant.model.AppDataUsage[]) r3
            r3 = r3[r5]
        L_0x003f:
            long r6 = r3.getTotal()
            r8.mTotalTraffic = r6
        L_0x0045:
            long r6 = r8.mTotalTraffic
            long r6 = r6 - r0
            r2.totalTraffic = r6
            long r0 = r2.totalTraffic
            r6 = 0
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 <= 0) goto L_0x0057
            java.util.List<com.miui.networkassistant.ui.adapter.MIServiceAppDetailListAdapter$MiAppInfo> r0 = r8.mAllAppList
            r0.add(r5, r2)
        L_0x0057:
            com.miui.networkassistant.ui.adapter.MIServiceAppDetailListAdapter r0 = r8.mTrafficAdapter
            java.util.List<com.miui.networkassistant.ui.adapter.MIServiceAppDetailListAdapter$MiAppInfo> r1 = r8.mAllAppList
            r0.setData(r1)
            java.util.List<com.miui.networkassistant.ui.adapter.MIServiceAppDetailListAdapter$MiAppInfo> r0 = r8.mAllAppList
            if (r0 == 0) goto L_0x006a
            int r0 = r0.size()
            if (r0 != 0) goto L_0x0069
            goto L_0x006a
        L_0x0069:
            r4 = r5
        L_0x006a:
            r8.showEmptyView(r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.fragment.ShowMiServiceAppDetailFragment.updateTrafficData():void");
    }

    /* access modifiers changed from: private */
    public void updateTrafficSorted() {
        updateTrafficData();
        this.mTrafficAdapter.trafficSorted(this.mSortedType);
    }

    /* access modifiers changed from: protected */
    public void initView() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            String string = arguments.getString("package_name");
            int i = arguments.getInt(DataUsageConstants.BUNDLE_TITLE_TYPE, this.mTitleType);
            this.mSortedType = i / 4;
            this.mTitleType = i % 4;
            this.mAppInfo = AppMonitorWrapper.getInstance(this.mAppContext).getAppInfoByPackageName(string);
        }
        if (this.mAppInfo == null) {
            finish();
            return;
        }
        this.mSlotNum = Sim.getCurrentOptSlotNum();
        onResetTitle();
        this.mCurrentImsi = SimCardHelper.getInstance(this.mAppContext).getSimImsi(this.mSlotNum);
        this.mStatisticAppTraffic = new StatisticAppTraffic(this.mAppContext, this.mCurrentImsi);
        this.miHelper = new MiServiceFrameworkHelper(this.mAppContext);
        setEmptyText((int) R.string.usage_sorted_empty_text);
        initViewDelayed();
        updateAppTraffic();
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
        return layoutInflater.inflate(R.layout.listitem_traffic_sorted_header, viewGroup, false);
    }

    /* access modifiers changed from: protected */
    public RecyclerView.a onCreateListAdapter() {
        this.mTrafficAdapter = new MIServiceAppDetailListAdapter(this.mActivity, (List<MIServiceAppDetailListAdapter.MiAppInfo>) null);
        return this.mTrafficAdapter;
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

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.mi_service_traffic_detail;
    }
}
