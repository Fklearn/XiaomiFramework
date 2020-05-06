package android.widget;

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
public class AdapterViewFlipper
  extends AdapterViewAnimator
{
  private static final int DEFAULT_INTERVAL = 10000;
  private static final boolean LOGD = false;
  private static final String TAG = "ViewFlipper";
  private boolean mAdvancedByHost = false;
  private boolean mAutoStart = false;
  private int mFlipInterval = 10000;
  private final Runnable mFlipRunnable = new Runnable()
  {
    public void run()
    {
      if (AdapterViewFlipper.this.mRunning) {
        AdapterViewFlipper.this.showNext();
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
        AdapterViewFlipper.access$002(AdapterViewFlipper.this, false);
        AdapterViewFlipper.this.updateRunning();
      }
      else if ("android.intent.action.USER_PRESENT".equals(paramAnonymousContext))
      {
        AdapterViewFlipper.access$002(AdapterViewFlipper.this, true);
        AdapterViewFlipper.this.updateRunning(false);
      }
    }
  };
  private boolean mRunning = false;
  private boolean mStarted = false;
  private boolean mUserPresent = true;
  private boolean mVisible = false;
  
  public AdapterViewFlipper(Context paramContext)
  {
    super(paramContext);
  }
  
  public AdapterViewFlipper(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public AdapterViewFlipper(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public AdapterViewFlipper(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AdapterViewFlipper, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.AdapterViewFlipper, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    this.mFlipInterval = localTypedArray.getInt(0, 10000);
    this.mAutoStart = localTypedArray.getBoolean(1, false);
    this.mLoopViews = true;
    localTypedArray.recycle();
  }
  
  private void updateRunning()
  {
    updateRunning(true);
  }
  
  private void updateRunning(boolean paramBoolean)
  {
    boolean bool;
    if ((!this.mAdvancedByHost) && (this.mVisible) && (this.mStarted) && (this.mUserPresent) && (this.mAdapter != null)) {
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
  
  public void fyiWillBeAdvancedByHostKThx()
  {
    this.mAdvancedByHost = true;
    updateRunning(false);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return AdapterViewFlipper.class.getName();
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
  
  public void setAdapter(Adapter paramAdapter)
  {
    super.setAdapter(paramAdapter);
    updateRunning();
  }
  
  public void setAutoStart(boolean paramBoolean)
  {
    this.mAutoStart = paramBoolean;
  }
  
  public void setFlipInterval(int paramInt)
  {
    this.mFlipInterval = paramInt;
  }
  
  @RemotableViewMethod
  public void showNext()
  {
    if (this.mRunning)
    {
      removeCallbacks(this.mFlipRunnable);
      postDelayed(this.mFlipRunnable, this.mFlipInterval);
    }
    super.showNext();
  }
  
  @RemotableViewMethod
  public void showPrevious()
  {
    if (this.mRunning)
    {
      removeCallbacks(this.mFlipRunnable);
      postDelayed(this.mFlipRunnable, this.mFlipInterval);
    }
    super.showPrevious();
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AdapterViewFlipper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */