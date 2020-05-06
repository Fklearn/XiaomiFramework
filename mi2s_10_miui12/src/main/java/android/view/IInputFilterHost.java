package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IInputFilterHost
  extends IInterface
{
  public abstract void sendInputEvent(InputEvent paramInputEvent, int paramInt)
    throws RemoteException;
  
  public static class Default
    implements IInputFilterHost
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void sendInputEvent(InputEvent paramInputEvent, int paramInt)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IInputFilterHost
  {
    private static final String DESCRIPTOR = "android.view.IInputFilterHost";
    static final int TRANSACTION_sendInputEvent = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.IInputFilterHost");
    }
    
    public static IInputFilterHost asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IInputFilterHost");
      if ((localIInterface != null) && ((localIInterface instanceof IInputFilterHost))) {
        return (IInputFilterHost)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IInputFilterHost getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1) {
        return null;
      }
      return "sendInputEvent";
    }
    
    public static boolean setDefaultImpl(IInputFilterHost paramIInputFilterHost)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIInputFilterHost != null))
      {
        Proxy.sDefaultImpl = paramIInputFilterHost;
        return true;
      }
      return false;
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public String getTransactionName(int paramInt)
    {
      return getDefaultTransactionName(paramInt);
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      if (paramInt1 != 1)
      {
        if (paramInt1 != 1598968902) {
          return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        }
        paramParcel2.writeString("android.view.IInputFilterHost");
        return true;
      }
      paramParcel1.enforceInterface("android.view.IInputFilterHost");
      if (paramParcel1.readInt() != 0) {
        paramParcel2 = (InputEvent)InputEvent.CREATOR.createFromParcel(paramParcel1);
      } else {
        paramParcel2 = null;
      }
      sendInputEvent(paramParcel2, paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IInputFilterHost
    {
      public static IInputFilterHost sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.view.IInputFilterHost";
      }
      
      public void sendInputEvent(InputEvent paramInputEvent, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IInputFilterHost");
          if (paramInputEvent != null)
          {
            localParcel.writeInt(1);
            paramInputEvent.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IInputFilterHost.Stub.getDefaultImpl() != null))
          {
            IInputFilterHost.Stub.getDefaultImpl().sendInputEvent(paramInputEvent, paramInt);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IInputFilterHost.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */