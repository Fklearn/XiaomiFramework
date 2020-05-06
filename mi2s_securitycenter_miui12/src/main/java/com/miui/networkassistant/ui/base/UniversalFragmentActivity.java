package com.miui.networkassistant.ui.base;

import android.os.Bundle;
import android.os.MessageQueue;
import android.text.TextUtils;
import b.b.c.c.b.g;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.ui.fragment.BgNetworkAppListFragment;
import com.miui.networkassistant.ui.fragment.OperatorSettingFragment;
import com.miui.networkassistant.ui.fragment.PackageSettingFragment;
import com.miui.networkassistant.ui.fragment.SettingFragment;
import com.miui.networkassistant.ui.fragment.ShowAppDetailFragment;
import com.miui.networkassistant.ui.fragment.TcSmsReportFragment;
import com.miui.networkassistant.ui.fragment.TrafficLimitSettingFragment;
import com.miui.networkassistant.utils.AnalyticsHelper;
import java.util.HashMap;

public class UniversalFragmentActivity extends g {
    private static HashMap<String, String> sActionMap = new HashMap<>();

    static {
        sActionMap.put(Constants.App.ACTION_NETWORK_ASSISTANT_MONTH_PACKAGE_SETTING, PackageSettingFragment.class.getName());
        sActionMap.put(Constants.App.ACTION_NETWORK_ASSISTANT_AUTO_TRAFFIC_CORRECTION_SETTING, OperatorSettingFragment.class.getName());
        sActionMap.put(Constants.App.ACTION_NETWORK_ASSISTANT_STATUS_BAR_SETTING, SettingFragment.class.getName());
        sActionMap.put(Constants.App.ACTION_NETWORK_ASSISTANT_SETTING, SettingFragment.class.getName());
        sActionMap.put(Constants.App.ACTION_NETWORK_ASSISTANT_OPERATOR_SETTING, OperatorSettingFragment.class.getName());
        sActionMap.put(Constants.App.ACTION_NETWORK_ASSISTANT_SMS_REPORT, TcSmsReportFragment.class.getName());
        sActionMap.put(Constants.App.ACTION_NETWORK_ASSISTANT_APP_DETAIL, ShowAppDetailFragment.class.getName());
        sActionMap.put(Constants.App.ACTION_NETWORK_ASSISTANT_BG_NETWORK, BgNetworkAppListFragment.class.getName());
        sActionMap.put(Constants.App.ACTION_NETWORK_ASSISTANT_LIMIT_SETTING, TrafficLimitSettingFragment.class.getName());
    }

    /* access modifiers changed from: protected */
    public boolean checkAction(String str) {
        return sActionMap.containsKey(str) || TextUtils.equals(str, "miui.intent.action.NETWORKASSISTANT_UNIVERSAL_FRAGMENT_ACTIVITY");
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        postOnIdleUiThread(new MessageQueue.IdleHandler() {
            public boolean queueIdle() {
                AnalyticsHelper.trackActiveNetworkAssistant(UniversalFragmentActivity.this.getApplicationContext());
                return false;
            }
        });
    }

    /* access modifiers changed from: protected */
    public boolean resolveAction(String str, Bundle bundle) {
        String str2 = sActionMap.get(str);
        if (bundle != null) {
            Bundle bundle2 = bundle.getBundle(g.FRAGMENT_ARGS);
            if (bundle2 != null) {
                bundle = bundle2;
            }
        } else {
            bundle = null;
        }
        if (str2 == null) {
            return false;
        }
        launchFragment(str2, bundle);
        return true;
    }
}
