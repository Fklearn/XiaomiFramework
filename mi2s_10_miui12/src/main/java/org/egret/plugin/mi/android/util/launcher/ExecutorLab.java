package org.egret.plugin.mi.android.util.launcher;

import android.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorLab
{
  private static final String TAG = "ExecutorLab";
  private static final int THREAD_SIZE = 3;
  private static ExecutorLab instance = null;
  private ExecutorService pool = Executors.newFixedThreadPool(3);
  private volatile boolean running = true;
  
  public static ExecutorLab getInstance()
  {
    if (instance == null) {
      instance = new ExecutorLab();
    }
    return instance;
  }
  
  public static void releaseInstance()
  {
    ExecutorLab localExecutorLab = instance;
    if (localExecutorLab != null)
    {
      localExecutorLab.shutDown();
      instance = null;
    }
  }
  
  private void shutDown()
  {
    if (!this.pool.isShutdown())
    {
      this.running = false;
      this.pool.shutdown();
      for (;;)
      {
        if (!this.pool.isTerminated()) {
          try
          {
            Thread.sleep(100L);
          }
          catch (InterruptedException localInterruptedException)
          {
            for (;;)
            {
              localInterruptedException.printStackTrace();
            }
          }
        }
      }
      this.pool = null;
    }
  }
  
  public void addTask(Runnable paramRunnable)
  {
    if (!this.running)
    {
      Log.d("ExecutorLab", "ExecutorLab is stop");
      return;
    }
    this.pool.execute(paramRunnable);
  }
  
  public boolean isRunning()
  {
    return this.running;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/egret/plugin/mi/android/util/launcher/ExecutorLab.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */