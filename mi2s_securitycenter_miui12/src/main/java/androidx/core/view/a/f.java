package androidx.core.view.a;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

public interface f {

    public static abstract class a {

        /* renamed from: a  reason: collision with root package name */
        Bundle f815a;

        @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
        public void a(Bundle bundle) {
            this.f815a = bundle;
        }
    }

    public static final class b extends a {
    }

    public static final class c extends a {
    }

    public static final class d extends a {
    }

    public static final class e extends a {
    }

    /* renamed from: androidx.core.view.a.f$f  reason: collision with other inner class name */
    public static final class C0014f extends a {
    }

    public static final class g extends a {
    }

    public static final class h extends a {
    }

    boolean a(@NonNull View view, @Nullable a aVar);
}
