package com.miui.antispam.ui.activity;

import b.b.a.e.o;
import java.util.Comparator;

/* renamed from: com.miui.antispam.ui.activity.t  reason: case insensitive filesystem */
class C0225t implements Comparator<o.a> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ z f2613a;

    C0225t(z zVar) {
        this.f2613a = zVar;
    }

    /* renamed from: a */
    public int compare(o.a aVar, o.a aVar2) {
        if (aVar.f1459a.startsWith("***")) {
            if (!aVar2.f1459a.startsWith("***")) {
                return 1;
            }
        } else if (aVar.f1459a.endsWith("*")) {
            if (aVar2.f1459a.startsWith("***")) {
                return -1;
            }
            if (!aVar2.f1459a.endsWith("*")) {
                return 1;
            }
        } else if (aVar2.f1459a.startsWith("***") || aVar2.f1459a.endsWith("*")) {
            return -1;
        }
        return aVar.f1459a.compareTo(aVar2.f1459a);
    }
}
