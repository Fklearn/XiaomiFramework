package com.miui.antivirus.service;

import com.miui.antivirus.ui.u;
import java.util.ArrayList;

class a implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f2886a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ArrayList f2887b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ DialogService f2888c;

    a(DialogService dialogService, int i, ArrayList arrayList) {
        this.f2888c = dialogService;
        this.f2886a = i;
        this.f2887b = arrayList;
    }

    public void run() {
        u.a(this.f2888c).a(this.f2886a, this.f2887b);
    }
}
