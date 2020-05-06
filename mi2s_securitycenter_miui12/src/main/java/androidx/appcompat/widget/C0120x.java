package androidx.appcompat.widget;

import a.a.a;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/* renamed from: androidx.appcompat.widget.x  reason: case insensitive filesystem */
public class C0120x extends SeekBar {

    /* renamed from: a  reason: collision with root package name */
    private final C0121y f674a;

    public C0120x(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, a.seekBarStyle);
    }

    public C0120x(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        qa.a((View) this, getContext());
        this.f674a = new C0121y(this);
        this.f674a.a(attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        this.f674a.b();
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        this.f674a.c();
    }

    /* access modifiers changed from: protected */
    public synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.f674a.a(canvas);
    }
}
