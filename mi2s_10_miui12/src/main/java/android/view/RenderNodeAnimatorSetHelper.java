package android.view;

import android.animation.TimeInterpolator;
import android.graphics.RecordingCanvas;
import android.graphics.RenderNode;
import com.android.internal.view.animation.FallbackLUTInterpolator;
import com.android.internal.view.animation.NativeInterpolatorFactory;
import com.android.internal.view.animation.NativeInterpolatorFactoryHelper;

public class RenderNodeAnimatorSetHelper
{
  public static long createNativeInterpolator(TimeInterpolator paramTimeInterpolator, long paramLong)
  {
    if (paramTimeInterpolator == null) {
      return NativeInterpolatorFactoryHelper.createLinearInterpolator();
    }
    if (RenderNodeAnimator.isNativeInterpolator(paramTimeInterpolator)) {
      return ((NativeInterpolatorFactory)paramTimeInterpolator).createNativeInterpolator();
    }
    return FallbackLUTInterpolator.createNativeInterpolator(paramTimeInterpolator, paramLong);
  }
  
  public static RenderNode getTarget(RecordingCanvas paramRecordingCanvas)
  {
    return paramRecordingCanvas.mNode;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/RenderNodeAnimatorSetHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */