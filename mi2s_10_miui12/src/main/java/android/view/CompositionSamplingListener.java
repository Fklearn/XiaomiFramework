package android.view;

import android.graphics.Rect;
import android.os.IBinder;
import com.android.internal.util.Preconditions;
import java.util.concurrent.Executor;

public abstract class CompositionSamplingListener
{
  private final Executor mExecutor;
  private final long mNativeListener;
  
  public CompositionSamplingListener(Executor paramExecutor)
  {
    this.mExecutor = paramExecutor;
    this.mNativeListener = nativeCreate(this);
  }
  
  private static void dispatchOnSampleCollected(CompositionSamplingListener paramCompositionSamplingListener, float paramFloat)
  {
    paramCompositionSamplingListener.mExecutor.execute(new _..Lambda.CompositionSamplingListener.hrbPutjnKRv7VkkiY9eg32N6QA8(paramCompositionSamplingListener, paramFloat));
  }
  
  private static native long nativeCreate(CompositionSamplingListener paramCompositionSamplingListener);
  
  private static native void nativeDestroy(long paramLong);
  
  private static native void nativeRegister(long paramLong, IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  private static native void nativeUnregister(long paramLong);
  
  public static void register(CompositionSamplingListener paramCompositionSamplingListener, int paramInt, IBinder paramIBinder, Rect paramRect)
  {
    boolean bool;
    if (paramInt == 0) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool, "default display only for now");
    nativeRegister(paramCompositionSamplingListener.mNativeListener, paramIBinder, paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
  }
  
  public static void unregister(CompositionSamplingListener paramCompositionSamplingListener)
  {
    nativeUnregister(paramCompositionSamplingListener.mNativeListener);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mNativeListener != 0L)
      {
        unregister(this);
        nativeDestroy(this.mNativeListener);
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public abstract void onSampleCollected(float paramFloat);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/CompositionSamplingListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */