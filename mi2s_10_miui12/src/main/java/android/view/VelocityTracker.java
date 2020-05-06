package android.view;

import android.annotation.UnsupportedAppUsage;
import android.util.Pools.SynchronizedPool;

public final class VelocityTracker
{
  private static final int ACTIVE_POINTER_ID = -1;
  private static final Pools.SynchronizedPool<VelocityTracker> sPool = new Pools.SynchronizedPool(2);
  private long mPtr;
  private final String mStrategy;
  
  private VelocityTracker(String paramString)
  {
    this.mPtr = nativeInitialize(paramString);
    this.mStrategy = paramString;
  }
  
  private static native void nativeAddMovement(long paramLong, MotionEvent paramMotionEvent);
  
  private static native void nativeClear(long paramLong);
  
  private static native void nativeComputeCurrentVelocity(long paramLong, int paramInt, float paramFloat);
  
  private static native void nativeDispose(long paramLong);
  
  private static native boolean nativeGetEstimator(long paramLong, int paramInt, Estimator paramEstimator);
  
  private static native float nativeGetXVelocity(long paramLong, int paramInt);
  
  private static native float nativeGetYVelocity(long paramLong, int paramInt);
  
  private static native long nativeInitialize(String paramString);
  
  public static VelocityTracker obtain()
  {
    VelocityTracker localVelocityTracker = (VelocityTracker)sPool.acquire();
    if (localVelocityTracker == null) {
      localVelocityTracker = new VelocityTracker(null);
    }
    return localVelocityTracker;
  }
  
  @UnsupportedAppUsage
  public static VelocityTracker obtain(String paramString)
  {
    if (paramString == null) {
      return obtain();
    }
    return new VelocityTracker(paramString);
  }
  
  public void addMovement(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent != null)
    {
      nativeAddMovement(this.mPtr, paramMotionEvent);
      return;
    }
    throw new IllegalArgumentException("event must not be null");
  }
  
  public void clear()
  {
    nativeClear(this.mPtr);
  }
  
  public void computeCurrentVelocity(int paramInt)
  {
    nativeComputeCurrentVelocity(this.mPtr, paramInt, Float.MAX_VALUE);
  }
  
  public void computeCurrentVelocity(int paramInt, float paramFloat)
  {
    nativeComputeCurrentVelocity(this.mPtr, paramInt, paramFloat);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mPtr != 0L)
      {
        nativeDispose(this.mPtr);
        this.mPtr = 0L;
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public boolean getEstimator(int paramInt, Estimator paramEstimator)
  {
    if (paramEstimator != null) {
      return nativeGetEstimator(this.mPtr, paramInt, paramEstimator);
    }
    throw new IllegalArgumentException("outEstimator must not be null");
  }
  
  public float getXVelocity()
  {
    return nativeGetXVelocity(this.mPtr, -1);
  }
  
  public float getXVelocity(int paramInt)
  {
    return nativeGetXVelocity(this.mPtr, paramInt);
  }
  
  public float getYVelocity()
  {
    return nativeGetYVelocity(this.mPtr, -1);
  }
  
  public float getYVelocity(int paramInt)
  {
    return nativeGetYVelocity(this.mPtr, paramInt);
  }
  
  public void recycle()
  {
    if (this.mStrategy == null)
    {
      clear();
      sPool.release(this);
    }
  }
  
  public static final class Estimator
  {
    private static final int MAX_DEGREE = 4;
    @UnsupportedAppUsage
    public float confidence;
    @UnsupportedAppUsage
    public int degree;
    @UnsupportedAppUsage
    public final float[] xCoeff = new float[5];
    @UnsupportedAppUsage
    public final float[] yCoeff = new float[5];
    
    private float estimate(float paramFloat, float[] paramArrayOfFloat)
    {
      float f1 = 0.0F;
      float f2 = 1.0F;
      for (int i = 0; i <= this.degree; i++)
      {
        f1 += paramArrayOfFloat[i] * f2;
        f2 *= paramFloat;
      }
      return f1;
    }
    
    public float estimateX(float paramFloat)
    {
      return estimate(paramFloat, this.xCoeff);
    }
    
    public float estimateY(float paramFloat)
    {
      return estimate(paramFloat, this.yCoeff);
    }
    
    public float getXCoeff(int paramInt)
    {
      float f;
      if (paramInt <= this.degree) {
        f = this.xCoeff[paramInt];
      } else {
        f = 0.0F;
      }
      return f;
    }
    
    public float getYCoeff(int paramInt)
    {
      float f;
      if (paramInt <= this.degree) {
        f = this.yCoeff[paramInt];
      } else {
        f = 0.0F;
      }
      return f;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/VelocityTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */