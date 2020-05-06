package android.widget;

import android.R.styleable;
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
import java.util.ArrayList;
import java.util.List;

public class HorizontalScrollView
  extends FrameLayout
{
  private static final int ANIMATED_SCROLL_GAP = 250;
  private static final int INVALID_POINTER = -1;
  private static final float MAX_SCROLL_FACTOR = 0.5F;
  private static final String TAG = "HorizontalScrollView";
  private int mActivePointerId = -1;
  @UnsupportedAppUsage
  private View mChildToScrollTo = null;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=124053130L)
  private EdgeEffect mEdgeGlowLeft = new EdgeEffect(getContext());
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=124052619L)
  private EdgeEffect mEdgeGlowRight = new EdgeEffect(getContext());
  @ViewDebug.ExportedProperty(category="layout")
  private boolean mFillViewport;
  private float mHorizontalScrollFactor;
  @UnsupportedAppUsage
  private boolean mIsBeingDragged = false;
  private boolean mIsLayoutDirty = true;
  @UnsupportedAppUsage
  private int mLastMotionX;
  private long mLastScroll;
  private int mMaximumVelocity;
  private int mMinimumVelocity;
  @UnsupportedAppUsage
  private int mOverflingDistance;
  @UnsupportedAppUsage
  private int mOverscrollDistance;
  private SavedState mSavedState;
  @UnsupportedAppUsage
  private OverScroller mScroller;
  private boolean mSmoothScrollingEnabled = true;
  private final Rect mTempRect = new Rect();
  private int mTouchSlop;
  @UnsupportedAppUsage
  private VelocityTracker mVelocityTracker;
  
  public HorizontalScrollView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public HorizontalScrollView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16843603);
  }
  
  public HorizontalScrollView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public HorizontalScrollView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    initScrollView();
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.HorizontalScrollView, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.HorizontalScrollView, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    setFillViewport(localTypedArray.getBoolean(0, false));
    localTypedArray.recycle();
    if (paramContext.getResources().getConfiguration().uiMode == 6) {
      setRevealOnFocusHint(false);
    }
  }
  
  private boolean canScroll()
  {
    boolean bool = false;
    View localView = getChildAt(0);
    if (localView != null)
    {
      int i = localView.getWidth();
      if (getWidth() < this.mPaddingLeft + i + this.mPaddingRight) {
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
  
  private void doScrollX(int paramInt)
  {
    if (paramInt != 0) {
      if (this.mSmoothScrollingEnabled) {
        smoothScrollBy(paramInt, 0);
      } else {
        scrollBy(paramInt, 0);
      }
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
      int m = localView.getLeft();
      int n = localView.getRight();
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
            if (((paramBoolean) && (m < ((View)localObject1).getLeft())) || ((!paramBoolean) && (n > ((View)localObject1).getRight()))) {
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
  
  private View findFocusableViewInMyBounds(boolean paramBoolean, int paramInt, View paramView)
  {
    int i = getHorizontalFadingEdgeLength() / 2;
    int j = paramInt + i;
    paramInt = getWidth() + paramInt - i;
    if ((paramView != null) && (paramView.getLeft() < paramInt) && (paramView.getRight() > j)) {
      return paramView;
    }
    return findFocusableViewInBounds(paramBoolean, j, paramInt);
  }
  
  private int getScrollRange()
  {
    int i = 0;
    if (getChildCount() > 0)
    {
      View localView = getChildAt(0);
      i = Math.max(0, localView.getWidth() - (getWidth() - this.mPaddingLeft - this.mPaddingRight));
    }
    return i;
  }
  
  private boolean inChild(int paramInt1, int paramInt2)
  {
    int i = getChildCount();
    boolean bool = false;
    if (i > 0)
    {
      i = this.mScrollX;
      View localView = getChildAt(0);
      if ((paramInt2 >= localView.getTop()) && (paramInt2 < localView.getBottom()) && (paramInt1 >= localView.getLeft() - i) && (paramInt1 < localView.getRight() - i)) {
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
    this.mHorizontalScrollFactor = localViewConfiguration.getScaledHorizontalScrollFactor();
  }
  
  private void initVelocityTrackerIfNotExists()
  {
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
  }
  
  private boolean isOffScreen(View paramView)
  {
    return isWithinDeltaOfScreen(paramView, 0) ^ true;
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
  
  private boolean isWithinDeltaOfScreen(View paramView, int paramInt)
  {
    paramView.getDrawingRect(this.mTempRect);
    offsetDescendantRectToMyCoords(paramView, this.mTempRect);
    boolean bool;
    if ((this.mTempRect.right + paramInt >= getScrollX()) && (this.mTempRect.left - paramInt <= getScrollX() + getWidth())) {
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
      this.mLastMotionX = ((int)paramMotionEvent.getX(i));
      this.mActivePointerId = paramMotionEvent.getPointerId(i);
      paramMotionEvent = this.mVelocityTracker;
      if (paramMotionEvent != null) {
        paramMotionEvent.clear();
      }
    }
  }
  
  @UnsupportedAppUsage
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
    int i = getWidth();
    int j = getScrollX();
    i = j + i;
    boolean bool2;
    if (paramInt1 == 17) {
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
      doScrollX(paramInt2);
      bool2 = bool1;
    }
    if (localObject != findFocus()) {
      ((View)localObject).requestFocus(paramInt1);
    }
    return bool2;
  }
  
  private void scrollToChild(View paramView)
  {
    paramView.getDrawingRect(this.mTempRect);
    offsetDescendantRectToMyCoords(paramView, this.mTempRect);
    int i = computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
    if (i != 0) {
      scrollBy(i, 0);
    }
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
        scrollBy(i, 0);
      } else {
        smoothScrollBy(i, 0);
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
    throw new IllegalStateException("HorizontalScrollView can host only one direct child");
  }
  
  public void addView(View paramView, int paramInt)
  {
    if (getChildCount() <= 0)
    {
      super.addView(paramView, paramInt);
      return;
    }
    throw new IllegalStateException("HorizontalScrollView can host only one direct child");
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    if (getChildCount() <= 0)
    {
      super.addView(paramView, paramInt, paramLayoutParams);
      return;
    }
    throw new IllegalStateException("HorizontalScrollView can host only one direct child");
  }
  
  public void addView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    if (getChildCount() <= 0)
    {
      super.addView(paramView, paramLayoutParams);
      return;
    }
    throw new IllegalStateException("HorizontalScrollView can host only one direct child");
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
    if ((localView1 != null) && (isWithinDeltaOfScreen(localView1, i)))
    {
      localView1.getDrawingRect(this.mTempRect);
      offsetDescendantRectToMyCoords(localView1, this.mTempRect);
      doScrollX(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
      localView1.requestFocus(paramInt);
    }
    else
    {
      int j = i;
      int k;
      if ((paramInt == 17) && (getScrollX() < j))
      {
        k = getScrollX();
      }
      else
      {
        k = j;
        if (paramInt == 66)
        {
          k = j;
          if (getChildCount() > 0)
          {
            int m = getChildAt(0).getRight();
            int n = getScrollX() + getWidth();
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
      if (paramInt == 66) {
        paramInt = k;
      } else {
        paramInt = -k;
      }
      doScrollX(paramInt);
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
  
  protected int computeHorizontalScrollOffset()
  {
    return Math.max(0, super.computeHorizontalScrollOffset());
  }
  
  protected int computeHorizontalScrollRange()
  {
    int i = getChildCount();
    int j = getWidth() - this.mPaddingLeft - this.mPaddingRight;
    if (i == 0) {
      return j;
    }
    i = getChildAt(0).getRight();
    int k = this.mScrollX;
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
        overScrollBy(k - i, m - j, i, j, n, 0, this.mOverflingDistance, 0, false);
        onScrollChanged(this.mScrollX, this.mScrollY, i, j);
        if (i3 != 0) {
          if ((k < 0) && (i >= 0)) {
            this.mEdgeGlowLeft.onAbsorb((int)this.mScroller.getCurrVelocity());
          } else if ((k > n) && (i <= n)) {
            this.mEdgeGlowRight.onAbsorb((int)this.mScroller.getCurrVelocity());
          }
        }
      }
      if (!awakenScrollBars()) {
        postInvalidateOnAnimation();
      }
    }
  }
  
  protected int computeScrollDeltaToGetChildRectOnScreen(Rect paramRect)
  {
    if (getChildCount() == 0) {
      return 0;
    }
    int i = getWidth();
    int j = getScrollX();
    int k = j + i;
    int m = getHorizontalFadingEdgeLength();
    int n = j;
    if (paramRect.left > 0) {
      n = j + m;
    }
    j = k;
    if (paramRect.right < getChildAt(0).getWidth()) {
      j = k - m;
    }
    m = 0;
    if ((paramRect.right > j) && (paramRect.left > n))
    {
      if (paramRect.width() > i) {
        k = 0 + (paramRect.left - n);
      } else {
        k = 0 + (paramRect.right - j);
      }
      k = Math.min(k, getChildAt(0).getRight() - j);
    }
    for (;;)
    {
      break;
      k = m;
      if (paramRect.left < n)
      {
        k = m;
        if (paramRect.right < j)
        {
          if (paramRect.width() > i) {
            k = 0 - (j - paramRect.right);
          } else {
            k = 0 - (n - paramRect.left);
          }
          k = Math.max(k, -getScrollX());
        }
      }
    }
    return k;
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
      int i = this.mScrollX;
      int j;
      int k;
      if (!this.mEdgeGlowLeft.isFinished())
      {
        j = paramCanvas.save();
        k = getHeight() - this.mPaddingTop - this.mPaddingBottom;
        paramCanvas.rotate(270.0F);
        paramCanvas.translate(-k + this.mPaddingTop, Math.min(0, i));
        this.mEdgeGlowLeft.setSize(k, getWidth());
        if (this.mEdgeGlowLeft.draw(paramCanvas)) {
          postInvalidateOnAnimation();
        }
        paramCanvas.restoreToCount(j);
      }
      if (!this.mEdgeGlowRight.isFinished())
      {
        j = paramCanvas.save();
        int m = getWidth();
        k = getHeight();
        int n = this.mPaddingTop;
        int i1 = this.mPaddingBottom;
        paramCanvas.rotate(90.0F);
        paramCanvas.translate(-this.mPaddingTop, -(Math.max(getScrollRange(), i) + m));
        this.mEdgeGlowRight.setSize(k - n - i1, m);
        if (this.mEdgeGlowRight.draw(paramCanvas)) {
          postInvalidateOnAnimation();
        }
        paramCanvas.restoreToCount(j);
      }
    }
  }
  
  protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    super.encodeProperties(paramViewHierarchyEncoder);
    paramViewHierarchyEncoder.addProperty("layout:fillViewPort", this.mFillViewport);
  }
  
  public boolean executeKeyEvent(KeyEvent paramKeyEvent)
  {
    this.mTempRect.setEmpty();
    boolean bool1 = canScroll();
    int i = 66;
    if (!bool1)
    {
      bool2 = isFocused();
      bool1 = false;
      if (bool2)
      {
        View localView = findFocus();
        paramKeyEvent = localView;
        if (localView == this) {
          paramKeyEvent = null;
        }
        paramKeyEvent = FocusFinder.getInstance().findNextFocus(this, paramKeyEvent, 66);
        if ((paramKeyEvent != null) && (paramKeyEvent != this) && (paramKeyEvent.requestFocus(66))) {
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
      if (j != 21)
      {
        if (j != 22)
        {
          if (j != 62)
          {
            bool1 = bool2;
          }
          else
          {
            if (paramKeyEvent.isShiftPressed()) {
              i = 17;
            }
            pageScroll(i);
            bool1 = bool2;
          }
        }
        else if (!paramKeyEvent.isAltPressed()) {
          bool1 = arrowScroll(66);
        } else {
          bool1 = fullScroll(66);
        }
      }
      else if (!paramKeyEvent.isAltPressed()) {
        bool1 = arrowScroll(17);
      } else {
        bool1 = fullScroll(17);
      }
    }
    return bool1;
  }
  
  public void fling(int paramInt)
  {
    if (getChildCount() > 0)
    {
      int i = getWidth() - this.mPaddingRight - this.mPaddingLeft;
      boolean bool = false;
      int j = getChildAt(0).getWidth();
      this.mScroller.fling(this.mScrollX, this.mScrollY, paramInt, 0, 0, Math.max(0, j - i), 0, 0, i / 2, 0);
      if (paramInt > 0) {
        bool = true;
      }
      View localView1 = findFocus();
      View localView2 = findFocusableViewInMyBounds(bool, this.mScroller.getFinalX(), localView1);
      Object localObject = localView2;
      if (localView2 == null) {
        localObject = this;
      }
      if (localObject != localView1)
      {
        if (bool) {
          paramInt = 66;
        } else {
          paramInt = 17;
        }
        ((View)localObject).requestFocus(paramInt);
      }
      postInvalidateOnAnimation();
    }
  }
  
  public boolean fullScroll(int paramInt)
  {
    int i;
    if (paramInt == 66) {
      i = 1;
    } else {
      i = 0;
    }
    int j = getWidth();
    Object localObject = this.mTempRect;
    ((Rect)localObject).left = 0;
    ((Rect)localObject).right = j;
    if ((i != 0) && (getChildCount() > 0))
    {
      localObject = getChildAt(0);
      this.mTempRect.right = ((View)localObject).getRight();
      localObject = this.mTempRect;
      ((Rect)localObject).left = (((Rect)localObject).right - j);
    }
    return scrollAndFocus(paramInt, this.mTempRect.left, this.mTempRect.right);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return HorizontalScrollView.class.getName();
  }
  
  public int getLeftEdgeEffectColor()
  {
    return this.mEdgeGlowLeft.getColor();
  }
  
  protected float getLeftFadingEdgeStrength()
  {
    if (getChildCount() == 0) {
      return 0.0F;
    }
    int i = getHorizontalFadingEdgeLength();
    if (this.mScrollX < i) {
      return this.mScrollX / i;
    }
    return 1.0F;
  }
  
  public int getMaxScrollAmount()
  {
    return (int)((this.mRight - this.mLeft) * 0.5F);
  }
  
  public int getRightEdgeEffectColor()
  {
    return this.mEdgeGlowRight.getColor();
  }
  
  protected float getRightFadingEdgeStrength()
  {
    if (getChildCount() == 0) {
      return 0.0F;
    }
    int i = getHorizontalFadingEdgeLength();
    int j = getWidth();
    int k = this.mPaddingRight;
    j = getChildAt(0).getRight() - this.mScrollX - (j - k);
    if (j < i) {
      return j / i;
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
    int i = this.mPaddingLeft;
    int j = this.mPaddingRight;
    paramView.measure(View.MeasureSpec.makeSafeMeasureSpec(Math.max(0, View.MeasureSpec.getSize(paramInt1) - (i + j)), 0), getChildMeasureSpec(paramInt2, this.mPaddingTop + this.mPaddingBottom, localLayoutParams.height));
  }
  
  protected void measureChildWithMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    int i = getChildMeasureSpec(paramInt3, this.mPaddingTop + this.mPaddingBottom + localMarginLayoutParams.topMargin + localMarginLayoutParams.bottomMargin + paramInt4, localMarginLayoutParams.height);
    paramInt4 = this.mPaddingLeft;
    paramInt3 = this.mPaddingRight;
    int j = localMarginLayoutParams.leftMargin;
    int k = localMarginLayoutParams.rightMargin;
    paramView.measure(View.MeasureSpec.makeSafeMeasureSpec(Math.max(0, View.MeasureSpec.getSize(paramInt1) - (paramInt4 + paramInt3 + j + k + paramInt2)), 0), i);
  }
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    if ((paramMotionEvent.getAction() == 8) && (!this.mIsBeingDragged))
    {
      float f;
      if (paramMotionEvent.isFromSource(2))
      {
        if ((paramMotionEvent.getMetaState() & 0x1) != 0) {
          f = -paramMotionEvent.getAxisValue(9);
        } else {
          f = paramMotionEvent.getAxisValue(10);
        }
      }
      else if (paramMotionEvent.isFromSource(4194304)) {
        f = paramMotionEvent.getAxisValue(26);
      } else {
        f = 0.0F;
      }
      int i = Math.round(this.mHorizontalScrollFactor * f);
      if (i != 0)
      {
        int j = getScrollRange();
        int k = this.mScrollX;
        int m = k + i;
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
          super.scrollTo(i, this.mScrollY);
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
    paramAccessibilityEvent.setMaxScrollX(getScrollRange());
    paramAccessibilityEvent.setMaxScrollY(this.mScrollY);
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    int i = getScrollRange();
    if (i > 0)
    {
      paramAccessibilityNodeInfo.setScrollable(true);
      if ((isEnabled()) && (this.mScrollX > 0))
      {
        paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
        paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_LEFT);
      }
      if ((isEnabled()) && (this.mScrollX < i))
      {
        paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
        paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_RIGHT);
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
    i &= 0xFF;
    if (i != 0)
    {
      if (i != 1) {
        if (i != 2)
        {
          if (i != 3)
          {
            if (i != 5)
            {
              if (i != 6) {
                break label364;
              }
              onSecondaryPointerUp(paramMotionEvent);
              this.mLastMotionX = ((int)paramMotionEvent.getX(paramMotionEvent.findPointerIndex(this.mActivePointerId)));
              break label364;
            }
            i = paramMotionEvent.getActionIndex();
            this.mLastMotionX = ((int)paramMotionEvent.getX(i));
            this.mActivePointerId = paramMotionEvent.getPointerId(i);
            break label364;
          }
        }
        else
        {
          i = this.mActivePointerId;
          if (i == -1) {
            break label364;
          }
          int j = paramMotionEvent.findPointerIndex(i);
          if (j == -1)
          {
            paramMotionEvent = new StringBuilder();
            paramMotionEvent.append("Invalid pointerId=");
            paramMotionEvent.append(i);
            paramMotionEvent.append(" in onInterceptTouchEvent");
            Log.e("HorizontalScrollView", paramMotionEvent.toString());
            break label364;
          }
          i = (int)paramMotionEvent.getX(j);
          if (Math.abs(i - this.mLastMotionX) <= this.mTouchSlop) {
            break label364;
          }
          this.mIsBeingDragged = true;
          this.mLastMotionX = i;
          initVelocityTrackerIfNotExists();
          this.mVelocityTracker.addMovement(paramMotionEvent);
          if (this.mParent == null) {
            break label364;
          }
          this.mParent.requestDisallowInterceptTouchEvent(true);
          break label364;
        }
      }
      this.mIsBeingDragged = false;
      this.mActivePointerId = -1;
      if (this.mScroller.springBack(this.mScrollX, this.mScrollY, 0, getScrollRange(), 0, 0)) {
        postInvalidateOnAnimation();
      }
    }
    else
    {
      i = (int)paramMotionEvent.getX();
      if (!inChild(i, (int)paramMotionEvent.getY()))
      {
        this.mIsBeingDragged = false;
        recycleVelocityTracker();
      }
      else
      {
        this.mLastMotionX = i;
        this.mActivePointerId = paramMotionEvent.getPointerId(0);
        initOrResetVelocityTracker();
        this.mVelocityTracker.addMovement(paramMotionEvent);
        this.mIsBeingDragged = (true ^ this.mScroller.isFinished());
      }
    }
    label364:
    return this.mIsBeingDragged;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i;
    int j;
    if (getChildCount() > 0)
    {
      i = getChildAt(0).getMeasuredWidth();
      localObject = (FrameLayout.LayoutParams)getChildAt(0).getLayoutParams();
      j = ((FrameLayout.LayoutParams)localObject).leftMargin;
      int k = ((FrameLayout.LayoutParams)localObject).rightMargin;
      j += k;
    }
    else
    {
      i = 0;
      j = 0;
    }
    if (i > paramInt3 - paramInt1 - getPaddingLeftWithForeground() - getPaddingRightWithForeground() - j) {
      paramBoolean = true;
    } else {
      paramBoolean = false;
    }
    layoutChildren(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
    this.mIsLayoutDirty = false;
    Object localObject = this.mChildToScrollTo;
    if ((localObject != null) && (isViewDescendantOf((View)localObject, this))) {
      scrollToChild(this.mChildToScrollTo);
    }
    this.mChildToScrollTo = null;
    if (!isLaidOut())
    {
      paramInt2 = Math.max(0, i - (paramInt3 - paramInt1 - this.mPaddingLeft - this.mPaddingRight));
      if (this.mSavedState != null)
      {
        if (isLayoutRtl()) {
          paramInt1 = paramInt2 - this.mSavedState.scrollOffsetFromStart;
        } else {
          paramInt1 = this.mSavedState.scrollOffsetFromStart;
        }
        this.mScrollX = paramInt1;
        this.mSavedState = null;
      }
      else if (isLayoutRtl())
      {
        this.mScrollX = (paramInt2 - this.mScrollX);
      }
      if (this.mScrollX > paramInt2) {
        this.mScrollX = paramInt2;
      } else if (this.mScrollX < 0) {
        this.mScrollX = 0;
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
    if (View.MeasureSpec.getMode(paramInt1) == 0) {
      return;
    }
    if (getChildCount() > 0)
    {
      View localView = getChildAt(0);
      FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
      if (getContext().getApplicationInfo().targetSdkVersion >= 23)
      {
        i = this.mPaddingLeft + this.mPaddingRight + localLayoutParams.leftMargin + localLayoutParams.rightMargin;
        paramInt1 = this.mPaddingTop + this.mPaddingBottom + localLayoutParams.topMargin + localLayoutParams.bottomMargin;
      }
      else
      {
        i = this.mPaddingLeft + this.mPaddingRight;
        paramInt1 = this.mPaddingTop + this.mPaddingBottom;
      }
      int i = getMeasuredWidth() - i;
      if (localView.getMeasuredWidth() < i) {
        localView.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), getChildMeasureSpec(paramInt2, paramInt1, localLayoutParams.height));
      }
    }
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
      if (paramBoolean1) {
        this.mScroller.springBack(this.mScrollX, this.mScrollY, 0, getScrollRange(), 0, 0);
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
      i = 66;
    }
    else
    {
      i = paramInt;
      if (paramInt == 1) {
        i = 17;
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
    int i;
    if (isLayoutRtl()) {
      i = -this.mScrollX;
    } else {
      i = this.mScrollX;
    }
    localSavedState.scrollOffsetFromStart = i;
    return localSavedState;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    View localView = findFocus();
    if ((localView != null) && (this != localView))
    {
      if (isWithinDeltaOfScreen(localView, this.mRight - this.mLeft))
      {
        localView.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(localView, this.mTempRect);
        doScrollX(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
      }
      return;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    initVelocityTrackerIfNotExists();
    this.mVelocityTracker.addMovement(paramMotionEvent);
    int i = paramMotionEvent.getAction() & 0xFF;
    int j = 0;
    ViewParent localViewParent;
    if (i != 0)
    {
      if (i != 1)
      {
        if (i != 2)
        {
          if (i != 3)
          {
            if (i == 6) {
              onSecondaryPointerUp(paramMotionEvent);
            }
          }
          else if ((this.mIsBeingDragged) && (getChildCount() > 0))
          {
            if (this.mScroller.springBack(this.mScrollX, this.mScrollY, 0, getScrollRange(), 0, 0)) {
              postInvalidateOnAnimation();
            }
            this.mActivePointerId = -1;
            this.mIsBeingDragged = false;
            recycleVelocityTracker();
            if (shouldDisplayEdgeEffects())
            {
              this.mEdgeGlowLeft.onRelease();
              this.mEdgeGlowRight.onRelease();
              break label749;
            }
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
            Log.e("HorizontalScrollView", paramMotionEvent.toString());
          }
          else
          {
            int m = (int)paramMotionEvent.getX(k);
            i = this.mLastMotionX - m;
            if ((!this.mIsBeingDragged) && (Math.abs(i) > this.mTouchSlop))
            {
              localViewParent = getParent();
              if (localViewParent != null) {
                localViewParent.requestDisallowInterceptTouchEvent(true);
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
              this.mLastMotionX = m;
              int n = this.mScrollX;
              m = this.mScrollY;
              int i1 = getScrollRange();
              int i2 = getOverScrollMode();
              if (i2 != 0)
              {
                m = j;
                if (i2 == 1)
                {
                  m = j;
                  if (i1 <= 0) {}
                }
              }
              else
              {
                m = 1;
              }
              if (overScrollBy(i, 0, this.mScrollX, 0, i1, 0, this.mOverscrollDistance, 0, true)) {
                this.mVelocityTracker.clear();
              }
              if (m != 0)
              {
                m = n + i;
                if (m < 0)
                {
                  this.mEdgeGlowLeft.onPull(i / getWidth(), 1.0F - paramMotionEvent.getY(k) / getHeight());
                  if (!this.mEdgeGlowRight.isFinished()) {
                    this.mEdgeGlowRight.onRelease();
                  }
                }
                else if (m > i1)
                {
                  this.mEdgeGlowRight.onPull(i / getWidth(), paramMotionEvent.getY(k) / getHeight());
                  if (!this.mEdgeGlowLeft.isFinished()) {
                    this.mEdgeGlowLeft.onRelease();
                  }
                }
                if ((shouldDisplayEdgeEffects()) && ((!this.mEdgeGlowLeft.isFinished()) || (!this.mEdgeGlowRight.isFinished()))) {
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
        i = (int)paramMotionEvent.getXVelocity(this.mActivePointerId);
        if (getChildCount() > 0) {
          if (Math.abs(i) > this.mMinimumVelocity) {
            fling(-i);
          } else if (this.mScroller.springBack(this.mScrollX, this.mScrollY, 0, getScrollRange(), 0, 0)) {
            postInvalidateOnAnimation();
          }
        }
        this.mActivePointerId = -1;
        this.mIsBeingDragged = false;
        recycleVelocityTracker();
        if (shouldDisplayEdgeEffects())
        {
          this.mEdgeGlowLeft.onRelease();
          this.mEdgeGlowRight.onRelease();
        }
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
        localViewParent = getParent();
        if (localViewParent != null) {
          localViewParent.requestDisallowInterceptTouchEvent(true);
        }
      }
      if (!this.mScroller.isFinished()) {
        this.mScroller.abortAnimation();
      }
      this.mLastMotionX = ((int)paramMotionEvent.getX());
      this.mActivePointerId = paramMotionEvent.getPointerId(0);
    }
    label749:
    return true;
  }
  
  public boolean pageScroll(int paramInt)
  {
    int i;
    if (paramInt == 66) {
      i = 1;
    } else {
      i = 0;
    }
    int j = getWidth();
    if (i != 0)
    {
      this.mTempRect.left = (getScrollX() + j);
      if (getChildCount() > 0)
      {
        localObject = getChildAt(0);
        if (this.mTempRect.left + j > ((View)localObject).getRight()) {
          this.mTempRect.left = (((View)localObject).getRight() - j);
        }
      }
    }
    else
    {
      this.mTempRect.left = (getScrollX() - j);
      if (this.mTempRect.left < 0) {
        this.mTempRect.left = 0;
      }
    }
    Object localObject = this.mTempRect;
    ((Rect)localObject).right = (((Rect)localObject).left + j);
    return scrollAndFocus(paramInt, this.mTempRect.left, this.mTempRect.right);
  }
  
  public boolean performAccessibilityActionInternal(int paramInt, Bundle paramBundle)
  {
    if (super.performAccessibilityActionInternal(paramInt, paramBundle)) {
      return true;
    }
    if (paramInt != 4096) {
      if ((paramInt != 8192) && (paramInt != 16908345))
      {
        if (paramInt != 16908347) {
          return false;
        }
      }
      else
      {
        if (!isEnabled()) {
          return false;
        }
        paramInt = getWidth();
        i = this.mPaddingLeft;
        j = this.mPaddingRight;
        paramInt = Math.max(0, this.mScrollX - (paramInt - i - j));
        if (paramInt != this.mScrollX)
        {
          smoothScrollTo(paramInt, 0);
          return true;
        }
        return false;
      }
    }
    if (!isEnabled()) {
      return false;
    }
    int j = getWidth();
    int i = this.mPaddingLeft;
    paramInt = this.mPaddingRight;
    paramInt = Math.min(this.mScrollX + (j - i - paramInt), getScrollRange());
    if (paramInt != this.mScrollX)
    {
      smoothScrollTo(paramInt, 0);
      return true;
    }
    return false;
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    if ((paramView2 != null) && (paramView2.getRevealOnFocusHint())) {
      if (!this.mIsLayoutDirty) {
        scrollToChild(paramView2);
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
  
  public void setEdgeEffectColor(int paramInt)
  {
    setLeftEdgeEffectColor(paramInt);
    setRightEdgeEffectColor(paramInt);
  }
  
  public void setFillViewport(boolean paramBoolean)
  {
    if (paramBoolean != this.mFillViewport)
    {
      this.mFillViewport = paramBoolean;
      requestLayout();
    }
  }
  
  public void setLeftEdgeEffectColor(int paramInt)
  {
    this.mEdgeGlowLeft.setColor(paramInt);
  }
  
  public void setRightEdgeEffectColor(int paramInt)
  {
    this.mEdgeGlowRight.setColor(paramInt);
  }
  
  public void setSmoothScrollingEnabled(boolean paramBoolean)
  {
    this.mSmoothScrollingEnabled = paramBoolean;
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
      int i = getWidth();
      paramInt2 = this.mPaddingRight;
      int j = this.mPaddingLeft;
      i = Math.max(0, getChildAt(0).getWidth() - (i - paramInt2 - j));
      paramInt2 = this.mScrollX;
      paramInt1 = Math.max(0, Math.min(paramInt2 + paramInt1, i));
      this.mScroller.startScroll(paramInt2, this.mScrollY, paramInt1 - paramInt2, 0);
      postInvalidateOnAnimation();
    }
    else
    {
      if (!this.mScroller.isFinished()) {
        this.mScroller.abortAnimation();
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
      public HorizontalScrollView.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new HorizontalScrollView.SavedState(paramAnonymousParcel);
      }
      
      public HorizontalScrollView.SavedState[] newArray(int paramAnonymousInt)
      {
        return new HorizontalScrollView.SavedState[paramAnonymousInt];
      }
    };
    public int scrollOffsetFromStart;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.scrollOffsetFromStart = paramParcel.readInt();
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("HorizontalScrollView.SavedState{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(" scrollPosition=");
      localStringBuilder.append(this.scrollOffsetFromStart);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.scrollOffsetFromStart);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/HorizontalScrollView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */