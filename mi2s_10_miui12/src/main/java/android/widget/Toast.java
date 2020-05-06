package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.app.ITransientNotification.Stub;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Toast
{
  public static final int LENGTH_LONG = 1;
  public static final int LENGTH_SHORT = 0;
  static final String TAG = "Toast";
  static final boolean localLOGV = false;
  @UnsupportedAppUsage(maxTargetSdk=28)
  private static INotificationManager sService;
  final Context mContext;
  @UnsupportedAppUsage
  int mDuration;
  View mNextView;
  @UnsupportedAppUsage(maxTargetSdk=28)
  final TN mTN;
  
  public Toast(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public Toast(Context paramContext, Looper paramLooper)
  {
    this.mContext = paramContext;
    this.mTN = new TN(paramContext.getPackageName(), paramLooper);
    this.mTN.mY = paramContext.getResources().getDimensionPixelSize(17105525);
    this.mTN.mGravity = paramContext.getResources().getInteger(17694904);
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  private static INotificationManager getService()
  {
    INotificationManager localINotificationManager = sService;
    if (localINotificationManager != null) {
      return localINotificationManager;
    }
    sService = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    return sService;
  }
  
  public static Toast makeText(Context paramContext, int paramInt1, int paramInt2)
    throws Resources.NotFoundException
  {
    return makeText(paramContext, paramContext.getResources().getText(paramInt1), paramInt2);
  }
  
  public static Toast makeText(Context paramContext, Looper paramLooper, CharSequence paramCharSequence, int paramInt)
  {
    Toast localToast = new Toast(paramContext, paramLooper);
    paramLooper = ((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(17367351, null);
    ((TextView)paramLooper.findViewById(16908299)).setText(ToastInjector.addAppName(paramContext, paramCharSequence));
    localToast.mNextView = paramLooper;
    localToast.mDuration = paramInt;
    return localToast;
  }
  
  public static Toast makeText(Context paramContext, CharSequence paramCharSequence, int paramInt)
  {
    return makeText(paramContext, null, paramCharSequence, paramInt);
  }
  
  public void cancel()
  {
    this.mTN.cancel();
  }
  
  public int getDuration()
  {
    return this.mDuration;
  }
  
  public int getGravity()
  {
    return this.mTN.mGravity;
  }
  
  public float getHorizontalMargin()
  {
    return this.mTN.mHorizontalMargin;
  }
  
  public float getVerticalMargin()
  {
    return this.mTN.mVerticalMargin;
  }
  
  public View getView()
  {
    return this.mNextView;
  }
  
  @UnsupportedAppUsage
  public WindowManager.LayoutParams getWindowParams()
  {
    return this.mTN.mParams;
  }
  
  public int getXOffset()
  {
    return this.mTN.mX;
  }
  
  public int getYOffset()
  {
    return this.mTN.mY;
  }
  
  public void setDuration(int paramInt)
  {
    this.mDuration = paramInt;
    this.mTN.mDuration = paramInt;
  }
  
  public void setGravity(int paramInt1, int paramInt2, int paramInt3)
  {
    TN localTN = this.mTN;
    localTN.mGravity = paramInt1;
    localTN.mX = paramInt2;
    localTN.mY = paramInt3;
  }
  
  public void setMargin(float paramFloat1, float paramFloat2)
  {
    TN localTN = this.mTN;
    localTN.mHorizontalMargin = paramFloat1;
    localTN.mVerticalMargin = paramFloat2;
  }
  
  public void setText(int paramInt)
  {
    setText(this.mContext.getText(paramInt));
  }
  
  public void setText(CharSequence paramCharSequence)
  {
    Object localObject = this.mNextView;
    if (localObject != null)
    {
      localObject = (TextView)((View)localObject).findViewById(16908299);
      if (localObject != null)
      {
        ((TextView)localObject).setText(paramCharSequence);
        return;
      }
      throw new RuntimeException("This Toast was not created with Toast.makeText()");
    }
    throw new RuntimeException("This Toast was not created with Toast.makeText()");
  }
  
  public void setType(int paramInt)
  {
    this.mTN.mParams.type = paramInt;
  }
  
  public void setView(View paramView)
  {
    this.mNextView = paramView;
  }
  
  public void show()
  {
    if (this.mNextView != null)
    {
      INotificationManager localINotificationManager = getService();
      String str = this.mContext.getOpPackageName();
      TN localTN = this.mTN;
      localTN.mNextView = this.mNextView;
      int i = this.mContext.getDisplayId();
      try
      {
        localINotificationManager.enqueueToast(str, localTN, this.mDuration, i);
      }
      catch (RemoteException localRemoteException) {}
      return;
    }
    throw new RuntimeException("setView must have been called");
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Duration {}
  
  private static class TN
    extends ITransientNotification.Stub
  {
    private static final int CANCEL = 2;
    private static final int HIDE = 1;
    static final long LONG_DURATION_TIMEOUT = 7000L;
    static final long SHORT_DURATION_TIMEOUT = 4000L;
    private static final int SHOW = 0;
    int mDuration;
    @UnsupportedAppUsage(maxTargetSdk=28)
    int mGravity;
    final Handler mHandler;
    float mHorizontalMargin;
    @UnsupportedAppUsage(maxTargetSdk=28)
    View mNextView;
    String mPackageName;
    @UnsupportedAppUsage(maxTargetSdk=28)
    final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    float mVerticalMargin;
    @UnsupportedAppUsage(maxTargetSdk=28)
    View mView;
    WindowManager mWM;
    int mX;
    @UnsupportedAppUsage(maxTargetSdk=28)
    int mY;
    
    TN(String paramString, Looper paramLooper)
    {
      WindowManager.LayoutParams localLayoutParams = this.mParams;
      localLayoutParams.height = -2;
      localLayoutParams.width = -2;
      localLayoutParams.format = -3;
      localLayoutParams.windowAnimations = 16973828;
      localLayoutParams.type = 2005;
      localLayoutParams.setTitle("Toast");
      localLayoutParams.flags = 152;
      this.mPackageName = paramString;
      paramString = paramLooper;
      if (paramLooper == null)
      {
        paramString = Looper.myLooper();
        if (paramString == null) {
          throw new RuntimeException("Can't toast on a thread that has not called Looper.prepare()");
        }
      }
      this.mHandler = new Handler(paramString, null)
      {
        public void handleMessage(Message paramAnonymousMessage)
        {
          int i = paramAnonymousMessage.what;
          if (i != 0)
          {
            if (i != 1)
            {
              if (i == 2)
              {
                Toast.TN.this.handleHide();
                Toast.TN.this.mNextView = null;
                try
                {
                  Toast.access$000().cancelToast(Toast.TN.this.mPackageName, Toast.TN.this);
                }
                catch (RemoteException paramAnonymousMessage) {}
              }
            }
            else
            {
              Toast.TN.this.handleHide();
              Toast.TN.this.mNextView = null;
            }
          }
          else
          {
            paramAnonymousMessage = (IBinder)paramAnonymousMessage.obj;
            Toast.TN.this.handleShow(paramAnonymousMessage);
          }
        }
      };
    }
    
    private void trySendAccessibilityEvent()
    {
      AccessibilityManager localAccessibilityManager = AccessibilityManager.getInstance(this.mView.getContext());
      if (!localAccessibilityManager.isEnabled()) {
        return;
      }
      AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(64);
      localAccessibilityEvent.setClassName(getClass().getName());
      localAccessibilityEvent.setPackageName(this.mView.getContext().getPackageName());
      this.mView.dispatchPopulateAccessibilityEvent(localAccessibilityEvent);
      localAccessibilityManager.sendAccessibilityEvent(localAccessibilityEvent);
    }
    
    public void cancel()
    {
      this.mHandler.obtainMessage(2).sendToTarget();
    }
    
    @UnsupportedAppUsage
    public void handleHide()
    {
      View localView = this.mView;
      if (localView != null)
      {
        if (localView.getParent() != null) {
          this.mWM.removeViewImmediate(this.mView);
        }
        try
        {
          Toast.access$000().finishToken(this.mPackageName, this);
        }
        catch (RemoteException localRemoteException) {}
        this.mView = null;
      }
    }
    
    public void handleShow(IBinder paramIBinder)
    {
      if ((!this.mHandler.hasMessages(2)) && (!this.mHandler.hasMessages(1)))
      {
        if (this.mView != this.mNextView)
        {
          handleHide();
          this.mView = this.mNextView;
          Object localObject1 = this.mView.getContext().getApplicationContext();
          String str = this.mView.getContext().getOpPackageName();
          Object localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = this.mView.getContext();
          }
          this.mWM = ((WindowManager)((Context)localObject2).getSystemService("window"));
          localObject1 = this.mView.getContext().getResources().getConfiguration();
          int i = Gravity.getAbsoluteGravity(this.mGravity, ((Configuration)localObject1).getLayoutDirection());
          localObject1 = this.mParams;
          ((WindowManager.LayoutParams)localObject1).gravity = i;
          if ((i & 0x7) == 7) {
            ((WindowManager.LayoutParams)localObject1).horizontalWeight = 1.0F;
          }
          if ((i & 0x70) == 112) {
            this.mParams.verticalWeight = 1.0F;
          }
          localObject1 = this.mParams;
          ((WindowManager.LayoutParams)localObject1).x = this.mX;
          ((WindowManager.LayoutParams)localObject1).y = this.mY;
          ((WindowManager.LayoutParams)localObject1).verticalMargin = this.mVerticalMargin;
          ((WindowManager.LayoutParams)localObject1).horizontalMargin = this.mHorizontalMargin;
          ((WindowManager.LayoutParams)localObject1).packageName = str;
          long l;
          if (this.mDuration == 1) {
            l = 7000L;
          } else {
            l = 4000L;
          }
          ((WindowManager.LayoutParams)localObject1).hideTimeoutMilliseconds = l;
          this.mParams.token = paramIBinder;
          if (this.mView.getParent() != null) {
            this.mWM.removeView(this.mView);
          }
          try
          {
            this.mWM.addView(this.mView, this.mParams);
            trySendAccessibilityEvent();
          }
          catch (WindowManager.BadTokenException paramIBinder) {}
          paramIBinder = new StringBuilder();
          paramIBinder.append("Show toast from OpPackageName:");
          paramIBinder.append(str);
          paramIBinder.append(", PackageName:");
          paramIBinder.append(((Context)localObject2).getPackageName());
          Log.i("Toast", paramIBinder.toString());
        }
        return;
      }
    }
    
    public void hide()
    {
      this.mHandler.obtainMessage(1).sendToTarget();
    }
    
    @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
    public void show(IBinder paramIBinder)
    {
      this.mHandler.obtainMessage(0, paramIBinder).sendToTarget();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/Toast.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */