package b.d.d.e;

import java.io.Serializable;

public class b extends c implements Serializable {

    /* renamed from: a  reason: collision with root package name */
    private final int f2160a;

    /* renamed from: b  reason: collision with root package name */
    private final int[] f2161b;

    /* renamed from: c  reason: collision with root package name */
    private final double[] f2162c;

    /* renamed from: d  reason: collision with root package name */
    private Double f2163d = null;

    public b(int i, int[] iArr, double[] dArr) {
        if (iArr.length != dArr.length || iArr.length > i) {
            throw new RuntimeException("构造参数异常,indices.length = " + iArr.length + " values.length=" + dArr.length + " size = " + i);
        }
        this.f2160a = i;
        this.f2161b = iArr;
        this.f2162c = dArr;
    }

    public double a(int i) {
        if (i < this.f2160a) {
            int i2 = 0;
            while (true) {
                int[] iArr = this.f2161b;
                if (i2 >= iArr.length) {
                    return 0.0d;
                }
                if (i == iArr[i2]) {
                    double[] dArr = this.f2162c;
                    return i2 < dArr.length ? dArr[i2] : this.f2163d.doubleValue();
                }
                i2++;
            }
        } else {
            throw new RuntimeException("参数异常i = " + i + "; >" + this.f2160a);
        }
    }

    public int a() {
        return this.f2160a;
    }

    public a b() {
        double[] dArr = new double[this.f2160a];
        int i = 0;
        while (true) {
            int[] iArr = this.f2161b;
            if (i >= iArr.length) {
                return new a(dArr);
            }
            double[] dArr2 = this.f2162c;
            if (i < dArr2.length) {
                dArr[iArr[i]] = dArr2[i];
            } else {
                dArr[iArr[i]] = this.f2163d.doubleValue();
            }
            i++;
        }
    }
}
