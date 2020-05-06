package android.view.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SystemApi;
import android.annotation.UnsupportedAppUsage;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.view.IWindow;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.IntPair;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AccessibilityManager
{
  public static final String ACTION_CHOOSE_ACCESSIBILITY_BUTTON = "com.android.internal.intent.action.CHOOSE_ACCESSIBILITY_BUTTON";
  public static final int AUTOCLICK_DELAY_DEFAULT = 600;
  public static final int DALTONIZER_CORRECT_DEUTERANOMALY = 12;
  public static final int DALTONIZER_DISABLED = -1;
  @UnsupportedAppUsage
  public static final int DALTONIZER_SIMULATE_MONOCHROMACY = 0;
  private static final boolean DEBUG = false;
  public static final int FLAG_CONTENT_CONTROLS = 4;
  public static final int FLAG_CONTENT_ICONS = 1;
  public static final int FLAG_CONTENT_TEXT = 2;
  private static final String LOG_TAG = "AccessibilityManager";
  public static final int STATE_FLAG_ACCESSIBILITY_ENABLED = 1;
  public static final int STATE_FLAG_HIGH_TEXT_CONTRAST_ENABLED = 4;
  public static final int STATE_FLAG_TOUCH_EXPLORATION_ENABLED = 2;
  @UnsupportedAppUsage
  private static AccessibilityManager sInstance;
  @UnsupportedAppUsage
  static final Object sInstanceSync = new Object();
  AccessibilityPolicy mAccessibilityPolicy;
  @UnsupportedAppUsage
  private final ArrayMap<AccessibilityStateChangeListener, Handler> mAccessibilityStateChangeListeners = new ArrayMap();
  final Handler.Callback mCallback = new MyCallback(null);
  private final IAccessibilityManagerClient.Stub mClient = new IAccessibilityManagerClient.Stub()
  {
    public void notifyServicesStateChanged(long paramAnonymousLong)
    {
      AccessibilityManager.this.updateUiTimeout(paramAnonymousLong);
      synchronized (AccessibilityManager.this.mLock)
      {
        if (AccessibilityManager.this.mServicesStateChangeListeners.isEmpty()) {
          return;
        }
        ArrayMap localArrayMap = new android/util/ArrayMap;
        localArrayMap.<init>(AccessibilityManager.this.mServicesStateChangeListeners);
        int i = localArrayMap.size();
        for (int j = 0; j < i; j++)
        {
          ??? = (AccessibilityManager.AccessibilityServicesStateChangeListener)AccessibilityManager.this.mServicesStateChangeListeners.keyAt(j);
          ((Handler)AccessibilityManager.this.mServicesStateChangeListeners.valueAt(j)).post(new _..Lambda.AccessibilityManager.1.o7fCplskH9NlBwJvkl6NoZ0L_BA(this, (AccessibilityManager.AccessibilityServicesStateChangeListener)???));
        }
        return;
      }
    }
    
    public void setRelevantEventTypes(int paramAnonymousInt)
    {
      AccessibilityManager.this.mRelevantEventTypes = paramAnonymousInt;
    }
    
    public void setState(int paramAnonymousInt)
    {
      AccessibilityManager.this.mHandler.obtainMessage(1, paramAnonymousInt, 0).sendToTarget();
    }
  };
  @UnsupportedAppUsage
  final Handler mHandler;
  private final ArrayMap<HighTextContrastChangeListener, Handler> mHighTextContrastStateChangeListeners = new ArrayMap();
  int mInteractiveUiTimeout;
  @UnsupportedAppUsage(maxTargetSdk=28)
  boolean mIsEnabled;
  @UnsupportedAppUsage(trackingBug=123768939L)
  boolean mIsHighTextContrastEnabled;
  boolean mIsTouchExplorationEnabled;
  @UnsupportedAppUsage
  private final Object mLock = new Object();
  int mNonInteractiveUiTimeout;
  int mRelevantEventTypes = -1;
  private SparseArray<List<AccessibilityRequestPreparer>> mRequestPreparerLists;
  @UnsupportedAppUsage
  private IAccessibilityManager mService;
  private final ArrayMap<AccessibilityServicesStateChangeListener, Handler> mServicesStateChangeListeners = new ArrayMap();
  private final ArrayMap<TouchExplorationStateChangeListener, Handler> mTouchExplorationStateChangeListeners = new ArrayMap();
  @UnsupportedAppUsage
  final int mUserId;
  
  public AccessibilityManager(Context arg1, IAccessibilityManager paramIAccessibilityManager, int paramInt)
  {
    this.mHandler = new Handler(???.getMainLooper(), this.mCallback);
    this.mUserId = paramInt;
    synchronized (this.mLock)
    {
      tryConnectToServiceLocked(paramIAccessibilityManager);
      return;
    }
  }
  
  public AccessibilityManager(Handler arg1, IAccessibilityManager paramIAccessibilityManager, int paramInt)
  {
    this.mHandler = ???;
    this.mUserId = paramInt;
    synchronized (this.mLock)
    {
      tryConnectToServiceLocked(paramIAccessibilityManager);
      return;
    }
  }
  
  @UnsupportedAppUsage
  public static AccessibilityManager getInstance(Context paramContext)
  {
    synchronized (sInstanceSync)
    {
      if (sInstance == null)
      {
        int i;
        if ((Binder.getCallingUid() != 1000) && (paramContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS") != 0) && (paramContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0)) {
          i = paramContext.getUserId();
        } else {
          i = -2;
        }
        AccessibilityManager localAccessibilityManager = new android/view/accessibility/AccessibilityManager;
        localAccessibilityManager.<init>(paramContext, null, i);
        sInstance = localAccessibilityManager;
      }
      return sInstance;
    }
  }
  
  private IAccessibilityManager getServiceLocked()
  {
    if (this.mService == null) {
      tryConnectToServiceLocked(null);
    }
    return this.mService;
  }
  
  public static boolean isAccessibilityButtonSupported()
  {
    return Resources.getSystem().getBoolean(17891518);
  }
  
  private void notifyAccessibilityStateChanged()
  {
    synchronized (this.mLock)
    {
      if (this.mAccessibilityStateChangeListeners.isEmpty()) {
        return;
      }
      boolean bool = isEnabled();
      ArrayMap localArrayMap = new android/util/ArrayMap;
      localArrayMap.<init>(this.mAccessibilityStateChangeListeners);
      int i = localArrayMap.size();
      for (int j = 0; j < i; j++)
      {
        ??? = (AccessibilityStateChangeListener)localArrayMap.keyAt(j);
        ((Handler)localArrayMap.valueAt(j)).post(new _..Lambda.AccessibilityManager.yzw5NYY7_MfAQ9gLy3mVllchaXo((AccessibilityStateChangeListener)???, bool));
      }
      return;
    }
  }
  
  private void notifyHighTextContrastStateChanged()
  {
    synchronized (this.mLock)
    {
      if (this.mHighTextContrastStateChangeListeners.isEmpty()) {
        return;
      }
      boolean bool = this.mIsHighTextContrastEnabled;
      ArrayMap localArrayMap = new android/util/ArrayMap;
      localArrayMap.<init>(this.mHighTextContrastStateChangeListeners);
      int i = localArrayMap.size();
      for (int j = 0; j < i; j++)
      {
        ??? = (HighTextContrastChangeListener)localArrayMap.keyAt(j);
        ((Handler)localArrayMap.valueAt(j)).post(new _..Lambda.AccessibilityManager.4M6GrmFiqsRwVzn352N10DcU6RM((HighTextContrastChangeListener)???, bool));
      }
      return;
    }
  }
  
  private void notifyTouchExplorationStateChanged()
  {
    synchronized (this.mLock)
    {
      if (this.mTouchExplorationStateChangeListeners.isEmpty()) {
        return;
      }
      boolean bool = this.mIsTouchExplorationEnabled;
      ArrayMap localArrayMap = new android/util/ArrayMap;
      localArrayMap.<init>(this.mTouchExplorationStateChangeListeners);
      int i = localArrayMap.size();
      for (int j = 0; j < i; j++)
      {
        ??? = (TouchExplorationStateChangeListener)localArrayMap.keyAt(j);
        ((Handler)localArrayMap.valueAt(j)).post(new _..Lambda.AccessibilityManager.a0OtrjOl35tiW2vwyvAmY6_LiLI((TouchExplorationStateChangeListener)???, bool));
      }
      return;
    }
  }
  
  @UnsupportedAppUsage
  private void setStateLocked(int paramInt)
  {
    boolean bool1 = false;
    boolean bool2;
    if ((paramInt & 0x1) != 0) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    boolean bool3;
    if ((paramInt & 0x2) != 0) {
      bool3 = true;
    } else {
      bool3 = false;
    }
    if ((paramInt & 0x4) != 0) {
      bool1 = true;
    }
    boolean bool4 = isEnabled();
    boolean bool5 = this.mIsTouchExplorationEnabled;
    boolean bool6 = this.mIsHighTextContrastEnabled;
    this.mIsEnabled = bool2;
    this.mIsTouchExplorationEnabled = bool3;
    this.mIsHighTextContrastEnabled = bool1;
    if (bool4 != isEnabled()) {
      notifyAccessibilityStateChanged();
    }
    if (bool5 != bool3) {
      notifyTouchExplorationStateChanged();
    }
    if (bool6 != bool1) {
      notifyHighTextContrastStateChanged();
    }
  }
  
  private void tryConnectToServiceLocked(IAccessibilityManager paramIAccessibilityManager)
  {
    IAccessibilityManager localIAccessibilityManager = paramIAccessibilityManager;
    if (paramIAccessibilityManager == null)
    {
      paramIAccessibilityManager = ServiceManager.getService("accessibility");
      if (paramIAccessibilityManager == null) {
        return;
      }
      localIAccessibilityManager = IAccessibilityManager.Stub.asInterface(paramIAccessibilityManager);
    }
    try
    {
      long l = localIAccessibilityManager.addClient(this.mClient, this.mUserId);
      setStateLocked(IntPair.first(l));
      this.mRelevantEventTypes = IntPair.second(l);
      updateUiTimeout(localIAccessibilityManager.getRecommendedTimeoutMillis());
      this.mService = localIAccessibilityManager;
    }
    catch (RemoteException paramIAccessibilityManager)
    {
      Log.e("AccessibilityManager", "AccessibilityManagerService is dead", paramIAccessibilityManager);
    }
  }
  
  private void updateUiTimeout(long paramLong)
  {
    this.mInteractiveUiTimeout = IntPair.first(paramLong);
    this.mNonInteractiveUiTimeout = IntPair.second(paramLong);
  }
  
  public int addAccessibilityInteractionConnection(IWindow paramIWindow, String paramString, IAccessibilityInteractionConnection paramIAccessibilityInteractionConnection)
  {
    synchronized (this.mLock)
    {
      IAccessibilityManager localIAccessibilityManager = getServiceLocked();
      if (localIAccessibilityManager == null) {
        return -1;
      }
      int i = this.mUserId;
      try
      {
        i = localIAccessibilityManager.addAccessibilityInteractionConnection(paramIWindow, paramIAccessibilityInteractionConnection, paramString, i);
        return i;
      }
      catch (RemoteException paramIWindow)
      {
        Log.e("AccessibilityManager", "Error while adding an accessibility interaction connection. ", paramIWindow);
        return -1;
      }
    }
  }
  
  public void addAccessibilityRequestPreparer(AccessibilityRequestPreparer paramAccessibilityRequestPreparer)
  {
    if (this.mRequestPreparerLists == null) {
      this.mRequestPreparerLists = new SparseArray(1);
    }
    int i = paramAccessibilityRequestPreparer.getAccessibilityViewId();
    List localList = (List)this.mRequestPreparerLists.get(i);
    Object localObject = localList;
    if (localList == null)
    {
      localObject = new ArrayList(1);
      this.mRequestPreparerLists.put(i, localObject);
    }
    ((List)localObject).add(paramAccessibilityRequestPreparer);
  }
  
  public void addAccessibilityServicesStateChangeListener(AccessibilityServicesStateChangeListener paramAccessibilityServicesStateChangeListener, Handler paramHandler)
  {
    synchronized (this.mLock)
    {
      ArrayMap localArrayMap = this.mServicesStateChangeListeners;
      if (paramHandler == null) {
        paramHandler = this.mHandler;
      }
      localArrayMap.put(paramAccessibilityServicesStateChangeListener, paramHandler);
      return;
    }
  }
  
  public void addAccessibilityStateChangeListener(AccessibilityStateChangeListener paramAccessibilityStateChangeListener, Handler paramHandler)
  {
    synchronized (this.mLock)
    {
      ArrayMap localArrayMap = this.mAccessibilityStateChangeListeners;
      if (paramHandler == null) {
        paramHandler = this.mHandler;
      }
      localArrayMap.put(paramAccessibilityStateChangeListener, paramHandler);
      return;
    }
  }
  
  public boolean addAccessibilityStateChangeListener(AccessibilityStateChangeListener paramAccessibilityStateChangeListener)
  {
    addAccessibilityStateChangeListener(paramAccessibilityStateChangeListener, null);
    return true;
  }
  
  public void addHighTextContrastStateChangeListener(HighTextContrastChangeListener paramHighTextContrastChangeListener, Handler paramHandler)
  {
    synchronized (this.mLock)
    {
      ArrayMap localArrayMap = this.mHighTextContrastStateChangeListeners;
      if (paramHandler == null) {
        paramHandler = this.mHandler;
      }
      localArrayMap.put(paramHighTextContrastChangeListener, paramHandler);
      return;
    }
  }
  
  public void addTouchExplorationStateChangeListener(TouchExplorationStateChangeListener paramTouchExplorationStateChangeListener, Handler paramHandler)
  {
    synchronized (this.mLock)
    {
      ArrayMap localArrayMap = this.mTouchExplorationStateChangeListeners;
      if (paramHandler == null) {
        paramHandler = this.mHandler;
      }
      localArrayMap.put(paramTouchExplorationStateChangeListener, paramHandler);
      return;
    }
  }
  
  public boolean addTouchExplorationStateChangeListener(TouchExplorationStateChangeListener paramTouchExplorationStateChangeListener)
  {
    addTouchExplorationStateChangeListener(paramTouchExplorationStateChangeListener, null);
    return true;
  }
  
  @Deprecated
  public List<ServiceInfo> getAccessibilityServiceList()
  {
    List localList = getInstalledAccessibilityServiceList();
    ArrayList localArrayList = new ArrayList();
    int i = localList.size();
    for (int j = 0; j < i; j++) {
      localArrayList.add(((AccessibilityServiceInfo)localList.get(j)).getResolveInfo().serviceInfo);
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  public String getAccessibilityShortcutService()
  {
    synchronized (this.mLock)
    {
      IAccessibilityManager localIAccessibilityManager = getServiceLocked();
      if (localIAccessibilityManager != null) {
        try
        {
          ??? = localIAccessibilityManager.getAccessibilityShortcutService();
          return (String)???;
        }
        catch (RemoteException localRemoteException)
        {
          localRemoteException.rethrowFromSystemServer();
        }
      }
      return null;
    }
  }
  
  @SystemApi
  public int getAccessibilityWindowId(IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      return -1;
    }
    synchronized (this.mLock)
    {
      IAccessibilityManager localIAccessibilityManager = getServiceLocked();
      if (localIAccessibilityManager == null) {
        return -1;
      }
      try
      {
        int i = localIAccessibilityManager.getAccessibilityWindowId(paramIBinder);
        return i;
      }
      catch (RemoteException paramIBinder)
      {
        return -1;
      }
    }
  }
  
  @VisibleForTesting
  public Handler.Callback getCallback()
  {
    return this.mCallback;
  }
  
  public IAccessibilityManagerClient getClient()
  {
    return this.mClient;
  }
  
  public List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int paramInt)
  {
    synchronized (this.mLock)
    {
      Object localObject2 = getServiceLocked();
      if (localObject2 == null)
      {
        localObject2 = Collections.emptyList();
        return (List<AccessibilityServiceInfo>)localObject2;
      }
      int i = this.mUserId;
      ??? = null;
      try
      {
        localObject2 = ((IAccessibilityManager)localObject2).getEnabledAccessibilityServiceList(paramInt, i);
        ??? = localObject2;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("AccessibilityManager", "Error while obtaining the installed AccessibilityServices. ", localRemoteException);
      }
      AccessibilityPolicy localAccessibilityPolicy = this.mAccessibilityPolicy;
      Object localObject3 = ???;
      if (localAccessibilityPolicy != null) {
        localObject3 = localAccessibilityPolicy.getEnabledAccessibilityServiceList(paramInt, (List)???);
      }
      if (localObject3 != null) {
        return Collections.unmodifiableList((List)localObject3);
      }
      return Collections.emptyList();
    }
  }
  
  public List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList()
  {
    synchronized (this.mLock)
    {
      Object localObject2 = getServiceLocked();
      if (localObject2 == null)
      {
        localObject2 = Collections.emptyList();
        return (List<AccessibilityServiceInfo>)localObject2;
      }
      int i = this.mUserId;
      ??? = null;
      try
      {
        localObject2 = ((IAccessibilityManager)localObject2).getInstalledAccessibilityServiceList(i);
        ??? = localObject2;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("AccessibilityManager", "Error while obtaining the installed AccessibilityServices. ", localRemoteException);
      }
      AccessibilityPolicy localAccessibilityPolicy = this.mAccessibilityPolicy;
      Object localObject3 = ???;
      if (localAccessibilityPolicy != null) {
        localObject3 = localAccessibilityPolicy.getInstalledAccessibilityServiceList((List)???);
      }
      if (localObject3 != null) {
        return Collections.unmodifiableList((List)localObject3);
      }
      return Collections.emptyList();
    }
  }
  
  public AccessibilityServiceInfo getInstalledServiceInfoWithComponentName(ComponentName paramComponentName)
  {
    List localList = getInstalledAccessibilityServiceList();
    if ((localList != null) && (paramComponentName != null))
    {
      for (int i = 0; i < localList.size(); i++) {
        if (paramComponentName.equals(((AccessibilityServiceInfo)localList.get(i)).getComponentName())) {
          return (AccessibilityServiceInfo)localList.get(i);
        }
      }
      return null;
    }
    return null;
  }
  
  public int getRecommendedTimeoutMillis(int paramInt1, int paramInt2)
  {
    int i = 0;
    int j;
    if ((paramInt2 & 0x4) != 0) {
      j = 1;
    } else {
      j = 0;
    }
    if (((paramInt2 & 0x1) != 0) || ((paramInt2 & 0x2) != 0)) {
      i = 1;
    }
    paramInt2 = paramInt1;
    paramInt1 = paramInt2;
    if (j != 0) {
      paramInt1 = Math.max(paramInt2, this.mInteractiveUiTimeout);
    }
    paramInt2 = paramInt1;
    if (i != 0) {
      paramInt2 = Math.max(paramInt1, this.mNonInteractiveUiTimeout);
    }
    return paramInt2;
  }
  
  public List<AccessibilityRequestPreparer> getRequestPreparersForAccessibilityId(int paramInt)
  {
    SparseArray localSparseArray = this.mRequestPreparerLists;
    if (localSparseArray == null) {
      return null;
    }
    return (List)localSparseArray.get(paramInt);
  }
  
  public void interrupt()
  {
    synchronized (this.mLock)
    {
      Object localObject2 = getServiceLocked();
      if (localObject2 == null) {
        return;
      }
      if (!isEnabled())
      {
        if (Looper.myLooper() != Looper.getMainLooper())
        {
          Log.e("AccessibilityManager", "Interrupt called with accessibility disabled");
          return;
        }
        localObject2 = new java/lang/IllegalStateException;
        ((IllegalStateException)localObject2).<init>("Accessibility off. Did you forget to check that?");
        throw ((Throwable)localObject2);
      }
      int i = this.mUserId;
      try
      {
        ((IAccessibilityManager)localObject2).interrupt(i);
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("AccessibilityManager", "Error while requesting interrupt from all services. ", localRemoteException);
      }
      return;
    }
  }
  
  public boolean isAccessibilityVolumeStreamActive()
  {
    List localList = getEnabledAccessibilityServiceList(-1);
    for (int i = 0; i < localList.size(); i++) {
      if ((((AccessibilityServiceInfo)localList.get(i)).flags & 0x80) != 0) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isEnabled()
  {
    synchronized (this.mLock)
    {
      boolean bool;
      if ((!this.mIsEnabled) && ((this.mAccessibilityPolicy == null) || (!this.mAccessibilityPolicy.isEnabled(this.mIsEnabled)))) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
  }
  
  @UnsupportedAppUsage
  public boolean isHighTextContrastEnabled()
  {
    synchronized (this.mLock)
    {
      if (getServiceLocked() == null) {
        return false;
      }
      boolean bool = this.mIsHighTextContrastEnabled;
      return bool;
    }
  }
  
  public boolean isTouchExplorationEnabled()
  {
    synchronized (this.mLock)
    {
      if (getServiceLocked() == null) {
        return false;
      }
      boolean bool = this.mIsTouchExplorationEnabled;
      return bool;
    }
  }
  
  public void notifyAccessibilityButtonClicked(int paramInt)
  {
    synchronized (this.mLock)
    {
      IAccessibilityManager localIAccessibilityManager = getServiceLocked();
      if (localIAccessibilityManager == null) {
        return;
      }
      try
      {
        localIAccessibilityManager.notifyAccessibilityButtonClicked(paramInt);
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("AccessibilityManager", "Error while dispatching accessibility button click", localRemoteException);
      }
      return;
    }
  }
  
  public void notifyAccessibilityButtonVisibilityChanged(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      IAccessibilityManager localIAccessibilityManager = getServiceLocked();
      if (localIAccessibilityManager == null) {
        return;
      }
      try
      {
        localIAccessibilityManager.notifyAccessibilityButtonVisibilityChanged(paramBoolean);
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("AccessibilityManager", "Error while dispatching accessibility button visibility change", localRemoteException);
      }
      return;
    }
  }
  
  @SystemApi
  public void performAccessibilityShortcut()
  {
    synchronized (this.mLock)
    {
      IAccessibilityManager localIAccessibilityManager = getServiceLocked();
      if (localIAccessibilityManager == null) {
        return;
      }
      try
      {
        localIAccessibilityManager.performAccessibilityShortcut();
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("AccessibilityManager", "Error performing accessibility shortcut. ", localRemoteException);
      }
      return;
    }
  }
  
  public void removeAccessibilityInteractionConnection(IWindow paramIWindow)
  {
    synchronized (this.mLock)
    {
      IAccessibilityManager localIAccessibilityManager = getServiceLocked();
      if (localIAccessibilityManager == null) {
        return;
      }
      try
      {
        localIAccessibilityManager.removeAccessibilityInteractionConnection(paramIWindow);
      }
      catch (RemoteException paramIWindow)
      {
        Log.e("AccessibilityManager", "Error while removing an accessibility interaction connection. ", paramIWindow);
      }
      return;
    }
  }
  
  public void removeAccessibilityRequestPreparer(AccessibilityRequestPreparer paramAccessibilityRequestPreparer)
  {
    if (this.mRequestPreparerLists == null) {
      return;
    }
    int i = paramAccessibilityRequestPreparer.getAccessibilityViewId();
    List localList = (List)this.mRequestPreparerLists.get(i);
    if (localList != null)
    {
      localList.remove(paramAccessibilityRequestPreparer);
      if (localList.isEmpty()) {
        this.mRequestPreparerLists.remove(i);
      }
    }
  }
  
  public void removeAccessibilityServicesStateChangeListener(AccessibilityServicesStateChangeListener paramAccessibilityServicesStateChangeListener)
  {
    synchronized (this.mLock)
    {
      this.mServicesStateChangeListeners.remove(paramAccessibilityServicesStateChangeListener);
      return;
    }
  }
  
  public boolean removeAccessibilityStateChangeListener(AccessibilityStateChangeListener paramAccessibilityStateChangeListener)
  {
    synchronized (this.mLock)
    {
      int i = this.mAccessibilityStateChangeListeners.indexOfKey(paramAccessibilityStateChangeListener);
      this.mAccessibilityStateChangeListeners.remove(paramAccessibilityStateChangeListener);
      boolean bool;
      if (i >= 0) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
  }
  
  public void removeHighTextContrastStateChangeListener(HighTextContrastChangeListener paramHighTextContrastChangeListener)
  {
    synchronized (this.mLock)
    {
      this.mHighTextContrastStateChangeListeners.remove(paramHighTextContrastChangeListener);
      return;
    }
  }
  
  public boolean removeTouchExplorationStateChangeListener(TouchExplorationStateChangeListener paramTouchExplorationStateChangeListener)
  {
    synchronized (this.mLock)
    {
      int i = this.mTouchExplorationStateChangeListeners.indexOfKey(paramTouchExplorationStateChangeListener);
      this.mTouchExplorationStateChangeListeners.remove(paramTouchExplorationStateChangeListener);
      boolean bool;
      if (i >= 0) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
  }
  
  /* Error */
  public void sendAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 105	android/view/accessibility/AccessibilityManager:mLock	Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: aload_0
    //   8: invokespecial 322	android/view/accessibility/AccessibilityManager:getServiceLocked	()Landroid/view/accessibility/IAccessibilityManager;
    //   11: astore_3
    //   12: aload_3
    //   13: ifnonnull +6 -> 19
    //   16: aload_2
    //   17: monitorexit
    //   18: return
    //   19: aload_1
    //   20: invokestatic 543	android/os/SystemClock:uptimeMillis	()J
    //   23: invokevirtual 548	android/view/accessibility/AccessibilityEvent:setEventTime	(J)V
    //   26: aload_0
    //   27: getfield 433	android/view/accessibility/AccessibilityManager:mAccessibilityPolicy	Landroid/view/accessibility/AccessibilityManager$AccessibilityPolicy;
    //   30: ifnull +35 -> 65
    //   33: aload_0
    //   34: getfield 433	android/view/accessibility/AccessibilityManager:mAccessibilityPolicy	Landroid/view/accessibility/AccessibilityManager$AccessibilityPolicy;
    //   37: aload_1
    //   38: aload_0
    //   39: getfield 265	android/view/accessibility/AccessibilityManager:mIsEnabled	Z
    //   42: aload_0
    //   43: getfield 107	android/view/accessibility/AccessibilityManager:mRelevantEventTypes	I
    //   46: invokeinterface 552 4 0
    //   51: astore 4
    //   53: aload 4
    //   55: astore 5
    //   57: aload 4
    //   59: ifnonnull +9 -> 68
    //   62: aload_2
    //   63: monitorexit
    //   64: return
    //   65: aload_1
    //   66: astore 5
    //   68: aload_0
    //   69: invokevirtual 228	android/view/accessibility/AccessibilityManager:isEnabled	()Z
    //   72: ifne +37 -> 109
    //   75: invokestatic 467	android/os/Looper:myLooper	()Landroid/os/Looper;
    //   78: invokestatic 468	android/os/Looper:getMainLooper	()Landroid/os/Looper;
    //   81: if_acmpeq +15 -> 96
    //   84: ldc 52
    //   86: ldc_w 554
    //   89: invokestatic 473	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   92: pop
    //   93: aload_2
    //   94: monitorexit
    //   95: return
    //   96: new 475	java/lang/IllegalStateException
    //   99: astore_1
    //   100: aload_1
    //   101: ldc_w 477
    //   104: invokespecial 480	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   107: aload_1
    //   108: athrow
    //   109: aload 5
    //   111: invokevirtual 557	android/view/accessibility/AccessibilityEvent:getEventType	()I
    //   114: aload_0
    //   115: getfield 107	android/view/accessibility/AccessibilityManager:mRelevantEventTypes	I
    //   118: iand
    //   119: ifne +6 -> 125
    //   122: aload_2
    //   123: monitorexit
    //   124: return
    //   125: aload_0
    //   126: getfield 143	android/view/accessibility/AccessibilityManager:mUserId	I
    //   129: istore 6
    //   131: aload_2
    //   132: monitorexit
    //   133: invokestatic 560	android/os/Binder:clearCallingIdentity	()J
    //   136: lstore 7
    //   138: aload_3
    //   139: aload 5
    //   141: iload 6
    //   143: invokeinterface 563 3 0
    //   148: lload 7
    //   150: invokestatic 566	android/os/Binder:restoreCallingIdentity	(J)V
    //   153: aload_1
    //   154: aload 5
    //   156: if_acmpeq +7 -> 163
    //   159: aload_1
    //   160: invokevirtual 569	android/view/accessibility/AccessibilityEvent:recycle	()V
    //   163: aload 5
    //   165: invokevirtual 569	android/view/accessibility/AccessibilityEvent:recycle	()V
    //   168: goto +72 -> 240
    //   171: astore 4
    //   173: lload 7
    //   175: invokestatic 566	android/os/Binder:restoreCallingIdentity	(J)V
    //   178: aload 4
    //   180: athrow
    //   181: astore 4
    //   183: goto +58 -> 241
    //   186: astore 4
    //   188: new 571	java/lang/StringBuilder
    //   191: astore_2
    //   192: aload_2
    //   193: invokespecial 572	java/lang/StringBuilder:<init>	()V
    //   196: aload_2
    //   197: ldc_w 574
    //   200: invokevirtual 578	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   203: pop
    //   204: aload_2
    //   205: aload 5
    //   207: invokevirtual 581	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   210: pop
    //   211: aload_2
    //   212: ldc_w 583
    //   215: invokevirtual 578	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   218: pop
    //   219: ldc 52
    //   221: aload_2
    //   222: invokevirtual 586	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   225: aload 4
    //   227: invokestatic 314	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   230: pop
    //   231: aload_1
    //   232: aload 5
    //   234: if_acmpeq -71 -> 163
    //   237: goto -78 -> 159
    //   240: return
    //   241: aload_1
    //   242: aload 5
    //   244: if_acmpeq +7 -> 251
    //   247: aload_1
    //   248: invokevirtual 569	android/view/accessibility/AccessibilityEvent:recycle	()V
    //   251: aload 5
    //   253: invokevirtual 569	android/view/accessibility/AccessibilityEvent:recycle	()V
    //   256: aload 4
    //   258: athrow
    //   259: astore_1
    //   260: aload_2
    //   261: monitorexit
    //   262: aload_1
    //   263: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	264	0	this	AccessibilityManager
    //   0	264	1	paramAccessibilityEvent	AccessibilityEvent
    //   4	257	2	localObject1	Object
    //   11	128	3	localIAccessibilityManager	IAccessibilityManager
    //   51	7	4	localAccessibilityEvent1	AccessibilityEvent
    //   171	8	4	localObject2	Object
    //   181	1	4	localObject3	Object
    //   186	71	4	localRemoteException	RemoteException
    //   55	197	5	localAccessibilityEvent2	AccessibilityEvent
    //   129	13	6	i	int
    //   136	38	7	l	long
    // Exception table:
    //   from	to	target	type
    //   138	148	171	finally
    //   133	138	181	finally
    //   148	153	181	finally
    //   173	181	181	finally
    //   188	231	181	finally
    //   133	138	186	android/os/RemoteException
    //   148	153	186	android/os/RemoteException
    //   173	181	186	android/os/RemoteException
    //   7	12	259	finally
    //   16	18	259	finally
    //   19	53	259	finally
    //   62	64	259	finally
    //   68	95	259	finally
    //   96	109	259	finally
    //   109	124	259	finally
    //   125	133	259	finally
    //   260	262	259	finally
  }
  
  public boolean sendFingerprintGesture(int paramInt)
  {
    synchronized (this.mLock)
    {
      IAccessibilityManager localIAccessibilityManager = getServiceLocked();
      if (localIAccessibilityManager == null) {
        return false;
      }
      try
      {
        boolean bool = localIAccessibilityManager.sendFingerprintGesture(paramInt);
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        return false;
      }
    }
  }
  
  public void setAccessibilityPolicy(AccessibilityPolicy paramAccessibilityPolicy)
  {
    synchronized (this.mLock)
    {
      this.mAccessibilityPolicy = paramAccessibilityPolicy;
      return;
    }
  }
  
  public void setPictureInPictureActionReplacingConnection(IAccessibilityInteractionConnection paramIAccessibilityInteractionConnection)
  {
    synchronized (this.mLock)
    {
      IAccessibilityManager localIAccessibilityManager = getServiceLocked();
      if (localIAccessibilityManager == null) {
        return;
      }
      try
      {
        localIAccessibilityManager.setPictureInPictureActionReplacingConnection(paramIAccessibilityInteractionConnection);
      }
      catch (RemoteException paramIAccessibilityInteractionConnection)
      {
        Log.e("AccessibilityManager", "Error setting picture in picture action replacement", paramIAccessibilityInteractionConnection);
      }
      return;
    }
  }
  
  public static abstract interface AccessibilityPolicy
  {
    public abstract List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int paramInt, List<AccessibilityServiceInfo> paramList);
    
    public abstract List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(List<AccessibilityServiceInfo> paramList);
    
    public abstract int getRelevantEventTypes(int paramInt);
    
    public abstract boolean isEnabled(boolean paramBoolean);
    
    public abstract AccessibilityEvent onAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent, boolean paramBoolean, int paramInt);
  }
  
  public static abstract interface AccessibilityServicesStateChangeListener
  {
    public abstract void onAccessibilityServicesStateChanged(AccessibilityManager paramAccessibilityManager);
  }
  
  public static abstract interface AccessibilityStateChangeListener
  {
    public abstract void onAccessibilityStateChanged(boolean paramBoolean);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ContentFlag {}
  
  public static abstract interface HighTextContrastChangeListener
  {
    public abstract void onHighTextContrastStateChanged(boolean paramBoolean);
  }
  
  private final class MyCallback
    implements Handler.Callback
  {
    public static final int MSG_SET_STATE = 1;
    
    private MyCallback() {}
    
    public boolean handleMessage(Message paramMessage)
    {
      int i;
      if (paramMessage.what == 1) {
        i = paramMessage.arg1;
      }
      synchronized (AccessibilityManager.this.mLock)
      {
        AccessibilityManager.this.setStateLocked(i);
        return true;
      }
    }
  }
  
  public static abstract interface TouchExplorationStateChangeListener
  {
    public abstract void onTouchExplorationStateChanged(boolean paramBoolean);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/accessibility/AccessibilityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */