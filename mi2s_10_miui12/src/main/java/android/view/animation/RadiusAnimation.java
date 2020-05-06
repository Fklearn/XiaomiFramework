package android.view.animation;

import android.util.Slog;

public class RadiusAnimation
  extends Animation
{
  private static final String TAG = "RadiusAnimation";
  private float mFromRadius;
  private float mToRadius;
  
  public RadiusAnimation(float paramFloat1, float paramFloat2)
  {
    this.mFromRadius = paramFloat1;
    this.mToRadius = paramFloat2;
  }
  
  protected void applyTransformation(float paramFloat, Transformation paramTransformation)
  {
    float f = this.mFromRadius;
    if ((paramTransformation instanceof MiuiTransformation)) {
      ((MiuiTransformation)paramTransformation).setRadius((this.mToRadius - f) * paramFloat + f);
    } else {
      Slog.d("RadiusAnimation", "RadiusAnimation must use for MiuiTransformation");
    }
  }
  
  public boolean willChangeBounds()
  {
    return false;
  }
  
  public boolean willChangeTransformationMatrix()
  {
    return false;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/RadiusAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */