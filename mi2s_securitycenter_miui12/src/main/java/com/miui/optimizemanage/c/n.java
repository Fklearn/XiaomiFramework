package com.miui.optimizemanage.c;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.l;
import b.b.c.j.r;
import com.miui.optimizemanage.memoryclean.LockAppManageActivity;
import com.miui.securitycenter.R;
import java.util.List;

public class n extends d {

    /* renamed from: c  reason: collision with root package name */
    public String f5920c;

    /* renamed from: d  reason: collision with root package name */
    public String f5921d;
    public String e;
    public String f;
    public String g;
    public String h;
    public String i;

    public static class a extends e {

        /* renamed from: a  reason: collision with root package name */
        TextView f5922a;

        /* renamed from: b  reason: collision with root package name */
        TextView f5923b;

        /* renamed from: c  reason: collision with root package name */
        Button f5924c;

        /* renamed from: d  reason: collision with root package name */
        ImageView f5925d;
        ImageView e;
        ImageView f;
        ImageView g;

        public a(View view) {
            super(view);
            this.f5922a = (TextView) view.findViewById(R.id.title);
            this.f5923b = (TextView) view.findViewById(R.id.summary);
            this.f5924c = (Button) view.findViewById(R.id.button);
            this.f5925d = (ImageView) view.findViewById(R.id.icon_item1);
            this.e = (ImageView) view.findViewById(R.id.icon_item2);
            this.f = (ImageView) view.findViewById(R.id.icon_item3);
            this.g = (ImageView) view.findViewById(R.id.icon_item4);
            int color = view.getResources().getColor(R.color.result_banner_icon_bg);
            this.f5925d.setColorFilter(color);
            this.e.setColorFilter(color);
            this.f.setColorFilter(color);
            this.g.setColorFilter(color);
            l.a(view);
        }

        public void a(View view, d dVar, int i) {
            super.a(view, dVar, i);
            n nVar = (n) dVar;
            this.f5922a.setText(nVar.j());
            this.f5923b.setText(nVar.i());
            this.f5924c.setText(nVar.d());
            String e2 = nVar.e();
            if (e2 != null) {
                this.f5925d.setImageBitmap(r.a(e2, r.h));
                this.f5925d.setVisibility(0);
            } else {
                this.f5925d.setVisibility(8);
            }
            String f2 = nVar.f();
            if (f2 != null) {
                this.e.setImageBitmap(r.a(f2, r.h));
                this.e.setVisibility(0);
            } else {
                this.e.setVisibility(8);
            }
            String g2 = nVar.g();
            if (g2 != null) {
                this.f.setImageBitmap(r.a(g2, r.h));
                this.f.setVisibility(0);
            } else {
                this.f.setVisibility(8);
            }
            String h = nVar.h();
            if (h != null) {
                this.g.setImageBitmap(r.a(h, r.h));
                this.g.setVisibility(0);
            } else {
                this.g.setVisibility(8);
            }
            view.setOnClickListener(nVar);
            this.f5924c.setOnClickListener(nVar);
        }
    }

    public n() {
        a((int) R.layout.om_result_list_item_lock_view);
    }

    private void a(Context context) {
        context.startActivity(new Intent(context, LockAppManageActivity.class));
    }

    public e a(View view) {
        return new a(view);
    }

    public void a(Context context, List<com.miui.optimizemanage.memoryclean.a> list) {
        int size = list.size();
        this.f5920c = context.getResources().getQuantityString(R.plurals.om_locked_apps_title, size, new Object[]{Integer.valueOf(size)});
        com.miui.optimizemanage.memoryclean.a aVar = list.get(0);
        this.f = UserHandle.getUserId(aVar.f5951b) == 999 ? "pkg_icon_xspace://".concat(aVar.f5950a) : "pkg_icon://".concat(aVar.f5950a);
        this.g = null;
        this.h = null;
        this.i = null;
        if (size > 1) {
            com.miui.optimizemanage.memoryclean.a aVar2 = list.get(1);
            this.g = UserHandle.getUserId(aVar2.f5951b) == 999 ? "pkg_icon_xspace://".concat(aVar2.f5950a) : "pkg_icon://".concat(aVar2.f5950a);
            if (size > 2) {
                com.miui.optimizemanage.memoryclean.a aVar3 = list.get(2);
                this.h = UserHandle.getUserId(aVar3.f5951b) == 999 ? "pkg_icon_xspace://".concat(aVar3.f5950a) : "pkg_icon://".concat(aVar3.f5950a);
                if (size > 3) {
                    com.miui.optimizemanage.memoryclean.a aVar4 = list.get(3);
                    this.i = UserHandle.getUserId(aVar4.f5951b) == 999 ? "pkg_icon_xspace://".concat(aVar4.f5950a) : "pkg_icon://".concat(aVar4.f5950a);
                }
            }
        }
    }

    public void a(String str) {
        this.e = str;
    }

    public void b(String str) {
        this.f5921d = str;
    }

    public String d() {
        return this.e;
    }

    public String e() {
        return this.f;
    }

    public String f() {
        return this.g;
    }

    public String g() {
        return this.h;
    }

    public String h() {
        return this.i;
    }

    public String i() {
        return this.f5921d;
    }

    public String j() {
        return this.f5920c;
    }

    public void onClick(View view) {
        super.onClick(view);
        a(view.getContext());
    }
}
