package androidx.fragment.app;

import a.d.e.f;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* renamed from: androidx.fragment.app.k  reason: case insensitive filesystem */
public abstract class C0141k<E> extends C0138h {
    @Nullable

    /* renamed from: a  reason: collision with root package name */
    private final Activity f916a;
    @NonNull

    /* renamed from: b  reason: collision with root package name */
    private final Context f917b;
    @NonNull

    /* renamed from: c  reason: collision with root package name */
    private final Handler f918c;

    /* renamed from: d  reason: collision with root package name */
    private final int f919d;
    final t e;

    C0141k(@Nullable Activity activity, @NonNull Context context, @NonNull Handler handler, int i) {
        this.e = new t();
        this.f916a = activity;
        f.a(context, "context == null");
        this.f917b = context;
        f.a(handler, "handler == null");
        this.f918c = handler;
        this.f919d = i;
    }

    C0141k(@NonNull C0137g gVar) {
        this(gVar, gVar, new Handler(), 0);
    }

    @Nullable
    public View a(int i) {
        return null;
    }

    /* access modifiers changed from: package-private */
    public void a(@NonNull Fragment fragment) {
    }

    public void a(@NonNull String str, @Nullable FileDescriptor fileDescriptor, @NonNull PrintWriter printWriter, @Nullable String[] strArr) {
    }

    public boolean b(@NonNull Fragment fragment) {
        return true;
    }

    public boolean c() {
        return true;
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public Activity e() {
        return this.f916a;
    }

    /* access modifiers changed from: package-private */
    @NonNull
    public Context f() {
        return this.f917b;
    }

    /* access modifiers changed from: package-private */
    @NonNull
    public Handler g() {
        return this.f918c;
    }

    @Nullable
    public abstract E h();

    @NonNull
    public LayoutInflater i() {
        return LayoutInflater.from(this.f917b);
    }

    public int j() {
        return this.f919d;
    }

    public boolean k() {
        return true;
    }

    public void l() {
    }
}
