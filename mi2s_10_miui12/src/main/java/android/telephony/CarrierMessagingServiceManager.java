package android.telephony;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.service.carrier.ICarrierMessagingService;
import android.service.carrier.ICarrierMessagingService.Stub;
import com.android.internal.util.Preconditions;

public abstract class CarrierMessagingServiceManager
{
  private volatile CarrierMessagingServiceConnection mCarrierMessagingServiceConnection;
  
  public boolean bindToCarrierMessagingService(Context paramContext, String paramString)
  {
    boolean bool;
    if (this.mCarrierMessagingServiceConnection == null) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkState(bool);
    Intent localIntent = new Intent("android.service.carrier.CarrierMessagingService");
    localIntent.setPackage(paramString);
    this.mCarrierMessagingServiceConnection = new CarrierMessagingServiceConnection(null);
    return paramContext.bindService(localIntent, this.mCarrierMessagingServiceConnection, 1);
  }
  
  public void disposeConnection(Context paramContext)
  {
    Preconditions.checkNotNull(this.mCarrierMessagingServiceConnection);
    paramContext.unbindService(this.mCarrierMessagingServiceConnection);
    this.mCarrierMessagingServiceConnection = null;
  }
  
  protected abstract void onServiceReady(ICarrierMessagingService paramICarrierMessagingService);
  
  private final class CarrierMessagingServiceConnection
    implements ServiceConnection
  {
    private CarrierMessagingServiceConnection() {}
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      CarrierMessagingServiceManager.this.onServiceReady(ICarrierMessagingService.Stub.asInterface(paramIBinder));
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName) {}
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/android/telephony/CarrierMessagingServiceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */