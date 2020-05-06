package android.view.animation;

import android.content.Context;
import android.util.AttributeSet;
import com.android.internal.view.animation.HasNativeInterpolator;
import com.android.internal.view.animation.NativeInterpolatorFactory;
import com.android.internal.view.animation.NativeInterpolatorFactoryHelper;

@HasNativeInterpolator
public class BounceInterpolator
  extends BaseInterpolator
  implements NativeInterpolatorFactory
{
  public BounceInterpolator() {}
  
  public BounceInterpolator(Context paramContext, AttributeSet paramAttributeSet) {}
  
  private static float bounce(float paramFloat)
  {
    return paramFloat * paramFloat * 8.0F;
  }
  
  public long createNativeInterpolator()
  {
    return NativeInterpolatorFactoryHelper.createBounceInterpolator();
  }
  
  public float getInterpolation(float paramFloat)
  {
    paramFloat *= 1.1226F;
    if (paramFloat < 0.3535F) {
      return bounce(paramFloat);
    }
    if (paramFloat < 0.7408F) {
      return bounce(paramFloat - 0.54719F) + 0.7F;
    }
    if (paramFloat < 0.9644F) {
      return bounce(paramFloat - 0.8526F) + 0.9F;
    }
    return bounce(paramFloat - 1.0435F) + 0.95F;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/BounceInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */