package android.view;

import android.content.Context;
import android.hardware.SensorListener;

@Deprecated
public abstract class OrientationListener
  implements SensorListener
{
  public static final int ORIENTATION_UNKNOWN = -1;
  private OrientationEventListener mOrientationEventLis;
  
  public OrientationListener(Context paramContext)
  {
    this.mOrientationEventLis = new OrientationEventListenerInternal(paramContext);
  }
  
  public OrientationListener(Context paramContext, int paramInt)
  {
    this.mOrientationEventLis = new OrientationEventListenerInternal(paramContext, paramInt);
  }
  
  public void disable()
  {
    this.mOrientationEventLis.disable();
  }
  
  public void enable()
  {
    this.mOrientationEventLis.enable();
  }
  
  public void onAccuracyChanged(int paramInt1, int paramInt2) {}
  
  public abstract void onOrientationChanged(int paramInt);
  
  public void onSensorChanged(int paramInt, float[] paramArrayOfFloat) {}
  
  class OrientationEventListenerInternal
    extends OrientationEventListener
  {
    OrientationEventListenerInternal(Context paramContext)
    {
      super();
    }
    
    OrientationEventListenerInternal(Context paramContext, int paramInt)
    {
      super(paramInt);
      registerListener(OrientationListener.this);
    }
    
    public void onOrientationChanged(int paramInt)
    {
      OrientationListener.this.onOrientationChanged(paramInt);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/OrientationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */