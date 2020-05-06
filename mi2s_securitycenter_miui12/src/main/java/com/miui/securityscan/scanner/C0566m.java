package com.miui.securityscan.scanner;

import android.app.Activity;
import com.miui.securityscan.L;
import com.miui.securityscan.model.GroupModel;
import com.miui.securityscan.scanner.O;
import java.lang.ref.WeakReference;

/* renamed from: com.miui.securityscan.scanner.m  reason: case insensitive filesystem */
public class C0566m implements O.c {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<L> f7908a;

    public C0566m(L l) {
        this.f7908a = new WeakReference<>(l);
    }

    public void a(GroupModel groupModel) {
        Activity activity;
        L l = (L) this.f7908a.get();
        if (groupModel != null && l != null && (activity = l.getActivity()) != null) {
            groupModel.optimize(activity);
        }
    }
}
