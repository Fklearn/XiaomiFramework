package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import java.lang.ref.WeakReference;

class ua extends Y {

    /* renamed from: b  reason: collision with root package name */
    private final WeakReference<Context> f665b;

    public ua(@NonNull Context context, @NonNull Resources resources) {
        super(resources);
        this.f665b = new WeakReference<>(context);
    }

    public Drawable getDrawable(int i) {
        Drawable drawable = super.getDrawable(i);
        Context context = (Context) this.f665b.get();
        if (!(drawable == null || context == null)) {
            X.a().a(context, i, drawable);
        }
        return drawable;
    }
}
