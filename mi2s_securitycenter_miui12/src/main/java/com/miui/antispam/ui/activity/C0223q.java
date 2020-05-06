package com.miui.antispam.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import com.miui.antispam.ui.activity.BackSoundActivity;
import miui.telephony.SubscriptionManager;

/* renamed from: com.miui.antispam.ui.activity.q  reason: case insensitive filesystem */
class C0223q implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BackSoundActivity.a f2609a;

    C0223q(BackSoundActivity.a aVar) {
        this.f2609a = aVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        Uri b2 = i == 0 ? BackSoundActivity.a.f2515a : BackSoundActivity.a.f2516b;
        Intent intent = new Intent("android.intent.action.CALL");
        if (this.f2609a.h != -1) {
            SubscriptionManager.putSlotIdExtra(intent, this.f2609a.h);
            intent.putExtra("com.android.phone.extra.slot", this.f2609a.h);
        }
        intent.setData(b2);
        this.f2609a.startActivity(intent);
        dialogInterface.dismiss();
    }
}
