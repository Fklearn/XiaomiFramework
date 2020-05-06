package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Transformation;
import com.android.internal.R.styleable;

@Deprecated
public class Gallery
  extends AbsSpinner
  implements GestureDetector.OnGestureListener
{
  private static final int SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT = 250;
  private static final String TAG = "Gallery";
  private static final boolean localLOGV = false;
  private int mAnimationDuration = 400;
  private AdapterView.AdapterContextMenuInfo mContextMenuInfo;
  private Runnable mDisableSuppressSelectionChangedRunnable = new Runnable()
  {
    public void run()
    {
      Gallery.access$002(Gallery.this, false);
      Gallery.this.selectionChanged();
    }
  };
  @UnsupportedAppUsage
  private int mDownTouchPosition;
  @UnsupportedAppUsage
  private View mDownTouchView;
  @UnsupportedAppUsage
  private FlingRunnable mFlingRunnable = new FlingRunnable();
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private GestureDetector mGestureDetector;
  private int mGravity;
  private boolean mIsFirstScroll;
  private boolean mIsRtl = true;
  private int mLeftMost;
  private boolean mReceivedInvokeKeyDown;
  private int mRightMost;
  private int mSelectedCenterOffset;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private View mSelectedChild;
  private boolean mShouldCallbackDuringFling = true;
  private boolean mShouldCallbackOnUnselectedItemClick = true;
  private boolean mShouldStopFling;
  @UnsupportedAppUsage
  private int mSpacing = 0;
  private boolean mSuppressSelectionChanged;
  private float mUnselectedAlpha;
  
  public Gallery(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public Gallery(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842864);
  }
  
  public Gallery(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public Gallery(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Gallery, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.Gallery, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    paramInt1 = localTypedArray.getInt(0, -1);
    if (paramInt1 >= 0) {
      setGravity(paramInt1);
    }
    paramInt1 = localTypedArray.getInt(1, -1);
    if (paramInt1 > 0) {
      setAnimationDuration(paramInt1);
    }
    setSpacing(localTypedArray.getDimensionPixelOffset(2, 0));
    setUnselectedAlpha(localTypedArray.getFloat(3, 0.5F));
    localTypedArray.recycle();
    this.mGroupFlags |= 0x400;
    this.mGroupFlags |= 0x800;
  }
  
  private int calculateTop(View paramView, boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = getMeasuredHeight();
    } else {
      i = getHeight();
    }
    int j;
    if (paramBoolean) {
      j = paramView.getMeasuredHeight();
    } else {
      j = paramView.getHeight();
    }
    int k = 0;
    int m = this.mGravity;
    if (m != 16)
    {
      if (m != 48)
      {
        if (m != 80) {
          i = k;
        } else {
          i = i - this.mSpinnerPadding.bottom - j;
        }
      }
      else {
        i = this.mSpinnerPadding.top;
      }
    }
    else
    {
      m = this.mSpinnerPadding.bottom;
      k = this.mSpinnerPadding.top;
      i = this.mSpinnerPadding.top + (i - m - k - j) / 2;
    }
    return i;
  }
  
  private void detachOffScreenChildren(boolean paramBoolean)
  {
    int i = getChildCount();
    int j = this.mFirstPosition;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2;
    int i3;
    View localView;
    if (paramBoolean)
    {
      i2 = this.mPaddingLeft;
      i3 = 0;
      n = i1;
      k = m;
      while (i3 < i)
      {
        if (this.mIsRtl) {
          m = i - 1 - i3;
        } else {
          m = i3;
        }
        localView = getChildAt(m);
        if (localView.getRight() >= i2) {
          break;
        }
        k = m;
        n++;
        this.mRecycler.put(j + m, localView);
        i3++;
      }
      if (!this.mIsRtl) {
        k = 0;
      }
      i3 = n;
    }
    else
    {
      i2 = getWidth();
      i1 = this.mPaddingRight;
      for (i3 = i - 1; i3 >= 0; i3--)
      {
        if (this.mIsRtl) {
          m = i - 1 - i3;
        } else {
          m = i3;
        }
        localView = getChildAt(m);
        if (localView.getLeft() <= i2 - i1) {
          break;
        }
        k = m;
        n++;
        this.mRecycler.put(j + m, localView);
      }
      i3 = n;
      if (this.mIsRtl)
      {
        k = 0;
        i3 = n;
      }
    }
    detachViewsFromParent(k, i3);
    if (paramBoolean != this.mIsRtl) {
      this.mFirstPosition += i3;
    }
  }
  
  private boolean dispatchLongPress(View paramView, int paramInt, long paramLong, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    boolean bool1 = false;
    if (this.mOnItemLongClickListener != null) {
      bool1 = this.mOnItemLongClickListener.onItemLongClick(this, this.mDownTouchView, this.mDownTouchPosition, paramLong);
    }
    boolean bool2 = bool1;
    if (!bool1)
    {
      this.mContextMenuInfo = new AdapterView.AdapterContextMenuInfo(paramView, paramInt, paramLong);
      if (paramBoolean) {
        bool2 = super.showContextMenuForChild(paramView, paramFloat1, paramFloat2);
      } else {
        bool2 = super.showContextMenuForChild(this);
      }
    }
    if (bool2) {
      performHapticFeedback(0);
    }
    return bool2;
  }
  
  private void dispatchPress(View paramView)
  {
    if (paramView != null) {
      paramView.setPressed(true);
    }
    setPressed(true);
  }
  
  private void dispatchUnpress()
  {
    for (int i = getChildCount() - 1; i >= 0; i--) {
      getChildAt(i).setPressed(false);
    }
    setPressed(false);
  }
  
  @UnsupportedAppUsage
  private void fillToGalleryLeft()
  {
    if (this.mIsRtl) {
      fillToGalleryLeftRtl();
    } else {
      fillToGalleryLeftLtr();
    }
  }
  
  private void fillToGalleryLeftLtr()
  {
    int i = this.mSpacing;
    int j = this.mPaddingLeft;
    View localView = getChildAt(0);
    int k;
    int m;
    if (localView != null)
    {
      k = this.mFirstPosition - 1;
      m = localView.getLeft() - i;
    }
    else
    {
      k = 0;
      int n = this.mRight;
      int i1 = this.mLeft;
      m = this.mPaddingRight;
      this.mShouldStopFling = true;
      m = n - i1 - m;
    }
    while ((m > j) && (k >= 0))
    {
      localView = makeAndAddView(k, k - this.mSelectedPosition, m, false);
      this.mFirstPosition = k;
      m = localView.getLeft() - i;
      k--;
    }
  }
  
  private void fillToGalleryLeftRtl()
  {
    int i = this.mSpacing;
    int j = this.mPaddingLeft;
    int k = getChildCount();
    int m = this.mItemCount;
    View localView = getChildAt(k - 1);
    if (localView != null)
    {
      m = this.mFirstPosition;
      int n = localView.getLeft();
      m += k;
      k = n - i;
    }
    else
    {
      k = this.mItemCount - 1;
      m = k;
      this.mFirstPosition = k;
      k = this.mRight - this.mLeft - this.mPaddingRight;
      this.mShouldStopFling = true;
    }
    while ((k > j) && (m < this.mItemCount))
    {
      k = makeAndAddView(m, m - this.mSelectedPosition, k, false).getLeft() - i;
      m++;
    }
  }
  
  @UnsupportedAppUsage
  private void fillToGalleryRight()
  {
    if (this.mIsRtl) {
      fillToGalleryRightRtl();
    } else {
      fillToGalleryRightLtr();
    }
  }
  
  private void fillToGalleryRightLtr()
  {
    int i = this.mSpacing;
    int j = this.mRight;
    int k = this.mLeft;
    int m = this.mPaddingRight;
    int n = getChildCount();
    int i1 = this.mItemCount;
    View localView = getChildAt(n - 1);
    int i2;
    int i3;
    if (localView != null)
    {
      i2 = this.mFirstPosition;
      i3 = localView.getRight();
      i2 += n;
      i3 += i;
    }
    else
    {
      i3 = this.mItemCount - 1;
      i2 = i3;
      this.mFirstPosition = i3;
      i3 = this.mPaddingLeft;
      this.mShouldStopFling = true;
    }
    while ((i3 < j - k - m) && (i2 < i1))
    {
      i3 = makeAndAddView(i2, i2 - this.mSelectedPosition, i3, true).getRight() + i;
      i2++;
    }
  }
  
  private void fillToGalleryRightRtl()
  {
    int i = this.mSpacing;
    int j = this.mRight;
    int k = this.mLeft;
    int m = this.mPaddingRight;
    View localView = getChildAt(0);
    int n;
    int i1;
    if (localView != null)
    {
      n = this.mFirstPosition - 1;
      i1 = localView.getRight() + i;
    }
    else
    {
      n = 0;
      i1 = this.mPaddingLeft;
      this.mShouldStopFling = true;
    }
    while ((i1 < j - k - m) && (n >= 0))
    {
      localView = makeAndAddView(n, n - this.mSelectedPosition, i1, true);
      this.mFirstPosition = n;
      i1 = localView.getRight() + i;
      n--;
    }
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private int getCenterOfGallery()
  {
    return (getWidth() - this.mPaddingLeft - this.mPaddingRight) / 2 + this.mPaddingLeft;
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private static int getCenterOfView(View paramView)
  {
    return paramView.getLeft() + paramView.getWidth() / 2;
  }
  
  @UnsupportedAppUsage
  private View makeAndAddView(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if (!this.mDataChanged)
    {
      localView = this.mRecycler.get(paramInt1);
      if (localView != null)
      {
        paramInt1 = localView.getLeft();
        this.mRightMost = Math.max(this.mRightMost, localView.getMeasuredWidth() + paramInt1);
        this.mLeftMost = Math.min(this.mLeftMost, paramInt1);
        setUpChild(localView, paramInt2, paramInt3, paramBoolean);
        return localView;
      }
    }
    View localView = this.mAdapter.getView(paramInt1, null, this);
    setUpChild(localView, paramInt2, paramInt3, paramBoolean);
    return localView;
  }
  
  private void offsetChildrenLeftAndRight(int paramInt)
  {
    for (int i = getChildCount() - 1; i >= 0; i--) {
      getChildAt(i).offsetLeftAndRight(paramInt);
    }
  }
  
  private void onFinishedMovement()
  {
    if (this.mSuppressSelectionChanged)
    {
      this.mSuppressSelectionChanged = false;
      super.selectionChanged();
    }
    this.mSelectedCenterOffset = 0;
    invalidate();
  }
  
  private void scrollIntoSlots()
  {
    if (getChildCount() != 0)
    {
      View localView = this.mSelectedChild;
      if (localView != null)
      {
        int i = getCenterOfView(localView);
        i = getCenterOfGallery() - i;
        if (i != 0) {
          this.mFlingRunnable.startUsingDistance(i);
        } else {
          onFinishedMovement();
        }
        return;
      }
    }
  }
  
  private boolean scrollToChild(int paramInt)
  {
    View localView = getChildAt(paramInt);
    if (localView != null)
    {
      paramInt = getCenterOfGallery();
      int i = getCenterOfView(localView);
      this.mFlingRunnable.startUsingDistance(paramInt - i);
      return true;
    }
    return false;
  }
  
  private void setSelectionToCenterChild()
  {
    View localView = this.mSelectedChild;
    if (this.mSelectedChild == null) {
      return;
    }
    int i = getCenterOfGallery();
    if ((localView.getLeft() <= i) && (localView.getRight() >= i)) {
      return;
    }
    int j = Integer.MAX_VALUE;
    int k = 0;
    int m = getChildCount() - 1;
    int n;
    for (;;)
    {
      n = k;
      if (m < 0) {
        break;
      }
      localView = getChildAt(m);
      if ((localView.getLeft() <= i) && (localView.getRight() >= i))
      {
        n = m;
        break;
      }
      int i1 = Math.min(Math.abs(localView.getLeft() - i), Math.abs(localView.getRight() - i));
      n = j;
      if (i1 < j)
      {
        n = i1;
        k = m;
      }
      m--;
      j = n;
    }
    m = this.mFirstPosition + n;
    if (m != this.mSelectedPosition)
    {
      setSelectedPositionInt(m);
      setNextSelectedPositionInt(m);
      checkSelectionChanged();
    }
  }
  
  private void setUpChild(View paramView, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    LayoutParams localLayoutParams1 = (LayoutParams)paramView.getLayoutParams();
    LayoutParams localLayoutParams2 = localLayoutParams1;
    if (localLayoutParams1 == null) {
      localLayoutParams2 = (LayoutParams)generateDefaultLayoutParams();
    }
    boolean bool1 = this.mIsRtl;
    boolean bool2 = false;
    if (paramBoolean != bool1) {
      i = -1;
    } else {
      i = 0;
    }
    addViewInLayout(paramView, i, localLayoutParams2, true);
    if (paramInt1 == 0) {
      bool2 = true;
    }
    paramView.setSelected(bool2);
    paramInt1 = ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mSpinnerPadding.top + this.mSpinnerPadding.bottom, localLayoutParams2.height);
    paramView.measure(ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mSpinnerPadding.left + this.mSpinnerPadding.right, localLayoutParams2.width), paramInt1);
    int i = calculateTop(paramView, true);
    int j = paramView.getMeasuredHeight();
    int k = paramView.getMeasuredWidth();
    if (paramBoolean)
    {
      paramInt1 = paramInt2;
      paramInt2 = paramInt1 + k;
    }
    else
    {
      paramInt1 = paramInt2 - k;
    }
    paramView.layout(paramInt1, i, paramInt2, j + i);
  }
  
  private boolean showContextMenuForChildInternal(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    int i = getPositionForView(paramView);
    if (i < 0) {
      return false;
    }
    return dispatchLongPress(paramView, i, this.mAdapter.getItemId(i), paramFloat1, paramFloat2, paramBoolean);
  }
  
  private boolean showContextMenuInternal(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    if ((isPressed()) && (this.mSelectedPosition >= 0)) {
      return dispatchLongPress(getChildAt(this.mSelectedPosition - this.mFirstPosition), this.mSelectedPosition, this.mSelectedRowId, paramFloat1, paramFloat2, paramBoolean);
    }
    return false;
  }
  
  private void updateSelectedItemMetadata()
  {
    View localView1 = this.mSelectedChild;
    View localView2 = getChildAt(this.mSelectedPosition - this.mFirstPosition);
    this.mSelectedChild = localView2;
    if (localView2 == null) {
      return;
    }
    localView2.setSelected(true);
    localView2.setFocusable(true);
    if (hasFocus()) {
      localView2.requestFocus();
    }
    if ((localView1 != null) && (localView1 != localView2))
    {
      localView1.setSelected(false);
      localView1.setFocusable(false);
    }
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  protected int computeHorizontalScrollExtent()
  {
    return 1;
  }
  
  protected int computeHorizontalScrollOffset()
  {
    return this.mSelectedPosition;
  }
  
  protected int computeHorizontalScrollRange()
  {
    return this.mItemCount;
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    return paramKeyEvent.dispatch(this, null, null);
  }
  
  protected void dispatchSetPressed(boolean paramBoolean)
  {
    View localView = this.mSelectedChild;
    if (localView != null) {
      localView.setPressed(paramBoolean);
    }
  }
  
  public void dispatchSetSelected(boolean paramBoolean) {}
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-2, -2);
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return new LayoutParams(paramLayoutParams);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return Gallery.class.getName();
  }
  
  protected int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    int i = this.mSelectedPosition - this.mFirstPosition;
    if (i < 0) {
      return paramInt2;
    }
    if (paramInt2 == paramInt1 - 1) {
      return i;
    }
    if (paramInt2 >= i) {
      return paramInt2 + 1;
    }
    return paramInt2;
  }
  
  int getChildHeight(View paramView)
  {
    return paramView.getMeasuredHeight();
  }
  
  protected boolean getChildStaticTransformation(View paramView, Transformation paramTransformation)
  {
    paramTransformation.clear();
    float f;
    if (paramView == this.mSelectedChild) {
      f = 1.0F;
    } else {
      f = this.mUnselectedAlpha;
    }
    paramTransformation.setAlpha(f);
    return true;
  }
  
  protected ContextMenu.ContextMenuInfo getContextMenuInfo()
  {
    return this.mContextMenuInfo;
  }
  
  int getLimitedMotionScrollAmount(boolean paramBoolean, int paramInt)
  {
    if (paramBoolean != this.mIsRtl) {
      i = this.mItemCount - 1;
    } else {
      i = 0;
    }
    View localView = getChildAt(i - this.mFirstPosition);
    if (localView == null) {
      return paramInt;
    }
    int j = getCenterOfView(localView);
    int i = getCenterOfGallery();
    if (paramBoolean)
    {
      if (j <= i) {
        return 0;
      }
    }
    else if (j >= i) {
      return 0;
    }
    i -= j;
    if (paramBoolean) {
      paramInt = Math.max(i, paramInt);
    } else {
      paramInt = Math.min(i, paramInt);
    }
    return paramInt;
  }
  
  void layout(int paramInt, boolean paramBoolean)
  {
    this.mIsRtl = isLayoutRtl();
    int i = this.mSpinnerPadding.left;
    int j = this.mRight;
    int k = this.mLeft;
    int m = this.mSpinnerPadding.left;
    paramInt = this.mSpinnerPadding.right;
    if (this.mDataChanged) {
      handleDataChanged();
    }
    if (this.mItemCount == 0)
    {
      resetList();
      return;
    }
    if (this.mNextSelectedPosition >= 0) {
      setSelectedPositionInt(this.mNextSelectedPosition);
    }
    recycleAllViews();
    detachAllViewsFromParent();
    this.mRightMost = 0;
    this.mLeftMost = 0;
    this.mFirstPosition = this.mSelectedPosition;
    View localView = makeAndAddView(this.mSelectedPosition, 0, 0, true);
    localView.offsetLeftAndRight((j - k - m - paramInt) / 2 + i - localView.getWidth() / 2 + this.mSelectedCenterOffset);
    fillToGalleryRight();
    fillToGalleryLeft();
    this.mRecycler.clear();
    invalidate();
    checkSelectionChanged();
    this.mDataChanged = false;
    this.mNeedSync = false;
    setNextSelectedPositionInt(this.mSelectedPosition);
    updateSelectedItemMetadata();
  }
  
  @UnsupportedAppUsage
  boolean moveDirection(int paramInt)
  {
    if (isLayoutRtl()) {
      paramInt = -paramInt;
    }
    paramInt = this.mSelectedPosition + paramInt;
    if ((this.mItemCount > 0) && (paramInt >= 0) && (paramInt < this.mItemCount))
    {
      scrollToChild(paramInt - this.mFirstPosition);
      return true;
    }
    return false;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.mGestureDetector == null)
    {
      this.mGestureDetector = new GestureDetector(getContext(), this);
      this.mGestureDetector.setIsLongpressEnabled(true);
    }
  }
  
  void onCancel()
  {
    onUp();
  }
  
  public boolean onDown(MotionEvent paramMotionEvent)
  {
    this.mFlingRunnable.stop(false);
    this.mDownTouchPosition = pointToPosition((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
    int i = this.mDownTouchPosition;
    if (i >= 0)
    {
      this.mDownTouchView = getChildAt(i - this.mFirstPosition);
      this.mDownTouchView.setPressed(true);
    }
    this.mIsFirstScroll = true;
    return true;
  }
  
  public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    if (!this.mShouldCallbackDuringFling)
    {
      removeCallbacks(this.mDisableSuppressSelectionChangedRunnable);
      if (!this.mSuppressSelectionChanged) {
        this.mSuppressSelectionChanged = true;
      }
    }
    this.mFlingRunnable.startUsingVelocity((int)-paramFloat1);
    return true;
  }
  
  protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
  {
    super.onFocusChanged(paramBoolean, paramInt, paramRect);
    if (paramBoolean)
    {
      paramRect = this.mSelectedChild;
      if (paramRect != null)
      {
        paramRect.requestFocus(paramInt);
        this.mSelectedChild.setSelected(true);
      }
    }
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    boolean bool;
    if (this.mItemCount > 1) {
      bool = true;
    } else {
      bool = false;
    }
    paramAccessibilityNodeInfo.setScrollable(bool);
    if (isEnabled())
    {
      if ((this.mItemCount > 0) && (this.mSelectedPosition < this.mItemCount - 1)) {
        paramAccessibilityNodeInfo.addAction(4096);
      }
      if ((isEnabled()) && (this.mItemCount > 0) && (this.mSelectedPosition > 0)) {
        paramAccessibilityNodeInfo.addAction(8192);
      }
    }
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramInt != 66) {
      switch (paramInt)
      {
      default: 
        break;
      case 22: 
        if (!moveDirection(1)) {
          break;
        }
        playSoundEffect(3);
        return true;
      case 21: 
        if (!moveDirection(-1)) {
          break;
        }
        playSoundEffect(1);
        return true;
      }
    } else {
      this.mReceivedInvokeKeyDown = true;
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if (KeyEvent.isConfirmKey(paramInt))
    {
      if ((this.mReceivedInvokeKeyDown) && (this.mItemCount > 0))
      {
        dispatchPress(this.mSelectedChild);
        postDelayed(new Runnable()
        {
          public void run()
          {
            Gallery.this.dispatchUnpress();
          }
        }, ViewConfiguration.getPressedStateDuration());
        performItemClick(getChildAt(this.mSelectedPosition - this.mFirstPosition), this.mSelectedPosition, this.mAdapter.getItemId(this.mSelectedPosition));
      }
      this.mReceivedInvokeKeyDown = false;
      return true;
    }
    return super.onKeyUp(paramInt, paramKeyEvent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.mInLayout = true;
    layout(0, false);
    this.mInLayout = false;
  }
  
  public void onLongPress(MotionEvent paramMotionEvent)
  {
    if (this.mDownTouchPosition < 0) {
      return;
    }
    performHapticFeedback(0);
    long l = getItemIdAtPosition(this.mDownTouchPosition);
    dispatchLongPress(this.mDownTouchView, this.mDownTouchPosition, l, paramMotionEvent.getX(), paramMotionEvent.getY(), true);
  }
  
  public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    this.mParent.requestDisallowInterceptTouchEvent(true);
    if (!this.mShouldCallbackDuringFling)
    {
      if (this.mIsFirstScroll)
      {
        if (!this.mSuppressSelectionChanged) {
          this.mSuppressSelectionChanged = true;
        }
        postDelayed(this.mDisableSuppressSelectionChangedRunnable, 250L);
      }
    }
    else if (this.mSuppressSelectionChanged) {
      this.mSuppressSelectionChanged = false;
    }
    trackMotionScroll((int)paramFloat1 * -1);
    this.mIsFirstScroll = false;
    return true;
  }
  
  public void onShowPress(MotionEvent paramMotionEvent) {}
  
  public boolean onSingleTapUp(MotionEvent paramMotionEvent)
  {
    int i = this.mDownTouchPosition;
    if (i >= 0)
    {
      scrollToChild(i - this.mFirstPosition);
      if ((this.mShouldCallbackOnUnselectedItemClick) || (this.mDownTouchPosition == this.mSelectedPosition)) {
        performItemClick(this.mDownTouchView, this.mDownTouchPosition, this.mAdapter.getItemId(this.mDownTouchPosition));
      }
      return true;
    }
    return false;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool = this.mGestureDetector.onTouchEvent(paramMotionEvent);
    int i = paramMotionEvent.getAction();
    if (i == 1) {
      onUp();
    } else if (i == 3) {
      onCancel();
    }
    return bool;
  }
  
  void onUp()
  {
    if (this.mFlingRunnable.mScroller.isFinished()) {
      scrollIntoSlots();
    }
    dispatchUnpress();
  }
  
  public boolean performAccessibilityActionInternal(int paramInt, Bundle paramBundle)
  {
    if (super.performAccessibilityActionInternal(paramInt, paramBundle)) {
      return true;
    }
    if (paramInt != 4096)
    {
      if (paramInt != 8192) {
        return false;
      }
      if ((isEnabled()) && (this.mItemCount > 0) && (this.mSelectedPosition > 0)) {
        return scrollToChild(this.mSelectedPosition - this.mFirstPosition - 1);
      }
      return false;
    }
    if ((isEnabled()) && (this.mItemCount > 0) && (this.mSelectedPosition < this.mItemCount - 1)) {
      return scrollToChild(this.mSelectedPosition - this.mFirstPosition + 1);
    }
    return false;
  }
  
  void selectionChanged()
  {
    if (!this.mSuppressSelectionChanged) {
      super.selectionChanged();
    }
  }
  
  public void setAnimationDuration(int paramInt)
  {
    this.mAnimationDuration = paramInt;
  }
  
  public void setCallbackDuringFling(boolean paramBoolean)
  {
    this.mShouldCallbackDuringFling = paramBoolean;
  }
  
  public void setCallbackOnUnselectedItemClick(boolean paramBoolean)
  {
    this.mShouldCallbackOnUnselectedItemClick = paramBoolean;
  }
  
  public void setGravity(int paramInt)
  {
    if (this.mGravity != paramInt)
    {
      this.mGravity = paramInt;
      requestLayout();
    }
  }
  
  void setSelectedPositionInt(int paramInt)
  {
    super.setSelectedPositionInt(paramInt);
    updateSelectedItemMetadata();
  }
  
  public void setSpacing(int paramInt)
  {
    this.mSpacing = paramInt;
  }
  
  public void setUnselectedAlpha(float paramFloat)
  {
    this.mUnselectedAlpha = paramFloat;
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
  
  @UnsupportedAppUsage
  void trackMotionScroll(int paramInt)
  {
    if (getChildCount() == 0) {
      return;
    }
    boolean bool;
    if (paramInt < 0) {
      bool = true;
    } else {
      bool = false;
    }
    int i = getLimitedMotionScrollAmount(bool, paramInt);
    if (i != paramInt)
    {
      this.mFlingRunnable.endFling(false);
      onFinishedMovement();
    }
    offsetChildrenLeftAndRight(i);
    detachOffScreenChildren(bool);
    if (bool) {
      fillToGalleryRight();
    } else {
      fillToGalleryLeft();
    }
    this.mRecycler.clear();
    setSelectionToCenterChild();
    View localView = this.mSelectedChild;
    if (localView != null) {
      this.mSelectedCenterOffset = (localView.getLeft() + localView.getWidth() / 2 - getWidth() / 2);
    }
    onScrollChanged(0, 0, 0, 0);
    invalidate();
  }
  
  private class FlingRunnable
    implements Runnable
  {
    private int mLastFlingX;
    private Scroller mScroller = new Scroller(Gallery.this.getContext());
    
    public FlingRunnable() {}
    
    private void endFling(boolean paramBoolean)
    {
      this.mScroller.forceFinished(true);
      if (paramBoolean) {
        Gallery.this.scrollIntoSlots();
      }
    }
    
    private void startCommon()
    {
      Gallery.this.removeCallbacks(this);
    }
    
    public void run()
    {
      if (Gallery.this.mItemCount == 0)
      {
        endFling(true);
        return;
      }
      Gallery.access$602(Gallery.this, false);
      Object localObject = this.mScroller;
      boolean bool = ((Scroller)localObject).computeScrollOffset();
      int i = ((Scroller)localObject).getCurrX();
      int j = this.mLastFlingX - i;
      int k;
      if (j > 0)
      {
        localObject = Gallery.this;
        if (((Gallery)localObject).mIsRtl) {
          k = Gallery.this.mFirstPosition + Gallery.this.getChildCount() - 1;
        } else {
          k = Gallery.this.mFirstPosition;
        }
        Gallery.access$702((Gallery)localObject, k);
        k = Math.min(Gallery.this.getWidth() - Gallery.this.mPaddingLeft - Gallery.this.mPaddingRight - 1, j);
      }
      else
      {
        Gallery.this.getChildCount();
        localObject = Gallery.this;
        if (((Gallery)localObject).mIsRtl) {
          k = Gallery.this.mFirstPosition;
        } else {
          k = Gallery.this.mFirstPosition + Gallery.this.getChildCount() - 1;
        }
        Gallery.access$702((Gallery)localObject, k);
        k = Math.max(-(Gallery.this.getWidth() - Gallery.this.mPaddingRight - Gallery.this.mPaddingLeft - 1), j);
      }
      Gallery.this.trackMotionScroll(k);
      if ((bool) && (!Gallery.this.mShouldStopFling))
      {
        this.mLastFlingX = i;
        Gallery.this.post(this);
      }
      else
      {
        endFling(true);
      }
    }
    
    public void startUsingDistance(int paramInt)
    {
      if (paramInt == 0) {
        return;
      }
      startCommon();
      this.mLastFlingX = 0;
      this.mScroller.startScroll(0, 0, -paramInt, 0, Gallery.this.mAnimationDuration);
      Gallery.this.post(this);
    }
    
    @UnsupportedAppUsage
    public void startUsingVelocity(int paramInt)
    {
      if (paramInt == 0) {
        return;
      }
      startCommon();
      int i;
      if (paramInt < 0) {
        i = Integer.MAX_VALUE;
      } else {
        i = 0;
      }
      this.mLastFlingX = i;
      this.mScroller.fling(i, 0, paramInt, 0, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
      Gallery.this.post(this);
    }
    
    public void stop(boolean paramBoolean)
    {
      Gallery.this.removeCallbacks(this);
      endFling(paramBoolean);
    }
  }
  
  public static class LayoutParams
    extends ViewGroup.LayoutParams
  {
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/Gallery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */