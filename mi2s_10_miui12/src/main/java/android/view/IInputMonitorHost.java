package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IInputMonitorHost
  extends IInterface
{
  public abstract void dispose()
    throws RemoteException;
  
  public abstract void pilferPointers()
    throws RemoteException;
  
  public static class Default
    implements IInputMonitorHost
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void dispose()
      throws RemoteException
    {}
    
    public void pilferPointers()
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IInputMonitorHost
  {
    private static final String DESCRIPTOR = "android.view.IInputMonitorHost";
    static final int TRANSACTION_dispose = 2;
    static final int TRANSACTION_pilferPointers = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.IInputMonitorHost");
    }
    
    public static IInputMonitorHost asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IInputMonitorHost");
      if ((localIInterface != null) && ((localIInterface instanceof IInputMonitorHost))) {
        return (IInputMonitorHost)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IInputMonitorHost getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2) {
          return null;
        }
        return "dispose";
      }
      return "pilferPointers";
    }
    
    public static boolean setDefaultImpl(IInputMonitorHost paramIInputMonitorHost)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIInputMonitorHost != null))
      {
        Proxy.sDefaultImpl = paramIInputMonitorHost;
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
        if (paramInt1 != 2)
        {
          if (paramInt1 != 1598968902) {
            return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
          }
          paramParcel2.writeString("android.view.IInputMonitorHost");
          return true;
        }
        paramParcel1.enforceInterface("android.view.IInputMonitorHost");
        dispose();
        return true;
      }
      paramParcel1.enforceInterface("android.view.IInputMonitorHost");
      pilferPointers();
      return true;
    }
    
    private static class Proxy
      implements IInputMonitorHost
    {
      public static IInputMonitorHost sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void dispose()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IInputMonitorHost");
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (IInputMonitorHost.Stub.getDefaultImpl() != null))
          {
            IInputMonitorHost.Stub.getDefaultImpl().dispose();
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.view.IInputMonitorHost";
      }
      
      public void pilferPointers()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IInputMonitorHost");
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IInputMonitorHost.Stub.getDefaultImpl() != null))
          {
            IInputMonitorHost.Stub.getDefaultImpl().pilferPointers();
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IInputMonitorHost.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */