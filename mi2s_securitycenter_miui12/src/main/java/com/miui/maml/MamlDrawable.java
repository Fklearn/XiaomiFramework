package com.miui.maml;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import java.lang.ref.WeakReference;

public class MamlDrawable extends Drawable {
    private static WeakReference<Drawable> sLayerBadgeDrawableBmpRef;
    protected Drawable mBadgeDrawable;
    protected Rect mBadgeLocation;
    protected ColorFilter mColorFilter;
    protected int mHeight;
    protected int mIntrinsicHeight;
    protected int mIntrinsicWidth;
    protected Runnable mInvalidateSelf = new Runnable() {
        public void run() {
            MamlDrawable.this.invalidateSelf();
        }
    };
    protected MamlDrawableState mState;
    protected int mWidth;

    public static class MamlDrawableState extends Drawable.ConstantState {
        protected Drawable mStateBadgeDrawable;
        protected Rect mStateBadgeLocation;

        /* access modifiers changed from: protected */
        public MamlDrawable createDrawable() {
            return null;
        }

        public int getChangingConfigurations() {
            return 0;
        }

        public Drawable newDrawable() {
            MamlDrawable createDrawable = createDrawable();
            Rect rect = null;
            if (createDrawable == null) {
                return null;
            }
            Drawable drawable = this.mStateBadgeDrawable;
            Drawable mutate = drawable != null ? drawable.mutate() : null;
            Rect rect2 = this.mStateBadgeLocation;
            if (rect2 != null) {
                rect = new Rect(rect2.left, rect2.top, rect2.right, rect2.bottom);
            }
            createDrawable.setBadgeInfo(mutate, rect);
            return createDrawable;
        }
    }

    public void cleanUp() {
    }

    public void draw(Canvas canvas) {
        drawIcon(canvas);
        try {
            if (this.mBadgeDrawable == null) {
                return;
            }
            if (this.mBadgeLocation != null) {
                this.mBadgeDrawable.setBounds(0, 0, this.mBadgeLocation.width(), this.mBadgeLocation.height());
                canvas.save();
                canvas.translate((float) this.mBadgeLocation.left, (float) this.mBadgeLocation.top);
                this.mBadgeDrawable.draw(canvas);
                canvas.restore();
                return;
            }
            this.mBadgeDrawable.setBounds(0, 0, this.mIntrinsicWidth, this.mIntrinsicHeight);
            this.mBadgeDrawable.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void drawIcon(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        cleanUp();
        super.finalize();
    }

    public Drawable.ConstantState getConstantState() {
        return this.mState;
    }

    public int getIntrinsicHeight() {
        return this.mIntrinsicHeight;
    }

    public int getIntrinsicWidth() {
        return this.mIntrinsicWidth;
    }

    public int getOpacity() {
        return -3;
    }

    public void setAlpha(int i) {
    }

    public void setBadgeInfo(Drawable drawable, Rect rect) {
        if (rect == null || (rect.left >= 0 && rect.top >= 0 && rect.width() <= this.mIntrinsicWidth && rect.height() <= this.mIntrinsicHeight)) {
            if (drawable instanceof LayerDrawable) {
                Drawable drawable2 = null;
                WeakReference<Drawable> weakReference = sLayerBadgeDrawableBmpRef;
                if (weakReference != null) {
                    drawable2 = (Drawable) weakReference.get();
                }
                if (drawable2 != null) {
                    drawable = drawable2.mutate();
                } else {
                    Bitmap createBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(createBitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);
                    drawable = new BitmapDrawable(createBitmap);
                    sLayerBadgeDrawableBmpRef = new WeakReference<>(drawable);
                }
            }
            ColorFilter colorFilter = this.mColorFilter;
            if (!(colorFilter == null || drawable == null)) {
                drawable.setColorFilter(colorFilter);
            }
            this.mBadgeDrawable = drawable;
            this.mBadgeLocation = rect;
            MamlDrawableState mamlDrawableState = this.mState;
            mamlDrawableState.mStateBadgeDrawable = drawable;
            mamlDrawableState.mStateBadgeLocation = rect;
            return;
        }
        throw new IllegalArgumentException("Badge location " + rect + " not in badged drawable bounds " + new Rect(0, 0, this.mIntrinsicWidth, this.mIntrinsicHeight));
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        this.mWidth = i3 - i;
        this.mHeight = i4 - i2;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mColorFilter = colorFilter;
    }

    public void setIntrinsicSize(int i, int i2) {
        this.mIntrinsicWidth = i;
        this.mIntrinsicHeight = i2;
    }
}
