package android.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.RenderNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ViewPropertyAnimator
{
  static final int ALPHA = 2048;
  static final int NONE = 0;
  static final int ROTATION = 32;
  static final int ROTATION_X = 64;
  static final int ROTATION_Y = 128;
  static final int SCALE_X = 8;
  static final int SCALE_Y = 16;
  private static final int TRANSFORM_MASK = 2047;
  static final int TRANSLATION_X = 1;
  static final int TRANSLATION_Y = 2;
  static final int TRANSLATION_Z = 4;
  static final int X = 256;
  static final int Y = 512;
  static final int Z = 1024;
  private Runnable mAnimationStarter = new Runnable()
  {
    public void run()
    {
      ViewPropertyAnimator.this.startAnimation();
    }
  };
  private HashMap<Animator, Runnable> mAnimatorCleanupMap;
  private AnimatorEventListener mAnimatorEventListener = new AnimatorEventListener(null);
  private HashMap<Animator, PropertyBundle> mAnimatorMap = new HashMap();
  private HashMap<Animator, Runnable> mAnimatorOnEndMap;
  private HashMap<Animator, Runnable> mAnimatorOnStartMap;
  private HashMap<Animator, Runnable> mAnimatorSetupMap;
  private long mDuration;
  private boolean mDurationSet = false;
  private TimeInterpolator mInterpolator;
  private boolean mInterpolatorSet = false;
  private Animator.AnimatorListener mListener = null;
  ArrayList<NameValuesHolder> mPendingAnimations = new ArrayList();
  private Runnable mPendingCleanupAction;
  private Runnable mPendingOnEndAction;
  private Runnable mPendingOnStartAction;
  private Runnable mPendingSetupAction;
  private long mStartDelay = 0L;
  private boolean mStartDelaySet = false;
  private ValueAnimator mTempValueAnimator;
  private ValueAnimator.AnimatorUpdateListener mUpdateListener = null;
  final View mView;
  
  ViewPropertyAnimator(View paramView)
  {
    this.mView = paramView;
    paramView.ensureTransformationInfo();
  }
  
  private void animateProperty(int paramInt, float paramFloat)
  {
    float f = getValue(paramInt);
    animatePropertyBy(paramInt, f, paramFloat - f);
  }
  
  private void animatePropertyBy(int paramInt, float paramFloat)
  {
    animatePropertyBy(paramInt, getValue(paramInt), paramFloat);
  }
  
  private void animatePropertyBy(int paramInt, float paramFloat1, float paramFloat2)
  {
    if (this.mAnimatorMap.size() > 0)
    {
      Object localObject1 = null;
      Iterator localIterator = this.mAnimatorMap.keySet().iterator();
      for (;;)
      {
        localObject2 = localObject1;
        if (!localIterator.hasNext()) {
          break;
        }
        localObject2 = (Animator)localIterator.next();
        PropertyBundle localPropertyBundle = (PropertyBundle)this.mAnimatorMap.get(localObject2);
        if ((localPropertyBundle.cancel(paramInt)) && (localPropertyBundle.mPropertyMask == 0)) {
          break;
        }
      }
      if (localObject2 != null) {
        ((Animator)localObject2).cancel();
      }
    }
    Object localObject2 = new NameValuesHolder(paramInt, paramFloat1, paramFloat2);
    this.mPendingAnimations.add(localObject2);
    this.mView.removeCallbacks(this.mAnimationStarter);
    this.mView.postOnAnimation(this.mAnimationStarter);
  }
  
  private float getValue(int paramInt)
  {
    RenderNode localRenderNode = this.mView.mRenderNode;
    if (paramInt != 1)
    {
      if (paramInt != 2)
      {
        if (paramInt != 4)
        {
          if (paramInt != 8)
          {
            if (paramInt != 16)
            {
              if (paramInt != 32)
              {
                if (paramInt != 64)
                {
                  if (paramInt != 128)
                  {
                    if (paramInt != 256)
                    {
                      if (paramInt != 512)
                      {
                        if (paramInt != 1024)
                        {
                          if (paramInt != 2048) {
                            return 0.0F;
                          }
                          return this.mView.getAlpha();
                        }
                        return localRenderNode.getElevation() + localRenderNode.getTranslationZ();
                      }
                      return this.mView.mTop + localRenderNode.getTranslationY();
                    }
                    return this.mView.mLeft + localRenderNode.getTranslationX();
                  }
                  return localRenderNode.getRotationY();
                }
                return localRenderNode.getRotationX();
              }
              return localRenderNode.getRotationZ();
            }
            return localRenderNode.getScaleY();
          }
          return localRenderNode.getScaleX();
        }
        return localRenderNode.getTranslationZ();
      }
      return localRenderNode.getTranslationY();
    }
    return localRenderNode.getTranslationX();
  }
  
  private void setValue(int paramInt, float paramFloat)
  {
    RenderNode localRenderNode = this.mView.mRenderNode;
    if (paramInt != 1)
    {
      if (paramInt != 2)
      {
        if (paramInt != 4)
        {
          if (paramInt != 8)
          {
            if (paramInt != 16)
            {
              if (paramInt != 32)
              {
                if (paramInt != 64)
                {
                  if (paramInt != 128)
                  {
                    if (paramInt != 256)
                    {
                      if (paramInt != 512)
                      {
                        if (paramInt != 1024)
                        {
                          if (paramInt == 2048)
                          {
                            this.mView.setAlphaInternal(paramFloat);
                            localRenderNode.setAlpha(paramFloat);
                          }
                        }
                        else {
                          localRenderNode.setTranslationZ(paramFloat - localRenderNode.getElevation());
                        }
                      }
                      else {
                        localRenderNode.setTranslationY(paramFloat - this.mView.mTop);
                      }
                    }
                    else {
                      localRenderNode.setTranslationX(paramFloat - this.mView.mLeft);
                    }
                  }
                  else {
                    localRenderNode.setRotationY(paramFloat);
                  }
                }
                else {
                  localRenderNode.setRotationX(paramFloat);
                }
              }
              else {
                localRenderNode.setRotationZ(paramFloat);
              }
            }
            else {
              localRenderNode.setScaleY(paramFloat);
            }
          }
          else {
            localRenderNode.setScaleX(paramFloat);
          }
        }
        else {
          localRenderNode.setTranslationZ(paramFloat);
        }
      }
      else {
        localRenderNode.setTranslationY(paramFloat);
      }
    }
    else {
      localRenderNode.setTranslationX(paramFloat);
    }
  }
  
  private void startAnimation()
  {
    this.mView.setHasTransientState(true);
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 1.0F });
    Object localObject = (ArrayList)this.mPendingAnimations.clone();
    this.mPendingAnimations.clear();
    int i = 0;
    int j = ((ArrayList)localObject).size();
    for (int k = 0; k < j; k++) {
      i |= ((NameValuesHolder)((ArrayList)localObject).get(k)).mNameConstant;
    }
    this.mAnimatorMap.put(localValueAnimator, new PropertyBundle(i, (ArrayList)localObject));
    localObject = this.mPendingSetupAction;
    if (localObject != null)
    {
      this.mAnimatorSetupMap.put(localValueAnimator, localObject);
      this.mPendingSetupAction = null;
    }
    localObject = this.mPendingCleanupAction;
    if (localObject != null)
    {
      this.mAnimatorCleanupMap.put(localValueAnimator, localObject);
      this.mPendingCleanupAction = null;
    }
    localObject = this.mPendingOnStartAction;
    if (localObject != null)
    {
      this.mAnimatorOnStartMap.put(localValueAnimator, localObject);
      this.mPendingOnStartAction = null;
    }
    localObject = this.mPendingOnEndAction;
    if (localObject != null)
    {
      this.mAnimatorOnEndMap.put(localValueAnimator, localObject);
      this.mPendingOnEndAction = null;
    }
    localValueAnimator.addUpdateListener(this.mAnimatorEventListener);
    localValueAnimator.addListener(this.mAnimatorEventListener);
    if (this.mStartDelaySet) {
      localValueAnimator.setStartDelay(this.mStartDelay);
    }
    if (this.mDurationSet) {
      localValueAnimator.setDuration(this.mDuration);
    }
    if (this.mInterpolatorSet) {
      localValueAnimator.setInterpolator(this.mInterpolator);
    }
    localValueAnimator.start();
  }
  
  public ViewPropertyAnimator alpha(float paramFloat)
  {
    animateProperty(2048, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator alphaBy(float paramFloat)
  {
    animatePropertyBy(2048, paramFloat);
    return this;
  }
  
  public void cancel()
  {
    if (this.mAnimatorMap.size() > 0)
    {
      Iterator localIterator = ((HashMap)this.mAnimatorMap.clone()).keySet().iterator();
      while (localIterator.hasNext()) {
        ((Animator)localIterator.next()).cancel();
      }
    }
    this.mPendingAnimations.clear();
    this.mPendingSetupAction = null;
    this.mPendingCleanupAction = null;
    this.mPendingOnStartAction = null;
    this.mPendingOnEndAction = null;
    this.mView.removeCallbacks(this.mAnimationStarter);
  }
  
  public long getDuration()
  {
    if (this.mDurationSet) {
      return this.mDuration;
    }
    if (this.mTempValueAnimator == null) {
      this.mTempValueAnimator = new ValueAnimator();
    }
    return this.mTempValueAnimator.getDuration();
  }
  
  public TimeInterpolator getInterpolator()
  {
    if (this.mInterpolatorSet) {
      return this.mInterpolator;
    }
    if (this.mTempValueAnimator == null) {
      this.mTempValueAnimator = new ValueAnimator();
    }
    return this.mTempValueAnimator.getInterpolator();
  }
  
  Animator.AnimatorListener getListener()
  {
    return this.mListener;
  }
  
  public long getStartDelay()
  {
    if (this.mStartDelaySet) {
      return this.mStartDelay;
    }
    return 0L;
  }
  
  ValueAnimator.AnimatorUpdateListener getUpdateListener()
  {
    return this.mUpdateListener;
  }
  
  boolean hasActions()
  {
    boolean bool;
    if ((this.mPendingSetupAction == null) && (this.mPendingCleanupAction == null) && (this.mPendingOnStartAction == null) && (this.mPendingOnEndAction == null)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public ViewPropertyAnimator rotation(float paramFloat)
  {
    animateProperty(32, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator rotationBy(float paramFloat)
  {
    animatePropertyBy(32, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator rotationX(float paramFloat)
  {
    animateProperty(64, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator rotationXBy(float paramFloat)
  {
    animatePropertyBy(64, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator rotationY(float paramFloat)
  {
    animateProperty(128, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator rotationYBy(float paramFloat)
  {
    animatePropertyBy(128, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator scaleX(float paramFloat)
  {
    animateProperty(8, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator scaleXBy(float paramFloat)
  {
    animatePropertyBy(8, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator scaleY(float paramFloat)
  {
    animateProperty(16, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator scaleYBy(float paramFloat)
  {
    animatePropertyBy(16, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator setDuration(long paramLong)
  {
    if (paramLong >= 0L)
    {
      this.mDurationSet = true;
      this.mDuration = paramLong;
      return this;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Animators cannot have negative duration: ");
    localStringBuilder.append(paramLong);
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public ViewPropertyAnimator setInterpolator(TimeInterpolator paramTimeInterpolator)
  {
    this.mInterpolatorSet = true;
    this.mInterpolator = paramTimeInterpolator;
    return this;
  }
  
  public ViewPropertyAnimator setListener(Animator.AnimatorListener paramAnimatorListener)
  {
    this.mListener = paramAnimatorListener;
    return this;
  }
  
  public ViewPropertyAnimator setStartDelay(long paramLong)
  {
    if (paramLong >= 0L)
    {
      this.mStartDelaySet = true;
      this.mStartDelay = paramLong;
      return this;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Animators cannot have negative start delay: ");
    localStringBuilder.append(paramLong);
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public ViewPropertyAnimator setUpdateListener(ValueAnimator.AnimatorUpdateListener paramAnimatorUpdateListener)
  {
    this.mUpdateListener = paramAnimatorUpdateListener;
    return this;
  }
  
  public void start()
  {
    this.mView.removeCallbacks(this.mAnimationStarter);
    startAnimation();
  }
  
  public ViewPropertyAnimator translationX(float paramFloat)
  {
    animateProperty(1, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator translationXBy(float paramFloat)
  {
    animatePropertyBy(1, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator translationY(float paramFloat)
  {
    animateProperty(2, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator translationYBy(float paramFloat)
  {
    animatePropertyBy(2, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator translationZ(float paramFloat)
  {
    animateProperty(4, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator translationZBy(float paramFloat)
  {
    animatePropertyBy(4, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator withEndAction(Runnable paramRunnable)
  {
    this.mPendingOnEndAction = paramRunnable;
    if ((paramRunnable != null) && (this.mAnimatorOnEndMap == null)) {
      this.mAnimatorOnEndMap = new HashMap();
    }
    return this;
  }
  
  public ViewPropertyAnimator withLayer()
  {
    this.mPendingSetupAction = new Runnable()
    {
      public void run()
      {
        ViewPropertyAnimator.this.mView.setLayerType(2, null);
        if (ViewPropertyAnimator.this.mView.isAttachedToWindow()) {
          ViewPropertyAnimator.this.mView.buildLayer();
        }
      }
    };
    this.mPendingCleanupAction = new Runnable()
    {
      public void run()
      {
        ViewPropertyAnimator.this.mView.setLayerType(this.val$currentLayerType, null);
      }
    };
    if (this.mAnimatorSetupMap == null) {
      this.mAnimatorSetupMap = new HashMap();
    }
    if (this.mAnimatorCleanupMap == null) {
      this.mAnimatorCleanupMap = new HashMap();
    }
    return this;
  }
  
  public ViewPropertyAnimator withStartAction(Runnable paramRunnable)
  {
    this.mPendingOnStartAction = paramRunnable;
    if ((paramRunnable != null) && (this.mAnimatorOnStartMap == null)) {
      this.mAnimatorOnStartMap = new HashMap();
    }
    return this;
  }
  
  public ViewPropertyAnimator x(float paramFloat)
  {
    animateProperty(256, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator xBy(float paramFloat)
  {
    animatePropertyBy(256, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator y(float paramFloat)
  {
    animateProperty(512, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator yBy(float paramFloat)
  {
    animatePropertyBy(512, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator z(float paramFloat)
  {
    animateProperty(1024, paramFloat);
    return this;
  }
  
  public ViewPropertyAnimator zBy(float paramFloat)
  {
    animatePropertyBy(1024, paramFloat);
    return this;
  }
  
  private class AnimatorEventListener
    implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener
  {
    private AnimatorEventListener() {}
    
    public void onAnimationCancel(Animator paramAnimator)
    {
      if (ViewPropertyAnimator.this.mListener != null) {
        ViewPropertyAnimator.this.mListener.onAnimationCancel(paramAnimator);
      }
      if (ViewPropertyAnimator.this.mAnimatorOnEndMap != null) {
        ViewPropertyAnimator.this.mAnimatorOnEndMap.remove(paramAnimator);
      }
    }
    
    public void onAnimationEnd(Animator paramAnimator)
    {
      ViewPropertyAnimator.this.mView.setHasTransientState(false);
      Runnable localRunnable;
      if (ViewPropertyAnimator.this.mAnimatorCleanupMap != null)
      {
        localRunnable = (Runnable)ViewPropertyAnimator.this.mAnimatorCleanupMap.get(paramAnimator);
        if (localRunnable != null) {
          localRunnable.run();
        }
        ViewPropertyAnimator.this.mAnimatorCleanupMap.remove(paramAnimator);
      }
      if (ViewPropertyAnimator.this.mListener != null) {
        ViewPropertyAnimator.this.mListener.onAnimationEnd(paramAnimator);
      }
      if (ViewPropertyAnimator.this.mAnimatorOnEndMap != null)
      {
        localRunnable = (Runnable)ViewPropertyAnimator.this.mAnimatorOnEndMap.get(paramAnimator);
        if (localRunnable != null) {
          localRunnable.run();
        }
        ViewPropertyAnimator.this.mAnimatorOnEndMap.remove(paramAnimator);
      }
      ViewPropertyAnimator.this.mAnimatorMap.remove(paramAnimator);
    }
    
    public void onAnimationRepeat(Animator paramAnimator)
    {
      if (ViewPropertyAnimator.this.mListener != null) {
        ViewPropertyAnimator.this.mListener.onAnimationRepeat(paramAnimator);
      }
    }
    
    public void onAnimationStart(Animator paramAnimator)
    {
      Runnable localRunnable;
      if (ViewPropertyAnimator.this.mAnimatorSetupMap != null)
      {
        localRunnable = (Runnable)ViewPropertyAnimator.this.mAnimatorSetupMap.get(paramAnimator);
        if (localRunnable != null) {
          localRunnable.run();
        }
        ViewPropertyAnimator.this.mAnimatorSetupMap.remove(paramAnimator);
      }
      if (ViewPropertyAnimator.this.mAnimatorOnStartMap != null)
      {
        localRunnable = (Runnable)ViewPropertyAnimator.this.mAnimatorOnStartMap.get(paramAnimator);
        if (localRunnable != null) {
          localRunnable.run();
        }
        ViewPropertyAnimator.this.mAnimatorOnStartMap.remove(paramAnimator);
      }
      if (ViewPropertyAnimator.this.mListener != null) {
        ViewPropertyAnimator.this.mListener.onAnimationStart(paramAnimator);
      }
    }
    
    public void onAnimationUpdate(ValueAnimator paramValueAnimator)
    {
      Object localObject = (ViewPropertyAnimator.PropertyBundle)ViewPropertyAnimator.this.mAnimatorMap.get(paramValueAnimator);
      if (localObject == null) {
        return;
      }
      boolean bool = ViewPropertyAnimator.this.mView.isHardwareAccelerated();
      if (!bool) {
        ViewPropertyAnimator.this.mView.invalidateParentCaches();
      }
      float f1 = paramValueAnimator.getAnimatedFraction();
      int i = ((ViewPropertyAnimator.PropertyBundle)localObject).mPropertyMask;
      if ((i & 0x7FF) != 0) {
        ViewPropertyAnimator.this.mView.invalidateViewProperty(bool, false);
      }
      localObject = ((ViewPropertyAnimator.PropertyBundle)localObject).mNameValuesHolder;
      if (localObject != null)
      {
        int j = ((ArrayList)localObject).size();
        for (int k = 0; k < j; k++)
        {
          ViewPropertyAnimator.NameValuesHolder localNameValuesHolder = (ViewPropertyAnimator.NameValuesHolder)((ArrayList)localObject).get(k);
          float f2 = localNameValuesHolder.mFromValue;
          float f3 = localNameValuesHolder.mDeltaValue;
          ViewPropertyAnimator.this.setValue(localNameValuesHolder.mNameConstant, f2 + f3 * f1);
        }
      }
      if (((i & 0x7FF) != 0) && (!bool))
      {
        localObject = ViewPropertyAnimator.this.mView;
        ((View)localObject).mPrivateFlags |= 0x20;
      }
      ViewPropertyAnimator.this.mView.invalidateViewProperty(false, false);
      if (ViewPropertyAnimator.this.mUpdateListener != null) {
        ViewPropertyAnimator.this.mUpdateListener.onAnimationUpdate(paramValueAnimator);
      }
    }
  }
  
  static class NameValuesHolder
  {
    float mDeltaValue;
    float mFromValue;
    int mNameConstant;
    
    NameValuesHolder(int paramInt, float paramFloat1, float paramFloat2)
    {
      this.mNameConstant = paramInt;
      this.mFromValue = paramFloat1;
      this.mDeltaValue = paramFloat2;
    }
  }
  
  private static class PropertyBundle
  {
    ArrayList<ViewPropertyAnimator.NameValuesHolder> mNameValuesHolder;
    int mPropertyMask;
    
    PropertyBundle(int paramInt, ArrayList<ViewPropertyAnimator.NameValuesHolder> paramArrayList)
    {
      this.mPropertyMask = paramInt;
      this.mNameValuesHolder = paramArrayList;
    }
    
    boolean cancel(int paramInt)
    {
      if ((this.mPropertyMask & paramInt) != 0)
      {
        ArrayList localArrayList = this.mNameValuesHolder;
        if (localArrayList != null)
        {
          int i = localArrayList.size();
          for (int j = 0; j < i; j++) {
            if (((ViewPropertyAnimator.NameValuesHolder)this.mNameValuesHolder.get(j)).mNameConstant == paramInt)
            {
              this.mNameValuesHolder.remove(j);
              this.mPropertyMask &= paramInt;
              return true;
            }
          }
        }
      }
      return false;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ViewPropertyAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */