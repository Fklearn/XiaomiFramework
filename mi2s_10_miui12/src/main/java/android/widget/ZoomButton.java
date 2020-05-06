package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;

@Deprecated
public class ZoomButton
  extends ImageButton
  implements View.OnLongClickListener
{
  private boolean mIsInLongpress;
  private final Runnable mRunnable = new Runnable()
  {
    public void run()
    {
      if ((ZoomButton.this.hasOnClickListeners()) && (ZoomButton.this.mIsInLongpress) && (ZoomButton.this.isEnabled()))
      {
        ZoomButton.this.callOnClick();
        ZoomButton localZoomButton = ZoomButton.this;
        localZoomButton.postDelayed(this, localZoomButton.mZoomSpeed);
      }
    }
  };
  private long mZoomSpeed = 1000L;
  
  public ZoomButton(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ZoomButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ZoomButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ZoomButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setOnLongClickListener(this);
  }
  
  public boolean dispatchUnhandledMove(View paramView, int paramInt)
  {
    clearFocus();
    return super.dispatchUnhandledMove(paramView, paramInt);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return ZoomButton.class.getName();
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    this.mIsInLongpress = false;
    return super.onKeyUp(paramInt, paramKeyEvent);
  }
  
  public boolean onLongClick(View paramView)
  {
    this.mIsInLongpress = true;
    post(this.mRunnable);
    return true;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((paramMotionEvent.getAction() == 3) || (paramMotionEvent.getAction() == 1)) {
      this.mIsInLongpress = false;
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    if (!paramBoolean) {
      setPressed(false);
    }
    super.setEnabled(paramBoolean);
  }
  
  public void setZoomSpeed(long paramLong)
  {
    this.mZoomSpeed = paramLong;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ZoomButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */