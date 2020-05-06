package com.miui.securityscan.scanner;

import android.app.Activity;
import com.miui.securityscan.L;
import com.miui.securityscan.h.a.a;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.O;
import java.lang.ref.WeakReference;

/* renamed from: com.miui.securityscan.scanner.l  reason: case insensitive filesystem */
public class C0565l implements O.a {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<L> f7907a;

    public C0565l(L l) {
        this.f7907a = new WeakReference<>(l);
    }

    public void a(int i, boolean z) {
        L l = (L) this.f7907a.get();
        if (!z && i > 0 && l != null) {
            a.a(i);
        }
    }

    public void a(AbsModel absModel) {
        if (((L) this.f7907a.get()) != null && absModel != null) {
            absModel.ignore();
        }
    }

    public void b(AbsModel absModel) {
        Activity activity;
        L l = (L) this.f7907a.get();
        if (l != null && absModel != null && (activity = l.getActivity()) != null) {
            absModel.optimize(activity);
        }
    }
}
