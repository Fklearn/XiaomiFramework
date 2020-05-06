package com.miui.gamebooster.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.c.j.i;
import com.miui.applicationlock.c.K;
import com.miui.applicationlock.c.o;
import com.miui.gamebooster.a.v;
import com.miui.gamebooster.a.x;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0374e;
import com.miui.gamebooster.m.C0382m;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.C0391w;
import com.miui.gamebooster.m.C0393y;
import com.miui.gamebooster.m.V;
import com.miui.gamebooster.m.Z;
import com.miui.gamebooster.m.ga;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.model.C0397c;
import com.miui.gamebooster.model.C0398d;
import com.miui.gamebooster.model.D;
import com.miui.gamebooster.model.E;
import com.miui.gamebooster.model.m;
import com.miui.gamebooster.model.n;
import com.miui.gamebooster.service.IGameBooster;
import com.miui.gamebooster.service.M;
import com.miui.gamebooster.service.MiuiVpnManageServiceCallback;
import com.miui.gamebooster.view.n;
import com.miui.gamebooster.xunyou.h;
import com.miui.networkassistant.utils.DateUtil;
import com.miui.networkassistant.vpn.miui.IMiuiVpnManageService;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.l;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.app.AlertDialog;
import miui.app.ProgressDialog;
import miui.os.Build;
import miui.security.SecurityManager;
import miui.util.IOUtils;
import org.json.JSONObject;

public class N extends b.b.c.c.b.d implements x.a, m, com.miui.gamebooster.xunyou.b {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f4939a = "N";
    /* access modifiers changed from: private */
    public boolean A;
    /* access modifiers changed from: private */
    public boolean B;
    /* access modifiers changed from: private */
    public String C;
    /* access modifiers changed from: private */
    public String D;
    /* access modifiers changed from: private */
    public boolean E;
    /* access modifiers changed from: private */
    public Boolean F = true;
    /* access modifiers changed from: private */
    public Boolean G = true;
    /* access modifiers changed from: private */
    public M H;
    /* access modifiers changed from: private */
    public M I;
    private long J;
    private AlertDialog K;
    private com.miui.gamebooster.viewPointwidget.c L;
    private h M;
    private boolean N;
    private Set<com.miui.gamebooster.viewPointwidget.b> O;
    private n P;
    private boolean Q;
    private boolean R;
    private ServiceConnection S;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public PackageManager f4940b;

    /* renamed from: c  reason: collision with root package name */
    private SecurityManager f4941c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public MainTopContentFrame f4942d;
    private View e;
    /* access modifiers changed from: private */
    public ViewPager f;
    /* access modifiers changed from: private */
    public ViewPager.OnPageChangeListener g;
    private View h;
    private View i;
    private View j;
    private View k;
    /* access modifiers changed from: private */
    public x l;
    private Runnable m;
    private Object n;
    /* access modifiers changed from: private */
    public IMiuiVpnManageService o;
    /* access modifiers changed from: private */
    public e p;
    /* access modifiers changed from: private */
    public d q;
    private AlertDialog r;
    private AlertDialog s;
    private AlertDialog t;
    /* access modifiers changed from: private */
    public ProgressDialog u;
    private LocalBroadcastManager v;
    private BroadcastReceiver w;
    private ArrayList<String> x = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<C0398d> y;
    /* access modifiers changed from: private */
    public ApplicationInfo z;

    public interface a {
        void a();
    }

    private static class b extends AsyncTask<Object, E, E> {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<N> f4943a;

        public b(N n) {
            this.f4943a = new WeakReference<>(n);
        }

        private void a(E e, N n) {
            IGameBooster n2;
            List<D> a2 = e.a();
            if (a2 != null && a2.size() != 0) {
                for (int i = 0; i < a2.size(); i++) {
                    D d2 = a2.get(i);
                    if (d2.d() == 2) {
                        long longValue = ga.a(d2.e(), "yyyy-MM-dd HH:mm:ss").longValue();
                        long longValue2 = ga.a(d2.b(), "yyyy-MM-dd HH:mm:ss").longValue();
                        long currentTimeMillis = System.currentTimeMillis();
                        if (currentTimeMillis > longValue && currentTimeMillis < longValue2) {
                            n.f4942d.setBusinessText(d2.a());
                            String unused = n.D = d2.a();
                        }
                    } else if (d2.d() == 1) {
                        com.miui.common.persistence.b.b("gb_notification_business_period", d2.c());
                        Activity activity = n.getActivity();
                        if (!(activity == null || (n2 = ((GameBoosterRealMainActivity) activity).n()) == null)) {
                            try {
                                n2.K();
                            } catch (RemoteException e2) {
                                Log.e("LoadXunyouDataTask", "RemoteException" + e2);
                            }
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(E e) {
            N n;
            if (e != null && (n = (N) this.f4943a.get()) != null && !n.isDetached()) {
                a(e, n);
            }
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onProgressUpdate(E... eArr) {
            E e;
            super.onProgressUpdate(eArr);
            N n = (N) this.f4943a.get();
            if (n != null && !n.isDetached() && (e = eArr[0]) != null) {
                a(e, n);
            }
        }

        /* access modifiers changed from: protected */
        public E doInBackground(Object... objArr) {
            Process.setThreadPriority(19);
            N n = (N) this.f4943a.get();
            E e = null;
            if (n == null || n.isDetached() || isCancelled() || !n.isAdded() || n.mAppContext == null) {
                return null;
            }
            Context applicationContext = n.mAppContext.getApplicationContext();
            if (!K.c(applicationContext)) {
                return null;
            }
            try {
                if (!com.miui.securitycenter.h.i()) {
                    Process.setThreadPriority(0);
                    return null;
                }
                String b2 = C0382m.b("gamebooster", "gbxunyoubusiness", applicationContext);
                if (!TextUtils.isEmpty(b2)) {
                    e = E.a(new JSONObject(b2));
                }
                publishProgress(new E[]{e});
                String a2 = E.a((Map<String, String>) new HashMap(), applicationContext);
                E a3 = E.a(new JSONObject(a2));
                if (a3 == null) {
                    C0382m.a("gamebooster", "gbxunyoubusiness", "", applicationContext);
                    Process.setThreadPriority(0);
                    return e;
                }
                if (!a3.a().isEmpty()) {
                    C0382m.a("gamebooster", "gbxunyoubusiness", a2, applicationContext);
                    e = a3;
                }
                Process.setThreadPriority(0);
                return e;
            } catch (Exception e2) {
                Log.e("LoadXunyouDataTask", "msg", e2);
            } catch (Throwable th) {
                Process.setThreadPriority(0);
                throw th;
            }
        }
    }

    private static class c extends AsyncTask<Void, ArrayList<C0398d>, ArrayList<C0398d>> {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<N> f4944a;

        public c(N n) {
            this.f4944a = new WeakReference<>(n);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public ArrayList<C0398d> doInBackground(Void... voidArr) {
            N n = (N) this.f4944a.get();
            if (n == null || n.isDetached() || isCancelled()) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            C0391w.a(n.mAppContext);
            ArrayList<ApplicationInfo> arrayList2 = new ArrayList<>();
            ArrayList arrayList3 = new ArrayList();
            String a2 = C0374e.a(n.mAppContext, "top_200_games.json");
            if (a2 != null && a2.length() > 0) {
                C0393y.a(n.f4940b, (List<ApplicationInfo>) arrayList2);
                for (ApplicationInfo applicationInfo : arrayList2) {
                    if (b.b.c.j.x.a(applicationInfo)) {
                        if (a2.contains(applicationInfo.packageName) || n.a(applicationInfo)) {
                            String a3 = b.b.c.j.x.a(n.mAppContext, applicationInfo);
                            C0391w.a(n.mAppContext, a3, applicationInfo.packageName, applicationInfo.uid, 0);
                            arrayList.add(new C0398d(applicationInfo, true, a3, applicationInfo.loadIcon(n.f4940b)));
                        } else {
                            arrayList3.add(applicationInfo);
                        }
                    }
                }
            }
            publishProgress(new ArrayList[]{arrayList});
            return C0393y.a(n.mAppContext, n.f4940b, (List<ApplicationInfo>) arrayList3);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(ArrayList<C0398d> arrayList) {
            super.onPostExecute(arrayList);
            N n = (N) this.f4944a.get();
            if (n != null && !n.isDetached()) {
                if (arrayList.size() > 0) {
                    n.y.addAll(n.y.size() == 0 ? 0 : n.y.size() - 1, arrayList);
                    n.A();
                    n.B();
                }
                N.b(n.mAppContext, n.y.size());
            }
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onProgressUpdate(ArrayList<C0398d>... arrayListArr) {
            super.onProgressUpdate(arrayListArr);
            N n = (N) this.f4944a.get();
            if (n != null && !n.isDetached()) {
                n.y.clear();
                n.y.addAll(arrayListArr[0]);
                n.A();
            }
        }
    }

    public static class d extends b.b.c.i.b {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<N> f4945a;

        public d(N n) {
            this.f4945a = new WeakReference<>(n);
        }

        public void handleMessage(Message message) {
            N n = (N) this.f4945a.get();
            if (n != null) {
                GameBoosterRealMainActivity gameBoosterRealMainActivity = (GameBoosterRealMainActivity) n.getActivity();
                super.handleMessage(message);
                switch (message.what) {
                    case 109:
                        n.l();
                        return;
                    case 110:
                        if (gameBoosterRealMainActivity != null) {
                            if (((Integer) message.obj).intValue() == 100) {
                                if (!gameBoosterRealMainActivity.isFinishing() && n.u != null && n.u.isShowing()) {
                                    n.u.dismiss();
                                }
                                C0393y.a(n.mAppContext, gameBoosterRealMainActivity.n(), n.z.packageName, B.e(n.z.uid / DefaultOggSeeker.MATCH_BYTE_RANGE));
                                return;
                            }
                            n.u.setProgress(((Integer) message.obj).intValue());
                            return;
                        }
                        return;
                    case 111:
                        if (n.C != null) {
                            boolean unused = n.E = true;
                            if (gameBoosterRealMainActivity != null) {
                                C0393y.a(n.mAppContext, n.C, n.getResources().getString(R.string.xunyou_pay_webview));
                                return;
                            }
                            return;
                        }
                        return;
                    case 112:
                        n.m();
                        return;
                    case 114:
                        n.h();
                        return;
                    default:
                        return;
                }
            }
        }
    }

    private static class e extends MiuiVpnManageServiceCallback {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<N> f4946a;

        public e(N n) {
            this.f4946a = new WeakReference<>(n);
        }

        public boolean isVpnConnected() {
            return super.isVpnConnected();
        }

        /* JADX WARNING: Removed duplicated region for block: B:23:0x0077  */
        /* JADX WARNING: Removed duplicated region for block: B:24:0x007b A[ADDED_TO_REGION] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onVpnStateChanged(int r6, int r7, java.lang.String r8) {
            /*
                r5 = this;
                super.onVpnStateChanged(r6, r7, r8)
                java.lang.ref.WeakReference<com.miui.gamebooster.ui.N> r0 = r5.f4946a
                java.lang.Object r0 = r0.get()
                com.miui.gamebooster.ui.N r0 = (com.miui.gamebooster.ui.N) r0
                if (r0 != 0) goto L_0x000e
                return
            L_0x000e:
                com.miui.networkassistant.vpn.miui.IMiuiVpnManageService r1 = r0.o
                if (r1 != 0) goto L_0x0015
                return
            L_0x0015:
                com.miui.gamebooster.ui.O r1 = new com.miui.gamebooster.ui.O
                r1.<init>(r5, r0, r7, r8)
                b.b.c.c.a.a.a(r1)
                com.miui.gamebooster.service.M r1 = r0.I
                com.miui.gamebooster.service.M r2 = com.miui.gamebooster.service.M.CONNECTVPN
                r3 = 0
                r4 = 1
                if (r1 != r2) goto L_0x0041
                r1 = 1001(0x3e9, float:1.403E-42)
                if (r7 != r1) goto L_0x0041
                java.lang.Boolean r1 = java.lang.Boolean.valueOf(r4)
                r0.c((java.lang.Boolean) r1)
                java.lang.String r1 = com.miui.gamebooster.ui.N.f4939a
                java.lang.String r2 = "vpn booster success"
            L_0x0038:
                android.util.Log.i(r1, r2)
                com.miui.gamebooster.service.M r1 = com.miui.gamebooster.service.M.INIT
                com.miui.gamebooster.service.M unused = r0.I = r1
                goto L_0x005b
            L_0x0041:
                com.miui.gamebooster.service.M r1 = r0.I
                com.miui.gamebooster.service.M r2 = com.miui.gamebooster.service.M.CONNECTVPN
                if (r1 != r2) goto L_0x005b
                r1 = 1002(0x3ea, float:1.404E-42)
                if (r7 != r1) goto L_0x005b
                java.lang.Boolean r1 = java.lang.Boolean.valueOf(r3)
                r0.c((java.lang.Boolean) r1)
                java.lang.String r1 = com.miui.gamebooster.ui.N.f4939a
                java.lang.String r2 = "vpn booster failed"
                goto L_0x0038
            L_0x005b:
                java.lang.String r0 = "gamebooster_xunyou_cache_expire"
                r1 = 102(0x66, float:1.43E-43)
                if (r7 != r1) goto L_0x006c
                r2 = 5
                java.lang.String r2 = java.lang.String.valueOf(r2)
                boolean r2 = r2.equals(r8)
                if (r2 != 0) goto L_0x0077
            L_0x006c:
                r2 = 3
                java.lang.String r2 = java.lang.String.valueOf(r2)
                boolean r2 = r2.equals(r8)
                if (r2 == 0) goto L_0x007b
            L_0x0077:
                com.miui.common.persistence.b.b((java.lang.String) r0, (boolean) r3)
                goto L_0x00a6
            L_0x007b:
                if (r7 != r1) goto L_0x00a6
                r1 = 4
                java.lang.String r1 = java.lang.String.valueOf(r1)
                boolean r1 = r1.equals(r8)
                if (r1 != 0) goto L_0x009e
                r1 = 2
                java.lang.String r1 = java.lang.String.valueOf(r1)
                boolean r1 = r1.equals(r8)
                if (r1 != 0) goto L_0x009e
                r1 = 6
                java.lang.String r1 = java.lang.String.valueOf(r1)
                boolean r1 = r1.equals(r8)
                if (r1 == 0) goto L_0x00a6
            L_0x009e:
                com.miui.common.persistence.b.b((java.lang.String) r0, (boolean) r4)
                java.lang.String r0 = "gamebooster_xunyou_cache_user_type"
                com.miui.common.persistence.b.b((java.lang.String) r0, (java.lang.String) r8)
            L_0x00a6:
                java.lang.String r0 = com.miui.gamebooster.ui.N.f4939a
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "VpnType:"
                r1.append(r2)
                r1.append(r6)
                java.lang.String r6 = " "
                r1.append(r6)
                java.lang.String r2 = "VpnState:"
                r1.append(r2)
                r1.append(r7)
                r1.append(r6)
                java.lang.String r6 = "Vpndata:"
                r1.append(r6)
                r1.append(r8)
                java.lang.String r6 = r1.toString()
                android.util.Log.i(r0, r6)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.ui.N.e.onVpnStateChanged(int, int, java.lang.String):void");
        }
    }

    private static class f implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<N> f4947a;

        /* renamed from: b  reason: collision with root package name */
        private Boolean f4948b = false;

        public f(N n) {
            this.f4947a = new WeakReference<>(n);
        }

        public void a(Boolean bool) {
            this.f4948b = bool;
        }

        public void run() {
            N n = (N) this.f4947a.get();
            if (n != null) {
                GameBoosterRealMainActivity gameBoosterRealMainActivity = (GameBoosterRealMainActivity) n.getActivity();
                for (int i = 1; i <= 100 && !this.f4948b.booleanValue() && gameBoosterRealMainActivity != null && !gameBoosterRealMainActivity.isFinishing(); i++) {
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    n.q.a(110, Integer.valueOf(i));
                }
            }
        }
    }

    public N() {
        M m2 = M.NOTINIT;
        this.H = m2;
        this.I = m2;
        this.N = false;
        this.S = new C0454v(this);
    }

    /* access modifiers changed from: private */
    public void A() {
        H h2 = new H(this);
        h hVar = this.M;
        if (hVar == null) {
            h2.a(new ArrayList());
        } else {
            hVar.a((h.a) h2);
        }
    }

    /* access modifiers changed from: private */
    public void B() {
        IGameBooster n2;
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.y.size(); i2++) {
            C0398d dVar = this.y.get(i2);
            if (!(dVar == null || dVar.b() == null)) {
                arrayList.add(dVar.b().packageName);
            }
        }
        this.x.clear();
        this.x.addAll(arrayList);
        com.miui.common.persistence.b.b("gb_added_games", (ArrayList<String>) arrayList);
        Activity activity = getActivity();
        if (activity != null && (n2 = ((GameBoosterRealMainActivity) activity).n()) != null) {
            try {
                n2.b((List<String>) arrayList);
            } catch (RemoteException e2) {
                Log.e(f4939a, e2.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public void W(N n2) {
        new b(n2).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
    }

    private void a(Activity activity) {
        activity.startActivityForResult(l.a(activity, activity.getString(R.string.gamebooster_network_dialog_title), activity.getString(R.string.gamebooster_network_dialog_message), activity.getString(17039360), activity.getString(17039370)), 201);
    }

    private void a(Context context, Activity activity, ArrayList<String> arrayList) {
        Intent intent = new Intent(context, SelectGameActivity.class);
        intent.putStringArrayListExtra("addedGames", arrayList);
        startActivityForResult(intent, 101);
        C0373d.d("tab1_gamebooster", "add_game");
        com.miui.gamebooster.i.a.b.b(context);
    }

    private void a(com.miui.gamebooster.d.f fVar) {
        com.miui.gamebooster.c.a.a(this.mAppContext);
        if (!com.miui.gamebooster.c.a.o(true) && fVar != com.miui.gamebooster.d.f.OVERDUE) {
            fVar = com.miui.gamebooster.d.f.CLOSE;
        }
        MainTopContentFrame mainTopContentFrame = this.f4942d;
        if (mainTopContentFrame != null) {
            mainTopContentFrame.a(fVar);
        }
    }

    /* access modifiers changed from: private */
    public static void b(Context context, int i2) {
        com.miui.gamebooster.c.a.a(context);
        if (com.miui.gamebooster.c.a.e()) {
            Toast.makeText(context, i2 > 0 ? R.string.add_done : R.string.no_game, 1).show();
        }
        com.miui.gamebooster.c.a.a(context);
        com.miui.gamebooster.c.a.H(false);
    }

    /* access modifiers changed from: private */
    public void c(Boolean bool) {
        b.b.c.c.a.a.a(new C0458x(this, bool));
    }

    /* access modifiers changed from: private */
    public void c(boolean z2) {
        if (!this.N && !this.Q) {
            if (z2 && com.miui.common.persistence.b.a("gt_xunyou_net_booster_try_again_dialog_show_again", false)) {
                return;
            }
            if (z2 || !com.miui.common.persistence.b.a("gamebooster_free_send_netbooster_open_nomore", false)) {
                String string = getResources().getString(R.string.free_send_net_booster);
                int i2 = R.string.free_send_net_booster_tip;
                if (z2) {
                    string = getResources().getString(R.string.free_send_net_booster_busniess);
                    i2 = R.string.free_send_net_booster_busniess_tip;
                }
                if (!z2) {
                    C0373d.l("show", "time");
                } else {
                    C0373d.h("show", "time");
                }
                new AlertDialog.Builder(this.mActivity).setTitle(string).setMessage(i2).setNegativeButton(getResources().getString(R.string.cancel), new C0452u(this, z2)).setPositiveButton(getResources().getString(R.string.open_now), new C0450t(this, z2)).setCheckBox(false, getResources().getString(R.string.fingerprint_not_remind)).create().show();
                this.Q = true;
            }
        }
    }

    /* access modifiers changed from: private */
    public void d(boolean z2) {
        Utils.a(z2, this.f4942d);
        Utils.a(!z2, this.e);
    }

    private void f(int i2) {
        C0373d.j("show", "time");
        new AlertDialog.Builder(this.mActivity).setTitle(getResources().getString(R.string.net_booster_expire_notification)).setMessage(getResources().getQuantityString(R.plurals.net_booster_expire_notification_tip, i2, new Object[]{Integer.valueOf(i2)})).setNegativeButton(getResources().getString(R.string.cancel), new C0445q(this)).setPositiveButton(getResources().getString(R.string.renew_now), new C0443p(this)).create().show();
        com.miui.common.persistence.b.b("gb_notification_expired", DateUtil.getDateFormat(2).format(new Date()));
    }

    private void g(int i2) {
        boolean z2 = false;
        int a2 = com.miui.common.persistence.b.a("gb_notification_business_period", 0);
        if (a2 > 0 && i2 > a2) {
            c(true);
            z2 = true;
        }
        a(com.miui.gamebooster.d.f.OVERDUE);
        if (!z2) {
            w();
        }
    }

    private void n() {
        Utils.a(this.k, (Runnable) new C0419d(this, i.f(this.mAppContext)));
    }

    /* access modifiers changed from: private */
    public void o() {
        try {
            this.o.init("xunyou");
            this.H = M.CONNECTVPN;
        } catch (Exception e2) {
            String str = f4939a;
            Log.i(str, "MiuiVpnServiceException:" + e2.toString());
        }
        x();
    }

    private void p() {
        b.b.c.c.a.a.a(new C0456w(this));
    }

    private void q() {
        SharedPreferences sharedPreferences = this.mAppContext.getSharedPreferences("gb_gamead_data_config", 0);
        com.miui.gamebooster.globalgame.util.a.b();
        com.miui.gamebooster.globalgame.util.a.a(getActivity().getApplication(), sharedPreferences);
    }

    private void r() {
        Utils.a((Runnable) new C0413a(this), this.h);
        Utils.a((Runnable) new C0415b(this), this.i);
        Utils.a((Runnable) new C0417c(this), this.j);
    }

    private void s() {
        String str;
        com.miui.gamebooster.c.a.a(this.mAppContext);
        String str2 = "loadGameListFromSql";
        if (com.miui.gamebooster.c.a.e()) {
            if (!com.miui.gamebooster.c.a.a() && !C0388t.s() && !Z.b(this.mAppContext, (String) null)) {
                v();
            }
            if (!C0391w.b(this.mAppContext)) {
                u();
                str = f4939a;
                str2 = "loadLocalGameList";
                Log.i(str, str2);
            }
        }
        t();
        str = f4939a;
        Log.i(str, str2);
    }

    /* access modifiers changed from: private */
    public void t() {
        b.b.c.c.a.a.a(new F(this));
    }

    private void u() {
        new c(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void v() {
        this.t = new AlertDialog.Builder(getActivity()).setTitle(R.string.ask_create_shortcut).setView(R.layout.welcome_icon_dialog).setNegativeButton(getResources().getString(R.string.cancel), new J(this)).setPositiveButton(getResources().getString(R.string.ok), new I(this)).create();
        this.t.show();
    }

    private void w() {
        long a2 = com.miui.common.persistence.b.a("gamebooster_xunyou_cache_time", -1);
        if (a2 > 0) {
            long a3 = com.miui.common.persistence.b.a("gb_notification_overdue_xy_time", -1);
            if (a3 < a2 && a2 <= System.currentTimeMillis()) {
                String str = f4939a;
                Log.i(str, "openNetBoosterOverDueDialog: xunyouOverTime=" + a2 + "\tlastShowOverTime=" + a3);
                C0373d.o("show", "time");
                new AlertDialog.Builder(this.mActivity).setTitle(getResources().getString(R.string.net_booster_overdue_notification)).setMessage(R.string.net_booster_overdue_notification_tip).setNegativeButton(getResources().getString(R.string.cancel), new C0448s(this)).setPositiveButton(getResources().getString(R.string.renew_now), new r(this)).create().show();
                com.miui.common.persistence.b.b("gb_notification_overdue_xy_time", System.currentTimeMillis());
            }
        }
    }

    private void x() {
        f fVar = new f(this);
        this.u = new ProgressDialog(getActivity());
        this.u.setProgressStyle(1);
        this.u.setMessage(getResources().getString(R.string.wifi_optizition_loading));
        this.u.setOnCancelListener(new C0437m(this, fVar));
        this.u.show();
        b.b.c.c.a.a.a(fVar);
    }

    private void y() {
        GameBoosterRealMainActivity gameBoosterRealMainActivity = (GameBoosterRealMainActivity) getActivity();
        if (com.miui.gamebooster.d.a.a() && gameBoosterRealMainActivity != null && !gameBoosterRealMainActivity.m) {
            gameBoosterRealMainActivity.m = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("gb_update_adapter_action");
            this.w = new C0460y(this);
            this.v.registerReceiver(this.w, intentFilter);
            Intent intent = new Intent();
            intent.setPackage("com.miui.securitycenter");
            intent.setAction("com.miui.networkassistant.vpn.MIUI_VPN_MANAGE_SERVICE");
            g.a((Context) getActivity(), intent, this.S, 1, UserHandle.OWNER);
        }
    }

    private void z() {
        new C0462z(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        try {
            if (this.E) {
                this.H = M.REFRESHUSERSTATE;
                this.o.init("xunyou");
                this.E = false;
            }
        } catch (Exception e2) {
            Log.i(f4939a, e2.toString());
        }
    }

    public ApplicationInfo a(String str, int i2) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return (ApplicationInfo) b.b.o.g.e.a(this.n, ApplicationInfo.class, "getApplicationInfo", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Integer.TYPE}, str, 8192, Integer.valueOf(i2));
        } catch (Exception unused) {
            return null;
        }
    }

    public N a(n nVar) {
        this.P = nVar;
        return this;
    }

    public N a(Runnable runnable) {
        this.m = runnable;
        return this;
    }

    public void a(Context context, Activity activity, ArrayList<String> arrayList, C0397c cVar) {
        ApplicationInfo b2 = cVar == null ? null : cVar.b();
        if (cVar == null) {
            a(context, activity, arrayList);
        } else if (b2 != null) {
            V.a("already_added_game", b2.packageName, new ArrayList());
            if (!com.miui.gamebooster.c.a.a(this.mAppContext).k(true)) {
                C0393y.a(context, b2.packageName, B.e(b2.uid / DefaultOggSeeker.MATCH_BYTE_RANGE));
                C0373d.d("tab1_gamebooster", "run_game");
                return;
            }
            if (!com.miui.gamebooster.c.a.y(false) || !cVar.f()) {
                C0393y.a(context, ((GameBoosterRealMainActivity) getActivity()).n(), b2.packageName, B.e(b2.uid / DefaultOggSeeker.MATCH_BYTE_RANGE));
            } else {
                this.z = b2;
                a(this.F);
            }
            C0373d.d("tab1_gamebooster", "run_game");
        } else if (cVar.a() != null) {
            getActivity();
            cVar.a().a();
            throw null;
        }
    }

    /* JADX WARNING: type inference failed for: r4v9, types: [com.miui.gamebooster.ui.GameBoosterRealMainActivity, android.app.Activity] */
    public void a(com.miui.gamebooster.d.g gVar) {
        if (!this.N) {
            if (this.K == null) {
                this.K = new AlertDialog.Builder(this.mActivity).setTitle(R.string.gamebooster_network_dialog_title).setMessage(R.string.gamebooster_network_dialog_message).setPositiveButton(17039370, new C0441o(this, gVar)).setNegativeButton(17039360, new C0439n(this)).create();
            }
            if (!this.K.isShowing()) {
                this.K.show();
            }
        } else if (!this.R) {
            a((Activity) (GameBoosterRealMainActivity) this.mActivity);
            this.R = true;
        }
    }

    public void a(@Nullable C0397c cVar, int i2, boolean z2) {
        if (!Utils.a(this)) {
            int currentItem = this.f.getCurrentItem();
            if (i2 == currentItem) {
                a(this.mAppContext, getActivity(), this.x, cVar);
                com.miui.gamebooster.i.a.b.a(this.mAppContext, (cVar == null || cVar.b() == null || TextUtils.isEmpty(cVar.b().packageName)) ? null : cVar.b().packageName, i2, z2);
            } else if (i2 < currentItem) {
                c(currentItem);
            } else {
                d(currentItem);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void a(Boolean bool) {
        if (!bool.booleanValue() && !com.miui.common.persistence.b.a("gamebooster_netbooster_open_nomore", false)) {
            C0373d.n("show", "time");
            this.s = new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.net_booster_title)).setMessage(R.string.net_booster_tip).setNegativeButton(getResources().getString(R.string.cancel), new L(this)).setPositiveButton(getResources().getString(R.string.open_now), new K(this)).setCheckBox(false, getResources().getString(R.string.fingerprint_not_remind)).create();
            this.s.show();
        } else if (bool.booleanValue()) {
            b(this.G);
        } else if (this.z != null) {
            C0393y.a(this.mAppContext.getApplicationContext(), ((GameBoosterRealMainActivity) getActivity()).n(), this.z.packageName, B.e(this.z.uid / DefaultOggSeeker.MATCH_BYTE_RANGE));
        }
    }

    public boolean a(ApplicationInfo applicationInfo) {
        Cursor cursor = null;
        try {
            cursor = C0391w.a(this.mAppContext, applicationInfo.packageName);
            if (cursor == null) {
                IOUtils.closeQuietly(cursor);
                return false;
            }
            if (cursor.moveToFirst()) {
                IOUtils.closeQuietly(cursor);
                return true;
            }
            IOUtils.closeQuietly(cursor);
            return false;
        } catch (Exception e2) {
            Log.e(f4939a, e2.toString());
        } catch (Throwable th) {
            IOUtils.closeQuietly(cursor);
            throw th;
        }
    }

    /* access modifiers changed from: protected */
    public void b(Boolean bool) {
        if (bool.booleanValue() || com.miui.common.persistence.b.a("gamebooster_netbooster_wifi_open_nomore", false)) {
            if (bool.booleanValue()) {
                b.b.o.f.c.a.a(this.mAppContext).a(true);
            }
            o();
            return;
        }
        C0373d.r("show", "time");
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            View inflate = activity.getLayoutInflater().inflate(R.layout.wifi_optizition_view, (ViewGroup) null);
            CheckBox checkBox = (CheckBox) inflate.findViewById(R.id.gb_checkbox);
            this.r = new AlertDialog.Builder(activity).setTitle(getResources().getString(R.string.wifi_optizition_title)).setView(inflate).setNegativeButton(getResources().getString(R.string.cancel), new C0435l(this, checkBox)).setPositiveButton(getResources().getString(R.string.open_now), new M(this, checkBox)).create();
            this.r.show();
        }
    }

    public void c(int i2) {
        if (!Utils.a(this) && i2 != 0) {
            this.f.setCurrentItem(i2 - 1, true);
        }
    }

    public boolean c() {
        com.miui.gamebooster.viewPointwidget.c cVar = this.L;
        if (cVar instanceof m) {
            return ((m) cVar).c();
        }
        return false;
    }

    public void d() {
        m();
    }

    public void d(int i2) {
        if (!Utils.a(this) && i2 != this.l.getCount() - 1) {
            this.f.setCurrentItem(i2 + 1, true);
        }
    }

    public /* synthetic */ void e(int i2) {
        this.k.getLayoutParams().height = i2;
        this.k.requestLayout();
    }

    /* access modifiers changed from: protected */
    public void f() {
        if (!K.c(this.mAppContext)) {
            if (!o.a(this.f4941c, "com.xiaomi.account")) {
                o.b(this.f4941c, "com.xiaomi.account");
            }
            Bundle bundle = new Bundle();
            Activity activity = this.mActivity;
            if (activity != null) {
                K.a(activity, bundle);
                return;
            }
            return;
        }
        try {
            this.o.init("xunyou");
            this.H = M.GETSETTINGURL;
        } catch (Exception e2) {
            String str = f4939a;
            Log.i(str, "MiuiVpnServiceException:" + e2.toString());
        }
    }

    public com.miui.gamebooster.model.o g() {
        return this.f4942d;
    }

    public void h() {
        if (!com.miui.securitycenter.h.i()) {
            a(com.miui.gamebooster.d.g.CLICKNETBOOSTER);
            return;
        }
        this.E = true;
        f();
        C0373d.d("tab1_gamebooster", "network_speeding");
    }

    public /* synthetic */ void i() {
        a(this.mAppContext, getActivity(), this.x);
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"ClickableViewAccessibility"})
    public void initView() {
        this.f4942d = (MainTopContentFrame) findViewById(R.id.content_frame);
        this.e = findViewById(R.id.boostTitle);
        this.f = findViewById(R.id.viewPager);
        this.h = findViewById(R.id.addGame);
        this.i = findViewById(R.id.setting);
        this.j = findViewById(R.id.playButton);
        this.k = findViewById(R.id.systemStatusBarHolder);
        this.O.add((com.miui.gamebooster.viewPointwidget.b) findViewById(R.id.hardwareInfo));
        n();
        this.f4942d.a(this.m);
        boolean z2 = true;
        Utils.a((ViewStub) findViewById(R.id.gameFeedStub), this.L);
        r();
        this.l = new x(this.mAppContext, this);
        View findViewById = findViewById(R.id.viewPagerContainer);
        View findViewById2 = findViewById(R.id.viewPagerFrame);
        com.miui.gamebooster.view.n.a(findViewById, this.f, findViewById2);
        com.miui.gamebooster.view.n.a(this.j);
        if (na.c()) {
            Utils.b(findViewById(R.id.nameStartBandage), findViewById(R.id.nameEndBandage));
        }
        this.f.setAdapter(this.l);
        this.f.setOffscreenPageLimit(2);
        this.f.setOnTouchListener(new n.b(findViewById2));
        TextView textView = (TextView) findViewById(R.id.gameName);
        Utils.a(this.mAppContext, textView, this.j, this.e);
        this.g = new n.a(this.l, textView, this.j);
        this.f.addOnPageChangeListener(this.g);
        this.f4940b = this.mAppContext.getPackageManager();
        this.f4942d.setEventHandler(this.q);
        s();
        this.B = ((GameBoosterRealMainActivity) getActivity()).l;
        if (!com.miui.gamebooster.d.a.a() || !this.B) {
            z2 = false;
        }
        d(z2);
        if (!com.miui.gamebooster.d.a.a() || !this.B) {
            com.miui.gamebooster.c.a.ea(false);
            if (!com.miui.securitycenter.h.i()) {
                a(com.miui.gamebooster.d.g.NOTHING);
                return;
            }
            return;
        }
        m();
        String str = this.D;
        if (str != null) {
            this.f4942d.setBusinessText(str);
        } else if (com.miui.securitycenter.h.i()) {
            W(this);
        }
    }

    public /* synthetic */ void j() {
        com.miui.gamebooster.model.n nVar = this.P;
        if (nVar != null) {
            nVar.k();
        }
    }

    public /* synthetic */ void k() {
        ViewPager viewPager = this.f;
        if (viewPager != null) {
            int currentItem = viewPager.getCurrentItem();
            a(this.l.a(currentItem), currentItem, false);
        }
    }

    public void l() {
        try {
            this.o.init("xunyou");
            this.H = M.REFRESHUSERSTATE;
        } catch (Exception e2) {
            String str = f4939a;
            Log.i(str, "MiuiVpnServiceException:" + e2.toString());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00c0, code lost:
        if (r5 < java.lang.System.currentTimeMillis()) goto L_0x00c2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void m() {
        /*
            r14 = this;
            java.lang.String r0 = "UTF-8"
            boolean r1 = com.miui.securitycenter.h.i()
            if (r1 != 0) goto L_0x000e
            com.miui.gamebooster.d.g r0 = com.miui.gamebooster.d.g.SETNETBOOSTERSTATUS
            r14.a((com.miui.gamebooster.d.g) r0)
            return
        L_0x000e:
            android.content.Context r1 = r14.mAppContext
            boolean r1 = com.miui.applicationlock.c.K.c(r1)
            r2 = 0
            if (r1 != 0) goto L_0x001b
            com.miui.gamebooster.c.a.ea(r2)
            return
        L_0x001b:
            java.lang.String r1 = "gamebooster_xunyou_cache_time"
            r3 = -1
            long r5 = com.miui.common.persistence.b.a((java.lang.String) r1, (long) r3)
            r7 = 2
            r8 = 0
            java.lang.String r9 = new java.lang.String     // Catch:{ UnsupportedEncodingException -> 0x0039 }
            android.content.Context r10 = r14.mAppContext     // Catch:{ UnsupportedEncodingException -> 0x0039 }
            java.lang.String r10 = com.miui.applicationlock.c.K.d(r10)     // Catch:{ UnsupportedEncodingException -> 0x0039 }
            byte[] r10 = r10.getBytes(r0)     // Catch:{ UnsupportedEncodingException -> 0x0039 }
            byte[] r10 = android.util.Base64.encode(r10, r7)     // Catch:{ UnsupportedEncodingException -> 0x0039 }
            r9.<init>(r10, r0)     // Catch:{ UnsupportedEncodingException -> 0x0039 }
            goto L_0x0044
        L_0x0039:
            r0 = move-exception
            java.lang.String r9 = f4939a
            java.lang.String r0 = r0.toString()
            android.util.Log.i(r9, r0)
            r9 = r8
        L_0x0044:
            java.lang.String r0 = "gb_xiaomi_id_md5_key"
            java.lang.String r10 = com.miui.common.persistence.b.a((java.lang.String) r0, (java.lang.String) r8)
            int r11 = com.miui.networkassistant.utils.DateUtil.getFromNowDayInterval(r5)
            if (r10 == 0) goto L_0x0056
            boolean r10 = r9.equals(r10)
            if (r10 != 0) goto L_0x005a
        L_0x0056:
            com.miui.common.persistence.b.b((java.lang.String) r0, (java.lang.String) r9)
            r5 = r3
        L_0x005a:
            long r9 = java.lang.System.currentTimeMillis()
            int r0 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            r9 = 1
            if (r0 <= 0) goto L_0x0065
            r0 = r9
            goto L_0x0066
        L_0x0065:
            r0 = r2
        L_0x0066:
            java.lang.String r10 = "gamebooster_xunyou_cache_expire"
            boolean r10 = com.miui.common.persistence.b.a((java.lang.String) r10, (boolean) r9)
            int r3 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            r12 = 0
            if (r3 != 0) goto L_0x0079
            com.miui.common.persistence.b.b((java.lang.String) r1, (long) r12)
            r14.p()
            goto L_0x00c5
        L_0x0079:
            int r1 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r1 != 0) goto L_0x008d
            com.miui.gamebooster.d.f r1 = com.miui.gamebooster.d.f.CLOSE
            r14.a((com.miui.gamebooster.d.f) r1)
            r14.c((boolean) r2)
            java.lang.String r1 = f4939a
            java.lang.String r3 = "showTryOutOrTryAgainDialog from GAMEBOOSTER_XUNYOU_CACHE_TIME_NOT_FIRST"
            android.util.Log.i(r1, r3)
            goto L_0x00c5
        L_0x008d:
            if (r0 == 0) goto L_0x00ba
            if (r10 == 0) goto L_0x00c2
            com.miui.gamebooster.d.f r1 = com.miui.gamebooster.d.f.OPEN
            r14.a((com.miui.gamebooster.d.f) r1)
            java.lang.String r1 = "gb_notification_expired"
            java.lang.String r1 = com.miui.common.persistence.b.a((java.lang.String) r1, (java.lang.String) r8)
            int r3 = -r11
            if (r3 >= r7) goto L_0x00c5
            if (r3 <= 0) goto L_0x00c5
            if (r1 == 0) goto L_0x00b6
            java.text.SimpleDateFormat r4 = com.miui.networkassistant.utils.DateUtil.getDateFormat(r7)
            java.util.Date r5 = new java.util.Date
            r5.<init>()
            java.lang.String r4 = r4.format(r5)
            boolean r1 = r1.equals(r4)
            if (r1 != 0) goto L_0x00c5
        L_0x00b6:
            r14.f((int) r3)
            goto L_0x00c5
        L_0x00ba:
            long r3 = java.lang.System.currentTimeMillis()
            int r1 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r1 >= 0) goto L_0x00c5
        L_0x00c2:
            r14.g((int) r11)
        L_0x00c5:
            if (r0 == 0) goto L_0x00ca
            if (r10 == 0) goto L_0x00ca
            r2 = r9
        L_0x00ca:
            com.miui.gamebooster.c.a.ea(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.ui.N.m():void");
    }

    public void onActivityResult(int i2, int i3, Intent intent) {
        if (i3 == -1) {
            b.b.c.c.a.a.a(new B(this));
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.N = Build.IS_INTERNATIONAL_BUILD;
        this.O = new HashSet();
        this.y = new ArrayList<>(1);
        this.f4941c = (SecurityManager) getActivity().getSystemService("security");
        this.v = LocalBroadcastManager.getInstance(this.mAppContext);
        y();
        this.q = new d(this);
        this.p = new e(this);
        q();
        h hVar = null;
        try {
            this.n = b.b.o.g.e.a(Class.forName("android.app.AppGlobals"), "getPackageManager", (Class<?>[]) null, new Object[0]);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        this.L = (!this.N || !com.miui.securitycenter.h.i()) ? null : new v(getActivity(), this.q);
        if (!this.N && com.miui.gamebooster.c.a.y(false) && com.miui.gamebooster.d.a.a()) {
            hVar = new h(this.mAppContext);
        }
        this.M = hVar;
        this.O.addAll(Arrays.asList(new com.miui.gamebooster.viewPointwidget.b[]{this.L, this.M}));
        com.miui.gamebooster.i.a.b.a(this.mAppContext);
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_main_layout;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        super.onDestroy();
        this.v.unregisterReceiver(this.w);
        try {
            this.o.unregisterCallback(this.p);
        } catch (Exception e2) {
            Log.i(f4939a, e2.toString());
        }
        if (!(this.o == null || this.S == null)) {
            getActivity().unbindService(this.S);
        }
        Utils.a(this.l, this.f4942d);
        this.P = null;
        Utils.a(this.L);
        this.O.clear();
    }

    public void onPause() {
        super.onPause();
        Utils.c((Collection<com.miui.gamebooster.viewPointwidget.b>) this.O);
        long currentTimeMillis = (System.currentTimeMillis() - this.J) / 1000;
        if (currentTimeMillis > 0) {
            C0373d.w(currentTimeMillis);
        }
    }

    public void onResume() {
        super.onResume();
        z();
        Utils.d((Collection<com.miui.gamebooster.viewPointwidget.b>) this.O);
        this.J = System.currentTimeMillis();
    }

    public void onStart() {
        super.onStart();
        Utils.e((Collection<com.miui.gamebooster.viewPointwidget.b>) this.O);
    }

    public void onStop() {
        super.onStop();
        Utils.f(this.O);
        b.b.c.c.a.a.a(new D(this, new ArrayList(this.l.b())));
    }
}
