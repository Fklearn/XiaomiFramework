package com.miui.securityscan.scanner;

import android.util.Log;
import com.miui.guardprovider.aidl.IVirusObserver;
import com.miui.guardprovider.aidl.VirusInfo;
import com.miui.securityscan.scanner.U;

class T implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ U.a f7873a;

    T(U.a aVar) {
        this.f7873a = aVar;
    }

    public void run() {
        try {
            int unused = this.f7873a.f7878c = this.f7873a.k.a((String[]) this.f7873a.e.keySet().toArray(new String[this.f7873a.e.size()]), (IVirusObserver) this.f7873a, false);
            if (this.f7873a.f7878c == -1) {
                this.f7873a.a(this.f7873a.f7878c, (VirusInfo[]) null);
            }
            Log.d("SystemCheckManager", "GPObserver taskId = " + this.f7873a.f7878c);
        } catch (Exception e) {
            Log.e("SystemCheckManager", "GPObserver Exception", e);
        }
    }
}
