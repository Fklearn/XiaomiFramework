package com.miui.antivirus.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import b.b.b.d.n;
import b.b.c.b.b;
import com.miui.antivirus.model.g;
import com.miui.antivirus.service.GuardService;
import com.miui.antivirus.ui.p;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import miui.app.Activity;

public class MonitoredAppSettingsActivity extends b.b.c.c.a implements LoaderManager.LoaderCallbacks<ArrayList<g>>, CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    private ArrayList<g> f2681a;

    /* renamed from: b  reason: collision with root package name */
    private ListView f2682b;

    /* renamed from: c  reason: collision with root package name */
    private View f2683c;

    /* renamed from: d  reason: collision with root package name */
    private p f2684d;
    private Context e;
    /* access modifiers changed from: private */
    public int f;
    /* access modifiers changed from: private */
    public int g;

    private static class a extends b.b.c.i.a<ArrayList<g>> {

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<MonitoredAppSettingsActivity> f2685b;

        /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.antivirus.activity.MonitoredAppSettingsActivity, java.lang.Object] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        a(com.miui.antivirus.activity.MonitoredAppSettingsActivity r2) {
            /*
                r1 = this;
                r1.<init>(r2)
                java.lang.ref.WeakReference r0 = new java.lang.ref.WeakReference
                r0.<init>(r2)
                r1.f2685b = r0
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.antivirus.activity.MonitoredAppSettingsActivity.a.<init>(com.miui.antivirus.activity.MonitoredAppSettingsActivity):void");
        }

        public ArrayList<g> loadInBackground() {
            Activity activity = (MonitoredAppSettingsActivity) this.f2685b.get();
            if (activity == null) {
                return (ArrayList) super.loadInBackground();
            }
            ArrayList<String> b2 = b.b.b.p.b(activity.getApplicationContext());
            ArrayList<String> h = n.h(activity.getApplicationContext());
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            Iterator it = ((ArrayList) b.a((Context) activity).a()).iterator();
            while (it.hasNext()) {
                PackageInfo packageInfo = (PackageInfo) it.next();
                if (h.contains(packageInfo.packageName)) {
                    if (b2.contains(packageInfo.packageName)) {
                        arrayList.add(new g.b(packageInfo.packageName, true));
                    } else {
                        arrayList2.add(new g.b(packageInfo.packageName, false));
                    }
                }
            }
            int unused = activity.f = arrayList.size();
            int unused2 = activity.g = arrayList2.size();
            ArrayList<g> arrayList3 = new ArrayList<>();
            if (arrayList.size() > 0) {
                g gVar = new g();
                gVar.a(g.a.ENABLED);
                gVar.a(activity.getResources().getQuantityString(R.plurals.sp_monitored_apps_list_group_enable, arrayList.size(), new Object[]{Integer.valueOf(arrayList.size())}));
                gVar.a((ArrayList<g.b>) arrayList);
                arrayList3.add(gVar);
            }
            if (arrayList2.size() > 0) {
                g gVar2 = new g();
                gVar2.a(g.a.DISABLED);
                gVar2.a(activity.getResources().getQuantityString(R.plurals.sp_monitored_apps_list_group_disable, arrayList2.size(), new Object[]{Integer.valueOf(arrayList2.size())}));
                gVar2.a((ArrayList<g.b>) arrayList2);
                arrayList3.add(gVar2);
            }
            return arrayList3;
        }
    }

    private void a(String str, boolean z) {
        int i;
        Object[] objArr;
        int i2;
        Resources resources;
        Iterator<g> it = this.f2681a.iterator();
        while (it.hasNext()) {
            g next = it.next();
            next.a(str, z);
            if (next.b() == g.a.ENABLED) {
                this.f = z ? this.f + 1 : this.f - 1;
                resources = getResources();
                i2 = R.plurals.sp_monitored_apps_list_group_enable;
                i = this.f;
                objArr = new Object[]{Integer.valueOf(i)};
            } else {
                this.g = z ? this.g - 1 : this.g + 1;
                resources = getResources();
                i2 = R.plurals.sp_monitored_apps_list_group_disable;
                i = this.g;
                objArr = new Object[]{Integer.valueOf(i)};
            }
            next.a(resources.getQuantityString(i2, i, objArr));
        }
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<ArrayList<g>> loader, ArrayList<g> arrayList) {
        this.f2681a = arrayList;
        this.f2682b.setEmptyView(this.f2683c);
        this.f2684d.updateData(this.f2681a);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, miui.app.Activity, com.miui.antivirus.activity.MonitoredAppSettingsActivity] */
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        String str = (String) compoundButton.getTag();
        HashMap<String, List<String>> j = n.j(this.e);
        if (j.keySet().contains(str)) {
            ArrayList arrayList = new ArrayList(b.b.b.p.a(this.e));
            if (z) {
                arrayList.addAll(j.get(str));
            } else {
                arrayList.removeAll(j.get(str));
            }
            b.b.b.p.a((ArrayList<String>) arrayList);
        }
        ArrayList arrayList2 = new ArrayList(b.b.b.p.b(this.e));
        if (z) {
            arrayList2.add(str);
        } else {
            arrayList2.remove(str);
        }
        b.b.b.p.b((ArrayList<String>) arrayList2);
        if (b.b.b.p.j()) {
            Intent intent = new Intent(this, GuardService.class);
            intent.setAction("action_register_foreground_notification");
            startService(intent);
        }
        a(str, z);
        this.f2684d.notifyDataSetChanged();
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [b.b.c.c.a, android.content.Context, android.app.LoaderManager$LoaderCallbacks, miui.app.Activity, com.miui.antivirus.activity.MonitoredAppSettingsActivity, android.widget.CompoundButton$OnCheckedChangeListener] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.e = this;
        setContentView(R.layout.sp_activity_monitored_apps_management);
        this.f2682b = (ListView) findViewById(R.id.list_view);
        this.f2683c = findViewById(R.id.empty_view);
        this.f2684d = new p(this);
        this.f2684d.setOnCheckedChangeListener(this);
        this.f2682b.setAdapter(this.f2684d);
        getLoaderManager().initLoader(112, (Bundle) null, this);
    }

    public Loader<ArrayList<g>> onCreateLoader(int i, Bundle bundle) {
        return new a(this);
    }

    public void onLoaderReset(Loader<ArrayList<g>> loader) {
    }
}
