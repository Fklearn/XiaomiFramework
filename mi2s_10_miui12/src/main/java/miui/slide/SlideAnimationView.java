package miui.slide;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;
import java.io.IOException;
import java.io.InputStream;
import miui.view.animation.QuarticEaseOutInterpolator;
import miui.view.animation.SineEaseOutInterpolator;

public class SlideAnimationView
  extends View
{
  public static final String TAG = "SlideAnimationView";
  public static final int TYPE_ANIMATION_DOWN = 1;
  public static final int TYPE_ANIMATION_TIP = 2;
  public static final int TYPE_ANIMATION_UP = 0;
  public static final int TYPE_BITMAP_SIDE = 1;
  public static final int TYPE_BITMAP_TOP = 0;
  private float mEdgeAlpha;
  private Bitmap mEdgeColor;
  private float mEdgeColorAlpha;
  private Bitmap mEdgeLeft;
  private Bitmap mEdgeLeftOri;
  private Bitmap mEdgeRight;
  private Bitmap mEdgeRightOri;
  private float mEdgeScaleX;
  private float mEdgeY;
  private ValueAnimator mFlowingAnimator;
  private Matrix mMatrix;
  private Paint mPaint = new Paint();
  private Bitmap mTop;
  private float mTopAlpha;
  private Bitmap mTopOri;
  private float mTopScaleY;
  
  public SlideAnimationView(Context paramContext)
  {
    super(paramContext);
    setBackgroundColor(0);
    this.mMatrix = new Matrix();
    this.mPaint = new Paint();
  }
  
  private int getNavigationBarHeight()
  {
    Resources localResources = getResources();
    return localResources.getDimensionPixelSize(localResources.getIdentifier("navigation_bar_height", "dimen", "android"));
  }
  
  private float rangeAlpha(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    float f;
    if (paramInt1 < paramInt3) {
      f = (paramInt1 - paramInt2) / (paramInt3 - paramInt2);
    } else if (paramInt1 >= paramInt4) {
      f = 1.0F - (paramInt1 - paramInt4) / (paramInt5 - paramInt4);
    } else {
      f = 1.0F;
    }
    return f;
  }
  
  public int getDisplayHeight()
  {
    return getResources().getDisplayMetrics().heightPixels + getNavigationBarHeight();
  }
  
  public int getDisplayWidth()
  {
    return getResources().getDisplayMetrics().widthPixels;
  }
  
  public boolean isPortrait()
  {
    int i = getResources().getConfiguration().orientation;
    boolean bool = true;
    if (i != 1) {
      bool = false;
    }
    return bool;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (isPortrait()) {
      try
      {
        this.mPaint.setAlpha((int)(this.mTopAlpha * 255.0F));
        if (this.mTop != null) {
          paramCanvas.drawBitmap(this.mTop, 0.0F, 0.0F, this.mPaint);
        }
        this.mPaint.setAlpha((int)(this.mEdgeAlpha * 255.0F));
        if (this.mEdgeLeft != null) {
          paramCanvas.drawBitmap(this.mEdgeLeft, 0.0F, this.mEdgeY, this.mPaint);
        }
        if (this.mEdgeRight != null) {
          paramCanvas.drawBitmap(this.mEdgeRight, getDisplayWidth() - this.mEdgeRight.getWidth(), this.mEdgeY, this.mPaint);
        }
        this.mPaint.setAlpha((int)(this.mEdgeColorAlpha * 255.0F));
        if (this.mEdgeColor != null) {
          paramCanvas.drawBitmap(this.mEdgeColor, 0.0F, 0.0F, this.mPaint);
        }
      }
      catch (Exception paramCanvas)
      {
        paramCanvas.printStackTrace();
      }
    }
  }
  
  public void reset()
  {
    setVisibility(8);
    this.mEdgeLeft = null;
    this.mEdgeRight = null;
    this.mTop = null;
    this.mEdgeColor = null;
    this.mEdgeLeftOri = null;
    this.mEdgeRightOri = null;
    this.mTopOri = null;
    this.mPaint.setAlpha(255);
  }
  
  public void setBitmap()
  {
    Object localObject1 = null;
    InputStream localInputStream1 = null;
    InputStream localInputStream2 = null;
    InputStream localInputStream3 = null;
    InputStream localInputStream4 = localInputStream1;
    InputStream localInputStream5 = localInputStream2;
    InputStream localInputStream6 = localInputStream3;
    label233:
    label241:
    label293:
    label301:
    try
    {
      InputStream localInputStream7 = getResources().openRawResource(285671812);
      localObject1 = localInputStream7;
      localInputStream4 = localInputStream1;
      localInputStream5 = localInputStream2;
      localInputStream6 = localInputStream3;
      this.mEdgeLeftOri = BitmapFactory.decodeStream(localInputStream7);
      localObject1 = localInputStream7;
      localInputStream4 = localInputStream1;
      localInputStream5 = localInputStream2;
      localInputStream6 = localInputStream3;
      localInputStream1 = getResources().openRawResource(285671813);
      localObject1 = localInputStream7;
      localInputStream4 = localInputStream1;
      localInputStream5 = localInputStream2;
      localInputStream6 = localInputStream3;
      this.mEdgeRightOri = BitmapFactory.decodeStream(localInputStream1);
      localObject1 = localInputStream7;
      localInputStream4 = localInputStream1;
      localInputStream5 = localInputStream2;
      localInputStream6 = localInputStream3;
      localInputStream2 = getResources().openRawResource(285671814);
      localObject1 = localInputStream7;
      localInputStream4 = localInputStream1;
      localInputStream5 = localInputStream2;
      localInputStream6 = localInputStream3;
      this.mTopOri = BitmapFactory.decodeStream(localInputStream2);
      localObject1 = localInputStream7;
      localInputStream4 = localInputStream1;
      localInputStream5 = localInputStream2;
      localInputStream6 = localInputStream3;
      localInputStream3 = getResources().openRawResource(285671811);
      localObject1 = localInputStream7;
      localInputStream4 = localInputStream1;
      localInputStream5 = localInputStream2;
      localInputStream6 = localInputStream3;
      this.mEdgeColor = BitmapFactory.decodeStream(localInputStream3);
      if (localInputStream7 != null) {
        try
        {
          localInputStream7.close();
        }
        catch (IOException localIOException1)
        {
          break label233;
        }
      }
      if (localInputStream1 != null) {
        localInputStream1.close();
      }
      if (localInputStream2 != null) {
        localInputStream2.close();
      }
      if (localInputStream3 != null)
      {
        localInputStream3.close();
        break label241;
        localIOException1.printStackTrace();
      }
      return;
    }
    finally
    {
      if (localObject1 != null) {
        try
        {
          ((InputStream)localObject1).close();
        }
        catch (IOException localIOException2)
        {
          break label293;
        }
      }
      if (localIOException2 != null) {
        localIOException2.close();
      }
      if (localInputStream5 != null) {
        localInputStream5.close();
      }
      if (localInputStream6 != null)
      {
        localInputStream6.close();
        break label301;
        localIOException2.printStackTrace();
      }
    }
  }
  
  public void startAnimating(final int paramInt)
  {
    if (isPortrait())
    {
      setVisibility(0);
      setBitmap();
      if (paramInt != 0)
      {
        if (paramInt == 1)
        {
          this.mFlowingAnimator = ValueAnimator.ofInt(new int[] { 2160, 0 });
          this.mFlowingAnimator.setDuration(800L);
          this.mFlowingAnimator.setInterpolator(new QuarticEaseOutInterpolator());
        }
      }
      else
      {
        this.mFlowingAnimator = ValueAnimator.ofInt(new int[] { 0, 2160 });
        this.mFlowingAnimator.setDuration(1000L);
        this.mFlowingAnimator.setInterpolator(new SineEaseOutInterpolator());
      }
      this.mFlowingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          try
          {
            int i = ((Integer)SlideAnimationView.this.mFlowingAnimator.getAnimatedValue()).intValue();
            SlideAnimationView.access$102(SlideAnimationView.this, SlideAnimationView.this.rangeAlpha(i, 0, 100, 150, 900));
            if ((SlideAnimationView.this.mTopAlpha < 0.0F) || (paramInt == 1)) {
              SlideAnimationView.access$102(SlideAnimationView.this, 0.0F);
            }
            SlideAnimationView.access$302(SlideAnimationView.this, (float)(SlideAnimationView.this.rangeAlpha(i, 0, 100, 150, 900) * 1.5D * 0.6D + 0.4D));
            SlideAnimationView.this.mMatrix.setScale(1.0F, SlideAnimationView.this.mTopScaleY);
            if ((SlideAnimationView.this.mTopOri != null) && (SlideAnimationView.this.mTopOri.getWidth() > 0) && (Math.round(SlideAnimationView.this.mTopOri.getHeight() * SlideAnimationView.this.mTopScaleY) > 0)) {
              SlideAnimationView.access$602(SlideAnimationView.this, Bitmap.createBitmap(SlideAnimationView.this.mTopOri, 0, 0, SlideAnimationView.this.mTopOri.getWidth(), SlideAnimationView.this.mTopOri.getHeight(), SlideAnimationView.this.mMatrix, true));
            }
            SlideAnimationView.access$702(SlideAnimationView.this, (float)(i * 1.2D - 300.0D - 1170.0D));
            SlideAnimationView.access$802(SlideAnimationView.this, SlideAnimationView.this.rangeAlpha(i, 480, 960, 1480, 1920));
            SlideAnimationView.access$902(SlideAnimationView.this, (float)(SlideAnimationView.this.rangeAlpha(i, 400, 800, 1000, 1800) * 0.9D + 0.1D));
            SlideAnimationView.this.mMatrix.setScale(SlideAnimationView.this.mEdgeScaleX, 1.0F);
            if ((SlideAnimationView.this.mEdgeLeftOri != null) && (Math.round(SlideAnimationView.this.mEdgeLeftOri.getWidth() * SlideAnimationView.this.mEdgeScaleX) > 0) && (SlideAnimationView.this.mEdgeLeftOri.getHeight() > 0)) {
              SlideAnimationView.access$1102(SlideAnimationView.this, Bitmap.createBitmap(SlideAnimationView.this.mEdgeLeftOri, 0, 0, SlideAnimationView.this.mEdgeLeftOri.getWidth(), SlideAnimationView.this.mEdgeLeftOri.getHeight(), SlideAnimationView.this.mMatrix, true));
            }
            if ((SlideAnimationView.this.mEdgeRightOri != null) && (Math.round(SlideAnimationView.this.mEdgeRightOri.getWidth() * SlideAnimationView.this.mEdgeScaleX) > 0) && (SlideAnimationView.this.mEdgeRightOri.getHeight() > 0)) {
              SlideAnimationView.access$1302(SlideAnimationView.this, Bitmap.createBitmap(SlideAnimationView.this.mEdgeRightOri, 0, 0, SlideAnimationView.this.mEdgeRightOri.getWidth(), SlideAnimationView.this.mEdgeRightOri.getHeight(), SlideAnimationView.this.mMatrix, true));
            }
            if (SlideAnimationView.this.mEdgeAlpha < 0.0F) {
              SlideAnimationView.access$802(SlideAnimationView.this, 0.0F);
            }
            SlideAnimationView.access$1402(SlideAnimationView.this, SlideAnimationView.this.rangeAlpha(i, 0, 450, 450, 1480));
            if ((SlideAnimationView.this.mEdgeColorAlpha < 0.0F) || (paramInt == 1)) {
              SlideAnimationView.access$1402(SlideAnimationView.this, 0.0F);
            }
            SlideAnimationView.this.invalidate();
          }
          catch (Exception paramAnonymousValueAnimator)
          {
            paramAnonymousValueAnimator.printStackTrace();
          }
        }
      });
      this.mFlowingAnimator.addListener(new Animator.AnimatorListener()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator) {}
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          SlideAnimationView.this.reset();
        }
        
        public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
        
        public void onAnimationStart(Animator paramAnonymousAnimator) {}
      });
      this.mFlowingAnimator.start();
    }
  }
  
  public void stopAnimator()
  {
    ValueAnimator localValueAnimator = this.mFlowingAnimator;
    if (localValueAnimator != null) {
      localValueAnimator.cancel();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/slide/SlideAnimationView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */