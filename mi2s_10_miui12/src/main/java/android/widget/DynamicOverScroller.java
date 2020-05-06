package android.widget;

import android.content.Context;
import android.view.animation.AnimationUtils;
import com.miui.internal.dynamicanimation.animation.DynamicAnimation;
import com.miui.internal.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener;
import com.miui.internal.dynamicanimation.animation.FlingAnimation;
import com.miui.internal.dynamicanimation.animation.FloatValueHolder;
import com.miui.internal.dynamicanimation.animation.SpringAnimation;
import com.miui.internal.dynamicanimation.animation.SpringForce;
import java.lang.reflect.Field;

class DynamicOverScroller
  extends OverScroller
{
  private static final float FLING_FRICTION = 0.4761905F;
  private static final float FLING_VELOCITY_SCALE = 1.05F;
  private static final float MAX_SPRING_INITIAL_VELOCITY = 4000.0F;
  private static final float MINIMAL_VISIBLE_CHANGE = 0.5F;
  private static final float SPRING_DAMPING_RATIO = 0.99F;
  private static final float SPRING_STIFFNESS = 200.0F;
  private static final Field mScrollerY = reflectField("mScrollerY");
  
  DynamicOverScroller(Context paramContext)
  {
    super(paramContext);
    replaceScroller(mScrollerY, new DynamicScroller(paramContext));
  }
  
  private static Field reflectField(String paramString)
  {
    try
    {
      paramString = OverScroller.class.getDeclaredField(paramString);
      paramString.setAccessible(true);
      return paramString;
    }
    catch (NoSuchFieldException paramString)
    {
      throw new RuntimeException(paramString);
    }
  }
  
  private void replaceScroller(Field paramField, OverScroller.SplineOverScroller paramSplineOverScroller)
  {
    try
    {
      paramField.set(this, paramSplineOverScroller);
    }
    catch (IllegalAccessException paramField)
    {
      paramField.printStackTrace();
    }
  }
  
  static class DynamicScroller
    extends OverScroller.SplineOverScroller
  {
    private FlingAnimation mFlingAnimation;
    private DynamicOverScroller.OverScrollHandler mHandler;
    private SpringAnimation mSpringAnimation = new SpringAnimation(this.mValue);
    private FloatValueHolder mValue = new FloatValueHolder();
    
    DynamicScroller(Context paramContext)
    {
      super();
      this.mSpringAnimation.setSpring(new SpringForce());
      this.mSpringAnimation.setMinimumVisibleChange(0.5F);
      this.mSpringAnimation.getSpring().setDampingRatio(0.99F);
      this.mSpringAnimation.getSpring().setStiffness(200.0F);
      this.mFlingAnimation = new FlingAnimation(this.mValue);
      this.mFlingAnimation.setMinimumVisibleChange(0.5F);
      this.mFlingAnimation.setFriction(0.4761905F);
    }
    
    private void doFling(int paramInt1, int paramInt2, final int paramInt3, final int paramInt4, final int paramInt5)
    {
      this.mFlingAnimation.setStartValue(0.0F);
      this.mFlingAnimation.setStartVelocity(paramInt2);
      long l = paramInt1 + this.mFlingAnimation.predictNaturalDest();
      if (l > paramInt4)
      {
        i = paramInt4;
        j = (int)this.mFlingAnimation.predictTimeTo(paramInt4 - paramInt1);
      }
      else if (l < paramInt3)
      {
        i = paramInt3;
        j = (int)this.mFlingAnimation.predictTimeTo(paramInt3 - paramInt1);
      }
      else
      {
        i = (int)l;
        j = (int)this.mFlingAnimation.predictDuration();
      }
      setFinished(false);
      setCurrVelocity(paramInt2);
      setStartTime(AnimationUtils.currentAnimationTimeMillis());
      setCurrentPosition(paramInt1);
      setStart(paramInt1);
      setDuration(j);
      setFinal(i);
      setState(0);
      int i = Math.min(paramInt3, paramInt1);
      int j = Math.max(paramInt4, paramInt1);
      this.mHandler = new DynamicOverScroller.OverScrollHandler(this.mFlingAnimation, paramInt1, paramInt2);
      this.mHandler.setOnFinishedListener(new DynamicOverScroller.OverScrollHandler.OnFinishedListener()
      {
        public boolean whenFinished(float paramAnonymousFloat1, float paramAnonymousFloat2)
        {
          OverScrollLogger.debug("fling finished: value(%f), velocity(%f), scroller boundary(%d, %d)", new Object[] { Float.valueOf(paramAnonymousFloat1), Float.valueOf(paramAnonymousFloat2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4) });
          DynamicOverScroller.DynamicScroller.this.mFlingAnimation.setStartValue(DynamicOverScroller.DynamicScroller.this.mHandler.mValue);
          DynamicOverScroller.DynamicScroller.this.mFlingAnimation.setStartVelocity(DynamicOverScroller.DynamicScroller.this.mHandler.mVelocity);
          paramAnonymousFloat2 = DynamicOverScroller.DynamicScroller.this.mFlingAnimation.predictNaturalDest();
          if (((int)paramAnonymousFloat1 != 0) && ((paramAnonymousFloat2 > paramInt4) || (paramAnonymousFloat2 < paramInt3)))
          {
            OverScrollLogger.debug("fling destination beyound boundary, start spring");
            DynamicOverScroller.DynamicScroller.this.resetHandler();
            DynamicOverScroller.DynamicScroller localDynamicScroller = DynamicOverScroller.DynamicScroller.this;
            localDynamicScroller.doSpring(2, localDynamicScroller.getCurrentPosition(), DynamicOverScroller.DynamicScroller.this.getCurrVelocity(), DynamicOverScroller.DynamicScroller.this.getFinal(), paramInt5);
            return true;
          }
          OverScrollLogger.debug("fling finished, no more work.");
          return false;
        }
      });
      this.mHandler.setMinValue(i);
      this.mHandler.setMaxValue(j);
      this.mHandler.start();
    }
    
    private void doSpring(int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4)
    {
      float f = paramFloat;
      if (paramFloat > 4000.0F)
      {
        OverScrollLogger.debug("%f is too fast for spring, slow down", new Object[] { Float.valueOf(paramFloat) });
        f = 4000.0F;
      }
      setFinished(false);
      setCurrVelocity(f);
      setStartTime(AnimationUtils.currentAnimationTimeMillis());
      setCurrentPosition(paramInt2);
      setStart(paramInt2);
      setDuration(Integer.MAX_VALUE);
      setFinal(paramInt3);
      setState(paramInt1);
      this.mHandler = new DynamicOverScroller.OverScrollHandler(this.mSpringAnimation, paramInt2, f);
      this.mSpringAnimation.getSpring().setFinalPosition(this.mHandler.getOffset(paramInt3));
      if (paramInt4 != 0) {
        if (f < 0.0F)
        {
          this.mHandler.setMinValue(paramInt3 - paramInt4);
          this.mHandler.setMaxValue(Math.max(paramInt3, paramInt2));
        }
        else
        {
          this.mHandler.setMinValue(Math.min(paramInt3, paramInt2));
          this.mHandler.setMaxValue(paramInt3 + paramInt4);
        }
      }
      this.mHandler.start();
    }
    
    private void resetHandler()
    {
      if (this.mHandler != null)
      {
        OverScrollLogger.debug("resetting current handler: state(%d), anim(%s), value(%d), velocity(%f)", new Object[] { Integer.valueOf(getState()), this.mHandler.getAnimation().getClass().getSimpleName(), Integer.valueOf(this.mHandler.mValue), Float.valueOf(this.mHandler.mVelocity) });
        this.mHandler.cancel();
        this.mHandler = null;
      }
    }
    
    private void startAfterEdge(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      int i = 0;
      OverScrollLogger.debug("startAfterEdge: start(%d) velocity(%d) boundary(%d, %d) over(%d)", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt4), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt5) });
      if ((paramInt1 > paramInt2) && (paramInt1 < paramInt3))
      {
        setFinished(true);
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
      int m = i;
      if (paramInt4 != 0)
      {
        m = i;
        if (Integer.signum(paramInt1 - k) * paramInt4 >= 0) {
          m = 1;
        }
      }
      if (m != 0)
      {
        OverScrollLogger.debug("spring forward");
        doSpring(2, paramInt1, paramInt4, k, paramInt5);
      }
      else
      {
        this.mFlingAnimation.setStartValue(paramInt1);
        this.mFlingAnimation.setStartVelocity(paramInt4);
        float f = this.mFlingAnimation.predictNaturalDest();
        if (((j != 0) && (f < paramInt3)) || ((j == 0) && (f > paramInt2)))
        {
          OverScrollLogger.debug("fling to content");
          doFling(paramInt1, paramInt4, paramInt2, paramInt3, paramInt5);
        }
        else
        {
          OverScrollLogger.debug("spring backward");
          doSpring(1, paramInt1, paramInt4, k, paramInt5);
        }
      }
    }
    
    boolean continueWhenFinished()
    {
      DynamicOverScroller.OverScrollHandler localOverScrollHandler = this.mHandler;
      if ((localOverScrollHandler != null) && (localOverScrollHandler.continueWhenFinished()))
      {
        OverScrollLogger.debug("checking have more work when finish");
        update();
        return true;
      }
      return false;
    }
    
    void extendDuration(int paramInt)
    {
      super.extendDuration(paramInt);
    }
    
    void finish()
    {
      OverScrollLogger.debug("finish scroller");
      setCurrentPosition(getFinal());
      setFinished(true);
      resetHandler();
    }
    
    void fling(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      paramInt2 = (int)(paramInt2 * 1.05F);
      OverScrollLogger.debug("FLING: start(%d) velocity(%d) boundary(%d, %d) over(%d)", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), Integer.valueOf(paramInt5) });
      resetHandler();
      if (paramInt2 == 0)
      {
        setCurrentPosition(paramInt1);
        setStart(paramInt1);
        setFinal(paramInt1);
        setDuration(0);
        setFinished(true);
        return;
      }
      if ((paramInt1 <= paramInt4) && (paramInt1 >= paramInt3))
      {
        doFling(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
        return;
      }
      startAfterEdge(paramInt1, paramInt3, paramInt4, paramInt2, paramInt5);
    }
    
    void notifyEdgeReached(int paramInt1, int paramInt2, int paramInt3)
    {
      if (getState() == 0)
      {
        if (this.mHandler != null) {
          resetHandler();
        }
        startAfterEdge(paramInt1, paramInt2, paramInt2, (int)getCurrVelocity(), paramInt3);
      }
    }
    
    void setFinalPosition(int paramInt)
    {
      super.setFinalPosition(paramInt);
    }
    
    void setFriction(float paramFloat)
    {
      this.mFlingAnimation.setFriction(paramFloat);
    }
    
    boolean springback(int paramInt1, int paramInt2, int paramInt3)
    {
      OverScrollLogger.debug("SPRING_BACK start(%d) boundary(%d, %d)", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) });
      if (this.mHandler != null) {
        resetHandler();
      }
      if (paramInt1 < paramInt2)
      {
        doSpring(1, paramInt1, 0.0F, paramInt2, 0);
      }
      else if (paramInt1 > paramInt3)
      {
        doSpring(1, paramInt1, 0.0F, paramInt3, 0);
      }
      else
      {
        setCurrentPosition(paramInt1);
        setStart(paramInt1);
        setFinal(paramInt1);
        setDuration(0);
        setFinished(true);
      }
      return isFinished() ^ true;
    }
    
    boolean update()
    {
      DynamicOverScroller.OverScrollHandler localOverScrollHandler = this.mHandler;
      if (localOverScrollHandler == null)
      {
        OverScrollLogger.debug("no handler found, aborting");
        return false;
      }
      boolean bool = localOverScrollHandler.update();
      setCurrentPosition(this.mHandler.mValue);
      setCurrVelocity(this.mHandler.mVelocity);
      if ((getState() == 2) && (Math.signum(this.mHandler.mValue) * Math.signum(this.mHandler.mVelocity) < 0.0F))
      {
        OverScrollLogger.debug("State Changed: BALLISTIC -> CUBIC");
        setState(1);
      }
      return bool ^ true;
    }
  }
  
  private static class OverScrollHandler
  {
    private float mAnimMaxValue;
    private float mAnimMinValue;
    DynamicAnimation<?> mAnimation;
    private long mLastUpdateTime;
    private final int mMaxLegalValue;
    private final int mMinLegalValue;
    private Monitor mMonitor = new Monitor(null);
    private OnFinishedListener mOnFinishedListener;
    int mStartValue;
    int mValue;
    float mVelocity;
    
    OverScrollHandler(DynamicAnimation<?> paramDynamicAnimation, int paramInt, float paramFloat)
    {
      this.mAnimation = paramDynamicAnimation;
      this.mAnimation.setMinValue(-3.4028235E38F);
      this.mAnimation.setMaxValue(Float.MAX_VALUE);
      this.mStartValue = paramInt;
      this.mVelocity = paramFloat;
      int i;
      if (paramInt > 0)
      {
        paramInt = Integer.MIN_VALUE + paramInt;
        i = Integer.MAX_VALUE;
      }
      else if (paramInt < 0)
      {
        int j = Integer.MIN_VALUE;
        i = Integer.MAX_VALUE + paramInt;
        paramInt = j;
      }
      else
      {
        paramInt = Integer.MIN_VALUE;
        i = Integer.MAX_VALUE;
      }
      this.mMinLegalValue = paramInt;
      this.mMaxLegalValue = i;
      this.mAnimation.setStartValue(0.0F);
      this.mAnimation.setStartVelocity(paramFloat);
    }
    
    void cancel()
    {
      this.mLastUpdateTime = 0L;
      this.mAnimation.cancel();
      this.mAnimation.removeUpdateListener(this.mMonitor);
    }
    
    boolean continueWhenFinished()
    {
      OnFinishedListener localOnFinishedListener = this.mOnFinishedListener;
      if (localOnFinishedListener != null) {
        return localOnFinishedListener.whenFinished(this.mValue, this.mVelocity);
      }
      return false;
    }
    
    DynamicAnimation<?> getAnimation()
    {
      return this.mAnimation;
    }
    
    int getOffset(int paramInt)
    {
      return paramInt - this.mStartValue;
    }
    
    void setMaxValue(int paramInt)
    {
      int i = paramInt;
      if (paramInt > this.mMaxLegalValue) {
        i = this.mMaxLegalValue;
      }
      float f = i - this.mStartValue;
      this.mAnimation.setMaxValue(f);
      this.mAnimMaxValue = f;
    }
    
    void setMinValue(int paramInt)
    {
      int i = paramInt;
      if (paramInt < this.mMinLegalValue) {
        i = this.mMinLegalValue;
      }
      float f = i - this.mStartValue;
      this.mAnimation.setMinValue(f);
      this.mAnimMinValue = f;
    }
    
    void setOnFinishedListener(OnFinishedListener paramOnFinishedListener)
    {
      this.mOnFinishedListener = paramOnFinishedListener;
    }
    
    void start()
    {
      this.mAnimation.addUpdateListener(this.mMonitor);
      this.mAnimation.start(true);
      this.mLastUpdateTime = 0L;
    }
    
    boolean update()
    {
      long l1 = this.mLastUpdateTime;
      long l2 = AnimationUtils.currentAnimationTimeMillis();
      if (l2 == l1)
      {
        OverScrollLogger.verbose("update done in this frame, dropping current update request");
        return this.mAnimation.isRunning() ^ true;
      }
      boolean bool = this.mAnimation.doAnimationFrame(l2);
      if (bool)
      {
        OverScrollLogger.verbose("%s finishing value(%d) velocity(%f)", new Object[] { this.mAnimation.getClass().getSimpleName(), Integer.valueOf(this.mValue), Float.valueOf(this.mVelocity) });
        this.mAnimation.removeUpdateListener(this.mMonitor);
        this.mLastUpdateTime = 0L;
      }
      this.mLastUpdateTime = l2;
      return bool;
    }
    
    private class Monitor
      implements DynamicAnimation.OnAnimationUpdateListener
    {
      private Monitor() {}
      
      public void onAnimationUpdate(DynamicAnimation paramDynamicAnimation, float paramFloat1, float paramFloat2)
      {
        DynamicOverScroller.OverScrollHandler localOverScrollHandler = DynamicOverScroller.OverScrollHandler.this;
        localOverScrollHandler.mVelocity = paramFloat2;
        localOverScrollHandler.mValue = (localOverScrollHandler.mStartValue + (int)paramFloat1);
        OverScrollLogger.verbose("%s updating value(%f), velocity(%f), min(%f), max(%f)", new Object[] { paramDynamicAnimation.getClass().getSimpleName(), Float.valueOf(paramFloat1), Float.valueOf(paramFloat2), Float.valueOf(DynamicOverScroller.OverScrollHandler.this.mAnimMinValue), Float.valueOf(DynamicOverScroller.OverScrollHandler.this.mAnimMaxValue) });
      }
    }
    
    static abstract interface OnFinishedListener
    {
      public abstract boolean whenFinished(float paramFloat1, float paramFloat2);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/DynamicOverScroller.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */