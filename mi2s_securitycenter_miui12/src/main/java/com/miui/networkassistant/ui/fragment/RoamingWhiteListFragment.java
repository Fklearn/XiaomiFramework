package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.g;
import com.miui.luckymoney.config.AppConstants;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.networkassistant.model.WhiteListItem;
import com.miui.networkassistant.service.FirewallService;
import com.miui.networkassistant.service.IFirewallBinder;
import com.miui.networkassistant.traffic.statistic.PreSetGroup;
import com.miui.networkassistant.ui.base.recyclerview.BaseAppWhiteListFragment;
import com.miui.networkassistant.ui.dialog.OptionTipDialog;
import com.miui.networkassistant.utils.LabelLoadHelper;
import com.miui.networkassistant.utils.NotificationUtil;
import com.miui.networkassistant.utils.PackageUtil;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.cloud.Constants;

public class RoamingWhiteListFragment extends BaseAppWhiteListFragment {
    private static final String TAG = "RoamingWhiteListFragment";
    private static final String mMiServicePackage = "com.xiaomi.xmsf";
    private static List<String> mRelatedMIServiceAppsList = new ArrayList();
    private OptionTipDialog mAppDependTipDialog;
    private OptionTipDialog.OptionDialogListener mAppDependTipListener = new OptionTipDialog.OptionDialogListener() {
        public void onOptionUpdated(boolean z) {
            if (z && RoamingWhiteListFragment.this.mMiServiceListItem != null) {
                RoamingWhiteListFragment roamingWhiteListFragment = RoamingWhiteListFragment.this;
                roamingWhiteListFragment.onItemSwitched(roamingWhiteListFragment.mMiServiceListItem, true);
            }
        }
    };
    private ServiceConnection mConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            boolean unused = RoamingWhiteListFragment.this.mServiceConnected = true;
            IFirewallBinder unused2 = RoamingWhiteListFragment.this.mFirewallBinder = IFirewallBinder.Stub.asInterface(iBinder);
            RoamingWhiteListFragment.this.reLoadView();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            IFirewallBinder unused = RoamingWhiteListFragment.this.mFirewallBinder = null;
            boolean unused2 = RoamingWhiteListFragment.this.mServiceConnected = false;
        }
    };
    private boolean mDataInited;
    /* access modifiers changed from: private */
    public IFirewallBinder mFirewallBinder;
    /* access modifiers changed from: private */
    public WhiteListItem mMiServiceListItem;
    /* access modifiers changed from: private */
    public boolean mServiceConnected;

    static {
        mRelatedMIServiceAppsList.add(Constants.CLOUDSERVICE_PACKAGE_NAME);
        mRelatedMIServiceAppsList.add("com.android.mms");
        mRelatedMIServiceAppsList.add(AppConstants.Package.PACKAGE_NAME_MITALK);
    }

    private void addRelatedApp(WhiteListItem whiteListItem) {
        if (mRelatedMIServiceAppsList.contains(whiteListItem.getPkgName()) && !isContainMiService()) {
            buildAppsDependDialog(whiteListItem.getPkgName());
        }
    }

    private void bindFirewallService() {
        Activity activity = this.mActivity;
        if (activity != null && this.mConn != null) {
            g.a((Context) activity, new Intent(activity, FirewallService.class), this.mConn, 1, B.k());
        }
    }

    private void buildAppsDependDialog(String str) {
        String string = this.mAppContext.getString(R.string.add_miservice_dialog_title);
        CharSequence lableByPackageName = PackageUtil.getLableByPackageName(this.mAppContext, str);
        this.mAppDependTipDialog.buildShowDialog(string, String.format(this.mAppContext.getString(R.string.add_miservice_dialog_content), new Object[]{lableByPackageName.toString()}));
    }

    private boolean isContainMiService() {
        WhiteListItem whiteListItem = this.mMiServiceListItem;
        return whiteListItem != null && whiteListItem.isEnabled();
    }

    private void unBindFirewallService() {
        ServiceConnection serviceConnection;
        if (this.mServiceConnected && (serviceConnection = this.mConn) != null) {
            this.mActivity.unbindService(serviceConnection);
            this.mConn = null;
        }
    }

    /* access modifiers changed from: protected */
    public ArrayList<WhiteListItem> onAppInfoListChange(ArrayList<AppInfo> arrayList) {
        if (this.mDataInited || arrayList == null || this.mFirewallBinder == null) {
            return null;
        }
        this.mDataInited = true;
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        Iterator<AppInfo> it = arrayList.iterator();
        while (it.hasNext()) {
            AppInfo next = it.next();
            if (!PreSetGroup.isPreFirewallWhiteListPackage(next.packageName.toString())) {
                WhiteListItem whiteListItem = new WhiteListItem();
                whiteListItem.setAppLabel(LabelLoadHelper.loadLabel(this.mAppContext, next.packageName).toString());
                whiteListItem.setPkgName(next.packageName.toString());
                if (next.packageName.toString().equals("com.xiaomi.xmsf")) {
                    this.mMiServiceListItem = whiteListItem;
                }
                try {
                    if (this.mFirewallBinder.getRoamingRule(next.packageName.toString()) == FirewallRule.Restrict) {
                        whiteListItem.setEnabled(false);
                        whiteListItem.setGroup(Integer.valueOf(R.plurals.hints_roaming_disable_title));
                        arrayList2.add(whiteListItem);
                    } else {
                        whiteListItem.setGroup(Integer.valueOf(R.plurals.hints_roaming_enable_title));
                        whiteListItem.setEnabled(true);
                        arrayList3.add(whiteListItem);
                    }
                } catch (RemoteException e) {
                    Log.i(TAG, "firewall get roaming rule", e);
                }
            }
        }
        ArrayList<WhiteListItem> arrayList4 = new ArrayList<>();
        arrayList4.addAll(arrayList3);
        arrayList4.addAll(arrayList2);
        return arrayList4;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        unBindFirewallService();
    }

    /* access modifiers changed from: protected */
    public int onEnableGroupRes() {
        return R.plurals.hints_roaming_enable_title;
    }

    /* access modifiers changed from: protected */
    public void onInit() {
        NotificationUtil.cancelOpenRoamingWhiteListNotify(this.mAppContext);
        this.mAppDependTipDialog = new OptionTipDialog(this.mActivity, this.mAppDependTipListener);
        bindFirewallService();
    }

    /* access modifiers changed from: protected */
    public void onItemSwitched(WhiteListItem whiteListItem, boolean z) {
        try {
            this.mFirewallBinder.setRoamingRule(whiteListItem.getPkgName(), z ? FirewallRule.Allow : FirewallRule.Restrict);
        } catch (RemoteException e) {
            Log.i(TAG, "RemoteExceptions setRoamingRule", e);
        }
        if (z) {
            addRelatedApp(whiteListItem);
        }
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.pref_whitelist_setting;
    }
}
