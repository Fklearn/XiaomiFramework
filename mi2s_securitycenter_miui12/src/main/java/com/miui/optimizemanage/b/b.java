package com.miui.optimizemanage.b;

import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.xiaomi.stat.d;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import miui.util.IOUtils;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static final int[] f5868a = {32, 544, 32, 32, 32, 32, 32, 32, 32, 8224, 32, 8224, 32, 8224, 8224};

    /* renamed from: b  reason: collision with root package name */
    private static final int[] f5869b = {32, 4640, 32, 32, 32, 32, 32, 32, 32, 8224, 32, 8224, 32, 8224, 8224, 32, 32, 32, 32, 32, 32, 8224, 8224};

    /* renamed from: c  reason: collision with root package name */
    private static final int[] f5870c = {288, 8224, 8224, 8224, 8224, 8224, 8224, 8224};

    /* renamed from: d  reason: collision with root package name */
    private static final int[] f5871d = {16416, 16416, 16416};
    private static final Comparator<a> e = new a();
    private long A;
    private int B;
    private int C;
    private int D;
    private int E;
    private int F;
    private int G;
    private boolean H;
    private int[] I;
    private int[] J;
    private final ArrayList<a> K = new ArrayList<>();
    private final ArrayList<a> L = new ArrayList<>();
    private boolean M;
    private boolean N = true;
    private byte[] O = new byte[MpegAudioHeader.MAX_FRAME_SIZE_BYTES];
    private final long[] f = new long[4];
    private final long[] g = new long[4];
    private final String[] h = new String[7];
    private final long[] i = new long[7];
    private final long[] j = new long[7];
    private final float[] k = new float[3];
    private boolean l = false;
    private final boolean m;
    private long n;
    private float o = 0.0f;
    private float p = 0.0f;
    private float q = 0.0f;
    private long r;
    private long s;
    private long t;
    private long u;
    private long v;
    private long w;
    private long x;
    private long y;
    private long z;

    public static class a {
        public boolean A;

        /* renamed from: a  reason: collision with root package name */
        public final int f5872a;

        /* renamed from: b  reason: collision with root package name */
        public final int f5873b;

        /* renamed from: c  reason: collision with root package name */
        final String f5874c;

        /* renamed from: d  reason: collision with root package name */
        final String f5875d;
        final String e;
        final ArrayList<a> f;
        final ArrayList<a> g;
        public boolean h;
        public String i;
        public String j;
        public int k;
        public long l;
        public long m;
        public long n;
        public long o;
        public long p;
        public int q;
        public int r;
        public long s;
        public long t;
        public int u;
        public int v;
        public long w;
        public boolean x;
        public boolean y;
        public boolean z;

        /* JADX WARNING: Removed duplicated region for block: B:10:0x009c  */
        /* JADX WARNING: Removed duplicated region for block: B:9:0x007f  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        a(int r5, int r6, boolean r7) {
            /*
                r4 = this;
                r4.<init>()
                r4.f5872a = r5
                java.lang.String r5 = "stat"
                java.lang.String r0 = "task"
                java.lang.String r1 = "/proc"
                r2 = 0
                if (r6 >= 0) goto L_0x004d
                java.io.File r6 = new java.io.File
                int r3 = r4.f5872a
                java.lang.String r3 = java.lang.Integer.toString(r3)
                r6.<init>(r1, r3)
                java.io.File r1 = new java.io.File
                r1.<init>(r6, r5)
                java.lang.String r5 = r1.toString()
                r4.f5874c = r5
                java.io.File r5 = new java.io.File
                java.lang.String r1 = "cmdline"
                r5.<init>(r6, r1)
                java.lang.String r5 = r5.toString()
                r4.f5875d = r5
                java.io.File r5 = new java.io.File
                r5.<init>(r6, r0)
                java.lang.String r5 = r5.toString()
                r4.e = r5
                if (r7 == 0) goto L_0x0075
                java.util.ArrayList r5 = new java.util.ArrayList
                r5.<init>()
                r4.f = r5
                java.util.ArrayList r5 = new java.util.ArrayList
                r5.<init>()
                r4.g = r5
                goto L_0x0079
            L_0x004d:
                java.io.File r7 = new java.io.File
                java.lang.String r6 = java.lang.Integer.toString(r6)
                r7.<init>(r1, r6)
                java.io.File r6 = new java.io.File
                java.io.File r1 = new java.io.File
                r1.<init>(r7, r0)
                int r7 = r4.f5872a
                java.lang.String r7 = java.lang.Integer.toString(r7)
                r6.<init>(r1, r7)
                java.io.File r7 = new java.io.File
                r7.<init>(r6, r5)
                java.lang.String r5 = r7.toString()
                r4.f5874c = r5
                r4.f5875d = r2
                r4.e = r2
            L_0x0075:
                r4.f = r2
                r4.g = r2
            L_0x0079:
                int r5 = android.os.Build.VERSION.SDK_INT
                r6 = 23
                if (r5 <= r6) goto L_0x009c
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "/proc/"
                r5.append(r6)
                int r6 = r4.f5872a
                r5.append(r6)
                java.lang.String r6 = "/status"
                r5.append(r6)
                java.lang.String r5 = r5.toString()
                int r5 = com.miui.optimizemanage.b.d.b(r5)
                goto L_0x00a2
            L_0x009c:
                java.lang.String r5 = r4.f5874c
                int r5 = com.miui.optimizemanage.b.d.a(r5)
            L_0x00a2:
                r4.f5873b = r5
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.optimizemanage.b.b.a.<init>(int, int, boolean):void");
        }
    }

    public b(boolean z2) {
        this.m = z2;
        this.n = 10;
        try {
            int i2 = Class.forName("android.system.OsConstants").getField("_SC_CLK_TCK").getInt((Object) null);
            Object obj = Class.forName("libcore.io.Libcore").getField(d.l).get((Object) null);
            this.n = 1000 / ((Long) obj.getClass().getMethod("sysconf", new Class[]{Integer.TYPE}).invoke(obj, new Object[]{Integer.valueOf(i2)})).longValue();
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e2) {
            Log.e("ProcessCpuTracker", "ProcessCpuTracker", e2);
        }
    }

    private String a(String str, char c2) {
        FileInputStream fileInputStream;
        StrictMode.ThreadPolicy allowThreadDiskReads = StrictMode.allowThreadDiskReads();
        try {
            fileInputStream = new FileInputStream(str);
            try {
                int read = fileInputStream.read(this.O);
                fileInputStream.close();
                if (read > 0) {
                    int i2 = 0;
                    while (true) {
                        if (i2 >= read) {
                            break;
                        } else if (this.O[i2] == c2) {
                            break;
                        } else {
                            i2++;
                        }
                    }
                    String str2 = new String(this.O, 0, i2);
                    IOUtils.closeQuietly(fileInputStream);
                    StrictMode.setThreadPolicy(allowThreadDiskReads);
                    return str2;
                }
            } catch (FileNotFoundException | IOException unused) {
            } catch (Throwable th) {
                th = th;
                IOUtils.closeQuietly(fileInputStream);
                StrictMode.setThreadPolicy(allowThreadDiskReads);
                throw th;
            }
        } catch (FileNotFoundException | IOException unused2) {
            fileInputStream = null;
        } catch (Throwable th2) {
            th = th2;
            fileInputStream = null;
            IOUtils.closeQuietly(fileInputStream);
            StrictMode.setThreadPolicy(allowThreadDiskReads);
            throw th;
        }
        IOUtils.closeQuietly(fileInputStream);
        StrictMode.setThreadPolicy(allowThreadDiskReads);
        return null;
    }

    private void a(a aVar, String str) {
        String str2 = aVar.j;
        if (str2 == null || str2.equals("app_process") || aVar.j.equals("<pre-initialized>")) {
            String a2 = a(str, 0);
            if (a2 != null && a2.length() > 1) {
                int lastIndexOf = a2.lastIndexOf("/");
                if (lastIndexOf > 0 && lastIndexOf < a2.length() - 1) {
                    a2 = a2.substring(lastIndexOf + 1);
                }
                str2 = a2;
            }
            if (str2 == null) {
                str2 = aVar.i;
            }
        }
        String str3 = aVar.j;
        if (str3 == null || !str2.equals(str3)) {
            aVar.j = str2;
            aVar.k = a(aVar.j);
        }
    }

    /* JADX WARNING: type inference failed for: r10v0 */
    /* JADX WARNING: type inference failed for: r10v1, types: [boolean] */
    /* JADX WARNING: type inference failed for: r10v2 */
    private int[] a(String str, int i2, boolean z2, int[] iArr, ArrayList<a> arrayList) {
        int i3;
        int i4;
        char c2;
        long j2;
        long j3;
        long j4;
        long j5;
        int i5 = i2;
        ArrayList<a> arrayList2 = arrayList;
        int[] a2 = d.a(str, iArr);
        ? r10 = 0;
        int length = a2 == null ? 0 : a2.length;
        int size = arrayList.size();
        int i6 = 0;
        int i7 = 0;
        while (i7 < length) {
            int i8 = a2[i7];
            if (i8 < 0) {
                break;
            }
            a aVar = i6 < size ? arrayList2.get(i6) : null;
            if (aVar == null || aVar.f5872a != i8) {
                i3 = length;
                if (aVar == null || aVar.f5872a > i8) {
                    a aVar2 = new a(i8, i5, this.m);
                    arrayList2.add(i6, aVar2);
                    int i9 = i6 + 1;
                    size++;
                    String[] strArr = this.h;
                    long[] jArr = this.i;
                    aVar2.m = SystemClock.uptimeMillis();
                    if (d.a(aVar2.f5874c.toString(), f5869b, strArr, jArr, (float[]) null)) {
                        aVar2.l = jArr[6];
                        if (aVar2.f5873b != 0) {
                            c2 = 1;
                            aVar2.h = true;
                        } else {
                            c2 = 1;
                        }
                        aVar2.i = strArr[0];
                        aVar2.s = jArr[c2];
                        aVar2.t = jArr[2];
                        long j6 = jArr[3];
                        long j7 = this.n;
                        aVar2.o = j6 * j7;
                        aVar2.p = jArr[4] * j7;
                        aVar2.w = jArr[5] * j7;
                    } else {
                        Log.w("ProcessCpuTracker", "Skipping unknown process pid " + i8);
                        aVar2.i = "<unknown>";
                        aVar2.p = 0;
                        aVar2.o = 0;
                        aVar2.t = 0;
                        aVar2.s = 0;
                    }
                    if (i5 < 0) {
                        a(aVar2, aVar2.f5875d);
                        ArrayList<a> arrayList3 = aVar2.f;
                        if (arrayList3 != null) {
                            this.J = a(aVar2.e, i8, true, this.J, arrayList3);
                        }
                    } else if (aVar2.h) {
                        aVar2.j = aVar2.i;
                        aVar2.k = a(aVar2.j);
                    }
                    aVar2.q = 0;
                    aVar2.r = 0;
                    aVar2.u = 0;
                    aVar2.v = 0;
                    i4 = 1;
                    aVar2.z = true;
                    if (!z2 && aVar2.h) {
                        aVar2.y = true;
                    }
                    i6 = i9;
                    i7 += i4;
                    length = i3;
                    r10 = 0;
                } else {
                    aVar.q = 0;
                    aVar.r = 0;
                    aVar.u = 0;
                    aVar.v = 0;
                    aVar.A = true;
                    aVar.y = true;
                    arrayList2.remove(i6);
                    size--;
                    i7--;
                }
            } else {
                aVar.z = r10;
                aVar.y = r10;
                int i10 = i6 + 1;
                if (aVar.h) {
                    long uptimeMillis = SystemClock.uptimeMillis();
                    long[] jArr2 = this.f;
                    if (d.a(aVar.f5874c.toString(), f5868a, (String[]) null, jArr2, (float[]) null)) {
                        long j8 = uptimeMillis;
                        long j9 = jArr2[r10];
                        long j10 = jArr2[1];
                        long j11 = jArr2[2];
                        i3 = length;
                        long j12 = this.n;
                        long j13 = j10;
                        long j14 = j11 * j12;
                        long j15 = j12 * jArr2[3];
                        if (j14 == aVar.o && j15 == aVar.p) {
                            aVar.q = 0;
                            aVar.r = 0;
                            aVar.u = 0;
                            aVar.v = 0;
                            if (aVar.x) {
                                aVar.x = false;
                            }
                            i6 = i10;
                        } else {
                            if (!aVar.x) {
                                aVar.x = true;
                            }
                            if (i5 < 0) {
                                a(aVar, aVar.f5875d);
                                ArrayList<a> arrayList4 = aVar.f;
                                if (arrayList4 != null) {
                                    j2 = j14;
                                    j5 = j8;
                                    j4 = j9;
                                    j3 = j13;
                                    this.J = a(aVar.e, i8, false, this.J, arrayList4);
                                    long j16 = j5;
                                    aVar.n = j16 - aVar.m;
                                    aVar.m = j16;
                                    long j17 = j2;
                                    aVar.q = (int) (j17 - aVar.o);
                                    aVar.r = (int) (j15 - aVar.p);
                                    aVar.o = j17;
                                    aVar.p = j15;
                                    long j18 = j4;
                                    aVar.u = (int) (j18 - aVar.s);
                                    long j19 = j3;
                                    aVar.v = (int) (j19 - aVar.t);
                                    aVar.s = j18;
                                    aVar.t = j19;
                                    aVar.y = true;
                                    i6 = i10;
                                }
                            }
                            j2 = j14;
                            j5 = j8;
                            j4 = j9;
                            j3 = j13;
                            long j162 = j5;
                            aVar.n = j162 - aVar.m;
                            aVar.m = j162;
                            long j172 = j2;
                            aVar.q = (int) (j172 - aVar.o);
                            aVar.r = (int) (j15 - aVar.p);
                            aVar.o = j172;
                            aVar.p = j15;
                            long j182 = j4;
                            aVar.u = (int) (j182 - aVar.s);
                            long j192 = j3;
                            aVar.v = (int) (j192 - aVar.t);
                            aVar.s = j182;
                            aVar.t = j192;
                            aVar.y = true;
                            i6 = i10;
                        }
                    }
                }
                i3 = length;
                i6 = i10;
            }
            i4 = 1;
            i7 += i4;
            length = i3;
            r10 = 0;
        }
        while (i6 < size) {
            a aVar3 = arrayList2.get(i6);
            aVar3.q = 0;
            aVar3.r = 0;
            aVar3.u = 0;
            aVar3.v = 0;
            aVar3.A = true;
            aVar3.y = true;
            arrayList2.remove(i6);
            size--;
        }
        return a2;
    }

    public final int a() {
        return this.K.size();
    }

    public int a(String str) {
        return 0;
    }

    public final a a(int i2) {
        return this.K.get(i2);
    }

    public void a(float f2, float f3, float f4) {
    }

    public void b() {
        this.N = true;
        c();
    }

    /* JADX INFO: finally extract failed */
    public void c() {
        long j2;
        long j3;
        long uptimeMillis = SystemClock.uptimeMillis();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long[] jArr = this.j;
        if (d.a("/proc/stat", f5870c, (String[]) null, jArr, (float[]) null)) {
            long j4 = jArr[0] + jArr[1];
            long j5 = this.n;
            long j6 = j4 * j5;
            long j7 = jArr[2] * j5;
            long j8 = jArr[3] * j5;
            long j9 = jArr[4] * j5;
            j2 = elapsedRealtime;
            long j10 = jArr[5] * j5;
            long j11 = j5 * jArr[6];
            j3 = uptimeMillis;
            this.B = (int) (j6 - this.v);
            this.C = (int) (j7 - this.w);
            this.D = (int) (j9 - this.x);
            this.E = (int) (j10 - this.y);
            this.F = (int) (j11 - this.z);
            this.G = (int) (j8 - this.A);
            this.H = true;
            this.v = j6;
            this.w = j7;
            this.x = j9;
            this.y = j10;
            this.z = j11;
            this.A = j8;
        } else {
            j3 = uptimeMillis;
            j2 = elapsedRealtime;
        }
        this.s = this.r;
        this.r = j3;
        this.u = this.t;
        this.t = j2;
        StrictMode.ThreadPolicy allowThreadDiskReads = StrictMode.allowThreadDiskReads();
        try {
            this.I = a("/proc", -1, this.N, this.I, this.K);
            StrictMode.setThreadPolicy(allowThreadDiskReads);
            float[] fArr = this.k;
            if (this.l && d.a("/proc/loadavg", f5871d, (String[]) null, (long[]) null, fArr)) {
                float f2 = fArr[0];
                float f3 = fArr[1];
                float f4 = fArr[2];
                if (!(f2 == this.o && f3 == this.p && f4 == this.q)) {
                    this.o = f2;
                    this.p = f3;
                    this.q = f4;
                    a(f2, f3, f4);
                }
            }
            this.M = false;
            this.N = false;
        } catch (Throwable th) {
            StrictMode.setThreadPolicy(allowThreadDiskReads);
            throw th;
        }
    }
}
