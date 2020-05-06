package androidx.appcompat.widget;

import a.a.a;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RatingBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/* renamed from: androidx.appcompat.widget.w  reason: case insensitive filesystem */
public class C0119w extends RatingBar {

    /* renamed from: a  reason: collision with root package name */
    private final C0118v f672a;

    public C0119w(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, a.ratingBarStyle);
    }

    public C0119w(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        qa.a((View) this, getContext());
        this.f672a = new C0118v(this);
        this.f672a.a(attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public synchronized void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        Bitmap a2 = this.f672a.a();
        if (a2 != null) {
            setMeasuredDimension(View.resolveSizeAndState(a2.getWidth() * getNumStars(), i, 0), getMeasuredHeight());
        }
    }
}
