package com.miui.applicationlock;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import com.miui.applicationlock.c.o;

class Z implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f3237a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3238b;

    Z(ConfirmAccessControl confirmAccessControl, int i) {
        this.f3238b = confirmAccessControl;
        this.f3237a = i;
    }

    /* JADX WARNING: type inference failed for: r4v3, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl] */
    public void onClick(DialogInterface dialogInterface, int i) {
        o.a("one_key_lock_dialog", this.f3237a);
        o.a(dialogInterface, "one_key_lock_notify_dialog", this.f3237a);
        Intent intent = new Intent(this.f3238b, ConfirmAccessControl.class);
        intent.putExtra("extra_data", "HappyCodingMain");
        intent.putExtra("checkAccess_to_uncheck", true);
        if (Build.VERSION.SDK_INT >= 24 && this.f3238b.R) {
            intent.setFlags(268435456);
        }
        this.f3238b.startActivityForResult(intent, 29000);
        boolean unused = this.f3238b.W = true;
    }
}
