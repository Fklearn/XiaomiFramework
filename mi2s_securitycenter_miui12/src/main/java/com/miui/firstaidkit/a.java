package com.miui.firstaidkit;

import com.miui.securityscan.cards.c;
import com.miui.securityscan.cards.k;
import java.lang.ref.WeakReference;

public class a implements k.a {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<FirstAidKitActivity> f3897a;

    public a(FirstAidKitActivity firstAidKitActivity) {
        this.f3897a = new WeakReference<>(firstAidKitActivity);
    }

    public void a(String str) {
        FirstAidKitActivity firstAidKitActivity = (FirstAidKitActivity) this.f3897a.get();
        if (firstAidKitActivity != null) {
            c.a(firstAidKitActivity.f, str);
        }
    }
}
