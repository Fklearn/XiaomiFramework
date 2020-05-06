package android.view.inputmethod;

import android.annotation.UnsupportedAppUsage;
import android.app.ActivityThread;
import android.app.Application;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.Trace;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.text.style.SuggestionSpan;
import android.util.Log;
import android.util.Pools.Pool;
import android.util.Pools.SimplePool;
import android.util.PrintWriterPrinter;
import android.util.Printer;
import android.util.SparseArray;
import android.view.ImeInsetsSourceConsumer;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventSender;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.autofill.AutofillManager;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.inputmethod.InputMethodPrivilegedOperations;
import com.android.internal.inputmethod.InputMethodPrivilegedOperationsRegistry;
import com.android.internal.os.SomeArgs;
import com.android.internal.view.IInputConnectionWrapper;
import com.android.internal.view.IInputContext;
import com.android.internal.view.IInputMethodClient;
import com.android.internal.view.IInputMethodClient.Stub;
import com.android.internal.view.IInputMethodManager;
import com.android.internal.view.IInputMethodSession;
import com.android.internal.view.InputBindResult;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class InputMethodManager
{
  static final boolean DEBUG = false;
  public static final int DISPATCH_HANDLED = 1;
  public static final int DISPATCH_IN_PROGRESS = -1;
  public static final int DISPATCH_NOT_HANDLED = 0;
  public static final int HIDE_IMPLICIT_ONLY = 1;
  public static final int HIDE_NOT_ALWAYS = 2;
  static final long INPUT_METHOD_NOT_RESPONDING_TIMEOUT = 2500L;
  static final int MSG_APPLY_IME_VISIBILITY = 20;
  static final int MSG_BIND = 2;
  static final int MSG_DUMP = 1;
  static final int MSG_FLUSH_INPUT_EVENT = 7;
  static final int MSG_REPORT_FULLSCREEN_MODE = 10;
  static final int MSG_REPORT_PRE_RENDERED = 15;
  static final int MSG_SEND_INPUT_EVENT = 5;
  static final int MSG_SET_ACTIVE = 4;
  static final int MSG_TIMEOUT_INPUT_EVENT = 6;
  static final int MSG_UNBIND = 3;
  static final int MSG_UPDATE_ACTIVITY_VIEW_TO_SCREEN_MATRIX = 30;
  private static final int NOT_A_SUBTYPE_ID = -1;
  static final String PENDING_EVENT_COUNTER = "aq:imm";
  private static final int REQUEST_UPDATE_CURSOR_ANCHOR_INFO_NONE = 0;
  public static final int RESULT_HIDDEN = 3;
  public static final int RESULT_SHOWN = 2;
  public static final int RESULT_UNCHANGED_HIDDEN = 1;
  public static final int RESULT_UNCHANGED_SHOWN = 0;
  public static final int SHOW_FORCED = 2;
  public static final int SHOW_IMPLICIT = 1;
  public static final int SHOW_IM_PICKER_MODE_AUTO = 0;
  public static final int SHOW_IM_PICKER_MODE_EXCLUDE_AUXILIARY_SUBTYPES = 2;
  public static final int SHOW_IM_PICKER_MODE_INCLUDE_AUXILIARY_SUBTYPES = 1;
  private static final String SUBTYPE_MODE_VOICE = "voice";
  static final String TAG = "InputMethodManager";
  @Deprecated
  @UnsupportedAppUsage
  @GuardedBy({"sLock"})
  static InputMethodManager sInstance;
  @GuardedBy({"sLock"})
  private static final SparseArray<InputMethodManager> sInstanceMap = new SparseArray();
  private static final Object sLock = new Object();
  boolean mActive = false;
  private Matrix mActivityViewToScreenMatrix = null;
  int mBindSequence = -1;
  final IInputMethodClient.Stub mClient = new IInputMethodClient.Stub()
  {
    public void applyImeVisibility(boolean paramAnonymousBoolean)
    {
      InputMethodManager.this.mH.obtainMessage(20, paramAnonymousBoolean, 0).sendToTarget();
    }
    
    protected void dump(FileDescriptor paramAnonymousFileDescriptor, PrintWriter paramAnonymousPrintWriter, String[] paramAnonymousArrayOfString)
    {
      CountDownLatch localCountDownLatch = new CountDownLatch(1);
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousFileDescriptor;
      localSomeArgs.arg2 = paramAnonymousPrintWriter;
      localSomeArgs.arg3 = paramAnonymousArrayOfString;
      localSomeArgs.arg4 = localCountDownLatch;
      InputMethodManager.this.mH.sendMessage(InputMethodManager.this.mH.obtainMessage(1, localSomeArgs));
      try
      {
        if (!localCountDownLatch.await(5L, TimeUnit.SECONDS)) {
          paramAnonymousPrintWriter.println("Timeout waiting for dump");
        }
      }
      catch (InterruptedException paramAnonymousFileDescriptor)
      {
        paramAnonymousPrintWriter.println("Interrupted waiting for dump");
      }
    }
    
    public void onBindMethod(InputBindResult paramAnonymousInputBindResult)
    {
      InputMethodManager.this.mH.obtainMessage(2, paramAnonymousInputBindResult).sendToTarget();
    }
    
    public void onUnbindMethod(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      InputMethodManager.this.mH.obtainMessage(3, paramAnonymousInt1, paramAnonymousInt2).sendToTarget();
    }
    
    public void reportFullscreenMode(boolean paramAnonymousBoolean)
    {
      InputMethodManager.this.mH.obtainMessage(10, paramAnonymousBoolean, 0).sendToTarget();
    }
    
    public void reportPreRendered(EditorInfo paramAnonymousEditorInfo)
    {
      InputMethodManager.this.mH.obtainMessage(15, 0, 0, paramAnonymousEditorInfo).sendToTarget();
    }
    
    public void setActive(boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
    {
      InputMethodManager.this.mH.obtainMessage(4, paramAnonymousBoolean1, paramAnonymousBoolean2).sendToTarget();
    }
    
    public void updateActivityViewToScreenMatrix(int paramAnonymousInt, float[] paramAnonymousArrayOfFloat)
    {
      InputMethodManager.this.mH.obtainMessage(30, paramAnonymousInt, 0, paramAnonymousArrayOfFloat).sendToTarget();
    }
  };
  CompletionInfo[] mCompletions;
  InputChannel mCurChannel;
  @UnsupportedAppUsage
  String mCurId;
  @UnsupportedAppUsage
  IInputMethodSession mCurMethod;
  @UnsupportedAppUsage
  View mCurRootView;
  ImeInputEventSender mCurSender;
  EditorInfo mCurrentTextBoxAttribute;
  private CursorAnchorInfo mCursorAnchorInfo = null;
  int mCursorCandEnd;
  int mCursorCandStart;
  @UnsupportedAppUsage
  Rect mCursorRect = new Rect();
  int mCursorSelEnd;
  int mCursorSelStart;
  private final int mDisplayId;
  final InputConnection mDummyInputConnection = new BaseInputConnection(this, false);
  boolean mFullscreenMode;
  @UnsupportedAppUsage(maxTargetSdk=28)
  final H mH;
  final IInputContext mIInputContext;
  private ImeInsetsSourceConsumer mImeInsetsConsumer;
  final Looper mMainLooper;
  @UnsupportedAppUsage(maxTargetSdk=28)
  View mNextServedView;
  final Pools.Pool<PendingEvent> mPendingEventPool = new Pools.SimplePool(20);
  final SparseArray<PendingEvent> mPendingEvents = new SparseArray(20);
  private int mRequestUpdateCursorAnchorInfoMonitorMode = 0;
  boolean mRestartOnNextWindowFocus = true;
  boolean mServedConnecting;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  ControlledInputConnectionWrapper mServedInputConnectionWrapper;
  @UnsupportedAppUsage(maxTargetSdk=28)
  View mServedView;
  @UnsupportedAppUsage
  final IInputMethodManager mService;
  @UnsupportedAppUsage
  Rect mTmpCursorRect = new Rect();
  
  private InputMethodManager(IInputMethodManager paramIInputMethodManager, int paramInt, Looper paramLooper)
  {
    this.mService = paramIInputMethodManager;
    this.mMainLooper = paramLooper;
    this.mH = new H(paramLooper);
    this.mDisplayId = paramInt;
    this.mIInputContext = new ControlledInputConnectionWrapper(paramLooper, this.mDummyInputConnection, this);
  }
  
  private static boolean canStartInput(View paramView)
  {
    boolean bool;
    if ((!paramView.hasWindowFocus()) && (!isAutofillUIShowing(paramView))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private boolean checkFocusNoStartInput(boolean paramBoolean)
  {
    if ((this.mServedView == this.mNextServedView) && (!paramBoolean)) {
      return false;
    }
    synchronized (this.mH)
    {
      if ((this.mServedView == this.mNextServedView) && (!paramBoolean)) {
        return false;
      }
      if (this.mNextServedView == null)
      {
        finishInputLocked();
        closeCurrentInput();
        return false;
      }
      ControlledInputConnectionWrapper localControlledInputConnectionWrapper = this.mServedInputConnectionWrapper;
      this.mServedView = this.mNextServedView;
      this.mCurrentTextBoxAttribute = null;
      this.mCompletions = null;
      this.mServedConnecting = true;
      if (!this.mServedView.onCheckIsTextEditor()) {
        maybeCallServedViewChangedLocked(null);
      }
      if (localControlledInputConnectionWrapper != null) {
        localControlledInputConnectionWrapper.finishComposingText();
      }
      return true;
    }
  }
  
  private static InputMethodManager createInstance(int paramInt, Looper paramLooper)
  {
    if (isInEditMode()) {
      paramLooper = createStubInstance(paramInt, paramLooper);
    } else {
      paramLooper = createRealInstance(paramInt, paramLooper);
    }
    return paramLooper;
  }
  
  /* Error */
  private static InputMethodManager createRealInstance(int paramInt, Looper paramLooper)
  {
    // Byte code:
    //   0: ldc_w 304
    //   3: invokestatic 310	android/os/ServiceManager:getServiceOrThrow	(Ljava/lang/String;)Landroid/os/IBinder;
    //   6: invokestatic 316	com/android/internal/view/IInputMethodManager$Stub:asInterface	(Landroid/os/IBinder;)Lcom/android/internal/view/IInputMethodManager;
    //   9: astore_2
    //   10: new 2	android/view/inputmethod/InputMethodManager
    //   13: dup
    //   14: aload_2
    //   15: iload_0
    //   16: aload_1
    //   17: invokespecial 318	android/view/inputmethod/InputMethodManager:<init>	(Lcom/android/internal/view/IInputMethodManager;ILandroid/os/Looper;)V
    //   20: astore_1
    //   21: invokestatic 324	android/os/Binder:clearCallingIdentity	()J
    //   24: lstore_3
    //   25: aload_2
    //   26: aload_1
    //   27: getfield 196	android/view/inputmethod/InputMethodManager:mClient	Lcom/android/internal/view/IInputMethodClient$Stub;
    //   30: aload_1
    //   31: getfield 219	android/view/inputmethod/InputMethodManager:mIInputContext	Lcom/android/internal/view/IInputContext;
    //   34: iload_0
    //   35: invokeinterface 330 4 0
    //   40: goto +13 -> 53
    //   43: astore_1
    //   44: goto +15 -> 59
    //   47: astore_2
    //   48: aload_2
    //   49: invokevirtual 334	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   52: pop
    //   53: lload_3
    //   54: invokestatic 338	android/os/Binder:restoreCallingIdentity	(J)V
    //   57: aload_1
    //   58: areturn
    //   59: lload_3
    //   60: invokestatic 338	android/os/Binder:restoreCallingIdentity	(J)V
    //   63: aload_1
    //   64: athrow
    //   65: astore_1
    //   66: new 340	java/lang/IllegalStateException
    //   69: dup
    //   70: aload_1
    //   71: invokespecial 343	java/lang/IllegalStateException:<init>	(Ljava/lang/Throwable;)V
    //   74: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	75	0	paramInt	int
    //   0	75	1	paramLooper	Looper
    //   9	17	2	localIInputMethodManager	IInputMethodManager
    //   47	2	2	localRemoteException	RemoteException
    //   24	36	3	l	long
    // Exception table:
    //   from	to	target	type
    //   25	40	43	finally
    //   48	53	43	finally
    //   25	40	47	android/os/RemoteException
    //   0	10	65	android/os/ServiceManager$ServiceNotFoundException
  }
  
  private static InputMethodManager createStubInstance(int paramInt, Looper paramLooper)
  {
    ClassLoader localClassLoader = IInputMethodManager.class.getClassLoader();
    -..Lambda.InputMethodManager.iDWn3IGSUFqIcs8Py42UhfrshxI localiDWn3IGSUFqIcs8Py42UhfrshxI = _..Lambda.InputMethodManager.iDWn3IGSUFqIcs8Py42UhfrshxI.INSTANCE;
    return new InputMethodManager((IInputMethodManager)Proxy.newProxyInstance(localClassLoader, new Class[] { IInputMethodManager.class }, localiDWn3IGSUFqIcs8Py42UhfrshxI), paramInt, paramLooper);
  }
  
  private static String dumpViewInfo(View paramView)
  {
    if (paramView == null) {
      return "null";
    }
    StringBuilder localStringBuilder1 = new StringBuilder();
    localStringBuilder1.append(paramView);
    StringBuilder localStringBuilder2 = new StringBuilder();
    localStringBuilder2.append(",focus=");
    localStringBuilder2.append(paramView.hasFocus());
    localStringBuilder1.append(localStringBuilder2.toString());
    localStringBuilder2 = new StringBuilder();
    localStringBuilder2.append(",windowFocus=");
    localStringBuilder2.append(paramView.hasWindowFocus());
    localStringBuilder1.append(localStringBuilder2.toString());
    localStringBuilder2 = new StringBuilder();
    localStringBuilder2.append(",autofillUiShowing=");
    localStringBuilder2.append(isAutofillUIShowing(paramView));
    localStringBuilder1.append(localStringBuilder2.toString());
    localStringBuilder2 = new StringBuilder();
    localStringBuilder2.append(",window=");
    localStringBuilder2.append(paramView.getWindowToken());
    localStringBuilder1.append(localStringBuilder2.toString());
    localStringBuilder2 = new StringBuilder();
    localStringBuilder2.append(",displayId=");
    localStringBuilder2.append(paramView.getContext().getDisplayId());
    localStringBuilder1.append(localStringBuilder2.toString());
    localStringBuilder2 = new StringBuilder();
    localStringBuilder2.append(",temporaryDetach=");
    localStringBuilder2.append(paramView.isTemporarilyDetached());
    localStringBuilder1.append(localStringBuilder2.toString());
    return localStringBuilder1.toString();
  }
  
  public static void ensureDefaultInstanceForDefaultDisplayIfNecessary()
  {
    forContextInternal(0, Looper.getMainLooper());
  }
  
  private void flushPendingEventsLocked()
  {
    this.mH.removeMessages(7);
    int i = this.mPendingEvents.size();
    for (int j = 0; j < i; j++)
    {
      int k = this.mPendingEvents.keyAt(j);
      Message localMessage = this.mH.obtainMessage(7, k, 0);
      localMessage.setAsynchronous(true);
      localMessage.sendToTarget();
    }
  }
  
  public static InputMethodManager forContext(Context paramContext)
  {
    int i = paramContext.getDisplayId();
    if (i == 0) {
      paramContext = Looper.getMainLooper();
    } else {
      paramContext = paramContext.getMainLooper();
    }
    return forContextInternal(i, paramContext);
  }
  
  private static InputMethodManager forContextInternal(int paramInt, Looper paramLooper)
  {
    int i;
    if (paramInt == 0) {
      i = 1;
    } else {
      i = 0;
    }
    synchronized (sLock)
    {
      InputMethodManager localInputMethodManager = (InputMethodManager)sInstanceMap.get(paramInt);
      if (localInputMethodManager != null) {
        return localInputMethodManager;
      }
      paramLooper = createInstance(paramInt, paramLooper);
      if ((sInstance == null) && (i != 0)) {
        sInstance = paramLooper;
      }
      sInstanceMap.put(paramInt, paramLooper);
      return paramLooper;
    }
  }
  
  private InputMethodManager getFallbackInputMethodManagerIfNecessary(View paramView)
  {
    if (paramView == null) {
      return null;
    }
    Object localObject = paramView.getViewRootImpl();
    if (localObject == null) {
      return null;
    }
    int i = ((ViewRootImpl)localObject).getDisplayId();
    if (i == this.mDisplayId) {
      return null;
    }
    localObject = (InputMethodManager)((ViewRootImpl)localObject).mContext.getSystemService(InputMethodManager.class);
    if (localObject == null)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("b/117267690: Failed to get non-null fallback IMM. view=");
      ((StringBuilder)localObject).append(paramView);
      Log.e("InputMethodManager", ((StringBuilder)localObject).toString());
      return null;
    }
    if (((InputMethodManager)localObject).mDisplayId != i)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("b/117267690: Failed to get fallback IMM with expected displayId=");
      localStringBuilder.append(i);
      localStringBuilder.append(" actual IMM#displayId=");
      localStringBuilder.append(((InputMethodManager)localObject).mDisplayId);
      localStringBuilder.append(" view=");
      localStringBuilder.append(paramView);
      Log.e("InputMethodManager", localStringBuilder.toString());
      return null;
    }
    paramView = new StringBuilder();
    paramView.append("b/117267690: Display ID mismatch found. ViewRootImpl displayId=");
    paramView.append(i);
    paramView.append(" InputMethodManager displayId=");
    paramView.append(this.mDisplayId);
    paramView.append(". Use the right InputMethodManager instance to avoid performance overhead.");
    Log.w("InputMethodManager", paramView.toString(), new Throwable());
    return (InputMethodManager)localObject;
  }
  
  @Deprecated
  @UnsupportedAppUsage
  public static InputMethodManager getInstance()
  {
    Log.w("InputMethodManager", "InputMethodManager.getInstance() is deprecated because it cannot be compatible with multi-display. Use context.getSystemService(InputMethodManager.class) instead.", new Throwable());
    ensureDefaultInstanceForDefaultDisplayIfNecessary();
    return peekInstance();
  }
  
  private static boolean isAutofillUIShowing(View paramView)
  {
    paramView = (AutofillManager)paramView.getContext().getSystemService(AutofillManager.class);
    boolean bool;
    if ((paramView != null) && (paramView.isAutofillUiShowing())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private static boolean isInEditMode()
  {
    return false;
  }
  
  private void maybeCallServedViewChangedLocked(EditorInfo paramEditorInfo)
  {
    ImeInsetsSourceConsumer localImeInsetsSourceConsumer = this.mImeInsetsConsumer;
    if (localImeInsetsSourceConsumer != null) {
      localImeInsetsSourceConsumer.onServedEditorChanged(paramEditorInfo);
    }
  }
  
  private PendingEvent obtainPendingEventLocked(InputEvent paramInputEvent, Object paramObject, String paramString, FinishedInputEventCallback paramFinishedInputEventCallback, Handler paramHandler)
  {
    PendingEvent localPendingEvent1 = (PendingEvent)this.mPendingEventPool.acquire();
    PendingEvent localPendingEvent2 = localPendingEvent1;
    if (localPendingEvent1 == null) {
      localPendingEvent2 = new PendingEvent(null);
    }
    localPendingEvent2.mEvent = paramInputEvent;
    localPendingEvent2.mToken = paramObject;
    localPendingEvent2.mInputMethodId = paramString;
    localPendingEvent2.mCallback = paramFinishedInputEventCallback;
    localPendingEvent2.mHandler = paramHandler;
    return localPendingEvent2;
  }
  
  @Deprecated
  @UnsupportedAppUsage
  public static InputMethodManager peekInstance()
  {
    Log.w("InputMethodManager", "InputMethodManager.peekInstance() is deprecated because it cannot be compatible with multi-display. Use context.getSystemService(InputMethodManager.class) instead.", new Throwable());
    synchronized (sLock)
    {
      InputMethodManager localInputMethodManager = sInstance;
      return localInputMethodManager;
    }
  }
  
  private void recyclePendingEventLocked(PendingEvent paramPendingEvent)
  {
    paramPendingEvent.recycle();
    this.mPendingEventPool.release(paramPendingEvent);
  }
  
  static void scheduleCheckFocusLocked(View paramView)
  {
    paramView = paramView.getViewRootImpl();
    if (paramView != null) {
      paramView.dispatchCheckFocus();
    }
  }
  
  private void showInputMethodPickerLocked()
  {
    try
    {
      this.mService.showInputMethodPickerFromClient(this.mClient, 0);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  static void tearDownEditMode()
  {
    if (isInEditMode()) {
      synchronized (sLock)
      {
        sInstance = null;
        return;
      }
    }
    throw new UnsupportedOperationException("This method must be called only from layoutlib");
  }
  
  @UnsupportedAppUsage
  public void checkFocus()
  {
    if (checkFocusNoStartInput(false)) {
      startInputInner(4, null, 0, 0, 0);
    }
  }
  
  void clearBindingLocked()
  {
    clearConnectionLocked();
    setInputChannelLocked(null);
    this.mBindSequence = -1;
    this.mCurId = null;
    this.mCurMethod = null;
  }
  
  void clearConnectionLocked()
  {
    this.mCurrentTextBoxAttribute = null;
    ControlledInputConnectionWrapper localControlledInputConnectionWrapper = this.mServedInputConnectionWrapper;
    if (localControlledInputConnectionWrapper != null)
    {
      localControlledInputConnectionWrapper.deactivate();
      this.mServedInputConnectionWrapper = null;
    }
  }
  
  @UnsupportedAppUsage
  void closeCurrentInput()
  {
    try
    {
      this.mService.hideSoftInput(this.mClient, 2, null);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int dispatchInputEvent(InputEvent paramInputEvent, Object paramObject, FinishedInputEventCallback paramFinishedInputEventCallback, Handler paramHandler)
  {
    synchronized (this.mH)
    {
      if (this.mCurMethod != null)
      {
        if ((paramInputEvent instanceof KeyEvent))
        {
          KeyEvent localKeyEvent = (KeyEvent)paramInputEvent;
          if ((localKeyEvent.getAction() == 0) && (localKeyEvent.getKeyCode() == 63) && (localKeyEvent.getRepeatCount() == 0))
          {
            showInputMethodPickerLocked();
            return 1;
          }
        }
        paramInputEvent = obtainPendingEventLocked(paramInputEvent, paramObject, this.mCurId, paramFinishedInputEventCallback, paramHandler);
        if (this.mMainLooper.isCurrentThread())
        {
          int i = sendInputEventOnMainLooperLocked(paramInputEvent);
          return i;
        }
        paramInputEvent = this.mH.obtainMessage(5, paramInputEvent);
        paramInputEvent.setAsynchronous(true);
        this.mH.sendMessage(paramInputEvent);
        return -1;
      }
      return 0;
    }
  }
  
  public void dispatchKeyEventFromInputMethod(View paramView, KeyEvent paramKeyEvent)
  {
    Object localObject = getFallbackInputMethodManagerIfNecessary(paramView);
    if (localObject != null)
    {
      ((InputMethodManager)localObject).dispatchKeyEventFromInputMethod(paramView, paramKeyEvent);
      return;
    }
    H localH = this.mH;
    if (paramView != null) {
      try
      {
        paramView = paramView.getViewRootImpl();
      }
      finally
      {
        break label80;
      }
    } else {
      paramView = null;
    }
    localObject = paramView;
    if (paramView == null)
    {
      localObject = paramView;
      if (this.mServedView != null) {
        localObject = this.mServedView.getViewRootImpl();
      }
    }
    if (localObject != null) {
      ((ViewRootImpl)localObject).dispatchKeyFromIme(paramKeyEvent);
    }
    return;
    label80:
    throw paramView;
  }
  
  public void displayCompletions(View paramView, CompletionInfo[] paramArrayOfCompletionInfo)
  {
    ??? = getFallbackInputMethodManagerIfNecessary(paramView);
    if (??? != null)
    {
      ((InputMethodManager)???).displayCompletions(paramView, paramArrayOfCompletionInfo);
      return;
    }
    checkFocus();
    synchronized (this.mH)
    {
      if ((this.mServedView != paramView) && ((this.mServedView == null) || (!this.mServedView.checkInputConnectionProxy(paramView)))) {
        return;
      }
      this.mCompletions = paramArrayOfCompletionInfo;
      paramView = this.mCurMethod;
      if (paramView != null) {
        try
        {
          this.mCurMethod.displayCompletions(this.mCompletions);
        }
        catch (RemoteException paramView) {}
      }
      return;
    }
  }
  
  void doDump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramFileDescriptor = new PrintWriterPrinter(paramPrintWriter);
    paramPrintWriter = new StringBuilder();
    paramPrintWriter.append("Input method client state for ");
    paramPrintWriter.append(this);
    paramPrintWriter.append(":");
    paramFileDescriptor.println(paramPrintWriter.toString());
    paramPrintWriter = new StringBuilder();
    paramPrintWriter.append("  mService=");
    paramPrintWriter.append(this.mService);
    paramFileDescriptor.println(paramPrintWriter.toString());
    paramPrintWriter = new StringBuilder();
    paramPrintWriter.append("  mMainLooper=");
    paramPrintWriter.append(this.mMainLooper);
    paramFileDescriptor.println(paramPrintWriter.toString());
    paramPrintWriter = new StringBuilder();
    paramPrintWriter.append("  mIInputContext=");
    paramPrintWriter.append(this.mIInputContext);
    paramFileDescriptor.println(paramPrintWriter.toString());
    paramPrintWriter = new StringBuilder();
    paramPrintWriter.append("  mActive=");
    paramPrintWriter.append(this.mActive);
    paramPrintWriter.append(" mRestartOnNextWindowFocus=");
    paramPrintWriter.append(this.mRestartOnNextWindowFocus);
    paramPrintWriter.append(" mBindSequence=");
    paramPrintWriter.append(this.mBindSequence);
    paramPrintWriter.append(" mCurId=");
    paramPrintWriter.append(this.mCurId);
    paramFileDescriptor.println(paramPrintWriter.toString());
    paramPrintWriter = new StringBuilder();
    paramPrintWriter.append("  mFullscreenMode=");
    paramPrintWriter.append(this.mFullscreenMode);
    paramFileDescriptor.println(paramPrintWriter.toString());
    paramPrintWriter = new StringBuilder();
    paramPrintWriter.append("  mCurMethod=");
    paramPrintWriter.append(this.mCurMethod);
    paramFileDescriptor.println(paramPrintWriter.toString());
    paramPrintWriter = new StringBuilder();
    paramPrintWriter.append("  mCurRootView=");
    paramPrintWriter.append(this.mCurRootView);
    paramFileDescriptor.println(paramPrintWriter.toString());
    paramPrintWriter = new StringBuilder();
    paramPrintWriter.append("  mServedView=");
    paramPrintWriter.append(this.mServedView);
    paramFileDescriptor.println(paramPrintWriter.toString());
    paramPrintWriter = new StringBuilder();
    paramPrintWriter.append("  mNextServedView=");
    paramPrintWriter.append(this.mNextServedView);
    paramFileDescriptor.println(paramPrintWriter.toString());
    paramPrintWriter = new StringBuilder();
    paramPrintWriter.append("  mServedConnecting=");
    paramPrintWriter.append(this.mServedConnecting);
    paramFileDescriptor.println(paramPrintWriter.toString());
    if (this.mCurrentTextBoxAttribute != null)
    {
      paramFileDescriptor.println("  mCurrentTextBoxAttribute:");
      this.mCurrentTextBoxAttribute.dump(paramFileDescriptor, "    ");
    }
    else
    {
      paramFileDescriptor.println("  mCurrentTextBoxAttribute: null");
    }
    paramPrintWriter = new StringBuilder();
    paramPrintWriter.append("  mServedInputConnectionWrapper=");
    paramPrintWriter.append(this.mServedInputConnectionWrapper);
    paramFileDescriptor.println(paramPrintWriter.toString());
    paramPrintWriter = new StringBuilder();
    paramPrintWriter.append("  mCompletions=");
    paramPrintWriter.append(Arrays.toString(this.mCompletions));
    paramFileDescriptor.println(paramPrintWriter.toString());
    paramPrintWriter = new StringBuilder();
    paramPrintWriter.append("  mCursorRect=");
    paramPrintWriter.append(this.mCursorRect);
    paramFileDescriptor.println(paramPrintWriter.toString());
    paramPrintWriter = new StringBuilder();
    paramPrintWriter.append("  mCursorSelStart=");
    paramPrintWriter.append(this.mCursorSelStart);
    paramPrintWriter.append(" mCursorSelEnd=");
    paramPrintWriter.append(this.mCursorSelEnd);
    paramPrintWriter.append(" mCursorCandStart=");
    paramPrintWriter.append(this.mCursorCandStart);
    paramPrintWriter.append(" mCursorCandEnd=");
    paramPrintWriter.append(this.mCursorCandEnd);
    paramFileDescriptor.println(paramPrintWriter.toString());
  }
  
  @UnsupportedAppUsage
  void finishInputLocked()
  {
    this.mNextServedView = null;
    this.mActivityViewToScreenMatrix = null;
    if (this.mServedView != null)
    {
      this.mServedView = null;
      this.mCompletions = null;
      this.mServedConnecting = false;
      clearConnectionLocked();
    }
  }
  
  void finishedInputEvent(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    synchronized (this.mH)
    {
      paramInt = this.mPendingEvents.indexOfKey(paramInt);
      if (paramInt < 0) {
        return;
      }
      PendingEvent localPendingEvent = (PendingEvent)this.mPendingEvents.valueAt(paramInt);
      this.mPendingEvents.removeAt(paramInt);
      Trace.traceCounter(4L, "aq:imm", this.mPendingEvents.size());
      if (paramBoolean2)
      {
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>();
        localStringBuilder.append("Timeout waiting for IME to handle input event after 2500 ms: ");
        localStringBuilder.append(localPendingEvent.mInputMethodId);
        Log.w("InputMethodManager", localStringBuilder.toString());
      }
      else
      {
        this.mH.removeMessages(6, localPendingEvent);
      }
      invokeFinishedInputEventCallback(localPendingEvent, paramBoolean1);
      return;
    }
  }
  
  @UnsupportedAppUsage
  public void focusIn(View paramView)
  {
    synchronized (this.mH)
    {
      focusInLocked(paramView);
      return;
    }
  }
  
  void focusInLocked(View paramView)
  {
    if ((paramView != null) && (paramView.isTemporarilyDetached())) {
      return;
    }
    if (this.mCurRootView != paramView.getRootView()) {
      return;
    }
    this.mNextServedView = paramView;
    scheduleCheckFocusLocked(paramView);
  }
  
  @UnsupportedAppUsage
  public void focusOut(View arg1)
  {
    synchronized (this.mH)
    {
      View localView = this.mServedView;
      return;
    }
  }
  
  @UnsupportedAppUsage
  public IInputMethodClient getClient()
  {
    return this.mClient;
  }
  
  public InputMethodSubtype getCurrentInputMethodSubtype()
  {
    try
    {
      InputMethodSubtype localInputMethodSubtype = this.mService.getCurrentInputMethodSubtype();
      return localInputMethodSubtype;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getDisplayId()
  {
    return this.mDisplayId;
  }
  
  public List<InputMethodInfo> getEnabledInputMethodList()
  {
    try
    {
      List localList = this.mService.getEnabledInputMethodList(UserHandle.myUserId());
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<InputMethodInfo> getEnabledInputMethodListAsUser(int paramInt)
  {
    try
    {
      List localList = this.mService.getEnabledInputMethodList(paramInt);
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<InputMethodSubtype> getEnabledInputMethodSubtypeList(InputMethodInfo paramInputMethodInfo, boolean paramBoolean)
  {
    try
    {
      IInputMethodManager localIInputMethodManager = this.mService;
      if (paramInputMethodInfo == null) {
        paramInputMethodInfo = null;
      } else {
        paramInputMethodInfo = paramInputMethodInfo.getId();
      }
      paramInputMethodInfo = localIInputMethodManager.getEnabledInputMethodSubtypeList(paramInputMethodInfo, paramBoolean);
      return paramInputMethodInfo;
    }
    catch (RemoteException paramInputMethodInfo)
    {
      throw paramInputMethodInfo.rethrowFromSystemServer();
    }
  }
  
  @UnsupportedAppUsage
  public IInputContext getInputContext()
  {
    return this.mIInputContext;
  }
  
  public List<InputMethodInfo> getInputMethodList()
  {
    try
    {
      List localList = this.mService.getInputMethodList(UserHandle.myUserId());
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<InputMethodInfo> getInputMethodListAsUser(int paramInt)
  {
    try
    {
      List localList = this.mService.getInputMethodList(paramInt);
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @UnsupportedAppUsage
  public int getInputMethodWindowVisibleHeight()
  {
    try
    {
      int i = this.mService.getInputMethodWindowVisibleHeight();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public InputMethodSubtype getLastInputMethodSubtype()
  {
    try
    {
      InputMethodSubtype localInputMethodSubtype = this.mService.getLastInputMethodSubtype();
      return localInputMethodSubtype;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public Map<InputMethodInfo, List<InputMethodSubtype>> getShortcutInputMethodsAndSubtypes()
  {
    List localList = getEnabledInputMethodList();
    localList.sort(Comparator.comparingInt(_..Lambda.InputMethodManager.pvWYFFVbHzZCDCCTiZVM09Xls4w.INSTANCE));
    int i = localList.size();
    for (int j = 0; j < i; j++)
    {
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)localList.get(j);
      int k = getEnabledInputMethodSubtypeList(localInputMethodInfo, true).size();
      for (int m = 0; m < k; m++)
      {
        InputMethodSubtype localInputMethodSubtype = localInputMethodInfo.getSubtypeAt(m);
        if ("voice".equals(localInputMethodSubtype.getMode())) {
          return Collections.singletonMap(localInputMethodInfo, Collections.singletonList(localInputMethodSubtype));
        }
      }
    }
    return Collections.emptyMap();
  }
  
  @Deprecated
  public void hideSoftInputFromInputMethod(IBinder paramIBinder, int paramInt)
  {
    InputMethodPrivilegedOperationsRegistry.get(paramIBinder).hideMySoftInput(paramInt);
  }
  
  public boolean hideSoftInputFromWindow(IBinder paramIBinder, int paramInt)
  {
    return hideSoftInputFromWindow(paramIBinder, paramInt, null);
  }
  
  public boolean hideSoftInputFromWindow(IBinder paramIBinder, int paramInt, ResultReceiver paramResultReceiver)
  {
    checkFocus();
    synchronized (this.mH)
    {
      if (this.mServedView != null)
      {
        IBinder localIBinder = this.mServedView.getWindowToken();
        if (localIBinder == paramIBinder) {
          try
          {
            boolean bool = this.mService.hideSoftInput(this.mClient, paramInt, paramResultReceiver);
            return bool;
          }
          catch (RemoteException paramIBinder)
          {
            throw paramIBinder.rethrowFromSystemServer();
          }
        }
      }
      return false;
    }
  }
  
  @Deprecated
  public void hideStatusIcon(IBinder paramIBinder)
  {
    InputMethodPrivilegedOperationsRegistry.get(paramIBinder).updateStatusIcon(null, 0);
  }
  
  void invokeFinishedInputEventCallback(PendingEvent paramPendingEvent, boolean paramBoolean)
  {
    paramPendingEvent.mHandled = paramBoolean;
    if (paramPendingEvent.mHandler.getLooper().isCurrentThread())
    {
      paramPendingEvent.run();
    }
    else
    {
      paramPendingEvent = Message.obtain(paramPendingEvent.mHandler, paramPendingEvent);
      paramPendingEvent.setAsynchronous(true);
      paramPendingEvent.sendToTarget();
    }
  }
  
  public boolean isAcceptingText()
  {
    checkFocus();
    ControlledInputConnectionWrapper localControlledInputConnectionWrapper = this.mServedInputConnectionWrapper;
    boolean bool;
    if ((localControlledInputConnectionWrapper != null) && (localControlledInputConnectionWrapper.getInputConnection() != null)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isActive()
  {
    checkFocus();
    synchronized (this.mH)
    {
      boolean bool;
      if ((this.mServedView != null) && (this.mCurrentTextBoxAttribute != null)) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
  }
  
  public boolean isActive(View paramView)
  {
    ??? = getFallbackInputMethodManagerIfNecessary(paramView);
    if (??? != null) {
      return ((InputMethodManager)???).isActive(paramView);
    }
    checkFocus();
    synchronized (this.mH)
    {
      boolean bool;
      if (((this.mServedView == paramView) || ((this.mServedView != null) && (this.mServedView.checkInputConnectionProxy(paramView)))) && (this.mCurrentTextBoxAttribute != null)) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
  }
  
  @UnsupportedAppUsage
  public boolean isCursorAnchorInfoEnabled()
  {
    synchronized (this.mH)
    {
      int i = this.mRequestUpdateCursorAnchorInfoMonitorMode;
      boolean bool1 = true;
      if ((i & 0x1) != 0) {
        i = 1;
      } else {
        i = 0;
      }
      int j;
      if ((this.mRequestUpdateCursorAnchorInfoMonitorMode & 0x2) != 0) {
        j = 1;
      } else {
        j = 0;
      }
      boolean bool2 = bool1;
      if (i == 0) {
        if (j != 0) {
          bool2 = bool1;
        } else {
          bool2 = false;
        }
      }
      return bool2;
    }
  }
  
  public boolean isFullscreenMode()
  {
    synchronized (this.mH)
    {
      boolean bool = this.mFullscreenMode;
      return bool;
    }
  }
  
  public boolean isInputMethodPickerShown()
  {
    try
    {
      boolean bool = this.mService.isInputMethodPickerShownForTest();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public boolean isWatchingCursor(View paramView)
  {
    return false;
  }
  
  public void notifyImeHidden()
  {
    try
    {
      synchronized (this.mH)
      {
        if (this.mCurMethod != null) {
          this.mCurMethod.notifyImeHidden();
        }
      }
    }
    catch (RemoteException localRemoteException) {}
  }
  
  @Deprecated
  @UnsupportedAppUsage
  public void notifySuggestionPicked(SuggestionSpan paramSuggestionSpan, String paramString, int paramInt)
  {
    Log.w("InputMethodManager", "notifySuggestionPicked() is deprecated.  Does nothing.");
  }
  
  @Deprecated
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=114740982L)
  public void notifyUserAction()
  {
    Log.w("InputMethodManager", "notifyUserAction() is a hidden method, which is now just a stub method that does nothing.  Leave comments in b.android.com/114740982 if your  application still depends on the previous behavior of this method.");
  }
  
  /* Error */
  public void onPostWindowFocus(View paramView1, View paramView2, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 212	android/view/inputmethod/InputMethodManager:mH	Landroid/view/inputmethod/InputMethodManager$H;
    //   4: astore 6
    //   6: aload 6
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 166	android/view/inputmethod/InputMethodManager:mRestartOnNextWindowFocus	Z
    //   13: ifeq +14 -> 27
    //   16: aload_0
    //   17: iconst_0
    //   18: putfield 166	android/view/inputmethod/InputMethodManager:mRestartOnNextWindowFocus	Z
    //   21: iconst_1
    //   22: istore 7
    //   24: goto +6 -> 30
    //   27: iconst_0
    //   28: istore 7
    //   30: aload_2
    //   31: ifnull +9 -> 40
    //   34: aload_2
    //   35: astore 8
    //   37: goto +6 -> 43
    //   40: aload_1
    //   41: astore 8
    //   43: aload_0
    //   44: aload 8
    //   46: invokevirtual 843	android/view/inputmethod/InputMethodManager:focusInLocked	(Landroid/view/View;)V
    //   49: aload 6
    //   51: monitorexit
    //   52: iconst_0
    //   53: istore 9
    //   55: aload_2
    //   56: ifnull +25 -> 81
    //   59: iconst_0
    //   60: iconst_1
    //   61: ior
    //   62: istore 10
    //   64: iload 10
    //   66: istore 9
    //   68: aload_2
    //   69: invokevirtual 280	android/view/View:onCheckIsTextEditor	()Z
    //   72: ifeq +9 -> 81
    //   75: iload 10
    //   77: iconst_2
    //   78: ior
    //   79: istore 9
    //   81: iload 4
    //   83: ifeq +12 -> 95
    //   86: iload 9
    //   88: iconst_4
    //   89: ior
    //   90: istore 9
    //   92: goto +3 -> 95
    //   95: aload_0
    //   96: iload 7
    //   98: invokespecial 238	android/view/inputmethod/InputMethodManager:checkFocusNoStartInput	(Z)Z
    //   101: ifeq +21 -> 122
    //   104: aload_0
    //   105: iconst_1
    //   106: aload_1
    //   107: invokevirtual 397	android/view/View:getWindowToken	()Landroid/os/IBinder;
    //   110: iload 9
    //   112: iload_3
    //   113: iload 5
    //   115: invokevirtual 651	android/view/inputmethod/InputMethodManager:startInputInner	(ILandroid/os/IBinder;III)Z
    //   118: ifeq +4 -> 122
    //   121: return
    //   122: aload_0
    //   123: getfield 212	android/view/inputmethod/InputMethodManager:mH	Landroid/view/inputmethod/InputMethodManager$H;
    //   126: astore_2
    //   127: aload_2
    //   128: monitorenter
    //   129: aload_0
    //   130: getfield 205	android/view/inputmethod/InputMethodManager:mService	Lcom/android/internal/view/IInputMethodManager;
    //   133: iconst_2
    //   134: aload_0
    //   135: getfield 196	android/view/inputmethod/InputMethodManager:mClient	Lcom/android/internal/view/IInputMethodClient$Stub;
    //   138: aload_1
    //   139: invokevirtual 397	android/view/View:getWindowToken	()Landroid/os/IBinder;
    //   142: iload 9
    //   144: iload_3
    //   145: iload 5
    //   147: aconst_null
    //   148: aconst_null
    //   149: iconst_0
    //   150: aload_1
    //   151: invokevirtual 403	android/view/View:getContext	()Landroid/content/Context;
    //   154: invokevirtual 1016	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   157: getfield 1021	android/content/pm/ApplicationInfo:targetSdkVersion	I
    //   160: invokeinterface 1025 11 0
    //   165: pop
    //   166: aload_2
    //   167: monitorexit
    //   168: return
    //   169: astore_1
    //   170: goto +9 -> 179
    //   173: astore_1
    //   174: aload_1
    //   175: invokevirtual 334	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   178: athrow
    //   179: aload_2
    //   180: monitorexit
    //   181: aload_1
    //   182: athrow
    //   183: astore_1
    //   184: goto +4 -> 188
    //   187: astore_1
    //   188: aload 6
    //   190: monitorexit
    //   191: aload_1
    //   192: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	193	0	this	InputMethodManager
    //   0	193	1	paramView1	View
    //   0	193	2	paramView2	View
    //   0	193	3	paramInt1	int
    //   0	193	4	paramBoolean	boolean
    //   0	193	5	paramInt2	int
    //   4	185	6	localH	H
    //   22	75	7	bool	boolean
    //   35	10	8	localView	View
    //   53	90	9	i	int
    //   62	17	10	j	int
    // Exception table:
    //   from	to	target	type
    //   129	166	169	finally
    //   166	168	169	finally
    //   174	179	169	finally
    //   179	181	169	finally
    //   129	166	173	android/os/RemoteException
    //   43	52	183	finally
    //   9	21	187	finally
    //   188	191	187	finally
  }
  
  @UnsupportedAppUsage
  public void onPreWindowFocus(View paramView, boolean paramBoolean)
  {
    H localH = this.mH;
    if (paramView == null) {
      try
      {
        this.mCurRootView = null;
      }
      finally
      {
        break label51;
      }
    }
    if (paramBoolean) {
      this.mCurRootView = paramView;
    } else if (paramView == this.mCurRootView) {
      this.mCurRootView = null;
    }
    return;
    label51:
    throw paramView;
  }
  
  public void onViewDetachedFromWindow(View paramView)
  {
    synchronized (this.mH)
    {
      if (this.mServedView == paramView)
      {
        this.mNextServedView = null;
        scheduleCheckFocusLocked(paramView);
      }
      return;
    }
  }
  
  public void registerImeConsumer(ImeInsetsSourceConsumer paramImeInsetsSourceConsumer)
  {
    if (paramImeInsetsSourceConsumer != null) {
      synchronized (this.mH)
      {
        this.mImeInsetsConsumer = paramImeInsetsSourceConsumer;
        return;
      }
    }
    throw new IllegalStateException("ImeInsetsSourceConsumer cannot be null.");
  }
  
  @Deprecated
  @UnsupportedAppUsage
  public void registerSuggestionSpansForNotification(SuggestionSpan[] paramArrayOfSuggestionSpan)
  {
    Log.w("InputMethodManager", "registerSuggestionSpansForNotification() is deprecated.  Does nothing.");
  }
  
  public void reportActivityView(int paramInt, Matrix paramMatrix)
  {
    if (paramMatrix == null) {
      paramMatrix = null;
    }
    try
    {
      float[] arrayOfFloat = new float[9];
      paramMatrix.getValues(arrayOfFloat);
      paramMatrix = arrayOfFloat;
      this.mService.reportActivityView(this.mClient, paramInt, paramMatrix);
      return;
    }
    catch (RemoteException paramMatrix)
    {
      throw paramMatrix.rethrowFromSystemServer();
    }
  }
  
  public boolean requestImeShow(ResultReceiver paramResultReceiver)
  {
    synchronized (this.mH)
    {
      if (this.mServedView == null) {
        return false;
      }
      showSoftInput(this.mServedView, 0, paramResultReceiver);
      return true;
    }
  }
  
  public void restartInput(View paramView)
  {
    ??? = getFallbackInputMethodManagerIfNecessary(paramView);
    if (??? != null)
    {
      ((InputMethodManager)???).restartInput(paramView);
      return;
    }
    checkFocus();
    synchronized (this.mH)
    {
      if ((this.mServedView != paramView) && ((this.mServedView == null) || (!this.mServedView.checkInputConnectionProxy(paramView)))) {
        return;
      }
      this.mServedConnecting = true;
      startInputInner(3, null, 0, 0, 0);
      return;
    }
  }
  
  public void sendAppPrivateCommand(View paramView, String paramString, Bundle paramBundle)
  {
    ??? = getFallbackInputMethodManagerIfNecessary(paramView);
    if (??? != null)
    {
      ((InputMethodManager)???).sendAppPrivateCommand(paramView, paramString, paramBundle);
      return;
    }
    checkFocus();
    synchronized (this.mH)
    {
      if (((this.mServedView == paramView) || ((this.mServedView != null) && (this.mServedView.checkInputConnectionProxy(paramView)))) && (this.mCurrentTextBoxAttribute != null))
      {
        paramView = this.mCurMethod;
        if (paramView != null)
        {
          try
          {
            this.mCurMethod.appPrivateCommand(paramString, paramBundle);
          }
          catch (RemoteException paramView)
          {
            paramString = new java/lang/StringBuilder;
            paramString.<init>();
            paramString.append("IME died: ");
            paramString.append(this.mCurId);
            Log.w("InputMethodManager", paramString.toString(), paramView);
          }
          return;
        }
      }
      return;
    }
  }
  
  void sendInputEventAndReportResultOnMainLooper(PendingEvent paramPendingEvent)
  {
    synchronized (this.mH)
    {
      int i = sendInputEventOnMainLooperLocked(paramPendingEvent);
      if (i == -1) {
        return;
      }
      boolean bool = true;
      if (i != 1) {
        bool = false;
      }
      invokeFinishedInputEventCallback(paramPendingEvent, bool);
      return;
    }
  }
  
  int sendInputEventOnMainLooperLocked(PendingEvent paramPendingEvent)
  {
    Object localObject = this.mCurChannel;
    if (localObject != null)
    {
      if (this.mCurSender == null) {
        this.mCurSender = new ImeInputEventSender((InputChannel)localObject, this.mH.getLooper());
      }
      localObject = paramPendingEvent.mEvent;
      int i = ((InputEvent)localObject).getSequenceNumber();
      if (this.mCurSender.sendInputEvent(i, (InputEvent)localObject))
      {
        this.mPendingEvents.put(i, paramPendingEvent);
        Trace.traceCounter(4L, "aq:imm", this.mPendingEvents.size());
        paramPendingEvent = this.mH.obtainMessage(6, i, 0, paramPendingEvent);
        paramPendingEvent.setAsynchronous(true);
        this.mH.sendMessageDelayed(paramPendingEvent, 2500L);
        return -1;
      }
      paramPendingEvent = new StringBuilder();
      paramPendingEvent.append("Unable to send input event to IME: ");
      paramPendingEvent.append(this.mCurId);
      paramPendingEvent.append(" dropping: ");
      paramPendingEvent.append(localObject);
      Log.w("InputMethodManager", paramPendingEvent.toString());
    }
    return 0;
  }
  
  @Deprecated
  public void setAdditionalInputMethodSubtypes(String paramString, InputMethodSubtype[] paramArrayOfInputMethodSubtype)
  {
    try
    {
      this.mService.setAdditionalInputMethodSubtypes(paramString, paramArrayOfInputMethodSubtype);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public boolean setCurrentInputMethodSubtype(InputMethodSubtype paramInputMethodSubtype)
  {
    if (Process.myUid() == 1000)
    {
      Log.w("InputMethodManager", "System process should not call setCurrentInputMethodSubtype() because almost always it is a bug under multi-user / multi-profile environment. Consider directly interacting with InputMethodManagerService via LocalServices.");
      return false;
    }
    if (paramInputMethodSubtype == null) {
      return false;
    }
    Object localObject1 = ActivityThread.currentApplication();
    if (localObject1 == null) {
      return false;
    }
    if (((Context)localObject1).checkSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
      return false;
    }
    localObject1 = ((Context)localObject1).getContentResolver();
    Object localObject2 = Settings.Secure.getString((ContentResolver)localObject1, "default_input_method");
    if (ComponentName.unflattenFromString((String)localObject2) == null) {
      return false;
    }
    try
    {
      localObject2 = this.mService.getEnabledInputMethodSubtypeList((String)localObject2, true);
      int i = ((List)localObject2).size();
      for (int j = 0; j < i; j++)
      {
        InputMethodSubtype localInputMethodSubtype = (InputMethodSubtype)((List)localObject2).get(j);
        if (localInputMethodSubtype.equals(paramInputMethodSubtype))
        {
          Settings.Secure.putInt((ContentResolver)localObject1, "selected_input_method_subtype", localInputMethodSubtype.hashCode());
          return true;
        }
      }
      return false;
    }
    catch (RemoteException paramInputMethodSubtype) {}
    return false;
  }
  
  void setInputChannelLocked(InputChannel paramInputChannel)
  {
    if (this.mCurChannel != paramInputChannel)
    {
      if (this.mCurSender != null)
      {
        flushPendingEventsLocked();
        this.mCurSender.dispose();
        this.mCurSender = null;
      }
      InputChannel localInputChannel = this.mCurChannel;
      if (localInputChannel != null) {
        localInputChannel.dispose();
      }
      this.mCurChannel = paramInputChannel;
    }
  }
  
  @Deprecated
  public void setInputMethod(IBinder paramIBinder, String paramString)
  {
    if (paramIBinder == null)
    {
      if (paramString == null) {
        return;
      }
      if (Process.myUid() == 1000)
      {
        Log.w("InputMethodManager", "System process should not be calling setInputMethod() because almost always it is a bug under multi-user / multi-profile environment. Consider interacting with InputMethodManagerService directly via LocalServices.");
        return;
      }
      Application localApplication = ActivityThread.currentApplication();
      if (localApplication == null) {
        return;
      }
      if (localApplication.checkSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
        return;
      }
      paramIBinder = getEnabledInputMethodList();
      int i = paramIBinder.size();
      int j = 0;
      int m;
      for (int k = 0;; k++)
      {
        m = j;
        if (k >= i) {
          break;
        }
        if (paramString.equals(((InputMethodInfo)paramIBinder.get(k)).getId()))
        {
          m = 1;
          break;
        }
      }
      if (m == 0)
      {
        paramIBinder = new StringBuilder();
        paramIBinder.append("Ignoring setInputMethod(null, ");
        paramIBinder.append(paramString);
        paramIBinder.append(") because the specified id not found in enabled IMEs.");
        Log.e("InputMethodManager", paramIBinder.toString());
        return;
      }
      Log.w("InputMethodManager", "The undocumented behavior that setInputMethod() accepts null token when the caller has WRITE_SECURE_SETTINGS is deprecated. This behavior may be completely removed in a future version.  Update secure settings directly instead.");
      paramIBinder = localApplication.getContentResolver();
      Settings.Secure.putInt(paramIBinder, "selected_input_method_subtype", -1);
      Settings.Secure.putString(paramIBinder, "default_input_method", paramString);
      return;
    }
    InputMethodPrivilegedOperationsRegistry.get(paramIBinder).setInputMethod(paramString);
  }
  
  @Deprecated
  public void setInputMethodAndSubtype(IBinder paramIBinder, String paramString, InputMethodSubtype paramInputMethodSubtype)
  {
    if (paramIBinder == null)
    {
      Log.e("InputMethodManager", "setInputMethodAndSubtype() does not accept null token on Android Q and later.");
      return;
    }
    InputMethodPrivilegedOperationsRegistry.get(paramIBinder).setInputMethodAndSubtype(paramString, paramInputMethodSubtype);
  }
  
  @UnsupportedAppUsage
  public void setUpdateCursorAnchorInfoMode(int paramInt)
  {
    synchronized (this.mH)
    {
      this.mRequestUpdateCursorAnchorInfoMonitorMode = paramInt;
      return;
    }
  }
  
  @Deprecated
  public boolean shouldOfferSwitchingToNextInputMethod(IBinder paramIBinder)
  {
    return InputMethodPrivilegedOperationsRegistry.get(paramIBinder).shouldOfferSwitchingToNextInputMethod();
  }
  
  public void showInputMethodAndSubtypeEnabler(String paramString)
  {
    try
    {
      this.mService.showInputMethodAndSubtypeEnablerFromClient(this.mClient, paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void showInputMethodPicker()
  {
    synchronized (this.mH)
    {
      showInputMethodPickerLocked();
      return;
    }
  }
  
  public void showInputMethodPickerFromSystem(boolean paramBoolean, int paramInt)
  {
    int i;
    if (paramBoolean) {
      i = 1;
    } else {
      i = 2;
    }
    try
    {
      this.mService.showInputMethodPickerFromSystem(this.mClient, i, paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean showSoftInput(View paramView, int paramInt)
  {
    InputMethodManager localInputMethodManager = getFallbackInputMethodManagerIfNecessary(paramView);
    if (localInputMethodManager != null) {
      return localInputMethodManager.showSoftInput(paramView, paramInt);
    }
    return showSoftInput(paramView, paramInt, null);
  }
  
  public boolean showSoftInput(View paramView, int paramInt, ResultReceiver paramResultReceiver)
  {
    ??? = getFallbackInputMethodManagerIfNecessary(paramView);
    if (??? != null) {
      return ((InputMethodManager)???).showSoftInput(paramView, paramInt, paramResultReceiver);
    }
    checkFocus();
    synchronized (this.mH)
    {
      if ((this.mServedView != paramView) && ((this.mServedView == null) || (!this.mServedView.checkInputConnectionProxy(paramView))))
      {
        paramResultReceiver = new java/lang/StringBuilder;
        paramResultReceiver.<init>();
        paramResultReceiver.append("showSoftInput fail, mServedView:");
        paramResultReceiver.append(this.mServedView);
        paramResultReceiver.append(" view:");
        paramResultReceiver.append(paramView);
        Log.d("InputMethodManager", paramResultReceiver.toString());
        return false;
      }
      try
      {
        boolean bool = this.mService.showSoftInput(this.mClient, paramInt, paramResultReceiver);
        return bool;
      }
      catch (RemoteException paramView)
      {
        throw paramView.rethrowFromSystemServer();
      }
    }
  }
  
  @Deprecated
  public void showSoftInputFromInputMethod(IBinder paramIBinder, int paramInt)
  {
    InputMethodPrivilegedOperationsRegistry.get(paramIBinder).showMySoftInput(paramInt);
  }
  
  @Deprecated
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123768499L)
  public void showSoftInputUnchecked(int paramInt, ResultReceiver paramResultReceiver)
  {
    try
    {
      Log.w("InputMethodManager", "showSoftInputUnchecked() is a hidden method, which will be removed soon. If you are using android.support.v7.widget.SearchView, please update to version 26.0 or newer version.");
      this.mService.showSoftInput(this.mClient, paramInt, paramResultReceiver);
      return;
    }
    catch (RemoteException paramResultReceiver)
    {
      throw paramResultReceiver.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void showStatusIcon(IBinder paramIBinder, String paramString, int paramInt)
  {
    InputMethodPrivilegedOperationsRegistry.get(paramIBinder).updateStatusIcon(paramString, paramInt);
  }
  
  /* Error */
  boolean startInputInner(int paramInt1, IBinder paramIBinder, int paramInt2, int paramInt3, int paramInt4)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 212	android/view/inputmethod/InputMethodManager:mH	Landroid/view/inputmethod/InputMethodManager$H;
    //   4: astore 6
    //   6: aload 6
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 261	android/view/inputmethod/InputMethodManager:mServedView	Landroid/view/View;
    //   13: astore 7
    //   15: aload 7
    //   17: ifnonnull +8 -> 25
    //   20: aload 6
    //   22: monitorexit
    //   23: iconst_0
    //   24: ireturn
    //   25: aload 6
    //   27: monitorexit
    //   28: aload_2
    //   29: ifnonnull +76 -> 105
    //   32: aload 7
    //   34: invokevirtual 397	android/view/View:getWindowToken	()Landroid/os/IBinder;
    //   37: astore 6
    //   39: aload 6
    //   41: ifnonnull +14 -> 55
    //   44: ldc 78
    //   46: ldc_w 1224
    //   49: invokestatic 491	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   52: pop
    //   53: iconst_0
    //   54: ireturn
    //   55: iload_3
    //   56: iconst_1
    //   57: ior
    //   58: istore 4
    //   60: iload 4
    //   62: istore_3
    //   63: aload 7
    //   65: invokevirtual 280	android/view/View:onCheckIsTextEditor	()Z
    //   68: ifeq +8 -> 76
    //   71: iload 4
    //   73: iconst_2
    //   74: ior
    //   75: istore_3
    //   76: aload 7
    //   78: invokevirtual 472	android/view/View:getViewRootImpl	()Landroid/view/ViewRootImpl;
    //   81: getfield 1228	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   84: getfield 1233	android/view/WindowManager$LayoutParams:softInputMode	I
    //   87: istore 5
    //   89: aload 7
    //   91: invokevirtual 472	android/view/View:getViewRootImpl	()Landroid/view/ViewRootImpl;
    //   94: getfield 1228	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   97: getfield 1236	android/view/WindowManager$LayoutParams:flags	I
    //   100: istore 4
    //   102: goto +18 -> 120
    //   105: iload 4
    //   107: istore 8
    //   109: iload 5
    //   111: istore 4
    //   113: iload 8
    //   115: istore 5
    //   117: aload_2
    //   118: astore 6
    //   120: aload 7
    //   122: invokevirtual 1240	android/view/View:getHandler	()Landroid/os/Handler;
    //   125: astore 9
    //   127: aload 9
    //   129: ifnonnull +9 -> 138
    //   132: aload_0
    //   133: invokevirtual 269	android/view/inputmethod/InputMethodManager:closeCurrentInput	()V
    //   136: iconst_0
    //   137: ireturn
    //   138: aload 9
    //   140: invokevirtual 975	android/os/Handler:getLooper	()Landroid/os/Looper;
    //   143: invokestatic 1243	android/os/Looper:myLooper	()Landroid/os/Looper;
    //   146: if_acmpeq +20 -> 166
    //   149: aload 9
    //   151: new 1245	android/view/inputmethod/_$$Lambda$InputMethodManager$dfnCauFoZCf_HfXs1QavrkwWDf0
    //   154: dup
    //   155: aload_0
    //   156: iload_1
    //   157: invokespecial 1248	android/view/inputmethod/_$$Lambda$InputMethodManager$dfnCauFoZCf_HfXs1QavrkwWDf0:<init>	(Landroid/view/inputmethod/InputMethodManager;I)V
    //   160: invokevirtual 1252	android/os/Handler:post	(Ljava/lang/Runnable;)Z
    //   163: pop
    //   164: iconst_0
    //   165: ireturn
    //   166: new 777	android/view/inputmethod/EditorInfo
    //   169: dup
    //   170: invokespecial 1253	android/view/inputmethod/EditorInfo:<init>	()V
    //   173: astore 10
    //   175: aload 10
    //   177: aload 7
    //   179: invokevirtual 403	android/view/View:getContext	()Landroid/content/Context;
    //   182: invokevirtual 1256	android/content/Context:getOpPackageName	()Ljava/lang/String;
    //   185: putfield 1259	android/view/inputmethod/EditorInfo:packageName	Ljava/lang/String;
    //   188: aload 10
    //   190: aload 7
    //   192: invokevirtual 1261	android/view/View:getId	()I
    //   195: putfield 1264	android/view/inputmethod/EditorInfo:fieldId	I
    //   198: aload 7
    //   200: aload 10
    //   202: invokevirtual 1268	android/view/View:onCreateInputConnection	(Landroid/view/inputmethod/EditorInfo;)Landroid/view/inputmethod/InputConnection;
    //   205: astore 11
    //   207: aload_0
    //   208: getfield 212	android/view/inputmethod/InputMethodManager:mH	Landroid/view/inputmethod/InputMethodManager$H;
    //   211: astore_2
    //   212: aload_2
    //   213: monitorenter
    //   214: aload_0
    //   215: getfield 261	android/view/inputmethod/InputMethodManager:mServedView	Landroid/view/View;
    //   218: aload 7
    //   220: if_acmpne +680 -> 900
    //   223: aload_0
    //   224: getfield 277	android/view/inputmethod/InputMethodManager:mServedConnecting	Z
    //   227: ifne +6 -> 233
    //   230: goto +670 -> 900
    //   233: iload_3
    //   234: istore 8
    //   236: aload_0
    //   237: getfield 273	android/view/inputmethod/InputMethodManager:mCurrentTextBoxAttribute	Landroid/view/inputmethod/EditorInfo;
    //   240: ifnonnull +9 -> 249
    //   243: iload_3
    //   244: bipush 8
    //   246: ior
    //   247: istore 8
    //   249: aload_0
    //   250: aload 10
    //   252: putfield 273	android/view/inputmethod/InputMethodManager:mCurrentTextBoxAttribute	Landroid/view/inputmethod/EditorInfo;
    //   255: aload_0
    //   256: aload 10
    //   258: invokespecial 284	android/view/inputmethod/InputMethodManager:maybeCallServedViewChangedLocked	(Landroid/view/inputmethod/EditorInfo;)V
    //   261: aload_0
    //   262: iconst_0
    //   263: putfield 277	android/view/inputmethod/InputMethodManager:mServedConnecting	Z
    //   266: aload_0
    //   267: getfield 271	android/view/inputmethod/InputMethodManager:mServedInputConnectionWrapper	Landroid/view/inputmethod/InputMethodManager$ControlledInputConnectionWrapper;
    //   270: astore 12
    //   272: aload 12
    //   274: ifnull +29 -> 303
    //   277: aload_0
    //   278: getfield 271	android/view/inputmethod/InputMethodManager:mServedInputConnectionWrapper	Landroid/view/inputmethod/InputMethodManager$ControlledInputConnectionWrapper;
    //   281: invokevirtual 666	android/view/inputmethod/InputMethodManager$ControlledInputConnectionWrapper:deactivate	()V
    //   284: aload_0
    //   285: aconst_null
    //   286: putfield 271	android/view/inputmethod/InputMethodManager:mServedInputConnectionWrapper	Landroid/view/inputmethod/InputMethodManager$ControlledInputConnectionWrapper;
    //   289: goto +14 -> 303
    //   292: astore 6
    //   294: aload_2
    //   295: astore 12
    //   297: aload 6
    //   299: astore_2
    //   300: goto +615 -> 915
    //   303: aload 11
    //   305: ifnull +111 -> 416
    //   308: aload_0
    //   309: aload 10
    //   311: getfield 1271	android/view/inputmethod/EditorInfo:initialSelStart	I
    //   314: putfield 798	android/view/inputmethod/InputMethodManager:mCursorSelStart	I
    //   317: aload_0
    //   318: aload 10
    //   320: getfield 1274	android/view/inputmethod/EditorInfo:initialSelEnd	I
    //   323: putfield 802	android/view/inputmethod/InputMethodManager:mCursorSelEnd	I
    //   326: aload_0
    //   327: iconst_m1
    //   328: putfield 806	android/view/inputmethod/InputMethodManager:mCursorCandStart	I
    //   331: aload_0
    //   332: iconst_m1
    //   333: putfield 810	android/view/inputmethod/InputMethodManager:mCursorCandEnd	I
    //   336: aload_0
    //   337: getfield 173	android/view/inputmethod/InputMethodManager:mCursorRect	Landroid/graphics/Rect;
    //   340: invokevirtual 1277	android/graphics/Rect:setEmpty	()V
    //   343: aload_0
    //   344: aconst_null
    //   345: putfield 175	android/view/inputmethod/InputMethodManager:mCursorAnchorInfo	Landroid/view/inputmethod/CursorAnchorInfo;
    //   348: aload 11
    //   350: invokestatic 1283	android/view/inputmethod/InputConnectionInspector:getMissingMethodFlags	(Landroid/view/inputmethod/InputConnection;)I
    //   353: istore_3
    //   354: iload_3
    //   355: bipush 32
    //   357: iand
    //   358: ifeq +9 -> 367
    //   361: aconst_null
    //   362: astore 12
    //   364: goto +12 -> 376
    //   367: aload 11
    //   369: invokeinterface 1286 1 0
    //   374: astore 12
    //   376: new 8	android/view/inputmethod/InputMethodManager$ControlledInputConnectionWrapper
    //   379: astore 13
    //   381: aload 12
    //   383: ifnull +13 -> 396
    //   386: aload 12
    //   388: invokevirtual 975	android/os/Handler:getLooper	()Landroid/os/Looper;
    //   391: astore 12
    //   393: goto +10 -> 403
    //   396: aload 9
    //   398: invokevirtual 975	android/os/Handler:getLooper	()Landroid/os/Looper;
    //   401: astore 12
    //   403: aload 13
    //   405: aload 12
    //   407: aload 11
    //   409: aload_0
    //   410: invokespecial 217	android/view/inputmethod/InputMethodManager$ControlledInputConnectionWrapper:<init>	(Landroid/os/Looper;Landroid/view/inputmethod/InputConnection;Landroid/view/inputmethod/InputMethodManager;)V
    //   413: goto +8 -> 421
    //   416: aconst_null
    //   417: astore 13
    //   419: iconst_0
    //   420: istore_3
    //   421: aload_0
    //   422: aload 13
    //   424: putfield 271	android/view/inputmethod/InputMethodManager:mServedInputConnectionWrapper	Landroid/view/inputmethod/InputMethodManager$ControlledInputConnectionWrapper;
    //   427: aload_0
    //   428: getfield 205	android/view/inputmethod/InputMethodManager:mService	Lcom/android/internal/view/IInputMethodManager;
    //   431: astore 9
    //   433: aload_0
    //   434: getfield 196	android/view/inputmethod/InputMethodManager:mClient	Lcom/android/internal/view/IInputMethodClient$Stub;
    //   437: astore 11
    //   439: aload 7
    //   441: invokevirtual 403	android/view/View:getContext	()Landroid/content/Context;
    //   444: invokevirtual 1016	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   447: getfield 1021	android/content/pm/ApplicationInfo:targetSdkVersion	I
    //   450: istore 14
    //   452: aload_2
    //   453: astore 12
    //   455: aload 9
    //   457: iload_1
    //   458: aload 11
    //   460: aload 6
    //   462: iload 8
    //   464: iload 5
    //   466: iload 4
    //   468: aload 10
    //   470: aload 13
    //   472: iload_3
    //   473: iload 14
    //   475: invokeinterface 1025 11 0
    //   480: astore 13
    //   482: aload 13
    //   484: ifnonnull +105 -> 589
    //   487: new 367	java/lang/StringBuilder
    //   490: astore 13
    //   492: aload 13
    //   494: invokespecial 368	java/lang/StringBuilder:<init>	()V
    //   497: aload 13
    //   499: ldc_w 1288
    //   502: invokevirtual 377	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   505: pop
    //   506: aload 13
    //   508: iload_1
    //   509: invokestatic 1294	com/android/internal/inputmethod/InputMethodDebug:startInputReasonToString	(I)Ljava/lang/String;
    //   512: invokevirtual 377	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   515: pop
    //   516: aload 13
    //   518: ldc_w 1296
    //   521: invokevirtual 377	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   524: pop
    //   525: aload 12
    //   527: astore 6
    //   529: aload 13
    //   531: aload 10
    //   533: invokevirtual 372	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   536: pop
    //   537: aload 12
    //   539: astore 6
    //   541: aload 13
    //   543: ldc_w 1298
    //   546: invokevirtual 377	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   549: pop
    //   550: aload 12
    //   552: astore 6
    //   554: aload 13
    //   556: iload 8
    //   558: invokestatic 1301	com/android/internal/inputmethod/InputMethodDebug:startInputFlagsToString	(I)Ljava/lang/String;
    //   561: invokevirtual 377	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   564: pop
    //   565: aload 12
    //   567: astore 6
    //   569: ldc 78
    //   571: aload 13
    //   573: invokevirtual 387	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   576: invokestatic 1304	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;)I
    //   579: pop
    //   580: aload 12
    //   582: astore 6
    //   584: aload 12
    //   586: monitorexit
    //   587: iconst_0
    //   588: ireturn
    //   589: aload 12
    //   591: astore 6
    //   593: aload_0
    //   594: aload 13
    //   596: invokevirtual 1310	com/android/internal/view/InputBindResult:getActivityViewToScreenMatrix	()Landroid/graphics/Matrix;
    //   599: putfield 177	android/view/inputmethod/InputMethodManager:mActivityViewToScreenMatrix	Landroid/graphics/Matrix;
    //   602: aload 12
    //   604: astore 6
    //   606: aload 13
    //   608: getfield 1313	com/android/internal/view/InputBindResult:id	Ljava/lang/String;
    //   611: ifnull +58 -> 669
    //   614: aload 12
    //   616: astore 6
    //   618: aload_0
    //   619: aload 13
    //   621: getfield 1316	com/android/internal/view/InputBindResult:channel	Landroid/view/InputChannel;
    //   624: invokevirtual 659	android/view/inputmethod/InputMethodManager:setInputChannelLocked	(Landroid/view/InputChannel;)V
    //   627: aload 12
    //   629: astore 6
    //   631: aload_0
    //   632: aload 13
    //   634: getfield 1319	com/android/internal/view/InputBindResult:sequence	I
    //   637: putfield 179	android/view/inputmethod/InputMethodManager:mBindSequence	I
    //   640: aload 12
    //   642: astore 6
    //   644: aload_0
    //   645: aload 13
    //   647: getfield 1322	com/android/internal/view/InputBindResult:method	Lcom/android/internal/view/IInputMethodSession;
    //   650: putfield 663	android/view/inputmethod/InputMethodManager:mCurMethod	Lcom/android/internal/view/IInputMethodSession;
    //   653: aload 12
    //   655: astore 6
    //   657: aload_0
    //   658: aload 13
    //   660: getfield 1313	com/android/internal/view/InputBindResult:id	Ljava/lang/String;
    //   663: putfield 661	android/view/inputmethod/InputMethodManager:mCurId	Ljava/lang/String;
    //   666: goto +43 -> 709
    //   669: aload 12
    //   671: astore 6
    //   673: aload 13
    //   675: getfield 1316	com/android/internal/view/InputBindResult:channel	Landroid/view/InputChannel;
    //   678: ifnull +31 -> 709
    //   681: aload 12
    //   683: astore 6
    //   685: aload 13
    //   687: getfield 1316	com/android/internal/view/InputBindResult:channel	Landroid/view/InputChannel;
    //   690: aload_0
    //   691: getfield 1070	android/view/inputmethod/InputMethodManager:mCurChannel	Landroid/view/InputChannel;
    //   694: if_acmpeq +15 -> 709
    //   697: aload 12
    //   699: astore 6
    //   701: aload 13
    //   703: getfield 1316	com/android/internal/view/InputBindResult:channel	Landroid/view/InputChannel;
    //   706: invokevirtual 1157	android/view/InputChannel:dispose	()V
    //   709: aload 12
    //   711: astore 6
    //   713: aload 13
    //   715: getfield 1325	com/android/internal/view/InputBindResult:result	I
    //   718: bipush 11
    //   720: if_icmpeq +6 -> 726
    //   723: goto +12 -> 735
    //   726: aload 12
    //   728: astore 6
    //   730: aload_0
    //   731: iconst_1
    //   732: putfield 166	android/view/inputmethod/InputMethodManager:mRestartOnNextWindowFocus	Z
    //   735: aload 12
    //   737: astore 6
    //   739: aload_0
    //   740: getfield 663	android/view/inputmethod/InputMethodManager:mCurMethod	Lcom/android/internal/view/IInputMethodSession;
    //   743: ifnull +40 -> 783
    //   746: aload 12
    //   748: astore 6
    //   750: aload_0
    //   751: getfield 275	android/view/inputmethod/InputMethodManager:mCompletions	[Landroid/view/inputmethod/CompletionInfo;
    //   754: astore 13
    //   756: aload 13
    //   758: ifnull +25 -> 783
    //   761: aload 12
    //   763: astore 6
    //   765: aload_0
    //   766: getfield 663	android/view/inputmethod/InputMethodManager:mCurMethod	Lcom/android/internal/view/IInputMethodSession;
    //   769: aload_0
    //   770: getfield 275	android/view/inputmethod/InputMethodManager:mCompletions	[Landroid/view/inputmethod/CompletionInfo;
    //   773: invokeinterface 725 2 0
    //   778: goto +5 -> 783
    //   781: astore 6
    //   783: goto +110 -> 893
    //   786: astore 12
    //   788: goto +24 -> 812
    //   791: astore 12
    //   793: goto +35 -> 828
    //   796: astore_2
    //   797: goto +118 -> 915
    //   800: astore 12
    //   802: goto +10 -> 812
    //   805: astore 12
    //   807: goto +21 -> 828
    //   810: astore 12
    //   812: aload_2
    //   813: astore 6
    //   815: ldc 78
    //   817: aload 12
    //   819: invokestatic 1328	android/util/Log:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
    //   822: pop
    //   823: goto +70 -> 893
    //   826: astore 12
    //   828: aload_2
    //   829: astore 13
    //   831: aload 13
    //   833: astore 6
    //   835: new 367	java/lang/StringBuilder
    //   838: astore 7
    //   840: aload 13
    //   842: astore 6
    //   844: aload 7
    //   846: invokespecial 368	java/lang/StringBuilder:<init>	()V
    //   849: aload 13
    //   851: astore 6
    //   853: aload 7
    //   855: ldc_w 1067
    //   858: invokevirtual 377	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   861: pop
    //   862: aload 13
    //   864: astore 6
    //   866: aload 7
    //   868: aload_0
    //   869: getfield 661	android/view/inputmethod/InputMethodManager:mCurId	Ljava/lang/String;
    //   872: invokevirtual 377	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   875: pop
    //   876: aload 13
    //   878: astore 6
    //   880: ldc 78
    //   882: aload 7
    //   884: invokevirtual 387	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   887: aload 12
    //   889: invokestatic 510	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   892: pop
    //   893: aload_2
    //   894: astore 6
    //   896: aload_2
    //   897: monitorexit
    //   898: iconst_1
    //   899: ireturn
    //   900: aload_2
    //   901: astore 6
    //   903: aload_2
    //   904: monitorexit
    //   905: iconst_0
    //   906: ireturn
    //   907: astore 6
    //   909: aload_2
    //   910: astore 12
    //   912: aload 6
    //   914: astore_2
    //   915: aload 12
    //   917: astore 6
    //   919: aload 12
    //   921: monitorexit
    //   922: aload_2
    //   923: athrow
    //   924: astore_2
    //   925: aload 6
    //   927: astore 12
    //   929: goto -14 -> 915
    //   932: astore_2
    //   933: aload 6
    //   935: monitorexit
    //   936: aload_2
    //   937: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	938	0	this	InputMethodManager
    //   0	938	1	paramInt1	int
    //   0	938	2	paramIBinder	IBinder
    //   0	938	3	paramInt2	int
    //   0	938	4	paramInt3	int
    //   0	938	5	paramInt4	int
    //   4	115	6	localObject1	Object
    //   292	169	6	localIBinder	IBinder
    //   527	237	6	localObject2	Object
    //   781	1	6	localRemoteException1	RemoteException
    //   813	89	6	localObject3	Object
    //   907	6	6	localObject4	Object
    //   917	17	6	localObject5	Object
    //   13	870	7	localObject6	Object
    //   107	450	8	i	int
    //   125	331	9	localObject7	Object
    //   173	359	10	localEditorInfo	EditorInfo
    //   205	254	11	localObject8	Object
    //   270	492	12	localObject9	Object
    //   786	1	12	localIllegalArgumentException1	IllegalArgumentException
    //   791	1	12	localRemoteException2	RemoteException
    //   800	1	12	localIllegalArgumentException2	IllegalArgumentException
    //   805	1	12	localRemoteException3	RemoteException
    //   810	8	12	localIllegalArgumentException3	IllegalArgumentException
    //   826	62	12	localRemoteException4	RemoteException
    //   910	18	12	localObject10	Object
    //   379	498	13	localObject11	Object
    //   450	24	14	j	int
    // Exception table:
    //   from	to	target	type
    //   277	289	292	finally
    //   308	354	292	finally
    //   367	376	292	finally
    //   376	381	292	finally
    //   386	393	292	finally
    //   396	403	292	finally
    //   403	413	292	finally
    //   765	778	781	android/os/RemoteException
    //   529	537	786	java/lang/IllegalArgumentException
    //   541	550	786	java/lang/IllegalArgumentException
    //   554	565	786	java/lang/IllegalArgumentException
    //   569	580	786	java/lang/IllegalArgumentException
    //   593	602	786	java/lang/IllegalArgumentException
    //   606	614	786	java/lang/IllegalArgumentException
    //   618	627	786	java/lang/IllegalArgumentException
    //   631	640	786	java/lang/IllegalArgumentException
    //   644	653	786	java/lang/IllegalArgumentException
    //   657	666	786	java/lang/IllegalArgumentException
    //   673	681	786	java/lang/IllegalArgumentException
    //   685	697	786	java/lang/IllegalArgumentException
    //   701	709	786	java/lang/IllegalArgumentException
    //   713	723	786	java/lang/IllegalArgumentException
    //   730	735	786	java/lang/IllegalArgumentException
    //   739	746	786	java/lang/IllegalArgumentException
    //   750	756	786	java/lang/IllegalArgumentException
    //   765	778	786	java/lang/IllegalArgumentException
    //   529	537	791	android/os/RemoteException
    //   541	550	791	android/os/RemoteException
    //   554	565	791	android/os/RemoteException
    //   569	580	791	android/os/RemoteException
    //   593	602	791	android/os/RemoteException
    //   606	614	791	android/os/RemoteException
    //   618	627	791	android/os/RemoteException
    //   631	640	791	android/os/RemoteException
    //   644	653	791	android/os/RemoteException
    //   657	666	791	android/os/RemoteException
    //   673	681	791	android/os/RemoteException
    //   685	697	791	android/os/RemoteException
    //   701	709	791	android/os/RemoteException
    //   713	723	791	android/os/RemoteException
    //   730	735	791	android/os/RemoteException
    //   739	746	791	android/os/RemoteException
    //   750	756	791	android/os/RemoteException
    //   455	482	796	finally
    //   487	525	796	finally
    //   455	482	800	java/lang/IllegalArgumentException
    //   487	525	800	java/lang/IllegalArgumentException
    //   455	482	805	android/os/RemoteException
    //   487	525	805	android/os/RemoteException
    //   427	452	810	java/lang/IllegalArgumentException
    //   427	452	826	android/os/RemoteException
    //   214	230	907	finally
    //   236	243	907	finally
    //   249	272	907	finally
    //   421	427	907	finally
    //   427	452	907	finally
    //   529	537	924	finally
    //   541	550	924	finally
    //   554	565	924	finally
    //   569	580	924	finally
    //   584	587	924	finally
    //   593	602	924	finally
    //   606	614	924	finally
    //   618	627	924	finally
    //   631	640	924	finally
    //   644	653	924	finally
    //   657	666	924	finally
    //   673	681	924	finally
    //   685	697	924	finally
    //   701	709	924	finally
    //   713	723	924	finally
    //   730	735	924	finally
    //   739	746	924	finally
    //   750	756	924	finally
    //   765	778	924	finally
    //   815	823	924	finally
    //   835	840	924	finally
    //   844	849	924	finally
    //   853	862	924	finally
    //   866	876	924	finally
    //   880	893	924	finally
    //   896	898	924	finally
    //   903	905	924	finally
    //   919	922	924	finally
    //   9	15	932	finally
    //   20	23	932	finally
    //   25	28	932	finally
    //   933	936	932	finally
  }
  
  @Deprecated
  public boolean switchToLastInputMethod(IBinder paramIBinder)
  {
    return InputMethodPrivilegedOperationsRegistry.get(paramIBinder).switchToPreviousInputMethod();
  }
  
  @Deprecated
  public boolean switchToNextInputMethod(IBinder paramIBinder, boolean paramBoolean)
  {
    return InputMethodPrivilegedOperationsRegistry.get(paramIBinder).switchToNextInputMethod(paramBoolean);
  }
  
  public void toggleSoftInput(int paramInt1, int paramInt2)
  {
    IInputMethodSession localIInputMethodSession = this.mCurMethod;
    if (localIInputMethodSession != null) {
      try
      {
        localIInputMethodSession.toggleSoftInput(paramInt1, paramInt2);
      }
      catch (RemoteException localRemoteException) {}
    }
  }
  
  public void toggleSoftInputFromWindow(IBinder paramIBinder, int paramInt1, int paramInt2)
  {
    synchronized (this.mH)
    {
      if ((this.mServedView != null) && (this.mServedView.getWindowToken() == paramIBinder))
      {
        paramIBinder = this.mCurMethod;
        if (paramIBinder != null) {
          try
          {
            this.mCurMethod.toggleSoftInput(paramInt1, paramInt2);
          }
          catch (RemoteException paramIBinder) {}
        }
        return;
      }
      return;
    }
  }
  
  public void unregisterImeConsumer(ImeInsetsSourceConsumer paramImeInsetsSourceConsumer)
  {
    if (paramImeInsetsSourceConsumer != null) {
      synchronized (this.mH)
      {
        if (this.mImeInsetsConsumer == paramImeInsetsSourceConsumer) {
          this.mImeInsetsConsumer = null;
        }
        return;
      }
    }
    throw new IllegalStateException("ImeInsetsSourceConsumer cannot be null.");
  }
  
  @Deprecated
  public void updateCursor(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    ??? = getFallbackInputMethodManagerIfNecessary(paramView);
    if (??? != null)
    {
      ((InputMethodManager)???).updateCursor(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    checkFocus();
    synchronized (this.mH)
    {
      if (((this.mServedView == paramView) || ((this.mServedView != null) && (this.mServedView.checkInputConnectionProxy(paramView)))) && (this.mCurrentTextBoxAttribute != null) && (this.mCurMethod != null))
      {
        this.mTmpCursorRect.set(paramInt1, paramInt2, paramInt3, paramInt4);
        boolean bool = this.mCursorRect.equals(this.mTmpCursorRect);
        if (!bool) {
          try
          {
            this.mCurMethod.updateCursor(this.mTmpCursorRect);
            this.mCursorRect.set(this.mTmpCursorRect);
          }
          catch (RemoteException localRemoteException)
          {
            paramView = new java/lang/StringBuilder;
            paramView.<init>();
            paramView.append("IME died: ");
            paramView.append(this.mCurId);
            Log.w("InputMethodManager", paramView.toString(), localRemoteException);
          }
        }
        return;
      }
      return;
    }
  }
  
  public void updateCursorAnchorInfo(View paramView, CursorAnchorInfo paramCursorAnchorInfo)
  {
    if ((paramView != null) && (paramCursorAnchorInfo != null))
    {
      ??? = getFallbackInputMethodManagerIfNecessary(paramView);
      if (??? != null)
      {
        ((InputMethodManager)???).updateCursorAnchorInfo(paramView, paramCursorAnchorInfo);
        return;
      }
      checkFocus();
      synchronized (this.mH)
      {
        if (((this.mServedView == paramView) || ((this.mServedView != null) && (this.mServedView.checkInputConnectionProxy(paramView)))) && (this.mCurrentTextBoxAttribute != null) && (this.mCurMethod != null))
        {
          int i = this.mRequestUpdateCursorAnchorInfoMonitorMode;
          int j = 1;
          if ((i & 0x1) == 0) {
            j = 0;
          }
          if ((j == 0) && (Objects.equals(this.mCursorAnchorInfo, paramCursorAnchorInfo))) {
            return;
          }
          try
          {
            if (this.mActivityViewToScreenMatrix != null) {
              this.mCurMethod.updateCursorAnchorInfo(CursorAnchorInfo.createForAdditionalParentMatrix(paramCursorAnchorInfo, this.mActivityViewToScreenMatrix));
            } else {
              this.mCurMethod.updateCursorAnchorInfo(paramCursorAnchorInfo);
            }
            this.mCursorAnchorInfo = paramCursorAnchorInfo;
            this.mRequestUpdateCursorAnchorInfoMonitorMode &= 0xFFFFFFFE;
          }
          catch (RemoteException paramView)
          {
            paramCursorAnchorInfo = new java/lang/StringBuilder;
            paramCursorAnchorInfo.<init>();
            paramCursorAnchorInfo.append("IME died: ");
            paramCursorAnchorInfo.append(this.mCurId);
            Log.w("InputMethodManager", paramCursorAnchorInfo.toString(), paramView);
          }
          return;
        }
        return;
      }
    }
  }
  
  public void updateExtractedText(View paramView, int paramInt, ExtractedText paramExtractedText)
  {
    ??? = getFallbackInputMethodManagerIfNecessary(paramView);
    if (??? != null)
    {
      ((InputMethodManager)???).updateExtractedText(paramView, paramInt, paramExtractedText);
      return;
    }
    checkFocus();
    synchronized (this.mH)
    {
      if ((this.mServedView != paramView) && ((this.mServedView == null) || (!this.mServedView.checkInputConnectionProxy(paramView)))) {
        return;
      }
      paramView = this.mCurMethod;
      if (paramView != null) {
        try
        {
          this.mCurMethod.updateExtractedText(paramInt, paramExtractedText);
        }
        catch (RemoteException paramView) {}
      }
      return;
    }
  }
  
  public void updateSelection(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    ??? = getFallbackInputMethodManagerIfNecessary(paramView);
    if (??? != null)
    {
      ((InputMethodManager)???).updateSelection(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    checkFocus();
    synchronized (this.mH)
    {
      if (((this.mServedView == paramView) || ((this.mServedView != null) && (this.mServedView.checkInputConnectionProxy(paramView)))) && (this.mCurrentTextBoxAttribute != null) && (this.mCurMethod != null))
      {
        int i;
        if ((this.mCursorSelStart == paramInt1) && (this.mCursorSelEnd == paramInt2) && (this.mCursorCandStart == paramInt3))
        {
          i = this.mCursorCandEnd;
          if (i == paramInt4) {}
        }
        else
        {
          try
          {
            i = this.mCursorSelStart;
            int j = this.mCursorSelEnd;
            this.mCursorSelStart = paramInt1;
            this.mCursorSelEnd = paramInt2;
            this.mCursorCandStart = paramInt3;
            this.mCursorCandEnd = paramInt4;
            this.mCurMethod.updateSelection(i, j, paramInt1, paramInt2, paramInt3, paramInt4);
          }
          catch (RemoteException localRemoteException)
          {
            paramView = new java/lang/StringBuilder;
            paramView.<init>();
            paramView.append("IME died: ");
            paramView.append(this.mCurId);
            Log.w("InputMethodManager", paramView.toString(), localRemoteException);
          }
        }
        return;
      }
      return;
    }
  }
  
  @Deprecated
  public void viewClicked(View paramView)
  {
    ??? = getFallbackInputMethodManagerIfNecessary(paramView);
    if (??? != null)
    {
      ((InputMethodManager)???).viewClicked(paramView);
      return;
    }
    boolean bool;
    if (this.mServedView != this.mNextServedView) {
      bool = true;
    } else {
      bool = false;
    }
    checkFocus();
    synchronized (this.mH)
    {
      if (((this.mServedView == paramView) || ((this.mServedView != null) && (this.mServedView.checkInputConnectionProxy(paramView)))) && (this.mCurrentTextBoxAttribute != null))
      {
        paramView = this.mCurMethod;
        if (paramView != null)
        {
          try
          {
            this.mCurMethod.viewClicked(bool);
          }
          catch (RemoteException paramView)
          {
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            localStringBuilder.append("IME died: ");
            localStringBuilder.append(this.mCurId);
            Log.w("InputMethodManager", localStringBuilder.toString(), paramView);
          }
          return;
        }
      }
      return;
    }
  }
  
  @UnsupportedAppUsage
  public void windowDismissed(IBinder paramIBinder)
  {
    checkFocus();
    synchronized (this.mH)
    {
      if ((this.mServedView != null) && (this.mServedView.getWindowToken() == paramIBinder)) {
        finishInputLocked();
      }
      if ((this.mCurRootView != null) && (this.mCurRootView.getWindowToken() == paramIBinder)) {
        this.mCurRootView = null;
      }
      return;
    }
  }
  
  private static class ControlledInputConnectionWrapper
    extends IInputConnectionWrapper
  {
    private final InputMethodManager mParentInputMethodManager;
    
    public ControlledInputConnectionWrapper(Looper paramLooper, InputConnection paramInputConnection, InputMethodManager paramInputMethodManager)
    {
      super(paramInputConnection);
      this.mParentInputMethodManager = paramInputMethodManager;
    }
    
    void deactivate()
    {
      if (isFinished()) {
        return;
      }
      closeConnection();
    }
    
    public boolean isActive()
    {
      boolean bool;
      if ((this.mParentInputMethodManager.mActive) && (!isFinished())) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("ControlledInputConnectionWrapper{connection=");
      localStringBuilder.append(getInputConnection());
      localStringBuilder.append(" finished=");
      localStringBuilder.append(isFinished());
      localStringBuilder.append(" mParentInputMethodManager.mActive=");
      localStringBuilder.append(this.mParentInputMethodManager.mActive);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
  }
  
  public static abstract interface FinishedInputEventCallback
  {
    public abstract void onFinishedInputEvent(Object paramObject, boolean paramBoolean);
  }
  
  class H
    extends Handler
  {
    H(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message arg1)
    {
      int i = ???.what;
      boolean bool1 = true;
      boolean bool2 = true;
      int j = 1;
      boolean bool3 = false;
      if (i != 10)
      {
        if (i != 15)
        {
          Object localObject6;
          if (i != 20)
          {
            if (i != 30)
            {
              switch (i)
              {
              default: 
                return;
              case 7: 
                InputMethodManager.this.finishedInputEvent(???.arg1, false, false);
                return;
              case 6: 
                InputMethodManager.this.finishedInputEvent(???.arg1, false, true);
                return;
              case 5: 
                InputMethodManager.this.sendInputEventAndReportResultOnMainLooper((InputMethodManager.PendingEvent)???.obj);
                return;
              case 4: 
                if (???.arg1 != 0) {
                  bool1 = true;
                } else {
                  bool1 = false;
                }
                if (???.arg2 != 0) {
                  bool3 = true;
                }
                synchronized (InputMethodManager.this.mH)
                {
                  InputMethodManager.this.mActive = bool1;
                  InputMethodManager.this.mFullscreenMode = bool3;
                  if (!bool1)
                  {
                    InputMethodManager.this.mRestartOnNextWindowFocus = true;
                    try
                    {
                      InputMethodManager.this.mIInputContext.finishComposingText();
                    }
                    catch (RemoteException localRemoteException1) {}
                  }
                  if ((InputMethodManager.this.mServedView != null) && (InputMethodManager.canStartInput(InputMethodManager.this.mServedView)) && (InputMethodManager.this.checkFocusNoStartInput(InputMethodManager.this.mRestartOnNextWindowFocus)))
                  {
                    if (bool1) {
                      j = 7;
                    } else {
                      j = 8;
                    }
                    InputMethodManager.this.startInputInner(j, null, 0, 0, 0);
                  }
                  return;
                }
              case 3: 
                j = ???.arg1;
                i = ???.arg2;
                synchronized (InputMethodManager.this.mH)
                {
                  if (InputMethodManager.this.mBindSequence != j) {
                    return;
                  }
                  InputMethodManager.this.clearBindingLocked();
                  if ((InputMethodManager.this.mServedView != null) && (InputMethodManager.this.mServedView.isFocused())) {
                    InputMethodManager.this.mServedConnecting = true;
                  }
                  bool1 = InputMethodManager.this.mActive;
                  if (bool1) {
                    InputMethodManager.this.startInputInner(6, null, 0, 0, 0);
                  }
                  return;
                }
              case 2: 
                ??? = (InputBindResult)???.obj;
                synchronized (InputMethodManager.this.mH)
                {
                  if ((InputMethodManager.this.mBindSequence >= 0) && (InputMethodManager.this.mBindSequence == ((InputBindResult)???).sequence))
                  {
                    InputMethodManager.access$002(InputMethodManager.this, 0);
                    InputMethodManager.this.setInputChannelLocked(((InputBindResult)???).channel);
                    InputMethodManager.this.mCurMethod = ((InputBindResult)???).method;
                    InputMethodManager.this.mCurId = ((InputBindResult)???).id;
                    InputMethodManager.this.mBindSequence = ((InputBindResult)???).sequence;
                    InputMethodManager.access$102(InputMethodManager.this, ((InputBindResult)???).getActivityViewToScreenMatrix());
                    InputMethodManager.this.startInputInner(5, null, 0, 0, 0);
                    return;
                  }
                  StringBuilder localStringBuilder1 = new java/lang/StringBuilder;
                  localStringBuilder1.<init>();
                  localStringBuilder1.append("Ignoring onBind: cur seq=");
                  localStringBuilder1.append(InputMethodManager.this.mBindSequence);
                  localStringBuilder1.append(", given seq=");
                  localStringBuilder1.append(((InputBindResult)???).sequence);
                  Log.w("InputMethodManager", localStringBuilder1.toString());
                  if ((((InputBindResult)???).channel != null) && (((InputBindResult)???).channel != InputMethodManager.this.mCurChannel)) {
                    ((InputBindResult)???).channel.dispose();
                  }
                  return;
                }
              }
              ??? = (SomeArgs)???.obj;
              try
              {
                InputMethodManager.this.doDump((FileDescriptor)???.arg1, (PrintWriter)???.arg2, (String[])???.arg3);
              }
              catch (RuntimeException localRuntimeException)
              {
                ??? = (PrintWriter)???.arg2;
                StringBuilder localStringBuilder2 = new StringBuilder();
                localStringBuilder2.append("Exception: ");
                localStringBuilder2.append(localRuntimeException);
                ((PrintWriter)???).println(localStringBuilder2.toString());
              }
              synchronized (???.arg4)
              {
                ((CountDownLatch)???.arg4).countDown();
                ???.recycle();
                return;
              }
            }
            ??? = (float[])???.obj;
            i = ???.arg1;
            synchronized (InputMethodManager.this.mH)
            {
              if (InputMethodManager.this.mBindSequence != i) {
                return;
              }
              if (??? == null)
              {
                InputMethodManager.access$102(InputMethodManager.this, null);
                return;
              }
              localObject6 = new float[9];
              InputMethodManager.this.mActivityViewToScreenMatrix.getValues((float[])localObject6);
              if (Arrays.equals((float[])localObject6, (float[])???)) {
                return;
              }
              InputMethodManager.this.mActivityViewToScreenMatrix.setValues((float[])???);
              if ((InputMethodManager.this.mCursorAnchorInfo != null) && (InputMethodManager.this.mCurMethod != null) && (InputMethodManager.this.mServedInputConnectionWrapper != null))
              {
                if ((InputMethodManager.this.mRequestUpdateCursorAnchorInfoMonitorMode & 0x2) == 0) {
                  j = 0;
                }
                if (j == 0) {
                  return;
                }
                try
                {
                  InputMethodManager.this.mCurMethod.updateCursorAnchorInfo(CursorAnchorInfo.createForAdditionalParentMatrix(InputMethodManager.this.mCursorAnchorInfo, InputMethodManager.this.mActivityViewToScreenMatrix));
                }
                catch (RemoteException localRemoteException2)
                {
                  localObject6 = new java/lang/StringBuilder;
                  ((StringBuilder)localObject6).<init>();
                  ((StringBuilder)localObject6).append("IME died: ");
                  ((StringBuilder)localObject6).append(InputMethodManager.this.mCurId);
                  Log.w("InputMethodManager", ((StringBuilder)localObject6).toString(), localRemoteException2);
                }
                return;
              }
              return;
            }
          }
          synchronized (InputMethodManager.this.mH)
          {
            if (InputMethodManager.this.mImeInsetsConsumer != null)
            {
              localObject6 = InputMethodManager.this.mImeInsetsConsumer;
              if (???.arg1 == 0) {
                bool1 = false;
              }
              ((ImeInsetsSourceConsumer)localObject6).applyImeVisibility(bool1);
            }
            return;
          }
        }
        synchronized (InputMethodManager.this.mH)
        {
          if (InputMethodManager.this.mImeInsetsConsumer != null) {
            InputMethodManager.this.mImeInsetsConsumer.onPreRendered((EditorInfo)???.obj);
          }
          return;
        }
      }
      if (???.arg1 != 0) {
        bool1 = bool2;
      } else {
        bool1 = false;
      }
      ??? = null;
      synchronized (InputMethodManager.this.mH)
      {
        InputMethodManager.this.mFullscreenMode = bool1;
        if (InputMethodManager.this.mServedInputConnectionWrapper != null) {
          ??? = InputMethodManager.this.mServedInputConnectionWrapper.getInputConnection();
        }
        if (??? != null) {
          ???.reportFullscreenMode(bool1);
        }
        return;
      }
    }
  }
  
  private final class ImeInputEventSender
    extends InputEventSender
  {
    public ImeInputEventSender(InputChannel paramInputChannel, Looper paramLooper)
    {
      super(paramLooper);
    }
    
    public void onInputEventFinished(int paramInt, boolean paramBoolean)
    {
      InputMethodManager.this.finishedInputEvent(paramInt, paramBoolean, false);
    }
  }
  
  private final class PendingEvent
    implements Runnable
  {
    public InputMethodManager.FinishedInputEventCallback mCallback;
    public InputEvent mEvent;
    public boolean mHandled;
    public Handler mHandler;
    public String mInputMethodId;
    public Object mToken;
    
    private PendingEvent() {}
    
    public void recycle()
    {
      this.mEvent = null;
      this.mToken = null;
      this.mInputMethodId = null;
      this.mCallback = null;
      this.mHandler = null;
      this.mHandled = false;
    }
    
    public void run()
    {
      this.mCallback.onFinishedInputEvent(this.mToken, this.mHandled);
      synchronized (InputMethodManager.this.mH)
      {
        InputMethodManager.this.recyclePendingEventLocked(this);
        return;
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/InputMethodManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */