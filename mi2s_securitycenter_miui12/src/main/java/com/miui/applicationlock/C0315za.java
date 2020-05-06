package com.miui.applicationlock;

import com.miui.applicationlock.c.C0257a;
import java.util.Comparator;

/* renamed from: com.miui.applicationlock.za  reason: case insensitive filesystem */
class C0315za implements Comparator<C0257a> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MaskNotificationActivity f3478a;

    C0315za(MaskNotificationActivity maskNotificationActivity) {
        this.f3478a = maskNotificationActivity;
    }

    /* renamed from: a */
    public int compare(C0257a aVar, C0257a aVar2) {
        if (aVar.c() && !aVar2.c()) {
            return -1;
        }
        if (aVar.c() || !aVar2.c()) {
            return aVar.a().compareTo(aVar2.a());
        }
        return 1;
    }
}
