package androidx.core.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

public class a extends androidx.core.content.a {

    /* renamed from: c  reason: collision with root package name */
    private static b f682c;

    /* renamed from: androidx.core.app.a$a  reason: collision with other inner class name */
    public interface C0011a {
    }

    public interface b {
        boolean a(@NonNull Activity activity, @IntRange(from = 0) int i, int i2, @Nullable Intent intent);
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public interface c {
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public static b a() {
        return f682c;
    }

    public static void a(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= 16) {
            activity.finishAffinity();
        } else {
            activity.finish();
        }
    }

    public static void b(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= 28 || !e.a(activity)) {
            activity.recreate();
        }
    }
}
