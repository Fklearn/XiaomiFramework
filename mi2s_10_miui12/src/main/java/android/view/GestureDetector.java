package android.view;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.StatsLog;

public class GestureDetector
{
  private static final int DOUBLE_TAP_MIN_TIME = ViewConfiguration.getDoubleTapMinTime();
  private static final int DOUBLE_TAP_TIMEOUT;
  @UnsupportedAppUsage(maxTargetSdk=28)
  private static final int LONGPRESS_TIMEOUT = ;
  private static final int LONG_PRESS = 2;
  private static final int SHOW_PRESS = 1;
  private static final int TAP = 3;
  private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
  private boolean mAlwaysInBiggerTapRegion;
  @UnsupportedAppUsage
  private boolean mAlwaysInTapRegion;
  private OnContextClickListener mContextClickListener;
  private MotionEvent mCurrentDownEvent;
  private MotionEvent mCurrentMotionEvent;
  private boolean mDeferConfirmSingleTap;
  private OnDoubleTapListener mDoubleTapListener;
  private int mDoubleTapSlopSquare;
  private int mDoubleTapTouchSlopSquare;
  private float mDownFocusX;
  private float mDownFocusY;
  private final Handler mHandler;
  private boolean mHasRecordedClassification;
  private boolean mIgnoreNextUpEvent;
  private boolean mInContextClick;
  private boolean mInLongPress;
  private final InputEventConsistencyVerifier mInputEventConsistencyVerifier;
  private boolean mIsDoubleTapping;
  private boolean mIsLongpressEnabled;
  private float mLastFocusX;
  private float mLastFocusY;
  @UnsupportedAppUsage
  private final OnGestureListener mListener;
  private int mMaximumFlingVelocity;
  @UnsupportedAppUsage
  private int mMinimumFlingVelocity;
  private MotionEvent mPreviousUpEvent;
  private boolean mStillDown;
  @UnsupportedAppUsage
  private int mTouchSlopSquare;
  private VelocityTracker mVelocityTracker;
  
  static
  {
    DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
  }
  
  public GestureDetector(Context paramContext, OnGestureListener paramOnGestureListener)
  {
    this(paramContext, paramOnGestureListener, null);
  }
  
  public GestureDetector(Context paramContext, OnGestureListener paramOnGestureListener, Handler paramHandler)
  {
    InputEventConsistencyVerifier localInputEventConsistencyVerifier;
    if (InputEventConsistencyVerifier.isInstrumentationEnabled()) {
      localInputEventConsistencyVerifier = new InputEventConsistencyVerifier(this, 0);
    } else {
      localInputEventConsistencyVerifier = null;
    }
    this.mInputEventConsistencyVerifier = localInputEventConsistencyVerifier;
    if (paramHandler != null) {
      this.mHandler = new GestureHandler(paramHandler);
    } else {
      this.mHandler = new GestureHandler();
    }
    this.mListener = paramOnGestureListener;
    if ((paramOnGestureListener instanceof OnDoubleTapListener)) {
      setOnDoubleTapListener((OnDoubleTapListener)paramOnGestureListener);
    }
    if ((paramOnGestureListener instanceof OnContextClickListener)) {
      setContextClickListener((OnContextClickListener)paramOnGestureListener);
    }
    init(paramContext);
  }
  
  public GestureDetector(Context paramContext, OnGestureListener paramOnGestureListener, Handler paramHandler, boolean paramBoolean)
  {
    this(paramContext, paramOnGestureListener, paramHandler);
  }
  
  @Deprecated
  public GestureDetector(OnGestureListener paramOnGestureListener)
  {
    this(null, paramOnGestureListener, null);
  }
  
  @Deprecated
  public GestureDetector(OnGestureListener paramOnGestureListener, Handler paramHandler)
  {
    this(null, paramOnGestureListener, paramHandler);
  }
  
  private void cancel()
  {
    this.mHandler.removeMessages(1);
    this.mHandler.removeMessages(2);
    this.mHandler.removeMessages(3);
    this.mVelocityTracker.recycle();
    this.mVelocityTracker = null;
    this.mIsDoubleTapping = false;
    this.mStillDown = false;
    this.mAlwaysInTapRegion = false;
    this.mAlwaysInBiggerTapRegion = false;
    this.mDeferConfirmSingleTap = false;
    this.mInLongPress = false;
    this.mInContextClick = false;
    this.mIgnoreNextUpEvent = false;
  }
  
  private void cancelTaps()
  {
    this.mHandler.removeMessages(1);
    this.mHandler.removeMessages(2);
    this.mHandler.removeMessages(3);
    this.mIsDoubleTapping = false;
    this.mAlwaysInTapRegion = false;
    this.mAlwaysInBiggerTapRegion = false;
    this.mDeferConfirmSingleTap = false;
    this.mInLongPress = false;
    this.mInContextClick = false;
    this.mIgnoreNextUpEvent = false;
  }
  
  private void dispatchLongPress()
  {
    this.mHandler.removeMessages(3);
    this.mDeferConfirmSingleTap = false;
    this.mInLongPress = true;
    this.mListener.onLongPress(this.mCurrentDownEvent);
  }
  
  private void init(Context paramContext)
  {
    if (this.mListener != null)
    {
      this.mIsLongpressEnabled = true;
      int i;
      int j;
      int k;
      if (paramContext == null)
      {
        i = ViewConfiguration.getTouchSlop();
        j = i;
        k = ViewConfiguration.getDoubleTapSlop();
        this.mMinimumFlingVelocity = ViewConfiguration.getMinimumFlingVelocity();
        this.mMaximumFlingVelocity = ViewConfiguration.getMaximumFlingVelocity();
      }
      else
      {
        paramContext = ViewConfiguration.get(paramContext);
        i = paramContext.getScaledTouchSlop();
        j = paramContext.getScaledDoubleTapTouchSlop();
        k = paramContext.getScaledDoubleTapSlop();
        this.mMinimumFlingVelocity = paramContext.getScaledMinimumFlingVelocity();
        this.mMaximumFlingVelocity = paramContext.getScaledMaximumFlingVelocity();
      }
      this.mTouchSlopSquare = (i * i);
      this.mDoubleTapTouchSlopSquare = (j * j);
      this.mDoubleTapSlopSquare = (k * k);
      return;
    }
    throw new NullPointerException("OnGestureListener must not be null");
  }
  
  private boolean isConsideredDoubleTap(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, MotionEvent paramMotionEvent3)
  {
    boolean bool1 = this.mAlwaysInBiggerTapRegion;
    boolean bool2 = false;
    if (!bool1) {
      return false;
    }
    long l = paramMotionEvent3.getEventTime() - paramMotionEvent2.getEventTime();
    if ((l <= DOUBLE_TAP_TIMEOUT) && (l >= DOUBLE_TAP_MIN_TIME))
    {
      int i = (int)paramMotionEvent1.getX() - (int)paramMotionEvent3.getX();
      int j = (int)paramMotionEvent1.getY() - (int)paramMotionEvent3.getY();
      int k;
      if ((paramMotionEvent1.getFlags() & 0x8) != 0) {
        k = 1;
      } else {
        k = 0;
      }
      if (k != 0) {
        k = 0;
      } else {
        k = this.mDoubleTapSlopSquare;
      }
      if (i * i + j * j < k) {
        bool2 = true;
      }
      return bool2;
    }
    return false;
  }
  
  private void recordGestureClassification(int paramInt)
  {
    if ((!this.mHasRecordedClassification) && (paramInt != 0))
    {
      if ((this.mCurrentDownEvent != null) && (this.mCurrentMotionEvent != null))
      {
        StatsLog.write(177, getClass().getName(), paramInt, (int)(SystemClock.uptimeMillis() - this.mCurrentMotionEvent.getDownTime()), (float)Math.hypot(this.mCurrentMotionEvent.getRawX() - this.mCurrentDownEvent.getRawX(), this.mCurrentMotionEvent.getRawY() - this.mCurrentDownEvent.getRawY()));
        this.mHasRecordedClassification = true;
        return;
      }
      this.mHasRecordedClassification = true;
      return;
    }
  }
  
  public boolean isLongpressEnabled()
  {
    return this.mIsLongpressEnabled;
  }
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    InputEventConsistencyVerifier localInputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
    if (localInputEventConsistencyVerifier != null) {
      localInputEventConsistencyVerifier.onGenericMotionEvent(paramMotionEvent, 0);
    }
    int i = paramMotionEvent.getActionButton();
    int j = paramMotionEvent.getActionMasked();
    if (j != 11)
    {
      if ((j == 12) && (this.mInContextClick) && ((i == 32) || (i == 2)))
      {
        this.mInContextClick = false;
        this.mIgnoreNextUpEvent = true;
      }
    }
    else if ((this.mContextClickListener != null) && (!this.mInContextClick) && (!this.mInLongPress) && ((i == 32) || (i == 2)) && (this.mContextClickListener.onContextClick(paramMotionEvent)))
    {
      this.mInContextClick = true;
      this.mHandler.removeMessages(2);
      this.mHandler.removeMessages(3);
      return true;
    }
    return false;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    Object localObject1 = this.mInputEventConsistencyVerifier;
    if (localObject1 != null) {
      ((InputEventConsistencyVerifier)localObject1).onTouchEvent(paramMotionEvent, 0);
    }
    int i = paramMotionEvent.getAction();
    localObject1 = this.mCurrentMotionEvent;
    if (localObject1 != null) {
      ((MotionEvent)localObject1).recycle();
    }
    this.mCurrentMotionEvent = MotionEvent.obtain(paramMotionEvent);
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
    this.mVelocityTracker.addMovement(paramMotionEvent);
    if ((i & 0xFF) == 6) {
      j = 1;
    } else {
      j = 0;
    }
    int k;
    if (j != 0) {
      k = paramMotionEvent.getActionIndex();
    } else {
      k = -1;
    }
    int m;
    if ((paramMotionEvent.getFlags() & 0x8) != 0) {
      m = 1;
    } else {
      m = 0;
    }
    float f1 = 0.0F;
    float f2 = 0.0F;
    int n = paramMotionEvent.getPointerCount();
    for (int i1 = 0; i1 < n; i1++) {
      if (k != i1)
      {
        f1 += paramMotionEvent.getX(i1);
        f2 += paramMotionEvent.getY(i1);
      }
    }
    if (j != 0) {
      j = n - 1;
    } else {
      j = n;
    }
    f1 /= j;
    float f3 = f2 / j;
    int j = i & 0xFF;
    boolean bool3;
    boolean bool4;
    Object localObject2;
    if (j != 0)
    {
      if (j != 1)
      {
        if (j != 2)
        {
          if (j != 3)
          {
            if (j != 5)
            {
              if (j == 6)
              {
                this.mLastFocusX = f1;
                this.mDownFocusX = f1;
                this.mLastFocusY = f3;
                this.mDownFocusY = f3;
                this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
                k = paramMotionEvent.getActionIndex();
                j = paramMotionEvent.getPointerId(k);
                f2 = this.mVelocityTracker.getXVelocity(j);
                f1 = this.mVelocityTracker.getYVelocity(j);
                for (m = 0; m < n; m++) {
                  if (m != k)
                  {
                    i1 = paramMotionEvent.getPointerId(m);
                    if (this.mVelocityTracker.getXVelocity(i1) * f2 + this.mVelocityTracker.getYVelocity(i1) * f1 < 0.0F)
                    {
                      this.mVelocityTracker.clear();
                      break;
                    }
                  }
                }
              }
            }
            else
            {
              this.mLastFocusX = f1;
              this.mDownFocusX = f1;
              this.mLastFocusY = f3;
              this.mDownFocusY = f3;
              cancelTaps();
            }
          }
          else {
            cancel();
          }
        }
        else if ((!this.mInLongPress) && (!this.mInContextClick))
        {
          i1 = paramMotionEvent.getClassification();
          boolean bool2 = this.mHandler.hasMessages(2);
          float f4 = this.mLastFocusX - f1;
          f2 = this.mLastFocusY - f3;
          if (this.mIsDoubleTapping)
          {
            recordGestureClassification(2);
            bool3 = this.mDoubleTapListener.onDoubleTapEvent(paramMotionEvent) | false;
          }
          else if (this.mAlwaysInTapRegion)
          {
            j = (int)(f1 - this.mDownFocusX);
            k = (int)(f3 - this.mDownFocusY);
            n = j * j + k * k;
            if (m != 0) {
              j = 0;
            } else {
              j = this.mTouchSlopSquare;
            }
            if (i1 == 1) {
              k = 1;
            } else {
              k = 0;
            }
            if ((bool2) && (k != 0)) {
              k = 1;
            } else {
              k = 0;
            }
            if (k != 0)
            {
              float f5 = ViewConfiguration.getAmbiguousGestureMultiplier();
              if (n > j)
              {
                this.mHandler.removeMessages(2);
                long l = ViewConfiguration.getLongPressTimeout();
                localObject1 = this.mHandler;
                ((Handler)localObject1).sendMessageAtTime(((Handler)localObject1).obtainMessage(2, 3, 0), paramMotionEvent.getDownTime() + ((float)l * f5));
              }
              j = (int)(j * (f5 * f5));
            }
            if (n > j)
            {
              recordGestureClassification(5);
              bool3 = this.mListener.onScroll(this.mCurrentDownEvent, paramMotionEvent, f4, f2);
              this.mLastFocusX = f1;
              this.mLastFocusY = f3;
              this.mAlwaysInTapRegion = false;
              this.mHandler.removeMessages(3);
              this.mHandler.removeMessages(1);
              this.mHandler.removeMessages(2);
            }
            else
            {
              bool3 = false;
            }
            if (m != 0) {
              j = 0;
            } else {
              j = this.mDoubleTapTouchSlopSquare;
            }
            if (n > j) {
              this.mAlwaysInBiggerTapRegion = false;
            }
          }
          else if ((Math.abs(f4) < 1.0F) && (Math.abs(f2) < 1.0F))
          {
            bool3 = false;
          }
          else
          {
            recordGestureClassification(5);
            bool3 = this.mListener.onScroll(this.mCurrentDownEvent, paramMotionEvent, f4, f2);
            this.mLastFocusX = f1;
            this.mLastFocusY = f3;
          }
          if (i1 == 2) {
            j = 1;
          } else {
            j = 0;
          }
          bool4 = bool3;
          if (j == 0) {
            break label1540;
          }
          bool4 = bool3;
          if (!bool2) {
            break label1540;
          }
          this.mHandler.removeMessages(2);
          localObject1 = this.mHandler;
          ((Handler)localObject1).sendMessage(((Handler)localObject1).obtainMessage(2, 4, 0));
          bool4 = bool3;
          break label1540;
        }
        bool4 = false;
      }
      else
      {
        this.mStillDown = false;
        localObject1 = MotionEvent.obtain(paramMotionEvent);
        if (this.mIsDoubleTapping)
        {
          recordGestureClassification(2);
          bool3 = false | this.mDoubleTapListener.onDoubleTapEvent(paramMotionEvent);
        }
        else
        {
          if (this.mInLongPress)
          {
            this.mHandler.removeMessages(3);
            this.mInLongPress = false;
          }
          else
          {
            if ((this.mAlwaysInTapRegion) && (!this.mIgnoreNextUpEvent))
            {
              recordGestureClassification(1);
              bool4 = this.mListener.onSingleTapUp(paramMotionEvent);
              bool3 = bool4;
              if (!this.mDeferConfirmSingleTap) {
                break label1185;
              }
              localObject2 = this.mDoubleTapListener;
              bool3 = bool4;
              if (localObject2 == null) {
                break label1185;
              }
              ((OnDoubleTapListener)localObject2).onSingleTapConfirmed(paramMotionEvent);
              bool3 = bool4;
              break label1185;
            }
            if (!this.mIgnoreNextUpEvent)
            {
              localObject2 = this.mVelocityTracker;
              j = paramMotionEvent.getPointerId(0);
              ((VelocityTracker)localObject2).computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
              f2 = ((VelocityTracker)localObject2).getYVelocity(j);
              f1 = ((VelocityTracker)localObject2).getXVelocity(j);
              if ((Math.abs(f2) > this.mMinimumFlingVelocity) || (Math.abs(f1) > this.mMinimumFlingVelocity))
              {
                bool3 = this.mListener.onFling(this.mCurrentDownEvent, paramMotionEvent, f1, f2);
                break label1185;
              }
            }
          }
          bool3 = false;
        }
        label1185:
        localObject2 = this.mPreviousUpEvent;
        if (localObject2 != null) {
          ((MotionEvent)localObject2).recycle();
        }
        this.mPreviousUpEvent = ((MotionEvent)localObject1);
        localObject1 = this.mVelocityTracker;
        if (localObject1 != null)
        {
          ((VelocityTracker)localObject1).recycle();
          this.mVelocityTracker = null;
        }
        this.mIsDoubleTapping = false;
        this.mDeferConfirmSingleTap = false;
        this.mIgnoreNextUpEvent = false;
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        bool4 = bool3;
      }
    }
    else
    {
      boolean bool1;
      if (this.mDoubleTapListener != null)
      {
        bool3 = this.mHandler.hasMessages(3);
        if (bool3) {
          this.mHandler.removeMessages(3);
        }
        localObject1 = this.mCurrentDownEvent;
        if (localObject1 != null)
        {
          localObject2 = this.mPreviousUpEvent;
          if ((localObject2 != null) && (bool3) && (isConsideredDoubleTap((MotionEvent)localObject1, (MotionEvent)localObject2, paramMotionEvent)))
          {
            this.mIsDoubleTapping = true;
            recordGestureClassification(2);
            bool1 = false | this.mDoubleTapListener.onDoubleTap(this.mCurrentDownEvent) | this.mDoubleTapListener.onDoubleTapEvent(paramMotionEvent);
            break label1385;
          }
        }
        this.mHandler.sendEmptyMessageDelayed(3, DOUBLE_TAP_TIMEOUT);
      }
      else
      {
        bool1 = false;
      }
      label1385:
      this.mLastFocusX = f1;
      this.mDownFocusX = f1;
      this.mLastFocusY = f3;
      this.mDownFocusY = f3;
      localObject1 = this.mCurrentDownEvent;
      if (localObject1 != null) {
        ((MotionEvent)localObject1).recycle();
      }
      this.mCurrentDownEvent = MotionEvent.obtain(paramMotionEvent);
      this.mAlwaysInTapRegion = true;
      this.mAlwaysInBiggerTapRegion = true;
      this.mStillDown = true;
      this.mInLongPress = false;
      this.mDeferConfirmSingleTap = false;
      this.mHasRecordedClassification = false;
      if (this.mIsLongpressEnabled)
      {
        this.mHandler.removeMessages(2);
        localObject1 = this.mHandler;
        ((Handler)localObject1).sendMessageAtTime(((Handler)localObject1).obtainMessage(2, 3, 0), this.mCurrentDownEvent.getDownTime() + ViewConfiguration.getLongPressTimeout());
      }
      this.mHandler.sendEmptyMessageAtTime(1, this.mCurrentDownEvent.getDownTime() + TAP_TIMEOUT);
      bool4 = bool1 | this.mListener.onDown(paramMotionEvent);
    }
    label1540:
    if (!bool4)
    {
      localObject1 = this.mInputEventConsistencyVerifier;
      if (localObject1 != null) {
        ((InputEventConsistencyVerifier)localObject1).onUnhandledEvent(paramMotionEvent, 0);
      }
    }
    return bool4;
  }
  
  public void setContextClickListener(OnContextClickListener paramOnContextClickListener)
  {
    this.mContextClickListener = paramOnContextClickListener;
  }
  
  public void setIsLongpressEnabled(boolean paramBoolean)
  {
    this.mIsLongpressEnabled = paramBoolean;
  }
  
  public void setOnDoubleTapListener(OnDoubleTapListener paramOnDoubleTapListener)
  {
    this.mDoubleTapListener = paramOnDoubleTapListener;
  }
  
  private class GestureHandler
    extends Handler
  {
    GestureHandler() {}
    
    GestureHandler(Handler paramHandler)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      int i = paramMessage.what;
      if (i != 1)
      {
        if (i != 2)
        {
          if (i == 3)
          {
            if (GestureDetector.this.mDoubleTapListener != null) {
              if (!GestureDetector.this.mStillDown)
              {
                GestureDetector.this.recordGestureClassification(1);
                GestureDetector.this.mDoubleTapListener.onSingleTapConfirmed(GestureDetector.this.mCurrentDownEvent);
              }
              else
              {
                GestureDetector.access$602(GestureDetector.this, true);
              }
            }
          }
          else
          {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("Unknown message ");
            localStringBuilder.append(paramMessage);
            throw new RuntimeException(localStringBuilder.toString());
          }
        }
        else
        {
          GestureDetector.this.recordGestureClassification(paramMessage.arg1);
          GestureDetector.this.dispatchLongPress();
        }
      }
      else {
        GestureDetector.this.mListener.onShowPress(GestureDetector.this.mCurrentDownEvent);
      }
    }
  }
  
  public static abstract interface OnContextClickListener
  {
    public abstract boolean onContextClick(MotionEvent paramMotionEvent);
  }
  
  public static abstract interface OnDoubleTapListener
  {
    public abstract boolean onDoubleTap(MotionEvent paramMotionEvent);
    
    public abstract boolean onDoubleTapEvent(MotionEvent paramMotionEvent);
    
    public abstract boolean onSingleTapConfirmed(MotionEvent paramMotionEvent);
  }
  
  public static abstract interface OnGestureListener
  {
    public abstract boolean onDown(MotionEvent paramMotionEvent);
    
    public abstract boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2);
    
    public abstract void onLongPress(MotionEvent paramMotionEvent);
    
    public abstract boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2);
    
    public abstract void onShowPress(MotionEvent paramMotionEvent);
    
    public abstract boolean onSingleTapUp(MotionEvent paramMotionEvent);
  }
  
  public static class SimpleOnGestureListener
    implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, GestureDetector.OnContextClickListener
  {
    public boolean onContextClick(MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public boolean onDoubleTap(MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public boolean onDoubleTapEvent(MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public boolean onDown(MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
    {
      return false;
    }
    
    public void onLongPress(MotionEvent paramMotionEvent) {}
    
    public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
    {
      return false;
    }
    
    public void onShowPress(MotionEvent paramMotionEvent) {}
    
    public boolean onSingleTapConfirmed(MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public boolean onSingleTapUp(MotionEvent paramMotionEvent)
    {
      return false;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/GestureDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */