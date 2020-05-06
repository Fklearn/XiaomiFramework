package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import com.android.internal.R.styleable;

public class RatingBar
  extends AbsSeekBar
{
  private int mNumStars = 5;
  @UnsupportedAppUsage
  private OnRatingBarChangeListener mOnRatingBarChangeListener;
  private int mProgressOnStartTracking;
  
  public RatingBar(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public RatingBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842876);
  }
  
  public RatingBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public RatingBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.RatingBar, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.RatingBar, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    paramInt1 = localTypedArray.getInt(0, this.mNumStars);
    setIsIndicator(localTypedArray.getBoolean(3, this.mIsUserSeekable ^ true));
    float f1 = localTypedArray.getFloat(1, -1.0F);
    float f2 = localTypedArray.getFloat(2, -1.0F);
    localTypedArray.recycle();
    if ((paramInt1 > 0) && (paramInt1 != this.mNumStars)) {
      setNumStars(paramInt1);
    }
    if (f2 >= 0.0F) {
      setStepSize(f2);
    } else {
      setStepSize(0.5F);
    }
    if (f1 >= 0.0F) {
      setRating(f1);
    }
    this.mTouchProgressOffset = 0.6F;
  }
  
  private float getProgressPerStar()
  {
    if (this.mNumStars > 0) {
      return getMax() * 1.0F / this.mNumStars;
    }
    return 1.0F;
  }
  
  private void updateSecondaryProgress(int paramInt)
  {
    float f = getProgressPerStar();
    if (f > 0.0F) {
      setSecondaryProgress((int)(Math.ceil(paramInt / f) * f));
    }
  }
  
  boolean canUserSetProgress()
  {
    boolean bool;
    if ((super.canUserSetProgress()) && (!isIndicator())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  void dispatchRatingChange(boolean paramBoolean)
  {
    OnRatingBarChangeListener localOnRatingBarChangeListener = this.mOnRatingBarChangeListener;
    if (localOnRatingBarChangeListener != null) {
      localOnRatingBarChangeListener.onRatingChanged(this, getRating(), paramBoolean);
    }
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return RatingBar.class.getName();
  }
  
  Shape getDrawableShape()
  {
    return new RectShape();
  }
  
  public int getNumStars()
  {
    return this.mNumStars;
  }
  
  public OnRatingBarChangeListener getOnRatingBarChangeListener()
  {
    return this.mOnRatingBarChangeListener;
  }
  
  public float getRating()
  {
    return getProgress() / getProgressPerStar();
  }
  
  public float getStepSize()
  {
    return getNumStars() / getMax();
  }
  
  public boolean isIndicator()
  {
    return this.mIsUserSeekable ^ true;
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    if (canUserSetProgress()) {
      paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_PROGRESS);
    }
  }
  
  void onKeyChange()
  {
    super.onKeyChange();
    dispatchRatingChange(true);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    try
    {
      super.onMeasure(paramInt1, paramInt2);
      if (this.mSampleWidth > 0) {
        setMeasuredDimension(resolveSizeAndState(this.mSampleWidth * this.mNumStars, paramInt1, 0), getMeasuredHeight());
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  void onProgressRefresh(float paramFloat, boolean paramBoolean, int paramInt)
  {
    super.onProgressRefresh(paramFloat, paramBoolean, paramInt);
    updateSecondaryProgress(paramInt);
    if (!paramBoolean) {
      dispatchRatingChange(false);
    }
  }
  
  void onStartTrackingTouch()
  {
    this.mProgressOnStartTracking = getProgress();
    super.onStartTrackingTouch();
  }
  
  void onStopTrackingTouch()
  {
    super.onStopTrackingTouch();
    if (getProgress() != this.mProgressOnStartTracking) {
      dispatchRatingChange(true);
    }
  }
  
  public void setIsIndicator(boolean paramBoolean)
  {
    this.mIsUserSeekable = (paramBoolean ^ true);
    if (paramBoolean) {
      setFocusable(16);
    } else {
      setFocusable(1);
    }
  }
  
  public void setMax(int paramInt)
  {
    if (paramInt <= 0) {
      return;
    }
    try
    {
      super.setMax(paramInt);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void setNumStars(int paramInt)
  {
    if (paramInt <= 0) {
      return;
    }
    this.mNumStars = paramInt;
    requestLayout();
  }
  
  public void setOnRatingBarChangeListener(OnRatingBarChangeListener paramOnRatingBarChangeListener)
  {
    this.mOnRatingBarChangeListener = paramOnRatingBarChangeListener;
  }
  
  public void setRating(float paramFloat)
  {
    setProgress(Math.round(getProgressPerStar() * paramFloat));
  }
  
  public void setStepSize(float paramFloat)
  {
    if (paramFloat <= 0.0F) {
      return;
    }
    paramFloat = this.mNumStars / paramFloat;
    int i = (int)(paramFloat / getMax() * getProgress());
    setMax((int)paramFloat);
    setProgress(i);
  }
  
  public static abstract interface OnRatingBarChangeListener
  {
    public abstract void onRatingChanged(RatingBar paramRatingBar, float paramFloat, boolean paramBoolean);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/RatingBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */