package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources;
import android.util.BoostFramework;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public class OverScroller
{
  private static final int DEFAULT_DURATION = 250;
  private static final int FLING_MODE = 1;
  static boolean SCROLL_BOOST_SS_ENABLE = false;
  private static final int SCROLL_MODE = 0;
  private final boolean mFlywheel;
  BoostFramework mGetProp = null;
  @UnsupportedAppUsage
  private Interpolator mInterpolator;
  private int mMode;
  private final SplineOverScroller mScrollerX;
  @UnsupportedAppUsage
  private final SplineOverScroller mScrollerY;
  
  public OverScroller(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OverScroller(Context paramContext, Interpolator paramInterpolator)
  {
    this(paramContext, paramInterpolator, true);
  }
  
  @Deprecated
  public OverScroller(Context paramContext, Interpolator paramInterpolator, float paramFloat1, float paramFloat2)
  {
    this(paramContext, paramInterpolator, true);
  }
  
  @Deprecated
  public OverScroller(Context paramContext, Interpolator paramInterpolator, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    this(paramContext, paramInterpolator, paramBoolean);
  }
  
  @UnsupportedAppUsage
  public OverScroller(Context paramContext, Interpolator paramInterpolator, boolean paramBoolean)
  {
    if (paramInterpolator == null) {
      this.mInterpolator = new Scroller.ViscousFluidInterpolator();
    } else {
      this.mInterpolator = paramInterpolator;
    }
    this.mFlywheel = paramBoolean;
    this.mScrollerX = new SplineOverScroller(paramContext);
    this.mScrollerY = new SplineOverScroller(paramContext);
    this.mGetProp = new BoostFramework();
    paramContext = this.mGetProp;
    if (paramContext != null) {
      SCROLL_BOOST_SS_ENABLE = Boolean.parseBoolean(paramContext.perfGetProp("vendor.perf.gestureflingboost.enable", "false"));
    }
  }
  
  public void abortAnimation()
  {
    this.mScrollerX.finish();
    this.mScrollerY.finish();
  }
  
  public boolean computeScrollOffset()
  {
    if (isFinished()) {
      return false;
    }
    int i = this.mMode;
    if (i != 0)
    {
      if (i == 1)
      {
        if ((!this.mScrollerX.mFinished) && (!this.mScrollerX.update()) && (!this.mScrollerX.continueWhenFinished())) {
          this.mScrollerX.finish();
        }
        if ((!this.mScrollerY.mFinished) && (!this.mScrollerY.update()) && (!this.mScrollerY.continueWhenFinished())) {
          this.mScrollerY.finish();
        }
      }
    }
    else
    {
      long l = AnimationUtils.currentAnimationTimeMillis() - this.mScrollerX.mStartTime;
      i = this.mScrollerX.mDuration;
      if (l < i)
      {
        float f = this.mInterpolator.getInterpolation((float)l / i);
        this.mScrollerX.updateScroll(f);
        this.mScrollerY.updateScroll(f);
      }
      else
      {
        abortAnimation();
      }
    }
    return true;
  }
  
  @Deprecated
  @UnsupportedAppUsage
  public void extendDuration(int paramInt)
  {
    this.mScrollerX.extendDuration(paramInt);
    this.mScrollerY.extendDuration(paramInt);
  }
  
  public void fling(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
  {
    fling(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, 0, 0);
  }
  
  public void fling(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10)
  {
    if ((this.mFlywheel) && (!isFinished()))
    {
      float f1 = this.mScrollerX.mCurrVelocity;
      float f2 = this.mScrollerY.mCurrVelocity;
      if ((Math.signum(paramInt3) == Math.signum(f1)) && (Math.signum(paramInt4) == Math.signum(f2)))
      {
        paramInt3 = (int)(paramInt3 + f1);
        paramInt4 = (int)(paramInt4 + f2);
      }
    }
    this.mMode = 1;
    this.mScrollerX.fling(paramInt1, paramInt3, paramInt5, paramInt6, paramInt9);
    this.mScrollerY.fling(paramInt2, paramInt4, paramInt7, paramInt8, paramInt10);
  }
  
  public final void forceFinished(boolean paramBoolean)
  {
    SplineOverScroller.access$002(this.mScrollerX, SplineOverScroller.access$002(this.mScrollerY, paramBoolean));
  }
  
  public float getCurrVelocity()
  {
    return (float)Math.hypot(this.mScrollerX.mCurrVelocity, this.mScrollerY.mCurrVelocity);
  }
  
  public final int getCurrX()
  {
    return this.mScrollerX.mCurrentPosition;
  }
  
  public final int getCurrY()
  {
    return this.mScrollerY.mCurrentPosition;
  }
  
  @Deprecated
  public final int getDuration()
  {
    return Math.max(this.mScrollerX.mDuration, this.mScrollerY.mDuration);
  }
  
  public final int getFinalX()
  {
    return this.mScrollerX.mFinal;
  }
  
  public final int getFinalY()
  {
    return this.mScrollerY.mFinal;
  }
  
  public final int getStartX()
  {
    return this.mScrollerX.mStart;
  }
  
  public final int getStartY()
  {
    return this.mScrollerY.mStart;
  }
  
  public final boolean isFinished()
  {
    boolean bool;
    if ((this.mScrollerX.mFinished) && (this.mScrollerY.mFinished)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isOverScrolled()
  {
    boolean bool;
    if (((!this.mScrollerX.mFinished) && (this.mScrollerX.mState != 0)) || ((!this.mScrollerY.mFinished) && (this.mScrollerY.mState != 0))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  public boolean isScrollingInDirection(float paramFloat1, float paramFloat2)
  {
    int i = this.mScrollerX.mFinal;
    int j = this.mScrollerX.mStart;
    int k = this.mScrollerY.mFinal;
    int m = this.mScrollerY.mStart;
    boolean bool;
    if ((!isFinished()) && (Math.signum(paramFloat1) == Math.signum(i - j)) && (Math.signum(paramFloat2) == Math.signum(k - m))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void notifyHorizontalEdgeReached(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mScrollerX.notifyEdgeReached(paramInt1, paramInt2, paramInt3);
  }
  
  public void notifyVerticalEdgeReached(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mScrollerY.notifyEdgeReached(paramInt1, paramInt2, paramInt3);
  }
  
  @Deprecated
  public void setFinalX(int paramInt)
  {
    this.mScrollerX.setFinalPosition(paramInt);
  }
  
  @Deprecated
  public void setFinalY(int paramInt)
  {
    this.mScrollerY.setFinalPosition(paramInt);
  }
  
  public final void setFriction(float paramFloat)
  {
    this.mScrollerX.setFriction(paramFloat);
    this.mScrollerY.setFriction(paramFloat);
  }
  
  @UnsupportedAppUsage
  void setInterpolator(Interpolator paramInterpolator)
  {
    if (paramInterpolator == null) {
      this.mInterpolator = new Scroller.ViscousFluidInterpolator();
    } else {
      this.mInterpolator = paramInterpolator;
    }
  }
  
  public boolean springBack(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    boolean bool1 = true;
    this.mMode = 1;
    boolean bool2 = this.mScrollerX.springback(paramInt1, paramInt3, paramInt4);
    boolean bool3 = this.mScrollerY.springback(paramInt2, paramInt5, paramInt6);
    boolean bool4 = bool1;
    if (!bool2) {
      if (bool3) {
        bool4 = bool1;
      } else {
        bool4 = false;
      }
    }
    return bool4;
  }
  
  public void startScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    startScroll(paramInt1, paramInt2, paramInt3, paramInt4, 250);
  }
  
  public void startScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    this.mMode = 0;
    this.mScrollerX.startScroll(paramInt1, paramInt3, paramInt5);
    this.mScrollerY.startScroll(paramInt2, paramInt4, paramInt5);
  }
  
  public int timePassed()
  {
    return (int)(AnimationUtils.currentAnimationTimeMillis() - Math.min(this.mScrollerX.mStartTime, this.mScrollerY.mStartTime));
  }
  
  static class SplineOverScroller
  {
    private static final int BALLISTIC = 2;
    private static final int CUBIC = 1;
    private static float DECELERATION_RATE = (float)(Math.log(0.78D) / Math.log(0.9D));
    private static final float END_TENSION = 1.0F;
    private static final float GRAVITY = 2000.0F;
    private static final float INFLEXION = 0.35F;
    private static final int NB_SAMPLES = 100;
    private static final float P1 = 0.175F;
    private static final float P2 = 0.35000002F;
    private static final int SPLINE = 0;
    private static final float[] SPLINE_POSITION = new float[101];
    private static final float[] SPLINE_TIME = new float[101];
    private static final float START_TENSION = 0.5F;
    static final int STATE_BALLISTIC = 2;
    static final int STATE_CUBIC = 1;
    static final int STATE_SPLINE = 0;
    private Context mContext;
    @UnsupportedAppUsage
    private float mCurrVelocity;
    private int mCurrentPosition;
    private float mDeceleration;
    private int mDuration;
    private int mFinal;
    private boolean mFinished;
    private float mFlingFriction = ViewConfiguration.getScrollFriction();
    private boolean mIsPerfLockAcquired = false;
    private int mOver;
    private BoostFramework mPerf = null;
    private float mPhysicalCoeff;
    private int mSplineDistance;
    private int mSplineDuration;
    private int mStart;
    private long mStartTime;
    private int mState = 0;
    private int mVelocity;
    
    static
    {
      float f1 = 0.0F;
      float f2 = 0.0F;
      int i = 0;
      if (i < 100)
      {
        float f3 = i / 100.0F;
        float f4 = 1.0F;
        for (;;)
        {
          float f5 = (f4 - f1) / 2.0F + f1;
          float f6 = f5 * 3.0F * (1.0F - f5);
          float f7 = ((1.0F - f5) * 0.175F + f5 * 0.35000002F) * f6 + f5 * f5 * f5;
          if (Math.abs(f7 - f3) < 1.0E-5D)
          {
            SPLINE_POSITION[i] = (((1.0F - f5) * 0.5F + f5) * f6 + f5 * f5 * f5);
            f4 = 1.0F;
            for (;;)
            {
              f5 = (f4 - f2) / 2.0F + f2;
              f6 = f5 * 3.0F * (1.0F - f5);
              f7 = ((1.0F - f5) * 0.5F + f5) * f6 + f5 * f5 * f5;
              if (Math.abs(f7 - f3) < 1.0E-5D)
              {
                SPLINE_TIME[i] = (f6 * ((1.0F - f5) * 0.175F + 0.35000002F * f5) + f5 * f5 * f5);
                i++;
                break;
              }
              if (f7 > f3) {
                f4 = f5;
              } else {
                f2 = f5;
              }
            }
          }
          if (f7 > f3) {
            f4 = f5;
          } else {
            f1 = f5;
          }
        }
      }
      float[] arrayOfFloat = SPLINE_POSITION;
      SPLINE_TIME[100] = 1.0F;
      arrayOfFloat[100] = 1.0F;
    }
    
    SplineOverScroller(Context paramContext)
    {
      this.mContext = paramContext;
      this.mFinished = true;
      this.mPhysicalCoeff = (386.0878F * (paramContext.getResources().getDisplayMetrics().density * 160.0F) * 0.84F);
      if ((!OverScroller.SCROLL_BOOST_SS_ENABLE) && (this.mPerf == null)) {
        this.mPerf = new BoostFramework(paramContext);
      }
    }
    
    private void adjustDuration(int paramInt1, int paramInt2, int paramInt3)
    {
      float f1 = Math.abs((paramInt3 - paramInt1) / (paramInt2 - paramInt1));
      paramInt1 = (int)(f1 * 100.0F);
      if (paramInt1 < 100)
      {
        float f2 = paramInt1 / 100.0F;
        float f3 = (paramInt1 + 1) / 100.0F;
        float[] arrayOfFloat = SPLINE_TIME;
        float f4 = arrayOfFloat[paramInt1];
        float f5 = arrayOfFloat[(paramInt1 + 1)];
        f2 = (f1 - f2) / (f3 - f2);
        this.mDuration = ((int)(this.mDuration * (f2 * (f5 - f4) + f4)));
      }
    }
    
    private void fitOnBounceCurve(int paramInt1, int paramInt2, int paramInt3)
    {
      float f1 = -paramInt3;
      float f2 = this.mDeceleration;
      f1 /= f2;
      f2 = (float)Math.sqrt((paramInt3 * paramInt3 / 2.0F / Math.abs(f2) + Math.abs(paramInt2 - paramInt1)) * 2.0D / Math.abs(this.mDeceleration));
      this.mStartTime -= (int)((f2 - f1) * 1000.0F);
      this.mStart = paramInt2;
      this.mCurrentPosition = paramInt2;
      this.mVelocity = ((int)(-this.mDeceleration * f2));
    }
    
    private static float getDeceleration(int paramInt)
    {
      float f;
      if (paramInt > 0) {
        f = -2000.0F;
      } else {
        f = 2000.0F;
      }
      return f;
    }
    
    private double getSplineDeceleration(int paramInt)
    {
      return Math.log(Math.abs(paramInt) * 0.35F / (this.mFlingFriction * this.mPhysicalCoeff));
    }
    
    private double getSplineFlingDistance(int paramInt)
    {
      double d1 = getSplineDeceleration(paramInt);
      float f = DECELERATION_RATE;
      double d2 = f;
      return this.mFlingFriction * this.mPhysicalCoeff * Math.exp(f / (d2 - 1.0D) * d1);
    }
    
    private int getSplineFlingDuration(int paramInt)
    {
      return (int)(Math.exp(getSplineDeceleration(paramInt) / (DECELERATION_RATE - 1.0D)) * 1000.0D);
    }
    
    private void onEdgeReached()
    {
      int i = this.mVelocity;
      float f1 = i * i;
      float f2 = f1 / (Math.abs(this.mDeceleration) * 2.0F);
      float f3 = Math.signum(this.mVelocity);
      i = this.mOver;
      float f4 = f2;
      if (f2 > i)
      {
        this.mDeceleration = (-f3 * f1 / (i * 2.0F));
        f4 = i;
      }
      this.mOver = ((int)f4);
      this.mState = 2;
      i = this.mStart;
      if (this.mVelocity <= 0) {
        f4 = -f4;
      }
      this.mFinal = (i + (int)f4);
      this.mDuration = (-(int)(this.mVelocity * 1000.0F / this.mDeceleration));
    }
    
    private void startAfterEdge(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      int i = 1;
      if ((paramInt1 > paramInt2) && (paramInt1 < paramInt3))
      {
        Log.e("OverScroller", "startAfterEdge called from a valid position");
        this.mFinished = true;
        return;
      }
      int j;
      if (paramInt1 > paramInt3) {
        j = 1;
      } else {
        j = 0;
      }
      int k;
      if (j != 0) {
        k = paramInt3;
      } else {
        k = paramInt2;
      }
      int m = paramInt1 - k;
      if (m * paramInt4 < 0) {
        i = 0;
      }
      if (i != 0)
      {
        startBounceAfterEdge(paramInt1, k, paramInt4);
      }
      else if (getSplineFlingDistance(paramInt4) > Math.abs(m))
      {
        if (j == 0) {
          paramInt2 = paramInt1;
        }
        if (j != 0) {
          paramInt3 = paramInt1;
        }
        fling(paramInt1, paramInt4, paramInt2, paramInt3, this.mOver);
      }
      else
      {
        startSpringback(paramInt1, k, paramInt4);
      }
    }
    
    private void startBounceAfterEdge(int paramInt1, int paramInt2, int paramInt3)
    {
      int i;
      if (paramInt3 == 0) {
        i = paramInt1 - paramInt2;
      } else {
        i = paramInt3;
      }
      this.mDeceleration = getDeceleration(i);
      fitOnBounceCurve(paramInt1, paramInt2, paramInt3);
      onEdgeReached();
    }
    
    private void startSpringback(int paramInt1, int paramInt2, int paramInt3)
    {
      this.mFinished = false;
      this.mState = 1;
      this.mStart = paramInt1;
      this.mCurrentPosition = paramInt1;
      this.mFinal = paramInt2;
      paramInt1 -= paramInt2;
      this.mDeceleration = getDeceleration(paramInt1);
      this.mVelocity = (-paramInt1);
      this.mOver = Math.abs(paramInt1);
      this.mDuration = ((int)(Math.sqrt(paramInt1 * -2.0D / this.mDeceleration) * 1000.0D));
    }
    
    boolean continueWhenFinished()
    {
      int i = this.mState;
      if (i != 0)
      {
        if (i != 1)
        {
          if (i == 2)
          {
            this.mStartTime += this.mDuration;
            startSpringback(this.mFinal, this.mStart, 0);
          }
        }
        else {
          return false;
        }
      }
      else
      {
        if (this.mDuration >= this.mSplineDuration) {
          break label125;
        }
        i = this.mFinal;
        this.mStart = i;
        this.mCurrentPosition = i;
        this.mVelocity = ((int)this.mCurrVelocity);
        this.mDeceleration = getDeceleration(this.mVelocity);
        this.mStartTime += this.mDuration;
        onEdgeReached();
      }
      update();
      return true;
      label125:
      return false;
    }
    
    void extendDuration(int paramInt)
    {
      this.mDuration = ((int)(AnimationUtils.currentAnimationTimeMillis() - this.mStartTime) + paramInt);
      this.mFinished = false;
    }
    
    void finish()
    {
      if ((!OverScroller.SCROLL_BOOST_SS_ENABLE) && (this.mIsPerfLockAcquired))
      {
        BoostFramework localBoostFramework = this.mPerf;
        if (localBoostFramework != null)
        {
          localBoostFramework.perfLockRelease();
          this.mIsPerfLockAcquired = false;
        }
      }
      this.mCurrentPosition = this.mFinal;
      this.mFinished = true;
    }
    
    void fling(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      this.mOver = paramInt5;
      this.mFinished = false;
      this.mVelocity = paramInt2;
      this.mCurrVelocity = paramInt2;
      this.mSplineDuration = 0;
      this.mDuration = 0;
      this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
      this.mStart = paramInt1;
      this.mCurrentPosition = paramInt1;
      if ((!OverScroller.SCROLL_BOOST_SS_ENABLE) && (this.mIsPerfLockAcquired))
      {
        BoostFramework localBoostFramework = this.mPerf;
        if (localBoostFramework != null)
        {
          localBoostFramework.perfLockRelease();
          this.mIsPerfLockAcquired = false;
        }
      }
      if ((paramInt1 <= paramInt4) && (paramInt1 >= paramInt3))
      {
        this.mState = 0;
        double d = 0.0D;
        if (paramInt2 != 0)
        {
          paramInt5 = getSplineFlingDuration(paramInt2);
          this.mSplineDuration = paramInt5;
          this.mDuration = paramInt5;
          d = getSplineFlingDistance(paramInt2);
        }
        this.mSplineDistance = ((int)(Math.signum(paramInt2) * d));
        this.mFinal = (this.mSplineDistance + paramInt1);
        paramInt1 = this.mFinal;
        if (paramInt1 < paramInt3)
        {
          adjustDuration(this.mStart, paramInt1, paramInt3);
          this.mFinal = paramInt3;
        }
        paramInt1 = this.mFinal;
        if (paramInt1 > paramInt4)
        {
          adjustDuration(this.mStart, paramInt1, paramInt4);
          this.mFinal = paramInt4;
        }
        return;
      }
      startAfterEdge(paramInt1, paramInt3, paramInt4, paramInt2);
    }
    
    final float getCurrVelocity()
    {
      return this.mCurrVelocity;
    }
    
    final int getCurrentPosition()
    {
      return this.mCurrentPosition;
    }
    
    final int getDuration()
    {
      return this.mDuration;
    }
    
    final int getFinal()
    {
      return this.mFinal;
    }
    
    final int getStart()
    {
      return this.mStart;
    }
    
    final long getStartTime()
    {
      return this.mStartTime;
    }
    
    final int getState()
    {
      return this.mState;
    }
    
    final boolean isFinished()
    {
      return this.mFinished;
    }
    
    void notifyEdgeReached(int paramInt1, int paramInt2, int paramInt3)
    {
      if (this.mState == 0)
      {
        this.mOver = paramInt3;
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        startAfterEdge(paramInt1, paramInt2, paramInt2, (int)this.mCurrVelocity);
      }
    }
    
    final void setCurrVelocity(float paramFloat)
    {
      this.mCurrVelocity = paramFloat;
    }
    
    final void setCurrentPosition(int paramInt)
    {
      this.mCurrentPosition = paramInt;
    }
    
    final void setDuration(int paramInt)
    {
      this.mDuration = paramInt;
    }
    
    final void setFinal(int paramInt)
    {
      this.mFinal = paramInt;
    }
    
    void setFinalPosition(int paramInt)
    {
      this.mFinal = paramInt;
      this.mFinished = false;
    }
    
    final void setFinished(boolean paramBoolean)
    {
      this.mFinished = paramBoolean;
    }
    
    void setFriction(float paramFloat)
    {
      this.mFlingFriction = paramFloat;
    }
    
    final void setStart(int paramInt)
    {
      this.mStart = paramInt;
    }
    
    final void setStartTime(long paramLong)
    {
      this.mStartTime = paramLong;
    }
    
    final void setState(int paramInt)
    {
      this.mState = paramInt;
    }
    
    boolean springback(int paramInt1, int paramInt2, int paramInt3)
    {
      this.mFinished = true;
      this.mFinal = paramInt1;
      this.mStart = paramInt1;
      this.mCurrentPosition = paramInt1;
      this.mVelocity = 0;
      this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
      this.mDuration = 0;
      if (paramInt1 < paramInt2) {
        startSpringback(paramInt1, paramInt2, 0);
      } else if (paramInt1 > paramInt3) {
        startSpringback(paramInt1, paramInt3, 0);
      }
      return true ^ this.mFinished;
    }
    
    void startScroll(int paramInt1, int paramInt2, int paramInt3)
    {
      this.mFinished = false;
      this.mStart = paramInt1;
      this.mCurrentPosition = paramInt1;
      this.mFinal = (paramInt1 + paramInt2);
      this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
      this.mDuration = paramInt3;
      this.mDeceleration = 0.0F;
      this.mVelocity = 0;
    }
    
    boolean update()
    {
      long l = AnimationUtils.currentAnimationTimeMillis() - this.mStartTime;
      boolean bool = false;
      if (l == 0L)
      {
        if (this.mDuration > 0) {
          bool = true;
        }
        return bool;
      }
      if (l > this.mDuration) {
        return false;
      }
      Object localObject;
      if ((!OverScroller.SCROLL_BOOST_SS_ENABLE) && (this.mPerf != null) && (!this.mIsPerfLockAcquired))
      {
        localObject = this.mContext.getPackageName();
        this.mIsPerfLockAcquired = true;
        this.mPerf.perfHint(4224, (String)localObject, this.mDuration, 1);
      }
      double d = 0.0D;
      int i = this.mState;
      float f1;
      float f2;
      float f3;
      if (i != 0)
      {
        if (i != 1)
        {
          if (i == 2)
          {
            f1 = (float)l / 1000.0F;
            i = this.mVelocity;
            f2 = i;
            f3 = this.mDeceleration;
            this.mCurrVelocity = (f2 + f3 * f1);
            d = i * f1 + f3 * f1 * f1 / 2.0F;
          }
        }
        else
        {
          f1 = (float)l / this.mDuration;
          f3 = f1 * f1;
          f2 = Math.signum(this.mVelocity);
          i = this.mOver;
          d = i * f2 * (3.0F * f3 - 2.0F * f1 * f3);
          this.mCurrVelocity = (i * f2 * 6.0F * (-f1 + f3));
        }
      }
      else
      {
        f1 = (float)l / this.mSplineDuration;
        i = (int)(f1 * 100.0F);
        f2 = 1.0F;
        f3 = 0.0F;
        if (i < 100)
        {
          f2 = i / 100.0F;
          f3 = (i + 1) / 100.0F;
          localObject = SPLINE_POSITION;
          float f4 = localObject[i];
          f3 = (localObject[(i + 1)] - f4) / (f3 - f2);
          f2 = f4 + (f1 - f2) * f3;
        }
        i = this.mSplineDistance;
        d = i * f2;
        this.mCurrVelocity = (i * f3 / this.mSplineDuration * 1000.0F);
      }
      this.mCurrentPosition = (this.mStart + (int)Math.round(d));
      return true;
    }
    
    void updateScroll(float paramFloat)
    {
      int i = this.mStart;
      this.mCurrentPosition = (i + Math.round((this.mFinal - i) * paramFloat));
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/OverScroller.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */