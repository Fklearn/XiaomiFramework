package b.d.e.a;

import android.util.Log;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static final String f2164a = "b.d.e.a.a";

    /* renamed from: b  reason: collision with root package name */
    private static final int[][] f2165b;

    /* renamed from: c  reason: collision with root package name */
    private double[] f2166c;

    /* renamed from: d  reason: collision with root package name */
    private double[][] f2167d;
    private int[] e;
    private int[] f;
    private int g;
    private int[] h;
    private int[] i;
    private int j;
    private int k;
    private byte[] l;

    static {
        int[] iArr = new int[4];
        iArr[0] = 1;
        int[] iArr2 = new int[4];
        iArr2[0] = 2;
        iArr2[2] = 1;
        iArr2[3] = 2;
        f2165b = new int[][]{iArr, iArr2};
    }

    private double a(int i2, char c2) {
        int i3 = this.f[i2];
        int i4 = this.e[i2] - 1;
        int i5 = 0;
        while (i5 <= i4) {
            int i6 = (i5 + i4) / 2;
            int i7 = (i6 * 6) + i3;
            byte[] bArr = this.l;
            char c3 = (char) (((bArr[i7] & 255) << 8) | (bArr[i7 + 1] & 255));
            if (c3 == c2) {
                return (double) b.a(bArr, i7 + 2);
            }
            if (c3 < c2) {
                i5 = i6 + 1;
            } else {
                i4 = i6 - 1;
            }
        }
        return -3.14E100d;
    }

    private int a(byte[] bArr, int i2, char[] cArr, int i3, int i4) {
        for (int i5 = 0; i5 < i4; i5++) {
            int i6 = i5 * 2;
            char c2 = (char) ((bArr[i6 + 1 + i2] & 255) | ((bArr[i6 + i2] & 255) << 8));
            int i7 = i3 + i5;
            if (cArr[i7] != c2) {
                return cArr[i7] - c2;
            }
        }
        return 0;
    }

    private List<String> a(char[] cArr, int i2, int i3) {
        int i4;
        char[] cArr2 = cArr;
        ArrayList arrayList = new ArrayList();
        int i5 = i3 - i2;
        double[][] dArr = (double[][]) Array.newInstance(double.class, new int[]{i5, 4});
        int[][] iArr = (int[][]) Array.newInstance(int.class, new int[]{i5, 4});
        for (int i6 = 0; i6 < i5; i6++) {
            if (i6 == 0) {
                for (int i7 = 0; i7 < 4; i7++) {
                    dArr[i6][i7] = this.f2166c[i7] + a(i7, cArr2[i6 + i2]);
                }
            } else {
                for (int i8 = 0; i8 < 4; i8++) {
                    Double valueOf = Double.valueOf(-3.14E100d);
                    for (int i9 = 0; i9 < 4; i9++) {
                        Double valueOf2 = Double.valueOf(dArr[i6 - 1][i9] + this.f2167d[i9][i8]);
                        if (valueOf2.doubleValue() > valueOf.doubleValue()) {
                            iArr[i6][i8] = i9;
                            valueOf = valueOf2;
                        }
                    }
                    if (valueOf.doubleValue() == -3.14E100d || a(i8, cArr2[i6 + i2]) == -3.14E100d) {
                        iArr[i6][i8] = 1;
                    }
                    dArr[i6][i8] = valueOf.doubleValue() + a(i8, cArr2[i6 + i2]);
                }
            }
        }
        int i10 = i5 - 1;
        int i11 = 3;
        if (dArr[i10][1] > dArr[i10][3]) {
            i11 = 1;
        }
        int[] iArr2 = new int[i5];
        iArr2[i10] = i11;
        for (int i12 = i5 - 2; i12 >= 0; i12--) {
            int i13 = i12 + 1;
            iArr2[i12] = iArr[i13][iArr2[i13]];
        }
        int i14 = 0;
        while (true) {
            if (i14 >= i5) {
                break;
            }
            int i15 = f2165b[0][iArr2[i14]];
            int i16 = i14 + 1;
            while (i16 < i5 && i15 == 1) {
                i15 = f2165b[i15][iArr2[i16]];
                i16++;
            }
            if (i15 == 0) {
                arrayList.add(new String(cArr2, i2 + i14, i16 - i14));
                i14 = i16;
            } else if (i16 >= i5) {
                while (i14 < i5) {
                    arrayList.add(new String(cArr2, i2 + i14, 1));
                    i14++;
                }
            } else {
                while (true) {
                    i4 = i16 - 1;
                    if (i14 >= i4) {
                        break;
                    }
                    arrayList.add(new String(cArr2, i2 + i14, 1));
                    i14++;
                }
                i14 = i4;
            }
        }
        return arrayList;
    }

    private boolean a(char c2) {
        return c2 >= 0 && c2 < 128;
    }

    private int b(char[] cArr, int i2, int i3) {
        int i4 = i3;
        if (i4 > this.g) {
            return 0;
        }
        int i5 = i4 - 1;
        int i6 = this.i[i5];
        int i7 = i4 * 2;
        int i8 = i7 + 4;
        int i9 = this.h[i5] - 1;
        int i10 = 0;
        while (i10 <= i9) {
            int i11 = (i10 + i9) / 2;
            int i12 = i6 + (i8 * i11);
            int a2 = a(this.l, i12, cArr, i2, i3);
            if (a2 == 0) {
                return b.b(this.l, i12 + i7);
            }
            if (a2 > 0) {
                i10 = i11 + 1;
            } else {
                i9 = i11 - 1;
            }
        }
        return 0;
    }

    public boolean a(InputStream inputStream) {
        try {
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            dataInputStream.readInt();
            dataInputStream.readInt();
            this.f2166c = new double[4];
            for (int i2 = 0; i2 < 4; i2++) {
                this.f2166c[i2] = dataInputStream.readDouble();
            }
            this.f2167d = (double[][]) Array.newInstance(double.class, new int[]{4, 4});
            for (int i3 = 0; i3 < 4; i3++) {
                for (int i4 = 0; i4 < 4; i4++) {
                    this.f2167d[i3][i4] = dataInputStream.readDouble();
                }
            }
            this.e = new int[4];
            for (int i5 = 0; i5 < 4; i5++) {
                this.e[i5] = dataInputStream.readInt();
            }
            this.g = dataInputStream.readInt();
            this.h = new int[this.g];
            for (int i6 = 0; i6 < this.g; i6++) {
                this.h[i6] = dataInputStream.readInt();
            }
            this.j = dataInputStream.readInt();
            this.k = dataInputStream.readInt();
            Log.d("Dict", "mLengthCount:" + this.g + "\tmTotalFreq:" + this.j + "\tmMinFreq:" + this.k);
            this.f = new int[4];
            this.f[0] = 0;
            for (int i7 = 1; i7 < 4; i7++) {
                int i8 = i7 - 1;
                this.f[i7] = this.f[i8] + (this.e[i8] * 6);
            }
            this.i = new int[this.g];
            this.i[0] = this.f[3] + (this.e[3] * 6) + 0;
            for (int i9 = 1; i9 < this.g; i9++) {
                int i10 = i9 - 1;
                this.i[i9] = this.i[i10] + (this.h[i10] * ((i9 * 2) + 4));
            }
            int i11 = this.i[this.g - 1] + (this.h[this.g - 1] * ((this.g * 2) + 4));
            int available = inputStream.available();
            if (i11 != available) {
                Log.e("Dict", "The dict file is wrong, please make sure it's not be modified.");
                return false;
            }
            this.l = new byte[available];
            if (inputStream.read(this.l) != available) {
                Log.e("Dict", "Reading dict file failed, availabe size is not equal to realy read size.");
                return false;
            }
            dataInputStream.close();
            return true;
        } catch (IOException unused) {
            Log.e(f2164a, "IOException on load dict.");
            return false;
        }
    }

    public String[] a(String str) {
        int i2;
        ArrayList arrayList = new ArrayList();
        char[] charArray = str.toCharArray();
        int length = charArray.length;
        int i3 = length + 1;
        double[] dArr = new double[i3];
        dArr[length] = 0.0d;
        int[] iArr = new int[i3];
        int i4 = length - 1;
        for (int i5 = i4; i5 >= 0; i5--) {
            iArr[i5] = i5 + 1;
        }
        double log = Math.log((double) ((((float) this.k) * 1.0f) / ((float) this.j)));
        for (int i6 = i4; i6 >= 0; i6--) {
            int i7 = i6 + 1;
            dArr[i6] = (2.0d * log) + dArr[i7];
            while (i7 <= length) {
                int b2 = b(charArray, i6, i7 - i6);
                if (b2 != 0) {
                    double log2 = Math.log((double) ((((float) b2) * 1.0f) / ((float) this.j))) + dArr[i7];
                    if (log2 > dArr[i6]) {
                        iArr[i6] = i7;
                        dArr[i6] = log2;
                    }
                }
                i7++;
            }
        }
        int i8 = 0;
        while (i2 < length) {
            if (iArr[i2] == i2 + 1) {
                int i9 = i2;
                while (i9 < length) {
                    int i10 = i9 + 1;
                    if (iArr[i9] != i10) {
                        break;
                    }
                    i9 = i10;
                }
                while (i2 < i9) {
                    int i11 = i2;
                    while (i11 < i9 && !a(charArray[i11])) {
                        i11++;
                    }
                    if (i2 < i11) {
                        arrayList.addAll(a(charArray, i2, i11));
                    }
                    i2 = i11;
                    while (i2 < i9 && a(charArray[i2])) {
                        i2++;
                    }
                    if (i11 < i2 && i11 < i9) {
                        arrayList.add(new String(charArray, i11, i2 - i11));
                    }
                }
                i8 = i9;
            } else {
                arrayList.add(new String(charArray, i2, iArr[i2] - i2));
                i8 = iArr[i2];
            }
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    public boolean b(String str) {
        return b(str.toCharArray(), 0, str.length()) > 0;
    }
}
