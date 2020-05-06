package com.miui.powercenter.autotask;

import android.view.View;
import android.widget.CheckBox;
import com.miui.powercenter.autotask.AutoTaskManageActivity;

/* renamed from: com.miui.powercenter.autotask.x  reason: case insensitive filesystem */
class C0494x implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f6772a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AutoTaskManageActivity.b f6773b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ AutoTaskManageActivity.a f6774c;

    C0494x(AutoTaskManageActivity.a aVar, int i, AutoTaskManageActivity.b bVar) {
        this.f6774c = aVar;
        this.f6772a = i;
        this.f6773b = bVar;
    }

    public void onClick(View view) {
        AutoTaskManageActivity.a aVar = this.f6774c;
        if (!aVar.e) {
            AutoTaskManageActivity.this.a(this.f6772a);
            return;
        }
        CheckBox checkBox = this.f6773b.e;
        checkBox.setChecked(!checkBox.isChecked());
        this.f6774c.a(this.f6772a, this.f6773b.e.isChecked(), false);
    }
}
