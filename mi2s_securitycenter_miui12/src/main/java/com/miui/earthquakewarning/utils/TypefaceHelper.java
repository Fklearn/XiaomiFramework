package com.miui.earthquakewarning.utils;

import android.content.Context;
import android.graphics.Typeface;

public class TypefaceHelper {
    private static Typeface sMitypeNumber1Typeface;
    private static Typeface sMitypeNumber2Typeface;

    private TypefaceHelper() {
    }

    public static synchronized Typeface getMitypeNumber1Typeface(Context context) {
        Typeface typeface;
        synchronized (TypefaceHelper.class) {
            if (sMitypeNumber1Typeface == null) {
                sMitypeNumber1Typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Mitype2018-80.otf");
            }
            typeface = sMitypeNumber1Typeface;
        }
        return typeface;
    }

    public static synchronized Typeface getMitypeNumber2Typeface(Context context) {
        Typeface typeface;
        synchronized (TypefaceHelper.class) {
            if (sMitypeNumber2Typeface == null) {
                sMitypeNumber2Typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Mitype.otf");
            }
            typeface = sMitypeNumber2Typeface;
        }
        return typeface;
    }
}
