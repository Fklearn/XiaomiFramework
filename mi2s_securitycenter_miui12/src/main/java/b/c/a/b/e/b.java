package b.c.a.b.e;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import b.c.a.b.a.i;
import b.c.a.c.d;
import java.lang.reflect.Field;

public class b extends d {
    public b(ImageView imageView) {
        super(imageView);
    }

    private static int a(Object obj, String str) {
        try {
            Field declaredField = ImageView.class.getDeclaredField(str);
            declaredField.setAccessible(true);
            int intValue = ((Integer) declaredField.get(obj)).intValue();
            if (intValue <= 0 || intValue >= Integer.MAX_VALUE) {
                return 0;
            }
            return intValue;
        } catch (Exception e) {
            d.a((Throwable) e);
            return 0;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r1 = (android.widget.ImageView) r2.f2026a.get();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int a() {
        /*
            r2 = this;
            int r0 = super.a()
            if (r0 > 0) goto L_0x0016
            java.lang.ref.Reference<android.view.View> r1 = r2.f2026a
            java.lang.Object r1 = r1.get()
            android.widget.ImageView r1 = (android.widget.ImageView) r1
            if (r1 == 0) goto L_0x0016
            java.lang.String r0 = "mMaxHeight"
            int r0 = a((java.lang.Object) r1, (java.lang.String) r0)
        L_0x0016:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: b.c.a.b.e.b.a():int");
    }

    /* access modifiers changed from: protected */
    public void a(Bitmap bitmap, View view) {
        ((ImageView) view).setImageBitmap(bitmap);
    }

    /* access modifiers changed from: protected */
    public void a(Drawable drawable, View view) {
        ((ImageView) view).setImageDrawable(drawable);
        if (drawable instanceof AnimationDrawable) {
            ((AnimationDrawable) drawable).start();
        }
    }

    public ImageView b() {
        return (ImageView) super.b();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r1 = (android.widget.ImageView) r2.f2026a.get();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int c() {
        /*
            r2 = this;
            int r0 = super.c()
            if (r0 > 0) goto L_0x0016
            java.lang.ref.Reference<android.view.View> r1 = r2.f2026a
            java.lang.Object r1 = r1.get()
            android.widget.ImageView r1 = (android.widget.ImageView) r1
            if (r1 == 0) goto L_0x0016
            java.lang.String r0 = "mMaxWidth"
            int r0 = a((java.lang.Object) r1, (java.lang.String) r0)
        L_0x0016:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: b.c.a.b.e.b.c():int");
    }

    public i e() {
        ImageView imageView = (ImageView) this.f2026a.get();
        return imageView != null ? i.a(imageView) : super.e();
    }
}
