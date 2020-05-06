package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.LongSparseArray;
import android.util.Pools.Pool;
import android.util.Pools.SimplePool;
import dalvik.system.CloseGuard;
import java.lang.ref.WeakReference;

public final class InputQueue
{
  private final LongSparseArray<ActiveInputEvent> mActiveEventArray = new LongSparseArray(20);
  private final Pools.Pool<ActiveInputEvent> mActiveInputEventPool = new Pools.SimplePool(20);
  private final CloseGuard mCloseGuard = CloseGuard.get();
  private long mPtr = nativeInit(new WeakReference(this), Looper.myQueue());
  
  public InputQueue()
  {
    this.mCloseGuard.open("dispose");
  }
  
  @UnsupportedAppUsage
  private void finishInputEvent(long paramLong, boolean paramBoolean)
  {
    int i = this.mActiveEventArray.indexOfKey(paramLong);
    if (i >= 0)
    {
      ActiveInputEvent localActiveInputEvent = (ActiveInputEvent)this.mActiveEventArray.valueAt(i);
      this.mActiveEventArray.removeAt(i);
      localActiveInputEvent.mCallback.onFinishedInputEvent(localActiveInputEvent.mToken, paramBoolean);
      recycleActiveInputEvent(localActiveInputEvent);
    }
  }
  
  private static native void nativeDispose(long paramLong);
  
  private static native long nativeInit(WeakReference<InputQueue> paramWeakReference, MessageQueue paramMessageQueue);
  
  private static native long nativeSendKeyEvent(long paramLong, KeyEvent paramKeyEvent, boolean paramBoolean);
  
  private static native long nativeSendMotionEvent(long paramLong, MotionEvent paramMotionEvent);
  
  private ActiveInputEvent obtainActiveInputEvent(Object paramObject, FinishedInputEventCallback paramFinishedInputEventCallback)
  {
    ActiveInputEvent localActiveInputEvent1 = (ActiveInputEvent)this.mActiveInputEventPool.acquire();
    ActiveInputEvent localActiveInputEvent2 = localActiveInputEvent1;
    if (localActiveInputEvent1 == null) {
      localActiveInputEvent2 = new ActiveInputEvent(null);
    }
    localActiveInputEvent2.mToken = paramObject;
    localActiveInputEvent2.mCallback = paramFinishedInputEventCallback;
    return localActiveInputEvent2;
  }
  
  private void recycleActiveInputEvent(ActiveInputEvent paramActiveInputEvent)
  {
    paramActiveInputEvent.recycle();
    this.mActiveInputEventPool.release(paramActiveInputEvent);
  }
  
  public void dispose()
  {
    dispose(false);
  }
  
  public void dispose(boolean paramBoolean)
  {
    CloseGuard localCloseGuard = this.mCloseGuard;
    if (localCloseGuard != null)
    {
      if (paramBoolean) {
        localCloseGuard.warnIfOpen();
      }
      this.mCloseGuard.close();
    }
    long l = this.mPtr;
    if (l != 0L)
    {
      nativeDispose(l);
      this.mPtr = 0L;
    }
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
  
  public long getNativePtr()
  {
    return this.mPtr;
  }
  
  public void sendInputEvent(InputEvent paramInputEvent, Object paramObject, boolean paramBoolean, FinishedInputEventCallback paramFinishedInputEventCallback)
  {
    paramObject = obtainActiveInputEvent(paramObject, paramFinishedInputEventCallback);
    long l;
    if ((paramInputEvent instanceof KeyEvent)) {
      l = nativeSendKeyEvent(this.mPtr, (KeyEvent)paramInputEvent, paramBoolean);
    } else {
      l = nativeSendMotionEvent(this.mPtr, (MotionEvent)paramInputEvent);
    }
    this.mActiveEventArray.put(l, paramObject);
  }
  
  private final class ActiveInputEvent
  {
    public InputQueue.FinishedInputEventCallback mCallback;
    public Object mToken;
    
    private ActiveInputEvent() {}
    
    public void recycle()
    {
      this.mToken = null;
      this.mCallback = null;
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onInputQueueCreated(InputQueue paramInputQueue);
    
    public abstract void onInputQueueDestroyed(InputQueue paramInputQueue);
  }
  
  public static abstract interface FinishedInputEventCallback
  {
    public abstract void onFinishedInputEvent(Object paramObject, boolean paramBoolean);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InputQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */