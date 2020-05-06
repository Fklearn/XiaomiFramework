package miui.slide;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.view.Display;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class SlideAnimationController
{
  public static final int SLIDER_CLOSE = 1;
  public static final int SLIDER_MOVE = 2;
  public static final int SLIDER_OPEN = 0;
  public static final int SLIDER_TIP = 3;
  private static String TAG = "SlideAnimationController";
  private SlideAnimationView mAnimationView;
  private boolean mAnimationViewAdded;
  private Context mContext;
  private Handler mHandler;
  private WindowManager mWindowManager;
  
  public SlideAnimationController(Context paramContext, Looper paramLooper)
  {
    this.mContext = paramContext;
    this.mHandler = new H(paramLooper);
    this.mWindowManager = ((WindowManager)this.mContext.getSystemService("window"));
    this.mAnimationView = new SlideAnimationView(this.mContext);
  }
  
  private WindowManager.LayoutParams getWindowParam(int paramInt)
  {
    if (paramInt <= 0)
    {
      localObject = new DisplayMetrics();
      this.mWindowManager.getDefaultDisplay().getRealMetrics((DisplayMetrics)localObject);
      paramInt = ((DisplayMetrics)localObject).heightPixels;
    }
    Object localObject = new WindowManager.LayoutParams(-1, paramInt, 2015, 1336, -3);
    ((WindowManager.LayoutParams)localObject).privateFlags |= 0x40;
    ((WindowManager.LayoutParams)localObject).privateFlags |= 0x10;
    ((WindowManager.LayoutParams)localObject).setTitle("slideAnimation");
    return (WindowManager.LayoutParams)localObject;
  }
  
  public void showView(int paramInt)
  {
    Message localMessage = Message.obtain();
    localMessage.what = paramInt;
    this.mHandler.sendMessage(localMessage);
  }
  
  private class H
    extends Handler
  {
    public H(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      try
      {
        int i = SlideAnimationController.this.mContext.getResources().getDisplayMetrics().widthPixels;
        WindowManager.LayoutParams localLayoutParams = SlideAnimationController.this.getWindowParam(-1);
        int j = paramMessage.what;
        if (j != 0)
        {
          if (j != 1)
          {
            if (j != 2)
            {
              if (j == 3)
              {
                Slog.d(SlideAnimationController.TAG, "slider tip animation");
                if (!SlideAnimationController.this.mAnimationViewAdded)
                {
                  localLayoutParams.width = i;
                  localLayoutParams.gravity = 17;
                  SlideAnimationController.this.mWindowManager.addView(SlideAnimationController.this.mAnimationView, localLayoutParams);
                  SlideAnimationController.this.mAnimationView.startAnimating(2);
                  SlideAnimationController.access$302(SlideAnimationController.this, true);
                }
              }
            }
            else
            {
              Slog.d(SlideAnimationController.TAG, "slider move animation");
              if (SlideAnimationController.this.mAnimationViewAdded)
              {
                SlideAnimationController.this.mAnimationView.stopAnimator();
                SlideAnimationController.this.mWindowManager.removeView(SlideAnimationController.this.mAnimationView);
                SlideAnimationController.access$302(SlideAnimationController.this, false);
              }
            }
          }
          else
          {
            Slog.d(SlideAnimationController.TAG, "slider close animation");
            if (!SlideAnimationController.this.mAnimationViewAdded)
            {
              localLayoutParams.width = i;
              localLayoutParams.gravity = 17;
              SlideAnimationController.this.mWindowManager.addView(SlideAnimationController.this.mAnimationView, localLayoutParams);
              SlideAnimationController.this.mAnimationView.startAnimating(1);
              SlideAnimationController.access$302(SlideAnimationController.this, true);
            }
          }
        }
        else
        {
          Slog.d(SlideAnimationController.TAG, "slider open animation");
          if (!SlideAnimationController.this.mAnimationViewAdded)
          {
            localLayoutParams.width = i;
            SlideAnimationController.this.mWindowManager.addView(SlideAnimationController.this.mAnimationView, localLayoutParams);
            SlideAnimationController.this.mAnimationView.startAnimating(0);
            SlideAnimationController.access$302(SlideAnimationController.this, true);
          }
        }
      }
      catch (Exception paramMessage)
      {
        paramMessage.printStackTrace();
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/slide/SlideAnimationController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */