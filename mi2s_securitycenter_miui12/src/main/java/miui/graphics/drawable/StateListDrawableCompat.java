package miui.graphics.drawable;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

public class StateListDrawableCompat {
    public static int getStateCount(StateListDrawable stateListDrawable) {
        return stateListDrawable.getStateCount();
    }

    public static Drawable getStateDrawable(StateListDrawable stateListDrawable, int i) {
        return stateListDrawable.getStateDrawable(i);
    }

    public static int[] getStateSet(StateListDrawable stateListDrawable, int i) {
        return stateListDrawable.getStateSet(i);
    }
}
