package android.widget;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.TableMaskFilter;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.RemotableViewMethod;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.LinearInterpolator;
import com.android.internal.R.styleable;
import java.lang.ref.WeakReference;
import java.util.HashMap;

@RemoteViews.RemoteView
public class StackView
  extends AdapterViewAnimator
{
  private static final int DEFAULT_ANIMATION_DURATION = 400;
  private static final int FRAME_PADDING = 4;
  private static final int GESTURE_NONE = 0;
  private static final int GESTURE_SLIDE_DOWN = 2;
  private static final int GESTURE_SLIDE_UP = 1;
  private static final int INVALID_POINTER = -1;
  private static final int ITEMS_SLIDE_DOWN = 1;
  private static final int ITEMS_SLIDE_UP = 0;
  private static final int MINIMUM_ANIMATION_DURATION = 50;
  private static final int MIN_TIME_BETWEEN_INTERACTION_AND_AUTOADVANCE = 5000;
  private static final long MIN_TIME_BETWEEN_SCROLLS = 100L;
  private static final int NUM_ACTIVE_VIEWS = 5;
  private static final float PERSPECTIVE_SCALE_FACTOR = 0.0F;
  private static final float PERSPECTIVE_SHIFT_FACTOR_X = 0.1F;
  private static final float PERSPECTIVE_SHIFT_FACTOR_Y = 0.1F;
  private static final float SLIDE_UP_RATIO = 0.7F;
  private static final int STACK_RELAYOUT_DURATION = 100;
  private static final float SWIPE_THRESHOLD_RATIO = 0.2F;
  private static HolographicHelper sHolographicHelper;
  private final String TAG = "StackView";
  private int mActivePointerId;
  private int mClickColor;
  private ImageView mClickFeedback;
  private boolean mClickFeedbackIsValid = false;
  private boolean mFirstLayoutHappened = false;
  private int mFramePadding;
  private ImageView mHighlight;
  private float mInitialX;
  private float mInitialY;
  private long mLastInteractionTime = 0L;
  private long mLastScrollTime;
  private int mMaximumVelocity;
  private float mNewPerspectiveShiftX;
  private float mNewPerspectiveShiftY;
  private float mPerspectiveShiftX;
  private float mPerspectiveShiftY;
  private int mResOutColor;
  private int mSlideAmount;
  private int mStackMode;
  private StackSlider mStackSlider;
  private int mSwipeGestureType = 0;
  private int mSwipeThreshold;
  private final Rect mTouchRect = new Rect();
  private int mTouchSlop;
  private boolean mTransitionIsSetup = false;
  private VelocityTracker mVelocityTracker;
  private int mYVelocity = 0;
  private final Rect stackInvalidateRect = new Rect();
  
  public StackView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public StackView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16843838);
  }
  
  public StackView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public StackView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.StackView, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.StackView, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    this.mResOutColor = localTypedArray.getColor(1, 0);
    this.mClickColor = localTypedArray.getColor(0, 0);
    localTypedArray.recycle();
    initStackView();
  }
  
  private void beginGestureIfNeeded(float paramFloat)
  {
    if (((int)Math.abs(paramFloat) > this.mTouchSlop) && (this.mSwipeGestureType == 0))
    {
      boolean bool = true;
      int i;
      if (paramFloat < 0.0F) {
        i = 1;
      } else {
        i = 2;
      }
      cancelLongPress();
      requestDisallowInterceptTouchEvent(true);
      if (this.mAdapter == null) {
        return;
      }
      int j = getCount();
      int k;
      if (this.mStackMode == 0)
      {
        if (i == 2) {
          k = 0;
        } else {
          k = 1;
        }
      }
      else if (i == 2) {
        k = 1;
      } else {
        k = 0;
      }
      int m;
      if ((this.mLoopViews) && (j == 1) && (((this.mStackMode == 0) && (i == 1)) || ((this.mStackMode == 1) && (i == 2)))) {
        m = 1;
      } else {
        m = 0;
      }
      int n;
      if ((this.mLoopViews) && (j == 1) && (((this.mStackMode == 1) && (i == 1)) || ((this.mStackMode == 0) && (i == 2)))) {
        n = 1;
      } else {
        n = 0;
      }
      if ((this.mLoopViews) && (n == 0) && (m == 0))
      {
        n = 0;
        m = k;
        k = n;
      }
      else if ((this.mCurrentWindowStartUnbounded + k != -1) && (n == 0))
      {
        if ((this.mCurrentWindowStartUnbounded + k != j - 1) && (m == 0))
        {
          n = 0;
          m = k;
          k = n;
        }
        else
        {
          n = 2;
          m = k;
          k = n;
        }
      }
      else
      {
        m = k + 1;
        k = 1;
      }
      if (k != 0) {
        bool = false;
      }
      this.mTransitionIsSetup = bool;
      View localView = getViewAtRelativeIndex(m);
      if (localView == null) {
        return;
      }
      setupStackSlider(localView, k);
      this.mSwipeGestureType = i;
      cancelHandleClick();
    }
  }
  
  private void handlePointerUp(MotionEvent paramMotionEvent)
  {
    int i = (int)(paramMotionEvent.getY(paramMotionEvent.findPointerIndex(this.mActivePointerId)) - this.mInitialY);
    this.mLastInteractionTime = System.currentTimeMillis();
    paramMotionEvent = this.mVelocityTracker;
    if (paramMotionEvent != null)
    {
      paramMotionEvent.computeCurrentVelocity(1000, this.mMaximumVelocity);
      this.mYVelocity = ((int)this.mVelocityTracker.getYVelocity(this.mActivePointerId));
    }
    paramMotionEvent = this.mVelocityTracker;
    if (paramMotionEvent != null)
    {
      paramMotionEvent.recycle();
      this.mVelocityTracker = null;
    }
    if ((i > this.mSwipeThreshold) && (this.mSwipeGestureType == 2) && (this.mStackSlider.mMode == 0))
    {
      this.mSwipeGestureType = 0;
      if (this.mStackMode == 0) {
        showPrevious();
      } else {
        showNext();
      }
      this.mHighlight.bringToFront();
    }
    else if ((i < -this.mSwipeThreshold) && (this.mSwipeGestureType == 1) && (this.mStackSlider.mMode == 0))
    {
      this.mSwipeGestureType = 0;
      if (this.mStackMode == 0) {
        showNext();
      } else {
        showPrevious();
      }
      this.mHighlight.bringToFront();
    }
    else
    {
      i = this.mSwipeGestureType;
      float f = 1.0F;
      Object localObject;
      if (i == 1)
      {
        if (this.mStackMode != 1) {
          f = 0.0F;
        }
        if ((this.mStackMode != 0) && (this.mStackSlider.mMode == 0)) {
          i = Math.round(this.mStackSlider.getDurationForOffscreenPosition());
        } else {
          i = Math.round(this.mStackSlider.getDurationForNeutralPosition());
        }
        localObject = new StackSlider(this.mStackSlider);
        paramMotionEvent = PropertyValuesHolder.ofFloat("YProgress", new float[] { f });
        paramMotionEvent = ObjectAnimator.ofPropertyValuesHolder(localObject, new PropertyValuesHolder[] { PropertyValuesHolder.ofFloat("XProgress", new float[] { 0.0F }), paramMotionEvent });
        paramMotionEvent.setDuration(i);
        paramMotionEvent.setInterpolator(new LinearInterpolator());
        paramMotionEvent.start();
      }
      else if (i == 2)
      {
        if (this.mStackMode == 1) {
          f = 0.0F;
        }
        if ((this.mStackMode != 1) && (this.mStackSlider.mMode == 0)) {
          i = Math.round(this.mStackSlider.getDurationForOffscreenPosition());
        } else {
          i = Math.round(this.mStackSlider.getDurationForNeutralPosition());
        }
        paramMotionEvent = new StackSlider(this.mStackSlider);
        localObject = PropertyValuesHolder.ofFloat("YProgress", new float[] { f });
        paramMotionEvent = ObjectAnimator.ofPropertyValuesHolder(paramMotionEvent, new PropertyValuesHolder[] { PropertyValuesHolder.ofFloat("XProgress", new float[] { 0.0F }), localObject });
        paramMotionEvent.setDuration(i);
        paramMotionEvent.start();
      }
    }
    this.mActivePointerId = -1;
    this.mSwipeGestureType = 0;
  }
  
  private void initStackView()
  {
    configureViewAnimator(5, 1);
    setStaticTransformationsEnabled(true);
    Object localObject = ViewConfiguration.get(getContext());
    this.mTouchSlop = ((ViewConfiguration)localObject).getScaledTouchSlop();
    this.mMaximumVelocity = ((ViewConfiguration)localObject).getScaledMaximumFlingVelocity();
    this.mActivePointerId = -1;
    this.mHighlight = new ImageView(getContext());
    localObject = this.mHighlight;
    ((ImageView)localObject).setLayoutParams(new LayoutParams((View)localObject));
    localObject = this.mHighlight;
    addViewInLayout((View)localObject, -1, new LayoutParams((View)localObject));
    this.mClickFeedback = new ImageView(getContext());
    localObject = this.mClickFeedback;
    ((ImageView)localObject).setLayoutParams(new LayoutParams((View)localObject));
    localObject = this.mClickFeedback;
    addViewInLayout((View)localObject, -1, new LayoutParams((View)localObject));
    this.mClickFeedback.setVisibility(4);
    this.mStackSlider = new StackSlider();
    if (sHolographicHelper == null) {
      sHolographicHelper = new HolographicHelper(this.mContext);
    }
    setClipChildren(false);
    setClipToPadding(false);
    this.mStackMode = 1;
    this.mWhichChild = -1;
    this.mFramePadding = ((int)Math.ceil(4.0F * this.mContext.getResources().getDisplayMetrics().density));
  }
  
  private void measureChildren()
  {
    int i = getChildCount();
    int j = getMeasuredWidth();
    int k = getMeasuredHeight();
    int m = Math.round(j * 0.9F) - this.mPaddingLeft - this.mPaddingRight;
    int n = Math.round(k * 0.9F) - this.mPaddingTop - this.mPaddingBottom;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    while (i3 < i)
    {
      View localView = getChildAt(i3);
      localView.measure(View.MeasureSpec.makeMeasureSpec(m, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(n, Integer.MIN_VALUE));
      int i4 = i1;
      int i5 = i2;
      if (localView != this.mHighlight)
      {
        i4 = i1;
        i5 = i2;
        if (localView != this.mClickFeedback)
        {
          i5 = localView.getMeasuredWidth();
          int i6 = localView.getMeasuredHeight();
          int i7 = i1;
          if (i5 > i1) {
            i7 = i5;
          }
          i4 = i7;
          i5 = i2;
          if (i6 > i2)
          {
            i5 = i6;
            i4 = i7;
          }
        }
      }
      i3++;
      i1 = i4;
      i2 = i5;
    }
    this.mNewPerspectiveShiftX = (j * 0.1F);
    this.mNewPerspectiveShiftY = (k * 0.1F);
    if ((i1 > 0) && (i > 0) && (i1 < m)) {
      this.mNewPerspectiveShiftX = (j - i1);
    }
    if ((i2 > 0) && (i > 0) && (i2 < n)) {
      this.mNewPerspectiveShiftY = (k - i2);
    }
  }
  
  private void onLayout()
  {
    if (!this.mFirstLayoutHappened)
    {
      this.mFirstLayoutHappened = true;
      updateChildTransforms();
    }
    int i = Math.round(getMeasuredHeight() * 0.7F);
    if (this.mSlideAmount != i)
    {
      this.mSlideAmount = i;
      this.mSwipeThreshold = Math.round(i * 0.2F);
    }
    if ((Float.compare(this.mPerspectiveShiftY, this.mNewPerspectiveShiftY) != 0) || (Float.compare(this.mPerspectiveShiftX, this.mNewPerspectiveShiftX) != 0))
    {
      this.mPerspectiveShiftY = this.mNewPerspectiveShiftY;
      this.mPerspectiveShiftX = this.mNewPerspectiveShiftX;
      updateChildTransforms();
    }
  }
  
  private void onSecondaryPointerUp(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionIndex();
    if (paramMotionEvent.getPointerId(i) == this.mActivePointerId)
    {
      if (this.mSwipeGestureType == 2) {
        j = 0;
      } else {
        j = 1;
      }
      View localView = getViewAtRelativeIndex(j);
      if (localView == null) {
        return;
      }
      for (int j = 0; j < paramMotionEvent.getPointerCount(); j++) {
        if (j != i)
        {
          float f1 = paramMotionEvent.getX(j);
          float f2 = paramMotionEvent.getY(j);
          this.mTouchRect.set(localView.getLeft(), localView.getTop(), localView.getRight(), localView.getBottom());
          if (this.mTouchRect.contains(Math.round(f1), Math.round(f2)))
          {
            float f3 = paramMotionEvent.getX(i);
            float f4 = paramMotionEvent.getY(i);
            this.mInitialY += f2 - f4;
            this.mInitialX += f1 - f3;
            this.mActivePointerId = paramMotionEvent.getPointerId(j);
            paramMotionEvent = this.mVelocityTracker;
            if (paramMotionEvent != null) {
              paramMotionEvent.clear();
            }
            return;
          }
        }
      }
      handlePointerUp(paramMotionEvent);
    }
  }
  
  private void pacedScroll(boolean paramBoolean)
  {
    if (System.currentTimeMillis() - this.mLastScrollTime > 100L)
    {
      if (paramBoolean) {
        showPrevious();
      } else {
        showNext();
      }
      this.mLastScrollTime = System.currentTimeMillis();
    }
  }
  
  private void setupStackSlider(View paramView, int paramInt)
  {
    this.mStackSlider.setMode(paramInt);
    if (paramView != null)
    {
      this.mHighlight.setImageBitmap(sHolographicHelper.createResOutline(paramView, this.mResOutColor));
      this.mHighlight.setRotation(paramView.getRotation());
      this.mHighlight.setTranslationY(paramView.getTranslationY());
      this.mHighlight.setTranslationX(paramView.getTranslationX());
      this.mHighlight.bringToFront();
      paramView.bringToFront();
      this.mStackSlider.setView(paramView);
      paramView.setVisibility(0);
    }
  }
  
  private void transformViewAtIndex(int paramInt, View paramView, boolean paramBoolean)
  {
    float f1 = this.mPerspectiveShiftY;
    float f2 = this.mPerspectiveShiftX;
    int i;
    if (this.mStackMode == 1)
    {
      i = this.mMaxNumActiveViews - paramInt - 1;
      paramInt = i;
      if (i == this.mMaxNumActiveViews - 1) {
        paramInt = i - 1;
      }
    }
    else
    {
      i = paramInt - 1;
      paramInt = i;
      if (i < 0) {
        paramInt = i + 1;
      }
    }
    float f3 = paramInt * 1.0F / (this.mMaxNumActiveViews - 2);
    float f4 = 1.0F - (1.0F - f3) * 0.0F;
    f1 = f3 * f1 + (f4 - 1.0F) * (getMeasuredHeight() * 0.9F / 2.0F);
    f3 = (1.0F - f3) * f2 + (1.0F - f4) * (getMeasuredWidth() * 0.9F / 2.0F);
    if ((paramView instanceof StackFrame)) {
      ((StackFrame)paramView).cancelTransformAnimator();
    }
    if (paramBoolean)
    {
      Object localObject = PropertyValuesHolder.ofFloat("translationX", new float[] { f3 });
      PropertyValuesHolder localPropertyValuesHolder = PropertyValuesHolder.ofFloat("translationY", new float[] { f1 });
      localObject = ObjectAnimator.ofPropertyValuesHolder(paramView, new PropertyValuesHolder[] { PropertyValuesHolder.ofFloat("scaleX", new float[] { f4 }), PropertyValuesHolder.ofFloat("scaleY", new float[] { f4 }), localPropertyValuesHolder, localObject });
      ((ObjectAnimator)localObject).setDuration(100L);
      if ((paramView instanceof StackFrame)) {
        ((StackFrame)paramView).setTransformAnimator((ObjectAnimator)localObject);
      }
      ((ObjectAnimator)localObject).start();
    }
    else
    {
      paramView.setTranslationX(f3);
      paramView.setTranslationY(f1);
      paramView.setScaleX(f4);
      paramView.setScaleY(f4);
    }
  }
  
  private void updateChildTransforms()
  {
    for (int i = 0; i < getNumActiveViews(); i++)
    {
      View localView = getViewAtRelativeIndex(i);
      if (localView != null) {
        transformViewAtIndex(i, localView, false);
      }
    }
  }
  
  public void advance()
  {
    long l1 = System.currentTimeMillis();
    long l2 = this.mLastInteractionTime;
    if (this.mAdapter == null) {
      return;
    }
    if ((getCount() == 1) && (this.mLoopViews)) {
      return;
    }
    if ((this.mSwipeGestureType == 0) && (l1 - l2 > 5000L)) {
      showNext();
    }
  }
  
  void applyTransformForChildAtIndex(View paramView, int paramInt) {}
  
  LayoutParams createOrReuseLayoutParams(View paramView)
  {
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    if ((localLayoutParams instanceof LayoutParams))
    {
      paramView = (LayoutParams)localLayoutParams;
      paramView.setHorizontalOffset(0);
      paramView.setVerticalOffset(0);
      paramView.width = 0;
      paramView.width = 0;
      return paramView;
    }
    return new LayoutParams(paramView);
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    int i = 0;
    paramCanvas.getClipBounds(this.stackInvalidateRect);
    int j = getChildCount();
    for (int k = 0; k < j; k++)
    {
      View localView = getChildAt(k);
      Object localObject = (LayoutParams)localView.getLayoutParams();
      if (((((LayoutParams)localObject).horizontalOffset == 0) && (((LayoutParams)localObject).verticalOffset == 0)) || (localView.getAlpha() == 0.0F) || (localView.getVisibility() != 0)) {
        ((LayoutParams)localObject).resetInvalidateRect();
      }
      localObject = ((LayoutParams)localObject).getInvalidateRect();
      if (!((Rect)localObject).isEmpty())
      {
        i = 1;
        this.stackInvalidateRect.union((Rect)localObject);
      }
    }
    if (i != 0)
    {
      paramCanvas.save();
      paramCanvas.clipRectUnion(this.stackInvalidateRect);
      super.dispatchDraw(paramCanvas);
      paramCanvas.restore();
    }
    else
    {
      super.dispatchDraw(paramCanvas);
    }
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return StackView.class.getName();
  }
  
  FrameLayout getFrameForChild()
  {
    StackFrame localStackFrame = new StackFrame(this.mContext);
    int i = this.mFramePadding;
    localStackFrame.setPadding(i, i, i, i);
    return localStackFrame;
  }
  
  void hideTapFeedback(View paramView)
  {
    this.mClickFeedback.setVisibility(4);
    invalidate();
  }
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    if (((paramMotionEvent.getSource() & 0x2) != 0) && (paramMotionEvent.getAction() == 8))
    {
      float f = paramMotionEvent.getAxisValue(9);
      if (f < 0.0F)
      {
        pacedScroll(false);
        return true;
      }
      if (f > 0.0F)
      {
        pacedScroll(true);
        return true;
      }
    }
    return super.onGenericMotionEvent(paramMotionEvent);
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    boolean bool;
    if (getChildCount() > 1) {
      bool = true;
    } else {
      bool = false;
    }
    paramAccessibilityNodeInfo.setScrollable(bool);
    if (isEnabled())
    {
      if (getDisplayedChild() < getChildCount() - 1) {
        paramAccessibilityNodeInfo.addAction(4096);
      }
      if (getDisplayedChild() > 0) {
        paramAccessibilityNodeInfo.addAction(8192);
      }
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction() & 0xFF;
    boolean bool = true;
    if (i != 0)
    {
      if (i != 1) {
        if (i != 2)
        {
          if (i != 3)
          {
            if (i != 6) {
              break label135;
            }
            onSecondaryPointerUp(paramMotionEvent);
            break label135;
          }
        }
        else
        {
          i = paramMotionEvent.findPointerIndex(this.mActivePointerId);
          if (i == -1)
          {
            Log.d("StackView", "Error: No data for our primary pointer.");
            return false;
          }
          beginGestureIfNeeded(paramMotionEvent.getY(i) - this.mInitialY);
          break label135;
        }
      }
      this.mActivePointerId = -1;
      this.mSwipeGestureType = 0;
    }
    else if (this.mActivePointerId == -1)
    {
      this.mInitialX = paramMotionEvent.getX();
      this.mInitialY = paramMotionEvent.getY();
      this.mActivePointerId = paramMotionEvent.getPointerId(0);
    }
    label135:
    if (this.mSwipeGestureType == 0) {
      bool = false;
    }
    return bool;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    checkForAndHandleDataChanged();
    paramInt2 = getChildCount();
    for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
    {
      View localView = getChildAt(paramInt1);
      int i = this.mPaddingLeft;
      paramInt4 = localView.getMeasuredWidth();
      paramInt3 = this.mPaddingTop;
      int j = localView.getMeasuredHeight();
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      localView.layout(this.mPaddingLeft + localLayoutParams.horizontalOffset, this.mPaddingTop + localLayoutParams.verticalOffset, localLayoutParams.horizontalOffset + (i + paramInt4), localLayoutParams.verticalOffset + (paramInt3 + j));
    }
    onLayout();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt2);
    int k = View.MeasureSpec.getMode(paramInt1);
    int m = View.MeasureSpec.getMode(paramInt2);
    paramInt1 = this.mReferenceChildWidth;
    paramInt2 = 0;
    int n;
    if ((paramInt1 != -1) && (this.mReferenceChildHeight != -1)) {
      n = 1;
    } else {
      n = 0;
    }
    if (m == 0)
    {
      if (n != 0) {
        paramInt1 = Math.round(this.mReferenceChildHeight * (1.1111112F + 1.0F)) + this.mPaddingTop + this.mPaddingBottom;
      } else {
        paramInt1 = 0;
      }
    }
    else
    {
      paramInt1 = j;
      if (m == Integer.MIN_VALUE) {
        if (n != 0)
        {
          paramInt1 = Math.round(this.mReferenceChildHeight * (1.1111112F + 1.0F)) + this.mPaddingTop + this.mPaddingBottom;
          if (paramInt1 > j) {
            paramInt1 = j | 0x1000000;
          }
        }
        else
        {
          paramInt1 = 0;
        }
      }
    }
    if (k == 0)
    {
      if (n != 0) {
        paramInt2 = Math.round(this.mReferenceChildWidth * (1.0F + 1.1111112F)) + this.mPaddingLeft + this.mPaddingRight;
      }
    }
    else
    {
      paramInt2 = i;
      if (m == Integer.MIN_VALUE) {
        if (n != 0)
        {
          paramInt2 = this.mReferenceChildWidth + this.mPaddingLeft + this.mPaddingRight;
          if (paramInt2 > i) {
            paramInt2 = i | 0x1000000;
          }
        }
        else
        {
          paramInt2 = 0;
        }
      }
    }
    setMeasuredDimension(paramInt2, paramInt1);
    measureChildren();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    super.onTouchEvent(paramMotionEvent);
    int i = paramMotionEvent.getAction();
    int j = paramMotionEvent.findPointerIndex(this.mActivePointerId);
    if (j == -1)
    {
      Log.d("StackView", "Error: No data for our primary pointer.");
      return false;
    }
    float f1 = paramMotionEvent.getY(j);
    float f2 = paramMotionEvent.getX(j);
    f1 -= this.mInitialY;
    float f3 = this.mInitialX;
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
    this.mVelocityTracker.addMovement(paramMotionEvent);
    i &= 0xFF;
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
        else
        {
          this.mActivePointerId = -1;
          this.mSwipeGestureType = 0;
        }
      }
      else
      {
        beginGestureIfNeeded(f1);
        i = this.mSlideAmount;
        f3 = (f2 - f3) / (i * 1.0F);
        j = this.mSwipeGestureType;
        if (j == 2)
        {
          f2 = (f1 - this.mTouchSlop * 1.0F) / i * 1.0F;
          f1 = f2;
          if (this.mStackMode == 1) {
            f1 = 1.0F - f2;
          }
          this.mStackSlider.setYProgress(1.0F - f1);
          this.mStackSlider.setXProgress(f3);
          return true;
        }
        if (j == 1)
        {
          f2 = -(this.mTouchSlop * 1.0F + f1) / i * 1.0F;
          f1 = f2;
          if (this.mStackMode == 1) {
            f1 = 1.0F - f2;
          }
          this.mStackSlider.setYProgress(f1);
          this.mStackSlider.setXProgress(f3);
          return true;
        }
      }
    }
    else {
      handlePointerUp(paramMotionEvent);
    }
    return true;
  }
  
  public boolean performAccessibilityActionInternal(int paramInt, Bundle paramBundle)
  {
    if (super.performAccessibilityActionInternal(paramInt, paramBundle)) {
      return true;
    }
    if (!isEnabled()) {
      return false;
    }
    if (paramInt != 4096)
    {
      if (paramInt != 8192) {
        return false;
      }
      if (getDisplayedChild() > 0)
      {
        showPrevious();
        return true;
      }
      return false;
    }
    if (getDisplayedChild() < getChildCount() - 1)
    {
      showNext();
      return true;
    }
    return false;
  }
  
  @RemotableViewMethod
  public void showNext()
  {
    if (this.mSwipeGestureType != 0) {
      return;
    }
    if (!this.mTransitionIsSetup)
    {
      View localView = getViewAtRelativeIndex(1);
      if (localView != null)
      {
        setupStackSlider(localView, 0);
        this.mStackSlider.setYProgress(0.0F);
        this.mStackSlider.setXProgress(0.0F);
      }
    }
    super.showNext();
  }
  
  void showOnly(int paramInt, boolean paramBoolean)
  {
    super.showOnly(paramInt, paramBoolean);
    for (paramInt = this.mCurrentWindowEnd; paramInt >= this.mCurrentWindowStart; paramInt--)
    {
      int i = modulo(paramInt, getWindowSize());
      if ((AdapterViewAnimator.ViewAndMetaData)this.mViewsMap.get(Integer.valueOf(i)) != null)
      {
        localObject = ((AdapterViewAnimator.ViewAndMetaData)this.mViewsMap.get(Integer.valueOf(i))).view;
        if (localObject != null) {
          ((View)localObject).bringToFront();
        }
      }
    }
    Object localObject = this.mHighlight;
    if (localObject != null) {
      ((ImageView)localObject).bringToFront();
    }
    this.mTransitionIsSetup = false;
    this.mClickFeedbackIsValid = false;
  }
  
  @RemotableViewMethod
  public void showPrevious()
  {
    if (this.mSwipeGestureType != 0) {
      return;
    }
    if (!this.mTransitionIsSetup)
    {
      View localView = getViewAtRelativeIndex(0);
      if (localView != null)
      {
        setupStackSlider(localView, 0);
        this.mStackSlider.setYProgress(1.0F);
        this.mStackSlider.setXProgress(0.0F);
      }
    }
    super.showPrevious();
  }
  
  void showTapFeedback(View paramView)
  {
    updateClickFeedback();
    this.mClickFeedback.setVisibility(0);
    this.mClickFeedback.bringToFront();
    invalidate();
  }
  
  void transformViewForTransition(int paramInt1, int paramInt2, final View paramView, boolean paramBoolean)
  {
    Object localObject;
    if (!paramBoolean)
    {
      ((StackFrame)paramView).cancelSliderAnimator();
      paramView.setRotationX(0.0F);
      localObject = (LayoutParams)paramView.getLayoutParams();
      ((LayoutParams)localObject).setVerticalOffset(0);
      ((LayoutParams)localObject).setHorizontalOffset(0);
    }
    if ((paramInt1 == -1) && (paramInt2 == getNumActiveViews() - 1))
    {
      transformViewAtIndex(paramInt2, paramView, false);
      paramView.setVisibility(0);
      paramView.setAlpha(1.0F);
    }
    else
    {
      StackSlider localStackSlider;
      if ((paramInt1 == 0) && (paramInt2 == 1))
      {
        ((StackFrame)paramView).cancelSliderAnimator();
        paramView.setVisibility(0);
        paramInt1 = Math.round(this.mStackSlider.getDurationForNeutralPosition(this.mYVelocity));
        localStackSlider = new StackSlider(this.mStackSlider);
        localStackSlider.setView(paramView);
        if (paramBoolean)
        {
          localObject = PropertyValuesHolder.ofFloat("YProgress", new float[] { 0.0F });
          localObject = ObjectAnimator.ofPropertyValuesHolder(localStackSlider, new PropertyValuesHolder[] { PropertyValuesHolder.ofFloat("XProgress", new float[] { 0.0F }), localObject });
          ((ObjectAnimator)localObject).setDuration(paramInt1);
          ((ObjectAnimator)localObject).setInterpolator(new LinearInterpolator());
          ((StackFrame)paramView).setSliderAnimator((ObjectAnimator)localObject);
          ((ObjectAnimator)localObject).start();
        }
        else
        {
          localStackSlider.setYProgress(0.0F);
          localStackSlider.setXProgress(0.0F);
        }
      }
      else if ((paramInt1 == 1) && (paramInt2 == 0))
      {
        ((StackFrame)paramView).cancelSliderAnimator();
        paramInt1 = Math.round(this.mStackSlider.getDurationForOffscreenPosition(this.mYVelocity));
        localStackSlider = new StackSlider(this.mStackSlider);
        localStackSlider.setView(paramView);
        if (paramBoolean)
        {
          localObject = PropertyValuesHolder.ofFloat("YProgress", new float[] { 1.0F });
          localObject = ObjectAnimator.ofPropertyValuesHolder(localStackSlider, new PropertyValuesHolder[] { PropertyValuesHolder.ofFloat("XProgress", new float[] { 0.0F }), localObject });
          ((ObjectAnimator)localObject).setDuration(paramInt1);
          ((ObjectAnimator)localObject).setInterpolator(new LinearInterpolator());
          ((StackFrame)paramView).setSliderAnimator((ObjectAnimator)localObject);
          ((ObjectAnimator)localObject).start();
        }
        else
        {
          localStackSlider.setYProgress(1.0F);
          localStackSlider.setXProgress(0.0F);
        }
      }
      else if (paramInt2 == 0)
      {
        paramView.setAlpha(0.0F);
        paramView.setVisibility(4);
      }
      else if (((paramInt1 == 0) || (paramInt1 == 1)) && (paramInt2 > 1))
      {
        paramView.setVisibility(0);
        paramView.setAlpha(1.0F);
        paramView.setRotationX(0.0F);
        localObject = (LayoutParams)paramView.getLayoutParams();
        ((LayoutParams)localObject).setVerticalOffset(0);
        ((LayoutParams)localObject).setHorizontalOffset(0);
      }
      else if (paramInt1 == -1)
      {
        paramView.setAlpha(1.0F);
        paramView.setVisibility(0);
      }
      else if (paramInt2 == -1)
      {
        if (paramBoolean) {
          postDelayed(new Runnable()
          {
            public void run()
            {
              paramView.setAlpha(0.0F);
            }
          }, 100L);
        } else {
          paramView.setAlpha(0.0F);
        }
      }
    }
    if (paramInt2 != -1) {
      transformViewAtIndex(paramInt2, paramView, paramBoolean);
    }
  }
  
  void updateClickFeedback()
  {
    if (!this.mClickFeedbackIsValid)
    {
      View localView = getViewAtRelativeIndex(1);
      if (localView != null)
      {
        this.mClickFeedback.setImageBitmap(sHolographicHelper.createClickOutline(localView, this.mClickColor));
        this.mClickFeedback.setTranslationX(localView.getTranslationX());
        this.mClickFeedback.setTranslationY(localView.getTranslationY());
      }
      this.mClickFeedbackIsValid = true;
    }
  }
  
  private static class HolographicHelper
  {
    private static final int CLICK_FEEDBACK = 1;
    private static final int RES_OUT = 0;
    private final Paint mBlurPaint = new Paint();
    private final Canvas mCanvas = new Canvas();
    private float mDensity;
    private final Paint mErasePaint = new Paint();
    private final Paint mHolographicPaint = new Paint();
    private final Matrix mIdentityMatrix = new Matrix();
    private BlurMaskFilter mLargeBlurMaskFilter;
    private final Canvas mMaskCanvas = new Canvas();
    private BlurMaskFilter mSmallBlurMaskFilter;
    private final int[] mTmpXY = new int[2];
    
    HolographicHelper(Context paramContext)
    {
      this.mDensity = paramContext.getResources().getDisplayMetrics().density;
      this.mHolographicPaint.setFilterBitmap(true);
      this.mHolographicPaint.setMaskFilter(TableMaskFilter.CreateClipTable(0, 30));
      this.mErasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
      this.mErasePaint.setFilterBitmap(true);
      this.mSmallBlurMaskFilter = new BlurMaskFilter(this.mDensity * 2.0F, BlurMaskFilter.Blur.NORMAL);
      this.mLargeBlurMaskFilter = new BlurMaskFilter(this.mDensity * 4.0F, BlurMaskFilter.Blur.NORMAL);
    }
    
    Bitmap createClickOutline(View paramView, int paramInt)
    {
      return createOutline(paramView, 1, paramInt);
    }
    
    Bitmap createOutline(View paramView, int paramInt1, int paramInt2)
    {
      this.mHolographicPaint.setColor(paramInt2);
      if (paramInt1 == 0) {
        this.mBlurPaint.setMaskFilter(this.mSmallBlurMaskFilter);
      } else if (paramInt1 == 1) {
        this.mBlurPaint.setMaskFilter(this.mLargeBlurMaskFilter);
      }
      if ((paramView.getMeasuredWidth() != 0) && (paramView.getMeasuredHeight() != 0))
      {
        Bitmap localBitmap = Bitmap.createBitmap(paramView.getResources().getDisplayMetrics(), paramView.getMeasuredWidth(), paramView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        this.mCanvas.setBitmap(localBitmap);
        float f1 = paramView.getRotationX();
        float f2 = paramView.getRotation();
        float f3 = paramView.getTranslationY();
        float f4 = paramView.getTranslationX();
        paramView.setRotationX(0.0F);
        paramView.setRotation(0.0F);
        paramView.setTranslationY(0.0F);
        paramView.setTranslationX(0.0F);
        paramView.draw(this.mCanvas);
        paramView.setRotationX(f1);
        paramView.setRotation(f2);
        paramView.setTranslationY(f3);
        paramView.setTranslationX(f4);
        drawOutline(this.mCanvas, localBitmap);
        this.mCanvas.setBitmap(null);
        return localBitmap;
      }
      return null;
    }
    
    Bitmap createResOutline(View paramView, int paramInt)
    {
      return createOutline(paramView, 0, paramInt);
    }
    
    void drawOutline(Canvas paramCanvas, Bitmap paramBitmap)
    {
      int[] arrayOfInt = this.mTmpXY;
      Bitmap localBitmap = paramBitmap.extractAlpha(this.mBlurPaint, arrayOfInt);
      this.mMaskCanvas.setBitmap(localBitmap);
      this.mMaskCanvas.drawBitmap(paramBitmap, -arrayOfInt[0], -arrayOfInt[1], this.mErasePaint);
      paramCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
      paramCanvas.setMatrix(this.mIdentityMatrix);
      paramCanvas.drawBitmap(localBitmap, arrayOfInt[0], arrayOfInt[1], this.mHolographicPaint);
      this.mMaskCanvas.setBitmap(null);
      localBitmap.recycle();
    }
  }
  
  class LayoutParams
    extends ViewGroup.LayoutParams
  {
    private final Rect globalInvalidateRect = new Rect();
    int horizontalOffset = 0;
    private final Rect invalidateRect = new Rect();
    private final RectF invalidateRectf = new RectF();
    View mView;
    private final Rect parentRect = new Rect();
    int verticalOffset = 0;
    
    LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      this.width = 0;
      this.height = 0;
    }
    
    LayoutParams(View paramView)
    {
      super(0);
      this.width = 0;
      this.height = 0;
      this.mView = paramView;
    }
    
    Rect getInvalidateRect()
    {
      return this.invalidateRect;
    }
    
    void invalidateGlobalRegion(View paramView, Rect paramRect)
    {
      this.globalInvalidateRect.set(paramRect);
      this.globalInvalidateRect.union(0, 0, StackView.this.getWidth(), StackView.this.getHeight());
      paramRect = paramView;
      if ((paramView.getParent() != null) && ((paramView.getParent() instanceof View)))
      {
        int i = 1;
        this.parentRect.set(0, 0, 0, 0);
        while ((paramRect.getParent() != null) && ((paramRect.getParent() instanceof View)) && (!this.parentRect.contains(this.globalInvalidateRect)))
        {
          if (i == 0) {
            this.globalInvalidateRect.offset(paramRect.getLeft() - paramRect.getScrollX(), paramRect.getTop() - paramRect.getScrollY());
          }
          i = 0;
          paramRect = (View)paramRect.getParent();
          this.parentRect.set(paramRect.getScrollX(), paramRect.getScrollY(), paramRect.getWidth() + paramRect.getScrollX(), paramRect.getHeight() + paramRect.getScrollY());
          paramRect.invalidate(this.globalInvalidateRect.left, this.globalInvalidateRect.top, this.globalInvalidateRect.right, this.globalInvalidateRect.bottom);
        }
        paramRect.invalidate(this.globalInvalidateRect.left, this.globalInvalidateRect.top, this.globalInvalidateRect.right, this.globalInvalidateRect.bottom);
        return;
      }
    }
    
    void resetInvalidateRect()
    {
      this.invalidateRect.set(0, 0, 0, 0);
    }
    
    public void setHorizontalOffset(int paramInt)
    {
      setOffsets(paramInt, this.verticalOffset);
    }
    
    public void setOffsets(int paramInt1, int paramInt2)
    {
      int i = paramInt1 - this.horizontalOffset;
      this.horizontalOffset = paramInt1;
      paramInt1 = paramInt2 - this.verticalOffset;
      this.verticalOffset = paramInt2;
      View localView = this.mView;
      if (localView != null)
      {
        localView.requestLayout();
        paramInt2 = Math.min(this.mView.getLeft() + i, this.mView.getLeft());
        i = Math.max(this.mView.getRight() + i, this.mView.getRight());
        int j = Math.min(this.mView.getTop() + paramInt1, this.mView.getTop());
        paramInt1 = Math.max(this.mView.getBottom() + paramInt1, this.mView.getBottom());
        this.invalidateRectf.set(paramInt2, j, i, paramInt1);
        float f1 = -this.invalidateRectf.left;
        float f2 = -this.invalidateRectf.top;
        this.invalidateRectf.offset(f1, f2);
        this.mView.getMatrix().mapRect(this.invalidateRectf);
        this.invalidateRectf.offset(-f1, -f2);
        this.invalidateRect.set((int)Math.floor(this.invalidateRectf.left), (int)Math.floor(this.invalidateRectf.top), (int)Math.ceil(this.invalidateRectf.right), (int)Math.ceil(this.invalidateRectf.bottom));
        invalidateGlobalRegion(this.mView, this.invalidateRect);
      }
    }
    
    public void setVerticalOffset(int paramInt)
    {
      setOffsets(this.horizontalOffset, paramInt);
    }
  }
  
  private static class StackFrame
    extends FrameLayout
  {
    WeakReference<ObjectAnimator> sliderAnimator;
    WeakReference<ObjectAnimator> transformAnimator;
    
    public StackFrame(Context paramContext)
    {
      super();
    }
    
    boolean cancelSliderAnimator()
    {
      Object localObject = this.sliderAnimator;
      if (localObject != null)
      {
        localObject = (ObjectAnimator)((WeakReference)localObject).get();
        if (localObject != null)
        {
          ((ObjectAnimator)localObject).cancel();
          return true;
        }
      }
      return false;
    }
    
    boolean cancelTransformAnimator()
    {
      Object localObject = this.transformAnimator;
      if (localObject != null)
      {
        localObject = (ObjectAnimator)((WeakReference)localObject).get();
        if (localObject != null)
        {
          ((ObjectAnimator)localObject).cancel();
          return true;
        }
      }
      return false;
    }
    
    void setSliderAnimator(ObjectAnimator paramObjectAnimator)
    {
      this.sliderAnimator = new WeakReference(paramObjectAnimator);
    }
    
    void setTransformAnimator(ObjectAnimator paramObjectAnimator)
    {
      this.transformAnimator = new WeakReference(paramObjectAnimator);
    }
  }
  
  private class StackSlider
  {
    static final int BEGINNING_OF_STACK_MODE = 1;
    static final int END_OF_STACK_MODE = 2;
    static final int NORMAL_MODE = 0;
    int mMode = 0;
    View mView;
    float mXProgress;
    float mYProgress;
    
    public StackSlider() {}
    
    public StackSlider(StackSlider paramStackSlider)
    {
      this.mView = paramStackSlider.mView;
      this.mYProgress = paramStackSlider.mYProgress;
      this.mXProgress = paramStackSlider.mXProgress;
      this.mMode = paramStackSlider.mMode;
    }
    
    private float cubic(float paramFloat)
    {
      return (float)(Math.pow(paramFloat * 2.0F - 1.0F, 3.0D) + 1.0D) / 2.0F;
    }
    
    private float getDuration(boolean paramBoolean, float paramFloat)
    {
      Object localObject = this.mView;
      if (localObject != null)
      {
        localObject = (StackView.LayoutParams)((View)localObject).getLayoutParams();
        float f1 = (float)Math.hypot(((StackView.LayoutParams)localObject).horizontalOffset, ((StackView.LayoutParams)localObject).verticalOffset);
        float f2 = (float)Math.hypot(StackView.this.mSlideAmount, StackView.this.mSlideAmount * 0.4F);
        float f3 = f1;
        if (f1 > f2) {
          f3 = f2;
        }
        if (paramFloat == 0.0F)
        {
          if (paramBoolean) {
            paramFloat = 1.0F - f3 / f2;
          } else {
            paramFloat = f3 / f2;
          }
          return paramFloat * 400.0F;
        }
        if (paramBoolean) {
          paramFloat = f3 / Math.abs(paramFloat);
        } else {
          paramFloat = (f2 - f3) / Math.abs(paramFloat);
        }
        if ((paramFloat >= 50.0F) && (paramFloat <= 400.0F)) {
          return paramFloat;
        }
        return getDuration(paramBoolean, 0.0F);
      }
      return 0.0F;
    }
    
    private float highlightAlphaInterpolator(float paramFloat)
    {
      if (paramFloat < 0.4F) {
        return cubic(paramFloat / 0.4F) * 0.85F;
      }
      return cubic(1.0F - (paramFloat - 0.4F) / (1.0F - 0.4F)) * 0.85F;
    }
    
    private float rotationInterpolator(float paramFloat)
    {
      if (paramFloat < 0.2F) {
        return 0.0F;
      }
      return (paramFloat - 0.2F) / (1.0F - 0.2F);
    }
    
    private float viewAlphaInterpolator(float paramFloat)
    {
      if (paramFloat > 0.3F) {
        return (paramFloat - 0.3F) / (1.0F - 0.3F);
      }
      return 0.0F;
    }
    
    float getDurationForNeutralPosition()
    {
      return getDuration(false, 0.0F);
    }
    
    float getDurationForNeutralPosition(float paramFloat)
    {
      return getDuration(false, paramFloat);
    }
    
    float getDurationForOffscreenPosition()
    {
      return getDuration(true, 0.0F);
    }
    
    float getDurationForOffscreenPosition(float paramFloat)
    {
      return getDuration(true, paramFloat);
    }
    
    public float getXProgress()
    {
      return this.mXProgress;
    }
    
    public float getYProgress()
    {
      return this.mYProgress;
    }
    
    void setMode(int paramInt)
    {
      this.mMode = paramInt;
    }
    
    void setView(View paramView)
    {
      this.mView = paramView;
    }
    
    public void setXProgress(float paramFloat)
    {
      paramFloat = Math.max(-2.0F, Math.min(2.0F, paramFloat));
      this.mXProgress = paramFloat;
      Object localObject = this.mView;
      if (localObject == null) {
        return;
      }
      localObject = (StackView.LayoutParams)((View)localObject).getLayoutParams();
      StackView.LayoutParams localLayoutParams = (StackView.LayoutParams)StackView.this.mHighlight.getLayoutParams();
      paramFloat *= 0.2F;
      ((StackView.LayoutParams)localObject).setHorizontalOffset(Math.round(StackView.this.mSlideAmount * paramFloat));
      localLayoutParams.setHorizontalOffset(Math.round(StackView.this.mSlideAmount * paramFloat));
    }
    
    public void setYProgress(float paramFloat)
    {
      float f = Math.max(0.0F, Math.min(1.0F, paramFloat));
      this.mYProgress = f;
      Object localObject = this.mView;
      if (localObject == null) {
        return;
      }
      localObject = (StackView.LayoutParams)((View)localObject).getLayoutParams();
      StackView.LayoutParams localLayoutParams = (StackView.LayoutParams)StackView.this.mHighlight.getLayoutParams();
      int i;
      if (StackView.this.mStackMode == 0) {
        i = 1;
      } else {
        i = -1;
      }
      if ((Float.compare(0.0F, this.mYProgress) != 0) && (Float.compare(1.0F, this.mYProgress) != 0))
      {
        if (this.mView.getLayerType() == 0) {
          this.mView.setLayerType(2, null);
        }
      }
      else if (this.mView.getLayerType() != 0) {
        this.mView.setLayerType(0, null);
      }
      int j = this.mMode;
      if (j != 0)
      {
        if (j != 1)
        {
          if (j == 2)
          {
            paramFloat = f * 0.2F;
            ((StackView.LayoutParams)localObject).setVerticalOffset(Math.round(-i * paramFloat * StackView.this.mSlideAmount));
            localLayoutParams.setVerticalOffset(Math.round(-i * paramFloat * StackView.this.mSlideAmount));
            StackView.this.mHighlight.setAlpha(highlightAlphaInterpolator(paramFloat));
          }
        }
        else
        {
          paramFloat = (1.0F - f) * 0.2F;
          ((StackView.LayoutParams)localObject).setVerticalOffset(Math.round(i * paramFloat * StackView.this.mSlideAmount));
          localLayoutParams.setVerticalOffset(Math.round(i * paramFloat * StackView.this.mSlideAmount));
          StackView.this.mHighlight.setAlpha(highlightAlphaInterpolator(paramFloat));
        }
      }
      else
      {
        ((StackView.LayoutParams)localObject).setVerticalOffset(Math.round(-f * i * StackView.this.mSlideAmount));
        localLayoutParams.setVerticalOffset(Math.round(-f * i * StackView.this.mSlideAmount));
        StackView.this.mHighlight.setAlpha(highlightAlphaInterpolator(f));
        paramFloat = viewAlphaInterpolator(1.0F - f);
        if ((this.mView.getAlpha() == 0.0F) && (paramFloat != 0.0F) && (this.mView.getVisibility() != 0)) {
          this.mView.setVisibility(0);
        } else if ((paramFloat == 0.0F) && (this.mView.getAlpha() != 0.0F) && (this.mView.getVisibility() == 0)) {
          this.mView.setVisibility(4);
        }
        this.mView.setAlpha(paramFloat);
        this.mView.setRotationX(i * 90.0F * rotationInterpolator(f));
        StackView.this.mHighlight.setRotationX(i * 90.0F * rotationInterpolator(f));
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/StackView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */