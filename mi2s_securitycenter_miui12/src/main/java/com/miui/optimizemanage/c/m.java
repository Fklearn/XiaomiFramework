package com.miui.optimizemanage.c;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

public class m extends ArrayAdapter<d> {

    /* renamed from: a  reason: collision with root package name */
    private Context f5918a;

    /* renamed from: b  reason: collision with root package name */
    private ArrayList<d> f5919b;

    public m(Context context) {
        this(context, new ArrayList());
    }

    public m(Context context, ArrayList<d> arrayList) {
        super(context, 0, arrayList);
        this.f5918a = context;
        this.f5919b = arrayList;
    }

    public int getCount() {
        return this.f5919b.size();
    }

    public d getItem(int i) {
        return this.f5919b.get(i);
    }

    public int getItemViewType(int i) {
        return this.f5919b.get(i).b();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        e eVar;
        d dVar = this.f5919b.get(i);
        if (view == null) {
            view = LayoutInflater.from(this.f5918a).inflate(dVar.a(), viewGroup, false);
            eVar = dVar.a(view);
            view.setTag(eVar);
        } else {
            eVar = (e) view.getTag();
        }
        eVar.a(view, dVar, i);
        return view;
    }

    public int getViewTypeCount() {
        return d.c();
    }
}
