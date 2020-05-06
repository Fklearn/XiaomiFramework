package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.StrictMode;
import android.os.StrictMode.Span;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewHierarchyEncoder;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.animation.AnimationUtils;
import com.android.internal.R.styleable;
import java.util.ArrayList;
import java.util.List;

public class ScrollView
  extends FrameLayout
{
  static final int ANIMATED_SCROLL_GAP = 250;
  private static final int INVALID_POINTER = -1;
  static final float MAX_SCROLL_FACTOR = 0.5F;
  private static final String TAG = "ScrollView";
  private int mActivePointerId = -1;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769715L)
  private View mChildToScrollTo = null;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769386L)
  private EdgeEffect mEdgeGlowBottom = new EdgeEffect(getContext());
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123768600L)
  private EdgeEffect mEdgeGlowTop = new EdgeEffect(getContext());
  @ViewDebug.ExportedProperty(category="layout")
  private boolean mFillViewport;
  @UnsupportedAppUsage
  private StrictMode.Span mFlingStrictSpan = null;
  @UnsupportedAppUsage
  private boolean mIsBeingDragged = false;
  private boolean mIsLayoutDirty = true;
  @UnsupportedAppUsage
  private int mLastMotionY;
  @UnsupportedAppUsage
  private long mLastScroll;
  private int mMaximumVelocity;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=124051125L)
  private int mMinimumVelocity;
  private int mNestedYOffset;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=124050903L)
  private int mOverflingDistance;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=124050903L)
  private int mOverscrollDistance;
  private SavedState mSavedState;
  private final int[] mScrollConsumed = new int[2];
  private final int[] mScrollOffset = new int[2];
  private StrictMode.Span mScrollStrictSpan = null;
  @UnsupportedAppUsage
  private OverScroller mScroller;
  private boolean mSmoothScrollingEnabled = true;
  private final Rect mTempRect = new Rect();
  private int mTouchSlop;
  @UnsupportedAppUsage
  private VelocityTracker mVelocityTracker;
  private float mVerticalScrollFactor;
  
  public ScrollView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ScrollView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842880);
  }
  
  public ScrollView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ScrollView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    initScrollView();
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ScrollView, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.ScrollView, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    setFillViewport(localTypedArray.getBoolean(0, false));
    localTypedArray.recycle();
    if (paramContext.getResources().getConfiguration().uiMode == 6) {
      setRevealOnFocusHint(false);
    }
  }
  
  @UnsupportedAppUsage
  private boolean canScroll()
  {
    boolean bool = false;
    View localView = getChildAt(0);
    if (localView != null)
    {
      int i = localView.getHeight();
      if (getHeight() < this.mPaddingTop + i + this.mPaddingBottom) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  private static int clamp(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt2 < paramInt3) && (paramInt1 >= 0))
    {
      if (paramInt2 + paramInt1 > paramInt3) {
        return paramInt3 - paramInt2;
      }
      return paramInt1;
    }
    return 0;
  }
  
  private void doScrollY(int paramInt)
  {
    if (paramInt != 0) {
      if (this.mSmoothScrollingEnabled) {
        smoothScrollBy(0, paramInt);
      } else {
        scrollBy(0, paramInt);
      }
    }
  }
  
  @UnsupportedAppUsage
  private void endDrag()
  {
    this.mIsBeingDragged = false;
    recycleVelocityTracker();
    if (shouldDisplayEdgeEffects())
    {
      this.mEdgeGlowTop.onRelease();
      this.mEdgeGlowBottom.onRelease();
    }
    StrictMode.Span localSpan = this.mScrollStrictSpan;
    if (localSpan != null)
    {
      localSpan.finish();
      this.mScrollStrictSpan = null;
    }
  }
  
  private View findFocusableViewInBounds(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = getFocusables(2);
    Object localObject1 = null;
    int i = 0;
    int j = localArrayList.size();
    int k = 0;
    while (k < j)
    {
      View localView = (View)localArrayList.get(k);
      int m = localView.getTop();
      int n = localView.getBottom();
      Object localObject2 = localObject1;
      int i1 = i;
      if (paramInt1 < n)
      {
        localObject2 = localObject1;
        i1 = i;
        if (m < paramInt2)
        {
          int i2 = 0;
          int i3;
          if ((paramInt1 < m) && (n < paramInt2)) {
            i3 = 1;
          } else {
            i3 = 0;
          }
          if (localObject1 == null)
          {
            localObject2 = localView;
            i1 = i3;
          }
          else
          {
            if (((paramBoolean) && (m < ((View)localObject1).getTop())) || ((!paramBoolean) && (n > ((View)localObject1).getBottom()))) {
              i2 = 1;
            }
            if (i != 0)
            {
              localObject2 = localObject1;
              i1 = i;
              if (i3 != 0)
              {
                localObject2 = localObject1;
                i1 = i;
                if (i2 != 0)
                {
                  localObject2 = localView;
                  i1 = i;
                }
              }
            }
            else if (i3 != 0)
            {
              localObject2 = localView;
              i1 = 1;
            }
            else
            {
              localObject2 = localObject1;
              i1 = i;
              if (i2 != 0)
              {
                localObject2 = localView;
                i1 = i;
              }
            }
          }
        }
      }
      k++;
      localObject1 = localObject2;
      i = i1;
    }
    return (View)localObject1;
  }
  
  private void flingWithNestedDispatch(int paramInt)
  {
    boolean bool;
    if (((this.mScrollY <= 0) && (paramInt <= 0)) || ((this.mScrollY >= getScrollRange()) && (paramInt >= 0))) {
      bool = false;
    } else {
      bool = true;
    }
    if (!dispatchNestedPreFling(0.0F, paramInt))
    {
      dispatchNestedFling(0.0F, paramInt, bool);
      if (bool) {
        fling(paramInt);
      }
    }
  }
  
  private int getScrollRange()
  {
    int i = 0;
    if (getChildCount() > 0)
    {
      View localView = getChildAt(0);
      i = Math.max(0, localView.getHeight() - (getHeight() - this.mPaddingBottom - this.mPaddingTop));
    }
    return i;
  }
  
  private boolean inChild(int paramInt1, int paramInt2)
  {
    int i = getChildCount();
    boolean bool = false;
    if (i > 0)
    {
      i = this.mScrollY;
      View localView = getChildAt(0);
      if ((paramInt2 >= localView.getTop() - i) && (paramInt2 < localView.getBottom() - i) && (paramInt1 >= localView.getLeft()) && (paramInt1 < localView.getRight())) {
        bool = true;
      }
      return bool;
    }
    return false;
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
  
  private void initScrollView()
  {
    this.mScroller = new OverScroller(getContext());
    setFocusable(true);
    setDescendantFocusability(262144);
    setWillNotDraw(false);
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(this.mContext);
    this.mTouchSlop = localViewConfiguration.getScaledTouchSlop();
    this.mMinimumVelocity = localViewConfiguration.getScaledMinimumFlingVelocity();
    this.mMaximumVelocity = localViewConfiguration.getScaledMaximumFlingVelocity();
    this.mOverscrollDistance = localViewConfiguration.getScaledOverscrollDistance();
    this.mOverflingDistance = localViewConfiguration.getScaledOverflingDistance();
    this.mVerticalScrollFactor = localViewConfiguration.getScaledVerticalScrollFactor();
  }
  
  private void initVelocityTrackerIfNotExists()
  {
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
  }
  
  private boolean isOffScreen(View paramView)
  {
    return isWithinDeltaOfScreen(paramView, 0, getHeight()) ^ true;
  }
  
  private static boolean isViewDescendantOf(View paramView1, View paramView2)
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
  
  private boolean isWithinDeltaOfScreen(View paramView, int paramInt1, int paramInt2)
  {
    paramView.getDrawingRect(this.mTempRect);
    offsetDescendantRectToMyCoords(paramView, this.mTempRect);
    boolean bool;
    if ((this.mTempRect.bottom + paramInt1 >= getScrollY()) && (this.mTempRect.top - paramInt1 <= getScrollY() + paramInt2)) {
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
      this.mLastMotionY = ((int)paramMotionEvent.getY(i));
      this.mActivePointerId = paramMotionEvent.getPointerId(i);
      paramMotionEvent = this.mVelocityTracker;
      if (paramMotionEvent != null) {
        paramMotionEvent.clear();
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
  
  private boolean scrollAndFocus(int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool1 = true;
    int i = getHeight();
    int j = getScrollY();
    i = j + i;
    boolean bool2;
    if (paramInt1 == 33) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    View localView = findFocusableViewInBounds(bool2, paramInt2, paramInt3);
    Object localObject = localView;
    if (localView == null) {
      localObject = this;
    }
    if ((paramInt2 >= j) && (paramInt3 <= i))
    {
      bool2 = false;
    }
    else
    {
      if (bool2) {
        paramInt2 -= j;
      } else {
        paramInt2 = paramInt3 - i;
      }
      doScrollY(paramInt2);
      bool2 = bool1;
    }
    if (localObject != findFocus()) {
      ((View)localObject).requestFocus(paramInt1);
    }
    return bool2;
  }
  
  private boolean scrollToChildRect(Rect paramRect, boolean paramBoolean)
  {
    int i = computeScrollDeltaToGetChildRectOnScreen(paramRect);
    boolean bool;
    if (i != 0) {
      bool = true;
    } else {
      bool = false;
    }
    if (bool) {
      if (paramBoolean) {
        scrollBy(0, i);
      } else {
        smoothScrollBy(0, i);
      }
    }
    return bool;
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
  
  public void addView(View paramView)
  {
    if (getChildCount() <= 0)
    {
      super.addView(paramView);
      return;
    }
    throw new IllegalStateException("ScrollView can host only one direct child");
  }
  
  public void addView(View paramView, int paramInt)
  {
    if (getChildCount() <= 0)
    {
      super.addView(paramView, paramInt);
      return;
    }
    throw new IllegalStateException("ScrollView can host only one direct child");
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    if (getChildCount() <= 0)
    {
      super.addView(paramView, paramInt, paramLayoutParams);
      return;
    }
    throw new IllegalStateException("ScrollView can host only one direct child");
  }
  
  public void addView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    if (getChildCount() <= 0)
    {
      super.addView(paramView, paramLayoutParams);
      return;
    }
    throw new IllegalStateException("ScrollView can host only one direct child");
  }
  
  public boolean arrowScroll(int paramInt)
  {
    View localView1 = findFocus();
    View localView2 = localView1;
    if (localView1 == this) {
      localView2 = null;
    }
    localView1 = FocusFinder.getInstance().findNextFocus(this, localView2, paramInt);
    int i = getMaxScrollAmount();
    if ((localView1 != null) && (isWithinDeltaOfScreen(localView1, i, getHeight())))
    {
      localView1.getDrawingRect(this.mTempRect);
      offsetDescendantRectToMyCoords(localView1, this.mTempRect);
      doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
      localView1.requestFocus(paramInt);
    }
    else
    {
      int j = i;
      int k;
      if ((paramInt == 33) && (getScrollY() < j))
      {
        k = getScrollY();
      }
      else
      {
        k = j;
        if (paramInt == 130)
        {
          k = j;
          if (getChildCount() > 0)
          {
            int m = getChildAt(0).getBottom();
            int n = getScrollY() + getHeight() - this.mPaddingBottom;
            k = j;
            if (m - n < i) {
              k = m - n;
            }
          }
        }
      }
      if (k == 0) {
        return false;
      }
      if (paramInt != 130) {
        k = -k;
      }
      doScrollY(k);
    }
    if ((localView2 != null) && (localView2.isFocused()) && (isOffScreen(localView2)))
    {
      paramInt = getDescendantFocusability();
      setDescendantFocusability(131072);
      requestFocus();
      setDescendantFocusability(paramInt);
    }
    return true;
  }
  
  public void computeScroll()
  {
    if (this.mScroller.computeScrollOffset())
    {
      int i = this.mScrollX;
      int j = this.mScrollY;
      int k = this.mScroller.getCurrX();
      int m = this.mScroller.getCurrY();
      if ((i != k) || (j != m))
      {
        int n = getScrollRange();
        int i1 = getOverScrollMode();
        int i2 = 1;
        int i3 = i2;
        if (i1 != 0) {
          if ((i1 == 1) && (n > 0)) {
            i3 = i2;
          } else {
            i3 = 0;
          }
        }
        overScrollBy(k - i, m - j, i, j, 0, n, 0, this.mOverflingDistance, false);
        onScrollChanged(this.mScrollX, this.mScrollY, i, j);
        if (i3 != 0) {
          if ((m < 0) && (j >= 0)) {
            this.mEdgeGlowTop.onAbsorb((int)this.mScroller.getCurrVelocity());
          } else if ((m > n) && (j <= n)) {
            this.mEdgeGlowBottom.onAbsorb((int)this.mScroller.getCurrVelocity());
          }
        }
      }
      if (!awakenScrollBars()) {
        postInvalidateOnAnimation();
      }
    }
    else
    {
      StrictMode.Span localSpan = this.mFlingStrictSpan;
      if (localSpan != null)
      {
        localSpan.finish();
        this.mFlingStrictSpan = null;
      }
    }
  }
  
  protected int computeScrollDeltaToGetChildRectOnScreen(Rect paramRect)
  {
    if (getChildCount() == 0) {
      return 0;
    }
    int i = getHeight();
    int j = getScrollY();
    int k = j + i;
    int m = getVerticalFadingEdgeLength();
    int n = j;
    if (paramRect.top > 0) {
      n = j + m;
    }
    j = k;
    if (paramRect.bottom < getChildAt(0).getHeight()) {
      j = k - m;
    }
    m = 0;
    if ((paramRect.bottom > j) && (paramRect.top > n))
    {
      if (paramRect.height() > i) {
        k = 0 + (paramRect.top - n);
      } else {
        k = 0 + (paramRect.bottom - j);
      }
      k = Math.min(k, getChildAt(0).getBottom() - j);
    }
    for (;;)
    {
      break;
      k = m;
      if (paramRect.top < n)
      {
        k = m;
        if (paramRect.bottom < j)
        {
          if (paramRect.height() > i) {
            k = 0 - (j - paramRect.bottom);
          } else {
            k = 0 - (n - paramRect.top);
          }
          k = Math.max(k, -getScrollY());
        }
      }
    }
    return k;
  }
  
  protected int computeVerticalScrollOffset()
  {
    return Math.max(0, super.computeVerticalScrollOffset());
  }
  
  protected int computeVerticalScrollRange()
  {
    int i = getChildCount();
    int j = getHeight() - this.mPaddingBottom - this.mPaddingTop;
    if (i == 0) {
      return j;
    }
    i = getChildAt(0).getBottom();
    int k = this.mScrollY;
    int m = Math.max(0, i - j);
    if (k < 0)
    {
      j = i - k;
    }
    else
    {
      j = i;
      if (k > m) {
        j = i + (k - m);
      }
    }
    return j;
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    boolean bool;
    if ((!super.dispatchKeyEvent(paramKeyEvent)) && (!executeKeyEvent(paramKeyEvent))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    if (shouldDisplayEdgeEffects())
    {
      int i = this.mScrollY;
      boolean bool = getClipToPadding();
      int j;
      int k;
      int m;
      float f1;
      float f2;
      if (!this.mEdgeGlowTop.isFinished())
      {
        j = paramCanvas.save();
        if (bool)
        {
          k = getWidth() - this.mPaddingLeft - this.mPaddingRight;
          m = getHeight() - this.mPaddingTop - this.mPaddingBottom;
          f1 = this.mPaddingLeft;
          f2 = this.mPaddingTop;
        }
        else
        {
          k = getWidth();
          m = getHeight();
          f1 = 0.0F;
          f2 = 0.0F;
        }
        paramCanvas.translate(f1, Math.min(0, i) + f2);
        this.mEdgeGlowTop.setSize(k, m);
        if (this.mEdgeGlowTop.draw(paramCanvas)) {
          postInvalidateOnAnimation();
        }
        paramCanvas.restoreToCount(j);
      }
      if (!this.mEdgeGlowBottom.isFinished())
      {
        j = paramCanvas.save();
        if (bool)
        {
          k = getWidth() - this.mPaddingLeft - this.mPaddingRight;
          m = getHeight() - this.mPaddingTop - this.mPaddingBottom;
          f1 = this.mPaddingLeft;
          f2 = this.mPaddingTop;
        }
        else
        {
          k = getWidth();
          m = getHeight();
          f1 = 0.0F;
          f2 = 0.0F;
        }
        paramCanvas.translate(-k + f1, Math.max(getScrollRange(), i) + m + f2);
        paramCanvas.rotate(180.0F, k, 0.0F);
        this.mEdgeGlowBottom.setSize(k, m);
        if (this.mEdgeGlowBottom.draw(paramCanvas)) {
          postInvalidateOnAnimation();
        }
        paramCanvas.restoreToCount(j);
      }
    }
  }
  
  protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    super.encodeProperties(paramViewHierarchyEncoder);
    paramViewHierarchyEncoder.addProperty("fillViewport", this.mFillViewport);
  }
  
  public boolean executeKeyEvent(KeyEvent paramKeyEvent)
  {
    this.mTempRect.setEmpty();
    boolean bool1 = canScroll();
    int i = 130;
    if (!bool1)
    {
      bool2 = isFocused();
      bool1 = false;
      if ((bool2) && (paramKeyEvent.getKeyCode() != 4))
      {
        View localView = findFocus();
        paramKeyEvent = localView;
        if (localView == this) {
          paramKeyEvent = null;
        }
        paramKeyEvent = FocusFinder.getInstance().findNextFocus(this, paramKeyEvent, 130);
        if ((paramKeyEvent != null) && (paramKeyEvent != this) && (paramKeyEvent.requestFocus(130))) {
          bool1 = true;
        }
        return bool1;
      }
      return false;
    }
    boolean bool2 = false;
    bool1 = bool2;
    if (paramKeyEvent.getAction() == 0)
    {
      int j = paramKeyEvent.getKeyCode();
      if (j != 19)
      {
        if (j != 20)
        {
          if (j != 62)
          {
            bool1 = bool2;
          }
          else
          {
            if (paramKeyEvent.isShiftPressed()) {
              i = 33;
            }
            pageScroll(i);
            bool1 = bool2;
          }
        }
        else if (!paramKeyEvent.isAltPressed()) {
          bool1 = arrowScroll(130);
        } else {
          bool1 = fullScroll(130);
        }
      }
      else if (!paramKeyEvent.isAltPressed()) {
        bool1 = arrowScroll(33);
      } else {
        bool1 = fullScroll(33);
      }
    }
    return bool1;
  }
  
  public void fling(int paramInt)
  {
    if (getChildCount() > 0)
    {
      int i = getHeight() - this.mPaddingBottom - this.mPaddingTop;
      int j = getChildAt(0).getHeight();
      this.mScroller.fling(this.mScrollX, this.mScrollY, 0, paramInt, 0, 0, 0, Math.max(0, j - i), 0, i / 2);
      if (this.mFlingStrictSpan == null) {
        this.mFlingStrictSpan = StrictMode.enterCriticalSpan("ScrollView-fling");
      }
      postInvalidateOnAnimation();
    }
  }
  
  public boolean fullScroll(int paramInt)
  {
    int i;
    if (paramInt == 130) {
      i = 1;
    } else {
      i = 0;
    }
    int j = getHeight();
    Object localObject = this.mTempRect;
    ((Rect)localObject).top = 0;
    ((Rect)localObject).bottom = j;
    if (i != 0)
    {
      i = getChildCount();
      if (i > 0)
      {
        localObject = getChildAt(i - 1);
        this.mTempRect.bottom = (((View)localObject).getBottom() + this.mPaddingBottom);
        localObject = this.mTempRect;
        ((Rect)localObject).top = (((Rect)localObject).bottom - j);
      }
    }
    return scrollAndFocus(paramInt, this.mTempRect.top, this.mTempRect.bottom);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return ScrollView.class.getName();
  }
  
  public int getBottomEdgeEffectColor()
  {
    return this.mEdgeGlowBottom.getColor();
  }
  
  protected float getBottomFadingEdgeStrength()
  {
    if (getChildCount() == 0) {
      return 0.0F;
    }
    int i = getVerticalFadingEdgeLength();
    int j = getHeight();
    int k = this.mPaddingBottom;
    j = getChildAt(0).getBottom() - this.mScrollY - (j - k);
    if (j < i) {
      return j / i;
    }
    return 1.0F;
  }
  
  public int getMaxScrollAmount()
  {
    return (int)((this.mBottom - this.mTop) * 0.5F);
  }
  
  public int getTopEdgeEffectColor()
  {
    return this.mEdgeGlowTop.getColor();
  }
  
  protected float getTopFadingEdgeStrength()
  {
    if (getChildCount() == 0) {
      return 0.0F;
    }
    int i = getVerticalFadingEdgeLength();
    if (this.mScrollY < i) {
      return this.mScrollY / i;
    }
    return 1.0F;
  }
  
  public boolean isFillViewport()
  {
    return this.mFillViewport;
  }
  
  public boolean isSmoothScrollingEnabled()
  {
    return this.mSmoothScrollingEnabled;
  }
  
  protected void measureChild(View paramView, int paramInt1, int paramInt2)
  {
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    int i = getChildMeasureSpec(paramInt1, this.mPaddingLeft + this.mPaddingRight, localLayoutParams.width);
    int j = this.mPaddingTop;
    paramInt1 = this.mPaddingBottom;
    paramView.measure(i, View.MeasureSpec.makeSafeMeasureSpec(Math.max(0, View.MeasureSpec.getSize(paramInt2) - (j + paramInt1)), 0));
  }
  
  protected void measureChildWithMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    int i = getChildMeasureSpec(paramInt1, this.mPaddingLeft + this.mPaddingRight + localMarginLayoutParams.leftMargin + localMarginLayoutParams.rightMargin + paramInt2, localMarginLayoutParams.width);
    int j = this.mPaddingTop;
    paramInt2 = this.mPaddingBottom;
    paramInt1 = localMarginLayoutParams.topMargin;
    int k = localMarginLayoutParams.bottomMargin;
    paramView.measure(i, View.MeasureSpec.makeSafeMeasureSpec(Math.max(0, View.MeasureSpec.getSize(paramInt3) - (j + paramInt2 + paramInt1 + k + paramInt4)), 0));
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    StrictMode.Span localSpan = this.mScrollStrictSpan;
    if (localSpan != null)
    {
      localSpan.finish();
      this.mScrollStrictSpan = null;
    }
    localSpan = this.mFlingStrictSpan;
    if (localSpan != null)
    {
      localSpan.finish();
      this.mFlingStrictSpan = null;
    }
  }
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getAction() == 8)
    {
      float f;
      if (paramMotionEvent.isFromSource(2)) {
        f = paramMotionEvent.getAxisValue(9);
      } else if (paramMotionEvent.isFromSource(4194304)) {
        f = paramMotionEvent.getAxisValue(26);
      } else {
        f = 0.0F;
      }
      int i = Math.round(this.mVerticalScrollFactor * f);
      if (i != 0)
      {
        int j = getScrollRange();
        int k = this.mScrollY;
        int m = k - i;
        if (m < 0)
        {
          i = 0;
        }
        else
        {
          i = m;
          if (m > j) {
            i = j;
          }
        }
        if (i != k)
        {
          super.scrollTo(this.mScrollX, i);
          return true;
        }
      }
    }
    return super.onGenericMotionEvent(paramMotionEvent);
  }
  
  public void onInitializeAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEventInternal(paramAccessibilityEvent);
    boolean bool;
    if (getScrollRange() > 0) {
      bool = true;
    } else {
      bool = false;
    }
    paramAccessibilityEvent.setScrollable(bool);
    paramAccessibilityEvent.setScrollX(this.mScrollX);
    paramAccessibilityEvent.setScrollY(this.mScrollY);
    paramAccessibilityEvent.setMaxScrollX(this.mScrollX);
    paramAccessibilityEvent.setMaxScrollY(getScrollRange());
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    if (isEnabled())
    {
      int i = getScrollRange();
      if (i > 0)
      {
        paramAccessibilityNodeInfo.setScrollable(true);
        if (this.mScrollY > 0)
        {
          paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
          paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP);
        }
        if (this.mScrollY < i)
        {
          paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
          paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN);
        }
      }
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    if ((i == 2) && (this.mIsBeingDragged)) {
      return true;
    }
    if (super.onInterceptTouchEvent(paramMotionEvent)) {
      return true;
    }
    if ((getScrollY() == 0) && (!canScrollVertically(1))) {
      return false;
    }
    i &= 0xFF;
    if (i != 0)
    {
      if (i != 1) {
        if (i != 2)
        {
          if (i != 3)
          {
            if (i != 6) {
              break label408;
            }
            onSecondaryPointerUp(paramMotionEvent);
            break label408;
          }
        }
        else
        {
          int j = this.mActivePointerId;
          if (j == -1) {
            break label408;
          }
          i = paramMotionEvent.findPointerIndex(j);
          if (i == -1)
          {
            paramMotionEvent = new StringBuilder();
            paramMotionEvent.append("Invalid pointerId=");
            paramMotionEvent.append(j);
            paramMotionEvent.append(" in onInterceptTouchEvent");
            Log.e("ScrollView", paramMotionEvent.toString());
            break label408;
          }
          i = (int)paramMotionEvent.getY(i);
          if ((Math.abs(i - this.mLastMotionY) <= this.mTouchSlop) || ((0x2 & getNestedScrollAxes()) != 0)) {
            break label408;
          }
          this.mIsBeingDragged = true;
          this.mLastMotionY = i;
          initVelocityTrackerIfNotExists();
          this.mVelocityTracker.addMovement(paramMotionEvent);
          this.mNestedYOffset = 0;
          if (this.mScrollStrictSpan == null) {
            this.mScrollStrictSpan = StrictMode.enterCriticalSpan("ScrollView-scroll");
          }
          paramMotionEvent = getParent();
          if (paramMotionEvent != null) {
            paramMotionEvent.requestDisallowInterceptTouchEvent(true);
          }
          break label408;
        }
      }
      this.mIsBeingDragged = false;
      this.mActivePointerId = -1;
      recycleVelocityTracker();
      if (this.mScroller.springBack(this.mScrollX, this.mScrollY, 0, 0, 0, getScrollRange())) {
        postInvalidateOnAnimation();
      }
      stopNestedScroll();
    }
    else
    {
      i = (int)paramMotionEvent.getY();
      if (!inChild((int)paramMotionEvent.getX(), i))
      {
        this.mIsBeingDragged = false;
        recycleVelocityTracker();
      }
      else
      {
        this.mLastMotionY = i;
        this.mActivePointerId = paramMotionEvent.getPointerId(0);
        initOrResetVelocityTracker();
        this.mVelocityTracker.addMovement(paramMotionEvent);
        this.mScroller.computeScrollOffset();
        this.mIsBeingDragged = (true ^ this.mScroller.isFinished());
        if ((this.mIsBeingDragged) && (this.mScrollStrictSpan == null)) {
          this.mScrollStrictSpan = StrictMode.enterCriticalSpan("ScrollView-scroll");
        }
        startNestedScroll(2);
      }
    }
    label408:
    return this.mIsBeingDragged;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.mIsLayoutDirty = false;
    Object localObject = this.mChildToScrollTo;
    if ((localObject != null) && (isViewDescendantOf((View)localObject, this))) {
      scrollToDescendant(this.mChildToScrollTo);
    }
    this.mChildToScrollTo = null;
    if (!isLaidOut())
    {
      localObject = this.mSavedState;
      if (localObject != null)
      {
        this.mScrollY = ((SavedState)localObject).scrollPosition;
        this.mSavedState = null;
      }
      if (getChildCount() > 0) {
        paramInt1 = getChildAt(0).getMeasuredHeight();
      } else {
        paramInt1 = 0;
      }
      paramInt1 = Math.max(0, paramInt1 - (paramInt4 - paramInt2 - this.mPaddingBottom - this.mPaddingTop));
      if (this.mScrollY > paramInt1) {
        this.mScrollY = paramInt1;
      } else if (this.mScrollY < 0) {
        this.mScrollY = 0;
      }
    }
    scrollTo(this.mScrollX, this.mScrollY);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if (!this.mFillViewport) {
      return;
    }
    if (View.MeasureSpec.getMode(paramInt2) == 0) {
      return;
    }
    if (getChildCount() > 0)
    {
      View localView = getChildAt(0);
      paramInt2 = getContext().getApplicationInfo().targetSdkVersion;
      FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
      if (paramInt2 >= 23)
      {
        paramInt2 = this.mPaddingLeft + this.mPaddingRight + localLayoutParams.leftMargin + localLayoutParams.rightMargin;
        i = this.mPaddingTop + this.mPaddingBottom + localLayoutParams.topMargin + localLayoutParams.bottomMargin;
      }
      else
      {
        paramInt2 = this.mPaddingLeft + this.mPaddingRight;
        i = this.mPaddingTop + this.mPaddingBottom;
      }
      int i = getMeasuredHeight() - i;
      if (localView.getMeasuredHeight() < i) {
        localView.measure(getChildMeasureSpec(paramInt1, paramInt2, localLayoutParams.width), View.MeasureSpec.makeMeasureSpec(i, 1073741824));
      }
    }
  }
  
  public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      flingWithNestedDispatch((int)paramFloat2);
      return true;
    }
    return false;
  }
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt1 = this.mScrollY;
    scrollBy(0, paramInt4);
    paramInt1 = this.mScrollY - paramInt1;
    dispatchNestedScroll(0, paramInt1, 0, paramInt4 - paramInt1, null);
  }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt)
  {
    super.onNestedScrollAccepted(paramView1, paramView2, paramInt);
    startNestedScroll(2);
  }
  
  protected void onOverScrolled(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!this.mScroller.isFinished())
    {
      int i = this.mScrollX;
      int j = this.mScrollY;
      this.mScrollX = paramInt1;
      this.mScrollY = paramInt2;
      invalidateParentIfNeeded();
      onScrollChanged(this.mScrollX, this.mScrollY, i, j);
      if (paramBoolean2) {
        this.mScroller.springBack(this.mScrollX, this.mScrollY, 0, 0, 0, getScrollRange());
      }
    }
    else
    {
      super.scrollTo(paramInt1, paramInt2);
    }
    awakenScrollBars();
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    int i;
    if (paramInt == 2)
    {
      i = 130;
    }
    else
    {
      i = paramInt;
      if (paramInt == 1) {
        i = 33;
      }
    }
    View localView;
    if (paramRect == null) {
      localView = FocusFinder.getInstance().findNextFocus(this, null, i);
    } else {
      localView = FocusFinder.getInstance().findNextFocusFromRect(this, paramRect, i);
    }
    if (localView == null) {
      return false;
    }
    if (isOffScreen(localView)) {
      return false;
    }
    return localView.requestFocus(i, paramRect);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (this.mContext.getApplicationInfo().targetSdkVersion <= 18)
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    this.mSavedState = paramParcelable;
    requestLayout();
  }
  
  protected Parcelable onSaveInstanceState()
  {
    if (this.mContext.getApplicationInfo().targetSdkVersion <= 18) {
      return super.onSaveInstanceState();
    }
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.scrollPosition = this.mScrollY;
    return localSavedState;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    View localView = findFocus();
    if ((localView != null) && (this != localView))
    {
      if (isWithinDeltaOfScreen(localView, 0, paramInt4))
      {
        localView.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(localView, this.mTempRect);
        doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
      }
      return;
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
  
  public void onStopNestedScroll(View paramView)
  {
    super.onStopNestedScroll(paramView);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    initVelocityTrackerIfNotExists();
    MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
    int i = paramMotionEvent.getActionMasked();
    int j = 0;
    if (i == 0) {
      this.mNestedYOffset = 0;
    }
    localMotionEvent.offsetLocation(0.0F, this.mNestedYOffset);
    Object localObject;
    if (i != 0)
    {
      if (i != 1)
      {
        if (i != 2)
        {
          if (i != 3)
          {
            if (i != 5)
            {
              if (i == 6)
              {
                onSecondaryPointerUp(paramMotionEvent);
                this.mLastMotionY = ((int)paramMotionEvent.getY(paramMotionEvent.findPointerIndex(this.mActivePointerId)));
              }
            }
            else
            {
              i = paramMotionEvent.getActionIndex();
              this.mLastMotionY = ((int)paramMotionEvent.getY(i));
              this.mActivePointerId = paramMotionEvent.getPointerId(i);
            }
          }
          else if ((this.mIsBeingDragged) && (getChildCount() > 0))
          {
            if (this.mScroller.springBack(this.mScrollX, this.mScrollY, 0, 0, 0, getScrollRange())) {
              postInvalidateOnAnimation();
            }
            this.mActivePointerId = -1;
            endDrag();
          }
        }
        else
        {
          int k = paramMotionEvent.findPointerIndex(this.mActivePointerId);
          if (k == -1)
          {
            paramMotionEvent = new StringBuilder();
            paramMotionEvent.append("Invalid pointerId=");
            paramMotionEvent.append(this.mActivePointerId);
            paramMotionEvent.append(" in onTouchEvent");
            Log.e("ScrollView", paramMotionEvent.toString());
          }
          else
          {
            int m = (int)paramMotionEvent.getY(k);
            int n = this.mLastMotionY - m;
            i = n;
            if (dispatchNestedPreScroll(0, n, this.mScrollConsumed, this.mScrollOffset))
            {
              i = n - this.mScrollConsumed[1];
              localMotionEvent.offsetLocation(0.0F, this.mScrollOffset[1]);
              this.mNestedYOffset += this.mScrollOffset[1];
            }
            if ((!this.mIsBeingDragged) && (Math.abs(i) > this.mTouchSlop))
            {
              localObject = getParent();
              if (localObject != null) {
                ((ViewParent)localObject).requestDisallowInterceptTouchEvent(true);
              }
              this.mIsBeingDragged = true;
              if (i > 0) {
                i -= this.mTouchSlop;
              } else {
                i += this.mTouchSlop;
              }
            }
            if (this.mIsBeingDragged)
            {
              this.mLastMotionY = (m - this.mScrollOffset[1]);
              int i1 = this.mScrollY;
              m = getScrollRange();
              int i2 = getOverScrollMode();
              if (i2 != 0)
              {
                n = j;
                if (i2 == 1)
                {
                  n = j;
                  if (m <= 0) {}
                }
              }
              else
              {
                n = 1;
              }
              if ((overScrollBy(0, i, 0, this.mScrollY, 0, m, 0, this.mOverscrollDistance, true)) && (!hasNestedScrollingParent())) {
                this.mVelocityTracker.clear();
              }
              j = this.mScrollY - i1;
              if (dispatchNestedScroll(0, j, 0, i - j, this.mScrollOffset))
              {
                i = this.mLastMotionY;
                paramMotionEvent = this.mScrollOffset;
                this.mLastMotionY = (i - paramMotionEvent[1]);
                localMotionEvent.offsetLocation(0.0F, paramMotionEvent[1]);
                this.mNestedYOffset += this.mScrollOffset[1];
              }
              else if (n != 0)
              {
                n = i1 + i;
                if (n < 0)
                {
                  this.mEdgeGlowTop.onPull(i / getHeight(), paramMotionEvent.getX(k) / getWidth());
                  if (!this.mEdgeGlowBottom.isFinished()) {
                    this.mEdgeGlowBottom.onRelease();
                  }
                }
                else if (n > m)
                {
                  this.mEdgeGlowBottom.onPull(i / getHeight(), 1.0F - paramMotionEvent.getX(k) / getWidth());
                  if (!this.mEdgeGlowTop.isFinished()) {
                    this.mEdgeGlowTop.onRelease();
                  }
                }
                if ((shouldDisplayEdgeEffects()) && ((!this.mEdgeGlowTop.isFinished()) || (!this.mEdgeGlowBottom.isFinished()))) {
                  postInvalidateOnAnimation();
                }
              }
            }
          }
        }
      }
      else if (this.mIsBeingDragged)
      {
        paramMotionEvent = this.mVelocityTracker;
        paramMotionEvent.computeCurrentVelocity(1000, this.mMaximumVelocity);
        i = (int)paramMotionEvent.getYVelocity(this.mActivePointerId);
        if (Math.abs(i) > this.mMinimumVelocity) {
          flingWithNestedDispatch(-i);
        } else if (this.mScroller.springBack(this.mScrollX, this.mScrollY, 0, 0, 0, getScrollRange())) {
          postInvalidateOnAnimation();
        }
        this.mActivePointerId = -1;
        endDrag();
      }
    }
    else
    {
      if (getChildCount() == 0) {
        return false;
      }
      boolean bool = this.mScroller.isFinished() ^ true;
      this.mIsBeingDragged = bool;
      if (bool)
      {
        localObject = getParent();
        if (localObject != null) {
          ((ViewParent)localObject).requestDisallowInterceptTouchEvent(true);
        }
      }
      if (!this.mScroller.isFinished())
      {
        this.mScroller.abortAnimation();
        localObject = this.mFlingStrictSpan;
        if (localObject != null)
        {
          ((StrictMode.Span)localObject).finish();
          this.mFlingStrictSpan = null;
        }
      }
      this.mLastMotionY = ((int)paramMotionEvent.getY());
      this.mActivePointerId = paramMotionEvent.getPointerId(0);
      startNestedScroll(2);
    }
    paramMotionEvent = this.mVelocityTracker;
    if (paramMotionEvent != null) {
      paramMotionEvent.addMovement(localMotionEvent);
    }
    localMotionEvent.recycle();
    return true;
  }
  
  public boolean pageScroll(int paramInt)
  {
    int i;
    if (paramInt == 130) {
      i = 1;
    } else {
      i = 0;
    }
    int j = getHeight();
    if (i != 0)
    {
      this.mTempRect.top = (getScrollY() + j);
      i = getChildCount();
      if (i > 0)
      {
        localObject = getChildAt(i - 1);
        if (this.mTempRect.top + j > ((View)localObject).getBottom()) {
          this.mTempRect.top = (((View)localObject).getBottom() - j);
        }
      }
    }
    else
    {
      this.mTempRect.top = (getScrollY() - j);
      if (this.mTempRect.top < 0) {
        this.mTempRect.top = 0;
      }
    }
    Object localObject = this.mTempRect;
    ((Rect)localObject).bottom = (((Rect)localObject).top + j);
    return scrollAndFocus(paramInt, this.mTempRect.top, this.mTempRect.bottom);
  }
  
  public boolean performAccessibilityActionInternal(int paramInt, Bundle paramBundle)
  {
    if (super.performAccessibilityActionInternal(paramInt, paramBundle)) {
      return true;
    }
    if (!isEnabled()) {
      return false;
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
        i = getHeight();
        paramInt = this.mPaddingBottom;
        j = this.mPaddingTop;
        paramInt = Math.max(this.mScrollY - (i - paramInt - j), 0);
        if (paramInt != this.mScrollY)
        {
          smoothScrollTo(0, paramInt);
          return true;
        }
        return false;
      }
    }
    int i = getHeight();
    paramInt = this.mPaddingBottom;
    int j = this.mPaddingTop;
    paramInt = Math.min(this.mScrollY + (i - paramInt - j), getScrollRange());
    if (paramInt != this.mScrollY)
    {
      smoothScrollTo(0, paramInt);
      return true;
    }
    return false;
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    if ((paramView2 != null) && (paramView2.getRevealOnFocusHint())) {
      if (!this.mIsLayoutDirty) {
        scrollToDescendant(paramView2);
      } else {
        this.mChildToScrollTo = paramView2;
      }
    }
    super.requestChildFocus(paramView1, paramView2);
  }
  
  public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean)
  {
    paramRect.offset(paramView.getLeft() - paramView.getScrollX(), paramView.getTop() - paramView.getScrollY());
    return scrollToChildRect(paramRect, paramBoolean);
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
    this.mIsLayoutDirty = true;
    super.requestLayout();
  }
  
  public void scrollTo(int paramInt1, int paramInt2)
  {
    if (getChildCount() > 0)
    {
      View localView = getChildAt(0);
      paramInt1 = clamp(paramInt1, getWidth() - this.mPaddingRight - this.mPaddingLeft, localView.getWidth());
      paramInt2 = clamp(paramInt2, getHeight() - this.mPaddingBottom - this.mPaddingTop, localView.getHeight());
      if ((paramInt1 != this.mScrollX) || (paramInt2 != this.mScrollY)) {
        super.scrollTo(paramInt1, paramInt2);
      }
    }
  }
  
  public void scrollToDescendant(View paramView)
  {
    if (!this.mIsLayoutDirty)
    {
      paramView.getDrawingRect(this.mTempRect);
      offsetDescendantRectToMyCoords(paramView, this.mTempRect);
      int i = computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
      if (i != 0) {
        scrollBy(0, i);
      }
    }
    else
    {
      this.mChildToScrollTo = paramView;
    }
  }
  
  public void setBottomEdgeEffectColor(int paramInt)
  {
    this.mEdgeGlowBottom.setColor(paramInt);
  }
  
  public void setEdgeEffectColor(int paramInt)
  {
    setTopEdgeEffectColor(paramInt);
    setBottomEdgeEffectColor(paramInt);
  }
  
  public void setFillViewport(boolean paramBoolean)
  {
    if (paramBoolean != this.mFillViewport)
    {
      this.mFillViewport = paramBoolean;
      requestLayout();
    }
  }
  
  public void setSmoothScrollingEnabled(boolean paramBoolean)
  {
    this.mSmoothScrollingEnabled = paramBoolean;
  }
  
  public void setTopEdgeEffectColor(int paramInt)
  {
    this.mEdgeGlowTop.setColor(paramInt);
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return true;
  }
  
  public final void smoothScrollBy(int paramInt1, int paramInt2)
  {
    if (getChildCount() == 0) {
      return;
    }
    if (AnimationUtils.currentAnimationTimeMillis() - this.mLastScroll > 250L)
    {
      int i = getHeight();
      int j = this.mPaddingBottom;
      paramInt1 = this.mPaddingTop;
      i = Math.max(0, getChildAt(0).getHeight() - (i - j - paramInt1));
      paramInt1 = this.mScrollY;
      paramInt2 = Math.max(0, Math.min(paramInt1 + paramInt2, i));
      this.mScroller.startScroll(this.mScrollX, paramInt1, 0, paramInt2 - paramInt1);
      postInvalidateOnAnimation();
    }
    else
    {
      if (!this.mScroller.isFinished())
      {
        this.mScroller.abortAnimation();
        StrictMode.Span localSpan = this.mFlingStrictSpan;
        if (localSpan != null)
        {
          localSpan.finish();
          this.mFlingStrictSpan = null;
        }
      }
      scrollBy(paramInt1, paramInt2);
    }
    this.mLastScroll = AnimationUtils.currentAnimationTimeMillis();
  }
  
  public final void smoothScrollTo(int paramInt1, int paramInt2)
  {
    smoothScrollBy(paramInt1 - this.mScrollX, paramInt2 - this.mScrollY);
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public ScrollView.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ScrollView.SavedState(paramAnonymousParcel);
      }
      
      public ScrollView.SavedState[] newArray(int paramAnonymousInt)
      {
        return new ScrollView.SavedState[paramAnonymousInt];
      }
    };
    public int scrollPosition;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.scrollPosition = paramParcel.readInt();
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("ScrollView.SavedState{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(" scrollPosition=");
      localStringBuilder.append(this.scrollPosition);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.scrollPosition);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ScrollView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */