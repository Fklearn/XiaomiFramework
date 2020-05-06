package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import b.b.c.c.a.b;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.model.WhiteListItem;
import com.miui.networkassistant.service.ITrafficManageBinder;
import com.miui.networkassistant.service.wrapper.TmBinderCacher;
import com.miui.networkassistant.ui.base.recyclerview.BaseAppWhiteListFragment;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.LabelLoadHelper;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;

public class DataUsageIgnoreAppListFragment extends BaseAppWhiteListFragment {
    private static final String TAG = "DataUsageIgnoreAppListFragment";
    private ServiceConnection mConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            DataUsageIgnoreAppListFragment dataUsageIgnoreAppListFragment = DataUsageIgnoreAppListFragment.this;
            dataUsageIgnoreAppListFragment.mServiceConnected = true;
            dataUsageIgnoreAppListFragment.mTrafficManageBinder = ITrafficManageBinder.Stub.asInterface(iBinder);
            DataUsageIgnoreAppListFragment dataUsageIgnoreAppListFragment2 = DataUsageIgnoreAppListFragment.this;
            dataUsageIgnoreAppListFragment2.mSimUserInfos[0] = SimUserInfo.getInstance(dataUsageIgnoreAppListFragment2.mAppContext, 0);
            if (DeviceUtil.IS_DUAL_CARD) {
                DataUsageIgnoreAppListFragment dataUsageIgnoreAppListFragment3 = DataUsageIgnoreAppListFragment.this;
                dataUsageIgnoreAppListFragment3.mSimUserInfos[1] = SimUserInfo.getInstance(dataUsageIgnoreAppListFragment3.mAppContext, 1);
                DataUsageIgnoreAppListFragment.this.mSlotNum = Sim.getCurrentOptSlotNum();
                DataUsageIgnoreAppListFragment.this.resetTitle();
            }
            DataUsageIgnoreAppListFragment dataUsageIgnoreAppListFragment4 = DataUsageIgnoreAppListFragment.this;
            dataUsageIgnoreAppListFragment4.postOnUiThread(new b(dataUsageIgnoreAppListFragment4) {
                public void runOnUiThread() {
                    DataUsageIgnoreAppListFragment.this.reLoadView();
                }
            });
        }

        public void onServiceDisconnected(ComponentName componentName) {
            DataUsageIgnoreAppListFragment dataUsageIgnoreAppListFragment = DataUsageIgnoreAppListFragment.this;
            dataUsageIgnoreAppListFragment.mTrafficManageBinder = null;
            dataUsageIgnoreAppListFragment.mServiceConnected = false;
        }
    };
    private boolean mDataInited;
    protected boolean mServiceConnected;
    protected SimUserInfo[] mSimUserInfos = new SimUserInfo[2];
    protected int mSlotNum = 0;
    protected ITrafficManageBinder mTrafficManageBinder;

    private void bindTrafficManageService() {
        TmBinderCacher.getInstance().bindTmService(this.mConn);
    }

    private void unbindTrafficManageService() {
        TmBinderCacher.getInstance().unbindTmService(this.mConn);
    }

    /* access modifiers changed from: protected */
    public ArrayList<WhiteListItem> onAppInfoListChange(ArrayList<AppInfo> arrayList) {
        if (this.mDataInited || !this.mServiceConnected) {
            return null;
        }
        this.mDataInited = true;
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        Iterator<AppInfo> it = arrayList.iterator();
        while (it.hasNext()) {
            AppInfo next = it.next();
            WhiteListItem whiteListItem = new WhiteListItem();
            whiteListItem.setAppLabel(LabelLoadHelper.loadLabel(this.mAppContext, next.packageName).toString());
            whiteListItem.setPkgName(next.packageName.toString());
            try {
                if (this.mTrafficManageBinder.isDataUsageIgnore(next.packageName.toString(), this.mSlotNum)) {
                    whiteListItem.setEnabled(true);
                    whiteListItem.setGroup(Integer.valueOf(R.plurals.hints_traffic_enable_title));
                    arrayList2.add(whiteListItem);
                } else {
                    whiteListItem.setEnabled(false);
                    whiteListItem.setGroup(Integer.valueOf(R.plurals.hints_traffic_disable_title));
                    arrayList3.add(whiteListItem);
                }
            } catch (RemoteException e) {
                Log.w(TAG, "RemoteException isDataUsageIgnore:", e);
            }
        }
        ArrayList<WhiteListItem> arrayList4 = new ArrayList<>();
        arrayList4.addAll(arrayList2);
        arrayList4.addAll(arrayList3);
        return arrayList4;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mServiceConnected) {
            try {
                this.mTrafficManageBinder.reloadIgnoreAppList(this.mSlotNum);
            } catch (RemoteException e) {
                Log.i(TAG, "reloadIgnoreAppList", e);
            }
        }
        unbindTrafficManageService();
    }

    /* access modifiers changed from: protected */
    public int onEnableGroupRes() {
        return R.plurals.hints_traffic_enable_title;
    }

    /* access modifiers changed from: protected */
    public void onInit() {
        bindTrafficManageService();
    }

    /* access modifiers changed from: protected */
    public void onItemSwitched(WhiteListItem whiteListItem, boolean z) {
        if (this.mServiceConnected) {
            try {
                this.mTrafficManageBinder.setDataUsageIgnore(whiteListItem.getPkgName(), z, this.mSlotNum);
            } catch (RemoteException e) {
                Log.w(TAG, "RemoteException setDataUsageIgnore:", e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.pref_data_usage_ignore_settings;
    }

    /* access modifiers changed from: protected */
    public void resetTitle() {
        postOnUiThread(new b(this) {
            public void runOnUiThread() {
                Object[] objArr = new Object[2];
                objArr[0] = DataUsageIgnoreAppListFragment.this.getTitle();
                objArr[1] = getString(DataUsageIgnoreAppListFragment.this.mSlotNum == 0 ? R.string.dual_setting_simcard1 : R.string.dual_setting_simcard2);
                DataUsageIgnoreAppListFragment.this.setTitle(String.format("%s-%s", objArr));
            }
        });
    }
}
