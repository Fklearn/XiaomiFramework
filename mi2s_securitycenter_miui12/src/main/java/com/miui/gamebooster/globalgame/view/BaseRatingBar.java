package com.miui.gamebooster.globalgame.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.miui.securitycenter.i;
import java.util.ArrayList;
import java.util.List;

@Keep
public class BaseRatingBar extends LinearLayout implements g {
    public static final String TAG = "SimpleRatingBar";
    private boolean mClearRatingEnabled;
    private Drawable mEmptyDrawable;
    private Drawable mFilledDrawable;
    private boolean mIsClickable;
    private boolean mIsIndicator;
    private boolean mIsScrollable;
    private float mMinimumStars;
    private int mNumStars;
    private a mOnRatingChangeListener;
    private int mPadding;
    protected List<b> mPartialViews;
    private float mPreviousRating;
    private float mRating;
    private int mStarHeight;
    private int mStarWidth;
    private float mStartX;
    private float mStartY;
    private float mStepSize;

    public interface a {
        void a(BaseRatingBar baseRatingBar, float f);
    }

    public BaseRatingBar(Context context) {
        this(context, (AttributeSet) null);
    }

    public BaseRatingBar(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BaseRatingBar(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mPadding = 20;
        this.mMinimumStars = 0.0f;
        this.mRating = -1.0f;
        this.mStepSize = 1.0f;
        this.mPreviousRating = 0.0f;
        this.mIsIndicator = false;
        this.mIsScrollable = true;
        this.mIsClickable = true;
        this.mClearRatingEnabled = true;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.BaseRatingBar);
        float f = obtainStyledAttributes.getFloat(7, 0.0f);
        initParamsValue(obtainStyledAttributes, context);
        verifyParamsValue();
        initRatingView();
        setRating(f);
    }

    private b getPartialView(int i, int i2, int i3, int i4, Drawable drawable, Drawable drawable2) {
        b bVar = new b(getContext(), i, i2, i3, i4);
        bVar.setFilledDrawable(drawable);
        bVar.setEmptyDrawable(drawable2);
        return bVar;
    }

    private void handleClickEvent(float f) {
        for (b next : this.mPartialViews) {
            if (isPositionInRatingView(f, next)) {
                float f2 = this.mStepSize;
                float intValue = f2 == 1.0f ? (float) ((Integer) next.getTag()).intValue() : c.a(next, f2, f);
                if (this.mPreviousRating == intValue && isClearRatingEnabled()) {
                    intValue = this.mMinimumStars;
                }
                setRating(intValue);
                return;
            }
        }
    }

    private void handleMoveEvent(float f) {
        for (b next : this.mPartialViews) {
            if (f < (((float) next.getWidth()) / 10.0f) + (this.mMinimumStars * ((float) next.getWidth()))) {
                setRating(this.mMinimumStars);
                return;
            } else if (isPositionInRatingView(f, next)) {
                float a2 = c.a(next, this.mStepSize, f);
                if (this.mRating != a2) {
                    setRating(a2);
                }
            }
        }
    }

    private void initParamsValue(TypedArray typedArray, Context context) {
        this.mNumStars = typedArray.getInt(6, this.mNumStars);
        this.mStepSize = typedArray.getFloat(12, this.mStepSize);
        this.mMinimumStars = typedArray.getFloat(5, this.mMinimumStars);
        this.mPadding = typedArray.getDimensionPixelSize(10, this.mPadding);
        this.mStarWidth = typedArray.getDimensionPixelSize(11, 0);
        this.mStarHeight = typedArray.getDimensionPixelSize(9, 0);
        Drawable drawable = null;
        this.mEmptyDrawable = typedArray.hasValue(2) ? context.getResources().getDrawable(typedArray.getResourceId(2, -1)) : null;
        if (typedArray.hasValue(3)) {
            drawable = context.getResources().getDrawable(typedArray.getResourceId(3, -1));
        }
        this.mFilledDrawable = drawable;
        this.mIsIndicator = typedArray.getBoolean(4, this.mIsIndicator);
        this.mIsScrollable = typedArray.getBoolean(8, this.mIsScrollable);
        this.mIsClickable = typedArray.getBoolean(1, this.mIsClickable);
        this.mClearRatingEnabled = typedArray.getBoolean(0, this.mClearRatingEnabled);
        typedArray.recycle();
    }

    private void initRatingView() {
        this.mPartialViews = new ArrayList();
        for (int i = 1; i <= this.mNumStars; i++) {
            b partialView = getPartialView(i, this.mStarWidth, this.mStarHeight, this.mPadding, this.mFilledDrawable, this.mEmptyDrawable);
            addView(partialView);
            this.mPartialViews.add(partialView);
        }
    }

    private boolean isPositionInRatingView(float f, View view) {
        return f > ((float) view.getLeft()) && f < ((float) view.getRight());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0048, code lost:
        if (r0 < 0.1f) goto L_0x0040;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void verifyParamsValue() {
        /*
            r3 = this;
            int r0 = r3.mNumStars
            if (r0 > 0) goto L_0x0007
            r0 = 5
            r3.mNumStars = r0
        L_0x0007:
            int r0 = r3.mPadding
            if (r0 >= 0) goto L_0x000e
            r0 = 0
            r3.mPadding = r0
        L_0x000e:
            android.graphics.drawable.Drawable r0 = r3.mEmptyDrawable
            if (r0 != 0) goto L_0x0023
            android.content.Context r0 = r3.getContext()
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131231583(0x7f08035f, float:1.8079251E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            r3.mEmptyDrawable = r0
        L_0x0023:
            android.graphics.drawable.Drawable r0 = r3.mFilledDrawable
            if (r0 != 0) goto L_0x0038
            android.content.Context r0 = r3.getContext()
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131231584(0x7f080360, float:1.8079253E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            r3.mFilledDrawable = r0
        L_0x0038:
            float r0 = r3.mStepSize
            r1 = 1065353216(0x3f800000, float:1.0)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0043
        L_0x0040:
            r3.mStepSize = r1
            goto L_0x004b
        L_0x0043:
            r1 = 1036831949(0x3dcccccd, float:0.1)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x004b
            goto L_0x0040
        L_0x004b:
            float r0 = r3.mMinimumStars
            int r1 = r3.mNumStars
            float r2 = r3.mStepSize
            float r0 = com.miui.gamebooster.globalgame.view.c.a((float) r0, (int) r1, (float) r2)
            r3.mMinimumStars = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.globalgame.view.BaseRatingBar.verifyParamsValue():void");
    }

    /* access modifiers changed from: protected */
    public void emptyRatingBar() {
        fillRatingBar(0.0f);
    }

    /* access modifiers changed from: protected */
    public void fillRatingBar(float f) {
        for (b next : this.mPartialViews) {
            int i = (((double) ((Integer) next.getTag()).intValue()) > Math.ceil((double) f) ? 1 : (((double) ((Integer) next.getTag()).intValue()) == Math.ceil((double) f) ? 0 : -1));
            if (i > 0) {
                next.a();
            } else if (i == 0) {
                next.setPartialFilled(f);
            } else {
                next.b();
            }
        }
    }

    public int getNumStars() {
        return this.mNumStars;
    }

    public float getRating() {
        return this.mRating;
    }

    public int getStarHeight() {
        return this.mStarHeight;
    }

    public int getStarPadding() {
        return this.mPadding;
    }

    public int getStarWidth() {
        return this.mStarWidth;
    }

    public float getStepSize() {
        return this.mStepSize;
    }

    public boolean isClearRatingEnabled() {
        return this.mClearRatingEnabled;
    }

    public boolean isClickable() {
        return this.mIsClickable;
    }

    public boolean isIndicator() {
        return this.mIsIndicator;
    }

    public boolean isScrollable() {
        return this.mIsScrollable;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        setRating(savedState.getRating());
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.setRating(this.mRating);
        return savedState;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (isIndicator()) {
            return false;
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mStartX = x;
            this.mStartY = y;
            this.mPreviousRating = this.mRating;
        } else if (action != 1) {
            if (action == 2) {
                if (!isScrollable()) {
                    return false;
                }
                handleMoveEvent(x);
            }
        } else if (!c.a(this.mStartX, this.mStartY, motionEvent) || !isClickable()) {
            return false;
        } else {
            handleClickEvent(x);
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        return true;
    }

    public void setClearRatingEnabled(boolean z) {
        this.mClearRatingEnabled = z;
    }

    public void setClickable(boolean z) {
        this.mIsClickable = z;
    }

    public void setEmptyDrawable(Drawable drawable) {
        this.mEmptyDrawable = drawable;
        for (b emptyDrawable : this.mPartialViews) {
            emptyDrawable.setEmptyDrawable(drawable);
        }
    }

    public void setEmptyDrawableRes(@DrawableRes int i) {
        setEmptyDrawable(getContext().getResources().getDrawable(i));
    }

    public void setFilledDrawable(Drawable drawable) {
        this.mFilledDrawable = drawable;
        for (b filledDrawable : this.mPartialViews) {
            filledDrawable.setFilledDrawable(drawable);
        }
    }

    public void setFilledDrawableRes(@DrawableRes int i) {
        setFilledDrawable(getContext().getResources().getDrawable(i));
    }

    public void setIsIndicator(boolean z) {
        this.mIsIndicator = z;
    }

    public void setMinimumStars(@FloatRange(from = 0.0d) float f) {
        this.mMinimumStars = c.a(f, this.mNumStars, this.mStepSize);
    }

    public void setNumStars(int i) {
        if (i > 0) {
            this.mPartialViews.clear();
            removeAllViews();
            this.mNumStars = i;
            initRatingView();
        }
    }

    public void setOnRatingChangeListener(a aVar) {
        this.mOnRatingChangeListener = aVar;
    }

    public void setRating(float f) {
        int i = this.mNumStars;
        if (f > ((float) i)) {
            f = (float) i;
        }
        float f2 = this.mMinimumStars;
        if (f < f2) {
            f = f2;
        }
        if (this.mRating != f) {
            this.mRating = f;
            a aVar = this.mOnRatingChangeListener;
            if (aVar != null) {
                aVar.a(this, this.mRating);
            }
            fillRatingBar(f);
        }
    }

    public void setScrollable(boolean z) {
        this.mIsScrollable = z;
    }

    public void setStarHeight(@IntRange(from = 0) int i) {
        this.mStarHeight = i;
        for (b starHeight : this.mPartialViews) {
            starHeight.setStarHeight(i);
        }
    }

    public void setStarPadding(int i) {
        if (i >= 0) {
            this.mPadding = i;
            for (b padding : this.mPartialViews) {
                int i2 = this.mPadding;
                padding.setPadding(i2, i2, i2, i2);
            }
        }
    }

    public void setStarWidth(@IntRange(from = 0) int i) {
        this.mStarWidth = i;
        for (b starWidth : this.mPartialViews) {
            starWidth.setStarWidth(i);
        }
    }

    public void setStepSize(@FloatRange(from = 0.1d, to = 1.0d) float f) {
        this.mStepSize = f;
    }
}
