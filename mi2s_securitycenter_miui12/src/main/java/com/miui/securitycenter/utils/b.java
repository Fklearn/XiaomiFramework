package com.miui.securitycenter.utils;

import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import java.util.Arrays;

public class b {

    /* renamed from: a  reason: collision with root package name */
    protected static final int[] f7539a = {16842919};

    /* renamed from: b  reason: collision with root package name */
    protected static final int[] f7540b = new int[0];

    public static Drawable a(float f, int i, int i2) {
        float[] fArr = new float[8];
        Arrays.fill(fArr, f);
        RoundRectShape roundRectShape = new RoundRectShape(fArr, (RectF) null, (float[]) null);
        ShapeDrawable a2 = a(roundRectShape, i);
        ShapeDrawable a3 = a(roundRectShape, i2);
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(f7539a, a3);
        stateListDrawable.addState(f7540b, a2);
        return stateListDrawable;
    }

    private static ShapeDrawable a(RoundRectShape roundRectShape, int i) {
        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
        Paint paint = shapeDrawable.getPaint();
        paint.setColor(i);
        paint.setStyle(Paint.Style.FILL);
        return shapeDrawable;
    }
}
