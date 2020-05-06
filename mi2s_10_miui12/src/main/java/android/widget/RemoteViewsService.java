package android.widget;

import android.app.Service;
import android.content.Intent;
import android.content.Intent.FilterComparison;
import android.os.IBinder;
import com.android.internal.widget.IRemoteViewsFactory.Stub;
import java.util.HashMap;

public abstract class RemoteViewsService
  extends Service
{
  private static final String LOG_TAG = "RemoteViewsService";
  private static final Object sLock = new Object();
  private static final HashMap<Intent.FilterComparison, RemoteViewsFactory> sRemoteViewFactories = new HashMap();
  
  public IBinder onBind(Intent paramIntent)
  {
    synchronized (sLock)
    {
      Object localObject2 = new android/content/Intent$FilterComparison;
      ((Intent.FilterComparison)localObject2).<init>(paramIntent);
      boolean bool;
      if (!sRemoteViewFactories.containsKey(localObject2))
      {
        paramIntent = onGetViewFactory(paramIntent);
        sRemoteViewFactories.put(localObject2, paramIntent);
        paramIntent.onCreate();
        bool = false;
      }
      else
      {
        paramIntent = (RemoteViewsFactory)sRemoteViewFactories.get(localObject2);
        bool = true;
      }
      localObject2 = new android/widget/RemoteViewsService$RemoteViewsFactoryAdapter;
      ((RemoteViewsFactoryAdapter)localObject2).<init>(paramIntent, bool);
      return (IBinder)localObject2;
    }
  }
  
  public abstract RemoteViewsFactory onGetViewFactory(Intent paramIntent);
  
  public static abstract interface RemoteViewsFactory
  {
    public abstract int getCount();
    
    public abstract long getItemId(int paramInt);
    
    public abstract RemoteViews getLoadingView();
    
    public abstract RemoteViews getViewAt(int paramInt);
    
    public abstract int getViewTypeCount();
    
    public abstract boolean hasStableIds();
    
    public abstract void onCreate();
    
    public abstract void onDataSetChanged();
    
    public abstract void onDestroy();
  }
  
  private static class RemoteViewsFactoryAdapter
    extends IRemoteViewsFactory.Stub
  {
    private RemoteViewsService.RemoteViewsFactory mFactory;
    private boolean mIsCreated;
    
    public RemoteViewsFactoryAdapter(RemoteViewsService.RemoteViewsFactory paramRemoteViewsFactory, boolean paramBoolean)
    {
      this.mFactory = paramRemoteViewsFactory;
      this.mIsCreated = paramBoolean;
    }
    
    /* Error */
    public int getCount()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: iconst_0
      //   3: istore_1
      //   4: aload_0
      //   5: getfield 18	android/widget/RemoteViewsService$RemoteViewsFactoryAdapter:mFactory	Landroid/widget/RemoteViewsService$RemoteViewsFactory;
      //   8: invokeinterface 29 1 0
      //   13: istore_2
      //   14: goto +26 -> 40
      //   17: astore_3
      //   18: goto +26 -> 44
      //   21: astore 4
      //   23: invokestatic 35	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   26: astore_3
      //   27: invokestatic 39	java/lang/Thread:getDefaultUncaughtExceptionHandler	()Ljava/lang/Thread$UncaughtExceptionHandler;
      //   30: aload_3
      //   31: aload 4
      //   33: invokeinterface 45 3 0
      //   38: iload_1
      //   39: istore_2
      //   40: aload_0
      //   41: monitorexit
      //   42: iload_2
      //   43: ireturn
      //   44: aload_0
      //   45: monitorexit
      //   46: aload_3
      //   47: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	48	0	this	RemoteViewsFactoryAdapter
      //   3	36	1	i	int
      //   13	30	2	j	int
      //   17	1	3	localObject	Object
      //   26	21	3	localThread	Thread
      //   21	11	4	localException	Exception
      // Exception table:
      //   from	to	target	type
      //   4	14	17	finally
      //   23	38	17	finally
      //   4	14	21	java/lang/Exception
    }
    
    /* Error */
    public long getItemId(int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: lconst_0
      //   3: lstore_2
      //   4: aload_0
      //   5: getfield 18	android/widget/RemoteViewsService$RemoteViewsFactoryAdapter:mFactory	Landroid/widget/RemoteViewsService$RemoteViewsFactory;
      //   8: iload_1
      //   9: invokeinterface 49 2 0
      //   14: lstore 4
      //   16: goto +30 -> 46
      //   19: astore 6
      //   21: goto +30 -> 51
      //   24: astore 6
      //   26: invokestatic 35	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   29: astore 7
      //   31: invokestatic 39	java/lang/Thread:getDefaultUncaughtExceptionHandler	()Ljava/lang/Thread$UncaughtExceptionHandler;
      //   34: aload 7
      //   36: aload 6
      //   38: invokeinterface 45 3 0
      //   43: lload_2
      //   44: lstore 4
      //   46: aload_0
      //   47: monitorexit
      //   48: lload 4
      //   50: lreturn
      //   51: aload_0
      //   52: monitorexit
      //   53: aload 6
      //   55: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	56	0	this	RemoteViewsFactoryAdapter
      //   0	56	1	paramInt	int
      //   3	41	2	l1	long
      //   14	35	4	l2	long
      //   19	1	6	localObject	Object
      //   24	30	6	localException	Exception
      //   29	6	7	localThread	Thread
      // Exception table:
      //   from	to	target	type
      //   4	16	19	finally
      //   26	43	19	finally
      //   4	16	24	java/lang/Exception
    }
    
    /* Error */
    public RemoteViews getLoadingView()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aconst_null
      //   3: astore_1
      //   4: aload_0
      //   5: getfield 18	android/widget/RemoteViewsService$RemoteViewsFactoryAdapter:mFactory	Landroid/widget/RemoteViewsService$RemoteViewsFactory;
      //   8: invokeinterface 53 1 0
      //   13: astore_2
      //   14: aload_2
      //   15: astore_1
      //   16: goto +22 -> 38
      //   19: astore_1
      //   20: goto +22 -> 42
      //   23: astore_3
      //   24: invokestatic 35	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   27: astore_2
      //   28: invokestatic 39	java/lang/Thread:getDefaultUncaughtExceptionHandler	()Ljava/lang/Thread$UncaughtExceptionHandler;
      //   31: aload_2
      //   32: aload_3
      //   33: invokeinterface 45 3 0
      //   38: aload_0
      //   39: monitorexit
      //   40: aload_1
      //   41: areturn
      //   42: aload_0
      //   43: monitorexit
      //   44: aload_1
      //   45: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	46	0	this	RemoteViewsFactoryAdapter
      //   3	13	1	localObject1	Object
      //   19	26	1	localRemoteViews	RemoteViews
      //   13	19	2	localObject2	Object
      //   23	10	3	localException	Exception
      // Exception table:
      //   from	to	target	type
      //   4	14	19	finally
      //   24	38	19	finally
      //   4	14	23	java/lang/Exception
    }
    
    /* Error */
    public RemoteViews getViewAt(int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aconst_null
      //   3: astore_2
      //   4: aload_0
      //   5: getfield 18	android/widget/RemoteViewsService$RemoteViewsFactoryAdapter:mFactory	Landroid/widget/RemoteViewsService$RemoteViewsFactory;
      //   8: iload_1
      //   9: invokeinterface 57 2 0
      //   14: astore_3
      //   15: aload_3
      //   16: ifnull +10 -> 26
      //   19: aload_3
      //   20: astore_2
      //   21: aload_3
      //   22: iconst_2
      //   23: invokevirtual 63	android/widget/RemoteViews:addFlags	(I)V
      //   26: aload_3
      //   27: astore_2
      //   28: goto +24 -> 52
      //   31: astore_2
      //   32: goto +24 -> 56
      //   35: astore 4
      //   37: invokestatic 35	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   40: astore_3
      //   41: invokestatic 39	java/lang/Thread:getDefaultUncaughtExceptionHandler	()Ljava/lang/Thread$UncaughtExceptionHandler;
      //   44: aload_3
      //   45: aload 4
      //   47: invokeinterface 45 3 0
      //   52: aload_0
      //   53: monitorexit
      //   54: aload_2
      //   55: areturn
      //   56: aload_0
      //   57: monitorexit
      //   58: aload_2
      //   59: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	60	0	this	RemoteViewsFactoryAdapter
      //   0	60	1	paramInt	int
      //   3	25	2	localObject1	Object
      //   31	28	2	localRemoteViews	RemoteViews
      //   14	31	3	localObject2	Object
      //   35	11	4	localException	Exception
      // Exception table:
      //   from	to	target	type
      //   4	15	31	finally
      //   21	26	31	finally
      //   37	52	31	finally
      //   4	15	35	java/lang/Exception
      //   21	26	35	java/lang/Exception
    }
    
    /* Error */
    public int getViewTypeCount()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: iconst_0
      //   3: istore_1
      //   4: aload_0
      //   5: getfield 18	android/widget/RemoteViewsService$RemoteViewsFactoryAdapter:mFactory	Landroid/widget/RemoteViewsService$RemoteViewsFactory;
      //   8: invokeinterface 66 1 0
      //   13: istore_2
      //   14: iload_2
      //   15: istore_1
      //   16: goto +24 -> 40
      //   19: astore_3
      //   20: goto +24 -> 44
      //   23: astore_3
      //   24: invokestatic 35	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   27: astore 4
      //   29: invokestatic 39	java/lang/Thread:getDefaultUncaughtExceptionHandler	()Ljava/lang/Thread$UncaughtExceptionHandler;
      //   32: aload 4
      //   34: aload_3
      //   35: invokeinterface 45 3 0
      //   40: aload_0
      //   41: monitorexit
      //   42: iload_1
      //   43: ireturn
      //   44: aload_0
      //   45: monitorexit
      //   46: aload_3
      //   47: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	48	0	this	RemoteViewsFactoryAdapter
      //   3	40	1	i	int
      //   13	2	2	j	int
      //   19	1	3	localObject	Object
      //   23	24	3	localException	Exception
      //   27	6	4	localThread	Thread
      // Exception table:
      //   from	to	target	type
      //   4	14	19	finally
      //   24	40	19	finally
      //   4	14	23	java/lang/Exception
    }
    
    /* Error */
    public boolean hasStableIds()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: iconst_0
      //   3: istore_1
      //   4: aload_0
      //   5: getfield 18	android/widget/RemoteViewsService$RemoteViewsFactoryAdapter:mFactory	Landroid/widget/RemoteViewsService$RemoteViewsFactory;
      //   8: invokeinterface 70 1 0
      //   13: istore_2
      //   14: goto +26 -> 40
      //   17: astore_3
      //   18: goto +26 -> 44
      //   21: astore_3
      //   22: invokestatic 35	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   25: astore 4
      //   27: invokestatic 39	java/lang/Thread:getDefaultUncaughtExceptionHandler	()Ljava/lang/Thread$UncaughtExceptionHandler;
      //   30: aload 4
      //   32: aload_3
      //   33: invokeinterface 45 3 0
      //   38: iload_1
      //   39: istore_2
      //   40: aload_0
      //   41: monitorexit
      //   42: iload_2
      //   43: ireturn
      //   44: aload_0
      //   45: monitorexit
      //   46: aload_3
      //   47: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	48	0	this	RemoteViewsFactoryAdapter
      //   3	36	1	bool1	boolean
      //   13	30	2	bool2	boolean
      //   17	1	3	localObject	Object
      //   21	26	3	localException	Exception
      //   25	6	4	localThread	Thread
      // Exception table:
      //   from	to	target	type
      //   4	14	17	finally
      //   22	38	17	finally
      //   4	14	21	java/lang/Exception
    }
    
    public boolean isCreated()
    {
      try
      {
        boolean bool = this.mIsCreated;
        return bool;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    /* Error */
    public void onDataSetChanged()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 18	android/widget/RemoteViewsService$RemoteViewsFactoryAdapter:mFactory	Landroid/widget/RemoteViewsService$RemoteViewsFactory;
      //   6: invokeinterface 74 1 0
      //   11: goto +22 -> 33
      //   14: astore_1
      //   15: goto +21 -> 36
      //   18: astore_2
      //   19: invokestatic 35	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   22: astore_1
      //   23: invokestatic 39	java/lang/Thread:getDefaultUncaughtExceptionHandler	()Ljava/lang/Thread$UncaughtExceptionHandler;
      //   26: aload_1
      //   27: aload_2
      //   28: invokeinterface 45 3 0
      //   33: aload_0
      //   34: monitorexit
      //   35: return
      //   36: aload_0
      //   37: monitorexit
      //   38: aload_1
      //   39: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	40	0	this	RemoteViewsFactoryAdapter
      //   14	1	1	localObject	Object
      //   22	17	1	localThread	Thread
      //   18	10	2	localException	Exception
      // Exception table:
      //   from	to	target	type
      //   2	11	14	finally
      //   19	33	14	finally
      //   2	11	18	java/lang/Exception
    }
    
    public void onDataSetChangedAsync()
    {
      try
      {
        onDataSetChanged();
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public void onDestroy(Intent paramIntent)
    {
      synchronized (RemoteViewsService.sLock)
      {
        Intent.FilterComparison localFilterComparison = new android/content/Intent$FilterComparison;
        localFilterComparison.<init>(paramIntent);
        if (RemoteViewsService.sRemoteViewFactories.containsKey(localFilterComparison))
        {
          paramIntent = (RemoteViewsService.RemoteViewsFactory)RemoteViewsService.sRemoteViewFactories.get(localFilterComparison);
          try
          {
            paramIntent.onDestroy();
          }
          catch (Exception paramIntent)
          {
            Thread localThread = Thread.currentThread();
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(localThread, paramIntent);
          }
          RemoteViewsService.sRemoteViewFactories.remove(localFilterComparison);
        }
        return;
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/RemoteViewsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */