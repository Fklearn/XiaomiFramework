package com.miui.optimizecenter.widget.storage;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;
import com.miui.securitycenter.Application;
import java.lang.ref.WeakReference;
import miui.text.ExtraTextUtils;

@SuppressLint({"AppCompatCustomView"})
public class SizeTextView extends TextView {

    /* renamed from: a  reason: collision with root package name */
    private ValueAnimator f5815a;

    /* renamed from: b  reason: collision with root package name */
    private long f5816b;

    /* renamed from: c  reason: collision with root package name */
    private long f5817c;

    public static class a implements ValueAnimator.AnimatorUpdateListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<SizeTextView> f5818a;

        /* renamed from: b  reason: collision with root package name */
        private long f5819b;

        /* renamed from: c  reason: collision with root package name */
        private long f5820c;

        public a(SizeTextView sizeTextView, long j, long j2) {
            this.f5818a = new WeakReference<>(sizeTextView);
            this.f5819b = j;
            this.f5820c = j2;
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            SizeTextView sizeTextView;
            Context applicationContext = Application.d().getApplicationContext();
            WeakReference<SizeTextView> weakReference = this.f5818a;
            if (weakReference != null && (sizeTextView = (SizeTextView) weakReference.get()) != null) {
                long j = this.f5819b;
                long floatValue = j + ((long) (((float) (this.f5820c - j)) * ((Float) valueAnimator.getAnimatedValue()).floatValue()));
                sizeTextView.setCurrentSize(floatValue);
                sizeTextView.setText(ExtraTextUtils.formatFileSize(applicationContext, floatValue));
            }
        }
    }

    public SizeTextView(Context context) {
        this(context, (AttributeSet) null);
    }

    public SizeTextView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SizeTextView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setText(ExtraTextUtils.formatFileSize(context, 0));
    }

    public void setCurrentSize(long j) {
        this.f5816b = j;
    }

    public void setSize(long j) {
        if (this.f5817c != j) {
            this.f5817c = j;
            ValueAnimator valueAnimator = this.f5815a;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.f5815a = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.f5815a.setDuration(700);
            this.f5815a.addUpdateListener(new a(this, this.f5816b, this.f5817c));
            this.f5815a.start();
        }
    }
}
