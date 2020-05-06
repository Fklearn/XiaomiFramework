package android.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.UnsupportedAppUsage;
import android.graphics.CanvasProperty;
import android.graphics.Paint;
import android.graphics.RecordingCanvas;
import android.graphics.RenderNode;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseIntArray;
import com.android.internal.util.VirtualRefBasePtr;
import com.android.internal.view.animation.FallbackLUTInterpolator;
import com.android.internal.view.animation.HasNativeInterpolator;
import com.android.internal.view.animation.NativeInterpolatorFactory;
import java.util.ArrayList;
import java.util.Objects;

public class RenderNodeAnimator
  extends Animator
{
  public static final int ALPHA = 11;
  public static final int LAST_VALUE = 11;
  public static final int PAINT_ALPHA = 1;
  public static final int PAINT_STROKE_WIDTH = 0;
  public static final int ROTATION = 5;
  public static final int ROTATION_X = 6;
  public static final int ROTATION_Y = 7;
  public static final int SCALE_X = 3;
  public static final int SCALE_Y = 4;
  private static final int STATE_DELAYED = 1;
  private static final int STATE_FINISHED = 3;
  private static final int STATE_PREPARE = 0;
  private static final int STATE_RUNNING = 2;
  public static final int TRANSLATION_X = 0;
  public static final int TRANSLATION_Y = 1;
  public static final int TRANSLATION_Z = 2;
  public static final int X = 8;
  public static final int Y = 9;
  public static final int Z = 10;
  private static ThreadLocal<DelayedAnimationHelper> sAnimationHelper = new ThreadLocal();
  private static final SparseIntArray sViewPropertyAnimatorMap = new SparseIntArray(15) {};
  private float mFinalValue;
  private Handler mHandler;
  private TimeInterpolator mInterpolator;
  private VirtualRefBasePtr mNativePtr;
  private int mRenderProperty = -1;
  private long mStartDelay = 0L;
  private long mStartTime;
  private int mState = 0;
  private RenderNode mTarget;
  private final boolean mUiThreadHandlesDelay;
  private long mUnscaledDuration = 300L;
  private long mUnscaledStartDelay = 0L;
  private View mViewTarget;
  
  @UnsupportedAppUsage
  public RenderNodeAnimator(int paramInt, float paramFloat)
  {
    this.mRenderProperty = paramInt;
    this.mFinalValue = paramFloat;
    this.mUiThreadHandlesDelay = true;
    init(nCreateAnimator(paramInt, paramFloat));
  }
  
  public RenderNodeAnimator(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
  {
    init(nCreateRevealAnimator(paramInt1, paramInt2, paramFloat1, paramFloat2));
    this.mUiThreadHandlesDelay = true;
  }
  
  @UnsupportedAppUsage
  public RenderNodeAnimator(CanvasProperty<Float> paramCanvasProperty, float paramFloat)
  {
    init(nCreateCanvasPropertyFloatAnimator(paramCanvasProperty.getNativeContainer(), paramFloat));
    this.mUiThreadHandlesDelay = false;
  }
  
  @UnsupportedAppUsage
  public RenderNodeAnimator(CanvasProperty<Paint> paramCanvasProperty, int paramInt, float paramFloat)
  {
    init(nCreateCanvasPropertyPaintAnimator(paramCanvasProperty.getNativeContainer(), paramInt, paramFloat));
    this.mUiThreadHandlesDelay = false;
  }
  
  private void applyInterpolator()
  {
    TimeInterpolator localTimeInterpolator = this.mInterpolator;
    if ((localTimeInterpolator != null) && (this.mNativePtr != null))
    {
      long l;
      if (isNativeInterpolator(localTimeInterpolator))
      {
        l = ((NativeInterpolatorFactory)this.mInterpolator).createNativeInterpolator();
      }
      else
      {
        l = nGetDuration(this.mNativePtr.get());
        l = FallbackLUTInterpolator.createNativeInterpolator(this.mInterpolator, l);
      }
      nSetInterpolator(this.mNativePtr.get(), l);
      return;
    }
  }
  
  @UnsupportedAppUsage
  private static void callOnFinished(RenderNodeAnimator paramRenderNodeAnimator)
  {
    Handler localHandler = paramRenderNodeAnimator.mHandler;
    if (localHandler != null)
    {
      Objects.requireNonNull(paramRenderNodeAnimator);
      localHandler.post(new _..Lambda.1kvF4JuyM42_wmyDVPAIYdPz1jE(paramRenderNodeAnimator));
    }
    else
    {
      localHandler = new Handler(Looper.getMainLooper(), null, true);
      Objects.requireNonNull(paramRenderNodeAnimator);
      localHandler.post(new _..Lambda.1kvF4JuyM42_wmyDVPAIYdPz1jE(paramRenderNodeAnimator));
    }
  }
  
  private void checkMutable()
  {
    if (this.mState == 0)
    {
      if (this.mNativePtr != null) {
        return;
      }
      throw new IllegalStateException("Animator's target has been destroyed (trying to modify an animation after activity destroy?)");
    }
    throw new IllegalStateException("Animator has already started, cannot change it now!");
  }
  
  private ArrayList<Animator.AnimatorListener> cloneListeners()
  {
    ArrayList localArrayList1 = getListeners();
    ArrayList localArrayList2 = localArrayList1;
    if (localArrayList1 != null) {
      localArrayList2 = (ArrayList)localArrayList1.clone();
    }
    return localArrayList2;
  }
  
  private void doStart()
  {
    if (this.mRenderProperty == 11)
    {
      this.mViewTarget.ensureTransformationInfo();
      this.mViewTarget.setAlphaInternal(this.mFinalValue);
    }
    moveToRunningState();
    View localView = this.mViewTarget;
    if (localView != null) {
      localView.invalidateViewProperty(true, false);
    }
  }
  
  private static DelayedAnimationHelper getHelper()
  {
    DelayedAnimationHelper localDelayedAnimationHelper1 = (DelayedAnimationHelper)sAnimationHelper.get();
    DelayedAnimationHelper localDelayedAnimationHelper2 = localDelayedAnimationHelper1;
    if (localDelayedAnimationHelper1 == null)
    {
      localDelayedAnimationHelper2 = new DelayedAnimationHelper();
      sAnimationHelper.set(localDelayedAnimationHelper2);
    }
    return localDelayedAnimationHelper2;
  }
  
  private void init(long paramLong)
  {
    this.mNativePtr = new VirtualRefBasePtr(paramLong);
  }
  
  static boolean isNativeInterpolator(TimeInterpolator paramTimeInterpolator)
  {
    return paramTimeInterpolator.getClass().isAnnotationPresent(HasNativeInterpolator.class);
  }
  
  @UnsupportedAppUsage
  public static int mapViewPropertyToRenderProperty(int paramInt)
  {
    return sViewPropertyAnimatorMap.get(paramInt);
  }
  
  private void moveToRunningState()
  {
    this.mState = 2;
    VirtualRefBasePtr localVirtualRefBasePtr = this.mNativePtr;
    if (localVirtualRefBasePtr != null) {
      nStart(localVirtualRefBasePtr.get());
    }
    notifyStartListeners();
  }
  
  private static native long nCreateAnimator(int paramInt, float paramFloat);
  
  private static native long nCreateCanvasPropertyFloatAnimator(long paramLong, float paramFloat);
  
  private static native long nCreateCanvasPropertyPaintAnimator(long paramLong, int paramInt, float paramFloat);
  
  private static native long nCreateRevealAnimator(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2);
  
  private static native void nEnd(long paramLong);
  
  private static native long nGetDuration(long paramLong);
  
  private static native void nSetAllowRunningAsync(long paramLong, boolean paramBoolean);
  
  private static native void nSetDuration(long paramLong1, long paramLong2);
  
  private static native void nSetInterpolator(long paramLong1, long paramLong2);
  
  private static native void nSetListener(long paramLong, RenderNodeAnimator paramRenderNodeAnimator);
  
  private static native void nSetStartDelay(long paramLong1, long paramLong2);
  
  private static native void nSetStartValue(long paramLong, float paramFloat);
  
  private static native void nStart(long paramLong);
  
  private void notifyStartListeners()
  {
    ArrayList localArrayList = cloneListeners();
    int i;
    if (localArrayList == null) {
      i = 0;
    } else {
      i = localArrayList.size();
    }
    for (int j = 0; j < i; j++) {
      ((Animator.AnimatorListener)localArrayList.get(j)).onAnimationStart(this);
    }
  }
  
  private boolean processDelayed(long paramLong)
  {
    long l = this.mStartTime;
    if (l == 0L)
    {
      this.mStartTime = paramLong;
    }
    else if (paramLong - l >= this.mStartDelay)
    {
      doStart();
      return true;
    }
    return false;
  }
  
  private void releaseNativePtr()
  {
    VirtualRefBasePtr localVirtualRefBasePtr = this.mNativePtr;
    if (localVirtualRefBasePtr != null)
    {
      localVirtualRefBasePtr.release();
      this.mNativePtr = null;
    }
  }
  
  private void setTarget(RenderNode paramRenderNode)
  {
    checkMutable();
    if (this.mTarget == null)
    {
      nSetListener(this.mNativePtr.get(), this);
      this.mTarget = paramRenderNode;
      this.mTarget.addAnimator(this);
      return;
    }
    throw new IllegalStateException("Target already set!");
  }
  
  public void cancel()
  {
    int i = this.mState;
    if ((i != 0) && (i != 3))
    {
      if (i == 1)
      {
        getHelper().removeDelayedAnimation(this);
        moveToRunningState();
      }
      ArrayList localArrayList = cloneListeners();
      if (localArrayList == null) {
        i = 0;
      } else {
        i = localArrayList.size();
      }
      for (int j = 0; j < i; j++) {
        ((Animator.AnimatorListener)localArrayList.get(j)).onAnimationCancel(this);
      }
      end();
    }
  }
  
  public Animator clone()
  {
    throw new IllegalStateException("Cannot clone this animator");
  }
  
  public void end()
  {
    int i = this.mState;
    if (i != 3)
    {
      if (i < 2)
      {
        getHelper().removeDelayedAnimation(this);
        doStart();
      }
      Object localObject = this.mNativePtr;
      if (localObject != null)
      {
        nEnd(((VirtualRefBasePtr)localObject).get());
        localObject = this.mViewTarget;
        if (localObject != null) {
          ((View)localObject).invalidateViewProperty(true, false);
        }
      }
      else
      {
        onFinished();
      }
    }
  }
  
  public long getDuration()
  {
    return this.mUnscaledDuration;
  }
  
  public TimeInterpolator getInterpolator()
  {
    return this.mInterpolator;
  }
  
  public long getNativeAnimator()
  {
    return this.mNativePtr.get();
  }
  
  public long getStartDelay()
  {
    return this.mUnscaledStartDelay;
  }
  
  public long getTotalDuration()
  {
    return this.mUnscaledDuration + this.mUnscaledStartDelay;
  }
  
  public boolean isRunning()
  {
    int i = this.mState;
    boolean bool1 = true;
    boolean bool2 = bool1;
    if (i != 1) {
      if (i == 2) {
        bool2 = bool1;
      } else {
        bool2 = false;
      }
    }
    return bool2;
  }
  
  public boolean isStarted()
  {
    boolean bool;
    if (this.mState != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  protected void onFinished()
  {
    int i = this.mState;
    if (i == 0)
    {
      releaseNativePtr();
      return;
    }
    if (i == 1)
    {
      getHelper().removeDelayedAnimation(this);
      notifyStartListeners();
    }
    this.mState = 3;
    ArrayList localArrayList = cloneListeners();
    if (localArrayList == null) {
      i = 0;
    } else {
      i = localArrayList.size();
    }
    for (int j = 0; j < i; j++) {
      ((Animator.AnimatorListener)localArrayList.get(j)).onAnimationEnd(this);
    }
    releaseNativePtr();
  }
  
  public void pause()
  {
    throw new UnsupportedOperationException();
  }
  
  public void resume()
  {
    throw new UnsupportedOperationException();
  }
  
  public void setAllowRunningAsynchronously(boolean paramBoolean)
  {
    checkMutable();
    nSetAllowRunningAsync(this.mNativePtr.get(), paramBoolean);
  }
  
  @UnsupportedAppUsage
  public RenderNodeAnimator setDuration(long paramLong)
  {
    checkMutable();
    if (paramLong >= 0L)
    {
      this.mUnscaledDuration = paramLong;
      nSetDuration(this.mNativePtr.get(), ((float)paramLong * ValueAnimator.getDurationScale()));
      return this;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("duration must be positive; ");
    localStringBuilder.append(paramLong);
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public void setInterpolator(TimeInterpolator paramTimeInterpolator)
  {
    checkMutable();
    this.mInterpolator = paramTimeInterpolator;
  }
  
  public void setStartDelay(long paramLong)
  {
    checkMutable();
    if (paramLong >= 0L)
    {
      this.mUnscaledStartDelay = paramLong;
      this.mStartDelay = ((ValueAnimator.getDurationScale() * (float)paramLong));
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("startDelay must be positive; ");
    localStringBuilder.append(paramLong);
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  @UnsupportedAppUsage
  public void setStartValue(float paramFloat)
  {
    checkMutable();
    nSetStartValue(this.mNativePtr.get(), paramFloat);
  }
  
  public void setTarget(RecordingCanvas paramRecordingCanvas)
  {
    setTarget(paramRecordingCanvas.mNode);
  }
  
  @UnsupportedAppUsage
  public void setTarget(DisplayListCanvas paramDisplayListCanvas)
  {
    setTarget((RecordingCanvas)paramDisplayListCanvas);
  }
  
  @UnsupportedAppUsage
  public void setTarget(View paramView)
  {
    this.mViewTarget = paramView;
    setTarget(this.mViewTarget.mRenderNode);
  }
  
  public void start()
  {
    if (this.mTarget != null)
    {
      if (this.mState == 0)
      {
        this.mState = 1;
        if (this.mHandler == null) {
          this.mHandler = new Handler(true);
        }
        applyInterpolator();
        if (this.mNativePtr == null)
        {
          cancel();
        }
        else if ((this.mStartDelay > 0L) && (this.mUiThreadHandlesDelay))
        {
          getHelper().addDelayedAnimation(this);
        }
        else
        {
          nSetStartDelay(this.mNativePtr.get(), this.mStartDelay);
          doStart();
        }
        return;
      }
      throw new IllegalStateException("Already started!");
    }
    throw new IllegalStateException("Missing target!");
  }
  
  private static class DelayedAnimationHelper
    implements Runnable
  {
    private boolean mCallbackScheduled;
    private final Choreographer mChoreographer = Choreographer.getInstance();
    private ArrayList<RenderNodeAnimator> mDelayedAnims = new ArrayList();
    
    private void scheduleCallback()
    {
      if (!this.mCallbackScheduled)
      {
        this.mCallbackScheduled = true;
        this.mChoreographer.postCallback(1, this, null);
      }
    }
    
    public void addDelayedAnimation(RenderNodeAnimator paramRenderNodeAnimator)
    {
      this.mDelayedAnims.add(paramRenderNodeAnimator);
      scheduleCallback();
    }
    
    public void removeDelayedAnimation(RenderNodeAnimator paramRenderNodeAnimator)
    {
      this.mDelayedAnims.remove(paramRenderNodeAnimator);
    }
    
    public void run()
    {
      long l = this.mChoreographer.getFrameTime();
      this.mCallbackScheduled = false;
      int i = 0;
      int j = 0;
      Object localObject;
      while (j < this.mDelayedAnims.size())
      {
        localObject = (RenderNodeAnimator)this.mDelayedAnims.get(j);
        int k = i;
        if (!((RenderNodeAnimator)localObject).processDelayed(l))
        {
          if (i != j) {
            this.mDelayedAnims.set(i, localObject);
          }
          k = i + 1;
        }
        j++;
        i = k;
      }
      while (this.mDelayedAnims.size() > i)
      {
        localObject = this.mDelayedAnims;
        ((ArrayList)localObject).remove(((ArrayList)localObject).size() - 1);
      }
      if (this.mDelayedAnims.size() > 0) {
        scheduleCallback();
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/RenderNodeAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */