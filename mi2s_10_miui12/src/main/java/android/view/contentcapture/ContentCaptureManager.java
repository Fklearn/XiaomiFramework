package android.view.contentcapture;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.ContentCaptureOptions;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.WindowManager.LayoutParams;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.IResultReceiver;
import com.android.internal.util.Preconditions;
import com.android.internal.util.SyncResultReceiver;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

public final class ContentCaptureManager
{
  public static final int DEFAULT_IDLE_FLUSHING_FREQUENCY_MS = 5000;
  public static final int DEFAULT_LOG_HISTORY_SIZE = 10;
  public static final int DEFAULT_MAX_BUFFER_SIZE = 100;
  public static final int DEFAULT_TEXT_CHANGE_FLUSHING_FREQUENCY_MS = 1000;
  public static final String DEVICE_CONFIG_PROPERTY_IDLE_FLUSH_FREQUENCY = "idle_flush_frequency";
  public static final String DEVICE_CONFIG_PROPERTY_IDLE_UNBIND_TIMEOUT = "idle_unbind_timeout";
  public static final String DEVICE_CONFIG_PROPERTY_LOGGING_LEVEL = "logging_level";
  public static final String DEVICE_CONFIG_PROPERTY_LOG_HISTORY_SIZE = "log_history_size";
  public static final String DEVICE_CONFIG_PROPERTY_MAX_BUFFER_SIZE = "max_buffer_size";
  public static final String DEVICE_CONFIG_PROPERTY_SERVICE_EXPLICITLY_ENABLED = "service_explicitly_enabled";
  public static final String DEVICE_CONFIG_PROPERTY_TEXT_CHANGE_FLUSH_FREQUENCY = "text_change_flush_frequency";
  public static final int LOGGING_LEVEL_DEBUG = 1;
  public static final int LOGGING_LEVEL_OFF = 0;
  public static final int LOGGING_LEVEL_VERBOSE = 2;
  public static final int RESULT_CODE_FALSE = 2;
  public static final int RESULT_CODE_OK = 0;
  public static final int RESULT_CODE_SECURITY_EXCEPTION = -1;
  public static final int RESULT_CODE_TRUE = 1;
  private static final int SYNC_CALLS_TIMEOUT_MS = 5000;
  private static final String TAG = ContentCaptureManager.class.getSimpleName();
  private final Context mContext;
  @GuardedBy({"mLock"})
  private int mFlags;
  private final Handler mHandler;
  private final Object mLock = new Object();
  @GuardedBy({"mLock"})
  private MainContentCaptureSession mMainSession;
  final ContentCaptureOptions mOptions;
  private final IContentCaptureManager mService;
  
  public ContentCaptureManager(Context paramContext, IContentCaptureManager paramIContentCaptureManager, ContentCaptureOptions paramContentCaptureOptions)
  {
    this.mContext = ((Context)Preconditions.checkNotNull(paramContext, "context cannot be null"));
    this.mService = ((IContentCaptureManager)Preconditions.checkNotNull(paramIContentCaptureManager, "service cannot be null"));
    this.mOptions = ((ContentCaptureOptions)Preconditions.checkNotNull(paramContentCaptureOptions, "options cannot be null"));
    ContentCaptureHelper.setLoggingLevel(this.mOptions.loggingLevel);
    if (ContentCaptureHelper.sVerbose)
    {
      paramIContentCaptureManager = TAG;
      paramContentCaptureOptions = new StringBuilder();
      paramContentCaptureOptions.append("Constructor for ");
      paramContentCaptureOptions.append(paramContext.getPackageName());
      Log.v(paramIContentCaptureManager, paramContentCaptureOptions.toString());
    }
    this.mHandler = Handler.createAsync(Looper.getMainLooper());
  }
  
  public static ComponentName getServiceSettingsComponentName()
  {
    Object localObject1 = ServiceManager.checkService("content_capture");
    if (localObject1 == null) {
      return null;
    }
    Object localObject2 = IContentCaptureManager.Stub.asInterface((IBinder)localObject1);
    localObject1 = new SyncResultReceiver(5000);
    try
    {
      ((IContentCaptureManager)localObject2).getServiceSettingsActivity((IResultReceiver)localObject1);
      if (((SyncResultReceiver)localObject1).getIntResult() != -1) {
        return (ComponentName)((SyncResultReceiver)localObject1).getParcelableResult();
      }
      localObject2 = new java/lang/SecurityException;
      ((SecurityException)localObject2).<init>(((SyncResultReceiver)localObject1).getStringResult());
      throw ((Throwable)localObject2);
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  private SyncResultReceiver syncRun(MyRunnable paramMyRunnable)
  {
    SyncResultReceiver localSyncResultReceiver = new SyncResultReceiver(5000);
    try
    {
      paramMyRunnable.run(localSyncResultReceiver);
      if (localSyncResultReceiver.getIntResult() != -1) {
        return localSyncResultReceiver;
      }
      paramMyRunnable = new java/lang/SecurityException;
      paramMyRunnable.<init>(localSyncResultReceiver.getStringResult());
      throw paramMyRunnable;
    }
    catch (RemoteException paramMyRunnable)
    {
      throw paramMyRunnable.rethrowFromSystemServer();
    }
  }
  
  public void dump(String arg1, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(???);
    paramPrintWriter.println("ContentCaptureManager");
    Object localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append(???);
    ((StringBuilder)localObject1).append("  ");
    localObject1 = ((StringBuilder)localObject1).toString();
    synchronized (this.mLock)
    {
      paramPrintWriter.print((String)localObject1);
      paramPrintWriter.print("isContentCaptureEnabled(): ");
      paramPrintWriter.println(isContentCaptureEnabled());
      paramPrintWriter.print((String)localObject1);
      paramPrintWriter.print("Debug: ");
      paramPrintWriter.print(ContentCaptureHelper.sDebug);
      paramPrintWriter.print(" Verbose: ");
      paramPrintWriter.println(ContentCaptureHelper.sVerbose);
      paramPrintWriter.print((String)localObject1);
      paramPrintWriter.print("Context: ");
      paramPrintWriter.println(this.mContext);
      paramPrintWriter.print((String)localObject1);
      paramPrintWriter.print("User: ");
      paramPrintWriter.println(this.mContext.getUserId());
      paramPrintWriter.print((String)localObject1);
      paramPrintWriter.print("Service: ");
      paramPrintWriter.println(this.mService);
      paramPrintWriter.print((String)localObject1);
      paramPrintWriter.print("Flags: ");
      paramPrintWriter.println(this.mFlags);
      paramPrintWriter.print((String)localObject1);
      paramPrintWriter.print("Options: ");
      this.mOptions.dumpShort(paramPrintWriter);
      paramPrintWriter.println();
      if (this.mMainSession != null)
      {
        Object localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        ((StringBuilder)localObject2).append((String)localObject1);
        ((StringBuilder)localObject2).append("  ");
        localObject2 = ((StringBuilder)localObject2).toString();
        paramPrintWriter.print((String)localObject1);
        paramPrintWriter.println("Main session:");
        this.mMainSession.dump((String)localObject2, paramPrintWriter);
      }
      else
      {
        paramPrintWriter.print((String)localObject1);
        paramPrintWriter.println("No sessions");
      }
      return;
    }
  }
  
  public void flush(int paramInt)
  {
    if (this.mOptions.lite) {
      return;
    }
    getMainContentCaptureSession().flush(paramInt);
  }
  
  public Set<ContentCaptureCondition> getContentCaptureConditions()
  {
    if ((!isContentCaptureEnabled()) && (!this.mOptions.lite)) {
      return null;
    }
    SyncResultReceiver localSyncResultReceiver = syncRun(new _..Lambda.ContentCaptureManager.F5a5O5ubPHwlndmmnmOInl75_sQ(this));
    return ContentCaptureHelper.toSet(localSyncResultReceiver.getParcelableListResult());
  }
  
  public MainContentCaptureSession getMainContentCaptureSession()
  {
    synchronized (this.mLock)
    {
      if (this.mMainSession == null)
      {
        localObject2 = new android/view/contentcapture/MainContentCaptureSession;
        ((MainContentCaptureSession)localObject2).<init>(this.mContext, this, this.mHandler, this.mService);
        this.mMainSession = ((MainContentCaptureSession)localObject2);
        if (ContentCaptureHelper.sVerbose)
        {
          String str = TAG;
          localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          ((StringBuilder)localObject2).append("getMainContentCaptureSession(): created ");
          ((StringBuilder)localObject2).append(this.mMainSession);
          Log.v(str, ((StringBuilder)localObject2).toString());
        }
      }
      Object localObject2 = this.mMainSession;
      return (MainContentCaptureSession)localObject2;
    }
  }
  
  public ComponentName getServiceComponentName()
  {
    if ((!isContentCaptureEnabled()) && (!this.mOptions.lite)) {
      return null;
    }
    Object localObject = new SyncResultReceiver(5000);
    try
    {
      this.mService.getServiceComponentName((IResultReceiver)localObject);
      localObject = (ComponentName)((SyncResultReceiver)localObject).getParcelableResult();
      return (ComponentName)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isContentCaptureEnabled()
  {
    if (this.mOptions.lite) {
      return false;
    }
    synchronized (this.mLock)
    {
      MainContentCaptureSession localMainContentCaptureSession = this.mMainSession;
      return (localMainContentCaptureSession == null) || (!localMainContentCaptureSession.isDisabled());
    }
  }
  
  @SystemApi
  public boolean isContentCaptureFeatureEnabled()
  {
    int i = syncRun(new _..Lambda.ContentCaptureManager.uvjEvSXcmP7_uA6i89N3m1TrKCk(this)).getIntResult();
    if (i != 1)
    {
      if (i != 2)
      {
        String str = TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("received invalid result: ");
        localStringBuilder.append(i);
        Log.wtf(str, localStringBuilder.toString());
        return false;
      }
      return false;
    }
    return true;
  }
  
  public void onActivityCreated(IBinder paramIBinder, ComponentName paramComponentName)
  {
    if (this.mOptions.lite) {
      return;
    }
    synchronized (this.mLock)
    {
      getMainContentCaptureSession().start(paramIBinder, paramComponentName, this.mFlags);
      return;
    }
  }
  
  public void onActivityDestroyed()
  {
    if (this.mOptions.lite) {
      return;
    }
    getMainContentCaptureSession().destroy();
  }
  
  public void onActivityPaused()
  {
    if (this.mOptions.lite) {
      return;
    }
    getMainContentCaptureSession().notifySessionLifecycle(false);
  }
  
  public void onActivityResumed()
  {
    if (this.mOptions.lite) {
      return;
    }
    getMainContentCaptureSession().notifySessionLifecycle(true);
  }
  
  public void removeData(DataRemovalRequest paramDataRemovalRequest)
  {
    Preconditions.checkNotNull(paramDataRemovalRequest);
    try
    {
      this.mService.removeData(paramDataRemovalRequest);
    }
    catch (RemoteException paramDataRemovalRequest)
    {
      paramDataRemovalRequest.rethrowFromSystemServer();
    }
  }
  
  public void setContentCaptureEnabled(boolean paramBoolean)
  {
    Object localObject1;
    if (ContentCaptureHelper.sDebug)
    {
      localObject1 = TAG;
      localObject3 = new StringBuilder();
      ((StringBuilder)localObject3).append("setContentCaptureEnabled(): setting to ");
      ((StringBuilder)localObject3).append(paramBoolean);
      ((StringBuilder)localObject3).append(" for ");
      ((StringBuilder)localObject3).append(this.mContext);
      Log.d((String)localObject1, ((StringBuilder)localObject3).toString());
    }
    Object localObject3 = this.mLock;
    if (paramBoolean) {}
    try
    {
      this.mFlags &= 0xFFFFFFFE;
      break label93;
      this.mFlags |= 0x1;
      label93:
      localObject1 = this.mMainSession;
      if (localObject1 != null) {
        ((MainContentCaptureSession)localObject1).setDisabled(paramBoolean ^ true);
      }
      return;
    }
    finally {}
  }
  
  public void updateWindowAttributes(WindowManager.LayoutParams paramLayoutParams)
  {
    Object localObject1;
    if (ContentCaptureHelper.sDebug)
    {
      String str = TAG;
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("updateWindowAttributes(): window flags=");
      ((StringBuilder)localObject1).append(paramLayoutParams.flags);
      Log.d(str, ((StringBuilder)localObject1).toString());
    }
    boolean bool;
    if ((paramLayoutParams.flags & 0x2000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    paramLayoutParams = this.mLock;
    if (bool) {}
    try
    {
      this.mFlags |= 0x2;
      break label100;
      this.mFlags &= 0xFFFFFFFD;
      label100:
      localObject1 = this.mMainSession;
      if (localObject1 != null) {
        ((MainContentCaptureSession)localObject1).setDisabled(bool);
      }
      return;
    }
    finally {}
  }
  
  public static abstract interface ContentCaptureClient
  {
    public abstract ComponentName contentCaptureClientGetComponentName();
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface LoggingLevel {}
  
  private static abstract interface MyRunnable
  {
    public abstract void run(SyncResultReceiver paramSyncResultReceiver)
      throws RemoteException;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/contentcapture/ContentCaptureManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */