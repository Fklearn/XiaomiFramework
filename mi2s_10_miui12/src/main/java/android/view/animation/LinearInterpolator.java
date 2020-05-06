package android.view.animation;

import android.content.Context;
import android.util.AttributeSet;
import com.android.internal.view.animation.HasNativeInterpolator;
import com.android.internal.view.animation.NativeInterpolatorFactory;
import com.android.internal.view.animation.NativeInterpolatorFactoryHelper;

@HasNativeInterpolator
public class LinearInterpolator
  extends BaseInterpolator
  implements NativeInterpolatorFactory
{
  public LinearInterpolator() {}
  
  public LinearInterpolator(Context paramContext, AttributeSet paramAttributeSet) {}
  
  public long createNativeInterpolator()
  {
    return NativeInterpolatorFactoryHelper.createLinearInterpolator();
  }
  
  public float getInterpolation(float paramFloat)
  {
    return paramFloat;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/LinearInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */