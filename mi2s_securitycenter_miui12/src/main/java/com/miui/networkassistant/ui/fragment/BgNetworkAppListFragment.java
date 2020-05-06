package com.miui.networkassistant.ui.fragment;

import android.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import b.b.c.j.B;
import com.miui.networkassistant.firewall.BackgroundPolicyService;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.model.WhiteListItem;
import com.miui.networkassistant.traffic.statistic.PreSetGroup;
import com.miui.networkassistant.ui.base.recyclerview.BaseAppWhiteListFragment;
import com.miui.networkassistant.ui.dialog.MessageDialog;
import com.miui.networkassistant.utils.LabelLoadHelper;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;

public class BgNetworkAppListFragment extends BaseAppWhiteListFragment {
    private BackgroundPolicyService mBgPolicyService;

    /* access modifiers changed from: protected */
    public ArrayList<WhiteListItem> onAppInfoListChange(ArrayList<AppInfo> arrayList) {
        if (arrayList == null) {
            return null;
        }
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        Iterator<AppInfo> it = arrayList.iterator();
        while (it.hasNext()) {
            AppInfo next = it.next();
            String charSequence = next.packageName.toString();
            if (B.a(next.uid) >= 10000 && !PreSetGroup.isPrePolicyPackage(charSequence)) {
                WhiteListItem whiteListItem = new WhiteListItem();
                whiteListItem.setAppLabel(LabelLoadHelper.loadLabel(this.mAppContext, next.packageName).toString());
                whiteListItem.setPkgName(charSequence);
                whiteListItem.setUid(next.uid);
                if (this.mBgPolicyService.isAppRestrictBackground(charSequence, next.uid)) {
                    whiteListItem.setEnabled(false);
                    whiteListItem.setGroup(Integer.valueOf(R.plurals.bg_network_restrict_count));
                    arrayList3.add(whiteListItem);
                } else {
                    whiteListItem.setEnabled(true);
                    whiteListItem.setGroup(Integer.valueOf(R.plurals.bg_network_allow_count));
                    arrayList2.add(whiteListItem);
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
        actionBar.setDisplayOptions(16, 16);
        ImageView imageView = new ImageView(this.mActivity);
        imageView.setContentDescription(this.mAppContext.getString(R.string.firewall_restrict_android_dialog_title));
        imageView.setBackgroundResource(R.drawable.app_manager_info_icon);
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new MessageDialog(BgNetworkAppListFragment.this.mActivity).buildShowDialog(BgNetworkAppListFragment.this.mActivity.getString(R.string.firewall_restrict_android_dialog_title), BgNetworkAppListFragment.this.mActivity.getString(R.string.app_bg_restrict_dialog_message));
            }
        });
        if (!(actionBar instanceof miui.app.ActionBar)) {
            return 0;
        }
        ((miui.app.ActionBar) actionBar).setEndView(imageView);
        return 0;
    }

    /* access modifiers changed from: protected */
    public int onEnableGroupRes() {
        return R.plurals.bg_network_allow_count;
    }

    /* access modifiers changed from: protected */
    public void onInit() {
        this.mBgPolicyService = BackgroundPolicyService.getInstance(this.mAppContext);
    }

    /* access modifiers changed from: protected */
    public void onItemSwitched(WhiteListItem whiteListItem, boolean z) {
        this.mBgPolicyService.setAppRestrictBackground(whiteListItem.getUid(), !z);
    }

    /* access modifiers changed from: protected */
    public int onSetTitle() {
        return R.string.pref_bg_network_title;
    }
}
