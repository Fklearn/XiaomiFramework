package com.miui.common.card;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

public class FillParentDrawable extends LayerDrawable {
    public FillParentDrawable(Drawable drawable) {
        super(new Drawable[]{drawable});
    }

    public int getIntrinsicHeight() {
        return -1;
    }

    public int getIntrinsicWidth() {
        return -1;
    }
}
