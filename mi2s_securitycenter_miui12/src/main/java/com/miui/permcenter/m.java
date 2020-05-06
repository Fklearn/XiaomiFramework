package com.miui.permcenter;

import android.content.Context;
import android.os.AsyncTask;
import com.miui.permission.PermissionManager;

class m extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f6176a;

    m(Context context) {
        this.f6176a = context;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        PermissionManager.getInstance(this.f6176a).updateData();
        return null;
    }
}
