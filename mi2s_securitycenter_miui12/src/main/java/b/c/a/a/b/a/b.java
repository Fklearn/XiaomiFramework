package b.c.a.a.b.a;

import android.graphics.Bitmap;
import b.c.a.a.b.a;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class b implements a {

    /* renamed from: a  reason: collision with root package name */
    private final LinkedHashMap<String, Bitmap> f1944a;

    /* renamed from: b  reason: collision with root package name */
    private final int f1945b;

    /* renamed from: c  reason: collision with root package name */
    private int f1946c;

    public b(int i) {
        if (i > 0) {
            this.f1945b = i;
            this.f1944a = new LinkedHashMap<>(0, 0.75f, true);
            return;
        }
        throw new IllegalArgumentException("maxSize <= 0");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x006d, code lost:
        throw new java.lang.IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(int r4) {
        /*
            r3 = this;
        L_0x0000:
            monitor-enter(r3)
            int r0 = r3.f1946c     // Catch:{ all -> 0x006e }
            if (r0 < 0) goto L_0x004f
            java.util.LinkedHashMap<java.lang.String, android.graphics.Bitmap> r0 = r3.f1944a     // Catch:{ all -> 0x006e }
            boolean r0 = r0.isEmpty()     // Catch:{ all -> 0x006e }
            if (r0 == 0) goto L_0x0011
            int r0 = r3.f1946c     // Catch:{ all -> 0x006e }
            if (r0 != 0) goto L_0x004f
        L_0x0011:
            int r0 = r3.f1946c     // Catch:{ all -> 0x006e }
            if (r0 <= r4) goto L_0x0030
            java.util.LinkedHashMap<java.lang.String, android.graphics.Bitmap> r0 = r3.f1944a     // Catch:{ all -> 0x006e }
            boolean r0 = r0.isEmpty()     // Catch:{ all -> 0x006e }
            if (r0 == 0) goto L_0x001e
            goto L_0x0030
        L_0x001e:
            java.util.LinkedHashMap<java.lang.String, android.graphics.Bitmap> r0 = r3.f1944a     // Catch:{ all -> 0x006e }
            java.util.Set r0 = r0.entrySet()     // Catch:{ all -> 0x006e }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x006e }
            java.lang.Object r0 = r0.next()     // Catch:{ all -> 0x006e }
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0     // Catch:{ all -> 0x006e }
            if (r0 != 0) goto L_0x0032
        L_0x0030:
            monitor-exit(r3)     // Catch:{ all -> 0x006e }
            goto L_0x004e
        L_0x0032:
            java.lang.Object r1 = r0.getKey()     // Catch:{ all -> 0x006e }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ all -> 0x006e }
            java.lang.Object r0 = r0.getValue()     // Catch:{ all -> 0x006e }
            android.graphics.Bitmap r0 = (android.graphics.Bitmap) r0     // Catch:{ all -> 0x006e }
            java.util.LinkedHashMap<java.lang.String, android.graphics.Bitmap> r2 = r3.f1944a     // Catch:{ all -> 0x006e }
            r2.remove(r1)     // Catch:{ all -> 0x006e }
            int r2 = r3.f1946c     // Catch:{ all -> 0x006e }
            int r0 = r3.b(r1, r0)     // Catch:{ all -> 0x006e }
            int r2 = r2 - r0
            r3.f1946c = r2     // Catch:{ all -> 0x006e }
            monitor-exit(r3)     // Catch:{ all -> 0x006e }
            goto L_0x0000
        L_0x004e:
            return
        L_0x004f:
            java.lang.IllegalStateException r4 = new java.lang.IllegalStateException     // Catch:{ all -> 0x006e }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x006e }
            r0.<init>()     // Catch:{ all -> 0x006e }
            java.lang.Class r1 = r3.getClass()     // Catch:{ all -> 0x006e }
            java.lang.String r1 = r1.getName()     // Catch:{ all -> 0x006e }
            r0.append(r1)     // Catch:{ all -> 0x006e }
            java.lang.String r1 = ".sizeOf() is reporting inconsistent results!"
            r0.append(r1)     // Catch:{ all -> 0x006e }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x006e }
            r4.<init>(r0)     // Catch:{ all -> 0x006e }
            throw r4     // Catch:{ all -> 0x006e }
        L_0x006e:
            r4 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x006e }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: b.c.a.a.b.a.b.a(int):void");
    }

    private int b(String str, Bitmap bitmap) {
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public Collection<String> a() {
        HashSet hashSet;
        synchronized (this) {
            hashSet = new HashSet(this.f1944a.keySet());
        }
        return hashSet;
    }

    public final boolean a(String str, Bitmap bitmap) {
        if (str == null || bitmap == null) {
            throw new NullPointerException("key == null || value == null");
        }
        synchronized (this) {
            this.f1946c += b(str, bitmap);
            Bitmap bitmap2 = (Bitmap) this.f1944a.put(str, bitmap);
            if (bitmap2 != null) {
                this.f1946c -= b(str, bitmap2);
            }
        }
        a(this.f1945b);
        return true;
    }

    public final Bitmap get(String str) {
        Bitmap bitmap;
        if (str != null) {
            synchronized (this) {
                bitmap = this.f1944a.get(str);
            }
            return bitmap;
        }
        throw new NullPointerException("key == null");
    }

    public final Bitmap remove(String str) {
        Bitmap bitmap;
        if (str != null) {
            synchronized (this) {
                bitmap = (Bitmap) this.f1944a.remove(str);
                if (bitmap != null) {
                    this.f1946c -= b(str, bitmap);
                }
            }
            return bitmap;
        }
        throw new NullPointerException("key == null");
    }

    public final synchronized String toString() {
        return String.format("LruCache[maxSize=%d]", new Object[]{Integer.valueOf(this.f1945b)});
    }
}
