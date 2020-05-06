package b.c.a.b.a;

import android.widget.ImageView;

public enum i {
    FIT_INSIDE,
    CROP;

    public static i a(ImageView imageView) {
        int i = h.f1983a[imageView.getScaleType().ordinal()];
        return (i == 1 || i == 2 || i == 3 || i == 4 || i == 5) ? FIT_INSIDE : CROP;
    }
}
