package com.miui.antispam.ui.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import b.b.a.d.b.s;
import b.b.a.d.b.v;
import b.b.a.e.n;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import java.util.List;
import miui.app.ActionBar;
import miui.app.Activity;
import miui.telephony.SubscriptionInfo;

public class AntiSpamNewSettingsActivity extends r {

    /* renamed from: d  reason: collision with root package name */
    public final String f2512d = "sim_1";
    public final String e = "sim_2";
    private ActionBar f;
    private ActionBar.Tab g;
    private ActionBar.Tab h;

    /* JADX WARNING: type inference failed for: r15v0, types: [android.content.Context, com.miui.antispam.ui.activity.AntiSpamNewSettingsActivity, miui.app.Activity] */
    private void c() {
        List<SubscriptionInfo> b2 = n.b();
        if (b2 == null || b2.size() != 2) {
            Intent intent = getIntent();
            if (intent == null || !intent.hasExtra("extra_settings_title_res")) {
                this.f.setTitle(R.string.Settings_title_anti_spam);
            } else {
                this.f.setTitle(intent.getIntExtra("extra_settings_title_res", R.string.Settings_title_anti_spam));
            }
            getFragmentManager().beginTransaction().replace(16908290, new s(), (String) null).commit();
            return;
        }
        String b3 = n.d((Context) this, b2.get(0).getSlotId()) ? n.b((Context) this) : b2.get(0).getDisplayName().toString();
        String b4 = n.d((Context) this, b2.get(1).getSlotId()) ? n.b((Context) this) : b2.get(1).getDisplayName().toString();
        this.g = this.f.newTab().setText(getString(R.string.st_tab_title, new Object[]{b3, 1}));
        this.h = this.f.newTab().setText(getString(R.string.st_tab_title, new Object[]{b4, 2}));
        this.f.setFragmentViewPagerMode(this, getFragmentManager());
        this.f.addFragmentTab("sim_1", this.g, s.class, (Bundle) null, true);
        this.f.addFragmentTab("sim_2", this.h, v.class, (Bundle) null, true);
        this.f.setTitle(R.string.Settings_title_anti_spam);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.f = getActionBar();
        this.f.setDisplayOptions(28);
        c();
        k.a((Activity) this);
    }
}
