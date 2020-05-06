package com.miui.securityscan.cards;

import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;
import com.miui.securitycenter.R;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f7647a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ g f7648b;

    e(g gVar, String str) {
        this.f7648b = gVar;
        this.f7647a = str;
    }

    public void run() {
        try {
            PackageManager packageManager = this.f7648b.f.getPackageManager();
            CharSequence loadLabel = packageManager.getApplicationInfo(this.f7647a, 0).loadLabel(packageManager);
            Toast.makeText(this.f7648b.f, this.f7648b.f.getString(R.string.install_sucess, new Object[]{loadLabel.toString()}), 1).show();
        } catch (Exception e) {
            Log.e("InstallCacheManager", " Toast error ", e);
        }
    }
}
