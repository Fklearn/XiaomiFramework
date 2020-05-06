package miui.slide;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.VibrationEffect.OneShot;
import android.os.Vibrator;
import android.provider.Settings.System;
import android.util.Slog;
import android.view.KeyEvent;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;

public class SlideCoverListener
{
  private static final int DELAY_TIME_MS = 300;
  private static final int MSG_DARK_SCREEN_EVENT = 100;
  private static final int MSG_ON_SCREEN_EVENT = 101;
  private static final int MSG_UPDATE_STATUS = 102;
  private static final boolean SEND_BROADCAST = false;
  public static final int SLIDE_COVER_SENSOR_TYPE = 33171002;
  public static final int SLIDE_EVENT_CLOSE = 1;
  public static final int SLIDE_EVENT_OPEN = 0;
  public static final int SLIDE_EVENT_SLIDING = 2;
  public static final String TAG = "SlideCoverListener";
  private static final int TIME_COST_MOST_EXPECTED = 50;
  private static final long VIBRATION_TIME_THRESHOLD = 8000L;
  private final Context mContext;
  private final Handler mHandler;
  private int mLastEvent = -1;
  private long mLastEventTime = -1L;
  private long mLastVibrateTime;
  private final SensorEventListener mListener = new SensorEventListener()
  {
    public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
    
    public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
    {
      int i = SlideCoverListener.this.getFrameworkSlideEvent((int)paramAnonymousSensorEvent.values[0]);
      if (SlideCoverListener.this.mLastEvent == i) {
        return;
      }
      if (!SlideCoverListener.this.mWakeLock.isHeld()) {
        SlideCoverListener.this.mWakeLock.acquire();
      }
      SlideCoverListener.access$102(SlideCoverListener.this, i);
      if (i != 2)
      {
        SlideCoverListener.access$308(SlideCoverListener.this);
        paramAnonymousSensorEvent = new StringBuilder();
        paramAnonymousSensorEvent.append("event values = ");
        paramAnonymousSensorEvent.append(i);
        Slog.d("SlideCoverListener", paramAnonymousSensorEvent.toString());
        Message.obtain(SlideCoverListener.this.mHandler, 102, i, 0).sendToTarget();
        long l = SystemClock.uptimeMillis();
        if (l - SlideCoverListener.this.mLastEventTime < 300L)
        {
          SlideCoverListener.access$608(SlideCoverListener.this);
          SlideCoverListener.access$502(SlideCoverListener.this, l);
          if ((SlideCoverListener.this.mVibrator != null) && (SlideCoverListener.this.mVibrator.hasVibrator()) && (l - SlideCoverListener.this.mLastVibrateTime > 8000L))
          {
            SlideCoverListener.this.mVibrator.vibrate(SlideCoverListener.this.mVibrationEffect);
            SlideCoverListener.access$802(SlideCoverListener.this, l);
          }
        }
        SlideCoverListener.access$502(SlideCoverListener.this, l);
      }
      paramAnonymousSensorEvent = SlideCoverListener.this.mHandler.obtainMessage();
      paramAnonymousSensorEvent.arg1 = i;
      paramAnonymousSensorEvent.obj = Long.valueOf(System.currentTimeMillis());
      paramAnonymousSensorEvent.what = 101;
      SlideCoverListener.this.mHandler.sendMessage(paramAnonymousSensorEvent);
    }
  };
  private final PowerManager mPowerManager;
  private int mQuickSlideEventCount;
  private Sensor mSensor;
  private final SensorManager mSensorManager;
  private final SlideCoverEventManager mSlideCoverEventManager;
  private int mSlideEventCount;
  private int mTotalHistoryCount;
  private VibrationEffect mVibrationEffect;
  private final Vibrator mVibrator;
  private PowerManager.WakeLock mWakeLock;
  
  public SlideCoverListener(Context paramContext)
  {
    this.mContext = paramContext;
    this.mSensorManager = ((SensorManager)paramContext.getSystemService("sensor"));
    this.mPowerManager = ((PowerManager)paramContext.getSystemService("power"));
    this.mVibrator = ((Vibrator)paramContext.getSystemService("vibrator"));
    this.mVibrationEffect = new VibrationEffect.OneShot(50L, 1);
    HandlerThread localHandlerThread = new HandlerThread("slide_cover", -2);
    localHandlerThread.start();
    this.mHandler = new H(localHandlerThread.getLooper());
    this.mSlideCoverEventManager = new SlideCoverEventManager(this, paramContext, this.mHandler.getLooper());
    this.mSensor = this.mSensorManager.getDefaultSensor(33171002, true);
  }
  
  private SensorEvent createSlideSensorEvent(float paramFloat)
  {
    SensorEvent localSensorEvent;
    try
    {
      Object localObject = SensorEvent.class.getDeclaredConstructor(new Class[] { Integer.TYPE });
      ((Constructor)localObject).setAccessible(true);
      localObject = (SensorEvent)((Constructor)localObject).newInstance(new Object[] { Integer.valueOf(16) });
    }
    catch (Exception localException)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("createSlideSensorEvent: ");
      localStringBuilder.append(localException.toString());
      Slog.e("SlideCoverListener", localStringBuilder.toString());
      localSensorEvent = null;
    }
    if (localSensorEvent != null) {
      localSensorEvent.values[0] = paramFloat;
    }
    return localSensorEvent;
  }
  
  private int getFrameworkSlideEvent(int paramInt)
  {
    if (paramInt == 1) {
      return 0;
    }
    if (paramInt == 0) {
      return 1;
    }
    if (paramInt == 2) {
      return 2;
    }
    return -1;
  }
  
  private void handleDispatchMessage(Message paramMessage)
  {
    int i = paramMessage.arg1;
    long l1 = System.currentTimeMillis();
    long l2 = l1 - ((Long)paramMessage.obj).longValue();
    if (l2 > 50L)
    {
      paramMessage = new StringBuilder();
      paramMessage.append("event ");
      paramMessage.append(i);
      paramMessage.append(" took ");
      paramMessage.append(l2);
      paramMessage.append(" ms before handle");
      Slog.e("SlideCoverListener", paramMessage.toString());
    }
    boolean bool = false;
    int j = -1;
    if (i == 0)
    {
      bool = this.mSlideCoverEventManager.handleSlideCoverEvent(0);
      j = 700;
    }
    else if (i == 1)
    {
      bool = this.mSlideCoverEventManager.handleSlideCoverEvent(1);
      j = 701;
    }
    else if (i == 2)
    {
      bool = this.mSlideCoverEventManager.handleSlideCoverEvent(2);
      j = 702;
    }
    if (!bool)
    {
      if (this.mWakeLock.isHeld()) {
        this.mWakeLock.release();
      }
      return;
    }
    l2 = SystemClock.uptimeMillis();
    paramMessage = new KeyEvent(l2, l2, 0, j, 0, 0, -1, 0);
    InputManager.getInstance().injectInputEvent(paramMessage, 0);
    paramMessage = new KeyEvent(l2, l2, 1, j, 0, 0, -1, 0);
    InputManager.getInstance().injectInputEvent(paramMessage, 0);
    if (System.currentTimeMillis() - l1 > 50L)
    {
      paramMessage = new StringBuilder();
      paramMessage.append("event ");
      paramMessage.append(i);
      paramMessage.append(" took ");
      paramMessage.append(System.currentTimeMillis() - l1);
      paramMessage.append(" ms during handle");
      Slog.e("SlideCoverListener", paramMessage.toString());
    }
    if (this.mWakeLock.isHeld()) {
      this.mWakeLock.release();
    }
  }
  
  private void updateSystemStatus(int paramInt)
  {
    Settings.System.putIntForUser(this.mContext.getContentResolver(), "sc_status", paramInt, 0);
    this.mTotalHistoryCount += 1;
    Settings.System.putIntForUser(this.mContext.getContentResolver(), "miui_slider_history_count", this.mTotalHistoryCount, 0);
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("SlideCoverListener:");
    paramPrintWriter.print(paramString);
    paramArrayOfString = new StringBuilder();
    paramArrayOfString.append("mSlideEventCount=");
    paramArrayOfString.append(this.mSlideEventCount);
    paramPrintWriter.println(paramArrayOfString.toString());
    paramPrintWriter.print(paramString);
    paramArrayOfString = new StringBuilder();
    paramArrayOfString.append("mQuickSlideEventCount=");
    paramArrayOfString.append(this.mQuickSlideEventCount);
    paramPrintWriter.println(paramArrayOfString.toString());
    paramPrintWriter.print(paramString);
    paramString = new StringBuilder();
    paramString.append("mTotalHistoryCount=");
    paramString.append(this.mTotalHistoryCount);
    paramPrintWriter.println(paramString.toString());
  }
  
  public void setSlideCoverState(int paramInt)
  {
    SensorEvent localSensorEvent = createSlideSensorEvent(paramInt);
    if (localSensorEvent != null) {
      this.mListener.onSensorChanged(localSensorEvent);
    }
  }
  
  public void systemReady()
  {
    this.mTotalHistoryCount = Settings.System.getInt(this.mContext.getContentResolver(), "miui_slider_history_count", 0);
    this.mSlideCoverEventManager.systemReady();
    this.mWakeLock = this.mPowerManager.newWakeLock(1, "SlideCover.mWakelock");
    this.mSensorManager.registerListener(this.mListener, this.mSensor, 3, this.mHandler);
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
        SlideCoverListener.this.updateSystemStatus(paramMessage.arg1);
        break;
      case 100: 
      case 101: 
        SlideCoverListener.this.handleDispatchMessage(paramMessage);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/slide/SlideCoverListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */