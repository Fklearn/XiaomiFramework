package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.FilterComparison;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.StrictMode;
import android.os.StrictMode.Span;
import android.os.Trace;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.StateSet;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.BaseSavedState;
import android.view.ViewConfiguration;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewHierarchyEncoder;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnTouchModeChangeListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputContentInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.R.styleable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbsListView
  extends AdapterView<ListAdapter>
  implements TextWatcher, ViewTreeObserver.OnGlobalLayoutListener, Filter.FilterListener, ViewTreeObserver.OnTouchModeChangeListener, RemoteViewsAdapter.RemoteAdapterConnectionCallback
{
  private static final int CHECK_POSITION_SEARCH_DISTANCE = 20;
  public static final int CHOICE_MODE_MULTIPLE = 2;
  public static final int CHOICE_MODE_MULTIPLE_MODAL = 3;
  public static final int CHOICE_MODE_NONE = 0;
  public static final int CHOICE_MODE_SINGLE = 1;
  private static final int INVALID_POINTER = -1;
  static final int LAYOUT_FORCE_BOTTOM = 3;
  static final int LAYOUT_FORCE_TOP = 1;
  static final int LAYOUT_MOVE_SELECTION = 6;
  static final int LAYOUT_NORMAL = 0;
  static final int LAYOUT_SET_SELECTION = 2;
  static final int LAYOUT_SPECIFIC = 4;
  static final int LAYOUT_SYNC = 5;
  private static final double MOVE_TOUCH_SLOP = 0.6D;
  private static final boolean OPTS_INPUT = true;
  static final int OVERSCROLL_LIMIT_DIVISOR = 3;
  private static final boolean PROFILE_FLINGING = false;
  private static final boolean PROFILE_SCROLLING = false;
  private static final String TAG = "AbsListView";
  static final int TOUCH_MODE_DONE_WAITING = 2;
  static final int TOUCH_MODE_DOWN = 0;
  static final int TOUCH_MODE_FLING = 4;
  private static final int TOUCH_MODE_OFF = 1;
  private static final int TOUCH_MODE_ON = 0;
  static final int TOUCH_MODE_OVERFLING = 6;
  static final int TOUCH_MODE_OVERSCROLL = 5;
  static final int TOUCH_MODE_REST = -1;
  static final int TOUCH_MODE_SCROLL = 3;
  static final int TOUCH_MODE_TAP = 1;
  private static final int TOUCH_MODE_UNKNOWN = -1;
  private static final double TOUCH_SLOP_MAX = 1.0D;
  private static final double TOUCH_SLOP_MIN = 0.6D;
  public static final int TRANSCRIPT_MODE_ALWAYS_SCROLL = 2;
  public static final int TRANSCRIPT_MODE_DISABLED = 0;
  public static final int TRANSCRIPT_MODE_NORMAL = 1;
  static final Interpolator sLinearInterpolator = new LinearInterpolator();
  private ListItemAccessibilityDelegate mAccessibilityDelegate;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private int mActivePointerId = -1;
  @UnsupportedAppUsage
  ListAdapter mAdapter;
  boolean mAdapterHasStableIds;
  private int mCacheColorHint;
  boolean mCachingActive;
  boolean mCachingStarted;
  SparseBooleanArray mCheckStates;
  LongSparseArray<Integer> mCheckedIdStates;
  int mCheckedItemCount;
  @UnsupportedAppUsage
  ActionMode mChoiceActionMode;
  int mChoiceMode = 0;
  private Runnable mClearScrollingCache;
  @UnsupportedAppUsage
  private ContextMenu.ContextMenuInfo mContextMenuInfo = null;
  @UnsupportedAppUsage
  AdapterDataSetObserver mDataSetObserver;
  private InputConnection mDefInputConnection;
  int mDefaultOverflingDistance;
  int mDefaultOverscrollDistance;
  private boolean mDeferNotifyDataSetChanged = false;
  private float mDensityScale;
  private int mDirection = 0;
  boolean mDrawSelectorOnTop = false;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123768444L)
  EdgeEffect mEdgeGlowBottom = new EdgeEffect(this.mContext);
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769408L)
  EdgeEffect mEdgeGlowTop = new EdgeEffect(this.mContext);
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123768941L)
  private FastScroller mFastScroll;
  boolean mFastScrollAlwaysVisible;
  boolean mFastScrollEnabled;
  private int mFastScrollStyle;
  private boolean mFiltered;
  private int mFirstPositionDistanceGuess;
  private boolean mFlingProfilingStarted = false;
  @UnsupportedAppUsage(maxTargetSdk=28)
  FlingRunnable mFlingRunnable;
  private StrictMode.Span mFlingStrictSpan = null;
  private boolean mForceTranscriptScroll;
  private boolean mGlobalLayoutListenerAddedFilter;
  private boolean mHasPerformedLongPress;
  @UnsupportedAppUsage
  private boolean mIsChildViewEnabled;
  private boolean mIsDetaching;
  private boolean mIsFirstTouchMoveEvent = false;
  final boolean[] mIsScrap = new boolean[1];
  private int mLastAccessibilityScrollEventFromIndex;
  private int mLastAccessibilityScrollEventToIndex;
  private int mLastHandledItemCount;
  private int mLastPositionDistanceGuess;
  private int mLastScrollState = 0;
  private int mLastTouchMode = -1;
  int mLastY;
  @UnsupportedAppUsage
  int mLayoutMode = 0;
  Rect mListPadding = new Rect();
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=124051740L)
  private int mMaximumVelocity;
  private int mMinimumVelocity;
  int mMotionCorrection;
  @UnsupportedAppUsage
  int mMotionPosition;
  int mMotionViewNewTop;
  int mMotionViewOriginalTop;
  int mMotionX;
  @UnsupportedAppUsage
  int mMotionY;
  private int mMoveAcceleration;
  MultiChoiceModeWrapper mMultiChoiceModeCallback;
  private int mNestedYOffset = 0;
  private int mNumTouchMoveEvent = 0;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769353L)
  private OnScrollListener mOnScrollListener;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769379L)
  int mOverflingDistance;
  @UnsupportedAppUsage
  int mOverscrollDistance;
  int mOverscrollMax;
  private final Thread mOwnerThread;
  private CheckForKeyLongPress mPendingCheckForKeyLongPress;
  @UnsupportedAppUsage
  private CheckForLongPress mPendingCheckForLongPress;
  @UnsupportedAppUsage
  private CheckForTap mPendingCheckForTap;
  private SavedState mPendingSync;
  private PerformClick mPerformClick;
  @UnsupportedAppUsage
  PopupWindow mPopup;
  private boolean mPopupHidden;
  Runnable mPositionScrollAfterLayout;
  @UnsupportedAppUsage
  AbsPositionScroller mPositionScroller;
  private InputConnectionWrapper mPublicInputConnection;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769398L)
  final RecycleBin mRecycler = new RecycleBin();
  private RemoteViewsAdapter mRemoteAdapter;
  int mResurrectToPosition = -1;
  private final int[] mScrollConsumed = new int[2];
  View mScrollDown;
  private final int[] mScrollOffset = new int[2];
  private boolean mScrollProfilingStarted = false;
  private StrictMode.Span mScrollStrictSpan = null;
  View mScrollUp;
  boolean mScrollingCacheEnabled;
  int mSelectedTop = 0;
  @UnsupportedAppUsage
  int mSelectionBottomPadding = 0;
  int mSelectionLeftPadding = 0;
  int mSelectionRightPadding = 0;
  @UnsupportedAppUsage
  int mSelectionTopPadding = 0;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  Drawable mSelector;
  @UnsupportedAppUsage(maxTargetSdk=28)
  int mSelectorPosition = -1;
  @UnsupportedAppUsage(maxTargetSdk=28)
  Rect mSelectorRect = new Rect();
  private int[] mSelectorState;
  private boolean mSmoothScrollbarEnabled = true;
  boolean mStackFromBottom;
  EditText mTextFilter;
  private boolean mTextFilterEnabled;
  private final float[] mTmpPoint = new float[2];
  private Rect mTouchFrame;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769413L)
  int mTouchMode = -1;
  private Runnable mTouchModeReset;
  @UnsupportedAppUsage
  private int mTouchSlop;
  private int mTranscriptMode;
  boolean mUsingMiuiTheme;
  private float mVelocityScale = 1.0F;
  @UnsupportedAppUsage
  private VelocityTracker mVelocityTracker;
  private float mVerticalScrollFactor;
  int mWidthMeasureSpec = 0;
  
  public AbsListView(Context paramContext)
  {
    super(paramContext);
    initAbsListView();
    this.mOwnerThread = Thread.currentThread();
    setVerticalScrollBarEnabled(true);
    paramContext = paramContext.obtainStyledAttributes(R.styleable.View);
    initializeScrollbarsInternal(paramContext);
    paramContext.recycle();
  }
  
  public AbsListView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842858);
  }
  
  public AbsListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public AbsListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    initAbsListView();
    this.mOwnerThread = Thread.currentThread();
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AbsListView, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.AbsListView, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    paramAttributeSet = localTypedArray.getDrawable(0);
    if (paramAttributeSet != null) {
      setSelector(paramAttributeSet);
    }
    this.mDrawSelectorOnTop = localTypedArray.getBoolean(1, false);
    setStackFromBottom(localTypedArray.getBoolean(2, false));
    setScrollingCacheEnabled(localTypedArray.getBoolean(3, true));
    setTextFilterEnabled(localTypedArray.getBoolean(4, false));
    setTranscriptMode(localTypedArray.getInt(5, 0));
    setCacheColorHint(localTypedArray.getColor(6, 0));
    setSmoothScrollbarEnabled(localTypedArray.getBoolean(9, true));
    setChoiceMode(localTypedArray.getInt(7, 0));
    setFastScrollEnabled(localTypedArray.getBoolean(8, false));
    setFastScrollStyle(localTypedArray.getResourceId(11, 0));
    setFastScrollAlwaysVisible(localTypedArray.getBoolean(10, false));
    localTypedArray.recycle();
    if (paramContext.getResources().getConfiguration().uiMode == 6) {
      setRevealOnFocusHint(false);
    }
  }
  
  private boolean acceptFilter()
  {
    boolean bool;
    if ((this.mTextFilterEnabled) && ((getAdapter() instanceof Filterable)) && (((Filterable)getAdapter()).getFilter() != null)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private void addAccessibilityActionIfEnabled(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean, AccessibilityNodeInfo.AccessibilityAction paramAccessibilityAction)
  {
    if (paramBoolean) {
      paramAccessibilityNodeInfo.addAction(paramAccessibilityAction);
    }
  }
  
  @UnsupportedAppUsage
  private boolean canScrollDown()
  {
    int i = getChildCount();
    int j = this.mFirstPosition;
    int k = this.mItemCount;
    boolean bool1 = false;
    boolean bool2;
    if (j + i < k) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    boolean bool3 = bool2;
    if (!bool2)
    {
      bool3 = bool2;
      if (i > 0)
      {
        bool2 = bool1;
        if (getChildAt(i - 1).getBottom() > this.mBottom - this.mListPadding.bottom) {
          bool2 = true;
        }
        bool3 = bool2;
      }
    }
    return bool3;
  }
  
  @UnsupportedAppUsage
  private boolean canScrollUp()
  {
    int i = this.mFirstPosition;
    boolean bool1 = true;
    boolean bool2;
    if (i > 0) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    boolean bool3 = bool2;
    if (!bool2)
    {
      bool3 = bool2;
      if (getChildCount() > 0)
      {
        if (getChildAt(0).getTop() < this.mListPadding.top) {
          bool2 = bool1;
        } else {
          bool2 = false;
        }
        bool3 = bool2;
      }
    }
    return bool3;
  }
  
  private void clearScrollingCache()
  {
    if (!isHardwareAccelerated())
    {
      if (this.mClearScrollingCache == null) {
        this.mClearScrollingCache = new Runnable()
        {
          public void run()
          {
            if (AbsListView.this.mCachingStarted)
            {
              AbsListView localAbsListView = AbsListView.this;
              localAbsListView.mCachingActive = false;
              localAbsListView.mCachingStarted = false;
              localAbsListView.setChildrenDrawnWithCacheEnabled(false);
              if ((AbsListView.this.mPersistentDrawingCache & 0x2) == 0) {
                AbsListView.this.setChildrenDrawingCacheEnabled(false);
              }
              if (!AbsListView.this.isAlwaysDrawnWithCacheEnabled()) {
                AbsListView.this.invalidate();
              }
            }
          }
        };
      }
      post(this.mClearScrollingCache);
    }
  }
  
  private boolean contentFits()
  {
    int i = getChildCount();
    boolean bool = true;
    if (i == 0) {
      return true;
    }
    if (i != this.mItemCount) {
      return false;
    }
    if ((getChildAt(0).getTop() < this.mListPadding.top) || (getChildAt(i - 1).getBottom() > getHeight() - this.mListPadding.bottom)) {
      bool = false;
    }
    return bool;
  }
  
  private void createScrollingCache()
  {
    if ((this.mScrollingCacheEnabled) && (!this.mCachingStarted) && (!isHardwareAccelerated()))
    {
      setChildrenDrawnWithCacheEnabled(true);
      setChildrenDrawingCacheEnabled(true);
      this.mCachingActive = true;
      this.mCachingStarted = true;
    }
  }
  
  private void createTextFilter(boolean paramBoolean)
  {
    if (this.mPopup == null)
    {
      PopupWindow localPopupWindow = new PopupWindow(getContext());
      localPopupWindow.setFocusable(false);
      localPopupWindow.setTouchable(false);
      localPopupWindow.setInputMethodMode(2);
      localPopupWindow.setContentView(getTextFilterInput());
      localPopupWindow.setWidth(-2);
      localPopupWindow.setHeight(-2);
      localPopupWindow.setBackgroundDrawable(null);
      this.mPopup = localPopupWindow;
      getViewTreeObserver().addOnGlobalLayoutListener(this);
      this.mGlobalLayoutListenerAddedFilter = true;
    }
    if (paramBoolean) {
      this.mPopup.setAnimationStyle(16974601);
    } else {
      this.mPopup.setAnimationStyle(16974602);
    }
  }
  
  private void dismissPopup()
  {
    PopupWindow localPopupWindow = this.mPopup;
    if (localPopupWindow != null) {
      localPopupWindow.dismiss();
    }
  }
  
  private void drawSelector(Canvas paramCanvas)
  {
    if (shouldDrawSelector())
    {
      Drawable localDrawable = this.mSelector;
      localDrawable.setBounds(this.mSelectorRect);
      localDrawable.draw(paramCanvas);
    }
  }
  
  private void finishGlows()
  {
    if (shouldDisplayEdgeEffects())
    {
      this.mEdgeGlowTop.finish();
      this.mEdgeGlowBottom.finish();
    }
  }
  
  static int getDistance(Rect paramRect1, Rect paramRect2, int paramInt)
  {
    int i;
    int j;
    int k;
    if ((paramInt != 1) && (paramInt != 2))
    {
      if (paramInt != 17)
      {
        if (paramInt != 33)
        {
          if (paramInt != 66)
          {
            if (paramInt == 130)
            {
              paramInt = paramRect1.left + paramRect1.width() / 2;
              i = paramRect1.bottom;
              j = paramRect2.left + paramRect2.width() / 2;
              k = paramRect2.top;
            }
            else
            {
              throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}.");
            }
          }
          else
          {
            paramInt = paramRect1.right;
            i = paramRect1.top + paramRect1.height() / 2;
            j = paramRect2.left;
            k = paramRect2.top + paramRect2.height() / 2;
          }
        }
        else
        {
          paramInt = paramRect1.left + paramRect1.width() / 2;
          i = paramRect1.top;
          j = paramRect2.left + paramRect2.width() / 2;
          k = paramRect2.bottom;
        }
      }
      else
      {
        paramInt = paramRect1.left;
        i = paramRect1.top + paramRect1.height() / 2;
        j = paramRect2.right;
        k = paramRect2.top + paramRect2.height() / 2;
      }
    }
    else
    {
      paramInt = paramRect1.right + paramRect1.width() / 2;
      i = paramRect1.top + paramRect1.height() / 2;
      j = paramRect2.left + paramRect2.width() / 2;
      k = paramRect2.top + paramRect2.height() / 2;
    }
    paramInt = j - paramInt;
    k -= i;
    return k * k + paramInt * paramInt;
  }
  
  private int[] getDrawableStateForSelector()
  {
    if (this.mIsChildViewEnabled) {
      return super.getDrawableState();
    }
    int i = ENABLED_STATE_SET[0];
    int[] arrayOfInt = onCreateDrawableState(1);
    int j = -1;
    int m;
    for (int k = arrayOfInt.length - 1;; k--)
    {
      m = j;
      if (k < 0) {
        break;
      }
      if (arrayOfInt[k] == i)
      {
        m = k;
        break;
      }
    }
    if (m >= 0) {
      System.arraycopy(arrayOfInt, m + 1, arrayOfInt, m, arrayOfInt.length - m - 1);
    }
    return arrayOfInt;
  }
  
  private EditText getTextFilterInput()
  {
    if (this.mTextFilter == null)
    {
      this.mTextFilter = ((EditText)LayoutInflater.from(getContext()).inflate(17367353, null));
      this.mTextFilter.setRawInputType(177);
      this.mTextFilter.setImeOptions(268435456);
      this.mTextFilter.addTextChangedListener(this);
    }
    return this.mTextFilter;
  }
  
  private void initAbsListView()
  {
    setClickable(true);
    setFocusableInTouchMode(true);
    setWillNotDraw(false);
    setAlwaysDrawnWithCacheEnabled(false);
    setScrollingCacheEnabled(true);
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(this.mContext);
    this.mTouchSlop = localViewConfiguration.getScaledTouchSlop();
    this.mVerticalScrollFactor = localViewConfiguration.getScaledVerticalScrollFactor();
    if (0.6D > 0.0D)
    {
      if (0.6D < 0.6D) {
        this.mMoveAcceleration = ((int)(this.mTouchSlop * 0.6D));
      } else if ((0.6D >= 0.6D) && (0.6D < 1.0D)) {
        this.mMoveAcceleration = ((int)(this.mTouchSlop * 0.6D));
      } else {
        this.mMoveAcceleration = this.mTouchSlop;
      }
    }
    else {
      this.mMoveAcceleration = this.mTouchSlop;
    }
    this.mMinimumVelocity = localViewConfiguration.getScaledMinimumFlingVelocity();
    this.mMaximumVelocity = localViewConfiguration.getScaledMaximumFlingVelocity();
    this.mOverscrollDistance = localViewConfiguration.getScaledOverscrollDistance();
    this.mOverflingDistance = localViewConfiguration.getScaledOverflingDistance();
    this.mDensityScale = getContext().getResources().getDisplayMetrics().density;
    AbsListViewInjector.onInit(this);
  }
  
  private void initOrResetVelocityTracker()
  {
    VelocityTracker localVelocityTracker = this.mVelocityTracker;
    if (localVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    } else {
      localVelocityTracker.clear();
    }
  }
  
  private void initVelocityTrackerIfNotExists()
  {
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
  }
  
  private void invalidateBottomGlow()
  {
    if (!shouldDisplayEdgeEffects()) {
      return;
    }
    boolean bool = getClipToPadding();
    int i = getHeight();
    int j = i;
    if (bool) {
      j = i - this.mPaddingBottom;
    }
    if (bool) {
      i = this.mPaddingLeft;
    } else {
      i = 0;
    }
    int k = getWidth();
    int m = k;
    if (bool) {
      m = k - this.mPaddingRight;
    }
    invalidate(i, j - this.mEdgeGlowBottom.getMaxHeight(), m, j);
  }
  
  private void invalidateTopGlow()
  {
    if (!shouldDisplayEdgeEffects()) {
      return;
    }
    boolean bool = getClipToPadding();
    int i = 0;
    int j;
    if (bool) {
      j = this.mPaddingTop;
    } else {
      j = 0;
    }
    if (bool) {
      i = this.mPaddingLeft;
    }
    int k = getWidth();
    int m = k;
    if (bool) {
      m = k - this.mPaddingRight;
    }
    invalidate(i, j, m, this.mEdgeGlowTop.getMaxHeight() + j);
  }
  
  private boolean isItemClickable(View paramView)
  {
    return paramView.hasExplicitFocusable() ^ true;
  }
  
  private boolean isOwnerThread()
  {
    boolean bool;
    if (this.mOwnerThread == Thread.currentThread()) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private void onSecondaryPointerUp(MotionEvent paramMotionEvent)
  {
    int i = (paramMotionEvent.getAction() & 0xFF00) >> 8;
    if (paramMotionEvent.getPointerId(i) == this.mActivePointerId)
    {
      if (i == 0) {
        i = 1;
      } else {
        i = 0;
      }
      this.mMotionX = ((int)paramMotionEvent.getX(i));
      this.mMotionY = ((int)paramMotionEvent.getY(i));
      this.mMotionCorrection = 0;
      this.mActivePointerId = paramMotionEvent.getPointerId(i);
    }
  }
  
  private void onTouchCancel()
  {
    int i = this.mTouchMode;
    if (i != 5)
    {
      if (i != 6)
      {
        this.mTouchMode = -1;
        setPressed(false);
        View localView = getChildAt(this.mMotionPosition - this.mFirstPosition);
        if (localView != null) {
          localView.setPressed(false);
        }
        clearScrollingCache();
        removeCallbacks(this.mPendingCheckForLongPress);
        recycleVelocityTracker();
      }
    }
    else
    {
      if (this.mFlingRunnable == null) {
        this.mFlingRunnable = new FlingRunnable();
      }
      this.mFlingRunnable.startSpringback();
    }
    if (shouldDisplayEdgeEffects())
    {
      this.mEdgeGlowTop.onRelease();
      this.mEdgeGlowBottom.onRelease();
    }
    this.mActivePointerId = -1;
  }
  
  private void onTouchDown(MotionEvent paramMotionEvent)
  {
    this.mHasPerformedLongPress = false;
    this.mActivePointerId = paramMotionEvent.getPointerId(0);
    hideSelector();
    if (this.mTouchMode == 6)
    {
      this.mFlingRunnable.endFling();
      AbsPositionScroller localAbsPositionScroller = this.mPositionScroller;
      if (localAbsPositionScroller != null) {
        localAbsPositionScroller.stop();
      }
      this.mTouchMode = 5;
      this.mMotionX = ((int)paramMotionEvent.getX());
      this.mMotionY = ((int)paramMotionEvent.getY());
      this.mLastY = this.mMotionY;
      this.mMotionCorrection = 0;
      this.mDirection = 0;
    }
    else
    {
      int i = (int)paramMotionEvent.getX();
      int j = (int)paramMotionEvent.getY();
      int k = pointToPosition(i, j);
      int m = k;
      if (!this.mDataChanged) {
        if (this.mTouchMode == 4)
        {
          createScrollingCache();
          this.mTouchMode = 3;
          this.mMotionCorrection = 0;
          m = findMotionRow(j);
          this.mFlingRunnable.flywheelTouch();
        }
        else
        {
          m = k;
          if (k >= 0)
          {
            m = k;
            if (((ListAdapter)getAdapter()).isEnabled(k))
            {
              this.mTouchMode = 0;
              if (this.mPendingCheckForTap == null) {
                this.mPendingCheckForTap = new CheckForTap(null);
              }
              this.mPendingCheckForTap.x = paramMotionEvent.getX();
              this.mPendingCheckForTap.y = paramMotionEvent.getY();
              postDelayed(this.mPendingCheckForTap, ViewConfiguration.getTapTimeout());
              m = k;
            }
          }
        }
      }
      if (m >= 0) {
        this.mMotionViewOriginalTop = getChildAt(m - this.mFirstPosition).getTop();
      }
      this.mMotionX = i;
      this.mMotionY = j;
      this.mMotionPosition = m;
      this.mLastY = Integer.MIN_VALUE;
    }
    if ((this.mTouchMode == 0) && (this.mMotionPosition != -1) && (performButtonActionOnTouchDown(paramMotionEvent))) {
      removeCallbacks(this.mPendingCheckForTap);
    }
  }
  
  private void onTouchMove(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2)
  {
    if (this.mHasPerformedLongPress) {
      return;
    }
    int i = paramMotionEvent1.findPointerIndex(this.mActivePointerId);
    int j = i;
    if (i == -1)
    {
      j = 0;
      this.mActivePointerId = paramMotionEvent1.getPointerId(0);
    }
    if (this.mDataChanged) {
      layoutChildren();
    }
    i = (int)paramMotionEvent1.getY(j);
    int k = this.mTouchMode;
    if ((k != 0) && (k != 1) && (k != 2))
    {
      if ((k == 3) || (k == 5)) {
        scrollIfNeeded((int)paramMotionEvent1.getX(j), i, paramMotionEvent2);
      }
    }
    else if (!startScrollIfNeeded((int)paramMotionEvent1.getX(j), i, paramMotionEvent2))
    {
      paramMotionEvent2 = getChildAt(this.mMotionPosition - this.mFirstPosition);
      float f = paramMotionEvent1.getX(j);
      if (!pointInView(f, i, this.mTouchSlop))
      {
        setPressed(false);
        if (paramMotionEvent2 != null) {
          paramMotionEvent2.setPressed(false);
        }
        if (this.mTouchMode == 0) {
          paramMotionEvent1 = this.mPendingCheckForTap;
        } else {
          paramMotionEvent1 = this.mPendingCheckForLongPress;
        }
        removeCallbacks(paramMotionEvent1);
        this.mTouchMode = 2;
        updateSelectorState();
      }
      else if (paramMotionEvent2 != null)
      {
        paramMotionEvent1 = this.mTmpPoint;
        paramMotionEvent1[0] = f;
        paramMotionEvent1[1] = i;
        transformPointToViewLocal(paramMotionEvent1, paramMotionEvent2);
        paramMotionEvent2.drawableHotspotChanged(paramMotionEvent1[0], paramMotionEvent1[1]);
      }
    }
  }
  
  private void onTouchUp(MotionEvent paramMotionEvent)
  {
    int i = this.mTouchMode;
    int n;
    if ((i != 0) && (i != 1) && (i != 2))
    {
      if (i != 3)
      {
        if (i == 5)
        {
          if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable();
          }
          paramMotionEvent = this.mVelocityTracker;
          paramMotionEvent.computeCurrentVelocity(1000, this.mMaximumVelocity);
          i = (int)paramMotionEvent.getYVelocity(this.mActivePointerId);
          reportScrollStateChange(2);
          if (Math.abs(i) > this.mMinimumVelocity) {
            this.mFlingRunnable.startOverfling(-i);
          } else {
            this.mFlingRunnable.startSpringback();
          }
        }
      }
      else
      {
        int j = getChildCount();
        if (j > 0)
        {
          int k = getChildAt(0).getTop();
          int m = getChildAt(j - 1).getBottom();
          n = this.mListPadding.top;
          int i1 = getHeight() - this.mListPadding.bottom;
          if ((this.mFirstPosition == 0) && (k >= n) && (this.mFirstPosition + j < this.mItemCount) && (m <= getHeight() - i1))
          {
            this.mTouchMode = -1;
            reportScrollStateChange(0);
          }
          else
          {
            paramMotionEvent = this.mVelocityTracker;
            paramMotionEvent.computeCurrentVelocity(1000, this.mMaximumVelocity);
            int i2 = (int)(paramMotionEvent.getYVelocity(this.mActivePointerId) * this.mVelocityScale);
            if (Math.abs(i2) > this.mMinimumVelocity) {
              i = 1;
            } else {
              i = 0;
            }
            if ((i != 0) && ((this.mFirstPosition != 0) || (k != n - this.mOverscrollDistance)) && ((this.mFirstPosition + j != this.mItemCount) || (m != this.mOverscrollDistance + i1)))
            {
              if (!dispatchNestedPreFling(0.0F, -i2))
              {
                if (this.mFlingRunnable == null) {
                  this.mFlingRunnable = new FlingRunnable();
                }
                reportScrollStateChange(2);
                this.mFlingRunnable.start(-i2);
                dispatchNestedFling(0.0F, -i2, true);
              }
              else
              {
                this.mTouchMode = -1;
                reportScrollStateChange(0);
              }
            }
            else
            {
              this.mTouchMode = -1;
              reportScrollStateChange(0);
              paramMotionEvent = this.mFlingRunnable;
              if (paramMotionEvent != null) {
                paramMotionEvent.endFling();
              }
              paramMotionEvent = this.mPositionScroller;
              if (paramMotionEvent != null) {
                paramMotionEvent.stop();
              }
              if ((i != 0) && (!dispatchNestedPreFling(0.0F, -i2))) {
                dispatchNestedFling(0.0F, -i2, false);
              }
            }
          }
        }
        else
        {
          this.mTouchMode = -1;
          reportScrollStateChange(0);
        }
      }
    }
    else
    {
      n = this.mMotionPosition;
      final View localView = getChildAt(n - this.mFirstPosition);
      if (localView != null)
      {
        if (this.mTouchMode != 0) {
          localView.setPressed(false);
        }
        float f = paramMotionEvent.getX();
        if ((f > this.mListPadding.left) && (f < getWidth() - this.mListPadding.right)) {
          i = 1;
        } else {
          i = 0;
        }
        if ((i != 0) && (!localView.hasExplicitFocusable()))
        {
          if (this.mPerformClick == null) {
            this.mPerformClick = new PerformClick(null);
          }
          final PerformClick localPerformClick = this.mPerformClick;
          localPerformClick.mClickMotionPosition = n;
          localPerformClick.rememberWindowAttachCount();
          this.mResurrectToPosition = n;
          i = this.mTouchMode;
          if ((i != 0) && (i != 1))
          {
            if ((!this.mDataChanged) && (this.mAdapter.isEnabled(n))) {
              localPerformClick.run();
            }
          }
          else
          {
            Object localObject;
            if (this.mTouchMode == 0) {
              localObject = this.mPendingCheckForTap;
            } else {
              localObject = this.mPendingCheckForLongPress;
            }
            removeCallbacks((Runnable)localObject);
            this.mLayoutMode = 0;
            if ((!this.mDataChanged) && (this.mAdapter.isEnabled(n)))
            {
              this.mTouchMode = 1;
              setSelectedPositionInt(this.mMotionPosition);
              layoutChildren();
              localView.setPressed(true);
              positionSelector(this.mMotionPosition, localView);
              setPressed(true);
              localObject = this.mSelector;
              if (localObject != null)
              {
                localObject = ((Drawable)localObject).getCurrent();
                if ((localObject != null) && ((localObject instanceof TransitionDrawable))) {
                  ((TransitionDrawable)localObject).resetTransition();
                }
                this.mSelector.setHotspot(f, paramMotionEvent.getY());
              }
              paramMotionEvent = this.mTouchModeReset;
              if (paramMotionEvent != null) {
                removeCallbacks(paramMotionEvent);
              }
              this.mTouchModeReset = new Runnable()
              {
                public void run()
                {
                  AbsListView.access$1502(AbsListView.this, null);
                  AbsListView.this.mTouchMode = -1;
                  localView.setPressed(false);
                  AbsListView.this.setPressed(false);
                  if ((!AbsListView.this.mDataChanged) && (!AbsListView.this.mIsDetaching) && (AbsListView.this.isAttachedToWindow())) {
                    localPerformClick.run();
                  }
                }
              };
              postDelayed(this.mTouchModeReset, ViewConfiguration.getPressedStateDurationForListview());
            }
            else
            {
              this.mTouchMode = -1;
              updateSelectorState();
            }
            return;
          }
        }
      }
      this.mTouchMode = -1;
      updateSelectorState();
    }
    setPressed(false);
    if (shouldDisplayEdgeEffects())
    {
      this.mEdgeGlowTop.onRelease();
      this.mEdgeGlowBottom.onRelease();
    }
    invalidate();
    removeCallbacks(this.mPendingCheckForLongPress);
    recycleVelocityTracker();
    this.mActivePointerId = -1;
    paramMotionEvent = this.mScrollStrictSpan;
    if (paramMotionEvent != null)
    {
      paramMotionEvent.finish();
      this.mScrollStrictSpan = null;
    }
  }
  
  private boolean performStylusButtonPressAction(MotionEvent paramMotionEvent)
  {
    if ((this.mChoiceMode == 3) && (this.mChoiceActionMode == null))
    {
      paramMotionEvent = getChildAt(this.mMotionPosition - this.mFirstPosition);
      if ((paramMotionEvent != null) && (performLongPress(paramMotionEvent, this.mMotionPosition, this.mAdapter.getItemId(this.mMotionPosition))))
      {
        this.mTouchMode = -1;
        setPressed(false);
        paramMotionEvent.setPressed(false);
        return true;
      }
    }
    return false;
  }
  
  private void positionPopup()
  {
    int i = getResources().getDisplayMetrics().heightPixels;
    int[] arrayOfInt = new int[2];
    getLocationOnScreen(arrayOfInt);
    i = i - arrayOfInt[1] - getHeight() + (int)(this.mDensityScale * 20.0F);
    if (!this.mPopup.isShowing()) {
      this.mPopup.showAtLocation(this, 81, arrayOfInt[0], i);
    } else {
      this.mPopup.update(arrayOfInt[0], i, -1, -1);
    }
  }
  
  @UnsupportedAppUsage
  private void positionSelector(int paramInt, View paramView, boolean paramBoolean, float paramFloat1, float paramFloat2)
  {
    int i;
    if (paramInt != this.mSelectorPosition) {
      i = 1;
    } else {
      i = 0;
    }
    if (paramInt != -1) {
      this.mSelectorPosition = paramInt;
    }
    Rect localRect = this.mSelectorRect;
    localRect.set(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom());
    if ((paramView instanceof SelectionBoundsAdjuster)) {
      ((SelectionBoundsAdjuster)paramView).adjustListItemSelectionBounds(localRect);
    }
    localRect.left -= this.mSelectionLeftPadding;
    localRect.top -= this.mSelectionTopPadding;
    localRect.right += this.mSelectionRightPadding;
    localRect.bottom += this.mSelectionBottomPadding;
    boolean bool = paramView.isEnabled();
    if (this.mIsChildViewEnabled != bool) {
      this.mIsChildViewEnabled = bool;
    }
    paramView = this.mSelector;
    if (paramView != null)
    {
      if (i != 0)
      {
        paramView.setVisible(false, false);
        paramView.setState(StateSet.NOTHING);
      }
      paramView.setBounds(localRect);
      if (i != 0)
      {
        if (getVisibility() == 0) {
          paramView.setVisible(true, false);
        }
        updateSelectorState();
      }
      if (paramBoolean) {
        paramView.setHotspot(paramFloat1, paramFloat2);
      }
    }
  }
  
  private void recycleVelocityTracker()
  {
    VelocityTracker localVelocityTracker = this.mVelocityTracker;
    if (localVelocityTracker != null)
    {
      localVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }
  
  private void scrollIfNeeded(int paramInt1, int paramInt2, MotionEvent paramMotionEvent)
  {
    int i = paramInt2 - this.mMotionY;
    int j = i;
    if (this.mLastY == Integer.MIN_VALUE) {
      j = i - this.mMotionCorrection;
    }
    i = this.mLastY;
    if (i != Integer.MIN_VALUE) {
      i -= paramInt2;
    } else {
      i = -j;
    }
    Object localObject;
    int m;
    if (dispatchNestedPreScroll(0, i, this.mScrollConsumed, this.mScrollOffset))
    {
      localObject = this.mScrollConsumed;
      k = localObject[1];
      int[] arrayOfInt = this.mScrollOffset;
      m = -arrayOfInt[1];
      i = localObject[1];
      if (paramMotionEvent != null)
      {
        paramMotionEvent.offsetLocation(0.0F, arrayOfInt[1]);
        this.mNestedYOffset += this.mScrollOffset[1];
      }
      j += k;
    }
    else
    {
      m = 0;
      i = 0;
    }
    int k = this.mLastY;
    if (k != Integer.MIN_VALUE) {
      i = paramInt2 - k + i;
    } else {
      i = j;
    }
    int n = 0;
    int i1 = 0;
    k = this.mTouchMode;
    int i2;
    if (k == 3)
    {
      if (this.mScrollStrictSpan == null) {
        this.mScrollStrictSpan = StrictMode.enterCriticalSpan("AbsListView-scroll");
      }
      if (paramInt2 != this.mLastY)
      {
        if (((this.mGroupFlags & 0x80000) == 0) && (Math.abs(j) > this.mTouchSlop))
        {
          localObject = getParent();
          if (localObject != null) {
            ((ViewParent)localObject).requestDisallowInterceptTouchEvent(true);
          }
        }
        k = this.mMotionPosition;
        if (k >= 0) {
          k -= this.mFirstPosition;
        } else {
          k = getChildCount() / 2;
        }
        localObject = getChildAt(k);
        if (localObject != null) {
          i2 = ((View)localObject).getTop();
        } else {
          i2 = 0;
        }
        boolean bool;
        if (i != 0) {
          bool = trackMotionScroll(j, i);
        } else {
          bool = false;
        }
        localObject = getChildAt(k);
        if (localObject != null)
        {
          j = ((View)localObject).getTop();
          if (bool)
          {
            k = -i - (j - i2);
            if (dispatchNestedScroll(0, k - i, 0, k, this.mScrollOffset))
            {
              localObject = this.mScrollOffset;
              j = 0 - localObject[1];
              if (paramMotionEvent != null)
              {
                paramMotionEvent.offsetLocation(0.0F, localObject[1]);
                this.mNestedYOffset += this.mScrollOffset[1];
              }
            }
            else
            {
              bool = overScrollBy(0, k, 0, this.mScrollY, 0, 0, 0, this.mOverscrollDistance, true);
              if (bool)
              {
                paramMotionEvent = this.mVelocityTracker;
                if (paramMotionEvent != null) {
                  paramMotionEvent.clear();
                }
              }
              j = getOverScrollMode();
              if (j != 0) {
                if (j == 1)
                {
                  if (contentFits())
                  {
                    j = i1;
                    break label669;
                  }
                }
                else
                {
                  j = i1;
                  break label669;
                }
              }
              if (!bool)
              {
                this.mDirection = 0;
                this.mTouchMode = 5;
              }
              if (i > 0)
              {
                this.mEdgeGlowTop.onPull(-k / getHeight(), paramInt1 / getWidth());
                if (!this.mEdgeGlowBottom.isFinished()) {
                  this.mEdgeGlowBottom.onRelease();
                }
                invalidateTopGlow();
                j = i1;
              }
              else
              {
                j = i1;
                if (i < 0)
                {
                  this.mEdgeGlowBottom.onPull(k / getHeight(), 1.0F - paramInt1 / getWidth());
                  if (!this.mEdgeGlowTop.isFinished()) {
                    this.mEdgeGlowTop.onRelease();
                  }
                  invalidateBottomGlow();
                  j = i1;
                }
              }
            }
          }
          else
          {
            j = i1;
          }
          label669:
          this.mMotionY = (paramInt2 + j + m);
        }
        else
        {
          j = n;
        }
        this.mLastY = (paramInt2 + j + m);
      }
    }
    else if ((k == 5) && (paramInt2 != this.mLastY))
    {
      i1 = this.mScrollY;
      n = i1 - i;
      if (paramInt2 > this.mLastY) {
        k = 1;
      } else {
        k = -1;
      }
      if (this.mDirection == 0) {
        this.mDirection = k;
      }
      i2 = -i;
      if (((n < 0) && (i1 >= 0)) || ((n > 0) && (i1 <= 0)))
      {
        i1 = -i1;
        i2 = i1;
        i += i1;
      }
      else
      {
        i = 0;
      }
      if (i2 != 0)
      {
        overScrollBy(0, i2, 0, this.mScrollY, 0, 0, 0, this.mOverscrollDistance, true);
        i1 = getOverScrollMode();
        if (i1 != 0) {
          if (i1 == 1)
          {
            if (contentFits()) {
              break label975;
            }
          }
          else {
            break label975;
          }
        }
        if (j > 0)
        {
          this.mEdgeGlowTop.onPull(i2 / getHeight(), paramInt1 / getWidth());
          if (!this.mEdgeGlowBottom.isFinished()) {
            this.mEdgeGlowBottom.onRelease();
          }
          invalidateTopGlow();
        }
        else if (j < 0)
        {
          this.mEdgeGlowBottom.onPull(i2 / getHeight(), 1.0F - paramInt1 / getWidth());
          if (!this.mEdgeGlowTop.isFinished()) {
            this.mEdgeGlowTop.onRelease();
          }
          invalidateBottomGlow();
        }
      }
      label975:
      if (i != 0)
      {
        if (this.mScrollY != 0)
        {
          this.mScrollY = 0;
          invalidateParentIfNeeded();
        }
        trackMotionScroll(i, i);
        this.mTouchMode = 3;
        j = findClosestMotionRow(paramInt2);
        paramInt1 = 0;
        this.mMotionCorrection = 0;
        paramMotionEvent = getChildAt(j - this.mFirstPosition);
        if (paramMotionEvent != null) {
          paramInt1 = paramMotionEvent.getTop();
        }
        this.mMotionViewOriginalTop = paramInt1;
        this.mMotionY = (paramInt2 + m);
        this.mMotionPosition = j;
      }
      this.mLastY = (paramInt2 + 0 + m);
      this.mDirection = k;
    }
  }
  
  private void setFastScrollerAlwaysVisibleUiThread(boolean paramBoolean)
  {
    FastScroller localFastScroller = this.mFastScroll;
    if (localFastScroller != null) {
      localFastScroller.setAlwaysShow(paramBoolean);
    }
  }
  
  private void setFastScrollerEnabledUiThread(boolean paramBoolean)
  {
    FastScroller localFastScroller = this.mFastScroll;
    if (localFastScroller != null)
    {
      localFastScroller.setEnabled(paramBoolean);
    }
    else if (paramBoolean)
    {
      this.mFastScroll = new FastScroller(this, this.mFastScrollStyle);
      this.mFastScroll.setEnabled(true);
    }
    resolvePadding();
    localFastScroller = this.mFastScroll;
    if (localFastScroller != null) {
      localFastScroller.updateLayout();
    }
  }
  
  private void setItemViewLayoutParams(View paramView, int paramInt)
  {
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    LayoutParams localLayoutParams1;
    if (localLayoutParams == null) {
      localLayoutParams1 = (LayoutParams)generateDefaultLayoutParams();
    } else if (!checkLayoutParams(localLayoutParams)) {
      localLayoutParams1 = (LayoutParams)generateLayoutParams(localLayoutParams);
    } else {
      localLayoutParams1 = (LayoutParams)localLayoutParams;
    }
    if (this.mAdapterHasStableIds) {
      localLayoutParams1.itemId = this.mAdapter.getItemId(paramInt);
    }
    localLayoutParams1.viewType = this.mAdapter.getItemViewType(paramInt);
    localLayoutParams1.isEnabled = this.mAdapter.isEnabled(paramInt);
    if (localLayoutParams1 != localLayoutParams) {
      paramView.setLayoutParams(localLayoutParams1);
    }
  }
  
  private boolean shouldDisplayEdgeEffects()
  {
    boolean bool;
    if (getOverScrollMode() != 2) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean showContextMenuForChildInternal(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    int i = getPositionForView(paramView);
    if (i < 0) {
      return false;
    }
    long l = this.mAdapter.getItemId(i);
    boolean bool1 = false;
    if (this.mOnItemLongClickListener != null) {
      bool1 = this.mOnItemLongClickListener.onItemLongClick(this, paramView, i, l);
    }
    boolean bool2 = bool1;
    if (!bool1)
    {
      this.mContextMenuInfo = createContextMenuInfo(getChildAt(i - this.mFirstPosition), i, l);
      if (paramBoolean) {
        bool2 = super.showContextMenuForChild(paramView, paramFloat1, paramFloat2);
      } else {
        bool2 = super.showContextMenuForChild(paramView);
      }
    }
    return bool2;
  }
  
  private boolean showContextMenuInternal(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    int i = pointToPosition((int)paramFloat1, (int)paramFloat2);
    if (i != -1)
    {
      long l = this.mAdapter.getItemId(i);
      View localView = getChildAt(i - this.mFirstPosition);
      if (localView != null)
      {
        this.mContextMenuInfo = createContextMenuInfo(localView, i, l);
        if (paramBoolean) {
          return super.showContextMenuForChild(this, paramFloat1, paramFloat2);
        }
        return super.showContextMenuForChild(this);
      }
    }
    if (paramBoolean) {
      return super.showContextMenu(paramFloat1, paramFloat2);
    }
    return super.showContextMenu();
  }
  
  private void showPopup()
  {
    if (getWindowVisibility() == 0)
    {
      createTextFilter(true);
      positionPopup();
      checkFocus();
    }
  }
  
  private boolean startScrollIfNeeded(int paramInt1, int paramInt2, MotionEvent paramMotionEvent)
  {
    int i = paramInt2 - this.mMotionY;
    int j = Math.abs(i);
    int k;
    if (this.mScrollY != 0) {
      k = 1;
    } else {
      k = 0;
    }
    if (this.mIsFirstTouchMoveEvent)
    {
      if (j > this.mMoveAcceleration) {
        j = 1;
      } else {
        j = 0;
      }
    }
    else if (j > this.mTouchSlop) {
      j = 1;
    } else {
      j = 0;
    }
    if (((k != 0) || (j != 0)) && ((getNestedScrollAxes() & 0x2) == 0))
    {
      createScrollingCache();
      if (k != 0)
      {
        this.mTouchMode = 5;
        this.mMotionCorrection = 0;
      }
      else
      {
        this.mTouchMode = 3;
        if (this.mIsFirstTouchMoveEvent)
        {
          j = this.mMoveAcceleration;
          if (i <= 0) {
            j = -j;
          }
          this.mMotionCorrection = j;
        }
        else
        {
          j = this.mTouchSlop;
          if (i <= 0) {
            j = -j;
          }
          this.mMotionCorrection = j;
        }
      }
      removeCallbacks(this.mPendingCheckForLongPress);
      setPressed(false);
      Object localObject = getChildAt(this.mMotionPosition - this.mFirstPosition);
      if (localObject != null) {
        ((View)localObject).setPressed(false);
      }
      reportScrollStateChange(1);
      localObject = getParent();
      if (localObject != null) {
        ((ViewParent)localObject).requestDisallowInterceptTouchEvent(true);
      }
      scrollIfNeeded(paramInt1, paramInt2, paramMotionEvent);
      return true;
    }
    return false;
  }
  
  private void updateOnScreenCheckedViews()
  {
    int i = this.mFirstPosition;
    int j = getChildCount();
    int k;
    if (getContext().getApplicationInfo().targetSdkVersion >= 11) {
      k = 1;
    } else {
      k = 0;
    }
    for (int m = 0; m < j; m++)
    {
      View localView = getChildAt(m);
      int n = i + m;
      if ((localView instanceof Checkable)) {
        ((Checkable)localView).setChecked(this.mCheckStates.get(n));
      } else if (k != 0) {
        localView.setActivated(this.mCheckStates.get(n));
      }
    }
  }
  
  private void useDefaultSelector()
  {
    setSelector(getContext().getDrawable(17301602));
  }
  
  public void addTouchables(ArrayList<View> paramArrayList)
  {
    int i = getChildCount();
    int j = this.mFirstPosition;
    ListAdapter localListAdapter = this.mAdapter;
    if (localListAdapter == null) {
      return;
    }
    for (int k = 0; k < i; k++)
    {
      View localView = getChildAt(k);
      if (localListAdapter.isEnabled(j + k)) {
        paramArrayList.add(localView);
      }
      localView.addTouchables(paramArrayList);
    }
  }
  
  public void afterTextChanged(Editable paramEditable) {}
  
  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
  
  public boolean canScrollList(int paramInt)
  {
    int i = getChildCount();
    boolean bool1 = false;
    boolean bool2 = false;
    if (i == 0) {
      return false;
    }
    int j = this.mFirstPosition;
    Rect localRect = this.mListPadding;
    if (paramInt > 0)
    {
      paramInt = getChildAt(i - 1).getBottom();
      if ((j + i < this.mItemCount) || (paramInt > getHeight() - localRect.bottom)) {
        bool2 = true;
      }
      return bool2;
    }
    paramInt = getChildAt(0).getTop();
    if (j <= 0)
    {
      bool2 = bool1;
      if (paramInt >= localRect.top) {}
    }
    else
    {
      bool2 = true;
    }
    return bool2;
  }
  
  public boolean checkInputConnectionProxy(View paramView)
  {
    boolean bool;
    if (paramView == this.mTextFilter) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  public void clearChoices()
  {
    Object localObject = this.mCheckStates;
    if (localObject != null) {
      ((SparseBooleanArray)localObject).clear();
    }
    localObject = this.mCheckedIdStates;
    if (localObject != null) {
      ((LongSparseArray)localObject).clear();
    }
    this.mCheckedItemCount = 0;
  }
  
  public void clearTextFilter()
  {
    if (this.mFiltered)
    {
      getTextFilterInput().setText("");
      this.mFiltered = false;
      PopupWindow localPopupWindow = this.mPopup;
      if ((localPopupWindow != null) && (localPopupWindow.isShowing())) {
        dismissPopup();
      }
    }
  }
  
  protected int computeVerticalScrollExtent()
  {
    int i = getChildCount();
    if (i > 0)
    {
      if (this.mSmoothScrollbarEnabled)
      {
        int j = i * 100;
        View localView = getChildAt(0);
        int k = localView.getTop();
        int m = localView.getHeight();
        int n = j;
        if (m > 0) {
          n = j + k * 100 / m;
        }
        localView = getChildAt(i - 1);
        i = localView.getBottom();
        k = localView.getHeight();
        j = n;
        if (k > 0) {
          j = n - (i - getHeight()) * 100 / k;
        }
        return j;
      }
      return 1;
    }
    return 0;
  }
  
  protected int computeVerticalScrollOffset()
  {
    int i = this.mFirstPosition;
    int j = getChildCount();
    if ((i >= 0) && (j > 0))
    {
      int k;
      int m;
      if (this.mSmoothScrollbarEnabled)
      {
        View localView = getChildAt(0);
        k = localView.getTop();
        m = localView.getHeight();
        if (m > 0) {
          return Math.max(i * 100 - k * 100 / m + (int)(this.mScrollY / getHeight() * this.mItemCount * 100.0F), 0);
        }
      }
      else
      {
        m = this.mItemCount;
        if (i == 0) {
          k = 0;
        } else if (i + j == m) {
          k = m;
        } else {
          k = j / 2 + i;
        }
        return (int)(i + j * (k / m));
      }
    }
    return 0;
  }
  
  protected int computeVerticalScrollRange()
  {
    int j;
    if (this.mSmoothScrollbarEnabled)
    {
      int i = Math.max(this.mItemCount * 100, 0);
      j = i;
      if (this.mScrollY != 0) {
        j = i + Math.abs((int)(this.mScrollY / getHeight() * this.mItemCount * 100.0F));
      }
    }
    else
    {
      j = this.mItemCount;
    }
    return j;
  }
  
  void confirmCheckedPositionsById()
  {
    this.mCheckStates.clear();
    int i = 0;
    label149:
    Object localObject;
    for (int j = 0; j < this.mCheckedIdStates.size(); j++)
    {
      long l = this.mCheckedIdStates.keyAt(j);
      int k = ((Integer)this.mCheckedIdStates.valueAt(j)).intValue();
      if (l != this.mAdapter.getItemId(k))
      {
        int m = Math.max(0, k - 20);
        int n = Math.min(k + 20, this.mItemCount);
        while (m < n)
        {
          if (l == this.mAdapter.getItemId(m))
          {
            this.mCheckStates.put(m, true);
            this.mCheckedIdStates.setValueAt(j, Integer.valueOf(m));
            m = 1;
            break label149;
          }
          m++;
        }
        m = 0;
        if (m == 0)
        {
          this.mCheckedIdStates.delete(l);
          j--;
          this.mCheckedItemCount -= 1;
          i = 1;
          ActionMode localActionMode = this.mChoiceActionMode;
          if (localActionMode != null)
          {
            localObject = this.mMultiChoiceModeCallback;
            if (localObject != null)
            {
              ((MultiChoiceModeWrapper)localObject).onItemCheckedStateChanged(localActionMode, k, l, false);
              break label216;
            }
          }
        }
      }
      else
      {
        label216:
        this.mCheckStates.put(k, true);
      }
    }
    if (i != 0)
    {
      localObject = this.mChoiceActionMode;
      if (localObject != null) {
        ((ActionMode)localObject).invalidate();
      }
    }
  }
  
  ContextMenu.ContextMenuInfo createContextMenuInfo(View paramView, int paramInt, long paramLong)
  {
    return new AdapterView.AdapterContextMenuInfo(paramView, paramInt, paramLong);
  }
  
  AbsPositionScroller createPositionScroller()
  {
    return new PositionScroller();
  }
  
  public void deferNotifyDataSetChanged()
  {
    this.mDeferNotifyDataSetChanged = true;
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    int i = 0;
    int j;
    if ((this.mGroupFlags & 0x22) == 34) {
      j = 1;
    } else {
      j = 0;
    }
    if (j != 0)
    {
      i = paramCanvas.save();
      int k = this.mScrollX;
      int m = this.mScrollY;
      paramCanvas.clipRect(this.mPaddingLeft + k, this.mPaddingTop + m, this.mRight + k - this.mLeft - this.mPaddingRight, this.mBottom + m - this.mTop - this.mPaddingBottom);
      this.mGroupFlags &= 0xFFFFFFDD;
    }
    boolean bool = this.mDrawSelectorOnTop;
    if (!bool) {
      drawSelector(paramCanvas);
    }
    super.dispatchDraw(paramCanvas);
    if (bool) {
      drawSelector(paramCanvas);
    }
    if (j != 0)
    {
      paramCanvas.restoreToCount(i);
      this.mGroupFlags = (0x22 | this.mGroupFlags);
    }
  }
  
  public void dispatchDrawableHotspotChanged(float paramFloat1, float paramFloat2) {}
  
  protected void dispatchSetPressed(boolean paramBoolean) {}
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    if (shouldDisplayEdgeEffects())
    {
      int i = this.mScrollY;
      boolean bool1 = getClipToPadding();
      int j;
      int k;
      int m;
      int n;
      if (bool1)
      {
        j = getWidth() - this.mPaddingLeft - this.mPaddingRight;
        k = getHeight() - this.mPaddingTop - this.mPaddingBottom;
        m = this.mPaddingLeft;
        n = this.mPaddingTop;
      }
      else
      {
        j = getWidth();
        k = getHeight();
        m = 0;
        n = 0;
      }
      this.mEdgeGlowTop.setSize(j, k);
      this.mEdgeGlowBottom.setSize(j, k);
      boolean bool2 = this.mEdgeGlowTop.isFinished();
      int i1 = 0;
      int i3;
      if (!bool2)
      {
        int i2 = paramCanvas.save();
        paramCanvas.clipRect(m, n, m + j, this.mEdgeGlowTop.getMaxHeight() + n);
        i3 = Math.min(0, this.mFirstPositionDistanceGuess + i);
        paramCanvas.translate(m, i3 + n);
        if (this.mEdgeGlowTop.draw(paramCanvas)) {
          invalidateTopGlow();
        }
        paramCanvas.restoreToCount(i2);
      }
      if (!this.mEdgeGlowBottom.isFinished())
      {
        i3 = paramCanvas.save();
        paramCanvas.clipRect(m, n + k - this.mEdgeGlowBottom.getMaxHeight(), m + j, n + k);
        k = -j;
        i = Math.max(getHeight(), this.mLastPositionDistanceGuess + i);
        n = i1;
        if (bool1) {
          n = this.mPaddingBottom;
        }
        paramCanvas.translate(k + m, i - n);
        paramCanvas.rotate(180.0F, j, 0.0F);
        if (this.mEdgeGlowBottom.draw(paramCanvas)) {
          invalidateBottomGlow();
        }
        paramCanvas.restoreToCount(i3);
      }
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    updateSelectorState();
  }
  
  protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    super.encodeProperties(paramViewHierarchyEncoder);
    paramViewHierarchyEncoder.addProperty("drawing:cacheColorHint", getCacheColorHint());
    paramViewHierarchyEncoder.addProperty("list:fastScrollEnabled", isFastScrollEnabled());
    paramViewHierarchyEncoder.addProperty("list:scrollingCacheEnabled", isScrollingCacheEnabled());
    paramViewHierarchyEncoder.addProperty("list:smoothScrollbarEnabled", isSmoothScrollbarEnabled());
    paramViewHierarchyEncoder.addProperty("list:stackFromBottom", isStackFromBottom());
    paramViewHierarchyEncoder.addProperty("list:textFilterEnabled", isTextFilterEnabled());
    View localView = getSelectedView();
    if (localView != null)
    {
      paramViewHierarchyEncoder.addPropertyKey("selectedView");
      localView.encode(paramViewHierarchyEncoder);
    }
  }
  
  abstract void fillGap(boolean paramBoolean);
  
  int findClosestMotionRow(int paramInt)
  {
    int i = getChildCount();
    if (i == 0) {
      return -1;
    }
    paramInt = findMotionRow(paramInt);
    if (paramInt == -1) {
      paramInt = this.mFirstPosition + i - 1;
    }
    return paramInt;
  }
  
  @UnsupportedAppUsage
  abstract int findMotionRow(int paramInt);
  
  public void fling(int paramInt)
  {
    if (this.mFlingRunnable == null) {
      this.mFlingRunnable = new FlingRunnable();
    }
    reportScrollStateChange(2);
    this.mFlingRunnable.start(paramInt);
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-1, -2, 0);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return new LayoutParams(paramLayoutParams);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return AbsListView.class.getName();
  }
  
  View getAccessibilityFocusedChild(View paramView)
  {
    ViewParent localViewParent = paramView.getParent();
    View localView = paramView;
    for (paramView = localViewParent; ((paramView instanceof View)) && (paramView != this); paramView = paramView.getParent()) {
      localView = (View)paramView;
    }
    if (!(paramView instanceof View)) {
      return null;
    }
    return localView;
  }
  
  public int getBottomEdgeEffectColor()
  {
    return this.mEdgeGlowBottom.getColor();
  }
  
  protected float getBottomFadingEdgeStrength()
  {
    int i = getChildCount();
    float f1 = super.getBottomFadingEdgeStrength();
    if (i == 0) {
      return f1;
    }
    if (this.mFirstPosition + i - 1 < this.mItemCount - 1) {
      return 1.0F;
    }
    i = getChildAt(i - 1).getBottom();
    int j = getHeight();
    float f2 = getVerticalFadingEdgeLength();
    if (i > j - this.mPaddingBottom) {
      f1 = (i - j + this.mPaddingBottom) / f2;
    }
    return f1;
  }
  
  protected int getBottomPaddingOffset()
  {
    int i;
    if ((this.mGroupFlags & 0x22) == 34) {
      i = 0;
    } else {
      i = this.mPaddingBottom;
    }
    return i;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public int getCacheColorHint()
  {
    return this.mCacheColorHint;
  }
  
  public int getCheckedItemCount()
  {
    return this.mCheckedItemCount;
  }
  
  public long[] getCheckedItemIds()
  {
    if ((this.mChoiceMode != 0) && (this.mCheckedIdStates != null) && (this.mAdapter != null))
    {
      LongSparseArray localLongSparseArray = this.mCheckedIdStates;
      int i = localLongSparseArray.size();
      long[] arrayOfLong = new long[i];
      for (int j = 0; j < i; j++) {
        arrayOfLong[j] = localLongSparseArray.keyAt(j);
      }
      return arrayOfLong;
    }
    return new long[0];
  }
  
  public int getCheckedItemPosition()
  {
    if (this.mChoiceMode == 1)
    {
      SparseBooleanArray localSparseBooleanArray = this.mCheckStates;
      if ((localSparseBooleanArray != null) && (localSparseBooleanArray.size() == 1)) {
        return this.mCheckStates.keyAt(0);
      }
    }
    return -1;
  }
  
  public SparseBooleanArray getCheckedItemPositions()
  {
    if (this.mChoiceMode != 0) {
      return this.mCheckStates;
    }
    return null;
  }
  
  public int getChoiceMode()
  {
    return this.mChoiceMode;
  }
  
  protected ContextMenu.ContextMenuInfo getContextMenuInfo()
  {
    return this.mContextMenuInfo;
  }
  
  public void getFocusedRect(Rect paramRect)
  {
    View localView = getSelectedView();
    if ((localView != null) && (localView.getParent() == this))
    {
      localView.getFocusedRect(paramRect);
      offsetDescendantRectToMyCoords(localView, paramRect);
    }
    else
    {
      super.getFocusedRect(paramRect);
    }
  }
  
  int getFooterViewsCount()
  {
    return 0;
  }
  
  int getHeaderViewsCount()
  {
    return 0;
  }
  
  int getHeightForPosition(int paramInt)
  {
    int i = getFirstVisiblePosition();
    int j = getChildCount();
    i = paramInt - i;
    if ((i >= 0) && (i < j)) {
      return getChildAt(i).getHeight();
    }
    View localView = obtainView(paramInt, this.mIsScrap);
    localView.measure(this.mWidthMeasureSpec, 0);
    j = localView.getMeasuredHeight();
    this.mRecycler.addScrapView(localView, paramInt);
    return j;
  }
  
  protected int getLeftPaddingOffset()
  {
    int i;
    if ((this.mGroupFlags & 0x22) == 34) {
      i = 0;
    } else {
      i = -this.mPaddingLeft;
    }
    return i;
  }
  
  public int getListPaddingBottom()
  {
    return this.mListPadding.bottom;
  }
  
  public int getListPaddingLeft()
  {
    return this.mListPadding.left;
  }
  
  public int getListPaddingRight()
  {
    return this.mListPadding.right;
  }
  
  public int getListPaddingTop()
  {
    return this.mListPadding.top;
  }
  
  protected int getRightPaddingOffset()
  {
    int i;
    if ((this.mGroupFlags & 0x22) == 34) {
      i = 0;
    } else {
      i = this.mPaddingRight;
    }
    return i;
  }
  
  @ViewDebug.ExportedProperty
  public View getSelectedView()
  {
    if ((this.mItemCount > 0) && (this.mSelectedPosition >= 0)) {
      return getChildAt(this.mSelectedPosition - this.mFirstPosition);
    }
    return null;
  }
  
  int getSelectionModeForAccessibility()
  {
    int i = getChoiceMode();
    if (i != 0)
    {
      if (i != 1)
      {
        if ((i != 2) && (i != 3)) {
          return 0;
        }
        return 2;
      }
      return 1;
    }
    return 0;
  }
  
  public Drawable getSelector()
  {
    return this.mSelector;
  }
  
  public int getSolidColor()
  {
    return this.mCacheColorHint;
  }
  
  public CharSequence getTextFilter()
  {
    if (this.mTextFilterEnabled)
    {
      EditText localEditText = this.mTextFilter;
      if (localEditText != null) {
        return localEditText.getText();
      }
    }
    return null;
  }
  
  public int getTopEdgeEffectColor()
  {
    return this.mEdgeGlowTop.getColor();
  }
  
  protected float getTopFadingEdgeStrength()
  {
    int i = getChildCount();
    float f1 = super.getTopFadingEdgeStrength();
    if (i == 0) {
      return f1;
    }
    if (this.mFirstPosition > 0) {
      return 1.0F;
    }
    i = getChildAt(0).getTop();
    float f2 = getVerticalFadingEdgeLength();
    if (i < this.mPaddingTop) {
      f1 = -(i - this.mPaddingTop) / f2;
    }
    return f1;
  }
  
  protected int getTopPaddingOffset()
  {
    int i;
    if ((this.mGroupFlags & 0x22) == 34) {
      i = 0;
    } else {
      i = -this.mPaddingTop;
    }
    return i;
  }
  
  public int getTranscriptMode()
  {
    return this.mTranscriptMode;
  }
  
  public int getVerticalScrollbarWidth()
  {
    FastScroller localFastScroller = this.mFastScroll;
    if ((localFastScroller != null) && (localFastScroller.isEnabled())) {
      return Math.max(super.getVerticalScrollbarWidth(), this.mFastScroll.getWidth());
    }
    return super.getVerticalScrollbarWidth();
  }
  
  void handleBoundsChange()
  {
    if (this.mInLayout) {
      return;
    }
    int i = getChildCount();
    if (i > 0)
    {
      this.mDataChanged = true;
      rememberSyncState();
      for (int j = 0; j < i; j++)
      {
        View localView = getChildAt(j);
        ViewGroup.LayoutParams localLayoutParams = localView.getLayoutParams();
        if ((localLayoutParams == null) || (localLayoutParams.width < 1) || (localLayoutParams.height < 1)) {
          localView.forceLayout();
        }
      }
    }
  }
  
  protected void handleDataChanged()
  {
    int i = this.mItemCount;
    int j = this.mLastHandledItemCount;
    this.mLastHandledItemCount = this.mItemCount;
    Object localObject;
    if (this.mChoiceMode != 0)
    {
      localObject = this.mAdapter;
      if ((localObject != null) && (((ListAdapter)localObject).hasStableIds())) {
        confirmCheckedPositionsById();
      }
    }
    this.mRecycler.clearTransientStateViews();
    int k = 3;
    int m;
    if (i > 0)
    {
      int i1;
      if (this.mNeedSync)
      {
        this.mNeedSync = false;
        this.mPendingSync = null;
        m = this.mTranscriptMode;
        if (m == 2)
        {
          this.mLayoutMode = 3;
          return;
        }
        if (m == 1)
        {
          if (this.mForceTranscriptScroll)
          {
            this.mForceTranscriptScroll = false;
            this.mLayoutMode = 3;
            return;
          }
          int n = getChildCount();
          i1 = getHeight() - getPaddingBottom();
          localObject = getChildAt(n - 1);
          if (localObject != null) {
            m = ((View)localObject).getBottom();
          } else {
            m = i1;
          }
          if ((this.mFirstPosition + n >= j) && (m <= i1))
          {
            this.mLayoutMode = 3;
            return;
          }
          awakenScrollBars();
        }
        m = this.mSyncMode;
        if (m != 0)
        {
          if (m == 1)
          {
            this.mLayoutMode = 5;
            this.mSyncPosition = Math.min(Math.max(0, this.mSyncPosition), i - 1);
          }
        }
        else
        {
          if (isInTouchMode())
          {
            this.mLayoutMode = 5;
            this.mSyncPosition = Math.min(Math.max(0, this.mSyncPosition), i - 1);
            return;
          }
          m = findSyncPosition();
          if ((m >= 0) && (lookForSelectablePosition(m, true) == m))
          {
            this.mSyncPosition = m;
            if (this.mSyncHeight == getHeight()) {
              this.mLayoutMode = 5;
            } else {
              this.mLayoutMode = 2;
            }
            setNextSelectedPositionInt(m);
            return;
          }
        }
      }
      if (!isInTouchMode())
      {
        i1 = getSelectedItemPosition();
        m = i1;
        if (i1 >= i) {
          m = i - 1;
        }
        i1 = m;
        if (m < 0) {
          i1 = 0;
        }
        m = lookForSelectablePosition(i1, true);
        if (m >= 0)
        {
          setNextSelectedPositionInt(m);
          return;
        }
        m = lookForSelectablePosition(i1, false);
        if (m >= 0) {
          setNextSelectedPositionInt(m);
        }
      }
      else if (this.mResurrectToPosition >= 0)
      {
        return;
      }
    }
    if (this.mStackFromBottom) {
      m = k;
    } else {
      m = 1;
    }
    this.mLayoutMode = m;
    this.mSelectedPosition = -1;
    this.mSelectedRowId = Long.MIN_VALUE;
    this.mNextSelectedPosition = -1;
    this.mNextSelectedRowId = Long.MIN_VALUE;
    this.mNeedSync = false;
    this.mPendingSync = null;
    this.mSelectorPosition = -1;
    checkSelectionChanged();
  }
  
  protected boolean handleScrollBarDragging(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public boolean hasTextFilter()
  {
    return this.mFiltered;
  }
  
  void hideSelector()
  {
    if (this.mSelectedPosition != -1)
    {
      if (this.mLayoutMode != 4) {
        this.mResurrectToPosition = this.mSelectedPosition;
      }
      if ((this.mNextSelectedPosition >= 0) && (this.mNextSelectedPosition != this.mSelectedPosition)) {
        this.mResurrectToPosition = this.mNextSelectedPosition;
      }
      setSelectedPositionInt(-1);
      setNextSelectedPositionInt(-1);
      this.mSelectedTop = 0;
    }
  }
  
  protected void internalSetPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.internalSetPadding(paramInt1, paramInt2, paramInt3, paramInt4);
    if (isLayoutRequested()) {
      handleBoundsChange();
    }
  }
  
  public void invalidateViews()
  {
    this.mDataChanged = true;
    rememberSyncState();
    requestLayout();
    invalidate();
  }
  
  @UnsupportedAppUsage
  void invokeOnItemScrollListener()
  {
    Object localObject = this.mFastScroll;
    if (localObject != null) {
      ((FastScroller)localObject).onScroll(this.mFirstPosition, getChildCount(), this.mItemCount);
    }
    localObject = this.mOnScrollListener;
    if (localObject != null) {
      ((OnScrollListener)localObject).onScroll(this, this.mFirstPosition, getChildCount(), this.mItemCount);
    }
    onScrollChanged(0, 0, 0, 0);
  }
  
  public boolean isDrawSelectorOnTop()
  {
    return this.mDrawSelectorOnTop;
  }
  
  public boolean isFastScrollAlwaysVisible()
  {
    FastScroller localFastScroller = this.mFastScroll;
    boolean bool1 = true;
    boolean bool2 = true;
    if (localFastScroller == null)
    {
      if ((!this.mFastScrollEnabled) || (!this.mFastScrollAlwaysVisible)) {
        bool2 = false;
      }
      return bool2;
    }
    if ((localFastScroller.isEnabled()) && (this.mFastScroll.isAlwaysShowEnabled())) {
      bool2 = bool1;
    } else {
      bool2 = false;
    }
    return bool2;
  }
  
  @ViewDebug.ExportedProperty
  public boolean isFastScrollEnabled()
  {
    FastScroller localFastScroller = this.mFastScroll;
    if (localFastScroller == null) {
      return this.mFastScrollEnabled;
    }
    return localFastScroller.isEnabled();
  }
  
  protected boolean isInFilterMode()
  {
    return this.mFiltered;
  }
  
  public boolean isItemChecked(int paramInt)
  {
    if (this.mChoiceMode != 0)
    {
      SparseBooleanArray localSparseBooleanArray = this.mCheckStates;
      if (localSparseBooleanArray != null) {
        return localSparseBooleanArray.get(paramInt);
      }
    }
    return false;
  }
  
  protected boolean isPaddingOffsetRequired()
  {
    boolean bool;
    if ((this.mGroupFlags & 0x22) != 34) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty
  public boolean isScrollingCacheEnabled()
  {
    return this.mScrollingCacheEnabled;
  }
  
  @ViewDebug.ExportedProperty
  public boolean isSmoothScrollbarEnabled()
  {
    return this.mSmoothScrollbarEnabled;
  }
  
  @ViewDebug.ExportedProperty
  public boolean isStackFromBottom()
  {
    return this.mStackFromBottom;
  }
  
  @ViewDebug.ExportedProperty
  public boolean isTextFilterEnabled()
  {
    return this.mTextFilterEnabled;
  }
  
  @UnsupportedAppUsage
  protected boolean isVerticalScrollBarHidden()
  {
    return isFastScrollEnabled();
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    Drawable localDrawable = this.mSelector;
    if (localDrawable != null) {
      localDrawable.jumpToCurrentState();
    }
  }
  
  void keyPressed()
  {
    if ((isEnabled()) && (isClickable()))
    {
      Drawable localDrawable = this.mSelector;
      Object localObject = this.mSelectorRect;
      if ((localDrawable != null) && ((isFocused()) || (touchModeDrawsInPressedState())) && (!((Rect)localObject).isEmpty()))
      {
        localObject = getChildAt(this.mSelectedPosition - this.mFirstPosition);
        if (localObject != null)
        {
          if (((View)localObject).hasExplicitFocusable()) {
            return;
          }
          ((View)localObject).setPressed(true);
        }
        setPressed(true);
        boolean bool = isLongClickable();
        localDrawable = localDrawable.getCurrent();
        if ((localDrawable != null) && ((localDrawable instanceof TransitionDrawable))) {
          if (bool) {
            ((TransitionDrawable)localDrawable).startTransition(ViewConfiguration.getLongPressTimeout());
          } else {
            ((TransitionDrawable)localDrawable).resetTransition();
          }
        }
        if ((bool) && (!this.mDataChanged))
        {
          if (this.mPendingCheckForKeyLongPress == null) {
            this.mPendingCheckForKeyLongPress = new CheckForKeyLongPress(null);
          }
          this.mPendingCheckForKeyLongPress.rememberWindowAttachCount();
          postDelayed(this.mPendingCheckForKeyLongPress, ViewConfiguration.getLongPressTimeout());
        }
      }
      return;
    }
  }
  
  protected void layoutChildren() {}
  
  View obtainView(int paramInt, boolean[] paramArrayOfBoolean)
  {
    Trace.traceBegin(8L, "obtainView");
    paramArrayOfBoolean[0] = false;
    View localView1 = this.mRecycler.getTransientStateView(paramInt);
    if (localView1 != null)
    {
      if (((LayoutParams)localView1.getLayoutParams()).viewType == this.mAdapter.getItemViewType(paramInt))
      {
        localView2 = this.mAdapter.getView(paramInt, localView1, this);
        if (localView2 != localView1)
        {
          setItemViewLayoutParams(localView2, paramInt);
          this.mRecycler.addScrapView(localView2, paramInt);
        }
      }
      paramArrayOfBoolean[0] = true;
      localView1.dispatchFinishTemporaryDetach();
      return localView1;
    }
    localView1 = this.mRecycler.getScrapView(paramInt);
    View localView2 = this.mAdapter.getView(paramInt, localView1, this);
    if (localView1 != null) {
      if (localView2 != localView1)
      {
        this.mRecycler.addScrapView(localView1, paramInt);
      }
      else if (localView2.isTemporarilyDetached())
      {
        paramArrayOfBoolean[0] = true;
        localView2.dispatchFinishTemporaryDetach();
      }
    }
    int i = this.mCacheColorHint;
    if (i != 0) {
      localView2.setDrawingCacheBackgroundColor(i);
    }
    if (localView2.getImportantForAccessibility() == 0) {
      localView2.setImportantForAccessibility(1);
    }
    setItemViewLayoutParams(localView2, paramInt);
    if (AccessibilityManager.getInstance(this.mContext).isEnabled())
    {
      if (this.mAccessibilityDelegate == null) {
        this.mAccessibilityDelegate = new ListItemAccessibilityDelegate();
      }
      if (localView2.getAccessibilityDelegate() == null) {
        localView2.setAccessibilityDelegate(this.mAccessibilityDelegate);
      }
    }
    Trace.traceEnd(8L);
    return localView2;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    ViewTreeObserver localViewTreeObserver = getViewTreeObserver();
    localViewTreeObserver.addOnTouchModeChangeListener(this);
    if ((this.mTextFilterEnabled) && (this.mPopup != null) && (!this.mGlobalLayoutListenerAddedFilter)) {
      localViewTreeObserver.addOnGlobalLayoutListener(this);
    }
    if ((this.mAdapter != null) && (this.mDataSetObserver == null))
    {
      this.mDataSetObserver = new AdapterDataSetObserver();
      this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
      this.mDataChanged = true;
      this.mOldItemCount = this.mItemCount;
      this.mItemCount = this.mAdapter.getCount();
    }
  }
  
  public void onCancelPendingInputEvents()
  {
    super.onCancelPendingInputEvents();
    Object localObject = this.mPerformClick;
    if (localObject != null) {
      removeCallbacks((Runnable)localObject);
    }
    localObject = this.mPendingCheckForTap;
    if (localObject != null) {
      removeCallbacks((Runnable)localObject);
    }
    localObject = this.mPendingCheckForLongPress;
    if (localObject != null) {
      removeCallbacks((Runnable)localObject);
    }
    localObject = this.mPendingCheckForKeyLongPress;
    if (localObject != null) {
      removeCallbacks((Runnable)localObject);
    }
  }
  
  public InputConnection onCreateInputConnection(EditorInfo paramEditorInfo)
  {
    if (isTextFilterEnabled())
    {
      if (this.mPublicInputConnection == null)
      {
        this.mDefInputConnection = new BaseInputConnection(this, false);
        this.mPublicInputConnection = new InputConnectionWrapper(paramEditorInfo);
      }
      paramEditorInfo.inputType = 177;
      paramEditorInfo.imeOptions = 6;
      return this.mPublicInputConnection;
    }
    return null;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mIsDetaching = true;
    dismissPopup();
    this.mRecycler.clear();
    Object localObject = getViewTreeObserver();
    ((ViewTreeObserver)localObject).removeOnTouchModeChangeListener(this);
    if ((this.mTextFilterEnabled) && (this.mPopup != null))
    {
      ((ViewTreeObserver)localObject).removeOnGlobalLayoutListener(this);
      this.mGlobalLayoutListenerAddedFilter = false;
    }
    localObject = this.mAdapter;
    if (localObject != null)
    {
      AdapterDataSetObserver localAdapterDataSetObserver = this.mDataSetObserver;
      if (localAdapterDataSetObserver != null)
      {
        ((ListAdapter)localObject).unregisterDataSetObserver(localAdapterDataSetObserver);
        this.mDataSetObserver = null;
      }
    }
    localObject = this.mScrollStrictSpan;
    if (localObject != null)
    {
      ((StrictMode.Span)localObject).finish();
      this.mScrollStrictSpan = null;
    }
    localObject = this.mFlingStrictSpan;
    if (localObject != null)
    {
      ((StrictMode.Span)localObject).finish();
      this.mFlingStrictSpan = null;
    }
    localObject = this.mFlingRunnable;
    if (localObject != null) {
      removeCallbacks((Runnable)localObject);
    }
    localObject = this.mPositionScroller;
    if (localObject != null) {
      ((AbsPositionScroller)localObject).stop();
    }
    localObject = this.mClearScrollingCache;
    if (localObject != null) {
      removeCallbacks((Runnable)localObject);
    }
    localObject = this.mPerformClick;
    if (localObject != null) {
      removeCallbacks((Runnable)localObject);
    }
    localObject = this.mTouchModeReset;
    if (localObject != null)
    {
      removeCallbacks((Runnable)localObject);
      this.mTouchModeReset.run();
    }
    this.mIsDetaching = false;
  }
  
  protected void onDisplayHint(int paramInt)
  {
    super.onDisplayHint(paramInt);
    PopupWindow localPopupWindow;
    if (paramInt != 0)
    {
      if (paramInt == 4)
      {
        localPopupWindow = this.mPopup;
        if ((localPopupWindow != null) && (localPopupWindow.isShowing())) {
          dismissPopup();
        }
      }
    }
    else if (this.mFiltered)
    {
      localPopupWindow = this.mPopup;
      if ((localPopupWindow != null) && (!localPopupWindow.isShowing())) {
        showPopup();
      }
    }
    boolean bool;
    if (paramInt == 4) {
      bool = true;
    } else {
      bool = false;
    }
    this.mPopupHidden = bool;
  }
  
  public void onFilterComplete(int paramInt)
  {
    if ((this.mSelectedPosition < 0) && (paramInt > 0))
    {
      this.mResurrectToPosition = -1;
      resurrectSelection();
    }
  }
  
  protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
  {
    super.onFocusChanged(paramBoolean, paramInt, paramRect);
    if ((paramBoolean) && (this.mSelectedPosition < 0) && (!isInTouchMode()))
    {
      if ((!isAttachedToWindow()) && (this.mAdapter != null))
      {
        this.mDataChanged = true;
        this.mOldItemCount = this.mItemCount;
        this.mItemCount = this.mAdapter.getCount();
      }
      resurrectSelection();
    }
  }
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    if (i != 8)
    {
      if ((i == 11) && (paramMotionEvent.isFromSource(2)))
      {
        i = paramMotionEvent.getActionButton();
        if ((i == 32) || (i == 2))
        {
          i = this.mTouchMode;
          if (((i == 0) || (i == 1)) && (performStylusButtonPressAction(paramMotionEvent)))
          {
            removeCallbacks(this.mPendingCheckForLongPress);
            removeCallbacks(this.mPendingCheckForTap);
          }
        }
      }
    }
    else
    {
      float f;
      if (paramMotionEvent.isFromSource(2)) {
        f = paramMotionEvent.getAxisValue(9);
      } else if (paramMotionEvent.isFromSource(4194304)) {
        f = paramMotionEvent.getAxisValue(26);
      } else {
        f = 0.0F;
      }
      i = Math.round(this.mVerticalScrollFactor * f);
      if ((i != 0) && (!trackMotionScroll(i, i))) {
        return true;
      }
    }
    return super.onGenericMotionEvent(paramMotionEvent);
  }
  
  public void onGlobalLayout()
  {
    PopupWindow localPopupWindow;
    if (isShown())
    {
      if (this.mFiltered)
      {
        localPopupWindow = this.mPopup;
        if ((localPopupWindow != null) && (!localPopupWindow.isShowing()) && (!this.mPopupHidden)) {
          showPopup();
        }
      }
    }
    else
    {
      localPopupWindow = this.mPopup;
      if ((localPopupWindow != null) && (localPopupWindow.isShowing())) {
        dismissPopup();
      }
    }
  }
  
  public void onInitializeAccessibilityNodeInfoForItem(View paramView, int paramInt, AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    if (paramInt == -1) {
      return;
    }
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    boolean bool;
    if ((localLayoutParams instanceof LayoutParams))
    {
      if ((((LayoutParams)localLayoutParams).isEnabled) && (isEnabled())) {
        bool = true;
      } else {
        bool = false;
      }
    }
    else {
      bool = false;
    }
    paramAccessibilityNodeInfo.setEnabled(bool);
    if (paramInt == getSelectedItemPosition())
    {
      paramAccessibilityNodeInfo.setSelected(true);
      addAccessibilityActionIfEnabled(paramAccessibilityNodeInfo, bool, AccessibilityNodeInfo.AccessibilityAction.ACTION_CLEAR_SELECTION);
    }
    else
    {
      addAccessibilityActionIfEnabled(paramAccessibilityNodeInfo, bool, AccessibilityNodeInfo.AccessibilityAction.ACTION_SELECT);
    }
    if (isItemClickable(paramView))
    {
      addAccessibilityActionIfEnabled(paramAccessibilityNodeInfo, bool, AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
      paramAccessibilityNodeInfo.setClickable(true);
    }
    if (isLongClickable())
    {
      addAccessibilityActionIfEnabled(paramAccessibilityNodeInfo, bool, AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK);
      paramAccessibilityNodeInfo.setLongClickable(true);
    }
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    if (isEnabled())
    {
      if (canScrollUp())
      {
        paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
        paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP);
        paramAccessibilityNodeInfo.setScrollable(true);
      }
      if (canScrollDown())
      {
        paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
        paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN);
        paramAccessibilityNodeInfo.setScrollable(true);
      }
    }
    paramAccessibilityNodeInfo.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
    paramAccessibilityNodeInfo.setClickable(false);
  }
  
  public boolean onInterceptHoverEvent(MotionEvent paramMotionEvent)
  {
    FastScroller localFastScroller = this.mFastScroll;
    if ((localFastScroller != null) && (localFastScroller.onInterceptHoverEvent(paramMotionEvent))) {
      return true;
    }
    return super.onInterceptHoverEvent(paramMotionEvent);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionMasked();
    Object localObject = this.mPositionScroller;
    if (localObject != null) {
      ((AbsPositionScroller)localObject).stop();
    }
    if ((!this.mIsDetaching) && (isAttachedToWindow()))
    {
      localObject = this.mFastScroll;
      if ((localObject != null) && (((FastScroller)localObject).onInterceptTouchEvent(paramMotionEvent))) {
        return true;
      }
      int j;
      if (i != 0)
      {
        if (i != 1) {
          if (i != 2)
          {
            if (i != 3)
            {
              if (i != 6) {
                break label387;
              }
              this.mNumTouchMoveEvent = 0;
              onSecondaryPointerUp(paramMotionEvent);
              break label387;
            }
          }
          else
          {
            this.mNumTouchMoveEvent += 1;
            if (this.mNumTouchMoveEvent == 1) {
              this.mIsFirstTouchMoveEvent = true;
            } else {
              this.mIsFirstTouchMoveEvent = false;
            }
            if (this.mTouchMode == 0)
            {
              j = paramMotionEvent.findPointerIndex(this.mActivePointerId);
              i = j;
              if (j == -1)
              {
                i = 0;
                this.mActivePointerId = paramMotionEvent.getPointerId(0);
              }
              j = (int)paramMotionEvent.getY(i);
              initVelocityTrackerIfNotExists();
              this.mVelocityTracker.addMovement(paramMotionEvent);
              if (startScrollIfNeeded((int)paramMotionEvent.getX(i), j, null)) {
                return true;
              }
            }
            break label387;
          }
        }
        this.mNumTouchMoveEvent = 0;
        this.mTouchMode = -1;
        this.mActivePointerId = -1;
        recycleVelocityTracker();
        reportScrollStateChange(0);
        stopNestedScroll();
      }
      else
      {
        this.mNumTouchMoveEvent = 0;
        int k = this.mTouchMode;
        if ((k == 6) || (k == 5)) {
          break label389;
        }
        j = (int)paramMotionEvent.getX();
        i = (int)paramMotionEvent.getY();
        this.mActivePointerId = paramMotionEvent.getPointerId(0);
        int m = findMotionRow(i);
        if ((k != 4) && (m >= 0))
        {
          this.mMotionViewOriginalTop = getChildAt(m - this.mFirstPosition).getTop();
          this.mMotionX = j;
          this.mMotionY = i;
          this.mMotionPosition = m;
          this.mTouchMode = 0;
          clearScrollingCache();
        }
        this.mLastY = Integer.MIN_VALUE;
        initOrResetVelocityTracker();
        this.mVelocityTracker.addMovement(paramMotionEvent);
        this.mNestedYOffset = 0;
        startNestedScroll(2);
        if (k == 4) {
          return true;
        }
      }
      label387:
      return false;
      label389:
      this.mMotionCorrection = 0;
      return true;
    }
    return false;
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if (KeyEvent.isConfirmKey(paramInt))
    {
      if (!isEnabled()) {
        return true;
      }
      if ((isClickable()) && (isPressed()) && (this.mSelectedPosition >= 0) && (this.mAdapter != null) && (this.mSelectedPosition < this.mAdapter.getCount()))
      {
        paramKeyEvent = getChildAt(this.mSelectedPosition - this.mFirstPosition);
        if (paramKeyEvent != null)
        {
          performItemClick(paramKeyEvent, this.mSelectedPosition, this.mSelectedRowId);
          paramKeyEvent.setPressed(false);
        }
        setPressed(false);
        return true;
      }
    }
    return super.onKeyUp(paramInt, paramKeyEvent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.mInLayout = true;
    int i = getChildCount();
    if (paramBoolean)
    {
      for (int j = 0; j < i; j++) {
        getChildAt(j).forceLayout();
      }
      this.mRecycler.markChildrenDirty();
    }
    layoutChildren();
    this.mOverscrollMax = ((paramInt4 - paramInt2) / 3);
    FastScroller localFastScroller = this.mFastScroll;
    if (localFastScroller != null) {
      localFastScroller.onItemCountChanged(getChildCount(), this.mItemCount);
    }
    this.mInLayout = false;
    AbsListViewInjector.onLayout(this, paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.mSelector == null) {
      useDefaultSelector();
    }
    Object localObject = this.mListPadding;
    ((Rect)localObject).left = (this.mSelectionLeftPadding + this.mPaddingLeft);
    ((Rect)localObject).top = (this.mSelectionTopPadding + this.mPaddingTop);
    ((Rect)localObject).right = (this.mSelectionRightPadding + this.mPaddingRight);
    ((Rect)localObject).bottom = (this.mSelectionBottomPadding + this.mPaddingBottom);
    paramInt1 = this.mTranscriptMode;
    boolean bool = true;
    if (paramInt1 == 1)
    {
      int i = getChildCount();
      paramInt2 = getHeight() - getPaddingBottom();
      localObject = getChildAt(i - 1);
      if (localObject != null) {
        paramInt1 = ((View)localObject).getBottom();
      } else {
        paramInt1 = paramInt2;
      }
      if ((this.mFirstPosition + i < this.mLastHandledItemCount) || (paramInt1 > paramInt2)) {
        bool = false;
      }
      this.mForceTranscriptScroll = bool;
    }
  }
  
  public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    int i = getChildCount();
    if ((!paramBoolean) && (i > 0) && (canScrollList((int)paramFloat2)) && (Math.abs(paramFloat2) > this.mMinimumVelocity))
    {
      reportScrollStateChange(2);
      if (this.mFlingRunnable == null) {
        this.mFlingRunnable = new FlingRunnable();
      }
      if (!dispatchNestedPreFling(0.0F, paramFloat2)) {
        this.mFlingRunnable.start((int)paramFloat2);
      }
      return true;
    }
    return dispatchNestedFling(paramFloat1, paramFloat2, paramBoolean);
  }
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramView = getChildAt(getChildCount() / 2);
    if (paramView != null) {
      paramInt1 = paramView.getTop();
    } else {
      paramInt1 = 0;
    }
    if ((paramView == null) || (trackMotionScroll(-paramInt4, -paramInt4)))
    {
      if (paramView != null)
      {
        paramInt2 = paramView.getTop() - paramInt1;
        paramInt1 = paramInt4 - paramInt2;
      }
      else
      {
        paramInt1 = paramInt4;
        paramInt2 = 0;
      }
      dispatchNestedScroll(0, paramInt2, 0, paramInt1, null);
    }
  }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt)
  {
    super.onNestedScrollAccepted(paramView1, paramView2, paramInt);
    startNestedScroll(2);
  }
  
  protected void onOverScrolled(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mScrollY != paramInt2)
    {
      onScrollChanged(this.mScrollX, paramInt2, this.mScrollX, this.mScrollY);
      this.mScrollY = paramInt2;
      invalidateParentIfNeeded();
      awakenScrollBars();
    }
  }
  
  public boolean onRemoteAdapterConnected()
  {
    RemoteViewsAdapter localRemoteViewsAdapter = this.mRemoteAdapter;
    if (localRemoteViewsAdapter != this.mAdapter)
    {
      setAdapter(localRemoteViewsAdapter);
      if (this.mDeferNotifyDataSetChanged)
      {
        this.mRemoteAdapter.notifyDataSetChanged();
        this.mDeferNotifyDataSetChanged = false;
      }
      return false;
    }
    if (localRemoteViewsAdapter != null)
    {
      localRemoteViewsAdapter.superNotifyDataSetChanged();
      return true;
    }
    return false;
  }
  
  public void onRemoteAdapterDisconnected() {}
  
  public PointerIcon onResolvePointerIcon(MotionEvent paramMotionEvent, int paramInt)
  {
    Object localObject = this.mFastScroll;
    if (localObject != null)
    {
      localObject = ((FastScroller)localObject).onResolvePointerIcon(paramMotionEvent, paramInt);
      if (localObject != null) {
        return (PointerIcon)localObject;
      }
    }
    return super.onResolvePointerIcon(paramMotionEvent, paramInt);
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    this.mDataChanged = true;
    this.mSyncHeight = paramParcelable.height;
    if (paramParcelable.selectedId >= 0L)
    {
      this.mNeedSync = true;
      this.mPendingSync = paramParcelable;
      this.mSyncRowId = paramParcelable.selectedId;
      this.mSyncPosition = paramParcelable.position;
      this.mSpecificTop = paramParcelable.viewTop;
      this.mSyncMode = 0;
    }
    else if (paramParcelable.firstId >= 0L)
    {
      setSelectedPositionInt(-1);
      setNextSelectedPositionInt(-1);
      this.mSelectorPosition = -1;
      this.mNeedSync = true;
      this.mPendingSync = paramParcelable;
      this.mSyncRowId = paramParcelable.firstId;
      this.mSyncPosition = paramParcelable.position;
      this.mSpecificTop = paramParcelable.viewTop;
      this.mSyncMode = 1;
    }
    setFilterText(paramParcelable.filter);
    if (paramParcelable.checkState != null) {
      this.mCheckStates = paramParcelable.checkState;
    }
    if (paramParcelable.checkIdState != null) {
      this.mCheckedIdStates = paramParcelable.checkIdState;
    }
    this.mCheckedItemCount = paramParcelable.checkedItemCount;
    if ((paramParcelable.inActionMode) && (this.mChoiceMode == 3))
    {
      paramParcelable = this.mMultiChoiceModeCallback;
      if (paramParcelable != null) {
        this.mChoiceActionMode = startActionMode(paramParcelable);
      }
    }
    requestLayout();
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    super.onRtlPropertiesChanged(paramInt);
    FastScroller localFastScroller = this.mFastScroll;
    if (localFastScroller != null) {
      localFastScroller.setScrollbarPosition(getVerticalScrollbarPosition());
    }
  }
  
  public Parcelable onSaveInstanceState()
  {
    dismissPopup();
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    Object localObject = this.mPendingSync;
    if (localObject != null)
    {
      localSavedState.selectedId = ((SavedState)localObject).selectedId;
      localSavedState.firstId = this.mPendingSync.firstId;
      localSavedState.viewTop = this.mPendingSync.viewTop;
      localSavedState.position = this.mPendingSync.position;
      localSavedState.height = this.mPendingSync.height;
      localSavedState.filter = this.mPendingSync.filter;
      localSavedState.inActionMode = this.mPendingSync.inActionMode;
      localSavedState.checkedItemCount = this.mPendingSync.checkedItemCount;
      localSavedState.checkState = this.mPendingSync.checkState;
      localSavedState.checkIdState = this.mPendingSync.checkIdState;
      return localSavedState;
    }
    int i = getChildCount();
    boolean bool = true;
    if ((i > 0) && (this.mItemCount > 0)) {
      i = 1;
    } else {
      i = 0;
    }
    long l = getSelectedItemId();
    localSavedState.selectedId = l;
    localSavedState.height = getHeight();
    int j;
    if (l >= 0L)
    {
      localSavedState.viewTop = this.mSelectedTop;
      localSavedState.position = getSelectedItemPosition();
      localSavedState.firstId = -1L;
    }
    else if ((i != 0) && (this.mFirstPosition > 0))
    {
      localSavedState.viewTop = getChildAt(0).getTop();
      j = this.mFirstPosition;
      i = j;
      if (j >= this.mItemCount) {
        i = this.mItemCount - 1;
      }
      localSavedState.position = i;
      localSavedState.firstId = this.mAdapter.getItemId(i);
    }
    else
    {
      localSavedState.viewTop = 0;
      localSavedState.firstId = -1L;
      localSavedState.position = 0;
    }
    localSavedState.filter = null;
    if (this.mFiltered)
    {
      localObject = this.mTextFilter;
      if (localObject != null)
      {
        localObject = ((EditText)localObject).getText();
        if (localObject != null) {
          localSavedState.filter = localObject.toString();
        }
      }
    }
    if ((this.mChoiceMode != 3) || (this.mChoiceActionMode == null)) {
      bool = false;
    }
    localSavedState.inActionMode = bool;
    localObject = this.mCheckStates;
    if (localObject != null) {
      localSavedState.checkState = ((SparseBooleanArray)localObject).clone();
    }
    if (this.mCheckedIdStates != null)
    {
      localObject = new LongSparseArray();
      j = this.mCheckedIdStates.size();
      for (i = 0; i < j; i++) {
        ((LongSparseArray)localObject).put(this.mCheckedIdStates.keyAt(i), (Integer)this.mCheckedIdStates.valueAt(i));
      }
      localSavedState.checkIdState = ((LongSparseArray)localObject);
    }
    localSavedState.checkedItemCount = this.mCheckedItemCount;
    localObject = this.mRemoteAdapter;
    if (localObject != null) {
      ((RemoteViewsAdapter)localObject).saveRemoteViewsCache();
    }
    return localSavedState;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    handleBoundsChange();
    FastScroller localFastScroller = this.mFastScroll;
    if (localFastScroller != null) {
      localFastScroller.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt)
  {
    boolean bool;
    if ((paramInt & 0x2) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
    if (isTextFilterEnabled())
    {
      createTextFilter(true);
      paramInt1 = paramCharSequence.length();
      boolean bool = this.mPopup.isShowing();
      if ((!bool) && (paramInt1 > 0))
      {
        showPopup();
        this.mFiltered = true;
      }
      else if ((bool) && (paramInt1 == 0))
      {
        dismissPopup();
        this.mFiltered = false;
      }
      Object localObject = this.mAdapter;
      if ((localObject instanceof Filterable))
      {
        localObject = ((Filterable)localObject).getFilter();
        if (localObject != null) {
          ((Filter)localObject).filter(paramCharSequence, this);
        } else {
          throw new IllegalStateException("You cannot call onTextChanged with a non filterable adapter");
        }
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1 = isEnabled();
    boolean bool2 = true;
    if (!bool1)
    {
      bool1 = bool2;
      if (!isClickable()) {
        if (isLongClickable()) {
          bool1 = bool2;
        } else {
          bool1 = false;
        }
      }
      return bool1;
    }
    Object localObject = this.mPositionScroller;
    if (localObject != null) {
      ((AbsPositionScroller)localObject).stop();
    }
    if ((!this.mIsDetaching) && (isAttachedToWindow()))
    {
      startNestedScroll(2);
      localObject = this.mFastScroll;
      if ((localObject != null) && (((FastScroller)localObject).onTouchEvent(paramMotionEvent))) {
        return true;
      }
      initVelocityTrackerIfNotExists();
      localObject = MotionEvent.obtain(paramMotionEvent);
      int i = paramMotionEvent.getActionMasked();
      if (i == 0) {
        this.mNestedYOffset = 0;
      }
      ((MotionEvent)localObject).offsetLocation(0.0F, this.mNestedYOffset);
      if (i != 0)
      {
        if (i != 1)
        {
          if (i != 2)
          {
            if (i != 3)
            {
              int j;
              if (i != 5)
              {
                if (i == 6)
                {
                  onSecondaryPointerUp(paramMotionEvent);
                  j = this.mMotionX;
                  i = this.mMotionY;
                  j = pointToPosition(j, i);
                  if (j >= 0)
                  {
                    this.mMotionViewOriginalTop = getChildAt(j - this.mFirstPosition).getTop();
                    this.mMotionPosition = j;
                  }
                  this.mLastY = i;
                  this.mNumTouchMoveEvent = 0;
                }
              }
              else
              {
                int k = paramMotionEvent.getActionIndex();
                j = paramMotionEvent.getPointerId(k);
                i = (int)paramMotionEvent.getX(k);
                k = (int)paramMotionEvent.getY(k);
                this.mMotionCorrection = 0;
                this.mActivePointerId = j;
                this.mMotionX = i;
                this.mMotionY = k;
                i = pointToPosition(i, k);
                if (i >= 0)
                {
                  this.mMotionViewOriginalTop = getChildAt(i - this.mFirstPosition).getTop();
                  this.mMotionPosition = i;
                }
                this.mLastY = k;
                this.mNumTouchMoveEvent = 0;
              }
            }
            else
            {
              onTouchCancel();
              this.mNumTouchMoveEvent = 0;
            }
          }
          else
          {
            this.mNumTouchMoveEvent += 1;
            if (this.mNumTouchMoveEvent == 1) {
              this.mIsFirstTouchMoveEvent = true;
            } else {
              this.mIsFirstTouchMoveEvent = false;
            }
            onTouchMove(paramMotionEvent, (MotionEvent)localObject);
          }
        }
        else
        {
          onTouchUp(paramMotionEvent);
          this.mNumTouchMoveEvent = 0;
        }
      }
      else
      {
        onTouchDown(paramMotionEvent);
        this.mNumTouchMoveEvent = 0;
      }
      paramMotionEvent = this.mVelocityTracker;
      if (paramMotionEvent != null) {
        paramMotionEvent.addMovement((MotionEvent)localObject);
      }
      ((MotionEvent)localObject).recycle();
      return true;
    }
    return false;
  }
  
  public void onTouchModeChanged(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      hideSelector();
      if ((getHeight() > 0) && (getChildCount() > 0)) {
        layoutChildren();
      }
      updateSelectorState();
    }
    else
    {
      int i = this.mTouchMode;
      if ((i == 5) || (i == 6))
      {
        Object localObject = this.mFlingRunnable;
        if (localObject != null) {
          ((FlingRunnable)localObject).endFling();
        }
        localObject = this.mPositionScroller;
        if (localObject != null) {
          ((AbsPositionScroller)localObject).stop();
        }
        if (this.mScrollY != 0)
        {
          this.mScrollY = 0;
          invalidateParentCaches();
          finishGlows();
          invalidate();
        }
      }
    }
  }
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    super.onWindowFocusChanged(paramBoolean);
    int i = isInTouchMode() ^ true;
    if (!paramBoolean)
    {
      setChildrenDrawingCacheEnabled(false);
      Object localObject = this.mFlingRunnable;
      if (localObject != null)
      {
        removeCallbacks((Runnable)localObject);
        localObject = this.mFlingRunnable;
        ((FlingRunnable)localObject).mSuppressIdleStateChangeCall = false;
        ((FlingRunnable)localObject).endFling();
        localObject = this.mPositionScroller;
        if (localObject != null) {
          ((AbsPositionScroller)localObject).stop();
        }
        if (this.mScrollY != 0)
        {
          this.mScrollY = 0;
          invalidateParentCaches();
          finishGlows();
          invalidate();
        }
      }
      dismissPopup();
      if (i == 1) {
        this.mResurrectToPosition = this.mSelectedPosition;
      }
    }
    else
    {
      if ((this.mFiltered) && (!this.mPopupHidden)) {
        showPopup();
      }
      int j = this.mLastTouchMode;
      if ((i != j) && (j != -1)) {
        if (i == 1)
        {
          resurrectSelection();
        }
        else
        {
          hideSelector();
          this.mLayoutMode = 0;
          layoutChildren();
        }
      }
    }
    this.mLastTouchMode = i;
  }
  
  protected boolean overScrollBy(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, boolean paramBoolean)
  {
    return AbsListViewInjector.overScrollBy(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramBoolean);
  }
  
  public boolean performAccessibilityActionInternal(int paramInt, Bundle paramBundle)
  {
    if (super.performAccessibilityActionInternal(paramInt, paramBundle)) {
      return true;
    }
    if (paramInt != 4096) {
      if ((paramInt != 8192) && (paramInt != 16908344))
      {
        if (paramInt != 16908346) {
          return false;
        }
      }
      else
      {
        if ((isEnabled()) && (canScrollUp()))
        {
          smoothScrollBy(-(getHeight() - this.mListPadding.top - this.mListPadding.bottom), 200);
          return true;
        }
        return false;
      }
    }
    if ((isEnabled()) && (canScrollDown()))
    {
      smoothScrollBy(getHeight() - this.mListPadding.top - this.mListPadding.bottom, 200);
      return true;
    }
    return false;
  }
  
  public boolean performItemClick(View paramView, int paramInt, long paramLong)
  {
    boolean bool1 = false;
    int i = 1;
    int j = 1;
    int k = 1;
    int m = 1;
    int n = this.mChoiceMode;
    if (n != 0)
    {
      int i1 = 0;
      if ((n != 2) && ((n != 3) || (this.mChoiceActionMode == null)))
      {
        k = m;
        m = i1;
        if (this.mChoiceMode == 1)
        {
          if ((this.mCheckStates.get(paramInt, false) ^ true))
          {
            this.mCheckStates.clear();
            this.mCheckStates.put(paramInt, true);
            if ((this.mCheckedIdStates != null) && (this.mAdapter.hasStableIds()))
            {
              this.mCheckedIdStates.clear();
              this.mCheckedIdStates.put(this.mAdapter.getItemId(paramInt), Integer.valueOf(paramInt));
            }
            this.mCheckedItemCount = 1;
          }
          else if ((this.mCheckStates.size() == 0) || (!this.mCheckStates.valueAt(0)))
          {
            this.mCheckedItemCount = 0;
          }
          m = 1;
          k = j;
          break label344;
        }
      }
      for (;;)
      {
        break;
        bool1 = this.mCheckStates.get(paramInt, false) ^ true;
        this.mCheckStates.put(paramInt, bool1);
        if ((this.mCheckedIdStates != null) && (this.mAdapter.hasStableIds())) {
          if (bool1) {
            this.mCheckedIdStates.put(this.mAdapter.getItemId(paramInt), Integer.valueOf(paramInt));
          } else {
            this.mCheckedIdStates.delete(this.mAdapter.getItemId(paramInt));
          }
        }
        if (bool1) {
          this.mCheckedItemCount += 1;
        } else {
          this.mCheckedItemCount -= 1;
        }
        ActionMode localActionMode = this.mChoiceActionMode;
        k = i;
        if (localActionMode != null)
        {
          this.mMultiChoiceModeCallback.onItemCheckedStateChanged(localActionMode, paramInt, paramLong, bool1);
          k = 0;
        }
        m = 1;
      }
      label344:
      if (m != 0) {
        updateOnScreenCheckedViews();
      }
      bool1 = true;
    }
    boolean bool2 = bool1;
    if (k != 0) {
      bool2 = bool1 | super.performItemClick(paramView, paramInt, paramLong);
    }
    return bool2;
  }
  
  @UnsupportedAppUsage
  boolean performLongPress(View paramView, int paramInt, long paramLong)
  {
    return performLongPress(paramView, paramInt, paramLong, -1.0F, -1.0F);
  }
  
  @UnsupportedAppUsage
  boolean performLongPress(View paramView, int paramInt, long paramLong, float paramFloat1, float paramFloat2)
  {
    if (this.mChoiceMode == 3)
    {
      if (this.mChoiceActionMode == null)
      {
        paramView = startActionMode(this.mMultiChoiceModeCallback);
        this.mChoiceActionMode = paramView;
        if (paramView != null)
        {
          setItemChecked(paramInt, true);
          performHapticFeedback(0);
        }
      }
      return true;
    }
    boolean bool1 = false;
    if (this.mOnItemLongClickListener != null) {
      bool1 = this.mOnItemLongClickListener.onItemLongClick(this, paramView, paramInt, paramLong);
    }
    boolean bool2 = bool1;
    if (!bool1)
    {
      this.mContextMenuInfo = createContextMenuInfo(paramView, paramInt, paramLong);
      if ((paramFloat1 != -1.0F) && (paramFloat2 != -1.0F)) {
        bool2 = super.showContextMenuForChild(this, paramFloat1, paramFloat2);
      } else {
        bool2 = super.showContextMenuForChild(this);
      }
    }
    if (bool2) {
      performHapticFeedback(0);
    }
    return bool2;
  }
  
  public int pointToPosition(int paramInt1, int paramInt2)
  {
    Object localObject1 = this.mTouchFrame;
    Object localObject2 = localObject1;
    if (localObject1 == null)
    {
      this.mTouchFrame = new Rect();
      localObject2 = this.mTouchFrame;
    }
    for (int i = getChildCount() - 1; i >= 0; i--)
    {
      localObject1 = getChildAt(i);
      if (((View)localObject1).getVisibility() == 0)
      {
        ((View)localObject1).getHitRect((Rect)localObject2);
        if (((Rect)localObject2).contains(paramInt1, paramInt2)) {
          return this.mFirstPosition + i;
        }
      }
    }
    return -1;
  }
  
  public long pointToRowId(int paramInt1, int paramInt2)
  {
    paramInt1 = pointToPosition(paramInt1, paramInt2);
    if (paramInt1 >= 0) {
      return this.mAdapter.getItemId(paramInt1);
    }
    return Long.MIN_VALUE;
  }
  
  void positionSelector(int paramInt, View paramView)
  {
    positionSelector(paramInt, paramView, false, -1.0F, -1.0F);
  }
  
  void positionSelectorLikeFocus(int paramInt, View paramView)
  {
    if ((this.mSelector != null) && (this.mSelectorPosition != paramInt) && (paramInt != -1))
    {
      Rect localRect = this.mSelectorRect;
      positionSelector(paramInt, paramView, true, localRect.exactCenterX(), localRect.exactCenterY());
    }
    else
    {
      positionSelector(paramInt, paramView);
    }
  }
  
  void positionSelectorLikeTouch(int paramInt, View paramView, float paramFloat1, float paramFloat2)
  {
    positionSelector(paramInt, paramView, true, paramFloat1, paramFloat2);
  }
  
  public void reclaimViews(List<View> paramList)
  {
    int i = getChildCount();
    RecyclerListener localRecyclerListener = this.mRecycler.mRecyclerListener;
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if ((localLayoutParams != null) && (this.mRecycler.shouldRecycleViewType(localLayoutParams.viewType)))
      {
        paramList.add(localView);
        localView.setAccessibilityDelegate(null);
        if (localRecyclerListener != null) {
          localRecyclerListener.onMovedToScrapHeap(localView);
        }
      }
    }
    this.mRecycler.reclaimScrapViews(paramList);
    removeAllViewsInLayout();
  }
  
  int reconcileSelectedPosition()
  {
    int i = this.mSelectedPosition;
    int j = i;
    if (i < 0) {
      j = this.mResurrectToPosition;
    }
    return Math.min(Math.max(0, j), this.mItemCount - 1);
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769710L)
  void reportScrollStateChange(int paramInt)
  {
    if (paramInt != this.mLastScrollState)
    {
      OnScrollListener localOnScrollListener = this.mOnScrollListener;
      if (localOnScrollListener != null)
      {
        this.mLastScrollState = paramInt;
        localOnScrollListener.onScrollStateChanged(this, paramInt);
      }
    }
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    if (paramBoolean) {
      recycleVelocityTracker();
    }
    super.requestDisallowInterceptTouchEvent(paramBoolean);
  }
  
  public void requestLayout()
  {
    if ((!this.mBlockLayoutRequests) && (!this.mInLayout)) {
      super.requestLayout();
    }
  }
  
  void requestLayoutIfNecessary()
  {
    if (getChildCount() > 0)
    {
      resetList();
      requestLayout();
      invalidate();
    }
  }
  
  void resetList()
  {
    removeAllViewsInLayout();
    this.mFirstPosition = 0;
    this.mDataChanged = false;
    this.mPositionScrollAfterLayout = null;
    this.mNeedSync = false;
    this.mPendingSync = null;
    this.mOldSelectedPosition = -1;
    this.mOldSelectedRowId = Long.MIN_VALUE;
    setSelectedPositionInt(-1);
    setNextSelectedPositionInt(-1);
    this.mSelectedTop = 0;
    this.mSelectorPosition = -1;
    this.mSelectorRect.setEmpty();
    invalidate();
  }
  
  boolean resurrectSelection()
  {
    int i = getChildCount();
    if (i <= 0) {
      return false;
    }
    int j = 0;
    int k = 0;
    int m = this.mListPadding.top;
    int n = this.mBottom - this.mTop - this.mListPadding.bottom;
    int i1 = this.mFirstPosition;
    int i2 = this.mResurrectToPosition;
    boolean bool = true;
    if ((i2 >= i1) && (i2 < i1 + i))
    {
      localObject = getChildAt(i2 - this.mFirstPosition);
      k = ((View)localObject).getTop();
      j = ((View)localObject).getBottom();
      if (k < m) {
        k = m + getVerticalFadingEdgeLength();
      } else if (j > n) {
        k = n - ((View)localObject).getMeasuredHeight() - getVerticalFadingEdgeLength();
      }
    }
    else
    {
      int i3;
      int i4;
      if (i2 < i1)
      {
        j = i1;
        i3 = 0;
        for (;;)
        {
          n = k;
          i2 = j;
          if (i3 >= i) {
            break;
          }
          i2 = getChildAt(i3).getTop();
          i4 = k;
          n = m;
          if (i3 == 0)
          {
            k = i2;
            if (i1 <= 0)
            {
              i4 = k;
              n = m;
              if (i2 >= m) {}
            }
            else
            {
              n = m + getVerticalFadingEdgeLength();
              i4 = k;
            }
          }
          if (i2 >= n)
          {
            k = i1 + i3;
            n = i2;
            i2 = k;
            break;
          }
          i3++;
          k = i4;
          m = n;
        }
        k = n;
      }
      else
      {
        i4 = this.mItemCount;
        bool = false;
        i2 = i - 1;
        k = j;
        while (i2 >= 0)
        {
          localObject = getChildAt(i2);
          j = ((View)localObject).getTop();
          int i5 = ((View)localObject).getBottom();
          m = k;
          i3 = n;
          if (i2 == i - 1)
          {
            k = j;
            if (i1 + i >= i4)
            {
              m = k;
              i3 = n;
              if (i5 <= n) {}
            }
            else
            {
              i3 = n - getVerticalFadingEdgeLength();
              m = k;
            }
          }
          if (i5 <= i3)
          {
            k = j;
            i2 = i1 + i2;
            break label397;
          }
          i2--;
          k = m;
          n = i3;
        }
        i2 = i1 + i - 1;
      }
    }
    label397:
    this.mResurrectToPosition = -1;
    removeCallbacks(this.mFlingRunnable);
    Object localObject = this.mPositionScroller;
    if (localObject != null) {
      ((AbsPositionScroller)localObject).stop();
    }
    this.mTouchMode = -1;
    clearScrollingCache();
    this.mSpecificTop = k;
    k = lookForSelectablePosition(i2, bool);
    if ((k >= i1) && (k <= getLastVisiblePosition()))
    {
      this.mLayoutMode = 4;
      updateSelectorState();
      setSelectionInt(k);
      invokeOnItemScrollListener();
    }
    else
    {
      k = -1;
    }
    reportScrollStateChange(0);
    if (k >= 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  boolean resurrectSelectionIfNeeded()
  {
    if ((this.mSelectedPosition < 0) && (resurrectSelection()))
    {
      updateSelectorState();
      return true;
    }
    return false;
  }
  
  public void scrollListBy(int paramInt)
  {
    trackMotionScroll(-paramInt, -paramInt);
  }
  
  public void sendAccessibilityEventUnchecked(AccessibilityEvent paramAccessibilityEvent)
  {
    if (paramAccessibilityEvent.getEventType() == 4096)
    {
      int i = getFirstVisiblePosition();
      int j = getLastVisiblePosition();
      if ((this.mLastAccessibilityScrollEventFromIndex == i) && (this.mLastAccessibilityScrollEventToIndex == j)) {
        return;
      }
      this.mLastAccessibilityScrollEventFromIndex = i;
      this.mLastAccessibilityScrollEventToIndex = j;
    }
    super.sendAccessibilityEventUnchecked(paramAccessibilityEvent);
  }
  
  boolean sendToTextFilter(int paramInt1, int paramInt2, KeyEvent paramKeyEvent)
  {
    if (!acceptFilter()) {
      return false;
    }
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = true;
    Object localObject;
    if (paramInt1 != 4)
    {
      if (paramInt1 != 62)
      {
        if (paramInt1 != 66) {
          switch (paramInt1)
          {
          default: 
            bool1 = bool4;
            break;
          }
        } else {
          bool1 = false;
        }
      }
      else {
        bool1 = this.mFiltered;
      }
    }
    else
    {
      bool4 = bool1;
      if (this.mFiltered)
      {
        localObject = this.mPopup;
        bool4 = bool1;
        if (localObject != null)
        {
          bool4 = bool1;
          if (((PopupWindow)localObject).isShowing())
          {
            if ((paramKeyEvent.getAction() == 0) && (paramKeyEvent.getRepeatCount() == 0))
            {
              localObject = getKeyDispatcherState();
              if (localObject != null) {
                ((KeyEvent.DispatcherState)localObject).startTracking(paramKeyEvent, this);
              }
              bool4 = true;
            }
            for (;;)
            {
              break;
              bool4 = bool3;
              if (paramKeyEvent.getAction() == 1)
              {
                bool4 = bool1;
                if (paramKeyEvent.isTracking())
                {
                  bool4 = bool1;
                  if (!paramKeyEvent.isCanceled())
                  {
                    bool4 = true;
                    this.mTextFilter.setText("");
                  }
                }
              }
            }
          }
        }
      }
      bool1 = false;
      bool2 = bool4;
    }
    bool4 = bool2;
    if (bool1)
    {
      createTextFilter(true);
      KeyEvent localKeyEvent = paramKeyEvent;
      localObject = localKeyEvent;
      if (localKeyEvent.getRepeatCount() > 0) {
        localObject = KeyEvent.changeTimeRepeat(paramKeyEvent, paramKeyEvent.getEventTime(), 0);
      }
      int i = paramKeyEvent.getAction();
      if (i != 0)
      {
        if (i != 1)
        {
          if (i != 2) {
            bool4 = bool2;
          } else {
            bool4 = this.mTextFilter.onKeyMultiple(paramInt1, paramInt2, paramKeyEvent);
          }
        }
        else {
          bool4 = this.mTextFilter.onKeyUp(paramInt1, (KeyEvent)localObject);
        }
      }
      else {
        bool4 = this.mTextFilter.onKeyDown(paramInt1, (KeyEvent)localObject);
      }
    }
    return bool4;
  }
  
  public void setAdapter(ListAdapter paramListAdapter)
  {
    if (paramListAdapter != null)
    {
      this.mAdapterHasStableIds = this.mAdapter.hasStableIds();
      if ((this.mChoiceMode != 0) && (this.mAdapterHasStableIds) && (this.mCheckedIdStates == null)) {
        this.mCheckedIdStates = new LongSparseArray();
      }
    }
    clearChoices();
  }
  
  public void setBottomEdgeEffectColor(int paramInt)
  {
    this.mEdgeGlowBottom.setColor(paramInt);
    invalidateBottomGlow();
  }
  
  public void setCacheColorHint(int paramInt)
  {
    if (paramInt != this.mCacheColorHint)
    {
      this.mCacheColorHint = paramInt;
      int i = getChildCount();
      for (int j = 0; j < i; j++) {
        getChildAt(j).setDrawingCacheBackgroundColor(paramInt);
      }
      this.mRecycler.setCacheColorHint(paramInt);
    }
  }
  
  public void setChoiceMode(int paramInt)
  {
    this.mChoiceMode = paramInt;
    Object localObject = this.mChoiceActionMode;
    if (localObject != null)
    {
      ((ActionMode)localObject).finish();
      this.mChoiceActionMode = null;
    }
    if (this.mChoiceMode != 0)
    {
      if (this.mCheckStates == null) {
        this.mCheckStates = new SparseBooleanArray(0);
      }
      if (this.mCheckedIdStates == null)
      {
        localObject = this.mAdapter;
        if ((localObject != null) && (((ListAdapter)localObject).hasStableIds())) {
          this.mCheckedIdStates = new LongSparseArray(0);
        }
      }
      if (this.mChoiceMode == 3)
      {
        clearChoices();
        setLongClickable(true);
      }
    }
  }
  
  public void setDrawSelectorOnTop(boolean paramBoolean)
  {
    this.mDrawSelectorOnTop = paramBoolean;
  }
  
  public void setEdgeEffectColor(int paramInt)
  {
    setTopEdgeEffectColor(paramInt);
    setBottomEdgeEffectColor(paramInt);
  }
  
  public void setFastScrollAlwaysVisible(final boolean paramBoolean)
  {
    if (this.mFastScrollAlwaysVisible != paramBoolean)
    {
      if ((paramBoolean) && (!this.mFastScrollEnabled)) {
        setFastScrollEnabled(true);
      }
      this.mFastScrollAlwaysVisible = paramBoolean;
      if (isOwnerThread()) {
        setFastScrollerAlwaysVisibleUiThread(paramBoolean);
      } else {
        post(new Runnable()
        {
          public void run()
          {
            AbsListView.this.setFastScrollerAlwaysVisibleUiThread(paramBoolean);
          }
        });
      }
    }
  }
  
  public void setFastScrollEnabled(final boolean paramBoolean)
  {
    if (this.mFastScrollEnabled != paramBoolean)
    {
      this.mFastScrollEnabled = paramBoolean;
      if (isOwnerThread()) {
        setFastScrollerEnabledUiThread(paramBoolean);
      } else {
        post(new Runnable()
        {
          public void run()
          {
            AbsListView.this.setFastScrollerEnabledUiThread(paramBoolean);
          }
        });
      }
    }
  }
  
  public void setFastScrollStyle(int paramInt)
  {
    FastScroller localFastScroller = this.mFastScroll;
    if (localFastScroller == null) {
      this.mFastScrollStyle = paramInt;
    } else {
      localFastScroller.setStyle(paramInt);
    }
  }
  
  public void setFilterText(String paramString)
  {
    if ((this.mTextFilterEnabled) && (!TextUtils.isEmpty(paramString)))
    {
      createTextFilter(false);
      this.mTextFilter.setText(paramString);
      this.mTextFilter.setSelection(paramString.length());
      ListAdapter localListAdapter = this.mAdapter;
      if ((localListAdapter instanceof Filterable))
      {
        if (this.mPopup == null) {
          ((Filterable)localListAdapter).getFilter().filter(paramString);
        }
        this.mFiltered = true;
        this.mDataSetObserver.clearSavedState();
      }
    }
  }
  
  protected boolean setFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool = super.setFrame(paramInt1, paramInt2, paramInt3, paramInt4);
    if (bool)
    {
      if (getWindowVisibility() == 0) {
        paramInt1 = 1;
      } else {
        paramInt1 = 0;
      }
      if ((this.mFiltered) && (paramInt1 != 0))
      {
        PopupWindow localPopupWindow = this.mPopup;
        if ((localPopupWindow != null) && (localPopupWindow.isShowing())) {
          positionPopup();
        }
      }
    }
    return bool;
  }
  
  public void setFriction(float paramFloat)
  {
    if (this.mFlingRunnable == null) {
      this.mFlingRunnable = new FlingRunnable();
    }
    this.mFlingRunnable.mScroller.setFriction(paramFloat);
  }
  
  public void setItemChecked(int paramInt, boolean paramBoolean)
  {
    int i = this.mChoiceMode;
    if (i == 0) {
      return;
    }
    if ((paramBoolean) && (i == 3) && (this.mChoiceActionMode == null))
    {
      MultiChoiceModeWrapper localMultiChoiceModeWrapper = this.mMultiChoiceModeCallback;
      if ((localMultiChoiceModeWrapper != null) && (localMultiChoiceModeWrapper.hasWrappedCallback())) {
        this.mChoiceActionMode = startActionMode(this.mMultiChoiceModeCallback);
      } else {
        throw new IllegalStateException("AbsListView: attempted to start selection mode for CHOICE_MODE_MULTIPLE_MODAL but no choice mode callback was supplied. Call setMultiChoiceModeListener to set a callback.");
      }
    }
    int j = this.mChoiceMode;
    i = 0;
    if ((j != 2) && (j != 3))
    {
      if ((this.mCheckedIdStates != null) && (this.mAdapter.hasStableIds())) {
        j = 1;
      } else {
        j = 0;
      }
      if (isItemChecked(paramInt) != paramBoolean) {
        i = 1;
      } else {
        i = 0;
      }
      if ((paramBoolean) || (isItemChecked(paramInt)))
      {
        this.mCheckStates.clear();
        if (j != 0) {
          this.mCheckedIdStates.clear();
        }
      }
      if (paramBoolean)
      {
        this.mCheckStates.put(paramInt, true);
        if (j != 0) {
          this.mCheckedIdStates.put(this.mAdapter.getItemId(paramInt), Integer.valueOf(paramInt));
        }
        this.mCheckedItemCount = 1;
        paramInt = i;
      }
      else if (this.mCheckStates.size() != 0)
      {
        paramInt = i;
        if (this.mCheckStates.valueAt(0)) {}
      }
      else
      {
        this.mCheckedItemCount = 0;
        paramInt = i;
      }
    }
    else
    {
      boolean bool = this.mCheckStates.get(paramInt);
      this.mCheckStates.put(paramInt, paramBoolean);
      if ((this.mCheckedIdStates != null) && (this.mAdapter.hasStableIds())) {
        if (paramBoolean) {
          this.mCheckedIdStates.put(this.mAdapter.getItemId(paramInt), Integer.valueOf(paramInt));
        } else {
          this.mCheckedIdStates.delete(this.mAdapter.getItemId(paramInt));
        }
      }
      if (bool != paramBoolean) {
        i = 1;
      }
      if (i != 0) {
        if (paramBoolean) {
          this.mCheckedItemCount += 1;
        } else {
          this.mCheckedItemCount -= 1;
        }
      }
      if (this.mChoiceActionMode != null)
      {
        long l = this.mAdapter.getItemId(paramInt);
        this.mMultiChoiceModeCallback.onItemCheckedStateChanged(this.mChoiceActionMode, paramInt, l, paramBoolean);
      }
      paramInt = i;
    }
    if ((!this.mInLayout) && (!this.mBlockLayoutRequests) && (paramInt != 0))
    {
      this.mDataChanged = true;
      rememberSyncState();
      requestLayout();
    }
  }
  
  public void setMultiChoiceModeListener(MultiChoiceModeListener paramMultiChoiceModeListener)
  {
    if (this.mMultiChoiceModeCallback == null) {
      this.mMultiChoiceModeCallback = new MultiChoiceModeWrapper();
    }
    this.mMultiChoiceModeCallback.setWrapped(paramMultiChoiceModeListener);
  }
  
  public void setOnScrollListener(OnScrollListener paramOnScrollListener)
  {
    this.mOnScrollListener = paramOnScrollListener;
    invokeOnItemScrollListener();
  }
  
  public void setRecyclerListener(RecyclerListener paramRecyclerListener)
  {
    RecycleBin.access$3802(this.mRecycler, paramRecyclerListener);
  }
  
  public void setRemoteViewsAdapter(Intent paramIntent)
  {
    setRemoteViewsAdapter(paramIntent, false);
  }
  
  public void setRemoteViewsAdapter(Intent paramIntent, boolean paramBoolean)
  {
    if ((this.mRemoteAdapter != null) && (new Intent.FilterComparison(paramIntent).equals(new Intent.FilterComparison(this.mRemoteAdapter.getRemoteViewsServiceIntent())))) {
      return;
    }
    this.mDeferNotifyDataSetChanged = false;
    this.mRemoteAdapter = new RemoteViewsAdapter(getContext(), paramIntent, this, paramBoolean);
    if (this.mRemoteAdapter.isDataReady()) {
      setAdapter(this.mRemoteAdapter);
    }
  }
  
  public Runnable setRemoteViewsAdapterAsync(Intent paramIntent)
  {
    return new RemoteViewsAdapter.AsyncRemoteAdapterAction(this, paramIntent);
  }
  
  public void setRemoteViewsOnClickHandler(RemoteViews.OnClickHandler paramOnClickHandler)
  {
    RemoteViewsAdapter localRemoteViewsAdapter = this.mRemoteAdapter;
    if (localRemoteViewsAdapter != null) {
      localRemoteViewsAdapter.setRemoteViewsOnClickHandler(paramOnClickHandler);
    }
  }
  
  public void setScrollBarStyle(int paramInt)
  {
    super.setScrollBarStyle(paramInt);
    FastScroller localFastScroller = this.mFastScroll;
    if (localFastScroller != null) {
      localFastScroller.setScrollBarStyle(paramInt);
    }
  }
  
  public void setScrollIndicators(View paramView1, View paramView2)
  {
    this.mScrollUp = paramView1;
    this.mScrollDown = paramView2;
  }
  
  public void setScrollingCacheEnabled(boolean paramBoolean)
  {
    if ((this.mScrollingCacheEnabled) && (!paramBoolean)) {
      clearScrollingCache();
    }
    this.mScrollingCacheEnabled = paramBoolean;
  }
  
  public void setSelectionFromTop(int paramInt1, int paramInt2)
  {
    if (this.mAdapter == null) {
      return;
    }
    if (!isInTouchMode())
    {
      int i = lookForSelectablePosition(paramInt1, true);
      paramInt1 = i;
      if (i >= 0)
      {
        setNextSelectedPositionInt(i);
        paramInt1 = i;
      }
    }
    else
    {
      this.mResurrectToPosition = paramInt1;
    }
    if (paramInt1 >= 0)
    {
      this.mLayoutMode = 4;
      this.mSpecificTop = (this.mListPadding.top + paramInt2);
      if (this.mNeedSync)
      {
        this.mSyncPosition = paramInt1;
        this.mSyncRowId = this.mAdapter.getItemId(paramInt1);
      }
      AbsPositionScroller localAbsPositionScroller = this.mPositionScroller;
      if (localAbsPositionScroller != null) {
        localAbsPositionScroller.stop();
      }
      requestLayout();
    }
  }
  
  abstract void setSelectionInt(int paramInt);
  
  public void setSelector(int paramInt)
  {
    setSelector(getContext().getDrawable(paramInt));
  }
  
  public void setSelector(Drawable paramDrawable)
  {
    Object localObject = this.mSelector;
    if (localObject != null)
    {
      ((Drawable)localObject).setCallback(null);
      unscheduleDrawable(this.mSelector);
    }
    this.mSelector = paramDrawable;
    localObject = new Rect();
    paramDrawable.getPadding((Rect)localObject);
    this.mSelectionLeftPadding = ((Rect)localObject).left;
    this.mSelectionTopPadding = ((Rect)localObject).top;
    this.mSelectionRightPadding = ((Rect)localObject).right;
    this.mSelectionBottomPadding = ((Rect)localObject).bottom;
    paramDrawable.setCallback(this);
    updateSelectorState();
  }
  
  public void setSmoothScrollbarEnabled(boolean paramBoolean)
  {
    this.mSmoothScrollbarEnabled = paramBoolean;
  }
  
  public void setStackFromBottom(boolean paramBoolean)
  {
    if (this.mStackFromBottom != paramBoolean)
    {
      this.mStackFromBottom = paramBoolean;
      requestLayoutIfNecessary();
    }
  }
  
  public void setTextFilterEnabled(boolean paramBoolean)
  {
    this.mTextFilterEnabled = paramBoolean;
  }
  
  public void setTopEdgeEffectColor(int paramInt)
  {
    this.mEdgeGlowTop.setColor(paramInt);
    invalidateTopGlow();
  }
  
  public void setTranscriptMode(int paramInt)
  {
    this.mTranscriptMode = paramInt;
  }
  
  public void setVelocityScale(float paramFloat)
  {
    this.mVelocityScale = paramFloat;
  }
  
  public void setVerticalScrollbarPosition(int paramInt)
  {
    super.setVerticalScrollbarPosition(paramInt);
    FastScroller localFastScroller = this.mFastScroll;
    if (localFastScroller != null) {
      localFastScroller.setScrollbarPosition(paramInt);
    }
  }
  
  void setVisibleRangeHint(int paramInt1, int paramInt2)
  {
    RemoteViewsAdapter localRemoteViewsAdapter = this.mRemoteAdapter;
    if (localRemoteViewsAdapter != null) {
      localRemoteViewsAdapter.setVisibleRangeHint(paramInt1, paramInt2);
    }
  }
  
  public final boolean shouldDrawSelector()
  {
    return this.mSelectorRect.isEmpty() ^ true;
  }
  
  boolean shouldShowSelector()
  {
    boolean bool;
    if (((isFocused()) && (!isInTouchMode())) || ((touchModeDrawsInPressedState()) && (isPressed()))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean showContextMenu()
  {
    return showContextMenuInternal(0.0F, 0.0F, false);
  }
  
  public boolean showContextMenu(float paramFloat1, float paramFloat2)
  {
    return showContextMenuInternal(paramFloat1, paramFloat2, true);
  }
  
  public boolean showContextMenuForChild(View paramView)
  {
    if (isShowingContextMenuWithCoords()) {
      return false;
    }
    return showContextMenuForChildInternal(paramView, 0.0F, 0.0F, false);
  }
  
  public boolean showContextMenuForChild(View paramView, float paramFloat1, float paramFloat2)
  {
    return showContextMenuForChildInternal(paramView, paramFloat1, paramFloat2, true);
  }
  
  public void smoothScrollBy(int paramInt1, int paramInt2)
  {
    smoothScrollBy(paramInt1, paramInt2, false, false);
  }
  
  @UnsupportedAppUsage
  void smoothScrollBy(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mFlingRunnable == null) {
      this.mFlingRunnable = new FlingRunnable();
    }
    int i = this.mFirstPosition;
    int j = getChildCount();
    int k = getPaddingTop();
    int m = getHeight();
    int n = getPaddingBottom();
    if ((paramInt1 != 0) && (this.mItemCount != 0) && (j != 0) && ((i != 0) || (getChildAt(0).getTop() != k) || (paramInt1 >= 0)) && ((i + j != this.mItemCount) || (getChildAt(j - 1).getBottom() != m - n) || (paramInt1 <= 0)))
    {
      reportScrollStateChange(2);
      this.mFlingRunnable.startScroll(paramInt1, paramInt2, paramBoolean1, paramBoolean2);
    }
    else
    {
      this.mFlingRunnable.endFling();
      AbsPositionScroller localAbsPositionScroller = this.mPositionScroller;
      if (localAbsPositionScroller != null) {
        localAbsPositionScroller.stop();
      }
    }
  }
  
  void smoothScrollByOffset(int paramInt)
  {
    int i = -1;
    if (paramInt < 0) {
      i = getFirstVisiblePosition();
    } else if (paramInt > 0) {
      i = getLastVisiblePosition();
    }
    if (i > -1)
    {
      View localView = getChildAt(i - getFirstVisiblePosition());
      if (localView != null)
      {
        Rect localRect = new Rect();
        int j = i;
        if (localView.getGlobalVisibleRect(localRect))
        {
          int k = localView.getWidth();
          j = localView.getHeight();
          float f = localRect.width() * localRect.height() / (k * j);
          if ((paramInt < 0) && (f < 0.75F))
          {
            j = i + 1;
          }
          else
          {
            j = i;
            if (paramInt > 0)
            {
              j = i;
              if (f < 0.75F) {
                j = i - 1;
              }
            }
          }
        }
        smoothScrollToPosition(Math.max(0, Math.min(getCount(), j + paramInt)));
      }
    }
  }
  
  public void smoothScrollToPosition(int paramInt)
  {
    if (this.mPositionScroller == null) {
      this.mPositionScroller = createPositionScroller();
    }
    this.mPositionScroller.start(paramInt);
  }
  
  public void smoothScrollToPosition(int paramInt1, int paramInt2)
  {
    if (this.mPositionScroller == null) {
      this.mPositionScroller = createPositionScroller();
    }
    this.mPositionScroller.start(paramInt1, paramInt2);
  }
  
  public void smoothScrollToPositionFromTop(int paramInt1, int paramInt2)
  {
    if (this.mPositionScroller == null) {
      this.mPositionScroller = createPositionScroller();
    }
    this.mPositionScroller.startWithOffset(paramInt1, paramInt2);
  }
  
  public void smoothScrollToPositionFromTop(int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.mPositionScroller == null) {
      this.mPositionScroller = createPositionScroller();
    }
    this.mPositionScroller.startWithOffset(paramInt1, paramInt2, paramInt3);
  }
  
  boolean superOverScrollBy(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, boolean paramBoolean)
  {
    return super.overScrollBy(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramBoolean);
  }
  
  boolean touchModeDrawsInPressedState()
  {
    int i = this.mTouchMode;
    return (i == 1) || (i == 2);
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=124051739L)
  boolean trackMotionScroll(int paramInt1, int paramInt2)
  {
    int i = getChildCount();
    if (i == 0) {
      return true;
    }
    int j = getChildAt(0).getTop();
    int k = getChildAt(i - 1).getBottom();
    Object localObject = this.mListPadding;
    int m = 0;
    int n = 0;
    if ((this.mGroupFlags & 0x22) == 34)
    {
      m = ((Rect)localObject).top;
      n = ((Rect)localObject).bottom;
    }
    int i1 = getHeight() - n;
    n = getHeight() - this.mPaddingBottom - this.mPaddingTop;
    int i2;
    if (paramInt1 < 0) {
      i2 = Math.max(-(n - 1), paramInt1);
    } else {
      i2 = Math.min(n - 1, paramInt1);
    }
    int i3;
    if (paramInt2 < 0) {
      i3 = Math.max(-(n - 1), paramInt2);
    } else {
      i3 = Math.min(n - 1, paramInt2);
    }
    int i4 = this.mFirstPosition;
    if (i4 == 0) {
      this.mFirstPositionDistanceGuess = (j - ((Rect)localObject).top);
    } else {
      this.mFirstPositionDistanceGuess += i3;
    }
    if (i4 + i == this.mItemCount) {
      this.mLastPositionDistanceGuess = (((Rect)localObject).bottom + k);
    } else {
      this.mLastPositionDistanceGuess += i3;
    }
    if ((i4 == 0) && (j >= ((Rect)localObject).top) && (i3 >= 0)) {
      paramInt1 = 1;
    } else {
      paramInt1 = 0;
    }
    if ((i4 + i == this.mItemCount) && (k <= getHeight() - ((Rect)localObject).bottom) && (i3 <= 0)) {
      paramInt2 = 1;
    } else {
      paramInt2 = 0;
    }
    if ((paramInt1 == 0) && (paramInt2 == 0))
    {
      if (i3 < 0) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      boolean bool2 = isInTouchMode();
      if (bool2) {
        hideSelector();
      }
      int i5 = getHeaderViewsCount();
      int i6 = this.mItemCount - getFooterViewsCount();
      n = 0;
      paramInt2 = 0;
      paramInt1 = 0;
      int i7;
      int i8;
      if (bool1)
      {
        n = -i3;
        paramInt2 = n;
        if ((this.mGroupFlags & 0x22) == 34) {
          paramInt2 = n + ((Rect)localObject).top;
        }
        i7 = 0;
        n = i1;
        while (i7 < i)
        {
          localObject = getChildAt(i7);
          if (((View)localObject).getBottom() >= paramInt2) {
            break;
          }
          paramInt1++;
          i8 = i4 + i7;
          if ((i8 >= i5) && (i8 < i6))
          {
            ((View)localObject).clearAccessibilityFocus();
            this.mRecycler.addScrapView((View)localObject, i8);
          }
          i7++;
        }
        n = 0;
        paramInt2 = paramInt1;
        paramInt1 = n;
      }
      else
      {
        paramInt1 = getHeight() - i3;
        i7 = paramInt1;
        if ((this.mGroupFlags & 0x22) == 34) {
          i7 = paramInt1 - ((Rect)localObject).bottom;
        }
        i8 = i - 1;
        paramInt1 = paramInt2;
        for (paramInt2 = i8; paramInt2 >= 0; paramInt2--)
        {
          localObject = getChildAt(paramInt2);
          if (((View)localObject).getTop() <= i7) {
            break;
          }
          n = paramInt2;
          paramInt1++;
          i8 = i4 + paramInt2;
          if ((i8 >= i5) && (i8 < i6))
          {
            ((View)localObject).clearAccessibilityFocus();
            this.mRecycler.addScrapView((View)localObject, i8);
          }
        }
        paramInt2 = paramInt1;
        paramInt1 = n;
      }
      this.mMotionViewNewTop = (this.mMotionViewOriginalTop + i2);
      this.mBlockLayoutRequests = true;
      if (paramInt2 > 0)
      {
        detachViewsFromParent(paramInt1, paramInt2);
        this.mRecycler.removeSkippedScrap();
      }
      if (!awakenScrollBars()) {
        invalidate();
      }
      offsetChildrenTopAndBottom(i3);
      if (bool1) {
        this.mFirstPosition += paramInt2;
      }
      paramInt1 = Math.abs(i3);
      if ((m - j < paramInt1) || (k - i1 < paramInt1)) {
        fillGap(bool1);
      }
      this.mRecycler.fullyDetachScrapViews();
      paramInt2 = 0;
      paramInt1 = 0;
      if (!bool2) {
        if (this.mSelectedPosition != -1)
        {
          paramInt2 = this.mSelectedPosition - this.mFirstPosition;
          if ((paramInt2 >= 0) && (paramInt2 < getChildCount()))
          {
            positionSelector(this.mSelectedPosition, getChildAt(paramInt2));
            paramInt1 = 1;
          }
        }
      }
      for (;;)
      {
        break;
        n = this.mSelectorPosition;
        if (n != -1)
        {
          n -= this.mFirstPosition;
          paramInt1 = paramInt2;
          if (n >= 0)
          {
            paramInt1 = paramInt2;
            if (n < getChildCount())
            {
              positionSelector(this.mSelectorPosition, getChildAt(n));
              paramInt1 = 1;
            }
          }
        }
      }
      if (paramInt1 == 0) {
        this.mSelectorRect.setEmpty();
      }
      this.mBlockLayoutRequests = false;
      invokeOnItemScrollListener();
      return false;
    }
    boolean bool1 = false;
    if (i3 != 0) {
      bool1 = true;
    }
    return bool1;
  }
  
  void updateScrollIndicators()
  {
    View localView = this.mScrollUp;
    int i = 0;
    int j;
    if (localView != null)
    {
      if (canScrollUp()) {
        j = 0;
      } else {
        j = 4;
      }
      localView.setVisibility(j);
    }
    localView = this.mScrollDown;
    if (localView != null)
    {
      if (canScrollDown()) {
        j = i;
      } else {
        j = 4;
      }
      localView.setVisibility(j);
    }
  }
  
  @UnsupportedAppUsage
  void updateSelectorState()
  {
    Drawable localDrawable = this.mSelector;
    if ((localDrawable != null) && (localDrawable.isStateful())) {
      if (shouldShowSelector())
      {
        if (localDrawable.setState(getDrawableStateForSelector())) {
          invalidateDrawable(localDrawable);
        }
      }
      else {
        localDrawable.setState(StateSet.NOTHING);
      }
    }
  }
  
  public boolean verifyDrawable(Drawable paramDrawable)
  {
    boolean bool;
    if ((this.mSelector != paramDrawable) && (!super.verifyDrawable(paramDrawable))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  static abstract class AbsPositionScroller
  {
    public abstract void start(int paramInt);
    
    public abstract void start(int paramInt1, int paramInt2);
    
    public abstract void startWithOffset(int paramInt1, int paramInt2);
    
    public abstract void startWithOffset(int paramInt1, int paramInt2, int paramInt3);
    
    public abstract void stop();
  }
  
  class AdapterDataSetObserver
    extends AdapterView<ListAdapter>.AdapterDataSetObserver
  {
    AdapterDataSetObserver()
    {
      super();
    }
    
    public void onChanged()
    {
      super.onChanged();
      if (AbsListView.this.mFastScroll != null) {
        AbsListView.this.mFastScroll.onSectionsChanged();
      }
    }
    
    public void onInvalidated()
    {
      super.onInvalidated();
      if (AbsListView.this.mFastScroll != null) {
        AbsListView.this.mFastScroll.onSectionsChanged();
      }
    }
  }
  
  private class CheckForKeyLongPress
    extends AbsListView.WindowRunnnable
    implements Runnable
  {
    private CheckForKeyLongPress()
    {
      super(null);
    }
    
    public void run()
    {
      if ((AbsListView.this.isPressed()) && (AbsListView.this.mSelectedPosition >= 0))
      {
        int i = AbsListView.this.mSelectedPosition;
        int j = AbsListView.this.mFirstPosition;
        View localView = AbsListView.this.getChildAt(i - j);
        if (!AbsListView.this.mDataChanged)
        {
          boolean bool = false;
          if (sameWindow())
          {
            AbsListView localAbsListView = AbsListView.this;
            bool = localAbsListView.performLongPress(localView, localAbsListView.mSelectedPosition, AbsListView.this.mSelectedRowId);
          }
          if (bool)
          {
            AbsListView.this.setPressed(false);
            localView.setPressed(false);
          }
        }
        else
        {
          AbsListView.this.setPressed(false);
          if (localView != null) {
            localView.setPressed(false);
          }
        }
      }
    }
  }
  
  private class CheckForLongPress
    extends AbsListView.WindowRunnnable
    implements Runnable
  {
    private static final int INVALID_COORD = -1;
    private float mX = -1.0F;
    private float mY = -1.0F;
    
    private CheckForLongPress()
    {
      super(null);
    }
    
    private void setCoords(float paramFloat1, float paramFloat2)
    {
      this.mX = paramFloat1;
      this.mY = paramFloat2;
    }
    
    public void run()
    {
      int i = AbsListView.this.mMotionPosition;
      Object localObject = AbsListView.this;
      localObject = ((AbsListView)localObject).getChildAt(i - ((AbsListView)localObject).mFirstPosition);
      if (localObject != null)
      {
        i = AbsListView.this.mMotionPosition;
        long l = AbsListView.this.mAdapter.getItemId(AbsListView.this.mMotionPosition);
        boolean bool1 = false;
        boolean bool2 = bool1;
        if (sameWindow())
        {
          bool2 = bool1;
          if (!AbsListView.this.mDataChanged)
          {
            float f1 = this.mX;
            if (f1 != -1.0F)
            {
              float f2 = this.mY;
              if (f2 != -1.0F)
              {
                bool2 = AbsListView.this.performLongPress((View)localObject, i, l, f1, f2);
                break label143;
              }
            }
            bool2 = AbsListView.this.performLongPress((View)localObject, i, l);
          }
        }
        label143:
        if (bool2)
        {
          AbsListView.access$802(AbsListView.this, true);
          AbsListView localAbsListView = AbsListView.this;
          localAbsListView.mTouchMode = -1;
          localAbsListView.setPressed(false);
          ((View)localObject).setPressed(false);
        }
        else
        {
          AbsListView.this.mTouchMode = 2;
        }
      }
    }
  }
  
  private final class CheckForTap
    implements Runnable
  {
    float x;
    float y;
    
    private CheckForTap() {}
    
    public void run()
    {
      if (AbsListView.this.mTouchMode == 0)
      {
        Object localObject1 = AbsListView.this;
        ((AbsListView)localObject1).mTouchMode = 1;
        localObject1 = ((AbsListView)localObject1).getChildAt(((AbsListView)localObject1).mMotionPosition - AbsListView.this.mFirstPosition);
        if ((localObject1 != null) && (!((View)localObject1).hasExplicitFocusable()))
        {
          Object localObject2 = AbsListView.this;
          ((AbsListView)localObject2).mLayoutMode = 0;
          if (!((AbsListView)localObject2).mDataChanged)
          {
            localObject2 = AbsListView.this.mTmpPoint;
            localObject2[0] = this.x;
            localObject2[1] = this.y;
            AbsListView.this.transformPointToViewLocal((float[])localObject2, (View)localObject1);
            ((View)localObject1).drawableHotspotChanged(localObject2[0], localObject2[1]);
            ((View)localObject1).setPressed(true);
            AbsListView.this.setPressed(true);
            AbsListView.this.layoutChildren();
            localObject2 = AbsListView.this;
            ((AbsListView)localObject2).positionSelector(((AbsListView)localObject2).mMotionPosition, (View)localObject1);
            AbsListView.this.refreshDrawableState();
            int i = ViewConfiguration.getLongPressTimeout();
            boolean bool = AbsListView.this.isLongClickable();
            if (AbsListView.this.mSelector != null)
            {
              localObject1 = AbsListView.this.mSelector.getCurrent();
              if ((localObject1 != null) && ((localObject1 instanceof TransitionDrawable))) {
                if (bool) {
                  ((TransitionDrawable)localObject1).startTransition(i);
                } else {
                  ((TransitionDrawable)localObject1).resetTransition();
                }
              }
              AbsListView.this.mSelector.setHotspot(this.x, this.y);
            }
            if (bool)
            {
              if (AbsListView.this.mPendingCheckForLongPress == null)
              {
                localObject1 = AbsListView.this;
                AbsListView.access$1002((AbsListView)localObject1, new AbsListView.CheckForLongPress((AbsListView)localObject1, null));
              }
              AbsListView.this.mPendingCheckForLongPress.setCoords(this.x, this.y);
              AbsListView.this.mPendingCheckForLongPress.rememberWindowAttachCount();
              localObject1 = AbsListView.this;
              ((AbsListView)localObject1).postDelayed(((AbsListView)localObject1).mPendingCheckForLongPress, i);
            }
            else
            {
              AbsListView.this.mTouchMode = 2;
            }
          }
          else
          {
            AbsListView.this.mTouchMode = 2;
          }
        }
      }
    }
  }
  
  private class FlingRunnable
    implements Runnable
  {
    private static final int FLYWHEEL_TIMEOUT = 40;
    private final Runnable mCheckFlywheel = new Runnable()
    {
      public void run()
      {
        int i = AbsListView.this.mActivePointerId;
        VelocityTracker localVelocityTracker = AbsListView.this.mVelocityTracker;
        OverScroller localOverScroller = AbsListView.FlingRunnable.this.mScroller;
        if ((localVelocityTracker != null) && (i != -1))
        {
          localVelocityTracker.computeCurrentVelocity(1000, AbsListView.this.mMaximumVelocity);
          float f = -localVelocityTracker.getYVelocity(i);
          if ((Math.abs(f) >= AbsListView.this.mMinimumVelocity) && (localOverScroller.isScrollingInDirection(0.0F, f)))
          {
            AbsListView.this.postDelayed(this, 40L);
          }
          else
          {
            AbsListView.FlingRunnable.this.endFling();
            AbsListView.this.mTouchMode = 3;
            AbsListView.this.reportScrollStateChange(1);
          }
          return;
        }
      }
    };
    private int mLastFlingY;
    @UnsupportedAppUsage
    OverScroller mScroller = new OverScroller(AbsListView.this.getContext());
    boolean mSuppressIdleStateChangeCall;
    
    FlingRunnable() {}
    
    void edgeReached(int paramInt)
    {
      this.mScroller.notifyVerticalEdgeReached(AbsListView.this.mScrollY, 0, AbsListView.this.mOverflingDistance);
      int i = AbsListView.this.getOverScrollMode();
      if ((i != 0) && ((i != 1) || (AbsListView.this.contentFits())))
      {
        AbsListView localAbsListView = AbsListView.this;
        localAbsListView.mTouchMode = -1;
        if (localAbsListView.mPositionScroller != null) {
          AbsListView.this.mPositionScroller.stop();
        }
      }
      else
      {
        AbsListView.this.mTouchMode = 6;
        i = (int)this.mScroller.getCurrVelocity();
        if (paramInt > 0) {
          AbsListView.this.mEdgeGlowTop.onAbsorb(i);
        } else {
          AbsListView.this.mEdgeGlowBottom.onAbsorb(i);
        }
      }
      AbsListView.this.invalidate();
      AbsListView.this.postOnAnimation(this);
    }
    
    @UnsupportedAppUsage(maxTargetSdk=28)
    void endFling()
    {
      AbsListView localAbsListView = AbsListView.this;
      localAbsListView.mTouchMode = -1;
      localAbsListView.removeCallbacks(this);
      AbsListView.this.removeCallbacks(this.mCheckFlywheel);
      if (!this.mSuppressIdleStateChangeCall) {
        AbsListView.this.reportScrollStateChange(0);
      }
      AbsListView.this.clearScrollingCache();
      this.mScroller.abortAnimation();
      if (AbsListView.this.mFlingStrictSpan != null)
      {
        AbsListView.this.mFlingStrictSpan.finish();
        AbsListView.access$2102(AbsListView.this, null);
      }
    }
    
    void flywheelTouch()
    {
      AbsListView.this.postDelayed(this.mCheckFlywheel, 40L);
    }
    
    public void run()
    {
      int i = AbsListView.this.mTouchMode;
      int j = 0;
      int k = 0;
      Object localObject;
      int m;
      int n;
      if (i != 3)
      {
        if (i != 4)
        {
          if (i != 6)
          {
            endFling();
            return;
          }
          localObject = this.mScroller;
          if (((OverScroller)localObject).computeScrollOffset())
          {
            j = AbsListView.this.mScrollY;
            m = ((OverScroller)localObject).getCurrY();
            AbsListView localAbsListView = AbsListView.this;
            if (localAbsListView.overScrollBy(0, m - j, 0, j, 0, 0, 0, localAbsListView.mOverflingDistance, false))
            {
              if ((j <= 0) && (m > 0)) {
                n = 1;
              } else {
                n = 0;
              }
              i = k;
              if (j >= 0)
              {
                i = k;
                if (m < 0) {
                  i = 1;
                }
              }
              if ((n == 0) && (i == 0))
              {
                startSpringback();
              }
              else
              {
                k = (int)((OverScroller)localObject).getCurrVelocity();
                n = k;
                if (i != 0) {
                  n = -k;
                }
                ((OverScroller)localObject).abortAnimation();
                start(n);
              }
            }
            else
            {
              AbsListView.this.invalidate();
              AbsListView.this.postOnAnimation(this);
            }
            break label615;
          }
          endFling();
          break label615;
        }
      }
      else if (this.mScroller.isFinished()) {
        return;
      }
      if (AbsListView.this.mDataChanged) {
        AbsListView.this.layoutChildren();
      }
      if ((AbsListView.this.mItemCount != 0) && (AbsListView.this.getChildCount() != 0))
      {
        localObject = this.mScroller;
        boolean bool1 = ((OverScroller)localObject).computeScrollOffset();
        m = ((OverScroller)localObject).getCurrY();
        i = this.mLastFlingY - m;
        if (i > 0)
        {
          localObject = AbsListView.this;
          ((AbsListView)localObject).mMotionPosition = ((AbsListView)localObject).mFirstPosition;
          localObject = AbsListView.this.getChildAt(0);
          AbsListView.this.mMotionViewOriginalTop = ((View)localObject).getTop();
          i = Math.min(AbsListView.this.getHeight() - AbsListView.this.mPaddingBottom - AbsListView.this.mPaddingTop - 1, i);
        }
        else
        {
          n = AbsListView.this.getChildCount() - 1;
          localObject = AbsListView.this;
          ((AbsListView)localObject).mMotionPosition = (((AbsListView)localObject).mFirstPosition + n);
          localObject = AbsListView.this.getChildAt(n);
          AbsListView.this.mMotionViewOriginalTop = ((View)localObject).getTop();
          i = Math.max(-(AbsListView.this.getHeight() - AbsListView.this.mPaddingBottom - AbsListView.this.mPaddingTop - 1), i);
        }
        localObject = AbsListView.this;
        localObject = ((AbsListView)localObject).getChildAt(((AbsListView)localObject).mMotionPosition - AbsListView.this.mFirstPosition);
        n = 0;
        if (localObject != null) {
          n = ((View)localObject).getTop();
        }
        boolean bool2 = AbsListView.this.trackMotionScroll(i, i);
        k = j;
        if (bool2)
        {
          k = j;
          if (i != 0) {
            k = 1;
          }
        }
        if (k != 0)
        {
          if (localObject != null)
          {
            n = -(i - (((View)localObject).getTop() - n));
            localObject = AbsListView.this;
            ((AbsListView)localObject).overScrollBy(0, n, 0, ((AbsListView)localObject).mScrollY, 0, 0, 0, AbsListView.this.mOverflingDistance, false);
          }
          if (bool1) {
            edgeReached(i);
          }
        }
        else if ((bool1) && (k == 0))
        {
          if (bool2) {
            AbsListView.this.invalidate();
          }
          this.mLastFlingY = m;
          AbsListView.this.postOnAnimation(this);
        }
        else
        {
          endFling();
        }
        label615:
        return;
      }
      endFling();
    }
    
    @UnsupportedAppUsage(maxTargetSdk=28)
    void start(int paramInt)
    {
      int i;
      if (paramInt < 0) {
        i = Integer.MAX_VALUE;
      } else {
        i = 0;
      }
      this.mLastFlingY = i;
      this.mScroller.setInterpolator(null);
      this.mScroller.fling(0, i, 0, paramInt, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
      AbsListView localAbsListView = AbsListView.this;
      localAbsListView.mTouchMode = 4;
      this.mSuppressIdleStateChangeCall = false;
      localAbsListView.postOnAnimation(this);
      if (AbsListView.this.mFlingStrictSpan == null) {
        AbsListView.access$2102(AbsListView.this, StrictMode.enterCriticalSpan("AbsListView-fling"));
      }
    }
    
    void startOverfling(int paramInt)
    {
      this.mScroller.setInterpolator(null);
      this.mScroller.fling(0, AbsListView.this.mScrollY, 0, paramInt, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, AbsListView.this.getHeight());
      AbsListView localAbsListView = AbsListView.this;
      localAbsListView.mTouchMode = 6;
      this.mSuppressIdleStateChangeCall = false;
      localAbsListView.invalidate();
      AbsListView.this.postOnAnimation(this);
    }
    
    void startScroll(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    {
      int i;
      if (paramInt1 < 0) {
        i = Integer.MAX_VALUE;
      } else {
        i = 0;
      }
      this.mLastFlingY = i;
      OverScroller localOverScroller = this.mScroller;
      if (paramBoolean1) {
        localObject = AbsListView.sLinearInterpolator;
      } else {
        localObject = null;
      }
      localOverScroller.setInterpolator((Interpolator)localObject);
      this.mScroller.startScroll(0, i, 0, paramInt1, paramInt2);
      Object localObject = AbsListView.this;
      ((AbsListView)localObject).mTouchMode = 4;
      this.mSuppressIdleStateChangeCall = paramBoolean2;
      ((AbsListView)localObject).postOnAnimation(this);
    }
    
    void startSpringback()
    {
      this.mSuppressIdleStateChangeCall = false;
      AbsListView localAbsListView;
      if (this.mScroller.springBack(0, AbsListView.this.mScrollY, 0, 0, 0, 0))
      {
        localAbsListView = AbsListView.this;
        localAbsListView.mTouchMode = 6;
        localAbsListView.invalidate();
        AbsListView.this.postOnAnimation(this);
      }
      else
      {
        localAbsListView = AbsListView.this;
        localAbsListView.mTouchMode = -1;
        localAbsListView.reportScrollStateChange(0);
      }
    }
  }
  
  private class InputConnectionWrapper
    implements InputConnection
  {
    private final EditorInfo mOutAttrs;
    private InputConnection mTarget;
    
    public InputConnectionWrapper(EditorInfo paramEditorInfo)
    {
      this.mOutAttrs = paramEditorInfo;
    }
    
    private InputConnection getTarget()
    {
      if (this.mTarget == null) {
        this.mTarget = AbsListView.this.getTextFilterInput().onCreateInputConnection(this.mOutAttrs);
      }
      return this.mTarget;
    }
    
    public boolean beginBatchEdit()
    {
      return getTarget().beginBatchEdit();
    }
    
    public boolean clearMetaKeyStates(int paramInt)
    {
      return getTarget().clearMetaKeyStates(paramInt);
    }
    
    public void closeConnection()
    {
      getTarget().closeConnection();
    }
    
    public boolean commitCompletion(CompletionInfo paramCompletionInfo)
    {
      return getTarget().commitCompletion(paramCompletionInfo);
    }
    
    public boolean commitContent(InputContentInfo paramInputContentInfo, int paramInt, Bundle paramBundle)
    {
      return getTarget().commitContent(paramInputContentInfo, paramInt, paramBundle);
    }
    
    public boolean commitCorrection(CorrectionInfo paramCorrectionInfo)
    {
      return getTarget().commitCorrection(paramCorrectionInfo);
    }
    
    public boolean commitText(CharSequence paramCharSequence, int paramInt)
    {
      return getTarget().commitText(paramCharSequence, paramInt);
    }
    
    public boolean deleteSurroundingText(int paramInt1, int paramInt2)
    {
      return getTarget().deleteSurroundingText(paramInt1, paramInt2);
    }
    
    public boolean deleteSurroundingTextInCodePoints(int paramInt1, int paramInt2)
    {
      return getTarget().deleteSurroundingTextInCodePoints(paramInt1, paramInt2);
    }
    
    public boolean endBatchEdit()
    {
      return getTarget().endBatchEdit();
    }
    
    public boolean finishComposingText()
    {
      InputConnection localInputConnection = this.mTarget;
      boolean bool;
      if ((localInputConnection != null) && (!localInputConnection.finishComposingText())) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    public int getCursorCapsMode(int paramInt)
    {
      InputConnection localInputConnection = this.mTarget;
      if (localInputConnection == null) {
        return 16384;
      }
      return localInputConnection.getCursorCapsMode(paramInt);
    }
    
    public ExtractedText getExtractedText(ExtractedTextRequest paramExtractedTextRequest, int paramInt)
    {
      return getTarget().getExtractedText(paramExtractedTextRequest, paramInt);
    }
    
    public Handler getHandler()
    {
      return getTarget().getHandler();
    }
    
    public CharSequence getSelectedText(int paramInt)
    {
      InputConnection localInputConnection = this.mTarget;
      if (localInputConnection == null) {
        return "";
      }
      return localInputConnection.getSelectedText(paramInt);
    }
    
    public CharSequence getTextAfterCursor(int paramInt1, int paramInt2)
    {
      InputConnection localInputConnection = this.mTarget;
      if (localInputConnection == null) {
        return "";
      }
      return localInputConnection.getTextAfterCursor(paramInt1, paramInt2);
    }
    
    public CharSequence getTextBeforeCursor(int paramInt1, int paramInt2)
    {
      InputConnection localInputConnection = this.mTarget;
      if (localInputConnection == null) {
        return "";
      }
      return localInputConnection.getTextBeforeCursor(paramInt1, paramInt2);
    }
    
    public boolean performContextMenuAction(int paramInt)
    {
      return getTarget().performContextMenuAction(paramInt);
    }
    
    public boolean performEditorAction(int paramInt)
    {
      if (paramInt == 6)
      {
        InputMethodManager localInputMethodManager = (InputMethodManager)AbsListView.this.getContext().getSystemService(InputMethodManager.class);
        if (localInputMethodManager != null) {
          localInputMethodManager.hideSoftInputFromWindow(AbsListView.this.getWindowToken(), 0);
        }
        return true;
      }
      return false;
    }
    
    public boolean performPrivateCommand(String paramString, Bundle paramBundle)
    {
      return getTarget().performPrivateCommand(paramString, paramBundle);
    }
    
    public boolean reportFullscreenMode(boolean paramBoolean)
    {
      return AbsListView.this.mDefInputConnection.reportFullscreenMode(paramBoolean);
    }
    
    public boolean requestCursorUpdates(int paramInt)
    {
      return getTarget().requestCursorUpdates(paramInt);
    }
    
    public boolean sendKeyEvent(KeyEvent paramKeyEvent)
    {
      return AbsListView.this.mDefInputConnection.sendKeyEvent(paramKeyEvent);
    }
    
    public boolean setComposingRegion(int paramInt1, int paramInt2)
    {
      return getTarget().setComposingRegion(paramInt1, paramInt2);
    }
    
    public boolean setComposingText(CharSequence paramCharSequence, int paramInt)
    {
      return getTarget().setComposingText(paramCharSequence, paramInt);
    }
    
    public boolean setSelection(int paramInt1, int paramInt2)
    {
      return getTarget().setSelection(paramInt1, paramInt2);
    }
  }
  
  public static class LayoutParams
    extends ViewGroup.LayoutParams
  {
    @ViewDebug.ExportedProperty(category="list")
    boolean forceAdd;
    boolean isEnabled;
    long itemId = -1L;
    @ViewDebug.ExportedProperty(category="list")
    boolean recycledHeaderFooter;
    @UnsupportedAppUsage
    int scrappedFromPosition;
    @ViewDebug.ExportedProperty(category="list", mapping={@android.view.ViewDebug.IntToString(from=-1, to="ITEM_VIEW_TYPE_IGNORE"), @android.view.ViewDebug.IntToString(from=-2, to="ITEM_VIEW_TYPE_HEADER_OR_FOOTER")})
    @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
    int viewType;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(int paramInt1, int paramInt2, int paramInt3)
    {
      super(paramInt2);
      this.viewType = paramInt3;
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
    {
      super.encodeProperties(paramViewHierarchyEncoder);
      paramViewHierarchyEncoder.addProperty("list:viewType", this.viewType);
      paramViewHierarchyEncoder.addProperty("list:recycledHeaderFooter", this.recycledHeaderFooter);
      paramViewHierarchyEncoder.addProperty("list:forceAdd", this.forceAdd);
      paramViewHierarchyEncoder.addProperty("list:isEnabled", this.isEnabled);
    }
  }
  
  class ListItemAccessibilityDelegate
    extends View.AccessibilityDelegate
  {
    ListItemAccessibilityDelegate() {}
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfo);
      int i = AbsListView.this.getPositionForView(paramView);
      AbsListView.this.onInitializeAccessibilityNodeInfoForItem(paramView, i, paramAccessibilityNodeInfo);
    }
    
    public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
    {
      if (super.performAccessibilityAction(paramView, paramInt, paramBundle)) {
        return true;
      }
      int i = AbsListView.this.getPositionForView(paramView);
      if ((i != -1) && (AbsListView.this.mAdapter != null))
      {
        if (i >= AbsListView.this.mAdapter.getCount()) {
          return false;
        }
        paramBundle = paramView.getLayoutParams();
        boolean bool;
        if ((paramBundle instanceof AbsListView.LayoutParams)) {
          bool = ((AbsListView.LayoutParams)paramBundle).isEnabled;
        } else {
          bool = false;
        }
        if ((AbsListView.this.isEnabled()) && (bool))
        {
          if (paramInt != 4)
          {
            if (paramInt != 8)
            {
              long l;
              if (paramInt != 16)
              {
                if (paramInt != 32) {
                  return false;
                }
                if (AbsListView.this.isLongClickable())
                {
                  l = AbsListView.this.getItemIdAtPosition(i);
                  return AbsListView.this.performLongPress(paramView, i, l);
                }
                return false;
              }
              if (AbsListView.this.isItemClickable(paramView))
              {
                l = AbsListView.this.getItemIdAtPosition(i);
                return AbsListView.this.performItemClick(paramView, i, l);
              }
              return false;
            }
            if (AbsListView.this.getSelectedItemPosition() == i)
            {
              AbsListView.this.setSelection(-1);
              return true;
            }
            return false;
          }
          if (AbsListView.this.getSelectedItemPosition() != i)
          {
            AbsListView.this.setSelection(i);
            return true;
          }
          return false;
        }
        return false;
      }
      return false;
    }
  }
  
  public static abstract interface MultiChoiceModeListener
    extends ActionMode.Callback
  {
    public abstract void onItemCheckedStateChanged(ActionMode paramActionMode, int paramInt, long paramLong, boolean paramBoolean);
  }
  
  class MultiChoiceModeWrapper
    implements AbsListView.MultiChoiceModeListener
  {
    private AbsListView.MultiChoiceModeListener mWrapped;
    
    MultiChoiceModeWrapper() {}
    
    public boolean hasWrappedCallback()
    {
      boolean bool;
      if (this.mWrapped != null) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public boolean onActionItemClicked(ActionMode paramActionMode, MenuItem paramMenuItem)
    {
      return this.mWrapped.onActionItemClicked(paramActionMode, paramMenuItem);
    }
    
    public boolean onCreateActionMode(ActionMode paramActionMode, Menu paramMenu)
    {
      if (this.mWrapped.onCreateActionMode(paramActionMode, paramMenu))
      {
        AbsListView.this.setLongClickable(false);
        return true;
      }
      return false;
    }
    
    public void onDestroyActionMode(ActionMode paramActionMode)
    {
      this.mWrapped.onDestroyActionMode(paramActionMode);
      paramActionMode = AbsListView.this;
      paramActionMode.mChoiceActionMode = null;
      paramActionMode.clearChoices();
      paramActionMode = AbsListView.this;
      paramActionMode.mDataChanged = true;
      paramActionMode.rememberSyncState();
      AbsListView.this.requestLayout();
      AbsListView.this.setLongClickable(true);
    }
    
    public void onItemCheckedStateChanged(ActionMode paramActionMode, int paramInt, long paramLong, boolean paramBoolean)
    {
      this.mWrapped.onItemCheckedStateChanged(paramActionMode, paramInt, paramLong, paramBoolean);
      if (AbsListView.this.getCheckedItemCount() == 0) {
        paramActionMode.finish();
      }
    }
    
    public boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu)
    {
      return this.mWrapped.onPrepareActionMode(paramActionMode, paramMenu);
    }
    
    public void setWrapped(AbsListView.MultiChoiceModeListener paramMultiChoiceModeListener)
    {
      this.mWrapped = paramMultiChoiceModeListener;
    }
  }
  
  public static abstract interface OnScrollListener
  {
    public static final int SCROLL_STATE_FLING = 2;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_TOUCH_SCROLL = 1;
    
    public abstract void onScroll(AbsListView paramAbsListView, int paramInt1, int paramInt2, int paramInt3);
    
    public abstract void onScrollStateChanged(AbsListView paramAbsListView, int paramInt);
  }
  
  class OverFlingRunnable
    extends AbsListView.FlingRunnable
  {
    OverFlingRunnable()
    {
      super();
      OverScrollLogger.debug("startOverfling: replacing default configuration.");
      this.mScroller = new DynamicOverScroller(AbsListView.this.getContext());
    }
    
    public void run()
    {
      AbsListViewInjector.doAnimationFrame(AbsListView.this, this);
    }
    
    void startOverfling(int paramInt)
    {
      AbsListViewInjector.startOverfling(AbsListView.this, this, paramInt);
      this.mSuppressIdleStateChangeCall = false;
    }
    
    void superDoAnimationFrame()
    {
      super.run();
    }
  }
  
  private class PerformClick
    extends AbsListView.WindowRunnnable
    implements Runnable
  {
    int mClickMotionPosition;
    
    private PerformClick()
    {
      super(null);
    }
    
    public void run()
    {
      if (AbsListView.this.mDataChanged) {
        return;
      }
      ListAdapter localListAdapter = AbsListView.this.mAdapter;
      int i = this.mClickMotionPosition;
      if ((localListAdapter != null) && (AbsListView.this.mItemCount > 0) && (i != -1) && (i < localListAdapter.getCount()) && (sameWindow()) && (localListAdapter.isEnabled(i)))
      {
        Object localObject = AbsListView.this;
        localObject = ((AbsListView)localObject).getChildAt(i - ((AbsListView)localObject).mFirstPosition);
        if (localObject != null) {
          AbsListView.this.performItemClick((View)localObject, i, localListAdapter.getItemId(i));
        }
      }
    }
  }
  
  class PositionScroller
    extends AbsListView.AbsPositionScroller
    implements Runnable
  {
    private static final int MOVE_DOWN_BOUND = 3;
    private static final int MOVE_DOWN_POS = 1;
    private static final int MOVE_OFFSET = 5;
    private static final int MOVE_UP_BOUND = 4;
    private static final int MOVE_UP_POS = 2;
    private static final int SCROLL_DURATION = 200;
    private int mBoundPos;
    private final int mExtraScroll = ViewConfiguration.get(AbsListView.this.mContext).getScaledFadingEdgeLength();
    private int mLastSeenPos;
    private int mMode;
    private int mOffsetFromTop;
    private int mScrollDuration;
    private int mTargetPos;
    
    PositionScroller() {}
    
    private void scrollToVisible(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = paramInt2;
      int j = AbsListView.this.mFirstPosition;
      int k = j + AbsListView.this.getChildCount() - 1;
      int m = AbsListView.this.mListPadding.top;
      int n = AbsListView.this.getHeight() - AbsListView.this.mListPadding.bottom;
      if ((paramInt1 < j) || (paramInt1 > k))
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("scrollToVisible called with targetPos ");
        ((StringBuilder)localObject).append(paramInt1);
        ((StringBuilder)localObject).append(" not visible [");
        ((StringBuilder)localObject).append(j);
        ((StringBuilder)localObject).append(", ");
        ((StringBuilder)localObject).append(k);
        ((StringBuilder)localObject).append("]");
        Log.w("AbsListView", ((StringBuilder)localObject).toString());
      }
      if (i >= j)
      {
        paramInt2 = i;
        if (i <= k) {}
      }
      else
      {
        paramInt2 = -1;
      }
      Object localObject = AbsListView.this.getChildAt(paramInt1 - j);
      i = ((View)localObject).getTop();
      k = ((View)localObject).getBottom();
      paramInt1 = 0;
      if (k > n) {
        paramInt1 = k - n;
      }
      if (i < m) {
        paramInt1 = i - m;
      }
      if (paramInt1 == 0) {
        return;
      }
      if (paramInt2 >= 0)
      {
        localObject = AbsListView.this.getChildAt(paramInt2 - j);
        j = ((View)localObject).getTop();
        paramInt2 = ((View)localObject).getBottom();
        i = Math.abs(paramInt1);
        if ((paramInt1 < 0) && (paramInt2 + i > n))
        {
          paramInt2 = Math.max(0, paramInt2 - n);
        }
        else
        {
          paramInt2 = paramInt1;
          if (paramInt1 > 0)
          {
            paramInt2 = paramInt1;
            if (j - i < m) {
              paramInt2 = Math.min(0, j - m);
            }
          }
        }
      }
      else
      {
        paramInt2 = paramInt1;
      }
      AbsListView.this.smoothScrollBy(paramInt2, paramInt3);
    }
    
    public void run()
    {
      int i = AbsListView.this.getHeight();
      int j = AbsListView.this.mFirstPosition;
      int k = this.mMode;
      boolean bool = false;
      int m;
      int n;
      Object localObject;
      int i1;
      if (k != 1)
      {
        if (k != 2)
        {
          if (k != 3)
          {
            if (k != 4)
            {
              if (k == 5)
              {
                if (this.mLastSeenPos == j)
                {
                  AbsListView.this.postOnAnimation(this);
                  return;
                }
                this.mLastSeenPos = j;
                m = AbsListView.this.getChildCount();
                if (m <= 0) {
                  return;
                }
                i = this.mTargetPos;
                n = j + m - 1;
                localObject = AbsListView.this.getChildAt(0);
                k = ((View)localObject).getHeight();
                View localView = AbsListView.this.getChildAt(m - 1);
                i1 = localView.getHeight();
                if (k == 0.0F) {
                  f1 = 1.0F;
                } else {
                  f1 = (((View)localObject).getTop() + k) / k;
                }
                float f2;
                if (i1 == 0.0F) {
                  f2 = 1.0F;
                } else {
                  f2 = (AbsListView.this.getHeight() + i1 - localView.getBottom()) / i1;
                }
                float f3 = 0.0F;
                if (i < j)
                {
                  f1 = j - i + (1.0F - f1) + 1.0F;
                }
                else
                {
                  f1 = f3;
                  if (i > n) {
                    f1 = i - n + (1.0F - f2);
                  }
                }
                float f1 = Math.min(Math.abs(f1 / m), 1.0F);
                if (i < j)
                {
                  k = (int)(-AbsListView.this.getHeight() * f1);
                  i = (int)(this.mScrollDuration * f1);
                  AbsListView.this.smoothScrollBy(k, i, true, true);
                  AbsListView.this.postOnAnimation(this);
                }
                else if (i > n)
                {
                  k = (int)(AbsListView.this.getHeight() * f1);
                  i = (int)(this.mScrollDuration * f1);
                  AbsListView.this.smoothScrollBy(k, i, true, true);
                  AbsListView.this.postOnAnimation(this);
                }
                else
                {
                  i = AbsListView.this.getChildAt(i - j).getTop() - this.mOffsetFromTop;
                  k = (int)(this.mScrollDuration * (Math.abs(i) / AbsListView.this.getHeight()));
                  AbsListView.this.smoothScrollBy(i, k, true, false);
                }
              }
            }
            else
            {
              k = AbsListView.this.getChildCount() - 2;
              if (k < 0) {
                return;
              }
              i1 = j + k;
              if (i1 == this.mLastSeenPos)
              {
                AbsListView.this.postOnAnimation(this);
                return;
              }
              localObject = AbsListView.this.getChildAt(k);
              k = ((View)localObject).getHeight();
              j = ((View)localObject).getTop();
              m = Math.max(AbsListView.this.mListPadding.top, this.mExtraScroll);
              this.mLastSeenPos = i1;
              if (i1 > this.mBoundPos)
              {
                AbsListView.this.smoothScrollBy(-(i - j - m), this.mScrollDuration, true, true);
                AbsListView.this.postOnAnimation(this);
              }
              else
              {
                i -= m;
                k = j + k;
                if (i > k) {
                  AbsListView.this.smoothScrollBy(-(i - k), this.mScrollDuration, true, false);
                } else {
                  AbsListView.this.reportScrollStateChange(0);
                }
              }
            }
          }
          else
          {
            k = AbsListView.this.getChildCount();
            if ((j != this.mBoundPos) && (k > 1) && (j + k < AbsListView.this.mItemCount))
            {
              i1 = j + 1;
              if (i1 == this.mLastSeenPos)
              {
                AbsListView.this.postOnAnimation(this);
                return;
              }
              localObject = AbsListView.this.getChildAt(1);
              j = ((View)localObject).getHeight();
              i = ((View)localObject).getTop();
              k = Math.max(AbsListView.this.mListPadding.bottom, this.mExtraScroll);
              if (i1 < this.mBoundPos)
              {
                AbsListView.this.smoothScrollBy(Math.max(0, j + i - k), this.mScrollDuration, true, true);
                this.mLastSeenPos = i1;
                AbsListView.this.postOnAnimation(this);
              }
              else if (i > k)
              {
                AbsListView.this.smoothScrollBy(i - k, this.mScrollDuration, true, false);
              }
              else
              {
                AbsListView.this.reportScrollStateChange(0);
              }
            }
            else
            {
              AbsListView.this.reportScrollStateChange(0);
            }
          }
        }
        else
        {
          if (j == this.mLastSeenPos)
          {
            AbsListView.this.postOnAnimation(this);
            return;
          }
          localObject = AbsListView.this;
          bool = false;
          localObject = ((AbsListView)localObject).getChildAt(0);
          if (localObject == null) {
            return;
          }
          i = ((View)localObject).getTop();
          if (j > 0) {
            k = Math.max(this.mExtraScroll, AbsListView.this.mListPadding.top);
          } else {
            k = AbsListView.this.mListPadding.top;
          }
          localObject = AbsListView.this;
          i1 = this.mScrollDuration;
          if (j > this.mTargetPos) {
            bool = true;
          }
          ((AbsListView)localObject).smoothScrollBy(i - k, i1, true, bool);
          this.mLastSeenPos = j;
          if (j > this.mTargetPos) {
            AbsListView.this.postOnAnimation(this);
          }
        }
      }
      else
      {
        k = AbsListView.this.getChildCount() - 1;
        j += k;
        if (k < 0) {
          return;
        }
        if (j == this.mLastSeenPos)
        {
          AbsListView.this.postOnAnimation(this);
          return;
        }
        localObject = AbsListView.this.getChildAt(k);
        i1 = ((View)localObject).getHeight();
        m = ((View)localObject).getTop();
        if (j < AbsListView.this.mItemCount - 1) {
          k = Math.max(AbsListView.this.mListPadding.bottom, this.mExtraScroll);
        } else {
          k = AbsListView.this.mListPadding.bottom;
        }
        localObject = AbsListView.this;
        n = this.mScrollDuration;
        if (j < this.mTargetPos) {
          bool = true;
        }
        ((AbsListView)localObject).smoothScrollBy(i1 - (i - m) + k, n, true, bool);
        this.mLastSeenPos = j;
        if (j < this.mTargetPos) {
          AbsListView.this.postOnAnimation(this);
        }
      }
    }
    
    public void start(final int paramInt)
    {
      stop();
      if (AbsListView.this.mDataChanged)
      {
        AbsListView.this.mPositionScrollAfterLayout = new Runnable()
        {
          public void run()
          {
            AbsListView.PositionScroller.this.start(paramInt);
          }
        };
        return;
      }
      int i = AbsListView.this.getChildCount();
      if (i == 0) {
        return;
      }
      int j = AbsListView.this.mFirstPosition;
      int k = j + i - 1;
      i = Math.max(0, Math.min(AbsListView.this.getCount() - 1, paramInt));
      if (i < j)
      {
        paramInt = j - i + 1;
        this.mMode = 2;
      }
      else
      {
        if (i <= k) {
          break label161;
        }
        paramInt = i - k + 1;
        this.mMode = 1;
      }
      if (paramInt > 0) {
        this.mScrollDuration = (200 / paramInt);
      } else {
        this.mScrollDuration = 200;
      }
      this.mTargetPos = i;
      this.mBoundPos = -1;
      this.mLastSeenPos = -1;
      AbsListView.this.postOnAnimation(this);
      return;
      label161:
      scrollToVisible(i, -1, 200);
    }
    
    public void start(final int paramInt1, final int paramInt2)
    {
      stop();
      if (paramInt2 == -1)
      {
        start(paramInt1);
        return;
      }
      if (AbsListView.this.mDataChanged)
      {
        AbsListView.this.mPositionScrollAfterLayout = new Runnable()
        {
          public void run()
          {
            AbsListView.PositionScroller.this.start(paramInt1, paramInt2);
          }
        };
        return;
      }
      int i = AbsListView.this.getChildCount();
      if (i == 0) {
        return;
      }
      int j = AbsListView.this.mFirstPosition;
      int k = j + i - 1;
      i = Math.max(0, Math.min(AbsListView.this.getCount() - 1, paramInt1));
      if (i < j)
      {
        k -= paramInt2;
        if (k < 1) {
          return;
        }
        paramInt1 = j - i + 1;
        j = k - 1;
        if (j < paramInt1)
        {
          paramInt1 = j;
          this.mMode = 4;
        }
        else
        {
          this.mMode = 2;
        }
      }
      else
      {
        if (i <= k) {
          break label246;
        }
        j = paramInt2 - j;
        if (j < 1) {
          return;
        }
        paramInt1 = i - k + 1;
        j--;
        if (j < paramInt1)
        {
          this.mMode = 3;
          paramInt1 = j;
        }
        else
        {
          this.mMode = 1;
        }
      }
      if (paramInt1 > 0) {
        this.mScrollDuration = (200 / paramInt1);
      } else {
        this.mScrollDuration = 200;
      }
      this.mTargetPos = i;
      this.mBoundPos = paramInt2;
      this.mLastSeenPos = -1;
      AbsListView.this.postOnAnimation(this);
      return;
      label246:
      scrollToVisible(i, paramInt2, 200);
    }
    
    public void startWithOffset(int paramInt1, int paramInt2)
    {
      startWithOffset(paramInt1, paramInt2, 200);
    }
    
    public void startWithOffset(final int paramInt1, final int paramInt2, final int paramInt3)
    {
      stop();
      if (AbsListView.this.mDataChanged)
      {
        AbsListView.this.mPositionScrollAfterLayout = new Runnable()
        {
          public void run()
          {
            AbsListView.PositionScroller.this.startWithOffset(paramInt1, paramInt2, paramInt3);
          }
        };
        return;
      }
      int i = AbsListView.this.getChildCount();
      if (i == 0) {
        return;
      }
      paramInt2 += AbsListView.this.getPaddingTop();
      this.mTargetPos = Math.max(0, Math.min(AbsListView.this.getCount() - 1, paramInt1));
      this.mOffsetFromTop = paramInt2;
      this.mBoundPos = -1;
      this.mLastSeenPos = -1;
      this.mMode = 5;
      int j = AbsListView.this.mFirstPosition;
      int k = j + i - 1;
      paramInt1 = this.mTargetPos;
      if (paramInt1 < j)
      {
        paramInt1 = j - paramInt1;
      }
      else
      {
        if (paramInt1 <= k) {
          break label191;
        }
        paramInt1 -= k;
      }
      float f = paramInt1 / i;
      if (f >= 1.0F) {
        paramInt3 = (int)(paramInt3 / f);
      }
      this.mScrollDuration = paramInt3;
      this.mLastSeenPos = -1;
      AbsListView.this.postOnAnimation(this);
      return;
      label191:
      paramInt1 = AbsListView.this.getChildAt(paramInt1 - j).getTop();
      AbsListView.this.smoothScrollBy(paramInt1 - paramInt2, paramInt3, true, false);
    }
    
    public void stop()
    {
      AbsListView.this.removeCallbacks(this);
    }
  }
  
  class RecycleBin
  {
    private View[] mActiveViews = new View[0];
    private ArrayList<View> mCurrentScrap;
    private int mFirstActivePosition;
    @UnsupportedAppUsage
    private AbsListView.RecyclerListener mRecyclerListener;
    private ArrayList<View>[] mScrapViews;
    private ArrayList<View> mSkippedScrap;
    private SparseArray<View> mTransientStateViews;
    private LongSparseArray<View> mTransientStateViewsById;
    private int mViewTypeCount;
    
    RecycleBin() {}
    
    private void clearScrap(ArrayList<View> paramArrayList)
    {
      int i = paramArrayList.size();
      for (int j = 0; j < i; j++) {
        removeDetachedView((View)paramArrayList.remove(i - 1 - j), false);
      }
    }
    
    private void clearScrapForRebind(View paramView)
    {
      paramView.clearAccessibilityFocus();
      paramView.setAccessibilityDelegate(null);
    }
    
    private ArrayList<View> getSkippedScrap()
    {
      if (this.mSkippedScrap == null) {
        this.mSkippedScrap = new ArrayList();
      }
      return this.mSkippedScrap;
    }
    
    private void pruneScrapViews()
    {
      int i = this.mActiveViews.length;
      int j = this.mViewTypeCount;
      Object localObject1 = this.mScrapViews;
      int m;
      for (int k = 0; k < j; k++)
      {
        localObject2 = localObject1[k];
        m = ((ArrayList)localObject2).size();
        while (m > i)
        {
          m--;
          ((ArrayList)localObject2).remove(m);
        }
      }
      localObject1 = this.mTransientStateViews;
      if (localObject1 != null) {
        for (k = 0; k < ((SparseArray)localObject1).size(); k = m + 1)
        {
          localObject2 = (View)((SparseArray)localObject1).valueAt(k);
          m = k;
          if (!((View)localObject2).hasTransientState())
          {
            removeDetachedView((View)localObject2, false);
            ((SparseArray)localObject1).removeAt(k);
            m = k - 1;
          }
        }
      }
      Object localObject2 = this.mTransientStateViewsById;
      if (localObject2 != null) {
        for (k = 0; k < ((LongSparseArray)localObject2).size(); k = m + 1)
        {
          localObject1 = (View)((LongSparseArray)localObject2).valueAt(k);
          m = k;
          if (!((View)localObject1).hasTransientState())
          {
            removeDetachedView((View)localObject1, false);
            ((LongSparseArray)localObject2).removeAt(k);
            m = k - 1;
          }
        }
      }
    }
    
    private void removeDetachedView(View paramView, boolean paramBoolean)
    {
      paramView.setAccessibilityDelegate(null);
      AbsListView.this.removeDetachedView(paramView, paramBoolean);
    }
    
    private View retrieveFromScrap(ArrayList<View> paramArrayList, int paramInt)
    {
      int i = paramArrayList.size();
      if (i > 0)
      {
        for (int j = i - 1; j >= 0; j--)
        {
          Object localObject = (View)paramArrayList.get(j);
          localObject = (AbsListView.LayoutParams)((View)localObject).getLayoutParams();
          if (AbsListView.this.mAdapterHasStableIds)
          {
            if (AbsListView.this.mAdapter.getItemId(paramInt) == ((AbsListView.LayoutParams)localObject).itemId) {
              return (View)paramArrayList.remove(j);
            }
          }
          else if (((AbsListView.LayoutParams)localObject).scrappedFromPosition == paramInt)
          {
            paramArrayList = (View)paramArrayList.remove(j);
            clearScrapForRebind(paramArrayList);
            return paramArrayList;
          }
        }
        paramArrayList = (View)paramArrayList.remove(i - 1);
        clearScrapForRebind(paramArrayList);
        return paramArrayList;
      }
      return null;
    }
    
    void addScrapView(View paramView, int paramInt)
    {
      Object localObject = (AbsListView.LayoutParams)paramView.getLayoutParams();
      if (localObject == null) {
        return;
      }
      ((AbsListView.LayoutParams)localObject).scrappedFromPosition = paramInt;
      int i = ((AbsListView.LayoutParams)localObject).viewType;
      if (!shouldRecycleViewType(i))
      {
        if (i != -2) {
          getSkippedScrap().add(paramView);
        }
        return;
      }
      paramView.dispatchStartTemporaryDetach();
      AbsListView.this.notifyViewAccessibilityStateChangedIfNeeded(1);
      if (paramView.hasTransientState())
      {
        if ((AbsListView.this.mAdapter != null) && (AbsListView.this.mAdapterHasStableIds))
        {
          if (this.mTransientStateViewsById == null) {
            this.mTransientStateViewsById = new LongSparseArray();
          }
          this.mTransientStateViewsById.put(((AbsListView.LayoutParams)localObject).itemId, paramView);
        }
        else if (!AbsListView.this.mDataChanged)
        {
          if (this.mTransientStateViews == null) {
            this.mTransientStateViews = new SparseArray();
          }
          this.mTransientStateViews.put(paramInt, paramView);
        }
        else
        {
          clearScrapForRebind(paramView);
          getSkippedScrap().add(paramView);
        }
      }
      else
      {
        clearScrapForRebind(paramView);
        if (this.mViewTypeCount == 1) {
          this.mCurrentScrap.add(paramView);
        } else {
          this.mScrapViews[i].add(paramView);
        }
        localObject = this.mRecyclerListener;
        if (localObject != null) {
          ((AbsListView.RecyclerListener)localObject).onMovedToScrapHeap(paramView);
        }
      }
    }
    
    @UnsupportedAppUsage
    void clear()
    {
      if (this.mViewTypeCount == 1)
      {
        clearScrap(this.mCurrentScrap);
      }
      else
      {
        int i = this.mViewTypeCount;
        for (int j = 0; j < i; j++) {
          clearScrap(this.mScrapViews[j]);
        }
      }
      clearTransientStateViews();
    }
    
    void clearTransientStateViews()
    {
      Object localObject = this.mTransientStateViews;
      int i;
      int j;
      if (localObject != null)
      {
        i = ((SparseArray)localObject).size();
        for (j = 0; j < i; j++) {
          removeDetachedView((View)((SparseArray)localObject).valueAt(j), false);
        }
        ((SparseArray)localObject).clear();
      }
      localObject = this.mTransientStateViewsById;
      if (localObject != null)
      {
        i = ((LongSparseArray)localObject).size();
        for (j = 0; j < i; j++) {
          removeDetachedView((View)((LongSparseArray)localObject).valueAt(j), false);
        }
        ((LongSparseArray)localObject).clear();
      }
    }
    
    void fillActiveViews(int paramInt1, int paramInt2)
    {
      if (this.mActiveViews.length < paramInt1) {
        this.mActiveViews = new View[paramInt1];
      }
      this.mFirstActivePosition = paramInt2;
      View[] arrayOfView = this.mActiveViews;
      for (int i = 0; i < paramInt1; i++)
      {
        View localView = AbsListView.this.getChildAt(i);
        AbsListView.LayoutParams localLayoutParams = (AbsListView.LayoutParams)localView.getLayoutParams();
        if ((localLayoutParams != null) && (localLayoutParams.viewType != -2))
        {
          arrayOfView[i] = localView;
          localLayoutParams.scrappedFromPosition = (paramInt2 + i);
        }
      }
    }
    
    void fullyDetachScrapViews()
    {
      int i = this.mViewTypeCount;
      ArrayList[] arrayOfArrayList = this.mScrapViews;
      for (int j = 0; j < i; j++)
      {
        ArrayList localArrayList = arrayOfArrayList[j];
        for (int k = localArrayList.size() - 1; k >= 0; k--)
        {
          View localView = (View)localArrayList.get(k);
          if (localView.isTemporarilyDetached()) {
            removeDetachedView(localView, false);
          }
        }
      }
    }
    
    View getActiveView(int paramInt)
    {
      paramInt -= this.mFirstActivePosition;
      View[] arrayOfView = this.mActiveViews;
      if ((paramInt >= 0) && (paramInt < arrayOfView.length))
      {
        View localView = arrayOfView[paramInt];
        arrayOfView[paramInt] = null;
        return localView;
      }
      return null;
    }
    
    View getScrapView(int paramInt)
    {
      int i = AbsListView.this.mAdapter.getItemViewType(paramInt);
      if (i < 0) {
        return null;
      }
      if (this.mViewTypeCount == 1) {
        return retrieveFromScrap(this.mCurrentScrap, paramInt);
      }
      ArrayList[] arrayOfArrayList = this.mScrapViews;
      if (i < arrayOfArrayList.length) {
        return retrieveFromScrap(arrayOfArrayList[i], paramInt);
      }
      return null;
    }
    
    View getTransientStateView(int paramInt)
    {
      if ((AbsListView.this.mAdapter != null) && (AbsListView.this.mAdapterHasStableIds) && (this.mTransientStateViewsById != null))
      {
        long l = AbsListView.this.mAdapter.getItemId(paramInt);
        localObject = (View)this.mTransientStateViewsById.get(l);
        this.mTransientStateViewsById.remove(l);
        return (View)localObject;
      }
      Object localObject = this.mTransientStateViews;
      if (localObject != null)
      {
        paramInt = ((SparseArray)localObject).indexOfKey(paramInt);
        if (paramInt >= 0)
        {
          localObject = (View)this.mTransientStateViews.valueAt(paramInt);
          this.mTransientStateViews.removeAt(paramInt);
          return (View)localObject;
        }
      }
      return null;
    }
    
    public void markChildrenDirty()
    {
      int i;
      int j;
      if (this.mViewTypeCount == 1)
      {
        localObject = this.mCurrentScrap;
        i = ((ArrayList)localObject).size();
        for (j = 0; j < i; j++) {
          ((View)((ArrayList)localObject).get(j)).forceLayout();
        }
      }
      else
      {
        int k = this.mViewTypeCount;
        for (j = 0; j < k; j++)
        {
          localObject = this.mScrapViews[j];
          int m = ((ArrayList)localObject).size();
          for (i = 0; i < m; i++) {
            ((View)((ArrayList)localObject).get(i)).forceLayout();
          }
        }
      }
      Object localObject = this.mTransientStateViews;
      if (localObject != null)
      {
        i = ((SparseArray)localObject).size();
        for (j = 0; j < i; j++) {
          ((View)this.mTransientStateViews.valueAt(j)).forceLayout();
        }
      }
      localObject = this.mTransientStateViewsById;
      if (localObject != null)
      {
        i = ((LongSparseArray)localObject).size();
        for (j = 0; j < i; j++) {
          ((View)this.mTransientStateViewsById.valueAt(j)).forceLayout();
        }
      }
    }
    
    void reclaimScrapViews(List<View> paramList)
    {
      if (this.mViewTypeCount == 1)
      {
        paramList.addAll(this.mCurrentScrap);
      }
      else
      {
        int i = this.mViewTypeCount;
        ArrayList[] arrayOfArrayList = this.mScrapViews;
        for (int j = 0; j < i; j++) {
          paramList.addAll(arrayOfArrayList[j]);
        }
      }
    }
    
    void removeSkippedScrap()
    {
      ArrayList localArrayList = this.mSkippedScrap;
      if (localArrayList == null) {
        return;
      }
      int i = localArrayList.size();
      for (int j = 0; j < i; j++) {
        removeDetachedView((View)this.mSkippedScrap.get(j), false);
      }
      this.mSkippedScrap.clear();
    }
    
    void scrapActiveViews()
    {
      View[] arrayOfView = this.mActiveViews;
      Object localObject1 = this.mRecyclerListener;
      int i = 1;
      int j;
      if (localObject1 != null) {
        j = 1;
      } else {
        j = 0;
      }
      if (this.mViewTypeCount <= 1) {
        i = 0;
      }
      localObject1 = this.mCurrentScrap;
      int k = arrayOfView.length - 1;
      while (k >= 0)
      {
        View localView = arrayOfView[k];
        Object localObject2 = localObject1;
        if (localView != null)
        {
          localObject2 = (AbsListView.LayoutParams)localView.getLayoutParams();
          int m = ((AbsListView.LayoutParams)localObject2).viewType;
          arrayOfView[k] = null;
          if (localView.hasTransientState())
          {
            localView.dispatchStartTemporaryDetach();
            if ((AbsListView.this.mAdapter != null) && (AbsListView.this.mAdapterHasStableIds))
            {
              if (this.mTransientStateViewsById == null) {
                this.mTransientStateViewsById = new LongSparseArray();
              }
              long l = AbsListView.this.mAdapter.getItemId(this.mFirstActivePosition + k);
              this.mTransientStateViewsById.put(l, localView);
              localObject2 = localObject1;
            }
            else if (!AbsListView.this.mDataChanged)
            {
              if (this.mTransientStateViews == null) {
                this.mTransientStateViews = new SparseArray();
              }
              this.mTransientStateViews.put(this.mFirstActivePosition + k, localView);
              localObject2 = localObject1;
            }
            else
            {
              localObject2 = localObject1;
              if (m != -2)
              {
                removeDetachedView(localView, false);
                localObject2 = localObject1;
              }
            }
          }
          else if (!shouldRecycleViewType(m))
          {
            localObject2 = localObject1;
            if (m != -2)
            {
              removeDetachedView(localView, false);
              localObject2 = localObject1;
            }
          }
          else
          {
            if (i != 0) {
              localObject1 = this.mScrapViews[m];
            }
            ((AbsListView.LayoutParams)localObject2).scrappedFromPosition = (this.mFirstActivePosition + k);
            removeDetachedView(localView, false);
            ((ArrayList)localObject1).add(localView);
            localObject2 = localObject1;
            if (j != 0)
            {
              this.mRecyclerListener.onMovedToScrapHeap(localView);
              localObject2 = localObject1;
            }
          }
        }
        k--;
        localObject1 = localObject2;
      }
      pruneScrapViews();
    }
    
    void setCacheColorHint(int paramInt)
    {
      ArrayList localArrayList;
      int i;
      int j;
      if (this.mViewTypeCount == 1)
      {
        localArrayList = this.mCurrentScrap;
        i = localArrayList.size();
        for (j = 0; j < i; j++) {
          ((View)localArrayList.get(j)).setDrawingCacheBackgroundColor(paramInt);
        }
      }
      else
      {
        int k = this.mViewTypeCount;
        for (j = 0; j < k; j++)
        {
          localArrayList = this.mScrapViews[j];
          int m = localArrayList.size();
          for (i = 0; i < m; i++) {
            ((View)localArrayList.get(i)).setDrawingCacheBackgroundColor(paramInt);
          }
        }
      }
      for (localArrayList : this.mActiveViews) {
        if (localArrayList != null) {
          localArrayList.setDrawingCacheBackgroundColor(paramInt);
        }
      }
    }
    
    public void setViewTypeCount(int paramInt)
    {
      if (paramInt >= 1)
      {
        ArrayList[] arrayOfArrayList = new ArrayList[paramInt];
        for (int i = 0; i < paramInt; i++) {
          arrayOfArrayList[i] = new ArrayList();
        }
        this.mViewTypeCount = paramInt;
        this.mCurrentScrap = arrayOfArrayList[0];
        this.mScrapViews = arrayOfArrayList;
        return;
      }
      throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
    }
    
    public boolean shouldRecycleViewType(int paramInt)
    {
      boolean bool;
      if (paramInt >= 0) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
  }
  
  public static abstract interface RecyclerListener
  {
    public abstract void onMovedToScrapHeap(View paramView);
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public AbsListView.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new AbsListView.SavedState(paramAnonymousParcel, null);
      }
      
      public AbsListView.SavedState[] newArray(int paramAnonymousInt)
      {
        return new AbsListView.SavedState[paramAnonymousInt];
      }
    };
    LongSparseArray<Integer> checkIdState;
    SparseBooleanArray checkState;
    int checkedItemCount;
    String filter;
    @UnsupportedAppUsage
    long firstId;
    int height;
    boolean inActionMode;
    int position;
    long selectedId;
    @UnsupportedAppUsage
    int viewTop;
    
    private SavedState(Parcel paramParcel)
    {
      super();
      this.selectedId = paramParcel.readLong();
      this.firstId = paramParcel.readLong();
      this.viewTop = paramParcel.readInt();
      this.position = paramParcel.readInt();
      this.height = paramParcel.readInt();
      this.filter = paramParcel.readString();
      boolean bool;
      if (paramParcel.readByte() != 0) {
        bool = true;
      } else {
        bool = false;
      }
      this.inActionMode = bool;
      this.checkedItemCount = paramParcel.readInt();
      this.checkState = paramParcel.readSparseBooleanArray();
      int i = paramParcel.readInt();
      if (i > 0)
      {
        this.checkIdState = new LongSparseArray();
        for (int j = 0; j < i; j++)
        {
          long l = paramParcel.readLong();
          int k = paramParcel.readInt();
          this.checkIdState.put(l, Integer.valueOf(k));
        }
      }
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("AbsListView.SavedState{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(" selectedId=");
      localStringBuilder.append(this.selectedId);
      localStringBuilder.append(" firstId=");
      localStringBuilder.append(this.firstId);
      localStringBuilder.append(" viewTop=");
      localStringBuilder.append(this.viewTop);
      localStringBuilder.append(" position=");
      localStringBuilder.append(this.position);
      localStringBuilder.append(" height=");
      localStringBuilder.append(this.height);
      localStringBuilder.append(" filter=");
      localStringBuilder.append(this.filter);
      localStringBuilder.append(" checkState=");
      localStringBuilder.append(this.checkState);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeLong(this.selectedId);
      paramParcel.writeLong(this.firstId);
      paramParcel.writeInt(this.viewTop);
      paramParcel.writeInt(this.position);
      paramParcel.writeInt(this.height);
      paramParcel.writeString(this.filter);
      paramParcel.writeByte((byte)this.inActionMode);
      paramParcel.writeInt(this.checkedItemCount);
      paramParcel.writeSparseBooleanArray(this.checkState);
      LongSparseArray localLongSparseArray = this.checkIdState;
      if (localLongSparseArray != null) {
        paramInt = localLongSparseArray.size();
      } else {
        paramInt = 0;
      }
      paramParcel.writeInt(paramInt);
      for (int i = 0; i < paramInt; i++)
      {
        paramParcel.writeLong(this.checkIdState.keyAt(i));
        paramParcel.writeInt(((Integer)this.checkIdState.valueAt(i)).intValue());
      }
    }
  }
  
  public static abstract interface SelectionBoundsAdjuster
  {
    public abstract void adjustListItemSelectionBounds(Rect paramRect);
  }
  
  private class WindowRunnnable
  {
    private int mOriginalAttachCount;
    
    private WindowRunnnable() {}
    
    public void rememberWindowAttachCount()
    {
      this.mOriginalAttachCount = AbsListView.this.getWindowAttachCount();
    }
    
    public boolean sameWindow()
    {
      boolean bool;
      if (AbsListView.this.getWindowAttachCount() == this.mOriginalAttachCount) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AbsListView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */