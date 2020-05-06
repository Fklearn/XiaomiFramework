package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;

@Deprecated
public class ZoomControls
  extends LinearLayout
{
  @UnsupportedAppUsage
  private final ZoomButton mZoomIn;
  @UnsupportedAppUsage
  private final ZoomButton mZoomOut;
  
  public ZoomControls(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ZoomControls(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setFocusable(false);
    ((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(17367369, this, true);
    this.mZoomIn = ((ZoomButton)findViewById(16909621));
    this.mZoomOut = ((ZoomButton)findViewById(16909623));
  }
  
  private void fade(int paramInt, float paramFloat1, float paramFloat2)
  {
    AlphaAnimation localAlphaAnimation = new AlphaAnimation(paramFloat1, paramFloat2);
    localAlphaAnimation.setDuration(500L);
    startAnimation(localAlphaAnimation);
    setVisibility(paramInt);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return ZoomControls.class.getName();
  }
  
  public boolean hasFocus()
  {
    boolean bool;
    if ((!this.mZoomIn.hasFocus()) && (!this.mZoomOut.hasFocus())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public void hide()
  {
    fade(8, 1.0F, 0.0F);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    return true;
  }
  
  public void setIsZoomInEnabled(boolean paramBoolean)
  {
    this.mZoomIn.setEnabled(paramBoolean);
  }
  
  public void setIsZoomOutEnabled(boolean paramBoolean)
  {
    this.mZoomOut.setEnabled(paramBoolean);
  }
  
  public void setOnZoomInClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mZoomIn.setOnClickListener(paramOnClickListener);
  }
  
  public void setOnZoomOutClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mZoomOut.setOnClickListener(paramOnClickListener);
  }
  
  public void setZoomSpeed(long paramLong)
  {
    this.mZoomIn.setZoomSpeed(paramLong);
    this.mZoomOut.setZoomSpeed(paramLong);
  }
  
  public void show()
  {
    fade(0, 0.0F, 1.0F);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ZoomControls.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */