package androidx.core.provider;

import android.content.Context;
import android.graphics.Typeface;
import androidx.core.provider.f;
import java.util.concurrent.Callable;

class b implements Callable<f.c> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f740a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ a f741b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ int f742c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ String f743d;

    b(Context context, a aVar, int i, String str) {
        this.f740a = context;
        this.f741b = aVar;
        this.f742c = i;
        this.f743d = str;
    }

    public f.c call() {
        f.c a2 = f.a(this.f740a, this.f741b, this.f742c);
        Typeface typeface = a2.f757a;
        if (typeface != null) {
            f.f747a.a(this.f743d, typeface);
        }
        return a2;
    }
}
