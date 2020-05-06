package androidx.fragment.app;

import androidx.annotation.Nullable;
import androidx.lifecycle.f;
import java.util.ArrayList;

public abstract class z {

    /* renamed from: a  reason: collision with root package name */
    ArrayList<a> f953a = new ArrayList<>();

    /* renamed from: b  reason: collision with root package name */
    int f954b;

    /* renamed from: c  reason: collision with root package name */
    int f955c;

    /* renamed from: d  reason: collision with root package name */
    int f956d;
    int e;
    int f;
    int g;
    boolean h;
    boolean i = true;
    @Nullable
    String j;
    int k;
    CharSequence l;
    int m;
    CharSequence n;
    ArrayList<String> o;
    ArrayList<String> p;
    boolean q = false;
    ArrayList<Runnable> r;

    static final class a {

        /* renamed from: a  reason: collision with root package name */
        int f957a;

        /* renamed from: b  reason: collision with root package name */
        Fragment f958b;

        /* renamed from: c  reason: collision with root package name */
        int f959c;

        /* renamed from: d  reason: collision with root package name */
        int f960d;
        int e;
        int f;
        f.b g;
        f.b h;

        a() {
        }

        a(int i, Fragment fragment) {
            this.f957a = i;
            this.f958b = fragment;
            f.b bVar = f.b.RESUMED;
            this.g = bVar;
            this.h = bVar;
        }
    }

    /* access modifiers changed from: package-private */
    public void a(a aVar) {
        this.f953a.add(aVar);
        aVar.f959c = this.f954b;
        aVar.f960d = this.f955c;
        aVar.e = this.f956d;
        aVar.f = this.e;
    }
}
