package com.miui.gamebooster.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import b.b.c.c.b.d;
import b.c.a.b.f.c;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.ui.ManualRecordSettingsTabFragment;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class ManualRecordSettingsLandFragment extends d implements ManualRecordSettingsTabFragment.a, c, View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private static final String f4931a = "tag_kpl_fragment";

    /* renamed from: b  reason: collision with root package name */
    private static final String f4932b = "tag_pubg_fragment";

    /* renamed from: c  reason: collision with root package name */
    private List<Fragment> f4933c = new ArrayList(2);

    /* renamed from: d  reason: collision with root package name */
    private View f4934d;
    private String e;
    private ManualRecordSettingsTabFragment f;

    private Fragment a(String str) {
        return getChildFragmentManager().findFragmentByTag(str);
    }

    private void a(Fragment fragment, String str) {
        FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.container, fragment, str);
        beginTransaction.commit();
    }

    public void a() {
        int e2 = this.f.e();
        a(this.f4933c.get(e2), e2 == 0 ? f4931a : f4932b);
    }

    public void b() {
        Fragment a2 = a("tag_set_fragment");
        Fa fa = a2;
        if (a2 == null) {
            Fa fa2 = new Fa();
            fa2.a(this);
            fa = fa2;
        }
        a(fa, "tag_set_fragment");
    }

    public void b(int i) {
        ((ib) this.f4933c.get(i)).e(i);
        a(this.f4933c.get(i), i == 0 ? f4931a : f4932b);
    }

    /* access modifiers changed from: protected */
    public void initView() {
        if (getActivity() instanceof WonderfulMomentActivity) {
            this.e = getActivity().getIntent().getStringExtra("gamePkg");
        }
        this.f = (ManualRecordSettingsTabFragment) getChildFragmentManager().findFragmentById(R.id.tabFragment);
        Bundle bundle = new Bundle();
        bundle.putString("gamePkg", this.e);
        this.f.setArguments(bundle);
        this.f.a((Object) this);
        this.f4934d = findViewById(R.id.backBtn);
        if (na.c()) {
            this.f4934d.setRotation(180.0f);
        }
        this.f4934d.setOnClickListener(this);
        ib ibVar = new ib();
        ibVar.a((Object) this);
        this.f4933c.add(ibVar);
        ib ibVar2 = new ib();
        ibVar2.a((Object) this);
        this.f4933c.add(ibVar2);
    }

    public void onClick(View view) {
        Activity activity;
        if (view == this.f4934d && (activity = getActivity()) != null) {
            activity.onBackPressed();
        }
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_fragment_manual_record_settings_land;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }
}
