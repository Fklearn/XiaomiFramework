package b.c.a.b.b;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import b.c.a.b.a.e;
import b.c.a.b.d.c;
import b.c.a.b.d.d;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class a implements b {

    /* renamed from: a  reason: collision with root package name */
    protected final boolean f1991a;

    /* renamed from: b.c.a.b.b.a$a  reason: collision with other inner class name */
    protected static class C0033a {

        /* renamed from: a  reason: collision with root package name */
        public final int f1992a;

        /* renamed from: b  reason: collision with root package name */
        public final boolean f1993b;

        protected C0033a() {
            this.f1992a = 0;
            this.f1993b = false;
        }

        protected C0033a(int i, boolean z) {
            this.f1992a = i;
            this.f1993b = z;
        }
    }

    protected static class b {

        /* renamed from: a  reason: collision with root package name */
        public final e f1994a;

        /* renamed from: b  reason: collision with root package name */
        public final C0033a f1995b;

        protected b(e eVar, C0033a aVar) {
            this.f1994a = eVar;
            this.f1995b = aVar;
        }
    }

    public a(boolean z) {
        this.f1991a = z;
    }

    private boolean a(String str, String str2) {
        return "image/jpeg".equalsIgnoreCase(str2) && d.a.b(str) == d.a.FILE;
    }

    /* access modifiers changed from: protected */
    public Bitmap a(Bitmap bitmap, c cVar, int i, boolean z) {
        Matrix matrix = new Matrix();
        b.c.a.b.a.d e = cVar.e();
        if (e == b.c.a.b.a.d.EXACTLY || e == b.c.a.b.a.d.EXACTLY_STRETCHED) {
            e eVar = new e(bitmap.getWidth(), bitmap.getHeight(), i);
            float b2 = b.c.a.c.b.b(eVar, cVar.g(), cVar.h(), e == b.c.a.b.a.d.EXACTLY_STRETCHED);
            if (Float.compare(b2, 1.0f) != 0) {
                matrix.setScale(b2, b2);
                if (this.f1991a) {
                    b.c.a.c.d.a("Scale subsampled image (%1$s) to %2$s (scale = %3$.5f) [%4$s]", eVar, eVar.a(b2), Float.valueOf(b2), cVar.d());
                }
            }
        }
        if (z) {
            matrix.postScale(-1.0f, 1.0f);
            if (this.f1991a) {
                b.c.a.c.d.a("Flip image horizontally [%s]", cVar.d());
            }
        }
        if (i != 0) {
            matrix.postRotate((float) i);
            if (this.f1991a) {
                b.c.a.c.d.a("Rotate image on %1$d° [%2$s]", Integer.valueOf(i), cVar.d());
            }
        }
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (createBitmap != bitmap) {
            bitmap.recycle();
        }
        return createBitmap;
    }

    public Bitmap a(c cVar) {
        InputStream b2 = b(cVar);
        if (b2 == null) {
            b.c.a.c.d.b("No stream for image [%s]", cVar.d());
            return null;
        }
        try {
            if (b2 instanceof c) {
                return ((c) b2).a();
            }
            b a2 = a(b2, cVar);
            InputStream b3 = b(b2, cVar);
            Bitmap decodeStream = BitmapFactory.decodeStream(b3, (Rect) null, a(a2.f1994a, cVar));
            b.c.a.c.c.a((Closeable) b3);
            if (decodeStream == null) {
                b.c.a.c.d.b("Image can't be decoded [%s]", cVar.d());
                return decodeStream;
            }
            C0033a aVar = a2.f1995b;
            return a(decodeStream, cVar, aVar.f1992a, aVar.f1993b);
        } finally {
            b.c.a.c.c.a((Closeable) b2);
        }
    }

    /* access modifiers changed from: protected */
    public BitmapFactory.Options a(e eVar, c cVar) {
        int i;
        b.c.a.b.a.d e = cVar.e();
        if (e == b.c.a.b.a.d.NONE) {
            i = 1;
        } else if (e == b.c.a.b.a.d.NONE_SAFE) {
            i = b.c.a.c.b.a(eVar);
        } else {
            i = b.c.a.c.b.a(eVar, cVar.g(), cVar.h(), e == b.c.a.b.a.d.IN_SAMPLE_POWER_OF_2);
        }
        if (i > 1 && this.f1991a) {
            b.c.a.c.d.a("Subsample original image (%1$s) to %2$s (scale = %3$d) [%4$s]", eVar, eVar.a(i), Integer.valueOf(i), cVar.d());
        }
        BitmapFactory.Options a2 = cVar.a();
        a2.inSampleSize = i;
        return a2;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: int} */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0022, code lost:
        r1 = r0;
        r0 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0018, code lost:
        r5 = 90;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x001c, code lost:
        r5 = 270;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0020, code lost:
        r5 = 180;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public b.c.a.b.b.a.C0033a a(java.lang.String r5) {
        /*
            r4 = this;
            r0 = 0
            r1 = 1
            android.media.ExifInterface r2 = new android.media.ExifInterface     // Catch:{ IOException -> 0x0025 }
            b.c.a.b.d.d$a r3 = b.c.a.b.d.d.a.FILE     // Catch:{ IOException -> 0x0025 }
            java.lang.String r3 = r3.a(r5)     // Catch:{ IOException -> 0x0025 }
            r2.<init>(r3)     // Catch:{ IOException -> 0x0025 }
            java.lang.String r3 = "Orientation"
            int r5 = r2.getAttributeInt(r3, r1)     // Catch:{ IOException -> 0x0025 }
            switch(r5) {
                case 1: goto L_0x002e;
                case 2: goto L_0x002f;
                case 3: goto L_0x0020;
                case 4: goto L_0x001f;
                case 5: goto L_0x001b;
                case 6: goto L_0x0018;
                case 7: goto L_0x0017;
                case 8: goto L_0x001c;
                default: goto L_0x0016;
            }
        L_0x0016:
            goto L_0x002e
        L_0x0017:
            r0 = r1
        L_0x0018:
            r5 = 90
            goto L_0x0022
        L_0x001b:
            r0 = r1
        L_0x001c:
            r5 = 270(0x10e, float:3.78E-43)
            goto L_0x0022
        L_0x001f:
            r0 = r1
        L_0x0020:
            r5 = 180(0xb4, float:2.52E-43)
        L_0x0022:
            r1 = r0
            r0 = r5
            goto L_0x002f
        L_0x0025:
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r0] = r5
            java.lang.String r5 = "Can't read EXIF tags from file [%s]"
            b.c.a.c.d.d(r5, r1)
        L_0x002e:
            r1 = r0
        L_0x002f:
            b.c.a.b.b.a$a r5 = new b.c.a.b.b.a$a
            r5.<init>(r0, r1)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: b.c.a.b.b.a.a(java.lang.String):b.c.a.b.b.a$a");
    }

    /* access modifiers changed from: protected */
    public b a(InputStream inputStream, c cVar) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, (Rect) null, options);
        String f = cVar.f();
        C0033a aVar = (!cVar.i() || !a(f, options.outMimeType)) ? new C0033a() : a(f);
        return new b(new e(options.outWidth, options.outHeight, aVar.f1992a), aVar);
    }

    /* access modifiers changed from: protected */
    public InputStream b(c cVar) {
        return cVar.b().a(cVar.f(), cVar.c());
    }

    /* access modifiers changed from: protected */
    public InputStream b(InputStream inputStream, c cVar) {
        if (inputStream.markSupported()) {
            try {
                inputStream.reset();
                return inputStream;
            } catch (IOException unused) {
            }
        }
        b.c.a.c.c.a((Closeable) inputStream);
        return b(cVar);
    }
}
