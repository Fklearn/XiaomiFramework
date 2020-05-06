package android.view.animation;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.util.AttributeSet;
import com.android.internal.R.styleable;

public class TranslateAnimation
  extends Animation
{
  protected float mFromXDelta;
  private int mFromXType = 0;
  @UnsupportedAppUsage
  protected float mFromXValue = 0.0F;
  protected float mFromYDelta;
  private int mFromYType = 0;
  @UnsupportedAppUsage
  protected float mFromYValue = 0.0F;
  protected float mToXDelta;
  private int mToXType = 0;
  @UnsupportedAppUsage
  protected float mToXValue = 0.0F;
  protected float mToYDelta;
  private int mToYType = 0;
  @UnsupportedAppUsage
  protected float mToYValue = 0.0F;
  
  public TranslateAnimation(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.mFromXValue = paramFloat1;
    this.mToXValue = paramFloat2;
    this.mFromYValue = paramFloat3;
    this.mToYValue = paramFloat4;
    this.mFromXType = 0;
    this.mToXType = 0;
    this.mFromYType = 0;
    this.mToYType = 0;
  }
  
  public TranslateAnimation(int paramInt1, float paramFloat1, int paramInt2, float paramFloat2, int paramInt3, float paramFloat3, int paramInt4, float paramFloat4)
  {
    this.mFromXValue = paramFloat1;
    this.mToXValue = paramFloat2;
    this.mFromYValue = paramFloat3;
    this.mToYValue = paramFloat4;
    this.mFromXType = paramInt1;
    this.mToXType = paramInt2;
    this.mFromYType = paramInt3;
    this.mToYType = paramInt4;
  }
  
  public TranslateAnimation(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.TranslateAnimation);
    paramAttributeSet = Animation.Description.parseValue(paramContext.peekValue(0));
    this.mFromXType = paramAttributeSet.type;
    this.mFromXValue = paramAttributeSet.value;
    paramAttributeSet = Animation.Description.parseValue(paramContext.peekValue(1));
    this.mToXType = paramAttributeSet.type;
    this.mToXValue = paramAttributeSet.value;
    paramAttributeSet = Animation.Description.parseValue(paramContext.peekValue(2));
    this.mFromYType = paramAttributeSet.type;
    this.mFromYValue = paramAttributeSet.value;
    paramAttributeSet = Animation.Description.parseValue(paramContext.peekValue(3));
    this.mToYType = paramAttributeSet.type;
    this.mToYValue = paramAttributeSet.value;
    paramContext.recycle();
  }
  
  protected void applyTransformation(float paramFloat, Transformation paramTransformation)
  {
    float f1 = this.mFromXDelta;
    float f2 = this.mFromYDelta;
    float f3 = this.mFromXDelta;
    float f4 = this.mToXDelta;
    if (f3 != f4) {
      f1 = f3 + (f4 - f3) * paramFloat;
    }
    f4 = this.mFromYDelta;
    f3 = this.mToYDelta;
    if (f4 != f3) {
      f2 = f4 + (f3 - f4) * paramFloat;
    }
    paramTransformation.getMatrix().setTranslate(f1, f2);
  }
  
  public void initialize(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.initialize(paramInt1, paramInt2, paramInt3, paramInt4);
    this.mFromXDelta = resolveSize(this.mFromXType, this.mFromXValue, paramInt1, paramInt3);
    this.mToXDelta = resolveSize(this.mToXType, this.mToXValue, paramInt1, paramInt3);
    this.mFromYDelta = resolveSize(this.mFromYType, this.mFromYValue, paramInt2, paramInt4);
    this.mToYDelta = resolveSize(this.mToYType, this.mToYValue, paramInt2, paramInt4);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/TranslateAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */