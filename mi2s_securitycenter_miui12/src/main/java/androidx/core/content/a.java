package androidx.core.content;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static final Object f701a = new Object();

    /* renamed from: b  reason: collision with root package name */
    private static TypedValue f702b;

    @ColorInt
    public static int a(@NonNull Context context, @ColorRes int i) {
        return Build.VERSION.SDK_INT >= 23 ? context.getColor(i) : context.getResources().getColor(i);
    }

    @Nullable
    public static Context a(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= 24) {
            return context.createDeviceProtectedStorageContext();
        }
        return null;
    }

    public static boolean a(@NonNull Context context, @NonNull Intent[] intentArr, @Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= 16) {
            context.startActivities(intentArr, bundle);
            return true;
        }
        context.startActivities(intentArr);
        return true;
    }

    @Nullable
    public static ColorStateList b(@NonNull Context context, @ColorRes int i) {
        return Build.VERSION.SDK_INT >= 23 ? context.getColorStateList(i) : context.getResources().getColorStateList(i);
    }

    @Nullable
    public static Drawable c(@NonNull Context context, @DrawableRes int i) {
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 21) {
            return context.getDrawable(i);
        }
        if (i2 < 16) {
            synchronized (f701a) {
                if (f702b == null) {
                    f702b = new TypedValue();
                }
                context.getResources().getValue(i, f702b, true);
                i = f702b.resourceId;
            }
        }
        return context.getResources().getDrawable(i);
    }
}
