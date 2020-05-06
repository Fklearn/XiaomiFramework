package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.app.IServiceConnection;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.FilterComparison;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import com.android.internal.widget.IRemoteViewsFactory;
import com.android.internal.widget.IRemoteViewsFactory.Stub;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Executor;

public class RemoteViewsAdapter
  extends BaseAdapter
  implements Handler.Callback
{
  private static final int CACHE_RESET_CONFIG_FLAGS = -1073737216;
  private static final int DEFAULT_CACHE_SIZE = 40;
  private static final int DEFAULT_LOADING_VIEW_HEIGHT = 50;
  static final int MSG_LOAD_NEXT_ITEM = 3;
  private static final int MSG_MAIN_HANDLER_COMMIT_METADATA = 1;
  private static final int MSG_MAIN_HANDLER_REMOTE_ADAPTER_CONNECTED = 3;
  private static final int MSG_MAIN_HANDLER_REMOTE_ADAPTER_DISCONNECTED = 4;
  private static final int MSG_MAIN_HANDLER_REMOTE_VIEWS_LOADED = 5;
  private static final int MSG_MAIN_HANDLER_SUPER_NOTIFY_DATA_SET_CHANGED = 2;
  static final int MSG_NOTIFY_DATA_SET_CHANGED = 2;
  static final int MSG_REQUEST_BIND = 1;
  static final int MSG_UNBIND_SERVICE = 4;
  private static final int REMOTE_VIEWS_CACHE_DURATION = 5000;
  private static final String TAG = "RemoteViewsAdapter";
  private static final int UNBIND_SERVICE_DELAY = 5000;
  private static Handler sCacheRemovalQueue;
  private static HandlerThread sCacheRemovalThread;
  private static final HashMap<RemoteViewsCacheKey, FixedSizeRemoteViewsCache> sCachedRemoteViewsCaches = new HashMap();
  private static final HashMap<RemoteViewsCacheKey, Runnable> sRemoteViewsCacheRemoveRunnables = new HashMap();
  private final int mAppWidgetId;
  private final Executor mAsyncViewLoadExecutor;
  @UnsupportedAppUsage
  private final FixedSizeRemoteViewsCache mCache;
  private final RemoteAdapterConnectionCallback mCallback;
  private final Context mContext;
  private boolean mDataReady = false;
  private final Intent mIntent;
  private ApplicationInfo mLastRemoteViewAppInfo;
  private final Handler mMainHandler;
  private final boolean mOnLightBackground;
  private RemoteViews.OnClickHandler mRemoteViewsOnClickHandler;
  private RemoteViewsFrameLayoutRefSet mRequestedViews;
  private final RemoteServiceHandler mServiceHandler;
  private int mVisibleWindowLowerBound;
  private int mVisibleWindowUpperBound;
  @UnsupportedAppUsage
  private final HandlerThread mWorkerThread;
  
  public RemoteViewsAdapter(Context arg1, Intent arg2, RemoteAdapterConnectionCallback paramRemoteAdapterConnectionCallback, boolean paramBoolean)
  {
    this.mContext = ???;
    this.mIntent = ???;
    if (this.mIntent != null)
    {
      this.mAppWidgetId = ???.getIntExtra("remoteAdapterAppWidgetId", -1);
      FixedSizeRemoteViewsCache localFixedSizeRemoteViewsCache = null;
      this.mRequestedViews = new RemoteViewsFrameLayoutRefSet(null);
      this.mOnLightBackground = ???.getBooleanExtra("remoteAdapterOnLightBackground", false);
      ???.removeExtra("remoteAdapterAppWidgetId");
      ???.removeExtra("remoteAdapterOnLightBackground");
      this.mWorkerThread = new HandlerThread("RemoteViewsCache-loader");
      this.mWorkerThread.start();
      this.mMainHandler = new Handler(Looper.myLooper(), this);
      this.mServiceHandler = new RemoteServiceHandler(this.mWorkerThread.getLooper(), this, ???.getApplicationContext());
      ??? = localFixedSizeRemoteViewsCache;
      if (paramBoolean) {
        ??? = new HandlerThreadExecutor(this.mWorkerThread);
      }
      this.mAsyncViewLoadExecutor = ???;
      this.mCallback = paramRemoteAdapterConnectionCallback;
      if (sCacheRemovalThread == null)
      {
        sCacheRemovalThread = new HandlerThread("RemoteViewsAdapter-cachePruner");
        sCacheRemovalThread.start();
        sCacheRemovalQueue = new Handler(sCacheRemovalThread.getLooper());
      }
      paramRemoteAdapterConnectionCallback = new RemoteViewsCacheKey(new Intent.FilterComparison(this.mIntent), this.mAppWidgetId);
      synchronized (sCachedRemoteViewsCaches)
      {
        localFixedSizeRemoteViewsCache = (FixedSizeRemoteViewsCache)sCachedRemoteViewsCaches.get(paramRemoteAdapterConnectionCallback);
        ??? = ???.getResources().getConfiguration();
        if ((localFixedSizeRemoteViewsCache != null) && ((localFixedSizeRemoteViewsCache.mConfiguration.diff(???) & 0xC0001200) == 0))
        {
          this.mCache = ((FixedSizeRemoteViewsCache)sCachedRemoteViewsCaches.get(paramRemoteAdapterConnectionCallback));
          synchronized (this.mCache.mMetaData)
          {
            if (this.mCache.mMetaData.count > 0) {
              this.mDataReady = true;
            }
          }
        }
        paramRemoteAdapterConnectionCallback = new android/widget/RemoteViewsAdapter$FixedSizeRemoteViewsCache;
        paramRemoteAdapterConnectionCallback.<init>(40, ???);
        this.mCache = paramRemoteAdapterConnectionCallback;
        if (!this.mDataReady) {
          requestBindService();
        }
        return;
      }
    }
    throw new IllegalArgumentException("Non-null Intent must be specified.");
  }
  
  private int[] getVisibleWindow(int paramInt)
  {
    int i = this.mVisibleWindowLowerBound;
    int j = this.mVisibleWindowUpperBound;
    if (((i != 0) || (j != 0)) && (i >= 0) && (j >= 0))
    {
      Object localObject;
      if (i <= j)
      {
        localObject = new int[j + 1 - i];
        for (paramInt = 0; i <= j; paramInt++)
        {
          localObject[paramInt] = i;
          i++;
        }
      }
      else
      {
        int k = Math.max(paramInt, i);
        int[] arrayOfInt = new int[k - i + j + 1];
        paramInt = 0;
        int m = 0;
        while (m <= j)
        {
          arrayOfInt[paramInt] = m;
          m++;
          paramInt++;
        }
        for (;;)
        {
          localObject = arrayOfInt;
          if (i >= k) {
            break;
          }
          arrayOfInt[paramInt] = i;
          i++;
          paramInt++;
        }
      }
      return (int[])localObject;
    }
    return new int[0];
  }
  
  private void requestBindService()
  {
    this.mServiceHandler.removeMessages(4);
    Message.obtain(this.mServiceHandler, 1, this.mAppWidgetId, 0, this.mIntent).sendToTarget();
  }
  
  private void updateRemoteViews(IRemoteViewsFactory arg1, int paramInt, boolean paramBoolean)
  {
    try
    {
      RemoteViews localRemoteViews = ???.getViewAt(paramInt);
      long l = ???.getItemId(paramInt);
      if (localRemoteViews != null)
      {
        if (localRemoteViews.mApplication != null)
        {
          ??? = this.mLastRemoteViewAppInfo;
          if ((??? != null) && (localRemoteViews.hasSameAppInfo(???))) {
            localRemoteViews.mApplication = this.mLastRemoteViewAppInfo;
          } else {
            this.mLastRemoteViewAppInfo = localRemoteViews.mApplication;
          }
        }
        int i = localRemoteViews.getLayoutId();
        synchronized (this.mCache.getMetaData())
        {
          boolean bool = ???.isViewTypeInRange(i);
          i = this.mCache.mMetaData.count;
          ??? = this.mCache;
          if (bool) {}
          try
          {
            int[] arrayOfInt = getVisibleWindow(i);
            this.mCache.insert(paramInt, localRemoteViews, l, arrayOfInt);
            if (paramBoolean) {
              Message.obtain(this.mMainHandler, 5, paramInt, 0, localRemoteViews).sendToTarget();
            }
            break label174;
            Log.e("RemoteViewsAdapter", "Error: widget's RemoteViewsFactory returns more view types than  indicated by getViewTypeCount() ");
            label174:
            return;
          }
          finally {}
        }
      }
      ??? = new java/lang/RuntimeException;
      ???.<init>("Null remoteViews");
      throw ???;
    }
    catch (RemoteException|RuntimeException ???)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Error in updateRemoteViews(");
      localStringBuilder.append(paramInt);
      localStringBuilder.append("): ");
      localStringBuilder.append(???.getMessage());
      Log.e("RemoteViewsAdapter", localStringBuilder.toString());
    }
  }
  
  /* Error */
  private void updateTemporaryMetaData(IRemoteViewsFactory arg1)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokeinterface 399 1 0
    //   6: istore_2
    //   7: aload_1
    //   8: invokeinterface 402 1 0
    //   13: istore_3
    //   14: aload_1
    //   15: invokeinterface 405 1 0
    //   20: istore 4
    //   22: new 19	android/widget/RemoteViewsAdapter$LoadingViewTemplate
    //   25: astore 5
    //   27: aload 5
    //   29: aload_1
    //   30: invokeinterface 409 1 0
    //   35: aload_0
    //   36: getfield 125	android/widget/RemoteViewsAdapter:mContext	Landroid/content/Context;
    //   39: invokespecial 412	android/widget/RemoteViewsAdapter$LoadingViewTemplate:<init>	(Landroid/widget/RemoteViews;Landroid/content/Context;)V
    //   42: iload 4
    //   44: ifle +53 -> 97
    //   47: aload 5
    //   49: getfield 416	android/widget/RemoteViewsAdapter$LoadingViewTemplate:remoteViews	Landroid/widget/RemoteViews;
    //   52: ifnonnull +45 -> 97
    //   55: aload_1
    //   56: iconst_0
    //   57: invokeinterface 330 2 0
    //   62: astore 6
    //   64: aload 6
    //   66: ifnull +31 -> 97
    //   69: aload_0
    //   70: getfield 125	android/widget/RemoteViewsAdapter:mContext	Landroid/content/Context;
    //   73: astore 7
    //   75: new 16	android/widget/RemoteViewsAdapter$HandlerThreadExecutor
    //   78: astore_1
    //   79: aload_1
    //   80: aload_0
    //   81: getfield 162	android/widget/RemoteViewsAdapter:mWorkerThread	Landroid/os/HandlerThread;
    //   84: invokespecial 195	android/widget/RemoteViewsAdapter$HandlerThreadExecutor:<init>	(Landroid/os/HandlerThread;)V
    //   87: aload 5
    //   89: aload 6
    //   91: aload 7
    //   93: aload_1
    //   94: invokevirtual 420	android/widget/RemoteViewsAdapter$LoadingViewTemplate:loadFirstViewHeight	(Landroid/widget/RemoteViews;Landroid/content/Context;Ljava/util/concurrent/Executor;)V
    //   97: aload_0
    //   98: getfield 242	android/widget/RemoteViewsAdapter:mCache	Landroid/widget/RemoteViewsAdapter$FixedSizeRemoteViewsCache;
    //   101: invokevirtual 423	android/widget/RemoteViewsAdapter$FixedSizeRemoteViewsCache:getTemporaryMetaData	()Landroid/widget/RemoteViewsAdapter$RemoteViewsMetaData;
    //   104: astore_1
    //   105: aload_1
    //   106: monitorenter
    //   107: aload_1
    //   108: iload_2
    //   109: putfield 425	android/widget/RemoteViewsAdapter$RemoteViewsMetaData:hasStableIds	Z
    //   112: aload_1
    //   113: iload_3
    //   114: iconst_1
    //   115: iadd
    //   116: putfield 428	android/widget/RemoteViewsAdapter$RemoteViewsMetaData:viewTypeCount	I
    //   119: aload_1
    //   120: iload 4
    //   122: putfield 249	android/widget/RemoteViewsAdapter$RemoteViewsMetaData:count	I
    //   125: aload_1
    //   126: aload 5
    //   128: putfield 432	android/widget/RemoteViewsAdapter$RemoteViewsMetaData:loadingTemplate	Landroid/widget/RemoteViewsAdapter$LoadingViewTemplate;
    //   131: aload_1
    //   132: monitorexit
    //   133: goto +101 -> 234
    //   136: astore 5
    //   138: aload_1
    //   139: monitorexit
    //   140: aload 5
    //   142: athrow
    //   143: astore 5
    //   145: new 374	java/lang/StringBuilder
    //   148: dup
    //   149: invokespecial 375	java/lang/StringBuilder:<init>	()V
    //   152: astore_1
    //   153: aload_1
    //   154: ldc_w 434
    //   157: invokevirtual 381	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   160: pop
    //   161: aload_1
    //   162: aload 5
    //   164: invokevirtual 392	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   167: invokevirtual 381	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   170: pop
    //   171: ldc 70
    //   173: aload_1
    //   174: invokevirtual 395	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   177: invokestatic 369	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   180: pop
    //   181: aload_0
    //   182: getfield 242	android/widget/RemoteViewsAdapter:mCache	Landroid/widget/RemoteViewsAdapter$FixedSizeRemoteViewsCache;
    //   185: invokevirtual 353	android/widget/RemoteViewsAdapter$FixedSizeRemoteViewsCache:getMetaData	()Landroid/widget/RemoteViewsAdapter$RemoteViewsMetaData;
    //   188: astore 5
    //   190: aload 5
    //   192: monitorenter
    //   193: aload_0
    //   194: getfield 242	android/widget/RemoteViewsAdapter:mCache	Landroid/widget/RemoteViewsAdapter$FixedSizeRemoteViewsCache;
    //   197: invokevirtual 353	android/widget/RemoteViewsAdapter$FixedSizeRemoteViewsCache:getMetaData	()Landroid/widget/RemoteViewsAdapter$RemoteViewsMetaData;
    //   200: invokevirtual 437	android/widget/RemoteViewsAdapter$RemoteViewsMetaData:reset	()V
    //   203: aload 5
    //   205: monitorexit
    //   206: aload_0
    //   207: getfield 242	android/widget/RemoteViewsAdapter:mCache	Landroid/widget/RemoteViewsAdapter$FixedSizeRemoteViewsCache;
    //   210: astore 5
    //   212: aload 5
    //   214: monitorenter
    //   215: aload_0
    //   216: getfield 242	android/widget/RemoteViewsAdapter:mCache	Landroid/widget/RemoteViewsAdapter$FixedSizeRemoteViewsCache;
    //   219: invokevirtual 438	android/widget/RemoteViewsAdapter$FixedSizeRemoteViewsCache:reset	()V
    //   222: aload 5
    //   224: monitorexit
    //   225: aload_0
    //   226: getfield 178	android/widget/RemoteViewsAdapter:mMainHandler	Landroid/os/Handler;
    //   229: iconst_2
    //   230: invokevirtual 441	android/os/Handler:sendEmptyMessage	(I)Z
    //   233: pop
    //   234: return
    //   235: astore_1
    //   236: aload 5
    //   238: monitorexit
    //   239: aload_1
    //   240: athrow
    //   241: astore_1
    //   242: aload 5
    //   244: monitorexit
    //   245: aload_1
    //   246: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	247	0	this	RemoteViewsAdapter
    //   6	103	2	bool	boolean
    //   13	103	3	i	int
    //   20	101	4	j	int
    //   25	102	5	localLoadingViewTemplate	LoadingViewTemplate
    //   136	5	5	localObject1	Object
    //   143	20	5	localRemoteException	RemoteException
    //   62	28	6	localRemoteViews	RemoteViews
    //   73	19	7	localContext	Context
    // Exception table:
    //   from	to	target	type
    //   107	133	136	finally
    //   138	140	136	finally
    //   0	42	143	android/os/RemoteException
    //   0	42	143	java/lang/RuntimeException
    //   47	64	143	android/os/RemoteException
    //   47	64	143	java/lang/RuntimeException
    //   69	97	143	android/os/RemoteException
    //   69	97	143	java/lang/RuntimeException
    //   97	107	143	android/os/RemoteException
    //   97	107	143	java/lang/RuntimeException
    //   140	143	143	android/os/RemoteException
    //   140	143	143	java/lang/RuntimeException
    //   215	225	235	finally
    //   236	239	235	finally
    //   193	206	241	finally
    //   242	245	241	finally
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      this.mServiceHandler.unbindNow();
      this.mWorkerThread.quit();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public int getCount()
  {
    synchronized (this.mCache.getMetaData())
    {
      int i = ???.count;
      return i;
    }
  }
  
  public Object getItem(int paramInt)
  {
    return null;
  }
  
  public long getItemId(int paramInt)
  {
    synchronized (this.mCache)
    {
      if (this.mCache.containsMetaDataAt(paramInt))
      {
        long l = this.mCache.getMetaDataAt(paramInt).itemId;
        return l;
      }
      return 0L;
    }
  }
  
  public int getItemViewType(int paramInt)
  {
    synchronized (this.mCache)
    {
      if (this.mCache.containsMetaDataAt(paramInt))
      {
        paramInt = this.mCache.getMetaDataAt(paramInt).typeId;
        synchronized (this.mCache.getMetaData())
        {
          paramInt = ((RemoteViewsMetaData)???).getMappedViewType(paramInt);
          return paramInt;
        }
      }
      return 0;
    }
  }
  
  @UnsupportedAppUsage
  public Intent getRemoteViewsServiceIntent()
  {
    return this.mIntent;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    synchronized (this.mCache)
    {
      RemoteViews localRemoteViews = this.mCache.getRemoteViewsAt(paramInt);
      int i;
      if (localRemoteViews != null) {
        i = 1;
      } else {
        i = 0;
      }
      boolean bool = false;
      if ((paramView != null) && ((paramView instanceof RemoteViewsFrameLayout))) {
        this.mRequestedViews.removeView((RemoteViewsFrameLayout)paramView);
      }
      if (i == 0) {
        requestBindService();
      } else {
        bool = this.mCache.queuePositionsToBePreloadedFromRequestedPosition(paramInt);
      }
      if ((paramView instanceof RemoteViewsFrameLayout))
      {
        paramView = (RemoteViewsFrameLayout)paramView;
      }
      else
      {
        paramView = new android/widget/RemoteViewsAdapter$RemoteViewsFrameLayout;
        paramView.<init>(paramViewGroup.getContext(), this.mCache);
        paramView.setExecutor(this.mAsyncViewLoadExecutor);
        paramView.setOnLightBackground(this.mOnLightBackground);
      }
      if (i != 0)
      {
        paramView.onRemoteViewsLoaded(localRemoteViews, this.mRemoteViewsOnClickHandler, false);
        if (bool) {
          this.mServiceHandler.sendEmptyMessage(3);
        }
      }
      else
      {
        paramView.onRemoteViewsLoaded(this.mCache.getMetaData().getLoadingTemplate(this.mContext).remoteViews, this.mRemoteViewsOnClickHandler, false);
        this.mRequestedViews.add(paramInt, paramView);
        this.mCache.queueRequestedPositionToLoad(paramInt);
        this.mServiceHandler.sendEmptyMessage(3);
      }
      return paramView;
    }
  }
  
  public int getViewTypeCount()
  {
    synchronized (this.mCache.getMetaData())
    {
      int i = ???.viewTypeCount;
      return i;
    }
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    int i = paramMessage.what;
    if (i != 1)
    {
      if (i != 2)
      {
        if (i != 3)
        {
          if (i != 4)
          {
            if (i != 5) {
              return false;
            }
            this.mRequestedViews.notifyOnRemoteViewsLoaded(paramMessage.arg1, (RemoteViews)paramMessage.obj);
            return true;
          }
          paramMessage = this.mCallback;
          if (paramMessage != null) {
            paramMessage.onRemoteAdapterDisconnected();
          }
          return true;
        }
        paramMessage = this.mCallback;
        if (paramMessage != null) {
          paramMessage.onRemoteAdapterConnected();
        }
        return true;
      }
      superNotifyDataSetChanged();
      return true;
    }
    this.mCache.commitTemporaryMetaData();
    return true;
  }
  
  public boolean hasStableIds()
  {
    synchronized (this.mCache.getMetaData())
    {
      boolean bool = ???.hasStableIds;
      return bool;
    }
  }
  
  @UnsupportedAppUsage
  public boolean isDataReady()
  {
    return this.mDataReady;
  }
  
  public boolean isEmpty()
  {
    boolean bool;
    if (getCount() <= 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void notifyDataSetChanged()
  {
    this.mServiceHandler.removeMessages(4);
    this.mServiceHandler.sendEmptyMessage(2);
  }
  
  @UnsupportedAppUsage
  public void saveRemoteViewsCache()
  {
    RemoteViewsCacheKey localRemoteViewsCacheKey = new RemoteViewsCacheKey(new Intent.FilterComparison(this.mIntent), this.mAppWidgetId);
    synchronized (sCachedRemoteViewsCaches)
    {
      if (sRemoteViewsCacheRemoveRunnables.containsKey(localRemoteViewsCacheKey))
      {
        sCacheRemovalQueue.removeCallbacks((Runnable)sRemoteViewsCacheRemoveRunnables.get(localRemoteViewsCacheKey));
        sRemoteViewsCacheRemoveRunnables.remove(localRemoteViewsCacheKey);
      }
      synchronized (this.mCache.mMetaData)
      {
        int i = this.mCache.mMetaData.count;
        synchronized (this.mCache)
        {
          int j = this.mCache.mIndexRemoteViews.size();
          if ((i > 0) && (j > 0)) {
            sCachedRemoteViewsCaches.put(localRemoteViewsCacheKey, this.mCache);
          }
          ??? = new android/widget/_$$Lambda$RemoteViewsAdapter$_xHEGE7CkOWJ8u7GAjsH_hc_iiA;
          ((_..Lambda.RemoteViewsAdapter._xHEGE7CkOWJ8u7GAjsH_hc_iiA)???).<init>(localRemoteViewsCacheKey);
          sRemoteViewsCacheRemoveRunnables.put(localRemoteViewsCacheKey, ???);
          sCacheRemovalQueue.postDelayed((Runnable)???, 5000L);
          return;
        }
      }
    }
  }
  
  @UnsupportedAppUsage
  public void setRemoteViewsOnClickHandler(RemoteViews.OnClickHandler paramOnClickHandler)
  {
    this.mRemoteViewsOnClickHandler = paramOnClickHandler;
  }
  
  @UnsupportedAppUsage
  public void setVisibleRangeHint(int paramInt1, int paramInt2)
  {
    this.mVisibleWindowLowerBound = paramInt1;
    this.mVisibleWindowUpperBound = paramInt2;
  }
  
  void superNotifyDataSetChanged()
  {
    super.notifyDataSetChanged();
  }
  
  public static class AsyncRemoteAdapterAction
    implements Runnable
  {
    private final RemoteViewsAdapter.RemoteAdapterConnectionCallback mCallback;
    private final Intent mIntent;
    
    public AsyncRemoteAdapterAction(RemoteViewsAdapter.RemoteAdapterConnectionCallback paramRemoteAdapterConnectionCallback, Intent paramIntent)
    {
      this.mCallback = paramRemoteAdapterConnectionCallback;
      this.mIntent = paramIntent;
    }
    
    public void run()
    {
      this.mCallback.setRemoteViewsAdapter(this.mIntent, true);
    }
  }
  
  private static class FixedSizeRemoteViewsCache
  {
    private static final float sMaxCountSlackPercent = 0.75F;
    private static final int sMaxMemoryLimitInBytes = 2097152;
    private final Configuration mConfiguration;
    private final SparseArray<RemoteViewsAdapter.RemoteViewsIndexMetaData> mIndexMetaData = new SparseArray();
    private final SparseArray<RemoteViews> mIndexRemoteViews = new SparseArray();
    private final SparseBooleanArray mIndicesToLoad = new SparseBooleanArray();
    private int mLastRequestedIndex;
    private final int mMaxCount;
    private final int mMaxCountSlack;
    private final RemoteViewsAdapter.RemoteViewsMetaData mMetaData = new RemoteViewsAdapter.RemoteViewsMetaData();
    private int mPreloadLowerBound;
    private int mPreloadUpperBound;
    private final RemoteViewsAdapter.RemoteViewsMetaData mTemporaryMetaData = new RemoteViewsAdapter.RemoteViewsMetaData();
    
    FixedSizeRemoteViewsCache(int paramInt, Configuration paramConfiguration)
    {
      this.mMaxCount = paramInt;
      this.mMaxCountSlack = Math.round(this.mMaxCount / 2 * 0.75F);
      this.mPreloadLowerBound = 0;
      this.mPreloadUpperBound = -1;
      this.mLastRequestedIndex = -1;
      this.mConfiguration = new Configuration(paramConfiguration);
    }
    
    private int getFarthestPositionFrom(int paramInt, int[] paramArrayOfInt)
    {
      int i = 0;
      int j = -1;
      int k = 0;
      int m = -1;
      int n = this.mIndexRemoteViews.size() - 1;
      while (n >= 0)
      {
        int i1 = this.mIndexRemoteViews.keyAt(n);
        int i2 = Math.abs(i1 - paramInt);
        int i3 = k;
        int i4 = m;
        if (i2 > k)
        {
          i3 = k;
          i4 = m;
          if (Arrays.binarySearch(paramArrayOfInt, i1) < 0)
          {
            i4 = i1;
            i3 = i2;
          }
        }
        m = i;
        if (i2 >= i)
        {
          j = i1;
          m = i2;
        }
        n--;
        i = m;
        k = i3;
        m = i4;
      }
      if (m > -1) {
        return m;
      }
      return j;
    }
    
    private int getRemoteViewsBitmapMemoryUsage()
    {
      int i = 0;
      int j = this.mIndexRemoteViews.size() - 1;
      while (j >= 0)
      {
        RemoteViews localRemoteViews = (RemoteViews)this.mIndexRemoteViews.valueAt(j);
        int k = i;
        if (localRemoteViews != null) {
          k = i + localRemoteViews.estimateMemoryUsage();
        }
        j--;
        i = k;
      }
      return i;
    }
    
    public void commitTemporaryMetaData()
    {
      synchronized (this.mTemporaryMetaData)
      {
        synchronized (this.mMetaData)
        {
          this.mMetaData.set(this.mTemporaryMetaData);
          return;
        }
      }
    }
    
    public boolean containsMetaDataAt(int paramInt)
    {
      boolean bool;
      if (this.mIndexMetaData.indexOfKey(paramInt) >= 0) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public boolean containsRemoteViewAt(int paramInt)
    {
      boolean bool;
      if (this.mIndexRemoteViews.indexOfKey(paramInt) >= 0) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public RemoteViewsAdapter.RemoteViewsMetaData getMetaData()
    {
      return this.mMetaData;
    }
    
    public RemoteViewsAdapter.RemoteViewsIndexMetaData getMetaDataAt(int paramInt)
    {
      return (RemoteViewsAdapter.RemoteViewsIndexMetaData)this.mIndexMetaData.get(paramInt);
    }
    
    public int getNextIndexToLoad()
    {
      synchronized (this.mIndicesToLoad)
      {
        int i = this.mIndicesToLoad.indexOfValue(true);
        int j = i;
        if (i < 0) {
          j = this.mIndicesToLoad.indexOfValue(false);
        }
        if (j < 0) {
          return -1;
        }
        i = this.mIndicesToLoad.keyAt(j);
        this.mIndicesToLoad.removeAt(j);
        return i;
      }
    }
    
    public RemoteViews getRemoteViewsAt(int paramInt)
    {
      return (RemoteViews)this.mIndexRemoteViews.get(paramInt);
    }
    
    public RemoteViewsAdapter.RemoteViewsMetaData getTemporaryMetaData()
    {
      return this.mTemporaryMetaData;
    }
    
    public void insert(int paramInt, RemoteViews paramRemoteViews, long paramLong, int[] paramArrayOfInt)
    {
      if (this.mIndexRemoteViews.size() >= this.mMaxCount) {
        this.mIndexRemoteViews.remove(getFarthestPositionFrom(paramInt, paramArrayOfInt));
      }
      int i = this.mLastRequestedIndex;
      if (i <= -1) {
        i = paramInt;
      }
      while (getRemoteViewsBitmapMemoryUsage() >= 2097152)
      {
        int j = getFarthestPositionFrom(i, paramArrayOfInt);
        if (j < 0) {
          break;
        }
        this.mIndexRemoteViews.remove(j);
      }
      paramArrayOfInt = (RemoteViewsAdapter.RemoteViewsIndexMetaData)this.mIndexMetaData.get(paramInt);
      if (paramArrayOfInt != null) {
        paramArrayOfInt.set(paramRemoteViews, paramLong);
      } else {
        this.mIndexMetaData.put(paramInt, new RemoteViewsAdapter.RemoteViewsIndexMetaData(paramRemoteViews, paramLong));
      }
      this.mIndexRemoteViews.put(paramInt, paramRemoteViews);
    }
    
    public boolean queuePositionsToBePreloadedFromRequestedPosition(int paramInt)
    {
      int i = this.mPreloadLowerBound;
      int j;
      if (i <= paramInt)
      {
        j = this.mPreloadUpperBound;
        if ((paramInt <= j) && (Math.abs(paramInt - (j + i) / 2) < this.mMaxCountSlack)) {
          return false;
        }
      }
      synchronized (this.mMetaData)
      {
        j = this.mMetaData.count;
        synchronized (this.mIndicesToLoad)
        {
          for (i = this.mIndicesToLoad.size() - 1; i >= 0; i--) {
            if (!this.mIndicesToLoad.valueAt(i)) {
              this.mIndicesToLoad.removeAt(i);
            }
          }
          i = this.mMaxCount / 2;
          this.mPreloadLowerBound = (paramInt - i);
          this.mPreloadUpperBound = (paramInt + i);
          paramInt = Math.max(0, this.mPreloadLowerBound);
          i = Math.min(this.mPreloadUpperBound, j - 1);
          while (paramInt <= i)
          {
            if ((this.mIndexRemoteViews.indexOfKey(paramInt) < 0) && (!this.mIndicesToLoad.get(paramInt))) {
              this.mIndicesToLoad.put(paramInt, false);
            }
            paramInt++;
          }
          return true;
        }
      }
    }
    
    public void queueRequestedPositionToLoad(int paramInt)
    {
      this.mLastRequestedIndex = paramInt;
      synchronized (this.mIndicesToLoad)
      {
        this.mIndicesToLoad.put(paramInt, true);
        return;
      }
    }
    
    public void reset()
    {
      this.mPreloadLowerBound = 0;
      this.mPreloadUpperBound = -1;
      this.mLastRequestedIndex = -1;
      this.mIndexRemoteViews.clear();
      this.mIndexMetaData.clear();
      synchronized (this.mIndicesToLoad)
      {
        this.mIndicesToLoad.clear();
        return;
      }
    }
  }
  
  private static class HandlerThreadExecutor
    implements Executor
  {
    private final HandlerThread mThread;
    
    HandlerThreadExecutor(HandlerThread paramHandlerThread)
    {
      this.mThread = paramHandlerThread;
    }
    
    public void execute(Runnable paramRunnable)
    {
      if (Thread.currentThread().getId() == this.mThread.getId()) {
        paramRunnable.run();
      } else {
        new Handler(this.mThread.getLooper()).post(paramRunnable);
      }
    }
  }
  
  private static class LoadingViewTemplate
  {
    public int defaultHeight;
    public final RemoteViews remoteViews;
    
    LoadingViewTemplate(RemoteViews paramRemoteViews, Context paramContext)
    {
      this.remoteViews = paramRemoteViews;
      this.defaultHeight = Math.round(50.0F * paramContext.getResources().getDisplayMetrics().density);
    }
    
    public void loadFirstViewHeight(RemoteViews paramRemoteViews, Context paramContext, Executor paramExecutor)
    {
      paramRemoteViews.applyAsync(paramContext, new RemoteViewsAdapter.RemoteViewsFrameLayout(paramContext, null), paramExecutor, new RemoteViews.OnViewAppliedListener()
      {
        public void onError(Exception paramAnonymousException)
        {
          Log.w("RemoteViewsAdapter", "Error inflating first RemoteViews", paramAnonymousException);
        }
        
        public void onViewApplied(View paramAnonymousView)
        {
          try
          {
            paramAnonymousView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
            RemoteViewsAdapter.LoadingViewTemplate.this.defaultHeight = paramAnonymousView.getMeasuredHeight();
          }
          catch (Exception paramAnonymousView)
          {
            onError(paramAnonymousView);
          }
        }
      });
    }
  }
  
  public static abstract interface RemoteAdapterConnectionCallback
  {
    public abstract void deferNotifyDataSetChanged();
    
    public abstract boolean onRemoteAdapterConnected();
    
    public abstract void onRemoteAdapterDisconnected();
    
    public abstract void setRemoteViewsAdapter(Intent paramIntent, boolean paramBoolean);
  }
  
  private static class RemoteServiceHandler
    extends Handler
    implements ServiceConnection
  {
    private final WeakReference<RemoteViewsAdapter> mAdapter;
    private boolean mBindRequested = false;
    private final Context mContext;
    private boolean mNotifyDataSetChangedPending = false;
    private IRemoteViewsFactory mRemoteViewsFactory;
    
    RemoteServiceHandler(Looper paramLooper, RemoteViewsAdapter paramRemoteViewsAdapter, Context paramContext)
    {
      super();
      this.mAdapter = new WeakReference(paramRemoteViewsAdapter);
      this.mContext = paramContext;
    }
    
    private void enqueueDeferredUnbindServiceMessage()
    {
      removeMessages(4);
      sendEmptyMessageDelayed(4, 5000L);
    }
    
    private boolean sendNotifyDataSetChange(boolean paramBoolean)
    {
      if (!paramBoolean) {}
      try
      {
        if (!this.mRemoteViewsFactory.isCreated()) {
          this.mRemoteViewsFactory.onDataSetChanged();
        }
        return true;
      }
      catch (RemoteException|RuntimeException localRemoteException)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Error in updateNotifyDataSetChanged(): ");
        localStringBuilder.append(localRemoteException.getMessage());
        Log.e("RemoteViewsAdapter", localStringBuilder.toString());
      }
      return false;
    }
    
    public void handleMessage(Message arg1)
    {
      RemoteViewsAdapter localRemoteViewsAdapter = (RemoteViewsAdapter)this.mAdapter.get();
      int i = ???.what;
      if (i != 1)
      {
        if (i != 2)
        {
          if (i != 3)
          {
            if (i != 4) {
              return;
            }
            unbindNow();
            return;
          }
          if ((localRemoteViewsAdapter != null) && (this.mRemoteViewsFactory != null))
          {
            removeMessages(4);
            i = localRemoteViewsAdapter.mCache.getNextIndexToLoad();
            if (i > -1)
            {
              localRemoteViewsAdapter.updateRemoteViews(this.mRemoteViewsFactory, i, true);
              sendEmptyMessage(3);
            }
            else
            {
              enqueueDeferredUnbindServiceMessage();
            }
            return;
          }
          return;
        }
        enqueueDeferredUnbindServiceMessage();
        if (localRemoteViewsAdapter == null) {
          return;
        }
        if (this.mRemoteViewsFactory == null)
        {
          this.mNotifyDataSetChangedPending = true;
          localRemoteViewsAdapter.requestBindService();
          return;
        }
        if (!sendNotifyDataSetChange(true)) {
          return;
        }
        synchronized (localRemoteViewsAdapter.mCache)
        {
          localRemoteViewsAdapter.mCache.reset();
          localRemoteViewsAdapter.updateTemporaryMetaData(this.mRemoteViewsFactory);
          synchronized (localRemoteViewsAdapter.mCache.getTemporaryMetaData())
          {
            int j = localRemoteViewsAdapter.mCache.getTemporaryMetaData().count;
            for (int m : localRemoteViewsAdapter.getVisibleWindow(j)) {
              if (m < j) {
                localRemoteViewsAdapter.updateRemoteViews(this.mRemoteViewsFactory, m, false);
              }
            }
            localRemoteViewsAdapter.mMainHandler.sendEmptyMessage(1);
            localRemoteViewsAdapter.mMainHandler.sendEmptyMessage(2);
            return;
          }
        }
      }
      if ((localObject2 == null) || (this.mRemoteViewsFactory != null)) {
        enqueueDeferredUnbindServiceMessage();
      }
      if (this.mBindRequested) {
        return;
      }
      ??? = this.mContext.getServiceDispatcher(this, this, 33554433);
      Intent localIntent = (Intent)???.obj;
      i = ???.arg1;
      try
      {
        this.mBindRequested = AppWidgetManager.getInstance(this.mContext).bindRemoteViewsService(this.mContext, i, localIntent, (IServiceConnection)???, 33554433);
      }
      catch (Exception localException)
      {
        ??? = new StringBuilder();
        ???.append("Failed to bind remoteViewsService: ");
        ???.append(localException.getMessage());
        Log.e("RemoteViewsAdapter", ???.toString());
      }
    }
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      this.mRemoteViewsFactory = IRemoteViewsFactory.Stub.asInterface(paramIBinder);
      enqueueDeferredUnbindServiceMessage();
      paramComponentName = (RemoteViewsAdapter)this.mAdapter.get();
      if (paramComponentName == null) {
        return;
      }
      if (this.mNotifyDataSetChangedPending)
      {
        this.mNotifyDataSetChangedPending = false;
        paramComponentName = Message.obtain(this, 2);
        handleMessage(paramComponentName);
        paramComponentName.recycle();
      }
      else
      {
        if (!sendNotifyDataSetChange(false)) {
          return;
        }
        paramComponentName.updateTemporaryMetaData(this.mRemoteViewsFactory);
        paramComponentName.mMainHandler.sendEmptyMessage(1);
        paramComponentName.mMainHandler.sendEmptyMessage(3);
      }
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      this.mRemoteViewsFactory = null;
      paramComponentName = (RemoteViewsAdapter)this.mAdapter.get();
      if (paramComponentName != null) {
        paramComponentName.mMainHandler.sendEmptyMessage(4);
      }
    }
    
    protected void unbindNow()
    {
      if (this.mBindRequested)
      {
        this.mBindRequested = false;
        this.mContext.unbindService(this);
      }
      this.mRemoteViewsFactory = null;
    }
  }
  
  static class RemoteViewsCacheKey
  {
    final Intent.FilterComparison filter;
    final int widgetId;
    
    RemoteViewsCacheKey(Intent.FilterComparison paramFilterComparison, int paramInt)
    {
      this.filter = paramFilterComparison;
      this.widgetId = paramInt;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool1 = paramObject instanceof RemoteViewsCacheKey;
      boolean bool2 = false;
      if (!bool1) {
        return false;
      }
      paramObject = (RemoteViewsCacheKey)paramObject;
      bool1 = bool2;
      if (((RemoteViewsCacheKey)paramObject).filter.equals(this.filter))
      {
        bool1 = bool2;
        if (((RemoteViewsCacheKey)paramObject).widgetId == this.widgetId) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public int hashCode()
    {
      Intent.FilterComparison localFilterComparison = this.filter;
      int i;
      if (localFilterComparison == null) {
        i = 0;
      } else {
        i = localFilterComparison.hashCode();
      }
      return i ^ this.widgetId << 2;
    }
  }
  
  static class RemoteViewsFrameLayout
    extends AppWidgetHostView
  {
    public int cacheIndex = -1;
    private final RemoteViewsAdapter.FixedSizeRemoteViewsCache mCache;
    
    public RemoteViewsFrameLayout(Context paramContext, RemoteViewsAdapter.FixedSizeRemoteViewsCache paramFixedSizeRemoteViewsCache)
    {
      super();
      this.mCache = paramFixedSizeRemoteViewsCache;
    }
    
    protected View getDefaultView()
    {
      int i = this.mCache.getMetaData().getLoadingTemplate(getContext()).defaultHeight;
      TextView localTextView = (TextView)LayoutInflater.from(getContext()).inflate(17367281, this, false);
      localTextView.setHeight(i);
      return localTextView;
    }
    
    protected View getErrorView()
    {
      return getDefaultView();
    }
    
    protected Context getRemoteContext()
    {
      return null;
    }
    
    public void onRemoteViewsLoaded(RemoteViews paramRemoteViews, RemoteViews.OnClickHandler paramOnClickHandler, boolean paramBoolean)
    {
      setOnClickHandler(paramOnClickHandler);
      if ((!paramBoolean) && ((paramRemoteViews == null) || (!paramRemoteViews.prefersAsyncApply()))) {
        paramBoolean = false;
      } else {
        paramBoolean = true;
      }
      applyRemoteViews(paramRemoteViews, paramBoolean);
    }
  }
  
  private class RemoteViewsFrameLayoutRefSet
    extends SparseArray<LinkedList<RemoteViewsAdapter.RemoteViewsFrameLayout>>
  {
    private RemoteViewsFrameLayoutRefSet() {}
    
    public void add(int paramInt, RemoteViewsAdapter.RemoteViewsFrameLayout paramRemoteViewsFrameLayout)
    {
      LinkedList localLinkedList1 = (LinkedList)get(paramInt);
      LinkedList localLinkedList2 = localLinkedList1;
      if (localLinkedList1 == null)
      {
        localLinkedList2 = new LinkedList();
        put(paramInt, localLinkedList2);
      }
      paramRemoteViewsFrameLayout.cacheIndex = paramInt;
      localLinkedList2.add(paramRemoteViewsFrameLayout);
    }
    
    public void notifyOnRemoteViewsLoaded(int paramInt, RemoteViews paramRemoteViews)
    {
      if (paramRemoteViews == null) {
        return;
      }
      Object localObject = (LinkedList)removeReturnOld(paramInt);
      if (localObject != null)
      {
        localObject = ((LinkedList)localObject).iterator();
        while (((Iterator)localObject).hasNext()) {
          ((RemoteViewsAdapter.RemoteViewsFrameLayout)((Iterator)localObject).next()).onRemoteViewsLoaded(paramRemoteViews, RemoteViewsAdapter.this.mRemoteViewsOnClickHandler, true);
        }
      }
    }
    
    public void removeView(RemoteViewsAdapter.RemoteViewsFrameLayout paramRemoteViewsFrameLayout)
    {
      if (paramRemoteViewsFrameLayout.cacheIndex < 0) {
        return;
      }
      LinkedList localLinkedList = (LinkedList)get(paramRemoteViewsFrameLayout.cacheIndex);
      if (localLinkedList != null) {
        localLinkedList.remove(paramRemoteViewsFrameLayout);
      }
      paramRemoteViewsFrameLayout.cacheIndex = -1;
    }
  }
  
  private static class RemoteViewsIndexMetaData
  {
    long itemId;
    int typeId;
    
    public RemoteViewsIndexMetaData(RemoteViews paramRemoteViews, long paramLong)
    {
      set(paramRemoteViews, paramLong);
    }
    
    public void set(RemoteViews paramRemoteViews, long paramLong)
    {
      this.itemId = paramLong;
      if (paramRemoteViews != null) {
        this.typeId = paramRemoteViews.getLayoutId();
      } else {
        this.typeId = 0;
      }
    }
  }
  
  private static class RemoteViewsMetaData
  {
    int count;
    boolean hasStableIds;
    RemoteViewsAdapter.LoadingViewTemplate loadingTemplate;
    private final SparseIntArray mTypeIdIndexMap = new SparseIntArray();
    int viewTypeCount;
    
    public RemoteViewsMetaData()
    {
      reset();
    }
    
    public RemoteViewsAdapter.LoadingViewTemplate getLoadingTemplate(Context paramContext)
    {
      try
      {
        if (this.loadingTemplate == null)
        {
          RemoteViewsAdapter.LoadingViewTemplate localLoadingViewTemplate = new android/widget/RemoteViewsAdapter$LoadingViewTemplate;
          localLoadingViewTemplate.<init>(null, paramContext);
          this.loadingTemplate = localLoadingViewTemplate;
        }
        paramContext = this.loadingTemplate;
        return paramContext;
      }
      finally {}
    }
    
    public int getMappedViewType(int paramInt)
    {
      int i = this.mTypeIdIndexMap.get(paramInt, -1);
      int j = i;
      if (i == -1)
      {
        j = this.mTypeIdIndexMap.size() + 1;
        this.mTypeIdIndexMap.put(paramInt, j);
      }
      return j;
    }
    
    public boolean isViewTypeInRange(int paramInt)
    {
      boolean bool;
      if (getMappedViewType(paramInt) < this.viewTypeCount) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public void reset()
    {
      this.count = 0;
      this.viewTypeCount = 1;
      this.hasStableIds = true;
      this.loadingTemplate = null;
      this.mTypeIdIndexMap.clear();
    }
    
    public void set(RemoteViewsMetaData paramRemoteViewsMetaData)
    {
      try
      {
        this.count = paramRemoteViewsMetaData.count;
        this.viewTypeCount = paramRemoteViewsMetaData.viewTypeCount;
        this.hasStableIds = paramRemoteViewsMetaData.hasStableIds;
        this.loadingTemplate = paramRemoteViewsMetaData.loadingTemplate;
        return;
      }
      finally {}
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/RemoteViewsAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */