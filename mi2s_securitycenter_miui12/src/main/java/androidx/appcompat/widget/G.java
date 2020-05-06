package androidx.appcompat.widget;

import android.graphics.Typeface;
import android.os.Build;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.g;
import java.lang.ref.WeakReference;

class G extends g.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f482a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f483b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ WeakReference f484c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ H f485d;

    G(H h, int i, int i2, WeakReference weakReference) {
        this.f485d = h;
        this.f482a = i;
        this.f483b = i2;
        this.f484c = weakReference;
    }

    public void a(int i) {
    }

    public void a(@NonNull Typeface typeface) {
        int i;
        if (Build.VERSION.SDK_INT >= 28 && (i = this.f482a) != -1) {
            typeface = Typeface.create(typeface, i, (this.f483b & 2) != 0);
        }
        this.f485d.a((WeakReference<TextView>) this.f484c, typeface);
    }
}
