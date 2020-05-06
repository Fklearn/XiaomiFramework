package com.miui.applicationlock.widget;

import android.text.Editable;
import b.b.o.f.a.e;
import com.miui.applicationlock.widget.LockPatternView;
import java.util.List;

class z implements LockPatternView.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ A f3464a;

    z(A a2) {
        this.f3464a = a2;
    }

    public void a() {
        this.f3464a.f3392c.removeCallbacks(this.f3464a.f3393d);
    }

    public void a(List<LockPatternView.a> list) {
        if (this.f3464a.e) {
            this.f3464a.f3391b.a(e.a().a(list));
        } else {
            this.f3464a.a(e.a().a(list));
        }
    }

    public void b() {
        this.f3464a.f3392c.removeCallbacks(this.f3464a.f3393d);
        if (this.f3464a.e) {
            this.f3464a.f3391b.a((Editable) null);
        }
    }

    public void b(List<LockPatternView.a> list) {
    }
}
