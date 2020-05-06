package miui.slide;

import android.app.Activity;
import android.app.AppGlobals;
import android.app.Application;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.provider.MiuiSettings.Global;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewRootImpl;
import java.util.ArrayList;
import java.util.List;

public class SlideConfig
  implements Parcelable
{
  public static final boolean BOOLEAN_CONDITION_TRUE_FALSE = true;
  public static final Parcelable.Creator<SlideConfig> CREATOR = new Parcelable.Creator()
  {
    public SlideConfig createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SlideConfig(paramAnonymousParcel, null);
    }
    
    public SlideConfig[] newArray(int paramAnonymousInt)
    {
      return new SlideConfig[paramAnonymousInt];
    }
  };
  public static final int FLAG_ACTION_BACK = 8;
  public static final int FLAG_ACTION_CLICK_CLASSNAME = 2;
  public static final int FLAG_ACTION_CLICK_VIEW = 16;
  public static final int FLAG_ACTION_CLICK_VIEWID = 1;
  public static final int FLAG_ACTION_TOUCH_POSITION = 4;
  public static final int FLAG_CONDITION_AUDIO_COMMUNICATION = 4;
  public static final int FLAG_CONDITION_AUDIO_NOT_RECORDING = 5;
  public static final int FLAG_CONDITION_BACK_CAMERA_OPEN = 3;
  public static final int FLAG_CONDITION_CAMERA_OPEN = 1;
  public static final int FLAG_CONDITION_FRONT_CAMERA_OPEN = 2;
  public static final int FLAG_RESULT_GOTO_ACTIVITY = 1;
  public static final int FLAG_RESULT_OPEN_AUDIO = 4;
  public static final int FLAG_RESULT_OPEN_CAMERA = 2;
  public static final String TAG = "SlideConfig";
  final int DEFAULT_EDGE_FLAGS;
  final int DEFAULT_META_STATE;
  final float DEFAULT_PRECISION_X;
  final float DEFAULT_PRECISION_Y;
  final float DEFAULT_SIZE = 1.0F;
  public boolean mConditionTrueFalse;
  public int mFlagAction;
  public int mFlagCondition;
  public int mFlagResult;
  public int mKeyCode;
  public String mStartingActivity;
  public String mTargetActivity;
  public List<TouchEventConfig> mTouchEventConfigList = new ArrayList();
  public int mVersionCode;
  public String mViewClassName;
  public String mViewID;
  
  public SlideConfig()
  {
    this.DEFAULT_META_STATE = 0;
    this.DEFAULT_PRECISION_X = 1.0F;
    this.DEFAULT_PRECISION_Y = 1.0F;
    this.DEFAULT_EDGE_FLAGS = 0;
  }
  
  public SlideConfig(int paramInt1, int paramInt2, String paramString1, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, String paramString2, String paramString3, List<TouchEventConfig> paramList, String paramString4)
  {
    this.DEFAULT_META_STATE = 0;
    this.DEFAULT_PRECISION_X = 1.0F;
    this.DEFAULT_PRECISION_Y = 1.0F;
    this.DEFAULT_EDGE_FLAGS = 0;
    this.mKeyCode = paramInt1;
    this.mVersionCode = paramInt2;
    this.mStartingActivity = paramString1;
    this.mFlagAction = paramInt3;
    this.mFlagResult = paramInt4;
    this.mFlagCondition = paramInt5;
    this.mConditionTrueFalse = paramBoolean;
    this.mViewID = paramString2;
    this.mViewClassName = paramString3;
    this.mTouchEventConfigList = paramList;
    this.mTargetActivity = paramString4;
  }
  
  private SlideConfig(Parcel paramParcel)
  {
    boolean bool = false;
    this.DEFAULT_META_STATE = 0;
    this.DEFAULT_PRECISION_X = 1.0F;
    this.DEFAULT_PRECISION_Y = 1.0F;
    this.DEFAULT_EDGE_FLAGS = 0;
    this.mKeyCode = paramParcel.readInt();
    this.mVersionCode = paramParcel.readInt();
    this.mStartingActivity = paramParcel.readString();
    this.mFlagAction = paramParcel.readInt();
    this.mFlagResult = paramParcel.readInt();
    this.mFlagCondition = paramParcel.readInt();
    if (paramParcel.readInt() == 1) {
      bool = true;
    }
    this.mConditionTrueFalse = bool;
    this.mViewID = paramParcel.readString();
    this.mViewClassName = paramParcel.readString();
    paramParcel.readTypedList(this.mTouchEventConfigList, TouchEventConfig.CREATOR);
    this.mTargetActivity = paramParcel.readString();
  }
  
  private int getInputDeviceId(int paramInt)
  {
    for (int k : ) {
      if (InputDevice.getDevice(k).supportsSource(paramInt)) {
        return k;
      }
    }
    return 0;
  }
  
  private void injectBackKey(View paramView)
  {
    long l = SystemClock.uptimeMillis();
    KeyEvent localKeyEvent = new KeyEvent(l, l, 0, 4, 0, 0, -1, 0, 0, 257);
    paramView.getViewRootImpl().dispatchInputEvent(localKeyEvent);
    localKeyEvent = new KeyEvent(l, l, 1, 4, 0, 0, -1, 0, 0, 257);
    paramView.getViewRootImpl().dispatchInputEvent(localKeyEvent);
  }
  
  private void injectMotionEvent(View paramView, int paramInt1, int paramInt2)
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("TouchEvent: ");
    ((StringBuilder)localObject).append(paramInt1);
    ((StringBuilder)localObject).append("-");
    ((StringBuilder)localObject).append(paramInt2);
    Log.d("SlideConfig", ((StringBuilder)localObject).toString());
    long l = SystemClock.uptimeMillis();
    localObject = MotionEvent.obtain(l, l, 0, paramInt1, paramInt2, 1.0F, 1.0F, 0, 1.0F, 1.0F, getInputDeviceId(4098), 0);
    ((MotionEvent)localObject).setSource(4098);
    paramView.getViewRootImpl().dispatchInputEvent((InputEvent)localObject);
    localObject = MotionEvent.obtain(l, l, 1, paramInt1, paramInt2, 0.0F, 1.0F, 0, 1.0F, 1.0F, getInputDeviceId(4098), 0);
    ((MotionEvent)localObject).setSource(4098);
    paramView.getViewRootImpl().dispatchInputEvent((InputEvent)localObject);
  }
  
  public boolean checkCondition()
  {
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    boolean bool5 = false;
    try
    {
      ISlideManagerService localISlideManagerService = ISlideManagerService.Stub.asInterface(ServiceManager.getService("miui.slide.SlideManagerService"));
      int i = this.mFlagCondition;
      if (i != 1)
      {
        if (i != 2)
        {
          if (i != 3)
          {
            if (i != 4)
            {
              if (i != 5) {
                return true;
              }
              i = localISlideManagerService.getCameraStatus();
              bool1 = bool5;
              if (i != 0)
              {
                bool1 = bool5;
                if ((i & 0x4) == 0) {
                  bool1 = true;
                }
              }
              return bool1;
            }
            if (((AudioManager)AppGlobals.getInitialApplication().getSystemService("audio")).getMode() == 3) {
              bool1 = true;
            }
            return bool1;
          }
          bool1 = bool2;
          if ((localISlideManagerService.getCameraStatus() & 0x2) != 0) {
            bool1 = true;
          }
          return bool1;
        }
        bool1 = bool3;
        if ((localISlideManagerService.getCameraStatus() & 0x1) != 0) {
          bool1 = true;
        }
        return bool1;
      }
      i = localISlideManagerService.getCameraStatus();
      bool1 = bool4;
      if (i != 0) {
        bool1 = true;
      }
      return bool1;
    }
    catch (Exception localException)
    {
      Slog.d("SlideConfig", localException.getMessage());
    }
    return false;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean hasActionFlag(int paramInt)
  {
    boolean bool;
    if ((this.mFlagAction & paramInt) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("SlideConfig{, mKeyCode=");
    localStringBuilder.append(this.mKeyCode);
    localStringBuilder.append(", mVersionCode=");
    localStringBuilder.append(this.mVersionCode);
    localStringBuilder.append(", mStartingActivity=");
    localStringBuilder.append(this.mStartingActivity);
    localStringBuilder.append(", mFlagAction=");
    localStringBuilder.append(this.mFlagAction);
    localStringBuilder.append(", mFlagResult=");
    localStringBuilder.append(this.mFlagResult);
    localStringBuilder.append(", mFlagCondition=");
    localStringBuilder.append(this.mFlagCondition);
    localStringBuilder.append(", mConditionTrueFalse=");
    localStringBuilder.append(this.mConditionTrueFalse);
    localStringBuilder.append(", mViewID=");
    localStringBuilder.append(this.mViewID);
    localStringBuilder.append(", mViewClassName=");
    localStringBuilder.append(this.mViewClassName);
    localStringBuilder.append(", mTargetActivity=");
    localStringBuilder.append(this.mTargetActivity);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public int tryGotoTarget(Activity paramActivity, View paramView)
  {
    boolean bool = checkCondition();
    if ((this.mConditionTrueFalse ^ bool)) {
      return 0;
    }
    if (hasActionFlag(8))
    {
      Log.d("SlideConfig", "FLAG_ACTION_BACK");
      if (paramView != null)
      {
        injectBackKey(paramView);
        return 8;
      }
    }
    int i;
    Object localObject;
    if (hasActionFlag(1))
    {
      Log.d("SlideConfig", "FLAG_ACTION_CLICK_VIEWID");
      if ((!TextUtils.isEmpty(this.mViewID)) && (paramActivity.getResources() != null))
      {
        i = paramActivity.getResources().getIdentifier(this.mViewID, "id", paramActivity.getBasePackageName());
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("resId: ");
        ((StringBuilder)localObject).append(i);
        Log.d("SlideConfig", ((StringBuilder)localObject).toString());
        localObject = paramActivity.findViewById(i);
        if ((localObject != null) && (((View)localObject).isVisibleToUser()))
        {
          Log.d("SlideConfig", "Target found by resId");
          ((View)localObject).performClick();
          return 1;
        }
      }
    }
    if (hasActionFlag(4))
    {
      Log.d("SlideConfig", "FLAG_ACTION_TOUCH_POSITION");
      if (paramView != null)
      {
        bool = MiuiSettings.Global.getBoolean(paramActivity.getContentResolver(), "force_fsg_nav_bar");
        localObject = this.mTouchEventConfigList;
        if ((localObject != null) && (((List)localObject).size() > 0))
        {
          localObject = (TouchEventConfig)this.mTouchEventConfigList.get(0);
          if (bool) {
            i = ((TouchEventConfig)localObject).mPositionBetaX;
          } else {
            i = ((TouchEventConfig)localObject).mPositionX;
          }
          int j;
          if (bool) {
            j = ((TouchEventConfig)localObject).mPositionBetaY;
          } else {
            j = ((TouchEventConfig)localObject).mPositionY;
          }
          injectMotionEvent(paramView, i, j);
          i = ((TouchEventConfig)localObject).mWaitingTime;
          if ((this.mTouchEventConfigList.size() > 1) && (i > 0))
          {
            localObject = (TouchEventConfig)this.mTouchEventConfigList.get(1);
            paramActivity.getMainThreadHandler().postDelayed(new _..Lambda.SlideConfig.OWrUjwHDwalS9A4Tr0PKIikWk3I(this, paramView, bool, (TouchEventConfig)localObject), i);
          }
        }
        return 4;
      }
    }
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mKeyCode);
    paramParcel.writeInt(this.mVersionCode);
    paramParcel.writeString(this.mStartingActivity);
    paramParcel.writeInt(this.mFlagAction);
    paramParcel.writeInt(this.mFlagResult);
    paramParcel.writeInt(this.mFlagCondition);
    paramParcel.writeInt(this.mConditionTrueFalse);
    paramParcel.writeString(this.mViewID);
    paramParcel.writeString(this.mViewClassName);
    paramParcel.writeTypedList(this.mTouchEventConfigList);
    paramParcel.writeString(this.mTargetActivity);
  }
  
  public static class TouchEventConfig
    implements Parcelable
  {
    public static final Parcelable.Creator<TouchEventConfig> CREATOR = new Parcelable.Creator()
    {
      public SlideConfig.TouchEventConfig createFromParcel(Parcel paramAnonymousParcel)
      {
        return new SlideConfig.TouchEventConfig(paramAnonymousParcel, null);
      }
      
      public SlideConfig.TouchEventConfig[] newArray(int paramAnonymousInt)
      {
        return new SlideConfig.TouchEventConfig[paramAnonymousInt];
      }
    };
    public int mPositionBetaX;
    public int mPositionBetaY;
    public int mPositionX;
    public int mPositionY;
    public int mWaitingTime;
    
    public TouchEventConfig() {}
    
    public TouchEventConfig(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      this.mPositionX = paramInt1;
      this.mPositionY = paramInt2;
      this.mPositionBetaX = paramInt3;
      this.mPositionBetaY = paramInt4;
      this.mWaitingTime = paramInt5;
    }
    
    private TouchEventConfig(Parcel paramParcel)
    {
      this.mPositionX = paramParcel.readInt();
      this.mPositionY = paramParcel.readInt();
      this.mPositionBetaX = paramParcel.readInt();
      this.mPositionBetaY = paramParcel.readInt();
      this.mWaitingTime = paramParcel.readInt();
    }
    
    public TouchEventConfig(TouchEventConfig paramTouchEventConfig)
    {
      this.mPositionX = paramTouchEventConfig.mPositionX;
      this.mPositionY = paramTouchEventConfig.mPositionY;
      this.mPositionBetaX = paramTouchEventConfig.mPositionBetaX;
      this.mPositionBetaY = paramTouchEventConfig.mPositionBetaY;
      this.mWaitingTime = paramTouchEventConfig.mWaitingTime;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("TouchEventConfig{, mPositionX=");
      localStringBuilder.append(this.mPositionX);
      localStringBuilder.append(", mPositionY=");
      localStringBuilder.append(this.mPositionY);
      localStringBuilder.append(", mPositionBetaX=");
      localStringBuilder.append(this.mPositionBetaX);
      localStringBuilder.append(", mPositionBetaY=");
      localStringBuilder.append(this.mPositionBetaY);
      localStringBuilder.append(", mWaitingTime=");
      localStringBuilder.append(this.mWaitingTime);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mPositionX);
      paramParcel.writeInt(this.mPositionY);
      paramParcel.writeInt(this.mPositionBetaX);
      paramParcel.writeInt(this.mPositionBetaY);
      paramParcel.writeInt(this.mWaitingTime);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/slide/SlideConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */