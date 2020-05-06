package com.miui.privacyapps.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.miui.securitycenter.R;

public class ViewPagerIndicator extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private Context f7425a;

    /* renamed from: b  reason: collision with root package name */
    private GradientDrawable f7426b;

    /* renamed from: c  reason: collision with root package name */
    private GradientDrawable f7427c;

    /* renamed from: d  reason: collision with root package name */
    private int f7428d;
    private int e;
    private int f;
    private int g;
    private int h;
    private boolean i;

    public ViewPagerIndicator(Context context) {
        this(context, (AttributeSet) null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f7425a = context;
        a();
    }

    private Drawable a(Drawable drawable, Drawable drawable2) {
        int[][] iArr = {new int[]{-16842913}, new int[]{16842913}};
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(iArr[0], drawable);
        stateListDrawable.addState(iArr[1], drawable2);
        return stateListDrawable;
    }

    private void a() {
        setOrientation(0);
        this.e = getResources().getDimensionPixelSize(R.dimen.viewpager_indicator_item_interval);
        this.f = this.e;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0038, code lost:
        r5 = r6.f7427c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(int r7, int r8) {
        /*
            r6 = this;
            if (r7 > 0) goto L_0x0003
            return
        L_0x0003:
            r6.f7428d = r7
            boolean r0 = r6.i
            r1 = 0
            if (r0 == 0) goto L_0x000c
            int r8 = r8 % r7
            goto L_0x0014
        L_0x000c:
            if (r8 > 0) goto L_0x0010
            r8 = r1
            goto L_0x0014
        L_0x0010:
            if (r8 < r7) goto L_0x0014
            int r8 = r7 + -1
        L_0x0014:
            r6.removeAllViews()
            android.widget.LinearLayout$LayoutParams r0 = new android.widget.LinearLayout$LayoutParams
            r2 = 1065353216(0x3f800000, float:1.0)
            r3 = -2
            r0.<init>(r3, r3, r2)
            int r2 = r6.e
            int r3 = r6.g
            int r4 = r6.f
            int r5 = r6.h
            r0.setMargins(r2, r3, r4, r5)
            r2 = r1
        L_0x002b:
            if (r2 >= r7) goto L_0x005d
            android.widget.ImageView r3 = new android.widget.ImageView
            android.content.Context r4 = r6.f7425a
            r3.<init>(r4)
            android.graphics.drawable.GradientDrawable r4 = r6.f7426b
            if (r4 == 0) goto L_0x0041
            android.graphics.drawable.GradientDrawable r5 = r6.f7427c
            if (r5 == 0) goto L_0x0041
            android.graphics.drawable.Drawable r4 = r6.a((android.graphics.drawable.Drawable) r4, (android.graphics.drawable.Drawable) r5)
            goto L_0x004c
        L_0x0041:
            android.content.res.Resources r4 = r6.getResources()
            r5 = 2131232297(0x7f080629, float:1.80807E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)
        L_0x004c:
            r3.setImageDrawable(r4)
            if (r2 != r8) goto L_0x0053
            r4 = 1
            goto L_0x0054
        L_0x0053:
            r4 = r1
        L_0x0054:
            r3.setSelected(r4)
            r6.addView(r3, r0)
            int r2 = r2 + 1
            goto L_0x002b
        L_0x005d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.privacyapps.view.ViewPagerIndicator.a(int, int):void");
    }

    public void a(int i2, int i3, int i4, int i5) {
        this.e = i2;
        this.g = i3;
        this.f = i4;
        this.h = i5;
    }

    public void a(int i2, int i3, int i4, int i5, int i6) {
        this.f7426b = new GradientDrawable();
        this.f7426b.setShape(i2);
        this.f7426b.setColor(i5);
        this.f7426b.setSize(i3, i4);
        this.f7427c = new GradientDrawable();
        this.f7427c.setShape(i2);
        this.f7427c.setColor(i6);
        this.f7427c.setSize(i3, i4);
    }

    public void setCycle(boolean z) {
        this.i = z;
    }

    public void setIndicatorNum(int i2) {
        a(i2, 0);
    }

    public void setSelected(int i2) {
        if (this.i) {
            i2 %= this.f7428d;
        } else if (i2 < 0 || i2 >= this.f7428d) {
            return;
        }
        int i3 = 0;
        while (i3 < this.f7428d) {
            ((ImageView) getChildAt(i3)).setSelected(i3 == i2);
            i3++;
        }
    }
}
