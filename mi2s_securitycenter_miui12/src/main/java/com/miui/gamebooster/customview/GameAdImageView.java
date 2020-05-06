package com.miui.gamebooster.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;
import com.miui.common.customview.AdImageView;

public class GameAdImageView extends AdImageView {

    /* renamed from: d  reason: collision with root package name */
    private Bitmap f4125d;
    private ImageView e;
    private int f;
    private int g;
    private WindowManager h;
    private WindowManager.LayoutParams i;
    private boolean j = false;
    private int k;
    private int l;
    private int m;
    private int n;

    public GameAdImageView(Context context) {
        super(context);
        b(context);
    }

    public GameAdImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        b(context);
    }

    public GameAdImageView(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        b(context);
    }

    private static int a(Context context) {
        Rect rect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int i2 = rect.top;
        if (i2 != 0) {
            return i2;
        }
        try {
            Class<?> cls = Class.forName("com.android.internal.R$dimen");
            return context.getResources().getDimensionPixelSize(Integer.parseInt(cls.getField("status_bar_height").get(cls.newInstance()).toString()));
        } catch (Exception e2) {
            e2.printStackTrace();
            return i2;
        }
    }

    private void a(Bitmap bitmap) {
        this.i = new WindowManager.LayoutParams();
        WindowManager.LayoutParams layoutParams = this.i;
        layoutParams.format = -3;
        layoutParams.gravity = 51;
        layoutParams.x = this.l;
        layoutParams.y = this.k - this.m;
        layoutParams.alpha = 0.9f;
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.flags = 24;
        this.e = new ImageView(getContext());
        this.e.setImageBitmap(bitmap);
        this.h.addView(this.e, this.i);
    }

    private boolean a(View view, int i2, int i3) {
        if (view == null) {
            return false;
        }
        int left = view.getLeft();
        int top = view.getTop();
        return i2 >= left && i2 <= left + view.getWidth() && i3 >= top && i3 <= top + view.getHeight();
    }

    private void b() {
        ImageView imageView = this.e;
        if (imageView != null) {
            this.h.removeView(imageView);
            this.e = null;
        }
    }

    private void b(Context context) {
        this.m = a(context);
        this.h = (WindowManager) context.getSystemService("window");
        this.n = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0036, code lost:
        if (java.lang.Math.abs(r1 - r3.g) <= r3.n) goto L_0x009a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0011, code lost:
        if (r0 != 3) goto L_0x009a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean dispatchTouchEvent(android.view.MotionEvent r4) {
        /*
            r3 = this;
            boolean r0 = r3.j
            if (r0 == 0) goto L_0x009a
            int r0 = r4.getAction()
            r1 = 1
            if (r0 == 0) goto L_0x004a
            if (r0 == r1) goto L_0x0039
            r1 = 2
            if (r0 == r1) goto L_0x0015
            r1 = 3
            if (r0 == r1) goto L_0x0046
            goto L_0x009a
        L_0x0015:
            float r0 = r4.getX()
            int r0 = (int) r0
            float r1 = r4.getY()
            int r1 = (int) r1
            boolean r0 = r3.a(r3, r0, r1)
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L_0x0046
            int r0 = r3.g
            int r1 = r1 - r0
            int r0 = java.lang.Math.abs(r1)
            int r1 = r3.n
            if (r0 <= r1) goto L_0x009a
            goto L_0x0046
        L_0x0039:
            boolean r0 = r3.hasOnClickListeners()
            if (r0 == 0) goto L_0x0046
            r0 = 0
            r3.playSoundEffect(r0)
            r3.callOnClick()
        L_0x0046:
            r3.b()
            goto L_0x009a
        L_0x004a:
            float r0 = r4.getX()
            int r0 = (int) r0
            r3.f = r0
            float r0 = r4.getY()
            int r0 = (int) r0
            r3.g = r0
            float r0 = r4.getRawY()
            int r2 = r3.g
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            r3.k = r0
            float r0 = r4.getRawX()
            int r2 = r3.f
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            r3.l = r0
            r3.setDrawingCacheEnabled(r1)
            android.graphics.Bitmap r0 = r3.getDrawingCache()
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r0)
            r3.f4125d = r0
            android.content.res.Resources r0 = r3.getResources()
            r1 = 2131099976(0x7f060148, float:1.781232E38)
            int r0 = r0.getColor(r1)
            android.graphics.Canvas r1 = new android.graphics.Canvas
            android.graphics.Bitmap r2 = r3.f4125d
            r1.<init>(r2)
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.SRC_ATOP
            r1.drawColor(r0, r2)
            android.graphics.Bitmap r0 = r3.f4125d
            r3.a((android.graphics.Bitmap) r0)
            r3.destroyDrawingCache()
        L_0x009a:
            boolean r4 = super.dispatchTouchEvent(r4)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.customview.GameAdImageView.dispatchTouchEvent(android.view.MotionEvent):boolean");
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.e != null) {
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setShowGray(boolean z) {
        this.j = z;
    }
}
