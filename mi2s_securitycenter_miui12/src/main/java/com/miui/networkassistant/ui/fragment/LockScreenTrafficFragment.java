package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.c.b.g;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.service.wrapper.AppMonitorWrapper;
import com.miui.networkassistant.traffic.purchase.PurchaseUtil;
import com.miui.networkassistant.ui.adapter.LockScreenAppListAdapter;
import com.miui.networkassistant.ui.base.ListFragment;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashMap;

public class LockScreenTrafficFragment extends ListFragment {
    public static final String BUNDLE_KEY_LIST_HEADER = "list_header";
    public static final String BUNDLE_KEY_UID_MAP = "uid_map";
    private static final int MSG_UPDATE_DATA = 0;
    private static final int SETTING_BUTTON_ID = 1;
    private static final int TITLE_FILED = 2131756674;
    /* access modifiers changed from: private */
    public LockScreenAppListAdapter mAdapter;
    private AppMonitorWrapper.AppMonitorListener mAppMonitorListener = new AppMonitorWrapper.AppMonitorListener() {
        public void onAppListUpdated() {
            LockScreenTrafficFragment.this.mHandler.sendEmptyMessage(0);
        }
    };
    /* access modifiers changed from: private */
    public AppMonitorWrapper mAppMonitorWrapper;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            Bundle arguments;
            if (message.what == 0 && (arguments = LockScreenTrafficFragment.this.getArguments()) != null) {
                HashMap hashMap = (HashMap) arguments.getSerializable(LockScreenTrafficFragment.BUNDLE_KEY_UID_MAP);
                ArrayList<AppInfo> filteredAppInfosList = LockScreenTrafficFragment.this.mAppMonitorWrapper.getFilteredAppInfosList();
                if (hashMap != null && filteredAppInfosList != null && !filteredAppInfosList.isEmpty()) {
                    LockScreenTrafficFragment.this.mAdapter.setData(filteredAppInfosList, hashMap);
                    LockScreenTrafficFragment.this.showEmptyView(false);
                }
            }
        }
    };
    private LockScreenAppListAdapter.OnItemClickListener mItemClickListener = new LockScreenAppListAdapter.OnItemClickListener() {
        public void onItemClick(int i) {
            String charSequence = LockScreenTrafficFragment.this.mAdapter.getData().get(i).appInfo.packageName.toString();
            Sim.operateOnSlotNum(Sim.getCurrentActiveSlotNum());
            ShowAppDetailFragment.startAppDetailFragment(LockScreenTrafficFragment.this.mActivity, charSequence);
        }
    };
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            int id = view.getId();
            if (id == 1) {
                Bundle bundle = new Bundle();
                bundle.putInt(Sim.SIM_SLOT_NUM_TAG, Sim.getCurrentActiveSlotNum());
                g.startWithFragment(LockScreenTrafficFragment.this.mActivity, TrafficLimitSettingFragment.class, bundle);
            } else if (id == R.id.textview_traffic_purchase) {
                PurchaseUtil.launchUrl(LockScreenTrafficFragment.this.mAppContext, PurchaseUtil.URL_PURCHASE_PACKAGE_LIST, "100010");
            }
        }
    };
    private ImageView mSetttingsButton;

    /* access modifiers changed from: protected */
    public void initView() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            ((TextView) findViewById(R.id.textview_header)).setText(arguments.getString(BUNDLE_KEY_LIST_HEADER));
            TextView textView = (TextView) findViewById(R.id.textview_traffic_purchase);
            int i = 0;
            if (!(this.mAppContext.getResources().getBoolean(R.bool.config_lock_screen_traffic_purchase_enabled) && !SimUserInfo.getInstance(this.mAppContext, Sim.getCurrentActiveSlotNum()).isOversea() && !DeviceUtil.isLargeScaleMode())) {
                i = 8;
            }
            textView.setVisibility(i);
            textView.setOnClickListener(this.mOnClickListener);
            this.mAdapter.setOnItemClickListener(this.mItemClickListener);
            this.mAppMonitorWrapper.registerLisener(this.mAppMonitorListener);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mAppMonitorWrapper = AppMonitorWrapper.getInstance(this.mAppContext);
    }

    /* access modifiers changed from: protected */
    public View onCreateFooterView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return null;
    }

    /* access modifiers changed from: protected */
    public View onCreateHeaderView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.listfragment_universal_header, (ViewGroup) null);
    }

    /* access modifiers changed from: protected */
    public RecyclerView.a onCreateListAdapter() {
        this.mAdapter = new LockScreenAppListAdapter(this.mActivity);
        return this.mAdapter;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        actionBar.setDisplayOptions(16, 16);
        this.mSetttingsButton = new ImageView(this.mActivity);
        this.mSetttingsButton.setBackgroundResource(miui.R.drawable.icon_settings_light);
        this.mSetttingsButton.setId(1);
        this.mSetttingsButton.setOnClickListener(this.mOnClickListener);
        if (!(actionBar instanceof miui.app.ActionBar)) {
            return 0;
        }
        ((miui.app.ActionBar) actionBar).setEndView(this.mSetttingsButton);
        return 0;
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.lock_screen_traffic_warn_title;
    }
}
