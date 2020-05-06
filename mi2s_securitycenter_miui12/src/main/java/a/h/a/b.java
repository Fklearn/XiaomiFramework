package a.h.a;

import a.c.j;
import a.h.b.a;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.i;
import androidx.lifecycle.p;
import androidx.lifecycle.q;
import androidx.lifecycle.s;
import androidx.lifecycle.t;
import androidx.lifecycle.u;
import java.io.FileDescriptor;
import java.io.PrintWriter;

class b extends a {

    /* renamed from: a  reason: collision with root package name */
    static boolean f154a = false;
    @NonNull

    /* renamed from: b  reason: collision with root package name */
    private final i f155b;
    @NonNull

    /* renamed from: c  reason: collision with root package name */
    private final c f156c;

    public static class a<D> extends p<D> implements a.C0007a<D> {
        private final int k;
        @Nullable
        private final Bundle l;
        @NonNull
        private final a.h.b.a<D> m;
        private i n;
        private C0006b<D> o;
        private a.h.b.a<D> p;

        /* access modifiers changed from: package-private */
        @MainThread
        public a.h.b.a<D> a(boolean z) {
            if (b.f154a) {
                Log.v("LoaderManager", "  Destroying: " + this);
            }
            this.m.a();
            throw null;
        }

        /* access modifiers changed from: protected */
        public void a() {
            if (b.f154a) {
                Log.v("LoaderManager", "  Starting: " + this);
            }
            this.m.c();
            throw null;
        }

        public void a(@NonNull q<? super D> qVar) {
            super.a(qVar);
            this.n = null;
            this.o = null;
        }

        public void a(D d2) {
            super.a(d2);
            a.h.b.a<D> aVar = this.p;
            if (aVar != null) {
                aVar.b();
                throw null;
            }
        }

        public void a(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            printWriter.print(str);
            printWriter.print("mId=");
            printWriter.print(this.k);
            printWriter.print(" mArgs=");
            printWriter.println(this.l);
            printWriter.print(str);
            printWriter.print("mLoader=");
            printWriter.println(this.m);
            a.h.b.a<D> aVar = this.m;
            aVar.a(str + "  ", fileDescriptor, printWriter, strArr);
            throw null;
        }

        /* access modifiers changed from: protected */
        public void b() {
            if (b.f154a) {
                Log.v("LoaderManager", "  Stopping: " + this);
            }
            this.m.d();
            throw null;
        }

        /* access modifiers changed from: package-private */
        public void c() {
            i iVar = this.n;
            C0006b<D> bVar = this.o;
            if (iVar != null && bVar != null) {
                super.a(bVar);
                a(iVar, bVar);
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(64);
            sb.append("LoaderInfo{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" #");
            sb.append(this.k);
            sb.append(" : ");
            a.d.e.a.a(this.m, sb);
            sb.append("}}");
            return sb.toString();
        }
    }

    /* renamed from: a.h.a.b$b  reason: collision with other inner class name */
    static class C0006b<D> implements q<D> {
    }

    static class c extends s {

        /* renamed from: c  reason: collision with root package name */
        private static final t.a f157c = new c();

        /* renamed from: d  reason: collision with root package name */
        private j<a> f158d = new j<>();
        private boolean e = false;

        c() {
        }

        @NonNull
        static c a(u uVar) {
            return (c) new t(uVar, f157c).a(c.class);
        }

        public void a(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (this.f158d.a() > 0) {
                printWriter.print(str);
                printWriter.println("Loaders:");
                String str2 = str + "    ";
                if (this.f158d.a() > 0) {
                    a d2 = this.f158d.d(0);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(this.f158d.b(0));
                    printWriter.print(": ");
                    printWriter.println(d2.toString());
                    d2.a(str2, fileDescriptor, printWriter, strArr);
                    throw null;
                }
            }
        }

        /* access modifiers changed from: protected */
        public void b() {
            super.b();
            if (this.f158d.a() <= 0) {
                this.f158d.clear();
            } else {
                this.f158d.d(0).a(true);
                throw null;
            }
        }

        /* access modifiers changed from: package-private */
        public void c() {
            int a2 = this.f158d.a();
            for (int i = 0; i < a2; i++) {
                this.f158d.d(i).c();
            }
        }
    }

    b(@NonNull i iVar, @NonNull u uVar) {
        this.f155b = iVar;
        this.f156c = c.a(uVar);
    }

    public void a() {
        this.f156c.c();
    }

    @Deprecated
    public void a(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.f156c.a(str, fileDescriptor, printWriter, strArr);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("LoaderManager{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" in ");
        a.d.e.a.a(this.f155b, sb);
        sb.append("}}");
        return sb.toString();
    }
}
