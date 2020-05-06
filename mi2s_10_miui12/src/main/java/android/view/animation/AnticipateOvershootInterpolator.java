package android.view.animation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R.styleable;
import com.android.internal.view.animation.HasNativeInterpolator;
import com.android.internal.view.animation.NativeInterpolatorFactory;
import com.android.internal.view.animation.NativeInterpolatorFactoryHelper;

@HasNativeInterpolator
public class AnticipateOvershootInterpolator
  extends BaseInterpolator
  implements NativeInterpolatorFactory
{
  private final float mTension;
  
  public AnticipateOvershootInterpolator()
  {
    this.mTension = 3.0F;
  }
  
  public AnticipateOvershootInterpolator(float paramFloat)
  {
    this.mTension = (1.5F * paramFloat);
  }
  
  public AnticipateOvershootInterpolator(float paramFloat1, float paramFloat2)
  {
    this.mTension = (paramFloat1 * paramFloat2);
  }
  
  public AnticipateOvershootInterpolator(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext.getResources(), paramContext.getTheme(), paramAttributeSet);
  }
  
  public AnticipateOvershootInterpolator(Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet)
  {
    if (paramTheme != null) {
      paramResources = paramTheme.obtainStyledAttributes(paramAttributeSet, R.styleable.AnticipateOvershootInterpolator, 0, 0);
    } else {
      paramResources = paramResources.obtainAttributes(paramAttributeSet, R.styleable.AnticipateOvershootInterpolator);
    }
    this.mTension = (paramResources.getFloat(0, 2.0F) * paramResources.getFloat(1, 1.5F));
    setChangingConfiguration(paramResources.getChangingConfigurations());
    paramResources.recycle();
  }
  
  private static float a(float paramFloat1, float paramFloat2)
  {
    return paramFloat1 * paramFloat1 * ((1.0F + paramFloat2) * paramFloat1 - paramFloat2);
  }
  
  private static float o(float paramFloat1, float paramFloat2)
  {
    return paramFloat1 * paramFloat1 * ((1.0F + paramFloat2) * paramFloat1 + paramFloat2);
  }
  
  public long createNativeInterpolator()
  {
    return NativeInterpolatorFactoryHelper.createAnticipateOvershootInterpolator(this.mTension);
  }
  
  public float getInterpolation(float paramFloat)
  {
    if (paramFloat < 0.5F) {
      return a(2.0F * paramFloat, this.mTension) * 0.5F;
    }
    return (o(paramFloat * 2.0F - 2.0F, this.mTension) + 2.0F) * 0.5F;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/AnticipateOvershootInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */