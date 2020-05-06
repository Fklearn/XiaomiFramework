package com.miui.permcenter.privacymanager.behaviorrecord;

import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.common.stickydecoration.PineRecyclerView;
import com.miui.permcenter.widget.b;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import miui.app.AlertDialog;
import miui.util.Log;
import miui.view.SearchActionMode;
import miui.widget.DropDownSingleChoiceMenu;
import miui.widget.ProgressBar;

public class AppBehaviorRecordActivity extends b.b.c.c.a {
    private com.miui.permcenter.b.b A = new g(this);
    private LoaderManager.LoaderCallbacks B = new h(this);
    private View.OnClickListener C = new j(this);
    private SearchActionMode.Callback D = new l(this);
    /* access modifiers changed from: private */
    public TextWatcher E = new b(this);
    private RecyclerView.l F = new c(this);
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public LayoutInflater f6392a;

    /* renamed from: b  reason: collision with root package name */
    private LoaderManager f6393b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public a f6394c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public List<com.miui.permcenter.privacymanager.a.a> f6395d;
    /* access modifiers changed from: private */
    public List<com.miui.permcenter.privacymanager.a.a> e;
    /* access modifiers changed from: private */
    public List<com.miui.permcenter.privacymanager.a.a> f;
    /* access modifiers changed from: private */
    public HashMap<String, ArrayList<Integer>> g;
    private View h;
    /* access modifiers changed from: private */
    public miuix.recyclerview.widget.RecyclerView i;
    /* access modifiers changed from: private */
    public LinearLayoutManager j;
    /* access modifiers changed from: private */
    public a k;
    private com.miui.permcenter.widget.b l;
    private View m;
    /* access modifiers changed from: private */
    public ProgressBar n;
    /* access modifiers changed from: private */
    public AtomicInteger o;
    /* access modifiers changed from: private */
    public volatile boolean p = true;
    /* access modifiers changed from: private */
    public c q;
    /* access modifiers changed from: private */
    public boolean r = false;
    /* access modifiers changed from: private */
    public String[] s;
    private DropDownSingleChoiceMenu t;
    /* access modifiers changed from: private */
    public int u;
    /* access modifiers changed from: private */
    public View v;
    private TextView w;
    /* access modifiers changed from: private */
    public SearchActionMode x;
    private ArrayList<com.miui.permcenter.privacymanager.a.a> y;
    private com.miui.permcenter.b.c z = new f(this);

    private static class a extends b.b.c.i.a<List<com.miui.permcenter.privacymanager.a.a>> {

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<AppBehaviorRecordActivity> f6396b;

        /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, java.lang.Object, com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public a(com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity r2) {
            /*
                r1 = this;
                r1.<init>(r2)
                java.lang.ref.WeakReference r0 = new java.lang.ref.WeakReference
                r0.<init>(r2)
                r1.f6396b = r0
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity.a.<init>(com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity):void");
        }

        public List<com.miui.permcenter.privacymanager.a.a> loadInBackground() {
            AppBehaviorRecordActivity appBehaviorRecordActivity = (AppBehaviorRecordActivity) this.f6396b.get();
            if (appBehaviorRecordActivity != null && !appBehaviorRecordActivity.isFinishing() && !appBehaviorRecordActivity.isDestroyed()) {
                AtomicInteger unused = appBehaviorRecordActivity.o = new AtomicInteger(0);
                appBehaviorRecordActivity.a(o.f6454a);
            }
            return null;
        }
    }

    private static class b extends AsyncTask<Void, Void, List<com.miui.permcenter.privacymanager.a.a>> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<AppBehaviorRecordActivity> f6397a;

        /* renamed from: b  reason: collision with root package name */
        private int f6398b;

        public b(AppBehaviorRecordActivity appBehaviorRecordActivity, int i) {
            this.f6397a = new WeakReference<>(appBehaviorRecordActivity);
            this.f6398b = appBehaviorRecordActivity.b(i);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public List<com.miui.permcenter.privacymanager.a.a> doInBackground(Void... voidArr) {
            AppBehaviorRecordActivity appBehaviorRecordActivity = (AppBehaviorRecordActivity) this.f6397a.get();
            if (appBehaviorRecordActivity == null || appBehaviorRecordActivity.isFinishing() || appBehaviorRecordActivity.isDestroyed() || appBehaviorRecordActivity.f6395d == null) {
                return null;
            }
            if (this.f6398b == com.miui.permcenter.privacymanager.a.b.g) {
                return appBehaviorRecordActivity.f6395d;
            }
            appBehaviorRecordActivity.e.clear();
            for (com.miui.permcenter.privacymanager.a.a aVar : appBehaviorRecordActivity.f6395d) {
                if (aVar.b(this.f6398b)) {
                    appBehaviorRecordActivity.e.add(aVar);
                }
            }
            return appBehaviorRecordActivity.e;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(List<com.miui.permcenter.privacymanager.a.a> list) {
            super.onPostExecute(list);
            AppBehaviorRecordActivity appBehaviorRecordActivity = (AppBehaviorRecordActivity) this.f6397a.get();
            if (appBehaviorRecordActivity != null && !appBehaviorRecordActivity.isFinishing() && !appBehaviorRecordActivity.isDestroyed() && list != null) {
                if (appBehaviorRecordActivity.x == null) {
                    appBehaviorRecordActivity.a(list, appBehaviorRecordActivity.s[appBehaviorRecordActivity.u], true);
                } else {
                    appBehaviorRecordActivity.c(appBehaviorRecordActivity.x.getSearchInput().getText().toString());
                }
                boolean unused = appBehaviorRecordActivity.r = false;
            }
        }
    }

    private static class c extends AsyncTask<Void, Void, List<com.miui.permcenter.privacymanager.a.a>> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<AppBehaviorRecordActivity> f6399a;

        public c(AppBehaviorRecordActivity appBehaviorRecordActivity) {
            this.f6399a = new WeakReference<>(appBehaviorRecordActivity);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public List<com.miui.permcenter.privacymanager.a.a> doInBackground(Void... voidArr) {
            AppBehaviorRecordActivity appBehaviorRecordActivity = (AppBehaviorRecordActivity) this.f6399a.get();
            if (!isCancelled() && !appBehaviorRecordActivity.isFinishing() && !appBehaviorRecordActivity.isDestroyed()) {
                Log.i("BehaviorRecord-ALL", "Loading more doInBackground ...");
                boolean unused = appBehaviorRecordActivity.p = false;
                appBehaviorRecordActivity.a(o.f6454a);
            }
            return null;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(List<com.miui.permcenter.privacymanager.a.a> list) {
            super.onPostExecute(list);
            AppBehaviorRecordActivity appBehaviorRecordActivity = (AppBehaviorRecordActivity) this.f6399a.get();
            if (!isCancelled() && !appBehaviorRecordActivity.isFinishing() && !appBehaviorRecordActivity.isDestroyed()) {
                Log.i("BehaviorRecord-ALL", "Loading more over, refresh and removeFooterView ...");
                appBehaviorRecordActivity.k.a(false);
                new b(appBehaviorRecordActivity, appBehaviorRecordActivity.u).execute(new Void[0]);
            }
        }
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity] */
    /* access modifiers changed from: private */
    public void a(int i2) {
        int size = this.f6395d.size() + i2;
        while (this.f6395d.size() < size) {
            Log.i("BehaviorRecord-ALL", "bulkLoad limit " + i2 + " , offset " + this.o.get());
            this.p = o.a((Context) this, this.f6395d, i2, this.o.get());
            if (!this.p) {
                Log.i("BehaviorRecord-ALL", "loading more already to end");
                runOnUiThread(new i(this));
                return;
            }
            this.o.addAndGet(i2);
        }
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity] */
    private void a(Bundle bundle) {
        this.f6392a = LayoutInflater.from(this);
        com.miui.common.persistence.b.b("PrivacyList", (ArrayList<String>) new ArrayList());
        this.f6395d = new CopyOnWriteArrayList();
        this.e = new CopyOnWriteArrayList();
        this.f6393b = getLoaderManager();
        LoaderManager loaderManager = this.f6393b;
        if (loaderManager != null) {
            if (bundle == null) {
                loaderManager.initLoader(888, (Bundle) null, this.B);
                this.u = 0;
            } else {
                loaderManager.restartLoader(888, (Bundle) null, this.B);
                this.u = bundle.getInt("KEY_MENU_STATE");
            }
        }
        this.s = getResources().getStringArray(R.array.app_behavior_type_menu);
        this.k = new a(this, 0);
        this.k.a(this.z);
        this.k.a(this.A);
        this.i.setAdapter(this.k);
        this.w.setHint(R.string.app_behavior_search);
        this.y = new ArrayList<>();
        com.miui.permcenter.privacymanager.a.a("EnterAllFrom", getIntent().getStringExtra("analytic"));
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity] */
    /* access modifiers changed from: private */
    public void a(@NonNull List<com.miui.permcenter.privacymanager.a.a> list, String str, boolean z2) {
        boolean z3;
        int i2 = 8;
        this.n.setVisibility(8);
        this.f = list;
        boolean z4 = this.f.size() == 0;
        if (!z4 || this.p) {
            this.m.setVisibility(8);
        } else {
            this.m.setVisibility(0);
            this.m.setZ(10.0f);
        }
        if (!z2 || !z4) {
            z3 = z4;
        } else {
            this.f.add(new com.miui.permcenter.privacymanager.a.a());
            z3 = false;
        }
        miuix.recyclerview.widget.RecyclerView recyclerView = this.i;
        if (!z3) {
            i2 = 0;
        }
        recyclerView.setVisibility(i2);
        if (!z3) {
            this.g = o.a((Context) this, this.f, false);
            this.k.a(list);
            this.i.b((RecyclerView.f) this.l);
            b.a a2 = b.a.a((com.miui.permcenter.b.a) new e(this, str));
            a2.b(getResources().getDimensionPixelSize(R.dimen.view_dimen_100));
            a2.a(getResources().getDimensionPixelSize(R.dimen.view_dimen_360));
            a2.a((com.miui.common.stickydecoration.b.b) new d(this));
            if (this.x != null) {
                a2.a(this.v);
            }
            this.l = a2.a();
            this.i.a((RecyclerView.f) this.l);
            if (this.p && z4) {
                this.n.setVisibility(0);
                this.n.setZ(10.0f);
                this.q = new c(this);
                this.q.execute(new Void[0]);
            }
        }
    }

    private boolean a(Intent intent) {
        return !getPackageManager().queryIntentActivities(intent, 0).isEmpty();
    }

    /* access modifiers changed from: private */
    public int b(int i2) {
        return i2 != 1 ? i2 != 2 ? i2 != 3 ? i2 != 4 ? com.miui.permcenter.privacymanager.a.b.g : com.miui.permcenter.privacymanager.a.b.f6332d : com.miui.permcenter.privacymanager.a.b.f6331c : com.miui.permcenter.privacymanager.a.b.f6330b : com.miui.permcenter.privacymanager.a.b.f6329a;
    }

    public static Intent b(String str) {
        Intent intent = new Intent("miui.intent.action.APP_BEHAVIRO_RECORD");
        intent.putExtra("analytic", str);
        return intent;
    }

    /* access modifiers changed from: private */
    public void c(String str) {
        o();
        for (com.miui.permcenter.privacymanager.a.a next : b(this.u) == com.miui.permcenter.privacymanager.a.b.g ? this.f6395d : this.e) {
            if (next.a(str)) {
                this.y.add(next);
            }
        }
        a(this.y, this.s[this.u], false);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity] */
    private void m() {
        this.n = findViewById(R.id.behavior_loading);
        this.h = findViewById(R.id.delegate_app_status_select_menu);
        this.i = (PineRecyclerView) findViewById(R.id.all_behavior_record_list);
        this.j = new LinearLayoutManager(this);
        this.j.j(1);
        this.i.setLayoutManager(this.j);
        this.i.a(this.F);
        this.t = new DropDownSingleChoiceMenu(this);
        this.v = findViewById(R.id.header_view);
        this.v.setOnClickListener(this.C);
        this.w = (TextView) this.v.findViewById(16908297);
        this.m = findViewById(R.id.behavior_empty_view);
    }

    /* access modifiers changed from: private */
    public void n() {
        this.t.setAnchorView(this.h);
        this.t.setItems(Arrays.asList(this.s));
        this.t.setSelectedItem(this.u);
        this.t.setOnMenuListener(new k(this));
        this.t.show();
    }

    /* access modifiers changed from: private */
    public void o() {
        if (!this.y.isEmpty()) {
            this.y.clear();
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity] */
    private void p() {
        new AlertDialog.Builder(this).setTitle(R.string.app_behavior_menu_about).setMessage(R.string.app_behavior_about_content_no_link).setPositiveButton(R.string.app_behavior_about_positive, (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    public void q() {
        this.x = startActionMode(this.D);
    }

    public void l() {
        if (this.x != null) {
            this.x = null;
        }
        new b(this, this.u).execute(new Void[0]);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.pm_activity_behavior_record);
        m();
        a(bundle);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.perm_app_behavior, menu);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        AppBehaviorRecordActivity.super.onDestroy();
        c cVar = this.q;
        if (cVar != null) {
            cVar.cancel(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.behavior_about /*2131296515*/:
                p();
                break;
            case R.id.behavior_bug_report /*2131296516*/:
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setClassName("com.miui.bugreport", "com.miui.bugreport.ui.FeedbackActivity");
                intent.addFlags(268468224);
                if (a(intent)) {
                    startActivity(intent);
                    break;
                }
                break;
        }
        return AppBehaviorRecordActivity.super.onOptionsItemSelected(menuItem);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        AppBehaviorRecordActivity.super.onSaveInstanceState(bundle);
        bundle.putInt("KEY_MENU_STATE", this.u);
    }
}
