package miui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;

public class AutoDisableScreenbuttonsFloatView
  extends FrameLayout
{
  private static final int DISMISS_DELAY_TIME = 8000;
  private Runnable mDismissRunnable = new Runnable()
  {
    public void run()
    {
      AutoDisableScreenbuttonsFloatView.this.dismiss();
    }
  };
  private boolean mIsShowing;
  
  public AutoDisableScreenbuttonsFloatView(Context paramContext)
  {
    super(paramContext);
  }
  
  public AutoDisableScreenbuttonsFloatView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public static AutoDisableScreenbuttonsFloatView inflate(Context paramContext)
  {
    return (AutoDisableScreenbuttonsFloatView)LayoutInflater.from(paramContext).inflate(285933583, null);
  }
  
  public void dismiss()
  {
    if (!this.mIsShowing) {
      return;
    }
    this.mIsShowing = false;
    removeCallbacks(this.mDismissRunnable);
    ((WindowManager)this.mContext.getSystemService("window")).removeView(this);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
  }
  
  public void show()
  {
    if (this.mIsShowing) {
      return;
    }
    WindowManager localWindowManager = (WindowManager)this.mContext.getSystemService("window");
    if (localWindowManager == null) {
      return;
    }
    WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(2010);
    localLayoutParams.gravity = 80;
    localLayoutParams.height = -2;
    localLayoutParams.width = -2;
    localLayoutParams.flags = 264;
    localLayoutParams.format = -3;
    localLayoutParams.windowAnimations = 286195713;
    localWindowManager.addView(this, localLayoutParams);
    postDelayed(this.mDismissRunnable, 8000L);
    this.mIsShowing = true;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/view/AutoDisableScreenbuttonsFloatView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */