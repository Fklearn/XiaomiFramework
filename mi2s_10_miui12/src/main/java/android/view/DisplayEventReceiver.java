package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import dalvik.annotation.optimization.FastNative;
import dalvik.system.CloseGuard;
import java.lang.ref.WeakReference;

public abstract class DisplayEventReceiver
{
  private static final String TAG = "DisplayEventReceiver";
  public static final int VSYNC_SOURCE_APP = 0;
  public static final int VSYNC_SOURCE_SURFACE_FLINGER = 1;
  private final CloseGuard mCloseGuard = CloseGuard.get();
  private MessageQueue mMessageQueue;
  @UnsupportedAppUsage
  private long mReceiverPtr;
  
  @UnsupportedAppUsage
  public DisplayEventReceiver(Looper paramLooper)
  {
    this(paramLooper, 0);
  }
  
  public DisplayEventReceiver(Looper paramLooper, int paramInt)
  {
    if (paramLooper != null)
    {
      this.mMessageQueue = paramLooper.getQueue();
      this.mReceiverPtr = nativeInit(new WeakReference(this), this.mMessageQueue, paramInt);
      this.mCloseGuard.open("dispose");
      return;
    }
    throw new IllegalArgumentException("looper must not be null");
  }
  
  private void dispatchConfigChanged(long paramLong1, long paramLong2, int paramInt)
  {
    onConfigChanged(paramLong1, paramLong2, paramInt);
  }
  
  @UnsupportedAppUsage
  private void dispatchHotplug(long paramLong1, long paramLong2, boolean paramBoolean)
  {
    onHotplug(paramLong1, paramLong2, paramBoolean);
  }
  
  @UnsupportedAppUsage
  private void dispatchVsync(long paramLong1, long paramLong2, int paramInt)
  {
    onVsync(paramLong1, paramLong2, paramInt);
  }
  
  private void dispose(boolean paramBoolean)
  {
    CloseGuard localCloseGuard = this.mCloseGuard;
    if (localCloseGuard != null)
    {
      if (paramBoolean) {
        localCloseGuard.warnIfOpen();
      }
      this.mCloseGuard.close();
    }
    long l = this.mReceiverPtr;
    if (l != 0L)
    {
      nativeDispose(l);
      this.mReceiverPtr = 0L;
    }
    this.mMessageQueue = null;
  }
  
  private static native void nativeDispose(long paramLong);
  
  private static native long nativeInit(WeakReference<DisplayEventReceiver> paramWeakReference, MessageQueue paramMessageQueue, int paramInt);
  
  @FastNative
  private static native void nativeScheduleVsync(long paramLong);
  
  public void dispose()
  {
    dispose(false);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      dispose(true);
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public void onConfigChanged(long paramLong1, long paramLong2, int paramInt) {}
  
  @UnsupportedAppUsage
  public void onHotplug(long paramLong1, long paramLong2, boolean paramBoolean) {}
  
  @UnsupportedAppUsage
  public void onVsync(long paramLong1, long paramLong2, int paramInt) {}
  
  @UnsupportedAppUsage
  public void scheduleVsync()
  {
    long l = this.mReceiverPtr;
    if (l == 0L) {
      Log.w("DisplayEventReceiver", "Attempted to schedule a vertical sync pulse but the display event receiver has already been disposed.");
    } else {
      nativeScheduleVsync(l);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/DisplayEventReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */