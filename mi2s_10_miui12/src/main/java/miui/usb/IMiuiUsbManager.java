package miui.usb;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IMiuiUsbManager
  extends IInterface
{
  public abstract void acceptMdbRestore()
    throws RemoteException;
  
  public abstract void allowUsbDebugging(boolean paramBoolean, String paramString)
    throws RemoteException;
  
  public abstract void cancelMdbRestore()
    throws RemoteException;
  
  public abstract void denyUsbDebugging()
    throws RemoteException;
  
  public static class Default
    implements IMiuiUsbManager
  {
    public void acceptMdbRestore()
      throws RemoteException
    {}
    
    public void allowUsbDebugging(boolean paramBoolean, String paramString)
      throws RemoteException
    {}
    
    public IBinder asBinder()
    {
      return null;
    }
    
    public void cancelMdbRestore()
      throws RemoteException
    {}
    
    public void denyUsbDebugging()
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IMiuiUsbManager
  {
    private static final String DESCRIPTOR = "miui.usb.IMiuiUsbManager";
    static final int TRANSACTION_acceptMdbRestore = 1;
    static final int TRANSACTION_allowUsbDebugging = 3;
    static final int TRANSACTION_cancelMdbRestore = 2;
    static final int TRANSACTION_denyUsbDebugging = 4;
    
    public Stub()
    {
      attachInterface(this, "miui.usb.IMiuiUsbManager");
    }
    
    public static IMiuiUsbManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("miui.usb.IMiuiUsbManager");
      if ((localIInterface != null) && ((localIInterface instanceof IMiuiUsbManager))) {
        return (IMiuiUsbManager)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IMiuiUsbManager getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          if (paramInt != 3)
          {
            if (paramInt != 4) {
              return null;
            }
            return "denyUsbDebugging";
          }
          return "allowUsbDebugging";
        }
        return "cancelMdbRestore";
      }
      return "acceptMdbRestore";
    }
    
    public static boolean setDefaultImpl(IMiuiUsbManager paramIMiuiUsbManager)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIMiuiUsbManager != null))
      {
        Proxy.sDefaultImpl = paramIMiuiUsbManager;
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
          if (paramInt1 != 3)
          {
            if (paramInt1 != 4)
            {
              if (paramInt1 != 1598968902) {
                return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
              }
              paramParcel2.writeString("miui.usb.IMiuiUsbManager");
              return true;
            }
            paramParcel1.enforceInterface("miui.usb.IMiuiUsbManager");
            denyUsbDebugging();
            paramParcel2.writeNoException();
            return true;
          }
          paramParcel1.enforceInterface("miui.usb.IMiuiUsbManager");
          boolean bool;
          if (paramParcel1.readInt() != 0) {
            bool = true;
          } else {
            bool = false;
          }
          allowUsbDebugging(bool, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
        paramParcel1.enforceInterface("miui.usb.IMiuiUsbManager");
        cancelMdbRestore();
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("miui.usb.IMiuiUsbManager");
      acceptMdbRestore();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IMiuiUsbManager
    {
      public static IMiuiUsbManager sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void acceptMdbRestore()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.usb.IMiuiUsbManager");
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (IMiuiUsbManager.Stub.getDefaultImpl() != null))
          {
            IMiuiUsbManager.Stub.getDefaultImpl().acceptMdbRestore();
            return;
          }
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void allowUsbDebugging(boolean paramBoolean, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.usb.IMiuiUsbManager");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(3, localParcel1, localParcel2, 0)) && (IMiuiUsbManager.Stub.getDefaultImpl() != null))
          {
            IMiuiUsbManager.Stub.getDefaultImpl().allowUsbDebugging(paramBoolean, paramString);
            return;
          }
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void cancelMdbRestore()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.usb.IMiuiUsbManager");
          if ((!this.mRemote.transact(2, localParcel1, localParcel2, 0)) && (IMiuiUsbManager.Stub.getDefaultImpl() != null))
          {
            IMiuiUsbManager.Stub.getDefaultImpl().cancelMdbRestore();
            return;
          }
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void denyUsbDebugging()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.usb.IMiuiUsbManager");
          if ((!this.mRemote.transact(4, localParcel1, localParcel2, 0)) && (IMiuiUsbManager.Stub.getDefaultImpl() != null))
          {
            IMiuiUsbManager.Stub.getDefaultImpl().denyUsbDebugging();
            return;
          }
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "miui.usb.IMiuiUsbManager";
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/usb/IMiuiUsbManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */