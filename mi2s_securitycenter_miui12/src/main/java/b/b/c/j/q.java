package b.b.c.j;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import b.c.a.b.g.a;
import com.miui.securitycenter.Application;
import miui.content.res.IconCustomizer;

class q implements a {
    q() {
    }

    public Bitmap process(Bitmap bitmap) {
        synchronized (r.g) {
            Bitmap bitmap2 = IconCustomizer.generateIconStyleDrawable(new BitmapDrawable(Application.d().getResources(), Bitmap.createBitmap(bitmap))).getBitmap();
            return bitmap2 != null ? bitmap2 : bitmap;
        }
    }
}
