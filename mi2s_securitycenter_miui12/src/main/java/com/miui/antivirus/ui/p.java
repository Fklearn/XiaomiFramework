package com.miui.antivirus.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.r;
import b.b.c.j.x;
import com.miui.antivirus.model.g;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.widget.SlidingButton;

public class p extends com.miui.common.expandableview.a {

    /* renamed from: a  reason: collision with root package name */
    private LayoutInflater f2978a;

    /* renamed from: b  reason: collision with root package name */
    private List<g> f2979b = new ArrayList();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public CompoundButton.OnCheckedChangeListener f2980c;

    /* renamed from: d  reason: collision with root package name */
    private Context f2981d;

    private static class a {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public TextView f2982a;

        private a() {
        }

        /* synthetic */ a(o oVar) {
            this();
        }
    }

    public static class b {

        /* renamed from: a  reason: collision with root package name */
        ImageView f2983a;

        /* renamed from: b  reason: collision with root package name */
        TextView f2984b;

        /* renamed from: c  reason: collision with root package name */
        SlidingButton f2985c;
    }

    public p(Context context) {
        this.f2978a = LayoutInflater.from(context);
        this.f2981d = context;
    }

    public int getCountForSection(int i) {
        return this.f2979b.get(i).c().size();
    }

    public Object getItem(int i, int i2) {
        return Integer.valueOf(i2);
    }

    public long getItemId(int i, int i2) {
        return (long) i2;
    }

    public View getItemView(int i, int i2, View view, ViewGroup viewGroup) {
        b bVar;
        String str = this.f2979b.get(i).c().get(i2).f2775a;
        boolean z = this.f2979b.get(i).c().get(i2).f2776b;
        if (view == null) {
            view = this.f2978a.inflate(R.layout.sp_monitored_apps_list_item_view, (ViewGroup) null);
            bVar = new b();
            bVar.f2983a = (ImageView) view.findViewById(R.id.icon);
            bVar.f2984b = (TextView) view.findViewById(R.id.title);
            bVar.f2985c = view.findViewById(R.id.sliding_button);
            bVar.f2985c.setOnPerformCheckedChangeListener(this.f2980c);
            view.setTag(bVar);
        } else {
            bVar = (b) view.getTag();
        }
        view.setOnClickListener(new o(this, bVar, z));
        r.a("pkg_icon://".concat(str), bVar.f2983a, r.f);
        bVar.f2984b.setText(x.j(this.f2981d, str));
        bVar.f2985c.setTag(str);
        bVar.f2985c.setChecked(z);
        return view;
    }

    public int getSectionCount() {
        return this.f2979b.size();
    }

    public View getSectionHeaderView(int i, View view, ViewGroup viewGroup) {
        a aVar;
        if (view == null) {
            view = this.f2978a.inflate(R.layout.sp_monitored_apps_list_header_view, (ViewGroup) null);
            aVar = new a((o) null);
            TextView unused = aVar.f2982a = (TextView) view.findViewById(R.id.header_title);
            view.setTag(aVar);
        } else {
            aVar = (a) view.getTag();
        }
        aVar.f2982a.setText(this.f2979b.get(i).a());
        return view;
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.f2980c = onCheckedChangeListener;
    }

    public void updateData(List<g> list) {
        this.f2979b.clear();
        this.f2979b.addAll(list);
        notifyDataSetChanged();
    }
}
