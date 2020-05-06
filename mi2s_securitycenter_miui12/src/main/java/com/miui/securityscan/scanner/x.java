package com.miui.securityscan.scanner;

import com.miui.securityscan.L;
import com.miui.securityscan.scanner.O;
import java.lang.ref.WeakReference;

public class x implements O.e {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<L> f7935a;

    public x(L l) {
        this.f7935a = new WeakReference<>(l);
    }

    public void a() {
        L l = (L) this.f7935a.get();
        if (l != null) {
            l.c();
        }
    }
}
