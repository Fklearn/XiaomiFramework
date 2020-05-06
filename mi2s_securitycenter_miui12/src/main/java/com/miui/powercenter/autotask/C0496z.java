package com.miui.powercenter.autotask;

import android.os.Bundle;
import b.b.c.c.b.l;

/* renamed from: com.miui.powercenter.autotask.z  reason: case insensitive filesystem */
public class C0496z extends l {

    /* renamed from: a  reason: collision with root package name */
    protected AutoTask f6775a;

    /* renamed from: b  reason: collision with root package name */
    protected AutoTask f6776b;

    public C0496z() {
        a((AutoTask) null);
    }

    /* access modifiers changed from: protected */
    public void a(AutoTask autoTask) {
        if (autoTask == null) {
            autoTask = new AutoTask();
        }
        this.f6775a = autoTask;
    }

    public boolean b() {
        return !this.f6776b.getName().equals(this.f6775a.getName()) || this.f6776b.getEnabled() != this.f6775a.getEnabled() || !this.f6776b.conditionsEquals(this.f6775a) || !this.f6776b.operationsEquals(this.f6775a) || this.f6776b.getRepeatType() != this.f6775a.getRepeatType() || this.f6776b.getRestoreLevel() != this.f6775a.getRestoreLevel();
    }

    public void onCreate(Bundle bundle) {
        AutoTask autoTask;
        super.onCreate(bundle);
        if (!(bundle == null || (autoTask = (AutoTask) bundle.getParcelable("task")) == null)) {
            this.f6776b = autoTask;
        }
        if (this.f6776b == null) {
            this.f6776b = new AutoTask(this.f6775a);
        }
    }

    public void onCreatePreferences(Bundle bundle, String str) {
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("task", this.f6776b);
    }
}
