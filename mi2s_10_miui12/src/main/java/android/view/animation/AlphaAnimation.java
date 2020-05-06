package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R.styleable;

public class AlphaAnimation
  extends Animation
{
  private float mFromAlpha;
  private float mToAlpha;
  
  public AlphaAnimation(float paramFloat1, float paramFloat2)
  {
    this.mFromAlpha = paramFloat1;
    this.mToAlpha = paramFloat2;
  }
  
  public AlphaAnimation(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AlphaAnimation);
    this.mFromAlpha = paramContext.getFloat(0, 1.0F);
    this.mToAlpha = paramContext.getFloat(1, 1.0F);
    paramContext.recycle();
  }
  
  protected void applyTransformation(float paramFloat, Transformation paramTransformation)
  {
    float f = this.mFromAlpha;
    paramTransformation.setAlpha((this.mToAlpha - f) * paramFloat + f);
  }
  
  public boolean hasAlpha()
  {
    return true;
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/AlphaAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */