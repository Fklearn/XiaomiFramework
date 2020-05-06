package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.Looper;
import android.os.MessageMonitor;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.util.Log;
import android.util.Slog;
import android.util.SparseIntArray;
import dalvik.system.CloseGuard;
import java.lang.ref.WeakReference;

public abstract class InputEventReceiver
{
  private static final int INPUT_DISPATCH_THRESHOLD_MS = 100;
  private static final int SLOW_INPUT_THRESHOLD_MS = 300;
  private static final String TAG = "InputEventReceiver";
  Choreographer mChoreographer;
  private final CloseGuard mCloseGuard = CloseGuard.get();
  private int mDispatchSeqNumber;
  private long mDispatchStartTime;
  private InputChannel mInputChannel;
  private MessageQueue mMessageQueue;
  private long mReceiverPtr;
  private final SparseIntArray mSeqMap = new SparseIntArray();
  
  public InputEventReceiver(InputChannel paramInputChannel, Looper paramLooper)
  {
    if (paramInputChannel != null)
    {
      if (paramLooper != null)
      {
        this.mInputChannel = paramInputChannel;
        this.mMessageQueue = paramLooper.getQueue();
        this.mReceiverPtr = nativeInit(new WeakReference(this), paramInputChannel, this.mMessageQueue);
        this.mCloseGuard.open("dispose");
        return;
      }
      throw new IllegalArgumentException("looper must not be null");
    }
    throw new IllegalArgumentException("inputChannel must not be null");
  }
  
  @UnsupportedAppUsage
  private void dispatchBatchedInputEventPending()
  {
    onBatchedInputEventPending();
  }
  
  @UnsupportedAppUsage
  private void dispatchInputEvent(int paramInt, InputEvent paramInputEvent)
  {
    this.mDispatchStartTime = SystemClock.uptimeMillis();
    this.mDispatchSeqNumber = paramInputEvent.getSequenceNumber();
    Looper.myLooper().getMessageMonitor().checkInputEvent(paramInputEvent);
    long l = this.mDispatchStartTime - paramInputEvent.getEventTime();
    if (l > 300L)
    {
      Object localObject;
      StringBuilder localStringBuilder;
      if ((paramInputEvent instanceof KeyEvent))
      {
        localObject = (KeyEvent)paramInputEvent;
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("App Input: ");
        localStringBuilder.append(l);
        localStringBuilder.append("ms before dispatchInputEvent (KeyEvent: event_seq=");
        localStringBuilder.append(paramInputEvent.getSequenceNumber());
        localStringBuilder.append(", seq=");
        localStringBuilder.append(paramInt);
        localStringBuilder.append(", code=");
        localStringBuilder.append(KeyEvent.keyCodeToString(((KeyEvent)localObject).getKeyCode()));
        localStringBuilder.append(", action=");
        localStringBuilder.append(KeyEvent.actionToString(((KeyEvent)localObject).getAction()));
        localStringBuilder.append(")");
        Slog.w("InputEventReceiver", localStringBuilder.toString());
      }
      else if ((paramInputEvent instanceof MotionEvent))
      {
        localObject = (MotionEvent)paramInputEvent;
        if (((MotionEvent)localObject).getAction() != 2)
        {
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("App Input: ");
          localStringBuilder.append(l);
          localStringBuilder.append("ms before dispatchInputEvent (MotionEvent: event_seq=");
          localStringBuilder.append(paramInputEvent.getSequenceNumber());
          localStringBuilder.append(", seq=");
          localStringBuilder.append(paramInt);
          localStringBuilder.append(", action=");
          localStringBuilder.append(MotionEvent.actionToString(((MotionEvent)localObject).getAction()));
          localStringBuilder.append(")");
          Slog.w("InputEventReceiver", localStringBuilder.toString());
        }
      }
    }
    this.mSeqMap.put(paramInputEvent.getSequenceNumber(), paramInt);
    onInputEvent(paramInputEvent);
  }
  
  private void dispatchMotionEventInfo(int paramInt1, int paramInt2)
  {
    try
    {
      if (this.mChoreographer == null) {
        this.mChoreographer = Choreographer.getInstance();
      }
      if (this.mChoreographer != null) {
        this.mChoreographer.setMotionEventInfo(paramInt1, paramInt2);
      }
    }
    catch (Exception localException)
    {
      Log.e("InputEventReceiver", "cannot invoke setMotionEventInfo.");
    }
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
    this.mInputChannel = null;
    this.mMessageQueue = null;
  }
  
  private static native boolean nativeConsumeBatchedInputEvents(long paramLong1, long paramLong2);
  
  private static native void nativeDispose(long paramLong);
  
  private static native void nativeFinishInputEvent(long paramLong, int paramInt, boolean paramBoolean);
  
  private static native long nativeInit(WeakReference<InputEventReceiver> paramWeakReference, InputChannel paramInputChannel, MessageQueue paramMessageQueue);
  
  public final boolean consumeBatchedInputEvents(long paramLong)
  {
    long l = this.mReceiverPtr;
    if (l == 0L)
    {
      Log.w("InputEventReceiver", "Attempted to consume batched input events but the input event receiver has already been disposed.");
      return false;
    }
    return nativeConsumeBatchedInputEvents(l, paramLong);
  }
  
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
  
  public final void finishInputEvent(InputEvent paramInputEvent, boolean paramBoolean)
  {
    if (paramInputEvent != null)
    {
      if (this.mReceiverPtr == 0L)
      {
        Log.w("InputEventReceiver", "Attempted to finish an input event but the input event receiver has already been disposed.");
      }
      else
      {
        int i = this.mSeqMap.indexOfKey(paramInputEvent.getSequenceNumber());
        if (i < 0)
        {
          Log.w("InputEventReceiver", "Attempted to finish an input event that is not in progress.");
        }
        else
        {
          int j = this.mSeqMap.valueAt(i);
          this.mSeqMap.removeAt(i);
          nativeFinishInputEvent(this.mReceiverPtr, j, paramBoolean);
          if (paramInputEvent.getSequenceNumber() == this.mDispatchSeqNumber)
          {
            long l = SystemClock.uptimeMillis() - this.mDispatchStartTime;
            if (l > 100L)
            {
              Object localObject;
              StringBuilder localStringBuilder;
              if ((paramInputEvent instanceof KeyEvent))
              {
                localObject = (KeyEvent)paramInputEvent;
                localStringBuilder = new StringBuilder();
                localStringBuilder.append("App Input: Dispatching InputEvent took ");
                localStringBuilder.append(l);
                localStringBuilder.append("ms in main thread! (KeyEvent: event_seq=");
                localStringBuilder.append(paramInputEvent.getSequenceNumber());
                localStringBuilder.append(", seq=");
                localStringBuilder.append(j);
                localStringBuilder.append(", code=");
                localStringBuilder.append(KeyEvent.keyCodeToString(((KeyEvent)localObject).getKeyCode()));
                localStringBuilder.append(", action=");
                localStringBuilder.append(KeyEvent.actionToString(((KeyEvent)localObject).getAction()));
                localStringBuilder.append(")");
                Slog.w("InputEventReceiver", localStringBuilder.toString());
              }
              else if ((paramInputEvent instanceof MotionEvent))
              {
                localObject = (MotionEvent)paramInputEvent;
                localStringBuilder = new StringBuilder();
                localStringBuilder.append("App Input: Dispatching InputEvent took ");
                localStringBuilder.append(l);
                localStringBuilder.append("ms in main thread! (MotionEvent: event_seq=");
                localStringBuilder.append(paramInputEvent.getSequenceNumber());
                localStringBuilder.append(", seq=");
                localStringBuilder.append(j);
                localStringBuilder.append(", action=");
                localStringBuilder.append(MotionEvent.actionToString(((MotionEvent)localObject).getAction()));
                localStringBuilder.append(")");
                Slog.w("InputEventReceiver", localStringBuilder.toString());
              }
            }
          }
        }
      }
      paramInputEvent.recycleIfNeededAfterDispatch();
      return;
    }
    throw new IllegalArgumentException("event must not be null");
  }
  
  public void onBatchedInputEventPending()
  {
    consumeBatchedInputEvents(-1L);
  }
  
  @UnsupportedAppUsage
  public void onInputEvent(InputEvent paramInputEvent)
  {
    finishInputEvent(paramInputEvent, false);
  }
  
  public static abstract interface Factory
  {
    public abstract InputEventReceiver createInputEventReceiver(InputChannel paramInputChannel, Looper paramLooper);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InputEventReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */