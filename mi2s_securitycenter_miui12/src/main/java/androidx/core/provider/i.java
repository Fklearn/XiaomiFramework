package androidx.core.provider;

import android.os.Handler;
import androidx.core.provider.k;
import java.util.concurrent.Callable;

class i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Callable f762a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Handler f763b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ k.a f764c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ k f765d;

    i(k kVar, Callable callable, Handler handler, k.a aVar) {
        this.f765d = kVar;
        this.f762a = callable;
        this.f763b = handler;
        this.f764c = aVar;
    }

    public void run() {
        Object obj;
        try {
            obj = this.f762a.call();
        } catch (Exception unused) {
            obj = null;
        }
        this.f763b.post(new h(this, obj));
    }
}
