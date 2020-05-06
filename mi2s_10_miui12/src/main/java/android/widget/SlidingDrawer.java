package android.widget;

import android.R.styleable;
import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

@Deprecated
public class SlidingDrawer
  extends ViewGroup
{
  private static final int ANIMATION_FRAME_DURATION = 16;
  private static final int COLLAPSED_FULL_CLOSED = -10002;
  private static final int EXPANDED_FULL_OPEN = -10001;
  private static final float MAXIMUM_ACCELERATION = 2000.0F;
  private static final float MAXIMUM_MAJOR_VELOCITY = 200.0F;
  private static final float MAXIMUM_MINOR_VELOCITY = 150.0F;
  private static final float MAXIMUM_TAP_VELOCITY = 100.0F;
  public static final int ORIENTATION_HORIZONTAL = 0;
  public static final int ORIENTATION_VERTICAL = 1;
  private static final int TAP_THRESHOLD = 6;
  private static final int VELOCITY_UNITS = 1000;
  private boolean mAllowSingleTap;
  private boolean mAnimateOnClick;
  private float mAnimatedAcceleration;
  private float mAnimatedVelocity;
  private boolean mAnimating;
  private long mAnimationLastTime;
  private float mAnimationPosition;
  private int mBottomOffset;
  private View mContent;
  private final int mContentId;
  private long mCurrentAnimationTime;
  private boolean mExpanded;
  private final Rect mFrame = new Rect();
  private View mHandle;
  private int mHandleHeight;
  private final int mHandleId;
  private int mHandleWidth;
  private final Rect mInvalidate = new Rect();
  private boolean mLocked;
  private final int mMaximumAcceleration;
  private final int mMaximumMajorVelocity;
  private final int mMaximumMinorVelocity;
  private final int mMaximumTapVelocity;
  private OnDrawerCloseListener mOnDrawerCloseListener;
  private OnDrawerOpenListener mOnDrawerOpenListener;
  private OnDrawerScrollListener mOnDrawerScrollListener;
  private final Runnable mSlidingRunnable = new Runnable()
  {
    public void run()
    {
      SlidingDrawer.this.doAnimation();
    }
  };
  private final int mTapThreshold;
  @UnsupportedAppUsage
  private int mTopOffset;
  @UnsupportedAppUsage
  private int mTouchDelta;
  @UnsupportedAppUsage
  private boolean mTracking;
  @UnsupportedAppUsage
  private VelocityTracker mVelocityTracker;
  private final int mVelocityUnits;
  private boolean mVertical;
  
  public SlidingDrawer(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public SlidingDrawer(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public SlidingDrawer(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SlidingDrawer, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.SlidingDrawer, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    boolean bool;
    if (localTypedArray.getInt(0, 1) == 1) {
      bool = true;
    } else {
      bool = false;
    }
    this.mVertical = bool;
    this.mBottomOffset = ((int)localTypedArray.getDimension(1, 0.0F));
    this.mTopOffset = ((int)localTypedArray.getDimension(2, 0.0F));
    this.mAllowSingleTap = localTypedArray.getBoolean(3, true);
    this.mAnimateOnClick = localTypedArray.getBoolean(6, true);
    paramInt1 = localTypedArray.getResourceId(4, 0);
    if (paramInt1 != 0)
    {
      paramInt2 = localTypedArray.getResourceId(5, 0);
      if (paramInt2 != 0)
      {
        if (paramInt1 != paramInt2)
        {
          this.mHandleId = paramInt1;
          this.mContentId = paramInt2;
          float f = getResources().getDisplayMetrics().density;
          this.mTapThreshold = ((int)(6.0F * f + 0.5F));
          this.mMaximumTapVelocity = ((int)(100.0F * f + 0.5F));
          this.mMaximumMinorVelocity = ((int)(150.0F * f + 0.5F));
          this.mMaximumMajorVelocity = ((int)(200.0F * f + 0.5F));
          this.mMaximumAcceleration = ((int)(2000.0F * f + 0.5F));
          this.mVelocityUnits = ((int)(1000.0F * f + 0.5F));
          localTypedArray.recycle();
          setAlwaysDrawnWithCacheEnabled(false);
          return;
        }
        throw new IllegalArgumentException("The content and handle attributes must refer to different children.");
      }
      throw new IllegalArgumentException("The content attribute is required and must refer to a valid child.");
    }
    throw new IllegalArgumentException("The handle attribute is required and must refer to a valid child.");
  }
  
  private void animateClose(int paramInt, boolean paramBoolean)
  {
    prepareTracking(paramInt);
    performFling(paramInt, this.mMaximumAcceleration, true, paramBoolean);
  }
  
  private void animateOpen(int paramInt, boolean paramBoolean)
  {
    prepareTracking(paramInt);
    performFling(paramInt, -this.mMaximumAcceleration, true, paramBoolean);
  }
  
  private void closeDrawer()
  {
    moveHandle(55534);
    this.mContent.setVisibility(8);
    this.mContent.destroyDrawingCache();
    if (!this.mExpanded) {
      return;
    }
    this.mExpanded = false;
    OnDrawerCloseListener localOnDrawerCloseListener = this.mOnDrawerCloseListener;
    if (localOnDrawerCloseListener != null) {
      localOnDrawerCloseListener.onDrawerClosed();
    }
  }
  
  private void doAnimation()
  {
    if (this.mAnimating)
    {
      incrementAnimation();
      float f = this.mAnimationPosition;
      int i = this.mBottomOffset;
      int j;
      if (this.mVertical) {
        j = getHeight();
      } else {
        j = getWidth();
      }
      if (f >= i + j - 1)
      {
        this.mAnimating = false;
        closeDrawer();
      }
      else
      {
        f = this.mAnimationPosition;
        if (f < this.mTopOffset)
        {
          this.mAnimating = false;
          openDrawer();
        }
        else
        {
          moveHandle((int)f);
          this.mCurrentAnimationTime += 16L;
          postDelayed(this.mSlidingRunnable, 16L);
        }
      }
    }
  }
  
  private void incrementAnimation()
  {
    long l = SystemClock.uptimeMillis();
    float f1 = (float)(l - this.mAnimationLastTime) / 1000.0F;
    float f2 = this.mAnimationPosition;
    float f3 = this.mAnimatedVelocity;
    float f4 = this.mAnimatedAcceleration;
    this.mAnimationPosition = (f3 * f1 + f2 + 0.5F * f4 * f1 * f1);
    this.mAnimatedVelocity = (f4 * f1 + f3);
    this.mAnimationLastTime = l;
  }
  
  private void moveHandle(int paramInt)
  {
    View localView = this.mHandle;
    int i;
    int j;
    int k;
    Rect localRect1;
    Rect localRect2;
    if (this.mVertical)
    {
      if (paramInt == 55535)
      {
        localView.offsetTopAndBottom(this.mTopOffset - localView.getTop());
        invalidate();
      }
      else if (paramInt == 55534)
      {
        localView.offsetTopAndBottom(this.mBottomOffset + this.mBottom - this.mTop - this.mHandleHeight - localView.getTop());
        invalidate();
      }
      else
      {
        i = localView.getTop();
        j = paramInt - i;
        k = this.mTopOffset;
        if (paramInt < k)
        {
          paramInt = k - i;
        }
        else
        {
          paramInt = j;
          if (j > this.mBottomOffset + this.mBottom - this.mTop - this.mHandleHeight - i) {
            paramInt = this.mBottomOffset + this.mBottom - this.mTop - this.mHandleHeight - i;
          }
        }
        localView.offsetTopAndBottom(paramInt);
        localRect1 = this.mFrame;
        localRect2 = this.mInvalidate;
        localView.getHitRect(localRect1);
        localRect2.set(localRect1);
        localRect2.union(localRect1.left, localRect1.top - paramInt, localRect1.right, localRect1.bottom - paramInt);
        localRect2.union(0, localRect1.bottom - paramInt, getWidth(), localRect1.bottom - paramInt + this.mContent.getHeight());
        invalidate(localRect2);
      }
    }
    else if (paramInt == 55535)
    {
      localView.offsetLeftAndRight(this.mTopOffset - localView.getLeft());
      invalidate();
    }
    else if (paramInt == 55534)
    {
      localView.offsetLeftAndRight(this.mBottomOffset + this.mRight - this.mLeft - this.mHandleWidth - localView.getLeft());
      invalidate();
    }
    else
    {
      i = localView.getLeft();
      j = paramInt - i;
      k = this.mTopOffset;
      if (paramInt < k)
      {
        paramInt = k - i;
      }
      else
      {
        paramInt = j;
        if (j > this.mBottomOffset + this.mRight - this.mLeft - this.mHandleWidth - i) {
          paramInt = this.mBottomOffset + this.mRight - this.mLeft - this.mHandleWidth - i;
        }
      }
      localView.offsetLeftAndRight(paramInt);
      localRect2 = this.mFrame;
      localRect1 = this.mInvalidate;
      localView.getHitRect(localRect2);
      localRect1.set(localRect2);
      localRect1.union(localRect2.left - paramInt, localRect2.top, localRect2.right - paramInt, localRect2.bottom);
      localRect1.union(localRect2.right - paramInt, 0, localRect2.right - paramInt + this.mContent.getWidth(), getHeight());
      invalidate(localRect1);
    }
  }
  
  private void openDrawer()
  {
    moveHandle(55535);
    this.mContent.setVisibility(0);
    if (this.mExpanded) {
      return;
    }
    this.mExpanded = true;
    OnDrawerOpenListener localOnDrawerOpenListener = this.mOnDrawerOpenListener;
    if (localOnDrawerOpenListener != null) {
      localOnDrawerOpenListener.onDrawerOpened();
    }
  }
  
  private void performFling(int paramInt, float paramFloat, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mAnimationPosition = paramInt;
    this.mAnimatedVelocity = paramFloat;
    int j;
    if (this.mExpanded)
    {
      if ((!paramBoolean1) && (paramFloat <= this.mMaximumMajorVelocity))
      {
        int i = this.mTopOffset;
        if (this.mVertical) {
          j = this.mHandleHeight;
        } else {
          j = this.mHandleWidth;
        }
        if ((paramInt <= i + j) || (paramFloat <= -this.mMaximumMajorVelocity))
        {
          this.mAnimatedAcceleration = (-this.mMaximumAcceleration);
          if (paramFloat <= 0.0F) {
            break label229;
          }
          this.mAnimatedVelocity = 0.0F;
          break label229;
        }
      }
      this.mAnimatedAcceleration = this.mMaximumAcceleration;
      if (paramFloat < 0.0F) {
        this.mAnimatedVelocity = 0.0F;
      }
    }
    else
    {
      if (!paramBoolean1) {
        if (paramFloat <= this.mMaximumMajorVelocity)
        {
          if (this.mVertical) {
            j = getHeight();
          } else {
            j = getWidth();
          }
          if ((paramInt <= j / 2) || (paramFloat <= -this.mMaximumMajorVelocity)) {}
        }
        else
        {
          this.mAnimatedAcceleration = this.mMaximumAcceleration;
          if (paramFloat >= 0.0F) {
            break label229;
          }
          this.mAnimatedVelocity = 0.0F;
          break label229;
        }
      }
      this.mAnimatedAcceleration = (-this.mMaximumAcceleration);
      if (paramFloat > 0.0F) {
        this.mAnimatedVelocity = 0.0F;
      }
    }
    label229:
    long l = SystemClock.uptimeMillis();
    this.mAnimationLastTime = l;
    this.mCurrentAnimationTime = (l + 16L);
    this.mAnimating = true;
    removeCallbacks(this.mSlidingRunnable);
    postDelayed(this.mSlidingRunnable, 16L);
    stopTracking(paramBoolean2);
  }
  
  @UnsupportedAppUsage
  private void prepareContent()
  {
    if (this.mAnimating) {
      return;
    }
    View localView = this.mContent;
    if (localView.isLayoutRequested())
    {
      int i;
      int k;
      if (this.mVertical)
      {
        i = this.mHandleHeight;
        int j = this.mBottom;
        k = this.mTop;
        int m = this.mTopOffset;
        localView.measure(View.MeasureSpec.makeMeasureSpec(this.mRight - this.mLeft, 1073741824), View.MeasureSpec.makeMeasureSpec(j - k - i - m, 1073741824));
        localView.layout(0, this.mTopOffset + i, localView.getMeasuredWidth(), this.mTopOffset + i + localView.getMeasuredHeight());
      }
      else
      {
        k = this.mHandle.getWidth();
        localView.measure(View.MeasureSpec.makeMeasureSpec(this.mRight - this.mLeft - k - this.mTopOffset, 1073741824), View.MeasureSpec.makeMeasureSpec(this.mBottom - this.mTop, 1073741824));
        i = this.mTopOffset;
        localView.layout(k + i, 0, i + k + localView.getMeasuredWidth(), localView.getMeasuredHeight());
      }
    }
    localView.getViewTreeObserver().dispatchOnPreDraw();
    if (!localView.isHardwareAccelerated()) {
      localView.buildDrawingCache();
    }
    localView.setVisibility(8);
  }
  
  @UnsupportedAppUsage
  private void prepareTracking(int paramInt)
  {
    this.mTracking = true;
    this.mVelocityTracker = VelocityTracker.obtain();
    if ((this.mExpanded ^ true))
    {
      this.mAnimatedAcceleration = this.mMaximumAcceleration;
      this.mAnimatedVelocity = this.mMaximumMajorVelocity;
      int i = this.mBottomOffset;
      int j;
      if (this.mVertical)
      {
        paramInt = getHeight();
        j = this.mHandleHeight;
      }
      else
      {
        paramInt = getWidth();
        j = this.mHandleWidth;
      }
      this.mAnimationPosition = (i + (paramInt - j));
      moveHandle((int)this.mAnimationPosition);
      this.mAnimating = true;
      removeCallbacks(this.mSlidingRunnable);
      long l = SystemClock.uptimeMillis();
      this.mAnimationLastTime = l;
      this.mCurrentAnimationTime = (16L + l);
      this.mAnimating = true;
    }
    else
    {
      if (this.mAnimating)
      {
        this.mAnimating = false;
        removeCallbacks(this.mSlidingRunnable);
      }
      moveHandle(paramInt);
    }
  }
  
  private void stopTracking(boolean paramBoolean)
  {
    this.mHandle.setPressed(false);
    this.mTracking = false;
    if (paramBoolean)
    {
      localObject = this.mOnDrawerScrollListener;
      if (localObject != null) {
        ((OnDrawerScrollListener)localObject).onScrollEnded();
      }
    }
    Object localObject = this.mVelocityTracker;
    if (localObject != null)
    {
      ((VelocityTracker)localObject).recycle();
      this.mVelocityTracker = null;
    }
  }
  
  public void animateClose()
  {
    prepareContent();
    OnDrawerScrollListener localOnDrawerScrollListener = this.mOnDrawerScrollListener;
    if (localOnDrawerScrollListener != null) {
      localOnDrawerScrollListener.onScrollStarted();
    }
    int i;
    if (this.mVertical) {
      i = this.mHandle.getTop();
    } else {
      i = this.mHandle.getLeft();
    }
    animateClose(i, false);
    if (localOnDrawerScrollListener != null) {
      localOnDrawerScrollListener.onScrollEnded();
    }
  }
  
  public void animateOpen()
  {
    prepareContent();
    OnDrawerScrollListener localOnDrawerScrollListener = this.mOnDrawerScrollListener;
    if (localOnDrawerScrollListener != null) {
      localOnDrawerScrollListener.onScrollStarted();
    }
    int i;
    if (this.mVertical) {
      i = this.mHandle.getTop();
    } else {
      i = this.mHandle.getLeft();
    }
    animateOpen(i, false);
    sendAccessibilityEvent(32);
    if (localOnDrawerScrollListener != null) {
      localOnDrawerScrollListener.onScrollEnded();
    }
  }
  
  public void animateToggle()
  {
    if (!this.mExpanded) {
      animateOpen();
    } else {
      animateClose();
    }
  }
  
  public void close()
  {
    closeDrawer();
    invalidate();
    requestLayout();
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    long l = getDrawingTime();
    View localView = this.mHandle;
    boolean bool = this.mVertical;
    drawChild(paramCanvas, localView, l);
    if ((!this.mTracking) && (!this.mAnimating)) {
      if (this.mExpanded)
      {
        drawChild(paramCanvas, this.mContent, l);
        return;
      }
    }
    for (;;)
    {
      break;
      Bitmap localBitmap = this.mContent.getDrawingCache();
      float f1 = 0.0F;
      if (localBitmap != null)
      {
        if (bool) {
          paramCanvas.drawBitmap(localBitmap, 0.0F, localView.getBottom(), null);
        } else {
          paramCanvas.drawBitmap(localBitmap, localView.getRight(), 0.0F, null);
        }
      }
      else
      {
        paramCanvas.save();
        float f2;
        if (bool) {
          f2 = 0.0F;
        } else {
          f2 = localView.getLeft() - this.mTopOffset;
        }
        if (bool) {
          f1 = localView.getTop() - this.mTopOffset;
        }
        paramCanvas.translate(f2, f1);
        drawChild(paramCanvas, this.mContent, l);
        paramCanvas.restore();
      }
    }
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return SlidingDrawer.class.getName();
  }
  
  public View getContent()
  {
    return this.mContent;
  }
  
  public View getHandle()
  {
    return this.mHandle;
  }
  
  public boolean isMoving()
  {
    boolean bool;
    if ((!this.mTracking) && (!this.mAnimating)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isOpened()
  {
    return this.mExpanded;
  }
  
  public void lock()
  {
    this.mLocked = true;
  }
  
  protected void onFinishInflate()
  {
    this.mHandle = findViewById(this.mHandleId);
    View localView = this.mHandle;
    if (localView != null)
    {
      localView.setOnClickListener(new DrawerToggler(null));
      this.mContent = findViewById(this.mContentId);
      localView = this.mContent;
      if (localView != null)
      {
        localView.setVisibility(8);
        return;
      }
      throw new IllegalArgumentException("The content attribute is must refer to an existing child.");
    }
    throw new IllegalArgumentException("The handle attribute is must refer to an existing child.");
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mLocked) {
      return false;
    }
    int i = paramMotionEvent.getAction();
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    Object localObject = this.mFrame;
    View localView = this.mHandle;
    localView.getHitRect((Rect)localObject);
    if ((!this.mTracking) && (!((Rect)localObject).contains((int)f1, (int)f2))) {
      return false;
    }
    if (i == 0)
    {
      this.mTracking = true;
      localView.setPressed(true);
      prepareContent();
      localObject = this.mOnDrawerScrollListener;
      if (localObject != null) {
        ((OnDrawerScrollListener)localObject).onScrollStarted();
      }
      if (this.mVertical)
      {
        i = this.mHandle.getTop();
        this.mTouchDelta = ((int)f2 - i);
        prepareTracking(i);
      }
      else
      {
        i = this.mHandle.getLeft();
        this.mTouchDelta = ((int)f1 - i);
        prepareTracking(i);
      }
      this.mVelocityTracker.addMovement(paramMotionEvent);
    }
    return true;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mTracking) {
      return;
    }
    paramInt1 = paramInt3 - paramInt1;
    paramInt2 = paramInt4 - paramInt2;
    View localView1 = this.mHandle;
    paramInt4 = localView1.getMeasuredWidth();
    int i = localView1.getMeasuredHeight();
    View localView2 = this.mContent;
    if (this.mVertical)
    {
      paramInt3 = (paramInt1 - paramInt4) / 2;
      if (this.mExpanded) {
        paramInt1 = this.mTopOffset;
      } else {
        paramInt1 = paramInt2 - i + this.mBottomOffset;
      }
      localView2.layout(0, this.mTopOffset + i, localView2.getMeasuredWidth(), this.mTopOffset + i + localView2.getMeasuredHeight());
      paramInt2 = paramInt1;
    }
    else
    {
      if (this.mExpanded) {
        paramInt1 = this.mTopOffset;
      } else {
        paramInt1 = paramInt1 - paramInt4 + this.mBottomOffset;
      }
      paramInt2 = (paramInt2 - i) / 2;
      paramInt3 = this.mTopOffset;
      localView2.layout(paramInt3 + paramInt4, 0, paramInt3 + paramInt4 + localView2.getMeasuredWidth(), localView2.getMeasuredHeight());
      paramInt3 = paramInt1;
    }
    localView1.layout(paramInt3, paramInt2, paramInt3 + paramInt4, paramInt2 + i);
    this.mHandleHeight = localView1.getHeight();
    this.mHandleWidth = localView1.getWidth();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt1);
    int k = View.MeasureSpec.getMode(paramInt2);
    int m = View.MeasureSpec.getSize(paramInt2);
    if ((i != 0) && (k != 0))
    {
      View localView = this.mHandle;
      measureChild(localView, paramInt1, paramInt2);
      if (this.mVertical)
      {
        paramInt1 = localView.getMeasuredHeight();
        paramInt2 = this.mTopOffset;
        this.mContent.measure(View.MeasureSpec.makeMeasureSpec(j, 1073741824), View.MeasureSpec.makeMeasureSpec(m - paramInt1 - paramInt2, 1073741824));
      }
      else
      {
        paramInt2 = localView.getMeasuredWidth();
        paramInt1 = this.mTopOffset;
        this.mContent.measure(View.MeasureSpec.makeMeasureSpec(j - paramInt2 - paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(m, 1073741824));
      }
      setMeasuredDimension(j, m);
      return;
    }
    throw new RuntimeException("SlidingDrawer cannot have UNSPECIFIED dimensions");
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1 = this.mLocked;
    boolean bool2 = true;
    if (bool1) {
      return true;
    }
    if (this.mTracking)
    {
      this.mVelocityTracker.addMovement(paramMotionEvent);
      int i = paramMotionEvent.getAction();
      if (i != 1) {
        if (i != 2)
        {
          if (i != 3) {
            break label588;
          }
        }
        else
        {
          if (this.mVertical) {
            f1 = paramMotionEvent.getY();
          } else {
            f1 = paramMotionEvent.getX();
          }
          moveHandle((int)f1 - this.mTouchDelta);
          break label588;
        }
      }
      VelocityTracker localVelocityTracker = this.mVelocityTracker;
      localVelocityTracker.computeCurrentVelocity(this.mVelocityUnits);
      float f2 = localVelocityTracker.getYVelocity();
      float f3 = localVelocityTracker.getXVelocity();
      bool1 = this.mVertical;
      float f4;
      int j;
      if (bool1)
      {
        if (f2 < 0.0F) {
          i = 1;
        } else {
          i = 0;
        }
        f4 = f3;
        if (f3 < 0.0F) {
          f4 = -f3;
        }
        j = this.mMaximumMinorVelocity;
        f1 = f2;
        f5 = f4;
        k = i;
        if (f4 > j)
        {
          f5 = j;
          f1 = f2;
          k = i;
        }
      }
      else
      {
        if (f3 < 0.0F) {
          i = 1;
        } else {
          i = 0;
        }
        f4 = f2;
        if (f2 < 0.0F) {
          f4 = -f2;
        }
        j = this.mMaximumMinorVelocity;
        f1 = f4;
        f5 = f3;
        k = i;
        if (f4 > j)
        {
          f1 = j;
          k = i;
          f5 = f3;
        }
      }
      float f5 = (float)Math.hypot(f5, f1);
      float f1 = f5;
      if (k != 0) {
        f1 = -f5;
      }
      i = this.mHandle.getTop();
      int k = this.mHandle.getLeft();
      if (Math.abs(f1) < this.mMaximumTapVelocity)
      {
        boolean bool3 = this.mExpanded;
        if (bool1 ? ((bool3) && (i < this.mTapThreshold + this.mTopOffset)) || ((!this.mExpanded) && (i > this.mBottomOffset + this.mBottom - this.mTop - this.mHandleHeight - this.mTapThreshold)) : ((bool3) && (k < this.mTapThreshold + this.mTopOffset)) || ((!this.mExpanded) && (k > this.mBottomOffset + this.mRight - this.mLeft - this.mHandleWidth - this.mTapThreshold)))
        {
          if (this.mAllowSingleTap)
          {
            playSoundEffect(0);
            if (this.mExpanded)
            {
              if (!bool1) {
                i = k;
              }
              animateClose(i, true);
            }
            else
            {
              if (!bool1) {
                i = k;
              }
              animateOpen(i, true);
            }
          }
          else
          {
            if (bool1) {
              k = i;
            }
            performFling(k, f1, false, true);
          }
        }
        else
        {
          if (!bool1) {
            i = k;
          }
          performFling(i, f1, false, true);
        }
      }
      else
      {
        if (!bool1) {
          i = k;
        }
        performFling(i, f1, false, true);
      }
    }
    label588:
    bool1 = bool2;
    if (!this.mTracking)
    {
      bool1 = bool2;
      if (!this.mAnimating) {
        if (super.onTouchEvent(paramMotionEvent)) {
          bool1 = bool2;
        } else {
          bool1 = false;
        }
      }
    }
    return bool1;
  }
  
  public void open()
  {
    openDrawer();
    invalidate();
    requestLayout();
    sendAccessibilityEvent(32);
  }
  
  public void setOnDrawerCloseListener(OnDrawerCloseListener paramOnDrawerCloseListener)
  {
    this.mOnDrawerCloseListener = paramOnDrawerCloseListener;
  }
  
  public void setOnDrawerOpenListener(OnDrawerOpenListener paramOnDrawerOpenListener)
  {
    this.mOnDrawerOpenListener = paramOnDrawerOpenListener;
  }
  
  public void setOnDrawerScrollListener(OnDrawerScrollListener paramOnDrawerScrollListener)
  {
    this.mOnDrawerScrollListener = paramOnDrawerScrollListener;
  }
  
  public void toggle()
  {
    if (!this.mExpanded) {
      openDrawer();
    } else {
      closeDrawer();
    }
    invalidate();
    requestLayout();
  }
  
  public void unlock()
  {
    this.mLocked = false;
  }
  
  private class DrawerToggler
    implements View.OnClickListener
  {
    private DrawerToggler() {}
    
    public void onClick(View paramView)
    {
      if (SlidingDrawer.this.mLocked) {
        return;
      }
      if (SlidingDrawer.this.mAnimateOnClick) {
        SlidingDrawer.this.animateToggle();
      } else {
        SlidingDrawer.this.toggle();
      }
    }
  }
  
  public static abstract interface OnDrawerCloseListener
  {
    public abstract void onDrawerClosed();
  }
  
  public static abstract interface OnDrawerOpenListener
  {
    public abstract void onDrawerOpened();
  }
  
  public static abstract interface OnDrawerScrollListener
  {
    public abstract void onScrollEnded();
    
    public abstract void onScrollStarted();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/SlidingDrawer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */