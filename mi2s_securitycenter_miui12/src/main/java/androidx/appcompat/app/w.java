package androidx.appcompat.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatDelegateImpl;

class w extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppCompatDelegateImpl.e f326a;

    w(AppCompatDelegateImpl.e eVar) {
        this.f326a = eVar;
    }

    public void onReceive(Context context, Intent intent) {
        this.f326a.d();
    }
}
