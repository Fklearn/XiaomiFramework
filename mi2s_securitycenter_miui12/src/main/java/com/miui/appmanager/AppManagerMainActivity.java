package com.miui.appmanager;

import android.app.ActivityManager;
import android.app.LoaderManager;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import b.b.c.j.C;
import b.b.c.j.y;
import b.b.g.a;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.miui.appmanager.C0319b;
import com.miui.appmanager.c.m;
import com.miui.appmanager.c.n;
import com.miui.appmanager.c.o;
import com.miui.appmanager.c.p;
import com.miui.appmanager.c.q;
import com.miui.appmanager.c.r;
import com.miui.appmanager.widget.AMMainTopView;
import com.miui.luckymoney.config.Constants;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.firewall.BackgroundPolicyService;
import com.miui.networkassistant.firewall.UserConfigure;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securityscan.M;
import com.miui.securityscan.a.C0536b;
import com.miui.securityscan.cards.g;
import com.miui.securityscan.cards.k;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import miui.app.AlertDialog;
import miui.os.Build;
import miui.security.SecurityManager;
import miui.securitycenter.utils.SecurityCenterHelper;
import miui.text.ChinesePinyinConverter;
import miui.text.ExtraTextUtils;
import miui.theme.ThemeManagerHelper;
import miui.view.SearchActionMode;
import miui.widget.ProgressBar;
import miuix.recyclerview.widget.RecyclerView;
import org.json.JSONException;
import org.json.JSONObject;

public class AppManagerMainActivity extends b.b.c.c.a implements LoaderManager.LoaderCallbacks<D>, View.OnClickListener, C0319b.a, a.b {

    /* renamed from: a  reason: collision with root package name */
    private static final List<String> f3489a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    public static final Comparator<com.miui.appmanager.c.k> f3490b = new j();

    /* renamed from: c  reason: collision with root package name */
    public static final Comparator<com.miui.appmanager.c.k> f3491c = new k();

    /* renamed from: d  reason: collision with root package name */
    public static final Comparator<com.miui.appmanager.c.k> f3492d = new l();
    public static final Comparator<com.miui.appmanager.c.k> e = new m();
    public static final Comparator<com.miui.appmanager.c.k> f = new n();
    /* access modifiers changed from: private */
    public String[] A;
    /* access modifiers changed from: private */
    public String B;
    /* access modifiers changed from: private */
    public f C;
    private boolean D = false;
    /* access modifiers changed from: private */
    public boolean E = false;
    private int F = 0;
    /* access modifiers changed from: private */
    public int G = 0;
    public final Object H = new Object();
    /* access modifiers changed from: private */
    public p I = new p();
    private r J = new r();
    private q K = new q();
    private n L = new n();
    private int M = 0;
    private int N;
    /* access modifiers changed from: private */
    public boolean O;
    /* access modifiers changed from: private */
    public int P = -1;
    /* access modifiers changed from: private */
    public boolean Q = false;
    private boolean R = false;
    private boolean S = false;
    /* access modifiers changed from: private */
    public boolean T = false;
    /* access modifiers changed from: private */
    public boolean U = false;
    /* access modifiers changed from: private */
    public boolean V = false;
    private boolean W;
    /* access modifiers changed from: private */
    public boolean X = false;
    /* access modifiers changed from: private */
    public boolean Y = false;
    private boolean Z = false;
    private boolean aa;
    private boolean ba;
    private boolean ca;
    /* access modifiers changed from: private */
    public boolean da;
    private int ea;
    /* access modifiers changed from: private */
    public List<com.miui.appmanager.c.k> fa = new ArrayList();
    /* access modifiers changed from: private */
    public View g;
    /* access modifiers changed from: private */
    public List<com.miui.appmanager.c.k> ga = new ArrayList();
    /* access modifiers changed from: private */
    public AMMainTopView h;
    /* access modifiers changed from: private */
    public List<com.miui.appmanager.c.k> ha = new ArrayList();
    /* access modifiers changed from: private */
    public View i;
    private HashSet<ComponentName> ia = new HashSet<>();
    private TextView j;
    /* access modifiers changed from: private */
    public List<com.miui.appmanager.widget.a> ja;
    private RecyclerView k;
    private k ka;
    /* access modifiers changed from: private */
    public C0319b l;
    private b la;
    private MenuItem m;
    private c ma;
    private MenuItem n;
    private b.b.g.a na;
    private ProgressBar o;
    private g oa;
    /* access modifiers changed from: private */
    public com.miui.appmanager.widget.d p;
    private i pa;
    private AlertDialog q;
    private e qa;
    /* access modifiers changed from: private */
    public PackageManager r;
    private final h ra = new p(this);
    private i s;
    protected SearchActionMode sa;
    private UserManager t;
    private SearchActionMode.Callback ta = new q(this);
    private com.miui.securityscan.cards.k u;
    private k.a ua = new r(this);
    private UsageStatsManager v;
    private g.a va = new s(this);
    private b.c.a.b.f w;
    /* access modifiers changed from: private */
    public TextWatcher wa = new v(this);
    private g x;
    final IPackageStatsObserver.Stub xa = new w(this);
    /* access modifiers changed from: private */
    public D y = new D();
    private View.OnClickListener ya = new y(this);
    private ArrayList<com.miui.appmanager.c.k> z = new ArrayList<>();

    static class a {

        /* renamed from: a  reason: collision with root package name */
        SparseArray<Map<String, h>> f3493a = new SparseArray<>();

        /* renamed from: b  reason: collision with root package name */
        SparseArray<Map<String, Long>> f3494b = new SparseArray<>();

        a() {
        }
    }

    private static class b extends AsyncTask<Void, Void, List<com.miui.appmanager.c.k>> {

        /* renamed from: a  reason: collision with root package name */
        private Context f3495a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<AppManagerMainActivity> f3496b;

        public b(AppManagerMainActivity appManagerMainActivity) {
            this.f3495a = appManagerMainActivity.getApplicationContext();
            this.f3496b = new WeakReference<>(appManagerMainActivity);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public List<com.miui.appmanager.c.k> doInBackground(Void... voidArr) {
            AppManagerMainActivity appManagerMainActivity;
            String str;
            if (!isCancelled() && (appManagerMainActivity = (AppManagerMainActivity) this.f3496b.get()) != null && !appManagerMainActivity.isFinishing()) {
                if (appManagerMainActivity.da) {
                    str = com.miui.securityscan.i.h.b(this.f3495a, "app_manager_adv");
                } else {
                    com.miui.securityscan.c.e a2 = com.miui.securityscan.c.e.a(this.f3495a, "data_config");
                    HashMap hashMap = new HashMap();
                    hashMap.put(Constants.JSON_KEY_DATA_VERSION, a2.a("dataVsersionAm", ""));
                    str = m.a(this.f3495a, hashMap);
                }
                String unused = appManagerMainActivity.B = str;
                boolean c2 = appManagerMainActivity.c(appManagerMainActivity.B);
                if (appManagerMainActivity.B != null && c2) {
                    C.b();
                }
                if (isCancelled()) {
                    return null;
                }
                try {
                    com.miui.securityscan.i.h.a(this.f3495a, "app_manager_adv", appManagerMainActivity.B);
                } catch (Exception e) {
                    Log.e("AppManagerMainActivity", "loadAppManagerAdv writeStringToFileDir error", e);
                }
                if (!isCancelled() && !Build.IS_INTERNATIONAL_BUILD && !appManagerMainActivity.da && c2) {
                    return appManagerMainActivity.a(this.f3495a);
                }
            }
            return null;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(List<com.miui.appmanager.c.k> list) {
            AppManagerMainActivity appManagerMainActivity;
            super.onPostExecute(list);
            if (!isCancelled() && (appManagerMainActivity = (AppManagerMainActivity) this.f3496b.get()) != null && !appManagerMainActivity.isFinishing() && list != null && !list.isEmpty()) {
                appManagerMainActivity.fa.clear();
                appManagerMainActivity.fa.addAll(list);
                appManagerMainActivity.F();
            }
        }
    }

    private static class c extends b.b.c.i.a<D> {

        /* renamed from: b  reason: collision with root package name */
        private Context f3497b;

        /* renamed from: c  reason: collision with root package name */
        private WeakReference<AppManagerMainActivity> f3498c;

        /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, com.miui.appmanager.AppManagerMainActivity, java.lang.Object] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public c(com.miui.appmanager.AppManagerMainActivity r2) {
            /*
                r1 = this;
                r1.<init>(r2)
                android.content.Context r0 = r2.getApplicationContext()
                r1.f3497b = r0
                java.lang.ref.WeakReference r0 = new java.lang.ref.WeakReference
                r0.<init>(r2)
                r1.f3498c = r0
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.appmanager.AppManagerMainActivity.c.<init>(com.miui.appmanager.AppManagerMainActivity):void");
        }

        public D loadInBackground() {
            AppManagerMainActivity appManagerMainActivity = (AppManagerMainActivity) this.f3498c.get();
            if (isLoadInBackgroundCanceled() || appManagerMainActivity == null || appManagerMainActivity.isFinishing()) {
                return null;
            }
            return appManagerMainActivity.b(this.f3497b);
        }
    }

    private static class d extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private Context f3499a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<AppManagerMainActivity> f3500b;

        /* renamed from: c  reason: collision with root package name */
        private final List<com.miui.appmanager.c.k> f3501c;

        public d(AppManagerMainActivity appManagerMainActivity, List<com.miui.appmanager.c.k> list) {
            this.f3499a = appManagerMainActivity.getApplicationContext();
            this.f3500b = new WeakReference<>(appManagerMainActivity);
            this.f3501c = list;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            Void voidR = null;
            if (isCancelled()) {
                return null;
            }
            AppManagerMainActivity appManagerMainActivity = (AppManagerMainActivity) this.f3500b.get();
            if (appManagerMainActivity != null && !appManagerMainActivity.isFinishing()) {
                SparseArray g = appManagerMainActivity.y();
                SparseArray h = appManagerMainActivity.s();
                int i = 0;
                while (i < this.f3501c.size()) {
                    if (isCancelled()) {
                        return voidR;
                    }
                    com.miui.appmanager.c.j jVar = (com.miui.appmanager.c.j) this.f3501c.get(i);
                    int userId = UserHandle.getUserId(jVar.i());
                    String e = jVar.e();
                    long a2 = appManagerMainActivity.a((SparseArray<Map<String, Long>>) g, e, userId);
                    appManagerMainActivity.a(this.f3499a, jVar, jVar.l(), (Map<String, h>) (Map) h.get(userId), a2);
                    jVar.b(a2);
                    boolean b2 = appManagerMainActivity.b(e, userId);
                    if (b2 != jVar.k()) {
                        if (!b2) {
                            jVar.b(false);
                        }
                        jVar.a(b2);
                    }
                    i++;
                    voidR = null;
                }
            }
            return voidR;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            AppManagerMainActivity appManagerMainActivity = (AppManagerMainActivity) this.f3500b.get();
            if (appManagerMainActivity != null && !appManagerMainActivity.isFinishing()) {
                if (appManagerMainActivity.G == 1 || appManagerMainActivity.G == 0) {
                    appManagerMainActivity.l.notifyDataSetChanged();
                } else {
                    appManagerMainActivity.F();
                }
                boolean unused = appManagerMainActivity.U = true;
            }
        }
    }

    private static class e extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private Context f3502a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<AppManagerMainActivity> f3503b;

        /* renamed from: c  reason: collision with root package name */
        private final List<com.miui.appmanager.c.k> f3504c;

        public e(AppManagerMainActivity appManagerMainActivity, List<com.miui.appmanager.c.k> list) {
            this.f3502a = appManagerMainActivity.getApplicationContext();
            this.f3503b = new WeakReference<>(appManagerMainActivity);
            this.f3504c = list;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            AppManagerMainActivity appManagerMainActivity = (AppManagerMainActivity) this.f3503b.get();
            if (!isCancelled() && appManagerMainActivity != null && !appManagerMainActivity.isFinishing()) {
                b.b.c.b.b a2 = b.b.c.b.b.a(this.f3502a);
                Iterator<com.miui.appmanager.c.k> it = this.f3504c.iterator();
                while (it.hasNext()) {
                    com.miui.appmanager.c.j jVar = (com.miui.appmanager.c.j) it.next();
                    if ("".equals(jVar.d())) {
                        try {
                            jVar.b(a2.a(jVar.e()).a());
                        } catch (Exception unused) {
                        }
                    }
                    jVar.a(appManagerMainActivity.b(jVar.d()));
                }
            }
            return null;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Void voidR) {
            AppManagerMainActivity appManagerMainActivity = (AppManagerMainActivity) this.f3503b.get();
            if (appManagerMainActivity != null && !appManagerMainActivity.isFinishing()) {
                if (!appManagerMainActivity.X && appManagerMainActivity.Y) {
                    appManagerMainActivity.F();
                }
                boolean unused = appManagerMainActivity.X = true;
            }
        }
    }

    private static class f extends Handler {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<AppManagerMainActivity> f3505a;

        public f(AppManagerMainActivity appManagerMainActivity) {
            this.f3505a = new WeakReference<>(appManagerMainActivity);
        }

        public void handleMessage(Message message) {
            Bundle data;
            super.handleMessage(message);
            AppManagerMainActivity appManagerMainActivity = (AppManagerMainActivity) this.f3505a.get();
            if (appManagerMainActivity != null && !appManagerMainActivity.isFinishing()) {
                int i = message.what;
                if (i == 0) {
                    appManagerMainActivity.a((List<com.miui.appmanager.c.k>) appManagerMainActivity.E ? appManagerMainActivity.ga : appManagerMainActivity.ha);
                } else if (i == 1) {
                    appManagerMainActivity.g((List<com.miui.appmanager.c.k>) appManagerMainActivity.E ? appManagerMainActivity.y.f3572c : appManagerMainActivity.y.f3571b);
                    appManagerMainActivity.C.removeMessages(1);
                    appManagerMainActivity.C.sendEmptyMessageDelayed(1, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                } else if (i == 2) {
                    appManagerMainActivity.b((List<com.miui.appmanager.c.k>) appManagerMainActivity.E ? appManagerMainActivity.ga : appManagerMainActivity.ha);
                } else if (i == 4 && (data = message.getData()) != null) {
                    appManagerMainActivity.a(appManagerMainActivity.getApplicationContext(), data.getInt(UserConfigure.Columns.USER_ID), data.getString("packageName"), Long.valueOf(data.getLong("size")).longValue());
                }
            }
        }
    }

    private static class g extends AsyncTask<Void, Void, Boolean> {

        /* renamed from: a  reason: collision with root package name */
        private Context f3506a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<AppManagerMainActivity> f3507b;

        /* renamed from: c  reason: collision with root package name */
        private final List<PackageInfo> f3508c;

        public g(AppManagerMainActivity appManagerMainActivity, List<PackageInfo> list) {
            this.f3506a = appManagerMainActivity.getApplicationContext();
            this.f3507b = new WeakReference<>(appManagerMainActivity);
            this.f3508c = list;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Boolean doInBackground(Void... voidArr) {
            AppManagerMainActivity appManagerMainActivity;
            if (isCancelled() || (appManagerMainActivity = (AppManagerMainActivity) this.f3507b.get()) == null || appManagerMainActivity.isFinishing()) {
                return false;
            }
            BackgroundPolicyService instance = BackgroundPolicyService.getInstance(this.f3506a);
            boolean z = false;
            for (int i = 0; i < this.f3508c.size(); i++) {
                if (isCancelled()) {
                    return z;
                }
                ApplicationInfo applicationInfo = this.f3508c.get(i).applicationInfo;
                int userId = UserHandle.getUserId(applicationInfo.uid);
                if (!AppManageUtils.a((SecurityManager) this.f3506a.getSystemService("security"), applicationInfo.packageName, UserHandle.getUserId(applicationInfo.uid))) {
                    AppManageUtils.a(applicationInfo.packageName, applicationInfo.uid, true);
                }
                appManagerMainActivity.r.clearPackagePreferredActivities(applicationInfo.packageName);
                if (appManagerMainActivity.a(applicationInfo.packageName, userId) == 3) {
                    appManagerMainActivity.r.setApplicationEnabledSetting(applicationInfo.packageName, 0, 1);
                    HashMap hashMap = appManagerMainActivity.y.e.get(userId);
                    com.miui.appmanager.c.j jVar = (com.miui.appmanager.c.j) hashMap.get(applicationInfo.packageName);
                    if (!(hashMap == null || jVar == null)) {
                        jVar.a(true);
                    }
                    z = true;
                }
                if (((applicationInfo.flags & 1) == 0 || applicationInfo.uid > 10000) && instance.isAppRestrictBackground(applicationInfo.packageName, applicationInfo.uid)) {
                    instance.setAppRestrictBackground(applicationInfo.uid, false);
                }
            }
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            appManagerMainActivity.r.getPreferredActivities(arrayList, arrayList2, (String) null);
            for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                appManagerMainActivity.r.clearPackagePreferredActivities(((ComponentName) arrayList2.get(i2)).getPackageName());
            }
            return z;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Boolean bool) {
            AppManagerMainActivity appManagerMainActivity = (AppManagerMainActivity) this.f3507b.get();
            if (appManagerMainActivity != null && !appManagerMainActivity.isFinishing() && bool.booleanValue()) {
                appManagerMainActivity.l.notifyDataSetChanged();
            }
        }
    }

    class h {

        /* renamed from: a  reason: collision with root package name */
        ArrayList<Integer> f3509a = new ArrayList<>();

        h(ArrayList<Integer> arrayList) {
            this.f3509a = arrayList;
        }
    }

    private static class i extends AsyncTask<Void, Void, Boolean> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<AppManagerMainActivity> f3511a;

        /* renamed from: b  reason: collision with root package name */
        private final List<com.miui.appmanager.c.k> f3512b;

        public i(AppManagerMainActivity appManagerMainActivity, List<com.miui.appmanager.c.k> list) {
            this.f3511a = new WeakReference<>(appManagerMainActivity);
            this.f3512b = list;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Boolean doInBackground(Void... voidArr) {
            boolean z = false;
            if (isCancelled()) {
                return false;
            }
            AppManagerMainActivity appManagerMainActivity = (AppManagerMainActivity) this.f3511a.get();
            if (appManagerMainActivity != null && !appManagerMainActivity.isFinishing()) {
                Iterator<com.miui.appmanager.c.k> it = this.f3512b.iterator();
                while (it.hasNext()) {
                    com.miui.appmanager.c.j jVar = (com.miui.appmanager.c.j) it.next();
                    String e = jVar.e();
                    if (isCancelled()) {
                        return z;
                    }
                    boolean b2 = appManagerMainActivity.b(e, UserHandle.getUserId(jVar.i()));
                    if (jVar.k() != b2) {
                        z = true;
                        if (!b2) {
                            jVar.b(false);
                        }
                        jVar.a(b2);
                    }
                }
            }
            return z;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Boolean bool) {
            AppManagerMainActivity appManagerMainActivity = (AppManagerMainActivity) this.f3511a.get();
            if (appManagerMainActivity != null && !appManagerMainActivity.isFinishing() && bool.booleanValue()) {
                appManagerMainActivity.l.notifyDataSetChanged();
            }
        }
    }

    private static class j extends AsyncTask<Void, Void, a> {

        /* renamed from: a  reason: collision with root package name */
        private Context f3513a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<AppManagerMainActivity> f3514b;

        /* renamed from: c  reason: collision with root package name */
        private List<com.miui.appmanager.c.k> f3515c;

        public j(AppManagerMainActivity appManagerMainActivity, List<com.miui.appmanager.c.k> list) {
            this.f3514b = new WeakReference<>(appManagerMainActivity);
            this.f3515c = list;
            this.f3513a = appManagerMainActivity.getApplicationContext();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public a doInBackground(Void... voidArr) {
            AppManagerMainActivity appManagerMainActivity;
            if (isCancelled() || (appManagerMainActivity = (AppManagerMainActivity) this.f3514b.get()) == null || appManagerMainActivity.isFinishing()) {
                return null;
            }
            a aVar = new a();
            aVar.f3494b = appManagerMainActivity.y();
            aVar.f3493a = appManagerMainActivity.s();
            return aVar;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(a aVar) {
            AppManagerMainActivity appManagerMainActivity;
            if (aVar != null && (appManagerMainActivity = (AppManagerMainActivity) this.f3514b.get()) != null && !appManagerMainActivity.isFinishing()) {
                boolean z = false;
                for (int i = 0; i < this.f3515c.size(); i++) {
                    com.miui.appmanager.c.j jVar = (com.miui.appmanager.c.j) this.f3515c.get(i);
                    int userId = UserHandle.getUserId(jVar.i());
                    long a2 = appManagerMainActivity.a(aVar.f3494b, jVar.e(), userId);
                    Map map = aVar.f3493a.get(userId);
                    boolean containsKey = map != null ? map.containsKey(jVar.e()) : false;
                    if (jVar.l() != containsKey) {
                        appManagerMainActivity.a(this.f3513a, jVar, containsKey, (Map<String, h>) map, a2);
                        jVar.b(a2);
                        z = true;
                    }
                }
                if (!z) {
                    return;
                }
                if (!appManagerMainActivity.O) {
                    appManagerMainActivity.l.notifyDataSetChanged();
                    int unused = appManagerMainActivity.P = -1;
                    return;
                }
                int unused2 = appManagerMainActivity.P = 1;
            }
        }
    }

    private static class k extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private Context f3516a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<AppManagerMainActivity> f3517b;

        /* renamed from: c  reason: collision with root package name */
        private List<com.miui.appmanager.c.k> f3518c;

        public k(AppManagerMainActivity appManagerMainActivity, List<com.miui.appmanager.c.k> list) {
            this.f3516a = appManagerMainActivity.getApplicationContext();
            this.f3517b = new WeakReference<>(appManagerMainActivity);
            this.f3518c = new ArrayList(list);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            AppManagerMainActivity appManagerMainActivity;
            if (!isCancelled() && (appManagerMainActivity = (AppManagerMainActivity) this.f3517b.get()) != null && !appManagerMainActivity.isFinishing()) {
                for (int i = 0; i < this.f3518c.size() && !isCancelled(); i++) {
                    com.miui.appmanager.c.j jVar = (com.miui.appmanager.c.j) this.f3518c.get(i);
                    if (jVar != null) {
                        if (Build.VERSION.SDK_INT > 25) {
                            F a2 = AppManageUtils.a(this.f3516a, jVar.b(), jVar.i());
                            appManagerMainActivity.a(UserHandle.getUserId(jVar.i()), jVar.e(), Long.valueOf(a2.f3578c + a2.f3577b));
                        } else {
                            AppManageUtils.a(appManagerMainActivity.r, jVar.e(), UserHandle.getUserId(jVar.i()), (IPackageStatsObserver) appManagerMainActivity.xa);
                        }
                    }
                }
                return null;
            }
            return null;
        }
    }

    static {
        try {
            f3489a.addAll((Collection) b.b.o.g.e.a(Class.forName("miui.securityspace.XSpaceConstant"), "REQUIRED_APPS", List.class));
        } catch (Exception e2) {
            Log.e("AppManagerMainActivity", "reflect error while get required_apps", e2);
        }
        f3489a.add("com.xiaomi.xmsf");
        f3489a.add("com.xiaomi.gamecenter.sdk.service");
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.appmanager.AppManagerMainActivity] */
    private void A() {
        this.x = new g(this);
        this.x.a(this.ra);
        com.miui.securityscan.cards.k kVar = this.u;
        if (kVar != null) {
            kVar.a(this.ua);
        }
        com.miui.securityscan.cards.g.a((Context) this).a(this.va);
    }

    /* access modifiers changed from: private */
    public void B() {
        this.oa = new g(this, new ArrayList(this.y.f3573d));
        this.oa.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* access modifiers changed from: private */
    public void C() {
        if (!this.z.isEmpty()) {
            Iterator<com.miui.appmanager.c.k> it = this.z.iterator();
            while (it.hasNext()) {
                com.miui.appmanager.c.k next = it.next();
                if (next instanceof com.miui.appmanager.c.j) {
                    com.miui.appmanager.c.j jVar = (com.miui.appmanager.c.j) next;
                    if (jVar.g() != null) {
                        jVar.c((String) null);
                    }
                }
            }
            this.z.clear();
        }
    }

    /* access modifiers changed from: private */
    public void D() {
        b.c.a.b.f fVar = this.w;
        if (fVar != null) {
            fVar.c();
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.appmanager.AppManagerMainActivity] */
    private void E() {
        g gVar = this.x;
        if (gVar != null) {
            gVar.a();
        }
        com.miui.securityscan.cards.k kVar = this.u;
        if (kVar != null) {
            kVar.b(this.ua);
        }
        com.miui.securityscan.cards.g.a((Context) this).c(this.va);
    }

    /* access modifiers changed from: private */
    public void F() {
        if (this.y != null) {
            boolean z2 = m() && this.z.isEmpty() && TextUtils.isEmpty(this.sa.getSearchInput().getText().toString());
            if (!m() || z2) {
                this.h.setVisibility(0);
                ArrayList arrayList = new ArrayList();
                if (!(this.E ? this.y.f3572c.isEmpty() : this.y.f3571b.isEmpty())) {
                    if (m() || z2) {
                        arrayList.remove(this.L);
                    } else {
                        arrayList.add(this.L);
                    }
                    if (!this.fa.isEmpty()) {
                        arrayList.addAll(this.fa);
                    }
                    e((List<com.miui.appmanager.c.k>) this.E ? this.y.f3572c : this.y.f3571b);
                    arrayList.add(this.I);
                    arrayList.addAll(this.E ? this.y.f3572c : this.y.f3571b);
                    this.l.b(arrayList);
                }
                this.l.b(arrayList);
                Resources resources = getResources();
                int size = this.E ? this.y.f3572c.size() : this.y.f3571b.size();
                Object[] objArr = new Object[1];
                objArr[0] = Integer.valueOf(this.E ? this.y.f3572c.size() : this.y.f3571b.size());
                this.j.setHint(resources.getQuantityString(R.plurals.find_applications, size, objArr));
                return;
            }
            this.h.setVisibility(8);
            e((List<com.miui.appmanager.c.k>) this.z);
            this.l.b(this.z);
        }
    }

    private void G() {
        MenuItem menuItem;
        if (this.m != null && (menuItem = this.n) != null) {
            if (this.E) {
                menuItem.setVisible(true);
                this.m.setVisible(false);
                return;
            }
            menuItem.setVisible(false);
            this.m.setVisible(true);
        }
    }

    /* access modifiers changed from: private */
    public int a(String str, int i2) {
        try {
            return AppManageUtils.a(str, i2);
        } catch (Exception e2) {
            Log.e("AppManagerMainActivity", "getApplicationEnabledSetting error", e2);
            return 0;
        }
    }

    /* access modifiers changed from: private */
    public long a(SparseArray<Map<String, Long>> sparseArray, String str, int i2) {
        Map map = sparseArray.get(i2);
        if (map == null || !map.containsKey(str)) {
            return -1;
        }
        long longValue = ((Long) map.get(str)).longValue();
        if (longValue <= 1262275200000L) {
            return -1;
        }
        return longValue;
    }

    private long a(h hVar) {
        long j2 = 0;
        if (hVar != null) {
            ArrayList<Integer> arrayList = hVar.f3509a;
            int[] iArr = new int[arrayList.size()];
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                iArr[i2] = arrayList.get(i2).intValue();
            }
            long[] processPss = SecurityCenterHelper.getProcessPss(iArr);
            if (processPss != null) {
                for (long j3 : processPss) {
                    j2 += j3 * 1024;
                }
            }
        }
        return j2;
    }

    private String a(long j2) {
        if (j2 == -1) {
            return getString(R.string.app_usage_never);
        }
        long currentTimeMillis = System.currentTimeMillis() - j2;
        if (currentTimeMillis <= 86400000) {
            return getString(R.string.app_usage_recently);
        }
        if (currentTimeMillis <= 2592000000L) {
            int i2 = (int) (currentTimeMillis / 86400000);
            return getResources().getQuantityString(R.plurals.app_usage_day, i2, new Object[]{Integer.valueOf(i2)});
        } else if (currentTimeMillis <= 31104000000L) {
            int i3 = (int) ((currentTimeMillis / 86400000) / 30);
            return getResources().getQuantityString(R.plurals.app_usage_month, i3, new Object[]{Integer.valueOf(i3)});
        } else {
            int i4 = (int) (((currentTimeMillis / 86400000) / 30) / 12);
            return getResources().getQuantityString(R.plurals.app_usage_year, i4, new Object[]{Integer.valueOf(i4)});
        }
    }

    /* access modifiers changed from: private */
    public List<com.miui.appmanager.c.k> a(Context context) {
        boolean z2;
        ArrayList arrayList = new ArrayList();
        if (!p()) {
            return arrayList;
        }
        com.miui.securityscan.c.e a2 = com.miui.securityscan.c.e.a(context, "data_config");
        m mVar = null;
        if (TextUtils.isEmpty(this.B)) {
            return arrayList;
        }
        try {
            mVar = m.a(context, new JSONObject(this.B), Boolean.valueOf(this.da));
        } catch (Exception e2) {
            Log.e("AppManagerMainActivity", "JSONException when getAdList", e2);
        }
        if (mVar == null) {
            return arrayList;
        }
        if (mVar.a() != null) {
            a2.b("dataVsersionAm", mVar.a());
        }
        ArrayList<com.miui.appmanager.c.k> b2 = mVar.b();
        if (b2.isEmpty()) {
            return b2;
        }
        ArrayList arrayList2 = new ArrayList();
        for (int size = b2.size() - 1; size > 0; size--) {
            if ((b2.get(size) instanceof o) && (b2.get(size - 1) instanceof o)) {
                arrayList2.add(b2.get(size));
            }
        }
        int i2 = 0;
        if (b2.get(0) instanceof o) {
            arrayList2.add(b2.get(0));
        }
        b2.removeAll(arrayList2);
        if (!b2.isEmpty()) {
            Iterator<com.miui.appmanager.c.k> it = b2.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z2 = false;
                    break;
                }
                com.miui.appmanager.c.k next = it.next();
                if (next instanceof com.miui.appmanager.c.i) {
                    z2 = ((com.miui.appmanager.c.i) next).d();
                    break;
                }
            }
            if (z2) {
                for (com.miui.appmanager.c.k next2 : b2) {
                    if (next2 instanceof com.miui.appmanager.c.c) {
                        ((com.miui.appmanager.c.c) next2).a(false);
                    }
                }
            }
        }
        for (com.miui.appmanager.c.k kVar : b2) {
            if (kVar instanceof com.miui.appmanager.c.c) {
                i2++;
            }
        }
        return i2 <= 0 ? new ArrayList() : b2;
    }

    private List<com.miui.appmanager.c.k> a(List<com.miui.appmanager.c.k> list, int i2, int i3) {
        if (i2 < 0 || i2 > i3 || i3 > list.size()) {
            return null;
        }
        return list.subList(i2, i3);
    }

    /* access modifiers changed from: private */
    public void a(int i2, String str, Long l2) {
        Message message = new Message();
        message.what = 4;
        Bundle bundle = new Bundle();
        bundle.putInt(UserConfigure.Columns.USER_ID, i2);
        bundle.putString("packageName", str);
        bundle.putLong("size", l2.longValue());
        message.setData(bundle);
        this.C.sendMessage(message);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x006b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(android.content.Context r5, int r6, java.lang.String r7, long r8) {
        /*
            r4 = this;
            com.miui.appmanager.AppManagerMainActivity$k r0 = r4.ka
            if (r0 == 0) goto L_0x000b
            boolean r0 = r0.isCancelled()
            if (r0 == 0) goto L_0x000b
            return
        L_0x000b:
            java.lang.Object r0 = r4.H
            monitor-enter(r0)
            com.miui.appmanager.D r1 = r4.y     // Catch:{ all -> 0x006c }
            android.util.SparseArray<java.util.HashMap<java.lang.String, com.miui.appmanager.c.k>> r1 = r1.e     // Catch:{ all -> 0x006c }
            java.lang.Object r6 = r1.get(r6)     // Catch:{ all -> 0x006c }
            java.util.HashMap r6 = (java.util.HashMap) r6     // Catch:{ all -> 0x006c }
            if (r6 != 0) goto L_0x001c
            monitor-exit(r0)     // Catch:{ all -> 0x006c }
            return
        L_0x001c:
            java.lang.Object r6 = r6.get(r7)     // Catch:{ all -> 0x006c }
            com.miui.appmanager.c.j r6 = (com.miui.appmanager.c.j) r6     // Catch:{ all -> 0x006c }
            int r7 = r4.F     // Catch:{ all -> 0x006c }
            r1 = 1
            int r7 = r7 + r1
            r4.F = r7     // Catch:{ all -> 0x006c }
            if (r6 == 0) goto L_0x006a
            long r2 = r6.h()     // Catch:{ all -> 0x006c }
            int r7 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r7 == 0) goto L_0x0053
            r6.a((long) r8)     // Catch:{ all -> 0x006c }
            boolean r7 = r4.W     // Catch:{ all -> 0x006c }
            if (r7 == 0) goto L_0x004c
            r7 = 2131755329(0x7f100141, float:1.9141534E38)
            java.lang.String r7 = r4.getString(r7)     // Catch:{ all -> 0x006c }
            java.lang.String r5 = miui.text.ExtraTextUtils.formatFileSize(r5, r8)     // Catch:{ all -> 0x006c }
            java.lang.String r5 = r7.concat(r5)     // Catch:{ all -> 0x006c }
        L_0x0048:
            r6.d((java.lang.String) r5)     // Catch:{ all -> 0x006c }
            goto L_0x0051
        L_0x004c:
            java.lang.String r5 = miui.text.ExtraTextUtils.formatFileSize(r5, r8)     // Catch:{ all -> 0x006c }
            goto L_0x0048
        L_0x0051:
            r4.S = r1     // Catch:{ all -> 0x006c }
        L_0x0053:
            int r5 = r4.F     // Catch:{ all -> 0x006c }
            int r6 = r4.N     // Catch:{ all -> 0x006c }
            if (r5 != r6) goto L_0x006a
            boolean r5 = r4.S     // Catch:{ all -> 0x006c }
            if (r5 == 0) goto L_0x0064
            com.miui.appmanager.AppManagerMainActivity$f r5 = r4.C     // Catch:{ all -> 0x006c }
            r6 = 0
            r5.sendEmptyMessage(r6)     // Catch:{ all -> 0x006c }
            goto L_0x006a
        L_0x0064:
            com.miui.appmanager.AppManagerMainActivity$f r5 = r4.C     // Catch:{ all -> 0x006c }
            r6 = 2
            r5.sendEmptyMessage(r6)     // Catch:{ all -> 0x006c }
        L_0x006a:
            monitor-exit(r0)     // Catch:{ all -> 0x006c }
            return
        L_0x006c:
            r5 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x006c }
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appmanager.AppManagerMainActivity.a(android.content.Context, int, java.lang.String, long):void");
    }

    /* access modifiers changed from: private */
    public void a(Context context, com.miui.appmanager.c.j jVar, boolean z2, Map<String, h> map, long j2) {
        String str;
        jVar.b(z2);
        if (z2) {
            h hVar = map.get(jVar.e());
            if (this.W) {
                str = getString(R.string.app_manager_memory) + ExtraTextUtils.formatShortFileSize(context, a(hVar));
            } else {
                str = ExtraTextUtils.formatShortFileSize(context, a(hVar));
            }
        } else {
            str = a(j2);
        }
        jVar.e(str);
    }

    /* access modifiers changed from: private */
    public void a(C0319b bVar, String str) {
        int i2;
        if (bVar != null) {
            Iterator<com.miui.appmanager.c.k> it = bVar.c().iterator();
            while (it.hasNext()) {
                com.miui.appmanager.c.k next = it.next();
                if ((next instanceof com.miui.appmanager.c.c) && str.equals(((com.miui.appmanager.c.c) next).f())) {
                    if (!this.O) {
                        bVar.notifyDataSetChanged();
                        i2 = -1;
                    } else {
                        i2 = 3;
                    }
                    this.P = i2;
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(C0319b bVar, String str, int i2, int i3) {
        int i4;
        if (bVar != null) {
            Iterator<com.miui.appmanager.c.k> it = bVar.c().iterator();
            while (it.hasNext()) {
                com.miui.appmanager.c.k next = it.next();
                if ((next instanceof com.miui.appmanager.c.c) && str.equals(((com.miui.appmanager.c.c) next).f())) {
                    if (!this.O) {
                        bVar.notifyDataSetChanged();
                        i4 = -1;
                    } else {
                        i4 = 3;
                    }
                    this.P = i4;
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(String str, UserHandle userHandle, String str2) {
        if (Constants.System.ACTION_PACKAGE_REMOVED.equals(str2)) {
            c(str, userHandle.getIdentifier());
        }
    }

    /* access modifiers changed from: private */
    public void a(List<com.miui.appmanager.c.k> list) {
        int i2;
        if (this.M != list.size()) {
            this.M = this.N;
            this.N = this.M + 20 > list.size() ? list.size() : this.M + 20;
            if (!this.O) {
                if (this.G != 3 || this.T) {
                    this.l.notifyDataSetChanged();
                } else {
                    F();
                }
                i2 = -1;
            } else {
                i2 = 0;
            }
            this.P = i2;
            int i3 = this.M;
            int i4 = this.N;
            if (i3 <= i4) {
                h(a(list, i3, i4));
            }
        }
    }

    private void a(List<com.miui.appmanager.c.k> list, String str, int i2) {
        for (com.miui.appmanager.c.k next : list) {
            com.miui.appmanager.c.j jVar = (com.miui.appmanager.c.j) next;
            if (jVar.e().equals(str) && UserHandle.getUserId(jVar.i()) == i2) {
                list.remove(next);
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(boolean z2) {
        this.E = !this.E;
        this.s.c(this.E);
        G();
        if (this.X && z2) {
            F();
        }
        if (!this.Z) {
            c(this.E ? this.ga : this.ha);
            this.T = false;
            h(a(this.E ? this.ga : this.ha, this.M, this.N));
            d((List<com.miui.appmanager.c.k>) this.E ? this.y.f3572c : this.y.f3571b);
            this.Z = true;
        }
    }

    private boolean a(List<String> list, String str, boolean z2) {
        if (miui.os.Build.IS_INTERNATIONAL_BUILD && "com.mint.keyboard".equals(str)) {
            String upperCase = y.a("ro.miui.region", "").toUpperCase();
            return !"IN".toUpperCase().equals(upperCase) && !"ID".toUpperCase().equals(upperCase);
        } else if (!miui.os.Build.IS_INTERNATIONAL_BUILD || !"com.miui.android.fashiongallery".equals(str)) {
            return (list != null && list.contains(str)) || AppManageUtils.f3484b.contains(str) || AppManageUtils.f3486d.contains(str) || !z2;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public D b(Context context) {
        String str;
        String str2;
        List<PackageInfo> a2;
        this.w = b.b.c.j.r.b();
        this.u = com.miui.securityscan.cards.k.a(context);
        this.aa = this.s.a();
        this.ea = M.d();
        D d2 = new D();
        b.b.c.b.b a3 = b.b.c.b.b.a(context);
        d2.f3573d = new ArrayList(a3.a());
        if (UserHandle.myUserId() == 0 && AppManageUtils.g(context) && (a2 = AppManageUtils.a(this.r, 64, 999)) != null && a2.size() > 0 && !d2.f3573d.containsAll(a2)) {
            d2.f3573d.addAll(a2);
        }
        SparseArray<Map<String, h>> s2 = s();
        List<UserHandle> userProfiles = this.t.getUserProfiles();
        for (UserHandle identifier : userProfiles) {
            d2.e.put(identifier.getIdentifier(), new HashMap());
        }
        SecurityManager securityManager = (SecurityManager) context.getSystemService("security");
        SparseArray<List<String>> a4 = AppManageUtils.a(context, this.r, userProfiles, this.ia);
        for (PackageInfo next : d2.f3573d) {
            boolean z2 = true;
            boolean z3 = false;
            if ((next.applicationInfo.flags & 1) == 0) {
                z2 = false;
            }
            int userId = UserHandle.getUserId(next.applicationInfo.uid);
            if ((!f3489a.contains(next.packageName) || !C.a(next.applicationInfo.uid)) && !AppManageUtils.g.contains(next.packageName) && !AppManageUtils.a(securityManager, next.packageName, userId)) {
                com.miui.appmanager.c.j jVar = new com.miui.appmanager.c.j();
                jVar.a(next);
                if (userId == 999) {
                    str2 = next.packageName;
                    str = "pkg_icon_xspace://";
                } else {
                    str2 = next.packageName;
                    str = "pkg_icon://";
                }
                jVar.a(str.concat(str2));
                Map map = s2.get(userId);
                if (map != null) {
                    z3 = map.containsKey(next.packageName);
                }
                jVar.b(z3);
                jVar.c(z2);
                HashMap hashMap = d2.e.get(userId);
                if (hashMap != null) {
                    hashMap.put(next.packageName, jVar);
                }
                boolean a5 = a(a4.get(userId), next.packageName, z2);
                if (a5) {
                    d2.f3571b.add(jVar);
                }
                if (a5 || this.E) {
                    try {
                        jVar.b(a3.a(next.packageName).a());
                    } catch (Exception unused) {
                    }
                }
                d2.f3572c.add(jVar);
            }
        }
        if (!miui.os.Build.IS_INTERNATIONAL_BUILD && this.da) {
            this.B = com.miui.securityscan.i.h.b(context, "app_manager_adv");
            d2.f3570a.addAll(a(context));
        }
        return d2;
    }

    /* access modifiers changed from: private */
    public void b(List<com.miui.appmanager.c.k> list) {
        if (this.M != list.size()) {
            this.M = this.N;
            this.N = this.M + 20 > list.size() ? list.size() : this.M + 20;
            if (this.O) {
                this.P = 0;
            } else if (this.G == 3 && !this.T) {
                F();
            }
            int i2 = this.M;
            int i3 = this.N;
            if (i2 <= i3) {
                h(a(list, i2, i3));
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x000a, code lost:
        r3 = a(r3, r4);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean b(java.lang.String r3, int r4) {
        /*
            r2 = this;
            java.lang.String r0 = "com.xiaomi.mipicks"
            boolean r0 = r0.equals(r3)
            r1 = 1
            if (r0 == 0) goto L_0x000a
            return r1
        L_0x000a:
            int r3 = r2.a((java.lang.String) r3, (int) r4)
            if (r3 == 0) goto L_0x0014
            if (r3 != r1) goto L_0x0013
            goto L_0x0014
        L_0x0013:
            r1 = 0
        L_0x0014:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appmanager.AppManagerMainActivity.b(java.lang.String, int):boolean");
    }

    private void c(String str, int i2) {
        synchronized (this.H) {
            if (this.y.e.get(i2) != null) {
                this.y.e.get(i2).remove(str);
            }
        }
        a((List<com.miui.appmanager.c.k>) this.y.f3571b, str, i2);
        a(this.y.f3572c, str, i2);
        if (m()) {
            a((List<com.miui.appmanager.c.k>) this.z, str, i2);
        }
        int i3 = 0;
        while (true) {
            if (i3 < this.y.f3573d.size()) {
                PackageInfo packageInfo = this.y.f3573d.get(i3);
                if (packageInfo != null && packageInfo.packageName.equals(str) && UserHandle.getUserId(packageInfo.applicationInfo.uid) == i2) {
                    this.y.f3573d.remove(i3);
                    break;
                }
                i3++;
            } else {
                break;
            }
        }
        F();
    }

    private void c(List<com.miui.appmanager.c.k> list) {
        this.F = 0;
        this.M = 0;
        this.N = this.M + 20;
        if (this.N > list.size()) {
            this.N = list.size();
        }
    }

    /* access modifiers changed from: private */
    public boolean c(String str) {
        if (this.da) {
            return true;
        }
        try {
            return new JSONObject(str).optBoolean("settingsShowAd", false);
        } catch (JSONException e2) {
            Log.e("AppManagerMainActivity", "getSettingsShowAd error", e2);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void d(String str) {
        r rVar;
        C();
        for (com.miui.appmanager.c.k kVar : this.E ? this.y.f3572c : this.y.f3571b) {
            com.miui.appmanager.c.j jVar = (com.miui.appmanager.c.j) kVar;
            E f2 = jVar.f();
            if (jVar.d().toLowerCase().indexOf(str.toLowerCase()) >= 0 || (f2 != null && (f2.f3574a.toString().toLowerCase().startsWith(str.toLowerCase()) || f2.f3575b.toString().toLowerCase().contains(str.toLowerCase())))) {
                this.z.add(kVar);
                jVar.c(str);
            }
        }
        this.K.a(getResources().getQuantityString(R.plurals.found_apps_title, this.z.size(), new Object[]{Integer.valueOf(this.z.size())}));
        this.l.b();
        this.l.a((com.miui.appmanager.c.k) this.K);
        this.l.a(this.z);
        if (!this.E && (rVar = this.J) != null) {
            this.l.a((com.miui.appmanager.c.k) rVar);
        }
        this.l.notifyDataSetChanged();
    }

    private void d(List<com.miui.appmanager.c.k> list) {
        this.U = false;
        new d(this, new ArrayList(list)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void e(List<com.miui.appmanager.c.k> list) {
        Comparator<com.miui.appmanager.c.k> comparator;
        int i2 = this.G;
        if (i2 == 0) {
            comparator = e;
        } else if (i2 == 1) {
            comparator = f3490b;
        } else if (i2 == 2) {
            comparator = f3491c;
        } else if (i2 == 3) {
            comparator = f3492d;
        } else if (i2 == 4) {
            comparator = f;
        } else {
            return;
        }
        Collections.sort(list, comparator);
    }

    private void f(List<com.miui.appmanager.c.k> list) {
        this.pa = new i(this, new ArrayList(list));
        this.pa.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* access modifiers changed from: private */
    public void g(List<com.miui.appmanager.c.k> list) {
        new j(this, list).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void h(List<com.miui.appmanager.c.k> list) {
        if (list != null) {
            this.S = false;
            if (!list.isEmpty()) {
                this.ka = new k(this, list);
                this.ka.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                return;
            }
            this.T = true;
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [com.miui.appmanager.b$a, android.content.Context, android.view.View$OnClickListener, miui.app.Activity, com.miui.appmanager.AppManagerMainActivity] */
    private void initData() {
        int b2;
        this.v = (UsageStatsManager) getSystemService("usagestats");
        this.r = getPackageManager();
        this.s = new i(this);
        this.t = (UserManager) getSystemService("user");
        this.E = this.s.d();
        this.l = new C0319b(this);
        this.l.a((C0319b.a) this);
        this.k = (RecyclerView) findViewById(R.id.app_manager_list_view);
        this.k.setLayoutManager(new LinearLayoutManager(this));
        this.k.setAdapter(this.l);
        this.k.setOnScrollListener(new u(this));
        this.g = findViewById(R.id.am_search_view);
        this.j = (TextView) this.g.findViewById(16908297);
        this.g.setOnClickListener(this);
        this.h = (AMMainTopView) findViewById(R.id.top_view);
        this.i = findViewById(R.id.anim_view);
        this.I.a(this.ya);
        this.p = new com.miui.appmanager.widget.d(this);
        this.o = findViewById(R.id.am_progressBar);
        x();
        if (!this.W && (b2 = AppManageUtils.b()) <= 3) {
            AppManageUtils.c(b2 + 1);
        }
        t();
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.appmanager.AppManagerMainActivity] */
    private void o() {
        AlertDialog alertDialog = this.q;
        if (alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_manager_reset_app_preferences_title);
            builder.setMessage(R.string.app_manager_reset_app_preferences_desc);
            builder.setPositiveButton(R.string.app_manager_reset_app_preferences_button, new t(this));
            builder.setNegativeButton(R.string.app_manager_dlg_cancel, (DialogInterface.OnClickListener) null);
            this.q = builder.show();
            return;
        }
        alertDialog.show();
    }

    private boolean p() {
        return com.miui.securitycenter.h.i();
    }

    private void q() {
        k kVar = this.ka;
        if (kVar != null) {
            kVar.cancel(true);
        }
        b bVar = this.la;
        if (bVar != null) {
            bVar.cancel(true);
        }
        g gVar = this.oa;
        if (gVar != null) {
            gVar.cancel(true);
        }
        i iVar = this.pa;
        if (iVar != null) {
            iVar.cancel(true);
        }
        e eVar = this.qa;
        if (eVar != null) {
            eVar.cancel(true);
        }
        c cVar = this.ma;
        if (cVar != null) {
            cVar.cancelLoad();
        }
    }

    /* access modifiers changed from: private */
    public String r() {
        int i2 = this.G;
        if (i2 == 0) {
            return NetworkDiagnosticsUtils.MIDROP_APHOST_STATE_RUNNING;
        }
        if (i2 == 1) {
            return "app_name";
        }
        if (i2 == 2) {
            return "frequency";
        }
        if (i2 == 3) {
            return "storage";
        }
        if (i2 != 4) {
            return null;
        }
        return "installtime";
    }

    /* access modifiers changed from: private */
    public SparseArray<Map<String, h>> s() {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) getSystemService("activity")).getRunningAppProcesses();
        SparseArray<Map<String, h>> sparseArray = new SparseArray<>();
        for (UserHandle identifier : this.t.getUserProfiles()) {
            sparseArray.put(identifier.getIdentifier(), new HashMap());
        }
        for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
            int userId = UserHandle.getUserId(next.uid);
            Map map = sparseArray.get(userId);
            if (map != null && next.pkgList != null) {
                int i2 = 0;
                while (true) {
                    String[] strArr = next.pkgList;
                    if (i2 >= strArr.length) {
                        break;
                    }
                    if (!miui.os.Build.IS_CU_CUSTOMIZATION_TEST || !"com.mobiletools.systemhelper".equals(strArr[i2])) {
                        if (map.containsKey(next.pkgList[i2])) {
                            h hVar = (h) map.get(next.pkgList[i2]);
                            hVar.f3509a.add(Integer.valueOf(next.pid));
                            map.remove(next.pkgList[i2]);
                            map.put(next.pkgList[i2], hVar);
                        } else {
                            ArrayList arrayList = new ArrayList();
                            arrayList.add(Integer.valueOf(next.pid));
                            map.put(next.pkgList[i2], new h(arrayList));
                        }
                        sparseArray.put(userId, map);
                    }
                    i2++;
                }
            }
        }
        return sparseArray;
    }

    private void t() {
        this.ja = new ArrayList();
        int[] iArr = {R.drawable.am_drop_item_status, R.drawable.am_drop_item_app_name, R.drawable.am_drop_item_frequency, R.drawable.am_drop_item_storage, R.drawable.am_drop_item_installtime};
        int[] iArr2 = {R.drawable.am_drop_item_status_selected, R.drawable.am_drop_item_app_name_selected, R.drawable.am_drop_item_frequency_selected, R.drawable.am_drop_item_storage_selected, R.drawable.am_drop_item_installtime_selected};
        String[] stringArray = getResources().getStringArray(R.array.sort_type);
        for (int i2 = 0; i2 < iArr.length; i2++) {
            this.ja.add(new com.miui.appmanager.widget.a(iArr[i2], iArr2[i2], stringArray[i2]));
        }
    }

    private void u() {
        SpannableString spannableString = new SpannableString(getString(R.string.app_manager_open_show_all_apps_immediate));
        spannableString.setSpan(new o(this), 0, spannableString.length(), 33);
        this.J.a(spannableString);
    }

    private boolean v() {
        String stringExtra = getIntent().getStringExtra("enter_way");
        if (!TextUtils.isEmpty(stringExtra)) {
            com.miui.appmanager.a.a.a(stringExtra);
        } else {
            com.miui.appmanager.a.a.a("other");
        }
        return "com.miui.securitycenter".equals(stringExtra);
    }

    private void w() {
        this.qa = new e(this, new ArrayList(this.y.f3572c));
        this.qa.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.appmanager.AppManagerMainActivity] */
    private void x() {
        if (!miui.os.Build.IS_INTERNATIONAL_BUILD) {
            this.ia.add(new ComponentName("com.google.android.gms", "com.google.android.gms.app.settings.GoogleSettingsActivity"));
            this.ia.add(new ComponentName("com.google.android.gms", "com.google.android.gms.common.settings.GoogleSettingsActivity"));
        }
        this.ia.add(new ComponentName("com.qualcomm.qti.modemtestmode", "com.qualcomm.qti.modemtestmode.MbnFileActivate"));
        this.ia.add(new ComponentName("com.google.android.googlequicksearchbox", "com.google.android.googlequicksearchbox.SearchActivity"));
        this.ia.add(new ComponentName("com.google.android.googlequicksearchbox", "com.google.android.handsfree.HandsFreeLauncherActivity"));
        this.ia.add(new ComponentName("com.google.android.inputmethod.pinyin", "com.google.android.apps.inputmethod.libs.framework.core.LauncherActivity"));
        this.ia.add(new ComponentName("com.opera.max.oem.xiaomi", "com.opera.max.ui.v2.MainActivity"));
        this.ia.add(new ComponentName("com.google.android.inputmethod.latin", "com.android.inputmethod.latin.setup.SetupActivity"));
        if (ThemeManagerHelper.needDisableTheme(this)) {
            this.ia.add(new ComponentName("com.android.thememanager", "com.android.thememanager.ThemeResourceTabActivity"));
        }
    }

    /* access modifiers changed from: private */
    public SparseArray<Map<String, Long>> y() {
        SparseArray<Map<String, Long>> sparseArray = new SparseArray<>();
        for (UserHandle identifier : this.t.getUserProfiles()) {
            int identifier2 = identifier.getIdentifier();
            sparseArray.put(identifier2, AppManageUtils.a(this.v, identifier2));
        }
        return sparseArray;
    }

    /* access modifiers changed from: private */
    public void z() {
        b.c.a.b.f fVar = this.w;
        if (fVar != null) {
            fVar.b();
        }
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<D> loader, D d2) {
        if (d2 != null) {
            this.R = true;
            this.h.setLabelVisible(this.aa);
            this.h.setUpdateNum(this.ea);
            this.h.b();
            this.h.a(com.miui.permcenter.privacymanager.behaviorrecord.o.b());
            this.A = getResources().getStringArray(R.array.sort_title);
            this.I.a(this.A[this.G]);
            this.y = d2;
            if (!miui.os.Build.IS_INTERNATIONAL_BUILD && this.da) {
                this.fa.clear();
                this.fa.addAll(this.y.f3570a);
            }
            int i2 = this.G;
            if (i2 == 1 || i2 == 0) {
                F();
            }
            this.ga = new ArrayList(this.y.f3572c);
            this.ha = new ArrayList(this.y.f3571b);
            c(this.E ? this.ga : this.ha);
            this.T = false;
            d((List<com.miui.appmanager.c.k>) this.E ? this.y.f3572c : this.y.f3571b);
            h(a(this.E ? this.ga : this.ha, this.M, this.N));
            w();
            this.o.setVisibility(8);
            if (!this.ba && !isFinishing()) {
                A();
                this.ba = true;
            }
            u();
        }
    }

    public void a(String str, com.miui.appmanager.c.c cVar) {
        if (!cVar.l()) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new C0536b.c(str, cVar));
            C0536b.a(getApplicationContext(), (List<Object>) arrayList);
        }
    }

    public void a(SearchActionMode.Callback callback) {
        this.sa = startActionMode(callback);
    }

    public E b(String str) {
        String f2;
        ArrayList arrayList;
        E e2 = new E();
        if (!(str == null || (f2 = AppManageUtils.f(str)) == null || (arrayList = ChinesePinyinConverter.getInstance().get(f2)) == null || arrayList.size() <= 0)) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ChinesePinyinConverter.Token token = (ChinesePinyinConverter.Token) it.next();
                String str2 = token.target;
                if (str2 != null && str2.length() > 0) {
                    e2.f3574a.append(token.target);
                    e2.f3575b.append(token.target.charAt(0));
                }
            }
        }
        return e2;
    }

    public void l() {
        if (this.sa != null) {
            this.sa = null;
        }
    }

    public boolean m() {
        return this.sa != null;
    }

    public void n() {
        if (!this.fa.isEmpty()) {
            this.fa.clear();
            F();
            this.ca = true;
        }
    }

    public void onClick(View view) {
        if (view == this.g) {
            a(this.ta);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        AppManagerMainActivity.super.onConfigurationChanged(configuration);
        com.miui.appmanager.widget.d dVar = this.p;
        if (dVar != null) {
            dVar.a();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        this.da = v();
        if (!this.da) {
            C.a((Context) Application.d());
        }
        super.onCreate(bundle);
        this.C = new f(this);
        setContentView(R.layout.app_manager_activity_main);
        if (bundle != null) {
            this.G = bundle.getInt("current_sory_type");
        }
        this.W = "zh_CN".equals(Locale.getDefault().toString());
        initData();
        Loader loader = getLoaderManager().getLoader(121);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(121, (Bundle) null, this);
        if (!(Build.VERSION.SDK_INT < 24 || bundle == null || loader == null)) {
            loaderManager.restartLoader(121, (Bundle) null, this);
        }
        this.na = b.b.g.a.a();
        this.na.a(this);
        boolean z2 = miui.os.Build.IS_INTERNATIONAL_BUILD;
        if (z2 || (!z2 && !this.da)) {
            this.la = new b(this);
            this.la.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    public Loader<D> onCreateLoader(int i2, Bundle bundle) {
        this.ma = new c(this);
        return this.ma;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.am_homepage_option, menu);
        this.m = menu.findItem(R.id.am_show_system);
        this.n = menu.findItem(R.id.am_hide_system);
        G();
        return true;
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, miui.app.Activity, com.miui.appmanager.AppManagerMainActivity, java.lang.Object] */
    public void onDestroy() {
        AppManagerMainActivity.super.onDestroy();
        D();
        if (this.ba) {
            E();
        }
        this.C.removeMessages(0);
        this.C.removeMessages(2);
        for (int i2 = 0; i2 < this.fa.size(); i2++) {
            com.miui.appmanager.c.k kVar = this.fa.get(i2);
            if (kVar != null && (kVar instanceof com.miui.appmanager.c.c)) {
                com.miui.appmanager.c.c cVar = (com.miui.appmanager.c.c) kVar;
                C.a(cVar.e());
                this.na.b(cVar.e());
            }
        }
        if ((!this.s.b() || this.ca) && !TextUtils.isEmpty(this.B)) {
            com.miui.securityscan.i.h.a(this, "app_manager_adv");
        }
        q();
        C.c();
        this.na.c(this);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, miui.app.Activity, com.miui.appmanager.AppManagerMainActivity] */
    public void onItemClick(int i2) {
        com.miui.appmanager.c.k a2 = this.l.a(i2);
        if (a2 instanceof com.miui.appmanager.c.j) {
            com.miui.appmanager.c.j jVar = (com.miui.appmanager.c.j) a2;
            Intent intent = new Intent(this, ApplicationsDetailsActivity.class);
            intent.putExtra("package_name", jVar.e());
            intent.putExtra("miui.intent.extra.USER_ID", UserHandle.getUserId(jVar.i()));
            intent.putExtra("size", jVar.h());
            intent.putExtra("enter_from_appmanagermainactivity", true);
            intent.putExtra("enter_way", !m() ? "00001" : "00002");
            startActivity(intent);
        }
    }

    public void onLoaderReset(Loader<D> loader) {
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, com.miui.appmanager.AppManagerMainActivity] */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onOptionsItemSelected(android.view.MenuItem r3) {
        /*
            r2 = this;
            int r0 = r3.getItemId()
            switch(r0) {
                case 2131296375: goto L_0x002d;
                case 2131296398: goto L_0x001c;
                case 2131296410: goto L_0x0018;
                case 2131296414: goto L_0x0008;
                case 2131296416: goto L_0x001c;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x004a
        L_0x0008:
            android.content.Intent r0 = new android.content.Intent
            java.lang.Class<com.miui.appmanager.AppManagerSettings> r1 = com.miui.appmanager.AppManagerSettings.class
            r0.<init>(r2, r1)
            r2.startActivity(r0)
            java.lang.String r0 = "settings"
        L_0x0014:
            com.miui.appmanager.a.a.b((java.lang.String) r0)
            goto L_0x004a
        L_0x0018:
            r2.o()
            goto L_0x004a
        L_0x001c:
            r0 = 1
            r2.Y = r0
            r1 = 0
            r3.setVisible(r1)
            r2.a((boolean) r0)
            boolean r0 = r2.E
            if (r0 == 0) goto L_0x004a
            java.lang.String r0 = "system_app"
            goto L_0x0014
        L_0x002d:
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 25
            if (r0 <= r1) goto L_0x0040
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "android.settings.MANAGE_DEFAULT_APPS_SETTINGS"
            r0.<init>(r1)
            java.lang.String r1 = "com.android.settings"
            r0.setPackage(r1)
            goto L_0x0047
        L_0x0040:
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "miui.intent.action.PREFERRED_APPLICATION_SETTINGS"
            r0.<init>(r1)
        L_0x0047:
            r2.startActivity(r0)
        L_0x004a:
            boolean r3 = com.miui.appmanager.AppManagerMainActivity.super.onOptionsItemSelected(r3)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.appmanager.AppManagerMainActivity.onOptionsItemSelected(android.view.MenuItem):boolean");
    }

    public void onPause() {
        super.onPause();
        D();
        this.D = true;
        this.C.removeMessages(1);
        AlertDialog alertDialog = this.q;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.q.dismiss();
        }
    }

    public void onResume() {
        super.onResume();
        this.C.removeMessages(1);
        this.C.sendEmptyMessageDelayed(1, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        if (this.D) {
            c((List<com.miui.appmanager.c.k>) this.E ? this.y.f3572c : this.y.f3571b);
            h(a(this.E ? this.ga : this.ha, this.M, this.N));
            boolean a2 = this.s.a();
            int d2 = M.d();
            if (!(this.h.a() == a2 && this.h.getUpdateNum() == d2)) {
                this.h.setLabelVisible(a2);
                this.h.setUpdateNum(d2);
                this.h.b();
            }
            this.h.a(com.miui.permcenter.privacymanager.behaviorrecord.o.b());
            f((List<com.miui.appmanager.c.k>) this.E ? this.y.f3572c : this.y.f3571b);
        }
        D();
    }

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("current_sory_type", this.G);
    }

    public void onStop() {
        AppManagerMainActivity.super.onStop();
        D();
    }
}
