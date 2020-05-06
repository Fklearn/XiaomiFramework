package b.c.a.a.a.a;

import android.graphics.Bitmap;
import b.c.a.c.c;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;

public abstract class a implements b.c.a.a.a.a {

    /* renamed from: a  reason: collision with root package name */
    public static final Bitmap.CompressFormat f1909a = Bitmap.CompressFormat.PNG;

    /* renamed from: b  reason: collision with root package name */
    protected final File f1910b;

    /* renamed from: c  reason: collision with root package name */
    protected final File f1911c;

    /* renamed from: d  reason: collision with root package name */
    protected final b.c.a.a.a.b.a f1912d;
    protected int e = 32768;
    protected Bitmap.CompressFormat f = f1909a;
    protected int g = 100;

    public a(File file, File file2, b.c.a.a.a.b.a aVar) {
        if (file == null) {
            throw new IllegalArgumentException("cacheDir argument must be not null");
        } else if (aVar != null) {
            this.f1910b = file;
            this.f1911c = file2;
            this.f1912d = aVar;
        } else {
            throw new IllegalArgumentException("fileNameGenerator argument must be not null");
        }
    }

    /* access modifiers changed from: protected */
    public File a(String str) {
        File file;
        String a2 = this.f1912d.a(str);
        File file2 = this.f1910b;
        if (!file2.exists() && !this.f1910b.mkdirs() && (file = this.f1911c) != null && (file.exists() || this.f1911c.mkdirs())) {
            file2 = this.f1911c;
        }
        return new File(file2, a2);
    }

    public boolean a(String str, Bitmap bitmap) {
        File a2 = a(str);
        File file = new File(a2.getAbsolutePath() + ".tmp");
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file), this.e);
        try {
            boolean compress = bitmap.compress(this.f, this.g, bufferedOutputStream);
            c.a((Closeable) bufferedOutputStream);
            if (compress && !file.renameTo(a2)) {
                compress = false;
            }
            if (!compress) {
                file.delete();
            }
            bitmap.recycle();
            return compress;
        } catch (Throwable th) {
            c.a((Closeable) bufferedOutputStream);
            file.delete();
            throw th;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0057  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean a(java.lang.String r6, java.io.InputStream r7, b.c.a.c.c.a r8) {
        /*
            r5 = this;
            java.io.File r6 = r5.a(r6)
            java.io.File r0 = new java.io.File
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = r6.getAbsolutePath()
            r1.append(r2)
            java.lang.String r2 = ".tmp"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            r1 = 0
            java.io.BufferedOutputStream r2 = new java.io.BufferedOutputStream     // Catch:{ all -> 0x004a }
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ all -> 0x004a }
            r3.<init>(r0)     // Catch:{ all -> 0x004a }
            int r4 = r5.e     // Catch:{ all -> 0x004a }
            r2.<init>(r3, r4)     // Catch:{ all -> 0x004a }
            int r3 = r5.e     // Catch:{ all -> 0x0045 }
            boolean r7 = b.c.a.c.c.a(r7, r2, r8, r3)     // Catch:{ all -> 0x0045 }
            b.c.a.c.c.a((java.io.Closeable) r2)     // Catch:{ all -> 0x0043 }
            if (r7 == 0) goto L_0x003d
            boolean r6 = r0.renameTo(r6)
            if (r6 != 0) goto L_0x003d
            r7 = r1
        L_0x003d:
            if (r7 != 0) goto L_0x0042
            r0.delete()
        L_0x0042:
            return r7
        L_0x0043:
            r8 = move-exception
            goto L_0x004c
        L_0x0045:
            r7 = move-exception
            b.c.a.c.c.a((java.io.Closeable) r2)     // Catch:{ all -> 0x004a }
            throw r7     // Catch:{ all -> 0x004a }
        L_0x004a:
            r8 = move-exception
            r7 = r1
        L_0x004c:
            if (r7 == 0) goto L_0x0055
            boolean r6 = r0.renameTo(r6)
            if (r6 != 0) goto L_0x0055
            r7 = r1
        L_0x0055:
            if (r7 != 0) goto L_0x005a
            r0.delete()
        L_0x005a:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: b.c.a.a.a.a.a.a(java.lang.String, java.io.InputStream, b.c.a.c.c$a):boolean");
    }

    public File get(String str) {
        return a(str);
    }
}
