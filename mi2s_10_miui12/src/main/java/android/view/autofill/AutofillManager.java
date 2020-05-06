package android.view.autofill;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SystemApi;
import android.content.AutofillOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.metrics.LogMaker;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.service.autofill.FillEventHistory;
import android.service.autofill.UserData;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.DebugUtils;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Choreographer;
import android.view.KeyEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.AccessibilityPolicy;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.widget.TextView;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.os.IResultReceiver;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.SyncResultReceiver;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;
import sun.misc.Cleaner;

public final class AutofillManager
{
  public static final int ACTION_START_SESSION = 1;
  public static final int ACTION_VALUE_CHANGED = 4;
  public static final int ACTION_VIEW_ENTERED = 2;
  public static final int ACTION_VIEW_EXITED = 3;
  private static final int AUTHENTICATION_ID_DATASET_ID_MASK = 65535;
  private static final int AUTHENTICATION_ID_DATASET_ID_SHIFT = 16;
  public static final int AUTHENTICATION_ID_DATASET_ID_UNDEFINED = 65535;
  public static final int DEFAULT_LOGGING_LEVEL;
  public static final int DEFAULT_MAX_PARTITIONS_SIZE = 10;
  public static final String DEVICE_CONFIG_AUGMENTED_SERVICE_IDLE_UNBIND_TIMEOUT = "augmented_service_idle_unbind_timeout";
  public static final String DEVICE_CONFIG_AUGMENTED_SERVICE_REQUEST_TIMEOUT = "augmented_service_request_timeout";
  public static final String DEVICE_CONFIG_AUTOFILL_SMART_SUGGESTION_SUPPORTED_MODES = "smart_suggestion_supported_modes";
  public static final String EXTRA_ASSIST_STRUCTURE = "android.view.autofill.extra.ASSIST_STRUCTURE";
  public static final String EXTRA_AUGMENTED_AUTOFILL_CLIENT = "android.view.autofill.extra.AUGMENTED_AUTOFILL_CLIENT";
  public static final String EXTRA_AUTHENTICATION_RESULT = "android.view.autofill.extra.AUTHENTICATION_RESULT";
  public static final String EXTRA_CLIENT_STATE = "android.view.autofill.extra.CLIENT_STATE";
  public static final String EXTRA_RESTORE_SESSION_TOKEN = "android.view.autofill.extra.RESTORE_SESSION_TOKEN";
  public static final int FC_SERVICE_TIMEOUT = 5000;
  public static final int FLAG_ADD_CLIENT_DEBUG = 2;
  public static final int FLAG_ADD_CLIENT_ENABLED = 1;
  public static final int FLAG_ADD_CLIENT_ENABLED_FOR_AUGMENTED_AUTOFILL_ONLY = 8;
  public static final int FLAG_ADD_CLIENT_VERBOSE = 4;
  public static final int FLAG_SMART_SUGGESTION_OFF = 0;
  public static final int FLAG_SMART_SUGGESTION_SYSTEM = 1;
  private static final String LAST_AUTOFILLED_DATA_TAG = "android:lastAutoFilledData";
  public static final int MAX_TEMP_AUGMENTED_SERVICE_DURATION_MS = 120000;
  public static final int NO_LOGGING = 0;
  public static final int NO_SESSION = Integer.MAX_VALUE;
  public static final int PENDING_UI_OPERATION_CANCEL = 1;
  public static final int PENDING_UI_OPERATION_RESTORE = 2;
  public static final int RECEIVER_FLAG_SESSION_FOR_AUGMENTED_AUTOFILL_ONLY = 1;
  public static final int RESULT_CODE_NOT_SERVICE = -1;
  public static final int RESULT_OK = 0;
  private static final String SESSION_ID_TAG = "android:sessionId";
  public static final int SET_STATE_FLAG_DEBUG = 8;
  public static final int SET_STATE_FLAG_ENABLED = 1;
  public static final int SET_STATE_FLAG_FOR_AUTOFILL_ONLY = 32;
  public static final int SET_STATE_FLAG_RESET_CLIENT = 4;
  public static final int SET_STATE_FLAG_RESET_SESSION = 2;
  public static final int SET_STATE_FLAG_VERBOSE = 16;
  public static final int STATE_ACTIVE = 1;
  public static final int STATE_DISABLED_BY_SERVICE = 4;
  public static final int STATE_FINISHED = 2;
  public static final int STATE_SHOWING_SAVE_UI = 3;
  private static final String STATE_TAG = "android:state";
  public static final int STATE_UNKNOWN = 0;
  public static final int STATE_UNKNOWN_COMPAT_MODE = 5;
  public static final int STATE_UNKNOWN_FAILED = 6;
  private static final int SYNC_CALLS_TIMEOUT_MS = 5000;
  private static final String TAG = "AutofillManager";
  @GuardedBy({"mLock"})
  private IAugmentedAutofillManagerClient mAugmentedAutofillServiceClient;
  @GuardedBy({"mLock"})
  private AutofillCallback mCallback;
  @GuardedBy({"mLock"})
  private CompatibilityBridge mCompatibilityBridge;
  private final Context mContext;
  @GuardedBy({"mLock"})
  private boolean mEnabled;
  @GuardedBy({"mLock"})
  private boolean mEnabledForAugmentedAutofillOnly;
  @GuardedBy({"mLock"})
  private Set<AutofillId> mEnteredForAugmentedAutofillIds;
  @GuardedBy({"mLock"})
  private ArraySet<AutofillId> mEnteredIds;
  @GuardedBy({"mLock"})
  private ArraySet<AutofillId> mFillableIds;
  @GuardedBy({"mLock"})
  private boolean mForAugmentedAutofillOnly;
  private AutofillId mIdShownFillUi;
  @GuardedBy({"mLock"})
  private ParcelableMap mLastAutofilledData;
  private final Object mLock = new Object();
  private final MetricsLogger mMetricsLogger = new MetricsLogger();
  @GuardedBy({"mLock"})
  private boolean mOnInvisibleCalled;
  private final AutofillOptions mOptions;
  @GuardedBy({"mLock"})
  private boolean mSaveOnFinish;
  @GuardedBy({"mLock"})
  private AutofillId mSaveTriggerId;
  private final IAutoFillManager mService;
  @GuardedBy({"mLock"})
  private IAutoFillManagerClient mServiceClient;
  @GuardedBy({"mLock"})
  private Cleaner mServiceClientCleaner;
  @GuardedBy({"mLock"})
  private int mSessionId = Integer.MAX_VALUE;
  @GuardedBy({"mLock"})
  private int mState;
  @GuardedBy({"mLock"})
  private TrackedViews mTrackedViews;
  
  static
  {
    int i;
    if (Build.IS_DEBUGGABLE) {
      i = 2;
    } else {
      i = 0;
    }
    DEFAULT_LOGGING_LEVEL = i;
  }
  
  public AutofillManager(Context paramContext, IAutoFillManager paramIAutoFillManager)
  {
    boolean bool1 = false;
    this.mState = 0;
    this.mContext = ((Context)Preconditions.checkNotNull(paramContext, "context cannot be null"));
    this.mService = paramIAutoFillManager;
    this.mOptions = paramContext.getAutofillOptions();
    paramContext = this.mOptions;
    if (paramContext != null)
    {
      if ((paramContext.loggingLevel & 0x2) != 0) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      Helper.sDebug = bool2;
      boolean bool2 = bool1;
      if ((this.mOptions.loggingLevel & 0x4) != 0) {
        bool2 = true;
      }
      Helper.sVerbose = bool2;
    }
  }
  
  @GuardedBy({"mLock"})
  private void addEnteredIdLocked(AutofillId paramAutofillId)
  {
    if (this.mEnteredIds == null) {
      this.mEnteredIds = new ArraySet(1);
    }
    paramAutofillId.resetSessionId();
    this.mEnteredIds.add(paramAutofillId);
  }
  
  private void authenticate(int paramInt1, int paramInt2, IntentSender paramIntentSender, Intent paramIntent)
  {
    synchronized (this.mLock)
    {
      if (paramInt1 == this.mSessionId)
      {
        AutofillClient localAutofillClient = getClient();
        if (localAutofillClient != null)
        {
          this.mOnInvisibleCalled = false;
          localAutofillClient.autofillClientAuthenticate(paramInt2, paramIntentSender, paramIntent);
        }
      }
      return;
    }
  }
  
  private void autofill(int paramInt, List<AutofillId> paramList, List<AutofillValue> paramList1)
  {
    int i;
    Object localObject2;
    View[] arrayOfView;
    Object localObject3;
    int j;
    synchronized (this.mLock)
    {
      if (paramInt != this.mSessionId) {
        return;
      }
      AutofillClient localAutofillClient = getClient();
      if (localAutofillClient == null) {
        return;
      }
      i = paramList.size();
      localObject2 = null;
      arrayOfView = localAutofillClient.autofillClientFindViewsByAutofillIdTraversal(Helper.toArray(paramList));
      localObject3 = null;
      j = 0;
      paramInt = 0;
      if (j >= i) {}
    }
    try
    {
      localAutofillId = (AutofillId)paramList.get(j);
    }
    finally
    {
      try
      {
        AutofillId localAutofillId;
        AutofillValue localAutofillValue = (AutofillValue)paramList1.get(j);
        View localView = arrayOfView[j];
        Object localObject4;
        if (localView == null)
        {
          localObject4 = new java/lang/StringBuilder;
          ((StringBuilder)localObject4).<init>();
          ((StringBuilder)localObject4).append("autofill(): no View with id ");
          ((StringBuilder)localObject4).append(localAutofillId);
          Log.d("AutofillManager", ((StringBuilder)localObject4).toString());
          localObject4 = localObject3;
          if (localObject3 == null)
          {
            localObject4 = new java/util/ArrayList;
            ((ArrayList)localObject4).<init>();
          }
          ((ArrayList)localObject4).add(localAutofillId);
          localObject3 = localObject4;
        }
        else if (localAutofillId.isVirtualInt())
        {
          localObject4 = localObject2;
          if (localObject2 == null)
          {
            localObject4 = new android/util/ArrayMap;
            ((ArrayMap)localObject4).<init>(1);
          }
          SparseArray localSparseArray = (SparseArray)((ArrayMap)localObject4).get(localView);
          localObject2 = localSparseArray;
          if (localSparseArray == null)
          {
            localObject2 = new android/util/SparseArray;
            ((SparseArray)localObject2).<init>(5);
            ((ArrayMap)localObject4).put(localView, localObject2);
          }
          ((SparseArray)localObject2).put(localAutofillId.getVirtualChildIntId(), localAutofillValue);
          localObject2 = localObject4;
        }
        else
        {
          if (this.mLastAutofilledData == null)
          {
            localObject4 = new android/view/autofill/ParcelableMap;
            ((ParcelableMap)localObject4).<init>(i - j);
            this.mLastAutofilledData = ((ParcelableMap)localObject4);
          }
          this.mLastAutofilledData.put(localAutofillId, localAutofillValue);
          localView.autofill(localAutofillValue);
          setAutofilledIfValuesIs(localView, localAutofillValue);
          paramInt++;
        }
        j++;
      }
      finally
      {
        int k;
        label522:
        for (;;) {}
      }
      paramList = finally;
      break label522;
      if (localObject3 != null)
      {
        if (Helper.sVerbose)
        {
          paramList = new java/lang/StringBuilder;
          paramList.<init>();
          paramList.append("autofill(): total failed views: ");
          paramList.append(localObject3);
          Log.v("AutofillManager", paramList.toString());
        }
        try
        {
          this.mService.setAutofillFailure(this.mSessionId, (List)localObject3, this.mContext.getUserId());
        }
        catch (RemoteException paramList)
        {
          paramList.rethrowFromSystemServer();
        }
      }
      k = paramInt;
      if (localObject2 != null) {
        for (j = 0;; j++)
        {
          k = paramInt;
          if (j >= ((ArrayMap)localObject2).size()) {
            break;
          }
          paramList1 = (View)((ArrayMap)localObject2).keyAt(j);
          paramList = (SparseArray)((ArrayMap)localObject2).valueAt(j);
          paramList1.autofill(paramList);
          paramInt += paramList.size();
        }
      }
      this.mMetricsLogger.write(newLog(913).addTaggedData(914, Integer.valueOf(i)).addTaggedData(915, Integer.valueOf(k)));
      return;
      paramList = finally;
      throw paramList;
    }
  }
  
  @GuardedBy({"mLock"})
  private void cancelLocked()
  {
    if ((!this.mEnabled) && (!isActiveLocked())) {
      return;
    }
    cancelSessionLocked();
  }
  
  @GuardedBy({"mLock"})
  private void cancelSessionLocked()
  {
    if (Helper.sVerbose)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("cancelSessionLocked(): ");
      localStringBuilder.append(getStateAsStringLocked());
      Log.v("AutofillManager", localStringBuilder.toString());
    }
    if (!isActiveLocked()) {
      return;
    }
    try
    {
      this.mService.cancelSession(this.mSessionId, this.mContext.getUserId());
      resetSessionLocked(true);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @GuardedBy({"mLock"})
  private void commitLocked()
  {
    if ((!this.mEnabled) && (!isActiveLocked())) {
      return;
    }
    finishSessionLocked();
  }
  
  private void dispatchUnhandledKey(int paramInt, AutofillId arg2, KeyEvent paramKeyEvent)
  {
    View localView = findView(???);
    if (localView == null) {
      return;
    }
    synchronized (this.mLock)
    {
      if (this.mSessionId == paramInt)
      {
        AutofillClient localAutofillClient = getClient();
        if (localAutofillClient != null) {
          localAutofillClient.autofillClientDispatchUnhandledKey(localView, paramKeyEvent);
        }
      }
      return;
    }
  }
  
  @GuardedBy({"mLock"})
  private void ensureServiceClientAddedIfNeededLocked()
  {
    Object localObject1 = getClient();
    if (localObject1 == null) {
      return;
    }
    if (this.mServiceClient == null)
    {
      this.mServiceClient = new AutofillManagerClient(this, null);
      try
      {
        int i = this.mContext.getUserId();
        Object localObject2 = new com/android/internal/util/SyncResultReceiver;
        ((SyncResultReceiver)localObject2).<init>(5000);
        this.mService.addClient(this.mServiceClient, ((AutofillClient)localObject1).autofillClientGetComponentName(), i, (IResultReceiver)localObject2);
        int j = ((SyncResultReceiver)localObject2).getIntResult();
        boolean bool1 = false;
        if ((j & 0x1) != 0) {
          bool2 = true;
        } else {
          bool2 = false;
        }
        this.mEnabled = bool2;
        if ((j & 0x2) != 0) {
          bool2 = true;
        } else {
          bool2 = false;
        }
        Helper.sDebug = bool2;
        if ((j & 0x4) != 0) {
          bool2 = true;
        } else {
          bool2 = false;
        }
        Helper.sVerbose = bool2;
        boolean bool2 = bool1;
        if ((j & 0x8) != 0) {
          bool2 = true;
        }
        this.mEnabledForAugmentedAutofillOnly = bool2;
        if (Helper.sVerbose)
        {
          localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          ((StringBuilder)localObject2).append("receiver results: flags=");
          ((StringBuilder)localObject2).append(j);
          ((StringBuilder)localObject2).append(" enabled=");
          ((StringBuilder)localObject2).append(this.mEnabled);
          ((StringBuilder)localObject2).append(", enabledForAugmentedOnly: ");
          ((StringBuilder)localObject2).append(this.mEnabledForAugmentedAutofillOnly);
          Log.v("AutofillManager", ((StringBuilder)localObject2).toString());
        }
        localObject1 = this.mService;
        IAutoFillManagerClient localIAutoFillManagerClient = this.mServiceClient;
        localObject2 = new android/view/autofill/_$$Lambda$AutofillManager$V76JiQu509LCUz3_ckpb_nB3JhA;
        ((_..Lambda.AutofillManager.V76JiQu509LCUz3_ckpb_nB3JhA)localObject2).<init>((IAutoFillManager)localObject1, localIAutoFillManagerClient, i);
        this.mServiceClientCleaner = Cleaner.create(this, (Runnable)localObject2);
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
  }
  
  private View findView(AutofillId paramAutofillId)
  {
    AutofillClient localAutofillClient = getClient();
    if (localAutofillClient != null) {
      return localAutofillClient.autofillClientFindViewByAutofillIdTraversal(paramAutofillId);
    }
    return null;
  }
  
  @GuardedBy({"mLock"})
  private void finishSessionLocked()
  {
    if (Helper.sVerbose)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("finishSessionLocked(): ");
      localStringBuilder.append(getStateAsStringLocked());
      Log.v("AutofillManager", localStringBuilder.toString());
    }
    if (!isActiveLocked()) {
      return;
    }
    try
    {
      this.mService.finishSession(this.mSessionId, this.mContext.getUserId());
      resetSessionLocked(true);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  private void getAugmentedAutofillClient(IResultReceiver paramIResultReceiver)
  {
    synchronized (this.mLock)
    {
      if (this.mAugmentedAutofillServiceClient == null)
      {
        localObject2 = new android/view/autofill/AutofillManager$AugmentedAutofillManagerClient;
        ((AugmentedAutofillManagerClient)localObject2).<init>(this, null);
        this.mAugmentedAutofillServiceClient = ((IAugmentedAutofillManagerClient)localObject2);
      }
      Object localObject2 = new android/os/Bundle;
      ((Bundle)localObject2).<init>();
      ((Bundle)localObject2).putBinder("android.view.autofill.extra.AUGMENTED_AUTOFILL_CLIENT", this.mAugmentedAutofillServiceClient.asBinder());
      try
      {
        paramIResultReceiver.send(0, (Bundle)localObject2);
      }
      catch (RemoteException paramIResultReceiver)
      {
        localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        ((StringBuilder)localObject2).append("Could not send AugmentedAutofillClient back: ");
        ((StringBuilder)localObject2).append(paramIResultReceiver);
        Log.w("AutofillManager", ((StringBuilder)localObject2).toString());
      }
      return;
    }
  }
  
  private static AutofillId getAutofillId(View paramView, int paramInt)
  {
    return new AutofillId(paramView.getAutofillViewId(), paramInt);
  }
  
  private AutofillClient getClient()
  {
    AutofillClient localAutofillClient = this.mContext.getAutofillClient();
    if ((localAutofillClient == null) && (Helper.sVerbose))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("No AutofillClient for ");
      localStringBuilder.append(this.mContext.getPackageName());
      localStringBuilder.append(" on context ");
      localStringBuilder.append(this.mContext);
      Log.v("AutofillManager", localStringBuilder.toString());
    }
    return localAutofillClient;
  }
  
  public static int getDatasetIdFromAuthenticationId(int paramInt)
  {
    return 0xFFFF & paramInt;
  }
  
  public static int getRequestIdFromAuthenticationId(int paramInt)
  {
    return paramInt >> 16;
  }
  
  public static String getSmartSuggestionModeToString(int paramInt)
  {
    if (paramInt != 0)
    {
      if (paramInt != 1)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("INVALID:");
        localStringBuilder.append(paramInt);
        return localStringBuilder.toString();
      }
      return "SYSTEM";
    }
    return "OFF";
  }
  
  private static String getStateAsString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("INVALID:");
      localStringBuilder.append(paramInt);
      return localStringBuilder.toString();
    case 6: 
      return "UNKNOWN_FAILED";
    case 5: 
      return "UNKNOWN_COMPAT_MODE";
    case 4: 
      return "DISABLED_BY_SERVICE";
    case 3: 
      return "SHOWING_SAVE_UI";
    case 2: 
      return "FINISHED";
    case 1: 
      return "ACTIVE";
    }
    return "UNKNOWN";
  }
  
  @GuardedBy({"mLock"})
  private String getStateAsStringLocked()
  {
    return getStateAsString(this.mState);
  }
  
  @GuardedBy({"mLock"})
  private boolean isActiveLocked()
  {
    int i = this.mState;
    boolean bool = true;
    if (i != 1) {
      bool = false;
    }
    return bool;
  }
  
  private boolean isClientDisablingEnterExitEvent()
  {
    AutofillClient localAutofillClient = getClient();
    boolean bool;
    if ((localAutofillClient != null) && (localAutofillClient.isDisablingEnterExitEventForAutofill())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean isClientVisibleForAutofillLocked()
  {
    AutofillClient localAutofillClient = getClient();
    boolean bool;
    if ((localAutofillClient != null) && (localAutofillClient.autofillClientIsVisibleForAutofill())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @GuardedBy({"mLock"})
  private boolean isDisabledByServiceLocked()
  {
    boolean bool;
    if (this.mState == 4) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @GuardedBy({"mLock"})
  private boolean isFinishedLocked()
  {
    boolean bool;
    if (this.mState == 2) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static int makeAuthenticationId(int paramInt1, int paramInt2)
  {
    return paramInt1 << 16 | 0xFFFF & paramInt2;
  }
  
  private LogMaker newLog(int paramInt)
  {
    LogMaker localLogMaker = new LogMaker(paramInt).addTaggedData(1456, Integer.valueOf(this.mSessionId));
    if (isCompatibilityModeEnabledLocked()) {
      localLogMaker.addTaggedData(1414, Integer.valueOf(1));
    }
    AutofillClient localAutofillClient = getClient();
    if (localAutofillClient == null) {
      localLogMaker.setPackageName(this.mContext.getPackageName());
    } else {
      localLogMaker.setComponentName(localAutofillClient.autofillClientGetComponentName());
    }
    return localLogMaker;
  }
  
  private void notifyNoFillUi(int paramInt1, AutofillId paramAutofillId, int paramInt2)
  {
    if (Helper.sVerbose)
    {
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("notifyNoFillUi(): sessionId=");
      ((StringBuilder)localObject1).append(paramInt1);
      ((StringBuilder)localObject1).append(", autofillId=");
      ((StringBuilder)localObject1).append(paramAutofillId);
      ((StringBuilder)localObject1).append(", sessionFinishedState=");
      ((StringBuilder)localObject1).append(paramInt2);
      Log.v("AutofillManager", ((StringBuilder)localObject1).toString());
    }
    View localView = findView(paramAutofillId);
    if (localView == null) {
      return;
    }
    Object localObject2 = null;
    Object localObject3 = this.mLock;
    Object localObject1 = localObject2;
    try
    {
      if (this.mSessionId == paramInt1)
      {
        localObject1 = localObject2;
        if (getClient() != null) {
          localObject1 = this.mCallback;
        }
      }
      if (localObject1 != null) {
        if (paramAutofillId.isVirtualInt()) {
          ((AutofillCallback)localObject1).onAutofillEvent(localView, paramAutofillId.getVirtualChildIntId(), 3);
        } else {
          ((AutofillCallback)localObject1).onAutofillEvent(localView, 3);
        }
      }
      if (paramInt2 != 0) {
        setSessionFinished(paramInt2, null);
      }
      return;
    }
    finally {}
  }
  
  private void notifyViewClicked(AutofillId paramAutofillId)
  {
    if (!hasAutofillFeature()) {
      return;
    }
    if (Helper.sVerbose)
    {
      ??? = new StringBuilder();
      ((StringBuilder)???).append("notifyViewClicked(): id=");
      ((StringBuilder)???).append(paramAutofillId);
      ((StringBuilder)???).append(", trigger=");
      ((StringBuilder)???).append(this.mSaveTriggerId);
      Log.v("AutofillManager", ((StringBuilder)???).toString());
    }
    synchronized (this.mLock)
    {
      if ((this.mEnabled) && (isActiveLocked()))
      {
        if ((this.mSaveTriggerId != null) && (this.mSaveTriggerId.equals(paramAutofillId)))
        {
          if (Helper.sDebug)
          {
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            localStringBuilder.append("triggering commit by click of ");
            localStringBuilder.append(paramAutofillId);
            Log.d("AutofillManager", localStringBuilder.toString());
          }
          commitLocked();
          this.mMetricsLogger.write(newLog(1229));
        }
        return;
      }
      return;
    }
  }
  
  private void notifyViewEntered(View paramView, int paramInt)
  {
    if (!hasAutofillFeature()) {
      return;
    }
    synchronized (this.mLock)
    {
      AutofillCallback localAutofillCallback = notifyViewEnteredLocked(paramView, paramInt);
      if (localAutofillCallback != null) {
        this.mCallback.onAutofillEvent(paramView, 3);
      }
      return;
    }
  }
  
  private void notifyViewEntered(View paramView, int paramInt1, Rect paramRect, int paramInt2)
  {
    if (!hasAutofillFeature()) {
      return;
    }
    synchronized (this.mLock)
    {
      paramRect = notifyViewEnteredLocked(paramView, paramInt1, paramRect, paramInt2);
      if (paramRect != null) {
        paramRect.onAutofillEvent(paramView, paramInt1, 3);
      }
      return;
    }
  }
  
  @GuardedBy({"mLock"})
  private AutofillCallback notifyViewEnteredLocked(View paramView, int paramInt)
  {
    AutofillId localAutofillId = paramView.getAutofillId();
    if (shouldIgnoreViewEnteredLocked(localAutofillId, paramInt)) {
      return null;
    }
    Object localObject1 = null;
    ensureServiceClientAddedIfNeededLocked();
    Object localObject2;
    if ((!this.mEnabled) && (!this.mEnabledForAugmentedAutofillOnly))
    {
      if (Helper.sVerbose)
      {
        paramView = new StringBuilder();
        paramView.append("ignoring notifyViewEntered(");
        paramView.append(localAutofillId);
        paramView.append("): disabled");
        Log.v("AutofillManager", paramView.toString());
      }
      localObject2 = localObject1;
      if (this.mCallback != null) {
        localObject2 = this.mCallback;
      }
    }
    else
    {
      localObject2 = localObject1;
      if (!isClientDisablingEnterExitEvent())
      {
        localObject2 = paramView.getAutofillValue();
        int i = paramInt;
        if ((paramView instanceof TextView))
        {
          i = paramInt;
          if (((TextView)paramView).isAnyPasswordInputType()) {
            i = paramInt | 0x4;
          }
        }
        if (!isActiveLocked())
        {
          startSessionLocked(localAutofillId, null, (AutofillValue)localObject2, i);
        }
        else
        {
          if ((this.mForAugmentedAutofillOnly) && ((i & 0x1) != 0))
          {
            if (Helper.sDebug)
            {
              paramView = new StringBuilder();
              paramView.append("notifyViewEntered(");
              paramView.append(localAutofillId);
              paramView.append("): resetting mForAugmentedAutofillOnly on manual request");
              Log.d("AutofillManager", paramView.toString());
            }
            this.mForAugmentedAutofillOnly = false;
          }
          updateSessionLocked(localAutofillId, null, (AutofillValue)localObject2, 2, i);
        }
        addEnteredIdLocked(localAutofillId);
        localObject2 = localObject1;
      }
    }
    return (AutofillCallback)localObject2;
  }
  
  @GuardedBy({"mLock"})
  private AutofillCallback notifyViewEnteredLocked(View paramView, int paramInt1, Rect paramRect, int paramInt2)
  {
    AutofillId localAutofillId = getAutofillId(paramView, paramInt1);
    Object localObject1 = null;
    if (shouldIgnoreViewEnteredLocked(localAutofillId, paramInt2)) {
      return null;
    }
    ensureServiceClientAddedIfNeededLocked();
    Object localObject2;
    if ((!this.mEnabled) && (!this.mEnabledForAugmentedAutofillOnly))
    {
      if (Helper.sVerbose)
      {
        paramView = new StringBuilder();
        paramView.append("ignoring notifyViewEntered(");
        paramView.append(localAutofillId);
        paramView.append("): disabled");
        Log.v("AutofillManager", paramView.toString());
      }
      localObject2 = localObject1;
      if (this.mCallback != null) {
        localObject2 = this.mCallback;
      }
    }
    else
    {
      localObject2 = localObject1;
      if (!isClientDisablingEnterExitEvent())
      {
        paramInt1 = paramInt2;
        if ((paramView instanceof TextView))
        {
          paramInt1 = paramInt2;
          if (((TextView)paramView).isAnyPasswordInputType()) {
            paramInt1 = paramInt2 | 0x4;
          }
        }
        if (!isActiveLocked())
        {
          startSessionLocked(localAutofillId, paramRect, null, paramInt1);
        }
        else
        {
          if ((this.mForAugmentedAutofillOnly) && ((paramInt1 & 0x1) != 0))
          {
            if (Helper.sDebug)
            {
              paramView = new StringBuilder();
              paramView.append("notifyViewEntered(");
              paramView.append(localAutofillId);
              paramView.append("): resetting mForAugmentedAutofillOnly on manual request");
              Log.d("AutofillManager", paramView.toString());
            }
            this.mForAugmentedAutofillOnly = false;
          }
          updateSessionLocked(localAutofillId, paramRect, null, 2, paramInt1);
        }
        addEnteredIdLocked(localAutofillId);
        localObject2 = localObject1;
      }
    }
    return (AutofillCallback)localObject2;
  }
  
  @GuardedBy({"mLock"})
  private void notifyViewExitedLocked(View paramView, int paramInt)
  {
    ensureServiceClientAddedIfNeededLocked();
    if (((this.mEnabled) || (this.mEnabledForAugmentedAutofillOnly)) && (isActiveLocked()) && (!isClientDisablingEnterExitEvent())) {
      updateSessionLocked(getAutofillId(paramView, paramInt), null, null, 3, 0);
    }
  }
  
  private void notifyViewVisibilityChangedInternal(View paramView, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    synchronized (this.mLock)
    {
      if (this.mForAugmentedAutofillOnly)
      {
        if (Helper.sVerbose) {
          Log.v("AutofillManager", "notifyViewVisibilityChanged(): ignoring on augmented only mode");
        }
        return;
      }
      if ((this.mEnabled) && (isActiveLocked()))
      {
        AutofillId localAutofillId;
        if (paramBoolean2) {
          localAutofillId = getAutofillId(paramView, paramInt);
        } else {
          localAutofillId = paramView.getAutofillId();
        }
        StringBuilder localStringBuilder;
        if (Helper.sVerbose)
        {
          localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localStringBuilder.append("visibility changed for ");
          localStringBuilder.append(localAutofillId);
          localStringBuilder.append(": ");
          localStringBuilder.append(paramBoolean1);
          Log.v("AutofillManager", localStringBuilder.toString());
        }
        if ((!paramBoolean1) && (this.mFillableIds != null) && (this.mFillableIds.contains(localAutofillId)))
        {
          if (Helper.sDebug)
          {
            localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            localStringBuilder.append("Hidding UI when view ");
            localStringBuilder.append(localAutofillId);
            localStringBuilder.append(" became invisible");
            Log.d("AutofillManager", localStringBuilder.toString());
          }
          requestHideFillUi(localAutofillId, paramView);
        }
        if (this.mTrackedViews != null)
        {
          this.mTrackedViews.notifyViewVisibilityChangedLocked(localAutofillId, paramBoolean1);
        }
        else if (Helper.sVerbose)
        {
          paramView = new java/lang/StringBuilder;
          paramView.<init>();
          paramView.append("Ignoring visibility change on ");
          paramView.append(localAutofillId);
          paramView.append(": no tracked views");
          Log.v("AutofillManager", paramView.toString());
        }
      }
      return;
    }
  }
  
  private void post(Runnable paramRunnable)
  {
    AutofillClient localAutofillClient = getClient();
    if (localAutofillClient == null)
    {
      if (Helper.sVerbose) {
        Log.v("AutofillManager", "ignoring post() because client is null");
      }
      return;
    }
    localAutofillClient.autofillClientRunOnUiThread(paramRunnable);
  }
  
  private void requestHideFillUi(AutofillId paramAutofillId, View paramView)
  {
    Object localObject1 = null;
    synchronized (this.mLock)
    {
      AutofillClient localAutofillClient = getClient();
      Object localObject3 = localObject1;
      if (localAutofillClient != null)
      {
        localObject3 = localObject1;
        if (localAutofillClient.autofillClientRequestHideFillUi())
        {
          this.mIdShownFillUi = null;
          localObject3 = this.mCallback;
        }
      }
      if (localObject3 != null) {
        if (paramAutofillId.isVirtualInt()) {
          ((AutofillCallback)localObject3).onAutofillEvent(paramView, paramAutofillId.getVirtualChildIntId(), 2);
        } else {
          ((AutofillCallback)localObject3).onAutofillEvent(paramView, 2);
        }
      }
      return;
    }
  }
  
  private void requestHideFillUi(AutofillId paramAutofillId, boolean paramBoolean)
  {
    View localView;
    if (paramAutofillId == null) {
      localView = null;
    } else {
      localView = findView(paramAutofillId);
    }
    if (Helper.sVerbose)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("requestHideFillUi(");
      localStringBuilder.append(paramAutofillId);
      localStringBuilder.append("): anchor = ");
      localStringBuilder.append(localView);
      Log.v("AutofillManager", localStringBuilder.toString());
    }
    if (localView == null)
    {
      if (paramBoolean)
      {
        paramAutofillId = getClient();
        if (paramAutofillId != null) {
          paramAutofillId.autofillClientRequestHideFillUi();
        }
      }
      return;
    }
    requestHideFillUi(paramAutofillId, localView);
  }
  
  private void requestShowFillUi(int paramInt1, AutofillId paramAutofillId, int paramInt2, int paramInt3, Rect paramRect, IAutofillWindowPresenter paramIAutofillWindowPresenter)
  {
    View localView = findView(paramAutofillId);
    if (localView == null) {
      return;
    }
    Object localObject1 = null;
    Object localObject3;
    synchronized (this.mLock)
    {
      int i = this.mSessionId;
      localObject3 = localObject1;
      if (i != paramInt1) {}
    }
    try
    {
      AutofillClient localAutofillClient = getClient();
      localObject3 = localObject1;
      if (localAutofillClient != null)
      {
        localObject3 = localObject1;
        if (localAutofillClient.autofillClientRequestShowFillUi(localView, paramInt2, paramInt3, paramRect, paramIAutofillWindowPresenter))
        {
          localObject3 = this.mCallback;
          this.mIdShownFillUi = paramAutofillId;
        }
      }
      if (localObject3 != null) {
        if (paramAutofillId.isVirtualInt()) {
          ((AutofillCallback)localObject3).onAutofillEvent(localView, paramAutofillId.getVirtualChildIntId(), 1);
        } else {
          ((AutofillCallback)localObject3).onAutofillEvent(localView, 1);
        }
      }
      return;
    }
    finally
    {
      for (;;) {}
    }
    paramAutofillId = finally;
    throw paramAutofillId;
  }
  
  @GuardedBy({"mLock"})
  private void resetSessionLocked(boolean paramBoolean)
  {
    this.mSessionId = Integer.MAX_VALUE;
    this.mState = 0;
    this.mTrackedViews = null;
    this.mFillableIds = null;
    this.mSaveTriggerId = null;
    this.mIdShownFillUi = null;
    if (paramBoolean) {
      this.mEnteredIds = null;
    }
  }
  
  private void setAutofilledIfValuesIs(View paramView, AutofillValue paramAutofillValue)
  {
    if (Objects.equals(paramView.getAutofillValue(), paramAutofillValue)) {
      synchronized (this.mLock)
      {
        if (this.mLastAutofilledData == null)
        {
          ParcelableMap localParcelableMap = new android/view/autofill/ParcelableMap;
          localParcelableMap.<init>(1);
          this.mLastAutofilledData = localParcelableMap;
        }
        this.mLastAutofilledData.put(paramView.getAutofillId(), paramAutofillValue);
        paramView.setAutofilled(true);
      }
    }
  }
  
  private void setNotifyOnClickLocked(AutofillId paramAutofillId, boolean paramBoolean)
  {
    Object localObject = findView(paramAutofillId);
    if (localObject == null)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("setNotifyOnClick(): invalid id: ");
      ((StringBuilder)localObject).append(paramAutofillId);
      Log.w("AutofillManager", ((StringBuilder)localObject).toString());
      return;
    }
    ((View)localObject).setNotifyAutofillManagerOnClick(paramBoolean);
  }
  
  private void setSaveUiState(int paramInt, boolean paramBoolean)
  {
    if (Helper.sDebug)
    {
      ??? = new StringBuilder();
      ((StringBuilder)???).append("setSaveUiState(");
      ((StringBuilder)???).append(paramInt);
      ((StringBuilder)???).append("): ");
      ((StringBuilder)???).append(paramBoolean);
      Log.d("AutofillManager", ((StringBuilder)???).toString());
    }
    synchronized (this.mLock)
    {
      if (this.mSessionId != Integer.MAX_VALUE)
      {
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>();
        localStringBuilder.append("setSaveUiState(");
        localStringBuilder.append(paramInt);
        localStringBuilder.append(", ");
        localStringBuilder.append(paramBoolean);
        localStringBuilder.append(") called on existing session ");
        localStringBuilder.append(this.mSessionId);
        localStringBuilder.append("; cancelling it");
        Log.w("AutofillManager", localStringBuilder.toString());
        cancelSessionLocked();
      }
      if (paramBoolean)
      {
        this.mSessionId = paramInt;
        this.mState = 3;
      }
      else
      {
        this.mSessionId = Integer.MAX_VALUE;
        this.mState = 0;
      }
      return;
    }
  }
  
  private void setSessionFinished(int paramInt, List<AutofillId> paramList)
  {
    if (paramList != null) {
      for (int i = 0; i < paramList.size(); i++) {
        ((AutofillId)paramList.get(i)).resetSessionId();
      }
    }
    synchronized (this.mLock)
    {
      Object localObject2;
      if (Helper.sVerbose)
      {
        localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        ((StringBuilder)localObject2).append("setSessionFinished(): from ");
        ((StringBuilder)localObject2).append(getStateAsStringLocked());
        ((StringBuilder)localObject2).append(" to ");
        ((StringBuilder)localObject2).append(getStateAsString(paramInt));
        ((StringBuilder)localObject2).append("; autofillableIds=");
        ((StringBuilder)localObject2).append(paramList);
        Log.v("AutofillManager", ((StringBuilder)localObject2).toString());
      }
      if (paramList != null)
      {
        localObject2 = new android/util/ArraySet;
        ((ArraySet)localObject2).<init>(paramList);
        this.mEnteredIds = ((ArraySet)localObject2);
      }
      if ((paramInt != 5) && (paramInt != 6))
      {
        resetSessionLocked(false);
        this.mState = paramInt;
      }
      else
      {
        resetSessionLocked(true);
        this.mState = 0;
      }
      return;
    }
  }
  
  private void setState(int paramInt)
  {
    if (Helper.sVerbose)
    {
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("setState(");
      ((StringBuilder)localObject1).append(paramInt);
      ((StringBuilder)localObject1).append(": ");
      ((StringBuilder)localObject1).append(DebugUtils.flagsToString(AutofillManager.class, "SET_STATE_FLAG_", paramInt));
      ((StringBuilder)localObject1).append(")");
      Log.v("AutofillManager", ((StringBuilder)localObject1).toString());
    }
    Object localObject1 = this.mLock;
    boolean bool1 = true;
    if ((paramInt & 0x20) != 0) {
      try
      {
        this.mForAugmentedAutofillOnly = true;
        return;
      }
      finally
      {
        break label216;
      }
    }
    boolean bool2;
    if ((paramInt & 0x1) != 0) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mEnabled = bool2;
    if ((!this.mEnabled) || ((paramInt & 0x2) != 0)) {
      resetSessionLocked(true);
    }
    if ((paramInt & 0x4) != 0)
    {
      this.mServiceClient = null;
      this.mAugmentedAutofillServiceClient = null;
      if (this.mServiceClientCleaner != null)
      {
        this.mServiceClientCleaner.clean();
        this.mServiceClientCleaner = null;
      }
    }
    if ((paramInt & 0x8) != 0) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    Helper.sDebug = bool2;
    if ((paramInt & 0x10) != 0) {
      bool2 = bool1;
    } else {
      bool2 = false;
    }
    Helper.sVerbose = bool2;
    return;
    label216:
    throw ((Throwable)localObject2);
  }
  
  private void setTrackedViews(int paramInt, AutofillId[] paramArrayOfAutofillId1, boolean paramBoolean1, boolean paramBoolean2, AutofillId[] paramArrayOfAutofillId2, AutofillId paramAutofillId)
  {
    if (paramAutofillId != null) {
      paramAutofillId.resetSessionId();
    }
    synchronized (this.mLock)
    {
      Object localObject2;
      if (Helper.sVerbose)
      {
        localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        ((StringBuilder)localObject2).append("setTrackedViews(): sessionId=");
        ((StringBuilder)localObject2).append(paramInt);
        ((StringBuilder)localObject2).append(", trackedIds=");
        ((StringBuilder)localObject2).append(Arrays.toString(paramArrayOfAutofillId1));
        ((StringBuilder)localObject2).append(", saveOnAllViewsInvisible=");
        ((StringBuilder)localObject2).append(paramBoolean1);
        ((StringBuilder)localObject2).append(", saveOnFinish=");
        ((StringBuilder)localObject2).append(paramBoolean2);
        ((StringBuilder)localObject2).append(", fillableIds=");
        ((StringBuilder)localObject2).append(Arrays.toString(paramArrayOfAutofillId2));
        ((StringBuilder)localObject2).append(", saveTrigerId=");
        ((StringBuilder)localObject2).append(paramAutofillId);
        ((StringBuilder)localObject2).append(", mFillableIds=");
        ((StringBuilder)localObject2).append(this.mFillableIds);
        ((StringBuilder)localObject2).append(", mEnabled=");
        ((StringBuilder)localObject2).append(this.mEnabled);
        ((StringBuilder)localObject2).append(", mSessionId=");
        ((StringBuilder)localObject2).append(this.mSessionId);
        Log.v("AutofillManager", ((StringBuilder)localObject2).toString());
      }
      if ((this.mEnabled) && (this.mSessionId == paramInt))
      {
        if (paramBoolean1)
        {
          localObject2 = new android/view/autofill/AutofillManager$TrackedViews;
          ((TrackedViews)localObject2).<init>(this, paramArrayOfAutofillId1);
          this.mTrackedViews = ((TrackedViews)localObject2);
        }
        else
        {
          this.mTrackedViews = null;
        }
        this.mSaveOnFinish = paramBoolean2;
        if (paramArrayOfAutofillId2 != null)
        {
          if (this.mFillableIds == null)
          {
            paramArrayOfAutofillId1 = new android/util/ArraySet;
            paramArrayOfAutofillId1.<init>(paramArrayOfAutofillId2.length);
            this.mFillableIds = paramArrayOfAutofillId1;
          }
          int i = paramArrayOfAutofillId2.length;
          for (paramInt = 0; paramInt < i; paramInt++)
          {
            paramArrayOfAutofillId1 = paramArrayOfAutofillId2[paramInt];
            paramArrayOfAutofillId1.resetSessionId();
            this.mFillableIds.add(paramArrayOfAutofillId1);
          }
        }
        if ((this.mSaveTriggerId != null) && (!this.mSaveTriggerId.equals(paramAutofillId))) {
          setNotifyOnClickLocked(this.mSaveTriggerId, false);
        }
        if ((paramAutofillId != null) && (!paramAutofillId.equals(this.mSaveTriggerId)))
        {
          this.mSaveTriggerId = paramAutofillId;
          setNotifyOnClickLocked(this.mSaveTriggerId, true);
        }
      }
      return;
    }
  }
  
  @GuardedBy({"mLock"})
  private boolean shouldIgnoreViewEnteredLocked(AutofillId paramAutofillId, int paramInt)
  {
    Object localObject;
    if (isDisabledByServiceLocked())
    {
      if (Helper.sVerbose)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("ignoring notifyViewEntered(flags=");
        ((StringBuilder)localObject).append(paramInt);
        ((StringBuilder)localObject).append(", view=");
        ((StringBuilder)localObject).append(paramAutofillId);
        ((StringBuilder)localObject).append(") on state ");
        ((StringBuilder)localObject).append(getStateAsStringLocked());
        ((StringBuilder)localObject).append(" because disabled by svc");
        Log.v("AutofillManager", ((StringBuilder)localObject).toString());
      }
      return true;
    }
    if ((isFinishedLocked()) && ((paramInt & 0x1) == 0))
    {
      localObject = this.mEnteredIds;
      if ((localObject != null) && (((ArraySet)localObject).contains(paramAutofillId)))
      {
        if (Helper.sVerbose)
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("ignoring notifyViewEntered(flags=");
          ((StringBuilder)localObject).append(paramInt);
          ((StringBuilder)localObject).append(", view=");
          ((StringBuilder)localObject).append(paramAutofillId);
          ((StringBuilder)localObject).append(") on state ");
          ((StringBuilder)localObject).append(getStateAsStringLocked());
          ((StringBuilder)localObject).append(" because view was already entered: ");
          ((StringBuilder)localObject).append(this.mEnteredIds);
          Log.v("AutofillManager", ((StringBuilder)localObject).toString());
        }
        return true;
      }
    }
    return false;
  }
  
  @GuardedBy({"mLock"})
  private void startSessionLocked(AutofillId paramAutofillId, Rect paramRect, AutofillValue paramAutofillValue, int paramInt)
  {
    Object localObject = this.mEnteredForAugmentedAutofillIds;
    if (((localObject != null) && (((Set)localObject).contains(paramAutofillId))) || (this.mEnabledForAugmentedAutofillOnly))
    {
      if (Helper.sVerbose)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Starting session for augmented autofill on ");
        ((StringBuilder)localObject).append(paramAutofillId);
        Log.v("AutofillManager", ((StringBuilder)localObject).toString());
      }
      paramInt |= 0x8;
    }
    if (Helper.sVerbose)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("startSessionLocked(): id=");
      ((StringBuilder)localObject).append(paramAutofillId);
      ((StringBuilder)localObject).append(", bounds=");
      ((StringBuilder)localObject).append(paramRect);
      ((StringBuilder)localObject).append(", value=");
      ((StringBuilder)localObject).append(paramAutofillValue);
      ((StringBuilder)localObject).append(", flags=");
      ((StringBuilder)localObject).append(paramInt);
      ((StringBuilder)localObject).append(", state=");
      ((StringBuilder)localObject).append(getStateAsStringLocked());
      ((StringBuilder)localObject).append(", compatMode=");
      ((StringBuilder)localObject).append(isCompatibilityModeEnabledLocked());
      ((StringBuilder)localObject).append(", augmentedOnly=");
      ((StringBuilder)localObject).append(this.mForAugmentedAutofillOnly);
      ((StringBuilder)localObject).append(", enabledAugmentedOnly=");
      ((StringBuilder)localObject).append(this.mEnabledForAugmentedAutofillOnly);
      ((StringBuilder)localObject).append(", enteredIds=");
      ((StringBuilder)localObject).append(this.mEnteredIds);
      Log.v("AutofillManager", ((StringBuilder)localObject).toString());
    }
    if ((this.mForAugmentedAutofillOnly) && (!this.mEnabledForAugmentedAutofillOnly) && ((paramInt & 0x1) != 0))
    {
      if (Helper.sVerbose) {
        Log.v("AutofillManager", "resetting mForAugmentedAutofillOnly on manual autofill request");
      }
      this.mForAugmentedAutofillOnly = false;
    }
    if ((this.mState != 0) && (!isFinishedLocked()) && ((paramInt & 0x1) == 0))
    {
      if (Helper.sVerbose)
      {
        paramRect = new StringBuilder();
        paramRect.append("not automatically starting session for ");
        paramRect.append(paramAutofillId);
        paramRect.append(" on state ");
        paramRect.append(getStateAsStringLocked());
        paramRect.append(" and flags ");
        paramRect.append(paramInt);
        Log.v("AutofillManager", paramRect.toString());
      }
      return;
    }
    try
    {
      AutofillClient localAutofillClient = getClient();
      if (localAutofillClient == null) {
        return;
      }
      SyncResultReceiver localSyncResultReceiver = new com/android/internal/util/SyncResultReceiver;
      localSyncResultReceiver.<init>(5000);
      localObject = localAutofillClient.autofillClientGetComponentName();
      IAutoFillManager localIAutoFillManager = this.mService;
      IBinder localIBinder1 = localAutofillClient.autofillClientGetActivityToken();
      IBinder localIBinder2 = this.mServiceClient.asBinder();
      int i = this.mContext.getUserId();
      boolean bool1;
      if (this.mCallback != null) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      boolean bool2 = isCompatibilityModeEnabledLocked();
      try
      {
        localIAutoFillManager.startSession(localIBinder1, localIBinder2, paramAutofillId, paramRect, paramAutofillValue, i, bool1, paramInt, (ComponentName)localObject, bool2, localSyncResultReceiver);
        this.mSessionId = localSyncResultReceiver.getIntResult();
        if (this.mSessionId != Integer.MAX_VALUE) {
          this.mState = 1;
        }
        if ((localSyncResultReceiver.getOptionalExtraIntResult(0) & 0x1) != 0)
        {
          if (Helper.sDebug)
          {
            paramAutofillId = new java/lang/StringBuilder;
            paramAutofillId.<init>();
            paramAutofillId.append("startSession(");
            paramAutofillId.append(localObject);
            paramAutofillId.append("): for augmented only");
            Log.d("AutofillManager", paramAutofillId.toString());
          }
          this.mForAugmentedAutofillOnly = true;
        }
        localAutofillClient.autofillClientResetableStateAvailable();
        return;
      }
      catch (RemoteException paramAutofillId) {}
      throw paramAutofillId.rethrowFromSystemServer();
    }
    catch (RemoteException paramAutofillId) {}
  }
  
  @GuardedBy({"mLock"})
  private void updateSessionLocked(AutofillId paramAutofillId, Rect paramRect, AutofillValue paramAutofillValue, int paramInt1, int paramInt2)
  {
    if (Helper.sVerbose)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("updateSessionLocked(): id=");
      localStringBuilder.append(paramAutofillId);
      localStringBuilder.append(", bounds=");
      localStringBuilder.append(paramRect);
      localStringBuilder.append(", value=");
      localStringBuilder.append(paramAutofillValue);
      localStringBuilder.append(", action=");
      localStringBuilder.append(paramInt1);
      localStringBuilder.append(", flags=");
      localStringBuilder.append(paramInt2);
      Log.v("AutofillManager", localStringBuilder.toString());
    }
    try
    {
      this.mService.updateSession(this.mSessionId, paramAutofillId, paramRect, paramAutofillValue, paramInt1, paramInt2, this.mContext.getUserId());
      return;
    }
    catch (RemoteException paramAutofillId)
    {
      throw paramAutofillId.rethrowFromSystemServer();
    }
  }
  
  public void cancel()
  {
    if (Helper.sVerbose) {
      Log.v("AutofillManager", "cancel() called by app");
    }
    if (!hasAutofillFeature()) {
      return;
    }
    synchronized (this.mLock)
    {
      cancelLocked();
      return;
    }
  }
  
  public void commit()
  {
    if (!hasAutofillFeature()) {
      return;
    }
    if (Helper.sVerbose) {
      Log.v("AutofillManager", "commit() called by app");
    }
    synchronized (this.mLock)
    {
      commitLocked();
      return;
    }
  }
  
  public void disableAutofillServices()
  {
    if (!hasAutofillFeature()) {
      return;
    }
    try
    {
      this.mService.disableOwnedAutofillServices(this.mContext.getUserId());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void disableOwnedAutofillServices()
  {
    disableAutofillServices();
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("AutofillManager:");
    ??? = new StringBuilder();
    ((StringBuilder)???).append(paramString);
    ((StringBuilder)???).append("  ");
    paramString = ((StringBuilder)???).toString();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("sessionId: ");
    paramPrintWriter.println(this.mSessionId);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("state: ");
    paramPrintWriter.println(getStateAsStringLocked());
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("context: ");
    paramPrintWriter.println(this.mContext);
    ??? = getClient();
    if (??? != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("client: ");
      paramPrintWriter.print(???);
      paramPrintWriter.print(" (");
      paramPrintWriter.print(((AutofillClient)???).autofillClientGetActivityToken());
      paramPrintWriter.println(')');
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("enabled: ");
    paramPrintWriter.println(this.mEnabled);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("enabledAugmentedOnly: ");
    paramPrintWriter.println(this.mForAugmentedAutofillOnly);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("hasService: ");
    ??? = this.mService;
    boolean bool1 = true;
    boolean bool2;
    if (??? != null) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    paramPrintWriter.println(bool2);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("hasCallback: ");
    if (this.mCallback != null) {
      bool2 = bool1;
    } else {
      bool2 = false;
    }
    paramPrintWriter.println(bool2);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("onInvisibleCalled ");
    paramPrintWriter.println(this.mOnInvisibleCalled);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("last autofilled data: ");
    paramPrintWriter.println(this.mLastAutofilledData);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("id of last fill UI shown: ");
    paramPrintWriter.println(this.mIdShownFillUi);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("tracked views: ");
    if (this.mTrackedViews == null)
    {
      paramPrintWriter.println("null");
    }
    else
    {
      ??? = new StringBuilder();
      ((StringBuilder)???).append(paramString);
      ((StringBuilder)???).append("  ");
      ??? = ((StringBuilder)???).toString();
      paramPrintWriter.println();
      paramPrintWriter.print((String)???);
      paramPrintWriter.print("visible:");
      paramPrintWriter.println(this.mTrackedViews.mVisibleTrackedIds);
      paramPrintWriter.print((String)???);
      paramPrintWriter.print("invisible:");
      paramPrintWriter.println(this.mTrackedViews.mInvisibleTrackedIds);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("fillable ids: ");
    paramPrintWriter.println(this.mFillableIds);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("entered ids: ");
    paramPrintWriter.println(this.mEnteredIds);
    if (this.mEnteredForAugmentedAutofillIds != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("entered ids for augmented autofill: ");
      paramPrintWriter.println(this.mEnteredForAugmentedAutofillIds);
    }
    if (this.mForAugmentedAutofillOnly)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("For Augmented Autofill Only");
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("save trigger id: ");
    paramPrintWriter.println(this.mSaveTriggerId);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("save on finish(): ");
    paramPrintWriter.println(this.mSaveOnFinish);
    if (this.mOptions != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("options: ");
      this.mOptions.dumpShort(paramPrintWriter);
      paramPrintWriter.println();
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("compat mode enabled: ");
    synchronized (this.mLock)
    {
      if (this.mCompatibilityBridge != null)
      {
        Object localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        ((StringBuilder)localObject2).append(paramString);
        ((StringBuilder)localObject2).append("  ");
        localObject2 = ((StringBuilder)localObject2).toString();
        paramPrintWriter.println("true");
        paramPrintWriter.print((String)localObject2);
        paramPrintWriter.print("windowId: ");
        paramPrintWriter.println(this.mCompatibilityBridge.mFocusedWindowId);
        paramPrintWriter.print((String)localObject2);
        paramPrintWriter.print("nodeId: ");
        paramPrintWriter.println(this.mCompatibilityBridge.mFocusedNodeId);
        paramPrintWriter.print((String)localObject2);
        paramPrintWriter.print("virtualId: ");
        paramPrintWriter.println(AccessibilityNodeInfo.getVirtualDescendantId(this.mCompatibilityBridge.mFocusedNodeId));
        paramPrintWriter.print((String)localObject2);
        paramPrintWriter.print("focusedBounds: ");
        paramPrintWriter.println(this.mCompatibilityBridge.mFocusedBounds);
      }
      else
      {
        paramPrintWriter.println("false");
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("debug: ");
      paramPrintWriter.print(Helper.sDebug);
      paramPrintWriter.print(" verbose: ");
      paramPrintWriter.println(Helper.sVerbose);
      return;
    }
  }
  
  public void enableCompatibilityMode()
  {
    synchronized (this.mLock)
    {
      if (Helper.sDebug)
      {
        localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        ((StringBuilder)localObject2).append("creating CompatibilityBridge for ");
        ((StringBuilder)localObject2).append(this.mContext);
        Slog.d("AutofillManager", ((StringBuilder)localObject2).toString());
      }
      Object localObject2 = new android/view/autofill/AutofillManager$CompatibilityBridge;
      ((CompatibilityBridge)localObject2).<init>(this);
      this.mCompatibilityBridge = ((CompatibilityBridge)localObject2);
      return;
    }
  }
  
  public ComponentName getAutofillServiceComponentName()
  {
    if (this.mService == null) {
      return null;
    }
    Object localObject = new SyncResultReceiver(5000);
    try
    {
      this.mService.getAutofillServiceComponentName((IResultReceiver)localObject);
      localObject = (ComponentName)((SyncResultReceiver)localObject).getParcelableResult();
      return (ComponentName)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<String> getAvailableFieldClassificationAlgorithms()
  {
    Object localObject = new SyncResultReceiver(5000);
    try
    {
      this.mService.getAvailableFieldClassificationAlgorithms((IResultReceiver)localObject);
      localObject = ((SyncResultReceiver)localObject).getStringArrayResult();
      if (localObject != null) {
        localObject = Arrays.asList((Object[])localObject);
      } else {
        localObject = Collections.emptyList();
      }
      return (List<String>)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.rethrowFromSystemServer();
    }
    return null;
  }
  
  public String getDefaultFieldClassificationAlgorithm()
  {
    Object localObject = new SyncResultReceiver(5000);
    try
    {
      this.mService.getDefaultFieldClassificationAlgorithm((IResultReceiver)localObject);
      localObject = ((SyncResultReceiver)localObject).getStringResult();
      return (String)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.rethrowFromSystemServer();
    }
    return null;
  }
  
  public FillEventHistory getFillEventHistory()
  {
    try
    {
      Object localObject = new com/android/internal/util/SyncResultReceiver;
      ((SyncResultReceiver)localObject).<init>(5000);
      this.mService.getFillEventHistory((IResultReceiver)localObject);
      localObject = (FillEventHistory)((SyncResultReceiver)localObject).getParcelableResult();
      return (FillEventHistory)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.rethrowFromSystemServer();
    }
    return null;
  }
  
  public AutofillId getNextAutofillId()
  {
    AutofillClient localAutofillClient = getClient();
    if (localAutofillClient == null) {
      return null;
    }
    AutofillId localAutofillId = localAutofillClient.autofillClientGetNextAutofillId();
    if ((localAutofillId == null) && (Helper.sDebug))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("getNextAutofillId(): client ");
      localStringBuilder.append(localAutofillClient);
      localStringBuilder.append(" returned null");
      Log.d("AutofillManager", localStringBuilder.toString());
    }
    return localAutofillId;
  }
  
  public UserData getUserData()
  {
    try
    {
      Object localObject = new com/android/internal/util/SyncResultReceiver;
      ((SyncResultReceiver)localObject).<init>(5000);
      this.mService.getUserData((IResultReceiver)localObject);
      localObject = (UserData)((SyncResultReceiver)localObject).getParcelableResult();
      return (UserData)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.rethrowFromSystemServer();
    }
    return null;
  }
  
  public String getUserDataId()
  {
    try
    {
      Object localObject = new com/android/internal/util/SyncResultReceiver;
      ((SyncResultReceiver)localObject).<init>(5000);
      this.mService.getUserDataId((IResultReceiver)localObject);
      localObject = ((SyncResultReceiver)localObject).getStringResult();
      return (String)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.rethrowFromSystemServer();
    }
    return null;
  }
  
  public boolean hasAutofillFeature()
  {
    boolean bool;
    if (this.mService != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean hasEnabledAutofillServices()
  {
    Object localObject = this.mService;
    boolean bool = false;
    if (localObject == null) {
      return false;
    }
    localObject = new SyncResultReceiver(5000);
    try
    {
      this.mService.isServiceEnabled(this.mContext.getUserId(), this.mContext.getPackageName(), (IResultReceiver)localObject);
      int i = ((SyncResultReceiver)localObject).getIntResult();
      if (i == 1) {
        bool = true;
      }
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isAutofillSupported()
  {
    Object localObject = this.mService;
    boolean bool = false;
    if (localObject == null) {
      return false;
    }
    localObject = new SyncResultReceiver(5000);
    try
    {
      this.mService.isServiceSupported(this.mContext.getUserId(), (IResultReceiver)localObject);
      int i = ((SyncResultReceiver)localObject).getIntResult();
      if (i == 1) {
        bool = true;
      }
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isAutofillUiShowing()
  {
    AutofillClient localAutofillClient = this.mContext.getAutofillClient();
    boolean bool;
    if ((localAutofillClient != null) && (localAutofillClient.autofillClientIsFillUiShowing())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @GuardedBy({"mLock"})
  public boolean isCompatibilityModeEnabledLocked()
  {
    boolean bool;
    if (this.mCompatibilityBridge != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isEnabled()
  {
    if (!hasAutofillFeature()) {
      return false;
    }
    synchronized (this.mLock)
    {
      if (isDisabledByServiceLocked()) {
        return false;
      }
      ensureServiceClientAddedIfNeededLocked();
      boolean bool = this.mEnabled;
      return bool;
    }
  }
  
  public boolean isFieldClassificationEnabled()
  {
    SyncResultReceiver localSyncResultReceiver = new SyncResultReceiver(5000);
    boolean bool = false;
    try
    {
      this.mService.isFieldClassificationEnabled(localSyncResultReceiver);
      int i = localSyncResultReceiver.getIntResult();
      if (i == 1) {
        bool = true;
      }
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.rethrowFromSystemServer();
    }
    return false;
  }
  
  public void notifyValueChanged(View paramView)
  {
    if (!hasAutofillFeature()) {
      return;
    }
    Object localObject1 = null;
    int i = 0;
    AutofillValue localAutofillValue = null;
    synchronized (this.mLock)
    {
      if (this.mLastAutofilledData == null)
      {
        paramView.setAutofilled(false);
      }
      else
      {
        localObject1 = paramView.getAutofillId();
        if (this.mLastAutofilledData.containsKey(localObject1))
        {
          localAutofillValue = paramView.getAutofillValue();
          i = 1;
          if (Objects.equals(this.mLastAutofilledData.get(localObject1), localAutofillValue))
          {
            paramView.setAutofilled(true);
          }
          else
          {
            paramView.setAutofilled(false);
            this.mLastAutofilledData.remove(localObject1);
          }
        }
        else
        {
          paramView.setAutofilled(false);
        }
      }
      if (this.mForAugmentedAutofillOnly)
      {
        if (Helper.sVerbose) {
          Log.v("AutofillManager", "notifyValueChanged(): not notifying system server on augmented-only mode");
        }
        return;
      }
      if ((this.mEnabled) && (isActiveLocked()))
      {
        Object localObject3 = localObject1;
        if (localObject1 == null) {
          localObject3 = paramView.getAutofillId();
        }
        if (i == 0) {
          localAutofillValue = paramView.getAutofillValue();
        }
        updateSessionLocked((AutofillId)localObject3, null, localAutofillValue, 4, 0);
        return;
      }
      if (Helper.sVerbose)
      {
        localObject1 = new java/lang/StringBuilder;
        ((StringBuilder)localObject1).<init>();
        ((StringBuilder)localObject1).append("notifyValueChanged(");
        ((StringBuilder)localObject1).append(paramView.getAutofillId());
        ((StringBuilder)localObject1).append("): ignoring on state ");
        ((StringBuilder)localObject1).append(getStateAsStringLocked());
        Log.v("AutofillManager", ((StringBuilder)localObject1).toString());
      }
      return;
    }
  }
  
  public void notifyValueChanged(View paramView, int paramInt, AutofillValue paramAutofillValue)
  {
    if (!hasAutofillFeature()) {
      return;
    }
    synchronized (this.mLock)
    {
      if (this.mForAugmentedAutofillOnly)
      {
        if (Helper.sVerbose) {
          Log.v("AutofillManager", "notifyValueChanged(): ignoring on augmented only mode");
        }
        return;
      }
      if ((this.mEnabled) && (isActiveLocked()))
      {
        updateSessionLocked(getAutofillId(paramView, paramInt), null, paramAutofillValue, 4, 0);
        return;
      }
      if (Helper.sVerbose)
      {
        paramAutofillValue = new java/lang/StringBuilder;
        paramAutofillValue.<init>();
        paramAutofillValue.append("notifyValueChanged(");
        paramAutofillValue.append(paramView.getAutofillId());
        paramAutofillValue.append(":");
        paramAutofillValue.append(paramInt);
        paramAutofillValue.append("): ignoring on state ");
        paramAutofillValue.append(getStateAsStringLocked());
        Log.v("AutofillManager", paramAutofillValue.toString());
      }
      return;
    }
  }
  
  public void notifyViewClicked(View paramView)
  {
    notifyViewClicked(paramView.getAutofillId());
  }
  
  public void notifyViewClicked(View paramView, int paramInt)
  {
    notifyViewClicked(getAutofillId(paramView, paramInt));
  }
  
  public void notifyViewEntered(View paramView)
  {
    notifyViewEntered(paramView, 0);
  }
  
  public void notifyViewEntered(View paramView, int paramInt, Rect paramRect)
  {
    notifyViewEntered(paramView, paramInt, paramRect, 0);
  }
  
  public void notifyViewEnteredForAugmentedAutofill(View arg1)
  {
    AutofillId localAutofillId = ???.getAutofillId();
    synchronized (this.mLock)
    {
      if (this.mEnteredForAugmentedAutofillIds == null)
      {
        ArraySet localArraySet = new android/util/ArraySet;
        localArraySet.<init>(1);
        this.mEnteredForAugmentedAutofillIds = localArraySet;
      }
      this.mEnteredForAugmentedAutofillIds.add(localAutofillId);
      return;
    }
  }
  
  public void notifyViewExited(View paramView)
  {
    if (!hasAutofillFeature()) {
      return;
    }
    synchronized (this.mLock)
    {
      notifyViewExitedLocked(paramView);
      return;
    }
  }
  
  public void notifyViewExited(View paramView, int paramInt)
  {
    if (Helper.sVerbose)
    {
      ??? = new StringBuilder();
      ((StringBuilder)???).append("notifyViewExited(");
      ((StringBuilder)???).append(paramView.getAutofillId());
      ((StringBuilder)???).append(", ");
      ((StringBuilder)???).append(paramInt);
      Log.v("AutofillManager", ((StringBuilder)???).toString());
    }
    if (!hasAutofillFeature()) {
      return;
    }
    synchronized (this.mLock)
    {
      notifyViewExitedLocked(paramView, paramInt);
      return;
    }
  }
  
  @GuardedBy({"mLock"})
  void notifyViewExitedLocked(View paramView)
  {
    ensureServiceClientAddedIfNeededLocked();
    if (((this.mEnabled) || (this.mEnabledForAugmentedAutofillOnly)) && (isActiveLocked()) && (!isClientDisablingEnterExitEvent())) {
      updateSessionLocked(paramView.getAutofillId(), null, null, 3, 0);
    }
  }
  
  public void notifyViewVisibilityChanged(View paramView, int paramInt, boolean paramBoolean)
  {
    notifyViewVisibilityChangedInternal(paramView, paramInt, paramBoolean, true);
  }
  
  public void notifyViewVisibilityChanged(View paramView, boolean paramBoolean)
  {
    notifyViewVisibilityChangedInternal(paramView, 0, paramBoolean, false);
  }
  
  public void onActivityFinishing()
  {
    if (!hasAutofillFeature()) {
      return;
    }
    synchronized (this.mLock)
    {
      if (this.mSaveOnFinish)
      {
        if (Helper.sDebug) {
          Log.d("AutofillManager", "onActivityFinishing(): calling commitLocked()");
        }
        commitLocked();
      }
      else
      {
        if (Helper.sDebug) {
          Log.d("AutofillManager", "onActivityFinishing(): calling cancelLocked()");
        }
        cancelLocked();
      }
      return;
    }
  }
  
  public void onAuthenticationResult(int paramInt, Intent paramIntent, View paramView)
  {
    if (!hasAutofillFeature()) {
      return;
    }
    if (Helper.sDebug)
    {
      ??? = new StringBuilder();
      ((StringBuilder)???).append("onAuthenticationResult(): id= ");
      ((StringBuilder)???).append(paramInt);
      ((StringBuilder)???).append(", data=");
      ((StringBuilder)???).append(paramIntent);
      Log.d("AutofillManager", ((StringBuilder)???).toString());
    }
    synchronized (this.mLock)
    {
      if (!isActiveLocked()) {
        return;
      }
      if ((!this.mOnInvisibleCalled) && (paramView != null) && (paramView.canNotifyAutofillEnterExitEvent()))
      {
        notifyViewExitedLocked(paramView);
        notifyViewEnteredLocked(paramView, 0);
      }
      if (paramIntent == null) {
        return;
      }
      Parcelable localParcelable = paramIntent.getParcelableExtra("android.view.autofill.extra.AUTHENTICATION_RESULT");
      paramView = new android/os/Bundle;
      paramView.<init>();
      paramView.putParcelable("android.view.autofill.extra.AUTHENTICATION_RESULT", localParcelable);
      paramIntent = paramIntent.getBundleExtra("android.view.autofill.extra.CLIENT_STATE");
      if (paramIntent != null) {
        paramView.putBundle("android.view.autofill.extra.CLIENT_STATE", paramIntent);
      }
      try
      {
        this.mService.setAuthenticationResult(paramView, this.mSessionId, paramInt, this.mContext.getUserId());
      }
      catch (RemoteException paramIntent)
      {
        Log.e("AutofillManager", "Error delivering authentication result", paramIntent);
      }
      return;
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    if (!hasAutofillFeature()) {
      return;
    }
    synchronized (this.mLock)
    {
      this.mLastAutofilledData = ((ParcelableMap)paramBundle.getParcelable("android:lastAutoFilledData"));
      if (isActiveLocked())
      {
        Log.w("AutofillManager", "New session was started before onCreate()");
        return;
      }
      this.mSessionId = paramBundle.getInt("android:sessionId", Integer.MAX_VALUE);
      this.mState = paramBundle.getInt("android:state", 0);
      if (this.mSessionId != Integer.MAX_VALUE)
      {
        ensureServiceClientAddedIfNeededLocked();
        paramBundle = getClient();
        if (paramBundle != null)
        {
          Object localObject2 = new com/android/internal/util/SyncResultReceiver;
          ((SyncResultReceiver)localObject2).<init>(5000);
          try
          {
            this.mService.restoreSession(this.mSessionId, paramBundle.autofillClientGetActivityToken(), this.mServiceClient.asBinder(), (IResultReceiver)localObject2);
            int i = ((SyncResultReceiver)localObject2).getIntResult();
            int j = 1;
            if (i != 1) {
              j = 0;
            }
            if (j == 0)
            {
              paramBundle = new java/lang/StringBuilder;
              paramBundle.<init>();
              paramBundle.append("Session ");
              paramBundle.append(this.mSessionId);
              paramBundle.append(" could not be restored");
              Log.w("AutofillManager", paramBundle.toString());
              this.mSessionId = Integer.MAX_VALUE;
              this.mState = 0;
            }
            else
            {
              if (Helper.sDebug)
              {
                localObject2 = new java/lang/StringBuilder;
                ((StringBuilder)localObject2).<init>();
                ((StringBuilder)localObject2).append("session ");
                ((StringBuilder)localObject2).append(this.mSessionId);
                ((StringBuilder)localObject2).append(" was restored");
                Log.d("AutofillManager", ((StringBuilder)localObject2).toString());
              }
              paramBundle.autofillClientResetableStateAvailable();
            }
          }
          catch (RemoteException paramBundle)
          {
            Log.e("AutofillManager", "Could not figure out if there was an autofill session", paramBundle);
          }
        }
      }
      return;
    }
  }
  
  public void onInvisibleForAutofill()
  {
    synchronized (this.mLock)
    {
      this.mOnInvisibleCalled = true;
      return;
    }
  }
  
  public void onPendingSaveUi(int paramInt, IBinder paramIBinder)
  {
    if (Helper.sVerbose)
    {
      ??? = new StringBuilder();
      ((StringBuilder)???).append("onPendingSaveUi(");
      ((StringBuilder)???).append(paramInt);
      ((StringBuilder)???).append("): ");
      ((StringBuilder)???).append(paramIBinder);
      Log.v("AutofillManager", ((StringBuilder)???).toString());
    }
    try
    {
      synchronized (this.mLock)
      {
        this.mService.onPendingSaveUi(paramInt, paramIBinder);
      }
    }
    catch (RemoteException paramIBinder)
    {
      paramIBinder.rethrowFromSystemServer();
      return;
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    if (!hasAutofillFeature()) {
      return;
    }
    synchronized (this.mLock)
    {
      if (this.mSessionId != Integer.MAX_VALUE) {
        paramBundle.putInt("android:sessionId", this.mSessionId);
      }
      if (this.mState != 0) {
        paramBundle.putInt("android:state", this.mState);
      }
      if (this.mLastAutofilledData != null) {
        paramBundle.putParcelable("android:lastAutoFilledData", this.mLastAutofilledData);
      }
      return;
    }
  }
  
  public void onVisibleForAutofill()
  {
    Choreographer.getInstance().postCallback(4, new _..Lambda.AutofillManager.YfpJNFodEuj5lbXfPlc77fsEvC8(this), null);
  }
  
  /* Error */
  public void registerCallback(AutofillCallback paramAutofillCallback)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 685	android/view/autofill/AutofillManager:hasAutofillFeature	()Z
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: getfield 187	android/view/autofill/AutofillManager:mLock	Ljava/lang/Object;
    //   12: astore_2
    //   13: aload_2
    //   14: monitorenter
    //   15: aload_1
    //   16: ifnonnull +6 -> 22
    //   19: aload_2
    //   20: monitorexit
    //   21: return
    //   22: aload_0
    //   23: getfield 674	android/view/autofill/AutofillManager:mCallback	Landroid/view/autofill/AutofillManager$AutofillCallback;
    //   26: ifnull +8 -> 34
    //   29: iconst_1
    //   30: istore_3
    //   31: goto +5 -> 36
    //   34: iconst_0
    //   35: istore_3
    //   36: aload_0
    //   37: aload_1
    //   38: putfield 674	android/view/autofill/AutofillManager:mCallback	Landroid/view/autofill/AutofillManager$AutofillCallback;
    //   41: iload_3
    //   42: ifne +33 -> 75
    //   45: aload_0
    //   46: getfield 205	android/view/autofill/AutofillManager:mService	Landroid/view/autofill/IAutoFillManager;
    //   49: aload_0
    //   50: getfield 189	android/view/autofill/AutofillManager:mSessionId	I
    //   53: aload_0
    //   54: getfield 203	android/view/autofill/AutofillManager:mContext	Landroid/content/Context;
    //   57: invokevirtual 437	android/content/Context:getUserId	()I
    //   60: iconst_1
    //   61: invokeinterface 1318 4 0
    //   66: goto +9 -> 75
    //   69: astore_1
    //   70: aload_1
    //   71: invokevirtual 447	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   74: athrow
    //   75: aload_2
    //   76: monitorexit
    //   77: return
    //   78: astore_1
    //   79: aload_2
    //   80: monitorexit
    //   81: aload_1
    //   82: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	83	0	this	AutofillManager
    //   0	83	1	paramAutofillCallback	AutofillCallback
    //   12	68	2	localObject	Object
    //   30	12	3	i	int
    // Exception table:
    //   from	to	target	type
    //   45	66	69	android/os/RemoteException
    //   19	21	78	finally
    //   22	29	78	finally
    //   36	41	78	finally
    //   45	66	78	finally
    //   70	75	78	finally
    //   75	77	78	finally
    //   79	81	78	finally
  }
  
  public void requestAutofill(View paramView)
  {
    notifyViewEntered(paramView, 1);
  }
  
  public void requestAutofill(View paramView, int paramInt, Rect paramRect)
  {
    notifyViewEntered(paramView, paramInt, paramRect, 1);
  }
  
  public void requestHideFillUi()
  {
    requestHideFillUi(this.mIdShownFillUi, true);
  }
  
  @SystemApi
  public void setAugmentedAutofillWhitelist(Set<String> paramSet, Set<ComponentName> paramSet1)
  {
    if (!hasAutofillFeature()) {
      return;
    }
    SyncResultReceiver localSyncResultReceiver = new SyncResultReceiver(5000);
    try
    {
      this.mService.setAugmentedAutofillWhitelist(Helper.toList(paramSet), Helper.toList(paramSet1), localSyncResultReceiver);
      int i = localSyncResultReceiver.getIntResult();
      if (i != -1)
      {
        if (i != 0)
        {
          paramSet = new StringBuilder();
          paramSet.append("setAugmentedAutofillWhitelist(): received invalid result: ");
          paramSet.append(i);
          Log.wtf("AutofillManager", paramSet.toString());
          return;
        }
        return;
      }
      throw new SecurityException("caller is not user's Augmented Autofill Service");
    }
    catch (RemoteException paramSet)
    {
      throw paramSet.rethrowFromSystemServer();
    }
  }
  
  public void setUserData(UserData paramUserData)
  {
    try
    {
      this.mService.setUserData(paramUserData);
    }
    catch (RemoteException paramUserData)
    {
      paramUserData.rethrowFromSystemServer();
    }
  }
  
  public void unregisterCallback(AutofillCallback paramAutofillCallback)
  {
    if (!hasAutofillFeature()) {
      return;
    }
    Object localObject = this.mLock;
    if (paramAutofillCallback != null) {}
    try
    {
      if ((this.mCallback != null) && (paramAutofillCallback == this.mCallback))
      {
        this.mCallback = null;
        try
        {
          this.mService.setHasCallback(this.mSessionId, this.mContext.getUserId(), false);
          return;
        }
        catch (RemoteException paramAutofillCallback)
        {
          throw paramAutofillCallback.rethrowFromSystemServer();
        }
      }
      return;
    }
    finally {}
  }
  
  private static final class AugmentedAutofillManagerClient
    extends IAugmentedAutofillManagerClient.Stub
  {
    private final WeakReference<AutofillManager> mAfm;
    
    private AugmentedAutofillManagerClient(AutofillManager paramAutofillManager)
    {
      this.mAfm = new WeakReference(paramAutofillManager);
    }
    
    public void autofill(int paramInt, List<AutofillId> paramList, List<AutofillValue> paramList1)
    {
      AutofillManager localAutofillManager = (AutofillManager)this.mAfm.get();
      if (localAutofillManager != null) {
        localAutofillManager.post(new _..Lambda.AutofillManager.AugmentedAutofillManagerClient.k_qssZkEBwVEPdSmrHGsi2QT_3Y(localAutofillManager, paramInt, paramList, paramList1));
      }
    }
    
    public Rect getViewCoordinates(AutofillId paramAutofillId)
    {
      Object localObject1 = (AutofillManager)this.mAfm.get();
      if (localObject1 == null) {
        return null;
      }
      localObject1 = ((AutofillManager)localObject1).getClient();
      if (localObject1 == null)
      {
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("getViewCoordinates(");
        ((StringBuilder)localObject1).append(paramAutofillId);
        ((StringBuilder)localObject1).append("): no autofill client");
        Log.w("AutofillManager", ((StringBuilder)localObject1).toString());
        return null;
      }
      Object localObject2 = ((AutofillManager.AutofillClient)localObject1).autofillClientFindViewByAutofillIdTraversal(paramAutofillId);
      if (localObject2 == null)
      {
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("getViewCoordinates(");
        ((StringBuilder)localObject1).append(paramAutofillId);
        ((StringBuilder)localObject1).append("): could not find view");
        Log.w("AutofillManager", ((StringBuilder)localObject1).toString());
        return null;
      }
      Rect localRect = new Rect();
      ((View)localObject2).getWindowVisibleDisplayFrame(localRect);
      localObject1 = new int[2];
      ((View)localObject2).getLocationOnScreen((int[])localObject1);
      localObject1 = new Rect(localObject1[0], localObject1[1] - localRect.top, localObject1[0] + ((View)localObject2).getWidth(), localObject1[1] - localRect.top + ((View)localObject2).getHeight());
      if (Helper.sVerbose)
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("Coordinates for ");
        ((StringBuilder)localObject2).append(paramAutofillId);
        ((StringBuilder)localObject2).append(": ");
        ((StringBuilder)localObject2).append(localObject1);
        Log.v("AutofillManager", ((StringBuilder)localObject2).toString());
      }
      return (Rect)localObject1;
    }
    
    public void requestHideFillUi(int paramInt, AutofillId paramAutofillId)
    {
      AutofillManager localAutofillManager = (AutofillManager)this.mAfm.get();
      if (localAutofillManager != null) {
        localAutofillManager.post(new _..Lambda.AutofillManager.AugmentedAutofillManagerClient.tbNtqpHgXnRdc3JO5HaBlxclFg0(localAutofillManager, paramAutofillId));
      }
    }
    
    public void requestShowFillUi(int paramInt1, AutofillId paramAutofillId, int paramInt2, int paramInt3, Rect paramRect, IAutofillWindowPresenter paramIAutofillWindowPresenter)
    {
      AutofillManager localAutofillManager = (AutofillManager)this.mAfm.get();
      if (localAutofillManager != null) {
        localAutofillManager.post(new _..Lambda.AutofillManager.AugmentedAutofillManagerClient.OrAY5q15e0VwuCSYnsGgs6GcY1U(localAutofillManager, paramInt1, paramAutofillId, paramInt2, paramInt3, paramRect, paramIAutofillWindowPresenter));
      }
    }
  }
  
  public static abstract class AutofillCallback
  {
    public static final int EVENT_INPUT_HIDDEN = 2;
    public static final int EVENT_INPUT_SHOWN = 1;
    public static final int EVENT_INPUT_UNAVAILABLE = 3;
    
    public void onAutofillEvent(View paramView, int paramInt) {}
    
    public void onAutofillEvent(View paramView, int paramInt1, int paramInt2) {}
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface AutofillEventType {}
  }
  
  public static abstract interface AutofillClient
  {
    public abstract void autofillClientAuthenticate(int paramInt, IntentSender paramIntentSender, Intent paramIntent);
    
    public abstract void autofillClientDispatchUnhandledKey(View paramView, KeyEvent paramKeyEvent);
    
    public abstract View autofillClientFindViewByAccessibilityIdTraversal(int paramInt1, int paramInt2);
    
    public abstract View autofillClientFindViewByAutofillIdTraversal(AutofillId paramAutofillId);
    
    public abstract View[] autofillClientFindViewsByAutofillIdTraversal(AutofillId[] paramArrayOfAutofillId);
    
    public abstract IBinder autofillClientGetActivityToken();
    
    public abstract ComponentName autofillClientGetComponentName();
    
    public abstract AutofillId autofillClientGetNextAutofillId();
    
    public abstract boolean[] autofillClientGetViewVisibility(AutofillId[] paramArrayOfAutofillId);
    
    public abstract boolean autofillClientIsCompatibilityModeEnabled();
    
    public abstract boolean autofillClientIsFillUiShowing();
    
    public abstract boolean autofillClientIsVisibleForAutofill();
    
    public abstract boolean autofillClientRequestHideFillUi();
    
    public abstract boolean autofillClientRequestShowFillUi(View paramView, int paramInt1, int paramInt2, Rect paramRect, IAutofillWindowPresenter paramIAutofillWindowPresenter);
    
    public abstract void autofillClientResetableStateAvailable();
    
    public abstract void autofillClientRunOnUiThread(Runnable paramRunnable);
    
    public abstract boolean isDisablingEnterExitEventForAutofill();
  }
  
  private static final class AutofillManagerClient
    extends IAutoFillManagerClient.Stub
  {
    private final WeakReference<AutofillManager> mAfm;
    
    private AutofillManagerClient(AutofillManager paramAutofillManager)
    {
      this.mAfm = new WeakReference(paramAutofillManager);
    }
    
    public void authenticate(int paramInt1, int paramInt2, IntentSender paramIntentSender, Intent paramIntent)
    {
      AutofillManager localAutofillManager = (AutofillManager)this.mAfm.get();
      if (localAutofillManager != null) {
        localAutofillManager.post(new _..Lambda.AutofillManager.AutofillManagerClient.qyxZ4PACUgHFGSvMBHzgwjJ3yns(localAutofillManager, paramInt1, paramInt2, paramIntentSender, paramIntent));
      }
    }
    
    public void autofill(int paramInt, List<AutofillId> paramList, List<AutofillValue> paramList1)
    {
      AutofillManager localAutofillManager = (AutofillManager)this.mAfm.get();
      if (localAutofillManager != null) {
        localAutofillManager.post(new _..Lambda.AutofillManager.AutofillManagerClient.1jAzMluMSJksx55SMUQn4BKB2Ng(localAutofillManager, paramInt, paramList, paramList1));
      }
    }
    
    public void dispatchUnhandledKey(int paramInt, AutofillId paramAutofillId, KeyEvent paramKeyEvent)
    {
      AutofillManager localAutofillManager = (AutofillManager)this.mAfm.get();
      if (localAutofillManager != null) {
        localAutofillManager.post(new _..Lambda.AutofillManager.AutofillManagerClient.xqXjXW0fvc8JdYR5fgGKw9lJc3I(localAutofillManager, paramInt, paramAutofillId, paramKeyEvent));
      }
    }
    
    public void getAugmentedAutofillClient(IResultReceiver paramIResultReceiver)
    {
      AutofillManager localAutofillManager = (AutofillManager)this.mAfm.get();
      if (localAutofillManager != null) {
        localAutofillManager.post(new _..Lambda.AutofillManager.AutofillManagerClient.eeFWMGoPtaXdpslR3NLvhgXvMMs(localAutofillManager, paramIResultReceiver));
      }
    }
    
    public void notifyNoFillUi(int paramInt1, AutofillId paramAutofillId, int paramInt2)
    {
      AutofillManager localAutofillManager = (AutofillManager)this.mAfm.get();
      if (localAutofillManager != null) {
        localAutofillManager.post(new _..Lambda.AutofillManager.AutofillManagerClient.K79QnIPRaZuikYDQdsLcIUBhqiI(localAutofillManager, paramInt1, paramAutofillId, paramInt2));
      }
    }
    
    public void requestHideFillUi(int paramInt, AutofillId paramAutofillId)
    {
      AutofillManager localAutofillManager = (AutofillManager)this.mAfm.get();
      if (localAutofillManager != null) {
        localAutofillManager.post(new _..Lambda.AutofillManager.AutofillManagerClient.dCTetwfU0gT1ZrSzZGZiGStXlOY(localAutofillManager, paramAutofillId));
      }
    }
    
    public void requestShowFillUi(int paramInt1, AutofillId paramAutofillId, int paramInt2, int paramInt3, Rect paramRect, IAutofillWindowPresenter paramIAutofillWindowPresenter)
    {
      AutofillManager localAutofillManager = (AutofillManager)this.mAfm.get();
      if (localAutofillManager != null) {
        localAutofillManager.post(new _..Lambda.AutofillManager.AutofillManagerClient.kRL9XILLc2XNr90gxVDACLzcyqc(localAutofillManager, paramInt1, paramAutofillId, paramInt2, paramInt3, paramRect, paramIAutofillWindowPresenter));
      }
    }
    
    public void setSaveUiState(int paramInt, boolean paramBoolean)
    {
      AutofillManager localAutofillManager = (AutofillManager)this.mAfm.get();
      if (localAutofillManager != null) {
        localAutofillManager.post(new _..Lambda.AutofillManager.AutofillManagerClient.QIW_100CKwHzdHffwaus9KOEHCA(localAutofillManager, paramInt, paramBoolean));
      }
    }
    
    public void setSessionFinished(int paramInt, List<AutofillId> paramList)
    {
      AutofillManager localAutofillManager = (AutofillManager)this.mAfm.get();
      if (localAutofillManager != null) {
        localAutofillManager.post(new _..Lambda.AutofillManager.AutofillManagerClient._IhPS_W7AwZ4M9TKIIFigmQd5SE(localAutofillManager, paramInt, paramList));
      }
    }
    
    public void setState(int paramInt)
    {
      AutofillManager localAutofillManager = (AutofillManager)this.mAfm.get();
      if (localAutofillManager != null) {
        localAutofillManager.post(new _..Lambda.AutofillManager.AutofillManagerClient.qH36EJk2Hkdja9ZZmTxqYPyr0YA(localAutofillManager, paramInt));
      }
    }
    
    public void setTrackedViews(int paramInt, AutofillId[] paramArrayOfAutofillId1, boolean paramBoolean1, boolean paramBoolean2, AutofillId[] paramArrayOfAutofillId2, AutofillId paramAutofillId)
    {
      AutofillManager localAutofillManager = (AutofillManager)this.mAfm.get();
      if (localAutofillManager != null) {
        localAutofillManager.post(new _..Lambda.AutofillManager.AutofillManagerClient.BPlC2x7GLNHFS92rPUSzbcpFhUc(localAutofillManager, paramInt, paramArrayOfAutofillId1, paramBoolean1, paramBoolean2, paramArrayOfAutofillId2, paramAutofillId));
      }
    }
    
    public void startIntentSender(IntentSender paramIntentSender, Intent paramIntent)
    {
      AutofillManager localAutofillManager = (AutofillManager)this.mAfm.get();
      if (localAutofillManager != null) {
        localAutofillManager.post(new _..Lambda.AutofillManager.AutofillManagerClient.pM5e3ez5KTBdZt4d8qLEERBUSiU(localAutofillManager, paramIntentSender, paramIntent));
      }
    }
  }
  
  private final class CompatibilityBridge
    implements AccessibilityManager.AccessibilityPolicy
  {
    @GuardedBy({"mLock"})
    AccessibilityServiceInfo mCompatServiceInfo;
    @GuardedBy({"mLock"})
    private final Rect mFocusedBounds = new Rect();
    @GuardedBy({"mLock"})
    private long mFocusedNodeId = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
    @GuardedBy({"mLock"})
    private int mFocusedWindowId = -1;
    @GuardedBy({"mLock"})
    private final Rect mTempBounds = new Rect();
    
    CompatibilityBridge()
    {
      AccessibilityManager.getInstance(AutofillManager.this.mContext).setAccessibilityPolicy(this);
    }
    
    private View findViewByAccessibilityId(int paramInt, long paramLong)
    {
      AutofillManager.AutofillClient localAutofillClient = AutofillManager.this.getClient();
      if (localAutofillClient == null) {
        return null;
      }
      return localAutofillClient.autofillClientFindViewByAccessibilityIdTraversal(AccessibilityNodeInfo.getAccessibilityViewId(paramLong), paramInt);
    }
    
    private AccessibilityNodeInfo findVirtualNodeByAccessibilityId(View paramView, int paramInt)
    {
      paramView = paramView.getAccessibilityNodeProvider();
      if (paramView == null) {
        return null;
      }
      return paramView.createAccessibilityNodeInfo(paramInt);
    }
    
    private AccessibilityServiceInfo getCompatServiceInfo()
    {
      synchronized (AutofillManager.this.mLock)
      {
        if (this.mCompatServiceInfo != null)
        {
          localObject2 = this.mCompatServiceInfo;
          return (AccessibilityServiceInfo)localObject2;
        }
        Object localObject2 = new android/content/Intent;
        ((Intent)localObject2).<init>();
        Object localObject4 = new android/content/ComponentName;
        ((ComponentName)localObject4).<init>("android", "com.android.server.autofill.AutofillCompatAccessibilityService");
        ((Intent)localObject2).setComponent((ComponentName)localObject4);
        ResolveInfo localResolveInfo = AutofillManager.this.mContext.getPackageManager().resolveService((Intent)localObject2, 1048704);
        try
        {
          localObject4 = new android/accessibilityservice/AccessibilityServiceInfo;
          ((AccessibilityServiceInfo)localObject4).<init>(localResolveInfo, AutofillManager.this.mContext);
          this.mCompatServiceInfo = ((AccessibilityServiceInfo)localObject4);
          localObject2 = this.mCompatServiceInfo;
          return (AccessibilityServiceInfo)localObject2;
        }
        catch (XmlPullParserException|IOException localXmlPullParserException)
        {
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localStringBuilder.append("Cannot find compat autofill service:");
          localStringBuilder.append(localObject2);
          Log.e("AutofillManager", localStringBuilder.toString());
          localObject2 = new java/lang/IllegalStateException;
          ((IllegalStateException)localObject2).<init>("Cannot find compat autofill service");
          throw ((Throwable)localObject2);
        }
      }
    }
    
    private boolean isVirtualNode(int paramInt)
    {
      boolean bool;
      if ((paramInt != -1) && (paramInt != Integer.MAX_VALUE)) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    private void notifyValueChanged(int paramInt, long paramLong)
    {
      int i = AccessibilityNodeInfo.getVirtualDescendantId(paramLong);
      if (!isVirtualNode(i)) {
        return;
      }
      View localView = findViewByAccessibilityId(paramInt, paramLong);
      if (localView == null) {
        return;
      }
      AccessibilityNodeInfo localAccessibilityNodeInfo = findVirtualNodeByAccessibilityId(localView, i);
      if (localAccessibilityNodeInfo == null) {
        return;
      }
      AutofillManager.this.notifyValueChanged(localView, i, AutofillValue.forText(localAccessibilityNodeInfo.getText()));
    }
    
    private void notifyViewClicked(int paramInt, long paramLong)
    {
      int i = AccessibilityNodeInfo.getVirtualDescendantId(paramLong);
      if (!isVirtualNode(i)) {
        return;
      }
      View localView = findViewByAccessibilityId(paramInt, paramLong);
      if (localView == null) {
        return;
      }
      if (findVirtualNodeByAccessibilityId(localView, i) == null) {
        return;
      }
      AutofillManager.this.notifyViewClicked(localView, i);
    }
    
    private boolean notifyViewEntered(int paramInt, long paramLong, Rect paramRect)
    {
      int i = AccessibilityNodeInfo.getVirtualDescendantId(paramLong);
      if (!isVirtualNode(i)) {
        return false;
      }
      View localView = findViewByAccessibilityId(paramInt, paramLong);
      if (localView == null) {
        return false;
      }
      AccessibilityNodeInfo localAccessibilityNodeInfo = findVirtualNodeByAccessibilityId(localView, i);
      if (localAccessibilityNodeInfo == null) {
        return false;
      }
      if (!localAccessibilityNodeInfo.isEditable()) {
        return false;
      }
      Rect localRect = this.mTempBounds;
      localAccessibilityNodeInfo.getBoundsInScreen(localRect);
      if (localRect.equals(paramRect)) {
        return false;
      }
      paramRect.set(localRect);
      AutofillManager.this.notifyViewEntered(localView, i, localRect);
      return true;
    }
    
    private void notifyViewExited(int paramInt, long paramLong)
    {
      int i = AccessibilityNodeInfo.getVirtualDescendantId(paramLong);
      if (!isVirtualNode(i)) {
        return;
      }
      View localView = findViewByAccessibilityId(paramInt, paramLong);
      if (localView == null) {
        return;
      }
      AutofillManager.this.notifyViewExited(localView, i);
    }
    
    @GuardedBy({"mLock"})
    private void updateTrackedViewsLocked()
    {
      if (AutofillManager.this.mTrackedViews != null) {
        AutofillManager.this.mTrackedViews.onVisibleForAutofillChangedLocked();
      }
    }
    
    public List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int paramInt, List<AccessibilityServiceInfo> paramList)
    {
      Object localObject = paramList;
      if (paramList == null) {
        localObject = new ArrayList();
      }
      ((List)localObject).add(getCompatServiceInfo());
      return (List<AccessibilityServiceInfo>)localObject;
    }
    
    public List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(List<AccessibilityServiceInfo> paramList)
    {
      Object localObject = paramList;
      if (paramList == null) {
        localObject = new ArrayList();
      }
      ((List)localObject).add(getCompatServiceInfo());
      return (List<AccessibilityServiceInfo>)localObject;
    }
    
    public int getRelevantEventTypes(int paramInt)
    {
      return paramInt | 0x8 | 0x10 | 0x1 | 0x800;
    }
    
    public boolean isEnabled(boolean paramBoolean)
    {
      return true;
    }
    
    public AccessibilityEvent onAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent, boolean paramBoolean, int paramInt)
    {
      paramInt = paramAccessibilityEvent.getEventType();
      if (Helper.sVerbose)
      {
        ??? = new StringBuilder();
        ((StringBuilder)???).append("onAccessibilityEvent(");
        ((StringBuilder)???).append(AccessibilityEvent.eventTypeToString(paramInt));
        ((StringBuilder)???).append("): virtualId=");
        ((StringBuilder)???).append(AccessibilityNodeInfo.getVirtualDescendantId(paramAccessibilityEvent.getSourceNodeId()));
        ((StringBuilder)???).append(", client=");
        ((StringBuilder)???).append(AutofillManager.this.getClient());
        Log.v("AutofillManager", ((StringBuilder)???).toString());
      }
      if (paramInt != 1)
      {
        if (paramInt != 8)
        {
          if (paramInt != 16)
          {
            if (paramInt != 2048) {
              break label418;
            }
            AutofillManager.AutofillClient localAutofillClient = AutofillManager.this.getClient();
            if (localAutofillClient == null) {
              break label418;
            }
            synchronized (AutofillManager.this.mLock)
            {
              if (localAutofillClient.autofillClientIsFillUiShowing()) {
                notifyViewEntered(this.mFocusedWindowId, this.mFocusedNodeId, this.mFocusedBounds);
              }
              updateTrackedViewsLocked();
            }
          }
          synchronized (AutofillManager.this.mLock)
          {
            if ((this.mFocusedWindowId == paramAccessibilityEvent.getWindowId()) && (this.mFocusedNodeId == paramAccessibilityEvent.getSourceNodeId())) {
              notifyValueChanged(paramAccessibilityEvent.getWindowId(), paramAccessibilityEvent.getSourceNodeId());
            }
          }
        }
        synchronized (AutofillManager.this.mLock)
        {
          if ((this.mFocusedWindowId == paramAccessibilityEvent.getWindowId()) && (this.mFocusedNodeId == paramAccessibilityEvent.getSourceNodeId())) {
            return paramAccessibilityEvent;
          }
          if ((this.mFocusedWindowId != -1) && (this.mFocusedNodeId != AccessibilityNodeInfo.UNDEFINED_NODE_ID))
          {
            notifyViewExited(this.mFocusedWindowId, this.mFocusedNodeId);
            this.mFocusedWindowId = -1;
            this.mFocusedNodeId = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
            this.mFocusedBounds.set(0, 0, 0, 0);
          }
          paramInt = paramAccessibilityEvent.getWindowId();
          long l = paramAccessibilityEvent.getSourceNodeId();
          if (notifyViewEntered(paramInt, l, this.mFocusedBounds))
          {
            this.mFocusedWindowId = paramInt;
            this.mFocusedNodeId = l;
          }
        }
      }
      synchronized (AutofillManager.this.mLock)
      {
        notifyViewClicked(paramAccessibilityEvent.getWindowId(), paramAccessibilityEvent.getSourceNodeId());
        label418:
        if (!paramBoolean) {
          paramAccessibilityEvent = null;
        }
        return paramAccessibilityEvent;
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface SmartSuggestionMode {}
  
  private class TrackedViews
  {
    private ArraySet<AutofillId> mInvisibleTrackedIds;
    private ArraySet<AutofillId> mVisibleTrackedIds;
    
    TrackedViews(AutofillId[] paramArrayOfAutofillId)
    {
      Object localObject = AutofillManager.this.getClient();
      if ((!ArrayUtils.isEmpty(paramArrayOfAutofillId)) && (localObject != null))
      {
        if (((AutofillManager.AutofillClient)localObject).autofillClientIsVisibleForAutofill())
        {
          if (Helper.sVerbose) {
            Log.v("AutofillManager", "client is visible, check tracked ids");
          }
          localObject = ((AutofillManager.AutofillClient)localObject).autofillClientGetViewVisibility(paramArrayOfAutofillId);
        }
        else
        {
          localObject = new boolean[paramArrayOfAutofillId.length];
        }
        int i = paramArrayOfAutofillId.length;
        for (int j = 0; j < i; j++)
        {
          AutofillId localAutofillId = paramArrayOfAutofillId[j];
          localAutofillId.resetSessionId();
          if (localObject[j] != 0) {
            this.mVisibleTrackedIds = addToSet(this.mVisibleTrackedIds, localAutofillId);
          } else {
            this.mInvisibleTrackedIds = addToSet(this.mInvisibleTrackedIds, localAutofillId);
          }
        }
      }
      if (Helper.sVerbose)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("TrackedViews(trackedIds=");
        ((StringBuilder)localObject).append(Arrays.toString(paramArrayOfAutofillId));
        ((StringBuilder)localObject).append("):  mVisibleTrackedIds=");
        ((StringBuilder)localObject).append(this.mVisibleTrackedIds);
        ((StringBuilder)localObject).append(" mInvisibleTrackedIds=");
        ((StringBuilder)localObject).append(this.mInvisibleTrackedIds);
        Log.v("AutofillManager", ((StringBuilder)localObject).toString());
      }
      if (this.mVisibleTrackedIds == null) {
        AutofillManager.this.finishSessionLocked();
      }
    }
    
    private <T> ArraySet<T> addToSet(ArraySet<T> paramArraySet, T paramT)
    {
      Object localObject = paramArraySet;
      if (paramArraySet == null) {
        localObject = new ArraySet(1);
      }
      ((ArraySet)localObject).add(paramT);
      return (ArraySet<T>)localObject;
    }
    
    private <T> boolean isInSet(ArraySet<T> paramArraySet, T paramT)
    {
      boolean bool;
      if ((paramArraySet != null) && (paramArraySet.contains(paramT))) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    private <T> ArraySet<T> removeFromSet(ArraySet<T> paramArraySet, T paramT)
    {
      if (paramArraySet == null) {
        return null;
      }
      paramArraySet.remove(paramT);
      if (paramArraySet.isEmpty()) {
        return null;
      }
      return paramArraySet;
    }
    
    @GuardedBy({"mLock"})
    void notifyViewVisibilityChangedLocked(AutofillId paramAutofillId, boolean paramBoolean)
    {
      if (Helper.sDebug)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("notifyViewVisibilityChangedLocked(): id=");
        localStringBuilder.append(paramAutofillId);
        localStringBuilder.append(" isVisible=");
        localStringBuilder.append(paramBoolean);
        Log.d("AutofillManager", localStringBuilder.toString());
      }
      if (AutofillManager.this.isClientVisibleForAutofillLocked()) {
        if (paramBoolean)
        {
          if (isInSet(this.mInvisibleTrackedIds, paramAutofillId))
          {
            this.mInvisibleTrackedIds = removeFromSet(this.mInvisibleTrackedIds, paramAutofillId);
            this.mVisibleTrackedIds = addToSet(this.mVisibleTrackedIds, paramAutofillId);
          }
        }
        else if (isInSet(this.mVisibleTrackedIds, paramAutofillId))
        {
          this.mVisibleTrackedIds = removeFromSet(this.mVisibleTrackedIds, paramAutofillId);
          this.mInvisibleTrackedIds = addToSet(this.mInvisibleTrackedIds, paramAutofillId);
        }
      }
      if (this.mVisibleTrackedIds == null)
      {
        if (Helper.sVerbose)
        {
          paramAutofillId = new StringBuilder();
          paramAutofillId.append("No more visible ids. Invisibile = ");
          paramAutofillId.append(this.mInvisibleTrackedIds);
          Log.v("AutofillManager", paramAutofillId.toString());
        }
        AutofillManager.this.finishSessionLocked();
      }
    }
    
    @GuardedBy({"mLock"})
    void onVisibleForAutofillChangedLocked()
    {
      Object localObject1 = AutofillManager.this.getClient();
      Object localObject2 = null;
      Object localObject3 = null;
      Object localObject4 = null;
      Object localObject5 = null;
      if (localObject1 != null)
      {
        if (Helper.sVerbose)
        {
          localObject6 = new StringBuilder();
          ((StringBuilder)localObject6).append("onVisibleForAutofillChangedLocked(): inv= ");
          ((StringBuilder)localObject6).append(this.mInvisibleTrackedIds);
          ((StringBuilder)localObject6).append(" vis=");
          ((StringBuilder)localObject6).append(this.mVisibleTrackedIds);
          Log.v("AutofillManager", ((StringBuilder)localObject6).toString());
        }
        Object localObject6 = this.mInvisibleTrackedIds;
        Object localObject7;
        int i;
        int j;
        if (localObject6 != null)
        {
          localObject6 = new ArrayList((Collection)localObject6);
          localObject7 = ((AutofillManager.AutofillClient)localObject1).autofillClientGetViewVisibility(Helper.toArray((Collection)localObject6));
          i = ((ArrayList)localObject6).size();
          j = 0;
          for (;;)
          {
            localObject2 = localObject3;
            localObject4 = localObject5;
            if (j >= i) {
              break;
            }
            AutofillId localAutofillId = (AutofillId)((ArrayList)localObject6).get(j);
            if (localObject7[j] != 0)
            {
              localObject4 = addToSet((ArraySet)localObject3, localAutofillId);
              localObject3 = localObject4;
              localObject2 = localObject5;
              if (Helper.sDebug)
              {
                localObject2 = new StringBuilder();
                ((StringBuilder)localObject2).append("onVisibleForAutofill() ");
                ((StringBuilder)localObject2).append(localAutofillId);
                ((StringBuilder)localObject2).append(" became visible");
                Log.d("AutofillManager", ((StringBuilder)localObject2).toString());
                localObject3 = localObject4;
                localObject2 = localObject5;
              }
            }
            else
            {
              localObject2 = addToSet((ArraySet)localObject5, localAutofillId);
            }
            j++;
            localObject5 = localObject2;
          }
        }
        localObject6 = this.mVisibleTrackedIds;
        localObject5 = localObject2;
        localObject3 = localObject4;
        if (localObject6 != null)
        {
          localObject6 = new ArrayList((Collection)localObject6);
          localObject1 = ((AutofillManager.AutofillClient)localObject1).autofillClientGetViewVisibility(Helper.toArray((Collection)localObject6));
          i = ((ArrayList)localObject6).size();
          j = 0;
          for (;;)
          {
            localObject5 = localObject2;
            localObject3 = localObject4;
            if (j >= i) {
              break;
            }
            localObject7 = (AutofillId)((ArrayList)localObject6).get(j);
            if (localObject1[j] != 0)
            {
              localObject5 = addToSet((ArraySet)localObject2, localObject7);
            }
            else
            {
              localObject3 = addToSet((ArraySet)localObject4, localObject7);
              localObject5 = localObject2;
              localObject4 = localObject3;
              if (Helper.sDebug)
              {
                localObject4 = new StringBuilder();
                ((StringBuilder)localObject4).append("onVisibleForAutofill() ");
                ((StringBuilder)localObject4).append(localObject7);
                ((StringBuilder)localObject4).append(" became invisible");
                Log.d("AutofillManager", ((StringBuilder)localObject4).toString());
                localObject4 = localObject3;
                localObject5 = localObject2;
              }
            }
            j++;
            localObject2 = localObject5;
          }
        }
        this.mInvisibleTrackedIds = ((ArraySet)localObject3);
        this.mVisibleTrackedIds = ((ArraySet)localObject5);
      }
      if (this.mVisibleTrackedIds == null)
      {
        if (Helper.sVerbose) {
          Log.v("AutofillManager", "onVisibleForAutofillChangedLocked(): no more visible ids");
        }
        AutofillManager.this.finishSessionLocked();
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/autofill/AutofillManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */