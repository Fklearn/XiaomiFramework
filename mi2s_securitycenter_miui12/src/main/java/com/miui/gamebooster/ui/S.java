package com.miui.gamebooster.ui;

import android.app.ActionBar;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import b.b.c.c.b.d;
import com.miui.gamebooster.m.C0384o;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.view.q;
import com.miui.gamebooster.view.r;
import com.miui.gamebooster.widget.CheckBoxSettingItemView;
import com.miui.securitycenter.R;
import com.miui.securitycenter.p;
import java.lang.ref.WeakReference;

public class S extends d implements q, View.OnClickListener, CheckBoxSettingItemView.a {

    /* renamed from: a  reason: collision with root package name */
    private r f4974a;

    /* renamed from: b  reason: collision with root package name */
    private View f4975b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public CheckBoxSettingItemView f4976c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public CheckBoxSettingItemView f4977d;
    /* access modifiers changed from: private */
    public CheckBoxSettingItemView e;
    /* access modifiers changed from: private */
    public CheckBoxSettingItemView f;
    /* access modifiers changed from: private */
    public CheckBoxSettingItemView g;
    /* access modifiers changed from: private */
    public int h = 0;
    private a i;

    private static class a extends AsyncTask<Void, Void, Integer> {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<S> f4978a;

        /* renamed from: b  reason: collision with root package name */
        private boolean f4979b;

        /* renamed from: c  reason: collision with root package name */
        private boolean f4980c;

        /* renamed from: d  reason: collision with root package name */
        private boolean f4981d;
        private boolean e;
        private boolean f;

        a(S s) {
            this.f4978a = new WeakReference<>(s);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Integer doInBackground(Void... voidArr) {
            if (((S) this.f4978a.get()) == null || isCancelled()) {
                return null;
            }
            int i = 0;
            this.f4979b = com.miui.gamebooster.c.a.r(false);
            this.f4980c = com.miui.gamebooster.c.a.s(false);
            this.f4981d = com.miui.gamebooster.c.a.u(false);
            this.e = com.miui.gamebooster.c.a.t(false);
            this.f = com.miui.gamebooster.c.a.g(false);
            if (this.f4979b) {
                i = 1;
            }
            if (this.f4980c) {
                i++;
            }
            if (this.f4981d) {
                i++;
            }
            if (this.e) {
                i++;
            }
            if (this.f) {
                i++;
            }
            return Integer.valueOf(i);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Integer num) {
            super.onPostExecute(num);
            S s = (S) this.f4978a.get();
            if (s != null && num != null) {
                int unused = s.h = num.intValue();
                s.f4976c.a(this.f4979b, false, false);
                s.f4977d.a(this.f4980c, false, false);
                s.e.a(this.f4981d, false, false);
                s.f.a(this.e, false, false);
                s.g.a(this.f, false, false);
            }
        }
    }

    private void e() {
        this.i = new a(this);
        this.i.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public void a(r rVar) {
        this.f4974a = rVar;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        ((TextView) findViewById(R.id.titleTv)).setText(getString(R.string.function_shield_title));
        this.f4975b = findViewById(R.id.backBtn);
        View view = this.f4975b;
        if (view != null) {
            view.setOnClickListener(this);
        }
        this.f4976c = (CheckBoxSettingItemView) findViewById(R.id.autoBrightnessSettingItem);
        this.f4976c.setOnCheckedChangeListener(this);
        this.f4977d = (CheckBoxSettingItemView) findViewById(R.id.readModeSettingItem);
        this.f4977d.setOnCheckedChangeListener(this);
        this.e = (CheckBoxSettingItemView) findViewById(R.id.screenshotSettingItem);
        this.e.setOnCheckedChangeListener(this);
        this.f = (CheckBoxSettingItemView) findViewById(R.id.notificationBarSettingItem);
        this.f.setOnCheckedChangeListener(this);
        this.g = (CheckBoxSettingItemView) findViewById(R.id.voiceTriggerSettingItem);
        this.g.setOnCheckedChangeListener(this);
        if (!C0388t.e()) {
            this.f4977d.setVisibility(8);
            if (p.a() < 12) {
                this.e.setVisibility(8);
            }
        }
        if (!C0388t.a(this.mAppContext)) {
            this.g.setVisibility(8);
        }
        com.miui.gamebooster.c.a.a((Context) this.mActivity);
    }

    public void onCheckedChanged(View view, boolean z) {
        if (z) {
            this.h++;
        } else {
            this.h--;
            if (this.h < 0) {
                this.h = 0;
            }
        }
        com.miui.gamebooster.c.a.b(this.h);
        if (view == this.f4976c) {
            com.miui.gamebooster.c.a.X(z);
        } else if (view == this.f4977d) {
            com.miui.gamebooster.c.a.Y(z);
            if (!z && this.mActivity != null) {
                Settings.System.putInt(this.mActivity.getContentResolver(), (String) C0384o.b("android.provider.MiuiSettings$ScreenEffect", "GAME_MODE"), 0);
            }
        } else if (view == this.e) {
            com.miui.gamebooster.c.a.aa(z);
        } else if (view == this.f) {
            com.miui.gamebooster.c.a.Z(z);
        } else if (view == this.g) {
            com.miui.gamebooster.c.a.F(z);
        }
    }

    public void onClick(View view) {
        r rVar;
        if (view == this.f4975b && (rVar = this.f4974a) != null) {
            rVar.pop();
        }
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_fragment_experience_settings;
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
