package com.miui.optimizecenter.storage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.antivirus.model.DangerousInfo;
import com.miui.appmanager.AppManageUtils;
import com.miui.luckymoney.config.AppConstants;
import com.miui.optimizecenter.storage.AppSystemDataManager;
import com.miui.optimizecenter.storage.a.b;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.i;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.app.AlertDialog;

public class AppStorageDetailActivity extends b.b.c.c.a implements View.OnClickListener, b.a {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public com.miui.optimizecenter.storage.model.b f5665a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public g f5666b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public AppSystemDataManager.AllDataObserver f5667c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public AppSystemDataManager.CacheDataObserver f5668d;
    private AppSystemDataManager.UninstallPkgObserver e;
    /* access modifiers changed from: private */
    public IPackageStatsObserver.Stub f;
    private boolean g;
    private boolean h;
    private boolean i;
    /* access modifiers changed from: private */
    public int j;
    private DialogInterface.OnClickListener k;
    private DialogInterface.OnClickListener l;
    private DialogInterface.OnClickListener m;
    /* access modifiers changed from: private */
    public AppSystemDataManager n;
    private RecyclerView o;
    /* access modifiers changed from: private */
    public com.miui.optimizecenter.storage.a.b p;
    private List<com.miui.optimizecenter.storage.a.c> q = new ArrayList();
    /* access modifiers changed from: private */
    public View r;
    /* access modifiers changed from: private */
    public com.miui.optimizecenter.storage.a.c s;
    /* access modifiers changed from: private */
    public com.miui.optimizecenter.storage.a.c t;

    private class a implements DialogInterface.OnClickListener {
        private a() {
        }

        /* synthetic */ a(AppStorageDetailActivity appStorageDetailActivity, e eVar) {
            this();
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            AppStorageDetailActivity.this.t.a(false);
            AppStorageDetailActivity.this.f5666b.sendEmptyMessage(-1006);
        }
    }

    private class b implements DialogInterface.OnClickListener {
        private b() {
        }

        /* synthetic */ b(AppStorageDetailActivity appStorageDetailActivity, e eVar) {
            this();
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            AppManageUtils.a((Object) AppStorageDetailActivity.this.getPackageManager(), AppStorageDetailActivity.this.f5665a.f5762d, AppStorageDetailActivity.this.j, (AppManageUtils.ClearCacheObserver) AppStorageDetailActivity.this.f5668d);
            AppStorageDetailActivity.this.s.a(false);
            AppStorageDetailActivity.this.f5666b.sendEmptyMessage(-1006);
        }
    }

    private class c implements DialogInterface.OnClickListener {
        private c() {
        }

        /* synthetic */ c(AppStorageDetailActivity appStorageDetailActivity, e eVar) {
            this();
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (AppStorageDetailActivity.this.t != null) {
                AppStorageDetailActivity.this.t.a(false);
            }
            if (!AppStorageDetailActivity.this.n.a(AppStorageDetailActivity.this.f5665a.f5762d, AppStorageDetailActivity.this.j, AppStorageDetailActivity.this.f5667c)) {
                AppStorageDetailActivity.this.b(1002);
            }
            AppStorageDetailActivity.this.f5666b.sendEmptyMessage(-1006);
        }
    }

    private class d extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<AppStorageDetailActivity> f5672a;

        /* renamed from: b  reason: collision with root package name */
        private int f5673b;

        public d(AppStorageDetailActivity appStorageDetailActivity, int i) {
            this.f5672a = new WeakReference<>(appStorageDetailActivity);
            this.f5673b = i;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            AppStorageDetailActivity appStorageDetailActivity;
            if (isCancelled() || (appStorageDetailActivity = (AppStorageDetailActivity) this.f5672a.get()) == null || appStorageDetailActivity.isFinishing()) {
                return null;
            }
            appStorageDetailActivity.getPackageManager().setApplicationEnabledSetting(AppStorageDetailActivity.this.f5665a.f5762d, this.f5673b, 0);
            AppStorageDetailActivity.this.f5666b.sendEmptyMessage(-1005);
            return null;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Void voidR) {
            View view;
            boolean z;
            AppStorageDetailActivity appStorageDetailActivity = (AppStorageDetailActivity) this.f5672a.get();
            if (appStorageDetailActivity != null && !appStorageDetailActivity.isFinishing()) {
                if (AppSystemDataManager.f5686b.contains(AppStorageDetailActivity.this.f5665a.f5762d)) {
                    view = AppStorageDetailActivity.this.r;
                    z = true;
                } else {
                    view = AppStorageDetailActivity.this.r;
                    z = false;
                }
                view.setEnabled(z);
            }
        }
    }

    private class e extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private Context f5675a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<AppStorageDetailActivity> f5676b;

        public e(AppStorageDetailActivity appStorageDetailActivity) {
            this.f5675a = appStorageDetailActivity.getApplicationContext();
            this.f5676b = new WeakReference<>(appStorageDetailActivity);
        }

        private void a(Context context) {
            if (AppStorageDetailActivity.this.f5665a.h != null) {
                if (Build.VERSION.SDK_INT > 25) {
                    com.miui.optimizecenter.storage.model.b a2 = AppStorageDetailActivity.this.n.a(context, AppStorageDetailActivity.this.f5665a.h, AppStorageDetailActivity.this.f5665a.f5760b);
                    long j = a2.m;
                    long j2 = a2.l;
                    long j3 = j + j2;
                    if (j3 != AppStorageDetailActivity.this.f5665a.k || j2 != AppStorageDetailActivity.this.f5665a.l) {
                        AppStorageDetailActivity.this.f5665a.k = j3;
                        AppStorageDetailActivity.this.f5665a.l = a2.l;
                        AppStorageDetailActivity.this.f5665a.p = a2.p;
                        AppStorageDetailActivity.this.f5665a.m = a2.m;
                        AppStorageDetailActivity.this.f5666b.sendEmptyMessage(-1004);
                        return;
                    }
                    return;
                }
                AppStorageDetailActivity.this.n.a(AppStorageDetailActivity.this.f5665a.f5762d, AppStorageDetailActivity.this.f5665a.f5760b, (IPackageStatsObserver) AppStorageDetailActivity.this.f);
            }
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            AppStorageDetailActivity appStorageDetailActivity;
            if (isCancelled() || (appStorageDetailActivity = (AppStorageDetailActivity) this.f5676b.get()) == null || appStorageDetailActivity.isFinishing()) {
                return null;
            }
            a(this.f5675a);
            return null;
        }
    }

    private class f extends IPackageStatsObserver.Stub {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<AppStorageDetailActivity> f5678a;

        public f(AppStorageDetailActivity appStorageDetailActivity) {
            this.f5678a = new WeakReference<>(appStorageDetailActivity);
        }

        public void onGetStatsCompleted(PackageStats packageStats, boolean z) {
            AppStorageDetailActivity appStorageDetailActivity = (AppStorageDetailActivity) this.f5678a.get();
            if (appStorageDetailActivity != null && !appStorageDetailActivity.isFinishing() && !appStorageDetailActivity.isDestroyed()) {
                long j = packageStats.externalDataSize + packageStats.externalMediaSize + packageStats.dataSize;
                long j2 = packageStats.externalCodeSize + packageStats.externalObbSize + packageStats.codeSize;
                long j3 = packageStats.externalCacheSize + packageStats.cacheSize;
                if (j != AppStorageDetailActivity.this.f5665a.l || j2 != AppStorageDetailActivity.this.f5665a.m || j3 != AppStorageDetailActivity.this.f5665a.p) {
                    AppStorageDetailActivity.this.f5665a.l = j;
                    AppStorageDetailActivity.this.f5665a.m = j2;
                    AppStorageDetailActivity.this.f5665a.p = j3;
                    AppStorageDetailActivity.this.f5665a.k = AppStorageDetailActivity.this.f5665a.l + AppStorageDetailActivity.this.f5665a.m + AppStorageDetailActivity.this.f5665a.p;
                    AppStorageDetailActivity.this.f5666b.sendEmptyMessage(-1004);
                }
            }
        }
    }

    private class g extends Handler {
        private g() {
        }

        /* synthetic */ g(AppStorageDetailActivity appStorageDetailActivity, e eVar) {
            this();
        }

        private void a() {
            AppStorageDetailActivity appStorageDetailActivity = AppStorageDetailActivity.this;
            new e(appStorageDetailActivity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }

        private void a(Message message, boolean z) {
            long j;
            int i = message.what;
            if (z) {
                long j2 = 0;
                if (Build.VERSION.SDK_INT > 25) {
                    if (i == -1001) {
                        long j3 = AppStorageDetailActivity.this.f5665a.l;
                        j = AppStorageDetailActivity.this.f5665a.p;
                        AppStorageDetailActivity.this.f5665a.l = 0;
                        AppStorageDetailActivity.this.f5665a.p = 0;
                        j2 = j3;
                    } else {
                        AppStorageDetailActivity.this.f5665a.l -= AppStorageDetailActivity.this.f5665a.p;
                        AppStorageDetailActivity.this.f5665a.p = 0;
                        j = 0;
                    }
                    AppStorageDetailActivity.this.f5665a.k = AppStorageDetailActivity.this.f5665a.o + AppStorageDetailActivity.this.f5665a.l + AppStorageDetailActivity.this.f5665a.m;
                    AppStorageDetailActivity.this.g();
                } else {
                    a();
                    j = 0;
                }
                s a2 = s.a((Context) Application.d());
                a2.a(j2, j);
                a2.a(true);
            } else if (i == -1001) {
                AppStorageDetailActivity.this.t.a(false);
                if (AppStorageDetailActivity.this.p != null) {
                    AppStorageDetailActivity.this.p.notifyDataSetChanged();
                }
            }
        }

        /* JADX WARNING: type inference failed for: r0v3, types: [android.content.Context, com.miui.optimizecenter.storage.AppStorageDetailActivity] */
        /* JADX WARNING: type inference failed for: r8v15, types: [android.content.Context, com.miui.optimizecenter.storage.AppStorageDetailActivity] */
        public void handleMessage(Message message) {
            switch (message.what) {
                case -1006:
                    if (AppStorageDetailActivity.this.p != null) {
                        AppStorageDetailActivity.this.p.notifyDataSetChanged();
                        return;
                    }
                    return;
                case -1005:
                    ApplicationInfo applicationInfo = null;
                    boolean z = false;
                    try {
                        applicationInfo = AppStorageDetailActivity.this.n.a(AppStorageDetailActivity.this.f5665a.f5762d, 128, ((Integer) b.b.o.g.e.a((Class<?>) UserHandle.class, "getUserId", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(AppStorageDetailActivity.this.f5665a.f5760b))).intValue());
                    } catch (Exception e) {
                        Log.e("AppStorageDetail", "handle message get application info error", e);
                    }
                    if (applicationInfo != null) {
                        AppStorageDetailActivity.this.f5665a.h = applicationInfo;
                    }
                    if (applicationInfo != null && applicationInfo.enabled) {
                        z = true;
                    }
                    com.miui.securityscan.i.c.a((Context) AppStorageDetailActivity.this, z ? R.string.app_manager_enabled : R.string.app_manager_disabled);
                    return;
                case -1004:
                    AppStorageDetailActivity.this.g();
                    return;
                case -1003:
                    if (!AppStorageDetailActivity.this.isFinishing()) {
                        com.miui.securityscan.i.c.a((Context) AppStorageDetailActivity.this, (int) R.string.storage_app_detail_uninstall_done);
                        AppStorageDetailActivity.this.finish();
                        s a2 = s.a((Context) Application.d());
                        a2.a(AppStorageDetailActivity.this.f5665a.f5762d);
                        a2.a(true);
                        return;
                    }
                    return;
                case -1002:
                case DangerousInfo.INVALID_VERSION_CODE:
                    a(message, ((Boolean) message.obj).booleanValue());
                    return;
                default:
                    return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(int i2) {
        new d(this, i2).execute(new Void[0]);
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.optimizecenter.storage.AppStorageDetailActivity] */
    private void a(CharSequence charSequence, CharSequence charSequence2) {
        new AlertDialog.Builder(this).setTitle(charSequence).setMessage(charSequence2).setPositiveButton(R.string.storage_app_manager_disable_text, new b(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    private static boolean a(Context context, String str, int i2) {
        try {
            return ((Boolean) b.b.o.g.e.a(Class.forName("miui.content.pm.PreloadedAppPolicy"), Boolean.TYPE, "isProtectedDataApp", (Class<?>[]) new Class[]{Context.class, String.class, Integer.TYPE}, context, str, Integer.valueOf(i2))).booleanValue();
        } catch (Exception e2) {
            Log.e("TAG", "isProtectedDataApp: ", e2);
            return false;
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.optimizecenter.storage.AppStorageDetailActivity] */
    /* access modifiers changed from: private */
    public void b(int i2) {
        AlertDialog.Builder builder;
        DialogInterface.OnClickListener onClickListener;
        if (i2 == 1000) {
            builder = new AlertDialog.Builder(this).setTitle(R.string.storage_app_detail_clear_all_dialog_title).setMessage(R.string.storage_app_detail_clear_all_dialog_message).setNegativeButton(R.string.storage_app_detail_dialog_cancel, (DialogInterface.OnClickListener) null);
            onClickListener = this.k;
        } else if (i2 == 1002) {
            new AlertDialog.Builder(this).setTitle(R.string.storage_app_manager_clear_dlg_title).setMessage(R.string.storage_app_manager_clear_failed_dlg_message).setNeutralButton(R.string.storage_app_detail_dialog_ok, this.l).create().show();
            return;
        } else if (i2 == 10001) {
            builder = new AlertDialog.Builder(this).setTitle(R.string.storage_app_detail_clear_cache_title).setNegativeButton(R.string.storage_app_detail_dialog_cancel, (DialogInterface.OnClickListener) null);
            onClickListener = this.m;
        } else {
            return;
        }
        builder.setPositiveButton(R.string.storage_app_detail_dialog_ok, onClickListener).show();
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.optimizecenter.storage.AppStorageDetailActivity] */
    private void b(CharSequence charSequence, CharSequence charSequence2) {
        new AlertDialog.Builder(this).setTitle(charSequence).setMessage(charSequence2).setPositiveButton(R.string.storage_app_manager_uninstall_application, new a(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.optimizecenter.storage.AppStorageDetailActivity] */
    private void c(int i2) {
        int i3 = R.string.storage_app_uninstall_app_dialog_title;
        int i4 = R.string.storage_app_uninstall_app_dialog_msg;
        if (i2 == 0) {
            i3 = R.string.storage_app_manager_factory_reset_dlg_title;
            i4 = R.string.storage_app_manager_factory_reset_dlg_msg;
        } else if (i2 == 1) {
            if (this.g) {
                i3 = R.string.storage_app_manager_uninstall_xspace_app_dlg_title;
                i4 = R.string.storage_app_manager_uninstall_xspace_app_dlg_msg;
            } else if (this.h) {
                i4 = R.string.storage_app_manager_uninstall_with_xspace_app_dlg_msg;
            }
            if (!this.i && a(this, this.f5665a.f5762d, 0)) {
                i3 = R.string.storage_app_manager_uninstall_protected_dlg_title;
                i4 = R.string.storage_app_manager_uninstall_protected_dlg_msg;
            }
        }
        new AlertDialog.Builder(this).setTitle(i3).setMessage(i4).setPositiveButton(17039370, new e(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    public void g() {
        long j2;
        for (int i2 = 0; i2 < this.q.size(); i2++) {
            com.miui.optimizecenter.storage.a.c cVar = this.q.get(i2);
            if (cVar != null) {
                int i3 = g.f5738a[cVar.b().ordinal()];
                if (i3 == 1) {
                    j2 = this.f5665a.k;
                } else if (i3 == 2) {
                    j2 = this.f5665a.m;
                } else if (i3 == 3) {
                    j2 = this.f5665a.p;
                } else if (i3 == 4) {
                    com.miui.optimizecenter.storage.model.b bVar = this.f5665a;
                    cVar.a(bVar.o + bVar.l);
                }
                cVar.a(j2);
            }
        }
        com.miui.optimizecenter.storage.a.b bVar2 = this.p;
        if (bVar2 != null) {
            bVar2.notifyDataSetChanged();
        }
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [android.content.Context, com.miui.optimizecenter.storage.a.b$a, android.view.View$OnClickListener, miui.app.Activity, com.miui.optimizecenter.storage.AppStorageDetailActivity] */
    private void initView() {
        com.miui.optimizecenter.storage.a.c cVar;
        List<com.miui.optimizecenter.storage.a.c> list;
        com.miui.optimizecenter.storage.a.c cVar2;
        this.r = findViewById(R.id.uninstall);
        this.r.setOnClickListener(this);
        this.o = (RecyclerView) findViewById(R.id.rv_main);
        Resources resources = getResources();
        this.q.clear();
        List<com.miui.optimizecenter.storage.a.c> list2 = this.q;
        com.miui.optimizecenter.storage.model.b bVar = this.f5665a;
        list2.add(new com.miui.optimizecenter.storage.a.c(bVar.f5761c, bVar.e, bVar.f, com.miui.optimizecenter.storage.a.d.HEADER));
        this.q.add(new com.miui.optimizecenter.storage.a.c(com.miui.optimizecenter.storage.a.d.TOTAL_SIZE));
        this.q.add(new com.miui.optimizecenter.storage.a.c(com.miui.optimizecenter.storage.a.d.APP_SIZE));
        this.q.add(new com.miui.optimizecenter.storage.a.c(com.miui.optimizecenter.storage.a.d.CACHE_SIZE));
        this.q.add(new com.miui.optimizecenter.storage.a.c(com.miui.optimizecenter.storage.a.d.USER_DATA_SIZE));
        this.q.add(new com.miui.optimizecenter.storage.a.c(com.miui.optimizecenter.storage.a.d.LINE));
        if (this.f5665a.a() == 0) {
            cVar = new com.miui.optimizecenter.storage.a.c(com.miui.optimizecenter.storage.a.d.CLEAR_CACHE);
        } else {
            if (this.f5665a.a() == 2) {
                if (AppConstants.Package.PACKAGE_NAME_MM.equals(this.f5665a.f5762d)) {
                    List<com.miui.optimizecenter.storage.a.c> list3 = this.q;
                    com.miui.optimizecenter.storage.a.c cVar3 = new com.miui.optimizecenter.storage.a.c((int) R.string.storage_app_detail_wechat_cleaner, com.miui.optimizecenter.storage.a.d.APP_CLEANER);
                    cVar3.a(true);
                    list3.add(cVar3);
                    list = this.q;
                    cVar2 = new com.miui.optimizecenter.storage.a.c(com.miui.optimizecenter.storage.a.d.APP_WECHAT_CLEANER);
                } else if (AppConstants.Package.PACKAGE_NAME_QQ.equals(this.f5665a.f5762d)) {
                    list = this.q;
                    cVar2 = new com.miui.optimizecenter.storage.a.c((int) R.string.storage_app_detail_qq_cleaner, com.miui.optimizecenter.storage.a.d.APP_CLEANER);
                }
                cVar2.a(true);
                list.add(cVar2);
            } else if (this.f5665a.a() == 1) {
                com.miui.optimizecenter.storage.a.c cVar4 = new com.miui.optimizecenter.storage.a.c(resources.getString(R.string.storage_app_detail_manage_space, new Object[]{this.f5665a.f5761c}), com.miui.optimizecenter.storage.a.d.MANAGER_STORAGE_SELF);
                cVar4.a(true);
                this.q.add(cVar4);
                cVar = new com.miui.optimizecenter.storage.a.c(com.miui.optimizecenter.storage.a.d.CLEAR_CACHE);
            }
            com.miui.optimizecenter.storage.a.c cVar5 = new com.miui.optimizecenter.storage.a.c(com.miui.optimizecenter.storage.a.d.CLEAR_ALL_DATA);
            cVar5.a(true);
            this.t = cVar5;
            this.q.add(this.t);
            g();
            this.p = new com.miui.optimizecenter.storage.a.b(this.q);
            this.o.setAdapter(this.p);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.j(1);
            this.o.setLayoutManager(linearLayoutManager);
            this.p.a(this);
        }
        cVar.a(true);
        this.s = cVar;
        list = this.q;
        cVar2 = this.s;
        list.add(cVar2);
        com.miui.optimizecenter.storage.a.c cVar52 = new com.miui.optimizecenter.storage.a.c(com.miui.optimizecenter.storage.a.d.CLEAR_ALL_DATA);
        cVar52.a(true);
        this.t = cVar52;
        this.q.add(this.t);
        g();
        this.p = new com.miui.optimizecenter.storage.a.b(this.q);
        this.o.setAdapter(this.p);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.j(1);
        this.o.setLayoutManager(linearLayoutManager2);
        this.p.a(this);
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.optimizecenter.storage.AppStorageDetailActivity] */
    private void l() {
        Intent intent;
        if (this.f5665a.f5762d.equals(AppConstants.Package.PACKAGE_NAME_MM)) {
            intent = new Intent("miui.intent.action.GARBAGE_DEEPCLEAN_WECHAT");
        } else if (this.f5665a.f5762d.equals(AppConstants.Package.PACKAGE_NAME_QQ)) {
            intent = new Intent("miui.intent.action.GARBAGE_DEEPCLEAN_QQ");
        } else {
            return;
        }
        com.miui.cleanmaster.g.b(this, intent);
        this.f5665a.q = 0;
    }

    private void m() {
        if (this.f5665a.f5762d.equals(AppConstants.Package.PACKAGE_NAME_MM)) {
            try {
                Intent parseUri = Intent.parseUri("#Intent;component=com.tencent.mm/.plugin.clean.ui.fileindexui.CleanChattingUI;end", 0);
                if (i.a((Context) Application.d(), parseUri)) {
                    startActivity(parseUri);
                    this.f5665a.q = 0;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [android.content.Context, miui.app.Activity, com.miui.optimizecenter.storage.AppStorageDetailActivity] */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x004f  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x009f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void n() {
        /*
            r8 = this;
            com.miui.optimizecenter.storage.model.b r0 = r8.f5665a
            android.content.pm.ApplicationInfo r0 = r0.h
            int r0 = r0.flags
            r0 = r0 & 128(0x80, float:1.794E-43)
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x000e
            r0 = r1
            goto L_0x000f
        L_0x000e:
            r0 = r2
        L_0x000f:
            if (r0 == 0) goto L_0x0016
            r8.c((int) r2)
            goto L_0x00a2
        L_0x0016:
            com.miui.optimizecenter.storage.model.b r0 = r8.f5665a
            android.content.pm.ApplicationInfo r0 = r0.h
            android.content.pm.PackageManager r3 = r8.getPackageManager()
            r4 = 0
            if (r0 == 0) goto L_0x004a
            android.os.Bundle r5 = r0.metaData
            if (r5 == 0) goto L_0x004a
            android.os.Bundle r5 = r0.metaData
            java.lang.String r6 = "app_description_title"
            int r5 = r5.getInt(r6)
            android.os.Bundle r6 = r0.metaData
            java.lang.String r7 = "app_description_content"
            int r6 = r6.getInt(r7)
            if (r5 == 0) goto L_0x004a
            if (r6 == 0) goto L_0x004a
            com.miui.optimizecenter.storage.model.b r4 = r8.f5665a
            java.lang.String r4 = r4.f5762d
            java.lang.CharSequence r4 = r3.getText(r4, r5, r0)
            com.miui.optimizecenter.storage.model.b r5 = r8.f5665a
            java.lang.String r5 = r5.f5762d
            java.lang.CharSequence r3 = r3.getText(r5, r6, r0)
            goto L_0x004b
        L_0x004a:
            r3 = r4
        L_0x004b:
            boolean r0 = r0.enabled
            if (r0 == 0) goto L_0x009f
            boolean r0 = r8.i
            if (r0 == 0) goto L_0x0074
            com.miui.optimizecenter.storage.model.b r0 = r8.f5665a
            java.lang.String r0 = r0.f5762d
            java.lang.String r5 = "com.miui.greenguard"
            boolean r0 = r5.equals(r0)
            if (r0 != 0) goto L_0x0074
            boolean r0 = android.text.TextUtils.isEmpty(r4)
            if (r0 != 0) goto L_0x0070
            boolean r0 = android.text.TextUtils.isEmpty(r3)
            if (r0 == 0) goto L_0x006c
            goto L_0x0070
        L_0x006c:
            r8.a((java.lang.CharSequence) r4, (java.lang.CharSequence) r3)
            goto L_0x00a2
        L_0x0070:
            r8.o()
            goto L_0x00a2
        L_0x0074:
            boolean r0 = r8.i
            if (r0 != 0) goto L_0x009b
            com.miui.optimizecenter.storage.model.b r0 = r8.f5665a
            java.lang.String r0 = r0.f5762d
            boolean r0 = a(r8, r0, r2)
            if (r0 == 0) goto L_0x009b
            boolean r0 = android.text.TextUtils.isEmpty(r4)
            if (r0 != 0) goto L_0x009b
            boolean r0 = android.text.TextUtils.isEmpty(r3)
            if (r0 == 0) goto L_0x008f
            goto L_0x009b
        L_0x008f:
            java.lang.String r0 = r4.toString()
            java.lang.String r1 = r3.toString()
            r8.b((java.lang.CharSequence) r0, (java.lang.CharSequence) r1)
            goto L_0x00a2
        L_0x009b:
            r8.c((int) r1)
            goto L_0x00a2
        L_0x009f:
            r8.a((int) r2)
        L_0x00a2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.optimizecenter.storage.AppStorageDetailActivity.n():void");
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.optimizecenter.storage.AppStorageDetailActivity] */
    private void o() {
        new AlertDialog.Builder(this).setTitle(R.string.storage_app_manager_disable_dlg_title).setMessage(R.string.storage_app_manager_disable_dlg_text).setPositiveButton(17039370, new f(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    public void p() {
        AppSystemDataManager appSystemDataManager;
        String str;
        int i2;
        AppSystemDataManager.UninstallPkgObserver uninstallPkgObserver;
        int i3;
        if (this.g) {
            appSystemDataManager = this.n;
            com.miui.optimizecenter.storage.model.b bVar = this.f5665a;
            str = bVar.f5762d;
            i2 = bVar.g;
            uninstallPkgObserver = this.e;
            i3 = this.j;
        } else {
            AppSystemDataManager appSystemDataManager2 = this.n;
            com.miui.optimizecenter.storage.model.b bVar2 = this.f5665a;
            appSystemDataManager2.a(bVar2.f5762d, bVar2.g, (IPackageDeleteObserver) this.e, this.j, 0);
            if (this.h) {
                appSystemDataManager = this.n;
                com.miui.optimizecenter.storage.model.b bVar3 = this.f5665a;
                str = bVar3.f5762d;
                i2 = bVar3.g;
                uninstallPkgObserver = null;
                i3 = 999;
            } else {
                return;
            }
        }
        appSystemDataManager.a(str, i2, (IPackageDeleteObserver) uninstallPkgObserver, i3, 0);
    }

    public /* synthetic */ void a(DialogInterface dialogInterface, int i2) {
        o();
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(com.miui.optimizecenter.storage.a.c r8) {
        /*
            r7 = this;
            if (r8 != 0) goto L_0x0003
            return
        L_0x0003:
            int[] r0 = com.miui.optimizecenter.storage.g.f5738a
            com.miui.optimizecenter.storage.a.d r8 = r8.b()
            int r8 = r8.ordinal()
            r8 = r0[r8]
            switch(r8) {
                case 5: goto L_0x0033;
                case 6: goto L_0x0030;
                case 7: goto L_0x001b;
                case 8: goto L_0x0017;
                case 9: goto L_0x0013;
                default: goto L_0x0012;
            }
        L_0x0012:
            goto L_0x0038
        L_0x0013:
            r7.l()
            goto L_0x0038
        L_0x0017:
            r7.m()
            goto L_0x0038
        L_0x001b:
            com.miui.optimizecenter.storage.model.b r8 = r7.f5665a
            android.content.pm.ApplicationInfo r0 = r8.h
            java.lang.String r4 = r0.manageSpaceActivityName
            if (r4 == 0) goto L_0x0038
            com.miui.optimizecenter.storage.AppSystemDataManager r1 = r7.n
            java.lang.String r3 = r8.f5762d
            int r5 = r7.j
            r6 = 10022(0x2726, float:1.4044E-41)
            r2 = r7
            r1.a((android.content.Context) r2, (java.lang.String) r3, (java.lang.String) r4, (int) r5, (int) r6)
            goto L_0x0038
        L_0x0030:
            r8 = 1000(0x3e8, float:1.401E-42)
            goto L_0x0035
        L_0x0033:
            r8 = 10001(0x2711, float:1.4014E-41)
        L_0x0035:
            r7.b((int) r8)
        L_0x0038:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.optimizecenter.storage.AppStorageDetailActivity.a(com.miui.optimizecenter.storage.a.c):void");
    }

    public /* synthetic */ void b(DialogInterface dialogInterface, int i2) {
        c(1);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.uninstall) {
            n();
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [b.b.c.c.a, android.content.Context, miui.app.Activity, com.miui.optimizecenter.storage.AppStorageDetailActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_storage_app_detail);
        if (getIntent() == null) {
            finish();
            return;
        }
        String stringExtra = getIntent().getStringExtra("model");
        int intExtra = getIntent().getIntExtra("uId", -1);
        if (stringExtra == null || intExtra == -1) {
            finish();
            return;
        }
        Iterator<com.miui.optimizecenter.storage.model.b> it = s.a((Context) this).a().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            com.miui.optimizecenter.storage.model.b next = it.next();
            if (next.f5762d.equals(stringExtra) && next.f5760b == intExtra) {
                this.f5665a = next;
                break;
            }
        }
        if (this.f5665a == null) {
            finish();
            return;
        }
        this.f5666b = new g(this, (e) null);
        this.f5667c = new AppSystemDataManager.AllDataObserver(this.f5666b);
        this.f5668d = new AppSystemDataManager.CacheDataObserver(this.f5666b);
        this.e = new AppSystemDataManager.UninstallPkgObserver(this.f5666b);
        initView();
        this.n = AppSystemDataManager.a((Context) this);
        this.f = new f(this);
        boolean z = true;
        if ((this.f5665a.h.flags & 1) == 0) {
            z = false;
        }
        this.i = z;
        this.g = this.n.b(this.f5665a.f5760b);
        this.h = this.n.a(this.f5665a.f5762d);
        this.j = this.n.a(this.f5665a.f5760b);
        this.k = new c(this, (e) null);
        this.l = new a(this, (e) null);
        this.m = new b(this, (e) null);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        g();
    }
}
