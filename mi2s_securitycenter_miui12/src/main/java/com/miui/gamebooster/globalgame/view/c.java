package com.miui.gamebooster.globalgame.view;

import android.view.MotionEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static DecimalFormat f4430a;

    static float a(float f, int i, float f2) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        float f3 = (float) i;
        if (f > f3) {
            f = f3;
        }
        return f % f2 != 0.0f ? f2 : f;
    }

    static float a(b bVar, float f, float f2) {
        DecimalFormat a2 = a();
        return Float.parseFloat(a2.format((double) (((float) ((Integer) bVar.getTag()).intValue()) - (1.0f - (((float) Math.round(Float.parseFloat(a2.format((double) ((f2 - ((float) bVar.getLeft())) / ((float) bVar.getWidth())))) / f)) * f)))));
    }

    static DecimalFormat a() {
        if (f4430a == null) {
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
            decimalFormatSymbols.setDecimalSeparator('.');
            f4430a = new DecimalFormat("#.##", decimalFormatSymbols);
        }
        return f4430a;
    }

    static boolean a(float f, float f2, MotionEvent motionEvent) {
        if (((float) (motionEvent.getEventTime() - motionEvent.getDownTime())) > 200.0f) {
            return false;
        }
        return Math.abs(f - motionEvent.getX()) <= 5.0f && Math.abs(f2 - motionEvent.getY()) <= 5.0f;
    }
}
