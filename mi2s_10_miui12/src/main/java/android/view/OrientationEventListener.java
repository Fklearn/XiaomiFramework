package android.view;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public abstract class OrientationEventListener
{
  private static final boolean DEBUG = false;
  public static final int ORIENTATION_UNKNOWN = -1;
  private static final String TAG = "OrientationEventListener";
  private static final boolean localLOGV = false;
  private boolean mEnabled = false;
  private OrientationListener mOldListener;
  private int mOrientation = -1;
  private int mRate;
  private Sensor mSensor;
  private SensorEventListener mSensorEventListener;
  private SensorManager mSensorManager;
  
  public OrientationEventListener(Context paramContext)
  {
    this(paramContext, 3);
  }
  
  public OrientationEventListener(Context paramContext, int paramInt)
  {
    this.mSensorManager = ((SensorManager)paramContext.getSystemService("sensor"));
    this.mRate = paramInt;
    this.mSensor = this.mSensorManager.getDefaultSensor(1);
    if (this.mSensor != null) {
      this.mSensorEventListener = new SensorEventListenerImpl();
    }
  }
  
  public boolean canDetectOrientation()
  {
    boolean bool;
    if (this.mSensor != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void disable()
  {
    if (this.mSensor == null)
    {
      Log.w("OrientationEventListener", "Cannot detect sensors. Invalid disable");
      return;
    }
    if (this.mEnabled == true)
    {
      this.mSensorManager.unregisterListener(this.mSensorEventListener);
      this.mEnabled = false;
    }
  }
  
  public void enable()
  {
    Sensor localSensor = this.mSensor;
    if (localSensor == null)
    {
      Log.w("OrientationEventListener", "Cannot detect sensors. Not enabled");
      return;
    }
    if (!this.mEnabled)
    {
      this.mSensorManager.registerListener(this.mSensorEventListener, localSensor, this.mRate);
      this.mEnabled = true;
    }
  }
  
  public abstract void onOrientationChanged(int paramInt);
  
  void registerListener(OrientationListener paramOrientationListener)
  {
    this.mOldListener = paramOrientationListener;
  }
  
  class SensorEventListenerImpl
    implements SensorEventListener
  {
    private static final int _DATA_X = 0;
    private static final int _DATA_Y = 1;
    private static final int _DATA_Z = 2;
    
    SensorEventListenerImpl() {}
    
    public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
    
    public void onSensorChanged(SensorEvent paramSensorEvent)
    {
      float[] arrayOfFloat = paramSensorEvent.values;
      int i = -1;
      float f1 = -arrayOfFloat[0];
      float f2 = -arrayOfFloat[1];
      float f3 = -arrayOfFloat[2];
      if (4.0F * (f1 * f1 + f2 * f2) >= f3 * f3)
      {
        int j;
        for (i = 90 - Math.round((float)Math.atan2(-f2, f1) * 57.29578F);; i -= 360)
        {
          j = i;
          if (i < 360) {
            break;
          }
        }
        for (;;)
        {
          i = j;
          if (j >= 0) {
            break;
          }
          j += 360;
        }
      }
      if (OrientationEventListener.this.mOldListener != null) {
        OrientationEventListener.this.mOldListener.onSensorChanged(1, paramSensorEvent.values);
      }
      if (i != OrientationEventListener.this.mOrientation)
      {
        OrientationEventListener.access$102(OrientationEventListener.this, i);
        OrientationEventListener.this.onOrientationChanged(i);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/OrientationEventListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */