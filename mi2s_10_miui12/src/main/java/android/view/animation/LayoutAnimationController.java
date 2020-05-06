package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.android.internal.R.styleable;
import java.util.Random;

public class LayoutAnimationController
{
  public static final int ORDER_NORMAL = 0;
  public static final int ORDER_RANDOM = 2;
  public static final int ORDER_REVERSE = 1;
  protected Animation mAnimation;
  private float mDelay;
  private long mDuration;
  protected Interpolator mInterpolator;
  private long mMaxDelay;
  private int mOrder;
  protected Random mRandomizer;
  
  public LayoutAnimationController(Context paramContext, AttributeSet paramAttributeSet)
  {
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.LayoutAnimation);
    this.mDelay = Animation.Description.parseValue(paramAttributeSet.peekValue(1)).value;
    this.mOrder = paramAttributeSet.getInt(3, 0);
    int i = paramAttributeSet.getResourceId(2, 0);
    if (i > 0) {
      setAnimation(paramContext, i);
    }
    i = paramAttributeSet.getResourceId(0, 0);
    if (i > 0) {
      setInterpolator(paramContext, i);
    }
    paramAttributeSet.recycle();
  }
  
  public LayoutAnimationController(Animation paramAnimation)
  {
    this(paramAnimation, 0.5F);
  }
  
  public LayoutAnimationController(Animation paramAnimation, float paramFloat)
  {
    this.mDelay = paramFloat;
    setAnimation(paramAnimation);
  }
  
  public Animation getAnimation()
  {
    return this.mAnimation;
  }
  
  public final Animation getAnimationForView(View paramView)
  {
    long l = getDelayForView(paramView) + this.mAnimation.getStartOffset();
    this.mMaxDelay = Math.max(this.mMaxDelay, l);
    try
    {
      paramView = this.mAnimation.clone();
      paramView.setStartOffset(l);
      return paramView;
    }
    catch (CloneNotSupportedException paramView) {}
    return null;
  }
  
  public float getDelay()
  {
    return this.mDelay;
  }
  
  protected long getDelayForView(View paramView)
  {
    paramView = paramView.getLayoutParams().layoutAnimationParameters;
    if (paramView == null) {
      return 0L;
    }
    float f1 = this.mDelay * (float)this.mAnimation.getDuration();
    long l = (getTransformedIndex(paramView) * f1);
    float f2 = paramView.count * f1;
    if (this.mInterpolator == null) {
      this.mInterpolator = new LinearInterpolator();
    }
    f1 = (float)l / f2;
    return (this.mInterpolator.getInterpolation(f1) * f2);
  }
  
  public Interpolator getInterpolator()
  {
    return this.mInterpolator;
  }
  
  public int getOrder()
  {
    return this.mOrder;
  }
  
  protected int getTransformedIndex(AnimationParameters paramAnimationParameters)
  {
    int i = getOrder();
    if (i != 1)
    {
      if (i != 2) {
        return paramAnimationParameters.index;
      }
      if (this.mRandomizer == null) {
        this.mRandomizer = new Random();
      }
      return (int)(paramAnimationParameters.count * this.mRandomizer.nextFloat());
    }
    return paramAnimationParameters.count - 1 - paramAnimationParameters.index;
  }
  
  public boolean isDone()
  {
    boolean bool;
    if (AnimationUtils.currentAnimationTimeMillis() > this.mAnimation.getStartTime() + this.mMaxDelay + this.mDuration) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void setAnimation(Context paramContext, int paramInt)
  {
    setAnimation(AnimationUtils.loadAnimation(paramContext, paramInt));
  }
  
  public void setAnimation(Animation paramAnimation)
  {
    this.mAnimation = paramAnimation;
    this.mAnimation.setFillBefore(true);
  }
  
  public void setDelay(float paramFloat)
  {
    this.mDelay = paramFloat;
  }
  
  public void setInterpolator(Context paramContext, int paramInt)
  {
    setInterpolator(AnimationUtils.loadInterpolator(paramContext, paramInt));
  }
  
  public void setInterpolator(Interpolator paramInterpolator)
  {
    this.mInterpolator = paramInterpolator;
  }
  
  public void setOrder(int paramInt)
  {
    this.mOrder = paramInt;
  }
  
  public void start()
  {
    this.mDuration = this.mAnimation.getDuration();
    this.mMaxDelay = Long.MIN_VALUE;
    this.mAnimation.setStartTime(-1L);
  }
  
  public boolean willOverlap()
  {
    boolean bool;
    if (this.mDelay < 1.0F) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static class AnimationParameters
  {
    public int count;
    public int index;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/LayoutAnimationController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */