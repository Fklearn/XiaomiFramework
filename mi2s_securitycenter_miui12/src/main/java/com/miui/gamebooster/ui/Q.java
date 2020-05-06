package com.miui.gamebooster.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.view.View;
import b.b.c.c.b.d;
import com.miui.gamebooster.m.C0379j;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.view.q;
import com.miui.gamebooster.view.r;
import com.miui.gamebooster.widget.CheckBoxSettingItemView;
import com.miui.gamebooster.widget.ValueSettingItemView;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;

public class Q extends d implements View.OnClickListener, q, CheckBoxSettingItemView.a {

    /* renamed from: a  reason: collision with root package name */
    private r f4957a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public CheckBoxSettingItemView f4958b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public ValueSettingItemView f4959c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public ValueSettingItemView f4960d;
    /* access modifiers changed from: private */
    public CheckBoxSettingItemView e;
    private a f;

    private static class a extends AsyncTask<Void, Void, Boolean> {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<Q> f4961a;

        /* renamed from: b  reason: collision with root package name */
        private boolean f4962b = false;

        /* renamed from: c  reason: collision with root package name */
        private boolean f4963c = false;

        /* renamed from: d  reason: collision with root package name */
        private boolean f4964d = false;
        private int e = 0;

        a(Q q) {
            this.f4961a = new WeakReference<>(q);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Boolean doInBackground(Void... voidArr) {
            Activity activity;
            Q q = (Q) this.f4961a.get();
            if (q == null || (activity = q.getActivity()) == null || activity.isFinishing() || activity.isDestroyed()) {
                return false;
            }
            this.f4962b = com.miui.gamebooster.c.a.l(true);
            this.f4963c = com.miui.gamebooster.c.a.d(true);
            this.e = com.miui.gamebooster.c.a.a(0);
            if (C0388t.w()) {
                this.f4964d = com.miui.gamebooster.c.a.j(false);
            }
            if (!C0388t.a(activity) && com.miui.gamebooster.c.a.g(false)) {
                this.e--;
            }
            return true;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Boolean bool) {
            Q q;
            Activity activity;
            super.onPostExecute(bool);
            if (bool.booleanValue() && (q = (Q) this.f4961a.get()) != null && (activity = q.getActivity()) != null && !activity.isFinishing() && !activity.isDestroyed()) {
                q.f4958b.a(this.f4962b, false, false);
                q.e.a(this.f4963c, false, false);
                ValueSettingItemView c2 = q.f4959c;
                Resources resources = activity.getResources();
                int i = this.e;
                c2.setValue(resources.getQuantityString(R.plurals.function_shield_num, i, new Object[]{Integer.valueOf(i)}));
                if (C0388t.w()) {
                    q.f4960d.setValue(activity.getResources().getString(this.f4964d ? R.string.start : R.string.function_close));
                }
            }
        }
    }

    private void e() {
        this.f = new a(this);
        this.f.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public void a(r rVar) {
        this.f4957a = rVar;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.f4958b = (CheckBoxSettingItemView) findViewById(R.id.handsFreeSettingItem);
        this.f4958b.setOnCheckedChangeListener(this);
        this.f4959c = (ValueSettingItemView) findViewById(R.id.experienceSettingItem);
        this.f4959c.setOnClickListener(this);
        this.f4960d = (ValueSettingItemView) findViewById(R.id.gwsdSettingItem);
        this.f4960d.setOnClickListener(this);
        this.e = (CheckBoxSettingItemView) findViewById(R.id.keyboardShieldSettingItem);
        this.e.setOnCheckedChangeListener(this);
        com.miui.gamebooster.c.a.a((Context) this.mActivity);
        if (!C0388t.w()) {
            this.f4960d.setVisibility(8);
        }
    }

    public void onCheckedChanged(View view, boolean z) {
        if (view == this.f4958b) {
            C0379j.a(z, this.mActivity);
        } else if (view == this.e) {
            com.miui.gamebooster.c.a.B(z);
        }
    }

    public void onClick(View view) {
        r rVar;
        r rVar2;
        if (view == this.f4959c && (rVar2 = this.f4957a) != null) {
            rVar2.a(new S());
        } else if (view == this.f4960d && (rVar = this.f4957a) != null) {
            rVar.a(new U());
        }
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_fragment_dnd_settings;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        a aVar = this.f;
        if (aVar != null) {
            aVar.cancel(true);
        }
    }

    public void onResume() {
        super.onResume();
        e();
    }
}
