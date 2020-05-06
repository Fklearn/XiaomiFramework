package b.c.a.a.a.a.a;

import android.graphics.Bitmap;
import b.c.a.a.a.a;
import b.c.a.a.a.a.a.c;
import b.c.a.c.c;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class d implements a {

    /* renamed from: a  reason: collision with root package name */
    public static final Bitmap.CompressFormat f1931a = Bitmap.CompressFormat.PNG;

    /* renamed from: b  reason: collision with root package name */
    protected c f1932b;

    /* renamed from: c  reason: collision with root package name */
    private File f1933c;

    /* renamed from: d  reason: collision with root package name */
    protected final b.c.a.a.a.b.a f1934d;
    private final File e;
    protected int f = 32768;
    protected Bitmap.CompressFormat g = f1931a;
    protected int h = 100;

    public d(File file, File file2, b.c.a.a.a.b.a aVar, long j, int i, File file3) {
        if (file != null) {
            int i2 = (j > 0 ? 1 : (j == 0 ? 0 : -1));
            if (i2 < 0) {
                throw new IllegalArgumentException("cacheMaxSize argument must be positive number");
            } else if (i < 0) {
                throw new IllegalArgumentException("cacheMaxFileCount argument must be positive number");
            } else if (aVar != null) {
                long j2 = i2 == 0 ? Long.MAX_VALUE : j;
                i = i == 0 ? Integer.MAX_VALUE : i;
                this.f1933c = file2;
                this.f1934d = aVar;
                this.e = file3;
                a(file, file2, j2, i, file3);
            } else {
                throw new IllegalArgumentException("fileNameGenerator argument must be not null");
            }
        } else {
            throw new IllegalArgumentException("cacheDir argument must be not null");
        }
    }

    private String a(String str) {
        return this.f1934d.a(str);
    }

    private void a(File file, File file2, long j, int i, File file3) {
        try {
            this.f1932b = c.a(file, 1, 1, j, i, file3);
        } catch (IOException e2) {
            b.c.a.c.d.a((Throwable) e2);
            if (file2 != null) {
                a(file2, (File) null, j, i, file3);
            }
            if (this.f1932b == null) {
                throw e2;
            }
        }
    }

    public boolean a(String str, Bitmap bitmap) {
        c.a a2 = this.f1932b.a(a(str));
        if (a2 == null) {
            return false;
        }
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(a2.a(0), this.f);
        try {
            boolean compress = bitmap.compress(this.g, this.h, bufferedOutputStream);
            if (compress) {
                a2.b();
            } else {
                a2.a();
            }
            return compress;
        } finally {
            b.c.a.c.c.a((Closeable) bufferedOutputStream);
        }
    }

    public boolean a(String str, InputStream inputStream, c.a aVar) {
        c.a a2 = this.f1932b.a(a(str));
        if (a2 == null) {
            return false;
        }
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(a2.a(0), this.f);
        try {
            boolean a3 = b.c.a.c.c.a(inputStream, bufferedOutputStream, aVar, this.f);
            b.c.a.c.c.a((Closeable) bufferedOutputStream);
            if (a3) {
                a2.b();
            } else {
                a2.a();
            }
            return a3;
        } catch (Throwable th) {
            b.c.a.c.c.a((Closeable) bufferedOutputStream);
            a2.a();
            throw th;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: b.c.a.a.a.a.a.c$c} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: java.io.File} */
    /* JADX WARNING: type inference failed for: r0v0 */
    /* JADX WARNING: type inference failed for: r0v3 */
    /* JADX WARNING: type inference failed for: r0v6 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0024  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x002e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.io.File get(java.lang.String r4) {
        /*
            r3 = this;
            r0 = 0
            b.c.a.a.a.a.a.c r1 = r3.f1932b     // Catch:{ IOException -> 0x001d, all -> 0x001b }
            java.lang.String r4 = r3.a(r4)     // Catch:{ IOException -> 0x001d, all -> 0x001b }
            b.c.a.a.a.a.a.c$c r4 = r1.b((java.lang.String) r4)     // Catch:{ IOException -> 0x001d, all -> 0x001b }
            if (r4 != 0) goto L_0x000e
            goto L_0x0013
        L_0x000e:
            r1 = 0
            java.io.File r0 = r4.a(r1)     // Catch:{ IOException -> 0x0019 }
        L_0x0013:
            if (r4 == 0) goto L_0x0018
            r4.close()
        L_0x0018:
            return r0
        L_0x0019:
            r1 = move-exception
            goto L_0x001f
        L_0x001b:
            r4 = move-exception
            goto L_0x002c
        L_0x001d:
            r1 = move-exception
            r4 = r0
        L_0x001f:
            b.c.a.c.d.a((java.lang.Throwable) r1)     // Catch:{ all -> 0x0028 }
            if (r4 == 0) goto L_0x0027
            r4.close()
        L_0x0027:
            return r0
        L_0x0028:
            r0 = move-exception
            r2 = r0
            r0 = r4
            r4 = r2
        L_0x002c:
            if (r0 == 0) goto L_0x0031
            r0.close()
        L_0x0031:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: b.c.a.a.a.a.a.d.get(java.lang.String):java.io.File");
    }
}
