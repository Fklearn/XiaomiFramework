package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.util.AttributeSet;
import com.android.internal.R.styleable;

public class RotateAnimation
  extends Animation
{
  private float mFromDegrees;
  private float mPivotX;
  private int mPivotXType = 0;
  private float mPivotXValue = 0.0F;
  private float mPivotY;
  private int mPivotYType = 0;
  private float mPivotYValue = 0.0F;
  private float mToDegrees;
  
  public RotateAnimation(float paramFloat1, float paramFloat2)
  {
    this.mFromDegrees = paramFloat1;
    this.mToDegrees = paramFloat2;
    this.mPivotX = 0.0F;
    this.mPivotY = 0.0F;
  }
  
  public RotateAnimation(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.mFromDegrees = paramFloat1;
    this.mToDegrees = paramFloat2;
    this.mPivotXType = 0;
    this.mPivotYType = 0;
    this.mPivotXValue = paramFloat3;
    this.mPivotYValue = paramFloat4;
    initializePivotPoint();
  }
  
  public RotateAnimation(float paramFloat1, float paramFloat2, int paramInt1, float paramFloat3, int paramInt2, float paramFloat4)
  {
    this.mFromDegrees = paramFloat1;
    this.mToDegrees = paramFloat2;
    this.mPivotXValue = paramFloat3;
    this.mPivotXType = paramInt1;
    this.mPivotYValue = paramFloat4;
    this.mPivotYType = paramInt2;
    initializePivotPoint();
  }
  
  public RotateAnimation(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.RotateAnimation);
    this.mFromDegrees = paramContext.getFloat(0, 0.0F);
    this.mToDegrees = paramContext.getFloat(1, 0.0F);
    paramAttributeSet = Animation.Description.parseValue(paramContext.peekValue(2));
    this.mPivotXType = paramAttributeSet.type;
    this.mPivotXValue = paramAttributeSet.value;
    paramAttributeSet = Animation.Description.parseValue(paramContext.peekValue(3));
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
    float f = this.mFromDegrees;
    f += (this.mToDegrees - f) * paramFloat;
    paramFloat = getScaleFactor();
    if ((this.mPivotX == 0.0F) && (this.mPivotY == 0.0F)) {
      paramTransformation.getMatrix().setRotate(f);
    } else {
      paramTransformation.getMatrix().setRotate(f, this.mPivotX * paramFloat, this.mPivotY * paramFloat);
    }
  }
  
  public void initialize(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.initialize(paramInt1, paramInt2, paramInt3, paramInt4);
    this.mPivotX = resolveSize(this.mPivotXType, this.mPivotXValue, paramInt1, paramInt3);
    this.mPivotY = resolveSize(this.mPivotYType, this.mPivotYValue, paramInt2, paramInt4);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/RotateAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */