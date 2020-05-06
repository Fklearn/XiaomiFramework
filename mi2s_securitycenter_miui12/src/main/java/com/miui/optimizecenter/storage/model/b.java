package com.miui.optimizecenter.storage.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.view.View;
import b.b.c.j.r;
import b.c.a.b.d;
import b.c.a.b.d.d;
import com.miui.optimizecenter.storage.h;
import com.miui.securitycenter.R;
import java.util.Comparator;
import miui.text.ExtraTextUtils;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private d f5759a;

    /* renamed from: b  reason: collision with root package name */
    public int f5760b;

    /* renamed from: c  reason: collision with root package name */
    public String f5761c;

    /* renamed from: d  reason: collision with root package name */
    public String f5762d;
    public String e;
    public String f;
    public int g;
    public ApplicationInfo h;
    public boolean i;
    private int j = 0;
    public long k;
    public long l;
    public long m;
    public long n;
    public long o;
    public long p;
    public long q;
    public final IPackageStatsObserver.Stub r = new a(this);

    public static class a implements Comparator<b> {
        /* renamed from: a */
        public int compare(b bVar, b bVar2) {
            return bVar.k >= bVar2.k ? -1 : 1;
        }
    }

    public b() {
        d.a aVar = new d.a();
        aVar.a(true);
        aVar.b(false);
        aVar.c(true);
        aVar.b((int) R.drawable.icon_def);
        aVar.c((int) R.drawable.icon_def);
        this.f5759a = aVar.a();
    }

    public int a() {
        return this.j;
    }

    public void a(int i2) {
        this.j = i2;
    }

    public void a(View view) {
        if (view.getTag() != null) {
            h.a aVar = (h.a) view.getTag();
            aVar.f5741b.setTag(this.f);
            r.a(d.a.PKG_ICON.c(this.f5762d), aVar.f5741b, this.f5759a);
            aVar.f5742c.setText(this.f5761c);
            Context context = view.getContext();
            aVar.f5743d.setText(context.getString(R.string.storage_app_list_desc, new Object[]{ExtraTextUtils.formatFileSize(context, this.k)}));
        }
    }

    public void b(View view) {
        Context context = view.getContext();
        Intent intent = new Intent("miui.intent.action.STORAGE_APP_INFO_DETAILS");
        intent.putExtra("model", this.f5762d);
        intent.putExtra("uId", this.f5760b);
        context.startActivity(intent);
    }
}
