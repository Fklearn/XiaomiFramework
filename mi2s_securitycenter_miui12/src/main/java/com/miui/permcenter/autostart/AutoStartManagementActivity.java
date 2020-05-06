package com.miui.permcenter.autostart;

import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.x;
import com.miui.common.stickydecoration.f;
import com.miui.permcenter.autostart.l;
import com.miui.permcenter.n;
import com.miui.permcenter.o;
import com.miui.permcenter.s;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import miui.app.Activity;
import miui.app.AlertDialog;
import miui.os.Build;
import miuix.recyclerview.widget.RecyclerView;

public class AutoStartManagementActivity extends b.b.c.c.a implements LoaderManager.LoaderCallbacks<h>, CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    private View f6047a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public int f6048b = -1;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public View f6049c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public List<String> f6050d;
    /* access modifiers changed from: private */
    public Handler e = new Handler();
    private h f;
    private boolean g;
    private MenuItem h;
    private MenuItem i;
    private boolean j;
    private RecyclerView k;
    private l l;
    private RecyclerView.f m;
    private b n;
    private c o;
    private l.a p = new f(this);

    class a implements Comparator<com.miui.permcenter.a> {

        /* renamed from: a  reason: collision with root package name */
        private Collator f6051a = Collator.getInstance(Locale.getDefault());

        a() {
        }

        /* renamed from: a */
        public int compare(com.miui.permcenter.a aVar, com.miui.permcenter.a aVar2) {
            return aVar.c() == aVar2.c() ? this.f6051a.compare(aVar.d(), aVar2.d()) : aVar.c() ? -1 : 1;
        }
    }

    private static class b extends b.b.c.i.a<h> {

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<AutoStartManagementActivity> f6053b;

        public b(AutoStartManagementActivity autoStartManagementActivity) {
            super(autoStartManagementActivity.getApplicationContext());
            this.f6053b = new WeakReference<>(autoStartManagementActivity);
        }

        public h loadInBackground() {
            AutoStartManagementActivity autoStartManagementActivity;
            if (isLoadInBackgroundCanceled() || (autoStartManagementActivity = (AutoStartManagementActivity) this.f6053b.get()) == null || autoStartManagementActivity.isFinishing() || autoStartManagementActivity.isDestroyed()) {
                return null;
            }
            List unused = autoStartManagementActivity.f6050d = s.a(autoStartManagementActivity.getApplicationContext());
            List<String> b2 = x.b(autoStartManagementActivity.getApplicationContext());
            Collections.sort(b2);
            ArrayList<com.miui.permcenter.a> a2 = n.a(autoStartManagementActivity.getApplicationContext(), (long) PermissionManager.PERM_ID_AUTOSTART, true);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();
            HashSet hashSet = new HashSet();
            if (Build.IS_CM_CUSTOMIZATION) {
                hashSet.add("com.greenpoint.android.mc10086.activity");
            }
            hashSet.add("com.miui.guardprovider");
            hashSet.add("com.xiaomi.account");
            hashSet.add("com.miui.virtualsim");
            Iterator<com.miui.permcenter.a> it = a2.iterator();
            while (it.hasNext()) {
                com.miui.permcenter.a next = it.next();
                if (!hashSet.contains(next.e())) {
                    if (next.f().get(Long.valueOf(PermissionManager.PERM_ID_AUTOSTART)).intValue() == 3 || (autoStartManagementActivity.f6050d != null && Collections.binarySearch(autoStartManagementActivity.f6050d, next.e()) >= 0)) {
                        next.b(true);
                        if (!next.h()) {
                            arrayList.add(next);
                        }
                        arrayList2.add(next);
                    } else {
                        if (!next.h()) {
                            arrayList3.add(next);
                        }
                        arrayList4.add(next);
                        if (Collections.binarySearch(b2, next.e()) >= 0) {
                            next.c(true);
                        }
                    }
                }
            }
            autoStartManagementActivity.getClass();
            a aVar = new a();
            Collections.sort(arrayList, aVar);
            Collections.sort(arrayList2, aVar);
            Collections.sort(arrayList3, aVar);
            Collections.sort(arrayList4, aVar);
            ArrayList<i> a3 = autoStartManagementActivity.a((ArrayList<com.miui.permcenter.a>) arrayList, (ArrayList<com.miui.permcenter.a>) arrayList3);
            ArrayList<i> a4 = autoStartManagementActivity.a((ArrayList<com.miui.permcenter.a>) arrayList2, (ArrayList<com.miui.permcenter.a>) arrayList4);
            h hVar = new h();
            hVar.f6075a = a3;
            hVar.f6076b = a4;
            return hVar;
        }
    }

    private static class c extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<AutoStartManagementActivity> f6054a;

        /* renamed from: b  reason: collision with root package name */
        private int f6055b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public String f6056c;

        /* renamed from: d  reason: collision with root package name */
        private boolean f6057d;
        /* access modifiers changed from: private */
        public boolean e;

        c(AutoStartManagementActivity autoStartManagementActivity, int i, String str, boolean z, boolean z2) {
            this.f6054a = new WeakReference<>(autoStartManagementActivity);
            this.f6055b = i;
            this.f6056c = str;
            this.f6057d = z;
            this.e = z2;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            Activity activity;
            if (!isCancelled() && (activity = (AutoStartManagementActivity) this.f6054a.get()) != null && !activity.isFinishing() && !activity.isDestroyed()) {
                PermissionManager.getInstance(activity).setApplicationPermission(PermissionManager.PERM_ID_AUTOSTART, this.f6055b, this.f6056c);
                s.a(activity.getApplicationContext(), this.f6056c, this.f6057d);
                List unused = activity.f6050d = s.a(activity.getApplicationContext());
                activity.e.postDelayed(new g(this, activity), 400);
            }
            return null;
        }
    }

    /* access modifiers changed from: private */
    public ArrayList<i> a(ArrayList<com.miui.permcenter.a> arrayList, ArrayList<com.miui.permcenter.a> arrayList2) {
        ArrayList<i> arrayList3 = new ArrayList<>();
        if (!arrayList.isEmpty()) {
            i iVar = new i();
            iVar.a(j.ENABLED);
            iVar.a(getResources().getQuantityString(R.plurals.hints_auto_start_enable_title, arrayList.size(), new Object[]{Integer.valueOf(arrayList.size())}));
            iVar.a(arrayList);
            arrayList3.add(iVar);
        }
        if (!arrayList2.isEmpty()) {
            i iVar2 = new i();
            iVar2.a(j.DISABLED);
            iVar2.a(getResources().getQuantityString(R.plurals.hints_auto_start_disable_title, arrayList2.size(), new Object[]{Integer.valueOf(arrayList2.size())}));
            iVar2.a(arrayList2);
            arrayList3.add(iVar2);
        }
        return arrayList3;
    }

    private void a(ArrayList<i> arrayList) {
        this.k.b(this.m);
        SparseArray sparseArray = new SparseArray();
        int i2 = 0;
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            for (int i4 = 0; i4 < arrayList.get(i3).c().size(); i4++) {
                sparseArray.put(i4 + i2, arrayList.get(i3).a());
            }
            i2 += arrayList.get(i3).c().size();
        }
        this.m = f.a.a((com.miui.common.stickydecoration.b.c) new e(this, sparseArray)).a();
        this.k.a(this.m);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.permcenter.autostart.AutoStartManagementActivity] */
    private void a(boolean z) {
        if (z || !o.a()) {
            o.a(true);
            new AlertDialog.Builder(this).setTitle(R.string.dialog_title_auto_start_declare).setMessage(R.string.dialog_msg_auto_start_declare).setPositiveButton(R.string.ok, (DialogInterface.OnClickListener) null).show();
        }
    }

    private void l() {
        if (this.f != null) {
            this.f6047a.setVisibility(8);
            this.l.a((List<i>) this.g ? this.f.f6076b : this.f.f6075a);
            a(this.g ? this.f.f6076b : this.f.f6075a);
            return;
        }
        this.f6047a.setVisibility(0);
        this.k.setVisibility(8);
    }

    private void m() {
        if (this.g) {
            this.i.setVisible(true);
            this.h.setVisible(false);
            return;
        }
        this.i.setVisible(false);
        this.h.setVisible(true);
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<h> loader, h hVar) {
        this.f = hVar;
        l();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i2, int i3, Intent intent) {
        int intExtra;
        int i4;
        if (i2 != 1 || i3 != -1) {
            return;
        }
        if (intent == null || (intExtra = intent.getIntExtra("pkg_position", -1)) == -1 || (i4 = this.f6048b) == -1 || intExtra != i4) {
            this.f6048b = -1;
            this.f6049c = null;
            return;
        }
        this.f6048b = -1;
        View view = this.f6049c;
        if (view != null) {
            l.b bVar = (l.b) view.getTag();
            if (bVar == null) {
                this.f6049c = null;
                return;
            }
            this.f6049c = null;
            int intExtra2 = intent.getIntExtra("auto_start_detail_result_permission_action", -1);
            boolean z = false;
            boolean booleanExtra = intent.getBooleanExtra("auto_start_detail_result_wakepath_accepted", false);
            if (intExtra2 == 3 || booleanExtra) {
                z = true;
            }
            bVar.f6093d.setChecked(z);
            com.miui.permcenter.a aVar = (com.miui.permcenter.a) bVar.f6093d.getTag();
            if (aVar != null) {
                HashMap<Long, Integer> f2 = aVar.f();
                f2.put(Long.valueOf(PermissionManager.PERM_ID_AUTOSTART), Integer.valueOf(intExtra2));
                aVar.a(f2);
                aVar.b(booleanExtra);
                this.f6050d = s.a(getApplicationContext());
            }
        }
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (this.j) {
            com.miui.permcenter.a aVar = (com.miui.permcenter.a) compoundButton.getTag();
            String e2 = aVar.e();
            HashMap<Long, Integer> f2 = aVar.f();
            int i2 = z ? 3 : 1;
            f2.put(Long.valueOf(PermissionManager.PERM_ID_AUTOSTART), Integer.valueOf(i2));
            aVar.b(z);
            this.o = new c(this, i2, e2, z, z);
            this.o.execute(new Void[0]);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [b.b.c.c.a, android.content.Context, com.miui.permcenter.autostart.AutoStartManagementActivity, android.app.LoaderManager$LoaderCallbacks, miui.app.Activity, android.widget.CompoundButton$OnCheckedChangeListener] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.pm_activity_auto_start_management);
        this.f6047a = findViewById(R.id.empty_view);
        this.k = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.auto_start_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.j(1);
        this.k.setLayoutManager(linearLayoutManager);
        this.l = new l(this, PermissionManager.PERM_ID_AUTOSTART);
        this.l.a((CompoundButton.OnCheckedChangeListener) this);
        this.l.a(this.p);
        this.k.setAdapter(this.l);
        getLoaderManager().initLoader(112, (Bundle) null, this);
        a(false);
        if (bundle != null) {
            this.g = bundle.getBoolean("ShowSystemApp", false);
        }
    }

    public Loader<h> onCreateLoader(int i2, Bundle bundle) {
        this.n = new b(this);
        return this.n;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.perm_autostart_option, menu);
        this.h = menu.findItem(R.id.show_system);
        this.i = menu.findItem(R.id.hide_system);
        m();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        b bVar = this.n;
        if (bVar != null) {
            bVar.cancelLoad();
        }
        c cVar = this.o;
        if (cVar != null) {
            cVar.cancel(true);
        }
        AutoStartManagementActivity.super.onDestroy();
    }

    public void onLoaderReset(Loader<h> loader) {
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.about) {
            sendBroadcast(new Intent("AUTO_START_MNG_INFO_CLICKED"));
            a(true);
            return true;
        } else if (itemId != R.id.hide_system && itemId != R.id.show_system) {
            return AutoStartManagementActivity.super.onOptionsItemSelected(menuItem);
        } else {
            if (this.f != null) {
                menuItem.setVisible(false);
                this.g = !this.g;
                m();
                l();
            }
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        this.j = false;
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        this.j = true;
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        AutoStartManagementActivity.super.onSaveInstanceState(bundle);
        bundle.putBoolean("ShowSystemApp", this.g);
    }
}
