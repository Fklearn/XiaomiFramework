package android.view;

import android.animation.LayoutTransition;
import android.animation.LayoutTransition.TransitionListener;
import android.annotation.UnsupportedAppUsage;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.graphics.RenderNode;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MiuiMultiWindowAdapter;
import android.util.Pools.SimplePool;
import android.util.Pools.SynchronizedPool;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LayoutAnimationController.AnimationParameters;
import android.view.animation.Transformation;
import android.view.autofill.Helper;
import com.android.internal.R.styleable;
import com.android.internal.widget.DecorCaptionView;
import com.miui.internal.variable.api.Overridable;
import com.miui.internal.variable.api.v29.Android_View_ViewGroup.Extension;
import com.miui.internal.variable.api.v29.Android_View_ViewGroup.Interface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public abstract class ViewGroup
  extends View
  implements ViewParent, ViewManager
{
  private static final int ARRAY_CAPACITY_INCREMENT = 12;
  private static final int ARRAY_INITIAL_CAPACITY = 12;
  private static final int CHILD_LEFT_INDEX = 0;
  private static final int CHILD_TOP_INDEX = 1;
  protected static final int CLIP_TO_PADDING_MASK = 34;
  @UnsupportedAppUsage
  private static final boolean DBG = false;
  private static final int[] DESCENDANT_FOCUSABILITY_FLAGS = { 131072, 262144, 393216 };
  private static final int FLAG_ADD_STATES_FROM_CHILDREN = 8192;
  @Deprecated
  private static final int FLAG_ALWAYS_DRAWN_WITH_CACHE = 16384;
  @Deprecated
  private static final int FLAG_ANIMATION_CACHE = 64;
  static final int FLAG_ANIMATION_DONE = 16;
  @Deprecated
  private static final int FLAG_CHILDREN_DRAWN_WITH_CACHE = 32768;
  static final int FLAG_CLEAR_TRANSFORMATION = 256;
  static final int FLAG_CLIP_CHILDREN = 1;
  private static final int FLAG_CLIP_TO_PADDING = 2;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123983692L)
  protected static final int FLAG_DISALLOW_INTERCEPT = 524288;
  static final int FLAG_INVALIDATE_REQUIRED = 4;
  static final int FLAG_IS_TRANSITION_GROUP = 16777216;
  static final int FLAG_IS_TRANSITION_GROUP_SET = 33554432;
  private static final int FLAG_LAYOUT_MODE_WAS_EXPLICITLY_SET = 8388608;
  private static final int FLAG_MASK_FOCUSABILITY = 393216;
  private static final int FLAG_NOTIFY_ANIMATION_LISTENER = 512;
  private static final int FLAG_NOTIFY_CHILDREN_ON_DRAWABLE_STATE_CHANGE = 65536;
  static final int FLAG_OPTIMIZE_INVALIDATE = 128;
  private static final int FLAG_PADDING_NOT_NULL = 32;
  private static final int FLAG_PREVENT_DISPATCH_ATTACHED_TO_WINDOW = 4194304;
  private static final int FLAG_RUN_ANIMATION = 8;
  private static final int FLAG_SHOW_CONTEXT_MENU_WITH_COORDS = 536870912;
  private static final int FLAG_SPLIT_MOTION_EVENTS = 2097152;
  private static final int FLAG_START_ACTION_MODE_FOR_CHILD_IS_NOT_TYPED = 268435456;
  private static final int FLAG_START_ACTION_MODE_FOR_CHILD_IS_TYPED = 134217728;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769647L)
  protected static final int FLAG_SUPPORT_STATIC_TRANSFORMATIONS = 2048;
  static final int FLAG_TOUCHSCREEN_BLOCKS_FOCUS = 67108864;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769377L)
  protected static final int FLAG_USE_CHILD_DRAWING_ORDER = 1024;
  public static final int FOCUS_AFTER_DESCENDANTS = 262144;
  public static final int FOCUS_BEFORE_DESCENDANTS = 131072;
  public static final int FOCUS_BLOCK_DESCENDANTS = 393216;
  public static final int LAYOUT_MODE_CLIP_BOUNDS = 0;
  public static int LAYOUT_MODE_DEFAULT = 0;
  public static final int LAYOUT_MODE_OPTICAL_BOUNDS = 1;
  private static final int LAYOUT_MODE_UNDEFINED = -1;
  @Deprecated
  public static final int PERSISTENT_ALL_CACHES = 3;
  @Deprecated
  public static final int PERSISTENT_ANIMATION_CACHE = 1;
  @Deprecated
  public static final int PERSISTENT_NO_CACHE = 0;
  @Deprecated
  public static final int PERSISTENT_SCROLLING_CACHE = 2;
  private static final ActionMode SENTINEL_ACTION_MODE = new ActionMode()
  {
    public void finish() {}
    
    public View getCustomView()
    {
      return null;
    }
    
    public Menu getMenu()
    {
      return null;
    }
    
    public MenuInflater getMenuInflater()
    {
      return null;
    }
    
    public CharSequence getSubtitle()
    {
      return null;
    }
    
    public CharSequence getTitle()
    {
      return null;
    }
    
    public void invalidate() {}
    
    public void setCustomView(View paramAnonymousView) {}
    
    public void setSubtitle(int paramAnonymousInt) {}
    
    public void setSubtitle(CharSequence paramAnonymousCharSequence) {}
    
    public void setTitle(int paramAnonymousInt) {}
    
    public void setTitle(CharSequence paramAnonymousCharSequence) {}
  };
  private static final String TAG = "ViewGroup";
  private static float[] sDebugLines;
  private Animation.AnimationListener mAnimationListener;
  Paint mCachePaint;
  @ViewDebug.ExportedProperty(category="layout")
  private int mChildCountWithTransientState = 0;
  private Transformation mChildTransformation;
  int mChildUnhandledKeyListeners = 0;
  @UnsupportedAppUsage(maxTargetSdk=28)
  private View[] mChildren;
  @UnsupportedAppUsage(maxTargetSdk=28)
  private int mChildrenCount;
  private HashSet<View> mChildrenInterestedInDrag;
  private View mCurrentDragChild;
  private DragEvent mCurrentDragStartEvent;
  private View mDefaultFocus;
  @UnsupportedAppUsage
  protected ArrayList<View> mDisappearingChildren;
  private HoverTarget mFirstHoverTarget;
  @UnsupportedAppUsage
  private TouchTarget mFirstTouchTarget;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private View mFocused;
  View mFocusedInCluster;
  @ViewDebug.ExportedProperty(flagMapping={@ViewDebug.FlagToString(equals=1, mask=1, name="CLIP_CHILDREN"), @ViewDebug.FlagToString(equals=2, mask=2, name="CLIP_TO_PADDING"), @ViewDebug.FlagToString(equals=32, mask=32, name="PADDING_NOT_NULL")}, formatToHexString=true)
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769411L)
  protected int mGroupFlags;
  private boolean mHoveredSelf;
  RectF mInvalidateRegion;
  Transformation mInvalidationTransformation;
  private boolean mIsInterestedInDrag;
  @ViewDebug.ExportedProperty(category="events")
  private int mLastTouchDownIndex = -1;
  @ViewDebug.ExportedProperty(category="events")
  private long mLastTouchDownTime;
  @ViewDebug.ExportedProperty(category="events")
  private float mLastTouchDownX;
  @ViewDebug.ExportedProperty(category="events")
  private float mLastTouchDownY;
  private LayoutAnimationController mLayoutAnimationController;
  private boolean mLayoutCalledWhileSuppressed = false;
  private int mLayoutMode = -1;
  private LayoutTransition.TransitionListener mLayoutTransitionListener = new LayoutTransition.TransitionListener()
  {
    public void endTransition(LayoutTransition paramAnonymousLayoutTransition, ViewGroup paramAnonymousViewGroup, View paramAnonymousView, int paramAnonymousInt)
    {
      if ((ViewGroup.this.mLayoutCalledWhileSuppressed) && (!paramAnonymousLayoutTransition.isChangingLayout()))
      {
        ViewGroup.this.requestLayout();
        ViewGroup.access$302(ViewGroup.this, false);
      }
      if ((paramAnonymousInt == 3) && (ViewGroup.this.mTransitioningViews != null)) {
        ViewGroup.this.endViewTransition(paramAnonymousView);
      }
    }
    
    public void startTransition(LayoutTransition paramAnonymousLayoutTransition, ViewGroup paramAnonymousViewGroup, View paramAnonymousView, int paramAnonymousInt)
    {
      if (paramAnonymousInt == 3) {
        ViewGroup.this.startViewTransition(paramAnonymousView);
      }
    }
  };
  private PointF mLocalPoint;
  private int mNestedScrollAxes;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123768704L)
  protected OnHierarchyChangeListener mOnHierarchyChangeListener;
  @UnsupportedAppUsage
  protected int mPersistentDrawingCache;
  private ArrayList<View> mPreSortedChildren;
  boolean mSuppressLayout = false;
  private float[] mTempPoint;
  private View mTooltipHoverTarget;
  private boolean mTooltipHoveredSelf;
  private List<Integer> mTransientIndices = null;
  private List<View> mTransientViews = null;
  private LayoutTransition mTransition;
  private ArrayList<View> mTransitioningViews;
  private ArrayList<View> mVisibilityChangingChildren;
  
  static
  {
    Android_View_ViewGroup.Extension.get().bindOriginal(new Impl(null));
  }
  
  public ViewGroup(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ViewGroup(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ViewGroup(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ViewGroup(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    initViewGroup();
    initFromAttributes(paramContext, paramAttributeSet, paramInt1, paramInt2);
    if (Android_View_ViewGroup.Extension.get().getExtension() != null) {
      ((Android_View_ViewGroup.Interface)Android_View_ViewGroup.Extension.get().getExtension().asInterface()).init(this, paramContext, paramAttributeSet, paramInt1, paramInt2);
    }
  }
  
  private void addDisappearingView(View paramView)
  {
    ArrayList localArrayList1 = this.mDisappearingChildren;
    ArrayList localArrayList2 = localArrayList1;
    if (localArrayList1 == null)
    {
      localArrayList2 = new ArrayList();
      this.mDisappearingChildren = localArrayList2;
    }
    localArrayList2.add(paramView);
  }
  
  private void addInArray(View paramView, int paramInt)
  {
    if (Android_View_ViewGroup.Extension.get().getExtension() != null) {
      ((Android_View_ViewGroup.Interface)Android_View_ViewGroup.Extension.get().getExtension().asInterface()).addInArray(this, paramView, paramInt);
    } else {
      originalAddInArray(paramView, paramInt);
    }
  }
  
  private TouchTarget addTouchTarget(View paramView, int paramInt)
  {
    paramView = TouchTarget.obtain(paramView, paramInt);
    paramView.next = this.mFirstTouchTarget;
    this.mFirstTouchTarget = paramView;
    return paramView;
  }
  
  private void addViewInner(View paramView, int paramInt, LayoutParams paramLayoutParams, boolean paramBoolean)
  {
    Object localObject = this.mTransition;
    if (localObject != null) {
      ((LayoutTransition)localObject).cancel(3);
    }
    if (paramView.getParent() == null)
    {
      localObject = this.mTransition;
      if (localObject != null) {
        ((LayoutTransition)localObject).addChild(this, paramView);
      }
      localObject = paramLayoutParams;
      if (!checkLayoutParams(paramLayoutParams)) {
        localObject = generateLayoutParams(paramLayoutParams);
      }
      if (paramBoolean) {
        paramView.mLayoutParams = ((LayoutParams)localObject);
      } else {
        paramView.setLayoutParams((LayoutParams)localObject);
      }
      int i = paramInt;
      if (paramInt < 0) {
        i = this.mChildrenCount;
      }
      addInArray(paramView, i);
      if (paramBoolean) {
        paramView.assignParent(this);
      } else {
        paramView.mParent = this;
      }
      if (paramView.hasUnhandledKeyListener()) {
        incrementChildUnhandledKeyListeners();
      }
      if (paramView.hasFocus()) {
        requestChildFocus(paramView, paramView.findFocus());
      }
      paramLayoutParams = this.mAttachInfo;
      if ((paramLayoutParams != null) && ((this.mGroupFlags & 0x400000) == 0))
      {
        paramBoolean = paramLayoutParams.mKeepScreenOn;
        paramLayoutParams.mKeepScreenOn = false;
        paramView.dispatchAttachedToWindow(this.mAttachInfo, this.mViewFlags & 0xC);
        if (paramLayoutParams.mKeepScreenOn) {
          needGlobalAttributesUpdate(true);
        }
        paramLayoutParams.mKeepScreenOn = paramBoolean;
      }
      if (paramView.isLayoutDirectionInherited()) {
        paramView.resetRtlProperties();
      }
      dispatchViewAdded(paramView);
      if ((paramView.mViewFlags & 0x400000) == 4194304) {
        this.mGroupFlags |= 0x10000;
      }
      if (paramView.hasTransientState()) {
        childHasTransientStateChanged(paramView, true);
      }
      if (paramView.getVisibility() != 8) {
        notifySubtreeAccessibilityStateChangedIfNeeded();
      }
      paramLayoutParams = this.mTransientIndices;
      if (paramLayoutParams != null)
      {
        int j = paramLayoutParams.size();
        for (paramInt = 0; paramInt < j; paramInt++)
        {
          int k = ((Integer)this.mTransientIndices.get(paramInt)).intValue();
          if (i <= k) {
            this.mTransientIndices.set(paramInt, Integer.valueOf(k + 1));
          }
        }
      }
      if ((this.mCurrentDragStartEvent != null) && (paramView.getVisibility() == 0)) {
        notifyChildOfDragStart(paramView);
      }
      if (paramView.hasDefaultFocus()) {
        setDefaultFocus(paramView);
      }
      touchAccessibilityNodeProviderIfNeeded(paramView);
      return;
    }
    throw new IllegalStateException("The specified child already has a parent. You must call removeView() on the child's parent first.");
  }
  
  private static void applyOpToRegionByBounds(Region paramRegion, View paramView, Region.Op paramOp)
  {
    int[] arrayOfInt = new int[2];
    paramView.getLocationInWindow(arrayOfInt);
    int i = arrayOfInt[0];
    int j = arrayOfInt[1];
    paramRegion.op(i, j, i + paramView.getWidth(), j + paramView.getHeight(), paramOp);
  }
  
  private void bindLayoutAnimation(View paramView)
  {
    paramView.setAnimation(this.mLayoutAnimationController.getAnimationForView(paramView));
  }
  
  private WindowInsets brokenDispatchApplyWindowInsets(WindowInsets paramWindowInsets)
  {
    WindowInsets localWindowInsets = paramWindowInsets;
    if (!paramWindowInsets.isConsumed())
    {
      int i = getChildCount();
      for (int j = 0;; j++)
      {
        localWindowInsets = paramWindowInsets;
        if (j >= i) {
          break;
        }
        paramWindowInsets = getChildAt(j).dispatchApplyWindowInsets(paramWindowInsets);
        if (paramWindowInsets.isConsumed())
        {
          localWindowInsets = paramWindowInsets;
          break;
        }
      }
    }
    return localWindowInsets;
  }
  
  private void cancelAndClearTouchTargets(MotionEvent paramMotionEvent)
  {
    if (this.mFirstTouchTarget != null)
    {
      int i = 0;
      MotionEvent localMotionEvent = paramMotionEvent;
      if (paramMotionEvent == null)
      {
        long l = SystemClock.uptimeMillis();
        localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
        localMotionEvent.setSource(4098);
        i = 1;
      }
      for (paramMotionEvent = this.mFirstTouchTarget; paramMotionEvent != null; paramMotionEvent = paramMotionEvent.next)
      {
        resetCancelNextUpFlag(paramMotionEvent.child);
        dispatchTransformedTouchEvent(localMotionEvent, true, paramMotionEvent.child, paramMotionEvent.pointerIdBits);
      }
      clearTouchTargets();
      if (i != 0) {
        localMotionEvent.recycle();
      }
    }
  }
  
  private void cancelHoverTarget(View paramView)
  {
    Object localObject1 = null;
    HoverTarget localHoverTarget;
    for (Object localObject2 = this.mFirstHoverTarget; localObject2 != null; localObject2 = localHoverTarget)
    {
      localHoverTarget = ((HoverTarget)localObject2).next;
      if (((HoverTarget)localObject2).child == paramView)
      {
        if (localObject1 == null) {
          this.mFirstHoverTarget = localHoverTarget;
        } else {
          ((HoverTarget)localObject1).next = localHoverTarget;
        }
        ((HoverTarget)localObject2).recycle();
        long l = SystemClock.uptimeMillis();
        localObject2 = MotionEvent.obtain(l, l, 10, 0.0F, 0.0F, 0);
        ((MotionEvent)localObject2).setSource(4098);
        paramView.dispatchHoverEvent((MotionEvent)localObject2);
        ((MotionEvent)localObject2).recycle();
        return;
      }
      localObject1 = localObject2;
    }
  }
  
  @UnsupportedAppUsage
  private void cancelTouchTarget(View paramView)
  {
    Object localObject1 = null;
    TouchTarget localTouchTarget;
    for (Object localObject2 = this.mFirstTouchTarget; localObject2 != null; localObject2 = localTouchTarget)
    {
      localTouchTarget = ((TouchTarget)localObject2).next;
      if (((TouchTarget)localObject2).child == paramView)
      {
        if (localObject1 == null) {
          this.mFirstTouchTarget = localTouchTarget;
        } else {
          ((TouchTarget)localObject1).next = localTouchTarget;
        }
        ((TouchTarget)localObject2).recycle();
        long l = SystemClock.uptimeMillis();
        localObject2 = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
        ((MotionEvent)localObject2).setSource(4098);
        paramView.dispatchTouchEvent((MotionEvent)localObject2);
        ((MotionEvent)localObject2).recycle();
        return;
      }
      localObject1 = localObject2;
    }
  }
  
  private void clearCachedLayoutMode()
  {
    if (!hasBooleanFlag(8388608)) {
      this.mLayoutMode = -1;
    }
  }
  
  private void clearTouchTargets()
  {
    Object localObject = this.mFirstTouchTarget;
    if (localObject != null)
    {
      TouchTarget localTouchTarget;
      do
      {
        localTouchTarget = ((TouchTarget)localObject).next;
        ((TouchTarget)localObject).recycle();
        localObject = localTouchTarget;
      } while (localTouchTarget != null);
      this.mFirstTouchTarget = null;
    }
  }
  
  private PointerIcon dispatchResolvePointerIcon(MotionEvent paramMotionEvent, int paramInt, View paramView)
  {
    if (!paramView.hasIdentityMatrix())
    {
      MotionEvent localMotionEvent = getTransformedMotionEvent(paramMotionEvent, paramView);
      paramMotionEvent = paramView.onResolvePointerIcon(localMotionEvent, paramInt);
      localMotionEvent.recycle();
    }
    else
    {
      float f1 = this.mScrollX - paramView.mLeft;
      float f2 = this.mScrollY - paramView.mTop;
      paramMotionEvent.offsetLocation(f1, f2);
      paramView = paramView.onResolvePointerIcon(paramMotionEvent, paramInt);
      paramMotionEvent.offsetLocation(-f1, -f2);
      paramMotionEvent = paramView;
    }
    return paramMotionEvent;
  }
  
  private boolean dispatchTooltipHoverEvent(MotionEvent paramMotionEvent, View paramView)
  {
    boolean bool;
    if (!paramView.hasIdentityMatrix())
    {
      paramMotionEvent = getTransformedMotionEvent(paramMotionEvent, paramView);
      bool = paramView.dispatchTooltipHoverEvent(paramMotionEvent);
      paramMotionEvent.recycle();
    }
    else
    {
      float f1 = this.mScrollX - paramView.mLeft;
      float f2 = this.mScrollY - paramView.mTop;
      paramMotionEvent.offsetLocation(f1, f2);
      bool = paramView.dispatchTooltipHoverEvent(paramMotionEvent);
      paramMotionEvent.offsetLocation(-f1, -f2);
    }
    return bool;
  }
  
  private boolean dispatchTransformedGenericPointerEvent(MotionEvent paramMotionEvent, View paramView)
  {
    boolean bool;
    if (!paramView.hasIdentityMatrix())
    {
      paramMotionEvent = getTransformedMotionEvent(paramMotionEvent, paramView);
      bool = paramView.dispatchGenericMotionEvent(paramMotionEvent);
      paramMotionEvent.recycle();
    }
    else
    {
      float f1 = this.mScrollX - paramView.mLeft;
      float f2 = this.mScrollY - paramView.mTop;
      paramMotionEvent.offsetLocation(f1, f2);
      bool = paramView.dispatchGenericMotionEvent(paramMotionEvent);
      paramMotionEvent.offsetLocation(-f1, -f2);
    }
    return bool;
  }
  
  private boolean dispatchTransformedTouchEvent(MotionEvent paramMotionEvent, boolean paramBoolean, View paramView, int paramInt)
  {
    int i = paramMotionEvent.getAction();
    if ((!paramBoolean) && (i != 3))
    {
      i = paramMotionEvent.getPointerIdBits();
      paramInt = i & paramInt;
      if (paramInt == 0) {
        return false;
      }
      if (paramInt == i)
      {
        if ((paramView != null) && (!paramView.hasIdentityMatrix()))
        {
          paramMotionEvent = MotionEvent.obtain(paramMotionEvent);
        }
        else
        {
          if (paramView == null)
          {
            paramBoolean = super.dispatchTouchEvent(paramMotionEvent);
          }
          else
          {
            float f1 = this.mScrollX - paramView.mLeft;
            float f2 = this.mScrollY - paramView.mTop;
            paramMotionEvent.offsetLocation(f1, f2);
            paramBoolean = paramView.dispatchTouchEvent(paramMotionEvent);
            paramMotionEvent.offsetLocation(-f1, -f2);
          }
          return paramBoolean;
        }
      }
      else {
        paramMotionEvent = paramMotionEvent.split(paramInt);
      }
      if (paramView == null)
      {
        paramBoolean = super.dispatchTouchEvent(paramMotionEvent);
      }
      else
      {
        paramMotionEvent.offsetLocation(this.mScrollX - paramView.mLeft, this.mScrollY - paramView.mTop);
        if (!paramView.hasIdentityMatrix()) {
          paramMotionEvent.transform(paramView.getInverseMatrix());
        }
        paramBoolean = paramView.dispatchTouchEvent(paramMotionEvent);
      }
      paramMotionEvent.recycle();
      return paramBoolean;
    }
    paramMotionEvent.setAction(3);
    if (paramView == null) {
      paramBoolean = super.dispatchTouchEvent(paramMotionEvent);
    } else {
      paramBoolean = paramView.dispatchTouchEvent(paramMotionEvent);
    }
    paramMotionEvent.setAction(i);
    return paramBoolean;
  }
  
  private static void drawCorner(Canvas paramCanvas, Paint paramPaint, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    fillRect(paramCanvas, paramPaint, paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + sign(paramInt4) * paramInt5);
    fillRect(paramCanvas, paramPaint, paramInt1, paramInt2, paramInt1 + sign(paramInt3) * paramInt5, paramInt2 + paramInt4);
  }
  
  private static void drawRect(Canvas paramCanvas, Paint paramPaint, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (sDebugLines == null) {
      sDebugLines = new float[16];
    }
    float[] arrayOfFloat = sDebugLines;
    arrayOfFloat[0] = paramInt1;
    arrayOfFloat[1] = paramInt2;
    arrayOfFloat[2] = paramInt3;
    arrayOfFloat[3] = paramInt2;
    arrayOfFloat[4] = paramInt3;
    arrayOfFloat[5] = paramInt2;
    arrayOfFloat[6] = paramInt3;
    arrayOfFloat[7] = paramInt4;
    arrayOfFloat[8] = paramInt3;
    arrayOfFloat[9] = paramInt4;
    arrayOfFloat[10] = paramInt1;
    arrayOfFloat[11] = paramInt4;
    arrayOfFloat[12] = paramInt1;
    arrayOfFloat[13] = paramInt4;
    arrayOfFloat[14] = paramInt1;
    arrayOfFloat[15] = paramInt2;
    paramCanvas.drawLines(arrayOfFloat, paramPaint);
  }
  
  private static void drawRectCorners(Canvas paramCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Paint paramPaint, int paramInt5, int paramInt6)
  {
    drawCorner(paramCanvas, paramPaint, paramInt1, paramInt2, paramInt5, paramInt5, paramInt6);
    drawCorner(paramCanvas, paramPaint, paramInt1, paramInt4, paramInt5, -paramInt5, paramInt6);
    drawCorner(paramCanvas, paramPaint, paramInt3, paramInt2, -paramInt5, paramInt5, paramInt6);
    drawCorner(paramCanvas, paramPaint, paramInt3, paramInt4, -paramInt5, -paramInt5, paramInt6);
  }
  
  private void exitHoverTargets()
  {
    if ((this.mHoveredSelf) || (this.mFirstHoverTarget != null))
    {
      long l = SystemClock.uptimeMillis();
      MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 10, 0.0F, 0.0F, 0);
      localMotionEvent.setSource(4098);
      dispatchHoverEvent(localMotionEvent);
      localMotionEvent.recycle();
    }
  }
  
  private void exitTooltipHoverTargets()
  {
    if ((this.mTooltipHoveredSelf) || (this.mTooltipHoverTarget != null))
    {
      long l = SystemClock.uptimeMillis();
      MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 10, 0.0F, 0.0F, 0);
      localMotionEvent.setSource(4098);
      dispatchTooltipHoverEvent(localMotionEvent);
      localMotionEvent.recycle();
    }
  }
  
  private static void fillDifference(Canvas paramCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Paint paramPaint)
  {
    paramInt5 = paramInt1 - paramInt5;
    paramInt7 = paramInt3 + paramInt7;
    fillRect(paramCanvas, paramPaint, paramInt5, paramInt2 - paramInt6, paramInt7, paramInt2);
    fillRect(paramCanvas, paramPaint, paramInt5, paramInt2, paramInt1, paramInt4);
    fillRect(paramCanvas, paramPaint, paramInt3, paramInt2, paramInt7, paramInt4);
    fillRect(paramCanvas, paramPaint, paramInt5, paramInt4, paramInt7, paramInt4 + paramInt8);
  }
  
  private static void fillRect(Canvas paramCanvas, Paint paramPaint, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt1 != paramInt3) && (paramInt2 != paramInt4))
    {
      int i = paramInt1;
      int j = paramInt3;
      if (paramInt1 > paramInt3)
      {
        j = paramInt1;
        i = paramInt3;
      }
      paramInt3 = paramInt2;
      paramInt1 = paramInt4;
      if (paramInt2 > paramInt4)
      {
        paramInt3 = paramInt4;
        paramInt1 = paramInt2;
      }
      paramCanvas.drawRect(i, paramInt3, j, paramInt1, paramPaint);
    }
  }
  
  private View findChildWithAccessibilityFocus()
  {
    Object localObject = getViewRootImpl();
    if (localObject == null) {
      return null;
    }
    View localView = ((ViewRootImpl)localObject).getAccessibilityFocusedHost();
    if (localView == null) {
      return null;
    }
    for (localObject = localView.getParent(); (localObject instanceof View); localObject = localView.getParent())
    {
      if (localObject == this) {
        return localView;
      }
      localView = (View)localObject;
    }
    return null;
  }
  
  private int getAndVerifyPreorderedIndex(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramInt2 = getChildDrawingOrder(paramInt1, paramInt2);
      if (paramInt2 < paramInt1)
      {
        paramInt1 = paramInt2;
      }
      else
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("getChildDrawingOrder() returned invalid index ");
        localStringBuilder.append(paramInt2);
        localStringBuilder.append(" (child count is ");
        localStringBuilder.append(paramInt1);
        localStringBuilder.append(")");
        throw new IndexOutOfBoundsException(localStringBuilder.toString());
      }
    }
    else
    {
      paramInt1 = paramInt2;
    }
    return paramInt1;
  }
  
  private static View getAndVerifyPreorderedView(ArrayList<View> paramArrayList, View[] paramArrayOfView, int paramInt)
  {
    if (paramArrayList != null)
    {
      paramArrayList = (View)paramArrayList.get(paramInt);
      if (paramArrayList == null)
      {
        paramArrayList = new StringBuilder();
        paramArrayList.append("Invalid preorderedList contained null child at index ");
        paramArrayList.append(paramInt);
        throw new RuntimeException(paramArrayList.toString());
      }
    }
    else
    {
      paramArrayList = paramArrayOfView[paramInt];
    }
    return paramArrayList;
  }
  
  public static int getChildMeasureSpec(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = View.MeasureSpec.getMode(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt1);
    int k = 0;
    paramInt1 = 0;
    j = Math.max(0, j - paramInt2);
    int m = 0;
    paramInt2 = 0;
    if (i != Integer.MIN_VALUE)
    {
      if (i != 0)
      {
        if (i != 1073741824)
        {
          paramInt1 = m;
        }
        else if (paramInt3 >= 0)
        {
          paramInt1 = paramInt3;
          paramInt2 = 1073741824;
        }
        else if (paramInt3 == -1)
        {
          paramInt1 = j;
          paramInt2 = 1073741824;
        }
        else
        {
          paramInt1 = m;
          if (paramInt3 == -2)
          {
            paramInt1 = j;
            paramInt2 = Integer.MIN_VALUE;
          }
        }
      }
      else if (paramInt3 >= 0)
      {
        paramInt1 = paramInt3;
        paramInt2 = 1073741824;
      }
      else if (paramInt3 == -1)
      {
        if (!View.sUseZeroUnspecifiedMeasureSpec) {
          paramInt1 = j;
        }
        paramInt2 = 0;
      }
      else
      {
        paramInt1 = m;
        if (paramInt3 == -2)
        {
          if (View.sUseZeroUnspecifiedMeasureSpec) {
            paramInt1 = k;
          } else {
            paramInt1 = j;
          }
          paramInt2 = 0;
        }
      }
    }
    else if (paramInt3 >= 0)
    {
      paramInt1 = paramInt3;
      paramInt2 = 1073741824;
    }
    else if (paramInt3 == -1)
    {
      paramInt1 = j;
      paramInt2 = Integer.MIN_VALUE;
    }
    else
    {
      paramInt1 = m;
      if (paramInt3 == -2)
      {
        paramInt1 = j;
        paramInt2 = Integer.MIN_VALUE;
      }
    }
    return View.MeasureSpec.makeMeasureSpec(paramInt1, paramInt2);
  }
  
  private ChildListForAutofill getChildrenForAutofill(int paramInt)
  {
    ChildListForAutofill localChildListForAutofill = ChildListForAutofill.obtain();
    populateChildrenForAutofill(localChildListForAutofill, paramInt);
    return localChildListForAutofill;
  }
  
  private PointF getLocalPoint()
  {
    if (this.mLocalPoint == null) {
      this.mLocalPoint = new PointF();
    }
    return this.mLocalPoint;
  }
  
  private float[] getTempPoint()
  {
    if (this.mTempPoint == null) {
      this.mTempPoint = new float[2];
    }
    return this.mTempPoint;
  }
  
  private TouchTarget getTouchTarget(View paramView)
  {
    for (TouchTarget localTouchTarget = this.mFirstTouchTarget; localTouchTarget != null; localTouchTarget = localTouchTarget.next) {
      if (localTouchTarget.child == paramView) {
        return localTouchTarget;
      }
    }
    return null;
  }
  
  private MotionEvent getTransformedMotionEvent(MotionEvent paramMotionEvent, View paramView)
  {
    float f1 = this.mScrollX - paramView.mLeft;
    float f2 = this.mScrollY - paramView.mTop;
    paramMotionEvent = MotionEvent.obtain(paramMotionEvent);
    paramMotionEvent.offsetLocation(f1, f2);
    if (!paramView.hasIdentityMatrix()) {
      paramMotionEvent.transform(paramView.getInverseMatrix());
    }
    return paramMotionEvent;
  }
  
  private boolean hasBooleanFlag(int paramInt)
  {
    boolean bool;
    if ((this.mGroupFlags & paramInt) == paramInt) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean hasChildWithZ()
  {
    for (int i = 0; i < this.mChildrenCount; i++) {
      if (this.mChildren[i].getZ() != 0.0F) {
        return true;
      }
    }
    return false;
  }
  
  private void initFromAttributes(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ViewGroup, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.ViewGroup, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    paramInt2 = localTypedArray.getIndexCount();
    for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
    {
      int i = localTypedArray.getIndex(paramInt1);
      switch (i)
      {
      default: 
        break;
      case 12: 
        setTouchscreenBlocksFocus(localTypedArray.getBoolean(i, false));
        break;
      case 11: 
        setTransitionGroup(localTypedArray.getBoolean(i, false));
        break;
      case 10: 
        setLayoutMode(localTypedArray.getInt(i, -1));
        break;
      case 9: 
        if (localTypedArray.getBoolean(i, false)) {
          setLayoutTransition(new LayoutTransition());
        }
        break;
      case 8: 
        setMotionEventSplittingEnabled(localTypedArray.getBoolean(i, false));
        break;
      case 7: 
        setDescendantFocusability(DESCENDANT_FOCUSABILITY_FLAGS[localTypedArray.getInt(i, 0)]);
        break;
      case 6: 
        setAddStatesFromChildren(localTypedArray.getBoolean(i, false));
        break;
      case 5: 
        setAlwaysDrawnWithCacheEnabled(localTypedArray.getBoolean(i, true));
        break;
      case 4: 
        setPersistentDrawingCache(localTypedArray.getInt(i, 2));
        break;
      case 3: 
        setAnimationCacheEnabled(localTypedArray.getBoolean(i, true));
        break;
      case 2: 
        i = localTypedArray.getResourceId(i, -1);
        if (i > 0) {
          setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this.mContext, i));
        }
        break;
      case 1: 
        setClipToPadding(localTypedArray.getBoolean(i, true));
        break;
      case 0: 
        setClipChildren(localTypedArray.getBoolean(i, true));
      }
    }
    localTypedArray.recycle();
  }
  
  private void initViewGroup()
  {
    if (!debugDraw()) {
      setFlags(128, 128);
    }
    this.mGroupFlags |= 0x1;
    this.mGroupFlags |= 0x2;
    this.mGroupFlags |= 0x10;
    this.mGroupFlags |= 0x40;
    this.mGroupFlags |= 0x4000;
    if (this.mContext.getApplicationInfo().targetSdkVersion >= 11) {
      this.mGroupFlags |= 0x200000;
    }
    setDescendantFocusability(131072);
    this.mChildren = new View[12];
    this.mChildrenCount = 0;
    this.mPersistentDrawingCache = 2;
  }
  
  private WindowInsets newDispatchApplyWindowInsets(WindowInsets paramWindowInsets)
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++) {
      getChildAt(j).dispatchApplyWindowInsets(paramWindowInsets);
    }
    return paramWindowInsets;
  }
  
  private void notifyAnimationListener()
  {
    this.mGroupFlags &= 0xFDFF;
    this.mGroupFlags |= 0x10;
    if (this.mAnimationListener != null) {
      post(new Runnable()
      {
        public void run()
        {
          ViewGroup.this.mAnimationListener.onAnimationEnd(ViewGroup.this.mLayoutAnimationController.getAnimation());
        }
      });
    }
    invalidate(true);
  }
  
  private static MotionEvent obtainMotionEventNoHistoryOrSelf(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getHistorySize() == 0) {
      return paramMotionEvent;
    }
    return MotionEvent.obtainNoHistory(paramMotionEvent);
  }
  
  private void populateChildrenForAutofill(ArrayList<View> paramArrayList, int paramInt)
  {
    int i = this.mChildrenCount;
    if (i <= 0) {
      return;
    }
    ArrayList localArrayList = buildOrderedChildList();
    boolean bool;
    if ((localArrayList == null) && (isChildrenDrawingOrderEnabled())) {
      bool = true;
    } else {
      bool = false;
    }
    for (int j = 0; j < i; j++)
    {
      int k = getAndVerifyPreorderedIndex(i, j, bool);
      View localView;
      if (localArrayList == null) {
        localView = this.mChildren[k];
      } else {
        localView = (View)localArrayList.get(k);
      }
      if (((paramInt & 0x1) == 0) && (!localView.isImportantForAutofill()))
      {
        if ((localView instanceof ViewGroup)) {
          ((ViewGroup)localView).populateChildrenForAutofill(paramArrayList, paramInt);
        }
      }
      else {
        paramArrayList.add(localView);
      }
    }
  }
  
  private void recreateChildDisplayList(View paramView)
  {
    boolean bool;
    if ((paramView.mPrivateFlags & 0x80000000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    paramView.mRecreateDisplayList = bool;
    paramView.mPrivateFlags &= 0x7FFFFFFF;
    paramView.updateDisplayListIfDirty();
    paramView.mRecreateDisplayList = false;
  }
  
  private void removeFromArray(int paramInt)
  {
    if (Android_View_ViewGroup.Extension.get().getExtension() != null) {
      ((Android_View_ViewGroup.Interface)Android_View_ViewGroup.Extension.get().getExtension().asInterface()).removeFromArray(this, paramInt);
    } else {
      originalRemoveFromArray(paramInt);
    }
  }
  
  private void removeFromArray(int paramInt1, int paramInt2)
  {
    if (Android_View_ViewGroup.Extension.get().getExtension() != null) {
      ((Android_View_ViewGroup.Interface)Android_View_ViewGroup.Extension.get().getExtension().asInterface()).removeFromArray(this, paramInt1, paramInt2);
    } else {
      originalRemoveFromArray(paramInt1, paramInt2);
    }
  }
  
  private void removePointersFromTouchTargets(int paramInt)
  {
    Object localObject1 = null;
    Object localObject2 = this.mFirstTouchTarget;
    while (localObject2 != null)
    {
      TouchTarget localTouchTarget = ((TouchTarget)localObject2).next;
      if ((((TouchTarget)localObject2).pointerIdBits & paramInt) != 0)
      {
        ((TouchTarget)localObject2).pointerIdBits &= paramInt;
        if (((TouchTarget)localObject2).pointerIdBits == 0)
        {
          if (localObject1 == null) {
            this.mFirstTouchTarget = localTouchTarget;
          } else {
            ((TouchTarget)localObject1).next = localTouchTarget;
          }
          ((TouchTarget)localObject2).recycle();
          localObject2 = localTouchTarget;
          continue;
        }
      }
      localObject1 = localObject2;
      localObject2 = localTouchTarget;
    }
  }
  
  private void removeViewInternal(int paramInt, View paramView)
  {
    Object localObject = this.mTransition;
    if (localObject != null) {
      ((LayoutTransition)localObject).removeChild(this, paramView);
    }
    int i = 0;
    if (paramView == this.mFocused)
    {
      paramView.unFocus(null);
      i = 1;
    }
    if (paramView == this.mFocusedInCluster) {
      clearFocusedInCluster(paramView);
    }
    paramView.clearAccessibilityFocus();
    cancelTouchTarget(paramView);
    cancelHoverTarget(paramView);
    if (paramView.getAnimation() == null)
    {
      localObject = this.mTransitioningViews;
      if ((localObject == null) || (!((ArrayList)localObject).contains(paramView)))
      {
        if (paramView.mAttachInfo == null) {
          break label107;
        }
        paramView.dispatchDetachedFromWindow();
        break label107;
      }
    }
    addDisappearingView(paramView);
    label107:
    boolean bool = paramView.hasTransientState();
    int j = 0;
    if (bool) {
      childHasTransientStateChanged(paramView, false);
    }
    needGlobalAttributesUpdate(false);
    removeFromArray(paramInt);
    if (paramView.hasUnhandledKeyListener()) {
      decrementChildUnhandledKeyListeners();
    }
    if (paramView == this.mDefaultFocus) {
      clearDefaultFocus(paramView);
    }
    if (i != 0)
    {
      clearChildFocus(paramView);
      if (!rootViewRequestFocus()) {
        notifyGlobalFocusCleared(this);
      }
    }
    dispatchViewRemoved(paramView);
    if (paramView.getVisibility() != 8) {
      notifySubtreeAccessibilityStateChangedIfNeeded();
    }
    localObject = this.mTransientIndices;
    if (localObject == null) {
      i = j;
    } else {
      i = ((List)localObject).size();
    }
    for (j = 0; j < i; j++)
    {
      int k = ((Integer)this.mTransientIndices.get(j)).intValue();
      if (paramInt < k) {
        this.mTransientIndices.set(j, Integer.valueOf(k - 1));
      }
    }
    if (this.mCurrentDragStartEvent != null) {
      this.mChildrenInterestedInDrag.remove(paramView);
    }
  }
  
  private boolean removeViewInternal(View paramView)
  {
    int i = indexOfChild(paramView);
    if (i >= 0)
    {
      removeViewInternal(i, paramView);
      return true;
    }
    return false;
  }
  
  private void removeViewsInternal(int paramInt1, int paramInt2)
  {
    int i = paramInt1 + paramInt2;
    if ((paramInt1 >= 0) && (paramInt2 >= 0) && (i <= this.mChildrenCount))
    {
      View localView1 = this.mFocused;
      int j;
      if (this.mAttachInfo != null) {
        j = 1;
      } else {
        j = 0;
      }
      int k = 0;
      Object localObject1 = null;
      View[] arrayOfView = this.mChildren;
      for (int m = paramInt1; m < i; m++)
      {
        View localView2 = arrayOfView[m];
        Object localObject2 = this.mTransition;
        if (localObject2 != null) {
          ((LayoutTransition)localObject2).removeChild(this, localView2);
        }
        if (localView2 == localView1)
        {
          localView2.unFocus(null);
          k = 1;
        }
        if (localView2 == this.mDefaultFocus) {
          localObject1 = localView2;
        }
        if (localView2 == this.mFocusedInCluster) {
          clearFocusedInCluster(localView2);
        }
        localView2.clearAccessibilityFocus();
        cancelTouchTarget(localView2);
        cancelHoverTarget(localView2);
        if (localView2.getAnimation() == null)
        {
          localObject2 = this.mTransitioningViews;
          if ((localObject2 == null) || (!((ArrayList)localObject2).contains(localView2)))
          {
            if (j == 0) {
              break label201;
            }
            localView2.dispatchDetachedFromWindow();
            break label201;
          }
        }
        addDisappearingView(localView2);
        label201:
        if (localView2.hasTransientState()) {
          childHasTransientStateChanged(localView2, false);
        }
        needGlobalAttributesUpdate(false);
        dispatchViewRemoved(localView2);
      }
      removeFromArray(paramInt1, paramInt2);
      if (localObject1 != null) {
        clearDefaultFocus((View)localObject1);
      }
      if (k != 0)
      {
        clearChildFocus(localView1);
        if (!rootViewRequestFocus()) {
          notifyGlobalFocusCleared(localView1);
        }
      }
      return;
    }
    throw new IndexOutOfBoundsException();
  }
  
  private static boolean resetCancelNextUpFlag(View paramView)
  {
    if ((paramView != null) && ((paramView.mPrivateFlags & 0x4000000) != 0))
    {
      paramView.mPrivateFlags &= 0xFBFFFFFF;
      return true;
    }
    return false;
  }
  
  private void resetTouchState()
  {
    clearTouchTargets();
    resetCancelNextUpFlag(this);
    this.mGroupFlags &= 0xFFF7FFFF;
    this.mNestedScrollAxes = 0;
  }
  
  private boolean restoreFocusInClusterInternal(int paramInt)
  {
    if ((this.mFocusedInCluster != null) && (getDescendantFocusability() != 393216) && ((this.mFocusedInCluster.mViewFlags & 0xC) == 0) && (this.mFocusedInCluster.restoreFocusInCluster(paramInt))) {
      return true;
    }
    return super.restoreFocusInCluster(paramInt);
  }
  
  private void setBooleanFlag(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mGroupFlags |= paramInt;
    } else {
      this.mGroupFlags &= paramInt;
    }
  }
  
  private void setLayoutMode(int paramInt, boolean paramBoolean)
  {
    this.mLayoutMode = paramInt;
    setBooleanFlag(8388608, paramBoolean);
  }
  
  private void setTouchscreenBlocksFocusNoRefocus(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mGroupFlags |= 0x4000000;
    } else {
      this.mGroupFlags &= 0xFBFFFFFF;
    }
  }
  
  private static int sign(int paramInt)
  {
    if (paramInt >= 0) {
      paramInt = 1;
    } else {
      paramInt = -1;
    }
    return paramInt;
  }
  
  private void touchAccessibilityNodeProviderIfNeeded(View paramView)
  {
    if (this.mContext.isAutofillCompatibilityEnabled()) {
      paramView.getAccessibilityNodeProvider();
    }
  }
  
  public void addChildrenForAccessibility(ArrayList<View> paramArrayList)
  {
    if (getAccessibilityNodeProvider() != null) {
      return;
    }
    ChildListForAccessibility localChildListForAccessibility = ChildListForAccessibility.obtain(this, true);
    try
    {
      int i = localChildListForAccessibility.getChildCount();
      for (int j = 0; j < i; j++)
      {
        View localView = localChildListForAccessibility.getChildAt(j);
        if ((localView.mViewFlags & 0xC) == 0) {
          if (localView.includeForAccessibility()) {
            paramArrayList.add(localView);
          } else {
            localView.addChildrenForAccessibility(paramArrayList);
          }
        }
      }
      return;
    }
    finally
    {
      localChildListForAccessibility.recycle();
    }
  }
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
  {
    int i = paramArrayList.size();
    int j = getDescendantFocusability();
    boolean bool = shouldBlockFocusForTouchscreen();
    int k;
    if ((!isFocusableInTouchMode()) && (bool)) {
      k = 0;
    } else {
      k = 1;
    }
    if (j == 393216)
    {
      if (k != 0) {
        super.addFocusables(paramArrayList, paramInt1, paramInt2);
      }
      return;
    }
    int m = paramInt2;
    if (bool) {
      m = paramInt2 | 0x1;
    }
    if ((j == 131072) && (k != 0)) {
      super.addFocusables(paramArrayList, paramInt1, m);
    }
    paramInt2 = 0;
    View[] arrayOfView = new View[this.mChildrenCount];
    int n = 0;
    while (n < this.mChildrenCount)
    {
      View localView = this.mChildren[n];
      int i1 = paramInt2;
      if ((localView.mViewFlags & 0xC) == 0)
      {
        arrayOfView[paramInt2] = localView;
        i1 = paramInt2 + 1;
      }
      n++;
      paramInt2 = i1;
    }
    FocusFinder.sort(arrayOfView, 0, paramInt2, this, isLayoutRtl());
    for (n = 0; n < paramInt2; n++) {
      arrayOfView[n].addFocusables(paramArrayList, paramInt1, m);
    }
    if ((j == 262144) && (k != 0) && (i == paramArrayList.size())) {
      super.addFocusables(paramArrayList, paramInt1, m);
    }
  }
  
  public void addKeyboardNavigationClusters(Collection<View> paramCollection, int paramInt)
  {
    int i = paramCollection.size();
    boolean bool;
    if (isKeyboardNavigationCluster()) {
      bool = getTouchscreenBlocksFocus();
    }
    try
    {
      setTouchscreenBlocksFocusNoRefocus(false);
      super.addKeyboardNavigationClusters(paramCollection, paramInt);
      setTouchscreenBlocksFocusNoRefocus(bool);
    }
    finally
    {
      setTouchscreenBlocksFocusNoRefocus(bool);
    }
    if (i != paramCollection.size()) {
      return;
    }
    if (getDescendantFocusability() == 393216) {
      return;
    }
    i = 0;
    View[] arrayOfView = new View[this.mChildrenCount];
    int j = 0;
    while (j < this.mChildrenCount)
    {
      View localView = this.mChildren[j];
      int k = i;
      if ((localView.mViewFlags & 0xC) == 0)
      {
        arrayOfView[i] = localView;
        k = i + 1;
      }
      j++;
      i = k;
    }
    FocusFinder.sort(arrayOfView, 0, i, this, isLayoutRtl());
    for (j = 0; j < i; j++) {
      arrayOfView[j].addKeyboardNavigationClusters(paramCollection, paramInt);
    }
  }
  
  public boolean addStatesFromChildren()
  {
    boolean bool;
    if ((this.mGroupFlags & 0x2000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void addTouchables(ArrayList<View> paramArrayList)
  {
    super.addTouchables(paramArrayList);
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++)
    {
      View localView = arrayOfView[j];
      if ((localView.mViewFlags & 0xC) == 0) {
        localView.addTouchables(paramArrayList);
      }
    }
  }
  
  @UnsupportedAppUsage
  public void addTransientView(View paramView, int paramInt)
  {
    if ((paramInt >= 0) && (paramView != null))
    {
      if (paramView.mParent == null)
      {
        if (this.mTransientIndices == null)
        {
          this.mTransientIndices = new ArrayList();
          this.mTransientViews = new ArrayList();
        }
        int i = this.mTransientIndices.size();
        if (i > 0)
        {
          for (int j = 0; (j < i) && (paramInt >= ((Integer)this.mTransientIndices.get(j)).intValue()); j++) {}
          this.mTransientIndices.add(j, Integer.valueOf(paramInt));
          this.mTransientViews.add(j, paramView);
        }
        else
        {
          this.mTransientIndices.add(Integer.valueOf(paramInt));
          this.mTransientViews.add(paramView);
        }
        paramView.mParent = this;
        if (this.mAttachInfo != null) {
          paramView.dispatchAttachedToWindow(this.mAttachInfo, this.mViewFlags & 0xC);
        }
        invalidate(true);
        return;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("The specified view already has a parent ");
      localStringBuilder.append(paramView.mParent);
      throw new IllegalStateException(localStringBuilder.toString());
    }
  }
  
  public void addView(View paramView)
  {
    addView(paramView, -1);
  }
  
  public void addView(View paramView, int paramInt)
  {
    if (paramView != null)
    {
      LayoutParams localLayoutParams1 = paramView.getLayoutParams();
      LayoutParams localLayoutParams2 = localLayoutParams1;
      if (localLayoutParams1 == null)
      {
        localLayoutParams2 = generateDefaultLayoutParams();
        if (localLayoutParams2 == null) {
          throw new IllegalArgumentException("generateDefaultLayoutParams() cannot return null");
        }
      }
      addView(paramView, paramInt, localLayoutParams2);
      return;
    }
    throw new IllegalArgumentException("Cannot add a null child view to a ViewGroup");
  }
  
  public void addView(View paramView, int paramInt1, int paramInt2)
  {
    LayoutParams localLayoutParams = generateDefaultLayoutParams();
    localLayoutParams.width = paramInt1;
    localLayoutParams.height = paramInt2;
    addView(paramView, -1, localLayoutParams);
  }
  
  public void addView(View paramView, int paramInt, LayoutParams paramLayoutParams)
  {
    if (paramView != null)
    {
      ForceDarkHelper.getInstance().updateWebView(paramView);
      requestLayout();
      invalidate(true);
      addViewInner(paramView, paramInt, paramLayoutParams, false);
      return;
    }
    throw new IllegalArgumentException("Cannot add a null child view to a ViewGroup");
  }
  
  public void addView(View paramView, LayoutParams paramLayoutParams)
  {
    addView(paramView, -1, paramLayoutParams);
  }
  
  protected boolean addViewInLayout(View paramView, int paramInt, LayoutParams paramLayoutParams)
  {
    return addViewInLayout(paramView, paramInt, paramLayoutParams, false);
  }
  
  protected boolean addViewInLayout(View paramView, int paramInt, LayoutParams paramLayoutParams, boolean paramBoolean)
  {
    if (paramView != null)
    {
      paramView.mParent = null;
      addViewInner(paramView, paramInt, paramLayoutParams, paramBoolean);
      paramView.mPrivateFlags = (paramView.mPrivateFlags & 0xFFDFFFFF | 0x20);
      return true;
    }
    throw new IllegalArgumentException("Cannot add a null child view to a ViewGroup");
  }
  
  protected void attachLayoutAnimationParameters(View paramView, LayoutParams paramLayoutParams, int paramInt1, int paramInt2)
  {
    LayoutAnimationController.AnimationParameters localAnimationParameters = paramLayoutParams.layoutAnimationParameters;
    paramView = localAnimationParameters;
    if (localAnimationParameters == null)
    {
      paramView = new LayoutAnimationController.AnimationParameters();
      paramLayoutParams.layoutAnimationParameters = paramView;
    }
    paramView.count = paramInt2;
    paramView.index = paramInt1;
  }
  
  protected void attachViewToParent(View paramView, int paramInt, LayoutParams paramLayoutParams)
  {
    paramView.mLayoutParams = paramLayoutParams;
    int i = paramInt;
    if (paramInt < 0) {
      i = this.mChildrenCount;
    }
    addInArray(paramView, i);
    paramView.mParent = this;
    paramView.mPrivateFlags = (paramView.mPrivateFlags & 0xFFDFFFFF & 0xFFFF7FFF | 0x20 | 0x80000000);
    this.mPrivateFlags |= 0x80000000;
    if (paramView.hasFocus()) {
      requestChildFocus(paramView, paramView.findFocus());
    }
    boolean bool;
    if ((isAttachedToWindow()) && (getWindowVisibility() == 0) && (isShown())) {
      bool = true;
    } else {
      bool = false;
    }
    dispatchVisibilityAggregated(bool);
    notifySubtreeAccessibilityStateChangedIfNeeded();
  }
  
  public void bringChildToFront(View paramView)
  {
    int i = indexOfChild(paramView);
    if (i >= 0)
    {
      removeFromArray(i);
      addInArray(paramView, this.mChildrenCount);
      paramView.mParent = this;
      requestLayout();
      invalidate();
    }
  }
  
  ArrayList<View> buildOrderedChildList()
  {
    int i = this.mChildrenCount;
    if ((i > 1) && (hasChildWithZ()))
    {
      Object localObject = this.mPreSortedChildren;
      if (localObject == null)
      {
        this.mPreSortedChildren = new ArrayList(i);
      }
      else
      {
        ((ArrayList)localObject).clear();
        this.mPreSortedChildren.ensureCapacity(i);
      }
      boolean bool = isChildrenDrawingOrderEnabled();
      for (int j = 0; j < i; j++)
      {
        int k = getAndVerifyPreorderedIndex(i, j, bool);
        localObject = this.mChildren[k];
        float f = ((View)localObject).getZ();
        for (k = j; (k > 0) && (((View)this.mPreSortedChildren.get(k - 1)).getZ() > f); k--) {}
        this.mPreSortedChildren.add(k, localObject);
      }
      return this.mPreSortedChildren;
    }
    return null;
  }
  
  public ArrayList<View> buildTouchDispatchChildList()
  {
    return buildOrderedChildList();
  }
  
  protected boolean canAnimate()
  {
    boolean bool;
    if (this.mLayoutAnimationController != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void captureTransitioningViews(List<View> paramList)
  {
    if (getVisibility() != 0) {
      return;
    }
    if (isTransitionGroup())
    {
      paramList.add(this);
    }
    else
    {
      int i = getChildCount();
      for (int j = 0; j < i; j++) {
        getChildAt(j).captureTransitioningViews(paramList);
      }
    }
  }
  
  protected boolean checkLayoutParams(LayoutParams paramLayoutParams)
  {
    boolean bool;
    if (paramLayoutParams != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void childDrawableStateChanged(View paramView)
  {
    if ((this.mGroupFlags & 0x2000) != 0) {
      refreshDrawableState();
    }
  }
  
  public void childHasTransientStateChanged(View paramView, boolean paramBoolean)
  {
    boolean bool = hasTransientState();
    if (paramBoolean) {
      this.mChildCountWithTransientState += 1;
    } else {
      this.mChildCountWithTransientState -= 1;
    }
    paramBoolean = hasTransientState();
    if ((this.mParent != null) && (bool != paramBoolean)) {
      try
      {
        this.mParent.childHasTransientStateChanged(this, paramBoolean);
      }
      catch (AbstractMethodError localAbstractMethodError)
      {
        paramView = new StringBuilder();
        paramView.append(this.mParent.getClass().getSimpleName());
        paramView.append(" does not fully implement ViewParent");
        Log.e("ViewGroup", paramView.toString(), localAbstractMethodError);
      }
    }
  }
  
  protected void cleanupLayoutState(View paramView)
  {
    paramView.mPrivateFlags &= 0xEFFF;
  }
  
  public void clearChildFocus(View paramView)
  {
    this.mFocused = null;
    if (this.mParent != null) {
      this.mParent.clearChildFocus(this);
    }
  }
  
  void clearDefaultFocus(View paramView)
  {
    View localView = this.mDefaultFocus;
    if ((localView != paramView) && (localView != null) && (localView.isFocusedByDefault())) {
      return;
    }
    this.mDefaultFocus = null;
    for (int i = 0; i < this.mChildrenCount; i++)
    {
      paramView = this.mChildren[i];
      if (paramView.isFocusedByDefault())
      {
        this.mDefaultFocus = paramView;
        return;
      }
      if ((this.mDefaultFocus == null) && (paramView.hasDefaultFocus())) {
        this.mDefaultFocus = paramView;
      }
    }
    if ((this.mParent instanceof ViewGroup)) {
      ((ViewGroup)this.mParent).clearDefaultFocus(this);
    }
  }
  
  public void clearDisappearingChildren()
  {
    ArrayList localArrayList = this.mDisappearingChildren;
    if (localArrayList != null)
    {
      int i = localArrayList.size();
      for (int j = 0; j < i; j++)
      {
        View localView = (View)localArrayList.get(j);
        if (localView.mAttachInfo != null) {
          localView.dispatchDetachedFromWindow();
        }
        localView.clearAnimation();
      }
      localArrayList.clear();
      invalidate();
    }
  }
  
  public void clearFocus()
  {
    if (this.mFocused == null)
    {
      super.clearFocus();
    }
    else
    {
      View localView = this.mFocused;
      this.mFocused = null;
      localView.clearFocus();
    }
  }
  
  void clearFocusedInCluster()
  {
    View localView = findKeyboardNavigationCluster();
    Object localObject = this;
    ViewParent localViewParent;
    do
    {
      ((ViewGroup)localObject).mFocusedInCluster = null;
      if (localObject == localView) {
        break;
      }
      localViewParent = ((ViewParent)localObject).getParent();
      localObject = localViewParent;
    } while ((localViewParent instanceof ViewGroup));
  }
  
  void clearFocusedInCluster(View paramView)
  {
    if (this.mFocusedInCluster != paramView) {
      return;
    }
    clearFocusedInCluster();
  }
  
  Insets computeOpticalInsets()
  {
    if (isLayoutModeOptical())
    {
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      while (n < this.mChildrenCount)
      {
        Object localObject = getChildAt(n);
        int i1 = i;
        int i2 = j;
        int i3 = k;
        int i4 = m;
        if (((View)localObject).getVisibility() == 0)
        {
          localObject = ((View)localObject).getOpticalInsets();
          i1 = Math.max(i, ((Insets)localObject).left);
          i2 = Math.max(j, ((Insets)localObject).top);
          i3 = Math.max(k, ((Insets)localObject).right);
          i4 = Math.max(m, ((Insets)localObject).bottom);
        }
        n++;
        i = i1;
        j = i2;
        k = i3;
        m = i4;
      }
      return Insets.of(i, j, k, m);
    }
    return Insets.NONE;
  }
  
  public Bitmap createSnapshot(ViewDebug.CanvasProvider paramCanvasProvider, boolean paramBoolean)
  {
    int i = this.mChildrenCount;
    Object localObject1 = null;
    Object localObject2;
    int j;
    if (paramBoolean)
    {
      localObject2 = new int[i];
      for (j = 0;; j++)
      {
        localObject1 = localObject2;
        if (j >= i) {
          break;
        }
        localObject1 = getChildAt(j);
        localObject2[j] = ((View)localObject1).getVisibility();
        if (localObject2[j] == 0) {
          ((View)localObject1).mViewFlags = (((View)localObject1).mViewFlags & 0xFFFFFFF3 | 0x4);
        }
      }
    }
    try
    {
      localObject2 = super.createSnapshot(paramCanvasProvider, paramBoolean);
      if (paramBoolean) {
        for (j = 0; j < i; j++)
        {
          paramCanvasProvider = getChildAt(j);
          paramCanvasProvider.mViewFlags = (paramCanvasProvider.mViewFlags & 0xFFFFFFF3 | localObject1[j] & 0xC);
        }
      }
      return (Bitmap)localObject2;
    }
    finally
    {
      if (paramBoolean) {
        for (j = 0; j < i; j++)
        {
          localObject2 = getChildAt(j);
          ((View)localObject2).mViewFlags = (((View)localObject2).mViewFlags & 0xFFFFFFF3 | localObject1[j] & 0xC);
        }
      }
    }
  }
  
  protected void debug(int paramInt)
  {
    super.debug(paramInt);
    Object localObject1;
    Object localObject2;
    if (this.mFocused != null)
    {
      localObject1 = debugIndent(paramInt);
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append((String)localObject1);
      ((StringBuilder)localObject2).append("mFocused");
      Log.d("View", ((StringBuilder)localObject2).toString());
      this.mFocused.debug(paramInt + 1);
    }
    if (this.mDefaultFocus != null)
    {
      localObject2 = debugIndent(paramInt);
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append((String)localObject2);
      ((StringBuilder)localObject1).append("mDefaultFocus");
      Log.d("View", ((StringBuilder)localObject1).toString());
      this.mDefaultFocus.debug(paramInt + 1);
    }
    if (this.mFocusedInCluster != null)
    {
      localObject2 = debugIndent(paramInt);
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append((String)localObject2);
      ((StringBuilder)localObject1).append("mFocusedInCluster");
      Log.d("View", ((StringBuilder)localObject1).toString());
      this.mFocusedInCluster.debug(paramInt + 1);
    }
    if (this.mChildrenCount != 0)
    {
      localObject2 = debugIndent(paramInt);
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append((String)localObject2);
      ((StringBuilder)localObject1).append("{");
      Log.d("View", ((StringBuilder)localObject1).toString());
    }
    int i = this.mChildrenCount;
    for (int j = 0; j < i; j++) {
      this.mChildren[j].debug(paramInt + 1);
    }
    if (this.mChildrenCount != 0)
    {
      localObject1 = debugIndent(paramInt);
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append((String)localObject1);
      ((StringBuilder)localObject2).append("}");
      Log.d("View", ((StringBuilder)localObject2).toString());
    }
  }
  
  void decrementChildUnhandledKeyListeners()
  {
    this.mChildUnhandledKeyListeners -= 1;
    if ((this.mChildUnhandledKeyListeners == 0) && ((this.mParent instanceof ViewGroup))) {
      ((ViewGroup)this.mParent).decrementChildUnhandledKeyListeners();
    }
  }
  
  protected void destroyHardwareResources()
  {
    super.destroyHardwareResources();
    int i = getChildCount();
    for (int j = 0; j < i; j++) {
      getChildAt(j).destroyHardwareResources();
    }
  }
  
  protected void detachAllViewsFromParent()
  {
    int i = this.mChildrenCount;
    if (i <= 0) {
      return;
    }
    View[] arrayOfView = this.mChildren;
    this.mChildrenCount = 0;
    i--;
    while (i >= 0)
    {
      arrayOfView[i].mParent = null;
      arrayOfView[i] = null;
      i--;
    }
  }
  
  protected void detachViewFromParent(int paramInt)
  {
    removeFromArray(paramInt);
  }
  
  protected void detachViewFromParent(View paramView)
  {
    removeFromArray(indexOfChild(paramView));
  }
  
  protected void detachViewsFromParent(int paramInt1, int paramInt2)
  {
    removeFromArray(paramInt1, paramInt2);
  }
  
  public boolean dispatchActivityResult(String paramString, int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (super.dispatchActivityResult(paramString, paramInt1, paramInt2, paramIntent)) {
      return true;
    }
    int i = getChildCount();
    for (int j = 0; j < i; j++) {
      if (getChildAt(j).dispatchActivityResult(paramString, paramInt1, paramInt2, paramIntent)) {
        return true;
      }
    }
    return false;
  }
  
  public WindowInsets dispatchApplyWindowInsets(WindowInsets paramWindowInsets)
  {
    paramWindowInsets = super.dispatchApplyWindowInsets(paramWindowInsets);
    if (View.sBrokenInsetsDispatch) {
      return brokenDispatchApplyWindowInsets(paramWindowInsets);
    }
    return newDispatchApplyWindowInsets(paramWindowInsets);
  }
  
  @UnsupportedAppUsage
  void dispatchAttachedToWindow(View.AttachInfo paramAttachInfo, int paramInt)
  {
    this.mGroupFlags |= 0x400000;
    super.dispatchAttachedToWindow(paramAttachInfo, paramInt);
    this.mGroupFlags &= 0xFFBFFFFF;
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++)
    {
      localObject = arrayOfView[j];
      ((View)localObject).dispatchAttachedToWindow(paramAttachInfo, combineVisibility(paramInt, ((View)localObject).getVisibility()));
    }
    Object localObject = this.mTransientIndices;
    if (localObject == null) {
      j = 0;
    } else {
      j = ((List)localObject).size();
    }
    for (i = 0; i < j; i++)
    {
      localObject = (View)this.mTransientViews.get(i);
      ((View)localObject).dispatchAttachedToWindow(paramAttachInfo, combineVisibility(paramInt, ((View)localObject).getVisibility()));
    }
  }
  
  void dispatchCancelPendingInputEvents()
  {
    super.dispatchCancelPendingInputEvents();
    View[] arrayOfView = this.mChildren;
    int i = this.mChildrenCount;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].dispatchCancelPendingInputEvents();
    }
  }
  
  public boolean dispatchCapturedPointerEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mPrivateFlags & 0x12) == 18)
    {
      if (super.dispatchCapturedPointerEvent(paramMotionEvent)) {
        return true;
      }
    }
    else
    {
      View localView = this.mFocused;
      if ((localView != null) && ((localView.mPrivateFlags & 0x10) == 16) && (this.mFocused.dispatchCapturedPointerEvent(paramMotionEvent))) {
        return true;
      }
    }
    return false;
  }
  
  void dispatchCollectViewAttributes(View.AttachInfo paramAttachInfo, int paramInt)
  {
    if ((paramInt & 0xC) == 0)
    {
      super.dispatchCollectViewAttributes(paramAttachInfo, paramInt);
      int i = this.mChildrenCount;
      View[] arrayOfView = this.mChildren;
      for (int j = 0; j < i; j++)
      {
        View localView = arrayOfView[j];
        localView.dispatchCollectViewAttributes(paramAttachInfo, localView.mViewFlags & 0xC | paramInt);
      }
    }
  }
  
  public void dispatchConfigurationChanged(Configuration paramConfiguration)
  {
    super.dispatchConfigurationChanged(paramConfiguration);
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].dispatchConfigurationChanged(paramConfiguration);
    }
  }
  
  @UnsupportedAppUsage
  void dispatchDetachedFromWindow()
  {
    cancelAndClearTouchTargets(null);
    exitHoverTargets();
    exitTooltipHoverTargets();
    int i = 0;
    this.mLayoutCalledWhileSuppressed = false;
    this.mChildrenInterestedInDrag = null;
    this.mIsInterestedInDrag = false;
    Object localObject = this.mCurrentDragStartEvent;
    if (localObject != null)
    {
      ((DragEvent)localObject).recycle();
      this.mCurrentDragStartEvent = null;
    }
    int j = this.mChildrenCount;
    localObject = this.mChildren;
    for (int k = 0; k < j; k++) {
      localObject[k].dispatchDetachedFromWindow();
    }
    clearDisappearingChildren();
    if (this.mTransientViews == null) {
      k = i;
    } else {
      k = this.mTransientIndices.size();
    }
    for (i = 0; i < k; i++) {
      ((View)this.mTransientViews.get(i)).dispatchDetachedFromWindow();
    }
    super.dispatchDetachedFromWindow();
  }
  
  public void dispatchDisplayHint(int paramInt)
  {
    super.dispatchDisplayHint(paramInt);
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].dispatchDisplayHint(paramInt);
    }
  }
  
  boolean dispatchDragEnterExitInPreN(DragEvent paramDragEvent)
  {
    if (paramDragEvent.mAction == 6)
    {
      View localView = this.mCurrentDragChild;
      if (localView != null)
      {
        localView.dispatchDragEnterExitInPreN(paramDragEvent);
        this.mCurrentDragChild = null;
      }
    }
    boolean bool;
    if ((this.mIsInterestedInDrag) && (super.dispatchDragEnterExitInPreN(paramDragEvent))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean dispatchDragEvent(DragEvent paramDragEvent)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    float f1 = paramDragEvent.mX;
    float f2 = paramDragEvent.mY;
    Object localObject1 = paramDragEvent.mClipData;
    PointF localPointF = getLocalPoint();
    int i = paramDragEvent.mAction;
    Object localObject2;
    if (i != 1)
    {
      if ((i != 2) && (i != 3))
      {
        if (i != 4)
        {
          bool1 = bool3;
        }
        else
        {
          localObject1 = this.mChildrenInterestedInDrag;
          if (localObject1 != null)
          {
            localObject2 = ((HashSet)localObject1).iterator();
            bool1 = bool4;
            while (((Iterator)localObject2).hasNext()) {
              if (((View)((Iterator)localObject2).next()).dispatchDragEvent(paramDragEvent)) {
                bool1 = true;
              }
            }
            ((HashSet)localObject1).clear();
          }
          localObject1 = this.mCurrentDragStartEvent;
          if (localObject1 != null)
          {
            ((DragEvent)localObject1).recycle();
            this.mCurrentDragStartEvent = null;
          }
          bool4 = bool1;
          if (this.mIsInterestedInDrag)
          {
            if (super.dispatchDragEvent(paramDragEvent)) {
              bool1 = true;
            }
            this.mIsInterestedInDrag = false;
            bool4 = bool1;
          }
          bool1 = bool4;
        }
      }
      else
      {
        localObject2 = findFrontmostDroppableChildAt(paramDragEvent.mX, paramDragEvent.mY, localPointF);
        if (localObject2 != this.mCurrentDragChild)
        {
          if (sCascadedDragDrop)
          {
            i = paramDragEvent.mAction;
            paramDragEvent.mX = 0.0F;
            paramDragEvent.mY = 0.0F;
            paramDragEvent.mClipData = null;
            View localView = this.mCurrentDragChild;
            if (localView != null)
            {
              paramDragEvent.mAction = 6;
              localView.dispatchDragEnterExitInPreN(paramDragEvent);
            }
            if (localObject2 != null)
            {
              paramDragEvent.mAction = 5;
              ((View)localObject2).dispatchDragEnterExitInPreN(paramDragEvent);
            }
            paramDragEvent.mAction = i;
            paramDragEvent.mX = f1;
            paramDragEvent.mY = f2;
            paramDragEvent.mClipData = ((ClipData)localObject1);
          }
          this.mCurrentDragChild = ((View)localObject2);
        }
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject1 = localObject2;
          if (this.mIsInterestedInDrag) {
            localObject1 = this;
          }
        }
        bool1 = bool3;
        if (localObject1 != null) {
          if (localObject1 != this)
          {
            paramDragEvent.mX = localPointF.x;
            paramDragEvent.mY = localPointF.y;
            bool4 = ((View)localObject1).dispatchDragEvent(paramDragEvent);
            paramDragEvent.mX = f1;
            paramDragEvent.mY = f2;
            bool1 = bool4;
            if (this.mIsInterestedInDrag)
            {
              if (sCascadedDragDrop) {
                bool2 = bool4;
              } else {
                bool2 = paramDragEvent.mEventHandlerWasCalled;
              }
              bool1 = bool4;
              if (!bool2) {
                bool1 = super.dispatchDragEvent(paramDragEvent);
              }
            }
          }
          else
          {
            bool1 = super.dispatchDragEvent(paramDragEvent);
          }
        }
      }
    }
    else
    {
      this.mCurrentDragChild = null;
      this.mCurrentDragStartEvent = DragEvent.obtain(paramDragEvent);
      localObject1 = this.mChildrenInterestedInDrag;
      if (localObject1 == null) {
        this.mChildrenInterestedInDrag = new HashSet();
      } else {
        ((HashSet)localObject1).clear();
      }
      int j = this.mChildrenCount;
      localObject1 = this.mChildren;
      i = 0;
      for (bool1 = bool2; i < j; bool1 = bool4)
      {
        localObject2 = localObject1[i];
        ((View)localObject2).mPrivateFlags2 &= 0xFFFFFFFC;
        bool4 = bool1;
        if (((View)localObject2).getVisibility() == 0)
        {
          bool4 = bool1;
          if (notifyChildOfDragStart(localObject1[i])) {
            bool4 = true;
          }
        }
        i++;
      }
      this.mIsInterestedInDrag = super.dispatchDragEvent(paramDragEvent);
      if (this.mIsInterestedInDrag) {
        bool1 = true;
      }
      if (!bool1)
      {
        this.mCurrentDragStartEvent.recycle();
        this.mCurrentDragStartEvent = null;
      }
    }
    return bool1;
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    boolean bool1 = paramCanvas.isRecordingFor(this.mRenderNode);
    int i = this.mChildrenCount;
    Object localObject1 = this.mChildren;
    int j = this.mGroupFlags;
    Object localObject3;
    if (((j & 0x8) != 0) && (canAnimate()))
    {
      isHardwareAccelerated();
      for (m = 0; m < i; m++)
      {
        localObject2 = localObject1[m];
        if ((((View)localObject2).mViewFlags & 0xC) == 0)
        {
          attachLayoutAnimationParameters((View)localObject2, ((View)localObject2).getLayoutParams(), m, i);
          bindLayoutAnimation((View)localObject2);
        }
      }
      localObject3 = this.mLayoutAnimationController;
      if (((LayoutAnimationController)localObject3).willOverlap()) {
        this.mGroupFlags |= 0x80;
      }
      ((LayoutAnimationController)localObject3).start();
      this.mGroupFlags &= 0xFFFFFFF7;
      this.mGroupFlags &= 0xFFFFFFEF;
      localObject2 = this.mAnimationListener;
      if (localObject2 != null) {
        ((Animation.AnimationListener)localObject2).onAnimationStart(((LayoutAnimationController)localObject3).getAnimation());
      }
    }
    int i2 = 0;
    boolean bool2 = false;
    int i3;
    if ((j & 0x22) == 34) {
      i3 = 1;
    } else {
      i3 = 0;
    }
    if (i3 != 0)
    {
      i2 = paramCanvas.save(2);
      paramCanvas.clipRect(this.mScrollX + this.mPaddingLeft, this.mScrollY + this.mPaddingTop, this.mScrollX + this.mRight - this.mLeft - this.mPaddingRight, this.mScrollY + this.mBottom - this.mTop - this.mPaddingBottom);
    }
    this.mPrivateFlags &= 0xFFFFFFBF;
    this.mGroupFlags &= 0xFFFFFFFB;
    long l = getDrawingTime();
    if (bool1) {
      paramCanvas.insertReorderBarrier();
    }
    Object localObject2 = this.mTransientIndices;
    int i4;
    if (localObject2 == null) {
      i4 = 0;
    } else {
      i4 = ((List)localObject2).size();
    }
    int i5;
    if (i4 != 0) {
      i5 = 0;
    } else {
      i5 = -1;
    }
    if (bool1) {
      localObject2 = null;
    } else {
      localObject2 = buildOrderedChildList();
    }
    if ((localObject2 == null) && (isChildrenDrawingOrderEnabled())) {
      bool2 = true;
    }
    int m = 0;
    int i6 = 0;
    while (i6 < i)
    {
      int i7 = m;
      m = j;
      int i8;
      while ((i5 >= 0) && (((Integer)this.mTransientIndices.get(i5)).intValue() == i6))
      {
        localObject3 = (View)this.mTransientViews.get(i5);
        if ((((View)localObject3).mViewFlags & 0xC) != 0)
        {
          j = i7;
          if (((View)localObject3).getAnimation() == null) {}
        }
        else
        {
          k = i7 | drawChild(paramCanvas, (View)localObject3, l);
        }
        i5++;
        i8 = i5;
        if (i5 >= i4) {
          i8 = -1;
        }
        i5 = i8;
        i7 = k;
      }
      localObject3 = getAndVerifyPreorderedView((ArrayList)localObject2, (View[])localObject1, getAndVerifyPreorderedIndex(i, i6, bool2));
      if ((((View)localObject3).mViewFlags & 0xC) != 0)
      {
        i8 = i7;
        if (((View)localObject3).getAnimation() == null) {}
      }
      else
      {
        i9 = i7 | drawChild(paramCanvas, (View)localObject3, l);
      }
      i6++;
      int k = m;
      m = i9;
    }
    int n;
    for (int i9 = m;; i9 = n)
    {
      m = i9;
      if (i5 < 0) {
        break;
      }
      localObject1 = (View)this.mTransientViews.get(i5);
      if ((((View)localObject1).mViewFlags & 0xC) != 0)
      {
        m = i9;
        if (((View)localObject1).getAnimation() == null) {}
      }
      else
      {
        n = i9 | drawChild(paramCanvas, (View)localObject1, l);
      }
      i5++;
      if (i5 >= i4) {
        break;
      }
    }
    if (localObject2 != null) {
      ((ArrayList)localObject2).clear();
    }
    i9 = n;
    if (this.mDisappearingChildren != null)
    {
      localObject2 = this.mDisappearingChildren;
      for (i5 = ((ArrayList)localObject2).size() - 1;; i5--)
      {
        i9 = n;
        if (i5 < 0) {
          break;
        }
        n |= drawChild(paramCanvas, (View)((ArrayList)localObject2).get(i5), l);
      }
    }
    if (bool1) {
      paramCanvas.insertInorderBarrier();
    }
    localObject2 = MiuiMultiWindowAdapter.getCaptionView(this, this.mContext);
    if (localObject2 != null) {
      ((DecorCaptionView)localObject2).findViewById(16909524).draw(paramCanvas, (ViewGroup)localObject2, getDrawingTime());
    }
    if (debugDraw()) {
      onDebugDraw(paramCanvas);
    }
    if (i3 != 0) {
      paramCanvas.restoreToCount(i2);
    }
    int i1 = this.mGroupFlags;
    if ((i1 & 0x4) == 4) {
      invalidate(true);
    }
    if (((i1 & 0x10) == 0) && ((i1 & 0x200) == 0))
    {
      paramCanvas = this.mLayoutAnimationController;
      if ((paramCanvas != null) && (paramCanvas.isDone()) && (i9 == 0))
      {
        this.mGroupFlags |= 0x200;
        post(new Runnable()
        {
          public void run()
          {
            ViewGroup.this.notifyAnimationListener();
          }
        });
      }
    }
  }
  
  public void dispatchDrawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    int i = this.mChildrenCount;
    if (i == 0) {
      return;
    }
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++)
    {
      View localView = arrayOfView[j];
      int k;
      if ((!localView.isClickable()) && (!localView.isLongClickable())) {
        k = 1;
      } else {
        k = 0;
      }
      int m;
      if ((localView.mViewFlags & 0x400000) != 0) {
        m = 1;
      } else {
        m = 0;
      }
      if ((k != 0) || (m != 0))
      {
        float[] arrayOfFloat = getTempPoint();
        arrayOfFloat[0] = paramFloat1;
        arrayOfFloat[1] = paramFloat2;
        transformPointToViewLocal(arrayOfFloat, localView);
        localView.drawableHotspotChanged(arrayOfFloat[0], arrayOfFloat[1]);
      }
    }
  }
  
  public void dispatchFinishTemporaryDetach()
  {
    super.dispatchFinishTemporaryDetach();
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].dispatchFinishTemporaryDetach();
    }
  }
  
  protected void dispatchFreezeSelfOnly(SparseArray<Parcelable> paramSparseArray)
  {
    super.dispatchSaveInstanceState(paramSparseArray);
  }
  
  protected boolean dispatchGenericFocusedEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mPrivateFlags & 0x12) == 18) {
      return super.dispatchGenericFocusedEvent(paramMotionEvent);
    }
    View localView = this.mFocused;
    if ((localView != null) && ((localView.mPrivateFlags & 0x10) == 16)) {
      return this.mFocused.dispatchGenericMotionEvent(paramMotionEvent);
    }
    return false;
  }
  
  protected boolean dispatchGenericPointerEvent(MotionEvent paramMotionEvent)
  {
    int i = this.mChildrenCount;
    if (i != 0)
    {
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      ArrayList localArrayList = buildOrderedChildList();
      boolean bool;
      if ((localArrayList == null) && (isChildrenDrawingOrderEnabled())) {
        bool = true;
      } else {
        bool = false;
      }
      View[] arrayOfView = this.mChildren;
      for (int j = i - 1; j >= 0; j--)
      {
        View localView = getAndVerifyPreorderedView(localArrayList, arrayOfView, getAndVerifyPreorderedIndex(i, j, bool));
        if ((localView.canReceivePointerEvents()) && (isTransformedTouchPointInView(f1, f2, localView, null)) && (dispatchTransformedGenericPointerEvent(paramMotionEvent, localView)))
        {
          if (localArrayList != null) {
            localArrayList.clear();
          }
          return true;
        }
      }
      if (localArrayList != null) {
        localArrayList.clear();
      }
    }
    return super.dispatchGenericPointerEvent(paramMotionEvent);
  }
  
  @UnsupportedAppUsage
  protected void dispatchGetDisplayList()
  {
    int i = this.mChildrenCount;
    Object localObject = this.mChildren;
    for (int j = 0; j < i; j++)
    {
      View localView = localObject[j];
      if (((localView.mViewFlags & 0xC) == 0) || (localView.getAnimation() != null)) {
        recreateChildDisplayList(localView);
      }
    }
    if (this.mTransientViews == null) {
      j = 0;
    } else {
      j = this.mTransientIndices.size();
    }
    for (i = 0; i < j; i++)
    {
      localObject = (View)this.mTransientViews.get(i);
      if (((((View)localObject).mViewFlags & 0xC) == 0) || (((View)localObject).getAnimation() != null)) {
        recreateChildDisplayList((View)localObject);
      }
    }
    if (this.mOverlay != null) {
      recreateChildDisplayList(this.mOverlay.getOverlayView());
    }
    if (this.mDisappearingChildren != null)
    {
      localObject = this.mDisappearingChildren;
      i = ((ArrayList)localObject).size();
      for (j = 0; j < i; j++) {
        recreateChildDisplayList((View)((ArrayList)localObject).get(j));
      }
    }
  }
  
  protected boolean dispatchHoverEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    boolean bool1 = onInterceptHoverEvent(paramMotionEvent);
    paramMotionEvent.setAction(i);
    Object localObject1 = paramMotionEvent;
    Object localObject2 = this.mFirstHoverTarget;
    this.mFirstHoverTarget = null;
    boolean bool3;
    boolean bool4;
    if ((!bool1) && (i != 10))
    {
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      int j = this.mChildrenCount;
      if (j != 0)
      {
        ArrayList localArrayList = buildOrderedChildList();
        if ((localArrayList == null) && (isChildrenDrawingOrderEnabled())) {
          bool2 = true;
        } else {
          bool2 = false;
        }
        bool3 = bool2;
        View[] arrayOfView = this.mChildren;
        int k = j - 1;
        bool2 = false;
        Object localObject3 = null;
        localObject4 = localObject1;
        localObject1 = localObject2;
        while (k >= 0)
        {
          View localView = getAndVerifyPreorderedView(localArrayList, arrayOfView, getAndVerifyPreorderedIndex(j, k, bool3));
          if ((localView.canReceivePointerEvents()) && (isTransformedTouchPointInView(f1, f2, localView, null)))
          {
            localObject2 = localObject1;
            Object localObject5 = null;
            for (;;)
            {
              int m;
              if (localObject2 == null)
              {
                localObject2 = HoverTarget.obtain(localView);
                m = 0;
              }
              else
              {
                if (((HoverTarget)localObject2).child != localView) {
                  break label389;
                }
                if (localObject5 != null) {
                  ((HoverTarget)localObject5).next = ((HoverTarget)localObject2).next;
                } else {
                  localObject1 = ((HoverTarget)localObject2).next;
                }
                ((HoverTarget)localObject2).next = null;
                m = 1;
              }
              if (localObject3 != null) {
                ((HoverTarget)localObject3).next = ((HoverTarget)localObject2);
              } else {
                this.mFirstHoverTarget = ((HoverTarget)localObject2);
              }
              if (i == 9)
              {
                if (m == 0) {
                  bool2 |= dispatchTransformedGenericPointerEvent(paramMotionEvent, localView);
                }
              }
              else if (i == 7) {
                if (m == 0)
                {
                  localObject4 = obtainMotionEventNoHistoryOrSelf((MotionEvent)localObject4);
                  ((MotionEvent)localObject4).setAction(9);
                  bool4 = dispatchTransformedGenericPointerEvent((MotionEvent)localObject4, localView);
                  ((MotionEvent)localObject4).setAction(i);
                  bool2 = bool2 | bool4 | dispatchTransformedGenericPointerEvent((MotionEvent)localObject4, localView);
                }
                else
                {
                  bool2 |= dispatchTransformedGenericPointerEvent(paramMotionEvent, localView);
                }
              }
              if (bool2)
              {
                localObject2 = localObject4;
                localObject3 = localObject1;
                bool1 = bool2;
                break label420;
              }
              localObject3 = localObject2;
              break;
              label389:
              localObject5 = localObject2;
              localObject2 = ((HoverTarget)localObject2).next;
            }
          }
          k--;
        }
        bool1 = bool2;
        localObject3 = localObject1;
        localObject2 = localObject4;
        label420:
        localObject1 = localObject2;
        localObject4 = localObject3;
        bool2 = bool1;
        if (localArrayList == null) {
          break label465;
        }
        localArrayList.clear();
        localObject1 = localObject2;
        localObject4 = localObject3;
        bool2 = bool1;
        break label465;
      }
    }
    boolean bool2 = false;
    for (Object localObject4 = localObject2; localObject4 != null; localObject4 = localObject2)
    {
      label465:
      localObject2 = ((HoverTarget)localObject4).child;
      if (i == 10)
      {
        bool2 |= dispatchTransformedGenericPointerEvent(paramMotionEvent, (View)localObject2);
      }
      else
      {
        if (i == 7)
        {
          bool1 = paramMotionEvent.isHoverExitPending();
          paramMotionEvent.setHoverExitPending(true);
          dispatchTransformedGenericPointerEvent(paramMotionEvent, (View)localObject2);
          paramMotionEvent.setHoverExitPending(bool1);
        }
        localObject1 = obtainMotionEventNoHistoryOrSelf((MotionEvent)localObject1);
        ((MotionEvent)localObject1).setAction(10);
        dispatchTransformedGenericPointerEvent((MotionEvent)localObject1, (View)localObject2);
        ((MotionEvent)localObject1).setAction(i);
      }
      localObject2 = ((HoverTarget)localObject4).next;
      ((HoverTarget)localObject4).recycle();
    }
    if ((!bool2) && (i != 10) && (!paramMotionEvent.isHoverExitPending())) {
      bool4 = true;
    } else {
      bool4 = false;
    }
    bool1 = this.mHoveredSelf;
    if (bool4 == bool1)
    {
      localObject2 = localObject1;
      bool1 = bool2;
      if (bool4)
      {
        bool1 = bool2 | super.dispatchHoverEvent(paramMotionEvent);
        localObject2 = localObject1;
      }
    }
    else
    {
      localObject4 = localObject1;
      bool3 = bool2;
      if (bool1)
      {
        if (i == 10)
        {
          bool2 |= super.dispatchHoverEvent(paramMotionEvent);
        }
        else
        {
          if (i == 7) {
            super.dispatchHoverEvent(paramMotionEvent);
          }
          localObject1 = obtainMotionEventNoHistoryOrSelf((MotionEvent)localObject1);
          ((MotionEvent)localObject1).setAction(10);
          super.dispatchHoverEvent((MotionEvent)localObject1);
          ((MotionEvent)localObject1).setAction(i);
        }
        this.mHoveredSelf = false;
        bool3 = bool2;
        localObject4 = localObject1;
      }
      localObject2 = localObject4;
      bool1 = bool3;
      if (bool4) {
        if (i == 9)
        {
          bool1 = bool3 | super.dispatchHoverEvent(paramMotionEvent);
          this.mHoveredSelf = true;
          localObject2 = localObject4;
        }
        else
        {
          localObject2 = localObject4;
          bool1 = bool3;
          if (i == 7)
          {
            localObject2 = obtainMotionEventNoHistoryOrSelf((MotionEvent)localObject4);
            ((MotionEvent)localObject2).setAction(9);
            bool2 = super.dispatchHoverEvent((MotionEvent)localObject2);
            ((MotionEvent)localObject2).setAction(i);
            bool1 = bool3 | bool2 | super.dispatchHoverEvent((MotionEvent)localObject2);
            this.mHoveredSelf = true;
          }
        }
      }
    }
    if (localObject2 != paramMotionEvent) {
      ((MotionEvent)localObject2).recycle();
    }
    return bool1;
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if (this.mInputEventConsistencyVerifier != null) {
      this.mInputEventConsistencyVerifier.onKeyEvent(paramKeyEvent, 1);
    }
    if ((this.mPrivateFlags & 0x12) == 18)
    {
      if (super.dispatchKeyEvent(paramKeyEvent)) {
        return true;
      }
    }
    else
    {
      View localView = this.mFocused;
      if ((localView != null) && ((localView.mPrivateFlags & 0x10) == 16) && (this.mFocused.dispatchKeyEvent(paramKeyEvent))) {
        return true;
      }
    }
    if (this.mInputEventConsistencyVerifier != null) {
      this.mInputEventConsistencyVerifier.onUnhandledEvent(paramKeyEvent, 1);
    }
    return false;
  }
  
  public boolean dispatchKeyEventPreIme(KeyEvent paramKeyEvent)
  {
    if ((this.mPrivateFlags & 0x12) == 18) {
      return super.dispatchKeyEventPreIme(paramKeyEvent);
    }
    View localView = this.mFocused;
    if ((localView != null) && ((localView.mPrivateFlags & 0x10) == 16)) {
      return this.mFocused.dispatchKeyEventPreIme(paramKeyEvent);
    }
    return false;
  }
  
  public boolean dispatchKeyShortcutEvent(KeyEvent paramKeyEvent)
  {
    if ((this.mPrivateFlags & 0x12) == 18) {
      return super.dispatchKeyShortcutEvent(paramKeyEvent);
    }
    View localView = this.mFocused;
    if ((localView != null) && ((localView.mPrivateFlags & 0x10) == 16)) {
      return this.mFocused.dispatchKeyShortcutEvent(paramKeyEvent);
    }
    return false;
  }
  
  void dispatchMovedToDisplay(Display paramDisplay, Configuration paramConfiguration)
  {
    super.dispatchMovedToDisplay(paramDisplay, paramConfiguration);
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].dispatchMovedToDisplay(paramDisplay, paramConfiguration);
    }
  }
  
  public void dispatchPointerCaptureChanged(boolean paramBoolean)
  {
    exitHoverTargets();
    super.dispatchPointerCaptureChanged(paramBoolean);
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].dispatchPointerCaptureChanged(paramBoolean);
    }
  }
  
  public boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    boolean bool;
    if (includeForAccessibility())
    {
      bool = super.dispatchPopulateAccessibilityEventInternal(paramAccessibilityEvent);
      if (bool) {
        return bool;
      }
    }
    ChildListForAccessibility localChildListForAccessibility = ChildListForAccessibility.obtain(this, true);
    try
    {
      int i = localChildListForAccessibility.getChildCount();
      for (int j = 0; j < i; j++)
      {
        View localView = localChildListForAccessibility.getChildAt(j);
        if ((localView.mViewFlags & 0xC) == 0)
        {
          bool = localView.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent);
          if (bool) {
            return bool;
          }
        }
      }
      return false;
    }
    finally
    {
      localChildListForAccessibility.recycle();
    }
  }
  
  public void dispatchProvideAutofillStructure(ViewStructure paramViewStructure, int paramInt)
  {
    super.dispatchProvideAutofillStructure(paramViewStructure, paramInt);
    if (paramViewStructure.getChildCount() != 0) {
      return;
    }
    if (!isLaidOut())
    {
      if (Helper.sVerbose)
      {
        paramViewStructure = new StringBuilder();
        paramViewStructure.append("dispatchProvideAutofillStructure(): not laid out, ignoring ");
        paramViewStructure.append(this.mChildrenCount);
        paramViewStructure.append(" children of ");
        paramViewStructure.append(getAutofillId());
        Log.v("View", paramViewStructure.toString());
      }
      return;
    }
    ChildListForAutofill localChildListForAutofill = getChildrenForAutofill(paramInt);
    int i = localChildListForAutofill.size();
    paramViewStructure.setChildCount(i);
    for (int j = 0; j < i; j++) {
      ((View)localChildListForAutofill.get(j)).dispatchProvideAutofillStructure(paramViewStructure.newChild(j), paramInt);
    }
    localChildListForAutofill.recycle();
  }
  
  public void dispatchProvideStructure(ViewStructure paramViewStructure)
  {
    super.dispatchProvideStructure(paramViewStructure);
    if ((!isAssistBlocked()) && (paramViewStructure.getChildCount() == 0))
    {
      int i = this.mChildrenCount;
      if (i <= 0) {
        return;
      }
      if (!isLaidOut())
      {
        if (Helper.sVerbose)
        {
          paramViewStructure = new StringBuilder();
          paramViewStructure.append("dispatchProvideStructure(): not laid out, ignoring ");
          paramViewStructure.append(i);
          paramViewStructure.append(" children of ");
          paramViewStructure.append(getAccessibilityViewId());
          Log.v("View", paramViewStructure.toString());
        }
        return;
      }
      paramViewStructure.setChildCount(i);
      Object localObject1 = buildOrderedChildList();
      boolean bool1;
      if ((localObject1 == null) && (isChildrenDrawingOrderEnabled())) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      int j = 0;
      while (j < i)
      {
        int m;
        try
        {
          k = getAndVerifyPreorderedIndex(i, j, bool1);
        }
        catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
        {
          m = j;
          if (this.mContext.getApplicationInfo().targetSdkVersion >= 23) {
            break label418;
          }
        }
        Object localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("Bad getChildDrawingOrder while collecting assist @ ");
        ((StringBuilder)localObject2).append(j);
        ((StringBuilder)localObject2).append(" of ");
        ((StringBuilder)localObject2).append(i);
        Log.w("ViewGroup", ((StringBuilder)localObject2).toString(), localIndexOutOfBoundsException);
        boolean bool2 = false;
        bool1 = bool2;
        int k = m;
        ArrayList localArrayList;
        if (j > 0)
        {
          localObject2 = new int[i];
          localObject1 = new SparseBooleanArray();
          for (int n = 0; n < j; n++)
          {
            localObject2[n] = getChildDrawingOrder(i, n);
            ((SparseBooleanArray)localObject1).put(localObject2[n], true);
          }
          n = 0;
          for (k = j; k < i; k++)
          {
            while (((SparseBooleanArray)localObject1).get(n, false)) {
              n++;
            }
            localObject2[k] = n;
            n++;
          }
          localArrayList = new ArrayList(i);
          for (n = 0;; n++)
          {
            localObject1 = localArrayList;
            bool1 = bool2;
            k = m;
            if (n >= i) {
              break;
            }
            k = localObject2[n];
            localArrayList.add(this.mChildren[k]);
          }
        }
        getAndVerifyPreorderedView((ArrayList)localObject1, this.mChildren, k).dispatchProvideStructure(paramViewStructure.newChild(j));
        j++;
        continue;
        label418:
        throw localArrayList;
      }
      if (localObject1 != null) {
        ((ArrayList)localObject1).clear();
      }
      return;
    }
  }
  
  protected void dispatchRestoreInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    super.dispatchRestoreInstanceState(paramSparseArray);
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++)
    {
      View localView = arrayOfView[j];
      if ((localView.mViewFlags & 0x20000000) != 536870912) {
        localView.dispatchRestoreInstanceState(paramSparseArray);
      }
    }
  }
  
  protected void dispatchSaveInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    super.dispatchSaveInstanceState(paramSparseArray);
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++)
    {
      View localView = arrayOfView[j];
      if ((localView.mViewFlags & 0x20000000) != 536870912) {
        localView.dispatchSaveInstanceState(paramSparseArray);
      }
    }
  }
  
  void dispatchScreenStateChanged(int paramInt)
  {
    super.dispatchScreenStateChanged(paramInt);
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].dispatchScreenStateChanged(paramInt);
    }
  }
  
  public void dispatchSetActivated(boolean paramBoolean)
  {
    View[] arrayOfView = this.mChildren;
    int i = this.mChildrenCount;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].setActivated(paramBoolean);
    }
  }
  
  protected void dispatchSetPressed(boolean paramBoolean)
  {
    View[] arrayOfView = this.mChildren;
    int i = this.mChildrenCount;
    for (int j = 0; j < i; j++)
    {
      View localView = arrayOfView[j];
      if ((!paramBoolean) || ((!localView.isClickable()) && (!localView.isLongClickable()))) {
        localView.setPressed(paramBoolean);
      }
    }
  }
  
  public void dispatchSetSelected(boolean paramBoolean)
  {
    View[] arrayOfView = this.mChildren;
    int i = this.mChildrenCount;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].setSelected(paramBoolean);
    }
  }
  
  public void dispatchStartTemporaryDetach()
  {
    super.dispatchStartTemporaryDetach();
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].dispatchStartTemporaryDetach();
    }
  }
  
  public void dispatchSystemUiVisibilityChanged(int paramInt)
  {
    super.dispatchSystemUiVisibilityChanged(paramInt);
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].dispatchSystemUiVisibilityChanged(paramInt);
    }
  }
  
  protected void dispatchThawSelfOnly(SparseArray<Parcelable> paramSparseArray)
  {
    super.dispatchRestoreInstanceState(paramSparseArray);
  }
  
  boolean dispatchTooltipHoverEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    if (i != 7)
    {
      if ((i != 9) && (i == 10))
      {
        localObject1 = this.mTooltipHoverTarget;
        if (localObject1 != null)
        {
          ((View)localObject1).dispatchTooltipHoverEvent(paramMotionEvent);
          this.mTooltipHoverTarget = null;
        }
        else if (this.mTooltipHoveredSelf)
        {
          super.dispatchTooltipHoverEvent(paramMotionEvent);
          this.mTooltipHoveredSelf = false;
        }
      }
      return false;
    }
    Object localObject2 = null;
    Object localObject3 = null;
    int j = this.mChildrenCount;
    if (j != 0)
    {
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      ArrayList localArrayList = buildOrderedChildList();
      boolean bool;
      if ((localArrayList == null) && (isChildrenDrawingOrderEnabled())) {
        bool = true;
      } else {
        bool = false;
      }
      localObject2 = this.mChildren;
      for (int k = j - 1;; k--)
      {
        localObject1 = localObject3;
        if (k < 0) {
          break;
        }
        int m = getAndVerifyPreorderedIndex(j, k, bool);
        localObject1 = getAndVerifyPreorderedView(localArrayList, (View[])localObject2, m);
        if ((((View)localObject1).canReceivePointerEvents()) && (isTransformedTouchPointInView(f1, f2, (View)localObject1, null)) && (dispatchTooltipHoverEvent(paramMotionEvent, (View)localObject1))) {
          break;
        }
      }
      localObject2 = localObject1;
      if (localArrayList != null)
      {
        localArrayList.clear();
        localObject2 = localObject1;
      }
    }
    Object localObject1 = this.mTooltipHoverTarget;
    if (localObject1 != localObject2)
    {
      if (localObject1 != null)
      {
        paramMotionEvent.setAction(10);
        this.mTooltipHoverTarget.dispatchTooltipHoverEvent(paramMotionEvent);
        paramMotionEvent.setAction(i);
      }
      this.mTooltipHoverTarget = ((View)localObject2);
    }
    if (this.mTooltipHoverTarget != null)
    {
      if (this.mTooltipHoveredSelf)
      {
        this.mTooltipHoveredSelf = false;
        paramMotionEvent.setAction(10);
        super.dispatchTooltipHoverEvent(paramMotionEvent);
        paramMotionEvent.setAction(i);
      }
      return true;
    }
    this.mTooltipHoveredSelf = super.dispatchTooltipHoverEvent(paramMotionEvent);
    return this.mTooltipHoveredSelf;
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mInputEventConsistencyVerifier != null) {
      this.mInputEventConsistencyVerifier.onTouchEvent(paramMotionEvent, 1);
    }
    if ((paramMotionEvent.isTargetAccessibilityFocus()) && (isAccessibilityFocusedViewOrHost())) {
      paramMotionEvent.setTargetAccessibilityFocus(false);
    }
    boolean bool1 = false;
    if (onFilterTouchEventForSecurity(paramMotionEvent))
    {
      int i = paramMotionEvent.getAction();
      int j = i & 0xFF;
      if (j == 0)
      {
        cancelAndClearTouchTargets(paramMotionEvent);
        resetTouchState();
      }
      int k;
      if ((j != 0) && (this.mFirstTouchTarget == null))
      {
        k = 1;
      }
      else
      {
        if ((this.mGroupFlags & 0x80000) != 0) {
          m = 1;
        } else {
          m = 0;
        }
        if (m == 0)
        {
          bool2 = onInterceptTouchEvent(paramMotionEvent);
          paramMotionEvent.setAction(i);
        }
        else
        {
          bool2 = false;
        }
        k = bool2;
      }
      if ((k != 0) || (this.mFirstTouchTarget != null)) {
        paramMotionEvent.setTargetAccessibilityFocus(false);
      }
      boolean bool3;
      if ((!resetCancelNextUpFlag(this)) && (j != 3)) {
        bool3 = false;
      } else {
        bool3 = true;
      }
      int n;
      if ((this.mGroupFlags & 0x200000) != 0) {
        n = 1;
      } else {
        n = 0;
      }
      Object localObject1 = null;
      Object localObject2 = null;
      int m = 0;
      Object localObject3;
      Object localObject4;
      if ((!bool3) && (k == 0))
      {
        if (paramMotionEvent.isTargetAccessibilityFocus()) {
          localObject3 = findChildWithAccessibilityFocus();
        } else {
          localObject3 = null;
        }
        if ((j != 0) && ((n == 0) || (j != 5)) && (j != 7)) {
          break label729;
        }
        int i1 = paramMotionEvent.getActionIndex();
        int i2;
        if (n != 0) {
          i2 = 1 << paramMotionEvent.getPointerId(i1);
        } else {
          i2 = -1;
        }
        removePointersFromTouchTargets(i2);
        i = this.mChildrenCount;
        if ((0 == 0) && (i != 0))
        {
          float f1 = paramMotionEvent.getX(i1);
          float f2 = paramMotionEvent.getY(i1);
          localObject4 = buildTouchDispatchChildList();
          if ((localObject4 == null) && (isChildrenDrawingOrderEnabled())) {
            bool2 = true;
          } else {
            bool2 = false;
          }
          View[] arrayOfView = this.mChildren;
          localObject2 = null;
          int i3 = i - 1;
          for (localObject1 = localObject3; i3 >= 0; localObject1 = localObject3)
          {
            int i4 = getAndVerifyPreorderedIndex(i, i3, bool2);
            View localView = getAndVerifyPreorderedView((ArrayList)localObject4, arrayOfView, i4);
            localObject3 = localObject1;
            int i5 = i3;
            if (localObject1 != null)
            {
              if (localObject1 != localView)
              {
                localObject3 = localObject1;
              }
              else
              {
                localObject3 = null;
                i5 = i - 1;
              }
            }
            else if ((localView.canReceivePointerEvents()) && (isTransformedTouchPointInView(f1, f2, localView, null)))
            {
              localObject2 = getTouchTarget(localView);
              if (localObject2 != null)
              {
                ((TouchTarget)localObject2).pointerIdBits |= i2;
                localObject3 = localObject2;
                break label640;
              }
              resetCancelNextUpFlag(localView);
              if (dispatchTransformedTouchEvent(paramMotionEvent, false, localView, i2))
              {
                this.mLastTouchDownTime = paramMotionEvent.getDownTime();
                if (localObject4 != null) {
                  for (m = 0; m < i; m++) {
                    if (arrayOfView[i4] == this.mChildren[m])
                    {
                      this.mLastTouchDownIndex = m;
                      break;
                    }
                  }
                } else {
                  this.mLastTouchDownIndex = i4;
                }
                this.mLastTouchDownX = paramMotionEvent.getX();
                this.mLastTouchDownY = paramMotionEvent.getY();
                localObject3 = addTouchTarget(localView, i2);
                m = 1;
                break label640;
              }
              paramMotionEvent.setTargetAccessibilityFocus(false);
              i3 = i5;
            }
            else
            {
              paramMotionEvent.setTargetAccessibilityFocus(false);
              i3 = i5;
            }
            i3--;
          }
          localObject3 = localObject2;
          label640:
          if (localObject4 != null) {
            ((ArrayList)localObject4).clear();
          }
        }
        else
        {
          m = 0;
          localObject3 = localObject2;
        }
        localObject2 = localObject3;
        i = m;
        if (localObject3 != null) {
          break label735;
        }
        localObject2 = localObject3;
        i = m;
        if (this.mFirstTouchTarget == null) {
          break label735;
        }
        for (localObject2 = this.mFirstTouchTarget; ((TouchTarget)localObject2).next != null; localObject2 = ((TouchTarget)localObject2).next) {}
        ((TouchTarget)localObject2).pointerIdBits |= i2;
        i = m;
        break label735;
      }
      label729:
      i = 0;
      localObject2 = localObject1;
      label735:
      boolean bool2 = false;
      if (this.mFirstTouchTarget == null)
      {
        bool2 = dispatchTransformedTouchEvent(paramMotionEvent, bool3, null, -1);
      }
      else
      {
        localObject1 = null;
        localObject3 = this.mFirstTouchTarget;
        while (localObject3 != null)
        {
          localObject4 = ((TouchTarget)localObject3).next;
          if ((i != 0) && (localObject3 == localObject2))
          {
            bool1 = true;
          }
          else
          {
            boolean bool4;
            if ((!resetCancelNextUpFlag(((TouchTarget)localObject3).child)) && (k == 0)) {
              bool4 = false;
            } else {
              bool4 = true;
            }
            if (dispatchTransformedTouchEvent(paramMotionEvent, bool4, ((TouchTarget)localObject3).child, ((TouchTarget)localObject3).pointerIdBits)) {
              bool2 = true;
            }
            bool1 = bool2;
            if (bool4)
            {
              if (localObject1 == null) {
                this.mFirstTouchTarget = ((TouchTarget)localObject4);
              } else {
                ((TouchTarget)localObject1).next = ((TouchTarget)localObject4);
              }
              ((TouchTarget)localObject3).recycle();
              localObject3 = localObject4;
              continue;
            }
          }
          localObject1 = localObject3;
          localObject3 = localObject4;
          bool2 = bool1;
        }
      }
      if ((!bool3) && (j != 1) && (j != 7))
      {
        bool1 = bool2;
        if (n != 0)
        {
          bool1 = bool2;
          if (j == 6)
          {
            removePointersFromTouchTargets(1 << paramMotionEvent.getPointerId(paramMotionEvent.getActionIndex()));
            bool1 = bool2;
          }
        }
      }
      else
      {
        resetTouchState();
        bool1 = bool2;
      }
    }
    if ((!bool1) && (this.mInputEventConsistencyVerifier != null)) {
      this.mInputEventConsistencyVerifier.onUnhandledEvent(paramMotionEvent, 1);
    }
    return bool1;
  }
  
  public boolean dispatchTrackballEvent(MotionEvent paramMotionEvent)
  {
    if (this.mInputEventConsistencyVerifier != null) {
      this.mInputEventConsistencyVerifier.onTrackballEvent(paramMotionEvent, 1);
    }
    if ((this.mPrivateFlags & 0x12) == 18)
    {
      if (super.dispatchTrackballEvent(paramMotionEvent)) {
        return true;
      }
    }
    else
    {
      View localView = this.mFocused;
      if ((localView != null) && ((localView.mPrivateFlags & 0x10) == 16) && (this.mFocused.dispatchTrackballEvent(paramMotionEvent))) {
        return true;
      }
    }
    if (this.mInputEventConsistencyVerifier != null) {
      this.mInputEventConsistencyVerifier.onUnhandledEvent(paramMotionEvent, 1);
    }
    return false;
  }
  
  View dispatchUnhandledKeyEvent(KeyEvent paramKeyEvent)
  {
    if (!hasUnhandledKeyListener()) {
      return null;
    }
    Object localObject = buildOrderedChildList();
    if (localObject != null) {
      try
      {
        for (i = ((ArrayList)localObject).size() - 1; i >= 0; i--)
        {
          View localView = ((View)((ArrayList)localObject).get(i)).dispatchUnhandledKeyEvent(paramKeyEvent);
          if (localView != null) {
            return localView;
          }
        }
      }
      finally
      {
        ((ArrayList)localObject).clear();
      }
    }
    for (int i = getChildCount() - 1; i >= 0; i--)
    {
      localObject = getChildAt(i).dispatchUnhandledKeyEvent(paramKeyEvent);
      if (localObject != null) {
        return (View)localObject;
      }
    }
    if (onUnhandledKeyEvent(paramKeyEvent)) {
      return this;
    }
    return null;
  }
  
  public boolean dispatchUnhandledMove(View paramView, int paramInt)
  {
    View localView = this.mFocused;
    boolean bool;
    if ((localView != null) && (localView.dispatchUnhandledMove(paramView, paramInt))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  void dispatchViewAdded(View paramView)
  {
    onViewAdded(paramView);
    OnHierarchyChangeListener localOnHierarchyChangeListener = this.mOnHierarchyChangeListener;
    if (localOnHierarchyChangeListener != null) {
      localOnHierarchyChangeListener.onChildViewAdded(this, paramView);
    }
  }
  
  @UnsupportedAppUsage
  void dispatchViewRemoved(View paramView)
  {
    onViewRemoved(paramView);
    OnHierarchyChangeListener localOnHierarchyChangeListener = this.mOnHierarchyChangeListener;
    if (localOnHierarchyChangeListener != null) {
      localOnHierarchyChangeListener.onChildViewRemoved(this, paramView);
    }
  }
  
  boolean dispatchVisibilityAggregated(boolean paramBoolean)
  {
    paramBoolean = super.dispatchVisibilityAggregated(paramBoolean);
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++) {
      if (arrayOfView[j].getVisibility() == 0) {
        arrayOfView[j].dispatchVisibilityAggregated(paramBoolean);
      }
    }
    return paramBoolean;
  }
  
  protected void dispatchVisibilityChanged(View paramView, int paramInt)
  {
    super.dispatchVisibilityChanged(paramView, paramInt);
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].dispatchVisibilityChanged(paramView, paramInt);
    }
  }
  
  public void dispatchWindowFocusChanged(boolean paramBoolean)
  {
    super.dispatchWindowFocusChanged(paramBoolean);
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].dispatchWindowFocusChanged(paramBoolean);
    }
  }
  
  void dispatchWindowInsetsAnimationFinished(WindowInsetsAnimationListener.InsetsAnimation paramInsetsAnimation)
  {
    super.dispatchWindowInsetsAnimationFinished(paramInsetsAnimation);
    int i = getChildCount();
    for (int j = 0; j < i; j++) {
      getChildAt(j).dispatchWindowInsetsAnimationFinished(paramInsetsAnimation);
    }
  }
  
  WindowInsets dispatchWindowInsetsAnimationProgress(WindowInsets paramWindowInsets)
  {
    paramWindowInsets = super.dispatchWindowInsetsAnimationProgress(paramWindowInsets);
    int i = getChildCount();
    for (int j = 0; j < i; j++) {
      getChildAt(j).dispatchWindowInsetsAnimationProgress(paramWindowInsets);
    }
    return paramWindowInsets;
  }
  
  void dispatchWindowInsetsAnimationStarted(WindowInsetsAnimationListener.InsetsAnimation paramInsetsAnimation)
  {
    super.dispatchWindowInsetsAnimationStarted(paramInsetsAnimation);
    int i = getChildCount();
    for (int j = 0; j < i; j++) {
      getChildAt(j).dispatchWindowInsetsAnimationStarted(paramInsetsAnimation);
    }
  }
  
  public void dispatchWindowSystemUiVisiblityChanged(int paramInt)
  {
    super.dispatchWindowSystemUiVisiblityChanged(paramInt);
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].dispatchWindowSystemUiVisiblityChanged(paramInt);
    }
  }
  
  public void dispatchWindowVisibilityChanged(int paramInt)
  {
    super.dispatchWindowVisibilityChanged(paramInt);
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].dispatchWindowVisibilityChanged(paramInt);
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    return paramView.draw(paramCanvas, this, paramLong);
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    int i = this.mGroupFlags;
    if ((0x10000 & i) != 0)
    {
      if ((i & 0x2000) == 0)
      {
        View[] arrayOfView = this.mChildren;
        int j = this.mChildrenCount;
        for (i = 0; i < j; i++)
        {
          View localView = arrayOfView[i];
          if ((localView.mViewFlags & 0x400000) != 0) {
            localView.refreshDrawableState();
          }
        }
      }
      throw new IllegalStateException("addStateFromChildren cannot be enabled if a child has duplicateParentState set to true");
    }
  }
  
  @UnsupportedAppUsage
  protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    super.encodeProperties(paramViewHierarchyEncoder);
    paramViewHierarchyEncoder.addProperty("focus:descendantFocusability", getDescendantFocusability());
    paramViewHierarchyEncoder.addProperty("drawing:clipChildren", getClipChildren());
    paramViewHierarchyEncoder.addProperty("drawing:clipToPadding", getClipToPadding());
    paramViewHierarchyEncoder.addProperty("drawing:childrenDrawingOrderEnabled", isChildrenDrawingOrderEnabled());
    paramViewHierarchyEncoder.addProperty("drawing:persistentDrawingCache", getPersistentDrawingCache());
    int i = getChildCount();
    paramViewHierarchyEncoder.addProperty("meta:__childCount__", (short)i);
    for (int j = 0; j < i; j++)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("meta:__child__");
      localStringBuilder.append(j);
      paramViewHierarchyEncoder.addPropertyKey(localStringBuilder.toString());
      getChildAt(j).encode(paramViewHierarchyEncoder);
    }
  }
  
  public void endViewTransition(View paramView)
  {
    ArrayList localArrayList = this.mTransitioningViews;
    if (localArrayList != null)
    {
      localArrayList.remove(paramView);
      localArrayList = this.mDisappearingChildren;
      if ((localArrayList != null) && (localArrayList.contains(paramView)))
      {
        localArrayList.remove(paramView);
        localArrayList = this.mVisibilityChangingChildren;
        if ((localArrayList != null) && (localArrayList.contains(paramView)))
        {
          this.mVisibilityChangingChildren.remove(paramView);
        }
        else
        {
          if (paramView.mAttachInfo != null) {
            paramView.dispatchDetachedFromWindow();
          }
          if (paramView.mParent != null) {
            paramView.mParent = null;
          }
        }
        invalidate();
      }
    }
  }
  
  public View findFocus()
  {
    if (isFocused()) {
      return this;
    }
    View localView = this.mFocused;
    if (localView != null) {
      return localView.findFocus();
    }
    return null;
  }
  
  View findFrontmostDroppableChildAt(float paramFloat1, float paramFloat2, PointF paramPointF)
  {
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    i--;
    while (i >= 0)
    {
      View localView = arrayOfView[i];
      if ((localView.canAcceptDrag()) && (isTransformedTouchPointInView(paramFloat1, paramFloat2, localView, paramPointF))) {
        return localView;
      }
      i--;
    }
    return null;
  }
  
  public void findNamedViews(Map<String, View> paramMap)
  {
    if ((getVisibility() != 0) && (this.mGhostView == null)) {
      return;
    }
    super.findNamedViews(paramMap);
    int i = getChildCount();
    for (int j = 0; j < i; j++) {
      getChildAt(j).findNamedViews(paramMap);
    }
  }
  
  public View findViewByAccessibilityIdTraversal(int paramInt)
  {
    Object localObject = super.findViewByAccessibilityIdTraversal(paramInt);
    if (localObject != null) {
      return (View)localObject;
    }
    if (getAccessibilityNodeProvider() != null) {
      return null;
    }
    int i = this.mChildrenCount;
    localObject = this.mChildren;
    for (int j = 0; j < i; j++)
    {
      View localView = localObject[j].findViewByAccessibilityIdTraversal(paramInt);
      if (localView != null) {
        return localView;
      }
    }
    return null;
  }
  
  public View findViewByAutofillIdTraversal(int paramInt)
  {
    View localView = super.findViewByAutofillIdTraversal(paramInt);
    if (localView != null) {
      return localView;
    }
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++)
    {
      localView = arrayOfView[j].findViewByAutofillIdTraversal(paramInt);
      if (localView != null) {
        return localView;
      }
    }
    return null;
  }
  
  protected <T extends View> T findViewByPredicateTraversal(Predicate<View> paramPredicate, View paramView)
  {
    if (paramPredicate.test(this)) {
      return this;
    }
    View[] arrayOfView = this.mChildren;
    int i = this.mChildrenCount;
    for (int j = 0; j < i; j++)
    {
      View localView = arrayOfView[j];
      if ((localView != paramView) && ((localView.mPrivateFlags & 0x8) == 0))
      {
        localView = localView.findViewByPredicate(paramPredicate);
        if (localView != null) {
          return localView;
        }
      }
    }
    return null;
  }
  
  protected <T extends View> T findViewTraversal(int paramInt)
  {
    if (paramInt == this.mID) {
      return this;
    }
    View[] arrayOfView = this.mChildren;
    int i = this.mChildrenCount;
    for (int j = 0; j < i; j++)
    {
      View localView = arrayOfView[j];
      if ((localView.mPrivateFlags & 0x8) == 0)
      {
        localView = localView.findViewById(paramInt);
        if (localView != null) {
          return localView;
        }
      }
    }
    return null;
  }
  
  protected <T extends View> T findViewWithTagTraversal(Object paramObject)
  {
    if ((paramObject != null) && (paramObject.equals(this.mTag))) {
      return this;
    }
    View[] arrayOfView = this.mChildren;
    int i = this.mChildrenCount;
    for (int j = 0; j < i; j++)
    {
      View localView = arrayOfView[j];
      if ((localView.mPrivateFlags & 0x8) == 0)
      {
        localView = localView.findViewWithTag(paramObject);
        if (localView != null) {
          return localView;
        }
      }
    }
    return null;
  }
  
  public void findViewsWithText(ArrayList<View> paramArrayList, CharSequence paramCharSequence, int paramInt)
  {
    super.findViewsWithText(paramArrayList, paramCharSequence, paramInt);
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++)
    {
      View localView = arrayOfView[j];
      if (((localView.mViewFlags & 0xC) == 0) && ((localView.mPrivateFlags & 0x8) == 0)) {
        localView.findViewsWithText(paramArrayList, paramCharSequence, paramInt);
      }
    }
  }
  
  void finishAnimatingView(View paramView, Animation paramAnimation)
  {
    ArrayList localArrayList = this.mDisappearingChildren;
    if ((localArrayList != null) && (localArrayList.contains(paramView)))
    {
      localArrayList.remove(paramView);
      if (paramView.mAttachInfo != null) {
        paramView.dispatchDetachedFromWindow();
      }
      paramView.clearAnimation();
      this.mGroupFlags |= 0x4;
    }
    if ((paramAnimation != null) && (!paramAnimation.getFillAfter())) {
      paramView.clearAnimation();
    }
    if ((paramView.mPrivateFlags & 0x10000) == 65536)
    {
      paramView.onAnimationEnd();
      paramView.mPrivateFlags &= 0xFFFEFFFF;
      this.mGroupFlags |= 0x4;
    }
  }
  
  public View focusSearch(View paramView, int paramInt)
  {
    if (isRootNamespace()) {
      return FocusFinder.getInstance().findNextFocus(this, paramView, paramInt);
    }
    if (this.mParent != null) {
      return this.mParent.focusSearch(paramView, paramInt);
    }
    return null;
  }
  
  public void focusableViewAvailable(View paramView)
  {
    if ((this.mParent != null) && (getDescendantFocusability() != 393216) && ((this.mViewFlags & 0xC) == 0) && ((isFocusableInTouchMode()) || (!shouldBlockFocusForTouchscreen())) && ((!isFocused()) || (getDescendantFocusability() == 262144))) {
      this.mParent.focusableViewAvailable(paramView);
    }
  }
  
  public boolean gatherTransparentRegion(Region paramRegion)
  {
    int i = this.mPrivateFlags;
    boolean bool1 = false;
    int j;
    if ((i & 0x200) == 0) {
      j = 1;
    } else {
      j = 0;
    }
    if ((j != 0) && (paramRegion == null)) {
      return true;
    }
    super.gatherTransparentRegion(paramRegion);
    int k = this.mChildrenCount;
    int m = 1;
    i = 1;
    boolean bool2;
    if (k > 0)
    {
      ArrayList localArrayList = buildOrderedChildList();
      if ((localArrayList == null) && (isChildrenDrawingOrderEnabled())) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      View[] arrayOfView = this.mChildren;
      m = 0;
      while (m < k)
      {
        View localView = getAndVerifyPreorderedView(localArrayList, arrayOfView, getAndVerifyPreorderedIndex(k, m, bool2));
        int n;
        if ((localView.mViewFlags & 0xC) != 0)
        {
          n = i;
          if (localView.getAnimation() == null) {}
        }
        else
        {
          n = i;
          if (!localView.gatherTransparentRegion(paramRegion)) {
            n = 0;
          }
        }
        m++;
        i = n;
      }
      m = i;
      if (localArrayList != null)
      {
        localArrayList.clear();
        m = i;
      }
    }
    if (j == 0)
    {
      bool2 = bool1;
      if (m == 0) {}
    }
    else
    {
      bool2 = true;
    }
    return bool2;
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-2, -2);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(LayoutParams paramLayoutParams)
  {
    return paramLayoutParams;
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return ViewGroup.class.getName();
  }
  
  public View getChildAt(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.mChildrenCount)) {
      return this.mChildren[paramInt];
    }
    return null;
  }
  
  public int getChildCount()
  {
    return this.mChildrenCount;
  }
  
  public final int getChildDrawingOrder(int paramInt)
  {
    return getChildDrawingOrder(getChildCount(), paramInt);
  }
  
  protected int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    return paramInt2;
  }
  
  protected boolean getChildStaticTransformation(View paramView, Transformation paramTransformation)
  {
    return false;
  }
  
  Transformation getChildTransformation()
  {
    if (this.mChildTransformation == null) {
      this.mChildTransformation = new Transformation();
    }
    return this.mChildTransformation;
  }
  
  public boolean getChildVisibleRect(View paramView, Rect paramRect, Point paramPoint)
  {
    return getChildVisibleRect(paramView, paramRect, paramPoint, false);
  }
  
  public boolean getChildVisibleRect(View paramView, Rect paramRect, Point paramPoint, boolean paramBoolean)
  {
    RectF localRectF;
    if (this.mAttachInfo != null) {
      localRectF = this.mAttachInfo.mTmpTransformRect;
    } else {
      localRectF = new RectF();
    }
    localRectF.set(paramRect);
    if (!paramView.hasIdentityMatrix()) {
      paramView.getMatrix().mapRect(localRectF);
    }
    int i = paramView.mLeft - this.mScrollX;
    int j = paramView.mTop - this.mScrollY;
    localRectF.offset(i, j);
    if (paramPoint != null)
    {
      if (!paramView.hasIdentityMatrix())
      {
        float[] arrayOfFloat;
        if (this.mAttachInfo != null) {
          arrayOfFloat = this.mAttachInfo.mTmpTransformLocation;
        } else {
          arrayOfFloat = new float[2];
        }
        arrayOfFloat[0] = paramPoint.x;
        arrayOfFloat[1] = paramPoint.y;
        paramView.getMatrix().mapPoints(arrayOfFloat);
        paramPoint.x = Math.round(arrayOfFloat[0]);
        paramPoint.y = Math.round(arrayOfFloat[1]);
      }
      paramPoint.x += i;
      paramPoint.y += j;
    }
    j = this.mRight - this.mLeft;
    i = this.mBottom - this.mTop;
    boolean bool1 = true;
    boolean bool2;
    if (this.mParent != null)
    {
      bool2 = bool1;
      if ((this.mParent instanceof ViewGroup))
      {
        bool2 = bool1;
        if (!((ViewGroup)this.mParent).getClipChildren()) {}
      }
    }
    else
    {
      bool2 = localRectF.intersect(0.0F, 0.0F, j, i);
    }
    if (!paramBoolean)
    {
      bool1 = bool2;
      if (!bool2) {}
    }
    else
    {
      bool1 = bool2;
      if ((this.mGroupFlags & 0x22) == 34) {
        bool1 = localRectF.intersect(this.mPaddingLeft, this.mPaddingTop, j - this.mPaddingRight, i - this.mPaddingBottom);
      }
    }
    if (!paramBoolean)
    {
      bool2 = bool1;
      if (!bool1) {}
    }
    else
    {
      bool2 = bool1;
      if (this.mClipBounds != null) {
        bool2 = localRectF.intersect(this.mClipBounds.left, this.mClipBounds.top, this.mClipBounds.right, this.mClipBounds.bottom);
      }
    }
    paramRect.set((int)Math.floor(localRectF.left), (int)Math.floor(localRectF.top), (int)Math.ceil(localRectF.right), (int)Math.ceil(localRectF.bottom));
    if (!paramBoolean)
    {
      bool1 = bool2;
      if (!bool2) {}
    }
    else
    {
      bool1 = bool2;
      if (this.mParent != null) {
        if ((this.mParent instanceof ViewGroup)) {
          bool1 = ((ViewGroup)this.mParent).getChildVisibleRect(this, paramRect, paramPoint, paramBoolean);
        } else {
          bool1 = this.mParent.getChildVisibleRect(this, paramRect, paramPoint);
        }
      }
    }
    return bool1;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public boolean getClipChildren()
  {
    int i = this.mGroupFlags;
    boolean bool = true;
    if ((i & 0x1) == 0) {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public boolean getClipToPadding()
  {
    return hasBooleanFlag(2);
  }
  
  View getDeepestFocusedChild()
  {
    View localView;
    for (Object localObject = this;; localObject = localView)
    {
      localView = null;
      if (localObject == null) {
        break;
      }
      if (((View)localObject).isFocused()) {
        return (View)localObject;
      }
      if ((localObject instanceof ViewGroup)) {
        localView = ((ViewGroup)localObject).getFocusedChild();
      }
    }
    return null;
  }
  
  @ViewDebug.ExportedProperty(category="focus", mapping={@ViewDebug.IntToString(from=131072, to="FOCUS_BEFORE_DESCENDANTS"), @ViewDebug.IntToString(from=262144, to="FOCUS_AFTER_DESCENDANTS"), @ViewDebug.IntToString(from=393216, to="FOCUS_BLOCK_DESCENDANTS")})
  public int getDescendantFocusability()
  {
    return this.mGroupFlags & 0x60000;
  }
  
  public View getFocusedChild()
  {
    return this.mFocused;
  }
  
  public LayoutAnimationController getLayoutAnimation()
  {
    return this.mLayoutAnimationController;
  }
  
  public Animation.AnimationListener getLayoutAnimationListener()
  {
    return this.mAnimationListener;
  }
  
  public int getLayoutMode()
  {
    if (this.mLayoutMode == -1)
    {
      int i;
      if ((this.mParent instanceof ViewGroup)) {
        i = ((ViewGroup)this.mParent).getLayoutMode();
      } else {
        i = LAYOUT_MODE_DEFAULT;
      }
      setLayoutMode(i, false);
    }
    return this.mLayoutMode;
  }
  
  public LayoutTransition getLayoutTransition()
  {
    return this.mTransition;
  }
  
  public int getNestedScrollAxes()
  {
    return this.mNestedScrollAxes;
  }
  
  int getNumChildrenForAccessibility()
  {
    int i = 0;
    int j = 0;
    while (j < getChildCount())
    {
      View localView = getChildAt(j);
      int k;
      if (localView.includeForAccessibility())
      {
        k = i + 1;
      }
      else
      {
        k = i;
        if ((localView instanceof ViewGroup)) {
          k = i + ((ViewGroup)localView).getNumChildrenForAccessibility();
        }
      }
      j++;
      i = k;
    }
    return i;
  }
  
  public ViewGroupOverlay getOverlay()
  {
    if (this.mOverlay == null) {
      this.mOverlay = new ViewGroupOverlay(this.mContext, this);
    }
    return (ViewGroupOverlay)this.mOverlay;
  }
  
  @ViewDebug.ExportedProperty(category="drawing", mapping={@ViewDebug.IntToString(from=0, to="NONE"), @ViewDebug.IntToString(from=1, to="ANIMATION"), @ViewDebug.IntToString(from=2, to="SCROLLING"), @ViewDebug.IntToString(from=3, to="ALL")})
  @Deprecated
  public int getPersistentDrawingCache()
  {
    return this.mPersistentDrawingCache;
  }
  
  void getScrollIndicatorBounds(Rect paramRect)
  {
    super.getScrollIndicatorBounds(paramRect);
    int i;
    if ((this.mGroupFlags & 0x22) == 34) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0)
    {
      paramRect.left += this.mPaddingLeft;
      paramRect.right -= this.mPaddingRight;
      paramRect.top += this.mPaddingTop;
      paramRect.bottom -= this.mPaddingBottom;
    }
  }
  
  @ViewDebug.ExportedProperty(category="focus")
  public boolean getTouchscreenBlocksFocus()
  {
    boolean bool;
    if ((this.mGroupFlags & 0x4000000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  public View getTransientView(int paramInt)
  {
    List localList = this.mTransientViews;
    if ((localList != null) && (paramInt < localList.size())) {
      return (View)this.mTransientViews.get(paramInt);
    }
    return null;
  }
  
  @UnsupportedAppUsage
  public int getTransientViewCount()
  {
    List localList = this.mTransientIndices;
    int i;
    if (localList == null) {
      i = 0;
    } else {
      i = localList.size();
    }
    return i;
  }
  
  public int getTransientViewIndex(int paramInt)
  {
    if (paramInt >= 0)
    {
      List localList = this.mTransientIndices;
      if ((localList != null) && (paramInt < localList.size())) {
        return ((Integer)this.mTransientIndices.get(paramInt)).intValue();
      }
    }
    return -1;
  }
  
  void handleFocusGainInternal(int paramInt, Rect paramRect)
  {
    View localView = this.mFocused;
    if (localView != null)
    {
      localView.unFocus(this);
      this.mFocused = null;
      this.mFocusedInCluster = null;
    }
    super.handleFocusGainInternal(paramInt, paramRect);
  }
  
  boolean hasDefaultFocus()
  {
    boolean bool;
    if ((this.mDefaultFocus == null) && (!super.hasDefaultFocus())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasFocus()
  {
    boolean bool;
    if (((this.mPrivateFlags & 0x2) == 0) && (this.mFocused == null)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  boolean hasFocusable(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((this.mViewFlags & 0xC) != 0) {
      return false;
    }
    if (((paramBoolean1) || (getFocusable() != 16)) && (isFocusable())) {
      return true;
    }
    if (getDescendantFocusability() != 393216) {
      return hasFocusableChild(paramBoolean2);
    }
    return false;
  }
  
  boolean hasFocusableChild(boolean paramBoolean)
  {
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++)
    {
      View localView = arrayOfView[j];
      if (((paramBoolean) && (localView.hasExplicitFocusable())) || ((!paramBoolean) && (localView.hasFocusable()))) {
        return true;
      }
    }
    return false;
  }
  
  protected boolean hasHoveredChild()
  {
    boolean bool;
    if (this.mFirstHoverTarget != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean hasTransientState()
  {
    boolean bool;
    if ((this.mChildCountWithTransientState <= 0) && (!super.hasTransientState())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  boolean hasUnhandledKeyListener()
  {
    boolean bool;
    if ((this.mChildUnhandledKeyListeners <= 0) && (!super.hasUnhandledKeyListener())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  void incrementChildUnhandledKeyListeners()
  {
    this.mChildUnhandledKeyListeners += 1;
    if ((this.mChildUnhandledKeyListeners == 1) && ((this.mParent instanceof ViewGroup))) {
      ((ViewGroup)this.mParent).incrementChildUnhandledKeyListeners();
    }
  }
  
  public int indexOfChild(View paramView)
  {
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++) {
      if (arrayOfView[j] == paramView) {
        return j;
      }
    }
    return -1;
  }
  
  protected void internalSetPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.internalSetPadding(paramInt1, paramInt2, paramInt3, paramInt4);
    if ((this.mPaddingLeft | this.mPaddingTop | this.mPaddingRight | this.mPaddingBottom) != 0) {
      this.mGroupFlags |= 0x20;
    } else {
      this.mGroupFlags &= 0xFFFFFFDF;
    }
  }
  
  @Deprecated
  public final void invalidateChild(View paramView, Rect paramRect)
  {
    View.AttachInfo localAttachInfo = this.mAttachInfo;
    if ((localAttachInfo != null) && (localAttachInfo.mHardwareAccelerated))
    {
      onDescendantInvalidated(paramView, paramView);
      return;
    }
    ViewGroup localViewGroup = this;
    if (localAttachInfo != null)
    {
      int i;
      if ((paramView.mPrivateFlags & 0x40) != 0) {
        i = 1;
      } else {
        i = 0;
      }
      Matrix localMatrix1 = paramView.getMatrix();
      if (paramView.mLayerType != 0)
      {
        this.mPrivateFlags |= 0x80000000;
        this.mPrivateFlags &= 0xFFFF7FFF;
      }
      int[] arrayOfInt = localAttachInfo.mInvalidateChildLocation;
      arrayOfInt[0] = paramView.mLeft;
      arrayOfInt[1] = paramView.mTop;
      Object localObject;
      Matrix localMatrix2;
      if (localMatrix1.isIdentity())
      {
        localObject = localViewGroup;
        localMatrix2 = localMatrix1;
        if ((this.mGroupFlags & 0x800) == 0) {}
      }
      else
      {
        localObject = localAttachInfo.mTmpTransformRect;
        ((RectF)localObject).set(paramRect);
        if ((this.mGroupFlags & 0x800) != 0)
        {
          Transformation localTransformation = localAttachInfo.mTmpTransformation;
          if (getChildStaticTransformation(paramView, localTransformation))
          {
            localMatrix2 = localAttachInfo.mTmpMatrix;
            localMatrix2.set(localTransformation.getMatrix());
            paramView = localMatrix2;
            if (!localMatrix1.isIdentity())
            {
              localMatrix2.preConcat(localMatrix1);
              paramView = localMatrix2;
            }
          }
          else
          {
            paramView = localMatrix1;
          }
        }
        else
        {
          paramView = localMatrix1;
        }
        paramView.mapRect((RectF)localObject);
        paramRect.set((int)Math.floor(((RectF)localObject).left), (int)Math.floor(((RectF)localObject).top), (int)Math.ceil(((RectF)localObject).right), (int)Math.ceil(((RectF)localObject).bottom));
        localMatrix2 = localMatrix1;
        localObject = localViewGroup;
      }
      for (;;)
      {
        paramView = null;
        if ((localObject instanceof View)) {
          paramView = (View)localObject;
        }
        if (i != 0) {
          if (paramView != null) {
            paramView.mPrivateFlags |= 0x40;
          } else if ((localObject instanceof ViewRootImpl)) {
            ((ViewRootImpl)localObject).mIsAnimating = true;
          }
        }
        if ((paramView != null) && ((paramView.mPrivateFlags & 0x200000) != 2097152)) {
          paramView.mPrivateFlags = (paramView.mPrivateFlags & 0xFFDFFFFF | 0x200000);
        }
        localObject = ((ViewParent)localObject).invalidateChildInParent(arrayOfInt, paramRect);
        if (paramView != null)
        {
          localMatrix1 = paramView.getMatrix();
          if (!localMatrix1.isIdentity())
          {
            paramView = localAttachInfo.mTmpTransformRect;
            paramView.set(paramRect);
            localMatrix1.mapRect(paramView);
            paramRect.set((int)Math.floor(paramView.left), (int)Math.floor(paramView.top), (int)Math.ceil(paramView.right), (int)Math.ceil(paramView.bottom));
          }
          else {}
        }
        if (localObject == null) {
          break;
        }
      }
    }
  }
  
  @Deprecated
  public ViewParent invalidateChildInParent(int[] paramArrayOfInt, Rect paramRect)
  {
    if ((this.mPrivateFlags & 0x8020) != 0)
    {
      int i = this.mGroupFlags;
      if ((i & 0x90) != 128)
      {
        paramRect.offset(paramArrayOfInt[0] - this.mScrollX, paramArrayOfInt[1] - this.mScrollY);
        if ((this.mGroupFlags & 0x1) == 0) {
          paramRect.union(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
        }
        i = this.mLeft;
        int j = this.mTop;
        if (((this.mGroupFlags & 0x1) == 1) && (!paramRect.intersect(0, 0, this.mRight - i, this.mBottom - j))) {
          paramRect.setEmpty();
        }
        paramArrayOfInt[0] = i;
        paramArrayOfInt[1] = j;
      }
      else
      {
        if ((i & 0x1) == 1) {
          paramRect.set(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
        } else {
          paramRect.union(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
        }
        paramArrayOfInt[0] = this.mLeft;
        paramArrayOfInt[1] = this.mTop;
        this.mPrivateFlags &= 0xFFFFFFDF;
      }
      this.mPrivateFlags &= 0xFFFF7FFF;
      if (this.mLayerType != 0) {
        this.mPrivateFlags |= 0x80000000;
      }
      return this.mParent;
    }
    return null;
  }
  
  void invalidateInheritedLayoutMode(int paramInt)
  {
    int i = this.mLayoutMode;
    if ((i != -1) && (i != paramInt) && (!hasBooleanFlag(8388608)))
    {
      setLayoutMode(-1, false);
      i = 0;
      int j = getChildCount();
      while (i < j)
      {
        getChildAt(i).invalidateInheritedLayoutMode(paramInt);
        i++;
      }
      return;
    }
  }
  
  @Deprecated
  public boolean isAlwaysDrawnWithCacheEnabled()
  {
    boolean bool;
    if ((this.mGroupFlags & 0x4000) == 16384) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @Deprecated
  public boolean isAnimationCacheEnabled()
  {
    boolean bool;
    if ((this.mGroupFlags & 0x40) == 64) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  protected boolean isChildrenDrawingOrderEnabled()
  {
    boolean bool;
    if ((this.mGroupFlags & 0x400) == 1024) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @Deprecated
  protected boolean isChildrenDrawnWithCacheEnabled()
  {
    boolean bool;
    if ((this.mGroupFlags & 0x8000) == 32768) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  boolean isLayoutModeOptical()
  {
    int i = this.mLayoutMode;
    boolean bool = true;
    if (i != 1) {
      bool = false;
    }
    return bool;
  }
  
  public boolean isLayoutSuppressed()
  {
    return this.mSuppressLayout;
  }
  
  public boolean isMotionEventSplittingEnabled()
  {
    boolean bool;
    if ((this.mGroupFlags & 0x200000) == 2097152) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isShowingContextMenuWithCoords()
  {
    boolean bool;
    if ((this.mGroupFlags & 0x20000000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  protected boolean isTransformedTouchPointInView(float paramFloat1, float paramFloat2, View paramView, PointF paramPointF)
  {
    float[] arrayOfFloat = getTempPoint();
    arrayOfFloat[0] = paramFloat1;
    arrayOfFloat[1] = paramFloat2;
    transformPointToViewLocal(arrayOfFloat, paramView);
    boolean bool = paramView.pointInView(arrayOfFloat[0], arrayOfFloat[1]);
    if ((bool) && (paramPointF != null)) {
      paramPointF.set(arrayOfFloat[0], arrayOfFloat[1]);
    }
    return bool;
  }
  
  public boolean isTransitionGroup()
  {
    int i = this.mGroupFlags;
    boolean bool1 = false;
    boolean bool2 = false;
    if ((0x2000000 & i) != 0)
    {
      if ((i & 0x1000000) != 0) {
        bool2 = true;
      }
      return bool2;
    }
    ViewOutlineProvider localViewOutlineProvider = getOutlineProvider();
    if ((getBackground() == null) && (getTransitionName() == null))
    {
      bool2 = bool1;
      if (localViewOutlineProvider != null)
      {
        bool2 = bool1;
        if (localViewOutlineProvider == ViewOutlineProvider.BACKGROUND) {}
      }
    }
    else
    {
      bool2 = true;
    }
    return bool2;
  }
  
  boolean isViewTransitioning(View paramView)
  {
    ArrayList localArrayList = this.mTransitioningViews;
    boolean bool;
    if ((localArrayList != null) && (localArrayList.contains(paramView))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    View[] arrayOfView = this.mChildren;
    int i = this.mChildrenCount;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].jumpDrawablesToCurrentState();
    }
  }
  
  public final void layout(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!this.mSuppressLayout)
    {
      LayoutTransition localLayoutTransition = this.mTransition;
      if ((localLayoutTransition == null) || (!localLayoutTransition.isChangingLayout()))
      {
        localLayoutTransition = this.mTransition;
        if (localLayoutTransition != null) {
          localLayoutTransition.layoutChange(this);
        }
        super.layout(paramInt1, paramInt2, paramInt3, paramInt4);
        return;
      }
    }
    this.mLayoutCalledWhileSuppressed = true;
  }
  
  @UnsupportedAppUsage
  public void makeOptionalFitsSystemWindows()
  {
    super.makeOptionalFitsSystemWindows();
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].makeOptionalFitsSystemWindows();
    }
  }
  
  protected void measureChild(View paramView, int paramInt1, int paramInt2)
  {
    LayoutParams localLayoutParams = paramView.getLayoutParams();
    paramView.measure(getChildMeasureSpec(paramInt1, this.mPaddingLeft + this.mPaddingRight, localLayoutParams.width), getChildMeasureSpec(paramInt2, this.mPaddingTop + this.mPaddingBottom, localLayoutParams.height));
  }
  
  protected void measureChildWithMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    MarginLayoutParams localMarginLayoutParams = (MarginLayoutParams)paramView.getLayoutParams();
    paramView.measure(getChildMeasureSpec(paramInt1, this.mPaddingLeft + this.mPaddingRight + localMarginLayoutParams.leftMargin + localMarginLayoutParams.rightMargin + paramInt2, localMarginLayoutParams.width), getChildMeasureSpec(paramInt3, this.mPaddingTop + this.mPaddingBottom + localMarginLayoutParams.topMargin + localMarginLayoutParams.bottomMargin + paramInt4, localMarginLayoutParams.height));
  }
  
  protected void measureChildren(int paramInt1, int paramInt2)
  {
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++)
    {
      View localView = arrayOfView[j];
      if ((localView.mViewFlags & 0xC) != 8) {
        measureChild(localView, paramInt1, paramInt2);
      }
    }
  }
  
  boolean notifyChildOfDragStart(View paramView)
  {
    float f1 = this.mCurrentDragStartEvent.mX;
    float f2 = this.mCurrentDragStartEvent.mY;
    float[] arrayOfFloat = getTempPoint();
    arrayOfFloat[0] = f1;
    arrayOfFloat[1] = f2;
    transformPointToViewLocal(arrayOfFloat, paramView);
    DragEvent localDragEvent = this.mCurrentDragStartEvent;
    localDragEvent.mX = arrayOfFloat[0];
    localDragEvent.mY = arrayOfFloat[1];
    boolean bool = paramView.dispatchDragEvent(localDragEvent);
    localDragEvent = this.mCurrentDragStartEvent;
    localDragEvent.mX = f1;
    localDragEvent.mY = f2;
    localDragEvent.mEventHandlerWasCalled = false;
    if (bool)
    {
      this.mChildrenInterestedInDrag.add(paramView);
      if (!paramView.canAcceptDrag())
      {
        paramView.mPrivateFlags2 |= 0x1;
        paramView.refreshDrawableState();
      }
    }
    return bool;
  }
  
  public void notifySubtreeAccessibilityStateChanged(View paramView1, View paramView2, int paramInt)
  {
    if (getAccessibilityLiveRegion() != 0) {
      notifyViewAccessibilityStateChangedIfNeeded(1);
    } else if (this.mParent != null) {
      try
      {
        this.mParent.notifySubtreeAccessibilityStateChanged(this, paramView2, paramInt);
      }
      catch (AbstractMethodError paramView1)
      {
        paramView2 = new StringBuilder();
        paramView2.append(this.mParent.getClass().getSimpleName());
        paramView2.append(" does not fully implement ViewParent");
        Log.e("View", paramView2.toString(), paramView1);
      }
    }
  }
  
  public void notifySubtreeAccessibilityStateChangedIfNeeded()
  {
    if ((AccessibilityManager.getInstance(this.mContext).isEnabled()) && (this.mAttachInfo != null))
    {
      if ((getImportantForAccessibility() != 4) && (!isImportantForAccessibility()) && (getChildCount() > 0))
      {
        ViewParent localViewParent = getParentForAccessibility();
        if ((localViewParent instanceof View))
        {
          ((View)localViewParent).notifySubtreeAccessibilityStateChangedIfNeeded();
          return;
        }
      }
      super.notifySubtreeAccessibilityStateChangedIfNeeded();
      return;
    }
  }
  
  @UnsupportedAppUsage
  public void offsetChildrenTopAndBottom(int paramInt)
  {
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      View localView = arrayOfView[k];
      localView.mTop += paramInt;
      localView.mBottom += paramInt;
      if (localView.mRenderNode != null)
      {
        j = 1;
        localView.mRenderNode.offsetTopAndBottom(paramInt);
      }
    }
    if (j != 0) {
      invalidateViewProperty(false, false);
    }
    notifySubtreeAccessibilityStateChangedIfNeeded();
  }
  
  public final void offsetDescendantRectToMyCoords(View paramView, Rect paramRect)
  {
    offsetRectBetweenParentAndChild(paramView, paramRect, true, false);
  }
  
  void offsetRectBetweenParentAndChild(View paramView, Rect paramRect, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramView == this) {
      return;
    }
    Object localObject = paramView.mParent;
    View localView = paramView;
    for (paramView = (View)localObject; (paramView != null) && ((paramView instanceof View)) && (paramView != this); paramView = localView.mParent)
    {
      if (paramBoolean1)
      {
        paramRect.offset(localView.mLeft - localView.mScrollX, localView.mTop - localView.mScrollY);
        if (paramBoolean2)
        {
          localView = (View)paramView;
          if (!paramRect.intersect(0, 0, localView.mRight - localView.mLeft, localView.mBottom - localView.mTop)) {
            paramRect.setEmpty();
          }
        }
      }
      else
      {
        if (paramBoolean2)
        {
          localObject = (View)paramView;
          if (!paramRect.intersect(0, 0, ((View)localObject).mRight - ((View)localObject).mLeft, ((View)localObject).mBottom - ((View)localObject).mTop)) {
            paramRect.setEmpty();
          }
        }
        paramRect.offset(localView.mScrollX - localView.mLeft, localView.mScrollY - localView.mTop);
      }
      localView = (View)paramView;
    }
    if (paramView == this)
    {
      if (paramBoolean1) {
        paramRect.offset(localView.mLeft - localView.mScrollX, localView.mTop - localView.mScrollY);
      } else {
        paramRect.offset(localView.mScrollX - localView.mLeft, localView.mScrollY - localView.mTop);
      }
      return;
    }
    throw new IllegalArgumentException("parameter must be a descendant of this view");
  }
  
  public final void offsetRectIntoDescendantCoords(View paramView, Rect paramRect)
  {
    offsetRectBetweenParentAndChild(paramView, paramRect, false, false);
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    clearCachedLayoutMode();
  }
  
  @UnsupportedAppUsage
  protected void onChildVisibilityChanged(View paramView, int paramInt1, int paramInt2)
  {
    if (Android_View_ViewGroup.Extension.get().getExtension() != null) {
      ((Android_View_ViewGroup.Interface)Android_View_ViewGroup.Extension.get().getExtension().asInterface()).onChildVisibilityChanged(this, paramView, paramInt1, paramInt2);
    } else {
      originalOnChildVisibilityChanged(paramView, paramInt1, paramInt2);
    }
  }
  
  protected int[] onCreateDrawableState(int paramInt)
  {
    if ((this.mGroupFlags & 0x2000) == 0) {
      return super.onCreateDrawableState(paramInt);
    }
    int i = 0;
    int j = getChildCount();
    int k = 0;
    Object localObject1;
    while (k < j)
    {
      localObject1 = getChildAt(k).getDrawableState();
      int m = i;
      if (localObject1 != null) {
        m = i + localObject1.length;
      }
      k++;
      i = m;
    }
    Object localObject2 = super.onCreateDrawableState(paramInt + i);
    paramInt = 0;
    while (paramInt < j)
    {
      int[] arrayOfInt = getChildAt(paramInt).getDrawableState();
      localObject1 = localObject2;
      if (arrayOfInt != null) {
        localObject1 = mergeDrawableStates((int[])localObject2, arrayOfInt);
      }
      paramInt++;
      localObject2 = localObject1;
    }
    return (int[])localObject2;
  }
  
  protected void onDebugDraw(Canvas paramCanvas)
  {
    Paint localPaint = getDebugPaint();
    localPaint.setColor(-65536);
    localPaint.setStyle(Paint.Style.STROKE);
    Object localObject;
    for (int i = 0; i < getChildCount(); i++)
    {
      View localView = getChildAt(i);
      if (localView.getVisibility() != 8)
      {
        localObject = localView.getOpticalInsets();
        j = localView.getLeft();
        k = ((Insets)localObject).left;
        int m = localView.getTop();
        drawRect(paramCanvas, localPaint, k + j, ((Insets)localObject).top + m, localView.getRight() - ((Insets)localObject).right - 1, localView.getBottom() - ((Insets)localObject).bottom - 1);
      }
    }
    localPaint.setColor(Color.argb(63, 255, 0, 255));
    localPaint.setStyle(Paint.Style.FILL);
    onDebugDrawMargins(paramCanvas, localPaint);
    localPaint.setColor(DEBUG_CORNERS_COLOR);
    localPaint.setStyle(Paint.Style.FILL);
    int k = dipsToPixels(8);
    int j = dipsToPixels(1);
    for (i = 0; i < getChildCount(); i++)
    {
      localObject = getChildAt(i);
      if (((View)localObject).getVisibility() != 8) {
        drawRectCorners(paramCanvas, ((View)localObject).getLeft(), ((View)localObject).getTop(), ((View)localObject).getRight(), ((View)localObject).getBottom(), localPaint, k, j);
      }
    }
  }
  
  protected void onDebugDrawMargins(Canvas paramCanvas, Paint paramPaint)
  {
    for (int i = 0; i < getChildCount(); i++)
    {
      View localView = getChildAt(i);
      localView.getLayoutParams().onDebugDraw(localView, paramCanvas, paramPaint);
    }
  }
  
  public void onDescendantInvalidated(View paramView1, View paramView2)
  {
    this.mPrivateFlags |= paramView2.mPrivateFlags & 0x40;
    if ((paramView2.mPrivateFlags & 0xFFDFFFFF) != 0)
    {
      this.mPrivateFlags = (this.mPrivateFlags & 0xFFDFFFFF | 0x200000);
      this.mPrivateFlags &= 0xFFFF7FFF;
    }
    if (this.mLayerType == 1)
    {
      this.mPrivateFlags |= 0x80200000;
      paramView2 = this;
    }
    if (this.mParent != null) {
      this.mParent.onDescendantInvalidated(this, paramView2);
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    clearCachedLayoutMode();
  }
  
  @UnsupportedAppUsage
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    if (getAccessibilityNodeProvider() != null) {
      return;
    }
    if (this.mAttachInfo != null)
    {
      ArrayList localArrayList = this.mAttachInfo.mTempArrayList;
      localArrayList.clear();
      addChildrenForAccessibility(localArrayList);
      int i = localArrayList.size();
      for (int j = 0; j < i; j++) {
        paramAccessibilityNodeInfo.addChildUnchecked((View)localArrayList.get(j));
      }
      localArrayList.clear();
    }
  }
  
  public boolean onInterceptHoverEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.isFromSource(8194))
    {
      int i = paramMotionEvent.getAction();
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      if (((i == 7) || (i == 9)) && (isOnScrollbar(f1, f2))) {
        return true;
      }
    }
    return false;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    return (paramMotionEvent.isFromSource(8194)) && (paramMotionEvent.getAction() == 0) && (paramMotionEvent.isButtonPressed(1)) && (isOnScrollbarThumb(paramMotionEvent.getX(), paramMotionEvent.getY()));
  }
  
  protected abstract void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    return dispatchNestedFling(paramFloat1, paramFloat2, paramBoolean);
  }
  
  public boolean onNestedPreFling(View paramView, float paramFloat1, float paramFloat2)
  {
    return dispatchNestedPreFling(paramFloat1, paramFloat2);
  }
  
  public boolean onNestedPrePerformAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
  {
    return false;
  }
  
  public void onNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt, null);
  }
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt)
  {
    this.mNestedScrollAxes = paramInt;
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    int i = this.mChildrenCount;
    int j;
    int k;
    if ((paramInt & 0x2) != 0)
    {
      j = 0;
      k = 1;
    }
    else
    {
      j = i - 1;
      k = -1;
      i = -1;
    }
    View[] arrayOfView = this.mChildren;
    while (j != i)
    {
      View localView = arrayOfView[j];
      if (((localView.mViewFlags & 0xC) == 0) && (localView.requestFocus(paramInt, paramRect))) {
        return true;
      }
      j += k;
    }
    return false;
  }
  
  public boolean onRequestSendAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    if (this.mAccessibilityDelegate != null) {
      return this.mAccessibilityDelegate.onRequestSendAccessibilityEvent(this, paramView, paramAccessibilityEvent);
    }
    return onRequestSendAccessibilityEventInternal(paramView, paramAccessibilityEvent);
  }
  
  public boolean onRequestSendAccessibilityEventInternal(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    return true;
  }
  
  public PointerIcon onResolvePointerIcon(MotionEvent paramMotionEvent, int paramInt)
  {
    float f1 = paramMotionEvent.getX(paramInt);
    float f2 = paramMotionEvent.getY(paramInt);
    if ((!isOnScrollbarThumb(f1, f2)) && (!isDraggingScrollBar()))
    {
      int i = this.mChildrenCount;
      if (i != 0)
      {
        ArrayList localArrayList = buildOrderedChildList();
        boolean bool;
        if ((localArrayList == null) && (isChildrenDrawingOrderEnabled())) {
          bool = true;
        } else {
          bool = false;
        }
        View[] arrayOfView = this.mChildren;
        for (int j = i - 1; j >= 0; j--)
        {
          Object localObject = getAndVerifyPreorderedView(localArrayList, arrayOfView, getAndVerifyPreorderedIndex(i, j, bool));
          if ((((View)localObject).canReceivePointerEvents()) && (isTransformedTouchPointInView(f1, f2, (View)localObject, null)))
          {
            localObject = dispatchResolvePointerIcon(paramMotionEvent, paramInt, (View)localObject);
            if (localObject != null)
            {
              if (localArrayList != null) {
                localArrayList.clear();
              }
              return (PointerIcon)localObject;
            }
          }
        }
        if (localArrayList != null) {
          localArrayList.clear();
        }
      }
      return super.onResolvePointerIcon(paramMotionEvent, paramInt);
    }
    return PointerIcon.getSystemIcon(this.mContext, 1000);
  }
  
  protected void onSetLayoutParams(View paramView, LayoutParams paramLayoutParams)
  {
    requestLayout();
  }
  
  public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt)
  {
    return false;
  }
  
  public void onStopNestedScroll(View paramView)
  {
    stopNestedScroll();
    this.mNestedScrollAxes = 0;
  }
  
  public void onViewAdded(View paramView) {}
  
  public void onViewRemoved(View paramView) {}
  
  void originalAddInArray(View paramView, int paramInt)
  {
    View[] arrayOfView1 = this.mChildren;
    int i = this.mChildrenCount;
    int j = arrayOfView1.length;
    if (paramInt == i)
    {
      View[] arrayOfView2 = arrayOfView1;
      if (j == i)
      {
        this.mChildren = new View[j + 12];
        System.arraycopy(arrayOfView1, 0, this.mChildren, 0, j);
        arrayOfView2 = this.mChildren;
      }
      paramInt = this.mChildrenCount;
      this.mChildrenCount = (paramInt + 1);
      arrayOfView2[paramInt] = paramView;
    }
    else
    {
      if (paramInt >= i) {
        break label189;
      }
      if (j == i)
      {
        this.mChildren = new View[j + 12];
        System.arraycopy(arrayOfView1, 0, this.mChildren, 0, paramInt);
        System.arraycopy(arrayOfView1, paramInt, this.mChildren, paramInt + 1, i - paramInt);
        arrayOfView1 = this.mChildren;
      }
      else
      {
        System.arraycopy(arrayOfView1, paramInt, arrayOfView1, paramInt + 1, i - paramInt);
      }
      arrayOfView1[paramInt] = paramView;
      this.mChildrenCount += 1;
      j = this.mLastTouchDownIndex;
      if (j >= paramInt) {
        this.mLastTouchDownIndex = (j + 1);
      }
    }
    return;
    label189:
    paramView = new StringBuilder();
    paramView.append("index=");
    paramView.append(paramInt);
    paramView.append(" count=");
    paramView.append(i);
    throw new IndexOutOfBoundsException(paramView.toString());
  }
  
  void originalOnChildVisibilityChanged(View paramView, int paramInt1, int paramInt2)
  {
    Object localObject = this.mTransition;
    if (localObject != null) {
      if (paramInt2 == 0)
      {
        ((LayoutTransition)localObject).showChild(this, paramView, paramInt1);
      }
      else
      {
        ((LayoutTransition)localObject).hideChild(this, paramView, paramInt2);
        localObject = this.mTransitioningViews;
        if ((localObject != null) && (((ArrayList)localObject).contains(paramView)))
        {
          if (this.mVisibilityChangingChildren == null) {
            this.mVisibilityChangingChildren = new ArrayList();
          }
          this.mVisibilityChangingChildren.add(paramView);
          addDisappearingView(paramView);
        }
      }
    }
    if ((paramInt2 == 0) && (this.mCurrentDragStartEvent != null) && (!this.mChildrenInterestedInDrag.contains(paramView))) {
      notifyChildOfDragStart(paramView);
    }
  }
  
  void originalRemoveFromArray(int paramInt)
  {
    View[] arrayOfView = this.mChildren;
    ArrayList localArrayList = this.mTransitioningViews;
    if ((localArrayList == null) || (!localArrayList.contains(arrayOfView[paramInt]))) {
      arrayOfView[paramInt].mParent = null;
    }
    int i = this.mChildrenCount;
    if (paramInt == i - 1)
    {
      i = this.mChildrenCount - 1;
      this.mChildrenCount = i;
      arrayOfView[i] = null;
    }
    else
    {
      if ((paramInt < 0) || (paramInt >= i)) {
        break label151;
      }
      System.arraycopy(arrayOfView, paramInt + 1, arrayOfView, paramInt, i - paramInt - 1);
      i = this.mChildrenCount - 1;
      this.mChildrenCount = i;
      arrayOfView[i] = null;
    }
    i = this.mLastTouchDownIndex;
    if (i == paramInt)
    {
      this.mLastTouchDownTime = 0L;
      this.mLastTouchDownIndex = -1;
    }
    else if (i > paramInt)
    {
      this.mLastTouchDownIndex = (i - 1);
    }
    return;
    label151:
    throw new IndexOutOfBoundsException();
  }
  
  void originalRemoveFromArray(int paramInt1, int paramInt2)
  {
    View[] arrayOfView = this.mChildren;
    int i = this.mChildrenCount;
    paramInt1 = Math.max(0, paramInt1);
    int j = Math.min(i, paramInt1 + paramInt2);
    if (paramInt1 == j) {
      return;
    }
    if (j == i)
    {
      for (paramInt2 = paramInt1; paramInt2 < j; paramInt2++)
      {
        arrayOfView[paramInt2].mParent = null;
        arrayOfView[paramInt2] = null;
      }
    }
    else
    {
      for (paramInt2 = paramInt1; paramInt2 < j; paramInt2++) {
        arrayOfView[paramInt2].mParent = null;
      }
      System.arraycopy(arrayOfView, j, arrayOfView, paramInt1, i - j);
      for (paramInt2 = i - (j - paramInt1); paramInt2 < i; paramInt2++) {
        arrayOfView[paramInt2] = null;
      }
    }
    this.mChildrenCount -= j - paramInt1;
  }
  
  boolean originalResolveLayoutDirection()
  {
    boolean bool = super.resolveLayoutDirection();
    if (bool)
    {
      int i = getChildCount();
      for (int j = 0; j < i; j++)
      {
        View localView = getChildAt(j);
        if (localView.isLayoutDirectionInherited()) {
          localView.resolveLayoutDirection();
        }
      }
    }
    return bool;
  }
  
  protected boolean pointInHoveredChild(MotionEvent paramMotionEvent)
  {
    if (this.mFirstHoverTarget != null) {
      return isTransformedTouchPointInView(paramMotionEvent.getX(), paramMotionEvent.getY(), this.mFirstHoverTarget.child, null);
    }
    return false;
  }
  
  public void recomputeViewAttributes(View paramView)
  {
    if ((this.mAttachInfo != null) && (!this.mAttachInfo.mRecomputeGlobalAttributes))
    {
      paramView = this.mParent;
      if (paramView != null) {
        paramView.recomputeViewAttributes(this);
      }
    }
  }
  
  public void removeAllViews()
  {
    removeAllViewsInLayout();
    requestLayout();
    invalidate(true);
  }
  
  public void removeAllViewsInLayout()
  {
    int i = this.mChildrenCount;
    if (i <= 0) {
      return;
    }
    View[] arrayOfView = this.mChildren;
    this.mChildrenCount = 0;
    View localView1 = this.mFocused;
    int j;
    if (this.mAttachInfo != null) {
      j = 1;
    } else {
      j = 0;
    }
    int k = 0;
    needGlobalAttributesUpdate(false);
    i--;
    while (i >= 0)
    {
      localView2 = arrayOfView[i];
      Object localObject = this.mTransition;
      if (localObject != null) {
        ((LayoutTransition)localObject).removeChild(this, localView2);
      }
      if (localView2 == localView1)
      {
        localView2.unFocus(null);
        k = 1;
      }
      localView2.clearAccessibilityFocus();
      cancelTouchTarget(localView2);
      cancelHoverTarget(localView2);
      if (localView2.getAnimation() == null)
      {
        localObject = this.mTransitioningViews;
        if ((localObject == null) || (!((ArrayList)localObject).contains(localView2)))
        {
          if (j == 0) {
            break label163;
          }
          localView2.dispatchDetachedFromWindow();
          break label163;
        }
      }
      addDisappearingView(localView2);
      label163:
      if (localView2.hasTransientState()) {
        childHasTransientStateChanged(localView2, false);
      }
      dispatchViewRemoved(localView2);
      localView2.mParent = null;
      arrayOfView[i] = null;
      i--;
    }
    View localView2 = this.mDefaultFocus;
    if (localView2 != null) {
      clearDefaultFocus(localView2);
    }
    localView2 = this.mFocusedInCluster;
    if (localView2 != null) {
      clearFocusedInCluster(localView2);
    }
    if (k != 0)
    {
      clearChildFocus(localView1);
      if (!rootViewRequestFocus()) {
        notifyGlobalFocusCleared(localView1);
      }
    }
  }
  
  protected void removeDetachedView(View paramView, boolean paramBoolean)
  {
    Object localObject = this.mTransition;
    if (localObject != null) {
      ((LayoutTransition)localObject).removeChild(this, paramView);
    }
    if (paramView == this.mFocused) {
      paramView.clearFocus();
    }
    if (paramView == this.mDefaultFocus) {
      clearDefaultFocus(paramView);
    }
    if (paramView == this.mFocusedInCluster) {
      clearFocusedInCluster(paramView);
    }
    paramView.clearAccessibilityFocus();
    cancelTouchTarget(paramView);
    cancelHoverTarget(paramView);
    if ((!paramBoolean) || (paramView.getAnimation() == null))
    {
      localObject = this.mTransitioningViews;
      if ((localObject == null) || (!((ArrayList)localObject).contains(paramView))) {}
    }
    else
    {
      addDisappearingView(paramView);
      break label114;
    }
    if (paramView.mAttachInfo != null) {
      paramView.dispatchDetachedFromWindow();
    }
    label114:
    if (paramView.hasTransientState()) {
      childHasTransientStateChanged(paramView, false);
    }
    dispatchViewRemoved(paramView);
  }
  
  @UnsupportedAppUsage
  public void removeTransientView(View paramView)
  {
    List localList = this.mTransientViews;
    if (localList == null) {
      return;
    }
    int i = localList.size();
    for (int j = 0; j < i; j++) {
      if (paramView == this.mTransientViews.get(j))
      {
        this.mTransientViews.remove(j);
        this.mTransientIndices.remove(j);
        paramView.mParent = null;
        if (paramView.mAttachInfo != null) {
          paramView.dispatchDetachedFromWindow();
        }
        invalidate(true);
        return;
      }
    }
  }
  
  public void removeView(View paramView)
  {
    if (removeViewInternal(paramView))
    {
      requestLayout();
      invalidate(true);
    }
  }
  
  public void removeViewAt(int paramInt)
  {
    removeViewInternal(paramInt, getChildAt(paramInt));
    requestLayout();
    invalidate(true);
  }
  
  public void removeViewInLayout(View paramView)
  {
    removeViewInternal(paramView);
  }
  
  public void removeViews(int paramInt1, int paramInt2)
  {
    removeViewsInternal(paramInt1, paramInt2);
    requestLayout();
    invalidate(true);
  }
  
  public void removeViewsInLayout(int paramInt1, int paramInt2)
  {
    removeViewsInternal(paramInt1, paramInt2);
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    if (getDescendantFocusability() == 393216) {
      return;
    }
    super.unFocus(paramView2);
    View localView = this.mFocused;
    if (localView != paramView1)
    {
      if (localView != null) {
        localView.unFocus(paramView2);
      }
      this.mFocused = paramView1;
    }
    if (this.mParent != null) {
      this.mParent.requestChildFocus(this, paramView2);
    }
  }
  
  public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean)
  {
    return false;
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    boolean bool;
    if ((this.mGroupFlags & 0x80000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    if (paramBoolean == bool) {
      return;
    }
    if (paramBoolean) {
      this.mGroupFlags |= 0x80000;
    } else {
      this.mGroupFlags &= 0xFFF7FFFF;
    }
    if (this.mParent != null) {
      this.mParent.requestDisallowInterceptTouchEvent(paramBoolean);
    }
  }
  
  public boolean requestFocus(int paramInt, Rect paramRect)
  {
    int i = getDescendantFocusability();
    boolean bool;
    if (i != 131072)
    {
      if (i != 262144)
      {
        if (i == 393216)
        {
          bool = super.requestFocus(paramInt, paramRect);
        }
        else
        {
          paramRect = new StringBuilder();
          paramRect.append("descendant focusability must be one of FOCUS_BEFORE_DESCENDANTS, FOCUS_AFTER_DESCENDANTS, FOCUS_BLOCK_DESCENDANTS but is ");
          paramRect.append(i);
          throw new IllegalStateException(paramRect.toString());
        }
      }
      else
      {
        bool = onRequestFocusInDescendants(paramInt, paramRect);
        if (!bool) {
          bool = super.requestFocus(paramInt, paramRect);
        }
      }
    }
    else
    {
      bool = super.requestFocus(paramInt, paramRect);
      if (!bool) {
        bool = onRequestFocusInDescendants(paramInt, paramRect);
      }
    }
    if ((bool) && (!isLayoutValid()) && ((this.mPrivateFlags & 0x1) == 0)) {
      this.mPrivateFlags |= 0x1;
    }
    return bool;
  }
  
  public boolean requestSendAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    ViewParent localViewParent = this.mParent;
    if (localViewParent == null) {
      return false;
    }
    if (!onRequestSendAccessibilityEvent(paramView, paramAccessibilityEvent)) {
      return false;
    }
    return localViewParent.requestSendAccessibilityEvent(this, paramAccessibilityEvent);
  }
  
  public void requestTransitionStart(LayoutTransition paramLayoutTransition)
  {
    ViewRootImpl localViewRootImpl = getViewRootImpl();
    if (localViewRootImpl != null) {
      localViewRootImpl.requestTransitionStart(paramLayoutTransition);
    }
  }
  
  public void requestTransparentRegion(View paramView)
  {
    if (paramView != null)
    {
      paramView.mPrivateFlags |= 0x200;
      if (this.mParent != null) {
        this.mParent.requestTransparentRegion(this);
      }
    }
  }
  
  protected void resetResolvedDrawables()
  {
    super.resetResolvedDrawables();
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if (localView.isLayoutDirectionInherited()) {
        localView.resetResolvedDrawables();
      }
    }
  }
  
  public void resetResolvedLayoutDirection()
  {
    super.resetResolvedLayoutDirection();
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if (localView.isLayoutDirectionInherited()) {
        localView.resetResolvedLayoutDirection();
      }
    }
  }
  
  public void resetResolvedPadding()
  {
    super.resetResolvedPadding();
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if (localView.isLayoutDirectionInherited()) {
        localView.resetResolvedPadding();
      }
    }
  }
  
  public void resetResolvedTextAlignment()
  {
    super.resetResolvedTextAlignment();
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if (localView.isTextAlignmentInherited()) {
        localView.resetResolvedTextAlignment();
      }
    }
  }
  
  public void resetResolvedTextDirection()
  {
    super.resetResolvedTextDirection();
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if (localView.isTextDirectionInherited()) {
        localView.resetResolvedTextDirection();
      }
    }
  }
  
  void resetSubtreeAccessibilityStateChanged()
  {
    super.resetSubtreeAccessibilityStateChanged();
    View[] arrayOfView = this.mChildren;
    int i = this.mChildrenCount;
    for (int j = 0; j < i; j++) {
      arrayOfView[j].resetSubtreeAccessibilityStateChanged();
    }
  }
  
  protected void resolveDrawables()
  {
    super.resolveDrawables();
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if ((localView.isLayoutDirectionInherited()) && (!localView.areDrawablesResolved())) {
        localView.resolveDrawables();
      }
    }
  }
  
  public boolean resolveLayoutDirection()
  {
    if (Android_View_ViewGroup.Extension.get().getExtension() != null) {
      return ((Android_View_ViewGroup.Interface)Android_View_ViewGroup.Extension.get().getExtension().asInterface()).resolveLayoutDirection(this);
    }
    return originalResolveLayoutDirection();
  }
  
  public void resolveLayoutParams()
  {
    super.resolveLayoutParams();
    int i = getChildCount();
    for (int j = 0; j < i; j++) {
      getChildAt(j).resolveLayoutParams();
    }
  }
  
  @UnsupportedAppUsage
  public void resolvePadding()
  {
    super.resolvePadding();
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if ((localView.isLayoutDirectionInherited()) && (!localView.isPaddingResolved())) {
        localView.resolvePadding();
      }
    }
  }
  
  public boolean resolveRtlPropertiesIfNeeded()
  {
    boolean bool = super.resolveRtlPropertiesIfNeeded();
    if (bool)
    {
      int i = getChildCount();
      for (int j = 0; j < i; j++)
      {
        View localView = getChildAt(j);
        if (localView.isLayoutDirectionInherited()) {
          localView.resolveRtlPropertiesIfNeeded();
        }
      }
    }
    return bool;
  }
  
  public boolean resolveTextAlignment()
  {
    boolean bool = super.resolveTextAlignment();
    if (bool)
    {
      int i = getChildCount();
      for (int j = 0; j < i; j++)
      {
        View localView = getChildAt(j);
        if (localView.isTextAlignmentInherited()) {
          localView.resolveTextAlignment();
        }
      }
    }
    return bool;
  }
  
  public boolean resolveTextDirection()
  {
    boolean bool = super.resolveTextDirection();
    if (bool)
    {
      int i = getChildCount();
      for (int j = 0; j < i; j++)
      {
        View localView = getChildAt(j);
        if (localView.isTextDirectionInherited()) {
          localView.resolveTextDirection();
        }
      }
    }
    return bool;
  }
  
  public boolean restoreDefaultFocus()
  {
    if ((this.mDefaultFocus != null) && (getDescendantFocusability() != 393216) && ((this.mDefaultFocus.mViewFlags & 0xC) == 0) && (this.mDefaultFocus.restoreDefaultFocus())) {
      return true;
    }
    return super.restoreDefaultFocus();
  }
  
  public boolean restoreFocusInCluster(int paramInt)
  {
    if (isKeyboardNavigationCluster())
    {
      boolean bool1 = getTouchscreenBlocksFocus();
      try
      {
        setTouchscreenBlocksFocusNoRefocus(false);
        boolean bool2 = restoreFocusInClusterInternal(paramInt);
        return bool2;
      }
      finally
      {
        setTouchscreenBlocksFocusNoRefocus(bool1);
      }
    }
    return restoreFocusInClusterInternal(paramInt);
  }
  
  public boolean restoreFocusNotInCluster()
  {
    if (this.mFocusedInCluster != null) {
      return restoreFocusInCluster(130);
    }
    if ((!isKeyboardNavigationCluster()) && ((this.mViewFlags & 0xC) == 0))
    {
      int i = getDescendantFocusability();
      if (i == 393216) {
        return super.requestFocus(130, null);
      }
      if ((i == 131072) && (super.requestFocus(130, null))) {
        return true;
      }
      for (int j = 0; j < this.mChildrenCount; j++)
      {
        View localView = this.mChildren[j];
        if ((!localView.isKeyboardNavigationCluster()) && (localView.restoreFocusNotInCluster())) {
          return true;
        }
      }
      if ((i == 262144) && (!hasFocusableChild(false))) {
        return super.requestFocus(130, null);
      }
      return false;
    }
    return false;
  }
  
  public void scheduleLayoutAnimation()
  {
    this.mGroupFlags |= 0x8;
  }
  
  public void setAddStatesFromChildren(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mGroupFlags |= 0x2000;
    } else {
      this.mGroupFlags &= 0xDFFF;
    }
    refreshDrawableState();
  }
  
  @Deprecated
  public void setAlwaysDrawnWithCacheEnabled(boolean paramBoolean)
  {
    setBooleanFlag(16384, paramBoolean);
  }
  
  @Deprecated
  public void setAnimationCacheEnabled(boolean paramBoolean)
  {
    setBooleanFlag(64, paramBoolean);
  }
  
  @Deprecated
  protected void setChildrenDrawingCacheEnabled(boolean paramBoolean)
  {
    if ((paramBoolean) || ((this.mPersistentDrawingCache & 0x3) != 3))
    {
      View[] arrayOfView = this.mChildren;
      int i = this.mChildrenCount;
      for (int j = 0; j < i; j++) {
        arrayOfView[j].setDrawingCacheEnabled(paramBoolean);
      }
    }
  }
  
  protected void setChildrenDrawingOrderEnabled(boolean paramBoolean)
  {
    setBooleanFlag(1024, paramBoolean);
  }
  
  @Deprecated
  protected void setChildrenDrawnWithCacheEnabled(boolean paramBoolean)
  {
    setBooleanFlag(32768, paramBoolean);
  }
  
  public void setClipChildren(boolean paramBoolean)
  {
    boolean bool;
    if ((this.mGroupFlags & 0x1) == 1) {
      bool = true;
    } else {
      bool = false;
    }
    if (paramBoolean != bool)
    {
      setBooleanFlag(1, paramBoolean);
      for (int i = 0; i < this.mChildrenCount; i++)
      {
        View localView = getChildAt(i);
        if (localView.mRenderNode != null) {
          localView.mRenderNode.setClipToBounds(paramBoolean);
        }
      }
      invalidate(true);
    }
  }
  
  public void setClipToPadding(boolean paramBoolean)
  {
    if (hasBooleanFlag(2) != paramBoolean)
    {
      setBooleanFlag(2, paramBoolean);
      invalidate(true);
    }
  }
  
  void setDefaultFocus(View paramView)
  {
    View localView = this.mDefaultFocus;
    if ((localView != null) && (localView.isFocusedByDefault())) {
      return;
    }
    this.mDefaultFocus = paramView;
    if ((this.mParent instanceof ViewGroup)) {
      ((ViewGroup)this.mParent).setDefaultFocus(this);
    }
  }
  
  public void setDescendantFocusability(int paramInt)
  {
    if ((paramInt != 131072) && (paramInt != 262144) && (paramInt != 393216)) {
      throw new IllegalArgumentException("must be one of FOCUS_BEFORE_DESCENDANTS, FOCUS_AFTER_DESCENDANTS, FOCUS_BLOCK_DESCENDANTS");
    }
    this.mGroupFlags &= 0xFFF9FFFF;
    this.mGroupFlags |= 0x60000 & paramInt;
  }
  
  public void setLayoutAnimation(LayoutAnimationController paramLayoutAnimationController)
  {
    this.mLayoutAnimationController = paramLayoutAnimationController;
    if (this.mLayoutAnimationController != null) {
      this.mGroupFlags |= 0x8;
    }
  }
  
  public void setLayoutAnimationListener(Animation.AnimationListener paramAnimationListener)
  {
    this.mAnimationListener = paramAnimationListener;
  }
  
  public void setLayoutMode(int paramInt)
  {
    if (this.mLayoutMode != paramInt)
    {
      invalidateInheritedLayoutMode(paramInt);
      boolean bool;
      if (paramInt != -1) {
        bool = true;
      } else {
        bool = false;
      }
      setLayoutMode(paramInt, bool);
      requestLayout();
    }
  }
  
  public void setLayoutTransition(LayoutTransition paramLayoutTransition)
  {
    if (this.mTransition != null)
    {
      LayoutTransition localLayoutTransition = this.mTransition;
      localLayoutTransition.cancel();
      localLayoutTransition.removeTransitionListener(this.mLayoutTransitionListener);
    }
    this.mTransition = paramLayoutTransition;
    paramLayoutTransition = this.mTransition;
    if (paramLayoutTransition != null) {
      paramLayoutTransition.addTransitionListener(this.mLayoutTransitionListener);
    }
  }
  
  public void setMotionEventSplittingEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mGroupFlags |= 0x200000;
    } else {
      this.mGroupFlags &= 0xFFDFFFFF;
    }
  }
  
  public void setOnHierarchyChangeListener(OnHierarchyChangeListener paramOnHierarchyChangeListener)
  {
    this.mOnHierarchyChangeListener = paramOnHierarchyChangeListener;
  }
  
  @Deprecated
  public void setPersistentDrawingCache(int paramInt)
  {
    this.mPersistentDrawingCache = (paramInt & 0x3);
  }
  
  protected void setStaticTransformationsEnabled(boolean paramBoolean)
  {
    setBooleanFlag(2048, paramBoolean);
  }
  
  public void setTouchscreenBlocksFocus(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mGroupFlags |= 0x4000000;
      if ((hasFocus()) && (!isKeyboardNavigationCluster())) {
        if (!getDeepestFocusedChild().isFocusableInTouchMode())
        {
          View localView = focusSearch(2);
          if (localView != null) {
            localView.requestFocus();
          }
        }
      }
    }
    else
    {
      this.mGroupFlags &= 0xFBFFFFFF;
    }
  }
  
  public void setTransitionGroup(boolean paramBoolean)
  {
    this.mGroupFlags |= 0x2000000;
    if (paramBoolean) {
      this.mGroupFlags |= 0x1000000;
    } else {
      this.mGroupFlags &= 0xFEFFFFFF;
    }
  }
  
  boolean shouldBlockFocusForTouchscreen()
  {
    boolean bool;
    if ((getTouchscreenBlocksFocus()) && (this.mContext.getPackageManager().hasSystemFeature("android.hardware.touchscreen")) && ((!isKeyboardNavigationCluster()) || ((!hasFocus()) && (findKeyboardNavigationCluster() == this)))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return true;
  }
  
  public boolean showContextMenuForChild(View paramView)
  {
    boolean bool1 = isShowingContextMenuWithCoords();
    boolean bool2 = false;
    if (bool1) {
      return false;
    }
    bool1 = bool2;
    if (this.mParent != null)
    {
      bool1 = bool2;
      if (this.mParent.showContextMenuForChild(paramView)) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  /* Error */
  public boolean showContextMenuForChild(View paramView, float paramFloat1, float paramFloat2)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_0
    //   2: getfield 432	android/view/ViewGroup:mGroupFlags	I
    //   5: ldc 105
    //   7: ior
    //   8: putfield 432	android/view/ViewGroup:mGroupFlags	I
    //   11: aload_0
    //   12: aload_1
    //   13: invokevirtual 2715	android/view/ViewGroup:showContextMenuForChild	(Landroid/view/View;)Z
    //   16: istore 4
    //   18: iconst_1
    //   19: istore 5
    //   21: iload 4
    //   23: ifeq +17 -> 40
    //   26: aload_0
    //   27: ldc_w 2716
    //   30: aload_0
    //   31: getfield 432	android/view/ViewGroup:mGroupFlags	I
    //   34: iand
    //   35: putfield 432	android/view/ViewGroup:mGroupFlags	I
    //   38: iconst_1
    //   39: ireturn
    //   40: aload_0
    //   41: ldc_w 2716
    //   44: aload_0
    //   45: getfield 432	android/view/ViewGroup:mGroupFlags	I
    //   48: iand
    //   49: putfield 432	android/view/ViewGroup:mGroupFlags	I
    //   52: aload_0
    //   53: getfield 1252	android/view/ViewGroup:mParent	Landroid/view/ViewParent;
    //   56: ifnull +21 -> 77
    //   59: aload_0
    //   60: getfield 1252	android/view/ViewGroup:mParent	Landroid/view/ViewParent;
    //   63: aload_1
    //   64: fload_2
    //   65: fload_3
    //   66: invokeinterface 2718 4 0
    //   71: ifeq +6 -> 77
    //   74: goto +6 -> 80
    //   77: iconst_0
    //   78: istore 5
    //   80: iload 5
    //   82: ireturn
    //   83: astore_1
    //   84: aload_0
    //   85: ldc_w 2716
    //   88: aload_0
    //   89: getfield 432	android/view/ViewGroup:mGroupFlags	I
    //   92: iand
    //   93: putfield 432	android/view/ViewGroup:mGroupFlags	I
    //   96: aload_1
    //   97: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	98	0	this	ViewGroup
    //   0	98	1	paramView	View
    //   0	98	2	paramFloat1	float
    //   0	98	3	paramFloat2	float
    //   16	6	4	bool1	boolean
    //   19	62	5	bool2	boolean
    // Exception table:
    //   from	to	target	type
    //   0	18	83	finally
  }
  
  /* Error */
  public ActionMode startActionModeForChild(View paramView, ActionMode.Callback paramCallback)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 432	android/view/ViewGroup:mGroupFlags	I
    //   4: istore_3
    //   5: ldc 111
    //   7: iload_3
    //   8: iand
    //   9: ifne +48 -> 57
    //   12: aload_0
    //   13: iload_3
    //   14: ldc 109
    //   16: ior
    //   17: putfield 432	android/view/ViewGroup:mGroupFlags	I
    //   20: aload_0
    //   21: aload_1
    //   22: aload_2
    //   23: iconst_0
    //   24: invokevirtual 2723	android/view/ViewGroup:startActionModeForChild	(Landroid/view/View;Landroid/view/ActionMode$Callback;I)Landroid/view/ActionMode;
    //   27: astore_1
    //   28: aload_0
    //   29: ldc_w 2724
    //   32: aload_0
    //   33: getfield 432	android/view/ViewGroup:mGroupFlags	I
    //   36: iand
    //   37: putfield 432	android/view/ViewGroup:mGroupFlags	I
    //   40: aload_1
    //   41: areturn
    //   42: astore_1
    //   43: aload_0
    //   44: ldc_w 2724
    //   47: aload_0
    //   48: getfield 432	android/view/ViewGroup:mGroupFlags	I
    //   51: iand
    //   52: putfield 432	android/view/ViewGroup:mGroupFlags	I
    //   55: aload_1
    //   56: athrow
    //   57: getstatic 240	android/view/ViewGroup:SENTINEL_ACTION_MODE	Landroid/view/ActionMode;
    //   60: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	61	0	this	ViewGroup
    //   0	61	1	paramView	View
    //   0	61	2	paramCallback	ActionMode.Callback
    //   4	13	3	i	int
    // Exception table:
    //   from	to	target	type
    //   12	28	42	finally
  }
  
  public ActionMode startActionModeForChild(View paramView, ActionMode.Callback paramCallback, int paramInt)
  {
    int i = this.mGroupFlags;
    ActionMode localActionMode;
    if (((0x10000000 & i) == 0) && (paramInt == 0)) {
      try
      {
        this.mGroupFlags = (i | 0x8000000);
        localActionMode = startActionModeForChild(paramView, paramCallback);
        this.mGroupFlags = (0xF7FFFFFF & this.mGroupFlags);
        if (localActionMode != SENTINEL_ACTION_MODE) {
          return localActionMode;
        }
      }
      finally
      {
        this.mGroupFlags = (0xF7FFFFFF & this.mGroupFlags);
      }
    }
    if (this.mParent != null) {
      try
      {
        localActionMode = this.mParent.startActionModeForChild(paramView, paramCallback, paramInt);
        return localActionMode;
      }
      catch (AbstractMethodError localAbstractMethodError)
      {
        return this.mParent.startActionModeForChild(paramView, paramCallback);
      }
    }
    return null;
  }
  
  public void startLayoutAnimation()
  {
    if (this.mLayoutAnimationController != null)
    {
      this.mGroupFlags |= 0x8;
      requestLayout();
    }
  }
  
  public void startViewTransition(View paramView)
  {
    if (paramView.mParent == this)
    {
      if (this.mTransitioningViews == null) {
        this.mTransitioningViews = new ArrayList();
      }
      this.mTransitioningViews.add(paramView);
    }
  }
  
  public void subtractObscuredTouchableRegion(Region paramRegion, View paramView)
  {
    int i = this.mChildrenCount;
    ArrayList localArrayList = buildTouchDispatchChildList();
    boolean bool;
    if ((localArrayList == null) && (isChildrenDrawingOrderEnabled())) {
      bool = true;
    } else {
      bool = false;
    }
    View[] arrayOfView = this.mChildren;
    for (int j = i - 1; j >= 0; j--)
    {
      View localView = getAndVerifyPreorderedView(localArrayList, arrayOfView, getAndVerifyPreorderedIndex(i, j, bool));
      if (localView == paramView) {
        break;
      }
      if (localView.canReceivePointerEvents()) {
        applyOpToRegionByBounds(paramRegion, localView, Region.Op.DIFFERENCE);
      }
    }
    applyOpToRegionByBounds(paramRegion, this, Region.Op.INTERSECT);
    paramView = getParent();
    if (paramView != null) {
      paramView.subtractObscuredTouchableRegion(paramRegion, this);
    }
  }
  
  public void suppressLayout(boolean paramBoolean)
  {
    this.mSuppressLayout = paramBoolean;
    if ((!paramBoolean) && (this.mLayoutCalledWhileSuppressed))
    {
      requestLayout();
      this.mLayoutCalledWhileSuppressed = false;
    }
  }
  
  @UnsupportedAppUsage
  public void transformPointToViewLocal(float[] paramArrayOfFloat, View paramView)
  {
    paramArrayOfFloat[0] += this.mScrollX - paramView.mLeft;
    paramArrayOfFloat[1] += this.mScrollY - paramView.mTop;
    if (!paramView.hasIdentityMatrix()) {
      paramView.getInverseMatrix().mapPoints(paramArrayOfFloat);
    }
  }
  
  void unFocus(View paramView)
  {
    View localView = this.mFocused;
    if (localView == null)
    {
      super.unFocus(paramView);
    }
    else
    {
      localView.unFocus(paramView);
      this.mFocused = null;
    }
  }
  
  boolean updateLocalSystemUiVisibility(int paramInt1, int paramInt2)
  {
    boolean bool = super.updateLocalSystemUiVisibility(paramInt1, paramInt2);
    int i = this.mChildrenCount;
    View[] arrayOfView = this.mChildren;
    for (int j = 0; j < i; j++) {
      bool |= arrayOfView[j].updateLocalSystemUiVisibility(paramInt1, paramInt2);
    }
    return bool;
  }
  
  public void updateViewLayout(View paramView, LayoutParams paramLayoutParams)
  {
    if (checkLayoutParams(paramLayoutParams))
    {
      if (paramView.mParent == this)
      {
        paramView.setLayoutParams(paramLayoutParams);
        return;
      }
      paramView = new StringBuilder();
      paramView.append("Given view not a child of ");
      paramView.append(this);
      throw new IllegalArgumentException(paramView.toString());
    }
    paramView = new StringBuilder();
    paramView.append("Invalid LayoutParams supplied to ");
    paramView.append(this);
    throw new IllegalArgumentException(paramView.toString());
  }
  
  static class ChildListForAccessibility
  {
    private static final int MAX_POOL_SIZE = 32;
    private static final Pools.SynchronizedPool<ChildListForAccessibility> sPool = new Pools.SynchronizedPool(32);
    private final ArrayList<View> mChildren = new ArrayList();
    private final ArrayList<ViewGroup.ViewLocationHolder> mHolders = new ArrayList();
    
    private void clear()
    {
      this.mChildren.clear();
    }
    
    private void init(ViewGroup paramViewGroup, boolean paramBoolean)
    {
      ArrayList localArrayList1 = this.mChildren;
      int i = paramViewGroup.getChildCount();
      for (int j = 0; j < i; j++) {
        localArrayList1.add(paramViewGroup.getChildAt(j));
      }
      if (paramBoolean)
      {
        ArrayList localArrayList2 = this.mHolders;
        for (j = 0; j < i; j++) {
          localArrayList2.add(ViewGroup.ViewLocationHolder.obtain(paramViewGroup, (View)localArrayList1.get(j)));
        }
        sort(localArrayList2);
        for (j = 0; j < i; j++)
        {
          paramViewGroup = (ViewGroup.ViewLocationHolder)localArrayList2.get(j);
          localArrayList1.set(j, paramViewGroup.mView);
          paramViewGroup.recycle();
        }
        localArrayList2.clear();
      }
    }
    
    public static ChildListForAccessibility obtain(ViewGroup paramViewGroup, boolean paramBoolean)
    {
      ChildListForAccessibility localChildListForAccessibility1 = (ChildListForAccessibility)sPool.acquire();
      ChildListForAccessibility localChildListForAccessibility2 = localChildListForAccessibility1;
      if (localChildListForAccessibility1 == null) {
        localChildListForAccessibility2 = new ChildListForAccessibility();
      }
      localChildListForAccessibility2.init(paramViewGroup, paramBoolean);
      return localChildListForAccessibility2;
    }
    
    private void sort(ArrayList<ViewGroup.ViewLocationHolder> paramArrayList)
    {
      try
      {
        ViewGroup.ViewLocationHolder.setComparisonStrategy(1);
        Collections.sort(paramArrayList);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        ViewGroup.ViewLocationHolder.setComparisonStrategy(2);
        Collections.sort(paramArrayList);
      }
    }
    
    public View getChildAt(int paramInt)
    {
      return (View)this.mChildren.get(paramInt);
    }
    
    public int getChildCount()
    {
      return this.mChildren.size();
    }
    
    public void recycle()
    {
      clear();
      sPool.release(this);
    }
  }
  
  private static class ChildListForAutofill
    extends ArrayList<View>
  {
    private static final int MAX_POOL_SIZE = 32;
    private static final Pools.SimplePool<ChildListForAutofill> sPool = new Pools.SimplePool(32);
    
    public static ChildListForAutofill obtain()
    {
      ChildListForAutofill localChildListForAutofill1 = (ChildListForAutofill)sPool.acquire();
      ChildListForAutofill localChildListForAutofill2 = localChildListForAutofill1;
      if (localChildListForAutofill1 == null) {
        localChildListForAutofill2 = new ChildListForAutofill();
      }
      return localChildListForAutofill2;
    }
    
    public void recycle()
    {
      clear();
      sPool.release(this);
    }
  }
  
  private static final class HoverTarget
  {
    private static final int MAX_RECYCLED = 32;
    private static HoverTarget sRecycleBin;
    private static final Object sRecycleLock = new Object[0];
    private static int sRecycledCount;
    public View child;
    public HoverTarget next;
    
    public static HoverTarget obtain(View paramView)
    {
      if (paramView != null) {
        synchronized (sRecycleLock)
        {
          HoverTarget localHoverTarget;
          if (sRecycleBin == null)
          {
            localHoverTarget = new android/view/ViewGroup$HoverTarget;
            localHoverTarget.<init>();
          }
          else
          {
            localHoverTarget = sRecycleBin;
            sRecycleBin = localHoverTarget.next;
            sRecycledCount -= 1;
            localHoverTarget.next = null;
          }
          localHoverTarget.child = paramView;
          return localHoverTarget;
        }
      }
      throw new IllegalArgumentException("child must be non-null");
    }
    
    public void recycle()
    {
      if (this.child != null) {
        synchronized (sRecycleLock)
        {
          if (sRecycledCount < 32)
          {
            this.next = sRecycleBin;
            sRecycleBin = this;
            sRecycledCount += 1;
          }
          else
          {
            this.next = null;
          }
          this.child = null;
          return;
        }
      }
      throw new IllegalStateException("already recycled once");
    }
  }
  
  private static class Impl
    implements Android_View_ViewGroup.Interface
  {
    public void addInArray(ViewGroup paramViewGroup, View paramView, int paramInt)
    {
      paramViewGroup.originalAddInArray(paramView, paramInt);
    }
    
    public void init(ViewGroup paramViewGroup, Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {}
    
    public void onChildVisibilityChanged(ViewGroup paramViewGroup, View paramView, int paramInt1, int paramInt2)
    {
      paramViewGroup.originalOnChildVisibilityChanged(paramView, paramInt1, paramInt2);
    }
    
    public void removeFromArray(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup.originalRemoveFromArray(paramInt);
    }
    
    public void removeFromArray(ViewGroup paramViewGroup, int paramInt1, int paramInt2)
    {
      paramViewGroup.originalRemoveFromArray(paramInt1, paramInt2);
    }
    
    public boolean resolveLayoutDirection(ViewGroup paramViewGroup)
    {
      return paramViewGroup.originalResolveLayoutDirection();
    }
  }
  
  public static class LayoutParams
  {
    @Deprecated
    public static final int FILL_PARENT = -1;
    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;
    @ViewDebug.ExportedProperty(category="layout", mapping={@ViewDebug.IntToString(from=-1, to="MATCH_PARENT"), @ViewDebug.IntToString(from=-2, to="WRAP_CONTENT")})
    public int height;
    public LayoutAnimationController.AnimationParameters layoutAnimationParameters;
    @ViewDebug.ExportedProperty(category="layout", mapping={@ViewDebug.IntToString(from=-1, to="MATCH_PARENT"), @ViewDebug.IntToString(from=-2, to="WRAP_CONTENT")})
    public int width;
    
    @UnsupportedAppUsage
    LayoutParams() {}
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      this.width = paramInt1;
      this.height = paramInt2;
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ViewGroup_Layout);
      setBaseAttributes(paramContext, 0, 1);
      paramContext.recycle();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      this.width = paramLayoutParams.width;
      this.height = paramLayoutParams.height;
    }
    
    protected static String sizeToString(int paramInt)
    {
      if (paramInt == -2) {
        return "wrap-content";
      }
      if (paramInt == -1) {
        return "match-parent";
      }
      return String.valueOf(paramInt);
    }
    
    public String debug(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString);
      localStringBuilder.append("ViewGroup.LayoutParams={ width=");
      localStringBuilder.append(sizeToString(this.width));
      localStringBuilder.append(", height=");
      localStringBuilder.append(sizeToString(this.height));
      localStringBuilder.append(" }");
      return localStringBuilder.toString();
    }
    
    void encode(ViewHierarchyEncoder paramViewHierarchyEncoder)
    {
      paramViewHierarchyEncoder.beginObject(this);
      encodeProperties(paramViewHierarchyEncoder);
      paramViewHierarchyEncoder.endObject();
    }
    
    protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
    {
      paramViewHierarchyEncoder.addProperty("width", this.width);
      paramViewHierarchyEncoder.addProperty("height", this.height);
    }
    
    public void onDebugDraw(View paramView, Canvas paramCanvas, Paint paramPaint) {}
    
    public void resolveLayoutDirection(int paramInt) {}
    
    protected void setBaseAttributes(TypedArray paramTypedArray, int paramInt1, int paramInt2)
    {
      this.width = paramTypedArray.getLayoutDimension(paramInt1, "layout_width");
      this.height = paramTypedArray.getLayoutDimension(paramInt2, "layout_height");
    }
  }
  
  public static class MarginLayoutParams
    extends ViewGroup.LayoutParams
  {
    public static final int DEFAULT_MARGIN_RELATIVE = Integer.MIN_VALUE;
    private static final int DEFAULT_MARGIN_RESOLVED = 0;
    private static final int LAYOUT_DIRECTION_MASK = 3;
    private static final int LEFT_MARGIN_UNDEFINED_MASK = 4;
    private static final int NEED_RESOLUTION_MASK = 32;
    private static final int RIGHT_MARGIN_UNDEFINED_MASK = 8;
    private static final int RTL_COMPATIBILITY_MODE_MASK = 16;
    private static final int UNDEFINED_MARGIN = Integer.MIN_VALUE;
    @ViewDebug.ExportedProperty(category="layout")
    public int bottomMargin;
    @ViewDebug.ExportedProperty(category="layout")
    @UnsupportedAppUsage
    private int endMargin = Integer.MIN_VALUE;
    @ViewDebug.ExportedProperty(category="layout")
    public int leftMargin;
    @ViewDebug.ExportedProperty(category="layout", flagMapping={@ViewDebug.FlagToString(equals=3, mask=3, name="LAYOUT_DIRECTION"), @ViewDebug.FlagToString(equals=4, mask=4, name="LEFT_MARGIN_UNDEFINED_MASK"), @ViewDebug.FlagToString(equals=8, mask=8, name="RIGHT_MARGIN_UNDEFINED_MASK"), @ViewDebug.FlagToString(equals=16, mask=16, name="RTL_COMPATIBILITY_MODE_MASK"), @ViewDebug.FlagToString(equals=32, mask=32, name="NEED_RESOLUTION_MASK")}, formatToHexString=true)
    byte mMarginFlags;
    @ViewDebug.ExportedProperty(category="layout")
    public int rightMargin;
    @ViewDebug.ExportedProperty(category="layout")
    @UnsupportedAppUsage
    private int startMargin = Integer.MIN_VALUE;
    @ViewDebug.ExportedProperty(category="layout")
    public int topMargin;
    
    public MarginLayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      this.mMarginFlags = ((byte)(byte)(this.mMarginFlags | 0x4));
      this.mMarginFlags = ((byte)(byte)(this.mMarginFlags | 0x8));
      this.mMarginFlags = ((byte)(byte)(this.mMarginFlags & 0xFFFFFFDF));
      this.mMarginFlags = ((byte)(byte)(this.mMarginFlags & 0xFFFFFFEF));
    }
    
    public MarginLayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ViewGroup_MarginLayout);
      setBaseAttributes(paramAttributeSet, 0, 1);
      int i = paramAttributeSet.getDimensionPixelSize(2, -1);
      if (i >= 0)
      {
        this.leftMargin = i;
        this.topMargin = i;
        this.rightMargin = i;
        this.bottomMargin = i;
      }
      else
      {
        int j = paramAttributeSet.getDimensionPixelSize(9, -1);
        i = paramAttributeSet.getDimensionPixelSize(10, -1);
        if (j >= 0)
        {
          this.leftMargin = j;
          this.rightMargin = j;
        }
        else
        {
          this.leftMargin = paramAttributeSet.getDimensionPixelSize(3, Integer.MIN_VALUE);
          if (this.leftMargin == Integer.MIN_VALUE)
          {
            this.mMarginFlags = ((byte)(byte)(this.mMarginFlags | 0x4));
            this.leftMargin = 0;
          }
          this.rightMargin = paramAttributeSet.getDimensionPixelSize(5, Integer.MIN_VALUE);
          if (this.rightMargin == Integer.MIN_VALUE)
          {
            this.mMarginFlags = ((byte)(byte)(this.mMarginFlags | 0x8));
            this.rightMargin = 0;
          }
        }
        this.startMargin = paramAttributeSet.getDimensionPixelSize(7, Integer.MIN_VALUE);
        this.endMargin = paramAttributeSet.getDimensionPixelSize(8, Integer.MIN_VALUE);
        if (i >= 0)
        {
          this.topMargin = i;
          this.bottomMargin = i;
        }
        else
        {
          this.topMargin = paramAttributeSet.getDimensionPixelSize(4, 0);
          this.bottomMargin = paramAttributeSet.getDimensionPixelSize(6, 0);
        }
        if (isMarginRelative()) {
          this.mMarginFlags = ((byte)(byte)(this.mMarginFlags | 0x20));
        }
      }
      boolean bool = paramContext.getApplicationInfo().hasRtlSupport();
      if ((paramContext.getApplicationInfo().targetSdkVersion < 17) || (!bool)) {
        this.mMarginFlags = ((byte)(byte)(this.mMarginFlags | 0x10));
      }
      this.mMarginFlags = ((byte)(byte)(0x0 | this.mMarginFlags));
      paramAttributeSet.recycle();
    }
    
    public MarginLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
      this.mMarginFlags = ((byte)(byte)(this.mMarginFlags | 0x4));
      this.mMarginFlags = ((byte)(byte)(this.mMarginFlags | 0x8));
      this.mMarginFlags = ((byte)(byte)(this.mMarginFlags & 0xFFFFFFDF));
      this.mMarginFlags = ((byte)(byte)(this.mMarginFlags & 0xFFFFFFEF));
    }
    
    public MarginLayoutParams(MarginLayoutParams paramMarginLayoutParams)
    {
      this.width = paramMarginLayoutParams.width;
      this.height = paramMarginLayoutParams.height;
      this.leftMargin = paramMarginLayoutParams.leftMargin;
      this.topMargin = paramMarginLayoutParams.topMargin;
      this.rightMargin = paramMarginLayoutParams.rightMargin;
      this.bottomMargin = paramMarginLayoutParams.bottomMargin;
      this.startMargin = paramMarginLayoutParams.startMargin;
      this.endMargin = paramMarginLayoutParams.endMargin;
      this.mMarginFlags = ((byte)paramMarginLayoutParams.mMarginFlags);
    }
    
    private void doResolveMargins()
    {
      int i = this.mMarginFlags;
      if ((i & 0x10) == 16)
      {
        if ((i & 0x4) == 4)
        {
          i = this.startMargin;
          if (i > Integer.MIN_VALUE) {
            this.leftMargin = i;
          }
        }
        if ((this.mMarginFlags & 0x8) == 8)
        {
          i = this.endMargin;
          if (i > Integer.MIN_VALUE) {
            this.rightMargin = i;
          }
        }
      }
      else if ((i & 0x3) != 1)
      {
        i = this.startMargin;
        if (i <= Integer.MIN_VALUE) {
          i = 0;
        }
        this.leftMargin = i;
        i = this.endMargin;
        if (i <= Integer.MIN_VALUE) {
          i = 0;
        }
        this.rightMargin = i;
      }
      else
      {
        i = this.endMargin;
        if (i <= Integer.MIN_VALUE) {
          i = 0;
        }
        this.leftMargin = i;
        i = this.startMargin;
        if (i <= Integer.MIN_VALUE) {
          i = 0;
        }
        this.rightMargin = i;
      }
      this.mMarginFlags = ((byte)(byte)(this.mMarginFlags & 0xFFFFFFDF));
    }
    
    public final void copyMarginsFrom(MarginLayoutParams paramMarginLayoutParams)
    {
      this.leftMargin = paramMarginLayoutParams.leftMargin;
      this.topMargin = paramMarginLayoutParams.topMargin;
      this.rightMargin = paramMarginLayoutParams.rightMargin;
      this.bottomMargin = paramMarginLayoutParams.bottomMargin;
      this.startMargin = paramMarginLayoutParams.startMargin;
      this.endMargin = paramMarginLayoutParams.endMargin;
      this.mMarginFlags = ((byte)paramMarginLayoutParams.mMarginFlags);
    }
    
    protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
    {
      super.encodeProperties(paramViewHierarchyEncoder);
      paramViewHierarchyEncoder.addProperty("leftMargin", this.leftMargin);
      paramViewHierarchyEncoder.addProperty("topMargin", this.topMargin);
      paramViewHierarchyEncoder.addProperty("rightMargin", this.rightMargin);
      paramViewHierarchyEncoder.addProperty("bottomMargin", this.bottomMargin);
      paramViewHierarchyEncoder.addProperty("startMargin", this.startMargin);
      paramViewHierarchyEncoder.addProperty("endMargin", this.endMargin);
    }
    
    public int getLayoutDirection()
    {
      return this.mMarginFlags & 0x3;
    }
    
    public int getMarginEnd()
    {
      int i = this.endMargin;
      if (i != Integer.MIN_VALUE) {
        return i;
      }
      if ((this.mMarginFlags & 0x20) == 32) {
        doResolveMargins();
      }
      if ((this.mMarginFlags & 0x3) != 1) {
        return this.rightMargin;
      }
      return this.leftMargin;
    }
    
    public int getMarginStart()
    {
      int i = this.startMargin;
      if (i != Integer.MIN_VALUE) {
        return i;
      }
      if ((this.mMarginFlags & 0x20) == 32) {
        doResolveMargins();
      }
      if ((this.mMarginFlags & 0x3) != 1) {
        return this.leftMargin;
      }
      return this.rightMargin;
    }
    
    public boolean isLayoutRtl()
    {
      int i = this.mMarginFlags;
      boolean bool = true;
      if ((i & 0x3) != 1) {
        bool = false;
      }
      return bool;
    }
    
    public boolean isMarginRelative()
    {
      boolean bool;
      if ((this.startMargin == Integer.MIN_VALUE) && (this.endMargin == Integer.MIN_VALUE)) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    public void onDebugDraw(View paramView, Canvas paramCanvas, Paint paramPaint)
    {
      Insets localInsets;
      if (View.isLayoutModeOptical(paramView.mParent)) {
        localInsets = paramView.getOpticalInsets();
      } else {
        localInsets = Insets.NONE;
      }
      ViewGroup.fillDifference(paramCanvas, paramView.getLeft() + localInsets.left, paramView.getTop() + localInsets.top, paramView.getRight() - localInsets.right, paramView.getBottom() - localInsets.bottom, this.leftMargin, this.topMargin, this.rightMargin, this.bottomMargin, paramPaint);
    }
    
    public void resolveLayoutDirection(int paramInt)
    {
      setLayoutDirection(paramInt);
      if ((isMarginRelative()) && ((this.mMarginFlags & 0x20) == 32))
      {
        doResolveMargins();
        return;
      }
    }
    
    public void setLayoutDirection(int paramInt)
    {
      if ((paramInt != 0) && (paramInt != 1)) {
        return;
      }
      int i = this.mMarginFlags;
      if (paramInt != (i & 0x3))
      {
        this.mMarginFlags = ((byte)(byte)(i & 0xFFFFFFFC));
        this.mMarginFlags = ((byte)(byte)(this.mMarginFlags | paramInt & 0x3));
        if (isMarginRelative()) {
          this.mMarginFlags = ((byte)(byte)(this.mMarginFlags | 0x20));
        } else {
          this.mMarginFlags = ((byte)(byte)(this.mMarginFlags & 0xFFFFFFDF));
        }
      }
    }
    
    public void setMarginEnd(int paramInt)
    {
      this.endMargin = paramInt;
      this.mMarginFlags = ((byte)(byte)(this.mMarginFlags | 0x20));
    }
    
    public void setMarginStart(int paramInt)
    {
      this.startMargin = paramInt;
      this.mMarginFlags = ((byte)(byte)(this.mMarginFlags | 0x20));
    }
    
    public void setMargins(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.leftMargin = paramInt1;
      this.topMargin = paramInt2;
      this.rightMargin = paramInt3;
      this.bottomMargin = paramInt4;
      this.mMarginFlags = ((byte)(byte)(this.mMarginFlags & 0xFFFFFFFB));
      this.mMarginFlags = ((byte)(byte)(this.mMarginFlags & 0xFFFFFFF7));
      if (isMarginRelative()) {
        this.mMarginFlags = ((byte)(byte)(this.mMarginFlags | 0x20));
      } else {
        this.mMarginFlags = ((byte)(byte)(this.mMarginFlags & 0xFFFFFFDF));
      }
    }
    
    @UnsupportedAppUsage
    public void setMarginsRelative(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.startMargin = paramInt1;
      this.topMargin = paramInt2;
      this.endMargin = paramInt3;
      this.bottomMargin = paramInt4;
      this.mMarginFlags = ((byte)(byte)(this.mMarginFlags | 0x20));
    }
  }
  
  public static abstract interface OnHierarchyChangeListener
  {
    public abstract void onChildViewAdded(View paramView1, View paramView2);
    
    public abstract void onChildViewRemoved(View paramView1, View paramView2);
  }
  
  private static final class TouchTarget
  {
    public static final int ALL_POINTER_IDS = -1;
    private static final int MAX_RECYCLED = 32;
    private static TouchTarget sRecycleBin;
    private static final Object sRecycleLock = new Object[0];
    private static int sRecycledCount;
    @UnsupportedAppUsage
    public View child;
    public TouchTarget next;
    public int pointerIdBits;
    
    public static TouchTarget obtain(View paramView, int paramInt)
    {
      if (paramView != null) {
        synchronized (sRecycleLock)
        {
          TouchTarget localTouchTarget;
          if (sRecycleBin == null)
          {
            localTouchTarget = new android/view/ViewGroup$TouchTarget;
            localTouchTarget.<init>();
          }
          else
          {
            localTouchTarget = sRecycleBin;
            sRecycleBin = localTouchTarget.next;
            sRecycledCount -= 1;
            localTouchTarget.next = null;
          }
          localTouchTarget.child = paramView;
          localTouchTarget.pointerIdBits = paramInt;
          return localTouchTarget;
        }
      }
      throw new IllegalArgumentException("child must be non-null");
    }
    
    public void recycle()
    {
      if (this.child != null) {
        synchronized (sRecycleLock)
        {
          if (sRecycledCount < 32)
          {
            this.next = sRecycleBin;
            sRecycleBin = this;
            sRecycledCount += 1;
          }
          else
          {
            this.next = null;
          }
          this.child = null;
          return;
        }
      }
      throw new IllegalStateException("already recycled once");
    }
  }
  
  static class ViewLocationHolder
    implements Comparable<ViewLocationHolder>
  {
    public static final int COMPARISON_STRATEGY_LOCATION = 2;
    public static final int COMPARISON_STRATEGY_STRIPE = 1;
    private static final int MAX_POOL_SIZE = 32;
    private static int sComparisonStrategy = 1;
    private static final Pools.SynchronizedPool<ViewLocationHolder> sPool = new Pools.SynchronizedPool(32);
    private int mLayoutDirection;
    private final Rect mLocation = new Rect();
    private ViewGroup mRoot;
    public View mView;
    
    private void clear()
    {
      this.mView = null;
      this.mRoot = null;
      this.mLocation.set(0, 0, 0, 0);
      this.mRoot = null;
    }
    
    private static int compareBoundsOfTree(ViewLocationHolder paramViewLocationHolder1, ViewLocationHolder paramViewLocationHolder2)
    {
      if (sComparisonStrategy == 1)
      {
        if (paramViewLocationHolder1.mLocation.bottom - paramViewLocationHolder2.mLocation.top <= 0) {
          return -1;
        }
        if (paramViewLocationHolder1.mLocation.top - paramViewLocationHolder2.mLocation.bottom >= 0) {
          return 1;
        }
      }
      if (paramViewLocationHolder1.mLayoutDirection == 0)
      {
        i = paramViewLocationHolder1.mLocation.left - paramViewLocationHolder2.mLocation.left;
        if (i != 0) {
          return i;
        }
      }
      else
      {
        i = paramViewLocationHolder1.mLocation.right - paramViewLocationHolder2.mLocation.right;
        if (i != 0) {
          return -i;
        }
      }
      int i = paramViewLocationHolder1.mLocation.top - paramViewLocationHolder2.mLocation.top;
      if (i != 0) {
        return i;
      }
      i = paramViewLocationHolder1.mLocation.height() - paramViewLocationHolder2.mLocation.height();
      if (i != 0) {
        return -i;
      }
      i = paramViewLocationHolder1.mLocation.width() - paramViewLocationHolder2.mLocation.width();
      if (i != 0) {
        return -i;
      }
      Object localObject = new Rect();
      Rect localRect1 = new Rect();
      Rect localRect2 = new Rect();
      paramViewLocationHolder1.mView.getBoundsOnScreen((Rect)localObject, true);
      paramViewLocationHolder2.mView.getBoundsOnScreen(localRect1, true);
      localObject = paramViewLocationHolder1.mView.findViewByPredicateTraversal(new _..Lambda.ViewGroup.ViewLocationHolder.QbO7cM0ULKe25a7bfXG3VH6DB0c(localRect2, (Rect)localObject), null);
      paramViewLocationHolder2 = paramViewLocationHolder2.mView.findViewByPredicateTraversal(new _..Lambda.ViewGroup.ViewLocationHolder.AjKvqdj7SGGIzA5qrlZUuu71jl8(localRect2, localRect1), null);
      if ((localObject != null) && (paramViewLocationHolder2 != null)) {
        return compareBoundsOfTree(obtain(paramViewLocationHolder1.mRoot, (View)localObject), obtain(paramViewLocationHolder1.mRoot, paramViewLocationHolder2));
      }
      if (localObject != null) {
        return 1;
      }
      if (paramViewLocationHolder2 != null) {
        return -1;
      }
      return 0;
    }
    
    private void init(ViewGroup paramViewGroup, View paramView)
    {
      Rect localRect = this.mLocation;
      paramView.getDrawingRect(localRect);
      paramViewGroup.offsetDescendantRectToMyCoords(paramView, localRect);
      this.mView = paramView;
      this.mRoot = paramViewGroup;
      this.mLayoutDirection = paramViewGroup.getLayoutDirection();
    }
    
    public static ViewLocationHolder obtain(ViewGroup paramViewGroup, View paramView)
    {
      ViewLocationHolder localViewLocationHolder1 = (ViewLocationHolder)sPool.acquire();
      ViewLocationHolder localViewLocationHolder2 = localViewLocationHolder1;
      if (localViewLocationHolder1 == null) {
        localViewLocationHolder2 = new ViewLocationHolder();
      }
      localViewLocationHolder2.init(paramViewGroup, paramView);
      return localViewLocationHolder2;
    }
    
    public static void setComparisonStrategy(int paramInt)
    {
      sComparisonStrategy = paramInt;
    }
    
    public int compareTo(ViewLocationHolder paramViewLocationHolder)
    {
      if (paramViewLocationHolder == null) {
        return 1;
      }
      int i = compareBoundsOfTree(this, paramViewLocationHolder);
      if (i != 0) {
        return i;
      }
      return this.mView.getAccessibilityViewId() - paramViewLocationHolder.mView.getAccessibilityViewId();
    }
    
    public void recycle()
    {
      clear();
      sPool.release(this);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ViewGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */