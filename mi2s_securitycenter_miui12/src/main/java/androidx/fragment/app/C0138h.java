package androidx.fragment.app;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/* renamed from: androidx.fragment.app.h  reason: case insensitive filesystem */
public abstract class C0138h {
    @Nullable
    public abstract View a(@IdRes int i);

    @NonNull
    @Deprecated
    public Fragment a(@NonNull Context context, @NonNull String str, @Nullable Bundle bundle) {
        return Fragment.a(context, str, bundle);
    }

    public abstract boolean c();
}
