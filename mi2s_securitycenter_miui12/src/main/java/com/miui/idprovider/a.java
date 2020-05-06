package com.miui.idprovider;

import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

class a extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IdProvider f5608a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    a(IdProvider idProvider, Handler handler) {
        super(handler);
        this.f5608a = idProvider;
    }

    public void onChange(boolean z) {
        IdProvider idProvider = this.f5608a;
        boolean z2 = true;
        if (Settings.Secure.getInt(idProvider.getContext().getContentResolver(), "allow_oaid_used", 1) != 1) {
            z2 = false;
        }
        boolean unused = idProvider.f5606d = z2;
        Log.i("IdProvider", "user change OAID switch!" + this.f5608a.f5606d);
    }
}
