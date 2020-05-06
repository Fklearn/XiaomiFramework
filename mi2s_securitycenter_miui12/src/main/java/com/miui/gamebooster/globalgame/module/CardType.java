package com.miui.gamebooster.globalgame.module;

import android.support.annotation.Keep;

@Keep
public class CardType {
    public static final int H5_LIST = 8;
    public static final int H5_LIST_2Row = 10;
    public static final int H5_LIST_TORRENT = 11;
    public static final int HORIZONTAL_LIST = 1;
    public static final int POST_BIG = 3;
    public static final int POST_SMALL = 4;
    public static final int PURE_IMAGE = 5;
    public static final int PURE_TITLE = 9;
    public static final int USER_GUIDE = 0;
    public static final int VERTICAL_LIST = 2;

    @Keep
    public @interface Type {
    }
}
