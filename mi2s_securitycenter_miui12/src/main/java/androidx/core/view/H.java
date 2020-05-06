package androidx.core.view;

import a.d.e.c;
import android.os.Build;
import android.view.WindowInsets;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import java.util.Objects;

public class H {

    /* renamed from: a  reason: collision with root package name */
    private final Object f786a;

    @VisibleForTesting
    @RestrictTo({RestrictTo.a.LIBRARY})
    H(@Nullable Object obj) {
        this.f786a = obj;
    }

    @RequiresApi(20)
    @NonNull
    public static H a(@NonNull WindowInsets windowInsets) {
        return new H(Objects.requireNonNull(windowInsets));
    }

    public int a() {
        if (Build.VERSION.SDK_INT >= 20) {
            return ((WindowInsets) this.f786a).getSystemWindowInsetBottom();
        }
        return 0;
    }

    public H a(int i, int i2, int i3, int i4) {
        if (Build.VERSION.SDK_INT >= 20) {
            return new H(((WindowInsets) this.f786a).replaceSystemWindowInsets(i, i2, i3, i4));
        }
        return null;
    }

    public int b() {
        if (Build.VERSION.SDK_INT >= 20) {
            return ((WindowInsets) this.f786a).getSystemWindowInsetLeft();
        }
        return 0;
    }

    public int c() {
        if (Build.VERSION.SDK_INT >= 20) {
            return ((WindowInsets) this.f786a).getSystemWindowInsetRight();
        }
        return 0;
    }

    public int d() {
        if (Build.VERSION.SDK_INT >= 20) {
            return ((WindowInsets) this.f786a).getSystemWindowInsetTop();
        }
        return 0;
    }

    public boolean e() {
        if (Build.VERSION.SDK_INT >= 21) {
            return ((WindowInsets) this.f786a).isConsumed();
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof H)) {
            return false;
        }
        return c.a(this.f786a, ((H) obj).f786a);
    }

    @RequiresApi(20)
    @Nullable
    public WindowInsets f() {
        return (WindowInsets) this.f786a;
    }

    public int hashCode() {
        Object obj = this.f786a;
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }
}
