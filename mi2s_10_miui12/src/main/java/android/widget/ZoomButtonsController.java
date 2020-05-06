package android.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewRootImpl;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

@Deprecated
public class ZoomButtonsController
  implements View.OnTouchListener
{
  private static final int MSG_DISMISS_ZOOM_CONTROLS = 3;
  private static final int MSG_POST_CONFIGURATION_CHANGED = 2;
  private static final int MSG_POST_SET_VISIBLE = 4;
  private static final String TAG = "ZoomButtonsController";
  private static final int ZOOM_CONTROLS_TIMEOUT = (int)ViewConfiguration.getZoomControlsTimeout();
  private static final int ZOOM_CONTROLS_TOUCH_PADDING = 20;
  private boolean mAutoDismissControls = true;
  private OnZoomListener mCallback;
  private final IntentFilter mConfigurationChangedFilter = new IntentFilter("android.intent.action.CONFIGURATION_CHANGED");
  private final BroadcastReceiver mConfigurationChangedReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (!ZoomButtonsController.this.mIsVisible) {
        return;
      }
      ZoomButtonsController.this.mHandler.removeMessages(2);
      ZoomButtonsController.this.mHandler.sendEmptyMessage(2);
    }
  };
  private final FrameLayout mContainer;
  private WindowManager.LayoutParams mContainerLayoutParams;
  private final int[] mContainerRawLocation = new int[2];
  private final Context mContext;
  private ZoomControls mControls;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      int i = paramAnonymousMessage.what;
      if (i != 2)
      {
        if (i != 3)
        {
          if (i == 4) {
            if (ZoomButtonsController.this.mOwnerView.getWindowToken() == null) {
              Log.e("ZoomButtonsController", "Cannot make the zoom controller visible if the owner view is not attached to a window.");
            } else {
              ZoomButtonsController.this.setVisible(true);
            }
          }
        }
        else {
          ZoomButtonsController.this.setVisible(false);
        }
      }
      else {
        ZoomButtonsController.this.onPostConfigurationChanged();
      }
    }
  };
  private boolean mIsVisible;
  private final View mOwnerView;
  private final int[] mOwnerViewRawLocation = new int[2];
  private Runnable mPostedVisibleInitializer;
  private boolean mReleaseTouchListenerOnUp;
  private final int[] mTempIntArray = new int[2];
  private final Rect mTempRect = new Rect();
  private int mTouchPaddingScaledSq;
  private View mTouchTargetView;
  private final int[] mTouchTargetWindowLocation = new int[2];
  private final WindowManager mWindowManager;
  
  public ZoomButtonsController(View paramView)
  {
    this.mContext = paramView.getContext();
    this.mWindowManager = ((WindowManager)this.mContext.getSystemService("window"));
    this.mOwnerView = paramView;
    this.mTouchPaddingScaledSq = ((int)(this.mContext.getResources().getDisplayMetrics().density * 20.0F));
    int i = this.mTouchPaddingScaledSq;
    this.mTouchPaddingScaledSq = (i * i);
    this.mContainer = createContainer();
  }
  
  private FrameLayout createContainer()
  {
    WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(-2, -2);
    localLayoutParams.gravity = 8388659;
    localLayoutParams.flags = 131608;
    localLayoutParams.height = -2;
    localLayoutParams.width = -1;
    localLayoutParams.type = 1000;
    localLayoutParams.format = -3;
    localLayoutParams.windowAnimations = 16974607;
    this.mContainerLayoutParams = localLayoutParams;
    Container localContainer = new Container(this.mContext);
    localContainer.setLayoutParams(localLayoutParams);
    localContainer.setMeasureAllChildren(true);
    ((LayoutInflater)this.mContext.getSystemService("layout_inflater")).inflate(17367368, localContainer);
    this.mControls = ((ZoomControls)localContainer.findViewById(16909620));
    this.mControls.setOnZoomInClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ZoomButtonsController.this.dismissControlsDelayed(ZoomButtonsController.ZOOM_CONTROLS_TIMEOUT);
        if (ZoomButtonsController.this.mCallback != null) {
          ZoomButtonsController.this.mCallback.onZoom(true);
        }
      }
    });
    this.mControls.setOnZoomOutClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ZoomButtonsController.this.dismissControlsDelayed(ZoomButtonsController.ZOOM_CONTROLS_TIMEOUT);
        if (ZoomButtonsController.this.mCallback != null) {
          ZoomButtonsController.this.mCallback.onZoom(false);
        }
      }
    });
    return localContainer;
  }
  
  private void dismissControlsDelayed(int paramInt)
  {
    if (this.mAutoDismissControls)
    {
      this.mHandler.removeMessages(3);
      this.mHandler.sendEmptyMessageDelayed(3, paramInt);
    }
  }
  
  private View findViewForTouch(int paramInt1, int paramInt2)
  {
    Object localObject1 = this.mContainerRawLocation;
    int i = paramInt1 - localObject1[0];
    int j = paramInt2 - localObject1[1];
    Rect localRect = this.mTempRect;
    Object localObject2 = null;
    paramInt2 = Integer.MAX_VALUE;
    paramInt1 = this.mContainer.getChildCount() - 1;
    while (paramInt1 >= 0)
    {
      View localView = this.mContainer.getChildAt(paramInt1);
      int k;
      if (localView.getVisibility() != 0)
      {
        localObject1 = localObject2;
        k = paramInt2;
      }
      else
      {
        localView.getHitRect(localRect);
        if (localRect.contains(i, j)) {
          return localView;
        }
        if ((i >= localRect.left) && (i <= localRect.right)) {
          k = 0;
        } else {
          k = Math.min(Math.abs(localRect.left - i), Math.abs(i - localRect.right));
        }
        if ((j >= localRect.top) && (j <= localRect.bottom)) {
          m = 0;
        } else {
          m = Math.min(Math.abs(localRect.top - j), Math.abs(j - localRect.bottom));
        }
        int m = k * k + m * m;
        localObject1 = localObject2;
        k = paramInt2;
        if (m < this.mTouchPaddingScaledSq)
        {
          localObject1 = localObject2;
          k = paramInt2;
          if (m < paramInt2)
          {
            localObject1 = localView;
            k = m;
          }
        }
      }
      paramInt1--;
      localObject2 = localObject1;
      paramInt2 = k;
    }
    return (View)localObject2;
  }
  
  private boolean isInterestingKey(int paramInt)
  {
    if ((paramInt != 4) && (paramInt != 66)) {
      switch (paramInt)
      {
      default: 
        return false;
      }
    }
    return true;
  }
  
  private boolean onContainerKey(KeyEvent paramKeyEvent)
  {
    int i = paramKeyEvent.getKeyCode();
    if (isInterestingKey(i))
    {
      if (i == 4)
      {
        if ((paramKeyEvent.getAction() == 0) && (paramKeyEvent.getRepeatCount() == 0))
        {
          localObject = this.mOwnerView;
          if (localObject != null)
          {
            localObject = ((View)localObject).getKeyDispatcherState();
            if (localObject != null) {
              ((KeyEvent.DispatcherState)localObject).startTracking(paramKeyEvent, this);
            }
          }
          return true;
        }
        if ((paramKeyEvent.getAction() == 1) && (paramKeyEvent.isTracking()) && (!paramKeyEvent.isCanceled()))
        {
          setVisible(false);
          return true;
        }
      }
      else
      {
        dismissControlsDelayed(ZOOM_CONTROLS_TIMEOUT);
      }
      return false;
    }
    Object localObject = this.mOwnerView.getViewRootImpl();
    if (localObject != null) {
      ((ViewRootImpl)localObject).dispatchInputEvent(paramKeyEvent);
    }
    return true;
  }
  
  private void onPostConfigurationChanged()
  {
    dismissControlsDelayed(ZOOM_CONTROLS_TIMEOUT);
    refreshPositioningVariables();
  }
  
  private void refreshPositioningVariables()
  {
    if (this.mOwnerView.getWindowToken() == null) {
      return;
    }
    int i = this.mOwnerView.getHeight();
    int j = this.mOwnerView.getWidth();
    i -= this.mContainer.getHeight();
    this.mOwnerView.getLocationOnScreen(this.mOwnerViewRawLocation);
    Object localObject = this.mContainerRawLocation;
    int[] arrayOfInt = this.mOwnerViewRawLocation;
    localObject[0] = arrayOfInt[0];
    arrayOfInt[1] += i;
    arrayOfInt = this.mTempIntArray;
    this.mOwnerView.getLocationInWindow(arrayOfInt);
    localObject = this.mContainerLayoutParams;
    ((WindowManager.LayoutParams)localObject).x = arrayOfInt[0];
    ((WindowManager.LayoutParams)localObject).width = j;
    ((WindowManager.LayoutParams)localObject).y = (arrayOfInt[1] + i);
    if (this.mIsVisible) {
      this.mWindowManager.updateViewLayout(this.mContainer, (ViewGroup.LayoutParams)localObject);
    }
  }
  
  private void setTouchTargetView(View paramView)
  {
    this.mTouchTargetView = paramView;
    if (paramView != null) {
      paramView.getLocationInWindow(this.mTouchTargetWindowLocation);
    }
  }
  
  public ViewGroup getContainer()
  {
    return this.mContainer;
  }
  
  public View getZoomControls()
  {
    return this.mControls;
  }
  
  public boolean isAutoDismissed()
  {
    return this.mAutoDismissControls;
  }
  
  public boolean isVisible()
  {
    return this.mIsVisible;
  }
  
  public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    if (paramMotionEvent.getPointerCount() > 1) {
      return false;
    }
    if (this.mReleaseTouchListenerOnUp)
    {
      if ((i == 1) || (i == 3))
      {
        this.mOwnerView.setOnTouchListener(null);
        setTouchTargetView(null);
        this.mReleaseTouchListenerOnUp = false;
      }
      return true;
    }
    dismissControlsDelayed(ZOOM_CONTROLS_TIMEOUT);
    paramView = this.mTouchTargetView;
    if (i != 0)
    {
      if ((i == 1) || (i == 3)) {
        setTouchTargetView(null);
      }
    }
    else
    {
      paramView = findViewForTouch((int)paramMotionEvent.getRawX(), (int)paramMotionEvent.getRawY());
      setTouchTargetView(paramView);
    }
    if (paramView != null)
    {
      Object localObject = this.mContainerRawLocation;
      int j = localObject[0];
      int[] arrayOfInt = this.mTouchTargetWindowLocation;
      i = arrayOfInt[0];
      int k = localObject[1];
      int m = arrayOfInt[1];
      localObject = MotionEvent.obtain(paramMotionEvent);
      paramMotionEvent = this.mOwnerViewRawLocation;
      ((MotionEvent)localObject).offsetLocation(paramMotionEvent[0] - (j + i), paramMotionEvent[1] - (k + m));
      float f1 = ((MotionEvent)localObject).getX();
      float f2 = ((MotionEvent)localObject).getY();
      if ((f1 < 0.0F) && (f1 > -20.0F)) {
        ((MotionEvent)localObject).offsetLocation(-f1, 0.0F);
      }
      if ((f2 < 0.0F) && (f2 > -20.0F)) {
        ((MotionEvent)localObject).offsetLocation(0.0F, -f2);
      }
      boolean bool = paramView.dispatchTouchEvent((MotionEvent)localObject);
      ((MotionEvent)localObject).recycle();
      return bool;
    }
    return false;
  }
  
  public void setAutoDismissed(boolean paramBoolean)
  {
    if (this.mAutoDismissControls == paramBoolean) {
      return;
    }
    this.mAutoDismissControls = paramBoolean;
  }
  
  public void setFocusable(boolean paramBoolean)
  {
    int i = this.mContainerLayoutParams.flags;
    WindowManager.LayoutParams localLayoutParams;
    if (paramBoolean)
    {
      localLayoutParams = this.mContainerLayoutParams;
      localLayoutParams.flags &= 0xFFFFFFF7;
    }
    else
    {
      localLayoutParams = this.mContainerLayoutParams;
      localLayoutParams.flags |= 0x8;
    }
    if ((this.mContainerLayoutParams.flags != i) && (this.mIsVisible)) {
      this.mWindowManager.updateViewLayout(this.mContainer, this.mContainerLayoutParams);
    }
  }
  
  public void setOnZoomListener(OnZoomListener paramOnZoomListener)
  {
    this.mCallback = paramOnZoomListener;
  }
  
  public void setVisible(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (this.mOwnerView.getWindowToken() == null)
      {
        if (!this.mHandler.hasMessages(4)) {
          this.mHandler.sendEmptyMessage(4);
        }
        return;
      }
      dismissControlsDelayed(ZOOM_CONTROLS_TIMEOUT);
    }
    if (this.mIsVisible == paramBoolean) {
      return;
    }
    this.mIsVisible = paramBoolean;
    if (paramBoolean)
    {
      if (this.mContainerLayoutParams.token == null) {
        this.mContainerLayoutParams.token = this.mOwnerView.getWindowToken();
      }
      this.mWindowManager.addView(this.mContainer, this.mContainerLayoutParams);
      if (this.mPostedVisibleInitializer == null) {
        this.mPostedVisibleInitializer = new Runnable()
        {
          public void run()
          {
            ZoomButtonsController.this.refreshPositioningVariables();
            if (ZoomButtonsController.this.mCallback != null) {
              ZoomButtonsController.this.mCallback.onVisibilityChanged(true);
            }
          }
        };
      }
      this.mHandler.post(this.mPostedVisibleInitializer);
      this.mContext.registerReceiver(this.mConfigurationChangedReceiver, this.mConfigurationChangedFilter);
      this.mOwnerView.setOnTouchListener(this);
      this.mReleaseTouchListenerOnUp = false;
    }
    else
    {
      if (this.mTouchTargetView != null) {
        this.mReleaseTouchListenerOnUp = true;
      } else {
        this.mOwnerView.setOnTouchListener(null);
      }
      this.mContext.unregisterReceiver(this.mConfigurationChangedReceiver);
      this.mWindowManager.removeViewImmediate(this.mContainer);
      this.mHandler.removeCallbacks(this.mPostedVisibleInitializer);
      OnZoomListener localOnZoomListener = this.mCallback;
      if (localOnZoomListener != null) {
        localOnZoomListener.onVisibilityChanged(false);
      }
    }
  }
  
  public void setZoomInEnabled(boolean paramBoolean)
  {
    this.mControls.setIsZoomInEnabled(paramBoolean);
  }
  
  public void setZoomOutEnabled(boolean paramBoolean)
  {
    this.mControls.setIsZoomOutEnabled(paramBoolean);
  }
  
  public void setZoomSpeed(long paramLong)
  {
    this.mControls.setZoomSpeed(paramLong);
  }
  
  private class Container
    extends FrameLayout
  {
    public Container(Context paramContext)
    {
      super();
    }
    
    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
    {
      boolean bool;
      if (ZoomButtonsController.this.onContainerKey(paramKeyEvent)) {
        bool = true;
      } else {
        bool = super.dispatchKeyEvent(paramKeyEvent);
      }
      return bool;
    }
  }
  
  public static abstract interface OnZoomListener
  {
    public abstract void onVisibilityChanged(boolean paramBoolean);
    
    public abstract void onZoom(boolean paramBoolean);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ZoomButtonsController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */