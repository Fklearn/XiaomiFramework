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
public class DecelerateInterpolator
  extends BaseInterpolator
  implements NativeInterpolatorFactory
{
  private float mFactor = 1.0F;
  
  public DecelerateInterpolator() {}
  
  public DecelerateInterpolator(float paramFloat)
  {
    this.mFactor = paramFloat;
  }
  
  public DecelerateInterpolator(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext.getResources(), paramContext.getTheme(), paramAttributeSet);
  }
  
  public DecelerateInterpolator(Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet)
  {
    if (paramTheme != null) {
      paramResources = paramTheme.obtainStyledAttributes(paramAttributeSet, R.styleable.DecelerateInterpolator, 0, 0);
    } else {
      paramResources = paramResources.obtainAttributes(paramAttributeSet, R.styleable.DecelerateInterpolator);
    }
    this.mFactor = paramResources.getFloat(0, 1.0F);
    setChangingConfiguration(paramResources.getChangingConfigurations());
    paramResources.recycle();
  }
  
  public long createNativeInterpolator()
  {
    return NativeInterpolatorFactoryHelper.createDecelerateInterpolator(this.mFactor);
  }
  
  public float getInterpolation(float paramFloat)
  {
    float f = this.mFactor;
    if (f == 1.0F) {
      paramFloat = 1.0F - (1.0F - paramFloat) * (1.0F - paramFloat);
    } else {
      paramFloat = (float)(1.0D - Math.pow(1.0F - paramFloat, f * 2.0F));
    }
    return paramFloat;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/DecelerateInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */