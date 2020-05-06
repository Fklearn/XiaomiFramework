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

public class F extends BaseAdapter {

    /* renamed from: a  reason: collision with root package name */
    private List<j> f4011a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private boolean f4012b = false;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public ImageView f4013a;

        /* renamed from: b  reason: collision with root package name */
        public TextView f4014b;

        /* renamed from: c  reason: collision with root package name */
        public ImageView f4015c;
    }

    public F(List<j> list, boolean z) {
        if (list != null) {
            this.f4011a = list;
        }
        this.f4012b = z;
    }

    public int getCount() {
        return this.f4011a.size();
    }

    public j getItem(int i) {
        return this.f4011a.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater;
        int i2;
        j item = getItem(i);
        if (view == null) {
            if (this.f4012b) {
                layoutInflater = LayoutInflater.from(viewGroup.getContext());
                i2 = R.layout.gamebox_function_item_h;
            } else {
                layoutInflater = LayoutInflater.from(viewGroup.getContext());
                i2 = R.layout.gamebox_function_item_v;
            }
            view = layoutInflater.inflate(i2, viewGroup, false);
            a aVar = new a();
            aVar.f4013a = (ImageView) view.findViewById(R.id.item_image);
            aVar.f4014b = (TextView) view.findViewById(R.id.label);
            aVar.f4015c = (ImageView) view.findViewById(R.id.stick_point);
            view.setTag(aVar);
        }
        item.a(i, view, viewGroup.getContext());
        return view;
    }
}
