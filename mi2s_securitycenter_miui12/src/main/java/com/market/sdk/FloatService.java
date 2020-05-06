package com.market.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import b.a.c;
import com.xiaomi.market.IAppDownloadManager;

public class FloatService extends c implements IAppDownloadManager {
    /* access modifiers changed from: private */
    public IAppDownloadManager k;

    private FloatService(Context context, Intent intent) {
        super(context, intent);
    }

    public static IAppDownloadManager a(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(p.f2241b, "com.xiaomi.market.data.AppDownloadService"));
        return new FloatService(context, intent);
    }

    public void a() {
    }

    public void a(Bundle bundle) {
        a((c.b) new j(this, bundle), "download");
    }

    public void a(IBinder iBinder) {
        this.k = IAppDownloadManager.Stub.a(iBinder);
    }

    public void a(String str, int i) {
        a((c.b) new l(this, str, i), "lifecycleChanged");
    }

    public IBinder asBinder() {
        return null;
    }

    public void c(Uri uri) {
        a((c.b) new k(this, uri), "downloadByUri");
    }
}
