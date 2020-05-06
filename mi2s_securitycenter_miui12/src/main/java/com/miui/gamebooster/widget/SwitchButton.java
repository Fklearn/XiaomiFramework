package com.miui.gamebooster.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import com.miui.gamebooster.globalgame.view.RoundedDrawable;
import com.miui.securitycenter.R;
import com.miui.securitycenter.i;

public class SwitchButton extends CompoundButton {

    /* renamed from: a  reason: collision with root package name */
    private static int[] f5381a = {16842912, 16842910, 16842919};

    /* renamed from: b  reason: collision with root package name */
    private static int[] f5382b = {-16842912, 16842910, 16842919};
    private RectF A;
    private RectF B;
    private RectF C;
    private Paint D;
    private boolean E;
    private boolean F;
    private boolean G = false;
    private ObjectAnimator H;
    private float I;
    private RectF J;
    private float K;
    private float L;
    private float M;
    private int N;
    private int O;
    private Paint P;
    private CharSequence Q;
    private CharSequence R;
    private TextPaint S;
    private Layout T;
    private Layout U;
    private float V;
    private float W;
    private int aa;
    private int ba;

    /* renamed from: c  reason: collision with root package name */
    private Drawable f5383c;
    private int ca;

    /* renamed from: d  reason: collision with root package name */
    private Drawable f5384d;
    private boolean da = false;
    private ColorStateList e;
    private boolean ea = false;
    private ColorStateList f;
    private boolean fa = false;
    private float g;
    private CompoundButton.OnCheckedChangeListener ga;
    private float h;
    private RectF i;
    private float j;
    private long k;
    private boolean l;
    private int m;
    private int n;
    private int o;
    private int p;
    private int q;
    private int r;
    private int s;
    private int t;
    private int u;
    private int v;
    private Drawable w;
    private Drawable x;
    private RectF y;
    private RectF z;

    static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new f();
        CharSequence offText;
        CharSequence onText;

        private SavedState(Parcel parcel) {
            super(parcel);
            this.onText = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            this.offText = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            TextUtils.writeToParcel(this.onText, parcel, i);
            TextUtils.writeToParcel(this.offText, parcel, i);
        }
    }

    public SwitchButton(Context context) {
        super(context);
        a(context, (AttributeSet) null);
    }

    public SwitchButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context, attributeSet);
    }

    public SwitchButton(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        a(context, attributeSet);
    }

    private int a(double d2) {
        return (int) Math.ceil(d2);
    }

    static ColorStateList a(int i2) {
        int i3 = i2 - -805306368;
        return new ColorStateList(new int[][]{new int[]{-16842910, 16842912}, new int[]{-16842910}, new int[]{16842912, 16842919}, new int[]{-16842912, 16842919}, new int[]{16842912}, new int[]{-16842912}}, new int[]{i2 - -520093696, 268435456, i3, 536870912, i3, 536870912});
    }

    private Layout a(CharSequence charSequence) {
        TextPaint textPaint = this.S;
        return new StaticLayout(charSequence, textPaint, (int) Math.ceil((double) Layout.getDesiredWidth(charSequence, textPaint)), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
    }

    private void a() {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        this.fa = true;
    }

    private void a(Context context, AttributeSet attributeSet) {
        Drawable drawable;
        Drawable drawable2;
        this.N = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.O = ViewConfiguration.getPressedStateDuration() + ViewConfiguration.getTapTimeout();
        this.D = new Paint(1);
        this.P = new Paint(1);
        this.P.setStyle(Paint.Style.STROKE);
        this.P.setStrokeWidth(getResources().getDisplayMetrics().density);
        this.S = getPaint();
        this.y = new RectF();
        this.z = new RectF();
        this.A = new RectF();
        this.i = new RectF();
        this.B = new RectF();
        this.C = new RectF();
        this.H = ObjectAnimator.ofFloat(this, "progress", new float[]{0.0f, 0.0f}).setDuration(250);
        this.H.setInterpolator(new AccelerateDecelerateInterpolator());
        this.J = new RectF();
        float f2 = getResources().getDisplayMetrics().density;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.CustomSwitchButton);
        boolean z2 = false;
        if (obtainStyledAttributes != null) {
            drawable2 = obtainStyledAttributes.getDrawable(1);
            drawable = obtainStyledAttributes.getDrawable(0);
            obtainStyledAttributes.recycle();
        } else {
            drawable2 = null;
            drawable = null;
        }
        if (drawable2 == null) {
            drawable2 = context.getResources().getDrawable(R.drawable.gb_switch_thumb_selector);
        }
        if (drawable == null) {
            drawable = context.getResources().getDrawable(R.drawable.gb_switch_bg_selector);
        }
        TypedArray obtainStyledAttributes2 = attributeSet == null ? null : context.obtainStyledAttributes(attributeSet, new int[]{16842970, 16842981});
        if (obtainStyledAttributes2 != null) {
            boolean z3 = obtainStyledAttributes2.getBoolean(0, true);
            boolean z4 = obtainStyledAttributes2.getBoolean(1, z3);
            setFocusable(z3);
            setClickable(z4);
            obtainStyledAttributes2.recycle();
        } else {
            setFocusable(true);
            setClickable(true);
        }
        this.Q = null;
        this.R = null;
        this.aa = 0;
        this.ba = 0;
        this.ca = 0;
        this.f5383c = drawable2;
        this.f = null;
        this.E = this.f5383c != null;
        this.m = 0;
        if (this.m == 0) {
            new TypedValue();
            this.m = 3309506;
        }
        if (!this.E && this.f == null) {
            this.f = b(this.m);
            this.r = this.f.getDefaultColor();
        }
        double d2 = (double) 0.0f;
        this.n = a(d2);
        this.o = a(d2);
        this.f5384d = drawable;
        this.e = null;
        if (this.f5384d != null) {
            z2 = true;
        }
        this.F = z2;
        if (!this.F && this.e == null) {
            this.e = a(this.m);
            this.s = this.e.getDefaultColor();
            this.t = this.e.getColorForState(f5381a, this.s);
        }
        this.i.set(20.0f, 20.0f, 20.0f, 20.0f);
        float f3 = 1.85f;
        if (this.i.width() >= 0.0f) {
            f3 = Math.max(1.85f, 1.0f);
        }
        this.j = f3;
        this.g = -1.0f;
        this.h = -1.0f;
        this.k = (long) 250;
        this.l = true;
        this.H.setDuration(this.k);
        if (isChecked()) {
            setProgress(1.0f);
        }
    }

    static ColorStateList b(int i2) {
        int i3 = i2 - -1728053248;
        return new ColorStateList(new int[][]{new int[]{-16842910, 16842912}, new int[]{-16842910}, new int[]{16842919, -16842912}, new int[]{16842919, 16842912}, new int[]{16842912}, new int[]{-16842912}}, new int[]{i2 - -1442840576, -4539718, i3, i3, i2 | RoundedDrawable.DEFAULT_BORDER_COLOR, -1118482});
    }

    private boolean b() {
        return 1 == getLayoutDirection();
    }

    private int c(int i2) {
        int size = View.MeasureSpec.getSize(i2);
        int mode = View.MeasureSpec.getMode(i2);
        if (this.o == 0 && this.E) {
            this.o = this.f5383c.getIntrinsicHeight();
        }
        if (mode == 1073741824) {
            int i3 = this.o;
            if (i3 != 0) {
                RectF rectF = this.i;
                this.q = a((double) (((float) i3) + rectF.top + rectF.bottom));
                this.q = a((double) Math.max((float) this.q, this.W));
                if ((((float) ((this.q + getPaddingTop()) + getPaddingBottom())) - Math.min(0.0f, this.i.top)) - Math.min(0.0f, this.i.bottom) > ((float) size)) {
                    this.o = 0;
                }
            }
            if (this.o == 0) {
                this.q = a((double) (((float) ((size - getPaddingTop()) - getPaddingBottom())) + Math.min(0.0f, this.i.top) + Math.min(0.0f, this.i.bottom)));
                int i4 = this.q;
                if (i4 >= 0) {
                    RectF rectF2 = this.i;
                    this.o = a((double) ((((float) i4) - rectF2.top) - rectF2.bottom));
                }
            }
            if (this.o >= 0) {
                return size;
            }
        } else {
            if (this.o == 0) {
                this.o = a((double) (getResources().getDisplayMetrics().density * 20.0f));
            }
            RectF rectF3 = this.i;
            this.q = a((double) (((float) this.o) + rectF3.top + rectF3.bottom));
            int i5 = this.q;
            if (i5 >= 0) {
                int a2 = a((double) (this.W - ((float) i5)));
                if (a2 > 0) {
                    this.q += a2;
                    this.o += a2;
                }
                int max = Math.max(this.o, this.q);
                return Math.max(Math.max(max, getPaddingTop() + max + getPaddingBottom()), getSuggestedMinimumHeight());
            }
        }
        this.q = 0;
        this.o = 0;
        return size;
    }

    private void c() {
        int i2;
        float f2;
        float f3;
        int i3 = this.n;
        if (i3 != 0 && (i2 = this.o) != 0 && this.p != 0 && this.q != 0) {
            if (this.g == -1.0f) {
                this.g = (float) (Math.min(i3, i2) / 2);
            }
            if (this.h == -1.0f) {
                this.h = (float) (Math.min(this.p, this.q) / 2);
            }
            int measuredWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
            int measuredHeight = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
            int a2 = a((double) ((((float) this.p) - Math.min(0.0f, this.i.left)) - Math.min(0.0f, this.i.right)));
            int a3 = a((double) ((((float) this.q) - Math.min(0.0f, this.i.top)) - Math.min(0.0f, this.i.bottom)));
            if (measuredHeight <= a3) {
                f2 = ((float) getPaddingTop()) + Math.max(0.0f, this.i.top);
            } else {
                f2 = ((float) (((measuredHeight - a3) + 1) / 2)) + ((float) getPaddingTop()) + Math.max(0.0f, this.i.top);
            }
            if (measuredWidth <= this.p) {
                f3 = ((float) getPaddingLeft()) + Math.max(0.0f, this.i.left);
            } else {
                f3 = ((float) (((measuredWidth - a2) + 1) / 2)) + ((float) getPaddingLeft()) + Math.max(0.0f, this.i.left);
            }
            this.y.set(f3, f2, ((float) this.n) + f3, ((float) this.o) + f2);
            RectF rectF = this.y;
            float f4 = rectF.left;
            RectF rectF2 = this.i;
            float f5 = f4 - rectF2.left;
            RectF rectF3 = this.z;
            float f6 = rectF.top;
            float f7 = rectF2.top;
            rectF3.set(f5, f6 - f7, ((float) this.p) + f5, (f6 - f7) + ((float) this.q));
            RectF rectF4 = this.A;
            RectF rectF5 = this.y;
            rectF4.set(rectF5.left, 0.0f, (this.z.right - this.i.right) - rectF5.width(), 0.0f);
            if (b()) {
                float width = this.A.width();
                this.y.set(f3 + width, f2, f3 + ((float) this.n) + width, ((float) this.o) + f2);
            }
            this.h = Math.min(Math.min(this.z.width(), this.z.height()) / 2.0f, this.h);
            Drawable drawable = this.f5384d;
            if (drawable != null) {
                RectF rectF6 = this.z;
                drawable.setBounds((int) rectF6.left, (int) rectF6.top, a((double) rectF6.right), a((double) this.z.bottom));
            }
            if (this.T != null) {
                RectF rectF7 = this.z;
                float width2 = (rectF7.left + (((((rectF7.width() + ((float) this.aa)) - ((float) this.n)) - this.i.right) - ((float) this.T.getWidth())) / 2.0f)) - ((float) this.ca);
                RectF rectF8 = this.z;
                float height = rectF8.top + ((rectF8.height() - ((float) this.T.getHeight())) / 2.0f);
                this.B.set(width2, height, ((float) this.T.getWidth()) + width2, ((float) this.T.getHeight()) + height);
            }
            if (this.U != null) {
                RectF rectF9 = this.z;
                float width3 = ((rectF9.right - (((((rectF9.width() + ((float) this.aa)) - ((float) this.n)) - this.i.left) - ((float) this.U.getWidth())) / 2.0f)) - ((float) this.U.getWidth())) + ((float) this.ca);
                RectF rectF10 = this.z;
                float height2 = rectF10.top + ((rectF10.height() - ((float) this.U.getHeight())) / 2.0f);
                this.C.set(width3, height2, ((float) this.U.getWidth()) + width3, ((float) this.U.getHeight()) + height2);
            }
            this.ea = true;
        }
    }

    private int d(int i2) {
        int size = View.MeasureSpec.getSize(i2);
        int mode = View.MeasureSpec.getMode(i2);
        if (this.n == 0 && this.E) {
            this.n = this.f5383c.getIntrinsicWidth();
        }
        int a2 = a((double) this.V);
        if (this.j == 0.0f) {
            this.j = 1.85f;
        }
        if (mode == 1073741824) {
            int paddingLeft = (size - getPaddingLeft()) - getPaddingRight();
            int i3 = this.n;
            if (i3 != 0) {
                int a3 = a((double) (((float) i3) * this.j));
                RectF rectF = this.i;
                int a4 = (this.ba + a2) - ((a3 - this.n) + a((double) Math.max(rectF.left, rectF.right)));
                float f2 = (float) a3;
                RectF rectF2 = this.i;
                this.p = a((double) (rectF2.left + f2 + rectF2.right + ((float) Math.max(a4, 0))));
                if (this.p < 0) {
                    this.n = 0;
                }
                if (f2 + Math.max(this.i.left, 0.0f) + Math.max(this.i.right, 0.0f) + ((float) Math.max(a4, 0)) > ((float) paddingLeft)) {
                    this.n = 0;
                }
            }
            if (this.n != 0) {
                return size;
            }
            int a5 = a((double) ((((float) ((size - getPaddingLeft()) - getPaddingRight())) - Math.max(this.i.left, 0.0f)) - Math.max(this.i.right, 0.0f)));
            if (a5 >= 0) {
                float f3 = (float) a5;
                this.n = a((double) (f3 / this.j));
                RectF rectF3 = this.i;
                this.p = a((double) (f3 + rectF3.left + rectF3.right));
                if (this.p >= 0) {
                    int i4 = a2 + this.ba;
                    int i5 = a5 - this.n;
                    RectF rectF4 = this.i;
                    int a6 = i4 - (i5 + a((double) Math.max(rectF4.left, rectF4.right)));
                    if (a6 > 0) {
                        this.n -= a6;
                    }
                    if (this.n >= 0) {
                        return size;
                    }
                }
            }
        } else {
            if (this.n == 0) {
                this.n = a((double) (getResources().getDisplayMetrics().density * 20.0f));
            }
            if (this.j == 0.0f) {
                this.j = 1.85f;
            }
            int a7 = a((double) (((float) this.n) * this.j));
            RectF rectF5 = this.i;
            int a8 = a((double) (((float) (a2 + this.ba)) - ((((float) (a7 - this.n)) + Math.max(rectF5.left, rectF5.right)) + ((float) this.aa))));
            float f4 = (float) a7;
            RectF rectF6 = this.i;
            this.p = a((double) (rectF6.left + f4 + rectF6.right + ((float) Math.max(0, a8))));
            if (this.p >= 0) {
                int a9 = a((double) (f4 + Math.max(0.0f, this.i.left) + Math.max(0.0f, this.i.right) + ((float) Math.max(0, a8))));
                return Math.max(a9, getPaddingLeft() + a9 + getPaddingRight());
            }
        }
        this.n = 0;
        this.p = 0;
        return size;
    }

    private float getProgress() {
        return this.I;
    }

    private boolean getStatusBasedOnPos() {
        return getProgress() > 0.5f;
    }

    private void setDrawableState(Drawable drawable) {
        if (drawable != null) {
            drawable.setState(getDrawableState());
            invalidate();
        }
    }

    @Keep
    private void setProgress(float f2) {
        if (f2 > 1.0f) {
            f2 = 1.0f;
        } else if (f2 < 0.0f) {
            f2 = 0.0f;
        }
        this.I = f2;
        invalidate();
    }

    public void a(float f2, float f3, float f4, float f5) {
        this.i.set(f2, f3, f4, f5);
        this.ea = false;
        requestLayout();
    }

    public void a(CharSequence charSequence, CharSequence charSequence2) {
        this.Q = charSequence;
        this.R = charSequence2;
        this.T = null;
        this.U = null;
        this.ea = false;
        requestLayout();
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void a(boolean z2) {
        ObjectAnimator objectAnimator;
        float[] fArr;
        ObjectAnimator objectAnimator2 = this.H;
        if (objectAnimator2 != null) {
            if (objectAnimator2.isRunning()) {
                this.H.cancel();
            }
            this.H.setDuration(this.k);
            if (z2) {
                objectAnimator = this.H;
                fArr = new float[]{this.I, 1.0f};
            } else {
                objectAnimator = this.H;
                fArr = new float[]{this.I, 0.0f};
            }
            objectAnimator.setFloatValues(fArr);
            this.H.start();
        }
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        Drawable drawable;
        ColorStateList colorStateList;
        ColorStateList colorStateList2;
        super.drawableStateChanged();
        if (this.E || (colorStateList2 = this.f) == null) {
            setDrawableState(this.f5383c);
        } else {
            this.r = colorStateList2.getColorForState(getDrawableState(), this.r);
        }
        int[] iArr = isChecked() ? f5382b : f5381a;
        ColorStateList textColors = getTextColors();
        if (textColors != null) {
            int defaultColor = textColors.getDefaultColor();
            this.u = textColors.getColorForState(f5381a, defaultColor);
            this.v = textColors.getColorForState(f5382b, defaultColor);
        }
        if (this.F || (colorStateList = this.e) == null) {
            Drawable drawable2 = this.f5384d;
            if (!(drawable2 instanceof StateListDrawable) || !this.l) {
                drawable = null;
            } else {
                drawable2.setState(iArr);
                drawable = this.f5384d.getCurrent().mutate();
            }
            this.x = drawable;
            setDrawableState(this.f5384d);
            Drawable drawable3 = this.f5384d;
            if (drawable3 != null) {
                this.w = drawable3.getCurrent().mutate();
                return;
            }
            return;
        }
        this.s = colorStateList.getColorForState(getDrawableState(), this.s);
        this.t = this.e.getColorForState(iArr, this.s);
    }

    public long getAnimationDuration() {
        return this.k;
    }

    public ColorStateList getBackColor() {
        return this.e;
    }

    public Drawable getBackDrawable() {
        return this.f5384d;
    }

    public float getBackRadius() {
        return this.h;
    }

    public PointF getBackSizeF() {
        return new PointF(this.z.width(), this.z.height());
    }

    public CharSequence getTextOff() {
        return this.R;
    }

    public CharSequence getTextOn() {
        return this.Q;
    }

    public ColorStateList getThumbColor() {
        return this.f;
    }

    public Drawable getThumbDrawable() {
        return this.f5383c;
    }

    public float getThumbHeight() {
        return (float) this.o;
    }

    public RectF getThumbMargin() {
        return this.i;
    }

    public float getThumbRadius() {
        return this.g;
    }

    public float getThumbRangeRatio() {
        return this.j;
    }

    public float getThumbWidth() {
        return (float) this.n;
    }

    public int getTintColor() {
        return this.m;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!this.ea) {
            c();
        }
        if (this.ea) {
            if (this.F) {
                if (!this.l || this.w == null || this.x == null) {
                    this.f5384d.setAlpha(255);
                    this.f5384d.draw(canvas);
                } else {
                    Drawable drawable = isChecked() ? this.w : this.x;
                    Drawable drawable2 = isChecked() ? this.x : this.w;
                    int progress = (int) (getProgress() * 255.0f);
                    drawable.setAlpha(progress);
                    drawable.draw(canvas);
                    drawable2.setAlpha(255 - progress);
                    drawable2.draw(canvas);
                }
            } else if (this.l) {
                int i2 = isChecked() ? this.s : this.t;
                int i3 = isChecked() ? this.t : this.s;
                int progress2 = (int) (getProgress() * 255.0f);
                this.D.setARGB((Color.alpha(i2) * progress2) / 255, Color.red(i2), Color.green(i2), Color.blue(i2));
                RectF rectF = this.z;
                float f2 = this.h;
                canvas.drawRoundRect(rectF, f2, f2, this.D);
                this.D.setARGB((Color.alpha(i3) * (255 - progress2)) / 255, Color.red(i3), Color.green(i3), Color.blue(i3));
                RectF rectF2 = this.z;
                float f3 = this.h;
                canvas.drawRoundRect(rectF2, f3, f3, this.D);
                this.D.setAlpha(255);
            } else {
                this.D.setColor(this.s);
                RectF rectF3 = this.z;
                float f4 = this.h;
                canvas.drawRoundRect(rectF3, f4, f4, this.D);
            }
            Layout layout = ((double) getProgress()) > 0.5d ? this.T : this.U;
            RectF rectF4 = ((double) getProgress()) > 0.5d ? this.B : this.C;
            if (!(layout == null || rectF4 == null)) {
                int progress3 = (int) ((((double) getProgress()) >= 0.75d ? (getProgress() * 4.0f) - 3.0f : ((double) getProgress()) < 0.25d ? 1.0f - (getProgress() * 4.0f) : 0.0f) * 255.0f);
                int i4 = ((double) getProgress()) > 0.5d ? this.u : this.v;
                layout.getPaint().setARGB((Color.alpha(i4) * progress3) / 255, Color.red(i4), Color.green(i4), Color.blue(i4));
                canvas.save();
                canvas.translate(rectF4.left, rectF4.top);
                layout.draw(canvas);
                canvas.restore();
            }
            this.J.set(this.y);
            int i5 = 1;
            if (b()) {
                i5 = -1;
            }
            this.J.offset(this.I * this.A.width() * ((float) i5), 0.0f);
            if (this.E) {
                Drawable drawable3 = this.f5383c;
                RectF rectF5 = this.J;
                drawable3.setBounds((int) rectF5.left, (int) rectF5.top, a((double) rectF5.right), a((double) this.J.bottom));
                this.f5383c.draw(canvas);
            } else {
                this.D.setColor(this.r);
                RectF rectF6 = this.J;
                float f5 = this.g;
                canvas.drawRoundRect(rectF6, f5, f5, this.D);
            }
            if (this.G) {
                this.P.setColor(Color.parseColor("#AA0000"));
                canvas.drawRect(this.z, this.P);
                this.P.setColor(Color.parseColor("#0000FF"));
                canvas.drawRect(this.J, this.P);
                this.P.setColor(Color.parseColor("#000000"));
                RectF rectF7 = this.A;
                float f6 = rectF7.left;
                float f7 = this.y.top;
                canvas.drawLine(f6, f7, rectF7.right, f7, this.P);
                this.P.setColor(Color.parseColor("#00CC00"));
                canvas.drawRect(((double) getProgress()) > 0.5d ? this.B : this.C, this.P);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        if (this.T == null && !TextUtils.isEmpty(this.Q)) {
            this.T = a(this.Q);
        }
        if (this.U == null && !TextUtils.isEmpty(this.R)) {
            this.U = a(this.R);
        }
        Layout layout = this.T;
        float width = layout != null ? (float) layout.getWidth() : 0.0f;
        Layout layout2 = this.U;
        float width2 = layout2 != null ? (float) layout2.getWidth() : 0.0f;
        if (width == 0.0f && width2 == 0.0f) {
            this.V = 0.0f;
        } else {
            this.V = Math.max(width, width2);
        }
        Layout layout3 = this.T;
        float height = layout3 != null ? (float) layout3.getHeight() : 0.0f;
        Layout layout4 = this.U;
        float height2 = layout4 != null ? (float) layout4.getHeight() : 0.0f;
        if (height == 0.0f && height2 == 0.0f) {
            this.W = 0.0f;
        } else {
            this.W = Math.max(height, height2);
        }
        setMeasuredDimension(d(i2), c(i3));
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        a(savedState.onText, savedState.offText);
        this.da = true;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.da = false;
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.onText = this.Q;
        savedState.offText = this.R;
        return savedState;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i2, int i3, int i4, int i5) {
        super.onSizeChanged(i2, i3, i4, i5);
        if (i2 != i4 || i3 != i5) {
            c();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0034, code lost:
        if (r0 != 3) goto L_0x00f3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r10) {
        /*
            r9 = this;
            boolean r0 = r9.isEnabled()
            r1 = 0
            if (r0 == 0) goto L_0x00f4
            boolean r0 = r9.isClickable()
            if (r0 == 0) goto L_0x00f4
            boolean r0 = r9.isFocusable()
            if (r0 == 0) goto L_0x00f4
            boolean r0 = r9.ea
            if (r0 != 0) goto L_0x0019
            goto L_0x00f4
        L_0x0019:
            int r0 = r10.getAction()
            float r2 = r10.getX()
            float r3 = r9.K
            float r2 = r2 - r3
            float r3 = r10.getY()
            float r4 = r9.L
            float r3 = r3 - r4
            r4 = 1
            if (r0 == 0) goto L_0x00e0
            if (r0 == r4) goto L_0x009b
            r5 = 2
            if (r0 == r5) goto L_0x0038
            r5 = 3
            if (r0 == r5) goto L_0x009b
            goto L_0x00f3
        L_0x0038:
            float r10 = r10.getX()
            boolean r0 = r9.b()
            if (r0 == 0) goto L_0x0044
            r0 = -1
            goto L_0x0045
        L_0x0044:
            r0 = r4
        L_0x0045:
            float r6 = r9.getProgress()
            float r7 = r9.M
            float r7 = r10 - r7
            float r0 = (float) r0
            float r7 = r7 * r0
            android.graphics.RectF r0 = r9.A
            float r0 = r0.width()
            float r7 = r7 / r0
            float r6 = r6 + r7
            r9.setProgress(r6)
            boolean r0 = r9.fa
            if (r0 != 0) goto L_0x0098
            float r0 = java.lang.Math.abs(r2)
            int r6 = r9.N
            int r6 = r6 / r5
            float r6 = (float) r6
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 > 0) goto L_0x0076
            float r0 = java.lang.Math.abs(r3)
            int r6 = r9.N
            int r6 = r6 / r5
            float r5 = (float) r6
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x0098
        L_0x0076:
            r0 = 0
            int r0 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
            if (r0 == 0) goto L_0x0095
            float r0 = java.lang.Math.abs(r2)
            float r5 = java.lang.Math.abs(r3)
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x0088
            goto L_0x0095
        L_0x0088:
            float r0 = java.lang.Math.abs(r3)
            float r2 = java.lang.Math.abs(r2)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x0098
            return r1
        L_0x0095:
            r9.a()
        L_0x0098:
            r9.M = r10
            goto L_0x00f3
        L_0x009b:
            r9.fa = r1
            r9.setPressed(r1)
            long r5 = r10.getEventTime()
            long r7 = r10.getDownTime()
            long r5 = r5 - r7
            float r10 = (float) r5
            float r0 = java.lang.Math.abs(r2)
            int r2 = r9.N
            float r2 = (float) r2
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x00cb
            float r0 = java.lang.Math.abs(r3)
            int r2 = r9.N
            float r2 = (float) r2
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x00cb
            int r0 = r9.O
            float r0 = (float) r0
            int r10 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r10 >= 0) goto L_0x00cb
            r9.performClick()
            goto L_0x00f3
        L_0x00cb:
            boolean r10 = r9.getStatusBasedOnPos()
            boolean r0 = r9.isChecked()
            if (r10 == r0) goto L_0x00dc
            r9.playSoundEffect(r1)
            r9.setChecked(r10)
            goto L_0x00f3
        L_0x00dc:
            r9.a((boolean) r10)
            goto L_0x00f3
        L_0x00e0:
            float r0 = r10.getX()
            r9.K = r0
            float r10 = r10.getY()
            r9.L = r10
            float r10 = r9.K
            r9.M = r10
            r9.setPressed(r4)
        L_0x00f3:
            return r4
        L_0x00f4:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.widget.SwitchButton.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public boolean performClick() {
        return super.performClick();
    }

    public void setAnimationDuration(long j2) {
        this.k = j2;
    }

    public void setBackColor(ColorStateList colorStateList) {
        this.e = colorStateList;
        if (this.e != null) {
            setBackDrawable((Drawable) null);
        }
        invalidate();
    }

    public void setBackColorRes(int i2) {
        setBackColor(ContextCompat.getColorStateList(getContext(), i2));
    }

    public void setBackDrawable(Drawable drawable) {
        this.f5384d = drawable;
        this.F = this.f5384d != null;
        refreshDrawableState();
        this.ea = false;
        requestLayout();
        invalidate();
    }

    public void setBackDrawableRes(int i2) {
        setBackDrawable(ContextCompat.getDrawable(getContext(), i2));
    }

    public void setBackRadius(float f2) {
        this.h = f2;
        if (!this.F) {
            invalidate();
        }
    }

    public void setChecked(boolean z2) {
        if (isChecked() != z2) {
            a(z2);
        }
        if (this.da) {
            setCheckedImmediatelyNoEvent(z2);
        } else {
            super.setChecked(z2);
        }
    }

    public void setCheckedImmediately(boolean z2) {
        super.setChecked(z2);
        ObjectAnimator objectAnimator = this.H;
        if (objectAnimator != null && objectAnimator.isRunning()) {
            this.H.cancel();
        }
        setProgress(z2 ? 1.0f : 0.0f);
        invalidate();
    }

    public void setCheckedImmediatelyNoEvent(boolean z2) {
        if (this.ga == null) {
            setCheckedImmediately(z2);
            return;
        }
        super.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
        setCheckedImmediately(z2);
        super.setOnCheckedChangeListener(this.ga);
    }

    public void setCheckedNoEvent(boolean z2) {
        if (this.ga == null) {
            setChecked(z2);
            return;
        }
        super.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
        setChecked(z2);
        super.setOnCheckedChangeListener(this.ga);
    }

    public void setDrawDebugRect(boolean z2) {
        this.G = z2;
        invalidate();
    }

    public void setFadeBack(boolean z2) {
        this.l = z2;
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        super.setOnCheckedChangeListener(onCheckedChangeListener);
        this.ga = onCheckedChangeListener;
    }

    public void setTextAdjust(int i2) {
        this.ca = i2;
        this.ea = false;
        requestLayout();
        invalidate();
    }

    public void setTextExtra(int i2) {
        this.ba = i2;
        this.ea = false;
        requestLayout();
        invalidate();
    }

    public void setTextThumbInset(int i2) {
        this.aa = i2;
        this.ea = false;
        requestLayout();
        invalidate();
    }

    public void setThumbColor(ColorStateList colorStateList) {
        this.f = colorStateList;
        if (this.f != null) {
            setThumbDrawable((Drawable) null);
        }
        invalidate();
    }

    public void setThumbColorRes(int i2) {
        setThumbColor(ContextCompat.getColorStateList(getContext(), i2));
    }

    public void setThumbDrawable(Drawable drawable) {
        this.f5383c = drawable;
        this.E = this.f5383c != null;
        refreshDrawableState();
        this.ea = false;
        requestLayout();
        invalidate();
    }

    public void setThumbDrawableRes(int i2) {
        setThumbDrawable(ContextCompat.getDrawable(getContext(), i2));
    }

    public void setThumbMargin(RectF rectF) {
        if (rectF == null) {
            a(0.0f, 0.0f, 0.0f, 0.0f);
        } else {
            a(rectF.left, rectF.top, rectF.right, rectF.bottom);
        }
    }

    public void setThumbRadius(float f2) {
        this.g = f2;
        if (!this.E) {
            invalidate();
        }
    }

    public void setThumbRangeRatio(float f2) {
        this.j = f2;
        this.ea = false;
        requestLayout();
    }

    public void setTintColor(int i2) {
        this.m = i2;
        this.f = b(this.m);
        this.e = a(this.m);
        this.F = false;
        this.E = false;
        refreshDrawableState();
        invalidate();
    }
}
