package b.b.b.a;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

public class c extends LayerDrawable {
    public c(Drawable drawable) {
        super(new Drawable[]{drawable});
    }

    public int getIntrinsicHeight() {
        return -1;
    }

    public int getIntrinsicWidth() {
        return -1;
    }
}
