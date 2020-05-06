package b.c.a.a.a.a.a;

import com.miui.activityutil.o;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

final class c implements Closeable {

    /* renamed from: a  reason: collision with root package name */
    static final Pattern f1914a = Pattern.compile("[a-z0-9_-]{1,64}");
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public static final OutputStream f1915b = new b();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public final File f1916c;

    /* renamed from: d  reason: collision with root package name */
    private final File f1917d;
    private final File e;
    private final File f;
    private final int g;
    private long h;
    private int i;
    /* access modifiers changed from: private */
    public final int j;
    private long k = 0;
    private int l = 0;
    /* access modifiers changed from: private */
    public Writer m;
    private final LinkedHashMap<String, b> n = new LinkedHashMap<>(0, 0.75f, true);
    /* access modifiers changed from: private */
    public int o;
    private long p = 0;
    final ThreadPoolExecutor q = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
    private final Callable<Void> r = new a(this);

    public final class a {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public final b f1918a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public final boolean[] f1919b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public boolean f1920c;

        /* renamed from: d  reason: collision with root package name */
        private boolean f1921d;

        /* renamed from: b.c.a.a.a.a.a.c$a$a  reason: collision with other inner class name */
        private class C0030a extends FilterOutputStream {
            private C0030a(OutputStream outputStream) {
                super(outputStream);
            }

            /* synthetic */ C0030a(a aVar, OutputStream outputStream, a aVar2) {
                this(outputStream);
            }

            public void close() {
                try {
                    this.out.close();
                } catch (IOException unused) {
                    boolean unused2 = a.this.f1920c = true;
                }
            }

            public void flush() {
                try {
                    this.out.flush();
                } catch (IOException unused) {
                    boolean unused2 = a.this.f1920c = true;
                }
            }

            public void write(int i) {
                try {
                    this.out.write(i);
                } catch (IOException unused) {
                    boolean unused2 = a.this.f1920c = true;
                }
            }

            public void write(byte[] bArr, int i, int i2) {
                try {
                    this.out.write(bArr, i, i2);
                } catch (IOException unused) {
                    boolean unused2 = a.this.f1920c = true;
                }
            }
        }

        private a(b bVar) {
            this.f1918a = bVar;
            this.f1919b = bVar.f1925c ? null : new boolean[c.this.j];
        }

        /* synthetic */ a(c cVar, b bVar, a aVar) {
            this(bVar);
        }

        /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0024 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.io.OutputStream a(int r4) {
            /*
                r3 = this;
                b.c.a.a.a.a.a.c r0 = b.c.a.a.a.a.a.c.this
                monitor-enter(r0)
                b.c.a.a.a.a.a.c$b r1 = r3.f1918a     // Catch:{ all -> 0x0046 }
                b.c.a.a.a.a.a.c$a r1 = r1.f1926d     // Catch:{ all -> 0x0046 }
                if (r1 != r3) goto L_0x0040
                b.c.a.a.a.a.a.c$b r1 = r3.f1918a     // Catch:{ all -> 0x0046 }
                boolean r1 = r1.f1925c     // Catch:{ all -> 0x0046 }
                if (r1 != 0) goto L_0x0018
                boolean[] r1 = r3.f1919b     // Catch:{ all -> 0x0046 }
                r2 = 1
                r1[r4] = r2     // Catch:{ all -> 0x0046 }
            L_0x0018:
                b.c.a.a.a.a.a.c$b r1 = r3.f1918a     // Catch:{ all -> 0x0046 }
                java.io.File r4 = r1.b((int) r4)     // Catch:{ all -> 0x0046 }
                java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ FileNotFoundException -> 0x0024 }
                r1.<init>(r4)     // Catch:{ FileNotFoundException -> 0x0024 }
                goto L_0x0032
            L_0x0024:
                b.c.a.a.a.a.a.c r1 = b.c.a.a.a.a.a.c.this     // Catch:{ all -> 0x0046 }
                java.io.File r1 = r1.f1916c     // Catch:{ all -> 0x0046 }
                r1.mkdirs()     // Catch:{ all -> 0x0046 }
                java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ FileNotFoundException -> 0x003a }
                r1.<init>(r4)     // Catch:{ FileNotFoundException -> 0x003a }
            L_0x0032:
                b.c.a.a.a.a.a.c$a$a r4 = new b.c.a.a.a.a.a.c$a$a     // Catch:{ all -> 0x0046 }
                r2 = 0
                r4.<init>(r3, r1, r2)     // Catch:{ all -> 0x0046 }
                monitor-exit(r0)     // Catch:{ all -> 0x0046 }
                return r4
            L_0x003a:
                java.io.OutputStream r4 = b.c.a.a.a.a.a.c.f1915b     // Catch:{ all -> 0x0046 }
                monitor-exit(r0)     // Catch:{ all -> 0x0046 }
                return r4
            L_0x0040:
                java.lang.IllegalStateException r4 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0046 }
                r4.<init>()     // Catch:{ all -> 0x0046 }
                throw r4     // Catch:{ all -> 0x0046 }
            L_0x0046:
                r4 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0046 }
                throw r4
            */
            throw new UnsupportedOperationException("Method not decompiled: b.c.a.a.a.a.a.c.a.a(int):java.io.OutputStream");
        }

        public void a() {
            c.this.a(this, false);
        }

        public void b() {
            if (this.f1920c) {
                c.this.a(this, false);
                c.this.c(this.f1918a.f1923a);
            } else {
                c.this.a(this, true);
            }
            this.f1921d = true;
        }
    }

    private final class b {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public final String f1923a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public final long[] f1924b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public boolean f1925c;
        /* access modifiers changed from: private */

        /* renamed from: d  reason: collision with root package name */
        public a f1926d;
        /* access modifiers changed from: private */
        public long e;

        private b(String str) {
            this.f1923a = str;
            this.f1924b = new long[c.this.j];
        }

        /* synthetic */ b(c cVar, String str, a aVar) {
            this(str);
        }

        private IOException a(String[] strArr) {
            throw new IOException("unexpected journal line: " + Arrays.toString(strArr));
        }

        /* access modifiers changed from: private */
        public void b(String[] strArr) {
            if (strArr.length == c.this.j) {
                int i = 0;
                while (i < strArr.length) {
                    try {
                        this.f1924b[i] = Long.parseLong(strArr[i]);
                        i++;
                    } catch (NumberFormatException unused) {
                        a(strArr);
                        throw null;
                    }
                }
                return;
            }
            a(strArr);
            throw null;
        }

        public File a(int i) {
            File e2 = c.this.f1916c;
            return new File(e2, this.f1923a + "" + i);
        }

        public String a() {
            StringBuilder sb = new StringBuilder();
            for (long append : this.f1924b) {
                sb.append(' ');
                sb.append(append);
            }
            return sb.toString();
        }

        public File b(int i) {
            File e2 = c.this.f1916c;
            return new File(e2, this.f1923a + "" + i + ".tmp");
        }
    }

    /* renamed from: b.c.a.a.a.a.a.c$c  reason: collision with other inner class name */
    public final class C0031c implements Closeable {

        /* renamed from: a  reason: collision with root package name */
        private final String f1927a;

        /* renamed from: b  reason: collision with root package name */
        private final long f1928b;

        /* renamed from: c  reason: collision with root package name */
        private File[] f1929c;

        /* renamed from: d  reason: collision with root package name */
        private final InputStream[] f1930d;
        private final long[] e;

        private C0031c(String str, long j, File[] fileArr, InputStream[] inputStreamArr, long[] jArr) {
            this.f1927a = str;
            this.f1928b = j;
            this.f1929c = fileArr;
            this.f1930d = inputStreamArr;
            this.e = jArr;
        }

        /* synthetic */ C0031c(c cVar, String str, long j, File[] fileArr, InputStream[] inputStreamArr, long[] jArr, a aVar) {
            this(str, j, fileArr, inputStreamArr, jArr);
        }

        public File a(int i) {
            return this.f1929c[i];
        }

        public void close() {
            for (InputStream a2 : this.f1930d) {
                g.a((Closeable) a2);
            }
        }
    }

    private c(File file, int i2, int i3, long j2, int i4, File file2) {
        File file3 = file2;
        this.f1916c = file;
        this.g = i2;
        this.f1917d = new File(file3, "journal");
        this.e = new File(file3, "journal.tmp");
        this.f = new File(file3, "journal.bkp");
        this.j = i3;
        this.h = j2;
        this.i = i4;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0021, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized b.c.a.a.a.a.a.c.a a(java.lang.String r6, long r7) {
        /*
            r5 = this;
            monitor-enter(r5)
            r5.c()     // Catch:{ all -> 0x0061 }
            r5.e((java.lang.String) r6)     // Catch:{ all -> 0x0061 }
            java.util.LinkedHashMap<java.lang.String, b.c.a.a.a.a.a.c$b> r0 = r5.n     // Catch:{ all -> 0x0061 }
            java.lang.Object r0 = r0.get(r6)     // Catch:{ all -> 0x0061 }
            b.c.a.a.a.a.a.c$b r0 = (b.c.a.a.a.a.a.c.b) r0     // Catch:{ all -> 0x0061 }
            r1 = -1
            int r1 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            r2 = 0
            if (r1 == 0) goto L_0x0022
            if (r0 == 0) goto L_0x0020
            long r3 = r0.e     // Catch:{ all -> 0x0061 }
            int r7 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r7 == 0) goto L_0x0022
        L_0x0020:
            monitor-exit(r5)
            return r2
        L_0x0022:
            if (r0 != 0) goto L_0x002f
            b.c.a.a.a.a.a.c$b r0 = new b.c.a.a.a.a.a.c$b     // Catch:{ all -> 0x0061 }
            r0.<init>(r5, r6, r2)     // Catch:{ all -> 0x0061 }
            java.util.LinkedHashMap<java.lang.String, b.c.a.a.a.a.a.c$b> r7 = r5.n     // Catch:{ all -> 0x0061 }
            r7.put(r6, r0)     // Catch:{ all -> 0x0061 }
            goto L_0x0037
        L_0x002f:
            b.c.a.a.a.a.a.c$a r7 = r0.f1926d     // Catch:{ all -> 0x0061 }
            if (r7 == 0) goto L_0x0037
            monitor-exit(r5)
            return r2
        L_0x0037:
            b.c.a.a.a.a.a.c$a r7 = new b.c.a.a.a.a.a.c$a     // Catch:{ all -> 0x0061 }
            r7.<init>(r5, r0, r2)     // Catch:{ all -> 0x0061 }
            b.c.a.a.a.a.a.c.a unused = r0.f1926d = r7     // Catch:{ all -> 0x0061 }
            java.io.Writer r8 = r5.m     // Catch:{ all -> 0x0061 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0061 }
            r0.<init>()     // Catch:{ all -> 0x0061 }
            java.lang.String r1 = "DIRTY "
            r0.append(r1)     // Catch:{ all -> 0x0061 }
            r0.append(r6)     // Catch:{ all -> 0x0061 }
            r6 = 10
            r0.append(r6)     // Catch:{ all -> 0x0061 }
            java.lang.String r6 = r0.toString()     // Catch:{ all -> 0x0061 }
            r8.write(r6)     // Catch:{ all -> 0x0061 }
            java.io.Writer r6 = r5.m     // Catch:{ all -> 0x0061 }
            r6.flush()     // Catch:{ all -> 0x0061 }
            monitor-exit(r5)
            return r7
        L_0x0061:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: b.c.a.a.a.a.a.c.a(java.lang.String, long):b.c.a.a.a.a.a.c$a");
    }

    public static c a(File file, int i2, int i3, long j2, int i4, File file2) {
        File file3 = file;
        if (j2 <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        } else if (i4 <= 0) {
            throw new IllegalArgumentException("maxFileCount <= 0");
        } else if (i3 > 0) {
            File file4 = new File(file, "journal.bkp");
            if (file4.exists()) {
                File file5 = new File(file, "journal");
                if (file5.exists()) {
                    file4.delete();
                } else {
                    a(file4, file5, false);
                }
            }
            c cVar = new c(file, i2, i3, j2, i4, file2);
            if (cVar.f1917d.exists()) {
                try {
                    cVar.f();
                    cVar.e();
                    cVar.m = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cVar.f1917d, true), g.f1940a));
                    return cVar;
                } catch (IOException e2) {
                    PrintStream printStream = System.out;
                    printStream.println("DiskLruCache " + file + " is corrupt: " + e2.getMessage() + ", removing");
                    cVar.b();
                }
            }
            file.mkdirs();
            c cVar2 = new c(file, i2, i3, j2, i4, file2);
            cVar2.g();
            return cVar2;
        } else {
            throw new IllegalArgumentException("valueCount <= 0");
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0114, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void a(b.c.a.a.a.a.a.c.a r11, boolean r12) {
        /*
            r10 = this;
            monitor-enter(r10)
            b.c.a.a.a.a.a.c$b r0 = r11.f1918a     // Catch:{ all -> 0x011b }
            b.c.a.a.a.a.a.c$a r1 = r0.f1926d     // Catch:{ all -> 0x011b }
            if (r1 != r11) goto L_0x0115
            r1 = 0
            if (r12 == 0) goto L_0x004d
            boolean r2 = r0.f1925c     // Catch:{ all -> 0x011b }
            if (r2 != 0) goto L_0x004d
            r2 = r1
        L_0x0015:
            int r3 = r10.j     // Catch:{ all -> 0x011b }
            if (r2 >= r3) goto L_0x004d
            boolean[] r3 = r11.f1919b     // Catch:{ all -> 0x011b }
            boolean r3 = r3[r2]     // Catch:{ all -> 0x011b }
            if (r3 == 0) goto L_0x0033
            java.io.File r3 = r0.b((int) r2)     // Catch:{ all -> 0x011b }
            boolean r3 = r3.exists()     // Catch:{ all -> 0x011b }
            if (r3 != 0) goto L_0x0030
            r11.a()     // Catch:{ all -> 0x011b }
            monitor-exit(r10)
            return
        L_0x0030:
            int r2 = r2 + 1
            goto L_0x0015
        L_0x0033:
            r11.a()     // Catch:{ all -> 0x011b }
            java.lang.IllegalStateException r11 = new java.lang.IllegalStateException     // Catch:{ all -> 0x011b }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x011b }
            r12.<init>()     // Catch:{ all -> 0x011b }
            java.lang.String r0 = "Newly created entry didn't create value for index "
            r12.append(r0)     // Catch:{ all -> 0x011b }
            r12.append(r2)     // Catch:{ all -> 0x011b }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x011b }
            r11.<init>(r12)     // Catch:{ all -> 0x011b }
            throw r11     // Catch:{ all -> 0x011b }
        L_0x004d:
            int r11 = r10.j     // Catch:{ all -> 0x011b }
            r2 = 1
            if (r1 >= r11) goto L_0x0087
            java.io.File r11 = r0.b((int) r1)     // Catch:{ all -> 0x011b }
            if (r12 == 0) goto L_0x0081
            boolean r3 = r11.exists()     // Catch:{ all -> 0x011b }
            if (r3 == 0) goto L_0x0084
            java.io.File r3 = r0.a((int) r1)     // Catch:{ all -> 0x011b }
            r11.renameTo(r3)     // Catch:{ all -> 0x011b }
            long[] r11 = r0.f1924b     // Catch:{ all -> 0x011b }
            r4 = r11[r1]     // Catch:{ all -> 0x011b }
            long r6 = r3.length()     // Catch:{ all -> 0x011b }
            long[] r11 = r0.f1924b     // Catch:{ all -> 0x011b }
            r11[r1] = r6     // Catch:{ all -> 0x011b }
            long r8 = r10.k     // Catch:{ all -> 0x011b }
            long r8 = r8 - r4
            long r8 = r8 + r6
            r10.k = r8     // Catch:{ all -> 0x011b }
            int r11 = r10.l     // Catch:{ all -> 0x011b }
            int r11 = r11 + r2
            r10.l = r11     // Catch:{ all -> 0x011b }
            goto L_0x0084
        L_0x0081:
            a((java.io.File) r11)     // Catch:{ all -> 0x011b }
        L_0x0084:
            int r1 = r1 + 1
            goto L_0x004d
        L_0x0087:
            int r11 = r10.o     // Catch:{ all -> 0x011b }
            int r11 = r11 + r2
            r10.o = r11     // Catch:{ all -> 0x011b }
            r11 = 0
            b.c.a.a.a.a.a.c.a unused = r0.f1926d = r11     // Catch:{ all -> 0x011b }
            boolean r11 = r0.f1925c     // Catch:{ all -> 0x011b }
            r11 = r11 | r12
            r1 = 10
            if (r11 == 0) goto L_0x00cd
            boolean unused = r0.f1925c = r2     // Catch:{ all -> 0x011b }
            java.io.Writer r11 = r10.m     // Catch:{ all -> 0x011b }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x011b }
            r2.<init>()     // Catch:{ all -> 0x011b }
            java.lang.String r3 = "CLEAN "
            r2.append(r3)     // Catch:{ all -> 0x011b }
            java.lang.String r3 = r0.f1923a     // Catch:{ all -> 0x011b }
            r2.append(r3)     // Catch:{ all -> 0x011b }
            java.lang.String r3 = r0.a()     // Catch:{ all -> 0x011b }
            r2.append(r3)     // Catch:{ all -> 0x011b }
            r2.append(r1)     // Catch:{ all -> 0x011b }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x011b }
            r11.write(r1)     // Catch:{ all -> 0x011b }
            if (r12 == 0) goto L_0x00f3
            long r11 = r10.p     // Catch:{ all -> 0x011b }
            r1 = 1
            long r1 = r1 + r11
            r10.p = r1     // Catch:{ all -> 0x011b }
            long unused = r0.e = r11     // Catch:{ all -> 0x011b }
            goto L_0x00f3
        L_0x00cd:
            java.util.LinkedHashMap<java.lang.String, b.c.a.a.a.a.a.c$b> r11 = r10.n     // Catch:{ all -> 0x011b }
            java.lang.String r12 = r0.f1923a     // Catch:{ all -> 0x011b }
            r11.remove(r12)     // Catch:{ all -> 0x011b }
            java.io.Writer r11 = r10.m     // Catch:{ all -> 0x011b }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x011b }
            r12.<init>()     // Catch:{ all -> 0x011b }
            java.lang.String r2 = "REMOVE "
            r12.append(r2)     // Catch:{ all -> 0x011b }
            java.lang.String r0 = r0.f1923a     // Catch:{ all -> 0x011b }
            r12.append(r0)     // Catch:{ all -> 0x011b }
            r12.append(r1)     // Catch:{ all -> 0x011b }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x011b }
            r11.write(r12)     // Catch:{ all -> 0x011b }
        L_0x00f3:
            java.io.Writer r11 = r10.m     // Catch:{ all -> 0x011b }
            r11.flush()     // Catch:{ all -> 0x011b }
            long r11 = r10.k     // Catch:{ all -> 0x011b }
            long r0 = r10.h     // Catch:{ all -> 0x011b }
            int r11 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1))
            if (r11 > 0) goto L_0x010c
            int r11 = r10.l     // Catch:{ all -> 0x011b }
            int r12 = r10.i     // Catch:{ all -> 0x011b }
            if (r11 > r12) goto L_0x010c
            boolean r11 = r10.d()     // Catch:{ all -> 0x011b }
            if (r11 == 0) goto L_0x0113
        L_0x010c:
            java.util.concurrent.ThreadPoolExecutor r11 = r10.q     // Catch:{ all -> 0x011b }
            java.util.concurrent.Callable<java.lang.Void> r12 = r10.r     // Catch:{ all -> 0x011b }
            r11.submit(r12)     // Catch:{ all -> 0x011b }
        L_0x0113:
            monitor-exit(r10)
            return
        L_0x0115:
            java.lang.IllegalStateException r11 = new java.lang.IllegalStateException     // Catch:{ all -> 0x011b }
            r11.<init>()     // Catch:{ all -> 0x011b }
            throw r11     // Catch:{ all -> 0x011b }
        L_0x011b:
            r11 = move-exception
            monitor-exit(r10)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: b.c.a.a.a.a.a.c.a(b.c.a.a.a.a.a.c$a, boolean):void");
    }

    private static void a(File file) {
        if (file.exists() && !file.delete()) {
            throw new IOException();
        }
    }

    private static void a(File file, File file2, boolean z) {
        if (z) {
            a(file2);
        }
        if (!file.renameTo(file2)) {
            throw new IOException();
        }
    }

    private void c() {
        if (this.m == null) {
            throw new IllegalStateException("cache is closed");
        }
    }

    private void d(String str) {
        String str2;
        int indexOf = str.indexOf(32);
        if (indexOf != -1) {
            int i2 = indexOf + 1;
            int indexOf2 = str.indexOf(32, i2);
            if (indexOf2 == -1) {
                str2 = str.substring(i2);
                if (indexOf == 6 && str.startsWith("REMOVE")) {
                    this.n.remove(str2);
                    return;
                }
            } else {
                str2 = str.substring(i2, indexOf2);
            }
            b bVar = this.n.get(str2);
            if (bVar == null) {
                bVar = new b(this, str2, (a) null);
                this.n.put(str2, bVar);
            }
            if (indexOf2 != -1 && indexOf == 5 && str.startsWith("CLEAN")) {
                String[] split = str.substring(indexOf2 + 1).split(" ");
                boolean unused = bVar.f1925c = true;
                a unused2 = bVar.f1926d = null;
                bVar.b(split);
            } else if (indexOf2 == -1 && indexOf == 5 && str.startsWith("DIRTY")) {
                a unused3 = bVar.f1926d = new a(this, bVar, (a) null);
            } else if (indexOf2 != -1 || indexOf != 4 || !str.startsWith("READ")) {
                throw new IOException("unexpected journal line: " + str);
            }
        } else {
            throw new IOException("unexpected journal line: " + str);
        }
    }

    /* access modifiers changed from: private */
    public boolean d() {
        int i2 = this.o;
        return i2 >= 2000 && i2 >= this.n.size();
    }

    private void e() {
        a(this.e);
        Iterator<b> it = this.n.values().iterator();
        while (it.hasNext()) {
            b next = it.next();
            int i2 = 0;
            if (next.f1926d == null) {
                while (i2 < this.j) {
                    this.k += next.f1924b[i2];
                    this.l++;
                    i2++;
                }
            } else {
                a unused = next.f1926d = null;
                while (i2 < this.j) {
                    a(next.a(i2));
                    a(next.b(i2));
                    i2++;
                }
                it.remove();
            }
        }
    }

    private void e(String str) {
        if (!f1914a.matcher(str).matches()) {
            throw new IllegalArgumentException("keys must match regex [a-z0-9_-]{1,64}: \"" + str + "\"");
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(4:16|17|18|19) */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        r9.o = r0 - r9.n.size();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x006b, code lost:
        return;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:16:0x005f */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:20:0x006c=Splitter:B:20:0x006c, B:16:0x005f=Splitter:B:16:0x005f} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void f() {
        /*
            r9 = this;
            java.lang.String r0 = ", "
            b.c.a.a.a.a.a.f r1 = new b.c.a.a.a.a.a.f
            java.io.FileInputStream r2 = new java.io.FileInputStream
            java.io.File r3 = r9.f1917d
            r2.<init>(r3)
            java.nio.charset.Charset r3 = b.c.a.a.a.a.a.g.f1940a
            r1.<init>(r2, r3)
            java.lang.String r2 = r1.a()     // Catch:{ all -> 0x009a }
            java.lang.String r3 = r1.a()     // Catch:{ all -> 0x009a }
            java.lang.String r4 = r1.a()     // Catch:{ all -> 0x009a }
            java.lang.String r5 = r1.a()     // Catch:{ all -> 0x009a }
            java.lang.String r6 = r1.a()     // Catch:{ all -> 0x009a }
            java.lang.String r7 = "libcore.io.DiskLruCache"
            boolean r7 = r7.equals(r2)     // Catch:{ all -> 0x009a }
            if (r7 == 0) goto L_0x006c
            java.lang.String r7 = "1"
            boolean r7 = r7.equals(r3)     // Catch:{ all -> 0x009a }
            if (r7 == 0) goto L_0x006c
            int r7 = r9.g     // Catch:{ all -> 0x009a }
            java.lang.String r7 = java.lang.Integer.toString(r7)     // Catch:{ all -> 0x009a }
            boolean r4 = r7.equals(r4)     // Catch:{ all -> 0x009a }
            if (r4 == 0) goto L_0x006c
            int r4 = r9.j     // Catch:{ all -> 0x009a }
            java.lang.String r4 = java.lang.Integer.toString(r4)     // Catch:{ all -> 0x009a }
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x009a }
            if (r4 == 0) goto L_0x006c
            java.lang.String r4 = ""
            boolean r4 = r4.equals(r6)     // Catch:{ all -> 0x009a }
            if (r4 == 0) goto L_0x006c
            r0 = 0
        L_0x0055:
            java.lang.String r2 = r1.a()     // Catch:{ EOFException -> 0x005f }
            r9.d((java.lang.String) r2)     // Catch:{ EOFException -> 0x005f }
            int r0 = r0 + 1
            goto L_0x0055
        L_0x005f:
            java.util.LinkedHashMap<java.lang.String, b.c.a.a.a.a.a.c$b> r2 = r9.n     // Catch:{ all -> 0x009a }
            int r2 = r2.size()     // Catch:{ all -> 0x009a }
            int r0 = r0 - r2
            r9.o = r0     // Catch:{ all -> 0x009a }
            b.c.a.a.a.a.a.g.a((java.io.Closeable) r1)
            return
        L_0x006c:
            java.io.IOException r4 = new java.io.IOException     // Catch:{ all -> 0x009a }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x009a }
            r7.<init>()     // Catch:{ all -> 0x009a }
            java.lang.String r8 = "unexpected journal header: ["
            r7.append(r8)     // Catch:{ all -> 0x009a }
            r7.append(r2)     // Catch:{ all -> 0x009a }
            r7.append(r0)     // Catch:{ all -> 0x009a }
            r7.append(r3)     // Catch:{ all -> 0x009a }
            r7.append(r0)     // Catch:{ all -> 0x009a }
            r7.append(r5)     // Catch:{ all -> 0x009a }
            r7.append(r0)     // Catch:{ all -> 0x009a }
            r7.append(r6)     // Catch:{ all -> 0x009a }
            java.lang.String r0 = "]"
            r7.append(r0)     // Catch:{ all -> 0x009a }
            java.lang.String r0 = r7.toString()     // Catch:{ all -> 0x009a }
            r4.<init>(r0)     // Catch:{ all -> 0x009a }
            throw r4     // Catch:{ all -> 0x009a }
        L_0x009a:
            r0 = move-exception
            b.c.a.a.a.a.a.g.a((java.io.Closeable) r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: b.c.a.a.a.a.a.c.f():void");
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: private */
    public synchronized void g() {
        String str;
        if (this.m != null) {
            this.m.close();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.e), g.f1940a));
        try {
            bufferedWriter.write("libcore.io.DiskLruCache");
            bufferedWriter.write("\n");
            bufferedWriter.write(o.f2310b);
            bufferedWriter.write("\n");
            bufferedWriter.write(Integer.toString(this.g));
            bufferedWriter.write("\n");
            bufferedWriter.write(Integer.toString(this.j));
            bufferedWriter.write("\n");
            bufferedWriter.write("\n");
            for (b next : this.n.values()) {
                if (next.f1926d != null) {
                    str = "DIRTY " + next.f1923a + 10;
                } else {
                    str = "CLEAN " + next.f1923a + next.a() + 10;
                }
                bufferedWriter.write(str);
            }
            bufferedWriter.close();
            if (this.f1917d.exists()) {
                a(this.f1917d, this.f, true);
            }
            a(this.e, this.f1917d, false);
            this.f.delete();
            this.m = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.f1917d, true), g.f1940a));
        } catch (Throwable th) {
            bufferedWriter.close();
            throw th;
        }
    }

    /* access modifiers changed from: private */
    public void h() {
        while (this.l > this.i) {
            c((String) this.n.entrySet().iterator().next().getKey());
        }
    }

    /* access modifiers changed from: private */
    public void i() {
        while (this.k > this.h) {
            c((String) this.n.entrySet().iterator().next().getKey());
        }
    }

    public a a(String str) {
        return a(str, -1);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(4:32|33|28|27) */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        r12.o++;
        r12.m.append("READ " + r13 + 10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x005f, code lost:
        if (d() == false) goto L_0x0068;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0061, code lost:
        r12.q.submit(r12.r);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x007a, code lost:
        return new b.c.a.a.a.a.a.c.C0031c(r12, r13, b.c.a.a.a.a.a.c.b.c(r0), r8, r9, b.c.a.a.a.a.a.c.b.a(r0), (b.c.a.a.a.a.a.a) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x008c, code lost:
        return null;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x007b */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0083  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized b.c.a.a.a.a.a.c.C0031c b(java.lang.String r13) {
        /*
            r12 = this;
            monitor-enter(r12)
            r12.c()     // Catch:{ all -> 0x008d }
            r12.e((java.lang.String) r13)     // Catch:{ all -> 0x008d }
            java.util.LinkedHashMap<java.lang.String, b.c.a.a.a.a.a.c$b> r0 = r12.n     // Catch:{ all -> 0x008d }
            java.lang.Object r0 = r0.get(r13)     // Catch:{ all -> 0x008d }
            b.c.a.a.a.a.a.c$b r0 = (b.c.a.a.a.a.a.c.b) r0     // Catch:{ all -> 0x008d }
            r1 = 0
            if (r0 != 0) goto L_0x0014
            monitor-exit(r12)
            return r1
        L_0x0014:
            boolean r2 = r0.f1925c     // Catch:{ all -> 0x008d }
            if (r2 != 0) goto L_0x001c
            monitor-exit(r12)
            return r1
        L_0x001c:
            int r2 = r12.j     // Catch:{ all -> 0x008d }
            java.io.File[] r8 = new java.io.File[r2]     // Catch:{ all -> 0x008d }
            int r2 = r12.j     // Catch:{ all -> 0x008d }
            java.io.InputStream[] r9 = new java.io.InputStream[r2]     // Catch:{ all -> 0x008d }
            r2 = 0
            r3 = r2
        L_0x0026:
            int r4 = r12.j     // Catch:{ FileNotFoundException -> 0x007b }
            if (r3 >= r4) goto L_0x003a
            java.io.File r4 = r0.a((int) r3)     // Catch:{ FileNotFoundException -> 0x007b }
            r8[r3] = r4     // Catch:{ FileNotFoundException -> 0x007b }
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x007b }
            r5.<init>(r4)     // Catch:{ FileNotFoundException -> 0x007b }
            r9[r3] = r5     // Catch:{ FileNotFoundException -> 0x007b }
            int r3 = r3 + 1
            goto L_0x0026
        L_0x003a:
            int r1 = r12.o     // Catch:{ all -> 0x008d }
            int r1 = r1 + 1
            r12.o = r1     // Catch:{ all -> 0x008d }
            java.io.Writer r1 = r12.m     // Catch:{ all -> 0x008d }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x008d }
            r2.<init>()     // Catch:{ all -> 0x008d }
            java.lang.String r3 = "READ "
            r2.append(r3)     // Catch:{ all -> 0x008d }
            r2.append(r13)     // Catch:{ all -> 0x008d }
            r3 = 10
            r2.append(r3)     // Catch:{ all -> 0x008d }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x008d }
            r1.append(r2)     // Catch:{ all -> 0x008d }
            boolean r1 = r12.d()     // Catch:{ all -> 0x008d }
            if (r1 == 0) goto L_0x0068
            java.util.concurrent.ThreadPoolExecutor r1 = r12.q     // Catch:{ all -> 0x008d }
            java.util.concurrent.Callable<java.lang.Void> r2 = r12.r     // Catch:{ all -> 0x008d }
            r1.submit(r2)     // Catch:{ all -> 0x008d }
        L_0x0068:
            b.c.a.a.a.a.a.c$c r1 = new b.c.a.a.a.a.a.c$c     // Catch:{ all -> 0x008d }
            long r6 = r0.e     // Catch:{ all -> 0x008d }
            long[] r10 = r0.f1924b     // Catch:{ all -> 0x008d }
            r11 = 0
            r3 = r1
            r4 = r12
            r5 = r13
            r3.<init>(r4, r5, r6, r8, r9, r10, r11)     // Catch:{ all -> 0x008d }
            monitor-exit(r12)
            return r1
        L_0x007b:
            int r13 = r12.j     // Catch:{ all -> 0x008d }
            if (r2 >= r13) goto L_0x008b
            r13 = r9[r2]     // Catch:{ all -> 0x008d }
            if (r13 == 0) goto L_0x008b
            r13 = r9[r2]     // Catch:{ all -> 0x008d }
            b.c.a.a.a.a.a.g.a((java.io.Closeable) r13)     // Catch:{ all -> 0x008d }
            int r2 = r2 + 1
            goto L_0x007b
        L_0x008b:
            monitor-exit(r12)
            return r1
        L_0x008d:
            r13 = move-exception
            monitor-exit(r12)
            throw r13
        */
        throw new UnsupportedOperationException("Method not decompiled: b.c.a.a.a.a.a.c.b(java.lang.String):b.c.a.a.a.a.a.c$c");
    }

    public void b() {
        close();
        g.a(this.f1916c);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0095, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0097, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean c(java.lang.String r9) {
        /*
            r8 = this;
            monitor-enter(r8)
            r8.c()     // Catch:{ all -> 0x0098 }
            r8.e((java.lang.String) r9)     // Catch:{ all -> 0x0098 }
            java.util.LinkedHashMap<java.lang.String, b.c.a.a.a.a.a.c$b> r0 = r8.n     // Catch:{ all -> 0x0098 }
            java.lang.Object r0 = r0.get(r9)     // Catch:{ all -> 0x0098 }
            b.c.a.a.a.a.a.c$b r0 = (b.c.a.a.a.a.a.c.b) r0     // Catch:{ all -> 0x0098 }
            r1 = 0
            if (r0 == 0) goto L_0x0096
            b.c.a.a.a.a.a.c$a r2 = r0.f1926d     // Catch:{ all -> 0x0098 }
            if (r2 == 0) goto L_0x001a
            goto L_0x0096
        L_0x001a:
            int r2 = r8.j     // Catch:{ all -> 0x0098 }
            r3 = 1
            if (r1 >= r2) goto L_0x0062
            java.io.File r2 = r0.a((int) r1)     // Catch:{ all -> 0x0098 }
            boolean r4 = r2.exists()     // Catch:{ all -> 0x0098 }
            if (r4 == 0) goto L_0x0047
            boolean r4 = r2.delete()     // Catch:{ all -> 0x0098 }
            if (r4 == 0) goto L_0x0030
            goto L_0x0047
        L_0x0030:
            java.io.IOException r9 = new java.io.IOException     // Catch:{ all -> 0x0098 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0098 }
            r0.<init>()     // Catch:{ all -> 0x0098 }
            java.lang.String r1 = "failed to delete "
            r0.append(r1)     // Catch:{ all -> 0x0098 }
            r0.append(r2)     // Catch:{ all -> 0x0098 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0098 }
            r9.<init>(r0)     // Catch:{ all -> 0x0098 }
            throw r9     // Catch:{ all -> 0x0098 }
        L_0x0047:
            long r4 = r8.k     // Catch:{ all -> 0x0098 }
            long[] r2 = r0.f1924b     // Catch:{ all -> 0x0098 }
            r6 = r2[r1]     // Catch:{ all -> 0x0098 }
            long r4 = r4 - r6
            r8.k = r4     // Catch:{ all -> 0x0098 }
            int r2 = r8.l     // Catch:{ all -> 0x0098 }
            int r2 = r2 - r3
            r8.l = r2     // Catch:{ all -> 0x0098 }
            long[] r2 = r0.f1924b     // Catch:{ all -> 0x0098 }
            r3 = 0
            r2[r1] = r3     // Catch:{ all -> 0x0098 }
            int r1 = r1 + 1
            goto L_0x001a
        L_0x0062:
            int r0 = r8.o     // Catch:{ all -> 0x0098 }
            int r0 = r0 + r3
            r8.o = r0     // Catch:{ all -> 0x0098 }
            java.io.Writer r0 = r8.m     // Catch:{ all -> 0x0098 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0098 }
            r1.<init>()     // Catch:{ all -> 0x0098 }
            java.lang.String r2 = "REMOVE "
            r1.append(r2)     // Catch:{ all -> 0x0098 }
            r1.append(r9)     // Catch:{ all -> 0x0098 }
            r2 = 10
            r1.append(r2)     // Catch:{ all -> 0x0098 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0098 }
            r0.append(r1)     // Catch:{ all -> 0x0098 }
            java.util.LinkedHashMap<java.lang.String, b.c.a.a.a.a.a.c$b> r0 = r8.n     // Catch:{ all -> 0x0098 }
            r0.remove(r9)     // Catch:{ all -> 0x0098 }
            boolean r9 = r8.d()     // Catch:{ all -> 0x0098 }
            if (r9 == 0) goto L_0x0094
            java.util.concurrent.ThreadPoolExecutor r9 = r8.q     // Catch:{ all -> 0x0098 }
            java.util.concurrent.Callable<java.lang.Void> r0 = r8.r     // Catch:{ all -> 0x0098 }
            r9.submit(r0)     // Catch:{ all -> 0x0098 }
        L_0x0094:
            monitor-exit(r8)
            return r3
        L_0x0096:
            monitor-exit(r8)
            return r1
        L_0x0098:
            r9 = move-exception
            monitor-exit(r8)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: b.c.a.a.a.a.a.c.c(java.lang.String):boolean");
    }

    public synchronized void close() {
        if (this.m != null) {
            Iterator it = new ArrayList(this.n.values()).iterator();
            while (it.hasNext()) {
                b bVar = (b) it.next();
                if (bVar.f1926d != null) {
                    bVar.f1926d.a();
                }
            }
            i();
            h();
            this.m.close();
            this.m = null;
        }
    }
}
