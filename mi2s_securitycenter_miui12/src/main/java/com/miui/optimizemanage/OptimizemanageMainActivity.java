package com.miui.optimizemanage;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.miui.common.customview.ActionBarContainer;
import com.miui.luckymoney.config.Constants;
import com.miui.optimizemanage.c.c;
import com.miui.optimizemanage.c.f;
import com.miui.securitycenter.R;
import com.miui.securityscan.c.e;
import com.miui.securityscan.i.h;
import com.miui.superpower.b.k;
import java.util.HashMap;
import java.util.Map;
import miui.app.Activity;
import miui.os.Build;

public class OptimizemanageMainActivity extends b.b.c.c.a {

    /* renamed from: a  reason: collision with root package name */
    public static final String[] f5861a = {"1.306.1.7", "1.306.1.8"};

    /* renamed from: b  reason: collision with root package name */
    public boolean f5862b = false;

    /* renamed from: c  reason: collision with root package name */
    private ActionBarContainer f5863c;

    /* renamed from: d  reason: collision with root package name */
    private b f5864d;
    private a e;

    private static class a extends AsyncTask<Void, Void, Void> {
        private a() {
        }

        /* synthetic */ a(o oVar) {
            this();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            if (isCancelled()) {
                return null;
            }
            n.a(OptimizemanageMainActivity.f5861a);
            return null;
        }
    }

    private static class b extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private Context f5865a;

        public b(Context context) {
            this.f5865a = context.getApplicationContext();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            if (isCancelled()) {
                return null;
            }
            e a2 = e.a(this.f5865a, "data_config");
            HashMap hashMap = new HashMap();
            hashMap.put(Constants.JSON_KEY_DATA_VERSION, a2.a("dataVersionOm", ""));
            String a3 = f.a(this.f5865a, (Map<String, String>) hashMap);
            if (a3 != null) {
                n.a(OptimizemanageMainActivity.f5861a);
            }
            if (isCancelled()) {
                return null;
            }
            try {
                h.a(this.f5865a, "om_adv_data", a3);
            } catch (Exception e) {
                Log.e("OptimizemanageMainActivity", "loadAppManagerAdv writeStringToFileDir error", e);
            }
            return null;
        }
    }

    private void m() {
        this.f5863c = (ActionBarContainer) findViewById(R.id.abc_action_bar);
        this.f5863c.setTitle(getString(R.string.optimize_manage_title));
        this.f5863c.setActionBarEventListener(new o(this));
    }

    private void n() {
        this.e = new a((o) null);
        this.e.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.optimizemanage.OptimizemanageMainActivity] */
    private void o() {
        this.f5864d = new b(this);
        this.f5864d.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public void a(int i) {
        ActionBarContainer actionBarContainer = this.f5863c;
        if (actionBarContainer != null) {
            actionBarContainer.a(i);
        }
    }

    public void a(c cVar) {
        ((v) getFragmentManager().findFragmentByTag("result_fragment")).a(cVar);
    }

    public void l() {
        ActionBarContainer actionBarContainer = this.f5863c;
        if (actionBarContainer != null) {
            actionBarContainer.setIsShowSecondTitle(false);
        }
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [b.b.c.c.a, android.content.Context, com.miui.optimizemanage.OptimizemanageMainActivity, miui.app.Activity, android.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.optimizemanage_activity_main);
        k.a((Activity) this);
        n.a((Context) this);
        m();
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.findFragmentByTag("clean_fragment") == null && fragmentManager.findFragmentByTag("result_fragment") == null) {
            long c2 = com.miui.optimizemanage.settings.c.c();
            if (System.currentTimeMillis() - c2 >= 300000 || c2 == 0 || this.f5862b) {
                int b2 = b.b.c.j.e.b();
                if (b.b.c.j.e.b(this) || b2 <= 9) {
                    this.f5863c.setIsShowSecondTitle(false);
                }
                FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
                beginTransaction.add(R.id.clean_content, new m(), "clean_fragment");
                beginTransaction.commitAllowingStateLoss();
                o();
            } else {
                FragmentTransaction beginTransaction2 = getFragmentManager().beginTransaction();
                beginTransaction2.add(R.id.result_content, new v(), "result_fragment");
                beginTransaction2.commitAllowingStateLoss();
                if (Build.IS_INTERNATIONAL_BUILD) {
                    n();
                }
                this.f5863c.setIsShowSecondTitle(false);
            }
            m();
            n.b("1.306.1.7", "1.306.1.8");
        }
        String stringExtra = getIntent().getStringExtra("enter_homepage_way");
        if (!TextUtils.isEmpty(stringExtra)) {
            com.miui.optimizemanage.a.a.a(stringExtra);
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        OptimizemanageMainActivity.super.onDestroy();
        b bVar = this.f5864d;
        if (bVar != null) {
            bVar.cancel(true);
        }
        a aVar = this.e;
        if (aVar != null) {
            aVar.cancel(true);
        }
    }
}
