package com.miui.powercenter.quickoptimize;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.miui.powercenter.f.a;
import com.miui.powercenter.utils.b;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.miui.powercenter.quickoptimize.k  reason: case insensitive filesystem */
public class C0532k extends BaseAdapter {

    /* renamed from: a  reason: collision with root package name */
    private List<a> f7229a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private Context f7230b;

    public C0532k(Context context) {
        this.f7230b = context;
    }

    public void a(List<a> list) {
        this.f7229a.clear();
        this.f7229a.addAll(list);
    }

    public int getCount() {
        return this.f7229a.size();
    }

    public Object getItem(int i) {
        return this.f7229a.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = new ImageView(this.f7230b);
            int dimension = (int) this.f7230b.getResources().getDimension(R.dimen.view_dimen_110);
            view.setLayoutParams(new AbsListView.LayoutParams(dimension, dimension));
        }
        a aVar = this.f7229a.get(i);
        int i2 = aVar.f7065d;
        if (i2 > 0) {
            b.a((ImageView) view, i2);
        } else if (!TextUtils.isEmpty(aVar.f7062a)) {
            b.a((ImageView) view, aVar.f7062a);
        } else {
            ((ImageView) view).setImageDrawable(this.f7230b.getPackageManager().getDefaultActivityIcon());
        }
        view.setContentDescription(b.a(this.f7230b, aVar.f7062a));
        return view;
    }
}
