package androidx.core.widget;

import android.os.Build;
import android.widget.EdgeEffect;
import androidx.annotation.NonNull;

public final class d {
    public static void a(@NonNull EdgeEffect edgeEffect, float f, float f2) {
        if (Build.VERSION.SDK_INT >= 21) {
            edgeEffect.onPull(f, f2);
        } else {
            edgeEffect.onPull(f);
        }
    }
}
