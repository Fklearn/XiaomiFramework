package miui.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemProperties;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager.LayoutParams;
import com.android.internal.R.styleable;

public class ScreenshotDrawable
  extends Drawable
  implements ValueAnimator.AnimatorUpdateListener
{
  private static final String TAG = "ScreenshotDrawable";
  private static boolean isdDisplayOled;
  private static boolean sHasRealBlur;
  private static int[] sTempLoc = new int[2];
  private int mBgColor;
  private Bitmap mBluredBitmap;
  private Drawable mOriginalDrawable;
  private View mOwnerView;
  private Paint mPaint = new Paint(3);
  private Rect mSrcRect = new Rect();
  private ValueAnimator mVisibilityChangeAnimator;
  
  static
  {
    boolean bool = false;
    sHasRealBlur = SystemProperties.getBoolean("ro.miui.has_real_blur", false);
    if (("oled".equals(SystemProperties.get("ro.vendor.display.type"))) || ("oled".equals(SystemProperties.get("ro.display.type")))) {
      bool = true;
    }
    isdDisplayOled = bool;
  }
  
  public ScreenshotDrawable(View paramView)
  {
    this.mOwnerView = paramView;
    paramView = paramView.getResources();
    int i;
    if (isdDisplayOled) {
      i = 285540422;
    } else {
      i = 285540421;
    }
    this.mBgColor = paramView.getColor(i);
  }
  
  private int mixColor(int paramInt1, int paramInt2)
  {
    return Color.alpha(paramInt1) * paramInt2 / 255 << 24 | 0xFFFFFF & paramInt1;
  }
  
  public static void processBlurBehindFlag(View paramView, ViewGroup.LayoutParams paramLayoutParams, boolean paramBoolean)
  {
    if ((((WindowManager.LayoutParams)paramLayoutParams).flags & 0x4) != 0)
    {
      if (!(paramView.getBackground() instanceof ScreenshotDrawable))
      {
        paramLayoutParams = new ScreenshotDrawable(paramView);
        paramLayoutParams.setOriginalDrawable(paramView.getBackground());
        paramView.setBackground(paramLayoutParams);
      }
    }
    else if ((paramBoolean) && ((paramView.getBackground() instanceof ScreenshotDrawable))) {
      ((ScreenshotDrawable)paramView.getBackground()).startVisibilityAnimator(false);
    }
  }
  
  /* Error */
  private void rebuildBluredBitmap()
  {
    // Byte code:
    //   0: getstatic 47	miui/util/ScreenshotDrawable:sHasRealBlur	Z
    //   3: ifeq +4 -> 7
    //   6: return
    //   7: aload_0
    //   8: aload_0
    //   9: getfield 84	miui/util/ScreenshotDrawable:mOwnerView	Landroid/view/View;
    //   12: invokevirtual 145	android/view/View:getContext	()Landroid/content/Context;
    //   15: fconst_1
    //   16: getstatic 150	miui/util/ScreenshotUtils:REAL_BLUR_MINIFY	I
    //   19: i2f
    //   20: fdiv
    //   21: iconst_0
    //   22: iconst_0
    //   23: iconst_1
    //   24: invokestatic 154	miui/util/ScreenshotUtils:getScreenshot	(Landroid/content/Context;FIIZ)Landroid/graphics/Bitmap;
    //   27: aload_0
    //   28: getfield 106	miui/util/ScreenshotDrawable:mBluredBitmap	Landroid/graphics/Bitmap;
    //   31: getstatic 157	miui/util/ScreenshotUtils:REAL_BLUR_RADIUS	I
    //   34: i2f
    //   35: invokestatic 160	android/content/res/Resources:getSystem	()Landroid/content/res/Resources;
    //   38: invokevirtual 164	android/content/res/Resources:getDisplayMetrics	()Landroid/util/DisplayMetrics;
    //   41: getfield 170	android/util/DisplayMetrics:density	F
    //   44: fmul
    //   45: f2i
    //   46: invokestatic 176	miui/graphics/BitmapFactory:fastBlur	(Landroid/graphics/Bitmap;Landroid/graphics/Bitmap;I)Landroid/graphics/Bitmap;
    //   49: putfield 106	miui/util/ScreenshotDrawable:mBluredBitmap	Landroid/graphics/Bitmap;
    //   52: goto +13 -> 65
    //   55: astore_1
    //   56: ldc 14
    //   58: ldc -78
    //   60: aload_1
    //   61: invokestatic 184	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   64: pop
    //   65: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	66	0	this	ScreenshotDrawable
    //   55	6	1	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   7	52	55	finally
  }
  
  private void startVisibilityAnimator(boolean paramBoolean)
  {
    ValueAnimator localValueAnimator = this.mVisibilityChangeAnimator;
    if ((localValueAnimator != null) && (localValueAnimator.isRunning())) {
      this.mVisibilityChangeAnimator.end();
    }
    if (paramBoolean)
    {
      this.mVisibilityChangeAnimator = ValueAnimator.ofInt(new int[] { 0, 255 });
      setAlpha(0);
    }
    else
    {
      this.mVisibilityChangeAnimator = ValueAnimator.ofInt(new int[] { this.mPaint.getAlpha(), 0 });
      this.mVisibilityChangeAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (ScreenshotDrawable.this.mBluredBitmap != null)
          {
            ScreenshotDrawable.this.mBluredBitmap.recycle();
            ScreenshotDrawable.access$102(ScreenshotDrawable.this, null);
          }
          if ((ScreenshotDrawable.this.getCallback() instanceof View)) {
            ((View)ScreenshotDrawable.this.getCallback()).setBackground(ScreenshotDrawable.this.getOriginalDrawable());
          }
        }
      });
    }
    this.mVisibilityChangeAnimator.setDuration(200L);
    this.mVisibilityChangeAnimator.addUpdateListener(this);
    this.mVisibilityChangeAnimator.start();
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.mBluredBitmap != null)
    {
      if (this.mSrcRect.isEmpty())
      {
        this.mOwnerView.getLocationOnScreen(sTempLoc);
        int i = sTempLoc[0] / ScreenshotUtils.REAL_BLUR_MINIFY;
        int j = sTempLoc[1] / ScreenshotUtils.REAL_BLUR_MINIFY;
        int k = getBounds().width() / ScreenshotUtils.REAL_BLUR_MINIFY;
        int m = getBounds().height() / ScreenshotUtils.REAL_BLUR_MINIFY;
        this.mSrcRect.set(i, j, i + k, j + m);
      }
      paramCanvas.drawBitmap(this.mBluredBitmap, this.mSrcRect, getBounds(), this.mPaint);
    }
    paramCanvas.drawColor(mixColor(this.mBgColor, this.mPaint.getAlpha()));
    Drawable localDrawable = this.mOriginalDrawable;
    if (localDrawable != null) {
      localDrawable.draw(paramCanvas);
    }
  }
  
  public int getOpacity()
  {
    return 0;
  }
  
  public Drawable getOriginalDrawable()
  {
    return this.mOriginalDrawable;
  }
  
  public void onAnimationUpdate(ValueAnimator paramValueAnimator)
  {
    setAlpha(((Integer)paramValueAnimator.getAnimatedValue()).intValue());
  }
  
  void processShow()
  {
    int i = 0;
    int j = i;
    Object localObject;
    if ((this.mOwnerView.getRootView().getLayoutParams() instanceof WindowManager.LayoutParams))
    {
      localObject = (WindowManager.LayoutParams)this.mOwnerView.getRootView().getLayoutParams();
      j = i;
      if (((WindowManager.LayoutParams)localObject).windowAnimations != 0)
      {
        localObject = this.mOwnerView.getContext().obtainStyledAttributes(((WindowManager.LayoutParams)localObject).windowAnimations, R.styleable.WindowAnimation);
        int k = ((TypedArray)localObject).getResourceId(0, 0);
        ((TypedArray)localObject).recycle();
        j = i;
        if (k != 0) {
          j = 1;
        }
      }
    }
    if (j != 0)
    {
      localObject = this.mVisibilityChangeAnimator;
      if ((localObject != null) && (((ValueAnimator)localObject).isRunning())) {
        this.mVisibilityChangeAnimator.end();
      }
      setAlpha(255);
    }
    else
    {
      startVisibilityAnimator(true);
    }
  }
  
  public void setAlpha(int paramInt)
  {
    this.mPaint.setAlpha(paramInt);
    Drawable localDrawable = this.mOriginalDrawable;
    if (localDrawable != null) {
      localDrawable.setAlpha(paramInt);
    }
    invalidateSelf();
  }
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    this.mSrcRect.setEmpty();
    Drawable localDrawable = this.mOriginalDrawable;
    if (localDrawable != null) {
      localDrawable.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void setOriginalDrawable(Drawable paramDrawable)
  {
    this.mOriginalDrawable = paramDrawable;
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      if (!(this.mOwnerView.getRootView().getLayoutParams() instanceof WindowManager.LayoutParams)) {
        this.mOwnerView.getRootView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener()
        {
          public void onViewAttachedToWindow(View paramAnonymousView)
          {
            ScreenshotDrawable.this.processShow();
            ScreenshotDrawable.this.mOwnerView.getRootView().removeOnAttachStateChangeListener(this);
          }
          
          public void onViewDetachedFromWindow(View paramAnonymousView) {}
        });
      } else {
        processShow();
      }
      this.mSrcRect.setEmpty();
      rebuildBluredBitmap();
    }
    return super.setVisible(paramBoolean1, paramBoolean2);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/ScreenshotDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */