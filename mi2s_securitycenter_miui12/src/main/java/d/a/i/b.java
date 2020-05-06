package d.a.i;

import android.animation.TimeInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import d.k.a.d;
import d.k.a.e;
import d.k.a.f;
import d.k.a.g;
import d.k.a.h;
import d.k.a.i;
import d.k.a.j;
import d.k.a.k;
import d.k.a.l;
import d.k.a.m;
import d.k.a.n;
import d.k.a.o;
import d.k.a.p;
import d.k.a.q;
import java.util.Arrays;

public class b {

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public final int f8781a;

        /* renamed from: b  reason: collision with root package name */
        public float[] f8782b;

        public a(int i) {
            this.f8781a = i;
        }

        public a a(float... fArr) {
            this.f8782b = fArr;
            return this;
        }

        public String toString() {
            return "EaseStyle{style=" + this.f8781a + ", factors=" + Arrays.toString(this.f8782b) + '}';
        }
    }

    /* renamed from: d.a.i.b$b  reason: collision with other inner class name */
    public static class C0075b extends a {

        /* renamed from: c  reason: collision with root package name */
        public long f8783c = 300;

        public C0075b(int i) {
            super(i);
        }

        public C0075b a(long j) {
            this.f8783c = j;
            return this;
        }

        public String toString() {
            return "InterpolateEaseStyle{style=" + this.f8781a + ", duration=" + this.f8783c + ", factors=" + Arrays.toString(this.f8782b) + '}';
        }
    }

    public static class c implements TimeInterpolator {

        /* renamed from: a  reason: collision with root package name */
        private float f8784a = 0.95f;

        /* renamed from: b  reason: collision with root package name */
        private float f8785b = 0.6f;

        /* renamed from: c  reason: collision with root package name */
        private float f8786c = -1.0f;

        /* renamed from: d  reason: collision with root package name */
        private float f8787d = this.f8786c;
        private float e = 1.0f;
        private float f;
        private float g;
        private float h;
        private float i;
        private float j;

        public c() {
            a();
        }

        private void a() {
            double pow = Math.pow(6.283185307179586d / ((double) this.f8785b), 2.0d);
            float f2 = this.e;
            this.f = (float) (pow * ((double) f2));
            this.g = (float) (((((double) this.f8784a) * 12.566370614359172d) * ((double) f2)) / ((double) this.f8785b));
            float f3 = f2 * 4.0f * this.f;
            float f4 = this.g;
            float f5 = this.e;
            this.h = ((float) Math.sqrt((double) (f3 - (f4 * f4)))) / (f5 * 2.0f);
            this.i = -((this.g / 2.0f) * f5);
            this.j = (0.0f - (this.i * this.f8786c)) / this.h;
        }

        public c a(float f2) {
            this.f8784a = f2;
            a();
            return this;
        }

        public c b(float f2) {
            this.f8785b = f2;
            a();
            return this;
        }

        public float getInterpolation(float f2) {
            return (float) ((Math.pow(2.718281828459045d, (double) (this.i * f2)) * ((((double) this.f8787d) * Math.cos((double) (this.h * f2))) + (((double) this.j) * Math.sin((double) (this.h * f2))))) + 1.0d);
        }
    }

    public static TimeInterpolator a(C0075b bVar) {
        if (bVar == null) {
            return null;
        }
        switch (bVar.f8781a) {
            case -1:
            case 1:
                return new LinearInterpolator();
            case 0:
                c cVar = new c();
                cVar.a(bVar.f8782b[0]);
                cVar.b(bVar.f8782b[1]);
                return cVar;
            case 2:
                return new g();
            case 3:
                return new i();
            case 4:
                return new h();
            case 5:
                return new d.k.a.a();
            case 6:
                return new d.k.a.c();
            case 7:
                return new d.k.a.b();
            case 8:
                return new j();
            case 9:
                return new i();
            case 10:
                return new k();
            case 11:
                return new l();
            case 12:
                return new n();
            case 13:
                return new m();
            case 14:
                return new o();
            case 15:
                return new q();
            case 16:
                return new p();
            case 17:
                return new d();
            case 18:
                return new f();
            case 19:
                return new e();
            case 20:
                return new DecelerateInterpolator();
            case 21:
                return new AccelerateDecelerateInterpolator();
            case 22:
                return new AccelerateInterpolator();
            default:
                return null;
        }
    }

    public static a a(int i, float... fArr) {
        if (i >= -1) {
            C0075b b2 = b(i, fArr.length > 1 ? Arrays.copyOfRange(fArr, 1, fArr.length) : new float[0]);
            if (fArr.length > 0) {
                b2.a((long) ((int) fArr[0]));
            }
            return b2;
        }
        a aVar = new a(i);
        aVar.a(fArr);
        return aVar;
    }

    public static boolean a(int i) {
        return i < -1;
    }

    private static C0075b b(int i, float... fArr) {
        C0075b bVar = new C0075b(i);
        bVar.a(fArr);
        return bVar;
    }
}
