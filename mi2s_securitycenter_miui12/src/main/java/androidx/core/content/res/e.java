package androidx.core.content.res;

import android.graphics.Typeface;
import androidx.core.content.res.g;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Typeface f716a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ g.a f717b;

    e(g.a aVar, Typeface typeface) {
        this.f717b = aVar;
        this.f716a = typeface;
    }

    public void run() {
        this.f717b.a(this.f716a);
    }
}
