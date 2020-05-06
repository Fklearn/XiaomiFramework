package androidx.appcompat.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/* renamed from: androidx.appcompat.widget.b  reason: case insensitive filesystem */
class C0089b extends Drawable {

    /* renamed from: a  reason: collision with root package name */
    final ActionBarContainer f585a;

    public C0089b(ActionBarContainer actionBarContainer) {
        this.f585a = actionBarContainer;
    }

    public void draw(Canvas canvas) {
        ActionBarContainer actionBarContainer = this.f585a;
        if (actionBarContainer.h) {
            Drawable drawable = actionBarContainer.g;
            if (drawable != null) {
                drawable.draw(canvas);
                return;
            }
            return;
        }
        Drawable drawable2 = actionBarContainer.e;
        if (drawable2 != null) {
            drawable2.draw(canvas);
        }
        ActionBarContainer actionBarContainer2 = this.f585a;
        Drawable drawable3 = actionBarContainer2.f;
        if (drawable3 != null && actionBarContainer2.i) {
            drawable3.draw(canvas);
        }
    }

    public int getOpacity() {
        return 0;
    }

    @RequiresApi(21)
    public void getOutline(@NonNull Outline outline) {
        Drawable drawable;
        ActionBarContainer actionBarContainer = this.f585a;
        if (actionBarContainer.h) {
            drawable = actionBarContainer.g;
            if (drawable == null) {
                return;
            }
        } else {
            drawable = actionBarContainer.e;
            if (drawable == null) {
                return;
            }
        }
        drawable.getOutline(outline);
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }
}
