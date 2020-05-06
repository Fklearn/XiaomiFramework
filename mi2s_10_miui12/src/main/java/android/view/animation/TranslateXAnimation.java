package android.view.animation;

import android.graphics.Matrix;

public class TranslateXAnimation
  extends TranslateAnimation
{
  float[] mTmpValues = new float[9];
  
  public TranslateXAnimation(float paramFloat1, float paramFloat2)
  {
    super(paramFloat1, paramFloat2, 0.0F, 0.0F);
  }
  
  public TranslateXAnimation(int paramInt1, float paramFloat1, int paramInt2, float paramFloat2)
  {
    super(paramInt1, paramFloat1, paramInt2, paramFloat2, 0, 0.0F, 0, 0.0F);
  }
  
  protected void applyTransformation(float paramFloat, Transformation paramTransformation)
  {
    paramTransformation.getMatrix().getValues(this.mTmpValues);
    float f1 = this.mFromXDelta;
    float f2 = this.mToXDelta;
    float f3 = this.mFromXDelta;
    paramTransformation.getMatrix().setTranslate(f1 + (f2 - f3) * paramFloat, this.mTmpValues[5]);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/TranslateXAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */