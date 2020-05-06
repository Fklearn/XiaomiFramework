package com.miui.permcenter;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class b implements Comparator<a> {

    /* renamed from: a  reason: collision with root package name */
    private Collator f6094a = Collator.getInstance(Locale.getDefault());

    /* renamed from: a */
    public int compare(a aVar, a aVar2) {
        return this.f6094a.compare(aVar.d(), aVar2.d());
    }
}
