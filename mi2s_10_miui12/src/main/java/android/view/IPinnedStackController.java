package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IPinnedStackController
  extends IInterface
{
  public abstract int getDisplayRotation()
    throws RemoteException;
  
  public abstract void setIsMinimized(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setMinEdgeSize(int paramInt)
    throws RemoteException;
  
  public static class Default
    implements IPinnedStackController
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public int getDisplayRotation()
      throws RemoteException
    {
      return 0;
    }
    
    public void setIsMinimized(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setMinEdgeSize(int paramInt)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IPinnedStackController
  {
    private static final String DESCRIPTOR = "android.view.IPinnedStackController";
    static final int TRANSACTION_getDisplayRotation = 3;
    static final int TRANSACTION_setIsMinimized = 1;
    static final int TRANSACTION_setMinEdgeSize = 2;
    
    public Stub()
    {
      attachInterface(this, "android.view.IPinnedStackController");
    }
    
    public static IPinnedStackController asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IPinnedStackController");
      if ((localIInterface != null) && ((localIInterface instanceof IPinnedStackController))) {
        return (IPinnedStackController)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IPinnedStackController getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          if (paramInt != 3) {
            return null;
          }
          return "getDisplayRotation";
        }
        return "setMinEdgeSize";
      }
      return "setIsMinimized";
    }
    
    public static boolean setDefaultImpl(IPinnedStackController paramIPinnedStackController)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIPinnedStackController != null))
      {
        Proxy.sDefaultImpl = paramIPinnedStackController;
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
            if (paramInt1 != 1598968902) {
              return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
            }
            paramParcel2.writeString("android.view.IPinnedStackController");
            return true;
          }
          paramParcel1.enforceInterface("android.view.IPinnedStackController");
          paramInt1 = getDisplayRotation();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
        paramParcel1.enforceInterface("android.view.IPinnedStackController");
        setMinEdgeSize(paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.view.IPinnedStackController");
      boolean bool;
      if (paramParcel1.readInt() != 0) {
        bool = true;
      } else {
        bool = false;
      }
      setIsMinimized(bool);
      return true;
    }
    
    private static class Proxy
      implements IPinnedStackController
    {
      public static IPinnedStackController sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public int getDisplayRotation()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IPinnedStackController");
          if ((!this.mRemote.transact(3, localParcel1, localParcel2, 0)) && (IPinnedStackController.Stub.getDefaultImpl() != null))
          {
            i = IPinnedStackController.Stub.getDefaultImpl().getDisplayRotation();
            return i;
          }
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.view.IPinnedStackController";
      }
      
      public void setIsMinimized(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IPinnedStackController");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IPinnedStackController.Stub.getDefaultImpl() != null))
          {
            IPinnedStackController.Stub.getDefaultImpl().setIsMinimized(paramBoolean);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setMinEdgeSize(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IPinnedStackController");
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (IPinnedStackController.Stub.getDefaultImpl() != null))
          {
            IPinnedStackController.Stub.getDefaultImpl().setMinEdgeSize(paramInt);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IPinnedStackController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */