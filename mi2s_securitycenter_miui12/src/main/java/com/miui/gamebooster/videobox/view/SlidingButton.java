package com.miui.gamebooster.videobox.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.miui.securitycenter.R;
import com.miui.securitycenter.i;

public class SlidingButton extends CheckBox {

    /* renamed from: a  reason: collision with root package name */
    private static final int[] f5223a = {16842912};

    /* renamed from: b  reason: collision with root package name */
    private Drawable f5224b;

    /* renamed from: c  reason: collision with root package name */
    private Drawable f5225c;

    /* renamed from: d  reason: collision with root package name */
    private int f5226d;
    private Drawable e;
    private int f;
    private int g;
    private int h;
    private int i;
    private int j;
    /* access modifiers changed from: private */
    public int k;
    /* access modifiers changed from: private */
    public int l;
    private int m;
    private int n;
    private boolean o;
    private boolean p;
    private int q;
    /* access modifiers changed from: private */
    public CompoundButton.OnCheckedChangeListener r;
    private Rect s;
    /* access modifiers changed from: private */
    public Animator t;
    /* access modifiers changed from: private */
    public boolean u;
    private StateListDrawable v;
    private boolean w;
    private Animator.AnimatorListener x;

    public SlidingButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public SlidingButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.gbSlidingButtonStyle);
    }

    public SlidingButton(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.s = new Rect();
        this.u = false;
        this.w = true;
        this.x = new i(this);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.GBSlidingButton, i2, R.style.GBSlidingButtonStyle);
        this.w = obtainStyledAttributes.getBoolean(5, true);
        setDrawingCacheEnabled(false);
        this.q = ViewConfiguration.get(context).getScaledTouchSlop() / 2;
        this.f5224b = obtainStyledAttributes.getDrawable(4);
        this.f5225c = obtainStyledAttributes.getDrawable(8);
        this.e = obtainStyledAttributes.getDrawable(7);
        setBackground(obtainStyledAttributes.getDrawable(0));
        this.f = this.f5224b.getIntrinsicWidth();
        this.g = this.f5224b.getIntrinsicHeight();
        Log.i("SlidingButton", "SlidingButton: w=" + this.f + "\th=" + this.g);
        this.h = Math.min(this.f, this.f5225c.getIntrinsicWidth());
        this.i = Math.min(this.g, this.f5225c.getIntrinsicHeight());
        this.j = 0;
        this.k = this.f - this.h;
        this.l = this.j;
        TypedValue typedValue = new TypedValue();
        obtainStyledAttributes.getValue(2, typedValue);
        TypedValue typedValue2 = new TypedValue();
        obtainStyledAttributes.getValue(3, typedValue2);
        Drawable drawable = obtainStyledAttributes.getDrawable(2);
        Drawable drawable2 = obtainStyledAttributes.getDrawable(3);
        Bitmap b2 = b(drawable);
        Bitmap b3 = (typedValue.type == typedValue2.type && typedValue.data == typedValue2.data && typedValue.resourceId == typedValue2.resourceId) ? b2 : b(drawable2);
        this.f5224b.setBounds(0, 0, this.f, this.g);
        if (!(b3 == null || b2 == null)) {
            Drawable drawable3 = obtainStyledAttributes.getDrawable(6);
            drawable3.setBounds(0, 0, this.f, this.g);
            Bitmap a2 = a(drawable3);
            this.v = a(context, a2, b3, b2);
            a2.recycle();
        }
        if (b3 != null && !b3.isRecycled()) {
            b3.recycle();
        }
        if (b2 != null && !b2.isRecycled()) {
            b2.recycle();
        }
        c();
        if (isChecked()) {
            setSliderOffset(this.k);
        }
        obtainStyledAttributes.recycle();
    }

    private Bitmap a(Drawable drawable) {
        Rect bounds = drawable.getBounds();
        Bitmap createBitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(createBitmap);
        drawable.draw(canvas);
        canvas.setBitmap((Bitmap) null);
        return createBitmap;
    }

    private Drawable a(Bitmap bitmap, Bitmap bitmap2, Paint paint) {
        Bitmap createBitmap = Bitmap.createBitmap(this.f, this.g, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
        canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint);
        canvas.setBitmap((Bitmap) null);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getContext().getResources(), createBitmap);
        bitmapDrawable.setBounds(0, 0, this.f, this.g);
        return bitmapDrawable;
    }

    private StateListDrawable a(Context context, Bitmap bitmap, Bitmap bitmap2, Bitmap bitmap3) {
        Paint paint = new Paint();
        Paint paint2 = new Paint();
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        paint2.setColorFilter(new PorterDuffColorFilter(context.getResources().getColor(R.color.gb_sliding_button_bar_on_light), PorterDuff.Mode.SRC_IN));
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Drawable a2 = a(bitmap, bitmap2, paint2);
        Drawable a3 = a(bitmap, bitmap3, paint);
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(f5223a, a2);
        stateListDrawable.addState(CheckBox.EMPTY_STATE_SET, a3);
        stateListDrawable.setExitFadeDuration(context.getResources().getInteger(R.integer.gb_button_exit_fade_duration));
        stateListDrawable.setBounds(0, 0, this.f, this.g);
        stateListDrawable.setCallback(this);
        return stateListDrawable;
    }

    private void a() {
        a(!isChecked());
    }

    private void a(int i2) {
        if (a((View) this)) {
            i2 = -i2;
        }
        this.l += i2;
        int i3 = this.l;
        int i4 = this.j;
        if (i3 < i4 || i3 > (i4 = this.k)) {
            this.l = i4;
        }
        setSliderOffset(this.l);
    }

    private void a(boolean z) {
        Animator animator = this.t;
        if (animator != null) {
            animator.cancel();
            this.t = null;
        }
        int[] iArr = new int[1];
        iArr[0] = z ? this.k : this.j;
        ObjectAnimator ofInt = ObjectAnimator.ofInt(this, "SliderOffset", iArr);
        ofInt.setInterpolator(new e());
        ofInt.setDuration(260);
        this.t = ofInt;
        this.t.addListener(this.x);
        this.t.start();
    }

    public static boolean a(View view) {
        return view.getLayoutDirection() == 1;
    }

    private Bitmap b(Drawable drawable) {
        drawable.setBounds(0, 0, this.f, this.g);
        Bitmap createBitmap = Bitmap.createBitmap(this.f, this.g, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.draw(canvas);
        canvas.setBitmap((Bitmap) null);
        return createBitmap;
    }

    /* access modifiers changed from: private */
    public void b() {
        if (this.r != null) {
            post(new j(this, isChecked()));
        }
    }

    private void c() {
        Drawable drawable = this.f5225c;
        if (drawable != null) {
            drawable.setState(getDrawableState());
            this.v.setState(getDrawableState());
        }
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        c();
    }

    public int getSliderOffset() {
        return this.l;
    }

    public int getSliderOnAlpha() {
        return this.f5226d;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        StateListDrawable stateListDrawable = this.v;
        if (stateListDrawable != null) {
            stateListDrawable.jumpToCurrentState();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Drawable drawable;
        int i2 = isEnabled() ? 255 : 127;
        StateListDrawable stateListDrawable = this.v;
        if (stateListDrawable != null) {
            stateListDrawable.draw(canvas);
        }
        this.f5224b.draw(canvas);
        boolean a2 = a((View) this);
        int i3 = a2 ? (this.f - this.l) - this.h : this.l;
        int i4 = a2 ? this.f - this.l : this.h + this.l;
        int i5 = this.g;
        int i6 = this.i;
        int i7 = (i5 - i6) / 2;
        int i8 = i6 + i7;
        if (isChecked()) {
            this.f5225c.setBounds(i3, i7, i4, i8);
            drawable = this.f5225c;
        } else {
            this.e.setBounds(i3, i7, i4, i8);
            drawable = this.e;
        }
        drawable.draw(canvas);
        setAlpha(((float) i2) / 255.0f);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        setMeasuredDimension(this.f, this.g);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0044, code lost:
        if (r0 != 3) goto L_0x00b2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            boolean r0 = r7.isEnabled()
            r1 = 0
            if (r0 != 0) goto L_0x0008
            return r1
        L_0x0008:
            int r0 = r8.getAction()
            float r2 = r8.getX()
            int r2 = (int) r2
            float r8 = r8.getY()
            int r8 = (int) r8
            android.graphics.Rect r3 = r7.s
            boolean r4 = a((android.view.View) r7)
            if (r4 == 0) goto L_0x0027
            int r5 = r7.f
            int r6 = r7.l
            int r5 = r5 - r6
            int r6 = r7.h
            int r5 = r5 - r6
            goto L_0x0029
        L_0x0027:
            int r5 = r7.l
        L_0x0029:
            if (r4 == 0) goto L_0x0031
            int r4 = r7.f
            int r6 = r7.l
            int r4 = r4 - r6
            goto L_0x0036
        L_0x0031:
            int r4 = r7.l
            int r6 = r7.h
            int r4 = r4 + r6
        L_0x0036:
            int r6 = r7.g
            r3.set(r5, r1, r4, r6)
            r4 = 1
            if (r0 == 0) goto L_0x009e
            r5 = 3
            r6 = 2
            if (r0 == r4) goto L_0x006a
            if (r0 == r6) goto L_0x0048
            if (r0 == r5) goto L_0x006a
            goto L_0x00b2
        L_0x0048:
            boolean r8 = r7.o
            if (r8 == 0) goto L_0x00b2
            int r8 = r7.m
            int r8 = r2 - r8
            r7.a((int) r8)
            r7.m = r2
            int r8 = r7.n
            int r2 = r2 - r8
            int r8 = java.lang.Math.abs(r2)
            int r0 = r7.q
            if (r8 < r0) goto L_0x00b2
            r7.p = r4
            android.view.ViewParent r8 = r7.getParent()
            r8.requestDisallowInterceptTouchEvent(r4)
            goto L_0x00b2
        L_0x006a:
            if (r0 != r5) goto L_0x007d
            int r0 = r3.left
            if (r2 < r0) goto L_0x0074
            int r0 = r3.right
            if (r2 <= r0) goto L_0x007d
        L_0x0074:
            int r0 = r3.top
            if (r8 < r0) goto L_0x00b2
            int r0 = r3.bottom
            if (r8 <= r0) goto L_0x007d
            goto L_0x00b2
        L_0x007d:
            boolean r8 = r7.o
            if (r8 == 0) goto L_0x0093
            boolean r8 = r7.p
            if (r8 != 0) goto L_0x0086
            goto L_0x0093
        L_0x0086:
            int r8 = r7.l
            int r0 = r7.k
            int r0 = r0 / r6
            if (r8 < r0) goto L_0x008e
            goto L_0x008f
        L_0x008e:
            r4 = r1
        L_0x008f:
            r7.a((boolean) r4)
            goto L_0x0096
        L_0x0093:
            r7.a()
        L_0x0096:
            r7.o = r1
            r7.p = r1
            r7.setPressed(r1)
            goto L_0x00b2
        L_0x009e:
            boolean r8 = r3.contains(r2, r8)
            if (r8 == 0) goto L_0x00aa
            r7.o = r4
            r7.setPressed(r4)
            goto L_0x00ac
        L_0x00aa:
            r7.o = r1
        L_0x00ac:
            r7.m = r2
            r7.n = r2
            r7.p = r1
        L_0x00b2:
            boolean r8 = r7.w
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.videobox.view.SlidingButton.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public boolean performClick() {
        super.performClick();
        b();
        return true;
    }

    public void setButtonDrawable(Drawable drawable) {
    }

    public void setChecked(boolean z) {
        if (isChecked() != z) {
            super.setChecked(z);
            this.l = z ? this.k : this.j;
            this.f5226d = z ? 255 : 0;
            invalidate();
        }
    }

    public void setOnPerformCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.r = onCheckedChangeListener;
    }

    public void setPressed(boolean z) {
        super.setPressed(z);
        invalidate();
    }

    public void setSliderOffset(int i2) {
        this.l = i2;
        invalidate();
    }

    public void setSliderOnAlpha(int i2) {
        this.f5226d = i2;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.v;
    }
}
