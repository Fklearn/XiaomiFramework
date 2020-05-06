package com.miui.gamebooster.ui;

import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.recyclerview.widget.LinearLayoutManager;
import b.b.c.f.a;
import com.miui.appmanager.AppManageUtils;
import com.miui.gamebooster.a.D;
import com.miui.gamebooster.e;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0390v;
import com.miui.gamebooster.mutiwindow.f;
import com.miui.gamebooster.service.IFreeformWindow;
import com.miui.luckymoney.config.AppConstants;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import miui.widget.ProgressBar;
import miuix.recyclerview.widget.RecyclerView;

public class QuickReplySettingsActivity extends b.b.c.c.a implements LoaderManager.LoaderCallbacks<C0455va>, D.a {

    /* renamed from: a  reason: collision with root package name */
    public static final List<String> f4966a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    public static final Comparator<e> f4967b = new Ea();

    /* renamed from: c  reason: collision with root package name */
    private RecyclerView f4968c;

    /* renamed from: d  reason: collision with root package name */
    private ProgressBar f4969d;
    /* access modifiers changed from: private */
    public PackageManager e;
    private ArrayList<e> f = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<String> g = new ArrayList<>();
    private D h;
    /* access modifiers changed from: private */
    public IFreeformWindow i;
    /* access modifiers changed from: private */
    public boolean j;
    /* access modifiers changed from: private */
    public boolean k;
    /* access modifiers changed from: private */
    public boolean l = true;
    a.C0027a m = new Ca(this);

    private static class a extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private Context f4970a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<QuickReplySettingsActivity> f4971b;

        /* renamed from: c  reason: collision with root package name */
        e f4972c;

        public a(QuickReplySettingsActivity quickReplySettingsActivity, e eVar) {
            this.f4970a = quickReplySettingsActivity.getApplicationContext();
            this.f4971b = new WeakReference<>(quickReplySettingsActivity);
            this.f4972c = eVar;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            QuickReplySettingsActivity quickReplySettingsActivity = (QuickReplySettingsActivity) this.f4971b.get();
            if (quickReplySettingsActivity == null || quickReplySettingsActivity.isFinishing()) {
                return null;
            }
            if (this.f4972c.a()) {
                com.miui.gamebooster.provider.a.a(this.f4970a, this.f4972c.e(), this.f4972c.g());
                return null;
            }
            com.miui.gamebooster.provider.a.a(this.f4970a, this.f4972c.e());
            return null;
        }
    }

    static {
        f4966a.add(AppConstants.Package.PACKAGE_NAME_MM);
        f4966a.add(AppConstants.Package.PACKAGE_NAME_QQ);
        f4966a.add("com.whatsapp");
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0031  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.List<java.lang.String> a(android.content.Context r3) {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            android.database.Cursor r3 = com.miui.gamebooster.provider.a.b(r3)     // Catch:{ all -> 0x002d }
            if (r3 == 0) goto L_0x0027
        L_0x000b:
            boolean r1 = r3.moveToNext()     // Catch:{ all -> 0x0025 }
            if (r1 == 0) goto L_0x0027
            java.lang.String r1 = "package_name"
            int r1 = r3.getColumnIndex(r1)     // Catch:{ all -> 0x0025 }
            java.lang.String r1 = r3.getString(r1)     // Catch:{ all -> 0x0025 }
            boolean r2 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x0025 }
            if (r2 != 0) goto L_0x000b
            r0.add(r1)     // Catch:{ all -> 0x0025 }
            goto L_0x000b
        L_0x0025:
            r0 = move-exception
            goto L_0x002f
        L_0x0027:
            if (r3 == 0) goto L_0x002c
            r3.close()
        L_0x002c:
            return r0
        L_0x002d:
            r0 = move-exception
            r3 = 0
        L_0x002f:
            if (r3 == 0) goto L_0x0034
            r3.close()
        L_0x0034:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.ui.QuickReplySettingsActivity.a(android.content.Context):java.util.List");
    }

    /* access modifiers changed from: private */
    public List<String> a(PackageManager packageManager, int i2, HashSet<ComponentName> hashSet) {
        ArrayList arrayList = new ArrayList();
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage((String) null);
        for (ResolveInfo next : AppManageUtils.a(packageManager, intent, 0, i2)) {
            if (!hashSet.contains(new ComponentName(next.activityInfo.packageName, next.activityInfo.name))) {
                arrayList.add(next.activityInfo.packageName);
            }
        }
        return arrayList;
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [com.miui.gamebooster.ui.QuickReplySettingsActivity, android.content.Context, miui.app.Activity, com.miui.gamebooster.a.D$a] */
    private void initView() {
        this.e = getPackageManager();
        com.miui.gamebooster.mutiwindow.a.a((Context) this).a(this.m);
        this.j = f.c(this);
        this.f4968c = (RecyclerView) findViewById(R.id.app_list);
        this.h = new D(this, this.f);
        this.h.a(this.j);
        this.h.a((D.a) this);
        this.f4968c.setLayoutManager(new LinearLayoutManager(this));
        this.f4968c.setAdapter(this.h);
        this.f4969d = findViewById(R.id.qr_progressBar);
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<C0455va> loader, C0455va vaVar) {
        this.f4969d.setVisibility(8);
        this.f = new ArrayList<>(vaVar.f5121a);
        this.g = new ArrayList<>(vaVar.f5122b);
        Collections.sort(this.f, f4967b);
        e eVar = new e(0);
        eVar.b(this.j);
        this.f.add(0, eVar);
        this.h.a((List<e>) this.f);
        this.h.notifyDataSetChanged();
        if (this.i != null && !this.l && this.j && !this.g.isEmpty()) {
            try {
                this.i.d(this.g);
            } catch (Exception e2) {
                Log.e("QuickReplySettings", "setQuickReplyApps error", e2);
            }
        }
    }

    public void a(e eVar) {
        boolean a2 = eVar.a();
        Iterator<e> it = this.f.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            e next = it.next();
            if (next.e().equals(eVar.e())) {
                next.a(a2);
                break;
            }
        }
        if (a2) {
            this.g.add(eVar.e());
        } else {
            this.g.remove(eVar.e());
        }
        IFreeformWindow iFreeformWindow = this.i;
        if (iFreeformWindow != null) {
            try {
                iFreeformWindow.d(this.g);
            } catch (Exception e2) {
                Log.e("QuickReplySettings", "setQuickReplyApps error", e2);
            }
        } else {
            this.l = false;
        }
        new a(this, eVar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        this.h.notifyDataSetChanged();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.quick_replay_setting_layout);
        initView();
        Loader loader = getLoaderManager().getLoader(325);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(325, (Bundle) null, this);
        if (Build.VERSION.SDK_INT >= 24 && bundle != null && loader != null) {
            loaderManager.restartLoader(325, (Bundle) null, this);
        }
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.miui.gamebooster.ui.QuickReplySettingsActivity, android.content.Context] */
    public Loader<C0455va> onCreateLoader(int i2, Bundle bundle) {
        return new Da(this, this);
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [com.miui.gamebooster.ui.QuickReplySettingsActivity, android.content.Context, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onDestroy() {
        QuickReplySettingsActivity.super.onDestroy();
        C0390v.a((Context) this).a();
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [com.miui.gamebooster.ui.QuickReplySettingsActivity, android.content.Context] */
    public void onItemClick(int i2) {
        e a2 = this.h.a(i2);
        int f2 = a2.f();
        if (f2 == 0) {
            this.j = !this.j;
            a2.b(this.j);
            f.b((Context) this, this.j);
            this.h.a(this.j);
            this.h.notifyDataSetChanged();
            if (this.i != null && this.j && !this.g.isEmpty()) {
                try {
                    this.i.d(this.g);
                } catch (Exception e2) {
                    Log.e("QuickReplySettings", "setQuickReplyApps error", e2);
                }
            } else if (this.i == null || this.g.isEmpty()) {
                this.l = false;
            }
            if (this.k) {
                f.a((Context) this, false);
            }
        } else if (this.j && f2 == 1 && a2 != null) {
            boolean z = !a2.a();
            a2.a(z);
            this.h.notifyDataSetChanged();
            a(a2);
            if (z) {
                C0373d.h(a2.e());
            }
        }
    }

    public void onLoaderReset(Loader<C0455va> loader) {
    }
}
