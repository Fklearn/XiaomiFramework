package com.miui.gamebooster.a;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.miui.gamebooster.model.C;
import com.miui.gamebooster.model.C0399e;
import com.miui.gamebooster.model.s;
import com.miui.gamebooster.model.t;
import java.util.Iterator;
import java.util.List;

public class I extends ArrayAdapter<C0399e> {

    /* renamed from: a  reason: collision with root package name */
    private LayoutInflater f4024a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public boolean f4025b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public a f4026c;

    public interface a {
        void a(int i);

        void a(int i, boolean z);

        void b(int i, boolean z);
    }

    public I(Context context, List<C0399e> list) {
        super(context, 0, list);
        this.f4024a = LayoutInflater.from(context);
    }

    /* access modifiers changed from: private */
    public void a(int i) {
        int i2 = i;
        while (i2 >= 0 && !(getItem(i2) instanceof s)) {
            i2--;
        }
        while (i < getCount() && !(getItem(i) instanceof s)) {
            i++;
        }
        if (i2 < i) {
            boolean z = true;
            for (int i3 = i2; i3 < i; i3++) {
                C0399e eVar = (C0399e) getItem(i3);
                if (eVar instanceof C) {
                    C c2 = (C) eVar;
                    if (c2.f() > 0) {
                        Iterator<t> it = c2.g().iterator();
                        while (true) {
                            if (it.hasNext()) {
                                if (!it.next().i()) {
                                    z = false;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            if (i2 >= 0 && i2 < getCount()) {
                C0399e eVar2 = (C0399e) getItem(i2);
                if (eVar2 instanceof s) {
                    ((s) eVar2).b(z);
                }
            }
            notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public void a(int i, boolean z) {
        if (i < getCount()) {
            while (true) {
                i++;
                if (i < getCount()) {
                    C0399e eVar = (C0399e) getItem(i);
                    if (!(eVar instanceof s)) {
                        if (eVar instanceof C) {
                            C c2 = (C) eVar;
                            if (c2.f() > 0) {
                                for (t a2 : c2.g()) {
                                    a2.a(z);
                                }
                            }
                        }
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
        }
    }

    public void a() {
        int count = getCount();
        if (count > 0) {
            for (int i = count - 1; i >= 0; i--) {
                C0399e eVar = (C0399e) getItem(i);
                if (eVar instanceof C) {
                    C c2 = (C) eVar;
                    if (c2.f() > 0) {
                        for (t a2 : c2.g()) {
                            a2.a(false);
                        }
                    }
                }
                if (eVar instanceof s) {
                    ((s) eVar).b(false);
                }
            }
        }
    }

    public void a(a aVar) {
        this.f4026c = aVar;
    }

    public void a(boolean z) {
        this.f4025b = z;
    }

    public int getItemViewType(int i) {
        return ((C0399e) getItem(i)).b();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        C0328f fVar;
        C0399e eVar = (C0399e) getItem(i);
        if (view == null) {
            view = this.f4024a.inflate(eVar.a(), viewGroup, false);
            fVar = eVar.a(view);
            view.setTag(fVar);
        } else {
            fVar = (C0328f) view.getTag();
        }
        eVar.a(this.f4025b);
        fVar.a(view, i, eVar, new H(this));
        return view;
    }

    public int getViewTypeCount() {
        return C0399e.c();
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
