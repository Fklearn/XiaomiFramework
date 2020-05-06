package android.view;

import android.animation.LayoutTransition;
import android.annotation.UnsupportedAppUsage;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.ResourcesManager;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.res.CompatibilityInfo;
import android.content.res.CompatibilityInfo.Translator;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.FrameInfo;
import android.graphics.HardwareRenderer.FrameDrawingCallback;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.RenderNode;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.hardware.input.InputManager;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.sysprop.DisplayProperties;
import android.util.AndroidRuntimeException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LongArray;
import android.util.MergedConfiguration;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener;
import android.view.accessibility.AccessibilityManager.HighTextContrastChangeListener;
import android.view.accessibility.AccessibilityNodeIdManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.accessibility.IAccessibilityInteractionConnection.Stub;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.autofill.AutofillManager;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodManager.FinishedInputEventCallback;
import android.widget.Scroller;
import com.android.internal.R.styleable;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.IResultReceiver;
import com.android.internal.os.SomeArgs;
import com.android.internal.policy.DecorView;
import com.android.internal.util.Preconditions;
import com.android.internal.view.BaseSurfaceHolder;
import com.android.internal.view.RootViewSurfaceTaker;
import com.android.internal.view.SurfaceCallbackHelper;
import com.miui.internal.contentcatcher.IInterceptor;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

public final class ViewRootImpl
  implements ViewParent, View.AttachInfo.Callbacks, ThreadedRenderer.DrawCallbacks
{
  private static final boolean DBG = false;
  private static final boolean DEBUG_CONFIGURATION = false;
  private static final boolean DEBUG_CONTENT_CAPTURE = false;
  private static final boolean DEBUG_DIALOG = false;
  private static final boolean DEBUG_DRAW = false;
  private static final boolean DEBUG_FPS = false;
  private static final boolean DEBUG_IMF = false;
  private static final boolean DEBUG_INPUT_RESIZE = false;
  private static final boolean DEBUG_INPUT_STAGES = false;
  private static final boolean DEBUG_KEEP_SCREEN_ON = false;
  private static final boolean DEBUG_LAYOUT = false;
  private static final boolean DEBUG_ORIENTATION = false;
  private static final boolean DEBUG_TRACKBALL = false;
  private static final boolean LOCAL_LOGV = false;
  private static final int MAX_QUEUED_INPUT_EVENT_POOL_SIZE = 10;
  static final int MAX_TRACKBALL_DELAY = 250;
  private static final int MSG_CAST_MODE = 1000;
  private static final int MSG_CHECK_FOCUS = 13;
  private static final int MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST = 21;
  private static final int MSG_CLOSE_SYSTEM_DIALOGS = 14;
  private static final int MSG_DIE = 3;
  private static final int MSG_DISPATCH_APP_VISIBILITY = 8;
  private static final int MSG_DISPATCH_DRAG_EVENT = 15;
  private static final int MSG_DISPATCH_DRAG_LOCATION_EVENT = 16;
  private static final int MSG_DISPATCH_GET_NEW_SURFACE = 9;
  private static final int MSG_DISPATCH_INPUT_EVENT = 7;
  private static final int MSG_DISPATCH_KEY_FROM_AUTOFILL = 12;
  private static final int MSG_DISPATCH_KEY_FROM_IME = 11;
  private static final int MSG_DISPATCH_SYSTEM_UI_VISIBILITY = 17;
  private static final int MSG_DISPATCH_WINDOW_SHOWN = 25;
  private static final int MSG_DRAW_FINISHED = 29;
  private static final int MSG_INSETS_CHANGED = 30;
  private static final int MSG_INSETS_CONTROL_CHANGED = 31;
  private static final int MSG_INVALIDATE = 1;
  private static final int MSG_INVALIDATE_RECT = 2;
  private static final int MSG_INVALIDATE_WORLD = 22;
  private static final int MSG_POINTER_CAPTURE_CHANGED = 28;
  private static final int MSG_PROCESS_INPUT_EVENTS = 19;
  private static final int MSG_REQUEST_KEYBOARD_SHORTCUTS = 26;
  private static final int MSG_RESIZED = 4;
  private static final int MSG_RESIZED_REPORT = 5;
  private static final int MSG_SYNTHESIZE_INPUT_EVENT = 24;
  private static final int MSG_SYSTEM_GESTURE_EXCLUSION_CHANGED = 32;
  private static final int MSG_UPDATE_CONFIGURATION = 18;
  private static final int MSG_UPDATE_POINTER_ICON = 27;
  private static final int MSG_WINDOW_FOCUS_CHANGED = 6;
  private static final int MSG_WINDOW_MOVED = 23;
  private static final boolean MT_RENDERER_AVAILABLE = true;
  public static final int NEW_INSETS_MODE_FULL = 2;
  public static final int NEW_INSETS_MODE_IME = 1;
  public static final int NEW_INSETS_MODE_NONE = 0;
  public static final String PROPERTY_EMULATOR_WIN_OUTSET_BOTTOM_PX = "ro.emu.win_outset_bottom_px";
  private static final String PROPERTY_PROFILE_RENDERING = "viewroot.profile_rendering";
  private static final String TAG = "ViewRootImpl";
  private static final String USE_NEW_INSETS_PROPERTY = "persist.wm.new_insets";
  private static final ArrayList<CastProjectionCallback> mCastProjectionCallbacks = new ArrayList();
  static final Interpolator mResizeInterpolator;
  private static boolean sAlwaysAssignFocus;
  private static boolean sCompatibilityDone;
  private static final ArrayList<ConfigChangedCallback> sConfigCallbacks;
  static boolean sFirstDrawComplete;
  static final ArrayList<Runnable> sFirstDrawHandlers;
  public static int sNewInsetsMode = SystemProperties.getInt("persist.wm.new_insets", 0);
  @UnsupportedAppUsage
  static final ThreadLocal<HandlerActionQueue> sRunQueues = new ThreadLocal();
  View mAccessibilityFocusedHost;
  AccessibilityNodeInfo mAccessibilityFocusedVirtualView;
  final AccessibilityInteractionConnectionManager mAccessibilityInteractionConnectionManager;
  AccessibilityInteractionController mAccessibilityInteractionController;
  final AccessibilityManager mAccessibilityManager;
  private ActivityConfigCallback mActivityConfigCallback;
  private boolean mActivityRelaunched;
  @UnsupportedAppUsage
  boolean mAdded;
  boolean mAddedTouchMode;
  private boolean mAppVisibilityChanged;
  boolean mAppVisible;
  boolean mApplyInsetsRequested;
  @UnsupportedAppUsage
  final View.AttachInfo mAttachInfo;
  AudioManager mAudioManager;
  final String mBasePackageName;
  public final Surface mBoundsSurface;
  private SurfaceControl mBoundsSurfaceControl;
  private int mCanvasOffsetX;
  private int mCanvasOffsetY;
  boolean mCastModeChanged;
  Choreographer mChoreographer;
  int mClientWindowLayoutFlags;
  final ConsumeBatchedInputImmediatelyRunnable mConsumeBatchedInputImmediatelyRunnable;
  boolean mConsumeBatchedInputImmediatelyScheduled;
  boolean mConsumeBatchedInputScheduled;
  final ConsumeBatchedInputRunnable mConsumedBatchedInputRunnable;
  @UnsupportedAppUsage
  public final Context mContext;
  int mCurScrollY;
  View mCurrentDragView;
  private PointerIcon mCustomPointerIcon;
  private final int mDensity;
  @UnsupportedAppUsage
  Rect mDirty;
  final Rect mDispatchContentInsets;
  DisplayCutout mDispatchDisplayCutout;
  final Rect mDispatchStableInsets;
  Display mDisplay;
  private final DisplayManager.DisplayListener mDisplayListener;
  final DisplayManager mDisplayManager;
  ClipDescription mDragDescription;
  final PointF mDragPoint;
  private boolean mDragResizing;
  boolean mDrawingAllowed;
  int mDrawsNeededToReport;
  @UnsupportedAppUsage
  FallbackEventHandler mFallbackEventHandler;
  boolean mFirst;
  InputStage mFirstInputStage;
  InputStage mFirstPostImeInputStage;
  private boolean mForceDecorViewVisibility;
  private boolean mForceNextConfigUpdate;
  boolean mForceNextWindowRelayout;
  private int mFpsNumFrames;
  private long mFpsPrevTime;
  private long mFpsStartTime;
  boolean mFullRedrawNeeded;
  private final GestureExclusionTracker mGestureExclusionTracker;
  boolean mHadWindowFocus;
  final ViewRootHandler mHandler;
  boolean mHandlingLayoutInLayoutRequest;
  int mHardwareXOffset;
  int mHardwareYOffset;
  boolean mHasHadWindowFocus;
  boolean mHaveMoveEvent;
  @UnsupportedAppUsage
  int mHeight;
  final HighContrastTextManager mHighContrastTextManager;
  private boolean mInLayout;
  InputChannel mInputChannel;
  private final InputEventCompatProcessor mInputCompatProcessor;
  protected final InputEventConsistencyVerifier mInputEventConsistencyVerifier;
  WindowInputEventReceiver mInputEventReceiver;
  InputQueue mInputQueue;
  InputQueue.Callback mInputQueueCallback;
  private final InsetsController mInsetsController;
  final InvalidateOnAnimationRunnable mInvalidateOnAnimationRunnable;
  private boolean mInvalidateRootRequested;
  boolean mIsAmbientMode;
  public boolean mIsAnimating;
  boolean mIsCastMode;
  boolean mIsCastModeRotationChanged;
  boolean mIsCreating;
  boolean mIsDrawing;
  boolean mIsInTraversal;
  boolean mIsProjectionMode;
  private final Configuration mLastConfigurationFromResources;
  final ViewTreeObserver.InternalInsetsInfo mLastGivenInsets;
  boolean mLastInCompatMode;
  boolean mLastOverscanRequested;
  private final MergedConfiguration mLastReportedMergedConfiguration;
  @UnsupportedAppUsage
  WeakReference<View> mLastScrolledFocus;
  int mLastSystemUiVisibility;
  final PointF mLastTouchPoint;
  int mLastTouchSource;
  boolean mLastWasImTarget;
  private WindowInsets mLastWindowInsets;
  boolean mLayoutRequested;
  ArrayList<View> mLayoutRequesters;
  volatile Object mLocalDragState;
  final WindowLeaked mLocation;
  boolean mLostWindowFocus;
  boolean mNeedUpdateBlurCrop;
  private boolean mNeedsRendererSetup;
  boolean mNewSurfaceNeeded;
  private final int mNoncompatDensity;
  int mOrigWindowType;
  boolean mPausedForTransition;
  boolean mPendingAlwaysConsumeSystemBars;
  final Rect mPendingBackDropFrame;
  final Rect mPendingContentInsets;
  final DisplayCutout.ParcelableWrapper mPendingDisplayCutout;
  int mPendingInputEventCount;
  QueuedInputEvent mPendingInputEventHead;
  String mPendingInputEventQueueLengthCounterName;
  QueuedInputEvent mPendingInputEventTail;
  private final MergedConfiguration mPendingMergedConfiguration;
  final Rect mPendingOutsets;
  final Rect mPendingOverscanInsets;
  final Rect mPendingStableInsets;
  private ArrayList<LayoutTransition> mPendingTransitions;
  final Rect mPendingVisibleInsets;
  boolean mPointerCapture;
  private int mPointerIconType;
  final Region mPreviousTransparentRegion;
  boolean mProcessInputEventsScheduled;
  private boolean mProfile;
  private boolean mProfileRendering;
  boolean mProjectionModeChanged;
  private QueuedInputEvent mQueuedInputEventPool;
  private int mQueuedInputEventPoolSize;
  private boolean mRemoved;
  private Choreographer.FrameCallback mRenderProfiler;
  private boolean mRenderProfilingEnabled;
  boolean mReportNextDraw;
  private int mResizeMode;
  boolean mScrollMayChange;
  int mScrollY;
  Scroller mScroller;
  SendWindowContentChangedAccessibilityEvent mSendWindowContentChangedAccessibilityEvent;
  int mSeq;
  int mSoftInputMode;
  @UnsupportedAppUsage
  boolean mStopped;
  @UnsupportedAppUsage
  public final Surface mSurface;
  private final SurfaceControl mSurfaceControl;
  BaseSurfaceHolder mSurfaceHolder;
  SurfaceHolder.Callback2 mSurfaceHolderCallback;
  private SurfaceSession mSurfaceSession;
  private int mSurfaceViewCount;
  InputStage mSyntheticInputStage;
  private String mTag;
  final int mTargetSdkVersion;
  private final Rect mTempBoundsRect;
  HashSet<View> mTempHashSet;
  private InsetsState mTempInsets;
  final Rect mTempRect;
  final Thread mThread;
  final Rect mTmpFrame;
  final int[] mTmpLocation;
  final TypedValue mTmpValue;
  private final SurfaceControl.Transaction mTransaction;
  CompatibilityInfo.Translator mTranslator;
  final Region mTransparentRegion;
  int mTraversalBarrier;
  final TraversalRunnable mTraversalRunnable;
  public boolean mTraversalScheduled;
  boolean mUnbufferedInputDispatch;
  private final UnhandledKeyManager mUnhandledKeyManager;
  @GuardedBy({"this"})
  boolean mUpcomingInTouchMode;
  @GuardedBy({"this"})
  boolean mUpcomingWindowFocus;
  private boolean mUseMTRenderer;
  @UnsupportedAppUsage
  View mView;
  final ViewConfiguration mViewConfiguration;
  private int mViewLayoutDirectionInitial;
  int mViewVisibility;
  final Rect mVisRect;
  @UnsupportedAppUsage
  int mWidth;
  boolean mWillDrawSoon;
  final Rect mWinFrame;
  final W mWindow;
  public final WindowManager.LayoutParams mWindowAttributes;
  boolean mWindowAttributesChanged;
  int mWindowAttributesChangesFlag;
  @GuardedBy({"mWindowCallbacks"})
  final ArrayList<WindowCallbacks> mWindowCallbacks;
  CountDownLatch mWindowDrawCountDown;
  @GuardedBy({"this"})
  boolean mWindowFocusChanged;
  @UnsupportedAppUsage
  final IWindowSession mWindowSession;
  private final ArrayList<WindowStoppedCallback> mWindowStoppedCallbacks;
  
  static
  {
    sFirstDrawHandlers = new ArrayList();
    sFirstDrawComplete = false;
    sConfigCallbacks = new ArrayList();
    sCompatibilityDone = false;
    mResizeInterpolator = new AccelerateDecelerateInterpolator();
  }
  
  /* Error */
  public ViewRootImpl(Context paramContext, Display paramDisplay)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 541	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: new 520	java/util/ArrayList
    //   8: dup
    //   9: invokespecial 521	java/util/ArrayList:<init>	()V
    //   12: putfield 543	android/view/ViewRootImpl:mWindowCallbacks	Ljava/util/ArrayList;
    //   15: aload_0
    //   16: iconst_2
    //   17: newarray <illegal type>
    //   19: putfield 545	android/view/ViewRootImpl:mTmpLocation	[I
    //   22: aload_0
    //   23: new 547	android/util/TypedValue
    //   26: dup
    //   27: invokespecial 548	android/util/TypedValue:<init>	()V
    //   30: putfield 550	android/view/ViewRootImpl:mTmpValue	Landroid/util/TypedValue;
    //   33: aload_0
    //   34: new 552	android/view/WindowManager$LayoutParams
    //   37: dup
    //   38: invokespecial 553	android/view/WindowManager$LayoutParams:<init>	()V
    //   41: putfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   44: aload_0
    //   45: iconst_1
    //   46: putfield 557	android/view/ViewRootImpl:mAppVisible	Z
    //   49: iconst_0
    //   50: istore_3
    //   51: aload_0
    //   52: iconst_0
    //   53: putfield 559	android/view/ViewRootImpl:mForceDecorViewVisibility	Z
    //   56: aload_0
    //   57: iconst_m1
    //   58: putfield 561	android/view/ViewRootImpl:mOrigWindowType	I
    //   61: aload_0
    //   62: iconst_0
    //   63: putfield 563	android/view/ViewRootImpl:mStopped	Z
    //   66: aload_0
    //   67: iconst_0
    //   68: putfield 565	android/view/ViewRootImpl:mIsAmbientMode	Z
    //   71: aload_0
    //   72: iconst_0
    //   73: putfield 567	android/view/ViewRootImpl:mPausedForTransition	Z
    //   76: aload_0
    //   77: iconst_0
    //   78: putfield 569	android/view/ViewRootImpl:mLastInCompatMode	Z
    //   81: aload_0
    //   82: new 571	android/graphics/Rect
    //   85: dup
    //   86: invokespecial 572	android/graphics/Rect:<init>	()V
    //   89: putfield 574	android/view/ViewRootImpl:mTempBoundsRect	Landroid/graphics/Rect;
    //   92: aload_0
    //   93: iconst_0
    //   94: putfield 576	android/view/ViewRootImpl:mIsProjectionMode	Z
    //   97: aload_0
    //   98: iconst_0
    //   99: putfield 578	android/view/ViewRootImpl:mIsCastMode	Z
    //   102: aload_0
    //   103: iconst_0
    //   104: putfield 580	android/view/ViewRootImpl:mIsCastModeRotationChanged	Z
    //   107: aload_0
    //   108: iconst_0
    //   109: putfield 582	android/view/ViewRootImpl:mCastModeChanged	Z
    //   112: aload_0
    //   113: iconst_0
    //   114: putfield 584	android/view/ViewRootImpl:mProjectionModeChanged	Z
    //   117: aload_0
    //   118: ldc_w 586
    //   121: putfield 588	android/view/ViewRootImpl:mPendingInputEventQueueLengthCounterName	Ljava/lang/String;
    //   124: aload_0
    //   125: new 106	android/view/ViewRootImpl$UnhandledKeyManager
    //   128: dup
    //   129: aconst_null
    //   130: invokespecial 591	android/view/ViewRootImpl$UnhandledKeyManager:<init>	(Landroid/view/ViewRootImpl$1;)V
    //   133: putfield 593	android/view/ViewRootImpl:mUnhandledKeyManager	Landroid/view/ViewRootImpl$UnhandledKeyManager;
    //   136: aload_0
    //   137: iconst_0
    //   138: putfield 595	android/view/ViewRootImpl:mWindowAttributesChanged	Z
    //   141: aload_0
    //   142: iconst_0
    //   143: putfield 597	android/view/ViewRootImpl:mWindowAttributesChangesFlag	I
    //   146: aload_0
    //   147: new 599	android/view/Surface
    //   150: dup
    //   151: invokespecial 600	android/view/Surface:<init>	()V
    //   154: putfield 602	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   157: aload_0
    //   158: new 604	android/view/SurfaceControl
    //   161: dup
    //   162: invokespecial 605	android/view/SurfaceControl:<init>	()V
    //   165: putfield 607	android/view/ViewRootImpl:mSurfaceControl	Landroid/view/SurfaceControl;
    //   168: aload_0
    //   169: new 599	android/view/Surface
    //   172: dup
    //   173: invokespecial 600	android/view/Surface:<init>	()V
    //   176: putfield 609	android/view/ViewRootImpl:mBoundsSurface	Landroid/view/Surface;
    //   179: aload_0
    //   180: new 611	android/view/SurfaceControl$Transaction
    //   183: dup
    //   184: invokespecial 612	android/view/SurfaceControl$Transaction:<init>	()V
    //   187: putfield 614	android/view/ViewRootImpl:mTransaction	Landroid/view/SurfaceControl$Transaction;
    //   190: aload_0
    //   191: new 571	android/graphics/Rect
    //   194: dup
    //   195: invokespecial 572	android/graphics/Rect:<init>	()V
    //   198: putfield 616	android/view/ViewRootImpl:mTmpFrame	Landroid/graphics/Rect;
    //   201: aload_0
    //   202: new 571	android/graphics/Rect
    //   205: dup
    //   206: invokespecial 572	android/graphics/Rect:<init>	()V
    //   209: putfield 618	android/view/ViewRootImpl:mPendingOverscanInsets	Landroid/graphics/Rect;
    //   212: aload_0
    //   213: new 571	android/graphics/Rect
    //   216: dup
    //   217: invokespecial 572	android/graphics/Rect:<init>	()V
    //   220: putfield 620	android/view/ViewRootImpl:mPendingVisibleInsets	Landroid/graphics/Rect;
    //   223: aload_0
    //   224: new 571	android/graphics/Rect
    //   227: dup
    //   228: invokespecial 572	android/graphics/Rect:<init>	()V
    //   231: putfield 622	android/view/ViewRootImpl:mPendingStableInsets	Landroid/graphics/Rect;
    //   234: aload_0
    //   235: new 571	android/graphics/Rect
    //   238: dup
    //   239: invokespecial 572	android/graphics/Rect:<init>	()V
    //   242: putfield 624	android/view/ViewRootImpl:mPendingContentInsets	Landroid/graphics/Rect;
    //   245: aload_0
    //   246: new 571	android/graphics/Rect
    //   249: dup
    //   250: invokespecial 572	android/graphics/Rect:<init>	()V
    //   253: putfield 626	android/view/ViewRootImpl:mPendingOutsets	Landroid/graphics/Rect;
    //   256: aload_0
    //   257: new 571	android/graphics/Rect
    //   260: dup
    //   261: invokespecial 572	android/graphics/Rect:<init>	()V
    //   264: putfield 628	android/view/ViewRootImpl:mPendingBackDropFrame	Landroid/graphics/Rect;
    //   267: aload_0
    //   268: new 630	android/view/DisplayCutout$ParcelableWrapper
    //   271: dup
    //   272: getstatic 635	android/view/DisplayCutout:NO_CUTOUT	Landroid/view/DisplayCutout;
    //   275: invokespecial 638	android/view/DisplayCutout$ParcelableWrapper:<init>	(Landroid/view/DisplayCutout;)V
    //   278: putfield 640	android/view/ViewRootImpl:mPendingDisplayCutout	Landroid/view/DisplayCutout$ParcelableWrapper;
    //   281: aload_0
    //   282: new 642	android/view/InsetsState
    //   285: dup
    //   286: invokespecial 643	android/view/InsetsState:<init>	()V
    //   289: putfield 645	android/view/ViewRootImpl:mTempInsets	Landroid/view/InsetsState;
    //   292: aload_0
    //   293: new 647	android/view/ViewTreeObserver$InternalInsetsInfo
    //   296: dup
    //   297: invokespecial 648	android/view/ViewTreeObserver$InternalInsetsInfo:<init>	()V
    //   300: putfield 650	android/view/ViewRootImpl:mLastGivenInsets	Landroid/view/ViewTreeObserver$InternalInsetsInfo;
    //   303: aload_0
    //   304: new 571	android/graphics/Rect
    //   307: dup
    //   308: invokespecial 572	android/graphics/Rect:<init>	()V
    //   311: putfield 652	android/view/ViewRootImpl:mDispatchContentInsets	Landroid/graphics/Rect;
    //   314: aload_0
    //   315: new 571	android/graphics/Rect
    //   318: dup
    //   319: invokespecial 572	android/graphics/Rect:<init>	()V
    //   322: putfield 654	android/view/ViewRootImpl:mDispatchStableInsets	Landroid/graphics/Rect;
    //   325: aload_0
    //   326: getstatic 635	android/view/DisplayCutout:NO_CUTOUT	Landroid/view/DisplayCutout;
    //   329: putfield 656	android/view/ViewRootImpl:mDispatchDisplayCutout	Landroid/view/DisplayCutout;
    //   332: aload_0
    //   333: iconst_1
    //   334: putfield 658	android/view/ViewRootImpl:mNeedUpdateBlurCrop	Z
    //   337: aload_0
    //   338: new 660	android/content/res/Configuration
    //   341: dup
    //   342: invokespecial 661	android/content/res/Configuration:<init>	()V
    //   345: putfield 663	android/view/ViewRootImpl:mLastConfigurationFromResources	Landroid/content/res/Configuration;
    //   348: aload_0
    //   349: new 665	android/util/MergedConfiguration
    //   352: dup
    //   353: invokespecial 666	android/util/MergedConfiguration:<init>	()V
    //   356: putfield 668	android/view/ViewRootImpl:mLastReportedMergedConfiguration	Landroid/util/MergedConfiguration;
    //   359: aload_0
    //   360: new 665	android/util/MergedConfiguration
    //   363: dup
    //   364: invokespecial 666	android/util/MergedConfiguration:<init>	()V
    //   367: putfield 670	android/view/ViewRootImpl:mPendingMergedConfiguration	Landroid/util/MergedConfiguration;
    //   370: aload_0
    //   371: new 672	android/graphics/PointF
    //   374: dup
    //   375: invokespecial 673	android/graphics/PointF:<init>	()V
    //   378: putfield 675	android/view/ViewRootImpl:mDragPoint	Landroid/graphics/PointF;
    //   381: aload_0
    //   382: new 672	android/graphics/PointF
    //   385: dup
    //   386: invokespecial 673	android/graphics/PointF:<init>	()V
    //   389: putfield 677	android/view/ViewRootImpl:mLastTouchPoint	Landroid/graphics/PointF;
    //   392: aload_0
    //   393: ldc2_w 678
    //   396: putfield 681	android/view/ViewRootImpl:mFpsStartTime	J
    //   399: aload_0
    //   400: ldc2_w 678
    //   403: putfield 683	android/view/ViewRootImpl:mFpsPrevTime	J
    //   406: aload_0
    //   407: iconst_1
    //   408: putfield 685	android/view/ViewRootImpl:mPointerIconType	I
    //   411: aload_0
    //   412: aconst_null
    //   413: putfield 687	android/view/ViewRootImpl:mCustomPointerIcon	Landroid/view/PointerIcon;
    //   416: aload_0
    //   417: new 23	android/view/ViewRootImpl$AccessibilityInteractionConnectionManager
    //   420: dup
    //   421: aload_0
    //   422: invokespecial 690	android/view/ViewRootImpl$AccessibilityInteractionConnectionManager:<init>	(Landroid/view/ViewRootImpl;)V
    //   425: putfield 692	android/view/ViewRootImpl:mAccessibilityInteractionConnectionManager	Landroid/view/ViewRootImpl$AccessibilityInteractionConnectionManager;
    //   428: aload_0
    //   429: iconst_0
    //   430: putfield 694	android/view/ViewRootImpl:mInLayout	Z
    //   433: aload_0
    //   434: new 520	java/util/ArrayList
    //   437: dup
    //   438: invokespecial 521	java/util/ArrayList:<init>	()V
    //   441: putfield 696	android/view/ViewRootImpl:mLayoutRequesters	Ljava/util/ArrayList;
    //   444: aload_0
    //   445: iconst_0
    //   446: putfield 698	android/view/ViewRootImpl:mHandlingLayoutInLayoutRequest	Z
    //   449: aload_0
    //   450: iconst_0
    //   451: putfield 700	android/view/ViewRootImpl:mSurfaceViewCount	I
    //   454: invokestatic 706	android/view/InputEventConsistencyVerifier:isInstrumentationEnabled	()Z
    //   457: ifeq +17 -> 474
    //   460: new 702	android/view/InputEventConsistencyVerifier
    //   463: dup
    //   464: aload_0
    //   465: iconst_0
    //   466: invokespecial 709	android/view/InputEventConsistencyVerifier:<init>	(Ljava/lang/Object;I)V
    //   469: astore 4
    //   471: goto +6 -> 477
    //   474: aconst_null
    //   475: astore 4
    //   477: aload_0
    //   478: aload 4
    //   480: putfield 711	android/view/ViewRootImpl:mInputEventConsistencyVerifier	Landroid/view/InputEventConsistencyVerifier;
    //   483: aload_0
    //   484: new 713	android/view/InsetsController
    //   487: dup
    //   488: aload_0
    //   489: invokespecial 714	android/view/InsetsController:<init>	(Landroid/view/ViewRootImpl;)V
    //   492: putfield 716	android/view/ViewRootImpl:mInsetsController	Landroid/view/InsetsController;
    //   495: aload_0
    //   496: new 718	android/view/GestureExclusionTracker
    //   499: dup
    //   500: invokespecial 719	android/view/GestureExclusionTracker:<init>	()V
    //   503: putfield 721	android/view/ViewRootImpl:mGestureExclusionTracker	Landroid/view/GestureExclusionTracker;
    //   506: aload_0
    //   507: ldc -34
    //   509: putfield 723	android/view/ViewRootImpl:mTag	Ljava/lang/String;
    //   512: aload_0
    //   513: iconst_0
    //   514: putfield 725	android/view/ViewRootImpl:mHaveMoveEvent	Z
    //   517: aload_0
    //   518: iconst_0
    //   519: putfield 727	android/view/ViewRootImpl:mProfile	Z
    //   522: aload_0
    //   523: new 12	android/view/ViewRootImpl$1
    //   526: dup
    //   527: aload_0
    //   528: invokespecial 728	android/view/ViewRootImpl$1:<init>	(Landroid/view/ViewRootImpl;)V
    //   531: putfield 730	android/view/ViewRootImpl:mDisplayListener	Landroid/hardware/display/DisplayManager$DisplayListener;
    //   534: aload_0
    //   535: new 520	java/util/ArrayList
    //   538: dup
    //   539: invokespecial 521	java/util/ArrayList:<init>	()V
    //   542: putfield 732	android/view/ViewRootImpl:mWindowStoppedCallbacks	Ljava/util/ArrayList;
    //   545: aload_0
    //   546: iconst_0
    //   547: putfield 734	android/view/ViewRootImpl:mDrawsNeededToReport	I
    //   550: aload_0
    //   551: new 115	android/view/ViewRootImpl$ViewRootHandler
    //   554: dup
    //   555: aload_0
    //   556: invokespecial 735	android/view/ViewRootImpl$ViewRootHandler:<init>	(Landroid/view/ViewRootImpl;)V
    //   559: putfield 737	android/view/ViewRootImpl:mHandler	Landroid/view/ViewRootImpl$ViewRootHandler;
    //   562: aload_0
    //   563: new 103	android/view/ViewRootImpl$TraversalRunnable
    //   566: dup
    //   567: aload_0
    //   568: invokespecial 738	android/view/ViewRootImpl$TraversalRunnable:<init>	(Landroid/view/ViewRootImpl;)V
    //   571: putfield 740	android/view/ViewRootImpl:mTraversalRunnable	Landroid/view/ViewRootImpl$TraversalRunnable;
    //   574: aload_0
    //   575: new 44	android/view/ViewRootImpl$ConsumeBatchedInputRunnable
    //   578: dup
    //   579: aload_0
    //   580: invokespecial 741	android/view/ViewRootImpl$ConsumeBatchedInputRunnable:<init>	(Landroid/view/ViewRootImpl;)V
    //   583: putfield 743	android/view/ViewRootImpl:mConsumedBatchedInputRunnable	Landroid/view/ViewRootImpl$ConsumeBatchedInputRunnable;
    //   586: aload_0
    //   587: new 41	android/view/ViewRootImpl$ConsumeBatchedInputImmediatelyRunnable
    //   590: dup
    //   591: aload_0
    //   592: invokespecial 744	android/view/ViewRootImpl$ConsumeBatchedInputImmediatelyRunnable:<init>	(Landroid/view/ViewRootImpl;)V
    //   595: putfield 746	android/view/ViewRootImpl:mConsumeBatchedInputImmediatelyRunnable	Landroid/view/ViewRootImpl$ConsumeBatchedInputImmediatelyRunnable;
    //   598: aload_0
    //   599: new 59	android/view/ViewRootImpl$InvalidateOnAnimationRunnable
    //   602: dup
    //   603: aload_0
    //   604: invokespecial 747	android/view/ViewRootImpl$InvalidateOnAnimationRunnable:<init>	(Landroid/view/ViewRootImpl;)V
    //   607: putfield 749	android/view/ViewRootImpl:mInvalidateOnAnimationRunnable	Landroid/view/ViewRootImpl$InvalidateOnAnimationRunnable;
    //   610: aload_0
    //   611: aload_1
    //   612: putfield 751	android/view/ViewRootImpl:mContext	Landroid/content/Context;
    //   615: aload_0
    //   616: invokestatic 757	android/view/WindowManagerGlobal:getWindowSession	()Landroid/view/IWindowSession;
    //   619: putfield 759	android/view/ViewRootImpl:mWindowSession	Landroid/view/IWindowSession;
    //   622: aload_0
    //   623: aload_2
    //   624: putfield 761	android/view/ViewRootImpl:mDisplay	Landroid/view/Display;
    //   627: aload_0
    //   628: aload_1
    //   629: invokevirtual 767	android/content/Context:getBasePackageName	()Ljava/lang/String;
    //   632: putfield 769	android/view/ViewRootImpl:mBasePackageName	Ljava/lang/String;
    //   635: aload_0
    //   636: invokestatic 775	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   639: putfield 777	android/view/ViewRootImpl:mThread	Ljava/lang/Thread;
    //   642: aload_0
    //   643: new 779	android/view/WindowLeaked
    //   646: dup
    //   647: aconst_null
    //   648: invokespecial 782	android/view/WindowLeaked:<init>	(Ljava/lang/String;)V
    //   651: putfield 784	android/view/ViewRootImpl:mLocation	Landroid/view/WindowLeaked;
    //   654: aload_0
    //   655: getfield 784	android/view/ViewRootImpl:mLocation	Landroid/view/WindowLeaked;
    //   658: invokevirtual 788	android/view/WindowLeaked:fillInStackTrace	()Ljava/lang/Throwable;
    //   661: pop
    //   662: aload_0
    //   663: iconst_m1
    //   664: putfield 790	android/view/ViewRootImpl:mWidth	I
    //   667: aload_0
    //   668: iconst_m1
    //   669: putfield 792	android/view/ViewRootImpl:mHeight	I
    //   672: aload_0
    //   673: new 571	android/graphics/Rect
    //   676: dup
    //   677: invokespecial 572	android/graphics/Rect:<init>	()V
    //   680: putfield 794	android/view/ViewRootImpl:mDirty	Landroid/graphics/Rect;
    //   683: aload_0
    //   684: new 571	android/graphics/Rect
    //   687: dup
    //   688: invokespecial 572	android/graphics/Rect:<init>	()V
    //   691: putfield 796	android/view/ViewRootImpl:mTempRect	Landroid/graphics/Rect;
    //   694: aload_0
    //   695: new 571	android/graphics/Rect
    //   698: dup
    //   699: invokespecial 572	android/graphics/Rect:<init>	()V
    //   702: putfield 798	android/view/ViewRootImpl:mVisRect	Landroid/graphics/Rect;
    //   705: aload_0
    //   706: new 571	android/graphics/Rect
    //   709: dup
    //   710: invokespecial 572	android/graphics/Rect:<init>	()V
    //   713: putfield 800	android/view/ViewRootImpl:mWinFrame	Landroid/graphics/Rect;
    //   716: aload_0
    //   717: new 118	android/view/ViewRootImpl$W
    //   720: dup
    //   721: aload_0
    //   722: invokespecial 801	android/view/ViewRootImpl$W:<init>	(Landroid/view/ViewRootImpl;)V
    //   725: putfield 803	android/view/ViewRootImpl:mWindow	Landroid/view/ViewRootImpl$W;
    //   728: aload_0
    //   729: aload_1
    //   730: invokevirtual 807	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   733: getfield 812	android/content/pm/ApplicationInfo:targetSdkVersion	I
    //   736: putfield 814	android/view/ViewRootImpl:mTargetSdkVersion	I
    //   739: aload_0
    //   740: bipush 8
    //   742: putfield 816	android/view/ViewRootImpl:mViewVisibility	I
    //   745: aload_0
    //   746: new 818	android/graphics/Region
    //   749: dup
    //   750: invokespecial 819	android/graphics/Region:<init>	()V
    //   753: putfield 821	android/view/ViewRootImpl:mTransparentRegion	Landroid/graphics/Region;
    //   756: aload_0
    //   757: new 818	android/graphics/Region
    //   760: dup
    //   761: invokespecial 819	android/graphics/Region:<init>	()V
    //   764: putfield 823	android/view/ViewRootImpl:mPreviousTransparentRegion	Landroid/graphics/Region;
    //   767: aload_0
    //   768: iconst_1
    //   769: putfield 825	android/view/ViewRootImpl:mFirst	Z
    //   772: aload_0
    //   773: iconst_0
    //   774: putfield 827	android/view/ViewRootImpl:mAdded	Z
    //   777: aload_0
    //   778: new 829	android/view/View$AttachInfo
    //   781: dup
    //   782: aload_0
    //   783: getfield 759	android/view/ViewRootImpl:mWindowSession	Landroid/view/IWindowSession;
    //   786: aload_0
    //   787: getfield 803	android/view/ViewRootImpl:mWindow	Landroid/view/ViewRootImpl$W;
    //   790: aload_2
    //   791: aload_0
    //   792: aload_0
    //   793: getfield 737	android/view/ViewRootImpl:mHandler	Landroid/view/ViewRootImpl$ViewRootHandler;
    //   796: aload_0
    //   797: aload_1
    //   798: invokespecial 832	android/view/View$AttachInfo:<init>	(Landroid/view/IWindowSession;Landroid/view/IWindow;Landroid/view/Display;Landroid/view/ViewRootImpl;Landroid/os/Handler;Landroid/view/View$AttachInfo$Callbacks;Landroid/content/Context;)V
    //   801: putfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   804: aload_0
    //   805: aload_1
    //   806: invokestatic 840	android/view/accessibility/AccessibilityManager:getInstance	(Landroid/content/Context;)Landroid/view/accessibility/AccessibilityManager;
    //   809: putfield 842	android/view/ViewRootImpl:mAccessibilityManager	Landroid/view/accessibility/AccessibilityManager;
    //   812: aload_0
    //   813: getfield 842	android/view/ViewRootImpl:mAccessibilityManager	Landroid/view/accessibility/AccessibilityManager;
    //   816: aload_0
    //   817: getfield 692	android/view/ViewRootImpl:mAccessibilityInteractionConnectionManager	Landroid/view/ViewRootImpl$AccessibilityInteractionConnectionManager;
    //   820: aload_0
    //   821: getfield 737	android/view/ViewRootImpl:mHandler	Landroid/view/ViewRootImpl$ViewRootHandler;
    //   824: invokevirtual 846	android/view/accessibility/AccessibilityManager:addAccessibilityStateChangeListener	(Landroid/view/accessibility/AccessibilityManager$AccessibilityStateChangeListener;Landroid/os/Handler;)V
    //   827: aload_0
    //   828: new 50	android/view/ViewRootImpl$HighContrastTextManager
    //   831: dup
    //   832: aload_0
    //   833: invokespecial 847	android/view/ViewRootImpl$HighContrastTextManager:<init>	(Landroid/view/ViewRootImpl;)V
    //   836: putfield 849	android/view/ViewRootImpl:mHighContrastTextManager	Landroid/view/ViewRootImpl$HighContrastTextManager;
    //   839: aload_0
    //   840: getfield 842	android/view/ViewRootImpl:mAccessibilityManager	Landroid/view/accessibility/AccessibilityManager;
    //   843: aload_0
    //   844: getfield 849	android/view/ViewRootImpl:mHighContrastTextManager	Landroid/view/ViewRootImpl$HighContrastTextManager;
    //   847: aload_0
    //   848: getfield 737	android/view/ViewRootImpl:mHandler	Landroid/view/ViewRootImpl$ViewRootHandler;
    //   851: invokevirtual 853	android/view/accessibility/AccessibilityManager:addHighTextContrastStateChangeListener	(Landroid/view/accessibility/AccessibilityManager$HighTextContrastChangeListener;Landroid/os/Handler;)V
    //   854: aload_0
    //   855: aload_1
    //   856: invokestatic 859	android/view/ViewConfiguration:get	(Landroid/content/Context;)Landroid/view/ViewConfiguration;
    //   859: putfield 861	android/view/ViewRootImpl:mViewConfiguration	Landroid/view/ViewConfiguration;
    //   862: aload_0
    //   863: aload_1
    //   864: invokevirtual 865	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   867: invokevirtual 871	android/content/res/Resources:getDisplayMetrics	()Landroid/util/DisplayMetrics;
    //   870: getfield 876	android/util/DisplayMetrics:densityDpi	I
    //   873: putfield 878	android/view/ViewRootImpl:mDensity	I
    //   876: aload_0
    //   877: aload_1
    //   878: invokevirtual 865	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   881: invokevirtual 871	android/content/res/Resources:getDisplayMetrics	()Landroid/util/DisplayMetrics;
    //   884: getfield 881	android/util/DisplayMetrics:noncompatDensityDpi	I
    //   887: putfield 883	android/view/ViewRootImpl:mNoncompatDensity	I
    //   890: aload_0
    //   891: new 885	com/android/internal/policy/PhoneFallbackEventHandler
    //   894: dup
    //   895: aload_1
    //   896: invokespecial 888	com/android/internal/policy/PhoneFallbackEventHandler:<init>	(Landroid/content/Context;)V
    //   899: putfield 890	android/view/ViewRootImpl:mFallbackEventHandler	Landroid/view/FallbackEventHandler;
    //   902: aload_0
    //   903: invokestatic 895	android/view/Choreographer:getInstance	()Landroid/view/Choreographer;
    //   906: putfield 897	android/view/ViewRootImpl:mChoreographer	Landroid/view/Choreographer;
    //   909: aload_0
    //   910: aload_1
    //   911: ldc_w 899
    //   914: invokevirtual 903	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   917: checkcast 905	android/hardware/display/DisplayManager
    //   920: putfield 907	android/view/ViewRootImpl:mDisplayManager	Landroid/hardware/display/DisplayManager;
    //   923: aload_1
    //   924: invokevirtual 865	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   927: ldc_w 908
    //   930: invokevirtual 912	android/content/res/Resources:getString	(I)Ljava/lang/String;
    //   933: astore_2
    //   934: aload_2
    //   935: invokevirtual 917	java/lang/String:isEmpty	()Z
    //   938: ifeq +18 -> 956
    //   941: aload_0
    //   942: new 919	android/view/InputEventCompatProcessor
    //   945: dup
    //   946: aload_1
    //   947: invokespecial 920	android/view/InputEventCompatProcessor:<init>	(Landroid/content/Context;)V
    //   950: putfield 922	android/view/ViewRootImpl:mInputCompatProcessor	Landroid/view/InputEventCompatProcessor;
    //   953: goto +63 -> 1016
    //   956: aload_2
    //   957: invokestatic 928	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   960: iconst_1
    //   961: anewarray 924	java/lang/Class
    //   964: dup
    //   965: iconst_0
    //   966: ldc_w 763
    //   969: aastore
    //   970: invokevirtual 932	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   973: iconst_1
    //   974: anewarray 4	java/lang/Object
    //   977: dup
    //   978: iconst_0
    //   979: aload_1
    //   980: aastore
    //   981: invokevirtual 938	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   984: checkcast 919	android/view/InputEventCompatProcessor
    //   987: astore_1
    //   988: aload_0
    //   989: aload_1
    //   990: putfield 922	android/view/ViewRootImpl:mInputCompatProcessor	Landroid/view/InputEventCompatProcessor;
    //   993: goto +23 -> 1016
    //   996: astore_1
    //   997: goto +59 -> 1056
    //   1000: astore_1
    //   1001: ldc -34
    //   1003: ldc_w 940
    //   1006: aload_1
    //   1007: invokestatic 946	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   1010: pop
    //   1011: aload_0
    //   1012: aconst_null
    //   1013: putfield 922	android/view/ViewRootImpl:mInputCompatProcessor	Landroid/view/InputEventCompatProcessor;
    //   1016: getstatic 529	android/view/ViewRootImpl:sCompatibilityDone	Z
    //   1019: ifne +22 -> 1041
    //   1022: aload_0
    //   1023: getfield 814	android/view/ViewRootImpl:mTargetSdkVersion	I
    //   1026: bipush 28
    //   1028: if_icmpge +5 -> 1033
    //   1031: iconst_1
    //   1032: istore_3
    //   1033: iload_3
    //   1034: putstatic 948	android/view/ViewRootImpl:sAlwaysAssignFocus	Z
    //   1037: iconst_1
    //   1038: putstatic 529	android/view/ViewRootImpl:sCompatibilityDone	Z
    //   1041: aload_0
    //   1042: invokevirtual 951	android/view/ViewRootImpl:loadSystemProperties	()V
    //   1045: invokestatic 956	android/view/ForceDarkHelper:getInstance	()Landroid/view/ForceDarkHelper;
    //   1048: aload_0
    //   1049: getfield 751	android/view/ViewRootImpl:mContext	Landroid/content/Context;
    //   1052: invokevirtual 959	android/view/ForceDarkHelper:registAppDarkModeObserver	(Landroid/content/Context;)V
    //   1055: return
    //   1056: aload_0
    //   1057: aconst_null
    //   1058: putfield 922	android/view/ViewRootImpl:mInputCompatProcessor	Landroid/view/InputEventCompatProcessor;
    //   1061: aload_1
    //   1062: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1063	0	this	ViewRootImpl
    //   0	1063	1	paramContext	Context
    //   0	1063	2	paramDisplay	Display
    //   50	984	3	bool	boolean
    //   469	10	4	localInputEventConsistencyVerifier	InputEventConsistencyVerifier
    // Exception table:
    //   from	to	target	type
    //   956	988	996	finally
    //   1001	1011	996	finally
    //   956	988	1000	java/lang/Exception
  }
  
  @UnsupportedAppUsage
  public static void addConfigCallback(ConfigChangedCallback paramConfigChangedCallback)
  {
    synchronized (sConfigCallbacks)
    {
      sConfigCallbacks.add(paramConfigChangedCallback);
      return;
    }
  }
  
  public static void addFirstDrawHandler(Runnable paramRunnable)
  {
    synchronized (sFirstDrawHandlers)
    {
      if (!sFirstDrawComplete) {
        sFirstDrawHandlers.add(paramRunnable);
      }
      return;
    }
  }
  
  private void applyKeepScreenOnFlag(WindowManager.LayoutParams paramLayoutParams)
  {
    if (this.mAttachInfo.mKeepScreenOn) {
      paramLayoutParams.flags |= 0x80;
    } else {
      paramLayoutParams.flags = (paramLayoutParams.flags & 0xFF7F | this.mClientWindowLayoutFlags & 0x80);
    }
  }
  
  private boolean checkForLeavingTouchModeAndConsume(KeyEvent paramKeyEvent)
  {
    if (!this.mAttachInfo.mInTouchMode) {
      return false;
    }
    int i = paramKeyEvent.getAction();
    if ((i != 0) && (i != 2)) {
      return false;
    }
    if ((paramKeyEvent.getFlags() & 0x4) != 0) {
      return false;
    }
    if (isNavigationKey(paramKeyEvent)) {
      return ensureTouchMode(false);
    }
    if (isTypingKey(paramKeyEvent))
    {
      ensureTouchMode(false);
      return false;
    }
    return false;
  }
  
  private boolean collectViewAttributes()
  {
    if (this.mAttachInfo.mRecomputeGlobalAttributes)
    {
      View.AttachInfo localAttachInfo = this.mAttachInfo;
      localAttachInfo.mRecomputeGlobalAttributes = false;
      boolean bool = localAttachInfo.mKeepScreenOn;
      localAttachInfo = this.mAttachInfo;
      localAttachInfo.mKeepScreenOn = false;
      localAttachInfo.mSystemUiVisibility = 0;
      localAttachInfo.mHasSystemUiListeners = false;
      this.mView.dispatchCollectViewAttributes(localAttachInfo, 0);
      localAttachInfo = this.mAttachInfo;
      localAttachInfo.mSystemUiVisibility &= this.mAttachInfo.mDisabledSystemUiVisibility;
      WindowManager.LayoutParams localLayoutParams = this.mWindowAttributes;
      localAttachInfo = this.mAttachInfo;
      localAttachInfo.mSystemUiVisibility |= getImpliedSystemUiVisibility(localLayoutParams);
      if ((this.mAttachInfo.mKeepScreenOn != bool) || (this.mAttachInfo.mSystemUiVisibility != localLayoutParams.subtreeSystemUiVisibility) || (this.mAttachInfo.mHasSystemUiListeners != localLayoutParams.hasSystemUiListeners))
      {
        applyKeepScreenOnFlag(localLayoutParams);
        localLayoutParams.subtreeSystemUiVisibility = this.mAttachInfo.mSystemUiVisibility;
        localLayoutParams.hasSystemUiListeners = this.mAttachInfo.mHasSystemUiListeners;
        this.mView.dispatchWindowSystemUiVisiblityChanged(this.mAttachInfo.mSystemUiVisibility);
        return true;
      }
    }
    return false;
  }
  
  private void deliverInputEvent(QueuedInputEvent paramQueuedInputEvent)
  {
    Trace.asyncTraceBegin(8L, "deliverInputEvent", paramQueuedInputEvent.mEvent.getSequenceNumber());
    Object localObject = this.mInputEventConsistencyVerifier;
    if (localObject != null) {
      ((InputEventConsistencyVerifier)localObject).onInputEvent(paramQueuedInputEvent.mEvent, 0);
    }
    if (paramQueuedInputEvent.shouldSendToSynthesizer()) {
      localObject = this.mSyntheticInputStage;
    } else if (paramQueuedInputEvent.shouldSkipIme()) {
      localObject = this.mFirstPostImeInputStage;
    } else {
      localObject = this.mFirstInputStage;
    }
    if ((paramQueuedInputEvent.mEvent instanceof KeyEvent)) {
      this.mUnhandledKeyManager.preDispatch((KeyEvent)paramQueuedInputEvent.mEvent);
    }
    if (localObject != null)
    {
      handleWindowFocusChanged();
      ((InputStage)localObject).deliver(paramQueuedInputEvent);
    }
    else
    {
      finishInputEvent(paramQueuedInputEvent);
    }
  }
  
  private void destroyHardwareRenderer()
  {
    ThreadedRenderer localThreadedRenderer = this.mAttachInfo.mThreadedRenderer;
    if (localThreadedRenderer != null)
    {
      Object localObject = this.mView;
      if (localObject != null) {
        localThreadedRenderer.destroyHardwareResources((View)localObject);
      }
      localThreadedRenderer.destroy();
      localThreadedRenderer.setRequested(false);
      localObject = this.mAttachInfo;
      ((View.AttachInfo)localObject).mThreadedRenderer = null;
      ((View.AttachInfo)localObject).mHardwareAccelerated = false;
    }
  }
  
  private void destroySurface()
  {
    this.mSurface.release();
    this.mSurfaceControl.release();
    this.mSurfaceSession = null;
    SurfaceControl localSurfaceControl = this.mBoundsSurfaceControl;
    if (localSurfaceControl != null)
    {
      localSurfaceControl.remove();
      this.mBoundsSurface.release();
      this.mBoundsSurfaceControl = null;
    }
  }
  
  private int dipToPx(int paramInt)
  {
    return (int)(this.mContext.getResources().getDisplayMetrics().density * paramInt + 0.5F);
  }
  
  private void dispatchInsetsChanged(InsetsState paramInsetsState)
  {
    this.mHandler.obtainMessage(30, paramInsetsState).sendToTarget();
  }
  
  private void dispatchInsetsControlChanged(InsetsState paramInsetsState, InsetsSourceControl[] paramArrayOfInsetsSourceControl)
  {
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.arg1 = paramInsetsState;
    localSomeArgs.arg2 = paramArrayOfInsetsSourceControl;
    this.mHandler.obtainMessage(31, localSomeArgs).sendToTarget();
  }
  
  @UnsupportedAppUsage
  private void dispatchResized(Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5, Rect paramRect6, boolean paramBoolean1, MergedConfiguration paramMergedConfiguration, Rect paramRect7, boolean paramBoolean2, boolean paramBoolean3, int paramInt, DisplayCutout.ParcelableWrapper paramParcelableWrapper)
  {
    boolean bool = this.mDragResizing;
    int i = 1;
    int j;
    if ((bool) && (this.mUseMTRenderer))
    {
      bool = paramRect1.equals(paramRect7);
      synchronized (this.mWindowCallbacks)
      {
        for (j = this.mWindowCallbacks.size() - 1; j >= 0; j--) {
          ((WindowCallbacks)this.mWindowCallbacks.get(j)).onWindowSizeIsChanging(paramRect7, bool, paramRect4, paramRect5);
        }
      }
    }
    ??? = this.mHandler;
    if (paramBoolean1) {
      j = 5;
    } else {
      j = 4;
    }
    ??? = ((ViewRootHandler)???).obtainMessage(j);
    Object localObject2 = this.mTranslator;
    if (localObject2 != null)
    {
      ((CompatibilityInfo.Translator)localObject2).translateRectInScreenToAppWindow(paramRect1);
      this.mTranslator.translateRectInScreenToAppWindow(paramRect2);
      this.mTranslator.translateRectInScreenToAppWindow(paramRect3);
      this.mTranslator.translateRectInScreenToAppWindow(paramRect4);
    }
    localObject2 = SomeArgs.obtain();
    if (Binder.getCallingPid() == Process.myPid()) {
      j = i;
    } else {
      j = 0;
    }
    if (j != 0) {
      paramRect1 = new Rect(paramRect1);
    }
    ((SomeArgs)localObject2).arg1 = paramRect1;
    if (j != 0) {
      paramRect3 = new Rect(paramRect3);
    }
    ((SomeArgs)localObject2).arg2 = paramRect3;
    if (j != 0) {
      paramRect4 = new Rect(paramRect4);
    }
    ((SomeArgs)localObject2).arg3 = paramRect4;
    if ((j != 0) && (paramMergedConfiguration != null)) {
      paramMergedConfiguration = new MergedConfiguration(paramMergedConfiguration);
    }
    ((SomeArgs)localObject2).arg4 = paramMergedConfiguration;
    if (j != 0) {
      paramRect2 = new Rect(paramRect2);
    }
    ((SomeArgs)localObject2).arg5 = paramRect2;
    if (j != 0) {
      paramRect1 = new Rect(paramRect5);
    } else {
      paramRect1 = paramRect5;
    }
    ((SomeArgs)localObject2).arg6 = paramRect1;
    if (j != 0) {
      paramRect6 = new Rect(paramRect6);
    }
    ((SomeArgs)localObject2).arg7 = paramRect6;
    if (j != 0) {
      paramRect7 = new Rect(paramRect7);
    }
    ((SomeArgs)localObject2).arg8 = paramRect7;
    ((SomeArgs)localObject2).arg9 = paramParcelableWrapper.get();
    ((SomeArgs)localObject2).argi1 = paramBoolean2;
    ((SomeArgs)localObject2).argi2 = paramBoolean3;
    ((SomeArgs)localObject2).argi3 = paramInt;
    ((Message)???).obj = localObject2;
    this.mHandler.sendMessage((Message)???);
  }
  
  private boolean draw(boolean paramBoolean)
  {
    Surface localSurface1 = this.mSurface;
    if (!localSurface1.isValid()) {
      return false;
    }
    int i;
    if (!sFirstDrawComplete) {
      synchronized (sFirstDrawHandlers)
      {
        sFirstDrawComplete = true;
        i = sFirstDrawHandlers.size();
        for (j = 0; j < i; j++) {
          this.mHandler.post((Runnable)sFirstDrawHandlers.get(j));
        }
      }
    }
    ??? = null;
    scrollToRectOrFocus(null, false);
    if (this.mAttachInfo.mViewScrollChanged)
    {
      localObject2 = this.mAttachInfo;
      ((View.AttachInfo)localObject2).mViewScrollChanged = false;
      ((View.AttachInfo)localObject2).mTreeObserver.dispatchOnScrollChanged();
    }
    Object localObject2 = this.mScroller;
    if ((localObject2 != null) && (((Scroller)localObject2).computeScrollOffset())) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0) {
      j = this.mScroller.getCurrY();
    } else {
      j = this.mScrollY;
    }
    if (this.mCurScrollY != j)
    {
      this.mCurScrollY = j;
      localObject2 = this.mView;
      if ((localObject2 instanceof RootViewSurfaceTaker)) {
        ((RootViewSurfaceTaker)localObject2).onRootViewScrollYChanged(this.mCurScrollY);
      }
      paramBoolean = true;
    }
    float f = this.mAttachInfo.mApplicationScale;
    boolean bool = this.mAttachInfo.mScalingRequired;
    localObject2 = this.mDirty;
    if (this.mSurfaceHolder != null)
    {
      ((Rect)localObject2).setEmpty();
      if (i != 0)
      {
        ??? = this.mScroller;
        if (??? != null) {
          ((Scroller)???).abortAnimation();
        }
      }
      return false;
    }
    if (paramBoolean) {
      ((Rect)localObject2).set(0, 0, (int)(this.mWidth * f + 0.5F), (int)(this.mHeight * f + 0.5F));
    }
    this.mAttachInfo.mTreeObserver.dispatchOnDraw();
    int k = -this.mCanvasOffsetX;
    int m = -this.mCanvasOffsetY + j;
    Object localObject3 = this.mWindowAttributes;
    if (localObject3 != null) {
      ??? = ((WindowManager.LayoutParams)localObject3).surfaceInsets;
    }
    if (??? != null)
    {
      j = ((Rect)???).left;
      int n = ((Rect)???).top;
      ((Rect)localObject2).offset(((Rect)???).left, ((Rect)???).right);
      m -= n;
      k -= j;
    }
    Drawable localDrawable = this.mAttachInfo.mAccessibilityFocusDrawable;
    if (localDrawable != null)
    {
      localObject3 = this.mAttachInfo.mTmpInvalRect;
      if (!getAccessibilityFocusedRect((Rect)localObject3)) {
        ((Rect)localObject3).setEmpty();
      }
      if (!((Rect)localObject3).equals(localDrawable.getBounds()))
      {
        j = 1;
        break label463;
      }
    }
    int j = 0;
    label463:
    this.mAttachInfo.mDrawingTime = (this.mChoreographer.getFrameTimeNanos() / 1000000L);
    paramBoolean = false;
    if ((((Rect)localObject2).isEmpty()) && (!this.mIsAnimating) && (j == 0)) {
      break label897;
    }
    if ((this.mAttachInfo.mThreadedRenderer != null) && (this.mAttachInfo.mThreadedRenderer.isEnabled()))
    {
      if ((j == 0) && (!this.mInvalidateRootRequested)) {
        j = 0;
      } else {
        j = 1;
      }
      this.mInvalidateRootRequested = false;
      this.mIsAnimating = false;
      if ((this.mHardwareYOffset != m) || (this.mHardwareXOffset != k))
      {
        this.mHardwareYOffset = m;
        this.mHardwareXOffset = k;
        j = 1;
      }
      if (j != 0) {
        this.mAttachInfo.mThreadedRenderer.invalidateRoot();
      }
      ((Rect)localObject2).setEmpty();
      paramBoolean = updateContentDrawBounds();
      if (this.mReportNextDraw) {
        this.mAttachInfo.mThreadedRenderer.setStopped(false);
      }
      if (paramBoolean) {
        requestDrawWindow();
      }
      paramBoolean = true;
      if (??? != null)
      {
        if ((((Rect)???).top == this.mAttachInfo.mThreadedRenderer.getInsetTop()) && (((Rect)???).left == this.mAttachInfo.mThreadedRenderer.getInsetLeft()) && (this.mWidth == this.mAttachInfo.mThreadedRenderer.getWidth()) && (this.mHeight == this.mAttachInfo.mThreadedRenderer.getHeight())) {
          break label759;
        }
        this.mAttachInfo.mThreadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, (Rect)???);
      }
      label759:
      this.mAttachInfo.mThreadedRenderer.draw(this.mView, this.mAttachInfo, this);
    }
    else
    {
      if ((this.mAttachInfo.mThreadedRenderer != null) && (!this.mAttachInfo.mThreadedRenderer.isEnabled()) && (this.mAttachInfo.mThreadedRenderer.isRequested()) && (this.mSurface.isValid())) {
        try
        {
          this.mAttachInfo.mThreadedRenderer.initializeIfNeeded(this.mWidth, this.mHeight, this.mAttachInfo, this.mSurface, (Rect)???);
          this.mFullRedrawNeeded = true;
          scheduleTraversals();
          return false;
        }
        catch (Surface.OutOfResourcesException localOutOfResourcesException)
        {
          handleOutOfResourcesException(localOutOfResourcesException);
          return false;
        }
      }
      if (!drawSoftware(localSurface2, this.mAttachInfo, k, m, bool, (Rect)localObject2, localOutOfResourcesException)) {
        return false;
      }
    }
    label897:
    if (i != 0)
    {
      this.mFullRedrawNeeded = true;
      scheduleTraversals();
    }
    return paramBoolean;
  }
  
  private void drawAccessibilityFocusedDrawableIfNeeded(Canvas paramCanvas)
  {
    Rect localRect = this.mAttachInfo.mTmpInvalRect;
    if (getAccessibilityFocusedRect(localRect))
    {
      Drawable localDrawable = getAccessibilityFocusedDrawable();
      if (localDrawable != null)
      {
        localDrawable.setBounds(localRect);
        localDrawable.draw(paramCanvas);
      }
    }
    else if (this.mAttachInfo.mAccessibilityFocusDrawable != null)
    {
      this.mAttachInfo.mAccessibilityFocusDrawable.setBounds(0, 0, 0, 0);
    }
  }
  
  /* Error */
  private boolean drawSoftware(Surface paramSurface, View.AttachInfo paramAttachInfo, int paramInt1, int paramInt2, boolean paramBoolean, Rect paramRect1, Rect paramRect2)
  {
    // Byte code:
    //   0: aload 7
    //   2: ifnull +29 -> 31
    //   5: aload 7
    //   7: getfield 1438	android/graphics/Rect:left	I
    //   10: istore 8
    //   12: iload 4
    //   14: aload 7
    //   16: getfield 1441	android/graphics/Rect:top	I
    //   19: iadd
    //   20: istore 9
    //   22: iload_3
    //   23: iload 8
    //   25: iadd
    //   26: istore 8
    //   28: goto +10 -> 38
    //   31: iload 4
    //   33: istore 9
    //   35: iload_3
    //   36: istore 8
    //   38: iload 8
    //   40: ineg
    //   41: istore 10
    //   43: iload 9
    //   45: ineg
    //   46: istore 11
    //   48: aload 6
    //   50: iload 10
    //   52: iload 11
    //   54: invokevirtual 1448	android/graphics/Rect:offset	(II)V
    //   57: aload 6
    //   59: getfield 1438	android/graphics/Rect:left	I
    //   62: istore 11
    //   64: aload 6
    //   66: getfield 1441	android/graphics/Rect:top	I
    //   69: istore 11
    //   71: aload 6
    //   73: getfield 1444	android/graphics/Rect:right	I
    //   76: istore 11
    //   78: aload 6
    //   80: getfield 1557	android/graphics/Rect:bottom	I
    //   83: istore 11
    //   85: aload_0
    //   86: getfield 602	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   89: aload 6
    //   91: invokevirtual 1561	android/view/Surface:lockCanvas	(Landroid/graphics/Rect;)Landroid/graphics/Canvas;
    //   94: astore_2
    //   95: aload_2
    //   96: aload_0
    //   97: getfield 878	android/view/ViewRootImpl:mDensity	I
    //   100: invokevirtual 1566	android/graphics/Canvas:setDensity	(I)V
    //   103: aload 6
    //   105: iload 8
    //   107: iload 9
    //   109: invokevirtual 1448	android/graphics/Rect:offset	(II)V
    //   112: aload_2
    //   113: invokevirtual 1569	android/graphics/Canvas:isOpaque	()Z
    //   116: ifeq +12 -> 128
    //   119: iload 4
    //   121: ifne +7 -> 128
    //   124: iload_3
    //   125: ifeq +11 -> 136
    //   128: aload_2
    //   129: iconst_0
    //   130: getstatic 1575	android/graphics/PorterDuff$Mode:CLEAR	Landroid/graphics/PorterDuff$Mode;
    //   133: invokevirtual 1579	android/graphics/Canvas:drawColor	(ILandroid/graphics/PorterDuff$Mode;)V
    //   136: aload 6
    //   138: invokevirtual 1418	android/graphics/Rect:setEmpty	()V
    //   141: aload_0
    //   142: iconst_0
    //   143: putfield 1477	android/view/ViewRootImpl:mIsAnimating	Z
    //   146: aload_0
    //   147: getfield 1150	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   150: astore 6
    //   152: aload 6
    //   154: aload 6
    //   156: getfield 1582	android/view/View:mPrivateFlags	I
    //   159: bipush 32
    //   161: ior
    //   162: putfield 1582	android/view/View:mPrivateFlags	I
    //   165: aload_2
    //   166: iload_3
    //   167: ineg
    //   168: i2f
    //   169: iload 4
    //   171: ineg
    //   172: i2f
    //   173: invokevirtual 1586	android/graphics/Canvas:translate	(FF)V
    //   176: aload_0
    //   177: getfield 1300	android/view/ViewRootImpl:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   180: ifnull +11 -> 191
    //   183: aload_0
    //   184: getfield 1300	android/view/ViewRootImpl:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   187: aload_2
    //   188: invokevirtual 1589	android/content/res/CompatibilityInfo$Translator:translateCanvas	(Landroid/graphics/Canvas;)V
    //   191: iload 5
    //   193: ifeq +11 -> 204
    //   196: aload_0
    //   197: getfield 883	android/view/ViewRootImpl:mNoncompatDensity	I
    //   200: istore_3
    //   201: goto +5 -> 206
    //   204: iconst_0
    //   205: istore_3
    //   206: aload_2
    //   207: iload_3
    //   208: invokevirtual 1592	android/graphics/Canvas:setScreenDensity	(I)V
    //   211: aload_0
    //   212: getfield 1150	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   215: aload_2
    //   216: invokevirtual 1593	android/view/View:draw	(Landroid/graphics/Canvas;)V
    //   219: aload_0
    //   220: aload_2
    //   221: invokespecial 1595	android/view/ViewRootImpl:drawAccessibilityFocusedDrawableIfNeeded	(Landroid/graphics/Canvas;)V
    //   224: aload_1
    //   225: aload_2
    //   226: invokevirtual 1598	android/view/Surface:unlockCanvasAndPost	(Landroid/graphics/Canvas;)V
    //   229: iconst_1
    //   230: ireturn
    //   231: astore_1
    //   232: aload_0
    //   233: getfield 723	android/view/ViewRootImpl:mTag	Ljava/lang/String;
    //   236: ldc_w 1600
    //   239: aload_1
    //   240: invokestatic 946	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   243: pop
    //   244: aload_0
    //   245: iconst_1
    //   246: putfield 1602	android/view/ViewRootImpl:mLayoutRequested	Z
    //   249: iconst_0
    //   250: ireturn
    //   251: astore 6
    //   253: aload_1
    //   254: aload_2
    //   255: invokevirtual 1598	android/view/Surface:unlockCanvasAndPost	(Landroid/graphics/Canvas;)V
    //   258: aload 6
    //   260: athrow
    //   261: astore_1
    //   262: aload_0
    //   263: getfield 723	android/view/ViewRootImpl:mTag	Ljava/lang/String;
    //   266: ldc_w 1600
    //   269: aload_1
    //   270: invokestatic 946	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   273: pop
    //   274: aload_0
    //   275: iconst_1
    //   276: putfield 1602	android/view/ViewRootImpl:mLayoutRequested	Z
    //   279: goto -30 -> 249
    //   282: astore_1
    //   283: goto +49 -> 332
    //   286: astore_1
    //   287: aload_0
    //   288: getfield 723	android/view/ViewRootImpl:mTag	Ljava/lang/String;
    //   291: ldc_w 1604
    //   294: aload_1
    //   295: invokestatic 946	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   298: pop
    //   299: aload_0
    //   300: iconst_1
    //   301: putfield 1602	android/view/ViewRootImpl:mLayoutRequested	Z
    //   304: aload 6
    //   306: iload 8
    //   308: iload 9
    //   310: invokevirtual 1448	android/graphics/Rect:offset	(II)V
    //   313: iconst_0
    //   314: ireturn
    //   315: astore_1
    //   316: aload_0
    //   317: aload_1
    //   318: invokespecial 1535	android/view/ViewRootImpl:handleOutOfResourcesException	(Landroid/view/Surface$OutOfResourcesException;)V
    //   321: aload 6
    //   323: iload 8
    //   325: iload 9
    //   327: invokevirtual 1448	android/graphics/Rect:offset	(II)V
    //   330: iconst_0
    //   331: ireturn
    //   332: aload 6
    //   334: iload 8
    //   336: iload 9
    //   338: invokevirtual 1448	android/graphics/Rect:offset	(II)V
    //   341: aload_1
    //   342: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	343	0	this	ViewRootImpl
    //   0	343	1	paramSurface	Surface
    //   0	343	2	paramAttachInfo	View.AttachInfo
    //   0	343	3	paramInt1	int
    //   0	343	4	paramInt2	int
    //   0	343	5	paramBoolean	boolean
    //   0	343	6	paramRect1	Rect
    //   0	343	7	paramRect2	Rect
    //   10	325	8	i	int
    //   20	317	9	j	int
    //   41	10	10	k	int
    //   46	38	11	m	int
    // Exception table:
    //   from	to	target	type
    //   224	229	231	java/lang/IllegalArgumentException
    //   112	119	251	finally
    //   128	136	251	finally
    //   136	191	251	finally
    //   196	201	251	finally
    //   206	224	251	finally
    //   253	258	261	java/lang/IllegalArgumentException
    //   48	103	282	finally
    //   287	304	282	finally
    //   316	321	282	finally
    //   48	103	286	java/lang/IllegalArgumentException
    //   48	103	315	android/view/Surface$OutOfResourcesException
  }
  
  private void dumpViewHierarchy(String paramString, PrintWriter paramPrintWriter, View paramView)
  {
    paramPrintWriter.print(paramString);
    if (paramView == null)
    {
      paramPrintWriter.println("null");
      return;
    }
    paramPrintWriter.println(paramView.toString());
    if (!(paramView instanceof ViewGroup)) {
      return;
    }
    paramView = (ViewGroup)paramView;
    int i = paramView.getChildCount();
    if (i <= 0) {
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("  ");
    paramString = localStringBuilder.toString();
    for (int j = 0; j < i; j++) {
      dumpViewHierarchy(paramString, paramPrintWriter, paramView.getChildAt(j));
    }
  }
  
  @UnsupportedAppUsage
  private void enableHardwareAcceleration(WindowManager.LayoutParams paramLayoutParams)
  {
    Object localObject = this.mAttachInfo;
    boolean bool1 = false;
    ((View.AttachInfo)localObject).mHardwareAccelerated = false;
    ((View.AttachInfo)localObject).mHardwareAccelerationRequested = false;
    if (this.mTranslator != null) {
      return;
    }
    if (ForceDarkHelper.getInstance().enableHardwareAccelerationIfNeeded(paramLayoutParams))
    {
      if (!ThreadedRenderer.isAvailable()) {
        return;
      }
      int i;
      if ((paramLayoutParams.privateFlags & 0x1) != 0) {
        i = 1;
      } else {
        i = 0;
      }
      int j;
      if ((paramLayoutParams.privateFlags & 0x2) != 0) {
        j = 1;
      } else {
        j = 0;
      }
      if (i != 0)
      {
        this.mAttachInfo.mHardwareAccelerationRequested = true;
      }
      else if ((!ThreadedRenderer.sRendererDisabled) || ((ThreadedRenderer.sSystemRendererDisabled) && (j != 0)))
      {
        if (this.mAttachInfo.mThreadedRenderer != null) {
          this.mAttachInfo.mThreadedRenderer.destroy();
        }
        localObject = paramLayoutParams.surfaceInsets;
        if ((((Rect)localObject).left == 0) && (((Rect)localObject).right == 0) && (((Rect)localObject).top == 0) && (((Rect)localObject).bottom == 0)) {
          i = 0;
        } else {
          i = 1;
        }
        boolean bool2;
        if ((paramLayoutParams.format == -1) && (i == 0)) {
          bool2 = false;
        } else {
          bool2 = true;
        }
        if ((this.mContext.getResources().getConfiguration().isScreenWideColorGamut()) && (paramLayoutParams.getColorMode() == 1)) {
          bool1 = true;
        }
        this.mAttachInfo.mThreadedRenderer = ThreadedRenderer.create(this.mContext, bool2, paramLayoutParams.getTitle().toString());
        this.mAttachInfo.mThreadedRenderer.setWideGamut(bool1);
        if (this.mAttachInfo.mThreadedRenderer != null)
        {
          paramLayoutParams = this.mAttachInfo;
          paramLayoutParams.mHardwareAccelerationRequested = true;
          paramLayoutParams.mHardwareAccelerated = true;
        }
      }
    }
  }
  
  private void endDragResizing()
  {
    if (this.mDragResizing)
    {
      this.mDragResizing = false;
      if (this.mUseMTRenderer) {
        for (int i = this.mWindowCallbacks.size() - 1; i >= 0; i--) {
          ((WindowCallbacks)this.mWindowCallbacks.get(i)).onWindowDragResizeEnd();
        }
      }
      this.mFullRedrawNeeded = true;
    }
  }
  
  private Rect ensureInsetsNonNegative(Rect paramRect, String paramString)
  {
    if ((paramRect.left >= 0) && (paramRect.top >= 0) && (paramRect.right >= 0) && (paramRect.bottom >= 0)) {
      return paramRect;
    }
    String str = this.mTag;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Negative ");
    localStringBuilder.append(paramString);
    localStringBuilder.append("Insets: ");
    localStringBuilder.append(paramRect);
    localStringBuilder.append(", mFirst=");
    localStringBuilder.append(this.mFirst);
    Log.wtf(str, localStringBuilder.toString());
    return new Rect(Math.max(0, paramRect.left), Math.max(0, paramRect.top), Math.max(0, paramRect.right), Math.max(0, paramRect.bottom));
  }
  
  private boolean ensureTouchModeLocally(boolean paramBoolean)
  {
    if (this.mAttachInfo.mInTouchMode == paramBoolean) {
      return false;
    }
    View.AttachInfo localAttachInfo = this.mAttachInfo;
    localAttachInfo.mInTouchMode = paramBoolean;
    localAttachInfo.mTreeObserver.dispatchOnTouchModeChanged(paramBoolean);
    if (paramBoolean) {
      paramBoolean = enterTouchMode();
    } else {
      paramBoolean = leaveTouchMode();
    }
    return paramBoolean;
  }
  
  private boolean enterTouchMode()
  {
    View localView = this.mView;
    if ((localView != null) && (localView.hasFocus()))
    {
      localView = this.mView.findFocus();
      if ((localView != null) && (!localView.isFocusableInTouchMode()))
      {
        ViewGroup localViewGroup = findAncestorToTakeFocusInTouchMode(localView);
        if (localViewGroup != null) {
          return localViewGroup.requestFocus();
        }
        localView.clearFocusInternal(null, true, false);
        return true;
      }
    }
    return false;
  }
  
  private static ViewGroup findAncestorToTakeFocusInTouchMode(View paramView)
  {
    for (paramView = paramView.getParent(); (paramView instanceof ViewGroup); paramView = paramView.getParent())
    {
      paramView = (ViewGroup)paramView;
      if ((paramView.getDescendantFocusability() == 262144) && (paramView.isFocusableInTouchMode())) {
        return paramView;
      }
      if (paramView.isRootNamespace()) {
        return null;
      }
    }
    return null;
  }
  
  private AccessibilityNodeInfo findFocusedVirtualNode(AccessibilityNodeProvider paramAccessibilityNodeProvider)
  {
    Object localObject1 = paramAccessibilityNodeProvider.findFocus(1);
    if (localObject1 != null) {
      return (AccessibilityNodeInfo)localObject1;
    }
    if (!this.mContext.isAutofillCompatibilityEnabled()) {
      return null;
    }
    Object localObject2 = paramAccessibilityNodeProvider.createAccessibilityNodeInfo(-1);
    if (((AccessibilityNodeInfo)localObject2).isFocused()) {
      return (AccessibilityNodeInfo)localObject2;
    }
    localObject1 = new LinkedList();
    ((Queue)localObject1).offer(localObject2);
    while (!((Queue)localObject1).isEmpty())
    {
      AccessibilityNodeInfo localAccessibilityNodeInfo1 = (AccessibilityNodeInfo)((Queue)localObject1).poll();
      localObject2 = localAccessibilityNodeInfo1.getChildNodeIds();
      if ((localObject2 != null) && (((LongArray)localObject2).size() > 0))
      {
        int i = ((LongArray)localObject2).size();
        for (int j = 0; j < i; j++)
        {
          AccessibilityNodeInfo localAccessibilityNodeInfo2 = paramAccessibilityNodeProvider.createAccessibilityNodeInfo(AccessibilityNodeInfo.getVirtualDescendantId(((LongArray)localObject2).get(j)));
          if (localAccessibilityNodeInfo2 != null)
          {
            if (localAccessibilityNodeInfo2.isFocused()) {
              return localAccessibilityNodeInfo2;
            }
            ((Queue)localObject1).offer(localAccessibilityNodeInfo2);
          }
        }
        localAccessibilityNodeInfo1.recycle();
      }
    }
    return null;
  }
  
  private void finishInputEvent(QueuedInputEvent paramQueuedInputEvent)
  {
    Trace.asyncTraceEnd(8L, "deliverInputEvent", paramQueuedInputEvent.mEvent.getSequenceNumber());
    if (paramQueuedInputEvent.mReceiver != null)
    {
      int i = paramQueuedInputEvent.mFlags;
      int j = 1;
      boolean bool;
      if ((i & 0x8) != 0) {
        bool = true;
      } else {
        bool = false;
      }
      if ((paramQueuedInputEvent.mFlags & 0x40) == 0) {
        j = 0;
      }
      if (j != 0) {
        Trace.traceBegin(8L, "processInputEventBeforeFinish");
      }
      try
      {
        InputEvent localInputEvent = this.mInputCompatProcessor.processInputEventBeforeFinish(paramQueuedInputEvent.mEvent);
        Trace.traceEnd(8L);
        if (localInputEvent != null) {
          paramQueuedInputEvent.mReceiver.finishInputEvent(localInputEvent, bool);
        }
      }
      finally
      {
        Trace.traceEnd(8L);
      }
    }
    else
    {
      paramQueuedInputEvent.mEvent.recycleIfNeededAfterDispatch();
    }
    recycleQueuedInputEvent(paramQueuedInputEvent);
  }
  
  private void fireAccessibilityFocusEventIfHasFocusedNode()
  {
    if (!AccessibilityManager.getInstance(this.mContext).isEnabled()) {
      return;
    }
    View localView = this.mView.findFocus();
    if (localView == null) {
      return;
    }
    Object localObject = localView.getAccessibilityNodeProvider();
    if (localObject == null)
    {
      localView.sendAccessibilityEvent(8);
    }
    else
    {
      AccessibilityNodeInfo localAccessibilityNodeInfo = findFocusedVirtualNode((AccessibilityNodeProvider)localObject);
      if (localAccessibilityNodeInfo != null)
      {
        int i = AccessibilityNodeInfo.getVirtualDescendantId(localAccessibilityNodeInfo.getSourceNodeId());
        localObject = AccessibilityEvent.obtain(8);
        ((AccessibilityEvent)localObject).setSource(localView, i);
        ((AccessibilityEvent)localObject).setPackageName(localAccessibilityNodeInfo.getPackageName());
        ((AccessibilityEvent)localObject).setChecked(localAccessibilityNodeInfo.isChecked());
        ((AccessibilityEvent)localObject).setContentDescription(localAccessibilityNodeInfo.getContentDescription());
        ((AccessibilityEvent)localObject).setPassword(localAccessibilityNodeInfo.isPassword());
        ((AccessibilityEvent)localObject).getText().add(localAccessibilityNodeInfo.getText());
        ((AccessibilityEvent)localObject).setEnabled(localAccessibilityNodeInfo.isEnabled());
        localView.getParent().requestSendAccessibilityEvent(localView, (AccessibilityEvent)localObject);
        localAccessibilityNodeInfo.recycle();
      }
    }
  }
  
  private static void forceLayout(View paramView)
  {
    paramView.forceLayout();
    if ((paramView instanceof ViewGroup))
    {
      paramView = (ViewGroup)paramView;
      int i = paramView.getChildCount();
      for (int j = 0; j < i; j++) {
        forceLayout(paramView.getChildAt(j));
      }
    }
  }
  
  private Drawable getAccessibilityFocusedDrawable()
  {
    if (this.mAttachInfo.mAccessibilityFocusDrawable == null)
    {
      TypedValue localTypedValue = new TypedValue();
      if (this.mView.mContext.getTheme().resolveAttribute(17956871, localTypedValue, true)) {
        this.mAttachInfo.mAccessibilityFocusDrawable = this.mView.mContext.getDrawable(localTypedValue.resourceId);
      }
    }
    return this.mAttachInfo.mAccessibilityFocusDrawable;
  }
  
  private boolean getAccessibilityFocusedRect(Rect paramRect)
  {
    Object localObject = AccessibilityManager.getInstance(this.mView.mContext);
    if ((((AccessibilityManager)localObject).isEnabled()) && (((AccessibilityManager)localObject).isTouchExplorationEnabled()))
    {
      localObject = this.mAccessibilityFocusedHost;
      if ((localObject != null) && (((View)localObject).mAttachInfo != null))
      {
        if (((View)localObject).getAccessibilityNodeProvider() == null)
        {
          ((View)localObject).getBoundsOnScreen(paramRect, true);
        }
        else
        {
          localObject = this.mAccessibilityFocusedVirtualView;
          if (localObject == null) {
            break label142;
          }
          ((AccessibilityNodeInfo)localObject).getBoundsInScreen(paramRect);
        }
        localObject = this.mAttachInfo;
        paramRect.offset(0, ((View.AttachInfo)localObject).mViewRootImpl.mScrollY);
        paramRect.offset(-((View.AttachInfo)localObject).mWindowLeft, -((View.AttachInfo)localObject).mWindowTop);
        if (!paramRect.intersect(0, 0, ((View.AttachInfo)localObject).mViewRootImpl.mWidth, ((View.AttachInfo)localObject).mViewRootImpl.mHeight)) {
          paramRect.setEmpty();
        }
        return paramRect.isEmpty() ^ true;
        label142:
        return false;
      }
      return false;
    }
    return false;
  }
  
  private AudioManager getAudioManager()
  {
    View localView = this.mView;
    if (localView != null)
    {
      if (this.mAudioManager == null) {
        this.mAudioManager = ((AudioManager)localView.getContext().getSystemService("audio"));
      }
      return this.mAudioManager;
    }
    throw new IllegalStateException("getAudioManager called when there is no mView");
  }
  
  private AutofillManager getAutofillManager()
  {
    Object localObject = this.mView;
    if ((localObject instanceof ViewGroup))
    {
      localObject = (ViewGroup)localObject;
      if (((ViewGroup)localObject).getChildCount() > 0) {
        return (AutofillManager)((ViewGroup)localObject).getChildAt(0).getContext().getSystemService(AutofillManager.class);
      }
    }
    return null;
  }
  
  private View getCommonPredecessor(View paramView1, View paramView2)
  {
    if (this.mTempHashSet == null) {
      this.mTempHashSet = new HashSet();
    }
    HashSet localHashSet = this.mTempHashSet;
    localHashSet.clear();
    while (paramView1 != null)
    {
      localHashSet.add(paramView1);
      paramView1 = paramView1.mParent;
      if ((paramView1 instanceof View)) {
        paramView1 = (View)paramView1;
      } else {
        paramView1 = null;
      }
    }
    paramView1 = paramView2;
    while (paramView1 != null)
    {
      if (localHashSet.contains(paramView1))
      {
        localHashSet.clear();
        return paramView1;
      }
      paramView1 = paramView1.mParent;
      if ((paramView1 instanceof View)) {
        paramView1 = (View)paramView1;
      } else {
        paramView1 = null;
      }
    }
    localHashSet.clear();
    return null;
  }
  
  private static void getGfxInfo(View paramView, int[] paramArrayOfInt)
  {
    RenderNode localRenderNode = paramView.mRenderNode;
    paramArrayOfInt[0] += 1;
    if (localRenderNode != null) {
      paramArrayOfInt[1] += (int)localRenderNode.computeApproximateMemoryUsage();
    }
    if ((paramView instanceof ViewGroup))
    {
      paramView = (ViewGroup)paramView;
      int i = paramView.getChildCount();
      for (int j = 0; j < i; j++) {
        getGfxInfo(paramView.getChildAt(j), paramArrayOfInt);
      }
    }
  }
  
  private int getImpliedSystemUiVisibility(WindowManager.LayoutParams paramLayoutParams)
  {
    int i = 0;
    if ((paramLayoutParams.flags & 0x4000000) != 0) {
      i = 0x0 | 0x500;
    }
    int j = i;
    if ((paramLayoutParams.flags & 0x8000000) != 0) {
      j = i | 0x300;
    }
    return j;
  }
  
  private int getNightMode()
  {
    return this.mContext.getResources().getConfiguration().uiMode & 0x30;
  }
  
  private static int getRootMeasureSpec(int paramInt1, int paramInt2)
  {
    if (paramInt2 != -2)
    {
      if (paramInt2 != -1) {
        paramInt1 = View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824);
      } else {
        paramInt1 = View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824);
      }
    }
    else {
      paramInt1 = View.MeasureSpec.makeMeasureSpec(paramInt1, Integer.MIN_VALUE);
    }
    return paramInt1;
  }
  
  static HandlerActionQueue getRunQueue()
  {
    HandlerActionQueue localHandlerActionQueue = (HandlerActionQueue)sRunQueues.get();
    if (localHandlerActionQueue != null) {
      return localHandlerActionQueue;
    }
    localHandlerActionQueue = new HandlerActionQueue();
    sRunQueues.set(localHandlerActionQueue);
    return localHandlerActionQueue;
  }
  
  private View getSourceForAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    int i = AccessibilityNodeInfo.getAccessibilityViewId(paramAccessibilityEvent.getSourceNodeId());
    return AccessibilityNodeIdManager.getInstance().findView(i);
  }
  
  private ArrayList<View> getValidLayoutRequesters(ArrayList<View> paramArrayList, boolean paramBoolean)
  {
    int i = paramArrayList.size();
    Object localObject1 = null;
    int j = 0;
    Object localObject2;
    while (j < i)
    {
      View localView = (View)paramArrayList.get(j);
      localObject2 = localObject1;
      if (localView != null)
      {
        localObject2 = localObject1;
        if (localView.mAttachInfo != null)
        {
          localObject2 = localObject1;
          if (localView.mParent != null) {
            if (!paramBoolean)
            {
              localObject2 = localObject1;
              if ((localView.mPrivateFlags & 0x1000) != 4096) {}
            }
            else
            {
              int k = 0;
              localObject2 = localView;
              int m;
              for (;;)
              {
                m = k;
                if (localObject2 == null) {
                  break;
                }
                if ((((View)localObject2).mViewFlags & 0xC) == 8)
                {
                  m = 1;
                  break;
                }
                if ((((View)localObject2).mParent instanceof View)) {
                  localObject2 = (View)((View)localObject2).mParent;
                } else {
                  localObject2 = null;
                }
              }
              localObject2 = localObject1;
              if (m == 0)
              {
                localObject2 = localObject1;
                if (localObject1 == null) {
                  localObject2 = new ArrayList();
                }
                ((ArrayList)localObject2).add(localView);
              }
            }
          }
        }
      }
      j++;
      localObject1 = localObject2;
    }
    if (!paramBoolean) {
      for (j = 0; j < i; j++)
      {
        localObject2 = (View)paramArrayList.get(j);
        while ((localObject2 != null) && ((((View)localObject2).mPrivateFlags & 0x1000) != 0))
        {
          ((View)localObject2).mPrivateFlags &= 0xEFFF;
          if ((((View)localObject2).mParent instanceof View)) {
            localObject2 = (View)((View)localObject2).mParent;
          } else {
            localObject2 = null;
          }
        }
      }
    }
    paramArrayList.clear();
    return (ArrayList<View>)localObject1;
  }
  
  private void handleDragEvent(DragEvent paramDragEvent)
  {
    if ((this.mView != null) && (this.mAdded))
    {
      int i = paramDragEvent.mAction;
      if (i == 1)
      {
        this.mCurrentDragView = null;
        this.mDragDescription = paramDragEvent.mClipDescription;
      }
      else
      {
        if (i == 4) {
          this.mDragDescription = null;
        }
        paramDragEvent.mClipDescription = this.mDragDescription;
      }
      if (i == 6)
      {
        if (View.sCascadedDragDrop) {
          this.mView.dispatchDragEnterExitInPreN(paramDragEvent);
        }
        setDragFocus(null, paramDragEvent);
      }
      else
      {
        if ((i == 2) || (i == 3))
        {
          this.mDragPoint.set(paramDragEvent.mX, paramDragEvent.mY);
          localObject = this.mTranslator;
          if (localObject != null) {
            ((CompatibilityInfo.Translator)localObject).translatePointInScreenToAppWindow(this.mDragPoint);
          }
          int j = this.mCurScrollY;
          if (j != 0) {
            this.mDragPoint.offset(0.0F, j);
          }
          paramDragEvent.mX = this.mDragPoint.x;
          paramDragEvent.mY = this.mDragPoint.y;
        }
        Object localObject = this.mCurrentDragView;
        if ((i == 3) && (paramDragEvent.mClipData != null)) {
          paramDragEvent.mClipData.prepareToEnterProcess();
        }
        boolean bool = this.mView.dispatchDragEvent(paramDragEvent);
        if ((i == 2) && (!paramDragEvent.mEventHandlerWasCalled)) {
          setDragFocus(null, paramDragEvent);
        }
        if (localObject != this.mCurrentDragView)
        {
          if (localObject != null) {}
          try
          {
            this.mWindowSession.dragRecipientExited(this.mWindow);
            if (this.mCurrentDragView != null) {
              this.mWindowSession.dragRecipientEntered(this.mWindow);
            }
          }
          catch (RemoteException localRemoteException1)
          {
            Slog.e(this.mTag, "Unable to note drag target change");
          }
        }
        if (i == 3) {
          try
          {
            String str = this.mTag;
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            localStringBuilder.append("Reporting drop result: ");
            localStringBuilder.append(bool);
            Log.i(str, localStringBuilder.toString());
            this.mWindowSession.reportDropResult(this.mWindow, bool);
          }
          catch (RemoteException localRemoteException2)
          {
            Log.e(this.mTag, "Unable to report drop result");
          }
        }
        if (i == 4)
        {
          this.mCurrentDragView = null;
          setLocalDragState(null);
          View.AttachInfo localAttachInfo = this.mAttachInfo;
          localAttachInfo.mDragToken = null;
          if (localAttachInfo.mDragSurface != null)
          {
            this.mAttachInfo.mDragSurface.release();
            this.mAttachInfo.mDragSurface = null;
          }
        }
      }
    }
    paramDragEvent.recycle();
  }
  
  private void handleOutOfResourcesException(Surface.OutOfResourcesException paramOutOfResourcesException)
  {
    Log.e(this.mTag, "OutOfResourcesException initializing HW surface", paramOutOfResourcesException);
    try
    {
      if ((!this.mWindowSession.outOfMemory(this.mWindow)) && (Process.myUid() != 1000))
      {
        Slog.w(this.mTag, "No processes killed for memory; killing self");
        Process.killProcess(Process.myPid());
      }
    }
    catch (RemoteException paramOutOfResourcesException) {}
    this.mLayoutRequested = true;
  }
  
  private void handlePointerCaptureChanged(boolean paramBoolean)
  {
    if (this.mPointerCapture == paramBoolean) {
      return;
    }
    this.mPointerCapture = paramBoolean;
    View localView = this.mView;
    if (localView != null) {
      localView.dispatchPointerCaptureChanged(paramBoolean);
    }
  }
  
  private void handleWindowContentChangedEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    Object localObject = this.mAccessibilityFocusedHost;
    if ((localObject != null) && (this.mAccessibilityFocusedVirtualView != null))
    {
      AccessibilityNodeProvider localAccessibilityNodeProvider = ((View)localObject).getAccessibilityNodeProvider();
      if (localAccessibilityNodeProvider == null)
      {
        this.mAccessibilityFocusedHost = null;
        this.mAccessibilityFocusedVirtualView = null;
        ((View)localObject).clearAccessibilityFocusNoCallbacks(0);
        return;
      }
      int i = paramAccessibilityEvent.getContentChangeTypes();
      if (((i & 0x1) == 0) && (i != 0)) {
        return;
      }
      int j = AccessibilityNodeInfo.getAccessibilityViewId(paramAccessibilityEvent.getSourceNodeId());
      i = 0;
      paramAccessibilityEvent = this.mAccessibilityFocusedHost;
      while ((paramAccessibilityEvent != null) && (i == 0)) {
        if (j == paramAccessibilityEvent.getAccessibilityViewId())
        {
          i = 1;
        }
        else
        {
          paramAccessibilityEvent = paramAccessibilityEvent.getParent();
          if ((paramAccessibilityEvent instanceof View)) {
            paramAccessibilityEvent = (View)paramAccessibilityEvent;
          } else {
            paramAccessibilityEvent = null;
          }
        }
      }
      if (i == 0) {
        return;
      }
      i = AccessibilityNodeInfo.getVirtualDescendantId(this.mAccessibilityFocusedVirtualView.getSourceNodeId());
      paramAccessibilityEvent = this.mTempRect;
      this.mAccessibilityFocusedVirtualView.getBoundsInScreen(paramAccessibilityEvent);
      this.mAccessibilityFocusedVirtualView = localAccessibilityNodeProvider.createAccessibilityNodeInfo(i);
      AccessibilityNodeInfo localAccessibilityNodeInfo = this.mAccessibilityFocusedVirtualView;
      if (localAccessibilityNodeInfo == null)
      {
        this.mAccessibilityFocusedHost = null;
        ((View)localObject).clearAccessibilityFocusNoCallbacks(0);
        localAccessibilityNodeProvider.performAction(i, AccessibilityNodeInfo.AccessibilityAction.ACTION_CLEAR_ACCESSIBILITY_FOCUS.getId(), null);
        invalidateRectOnScreen(paramAccessibilityEvent);
      }
      else
      {
        localObject = localAccessibilityNodeInfo.getBoundsInScreen();
        if (!paramAccessibilityEvent.equals(localObject))
        {
          paramAccessibilityEvent.union((Rect)localObject);
          invalidateRectOnScreen(paramAccessibilityEvent);
        }
      }
      return;
    }
  }
  
  private void handleWindowFocusChanged()
  {
    try
    {
      if (!this.mWindowFocusChanged) {
        return;
      }
      this.mWindowFocusChanged = false;
      boolean bool1 = this.mUpcomingWindowFocus;
      boolean bool2 = this.mUpcomingInTouchMode;
      if (sNewInsetsMode != 0) {
        if (bool1) {
          this.mInsetsController.onWindowFocusGained();
        } else {
          this.mInsetsController.onWindowFocusLost();
        }
      }
      if (this.mAdded)
      {
        profileRendering(bool1);
        if (bool1)
        {
          ensureTouchModeLocally(bool2);
          if ((this.mAttachInfo.mThreadedRenderer != null) && (this.mSurface.isValid()))
          {
            this.mFullRedrawNeeded = true;
            try
            {
              Object localObject1 = this.mWindowAttributes;
              if (localObject1 != null) {
                localObject1 = ((WindowManager.LayoutParams)localObject1).surfaceInsets;
              } else {
                localObject1 = null;
              }
              this.mAttachInfo.mThreadedRenderer.initializeIfNeeded(this.mWidth, this.mHeight, this.mAttachInfo, this.mSurface, (Rect)localObject1);
            }
            catch (Surface.OutOfResourcesException localOutOfResourcesException)
            {
              Log.e(this.mTag, "OutOfResourcesException locking surface", localOutOfResourcesException);
              try
              {
                if (!this.mWindowSession.outOfMemory(this.mWindow))
                {
                  Slog.w(this.mTag, "No processes killed for memory; killing self");
                  Process.killProcess(Process.myPid());
                }
              }
              catch (RemoteException localRemoteException) {}
              localObject2 = this.mHandler;
              ((ViewRootHandler)localObject2).sendMessageDelayed(((ViewRootHandler)localObject2).obtainMessage(6), 500L);
              return;
            }
          }
        }
        this.mAttachInfo.mHasWindowFocus = bool1;
        this.mLastWasImTarget = WindowManager.LayoutParams.mayUseInputMethod(this.mWindowAttributes.flags);
        Object localObject2 = (InputMethodManager)this.mContext.getSystemService(InputMethodManager.class);
        if ((localObject2 != null) && (this.mLastWasImTarget) && (!isInLocalFocusMode())) {
          ((InputMethodManager)localObject2).onPreWindowFocus(this.mView, bool1);
        }
        if (this.mView != null)
        {
          this.mAttachInfo.mKeyDispatchState.reset();
          this.mView.dispatchWindowFocusChanged(bool1);
          this.mAttachInfo.mTreeObserver.dispatchOnWindowFocusChange(bool1);
          if (this.mAttachInfo.mTooltipHost != null) {
            this.mAttachInfo.mTooltipHost.hideTooltip();
          }
        }
        if (bool1)
        {
          if ((localObject2 != null) && (this.mLastWasImTarget) && (!isInLocalFocusMode()))
          {
            View localView = this.mView;
            ((InputMethodManager)localObject2).onPostWindowFocus(localView, localView.findFocus(), this.mWindowAttributes.softInputMode, this.mHasHadWindowFocus ^ true, this.mWindowAttributes.flags);
          }
          localObject2 = this.mWindowAttributes;
          ((WindowManager.LayoutParams)localObject2).softInputMode &= 0xFEFF;
          localObject2 = (WindowManager.LayoutParams)this.mView.getLayoutParams();
          ((WindowManager.LayoutParams)localObject2).softInputMode &= 0xFEFF;
          this.mHasHadWindowFocus = true;
          fireAccessibilityFocusEventIfHasFocusedNode();
        }
        else if (this.mPointerCapture)
        {
          handlePointerCaptureChanged(false);
        }
      }
      this.mFirstInputStage.onWindowFocusChanged(bool1);
      return;
    }
    finally {}
  }
  
  private boolean hasColorModeChanged(int paramInt)
  {
    if (this.mAttachInfo.mThreadedRenderer == null) {
      return false;
    }
    int i;
    if (paramInt == 1) {
      i = 1;
    } else {
      i = 0;
    }
    if (this.mAttachInfo.mThreadedRenderer.isWideGamut() == i) {
      return false;
    }
    return (i == 0) || (this.mContext.getResources().getConfiguration().isScreenWideColorGamut());
  }
  
  private void invalidateRectOnScreen(Rect paramRect)
  {
    Rect localRect = this.mDirty;
    localRect.union(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
    float f = this.mAttachInfo.mApplicationScale;
    boolean bool = localRect.intersect(0, 0, (int)(this.mWidth * f + 0.5F), (int)(this.mHeight * f + 0.5F));
    if (!bool) {
      localRect.setEmpty();
    }
    if ((!this.mWillDrawSoon) && ((bool) || (this.mIsAnimating))) {
      scheduleTraversals();
    }
  }
  
  @UnsupportedAppUsage
  public static void invokeFunctor(long paramLong, boolean paramBoolean)
  {
    ThreadedRenderer.invokeFunctor(paramLong, paramBoolean);
  }
  
  private boolean isAutofillUiShowing()
  {
    AutofillManager localAutofillManager = getAutofillManager();
    if (localAutofillManager == null) {
      return false;
    }
    return localAutofillManager.isAutofillUiShowing();
  }
  
  private boolean isInLocalFocusMode()
  {
    boolean bool;
    if ((this.mWindowAttributes.flags & 0x10000000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  static boolean isInTouchMode()
  {
    IWindowSession localIWindowSession = WindowManagerGlobal.peekWindowSession();
    if (localIWindowSession != null) {
      try
      {
        boolean bool = localIWindowSession.getInTouchMode();
        return bool;
      }
      catch (RemoteException localRemoteException) {}
    }
    return false;
  }
  
  private static boolean isNavigationKey(KeyEvent paramKeyEvent)
  {
    int i = paramKeyEvent.getKeyCode();
    if ((i != 61) && (i != 62) && (i != 66) && (i != 92) && (i != 93) && (i != 122) && (i != 123)) {
      switch (i)
      {
      default: 
        return false;
      }
    }
    return true;
  }
  
  static boolean isTerminalInputEvent(InputEvent paramInputEvent)
  {
    boolean bool1 = paramInputEvent instanceof KeyEvent;
    boolean bool2 = false;
    boolean bool3 = false;
    if (bool1)
    {
      if (((KeyEvent)paramInputEvent).getAction() == 1) {
        bool3 = true;
      }
      return bool3;
    }
    int i = ((MotionEvent)paramInputEvent).getAction();
    if ((i != 1) && (i != 3))
    {
      bool3 = bool2;
      if (i != 10) {}
    }
    else
    {
      bool3 = true;
    }
    return bool3;
  }
  
  private static boolean isTypingKey(KeyEvent paramKeyEvent)
  {
    boolean bool;
    if (paramKeyEvent.getUnicodeChar() > 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isViewDescendantOf(View paramView1, View paramView2)
  {
    boolean bool = true;
    if (paramView1 == paramView2) {
      return true;
    }
    paramView1 = paramView1.getParent();
    if ((!(paramView1 instanceof ViewGroup)) || (!isViewDescendantOf((View)paramView1, paramView2))) {
      bool = false;
    }
    return bool;
  }
  
  private boolean leaveTouchMode()
  {
    View localView = this.mView;
    if (localView != null)
    {
      if (localView.hasFocus())
      {
        localView = this.mView.findFocus();
        if (!(localView instanceof ViewGroup)) {
          return false;
        }
        if (((ViewGroup)localView).getDescendantFocusability() != 262144) {
          return false;
        }
      }
      return this.mView.restoreDefaultFocus();
    }
    return false;
  }
  
  private void maybeHandleWindowMove(Rect paramRect)
  {
    int i;
    if ((this.mAttachInfo.mWindowLeft == paramRect.left) && (this.mAttachInfo.mWindowTop == paramRect.top)) {
      i = 0;
    } else {
      i = 1;
    }
    if (i != 0)
    {
      CompatibilityInfo.Translator localTranslator = this.mTranslator;
      if (localTranslator != null) {
        localTranslator.translateRectInScreenToAppWinFrame(paramRect);
      }
      this.mAttachInfo.mWindowLeft = paramRect.left;
      this.mAttachInfo.mWindowTop = paramRect.top;
    }
    if ((i != 0) || (this.mAttachInfo.mNeedsUpdateLightCenter))
    {
      if (this.mAttachInfo.mThreadedRenderer != null) {
        this.mAttachInfo.mThreadedRenderer.setLightCenter(this.mAttachInfo);
      }
      this.mAttachInfo.mNeedsUpdateLightCenter = false;
    }
  }
  
  private void maybeUpdateTooltip(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getPointerCount() != 1) {
      return;
    }
    int i = paramMotionEvent.getActionMasked();
    if ((i != 9) && (i != 7) && (i != 10)) {
      return;
    }
    Object localObject = AccessibilityManager.getInstance(this.mContext);
    if ((((AccessibilityManager)localObject).isEnabled()) && (((AccessibilityManager)localObject).isTouchExplorationEnabled())) {
      return;
    }
    localObject = this.mView;
    if (localObject == null)
    {
      Slog.d(this.mTag, "maybeUpdateTooltip called after view was removed");
      return;
    }
    ((View)localObject).dispatchTooltipHoverEvent(paramMotionEvent);
  }
  
  private boolean measureHierarchy(View paramView, WindowManager.LayoutParams paramLayoutParams, Resources paramResources, int paramInt1, int paramInt2)
  {
    boolean bool1 = false;
    int i = 0;
    int j = i;
    if (paramLayoutParams.width == -2)
    {
      DisplayMetrics localDisplayMetrics = paramResources.getDisplayMetrics();
      paramResources.getValue(17105071, this.mTmpValue, true);
      int k = 0;
      if (this.mTmpValue.type == 5) {
        k = (int)this.mTmpValue.getDimension(localDisplayMetrics);
      }
      j = i;
      if (k != 0)
      {
        j = i;
        if (paramInt1 > k)
        {
          j = getRootMeasureSpec(k, paramLayoutParams.width);
          int m = getRootMeasureSpec(paramInt2, paramLayoutParams.height);
          performMeasure(j, m);
          if ((paramView.getMeasuredWidthAndState() & 0x1000000) == 0)
          {
            j = 1;
          }
          else
          {
            performMeasure(getRootMeasureSpec((k + paramInt1) / 2, paramLayoutParams.width), m);
            j = i;
            if ((paramView.getMeasuredWidthAndState() & 0x1000000) == 0) {
              j = 1;
            }
          }
        }
      }
    }
    boolean bool2 = bool1;
    if (j == 0)
    {
      performMeasure(getRootMeasureSpec(paramInt1, paramLayoutParams.width), getRootMeasureSpec(paramInt2, paramLayoutParams.height));
      if (this.mWidth == paramView.getMeasuredWidth())
      {
        bool2 = bool1;
        if (this.mHeight == paramView.getMeasuredHeight()) {}
      }
      else
      {
        bool2 = true;
      }
    }
    return bool2;
  }
  
  private void notifySurfaceDestroyed()
  {
    this.mSurfaceHolder.ungetCallbacks();
    SurfaceHolder.Callback[] arrayOfCallback = this.mSurfaceHolder.getCallbacks();
    if (arrayOfCallback != null)
    {
      int i = arrayOfCallback.length;
      for (int j = 0; j < i; j++) {
        arrayOfCallback[j].surfaceDestroyed(this.mSurfaceHolder);
      }
    }
  }
  
  private QueuedInputEvent obtainQueuedInputEvent(InputEvent paramInputEvent, InputEventReceiver paramInputEventReceiver, int paramInt)
  {
    QueuedInputEvent localQueuedInputEvent = this.mQueuedInputEventPool;
    if (localQueuedInputEvent != null)
    {
      this.mQueuedInputEventPoolSize -= 1;
      this.mQueuedInputEventPool = localQueuedInputEvent.mNext;
      localQueuedInputEvent.mNext = null;
    }
    else
    {
      localQueuedInputEvent = new QueuedInputEvent(null);
    }
    localQueuedInputEvent.mEvent = paramInputEvent;
    localQueuedInputEvent.mReceiver = paramInputEventReceiver;
    localQueuedInputEvent.mFlags = paramInt;
    return localQueuedInputEvent;
  }
  
  private void performConfigurationChange(MergedConfiguration paramMergedConfiguration, boolean paramBoolean, int paramInt)
  {
    if (paramMergedConfiguration != null)
    {
      if (this.mIsCastModeRotationChanged) {
        return;
      }
      ??? = paramMergedConfiguration.getGlobalConfiguration();
      Configuration localConfiguration = paramMergedConfiguration.getOverrideConfiguration();
      CompatibilityInfo localCompatibilityInfo = this.mDisplay.getDisplayAdjustments().getCompatibilityInfo();
      paramMergedConfiguration = (MergedConfiguration)???;
      if (!localCompatibilityInfo.equals(CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO))
      {
        paramMergedConfiguration = new Configuration((Configuration)???);
        localCompatibilityInfo.applyToConfiguration(this.mNoncompatDensity, paramMergedConfiguration);
      }
      synchronized (sConfigCallbacks)
      {
        for (int i = sConfigCallbacks.size() - 1; i >= 0; i--) {
          ((ConfigChangedCallback)sConfigCallbacks.get(i)).onConfigurationChanged(paramMergedConfiguration);
        }
        this.mLastReportedMergedConfiguration.setConfiguration(paramMergedConfiguration, localConfiguration);
        this.mForceNextConfigUpdate = paramBoolean;
        paramMergedConfiguration = this.mActivityConfigCallback;
        if (paramMergedConfiguration != null) {
          paramMergedConfiguration.onConfigurationChanged(localConfiguration, paramInt);
        } else {
          updateConfiguration(paramInt);
        }
        this.mForceNextConfigUpdate = false;
        return;
      }
    }
    throw new IllegalArgumentException("No merged config provided.");
  }
  
  private void performDraw()
  {
    if ((this.mAttachInfo.mDisplayState == 1) && (!this.mReportNextDraw)) {
      return;
    }
    if (this.mView == null) {
      return;
    }
    boolean bool;
    if ((!this.mFullRedrawNeeded) && (!this.mReportNextDraw)) {
      bool = false;
    } else {
      bool = true;
    }
    this.mFullRedrawNeeded = false;
    this.mIsDrawing = true;
    Trace.traceBegin(8L, "draw");
    int i = 0;
    int j = 0;
    int k = i;
    Object localObject1;
    Object localObject3;
    if (this.mAttachInfo.mThreadedRenderer != null)
    {
      k = i;
      if (this.mAttachInfo.mThreadedRenderer.isEnabled())
      {
        localObject1 = this.mAttachInfo.mTreeObserver.captureFrameCommitCallbacks();
        if (this.mReportNextDraw)
        {
          k = 1;
          localObject3 = this.mAttachInfo.mHandler;
          this.mAttachInfo.mThreadedRenderer.setFrameCompleteCallback(new _..Lambda.ViewRootImpl.YBiqAhbCbXVPSKdbE3K4rH2gpxI(this, (Handler)localObject3, (ArrayList)localObject1));
        }
        for (;;)
        {
          break;
          k = j;
          if (localObject1 != null)
          {
            k = j;
            if (((ArrayList)localObject1).size() > 0)
            {
              localObject3 = this.mAttachInfo.mHandler;
              this.mAttachInfo.mThreadedRenderer.setFrameCompleteCallback(new _..Lambda.ViewRootImpl.zlBUjCwDtoAWMNaHI62DIq_eKFY((Handler)localObject3, (ArrayList)localObject1));
              k = i;
            }
          }
        }
      }
    }
    try
    {
      bool = draw(bool);
      j = k;
      if (k != 0)
      {
        j = k;
        if (!bool)
        {
          this.mAttachInfo.mThreadedRenderer.setFrameCompleteCallback(null);
          j = 0;
        }
      }
      this.mIsDrawing = false;
      Trace.traceEnd(8L);
      if (this.mAttachInfo.mPendingAnimatingRenderNodes != null)
      {
        i = this.mAttachInfo.mPendingAnimatingRenderNodes.size();
        for (k = 0; k < i; k++) {
          ((RenderNode)this.mAttachInfo.mPendingAnimatingRenderNodes.get(k)).endAllAnimators();
        }
        this.mAttachInfo.mPendingAnimatingRenderNodes.clear();
      }
      if (this.mReportNextDraw)
      {
        this.mReportNextDraw = false;
        localObject1 = this.mWindowDrawCountDown;
        if (localObject1 != null)
        {
          try
          {
            ((CountDownLatch)localObject1).await();
          }
          catch (InterruptedException localInterruptedException)
          {
            Log.e(this.mTag, "Window redraw count down interrupted!");
          }
          this.mWindowDrawCountDown = null;
        }
        if (this.mAttachInfo.mThreadedRenderer != null) {
          this.mAttachInfo.mThreadedRenderer.setStopped(this.mStopped);
        }
        if ((this.mSurfaceHolder != null) && (this.mSurface.isValid()))
        {
          localObject3 = new SurfaceCallbackHelper(new _..Lambda.ViewRootImpl.dznxCZGM2R1fsBljsJKomLjBRoM(this));
          SurfaceHolder.Callback[] arrayOfCallback = this.mSurfaceHolder.getCallbacks();
          ((SurfaceCallbackHelper)localObject3).dispatchSurfaceRedrawNeededAsync(this.mSurfaceHolder, arrayOfCallback);
        }
        else if (j == 0)
        {
          if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.fence();
          }
          pendingDrawFinished();
        }
      }
      return;
    }
    finally
    {
      this.mIsDrawing = false;
      Trace.traceEnd(8L);
    }
  }
  
  private void performLayout(WindowManager.LayoutParams paramLayoutParams, int paramInt1, int paramInt2)
  {
    this.mLayoutRequested = false;
    this.mScrollMayChange = true;
    this.mInLayout = true;
    View localView = this.mView;
    if (localView == null) {
      return;
    }
    Trace.traceBegin(8L, "layout");
    try
    {
      localView.layout(0, 0, localView.getMeasuredWidth(), localView.getMeasuredHeight());
      this.mInLayout = false;
      if (this.mLayoutRequesters.size() > 0)
      {
        ArrayList localArrayList = getValidLayoutRequesters(this.mLayoutRequesters, false);
        if (localArrayList != null)
        {
          this.mHandlingLayoutInLayoutRequest = true;
          int i = localArrayList.size();
          Object localObject;
          for (int j = 0; j < i; j++)
          {
            localObject = (View)localArrayList.get(j);
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            localStringBuilder.append("requestLayout() improperly called by ");
            localStringBuilder.append(localObject);
            localStringBuilder.append(" during layout: running second layout pass");
            Log.w("View", localStringBuilder.toString());
            ((View)localObject).requestLayout();
          }
          measureHierarchy(localView, paramLayoutParams, this.mView.getContext().getResources(), paramInt1, paramInt2);
          this.mInLayout = true;
          localView.layout(0, 0, localView.getMeasuredWidth(), localView.getMeasuredHeight());
          this.mHandlingLayoutInLayoutRequest = false;
          localArrayList = getValidLayoutRequesters(this.mLayoutRequesters, true);
          if (localArrayList != null)
          {
            localObject = getRunQueue();
            paramLayoutParams = new android/view/ViewRootImpl$2;
            paramLayoutParams.<init>(this, localArrayList);
            ((HandlerActionQueue)localObject).post(paramLayoutParams);
          }
        }
      }
      Trace.traceEnd(8L);
      this.mInLayout = false;
      return;
    }
    finally
    {
      Trace.traceEnd(8L);
    }
  }
  
  private void performMeasure(int paramInt1, int paramInt2)
  {
    if (this.mView == null) {
      return;
    }
    Trace.traceBegin(8L, "measure");
    try
    {
      this.mView.measure(paramInt1, paramInt2);
      return;
    }
    finally
    {
      Trace.traceEnd(8L);
    }
  }
  
  /* Error */
  private void performTraversals()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 1150	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   4: astore_1
    //   5: aload_1
    //   6: ifnull +6226 -> 6232
    //   9: aload_0
    //   10: getfield 827	android/view/ViewRootImpl:mAdded	Z
    //   13: ifne +6 -> 19
    //   16: goto +6216 -> 6232
    //   19: aload_0
    //   20: iconst_1
    //   21: putfield 2591	android/view/ViewRootImpl:mIsInTraversal	Z
    //   24: aload_0
    //   25: iconst_1
    //   26: putfield 2290	android/view/ViewRootImpl:mWillDrawSoon	Z
    //   29: iconst_0
    //   30: istore_2
    //   31: aload_0
    //   32: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   35: astore_3
    //   36: aload_0
    //   37: invokevirtual 2594	android/view/ViewRootImpl:getHostVisibility	()I
    //   40: istore 4
    //   42: aload_0
    //   43: getfield 825	android/view/ViewRootImpl:mFirst	Z
    //   46: ifne +32 -> 78
    //   49: aload_0
    //   50: getfield 816	android/view/ViewRootImpl:mViewVisibility	I
    //   53: iload 4
    //   55: if_icmpne +17 -> 72
    //   58: aload_0
    //   59: getfield 2596	android/view/ViewRootImpl:mNewSurfaceNeeded	Z
    //   62: ifne +10 -> 72
    //   65: aload_0
    //   66: getfield 2598	android/view/ViewRootImpl:mAppVisibilityChanged	Z
    //   69: ifeq +9 -> 78
    //   72: iconst_1
    //   73: istore 5
    //   75: goto +6 -> 81
    //   78: iconst_0
    //   79: istore 5
    //   81: aload_0
    //   82: iconst_0
    //   83: putfield 2598	android/view/ViewRootImpl:mAppVisibilityChanged	Z
    //   86: aload_0
    //   87: getfield 825	android/view/ViewRootImpl:mFirst	Z
    //   90: ifne +46 -> 136
    //   93: aload_0
    //   94: getfield 816	android/view/ViewRootImpl:mViewVisibility	I
    //   97: ifne +9 -> 106
    //   100: iconst_1
    //   101: istore 6
    //   103: goto +6 -> 109
    //   106: iconst_0
    //   107: istore 6
    //   109: iload 4
    //   111: ifne +9 -> 120
    //   114: iconst_1
    //   115: istore 7
    //   117: goto +6 -> 123
    //   120: iconst_0
    //   121: istore 7
    //   123: iload 6
    //   125: iload 7
    //   127: if_icmpeq +9 -> 136
    //   130: iconst_1
    //   131: istore 8
    //   133: goto +6 -> 139
    //   136: iconst_0
    //   137: istore 8
    //   139: aconst_null
    //   140: astore 9
    //   142: aload_0
    //   143: getfield 595	android/view/ViewRootImpl:mWindowAttributesChanged	Z
    //   146: ifeq +17 -> 163
    //   149: aload_0
    //   150: iconst_0
    //   151: putfield 595	android/view/ViewRootImpl:mWindowAttributesChanged	Z
    //   154: aload_3
    //   155: astore 9
    //   157: iconst_1
    //   158: istore 10
    //   160: goto +6 -> 166
    //   163: iconst_0
    //   164: istore 10
    //   166: aload_0
    //   167: getfield 761	android/view/ViewRootImpl:mDisplay	Landroid/view/Display;
    //   170: invokevirtual 2447	android/view/Display:getDisplayAdjustments	()Landroid/view/DisplayAdjustments;
    //   173: invokevirtual 2453	android/view/DisplayAdjustments:getCompatibilityInfo	()Landroid/content/res/CompatibilityInfo;
    //   176: invokevirtual 2601	android/content/res/CompatibilityInfo:supportsScreen	()Z
    //   179: istore 11
    //   181: aload_0
    //   182: getfield 569	android/view/ViewRootImpl:mLastInCompatMode	Z
    //   185: istore 12
    //   187: iload 11
    //   189: iload 12
    //   191: if_icmpne +61 -> 252
    //   194: aload_0
    //   195: iconst_1
    //   196: putfield 1528	android/view/ViewRootImpl:mFullRedrawNeeded	Z
    //   199: aload_0
    //   200: iconst_1
    //   201: putfield 1602	android/view/ViewRootImpl:mLayoutRequested	Z
    //   204: iload 12
    //   206: ifeq +23 -> 229
    //   209: aload_3
    //   210: aload_3
    //   211: getfield 1654	android/view/WindowManager$LayoutParams:privateFlags	I
    //   214: sipush 65407
    //   217: iand
    //   218: putfield 1654	android/view/WindowManager$LayoutParams:privateFlags	I
    //   221: aload_0
    //   222: iconst_0
    //   223: putfield 569	android/view/ViewRootImpl:mLastInCompatMode	Z
    //   226: goto +20 -> 246
    //   229: aload_3
    //   230: aload_3
    //   231: getfield 1654	android/view/WindowManager$LayoutParams:privateFlags	I
    //   234: sipush 128
    //   237: ior
    //   238: putfield 1654	android/view/WindowManager$LayoutParams:privateFlags	I
    //   241: aload_0
    //   242: iconst_1
    //   243: putfield 569	android/view/ViewRootImpl:mLastInCompatMode	Z
    //   246: aload_3
    //   247: astore 9
    //   249: goto +3 -> 252
    //   252: aload_0
    //   253: iconst_0
    //   254: putfield 597	android/view/ViewRootImpl:mWindowAttributesChangesFlag	I
    //   257: aload_0
    //   258: getfield 800	android/view/ViewRootImpl:mWinFrame	Landroid/graphics/Rect;
    //   261: astore 13
    //   263: aload_0
    //   264: getfield 825	android/view/ViewRootImpl:mFirst	Z
    //   267: ifeq +175 -> 442
    //   270: aload_0
    //   271: iconst_1
    //   272: putfield 1528	android/view/ViewRootImpl:mFullRedrawNeeded	Z
    //   275: aload_0
    //   276: iconst_1
    //   277: putfield 1602	android/view/ViewRootImpl:mLayoutRequested	Z
    //   280: aload_0
    //   281: getfield 751	android/view/ViewRootImpl:mContext	Landroid/content/Context;
    //   284: invokevirtual 865	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   287: invokevirtual 1667	android/content/res/Resources:getConfiguration	()Landroid/content/res/Configuration;
    //   290: astore 14
    //   292: aload_3
    //   293: invokestatic 2604	android/view/ViewRootImpl:shouldUseDisplaySize	(Landroid/view/WindowManager$LayoutParams;)Z
    //   296: ifeq +38 -> 334
    //   299: new 2606	android/graphics/Point
    //   302: dup
    //   303: invokespecial 2607	android/graphics/Point:<init>	()V
    //   306: astore 15
    //   308: aload_0
    //   309: getfield 761	android/view/ViewRootImpl:mDisplay	Landroid/view/Display;
    //   312: aload 15
    //   314: invokevirtual 2611	android/view/Display:getRealSize	(Landroid/graphics/Point;)V
    //   317: aload 15
    //   319: getfield 2613	android/graphics/Point:x	I
    //   322: istore 6
    //   324: aload 15
    //   326: getfield 2615	android/graphics/Point:y	I
    //   329: istore 7
    //   331: goto +21 -> 352
    //   334: aload_0
    //   335: getfield 800	android/view/ViewRootImpl:mWinFrame	Landroid/graphics/Rect;
    //   338: invokevirtual 2617	android/graphics/Rect:width	()I
    //   341: istore 6
    //   343: aload_0
    //   344: getfield 800	android/view/ViewRootImpl:mWinFrame	Landroid/graphics/Rect;
    //   347: invokevirtual 2619	android/graphics/Rect:height	()I
    //   350: istore 7
    //   352: aload_0
    //   353: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   356: astore 15
    //   358: aload 15
    //   360: iconst_1
    //   361: putfield 2622	android/view/View$AttachInfo:mUse32BitDrawingCache	Z
    //   364: aload 15
    //   366: iload 4
    //   368: putfield 2625	android/view/View$AttachInfo:mWindowVisibility	I
    //   371: aload 15
    //   373: iconst_0
    //   374: putfield 1142	android/view/View$AttachInfo:mRecomputeGlobalAttributes	Z
    //   377: aload_0
    //   378: getfield 663	android/view/ViewRootImpl:mLastConfigurationFromResources	Landroid/content/res/Configuration;
    //   381: aload 14
    //   383: invokevirtual 2628	android/content/res/Configuration:setTo	(Landroid/content/res/Configuration;)V
    //   386: aload_0
    //   387: aload_0
    //   388: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   391: getfield 1145	android/view/View$AttachInfo:mSystemUiVisibility	I
    //   394: putfield 2630	android/view/ViewRootImpl:mLastSystemUiVisibility	I
    //   397: aload_0
    //   398: getfield 2632	android/view/ViewRootImpl:mViewLayoutDirectionInitial	I
    //   401: iconst_2
    //   402: if_icmpne +12 -> 414
    //   405: aload_1
    //   406: aload 14
    //   408: invokevirtual 2635	android/content/res/Configuration:getLayoutDirection	()I
    //   411: invokevirtual 2638	android/view/View:setLayoutDirection	(I)V
    //   414: aload_1
    //   415: aload_0
    //   416: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   419: iconst_0
    //   420: invokevirtual 2641	android/view/View:dispatchAttachedToWindow	(Landroid/view/View$AttachInfo;I)V
    //   423: aload_0
    //   424: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   427: getfield 1383	android/view/View$AttachInfo:mTreeObserver	Landroid/view/ViewTreeObserver;
    //   430: iconst_1
    //   431: invokevirtual 2644	android/view/ViewTreeObserver:dispatchOnWindowAttachedChange	(Z)V
    //   434: aload_0
    //   435: aload_1
    //   436: invokevirtual 2647	android/view/ViewRootImpl:dispatchApplyInsets	(Landroid/view/View;)V
    //   439: goto +63 -> 502
    //   442: aload 13
    //   444: invokevirtual 2617	android/graphics/Rect:width	()I
    //   447: istore 16
    //   449: aload 13
    //   451: invokevirtual 2619	android/graphics/Rect:height	()I
    //   454: istore 17
    //   456: iload 16
    //   458: aload_0
    //   459: getfield 790	android/view/ViewRootImpl:mWidth	I
    //   462: if_icmpne +20 -> 482
    //   465: iload 17
    //   467: istore 7
    //   469: iload 16
    //   471: istore 6
    //   473: iload 17
    //   475: aload_0
    //   476: getfield 792	android/view/ViewRootImpl:mHeight	I
    //   479: if_icmpeq +23 -> 502
    //   482: aload_0
    //   483: iconst_1
    //   484: putfield 1528	android/view/ViewRootImpl:mFullRedrawNeeded	Z
    //   487: aload_0
    //   488: iconst_1
    //   489: putfield 1602	android/view/ViewRootImpl:mLayoutRequested	Z
    //   492: iconst_1
    //   493: istore_2
    //   494: iload 16
    //   496: istore 6
    //   498: iload 17
    //   500: istore 7
    //   502: iload 5
    //   504: ifeq +76 -> 580
    //   507: aload_0
    //   508: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   511: iload 4
    //   513: putfield 2625	android/view/View$AttachInfo:mWindowVisibility	I
    //   516: aload_1
    //   517: iload 4
    //   519: invokevirtual 2650	android/view/View:dispatchWindowVisibilityChanged	(I)V
    //   522: iload 8
    //   524: ifeq +24 -> 548
    //   527: iload 4
    //   529: ifne +9 -> 538
    //   532: iconst_1
    //   533: istore 12
    //   535: goto +6 -> 541
    //   538: iconst_0
    //   539: istore 12
    //   541: aload_1
    //   542: iload 12
    //   544: invokevirtual 2653	android/view/View:dispatchVisibilityAggregated	(Z)Z
    //   547: pop
    //   548: iload 4
    //   550: ifne +10 -> 560
    //   553: aload_0
    //   554: getfield 2596	android/view/ViewRootImpl:mNewSurfaceNeeded	Z
    //   557: ifeq +11 -> 568
    //   560: aload_0
    //   561: invokespecial 2655	android/view/ViewRootImpl:endDragResizing	()V
    //   564: aload_0
    //   565: invokevirtual 2657	android/view/ViewRootImpl:destroyHardwareResources	()V
    //   568: iload 4
    //   570: bipush 8
    //   572: if_icmpne +8 -> 580
    //   575: aload_0
    //   576: iconst_0
    //   577: putfield 2269	android/view/ViewRootImpl:mHasHadWindowFocus	Z
    //   580: aload_0
    //   581: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   584: getfield 2625	android/view/View$AttachInfo:mWindowVisibility	I
    //   587: ifeq +7 -> 594
    //   590: aload_1
    //   591: invokevirtual 2660	android/view/View:clearAccessibilityFocus	()V
    //   594: invokestatic 2579	android/view/ViewRootImpl:getRunQueue	()Landroid/view/HandlerActionQueue;
    //   597: aload_0
    //   598: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   601: getfield 2503	android/view/View$AttachInfo:mHandler	Landroid/os/Handler;
    //   604: invokevirtual 2664	android/view/HandlerActionQueue:executeActions	(Landroid/os/Handler;)V
    //   607: iconst_0
    //   608: istore 8
    //   610: aload_0
    //   611: getfield 1602	android/view/ViewRootImpl:mLayoutRequested	Z
    //   614: ifeq +30 -> 644
    //   617: aload_0
    //   618: getfield 563	android/view/ViewRootImpl:mStopped	Z
    //   621: ifeq +10 -> 631
    //   624: aload_0
    //   625: getfield 1494	android/view/ViewRootImpl:mReportNextDraw	Z
    //   628: ifeq +16 -> 644
    //   631: aload_0
    //   632: getfield 580	android/view/ViewRootImpl:mIsCastModeRotationChanged	Z
    //   635: ifne +9 -> 644
    //   638: iconst_1
    //   639: istore 18
    //   641: goto +6 -> 647
    //   644: iconst_0
    //   645: istore 18
    //   647: iload 18
    //   649: ifeq +323 -> 972
    //   652: aload_0
    //   653: getfield 1150	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   656: invokevirtual 1966	android/view/View:getContext	()Landroid/content/Context;
    //   659: invokevirtual 865	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   662: astore 14
    //   664: aload_0
    //   665: getfield 825	android/view/ViewRootImpl:mFirst	Z
    //   668: ifeq +37 -> 705
    //   671: aload_0
    //   672: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   675: astore 15
    //   677: aload_0
    //   678: getfield 2666	android/view/ViewRootImpl:mAddedTouchMode	Z
    //   681: istore 12
    //   683: aload 15
    //   685: iload 12
    //   687: iconst_1
    //   688: ixor
    //   689: putfield 1119	android/view/View$AttachInfo:mInTouchMode	Z
    //   692: aload_0
    //   693: iload 12
    //   695: invokespecial 2220	android/view/ViewRootImpl:ensureTouchModeLocally	(Z)Z
    //   698: pop
    //   699: iconst_0
    //   700: istore 8
    //   702: goto +248 -> 950
    //   705: aload_0
    //   706: getfield 618	android/view/ViewRootImpl:mPendingOverscanInsets	Landroid/graphics/Rect;
    //   709: aload_0
    //   710: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   713: getfield 2669	android/view/View$AttachInfo:mOverscanInsets	Landroid/graphics/Rect;
    //   716: invokevirtual 1283	android/graphics/Rect:equals	(Ljava/lang/Object;)Z
    //   719: ifne +6 -> 725
    //   722: iconst_1
    //   723: istore 8
    //   725: aload_0
    //   726: getfield 624	android/view/ViewRootImpl:mPendingContentInsets	Landroid/graphics/Rect;
    //   729: aload_0
    //   730: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   733: getfield 2672	android/view/View$AttachInfo:mContentInsets	Landroid/graphics/Rect;
    //   736: invokevirtual 1283	android/graphics/Rect:equals	(Ljava/lang/Object;)Z
    //   739: ifne +6 -> 745
    //   742: iconst_1
    //   743: istore 8
    //   745: aload_0
    //   746: getfield 622	android/view/ViewRootImpl:mPendingStableInsets	Landroid/graphics/Rect;
    //   749: aload_0
    //   750: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   753: getfield 2675	android/view/View$AttachInfo:mStableInsets	Landroid/graphics/Rect;
    //   756: invokevirtual 1283	android/graphics/Rect:equals	(Ljava/lang/Object;)Z
    //   759: ifne +6 -> 765
    //   762: iconst_1
    //   763: istore 8
    //   765: aload_0
    //   766: getfield 640	android/view/ViewRootImpl:mPendingDisplayCutout	Landroid/view/DisplayCutout$ParcelableWrapper;
    //   769: aload_0
    //   770: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   773: getfield 2678	android/view/View$AttachInfo:mDisplayCutout	Landroid/view/DisplayCutout$ParcelableWrapper;
    //   776: invokevirtual 2679	android/view/DisplayCutout$ParcelableWrapper:equals	(Ljava/lang/Object;)Z
    //   779: ifne +6 -> 785
    //   782: iconst_1
    //   783: istore 8
    //   785: aload_0
    //   786: getfield 620	android/view/ViewRootImpl:mPendingVisibleInsets	Landroid/graphics/Rect;
    //   789: aload_0
    //   790: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   793: getfield 2682	android/view/View$AttachInfo:mVisibleInsets	Landroid/graphics/Rect;
    //   796: invokevirtual 1283	android/graphics/Rect:equals	(Ljava/lang/Object;)Z
    //   799: ifne +17 -> 816
    //   802: aload_0
    //   803: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   806: getfield 2682	android/view/View$AttachInfo:mVisibleInsets	Landroid/graphics/Rect;
    //   809: aload_0
    //   810: getfield 620	android/view/ViewRootImpl:mPendingVisibleInsets	Landroid/graphics/Rect;
    //   813: invokevirtual 2684	android/graphics/Rect:set	(Landroid/graphics/Rect;)V
    //   816: aload_0
    //   817: getfield 626	android/view/ViewRootImpl:mPendingOutsets	Landroid/graphics/Rect;
    //   820: aload_0
    //   821: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   824: getfield 2687	android/view/View$AttachInfo:mOutsets	Landroid/graphics/Rect;
    //   827: invokevirtual 1283	android/graphics/Rect:equals	(Ljava/lang/Object;)Z
    //   830: ifne +6 -> 836
    //   833: iconst_1
    //   834: istore 8
    //   836: aload_0
    //   837: getfield 2689	android/view/ViewRootImpl:mPendingAlwaysConsumeSystemBars	Z
    //   840: aload_0
    //   841: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   844: getfield 2692	android/view/View$AttachInfo:mAlwaysConsumeSystemBars	Z
    //   847: if_icmpeq +6 -> 853
    //   850: iconst_1
    //   851: istore 8
    //   853: aload_3
    //   854: getfield 2379	android/view/WindowManager$LayoutParams:width	I
    //   857: bipush -2
    //   859: if_icmpeq +18 -> 877
    //   862: aload_3
    //   863: getfield 2396	android/view/WindowManager$LayoutParams:height	I
    //   866: bipush -2
    //   868: if_icmpne +6 -> 874
    //   871: goto +6 -> 877
    //   874: goto +76 -> 950
    //   877: iconst_1
    //   878: istore_2
    //   879: aload_3
    //   880: invokestatic 2604	android/view/ViewRootImpl:shouldUseDisplaySize	(Landroid/view/WindowManager$LayoutParams;)Z
    //   883: ifeq +38 -> 921
    //   886: new 2606	android/graphics/Point
    //   889: dup
    //   890: invokespecial 2607	android/graphics/Point:<init>	()V
    //   893: astore 15
    //   895: aload_0
    //   896: getfield 761	android/view/ViewRootImpl:mDisplay	Landroid/view/Display;
    //   899: aload 15
    //   901: invokevirtual 2611	android/view/Display:getRealSize	(Landroid/graphics/Point;)V
    //   904: aload 15
    //   906: getfield 2613	android/graphics/Point:x	I
    //   909: istore 6
    //   911: aload 15
    //   913: getfield 2615	android/graphics/Point:y	I
    //   916: istore 7
    //   918: goto +32 -> 950
    //   921: aload 14
    //   923: invokevirtual 1667	android/content/res/Resources:getConfiguration	()Landroid/content/res/Configuration;
    //   926: astore 15
    //   928: aload_0
    //   929: aload 15
    //   931: getfield 2695	android/content/res/Configuration:screenWidthDp	I
    //   934: invokespecial 2697	android/view/ViewRootImpl:dipToPx	(I)I
    //   937: istore 6
    //   939: aload_0
    //   940: aload 15
    //   942: getfield 2700	android/content/res/Configuration:screenHeightDp	I
    //   945: invokespecial 2697	android/view/ViewRootImpl:dipToPx	(I)I
    //   948: istore 7
    //   950: iload_2
    //   951: aload_0
    //   952: aload_1
    //   953: aload_3
    //   954: aload 14
    //   956: iload 6
    //   958: iload 7
    //   960: invokespecial 2577	android/view/ViewRootImpl:measureHierarchy	(Landroid/view/View;Landroid/view/WindowManager$LayoutParams;Landroid/content/res/Resources;II)Z
    //   963: ior
    //   964: istore_2
    //   965: iload 6
    //   967: istore 16
    //   969: goto +10 -> 979
    //   972: iconst_0
    //   973: istore 8
    //   975: iload 6
    //   977: istore 16
    //   979: aload_0
    //   980: invokespecial 2702	android/view/ViewRootImpl:collectViewAttributes	()Z
    //   983: ifeq +6 -> 989
    //   986: aload_3
    //   987: astore 9
    //   989: aload_0
    //   990: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   993: getfield 2705	android/view/View$AttachInfo:mForceReportNewAttributes	Z
    //   996: ifeq +14 -> 1010
    //   999: aload_0
    //   1000: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   1003: iconst_0
    //   1004: putfield 2705	android/view/View$AttachInfo:mForceReportNewAttributes	Z
    //   1007: aload_3
    //   1008: astore 9
    //   1010: aload_0
    //   1011: getfield 825	android/view/ViewRootImpl:mFirst	Z
    //   1014: ifne +13 -> 1027
    //   1017: aload_0
    //   1018: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   1021: getfield 2708	android/view/View$AttachInfo:mViewVisibilityChanged	Z
    //   1024: ifeq +128 -> 1152
    //   1027: aload_0
    //   1028: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   1031: astore 14
    //   1033: aload 14
    //   1035: iconst_0
    //   1036: putfield 2708	android/view/View$AttachInfo:mViewVisibilityChanged	Z
    //   1039: aload_0
    //   1040: getfield 2710	android/view/ViewRootImpl:mSoftInputMode	I
    //   1043: sipush 240
    //   1046: iand
    //   1047: istore 6
    //   1049: iload 6
    //   1051: ifne +101 -> 1152
    //   1054: aload 14
    //   1056: getfield 2713	android/view/View$AttachInfo:mScrollContainers	Ljava/util/ArrayList;
    //   1059: invokevirtual 1286	java/util/ArrayList:size	()I
    //   1062: istore 19
    //   1064: iconst_0
    //   1065: istore 17
    //   1067: iload 17
    //   1069: iload 19
    //   1071: if_icmpge +34 -> 1105
    //   1074: aload_0
    //   1075: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   1078: getfield 2713	android/view/View$AttachInfo:mScrollContainers	Ljava/util/ArrayList;
    //   1081: iload 17
    //   1083: invokevirtual 1289	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   1086: checkcast 1152	android/view/View
    //   1089: invokevirtual 2716	android/view/View:isShown	()Z
    //   1092: ifeq +7 -> 1099
    //   1095: bipush 16
    //   1097: istore 6
    //   1099: iinc 17 1
    //   1102: goto -35 -> 1067
    //   1105: iload 6
    //   1107: istore 17
    //   1109: iload 6
    //   1111: ifne +7 -> 1118
    //   1114: bipush 32
    //   1116: istore 17
    //   1118: aload_3
    //   1119: getfield 2267	android/view/WindowManager$LayoutParams:softInputMode	I
    //   1122: sipush 240
    //   1125: iand
    //   1126: iload 17
    //   1128: if_icmpeq +24 -> 1152
    //   1131: aload_3
    //   1132: aload_3
    //   1133: getfield 2267	android/view/WindowManager$LayoutParams:softInputMode	I
    //   1136: sipush 65295
    //   1139: iand
    //   1140: iload 17
    //   1142: ior
    //   1143: putfield 2267	android/view/WindowManager$LayoutParams:softInputMode	I
    //   1146: aload_3
    //   1147: astore 9
    //   1149: goto +3 -> 1152
    //   1152: aload 9
    //   1154: ifnull +66 -> 1220
    //   1157: aload_1
    //   1158: getfield 1582	android/view/View:mPrivateFlags	I
    //   1161: sipush 512
    //   1164: iand
    //   1165: ifeq +21 -> 1186
    //   1168: aload 9
    //   1170: getfield 1663	android/view/WindowManager$LayoutParams:format	I
    //   1173: invokestatic 2721	android/graphics/PixelFormat:formatHasAlpha	(I)Z
    //   1176: ifne +10 -> 1186
    //   1179: aload 9
    //   1181: bipush -3
    //   1183: putfield 1663	android/view/WindowManager$LayoutParams:format	I
    //   1186: aload_0
    //   1187: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   1190: astore 14
    //   1192: aload 9
    //   1194: getfield 1114	android/view/WindowManager$LayoutParams:flags	I
    //   1197: ldc_w 2722
    //   1200: iand
    //   1201: ifeq +9 -> 1210
    //   1204: iconst_1
    //   1205: istore 12
    //   1207: goto +6 -> 1213
    //   1210: iconst_0
    //   1211: istore 12
    //   1213: aload 14
    //   1215: iload 12
    //   1217: putfield 2725	android/view/View$AttachInfo:mOverscanRequested	Z
    //   1220: aload_0
    //   1221: getfield 2727	android/view/ViewRootImpl:mApplyInsetsRequested	Z
    //   1224: ifeq +60 -> 1284
    //   1227: aload_0
    //   1228: iconst_0
    //   1229: putfield 2727	android/view/ViewRootImpl:mApplyInsetsRequested	Z
    //   1232: aload_0
    //   1233: aload_0
    //   1234: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   1237: getfield 2725	android/view/View$AttachInfo:mOverscanRequested	Z
    //   1240: putfield 2729	android/view/ViewRootImpl:mLastOverscanRequested	Z
    //   1243: aload_0
    //   1244: aload_1
    //   1245: invokevirtual 2647	android/view/ViewRootImpl:dispatchApplyInsets	(Landroid/view/View;)V
    //   1248: aload_0
    //   1249: getfield 1602	android/view/ViewRootImpl:mLayoutRequested	Z
    //   1252: ifeq +29 -> 1281
    //   1255: iload_2
    //   1256: aload_0
    //   1257: aload_1
    //   1258: aload_3
    //   1259: aload_0
    //   1260: getfield 1150	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   1263: invokevirtual 1966	android/view/View:getContext	()Landroid/content/Context;
    //   1266: invokevirtual 865	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   1269: iload 16
    //   1271: iload 7
    //   1273: invokespecial 2577	android/view/ViewRootImpl:measureHierarchy	(Landroid/view/View;Landroid/view/WindowManager$LayoutParams;Landroid/content/res/Resources;II)Z
    //   1276: ior
    //   1277: istore_2
    //   1278: goto +6 -> 1284
    //   1281: goto +3 -> 1284
    //   1284: aload 9
    //   1286: astore 14
    //   1288: iload 18
    //   1290: ifeq +8 -> 1298
    //   1293: aload_0
    //   1294: iconst_0
    //   1295: putfield 1602	android/view/ViewRootImpl:mLayoutRequested	Z
    //   1298: iload 18
    //   1300: ifeq +97 -> 1397
    //   1303: iload_2
    //   1304: ifeq +93 -> 1397
    //   1307: aload_0
    //   1308: getfield 790	android/view/ViewRootImpl:mWidth	I
    //   1311: aload_1
    //   1312: invokevirtual 2406	android/view/View:getMeasuredWidth	()I
    //   1315: if_icmpne +76 -> 1391
    //   1318: aload_0
    //   1319: getfield 792	android/view/ViewRootImpl:mHeight	I
    //   1322: aload_1
    //   1323: invokevirtual 2409	android/view/View:getMeasuredHeight	()I
    //   1326: if_icmpne +65 -> 1391
    //   1329: aload_3
    //   1330: getfield 2379	android/view/WindowManager$LayoutParams:width	I
    //   1333: bipush -2
    //   1335: if_icmpne +25 -> 1360
    //   1338: aload 13
    //   1340: invokevirtual 2617	android/graphics/Rect:width	()I
    //   1343: iload 16
    //   1345: if_icmpge +15 -> 1360
    //   1348: aload 13
    //   1350: invokevirtual 2617	android/graphics/Rect:width	()I
    //   1353: aload_0
    //   1354: getfield 790	android/view/ViewRootImpl:mWidth	I
    //   1357: if_icmpne +34 -> 1391
    //   1360: aload_3
    //   1361: getfield 2396	android/view/WindowManager$LayoutParams:height	I
    //   1364: bipush -2
    //   1366: if_icmpne +31 -> 1397
    //   1369: aload 13
    //   1371: invokevirtual 2619	android/graphics/Rect:height	()I
    //   1374: iload 7
    //   1376: if_icmpge +21 -> 1397
    //   1379: aload 13
    //   1381: invokevirtual 2619	android/graphics/Rect:height	()I
    //   1384: aload_0
    //   1385: getfield 792	android/view/ViewRootImpl:mHeight	I
    //   1388: if_icmpeq +9 -> 1397
    //   1391: iconst_1
    //   1392: istore 6
    //   1394: goto +6 -> 1400
    //   1397: iconst_0
    //   1398: istore 6
    //   1400: aload_0
    //   1401: getfield 1278	android/view/ViewRootImpl:mDragResizing	Z
    //   1404: ifeq +16 -> 1420
    //   1407: aload_0
    //   1408: getfield 2731	android/view/ViewRootImpl:mResizeMode	I
    //   1411: ifne +9 -> 1420
    //   1414: iconst_1
    //   1415: istore 7
    //   1417: goto +6 -> 1423
    //   1420: iconst_0
    //   1421: istore 7
    //   1423: aload_0
    //   1424: getfield 2733	android/view/ViewRootImpl:mActivityRelaunched	Z
    //   1427: istore 12
    //   1429: aload_0
    //   1430: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   1433: getfield 1383	android/view/View$AttachInfo:mTreeObserver	Landroid/view/ViewTreeObserver;
    //   1436: invokevirtual 2736	android/view/ViewTreeObserver:hasComputeInternalInsetsListeners	()Z
    //   1439: ifne +22 -> 1461
    //   1442: aload_0
    //   1443: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   1446: getfield 2739	android/view/View$AttachInfo:mHasNonEmptyGivenInternalInsets	Z
    //   1449: ifeq +6 -> 1455
    //   1452: goto +9 -> 1461
    //   1455: iconst_0
    //   1456: istore 20
    //   1458: goto +6 -> 1464
    //   1461: iconst_1
    //   1462: istore 20
    //   1464: iconst_0
    //   1465: istore 17
    //   1467: iconst_0
    //   1468: istore_2
    //   1469: aload_0
    //   1470: getfield 602	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   1473: invokevirtual 2742	android/view/Surface:getGenerationId	()I
    //   1476: istore 21
    //   1478: iload 4
    //   1480: ifne +9 -> 1489
    //   1483: iconst_1
    //   1484: istore 22
    //   1486: goto +6 -> 1492
    //   1489: iconst_0
    //   1490: istore 22
    //   1492: aload_0
    //   1493: getfield 2744	android/view/ViewRootImpl:mForceNextWindowRelayout	Z
    //   1496: istore 23
    //   1498: iconst_0
    //   1499: istore 16
    //   1501: aload_0
    //   1502: getfield 825	android/view/ViewRootImpl:mFirst	Z
    //   1505: ifne +65 -> 1570
    //   1508: iload 6
    //   1510: iload 7
    //   1512: ior
    //   1513: iload 12
    //   1515: ior
    //   1516: ifne +54 -> 1570
    //   1519: iload 8
    //   1521: ifne +49 -> 1570
    //   1524: iload 5
    //   1526: ifne +44 -> 1570
    //   1529: aload 14
    //   1531: ifnonnull +36 -> 1567
    //   1534: iconst_0
    //   1535: istore 12
    //   1537: aload_0
    //   1538: getfield 2744	android/view/ViewRootImpl:mForceNextWindowRelayout	Z
    //   1541: ifeq +6 -> 1547
    //   1544: goto +26 -> 1570
    //   1547: aload_0
    //   1548: aload 13
    //   1550: invokespecial 969	android/view/ViewRootImpl:maybeHandleWindowMove	(Landroid/graphics/Rect;)V
    //   1553: iload 17
    //   1555: istore 7
    //   1557: iload_2
    //   1558: istore 6
    //   1560: iload 16
    //   1562: istore 10
    //   1564: goto +3636 -> 5200
    //   1567: goto +3 -> 1570
    //   1570: aload 13
    //   1572: astore 9
    //   1574: aload_0
    //   1575: iconst_0
    //   1576: putfield 2744	android/view/ViewRootImpl:mForceNextWindowRelayout	Z
    //   1579: iload 22
    //   1581: ifeq +36 -> 1617
    //   1584: iload 20
    //   1586: ifeq +21 -> 1607
    //   1589: aload_0
    //   1590: getfield 825	android/view/ViewRootImpl:mFirst	Z
    //   1593: ifne +8 -> 1601
    //   1596: iload 5
    //   1598: ifeq +9 -> 1607
    //   1601: iconst_1
    //   1602: istore 12
    //   1604: goto +6 -> 1610
    //   1607: iconst_0
    //   1608: istore 12
    //   1610: iload 12
    //   1612: istore 11
    //   1614: goto +6 -> 1620
    //   1617: iconst_0
    //   1618: istore 11
    //   1620: aload_0
    //   1621: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   1624: astore 13
    //   1626: aload 13
    //   1628: ifnull +19 -> 1647
    //   1631: aload 13
    //   1633: getfield 2748	com/android/internal/view/BaseSurfaceHolder:mSurfaceLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   1636: invokevirtual 2753	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   1639: aload_0
    //   1640: iconst_1
    //   1641: putfield 2755	android/view/ViewRootImpl:mDrawingAllowed	Z
    //   1644: goto +3 -> 1647
    //   1647: iconst_0
    //   1648: istore 24
    //   1650: iconst_0
    //   1651: istore 25
    //   1653: iconst_0
    //   1654: istore 8
    //   1656: iconst_0
    //   1657: istore 26
    //   1659: iconst_0
    //   1660: istore 16
    //   1662: iconst_0
    //   1663: istore 17
    //   1665: iconst_0
    //   1666: istore 27
    //   1668: aload_0
    //   1669: getfield 602	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   1672: invokevirtual 1366	android/view/Surface:isValid	()Z
    //   1675: istore 28
    //   1677: aload_0
    //   1678: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   1681: getfield 1222	android/view/View$AttachInfo:mThreadedRenderer	Landroid/view/ThreadedRenderer;
    //   1684: astore 13
    //   1686: aload 13
    //   1688: ifnull +129 -> 1817
    //   1691: aload_0
    //   1692: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   1695: getfield 1222	android/view/View$AttachInfo:mThreadedRenderer	Landroid/view/ThreadedRenderer;
    //   1698: invokevirtual 2758	android/view/ThreadedRenderer:pause	()Z
    //   1701: ifeq +66 -> 1767
    //   1704: aload_0
    //   1705: getfield 794	android/view/ViewRootImpl:mDirty	Landroid/graphics/Rect;
    //   1708: astore 13
    //   1710: iconst_0
    //   1711: istore 12
    //   1713: aload_0
    //   1714: getfield 790	android/view/ViewRootImpl:mWidth	I
    //   1717: istore_2
    //   1718: iconst_0
    //   1719: istore 7
    //   1721: aload_0
    //   1722: getfield 792	android/view/ViewRootImpl:mHeight	I
    //   1725: istore 6
    //   1727: aload 13
    //   1729: iconst_0
    //   1730: iconst_0
    //   1731: iload_2
    //   1732: iload 6
    //   1734: invokevirtual 1425	android/graphics/Rect:set	(IIII)V
    //   1737: goto +30 -> 1767
    //   1740: astore 13
    //   1742: iconst_0
    //   1743: istore 6
    //   1745: iload 10
    //   1747: istore 16
    //   1749: goto +2750 -> 4499
    //   1752: astore 13
    //   1754: iconst_0
    //   1755: istore 7
    //   1757: iconst_0
    //   1758: istore 6
    //   1760: iload 10
    //   1762: istore 16
    //   1764: goto +2735 -> 4499
    //   1767: aload_0
    //   1768: getfield 897	android/view/ViewRootImpl:mChoreographer	Landroid/view/Choreographer;
    //   1771: getfield 2762	android/view/Choreographer:mFrameInfo	Landroid/graphics/FrameInfo;
    //   1774: lconst_1
    //   1775: invokevirtual 2767	android/graphics/FrameInfo:addFlags	(J)V
    //   1778: goto +39 -> 1817
    //   1781: iconst_0
    //   1782: istore 7
    //   1784: iconst_0
    //   1785: istore 12
    //   1787: astore 13
    //   1789: iconst_0
    //   1790: istore 6
    //   1792: iload 10
    //   1794: istore 16
    //   1796: goto +2703 -> 4499
    //   1799: astore 13
    //   1801: iconst_0
    //   1802: istore 12
    //   1804: iconst_0
    //   1805: istore 7
    //   1807: iconst_0
    //   1808: istore 6
    //   1810: iload 10
    //   1812: istore 16
    //   1814: goto +2685 -> 4499
    //   1817: iconst_0
    //   1818: istore_2
    //   1819: iconst_0
    //   1820: istore 6
    //   1822: iconst_0
    //   1823: istore 29
    //   1825: iconst_0
    //   1826: istore 30
    //   1828: iconst_0
    //   1829: istore 31
    //   1831: iconst_0
    //   1832: istore 12
    //   1834: iconst_0
    //   1835: istore 32
    //   1837: aload_0
    //   1838: aload 14
    //   1840: iload 4
    //   1842: iload 11
    //   1844: invokespecial 2771	android/view/ViewRootImpl:relayoutWindow	(Landroid/view/WindowManager$LayoutParams;IZ)I
    //   1847: istore 7
    //   1849: iload 10
    //   1851: istore 33
    //   1853: iload 25
    //   1855: istore 34
    //   1857: iload 16
    //   1859: istore 6
    //   1861: iload 31
    //   1863: istore 35
    //   1865: iload_2
    //   1866: istore 19
    //   1868: aload_0
    //   1869: getfield 658	android/view/ViewRootImpl:mNeedUpdateBlurCrop	Z
    //   1872: istore 12
    //   1874: iload 12
    //   1876: ifeq +100 -> 1976
    //   1879: iload 10
    //   1881: istore 33
    //   1883: iload 26
    //   1885: istore 19
    //   1887: iload 27
    //   1889: istore 6
    //   1891: iload 32
    //   1893: istore 35
    //   1895: iload 29
    //   1897: istore 34
    //   1899: aload_1
    //   1900: invokevirtual 2277	android/view/View:getLayoutParams	()Landroid/view/ViewGroup$LayoutParams;
    //   1903: instanceof 552
    //   1906: ifeq +70 -> 1976
    //   1909: iload 10
    //   1911: istore 33
    //   1913: iload 26
    //   1915: istore 19
    //   1917: iload 27
    //   1919: istore 6
    //   1921: iload 32
    //   1923: istore 35
    //   1925: iload 29
    //   1927: istore 34
    //   1929: aload_0
    //   1930: aload_1
    //   1931: invokevirtual 2277	android/view/View:getLayoutParams	()Landroid/view/ViewGroup$LayoutParams;
    //   1934: checkcast 552	android/view/WindowManager$LayoutParams
    //   1937: invokevirtual 2774	android/view/ViewRootImpl:updateBlurCrop	(Landroid/view/WindowManager$LayoutParams;)V
    //   1940: goto +36 -> 1976
    //   1943: astore 13
    //   1945: iload 7
    //   1947: istore 10
    //   1949: iload 33
    //   1951: istore 16
    //   1953: iload 19
    //   1955: istore 8
    //   1957: iload 6
    //   1959: istore 17
    //   1961: iload 35
    //   1963: istore 12
    //   1965: iload 34
    //   1967: istore 7
    //   1969: iload 10
    //   1971: istore 6
    //   1973: goto +2526 -> 4499
    //   1976: iload 10
    //   1978: istore 33
    //   1980: iload 25
    //   1982: istore 34
    //   1984: iload 16
    //   1986: istore 6
    //   1988: iload 31
    //   1990: istore 35
    //   1992: iload_2
    //   1993: istore 19
    //   1995: aload_0
    //   1996: getfield 670	android/view/ViewRootImpl:mPendingMergedConfiguration	Landroid/util/MergedConfiguration;
    //   1999: aload_0
    //   2000: getfield 668	android/view/ViewRootImpl:mLastReportedMergedConfiguration	Landroid/util/MergedConfiguration;
    //   2003: invokevirtual 2775	android/util/MergedConfiguration:equals	(Ljava/lang/Object;)Z
    //   2006: istore 12
    //   2008: iload 24
    //   2010: istore 8
    //   2012: iload 12
    //   2014: ifne +97 -> 2111
    //   2017: iload 10
    //   2019: istore 33
    //   2021: iload 26
    //   2023: istore 19
    //   2025: iload 27
    //   2027: istore 6
    //   2029: iload 32
    //   2031: istore 35
    //   2033: iload 29
    //   2035: istore 34
    //   2037: aload_0
    //   2038: getfield 670	android/view/ViewRootImpl:mPendingMergedConfiguration	Landroid/util/MergedConfiguration;
    //   2041: astore 13
    //   2043: iload 10
    //   2045: istore 33
    //   2047: iload 26
    //   2049: istore 19
    //   2051: iload 27
    //   2053: istore 6
    //   2055: iload 32
    //   2057: istore 35
    //   2059: iload 29
    //   2061: istore 34
    //   2063: aload_0
    //   2064: getfield 825	android/view/ViewRootImpl:mFirst	Z
    //   2067: ifne +9 -> 2076
    //   2070: iconst_1
    //   2071: istore 12
    //   2073: goto +6 -> 2079
    //   2076: iconst_0
    //   2077: istore 12
    //   2079: iload 10
    //   2081: istore 33
    //   2083: iload 26
    //   2085: istore 19
    //   2087: iload 27
    //   2089: istore 6
    //   2091: iload 32
    //   2093: istore 35
    //   2095: iload 29
    //   2097: istore 34
    //   2099: aload_0
    //   2100: aload 13
    //   2102: iload 12
    //   2104: iconst_m1
    //   2105: invokespecial 1056	android/view/ViewRootImpl:performConfigurationChange	(Landroid/util/MergedConfiguration;ZI)V
    //   2108: iconst_1
    //   2109: istore 8
    //   2111: iload 10
    //   2113: istore 33
    //   2115: iload 8
    //   2117: istore 34
    //   2119: iload 16
    //   2121: istore 6
    //   2123: iload 31
    //   2125: istore 35
    //   2127: iload_2
    //   2128: istore 19
    //   2130: aload_0
    //   2131: getfield 618	android/view/ViewRootImpl:mPendingOverscanInsets	Landroid/graphics/Rect;
    //   2134: aload_0
    //   2135: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2138: getfield 2669	android/view/View$AttachInfo:mOverscanInsets	Landroid/graphics/Rect;
    //   2141: invokevirtual 1283	android/graphics/Rect:equals	(Ljava/lang/Object;)Z
    //   2144: ifne +9 -> 2153
    //   2147: iconst_1
    //   2148: istore 26
    //   2150: goto +6 -> 2156
    //   2153: iconst_0
    //   2154: istore 26
    //   2156: iload 10
    //   2158: istore 33
    //   2160: iload 8
    //   2162: istore 34
    //   2164: iload 16
    //   2166: istore 6
    //   2168: iload 31
    //   2170: istore 35
    //   2172: iload_2
    //   2173: istore 19
    //   2175: aload_0
    //   2176: getfield 624	android/view/ViewRootImpl:mPendingContentInsets	Landroid/graphics/Rect;
    //   2179: aload_0
    //   2180: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2183: getfield 2672	android/view/View$AttachInfo:mContentInsets	Landroid/graphics/Rect;
    //   2186: invokevirtual 1283	android/graphics/Rect:equals	(Ljava/lang/Object;)Z
    //   2189: ifne +9 -> 2198
    //   2192: iconst_1
    //   2193: istore 16
    //   2195: goto +6 -> 2201
    //   2198: iconst_0
    //   2199: istore 16
    //   2201: iload 10
    //   2203: istore 33
    //   2205: iload 8
    //   2207: istore 34
    //   2209: iload 16
    //   2211: istore 6
    //   2213: iload 31
    //   2215: istore 35
    //   2217: iload_2
    //   2218: istore 19
    //   2220: aload_0
    //   2221: getfield 620	android/view/ViewRootImpl:mPendingVisibleInsets	Landroid/graphics/Rect;
    //   2224: aload_0
    //   2225: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2228: getfield 2682	android/view/View$AttachInfo:mVisibleInsets	Landroid/graphics/Rect;
    //   2231: invokevirtual 1283	android/graphics/Rect:equals	(Ljava/lang/Object;)Z
    //   2234: ifne +9 -> 2243
    //   2237: iconst_1
    //   2238: istore 27
    //   2240: goto +6 -> 2246
    //   2243: iconst_0
    //   2244: istore 27
    //   2246: iload 10
    //   2248: istore 33
    //   2250: iload 8
    //   2252: istore 34
    //   2254: iload 16
    //   2256: istore 6
    //   2258: iload 31
    //   2260: istore 35
    //   2262: iload_2
    //   2263: istore 19
    //   2265: aload_0
    //   2266: getfield 622	android/view/ViewRootImpl:mPendingStableInsets	Landroid/graphics/Rect;
    //   2269: aload_0
    //   2270: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2273: getfield 2675	android/view/View$AttachInfo:mStableInsets	Landroid/graphics/Rect;
    //   2276: invokevirtual 1283	android/graphics/Rect:equals	(Ljava/lang/Object;)Z
    //   2279: ifne +9 -> 2288
    //   2282: iconst_1
    //   2283: istore 24
    //   2285: goto +6 -> 2291
    //   2288: iconst_0
    //   2289: istore 24
    //   2291: iload 10
    //   2293: istore 33
    //   2295: iload 8
    //   2297: istore 34
    //   2299: iload 16
    //   2301: istore 6
    //   2303: iload 31
    //   2305: istore 35
    //   2307: iload_2
    //   2308: istore 19
    //   2310: aload_0
    //   2311: getfield 640	android/view/ViewRootImpl:mPendingDisplayCutout	Landroid/view/DisplayCutout$ParcelableWrapper;
    //   2314: aload_0
    //   2315: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2318: getfield 2678	android/view/View$AttachInfo:mDisplayCutout	Landroid/view/DisplayCutout$ParcelableWrapper;
    //   2321: invokevirtual 2679	android/view/DisplayCutout$ParcelableWrapper:equals	(Ljava/lang/Object;)Z
    //   2324: ifne +9 -> 2333
    //   2327: iconst_1
    //   2328: istore 25
    //   2330: goto +6 -> 2336
    //   2333: iconst_0
    //   2334: istore 25
    //   2336: iload 10
    //   2338: istore 33
    //   2340: iload 8
    //   2342: istore 34
    //   2344: iload 16
    //   2346: istore 6
    //   2348: iload 31
    //   2350: istore 35
    //   2352: iload_2
    //   2353: istore 19
    //   2355: aload_0
    //   2356: getfield 626	android/view/ViewRootImpl:mPendingOutsets	Landroid/graphics/Rect;
    //   2359: aload_0
    //   2360: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2363: getfield 2687	android/view/View$AttachInfo:mOutsets	Landroid/graphics/Rect;
    //   2366: invokevirtual 1283	android/graphics/Rect:equals	(Ljava/lang/Object;)Z
    //   2369: istore 12
    //   2371: iload 7
    //   2373: bipush 32
    //   2375: iand
    //   2376: ifeq +8 -> 2384
    //   2379: iconst_1
    //   2380: istore_2
    //   2381: goto +5 -> 2386
    //   2384: iconst_0
    //   2385: istore_2
    //   2386: iload 10
    //   2388: iload_2
    //   2389: ior
    //   2390: istore 17
    //   2392: iload 17
    //   2394: istore 33
    //   2396: iload 8
    //   2398: istore 34
    //   2400: iload 16
    //   2402: istore 6
    //   2404: iload 31
    //   2406: istore 35
    //   2408: iload_2
    //   2409: istore 19
    //   2411: aload_0
    //   2412: getfield 2689	android/view/ViewRootImpl:mPendingAlwaysConsumeSystemBars	Z
    //   2415: aload_0
    //   2416: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2419: getfield 2692	android/view/View$AttachInfo:mAlwaysConsumeSystemBars	Z
    //   2422: if_icmpeq +9 -> 2431
    //   2425: iconst_1
    //   2426: istore 29
    //   2428: goto +6 -> 2434
    //   2431: iconst_0
    //   2432: istore 29
    //   2434: iload 17
    //   2436: istore 33
    //   2438: iload 8
    //   2440: istore 34
    //   2442: iload 16
    //   2444: istore 6
    //   2446: iload 31
    //   2448: istore 35
    //   2450: iload_2
    //   2451: istore 19
    //   2453: aload_0
    //   2454: aload_3
    //   2455: invokevirtual 1673	android/view/WindowManager$LayoutParams:getColorMode	()I
    //   2458: invokespecial 2777	android/view/ViewRootImpl:hasColorModeChanged	(I)Z
    //   2461: istore 36
    //   2463: iload 16
    //   2465: ifeq +36 -> 2501
    //   2468: iload 17
    //   2470: istore 33
    //   2472: iload 8
    //   2474: istore 19
    //   2476: iload 16
    //   2478: istore 6
    //   2480: iload 32
    //   2482: istore 35
    //   2484: iload_2
    //   2485: istore 34
    //   2487: aload_0
    //   2488: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2491: getfield 2672	android/view/View$AttachInfo:mContentInsets	Landroid/graphics/Rect;
    //   2494: aload_0
    //   2495: getfield 624	android/view/ViewRootImpl:mPendingContentInsets	Landroid/graphics/Rect;
    //   2498: invokevirtual 2684	android/graphics/Rect:set	(Landroid/graphics/Rect;)V
    //   2501: iload 16
    //   2503: istore 6
    //   2505: iload 26
    //   2507: ifeq +39 -> 2546
    //   2510: iload 17
    //   2512: istore 33
    //   2514: iload 8
    //   2516: istore 19
    //   2518: iload 16
    //   2520: istore 6
    //   2522: iload 32
    //   2524: istore 35
    //   2526: iload_2
    //   2527: istore 34
    //   2529: aload_0
    //   2530: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2533: getfield 2669	android/view/View$AttachInfo:mOverscanInsets	Landroid/graphics/Rect;
    //   2536: aload_0
    //   2537: getfield 618	android/view/ViewRootImpl:mPendingOverscanInsets	Landroid/graphics/Rect;
    //   2540: invokevirtual 2684	android/graphics/Rect:set	(Landroid/graphics/Rect;)V
    //   2543: iconst_1
    //   2544: istore 6
    //   2546: iload 6
    //   2548: istore 10
    //   2550: iload 24
    //   2552: ifeq +35 -> 2587
    //   2555: iload 17
    //   2557: istore 33
    //   2559: iload 8
    //   2561: istore 19
    //   2563: iload 32
    //   2565: istore 35
    //   2567: iload_2
    //   2568: istore 34
    //   2570: aload_0
    //   2571: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2574: getfield 2675	android/view/View$AttachInfo:mStableInsets	Landroid/graphics/Rect;
    //   2577: aload_0
    //   2578: getfield 622	android/view/ViewRootImpl:mPendingStableInsets	Landroid/graphics/Rect;
    //   2581: invokevirtual 2684	android/graphics/Rect:set	(Landroid/graphics/Rect;)V
    //   2584: iconst_1
    //   2585: istore 10
    //   2587: iload 10
    //   2589: istore 6
    //   2591: iload 25
    //   2593: ifeq +39 -> 2632
    //   2596: iload 17
    //   2598: istore 33
    //   2600: iload 8
    //   2602: istore 19
    //   2604: iload 10
    //   2606: istore 6
    //   2608: iload 32
    //   2610: istore 35
    //   2612: iload_2
    //   2613: istore 34
    //   2615: aload_0
    //   2616: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2619: getfield 2678	android/view/View$AttachInfo:mDisplayCutout	Landroid/view/DisplayCutout$ParcelableWrapper;
    //   2622: aload_0
    //   2623: getfield 640	android/view/ViewRootImpl:mPendingDisplayCutout	Landroid/view/DisplayCutout$ParcelableWrapper;
    //   2626: invokevirtual 2780	android/view/DisplayCutout$ParcelableWrapper:set	(Landroid/view/DisplayCutout$ParcelableWrapper;)V
    //   2629: iconst_1
    //   2630: istore 6
    //   2632: iload 6
    //   2634: istore 16
    //   2636: iload 29
    //   2638: ifeq +32 -> 2670
    //   2641: iload 17
    //   2643: istore 33
    //   2645: iload 8
    //   2647: istore 19
    //   2649: iload 32
    //   2651: istore 35
    //   2653: iload_2
    //   2654: istore 34
    //   2656: aload_0
    //   2657: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2660: aload_0
    //   2661: getfield 2689	android/view/ViewRootImpl:mPendingAlwaysConsumeSystemBars	Z
    //   2664: putfield 2692	android/view/View$AttachInfo:mAlwaysConsumeSystemBars	Z
    //   2667: iconst_1
    //   2668: istore 16
    //   2670: iload 16
    //   2672: ifne +133 -> 2805
    //   2675: iload 17
    //   2677: istore 33
    //   2679: iload 8
    //   2681: istore 19
    //   2683: iload 16
    //   2685: istore 6
    //   2687: iload 32
    //   2689: istore 35
    //   2691: iload_2
    //   2692: istore 34
    //   2694: aload_0
    //   2695: getfield 2630	android/view/ViewRootImpl:mLastSystemUiVisibility	I
    //   2698: aload_0
    //   2699: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2702: getfield 1145	android/view/View$AttachInfo:mSystemUiVisibility	I
    //   2705: if_icmpne +100 -> 2805
    //   2708: iload 17
    //   2710: istore 33
    //   2712: iload 8
    //   2714: istore 19
    //   2716: iload 16
    //   2718: istore 6
    //   2720: iload 32
    //   2722: istore 35
    //   2724: iload_2
    //   2725: istore 34
    //   2727: aload_0
    //   2728: getfield 2727	android/view/ViewRootImpl:mApplyInsetsRequested	Z
    //   2731: ifne +74 -> 2805
    //   2734: iload 17
    //   2736: istore 33
    //   2738: iload 8
    //   2740: istore 19
    //   2742: iload 16
    //   2744: istore 6
    //   2746: iload 32
    //   2748: istore 35
    //   2750: iload_2
    //   2751: istore 34
    //   2753: aload_0
    //   2754: getfield 2729	android/view/ViewRootImpl:mLastOverscanRequested	Z
    //   2757: istore 37
    //   2759: iload 17
    //   2761: istore 33
    //   2763: iload 8
    //   2765: istore 19
    //   2767: iload 16
    //   2769: istore 6
    //   2771: iload 32
    //   2773: istore 35
    //   2775: iload_2
    //   2776: istore 34
    //   2778: aload_0
    //   2779: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2782: getfield 2725	android/view/View$AttachInfo:mOverscanRequested	Z
    //   2785: istore 38
    //   2787: iload 37
    //   2789: iload 38
    //   2791: if_icmpne +14 -> 2805
    //   2794: iload 16
    //   2796: istore 10
    //   2798: iload 12
    //   2800: iconst_1
    //   2801: ixor
    //   2802: ifeq +147 -> 2949
    //   2805: iload 17
    //   2807: istore 33
    //   2809: iload 8
    //   2811: istore 34
    //   2813: iload 16
    //   2815: istore 6
    //   2817: iload 31
    //   2819: istore 35
    //   2821: iload_2
    //   2822: istore 19
    //   2824: aload_0
    //   2825: aload_0
    //   2826: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2829: getfield 1145	android/view/View$AttachInfo:mSystemUiVisibility	I
    //   2832: putfield 2630	android/view/ViewRootImpl:mLastSystemUiVisibility	I
    //   2835: iload 17
    //   2837: istore 33
    //   2839: iload 8
    //   2841: istore 34
    //   2843: iload 16
    //   2845: istore 6
    //   2847: iload 31
    //   2849: istore 35
    //   2851: iload_2
    //   2852: istore 19
    //   2854: aload_0
    //   2855: aload_0
    //   2856: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2859: getfield 2725	android/view/View$AttachInfo:mOverscanRequested	Z
    //   2862: putfield 2729	android/view/ViewRootImpl:mLastOverscanRequested	Z
    //   2865: iload 17
    //   2867: istore 33
    //   2869: iload 8
    //   2871: istore 34
    //   2873: iload 16
    //   2875: istore 6
    //   2877: iload 31
    //   2879: istore 35
    //   2881: iload_2
    //   2882: istore 19
    //   2884: aload_0
    //   2885: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2888: getfield 2687	android/view/View$AttachInfo:mOutsets	Landroid/graphics/Rect;
    //   2891: aload_0
    //   2892: getfield 626	android/view/ViewRootImpl:mPendingOutsets	Landroid/graphics/Rect;
    //   2895: invokevirtual 2684	android/graphics/Rect:set	(Landroid/graphics/Rect;)V
    //   2898: iload 17
    //   2900: istore 33
    //   2902: iload 8
    //   2904: istore 34
    //   2906: iload 16
    //   2908: istore 6
    //   2910: iload 31
    //   2912: istore 35
    //   2914: iload_2
    //   2915: istore 19
    //   2917: aload_0
    //   2918: iconst_0
    //   2919: putfield 2727	android/view/ViewRootImpl:mApplyInsetsRequested	Z
    //   2922: iload 17
    //   2924: istore 33
    //   2926: iload 8
    //   2928: istore 34
    //   2930: iload 16
    //   2932: istore 6
    //   2934: iload 31
    //   2936: istore 35
    //   2938: iload_2
    //   2939: istore 19
    //   2941: aload_0
    //   2942: aload_1
    //   2943: invokevirtual 2647	android/view/ViewRootImpl:dispatchApplyInsets	(Landroid/view/View;)V
    //   2946: iconst_1
    //   2947: istore 10
    //   2949: iload 27
    //   2951: ifeq +36 -> 2987
    //   2954: iload 17
    //   2956: istore 33
    //   2958: iload 8
    //   2960: istore 19
    //   2962: iload 10
    //   2964: istore 6
    //   2966: iload 32
    //   2968: istore 35
    //   2970: iload_2
    //   2971: istore 34
    //   2973: aload_0
    //   2974: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   2977: getfield 2682	android/view/View$AttachInfo:mVisibleInsets	Landroid/graphics/Rect;
    //   2980: aload_0
    //   2981: getfield 620	android/view/ViewRootImpl:mPendingVisibleInsets	Landroid/graphics/Rect;
    //   2984: invokevirtual 2684	android/graphics/Rect:set	(Landroid/graphics/Rect;)V
    //   2987: iload 36
    //   2989: ifeq +122 -> 3111
    //   2992: iload 17
    //   2994: istore 33
    //   2996: iload 8
    //   2998: istore 19
    //   3000: iload 10
    //   3002: istore 6
    //   3004: iload 32
    //   3006: istore 35
    //   3008: iload_2
    //   3009: istore 34
    //   3011: aload_0
    //   3012: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   3015: getfield 1222	android/view/View$AttachInfo:mThreadedRenderer	Landroid/view/ThreadedRenderer;
    //   3018: ifnull +93 -> 3111
    //   3021: iload 17
    //   3023: istore 33
    //   3025: iload 8
    //   3027: istore 19
    //   3029: iload 10
    //   3031: istore 6
    //   3033: iload 32
    //   3035: istore 35
    //   3037: iload_2
    //   3038: istore 34
    //   3040: aload_0
    //   3041: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   3044: getfield 1222	android/view/View$AttachInfo:mThreadedRenderer	Landroid/view/ThreadedRenderer;
    //   3047: astore 13
    //   3049: iload 17
    //   3051: istore 33
    //   3053: iload 8
    //   3055: istore 19
    //   3057: iload 10
    //   3059: istore 6
    //   3061: iload 32
    //   3063: istore 35
    //   3065: iload_2
    //   3066: istore 34
    //   3068: aload_3
    //   3069: invokevirtual 1673	android/view/WindowManager$LayoutParams:getColorMode	()I
    //   3072: iconst_1
    //   3073: if_icmpne +9 -> 3082
    //   3076: iconst_1
    //   3077: istore 12
    //   3079: goto +6 -> 3085
    //   3082: iconst_0
    //   3083: istore 12
    //   3085: iload 17
    //   3087: istore 33
    //   3089: iload 8
    //   3091: istore 19
    //   3093: iload 10
    //   3095: istore 6
    //   3097: iload 32
    //   3099: istore 35
    //   3101: iload_2
    //   3102: istore 34
    //   3104: aload 13
    //   3106: iload 12
    //   3108: invokevirtual 1687	android/view/ThreadedRenderer:setWideGamut	(Z)V
    //   3111: iload 28
    //   3113: ifne +266 -> 3379
    //   3116: iload 17
    //   3118: istore 33
    //   3120: iload 8
    //   3122: istore 19
    //   3124: iload 10
    //   3126: istore 6
    //   3128: iload 32
    //   3130: istore 35
    //   3132: iload_2
    //   3133: istore 34
    //   3135: iload 30
    //   3137: istore 12
    //   3139: aload_0
    //   3140: getfield 602	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   3143: invokevirtual 1366	android/view/Surface:isValid	()Z
    //   3146: ifeq +795 -> 3941
    //   3149: iload 17
    //   3151: istore 33
    //   3153: iload 8
    //   3155: istore 19
    //   3157: iload 10
    //   3159: istore 6
    //   3161: iload 32
    //   3163: istore 35
    //   3165: iload_2
    //   3166: istore 34
    //   3168: aload_0
    //   3169: iconst_1
    //   3170: putfield 1528	android/view/ViewRootImpl:mFullRedrawNeeded	Z
    //   3173: iload 17
    //   3175: istore 33
    //   3177: iload 8
    //   3179: istore 19
    //   3181: iload 10
    //   3183: istore 6
    //   3185: iload 32
    //   3187: istore 35
    //   3189: iload_2
    //   3190: istore 34
    //   3192: aload_0
    //   3193: getfield 823	android/view/ViewRootImpl:mPreviousTransparentRegion	Landroid/graphics/Region;
    //   3196: invokevirtual 2781	android/graphics/Region:setEmpty	()V
    //   3199: iload 17
    //   3201: istore 33
    //   3203: iload 8
    //   3205: istore 19
    //   3207: iload 10
    //   3209: istore 6
    //   3211: iload 32
    //   3213: istore 35
    //   3215: iload_2
    //   3216: istore 34
    //   3218: iconst_1
    //   3219: invokestatic 2784	android/view/Choreographer:setNextFrameAtFront	(Z)V
    //   3222: iload 17
    //   3224: istore 33
    //   3226: iload 8
    //   3228: istore 19
    //   3230: iload 10
    //   3232: istore 6
    //   3234: iload 32
    //   3236: istore 35
    //   3238: iload_2
    //   3239: istore 34
    //   3241: aload_0
    //   3242: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   3245: getfield 1222	android/view/View$AttachInfo:mThreadedRenderer	Landroid/view/ThreadedRenderer;
    //   3248: astore 13
    //   3250: iload 30
    //   3252: istore 12
    //   3254: aload 13
    //   3256: ifnull +685 -> 3941
    //   3259: iload 17
    //   3261: istore 33
    //   3263: iload 8
    //   3265: istore 19
    //   3267: iload 10
    //   3269: istore 6
    //   3271: iload 32
    //   3273: istore 35
    //   3275: iload_2
    //   3276: istore 34
    //   3278: aload_0
    //   3279: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   3282: getfield 1222	android/view/View$AttachInfo:mThreadedRenderer	Landroid/view/ThreadedRenderer;
    //   3285: aload_0
    //   3286: getfield 602	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   3289: invokevirtual 2788	android/view/ThreadedRenderer:initialize	(Landroid/view/Surface;)Z
    //   3292: istore 12
    //   3294: iload 12
    //   3296: ifeq +40 -> 3336
    //   3299: iload 12
    //   3301: istore 35
    //   3303: aload_1
    //   3304: getfield 1582	android/view/View:mPrivateFlags	I
    //   3307: sipush 512
    //   3310: iand
    //   3311: ifne +25 -> 3336
    //   3314: iload 12
    //   3316: istore 35
    //   3318: aload_0
    //   3319: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   3322: getfield 1222	android/view/View$AttachInfo:mThreadedRenderer	Landroid/view/ThreadedRenderer;
    //   3325: invokevirtual 2791	android/view/ThreadedRenderer:allocateBuffers	()V
    //   3328: goto +8 -> 3336
    //   3331: astore 13
    //   3333: goto +11 -> 3344
    //   3336: goto +605 -> 3941
    //   3339: astore 13
    //   3341: iconst_0
    //   3342: istore 12
    //   3344: iload 12
    //   3346: istore 35
    //   3348: aload_0
    //   3349: aload 13
    //   3351: invokespecial 1535	android/view/ViewRootImpl:handleOutOfResourcesException	(Landroid/view/Surface$OutOfResourcesException;)V
    //   3354: return
    //   3355: astore 13
    //   3357: iload 35
    //   3359: istore 12
    //   3361: iload 7
    //   3363: istore 6
    //   3365: iload 17
    //   3367: istore 16
    //   3369: iload 10
    //   3371: istore 17
    //   3373: iload_2
    //   3374: istore 7
    //   3376: goto +1123 -> 4499
    //   3379: iload 17
    //   3381: istore 33
    //   3383: iload 8
    //   3385: istore 34
    //   3387: iload 10
    //   3389: istore 6
    //   3391: iload 31
    //   3393: istore 35
    //   3395: iload_2
    //   3396: istore 19
    //   3398: aload_0
    //   3399: getfield 602	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   3402: invokevirtual 1366	android/view/Surface:isValid	()Z
    //   3405: istore 12
    //   3407: iload 12
    //   3409: ifne +324 -> 3733
    //   3412: iload 17
    //   3414: istore 33
    //   3416: iload 8
    //   3418: istore 19
    //   3420: iload 10
    //   3422: istore 6
    //   3424: iload 32
    //   3426: istore 35
    //   3428: iload_2
    //   3429: istore 34
    //   3431: aload_0
    //   3432: getfield 2793	android/view/ViewRootImpl:mLastScrolledFocus	Ljava/lang/ref/WeakReference;
    //   3435: ifnull +29 -> 3464
    //   3438: iload 17
    //   3440: istore 33
    //   3442: iload 8
    //   3444: istore 19
    //   3446: iload 10
    //   3448: istore 6
    //   3450: iload 32
    //   3452: istore 35
    //   3454: iload_2
    //   3455: istore 34
    //   3457: aload_0
    //   3458: getfield 2793	android/view/ViewRootImpl:mLastScrolledFocus	Ljava/lang/ref/WeakReference;
    //   3461: invokevirtual 2796	java/lang/ref/WeakReference:clear	()V
    //   3464: iload 17
    //   3466: istore 33
    //   3468: iload 8
    //   3470: istore 19
    //   3472: iload 10
    //   3474: istore 6
    //   3476: iload 32
    //   3478: istore 35
    //   3480: iload_2
    //   3481: istore 34
    //   3483: aload_0
    //   3484: iconst_0
    //   3485: putfield 1402	android/view/ViewRootImpl:mCurScrollY	I
    //   3488: iload 17
    //   3490: istore 33
    //   3492: iload 8
    //   3494: istore 19
    //   3496: iload 10
    //   3498: istore 6
    //   3500: iload 32
    //   3502: istore 35
    //   3504: iload_2
    //   3505: istore 34
    //   3507: aload_0
    //   3508: iconst_0
    //   3509: putfield 1400	android/view/ViewRootImpl:mScrollY	I
    //   3512: iload 17
    //   3514: istore 33
    //   3516: iload 8
    //   3518: istore 19
    //   3520: iload 10
    //   3522: istore 6
    //   3524: iload 32
    //   3526: istore 35
    //   3528: iload_2
    //   3529: istore 34
    //   3531: aload_0
    //   3532: getfield 1150	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   3535: instanceof 1404
    //   3538: ifeq +38 -> 3576
    //   3541: iload 17
    //   3543: istore 33
    //   3545: iload 8
    //   3547: istore 19
    //   3549: iload 10
    //   3551: istore 6
    //   3553: iload 32
    //   3555: istore 35
    //   3557: iload_2
    //   3558: istore 34
    //   3560: aload_0
    //   3561: getfield 1150	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   3564: checkcast 1404	com/android/internal/view/RootViewSurfaceTaker
    //   3567: aload_0
    //   3568: getfield 1402	android/view/ViewRootImpl:mCurScrollY	I
    //   3571: invokeinterface 1407 2 0
    //   3576: iload 17
    //   3578: istore 33
    //   3580: iload 8
    //   3582: istore 19
    //   3584: iload 10
    //   3586: istore 6
    //   3588: iload 32
    //   3590: istore 35
    //   3592: iload_2
    //   3593: istore 34
    //   3595: aload_0
    //   3596: getfield 1390	android/view/ViewRootImpl:mScroller	Landroid/widget/Scroller;
    //   3599: ifnull +29 -> 3628
    //   3602: iload 17
    //   3604: istore 33
    //   3606: iload 8
    //   3608: istore 19
    //   3610: iload 10
    //   3612: istore 6
    //   3614: iload 32
    //   3616: istore 35
    //   3618: iload_2
    //   3619: istore 34
    //   3621: aload_0
    //   3622: getfield 1390	android/view/ViewRootImpl:mScroller	Landroid/widget/Scroller;
    //   3625: invokevirtual 1421	android/widget/Scroller:abortAnimation	()V
    //   3628: iload 17
    //   3630: istore 33
    //   3632: iload 8
    //   3634: istore 19
    //   3636: iload 10
    //   3638: istore 6
    //   3640: iload 32
    //   3642: istore 35
    //   3644: iload_2
    //   3645: istore 34
    //   3647: iload 30
    //   3649: istore 12
    //   3651: aload_0
    //   3652: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   3655: getfield 1222	android/view/View$AttachInfo:mThreadedRenderer	Landroid/view/ThreadedRenderer;
    //   3658: ifnull +283 -> 3941
    //   3661: iload 17
    //   3663: istore 33
    //   3665: iload 8
    //   3667: istore 19
    //   3669: iload 10
    //   3671: istore 6
    //   3673: iload 32
    //   3675: istore 35
    //   3677: iload_2
    //   3678: istore 34
    //   3680: iload 30
    //   3682: istore 12
    //   3684: aload_0
    //   3685: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   3688: getfield 1222	android/view/View$AttachInfo:mThreadedRenderer	Landroid/view/ThreadedRenderer;
    //   3691: invokevirtual 1480	android/view/ThreadedRenderer:isEnabled	()Z
    //   3694: ifeq +247 -> 3941
    //   3697: iload 17
    //   3699: istore 33
    //   3701: iload 8
    //   3703: istore 19
    //   3705: iload 10
    //   3707: istore 6
    //   3709: iload 32
    //   3711: istore 35
    //   3713: iload_2
    //   3714: istore 34
    //   3716: aload_0
    //   3717: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   3720: getfield 1222	android/view/View$AttachInfo:mThreadedRenderer	Landroid/view/ThreadedRenderer;
    //   3723: invokevirtual 1230	android/view/ThreadedRenderer:destroy	()V
    //   3726: iload 30
    //   3728: istore 12
    //   3730: goto +211 -> 3941
    //   3733: iload 17
    //   3735: istore 33
    //   3737: iload 8
    //   3739: istore 34
    //   3741: iload 10
    //   3743: istore 6
    //   3745: iload 31
    //   3747: istore 35
    //   3749: iload_2
    //   3750: istore 19
    //   3752: iload 21
    //   3754: aload_0
    //   3755: getfield 602	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   3758: invokevirtual 2742	android/view/Surface:getGenerationId	()I
    //   3761: if_icmpne +21 -> 3782
    //   3764: iload_2
    //   3765: ifne +17 -> 3782
    //   3768: iload 23
    //   3770: ifne +12 -> 3782
    //   3773: iload 30
    //   3775: istore 12
    //   3777: iload 36
    //   3779: ifeq +162 -> 3941
    //   3782: iload 17
    //   3784: istore 33
    //   3786: iload 8
    //   3788: istore 34
    //   3790: iload 10
    //   3792: istore 6
    //   3794: iload 31
    //   3796: istore 35
    //   3798: iload_2
    //   3799: istore 19
    //   3801: aload_0
    //   3802: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   3805: astore 13
    //   3807: iload 30
    //   3809: istore 12
    //   3811: aload 13
    //   3813: ifnonnull +128 -> 3941
    //   3816: iload 17
    //   3818: istore 33
    //   3820: iload 8
    //   3822: istore 19
    //   3824: iload 10
    //   3826: istore 6
    //   3828: iload 32
    //   3830: istore 35
    //   3832: iload_2
    //   3833: istore 34
    //   3835: iload 30
    //   3837: istore 12
    //   3839: aload_0
    //   3840: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   3843: getfield 1222	android/view/View$AttachInfo:mThreadedRenderer	Landroid/view/ThreadedRenderer;
    //   3846: ifnull +95 -> 3941
    //   3849: iload 17
    //   3851: istore 33
    //   3853: iload 8
    //   3855: istore 19
    //   3857: iload 10
    //   3859: istore 6
    //   3861: iload 32
    //   3863: istore 35
    //   3865: iload_2
    //   3866: istore 34
    //   3868: aload_0
    //   3869: iconst_1
    //   3870: putfield 1528	android/view/ViewRootImpl:mFullRedrawNeeded	Z
    //   3873: iload 17
    //   3875: istore 33
    //   3877: iload 8
    //   3879: istore 19
    //   3881: iload 10
    //   3883: istore 6
    //   3885: iload 32
    //   3887: istore 35
    //   3889: iload_2
    //   3890: istore 34
    //   3892: aload_0
    //   3893: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   3896: getfield 1222	android/view/View$AttachInfo:mThreadedRenderer	Landroid/view/ThreadedRenderer;
    //   3899: aload_0
    //   3900: getfield 602	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   3903: invokevirtual 2800	android/view/ThreadedRenderer:updateSurface	(Landroid/view/Surface;)V
    //   3906: iload 30
    //   3908: istore 12
    //   3910: goto +31 -> 3941
    //   3913: astore 13
    //   3915: iload 17
    //   3917: istore 33
    //   3919: iload 8
    //   3921: istore 19
    //   3923: iload 10
    //   3925: istore 6
    //   3927: iload 32
    //   3929: istore 35
    //   3931: iload_2
    //   3932: istore 34
    //   3934: aload_0
    //   3935: aload 13
    //   3937: invokespecial 1535	android/view/ViewRootImpl:handleOutOfResourcesException	(Landroid/view/Surface$OutOfResourcesException;)V
    //   3940: return
    //   3941: iload 7
    //   3943: bipush 16
    //   3945: iand
    //   3946: ifeq +9 -> 3955
    //   3949: iconst_1
    //   3950: istore 16
    //   3952: goto +6 -> 3958
    //   3955: iconst_0
    //   3956: istore 16
    //   3958: iload 7
    //   3960: bipush 8
    //   3962: iand
    //   3963: ifeq +9 -> 3972
    //   3966: iconst_1
    //   3967: istore 6
    //   3969: goto +6 -> 3975
    //   3972: iconst_0
    //   3973: istore 6
    //   3975: iload 16
    //   3977: ifne +17 -> 3994
    //   3980: iload 6
    //   3982: ifeq +6 -> 3988
    //   3985: goto +9 -> 3994
    //   3988: iconst_0
    //   3989: istore 32
    //   3991: goto +6 -> 3997
    //   3994: iconst_1
    //   3995: istore 32
    //   3997: iload 17
    //   3999: istore 33
    //   4001: iload 8
    //   4003: istore 34
    //   4005: iload 10
    //   4007: istore 6
    //   4009: iload 12
    //   4011: istore 35
    //   4013: iload_2
    //   4014: istore 19
    //   4016: aload_0
    //   4017: getfield 1278	android/view/ViewRootImpl:mDragResizing	Z
    //   4020: iload 32
    //   4022: if_icmpeq +322 -> 4344
    //   4025: iload 32
    //   4027: ifeq +310 -> 4337
    //   4030: iload 16
    //   4032: ifeq +9 -> 4041
    //   4035: iconst_0
    //   4036: istore 16
    //   4038: goto +6 -> 4044
    //   4041: iconst_1
    //   4042: istore 16
    //   4044: iload 17
    //   4046: istore 33
    //   4048: iload 8
    //   4050: istore 34
    //   4052: iload 10
    //   4054: istore 6
    //   4056: iload 12
    //   4058: istore 35
    //   4060: iload_2
    //   4061: istore 19
    //   4063: aload_0
    //   4064: iload 16
    //   4066: putfield 2731	android/view/ViewRootImpl:mResizeMode	I
    //   4069: iload 17
    //   4071: istore 33
    //   4073: iload 8
    //   4075: istore 34
    //   4077: iload 10
    //   4079: istore 6
    //   4081: iload 12
    //   4083: istore 35
    //   4085: iload_2
    //   4086: istore 19
    //   4088: aload_0
    //   4089: getfield 800	android/view/ViewRootImpl:mWinFrame	Landroid/graphics/Rect;
    //   4092: invokevirtual 2617	android/graphics/Rect:width	()I
    //   4095: istore 27
    //   4097: iload 17
    //   4099: istore 33
    //   4101: iload 8
    //   4103: istore 34
    //   4105: iload 10
    //   4107: istore 6
    //   4109: iload 12
    //   4111: istore 35
    //   4113: iload_2
    //   4114: istore 19
    //   4116: aload_0
    //   4117: getfield 628	android/view/ViewRootImpl:mPendingBackDropFrame	Landroid/graphics/Rect;
    //   4120: invokevirtual 2617	android/graphics/Rect:width	()I
    //   4123: istore 16
    //   4125: iload 27
    //   4127: iload 16
    //   4129: if_icmpne +72 -> 4201
    //   4132: iload 17
    //   4134: istore 33
    //   4136: iload 8
    //   4138: istore 19
    //   4140: iload 10
    //   4142: istore 6
    //   4144: iload 12
    //   4146: istore 35
    //   4148: iload_2
    //   4149: istore 34
    //   4151: aload_0
    //   4152: getfield 800	android/view/ViewRootImpl:mWinFrame	Landroid/graphics/Rect;
    //   4155: invokevirtual 2619	android/graphics/Rect:height	()I
    //   4158: istore 27
    //   4160: iload 17
    //   4162: istore 33
    //   4164: iload 8
    //   4166: istore 19
    //   4168: iload 10
    //   4170: istore 6
    //   4172: iload 12
    //   4174: istore 35
    //   4176: iload_2
    //   4177: istore 34
    //   4179: aload_0
    //   4180: getfield 628	android/view/ViewRootImpl:mPendingBackDropFrame	Landroid/graphics/Rect;
    //   4183: invokevirtual 2619	android/graphics/Rect:height	()I
    //   4186: istore 16
    //   4188: iload 27
    //   4190: iload 16
    //   4192: if_icmpne +9 -> 4201
    //   4195: iconst_1
    //   4196: istore 16
    //   4198: goto +6 -> 4204
    //   4201: iconst_0
    //   4202: istore 16
    //   4204: iload 17
    //   4206: istore 33
    //   4208: iload 8
    //   4210: istore 34
    //   4212: iload 10
    //   4214: istore 6
    //   4216: iload 12
    //   4218: istore 35
    //   4220: iload_2
    //   4221: istore 19
    //   4223: aload_0
    //   4224: getfield 628	android/view/ViewRootImpl:mPendingBackDropFrame	Landroid/graphics/Rect;
    //   4227: astore 15
    //   4229: iload 16
    //   4231: ifne +9 -> 4240
    //   4234: iconst_1
    //   4235: istore 30
    //   4237: goto +6 -> 4243
    //   4240: iconst_0
    //   4241: istore 30
    //   4243: iload 17
    //   4245: istore 33
    //   4247: iload 8
    //   4249: istore 34
    //   4251: iload 10
    //   4253: istore 6
    //   4255: iload 12
    //   4257: istore 35
    //   4259: iload_2
    //   4260: istore 19
    //   4262: aload_0
    //   4263: getfield 620	android/view/ViewRootImpl:mPendingVisibleInsets	Landroid/graphics/Rect;
    //   4266: astore 14
    //   4268: aload_0
    //   4269: getfield 622	android/view/ViewRootImpl:mPendingStableInsets	Landroid/graphics/Rect;
    //   4272: astore 13
    //   4274: aload_0
    //   4275: getfield 2731	android/view/ViewRootImpl:mResizeMode	I
    //   4278: istore 6
    //   4280: aload_0
    //   4281: aload 15
    //   4283: iload 30
    //   4285: aload 14
    //   4287: aload 13
    //   4289: iload 6
    //   4291: invokespecial 2804	android/view/ViewRootImpl:startDragResizing	(Landroid/graphics/Rect;ZLandroid/graphics/Rect;Landroid/graphics/Rect;I)V
    //   4294: goto +50 -> 4344
    //   4297: astore 13
    //   4299: iload 7
    //   4301: istore 6
    //   4303: iload 17
    //   4305: istore 16
    //   4307: iload 10
    //   4309: istore 17
    //   4311: iload_2
    //   4312: istore 7
    //   4314: goto +185 -> 4499
    //   4317: astore 13
    //   4319: iload 7
    //   4321: istore 6
    //   4323: iload 17
    //   4325: istore 16
    //   4327: iload 10
    //   4329: istore 17
    //   4331: iload_2
    //   4332: istore 7
    //   4334: goto +165 -> 4499
    //   4337: aload_0
    //   4338: invokespecial 2655	android/view/ViewRootImpl:endDragResizing	()V
    //   4341: goto +3 -> 4344
    //   4344: aload_0
    //   4345: getfield 1280	android/view/ViewRootImpl:mUseMTRenderer	Z
    //   4348: ifne +43 -> 4391
    //   4351: iload 32
    //   4353: ifeq +28 -> 4381
    //   4356: aload_0
    //   4357: aload_0
    //   4358: getfield 800	android/view/ViewRootImpl:mWinFrame	Landroid/graphics/Rect;
    //   4361: getfield 1438	android/graphics/Rect:left	I
    //   4364: putfield 1430	android/view/ViewRootImpl:mCanvasOffsetX	I
    //   4367: aload_0
    //   4368: aload_0
    //   4369: getfield 800	android/view/ViewRootImpl:mWinFrame	Landroid/graphics/Rect;
    //   4372: getfield 1441	android/graphics/Rect:top	I
    //   4375: putfield 1432	android/view/ViewRootImpl:mCanvasOffsetY	I
    //   4378: goto +13 -> 4391
    //   4381: aload_0
    //   4382: iconst_0
    //   4383: putfield 1432	android/view/ViewRootImpl:mCanvasOffsetY	I
    //   4386: aload_0
    //   4387: iconst_0
    //   4388: putfield 1430	android/view/ViewRootImpl:mCanvasOffsetX	I
    //   4391: iload 8
    //   4393: istore 16
    //   4395: iload_2
    //   4396: istore 6
    //   4398: iload 7
    //   4400: istore 8
    //   4402: iload 16
    //   4404: istore 7
    //   4406: iload 17
    //   4408: istore 16
    //   4410: goto +107 -> 4517
    //   4413: iload 7
    //   4415: istore 6
    //   4417: astore 13
    //   4419: iload 17
    //   4421: istore 16
    //   4423: iload 10
    //   4425: istore 17
    //   4427: iload_2
    //   4428: istore 7
    //   4430: goto +69 -> 4499
    //   4433: astore 13
    //   4435: iload 7
    //   4437: istore 10
    //   4439: iload 33
    //   4441: istore 16
    //   4443: iload 34
    //   4445: istore 8
    //   4447: iload 6
    //   4449: istore 17
    //   4451: iload 35
    //   4453: istore 12
    //   4455: iload 19
    //   4457: istore 7
    //   4459: iload 10
    //   4461: istore 6
    //   4463: goto +36 -> 4499
    //   4466: astore 13
    //   4468: iconst_0
    //   4469: istore_2
    //   4470: iload 10
    //   4472: istore 16
    //   4474: iload 6
    //   4476: istore 7
    //   4478: iload_2
    //   4479: istore 6
    //   4481: goto +18 -> 4499
    //   4484: astore 13
    //   4486: iconst_0
    //   4487: istore 12
    //   4489: iconst_0
    //   4490: istore 7
    //   4492: iconst_0
    //   4493: istore 6
    //   4495: iload 10
    //   4497: istore 16
    //   4499: iload 6
    //   4501: istore_2
    //   4502: iload 17
    //   4504: istore 10
    //   4506: iload 7
    //   4508: istore 6
    //   4510: iload 8
    //   4512: istore 7
    //   4514: iload_2
    //   4515: istore 8
    //   4517: aload_0
    //   4518: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   4521: aload 9
    //   4523: getfield 1438	android/graphics/Rect:left	I
    //   4526: putfield 1951	android/view/View$AttachInfo:mWindowLeft	I
    //   4529: aload_0
    //   4530: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   4533: aload 9
    //   4535: getfield 1441	android/graphics/Rect:top	I
    //   4538: putfield 1954	android/view/View$AttachInfo:mWindowTop	I
    //   4541: aload_0
    //   4542: getfield 790	android/view/ViewRootImpl:mWidth	I
    //   4545: aload 9
    //   4547: invokevirtual 2617	android/graphics/Rect:width	()I
    //   4550: if_icmpne +15 -> 4565
    //   4553: aload_0
    //   4554: getfield 792	android/view/ViewRootImpl:mHeight	I
    //   4557: aload 9
    //   4559: invokevirtual 2619	android/graphics/Rect:height	()I
    //   4562: if_icmpeq +21 -> 4583
    //   4565: aload_0
    //   4566: aload 9
    //   4568: invokevirtual 2617	android/graphics/Rect:width	()I
    //   4571: putfield 790	android/view/ViewRootImpl:mWidth	I
    //   4574: aload_0
    //   4575: aload 9
    //   4577: invokevirtual 2619	android/graphics/Rect:height	()I
    //   4580: putfield 792	android/view/ViewRootImpl:mHeight	I
    //   4583: aload_0
    //   4584: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   4587: ifnull +298 -> 4885
    //   4590: aload_0
    //   4591: getfield 602	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   4594: invokevirtual 1366	android/view/Surface:isValid	()Z
    //   4597: ifeq +14 -> 4611
    //   4600: aload_0
    //   4601: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   4604: aload_0
    //   4605: getfield 602	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   4608: putfield 2805	com/android/internal/view/BaseSurfaceHolder:mSurface	Landroid/view/Surface;
    //   4611: aload_0
    //   4612: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   4615: aload_0
    //   4616: getfield 790	android/view/ViewRootImpl:mWidth	I
    //   4619: aload_0
    //   4620: getfield 792	android/view/ViewRootImpl:mHeight	I
    //   4623: invokevirtual 2808	com/android/internal/view/BaseSurfaceHolder:setSurfaceFrameSize	(II)V
    //   4626: aload_0
    //   4627: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   4630: getfield 2748	com/android/internal/view/BaseSurfaceHolder:mSurfaceLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4633: invokevirtual 2811	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   4636: aload_0
    //   4637: getfield 602	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   4640: invokevirtual 1366	android/view/Surface:isValid	()Z
    //   4643: ifeq +165 -> 4808
    //   4646: iload 28
    //   4648: ifne +67 -> 4715
    //   4651: aload_0
    //   4652: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   4655: invokevirtual 2415	com/android/internal/view/BaseSurfaceHolder:ungetCallbacks	()V
    //   4658: aload_0
    //   4659: iconst_1
    //   4660: putfield 2813	android/view/ViewRootImpl:mIsCreating	Z
    //   4663: aload_0
    //   4664: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   4667: invokevirtual 2419	com/android/internal/view/BaseSurfaceHolder:getCallbacks	()[Landroid/view/SurfaceHolder$Callback;
    //   4670: astore 13
    //   4672: aload 13
    //   4674: ifnull +38 -> 4712
    //   4677: aload 13
    //   4679: arraylength
    //   4680: istore 16
    //   4682: iconst_0
    //   4683: istore_2
    //   4684: iload_2
    //   4685: iload 16
    //   4687: if_icmpge +22 -> 4709
    //   4690: aload 13
    //   4692: iload_2
    //   4693: aaload
    //   4694: aload_0
    //   4695: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   4698: invokeinterface 2816 2 0
    //   4703: iinc 2 1
    //   4706: goto -22 -> 4684
    //   4709: goto +3 -> 4712
    //   4712: iconst_1
    //   4713: istore 16
    //   4715: iload 16
    //   4717: ifne +21 -> 4738
    //   4720: iload 21
    //   4722: aload_0
    //   4723: getfield 602	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   4726: invokevirtual 2742	android/view/Surface:getGenerationId	()I
    //   4729: if_icmpeq +6 -> 4735
    //   4732: goto +6 -> 4738
    //   4735: goto +65 -> 4800
    //   4738: aload_0
    //   4739: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   4742: invokevirtual 2419	com/android/internal/view/BaseSurfaceHolder:getCallbacks	()[Landroid/view/SurfaceHolder$Callback;
    //   4745: astore 13
    //   4747: aload 13
    //   4749: ifnull +51 -> 4800
    //   4752: aload 13
    //   4754: arraylength
    //   4755: istore_2
    //   4756: iconst_0
    //   4757: istore 16
    //   4759: iload 16
    //   4761: iload_2
    //   4762: if_icmpge +35 -> 4797
    //   4765: aload 13
    //   4767: iload 16
    //   4769: aaload
    //   4770: aload_0
    //   4771: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   4774: aload_3
    //   4775: getfield 1663	android/view/WindowManager$LayoutParams:format	I
    //   4778: aload_0
    //   4779: getfield 790	android/view/ViewRootImpl:mWidth	I
    //   4782: aload_0
    //   4783: getfield 792	android/view/ViewRootImpl:mHeight	I
    //   4786: invokeinterface 2820 5 0
    //   4791: iinc 16 1
    //   4794: goto -35 -> 4759
    //   4797: goto +3 -> 4800
    //   4800: aload_0
    //   4801: iconst_0
    //   4802: putfield 2813	android/view/ViewRootImpl:mIsCreating	Z
    //   4805: goto +80 -> 4885
    //   4808: iload 6
    //   4810: istore_2
    //   4811: iload_2
    //   4812: istore 6
    //   4814: iload 28
    //   4816: ifeq +69 -> 4885
    //   4819: aload_0
    //   4820: invokespecial 2822	android/view/ViewRootImpl:notifySurfaceDestroyed	()V
    //   4823: aload_0
    //   4824: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   4827: getfield 2748	com/android/internal/view/BaseSurfaceHolder:mSurfaceLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4830: invokevirtual 2753	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   4833: aload_0
    //   4834: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   4837: astore 13
    //   4839: new 599	android/view/Surface
    //   4842: astore 9
    //   4844: aload 9
    //   4846: invokespecial 600	android/view/Surface:<init>	()V
    //   4849: aload 13
    //   4851: aload 9
    //   4853: putfield 2805	com/android/internal/view/BaseSurfaceHolder:mSurface	Landroid/view/Surface;
    //   4856: aload_0
    //   4857: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   4860: getfield 2748	com/android/internal/view/BaseSurfaceHolder:mSurfaceLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4863: invokevirtual 2811	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   4866: iload_2
    //   4867: istore 6
    //   4869: goto +16 -> 4885
    //   4872: astore_3
    //   4873: aload_0
    //   4874: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   4877: getfield 2748	com/android/internal/view/BaseSurfaceHolder:mSurfaceLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4880: invokevirtual 2811	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   4883: aload_3
    //   4884: athrow
    //   4885: aload_0
    //   4886: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   4889: getfield 1222	android/view/View$AttachInfo:mThreadedRenderer	Landroid/view/ThreadedRenderer;
    //   4892: astore 9
    //   4894: aload 9
    //   4896: ifnull +76 -> 4972
    //   4899: aload 9
    //   4901: invokevirtual 1480	android/view/ThreadedRenderer:isEnabled	()Z
    //   4904: ifeq +68 -> 4972
    //   4907: iload 12
    //   4909: ifne +34 -> 4943
    //   4912: aload_0
    //   4913: getfield 790	android/view/ViewRootImpl:mWidth	I
    //   4916: aload 9
    //   4918: invokevirtual 1509	android/view/ThreadedRenderer:getWidth	()I
    //   4921: if_icmpne +22 -> 4943
    //   4924: aload_0
    //   4925: getfield 792	android/view/ViewRootImpl:mHeight	I
    //   4928: aload 9
    //   4930: invokevirtual 1512	android/view/ThreadedRenderer:getHeight	()I
    //   4933: if_icmpne +10 -> 4943
    //   4936: aload_0
    //   4937: getfield 2824	android/view/ViewRootImpl:mNeedsRendererSetup	Z
    //   4940: ifeq +32 -> 4972
    //   4943: aload 9
    //   4945: aload_0
    //   4946: getfield 790	android/view/ViewRootImpl:mWidth	I
    //   4949: aload_0
    //   4950: getfield 792	android/view/ViewRootImpl:mHeight	I
    //   4953: aload_0
    //   4954: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   4957: aload_0
    //   4958: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   4961: getfield 1435	android/view/WindowManager$LayoutParams:surfaceInsets	Landroid/graphics/Rect;
    //   4964: invokevirtual 1516	android/view/ThreadedRenderer:setup	(IILandroid/view/View$AttachInfo;Landroid/graphics/Rect;)V
    //   4967: aload_0
    //   4968: iconst_0
    //   4969: putfield 2824	android/view/ViewRootImpl:mNeedsRendererSetup	Z
    //   4972: aload_0
    //   4973: getfield 563	android/view/ViewRootImpl:mStopped	Z
    //   4976: ifeq +10 -> 4986
    //   4979: aload_0
    //   4980: getfield 1494	android/view/ViewRootImpl:mReportNextDraw	Z
    //   4983: ifeq +63 -> 5046
    //   4986: iload 8
    //   4988: iconst_1
    //   4989: iand
    //   4990: ifeq +9 -> 4999
    //   4993: iconst_1
    //   4994: istore 12
    //   4996: goto +6 -> 5002
    //   4999: iconst_0
    //   5000: istore 12
    //   5002: aload_0
    //   5003: iload 12
    //   5005: invokespecial 2220	android/view/ViewRootImpl:ensureTouchModeLocally	(Z)Z
    //   5008: ifne +41 -> 5049
    //   5011: aload_0
    //   5012: getfield 790	android/view/ViewRootImpl:mWidth	I
    //   5015: aload_1
    //   5016: invokevirtual 2406	android/view/View:getMeasuredWidth	()I
    //   5019: if_icmpne +30 -> 5049
    //   5022: aload_0
    //   5023: getfield 792	android/view/ViewRootImpl:mHeight	I
    //   5026: aload_1
    //   5027: invokevirtual 2409	android/view/View:getMeasuredHeight	()I
    //   5030: if_icmpne +19 -> 5049
    //   5033: iload 10
    //   5035: ifne +14 -> 5049
    //   5038: iload 7
    //   5040: ifeq +6 -> 5046
    //   5043: goto +6 -> 5049
    //   5046: goto +138 -> 5184
    //   5049: aload_0
    //   5050: getfield 790	android/view/ViewRootImpl:mWidth	I
    //   5053: aload_3
    //   5054: getfield 2379	android/view/WindowManager$LayoutParams:width	I
    //   5057: invokestatic 2393	android/view/ViewRootImpl:getRootMeasureSpec	(II)I
    //   5060: istore_2
    //   5061: aload_0
    //   5062: getfield 792	android/view/ViewRootImpl:mHeight	I
    //   5065: aload_3
    //   5066: getfield 2396	android/view/WindowManager$LayoutParams:height	I
    //   5069: invokestatic 2393	android/view/ViewRootImpl:getRootMeasureSpec	(II)I
    //   5072: istore 16
    //   5074: aload_0
    //   5075: iload_2
    //   5076: iload 16
    //   5078: invokespecial 2399	android/view/ViewRootImpl:performMeasure	(II)V
    //   5081: aload_1
    //   5082: invokevirtual 2406	android/view/View:getMeasuredWidth	()I
    //   5085: istore 18
    //   5087: aload_1
    //   5088: invokevirtual 2409	android/view/View:getMeasuredHeight	()I
    //   5091: istore 17
    //   5093: iconst_0
    //   5094: istore 10
    //   5096: aload_3
    //   5097: getfield 2827	android/view/WindowManager$LayoutParams:horizontalWeight	F
    //   5100: fconst_0
    //   5101: fcmpl
    //   5102: ifle +30 -> 5132
    //   5105: iload 18
    //   5107: aload_0
    //   5108: getfield 790	android/view/ViewRootImpl:mWidth	I
    //   5111: iload 18
    //   5113: isub
    //   5114: i2f
    //   5115: aload_3
    //   5116: getfield 2827	android/view/WindowManager$LayoutParams:horizontalWeight	F
    //   5119: fmul
    //   5120: f2i
    //   5121: iadd
    //   5122: ldc_w 2017
    //   5125: invokestatic 2022	android/view/View$MeasureSpec:makeMeasureSpec	(II)I
    //   5128: istore_2
    //   5129: iconst_1
    //   5130: istore 10
    //   5132: aload_3
    //   5133: getfield 2830	android/view/WindowManager$LayoutParams:verticalWeight	F
    //   5136: fconst_0
    //   5137: fcmpl
    //   5138: ifle +31 -> 5169
    //   5141: iload 17
    //   5143: aload_0
    //   5144: getfield 792	android/view/ViewRootImpl:mHeight	I
    //   5147: iload 17
    //   5149: isub
    //   5150: i2f
    //   5151: aload_3
    //   5152: getfield 2830	android/view/WindowManager$LayoutParams:verticalWeight	F
    //   5155: fmul
    //   5156: f2i
    //   5157: iadd
    //   5158: ldc_w 2017
    //   5161: invokestatic 2022	android/view/View$MeasureSpec:makeMeasureSpec	(II)I
    //   5164: istore 16
    //   5166: iconst_1
    //   5167: istore 10
    //   5169: iload 10
    //   5171: ifeq +10 -> 5181
    //   5174: aload_0
    //   5175: iload_2
    //   5176: iload 16
    //   5178: invokespecial 2399	android/view/ViewRootImpl:performMeasure	(II)V
    //   5181: iconst_1
    //   5182: istore 18
    //   5184: iload 11
    //   5186: istore 12
    //   5188: iload 6
    //   5190: istore 10
    //   5192: iload 7
    //   5194: istore 6
    //   5196: iload 8
    //   5198: istore 7
    //   5200: iload 10
    //   5202: ifeq +7 -> 5209
    //   5205: aload_0
    //   5206: invokespecial 2833	android/view/ViewRootImpl:updateBoundsSurface	()V
    //   5209: iload 18
    //   5211: ifeq +23 -> 5234
    //   5214: aload_0
    //   5215: getfield 563	android/view/ViewRootImpl:mStopped	Z
    //   5218: ifeq +10 -> 5228
    //   5221: aload_0
    //   5222: getfield 1494	android/view/ViewRootImpl:mReportNextDraw	Z
    //   5225: ifeq +9 -> 5234
    //   5228: iconst_1
    //   5229: istore 6
    //   5231: goto +6 -> 5237
    //   5234: iconst_0
    //   5235: istore 6
    //   5237: iload 6
    //   5239: ifne +22 -> 5261
    //   5242: aload_0
    //   5243: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   5246: getfield 1142	android/view/View$AttachInfo:mRecomputeGlobalAttributes	Z
    //   5249: ifeq +6 -> 5255
    //   5252: goto +9 -> 5261
    //   5255: iconst_0
    //   5256: istore 8
    //   5258: goto +6 -> 5264
    //   5261: iconst_1
    //   5262: istore 8
    //   5264: iload 6
    //   5266: ifeq +173 -> 5439
    //   5269: aload_0
    //   5270: aload_3
    //   5271: aload_0
    //   5272: getfield 790	android/view/ViewRootImpl:mWidth	I
    //   5275: aload_0
    //   5276: getfield 792	android/view/ViewRootImpl:mHeight	I
    //   5279: invokespecial 2835	android/view/ViewRootImpl:performLayout	(Landroid/view/WindowManager$LayoutParams;II)V
    //   5282: aload_1
    //   5283: getfield 1582	android/view/View:mPrivateFlags	I
    //   5286: sipush 512
    //   5289: iand
    //   5290: ifeq +146 -> 5436
    //   5293: aload_1
    //   5294: aload_0
    //   5295: getfield 545	android/view/ViewRootImpl:mTmpLocation	[I
    //   5298: invokevirtual 2839	android/view/View:getLocationInWindow	([I)V
    //   5301: aload_0
    //   5302: getfield 821	android/view/ViewRootImpl:mTransparentRegion	Landroid/graphics/Region;
    //   5305: astore_3
    //   5306: aload_0
    //   5307: getfield 545	android/view/ViewRootImpl:mTmpLocation	[I
    //   5310: astore 9
    //   5312: aload_3
    //   5313: aload 9
    //   5315: iconst_0
    //   5316: iaload
    //   5317: aload 9
    //   5319: iconst_1
    //   5320: iaload
    //   5321: aload 9
    //   5323: iconst_0
    //   5324: iaload
    //   5325: aload_1
    //   5326: getfield 2842	android/view/View:mRight	I
    //   5329: iadd
    //   5330: aload_1
    //   5331: getfield 2845	android/view/View:mLeft	I
    //   5334: isub
    //   5335: aload_0
    //   5336: getfield 545	android/view/ViewRootImpl:mTmpLocation	[I
    //   5339: iconst_1
    //   5340: iaload
    //   5341: aload_1
    //   5342: getfield 2848	android/view/View:mBottom	I
    //   5345: iadd
    //   5346: aload_1
    //   5347: getfield 2851	android/view/View:mTop	I
    //   5350: isub
    //   5351: invokevirtual 2853	android/graphics/Region:set	(IIII)Z
    //   5354: pop
    //   5355: aload_1
    //   5356: aload_0
    //   5357: getfield 821	android/view/ViewRootImpl:mTransparentRegion	Landroid/graphics/Region;
    //   5360: invokevirtual 2857	android/view/View:gatherTransparentRegion	(Landroid/graphics/Region;)Z
    //   5363: pop
    //   5364: aload_0
    //   5365: getfield 1300	android/view/ViewRootImpl:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   5368: astore_3
    //   5369: aload_3
    //   5370: ifnull +11 -> 5381
    //   5373: aload_3
    //   5374: aload_0
    //   5375: getfield 821	android/view/ViewRootImpl:mTransparentRegion	Landroid/graphics/Region;
    //   5378: invokevirtual 2861	android/content/res/CompatibilityInfo$Translator:translateRegionInWindowToScreen	(Landroid/graphics/Region;)V
    //   5381: aload_0
    //   5382: getfield 821	android/view/ViewRootImpl:mTransparentRegion	Landroid/graphics/Region;
    //   5385: aload_0
    //   5386: getfield 823	android/view/ViewRootImpl:mPreviousTransparentRegion	Landroid/graphics/Region;
    //   5389: invokevirtual 2862	android/graphics/Region:equals	(Ljava/lang/Object;)Z
    //   5392: ifne +47 -> 5439
    //   5395: aload_0
    //   5396: getfield 823	android/view/ViewRootImpl:mPreviousTransparentRegion	Landroid/graphics/Region;
    //   5399: aload_0
    //   5400: getfield 821	android/view/ViewRootImpl:mTransparentRegion	Landroid/graphics/Region;
    //   5403: invokevirtual 2864	android/graphics/Region:set	(Landroid/graphics/Region;)Z
    //   5406: pop
    //   5407: aload_0
    //   5408: iconst_1
    //   5409: putfield 1528	android/view/ViewRootImpl:mFullRedrawNeeded	Z
    //   5412: aload_0
    //   5413: getfield 759	android/view/ViewRootImpl:mWindowSession	Landroid/view/IWindowSession;
    //   5416: aload_0
    //   5417: getfield 803	android/view/ViewRootImpl:mWindow	Landroid/view/ViewRootImpl$W;
    //   5420: aload_0
    //   5421: getfield 821	android/view/ViewRootImpl:mTransparentRegion	Landroid/graphics/Region;
    //   5424: invokeinterface 2868 3 0
    //   5429: goto +10 -> 5439
    //   5432: astore_3
    //   5433: goto +6 -> 5439
    //   5436: goto +3 -> 5439
    //   5439: iload 8
    //   5441: ifeq +20 -> 5461
    //   5444: aload_0
    //   5445: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   5448: astore_3
    //   5449: aload_3
    //   5450: iconst_0
    //   5451: putfield 1142	android/view/View$AttachInfo:mRecomputeGlobalAttributes	Z
    //   5454: aload_3
    //   5455: getfield 1383	android/view/View$AttachInfo:mTreeObserver	Landroid/view/ViewTreeObserver;
    //   5458: invokevirtual 2871	android/view/ViewTreeObserver:dispatchOnGlobalLayout	()V
    //   5461: iload 20
    //   5463: ifeq +187 -> 5650
    //   5466: aload_0
    //   5467: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   5470: getfield 2874	android/view/View$AttachInfo:mGivenInternalInsets	Landroid/view/ViewTreeObserver$InternalInsetsInfo;
    //   5473: astore 14
    //   5475: aload 14
    //   5477: invokevirtual 2875	android/view/ViewTreeObserver$InternalInsetsInfo:reset	()V
    //   5480: aload_0
    //   5481: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   5484: getfield 1383	android/view/View$AttachInfo:mTreeObserver	Landroid/view/ViewTreeObserver;
    //   5487: aload 14
    //   5489: invokevirtual 2879	android/view/ViewTreeObserver:dispatchOnComputeInternalInsets	(Landroid/view/ViewTreeObserver$InternalInsetsInfo;)V
    //   5492: aload_0
    //   5493: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   5496: aload 14
    //   5498: invokevirtual 2880	android/view/ViewTreeObserver$InternalInsetsInfo:isEmpty	()Z
    //   5501: iconst_1
    //   5502: ixor
    //   5503: putfield 2739	android/view/View$AttachInfo:mHasNonEmptyGivenInternalInsets	Z
    //   5506: iload 12
    //   5508: ifne +21 -> 5529
    //   5511: aload_0
    //   5512: getfield 650	android/view/ViewRootImpl:mLastGivenInsets	Landroid/view/ViewTreeObserver$InternalInsetsInfo;
    //   5515: aload 14
    //   5517: invokevirtual 2881	android/view/ViewTreeObserver$InternalInsetsInfo:equals	(Ljava/lang/Object;)Z
    //   5520: ifne +6 -> 5526
    //   5523: goto +6 -> 5529
    //   5526: goto +124 -> 5650
    //   5529: aload_0
    //   5530: getfield 650	android/view/ViewRootImpl:mLastGivenInsets	Landroid/view/ViewTreeObserver$InternalInsetsInfo;
    //   5533: aload 14
    //   5535: invokevirtual 2883	android/view/ViewTreeObserver$InternalInsetsInfo:set	(Landroid/view/ViewTreeObserver$InternalInsetsInfo;)V
    //   5538: aload_0
    //   5539: getfield 1300	android/view/ViewRootImpl:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   5542: astore_3
    //   5543: aload_3
    //   5544: ifnull +44 -> 5588
    //   5547: aload_3
    //   5548: aload 14
    //   5550: getfield 2886	android/view/ViewTreeObserver$InternalInsetsInfo:contentInsets	Landroid/graphics/Rect;
    //   5553: invokevirtual 2890	android/content/res/CompatibilityInfo$Translator:getTranslatedContentInsets	(Landroid/graphics/Rect;)Landroid/graphics/Rect;
    //   5556: astore 9
    //   5558: aload_0
    //   5559: getfield 1300	android/view/ViewRootImpl:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   5562: aload 14
    //   5564: getfield 2893	android/view/ViewTreeObserver$InternalInsetsInfo:visibleInsets	Landroid/graphics/Rect;
    //   5567: invokevirtual 2896	android/content/res/CompatibilityInfo$Translator:getTranslatedVisibleInsets	(Landroid/graphics/Rect;)Landroid/graphics/Rect;
    //   5570: astore 13
    //   5572: aload_0
    //   5573: getfield 1300	android/view/ViewRootImpl:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   5576: aload 14
    //   5578: getfield 2899	android/view/ViewTreeObserver$InternalInsetsInfo:touchableRegion	Landroid/graphics/Region;
    //   5581: invokevirtual 2903	android/content/res/CompatibilityInfo$Translator:getTranslatedTouchableArea	(Landroid/graphics/Region;)Landroid/graphics/Region;
    //   5584: astore_3
    //   5585: goto +23 -> 5608
    //   5588: aload 14
    //   5590: getfield 2886	android/view/ViewTreeObserver$InternalInsetsInfo:contentInsets	Landroid/graphics/Rect;
    //   5593: astore 9
    //   5595: aload 14
    //   5597: getfield 2893	android/view/ViewTreeObserver$InternalInsetsInfo:visibleInsets	Landroid/graphics/Rect;
    //   5600: astore 13
    //   5602: aload 14
    //   5604: getfield 2899	android/view/ViewTreeObserver$InternalInsetsInfo:touchableRegion	Landroid/graphics/Region;
    //   5607: astore_3
    //   5608: aload_0
    //   5609: getfield 759	android/view/ViewRootImpl:mWindowSession	Landroid/view/IWindowSession;
    //   5612: astore 39
    //   5614: aload_0
    //   5615: getfield 803	android/view/ViewRootImpl:mWindow	Landroid/view/ViewRootImpl$W;
    //   5618: astore 15
    //   5620: aload 39
    //   5622: aload 15
    //   5624: aload 14
    //   5626: getfield 2906	android/view/ViewTreeObserver$InternalInsetsInfo:mTouchableInsets	I
    //   5629: aload 9
    //   5631: aload 13
    //   5633: aload_3
    //   5634: invokeinterface 2910 6 0
    //   5639: goto +11 -> 5650
    //   5642: astore_3
    //   5643: goto +7 -> 5650
    //   5646: astore_3
    //   5647: goto +3 -> 5650
    //   5650: aload_0
    //   5651: getfield 825	android/view/ViewRootImpl:mFirst	Z
    //   5654: ifeq +78 -> 5732
    //   5657: getstatic 948	android/view/ViewRootImpl:sAlwaysAssignFocus	Z
    //   5660: ifne +48 -> 5708
    //   5663: invokestatic 2912	android/view/ViewRootImpl:isInTouchMode	()Z
    //   5666: ifne +6 -> 5672
    //   5669: goto +39 -> 5708
    //   5672: aload_0
    //   5673: getfield 1150	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   5676: invokevirtual 1734	android/view/View:findFocus	()Landroid/view/View;
    //   5679: astore_3
    //   5680: aload_3
    //   5681: instanceof 1621
    //   5684: ifeq +48 -> 5732
    //   5687: aload_3
    //   5688: checkcast 1621	android/view/ViewGroup
    //   5691: invokevirtual 1755	android/view/ViewGroup:getDescendantFocusability	()I
    //   5694: ldc_w 1756
    //   5697: if_icmpne +35 -> 5732
    //   5700: aload_3
    //   5701: invokevirtual 2350	android/view/View:restoreDefaultFocus	()Z
    //   5704: pop
    //   5705: goto +27 -> 5732
    //   5708: aload_0
    //   5709: getfield 1150	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   5712: astore_3
    //   5713: aload_3
    //   5714: ifnull +18 -> 5732
    //   5717: aload_3
    //   5718: invokevirtual 1730	android/view/View:hasFocus	()Z
    //   5721: ifne +11 -> 5732
    //   5724: aload_0
    //   5725: getfield 1150	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   5728: invokevirtual 2350	android/view/View:restoreDefaultFocus	()Z
    //   5731: pop
    //   5732: iload 5
    //   5734: ifne +10 -> 5744
    //   5737: aload_0
    //   5738: getfield 825	android/view/ViewRootImpl:mFirst	Z
    //   5741: ifeq +14 -> 5755
    //   5744: iload 22
    //   5746: ifeq +9 -> 5755
    //   5749: iconst_1
    //   5750: istore 6
    //   5752: goto +6 -> 5758
    //   5755: iconst_0
    //   5756: istore 6
    //   5758: aload_0
    //   5759: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   5762: getfield 2231	android/view/View$AttachInfo:mHasWindowFocus	Z
    //   5765: ifeq +14 -> 5779
    //   5768: iload 22
    //   5770: ifeq +9 -> 5779
    //   5773: iconst_1
    //   5774: istore 12
    //   5776: goto +6 -> 5782
    //   5779: iconst_0
    //   5780: istore 12
    //   5782: iload 12
    //   5784: ifeq +16 -> 5800
    //   5787: aload_0
    //   5788: getfield 2914	android/view/ViewRootImpl:mLostWindowFocus	Z
    //   5791: ifeq +9 -> 5800
    //   5794: iconst_1
    //   5795: istore 8
    //   5797: goto +6 -> 5803
    //   5800: iconst_0
    //   5801: istore 8
    //   5803: iload 8
    //   5805: ifeq +11 -> 5816
    //   5808: aload_0
    //   5809: iconst_0
    //   5810: putfield 2914	android/view/ViewRootImpl:mLostWindowFocus	Z
    //   5813: goto +20 -> 5833
    //   5816: iload 12
    //   5818: ifne +15 -> 5833
    //   5821: aload_0
    //   5822: getfield 2916	android/view/ViewRootImpl:mHadWindowFocus	Z
    //   5825: ifeq +8 -> 5833
    //   5828: aload_0
    //   5829: iconst_1
    //   5830: putfield 2914	android/view/ViewRootImpl:mLostWindowFocus	Z
    //   5833: iload 6
    //   5835: ifne +8 -> 5843
    //   5838: iload 8
    //   5840: ifeq +48 -> 5888
    //   5843: aload_0
    //   5844: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   5847: astore_3
    //   5848: aload_3
    //   5849: ifnonnull +9 -> 5858
    //   5852: iconst_0
    //   5853: istore 6
    //   5855: goto +22 -> 5877
    //   5858: aload_3
    //   5859: getfield 2917	android/view/WindowManager$LayoutParams:type	I
    //   5862: sipush 2005
    //   5865: if_icmpne +9 -> 5874
    //   5868: iconst_1
    //   5869: istore 6
    //   5871: goto +6 -> 5877
    //   5874: iconst_0
    //   5875: istore 6
    //   5877: iload 6
    //   5879: ifne +9 -> 5888
    //   5882: aload_1
    //   5883: bipush 32
    //   5885: invokevirtual 1852	android/view/View:sendAccessibilityEvent	(I)V
    //   5888: aload_0
    //   5889: iconst_0
    //   5890: putfield 825	android/view/ViewRootImpl:mFirst	Z
    //   5893: aload_0
    //   5894: iconst_0
    //   5895: putfield 2290	android/view/ViewRootImpl:mWillDrawSoon	Z
    //   5898: aload_0
    //   5899: iconst_0
    //   5900: putfield 2596	android/view/ViewRootImpl:mNewSurfaceNeeded	Z
    //   5903: aload_0
    //   5904: iconst_0
    //   5905: putfield 2733	android/view/ViewRootImpl:mActivityRelaunched	Z
    //   5908: aload_0
    //   5909: iload 4
    //   5911: putfield 816	android/view/ViewRootImpl:mViewVisibility	I
    //   5914: aload_0
    //   5915: iload 12
    //   5917: putfield 2916	android/view/ViewRootImpl:mHadWindowFocus	Z
    //   5920: iload 12
    //   5922: ifeq +116 -> 6038
    //   5925: aload_0
    //   5926: invokespecial 1009	android/view/ViewRootImpl:isInLocalFocusMode	()Z
    //   5929: ifne +109 -> 6038
    //   5932: aload_0
    //   5933: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   5936: getfield 1114	android/view/WindowManager$LayoutParams:flags	I
    //   5939: invokestatic 2235	android/view/WindowManager$LayoutParams:mayUseInputMethod	(I)Z
    //   5942: istore 11
    //   5944: iload 11
    //   5946: aload_0
    //   5947: getfield 2237	android/view/ViewRootImpl:mLastWasImTarget	Z
    //   5950: if_icmpeq +85 -> 6035
    //   5953: aload_0
    //   5954: iload 11
    //   5956: putfield 2237	android/view/ViewRootImpl:mLastWasImTarget	Z
    //   5959: aload_0
    //   5960: getfield 751	android/view/ViewRootImpl:mContext	Landroid/content/Context;
    //   5963: ldc_w 2239
    //   5966: invokevirtual 1980	android/content/Context:getSystemService	(Ljava/lang/Class;)Ljava/lang/Object;
    //   5969: checkcast 2239	android/view/inputmethod/InputMethodManager
    //   5972: astore_3
    //   5973: aload_3
    //   5974: ifnull +58 -> 6032
    //   5977: iload 11
    //   5979: ifeq +53 -> 6032
    //   5982: aload_3
    //   5983: aload_0
    //   5984: getfield 1150	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   5987: iload 12
    //   5989: invokevirtual 2243	android/view/inputmethod/InputMethodManager:onPreWindowFocus	(Landroid/view/View;Z)V
    //   5992: aload_0
    //   5993: getfield 1150	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   5996: astore 9
    //   5998: aload_3
    //   5999: aload 9
    //   6001: aload 9
    //   6003: invokevirtual 1734	android/view/View:findFocus	()Landroid/view/View;
    //   6006: aload_0
    //   6007: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   6010: getfield 2267	android/view/WindowManager$LayoutParams:softInputMode	I
    //   6013: aload_0
    //   6014: getfield 2269	android/view/ViewRootImpl:mHasHadWindowFocus	Z
    //   6017: iconst_1
    //   6018: ixor
    //   6019: aload_0
    //   6020: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   6023: getfield 1114	android/view/WindowManager$LayoutParams:flags	I
    //   6026: invokevirtual 2273	android/view/inputmethod/InputMethodManager:onPostWindowFocus	(Landroid/view/View;Landroid/view/View;IZI)V
    //   6029: goto +9 -> 6038
    //   6032: goto +6 -> 6038
    //   6035: goto +3 -> 6038
    //   6038: iconst_1
    //   6039: istore 8
    //   6041: iload 7
    //   6043: iconst_2
    //   6044: iand
    //   6045: ifeq +7 -> 6052
    //   6048: aload_0
    //   6049: invokespecial 1090	android/view/ViewRootImpl:reportNextDraw	()V
    //   6052: iload 8
    //   6054: istore 6
    //   6056: aload_0
    //   6057: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   6060: getfield 1383	android/view/View$AttachInfo:mTreeObserver	Landroid/view/ViewTreeObserver;
    //   6063: invokevirtual 2920	android/view/ViewTreeObserver:dispatchOnPreDraw	()Z
    //   6066: ifne +18 -> 6084
    //   6069: iload 22
    //   6071: ifne +10 -> 6081
    //   6074: iload 8
    //   6076: istore 6
    //   6078: goto +6 -> 6084
    //   6081: iconst_0
    //   6082: istore 6
    //   6084: iload 6
    //   6086: ifne +69 -> 6155
    //   6089: aload_0
    //   6090: getfield 2922	android/view/ViewRootImpl:mPendingTransitions	Ljava/util/ArrayList;
    //   6093: astore_3
    //   6094: aload_3
    //   6095: ifnull +53 -> 6148
    //   6098: aload_3
    //   6099: invokevirtual 1286	java/util/ArrayList:size	()I
    //   6102: ifle +46 -> 6148
    //   6105: iconst_0
    //   6106: istore 6
    //   6108: iload 6
    //   6110: aload_0
    //   6111: getfield 2922	android/view/ViewRootImpl:mPendingTransitions	Ljava/util/ArrayList;
    //   6114: invokevirtual 1286	java/util/ArrayList:size	()I
    //   6117: if_icmpge +24 -> 6141
    //   6120: aload_0
    //   6121: getfield 2922	android/view/ViewRootImpl:mPendingTransitions	Ljava/util/ArrayList;
    //   6124: iload 6
    //   6126: invokevirtual 1289	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6129: checkcast 2924	android/animation/LayoutTransition
    //   6132: invokevirtual 2927	android/animation/LayoutTransition:startChangingAnimations	()V
    //   6135: iinc 6 1
    //   6138: goto -30 -> 6108
    //   6141: aload_0
    //   6142: getfield 2922	android/view/ViewRootImpl:mPendingTransitions	Ljava/util/ArrayList;
    //   6145: invokevirtual 2053	java/util/ArrayList:clear	()V
    //   6148: aload_0
    //   6149: invokespecial 2929	android/view/ViewRootImpl:performDraw	()V
    //   6152: goto +74 -> 6226
    //   6155: iload 22
    //   6157: ifeq +10 -> 6167
    //   6160: aload_0
    //   6161: invokevirtual 1531	android/view/ViewRootImpl:scheduleTraversals	()V
    //   6164: goto +62 -> 6226
    //   6167: aload_0
    //   6168: getfield 2922	android/view/ViewRootImpl:mPendingTransitions	Ljava/util/ArrayList;
    //   6171: astore_3
    //   6172: aload_3
    //   6173: ifnull +53 -> 6226
    //   6176: aload_3
    //   6177: invokevirtual 1286	java/util/ArrayList:size	()I
    //   6180: ifle +46 -> 6226
    //   6183: iconst_0
    //   6184: istore 6
    //   6186: iload 6
    //   6188: aload_0
    //   6189: getfield 2922	android/view/ViewRootImpl:mPendingTransitions	Ljava/util/ArrayList;
    //   6192: invokevirtual 1286	java/util/ArrayList:size	()I
    //   6195: if_icmpge +24 -> 6219
    //   6198: aload_0
    //   6199: getfield 2922	android/view/ViewRootImpl:mPendingTransitions	Ljava/util/ArrayList;
    //   6202: iload 6
    //   6204: invokevirtual 1289	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6207: checkcast 2924	android/animation/LayoutTransition
    //   6210: invokevirtual 2932	android/animation/LayoutTransition:endChangingAnimations	()V
    //   6213: iinc 6 1
    //   6216: goto -30 -> 6186
    //   6219: aload_0
    //   6220: getfield 2922	android/view/ViewRootImpl:mPendingTransitions	Ljava/util/ArrayList;
    //   6223: invokevirtual 2053	java/util/ArrayList:clear	()V
    //   6226: aload_0
    //   6227: iconst_0
    //   6228: putfield 2591	android/view/ViewRootImpl:mIsInTraversal	Z
    //   6231: return
    //   6232: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	6233	0	this	ViewRootImpl
    //   4	5879	1	localView	View
    //   30	1528	2	i	int
    //   1717	3459	2	j	int
    //   35	4740	3	localLayoutParams1	WindowManager.LayoutParams
    //   4872	399	3	localLayoutParams2	WindowManager.LayoutParams
    //   5305	69	3	localObject1	Object
    //   5432	1	3	localRemoteException1	RemoteException
    //   5448	186	3	localObject2	Object
    //   5642	1	3	localRemoteException2	RemoteException
    //   5646	1	3	localRemoteException3	RemoteException
    //   5679	498	3	localObject3	Object
    //   40	5870	4	k	int
    //   73	5660	5	m	int
    //   101	6113	6	n	int
    //   115	5930	7	i1	int
    //   131	5944	8	i2	int
    //   140	5862	9	localObject4	Object
    //   158	5043	10	i3	int
    //   179	5799	11	bool1	boolean
    //   185	5803	12	bool2	boolean
    //   261	1467	13	localObject5	Object
    //   1740	1	13	localRemoteException4	RemoteException
    //   1752	1	13	localRemoteException5	RemoteException
    //   1787	1	13	localRemoteException6	RemoteException
    //   1799	1	13	localRemoteException7	RemoteException
    //   1943	1	13	localRemoteException8	RemoteException
    //   2041	1214	13	localObject6	Object
    //   3331	1	13	localOutOfResourcesException1	Surface.OutOfResourcesException
    //   3339	11	13	localOutOfResourcesException2	Surface.OutOfResourcesException
    //   3355	1	13	localRemoteException9	RemoteException
    //   3805	7	13	localBaseSurfaceHolder	BaseSurfaceHolder
    //   3913	23	13	localOutOfResourcesException3	Surface.OutOfResourcesException
    //   4272	16	13	localRect	Rect
    //   4297	1	13	localRemoteException10	RemoteException
    //   4317	1	13	localRemoteException11	RemoteException
    //   4417	1	13	localRemoteException12	RemoteException
    //   4433	1	13	localRemoteException13	RemoteException
    //   4466	1	13	localRemoteException14	RemoteException
    //   4484	1	13	localRemoteException15	RemoteException
    //   4670	962	13	localObject7	Object
    //   290	5335	14	localObject8	Object
    //   306	5317	15	localObject9	Object
    //   447	4730	16	i4	int
    //   454	4704	17	i5	int
    //   639	4571	18	i6	int
    //   1062	3394	19	i7	int
    //   1456	4006	20	i8	int
    //   1476	3254	21	i9	int
    //   1484	4672	22	i10	int
    //   1496	2273	23	bool3	boolean
    //   1648	903	24	i11	int
    //   1651	941	25	i12	int
    //   1657	849	26	i13	int
    //   1666	2527	27	i14	int
    //   1675	3140	28	bool4	boolean
    //   1823	814	29	i15	int
    //   1826	2458	30	bool5	boolean
    //   1829	1966	31	bool6	boolean
    //   1835	2517	32	bool7	boolean
    //   1851	2589	33	i16	int
    //   1855	2589	34	i17	int
    //   1863	2589	35	bool8	boolean
    //   2461	1317	36	bool9	boolean
    //   2757	35	37	bool10	boolean
    //   2785	7	38	bool11	boolean
    //   5612	9	39	localIWindowSession	IWindowSession
    // Exception table:
    //   from	to	target	type
    //   1721	1727	1740	android/os/RemoteException
    //   1713	1718	1752	android/os/RemoteException
    //   1727	1737	1781	android/os/RemoteException
    //   1767	1778	1781	android/os/RemoteException
    //   1691	1710	1799	android/os/RemoteException
    //   1899	1909	1943	android/os/RemoteException
    //   1929	1940	1943	android/os/RemoteException
    //   2037	2043	1943	android/os/RemoteException
    //   2063	2070	1943	android/os/RemoteException
    //   2099	2108	1943	android/os/RemoteException
    //   2487	2501	1943	android/os/RemoteException
    //   2529	2543	1943	android/os/RemoteException
    //   2570	2584	1943	android/os/RemoteException
    //   2615	2629	1943	android/os/RemoteException
    //   2656	2667	1943	android/os/RemoteException
    //   2694	2708	1943	android/os/RemoteException
    //   2727	2734	1943	android/os/RemoteException
    //   2753	2759	1943	android/os/RemoteException
    //   2778	2787	1943	android/os/RemoteException
    //   2973	2987	1943	android/os/RemoteException
    //   3011	3021	1943	android/os/RemoteException
    //   3040	3049	1943	android/os/RemoteException
    //   3068	3076	1943	android/os/RemoteException
    //   3104	3111	1943	android/os/RemoteException
    //   3139	3149	1943	android/os/RemoteException
    //   3168	3173	1943	android/os/RemoteException
    //   3192	3199	1943	android/os/RemoteException
    //   3218	3222	1943	android/os/RemoteException
    //   3241	3250	1943	android/os/RemoteException
    //   3278	3294	1943	android/os/RemoteException
    //   3431	3438	1943	android/os/RemoteException
    //   3457	3464	1943	android/os/RemoteException
    //   3483	3488	1943	android/os/RemoteException
    //   3507	3512	1943	android/os/RemoteException
    //   3531	3541	1943	android/os/RemoteException
    //   3560	3576	1943	android/os/RemoteException
    //   3595	3602	1943	android/os/RemoteException
    //   3621	3628	1943	android/os/RemoteException
    //   3651	3661	1943	android/os/RemoteException
    //   3684	3697	1943	android/os/RemoteException
    //   3716	3726	1943	android/os/RemoteException
    //   3839	3849	1943	android/os/RemoteException
    //   3868	3873	1943	android/os/RemoteException
    //   3892	3906	1943	android/os/RemoteException
    //   3934	3940	1943	android/os/RemoteException
    //   4151	4160	1943	android/os/RemoteException
    //   4179	4188	1943	android/os/RemoteException
    //   3303	3314	3331	android/view/Surface$OutOfResourcesException
    //   3318	3328	3331	android/view/Surface$OutOfResourcesException
    //   3278	3294	3339	android/view/Surface$OutOfResourcesException
    //   3303	3314	3355	android/os/RemoteException
    //   3318	3328	3355	android/os/RemoteException
    //   3348	3354	3355	android/os/RemoteException
    //   3892	3906	3913	android/view/Surface$OutOfResourcesException
    //   4274	4280	4297	android/os/RemoteException
    //   4268	4274	4317	android/os/RemoteException
    //   4280	4294	4413	android/os/RemoteException
    //   4337	4341	4413	android/os/RemoteException
    //   4344	4351	4413	android/os/RemoteException
    //   4356	4378	4413	android/os/RemoteException
    //   4381	4391	4413	android/os/RemoteException
    //   1868	1874	4433	android/os/RemoteException
    //   1995	2008	4433	android/os/RemoteException
    //   2130	2147	4433	android/os/RemoteException
    //   2175	2192	4433	android/os/RemoteException
    //   2220	2237	4433	android/os/RemoteException
    //   2265	2282	4433	android/os/RemoteException
    //   2310	2327	4433	android/os/RemoteException
    //   2355	2371	4433	android/os/RemoteException
    //   2411	2425	4433	android/os/RemoteException
    //   2453	2463	4433	android/os/RemoteException
    //   2824	2835	4433	android/os/RemoteException
    //   2854	2865	4433	android/os/RemoteException
    //   2884	2898	4433	android/os/RemoteException
    //   2917	2922	4433	android/os/RemoteException
    //   2941	2946	4433	android/os/RemoteException
    //   3398	3407	4433	android/os/RemoteException
    //   3752	3764	4433	android/os/RemoteException
    //   3801	3807	4433	android/os/RemoteException
    //   4016	4025	4433	android/os/RemoteException
    //   4063	4069	4433	android/os/RemoteException
    //   4088	4097	4433	android/os/RemoteException
    //   4116	4125	4433	android/os/RemoteException
    //   4223	4229	4433	android/os/RemoteException
    //   4262	4268	4433	android/os/RemoteException
    //   1837	1849	4466	android/os/RemoteException
    //   1677	1686	4484	android/os/RemoteException
    //   4833	4856	4872	finally
    //   5412	5429	5432	android/os/RemoteException
    //   5620	5639	5642	android/os/RemoteException
    //   5608	5620	5646	android/os/RemoteException
  }
  
  private void postDrawFinished()
  {
    this.mHandler.sendEmptyMessage(29);
  }
  
  private void postSendWindowContentChangedCallback(View paramView, int paramInt)
  {
    if (this.mSendWindowContentChangedAccessibilityEvent == null) {
      this.mSendWindowContentChangedAccessibilityEvent = new SendWindowContentChangedAccessibilityEvent(null);
    }
    this.mSendWindowContentChangedAccessibilityEvent.runOrPost(paramView, paramInt);
  }
  
  private void profileRendering(boolean paramBoolean)
  {
    if (this.mProfileRendering)
    {
      this.mRenderProfilingEnabled = paramBoolean;
      Choreographer.FrameCallback localFrameCallback = this.mRenderProfiler;
      if (localFrameCallback != null) {
        this.mChoreographer.removeFrameCallback(localFrameCallback);
      }
      if (this.mRenderProfilingEnabled)
      {
        if (this.mRenderProfiler == null) {
          this.mRenderProfiler = new Choreographer.FrameCallback()
          {
            public void doFrame(long paramAnonymousLong)
            {
              ViewRootImpl.this.mDirty.set(0, 0, ViewRootImpl.this.mWidth, ViewRootImpl.this.mHeight);
              ViewRootImpl.this.scheduleTraversals();
              if (ViewRootImpl.this.mRenderProfilingEnabled) {
                ViewRootImpl.this.mChoreographer.postFrameCallback(ViewRootImpl.this.mRenderProfiler);
              }
            }
          };
        }
        this.mChoreographer.postFrameCallback(this.mRenderProfiler);
      }
      else
      {
        this.mRenderProfiler = null;
      }
    }
  }
  
  private void recycleQueuedInputEvent(QueuedInputEvent paramQueuedInputEvent)
  {
    paramQueuedInputEvent.mEvent = null;
    paramQueuedInputEvent.mReceiver = null;
    int i = this.mQueuedInputEventPoolSize;
    if (i < 10)
    {
      this.mQueuedInputEventPoolSize = (i + 1);
      paramQueuedInputEvent.mNext = this.mQueuedInputEventPool;
      this.mQueuedInputEventPool = paramQueuedInputEvent;
    }
  }
  
  private int relayoutWindow(WindowManager.LayoutParams paramLayoutParams, int paramInt, boolean paramBoolean)
    throws RemoteException
  {
    float f = this.mAttachInfo.mApplicationScale;
    int i;
    if ((paramLayoutParams != null) && (this.mTranslator != null))
    {
      paramLayoutParams.backup();
      this.mTranslator.translateWindowLayout(paramLayoutParams);
      i = 1;
    }
    else
    {
      i = 0;
    }
    if ((paramLayoutParams != null) && (this.mOrigWindowType != paramLayoutParams.type) && (this.mTargetSdkVersion < 14))
    {
      String str = this.mTag;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Window type can not be changed after the window is added; ignoring change of ");
      localStringBuilder.append(this.mView);
      Slog.w(str, localStringBuilder.toString());
      paramLayoutParams.type = this.mOrigWindowType;
    }
    long l;
    if (this.mSurface.isValid()) {
      l = this.mSurface.getNextFrameNumber();
    } else {
      l = -1L;
    }
    if (paramLayoutParams != null) {
      if (this.mIsCastMode) {
        paramLayoutParams.extraFlags |= 0x2000000;
      } else {
        paramLayoutParams.extraFlags &= 0xFDFFFFFF;
      }
    }
    paramInt = this.mWindowSession.relayout(this.mWindow, this.mSeq, paramLayoutParams, (int)(this.mView.getMeasuredWidth() * f + 0.5F), (int)(this.mView.getMeasuredHeight() * f + 0.5F), paramInt, paramBoolean, l, this.mTmpFrame, this.mPendingOverscanInsets, this.mPendingContentInsets, this.mPendingVisibleInsets, this.mPendingStableInsets, this.mPendingOutsets, this.mPendingBackDropFrame, this.mPendingDisplayCutout, this.mPendingMergedConfiguration, this.mSurfaceControl, this.mTempInsets);
    if (this.mSurfaceControl.isValid()) {
      this.mSurface.copyFrom(this.mSurfaceControl);
    } else {
      destroySurface();
    }
    boolean bool;
    if ((paramInt & 0x40) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    this.mPendingAlwaysConsumeSystemBars = bool;
    if (i != 0) {
      paramLayoutParams.restore();
    }
    paramLayoutParams = this.mTranslator;
    if (paramLayoutParams != null)
    {
      paramLayoutParams.translateRectInScreenToAppWinFrame(this.mTmpFrame);
      this.mTranslator.translateRectInScreenToAppWindow(this.mPendingOverscanInsets);
      this.mTranslator.translateRectInScreenToAppWindow(this.mPendingContentInsets);
      this.mTranslator.translateRectInScreenToAppWindow(this.mPendingVisibleInsets);
      this.mTranslator.translateRectInScreenToAppWindow(this.mPendingStableInsets);
    }
    setFrame(this.mTmpFrame);
    this.mInsetsController.onStateChanged(this.mTempInsets);
    return paramInt;
  }
  
  private void removeSendWindowContentChangedCallback()
  {
    SendWindowContentChangedAccessibilityEvent localSendWindowContentChangedAccessibilityEvent = this.mSendWindowContentChangedAccessibilityEvent;
    if (localSendWindowContentChangedAccessibilityEvent != null) {
      this.mHandler.removeCallbacks(localSendWindowContentChangedAccessibilityEvent);
    }
  }
  
  private void reportDrawFinished()
  {
    try
    {
      this.mDrawsNeededToReport = 0;
      this.mWindowSession.finishDrawing(this.mWindow);
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void reportNextDraw()
  {
    if (!this.mReportNextDraw) {
      drawPending();
    }
    this.mReportNextDraw = true;
  }
  
  private void requestDrawWindow()
  {
    if (!this.mUseMTRenderer) {
      return;
    }
    this.mWindowDrawCountDown = new CountDownLatch(this.mWindowCallbacks.size());
    for (int i = this.mWindowCallbacks.size() - 1; i >= 0; i--) {
      ((WindowCallbacks)this.mWindowCallbacks.get(i)).onRequestDraw(this.mReportNextDraw);
    }
  }
  
  private void resetPointerIcon(MotionEvent paramMotionEvent)
  {
    this.mPointerIconType = 1;
    updatePointerIcon(paramMotionEvent);
  }
  
  private void scheduleProcessInputEvents()
  {
    if (!this.mProcessInputEventsScheduled)
    {
      this.mProcessInputEventsScheduled = true;
      Message localMessage = this.mHandler.obtainMessage(19);
      localMessage.setAsynchronous(true);
      this.mHandler.sendMessage(localMessage);
    }
  }
  
  private void setBoundsSurfaceCrop()
  {
    this.mTempBoundsRect.set(this.mWinFrame);
    this.mTempBoundsRect.offsetTo(this.mWindowAttributes.surfaceInsets.left, this.mWindowAttributes.surfaceInsets.top);
    this.mTransaction.setWindowCrop(this.mBoundsSurfaceControl, this.mTempBoundsRect);
  }
  
  private void setFrame(Rect paramRect)
  {
    this.mWinFrame.set(paramRect);
    this.mInsetsController.onFrameChanged(paramRect);
  }
  
  private void setTag()
  {
    String[] arrayOfString = this.mWindowAttributes.getTitle().toString().split("\\.");
    if (arrayOfString.length > 0)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("ViewRootImpl[");
      localStringBuilder.append(arrayOfString[(arrayOfString.length - 1)]);
      localStringBuilder.append("]");
      this.mTag = localStringBuilder.toString();
    }
  }
  
  private static boolean shouldUseDisplaySize(WindowManager.LayoutParams paramLayoutParams)
  {
    boolean bool;
    if ((paramLayoutParams.type != 2014) && (paramLayoutParams.type != 2011) && (paramLayoutParams.type != 2020)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private void startDragResizing(Rect paramRect1, boolean paramBoolean, Rect paramRect2, Rect paramRect3, int paramInt)
  {
    if (!this.mDragResizing)
    {
      this.mDragResizing = true;
      if (this.mUseMTRenderer) {
        for (int i = this.mWindowCallbacks.size() - 1; i >= 0; i--) {
          ((WindowCallbacks)this.mWindowCallbacks.get(i)).onWindowDragResizeStart(paramRect1, paramBoolean, paramRect2, paramRect3, paramInt);
        }
      }
      this.mFullRedrawNeeded = true;
    }
  }
  
  private void trackFPS()
  {
    long l1 = System.currentTimeMillis();
    if (this.mFpsStartTime < 0L)
    {
      this.mFpsPrevTime = l1;
      this.mFpsStartTime = l1;
      this.mFpsNumFrames = 0;
    }
    else
    {
      this.mFpsNumFrames += 1;
      String str = Integer.toHexString(System.identityHashCode(this));
      long l2 = this.mFpsPrevTime;
      long l3 = l1 - this.mFpsStartTime;
      Object localObject1 = this.mTag;
      Object localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("0x");
      ((StringBuilder)localObject2).append(str);
      ((StringBuilder)localObject2).append("\tFrame time:\t");
      ((StringBuilder)localObject2).append(l1 - l2);
      Log.v((String)localObject1, ((StringBuilder)localObject2).toString());
      this.mFpsPrevTime = l1;
      if (l3 > 1000L)
      {
        float f = this.mFpsNumFrames * 1000.0F / (float)l3;
        localObject2 = this.mTag;
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("0x");
        ((StringBuilder)localObject1).append(str);
        ((StringBuilder)localObject1).append("\tFPS:\t");
        ((StringBuilder)localObject1).append(f);
        Log.v((String)localObject2, ((StringBuilder)localObject1).toString());
        this.mFpsStartTime = l1;
        this.mFpsNumFrames = 0;
      }
    }
  }
  
  private void updateBoundsSurface()
  {
    if ((this.mBoundsSurfaceControl != null) && (this.mSurface.isValid()))
    {
      setBoundsSurfaceCrop();
      SurfaceControl.Transaction localTransaction = this.mTransaction;
      SurfaceControl localSurfaceControl = this.mBoundsSurfaceControl;
      Surface localSurface = this.mSurface;
      localTransaction.deferTransactionUntilSurface(localSurfaceControl, localSurface, localSurface.getNextFrameNumber()).apply();
    }
  }
  
  private boolean updateContentDrawBounds()
  {
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = this.mUseMTRenderer;
    boolean bool4 = true;
    if (bool3) {
      for (int i = this.mWindowCallbacks.size() - 1;; i--)
      {
        bool1 = bool2;
        if (i < 0) {
          break;
        }
        bool2 |= ((WindowCallbacks)this.mWindowCallbacks.get(i)).onContentDrawn(this.mWindowAttributes.surfaceInsets.left, this.mWindowAttributes.surfaceInsets.top, this.mWidth, this.mHeight);
      }
    }
    if ((this.mDragResizing) && (this.mReportNextDraw)) {
      bool2 = bool4;
    } else {
      bool2 = false;
    }
    return bool1 | bool2;
  }
  
  private void updateForceDarkMode()
  {
    if (this.mAttachInfo.mThreadedRenderer == null) {
      return;
    }
    int i = getNightMode();
    boolean bool1 = true;
    boolean bool2;
    if (i == 32) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    boolean bool3 = bool2;
    if (bool2)
    {
      bool2 = SystemProperties.getBoolean("debug.hwui.force_dark", false);
      TypedArray localTypedArray = this.mContext.obtainStyledAttributes(R.styleable.Theme);
      if ((localTypedArray.getBoolean(279, true)) && (localTypedArray.getBoolean(278, bool2))) {
        bool2 = bool1;
      } else {
        bool2 = false;
      }
      localTypedArray.recycle();
      bool3 = bool2;
    }
    if (this.mAttachInfo.mThreadedRenderer.setForceDark(bool3)) {
      invalidateWorld(this.mView);
    }
  }
  
  private void updateInternalDisplay(int paramInt, Resources paramResources)
  {
    Object localObject = ResourcesManager.getInstance().getAdjustedDisplay(paramInt, paramResources);
    if (localObject == null)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Cannot get desired display with Id: ");
      ((StringBuilder)localObject).append(paramInt);
      Slog.w("ViewRootImpl", ((StringBuilder)localObject).toString());
      this.mDisplay = ResourcesManager.getInstance().getAdjustedDisplay(0, paramResources);
    }
    else
    {
      this.mDisplay = ((Display)localObject);
    }
    this.mContext.updateDisplay(this.mDisplay.getDisplayId());
  }
  
  private boolean updatePointerIcon(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX(0);
    float f2 = paramMotionEvent.getY(0);
    View localView = this.mView;
    if (localView == null)
    {
      Slog.d(this.mTag, "updatePointerIcon called after view was removed");
      return false;
    }
    if ((f1 >= 0.0F) && (f1 < localView.getWidth()) && (f2 >= 0.0F) && (f2 < this.mView.getHeight()))
    {
      paramMotionEvent = this.mView.onResolvePointerIcon(paramMotionEvent, 0);
      int i;
      if (paramMotionEvent != null) {
        i = paramMotionEvent.getType();
      } else {
        i = 1000;
      }
      if (this.mPointerIconType != i)
      {
        this.mPointerIconType = i;
        this.mCustomPointerIcon = null;
        if (this.mPointerIconType != -1)
        {
          InputManager.getInstance().setPointerIconType(i);
          return true;
        }
      }
      if ((this.mPointerIconType == -1) && (!paramMotionEvent.equals(this.mCustomPointerIcon)))
      {
        this.mCustomPointerIcon = paramMotionEvent;
        InputManager.getInstance().setCustomPointerIcon(this.mCustomPointerIcon);
      }
      return true;
    }
    Slog.d(this.mTag, "updatePointerIcon called with position out of bounds");
    return false;
  }
  
  void addCastProjectionCallback(CastProjectionCallback paramCastProjectionCallback)
  {
    mCastProjectionCallbacks.add(paramCastProjectionCallback);
  }
  
  public void addWindowCallbacks(WindowCallbacks paramWindowCallbacks)
  {
    synchronized (this.mWindowCallbacks)
    {
      this.mWindowCallbacks.add(paramWindowCallbacks);
      return;
    }
  }
  
  void addWindowStoppedCallback(WindowStoppedCallback paramWindowStoppedCallback)
  {
    this.mWindowStoppedCallbacks.add(paramWindowStoppedCallback);
  }
  
  public void bringChildToFront(View paramView) {}
  
  public boolean canResolveLayoutDirection()
  {
    return true;
  }
  
  public boolean canResolveTextAlignment()
  {
    return true;
  }
  
  public boolean canResolveTextDirection()
  {
    return true;
  }
  
  @UnsupportedAppUsage
  public void cancelInvalidate(View paramView)
  {
    this.mHandler.removeMessages(1, paramView);
    this.mHandler.removeMessages(2, paramView);
    this.mInvalidateOnAnimationRunnable.removeView(paramView);
  }
  
  void changeCanvasOpacity(boolean paramBoolean)
  {
    String str = this.mTag;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("changeCanvasOpacity: opaque=");
    localStringBuilder.append(paramBoolean);
    Log.d(str, localStringBuilder.toString());
    boolean bool;
    if ((this.mView.mPrivateFlags & 0x200) == 0) {
      bool = true;
    } else {
      bool = false;
    }
    if (this.mAttachInfo.mThreadedRenderer != null) {
      this.mAttachInfo.mThreadedRenderer.setOpaque(paramBoolean & bool);
    }
  }
  
  void checkThread()
  {
    if (this.mThread == Thread.currentThread()) {
      return;
    }
    throw new CalledFromWrongThreadException("Only the original thread that created a view hierarchy can touch its views.");
  }
  
  public void childDrawableStateChanged(View paramView) {}
  
  public void childHasTransientStateChanged(View paramView, boolean paramBoolean) {}
  
  public void clearChildFocus(View paramView)
  {
    checkThread();
    scheduleTraversals();
  }
  
  public void createBoundsSurface(int paramInt)
  {
    if (this.mSurfaceSession == null) {
      this.mSurfaceSession = new SurfaceSession();
    }
    if ((this.mBoundsSurfaceControl != null) && (this.mBoundsSurface.isValid())) {
      return;
    }
    SurfaceControl.Builder localBuilder = new SurfaceControl.Builder(this.mSurfaceSession);
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Bounds for - ");
    localStringBuilder.append(getTitle().toString());
    this.mBoundsSurfaceControl = localBuilder.setName(localStringBuilder.toString()).setParent(this.mSurfaceControl).build();
    setBoundsSurfaceCrop();
    this.mTransaction.setLayer(this.mBoundsSurfaceControl, paramInt).show(this.mBoundsSurfaceControl).apply();
    this.mBoundsSurface.copyFrom(this.mBoundsSurfaceControl);
  }
  
  public void createContextMenu(ContextMenu paramContextMenu) {}
  
  public void debug()
  {
    this.mView.debug();
  }
  
  void destroyHardwareResources()
  {
    ThreadedRenderer localThreadedRenderer = this.mAttachInfo.mThreadedRenderer;
    if (localThreadedRenderer != null)
    {
      if (Looper.myLooper() != this.mAttachInfo.mHandler.getLooper())
      {
        this.mAttachInfo.mHandler.postAtFrontOfQueue(new _..Lambda.dj1hfDQd0iEp_uBDBPEUMMYJJwk(this));
        return;
      }
      localThreadedRenderer.destroyHardwareResources(this.mView);
      localThreadedRenderer.destroy();
    }
  }
  
  @UnsupportedAppUsage
  public void detachFunctor(long paramLong)
  {
    if (this.mAttachInfo.mThreadedRenderer != null) {
      this.mAttachInfo.mThreadedRenderer.stopDrawing();
    }
  }
  
  boolean die(boolean paramBoolean)
  {
    if ((paramBoolean) && (!this.mIsInTraversal))
    {
      doDie();
      return false;
    }
    if (!this.mIsDrawing)
    {
      destroyHardwareRenderer();
    }
    else
    {
      String str = this.mTag;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Attempting to destroy the window while drawing!\n  window=");
      localStringBuilder.append(this);
      localStringBuilder.append(", title=");
      localStringBuilder.append(this.mWindowAttributes.getTitle());
      Log.e(str, localStringBuilder.toString());
    }
    this.mHandler.sendEmptyMessage(3);
    return true;
  }
  
  public void dispatchAppVisibility(boolean paramBoolean)
  {
    Message localMessage = this.mHandler.obtainMessage(8);
    localMessage.arg1 = paramBoolean;
    this.mHandler.sendMessage(localMessage);
  }
  
  void dispatchApplyInsets(View paramView)
  {
    Trace.traceBegin(8L, "dispatchApplyInsets");
    int i = 1;
    WindowInsets localWindowInsets1 = getWindowInsets(true);
    if (this.mWindowAttributes.layoutInDisplayCutoutMode != 1) {
      i = 0;
    }
    WindowInsets localWindowInsets2 = localWindowInsets1;
    if (i == 0) {
      localWindowInsets2 = localWindowInsets1.consumeDisplayCutout();
    }
    paramView.dispatchApplyWindowInsets(localWindowInsets2);
    Trace.traceEnd(8L);
  }
  
  public void dispatchCheckFocus()
  {
    if (!this.mHandler.hasMessages(13)) {
      this.mHandler.sendEmptyMessage(13);
    }
  }
  
  public void dispatchCloseSystemDialogs(String paramString)
  {
    Message localMessage = Message.obtain();
    localMessage.what = 14;
    localMessage.obj = paramString;
    this.mHandler.sendMessage(localMessage);
  }
  
  void dispatchDetachedFromWindow()
  {
    Object localObject = this.mFirstInputStage;
    if (localObject != null) {
      ((InputStage)localObject).onDetachedFromWindow();
    }
    localObject = this.mView;
    if ((localObject != null) && (((View)localObject).mAttachInfo != null))
    {
      this.mAttachInfo.mTreeObserver.dispatchOnWindowAttachedChange(false);
      this.mView.dispatchDetachedFromWindow();
    }
    this.mAccessibilityInteractionConnectionManager.ensureNoConnection();
    this.mAccessibilityManager.removeAccessibilityStateChangeListener(this.mAccessibilityInteractionConnectionManager);
    this.mAccessibilityManager.removeHighTextContrastStateChangeListener(this.mHighContrastTextManager);
    removeSendWindowContentChangedCallback();
    destroyHardwareRenderer();
    setAccessibilityFocus(null, null);
    this.mView.assignParent(null);
    this.mView = null;
    this.mAttachInfo.mRootView = null;
    destroySurface();
    localObject = this.mInputQueueCallback;
    if (localObject != null)
    {
      InputQueue localInputQueue = this.mInputQueue;
      if (localInputQueue != null)
      {
        ((InputQueue.Callback)localObject).onInputQueueDestroyed(localInputQueue);
        this.mInputQueue.dispose();
        this.mInputQueueCallback = null;
        this.mInputQueue = null;
      }
    }
    localObject = this.mInputEventReceiver;
    if (localObject != null)
    {
      ((WindowInputEventReceiver)localObject).dispose();
      this.mInputEventReceiver = null;
    }
    try
    {
      this.mWindowSession.remove(this.mWindow);
    }
    catch (RemoteException localRemoteException) {}
    InputChannel localInputChannel = this.mInputChannel;
    if (localInputChannel != null)
    {
      localInputChannel.dispose();
      this.mInputChannel = null;
    }
    this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
    unscheduleTraversals();
  }
  
  public void dispatchDragEvent(DragEvent paramDragEvent)
  {
    int i;
    if (paramDragEvent.getAction() == 2)
    {
      i = 16;
      this.mHandler.removeMessages(16);
    }
    else
    {
      i = 15;
    }
    paramDragEvent = this.mHandler.obtainMessage(i, paramDragEvent);
    this.mHandler.sendMessage(paramDragEvent);
  }
  
  public void dispatchGetNewSurface()
  {
    Message localMessage = this.mHandler.obtainMessage(9);
    this.mHandler.sendMessage(localMessage);
  }
  
  @UnsupportedAppUsage
  public void dispatchInputEvent(InputEvent paramInputEvent)
  {
    dispatchInputEvent(paramInputEvent, null);
  }
  
  @UnsupportedAppUsage
  public void dispatchInputEvent(InputEvent paramInputEvent, InputEventReceiver paramInputEventReceiver)
  {
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.arg1 = paramInputEvent;
    localSomeArgs.arg2 = paramInputEventReceiver;
    paramInputEvent = this.mHandler.obtainMessage(7, localSomeArgs);
    paramInputEvent.setAsynchronous(true);
    this.mHandler.sendMessage(paramInputEvent);
  }
  
  public void dispatchInvalidateDelayed(View paramView, long paramLong)
  {
    paramView = this.mHandler.obtainMessage(1, paramView);
    this.mHandler.sendMessageDelayed(paramView, paramLong);
  }
  
  public void dispatchInvalidateOnAnimation(View paramView)
  {
    this.mInvalidateOnAnimationRunnable.addView(paramView);
  }
  
  public void dispatchInvalidateRectDelayed(View.AttachInfo.InvalidateInfo paramInvalidateInfo, long paramLong)
  {
    paramInvalidateInfo = this.mHandler.obtainMessage(2, paramInvalidateInfo);
    this.mHandler.sendMessageDelayed(paramInvalidateInfo, paramLong);
  }
  
  public void dispatchInvalidateRectOnAnimation(View.AttachInfo.InvalidateInfo paramInvalidateInfo)
  {
    this.mInvalidateOnAnimationRunnable.addViewRect(paramInvalidateInfo);
  }
  
  public final void dispatchKeyEventToContentCatcher(KeyEvent paramKeyEvent)
  {
    Activity localActivity = this.mView.getAttachedActivityInstance();
    if ((localActivity != null) && (localActivity.getInterceptor() != null)) {
      localActivity.getInterceptor().dispatchKeyEvent(paramKeyEvent, this.mView, localActivity);
    }
  }
  
  public void dispatchKeyFromAutofill(KeyEvent paramKeyEvent)
  {
    paramKeyEvent = this.mHandler.obtainMessage(12, paramKeyEvent);
    paramKeyEvent.setAsynchronous(true);
    this.mHandler.sendMessage(paramKeyEvent);
  }
  
  @UnsupportedAppUsage
  public void dispatchKeyFromIme(KeyEvent paramKeyEvent)
  {
    paramKeyEvent = this.mHandler.obtainMessage(11, paramKeyEvent);
    paramKeyEvent.setAsynchronous(true);
    this.mHandler.sendMessage(paramKeyEvent);
  }
  
  public void dispatchMoved(int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    int j = paramInt2;
    if (this.mTranslator != null)
    {
      localObject = new PointF(paramInt1, paramInt2);
      this.mTranslator.translatePointInScreenToAppWindow((PointF)localObject);
      i = (int)(((PointF)localObject).x + 0.5D);
      j = (int)(((PointF)localObject).y + 0.5D);
    }
    Object localObject = this.mHandler.obtainMessage(23, i, j);
    this.mHandler.sendMessage((Message)localObject);
  }
  
  public void dispatchPointerCaptureChanged(boolean paramBoolean)
  {
    this.mHandler.removeMessages(28);
    Message localMessage = this.mHandler.obtainMessage(28);
    localMessage.arg1 = paramBoolean;
    this.mHandler.sendMessage(localMessage);
  }
  
  public void dispatchRequestKeyboardShortcuts(IResultReceiver paramIResultReceiver, int paramInt)
  {
    this.mHandler.obtainMessage(26, paramInt, 0, paramIResultReceiver).sendToTarget();
  }
  
  public void dispatchSystemUiVisibilityChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    SystemUiVisibilityInfo localSystemUiVisibilityInfo = new SystemUiVisibilityInfo();
    localSystemUiVisibilityInfo.seq = paramInt1;
    localSystemUiVisibilityInfo.globalVisibility = paramInt2;
    localSystemUiVisibilityInfo.localValue = paramInt3;
    localSystemUiVisibilityInfo.localChanges = paramInt4;
    ViewRootHandler localViewRootHandler = this.mHandler;
    localViewRootHandler.sendMessage(localViewRootHandler.obtainMessage(17, localSystemUiVisibilityInfo));
  }
  
  @UnsupportedAppUsage
  public void dispatchUnhandledInputEvent(InputEvent paramInputEvent)
  {
    Object localObject = paramInputEvent;
    if ((paramInputEvent instanceof MotionEvent)) {
      localObject = MotionEvent.obtain((MotionEvent)paramInputEvent);
    }
    synthesizeInputEvent((InputEvent)localObject);
  }
  
  public boolean dispatchUnhandledKeyEvent(KeyEvent paramKeyEvent)
  {
    return this.mUnhandledKeyManager.dispatch(this.mView, paramKeyEvent);
  }
  
  public void dispatchWindowShown()
  {
    this.mHandler.sendEmptyMessage(25);
  }
  
  void doConsumeBatchedInput(long paramLong)
  {
    if (this.mConsumeBatchedInputScheduled)
    {
      this.mConsumeBatchedInputScheduled = false;
      WindowInputEventReceiver localWindowInputEventReceiver = this.mInputEventReceiver;
      if ((localWindowInputEventReceiver != null) && (localWindowInputEventReceiver.consumeBatchedInputEvents(paramLong)) && (paramLong != -1L)) {
        scheduleConsumeBatchedInput();
      }
      doProcessInputEvents();
    }
  }
  
  void doDie()
  {
    checkThread();
    try
    {
      if (this.mRemoved) {
        return;
      }
      int i = 1;
      this.mRemoved = true;
      if (this.mAdded) {
        dispatchDetachedFromWindow();
      }
      if ((this.mAdded) && (!this.mFirst))
      {
        destroyHardwareRenderer();
        if (this.mView != null)
        {
          int j = this.mView.getVisibility();
          if (this.mViewVisibility == j) {
            i = 0;
          }
          boolean bool = this.mWindowAttributesChanged;
          if ((bool) || (i != 0)) {
            try
            {
              if ((relayoutWindow(this.mWindowAttributes, j, false) & 0x2) != 0) {
                this.mWindowSession.finishDrawing(this.mWindow);
              }
            }
            catch (RemoteException localRemoteException) {}
          }
          destroySurface();
        }
      }
      this.mAdded = false;
      WindowManagerGlobal.getInstance().doRemoveView(this);
      return;
    }
    finally {}
  }
  
  void doProcessInputEvents()
  {
    while (this.mPendingInputEventHead != null)
    {
      QueuedInputEvent localQueuedInputEvent = this.mPendingInputEventHead;
      this.mPendingInputEventHead = localQueuedInputEvent.mNext;
      if (this.mPendingInputEventHead == null) {
        this.mPendingInputEventTail = null;
      }
      localQueuedInputEvent.mNext = null;
      this.mPendingInputEventCount -= 1;
      Trace.traceCounter(4L, this.mPendingInputEventQueueLengthCounterName, this.mPendingInputEventCount);
      long l1 = localQueuedInputEvent.mEvent.getEventTimeNano();
      long l2 = l1;
      long l3 = l2;
      if ((localQueuedInputEvent.mEvent instanceof MotionEvent))
      {
        MotionEvent localMotionEvent = (MotionEvent)localQueuedInputEvent.mEvent;
        l3 = l2;
        if (localMotionEvent.getHistorySize() > 0) {
          l3 = localMotionEvent.getHistoricalEventTimeNano(0);
        }
      }
      this.mChoreographer.mFrameInfo.updateInputEventTime(l1, l3);
      deliverInputEvent(localQueuedInputEvent);
    }
    if (this.mProcessInputEventsScheduled)
    {
      this.mProcessInputEventsScheduled = false;
      this.mHandler.removeMessages(19);
    }
  }
  
  void doTraversal()
  {
    if (this.mTraversalScheduled)
    {
      this.mTraversalScheduled = false;
      this.mHandler.getLooper().getQueue().removeSyncBarrier(this.mTraversalBarrier);
      if (this.mProfile) {
        Debug.startMethodTracing("ViewAncestor");
      }
      performTraversals();
      if (this.mProfile)
      {
        Debug.stopMethodTracing();
        this.mProfile = false;
      }
    }
  }
  
  void drawPending()
  {
    this.mDrawsNeededToReport += 1;
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramFileDescriptor = new StringBuilder();
    paramFileDescriptor.append(paramString);
    paramFileDescriptor.append("  ");
    paramFileDescriptor = paramFileDescriptor.toString();
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("ViewRoot:");
    paramPrintWriter.print(paramFileDescriptor);
    paramPrintWriter.print("mAdded=");
    paramPrintWriter.print(this.mAdded);
    paramPrintWriter.print(" mRemoved=");
    paramPrintWriter.println(this.mRemoved);
    paramPrintWriter.print(paramFileDescriptor);
    paramPrintWriter.print("mConsumeBatchedInputScheduled=");
    paramPrintWriter.println(this.mConsumeBatchedInputScheduled);
    paramPrintWriter.print(paramFileDescriptor);
    paramPrintWriter.print("mConsumeBatchedInputImmediatelyScheduled=");
    paramPrintWriter.println(this.mConsumeBatchedInputImmediatelyScheduled);
    paramPrintWriter.print(paramFileDescriptor);
    paramPrintWriter.print("mPendingInputEventCount=");
    paramPrintWriter.println(this.mPendingInputEventCount);
    paramPrintWriter.print(paramFileDescriptor);
    paramPrintWriter.print("mProcessInputEventsScheduled=");
    paramPrintWriter.println(this.mProcessInputEventsScheduled);
    paramPrintWriter.print(paramFileDescriptor);
    paramPrintWriter.print("mTraversalScheduled=");
    paramPrintWriter.print(this.mTraversalScheduled);
    paramPrintWriter.print(paramFileDescriptor);
    paramPrintWriter.print("mIsAmbientMode=");
    paramPrintWriter.print(this.mIsAmbientMode);
    if (this.mTraversalScheduled)
    {
      paramPrintWriter.print(" (barrier=");
      paramPrintWriter.print(this.mTraversalBarrier);
      paramPrintWriter.println(")");
    }
    else
    {
      paramPrintWriter.println();
    }
    this.mFirstInputStage.dump(paramFileDescriptor, paramPrintWriter);
    this.mChoreographer.dump(paramString, paramPrintWriter);
    this.mInsetsController.dump(paramString, paramPrintWriter);
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("View Hierarchy:");
    dumpViewHierarchy(paramFileDescriptor, paramPrintWriter, this.mView);
  }
  
  public void dumpGfxInfo(int[] paramArrayOfInt)
  {
    paramArrayOfInt[1] = 0;
    paramArrayOfInt[0] = 0;
    View localView = this.mView;
    if (localView != null) {
      getGfxInfo(localView, paramArrayOfInt);
    }
  }
  
  @UnsupportedAppUsage
  void enqueueInputEvent(InputEvent paramInputEvent)
  {
    enqueueInputEvent(paramInputEvent, null, 0, false);
  }
  
  @UnsupportedAppUsage
  void enqueueInputEvent(InputEvent paramInputEvent, InputEventReceiver paramInputEventReceiver, int paramInt, boolean paramBoolean)
  {
    paramInputEventReceiver = obtainQueuedInputEvent(paramInputEvent, paramInputEventReceiver, paramInt);
    paramInputEvent = this.mPendingInputEventTail;
    if (paramInputEvent == null)
    {
      this.mPendingInputEventHead = paramInputEventReceiver;
      this.mPendingInputEventTail = paramInputEventReceiver;
    }
    else
    {
      paramInputEvent.mNext = paramInputEventReceiver;
      this.mPendingInputEventTail = paramInputEventReceiver;
    }
    this.mPendingInputEventCount += 1;
    Trace.traceCounter(4L, this.mPendingInputEventQueueLengthCounterName, this.mPendingInputEventCount);
    if (paramBoolean) {
      doProcessInputEvents();
    } else {
      scheduleProcessInputEvents();
    }
  }
  
  @UnsupportedAppUsage
  boolean ensureTouchMode(boolean paramBoolean)
  {
    if (this.mAttachInfo.mInTouchMode == paramBoolean) {
      return false;
    }
    try
    {
      this.mWindowSession.setInTouchMode(paramBoolean);
      return ensureTouchModeLocally(paramBoolean);
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException(localRemoteException);
    }
  }
  
  public View focusSearch(View paramView, int paramInt)
  {
    checkThread();
    if (!(this.mView instanceof ViewGroup)) {
      return null;
    }
    return FocusFinder.getInstance().findNextFocus((ViewGroup)this.mView, paramView, paramInt);
  }
  
  public void focusableViewAvailable(View paramView)
  {
    checkThread();
    View localView = this.mView;
    if (localView != null) {
      if (!localView.hasFocus())
      {
        if ((sAlwaysAssignFocus) || (!this.mAttachInfo.mInTouchMode)) {
          paramView.requestFocus();
        }
      }
      else
      {
        localView = this.mView.findFocus();
        if (((localView instanceof ViewGroup)) && (((ViewGroup)localView).getDescendantFocusability() == 262144) && (isViewDescendantOf(paramView, localView))) {
          paramView.requestFocus();
        }
      }
    }
  }
  
  @UnsupportedAppUsage
  public View getAccessibilityFocusedHost()
  {
    return this.mAccessibilityFocusedHost;
  }
  
  @UnsupportedAppUsage
  public AccessibilityNodeInfo getAccessibilityFocusedVirtualView()
  {
    return this.mAccessibilityFocusedVirtualView;
  }
  
  public AccessibilityInteractionController getAccessibilityInteractionController()
  {
    if (this.mView != null)
    {
      if (this.mAccessibilityInteractionController == null) {
        this.mAccessibilityInteractionController = new AccessibilityInteractionController(this);
      }
      return this.mAccessibilityInteractionController;
    }
    throw new IllegalStateException("getAccessibilityInteractionController called when there is no mView");
  }
  
  public boolean getChildVisibleRect(View paramView, Rect paramRect, Point paramPoint)
  {
    if (paramView == this.mView) {
      return paramRect.intersect(0, 0, this.mWidth, this.mHeight);
    }
    throw new RuntimeException("child is not mine, honest!");
  }
  
  public int getDisplayId()
  {
    return this.mDisplay.getDisplayId();
  }
  
  public int getHeight()
  {
    return this.mHeight;
  }
  
  int getHostVisibility()
  {
    int i;
    if ((!this.mAppVisible) && (!this.mForceDecorViewVisibility)) {
      i = 8;
    } else {
      i = this.mView.getVisibility();
    }
    return i;
  }
  
  InsetsController getInsetsController()
  {
    return this.mInsetsController;
  }
  
  public boolean getIsProjectionMode()
  {
    return this.mIsProjectionMode;
  }
  
  @UnsupportedAppUsage
  public void getLastTouchPoint(Point paramPoint)
  {
    paramPoint.x = ((int)this.mLastTouchPoint.x);
    paramPoint.y = ((int)this.mLastTouchPoint.y);
  }
  
  public int getLastTouchSource()
  {
    return this.mLastTouchSource;
  }
  
  public int getLayoutDirection()
  {
    return 0;
  }
  
  final WindowLeaked getLocation()
  {
    return this.mLocation;
  }
  
  public ViewParent getParent()
  {
    return null;
  }
  
  public ViewParent getParentForAccessibility()
  {
    return null;
  }
  
  public boolean getProjectionModeChanged()
  {
    return this.mProjectionModeChanged;
  }
  
  public List<Rect> getRootSystemGestureExclusionRects()
  {
    return this.mGestureExclusionTracker.getRootSystemGestureExclusionRects();
  }
  
  public SurfaceControl getSurfaceControl()
  {
    return this.mSurfaceControl;
  }
  
  public int getTextAlignment()
  {
    return 1;
  }
  
  public int getTextDirection()
  {
    return 1;
  }
  
  public CharSequence getTitle()
  {
    return this.mWindowAttributes.getTitle();
  }
  
  @UnsupportedAppUsage
  public View getView()
  {
    return this.mView;
  }
  
  public int getWidth()
  {
    return this.mWidth;
  }
  
  @UnsupportedAppUsage
  public int getWindowFlags()
  {
    return this.mWindowAttributes.flags;
  }
  
  WindowInsets getWindowInsets(boolean paramBoolean)
  {
    if ((this.mLastWindowInsets == null) || (paramBoolean))
    {
      this.mDispatchContentInsets.set(this.mAttachInfo.mContentInsets);
      this.mDispatchStableInsets.set(this.mAttachInfo.mStableInsets);
      this.mDispatchDisplayCutout = this.mAttachInfo.mDisplayCutout.get();
      Rect localRect1 = this.mDispatchContentInsets;
      Rect localRect2 = this.mDispatchStableInsets;
      DisplayCutout localDisplayCutout = this.mDispatchDisplayCutout;
      if ((!paramBoolean) && ((!this.mPendingContentInsets.equals(localRect1)) || (!this.mPendingStableInsets.equals(localRect2)) || (!this.mPendingDisplayCutout.get().equals(localDisplayCutout))))
      {
        localRect1 = this.mPendingContentInsets;
        localRect2 = this.mPendingStableInsets;
        localDisplayCutout = this.mPendingDisplayCutout.get();
      }
      Rect localRect3 = this.mAttachInfo.mOutsets;
      Rect localRect4;
      if ((localRect3.left <= 0) && (localRect3.top <= 0) && (localRect3.right <= 0))
      {
        localRect4 = localRect1;
        if (localRect3.bottom <= 0) {}
      }
      else
      {
        localRect4 = new Rect(localRect1.left + localRect3.left, localRect1.top + localRect3.top, localRect1.right + localRect3.right, localRect1.bottom + localRect3.bottom);
      }
      localRect1 = ensureInsetsNonNegative(localRect4, "content");
      localRect2 = ensureInsetsNonNegative(localRect2, "stable");
      this.mLastWindowInsets = this.mInsetsController.calculateInsets(this.mContext.getResources().getConfiguration().isScreenRound(), this.mAttachInfo.mAlwaysConsumeSystemBars, localDisplayCutout, localRect1, localRect2, this.mWindowAttributes.softInputMode);
    }
    return this.mLastWindowInsets;
  }
  
  void handleAppVisibility(boolean paramBoolean)
  {
    if (this.mAppVisible != paramBoolean)
    {
      this.mAppVisible = paramBoolean;
      this.mAppVisibilityChanged = true;
      scheduleTraversals();
      if (!this.mAppVisible) {
        WindowManagerGlobal.trimForeground();
      }
      if (this.mAppVisible) {
        ForceDarkHelper.getInstance().updateForceDarkMode(this);
      }
    }
  }
  
  void handleCastModeChange()
  {
    for (int i = 0; i < mCastProjectionCallbacks.size(); i++) {
      ((CastProjectionCallback)mCastProjectionCallbacks.get(i)).castModeChanged();
    }
  }
  
  public void handleDispatchSystemUiVisibilityChanged(SystemUiVisibilityInfo paramSystemUiVisibilityInfo)
  {
    if (this.mSeq != paramSystemUiVisibilityInfo.seq)
    {
      this.mSeq = paramSystemUiVisibilityInfo.seq;
      this.mAttachInfo.mForceReportNewAttributes = true;
      scheduleTraversals();
    }
    if (this.mView == null) {
      return;
    }
    if (paramSystemUiVisibilityInfo.localChanges != 0) {
      this.mView.updateLocalSystemUiVisibility(paramSystemUiVisibilityInfo.localValue, paramSystemUiVisibilityInfo.localChanges);
    }
    int i = paramSystemUiVisibilityInfo.globalVisibility & 0x7;
    if (i != this.mAttachInfo.mGlobalSystemUiVisibility)
    {
      this.mAttachInfo.mGlobalSystemUiVisibility = i;
      this.mView.dispatchSystemUiVisibilityChanged(i);
    }
  }
  
  public void handleDispatchWindowShown()
  {
    this.mAttachInfo.mTreeObserver.dispatchOnWindowShown();
  }
  
  void handleGetNewSurface()
  {
    this.mNewSurfaceNeeded = true;
    this.mFullRedrawNeeded = true;
    scheduleTraversals();
  }
  
  public void handleRequestKeyboardShortcuts(IResultReceiver paramIResultReceiver, int paramInt)
  {
    Bundle localBundle = new Bundle();
    ArrayList localArrayList = new ArrayList();
    View localView = this.mView;
    if (localView != null) {
      localView.requestKeyboardShortcuts(localArrayList, paramInt);
    }
    localBundle.putParcelableArrayList("shortcuts_array", localArrayList);
    try
    {
      paramIResultReceiver.send(0, localBundle);
    }
    catch (RemoteException paramIResultReceiver) {}
  }
  
  boolean hasPointerCapture()
  {
    return this.mPointerCapture;
  }
  
  @UnsupportedAppUsage
  void invalidate()
  {
    this.mDirty.set(0, 0, this.mWidth, this.mHeight);
    if (!this.mWillDrawSoon) {
      scheduleTraversals();
    }
  }
  
  public void invalidateChild(View paramView, Rect paramRect)
  {
    invalidateChildInParent(null, paramRect);
  }
  
  public ViewParent invalidateChildInParent(int[] paramArrayOfInt, Rect paramRect)
  {
    checkThread();
    if (paramRect == null)
    {
      invalidate();
      return null;
    }
    if ((paramRect.isEmpty()) && (!this.mIsAnimating)) {
      return null;
    }
    if (this.mCurScrollY == 0)
    {
      paramArrayOfInt = paramRect;
      if (this.mTranslator == null) {}
    }
    else
    {
      this.mTempRect.set(paramRect);
      paramRect = this.mTempRect;
      int i = this.mCurScrollY;
      if (i != 0) {
        paramRect.offset(0, -i);
      }
      paramArrayOfInt = this.mTranslator;
      if (paramArrayOfInt != null) {
        paramArrayOfInt.translateRectInAppWindowToScreen(paramRect);
      }
      paramArrayOfInt = paramRect;
      if (this.mAttachInfo.mScalingRequired)
      {
        paramRect.inset(-1, -1);
        paramArrayOfInt = paramRect;
      }
    }
    invalidateRectOnScreen(paramArrayOfInt);
    return null;
  }
  
  void invalidateWorld(View paramView)
  {
    paramView.invalidate();
    if ((paramView instanceof ViewGroup))
    {
      paramView = (ViewGroup)paramView;
      for (int i = 0; i < paramView.getChildCount(); i++) {
        invalidateWorld(paramView.getChildAt(i));
      }
    }
  }
  
  boolean isInLayout()
  {
    return this.mInLayout;
  }
  
  public boolean isLayoutDirectionResolved()
  {
    return true;
  }
  
  public boolean isLayoutRequested()
  {
    return this.mLayoutRequested;
  }
  
  public boolean isTextAlignmentResolved()
  {
    return true;
  }
  
  public boolean isTextDirectionResolved()
  {
    return true;
  }
  
  public View keyboardNavigationClusterSearch(View paramView, int paramInt)
  {
    checkThread();
    return FocusFinder.getInstance().findNextKeyboardNavigationCluster(this.mView, paramView, paramInt);
  }
  
  public void loadSystemProperties()
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        ViewRootImpl.access$3502(ViewRootImpl.this, SystemProperties.getBoolean("viewroot.profile_rendering", false));
        ViewRootImpl localViewRootImpl = ViewRootImpl.this;
        localViewRootImpl.profileRendering(localViewRootImpl.mAttachInfo.mHasWindowFocus);
        if ((ViewRootImpl.this.mAttachInfo.mThreadedRenderer != null) && (ViewRootImpl.this.mAttachInfo.mThreadedRenderer.loadSystemProperties())) {
          ViewRootImpl.this.invalidate();
        }
        boolean bool = ((Boolean)DisplayProperties.debug_layout().orElse(Boolean.valueOf(false))).booleanValue();
        if (bool != ViewRootImpl.this.mAttachInfo.mDebugLayout)
        {
          ViewRootImpl.this.mAttachInfo.mDebugLayout = bool;
          if (!ViewRootImpl.this.mHandler.hasMessages(22)) {
            ViewRootImpl.this.mHandler.sendEmptyMessageDelayed(22, 200L);
          }
        }
      }
    });
  }
  
  public void notifyCastMode(boolean paramBoolean)
  {
    this.mIsCastMode = paramBoolean;
    Message localMessage = Message.obtain();
    localMessage.what = 1000;
    this.mHandler.sendMessage(localMessage);
  }
  
  public void notifyChildRebuilt()
  {
    if ((this.mView instanceof RootViewSurfaceTaker))
    {
      Object localObject = this.mSurfaceHolderCallback;
      if (localObject != null) {
        this.mSurfaceHolder.removeCallback((SurfaceHolder.Callback)localObject);
      }
      this.mSurfaceHolderCallback = ((RootViewSurfaceTaker)this.mView).willYouTakeTheSurface();
      if (this.mSurfaceHolderCallback != null)
      {
        this.mSurfaceHolder = new TakenSurfaceHolder();
        this.mSurfaceHolder.setFormat(0);
        this.mSurfaceHolder.addCallback(this.mSurfaceHolderCallback);
      }
      else
      {
        this.mSurfaceHolder = null;
      }
      this.mInputQueueCallback = ((RootViewSurfaceTaker)this.mView).willYouTakeTheInputQueue();
      localObject = this.mInputQueueCallback;
      if (localObject != null) {
        ((InputQueue.Callback)localObject).onInputQueueCreated(this.mInputQueue);
      }
    }
  }
  
  public void notifyContentChangeToContentCatcher()
  {
    Object localObject = this.mView;
    if ((localObject != null) && ((localObject instanceof DecorView)))
    {
      localObject = ((DecorView)localObject).getWindowContext();
      if ((localObject != null) && ((localObject instanceof Activity)))
      {
        localObject = (Activity)localObject;
        if (((Activity)localObject).getInterceptor() != null) {
          ((Activity)localObject).getInterceptor().notifyContentChange();
        }
      }
    }
  }
  
  void notifyInsetsChanged()
  {
    if (sNewInsetsMode == 0) {
      return;
    }
    this.mApplyInsetsRequested = true;
    if (!this.mIsInTraversal) {
      scheduleTraversals();
    }
  }
  
  public void notifyProjectionMode(boolean paramBoolean)
  {
    this.mIsProjectionMode = paramBoolean;
    this.mProjectionModeChanged = true;
  }
  
  void notifyRendererOfFramePending()
  {
    if (this.mAttachInfo.mThreadedRenderer != null) {
      this.mAttachInfo.mThreadedRenderer.notifyFramePending();
    }
  }
  
  public void notifyRotationChanged(boolean paramBoolean)
  {
    this.mIsCastModeRotationChanged = paramBoolean;
  }
  
  public void notifySubtreeAccessibilityStateChanged(View paramView1, View paramView2, int paramInt)
  {
    postSendWindowContentChangedCallback((View)Preconditions.checkNotNull(paramView2), paramInt);
  }
  
  void notifySurfaceViewCountChange(boolean paramBoolean)
  {
    if (paramBoolean) {}
    try
    {
      if (this.mSurfaceViewCount <= 0) {
        this.mWindowSession.notifyHasSurfaceView(this.mWindow, true);
      }
      this.mSurfaceViewCount += 1;
      return;
      int i = this.mSurfaceViewCount - 1;
      this.mSurfaceViewCount = i;
      if (i <= 0) {
        this.mWindowSession.notifyHasSurfaceView(this.mWindow, false);
      }
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e(this.mTag, "Unable to notify surfaceview count change.");
    }
  }
  
  public void onDescendantInvalidated(View paramView1, View paramView2)
  {
    if ((paramView2.mPrivateFlags & 0x40) != 0) {
      this.mIsAnimating = true;
    }
    invalidate();
  }
  
  public void onMovedToDisplay(int paramInt, Configuration paramConfiguration)
  {
    if (this.mDisplay.getDisplayId() == paramInt) {
      return;
    }
    updateInternalDisplay(paramInt, this.mView.getResources());
    this.mAttachInfo.mDisplayState = this.mDisplay.getState();
    this.mView.dispatchMovedToDisplay(this.mDisplay, paramConfiguration);
  }
  
  public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    return false;
  }
  
  public boolean onNestedPreFling(View paramView, float paramFloat1, float paramFloat2)
  {
    return false;
  }
  
  public boolean onNestedPrePerformAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
  {
    return false;
  }
  
  public void onNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt) {}
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt) {}
  
  public void onPostDraw(RecordingCanvas paramRecordingCanvas)
  {
    drawAccessibilityFocusedDrawableIfNeeded(paramRecordingCanvas);
    if (this.mUseMTRenderer) {
      for (int i = this.mWindowCallbacks.size() - 1; i >= 0; i--) {
        ((WindowCallbacks)this.mWindowCallbacks.get(i)).onPostDraw(paramRecordingCanvas);
      }
    }
  }
  
  public void onPreDraw(RecordingCanvas paramRecordingCanvas)
  {
    if ((this.mCurScrollY != 0) && (this.mHardwareYOffset != 0) && (this.mAttachInfo.mThreadedRenderer.isOpaque())) {
      paramRecordingCanvas.drawColor(-16777216);
    }
    paramRecordingCanvas.translate(-this.mHardwareXOffset, -this.mHardwareYOffset);
  }
  
  public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt)
  {
    return false;
  }
  
  public void onStopNestedScroll(View paramView) {}
  
  public void onWindowTitleChanged()
  {
    this.mAttachInfo.mForceReportNewAttributes = true;
  }
  
  void outputDisplayList(View paramView)
  {
    paramView.mRenderNode.output();
  }
  
  void pendingDrawFinished()
  {
    int i = this.mDrawsNeededToReport;
    if (i != 0)
    {
      this.mDrawsNeededToReport = (i - 1);
      if (this.mDrawsNeededToReport == 0) {
        reportDrawFinished();
      }
      return;
    }
    throw new RuntimeException("Unbalanced drawPending/pendingDrawFinished calls");
  }
  
  public boolean performHapticFeedback(int paramInt, boolean paramBoolean)
  {
    try
    {
      paramBoolean = this.mWindowSession.performHapticFeedback(paramInt, paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public void playSoundEffect(int paramInt)
  {
    checkThread();
    try
    {
      Object localObject1 = getAudioManager();
      if (paramInt != 0)
      {
        if (paramInt != 1)
        {
          if (paramInt != 2)
          {
            if (paramInt != 3)
            {
              if (paramInt == 4)
              {
                ((AudioManager)localObject1).playSoundEffect(2);
                return;
              }
              localObject1 = new java/lang/IllegalArgumentException;
              localObject2 = new java/lang/StringBuilder;
              ((StringBuilder)localObject2).<init>();
              ((StringBuilder)localObject2).append("unknown effect id ");
              ((StringBuilder)localObject2).append(paramInt);
              ((StringBuilder)localObject2).append(" not defined in ");
              ((StringBuilder)localObject2).append(SoundEffectConstants.class.getCanonicalName());
              ((IllegalArgumentException)localObject1).<init>(((StringBuilder)localObject2).toString());
              throw ((Throwable)localObject1);
            }
            ((AudioManager)localObject1).playSoundEffect(4);
            return;
          }
          ((AudioManager)localObject1).playSoundEffect(1);
          return;
        }
        ((AudioManager)localObject1).playSoundEffect(3);
        return;
      }
      ((AudioManager)localObject1).playSoundEffect(0);
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Object localObject2 = this.mTag;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("FATAL EXCEPTION when attempting to play sound effect: ");
      localStringBuilder.append(localIllegalStateException);
      Log.e((String)localObject2, localStringBuilder.toString());
      localIllegalStateException.printStackTrace();
    }
  }
  
  void pokeDrawLockIfNeeded()
  {
    int i = this.mAttachInfo.mDisplayState;
    if ((this.mView != null) && (this.mAdded) && (this.mTraversalScheduled) && ((i == 3) || (i == 4))) {
      try
      {
        this.mWindowSession.pokeDrawLock(this.mWindow);
      }
      catch (RemoteException localRemoteException) {}
    }
  }
  
  public void profile()
  {
    this.mProfile = true;
  }
  
  public void recomputeViewAttributes(View paramView)
  {
    checkThread();
    if (this.mView == paramView)
    {
      this.mAttachInfo.mRecomputeGlobalAttributes = true;
      if (!this.mWillDrawSoon) {
        scheduleTraversals();
      }
    }
  }
  
  public void registerAnimatingRenderNode(RenderNode paramRenderNode)
  {
    if (this.mAttachInfo.mThreadedRenderer != null)
    {
      this.mAttachInfo.mThreadedRenderer.registerAnimatingRenderNode(paramRenderNode);
    }
    else
    {
      if (this.mAttachInfo.mPendingAnimatingRenderNodes == null) {
        this.mAttachInfo.mPendingAnimatingRenderNodes = new ArrayList();
      }
      this.mAttachInfo.mPendingAnimatingRenderNodes.add(paramRenderNode);
    }
  }
  
  public void registerRtFrameCallback(HardwareRenderer.FrameDrawingCallback paramFrameDrawingCallback)
  {
    if (this.mAttachInfo.mThreadedRenderer != null) {
      this.mAttachInfo.mThreadedRenderer.registerRtFrameCallback(new _..Lambda.ViewRootImpl.IReiNMSbDakZSGbIZuL_ifaFWn8(paramFrameDrawingCallback));
    }
  }
  
  public void registerVectorDrawableAnimator(NativeVectorDrawableAnimator paramNativeVectorDrawableAnimator)
  {
    if (this.mAttachInfo.mThreadedRenderer != null) {
      this.mAttachInfo.mThreadedRenderer.registerVectorDrawableAnimator(paramNativeVectorDrawableAnimator);
    }
  }
  
  void removeCastProjectionCallback(CastProjectionCallback paramCastProjectionCallback)
  {
    mCastProjectionCallbacks.remove(paramCastProjectionCallback);
  }
  
  public void removeWindowCallbacks(WindowCallbacks paramWindowCallbacks)
  {
    synchronized (this.mWindowCallbacks)
    {
      this.mWindowCallbacks.remove(paramWindowCallbacks);
      return;
    }
  }
  
  void removeWindowStoppedCallback(WindowStoppedCallback paramWindowStoppedCallback)
  {
    this.mWindowStoppedCallbacks.remove(paramWindowStoppedCallback);
  }
  
  public void reportActivityRelaunched()
  {
    this.mActivityRelaunched = true;
  }
  
  public void reportDrawFinish()
  {
    CountDownLatch localCountDownLatch = this.mWindowDrawCountDown;
    if (localCountDownLatch != null) {
      localCountDownLatch.countDown();
    }
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    checkThread();
    scheduleTraversals();
  }
  
  public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean)
  {
    if (paramRect == null) {
      return scrollToRectOrFocus(null, paramBoolean);
    }
    paramRect.offset(paramView.getLeft() - paramView.getScrollX(), paramView.getTop() - paramView.getScrollY());
    paramBoolean = scrollToRectOrFocus(paramRect, paramBoolean);
    this.mTempRect.set(paramRect);
    this.mTempRect.offset(0, -this.mCurScrollY);
    this.mTempRect.offset(this.mAttachInfo.mWindowLeft, this.mAttachInfo.mWindowTop);
    try
    {
      this.mWindowSession.onRectangleOnScreenRequested(this.mWindow, this.mTempRect);
    }
    catch (RemoteException paramView) {}
    return paramBoolean;
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean) {}
  
  public void requestFitSystemWindows()
  {
    checkThread();
    this.mApplyInsetsRequested = true;
    scheduleTraversals();
  }
  
  public void requestInvalidateRootRenderNode()
  {
    this.mInvalidateRootRequested = true;
  }
  
  public void requestLayout()
  {
    if (!this.mHandlingLayoutInLayoutRequest)
    {
      checkThread();
      this.mLayoutRequested = true;
      scheduleTraversals();
    }
  }
  
  boolean requestLayoutDuringLayout(View paramView)
  {
    if ((paramView.mParent != null) && (paramView.mAttachInfo != null))
    {
      if (!this.mLayoutRequesters.contains(paramView)) {
        this.mLayoutRequesters.add(paramView);
      }
      return !this.mHandlingLayoutInLayoutRequest;
    }
    return true;
  }
  
  void requestPointerCapture(boolean paramBoolean)
  {
    if (this.mPointerCapture == paramBoolean) {
      return;
    }
    InputManager.getInstance().requestPointerCapture(this.mAttachInfo.mWindowToken, paramBoolean);
  }
  
  public boolean requestSendAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    if ((this.mView != null) && (!this.mStopped) && (!this.mPausedForTransition))
    {
      if (paramAccessibilityEvent.getEventType() != 2048)
      {
        paramView = this.mSendWindowContentChangedAccessibilityEvent;
        if ((paramView != null) && (paramView.mSource != null)) {
          this.mSendWindowContentChangedAccessibilityEvent.removeCallbacksAndRun();
        }
      }
      int i = paramAccessibilityEvent.getEventType();
      View localView = getSourceForAccessibilityEvent(paramAccessibilityEvent);
      if (i != 2048)
      {
        if (i != 32768)
        {
          if ((i == 65536) && (localView != null) && (localView.getAccessibilityNodeProvider() != null)) {
            setAccessibilityFocus(null, null);
          }
        }
        else if (localView != null)
        {
          paramView = localView.getAccessibilityNodeProvider();
          if (paramView != null) {
            setAccessibilityFocus(localView, paramView.createAccessibilityNodeInfo(AccessibilityNodeInfo.getVirtualDescendantId(paramAccessibilityEvent.getSourceNodeId())));
          }
        }
      }
      else {
        handleWindowContentChangedEvent(paramAccessibilityEvent);
      }
      this.mAccessibilityManager.sendAccessibilityEvent(paramAccessibilityEvent);
      return true;
    }
    return false;
  }
  
  public void requestTransitionStart(LayoutTransition paramLayoutTransition)
  {
    ArrayList localArrayList = this.mPendingTransitions;
    if ((localArrayList == null) || (!localArrayList.contains(paramLayoutTransition)))
    {
      if (this.mPendingTransitions == null) {
        this.mPendingTransitions = new ArrayList();
      }
      this.mPendingTransitions.add(paramLayoutTransition);
    }
  }
  
  public void requestTransparentRegion(View paramView)
  {
    checkThread();
    View localView = this.mView;
    if (localView == paramView)
    {
      localView.mPrivateFlags |= 0x200;
      this.mWindowAttributesChanged = true;
      this.mWindowAttributesChangesFlag = 0;
      requestLayout();
    }
  }
  
  public void requestUpdateConfiguration(Configuration paramConfiguration)
  {
    paramConfiguration = this.mHandler.obtainMessage(18, paramConfiguration);
    this.mHandler.sendMessage(paramConfiguration);
  }
  
  void scheduleConsumeBatchedInput()
  {
    if (!this.mConsumeBatchedInputScheduled)
    {
      this.mConsumeBatchedInputScheduled = true;
      this.mChoreographer.postCallback(0, this.mConsumedBatchedInputRunnable, null);
    }
  }
  
  void scheduleConsumeBatchedInputImmediately()
  {
    if (!this.mConsumeBatchedInputImmediatelyScheduled)
    {
      unscheduleConsumeBatchedInput();
      this.mConsumeBatchedInputImmediatelyScheduled = true;
      this.mHandler.post(this.mConsumeBatchedInputImmediatelyRunnable);
    }
  }
  
  @UnsupportedAppUsage
  void scheduleTraversals()
  {
    if (!this.mTraversalScheduled)
    {
      this.mTraversalScheduled = true;
      this.mTraversalBarrier = this.mHandler.getLooper().getQueue().postSyncBarrier();
      this.mChoreographer.postCallback(3, this.mTraversalRunnable, null);
      if (!this.mUnbufferedInputDispatch) {
        scheduleConsumeBatchedInput();
      }
      notifyRendererOfFramePending();
      pokeDrawLockIfNeeded();
    }
  }
  
  boolean scrollToRectOrFocus(Rect paramRect, boolean paramBoolean)
  {
    Object localObject = this.mAttachInfo.mContentInsets;
    Rect localRect = this.mAttachInfo.mVisibleInsets;
    int i = 0;
    boolean bool1 = false;
    boolean bool2;
    int j;
    if ((localRect.left <= ((Rect)localObject).left) && (localRect.top <= ((Rect)localObject).top) && (localRect.right <= ((Rect)localObject).right))
    {
      bool2 = bool1;
      if (localRect.bottom <= ((Rect)localObject).bottom) {}
    }
    else
    {
      j = this.mScrollY;
      View localView = this.mView.findFocus();
      if (localView == null) {
        return false;
      }
      localObject = this.mLastScrolledFocus;
      if (localObject != null) {
        localObject = (View)((WeakReference)localObject).get();
      } else {
        localObject = null;
      }
      if (localView != localObject) {
        paramRect = null;
      }
      if ((localView == localObject) && (!this.mScrollMayChange) && (paramRect == null))
      {
        i = j;
        bool2 = bool1;
      }
      else
      {
        this.mLastScrolledFocus = new WeakReference(localView);
        this.mScrollMayChange = false;
        i = j;
        bool2 = bool1;
        if (localView.getGlobalVisibleRect(this.mVisRect, null))
        {
          if (paramRect == null)
          {
            localView.getFocusedRect(this.mTempRect);
            paramRect = this.mView;
            if ((paramRect instanceof ViewGroup)) {
              ((ViewGroup)paramRect).offsetDescendantRectToMyCoords(localView, this.mTempRect);
            }
          }
          else
          {
            this.mTempRect.set(paramRect);
          }
          i = j;
          bool2 = bool1;
          if (this.mTempRect.intersect(this.mVisRect))
          {
            if (this.mTempRect.height() > this.mView.getHeight() - localRect.top - localRect.bottom) {
              i = j;
            } else if (this.mTempRect.top < localRect.top) {
              i = this.mTempRect.top - localRect.top;
            } else if (this.mTempRect.bottom > this.mView.getHeight() - localRect.bottom) {
              i = this.mTempRect.bottom - (this.mView.getHeight() - localRect.bottom);
            } else {
              i = 0;
            }
            bool2 = true;
          }
        }
      }
    }
    if (i != this.mScrollY)
    {
      if (!paramBoolean)
      {
        if (this.mScroller == null) {
          this.mScroller = new Scroller(this.mView.getContext());
        }
        paramRect = this.mScroller;
        j = this.mScrollY;
        paramRect.startScroll(0, j, 0, i - j);
      }
      else
      {
        paramRect = this.mScroller;
        if (paramRect != null) {
          paramRect.abortAnimation();
        }
      }
      this.mScrollY = i;
    }
    return bool2;
  }
  
  void setAccessibilityFocus(View paramView, AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    if (this.mAccessibilityFocusedVirtualView != null)
    {
      localObject = this.mAccessibilityFocusedVirtualView;
      View localView = this.mAccessibilityFocusedHost;
      this.mAccessibilityFocusedHost = null;
      this.mAccessibilityFocusedVirtualView = null;
      localView.clearAccessibilityFocusNoCallbacks(64);
      AccessibilityNodeProvider localAccessibilityNodeProvider = localView.getAccessibilityNodeProvider();
      if (localAccessibilityNodeProvider != null)
      {
        ((AccessibilityNodeInfo)localObject).getBoundsInParent(this.mTempRect);
        localView.invalidate(this.mTempRect);
        localAccessibilityNodeProvider.performAction(AccessibilityNodeInfo.getVirtualDescendantId(((AccessibilityNodeInfo)localObject).getSourceNodeId()), 128, null);
      }
      ((AccessibilityNodeInfo)localObject).recycle();
    }
    Object localObject = this.mAccessibilityFocusedHost;
    if ((localObject != null) && (localObject != paramView)) {
      ((View)localObject).clearAccessibilityFocusNoCallbacks(64);
    }
    this.mAccessibilityFocusedHost = paramView;
    this.mAccessibilityFocusedVirtualView = paramAccessibilityNodeInfo;
    if (this.mAttachInfo.mThreadedRenderer != null) {
      this.mAttachInfo.mThreadedRenderer.invalidateRoot();
    }
  }
  
  public void setActivityConfigCallback(ActivityConfigCallback paramActivityConfigCallback)
  {
    this.mActivityConfigCallback = paramActivityConfigCallback;
  }
  
  public void setDragFocus(View paramView, DragEvent paramDragEvent)
  {
    if ((this.mCurrentDragView != paramView) && (!View.sCascadedDragDrop))
    {
      float f1 = paramDragEvent.mX;
      float f2 = paramDragEvent.mY;
      int i = paramDragEvent.mAction;
      ClipData localClipData = paramDragEvent.mClipData;
      paramDragEvent.mX = 0.0F;
      paramDragEvent.mY = 0.0F;
      paramDragEvent.mClipData = null;
      View localView = this.mCurrentDragView;
      if (localView != null)
      {
        paramDragEvent.mAction = 6;
        localView.callDragEventHandler(paramDragEvent);
      }
      if (paramView != null)
      {
        paramDragEvent.mAction = 5;
        paramView.callDragEventHandler(paramDragEvent);
      }
      paramDragEvent.mAction = i;
      paramDragEvent.mX = f1;
      paramDragEvent.mY = f2;
      paramDragEvent.mClipData = localClipData;
    }
    this.mCurrentDragView = paramView;
  }
  
  public void setForceNextWindowRelayout()
  {
    this.mForceNextWindowRelayout = true;
    requestLayout();
  }
  
  public void setIsAmbientMode(boolean paramBoolean)
  {
    this.mIsAmbientMode = paramBoolean;
  }
  
  void setLayoutParams(WindowManager.LayoutParams paramLayoutParams, boolean paramBoolean)
  {
    try
    {
      int i = this.mWindowAttributes.surfaceInsets.left;
      int j = this.mWindowAttributes.surfaceInsets.top;
      int k = this.mWindowAttributes.surfaceInsets.right;
      int m = this.mWindowAttributes.surfaceInsets.bottom;
      int n = this.mWindowAttributes.softInputMode;
      boolean bool = this.mWindowAttributes.hasManualSurfaceInsets;
      this.mClientWindowLayoutFlags = paramLayoutParams.flags;
      int i1 = this.mWindowAttributes.privateFlags;
      paramLayoutParams.systemUiVisibility = this.mWindowAttributes.systemUiVisibility;
      paramLayoutParams.subtreeSystemUiVisibility = this.mWindowAttributes.subtreeSystemUiVisibility;
      this.mWindowAttributesChangesFlag = this.mWindowAttributes.copyFrom(paramLayoutParams);
      if ((this.mWindowAttributesChangesFlag & 0x80000) != 0) {
        this.mAttachInfo.mRecomputeGlobalAttributes = true;
      }
      if ((this.mWindowAttributesChangesFlag & 0x1) != 0) {
        this.mAttachInfo.mNeedsUpdateLightCenter = true;
      }
      if (this.mWindowAttributes.packageName == null) {
        this.mWindowAttributes.packageName = this.mBasePackageName;
      }
      WindowManager.LayoutParams localLayoutParams = this.mWindowAttributes;
      localLayoutParams.privateFlags |= i1 & 0x80;
      if (this.mWindowAttributes.preservePreviousSurfaceInsets)
      {
        this.mWindowAttributes.surfaceInsets.set(i, j, k, m);
        this.mWindowAttributes.hasManualSurfaceInsets = bool;
      }
      else if ((this.mWindowAttributes.surfaceInsets.left != i) || (this.mWindowAttributes.surfaceInsets.top != j) || (this.mWindowAttributes.surfaceInsets.right != k) || (this.mWindowAttributes.surfaceInsets.bottom != m))
      {
        this.mNeedsRendererSetup = true;
      }
      applyKeepScreenOnFlag(this.mWindowAttributes);
      if (paramBoolean)
      {
        this.mSoftInputMode = paramLayoutParams.softInputMode;
        requestLayout();
      }
      if ((paramLayoutParams.softInputMode & 0xF0) == 0) {
        this.mWindowAttributes.softInputMode = (this.mWindowAttributes.softInputMode & 0xFF0F | n & 0xF0);
      }
      this.mWindowAttributesChanged = true;
      scheduleTraversals();
      return;
    }
    finally {}
  }
  
  @UnsupportedAppUsage
  void setLocalDragState(Object paramObject)
  {
    this.mLocalDragState = paramObject;
  }
  
  public void setPausedForTransition(boolean paramBoolean)
  {
    this.mPausedForTransition = paramBoolean;
  }
  
  public void setProjectionModeChanged(boolean paramBoolean)
  {
    this.mProjectionModeChanged = paramBoolean;
  }
  
  public void setReportNextDraw()
  {
    reportNextDraw();
    invalidate();
  }
  
  public void setRootSystemGestureExclusionRects(List<Rect> paramList)
  {
    this.mGestureExclusionTracker.setRootSystemGestureExclusionRects(paramList);
    this.mHandler.sendEmptyMessage(32);
  }
  
  /* Error */
  public void setView(View paramView1, WindowManager.LayoutParams paramLayoutParams, View paramView2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 1150	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   6: ifnonnull +1874 -> 1880
    //   9: aload_0
    //   10: aload_1
    //   11: putfield 1150	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   14: aload_0
    //   15: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   18: aload_0
    //   19: getfield 761	android/view/ViewRootImpl:mDisplay	Landroid/view/Display;
    //   22: invokevirtual 3792	android/view/Display:getState	()I
    //   25: putfield 2493	android/view/View$AttachInfo:mDisplayState	I
    //   28: aload_0
    //   29: getfield 907	android/view/ViewRootImpl:mDisplayManager	Landroid/hardware/display/DisplayManager;
    //   32: aload_0
    //   33: getfield 730	android/view/ViewRootImpl:mDisplayListener	Landroid/hardware/display/DisplayManager$DisplayListener;
    //   36: aload_0
    //   37: getfield 737	android/view/ViewRootImpl:mHandler	Landroid/view/ViewRootImpl$ViewRootHandler;
    //   40: invokevirtual 4020	android/hardware/display/DisplayManager:registerDisplayListener	(Landroid/hardware/display/DisplayManager$DisplayListener;Landroid/os/Handler;)V
    //   43: aload_0
    //   44: aload_0
    //   45: getfield 1150	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   48: invokevirtual 4023	android/view/View:getRawLayoutDirection	()I
    //   51: putfield 2632	android/view/ViewRootImpl:mViewLayoutDirectionInitial	I
    //   54: aload_0
    //   55: getfield 890	android/view/ViewRootImpl:mFallbackEventHandler	Landroid/view/FallbackEventHandler;
    //   58: aload_1
    //   59: invokeinterface 4027 2 0
    //   64: aload_0
    //   65: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   68: astore 4
    //   70: aload 4
    //   72: aload_2
    //   73: invokevirtual 3997	android/view/WindowManager$LayoutParams:copyFrom	(Landroid/view/WindowManager$LayoutParams;)I
    //   76: pop
    //   77: aload_0
    //   78: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   81: getfield 4001	android/view/WindowManager$LayoutParams:packageName	Ljava/lang/String;
    //   84: ifnonnull +14 -> 98
    //   87: aload_0
    //   88: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   91: aload_0
    //   92: getfield 769	android/view/ViewRootImpl:mBasePackageName	Ljava/lang/String;
    //   95: putfield 4001	android/view/WindowManager$LayoutParams:packageName	Ljava/lang/String;
    //   98: aload_0
    //   99: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   102: astore_2
    //   103: aload_0
    //   104: invokespecial 4029	android/view/ViewRootImpl:setTag	()V
    //   107: aload_0
    //   108: aload_2
    //   109: getfield 1114	android/view/WindowManager$LayoutParams:flags	I
    //   112: putfield 1116	android/view/ViewRootImpl:mClientWindowLayoutFlags	I
    //   115: aload_0
    //   116: aconst_null
    //   117: aconst_null
    //   118: invokevirtual 3321	android/view/ViewRootImpl:setAccessibilityFocus	(Landroid/view/View;Landroid/view/accessibility/AccessibilityNodeInfo;)V
    //   121: aload_1
    //   122: instanceof 1404
    //   125: ifeq +59 -> 184
    //   128: aload_0
    //   129: aload_1
    //   130: checkcast 1404	com/android/internal/view/RootViewSurfaceTaker
    //   133: invokeinterface 3736 1 0
    //   138: putfield 3728	android/view/ViewRootImpl:mSurfaceHolderCallback	Landroid/view/SurfaceHolder$Callback2;
    //   141: aload_0
    //   142: getfield 3728	android/view/ViewRootImpl:mSurfaceHolderCallback	Landroid/view/SurfaceHolder$Callback2;
    //   145: ifnull +39 -> 184
    //   148: new 97	android/view/ViewRootImpl$TakenSurfaceHolder
    //   151: astore 4
    //   153: aload 4
    //   155: aload_0
    //   156: invokespecial 3737	android/view/ViewRootImpl$TakenSurfaceHolder:<init>	(Landroid/view/ViewRootImpl;)V
    //   159: aload_0
    //   160: aload 4
    //   162: putfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   165: aload_0
    //   166: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   169: iconst_0
    //   170: invokevirtual 3740	com/android/internal/view/BaseSurfaceHolder:setFormat	(I)V
    //   173: aload_0
    //   174: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   177: aload_0
    //   178: getfield 3728	android/view/ViewRootImpl:mSurfaceHolderCallback	Landroid/view/SurfaceHolder$Callback2;
    //   181: invokevirtual 3743	com/android/internal/view/BaseSurfaceHolder:addCallback	(Landroid/view/SurfaceHolder$Callback;)V
    //   184: aload_2
    //   185: getfield 3992	android/view/WindowManager$LayoutParams:hasManualSurfaceInsets	Z
    //   188: ifne +10 -> 198
    //   191: aload_2
    //   192: aload_1
    //   193: iconst_0
    //   194: iconst_1
    //   195: invokevirtual 4032	android/view/WindowManager$LayoutParams:setSurfaceInsets	(Landroid/view/View;ZZ)V
    //   198: aload_0
    //   199: getfield 761	android/view/ViewRootImpl:mDisplay	Landroid/view/Display;
    //   202: invokevirtual 2447	android/view/Display:getDisplayAdjustments	()Landroid/view/DisplayAdjustments;
    //   205: invokevirtual 2453	android/view/DisplayAdjustments:getCompatibilityInfo	()Landroid/content/res/CompatibilityInfo;
    //   208: astore 4
    //   210: aload_0
    //   211: aload 4
    //   213: invokevirtual 4036	android/content/res/CompatibilityInfo:getTranslator	()Landroid/content/res/CompatibilityInfo$Translator;
    //   216: putfield 1300	android/view/ViewRootImpl:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   219: aload_0
    //   220: getfield 1415	android/view/ViewRootImpl:mSurfaceHolder	Lcom/android/internal/view/BaseSurfaceHolder;
    //   223: ifnonnull +53 -> 276
    //   226: aload_0
    //   227: aload_2
    //   228: invokespecial 4038	android/view/ViewRootImpl:enableHardwareAcceleration	(Landroid/view/WindowManager$LayoutParams;)V
    //   231: invokestatic 956	android/view/ForceDarkHelper:getInstance	()Landroid/view/ForceDarkHelper;
    //   234: aload_0
    //   235: invokevirtual 3651	android/view/ForceDarkHelper:updateForceDarkMode	(Landroid/view/ViewRootImpl;)V
    //   238: aload_0
    //   239: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   242: getfield 1222	android/view/View$AttachInfo:mThreadedRenderer	Landroid/view/ThreadedRenderer;
    //   245: ifnull +9 -> 254
    //   248: iconst_1
    //   249: istore 5
    //   251: goto +6 -> 257
    //   254: iconst_0
    //   255: istore 5
    //   257: aload_0
    //   258: getfield 1280	android/view/ViewRootImpl:mUseMTRenderer	Z
    //   261: iload 5
    //   263: if_icmpeq +13 -> 276
    //   266: aload_0
    //   267: invokespecial 2655	android/view/ViewRootImpl:endDragResizing	()V
    //   270: aload_0
    //   271: iload 5
    //   273: putfield 1280	android/view/ViewRootImpl:mUseMTRenderer	Z
    //   276: aload_0
    //   277: getfield 1300	android/view/ViewRootImpl:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   280: ifnull +32 -> 312
    //   283: aload_0
    //   284: getfield 602	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   287: aload_0
    //   288: getfield 1300	android/view/ViewRootImpl:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   291: invokevirtual 4042	android/view/Surface:setCompatibilityTranslator	(Landroid/content/res/CompatibilityInfo$Translator;)V
    //   294: aload_2
    //   295: invokevirtual 2955	android/view/WindowManager$LayoutParams:backup	()V
    //   298: aload_0
    //   299: getfield 1300	android/view/ViewRootImpl:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   302: aload_2
    //   303: invokevirtual 2958	android/content/res/CompatibilityInfo$Translator:translateWindowLayout	(Landroid/view/WindowManager$LayoutParams;)V
    //   306: iconst_1
    //   307: istore 6
    //   309: goto +6 -> 315
    //   312: iconst_0
    //   313: istore 6
    //   315: aload 4
    //   317: invokevirtual 2601	android/content/res/CompatibilityInfo:supportsScreen	()Z
    //   320: ifne +20 -> 340
    //   323: aload_2
    //   324: aload_2
    //   325: getfield 1654	android/view/WindowManager$LayoutParams:privateFlags	I
    //   328: sipush 128
    //   331: ior
    //   332: putfield 1654	android/view/WindowManager$LayoutParams:privateFlags	I
    //   335: aload_0
    //   336: iconst_1
    //   337: putfield 569	android/view/ViewRootImpl:mLastInCompatMode	Z
    //   340: aload_0
    //   341: aload_2
    //   342: getfield 2267	android/view/WindowManager$LayoutParams:softInputMode	I
    //   345: putfield 2710	android/view/ViewRootImpl:mSoftInputMode	I
    //   348: aload_0
    //   349: iconst_1
    //   350: putfield 595	android/view/ViewRootImpl:mWindowAttributesChanged	Z
    //   353: aload_0
    //   354: iconst_m1
    //   355: putfield 597	android/view/ViewRootImpl:mWindowAttributesChangesFlag	I
    //   358: aload_0
    //   359: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   362: aload_1
    //   363: putfield 3328	android/view/View$AttachInfo:mRootView	Landroid/view/View;
    //   366: aload_0
    //   367: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   370: astore 4
    //   372: aload_0
    //   373: getfield 1300	android/view/ViewRootImpl:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   376: ifnull +9 -> 385
    //   379: iconst_1
    //   380: istore 5
    //   382: goto +6 -> 388
    //   385: iconst_0
    //   386: istore 5
    //   388: aload 4
    //   390: iload 5
    //   392: putfield 1413	android/view/View$AttachInfo:mScalingRequired	Z
    //   395: aload_0
    //   396: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   399: astore 4
    //   401: aload_0
    //   402: getfield 1300	android/view/ViewRootImpl:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   405: ifnonnull +9 -> 414
    //   408: fconst_1
    //   409: fstore 7
    //   411: goto +12 -> 423
    //   414: aload_0
    //   415: getfield 1300	android/view/ViewRootImpl:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   418: getfield 4045	android/content/res/CompatibilityInfo$Translator:applicationScale	F
    //   421: fstore 7
    //   423: aload 4
    //   425: fload 7
    //   427: putfield 1410	android/view/View$AttachInfo:mApplicationScale	F
    //   430: aload_3
    //   431: ifnull +14 -> 445
    //   434: aload_0
    //   435: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   438: aload_3
    //   439: invokevirtual 4049	android/view/View:getApplicationWindowToken	()Landroid/os/IBinder;
    //   442: putfield 4052	android/view/View$AttachInfo:mPanelParentWindowToken	Landroid/os/IBinder;
    //   445: aload_0
    //   446: iconst_1
    //   447: putfield 827	android/view/ViewRootImpl:mAdded	Z
    //   450: aload_0
    //   451: invokevirtual 3938	android/view/ViewRootImpl:requestLayout	()V
    //   454: aload_0
    //   455: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   458: getfield 4055	android/view/WindowManager$LayoutParams:inputFeatures	I
    //   461: iconst_2
    //   462: iand
    //   463: ifne +16 -> 479
    //   466: new 3352	android/view/InputChannel
    //   469: astore_3
    //   470: aload_3
    //   471: invokespecial 4056	android/view/InputChannel:<init>	()V
    //   474: aload_0
    //   475: aload_3
    //   476: putfield 3350	android/view/ViewRootImpl:mInputChannel	Landroid/view/InputChannel;
    //   479: aload_0
    //   480: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   483: getfield 1654	android/view/WindowManager$LayoutParams:privateFlags	I
    //   486: sipush 16384
    //   489: iand
    //   490: ifeq +9 -> 499
    //   493: iconst_1
    //   494: istore 5
    //   496: goto +6 -> 502
    //   499: iconst_0
    //   500: istore 5
    //   502: aload_0
    //   503: iload 5
    //   505: putfield 559	android/view/ViewRootImpl:mForceDecorViewVisibility	Z
    //   508: aload_0
    //   509: getfield 3350	android/view/ViewRootImpl:mInputChannel	Landroid/view/InputChannel;
    //   512: ifnonnull +16 -> 528
    //   515: new 3352	android/view/InputChannel
    //   518: astore_3
    //   519: aload_3
    //   520: invokespecial 4056	android/view/InputChannel:<init>	()V
    //   523: aload_0
    //   524: aload_3
    //   525: putfield 3350	android/view/ViewRootImpl:mInputChannel	Landroid/view/InputChannel;
    //   528: aload_0
    //   529: aload_0
    //   530: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   533: getfield 2917	android/view/WindowManager$LayoutParams:type	I
    //   536: putfield 561	android/view/ViewRootImpl:mOrigWindowType	I
    //   539: aload_0
    //   540: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   543: iconst_1
    //   544: putfield 1142	android/view/View$AttachInfo:mRecomputeGlobalAttributes	Z
    //   547: aload_0
    //   548: invokespecial 2702	android/view/ViewRootImpl:collectViewAttributes	()Z
    //   551: pop
    //   552: aload_0
    //   553: getfield 759	android/view/ViewRootImpl:mWindowSession	Landroid/view/IWindowSession;
    //   556: astore 8
    //   558: aload_0
    //   559: getfield 803	android/view/ViewRootImpl:mWindow	Landroid/view/ViewRootImpl$W;
    //   562: astore 9
    //   564: aload_0
    //   565: getfield 2969	android/view/ViewRootImpl:mSeq	I
    //   568: istore 10
    //   570: aload_0
    //   571: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   574: astore 11
    //   576: aload_0
    //   577: invokevirtual 2594	android/view/ViewRootImpl:getHostVisibility	()I
    //   580: istore 12
    //   582: aload_0
    //   583: getfield 761	android/view/ViewRootImpl:mDisplay	Landroid/view/Display;
    //   586: invokevirtual 3131	android/view/Display:getDisplayId	()I
    //   589: istore 13
    //   591: aload_0
    //   592: getfield 616	android/view/ViewRootImpl:mTmpFrame	Landroid/graphics/Rect;
    //   595: astore_3
    //   596: aload_0
    //   597: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   600: getfield 2672	android/view/View$AttachInfo:mContentInsets	Landroid/graphics/Rect;
    //   603: astore 14
    //   605: aload_0
    //   606: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   609: getfield 2675	android/view/View$AttachInfo:mStableInsets	Landroid/graphics/Rect;
    //   612: astore 15
    //   614: aload_0
    //   615: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   618: getfield 2687	android/view/View$AttachInfo:mOutsets	Landroid/graphics/Rect;
    //   621: astore 16
    //   623: aload_0
    //   624: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   627: getfield 2678	android/view/View$AttachInfo:mDisplayCutout	Landroid/view/DisplayCutout$ParcelableWrapper;
    //   630: astore 4
    //   632: aload_0
    //   633: getfield 3350	android/view/ViewRootImpl:mInputChannel	Landroid/view/InputChannel;
    //   636: astore 17
    //   638: aload 8
    //   640: aload 9
    //   642: iload 10
    //   644: aload 11
    //   646: iload 12
    //   648: iload 13
    //   650: aload_3
    //   651: aload 14
    //   653: aload 15
    //   655: aload 16
    //   657: aload 4
    //   659: aload 17
    //   661: aload_0
    //   662: getfield 645	android/view/ViewRootImpl:mTempInsets	Landroid/view/InsetsState;
    //   665: invokeinterface 4060 13 0
    //   670: istore 12
    //   672: aload_0
    //   673: aload_0
    //   674: getfield 616	android/view/ViewRootImpl:mTmpFrame	Landroid/graphics/Rect;
    //   677: invokespecial 1086	android/view/ViewRootImpl:setFrame	(Landroid/graphics/Rect;)V
    //   680: iload 6
    //   682: ifeq +7 -> 689
    //   685: aload_2
    //   686: invokevirtual 2983	android/view/WindowManager$LayoutParams:restore	()V
    //   689: aload_0
    //   690: getfield 1300	android/view/ViewRootImpl:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   693: ifnull +17 -> 710
    //   696: aload_0
    //   697: getfield 1300	android/view/ViewRootImpl:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   700: aload_0
    //   701: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   704: getfield 2672	android/view/View$AttachInfo:mContentInsets	Landroid/graphics/Rect;
    //   707: invokevirtual 1305	android/content/res/CompatibilityInfo$Translator:translateRectInScreenToAppWindow	(Landroid/graphics/Rect;)V
    //   710: aload_0
    //   711: getfield 618	android/view/ViewRootImpl:mPendingOverscanInsets	Landroid/graphics/Rect;
    //   714: iconst_0
    //   715: iconst_0
    //   716: iconst_0
    //   717: iconst_0
    //   718: invokevirtual 1425	android/graphics/Rect:set	(IIII)V
    //   721: aload_0
    //   722: getfield 624	android/view/ViewRootImpl:mPendingContentInsets	Landroid/graphics/Rect;
    //   725: aload_0
    //   726: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   729: getfield 2672	android/view/View$AttachInfo:mContentInsets	Landroid/graphics/Rect;
    //   732: invokevirtual 2684	android/graphics/Rect:set	(Landroid/graphics/Rect;)V
    //   735: aload_0
    //   736: getfield 622	android/view/ViewRootImpl:mPendingStableInsets	Landroid/graphics/Rect;
    //   739: aload_0
    //   740: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   743: getfield 2675	android/view/View$AttachInfo:mStableInsets	Landroid/graphics/Rect;
    //   746: invokevirtual 2684	android/graphics/Rect:set	(Landroid/graphics/Rect;)V
    //   749: aload_0
    //   750: getfield 640	android/view/ViewRootImpl:mPendingDisplayCutout	Landroid/view/DisplayCutout$ParcelableWrapper;
    //   753: aload_0
    //   754: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   757: getfield 2678	android/view/View$AttachInfo:mDisplayCutout	Landroid/view/DisplayCutout$ParcelableWrapper;
    //   760: invokevirtual 2780	android/view/DisplayCutout$ParcelableWrapper:set	(Landroid/view/DisplayCutout$ParcelableWrapper;)V
    //   763: aload_0
    //   764: getfield 620	android/view/ViewRootImpl:mPendingVisibleInsets	Landroid/graphics/Rect;
    //   767: iconst_0
    //   768: iconst_0
    //   769: iconst_0
    //   770: iconst_0
    //   771: invokevirtual 1425	android/graphics/Rect:set	(IIII)V
    //   774: aload_0
    //   775: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   778: astore_3
    //   779: iload 12
    //   781: iconst_4
    //   782: iand
    //   783: ifeq +9 -> 792
    //   786: iconst_1
    //   787: istore 5
    //   789: goto +6 -> 795
    //   792: iconst_0
    //   793: istore 5
    //   795: aload_3
    //   796: iload 5
    //   798: putfield 2692	android/view/View$AttachInfo:mAlwaysConsumeSystemBars	Z
    //   801: aload_0
    //   802: aload_0
    //   803: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   806: getfield 2692	android/view/View$AttachInfo:mAlwaysConsumeSystemBars	Z
    //   809: putfield 2689	android/view/ViewRootImpl:mPendingAlwaysConsumeSystemBars	Z
    //   812: aload_0
    //   813: getfield 716	android/view/ViewRootImpl:mInsetsController	Landroid/view/InsetsController;
    //   816: aload_0
    //   817: getfield 645	android/view/ViewRootImpl:mTempInsets	Landroid/view/InsetsState;
    //   820: invokevirtual 2987	android/view/InsetsController:onStateChanged	(Landroid/view/InsetsState;)Z
    //   823: pop
    //   824: iload 12
    //   826: ifge +565 -> 1391
    //   829: aload_0
    //   830: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   833: aconst_null
    //   834: putfield 3328	android/view/View$AttachInfo:mRootView	Landroid/view/View;
    //   837: aload_0
    //   838: iconst_0
    //   839: putfield 827	android/view/ViewRootImpl:mAdded	Z
    //   842: aload_0
    //   843: getfield 890	android/view/ViewRootImpl:mFallbackEventHandler	Landroid/view/FallbackEventHandler;
    //   846: aconst_null
    //   847: invokeinterface 4027 2 0
    //   852: aload_0
    //   853: invokevirtual 3360	android/view/ViewRootImpl:unscheduleTraversals	()V
    //   856: aload_0
    //   857: aconst_null
    //   858: aconst_null
    //   859: invokevirtual 3321	android/view/ViewRootImpl:setAccessibilityFocus	(Landroid/view/View;Landroid/view/accessibility/AccessibilityNodeInfo;)V
    //   862: iload 12
    //   864: tableswitch	default:+56->920, -10:+427->1291, -9:+380->1244, -8:+321->1185, -7:+254->1118, -6:+251->1115, -5:+204->1068, -4:+157->1021, -3:+110->974, -2:+63->927, -1:+63->927
    //   920: new 3576	java/lang/RuntimeException
    //   923: astore_2
    //   924: goto +434 -> 1358
    //   927: new 4062	android/view/WindowManager$BadTokenException
    //   930: astore_1
    //   931: new 1626	java/lang/StringBuilder
    //   934: astore_3
    //   935: aload_3
    //   936: invokespecial 1627	java/lang/StringBuilder:<init>	()V
    //   939: aload_3
    //   940: ldc_w 4064
    //   943: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   946: pop
    //   947: aload_3
    //   948: aload_2
    //   949: getfield 4067	android/view/WindowManager$LayoutParams:token	Landroid/os/IBinder;
    //   952: invokevirtual 1700	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   955: pop
    //   956: aload_3
    //   957: ldc_w 4069
    //   960: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   963: pop
    //   964: aload_1
    //   965: aload_3
    //   966: invokevirtual 1634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   969: invokespecial 4070	android/view/WindowManager$BadTokenException:<init>	(Ljava/lang/String;)V
    //   972: aload_1
    //   973: athrow
    //   974: new 4062	android/view/WindowManager$BadTokenException
    //   977: astore_3
    //   978: new 1626	java/lang/StringBuilder
    //   981: astore_1
    //   982: aload_1
    //   983: invokespecial 1627	java/lang/StringBuilder:<init>	()V
    //   986: aload_1
    //   987: ldc_w 4064
    //   990: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   993: pop
    //   994: aload_1
    //   995: aload_2
    //   996: getfield 4067	android/view/WindowManager$LayoutParams:token	Landroid/os/IBinder;
    //   999: invokevirtual 1700	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1002: pop
    //   1003: aload_1
    //   1004: ldc_w 4072
    //   1007: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1010: pop
    //   1011: aload_3
    //   1012: aload_1
    //   1013: invokevirtual 1634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1016: invokespecial 4070	android/view/WindowManager$BadTokenException:<init>	(Ljava/lang/String;)V
    //   1019: aload_3
    //   1020: athrow
    //   1021: new 4062	android/view/WindowManager$BadTokenException
    //   1024: astore_1
    //   1025: new 1626	java/lang/StringBuilder
    //   1028: astore_3
    //   1029: aload_3
    //   1030: invokespecial 1627	java/lang/StringBuilder:<init>	()V
    //   1033: aload_3
    //   1034: ldc_w 4074
    //   1037: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1040: pop
    //   1041: aload_3
    //   1042: aload_2
    //   1043: getfield 4067	android/view/WindowManager$LayoutParams:token	Landroid/os/IBinder;
    //   1046: invokevirtual 1700	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1049: pop
    //   1050: aload_3
    //   1051: ldc_w 4076
    //   1054: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1057: pop
    //   1058: aload_1
    //   1059: aload_3
    //   1060: invokevirtual 1634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1063: invokespecial 4070	android/view/WindowManager$BadTokenException:<init>	(Ljava/lang/String;)V
    //   1066: aload_1
    //   1067: athrow
    //   1068: new 4062	android/view/WindowManager$BadTokenException
    //   1071: astore_2
    //   1072: new 1626	java/lang/StringBuilder
    //   1075: astore_1
    //   1076: aload_1
    //   1077: invokespecial 1627	java/lang/StringBuilder:<init>	()V
    //   1080: aload_1
    //   1081: ldc_w 4078
    //   1084: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1087: pop
    //   1088: aload_1
    //   1089: aload_0
    //   1090: getfield 803	android/view/ViewRootImpl:mWindow	Landroid/view/ViewRootImpl$W;
    //   1093: invokevirtual 1700	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1096: pop
    //   1097: aload_1
    //   1098: ldc_w 4080
    //   1101: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1104: pop
    //   1105: aload_2
    //   1106: aload_1
    //   1107: invokevirtual 1634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1110: invokespecial 4070	android/view/WindowManager$BadTokenException:<init>	(Ljava/lang/String;)V
    //   1113: aload_2
    //   1114: athrow
    //   1115: aload_0
    //   1116: monitorexit
    //   1117: return
    //   1118: new 4062	android/view/WindowManager$BadTokenException
    //   1121: astore_2
    //   1122: new 1626	java/lang/StringBuilder
    //   1125: astore_1
    //   1126: aload_1
    //   1127: invokespecial 1627	java/lang/StringBuilder:<init>	()V
    //   1130: aload_1
    //   1131: ldc_w 4082
    //   1134: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1137: pop
    //   1138: aload_1
    //   1139: aload_0
    //   1140: getfield 803	android/view/ViewRootImpl:mWindow	Landroid/view/ViewRootImpl$W;
    //   1143: invokevirtual 1700	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1146: pop
    //   1147: aload_1
    //   1148: ldc_w 4084
    //   1151: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1154: pop
    //   1155: aload_1
    //   1156: aload_0
    //   1157: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   1160: getfield 2917	android/view/WindowManager$LayoutParams:type	I
    //   1163: invokevirtual 3128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1166: pop
    //   1167: aload_1
    //   1168: ldc_w 4086
    //   1171: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1174: pop
    //   1175: aload_2
    //   1176: aload_1
    //   1177: invokevirtual 1634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1180: invokespecial 4070	android/view/WindowManager$BadTokenException:<init>	(Ljava/lang/String;)V
    //   1183: aload_2
    //   1184: athrow
    //   1185: new 4062	android/view/WindowManager$BadTokenException
    //   1188: astore_2
    //   1189: new 1626	java/lang/StringBuilder
    //   1192: astore_1
    //   1193: aload_1
    //   1194: invokespecial 1627	java/lang/StringBuilder:<init>	()V
    //   1197: aload_1
    //   1198: ldc_w 4082
    //   1201: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1204: pop
    //   1205: aload_1
    //   1206: aload_0
    //   1207: getfield 803	android/view/ViewRootImpl:mWindow	Landroid/view/ViewRootImpl$W;
    //   1210: invokevirtual 1700	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1213: pop
    //   1214: aload_1
    //   1215: ldc_w 4088
    //   1218: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1221: pop
    //   1222: aload_1
    //   1223: aload_0
    //   1224: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   1227: getfield 2917	android/view/WindowManager$LayoutParams:type	I
    //   1230: invokevirtual 3128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1233: pop
    //   1234: aload_2
    //   1235: aload_1
    //   1236: invokevirtual 1634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1239: invokespecial 4070	android/view/WindowManager$BadTokenException:<init>	(Ljava/lang/String;)V
    //   1242: aload_2
    //   1243: athrow
    //   1244: new 4090	android/view/WindowManager$InvalidDisplayException
    //   1247: astore_2
    //   1248: new 1626	java/lang/StringBuilder
    //   1251: astore_1
    //   1252: aload_1
    //   1253: invokespecial 1627	java/lang/StringBuilder:<init>	()V
    //   1256: aload_1
    //   1257: ldc_w 4082
    //   1260: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1263: pop
    //   1264: aload_1
    //   1265: aload_0
    //   1266: getfield 803	android/view/ViewRootImpl:mWindow	Landroid/view/ViewRootImpl$W;
    //   1269: invokevirtual 1700	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1272: pop
    //   1273: aload_1
    //   1274: ldc_w 4092
    //   1277: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1280: pop
    //   1281: aload_2
    //   1282: aload_1
    //   1283: invokevirtual 1634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1286: invokespecial 4093	android/view/WindowManager$InvalidDisplayException:<init>	(Ljava/lang/String;)V
    //   1289: aload_2
    //   1290: athrow
    //   1291: new 4090	android/view/WindowManager$InvalidDisplayException
    //   1294: astore_2
    //   1295: new 1626	java/lang/StringBuilder
    //   1298: astore_1
    //   1299: aload_1
    //   1300: invokespecial 1627	java/lang/StringBuilder:<init>	()V
    //   1303: aload_1
    //   1304: ldc_w 4082
    //   1307: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1310: pop
    //   1311: aload_1
    //   1312: aload_0
    //   1313: getfield 803	android/view/ViewRootImpl:mWindow	Landroid/view/ViewRootImpl$W;
    //   1316: invokevirtual 1700	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1319: pop
    //   1320: aload_1
    //   1321: ldc_w 4095
    //   1324: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1327: pop
    //   1328: aload_1
    //   1329: aload_0
    //   1330: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   1333: getfield 2917	android/view/WindowManager$LayoutParams:type	I
    //   1336: invokevirtual 3128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1339: pop
    //   1340: aload_1
    //   1341: ldc_w 4097
    //   1344: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1347: pop
    //   1348: aload_2
    //   1349: aload_1
    //   1350: invokevirtual 1634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1353: invokespecial 4093	android/view/WindowManager$InvalidDisplayException:<init>	(Ljava/lang/String;)V
    //   1356: aload_2
    //   1357: athrow
    //   1358: new 1626	java/lang/StringBuilder
    //   1361: astore_1
    //   1362: aload_1
    //   1363: invokespecial 1627	java/lang/StringBuilder:<init>	()V
    //   1366: aload_1
    //   1367: ldc_w 4099
    //   1370: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1373: pop
    //   1374: aload_1
    //   1375: iload 12
    //   1377: invokevirtual 3128	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1380: pop
    //   1381: aload_2
    //   1382: aload_1
    //   1383: invokevirtual 1634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1386: invokespecial 3609	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   1389: aload_2
    //   1390: athrow
    //   1391: aload_1
    //   1392: instanceof 1404
    //   1395: ifeq +16 -> 1411
    //   1398: aload_0
    //   1399: aload_1
    //   1400: checkcast 1404	com/android/internal/view/RootViewSurfaceTaker
    //   1403: invokeinterface 3747 1 0
    //   1408: putfield 3330	android/view/ViewRootImpl:mInputQueueCallback	Landroid/view/InputQueue$Callback;
    //   1411: aload_0
    //   1412: getfield 555	android/view/ViewRootImpl:mWindowAttributes	Landroid/view/WindowManager$LayoutParams;
    //   1415: getfield 4055	android/view/WindowManager$LayoutParams:inputFeatures	I
    //   1418: iconst_2
    //   1419: iand
    //   1420: ifeq +8 -> 1428
    //   1423: aload_0
    //   1424: aconst_null
    //   1425: putfield 3350	android/view/ViewRootImpl:mInputChannel	Landroid/view/InputChannel;
    //   1428: aload_0
    //   1429: getfield 3350	android/view/ViewRootImpl:mInputChannel	Landroid/view/InputChannel;
    //   1432: ifnull +57 -> 1489
    //   1435: aload_0
    //   1436: getfield 3330	android/view/ViewRootImpl:mInputQueueCallback	Landroid/view/InputQueue$Callback;
    //   1439: ifnull +29 -> 1468
    //   1442: new 3340	android/view/InputQueue
    //   1445: astore_3
    //   1446: aload_3
    //   1447: invokespecial 4100	android/view/InputQueue:<init>	()V
    //   1450: aload_0
    //   1451: aload_3
    //   1452: putfield 3332	android/view/ViewRootImpl:mInputQueue	Landroid/view/InputQueue;
    //   1455: aload_0
    //   1456: getfield 3330	android/view/ViewRootImpl:mInputQueueCallback	Landroid/view/InputQueue$Callback;
    //   1459: aload_0
    //   1460: getfield 3332	android/view/ViewRootImpl:mInputQueue	Landroid/view/InputQueue;
    //   1463: invokeinterface 3750 2 0
    //   1468: new 121	android/view/ViewRootImpl$WindowInputEventReceiver
    //   1471: astore_3
    //   1472: aload_3
    //   1473: aload_0
    //   1474: aload_0
    //   1475: getfield 3350	android/view/ViewRootImpl:mInputChannel	Landroid/view/InputChannel;
    //   1478: invokestatic 3246	android/os/Looper:myLooper	()Landroid/os/Looper;
    //   1481: invokespecial 4103	android/view/ViewRootImpl$WindowInputEventReceiver:<init>	(Landroid/view/ViewRootImpl;Landroid/view/InputChannel;Landroid/os/Looper;)V
    //   1484: aload_0
    //   1485: aload_3
    //   1486: putfield 3345	android/view/ViewRootImpl:mInputEventReceiver	Landroid/view/ViewRootImpl$WindowInputEventReceiver;
    //   1489: aload_1
    //   1490: aload_0
    //   1491: invokevirtual 3325	android/view/View:assignParent	(Landroid/view/ViewParent;)V
    //   1494: iload 12
    //   1496: iconst_1
    //   1497: iand
    //   1498: ifeq +9 -> 1507
    //   1501: iconst_1
    //   1502: istore 5
    //   1504: goto +6 -> 1510
    //   1507: iconst_0
    //   1508: istore 5
    //   1510: aload_0
    //   1511: iload 5
    //   1513: putfield 2666	android/view/ViewRootImpl:mAddedTouchMode	Z
    //   1516: iload 12
    //   1518: iconst_2
    //   1519: iand
    //   1520: ifeq +9 -> 1529
    //   1523: iconst_1
    //   1524: istore 5
    //   1526: goto +6 -> 1532
    //   1529: iconst_0
    //   1530: istore 5
    //   1532: aload_0
    //   1533: iload 5
    //   1535: putfield 557	android/view/ViewRootImpl:mAppVisible	Z
    //   1538: aload_0
    //   1539: getfield 842	android/view/ViewRootImpl:mAccessibilityManager	Landroid/view/accessibility/AccessibilityManager;
    //   1542: invokevirtual 1845	android/view/accessibility/AccessibilityManager:isEnabled	()Z
    //   1545: ifeq +10 -> 1555
    //   1548: aload_0
    //   1549: getfield 692	android/view/ViewRootImpl:mAccessibilityInteractionConnectionManager	Landroid/view/ViewRootImpl$AccessibilityInteractionConnectionManager;
    //   1552: invokevirtual 4106	android/view/ViewRootImpl$AccessibilityInteractionConnectionManager:ensureConnection	()V
    //   1555: aload_1
    //   1556: invokevirtual 4109	android/view/View:getImportantForAccessibility	()I
    //   1559: ifne +8 -> 1567
    //   1562: aload_1
    //   1563: iconst_1
    //   1564: invokevirtual 4112	android/view/View:setImportantForAccessibility	(I)V
    //   1567: aload_2
    //   1568: invokevirtual 1677	android/view/WindowManager$LayoutParams:getTitle	()Ljava/lang/CharSequence;
    //   1571: astore_1
    //   1572: new 74	android/view/ViewRootImpl$SyntheticInputStage
    //   1575: astore_2
    //   1576: aload_2
    //   1577: aload_0
    //   1578: invokespecial 4113	android/view/ViewRootImpl$SyntheticInputStage:<init>	(Landroid/view/ViewRootImpl;)V
    //   1581: aload_0
    //   1582: aload_2
    //   1583: putfield 1203	android/view/ViewRootImpl:mSyntheticInputStage	Landroid/view/ViewRootImpl$InputStage;
    //   1586: new 109	android/view/ViewRootImpl$ViewPostImeInputStage
    //   1589: astore_2
    //   1590: aload_2
    //   1591: aload_0
    //   1592: aload_0
    //   1593: getfield 1203	android/view/ViewRootImpl:mSyntheticInputStage	Landroid/view/ViewRootImpl$InputStage;
    //   1596: invokespecial 4116	android/view/ViewRootImpl$ViewPostImeInputStage:<init>	(Landroid/view/ViewRootImpl;Landroid/view/ViewRootImpl$InputStage;)V
    //   1599: new 62	android/view/ViewRootImpl$NativePostImeInputStage
    //   1602: astore_3
    //   1603: new 1626	java/lang/StringBuilder
    //   1606: astore 4
    //   1608: aload 4
    //   1610: invokespecial 1627	java/lang/StringBuilder:<init>	()V
    //   1613: aload 4
    //   1615: ldc_w 4118
    //   1618: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1621: pop
    //   1622: aload 4
    //   1624: aload_1
    //   1625: invokevirtual 1700	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1628: pop
    //   1629: aload_3
    //   1630: aload_0
    //   1631: aload_2
    //   1632: aload 4
    //   1634: invokevirtual 1634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1637: invokespecial 4121	android/view/ViewRootImpl$NativePostImeInputStage:<init>	(Landroid/view/ViewRootImpl;Landroid/view/ViewRootImpl$InputStage;Ljava/lang/String;)V
    //   1640: new 47	android/view/ViewRootImpl$EarlyPostImeInputStage
    //   1643: astore_2
    //   1644: aload_2
    //   1645: aload_0
    //   1646: aload_3
    //   1647: invokespecial 4122	android/view/ViewRootImpl$EarlyPostImeInputStage:<init>	(Landroid/view/ViewRootImpl;Landroid/view/ViewRootImpl$InputStage;)V
    //   1650: new 53	android/view/ViewRootImpl$ImeInputStage
    //   1653: astore 4
    //   1655: new 1626	java/lang/StringBuilder
    //   1658: astore_3
    //   1659: aload_3
    //   1660: invokespecial 1627	java/lang/StringBuilder:<init>	()V
    //   1663: aload_3
    //   1664: ldc_w 4124
    //   1667: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1670: pop
    //   1671: aload_3
    //   1672: aload_1
    //   1673: invokevirtual 1700	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1676: pop
    //   1677: aload 4
    //   1679: aload_0
    //   1680: aload_2
    //   1681: aload_3
    //   1682: invokevirtual 1634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1685: invokespecial 4125	android/view/ViewRootImpl$ImeInputStage:<init>	(Landroid/view/ViewRootImpl;Landroid/view/ViewRootImpl$InputStage;Ljava/lang/String;)V
    //   1688: new 112	android/view/ViewRootImpl$ViewPreImeInputStage
    //   1691: astore_3
    //   1692: aload_3
    //   1693: aload_0
    //   1694: aload 4
    //   1696: invokespecial 4126	android/view/ViewRootImpl$ViewPreImeInputStage:<init>	(Landroid/view/ViewRootImpl;Landroid/view/ViewRootImpl$InputStage;)V
    //   1699: new 65	android/view/ViewRootImpl$NativePreImeInputStage
    //   1702: astore 4
    //   1704: new 1626	java/lang/StringBuilder
    //   1707: astore 8
    //   1709: aload 8
    //   1711: invokespecial 1627	java/lang/StringBuilder:<init>	()V
    //   1714: aload 8
    //   1716: ldc_w 4128
    //   1719: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1722: pop
    //   1723: aload 8
    //   1725: aload_1
    //   1726: invokevirtual 1700	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1729: pop
    //   1730: aload 4
    //   1732: aload_0
    //   1733: aload_3
    //   1734: aload 8
    //   1736: invokevirtual 1634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1739: invokespecial 4129	android/view/ViewRootImpl$NativePreImeInputStage:<init>	(Landroid/view/ViewRootImpl;Landroid/view/ViewRootImpl$InputStage;Ljava/lang/String;)V
    //   1742: aload_0
    //   1743: aload 4
    //   1745: putfield 1210	android/view/ViewRootImpl:mFirstInputStage	Landroid/view/ViewRootImpl$InputStage;
    //   1748: aload_0
    //   1749: aload_2
    //   1750: putfield 1208	android/view/ViewRootImpl:mFirstPostImeInputStage	Landroid/view/ViewRootImpl$InputStage;
    //   1753: new 1626	java/lang/StringBuilder
    //   1756: astore_2
    //   1757: aload_2
    //   1758: invokespecial 1627	java/lang/StringBuilder:<init>	()V
    //   1761: aload_2
    //   1762: ldc_w 4131
    //   1765: invokevirtual 1631	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1768: pop
    //   1769: aload_2
    //   1770: aload_1
    //   1771: invokevirtual 1700	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1774: pop
    //   1775: aload_0
    //   1776: aload_2
    //   1777: invokevirtual 1634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1780: putfield 588	android/view/ViewRootImpl:mPendingInputEventQueueLengthCounterName	Ljava/lang/String;
    //   1783: goto +97 -> 1880
    //   1786: astore_1
    //   1787: goto +97 -> 1884
    //   1790: astore_1
    //   1791: goto +74 -> 1865
    //   1794: astore_1
    //   1795: goto +12 -> 1807
    //   1798: astore_1
    //   1799: goto +8 -> 1807
    //   1802: astore_1
    //   1803: goto +62 -> 1865
    //   1806: astore_1
    //   1807: aload_0
    //   1808: iconst_0
    //   1809: putfield 827	android/view/ViewRootImpl:mAdded	Z
    //   1812: aload_0
    //   1813: aconst_null
    //   1814: putfield 1150	android/view/ViewRootImpl:mView	Landroid/view/View;
    //   1817: aload_0
    //   1818: getfield 834	android/view/ViewRootImpl:mAttachInfo	Landroid/view/View$AttachInfo;
    //   1821: aconst_null
    //   1822: putfield 3328	android/view/View$AttachInfo:mRootView	Landroid/view/View;
    //   1825: aload_0
    //   1826: aconst_null
    //   1827: putfield 3350	android/view/ViewRootImpl:mInputChannel	Landroid/view/InputChannel;
    //   1830: aload_0
    //   1831: getfield 890	android/view/ViewRootImpl:mFallbackEventHandler	Landroid/view/FallbackEventHandler;
    //   1834: aconst_null
    //   1835: invokeinterface 4027 2 0
    //   1840: aload_0
    //   1841: invokevirtual 3360	android/view/ViewRootImpl:unscheduleTraversals	()V
    //   1844: aload_0
    //   1845: aconst_null
    //   1846: aconst_null
    //   1847: invokevirtual 3321	android/view/ViewRootImpl:setAccessibilityFocus	(Landroid/view/View;Landroid/view/accessibility/AccessibilityNodeInfo;)V
    //   1850: new 3576	java/lang/RuntimeException
    //   1853: astore_3
    //   1854: aload_3
    //   1855: ldc_w 4133
    //   1858: aload_1
    //   1859: invokespecial 4136	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1862: aload_3
    //   1863: athrow
    //   1864: astore_1
    //   1865: iload 6
    //   1867: ifeq +7 -> 1874
    //   1870: aload_2
    //   1871: invokevirtual 2983	android/view/WindowManager$LayoutParams:restore	()V
    //   1874: aload_1
    //   1875: athrow
    //   1876: astore_1
    //   1877: goto +7 -> 1884
    //   1880: aload_0
    //   1881: monitorexit
    //   1882: return
    //   1883: astore_1
    //   1884: aload_0
    //   1885: monitorexit
    //   1886: aload_1
    //   1887: athrow
    //   1888: astore_1
    //   1889: goto -5 -> 1884
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1892	0	this	ViewRootImpl
    //   0	1892	1	paramView1	View
    //   0	1892	2	paramLayoutParams	WindowManager.LayoutParams
    //   0	1892	3	paramView2	View
    //   68	1676	4	localObject1	Object
    //   249	1285	5	bool	boolean
    //   307	1559	6	i	int
    //   409	17	7	f	float
    //   556	1179	8	localObject2	Object
    //   562	79	9	localW	W
    //   568	75	10	j	int
    //   574	71	11	localLayoutParams	WindowManager.LayoutParams
    //   580	940	12	k	int
    //   589	60	13	m	int
    //   603	49	14	localRect1	Rect
    //   612	42	15	localRect2	Rect
    //   621	35	16	localRect3	Rect
    //   636	24	17	localInputChannel	InputChannel
    // Exception table:
    //   from	to	target	type
    //   685	689	1786	finally
    //   689	710	1786	finally
    //   710	779	1786	finally
    //   795	824	1786	finally
    //   829	862	1786	finally
    //   920	924	1786	finally
    //   927	974	1786	finally
    //   974	1021	1786	finally
    //   1021	1068	1786	finally
    //   1068	1115	1786	finally
    //   1115	1117	1786	finally
    //   1118	1185	1786	finally
    //   1185	1244	1786	finally
    //   1244	1291	1786	finally
    //   1291	1358	1786	finally
    //   1358	1391	1786	finally
    //   638	680	1790	finally
    //   638	680	1794	android/os/RemoteException
    //   632	638	1798	android/os/RemoteException
    //   528	632	1802	finally
    //   528	632	1806	android/os/RemoteException
    //   632	638	1864	finally
    //   1807	1864	1864	finally
    //   70	98	1876	finally
    //   98	103	1876	finally
    //   2	70	1883	finally
    //   103	184	1888	finally
    //   184	198	1888	finally
    //   198	248	1888	finally
    //   257	276	1888	finally
    //   276	306	1888	finally
    //   315	340	1888	finally
    //   340	379	1888	finally
    //   388	408	1888	finally
    //   414	423	1888	finally
    //   423	430	1888	finally
    //   434	445	1888	finally
    //   445	479	1888	finally
    //   479	493	1888	finally
    //   502	528	1888	finally
    //   1391	1411	1888	finally
    //   1411	1428	1888	finally
    //   1428	1468	1888	finally
    //   1468	1489	1888	finally
    //   1489	1494	1888	finally
    //   1510	1516	1888	finally
    //   1532	1555	1888	finally
    //   1555	1567	1888	finally
    //   1567	1783	1888	finally
    //   1870	1874	1888	finally
    //   1874	1876	1888	finally
    //   1880	1882	1888	finally
    //   1884	1886	1888	finally
  }
  
  void setWindowStopped(boolean paramBoolean)
  {
    checkThread();
    if (this.mStopped != paramBoolean)
    {
      this.mStopped = paramBoolean;
      ThreadedRenderer localThreadedRenderer = this.mAttachInfo.mThreadedRenderer;
      if (localThreadedRenderer != null) {
        localThreadedRenderer.setStopped(this.mStopped);
      }
      if (!this.mStopped)
      {
        this.mNewSurfaceNeeded = true;
        scheduleTraversals();
      }
      else if (localThreadedRenderer != null)
      {
        localThreadedRenderer.destroyHardwareResources(this.mView);
      }
      for (int i = 0; i < this.mWindowStoppedCallbacks.size(); i++) {
        ((WindowStoppedCallback)this.mWindowStoppedCallbacks.get(i)).windowStopped(paramBoolean);
      }
      if (this.mStopped)
      {
        if ((this.mSurfaceHolder != null) && (this.mSurface.isValid())) {
          notifySurfaceDestroyed();
        }
        destroySurface();
      }
    }
  }
  
  public boolean showContextMenuForChild(View paramView)
  {
    return false;
  }
  
  public boolean showContextMenuForChild(View paramView, float paramFloat1, float paramFloat2)
  {
    return false;
  }
  
  public ActionMode startActionModeForChild(View paramView, ActionMode.Callback paramCallback)
  {
    return null;
  }
  
  public ActionMode startActionModeForChild(View paramView, ActionMode.Callback paramCallback, int paramInt)
  {
    return null;
  }
  
  public void synthesizeInputEvent(InputEvent paramInputEvent)
  {
    paramInputEvent = this.mHandler.obtainMessage(24, paramInputEvent);
    paramInputEvent.setAsynchronous(true);
    this.mHandler.sendMessage(paramInputEvent);
  }
  
  void systemGestureExclusionChanged()
  {
    List localList = this.mGestureExclusionTracker.computeChangedRects();
    if ((localList != null) && (this.mView != null)) {
      try
      {
        this.mWindowSession.reportSystemGestureExclusionChanged(this.mWindow, localList);
        this.mAttachInfo.mTreeObserver.dispatchOnSystemGestureExclusionRectsChanged(localList);
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
  }
  
  void transformMatrixToGlobal(Matrix paramMatrix)
  {
    paramMatrix.preTranslate(this.mAttachInfo.mWindowLeft, this.mAttachInfo.mWindowTop);
  }
  
  void transformMatrixToLocal(Matrix paramMatrix)
  {
    paramMatrix.postTranslate(-this.mAttachInfo.mWindowLeft, -this.mAttachInfo.mWindowTop);
  }
  
  void unscheduleConsumeBatchedInput()
  {
    if (this.mConsumeBatchedInputScheduled)
    {
      this.mConsumeBatchedInputScheduled = false;
      this.mChoreographer.removeCallbacks(0, this.mConsumedBatchedInputRunnable, null);
    }
  }
  
  void unscheduleTraversals()
  {
    if (this.mTraversalScheduled)
    {
      this.mTraversalScheduled = false;
      this.mHandler.getLooper().getQueue().removeSyncBarrier(this.mTraversalBarrier);
      this.mChoreographer.removeCallbacks(3, this.mTraversalRunnable, null);
    }
  }
  
  void updateBlurCrop(WindowManager.LayoutParams paramLayoutParams)
  {
    if (this.mSurfaceControl.isValid()) {
      SurfaceControl.openTransaction();
    }
    try
    {
      this.mNeedUpdateBlurCrop = false;
      this.mSurfaceControl.setBlurCrop(paramLayoutParams.blurRelativeCrop, paramLayoutParams.blurAbsoluteCrop);
      SurfaceControl.closeTransaction();
    }
    finally
    {
      SurfaceControl.closeTransaction();
    }
  }
  
  public void updateConfiguration(int paramInt)
  {
    Object localObject = this.mView;
    if (localObject == null) {
      return;
    }
    Resources localResources = ((View)localObject).getResources();
    localObject = localResources.getConfiguration();
    if (paramInt != -1) {
      onMovedToDisplay(paramInt, (Configuration)localObject);
    }
    if ((this.mForceNextConfigUpdate) || (this.mLastConfigurationFromResources.diff((Configuration)localObject) != 0))
    {
      updateInternalDisplay(this.mDisplay.getDisplayId(), localResources);
      int i = this.mLastConfigurationFromResources.getLayoutDirection();
      paramInt = ((Configuration)localObject).getLayoutDirection();
      this.mLastConfigurationFromResources.setTo((Configuration)localObject);
      if ((i != paramInt) && (this.mViewLayoutDirectionInitial == 2)) {
        this.mView.setLayoutDirection(paramInt);
      }
      this.mView.dispatchConfigurationChanged((Configuration)localObject);
      this.mForceNextWindowRelayout = true;
      requestLayout();
    }
    ForceDarkHelper.getInstance().updateForceDarkMode(this);
  }
  
  public void updatePointerIcon(float paramFloat1, float paramFloat2)
  {
    this.mHandler.removeMessages(27);
    Object localObject = MotionEvent.obtain(0L, SystemClock.uptimeMillis(), 7, paramFloat1, paramFloat2, 0);
    localObject = this.mHandler.obtainMessage(27, localObject);
    this.mHandler.sendMessage((Message)localObject);
  }
  
  void updateSystemGestureExclusionRectsForView(View paramView)
  {
    this.mGestureExclusionTracker.updateRectsForView(paramView);
    this.mHandler.sendEmptyMessage(32);
  }
  
  public void windowFocusChanged(boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      this.mWindowFocusChanged = true;
      this.mUpcomingWindowFocus = paramBoolean1;
      this.mUpcomingInTouchMode = paramBoolean2;
      Message localMessage = Message.obtain();
      localMessage.what = 6;
      this.mHandler.sendMessage(localMessage);
      return;
    }
    finally {}
  }
  
  static final class AccessibilityInteractionConnection
    extends IAccessibilityInteractionConnection.Stub
  {
    private final WeakReference<ViewRootImpl> mViewRootImpl;
    
    AccessibilityInteractionConnection(ViewRootImpl paramViewRootImpl)
    {
      this.mViewRootImpl = new WeakReference(paramViewRootImpl);
    }
    
    public void clearAccessibilityFocus()
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewRootImpl.get();
      if ((localViewRootImpl != null) && (localViewRootImpl.mView != null)) {
        localViewRootImpl.getAccessibilityInteractionController().clearAccessibilityFocusClientThread();
      }
    }
    
    public void findAccessibilityNodeInfoByAccessibilityId(long paramLong1, Region paramRegion, int paramInt1, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt2, int paramInt3, long paramLong2, MagnificationSpec paramMagnificationSpec, Bundle paramBundle)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewRootImpl.get();
      if ((localViewRootImpl != null) && (localViewRootImpl.mView != null)) {
        localViewRootImpl.getAccessibilityInteractionController().findAccessibilityNodeInfoByAccessibilityIdClientThread(paramLong1, paramRegion, paramInt1, paramIAccessibilityInteractionConnectionCallback, paramInt2, paramInt3, paramLong2, paramMagnificationSpec, paramBundle);
      } else {
        try
        {
          paramIAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfosResult(null, paramInt1);
        }
        catch (RemoteException paramRegion) {}
      }
    }
    
    public void findAccessibilityNodeInfosByText(long paramLong1, String paramString, Region paramRegion, int paramInt1, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt2, int paramInt3, long paramLong2, MagnificationSpec paramMagnificationSpec)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewRootImpl.get();
      if ((localViewRootImpl != null) && (localViewRootImpl.mView != null)) {
        localViewRootImpl.getAccessibilityInteractionController().findAccessibilityNodeInfosByTextClientThread(paramLong1, paramString, paramRegion, paramInt1, paramIAccessibilityInteractionConnectionCallback, paramInt2, paramInt3, paramLong2, paramMagnificationSpec);
      } else {
        try
        {
          paramIAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfosResult(null, paramInt1);
        }
        catch (RemoteException paramString) {}
      }
    }
    
    public void findAccessibilityNodeInfosByViewId(long paramLong1, String paramString, Region paramRegion, int paramInt1, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt2, int paramInt3, long paramLong2, MagnificationSpec paramMagnificationSpec)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewRootImpl.get();
      if ((localViewRootImpl != null) && (localViewRootImpl.mView != null)) {
        localViewRootImpl.getAccessibilityInteractionController().findAccessibilityNodeInfosByViewIdClientThread(paramLong1, paramString, paramRegion, paramInt1, paramIAccessibilityInteractionConnectionCallback, paramInt2, paramInt3, paramLong2, paramMagnificationSpec);
      } else {
        try
        {
          paramIAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult(null, paramInt1);
        }
        catch (RemoteException paramString) {}
      }
    }
    
    public void findFocus(long paramLong1, int paramInt1, Region paramRegion, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, int paramInt4, long paramLong2, MagnificationSpec paramMagnificationSpec)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewRootImpl.get();
      if ((localViewRootImpl != null) && (localViewRootImpl.mView != null)) {
        localViewRootImpl.getAccessibilityInteractionController().findFocusClientThread(paramLong1, paramInt1, paramRegion, paramInt2, paramIAccessibilityInteractionConnectionCallback, paramInt3, paramInt4, paramLong2, paramMagnificationSpec);
      } else {
        try
        {
          paramIAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult(null, paramInt2);
        }
        catch (RemoteException paramRegion) {}
      }
    }
    
    public void focusSearch(long paramLong1, int paramInt1, Region paramRegion, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, int paramInt4, long paramLong2, MagnificationSpec paramMagnificationSpec)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewRootImpl.get();
      if ((localViewRootImpl != null) && (localViewRootImpl.mView != null)) {
        localViewRootImpl.getAccessibilityInteractionController().focusSearchClientThread(paramLong1, paramInt1, paramRegion, paramInt2, paramIAccessibilityInteractionConnectionCallback, paramInt3, paramInt4, paramLong2, paramMagnificationSpec);
      } else {
        try
        {
          paramIAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult(null, paramInt2);
        }
        catch (RemoteException paramRegion) {}
      }
    }
    
    public void notifyOutsideTouch()
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewRootImpl.get();
      if ((localViewRootImpl != null) && (localViewRootImpl.mView != null)) {
        localViewRootImpl.getAccessibilityInteractionController().notifyOutsideTouchClientThread();
      }
    }
    
    public void performAccessibilityAction(long paramLong1, int paramInt1, Bundle paramBundle, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, int paramInt4, long paramLong2)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewRootImpl.get();
      if ((localViewRootImpl != null) && (localViewRootImpl.mView != null)) {
        localViewRootImpl.getAccessibilityInteractionController().performAccessibilityActionClientThread(paramLong1, paramInt1, paramBundle, paramInt2, paramIAccessibilityInteractionConnectionCallback, paramInt3, paramInt4, paramLong2);
      } else {
        try
        {
          paramIAccessibilityInteractionConnectionCallback.setPerformAccessibilityActionResult(false, paramInt2);
        }
        catch (RemoteException paramBundle) {}
      }
    }
  }
  
  final class AccessibilityInteractionConnectionManager
    implements AccessibilityManager.AccessibilityStateChangeListener
  {
    AccessibilityInteractionConnectionManager() {}
    
    public void ensureConnection()
    {
      int i;
      if (ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId != -1) {
        i = 1;
      } else {
        i = 0;
      }
      if (i == 0) {
        ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId = ViewRootImpl.this.mAccessibilityManager.addAccessibilityInteractionConnection(ViewRootImpl.this.mWindow, ViewRootImpl.this.mContext.getPackageName(), new ViewRootImpl.AccessibilityInteractionConnection(ViewRootImpl.this));
      }
    }
    
    public void ensureNoConnection()
    {
      int i;
      if (ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId != -1) {
        i = 1;
      } else {
        i = 0;
      }
      if (i != 0)
      {
        ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId = -1;
        ViewRootImpl.this.mAccessibilityManager.removeAccessibilityInteractionConnection(ViewRootImpl.this.mWindow);
      }
    }
    
    public void onAccessibilityStateChanged(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        ensureConnection();
        if ((ViewRootImpl.this.mAttachInfo.mHasWindowFocus) && (ViewRootImpl.this.mView != null))
        {
          ViewRootImpl.this.mView.sendAccessibilityEvent(32);
          View localView = ViewRootImpl.this.mView.findFocus();
          if ((localView != null) && (localView != ViewRootImpl.this.mView)) {
            localView.sendAccessibilityEvent(8);
          }
        }
      }
      else
      {
        ensureNoConnection();
        ViewRootImpl.this.mHandler.obtainMessage(21).sendToTarget();
      }
    }
  }
  
  public static abstract interface ActivityConfigCallback
  {
    public abstract void onConfigurationChanged(Configuration paramConfiguration, int paramInt);
  }
  
  abstract class AsyncInputStage
    extends ViewRootImpl.InputStage
  {
    protected static final int DEFER = 3;
    private ViewRootImpl.QueuedInputEvent mQueueHead;
    private int mQueueLength;
    private ViewRootImpl.QueuedInputEvent mQueueTail;
    private final String mTraceCounter;
    
    public AsyncInputStage(ViewRootImpl.InputStage paramInputStage, String paramString)
    {
      super(paramInputStage);
      this.mTraceCounter = paramString;
    }
    
    private void dequeue(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent1, ViewRootImpl.QueuedInputEvent paramQueuedInputEvent2)
    {
      if (paramQueuedInputEvent2 == null) {
        this.mQueueHead = paramQueuedInputEvent1.mNext;
      } else {
        paramQueuedInputEvent2.mNext = paramQueuedInputEvent1.mNext;
      }
      if (this.mQueueTail == paramQueuedInputEvent1) {
        this.mQueueTail = paramQueuedInputEvent2;
      }
      paramQueuedInputEvent1.mNext = null;
      this.mQueueLength -= 1;
      Trace.traceCounter(4L, this.mTraceCounter, this.mQueueLength);
    }
    
    private void enqueue(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      ViewRootImpl.QueuedInputEvent localQueuedInputEvent = this.mQueueTail;
      if (localQueuedInputEvent == null)
      {
        this.mQueueHead = paramQueuedInputEvent;
        this.mQueueTail = paramQueuedInputEvent;
      }
      else
      {
        localQueuedInputEvent.mNext = paramQueuedInputEvent;
        this.mQueueTail = paramQueuedInputEvent;
      }
      this.mQueueLength += 1;
      Trace.traceCounter(4L, this.mTraceCounter, this.mQueueLength);
    }
    
    protected void apply(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent, int paramInt)
    {
      if (paramInt == 3) {
        defer(paramQueuedInputEvent);
      } else {
        super.apply(paramQueuedInputEvent, paramInt);
      }
    }
    
    protected void defer(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      paramQueuedInputEvent.mFlags |= 0x2;
      enqueue(paramQueuedInputEvent);
    }
    
    void dump(String paramString, PrintWriter paramPrintWriter)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(getClass().getName());
      paramPrintWriter.print(": mQueueLength=");
      paramPrintWriter.println(this.mQueueLength);
      super.dump(paramString, paramPrintWriter);
    }
    
    protected void forward(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      paramQueuedInputEvent.mFlags &= 0xFFFFFFFD;
      ViewRootImpl.QueuedInputEvent localQueuedInputEvent1 = this.mQueueHead;
      if (localQueuedInputEvent1 == null)
      {
        super.forward(paramQueuedInputEvent);
        return;
      }
      int i = paramQueuedInputEvent.mEvent.getDeviceId();
      ViewRootImpl.QueuedInputEvent localQueuedInputEvent2 = null;
      int k;
      for (int j = 0; (localQueuedInputEvent1 != null) && (localQueuedInputEvent1 != paramQueuedInputEvent); j = k)
      {
        k = j;
        if (j == 0)
        {
          k = j;
          if (i == localQueuedInputEvent1.mEvent.getDeviceId()) {
            k = 1;
          }
        }
        localQueuedInputEvent2 = localQueuedInputEvent1;
        localQueuedInputEvent1 = localQueuedInputEvent1.mNext;
      }
      if (j != 0)
      {
        if (localQueuedInputEvent1 == null) {
          enqueue(paramQueuedInputEvent);
        }
        return;
      }
      ViewRootImpl.QueuedInputEvent localQueuedInputEvent3 = localQueuedInputEvent1;
      if (localQueuedInputEvent1 != null)
      {
        localQueuedInputEvent3 = localQueuedInputEvent1.mNext;
        dequeue(paramQueuedInputEvent, localQueuedInputEvent2);
      }
      super.forward(paramQueuedInputEvent);
      paramQueuedInputEvent = localQueuedInputEvent3;
      while (paramQueuedInputEvent != null) {
        if (i == paramQueuedInputEvent.mEvent.getDeviceId())
        {
          if ((paramQueuedInputEvent.mFlags & 0x2) != 0) {
            break;
          }
          localQueuedInputEvent1 = paramQueuedInputEvent.mNext;
          dequeue(paramQueuedInputEvent, localQueuedInputEvent2);
          super.forward(paramQueuedInputEvent);
          paramQueuedInputEvent = localQueuedInputEvent1;
        }
        else
        {
          localQueuedInputEvent2 = paramQueuedInputEvent;
          paramQueuedInputEvent = paramQueuedInputEvent.mNext;
        }
      }
    }
  }
  
  public static final class CalledFromWrongThreadException
    extends AndroidRuntimeException
  {
    @UnsupportedAppUsage
    public CalledFromWrongThreadException(String paramString)
    {
      super();
    }
  }
  
  static abstract interface CastProjectionCallback
  {
    public abstract void castModeChanged();
  }
  
  public static abstract interface ConfigChangedCallback
  {
    public abstract void onConfigurationChanged(Configuration paramConfiguration);
  }
  
  final class ConsumeBatchedInputImmediatelyRunnable
    implements Runnable
  {
    ConsumeBatchedInputImmediatelyRunnable() {}
    
    public void run()
    {
      ViewRootImpl.this.doConsumeBatchedInput(-1L);
    }
  }
  
  final class ConsumeBatchedInputRunnable
    implements Runnable
  {
    ConsumeBatchedInputRunnable() {}
    
    public void run()
    {
      ViewRootImpl localViewRootImpl = ViewRootImpl.this;
      localViewRootImpl.doConsumeBatchedInput(localViewRootImpl.mChoreographer.getFrameTimeNanos());
    }
  }
  
  final class EarlyPostImeInputStage
    extends ViewRootImpl.InputStage
  {
    public EarlyPostImeInputStage(ViewRootImpl.InputStage paramInputStage)
    {
      super(paramInputStage);
    }
    
    private int processKeyEvent(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      paramQueuedInputEvent = (KeyEvent)paramQueuedInputEvent.mEvent;
      if (ViewRootImpl.this.mAttachInfo.mTooltipHost != null) {
        ViewRootImpl.this.mAttachInfo.mTooltipHost.handleTooltipKey(paramQueuedInputEvent);
      }
      if (ViewRootImpl.this.checkForLeavingTouchModeAndConsume(paramQueuedInputEvent)) {
        return 1;
      }
      ViewRootImpl.this.mFallbackEventHandler.preDispatchKeyEvent(paramQueuedInputEvent);
      return 0;
    }
    
    private int processMotionEvent(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      MotionEvent localMotionEvent = (MotionEvent)paramQueuedInputEvent.mEvent;
      if (localMotionEvent.isFromSource(2)) {
        return processPointerEvent(paramQueuedInputEvent);
      }
      int i = localMotionEvent.getActionMasked();
      if (((i == 0) || (i == 8)) && (localMotionEvent.isFromSource(8))) {
        ViewRootImpl.this.ensureTouchMode(false);
      }
      return 0;
    }
    
    private int processPointerEvent(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      paramQueuedInputEvent = (MotionEvent)paramQueuedInputEvent.mEvent;
      if (ViewRootImpl.this.mTranslator != null) {
        ViewRootImpl.this.mTranslator.translateEventInScreenToAppWindow(paramQueuedInputEvent);
      }
      int i = paramQueuedInputEvent.getAction();
      if ((i == 0) || (i == 8)) {
        ViewRootImpl.this.ensureTouchMode(paramQueuedInputEvent.isFromSource(4098));
      }
      if (i == 0)
      {
        AutofillManager localAutofillManager = ViewRootImpl.this.getAutofillManager();
        if (localAutofillManager != null) {
          localAutofillManager.requestHideFillUi();
        }
      }
      if ((i == 0) && (ViewRootImpl.this.mAttachInfo.mTooltipHost != null)) {
        ViewRootImpl.this.mAttachInfo.mTooltipHost.hideTooltip();
      }
      if (ViewRootImpl.this.mCurScrollY != 0) {
        paramQueuedInputEvent.offsetLocation(0.0F, ViewRootImpl.this.mCurScrollY);
      }
      if (paramQueuedInputEvent.isTouchEvent())
      {
        ViewRootImpl.this.mLastTouchPoint.x = paramQueuedInputEvent.getRawX();
        ViewRootImpl.this.mLastTouchPoint.y = paramQueuedInputEvent.getRawY();
        ViewRootImpl.this.mLastTouchSource = paramQueuedInputEvent.getSource();
      }
      return 0;
    }
    
    protected int onProcess(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      if ((paramQueuedInputEvent.mEvent instanceof KeyEvent)) {
        return processKeyEvent(paramQueuedInputEvent);
      }
      if ((paramQueuedInputEvent.mEvent instanceof MotionEvent)) {
        return processMotionEvent(paramQueuedInputEvent);
      }
      return 0;
    }
  }
  
  final class HighContrastTextManager
    implements AccessibilityManager.HighTextContrastChangeListener
  {
    HighContrastTextManager()
    {
      ThreadedRenderer.setHighContrastText(ViewRootImpl.this.mAccessibilityManager.isHighTextContrastEnabled());
    }
    
    public void onHighTextContrastStateChanged(boolean paramBoolean)
    {
      ThreadedRenderer.setHighContrastText(paramBoolean);
      ViewRootImpl.this.destroyHardwareResources();
      ViewRootImpl.this.invalidate();
    }
  }
  
  final class ImeInputStage
    extends ViewRootImpl.AsyncInputStage
    implements InputMethodManager.FinishedInputEventCallback
  {
    public ImeInputStage(ViewRootImpl.InputStage paramInputStage, String paramString)
    {
      super(paramInputStage, paramString);
    }
    
    public void onFinishedInputEvent(Object paramObject, boolean paramBoolean)
    {
      paramObject = (ViewRootImpl.QueuedInputEvent)paramObject;
      if (paramBoolean)
      {
        finish((ViewRootImpl.QueuedInputEvent)paramObject, true);
        return;
      }
      forward((ViewRootImpl.QueuedInputEvent)paramObject);
    }
    
    protected int onProcess(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      if ((ViewRootImpl.this.mLastWasImTarget) && (!ViewRootImpl.this.isInLocalFocusMode()))
      {
        InputMethodManager localInputMethodManager = (InputMethodManager)ViewRootImpl.this.mContext.getSystemService(InputMethodManager.class);
        if (localInputMethodManager != null)
        {
          int i = localInputMethodManager.dispatchInputEvent(paramQueuedInputEvent.mEvent, paramQueuedInputEvent, this, ViewRootImpl.this.mHandler);
          if (i == 1) {
            return 1;
          }
          if (i == 0) {
            return 0;
          }
          return 3;
        }
      }
      return 0;
    }
  }
  
  abstract class InputStage
  {
    protected static final int FINISH_HANDLED = 1;
    protected static final int FINISH_NOT_HANDLED = 2;
    protected static final int FORWARD = 0;
    private final InputStage mNext;
    
    public InputStage(InputStage paramInputStage)
    {
      this.mNext = paramInputStage;
    }
    
    private boolean isBack(InputEvent paramInputEvent)
    {
      boolean bool1 = paramInputEvent instanceof KeyEvent;
      boolean bool2 = false;
      if (bool1)
      {
        if (((KeyEvent)paramInputEvent).getKeyCode() == 4) {
          bool2 = true;
        }
        return bool2;
      }
      return false;
    }
    
    protected void apply(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent, int paramInt)
    {
      if (paramInt == 0)
      {
        forward(paramQueuedInputEvent);
      }
      else if (paramInt == 1)
      {
        finish(paramQueuedInputEvent, true);
      }
      else
      {
        if (paramInt != 2) {
          break label38;
        }
        finish(paramQueuedInputEvent, false);
      }
      return;
      label38:
      paramQueuedInputEvent = new StringBuilder();
      paramQueuedInputEvent.append("Invalid result: ");
      paramQueuedInputEvent.append(paramInt);
      throw new IllegalArgumentException(paramQueuedInputEvent.toString());
    }
    
    public final void deliver(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      if ((paramQueuedInputEvent.mFlags & 0x4) != 0) {
        forward(paramQueuedInputEvent);
      } else if (shouldDropInputEvent(paramQueuedInputEvent)) {
        finish(paramQueuedInputEvent, false);
      } else {
        apply(paramQueuedInputEvent, onProcess(paramQueuedInputEvent));
      }
    }
    
    void dump(String paramString, PrintWriter paramPrintWriter)
    {
      InputStage localInputStage = this.mNext;
      if (localInputStage != null) {
        localInputStage.dump(paramString, paramPrintWriter);
      }
    }
    
    protected void finish(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent, boolean paramBoolean)
    {
      paramQueuedInputEvent.mFlags |= 0x4;
      if (paramBoolean) {
        paramQueuedInputEvent.mFlags |= 0x8;
      }
      forward(paramQueuedInputEvent);
    }
    
    protected void forward(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      onDeliverToNext(paramQueuedInputEvent);
    }
    
    protected void onDeliverToNext(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      InputStage localInputStage = this.mNext;
      if (localInputStage != null) {
        localInputStage.deliver(paramQueuedInputEvent);
      } else {
        ViewRootImpl.this.finishInputEvent(paramQueuedInputEvent);
      }
    }
    
    protected void onDetachedFromWindow()
    {
      InputStage localInputStage = this.mNext;
      if (localInputStage != null) {
        localInputStage.onDetachedFromWindow();
      }
    }
    
    protected int onProcess(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      return 0;
    }
    
    protected void onWindowFocusChanged(boolean paramBoolean)
    {
      InputStage localInputStage = this.mNext;
      if (localInputStage != null) {
        localInputStage.onWindowFocusChanged(paramBoolean);
      }
    }
    
    protected boolean shouldDropInputEvent(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      if ((ViewRootImpl.this.mView != null) && (ViewRootImpl.this.mAdded))
      {
        if (((ViewRootImpl.this.mAttachInfo.mHasWindowFocus) || (paramQueuedInputEvent.mEvent.isFromSource(2)) || (ViewRootImpl.this.isAutofillUiShowing())) && (!ViewRootImpl.this.mStopped) && ((!ViewRootImpl.this.mIsAmbientMode) || (paramQueuedInputEvent.mEvent.isFromSource(1))) && ((!ViewRootImpl.this.mPausedForTransition) || (isBack(paramQueuedInputEvent.mEvent)))) {
          return false;
        }
        if (ViewRootImpl.isTerminalInputEvent(paramQueuedInputEvent.mEvent))
        {
          paramQueuedInputEvent.mEvent.cancel();
          localObject1 = ViewRootImpl.this.mTag;
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("Cancelling event due to no window focus: ");
          ((StringBuilder)localObject2).append(paramQueuedInputEvent.mEvent);
          Slog.w((String)localObject1, ((StringBuilder)localObject2).toString());
          return false;
        }
        localObject2 = ViewRootImpl.this.mTag;
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("Dropping event due to no window focus: ");
        ((StringBuilder)localObject1).append(paramQueuedInputEvent.mEvent);
        Slog.w((String)localObject2, ((StringBuilder)localObject1).toString());
        return true;
      }
      Object localObject2 = ViewRootImpl.this.mTag;
      Object localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("Dropping event due to root view being removed: ");
      ((StringBuilder)localObject1).append(paramQueuedInputEvent.mEvent);
      Slog.w((String)localObject2, ((StringBuilder)localObject1).toString());
      return true;
    }
  }
  
  final class InvalidateOnAnimationRunnable
    implements Runnable
  {
    private boolean mPosted;
    private View.AttachInfo.InvalidateInfo[] mTempViewRects;
    private View[] mTempViews;
    private final ArrayList<View.AttachInfo.InvalidateInfo> mViewRects = new ArrayList();
    private final ArrayList<View> mViews = new ArrayList();
    
    InvalidateOnAnimationRunnable() {}
    
    private void postIfNeededLocked()
    {
      if (!this.mPosted)
      {
        ViewRootImpl.this.mChoreographer.postCallback(1, this, null);
        this.mPosted = true;
      }
    }
    
    public void addView(View paramView)
    {
      try
      {
        this.mViews.add(paramView);
        postIfNeededLocked();
        return;
      }
      finally {}
    }
    
    public void addViewRect(View.AttachInfo.InvalidateInfo paramInvalidateInfo)
    {
      try
      {
        this.mViewRects.add(paramInvalidateInfo);
        postIfNeededLocked();
        return;
      }
      finally {}
    }
    
    public void removeView(View paramView)
    {
      try
      {
        this.mViews.remove(paramView);
        int j;
        for (int i = this.mViewRects.size();; i = j)
        {
          j = i - 1;
          if (i <= 0) {
            break;
          }
          View.AttachInfo.InvalidateInfo localInvalidateInfo = (View.AttachInfo.InvalidateInfo)this.mViewRects.get(j);
          if (localInvalidateInfo.target == paramView)
          {
            this.mViewRects.remove(j);
            localInvalidateInfo.recycle();
          }
        }
        if ((this.mPosted) && (this.mViews.isEmpty()) && (this.mViewRects.isEmpty()))
        {
          ViewRootImpl.this.mChoreographer.removeCallbacks(1, this, null);
          this.mPosted = false;
        }
        return;
      }
      finally {}
    }
    
    public void run()
    {
      try
      {
        this.mPosted = false;
        int i = this.mViews.size();
        ArrayList localArrayList;
        Object localObject1;
        if (i != 0)
        {
          localArrayList = this.mViews;
          if (this.mTempViews != null) {
            localObject1 = this.mTempViews;
          } else {
            localObject1 = new View[i];
          }
          this.mTempViews = ((View[])localArrayList.toArray((Object[])localObject1));
          this.mViews.clear();
        }
        int j = this.mViewRects.size();
        if (j != 0)
        {
          localArrayList = this.mViewRects;
          if (this.mTempViewRects != null) {
            localObject1 = this.mTempViewRects;
          } else {
            localObject1 = new View.AttachInfo.InvalidateInfo[j];
          }
          this.mTempViewRects = ((View.AttachInfo.InvalidateInfo[])localArrayList.toArray((Object[])localObject1));
          this.mViewRects.clear();
        }
        for (int k = 0; k < i; k++)
        {
          this.mTempViews[k].invalidate();
          this.mTempViews[k] = null;
        }
        for (k = 0; k < j; k++)
        {
          localObject1 = this.mTempViewRects[k];
          ((View.AttachInfo.InvalidateInfo)localObject1).target.invalidate(((View.AttachInfo.InvalidateInfo)localObject1).left, ((View.AttachInfo.InvalidateInfo)localObject1).top, ((View.AttachInfo.InvalidateInfo)localObject1).right, ((View.AttachInfo.InvalidateInfo)localObject1).bottom);
          ((View.AttachInfo.InvalidateInfo)localObject1).recycle();
        }
        return;
      }
      finally {}
    }
  }
  
  final class NativePostImeInputStage
    extends ViewRootImpl.AsyncInputStage
    implements InputQueue.FinishedInputEventCallback
  {
    public NativePostImeInputStage(ViewRootImpl.InputStage paramInputStage, String paramString)
    {
      super(paramInputStage, paramString);
    }
    
    public void onFinishedInputEvent(Object paramObject, boolean paramBoolean)
    {
      paramObject = (ViewRootImpl.QueuedInputEvent)paramObject;
      if (paramBoolean)
      {
        finish((ViewRootImpl.QueuedInputEvent)paramObject, true);
        return;
      }
      forward((ViewRootImpl.QueuedInputEvent)paramObject);
    }
    
    protected int onProcess(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      if (ViewRootImpl.this.mInputQueue != null)
      {
        ViewRootImpl.this.mInputQueue.sendInputEvent(paramQueuedInputEvent.mEvent, paramQueuedInputEvent, false, this);
        return 3;
      }
      return 0;
    }
  }
  
  final class NativePreImeInputStage
    extends ViewRootImpl.AsyncInputStage
    implements InputQueue.FinishedInputEventCallback
  {
    public NativePreImeInputStage(ViewRootImpl.InputStage paramInputStage, String paramString)
    {
      super(paramInputStage, paramString);
    }
    
    public void onFinishedInputEvent(Object paramObject, boolean paramBoolean)
    {
      paramObject = (ViewRootImpl.QueuedInputEvent)paramObject;
      if (paramBoolean)
      {
        finish((ViewRootImpl.QueuedInputEvent)paramObject, true);
        return;
      }
      forward((ViewRootImpl.QueuedInputEvent)paramObject);
    }
    
    protected int onProcess(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      if ((ViewRootImpl.this.mInputQueue != null) && ((paramQueuedInputEvent.mEvent instanceof KeyEvent)))
      {
        ViewRootImpl.this.mInputQueue.sendInputEvent(paramQueuedInputEvent.mEvent, paramQueuedInputEvent, true, this);
        return 3;
      }
      return 0;
    }
  }
  
  private static final class QueuedInputEvent
  {
    public static final int FLAG_DEFERRED = 2;
    public static final int FLAG_DELIVER_POST_IME = 1;
    public static final int FLAG_FINISHED = 4;
    public static final int FLAG_FINISHED_HANDLED = 8;
    public static final int FLAG_MODIFIED_FOR_COMPATIBILITY = 64;
    public static final int FLAG_RESYNTHESIZED = 16;
    public static final int FLAG_UNHANDLED = 32;
    public InputEvent mEvent;
    public int mFlags;
    public QueuedInputEvent mNext;
    public InputEventReceiver mReceiver;
    
    private boolean flagToString(String paramString, int paramInt, boolean paramBoolean, StringBuilder paramStringBuilder)
    {
      if ((this.mFlags & paramInt) != 0)
      {
        if (paramBoolean) {
          paramStringBuilder.append("|");
        }
        paramStringBuilder.append(paramString);
        return true;
      }
      return paramBoolean;
    }
    
    public boolean shouldSendToSynthesizer()
    {
      return (this.mFlags & 0x20) != 0;
    }
    
    public boolean shouldSkipIme()
    {
      int i = this.mFlags;
      boolean bool = true;
      if ((i & 0x1) != 0) {
        return true;
      }
      InputEvent localInputEvent = this.mEvent;
      if ((!(localInputEvent instanceof MotionEvent)) || ((localInputEvent.isFromSource(2)) || (!this.mEvent.isFromSource(4194304)))) {
        bool = false;
      }
      return bool;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder1 = new StringBuilder("QueuedInputEvent{flags=");
      if (!flagToString("UNHANDLED", 32, flagToString("RESYNTHESIZED", 16, flagToString("FINISHED_HANDLED", 8, flagToString("FINISHED", 4, flagToString("DEFERRED", 2, flagToString("DELIVER_POST_IME", 1, false, localStringBuilder1), localStringBuilder1), localStringBuilder1), localStringBuilder1), localStringBuilder1), localStringBuilder1)) {
        localStringBuilder1.append("0");
      }
      StringBuilder localStringBuilder2 = new StringBuilder();
      localStringBuilder2.append(", hasNextQueuedEvent=");
      Object localObject = this.mEvent;
      String str = "true";
      if (localObject != null) {
        localObject = "true";
      } else {
        localObject = "false";
      }
      localStringBuilder2.append((String)localObject);
      localStringBuilder1.append(localStringBuilder2.toString());
      localStringBuilder2 = new StringBuilder();
      localStringBuilder2.append(", hasInputEventReceiver=");
      if (this.mReceiver != null) {
        localObject = str;
      } else {
        localObject = "false";
      }
      localStringBuilder2.append((String)localObject);
      localStringBuilder1.append(localStringBuilder2.toString());
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append(", mEvent=");
      ((StringBuilder)localObject).append(this.mEvent);
      ((StringBuilder)localObject).append("}");
      localStringBuilder1.append(((StringBuilder)localObject).toString());
      return localStringBuilder1.toString();
    }
  }
  
  private class SendWindowContentChangedAccessibilityEvent
    implements Runnable
  {
    private int mChangeTypes = 0;
    public long mLastEventTimeMillis;
    public StackTraceElement[] mOrigin;
    public View mSource;
    
    private SendWindowContentChangedAccessibilityEvent() {}
    
    public void removeCallbacksAndRun()
    {
      ViewRootImpl.this.mHandler.removeCallbacks(this);
      run();
    }
    
    public void run()
    {
      View localView = this.mSource;
      this.mSource = null;
      if (localView == null)
      {
        Log.e("ViewRootImpl", "Accessibility content change has no source");
        return;
      }
      if (AccessibilityManager.getInstance(ViewRootImpl.this.mContext).isEnabled())
      {
        this.mLastEventTimeMillis = SystemClock.uptimeMillis();
        AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain();
        localAccessibilityEvent.setEventType(2048);
        localAccessibilityEvent.setContentChangeTypes(this.mChangeTypes);
        localView.sendAccessibilityEventUnchecked(localAccessibilityEvent);
      }
      else
      {
        this.mLastEventTimeMillis = 0L;
      }
      localView.resetSubtreeAccessibilityStateChanged();
      this.mChangeTypes = 0;
    }
    
    public void runOrPost(View paramView, int paramInt)
    {
      if (ViewRootImpl.this.mHandler.getLooper() != Looper.myLooper())
      {
        Log.e("ViewRootImpl", "Accessibility content change on non-UI thread. Future Android versions will throw an exception.", new ViewRootImpl.CalledFromWrongThreadException("Only the original thread that created a view hierarchy can touch its views."));
        ViewRootImpl.this.mHandler.removeCallbacks(this);
        if (this.mSource != null) {
          run();
        }
      }
      Object localObject = this.mSource;
      if (localObject != null)
      {
        View localView = ViewRootImpl.this.getCommonPredecessor((View)localObject, paramView);
        localObject = localView;
        if (localView != null) {
          localObject = localView.getSelfOrParentImportantForA11y();
        }
        if (localObject != null) {
          paramView = (View)localObject;
        }
        this.mSource = paramView;
        this.mChangeTypes |= paramInt;
        return;
      }
      this.mSource = paramView;
      this.mChangeTypes = paramInt;
      long l1 = SystemClock.uptimeMillis() - this.mLastEventTimeMillis;
      long l2 = ViewConfiguration.getSendRecurringAccessibilityEventsInterval();
      if (l1 >= l2) {
        removeCallbacksAndRun();
      } else {
        ViewRootImpl.this.mHandler.postDelayed(this, l2 - l1);
      }
    }
  }
  
  final class SyntheticInputStage
    extends ViewRootImpl.InputStage
  {
    private final ViewRootImpl.SyntheticJoystickHandler mJoystick = new ViewRootImpl.SyntheticJoystickHandler(ViewRootImpl.this);
    private final ViewRootImpl.SyntheticKeyboardHandler mKeyboard = new ViewRootImpl.SyntheticKeyboardHandler(ViewRootImpl.this);
    private final ViewRootImpl.SyntheticTouchNavigationHandler mTouchNavigation = new ViewRootImpl.SyntheticTouchNavigationHandler(ViewRootImpl.this);
    private final ViewRootImpl.SyntheticTrackballHandler mTrackball = new ViewRootImpl.SyntheticTrackballHandler(ViewRootImpl.this);
    
    public SyntheticInputStage()
    {
      super(null);
    }
    
    protected void onDeliverToNext(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      if (((paramQueuedInputEvent.mFlags & 0x10) == 0) && ((paramQueuedInputEvent.mEvent instanceof MotionEvent)))
      {
        MotionEvent localMotionEvent = (MotionEvent)paramQueuedInputEvent.mEvent;
        int i = localMotionEvent.getSource();
        if ((i & 0x4) != 0) {
          this.mTrackball.cancel();
        } else if ((i & 0x10) != 0) {
          ViewRootImpl.SyntheticJoystickHandler.access$2600(this.mJoystick);
        } else if ((i & 0x200000) == 2097152) {
          this.mTouchNavigation.cancel(localMotionEvent);
        }
      }
      super.onDeliverToNext(paramQueuedInputEvent);
    }
    
    protected void onDetachedFromWindow()
    {
      ViewRootImpl.SyntheticJoystickHandler.access$2600(this.mJoystick);
    }
    
    protected int onProcess(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      paramQueuedInputEvent.mFlags |= 0x10;
      if ((paramQueuedInputEvent.mEvent instanceof MotionEvent))
      {
        paramQueuedInputEvent = (MotionEvent)paramQueuedInputEvent.mEvent;
        int i = paramQueuedInputEvent.getSource();
        if ((i & 0x4) != 0)
        {
          this.mTrackball.process(paramQueuedInputEvent);
          return 1;
        }
        if ((i & 0x10) != 0)
        {
          this.mJoystick.process(paramQueuedInputEvent);
          return 1;
        }
        if ((i & 0x200000) == 2097152)
        {
          this.mTouchNavigation.process(paramQueuedInputEvent);
          return 1;
        }
      }
      else if ((paramQueuedInputEvent.mFlags & 0x20) != 0)
      {
        this.mKeyboard.process((KeyEvent)paramQueuedInputEvent.mEvent);
        return 1;
      }
      return 0;
    }
    
    protected void onWindowFocusChanged(boolean paramBoolean)
    {
      if (!paramBoolean) {
        ViewRootImpl.SyntheticJoystickHandler.access$2600(this.mJoystick);
      }
    }
  }
  
  final class SyntheticJoystickHandler
    extends Handler
  {
    private static final int MSG_ENQUEUE_X_AXIS_KEY_REPEAT = 1;
    private static final int MSG_ENQUEUE_Y_AXIS_KEY_REPEAT = 2;
    private final SparseArray<KeyEvent> mDeviceKeyEvents = new SparseArray();
    private final JoystickAxesState mJoystickAxesState = new JoystickAxesState();
    
    public SyntheticJoystickHandler()
    {
      super();
    }
    
    private void cancel()
    {
      removeMessages(1);
      removeMessages(2);
      for (int i = 0; i < this.mDeviceKeyEvents.size(); i++)
      {
        KeyEvent localKeyEvent = (KeyEvent)this.mDeviceKeyEvents.valueAt(i);
        if (localKeyEvent != null) {
          ViewRootImpl.this.enqueueInputEvent(KeyEvent.changeTimeRepeat(localKeyEvent, SystemClock.uptimeMillis(), 0));
        }
      }
      this.mDeviceKeyEvents.clear();
      this.mJoystickAxesState.resetState();
    }
    
    private void update(MotionEvent paramMotionEvent)
    {
      int i = paramMotionEvent.getHistorySize();
      for (int j = 0; j < i; j++)
      {
        l = paramMotionEvent.getHistoricalEventTime(j);
        this.mJoystickAxesState.updateStateForAxis(paramMotionEvent, l, 0, paramMotionEvent.getHistoricalAxisValue(0, 0, j));
        this.mJoystickAxesState.updateStateForAxis(paramMotionEvent, l, 1, paramMotionEvent.getHistoricalAxisValue(1, 0, j));
        this.mJoystickAxesState.updateStateForAxis(paramMotionEvent, l, 15, paramMotionEvent.getHistoricalAxisValue(15, 0, j));
        this.mJoystickAxesState.updateStateForAxis(paramMotionEvent, l, 16, paramMotionEvent.getHistoricalAxisValue(16, 0, j));
      }
      long l = paramMotionEvent.getEventTime();
      this.mJoystickAxesState.updateStateForAxis(paramMotionEvent, l, 0, paramMotionEvent.getAxisValue(0));
      this.mJoystickAxesState.updateStateForAxis(paramMotionEvent, l, 1, paramMotionEvent.getAxisValue(1));
      this.mJoystickAxesState.updateStateForAxis(paramMotionEvent, l, 15, paramMotionEvent.getAxisValue(15));
      this.mJoystickAxesState.updateStateForAxis(paramMotionEvent, l, 16, paramMotionEvent.getAxisValue(16));
    }
    
    public void handleMessage(Message paramMessage)
    {
      int i = paramMessage.what;
      if (((i == 1) || (i == 2)) && (ViewRootImpl.this.mAttachInfo.mHasWindowFocus))
      {
        KeyEvent localKeyEvent = (KeyEvent)paramMessage.obj;
        localKeyEvent = KeyEvent.changeTimeRepeat(localKeyEvent, SystemClock.uptimeMillis(), localKeyEvent.getRepeatCount() + 1);
        ViewRootImpl.this.enqueueInputEvent(localKeyEvent);
        paramMessage = obtainMessage(paramMessage.what, localKeyEvent);
        paramMessage.setAsynchronous(true);
        sendMessageDelayed(paramMessage, ViewConfiguration.getKeyRepeatDelay());
      }
    }
    
    public void process(MotionEvent paramMotionEvent)
    {
      int i = paramMotionEvent.getActionMasked();
      if (i != 2)
      {
        if (i != 3)
        {
          String str = ViewRootImpl.this.mTag;
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("Unexpected action: ");
          localStringBuilder.append(paramMotionEvent.getActionMasked());
          Log.w(str, localStringBuilder.toString());
        }
        else
        {
          cancel();
        }
      }
      else {
        update(paramMotionEvent);
      }
    }
    
    final class JoystickAxesState
    {
      private static final int STATE_DOWN_OR_RIGHT = 1;
      private static final int STATE_NEUTRAL = 0;
      private static final int STATE_UP_OR_LEFT = -1;
      final int[] mAxisStatesHat = { 0, 0 };
      final int[] mAxisStatesStick = { 0, 0 };
      
      JoystickAxesState() {}
      
      private boolean isXAxis(int paramInt)
      {
        boolean bool;
        if ((paramInt != 0) && (paramInt != 15)) {
          bool = false;
        } else {
          bool = true;
        }
        return bool;
      }
      
      private boolean isYAxis(int paramInt)
      {
        boolean bool1 = true;
        boolean bool2 = bool1;
        if (paramInt != 1) {
          if (paramInt == 16) {
            bool2 = bool1;
          } else {
            bool2 = false;
          }
        }
        return bool2;
      }
      
      private int joystickAxisAndStateToKeycode(int paramInt1, int paramInt2)
      {
        if ((isXAxis(paramInt1)) && (paramInt2 == -1)) {
          return 21;
        }
        if ((isXAxis(paramInt1)) && (paramInt2 == 1)) {
          return 22;
        }
        if ((isYAxis(paramInt1)) && (paramInt2 == -1)) {
          return 19;
        }
        if ((isYAxis(paramInt1)) && (paramInt2 == 1)) {
          return 20;
        }
        String str = ViewRootImpl.this.mTag;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Unknown axis ");
        localStringBuilder.append(paramInt1);
        localStringBuilder.append(" or direction ");
        localStringBuilder.append(paramInt2);
        Log.e(str, localStringBuilder.toString());
        return 0;
      }
      
      private int joystickAxisValueToState(float paramFloat)
      {
        if (paramFloat >= 0.5F) {
          return 1;
        }
        if (paramFloat <= -0.5F) {
          return -1;
        }
        return 0;
      }
      
      void resetState()
      {
        int[] arrayOfInt = this.mAxisStatesHat;
        arrayOfInt[0] = 0;
        arrayOfInt[1] = 0;
        arrayOfInt = this.mAxisStatesStick;
        arrayOfInt[0] = 0;
        arrayOfInt[1] = 0;
      }
      
      void updateStateForAxis(MotionEvent paramMotionEvent, long paramLong, int paramInt, float paramFloat)
      {
        int i;
        int j;
        if (isXAxis(paramInt))
        {
          i = 0;
          j = 1;
        }
        else
        {
          if (!isYAxis(paramInt)) {
            break label359;
          }
          i = 1;
          j = 2;
        }
        int k = joystickAxisValueToState(paramFloat);
        if ((paramInt != 0) && (paramInt != 1)) {
          m = this.mAxisStatesHat[i];
        } else {
          m = this.mAxisStatesStick[i];
        }
        if (m == k) {
          return;
        }
        int n = paramMotionEvent.getMetaState();
        int i1 = paramMotionEvent.getDeviceId();
        int i2 = i1;
        int i3 = paramMotionEvent.getSource();
        if ((m == 1) || (m == -1))
        {
          m = joystickAxisAndStateToKeycode(paramInt, m);
          if (m != 0)
          {
            ViewRootImpl.this.enqueueInputEvent(new KeyEvent(paramLong, paramLong, 1, m, 0, n, i2, 0, 1024, i3));
            ViewRootImpl.SyntheticJoystickHandler.this.mDeviceKeyEvents.put(i2, null);
          }
          ViewRootImpl.SyntheticJoystickHandler.this.removeMessages(j);
        }
        if ((k != 1) && (k != -1)) {
          break label323;
        }
        int m = joystickAxisAndStateToKeycode(paramInt, k);
        if (m != 0)
        {
          paramMotionEvent = new KeyEvent(paramLong, paramLong, 0, m, 0, n, i1, 0, 1024, i3);
          ViewRootImpl.this.enqueueInputEvent(paramMotionEvent);
          paramMotionEvent = ViewRootImpl.SyntheticJoystickHandler.this.obtainMessage(j, paramMotionEvent);
          paramMotionEvent.setAsynchronous(true);
          ViewRootImpl.SyntheticJoystickHandler.this.sendMessageDelayed(paramMotionEvent, ViewConfiguration.getKeyRepeatTimeout());
          ViewRootImpl.SyntheticJoystickHandler.this.mDeviceKeyEvents.put(i1, new KeyEvent(paramLong, paramLong, 1, m, 0, n, i1, 0, 1056, i3));
        }
        label323:
        if ((paramInt != 0) && (paramInt != 1)) {
          this.mAxisStatesHat[i] = k;
        } else {
          this.mAxisStatesStick[i] = k;
        }
        return;
        label359:
        paramMotionEvent = ViewRootImpl.this.mTag;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Unexpected axis ");
        localStringBuilder.append(paramInt);
        localStringBuilder.append(" in updateStateForAxis!");
        Log.e(paramMotionEvent, localStringBuilder.toString());
      }
    }
  }
  
  final class SyntheticKeyboardHandler
  {
    SyntheticKeyboardHandler() {}
    
    public void process(KeyEvent paramKeyEvent)
    {
      if ((paramKeyEvent.getFlags() & 0x400) != 0) {
        return;
      }
      Object localObject = paramKeyEvent.getKeyCharacterMap();
      int i = paramKeyEvent.getKeyCode();
      int j = paramKeyEvent.getMetaState();
      localObject = ((KeyCharacterMap)localObject).getFallbackAction(i, j);
      if (localObject != null)
      {
        i = paramKeyEvent.getFlags();
        paramKeyEvent = KeyEvent.obtain(paramKeyEvent.getDownTime(), paramKeyEvent.getEventTime(), paramKeyEvent.getAction(), ((KeyCharacterMap.FallbackAction)localObject).keyCode, paramKeyEvent.getRepeatCount(), ((KeyCharacterMap.FallbackAction)localObject).metaState, paramKeyEvent.getDeviceId(), paramKeyEvent.getScanCode(), i | 0x400, paramKeyEvent.getSource(), null);
        ((KeyCharacterMap.FallbackAction)localObject).recycle();
        ViewRootImpl.this.enqueueInputEvent(paramKeyEvent);
      }
    }
  }
  
  final class SyntheticTouchNavigationHandler
    extends Handler
  {
    private static final float DEFAULT_HEIGHT_MILLIMETERS = 48.0F;
    private static final float DEFAULT_WIDTH_MILLIMETERS = 48.0F;
    private static final float FLING_TICK_DECAY = 0.8F;
    private static final boolean LOCAL_DEBUG = false;
    private static final String LOCAL_TAG = "SyntheticTouchNavigationHandler";
    private static final float MAX_FLING_VELOCITY_TICKS_PER_SECOND = 20.0F;
    private static final float MIN_FLING_VELOCITY_TICKS_PER_SECOND = 6.0F;
    private static final int TICK_DISTANCE_MILLIMETERS = 12;
    private float mAccumulatedX;
    private float mAccumulatedY;
    private int mActivePointerId = -1;
    private float mConfigMaxFlingVelocity;
    private float mConfigMinFlingVelocity;
    private float mConfigTickDistance;
    private boolean mConsumedMovement;
    private int mCurrentDeviceId = -1;
    private boolean mCurrentDeviceSupported;
    private int mCurrentSource;
    private final Runnable mFlingRunnable = new Runnable()
    {
      public void run()
      {
        long l = SystemClock.uptimeMillis();
        ViewRootImpl.SyntheticTouchNavigationHandler localSyntheticTouchNavigationHandler = ViewRootImpl.SyntheticTouchNavigationHandler.this;
        localSyntheticTouchNavigationHandler.sendKeyDownOrRepeat(l, localSyntheticTouchNavigationHandler.mPendingKeyCode, ViewRootImpl.SyntheticTouchNavigationHandler.this.mPendingKeyMetaState);
        ViewRootImpl.SyntheticTouchNavigationHandler.access$3132(ViewRootImpl.SyntheticTouchNavigationHandler.this, 0.8F);
        if (!ViewRootImpl.SyntheticTouchNavigationHandler.this.postFling(l))
        {
          ViewRootImpl.SyntheticTouchNavigationHandler.access$3302(ViewRootImpl.SyntheticTouchNavigationHandler.this, false);
          ViewRootImpl.SyntheticTouchNavigationHandler.this.finishKeys(l);
        }
      }
    };
    private float mFlingVelocity;
    private boolean mFlinging;
    private float mLastX;
    private float mLastY;
    private int mPendingKeyCode = 0;
    private long mPendingKeyDownTime;
    private int mPendingKeyMetaState;
    private int mPendingKeyRepeatCount;
    private float mStartX;
    private float mStartY;
    private VelocityTracker mVelocityTracker;
    
    public SyntheticTouchNavigationHandler()
    {
      super();
    }
    
    private void cancelFling()
    {
      if (this.mFlinging)
      {
        removeCallbacks(this.mFlingRunnable);
        this.mFlinging = false;
      }
    }
    
    private float consumeAccumulatedMovement(long paramLong, int paramInt1, float paramFloat, int paramInt2, int paramInt3)
    {
      float f;
      for (;;)
      {
        f = paramFloat;
        if (paramFloat > -this.mConfigTickDistance) {
          break;
        }
        sendKeyDownOrRepeat(paramLong, paramInt2, paramInt1);
        paramFloat += this.mConfigTickDistance;
      }
      while (f >= this.mConfigTickDistance)
      {
        sendKeyDownOrRepeat(paramLong, paramInt3, paramInt1);
        f -= this.mConfigTickDistance;
      }
      return f;
    }
    
    private void consumeAccumulatedMovement(long paramLong, int paramInt)
    {
      float f1 = Math.abs(this.mAccumulatedX);
      float f2 = Math.abs(this.mAccumulatedY);
      if (f1 >= f2)
      {
        if (f1 >= this.mConfigTickDistance)
        {
          this.mAccumulatedX = consumeAccumulatedMovement(paramLong, paramInt, this.mAccumulatedX, 21, 22);
          this.mAccumulatedY = 0.0F;
          this.mConsumedMovement = true;
        }
      }
      else if (f2 >= this.mConfigTickDistance)
      {
        this.mAccumulatedY = consumeAccumulatedMovement(paramLong, paramInt, this.mAccumulatedY, 19, 20);
        this.mAccumulatedX = 0.0F;
        this.mConsumedMovement = true;
      }
    }
    
    private void finishKeys(long paramLong)
    {
      cancelFling();
      sendKeyUp(paramLong);
    }
    
    private void finishTracking(long paramLong)
    {
      if (this.mActivePointerId >= 0)
      {
        this.mActivePointerId = -1;
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
      }
    }
    
    private boolean postFling(long paramLong)
    {
      float f = this.mFlingVelocity;
      if (f >= this.mConfigMinFlingVelocity)
      {
        long l = (this.mConfigTickDistance / f * 1000.0F);
        postAtTime(this.mFlingRunnable, paramLong + l);
        return true;
      }
      return false;
    }
    
    private void sendKeyDownOrRepeat(long paramLong, int paramInt1, int paramInt2)
    {
      if (this.mPendingKeyCode != paramInt1)
      {
        sendKeyUp(paramLong);
        this.mPendingKeyDownTime = paramLong;
        this.mPendingKeyCode = paramInt1;
        this.mPendingKeyRepeatCount = 0;
      }
      else
      {
        this.mPendingKeyRepeatCount += 1;
      }
      this.mPendingKeyMetaState = paramInt2;
      ViewRootImpl.this.enqueueInputEvent(new KeyEvent(this.mPendingKeyDownTime, paramLong, 0, this.mPendingKeyCode, this.mPendingKeyRepeatCount, this.mPendingKeyMetaState, this.mCurrentDeviceId, 1024, this.mCurrentSource));
    }
    
    private void sendKeyUp(long paramLong)
    {
      int i = this.mPendingKeyCode;
      if (i != 0)
      {
        ViewRootImpl.this.enqueueInputEvent(new KeyEvent(this.mPendingKeyDownTime, paramLong, 1, i, 0, this.mPendingKeyMetaState, this.mCurrentDeviceId, 0, 1024, this.mCurrentSource));
        this.mPendingKeyCode = 0;
      }
    }
    
    private boolean startFling(long paramLong, float paramFloat1, float paramFloat2)
    {
      switch (this.mPendingKeyCode)
      {
      default: 
        break;
      case 22: 
        if ((paramFloat1 >= this.mConfigMinFlingVelocity) && (Math.abs(paramFloat2) < this.mConfigMinFlingVelocity)) {
          this.mFlingVelocity = paramFloat1;
        } else {
          return false;
        }
        break;
      case 21: 
        if ((-paramFloat1 >= this.mConfigMinFlingVelocity) && (Math.abs(paramFloat2) < this.mConfigMinFlingVelocity)) {
          this.mFlingVelocity = (-paramFloat1);
        } else {
          return false;
        }
        break;
      case 20: 
        if ((paramFloat2 >= this.mConfigMinFlingVelocity) && (Math.abs(paramFloat1) < this.mConfigMinFlingVelocity)) {
          this.mFlingVelocity = paramFloat2;
        } else {
          return false;
        }
        break;
      case 19: 
        if ((-paramFloat2 >= this.mConfigMinFlingVelocity) && (Math.abs(paramFloat1) < this.mConfigMinFlingVelocity)) {
          this.mFlingVelocity = (-paramFloat2);
        } else {
          return false;
        }
        break;
      }
      this.mFlinging = postFling(paramLong);
      return this.mFlinging;
    }
    
    public void cancel(MotionEvent paramMotionEvent)
    {
      if ((this.mCurrentDeviceId == paramMotionEvent.getDeviceId()) && (this.mCurrentSource == paramMotionEvent.getSource()))
      {
        long l = paramMotionEvent.getEventTime();
        finishKeys(l);
        finishTracking(l);
      }
    }
    
    public void process(MotionEvent paramMotionEvent)
    {
      long l = paramMotionEvent.getEventTime();
      int i = paramMotionEvent.getDeviceId();
      int j = paramMotionEvent.getSource();
      float f1;
      float f2;
      if ((this.mCurrentDeviceId != i) || (this.mCurrentSource != j))
      {
        finishKeys(l);
        finishTracking(l);
        this.mCurrentDeviceId = i;
        this.mCurrentSource = j;
        this.mCurrentDeviceSupported = false;
        Object localObject = paramMotionEvent.getDevice();
        if (localObject != null)
        {
          InputDevice.MotionRange localMotionRange = ((InputDevice)localObject).getMotionRange(0);
          localObject = ((InputDevice)localObject).getMotionRange(1);
          if ((localMotionRange != null) && (localObject != null))
          {
            this.mCurrentDeviceSupported = true;
            f1 = localMotionRange.getResolution();
            f2 = f1;
            if (f1 <= 0.0F) {
              f2 = localMotionRange.getRange() / 48.0F;
            }
            float f3 = ((InputDevice.MotionRange)localObject).getResolution();
            f1 = f3;
            if (f3 <= 0.0F) {
              f1 = ((InputDevice.MotionRange)localObject).getRange() / 48.0F;
            }
            this.mConfigTickDistance = (12.0F * ((f2 + f1) * 0.5F));
            f2 = this.mConfigTickDistance;
            this.mConfigMinFlingVelocity = (6.0F * f2);
            this.mConfigMaxFlingVelocity = (f2 * 20.0F);
          }
        }
      }
      if (!this.mCurrentDeviceSupported) {
        return;
      }
      j = paramMotionEvent.getActionMasked();
      if (j != 0)
      {
        if ((j != 1) && (j != 2))
        {
          if (j == 3)
          {
            finishKeys(l);
            finishTracking(l);
          }
        }
        else
        {
          i = this.mActivePointerId;
          if (i >= 0)
          {
            i = paramMotionEvent.findPointerIndex(i);
            if (i < 0)
            {
              finishKeys(l);
              finishTracking(l);
            }
            else
            {
              this.mVelocityTracker.addMovement(paramMotionEvent);
              f1 = paramMotionEvent.getX(i);
              f2 = paramMotionEvent.getY(i);
              this.mAccumulatedX += f1 - this.mLastX;
              this.mAccumulatedY += f2 - this.mLastY;
              this.mLastX = f1;
              this.mLastY = f2;
              consumeAccumulatedMovement(l, paramMotionEvent.getMetaState());
              if (j == 1)
              {
                if ((this.mConsumedMovement) && (this.mPendingKeyCode != 0))
                {
                  this.mVelocityTracker.computeCurrentVelocity(1000, this.mConfigMaxFlingVelocity);
                  if (!startFling(l, this.mVelocityTracker.getXVelocity(this.mActivePointerId), this.mVelocityTracker.getYVelocity(this.mActivePointerId))) {
                    finishKeys(l);
                  }
                }
                finishTracking(l);
              }
            }
          }
        }
      }
      else
      {
        boolean bool = this.mFlinging;
        finishKeys(l);
        finishTracking(l);
        this.mActivePointerId = paramMotionEvent.getPointerId(0);
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mVelocityTracker.addMovement(paramMotionEvent);
        this.mStartX = paramMotionEvent.getX();
        this.mStartY = paramMotionEvent.getY();
        this.mLastX = this.mStartX;
        this.mLastY = this.mStartY;
        this.mAccumulatedX = 0.0F;
        this.mAccumulatedY = 0.0F;
        this.mConsumedMovement = bool;
      }
    }
  }
  
  final class SyntheticTrackballHandler
  {
    private long mLastTime;
    private final ViewRootImpl.TrackballAxis mX = new ViewRootImpl.TrackballAxis();
    private final ViewRootImpl.TrackballAxis mY = new ViewRootImpl.TrackballAxis();
    
    SyntheticTrackballHandler() {}
    
    public void cancel()
    {
      this.mLastTime = -2147483648L;
      if ((ViewRootImpl.this.mView != null) && (ViewRootImpl.this.mAdded)) {
        ViewRootImpl.this.ensureTouchMode(false);
      }
    }
    
    public void process(MotionEvent paramMotionEvent)
    {
      long l = SystemClock.uptimeMillis();
      if (this.mLastTime + 250L < l)
      {
        this.mX.reset(0);
        this.mY.reset(0);
        this.mLastTime = l;
      }
      int i = paramMotionEvent.getAction();
      int j = paramMotionEvent.getMetaState();
      if (i != 0)
      {
        if (i == 1)
        {
          this.mX.reset(2);
          this.mY.reset(2);
          ViewRootImpl.this.enqueueInputEvent(new KeyEvent(l, l, 1, 23, 0, j, -1, 0, 1024, 257));
        }
      }
      else
      {
        this.mX.reset(2);
        this.mY.reset(2);
        ViewRootImpl.this.enqueueInputEvent(new KeyEvent(l, l, 0, 23, 0, j, -1, 0, 1024, 257));
      }
      float f1 = this.mX.collect(paramMotionEvent.getX(), paramMotionEvent.getEventTime(), "X");
      float f2 = this.mY.collect(paramMotionEvent.getY(), paramMotionEvent.getEventTime(), "Y");
      int k = 0;
      if (f1 > f2)
      {
        k = this.mX.generate();
        if (k != 0)
        {
          if (k > 0) {
            i = 22;
          } else {
            i = 21;
          }
          f2 = this.mX.acceleration;
          this.mY.reset(2);
        }
        else
        {
          i = 0;
          f2 = 1.0F;
        }
      }
      else if (f2 > 0.0F)
      {
        k = this.mY.generate();
        if (k != 0)
        {
          if (k > 0) {
            i = 20;
          } else {
            i = 19;
          }
          f2 = this.mY.acceleration;
          this.mX.reset(2);
        }
        else
        {
          i = 0;
          f2 = 1.0F;
        }
      }
      else
      {
        i = 0;
        f2 = 1.0F;
      }
      if (i != 0)
      {
        int m = k;
        if (k < 0) {
          m = -k;
        }
        k = (int)(m * f2);
        if (k > m)
        {
          m--;
          ViewRootImpl.this.enqueueInputEvent(new KeyEvent(l, l, 2, i, k - m, j, -1, 0, 1024, 257));
        }
        while (m > 0)
        {
          m--;
          l = SystemClock.uptimeMillis();
          ViewRootImpl.this.enqueueInputEvent(new KeyEvent(l, l, 0, i, 0, j, -1, 0, 1024, 257));
          ViewRootImpl.this.enqueueInputEvent(new KeyEvent(l, l, 1, i, 0, j, -1, 0, 1024, 257));
        }
        this.mLastTime = l;
      }
    }
  }
  
  static final class SystemUiVisibilityInfo
  {
    int globalVisibility;
    int localChanges;
    int localValue;
    int seq;
  }
  
  class TakenSurfaceHolder
    extends BaseSurfaceHolder
  {
    TakenSurfaceHolder() {}
    
    public boolean isCreating()
    {
      return ViewRootImpl.this.mIsCreating;
    }
    
    public boolean onAllowLockCanvas()
    {
      return ViewRootImpl.this.mDrawingAllowed;
    }
    
    public void onRelayoutContainer() {}
    
    public void onUpdateSurface()
    {
      throw new IllegalStateException("Shouldn't be here");
    }
    
    public void setFixedSize(int paramInt1, int paramInt2)
    {
      throw new UnsupportedOperationException("Currently only support sizing from layout");
    }
    
    public void setFormat(int paramInt)
    {
      ((RootViewSurfaceTaker)ViewRootImpl.this.mView).setSurfaceFormat(paramInt);
    }
    
    public void setKeepScreenOn(boolean paramBoolean)
    {
      ((RootViewSurfaceTaker)ViewRootImpl.this.mView).setSurfaceKeepScreenOn(paramBoolean);
    }
    
    public void setType(int paramInt)
    {
      ((RootViewSurfaceTaker)ViewRootImpl.this.mView).setSurfaceType(paramInt);
    }
  }
  
  static final class TrackballAxis
  {
    static final float ACCEL_MOVE_SCALING_FACTOR = 0.025F;
    static final long FAST_MOVE_TIME = 150L;
    static final float FIRST_MOVEMENT_THRESHOLD = 0.5F;
    static final float MAX_ACCELERATION = 20.0F;
    static final float SECOND_CUMULATIVE_MOVEMENT_THRESHOLD = 2.0F;
    static final float SUBSEQUENT_INCREMENTAL_MOVEMENT_THRESHOLD = 1.0F;
    float acceleration = 1.0F;
    int dir;
    long lastMoveTime = 0L;
    int nonAccelMovement;
    float position;
    int step;
    
    float collect(float paramFloat, long paramLong, String paramString)
    {
      float f1 = 1.0F;
      long l1;
      if (paramFloat > 0.0F)
      {
        l1 = (150.0F * paramFloat);
        if (this.dir < 0)
        {
          this.position = 0.0F;
          this.step = 0;
          this.acceleration = 1.0F;
          this.lastMoveTime = 0L;
        }
        this.dir = 1;
      }
      else if (paramFloat < 0.0F)
      {
        l1 = (-paramFloat * 150.0F);
        if (this.dir > 0)
        {
          this.position = 0.0F;
          this.step = 0;
          this.acceleration = 1.0F;
          this.lastMoveTime = 0L;
        }
        this.dir = -1;
      }
      else
      {
        l1 = 0L;
      }
      if (l1 > 0L)
      {
        long l2 = paramLong - this.lastMoveTime;
        this.lastMoveTime = paramLong;
        float f2 = this.acceleration;
        float f3;
        if (l2 < l1)
        {
          f1 = (float)(l1 - l2) * 0.025F;
          f3 = f2;
          if (f1 > 1.0F) {
            f3 = f2 * f1;
          }
          f2 = 20.0F;
          if (f3 < 20.0F) {
            f2 = f3;
          }
          this.acceleration = f2;
        }
        else
        {
          float f4 = (float)(l2 - l1) * 0.025F;
          f3 = f2;
          if (f4 > 1.0F) {
            f3 = f2 / f4;
          }
          f2 = f1;
          if (f3 > 1.0F) {
            f2 = f3;
          }
          this.acceleration = f2;
        }
      }
      this.position += paramFloat;
      return Math.abs(this.position);
    }
    
    int generate()
    {
      int i = 0;
      this.nonAccelMovement = 0;
      for (;;)
      {
        int j;
        if (this.position >= 0.0F) {
          j = 1;
        } else {
          j = -1;
        }
        int k = this.step;
        if (k != 0)
        {
          if (k != 1)
          {
            if (Math.abs(this.position) < 1.0F) {
              return i;
            }
            i += j;
            this.position -= j * 1.0F;
            float f = this.acceleration * 1.1F;
            if (f >= 20.0F) {
              f = this.acceleration;
            }
            this.acceleration = f;
            j = i;
          }
          else
          {
            if (Math.abs(this.position) < 2.0F) {
              return i;
            }
            i += j;
            this.nonAccelMovement += j;
            this.position -= j * 2.0F;
            this.step = 2;
            j = i;
          }
        }
        else
        {
          if (Math.abs(this.position) < 0.5F) {
            return i;
          }
          i += j;
          this.nonAccelMovement += j;
          this.step = 1;
          j = i;
        }
        i = j;
      }
    }
    
    void reset(int paramInt)
    {
      this.position = 0.0F;
      this.acceleration = 1.0F;
      this.lastMoveTime = 0L;
      this.step = paramInt;
      this.dir = 0;
    }
  }
  
  final class TraversalRunnable
    implements Runnable
  {
    TraversalRunnable() {}
    
    public void run()
    {
      ViewRootImpl.this.doTraversal();
      ViewRootImpl.this.notifyContentChangeToContentCatcher();
    }
  }
  
  private static class UnhandledKeyManager
  {
    private final SparseArray<WeakReference<View>> mCapturedKeys = new SparseArray();
    private WeakReference<View> mCurrentReceiver = null;
    private boolean mDispatched = true;
    
    boolean dispatch(View paramView, KeyEvent paramKeyEvent)
    {
      if (this.mDispatched) {
        return false;
      }
      try
      {
        Trace.traceBegin(8L, "UnhandledKeyEvent dispatch");
        boolean bool = true;
        this.mDispatched = true;
        paramView = paramView.dispatchUnhandledKeyEvent(paramKeyEvent);
        if (paramKeyEvent.getAction() == 0)
        {
          int i = paramKeyEvent.getKeyCode();
          if ((paramView != null) && (!KeyEvent.isModifierKey(i)))
          {
            SparseArray localSparseArray = this.mCapturedKeys;
            paramKeyEvent = new java/lang/ref/WeakReference;
            paramKeyEvent.<init>(paramView);
            localSparseArray.put(i, paramKeyEvent);
          }
        }
        Trace.traceEnd(8L);
        if (paramView == null) {
          bool = false;
        }
        return bool;
      }
      finally
      {
        Trace.traceEnd(8L);
      }
    }
    
    void preDispatch(KeyEvent paramKeyEvent)
    {
      this.mCurrentReceiver = null;
      if (paramKeyEvent.getAction() == 1)
      {
        int i = this.mCapturedKeys.indexOfKey(paramKeyEvent.getKeyCode());
        if (i >= 0)
        {
          this.mCurrentReceiver = ((WeakReference)this.mCapturedKeys.valueAt(i));
          this.mCapturedKeys.removeAt(i);
        }
      }
    }
    
    boolean preViewDispatch(KeyEvent paramKeyEvent)
    {
      this.mDispatched = false;
      if (this.mCurrentReceiver == null) {
        this.mCurrentReceiver = ((WeakReference)this.mCapturedKeys.get(paramKeyEvent.getKeyCode()));
      }
      Object localObject = this.mCurrentReceiver;
      if (localObject != null)
      {
        localObject = (View)((WeakReference)localObject).get();
        if (paramKeyEvent.getAction() == 1) {
          this.mCurrentReceiver = null;
        }
        if ((localObject != null) && (((View)localObject).isAttachedToWindow())) {
          ((View)localObject).onUnhandledKeyEvent(paramKeyEvent);
        }
        return true;
      }
      return false;
    }
  }
  
  final class ViewPostImeInputStage
    extends ViewRootImpl.InputStage
  {
    public ViewPostImeInputStage(ViewRootImpl.InputStage paramInputStage)
    {
      super(paramInputStage);
    }
    
    private void maybeUpdatePointerIcon(MotionEvent paramMotionEvent)
    {
      if ((paramMotionEvent.getPointerCount() == 1) && (paramMotionEvent.isFromSource(8194)))
      {
        if ((paramMotionEvent.getActionMasked() == 9) || (paramMotionEvent.getActionMasked() == 10)) {
          ViewRootImpl.access$2402(ViewRootImpl.this, 1);
        }
        if ((paramMotionEvent.getActionMasked() != 10) && (!ViewRootImpl.this.updatePointerIcon(paramMotionEvent)) && (paramMotionEvent.getActionMasked() == 7)) {
          ViewRootImpl.access$2402(ViewRootImpl.this, 1);
        }
      }
    }
    
    private boolean performFocusNavigation(KeyEvent paramKeyEvent)
    {
      int i = 0;
      int j = paramKeyEvent.getKeyCode();
      if (j != 61) {
        switch (j)
        {
        default: 
          break;
        case 22: 
          if (!paramKeyEvent.hasNoModifiers()) {
            break;
          }
          i = 66;
          break;
        case 21: 
          if (!paramKeyEvent.hasNoModifiers()) {
            break;
          }
          i = 17;
          break;
        case 20: 
          if (!paramKeyEvent.hasNoModifiers()) {
            break;
          }
          i = 130;
          break;
        case 19: 
          if (!paramKeyEvent.hasNoModifiers()) {
            break;
          }
          i = 33;
          break;
        }
      } else if (paramKeyEvent.hasNoModifiers()) {
        i = 2;
      } else if (paramKeyEvent.hasModifiers(1)) {
        i = 1;
      }
      if (i != 0)
      {
        paramKeyEvent = ViewRootImpl.this.mView.findFocus();
        if (paramKeyEvent != null)
        {
          View localView = paramKeyEvent.focusSearch(i);
          if ((localView != null) && (localView != paramKeyEvent))
          {
            paramKeyEvent.getFocusedRect(ViewRootImpl.this.mTempRect);
            if ((ViewRootImpl.this.mView instanceof ViewGroup))
            {
              ((ViewGroup)ViewRootImpl.this.mView).offsetDescendantRectToMyCoords(paramKeyEvent, ViewRootImpl.this.mTempRect);
              ((ViewGroup)ViewRootImpl.this.mView).offsetRectIntoDescendantCoords(localView, ViewRootImpl.this.mTempRect);
            }
            if (localView.requestFocus(i, ViewRootImpl.this.mTempRect))
            {
              ViewRootImpl.this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(i));
              return true;
            }
          }
          if (ViewRootImpl.this.mView.dispatchUnhandledMove(paramKeyEvent, i)) {
            return true;
          }
        }
        else if (ViewRootImpl.this.mView.restoreDefaultFocus())
        {
          return true;
        }
      }
      return false;
    }
    
    private boolean performKeyboardGroupNavigation(int paramInt)
    {
      View localView1 = ViewRootImpl.this.mView.findFocus();
      if ((localView1 == null) && (ViewRootImpl.this.mView.restoreDefaultFocus())) {
        return true;
      }
      if (localView1 == null) {
        localView1 = ViewRootImpl.this.keyboardNavigationClusterSearch(null, paramInt);
      } else {
        localView1 = localView1.keyboardNavigationClusterSearch(null, paramInt);
      }
      int i = paramInt;
      if ((paramInt == 2) || (paramInt == 1)) {
        i = 130;
      }
      View localView2 = localView1;
      if (localView1 != null)
      {
        localView2 = localView1;
        if (localView1.isRootNamespace())
        {
          if (localView1.restoreFocusNotInCluster())
          {
            ViewRootImpl.this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(paramInt));
            return true;
          }
          localView2 = ViewRootImpl.this.keyboardNavigationClusterSearch(null, paramInt);
        }
      }
      if ((localView2 != null) && (localView2.restoreFocusInCluster(i)))
      {
        ViewRootImpl.this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(paramInt));
        return true;
      }
      return false;
    }
    
    private int processGenericMotionEvent(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      paramQueuedInputEvent = (MotionEvent)paramQueuedInputEvent.mEvent;
      if ((paramQueuedInputEvent.isFromSource(1048584)) && (ViewRootImpl.this.hasPointerCapture()) && (ViewRootImpl.this.mView.dispatchCapturedPointerEvent(paramQueuedInputEvent))) {
        return 1;
      }
      if (ViewRootImpl.this.mView.dispatchGenericMotionEvent(paramQueuedInputEvent)) {
        return 1;
      }
      return 0;
    }
    
    private int processKeyEvent(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      KeyEvent localKeyEvent = (KeyEvent)paramQueuedInputEvent.mEvent;
      if (ViewRootImpl.this.mUnhandledKeyManager.preViewDispatch(localKeyEvent)) {
        return 1;
      }
      if (ViewRootImpl.this.mView.dispatchKeyEvent(localKeyEvent)) {
        return 1;
      }
      if (shouldDropInputEvent(paramQueuedInputEvent)) {
        return 2;
      }
      if (ViewRootImpl.this.mUnhandledKeyManager.dispatch(ViewRootImpl.this.mView, localKeyEvent)) {
        return 1;
      }
      int i = 0;
      int j = i;
      if (localKeyEvent.getAction() == 0)
      {
        j = i;
        if (localKeyEvent.getKeyCode() == 61) {
          if (KeyEvent.metaStateHasModifiers(localKeyEvent.getMetaState(), 65536))
          {
            j = 2;
          }
          else
          {
            j = i;
            if (KeyEvent.metaStateHasModifiers(localKeyEvent.getMetaState(), 65537)) {
              j = 1;
            }
          }
        }
      }
      if ((localKeyEvent.getAction() == 0) && (!KeyEvent.metaStateHasNoModifiers(localKeyEvent.getMetaState())) && (localKeyEvent.getRepeatCount() == 0) && (!KeyEvent.isModifierKey(localKeyEvent.getKeyCode())) && (j == 0))
      {
        if (ViewRootImpl.this.mView.dispatchKeyShortcutEvent(localKeyEvent)) {
          return 1;
        }
        if (shouldDropInputEvent(paramQueuedInputEvent)) {
          return 2;
        }
      }
      if (ViewRootImpl.this.mFallbackEventHandler.dispatchKeyEvent(localKeyEvent)) {
        return 1;
      }
      if (shouldDropInputEvent(paramQueuedInputEvent)) {
        return 2;
      }
      if (localKeyEvent.getAction() == 0) {
        if (j != 0)
        {
          if (performKeyboardGroupNavigation(j)) {
            return 1;
          }
        }
        else if (performFocusNavigation(localKeyEvent)) {
          return 1;
        }
      }
      return 0;
    }
    
    private int processPointerEvent(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      paramQueuedInputEvent = (MotionEvent)paramQueuedInputEvent.mEvent;
      ViewRootImplInjector.checkForThreeGesture(paramQueuedInputEvent);
      ViewRootImpl.this.mAttachInfo.mUnbufferedDispatchRequested = false;
      ViewRootImpl.this.mAttachInfo.mHandlingPointerEvent = true;
      int i = ViewRootImpl.this.mView.dispatchPointerEvent(paramQueuedInputEvent);
      int j = paramQueuedInputEvent.getActionMasked();
      if (j == 2) {
        ViewRootImpl.this.mHaveMoveEvent = true;
      } else if (j == 1) {
        ViewRootImpl.this.mHaveMoveEvent = false;
      }
      maybeUpdatePointerIcon(paramQueuedInputEvent);
      ViewRootImpl.this.maybeUpdateTooltip(paramQueuedInputEvent);
      ViewRootImpl.this.mAttachInfo.mHandlingPointerEvent = false;
      if ((ViewRootImpl.this.mAttachInfo.mUnbufferedDispatchRequested) && (!ViewRootImpl.this.mUnbufferedInputDispatch))
      {
        paramQueuedInputEvent = ViewRootImpl.this;
        paramQueuedInputEvent.mUnbufferedInputDispatch = true;
        if (paramQueuedInputEvent.mConsumeBatchedInputScheduled) {
          ViewRootImpl.this.scheduleConsumeBatchedInputImmediately();
        }
      }
      return i;
    }
    
    private int processTrackballEvent(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      paramQueuedInputEvent = (MotionEvent)paramQueuedInputEvent.mEvent;
      if ((paramQueuedInputEvent.isFromSource(131076)) && ((!ViewRootImpl.this.hasPointerCapture()) || (ViewRootImpl.this.mView.dispatchCapturedPointerEvent(paramQueuedInputEvent)))) {
        return 1;
      }
      if (ViewRootImpl.this.mView.dispatchTrackballEvent(paramQueuedInputEvent)) {
        return 1;
      }
      return 0;
    }
    
    protected void onDeliverToNext(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      if ((ViewRootImpl.this.mUnbufferedInputDispatch) && ((paramQueuedInputEvent.mEvent instanceof MotionEvent)) && (((MotionEvent)paramQueuedInputEvent.mEvent).isTouchEvent()) && (ViewRootImpl.isTerminalInputEvent(paramQueuedInputEvent.mEvent)))
      {
        ViewRootImpl localViewRootImpl = ViewRootImpl.this;
        localViewRootImpl.mUnbufferedInputDispatch = false;
        localViewRootImpl.scheduleConsumeBatchedInput();
      }
      super.onDeliverToNext(paramQueuedInputEvent);
    }
    
    protected int onProcess(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      if ((paramQueuedInputEvent.mEvent instanceof KeyEvent)) {
        return processKeyEvent(paramQueuedInputEvent);
      }
      int i = paramQueuedInputEvent.mEvent.getSource();
      if ((i & 0x2) != 0) {
        return processPointerEvent(paramQueuedInputEvent);
      }
      if ((i & 0x4) != 0) {
        return processTrackballEvent(paramQueuedInputEvent);
      }
      return processGenericMotionEvent(paramQueuedInputEvent);
    }
  }
  
  final class ViewPreImeInputStage
    extends ViewRootImpl.InputStage
  {
    public ViewPreImeInputStage(ViewRootImpl.InputStage paramInputStage)
    {
      super(paramInputStage);
    }
    
    private int processKeyEvent(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      paramQueuedInputEvent = (KeyEvent)paramQueuedInputEvent.mEvent;
      ViewRootImpl.this.dispatchKeyEventToContentCatcher(paramQueuedInputEvent);
      if (ViewRootImpl.this.mView.dispatchKeyEventPreIme(paramQueuedInputEvent)) {
        return 1;
      }
      return 0;
    }
    
    protected int onProcess(ViewRootImpl.QueuedInputEvent paramQueuedInputEvent)
    {
      if ((paramQueuedInputEvent.mEvent instanceof KeyEvent)) {
        return processKeyEvent(paramQueuedInputEvent);
      }
      return 0;
    }
  }
  
  final class ViewRootHandler
    extends Handler
  {
    ViewRootHandler() {}
    
    public String getMessageName(Message paramMessage)
    {
      int i = paramMessage.what;
      if (i != 21)
      {
        if (i != 1000)
        {
          switch (i)
          {
          default: 
            switch (i)
            {
            default: 
              switch (i)
              {
              default: 
                switch (i)
                {
                default: 
                  return super.getMessageName(paramMessage);
                case 32: 
                  return "MSG_SYSTEM_GESTURE_EXCLUSION_CHANGED";
                case 31: 
                  return "MSG_INSETS_CONTROL_CHANGED";
                case 30: 
                  return "MSG_INSETS_CHANGED";
                case 29: 
                  return "MSG_DRAW_FINISHED";
                case 28: 
                  return "MSG_POINTER_CAPTURE_CHANGED";
                }
                return "MSG_UPDATE_POINTER_ICON";
              case 25: 
                return "MSG_DISPATCH_WINDOW_SHOWN";
              case 24: 
                return "MSG_SYNTHESIZE_INPUT_EVENT";
              }
              return "MSG_WINDOW_MOVED";
            case 19: 
              return "MSG_PROCESS_INPUT_EVENTS";
            case 18: 
              return "MSG_UPDATE_CONFIGURATION";
            case 17: 
              return "MSG_DISPATCH_SYSTEM_UI_VISIBILITY";
            case 16: 
              return "MSG_DISPATCH_DRAG_LOCATION_EVENT";
            case 15: 
              return "MSG_DISPATCH_DRAG_EVENT";
            case 14: 
              return "MSG_CLOSE_SYSTEM_DIALOGS";
            case 13: 
              return "MSG_CHECK_FOCUS";
            case 12: 
              return "MSG_DISPATCH_KEY_FROM_AUTOFILL";
            }
            return "MSG_DISPATCH_KEY_FROM_IME";
          case 9: 
            return "MSG_DISPATCH_GET_NEW_SURFACE";
          case 8: 
            return "MSG_DISPATCH_APP_VISIBILITY";
          case 7: 
            return "MSG_DISPATCH_INPUT_EVENT";
          case 6: 
            return "MSG_WINDOW_FOCUS_CHANGED";
          case 5: 
            return "MSG_RESIZED_REPORT";
          case 4: 
            return "MSG_RESIZED";
          case 3: 
            return "MSG_DIE";
          case 2: 
            return "MSG_INVALIDATE_RECT";
          }
          return "MSG_INVALIDATE";
        }
        return "MSG_CAST_MODE";
      }
      return "MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST";
    }
    
    public void handleMessage(Message paramMessage)
    {
      int i = paramMessage.what;
      if (i != 1000)
      {
        int j = -1;
        boolean bool1 = true;
        boolean bool2 = true;
        boolean bool3 = true;
        Object localObject1;
        int k;
        int m;
        Object localObject2;
        switch (i)
        {
        default: 
          switch (i)
          {
          default: 
            switch (i)
            {
            default: 
              break;
            case 32: 
              ViewRootImpl.this.systemGestureExclusionChanged();
              break;
            case 31: 
              paramMessage = (SomeArgs)paramMessage.obj;
              ViewRootImpl.this.mInsetsController.onControlsChanged((InsetsSourceControl[])paramMessage.arg2);
              ViewRootImpl.this.mInsetsController.onStateChanged((InsetsState)paramMessage.arg1);
              break;
            case 30: 
              ViewRootImpl.this.mInsetsController.onStateChanged((InsetsState)paramMessage.obj);
              break;
            case 29: 
              ViewRootImpl.this.pendingDrawFinished();
              break;
            case 28: 
              if (paramMessage.arg1 == 0) {
                bool3 = false;
              }
              ViewRootImpl.this.handlePointerCaptureChanged(bool3);
              break;
            case 27: 
              paramMessage = (MotionEvent)paramMessage.obj;
              ViewRootImpl.this.resetPointerIcon(paramMessage);
              break;
            case 26: 
              localObject1 = (IResultReceiver)paramMessage.obj;
              j = paramMessage.arg1;
              ViewRootImpl.this.handleRequestKeyboardShortcuts((IResultReceiver)localObject1, j);
              break;
            case 25: 
              ViewRootImpl.this.handleDispatchWindowShown();
              break;
            case 24: 
              paramMessage = (InputEvent)paramMessage.obj;
              ViewRootImpl.this.enqueueInputEvent(paramMessage, null, 32, true);
              break;
            case 23: 
              if (!ViewRootImpl.this.mAdded) {
                break;
              }
              k = ViewRootImpl.this.mWinFrame.width();
              i = ViewRootImpl.this.mWinFrame.height();
              j = paramMessage.arg1;
              m = paramMessage.arg2;
              ViewRootImpl.this.mTmpFrame.left = j;
              ViewRootImpl.this.mTmpFrame.right = (j + k);
              ViewRootImpl.this.mTmpFrame.top = m;
              ViewRootImpl.this.mTmpFrame.bottom = (m + i);
              paramMessage = ViewRootImpl.this;
              paramMessage.setFrame(paramMessage.mTmpFrame);
              ViewRootImpl.this.mPendingBackDropFrame.set(ViewRootImpl.this.mWinFrame);
              paramMessage = ViewRootImpl.this;
              paramMessage.maybeHandleWindowMove(paramMessage.mWinFrame);
              break;
            case 22: 
              if (ViewRootImpl.this.mView == null) {
                break;
              }
              paramMessage = ViewRootImpl.this;
              paramMessage.invalidateWorld(paramMessage.mView);
              break;
            case 21: 
              ViewRootImpl.this.setAccessibilityFocus(null, null);
            }
            break;
          case 19: 
            paramMessage = ViewRootImpl.this;
            paramMessage.mProcessInputEventsScheduled = false;
            paramMessage.doProcessInputEvents();
            break;
          case 18: 
            localObject1 = (Configuration)paramMessage.obj;
            paramMessage = (Message)localObject1;
            if (((Configuration)localObject1).isOtherSeqNewer(ViewRootImpl.this.mLastReportedMergedConfiguration.getMergedConfiguration())) {
              paramMessage = ViewRootImpl.this.mLastReportedMergedConfiguration.getGlobalConfiguration();
            }
            ViewRootImpl.this.mPendingMergedConfiguration.setConfiguration(paramMessage, ViewRootImpl.this.mLastReportedMergedConfiguration.getOverrideConfiguration());
            paramMessage = ViewRootImpl.this;
            paramMessage.performConfigurationChange(paramMessage.mPendingMergedConfiguration, false, -1);
            break;
          case 17: 
            ViewRootImpl.this.handleDispatchSystemUiVisibilityChanged((ViewRootImpl.SystemUiVisibilityInfo)paramMessage.obj);
            break;
          case 15: 
          case 16: 
            paramMessage = (DragEvent)paramMessage.obj;
            paramMessage.mLocalState = ViewRootImpl.this.mLocalDragState;
            ViewRootImpl.this.handleDragEvent(paramMessage);
            break;
          case 14: 
            if (ViewRootImpl.this.mView == null) {
              break;
            }
            ViewRootImpl.this.mView.onCloseSystemDialogs((String)paramMessage.obj);
            break;
          case 13: 
            paramMessage = (InputMethodManager)ViewRootImpl.this.mContext.getSystemService(InputMethodManager.class);
            if (paramMessage != null) {
              paramMessage.checkFocus();
            }
            break;
          case 12: 
            paramMessage = (KeyEvent)paramMessage.obj;
            ViewRootImpl.this.enqueueInputEvent(paramMessage, null, 0, true);
            break;
          case 11: 
            localObject1 = (KeyEvent)paramMessage.obj;
            paramMessage = (Message)localObject1;
            if ((((KeyEvent)localObject1).getFlags() & 0x8) != 0) {
              paramMessage = KeyEvent.changeFlags((KeyEvent)localObject1, ((KeyEvent)localObject1).getFlags() & 0xFFFFFFF7);
            }
            ViewRootImpl.this.enqueueInputEvent(paramMessage, null, 1, true);
          }
          break;
        case 9: 
          ViewRootImpl.this.handleGetNewSurface();
          break;
        case 8: 
          localObject1 = ViewRootImpl.this;
          if (paramMessage.arg1 != 0) {
            bool3 = bool1;
          } else {
            bool3 = false;
          }
          ((ViewRootImpl)localObject1).handleAppVisibility(bool3);
          break;
        case 7: 
          localObject1 = (SomeArgs)paramMessage.obj;
          paramMessage = (InputEvent)((SomeArgs)localObject1).arg1;
          localObject2 = (InputEventReceiver)((SomeArgs)localObject1).arg2;
          ViewRootImpl.this.enqueueInputEvent(paramMessage, (InputEventReceiver)localObject2, 0, true);
          ((SomeArgs)localObject1).recycle();
          break;
        case 6: 
          ViewRootImpl.this.handleWindowFocusChanged();
          break;
        case 4: 
          localObject1 = (SomeArgs)paramMessage.obj;
        case 5: 
          if (((ViewRootImpl.this.mWinFrame.equals(((SomeArgs)localObject1).arg1)) && (ViewRootImpl.this.mPendingOverscanInsets.equals(((SomeArgs)localObject1).arg5)) && (ViewRootImpl.this.mPendingContentInsets.equals(((SomeArgs)localObject1).arg2)) && (ViewRootImpl.this.mPendingStableInsets.equals(((SomeArgs)localObject1).arg6)) && (ViewRootImpl.this.mPendingDisplayCutout.get().equals(((SomeArgs)localObject1).arg9)) && (ViewRootImpl.this.mPendingVisibleInsets.equals(((SomeArgs)localObject1).arg3)) && (ViewRootImpl.this.mPendingOutsets.equals(((SomeArgs)localObject1).arg7)) && (ViewRootImpl.this.mPendingBackDropFrame.equals(((SomeArgs)localObject1).arg8)) && (((SomeArgs)localObject1).arg4 == null) && (((SomeArgs)localObject1).argi1 == 0) && (ViewRootImpl.this.mDisplay.getDisplayId() == ((SomeArgs)localObject1).argi3)) || (!ViewRootImpl.this.mAdded)) {
            break;
          }
          localObject1 = (SomeArgs)paramMessage.obj;
          k = ((SomeArgs)localObject1).argi3;
          MergedConfiguration localMergedConfiguration = (MergedConfiguration)((SomeArgs)localObject1).arg4;
          if (ViewRootImpl.this.mDisplay.getDisplayId() != k) {
            i = 1;
          } else {
            i = 0;
          }
          m = 0;
          if (!ViewRootImpl.this.mLastReportedMergedConfiguration.equals(localMergedConfiguration))
          {
            localObject2 = ViewRootImpl.this;
            if (i != 0) {
              j = k;
            }
            ((ViewRootImpl)localObject2).performConfigurationChange(localMergedConfiguration, false, j);
            j = 1;
          }
          else
          {
            j = m;
            if (i != 0)
            {
              localObject2 = ViewRootImpl.this;
              ((ViewRootImpl)localObject2).onMovedToDisplay(k, ((ViewRootImpl)localObject2).mLastConfigurationFromResources);
              j = m;
            }
          }
          if ((ViewRootImpl.this.mWinFrame.equals(((SomeArgs)localObject1).arg1)) && (ViewRootImpl.this.mPendingOverscanInsets.equals(((SomeArgs)localObject1).arg5)) && (ViewRootImpl.this.mPendingContentInsets.equals(((SomeArgs)localObject1).arg2)) && (ViewRootImpl.this.mPendingStableInsets.equals(((SomeArgs)localObject1).arg6)) && (ViewRootImpl.this.mPendingDisplayCutout.get().equals(((SomeArgs)localObject1).arg9)) && (ViewRootImpl.this.mPendingVisibleInsets.equals(((SomeArgs)localObject1).arg3)) && (ViewRootImpl.this.mPendingOutsets.equals(((SomeArgs)localObject1).arg7))) {
            i = 0;
          } else {
            i = 1;
          }
          ViewRootImpl.this.setFrame((Rect)((SomeArgs)localObject1).arg1);
          ViewRootImpl.this.mPendingOverscanInsets.set((Rect)((SomeArgs)localObject1).arg5);
          ViewRootImpl.this.mPendingContentInsets.set((Rect)((SomeArgs)localObject1).arg2);
          ViewRootImpl.this.mPendingStableInsets.set((Rect)((SomeArgs)localObject1).arg6);
          ViewRootImpl.this.mPendingDisplayCutout.set((DisplayCutout)((SomeArgs)localObject1).arg9);
          ViewRootImpl.this.mPendingVisibleInsets.set((Rect)((SomeArgs)localObject1).arg3);
          ViewRootImpl.this.mPendingOutsets.set((Rect)((SomeArgs)localObject1).arg7);
          ViewRootImpl.this.mPendingBackDropFrame.set((Rect)((SomeArgs)localObject1).arg8);
          localObject2 = ViewRootImpl.this;
          if (((SomeArgs)localObject1).argi1 != 0) {
            bool3 = true;
          } else {
            bool3 = false;
          }
          ((ViewRootImpl)localObject2).mForceNextWindowRelayout = bool3;
          localObject2 = ViewRootImpl.this;
          if (((SomeArgs)localObject1).argi2 != 0) {
            bool3 = bool2;
          } else {
            bool3 = false;
          }
          ((ViewRootImpl)localObject2).mPendingAlwaysConsumeSystemBars = bool3;
          ((SomeArgs)localObject1).recycle();
          if (paramMessage.what == 5) {
            ViewRootImpl.this.reportNextDraw();
          }
          if ((ViewRootImpl.this.mView != null) && ((i != 0) || (j != 0))) {
            ViewRootImpl.forceLayout(ViewRootImpl.this.mView);
          }
          ViewRootImpl.this.requestLayout();
          break;
        case 3: 
          ViewRootImpl.this.doDie();
          break;
        case 2: 
          paramMessage = (View.AttachInfo.InvalidateInfo)paramMessage.obj;
          paramMessage.target.invalidate(paramMessage.left, paramMessage.top, paramMessage.right, paramMessage.bottom);
          paramMessage.recycle();
          break;
        case 1: 
          ((View)paramMessage.obj).invalidate();
          break;
        }
      }
      else
      {
        ViewRootImpl.this.handleCastModeChange();
      }
    }
    
    public boolean sendMessageAtTime(Message paramMessage, long paramLong)
    {
      if ((paramMessage.what == 26) && (paramMessage.obj == null)) {
        throw new NullPointerException("Attempted to call MSG_REQUEST_KEYBOARD_SHORTCUTS with null receiver:");
      }
      return super.sendMessageAtTime(paramMessage, paramLong);
    }
  }
  
  static class W
    extends IWindow.Stub
  {
    private final WeakReference<ViewRootImpl> mViewAncestor;
    private final IWindowSession mWindowSession;
    
    W(ViewRootImpl paramViewRootImpl)
    {
      this.mViewAncestor = new WeakReference(paramViewRootImpl);
      this.mWindowSession = paramViewRootImpl.mWindowSession;
    }
    
    private static int checkCallingPermission(String paramString)
    {
      try
      {
        int i = ActivityManager.getService().checkPermission(paramString, Binder.getCallingPid(), Binder.getCallingUid());
        return i;
      }
      catch (RemoteException paramString) {}
      return -1;
    }
    
    public void closeSystemDialogs(String paramString)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.dispatchCloseSystemDialogs(paramString);
      }
    }
    
    public void dispatchAppVisibility(boolean paramBoolean)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.dispatchAppVisibility(paramBoolean);
      }
    }
    
    public void dispatchDragEvent(DragEvent paramDragEvent)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.dispatchDragEvent(paramDragEvent);
      }
    }
    
    public void dispatchGetNewSurface()
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.dispatchGetNewSurface();
      }
    }
    
    public void dispatchPointerCaptureChanged(boolean paramBoolean)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.dispatchPointerCaptureChanged(paramBoolean);
      }
    }
    
    public void dispatchSystemUiVisibilityChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.dispatchSystemUiVisibilityChanged(paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void dispatchWallpaperCommand(String paramString, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle, boolean paramBoolean)
    {
      if (paramBoolean) {
        try
        {
          this.mWindowSession.wallpaperCommandComplete(asBinder(), null);
        }
        catch (RemoteException paramString) {}
      }
    }
    
    public void dispatchWallpaperOffsets(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, boolean paramBoolean)
    {
      if (paramBoolean) {
        try
        {
          this.mWindowSession.wallpaperOffsetsComplete(asBinder());
        }
        catch (RemoteException localRemoteException) {}
      }
    }
    
    public void dispatchWindowShown()
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.dispatchWindowShown();
      }
    }
    
    /* Error */
    public void executeCommand(String paramString1, String paramString2, android.os.ParcelFileDescriptor paramParcelFileDescriptor)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 24	android/view/ViewRootImpl$W:mViewAncestor	Ljava/lang/ref/WeakReference;
      //   4: invokevirtual 59	java/lang/ref/WeakReference:get	()Ljava/lang/Object;
      //   7: checkcast 6	android/view/ViewRootImpl
      //   10: astore 4
      //   12: aload 4
      //   14: ifnull +184 -> 198
      //   17: aload 4
      //   19: getfield 109	android/view/ViewRootImpl:mView	Landroid/view/View;
      //   22: astore 5
      //   24: aload 5
      //   26: ifnull +172 -> 198
      //   29: ldc 111
      //   31: invokestatic 113	android/view/ViewRootImpl$W:checkCallingPermission	(Ljava/lang/String;)I
      //   34: ifne +114 -> 148
      //   37: aconst_null
      //   38: astore 6
      //   40: aconst_null
      //   41: astore 7
      //   43: aload 7
      //   45: astore 4
      //   47: aload 6
      //   49: astore 8
      //   51: new 115	android/os/ParcelFileDescriptor$AutoCloseOutputStream
      //   54: astore 9
      //   56: aload 7
      //   58: astore 4
      //   60: aload 6
      //   62: astore 8
      //   64: aload 9
      //   66: aload_3
      //   67: invokespecial 118	android/os/ParcelFileDescriptor$AutoCloseOutputStream:<init>	(Landroid/os/ParcelFileDescriptor;)V
      //   70: aload 9
      //   72: astore_3
      //   73: aload_3
      //   74: astore 4
      //   76: aload_3
      //   77: astore 8
      //   79: aload 5
      //   81: aload_1
      //   82: aload_2
      //   83: aload_3
      //   84: invokestatic 124	android/view/ViewDebug:dispatchCommand	(Landroid/view/View;Ljava/lang/String;Ljava/lang/String;Ljava/io/OutputStream;)V
      //   87: aload_3
      //   88: invokevirtual 129	java/io/OutputStream:close	()V
      //   91: goto +26 -> 117
      //   94: astore_1
      //   95: goto +33 -> 128
      //   98: astore_1
      //   99: aload 8
      //   101: astore 4
      //   103: aload_1
      //   104: invokevirtual 132	java/io/IOException:printStackTrace	()V
      //   107: aload 8
      //   109: ifnull +89 -> 198
      //   112: aload 8
      //   114: invokevirtual 129	java/io/OutputStream:close	()V
      //   117: goto +81 -> 198
      //   120: astore_1
      //   121: aload_1
      //   122: invokevirtual 132	java/io/IOException:printStackTrace	()V
      //   125: goto -8 -> 117
      //   128: aload 4
      //   130: ifnull +16 -> 146
      //   133: aload 4
      //   135: invokevirtual 129	java/io/OutputStream:close	()V
      //   138: goto +8 -> 146
      //   141: astore_2
      //   142: aload_2
      //   143: invokevirtual 132	java/io/IOException:printStackTrace	()V
      //   146: aload_1
      //   147: athrow
      //   148: new 134	java/lang/StringBuilder
      //   151: dup
      //   152: invokespecial 135	java/lang/StringBuilder:<init>	()V
      //   155: astore_1
      //   156: aload_1
      //   157: ldc -119
      //   159: invokevirtual 141	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   162: pop
      //   163: aload_1
      //   164: invokestatic 44	android/os/Binder:getCallingPid	()I
      //   167: invokevirtual 144	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   170: pop
      //   171: aload_1
      //   172: ldc -110
      //   174: invokevirtual 141	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   177: pop
      //   178: aload_1
      //   179: invokestatic 47	android/os/Binder:getCallingUid	()I
      //   182: invokevirtual 144	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   185: pop
      //   186: new 148	java/lang/SecurityException
      //   189: dup
      //   190: aload_1
      //   191: invokevirtual 152	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   194: invokespecial 154	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
      //   197: athrow
      //   198: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	199	0	this	W
      //   0	199	1	paramString1	String
      //   0	199	2	paramString2	String
      //   0	199	3	paramParcelFileDescriptor	android.os.ParcelFileDescriptor
      //   10	124	4	localObject1	Object
      //   22	58	5	localView	View
      //   38	23	6	localObject2	Object
      //   41	16	7	localObject3	Object
      //   49	64	8	localObject4	Object
      //   54	17	9	localAutoCloseOutputStream	android.os.ParcelFileDescriptor.AutoCloseOutputStream
      // Exception table:
      //   from	to	target	type
      //   51	56	94	finally
      //   64	70	94	finally
      //   79	87	94	finally
      //   103	107	94	finally
      //   51	56	98	java/io/IOException
      //   64	70	98	java/io/IOException
      //   79	87	98	java/io/IOException
      //   87	91	120	java/io/IOException
      //   112	117	120	java/io/IOException
      //   133	138	141	java/io/IOException
    }
    
    public void insetsChanged(InsetsState paramInsetsState)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.dispatchInsetsChanged(paramInsetsState);
      }
    }
    
    public void insetsControlChanged(InsetsState paramInsetsState, InsetsSourceControl[] paramArrayOfInsetsSourceControl)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.dispatchInsetsControlChanged(paramInsetsState, paramArrayOfInsetsSourceControl);
      }
    }
    
    public void moved(int paramInt1, int paramInt2)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.dispatchMoved(paramInt1, paramInt2);
      }
    }
    
    public void notifyCastMode(boolean paramBoolean)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.notifyCastMode(paramBoolean);
      }
    }
    
    public void notifyProjectionMode(boolean paramBoolean)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.notifyProjectionMode(paramBoolean);
      }
    }
    
    public void notifyRotationChanged(boolean paramBoolean)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.notifyRotationChanged(paramBoolean);
      }
    }
    
    public void requestAppKeyboardShortcuts(IResultReceiver paramIResultReceiver, int paramInt)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.dispatchRequestKeyboardShortcuts(paramIResultReceiver, paramInt);
      }
    }
    
    public void resized(Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5, Rect paramRect6, boolean paramBoolean1, MergedConfiguration paramMergedConfiguration, Rect paramRect7, boolean paramBoolean2, boolean paramBoolean3, int paramInt, DisplayCutout.ParcelableWrapper paramParcelableWrapper)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.dispatchResized(paramRect1, paramRect2, paramRect3, paramRect4, paramRect5, paramRect6, paramBoolean1, paramMergedConfiguration, paramRect7, paramBoolean2, paramBoolean3, paramInt, paramParcelableWrapper);
      }
    }
    
    public void updatePointerIcon(float paramFloat1, float paramFloat2)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.updatePointerIcon(paramFloat1, paramFloat2);
      }
    }
    
    public void windowFocusChanged(boolean paramBoolean1, boolean paramBoolean2)
    {
      ViewRootImpl localViewRootImpl = (ViewRootImpl)this.mViewAncestor.get();
      if (localViewRootImpl != null) {
        localViewRootImpl.windowFocusChanged(paramBoolean1, paramBoolean2);
      }
    }
  }
  
  final class WindowInputEventReceiver
    extends InputEventReceiver
  {
    public WindowInputEventReceiver(InputChannel paramInputChannel, Looper paramLooper)
    {
      super(paramLooper);
    }
    
    public void dispose()
    {
      ViewRootImpl.this.unscheduleConsumeBatchedInput();
      super.dispose();
    }
    
    public void onBatchedInputEventPending()
    {
      if (ViewRootImpl.this.mUnbufferedInputDispatch) {
        super.onBatchedInputEventPending();
      } else {
        ViewRootImpl.this.scheduleConsumeBatchedInput();
      }
    }
    
    public void onInputEvent(InputEvent paramInputEvent)
    {
      ViewRootImplInjector.logOnInputEvent(paramInputEvent);
      Trace.traceBegin(8L, "processInputEventForCompatibility");
      try
      {
        List localList = ViewRootImpl.this.mInputCompatProcessor.processInputEventForCompatibility(paramInputEvent);
        Trace.traceEnd(8L);
        if (localList != null)
        {
          if (localList.isEmpty()) {
            finishInputEvent(paramInputEvent, true);
          } else {
            for (int i = 0; i < localList.size(); i++) {
              ViewRootImpl.this.enqueueInputEvent((InputEvent)localList.get(i), this, 64, true);
            }
          }
        }
        else {
          ViewRootImpl.this.enqueueInputEvent(paramInputEvent, this, 0, true);
        }
        return;
      }
      finally
      {
        Trace.traceEnd(8L);
      }
    }
  }
  
  static abstract interface WindowStoppedCallback
  {
    public abstract void windowStopped(boolean paramBoolean);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ViewRootImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */