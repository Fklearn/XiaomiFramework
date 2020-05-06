package com.miui.applicationlock;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.g;
import com.miui.applicationlock.E;
import com.miui.applicationlock.a.i;
import com.miui.applicationlock.c.C;
import com.miui.applicationlock.c.C0257a;
import com.miui.applicationlock.c.C0259c;
import com.miui.applicationlock.c.E;
import com.miui.applicationlock.c.F;
import com.miui.applicationlock.c.G;
import com.miui.applicationlock.c.o;
import com.miui.applicationlock.c.q;
import com.miui.applicationlock.c.z;
import com.miui.applicationlock.widget.C0309b;
import com.miui.common.stickydecoration.f;
import com.miui.luckymoney.config.AppConstants;
import com.miui.networkassistant.utils.HybirdServiceUtil;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import miui.app.AlertDialog;
import miui.cloud.Constants;
import miui.security.SecurityManager;
import miui.util.ArraySet;
import miui.view.SearchActionMode;

/* renamed from: com.miui.applicationlock.y  reason: case insensitive filesystem */
public class C0312y extends Fragment {

    /* renamed from: a  reason: collision with root package name */
    public static final ArraySet<String> f3467a = new ArraySet<>();

    /* renamed from: b  reason: collision with root package name */
    public static final ArrayList<String> f3468b = new ArrayList<>();
    private boolean A;
    private c B;
    private RecyclerView.f C;
    private RecyclerView.f D;
    private View.OnClickListener E = new C0283k(this);
    /* access modifiers changed from: private */
    public TextWatcher F = new C0293p(this);
    /* access modifiers changed from: private */
    public final z G = new b(this, (C0283k) null);
    private DialogInterface.OnClickListener H = new C0302u(this);
    private DialogInterface.OnClickListener I = new C0304v(this);
    /* access modifiers changed from: private */
    public Comparator<C0257a> J = new C0279i(this);
    /* access modifiers changed from: private */
    public Comparator<C0257a> K = new C0281j(this);
    /* access modifiers changed from: private */
    public SearchActionMode.Callback L = new C0285l(this);
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public miuix.recyclerview.widget.RecyclerView f3469c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public TextView f3470d;
    /* access modifiers changed from: private */
    public View e;
    /* access modifiers changed from: private */
    public E f;
    /* access modifiers changed from: private */
    public SecurityManager g;
    private LayoutInflater h;
    protected SearchActionMode i;
    /* access modifiers changed from: private */
    public ArrayList<F> j;
    /* access modifiers changed from: private */
    public ArrayList<C0257a> k;
    /* access modifiers changed from: private */
    public E l;
    /* access modifiers changed from: private */
    public String m;
    /* access modifiers changed from: private */
    public int n = 0;
    /* access modifiers changed from: private */
    public AlertDialog o;
    /* access modifiers changed from: private */
    public AlertDialog p;
    private AlertDialog q;
    /* access modifiers changed from: private */
    public TextView r;
    /* access modifiers changed from: private */
    public TextView s;
    /* access modifiers changed from: private */
    public C0259c t;
    /* access modifiers changed from: private */
    public String u;
    /* access modifiers changed from: private */
    public int v;
    /* access modifiers changed from: private */
    public String w;
    /* access modifiers changed from: private */
    public C x;
    /* access modifiers changed from: private */
    public int y;
    /* access modifiers changed from: private */
    public Activity z;

    /* renamed from: com.miui.applicationlock.y$a */
    private static class a implements q {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<C0312y> f3471a;

        private a(C0312y yVar) {
            this.f3471a = new WeakReference<>(yVar);
        }

        /* synthetic */ a(C0312y yVar, C0283k kVar) {
            this(yVar);
        }

        public void a() {
            C0312y yVar = (C0312y) this.f3471a.get();
            if (yVar != null) {
                if (C0312y.c(yVar) < 5) {
                    yVar.r.setText(yVar.getResources().getString(R.string.fingerprint_verify_try_agin));
                    yVar.o.show();
                    o.j(yVar.getActivity());
                    return;
                }
                int unused = yVar.n = 0;
                yVar.o.dismiss();
                Toast.makeText(yVar.z, yVar.getResources().getString(R.string.fingerprint_verify_failed), 0).show();
                yVar.t.d(false);
                yVar.l.a();
            }
        }

        public void a(int i) {
            C0312y yVar = (C0312y) this.f3471a.get();
            if (yVar != null) {
                o.a(i, yVar.v);
                yVar.t.d(true);
                Toast.makeText(yVar.z.getApplicationContext(), yVar.z.getResources().getString(R.string.fingerprint_verify_succeed), 0).show();
                int unused = yVar.n = 0;
                yVar.o.setOnDismissListener((DialogInterface.OnDismissListener) null);
                yVar.o.dismiss();
                yVar.l.a();
            }
        }
    }

    /* renamed from: com.miui.applicationlock.y$b */
    private static class b implements z {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<C0312y> f3472a;

        private b(C0312y yVar) {
            this.f3472a = new WeakReference<>(yVar);
        }

        /* synthetic */ b(C0312y yVar, C0283k kVar) {
            this(yVar);
        }

        public void a() {
            Log.d("AppLockManageFragment", " restartFaceUnlock ");
        }

        public void a(String str) {
            C0312y yVar = (C0312y) this.f3472a.get();
            if (yVar != null) {
                Log.d("AppLockManageFragment", " onFaceHelp ");
                if (yVar.s != null) {
                    yVar.s.setText(str);
                }
            }
        }

        public void a(boolean z) {
            C0312y yVar = (C0312y) this.f3472a.get();
            if (yVar != null) {
                Log.d("AppLockManageFragment", " onFaceAuthFailed ");
                if (yVar.s != null) {
                    yVar.s.setText(R.string.face_unlock_verity_dialog_title_failed);
                }
                yVar.p.dismiss();
                Toast.makeText(yVar.z.getApplicationContext(), R.string.face_unlock_toast_verity_failed, 1).show();
            }
        }

        public void b() {
            C0312y yVar = (C0312y) this.f3472a.get();
            if (yVar != null) {
                Log.d("AppLockManageFragment", " onFaceAuthenticated ");
                if (yVar.s != null) {
                    yVar.s.setText(R.string.face_unlock_verity_dialog_title_succeed);
                }
                yVar.p.dismiss();
                yVar.t.c(true);
            }
        }

        public void c() {
            Log.d("AppLockManageFragment", " onFaceLocked ");
        }

        public void d() {
            C0312y yVar = (C0312y) this.f3472a.get();
            if (yVar != null) {
                Log.d("AppLockManageFragment", " onFaceStart ");
                if (yVar.s != null) {
                    yVar.s.setText(R.string.face_unlock_face_start_title);
                }
            }
        }
    }

    /* renamed from: com.miui.applicationlock.y$c */
    private static class c implements LoaderManager.LoaderCallbacks<ArrayList<F>> {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<C0312y> f3473a;

        private c(C0312y yVar) {
            this.f3473a = new WeakReference<>(yVar);
        }

        /* synthetic */ c(C0312y yVar, C0283k kVar) {
            this(yVar);
        }

        /* renamed from: a */
        public void onLoadFinished(Loader<ArrayList<F>> loader, ArrayList<F> arrayList) {
            C0312y yVar = (C0312y) this.f3473a.get();
            if (yVar != null) {
                ArrayList unused = yVar.j = arrayList;
                String format = String.format(yVar.getResources().getQuantityString(R.plurals.find_applications, yVar.k.size()), new Object[]{Integer.valueOf(yVar.k.size())});
                yVar.f3470d.setHint(format);
                yVar.f3470d.setContentDescription(format);
                yVar.f.a((List<F>) yVar.j, false);
                yVar.d();
            }
        }

        public Loader<ArrayList<F>> onCreateLoader(int i, Bundle bundle) {
            C0312y yVar = (C0312y) this.f3473a.get();
            if (yVar == null) {
                return null;
            }
            return new C0314z(this, yVar.getActivity(), yVar);
        }

        public void onLoaderReset(Loader<ArrayList<F>> loader) {
        }
    }

    static {
        f3467a.add("com.android.soundrecorder");
        f3467a.add("com.android.contacts");
        f3467a.add("com.android.browser");
        f3467a.add("com.mi.globalbrowser");
        f3467a.add("com.android.stk");
        f3467a.add("com.android.mms");
        f3467a.add("com.android.thememanager");
        f3467a.add("com.android.gallery3d");
        f3467a.add("com.android.updater");
        f3467a.add(AppConstants.Package.PACKAGE_NAME_FILE);
        f3467a.add("com.mi.android.globalFileexplorer");
        f3467a.add("com.android.calendar");
        f3467a.add("com.xiaomi.calendar");
        f3467a.add("com.android.vending");
        f3467a.add("com.android.apps.tag");
        f3467a.add("com.android.email");
        f3467a.add("com.android.providers.downloads.ui");
        f3467a.add("com.google.android.talk");
        f3467a.add("com.google.android.gm");
        f3467a.add("com.miui.camera");
        f3467a.add(SecurityManager.SKIP_INTERCEPT_PACKAGE);
        f3467a.add("com.miui.player");
        f3467a.add("com.miui.backup");
        f3467a.add("com.miui.notes");
        f3467a.add("com.xiaomi.market");
        f3467a.add("com.miui.antispam");
        f3467a.add("com.miui.video");
        f3467a.add("com.miui.screenrecorder");
        f3467a.add("net.cactii.flash2");
        f3467a.add("com.xiaomi.gamecenter");
        f3467a.add("com.xiaomi.gamecenter.pad");
        f3467a.add("com.google.android.music");
        f3467a.add("com.google.android.youtube");
        f3467a.add("com.google.android.apps.plus");
        f3467a.add("com.facebook.orca");
        f3467a.add("com.android.chrome");
        f3467a.add("com.xiaomi.vipaccount");
        f3467a.add("com.xiaomi.payment");
        f3467a.add("com.mipay.wallet");
        f3467a.add("com.xiaomi.jr");
        f3467a.add(Constants.CLOUDSERVICE_PACKAGE_NAME);
        f3467a.add("com.xiaomi.scanner");
        f3467a.add("com.android.settings");
        f3467a.add("com.google.android.apps.docs");
        f3467a.add("com.google.android.apps.photos");
        f3467a.add("com.google.android.apps.maps");
        f3467a.add("com.google.android.videos");
        f3467a.add("com.xiaomi.midrop");
        f3467a.add("com.miui.videoplayer");
        f3467a.add("com.miui.voiceassist");
        f3467a.add("com.android.quicksearchbox");
        f3467a.add(HybirdServiceUtil.HYBIRD_PACKAGE_NAME);
        f3467a.add("com.google.android.contacts");
        f3467a.add("com.google.android.dialer");
        f3467a.add("com.google.android.apps.messaging");
        f3467a.add("com.xiaomi.mipicks");
        f3467a.add("com.google.android.apps.tachyon");
        f3467a.add("com.mipay.wallet.in");
        f3467a.add("com.htc.album");
        f3468b.add(AppConstants.Package.PACKAGE_NAME_MM);
        f3468b.add(AppConstants.Package.PACKAGE_NAME_QQ);
        f3468b.add("com.android.mms");
        f3468b.add("com.google.android.apps.messaging");
        f3468b.add(SecurityManager.SKIP_INTERCEPT_PACKAGE);
        f3468b.add("com.android.contacts");
        f3468b.add("com.google.android.contacts");
        f3468b.add(AppConstants.Package.PACKAGE_NAME_ALIPAY);
        f3468b.add("jp.naver.line.android");
        f3468b.add("com.whatsapp");
        f3468b.add("com.bbm");
        f3468b.add("com.bsb.hike");
        f3468b.add("com.facebook.orca");
        f3468b.add("com.viber.voip");
        f3468b.add("com.taobao.taobao");
        f3468b.add("com.tmall.wireless");
        f3468b.add("com.immomo.momo");
        f3468b.add("com.jingdong.app.mall");
        f3468b.add("com.miui.notes");
        f3468b.add("com.mipay.wallet");
        f3468b.add("com.android.email");
        f3468b.add("com.facebook.katana");
        f3468b.add("com.wumii.android.mimi");
        f3468b.add("com.mi.android.globalFileexplorer");
        f3468b.add("com.miui.videoplayer");
        f3468b.add("com.android.browser");
        f3468b.add("com.mi.globalbrowser");
        f3468b.add("com.android.chrome");
        f3468b.add("com.google.android.youtube");
        f3468b.add("com.instagram.android");
        f3468b.add("com.vkontakte.android");
    }

    /* access modifiers changed from: private */
    public void a(String str) {
        ArrayList arrayList = new ArrayList();
        int size = this.j.size();
        F f2 = new F();
        ArrayList arrayList2 = new ArrayList();
        f2.a((List<C0257a>) arrayList2);
        for (int i2 = 0; i2 < size; i2++) {
            for (C0257a next : this.j.get(i2).a()) {
                if (next.a().toLowerCase().indexOf(str.toLowerCase()) >= 0) {
                    arrayList2.add(next);
                }
            }
        }
        arrayList.add(f2);
        f2.a(getResources().getQuantityString(R.plurals.found_apps_title, arrayList2.size(), new Object[]{Integer.valueOf(arrayList2.size())}));
        this.f.a((List<F>) arrayList, true);
        a((ArrayList<F>) arrayList);
    }

    private void a(ArrayList<F> arrayList) {
        this.f3469c.b(this.C);
        this.f3469c.b(this.D);
        HashMap hashMap = new HashMap();
        int i2 = 0;
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            for (int i4 = 0; i4 < arrayList.get(i3).a().size(); i4++) {
                F f2 = new F();
                f2.a(arrayList.get(i3).b());
                f2.a(arrayList.get(i3).c());
                hashMap.put(Integer.valueOf(i4 + i2), f2);
            }
            i2 += arrayList.get(i3).a().size();
        }
        this.D = f.a.a((com.miui.common.stickydecoration.b.c) new C0291o(this, hashMap)).a();
        this.f3469c.a(this.D);
    }

    static /* synthetic */ int c(C0312y yVar) {
        int i2 = yVar.n + 1;
        yVar.n = i2;
        return i2;
    }

    /* access modifiers changed from: private */
    public void d() {
        e();
        this.f3469c.b(this.C);
        this.f3469c.b(this.D);
        HashMap hashMap = new HashMap();
        int i2 = 0;
        for (int i3 = 0; i3 < this.j.size(); i3++) {
            for (int i4 = 0; i4 < this.j.get(i3).a().size(); i4++) {
                F f2 = new F();
                f2.a(this.j.get(i3).b());
                f2.a(this.j.get(i3).c());
                hashMap.put(Integer.valueOf(i4 + i2), f2);
            }
            i2 += this.j.get(i3).a().size();
        }
        f.a a2 = f.a.a((com.miui.common.stickydecoration.b.c) new C0287m(this, hashMap));
        a2.a((com.miui.common.stickydecoration.b.b) new C0289n(this));
        this.C = a2.a();
        this.f3469c.a(this.C);
    }

    private void e() {
        String quantityString;
        Iterator<F> it = this.j.iterator();
        int i2 = 0;
        int i3 = 0;
        while (it.hasNext()) {
            F next = it.next();
            if (next.c() != null) {
                for (C0257a f2 : next.a()) {
                    if (f2.f()) {
                        i3++;
                    } else {
                        i2++;
                    }
                }
            }
        }
        Iterator<F> it2 = this.j.iterator();
        while (it2.hasNext()) {
            F next2 = it2.next();
            G c2 = next2.c();
            if (c2 != null) {
                if (c2 == G.RECOMMEND) {
                    quantityString = this.z.getResources().getString(R.string.applock_app_recommend_lock_title);
                } else if (c2 == G.ENABLED) {
                    quantityString = this.z.getResources().getQuantityString(R.plurals.number_locked, i3, new Object[]{Integer.valueOf(i3)});
                } else {
                    quantityString = this.z.getResources().getQuantityString(R.plurals.number_to_lock, i2, new Object[]{Integer.valueOf(i2)});
                }
                next2.a(quantityString);
            }
        }
    }

    /* access modifiers changed from: private */
    public void f() {
        this.p = new AlertDialog.Builder(getActivity()).create();
        View inflate = this.z.getLayoutInflater().inflate(R.layout.guide_face_unlock_dialog, (ViewGroup) null);
        this.s = (TextView) inflate.findViewById(R.id.confirm_face_unlock_view_msg);
        this.s.setText(R.string.face_unlock_verity_dialog_summary);
        this.p.setTitle(R.string.applock_face_unlock_title);
        this.p.setView(inflate);
        this.p.setButton(-2, getResources().getString(R.string.cancel), this.I);
        this.p.show();
        this.p.setOnDismissListener(new C0310x(this));
    }

    /* access modifiers changed from: private */
    public void g() {
        this.o = new AlertDialog.Builder(getActivity()).create();
        this.o.setTitle(getResources().getString(R.string.fingerprint_identify_msg));
        View inflate = this.z.getLayoutInflater().inflate(R.layout.confirm_fingerprint_dialog, (ViewGroup) null);
        this.r = (TextView) inflate.findViewById(R.id.confirm_fingerprint_view_msg);
        this.o.setView(inflate);
        this.o.setButton(-2, getResources().getString(R.string.cancel), this.H);
        this.o.show();
        this.o.setOnDismissListener(new C0306w(this));
    }

    /* access modifiers changed from: private */
    public void h() {
        this.o = new C0309b(this.z, R.style.Fod_Dialog_Fullscreen, this.l);
        Animation loadAnimation = AnimationUtils.loadAnimation(this.z, R.anim.fod_finger_appear);
        View inflate = this.z.getLayoutInflater().inflate(R.layout.applock_fod_fingerprint_window, (ViewGroup) null);
        this.r = (TextView) inflate.findViewById(R.id.confirm_fingerprint_view_msg);
        inflate.setAnimation(loadAnimation);
        this.o.show();
        this.o.setContentView(inflate);
        this.o.setOnDismissListener(new C0254b(this));
        ((TextView) inflate.findViewById(R.id.cancel_finger_authenticate)).setOnClickListener(new C0256c(this));
    }

    /* access modifiers changed from: private */
    public void i() {
        new AlertDialog.Builder(getActivity()).setTitle(R.string.applock_face_unlock_title).setNegativeButton(getResources().getString(R.string.cancal_to_setting_fingerprint), new C0277h(this)).setPositiveButton(getResources().getString(R.string.face_unlock_guide_confirm), new C0275g(this)).setOnDismissListener(new C0273f(this)).setView(this.z.getLayoutInflater().inflate(R.layout.guide_face_unlock_dialog, (ViewGroup) null)).create().show();
    }

    /* access modifiers changed from: private */
    public void j() {
        this.q = new AlertDialog.Builder(getActivity()).setTitle(R.string.fingerprint_remind_dialog_title).setMessage(R.string.finger_remind_message).setNegativeButton(getResources().getString(R.string.cancal_to_setting_fingerprint), new C0271e(this)).setPositiveButton(getResources().getString(R.string.go_to_setting_fingerprint), new C0269d(this)).create();
        this.q.show();
    }

    /* access modifiers changed from: private */
    public void k() {
        if (o.r()) {
            new r(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    private void l() {
        E e2;
        this.u = this.z.getIntent().getStringExtra("external_app_name");
        int b2 = o.b(this.u);
        if (TextUtils.isEmpty(this.u) || b2 >= 2) {
            e2 = new E(this.z, false, new Handler());
        } else {
            o.b(this.u, b2 + 1);
            e2 = new E(this.z, true, new Handler());
        }
        this.f = e2;
    }

    /* access modifiers changed from: private */
    public void m() {
        this.n = 0;
        Settings.Secure.putInt(this.z.getContentResolver(), i.f3250a, 1);
        this.l.a();
        o.b((Context) this.z, true);
    }

    /* access modifiers changed from: private */
    public void n() {
        this.x.a((Runnable) new C0252a(this));
    }

    public void a() {
        if (this.i != null) {
            this.i = null;
        }
    }

    public void a(SearchActionMode.Callback callback) {
        this.i = getActivity().startActionMode(callback);
    }

    public void b() {
        if (!this.x.c() || !o.q()) {
            k();
        } else {
            new C0300t(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    public boolean c() {
        return this.i != null;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.v = g.a(this.z.getApplicationContext());
        this.t = C0259c.b(this.z.getApplicationContext());
        if (this.t.j()) {
            this.t.e(false);
        }
        this.k = new ArrayList<>();
        this.j = new ArrayList<>();
        this.B = new c(this, (C0283k) null);
        if (bundle != null) {
            getLoaderManager().restartLoader(112, (Bundle) null, this.B);
        } else {
            getLoaderManager().initLoader(112, (Bundle) null, this.B);
        }
        l();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.z);
        boolean z2 = true;
        linearLayoutManager.j(1);
        this.f3469c.setLayoutManager(linearLayoutManager);
        this.f3469c.setAdapter(this.f);
        this.f.a((E.a) new C0295q(this));
        this.f3469c.setSpringEnabled(false);
        this.g = (SecurityManager) this.z.getSystemService("security");
        this.l = com.miui.applicationlock.c.E.a((Context) getActivity());
        this.x = C.a(this.z.getApplicationContext());
        this.A = this.t.f();
        if (!TransitionHelper.a(getActivity()) || !this.l.d() || !this.l.c() || !this.t.i()) {
            this.t.d(false);
        } else {
            this.t.d(true);
        }
        if (bundle != null && bundle.containsKey("is_show_dialog")) {
            z2 = bundle.getBoolean("is_show_dialog");
        }
        if (z2) {
            b();
        }
    }

    public void onActivityResult(int i2, int i3, Intent intent) {
        ((PrivacyAndAppLockManageActivity) getActivity()).onActivityResult(i2, i3, intent);
        if (i2 != 30) {
            if (i2 == 34) {
                if (this.x.a()) {
                    this.t.c(true);
                }
            } else {
                return;
            }
        } else if (i3 != -1) {
            this.t.d(false);
        } else if (o.d(this.z, this.v)) {
            this.t.d(true);
        }
        ((PrivacyAndAppLockManageActivity) getActivity()).a(true);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.z = activity;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.m = getResources().getConfiguration().locale.getLanguage();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.jump_lock_page, (ViewGroup) null);
        this.f3469c = (miuix.recyclerview.widget.RecyclerView) inflate.findViewById(R.id.listnolockapps);
        this.h = layoutInflater;
        this.e = inflate.findViewById(R.id.search_view);
        this.f3470d = (TextView) this.e.findViewById(16908297);
        this.e.setOnClickListener(this.E);
        return inflate;
    }

    public void onDestroy() {
        super.onDestroy();
        this.l.a();
        ArrayList<C0257a> arrayList = this.k;
        if (arrayList != null) {
            long j2 = 0;
            Iterator<C0257a> it = arrayList.iterator();
            while (it.hasNext()) {
                if (it.next().f()) {
                    j2++;
                }
            }
            com.miui.common.persistence.b.b("locked_app_quantity1", j2);
        }
    }

    public void onPause() {
        super.onPause();
        this.l.a();
        AlertDialog alertDialog = this.o;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        AlertDialog alertDialog2 = this.q;
        if (alertDialog2 != null) {
            alertDialog2.dismiss();
        }
    }

    public void onResume() {
        AlertDialog alertDialog = this.o;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.l.a((q) new a(this, (C0283k) null), 1);
        }
        if (this.A != this.t.f()) {
            this.A = this.t.f();
            getLoaderManager().restartLoader(112, (Bundle) null, this.B);
            Log.d("AppLockManageFragment", "loader restart");
        }
        super.onResume();
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("is_show_dialog", false);
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }
}
