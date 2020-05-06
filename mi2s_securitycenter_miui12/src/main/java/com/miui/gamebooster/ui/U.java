package com.miui.gamebooster.ui;

import android.app.ActionBar;
import android.view.View;
import b.b.c.c.b.d;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.view.q;
import com.miui.gamebooster.view.r;
import com.miui.gamebooster.widget.CheckBoxSettingItemView;
import com.miui.securitycenter.R;

public class U extends d implements q, View.OnClickListener, CheckBoxSettingItemView.a {

    /* renamed from: a  reason: collision with root package name */
    private r f5009a;

    /* renamed from: b  reason: collision with root package name */
    private View f5010b;

    /* renamed from: c  reason: collision with root package name */
    private CheckBoxSettingItemView f5011c;

    public void a(r rVar) {
        this.f5009a = rVar;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.f5010b = findViewById(R.id.backBtn);
        View view = this.f5010b;
        if (view != null) {
            view.setOnClickListener(this);
        }
        this.f5011c = (CheckBoxSettingItemView) findViewById(R.id.gwsdSettingItem);
        this.f5011c.setOnCheckedChangeListener(this);
        this.f5011c.a(a.j(false), false, false);
    }

    public void onCheckedChanged(View view, boolean z) {
        if (view == this.f5011c) {
            a.K(z);
        }
    }

    public void onClick(View view) {
        r rVar;
        if (view == this.f5010b && (rVar = this.f5009a) != null) {
            rVar.pop();
        }
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_fragment_gwsd_settings;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }
}
