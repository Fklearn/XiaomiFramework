package androidx.fragment.app;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

/* renamed from: androidx.fragment.app.l  reason: case insensitive filesystem */
public abstract class C0142l {

    /* renamed from: a  reason: collision with root package name */
    static final C0140j f920a = new C0140j();

    /* renamed from: b  reason: collision with root package name */
    private C0140j f921b = null;

    /* renamed from: androidx.fragment.app.l$a */
    public interface a {
    }

    /* renamed from: androidx.fragment.app.l$b */
    public static abstract class b {
        public abstract void a(@NonNull C0142l lVar, @NonNull Fragment fragment);

        public abstract void a(@NonNull C0142l lVar, @NonNull Fragment fragment, @NonNull Context context);

        public abstract void a(@NonNull C0142l lVar, @NonNull Fragment fragment, @Nullable Bundle bundle);

        public abstract void a(@NonNull C0142l lVar, @NonNull Fragment fragment, @NonNull View view, @Nullable Bundle bundle);

        public abstract void b(@NonNull C0142l lVar, @NonNull Fragment fragment);

        public abstract void b(@NonNull C0142l lVar, @NonNull Fragment fragment, @NonNull Context context);

        public abstract void b(@NonNull C0142l lVar, @NonNull Fragment fragment, @Nullable Bundle bundle);

        public abstract void c(@NonNull C0142l lVar, @NonNull Fragment fragment);

        public abstract void c(@NonNull C0142l lVar, @NonNull Fragment fragment, @Nullable Bundle bundle);

        public abstract void d(@NonNull C0142l lVar, @NonNull Fragment fragment);

        public abstract void d(@NonNull C0142l lVar, @NonNull Fragment fragment, @NonNull Bundle bundle);

        public abstract void e(@NonNull C0142l lVar, @NonNull Fragment fragment);

        public abstract void f(@NonNull C0142l lVar, @NonNull Fragment fragment);

        public abstract void g(@NonNull C0142l lVar, @NonNull Fragment fragment);
    }

    /* renamed from: androidx.fragment.app.l$c */
    public interface c {
        void onBackStackChanged();
    }

    @NonNull
    public C0140j a() {
        if (this.f921b == null) {
            this.f921b = f920a;
        }
        return this.f921b;
    }

    public void a(@NonNull C0140j jVar) {
        this.f921b = jVar;
    }

    public abstract void a(@NonNull String str, @Nullable FileDescriptor fileDescriptor, @NonNull PrintWriter printWriter, @Nullable String[] strArr);

    @NonNull
    public abstract List<Fragment> b();

    public abstract boolean c();
}
