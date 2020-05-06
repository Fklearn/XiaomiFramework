package com.miui.appmanager.b;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.UserHandle;
import android.view.Menu;
import android.view.MenuItem;
import b.b.c.j.x;
import com.miui.appmanager.AppManageUtils;
import com.miui.appmanager.F;
import com.miui.securitycenter.R;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.lang.ref.WeakReference;
import miui.text.ExtraTextUtils;
import miuix.preference.TextPreference;
import miuix.preference.s;

public class c extends s implements LoaderManager.LoaderCallbacks<Void> {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public TextPreference f3594a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public TextPreference f3595b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public TextPreference f3596c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public TextPreference f3597d;
    /* access modifiers changed from: private */
    public MenuItem e;
    /* access modifiers changed from: private */
    public ApplicationInfo f;
    private AppManageUtils.ClearCacheObserver g;
    private AppManageUtils.ClearUserDataObserver h;
    private DevicePolicyManager i;
    private Object j;
    /* access modifiers changed from: private */
    public g k;
    private f l;
    private String m;
    private int n;
    private int o;
    /* access modifiers changed from: private */
    public long p;
    /* access modifiers changed from: private */
    public long q;
    /* access modifiers changed from: private */
    public long r;
    /* access modifiers changed from: private */
    public long s;
    /* access modifiers changed from: private */
    public boolean t = true;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener u;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener v;
    private DialogInterface.OnClickListener w;
    private DialogInterface.OnClickListener x;
    private IPackageStatsObserver.Stub y;
    private a z;

    private static class a extends b.b.c.i.a<Void> {

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<c> f3598b;

        /* renamed from: c  reason: collision with root package name */
        private Context f3599c;

        public a(c cVar) {
            super(cVar.getContext());
            this.f3598b = new WeakReference<>(cVar);
            Activity activity = cVar.getActivity();
            if (activity != null) {
                this.f3599c = activity.getApplicationContext();
            }
        }

        public Void loadInBackground() {
            c cVar = (c) this.f3598b.get();
            if (cVar == null || isLoadInBackgroundCanceled()) {
                return null;
            }
            cVar.a(this.f3599c);
            return null;
        }
    }

    private static class b implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<c> f3600a;

        public b(c cVar) {
            this.f3600a = new WeakReference<>(cVar);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            c cVar = (c) this.f3600a.get();
            if (cVar != null) {
                cVar.e.setEnabled(false);
            }
        }
    }

    /* renamed from: com.miui.appmanager.b.c$c  reason: collision with other inner class name */
    private static class C0042c implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<c> f3601a;

        /* renamed from: b  reason: collision with root package name */
        private Context f3602b;

        public C0042c(c cVar) {
            this.f3601a = new WeakReference<>(cVar);
            Activity activity = cVar.getActivity();
            if (activity != null) {
                this.f3602b = activity.getApplicationContext();
            }
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            Activity activity;
            c cVar = (c) this.f3601a.get();
            if (cVar != null && (activity = cVar.getActivity()) != null) {
                if (i != 0) {
                    if (i != 1) {
                        return;
                    }
                } else if (cVar.r > 0 && cVar.t) {
                    if (cVar.f.manageSpaceActivityName != null) {
                        cVar.b((Context) activity);
                        return;
                    } else {
                        cVar.a(activity, 1, cVar.v);
                        return;
                    }
                }
                cVar.a(activity, 3, cVar.u);
            }
        }
    }

    private static class d implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<c> f3603a;

        public d(c cVar) {
            this.f3603a = new WeakReference<>(cVar);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            c cVar = (c) this.f3603a.get();
            if (cVar != null) {
                cVar.b();
            }
        }
    }

    private static class e implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<c> f3604a;

        public e(c cVar) {
            this.f3604a = new WeakReference<>(cVar);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            c cVar = (c) this.f3604a.get();
            if (cVar != null) {
                cVar.a();
            }
        }
    }

    private static class f extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private Context f3605a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<c> f3606b;

        public f(c cVar) {
            Activity activity = cVar.getActivity();
            if (activity != null) {
                this.f3605a = activity.getApplicationContext();
            }
            this.f3606b = new WeakReference<>(cVar);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            c cVar;
            Context context;
            if (isCancelled() || (cVar = (c) this.f3606b.get()) == null || (context = this.f3605a) == null) {
                return null;
            }
            cVar.a(context);
            return null;
        }
    }

    private static class g extends Handler {

        /* renamed from: a  reason: collision with root package name */
        private Context f3607a;

        /* renamed from: b  reason: collision with root package name */
        private final WeakReference<c> f3608b;

        public g(c cVar) {
            Activity activity = cVar.getActivity();
            if (activity != null) {
                this.f3607a = activity.getApplicationContext();
            }
            this.f3608b = new WeakReference<>(cVar);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            c cVar = (c) this.f3608b.get();
            if (cVar != null && this.f3607a != null) {
                int i = message.what;
                if (i == 1) {
                    cVar.f3594a.a(ExtraTextUtils.formatFileSize(this.f3607a, cVar.p));
                    cVar.f3595b.a(ExtraTextUtils.formatFileSize(this.f3607a, cVar.q));
                    cVar.f3596c.a(ExtraTextUtils.formatFileSize(this.f3607a, cVar.r));
                    cVar.f3597d.a(ExtraTextUtils.formatFileSize(this.f3607a, cVar.s));
                    cVar.e();
                } else if (i == 2 || i == 3) {
                    int i2 = message.arg1;
                    int i3 = message.what;
                    if (i2 == 1) {
                        if (Build.VERSION.SDK_INT > 25) {
                            if (i3 == 3) {
                                long unused = cVar.r = 0;
                            } else {
                                long unused2 = cVar.r = cVar.r - cVar.s;
                            }
                            long unused3 = cVar.s = 0;
                            long unused4 = cVar.p = cVar.r + cVar.q;
                            cVar.k.sendEmptyMessage(1);
                            return;
                        }
                        cVar.c();
                    } else if (i3 == 3) {
                        cVar.e.setEnabled(true);
                    }
                }
            }
        }
    }

    private static class h extends IPackageStatsObserver.Stub {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<c> f3609a;

        public h(c cVar) {
            this.f3609a = new WeakReference<>(cVar);
        }

        public void onGetStatsCompleted(PackageStats packageStats, boolean z) {
            c cVar = (c) this.f3609a.get();
            if (cVar != null) {
                long j = packageStats.externalDataSize + packageStats.externalMediaSize + packageStats.dataSize;
                long j2 = packageStats.externalCodeSize + packageStats.externalObbSize + packageStats.codeSize;
                long j3 = packageStats.externalCacheSize + packageStats.cacheSize;
                if (j != cVar.r || j2 != cVar.q || j3 != cVar.s) {
                    long unused = cVar.r = j;
                    long unused2 = cVar.q = j2;
                    long unused3 = cVar.s = j3;
                    long unused4 = cVar.p = cVar.r + cVar.q + cVar.s;
                    cVar.k.sendEmptyMessage(1);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void a() {
        if (this.h == null) {
            this.h = new AppManageUtils.ClearUserDataObserver(this.k);
        }
        this.e.setEnabled(false);
        if (!AppManageUtils.a(this.m, this.o, this.h)) {
            a(getActivity(), 2, this.w);
        }
        com.miui.appmanager.a.a.f("clear_data");
    }

    /* access modifiers changed from: private */
    public void a(Activity activity, int i2, DialogInterface.OnClickListener onClickListener) {
        if (activity != null) {
            AppManageUtils.a((Context) activity, i2, onClickListener);
        }
    }

    /* access modifiers changed from: private */
    public void a(Context context) {
        ApplicationInfo applicationInfo = this.f;
        if (applicationInfo != null) {
            if (Build.VERSION.SDK_INT > 25) {
                F a2 = AppManageUtils.a(context, applicationInfo, this.n);
                if (a2.f3577b != this.r || a2.f3578c != this.q || a2.f3576a != this.s) {
                    this.r = a2.f3577b;
                    this.q = a2.f3578c;
                    this.s = a2.f3576a;
                    this.p = this.r + this.q;
                    this.k.sendEmptyMessage(1);
                    return;
                }
                return;
            }
            Activity activity = getActivity();
            if (activity != null) {
                AppManageUtils.a(activity.getPackageManager(), this.m, this.o, (IPackageStatsObserver) this.y);
            }
        }
    }

    private void a(Bundle bundle) {
        Activity activity = getActivity();
        if (activity != null) {
            activity.setTitle(getString(R.string.app_manager_details_storage_title).concat("-").concat(x.a((Context) activity, this.f)));
            this.f3594a = (TextPreference) findPreference("key_total_size");
            this.f3595b = (TextPreference) findPreference("key_code_size");
            this.f3596c = (TextPreference) findPreference("key_data_size");
            this.f3597d = (TextPreference) findPreference("key_cache_size");
            this.f3594a.a((int) R.string.app_manager_comuting_size);
            this.f3595b.a((int) R.string.app_manager_comuting_size);
            this.f3596c.a((int) R.string.app_manager_comuting_size);
            this.f3597d.a((int) R.string.app_manager_comuting_size);
            this.i = (DevicePolicyManager) activity.getSystemService("device_policy");
            this.y = new h(this);
            this.u = new d(this);
            this.v = new e(this);
            this.w = new b(this);
            this.x = new C0042c(this);
            Loader loader = getLoaderManager().getLoader(131);
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(131, (Bundle) null, this);
            if (Build.VERSION.SDK_INT >= 24 && bundle != null && loader != null) {
                loaderManager.restartLoader(131, (Bundle) null, this);
            }
        }
    }

    /* access modifiers changed from: private */
    public void b() {
        if (this.g == null) {
            this.g = new AppManageUtils.ClearCacheObserver(this.k);
        }
        AppManageUtils.a(this.j, this.m, this.o, this.g);
        com.miui.appmanager.a.a.f("clear_cache");
    }

    /* access modifiers changed from: private */
    public void b(Context context) {
        if (context != null) {
            AppManageUtils.a(context, this.m, this.f.manageSpaceActivityName, this.o, 10024);
        }
    }

    /* access modifiers changed from: private */
    public void c() {
        this.l = new f(this);
        this.l.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void d() {
        ApplicationInfo applicationInfo = this.f;
        if (applicationInfo.manageSpaceActivityName == null && ((applicationInfo.flags & 65) == 1 || AppManageUtils.a(this.i, this.m))) {
            this.t = false;
        }
        this.e.setTitle(R.string.app_manager_menu_clear_data);
    }

    /* access modifiers changed from: private */
    public void e() {
        int i2;
        MenuItem menuItem;
        if (this.e != null) {
            long j2 = this.p;
            if (j2 == -1 || j2 == -2 || ((this.r <= 0 || !this.t) && this.s <= 0)) {
                this.e.setEnabled(false);
                return;
            }
            this.e.setEnabled(true);
            if (this.r > 0 && this.t && this.s > 0) {
                menuItem = this.e;
                i2 = R.string.app_manager_menu_clear_data;
            } else if (this.r <= 0 || !this.t) {
                if (this.s > 0) {
                    menuItem = this.e;
                    i2 = R.string.app_manager_clear_cache;
                } else {
                    return;
                }
            } else if (this.f.manageSpaceActivityName != null) {
                menuItem = this.e;
                i2 = R.string.app_manager_manage_space;
            } else {
                menuItem = this.e;
                i2 = R.string.app_manager_clear_all_data;
            }
            menuItem.setTitle(i2);
        }
    }

    private void finish() {
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            activity.finish();
        }
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<Void> loader, Void voidR) {
    }

    public void a(Menu menu) {
        MenuItem menuItem;
        int i2;
        if (this.f != null) {
            this.e = menu.add(0, 1, 0, R.string.app_manager_menu_clear_data);
            int b2 = b.b.c.j.e.b();
            if (b2 > 8) {
                menuItem = this.e;
                i2 = R.drawable.action_button_clear_svg;
            } else if (b2 > 7) {
                menuItem = this.e;
                i2 = R.drawable.action_button_clear_light;
            } else {
                menuItem = this.e;
                i2 = R.drawable.action_button_clear_light_9;
            }
            menuItem.setIcon(i2);
            this.e.setShowAsAction(1);
            d();
        }
    }

    public void a(MenuItem menuItem) {
        Activity activity;
        boolean z2;
        if (menuItem.getItemId() == 1 && (activity = getActivity()) != null) {
            long j2 = this.r;
            if (j2 > 0 && z2) {
                long j3 = this.s;
                if (j3 > 0) {
                    AppManageUtils.a((Context) activity, this.f.manageSpaceActivityName, j2, j3, (z2 = this.t), this.x);
                    return;
                }
            }
            if (this.r <= 0 || !this.t) {
                if (this.s > 0) {
                    a(activity, 3, this.u);
                }
            } else if (this.f.manageSpaceActivityName != null) {
                b((Context) getActivity());
            } else {
                a(activity, 1, this.v);
            }
        }
    }

    public void b(Menu menu) {
        e();
    }

    public void onActivityResult(int i2, int i3, Intent intent) {
        super.onActivityResult(i2, i3, intent);
        if (i2 == 10024) {
            c();
        }
    }

    public Loader<Void> onCreateLoader(int i2, Bundle bundle) {
        this.z = new a(this);
        return this.z;
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(R.xml.app_manager_storage_details);
        this.k = new g(this);
        Bundle arguments = getArguments();
        this.m = arguments.getString("package_name");
        this.n = arguments.getInt(MijiaAlertModel.KEY_UID, -1);
        this.o = UserHandle.getUserId(this.n);
        try {
            IBinder iBinder = (IBinder) b.b.o.g.e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "package");
            this.j = b.b.o.g.e.a(Class.forName("android.content.pm.IPackageManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder);
            this.f = AppManageUtils.a(this.j, this.m, 0, this.o);
        } catch (Exception unused) {
        }
        if (this.f == null) {
            finish();
        } else {
            a(bundle);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        this.k.removeMessages(1);
        this.k.removeMessages(2);
        this.k.removeMessages(3);
        f fVar = this.l;
        if (fVar != null) {
            fVar.cancel(true);
        }
        a aVar = this.z;
        if (aVar != null) {
            aVar.cancelLoad();
        }
    }

    public void onLoaderReset(Loader<Void> loader) {
    }

    public void onResume() {
        super.onResume();
        try {
            this.f = AppManageUtils.a(this.j, this.m, 0, this.o);
        } catch (Exception unused) {
        }
        if (this.f == null) {
            finish();
        }
    }
}
