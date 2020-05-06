package com.miui.gamebooster.videobox.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import com.miui.gamebooster.n.d.b;
import com.miui.gamebooster.n.d.j;
import com.miui.gamebooster.videobox.utils.e;
import com.miui.gamebooster.videobox.view.VBIndicatorView;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class h extends BaseAdapter {

    /* renamed from: a  reason: collision with root package name */
    private List<j> f5167a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private b.a f5168b;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public View f5169a;

        /* renamed from: b  reason: collision with root package name */
        public View f5170b;

        /* renamed from: c  reason: collision with root package name */
        public ViewPager f5171c;

        /* renamed from: d  reason: collision with root package name */
        public VBIndicatorView f5172d;
        public FrameLayout e;
    }

    public h(Context context, b.a aVar) {
        this.f5167a.addAll(com.miui.gamebooster.n.b.b.a(context));
        this.f5168b = aVar;
    }

    public boolean a() {
        j jVar;
        Iterator<j> it = this.f5167a.iterator();
        while (true) {
            if (!it.hasNext()) {
                jVar = null;
                break;
            }
            jVar = it.next();
            if (jVar != null && jVar.f() == com.miui.gamebooster.n.c.b.QUICK_FUNC) {
                break;
            }
        }
        return jVar != null && !e.b() && jVar.c() <= 3;
    }

    public int getCount() {
        return this.f5167a.size();
    }

    public j getItem(int i) {
        return this.f5167a.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        j item = getItem(i);
        int d2 = item.d();
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(d2, viewGroup, false);
            a aVar = new a();
            aVar.f5169a = view.findViewById(R.id.tv_setting);
            aVar.f5170b = view.findViewById(R.id.divider);
            aVar.f5171c = view.findViewById(R.id.vb_item_viewpager);
            aVar.f5172d = (VBIndicatorView) view.findViewById(R.id.vb_indicator);
            aVar.e = (FrameLayout) view.findViewById(R.id.vp_container);
            view.setTag(aVar);
        }
        item.a(i, view, this.f5168b);
        return view;
    }
}
