package com.miui.gamebooster.a;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.miui.gamebooster.model.j;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class E extends BaseAdapter {

    /* renamed from: a  reason: collision with root package name */
    private boolean f4007a = false;

    /* renamed from: b  reason: collision with root package name */
    private List<j> f4008b = new ArrayList();

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public ImageView f4009a;

        /* renamed from: b  reason: collision with root package name */
        public TextView f4010b;
    }

    public E(List<j> list, boolean z) {
        if (list != null) {
            this.f4008b = list;
        }
        this.f4007a = z;
    }

    public int getCount() {
        return this.f4008b.size();
    }

    public j getItem(int i) {
        return this.f4008b.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater;
        int i2;
        j item = getItem(i);
        if (view == null) {
            if (this.f4007a) {
                layoutInflater = LayoutInflater.from(viewGroup.getContext());
                i2 = R.layout.gb_h_recommend_app_item;
            } else {
                layoutInflater = LayoutInflater.from(viewGroup.getContext());
                i2 = R.layout.gb_v_recommend_app_item;
            }
            view = layoutInflater.inflate(i2, viewGroup, false);
            a aVar = new a();
            aVar.f4009a = (ImageView) view.findViewById(R.id.item_image);
            aVar.f4010b = (TextView) view.findViewById(R.id.label);
            view.setTag(aVar);
        }
        item.a(i, view, viewGroup.getContext());
        return view;
    }
}
