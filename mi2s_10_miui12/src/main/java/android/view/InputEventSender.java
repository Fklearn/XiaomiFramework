package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.lang.ref.WeakReference;

public abstract class InputEventSender
{
  private static final String TAG = "InputEventSender";
  private final CloseGuard mCloseGuard = CloseGuard.get();
  private InputChannel mInputChannel;
  private MessageQueue mMessageQueue;
  private long mSenderPtr;
  
  public InputEventSender(InputChannel paramInputChannel, Looper paramLooper)
  {
    if (paramInputChannel != null)
    {
      if (paramLooper != null)
      {
        this.mInputChannel = paramInputChannel;
        this.mMessageQueue = paramLooper.getQueue();
        this.mSenderPtr = nativeInit(new WeakReference(this), paramInputChannel, this.mMessageQueue);
        this.mCloseGuard.open("dispose");
        return;
      }
      throw new IllegalArgumentException("looper must not be null");
    }
    throw new IllegalArgumentException("inputChannel must not be null");
  }
  
  @UnsupportedAppUsage
  private void dispatchInputEventFinished(int paramInt, boolean paramBoolean)
  {
    onInputEventFinished(paramInt, paramBoolean);
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
    long l = this.mSenderPtr;
    if (l != 0L)
    {
      nativeDispose(l);
      this.mSenderPtr = 0L;
    }
    this.mInputChannel = null;
    this.mMessageQueue = null;
  }
  
  private static native void nativeDispose(long paramLong);
  
  private static native long nativeInit(WeakReference<InputEventSender> paramWeakReference, InputChannel paramInputChannel, MessageQueue paramMessageQueue);
  
  private static native boolean nativeSendKeyEvent(long paramLong, int paramInt, KeyEvent paramKeyEvent);
  
  private static native boolean nativeSendMotionEvent(long paramLong, int paramInt, MotionEvent paramMotionEvent);
  
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
  
  public void onInputEventFinished(int paramInt, boolean paramBoolean) {}
  
  public final boolean sendInputEvent(int paramInt, InputEvent paramInputEvent)
  {
    if (paramInputEvent != null)
    {
      long l = this.mSenderPtr;
      if (l == 0L)
      {
        Log.w("InputEventSender", "Attempted to send an input event but the input event sender has already been disposed.");
        return false;
      }
      if ((paramInputEvent instanceof KeyEvent)) {
        return nativeSendKeyEvent(l, paramInt, (KeyEvent)paramInputEvent);
      }
      return nativeSendMotionEvent(l, paramInt, (MotionEvent)paramInputEvent);
    }
    throw new IllegalArgumentException("event must not be null");
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InputEventSender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */