package com.miui.gamebooster.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import b.b.c.c.b.d;
import com.miui.gamebooster.m.C0371b;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.C0389u;
import com.miui.gamebooster.m.G;
import com.miui.gamebooster.m.Z;
import com.miui.gamebooster.service.IGameBooster;
import com.miui.gamebooster.widget.CheckBoxSettingItemView;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;

/* renamed from: com.miui.gamebooster.ui.ua  reason: case insensitive filesystem */
public class C0453ua extends d implements CheckBoxSettingItemView.a, View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private CheckBoxSettingItemView f5112a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public CheckBoxSettingItemView f5113b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public CheckBoxSettingItemView f5114c;

    /* renamed from: d  reason: collision with root package name */
    private CheckBoxSettingItemView f5115d;
    private View e;
    /* access modifiers changed from: private */
    public View f;
    /* access modifiers changed from: private */
    public View g;
    private b h;
    private a i;

    /* renamed from: com.miui.gamebooster.ui.ua$a */
    private static class a extends AsyncTask<Void, Void, Boolean> {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<C0453ua> f5116a;

        /* renamed from: b  reason: collision with root package name */
        private int f5117b = 0;

        /* renamed from: c  reason: collision with root package name */
        private boolean f5118c;

        /* renamed from: d  reason: collision with root package name */
        private boolean f5119d;

        a(C0453ua uaVar) {
            this.f5116a = new WeakReference<>(uaVar);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Boolean doInBackground(Void... voidArr) {
            if (((C0453ua) this.f5116a.get()) == null || isCancelled()) {
                return null;
            }
            this.f5117b = com.miui.gamebooster.c.a.b();
            this.f5118c = com.miui.gamebooster.c.a.a(true);
            this.f5119d = com.miui.gamebooster.c.a.w(true);
            return true;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            C0453ua uaVar = (C0453ua) this.f5116a.get();
            if (uaVar != null) {
                if (C0388t.s()) {
                    if (this.f5117b == 0) {
                        uaVar.f.setSelected(true);
                        uaVar.g.setSelected(false);
                    } else {
                        uaVar.f.setSelected(false);
                        uaVar.g.setSelected(true);
                    }
                }
                uaVar.f5113b.a(this.f5118c, false, false);
                uaVar.f5114c.a(this.f5119d, false, false);
            }
        }
    }

    /* renamed from: com.miui.gamebooster.ui.ua$b */
    interface b {
        void b(boolean z);
    }

    private void c(boolean z) {
        b bVar = this.h;
        if (bVar != null) {
            bVar.b(z);
        }
        for (View view : new View[]{this.f5113b, this.f5114c, this.e, this.f, this.g}) {
            if (view != null) {
                view.setEnabled(z);
            }
        }
        if (z) {
            this.e.setAlpha(1.0f);
            return;
        }
        this.e.setAlpha(0.2f);
        Activity activity = this.mActivity;
        if (activity != null) {
            C0371b.a(activity);
        }
        com.miui.common.persistence.b.b("game_IsAntiMsg", false);
    }

    private void e() {
        this.i = new a(this);
        this.i.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public void a(b bVar) {
        this.h = bVar;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.f5112a = (CheckBoxSettingItemView) findViewById(R.id.globalSwitchSettingItem);
        this.f5112a.setOnCheckedChangeListener(this);
        this.f5113b = (CheckBoxSettingItemView) findViewById(R.id.gameboxSettingItem);
        this.f5113b.setOnCheckedChangeListener(this);
        this.f5114c = (CheckBoxSettingItemView) findViewById(R.id.slipSettingItem);
        this.f5114c.setOnCheckedChangeListener(this);
        this.f5115d = (CheckBoxSettingItemView) findViewById(R.id.shortcutSettingItem);
        this.f5115d.setOnCheckedChangeListener(this);
        if (C0389u.a()) {
            this.f5115d.setVisibility(8);
        }
        this.e = findViewById(R.id.showWaySettingItem);
        this.f = findViewById(R.id.showWayLandBtn);
        this.f.setOnClickListener(this);
        this.g = findViewById(R.id.showWayPortraitBtn);
        this.g.setOnClickListener(this);
        com.miui.gamebooster.c.a.a((Context) this.mActivity);
        (C0388t.o() ? this.f5113b : this.f5114c).setVisibility(8);
        if (!C0388t.s()) {
            this.e.setVisibility(8);
        }
        boolean k = com.miui.gamebooster.c.a.a(this.mAppContext).k(true);
        this.f5112a.a(k, false, false);
        c(k);
        this.f5115d.a(Z.b(this.mActivity, (String) null), false, false);
    }

    public void onCheckedChanged(View view, boolean z) {
        if (view == this.f5112a) {
            c(z);
            IGameBooster iGameBooster = null;
            Activity activity = this.mActivity;
            if (activity instanceof SettingsActivity) {
                iGameBooster = ((SettingsActivity) activity).m();
            }
            G.a(z, this.mActivity, iGameBooster);
        } else if (view == this.f5113b) {
            G.a(z);
        } else if (view == this.f5115d) {
            G.a(z, this.mActivity);
        } else if (view == this.f5114c) {
            com.miui.gamebooster.c.a.ca(z);
        }
    }

    public void onClick(View view) {
        if (view == this.f || view == this.g) {
            View view2 = this.f;
            view2.setSelected(!view2.isSelected());
            View view3 = this.g;
            view3.setSelected(!view3.isSelected());
            com.miui.gamebooster.c.a.c(this.f.isSelected() ^ true ? 1 : 0);
        }
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_fragment_global_settings;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        a aVar = this.i;
        if (aVar != null) {
            aVar.cancel(true);
        }
    }

    public void onResume() {
        super.onResume();
        e();
    }
}
