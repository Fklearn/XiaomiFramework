package com.miui.gamebooster.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.miui.gamebooster.model.p;

/* renamed from: com.miui.gamebooster.customview.y  reason: case insensitive filesystem */
public class C0355y extends ImageView {

    /* renamed from: a  reason: collision with root package name */
    private a f4240a = a.NORMAL;

    /* renamed from: b  reason: collision with root package name */
    private p f4241b;

    /* renamed from: com.miui.gamebooster.customview.y$a */
    enum a {
        LOW,
        NORMAL
    }

    public C0355y(Context context) {
        super(context);
    }

    public C0355y(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public p getImage() {
        return this.f4241b;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.f4240a == a.LOW) {
            setImageBitmap((Bitmap) null);
        }
    }

    public void setImage(p pVar) {
        this.f4241b = pVar;
    }

    public void setWeight(a aVar) {
        this.f4240a = aVar;
    }
}
