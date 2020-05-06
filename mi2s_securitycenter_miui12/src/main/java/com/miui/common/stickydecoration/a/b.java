package com.miui.common.stickydecoration.a;

import android.util.LruCache;

class b extends LruCache<Integer, T> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c f3837a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    b(c cVar, int i) {
        super(i);
        this.f3837a = cVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void entryRemoved(boolean z, Integer num, T t, T t2) {
        super.entryRemoved(z, num, t, t2);
    }
}
