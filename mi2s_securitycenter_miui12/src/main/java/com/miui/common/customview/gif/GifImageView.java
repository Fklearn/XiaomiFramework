package com.miui.common.customview.gif;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import java.io.InputStream;

public class GifImageView extends ImageView implements Runnable {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public a f3801a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Bitmap f3802b;

    /* renamed from: c  reason: collision with root package name */
    private final Handler f3803c = new Handler(Looper.getMainLooper());
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public boolean f3804d;
    /* access modifiers changed from: private */
    public boolean e;
    /* access modifiers changed from: private */
    public Thread f;
    private a g = null;
    private long h = -1;
    private int i;
    private int j;
    private final Runnable k = new e(this);
    private final Runnable l = new f(this);

    public interface a {
        Bitmap a(Bitmap bitmap);
    }

    public GifImageView(Context context) {
        super(context);
    }

    public GifImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x002e A[SYNTHETIC, Splitter:B:21:0x002e] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0033 A[Catch:{ IOException -> 0x0036 }] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x003a A[SYNTHETIC, Splitter:B:31:0x003a] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x003f A[Catch:{ IOException -> 0x0048 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private byte[] a(java.io.InputStream r7) {
        /*
            r6 = this;
            r0 = 0
            if (r7 == 0) goto L_0x0043
            java.io.ByteArrayOutputStream r1 = new java.io.ByteArrayOutputStream     // Catch:{ IOException -> 0x0037, all -> 0x0028 }
            r1.<init>()     // Catch:{ IOException -> 0x0037, all -> 0x0028 }
            r2 = 2048(0x800, float:2.87E-42)
            byte[] r2 = new byte[r2]     // Catch:{ IOException -> 0x0038, all -> 0x0026 }
        L_0x000c:
            r3 = 100
            r4 = 0
            int r3 = r7.read(r2, r4, r3)     // Catch:{ IOException -> 0x0038, all -> 0x0026 }
            if (r3 <= 0) goto L_0x0019
            r1.write(r2, r4, r3)     // Catch:{ IOException -> 0x0038, all -> 0x0026 }
            goto L_0x000c
        L_0x0019:
            byte[] r0 = r1.toByteArray()     // Catch:{ IOException -> 0x0038, all -> 0x0026 }
            if (r7 == 0) goto L_0x0022
            r7.close()     // Catch:{ IOException -> 0x0025 }
        L_0x0022:
            r1.close()     // Catch:{ IOException -> 0x0025 }
        L_0x0025:
            return r0
        L_0x0026:
            r0 = move-exception
            goto L_0x002c
        L_0x0028:
            r1 = move-exception
            r5 = r1
            r1 = r0
            r0 = r5
        L_0x002c:
            if (r7 == 0) goto L_0x0031
            r7.close()     // Catch:{ IOException -> 0x0036 }
        L_0x0031:
            if (r1 == 0) goto L_0x0036
            r1.close()     // Catch:{ IOException -> 0x0036 }
        L_0x0036:
            throw r0
        L_0x0037:
            r1 = r0
        L_0x0038:
            if (r7 == 0) goto L_0x003d
            r7.close()     // Catch:{ IOException -> 0x0048 }
        L_0x003d:
            if (r1 == 0) goto L_0x0048
            r1.close()     // Catch:{ IOException -> 0x0048 }
            goto L_0x0048
        L_0x0043:
            if (r7 == 0) goto L_0x0048
            r7.close()     // Catch:{ IOException -> 0x0048 }
        L_0x0048:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.common.customview.gif.GifImageView.a(java.io.InputStream):byte[]");
    }

    private boolean e() {
        return this.f3804d && this.f3801a != null && this.f == null;
    }

    public void a() {
        this.f3804d = false;
        this.e = true;
        d();
        this.f3803c.post(this.l);
    }

    public void b() {
        this.f3804d = false;
        this.e = true;
        d();
        setImageDrawable((Drawable) null);
        this.f3803c.post(this.l);
    }

    public void c() {
        this.f3804d = true;
        if (e()) {
            this.f = new Thread(this);
            this.f.start();
        }
    }

    public void d() {
        this.f3804d = false;
        Thread thread = this.f;
        if (thread != null) {
            thread.interrupt();
            this.f = null;
        }
    }

    public Bitmap getFirstFrame() {
        a aVar = this.f3801a;
        if (aVar != null) {
            return aVar.e();
        }
        return null;
    }

    public long getFramesDisplayDuration() {
        return this.h;
    }

    public int getGifHeight() {
        return this.f3801a.c();
    }

    public int getGifWidth() {
        return this.f3801a.f();
    }

    public a getOnFrameAvailable() {
        return this.g;
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x0062  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0082 A[EDGE_INSN: B:53:0x0082->B:40:0x0082 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r9 = this;
            java.lang.String r0 = "GifDecoderView"
            boolean r1 = r9.e
            if (r1 == 0) goto L_0x000e
            android.os.Handler r0 = r9.f3803c
            java.lang.Runnable r1 = r9.l
            r0.post(r1)
            return
        L_0x000e:
            com.miui.common.customview.gif.a r1 = r9.f3801a
            if (r1 != 0) goto L_0x0013
            return
        L_0x0013:
            int r1 = r1.b()
        L_0x0017:
            r2 = 0
        L_0x0018:
            if (r2 >= r1) goto L_0x0082
            boolean r3 = r9.f3804d
            if (r3 != 0) goto L_0x001f
            goto L_0x0082
        L_0x001f:
            r3 = 0
            long r5 = java.lang.System.nanoTime()     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0058, IllegalArgumentException -> 0x0056 }
            com.miui.common.customview.gif.a r7 = r9.f3801a     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0058, IllegalArgumentException -> 0x0056 }
            android.graphics.Bitmap r7 = r7.e()     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0058, IllegalArgumentException -> 0x0056 }
            r9.f3802b = r7     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0058, IllegalArgumentException -> 0x0056 }
            long r7 = java.lang.System.nanoTime()     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0058, IllegalArgumentException -> 0x0056 }
            long r7 = r7 - r5
            r5 = 1000000(0xf4240, double:4.940656E-318)
            long r5 = r7 / r5
            com.miui.common.customview.gif.GifImageView$a r7 = r9.g     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0054, IllegalArgumentException -> 0x0052 }
            if (r7 == 0) goto L_0x0045
            com.miui.common.customview.gif.GifImageView$a r7 = r9.g     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0054, IllegalArgumentException -> 0x0052 }
            android.graphics.Bitmap r8 = r9.f3802b     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0054, IllegalArgumentException -> 0x0052 }
            android.graphics.Bitmap r7 = r7.a(r8)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0054, IllegalArgumentException -> 0x0052 }
            r9.f3802b = r7     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0054, IllegalArgumentException -> 0x0052 }
        L_0x0045:
            boolean r7 = r9.f3804d     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0054, IllegalArgumentException -> 0x0052 }
            if (r7 != 0) goto L_0x004a
            goto L_0x0082
        L_0x004a:
            android.os.Handler r7 = r9.f3803c     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0054, IllegalArgumentException -> 0x0052 }
            java.lang.Runnable r8 = r9.k     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0054, IllegalArgumentException -> 0x0052 }
            r7.post(r8)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0054, IllegalArgumentException -> 0x0052 }
            goto L_0x005d
        L_0x0052:
            r7 = move-exception
            goto L_0x005a
        L_0x0054:
            r7 = move-exception
            goto L_0x005a
        L_0x0056:
            r7 = move-exception
            goto L_0x0059
        L_0x0058:
            r7 = move-exception
        L_0x0059:
            r5 = r3
        L_0x005a:
            android.util.Log.w(r0, r7)
        L_0x005d:
            boolean r7 = r9.f3804d
            if (r7 != 0) goto L_0x0062
            goto L_0x0082
        L_0x0062:
            com.miui.common.customview.gif.a r7 = r9.f3801a
            r7.a()
            com.miui.common.customview.gif.a r7 = r9.f3801a     // Catch:{ Exception -> 0x007f }
            int r7 = r7.d()     // Catch:{ Exception -> 0x007f }
            long r7 = (long) r7     // Catch:{ Exception -> 0x007f }
            long r7 = r7 - r5
            int r5 = (int) r7     // Catch:{ Exception -> 0x007f }
            if (r5 <= 0) goto L_0x007f
            long r6 = r9.h     // Catch:{ Exception -> 0x007f }
            int r3 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x007b
            long r3 = r9.h     // Catch:{ Exception -> 0x007f }
            goto L_0x007c
        L_0x007b:
            long r3 = (long) r5     // Catch:{ Exception -> 0x007f }
        L_0x007c:
            java.lang.Thread.sleep(r3)     // Catch:{ Exception -> 0x007f }
        L_0x007f:
            int r2 = r2 + 1
            goto L_0x0018
        L_0x0082:
            int r2 = r9.i
            int r2 = r2 + 1
            r9.i = r2
            boolean r2 = r9.f3804d
            if (r2 == 0) goto L_0x0094
            int r2 = r9.i
            int r3 = r9.j
            if (r2 < r3) goto L_0x0017
            if (r3 == 0) goto L_0x0017
        L_0x0094:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.common.customview.gif.GifImageView.run():void");
    }

    public void setBytes(byte[] bArr) {
        this.f3801a = new a();
        try {
            this.f3801a.a(bArr);
            this.f3801a.a();
            if (e()) {
                this.f = new Thread(this);
                this.f.start();
            }
        } catch (OutOfMemoryError e2) {
            this.f3801a = null;
            Log.e("GifDecoderView", e2.getMessage(), e2);
        } catch (Exception e3) {
            this.f3801a = null;
            e3.printStackTrace();
        }
    }

    public void setFramesDisplayDuration(long j2) {
        this.h = j2;
    }

    public void setOnFrameAvailable(a aVar) {
        this.g = aVar;
    }

    public void setRepeatCounts(int i2) {
        this.j = i2;
    }

    public void setStream(InputStream inputStream) {
        setBytes(a(inputStream));
    }
}
