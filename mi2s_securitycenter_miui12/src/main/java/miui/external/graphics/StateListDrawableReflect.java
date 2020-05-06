package miui.external.graphics;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import miui.graphics.drawable.StateListDrawableCompat;

public class StateListDrawableReflect {
    private StateListDrawableReflect() {
    }

    @SuppressLint({"NewApi"})
    public static int getStateCount(StateListDrawable stateListDrawable) {
        return StateListDrawableCompat.getStateCount(stateListDrawable);
    }

    @SuppressLint({"NewApi"})
    public static Drawable getStateDrawable(StateListDrawable stateListDrawable, int i) {
        return StateListDrawableCompat.getStateDrawable(stateListDrawable, i);
    }

    @SuppressLint({"NewApi"})
    public static int[] getStateSet(StateListDrawable stateListDrawable, int i) {
        return StateListDrawableCompat.getStateSet(stateListDrawable, i);
    }
}
