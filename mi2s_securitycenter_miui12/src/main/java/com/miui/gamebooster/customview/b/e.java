package com.miui.gamebooster.customview.b;

import android.support.v4.util.SparseArrayCompat;

public class e<T> {

    /* renamed from: a  reason: collision with root package name */
    SparseArrayCompat<d<T>> f4181a = new SparseArrayCompat<>();

    public int a() {
        return this.f4181a.size();
    }

    public int a(T t, int i) {
        for (int size = this.f4181a.size() - 1; size >= 0; size--) {
            if (((d) this.f4181a.valueAt(size)).a(t, i)) {
                return this.f4181a.keyAt(size);
            }
        }
        throw new IllegalArgumentException("No ItemViewType added that matches position=" + i + " in data source");
    }

    public d a(int i) {
        return (d) this.f4181a.get(i);
    }

    public e<T> a(int i, d<T> dVar) {
        if (this.f4181a.get(i) == null) {
            this.f4181a.put(i, dVar);
            return this;
        }
        throw new IllegalArgumentException("An ItemViewType is already registered for the viewType = " + i + ". Already registered ItemViewType is " + this.f4181a.get(i));
    }

    public e<T> a(d<T> dVar) {
        int size = this.f4181a.size();
        if (dVar != null) {
            this.f4181a.put(size, dVar);
        }
        return this;
    }

    public void a(g gVar, T t, int i) {
        int size = this.f4181a.size();
        for (int i2 = 0; i2 < size; i2++) {
            d dVar = (d) this.f4181a.valueAt(i2);
            if (dVar.a(t, i)) {
                dVar.a(gVar, t, i);
                return;
            }
        }
        throw new IllegalArgumentException("No ItemViewTypeManager added that matches position=" + i + " in data source");
    }
}
