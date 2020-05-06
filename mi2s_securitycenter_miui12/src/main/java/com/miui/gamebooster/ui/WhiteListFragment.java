package com.miui.gamebooster.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.c.b.d;
import b.b.c.j.B;
import b.b.c.j.x;
import b.b.o.g.e;
import com.miui.applicationlock.c.F;
import com.miui.common.stickydecoration.b.c;
import com.miui.common.stickydecoration.f;
import com.miui.gamebooster.a.b.a.b;
import com.miui.gamebooster.customview.b.f;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.model.C0398d;
import com.miui.gamebooster.model.k;
import com.miui.gamebooster.model.l;
import com.miui.gamebooster.view.q;
import com.miui.gamebooster.view.r;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.app.ProgressDialog;
import miui.view.SearchActionMode;
import miuix.recyclerview.widget.RecyclerView;

public class WhiteListFragment extends d implements LoaderManager.LoaderCallbacks<ArrayList<k>>, q, View.OnClickListener {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f5024a = "com.miui.gamebooster.ui.WhiteListFragment";
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public ArrayList<k> f5025b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public RecyclerView f5026c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public View f5027d;
    private TextView e;
    private View f;
    /* access modifiers changed from: private */
    public f g;
    protected SearchActionMode h;
    private r i;
    private View j;
    private ProgressDialog k;
    /* access modifiers changed from: private */
    public Map<Integer, Integer> l;
    private RecyclerView.f m;
    CompoundButton.OnCheckedChangeListener n = new Xa(this);
    private View.OnClickListener o = new Ya(this);
    /* access modifiers changed from: private */
    public TextWatcher p = new _a(this);
    /* access modifiers changed from: private */
    public SearchActionMode.Callback q = new ab(this);

    static class a extends b.b.c.i.a<ArrayList<k>> {

        /* renamed from: b  reason: collision with root package name */
        private PackageManager f5028b = getContext().getPackageManager();

        /* renamed from: c  reason: collision with root package name */
        private Object f5029c;

        public a(Context context) {
            super(context);
            try {
                this.f5029c = e.a(Class.forName("android.app.AppGlobals"), "getPackageManager", (Class<?>[]) null, new Object[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private ApplicationInfo a(String str, int i) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            try {
                return (ApplicationInfo) e.a(this.f5029c, ApplicationInfo.class, "getApplicationInfo", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Integer.TYPE}, str, 8192, Integer.valueOf(i));
            } catch (Exception unused) {
                return null;
            }
        }

        /* JADX WARNING: Can't wrap try/catch for region: R(2:20|21) */
        /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
            com.miui.gamebooster.m.C0391w.a(r9, r2, r1, true, 1);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0049, code lost:
            r9 = th;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:20:0x0044 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private java.util.ArrayList<android.content.pm.ApplicationInfo> a(android.content.Context r9) {
            /*
                r8 = this;
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                if (r9 != 0) goto L_0x0008
                return r0
            L_0x0008:
                r1 = -1
                r2 = 0
                r3 = 1
                android.database.Cursor r4 = com.miui.gamebooster.m.C0391w.a((android.content.Context) r9, (int) r3)     // Catch:{ Exception -> 0x0043, all -> 0x0040 }
                if (r4 == 0) goto L_0x003c
            L_0x0011:
                boolean r5 = r4.moveToNext()     // Catch:{ Exception -> 0x0044 }
                if (r5 == 0) goto L_0x003c
                java.lang.String r5 = "package_name"
                int r5 = r4.getColumnIndex(r5)     // Catch:{ Exception -> 0x0044 }
                java.lang.String r2 = r4.getString(r5)     // Catch:{ Exception -> 0x0044 }
                java.lang.String r5 = "package_uid"
                int r5 = r4.getColumnIndex(r5)     // Catch:{ Exception -> 0x0044 }
                int r1 = r4.getInt(r5)     // Catch:{ Exception -> 0x0044 }
                android.content.pm.ApplicationInfo r5 = r8.a(r2, r1)     // Catch:{ Exception -> 0x0044 }
                if (r5 == 0) goto L_0x0011
                int r6 = r5.flags     // Catch:{ Exception -> 0x0044 }
                r7 = 8388608(0x800000, float:1.17549435E-38)
                r6 = r6 & r7
                if (r6 == 0) goto L_0x0011
                r0.add(r5)     // Catch:{ Exception -> 0x0044 }
                goto L_0x0011
            L_0x003c:
                miui.util.IOUtils.closeQuietly(r4)
                goto L_0x0048
            L_0x0040:
                r9 = move-exception
                r4 = r2
                goto L_0x004a
            L_0x0043:
                r4 = r2
            L_0x0044:
                com.miui.gamebooster.m.C0391w.a((android.content.Context) r9, (java.lang.String) r2, (int) r1, (boolean) r3, (int) r3)     // Catch:{ all -> 0x0049 }
                goto L_0x003c
            L_0x0048:
                return r0
            L_0x0049:
                r9 = move-exception
            L_0x004a:
                miui.util.IOUtils.closeQuietly(r4)
                throw r9
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.ui.WhiteListFragment.a.a(android.content.Context):java.util.ArrayList");
        }

        public ArrayList<k> loadInBackground() {
            List<ApplicationInfo> a2;
            ArrayList<k> arrayList = new ArrayList<>();
            Context context = getContext();
            if (context == null) {
                return arrayList;
            }
            ArrayList<ApplicationInfo> a3 = a(context);
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            List<ApplicationInfo> arrayList4 = new ArrayList<>();
            try {
                arrayList4 = b.b.c.d.a(0, 0);
                if (B.j() == 0 && (a2 = b.b.c.d.a(0, 999)) != null) {
                    arrayList4.addAll(a2);
                }
            } catch (Exception e) {
                Log.i(WhiteListFragment.f5024a, e.toString());
            }
            ArrayList arrayList5 = new ArrayList();
            ArrayList arrayList6 = new ArrayList();
            if (a3 != null && a3.size() > 0) {
                Iterator<ApplicationInfo> it = a3.iterator();
                while (it.hasNext()) {
                    ApplicationInfo next = it.next();
                    arrayList5.add(Integer.valueOf(next.uid));
                    arrayList6.add(next.packageName);
                }
            }
            for (ApplicationInfo applicationInfo : arrayList4) {
                if (x.a(applicationInfo) && this.f5028b.getLaunchIntentForPackage(applicationInfo.packageName) != null) {
                    if (!arrayList5.contains(Integer.valueOf(applicationInfo.uid)) || !arrayList6.contains(applicationInfo.packageName)) {
                        arrayList2.add(new C0398d(applicationInfo, false, applicationInfo.loadLabel(this.f5028b), applicationInfo.loadIcon(this.f5028b)));
                    } else {
                        arrayList3.add(new C0398d(applicationInfo, true, applicationInfo.loadLabel(this.f5028b), applicationInfo.loadIcon(this.f5028b)));
                    }
                }
            }
            if (!arrayList3.isEmpty()) {
                k kVar = new k();
                kVar.a(l.ENABLED);
                kVar.a(context.getResources().getQuantityString(R.plurals.install_game_count_title, arrayList3.size(), new Object[]{Integer.valueOf(arrayList3.size())}));
                kVar.a(arrayList3.size());
                kVar.a((ArrayList<C0398d>) new ArrayList(arrayList3));
                arrayList.add(kVar);
            }
            if (!arrayList2.isEmpty()) {
                k kVar2 = new k();
                kVar2.a(l.DISABLED);
                kVar2.a(context.getResources().getQuantityString(R.plurals.uninstall_game_count_title, arrayList2.size(), new Object[]{Integer.valueOf(arrayList2.size())}));
                kVar2.a(arrayList2.size());
                kVar2.a((ArrayList<C0398d>) new ArrayList(arrayList2));
                arrayList.add(kVar2);
            }
            return arrayList;
        }
    }

    /* access modifiers changed from: private */
    public void f() {
        if (!h()) {
            this.f5026c.b(this.m);
            HashMap hashMap = new HashMap();
            int i2 = 0;
            for (int i3 = 0; i3 < this.f5025b.size(); i3++) {
                for (int i4 = 0; i4 < this.f5025b.get(i3).a().size(); i4++) {
                    F f2 = new F();
                    f2.a(this.f5025b.get(i3).b());
                    hashMap.put(Integer.valueOf(i4 + i2), f2);
                }
                i2 += this.f5025b.get(i3).a().size();
            }
            f.a a2 = f.a.a((c) new Za(this, hashMap));
            a2.a(this.mActivity.getResources().getDimensionPixelOffset(R.dimen.view_dimen_136));
            this.m = a2.a();
            this.f5026c.a(this.m);
        }
    }

    private void g() {
        ProgressDialog progressDialog = this.k;
        if (progressDialog != null) {
            progressDialog.dismiss();
            this.k = null;
        }
    }

    /* access modifiers changed from: private */
    public boolean h() {
        Activity activity = getActivity();
        return activity != null && 6 == activity.getRequestedOrientation();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager;
        Activity activity = getActivity();
        if (activity != null && (inputMethodManager = (InputMethodManager) activity.getSystemService("input_method")) != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void i() {
        if (this.k == null) {
            this.k = ProgressDialog.show(getActivity(), (CharSequence) null, getString(R.string.gb_add_game_loading_tips));
        }
        this.k.show();
    }

    /* access modifiers changed from: private */
    public void startSearchMode(SearchActionMode.Callback callback) {
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            this.h = activity.startActionMode(callback);
        }
    }

    /* access modifiers changed from: private */
    public void updateSearchResult(String str) {
        Activity activity = getActivity();
        if (activity != null && !activity.isDestroyed() && !activity.isFinishing()) {
            ArrayList arrayList = new ArrayList();
            int size = this.f5025b.size();
            k kVar = new k();
            ArrayList arrayList2 = new ArrayList();
            kVar.a((ArrayList<C0398d>) arrayList2);
            for (int i2 = 0; i2 < size; i2++) {
                Iterator<C0398d> it = this.f5025b.get(i2).a().iterator();
                while (it.hasNext()) {
                    C0398d next = it.next();
                    if (x.a((Context) activity, next.b()).toLowerCase().contains(str.toLowerCase())) {
                        arrayList2.add(next);
                    }
                }
            }
            arrayList.add(kVar);
            kVar.a(getResources().getQuantityString(R.plurals.found_apps_title, arrayList2.size(), new Object[]{Integer.valueOf(arrayList2.size())}));
            a((List<k>) arrayList, false);
            f();
        }
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<ArrayList<k>> loader, ArrayList<k> arrayList) {
        g();
        this.f5025b = arrayList;
        Iterator<k> it = arrayList.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            i2 += it.next().d();
        }
        String format = String.format(getResources().getQuantityString(R.plurals.find_applications, i2), new Object[]{Integer.valueOf(i2)});
        this.e.setHint(format);
        this.e.setContentDescription(format);
        a((List<k>) this.f5025b, true);
        f();
    }

    public /* synthetic */ void a(View view, boolean z) {
        if (!z) {
            hideKeyboard(view);
        }
    }

    public void a(r rVar) {
        this.i = rVar;
    }

    public void a(List<k> list, boolean z) {
        if (h() && z) {
            if (this.l == null) {
                this.l = new HashMap();
            }
            this.l.clear();
        }
        this.g.b();
        for (int i2 = 0; i2 < list.size(); i2++) {
            k kVar = list.get(i2);
            if (h() && z) {
                this.l.put(Integer.valueOf(i2), Integer.valueOf(this.g.getItemCount()));
                this.g.a(new C0398d((ApplicationInfo) null, false, kVar.b(), (Drawable) null));
            }
            this.g.a(kVar.a());
        }
        this.g.notifyDataSetChanged();
    }

    public void exitSearchMode() {
        if (this.h != null) {
            this.h = null;
        }
    }

    /* access modifiers changed from: protected */
    public void initView() {
        Activity activity = getActivity();
        if (!Utils.a(activity)) {
            this.f5026c = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.list_view);
            boolean z = false;
            this.f5026c.setSpringEnabled(false);
            this.f5026c.setLayoutManager(new LinearLayoutManager(activity));
            this.f = findViewById(R.id.empty_view);
            this.g = new com.miui.gamebooster.customview.b.f(activity);
            this.g.a(new com.miui.gamebooster.a.b.a.a(this.mActivity.getRequestedOrientation() == 6));
            com.miui.gamebooster.customview.b.f fVar = this.g;
            if (this.mActivity.getRequestedOrientation() == 6) {
                z = true;
            }
            fVar.a(new b(z, this.n));
            this.f5026c.setAdapter(this.g);
            this.f5027d = findViewById(R.id.search_view);
            this.e = (TextView) this.f5027d.findViewById(16908297);
            this.j = findViewById(R.id.backBtn);
            View view = this.j;
            if (view != null) {
                view.setOnClickListener(this);
            }
            if (this.e == null) {
                this.e = (TextView) this.f5027d.findViewById(R.id.input);
                this.e.addTextChangedListener(this.p);
                this.e.setOnFocusChangeListener(new C0425g(this));
            } else {
                this.f5027d.setOnClickListener(this.o);
            }
            getLoaderManager().initLoader(112, (Bundle) null, this);
        }
    }

    public boolean isSearchMode() {
        return this.h != null || h();
    }

    public void onClick(View view) {
        r rVar;
        if (view == this.j && (rVar = this.i) != null) {
            rVar.pop();
        }
    }

    public Loader<ArrayList<k>> onCreateLoader(int i2, Bundle bundle) {
        i();
        return new a(getActivity());
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_fragment_white_list;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onLoaderReset(Loader<ArrayList<k>> loader) {
    }
}
