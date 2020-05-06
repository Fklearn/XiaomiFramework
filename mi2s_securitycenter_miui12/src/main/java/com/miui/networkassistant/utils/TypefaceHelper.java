package com.miui.networkassistant.utils;

import android.content.Context;
import android.graphics.Typeface;

public class TypefaceHelper {
    private static Typeface sMiuiBoldTypeface;
    private static Typeface sMiuiDemiBoldTypeface;
    private static Typeface sMiuiLightTypeface;
    private static Typeface sMiuiNumTypefaceForNA;
    private static Typeface sMiuiNumTypefaceSemiBold;
    private static Typeface sMiuiRegularTypeface;
    private static Typeface sMiuiThinTypeface;
    private static Typeface sRobotoBoldCondensed;

    public static synchronized Typeface getMiuiBoldTypeface(Context context) {
        Typeface typeface;
        synchronized (TypefaceHelper.class) {
            if (sMiuiBoldTypeface == null) {
                sMiuiBoldTypeface = Typeface.create("miuiex-bold", 0);
            }
            typeface = sMiuiBoldTypeface;
        }
        return typeface;
    }

    public static synchronized Typeface getMiuiDemiBoldCondensed(Context context) {
        Typeface typeface;
        synchronized (TypefaceHelper.class) {
            if (sMiuiDemiBoldTypeface == null) {
                sMiuiDemiBoldTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Mitype-DemiBold.otf");
            }
            typeface = sMiuiDemiBoldTypeface;
        }
        return typeface;
    }

    public static synchronized Typeface getMiuiLightTypeface(Context context) {
        Typeface typeface;
        synchronized (TypefaceHelper.class) {
            if (sMiuiLightTypeface == null) {
                sMiuiLightTypeface = Typeface.create("miuiex-light", 0);
            }
            typeface = sMiuiLightTypeface;
        }
        return typeface;
    }

    public static synchronized Typeface getMiuiRegularTypeface(Context context) {
        Typeface typeface;
        synchronized (TypefaceHelper.class) {
            if (sMiuiRegularTypeface == null) {
                sMiuiRegularTypeface = Typeface.create("miuiex-normal", 0);
            }
            typeface = sMiuiRegularTypeface;
        }
        return typeface;
    }

    public static synchronized Typeface getMiuiThinTypeface(Context context) {
        Typeface typeface;
        synchronized (TypefaceHelper.class) {
            if (sMiuiThinTypeface == null) {
                sMiuiThinTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Mitype.otf");
            }
            typeface = sMiuiThinTypeface;
        }
        return typeface;
    }

    public static synchronized Typeface getMiuiTypefaceForNA(Context context) {
        Typeface typeface;
        synchronized (TypefaceHelper.class) {
            if (sMiuiNumTypefaceForNA == null) {
                sMiuiNumTypefaceForNA = Typeface.createFromAsset(context.getAssets(), "fonts/NA-Mitype.otf");
            }
            typeface = sMiuiNumTypefaceForNA;
        }
        return typeface;
    }

    public static synchronized Typeface getMiuiTypefaceForSemiBold(Context context) {
        Typeface typeface;
        synchronized (TypefaceHelper.class) {
            if (sMiuiNumTypefaceSemiBold == null) {
                sMiuiNumTypefaceSemiBold = Typeface.createFromAsset(context.getAssets(), "fonts/TitilliumWeb-SemiBold.ttf");
            }
            typeface = sMiuiNumTypefaceSemiBold;
        }
        return typeface;
    }

    public static synchronized Typeface getRobotoBoldCondensed(Context context) {
        Typeface typeface;
        synchronized (TypefaceHelper.class) {
            if (sRobotoBoldCondensed == null) {
                sRobotoBoldCondensed = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-BoldCondensed.ttf");
            }
            typeface = sRobotoBoldCondensed;
        }
        return typeface;
    }
}
