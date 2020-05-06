package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

public abstract class Filter
{
  private static final int FILTER_TOKEN = -791613427;
  private static final int FINISH_TOKEN = -559038737;
  private static final String LOG_TAG = "Filter";
  private static final String THREAD_NAME = "Filter";
  private Delayer mDelayer;
  private final Object mLock = new Object();
  private Handler mResultHandler = new ResultsHandler(null);
  private Handler mThreadHandler;
  
  public CharSequence convertResultToString(Object paramObject)
  {
    if (paramObject == null) {
      paramObject = "";
    } else {
      paramObject = paramObject.toString();
    }
    return (CharSequence)paramObject;
  }
  
  public final void filter(CharSequence paramCharSequence)
  {
    filter(paramCharSequence, null);
  }
  
  public final void filter(CharSequence paramCharSequence, FilterListener paramFilterListener)
  {
    synchronized (this.mLock)
    {
      if (this.mThreadHandler == null)
      {
        localObject2 = new android/os/HandlerThread;
        ((HandlerThread)localObject2).<init>("Filter", 10);
        ((HandlerThread)localObject2).start();
        localObject3 = new android/widget/Filter$RequestHandler;
        ((RequestHandler)localObject3).<init>(this, ((HandlerThread)localObject2).getLooper());
        this.mThreadHandler = ((Handler)localObject3);
      }
      long l;
      if (this.mDelayer == null) {
        l = 0L;
      } else {
        l = this.mDelayer.getPostingDelay(paramCharSequence);
      }
      Message localMessage = this.mThreadHandler.obtainMessage(-791613427);
      Object localObject2 = new android/widget/Filter$RequestArguments;
      Object localObject3 = null;
      ((RequestArguments)localObject2).<init>(null);
      if (paramCharSequence != null) {
        localObject3 = paramCharSequence.toString();
      }
      ((RequestArguments)localObject2).constraint = ((CharSequence)localObject3);
      ((RequestArguments)localObject2).listener = paramFilterListener;
      localMessage.obj = localObject2;
      this.mThreadHandler.removeMessages(-791613427);
      this.mThreadHandler.removeMessages(-559038737);
      this.mThreadHandler.sendMessageDelayed(localMessage, l);
      return;
    }
  }
  
  protected abstract FilterResults performFiltering(CharSequence paramCharSequence);
  
  protected abstract void publishResults(CharSequence paramCharSequence, FilterResults paramFilterResults);
  
  @UnsupportedAppUsage
  public void setDelayer(Delayer paramDelayer)
  {
    synchronized (this.mLock)
    {
      this.mDelayer = paramDelayer;
      return;
    }
  }
  
  public static abstract interface Delayer
  {
    public abstract long getPostingDelay(CharSequence paramCharSequence);
  }
  
  public static abstract interface FilterListener
  {
    public abstract void onFilterComplete(int paramInt);
  }
  
  protected static class FilterResults
  {
    public int count;
    public Object values;
  }
  
  private static class RequestArguments
  {
    CharSequence constraint;
    Filter.FilterListener listener;
    Filter.FilterResults results;
  }
  
  private class RequestHandler
    extends Handler
  {
    public RequestHandler(Looper paramLooper)
    {
      super();
    }
    
    /* Error */
    public void handleMessage(Message paramMessage)
    {
      // Byte code:
      //   0: aload_1
      //   1: getfield 27	android/os/Message:what	I
      //   4: istore_2
      //   5: iload_2
      //   6: ldc 28
      //   8: if_icmpeq +64 -> 72
      //   11: iload_2
      //   12: ldc 29
      //   14: if_icmpeq +6 -> 20
      //   17: goto +185 -> 202
      //   20: aload_0
      //   21: getfield 13	android/widget/Filter$RequestHandler:this$0	Landroid/widget/Filter;
      //   24: invokestatic 33	android/widget/Filter:access$300	(Landroid/widget/Filter;)Ljava/lang/Object;
      //   27: astore_1
      //   28: aload_1
      //   29: monitorenter
      //   30: aload_0
      //   31: getfield 13	android/widget/Filter$RequestHandler:this$0	Landroid/widget/Filter;
      //   34: invokestatic 37	android/widget/Filter:access$400	(Landroid/widget/Filter;)Landroid/os/Handler;
      //   37: ifnull +25 -> 62
      //   40: aload_0
      //   41: getfield 13	android/widget/Filter$RequestHandler:this$0	Landroid/widget/Filter;
      //   44: invokestatic 37	android/widget/Filter:access$400	(Landroid/widget/Filter;)Landroid/os/Handler;
      //   47: invokevirtual 41	android/os/Handler:getLooper	()Landroid/os/Looper;
      //   50: invokevirtual 47	android/os/Looper:quit	()V
      //   53: aload_0
      //   54: getfield 13	android/widget/Filter$RequestHandler:this$0	Landroid/widget/Filter;
      //   57: aconst_null
      //   58: invokestatic 51	android/widget/Filter:access$402	(Landroid/widget/Filter;Landroid/os/Handler;)Landroid/os/Handler;
      //   61: pop
      //   62: aload_1
      //   63: monitorexit
      //   64: goto +138 -> 202
      //   67: astore_3
      //   68: aload_1
      //   69: monitorexit
      //   70: aload_3
      //   71: athrow
      //   72: aload_1
      //   73: getfield 55	android/os/Message:obj	Ljava/lang/Object;
      //   76: checkcast 57	android/widget/Filter$RequestArguments
      //   79: astore_1
      //   80: aload_1
      //   81: aload_0
      //   82: getfield 13	android/widget/Filter$RequestHandler:this$0	Landroid/widget/Filter;
      //   85: aload_1
      //   86: getfield 61	android/widget/Filter$RequestArguments:constraint	Ljava/lang/CharSequence;
      //   89: invokevirtual 65	android/widget/Filter:performFiltering	(Ljava/lang/CharSequence;)Landroid/widget/Filter$FilterResults;
      //   92: putfield 69	android/widget/Filter$RequestArguments:results	Landroid/widget/Filter$FilterResults;
      //   95: aload_0
      //   96: getfield 13	android/widget/Filter$RequestHandler:this$0	Landroid/widget/Filter;
      //   99: invokestatic 72	android/widget/Filter:access$200	(Landroid/widget/Filter;)Landroid/os/Handler;
      //   102: iload_2
      //   103: invokevirtual 76	android/os/Handler:obtainMessage	(I)Landroid/os/Message;
      //   106: astore_3
      //   107: aload_3
      //   108: aload_1
      //   109: putfield 55	android/os/Message:obj	Ljava/lang/Object;
      //   112: aload_3
      //   113: invokevirtual 79	android/os/Message:sendToTarget	()V
      //   116: goto +36 -> 152
      //   119: astore 4
      //   121: goto +87 -> 208
      //   124: astore 4
      //   126: new 81	android/widget/Filter$FilterResults
      //   129: astore_3
      //   130: aload_3
      //   131: invokespecial 83	android/widget/Filter$FilterResults:<init>	()V
      //   134: aload_1
      //   135: aload_3
      //   136: putfield 69	android/widget/Filter$RequestArguments:results	Landroid/widget/Filter$FilterResults;
      //   139: ldc 85
      //   141: ldc 87
      //   143: aload 4
      //   145: invokestatic 93	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   148: pop
      //   149: goto -54 -> 95
      //   152: aload_0
      //   153: getfield 13	android/widget/Filter$RequestHandler:this$0	Landroid/widget/Filter;
      //   156: invokestatic 33	android/widget/Filter:access$300	(Landroid/widget/Filter;)Ljava/lang/Object;
      //   159: astore_1
      //   160: aload_1
      //   161: monitorenter
      //   162: aload_0
      //   163: getfield 13	android/widget/Filter$RequestHandler:this$0	Landroid/widget/Filter;
      //   166: invokestatic 37	android/widget/Filter:access$400	(Landroid/widget/Filter;)Landroid/os/Handler;
      //   169: ifnull +31 -> 200
      //   172: aload_0
      //   173: getfield 13	android/widget/Filter$RequestHandler:this$0	Landroid/widget/Filter;
      //   176: invokestatic 37	android/widget/Filter:access$400	(Landroid/widget/Filter;)Landroid/os/Handler;
      //   179: ldc 29
      //   181: invokevirtual 76	android/os/Handler:obtainMessage	(I)Landroid/os/Message;
      //   184: astore_3
      //   185: aload_0
      //   186: getfield 13	android/widget/Filter$RequestHandler:this$0	Landroid/widget/Filter;
      //   189: invokestatic 37	android/widget/Filter:access$400	(Landroid/widget/Filter;)Landroid/os/Handler;
      //   192: aload_3
      //   193: ldc2_w 94
      //   196: invokevirtual 99	android/os/Handler:sendMessageDelayed	(Landroid/os/Message;J)Z
      //   199: pop
      //   200: aload_1
      //   201: monitorexit
      //   202: return
      //   203: astore_3
      //   204: aload_1
      //   205: monitorexit
      //   206: aload_3
      //   207: athrow
      //   208: aload_0
      //   209: getfield 13	android/widget/Filter$RequestHandler:this$0	Landroid/widget/Filter;
      //   212: invokestatic 72	android/widget/Filter:access$200	(Landroid/widget/Filter;)Landroid/os/Handler;
      //   215: iload_2
      //   216: invokevirtual 76	android/os/Handler:obtainMessage	(I)Landroid/os/Message;
      //   219: astore_3
      //   220: aload_3
      //   221: aload_1
      //   222: putfield 55	android/os/Message:obj	Ljava/lang/Object;
      //   225: aload_3
      //   226: invokevirtual 79	android/os/Message:sendToTarget	()V
      //   229: aload 4
      //   231: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	232	0	this	RequestHandler
      //   0	232	1	paramMessage	Message
      //   4	212	2	i	int
      //   67	4	3	localObject1	Object
      //   106	87	3	localObject2	Object
      //   203	4	3	localObject3	Object
      //   219	7	3	localMessage	Message
      //   119	1	4	localObject4	Object
      //   124	106	4	localException	Exception
      // Exception table:
      //   from	to	target	type
      //   30	62	67	finally
      //   62	64	67	finally
      //   68	70	67	finally
      //   80	95	119	finally
      //   126	149	119	finally
      //   80	95	124	java/lang/Exception
      //   162	200	203	finally
      //   200	202	203	finally
      //   204	206	203	finally
    }
  }
  
  private class ResultsHandler
    extends Handler
  {
    private ResultsHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      paramMessage = (Filter.RequestArguments)paramMessage.obj;
      Filter.this.publishResults(paramMessage.constraint, paramMessage.results);
      if (paramMessage.listener != null)
      {
        int i;
        if (paramMessage.results != null) {
          i = paramMessage.results.count;
        } else {
          i = -1;
        }
        paramMessage.listener.onFilterComplete(i);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/Filter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */