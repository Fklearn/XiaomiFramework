package android.view.contentcapture;

import android.content.ComponentName;
import android.content.ContentCaptureOptions;
import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.LocalLog;
import android.util.Log;
import android.util.TimeUtils;
import android.view.autofill.AutofillId;
import com.android.internal.os.IResultReceiver.Stub;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MainContentCaptureSession
  extends ContentCaptureSession
{
  public static final String EXTRA_BINDER = "binder";
  public static final String EXTRA_ENABLED_STATE = "enabled";
  private static final boolean FORCE_FLUSH = true;
  private static final int MSG_FLUSH = 1;
  private static final String TAG = MainContentCaptureSession.class.getSimpleName();
  private IBinder mApplicationToken;
  private ComponentName mComponentName;
  private final Context mContext;
  private IContentCaptureDirectManager mDirectServiceInterface;
  private IBinder.DeathRecipient mDirectServiceVulture;
  private final AtomicBoolean mDisabled = new AtomicBoolean(false);
  private ArrayList<ContentCaptureEvent> mEvents;
  private final LocalLog mFlushHistory;
  private final Handler mHandler;
  private final ContentCaptureManager mManager;
  private long mNextFlush;
  private boolean mNextFlushForTextChanged = false;
  private final IResultReceiver.Stub mSessionStateReceiver;
  private int mState = 0;
  private final IContentCaptureManager mSystemServerInterface;
  
  protected MainContentCaptureSession(Context paramContext, ContentCaptureManager paramContentCaptureManager, Handler paramHandler, IContentCaptureManager paramIContentCaptureManager)
  {
    this.mContext = paramContext;
    this.mManager = paramContentCaptureManager;
    this.mHandler = paramHandler;
    this.mSystemServerInterface = paramIContentCaptureManager;
    int i = this.mManager.mOptions.logHistorySize;
    if (i > 0) {
      paramContext = new LocalLog(i);
    } else {
      paramContext = null;
    }
    this.mFlushHistory = paramContext;
    this.mSessionStateReceiver = new IResultReceiver.Stub()
    {
      public void send(int paramAnonymousInt, Bundle paramAnonymousBundle)
      {
        if (paramAnonymousBundle != null)
        {
          if (paramAnonymousBundle.getBoolean("enabled"))
          {
            boolean bool;
            if (paramAnonymousInt == 2) {
              bool = true;
            } else {
              bool = false;
            }
            MainContentCaptureSession.this.mDisabled.set(bool);
            return;
          }
          paramAnonymousBundle = paramAnonymousBundle.getBinder("binder");
          if (paramAnonymousBundle == null)
          {
            Log.wtf(MainContentCaptureSession.TAG, "No binder extra result");
            MainContentCaptureSession.this.mHandler.post(new _..Lambda.MainContentCaptureSession.1.JPRO_nNGZpgXrKr4QC_iQiTbQx0(this));
            return;
          }
        }
        else
        {
          paramAnonymousBundle = null;
        }
        MainContentCaptureSession.this.mHandler.post(new _..Lambda.MainContentCaptureSession.1.Xhq3WJibbalS1G_W3PRC2m7muhM(this, paramAnonymousInt, paramAnonymousBundle));
      }
    };
  }
  
  private ParceledListSlice<ContentCaptureEvent> clearEvents()
  {
    Object localObject = this.mEvents;
    if (localObject == null) {
      localObject = Collections.emptyList();
    }
    this.mEvents = null;
    return new ParceledListSlice((List)localObject);
  }
  
  private void destroySession()
  {
    String str;
    StringBuilder localStringBuilder;
    if (ContentCaptureHelper.sDebug)
    {
      str = TAG;
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Destroying session (ctx=");
      localStringBuilder.append(this.mContext);
      localStringBuilder.append(", id=");
      localStringBuilder.append(this.mId);
      localStringBuilder.append(") with ");
      ArrayList localArrayList = this.mEvents;
      int i;
      if (localArrayList == null) {
        i = 0;
      } else {
        i = localArrayList.size();
      }
      localStringBuilder.append(i);
      localStringBuilder.append(" event(s) for ");
      localStringBuilder.append(getDebugState());
      Log.d(str, localStringBuilder.toString());
    }
    try
    {
      this.mSystemServerInterface.finishSession(this.mId);
    }
    catch (RemoteException localRemoteException)
    {
      str = TAG;
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Error destroying system-service session ");
      localStringBuilder.append(this.mId);
      localStringBuilder.append(" for ");
      localStringBuilder.append(getDebugState());
      localStringBuilder.append(": ");
      localStringBuilder.append(localRemoteException);
      Log.e(str, localStringBuilder.toString());
    }
  }
  
  private void flushIfNeeded(int paramInt)
  {
    ArrayList localArrayList = this.mEvents;
    if ((localArrayList != null) && (!localArrayList.isEmpty()))
    {
      flush(paramInt);
      return;
    }
    if (ContentCaptureHelper.sVerbose) {
      Log.v(TAG, "Nothing to flush");
    }
  }
  
  private String getActivityName()
  {
    Object localObject;
    if (this.mComponentName == null)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("pkg:");
      ((StringBuilder)localObject).append(this.mContext.getPackageName());
      localObject = ((StringBuilder)localObject).toString();
    }
    else
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("act:");
      ((StringBuilder)localObject).append(this.mComponentName.flattenToShortString());
      localObject = ((StringBuilder)localObject).toString();
    }
    return (String)localObject;
  }
  
  private String getDebugState()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(getActivityName());
    localStringBuilder.append(" [state=");
    localStringBuilder.append(getStateAsString(this.mState));
    localStringBuilder.append(", disabled=");
    localStringBuilder.append(this.mDisabled.get());
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  private String getDebugState(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(getDebugState());
    localStringBuilder.append(", reason=");
    localStringBuilder.append(getFlushReasonAsString(paramInt));
    return localStringBuilder.toString();
  }
  
  private boolean hasStarted()
  {
    boolean bool;
    if (this.mState != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private void onSessionStarted(int paramInt, IBinder paramIBinder)
  {
    int i = 0;
    Object localObject;
    if (paramIBinder != null)
    {
      this.mDirectServiceInterface = IContentCaptureDirectManager.Stub.asInterface(paramIBinder);
      this.mDirectServiceVulture = new _..Lambda.MainContentCaptureSession.UWslDbWedtPhv49PtRsvG4TlYWw(this);
      try
      {
        paramIBinder.linkToDeath(this.mDirectServiceVulture, 0);
      }
      catch (RemoteException localRemoteException)
      {
        localObject = TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Failed to link to death on ");
        localStringBuilder.append(paramIBinder);
        localStringBuilder.append(": ");
        localStringBuilder.append(localRemoteException);
        Log.w((String)localObject, localStringBuilder.toString());
      }
    }
    if ((paramInt & 0x4) != 0)
    {
      resetSession(paramInt);
    }
    else
    {
      this.mState = paramInt;
      this.mDisabled.set(false);
    }
    if (ContentCaptureHelper.sVerbose)
    {
      String str = TAG;
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("handleSessionStarted() result: id=");
      ((StringBuilder)localObject).append(this.mId);
      ((StringBuilder)localObject).append(" resultCode=");
      ((StringBuilder)localObject).append(paramInt);
      ((StringBuilder)localObject).append(", state=");
      ((StringBuilder)localObject).append(getStateAsString(this.mState));
      ((StringBuilder)localObject).append(", disabled=");
      ((StringBuilder)localObject).append(this.mDisabled.get());
      ((StringBuilder)localObject).append(", binder=");
      ((StringBuilder)localObject).append(paramIBinder);
      ((StringBuilder)localObject).append(", events=");
      paramIBinder = this.mEvents;
      if (paramIBinder == null) {
        paramInt = i;
      } else {
        paramInt = paramIBinder.size();
      }
      ((StringBuilder)localObject).append(paramInt);
      Log.v(str, ((StringBuilder)localObject).toString());
    }
  }
  
  private void resetSession(int paramInt)
  {
    if (ContentCaptureHelper.sVerbose)
    {
      localObject = TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("handleResetSession(");
      localStringBuilder.append(getActivityName());
      localStringBuilder.append("): from ");
      localStringBuilder.append(getStateAsString(this.mState));
      localStringBuilder.append(" to ");
      localStringBuilder.append(getStateAsString(paramInt));
      Log.v((String)localObject, localStringBuilder.toString());
    }
    this.mState = paramInt;
    Object localObject = this.mDisabled;
    boolean bool;
    if ((paramInt & 0x4) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    ((AtomicBoolean)localObject).set(bool);
    this.mApplicationToken = null;
    this.mComponentName = null;
    this.mEvents = null;
    localObject = this.mDirectServiceInterface;
    if (localObject != null) {
      ((IContentCaptureDirectManager)localObject).asBinder().unlinkToDeath(this.mDirectServiceVulture, 0);
    }
    this.mDirectServiceInterface = null;
    this.mHandler.removeMessages(1);
  }
  
  private void scheduleFlush(int paramInt, boolean paramBoolean)
  {
    if (ContentCaptureHelper.sVerbose)
    {
      localObject1 = TAG;
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("handleScheduleFlush(");
      ((StringBuilder)localObject2).append(getDebugState(paramInt));
      ((StringBuilder)localObject2).append(", checkExisting=");
      ((StringBuilder)localObject2).append(paramBoolean);
      Log.v((String)localObject1, ((StringBuilder)localObject2).toString());
    }
    if (!hasStarted())
    {
      if (ContentCaptureHelper.sVerbose) {
        Log.v(TAG, "handleScheduleFlush(): session not started yet");
      }
      return;
    }
    if (this.mDisabled.get())
    {
      String str = TAG;
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("handleScheduleFlush(");
      ((StringBuilder)localObject2).append(getDebugState(paramInt));
      ((StringBuilder)localObject2).append("): should not be called when disabled. events=");
      localObject1 = this.mEvents;
      if (localObject1 == null) {
        localObject1 = null;
      } else {
        localObject1 = Integer.valueOf(((ArrayList)localObject1).size());
      }
      ((StringBuilder)localObject2).append(localObject1);
      Log.e(str, ((StringBuilder)localObject2).toString());
      return;
    }
    if ((paramBoolean) && (this.mHandler.hasMessages(1))) {
      this.mHandler.removeMessages(1);
    }
    int i;
    if (paramInt == 5)
    {
      i = this.mManager.mOptions.idleFlushingFrequencyMs;
    }
    else
    {
      if (paramInt != 6) {
        break label341;
      }
      i = this.mManager.mOptions.textChangeFlushingFrequencyMs;
    }
    this.mNextFlush = (System.currentTimeMillis() + i);
    if (ContentCaptureHelper.sVerbose)
    {
      localObject2 = TAG;
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("handleScheduleFlush(): scheduled to flush in ");
      ((StringBuilder)localObject1).append(i);
      ((StringBuilder)localObject1).append("ms: ");
      ((StringBuilder)localObject1).append(TimeUtils.logTimeOfDay(this.mNextFlush));
      Log.v((String)localObject2, ((StringBuilder)localObject1).toString());
    }
    this.mHandler.postDelayed(new _..Lambda.MainContentCaptureSession.49zT7C2BXrEdkyggyGk1Qs4d46k(this, paramInt), 1, i);
    return;
    label341:
    Object localObject1 = TAG;
    Object localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append("handleScheduleFlush(");
    ((StringBuilder)localObject2).append(getDebugState(paramInt));
    ((StringBuilder)localObject2).append("): not called with a timeout reason.");
    Log.e((String)localObject1, ((StringBuilder)localObject2).toString());
  }
  
  private void sendEvent(ContentCaptureEvent paramContentCaptureEvent)
  {
    sendEvent(paramContentCaptureEvent, false);
  }
  
  private void sendEvent(ContentCaptureEvent paramContentCaptureEvent, boolean paramBoolean)
  {
    int i = paramContentCaptureEvent.getType();
    Object localObject1;
    StringBuilder localStringBuilder;
    if (ContentCaptureHelper.sVerbose)
    {
      localObject1 = TAG;
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("handleSendEvent(");
      localStringBuilder.append(getDebugState());
      localStringBuilder.append("): ");
      localStringBuilder.append(paramContentCaptureEvent);
      Log.v((String)localObject1, localStringBuilder.toString());
    }
    if ((!hasStarted()) && (i != -1) && (i != 6))
    {
      paramContentCaptureEvent = TAG;
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("handleSendEvent(");
      ((StringBuilder)localObject1).append(getDebugState());
      ((StringBuilder)localObject1).append(", ");
      ((StringBuilder)localObject1).append(ContentCaptureEvent.getTypeAsString(i));
      ((StringBuilder)localObject1).append("): dropping because session not started yet");
      Log.v(paramContentCaptureEvent, ((StringBuilder)localObject1).toString());
      return;
    }
    if (this.mDisabled.get())
    {
      if (ContentCaptureHelper.sVerbose) {
        Log.v(TAG, "handleSendEvent(): ignoring when disabled");
      }
      return;
    }
    int j = this.mManager.mOptions.maxBufferSize;
    if (this.mEvents == null)
    {
      if (ContentCaptureHelper.sVerbose)
      {
        localObject1 = TAG;
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("handleSendEvent(): creating buffer for ");
        localStringBuilder.append(j);
        localStringBuilder.append(" events");
        Log.v((String)localObject1, localStringBuilder.toString());
      }
      this.mEvents = new ArrayList(j);
    }
    int k = 1;
    int m = k;
    Object localObject2;
    if (!this.mEvents.isEmpty())
    {
      m = k;
      if (i == 3)
      {
        localObject1 = this.mEvents;
        localObject2 = (ContentCaptureEvent)((ArrayList)localObject1).get(((ArrayList)localObject1).size() - 1);
        m = k;
        if (((ContentCaptureEvent)localObject2).getType() == 3)
        {
          m = k;
          if (((ContentCaptureEvent)localObject2).getId().equals(paramContentCaptureEvent.getId()))
          {
            if (ContentCaptureHelper.sVerbose)
            {
              localObject1 = TAG;
              localStringBuilder = new StringBuilder();
              localStringBuilder.append("Buffering VIEW_TEXT_CHANGED event, updated text=");
              localStringBuilder.append(ContentCaptureHelper.getSanitizedString(paramContentCaptureEvent.getText()));
              Log.v((String)localObject1, localStringBuilder.toString());
            }
            ((ContentCaptureEvent)localObject2).mergeEvent(paramContentCaptureEvent);
            m = 0;
          }
        }
      }
    }
    k = m;
    if (!this.mEvents.isEmpty())
    {
      k = m;
      if (i == 2)
      {
        localObject1 = this.mEvents;
        localObject1 = (ContentCaptureEvent)((ArrayList)localObject1).get(((ArrayList)localObject1).size() - 1);
        k = m;
        if (((ContentCaptureEvent)localObject1).getType() == 2)
        {
          k = m;
          if (paramContentCaptureEvent.getSessionId() == ((ContentCaptureEvent)localObject1).getSessionId())
          {
            if (ContentCaptureHelper.sVerbose)
            {
              localObject2 = TAG;
              localStringBuilder = new StringBuilder();
              localStringBuilder.append("Buffering TYPE_VIEW_DISAPPEARED events for session ");
              localStringBuilder.append(((ContentCaptureEvent)localObject1).getSessionId());
              Log.v((String)localObject2, localStringBuilder.toString());
            }
            ((ContentCaptureEvent)localObject1).mergeEvent(paramContentCaptureEvent);
            k = 0;
          }
        }
      }
    }
    if (k != 0) {
      this.mEvents.add(paramContentCaptureEvent);
    }
    k = this.mEvents.size();
    if (k < j) {
      m = 1;
    } else {
      m = 0;
    }
    if ((m != 0) && (!paramBoolean))
    {
      if (i == 3)
      {
        this.mNextFlushForTextChanged = true;
        m = 6;
      }
      else
      {
        if (this.mNextFlushForTextChanged)
        {
          if (ContentCaptureHelper.sVerbose) {
            Log.i(TAG, "Not scheduling flush because next flush is for text changed");
          }
          return;
        }
        m = 5;
      }
      scheduleFlush(m, true);
      return;
    }
    if ((this.mState != 2) && (k >= j))
    {
      if (ContentCaptureHelper.sDebug)
      {
        localObject1 = TAG;
        paramContentCaptureEvent = new StringBuilder();
        paramContentCaptureEvent.append("Closing session for ");
        paramContentCaptureEvent.append(getDebugState());
        paramContentCaptureEvent.append(" after ");
        paramContentCaptureEvent.append(k);
        paramContentCaptureEvent.append(" delayed events");
        Log.d((String)localObject1, paramContentCaptureEvent.toString());
      }
      resetSession(132);
      return;
    }
    if (i != -2)
    {
      if (i != -1) {
        m = 1;
      } else {
        m = 3;
      }
    }
    else {
      m = 4;
    }
    flush(m);
  }
  
  void dump(String paramString, PrintWriter paramPrintWriter)
  {
    super.dump(paramString, paramPrintWriter);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mContext: ");
    paramPrintWriter.println(this.mContext);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("user: ");
    paramPrintWriter.println(this.mContext.getUserId());
    if (this.mDirectServiceInterface != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mDirectServiceInterface: ");
      paramPrintWriter.println(this.mDirectServiceInterface);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mDisabled: ");
    paramPrintWriter.println(this.mDisabled.get());
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("isEnabled(): ");
    paramPrintWriter.println(isContentCaptureEnabled());
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("state: ");
    paramPrintWriter.println(getStateAsString(this.mState));
    if (this.mApplicationToken != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("app token: ");
      paramPrintWriter.println(this.mApplicationToken);
    }
    if (this.mComponentName != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("component name: ");
      paramPrintWriter.println(this.mComponentName.flattenToShortString());
    }
    Object localObject = this.mEvents;
    if ((localObject != null) && (!((ArrayList)localObject).isEmpty()))
    {
      int i = this.mEvents.size();
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("buffered events: ");
      paramPrintWriter.print(i);
      paramPrintWriter.print('/');
      paramPrintWriter.println(this.mManager.mOptions.maxBufferSize);
      if ((ContentCaptureHelper.sVerbose) && (i > 0))
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append(paramString);
        ((StringBuilder)localObject).append("  ");
        String str = ((StringBuilder)localObject).toString();
        for (int j = 0; j < i; j++)
        {
          localObject = (ContentCaptureEvent)this.mEvents.get(j);
          paramPrintWriter.print(str);
          paramPrintWriter.print(j);
          paramPrintWriter.print(": ");
          ((ContentCaptureEvent)localObject).dump(paramPrintWriter);
          paramPrintWriter.println();
        }
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mNextFlushForTextChanged: ");
      paramPrintWriter.println(this.mNextFlushForTextChanged);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("flush frequency: ");
      if (this.mNextFlushForTextChanged) {
        paramPrintWriter.println(this.mManager.mOptions.textChangeFlushingFrequencyMs);
      } else {
        paramPrintWriter.println(this.mManager.mOptions.idleFlushingFrequencyMs);
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("next flush: ");
      TimeUtils.formatDuration(this.mNextFlush - System.currentTimeMillis(), paramPrintWriter);
      paramPrintWriter.print(" (");
      paramPrintWriter.print(TimeUtils.logTimeOfDay(this.mNextFlush));
      paramPrintWriter.println(")");
    }
    if (this.mFlushHistory != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("flush history:");
      this.mFlushHistory.reverseDump(null, paramPrintWriter, null);
      paramPrintWriter.println();
    }
    else
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("not logging flush history");
    }
    super.dump(paramString, paramPrintWriter);
  }
  
  void flush(int paramInt)
  {
    if (this.mEvents == null) {
      return;
    }
    Object localObject1;
    if (this.mDisabled.get())
    {
      localObject1 = TAG;
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("handleForceFlush(");
      ((StringBuilder)localObject2).append(getDebugState(paramInt));
      ((StringBuilder)localObject2).append("): should not be when disabled");
      Log.e((String)localObject1, ((StringBuilder)localObject2).toString());
      return;
    }
    if (this.mDirectServiceInterface == null)
    {
      if (ContentCaptureHelper.sVerbose)
      {
        localObject1 = TAG;
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("handleForceFlush(");
        ((StringBuilder)localObject2).append(getDebugState(paramInt));
        ((StringBuilder)localObject2).append("): hold your horses, client not ready: ");
        ((StringBuilder)localObject2).append(this.mEvents);
        Log.v((String)localObject1, ((StringBuilder)localObject2).toString());
      }
      if (!this.mHandler.hasMessages(1)) {
        scheduleFlush(paramInt, false);
      }
      return;
    }
    int i = this.mEvents.size();
    Object localObject2 = getFlushReasonAsString(paramInt);
    if (ContentCaptureHelper.sDebug)
    {
      String str = TAG;
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("Flushing ");
      ((StringBuilder)localObject1).append(i);
      ((StringBuilder)localObject1).append(" event(s) for ");
      ((StringBuilder)localObject1).append(getDebugState(paramInt));
      Log.d(str, ((StringBuilder)localObject1).toString());
    }
    if (this.mFlushHistory != null)
    {
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("r=");
      ((StringBuilder)localObject1).append((String)localObject2);
      ((StringBuilder)localObject1).append(" s=");
      ((StringBuilder)localObject1).append(i);
      ((StringBuilder)localObject1).append(" m=");
      ((StringBuilder)localObject1).append(this.mManager.mOptions.maxBufferSize);
      ((StringBuilder)localObject1).append(" i=");
      ((StringBuilder)localObject1).append(this.mManager.mOptions.idleFlushingFrequencyMs);
      localObject2 = ((StringBuilder)localObject1).toString();
      this.mFlushHistory.log((String)localObject2);
    }
    try
    {
      this.mHandler.removeMessages(1);
      if (paramInt == 6) {
        this.mNextFlushForTextChanged = false;
      }
      localObject2 = clearEvents();
      this.mDirectServiceInterface.sendEvents((ParceledListSlice)localObject2, paramInt, this.mManager.mOptions);
    }
    catch (RemoteException localRemoteException)
    {
      localObject2 = TAG;
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("Error sending ");
      ((StringBuilder)localObject1).append(i);
      ((StringBuilder)localObject1).append(" for ");
      ((StringBuilder)localObject1).append(getDebugState());
      ((StringBuilder)localObject1).append(": ");
      ((StringBuilder)localObject1).append(localRemoteException);
      Log.w((String)localObject2, ((StringBuilder)localObject1).toString());
    }
  }
  
  MainContentCaptureSession getMainCaptureSession()
  {
    return this;
  }
  
  void internalNotifyViewAppeared(ViewNode.ViewStructureImpl paramViewStructureImpl)
  {
    notifyViewAppeared(this.mId, paramViewStructureImpl);
  }
  
  void internalNotifyViewDisappeared(AutofillId paramAutofillId)
  {
    notifyViewDisappeared(this.mId, paramAutofillId);
  }
  
  void internalNotifyViewTextChanged(AutofillId paramAutofillId, CharSequence paramCharSequence)
  {
    notifyViewTextChanged(this.mId, paramAutofillId, paramCharSequence);
  }
  
  public void internalNotifyViewTreeEvent(boolean paramBoolean)
  {
    notifyViewTreeEvent(this.mId, paramBoolean);
  }
  
  boolean isContentCaptureEnabled()
  {
    boolean bool;
    if ((super.isContentCaptureEnabled()) && (this.mManager.isContentCaptureEnabled())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  boolean isDisabled()
  {
    return this.mDisabled.get();
  }
  
  ContentCaptureSession newChild(ContentCaptureContext paramContentCaptureContext)
  {
    ChildContentCaptureSession localChildContentCaptureSession = new ChildContentCaptureSession(this, paramContentCaptureContext);
    notifyChildSessionStarted(this.mId, localChildContentCaptureSession.mId, paramContentCaptureContext);
    return localChildContentCaptureSession;
  }
  
  void notifyChildSessionFinished(int paramInt1, int paramInt2)
  {
    sendEvent(new ContentCaptureEvent(paramInt2, -2).setParentSessionId(paramInt1), true);
  }
  
  void notifyChildSessionStarted(int paramInt1, int paramInt2, ContentCaptureContext paramContentCaptureContext)
  {
    sendEvent(new ContentCaptureEvent(paramInt2, -1).setParentSessionId(paramInt1).setClientContext(paramContentCaptureContext), true);
  }
  
  void notifyContextUpdated(int paramInt, ContentCaptureContext paramContentCaptureContext)
  {
    sendEvent(new ContentCaptureEvent(paramInt, 6).setClientContext(paramContentCaptureContext));
  }
  
  public void notifySessionLifecycle(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 7;
    } else {
      i = 8;
    }
    sendEvent(new ContentCaptureEvent(this.mId, i), true);
  }
  
  void notifyViewAppeared(int paramInt, ViewNode.ViewStructureImpl paramViewStructureImpl)
  {
    sendEvent(new ContentCaptureEvent(paramInt, 1).setViewNode(paramViewStructureImpl.mNode));
  }
  
  public void notifyViewDisappeared(int paramInt, AutofillId paramAutofillId)
  {
    sendEvent(new ContentCaptureEvent(paramInt, 2).setAutofillId(paramAutofillId));
  }
  
  void notifyViewTextChanged(int paramInt, AutofillId paramAutofillId, CharSequence paramCharSequence)
  {
    sendEvent(new ContentCaptureEvent(paramInt, 3).setAutofillId(paramAutofillId).setText(paramCharSequence));
  }
  
  public void notifyViewTreeEvent(int paramInt, boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 4;
    } else {
      i = 5;
    }
    sendEvent(new ContentCaptureEvent(paramInt, i), true);
  }
  
  void onDestroy()
  {
    this.mHandler.removeMessages(1);
    this.mHandler.post(new _..Lambda.MainContentCaptureSession.HTmdDf687TPcaTnLyPp3wo0gI60(this));
  }
  
  boolean setDisabled(boolean paramBoolean)
  {
    return this.mDisabled.compareAndSet(paramBoolean ^ true, paramBoolean);
  }
  
  void start(IBinder paramIBinder, ComponentName paramComponentName, int paramInt)
  {
    if (!isContentCaptureEnabled()) {
      return;
    }
    String str;
    StringBuilder localStringBuilder;
    if (ContentCaptureHelper.sVerbose)
    {
      str = TAG;
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("start(): token=");
      localStringBuilder.append(paramIBinder);
      localStringBuilder.append(", comp=");
      localStringBuilder.append(ComponentName.flattenToShortString(paramComponentName));
      Log.v(str, localStringBuilder.toString());
    }
    if (hasStarted())
    {
      if (ContentCaptureHelper.sDebug)
      {
        str = TAG;
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("ignoring handleStartSession(");
        localStringBuilder.append(paramIBinder);
        localStringBuilder.append("/");
        localStringBuilder.append(ComponentName.flattenToShortString(paramComponentName));
        localStringBuilder.append(" while on state ");
        localStringBuilder.append(getStateAsString(this.mState));
        Log.d(str, localStringBuilder.toString());
      }
      return;
    }
    this.mState = 1;
    this.mApplicationToken = paramIBinder;
    this.mComponentName = paramComponentName;
    if (ContentCaptureHelper.sVerbose)
    {
      str = TAG;
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("handleStartSession(): token=");
      localStringBuilder.append(paramIBinder);
      localStringBuilder.append(", act=");
      localStringBuilder.append(getDebugState());
      localStringBuilder.append(", id=");
      localStringBuilder.append(this.mId);
      Log.v(str, localStringBuilder.toString());
    }
    try
    {
      this.mSystemServerInterface.startSession(this.mApplicationToken, paramComponentName, this.mId, paramInt, this.mSessionStateReceiver);
    }
    catch (RemoteException localRemoteException)
    {
      str = TAG;
      paramIBinder = new StringBuilder();
      paramIBinder.append("Error starting session for ");
      paramIBinder.append(paramComponentName.flattenToShortString());
      paramIBinder.append(": ");
      paramIBinder.append(localRemoteException);
      Log.w(str, paramIBinder.toString());
    }
  }
  
  public void updateContentCaptureContext(ContentCaptureContext paramContentCaptureContext)
  {
    notifyContextUpdated(this.mId, paramContentCaptureContext);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/contentcapture/MainContentCaptureSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */