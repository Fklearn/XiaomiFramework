package d.a.i;

import android.os.SystemClock;
import java.util.Arrays;
import java.util.LinkedList;

public class f {

    /* renamed from: a  reason: collision with root package name */
    private LinkedList<a> f8791a = new LinkedList<>();

    /* renamed from: b  reason: collision with root package name */
    private float[] f8792b;

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        double[] f8793a;

        /* renamed from: b  reason: collision with root package name */
        long f8794b;

        private a() {
        }
    }

    private float a(double d2, double d3, long j) {
        return (float) (j == 0 ? 0.0d : (d2 - d3) / ((double) (((float) j) / 1000.0f)));
    }

    private float a(int i, a aVar, a aVar2) {
        long j;
        a aVar3;
        float f;
        a aVar4 = aVar;
        a aVar5 = aVar2;
        double d2 = aVar4.f8793a[i];
        long j2 = aVar4.f8794b;
        double a2 = (double) a(d2, aVar5.f8793a[i], j2 - aVar5.f8794b);
        int size = this.f8791a.size() - 2;
        long j3 = 0;
        a aVar6 = null;
        while (true) {
            if (size < 0) {
                j = j3;
                aVar3 = aVar6;
                f = Float.MAX_VALUE;
                break;
            }
            aVar3 = this.f8791a.get(size);
            long j4 = j2 - aVar3.f8794b;
            if (j4 <= 30 || j4 >= 100) {
                size--;
                aVar6 = aVar3;
                j3 = j4;
            } else {
                f = a(d2, aVar3.f8793a[i], j4);
                double d3 = (double) f;
                if (a2 * d3 > 0.0d) {
                    f = (float) (f > 0.0f ? Math.max(a2, d3) : Math.min(a2, d3));
                }
                j = j4;
            }
        }
        if (f != Float.MAX_VALUE || aVar3 == null) {
            return f;
        }
        return a(d2, aVar3.f8793a[i], j);
    }

    private void a(a aVar) {
        this.f8791a.add(aVar);
        if (this.f8791a.size() > 10) {
            this.f8791a.remove(0);
        }
        d();
    }

    private void b() {
        float[] fArr = this.f8792b;
        if (fArr != null) {
            Arrays.fill(fArr, 0.0f);
        }
    }

    private a c() {
        a aVar = new a();
        aVar.f8794b = SystemClock.uptimeMillis();
        return aVar;
    }

    private void d() {
        int size = this.f8791a.size();
        if (size >= 2) {
            a last = this.f8791a.getLast();
            a aVar = this.f8791a.get(size - 2);
            float[] fArr = this.f8792b;
            if (fArr == null || fArr.length < last.f8793a.length) {
                this.f8792b = new float[last.f8793a.length];
            }
            for (int i = 0; i < last.f8793a.length; i++) {
                this.f8792b[i] = a(i, last, aVar);
            }
            return;
        }
        b();
    }

    public float a(int i) {
        float[] fArr;
        long uptimeMillis = SystemClock.uptimeMillis();
        if ((this.f8791a.size() <= 0 || Math.abs(uptimeMillis - this.f8791a.getLast().f8794b) <= 50) && (fArr = this.f8792b) != null && fArr.length > i) {
            return fArr[i];
        }
        return 0.0f;
    }

    public void a() {
        this.f8791a.clear();
        b();
    }

    public void a(double... dArr) {
        if (dArr != null && dArr.length != 0) {
            a c2 = c();
            c2.f8793a = dArr;
            a(c2);
        }
    }
}
