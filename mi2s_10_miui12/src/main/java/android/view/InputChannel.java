package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class InputChannel
  implements Parcelable
{
  @UnsupportedAppUsage
  public static final Parcelable.Creator<InputChannel> CREATOR = new Parcelable.Creator()
  {
    public InputChannel createFromParcel(Parcel paramAnonymousParcel)
    {
      InputChannel localInputChannel = new InputChannel();
      localInputChannel.readFromParcel(paramAnonymousParcel);
      return localInputChannel;
    }
    
    public InputChannel[] newArray(int paramAnonymousInt)
    {
      return new InputChannel[paramAnonymousInt];
    }
  };
  private static final boolean DEBUG = false;
  private static final String TAG = "InputChannel";
  @UnsupportedAppUsage
  private long mPtr;
  
  private native void nativeDispose(boolean paramBoolean);
  
  private native void nativeDup(InputChannel paramInputChannel);
  
  private native String nativeGetName();
  
  private native IBinder nativeGetToken();
  
  private static native InputChannel[] nativeOpenInputChannelPair(String paramString);
  
  private native void nativeReadFromParcel(Parcel paramParcel);
  
  private native void nativeSetToken(IBinder paramIBinder);
  
  private native void nativeTransferTo(InputChannel paramInputChannel);
  
  private native void nativeWriteToParcel(Parcel paramParcel);
  
  public static InputChannel[] openInputChannelPair(String paramString)
  {
    if (paramString != null) {
      return nativeOpenInputChannelPair(paramString);
    }
    throw new IllegalArgumentException("name must not be null");
  }
  
  public int describeContents()
  {
    return 1;
  }
  
  public void dispose()
  {
    nativeDispose(false);
  }
  
  public InputChannel dup()
  {
    InputChannel localInputChannel = new InputChannel();
    nativeDup(localInputChannel);
    return localInputChannel;
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      nativeDispose(true);
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public String getName()
  {
    String str = nativeGetName();
    if (str == null) {
      str = "uninitialized";
    }
    return str;
  }
  
  public IBinder getToken()
  {
    return nativeGetToken();
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    if (paramParcel != null)
    {
      nativeReadFromParcel(paramParcel);
      return;
    }
    throw new IllegalArgumentException("in must not be null");
  }
  
  public void setToken(IBinder paramIBinder)
  {
    nativeSetToken(paramIBinder);
  }
  
  public String toString()
  {
    return getName();
  }
  
  public void transferTo(InputChannel paramInputChannel)
  {
    if (paramInputChannel != null)
    {
      nativeTransferTo(paramInputChannel);
      return;
    }
    throw new IllegalArgumentException("outParameter must not be null");
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (paramParcel != null)
    {
      nativeWriteToParcel(paramParcel);
      if ((paramInt & 0x1) != 0) {
        dispose();
      }
      return;
    }
    throw new IllegalArgumentException("out must not be null");
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InputChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */