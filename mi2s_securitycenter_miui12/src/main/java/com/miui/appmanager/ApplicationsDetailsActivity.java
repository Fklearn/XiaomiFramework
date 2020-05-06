package com.miui.appmanager;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.LoaderManager;
import android.app.admin.DevicePolicyManager;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import b.b.c.c.a;
import b.b.c.j.C;
import b.b.c.j.C0195b;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.miui.appmanager.AppManageUtils;
import com.miui.appmanager.widget.AppDetailBannerItemView;
import com.miui.appmanager.widget.AppDetailCheckBoxView;
import com.miui.appmanager.widget.AppDetailListTitleView;
import com.miui.appmanager.widget.AppDetailRightSummaryPointView;
import com.miui.appmanager.widget.AppDetailTextBannerView;
import com.miui.appmanager.widget.AppDetailTitleView;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.firewall.UserConfigure;
import com.miui.networkassistant.model.DataUsageConstants;
import com.miui.networkassistant.traffic.statistic.PreSetGroup;
import com.miui.networkassistant.traffic.statistic.StatisticAppTraffic;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.networkassistant.utils.HybirdServiceUtil;
import com.miui.networkassistant.utils.PackageUtil;
import com.miui.permcenter.autostart.AutoStartDetailManagementActivity;
import com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity;
import com.miui.permission.PermissionManager;
import com.miui.permission.RequiredPermissionsUtil;
import com.miui.powercenter.legacypowerrank.BatteryData;
import com.miui.powercenter.legacypowerrank.PowerDetailActivity;
import com.miui.powercenter.legacypowerrank.b;
import com.miui.powerkeeper.IPowerKeeper;
import com.miui.securitycenter.R;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import miui.app.ActionBar;
import miui.app.AlertDialog;
import miui.os.Build;
import miui.provider.ExtraNetwork;
import miui.text.ExtraTextUtils;

public class ApplicationsDetailsActivity extends a implements View.OnClickListener, LoaderManager.LoaderCallbacks<Boolean>, CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    private static final Object f3519a = new Object();
    private ActivityManager A;
    /* access modifiers changed from: private */
    public int Aa;
    /* access modifiers changed from: private */
    public Object B;
    /* access modifiers changed from: private */
    public int Ba;
    private Object C;
    /* access modifiers changed from: private */
    public int Ca;
    /* access modifiers changed from: private */
    public PackageManager D;
    private int Da = b.b.c.j.e.b();
    private DevicePolicyManager E;
    /* access modifiers changed from: private */
    public HashMap<Long, Integer> Ea = new HashMap<>();
    /* access modifiers changed from: private */
    public q F;
    private HashSet<String> Fa;
    private AppManageUtils.ClearUserDataObserver G;
    private IPackageStatsObserver.Stub Ga;
    private AppManageUtils.ClearCacheObserver H;
    /* access modifiers changed from: private */
    public IPowerKeeper Ha = null;
    /* access modifiers changed from: private */
    public AppOpsManager I;
    private u Ia;
    private y J;
    private ServiceConnection Ja;
    private l K;
    private CompoundButton.OnCheckedChangeListener Ka;
    private p L;
    private CompoundButton.OnCheckedChangeListener La;
    private o M;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener Ma;
    private AppWidgetManager N;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener Na;
    private C0316a O;
    private DialogInterface.OnClickListener Oa;
    /* access modifiers changed from: private */
    public int P;
    private DialogInterface.OnClickListener Pa;
    /* access modifiers changed from: private */
    public int Q;
    /* access modifiers changed from: private */
    public long R;
    /* access modifiers changed from: private */
    public long S = 0;
    /* access modifiers changed from: private */
    public long T = 0;
    /* access modifiers changed from: private */
    public long U = 0;
    /* access modifiers changed from: private */
    public int V;
    private int W;
    private boolean X = false;
    private w Y;
    /* access modifiers changed from: private */
    public long Z;
    /* access modifiers changed from: private */
    public String aa;

    /* renamed from: b  reason: collision with root package name */
    private AppDetailTitleView f3520b;
    /* access modifiers changed from: private */
    public String ba;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public AppDetailBannerItemView f3521c;
    /* access modifiers changed from: private */
    public String ca;

    /* renamed from: d  reason: collision with root package name */
    private AppDetailBannerItemView f3522d;
    /* access modifiers changed from: private */
    public String da;
    private AppDetailBannerItemView e;
    /* access modifiers changed from: private */
    public String ea;
    private AppDetailListTitleView f;
    private boolean fa = false;
    private AppDetailListTitleView g;
    private boolean ga = false;
    private AppDetailRightSummaryPointView h;
    /* access modifiers changed from: private */
    public double ha;
    /* access modifiers changed from: private */
    public AppDetailCheckBoxView i;
    /* access modifiers changed from: private */
    public boolean ia;
    private AppDetailBannerItemView j;
    /* access modifiers changed from: private */
    public boolean ja;
    private AppDetailBannerItemView k;
    private boolean ka;
    private AppDetailBannerItemView l;
    /* access modifiers changed from: private */
    public boolean la = true;
    /* access modifiers changed from: private */
    public AppDetailBannerItemView m;
    /* access modifiers changed from: private */
    public boolean ma = false;
    /* access modifiers changed from: private */
    public AppDetailBannerItemView n;
    private boolean na = false;
    private AppDetailBannerItemView o;
    /* access modifiers changed from: private */
    public boolean oa;
    private AppDetailTextBannerView p;
    /* access modifiers changed from: private */
    public boolean pa;
    private AppDetailTextBannerView q;
    /* access modifiers changed from: private */
    public boolean qa;
    private AppDetailCheckBoxView r;
    /* access modifiers changed from: private */
    public boolean ra;
    private AppDetailCheckBoxView s;
    /* access modifiers changed from: private */
    public boolean sa;
    private View t;
    /* access modifiers changed from: private */
    public boolean ta;
    /* access modifiers changed from: private */
    public MenuItem u;
    private boolean ua;
    /* access modifiers changed from: private */
    public MenuItem v;
    /* access modifiers changed from: private */
    public boolean va;
    /* access modifiers changed from: private */
    public MenuItem w;
    /* access modifiers changed from: private */
    public boolean wa;
    /* access modifiers changed from: private */
    public Resources x;
    private boolean xa;
    /* access modifiers changed from: private */
    public ApplicationInfo y;
    private boolean ya;
    private PackageInfo z;
    /* access modifiers changed from: private */
    public int za;

    private static class A implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3523a;

        /* renamed from: b  reason: collision with root package name */
        private int f3524b;

        public A(ApplicationsDetailsActivity applicationsDetailsActivity, int i) {
            this.f3523a = new WeakReference<>(applicationsDetailsActivity);
            this.f3524b = i;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3523a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                applicationsDetailsActivity.a(applicationsDetailsActivity.aa, applicationsDetailsActivity.P);
                String str = null;
                int i2 = this.f3524b;
                if (i2 == 0) {
                    str = "update_app";
                } else if (i2 == 1) {
                    str = "uninstall_app";
                }
                if (str != null) {
                    com.miui.appmanager.a.a.b(str, applicationsDetailsActivity.aa);
                }
            }
        }
    }

    private static class B extends Thread {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3525a;

        /* renamed from: b  reason: collision with root package name */
        private Context f3526b;

        public B(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3525a = new WeakReference<>(applicationsDetailsActivity);
            this.f3526b = applicationsDetailsActivity.getApplicationContext();
        }

        public void run() {
            super.run();
            try {
                ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3525a.get();
                if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing()) {
                    if (!applicationsDetailsActivity.isDestroyed()) {
                        Thread.sleep(500);
                        ApplicationsDetailsActivity applicationsDetailsActivity2 = (ApplicationsDetailsActivity) this.f3525a.get();
                        if (applicationsDetailsActivity2 != null && !applicationsDetailsActivity2.isFinishing() && !applicationsDetailsActivity2.isDestroyed()) {
                            boolean unused = applicationsDetailsActivity2.ia = AppManageUtils.a(this.f3526b, applicationsDetailsActivity2.aa);
                        }
                        applicationsDetailsActivity2.F.post(new d(applicationsDetailsActivity2));
                    }
                }
            } catch (Exception e) {
                Log.e("ApplicationsDetailActivity", "update autostart error", e);
            }
        }
    }

    /* renamed from: com.miui.appmanager.ApplicationsDetailsActivity$a  reason: case insensitive filesystem */
    private static class C0316a extends b.b.c.i.a<Boolean> {

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3527b;

        /* JADX WARNING: type inference failed for: r2v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context, java.lang.Object] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public C0316a(com.miui.appmanager.ApplicationsDetailsActivity r2) {
            /*
                r1 = this;
                r1.<init>(r2)
                java.lang.ref.WeakReference r0 = new java.lang.ref.WeakReference
                r0.<init>(r2)
                r1.f3527b = r0
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.appmanager.ApplicationsDetailsActivity.C0316a.<init>(com.miui.appmanager.ApplicationsDetailsActivity):void");
        }

        public Boolean loadInBackground() {
            ApplicationsDetailsActivity applicationsDetailsActivity;
            if (isLoadInBackgroundCanceled() || (applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3527b.get()) == null || applicationsDetailsActivity.isFinishing() || applicationsDetailsActivity.isDestroyed()) {
                return false;
            }
            b.b.c.j.r.b();
            Context applicationContext = applicationsDetailsActivity.getApplicationContext();
            applicationsDetailsActivity.c(applicationContext);
            long unused = applicationsDetailsActivity.Z = applicationsDetailsActivity.d(applicationContext);
            if (isLoadInBackgroundCanceled()) {
                return false;
            }
            double unused2 = applicationsDetailsActivity.ha = applicationsDetailsActivity.s();
            if (isLoadInBackgroundCanceled()) {
                return false;
            }
            boolean unused3 = applicationsDetailsActivity.ia = AppManageUtils.a(applicationContext, applicationsDetailsActivity.aa);
            applicationsDetailsActivity.b(applicationContext);
            String unused4 = applicationsDetailsActivity.ba = com.miui.powercenter.utils.o.h(applicationContext);
            boolean unused5 = applicationsDetailsActivity.ja = applicationsDetailsActivity.e(applicationContext);
            if (isLoadInBackgroundCanceled()) {
                return false;
            }
            HashMap unused6 = applicationsDetailsActivity.Ea = com.miui.permcenter.n.b(applicationContext, applicationsDetailsActivity.aa);
            boolean unused7 = applicationsDetailsActivity.oa = !ExtraNetwork.isWifiRestrict(applicationContext, applicationsDetailsActivity.da);
            if (applicationsDetailsActivity.va) {
                int unused8 = applicationsDetailsActivity.Ca = AppManageUtils.a(applicationsDetailsActivity.I, applicationsDetailsActivity.Ba, applicationsDetailsActivity.aa);
                int unused9 = applicationsDetailsActivity.Aa = AppManageUtils.a(applicationContext, applicationsDetailsActivity.B, applicationsDetailsActivity.Ba, applicationsDetailsActivity.Ca);
            }
            String unused10 = applicationsDetailsActivity.ea = applicationsDetailsActivity.a(applicationContext);
            if (isLoadInBackgroundCanceled()) {
                return false;
            }
            SimCardHelper instance = SimCardHelper.getInstance(applicationContext);
            boolean unused11 = applicationsDetailsActivity.wa = instance.isDualSimInserted();
            if (applicationsDetailsActivity.wa) {
                boolean unused12 = applicationsDetailsActivity.pa = !ExtraNetwork.isMobileRestrict(applicationContext, applicationsDetailsActivity.da, 0);
                boolean unused13 = applicationsDetailsActivity.qa = !ExtraNetwork.isMobileRestrict(applicationContext, applicationsDetailsActivity.da, 1);
            } else {
                int unused14 = applicationsDetailsActivity.za = instance.getCurrentMobileSlotNum();
                boolean unused15 = applicationsDetailsActivity.pa = !ExtraNetwork.isMobileRestrict(applicationContext, applicationsDetailsActivity.da, applicationsDetailsActivity.za);
            }
            return !isLoadInBackgroundCanceled();
        }
    }

    /* renamed from: com.miui.appmanager.ApplicationsDetailsActivity$b  reason: case insensitive filesystem */
    private static class C0317b implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private Context f3528a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3529b;

        /* renamed from: c  reason: collision with root package name */
        private boolean f3530c;

        public C0317b(ApplicationsDetailsActivity applicationsDetailsActivity, boolean z) {
            this.f3528a = applicationsDetailsActivity.getApplicationContext();
            this.f3529b = new WeakReference<>(applicationsDetailsActivity);
            this.f3530c = z;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3529b.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                AppManageUtils.c(this.f3528a, applicationsDetailsActivity.aa, this.f3530c);
            }
        }
    }

    private static class c implements DialogInterface.OnDismissListener {

        /* renamed from: a  reason: collision with root package name */
        private Context f3531a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3532b;

        public c(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3532b = new WeakReference<>(applicationsDetailsActivity);
            this.f3531a = applicationsDetailsActivity.getApplicationContext();
        }

        public void onDismiss(DialogInterface dialogInterface) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3532b.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                applicationsDetailsActivity.i.setSlideButtonChecked(AppManageUtils.a(this.f3531a, applicationsDetailsActivity.aa));
            }
        }
    }

    private static class d implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3533a;

        public d(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3533a = new WeakReference<>(applicationsDetailsActivity);
        }

        public void run() {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3533a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                applicationsDetailsActivity.i.setSlideButtonChecked(applicationsDetailsActivity.ia);
            }
        }
    }

    private static class e implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3534a;

        public e(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3534a = new WeakReference<>(applicationsDetailsActivity);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3534a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                applicationsDetailsActivity.w.setEnabled(false);
            }
        }
    }

    private static class f implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3535a;

        public f(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3535a = new WeakReference<>(applicationsDetailsActivity);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3535a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                if (i != 0) {
                    if (i != 1) {
                        return;
                    }
                } else if (applicationsDetailsActivity.S > 0 && applicationsDetailsActivity.la) {
                    if (applicationsDetailsActivity.y.manageSpaceActivityName != null) {
                        applicationsDetailsActivity.O();
                        return;
                    } else {
                        applicationsDetailsActivity.a(1, applicationsDetailsActivity.Na);
                        return;
                    }
                }
                applicationsDetailsActivity.a(3, applicationsDetailsActivity.Ma);
            }
        }
    }

    private static class g implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3536a;

        public g(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3536a = new WeakReference<>(applicationsDetailsActivity);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3536a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                applicationsDetailsActivity.n();
            }
        }
    }

    private static class h implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3537a;

        public h(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3537a = new WeakReference<>(applicationsDetailsActivity);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3537a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                applicationsDetailsActivity.A();
            }
        }
    }

    private static class i implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3538a;

        public i(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3538a = new WeakReference<>(applicationsDetailsActivity);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3538a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                applicationsDetailsActivity.J();
            }
        }
    }

    private static class j implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3539a;

        public j(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3539a = new WeakReference<>(applicationsDetailsActivity);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3539a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                applicationsDetailsActivity.b(1);
            }
        }
    }

    private static class k implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3540a;

        public k(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3540a = new WeakReference<>(applicationsDetailsActivity);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3540a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                applicationsDetailsActivity.a(3);
            }
        }
    }

    private static class l extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3541a;

        /* renamed from: b  reason: collision with root package name */
        private int f3542b;

        public l(ApplicationsDetailsActivity applicationsDetailsActivity, int i) {
            this.f3541a = new WeakReference<>(applicationsDetailsActivity);
            this.f3542b = i;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            ApplicationsDetailsActivity applicationsDetailsActivity;
            if (isCancelled() || (applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3541a.get()) == null || applicationsDetailsActivity.isFinishing()) {
                return null;
            }
            applicationsDetailsActivity.D.setApplicationEnabledSetting(applicationsDetailsActivity.aa, this.f3542b, 0);
            applicationsDetailsActivity.F.sendEmptyMessage(1);
            return null;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Void voidR) {
            MenuItem menuItem;
            boolean z;
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3541a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing()) {
                if (AppManageUtils.f3485c.contains(applicationsDetailsActivity.aa)) {
                    menuItem = applicationsDetailsActivity.v;
                    z = true;
                } else {
                    menuItem = applicationsDetailsActivity.v;
                    z = false;
                }
                menuItem.setEnabled(z);
            }
        }
    }

    private static class m implements CompoundButton.OnCheckedChangeListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3543a;

        public m(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3543a = new WeakReference<>(applicationsDetailsActivity);
        }

        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3543a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                AppManageUtils.a(applicationsDetailsActivity.aa, !z);
            }
        }
    }

    private static class n implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3544a;

        public n(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3544a = new WeakReference<>(applicationsDetailsActivity);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3544a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                applicationsDetailsActivity.p();
            }
        }
    }

    private static class o extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private Context f3545a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3546b;

        public o(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3545a = applicationsDetailsActivity.getApplicationContext();
            this.f3546b = new WeakReference<>(applicationsDetailsActivity);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            ApplicationsDetailsActivity applicationsDetailsActivity;
            if (isCancelled() || (applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3546b.get()) == null || applicationsDetailsActivity.isFinishing()) {
                return null;
            }
            applicationsDetailsActivity.c(this.f3545a);
            return null;
        }
    }

    private static class p extends AsyncTask<Void, Void, String> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3547a;

        public p(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3547a = new WeakReference<>(applicationsDetailsActivity);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public String doInBackground(Void... voidArr) {
            ApplicationsDetailsActivity applicationsDetailsActivity;
            String string;
            if (isCancelled() || (applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3547a.get()) == null || applicationsDetailsActivity.isFinishing() || applicationsDetailsActivity.Ha == null) {
                return null;
            }
            Bundle bundle = new Bundle();
            bundle.putString("App", applicationsDetailsActivity.aa);
            Bundle bundle2 = new Bundle();
            try {
                if (applicationsDetailsActivity.Ha.a(bundle, bundle2) != 0) {
                    return null;
                }
                String string2 = bundle2.getString("AppConfigure");
                if ("no_restrict".equals(string2)) {
                    string = applicationsDetailsActivity.x.getString(R.string.app_manager_powerkeeper_no_restrict);
                } else if ("miui_auto".equals(string2)) {
                    string = applicationsDetailsActivity.x.getString(R.string.app_manager_powerkeeper_miui_auto);
                } else if ("restrict_bg".equals(string2)) {
                    string = applicationsDetailsActivity.x.getString(R.string.app_manager_powerkeeper_restrict_bg);
                } else if (!"no_bg".equals(string2)) {
                    return null;
                } else {
                    string = applicationsDetailsActivity.x.getString(R.string.app_manager_powerkeeper_no_bg);
                }
                return string;
            } catch (RemoteException e) {
                Log.e("ApplicationsDetailActivity", "getPowerSaveAppConfigure error", e);
                return null;
            }
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3547a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing()) {
                int i = 0;
                boolean z = !applicationsDetailsActivity.ma && !"disable".equals(applicationsDetailsActivity.ba) && str != null;
                AppDetailBannerItemView R = applicationsDetailsActivity.m;
                if (!z) {
                    i = 8;
                }
                R.setVisibility(i);
                if (str != null) {
                    applicationsDetailsActivity.m.setSummary(str);
                }
            }
        }
    }

    private static class q extends Handler {

        /* renamed from: a  reason: collision with root package name */
        private Context f3548a;

        /* renamed from: b  reason: collision with root package name */
        private final WeakReference<ApplicationsDetailsActivity> f3549b;

        public q(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3548a = applicationsDetailsActivity.getApplicationContext();
            this.f3549b = new WeakReference<>(applicationsDetailsActivity);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3549b.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing()) {
                int i = message.what;
                if (i != 0) {
                    boolean z = true;
                    if (i == 1) {
                        ApplicationInfo applicationInfo = null;
                        try {
                            applicationInfo = AppManageUtils.a(applicationsDetailsActivity.B, applicationsDetailsActivity.aa, 128, applicationsDetailsActivity.P);
                        } catch (Exception e) {
                            Log.e("ApplicationsDetailActivity", "handle message get application info error", e);
                        }
                        if (applicationInfo != null) {
                            ApplicationInfo unused = applicationsDetailsActivity.y = applicationInfo;
                        }
                        if (applicationInfo == null || !applicationInfo.enabled) {
                            z = false;
                        }
                        int unused2 = applicationsDetailsActivity.V = z ? R.string.app_manager_disable_text : R.string.app_manager_enable_text;
                        applicationsDetailsActivity.v.setTitle(applicationsDetailsActivity.V);
                        com.miui.securityscan.i.c.a(this.f3548a, z ? R.string.app_manager_enabled : R.string.app_manager_disabled);
                    } else if (i == 2 || i == 3) {
                        applicationsDetailsActivity.a(message);
                    }
                } else {
                    applicationsDetailsActivity.f3521c.setSummary(ExtraTextUtils.formatFileSize(this.f3548a, applicationsDetailsActivity.R));
                    applicationsDetailsActivity.f(this.f3548a);
                }
            }
        }
    }

    private static class r implements DialogInterface.OnMultiChoiceClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3550a;

        public r(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3550a = new WeakReference<>(applicationsDetailsActivity);
        }

        public void onClick(DialogInterface dialogInterface, int i, boolean z) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3550a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                if (applicationsDetailsActivity.ma) {
                    if (i != 0) {
                        if (i != 1) {
                            return;
                        }
                    }
                    boolean unused = applicationsDetailsActivity.ra = z;
                    return;
                } else if (i != 0) {
                    if (i != 1) {
                        if (i != 2) {
                            return;
                        }
                    }
                    boolean unused2 = applicationsDetailsActivity.ra = z;
                    return;
                } else {
                    boolean unused3 = applicationsDetailsActivity.ta = z;
                    return;
                }
                boolean unused4 = applicationsDetailsActivity.sa = z;
            }
        }
    }

    private static class s implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3551a;

        /* renamed from: b  reason: collision with root package name */
        private Context f3552b;

        public s(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3552b = applicationsDetailsActivity.getApplicationContext();
            this.f3551a = new WeakReference<>(applicationsDetailsActivity);
        }

        /* JADX WARNING: Removed duplicated region for block: B:17:0x0094  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onClick(android.content.DialogInterface r7, int r8) {
            /*
                r6 = this;
                java.lang.ref.WeakReference<com.miui.appmanager.ApplicationsDetailsActivity> r7 = r6.f3551a
                java.lang.Object r7 = r7.get()
                com.miui.appmanager.ApplicationsDetailsActivity r7 = (com.miui.appmanager.ApplicationsDetailsActivity) r7
                if (r7 != 0) goto L_0x000b
                return
            L_0x000b:
                boolean r8 = r7.wa
                if (r8 == 0) goto L_0x005f
                boolean r8 = r7.ra
                boolean r0 = r7.pa
                if (r8 == r0) goto L_0x0039
                boolean r8 = r7.ra
                boolean unused = r7.pa = r8
                com.miui.appmanager.ApplicationsDetailsActivity$t r8 = new com.miui.appmanager.ApplicationsDetailsActivity$t
                android.content.Context r1 = r6.f3552b
                java.lang.String r2 = r7.da
                boolean r0 = r7.pa
                r3 = r0 ^ 1
                r4 = 0
                r5 = 1
                r0 = r8
                r0.<init>(r1, r2, r3, r4, r5)
                r8.start()
            L_0x0039:
                boolean r8 = r7.sa
                boolean r0 = r7.qa
                if (r8 == r0) goto L_0x008a
                boolean r8 = r7.sa
                boolean unused = r7.qa = r8
                com.miui.appmanager.ApplicationsDetailsActivity$t r8 = new com.miui.appmanager.ApplicationsDetailsActivity$t
                android.content.Context r1 = r6.f3552b
                java.lang.String r2 = r7.da
                boolean r0 = r7.qa
                r3 = r0 ^ 1
                r4 = 1
                r5 = 1
                r0 = r8
                r0.<init>(r1, r2, r3, r4, r5)
                goto L_0x0087
            L_0x005f:
                boolean r8 = r7.ra
                boolean r0 = r7.pa
                if (r8 == r0) goto L_0x008a
                boolean r8 = r7.ra
                boolean unused = r7.pa = r8
                com.miui.appmanager.ApplicationsDetailsActivity$t r8 = new com.miui.appmanager.ApplicationsDetailsActivity$t
                android.content.Context r1 = r6.f3552b
                java.lang.String r2 = r7.da
                boolean r0 = r7.pa
                r3 = r0 ^ 1
                int r4 = r7.za
                r5 = 1
                r0 = r8
                r0.<init>(r1, r2, r3, r4, r5)
            L_0x0087:
                r8.start()
            L_0x008a:
                boolean r8 = r7.ta
                boolean r0 = r7.oa
                if (r8 == r0) goto L_0x00b2
                boolean r8 = r7.ta
                boolean unused = r7.oa = r8
                com.miui.appmanager.ApplicationsDetailsActivity$t r8 = new com.miui.appmanager.ApplicationsDetailsActivity$t
                android.content.Context r1 = r6.f3552b
                java.lang.String r2 = r7.da
                boolean r0 = r7.oa
                r3 = r0 ^ 1
                r4 = -1
                r5 = 0
                r0 = r8
                r0.<init>(r1, r2, r3, r4, r5)
                r8.start()
            L_0x00b2:
                com.miui.appmanager.widget.AppDetailBannerItemView r8 = r7.n
                java.lang.String r7 = r7.t()
                r8.setSummary((java.lang.String) r7)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.appmanager.ApplicationsDetailsActivity.s.onClick(android.content.DialogInterface, int):void");
        }
    }

    private static class t extends Thread {

        /* renamed from: a  reason: collision with root package name */
        private Context f3553a;

        /* renamed from: b  reason: collision with root package name */
        private String f3554b;

        /* renamed from: c  reason: collision with root package name */
        private boolean f3555c;

        /* renamed from: d  reason: collision with root package name */
        private int f3556d;
        private boolean e;

        public t(Context context, String str, boolean z, int i, boolean z2) {
            this.f3553a = context;
            this.f3554b = str;
            this.f3555c = z;
            this.f3556d = i;
            this.e = z2;
        }

        public void run() {
            super.run();
            if (this.e) {
                ExtraNetwork.setMobileRestrict(this.f3553a, this.f3554b, this.f3555c, this.f3556d);
            } else {
                ExtraNetwork.setWifiRestrict(this.f3553a, this.f3554b, this.f3555c);
            }
        }
    }

    private static class u extends IPackageDeleteObserver.Stub {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public Context f3557a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3558b;

        public u(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3557a = applicationsDetailsActivity.getApplicationContext();
            this.f3558b = new WeakReference<>(applicationsDetailsActivity);
        }

        public void packageDeleted(String str, int i) {
            ApplicationsDetailsActivity applicationsDetailsActivity;
            if (i == 1 && (applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3558b.get()) != null) {
                applicationsDetailsActivity.F.post(new A(this, applicationsDetailsActivity, str));
            }
        }
    }

    private static class v extends IPackageStatsObserver.Stub {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3559a;

        public v(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3559a = new WeakReference<>(applicationsDetailsActivity);
        }

        public void onGetStatsCompleted(PackageStats packageStats, boolean z) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3559a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                long j = packageStats.externalDataSize + packageStats.externalMediaSize + packageStats.dataSize;
                long j2 = packageStats.externalCodeSize + packageStats.externalObbSize + packageStats.codeSize;
                long j3 = packageStats.externalCacheSize + packageStats.cacheSize;
                if (j != applicationsDetailsActivity.S || j2 != applicationsDetailsActivity.U || j3 != applicationsDetailsActivity.T) {
                    long unused = applicationsDetailsActivity.S = j;
                    long unused2 = applicationsDetailsActivity.U = j2;
                    long unused3 = applicationsDetailsActivity.T = j3;
                    long unused4 = applicationsDetailsActivity.R = applicationsDetailsActivity.S + applicationsDetailsActivity.U + applicationsDetailsActivity.T;
                    applicationsDetailsActivity.F.sendEmptyMessage(0);
                }
            }
        }
    }

    class w {

        /* renamed from: a  reason: collision with root package name */
        int f3560a = 0;

        /* renamed from: b  reason: collision with root package name */
        int f3561b = 0;

        /* renamed from: c  reason: collision with root package name */
        int f3562c = 0;

        w() {
        }
    }

    private static class x implements ServiceConnection {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3564a;

        public x(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3564a = new WeakReference<>(applicationsDetailsActivity);
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3564a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                IPowerKeeper unused = applicationsDetailsActivity.Ha = IPowerKeeper.Stub.a(iBinder);
                applicationsDetailsActivity.v();
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3564a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                IPowerKeeper unused = applicationsDetailsActivity.Ha = null;
            }
        }
    }

    private static class y extends AsyncTask<Void, Void, ActivityManager.TaskDescription> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3565a;

        public y(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3565a = new WeakReference<>(applicationsDetailsActivity);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public ActivityManager.TaskDescription doInBackground(Void... voidArr) {
            ApplicationsDetailsActivity applicationsDetailsActivity;
            ApplicationInfo applicationInfo;
            if (!isCancelled() && (applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3565a.get()) != null && !applicationsDetailsActivity.isFinishing()) {
                try {
                    applicationInfo = AppManageUtils.a(applicationsDetailsActivity.B, "com.android.settings", 0, 0);
                } catch (Exception e) {
                    Log.e("ApplicationsDetailActivity", "setTaskDescrition getApplicationInfo error", e);
                    applicationInfo = null;
                }
                if (applicationInfo != null) {
                    return new ActivityManager.TaskDescription(applicationsDetailsActivity.getString(R.string.app_manager_details_title), b.b.c.j.r.a(applicationInfo.loadIcon(applicationsDetailsActivity.D)));
                }
            }
            return null;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(ActivityManager.TaskDescription taskDescription) {
            ApplicationsDetailsActivity applicationsDetailsActivity;
            super.onPostExecute(taskDescription);
            if (taskDescription != null && (applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3565a.get()) != null && !applicationsDetailsActivity.isFinishing()) {
                applicationsDetailsActivity.setTaskDescription(taskDescription);
            }
        }
    }

    private static class z implements CompoundButton.OnCheckedChangeListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<ApplicationsDetailsActivity> f3566a;

        /* renamed from: b  reason: collision with root package name */
        private Context f3567b;

        public z(ApplicationsDetailsActivity applicationsDetailsActivity) {
            this.f3566a = new WeakReference<>(applicationsDetailsActivity);
            this.f3567b = applicationsDetailsActivity.getApplicationContext();
        }

        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            ApplicationsDetailsActivity applicationsDetailsActivity = (ApplicationsDetailsActivity) this.f3566a.get();
            if (applicationsDetailsActivity != null && !applicationsDetailsActivity.isFinishing() && !applicationsDetailsActivity.isDestroyed()) {
                AppManageUtils.a(this.f3567b, applicationsDetailsActivity.aa, z);
            }
        }
    }

    /* access modifiers changed from: private */
    public void A() {
        if (this.G == null) {
            this.G = new AppManageUtils.ClearUserDataObserver(this.F);
        }
        this.w.setEnabled(false);
        if (!AppManageUtils.a(this.aa, this.P, this.G)) {
            a(2, this.Oa);
        }
        V();
        com.miui.appmanager.a.a.d("clear_data");
    }

    private boolean B() {
        return Build.IS_CM_CUSTOMIZATION && "com.greenpoint.android.mc10086.activity".equals(this.aa);
    }

    private boolean C() {
        return Build.IS_INTERNATIONAL_BUILD && "com.xiaomi.mircs".equals(this.aa);
    }

    private boolean D() {
        return AppManageUtils.a(this.E, this.aa) || AppManageUtils.h.contains(this.aa) || (Build.IS_INTERNATIONAL_BUILD && ("com.facemoji.lite.xiaomi".equals(this.aa) || "com.kikaoem.xiaomi.qisiemoji.inputmethod".equals(this.aa)));
    }

    private boolean E() {
        try {
            PackageInfo packageInfo = this.D.getPackageInfo(Constants.System.ANDROID_PACKAGE_NAME, 64);
            if (this.z != null) {
                return (this.z.signatures != null && packageInfo.signatures[0].equals(this.z.signatures[0])) || this.Fa.contains(this.aa);
            }
            return false;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    private boolean F() {
        int i2;
        boolean z2;
        ApplicationInfo applicationInfo;
        boolean z3 = (this.y.flags & 128) != 0;
        boolean contains = AppManageUtils.f.contains(this.aa);
        try {
            i2 = ((Integer) b.b.o.g.e.a((Class<?>) PackageManager.class, "MATCH_FACTORY_ONLY", Integer.TYPE)).intValue();
        } catch (Exception unused) {
            Log.i("ApplicationsDetailActivity", "reflect error when get factory flag");
            i2 = -1;
        }
        if (i2 != -1) {
            PackageInfo a2 = b.b.o.b.a.a.a(this.aa, i2 | 128 | 64, this.P);
            this.z = a2;
            if (!(a2 == null || (applicationInfo = a2.applicationInfo) == null || applicationInfo.metaData == null)) {
                z2 = a2.applicationInfo.metaData.getBoolean("com.miui.stub.install");
                return !z3 && !contains && !z2;
            }
        }
        z2 = false;
        if (!z3) {
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context, miui.app.Activity] */
    private void G() {
        new AlertDialog.Builder(this).setTitle(getText(R.string.app_manager_force_stop_dlg_title)).setIconAttribute(16843605).setMessage(getText(R.string.app_manager_force_stop_dlg_text)).setPositiveButton(R.string.app_manager_dlg_ok, new n(this)).setNegativeButton(R.string.app_manager_dlg_cancel, (DialogInterface.OnClickListener) null).show();
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context] */
    private boolean H() {
        return this.ma || !b.b.c.j.e.a((Context) this, this.aa, 0);
    }

    private void I() {
        if (!getIntent().getBooleanExtra("enter_from_appmanagermainactivity", false)) {
            this.J = new y(this);
            this.J.execute(new Void[0]);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context] */
    /* access modifiers changed from: private */
    public void J() {
        new AlertDialog.Builder(this).setTitle(R.string.app_manager_disable_dlg_title).setMessage(R.string.app_manager_disable_dlg_text).setPositiveButton(17039370, new k(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context, miui.app.Activity] */
    private void K() {
        boolean[] zArr;
        String[] strArr;
        this.ta = this.oa;
        this.ra = this.pa;
        this.sa = this.qa;
        if (this.ma) {
            if (this.wa) {
                strArr = new String[]{getString(R.string.app_manager_mobile_slot1), getString(R.string.app_manager_mobile_slot2)};
                zArr = new boolean[]{this.ra, this.sa};
            } else {
                strArr = new String[]{getString(R.string.app_manager_net_mobile)};
                zArr = new boolean[]{this.ra};
            }
        } else if (this.wa) {
            String[] strArr2 = {getString(R.string.app_manager_net_wifi), getString(R.string.app_manager_mobile_slot1), getString(R.string.app_manager_mobile_slot2)};
            zArr = new boolean[]{this.ta, this.ra, this.sa};
            strArr = strArr2;
        } else {
            strArr = new String[]{getString(R.string.app_manager_net_wifi), getString(R.string.app_manager_net_mobile)};
            zArr = new boolean[]{this.ta, this.ra};
        }
        new AlertDialog.Builder(this).setTitle(R.string.app_manager_net_control_title).setMultiChoiceItems(strArr, zArr, new r(this)).setPositiveButton(R.string.app_manager_dlg_ok, new s(this)).setNegativeButton(R.string.app_manager_dlg_cancel, (DialogInterface.OnClickListener) null).show();
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context, miui.app.Activity] */
    private void L() {
        Intent intent = new Intent(this, AMAppStorageDetailsActivity.class);
        intent.putExtra("package_name", this.aa);
        intent.putExtra(MijiaAlertModel.KEY_UID, this.Q);
        intent.putExtra("size", this.R);
        startActivity(intent);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context] */
    private void M() {
        Intent intent = new Intent("android.intent.action.MANAGE_APP_PERMISSIONS");
        intent.putExtra("android.intent.extra.PACKAGE_NAME", this.aa);
        b.b.c.j.g.b((Context) this, intent, new UserHandle(this.P));
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context, miui.app.Activity] */
    private void N() {
        Intent intent = new Intent(this, AutoStartDetailManagementActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("pkg_label", this.ca);
        bundle.putString("pkg_name", this.aa);
        com.miui.permcenter.a a2 = com.miui.permcenter.n.a((Context) this, (long) PermissionManager.PERM_ID_AUTOSTART, this.aa);
        Integer num = 1;
        if (a2 != null) {
            num = a2.f().get(Long.valueOf(PermissionManager.PERM_ID_AUTOSTART));
        }
        bundle.putInt("action", num.intValue());
        List<String> a3 = com.miui.permcenter.s.a(this);
        if (a3 == null || a3.size() <= 0 || !a3.contains(this.aa)) {
            bundle.putBoolean("white_list", false);
        } else {
            bundle.putBoolean("white_list", true);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context] */
    /* access modifiers changed from: private */
    public void O() {
        AppManageUtils.a(this, this.aa, this.y.manageSpaceActivityName, this.P, 10022);
    }

    private void P() {
        Intent intent = new Intent(Constants.App.ACTION_NETWORK_ASSISTANT_APP_DETAIL);
        Bundle bundle = new Bundle();
        bundle.putString("package_name", this.da);
        bundle.putInt(DataUsageConstants.BUNDLE_TITLE_TYPE, 2);
        bundle.putInt(DataUsageConstants.BUNDLE_SORT_TYPE, 0);
        intent.putExtras(bundle);
        intent.putExtra("from_appmanager", true);
        intent.putExtra(Sim.SIM_SLOT_NUM_TAG, Sim.getCurrentActiveSlotNum());
        startActivity(intent);
    }

    private void Q() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClassName("com.android.settings", "com.android.settings.Settings$NotificationFilterActivity");
        intent.putExtra("appName", this.ca);
        intent.putExtra("packageName", this.aa);
        intent.putExtra(UserConfigure.Columns.USER_ID, this.P);
        intent.putExtra(":miui:starting_window_label", this.ca);
        startActivity(intent);
    }

    private void R() {
        Intent intent = new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES");
        intent.setData(Uri.parse("package:".concat(this.aa)));
        startActivity(intent);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context, miui.app.Activity] */
    private void S() {
        Intent intent = new Intent(this, PowerDetailActivity.class);
        BatteryData r2 = r();
        if (r2 != null) {
            intent.putExtras(a((Context) this, r2));
        }
        startActivity(intent);
    }

    private void T() {
        try {
            Intent intent = new Intent("miui.intent.action.HIDDEN_APPS_CONFIG_ACTIVITY");
            intent.setClassName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity");
            intent.putExtra("package_name", this.aa);
            intent.putExtra("package_label", this.ca);
            intent.putExtra("pkg_name", this.aa);
            intent.putExtra("pkg_label", this.ca);
            startActivity(intent);
        } catch (ActivityNotFoundException e2) {
            Log.e("ApplicationsDetailActivity", "HiddenAppsConfigActivity not found", e2);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x008c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void U() {
        /*
            r6 = this;
            boolean r0 = r6.X
            r1 = 0
            if (r0 == 0) goto L_0x000a
            r6.b((int) r1)
            goto L_0x008f
        L_0x000a:
            android.content.pm.ApplicationInfo r0 = r6.y
            r2 = 0
            if (r0 == 0) goto L_0x0040
            android.os.Bundle r0 = r0.metaData
            if (r0 == 0) goto L_0x0040
            android.content.pm.ApplicationInfo r0 = r6.y
            android.os.Bundle r0 = r0.metaData
            java.lang.String r3 = "app_description_title"
            int r0 = r0.getInt(r3)
            android.content.pm.ApplicationInfo r3 = r6.y
            android.os.Bundle r3 = r3.metaData
            java.lang.String r4 = "app_description_content"
            int r3 = r3.getInt(r4)
            if (r0 == 0) goto L_0x0040
            if (r3 == 0) goto L_0x0040
            android.content.pm.PackageManager r2 = r6.D
            java.lang.String r4 = r6.aa
            android.content.pm.ApplicationInfo r5 = r6.y
            java.lang.CharSequence r2 = r2.getText(r4, r0, r5)
            android.content.pm.PackageManager r0 = r6.D
            java.lang.String r4 = r6.aa
            android.content.pm.ApplicationInfo r5 = r6.y
            java.lang.CharSequence r0 = r0.getText(r4, r3, r5)
            goto L_0x0041
        L_0x0040:
            r0 = r2
        L_0x0041:
            android.content.pm.ApplicationInfo r3 = r6.y
            boolean r3 = r3.enabled
            if (r3 == 0) goto L_0x008c
            boolean r1 = r6.ma
            if (r1 == 0) goto L_0x0068
            java.lang.String r1 = r6.aa
            boolean r1 = b.b.f.a.a((java.lang.String) r1)
            if (r1 != 0) goto L_0x0068
            boolean r1 = android.text.TextUtils.isEmpty(r2)
            if (r1 != 0) goto L_0x0064
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x0060
            goto L_0x0064
        L_0x0060:
            r6.a((java.lang.CharSequence) r2, (java.lang.CharSequence) r0)
            goto L_0x008f
        L_0x0064:
            r6.J()
            goto L_0x008f
        L_0x0068:
            boolean r1 = r6.H()
            r3 = 1
            if (r1 != 0) goto L_0x0088
            boolean r1 = android.text.TextUtils.isEmpty(r2)
            if (r1 != 0) goto L_0x0088
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x007c
            goto L_0x0088
        L_0x007c:
            java.lang.String r1 = r2.toString()
            java.lang.String r0 = r0.toString()
            r6.b((java.lang.CharSequence) r1, (java.lang.CharSequence) r0)
            goto L_0x008f
        L_0x0088:
            r6.b((int) r3)
            goto L_0x008f
        L_0x008c:
            r6.a((int) r1)
        L_0x008f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appmanager.ApplicationsDetailsActivity.U():void");
    }

    private void V() {
        new B(this).start();
    }

    private Bundle a(Context context, BatteryData batteryData) {
        double[] dArr;
        int[] iArr;
        double[] dArr2;
        Bundle bundle = new Bundle();
        bundle.putString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, b.a(context, batteryData));
        bundle.putFloat("percent", (float) ((batteryData.getValue() / com.miui.powercenter.legacypowerrank.i.c()) * 100.0d));
        bundle.putString("iconPackage", this.aa);
        bundle.putInt("iconId", b.a(batteryData));
        if (batteryData.getUid() >= 0) {
            bundle.putInt(MijiaAlertModel.KEY_UID, this.Q);
        }
        bundle.putInt("drainType", batteryData.drainType);
        bundle.putBoolean("showMenus", false);
        int i2 = batteryData.drainType;
        if (i2 == 1) {
            iArr = new int[]{R.string.usage_type_on_time, R.string.usage_type_no_coverage};
            dArr = new double[]{(double) batteryData.usageTime, batteryData.noCoveragePercent};
        } else if (i2 != 6) {
            if (i2 == 3) {
                iArr = new int[]{R.string.usage_type_wifi_running, R.string.usage_type_cpu, R.string.usage_type_cpu_foreground, R.string.usage_type_wake_lock, R.string.usage_type_data_send, R.string.usage_type_data_recv};
                dArr2 = new double[]{(double) batteryData.usageTime, (double) batteryData.cpuTime, (double) batteryData.cpuFgTime, (double) batteryData.wakeLockTime, (double) batteryData.mobileTxBytes, (double) batteryData.mobileRxBytes};
            } else if (i2 != 4) {
                iArr = new int[]{R.string.usage_type_on_time};
                dArr = new double[]{(double) batteryData.usageTime};
            } else {
                iArr = new int[]{R.string.usage_type_on_time, R.string.usage_type_cpu, R.string.usage_type_cpu_foreground, R.string.usage_type_wake_lock, R.string.usage_type_data_send, R.string.usage_type_data_recv};
                dArr2 = new double[]{(double) batteryData.usageTime, (double) batteryData.cpuTime, (double) batteryData.cpuFgTime, (double) batteryData.wakeLockTime, (double) batteryData.mobileTxBytes, (double) batteryData.mobileRxBytes};
            }
            dArr = dArr2;
        } else {
            int[] iArr2 = {R.string.usage_type_cpu, R.string.usage_type_cpu_foreground, R.string.usage_type_wake_lock, R.string.usage_type_gps, R.string.usage_type_wifi_running, R.string.usage_type_data_send, R.string.usage_type_data_recv, R.string.usage_type_audio, R.string.usage_type_video};
            dArr = new double[]{(double) batteryData.cpuTime, (double) batteryData.cpuFgTime, (double) batteryData.wakeLockTime, (double) batteryData.gpsTime, (double) batteryData.wifiRunningTime, (double) batteryData.mobileTxBytes, (double) batteryData.mobileRxBytes, 0.0d, 0.0d};
            iArr = iArr2;
        }
        bundle.putIntArray("types", iArr);
        bundle.putDoubleArray("values", dArr);
        return bundle;
    }

    /* access modifiers changed from: private */
    public String a(Context context) {
        int b2;
        synchronized (f3519a) {
            b2 = AppManageUtils.b(context, this.aa, true);
        }
        return getString(b2 == 3 ? R.string.app_manager_not_allow : R.string.app_manager_allow);
    }

    /* access modifiers changed from: private */
    public void a(int i2) {
        String str;
        String str2;
        this.K = new l(this, i2);
        this.K.execute(new Void[0]);
        if (i2 == 3) {
            str = this.aa;
            str2 = "disable_app";
        } else {
            str = this.aa;
            str2 = "enable_app";
        }
        com.miui.appmanager.a.a.b(str2, str);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context] */
    /* access modifiers changed from: private */
    public void a(int i2, DialogInterface.OnClickListener onClickListener) {
        AppManageUtils.a((Context) this, i2, onClickListener);
    }

    /* access modifiers changed from: private */
    public void a(Message message) {
        int i2 = message.arg1;
        int i3 = message.what;
        if (i2 == 1) {
            int i4 = 0;
            if (Build.VERSION.SDK_INT > 25) {
                if (i3 == 3) {
                    this.S = 0;
                } else {
                    this.S -= this.T;
                }
                this.T = 0;
                this.R = this.S + this.U;
                this.F.sendEmptyMessage(0);
            } else {
                u();
            }
            this.fa = q();
            this.u.setEnabled(this.fa);
            AppDetailRightSummaryPointView appDetailRightSummaryPointView = this.h;
            if (this.fa) {
                i4 = R.string.app_behavior_now_running;
            }
            appDetailRightSummaryPointView.setSummary(i4);
        } else if (i3 == 3) {
            this.w.setEnabled(true);
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context] */
    private void a(CharSequence charSequence, CharSequence charSequence2) {
        new AlertDialog.Builder(this).setTitle(charSequence).setMessage(charSequence2).setPositiveButton(R.string.app_manager_disable_text, new i(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    public void a(String str, int i2) {
        if (b.b.f.a.a(str)) {
            AppManageUtils.a(this.B, str, this.z.versionCode, (IPackageDeleteObserver) this.Ia, i2, 4);
        } else if (this.ua) {
            AppManageUtils.a(this.B, str, this.z.versionCode, (IPackageDeleteObserver) this.Ia, i2, 0);
        } else {
            AppManageUtils.a(this.B, str, this.z.versionCode, (IPackageDeleteObserver) this.Ia, i2, 0);
            if (b.b.o.b.a.a.a(this.B, str)) {
                AppManageUtils.a(this.B, str, this.z.versionCode, (IPackageDeleteObserver) null, 999, 0);
            }
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context, miui.app.Activity] */
    private void a(boolean z2) {
        AlertDialog create = new AlertDialog.Builder(this).setTitle(R.string.app_manager_as_dlg_title).setMessage(R.string.app_manager_as_dlg_msg).setPositiveButton(17039370, new C0317b(this, z2)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
        create.setOnDismissListener(new c(this));
        if (!isFinishing()) {
            create.show();
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context] */
    /* access modifiers changed from: private */
    public void b(int i2) {
        int i3 = R.string.uninstall_app_dialog_title;
        int i4 = R.string.uninstall_app_dialog_msg;
        if (i2 == 0) {
            i3 = R.string.app_manager_factory_reset_dlg_title;
            i4 = R.string.app_manager_factory_reset_dlg_msg;
        } else if (i2 == 1) {
            if (this.ua) {
                i3 = R.string.app_manager_uninstall_xspace_app_dlg_title;
                i4 = R.string.app_manager_uninstall_xspace_app_dlg_msg;
            } else if (b.b.o.b.a.a.a(this.B, this.aa)) {
                i4 = R.string.app_manager_uninstall_with_xspace_app_dlg_msg;
            }
            if (!H()) {
                i3 = R.string.app_manager_uninstall_protected_dlg_title;
                i4 = R.string.app_manager_uninstall_protected_dlg_msg;
            }
        }
        new AlertDialog.Builder(this).setTitle(i3).setMessage(i4).setPositiveButton(17039370, new A(this, i2)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    public void b(Context context) {
        HashMap<Long, Integer> b2;
        this.Y = new w();
        if (!this.aa.equals("com.miui.klo.bugreport") && (b2 = com.miui.permcenter.n.b(context, this.aa)) != null) {
            Object[] array = b2.keySet().toArray();
            for (Object obj : array) {
                Integer num = b2.get((Long) obj);
                if (num.intValue() == 3) {
                    this.Y.f3560a++;
                } else if (num.intValue() == 1) {
                    this.Y.f3562c++;
                } else if (num.intValue() == 2) {
                    this.Y.f3561b++;
                }
            }
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context] */
    private void b(CharSequence charSequence, CharSequence charSequence2) {
        new AlertDialog.Builder(this).setTitle(charSequence).setMessage(charSequence2).setPositiveButton(R.string.app_manager_unstall_application, new j(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    private void b(boolean z2) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR_PRIVATE");
        intent.putExtra("extra_pkgname", this.aa);
        intent.putExtra("extra_remove_other_settings", z2);
        startActivity(intent);
    }

    /* access modifiers changed from: private */
    public void c(Context context) {
        ApplicationInfo applicationInfo = this.y;
        if (applicationInfo != null) {
            if (Build.VERSION.SDK_INT > 25) {
                F a2 = AppManageUtils.a(context, applicationInfo, this.Q);
                long j2 = a2.f3578c;
                long j3 = a2.f3577b;
                long j4 = j2 + j3;
                if (j4 != this.R || j3 != this.S) {
                    this.R = j4;
                    this.S = a2.f3577b;
                    this.T = a2.f3576a;
                    this.U = a2.f3578c;
                    this.F.sendEmptyMessage(0);
                    return;
                }
                return;
            }
            AppManageUtils.a(this.D, this.aa, this.P, (IPackageStatsObserver) this.Ga);
        }
    }

    /* access modifiers changed from: private */
    public long d(Context context) {
        return new StatisticAppTraffic(context, b.b.c.j.i.a(context)).buildMobileDataUsage(this.Q, false).get(3)[0].getTotal();
    }

    /* access modifiers changed from: private */
    public boolean e(Context context) {
        ArrayList arrayList = new ArrayList();
        this.D.getPreferredActivities(new ArrayList(), arrayList, this.aa);
        int myUserId = UserHandle.myUserId();
        return (arrayList.size() > 0 || AppManageUtils.b(this.C, this.aa, myUserId) || TextUtils.equals(this.aa, AppManageUtils.a(this.D, myUserId))) || AppManageUtils.a(this.N, this.aa);
    }

    /* access modifiers changed from: private */
    public void f(Context context) {
        int i2;
        MenuItem menuItem;
        if (this.w != null) {
            long j2 = this.R;
            if (j2 == -1 || j2 == -2 || ((this.S <= 0 || !this.la) && this.T <= 0)) {
                this.w.setEnabled(false);
            } else {
                this.w.setEnabled(true);
                if (this.S > 0 && this.la && this.T > 0) {
                    menuItem = this.w;
                    i2 = R.string.app_manager_menu_clear_data;
                } else if (this.S <= 0 || !this.la) {
                    if (this.T > 0) {
                        menuItem = this.w;
                        i2 = R.string.app_manager_clear_cache;
                    }
                } else if (this.y.manageSpaceActivityName != null) {
                    menuItem = this.w;
                    i2 = R.string.app_manager_manage_space;
                } else {
                    menuItem = this.w;
                    i2 = R.string.app_manager_clear_all_data;
                }
                menuItem.setTitle(i2);
            }
            if (b.b.c.j.e.c(context, this.aa, this.P)) {
                Log.d("Enterprise", "Package " + this.aa + " should keep alive");
                this.w.setEnabled(false);
                this.f3521c.setViewEnable(false);
            }
        }
    }

    /* JADX WARNING: type inference failed for: r9v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context, android.view.View$OnClickListener, miui.app.Activity, android.widget.CompoundButton$OnCheckedChangeListener, android.content.ContextWrapper] */
    private void initData() {
        int i2;
        AppDetailBannerItemView appDetailBannerItemView;
        boolean z2;
        this.X = F();
        this.V = R.string.app_manager_unstall_application;
        this.W = R.drawable.app_manager_delete_icon;
        this.Ja = new x(this);
        Intent intent = new Intent();
        intent.setClassName("com.miui.powerkeeper", "com.miui.powerkeeper.PowerKeeperBackgroundService");
        b.b.c.j.g.a((Context) this, intent, this.Ja, 1, UserHandle.OWNER);
        this.x = getResources();
        this.D = getPackageManager();
        this.E = (DevicePolicyManager) getSystemService("device_policy");
        this.A = (ActivityManager) getSystemService("activity");
        this.N = AppWidgetManager.getInstance(this);
        this.f3520b = (AppDetailTitleView) findViewById(R.id.am_detail_title);
        this.f3520b.setAppLabel(this.ca);
        this.z = b.b.o.b.a.a.a(this.aa, PsExtractor.AUDIO_STREAM, this.P);
        if (this.z != null) {
            this.f3520b.setAppVersion(this.x.getString(R.string.app_manager_app_version_name) + this.z.versionName);
        } else {
            finish();
        }
        this.f3521c = (AppDetailBannerItemView) findViewById(R.id.am_storage_view);
        this.f3521c.setTitle(R.string.app_manager_details_storage_title);
        this.f3521c.setSummary(ExtraTextUtils.formatFileSize(this, this.R));
        this.f3522d = (AppDetailBannerItemView) findViewById(R.id.am_traffic_view);
        this.f3522d.setTitle(R.string.app_manager_details_traffic_title);
        this.e = (AppDetailBannerItemView) findViewById(R.id.am_power_view);
        this.e.setTitle(R.string.app_manager_details_electric_title);
        this.f = (AppDetailListTitleView) findViewById(R.id.am_detail_perm_title);
        this.f.setTitle(R.string.app_manager_card_permission_title);
        this.g = (AppDetailListTitleView) findViewById(R.id.am_detail_default_title);
        this.g.setTitle(R.string.app_manager_advanced_setting);
        this.h = (AppDetailRightSummaryPointView) findViewById(R.id.am_detail_behavior);
        this.h.setTitle(R.string.app_behavior_door);
        this.i = (AppDetailCheckBoxView) findViewById(R.id.am_detail_as);
        this.i.setTitle(R.string.app_manager_permission_startself_title);
        this.i.setVisibility((!(this.ma ? AppManageUtils.c(this.aa) : this.Q != 1000) || B()) ? 8 : 0);
        this.i.setSummaryVisible(false);
        this.j = (AppDetailBannerItemView) findViewById(R.id.am_detail_perm);
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            appDetailBannerItemView = this.j;
            i2 = R.string.app_manager_globel_other_perm_title;
        } else {
            appDetailBannerItemView = this.j;
            i2 = R.string.app_manager_permission_manager_title;
        }
        appDetailBannerItemView.setTitle(i2);
        boolean z3 = ((this.ma || this.na) && !RequiredPermissionsUtil.isAdaptedRequiredPermissions(this.z)) || C() || RequiredPermissionsUtil.isAdaptedRequiredPermissionsOnData(this.z);
        this.j.setTag(R.id.tag_remove_other_settings, Boolean.valueOf(z3));
        if (com.miui.permcenter.privacymanager.b.c.a(this)) {
            this.j.setVisibility(8);
        } else {
            this.j.setVisibility(z3 ? 8 : 0);
        }
        this.h.setVisibility((!com.miui.permcenter.privacymanager.behaviorrecord.o.a((Context) this) || com.miui.permcenter.privacymanager.behaviorrecord.o.a((Context) this, this.aa, this.P)) ? 8 : 0);
        this.k = (AppDetailBannerItemView) findViewById(R.id.am_hydrid_perm);
        Intent intent2 = new Intent(HybirdServiceUtil.ACTION_HYBIRD_PERMISSIONS);
        if (HybirdServiceUtil.HYBIRD_PACKAGE_NAME.equals(this.aa) && b.b.c.j.x.b((Context) this, intent2)) {
            this.k.setVisibility(0);
            this.k.setSummaryVisible(false);
            this.k.setTitle(R.string.manage_hybrid_permissions);
        }
        this.l = (AppDetailBannerItemView) findViewById(R.id.am_detail_notify);
        this.l.setTitle(R.string.app_manager_permission_notify_title);
        this.m = (AppDetailBannerItemView) findViewById(R.id.am_detail_keeper);
        this.m.setVisibility(this.ma ? 8 : 0);
        this.m.setTitle(R.string.app_manager_permission_power_saving_title);
        this.n = (AppDetailBannerItemView) findViewById(R.id.am_detail_net);
        this.xa = PreSetGroup.isPreFirewallWhiteListPackage(this.aa);
        this.ka = this.D.checkPermission("android.permission.INTERNET", this.aa) == 0;
        if (!this.ka || this.xa) {
            this.n.setVisibility(8);
        } else {
            this.n.setVisibility(0);
            this.n.setTitle(R.string.app_manager_net_control_title);
        }
        this.p = (AppDetailTextBannerView) findViewById(R.id.am_detail_default);
        this.p.setTitle(R.string.app_manager_default_open_title);
        if (!miui.os.Build.IS_INTERNATIONAL_BUILD && "com.android.browser".equals(this.aa)) {
            this.p.setVisibility(8);
        }
        this.q = (AppDetailTextBannerView) findViewById(R.id.am_global_perm);
        if (miui.os.Build.IS_INTERNATIONAL_BUILD && !"com.xiaomi.mircs".equals(this.aa)) {
            this.q.setVisibility(0);
            this.q.setTitle(R.string.app_manager_globel_perm_title);
            this.q.setSummary((int) R.string.app_manager_globel_perm_summary);
            this.q.setOnClickListener(this);
        }
        this.t = findViewById(R.id.am_empty_view);
        this.f3521c.setOnClickListener(this);
        this.f3522d.setOnClickListener(this);
        this.e.setOnClickListener(this);
        this.h.setOnClickListener(this);
        this.i.setOnClickListener(this);
        this.i.setSlideButtonOnCheckedListener(this);
        this.j.setOnClickListener(this);
        this.k.setOnClickListener(this);
        this.l.setOnClickListener(this);
        this.m.setOnClickListener(this);
        this.p.setOnClickListener(this);
        this.n.setOnClickListener(this);
        String[] stringArray = this.x.getStringArray(R.array.always_enabled_app_list);
        this.Fa = new HashSet<>(stringArray.length);
        for (String add : stringArray) {
            this.Fa.add(add);
        }
        this.Aa = R.string.app_manager_not_allow;
        this.o = (AppDetailBannerItemView) findViewById(R.id.am_potentiaL_app);
        if (Build.VERSION.SDK_INT > 25) {
            this.I = (AppOpsManager) getSystemService("appops");
            this.Ba = this.ua ? AppManageUtils.a(this.Q) : this.Q;
            this.Ca = AppManageUtils.a(this.I, this.Ba, this.aa);
            this.va = AppManageUtils.c(this.B, this.aa, this.Ca) && !AppManageUtils.e.contains(this.aa) && this.Q > 10000;
            if (this.va) {
                this.o.setVisibility(0);
                this.o.setTitle(R.string.app_manager_install_other_app);
                this.o.setOnClickListener(this);
            }
        }
        String concat = "pkg_icon://".concat(this.aa);
        if (UserHandle.getUserId(this.Q) == 999) {
            concat = "pkg_icon_xspace://".concat(this.aa);
        }
        this.f3520b.setAppIcon(concat);
        if (AppManageUtils.a((Context) this, this.aa, this.P)) {
            Log.d("Enterprise", "Package " + this.aa + "is allowed auto start");
            this.i.setViewEnable(false);
        }
        if (b.b.o.c.a.a(this, this.aa, this.P)) {
            Log.d("Enterprise", "Net config is restricted for package" + this.aa);
            this.n.setViewEnable(false);
        }
        try {
            z2 = AppManageUtils.a((ContextWrapper) this);
        } catch (Exception unused) {
            Log.e("ApplicationsDetailActivity", "hasNavigationBar error");
            z2 = false;
        }
        boolean z4 = AppManageUtils.b(this.aa) != 1;
        this.r = (AppDetailCheckBoxView) findViewById(R.id.am_full_screen);
        this.ya = !miui.os.Build.IS_TABLET && z2 && z4;
        if (this.ya) {
            this.r.setTitle(R.string.app_manager_full_screen_title);
            this.r.setSummary(R.string.app_manager_full_screen_summary);
            this.r.setOnClickListener(this);
            this.Ka = new m(this);
            this.r.setSlideButtonOnCheckedListener(this.Ka);
        } else {
            this.r.setVisibility(8);
        }
        this.s = (AppDetailCheckBoxView) findViewById(R.id.am_thumbnail_blur);
        if (AppManageUtils.f((Context) this)) {
            this.s.setVisibility(0);
            this.s.setTitle(R.string.app_manager_thumbnail_blur_title);
            this.s.setSummary(R.string.app_manager_thumbnail_blur_summary);
            this.s.setOnClickListener(this);
            this.La = new z(this);
            this.s.setSlideButtonOnCheckedListener(this.La);
        } else {
            this.s.setVisibility(8);
        }
        try {
            this.C = b.b.o.g.e.a(Class.forName("android.hardware.usb.IUsbManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, (IBinder) b.b.o.g.e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "usb"));
        } catch (Exception e2) {
            Log.e("ApplicationsDetailActivity", "reflect error while get usb manager service", e2);
        }
        this.Ga = new v(this);
        this.Pa = new f(this);
        this.Ma = new g(this);
        this.Na = new h(this);
        this.Oa = new e(this);
    }

    private void l() {
        y yVar = this.J;
        if (yVar != null) {
            yVar.cancel(true);
        }
        l lVar = this.K;
        if (lVar != null) {
            lVar.cancel(true);
        }
        p pVar = this.L;
        if (pVar != null) {
            pVar.cancel(true);
        }
        o oVar = this.M;
        if (oVar != null) {
            oVar.cancel(true);
        }
        C0316a aVar = this.O;
        if (aVar != null) {
            aVar.cancelLoad();
        }
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context] */
    private void m() {
        if (this.u != null && this.v != null) {
            this.fa = q();
            this.u.setEnabled(this.fa);
            z();
            this.h.setSummary(this.fa ? R.string.app_behavior_now_running : 0);
            if (b.b.c.j.e.c(this, this.aa, this.P)) {
                Log.d("Enterprise", "Package " + this.aa + " should keep alive");
                this.u.setEnabled(false);
                this.w.setEnabled(false);
            }
            if (b.b.c.j.e.b(this, this.aa, this.P)) {
                Log.d("Enterprise", "Package " + this.aa + " is protected from delete");
                this.v.setEnabled(false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void n() {
        if (this.H == null) {
            this.H = new AppManageUtils.ClearCacheObserver(this.F);
        }
        AppManageUtils.a(this.B, this.aa, this.P, this.H);
        com.miui.appmanager.a.a.d("clear_cache");
    }

    private void o() {
        int myUserId = UserHandle.myUserId();
        try {
            this.D.clearPackagePreferredActivities(this.aa);
            if (TextUtils.equals(this.aa, AppManageUtils.a(this.D, myUserId))) {
                AppManageUtils.a(this.D, (String) null, myUserId);
            }
            AppManageUtils.a(this.C, this.aa, myUserId);
        } catch (Exception e2) {
            Log.e("ApplicationsDetailActivity", "mUsbManager.clearDefaults", e2);
        }
        AppManageUtils.a(this.N, this.aa, false);
        com.miui.securityscan.i.c.a(getApplicationContext(), (int) R.string.app_manager_default_cleared);
        this.p.setSummary((int) R.string.app_manager_default_close_summary);
        this.ja = false;
    }

    /* access modifiers changed from: private */
    public void p() {
        AppManageUtils.a(this.A, this.aa, this.P);
        int i2 = 0;
        this.fa = false;
        this.u.setEnabled(false);
        AppDetailRightSummaryPointView appDetailRightSummaryPointView = this.h;
        if (this.fa) {
            i2 = R.string.app_behavior_now_running;
        }
        appDetailRightSummaryPointView.setSummary(i2);
    }

    private boolean q() {
        Iterator<ActivityManager.RunningAppProcessInfo> it = this.A.getRunningAppProcesses().iterator();
        while (true) {
            int i2 = 0;
            if (!it.hasNext()) {
                return false;
            }
            ActivityManager.RunningAppProcessInfo next = it.next();
            if (next.uid == this.Q && next.pkgList != null) {
                while (true) {
                    String[] strArr = next.pkgList;
                    if (i2 >= strArr.length) {
                        continue;
                        break;
                    } else if (strArr[i2].equals(this.aa)) {
                        return true;
                    } else {
                        i2++;
                    }
                }
            }
        }
    }

    private BatteryData r() {
        for (BatteryData next : com.miui.powercenter.legacypowerrank.i.a()) {
            if (next.getPackageName() != null && next.getPackageName().equals(this.aa) && UserHandle.getUserId(next.uid) == this.P) {
                return next;
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public double s() {
        com.miui.powercenter.legacypowerrank.i.d();
        for (BatteryData next : com.miui.powercenter.legacypowerrank.i.a()) {
            if (next.getPackageName() != null && next.getPackageName().equals(this.aa) && UserHandle.getUserId(next.uid) == this.P) {
                double value = (next.getValue() / com.miui.powercenter.legacypowerrank.i.c()) * 100.0d;
                if (value < 0.5d) {
                    return 0.0d;
                }
                return value;
            }
        }
        return 0.0d;
    }

    /* access modifiers changed from: private */
    public String t() {
        int i2;
        int i3;
        if (!this.pa && !this.qa) {
            if (this.ma) {
                i3 = R.string.app_manager_system_mobile_disable;
            } else if (!this.oa) {
                i3 = R.string.app_manager_disable;
            }
            return getString(i3);
        }
        StringBuilder sb = new StringBuilder();
        boolean z2 = false;
        if (!this.ma && this.oa) {
            sb.append(getString(R.string.app_manager_net_wifi));
            z2 = true;
        }
        if (this.wa) {
            if (this.pa) {
                if (z2) {
                    sb.append(getString(R.string.app_manger_des_separator));
                }
                sb.append(getString(R.string.app_manager_mobile_slot1));
                z2 = true;
            }
            if (this.qa) {
                if (z2) {
                    sb.append(getString(R.string.app_manger_des_separator));
                }
                i2 = R.string.app_manager_mobile_slot2;
            }
            return sb.toString();
        }
        if (this.pa) {
            if (z2) {
                sb.append(getString(R.string.app_manger_des_separator));
            }
            i2 = R.string.app_manager_net_mobile;
        }
        return sb.toString();
        sb.append(getString(i2));
        return sb.toString();
    }

    private void u() {
        this.M = new o(this);
        this.M.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* access modifiers changed from: private */
    public void v() {
        this.L = new p(this);
        this.L.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private int w() {
        int i2 = this.Da;
        return i2 > 8 ? R.drawable.action_button_stop_svg : i2 > 7 ? R.drawable.action_button_stop : R.drawable.action_button_stop_9;
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context, miui.app.Activity] */
    private void x() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(R.drawable.app_manager_info_icon);
            imageView.setContentDescription(getString(R.string.app_manager_app_info_label));
            imageView.setOnClickListener(new z(this));
            if (!C0195b.a(actionBar, (View) imageView)) {
                actionBar.setDisplayOptions(16, 16);
                actionBar.setCustomView(imageView, new ActionBar.LayoutParams(-2, -2, 8388629));
            }
        }
    }

    private void y() {
        ApplicationInfo applicationInfo = this.y;
        if (applicationInfo.manageSpaceActivityName == null && ((applicationInfo.flags & 65) == 1 || AppManageUtils.a(this.E, this.aa))) {
            this.la = false;
        }
        this.w.setTitle(R.string.app_manager_menu_clear_data);
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context] */
    private void z() {
        boolean z2 = false;
        if (D()) {
            this.v.setVisible(false);
            return;
        }
        if (this.X) {
            this.W = w();
            this.V = R.string.app_manager_factory_reset;
        } else {
            boolean z3 = this.ma;
            int i2 = R.string.app_manager_enable_text;
            if (!z3 || b.b.f.a.a(this.aa)) {
                if (this.y.enabled) {
                    this.W = R.drawable.app_manager_delete_icon;
                    this.V = R.string.app_manager_unstall_application;
                } else {
                    this.W = w();
                    this.V = R.string.app_manager_enable_text;
                }
                if (b.b.f.a.a(this.aa)) {
                    z2 = b.b.f.a.b(this);
                    this.v.setTitle(this.V);
                    this.v.setIcon(this.W);
                    this.v.setEnabled(z2);
                }
            } else {
                this.W = w();
                if (AppManageUtils.f3485c.contains(this.aa)) {
                    if (this.y.enabled) {
                        i2 = R.string.app_manager_disable_text;
                    }
                    this.V = i2;
                } else {
                    try {
                        Intent intent = new Intent("android.intent.action.MAIN");
                        intent.addCategory("android.intent.category.HOME");
                        intent.setPackage(this.aa);
                        List<ResolveInfo> queryIntentActivities = this.D.queryIntentActivities(intent, 0);
                        if ((queryIntentActivities == null || queryIntentActivities.size() <= 0) && !E()) {
                            if (!this.y.enabled) {
                                this.V = R.string.app_manager_enable_text;
                            }
                        }
                        this.V = R.string.app_manager_disable_text;
                    } catch (Exception unused) {
                    }
                    this.v.setTitle(this.V);
                    this.v.setIcon(this.W);
                    this.v.setEnabled(z2);
                }
            }
        }
        z2 = true;
        this.v.setTitle(this.V);
        this.v.setIcon(this.W);
        this.v.setEnabled(z2);
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context, miui.app.Activity] */
    /* renamed from: a */
    public void onLoadFinished(Loader<Boolean> loader, Boolean bool) {
        int i2;
        AppDetailTextBannerView appDetailTextBannerView;
        Resources resources;
        int i3;
        Object[] objArr;
        if (bool.booleanValue()) {
            this.f3522d.setSummary(FormatBytesUtil.formatBytes(this, this.Z));
            boolean z2 = true;
            int i4 = 0;
            this.e.setSummary(getString(R.string.app_manager_power_consume, new Object[]{Double.valueOf(this.ha)}));
            this.i.setSlideButtonChecked(this.ia);
            String str = null;
            w wVar = this.Y;
            if (wVar != null) {
                int i5 = wVar.f3560a;
                if (i5 != 0) {
                    resources = this.x;
                    i3 = R.plurals.app_manager_permission_summary_accept;
                    objArr = new Object[]{Integer.valueOf(i5)};
                } else {
                    i5 = wVar.f3561b;
                    if (i5 != 0) {
                        resources = this.x;
                        i3 = R.plurals.app_manager_permission_summary_ask;
                        objArr = new Object[]{Integer.valueOf(i5)};
                    } else {
                        str = this.x.getString(R.string.app_manager_permission_summary_reject);
                    }
                }
                str = resources.getQuantityString(i3, i5, objArr);
            }
            boolean z3 = (this.ma && RequiredPermissionsUtil.isAdaptedRequiredPermissions(this.z)) || RequiredPermissionsUtil.isAdaptedRequiredPermissionsOnData(this.z);
            if (!z3) {
                if (com.miui.permcenter.privacymanager.b.c.a(this)) {
                    this.j.setSummary("");
                } else {
                    this.j.setSummary(str);
                }
            }
            if (!z3 && (this.ma || this.na || this.Ea == null || C())) {
                z2 = false;
            }
            this.j.setTag(R.id.tag_remove_other_settings, Boolean.valueOf(!z2));
            AppDetailBannerItemView appDetailBannerItemView = this.j;
            if (!z2) {
                i4 = 8;
            }
            appDetailBannerItemView.setVisibility(i4);
            v();
            if (this.ja) {
                appDetailTextBannerView = this.p;
                i2 = R.string.app_manager_default_open_summary;
            } else {
                appDetailTextBannerView = this.p;
                i2 = R.string.app_manager_default_close_summary;
            }
            appDetailTextBannerView.setSummary(i2);
            this.n.setSummary(t());
            if (this.va) {
                this.o.setSummary(this.Aa);
            }
            this.l.setSummary(this.ea);
            String stringExtra = getIntent().getStringExtra("enter_way");
            if (TextUtils.isEmpty(stringExtra)) {
                stringExtra = "00004";
            }
            com.miui.appmanager.a.a.c(stringExtra);
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i2, int i3, Intent intent) {
        ApplicationsDetailsActivity.super.onActivityResult(i2, i3, intent);
        if (i2 == 10022) {
            u();
        }
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context] */
    public void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
        if (z2) {
            a(z2);
        } else {
            AppManageUtils.c((Context) this, this.aa, z2);
        }
        com.miui.appmanager.a.a.d("start_toggle");
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context, miui.app.Activity] */
    public void onClick(View view) {
        Intent a2;
        boolean a3;
        AppDetailCheckBoxView appDetailCheckBoxView;
        String str;
        if (view == this.f3521c) {
            if (this.R > 0) {
                L();
            } else {
                com.miui.securityscan.i.c.a(getApplicationContext(), (int) R.string.app_manager_has_not_data);
            }
            str = "storage";
        } else if (view == this.f3522d) {
            if (!this.ka || this.Z <= 0) {
                com.miui.securityscan.i.c.a(getApplicationContext(), (int) R.string.app_manager_has_not_data);
            } else {
                P();
            }
            str = "flow";
        } else if (view == this.e) {
            if (this.ha > 0.0d) {
                S();
            } else {
                com.miui.securityscan.i.c.a(getApplicationContext(), (int) R.string.app_manager_has_not_data);
            }
            str = "power";
        } else if (view == this.i) {
            N();
            str = "start_toggle";
        } else {
            AppDetailBannerItemView appDetailBannerItemView = this.j;
            if (view == appDetailBannerItemView) {
                b(((Boolean) appDetailBannerItemView.getTag(R.id.tag_remove_other_settings)).booleanValue());
                str = "permissions";
            } else {
                if (view == this.k) {
                    a2 = new Intent(HybirdServiceUtil.ACTION_HYBIRD_PERMISSIONS);
                } else if (view == this.l) {
                    Q();
                    str = "noti_manage";
                } else if (view == this.m) {
                    T();
                    str = "bettery_save";
                } else if (view == this.p) {
                    if (this.ja) {
                        o();
                    } else {
                        com.miui.securityscan.i.c.a(getApplicationContext(), (int) R.string.app_manager_default_close_summary);
                    }
                    str = "clean_default";
                } else if (view == this.n) {
                    K();
                    str = "network_control";
                } else if (view == this.q) {
                    M();
                    return;
                } else if (view == this.o) {
                    R();
                    return;
                } else {
                    AppDetailCheckBoxView appDetailCheckBoxView2 = this.r;
                    if (view == appDetailCheckBoxView2) {
                        a3 = appDetailCheckBoxView2.a();
                        AppManageUtils.a(this.aa, a3);
                        appDetailCheckBoxView = this.r;
                    } else {
                        AppDetailCheckBoxView appDetailCheckBoxView3 = this.s;
                        if (view == appDetailCheckBoxView3) {
                            a3 = appDetailCheckBoxView3.a();
                            AppManageUtils.a((Context) this, this.aa, !a3);
                            appDetailCheckBoxView = this.s;
                        } else if (view == this.h) {
                            a2 = PrivacyDetailActivity.a(this.aa, this.P, "app_detail");
                        } else {
                            return;
                        }
                    }
                    appDetailCheckBoxView.setSlideButtonChecked(!a3);
                    return;
                }
                startActivity(a2);
                return;
            }
        }
        com.miui.appmanager.a.a.d(str);
    }

    public void onConfigurationChanged(Configuration configuration) {
        ApplicationsDetailsActivity.super.onConfigurationChanged(configuration);
    }

    /* JADX WARNING: type inference failed for: r10v0, types: [b.b.c.c.a, com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context, android.app.LoaderManager$LoaderCallbacks, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.app_manager_applications_details);
        this.F = new q(this);
        Intent intent = getIntent();
        boolean z2 = false;
        try {
            this.B = b.b.o.g.e.a(Class.forName("android.content.pm.IPackageManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, (IBinder) b.b.o.g.e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "package"));
        } catch (Exception e2) {
            Log.e("ApplicationsDetailActivity", "reflect error while get package manager service", e2);
        }
        this.P = intent.getIntExtra("miui.intent.extra.USER_ID", UserHandle.myUserId());
        this.ua = C.b(this.P);
        this.R = intent.getLongExtra("size", 0);
        if (this.R == -1) {
            this.R = 0;
        }
        this.aa = intent.getStringExtra("package_name");
        if (this.aa == null) {
            finish();
        }
        this.y = AppManageUtils.a(this.B, this.aa, 128, this.P);
        if (this.y == null) {
            finish();
            return;
        }
        x();
        this.Q = this.y.uid;
        this.da = PackageUtil.getPackageNameFormat(this.aa, this.Q);
        this.ma = (this.y.flags & 1) != 0;
        if (b.b.c.j.B.a(this.Q) <= 10000) {
            z2 = true;
        }
        this.na = z2;
        this.ca = b.b.c.j.x.j(this, this.aa).toString();
        this.Ia = new u(this);
        initData();
        Loader loader = getLoaderManager().getLoader(124);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(124, (Bundle) null, this);
        if (!(Build.VERSION.SDK_INT < 24 || bundle == null || loader == null)) {
            loaderManager.restartLoader(124, (Bundle) null, this);
        }
        I();
    }

    public Loader<Boolean> onCreateLoader(int i2, Bundle bundle) {
        this.O = new C0316a(this);
        return this.O;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;
        int i2;
        if (this.y != null) {
            this.u = menu.add(0, 1, 0, R.string.menu_item_force_stop);
            this.u.setIcon(R.drawable.app_manager_finish_icon);
            this.u.setEnabled(this.fa);
            this.u.setShowAsAction(1);
            this.v = menu.add(0, 2, 0, R.string.app_manager_unstall_application);
            this.v.setIcon(R.drawable.app_manager_delete_icon);
            this.v.setEnabled(true);
            this.v.setShowAsAction(1);
            this.w = menu.add(0, 3, 0, R.string.app_manager_menu_clear_data);
            int i3 = this.Da;
            if (i3 > 8) {
                menuItem = this.w;
                i2 = R.drawable.action_button_clear_svg;
            } else if (i3 > 7) {
                menuItem = this.w;
                i2 = R.drawable.action_button_clear_light;
            } else {
                menuItem = this.w;
                i2 = R.drawable.action_button_clear_light_9;
            }
            menuItem.setIcon(i2);
            this.w.setShowAsAction(1);
            y();
            z();
        }
        return ApplicationsDetailsActivity.super.onCreateOptionsMenu(menu);
    }

    public void onDestroy() {
        ServiceConnection serviceConnection;
        ApplicationsDetailsActivity.super.onDestroy();
        if (!(this.Ha == null || (serviceConnection = this.Ja) == null)) {
            unbindService(serviceConnection);
        }
        this.F.removeCallbacksAndMessages((Object) null);
        l();
        getLoaderManager().destroyLoader(124);
    }

    public void onLoaderReset(Loader<Boolean> loader) {
    }

    /* JADX WARNING: type inference failed for: r3v1, types: [android.content.Context] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onOptionsItemSelected(android.view.MenuItem r12) {
        /*
            r11 = this;
            int r0 = r12.getItemId()
            r1 = 1
            if (r0 == r1) goto L_0x005e
            r2 = 2
            if (r0 == r2) goto L_0x0055
            r2 = 3
            if (r0 == r2) goto L_0x000e
            goto L_0x0050
        L_0x000e:
            long r5 = r11.S
            r3 = 0
            int r0 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x002b
            boolean r9 = r11.la
            if (r9 == 0) goto L_0x002b
            long r7 = r11.T
            int r0 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x002b
            android.content.pm.ApplicationInfo r0 = r11.y
            java.lang.String r4 = r0.manageSpaceActivityName
            android.content.DialogInterface$OnClickListener r10 = r11.Pa
            r3 = r11
            com.miui.appmanager.AppManageUtils.a((android.content.Context) r3, (java.lang.String) r4, (long) r5, (long) r7, (boolean) r9, (android.content.DialogInterface.OnClickListener) r10)
            goto L_0x0050
        L_0x002b:
            long r5 = r11.S
            int r0 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x0045
            boolean r0 = r11.la
            if (r0 == 0) goto L_0x0045
            android.content.pm.ApplicationInfo r0 = r11.y
            java.lang.String r0 = r0.manageSpaceActivityName
            if (r0 == 0) goto L_0x003f
            r11.O()
            goto L_0x0050
        L_0x003f:
            android.content.DialogInterface$OnClickListener r0 = r11.Na
            r11.a((int) r1, (android.content.DialogInterface.OnClickListener) r0)
            goto L_0x0050
        L_0x0045:
            long r0 = r11.T
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x0050
            android.content.DialogInterface$OnClickListener r0 = r11.Ma
            r11.a((int) r2, (android.content.DialogInterface.OnClickListener) r0)
        L_0x0050:
            boolean r12 = com.miui.appmanager.ApplicationsDetailsActivity.super.onOptionsItemSelected(r12)
            return r12
        L_0x0055:
            r11.U()
            java.lang.String r12 = "uninstall"
        L_0x005a:
            com.miui.appmanager.a.a.d((java.lang.String) r12)
            return r1
        L_0x005e:
            r11.G()
            java.lang.String r12 = "stop_running"
            goto L_0x005a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appmanager.ApplicationsDetailsActivity.onOptionsItemSelected(android.view.MenuItem):boolean");
    }

    public void onPause() {
        super.onPause();
        this.ga = true;
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context, miui.app.Activity] */
    public boolean onPrepareOptionsMenu(Menu menu) {
        m();
        f((Context) this);
        return ApplicationsDetailsActivity.super.onPrepareOptionsMenu(menu);
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [b.b.c.c.a, com.miui.appmanager.ApplicationsDetailsActivity, android.content.Context, android.app.LoaderManager$LoaderCallbacks, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        ApplicationInfo a2 = AppManageUtils.a(this.B, this.aa, 128, this.P);
        if (a2 == null) {
            finish();
        } else {
            this.y = a2;
        }
        if (this.ga) {
            getLoaderManager().restartLoader(124, (Bundle) null, this);
        }
        m();
        this.r.setSlideButtonChecked(!AppManageUtils.d(this.aa));
        this.s.setSlideButtonChecked(AppManageUtils.b(this, this.aa));
        this.h.setPrivacyEnable(com.miui.permcenter.privacymanager.behaviorrecord.o.b(this.aa, this.P));
    }
}
