package com.miui.gamebooster.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import b.b.c.d;
import b.b.c.f.a;
import b.b.c.j.B;
import b.b.c.j.e;
import b.b.c.j.i;
import b.b.c.j.x;
import com.miui.gamebooster.a.G;
import com.miui.gamebooster.m.C0390v;
import com.miui.gamebooster.m.C0391w;
import com.miui.gamebooster.m.Q;
import com.miui.gamebooster.m.V;
import com.miui.gamebooster.m.fa;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.model.C0398d;
import com.miui.gamebooster.service.IGameBooster;
import com.miui.gamebooster.videobox.settings.f;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import miui.app.ProgressDialog;
import miui.util.IOUtils;

public class SelectGameLandActivity extends b.b.c.c.a implements LoaderManager.LoaderCallbacks<Pair>, G.a, View.OnClickListener, TextWatcher, View.OnFocusChangeListener {

    /* renamed from: a  reason: collision with root package name */
    private TextView f4990a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f4991b;

    /* renamed from: c  reason: collision with root package name */
    private ListView f4992c;

    /* renamed from: d  reason: collision with root package name */
    private ListView f4993d;
    private View e;
    private TextView f;
    private View g;
    private G h;
    private G i;
    private ArrayList<C0398d> j = new ArrayList<>();
    private ArrayList<C0398d> k = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<String> l = new ArrayList<>();
    /* access modifiers changed from: private */
    public IGameBooster m;
    /* access modifiers changed from: private */
    public final Object n = new Object();
    /* access modifiers changed from: private */
    public List<AsyncTask<Void, Void, Boolean>> o = new CopyOnWriteArrayList();
    private ProgressDialog p;
    a.C0027a q = new Na(this);

    private static class a extends b.b.c.i.a<Pair> {

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<Activity> f4994b;

        private a(Activity activity) {
            super(activity);
            this.f4994b = new WeakReference<>(activity);
        }

        /* synthetic */ a(Activity activity, Na na) {
            this(activity);
        }

        public Pair loadInBackground() {
            PackageManager packageManager;
            List<ApplicationInfo> a2;
            Activity activity = (Activity) this.f4994b.get();
            List<ApplicationInfo> list = null;
            if (activity == null || (packageManager = activity.getPackageManager()) == null) {
                return null;
            }
            ArrayList a3 = SelectGameLandActivity.b((Context) activity);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            Pair pair = new Pair(arrayList2, arrayList);
            try {
                list = d.a(0, B.c());
                if (!(B.j() != 0 || list == null || (a2 = d.a(0, 999)) == null)) {
                    list.addAll(a2);
                }
            } catch (Exception e) {
                Log.i("SelectGameLandActivity", e.toString());
            }
            if (list == null) {
                return pair;
            }
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();
            Iterator it = a3.iterator();
            while (it.hasNext()) {
                ApplicationInfo applicationInfo = (ApplicationInfo) it.next();
                arrayList3.add(Integer.valueOf(applicationInfo.uid));
                arrayList4.add(applicationInfo.packageName);
            }
            ArrayList<String> a4 = f.a((ArrayList<String>) new ArrayList());
            for (ApplicationInfo next : list) {
                if (!a4.contains(next.packageName) && x.a(next) && !e.a((Context) activity, next.packageName, 0) && packageManager.getLaunchIntentForPackage(next.packageName) != null) {
                    if (!arrayList3.contains(Integer.valueOf(next.uid)) || !arrayList4.contains(next.packageName)) {
                        arrayList.add(new C0398d(next, false, next.loadLabel(packageManager), next.loadIcon(packageManager)));
                    } else {
                        arrayList2.add(new C0398d(next, true, next.loadLabel(packageManager), next.loadIcon(packageManager)));
                    }
                }
            }
            return pair;
        }
    }

    private static class b extends AsyncTask<Void, Void, Boolean> {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<SelectGameLandActivity> f4995a;

        /* renamed from: b  reason: collision with root package name */
        private boolean f4996b;

        /* renamed from: c  reason: collision with root package name */
        private C0398d f4997c;

        b(SelectGameLandActivity selectGameLandActivity, boolean z, C0398d dVar) {
            this.f4995a = new WeakReference<>(selectGameLandActivity);
            this.f4996b = z;
            this.f4997c = dVar;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Boolean doInBackground(Void... voidArr) {
            SelectGameLandActivity selectGameLandActivity = (SelectGameLandActivity) this.f4995a.get();
            if (selectGameLandActivity == null || isCancelled()) {
                return false;
            }
            String str = this.f4997c.b().packageName;
            String charSequence = x.j(selectGameLandActivity.getApplicationContext(), str).toString();
            int i = this.f4997c.b().uid;
            synchronized (selectGameLandActivity.n) {
                if (this.f4996b) {
                    selectGameLandActivity.l.add(str);
                    C0391w.a(selectGameLandActivity.getApplicationContext(), charSequence, str, i, 0);
                } else {
                    selectGameLandActivity.l.remove(str);
                    C0391w.a(selectGameLandActivity.getApplicationContext(), str, i, false, 0);
                    V.a("already_added_game", str, new ArrayList());
                }
                com.miui.common.persistence.b.b("gb_added_games", (ArrayList<String>) selectGameLandActivity.l);
            }
            return true;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            SelectGameLandActivity selectGameLandActivity = (SelectGameLandActivity) this.f4995a.get();
            if (selectGameLandActivity != null && !selectGameLandActivity.isFinishing() && !selectGameLandActivity.isDestroyed()) {
                selectGameLandActivity.o.remove(this);
                synchronized (selectGameLandActivity.n) {
                    if (selectGameLandActivity.m != null) {
                        try {
                            selectGameLandActivity.m.b((List<String>) selectGameLandActivity.l);
                        } catch (RemoteException e) {
                            Log.e("SelectGameLandActivity", e.toString());
                        }
                    }
                }
            }
        }
    }

    private static ApplicationInfo a(Object obj, String str, int i2) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return (ApplicationInfo) b.b.o.g.e.a(obj, ApplicationInfo.class, "getApplicationInfo", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Integer.TYPE}, str, 8192, Integer.valueOf(i2));
        } catch (Exception unused) {
            return null;
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.gamebooster.ui.SelectGameLandActivity] */
    private ArrayList<C0398d> a(ArrayList<C0398d> arrayList, String str) {
        ArrayList<C0398d> arrayList2 = new ArrayList<>();
        Iterator<C0398d> it = arrayList.iterator();
        while (it.hasNext()) {
            C0398d next = it.next();
            if (x.a((Context) this, next.b()).toLowerCase().contains(str.toLowerCase())) {
                arrayList2.add(next);
            }
        }
        return arrayList2;
    }

    private void a(SelectGameLandActivity selectGameLandActivity, boolean z, C0398d dVar) {
        b bVar = new b(selectGameLandActivity, z, dVar);
        this.o.add(bVar);
        bVar.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void a(List<C0398d> list, List<C0398d> list2) {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(list);
        arrayList.addAll(list2);
        Iterator it = arrayList.iterator();
        int i2 = 0;
        int i3 = 0;
        while (it.hasNext()) {
            if (((C0398d) it.next()).e()) {
                i2++;
            } else {
                i3++;
            }
        }
        this.f4991b.setText(getResources().getQuantityString(R.plurals.install_game_count_title, i2, new Object[]{Integer.valueOf(i2)}));
        this.f4990a.setText(getResources().getQuantityString(R.plurals.uninstall_game_count_title, i3, new Object[]{Integer.valueOf(i3)}));
    }

    /* access modifiers changed from: private */
    public static ArrayList<ApplicationInfo> b(@NonNull Context context) {
        Object obj;
        Cursor cursor;
        Context applicationContext = context.getApplicationContext();
        String str = null;
        try {
            obj = b.b.o.g.e.a(Class.forName("android.app.AppGlobals"), "getPackageManager", (Class<?>[]) null, new Object[0]);
        } catch (Exception e2) {
            e2.printStackTrace();
            obj = null;
        }
        ArrayList<ApplicationInfo> arrayList = new ArrayList<>();
        if (obj == null) {
            return arrayList;
        }
        int i2 = -1;
        try {
            cursor = C0391w.a(applicationContext, 0);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    try {
                        str = cursor.getString(cursor.getColumnIndex("package_name"));
                        i2 = cursor.getInt(cursor.getColumnIndex("package_uid"));
                        ApplicationInfo a2 = a(obj, str, i2);
                        if (!(a2 == null || (a2.flags & 8388608) == 0)) {
                            arrayList.add(a2);
                        }
                    } catch (Exception unused) {
                        try {
                            C0391w.a(applicationContext, str, i2, true, 1);
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
            C0391w.a(applicationContext, str, i2, true, 1);
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

    private void b(String str) {
        ArrayList<C0398d> a2 = a(this.j, str);
        ArrayList<C0398d> a3 = a(this.k, str);
        int size = a2.size();
        int size2 = a3.size();
        this.f4991b.setText(getResources().getQuantityString(R.plurals.found_apps_title, size, new Object[]{Integer.valueOf(size)}));
        this.f4990a.setText(getResources().getQuantityString(R.plurals.found_apps_title, size2, new Object[]{Integer.valueOf(size2)}));
        this.h.a(a2);
        this.i.a(a3);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService("input_method");
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void l() {
        if (Build.VERSION.SDK_INT >= 28 && i.e()) {
            getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new Oa(this));
        }
    }

    private void m() {
        ProgressDialog progressDialog = this.p;
        if (progressDialog != null) {
            progressDialog.dismiss();
            this.p = null;
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.gamebooster.ui.SelectGameLandActivity, miui.app.Activity] */
    private void n() {
        if (this.p == null) {
            this.p = ProgressDialog.show(this, (CharSequence) null, getString(R.string.gb_add_game_loading_tips));
        }
        this.p.show();
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<Pair> loader, Pair pair) {
        m();
        if (pair != null) {
            this.j = (ArrayList) pair.first;
            this.k = (ArrayList) pair.second;
            this.l.clear();
            Iterator<C0398d> it = this.j.iterator();
            while (it.hasNext()) {
                String str = it.next().b().packageName;
                if (!TextUtils.isEmpty(str)) {
                    this.l.add(str);
                }
            }
            int size = this.j.size() + this.k.size();
            this.f.setHint(String.format(getResources().getQuantityString(R.plurals.find_applications, size), new Object[]{Integer.valueOf(size)}));
            a((List<C0398d>) this.j, (List<C0398d>) this.k);
            this.f4993d.setEmptyView(this.e);
            this.h.a(this.j);
            this.i.a(this.k);
        }
    }

    public void a(G g2, CompoundButton compoundButton, boolean z) {
        C0398d dVar = (C0398d) compoundButton.getTag();
        if (dVar != null) {
            dVar.a(z);
            a(this, z, dVar);
            a((List<C0398d>) this.j, (List<C0398d>) this.k);
        }
    }

    public void afterTextChanged(Editable editable) {
        String trim = editable.toString().trim();
        if (this.j.size() != 0 || this.k.size() != 0) {
            if (TextUtils.isEmpty(trim)) {
                this.h.a(this.j);
                this.i.a(this.k);
                a((List<C0398d>) this.j, (List<C0398d>) this.k);
                return;
            }
            b(trim);
        }
    }

    public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
    }

    public void onBackPressed() {
        SelectGameLandActivity.super.onBackPressed();
        overridePendingTransition(17432578, 17432579);
    }

    public void onClick(View view) {
        if (view == this.g) {
            onBackPressed();
        }
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [b.b.c.c.a, android.content.Context, com.miui.gamebooster.ui.SelectGameLandActivity, android.app.LoaderManager$LoaderCallbacks, android.view.View$OnClickListener, android.view.View$OnFocusChangeListener, miui.app.Activity, android.app.Activity, com.miui.gamebooster.a.G$a, android.text.TextWatcher] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setTheme(Build.VERSION.SDK_INT == 26 ? R.style.GameLandscape2 : R.style.GameLandscape);
        super.onCreate(bundle);
        Q.a((Activity) this);
        setContentView(R.layout.gb_activity_select_game_land);
        na.a((Activity) this);
        C0390v.a((Context) this).a(this.q);
        this.g = findViewById(R.id.backBtn);
        this.g.setOnClickListener(this);
        this.f4990a = (TextView) findViewById(R.id.notAddedTv);
        this.f4991b = (TextView) findViewById(R.id.addedTv);
        this.f4992c = (ListView) findViewById(R.id.notAddedListView);
        this.f4993d = (ListView) findViewById(R.id.addedListView);
        this.e = findViewById(R.id.emptyView);
        this.f = (TextView) findViewById(R.id.input);
        this.f.addTextChangedListener(this);
        this.f.setOnFocusChangeListener(this);
        this.h = new G(this);
        this.h.a((G.a) this);
        this.f4993d.setAdapter(this.h);
        this.i = new G(this);
        this.i.a((G.a) this);
        this.f4992c.setAdapter(this.i);
        this.f4990a.setText(getResources().getQuantityString(R.plurals.uninstall_game_count_title, 0, new Object[]{0}));
        this.f4991b.setText(getResources().getQuantityString(R.plurals.install_game_count_title, 0, new Object[]{0}));
        getLoaderManager().initLoader(112, (Bundle) null, this);
        fa.a(this);
        l();
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.miui.gamebooster.ui.SelectGameLandActivity, android.app.Activity] */
    public Loader<Pair> onCreateLoader(int i2, Bundle bundle) {
        n();
        return new a(this, (Na) null);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.gamebooster.ui.SelectGameLandActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onDestroy() {
        SelectGameLandActivity.super.onDestroy();
        for (AsyncTask next : this.o) {
            if (next != null) {
                next.cancel(true);
            }
        }
        C0390v.a((Context) this).a();
    }

    public void onFocusChange(View view, boolean z) {
        if (!z) {
            hideKeyboard(view);
        }
    }

    public void onLoaderReset(Loader<Pair> loader) {
    }

    public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.miui.gamebooster.ui.SelectGameLandActivity, miui.app.Activity, android.app.Activity] */
    public void onWindowFocusChanged(boolean z) {
        SelectGameLandActivity.super.onWindowFocusChanged(z);
        if (z) {
            na.a((Activity) this);
        }
    }
}
