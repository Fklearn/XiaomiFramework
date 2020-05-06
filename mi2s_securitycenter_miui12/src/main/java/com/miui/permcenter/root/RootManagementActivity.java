package com.miui.permcenter.root;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.CompoundButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.c.a;
import com.miui.common.stickydecoration.b.c;
import com.miui.common.stickydecoration.f;
import com.miui.permcenter.autostart.i;
import com.miui.permcenter.autostart.j;
import com.miui.permcenter.autostart.l;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.recyclerview.widget.RecyclerView;

public class RootManagementActivity extends a implements LoaderManager.LoaderCallbacks<ArrayList<i>>, CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    private l f6492a;

    /* renamed from: b  reason: collision with root package name */
    private RecyclerView f6493b;

    /* renamed from: c  reason: collision with root package name */
    private View f6494c;

    /* renamed from: d  reason: collision with root package name */
    private ArrayList<i> f6495d;
    private RecyclerView.f e;

    private void a(ArrayList<i> arrayList) {
        this.f6493b.b(this.e);
        SparseArray sparseArray = new SparseArray();
        int i = 0;
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            for (int i3 = 0; i3 < arrayList.get(i2).c().size(); i3++) {
                sparseArray.put(i3 + i, arrayList.get(i2).a());
            }
            i += arrayList.get(i2).c().size();
        }
        this.e = f.a.a((c) new d(this, sparseArray)).a();
        this.f6493b.a(this.e);
    }

    private void l() {
        ArrayList<i> arrayList = this.f6495d;
        if (arrayList == null || arrayList.size() == 0) {
            this.f6494c.setVisibility(0);
            return;
        }
        this.f6494c.setVisibility(8);
        this.f6492a.a((List<i>) this.f6495d);
        a(this.f6495d);
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<ArrayList<i>> loader, ArrayList<i> arrayList) {
        this.f6495d = arrayList;
        l();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        Loader loader;
        RootManagementActivity.super.onActivityResult(i, i2, intent);
        if (i == 50 && (loader = getLoaderManager().getLoader(113)) != null) {
            loader.forceLoad();
        }
    }

    /* JADX WARNING: type inference failed for: r10v0, types: [android.content.Context, com.miui.permcenter.root.RootManagementActivity, miui.app.Activity] */
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        com.miui.permcenter.a aVar = (com.miui.permcenter.a) compoundButton.getTag();
        String e2 = aVar.e();
        if (z) {
            Intent intent = new Intent(this, RootApplyActivity.class);
            intent.putExtra("extra_pkgname", e2);
            startActivityForResult(intent, 50);
            overridePendingTransition(0, 0);
            return;
        }
        aVar.f().put(512L, Integer.valueOf(z ? 3 : 1));
        Iterator<i> it = this.f6495d.iterator();
        int i = 0;
        int i2 = 0;
        while (it.hasNext()) {
            Iterator<com.miui.permcenter.a> it2 = it.next().c().iterator();
            while (it2.hasNext()) {
                if (it2.next().f().get(512L).intValue() == 3) {
                    i++;
                } else {
                    i2++;
                }
            }
        }
        Iterator<i> it3 = this.f6495d.iterator();
        while (it3.hasNext()) {
            i next = it3.next();
            next.a(next.b() == j.ENABLED ? getResources().getQuantityString(R.plurals.hints_get_root_enable_title, i, new Object[]{Integer.valueOf(i)}) : getResources().getQuantityString(R.plurals.hints_get_root_disable_title, i2, new Object[]{Integer.valueOf(i2)}));
        }
        this.f6492a.notifyDataSetChanged();
        new c(this, e2).execute(new Void[0]);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [b.b.c.c.a, android.content.Context, android.app.LoaderManager$LoaderCallbacks, com.miui.permcenter.root.RootManagementActivity, miui.app.Activity, android.widget.CompoundButton$OnCheckedChangeListener] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.pm_activity_root_management);
        this.f6493b = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.j(1);
        this.f6493b.setLayoutManager(linearLayoutManager);
        this.f6494c = findViewById(R.id.empty_view);
        this.f6492a = new l(this, 512);
        this.f6492a.a((CompoundButton.OnCheckedChangeListener) this);
        this.f6493b.setAdapter(this.f6492a);
        getLoaderManager().initLoader(113, (Bundle) null, this);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.permcenter.root.RootManagementActivity] */
    public Loader<ArrayList<i>> onCreateLoader(int i, Bundle bundle) {
        return new b(this, this);
    }

    public void onLoaderReset(Loader<ArrayList<i>> loader) {
    }
}
