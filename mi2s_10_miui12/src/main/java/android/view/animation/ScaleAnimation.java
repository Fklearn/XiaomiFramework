package android.view.animation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.android.internal.R.styleable;

public class ScaleAnimation
  extends Animation
{
  private float mFromX;
  private int mFromXData = 0;
  private int mFromXType = 0;
  private float mFromY;
  private int mFromYData = 0;
  private int mFromYType = 0;
  private float mPivotX;
  private int mPivotXType = 0;
  private float mPivotXValue = 0.0F;
  private float mPivotY;
  private int mPivotYType = 0;
  private float mPivotYValue = 0.0F;
  private final Resources mResources;
  private float mToX;
  private int mToXData = 0;
  private int mToXType = 0;
  private float mToY;
  private int mToYData = 0;
  private int mToYType = 0;
  
  public ScaleAnimation(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.mResources = null;
    this.mFromX = paramFloat1;
    this.mToX = paramFloat2;
    this.mFromY = paramFloat3;
    this.mToY = paramFloat4;
    this.mPivotX = 0.0F;
    this.mPivotY = 0.0F;
  }
  
  public ScaleAnimation(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    this.mResources = null;
    this.mFromX = paramFloat1;
    this.mToX = paramFloat2;
    this.mFromY = paramFloat3;
    this.mToY = paramFloat4;
    this.mPivotXType = 0;
    this.mPivotYType = 0;
    this.mPivotXValue = paramFloat5;
    this.mPivotYValue = paramFloat6;
    initializePivotPoint();
  }
  
  public ScaleAnimation(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, float paramFloat5, int paramInt2, float paramFloat6)
  {
    this.mResources = null;
    this.mFromX = paramFloat1;
    this.mToX = paramFloat2;
    this.mFromY = paramFloat3;
    this.mToY = paramFloat4;
    this.mPivotXValue = paramFloat5;
    this.mPivotXType = paramInt1;
    this.mPivotYValue = paramFloat6;
    this.mPivotYType = paramInt2;
    initializePivotPoint();
  }
  
  public ScaleAnimation(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mResources = paramContext.getResources();
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ScaleAnimation);
    paramAttributeSet = paramContext.peekValue(2);
    this.mFromX = 0.0F;
    if (paramAttributeSet != null) {
      if (paramAttributeSet.type == 4)
      {
        this.mFromX = paramAttributeSet.getFloat();
      }
      else
      {
        this.mFromXType = paramAttributeSet.type;
        this.mFromXData = paramAttributeSet.data;
      }
    }
    paramAttributeSet = paramContext.peekValue(3);
    this.mToX = 0.0F;
    if (paramAttributeSet != null) {
      if (paramAttributeSet.type == 4)
      {
        this.mToX = paramAttributeSet.getFloat();
      }
      else
      {
        this.mToXType = paramAttributeSet.type;
        this.mToXData = paramAttributeSet.data;
      }
    }
    paramAttributeSet = paramContext.peekValue(4);
    this.mFromY = 0.0F;
    if (paramAttributeSet != null) {
      if (paramAttributeSet.type == 4)
      {
        this.mFromY = paramAttributeSet.getFloat();
      }
      else
      {
        this.mFromYType = paramAttributeSet.type;
        this.mFromYData = paramAttributeSet.data;
      }
    }
    paramAttributeSet = paramContext.peekValue(5);
    this.mToY = 0.0F;
    if (paramAttributeSet != null) {
      if (paramAttributeSet.type == 4)
      {
        this.mToY = paramAttributeSet.getFloat();
      }
      else
      {
        this.mToYType = paramAttributeSet.type;
        this.mToYData = paramAttributeSet.data;
      }
    }
    paramAttributeSet = Animation.Description.parseValue(paramContext.peekValue(0));
    this.mPivotXType = paramAttributeSet.type;
    this.mPivotXValue = paramAttributeSet.value;
    paramAttributeSet = Animation.Description.parseValue(paramContext.peekValue(1));
    this.mPivotYType = paramAttributeSet.type;
    this.mPivotYValue = paramAttributeSet.value;
    paramContext.recycle();
    initializePivotPoint();
  }
  
  private void initializePivotPoint()
  {
    if (this.mPivotXType == 0) {
      this.mPivotX = this.mPivotXValue;
    }
    if (this.mPivotYType == 0) {
      this.mPivotY = this.mPivotYValue;
    }
  }
  
  protected void applyTransformation(float paramFloat, Transformation paramTransformation)
  {
    float f1 = 1.0F;
    float f2 = 1.0F;
    float f3 = getScaleFactor();
    if ((this.mFromX != 1.0F) || (this.mToX != 1.0F))
    {
      f1 = this.mFromX;
      f1 += (this.mToX - f1) * paramFloat;
    }
    if ((this.mFromY != 1.0F) || (this.mToY != 1.0F))
    {
      f2 = this.mFromY;
      f2 += (this.mToY - f2) * paramFloat;
    }
    if ((this.mPivotX == 0.0F) && (this.mPivotY == 0.0F)) {
      paramTransformation.getMatrix().setScale(f1, f2);
    } else {
      paramTransformation.getMatrix().setScale(f1, f2, this.mPivotX * f3, this.mPivotY * f3);
    }
  }
  
  public void initialize(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.initialize(paramInt1, paramInt2, paramInt3, paramInt4);
    this.mFromX = resolveScale(this.mFromX, this.mFromXType, this.mFromXData, paramInt1, paramInt3);
    this.mToX = resolveScale(this.mToX, this.mToXType, this.mToXData, paramInt1, paramInt3);
    this.mFromY = resolveScale(this.mFromY, this.mFromYType, this.mFromYData, paramInt2, paramInt4);
    this.mToY = resolveScale(this.mToY, this.mToYType, this.mToYData, paramInt2, paramInt4);
    this.mPivotX = resolveSize(this.mPivotXType, this.mPivotXValue, paramInt1, paramInt3);
    this.mPivotY = resolveSize(this.mPivotYType, this.mPivotYValue, paramInt2, paramInt4);
  }
  
  float resolveScale(float paramFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramInt1 == 6)
    {
      paramFloat = TypedValue.complexToFraction(paramInt2, paramInt3, paramInt4);
    }
    else
    {
      if (paramInt1 != 5) {
        return paramFloat;
      }
      paramFloat = TypedValue.complexToDimension(paramInt2, this.mResources.getDisplayMetrics());
    }
    if (paramInt3 == 0) {
      return 1.0F;
    }
    return paramFloat / paramInt3;
    return paramFloat;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/ScaleAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */