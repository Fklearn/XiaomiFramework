package b.d.a.a.b;

import java.io.Serializable;

public class b extends c implements Serializable {

    /* renamed from: a  reason: collision with root package name */
    private final int f2099a;

    /* renamed from: b  reason: collision with root package name */
    private final int[] f2100b;

    /* renamed from: c  reason: collision with root package name */
    private final float[] f2101c;

    /* renamed from: d  reason: collision with root package name */
    private Float f2102d = null;

    public b(int i, int[] iArr, float[] fArr) {
        if (iArr.length != fArr.length || iArr.length > i) {
            throw new RuntimeException("构造参数异常,indices.length = " + iArr.length + " values.length=" + fArr.length + " size = " + i);
        }
        this.f2099a = i;
        this.f2100b = iArr;
        this.f2101c = fArr;
    }

    public b(int i, int[] iArr, float[] fArr, float f) {
        if (fArr.length > i || iArr.length > i || fArr.length > iArr.length) {
            throw new RuntimeException("构造参数异常,indices.length = " + iArr.length + " values.length=" + fArr.length + " size = " + i);
        }
        this.f2099a = i;
        this.f2100b = iArr;
        this.f2101c = fArr;
        this.f2102d = Float.valueOf(f);
    }

    public int a() {
        return this.f2099a;
    }

    /* access modifiers changed from: package-private */
    public int[] b() {
        return this.f2100b;
    }

    /* access modifiers changed from: package-private */
    public int c() {
        return this.f2100b.length;
    }

    public a d() {
        float[] fArr = new float[this.f2099a];
        int i = 0;
        while (true) {
            int[] iArr = this.f2100b;
            if (i >= iArr.length) {
                return new a(fArr);
            }
            float[] fArr2 = this.f2101c;
            if (i < fArr2.length) {
                fArr[iArr[i]] = fArr2[i];
            } else {
                fArr[iArr[i]] = this.f2102d.floatValue();
            }
            i++;
        }
    }

    /* access modifiers changed from: package-private */
    public float[] e() {
        float[] fArr = new float[this.f2100b.length];
        for (int i = 0; i < this.f2100b.length; i++) {
            float[] fArr2 = this.f2101c;
            if (i < fArr2.length) {
                fArr[i] = fArr2[i];
            } else {
                fArr[i] = this.f2102d.floatValue();
            }
        }
        return fArr;
    }
}
