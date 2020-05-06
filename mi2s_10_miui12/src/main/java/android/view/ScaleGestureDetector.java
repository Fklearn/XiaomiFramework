package android.view;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;

public class ScaleGestureDetector
{
  private static final int ANCHORED_SCALE_MODE_DOUBLE_TAP = 1;
  private static final int ANCHORED_SCALE_MODE_NONE = 0;
  private static final int ANCHORED_SCALE_MODE_STYLUS = 2;
  private static final float SCALE_FACTOR = 0.5F;
  private static final String TAG = "ScaleGestureDetector";
  private static final long TOUCH_STABILIZE_TIME = 128L;
  private int mAnchoredScaleMode = 0;
  private float mAnchoredScaleStartX;
  private float mAnchoredScaleStartY;
  private final Context mContext;
  private float mCurrSpan;
  private float mCurrSpanX;
  private float mCurrSpanY;
  private long mCurrTime;
  private boolean mEventBeforeOrAboveStartingGestureEvent;
  private float mFocusX;
  private float mFocusY;
  private GestureDetector mGestureDetector;
  private final Handler mHandler;
  private boolean mInProgress;
  private float mInitialSpan;
  private final InputEventConsistencyVerifier mInputEventConsistencyVerifier;
  @UnsupportedAppUsage
  private final OnScaleGestureListener mListener;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123768938L)
  private int mMinSpan;
  private float mPrevSpan;
  private float mPrevSpanX;
  private float mPrevSpanY;
  private long mPrevTime;
  private boolean mQuickScaleEnabled;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123768938L)
  private int mSpanSlop;
  private boolean mStylusScaleEnabled;
  
  public ScaleGestureDetector(Context paramContext, OnScaleGestureListener paramOnScaleGestureListener)
  {
    this(paramContext, paramOnScaleGestureListener, null);
  }
  
  public ScaleGestureDetector(Context paramContext, OnScaleGestureListener paramOnScaleGestureListener, Handler paramHandler)
  {
    InputEventConsistencyVerifier localInputEventConsistencyVerifier;
    if (InputEventConsistencyVerifier.isInstrumentationEnabled()) {
      localInputEventConsistencyVerifier = new InputEventConsistencyVerifier(this, 0);
    } else {
      localInputEventConsistencyVerifier = null;
    }
    this.mInputEventConsistencyVerifier = localInputEventConsistencyVerifier;
    this.mContext = paramContext;
    this.mListener = paramOnScaleGestureListener;
    paramOnScaleGestureListener = ViewConfiguration.get(paramContext);
    this.mSpanSlop = (paramOnScaleGestureListener.getScaledTouchSlop() * 2);
    this.mMinSpan = paramOnScaleGestureListener.getScaledMinimumScalingSpan();
    this.mHandler = paramHandler;
    int i = paramContext.getApplicationInfo().targetSdkVersion;
    if (i > 18) {
      setQuickScaleEnabled(true);
    }
    if (i > 22) {
      setStylusScaleEnabled(true);
    }
  }
  
  private boolean inAnchoredScaleMode()
  {
    boolean bool;
    if (this.mAnchoredScaleMode != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public float getCurrentSpan()
  {
    return this.mCurrSpan;
  }
  
  public float getCurrentSpanX()
  {
    return this.mCurrSpanX;
  }
  
  public float getCurrentSpanY()
  {
    return this.mCurrSpanY;
  }
  
  public long getEventTime()
  {
    return this.mCurrTime;
  }
  
  public float getFocusX()
  {
    return this.mFocusX;
  }
  
  public float getFocusY()
  {
    return this.mFocusY;
  }
  
  public float getPreviousSpan()
  {
    return this.mPrevSpan;
  }
  
  public float getPreviousSpanX()
  {
    return this.mPrevSpanX;
  }
  
  public float getPreviousSpanY()
  {
    return this.mPrevSpanY;
  }
  
  public float getScaleFactor()
  {
    boolean bool = inAnchoredScaleMode();
    float f1 = 1.0F;
    if (bool)
    {
      int i;
      if (((this.mEventBeforeOrAboveStartingGestureEvent) && (this.mCurrSpan < this.mPrevSpan)) || ((!this.mEventBeforeOrAboveStartingGestureEvent) && (this.mCurrSpan > this.mPrevSpan))) {
        i = 1;
      } else {
        i = 0;
      }
      f2 = Math.abs(1.0F - this.mCurrSpan / this.mPrevSpan) * 0.5F;
      if (this.mPrevSpan > this.mSpanSlop) {
        if (i != 0) {
          f1 = 1.0F + f2;
        } else {
          f1 = 1.0F - f2;
        }
      }
      return f1;
    }
    float f2 = this.mPrevSpan;
    if (f2 > 0.0F) {
      f1 = this.mCurrSpan / f2;
    }
    return f1;
  }
  
  public long getTimeDelta()
  {
    return this.mCurrTime - this.mPrevTime;
  }
  
  public boolean isInProgress()
  {
    return this.mInProgress;
  }
  
  public boolean isQuickScaleEnabled()
  {
    return this.mQuickScaleEnabled;
  }
  
  public boolean isStylusScaleEnabled()
  {
    return this.mStylusScaleEnabled;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    InputEventConsistencyVerifier localInputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
    if (localInputEventConsistencyVerifier != null) {
      localInputEventConsistencyVerifier.onTouchEvent(paramMotionEvent, 0);
    }
    this.mCurrTime = paramMotionEvent.getEventTime();
    int i = paramMotionEvent.getActionMasked();
    if (this.mQuickScaleEnabled) {
      this.mGestureDetector.onTouchEvent(paramMotionEvent);
    }
    int j = paramMotionEvent.getPointerCount();
    int k;
    if ((paramMotionEvent.getButtonState() & 0x20) != 0) {
      k = 1;
    } else {
      k = 0;
    }
    int m;
    if ((this.mAnchoredScaleMode == 2) && (k == 0)) {
      m = 1;
    } else {
      m = 0;
    }
    int n;
    if ((i != 1) && (i != 3) && (m == 0)) {
      n = 0;
    } else {
      n = 1;
    }
    if ((i == 0) || (n != 0))
    {
      if (this.mInProgress)
      {
        this.mListener.onScaleEnd(this);
        this.mInProgress = false;
        this.mInitialSpan = 0.0F;
        this.mAnchoredScaleMode = 0;
      }
      else if ((inAnchoredScaleMode()) && (n != 0))
      {
        this.mInProgress = false;
        this.mInitialSpan = 0.0F;
        this.mAnchoredScaleMode = 0;
      }
      if (n != 0) {
        return true;
      }
    }
    if ((!this.mInProgress) && (this.mStylusScaleEnabled) && (!inAnchoredScaleMode()) && (n == 0) && (k != 0))
    {
      this.mAnchoredScaleStartX = paramMotionEvent.getX();
      this.mAnchoredScaleStartY = paramMotionEvent.getY();
      this.mAnchoredScaleMode = 2;
      this.mInitialSpan = 0.0F;
    }
    if ((i != 0) && (i != 6) && (i != 5) && (m == 0)) {
      k = 0;
    } else {
      k = 1;
    }
    if (i == 6) {
      n = 1;
    } else {
      n = 0;
    }
    if (n != 0) {
      m = paramMotionEvent.getActionIndex();
    } else {
      m = -1;
    }
    float f1 = 0.0F;
    float f2 = 0.0F;
    if (n != 0) {
      n = j - 1;
    } else {
      n = j;
    }
    if (inAnchoredScaleMode())
    {
      f1 = this.mAnchoredScaleStartX;
      f2 = this.mAnchoredScaleStartY;
      if (paramMotionEvent.getY() < f2) {
        this.mEventBeforeOrAboveStartingGestureEvent = true;
      } else {
        this.mEventBeforeOrAboveStartingGestureEvent = false;
      }
    }
    else
    {
      for (i1 = 0; i1 < j; i1++) {
        if (m != i1)
        {
          f1 += paramMotionEvent.getX(i1);
          f2 += paramMotionEvent.getY(i1);
        }
      }
      f1 /= n;
      f2 /= n;
    }
    float f3 = 0.0F;
    int i1 = 0;
    float f4 = 0.0F;
    while (i1 < j)
    {
      if (m != i1)
      {
        f3 += Math.abs(paramMotionEvent.getX(i1) - f1);
        f4 += Math.abs(paramMotionEvent.getY(i1) - f2);
      }
      i1++;
    }
    f3 /= n;
    f4 /= n;
    float f5 = f3 * 2.0F;
    f3 = f4 * 2.0F;
    if (inAnchoredScaleMode()) {
      f4 = f3;
    } else {
      f4 = (float)Math.hypot(f5, f3);
    }
    boolean bool = this.mInProgress;
    this.mFocusX = f1;
    this.mFocusY = f2;
    if ((!inAnchoredScaleMode()) && (this.mInProgress) && ((f4 < this.mMinSpan) || (k != 0)))
    {
      this.mListener.onScaleEnd(this);
      this.mInProgress = false;
      this.mInitialSpan = f4;
    }
    if (k != 0)
    {
      this.mCurrSpanX = f5;
      this.mPrevSpanX = f5;
      this.mCurrSpanY = f3;
      this.mPrevSpanY = f3;
      this.mCurrSpan = f4;
      this.mPrevSpan = f4;
      this.mInitialSpan = f4;
    }
    if (inAnchoredScaleMode()) {
      k = this.mSpanSlop;
    } else {
      k = this.mMinSpan;
    }
    if ((!this.mInProgress) && (f4 >= k))
    {
      if ((!bool) && (Math.abs(f4 - this.mInitialSpan) <= this.mSpanSlop)) {
        break label815;
      }
      this.mCurrSpanX = f5;
      this.mPrevSpanX = f5;
      this.mCurrSpanY = f3;
      this.mPrevSpanY = f3;
      this.mCurrSpan = f4;
      this.mPrevSpan = f4;
      this.mPrevTime = this.mCurrTime;
      this.mInProgress = this.mListener.onScaleBegin(this);
    }
    label815:
    if (i == 2)
    {
      this.mCurrSpanX = f5;
      this.mCurrSpanY = f3;
      this.mCurrSpan = f4;
      bool = true;
      if (this.mInProgress) {
        bool = this.mListener.onScale(this);
      }
      if (bool)
      {
        this.mPrevSpanX = this.mCurrSpanX;
        this.mPrevSpanY = this.mCurrSpanY;
        this.mPrevSpan = this.mCurrSpan;
        this.mPrevTime = this.mCurrTime;
      }
      else {}
    }
    return true;
  }
  
  public void setQuickScaleEnabled(boolean paramBoolean)
  {
    this.mQuickScaleEnabled = paramBoolean;
    if ((this.mQuickScaleEnabled) && (this.mGestureDetector == null))
    {
      GestureDetector.SimpleOnGestureListener local1 = new GestureDetector.SimpleOnGestureListener()
      {
        public boolean onDoubleTap(MotionEvent paramAnonymousMotionEvent)
        {
          ScaleGestureDetector.access$002(ScaleGestureDetector.this, paramAnonymousMotionEvent.getX());
          ScaleGestureDetector.access$102(ScaleGestureDetector.this, paramAnonymousMotionEvent.getY());
          ScaleGestureDetector.access$202(ScaleGestureDetector.this, 1);
          return true;
        }
      };
      this.mGestureDetector = new GestureDetector(this.mContext, local1, this.mHandler);
    }
  }
  
  public void setStylusScaleEnabled(boolean paramBoolean)
  {
    this.mStylusScaleEnabled = paramBoolean;
  }
  
  public static abstract interface OnScaleGestureListener
  {
    public abstract boolean onScale(ScaleGestureDetector paramScaleGestureDetector);
    
    public abstract boolean onScaleBegin(ScaleGestureDetector paramScaleGestureDetector);
    
    public abstract void onScaleEnd(ScaleGestureDetector paramScaleGestureDetector);
  }
  
  public static class SimpleOnScaleGestureListener
    implements ScaleGestureDetector.OnScaleGestureListener
  {
    public boolean onScale(ScaleGestureDetector paramScaleGestureDetector)
    {
      return false;
    }
    
    public boolean onScaleBegin(ScaleGestureDetector paramScaleGestureDetector)
    {
      return true;
    }
    
    public void onScaleEnd(ScaleGestureDetector paramScaleGestureDetector) {}
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ScaleGestureDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */