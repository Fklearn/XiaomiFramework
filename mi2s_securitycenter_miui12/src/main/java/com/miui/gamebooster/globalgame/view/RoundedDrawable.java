package com.miui.gamebooster.globalgame.view;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import java.util.HashSet;

@Keep
public class RoundedDrawable extends Drawable {
    public static final int DEFAULT_BORDER_COLOR = -16777216;
    public static final String TAG = "RoundedDrawable";
    private final Bitmap mBitmap;
    private final int mBitmapHeight;
    private final Paint mBitmapPaint;
    private final RectF mBitmapRect = new RectF();
    private final int mBitmapWidth;
    private ColorStateList mBorderColor;
    private final Paint mBorderPaint;
    private final RectF mBorderRect = new RectF();
    private float mBorderWidth;
    private final RectF mBounds = new RectF();
    private float mCornerRadius;
    private final boolean[] mCornersRounded;
    private final RectF mDrawableRect = new RectF();
    private boolean mOval;
    private boolean mRebuildShader;
    private ImageView.ScaleType mScaleType;
    private final Matrix mShaderMatrix = new Matrix();
    private final RectF mSquareCornersRect = new RectF();
    private Shader.TileMode mTileModeX;
    private Shader.TileMode mTileModeY;

    public RoundedDrawable(Bitmap bitmap) {
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        this.mTileModeX = tileMode;
        this.mTileModeY = tileMode;
        this.mRebuildShader = true;
        this.mCornerRadius = 0.0f;
        this.mCornersRounded = new boolean[]{true, true, true, true};
        this.mOval = false;
        this.mBorderWidth = 0.0f;
        this.mBorderColor = ColorStateList.valueOf(DEFAULT_BORDER_COLOR);
        this.mScaleType = ImageView.ScaleType.FIT_CENTER;
        this.mBitmap = bitmap;
        this.mBitmapWidth = bitmap.getWidth();
        this.mBitmapHeight = bitmap.getHeight();
        this.mBitmapRect.set(0.0f, 0.0f, (float) this.mBitmapWidth, (float) this.mBitmapHeight);
        this.mBitmapPaint = new Paint();
        this.mBitmapPaint.setStyle(Paint.Style.FILL);
        this.mBitmapPaint.setAntiAlias(true);
        this.mBorderPaint = new Paint();
        this.mBorderPaint.setStyle(Paint.Style.STROKE);
        this.mBorderPaint.setAntiAlias(true);
        this.mBorderPaint.setColor(this.mBorderColor.getColorForState(getState(), DEFAULT_BORDER_COLOR));
        this.mBorderPaint.setStrokeWidth(this.mBorderWidth);
    }

    private static boolean all(boolean[] zArr) {
        for (boolean z : zArr) {
            if (z) {
                return false;
            }
        }
        return true;
    }

    private static boolean any(boolean[] zArr) {
        for (boolean z : zArr) {
            if (z) {
                return true;
            }
        }
        return false;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap createBitmap = Bitmap.createBitmap(Math.max(drawable.getIntrinsicWidth(), 2), Math.max(drawable.getIntrinsicHeight(), 2), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return createBitmap;
        } catch (Throwable th) {
            th.printStackTrace();
            Log.w(TAG, "Failed to create bitmap from drawable!");
            return null;
        }
    }

    public static RoundedDrawable fromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            return new RoundedDrawable(bitmap);
        }
        return null;
    }

    public static Drawable fromDrawable(Drawable drawable) {
        if (drawable == null || (drawable instanceof RoundedDrawable)) {
            return drawable;
        }
        if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            int numberOfLayers = layerDrawable.getNumberOfLayers();
            for (int i = 0; i < numberOfLayers; i++) {
                layerDrawable.setDrawableByLayerId(layerDrawable.getId(i), fromDrawable(layerDrawable.getDrawable(i)));
            }
            return layerDrawable;
        }
        Bitmap drawableToBitmap = drawableToBitmap(drawable);
        return drawableToBitmap != null ? new RoundedDrawable(drawableToBitmap) : drawable;
    }

    private static boolean only(int i, boolean[] zArr) {
        int length = zArr.length;
        int i2 = 0;
        while (true) {
            boolean z = true;
            if (i2 >= length) {
                return true;
            }
            boolean z2 = zArr[i2];
            if (i2 != i) {
                z = false;
            }
            if (z2 != z) {
                return false;
            }
            i2++;
        }
    }

    private void redrawBitmapForSquareCorners(Canvas canvas) {
        if (!all(this.mCornersRounded) && this.mCornerRadius != 0.0f) {
            RectF rectF = this.mDrawableRect;
            float f = rectF.left;
            float f2 = rectF.top;
            float width = rectF.width() + f;
            float height = this.mDrawableRect.height() + f2;
            float f3 = this.mCornerRadius;
            if (!this.mCornersRounded[0]) {
                this.mSquareCornersRect.set(f, f2, f + f3, f2 + f3);
                canvas.drawRect(this.mSquareCornersRect, this.mBitmapPaint);
            }
            if (!this.mCornersRounded[1]) {
                this.mSquareCornersRect.set(width - f3, f2, width, f3);
                canvas.drawRect(this.mSquareCornersRect, this.mBitmapPaint);
            }
            if (!this.mCornersRounded[2]) {
                this.mSquareCornersRect.set(width - f3, height - f3, width, height);
                canvas.drawRect(this.mSquareCornersRect, this.mBitmapPaint);
            }
            if (!this.mCornersRounded[3]) {
                this.mSquareCornersRect.set(f, height - f3, f3 + f, height);
                canvas.drawRect(this.mSquareCornersRect, this.mBitmapPaint);
            }
        }
    }

    private void redrawBorderForSquareCorners(Canvas canvas) {
        float f;
        if (!all(this.mCornersRounded) && this.mCornerRadius != 0.0f) {
            RectF rectF = this.mDrawableRect;
            float f2 = rectF.left;
            float f3 = rectF.top;
            float width = rectF.width() + f2;
            float height = f3 + this.mDrawableRect.height();
            float f4 = this.mCornerRadius;
            float f5 = this.mBorderWidth / 2.0f;
            if (!this.mCornersRounded[0]) {
                Canvas canvas2 = canvas;
                canvas2.drawLine(f2 - f5, f3, f2 + f4, f3, this.mBorderPaint);
                canvas2.drawLine(f2, f3 - f5, f2, f3 + f4, this.mBorderPaint);
            }
            if (!this.mCornersRounded[1]) {
                Canvas canvas3 = canvas;
                float f6 = width;
                canvas3.drawLine((width - f4) - f5, f3, f6, f3, this.mBorderPaint);
                canvas3.drawLine(width, f3 - f5, f6, f3 + f4, this.mBorderPaint);
            }
            if (!this.mCornersRounded[2]) {
                f = f4;
                canvas.drawLine((width - f4) - f5, height, width + f5, height, this.mBorderPaint);
                canvas.drawLine(width, height - f, width, height, this.mBorderPaint);
            } else {
                f = f4;
            }
            if (!this.mCornersRounded[3]) {
                canvas.drawLine(f2 - f5, height, f2 + f, height, this.mBorderPaint);
                canvas.drawLine(f2, height - f, f2, height, this.mBorderPaint);
            }
        }
    }

    private void updateShaderMatrix() {
        float f;
        float f2;
        Matrix matrix;
        RectF rectF;
        RectF rectF2;
        Matrix.ScaleToFit scaleToFit;
        int i = d.f4431a[this.mScaleType.ordinal()];
        if (i == 1) {
            this.mBorderRect.set(this.mBounds);
            RectF rectF3 = this.mBorderRect;
            float f3 = this.mBorderWidth;
            rectF3.inset(f3 / 2.0f, f3 / 2.0f);
            this.mShaderMatrix.reset();
            this.mShaderMatrix.setTranslate((float) ((int) (((this.mBorderRect.width() - ((float) this.mBitmapWidth)) * 0.5f) + 0.5f)), (float) ((int) (((this.mBorderRect.height() - ((float) this.mBitmapHeight)) * 0.5f) + 0.5f)));
        } else if (i != 2) {
            if (i != 3) {
                if (i == 5) {
                    this.mBorderRect.set(this.mBitmapRect);
                    matrix = this.mShaderMatrix;
                    rectF = this.mBitmapRect;
                    rectF2 = this.mBounds;
                    scaleToFit = Matrix.ScaleToFit.END;
                } else if (i == 6) {
                    this.mBorderRect.set(this.mBitmapRect);
                    matrix = this.mShaderMatrix;
                    rectF = this.mBitmapRect;
                    rectF2 = this.mBounds;
                    scaleToFit = Matrix.ScaleToFit.START;
                } else if (i != 7) {
                    this.mBorderRect.set(this.mBitmapRect);
                    matrix = this.mShaderMatrix;
                    rectF = this.mBitmapRect;
                    rectF2 = this.mBounds;
                    scaleToFit = Matrix.ScaleToFit.CENTER;
                } else {
                    this.mBorderRect.set(this.mBounds);
                    RectF rectF4 = this.mBorderRect;
                    float f4 = this.mBorderWidth;
                    rectF4.inset(f4 / 2.0f, f4 / 2.0f);
                    this.mShaderMatrix.reset();
                    this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBorderRect, Matrix.ScaleToFit.FILL);
                }
                matrix.setRectToRect(rectF, rectF2, scaleToFit);
            } else {
                this.mShaderMatrix.reset();
                float min = (((float) this.mBitmapWidth) > this.mBounds.width() || ((float) this.mBitmapHeight) > this.mBounds.height()) ? Math.min(this.mBounds.width() / ((float) this.mBitmapWidth), this.mBounds.height() / ((float) this.mBitmapHeight)) : 1.0f;
                this.mShaderMatrix.setScale(min, min);
                this.mShaderMatrix.postTranslate((float) ((int) (((this.mBounds.width() - (((float) this.mBitmapWidth) * min)) * 0.5f) + 0.5f)), (float) ((int) (((this.mBounds.height() - (((float) this.mBitmapHeight) * min)) * 0.5f) + 0.5f)));
                this.mBorderRect.set(this.mBitmapRect);
            }
            this.mShaderMatrix.mapRect(this.mBorderRect);
            RectF rectF5 = this.mBorderRect;
            float f5 = this.mBorderWidth;
            rectF5.inset(f5 / 2.0f, f5 / 2.0f);
            this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBorderRect, Matrix.ScaleToFit.FILL);
        } else {
            this.mBorderRect.set(this.mBounds);
            RectF rectF6 = this.mBorderRect;
            float f6 = this.mBorderWidth;
            rectF6.inset(f6 / 2.0f, f6 / 2.0f);
            this.mShaderMatrix.reset();
            float f7 = 0.0f;
            if (((float) this.mBitmapWidth) * this.mBorderRect.height() > this.mBorderRect.width() * ((float) this.mBitmapHeight)) {
                f2 = this.mBorderRect.height() / ((float) this.mBitmapHeight);
                f = 0.0f;
                f7 = (this.mBorderRect.width() - (((float) this.mBitmapWidth) * f2)) * 0.5f;
            } else {
                f2 = this.mBorderRect.width() / ((float) this.mBitmapWidth);
                f = (this.mBorderRect.height() - (((float) this.mBitmapHeight) * f2)) * 0.5f;
            }
            this.mShaderMatrix.setScale(f2, f2);
            Matrix matrix2 = this.mShaderMatrix;
            float f8 = this.mBorderWidth;
            matrix2.postTranslate(((float) ((int) (f7 + 0.5f))) + (f8 / 2.0f), ((float) ((int) (f + 0.5f))) + (f8 / 2.0f));
        }
        this.mDrawableRect.set(this.mBorderRect);
        this.mRebuildShader = true;
    }

    public void draw(@NonNull Canvas canvas) {
        Paint paint;
        RectF rectF;
        if (this.mRebuildShader) {
            BitmapShader bitmapShader = new BitmapShader(this.mBitmap, this.mTileModeX, this.mTileModeY);
            Shader.TileMode tileMode = this.mTileModeX;
            Shader.TileMode tileMode2 = Shader.TileMode.CLAMP;
            if (tileMode == tileMode2 && this.mTileModeY == tileMode2) {
                bitmapShader.setLocalMatrix(this.mShaderMatrix);
            }
            this.mBitmapPaint.setShader(bitmapShader);
            this.mRebuildShader = false;
        }
        if (this.mOval) {
            if (this.mBorderWidth > 0.0f) {
                canvas.drawOval(this.mDrawableRect, this.mBitmapPaint);
                rectF = this.mBorderRect;
                paint = this.mBorderPaint;
            } else {
                rectF = this.mDrawableRect;
                paint = this.mBitmapPaint;
            }
            canvas.drawOval(rectF, paint);
        } else if (any(this.mCornersRounded)) {
            float f = this.mCornerRadius;
            if (this.mBorderWidth > 0.0f) {
                canvas.drawRoundRect(this.mDrawableRect, f, f, this.mBitmapPaint);
                canvas.drawRoundRect(this.mBorderRect, f, f, this.mBorderPaint);
                redrawBitmapForSquareCorners(canvas);
                redrawBorderForSquareCorners(canvas);
                return;
            }
            canvas.drawRoundRect(this.mDrawableRect, f, f, this.mBitmapPaint);
            redrawBitmapForSquareCorners(canvas);
        } else {
            canvas.drawRect(this.mDrawableRect, this.mBitmapPaint);
            if (this.mBorderWidth > 0.0f) {
                canvas.drawRect(this.mBorderRect, this.mBorderPaint);
            }
        }
    }

    public int getAlpha() {
        return this.mBitmapPaint.getAlpha();
    }

    public int getBorderColor() {
        return this.mBorderColor.getDefaultColor();
    }

    public ColorStateList getBorderColors() {
        return this.mBorderColor;
    }

    public float getBorderWidth() {
        return this.mBorderWidth;
    }

    public ColorFilter getColorFilter() {
        return this.mBitmapPaint.getColorFilter();
    }

    public float getCornerRadius() {
        return this.mCornerRadius;
    }

    public float getCornerRadius(int i) {
        if (this.mCornersRounded[i]) {
            return this.mCornerRadius;
        }
        return 0.0f;
    }

    public int getIntrinsicHeight() {
        return this.mBitmapHeight;
    }

    public int getIntrinsicWidth() {
        return this.mBitmapWidth;
    }

    public int getOpacity() {
        return -3;
    }

    public ImageView.ScaleType getScaleType() {
        return this.mScaleType;
    }

    public Bitmap getSourceBitmap() {
        return this.mBitmap;
    }

    public Shader.TileMode getTileModeX() {
        return this.mTileModeX;
    }

    public Shader.TileMode getTileModeY() {
        return this.mTileModeY;
    }

    public boolean isOval() {
        return this.mOval;
    }

    public boolean isStateful() {
        return this.mBorderColor.isStateful();
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(@NonNull Rect rect) {
        super.onBoundsChange(rect);
        this.mBounds.set(rect);
        updateShaderMatrix();
    }

    /* access modifiers changed from: protected */
    public boolean onStateChange(int[] iArr) {
        int colorForState = this.mBorderColor.getColorForState(iArr, 0);
        if (this.mBorderPaint.getColor() == colorForState) {
            return super.onStateChange(iArr);
        }
        this.mBorderPaint.setColor(colorForState);
        return true;
    }

    public void setAlpha(int i) {
        this.mBitmapPaint.setAlpha(i);
        invalidateSelf();
    }

    public RoundedDrawable setBorderColor(@ColorInt int i) {
        return setBorderColor(ColorStateList.valueOf(i));
    }

    public RoundedDrawable setBorderColor(ColorStateList colorStateList) {
        if (colorStateList == null) {
            colorStateList = ColorStateList.valueOf(0);
        }
        this.mBorderColor = colorStateList;
        this.mBorderPaint.setColor(this.mBorderColor.getColorForState(getState(), DEFAULT_BORDER_COLOR));
        return this;
    }

    public RoundedDrawable setBorderWidth(float f) {
        this.mBorderWidth = f;
        this.mBorderPaint.setStrokeWidth(this.mBorderWidth);
        return this;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mBitmapPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    public RoundedDrawable setCornerRadius(float f) {
        setCornerRadius(f, f, f, f);
        return this;
    }

    public RoundedDrawable setCornerRadius(float f, float f2, float f3, float f4) {
        HashSet hashSet = new HashSet(4);
        hashSet.add(Float.valueOf(f));
        hashSet.add(Float.valueOf(f2));
        hashSet.add(Float.valueOf(f3));
        hashSet.add(Float.valueOf(f4));
        hashSet.remove(Float.valueOf(0.0f));
        if (hashSet.size() <= 1) {
            if (!hashSet.isEmpty()) {
                float floatValue = ((Float) hashSet.iterator().next()).floatValue();
                if (Float.isInfinite(floatValue) || Float.isNaN(floatValue) || floatValue < 0.0f) {
                    throw new IllegalArgumentException("Invalid radius value: " + floatValue);
                }
                this.mCornerRadius = floatValue;
            } else {
                this.mCornerRadius = 0.0f;
            }
            boolean z = false;
            this.mCornersRounded[0] = f > 0.0f;
            this.mCornersRounded[1] = f2 > 0.0f;
            this.mCornersRounded[2] = f3 > 0.0f;
            boolean[] zArr = this.mCornersRounded;
            if (f4 > 0.0f) {
                z = true;
            }
            zArr[3] = z;
            return this;
        }
        throw new IllegalArgumentException("Multiple nonzero corner radii not yet supported.");
    }

    public RoundedDrawable setCornerRadius(int i, float f) {
        int i2 = (f > 0.0f ? 1 : (f == 0.0f ? 0 : -1));
        if (i2 != 0) {
            float f2 = this.mCornerRadius;
            if (!(f2 == 0.0f || f2 == f)) {
                throw new IllegalArgumentException("Multiple nonzero corner radii not yet supported.");
            }
        }
        if (i2 == 0) {
            if (only(i, this.mCornersRounded)) {
                this.mCornerRadius = 0.0f;
            }
            this.mCornersRounded[i] = false;
        } else {
            if (this.mCornerRadius == 0.0f) {
                this.mCornerRadius = f;
            }
            this.mCornersRounded[i] = true;
        }
        return this;
    }

    public void setDither(boolean z) {
        this.mBitmapPaint.setDither(z);
        invalidateSelf();
    }

    public void setFilterBitmap(boolean z) {
        this.mBitmapPaint.setFilterBitmap(z);
        invalidateSelf();
    }

    public RoundedDrawable setOval(boolean z) {
        this.mOval = z;
        return this;
    }

    public RoundedDrawable setScaleType(ImageView.ScaleType scaleType) {
        if (scaleType == null) {
            scaleType = ImageView.ScaleType.FIT_CENTER;
        }
        if (this.mScaleType != scaleType) {
            this.mScaleType = scaleType;
            updateShaderMatrix();
        }
        return this;
    }

    public RoundedDrawable setTileModeX(Shader.TileMode tileMode) {
        if (this.mTileModeX != tileMode) {
            this.mTileModeX = tileMode;
            this.mRebuildShader = true;
            invalidateSelf();
        }
        return this;
    }

    public RoundedDrawable setTileModeY(Shader.TileMode tileMode) {
        if (this.mTileModeY != tileMode) {
            this.mTileModeY = tileMode;
            this.mRebuildShader = true;
            invalidateSelf();
        }
        return this;
    }

    public Bitmap toBitmap() {
        return drawableToBitmap(this);
    }
}
