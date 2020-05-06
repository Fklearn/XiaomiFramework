package android.view;

import android.os.Handler;
import com.android.internal.util.GrowingArrayUtils;

public class HandlerActionQueue
{
  private HandlerAction[] mActions;
  private int mCount;
  
  public void executeActions(Handler paramHandler)
  {
    try
    {
      HandlerAction[] arrayOfHandlerAction = this.mActions;
      int i = 0;
      int j = this.mCount;
      while (i < j)
      {
        HandlerAction localHandlerAction = arrayOfHandlerAction[i];
        paramHandler.postDelayed(localHandlerAction.action, localHandlerAction.delay);
        i++;
      }
      this.mActions = null;
      this.mCount = 0;
      return;
    }
    finally {}
  }
  
  public long getDelay(int paramInt)
  {
    if (paramInt < this.mCount) {
      return this.mActions[paramInt].delay;
    }
    throw new IndexOutOfBoundsException();
  }
  
  public Runnable getRunnable(int paramInt)
  {
    if (paramInt < this.mCount) {
      return this.mActions[paramInt].action;
    }
    throw new IndexOutOfBoundsException();
  }
  
  public void post(Runnable paramRunnable)
  {
    postDelayed(paramRunnable, 0L);
  }
  
  public void postDelayed(Runnable paramRunnable, long paramLong)
  {
    paramRunnable = new HandlerAction(paramRunnable, paramLong);
    try
    {
      if (this.mActions == null) {
        this.mActions = new HandlerAction[4];
      }
      this.mActions = ((HandlerAction[])GrowingArrayUtils.append(this.mActions, this.mCount, paramRunnable));
      this.mCount += 1;
      return;
    }
    finally {}
  }
  
  public void removeCallbacks(Runnable paramRunnable)
  {
    try
    {
      int i = this.mCount;
      int j = 0;
      HandlerAction[] arrayOfHandlerAction = this.mActions;
      for (int k = 0; k < i; k++) {
        if (!arrayOfHandlerAction[k].matches(paramRunnable))
        {
          if (j != k) {
            arrayOfHandlerAction[j] = arrayOfHandlerAction[k];
          }
          j++;
        }
      }
      this.mCount = j;
      while (j < i)
      {
        arrayOfHandlerAction[j] = null;
        j++;
      }
      return;
    }
    finally {}
  }
  
  public int size()
  {
    return this.mCount;
  }
  
  private static class HandlerAction
  {
    final Runnable action;
    final long delay;
    
    public HandlerAction(Runnable paramRunnable, long paramLong)
    {
      this.action = paramRunnable;
      this.delay = paramLong;
    }
    
    public boolean matches(Runnable paramRunnable)
    {
      if ((paramRunnable != null) || (this.action != null))
      {
        Runnable localRunnable = this.action;
        if ((localRunnable == null) || (!localRunnable.equals(paramRunnable))) {}
      }
      else
      {
        return true;
      }
      boolean bool = false;
      return bool;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/HandlerActionQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */