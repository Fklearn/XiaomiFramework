package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Process;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import com.android.internal.R.styleable;

@RemoteViews.RemoteView
public class ViewFlipper
  extends ViewAnimator
{
  private static final int DEFAULT_INTERVAL = 3000;
  private static final boolean LOGD = false;
  private static final String TAG = "ViewFlipper";
  private boolean mAutoStart = false;
  private int mFlipInterval = 3000;
  private final Runnable mFlipRunnable = new Runnable()
  {
    public void run()
    {
      if (ViewFlipper.this.mRunning)
      {
        ViewFlipper.this.showNext();
        ViewFlipper localViewFlipper = ViewFlipper.this;
        localViewFlipper.postDelayed(localViewFlipper.mFlipRunnable, ViewFlipper.this.mFlipInterval);
      }
    }
  };
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if ("android.intent.action.SCREEN_OFF".equals(paramAnonymousContext))
      {
        ViewFlipper.access$002(ViewFlipper.this, false);
        ViewFlipper.this.updateRunning();
      }
      else if ("android.intent.action.USER_PRESENT".equals(paramAnonymousContext))
      {
        ViewFlipper.access$002(ViewFlipper.this, true);
        ViewFlipper.this.updateRunning(false);
      }
    }
  };
  private boolean mRunning = false;
  private boolean mStarted = false;
  @UnsupportedAppUsage
  private boolean mUserPresent = true;
  private boolean mVisible = false;
  
  public ViewFlipper(Context paramContext)
  {
    super(paramContext);
  }
  
  public ViewFlipper(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ViewFlipper);
    this.mFlipInterval = paramContext.getInt(0, 3000);
    this.mAutoStart = paramContext.getBoolean(1, false);
    paramContext.recycle();
  }
  
  private void updateRunning()
  {
    updateRunning(true);
  }
  
  @UnsupportedAppUsage
  private void updateRunning(boolean paramBoolean)
  {
    boolean bool;
    if ((this.mVisible) && (this.mStarted) && (this.mUserPresent)) {
      bool = true;
    } else {
      bool = false;
    }
    if (bool != this.mRunning)
    {
      if (bool)
      {
        showOnly(this.mWhichChild, paramBoolean);
        postDelayed(this.mFlipRunnable, this.mFlipInterval);
      }
      else
      {
        removeCallbacks(this.mFlipRunnable);
      }
      this.mRunning = bool;
    }
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return ViewFlipper.class.getName();
  }
  
  public int getFlipInterval()
  {
    return this.mFlipInterval;
  }
  
  public boolean isAutoStart()
  {
    return this.mAutoStart;
  }
  
  public boolean isFlipping()
  {
    return this.mStarted;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
    localIntentFilter.addAction("android.intent.action.USER_PRESENT");
    getContext().registerReceiverAsUser(this.mReceiver, Process.myUserHandle(), localIntentFilter, null, getHandler());
    if (this.mAutoStart) {
      startFlipping();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mVisible = false;
    getContext().unregisterReceiver(this.mReceiver);
    updateRunning();
  }
  
  protected void onWindowVisibilityChanged(int paramInt)
  {
    super.onWindowVisibilityChanged(paramInt);
    boolean bool;
    if (paramInt == 0) {
      bool = true;
    } else {
      bool = false;
    }
    this.mVisible = bool;
    updateRunning(false);
  }
  
  public void setAutoStart(boolean paramBoolean)
  {
    this.mAutoStart = paramBoolean;
  }
  
  @RemotableViewMethod
  public void setFlipInterval(int paramInt)
  {
    this.mFlipInterval = paramInt;
  }
  
  public void startFlipping()
  {
    this.mStarted = true;
    updateRunning();
  }
  
  public void stopFlipping()
  {
    this.mStarted = false;
    updateRunning();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ViewFlipper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */