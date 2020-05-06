package miui.slide;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.view.Display;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.os.SomeArgs;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import miui.os.MiuiBoosterClient;
import miui.process.IActivityChangeListener.Stub;

public class SlideCoverEventManager
{
  private static final String FIRST_FRONT_CAMERA_OPEN = "first_front_camera_open";
  private static final String GAME_BOOST_SEGMENT_NAME = "gb_boosting";
  private static final int MSG_ACTIVITY_CHANGED = 101;
  private static final int MSG_FRONT_CAMERA_OPEN_STATUS = 102;
  private static final int MSG_INIT_OTHER_INFO = 100;
  private static final int SLIDER_FIRST_TIP_SHOW = 0;
  private static final int SLIDER_SECOND_TIP_SHOW = 2;
  private static final String SLIDE_COVER_EVENT_STATUS = "sc_event_status";
  public static final String TAG = "SlideCoverEventManager";
  private static final String USER_SETUP_COMPLETE = "user_setup_complete";
  private static final Object sCallBackLock;
  private static SlideCoverEventManager sInstance;
  private static ArrayList<String> sListenerWhiteList;
  private static final Object sLock = new Object();
  private IActivityChangeListener.Stub mActivityListener = new IActivityChangeListener.Stub()
  {
    public void onActivityChanged(ComponentName paramAnonymousComponentName1, ComponentName paramAnonymousComponentName2)
    {
      if ((paramAnonymousComponentName1 != null) && (paramAnonymousComponentName2 != null))
      {
        SomeArgs localSomeArgs = SomeArgs.obtain();
        localSomeArgs.arg1 = paramAnonymousComponentName1;
        localSomeArgs.arg2 = paramAnonymousComponentName2;
        SlideCoverEventManager.this.mHandler.obtainMessage(101, localSomeArgs).sendToTarget();
        return;
      }
    }
  };
  private SlideAnimationController mAnimationController;
  private int mAnswerCallCount;
  private SlideCallbacks mCallBacks;
  private SlideCameraMonitor.CameraOpenListener mCameraOpenListener = new SlideCameraMonitor.CameraOpenListener()
  {
    public void onCameraClose(int paramAnonymousInt)
    {
      if (SlideCameraMonitor.getInstance().getFrontCameraID().contains(Integer.valueOf(paramAnonymousInt)))
      {
        SlideCoverEventManager.access$302(SlideCoverEventManager.this, false);
        SlideCoverEventManager.this.hideTipsView();
      }
    }
    
    public void onCameraOpen(int paramAnonymousInt)
    {
      if (SlideCameraMonitor.getInstance().getFrontCameraID().contains(Integer.valueOf(paramAnonymousInt)))
      {
        SlideCoverEventManager.access$302(SlideCoverEventManager.this, true);
        if (SlideCoverEventManager.this.mSlideCoverStatus == 1) {
          SlideCoverEventManager.this.bindSliderViewServiceDelay();
        } else {
          SlideCoverEventManager.this.hideTipsView();
        }
      }
    }
  };
  private ServiceConnection mConn = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      Slog.i("SlideCoverEventManager", "onServiceConnected");
      SlideCoverEventManager.access$702(SlideCoverEventManager.this, ISliderViewService.Stub.asInterface(paramAnonymousIBinder));
      SlideCoverEventManager.this.showTipsView();
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      Slog.d("SlideCoverEventManager", "onServiceDisconnected");
      SlideCoverEventManager.access$702(SlideCoverEventManager.this, null);
      SlideCoverEventManager.this.bindSliderView();
    }
  };
  private Context mContext;
  private int mCurrentUserId = 0;
  private boolean mFirstEvent = true;
  private boolean mFirstFrontCameraOpen;
  private ArrayList<String> mForbiddenActivities;
  private ComponentName mForegroundComponent;
  private boolean mFrontCameraOpening;
  private boolean mGameBoostMode;
  private int mGameBoosterCount;
  private Handler mHandler;
  private boolean mInDriveMode;
  private boolean mInitMonitor;
  private int mLaunchAppCount;
  private int mLaunchCameraCount;
  private int mLaunchPanelCount;
  private String mLaunchPkg;
  private boolean mOnForbiddenActivity;
  private boolean mOnMiuiAdjustActivity;
  private boolean mOnTargetApps;
  private boolean mPhoneFloating;
  private boolean mPhoneForeground;
  private PhoneStateListener mPhoneListener;
  private int mPhoneState;
  private PowerManager mPowerManager;
  private ComponentName mPreComponent;
  private BroadcastReceiver mReceiver;
  private SettingsObserver mSettingsObserver;
  private boolean mSetupCompleted;
  private boolean mShowingTipsView;
  private int mSlideChoice;
  private int mSlideCoverStatus = -1;
  private SlideCoverListener mSliderListener;
  private ISliderViewService mSliderViewService;
  private boolean mSoundEnable;
  private TelecomManager mTelecomManager;
  private int mUseOnAdaptedAppCount;
  private int mWakeUpCount;
  private IWindowManager mWindowManager;
  
  static
  {
    sCallBackLock = new Object();
    sListenerWhiteList = new ArrayList();
    sListenerWhiteList.add("gamebooster");
    sListenerWhiteList.add("sliderpanel");
  }
  
  public SlideCoverEventManager(SlideCoverListener paramSlideCoverListener, Context paramContext, Looper paramLooper)
  {
    this.mSliderListener = paramSlideCoverListener;
    this.mContext = paramContext;
    this.mHandler = new H(paramLooper);
    this.mCallBacks = new SlideCallbacks(this.mHandler.getLooper());
    sInstance = this;
  }
  
  private void bindSliderView()
  {
    Slog.d("SlideCoverEventManager", "bindSliderView");
    Intent localIntent = new Intent();
    localIntent.setAction("com.android.systemui.sliderview.SliderViewService");
    localIntent.setPackage("com.android.systemui");
    this.mContext.bindServiceAsUser(localIntent, this.mConn, 1, this.mHandler, UserHandle.CURRENT);
  }
  
  private void bindSliderViewServiceDelay()
  {
    Object localObject = this.mForegroundComponent;
    if (((localObject != null) && ("com.android.keyguard.settings.MiuiNormalCameraFaceInput".equals(((ComponentName)localObject).getClassName()))) || (this.mShowingTipsView)) {
      return;
    }
    this.mHandler.removeMessages(102);
    localObject = this.mHandler.obtainMessage(102);
    this.mHandler.sendMessageDelayed((Message)localObject, 2000L);
  }
  
  public static SlideCoverEventManager getInstance()
  {
    return sInstance;
  }
  
  private void handleBindSliderView()
  {
    this.mHandler.removeMessages(102);
    if (this.mShowingTipsView) {
      return;
    }
    if (this.mSliderViewService != null)
    {
      showTipsView();
      return;
    }
    bindSliderView();
  }
  
  private boolean handleCamera(int paramInt)
  {
    if (handleMiuiAdjustApp(paramInt))
    {
      this.mUseOnAdaptedAppCount += 1;
      return true;
    }
    if ((paramInt == 0) && (!this.mOnTargetApps)) {
      if (SlideCameraMonitor.getInstance().isCameraOpening())
      {
        ComponentName localComponentName = this.mPreComponent;
        if ((localComponentName == null) || (!localComponentName.getPackageName().equals("com.android.camera"))) {}
      }
      else
      {
        launchCamera();
        this.mLaunchCameraCount += 1;
        return false;
      }
    }
    return true;
  }
  
  private boolean handleChoice(int paramInt)
  {
    Object localObject = this.mForegroundComponent;
    boolean bool = false;
    if ((localObject != null) && (((ComponentName)localObject).getPackageName().equals("com.android.camera"))) {
      return false;
    }
    if (this.mOnMiuiAdjustActivity) {
      return false;
    }
    if (this.mFrontCameraOpening)
    {
      if (this.mSlideChoice != 1) {
        bool = true;
      }
      return bool;
    }
    int i = this.mSlideChoice;
    if (i == 0) {
      return true;
    }
    if ((paramInt == 0) && (i == 3))
    {
      localObject = this.mLaunchPkg;
      if (localObject != null)
      {
        localObject = SlideUtils.getLaunchIntentForPackageAsUser((String)localObject, this.mCurrentUserId);
        if (localObject == null)
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("error to launch ");
          ((StringBuilder)localObject).append(this.mLaunchPkg);
          Slog.d("SlideCoverEventManager", ((StringBuilder)localObject).toString());
          return true;
        }
        this.mContext.startActivityAsUser((Intent)localObject, UserHandle.CURRENT);
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("launch ");
        ((StringBuilder)localObject).append(this.mLaunchPkg);
        Slog.d("SlideCoverEventManager", ((StringBuilder)localObject).toString());
        this.mLaunchAppCount += 1;
        return true;
      }
    }
    return this.mCallBacks.notifyStatusChanged(paramInt);
  }
  
  private boolean handleKeyGuard(int paramInt)
  {
    if ((paramInt == 0) && (isKeyguardLocked()))
    {
      Slog.d("SlideCoverEventManager", "event to be handled by keyguard");
      return true;
    }
    return false;
  }
  
  private boolean handleMiuiAdjustApp(int paramInt)
  {
    return this.mOnMiuiAdjustActivity;
  }
  
  private boolean handlePhone(int paramInt)
  {
    int i = this.mPhoneState;
    if (i == 2) {
      return true;
    }
    if ((paramInt == 0) && (i == 1) && ((this.mPhoneForeground) || (this.mPhoneFloating) || (this.mInDriveMode))) {
      try
      {
        Slog.d("SlideCoverEventManager", "answer a call");
        this.mTelecomManager.acceptRingingCall();
        this.mAnswerCallCount += 1;
        return true;
      }
      catch (Exception localException)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("error to answer a call:");
        localStringBuilder.append(localException.toString());
        Slog.d("SlideCoverEventManager", localStringBuilder.toString());
      }
    }
    return false;
  }
  
  private void handleSlideCoverAnimation(int paramInt)
  {
    this.mAnimationController.showView(paramInt);
    handleSlideCoverSound(paramInt);
  }
  
  private void handleSlideCoverSound(int paramInt)
  {
    if (!this.mSoundEnable) {
      return;
    }
    ISliderViewService localISliderViewService = this.mSliderViewService;
    if (localISliderViewService != null) {
      try
      {
        localISliderViewService.playSound(paramInt);
      }
      catch (RemoteException localRemoteException)
      {
        Slog.d("SlideCoverEventManager", "error to play sound");
      }
    }
  }
  
  private void handleSystem(int paramInt)
  {
    updateEventStatus(paramInt);
    if (paramInt == 0)
    {
      PowerManager localPowerManager = this.mPowerManager;
      if (localPowerManager != null)
      {
        if (!localPowerManager.isInteractive()) {
          this.mWakeUpCount += 1;
        }
        this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), "android.policy:SLIDE");
      }
      hideTipsView();
    }
    handleSlideCoverAnimation(paramInt);
    if ((paramInt == 1) && (this.mFrontCameraOpening)) {
      bindSliderViewServiceDelay();
    }
  }
  
  private boolean handleWechatHardCoder(int paramInt)
  {
    ComponentName localComponentName = this.mForegroundComponent;
    if ((localComponentName != null) && ("com.tencent.mm".equals(localComponentName.getPackageName())) && ("com.tencent.mm.plugin.voip.ui.VideoActivity".equals(this.mForegroundComponent.getClassName())))
    {
      if (!SystemProperties.getBoolean("sys.hardcoder.registered", false)) {
        return false;
      }
      if (paramInt == 0) {
        return MiuiBoosterClient.getInstance().writeEvent(1);
      }
      if (paramInt == 1) {
        return MiuiBoosterClient.getInstance().writeEvent(2);
      }
      return true;
    }
    return false;
  }
  
  private void hideTipsView()
  {
    synchronized (sLock)
    {
      this.mHandler.removeMessages(102);
      try
      {
        if ((this.mSliderViewService != null) && (this.mShowingTipsView))
        {
          Slog.d("SlideCoverEventManager", "removeSliderView");
          this.mSliderViewService.removeSliderView(1);
        }
      }
      catch (RemoteException localRemoteException)
      {
        localRemoteException.printStackTrace();
      }
      this.mShowingTipsView = false;
      return;
    }
  }
  
  private void initOtherInfo()
  {
    SlideCloudConfigHelper.getInstance().setActivityChangeListener(this.mActivityListener);
    this.mForbiddenActivities = new ArrayList();
    this.mForbiddenActivities.add("com.android.settings.faceunlock.MiuiFaceDataInput");
    this.mForbiddenActivities.add("com.android.settings.faceunlock.MiuiNormalCameraFaceInput");
    this.mForbiddenActivities.add("com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput");
    this.mForbiddenActivities.add("com.android.settings.faceunlock.MiuiFaceDataIntroduction");
    Object localObject = this.mContext.getContentResolver();
    this.mSettingsObserver = new SettingsObserver(this.mHandler);
    ((ContentResolver)localObject).registerContentObserver(Settings.System.getUriFor("status_bar_in_call_notification_floating"), false, this.mSettingsObserver, -1);
    ((ContentResolver)localObject).registerContentObserver(Settings.System.getUriFor("drive_mode_drive_mode"), false, this.mSettingsObserver, -1);
    ((ContentResolver)localObject).registerContentObserver(Settings.Secure.getUriFor("gb_boosting"), false, this.mSettingsObserver, -1);
    ((ContentResolver)localObject).registerContentObserver(Settings.System.getUriFor("miui_slider_tool_choice"), false, this.mSettingsObserver, -1);
    ((ContentResolver)localObject).registerContentObserver(Settings.System.getUriFor("miui_slider_launch_pkg"), false, this.mSettingsObserver, -1);
    ((ContentResolver)localObject).registerContentObserver(Settings.System.getUriFor("miui_slider_sound_check"), false, this.mSettingsObserver, -1);
    boolean bool1 = true;
    boolean bool2;
    if (Settings.System.getIntForUser((ContentResolver)localObject, "first_front_camera_open", 1, 0) == 1) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mFirstFrontCameraOpen = bool2;
    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "user_setup_complete", 0, -2) == 1) {
      bool2 = bool1;
    } else {
      bool2 = false;
    }
    this.mSetupCompleted = bool2;
    this.mReceiver = new SlideReceiver(null);
    localObject = new IntentFilter();
    ((IntentFilter)localObject).addAction("android.intent.action.USER_SWITCHED");
    this.mContext.registerReceiver(this.mReceiver, (IntentFilter)localObject);
    localObject = new IntentFilter();
    ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_MOUNTED");
    ((IntentFilter)localObject).addDataScheme("file");
    this.mContext.registerReceiver(this.mReceiver, (IntentFilter)localObject);
    updateSettings();
  }
  
  private void launchCamera()
  {
    Intent localIntent = new Intent();
    localIntent.setFlags(268468224);
    localIntent.putExtra("ShowCameraWhenLocked", true);
    localIntent.putExtra("StartActivityWhenLocked", true);
    localIntent.setAction("android.media.action.STILL_IMAGE_CAMERA");
    localIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
    localIntent.putExtra("autofocus", true);
    localIntent.putExtra("fullScreen", false);
    localIntent.putExtra("showActionIcons", false);
    localIntent.putExtra("android.intent.extras.SCREEN_SLIDE", true);
    localIntent.setComponent(new ComponentName("com.android.camera", "com.android.camera.Camera"));
    ActivityOptions localActivityOptions = ActivityOptions.makeCustomAnimation(this.mContext, 285278216, 285278246);
    Slog.d("SlideCoverEventManager", "launchCamera");
    this.mContext.startActivityAsUser(localIntent, localActivityOptions.toBundle(), UserHandle.CURRENT);
  }
  
  private void onActivityChanged(SomeArgs paramSomeArgs)
  {
    ComponentName localComponentName1 = (ComponentName)paramSomeArgs.arg1;
    ComponentName localComponentName2 = (ComponentName)paramSomeArgs.arg2;
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("notifyChange! preName = ");
    ((StringBuilder)localObject).append(localComponentName1.toString());
    ((StringBuilder)localObject).append("; curName = ");
    ((StringBuilder)localObject).append(localComponentName2.toString());
    Log.i("SlideCoverEventManager", ((StringBuilder)localObject).toString());
    localObject = localComponentName2.getClassName();
    this.mForegroundComponent = localComponentName2;
    this.mPreComponent = localComponentName1;
    this.mOnForbiddenActivity = this.mForbiddenActivities.contains(localObject);
    this.mOnMiuiAdjustActivity = SlideCloudConfigHelper.getInstance().is3rdAppProcessingActivity(localComponentName2.getPackageName(), (String)localObject);
    this.mOnTargetApps = SlideCloudConfigHelper.getInstance().isMiuiAdapteringApp(localComponentName2.getPackageName());
    boolean bool1 = localComponentName2.getPackageName().equals("com.miui.home");
    boolean bool2 = true;
    if ((bool1) && (!this.mInitMonitor))
    {
      SlideCameraMonitor.getInstance().init(this.mContext, this.mHandler.getLooper());
      SlideCameraMonitor.getInstance().setCameraOpenListener(this.mCameraOpenListener);
      this.mInitMonitor = true;
    }
    if ((this.mFrontCameraOpening) && (!((String)localObject).equals("com.android.systemui.recents.RecentsActivity"))) {
      hideTipsView();
    }
    if ((!((String)localObject).equals("com.android.incallui.InCallActivity")) && (!((String)localObject).equals("com.android.contacts.activities.PeopleActivity")) && (!((String)localObject).equals("com.android.phone.MiuiEmergencyDialer"))) {
      bool2 = false;
    }
    this.mPhoneForeground = bool2;
    paramSomeArgs.recycle();
  }
  
  private void showTipsView()
  {
    synchronized (sLock)
    {
      if ((this.mSliderViewService != null) && (this.mFrontCameraOpening) && (this.mSlideCoverStatus != 0) && (!this.mShowingTipsView))
      {
        int i;
        if (this.mFirstFrontCameraOpen)
        {
          Slog.d("SlideCoverEventManager", "first open front camera");
          this.mFirstFrontCameraOpen = false;
          i = 0;
        }
        else
        {
          i = 2;
        }
        try
        {
          Slog.d("SlideCoverEventManager", "showSliderView");
          this.mSliderViewService.showSliderView(i);
          this.mShowingTipsView = true;
        }
        catch (RemoteException localRemoteException)
        {
          Slog.d("SlideCoverEventManager", localRemoteException.toString());
          this.mShowingTipsView = false;
          this.mSliderViewService = null;
        }
        if (i == 0) {
          Settings.System.putIntForUser(this.mContext.getContentResolver(), "first_front_camera_open", 0, 0);
        }
        return;
      }
      Slog.d("SlideCoverEventManager", "show tips conditions are not satisfied");
      return;
    }
  }
  
  private void updateEventStatus(int paramInt)
  {
    Settings.System.putIntForUser(this.mContext.getContentResolver(), "sc_event_status", paramInt, 0);
  }
  
  private void updateLaunchApp(ContentResolver paramContentResolver)
  {
    this.mLaunchPkg = Settings.System.getStringForUser(paramContentResolver, "miui_slider_launch_pkg", -2);
  }
  
  private void updatePhoneFloating(ContentResolver paramContentResolver)
  {
    boolean bool = false;
    if (Settings.System.getIntForUser(paramContentResolver, "status_bar_in_call_notification_floating", 0, -2) == 1) {
      bool = true;
    }
    this.mPhoneFloating = bool;
  }
  
  private void updateSettings()
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    updateSetupComplete(localContentResolver);
    updatePhoneFloating(localContentResolver);
    updateSlideChoice(localContentResolver);
    updateLaunchApp(localContentResolver);
    updateSoundCheck(localContentResolver);
  }
  
  private void updateSetupComplete(ContentResolver paramContentResolver)
  {
    boolean bool = false;
    if (Settings.Secure.getIntForUser(paramContentResolver, "user_setup_complete", 0, -2) == 1) {
      bool = true;
    }
    this.mSetupCompleted = bool;
  }
  
  private void updateSlideChoice(ContentResolver paramContentResolver)
  {
    this.mSlideChoice = Settings.System.getIntForUser(paramContentResolver, "miui_slider_tool_choice", 1, -2);
  }
  
  private void updateSoundCheck(ContentResolver paramContentResolver)
  {
    boolean bool = true;
    if (Settings.System.getIntForUser(paramContentResolver, "miui_slider_sound_check", 1, -2) != 1) {
      bool = false;
    }
    this.mSoundEnable = bool;
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter, String[] arg3)
  {
    this.mSliderListener.dump(paramString, paramPrintWriter, ???);
    paramPrintWriter.println("SlideCoverEventManager:");
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mWakeUpCount=");
    ???.append(this.mWakeUpCount);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mLaunchCameraCount=");
    ???.append(this.mLaunchCameraCount);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mAnswerCallCount=");
    ???.append(this.mAnswerCallCount);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mUseOnAdaptedAppCount=");
    ???.append(this.mUseOnAdaptedAppCount);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mLaunchAppCount=");
    ???.append(this.mLaunchAppCount);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mLaunchPanelCount=");
    ???.append(this.mLaunchPanelCount);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mGameBoosterCount=");
    ???.append(this.mGameBoosterCount);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mSlideCoverStatus=");
    ???.append(this.mSlideCoverStatus);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mSlideChoice=");
    ???.append(this.mSlideChoice);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mFrontCameraOpening=");
    ???.append(this.mFrontCameraOpening);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mInDriveMode=");
    ???.append(this.mInDriveMode);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mLaunchPkg=");
    ???.append(this.mLaunchPkg);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mSoundEnable=");
    ???.append(this.mSoundEnable);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mPhoneState=");
    ???.append(this.mPhoneState);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mGameBoostMode=");
    ???.append(this.mGameBoostMode);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mCurrentUserId=");
    ???.append(this.mCurrentUserId);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("mForegroundComponent=");
    ??? = this.mForegroundComponent;
    if (??? == null) {
      ??? = "";
    } else {
      ??? = ???.toString();
    }
    ((StringBuilder)localObject).append(???);
    paramPrintWriter.println(((StringBuilder)localObject).toString());
    paramPrintWriter.print(paramString);
    ??? = new StringBuilder();
    ???.append("mOnTargetApps=");
    ???.append(this.mOnTargetApps);
    paramPrintWriter.println(???.toString());
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("mForbiddenActivities:");
    localObject = this.mForbiddenActivities.iterator();
    while (((Iterator)localObject).hasNext())
    {
      ??? = (String)((Iterator)localObject).next();
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(paramString);
      paramPrintWriter.println(???);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("sListenerWhiteList:");
    ??? = sListenerWhiteList.iterator();
    while (???.hasNext())
    {
      localObject = (String)???.next();
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(paramString);
      paramPrintWriter.println((String)localObject);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("registered slide event listener:");
    synchronized (sCallBackLock)
    {
      ArrayMap localArrayMap = this.mCallBacks.getCallbacks();
      Iterator localIterator = localArrayMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        localObject = (IBinder)localIterator.next();
        paramPrintWriter.print(paramString);
        paramPrintWriter.print(paramString);
        paramPrintWriter.println(localArrayMap.get(localObject));
      }
      ??? = this.mSliderViewService;
      if (??? != null) {
        try
        {
          paramPrintWriter.print(???.getDumpContent(paramString));
        }
        catch (RemoteException paramString) {}
      }
      return;
    }
  }
  
  public boolean handleSlideCoverEvent(int paramInt)
  {
    if (paramInt != 2)
    {
      if (this.mSlideCoverStatus == paramInt) {
        return false;
      }
      this.mSlideCoverStatus = paramInt;
    }
    if (this.mFirstEvent)
    {
      this.mFirstEvent = false;
      return false;
    }
    if (!this.mSetupCompleted)
    {
      updateSetupComplete(this.mContext.getContentResolver());
      if (!this.mSetupCompleted)
      {
        handleSlideCoverAnimation(paramInt);
        return false;
      }
    }
    if (this.mContext.getDisplay().getRotation() != 0)
    {
      if (handlePhone(paramInt)) {
        return false;
      }
      handleSlideCoverSound(paramInt);
      this.mCallBacks.notifyStatusChanged(paramInt);
      return false;
    }
    handleSystem(paramInt);
    if (handlePhone(paramInt)) {
      return false;
    }
    if (handleKeyGuard(paramInt)) {
      return true;
    }
    if ((this.mGameBoostMode) && (this.mCallBacks.notifyStatusChanged(paramInt))) {
      return false;
    }
    if (handleWechatHardCoder(paramInt)) {
      return false;
    }
    if (this.mOnForbiddenActivity) {
      return true;
    }
    if (handleChoice(paramInt)) {
      return false;
    }
    return handleCamera(paramInt);
  }
  
  public boolean isKeyguardLocked()
  {
    try
    {
      boolean bool = this.mWindowManager.isKeyguardLocked();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.d("SlideCoverEventManager", "error to get keyguard status");
    }
    return false;
  }
  
  public void registerSlideChangeListener(String paramString, ISlideChangeListener paramISlideChangeListener)
  {
    String str = SlideUtils.getProcessName(this.mContext, Binder.getCallingPid());
    int i = UserHandle.getUserId(Binder.getCallingUid());
    if (str == null)
    {
      paramString = new StringBuilder();
      paramString.append("can't find processName for pid ");
      paramString.append(Binder.getCallingPid());
      paramString.append(" when register");
      Slog.d("SlideCoverEventManager", paramString.toString());
      return;
    }
    if (!sListenerWhiteList.contains(paramString))
    {
      paramISlideChangeListener = new StringBuilder();
      paramISlideChangeListener.append(paramString);
      paramISlideChangeListener.append(" is not authenticated, ignore");
      Slog.d("SlideCoverEventManager", paramISlideChangeListener.toString());
      return;
    }
    this.mCallBacks.register(paramString, i, str, paramISlideChangeListener);
  }
  
  public void systemReady()
  {
    this.mWindowManager = WindowManagerGlobal.getWindowManagerService();
    this.mPhoneListener = new PhoneStateListener()
    {
      public void onCallStateChanged(int paramAnonymousInt, String paramAnonymousString)
      {
        SlideCoverEventManager.access$002(SlideCoverEventManager.this, paramAnonymousInt);
        paramAnonymousString = new StringBuilder();
        paramAnonymousString.append("mPhoneState=");
        paramAnonymousString.append(SlideCoverEventManager.this.mPhoneState);
        Slog.d("SlideCoverEventManager", paramAnonymousString.toString());
      }
    };
    TelephonyManager.from(this.mContext).listen(this.mPhoneListener, 32);
    this.mTelecomManager = TelecomManager.from(this.mContext);
    this.mPowerManager = ((PowerManager)this.mContext.getSystemService("power"));
    this.mAnimationController = new SlideAnimationController(this.mContext, this.mHandler.getLooper());
    this.mHandler.obtainMessage(100).sendToTarget();
  }
  
  public void unregisterSlideChangeListener(ISlideChangeListener paramISlideChangeListener)
  {
    this.mCallBacks.unregister(paramISlideChangeListener.asBinder());
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
      switch (paramMessage.what)
      {
      default: 
        break;
      case 102: 
        SlideCoverEventManager.this.handleBindSliderView();
        break;
      case 101: 
        SlideCoverEventManager.this.onActivityChanged((SomeArgs)paramMessage.obj);
        break;
      case 100: 
        SlideCoverEventManager.this.initOtherInfo();
      }
    }
  }
  
  private final class SettingsObserver
    extends ContentObserver
  {
    public SettingsObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      Object localObject = paramUri.getLastPathSegment();
      paramUri = SlideCoverEventManager.this.mContext.getContentResolver();
      int i = ((String)localObject).hashCode();
      boolean bool = false;
      paramBoolean = false;
      switch (i)
      {
      }
      for (;;)
      {
        break;
        if (((String)localObject).equals("drive_mode_drive_mode"))
        {
          i = 1;
          break label184;
          if (((String)localObject).equals("status_bar_in_call_notification_floating"))
          {
            i = 0;
            break label184;
            if (((String)localObject).equals("miui_slider_launch_pkg"))
            {
              i = 4;
              break label184;
              if (((String)localObject).equals("gb_boosting"))
              {
                i = 2;
                break label184;
                if (((String)localObject).equals("miui_slider_tool_choice"))
                {
                  i = 3;
                  break label184;
                  if (((String)localObject).equals("miui_slider_sound_check"))
                  {
                    i = 5;
                    break label184;
                  }
                }
              }
            }
          }
        }
      }
      i = -1;
      label184:
      if (i != 0)
      {
        if (i != 1)
        {
          if (i != 2)
          {
            if (i != 3)
            {
              if (i != 4)
              {
                if (i == 5) {
                  SlideCoverEventManager.this.updateSoundCheck(paramUri);
                }
              }
              else {
                SlideCoverEventManager.this.updateLaunchApp(paramUri);
              }
            }
            else {
              SlideCoverEventManager.this.updateSlideChoice(paramUri);
            }
          }
          else
          {
            localObject = SlideCoverEventManager.this;
            if (Settings.Secure.getIntForUser(paramUri, "gb_boosting", 0, -2) == 1) {
              paramBoolean = true;
            }
            SlideCoverEventManager.access$2002((SlideCoverEventManager)localObject, paramBoolean);
            paramUri = new StringBuilder();
            paramUri.append("mGameBoostMode=");
            paramUri.append(SlideCoverEventManager.this.mGameBoostMode);
            Slog.d("SlideCoverEventManager", paramUri.toString());
          }
        }
        else
        {
          localObject = SlideCoverEventManager.this;
          paramBoolean = bool;
          if (Settings.System.getIntForUser(paramUri, "drive_mode_drive_mode", 0, -2) != 0) {
            paramBoolean = true;
          }
          SlideCoverEventManager.access$1902((SlideCoverEventManager)localObject, paramBoolean);
          paramUri = new StringBuilder();
          paramUri.append("mInDriveMode=");
          paramUri.append(SlideCoverEventManager.this.mInDriveMode);
          Slog.d("SlideCoverEventManager", paramUri.toString());
        }
      }
      else
      {
        SlideCoverEventManager.this.updatePhoneFloating(paramUri);
        paramUri = new StringBuilder();
        paramUri.append("mPhoneForegroundState=");
        paramUri.append(SlideCoverEventManager.this.mPhoneFloating);
        Slog.d("SlideCoverEventManager", paramUri.toString());
      }
    }
  }
  
  private class SlideCallbacks
    extends Handler
  {
    private final ArrayMap<IBinder, Callback> mCallbackMap = new ArrayMap();
    
    public SlideCallbacks(Looper paramLooper)
    {
      super();
    }
    
    private boolean notifyStatusChanged(int paramInt)
    {
      Object localObject1 = SlideCoverEventManager.sCallBackLock;
      Object localObject2 = null;
      try
      {
        Iterator localIterator1 = SlideCoverEventManager.sListenerWhiteList.iterator();
        while (localIterator1.hasNext())
        {
          String str = (String)localIterator1.next();
          if (((!str.contains("gamebooster")) || (SlideCoverEventManager.this.mGameBoostMode)) && ((!str.contains("sliderpanel")) || ((SlideCoverEventManager.this.mSlideChoice == 2) && (SlideCoverEventManager.this.mContext.getDisplay().getRotation() == 0))))
          {
            int i = 0;
            Iterator localIterator2 = this.mCallbackMap.keySet().iterator();
            while (localIterator2.hasNext())
            {
              localObject2 = (IBinder)localIterator2.next();
              localObject2 = (Callback)this.mCallbackMap.get(localObject2);
              int j = i;
              if (localObject2 != null)
              {
                j = i;
                if (((Callback)localObject2).mIdentity.equals(str))
                {
                  SomeArgs localSomeArgs = SomeArgs.obtain();
                  localSomeArgs.arg1 = localObject2;
                  StringBuilder localStringBuilder = new java/lang/StringBuilder;
                  localStringBuilder.<init>();
                  localStringBuilder.append(((Callback)localObject2).mProcessName);
                  localStringBuilder.append("_");
                  localStringBuilder.append(((Callback)localObject2).mIdentity);
                  localSomeArgs.arg2 = localStringBuilder.toString();
                  Message.obtain(this, paramInt, localSomeArgs).sendToTarget();
                  j = 1;
                }
              }
              i = j;
            }
            if (i != 0)
            {
              if ((localObject2 != null) && (((Callback)localObject2).mUserId == SlideCoverEventManager.this.mCurrentUserId) && (paramInt == 0))
              {
                if (str.contains("gamebooster")) {
                  SlideCoverEventManager.access$2708(SlideCoverEventManager.this);
                }
                if (str.contains("sliderpanel")) {
                  SlideCoverEventManager.access$2808(SlideCoverEventManager.this);
                }
              }
              return true;
            }
          }
        }
        return false;
      }
      finally {}
    }
    
    public ArrayMap<IBinder, Callback> getCallbacks()
    {
      return this.mCallbackMap;
    }
    
    /* Error */
    public void handleMessage(Message paramMessage)
    {
      // Byte code:
      //   0: aload_1
      //   1: getfield 177	android/os/Message:obj	Ljava/lang/Object;
      //   4: checkcast 118	com/android/internal/os/SomeArgs
      //   7: astore_2
      //   8: aload_2
      //   9: getfield 126	com/android/internal/os/SomeArgs:arg1	Ljava/lang/Object;
      //   12: checkcast 9	miui/slide/SlideCoverEventManager$SlideCallbacks$Callback
      //   15: astore_3
      //   16: aload_2
      //   17: getfield 145	com/android/internal/os/SomeArgs:arg2	Ljava/lang/Object;
      //   20: checkcast 63	java/lang/String
      //   23: astore 4
      //   25: aload_3
      //   26: getfield 181	miui/slide/SlideCoverEventManager$SlideCallbacks$Callback:mListener	Lmiui/slide/ISlideChangeListener;
      //   29: aload_1
      //   30: getfield 184	android/os/Message:what	I
      //   33: invokeinterface 190 2 0
      //   38: new 128	java/lang/StringBuilder
      //   41: astore 5
      //   43: aload 5
      //   45: invokespecial 129	java/lang/StringBuilder:<init>	()V
      //   48: aload 5
      //   50: ldc -64
      //   52: invokevirtual 136	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   55: pop
      //   56: aload 5
      //   58: aload_1
      //   59: getfield 184	android/os/Message:what	I
      //   62: invokevirtual 195	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   65: pop
      //   66: aload 5
      //   68: ldc -59
      //   70: invokevirtual 136	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   73: pop
      //   74: aload 5
      //   76: aload_3
      //   77: getfield 157	miui/slide/SlideCoverEventManager$SlideCallbacks$Callback:mUserId	I
      //   80: invokevirtual 195	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   83: pop
      //   84: aload 5
      //   86: ldc -57
      //   88: invokevirtual 136	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   91: pop
      //   92: aload 5
      //   94: aload 4
      //   96: invokevirtual 136	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   99: pop
      //   100: ldc -55
      //   102: aload 5
      //   104: invokevirtual 142	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   107: invokestatic 207	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   110: pop
      //   111: goto +18 -> 129
      //   114: astore_1
      //   115: goto +19 -> 134
      //   118: astore_1
      //   119: ldc -55
      //   121: aload_1
      //   122: invokevirtual 208	android/os/RemoteException:toString	()Ljava/lang/String;
      //   125: invokestatic 211	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   128: pop
      //   129: aload_2
      //   130: invokevirtual 214	com/android/internal/os/SomeArgs:recycle	()V
      //   133: return
      //   134: aload_2
      //   135: invokevirtual 214	com/android/internal/os/SomeArgs:recycle	()V
      //   138: aload_1
      //   139: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	140	0	this	SlideCallbacks
      //   0	140	1	paramMessage	Message
      //   7	128	2	localSomeArgs	SomeArgs
      //   15	62	3	localCallback	Callback
      //   23	72	4	str	String
      //   41	62	5	localStringBuilder	StringBuilder
      // Exception table:
      //   from	to	target	type
      //   25	111	114	finally
      //   119	129	114	finally
      //   25	111	118	android/os/RemoteException
    }
    
    public void register(String paramString1, int paramInt, String paramString2, ISlideChangeListener paramISlideChangeListener)
    {
      try
      {
        synchronized (SlideCoverEventManager.sCallBackLock)
        {
          localObject2 = paramISlideChangeListener.asBinder();
          Callback localCallback = new miui/slide/SlideCoverEventManager$SlideCallbacks$Callback;
          localCallback.<init>(this, paramISlideChangeListener, paramInt, paramString2, paramString1);
          ((IBinder)localObject2).linkToDeath(localCallback, 0);
          paramISlideChangeListener = new java/lang/StringBuilder;
          paramISlideChangeListener.<init>();
          paramISlideChangeListener.append(paramString2);
          paramISlideChangeListener.append("_");
          paramISlideChangeListener.append(paramString1);
          paramISlideChangeListener.append(" registered");
          Slog.d("SlideCoverEventManager", paramISlideChangeListener.toString());
          this.mCallbackMap.put(localObject2, localCallback);
        }
      }
      catch (RemoteException paramISlideChangeListener)
      {
        Object localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        ((StringBuilder)localObject2).append("failed to register listener for ");
        ((StringBuilder)localObject2).append(paramString2);
        ((StringBuilder)localObject2).append("_");
        ((StringBuilder)localObject2).append(paramString1);
        ((StringBuilder)localObject2).append(" ");
        ((StringBuilder)localObject2).append(paramISlideChangeListener.toString());
        Slog.d("SlideCoverEventManager", ((StringBuilder)localObject2).toString());
        return;
      }
    }
    
    public void unregister(IBinder paramIBinder)
    {
      synchronized (SlideCoverEventManager.sCallBackLock)
      {
        paramIBinder = (Callback)this.mCallbackMap.remove(paramIBinder);
        if (paramIBinder != null) {
          paramIBinder.mListener.asBinder().unlinkToDeath(paramIBinder, 0);
        }
        return;
      }
    }
    
    private final class Callback
      implements IBinder.DeathRecipient
    {
      final String mIdentity;
      final ISlideChangeListener mListener;
      final String mProcessName;
      final int mUserId;
      
      Callback(ISlideChangeListener paramISlideChangeListener, int paramInt, String paramString1, String paramString2)
      {
        this.mListener = paramISlideChangeListener;
        this.mUserId = paramInt;
        this.mProcessName = paramString1;
        this.mIdentity = paramString2;
      }
      
      public void binderDied()
      {
        synchronized (SlideCoverEventManager.sCallBackLock)
        {
          SlideCoverEventManager.SlideCallbacks.this.mCallbackMap.remove(this.mListener.asBinder());
          return;
        }
      }
      
      public String toString()
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(this.mProcessName);
        localStringBuilder.append("_");
        localStringBuilder.append(this.mIdentity);
        localStringBuilder.append("_");
        localStringBuilder.append(this.mUserId);
        return localStringBuilder.toString();
      }
    }
  }
  
  private final class SlideReceiver
    extends BroadcastReceiver
  {
    private SlideReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = paramIntent.getAction();
      int i = paramContext.hashCode();
      if (i != -1514214344)
      {
        break label22;
        if (i != 959232034) {
          break label53;
        }
      }
      label22:
      while (!paramContext.equals("android.intent.action.MEDIA_MOUNTED"))
      {
        while (!paramContext.equals("android.intent.action.USER_SWITCHED")) {}
        i = 0;
        break;
      }
      i = 1;
      break label55;
      label53:
      i = -1;
      label55:
      if (i != 0)
      {
        if (i == 1) {
          SlideCoverEventManager.this.bindSliderView();
        }
      }
      else
      {
        i = paramIntent.getIntExtra("android.intent.extra.user_handle", 0);
        if (i != SlideCoverEventManager.this.mCurrentUserId)
        {
          SlideCoverEventManager.access$1402(SlideCoverEventManager.this, i);
          SlideCoverEventManager.this.updateSettings();
          SlideCoverEventManager.this.bindSliderView();
        }
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/slide/SlideCoverEventManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */