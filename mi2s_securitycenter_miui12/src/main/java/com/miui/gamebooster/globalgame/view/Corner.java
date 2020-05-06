package com.miui.gamebooster.globalgame.view;

import android.support.annotation.Keep;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Keep
@Retention(RetentionPolicy.SOURCE)
public @interface Corner {
    public static final int BOTTOM_LEFT = 3;
    public static final int BOTTOM_RIGHT = 2;
    public static final int TOP_LEFT = 0;
    public static final int TOP_RIGHT = 1;
}
