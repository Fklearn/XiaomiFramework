package com.miui.gamebooster.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.View;
import b.b.c.c.b.d;
import com.miui.gamebooster.ui.C0453ua;
import com.miui.gamebooster.ui.SettingsTabFragment;
import com.miui.gamebooster.view.s;
import com.miui.gamebooster.view.v;
import com.miui.securitycenter.R;

public class SettingsLandFragment extends d implements SettingsTabFragment.a, C0453ua.b, View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private View f5002a;

    public void b(int i) {
        v vVar;
        Fragment fragment;
        if (i != 0) {
            if (i == 1) {
                fragment = new Ba();
            } else if (i == 2) {
                fragment = new Q();
            } else if (i != 3) {
                vVar = null;
            } else {
                fragment = new AdvancedSettingsFragment();
            }
            vVar = v.b(fragment);
        } else {
            C0453ua uaVar = new C0453ua();
            uaVar.a((C0453ua.b) this);
            vVar = uaVar;
        }
        if (vVar != null) {
            FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
            beginTransaction.replace(R.id.container, vVar);
            beginTransaction.commit();
        }
    }

    public void b(boolean z) {
        Fragment findFragmentById = getChildFragmentManager().findFragmentById(R.id.tabFragment);
        if (findFragmentById instanceof s) {
            ((s) findFragmentById).a(z);
        }
    }

    /* access modifiers changed from: protected */
    public void initView() {
        SettingsTabFragment settingsTabFragment = (SettingsTabFragment) getChildFragmentManager().findFragmentById(R.id.tabFragment);
        if (settingsTabFragment != null) {
            settingsTabFragment.a((Object) this);
        }
        this.f5002a = findViewById(R.id.backBtn);
        this.f5002a.setOnClickListener(this);
    }

    public void onClick(View view) {
        Activity activity;
        if (view == this.f5002a && (activity = getActivity()) != null) {
            activity.onBackPressed();
        }
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_fragment_settings_land;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }
}
