package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.internal.R.styleable;

public class EdgeEffect
{
  private static final double ANGLE = 0.5235987755982988D;
  private static final float COS = (float)Math.cos(0.5235987755982988D);
  public static final BlendMode DEFAULT_BLEND_MODE = BlendMode.SRC_ATOP;
  private static final float EPSILON = 0.001F;
  private static final float GLOW_ALPHA_START = 0.09F;
  private static final float MAX_ALPHA = 0.15F;
  private static final float MAX_GLOW_SCALE = 2.0F;
  private static final int MAX_VELOCITY = 10000;
  private static final int MIN_VELOCITY = 100;
  private static final int PULL_DECAY_TIME = 2000;
  private static final float PULL_DISTANCE_ALPHA_GLOW_FACTOR = 0.8F;
  private static final float PULL_GLOW_BEGIN = 0.0F;
  private static final int PULL_TIME = 167;
  private static final float RADIUS_FACTOR = 0.6F;
  private static final int RECEDE_TIME = 600;
  private static final float SIN = (float)Math.sin(0.5235987755982988D);
  private static final int STATE_ABSORB = 2;
  private static final int STATE_IDLE = 0;
  private static final int STATE_PULL = 1;
  private static final int STATE_PULL_DECAY = 4;
  private static final int STATE_RECEDE = 3;
  private static final String TAG = "EdgeEffect";
  private static final int VELOCITY_GLOW_FACTOR = 6;
  private float mBaseGlowScale;
  private final Rect mBounds = new Rect();
  private float mDisplacement = 0.5F;
  private float mDuration;
  private float mGlowAlpha;
  private float mGlowAlphaFinish;
  private float mGlowAlphaStart;
  @UnsupportedAppUsage
  private float mGlowScaleY;
  private float mGlowScaleYFinish;
  private float mGlowScaleYStart;
  private final Interpolator mInterpolator;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769450L)
  private final Paint mPaint = new Paint();
  private float mPullDistance;
  private float mRadius;
  private long mStartTime;
  private int mState = 0;
  private float mTargetDisplacement = 0.5F;
  
  public EdgeEffect(Context paramContext)
  {
    this.mPaint.setAntiAlias(true);
    paramContext = paramContext.obtainStyledAttributes(R.styleable.EdgeEffect);
    int i = paramContext.getColor(0, -10066330);
    paramContext.recycle();
    this.mPaint.setColor(0xFFFFFF & i | 0x33000000);
    this.mPaint.setStyle(Paint.Style.FILL);
    this.mPaint.setBlendMode(DEFAULT_BLEND_MODE);
    this.mInterpolator = new DecelerateInterpolator();
  }
  
  private void update()
  {
    float f1 = Math.min((float)(AnimationUtils.currentAnimationTimeMillis() - this.mStartTime) / this.mDuration, 1.0F);
    float f2 = this.mInterpolator.getInterpolation(f1);
    float f3 = this.mGlowAlphaStart;
    this.mGlowAlpha = (f3 + (this.mGlowAlphaFinish - f3) * f2);
    f3 = this.mGlowScaleYStart;
    this.mGlowScaleY = (f3 + (this.mGlowScaleYFinish - f3) * f2);
    this.mDisplacement = ((this.mDisplacement + this.mTargetDisplacement) / 2.0F);
    if (f1 >= 0.999F)
    {
      int i = this.mState;
      if (i != 1)
      {
        if (i != 2)
        {
          if (i != 3)
          {
            if (i == 4) {
              this.mState = 3;
            }
          }
          else {
            this.mState = 0;
          }
        }
        else
        {
          this.mState = 3;
          this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
          this.mDuration = 600.0F;
          this.mGlowAlphaStart = this.mGlowAlpha;
          this.mGlowScaleYStart = this.mGlowScaleY;
          this.mGlowAlphaFinish = 0.0F;
          this.mGlowScaleYFinish = 0.0F;
        }
      }
      else
      {
        this.mState = 4;
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mDuration = 2000.0F;
        this.mGlowAlphaStart = this.mGlowAlpha;
        this.mGlowScaleYStart = this.mGlowScaleY;
        this.mGlowAlphaFinish = 0.0F;
        this.mGlowScaleYFinish = 0.0F;
      }
    }
  }
  
  public boolean draw(Canvas paramCanvas)
  {
    update();
    int i = paramCanvas.save();
    float f1 = this.mBounds.centerX();
    float f2 = this.mBounds.height();
    float f3 = this.mRadius;
    paramCanvas.scale(1.0F, Math.min(this.mGlowScaleY, 1.0F) * this.mBaseGlowScale, f1, 0.0F);
    float f4 = Math.max(0.0F, Math.min(this.mDisplacement, 1.0F));
    f4 = this.mBounds.width() * (f4 - 0.5F) / 2.0F;
    paramCanvas.clipRect(this.mBounds);
    paramCanvas.translate(f4, 0.0F);
    this.mPaint.setAlpha((int)(this.mGlowAlpha * 255.0F));
    paramCanvas.drawCircle(f1, f2 - f3, this.mRadius, this.mPaint);
    paramCanvas.restoreToCount(i);
    int j = 0;
    int k = this.mState;
    boolean bool = false;
    i = j;
    if (k == 3)
    {
      i = j;
      if (this.mGlowScaleY == 0.0F)
      {
        this.mState = 0;
        i = 1;
      }
    }
    if ((this.mState != 0) || (i != 0)) {
      bool = true;
    }
    return bool;
  }
  
  public void finish()
  {
    this.mState = 0;
  }
  
  public BlendMode getBlendMode()
  {
    return this.mPaint.getBlendMode();
  }
  
  public int getColor()
  {
    return this.mPaint.getColor();
  }
  
  public int getMaxHeight()
  {
    return (int)(this.mBounds.height() * 2.0F + 0.5F);
  }
  
  public boolean isFinished()
  {
    boolean bool;
    if (this.mState == 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void onAbsorb(int paramInt)
  {
    this.mState = 2;
    paramInt = Math.min(Math.max(100, Math.abs(paramInt)), 10000);
    this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
    this.mDuration = (paramInt * 0.02F + 0.15F);
    this.mGlowAlphaStart = 0.09F;
    this.mGlowScaleYStart = Math.max(this.mGlowScaleY, 0.0F);
    this.mGlowScaleYFinish = Math.min(paramInt / 100 * paramInt * 1.5E-4F / 2.0F + 0.025F, 1.0F);
    this.mGlowAlphaFinish = Math.max(this.mGlowAlphaStart, Math.min(paramInt * 6 * 1.0E-5F, 0.15F));
    this.mTargetDisplacement = 0.5F;
  }
  
  public void onPull(float paramFloat)
  {
    onPull(paramFloat, 0.5F);
  }
  
  public void onPull(float paramFloat1, float paramFloat2)
  {
    long l = AnimationUtils.currentAnimationTimeMillis();
    this.mTargetDisplacement = paramFloat2;
    if ((this.mState == 4) && ((float)(l - this.mStartTime) < this.mDuration)) {
      return;
    }
    if (this.mState != 1) {
      this.mGlowScaleY = Math.max(0.0F, this.mGlowScaleY);
    }
    this.mState = 1;
    this.mStartTime = l;
    this.mDuration = 167.0F;
    this.mPullDistance += paramFloat1;
    paramFloat1 = Math.abs(paramFloat1);
    paramFloat1 = Math.min(0.15F, this.mGlowAlpha + 0.8F * paramFloat1);
    this.mGlowAlphaStart = paramFloat1;
    this.mGlowAlpha = paramFloat1;
    paramFloat1 = this.mPullDistance;
    if (paramFloat1 == 0.0F)
    {
      this.mGlowScaleYStart = 0.0F;
      this.mGlowScaleY = 0.0F;
    }
    else
    {
      paramFloat1 = (float)(Math.max(0.0D, 1.0D - 1.0D / Math.sqrt(Math.abs(paramFloat1) * this.mBounds.height()) - 0.3D) / 0.7D);
      this.mGlowScaleYStart = paramFloat1;
      this.mGlowScaleY = paramFloat1;
    }
    this.mGlowAlphaFinish = this.mGlowAlpha;
    this.mGlowScaleYFinish = this.mGlowScaleY;
  }
  
  public void onRelease()
  {
    this.mPullDistance = 0.0F;
    int i = this.mState;
    if ((i != 1) && (i != 4)) {
      return;
    }
    this.mState = 3;
    this.mGlowAlphaStart = this.mGlowAlpha;
    this.mGlowScaleYStart = this.mGlowScaleY;
    this.mGlowAlphaFinish = 0.0F;
    this.mGlowScaleYFinish = 0.0F;
    this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
    this.mDuration = 600.0F;
  }
  
  public void setBlendMode(BlendMode paramBlendMode)
  {
    this.mPaint.setBlendMode(paramBlendMode);
  }
  
  public void setColor(int paramInt)
  {
    this.mPaint.setColor(paramInt);
  }
  
  public void setSize(int paramInt1, int paramInt2)
  {
    float f1 = paramInt1;
    float f2 = SIN;
    float f3 = f1 * 0.6F / f2;
    float f4 = COS;
    f1 = f3 - f4 * f3;
    float f5 = paramInt2 * 0.6F / f2;
    this.mRadius = f3;
    f2 = 1.0F;
    if (f1 > 0.0F) {
      f2 = Math.min((f5 - f4 * f5) / f1, 1.0F);
    }
    this.mBaseGlowScale = f2;
    Rect localRect = this.mBounds;
    localRect.set(localRect.left, this.mBounds.top, paramInt1, (int)Math.min(paramInt2, f1));
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/EdgeEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */