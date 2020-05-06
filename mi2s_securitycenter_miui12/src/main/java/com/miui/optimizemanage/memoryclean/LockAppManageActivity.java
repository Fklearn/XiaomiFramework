package com.miui.optimizemanage.memoryclean;

import android.os.Bundle;
import android.os.UserHandle;
import android.widget.ProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.common.stickydecoration.b.b;
import com.miui.common.stickydecoration.b.c;
import com.miui.common.stickydecoration.f;
import com.miui.optimizemanage.memoryclean.e;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import miuix.recyclerview.widget.RecyclerView;

public class LockAppManageActivity extends b.b.c.c.a {

    /* renamed from: a  reason: collision with root package name */
    private static List<a> f5946a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private RecyclerView f5947b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public e f5948c;

    /* renamed from: d  reason: collision with root package name */
    private ProgressBar f5949d;
    private RecyclerView.f e;
    private List<e.a> f = new ArrayList();

    public interface a {
        void a();
    }

    /* access modifiers changed from: private */
    public void a(int i, c cVar) {
        boolean z = !cVar.f5954c;
        b.b.c.j.e.a(cVar.f5953b, UserHandle.getUserId(cVar.f5952a), z);
        cVar.f5954c = z;
        this.f5948c.notifyItemChanged(i);
        m();
    }

    public static void a(a aVar) {
        if (!f5946a.contains(aVar)) {
            f5946a.add(aVar);
        }
    }

    /* access modifiers changed from: private */
    public void a(boolean z) {
        ProgressBar progressBar;
        int i;
        if (z) {
            progressBar = this.f5949d;
            i = 0;
        } else {
            progressBar = this.f5949d;
            i = 8;
        }
        progressBar.setVisibility(i);
    }

    public static void b(a aVar) {
        if (f5946a.contains(aVar)) {
            f5946a.remove(aVar);
        }
    }

    /* access modifiers changed from: private */
    public void l() {
        this.f5947b.b(this.e);
        HashMap hashMap = new HashMap();
        int i = 0;
        for (int i2 = 0; i2 < this.f.size(); i2++) {
            for (int i3 = 0; i3 < this.f.get(i2).f5963b.size(); i3++) {
                e.a aVar = new e.a();
                aVar.f5962a = this.f.get(i2).f5962a;
                hashMap.put(Integer.valueOf(i3 + i), aVar);
            }
            i += this.f.get(i2).f5963b.size();
        }
        f.a a2 = f.a.a((c) new h(this, hashMap));
        a2.a((b) null);
        this.e = a2.a();
        this.f5947b.a(this.e);
    }

    private void m() {
        for (a a2 : f5946a) {
            a2.a();
        }
    }

    private void n() {
        new g(this).executeOnExecutor(com.miui.optimizemanage.d.e.b(), new Void[0]);
    }

    public void a(List<c> list) {
        this.f.clear();
        e.a aVar = new e.a();
        aVar.f5962a = 1;
        aVar.f5963b = new ArrayList();
        e.a aVar2 = new e.a();
        aVar2.f5962a = 2;
        aVar2.f5963b = new ArrayList();
        for (c next : list) {
            (next.f5954c ? aVar.f5963b : aVar2.f5963b).add(next);
        }
        if (!aVar.f5963b.isEmpty()) {
            this.f.add(aVar);
        }
        if (!aVar2.f5963b.isEmpty()) {
            this.f.add(aVar2);
        }
        this.f5948c.a(this.f);
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [b.b.c.c.a, android.content.Context, miui.app.Activity, com.miui.optimizemanage.memoryclean.LockAppManageActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.om_activity_lock_app_manage);
        this.f5947b = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.list);
        this.f5947b.setLayoutManager(new LinearLayoutManager(this));
        this.f5948c = new e(this);
        this.f5947b.setAdapter(this.f5948c);
        this.f5948c.a((e.b) new f(this));
        this.f5949d = (ProgressBar) findViewById(R.id.progressBar);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        n();
    }
}
