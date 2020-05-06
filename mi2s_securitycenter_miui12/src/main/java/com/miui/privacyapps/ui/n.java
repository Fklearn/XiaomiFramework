package com.miui.privacyapps.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b.b.k.b.a;
import b.b.k.c;
import b.b.k.d;
import b.b.o.g.e;
import com.miui.appmanager.AppManageUtils;
import com.miui.common.stickydecoration.f;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.firewall.UserConfigure;
import com.miui.permcenter.autostart.j;
import com.miui.privacyapps.ui.p;
import com.miui.privacyapps.view.b;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import miui.security.SecurityManager;
import miui.view.SearchActionMode;
import miuix.recyclerview.widget.RecyclerView;

public class n extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<d>>, View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    public static final Comparator<c> f7410a = new h();

    /* renamed from: b  reason: collision with root package name */
    public static final Comparator<c> f7411b = new i();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public RecyclerView f7412c;

    /* renamed from: d  reason: collision with root package name */
    private TextView f7413d;
    private TextView e;
    /* access modifiers changed from: private */
    public View f;
    /* access modifiers changed from: private */
    public p g;
    private com.miui.privacyapps.view.c h;
    private b i;
    private ActivityManager j;
    private PackageManager k;
    /* access modifiers changed from: private */
    public a l;
    private SecurityManager m;
    private SearchActionMode n;
    private boolean o;
    private int p;
    /* access modifiers changed from: private */
    public Activity q;
    /* access modifiers changed from: private */
    public ArrayList<d> r = new ArrayList<>();
    private RecyclerView.f s;
    private RecyclerView.f t;
    /* access modifiers changed from: private */
    public TextWatcher u = new f(this);
    private SearchActionMode.Callback v = new g(this);
    private final BroadcastReceiver w = new j(this);

    private ResolveInfo a(PackageManager packageManager, ActivityManager.RecentTaskInfo recentTaskInfo) {
        Intent intent = new Intent(recentTaskInfo.baseIntent);
        ComponentName componentName = recentTaskInfo.origActivity;
        if (componentName != null) {
            intent.setComponent(componentName);
        }
        intent.setFlags((intent.getFlags() & -2097153) | 268435456);
        return packageManager.resolveActivity(intent, 0);
    }

    /* access modifiers changed from: private */
    public void a(c cVar) {
        this.h = new com.miui.privacyapps.view.c(getActivity());
        Window window = this.h.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawableResource(R.drawable.pa_dialog_background);
        window.setGravity(80);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.height = -2;
        window.setAttributes(attributes);
        this.h.setCanceledOnTouchOutside(false);
        this.h.show();
        this.h.setOnDismissListener(new e(this, cVar));
        this.l.b(false);
    }

    /* access modifiers changed from: private */
    public void a(String str) {
        ArrayList arrayList = new ArrayList();
        Iterator<d> it = this.r.iterator();
        while (it.hasNext()) {
            Iterator<c> it2 = it.next().c().iterator();
            while (it2.hasNext()) {
                c next = it2.next();
                if (next.b().toLowerCase().indexOf(str.toLowerCase()) >= 0) {
                    arrayList.add(next);
                }
            }
        }
        Collections.sort(arrayList, f7411b);
        d dVar = new d();
        dVar.a((ArrayList<c>) arrayList);
        dVar.a(getResources().getQuantityString(R.plurals.find_applications, arrayList.size(), new Object[]{Integer.valueOf(arrayList.size())}));
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(dVar);
        this.g.a((ArrayList<d>) arrayList2);
        a((ArrayList<d>) arrayList2);
    }

    private void a(String str, int i2, boolean z) {
        String str2;
        this.m.setPrivacyApp(str, i2, z);
        int a2 = this.l.a();
        Iterator<d> it = this.r.iterator();
        while (it.hasNext()) {
            d next = it.next();
            j b2 = next.b();
            if (b2 != null) {
                if (b2 == j.ENABLED) {
                    int i3 = this.p - a2;
                    str2 = getResources().getQuantityString(R.plurals.privacy_apps_enable_header_title, i3, new Object[]{Integer.valueOf(i3)});
                } else {
                    str2 = getResources().getQuantityString(R.plurals.privacy_apps_disable_header_title, a2, new Object[]{Integer.valueOf(a2)});
                }
                next.a(str2);
            }
        }
        this.g.notifyDataSetChanged();
        this.q.getContentResolver().notifyChange(b.b.k.a.f1828a, (ContentObserver) null);
        if (z) {
            b.b.k.a.a.a(str);
        }
    }

    private void a(ArrayList<d> arrayList) {
        this.f7412c.b(this.s);
        this.f7412c.b(this.t);
        HashMap hashMap = new HashMap();
        int i2 = 0;
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            for (int i4 = 0; i4 < arrayList.get(i3).c().size(); i4++) {
                d dVar = new d();
                dVar.a(arrayList.get(i3).a());
                dVar.a(arrayList.get(i3).b());
                hashMap.put(Integer.valueOf(i4 + i2), dVar);
            }
            i2 += arrayList.get(i3).c().size();
        }
        this.t = f.a.a((com.miui.common.stickydecoration.b.c) new d(this, hashMap)).a();
        this.f7412c.a(this.t);
    }

    private boolean a(String str, int i2) {
        Iterator<ActivityManager.RunningAppProcessInfo> it = this.j.getRunningAppProcesses().iterator();
        while (true) {
            int i3 = 0;
            if (!it.hasNext()) {
                return false;
            }
            ActivityManager.RunningAppProcessInfo next = it.next();
            if (next.uid == i2 && next.pkgList != null) {
                while (true) {
                    String[] strArr = next.pkgList;
                    if (i3 >= strArr.length) {
                        continue;
                        break;
                    } else if (strArr[i3].equals(str)) {
                        return true;
                    } else {
                        i3++;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void b(c cVar) {
        boolean z = true;
        boolean z2 = !cVar.a();
        cVar.a(z2);
        String c2 = cVar.c();
        int e2 = cVar.e();
        if (this.l.e()) {
            int d2 = cVar.d();
            if (z2) {
                z = false;
            }
            AppManageUtils.a(c2, d2, z);
        }
        a(c2, e2, z2);
        if (z2 && a(c2, cVar.d())) {
            b(c2, e2);
        }
        this.g.notifyDataSetChanged();
    }

    private void b(String str, int i2) {
        ActivityInfo activityInfo;
        List<ActivityManager.RecentTaskInfo> recentTasks = this.j.getRecentTasks(1001, 6);
        if (recentTasks != null) {
            int i3 = 1;
            while (true) {
                if (i3 >= recentTasks.size()) {
                    break;
                }
                ActivityManager.RecentTaskInfo recentTaskInfo = recentTasks.get(i3);
                ResolveInfo a2 = a(this.k, recentTaskInfo);
                if (!(a2 == null || (activityInfo = a2.activityInfo) == null || activityInfo.packageName == null)) {
                    String str2 = a2.activityInfo.packageName;
                    int i4 = 0;
                    try {
                        i4 = ((Integer) e.b(recentTaskInfo, UserConfigure.Columns.USER_ID)).intValue();
                    } catch (IllegalAccessException | NoSuchFieldException e2) {
                        Log.e("PrivacyAppsManageFragment", UserConfigure.Columns.USER_ID, e2);
                    }
                    if (str2.equals(str) && i4 == i2) {
                        try {
                            AppManageUtils.b(recentTaskInfo.persistentId);
                            break;
                        } catch (Exception unused) {
                        }
                    }
                }
                i3++;
            }
        }
        AppManageUtils.a(this.j, str, i2);
    }

    /* access modifiers changed from: private */
    public void d() {
        this.f7412c.b(this.s);
        this.f7412c.b(this.t);
        HashMap hashMap = new HashMap();
        int i2 = 0;
        for (int i3 = 0; i3 < this.r.size(); i3++) {
            for (int i4 = 0; i4 < this.r.get(i3).c().size(); i4++) {
                d dVar = new d();
                dVar.a(this.r.get(i3).a());
                dVar.a(this.r.get(i3).b());
                hashMap.put(Integer.valueOf(i4 + i2), dVar);
            }
            i2 += this.r.get(i3).c().size();
        }
        this.s = f.a.a((com.miui.common.stickydecoration.b.c) new m(this, hashMap)).a();
        this.f7412c.a(this.s);
    }

    /* access modifiers changed from: private */
    public ArrayList<d> e() {
        List<PackageInfo> a2;
        ArrayList<d> arrayList = new ArrayList<>();
        b.b.c.b.b a3 = b.b.c.b.b.a((Context) getActivity());
        ArrayList arrayList2 = new ArrayList(a3.a());
        if (UserHandle.myUserId() == 0 && AppManageUtils.g(getActivity()) && (a2 = AppManageUtils.a(this.k, 64, 999)) != null && a2.size() > 0 && !arrayList2.containsAll(a2)) {
            arrayList2.addAll(a2);
        }
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        Iterator it = arrayList2.iterator();
        while (true) {
            boolean z = false;
            if (!it.hasNext()) {
                break;
            }
            PackageInfo packageInfo = (PackageInfo) it.next();
            if ((packageInfo.applicationInfo.flags & 1) != 0) {
                z = true;
            }
            if (!z) {
                String str = null;
                try {
                    str = a3.a(packageInfo.packageName).a();
                } catch (Exception e2) {
                    Log.e("PrivacyAppsManageFragment", "getAppLabel error", e2);
                }
                if (str != null) {
                    int userId = UserHandle.getUserId(packageInfo.applicationInfo.uid);
                    c cVar = new c();
                    cVar.b(packageInfo.packageName);
                    cVar.b(userId);
                    cVar.a(packageInfo.applicationInfo.uid);
                    cVar.a(str);
                    boolean isPrivacyApp = this.m.isPrivacyApp(packageInfo.packageName, userId);
                    cVar.a(isPrivacyApp);
                    if (isPrivacyApp) {
                        arrayList4.add(cVar);
                    } else if (this.l.a(this.k, packageInfo.packageName, userId) != null) {
                        arrayList3.add(cVar);
                    }
                }
            }
        }
        if (!arrayList4.isEmpty()) {
            d dVar = new d();
            dVar.a(getResources().getQuantityString(R.plurals.privacy_apps_disable_header_title, arrayList4.size(), new Object[]{Integer.valueOf(arrayList4.size())}));
            dVar.a(j.DISABLED);
            dVar.a((ArrayList<c>) arrayList4);
            arrayList.add(dVar);
        }
        if (!arrayList3.isEmpty()) {
            d dVar2 = new d();
            dVar2.a(getResources().getQuantityString(R.plurals.privacy_apps_enable_header_title, arrayList3.size(), new Object[]{Integer.valueOf(arrayList3.size())}));
            dVar2.a(j.ENABLED);
            dVar2.a((ArrayList<c>) arrayList3);
            arrayList.add(dVar2);
        }
        return arrayList;
    }

    public void a() {
        if (this.n != null) {
            this.n = null;
        }
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<ArrayList<d>> loader, ArrayList<d> arrayList) {
        this.r = arrayList;
        this.g.a(arrayList);
        d();
        this.p = 0;
        Iterator<d> it = this.r.iterator();
        while (it.hasNext()) {
            ArrayList<c> c2 = it.next().c();
            int size = c2.size();
            if (size > 0) {
                this.p += size;
                if (size > 1) {
                    Collections.sort(c2, f7410a);
                }
            }
        }
        if (this.p == 0) {
            this.f7413d.setVisibility(0);
        }
        TextView textView = this.e;
        Resources resources = getResources();
        int i2 = this.p;
        textView.setHint(resources.getQuantityString(R.plurals.find_applications, i2, new Object[]{Integer.valueOf(i2)}));
    }

    public void a(SearchActionMode.Callback callback) {
        this.n = getActivity().startActionMode(callback);
    }

    public boolean b() {
        return this.n != null;
    }

    public void c() {
        a aVar = this.l;
        if (aVar != null) {
            int a2 = aVar.a();
            boolean b2 = this.l.b();
            if (a2 <= 0 && b2) {
                this.i = new b(getActivity());
                Window window = this.i.getWindow();
                window.getDecorView().setPadding(0, 0, 0, 0);
                window.setBackgroundDrawableResource(R.drawable.pa_dialog_background);
                window.setGravity(80);
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.width = -1;
                attributes.height = -2;
                window.setAttributes(attributes);
                this.i.setCanceledOnTouchOutside(false);
                this.i.show();
                this.l.a(false);
            } else if (b2) {
                this.l.a(false);
            }
        }
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        Activity activity = this.q;
        this.j = (ActivityManager) activity.getSystemService("activity");
        this.k = activity.getPackageManager();
        this.l = new a(activity);
        this.m = (SecurityManager) activity.getSystemService("security");
        Loader loader = getLoaderManager().getLoader(320);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(320, (Bundle) null, this);
        if (!(Build.VERSION.SDK_INT < 24 || bundle == null || loader == null)) {
            loaderManager.restartLoader(320, (Bundle) null, this);
        }
        this.o = activity.getIntent().getBooleanExtra("enter_from_privacyapps_page", false);
        if (this.o) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constants.System.ACTION_SCREEN_OFF);
            activity.registerReceiver(this.w, intentFilter);
        }
        activity.setResult(-1);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.q = activity;
    }

    public void onClick(View view) {
        if (view == this.f) {
            a(this.v);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public Loader<ArrayList<d>> onCreateLoader(int i2, Bundle bundle) {
        return new l(this, getActivity());
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.privacy_apps_manage, (ViewGroup) null);
        this.f7412c = (miuix.recyclerview.widget.RecyclerView) inflate.findViewById(R.id.list_view);
        this.g = new p(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.q);
        linearLayoutManager.j(1);
        this.f7412c.setLayoutManager(linearLayoutManager);
        this.f7412c.setAdapter(this.g);
        this.g.a((p.a) new k(this));
        this.f = inflate.findViewById(R.id.search_view);
        this.e = (TextView) this.f.findViewById(16908297);
        this.f.setOnClickListener(this);
        this.f7413d = (TextView) inflate.findViewById(R.id.empty_view);
        return inflate;
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.o) {
            this.q.unregisterReceiver(this.w);
        }
    }

    public void onLoaderReset(Loader<ArrayList<d>> loader) {
    }

    public void onPause() {
        super.onPause();
        com.miui.privacyapps.view.c cVar = this.h;
        if (cVar != null) {
            cVar.dismiss();
        }
        b bVar = this.i;
        if (bVar != null) {
            bVar.dismiss();
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }
}
