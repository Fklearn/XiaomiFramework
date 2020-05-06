package com.miui.common.customview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import com.miui.networkassistant.utils.TypefaceHelper;
import java.util.Locale;

public class ScoreTextView extends TextView {

    /* renamed from: a  reason: collision with root package name */
    private final int f3792a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public int f3793b;

    /* renamed from: c  reason: collision with root package name */
    private ObjectAnimator f3794c;

    /* renamed from: d  reason: collision with root package name */
    private int f3795d;
    private boolean e;
    private Context f;
    private Typeface g;

    public ScoreTextView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ScoreTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f3792a = 650;
        this.f3793b = -1;
        this.f3795d = -1;
        this.f = context;
        this.g = TypefaceHelper.getMiuiDemiBoldCondensed(context);
        setTypeface(this.g);
    }

    private synchronized void a(int i, int i2) {
        if (this.f3794c != null) {
            this.f3794c.cancel();
            this.f3794c = null;
        }
        this.f3794c = ObjectAnimator.ofInt(this, "FlipScore", new int[]{i, i2});
        this.f3794c.setDuration(650);
        this.f3794c.setInterpolator(new DecelerateInterpolator());
        this.f3794c.addListener(new f(this));
        this.f3794c.start();
    }

    /* access modifiers changed from: private */
    public void a(CharSequence charSequence) {
        setText(charSequence);
    }

    public int getFlipScore() {
        return this.f3795d;
    }

    public int getTextScore() {
        return this.f3793b;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.e && getLayout() != null) {
            canvas.translate(0.0f, (float) (-getLayout().getLineDescent(0)));
        }
        super.onDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.e) {
            setMeasuredDimension(getMeasuredWidth(), getLayout().getHeight());
        }
    }

    public void setFlipScore(int i) {
        if (this.f3795d != i) {
            this.f3795d = i;
            a((CharSequence) String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void setNoPaddings(boolean z) {
        this.e = z;
    }

    public void setNumber(int i) {
        int i2 = this.f3793b;
        if (i2 != -1 && i2 != i) {
            a(i2, i);
        } else if (this.f3793b != i) {
            a((CharSequence) String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i)}));
        }
        this.f3793b = i;
    }

    public void setScore(int i) {
        int i2 = this.f3793b;
        if (i2 != -1 && i2 != i) {
            a(i2, i);
        } else if (this.f3793b != i) {
            a((CharSequence) String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i)}));
        }
        this.f3793b = i;
    }
}
