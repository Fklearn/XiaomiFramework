package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import java.lang.ref.WeakReference;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public class Ia extends Resources {

    /* renamed from: a  reason: collision with root package name */
    private static boolean f502a = false;

    /* renamed from: b  reason: collision with root package name */
    private final WeakReference<Context> f503b;

    public Ia(@NonNull Context context, @NonNull Resources resources) {
        super(resources.getAssets(), resources.getDisplayMetrics(), resources.getConfiguration());
        this.f503b = new WeakReference<>(context);
    }

    public static boolean a() {
        return f502a;
    }

    public static boolean b() {
        return a() && Build.VERSION.SDK_INT <= 20;
    }

    /* access modifiers changed from: package-private */
    public final Drawable a(int i) {
        return super.getDrawable(i);
    }

    public Drawable getDrawable(int i) {
        Context context = (Context) this.f503b.get();
        return context != null ? X.a().a(context, this, i) : super.getDrawable(i);
    }
}
