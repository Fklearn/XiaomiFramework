package com.miui.networkassistant.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import b.b.c.c.a;
import b.b.c.c.b.g;
import com.miui.networkassistant.ui.fragment.BgNetworkAppListFragment;
import com.miui.networkassistant.ui.fragment.MobileFirewallFragment;
import com.miui.networkassistant.ui.fragment.NewInstalledPreSettingFragment;
import com.miui.networkassistant.ui.fragment.WlanFirewallFragment;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.securitycenter.R;
import miui.app.ActionBar;

public class FirewallActivity extends a {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        AnalyticsHelper.trackActiveNetworkAssistant(getApplicationContext());
        onCustomizeActionBar(getActionBar());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.network_firewall_menu, menu);
        return true;
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [android.content.Context, com.miui.networkassistant.ui.activity.FirewallActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCustomizeActionBar(ActionBar actionBar) {
        actionBar.setFragmentViewPagerMode(this, getFragmentManager());
        actionBar.addFragmentTab(MobileFirewallFragment.class.getSimpleName(), actionBar.newTab().setText(R.string.firewall_mobile).setTag(0), MobileFirewallFragment.class, (Bundle) null, false);
        actionBar.addFragmentTab(WlanFirewallFragment.class.getSimpleName(), actionBar.newTab().setText(R.string.firewall_wifi).setTag(1), WlanFirewallFragment.class, (Bundle) null, false);
        actionBar.setDisplayOptions(28, 16);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.miui.networkassistant.ui.activity.FirewallActivity, miui.app.Activity, android.app.Activity] */
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Class cls;
        switch (menuItem.getItemId()) {
            case 16908332:
                finish();
                return true;
            case R.id.menu_bg_network_control /*2131297317*/:
                cls = BgNetworkAppListFragment.class;
                break;
            case R.id.menu_firewall /*2131297318*/:
                cls = NewInstalledPreSettingFragment.class;
                break;
            default:
                return true;
        }
        g.startWithFragment(this, cls);
        return true;
    }
}
