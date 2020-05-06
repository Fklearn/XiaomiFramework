package com.miui.gamebooster.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.f.a;
import b.b.c.j.x;
import b.b.o.g.e;
import com.miui.applicationlock.c.F;
import com.miui.common.persistence.b;
import com.miui.common.stickydecoration.b.c;
import com.miui.common.stickydecoration.f;
import com.miui.gamebooster.customview.b.f;
import com.miui.gamebooster.m.C0390v;
import com.miui.gamebooster.m.C0391w;
import com.miui.gamebooster.m.V;
import com.miui.gamebooster.model.C0398d;
import com.miui.gamebooster.model.k;
import com.miui.gamebooster.service.IGameBooster;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import miui.app.ProgressDialog;
import miui.util.IOUtils;
import miui.view.SearchActionMode;
import miuix.recyclerview.widget.RecyclerView;

public class SelectGameActivity extends b.b.c.c.a implements LoaderManager.LoaderCallbacks<ArrayList<k>> {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f4983a = "com.miui.gamebooster.ui.SelectGameActivity";
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public ArrayList<k> f4984b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public RecyclerView f4985c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public View f4986d;
    private TextView e;
    private View f;
    private f g;
    /* access modifiers changed from: private */
    public PackageManager h;
    protected SearchActionMode i;
    private Object j;
    /* access modifiers changed from: private */
    public IGameBooster k;
    /* access modifiers changed from: private */
    public ArrayList<String> l = new ArrayList<>();
    /* access modifiers changed from: private */
    public Object m = new Object();
    /* access modifiers changed from: private */
    public List<AsyncTask<Void, Void, Boolean>> n = new CopyOnWriteArrayList();
    private ProgressDialog o;
    private RecyclerView.f p;
    a.C0027a q = new Ga(this);
    private HashMap<ApplicationInfo, Boolean> r = new HashMap<>();
    CompoundButton.OnCheckedChangeListener s = new Ja(this);
    private View.OnClickListener t = new Ka(this);
    /* access modifiers changed from: private */
    public TextWatcher u = new La(this);
    /* access modifiers changed from: private */
    public SearchActionMode.Callback v = new Ma(this);

    private static class a extends AsyncTask<Void, Void, Boolean> {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<SelectGameActivity> f4987a;

        /* renamed from: b  reason: collision with root package name */
        private boolean f4988b;

        /* renamed from: c  reason: collision with root package name */
        private C0398d f4989c;

        public a(SelectGameActivity selectGameActivity, boolean z, C0398d dVar) {
            this.f4987a = new WeakReference<>(selectGameActivity);
            this.f4988b = z;
            this.f4989c = dVar;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Boolean doInBackground(Void... voidArr) {
            SelectGameActivity selectGameActivity = (SelectGameActivity) this.f4987a.get();
            if (selectGameActivity == null || isCancelled()) {
                return false;
            }
            String str = this.f4989c.b().packageName;
            String charSequence = x.j(selectGameActivity.getApplicationContext(), str).toString();
            int i = this.f4989c.b().uid;
            synchronized (selectGameActivity.m) {
                if (this.f4988b) {
                    selectGameActivity.l.add(str);
                    C0391w.a(selectGameActivity.getApplicationContext(), charSequence, str, i, 0);
                } else {
                    selectGameActivity.l.remove(str);
                    C0391w.a(selectGameActivity.getApplicationContext(), str, i, false, 0);
                    V.a("already_added_game", str, new ArrayList());
                }
                b.b("gb_added_games", (ArrayList<String>) selectGameActivity.l);
            }
            return true;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            SelectGameActivity selectGameActivity = (SelectGameActivity) this.f4987a.get();
            if (selectGameActivity != null && !selectGameActivity.isFinishing() && !selectGameActivity.isDestroyed()) {
                if (selectGameActivity.n.contains(this)) {
                    selectGameActivity.n.remove(this);
                }
                synchronized (selectGameActivity.m) {
                    if (selectGameActivity.k != null) {
                        try {
                            selectGameActivity.k.b((List<String>) selectGameActivity.l);
                        } catch (RemoteException e) {
                            Log.e(SelectGameActivity.f4983a, e.toString());
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(SelectGameActivity selectGameActivity, boolean z, C0398d dVar) {
        a aVar = new a(selectGameActivity, z, dVar);
        this.n.add(aVar);
        aVar.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* JADX WARNING: type inference failed for: r10v0, types: [android.content.Context, com.miui.gamebooster.ui.SelectGameActivity, miui.app.Activity] */
    /* access modifiers changed from: private */
    public void b(String str) {
        ArrayList arrayList = new ArrayList();
        int size = this.f4984b.size();
        k kVar = new k();
        ArrayList arrayList2 = new ArrayList();
        kVar.a((ArrayList<C0398d>) arrayList2);
        for (int i2 = 0; i2 < size; i2++) {
            Iterator<C0398d> it = this.f4984b.get(i2).a().iterator();
            while (it.hasNext()) {
                C0398d next = it.next();
                if (x.a((Context) this, next.b()).toLowerCase().indexOf(str.toLowerCase()) >= 0) {
                    arrayList2.add(next);
                }
            }
        }
        arrayList.add(kVar);
        kVar.a(getResources().getQuantityString(R.plurals.found_apps_title, arrayList2.size(), new Object[]{Integer.valueOf(arrayList2.size())}));
        a((List<k>) arrayList);
        o();
    }

    /* access modifiers changed from: private */
    public void o() {
        this.f4985c.b(this.p);
        HashMap hashMap = new HashMap();
        int i2 = 0;
        for (int i3 = 0; i3 < this.f4984b.size(); i3++) {
            for (int i4 = 0; i4 < this.f4984b.get(i3).a().size(); i4++) {
                F f2 = new F();
                f2.a(this.f4984b.get(i3).b());
                hashMap.put(Integer.valueOf(i4 + i2), f2);
            }
            i2 += this.f4984b.get(i3).a().size();
        }
        f.a a2 = f.a.a((c) new Ia(this, hashMap));
        a2.a(getResources().getDimensionPixelOffset(R.dimen.view_dimen_136));
        this.p = a2.a();
        this.f4985c.a(this.p);
    }

    private void p() {
        ProgressDialog progressDialog = this.o;
        if (progressDialog != null) {
            progressDialog.dismiss();
            this.o = null;
        }
    }

    private void q() {
        SharedPreferences sharedPreferences = getSharedPreferences("gb_gamead_data_config", 0);
        com.miui.gamebooster.globalgame.util.a.b();
        com.miui.gamebooster.globalgame.util.a.a(getApplication(), sharedPreferences);
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [android.content.Context, com.miui.gamebooster.ui.SelectGameActivity] */
    /* access modifiers changed from: private */
    public ArrayList<ApplicationInfo> r() {
        Cursor cursor;
        ArrayList<ApplicationInfo> arrayList = new ArrayList<>();
        String str = null;
        int i2 = -1;
        try {
            cursor = C0391w.a((Context) this, 0);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    try {
                        str = cursor.getString(cursor.getColumnIndex("package_name"));
                        i2 = cursor.getInt(cursor.getColumnIndex("package_uid"));
                        ApplicationInfo a2 = a(str, i2);
                        if (!(a2 == null || (a2.flags & 8388608) == 0)) {
                            arrayList.add(a2);
                        }
                    } catch (Exception unused) {
                        try {
                            C0391w.a((Context) this, str, i2, true, 1);
                            IOUtils.closeQuietly(cursor);
                            return arrayList;
                        } catch (Throwable th) {
                            th = th;
                        }
                    }
                }
            }
        } catch (Exception unused2) {
            cursor = null;
            C0391w.a((Context) this, str, i2, true, 1);
            IOUtils.closeQuietly(cursor);
            return arrayList;
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
            IOUtils.closeQuietly(cursor);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return arrayList;
    }

    private void s() {
        setResult(-1, new Intent());
        finish();
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.gamebooster.ui.SelectGameActivity, miui.app.Activity] */
    private void t() {
        if (this.o == null) {
            this.o = ProgressDialog.show(this, (CharSequence) null, getString(R.string.gb_add_game_loading_tips));
        }
        this.o.show();
    }

    public ApplicationInfo a(String str, int i2) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return (ApplicationInfo) e.a(this.j, ApplicationInfo.class, "getApplicationInfo", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Integer.TYPE}, str, 8192, Integer.valueOf(i2));
        } catch (Exception unused) {
            return null;
        }
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<ArrayList<k>> loader, ArrayList<k> arrayList) {
        p();
        this.f4984b = arrayList;
        Iterator<k> it = arrayList.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            i2 += it.next().d();
        }
        String format = String.format(getResources().getQuantityString(R.plurals.find_applications, i2), new Object[]{Integer.valueOf(i2)});
        this.e.setHint(format);
        this.e.setContentDescription(format);
        a((List<k>) this.f4984b);
        o();
    }

    public void a(List<k> list) {
        this.g.b();
        for (int i2 = 0; i2 < list.size(); i2++) {
            this.g.a(list.get(i2).a());
        }
        this.g.notifyDataSetChanged();
    }

    public void a(SearchActionMode.Callback callback) {
        this.i = startActionMode(callback);
    }

    public void m() {
        if (this.i != null) {
            this.i = null;
        }
    }

    public boolean n() {
        return this.i != null;
    }

    public void onBackPressed() {
        s();
        finish();
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [b.b.c.c.a, android.content.Context, android.app.LoaderManager$LoaderCallbacks, com.miui.gamebooster.ui.SelectGameActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.select_game_layout);
        this.h = getPackageManager();
        boolean z = false;
        try {
            this.j = e.a(Class.forName("android.app.AppGlobals"), "getPackageManager", (Class<?>[]) null, new Object[0]);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        C0390v.a((Context) this).a(this.q);
        this.f4985c = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.list_view);
        this.f4985c.setSpringEnabled(false);
        this.f4985c.setLayoutManager(new LinearLayoutManager(this));
        this.f4986d = findViewById(R.id.search_view);
        this.f = findViewById(R.id.empty_view);
        this.g = new com.miui.gamebooster.customview.b.f(this);
        com.miui.gamebooster.customview.b.f fVar = this.g;
        if (getRequestedOrientation() == 6) {
            z = true;
        }
        fVar.a(new com.miui.gamebooster.a.b.a.b(z, this.s));
        this.f4985c.setAdapter(this.g);
        this.f4986d = findViewById(R.id.search_view);
        this.e = (TextView) this.f4986d.findViewById(16908297);
        this.f4986d.setOnClickListener(this.t);
        q();
        getLoaderManager().initLoader(112, (Bundle) null, this);
        ArrayList<String> stringArrayListExtra = getIntent().getStringArrayListExtra("addedGames");
        if (stringArrayListExtra != null && !stringArrayListExtra.isEmpty()) {
            this.l.clear();
            this.l.addAll(stringArrayListExtra);
        }
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.gamebooster.ui.SelectGameActivity] */
    public Loader<ArrayList<k>> onCreateLoader(int i2, Bundle bundle) {
        t();
        return new Ha(this, this);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.gamebooster.ui.SelectGameActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onDestroy() {
        SelectGameActivity.super.onDestroy();
        for (AsyncTask next : this.n) {
            if (next != null) {
                next.cancel(true);
            }
        }
        C0390v.a((Context) this).a();
    }

    public void onLoaderReset(Loader<ArrayList<k>> loader) {
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        s();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
    }
}
