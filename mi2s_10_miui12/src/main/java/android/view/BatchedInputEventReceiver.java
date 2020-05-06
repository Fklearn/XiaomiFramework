package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.Looper;

public class BatchedInputEventReceiver
  extends InputEventReceiver
{
  private final BatchedInputRunnable mBatchedInputRunnable = new BatchedInputRunnable(null);
  private boolean mBatchedInputScheduled;
  Choreographer mChoreographer;
  
  @UnsupportedAppUsage
  public BatchedInputEventReceiver(InputChannel paramInputChannel, Looper paramLooper, Choreographer paramChoreographer)
  {
    super(paramInputChannel, paramLooper);
    this.mChoreographer = paramChoreographer;
  }
  
  private void scheduleBatchedInput()
  {
    if (!this.mBatchedInputScheduled)
    {
      this.mBatchedInputScheduled = true;
      this.mChoreographer.postCallback(0, this.mBatchedInputRunnable, null);
    }
  }
  
  private void unscheduleBatchedInput()
  {
    if (this.mBatchedInputScheduled)
    {
      this.mBatchedInputScheduled = false;
      this.mChoreographer.removeCallbacks(0, this.mBatchedInputRunnable, null);
    }
  }
  
  public void dispose()
  {
    unscheduleBatchedInput();
    super.dispose();
  }
  
  void doConsumeBatchedInput(long paramLong)
  {
    if (this.mBatchedInputScheduled)
    {
      this.mBatchedInputScheduled = false;
      if ((consumeBatchedInputEvents(paramLong)) && (paramLong != -1L)) {
        scheduleBatchedInput();
      }
    }
  }
  
  public void onBatchedInputEventPending()
  {
    scheduleBatchedInput();
  }
  
  private final class BatchedInputRunnable
    implements Runnable
  {
    private BatchedInputRunnable() {}
    
    public void run()
    {
      BatchedInputEventReceiver localBatchedInputEventReceiver = BatchedInputEventReceiver.this;
      localBatchedInputEventReceiver.doConsumeBatchedInput(localBatchedInputEventReceiver.mChoreographer.getFrameTimeNanos());
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/BatchedInputEventReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */