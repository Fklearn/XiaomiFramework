package android.view;

import android.os.IBinder;

public final class InputApplicationHandle
{
  public long dispatchingTimeoutNanos;
  public String name;
  private long ptr;
  public IBinder token;
  
  public InputApplicationHandle(IBinder paramIBinder)
  {
    this.token = paramIBinder;
  }
  
  private native void nativeDispose();
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      nativeDispose();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InputApplicationHandle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */