package android.view;

import android.annotation.UnsupportedAppUsage;

public final class SurfaceSession
{
  @UnsupportedAppUsage
  private long mNativeClient = nativeCreate();
  
  private static native long nativeCreate();
  
  private static native void nativeDestroy(long paramLong);
  
  private static native void nativeKill(long paramLong);
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mNativeClient != 0L) {
        nativeDestroy(this.mNativeClient);
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  @UnsupportedAppUsage
  public void kill()
  {
    nativeKill(this.mNativeClient);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/SurfaceSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */