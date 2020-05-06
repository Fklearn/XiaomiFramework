package androidx.core.view;

import android.os.Build;
import android.view.Gravity;

/* renamed from: androidx.core.view.c  reason: case insensitive filesystem */
public final class C0125c {
    public static int a(int i, int i2) {
        return Build.VERSION.SDK_INT >= 17 ? Gravity.getAbsoluteGravity(i, i2) : i & -8388609;
    }
}
