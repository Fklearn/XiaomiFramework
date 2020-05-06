package com.miui.gamebooster.a;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.miui.gamebooster.d.d;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.model.g;
import com.miui.securitycenter.R;
import java.util.List;

public class y extends BaseAdapter {

    /* renamed from: a  reason: collision with root package name */
    private Context f4079a;

    /* renamed from: b  reason: collision with root package name */
    private List<g> f4080b;

    /* renamed from: c  reason: collision with root package name */
    private LayoutInflater f4081c;

    static class a {

        /* renamed from: a  reason: collision with root package name */
        RelativeLayout f4082a;

        /* renamed from: b  reason: collision with root package name */
        ImageView f4083b;

        a() {
        }
    }

    public y(Context context, List<g> list) {
        this.f4079a = context;
        this.f4080b = list;
        this.f4081c = LayoutInflater.from(context);
    }

    public int getCount() {
        List<g> list = this.f4080b;
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public g getItem(int i) {
        List<g> list = this.f4080b;
        if (list != null) {
            return list.get(i);
        }
        return null;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        a aVar;
        if (view == null) {
            view = this.f4081c.inflate(R.layout.gamebox_item, (ViewGroup) null);
            aVar = new a();
            aVar.f4083b = (ImageView) view.findViewById(R.id.item_image);
            aVar.f4082a = (RelativeLayout) view.findViewById(R.id.item_bg);
            view.setTag(aVar);
        } else {
            aVar = (a) view.getTag();
        }
        com.miui.gamebooster.c.a.a(this.f4079a);
        aVar.f4083b.setImageResource(this.f4080b.get(i).b());
        boolean z = true;
        if (this.f4080b.get(i).c() != d.ANTIMSG || ((!C0388t.m() || !com.miui.gamebooster.c.a.b(false)) && !com.miui.gamebooster.c.a.c(false))) {
            z = false;
        }
        if (z) {
            aVar.f4083b.setBackground(this.f4079a.getResources().getDrawable(R.drawable.gamebox_antimsg_openbg));
        }
        return view;
    }
}
