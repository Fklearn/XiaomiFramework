package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.Looper;
import android.os.MessageQueue;
import com.android.internal.util.VirtualRefBasePtr;
import java.lang.ref.WeakReference;

public class FrameMetricsObserver
{
  @UnsupportedAppUsage
  private FrameMetrics mFrameMetrics;
  Window.OnFrameMetricsAvailableListener mListener;
  @UnsupportedAppUsage
  private MessageQueue mMessageQueue;
  public VirtualRefBasePtr mNative;
  private WeakReference<Window> mWindow;
  
  FrameMetricsObserver(Window paramWindow, Looper paramLooper, Window.OnFrameMetricsAvailableListener paramOnFrameMetricsAvailableListener)
  {
    if (paramLooper != null)
    {
      this.mMessageQueue = paramLooper.getQueue();
      if (this.mMessageQueue != null)
      {
        this.mFrameMetrics = new FrameMetrics();
        this.mWindow = new WeakReference(paramWindow);
        this.mListener = paramOnFrameMetricsAvailableListener;
        return;
      }
      throw new IllegalStateException("invalid looper, null message queue\n");
    }
    throw new NullPointerException("looper cannot be null");
  }
  
  @UnsupportedAppUsage
  private void notifyDataAvailable(int paramInt)
  {
    Window localWindow = (Window)this.mWindow.get();
    if (localWindow != null) {
      this.mListener.onFrameMetricsAvailable(localWindow, this.mFrameMetrics, paramInt);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/FrameMetricsObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */