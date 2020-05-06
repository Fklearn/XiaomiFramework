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
public class AccelerateInterpolator
  extends BaseInterpolator
  implements NativeInterpolatorFactory
{
  private final double mDoubleFactor;
  private final float mFactor;
  
  public AccelerateInterpolator()
  {
    this.mFactor = 1.0F;
    this.mDoubleFactor = 2.0D;
  }
  
  public AccelerateInterpolator(float paramFloat)
  {
    this.mFactor = paramFloat;
    this.mDoubleFactor = (this.mFactor * 2.0F);
  }
  
  public AccelerateInterpolator(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext.getResources(), paramContext.getTheme(), paramAttributeSet);
  }
  
  public AccelerateInterpolator(Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet)
  {
    if (paramTheme != null) {
      paramResources = paramTheme.obtainStyledAttributes(paramAttributeSet, R.styleable.AccelerateInterpolator, 0, 0);
    } else {
      paramResources = paramResources.obtainAttributes(paramAttributeSet, R.styleable.AccelerateInterpolator);
    }
    this.mFactor = paramResources.getFloat(0, 1.0F);
    this.mDoubleFactor = (this.mFactor * 2.0F);
    setChangingConfiguration(paramResources.getChangingConfigurations());
    paramResources.recycle();
  }
  
  public long createNativeInterpolator()
  {
    return NativeInterpolatorFactoryHelper.createAccelerateInterpolator(this.mFactor);
  }
  
  public float getInterpolation(float paramFloat)
  {
    if (this.mFactor == 1.0F) {
      return paramFloat * paramFloat;
    }
    return (float)Math.pow(paramFloat, this.mDoubleFactor);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/AccelerateInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */