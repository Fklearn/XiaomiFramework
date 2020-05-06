package miui.external.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import d.b.b;
import miui.R;

public class TaggingDrawableUtil {
    private static final int[] STATES_TAGS = {R.attr.state_single_v, R.attr.state_first_v, R.attr.state_middle_v, R.attr.state_last_v};
    public static final int[] STATE_SET_FIRST = {R.attr.state_first_v};
    public static final int[] STATE_SET_LAST = {R.attr.state_last_v};
    public static final int[] STATE_SET_MIDDLE = {R.attr.state_middle_v};
    public static final int[] STATE_SET_SINGLE = {R.attr.state_single_v};

    public static void updateBackgroundState(View view, int i, int i2) {
        if (view != null && i2 != 0) {
            Drawable background = view.getBackground();
            if ((background instanceof StateListDrawable) && TaggingDrawable.containsTagState((StateListDrawable) background, STATES_TAGS)) {
                TaggingDrawable taggingDrawable = new TaggingDrawable(background);
                view.setBackground(taggingDrawable);
                background = taggingDrawable;
            }
            if (background instanceof TaggingDrawable) {
                ((TaggingDrawable) background).setTaggingState(i2 == 1 ? STATE_SET_SINGLE : i == 0 ? STATE_SET_FIRST : i == i2 - 1 ? STATE_SET_LAST : STATE_SET_MIDDLE);
            }
        }
    }

    public static void updateBackgroundState(View view, int[] iArr) {
        if (view != null) {
            Drawable background = view.getBackground();
            if ((background instanceof StateListDrawable) && TaggingDrawable.containsTagState((StateListDrawable) background, STATES_TAGS)) {
                TaggingDrawable taggingDrawable = new TaggingDrawable(background);
                view.setBackground(taggingDrawable);
                background = taggingDrawable;
            }
            if (background instanceof TaggingDrawable) {
                ((TaggingDrawable) background).setTaggingState(iArr);
            }
        }
    }

    public static void updateItemBackground(View view, int i, int i2) {
        updateBackgroundState(view, i, i2);
        updateItemPadding(view, i, i2);
    }

    public static void updateItemPadding(View view, int i, int i2) {
        int dimensionPixelSize;
        Resources resources;
        int i3;
        Resources resources2;
        int i4;
        if (view != null && i2 != 0) {
            int paddingStart = view.getPaddingStart();
            view.getPaddingTop();
            int paddingEnd = view.getPaddingEnd();
            view.getPaddingBottom();
            Context applicationContext = view.getContext().getApplicationContext();
            if (i2 != 1) {
                if (i == 0) {
                    resources2 = applicationContext.getResources();
                    i4 = b.miuix_compat_drop_down_menu_padding_large;
                    dimensionPixelSize = resources2.getDimensionPixelSize(i4);
                    resources = applicationContext.getResources();
                    i3 = b.miuix_compat_drop_down_menu_padding_small;
                    view.setPaddingRelative(paddingStart, dimensionPixelSize, paddingEnd, resources.getDimensionPixelSize(i3));
                } else if (i == i2 - 1) {
                    dimensionPixelSize = applicationContext.getResources().getDimensionPixelSize(b.miuix_compat_drop_down_menu_padding_small);
                    resources = applicationContext.getResources();
                    i3 = b.miuix_compat_drop_down_menu_padding_large;
                    view.setPaddingRelative(paddingStart, dimensionPixelSize, paddingEnd, resources.getDimensionPixelSize(i3));
                }
            }
            resources2 = applicationContext.getResources();
            i4 = b.miuix_compat_drop_down_menu_padding_small;
            dimensionPixelSize = resources2.getDimensionPixelSize(i4);
            resources = applicationContext.getResources();
            i3 = b.miuix_compat_drop_down_menu_padding_small;
            view.setPaddingRelative(paddingStart, dimensionPixelSize, paddingEnd, resources.getDimensionPixelSize(i3));
        }
    }
}
