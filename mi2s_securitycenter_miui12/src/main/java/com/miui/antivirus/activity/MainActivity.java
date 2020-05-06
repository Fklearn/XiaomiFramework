package com.miui.antivirus.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import b.b.b.a.b;
import b.b.b.d.k;
import b.b.b.d.m;
import b.b.b.i;
import b.b.b.o;
import b.b.b.p;
import b.b.c.j.B;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.miui.antivirus.model.a;
import com.miui.antivirus.model.e;
import com.miui.antivirus.model.l;
import com.miui.antivirus.result.C0238a;
import com.miui.antivirus.result.C0243f;
import com.miui.antivirus.result.C0244g;
import com.miui.antivirus.result.C0250m;
import com.miui.antivirus.result.K;
import com.miui.antivirus.result.N;
import com.miui.antivirus.result.t;
import com.miui.antivirus.service.GuardService;
import com.miui.antivirus.ui.CustomActionBar;
import com.miui.antivirus.ui.MainActivityView;
import com.miui.antivirus.ui.MainHandleBar;
import com.miui.antivirus.whitelist.j;
import com.miui.common.customview.AdImageView;
import com.miui.guardprovider.VirusObserver;
import com.miui.guardprovider.aidl.UpdateInfo;
import com.miui.guardprovider.b;
import com.miui.permcenter.n;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.w;
import com.miui.securityscan.scanner.ScoreManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.app.Activity;
import miui.app.AlertDialog;
import miui.app.ProgressDialog;
import miui.os.Build;
import miui.text.ExtraTextUtils;
import miui.util.Log;
import miui.widget.SlidingButton;

public class MainActivity extends b.b.c.c.a implements K.c {

    /* renamed from: a  reason: collision with root package name */
    protected static boolean f2666a = false;

    /* renamed from: b  reason: collision with root package name */
    private static final String f2667b = (Build.IS_INTERNATIONAL_BUILD ? "http://api.sec.intl.miui.com/docs/disclaimer/av/en.html" : "http://api.sec.miui.com/docs/disclaimer/av/en.html");

    /* renamed from: c  reason: collision with root package name */
    private static final String f2668c = (Build.IS_INTERNATIONAL_BUILD ? "http://api.sec.intl.miui.com/docs/disclaimer/av/cn.html" : "http://api.sec.miui.com/docs/disclaimer/av/cn.html");
    /* access modifiers changed from: private */
    public b.b.b.c.e A;
    private CustomActionBar B;
    private ContentObserver C = new s(this, this.w);
    /* access modifiers changed from: private */
    public boolean D = false;
    private boolean E = false;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public boolean f2669d = false;
    /* access modifiers changed from: private */
    public boolean e = false;
    /* access modifiers changed from: private */
    public boolean f = false;
    /* access modifiers changed from: private */
    public boolean g = false;
    /* access modifiers changed from: private */
    public AtomicBoolean h = new AtomicBoolean(false);
    private ArrayList<C0238a> i = new ArrayList<>();
    private t j;
    private Runnable k = new l(this);
    /* access modifiers changed from: private */
    public Context l;
    /* access modifiers changed from: private */
    public MainActivityView m;
    private com.miui.guardprovider.b n;
    private b.C0050b o;
    private WifiManager p;
    /* access modifiers changed from: private */
    public h q = h.NORMAL;
    /* access modifiers changed from: private */
    public o r;
    /* access modifiers changed from: private */
    public d s;
    /* access modifiers changed from: private */
    public List<com.miui.antivirus.model.a> t = Collections.synchronizedList(new ArrayList());
    /* access modifiers changed from: private */
    public int u = -1;
    private int v = 0;
    /* access modifiers changed from: private */
    public c w = new c(this);
    private j x;
    /* access modifiers changed from: private */
    public i y;
    private ProgressDialog z;

    private class a extends AsyncTask<Void, Void, Void> {
        private a() {
        }

        /* synthetic */ a(MainActivity mainActivity, l lVar) {
            this();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            Iterator<com.miui.permcenter.a> it = n.b(MainActivity.this.getApplicationContext(), (long) PermissionManager.PERM_ID_READ_NOTIFICATION_SMS).iterator();
            while (it.hasNext()) {
                com.miui.permcenter.a next = it.next();
                if (next.f().get(Long.valueOf(PermissionManager.PERM_ID_READ_NOTIFICATION_SMS)).intValue() == 3) {
                    PermissionManager.getInstance(MainActivity.this.l).setApplicationPermission(PermissionManager.PERM_ID_READ_NOTIFICATION_SMS, 1, next.e());
                }
            }
            return null;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Void voidR) {
            Toast.makeText(MainActivity.this.l, R.string.toast_reject_sms_read_permission_success, 0).show();
            MainActivity.this.r.a(false);
            MainActivity.this.z();
        }
    }

    private static class b extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<MainActivity> f2671a;

        b(MainActivity mainActivity) {
            this.f2671a = new WeakReference<>(mainActivity);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            MainActivity mainActivity = (MainActivity) this.f2671a.get();
            if (mainActivity == null) {
                return null;
            }
            mainActivity.y.a((VirusObserver) new g(mainActivity));
            return null;
        }
    }

    private static class c extends b.b.c.i.b {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<MainActivity> f2672a;

        public c(MainActivity mainActivity) {
            this.f2672a = new WeakReference<>(mainActivity);
        }

        public void handleMessage(Message message) {
            Activity activity = (MainActivity) this.f2672a.get();
            if (activity != null) {
                int i = message.what;
                if (i == 1000) {
                    activity.o();
                } else if (i == 1012) {
                    activity.a((com.miui.antivirus.model.e) message.obj);
                } else if (i == 1027) {
                    activity.a(((Float) message.obj).floatValue());
                } else if (i == 1030) {
                    activity.u();
                } else if (i != 1037) {
                    if (i == 1039) {
                        activity.w();
                    } else if (i == 1014) {
                        activity.K();
                    } else if (i == 1015) {
                        activity.p();
                    } else if (i == 1024) {
                        MainActivity.f2666a = true;
                        activity.l();
                    } else if (i == 1025) {
                        activity.m.a();
                    } else if (i == 1045) {
                        activity.t();
                    } else if (i == 1046) {
                        activity.q();
                    } else if (i == 1062) {
                        activity.x();
                    } else if (i != 1063) {
                        switch (i) {
                            case 1032:
                                activity.v();
                                return;
                            case 1033:
                                activity.a((a.C0039a) message.obj);
                                return;
                            case 1034:
                                activity.a((b.b.b.b.a) message.obj);
                                return;
                            case 1035:
                                h unused = activity.q = h.SCANNED;
                                activity.s();
                                return;
                            default:
                                switch (i) {
                                    case 1049:
                                        activity.c((com.miui.antivirus.model.a) message.obj);
                                        return;
                                    case 1050:
                                        activity.a((com.miui.antivirus.model.a) message.obj);
                                        return;
                                    case 1051:
                                        activity.r();
                                        return;
                                    case 1052:
                                        activity.b((com.miui.antivirus.model.a) message.obj);
                                        return;
                                    default:
                                        return;
                                }
                        }
                    } else {
                        activity.a((UpdateInfo) message.obj);
                    }
                } else if (activity.e) {
                    activity.finish();
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.addFlags(67108864);
                    activity.startActivity(intent);
                }
            }
        }
    }

    private static class d implements o.d {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<MainActivity> f2673a;

        public d(MainActivity mainActivity) {
            this.f2673a = new WeakReference<>(mainActivity);
        }

        public void a(int i) {
            MainActivity mainActivity = (MainActivity) this.f2673a.get();
            Log.i("AntiVirusMainActivity", "onGetVirusTaskId:" + i);
            if (mainActivity != null) {
                int unused = mainActivity.u = i;
            }
        }

        public void a(a.C0039a aVar) {
            MainActivity mainActivity = (MainActivity) this.f2673a.get();
            if (mainActivity != null) {
                mainActivity.w.a(1033, aVar);
            }
        }

        public void a(com.miui.antivirus.model.e eVar) {
            MainActivity mainActivity = (MainActivity) this.f2673a.get();
            if (mainActivity != null) {
                mainActivity.w.a(1034, b.b.b.b.a.a(0, eVar, false, (l.a) null));
            }
        }

        public void a(l.a aVar, boolean z) {
            MainActivity mainActivity = (MainActivity) this.f2673a.get();
            if (mainActivity != null) {
                mainActivity.w.a(1034, b.b.b.b.a.a(1, (com.miui.antivirus.model.e) null, z, aVar));
            }
        }

        public void b() {
            MainActivity mainActivity = (MainActivity) this.f2673a.get();
            if (mainActivity != null) {
                mainActivity.w.a(1032, (Object) null);
            }
        }

        public void c() {
            MainActivity mainActivity = (MainActivity) this.f2673a.get();
            if (mainActivity != null) {
                mainActivity.w.a(1035, (Object) null);
            }
        }

        public void d() {
            MainActivity mainActivity = (MainActivity) this.f2673a.get();
            if (mainActivity != null) {
                mainActivity.w.a(1038, (Object) null);
            }
        }

        public void e() {
            MainActivity mainActivity = (MainActivity) this.f2673a.get();
            if (mainActivity != null) {
                mainActivity.w.a(1046, (Object) null);
            }
        }

        public void f() {
            MainActivity mainActivity = (MainActivity) this.f2673a.get();
            if (mainActivity != null) {
                int unused = mainActivity.u = -1;
            }
        }

        public void g() {
            MainActivity mainActivity = (MainActivity) this.f2673a.get();
            if (mainActivity != null) {
                mainActivity.w.a(1045, (Object) null);
            }
        }

        public boolean isCancelled() {
            MainActivity mainActivity = (MainActivity) this.f2673a.get();
            if (mainActivity != null) {
                return mainActivity.f;
            }
            return false;
        }
    }

    private class e extends AsyncTask<Void, Void, Void> {
        private e() {
        }

        /* synthetic */ e(MainActivity mainActivity, l lVar) {
            this();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            if (Build.IS_STABLE_VERSION) {
                return null;
            }
            Iterator<com.miui.permcenter.a> it = n.a(MainActivity.this.getApplicationContext(), 512).iterator();
            while (it.hasNext()) {
                com.miui.permcenter.a next = it.next();
                if (next.f().get(512L).intValue() == 3) {
                    PermissionManager.getInstance(MainActivity.this.l).setApplicationPermission(512, 1, next.e());
                }
            }
            return null;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Void voidR) {
            Intent intent = new Intent("start_by_safepay");
            intent.setClassName("com.android.updater", "com.android.updater.MainActivity");
            intent.putExtra("user_action", "user_action_update_full");
            if (MainActivity.this.getPackageManager().resolveActivity(intent, 0) != null) {
                MainActivity.this.startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this.l, R.string.sc_warning_updater_not_found, 0).show();
            }
        }
    }

    private static class f extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private String f2675a = "";

        f(String str) {
            this.f2675a = str;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            Process.setThreadPriority(10);
            try {
                Thread.sleep(AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            } catch (Exception unused) {
            }
            C0250m.c();
            if (TextUtils.isEmpty(this.f2675a)) {
                this.f2675a = com.miui.activityutil.h.f2289a;
            }
            if ("00002".equals(this.f2675a)) {
                C0250m.f2843c.put(43, true);
            }
            b.a.g(this.f2675a);
            return null;
        }
    }

    private static class g extends VirusObserver {

        /* renamed from: c  reason: collision with root package name */
        private WeakReference<MainActivity> f2676c;

        g(MainActivity mainActivity) {
            this.f2676c = new WeakReference<>(mainActivity);
        }

        public void a(UpdateInfo updateInfo) {
            MainActivity mainActivity = (MainActivity) this.f2676c.get();
            if (!"MiEngine".equals(updateInfo.engineName) && mainActivity != null) {
                mainActivity.w.a(1063, updateInfo, 1000);
            }
        }

        public void p(int i) {
            android.util.Log.i("AntiVirusMainActivity", "onUpdateFinished : " + i);
            MainActivity mainActivity = (MainActivity) this.f2676c.get();
            if (mainActivity != null) {
                com.miui.guardprovider.b.a(mainActivity.getApplicationContext()).a();
            }
        }
    }

    public enum h {
        NORMAL,
        SCANNING,
        SCANNED,
        CLEANING,
        CLEANED
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity, miui.app.Activity] */
    private void A() {
        if (Build.IS_INTERNATIONAL_BUILD) {
            startActivityForResult(com.miui.securityscan.i.l.a(this, getString(R.string.sp_network_privacy_dialog_title), getString(R.string.sp_network_privacy_dialog_message), getString(17039360), getString(17039370)), 203);
            return;
        }
        b.b.b.d.h hVar = new b.b.b.d.h((DialogInterface.OnClickListener) new w(this));
        b.b.b.d.h hVar2 = new b.b.b.d.h((DialogInterface.OnDismissListener) new x(this));
        AlertDialog create = new AlertDialog.Builder(this).setTitle(R.string.sp_network_privacy_dialog_title).setMessage(R.string.sp_network_privacy_dialog_message).setPositiveButton(17039370, hVar).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setOnDismissListener(hVar2).create();
        create.show();
        hVar.a(create);
        hVar2.a(create);
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity, miui.app.Activity] */
    private void B() {
        long b2 = com.miui.securitycenter.h.b((Context) this);
        if (b2 >= 102400000) {
            this.D = true;
        }
        if (ScoreManager.e().j() < 80) {
            this.E = true;
        }
        if (this.D || this.E) {
            b.b.b.d.h hVar = new b.b.b.d.h((DialogInterface.OnClickListener) new C0231c(this));
            b.b.b.d.h hVar2 = new b.b.b.d.h((DialogInterface.OnDismissListener) new C0232d(this));
            AlertDialog create = new AlertDialog.Builder(this).setTitle(this.D ? R.string.activity_title_garbage_cleanup : R.string.exit_dialog_scan_title).setMessage(this.D ? getString(R.string.exit_dialog_garbage_clean_message, new Object[]{ExtraTextUtils.formatFileSize(this, b2)}) : getString(R.string.exit_dialog_homepage_optimise_message)).setPositiveButton(this.D ? R.string.exit_dialog_garbage_clean_positive_button : R.string.action_button_text_80_100, hVar).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).setOnDismissListener(hVar2).create();
            create.show();
            hVar.a(create);
            hVar2.a(create);
            b.C0023b.b(this.D ? "clean_master" : "home_page_optimise");
            return;
        }
        finish();
    }

    /* JADX WARNING: type inference failed for: r10v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity, miui.app.Activity] */
    private void C() {
        View inflate = View.inflate(this, R.layout.v_activity_dialog, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.dialog_header);
        String string = getString(R.string.antivirus_privacy_dialog_summary_link);
        SpannableString spannableString = new SpannableString(getString(R.string.antivirus_privacy_dialog_summary, new Object[]{string}));
        int lastIndexOf = spannableString.toString().lastIndexOf(string);
        spannableString.setSpan(new URLSpan(Locale.getDefault().toString().equals("zh_CN") ? f2668c : f2667b), lastIndexOf, string.length() + lastIndexOf, 33);
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) inflate.findViewById(R.id.switch_text)).setText(getString(R.string.antivirus_privacy_dialog_cloudscan));
        ((TextView) inflate.findViewById(R.id.dialog_summary)).setText(getString(R.string.antivirus_privacy_dialog_cloudscan_description));
        SlidingButton findViewById = inflate.findViewById(R.id.dialog_checkbox);
        findViewById.setChecked(true);
        ((LinearLayout) inflate.findViewById(R.id.cloud_scan_layout)).setOnClickListener(new y(this, findViewById));
        b.b.b.d.h hVar = new b.b.b.d.h((DialogInterface.OnClickListener) new z(this, findViewById));
        AlertDialog create = new AlertDialog.Builder(this).setCancelable(false).setTitle(R.string.antivirus_privacy_dialog_title).setView(inflate).setPositiveButton(R.string.antivirus_privacy_dialog_ok, hVar).create();
        create.show();
        hVar.a(create);
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity] */
    private void D() {
        b.b.b.d.h hVar = new b.b.b.d.h((DialogInterface.OnClickListener) new C0236h(this));
        AlertDialog create = new AlertDialog.Builder(this).setTitle(R.string.dialog_title_stop_virus_scan).setMessage(R.string.dialog_msg_stop_virus_scan).setPositiveButton(R.string.ok, hVar).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).create();
        create.show();
        hVar.a(create);
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity] */
    private void E() {
        b.b.b.d.h hVar = new b.b.b.d.h((DialogInterface.OnClickListener) new C0235g(this));
        AlertDialog create = new AlertDialog.Builder(this).setTitle(R.string.dialog_title_stop_virus_scan).setMessage(R.string.dialog_msg_stop_virus_scan).setPositiveButton(R.string.ok, hVar).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).create();
        create.show();
        hVar.a(create);
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity] */
    /* access modifiers changed from: private */
    public void F() {
        if (p.i()) {
            b.b.c.j.d.a(new v(this));
            C();
        } else if (!this.y.d() || !this.y.c()) {
            o();
            b.a.j("scan");
        } else {
            b((Context) this);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity, miui.app.Activity] */
    /* access modifiers changed from: private */
    public void G() {
        this.z = ProgressDialog.show(this, (CharSequence) null, getString(R.string.antivirus_toast_updating), true, true);
        this.z.setOnCancelListener(new q(this));
        new b(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* access modifiers changed from: private */
    public void H() {
        if (this.u != -1) {
            this.n.b((b.a) new C0237i(this));
        }
    }

    private void I() {
        new f(getIntent().getStringExtra("enter_homepage_way")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private List<com.miui.antivirus.model.e> J() {
        l w2 = this.r.w();
        com.miui.antivirus.model.e m2 = this.r.m();
        com.miui.antivirus.model.e j2 = this.r.j();
        boolean k2 = this.r.k();
        List<com.miui.antivirus.model.e> u2 = this.r.u();
        List<com.miui.antivirus.model.e> s2 = this.r.s();
        boolean z2 = j2 != null;
        boolean z3 = !b.b.a.e.c.d(this.l);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new com.miui.antivirus.model.e(e.b.TOP));
        if (!Build.IS_INTERNATIONAL_BUILD && w2.e()) {
            com.miui.antivirus.model.e eVar = new com.miui.antivirus.model.e(e.b.HEADER);
            eVar.d(true);
            eVar.h(getString(R.string.result_wifi_header));
            eVar.d(getString(R.string.scan_status_risk));
            arrayList.add(eVar);
            arrayList.add(w2);
        }
        if (m2 != null) {
            arrayList.add(m2);
        }
        if ((!Build.IS_INTERNATIONAL_BUILD || Build.checkRegion("IN")) && !p.j()) {
            com.miui.antivirus.model.e eVar2 = new com.miui.antivirus.model.e(e.b.APP);
            eVar2.a(e.a.MONITOR);
            arrayList.add(eVar2);
        }
        if (z2 || z3 || k2) {
            com.miui.antivirus.model.e eVar3 = new com.miui.antivirus.model.e(e.b.HEADER);
            eVar3.d(true);
            eVar3.h(getString(R.string.result_sms_header));
            eVar3.d(getString(R.string.scan_status_risk));
            arrayList.add(eVar3);
            if (z2) {
                j2.c(false);
                j2.b(true);
                arrayList.add(j2);
                if (z3 || k2) {
                    j2.b(false);
                }
            }
            if (z3) {
                com.miui.antivirus.model.e eVar4 = new com.miui.antivirus.model.e(e.b.APP);
                eVar4.a(e.a.URL);
                eVar4.c(false);
                eVar4.b(true);
                arrayList.add(eVar4);
                if (k2) {
                    eVar4.b(false);
                }
            }
            if (k2) {
                com.miui.antivirus.model.e eVar5 = new com.miui.antivirus.model.e(e.b.APP);
                eVar5.a(e.a.AUTH);
                eVar5.c(false);
                arrayList.add(eVar5);
            }
        }
        if (!u2.isEmpty()) {
            com.miui.antivirus.model.e eVar6 = new com.miui.antivirus.model.e(e.b.HEADER);
            com.miui.antivirus.model.e eVar7 = new com.miui.antivirus.model.e(e.b.BUTTON);
            eVar7.a(e.a.VIRUS);
            eVar6.d(true);
            eVar6.h(getResources().getQuantityString(R.plurals.result_virus_header, u2.size(), new Object[]{Integer.valueOf(u2.size())}));
            eVar6.d(getString(R.string.scan_status_risk));
            arrayList.add(eVar6);
            arrayList.addAll(u2);
            arrayList.add(eVar7);
        }
        if (!Build.IS_INTERNATIONAL_BUILD && !s2.isEmpty()) {
            s2.get(s2.size() - 1).b(true);
            com.miui.antivirus.model.e eVar8 = new com.miui.antivirus.model.e(e.b.HEADER);
            eVar8.d(true);
            eVar8.h(getResources().getQuantityString(R.plurals.result_sign_header, s2.size(), new Object[]{Integer.valueOf(s2.size())}));
            eVar8.d(getString(R.string.scan_status_risk));
            arrayList.add(eVar8);
            arrayList.addAll(s2);
        }
        if ((!Build.IS_INTERNATIONAL_BUILD || Build.checkRegion("IN")) && p.j()) {
            int d2 = b.b.b.d.n.d(this.l);
            com.miui.antivirus.model.e eVar9 = new com.miui.antivirus.model.e(e.b.HEADER);
            eVar9.h(d2 == 0 ? getString(R.string.sp_monitored_apps_count_zero) : getResources().getQuantityString(R.plurals.sp_monitored_apps_count, d2, new Object[]{Integer.valueOf(d2)}));
            com.miui.antivirus.model.e eVar10 = new com.miui.antivirus.model.e(e.b.SAFE);
            eVar10.a(e.c.MONITOR);
            arrayList.add(eVar9);
            arrayList.add(eVar10);
        }
        return arrayList;
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity, miui.app.Activity] */
    /* access modifiers changed from: private */
    public void K() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    /* access modifiers changed from: private */
    public void a(float f2) {
        this.m.setContentAlpha(f2);
    }

    /* access modifiers changed from: private */
    public void a(b.b.b.b.a aVar) {
        Object obj;
        l.a aVar2;
        o oVar;
        com.miui.antivirus.model.e a2 = aVar.a();
        boolean z2 = true;
        if (aVar.b() == 0) {
            int i2 = r.f2732b[a2.g().ordinal()];
            if (i2 == 1) {
                this.r.g(a2);
            } else if (i2 == 2) {
                this.r.f(a2);
            } else if (i2 == 3) {
                boolean w2 = a2.w();
                this.r.a(w2);
                this.t.add(new com.miui.antivirus.model.i(a.C0039a.SMS, getString(R.string.apps_list_header_title_sms_access), (String) null, w2));
            } else if (i2 == 4) {
                if (a2.p() == o.g.SAFE) {
                    z2 = false;
                }
                this.t.add(new com.miui.antivirus.model.i(a.C0039a.APP, a2.h(), (String) null, z2));
                if (z2) {
                    a2.c(false);
                    a2.b(false);
                    this.r.b(a2);
                }
            } else if (i2 == 5) {
                boolean z3 = a2.s() == 2;
                com.miui.antivirus.model.i iVar = new com.miui.antivirus.model.i(a.C0039a.APP, a2.h(), (String) null, z3);
                iVar.a(true);
                this.t.add(iVar);
                if (z3) {
                    a2.c(false);
                    a2.b(false);
                    this.r.a(a2);
                }
            }
        } else if (1 == aVar.b()) {
            boolean d2 = aVar.d();
            int i3 = r.f2733c[aVar.c().ordinal()];
            if (i3 == 1) {
                obj = new com.miui.antivirus.model.i(a.C0039a.WIFI, getString(R.string.wifi_item_title_connection), (String) null, d2);
                oVar = this.r;
                aVar2 = l.a.CONNECTION;
            } else if (i3 == 2) {
                obj = new com.miui.antivirus.model.i(a.C0039a.WIFI, getString(R.string.wifi_item_title_encryption), (String) null, d2);
                oVar = this.r;
                aVar2 = l.a.ENCRYPTION;
            } else if (i3 == 3) {
                obj = new com.miui.antivirus.model.i(a.C0039a.WIFI, getString(R.string.wifi_item_title_fake), (String) null, d2);
                oVar = this.r;
                aVar2 = l.a.FAKE;
            } else if (i3 == 4) {
                obj = new com.miui.antivirus.model.i(a.C0039a.WIFI, getString(R.string.wifi_item_title_dns), (String) null, d2);
                oVar = this.r;
                aVar2 = l.a.DNS;
            } else if (i3 != 5) {
                obj = null;
                this.t.add(obj);
            } else {
                obj = new com.miui.antivirus.model.i(a.C0039a.WIFI, getString(R.string.wifi_item_title_arp_attack), (String) null, d2);
                oVar = this.r;
                aVar2 = l.a.ARP;
            }
            oVar.a(aVar2, d2);
            this.t.add(obj);
        }
    }

    private void a(m mVar) {
        b.a.e(this.e ? "stop_enter_result" : mVar == m.SAFE ? "enter_result_safe" : "enter_result_risky");
        MainActivityView mainActivityView = this.m;
        if (this.e) {
            mVar = m.INTERRUPT;
        }
        mainActivityView.a(mVar);
        this.m.a(this.r.i(), this.e, true);
        this.w.a(1025, new Object(), 800);
    }

    /* access modifiers changed from: private */
    public void a(a.C0039a aVar) {
        this.t.add(new com.miui.antivirus.model.h(aVar, (String) null, (String) null, 0));
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity, miui.app.Activity] */
    /* access modifiers changed from: private */
    public void a(com.miui.antivirus.model.e eVar) {
        switch (r.f2732b[eVar.g().ordinal()]) {
            case 1:
                if (!B.f()) {
                    Toast.makeText(this.l, R.string.sc_system_risk_fix_info_xspace, 0).show();
                    return;
                } else if (((com.miui.antivirus.model.j) eVar).y()) {
                    new e(this, (l) null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    b.C0023b.f();
                    return;
                } else {
                    Intent intent = new Intent("start_by_safepay");
                    intent.setClassName("com.android.updater", "com.android.updater.MainActivity");
                    intent.putExtra("user_action", "user_action_update");
                    if (getPackageManager().resolveActivity(intent, 0) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(this.l, R.string.sc_warning_updater_not_found, 0).show();
                    }
                    b.C0023b.j();
                    return;
                }
            case 2:
                try {
                    b.b.o.g.d.a("AntiVirusMainActivity", Class.forName("com.android.internal.telephony.SmsApplication"), "setDefaultApplication", (Class<?>[]) new Class[]{String.class, Context.class}, b.b.b.d.n.d() ? "com.google.android.apps.messaging" : "com.android.mms", this.l);
                } catch (Exception e2) {
                    Log.e("AntiVirusMainActivity", "setDefaultApplication exception!", e2);
                }
                Toast.makeText(this.l, R.string.apps_item_sms_default_set_success, 0).show();
                this.r.a();
                z();
                b.C0023b.b();
                return;
            case 3:
                new a(this, (l) null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                b.C0023b.e();
                return;
            case 4:
                Toast.makeText(this.l, R.string.apps_item_virus_cleanup_success, 0).show();
                z();
                b.C0023b.k();
                return;
            case 5:
                b.b.b.d.h hVar = new b.b.b.d.h((DialogInterface.OnClickListener) new C0229a(this, eVar));
                AlertDialog create = new AlertDialog.Builder(this).setTitle(R.string.apps_item_sign_dialog_title).setMessage(getString(R.string.apps_item_sign_dialog_content, new Object[]{eVar.h()})).setPositiveButton(17039370, hVar).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
                create.show();
                hVar.a(create);
                return;
            case 6:
                com.miui.support.provider.a.a(this.l, true);
                Toast.makeText(this.l, R.string.toast_set_success, 0).show();
                z();
                b.C0023b.a();
                return;
            case 7:
                Intent intent2 = new Intent(this, GuardService.class);
                intent2.setAction("action_register_foreground_notification");
                startService(intent2);
                Toast.makeText(this.l, R.string.toast_set_success, 0).show();
                b.C0023b.d();
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public void a(UpdateInfo updateInfo) {
        Log.d("AntiVirusMainActivity", "engine:" + updateInfo.engineName + "result:" + updateInfo.updateResult);
        int i2 = updateInfo.updateResult;
        int i3 = R.string.antivirus_toast_update_failed;
        if (i2 != 0) {
            if (i2 == 2) {
                b.a.f("fail");
            } else if (i2 == 3) {
                i3 = R.string.antivirus_toast_already_update;
            }
            Toast.makeText(this.l, i3, 0).show();
            a(this.z);
        }
        i3 = R.string.antivirus_toast_update_success;
        this.y.a(System.currentTimeMillis(), updateInfo.engineName);
        this.y.b(System.currentTimeMillis());
        b.a.f("success");
        Toast.makeText(this.l, i3, 0).show();
        a(this.z);
    }

    private void a(ProgressDialog progressDialog) {
        if (!isFinishing() && !isDestroyed()) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (this.h.compareAndSet(false, true)) {
                o();
            }
        }
    }

    /* access modifiers changed from: private */
    public void b(com.miui.antivirus.model.a aVar) {
        int b2 = aVar.b();
        if (b2 != 0) {
            if (b2 == 1) {
                p.d(false);
            } else if (b2 == 2) {
                p.f(false);
            } else if (b2 == 3) {
                com.miui.antivirus.model.e eVar = (com.miui.antivirus.model.e) aVar;
                this.r.e(eVar);
                this.x.a(eVar);
            } else if (b2 == 4) {
                com.miui.antivirus.model.e eVar2 = (com.miui.antivirus.model.e) aVar;
                this.r.d(eVar2);
                ArrayList arrayList = new ArrayList(p.g());
                arrayList.add(eVar2.m());
                p.c((ArrayList<String>) arrayList);
            }
            this.r.c();
        } else {
            p.i(false);
            this.r.f();
        }
        z();
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity] */
    private void b(N n2) {
        this.A = new b.b.b.c.e(this);
        if (this.A.a((View) this.m, n2)) {
            this.A.g();
            this.A.h();
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity] */
    /* access modifiers changed from: private */
    public void c(com.miui.antivirus.model.a aVar) {
        MainActivityView mainActivityView;
        MainHandleBar.a aVar2;
        MainHandleBar.b bVar;
        int i2 = r.f2731a[aVar.a().ordinal()];
        if (i2 != 1) {
            if (i2 != 2) {
                if (i2 != 3) {
                    if (i2 == 4 && this.r.l() == m.SAFE) {
                        mainActivityView = this.m;
                        bVar = MainHandleBar.b.SMS;
                    } else {
                        return;
                    }
                } else if (this.r.n() == m.SAFE) {
                    mainActivityView = this.m;
                    bVar = MainHandleBar.b.SYSTEM;
                } else {
                    return;
                }
            } else if (!p.p() || !k.b(this)) {
                mainActivityView = this.m;
                bVar = MainHandleBar.b.NETWORK;
                aVar2 = MainHandleBar.a.OMITTED;
                mainActivityView.a(bVar, aVar2);
            } else if (this.r.x() == m.SAFE) {
                mainActivityView = this.m;
                bVar = MainHandleBar.b.NETWORK;
            } else {
                return;
            }
            aVar2 = MainHandleBar.a.SAFE;
            mainActivityView.a(bVar, aVar2);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity] */
    private void m() {
        b.b.b.c.e eVar;
        if (!Build.IS_INTERNATIONAL_BUILD && b.b.b.c.g.b(this) && (eVar = this.A) != null) {
            eVar.i();
            this.A.a(false);
            this.A.c();
            this.A.b();
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity, miui.app.Activity] */
    private void n() {
        this.B = (CustomActionBar) findViewById(R.id.actionbar);
        this.B.setTitle(getTitle().toString());
        if (b.b.b.d.n.k(this) || !b.b.b.d.n.c()) {
            this.B.setIsShowSecondTitle(false);
        }
        this.B.setActionBarEventListener(new u(this));
    }

    /* access modifiers changed from: private */
    public void o() {
        int i2 = r.f2734d[this.q.ordinal()];
        if (i2 == 1 || i2 == 2) {
            E();
        } else if (i2 == 3) {
            this.q = h.SCANNING;
            this.f = false;
            this.n.b((b.a) new C0234f(this));
            b.a.e();
        }
    }

    /* access modifiers changed from: private */
    public void p() {
        b.b.b.c.e eVar = this.A;
        if (eVar == null || !eVar.d() || !this.A.f()) {
            int i2 = r.f2734d[this.q.ordinal()];
            if (i2 == 1 || (i2 == 2 && !this.g)) {
                D();
            } else {
                B();
            }
        } else {
            this.A.c();
        }
    }

    /* access modifiers changed from: private */
    public void q() {
        android.util.Log.i("AntiVirusMainActivity", "onFinishDefaultSMSCheck");
        com.miui.antivirus.model.i iVar = new com.miui.antivirus.model.i(a.C0039a.SMS, getString(R.string.apps_list_header_title_sms), (String) null, this.r.j() != null);
        com.miui.antivirus.model.i iVar2 = new com.miui.antivirus.model.i(a.C0039a.SMS, getString(R.string.sp_scan_item_url_antispam), (String) null, true ^ b.b.a.e.c.d(this.l));
        this.t.add(iVar);
        this.t.add(iVar2);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity] */
    /* access modifiers changed from: private */
    public void r() {
        if (!this.g) {
            m();
            Log.i("AntiVirusMainActivity", "onFinishScanAnimation");
            this.g = true;
            if (this.r.h() == m.SAFE && !this.e) {
                this.m.a(MainHandleBar.b.VIRUS, MainHandleBar.a.SAFE);
            }
            this.i.clear();
            this.i.addAll(J());
            this.j = new t(this, this.i, this.w);
            this.m.a(this.j, this.i);
            m q2 = this.r.q();
            a(q2);
            p.b(System.currentTimeMillis());
            p.b(this.r.v());
            p.a(this.r.t() - this.r.v());
            p.a(q2);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity] */
    /* access modifiers changed from: private */
    public void s() {
        b.a.a(this.r.o());
        b.a.c((long) this.r.a((Boolean) true));
        b.a.b((long) (this.r.t() + this.r.r()));
        b.C0023b.a((Context) this);
    }

    /* access modifiers changed from: private */
    public void t() {
        android.util.Log.i("AntiVirusMainActivity", "onFinishSystemCheck");
        com.miui.antivirus.model.e m2 = this.r.m();
        boolean z2 = false;
        boolean z3 = m2 != null && ((com.miui.antivirus.model.j) m2).y();
        if (m2 != null && ((com.miui.antivirus.model.j) m2).x()) {
            z2 = true;
        }
        com.miui.antivirus.model.i iVar = new com.miui.antivirus.model.i(a.C0039a.SYSTEM, getString(R.string.main_activity_content_summary_root), (String) null, z3);
        com.miui.antivirus.model.i iVar2 = new com.miui.antivirus.model.i(a.C0039a.SYSTEM, getString(R.string.main_activity_content_summary_update), (String) null, z2);
        com.miui.antivirus.model.i iVar3 = new com.miui.antivirus.model.i(a.C0039a.SYSTEM, getString(R.string.sp_scan_item_monitor), (String) null, true ^ p.j());
        if (p.k()) {
            this.t.add(iVar);
        }
        if (p.m()) {
            this.t.add(iVar2);
        }
        this.t.add(iVar3);
    }

    /* access modifiers changed from: private */
    public void u() {
        Log.e("AntiVirusMainActivity", "ERROR : GuardProvider Service disconnected !");
        this.q = h.NORMAL;
        H();
        r();
    }

    /* access modifiers changed from: private */
    public void v() {
        if (!this.e) {
            this.g = false;
            this.m.c();
            this.m.a(m.SAFE);
            b.b.c.j.d.a(new C0230b(this));
        }
    }

    /* access modifiers changed from: private */
    public void w() {
        this.p.removeNetwork(this.p.getConnectionInfo().getNetworkId());
        this.r.f();
        z();
        b.C0023b.l();
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.antivirus.activity.MainActivity, com.miui.antivirus.result.K$c, android.app.Activity] */
    /* access modifiers changed from: private */
    public void x() {
        if (!Build.IS_INTERNATIONAL_BUILD) {
            K.a((K.c) this, (Context) this);
        }
        K.a(this.k);
        K.b(this);
    }

    private void y() {
        m q2 = this.r.q();
        MainActivityView mainActivityView = this.m;
        if (this.e) {
            q2 = m.INTERRUPT;
        }
        mainActivityView.a(q2);
        int i2 = this.r.i();
        int v2 = this.r.v();
        int i3 = i2 - v2;
        if (i2 > 0) {
            MainActivityView mainActivityView2 = this.m;
            mainActivityView2.setScanResult(getResources().getQuantityString(R.plurals.hints_handle_item_text_risk_number, i3, new Object[]{Integer.valueOf(i3)}) + getResources().getQuantityString(R.plurals.hints_handle_item_text_virus_number, v2, new Object[]{Integer.valueOf(v2)}));
        }
    }

    /* access modifiers changed from: private */
    public void z() {
        K resultControl = this.m.getResultControl();
        if (resultControl != null) {
            int i2 = this.r.i();
            this.m.a(i2, this.e, false);
            Settings.Secure.putInt(getContentResolver(), "antivirus_last_risk_count", i2);
            this.i.clear();
            this.i.addAll(J());
            this.i.addAll(resultControl.c());
            resultControl.f();
            p.b(this.r.v());
            p.a(this.r.t() - this.r.v());
            p.a(this.r.q());
        }
    }

    public void a(Context context) {
        if (b.b.c.j.f.c(context)) {
            G();
            return;
        }
        b.b.b.d.h hVar = new b.b.b.d.h((DialogInterface.OnClickListener) new j(this));
        b.b.b.d.h hVar2 = new b.b.b.d.h((DialogInterface.OnClickListener) new k(this));
        AlertDialog create = new AlertDialog.Builder(context).setTitle(R.string.virus_update_tips_title).setMessage(R.string.virus_wait_network_dialog_message).setPositiveButton(R.string.antivirus_update_btn_contiue, hVar).setNegativeButton(17039360, hVar2).setOnCancelListener(new m(this)).create();
        create.show();
        hVar.a(create);
        hVar2.a(create);
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x00ab  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(com.miui.antivirus.model.a r6) {
        /*
            r5 = this;
            int[] r0 = com.miui.antivirus.activity.r.f2731a
            com.miui.antivirus.model.a$a r1 = r6.a()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            r1 = 2131758601(0x7f100e09, float:1.914817E38)
            r2 = 1
            if (r0 == r2) goto L_0x0042
            r3 = 2
            if (r0 == r3) goto L_0x0032
            r3 = 3
            if (r0 == r3) goto L_0x0027
            r3 = 4
            if (r0 == r3) goto L_0x001c
            goto L_0x005b
        L_0x001c:
            boolean r0 = r6.e()
            if (r0 == 0) goto L_0x005b
            com.miui.antivirus.ui.MainActivityView r0 = r5.m
            com.miui.antivirus.ui.MainHandleBar$b r3 = com.miui.antivirus.ui.MainHandleBar.b.VIRUS
            goto L_0x003c
        L_0x0027:
            boolean r0 = r6.e()
            if (r0 == 0) goto L_0x005b
            com.miui.antivirus.ui.MainActivityView r0 = r5.m
            com.miui.antivirus.ui.MainHandleBar$b r3 = com.miui.antivirus.ui.MainHandleBar.b.SMS
            goto L_0x003c
        L_0x0032:
            boolean r0 = r6.e()
            if (r0 == 0) goto L_0x005b
            com.miui.antivirus.ui.MainActivityView r0 = r5.m
            com.miui.antivirus.ui.MainHandleBar$b r3 = com.miui.antivirus.ui.MainHandleBar.b.SYSTEM
        L_0x003c:
            com.miui.antivirus.ui.MainHandleBar$a r4 = com.miui.antivirus.ui.MainHandleBar.a.RISKY
            r0.a((com.miui.antivirus.ui.MainHandleBar.b) r3, (com.miui.antivirus.ui.MainHandleBar.a) r4)
            goto L_0x005b
        L_0x0042:
            boolean r0 = r6.e()
            if (r0 == 0) goto L_0x005b
            java.lang.String r0 = r5.getString(r1)
            java.lang.String r3 = r6.c()
            boolean r0 = r0.equals(r3)
            if (r0 != 0) goto L_0x005b
            com.miui.antivirus.ui.MainActivityView r0 = r5.m
            com.miui.antivirus.ui.MainHandleBar$b r3 = com.miui.antivirus.ui.MainHandleBar.b.NETWORK
            goto L_0x003c
        L_0x005b:
            int r0 = r5.v
            int r0 = r0 + r2
            r5.v = r0
            boolean r0 = r6.e()
            if (r0 == 0) goto L_0x0077
            java.lang.String r0 = r5.getString(r1)
            java.lang.String r1 = r6.c()
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L_0x0077
            r5.y()
        L_0x0077:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "scanning: "
            r0.append(r1)
            int r1 = r5.v
            r0.append(r1)
            java.lang.String r1 = "/"
            r0.append(r1)
            b.b.b.o r1 = r5.r
            int r1 = r1.p()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AntiVirusMainActivity"
            miui.util.Log.i(r1, r0)
            int r0 = r5.v
            r1 = 100
            int r0 = r0 * r1
            b.b.b.o r3 = r5.r
            int r3 = r3.p()
            int r0 = r0 / r3
            if (r0 <= r1) goto L_0x00ac
            r0 = r1
        L_0x00ac:
            com.miui.antivirus.ui.MainActivityView r1 = r5.m
            java.util.Locale r3 = java.util.Locale.getDefault()
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r2[r4] = r0
            java.lang.String r0 = "%d"
            java.lang.String r0 = java.lang.String.format(r3, r0, r2)
            r1.setContentProgressText(r0)
            com.miui.antivirus.ui.MainActivityView r0 = r5.m
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 2131756586(0x7f10062a, float:1.9144084E38)
            java.lang.String r2 = r5.getString(r2)
            r1.append(r2)
            java.lang.String r6 = r6.c()
            r1.append(r6)
            java.lang.String r6 = r1.toString()
            r0.setContentSummary(r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antivirus.activity.MainActivity.a(com.miui.antivirus.model.a):void");
    }

    public void a(N n2) {
        if (!Build.IS_INTERNATIONAL_BUILD && n2 != null) {
            h hVar = this.q;
            if (hVar == h.SCANNING || hVar == h.NORMAL) {
                b(n2);
            }
        }
    }

    public void a(C0238a aVar) {
        this.m.getResultControl().a(aVar);
    }

    public void a(C0244g gVar, List<C0244g> list, List<C0244g> list2) {
        this.m.getResultControl().a(gVar, list, list2);
    }

    public void a(AdImageView adImageView, int i2, C0243f fVar) {
        this.m.getResultControl().a(adImageView, i2, fVar);
    }

    public void b(Context context) {
        if (!b.b.c.j.f.b(context) || (Build.IS_INTERNATIONAL_BUILD && !com.miui.securitycenter.h.i())) {
            o();
            return;
        }
        b.b.b.d.h hVar = new b.b.b.d.h((DialogInterface.OnClickListener) new n(this, context));
        b.b.b.d.h hVar2 = new b.b.b.d.h((DialogInterface.OnClickListener) new o(this));
        AlertDialog create = new AlertDialog.Builder(context).setTitle(R.string.virus_update_tips_title).setMessage(com.miui.securitycenter.h.i() ? R.string.antivirus_longtime_no_update1 : R.string.antivirus_longtime_no_update).setPositiveButton(R.string.antivirus_update_btn_update, hVar).setNegativeButton(17039360, hVar2).setOnCancelListener(new p(this)).create();
        create.show();
        hVar2.a(create);
        hVar.a(create);
        this.y.a(System.currentTimeMillis());
        b.a.j("pop_up");
    }

    public void l() {
        if (!K.d()) {
            this.f2669d = true;
        } else if (f2666a) {
            f2666a = false;
            this.m.b();
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i2, int i3, Intent intent) {
        MainActivity.super.onActivityResult(i2, i3, intent);
        if (i2 == 203) {
            if (i3 == -1) {
                com.miui.securityscan.i.l.a(getApplicationContext(), true);
                F();
            } else if (i3 == 0) {
                finish();
            }
        }
    }

    public void onBackPressed() {
        p();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.v_activity_main);
        com.miui.superpower.b.k.a((Activity) this);
        this.l = getApplicationContext();
        this.y = i.a(this.l);
        this.n = com.miui.guardprovider.b.a(this.l);
        this.n.a((b.a) null);
        this.o = new t(this);
        this.n.a(this.o);
        this.r = o.a(this.l);
        this.x = j.a(this.l);
        this.p = (WifiManager) getApplicationContext().getSystemService("wifi");
        this.s = new d(this);
        this.m = (MainActivityView) findViewById(R.id.main_view);
        this.m.setEventHandler(this.w);
        this.m.setScanResult(getString(R.string.hints_scan_result_safe));
        this.m.setContentSummary(getString(R.string.descx_quick_scan_preparation));
        if (com.miui.securitycenter.h.i()) {
            F();
        } else {
            A();
        }
        I();
        this.w.sendEmptyMessageDelayed(1062, 600);
        getContentResolver().registerContentObserver(Uri.withAppendedPath(Uri.parse("content://com.miui.securitycenter.remoteprovider"), "key_safepay_auto_scan_state"), false, this.C);
        n();
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [com.miui.antivirus.activity.MainActivity, miui.app.Activity, android.app.Activity] */
    /* access modifiers changed from: protected */
    public void onDestroy() {
        MainActivity.super.onDestroy();
        H();
        this.n.b(this.o);
        this.n.a();
        K.a((Runnable) null);
        K.a((android.app.Activity) this);
        getContentResolver().unregisterContentObserver(this.C);
        if (this.m.getResultControl() != null) {
            this.m.getResultControl().e();
        }
        w.a();
        b.b.b.c.e eVar = this.A;
        if (eVar != null) {
            eVar.a();
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }
}
