package android.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public final class InputMonitor
  implements Parcelable
{
  public static final Parcelable.Creator<InputMonitor> CREATOR = new Parcelable.Creator()
  {
    public InputMonitor createFromParcel(Parcel paramAnonymousParcel)
    {
      return new InputMonitor(paramAnonymousParcel);
    }
    
    public InputMonitor[] newArray(int paramAnonymousInt)
    {
      return new InputMonitor[paramAnonymousInt];
    }
  };
  private static final boolean DEBUG = false;
  private static final String TAG = "InputMonitor";
  private final InputChannel mChannel;
  private final IInputMonitorHost mHost;
  private final String mName;
  
  public InputMonitor(Parcel paramParcel)
  {
    this.mName = paramParcel.readString();
    this.mChannel = ((InputChannel)paramParcel.readParcelable(null));
    this.mHost = IInputMonitorHost.Stub.asInterface(paramParcel.readStrongBinder());
  }
  
  public InputMonitor(String paramString, InputChannel paramInputChannel, IInputMonitorHost paramIInputMonitorHost)
  {
    this.mName = paramString;
    this.mChannel = paramInputChannel;
    this.mHost = paramIInputMonitorHost;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dispose()
  {
    this.mChannel.dispose();
    try
    {
      this.mHost.dispose();
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public InputChannel getInputChannel()
  {
    return this.mChannel;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public void pilferPointers()
  {
    try
    {
      this.mHost.pilferPointers();
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("InputMonitor{mName=");
    localStringBuilder.append(this.mName);
    localStringBuilder.append(", mChannel=");
    localStringBuilder.append(this.mChannel);
    localStringBuilder.append(", mHost=");
    localStringBuilder.append(this.mHost);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mName);
    paramParcel.writeParcelable(this.mChannel, paramInt);
    paramParcel.writeStrongBinder(this.mHost.asBinder());
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InputMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */