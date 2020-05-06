package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public class Scroller
{
  @UnsupportedAppUsage
  private static float DECELERATION_RATE = (float)(Math.log(0.78D) / Math.log(0.9D));
  private static final int DEFAULT_DURATION = 250;
  private static final float END_TENSION = 1.0F;
  private static final int FLING_MODE = 1;
  @UnsupportedAppUsage
  private static final float INFLEXION = 0.35F;
  private static final int NB_SAMPLES = 100;
  private static final float P1 = 0.175F;
  private static final float P2 = 0.35000002F;
  private static final int SCROLL_MODE = 0;
  private static final float[] SPLINE_POSITION = new float[101];
  private static final float[] SPLINE_TIME = new float[101];
  private static final float START_TENSION = 0.5F;
  private Context mContext;
  private float mCurrVelocity;
  private int mCurrX;
  private int mCurrY;
  @UnsupportedAppUsage
  private float mDeceleration;
  private float mDeltaX;
  private float mDeltaY;
  private int mDistance;
  @UnsupportedAppUsage
  private int mDuration;
  private float mDurationReciprocal;
  private int mFinalX;
  private int mFinalY;
  private boolean mFinished = true;
  private float mFlingFriction = ViewConfiguration.getScrollFriction();
  private boolean mFlywheel;
  @UnsupportedAppUsage
  private final Interpolator mInterpolator;
  private int mMaxX;
  private int mMaxY;
  private int mMinX;
  private int mMinY;
  private int mMode;
  @UnsupportedAppUsage
  private float mPhysicalCoeff;
  private final float mPpi;
  private long mStartTime;
  private int mStartX;
  private int mStartY;
  private float mVelocity;
  
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
            f7 = f5 * 3.0F * (1.0F - f5);
            f6 = ((1.0F - f5) * 0.5F + f5) * f7 + f5 * f5 * f5;
            if (Math.abs(f6 - f3) < 1.0E-5D)
            {
              SPLINE_TIME[i] = (f7 * ((1.0F - f5) * 0.175F + 0.35000002F * f5) + f5 * f5 * f5);
              i++;
              break;
            }
            if (f6 > f3) {
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
  
  public Scroller(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public Scroller(Context paramContext, Interpolator paramInterpolator)
  {
    this(paramContext, paramInterpolator, bool);
  }
  
  public Scroller(Context paramContext, Interpolator paramInterpolator, boolean paramBoolean)
  {
    this.mContext = paramContext;
    if (paramInterpolator == null) {
      this.mInterpolator = new ViscousFluidInterpolator();
    } else {
      this.mInterpolator = paramInterpolator;
    }
    this.mPpi = (paramContext.getResources().getDisplayMetrics().density * 160.0F);
    this.mDeceleration = computeDeceleration(ViewConfiguration.getScrollFriction());
    this.mFlywheel = paramBoolean;
    this.mPhysicalCoeff = computeDeceleration(0.84F);
  }
  
  private float computeDeceleration(float paramFloat)
  {
    return this.mPpi * 386.0878F * paramFloat;
  }
  
  private double getSplineDeceleration(float paramFloat)
  {
    return Math.log(Math.abs(paramFloat) * 0.35F / (this.mFlingFriction * this.mPhysicalCoeff));
  }
  
  private double getSplineFlingDistance(float paramFloat)
  {
    double d1 = getSplineDeceleration(paramFloat);
    paramFloat = DECELERATION_RATE;
    double d2 = paramFloat;
    return this.mFlingFriction * this.mPhysicalCoeff * Math.exp(paramFloat / (d2 - 1.0D) * d1);
  }
  
  private int getSplineFlingDuration(float paramFloat)
  {
    return (int)(Math.exp(getSplineDeceleration(paramFloat) / (DECELERATION_RATE - 1.0D)) * 1000.0D);
  }
  
  public void abortAnimation()
  {
    this.mCurrX = this.mFinalX;
    this.mCurrY = this.mFinalY;
    this.mFinished = true;
  }
  
  public boolean computeScrollOffset()
  {
    if (this.mFinished) {
      return false;
    }
    int i = (int)(AnimationUtils.currentAnimationTimeMillis() - this.mStartTime);
    int j = this.mDuration;
    if (i < j)
    {
      int k = this.mMode;
      float f3;
      if (k != 0)
      {
        if (k == 1)
        {
          float f1 = i / j;
          i = (int)(f1 * 100.0F);
          float f2 = 1.0F;
          f3 = 0.0F;
          if (i < 100)
          {
            float f4 = i / 100.0F;
            f3 = (i + 1) / 100.0F;
            float[] arrayOfFloat = SPLINE_POSITION;
            f2 = arrayOfFloat[i];
            f3 = (arrayOfFloat[(i + 1)] - f2) / (f3 - f4);
            f2 += (f1 - f4) * f3;
          }
          this.mCurrVelocity = (this.mDistance * f3 / this.mDuration * 1000.0F);
          i = this.mStartX;
          this.mCurrX = (i + Math.round((this.mFinalX - i) * f2));
          this.mCurrX = Math.min(this.mCurrX, this.mMaxX);
          this.mCurrX = Math.max(this.mCurrX, this.mMinX);
          i = this.mStartY;
          this.mCurrY = (i + Math.round((this.mFinalY - i) * f2));
          this.mCurrY = Math.min(this.mCurrY, this.mMaxY);
          this.mCurrY = Math.max(this.mCurrY, this.mMinY);
          if ((this.mCurrX == this.mFinalX) && (this.mCurrY == this.mFinalY)) {
            this.mFinished = true;
          }
        }
      }
      else
      {
        f3 = this.mInterpolator.getInterpolation(i * this.mDurationReciprocal);
        this.mCurrX = (this.mStartX + Math.round(this.mDeltaX * f3));
        this.mCurrY = (this.mStartY + Math.round(this.mDeltaY * f3));
      }
    }
    else
    {
      this.mCurrX = this.mFinalX;
      this.mCurrY = this.mFinalY;
      this.mFinished = true;
    }
    return true;
  }
  
  public void extendDuration(int paramInt)
  {
    this.mDuration = (timePassed() + paramInt);
    this.mDurationReciprocal = (1.0F / this.mDuration);
    this.mFinished = false;
  }
  
  public void fling(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
  {
    int i = paramInt4;
    int j = paramInt3;
    paramInt4 = i;
    float f1;
    if (this.mFlywheel)
    {
      j = paramInt3;
      paramInt4 = i;
      if (!this.mFinished)
      {
        f1 = getCurrVelocity();
        float f2 = this.mFinalX - this.mStartX;
        f3 = this.mFinalY - this.mStartY;
        f4 = (float)Math.hypot(f2, f3);
        f2 /= f4;
        f3 /= f4;
        f4 = f2 * f1;
        f1 = f3 * f1;
        j = paramInt3;
        paramInt4 = i;
        if (Math.signum(paramInt3) == Math.signum(f4))
        {
          j = paramInt3;
          paramInt4 = i;
          if (Math.signum(i) == Math.signum(f1))
          {
            j = (int)(paramInt3 + f4);
            paramInt4 = (int)(i + f1);
          }
        }
      }
    }
    this.mMode = 1;
    this.mFinished = false;
    float f4 = (float)Math.hypot(j, paramInt4);
    this.mVelocity = f4;
    this.mDuration = getSplineFlingDuration(f4);
    this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
    this.mStartX = paramInt1;
    this.mStartY = paramInt2;
    float f3 = 1.0F;
    if (f4 == 0.0F) {
      f1 = 1.0F;
    } else {
      f1 = j / f4;
    }
    if (f4 != 0.0F) {
      f3 = paramInt4 / f4;
    }
    double d = getSplineFlingDistance(f4);
    this.mDistance = ((int)(Math.signum(f4) * d));
    this.mMinX = paramInt5;
    this.mMaxX = paramInt6;
    this.mMinY = paramInt7;
    this.mMaxY = paramInt8;
    this.mFinalX = ((int)Math.round(f1 * d) + paramInt1);
    this.mFinalX = Math.min(this.mFinalX, this.mMaxX);
    this.mFinalX = Math.max(this.mFinalX, this.mMinX);
    this.mFinalY = ((int)Math.round(f3 * d) + paramInt2);
    this.mFinalY = Math.min(this.mFinalY, this.mMaxY);
    this.mFinalY = Math.max(this.mFinalY, this.mMinY);
  }
  
  public final void forceFinished(boolean paramBoolean)
  {
    this.mFinished = paramBoolean;
  }
  
  public float getCurrVelocity()
  {
    float f;
    if (this.mMode == 1) {
      f = this.mCurrVelocity;
    } else {
      f = this.mVelocity - this.mDeceleration * timePassed() / 2000.0F;
    }
    return f;
  }
  
  public final int getCurrX()
  {
    return this.mCurrX;
  }
  
  public final int getCurrY()
  {
    return this.mCurrY;
  }
  
  public final int getDuration()
  {
    return this.mDuration;
  }
  
  public final int getFinalX()
  {
    return this.mFinalX;
  }
  
  public final int getFinalY()
  {
    return this.mFinalY;
  }
  
  public final int getStartX()
  {
    return this.mStartX;
  }
  
  public final int getStartY()
  {
    return this.mStartY;
  }
  
  public final boolean isFinished()
  {
    return this.mFinished;
  }
  
  public boolean isScrollingInDirection(float paramFloat1, float paramFloat2)
  {
    boolean bool;
    if ((!this.mFinished) && (Math.signum(paramFloat1) == Math.signum(this.mFinalX - this.mStartX)) && (Math.signum(paramFloat2) == Math.signum(this.mFinalY - this.mStartY))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void setFinalX(int paramInt)
  {
    this.mFinalX = paramInt;
    this.mDeltaX = (this.mFinalX - this.mStartX);
    this.mFinished = false;
  }
  
  public void setFinalY(int paramInt)
  {
    this.mFinalY = paramInt;
    this.mDeltaY = (this.mFinalY - this.mStartY);
    this.mFinished = false;
  }
  
  public final void setFriction(float paramFloat)
  {
    this.mDeceleration = computeDeceleration(paramFloat);
    this.mFlingFriction = paramFloat;
  }
  
  public void startScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    startScroll(paramInt1, paramInt2, paramInt3, paramInt4, 250);
  }
  
  public void startScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    this.mMode = 0;
    this.mFinished = false;
    this.mDuration = paramInt5;
    this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
    this.mStartX = paramInt1;
    this.mStartY = paramInt2;
    this.mFinalX = (paramInt1 + paramInt3);
    this.mFinalY = (paramInt2 + paramInt4);
    this.mDeltaX = paramInt3;
    this.mDeltaY = paramInt4;
    this.mDurationReciprocal = (1.0F / this.mDuration);
  }
  
  public int timePassed()
  {
    return (int)(AnimationUtils.currentAnimationTimeMillis() - this.mStartTime);
  }
  
  static class ViscousFluidInterpolator
    implements Interpolator
  {
    private static final float VISCOUS_FLUID_NORMALIZE = 1.0F / viscousFluid(1.0F);
    private static final float VISCOUS_FLUID_OFFSET = 1.0F - VISCOUS_FLUID_NORMALIZE * viscousFluid(1.0F);
    private static final float VISCOUS_FLUID_SCALE = 8.0F;
    
    private static float viscousFluid(float paramFloat)
    {
      paramFloat *= 8.0F;
      if (paramFloat < 1.0F) {
        paramFloat -= 1.0F - (float)Math.exp(-paramFloat);
      } else {
        paramFloat = 0.36787945F + (1.0F - 0.36787945F) * (1.0F - (float)Math.exp(1.0F - paramFloat));
      }
      return paramFloat;
    }
    
    public float getInterpolation(float paramFloat)
    {
      paramFloat = VISCOUS_FLUID_NORMALIZE * viscousFluid(paramFloat);
      if (paramFloat > 0.0F) {
        return VISCOUS_FLUID_OFFSET + paramFloat;
      }
      return paramFloat;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/Scroller.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */