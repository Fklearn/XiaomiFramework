package com.miui.firstaidkit;

import com.miui.securityscan.cards.g;
import java.lang.ref.WeakReference;

public class c implements g.a {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<FirstAidKitActivity> f3929a;

    public c(FirstAidKitActivity firstAidKitActivity) {
        this.f3929a = new WeakReference<>(firstAidKitActivity);
    }

    public void a(String str, int i, int i2) {
        FirstAidKitActivity firstAidKitActivity = (FirstAidKitActivity) this.f3929a.get();
        if (firstAidKitActivity != null) {
            firstAidKitActivity.a(firstAidKitActivity.f, str, i, i2);
        }
    }
}
