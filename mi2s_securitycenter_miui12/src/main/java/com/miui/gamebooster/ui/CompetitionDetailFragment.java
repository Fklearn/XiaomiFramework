package com.miui.gamebooster.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import b.b.c.c.b.d;
import b.b.c.j.i;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.view.q;
import com.miui.gamebooster.view.r;
import com.miui.gamebooster.widget.CheckBoxSettingItemView;
import com.miui.securitycenter.R;
import com.miui.securitycenter.p;

public class CompetitionDetailFragment extends d implements q, View.OnClickListener, CheckBoxSettingItemView.a {

    /* renamed from: a  reason: collision with root package name */
    private CompoundButton f4865a;

    /* renamed from: b  reason: collision with root package name */
    private LinearLayout f4866b;

    /* renamed from: c  reason: collision with root package name */
    private LinearLayout f4867c;

    /* renamed from: d  reason: collision with root package name */
    private LinearLayout f4868d;
    private TextView e;
    private TextView f;
    private CheckBoxSettingItemView g;
    private r h;
    private CompoundButton.OnCheckedChangeListener i = new P(this);

    /* access modifiers changed from: private */
    public void c(boolean z) {
        a.D(z);
    }

    public void a(r rVar) {
        this.h = rVar;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.f4865a = findViewById(R.id.sliding_button);
        boolean e2 = a.e(true);
        CompoundButton compoundButton = this.f4865a;
        if (compoundButton != null) {
            compoundButton.setChecked(e2);
            this.f4865a.setOnCheckedChangeListener(this.i);
        }
        this.g = (CheckBoxSettingItemView) findViewById(R.id.competitionSettingItem);
        CheckBoxSettingItemView checkBoxSettingItemView = this.g;
        if (checkBoxSettingItemView != null) {
            checkBoxSettingItemView.a(e2, false, false);
            this.g.setOnCheckedChangeListener(this);
            TextView textView = (TextView) findViewById(R.id.titleTv);
            if (textView != null) {
                textView.setText(R.string.gs_performance_mode);
            }
        }
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_competition);
        Activity activity = getActivity();
        if (!(linearLayout == null || !i.e() || activity == null)) {
            if (na.c()) {
                linearLayout.setPadding(i.f(activity), 0, 0, activity.getResources().getDimensionPixelOffset(R.dimen.view_dimen_100));
            } else {
                linearLayout.setPadding(0, 0, i.f(activity), activity.getResources().getDimensionPixelOffset(R.dimen.view_dimen_100));
            }
        }
        this.f4866b = (LinearLayout) findViewById(R.id.doze_optimization);
        this.f4867c = (LinearLayout) findViewById(R.id.audio_optimization);
        this.f4868d = (LinearLayout) findViewById(R.id.perfermance_optimization);
        this.e = (TextView) findViewById(R.id.wlan_optimization_title);
        this.f = (TextView) findViewById(R.id.wlan_optimization_summary);
        if (!C0388t.j()) {
            this.e.setText(R.string.forground_network_optimization_title);
            this.f.setText(R.string.forground_network_optimization_summary);
        }
        if (!C0388t.r()) {
            this.f4866b.setVisibility(8);
        }
        if (!C0388t.c()) {
            this.f4867c.setVisibility(8);
        }
        if (C0388t.b() && p.a() < 12) {
            this.f4868d.setVisibility(0);
        }
        View findViewById = findViewById(R.id.backBtn);
        if (findViewById != null) {
            findViewById.setOnClickListener(this);
        }
    }

    public void onCheckedChanged(View view, boolean z) {
        if (view == this.g) {
            c(z);
        }
    }

    public void onClick(View view) {
        r rVar;
        if (view.getId() == R.id.backBtn && (rVar = this.h) != null) {
            rVar.pop();
        }
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_fragment_competition_detail;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }
}
