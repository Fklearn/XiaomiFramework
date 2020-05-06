package miui.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Slog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProximitySensorWrapper
{
  private static final int EVENT_FAR = 1;
  private static final int EVENT_TOO_CLOSE = 0;
  private static final float PROXIMITY_THRESHOLD = 4.0F;
  public static final int STATE_STABLE_DELAY = 300;
  private final Context mContext;
  private final Handler mHandler;
  private final List<ProximitySensorChangeListener> mProximitySensorChangeListeners = new ArrayList();
  private int mProximitySensorState;
  private final Sensor mSensor;
  private final SensorEventListener mSensorListener = new SensorEventListener()
  {
    public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
    
    public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
    {
      float f = paramAnonymousSensorEvent.values[0];
      int i;
      if ((f >= 0.0D) && (f < 4.0F) && (f < ProximitySensorWrapper.this.mSensor.getMaximumRange())) {
        i = 1;
      } else {
        i = 0;
      }
      paramAnonymousSensorEvent = new StringBuilder();
      paramAnonymousSensorEvent.append("proximity distance: ");
      paramAnonymousSensorEvent.append(f);
      Slog.d("ProximitySensorWrapper", paramAnonymousSensorEvent.toString());
      if (i != 0)
      {
        if (ProximitySensorWrapper.this.mProximitySensorState != 1)
        {
          ProximitySensorWrapper.access$102(ProximitySensorWrapper.this, 1);
          ProximitySensorWrapper.this.mHandler.removeMessages(1);
          ProximitySensorWrapper.this.mHandler.sendEmptyMessageDelayed(0, 300L);
        }
      }
      else if (ProximitySensorWrapper.this.mProximitySensorState != 0)
      {
        ProximitySensorWrapper.access$102(ProximitySensorWrapper.this, 0);
        ProximitySensorWrapper.this.mHandler.removeMessages(0);
        ProximitySensorWrapper.this.mHandler.sendEmptyMessageDelayed(1, 300L);
      }
    }
  };
  private final SensorManager mSensorManager;
  
  public ProximitySensorWrapper(Context paramContext)
  {
    this.mContext = paramContext;
    this.mHandler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        int i = paramAnonymousMessage.what;
        if (i != 0)
        {
          if (i == 1) {
            ProximitySensorWrapper.this.notifyListeners(false);
          }
        }
        else {
          ProximitySensorWrapper.this.notifyListeners(true);
        }
      }
    };
    this.mProximitySensorState = -1;
    this.mSensorManager = ((SensorManager)this.mContext.getSystemService("sensor"));
    this.mSensor = this.mSensorManager.getDefaultSensor(8);
  }
  
  private void notifyListeners(boolean paramBoolean)
  {
    synchronized (this.mProximitySensorChangeListeners)
    {
      Iterator localIterator = this.mProximitySensorChangeListeners.iterator();
      while (localIterator.hasNext()) {
        ((ProximitySensorChangeListener)localIterator.next()).onSensorChanged(paramBoolean);
      }
      return;
    }
  }
  
  private void unregisterSensorEventListenerLocked()
  {
    if (this.mProximitySensorChangeListeners.size() == 0) {
      this.mSensorManager.unregisterListener(this.mSensorListener, this.mSensor);
    }
  }
  
  public void registerListener(ProximitySensorChangeListener paramProximitySensorChangeListener)
  {
    synchronized (this.mProximitySensorChangeListeners)
    {
      if (!this.mProximitySensorChangeListeners.contains(paramProximitySensorChangeListener))
      {
        if (this.mProximitySensorChangeListeners.size() == 0) {
          this.mSensorManager.registerListener(this.mSensorListener, this.mSensor, 0);
        }
        this.mProximitySensorChangeListeners.add(paramProximitySensorChangeListener);
      }
      return;
    }
  }
  
  public void unregisterAllListeners()
  {
    synchronized (this.mProximitySensorChangeListeners)
    {
      this.mProximitySensorChangeListeners.clear();
      unregisterSensorEventListenerLocked();
      return;
    }
  }
  
  public void unregisterListener(ProximitySensorChangeListener paramProximitySensorChangeListener)
  {
    synchronized (this.mProximitySensorChangeListeners)
    {
      this.mProximitySensorChangeListeners.remove(paramProximitySensorChangeListener);
      unregisterSensorEventListenerLocked();
      return;
    }
  }
  
  public static abstract interface ProximitySensorChangeListener
  {
    public abstract void onSensorChanged(boolean paramBoolean);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/ProximitySensorWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */