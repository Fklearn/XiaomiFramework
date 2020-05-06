package android.view;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.HashMap;

public class WindowId
  implements Parcelable
{
  public static final Parcelable.Creator<WindowId> CREATOR = new Parcelable.Creator()
  {
    public WindowId createFromParcel(Parcel paramAnonymousParcel)
    {
      paramAnonymousParcel = paramAnonymousParcel.readStrongBinder();
      if (paramAnonymousParcel != null) {
        paramAnonymousParcel = new WindowId(paramAnonymousParcel);
      } else {
        paramAnonymousParcel = null;
      }
      return paramAnonymousParcel;
    }
    
    public WindowId[] newArray(int paramAnonymousInt)
    {
      return new WindowId[paramAnonymousInt];
    }
  };
  private final IWindowId mToken;
  
  public WindowId(IBinder paramIBinder)
  {
    this.mToken = IWindowId.Stub.asInterface(paramIBinder);
  }
  
  public WindowId(IWindowId paramIWindowId)
  {
    this.mToken = paramIWindowId;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof WindowId)) {
      return this.mToken.asBinder().equals(((WindowId)paramObject).mToken.asBinder());
    }
    return false;
  }
  
  public IWindowId getTarget()
  {
    return this.mToken;
  }
  
  public int hashCode()
  {
    return this.mToken.asBinder().hashCode();
  }
  
  public boolean isFocused()
  {
    try
    {
      boolean bool = this.mToken.isFocused();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public void registerFocusObserver(FocusObserver paramFocusObserver)
  {
    synchronized (paramFocusObserver.mRegistrations)
    {
      if (!paramFocusObserver.mRegistrations.containsKey(this.mToken.asBinder()))
      {
        paramFocusObserver.mRegistrations.put(this.mToken.asBinder(), this);
        try
        {
          this.mToken.registerFocusObserver(paramFocusObserver.mIObserver);
        }
        catch (RemoteException paramFocusObserver) {}
        return;
      }
      paramFocusObserver = new java/lang/IllegalStateException;
      paramFocusObserver.<init>("Focus observer already registered with input token");
      throw paramFocusObserver;
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("IntentSender{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(": ");
    localStringBuilder.append(this.mToken.asBinder());
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public void unregisterFocusObserver(FocusObserver paramFocusObserver)
  {
    synchronized (paramFocusObserver.mRegistrations)
    {
      Object localObject = paramFocusObserver.mRegistrations.remove(this.mToken.asBinder());
      if (localObject != null)
      {
        try
        {
          this.mToken.unregisterFocusObserver(paramFocusObserver.mIObserver);
        }
        catch (RemoteException paramFocusObserver) {}
        return;
      }
      paramFocusObserver = new java/lang/IllegalStateException;
      paramFocusObserver.<init>("Focus observer not registered with input token");
      throw paramFocusObserver;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeStrongBinder(this.mToken.asBinder());
  }
  
  public static abstract class FocusObserver
  {
    final Handler mHandler = new H();
    final IWindowFocusObserver.Stub mIObserver = new IWindowFocusObserver.Stub()
    {
      public void focusGained(IBinder paramAnonymousIBinder)
      {
        synchronized (WindowId.FocusObserver.this.mRegistrations)
        {
          paramAnonymousIBinder = (WindowId)WindowId.FocusObserver.this.mRegistrations.get(paramAnonymousIBinder);
          if (WindowId.FocusObserver.this.mHandler != null) {
            WindowId.FocusObserver.this.mHandler.sendMessage(WindowId.FocusObserver.this.mHandler.obtainMessage(1, paramAnonymousIBinder));
          } else {
            WindowId.FocusObserver.this.onFocusGained(paramAnonymousIBinder);
          }
          return;
        }
      }
      
      public void focusLost(IBinder paramAnonymousIBinder)
      {
        synchronized (WindowId.FocusObserver.this.mRegistrations)
        {
          paramAnonymousIBinder = (WindowId)WindowId.FocusObserver.this.mRegistrations.get(paramAnonymousIBinder);
          if (WindowId.FocusObserver.this.mHandler != null) {
            WindowId.FocusObserver.this.mHandler.sendMessage(WindowId.FocusObserver.this.mHandler.obtainMessage(2, paramAnonymousIBinder));
          } else {
            WindowId.FocusObserver.this.onFocusLost(paramAnonymousIBinder);
          }
          return;
        }
      }
    };
    final HashMap<IBinder, WindowId> mRegistrations = new HashMap();
    
    public abstract void onFocusGained(WindowId paramWindowId);
    
    public abstract void onFocusLost(WindowId paramWindowId);
    
    class H
      extends Handler
    {
      H() {}
      
      public void handleMessage(Message paramMessage)
      {
        int i = paramMessage.what;
        if (i != 1)
        {
          if (i != 2) {
            super.handleMessage(paramMessage);
          } else {
            WindowId.FocusObserver.this.onFocusLost((WindowId)paramMessage.obj);
          }
        }
        else {
          WindowId.FocusObserver.this.onFocusGained((WindowId)paramMessage.obj);
        }
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/WindowId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */