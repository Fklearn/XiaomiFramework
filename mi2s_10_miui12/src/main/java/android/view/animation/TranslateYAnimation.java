package android.view.animation;

import android.annotation.UnsupportedAppUsage;
import android.graphics.Matrix;

public class TranslateYAnimation
  extends TranslateAnimation
{
  float[] mTmpValues = new float[9];
  
  public TranslateYAnimation(float paramFloat1, float paramFloat2)
  {
    super(0.0F, 0.0F, paramFloat1, paramFloat2);
  }
  
  @UnsupportedAppUsage
  public TranslateYAnimation(int paramInt1, float paramFloat1, int paramInt2, float paramFloat2)
  {
    super(0, 0.0F, 0, 0.0F, paramInt1, paramFloat1, paramInt2, paramFloat2);
  }
  
  protected void applyTransformation(float paramFloat, Transformation paramTransformation)
  {
    paramTransformation.getMatrix().getValues(this.mTmpValues);
    float f1 = this.mFromYDelta;
    float f2 = this.mToYDelta;
    float f3 = this.mFromYDelta;
    paramTransformation.getMatrix().setTranslate(this.mTmpValues[2], f1 + (f2 - f3) * paramFloat);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/TranslateYAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */