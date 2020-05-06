package android.view.animation;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.android.internal.R.styleable;
import dalvik.system.CloseGuard;

public abstract class Animation
  implements Cloneable
{
  public static final int ABSOLUTE = 0;
  public static final int INFINITE = -1;
  public static final int RELATIVE_TO_PARENT = 2;
  public static final int RELATIVE_TO_SELF = 1;
  public static final int RESTART = 1;
  public static final int REVERSE = 2;
  public static final int START_ON_FIRST_FRAME = -1;
  public static final int ZORDER_BOTTOM = -1;
  public static final int ZORDER_NORMAL = 0;
  public static final int ZORDER_TOP = 1;
  private final CloseGuard guard = CloseGuard.get();
  private int mBackgroundColor;
  boolean mCycleFlip = false;
  long mDuration;
  boolean mEnded = false;
  boolean mFillAfter = false;
  boolean mFillBefore = true;
  boolean mFillEnabled = false;
  private boolean mHasRoundedCorners;
  boolean mInitialized = false;
  Interpolator mInterpolator;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=117519981L)
  private AnimationListener mListener;
  private Handler mListenerHandler;
  private boolean mMore = true;
  private Runnable mOnEnd;
  private Runnable mOnRepeat;
  private Runnable mOnStart;
  private boolean mOneMoreTime = true;
  @UnsupportedAppUsage
  RectF mPreviousRegion = new RectF();
  @UnsupportedAppUsage
  Transformation mPreviousTransformation = new Transformation();
  @UnsupportedAppUsage
  RectF mRegion = new RectF();
  int mRepeatCount = 0;
  int mRepeatMode = 1;
  int mRepeated = 0;
  private float mScaleFactor = 1.0F;
  private boolean mShowWallpaper;
  long mStartOffset;
  long mStartTime = -1L;
  boolean mStarted = false;
  @UnsupportedAppUsage
  Transformation mTransformation = new Transformation();
  private int mZAdjustment;
  
  public Animation()
  {
    ensureInterpolator();
  }
  
  public Animation(Context paramContext, AttributeSet paramAttributeSet)
  {
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Animation);
    setDuration(paramAttributeSet.getInt(2, 0));
    setStartOffset(paramAttributeSet.getInt(5, 0));
    setFillEnabled(paramAttributeSet.getBoolean(9, this.mFillEnabled));
    setFillBefore(paramAttributeSet.getBoolean(3, this.mFillBefore));
    setFillAfter(paramAttributeSet.getBoolean(4, this.mFillAfter));
    setRepeatCount(paramAttributeSet.getInt(6, this.mRepeatCount));
    setRepeatMode(paramAttributeSet.getInt(7, 1));
    setZAdjustment(paramAttributeSet.getInt(8, 0));
    setBackgroundColor(paramAttributeSet.getInt(0, 0));
    setDetachWallpaper(paramAttributeSet.getBoolean(10, false));
    setShowWallpaper(paramAttributeSet.getBoolean(12, false));
    setHasRoundedCorners(paramAttributeSet.getBoolean(11, false));
    int i = paramAttributeSet.getResourceId(1, 0);
    paramAttributeSet.recycle();
    if (i > 0) {
      setInterpolator(paramContext, i);
    }
    ensureInterpolator();
  }
  
  private void fireAnimationEnd()
  {
    if (hasAnimationListener())
    {
      Handler localHandler = this.mListenerHandler;
      if (localHandler == null) {
        dispatchAnimationEnd();
      } else {
        localHandler.postAtFrontOfQueue(this.mOnEnd);
      }
    }
  }
  
  private void fireAnimationRepeat()
  {
    if (hasAnimationListener())
    {
      Handler localHandler = this.mListenerHandler;
      if (localHandler == null) {
        dispatchAnimationRepeat();
      } else {
        localHandler.postAtFrontOfQueue(this.mOnRepeat);
      }
    }
  }
  
  private void fireAnimationStart()
  {
    if (hasAnimationListener())
    {
      Handler localHandler = this.mListenerHandler;
      if (localHandler == null) {
        dispatchAnimationStart();
      } else {
        localHandler.postAtFrontOfQueue(this.mOnStart);
      }
    }
  }
  
  private boolean hasAnimationListener()
  {
    boolean bool;
    if (this.mListener != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean isCanceled()
  {
    boolean bool;
    if (this.mStartTime == Long.MIN_VALUE) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  protected void applyTransformation(float paramFloat, Transformation paramTransformation) {}
  
  public void cancel()
  {
    if ((this.mStarted) && (!this.mEnded))
    {
      fireAnimationEnd();
      this.mEnded = true;
      this.guard.close();
    }
    this.mStartTime = Long.MIN_VALUE;
    this.mOneMoreTime = false;
    this.mMore = false;
  }
  
  protected Animation clone()
    throws CloneNotSupportedException
  {
    Animation localAnimation = (Animation)super.clone();
    localAnimation.mPreviousRegion = new RectF();
    localAnimation.mRegion = new RectF();
    localAnimation.mTransformation = new Transformation();
    localAnimation.mPreviousTransformation = new Transformation();
    return localAnimation;
  }
  
  public long computeDurationHint()
  {
    return (getStartOffset() + getDuration()) * (getRepeatCount() + 1);
  }
  
  @UnsupportedAppUsage
  public void detach()
  {
    if ((this.mStarted) && (!this.mEnded))
    {
      this.mEnded = true;
      this.guard.close();
      fireAnimationEnd();
    }
  }
  
  void dispatchAnimationEnd()
  {
    AnimationListener localAnimationListener = this.mListener;
    if (localAnimationListener != null) {
      localAnimationListener.onAnimationEnd(this);
    }
  }
  
  void dispatchAnimationRepeat()
  {
    AnimationListener localAnimationListener = this.mListener;
    if (localAnimationListener != null) {
      localAnimationListener.onAnimationRepeat(this);
    }
  }
  
  void dispatchAnimationStart()
  {
    AnimationListener localAnimationListener = this.mListener;
    if (localAnimationListener != null) {
      localAnimationListener.onAnimationStart(this);
    }
  }
  
  protected void ensureInterpolator()
  {
    if (this.mInterpolator == null) {
      this.mInterpolator = new AccelerateDecelerateInterpolator();
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.guard != null) {
        this.guard.warnIfOpen();
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public int getBackgroundColor()
  {
    return this.mBackgroundColor;
  }
  
  public boolean getDetachWallpaper()
  {
    return true;
  }
  
  public long getDuration()
  {
    return this.mDuration;
  }
  
  public boolean getFillAfter()
  {
    return this.mFillAfter;
  }
  
  public boolean getFillBefore()
  {
    return this.mFillBefore;
  }
  
  public Interpolator getInterpolator()
  {
    return this.mInterpolator;
  }
  
  @UnsupportedAppUsage
  public void getInvalidateRegion(int paramInt1, int paramInt2, int paramInt3, int paramInt4, RectF paramRectF, Transformation paramTransformation)
  {
    Object localObject = this.mRegion;
    RectF localRectF = this.mPreviousRegion;
    paramRectF.set(paramInt1, paramInt2, paramInt3, paramInt4);
    paramTransformation.getMatrix().mapRect(paramRectF);
    paramRectF.inset(-1.0F, -1.0F);
    ((RectF)localObject).set(paramRectF);
    paramRectF.union(localRectF);
    localRectF.set((RectF)localObject);
    paramRectF = this.mTransformation;
    localObject = this.mPreviousTransformation;
    paramRectF.set(paramTransformation);
    paramTransformation.set((Transformation)localObject);
    ((Transformation)localObject).set(paramRectF);
  }
  
  public int getRepeatCount()
  {
    return this.mRepeatCount;
  }
  
  public int getRepeatMode()
  {
    return this.mRepeatMode;
  }
  
  protected float getScaleFactor()
  {
    return this.mScaleFactor;
  }
  
  public boolean getShowWallpaper()
  {
    return this.mShowWallpaper;
  }
  
  public long getStartOffset()
  {
    return this.mStartOffset;
  }
  
  public long getStartTime()
  {
    return this.mStartTime;
  }
  
  public boolean getTransformation(long paramLong, Transformation paramTransformation)
  {
    if (this.mStartTime == -1L) {
      this.mStartTime = paramLong;
    }
    long l1 = getStartOffset();
    long l2 = this.mDuration;
    if (l2 != 0L) {
      f1 = (float)(paramLong - (this.mStartTime + l1)) / (float)l2;
    } else if (paramLong < this.mStartTime) {
      f1 = 0.0F;
    } else {
      f1 = 1.0F;
    }
    int i;
    if ((f1 < 1.0F) && (!isCanceled())) {
      i = 0;
    } else {
      i = 1;
    }
    boolean bool;
    if (i == 0) {
      bool = true;
    } else {
      bool = false;
    }
    this.mMore = bool;
    float f2 = f1;
    if (!this.mFillEnabled) {
      f2 = Math.max(Math.min(f1, 1.0F), 0.0F);
    }
    if (((f2 < 0.0F) && (!this.mFillBefore)) || ((f2 > 1.0F) && (!this.mFillAfter))) {
      break label263;
    }
    if (!this.mStarted)
    {
      fireAnimationStart();
      this.mStarted = true;
      if (NoImagePreloadHolder.USE_CLOSEGUARD) {
        this.guard.open("cancel or detach or getTransformation");
      }
    }
    float f1 = f2;
    if (this.mFillEnabled) {
      f1 = Math.max(Math.min(f2, 1.0F), 0.0F);
    }
    f2 = f1;
    if (this.mCycleFlip) {
      f2 = 1.0F - f1;
    }
    applyTransformation(this.mInterpolator.getInterpolation(f2), paramTransformation);
    label263:
    if (i != 0) {
      if ((this.mRepeatCount != this.mRepeated) && (!isCanceled()))
      {
        if (this.mRepeatCount > 0) {
          this.mRepeated += 1;
        }
        if (this.mRepeatMode == 2) {
          this.mCycleFlip ^= true;
        }
        this.mStartTime = -1L;
        this.mMore = true;
        fireAnimationRepeat();
      }
      else if (!this.mEnded)
      {
        this.mEnded = true;
        this.guard.close();
        fireAnimationEnd();
      }
    }
    if ((!this.mMore) && (this.mOneMoreTime))
    {
      this.mOneMoreTime = false;
      return true;
    }
    return this.mMore;
  }
  
  public boolean getTransformation(long paramLong, Transformation paramTransformation, float paramFloat)
  {
    this.mScaleFactor = paramFloat;
    return getTransformation(paramLong, paramTransformation);
  }
  
  public int getZAdjustment()
  {
    return this.mZAdjustment;
  }
  
  public boolean hasAlpha()
  {
    return false;
  }
  
  public boolean hasEnded()
  {
    return this.mEnded;
  }
  
  public boolean hasRoundedCorners()
  {
    return this.mHasRoundedCorners;
  }
  
  public boolean hasStarted()
  {
    return this.mStarted;
  }
  
  public void initialize(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    reset();
    this.mInitialized = true;
  }
  
  @UnsupportedAppUsage
  public void initializeInvalidateRegion(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Object localObject = this.mPreviousRegion;
    ((RectF)localObject).set(paramInt1, paramInt2, paramInt3, paramInt4);
    ((RectF)localObject).inset(-1.0F, -1.0F);
    if (this.mFillBefore)
    {
      localObject = this.mPreviousTransformation;
      applyTransformation(this.mInterpolator.getInterpolation(0.0F), (Transformation)localObject);
    }
  }
  
  public boolean isFillEnabled()
  {
    return this.mFillEnabled;
  }
  
  public boolean isInitialized()
  {
    return this.mInitialized;
  }
  
  public void reset()
  {
    this.mPreviousRegion.setEmpty();
    this.mPreviousTransformation.clear();
    this.mInitialized = false;
    this.mCycleFlip = false;
    this.mRepeated = 0;
    this.mMore = true;
    this.mOneMoreTime = true;
    this.mListenerHandler = null;
  }
  
  protected float resolveSize(int paramInt1, float paramFloat, int paramInt2, int paramInt3)
  {
    if (paramInt1 != 0)
    {
      if (paramInt1 != 1)
      {
        if (paramInt1 != 2) {
          return paramFloat;
        }
        return paramInt3 * paramFloat;
      }
      return paramInt2 * paramFloat;
    }
    return paramFloat;
  }
  
  public void restrictDuration(long paramLong)
  {
    long l1 = this.mStartOffset;
    if (l1 > paramLong)
    {
      this.mStartOffset = paramLong;
      this.mDuration = 0L;
      this.mRepeatCount = 0;
      return;
    }
    long l2 = this.mDuration + l1;
    long l3 = l2;
    if (l2 > paramLong)
    {
      this.mDuration = (paramLong - l1);
      l3 = paramLong;
    }
    if (this.mDuration <= 0L)
    {
      this.mDuration = 0L;
      this.mRepeatCount = 0;
      return;
    }
    int i = this.mRepeatCount;
    if ((i < 0) || (i > paramLong) || (i * l3 > paramLong))
    {
      this.mRepeatCount = ((int)(paramLong / l3) - 1);
      if (this.mRepeatCount < 0) {
        this.mRepeatCount = 0;
      }
    }
  }
  
  public void scaleCurrentDuration(float paramFloat)
  {
    this.mDuration = (((float)this.mDuration * paramFloat));
    this.mStartOffset = (((float)this.mStartOffset * paramFloat));
  }
  
  public void setAnimationListener(AnimationListener paramAnimationListener)
  {
    this.mListener = paramAnimationListener;
  }
  
  public void setBackgroundColor(int paramInt)
  {
    this.mBackgroundColor = paramInt;
  }
  
  public void setDetachWallpaper(boolean paramBoolean) {}
  
  public void setDuration(long paramLong)
  {
    if (paramLong >= 0L)
    {
      this.mDuration = paramLong;
      return;
    }
    throw new IllegalArgumentException("Animation duration cannot be negative");
  }
  
  public void setFillAfter(boolean paramBoolean)
  {
    this.mFillAfter = paramBoolean;
  }
  
  public void setFillBefore(boolean paramBoolean)
  {
    this.mFillBefore = paramBoolean;
  }
  
  public void setFillEnabled(boolean paramBoolean)
  {
    this.mFillEnabled = paramBoolean;
  }
  
  public void setHasRoundedCorners(boolean paramBoolean)
  {
    this.mHasRoundedCorners = paramBoolean;
  }
  
  public void setInterpolator(Context paramContext, int paramInt)
  {
    setInterpolator(AnimationUtils.loadInterpolator(paramContext, paramInt));
  }
  
  public void setInterpolator(Interpolator paramInterpolator)
  {
    this.mInterpolator = paramInterpolator;
  }
  
  public void setListenerHandler(Handler paramHandler)
  {
    if (this.mListenerHandler == null)
    {
      this.mOnStart = new Runnable()
      {
        public void run()
        {
          Animation.this.dispatchAnimationStart();
        }
      };
      this.mOnRepeat = new Runnable()
      {
        public void run()
        {
          Animation.this.dispatchAnimationRepeat();
        }
      };
      this.mOnEnd = new Runnable()
      {
        public void run()
        {
          Animation.this.dispatchAnimationEnd();
        }
      };
    }
    this.mListenerHandler = paramHandler;
  }
  
  public void setRepeatCount(int paramInt)
  {
    int i = paramInt;
    if (paramInt < 0) {
      i = -1;
    }
    this.mRepeatCount = i;
  }
  
  public void setRepeatMode(int paramInt)
  {
    this.mRepeatMode = paramInt;
  }
  
  public void setShowWallpaper(boolean paramBoolean)
  {
    this.mShowWallpaper = paramBoolean;
  }
  
  public void setStartOffset(long paramLong)
  {
    this.mStartOffset = paramLong;
  }
  
  public void setStartTime(long paramLong)
  {
    this.mStartTime = paramLong;
    this.mEnded = false;
    this.mStarted = false;
    this.mCycleFlip = false;
    this.mRepeated = 0;
    this.mMore = true;
  }
  
  public void setZAdjustment(int paramInt)
  {
    this.mZAdjustment = paramInt;
  }
  
  public void start()
  {
    setStartTime(-1L);
  }
  
  public void startNow()
  {
    setStartTime(AnimationUtils.currentAnimationTimeMillis());
  }
  
  public boolean willChangeBounds()
  {
    return true;
  }
  
  public boolean willChangeTransformationMatrix()
  {
    return true;
  }
  
  public static abstract interface AnimationListener
  {
    public abstract void onAnimationEnd(Animation paramAnimation);
    
    public abstract void onAnimationRepeat(Animation paramAnimation);
    
    public abstract void onAnimationStart(Animation paramAnimation);
  }
  
  protected static class Description
  {
    public int type;
    public float value;
    
    static Description parseValue(TypedValue paramTypedValue)
    {
      Description localDescription = new Description();
      if (paramTypedValue == null)
      {
        localDescription.type = 0;
        localDescription.value = 0.0F;
      }
      else
      {
        if (paramTypedValue.type == 6)
        {
          int i = paramTypedValue.data;
          int j = 1;
          if ((i & 0xF) == 1) {
            j = 2;
          }
          localDescription.type = j;
          localDescription.value = TypedValue.complexToFloat(paramTypedValue.data);
          return localDescription;
        }
        if (paramTypedValue.type == 4)
        {
          localDescription.type = 0;
          localDescription.value = paramTypedValue.getFloat();
          return localDescription;
        }
        if ((paramTypedValue.type >= 16) && (paramTypedValue.type <= 31))
        {
          localDescription.type = 0;
          localDescription.value = paramTypedValue.data;
          return localDescription;
        }
      }
      localDescription.type = 0;
      localDescription.value = 0.0F;
      return localDescription;
    }
  }
  
  private static class NoImagePreloadHolder
  {
    public static final boolean USE_CLOSEGUARD = SystemProperties.getBoolean("log.closeguard.Animation", false);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/Animation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */