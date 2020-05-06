package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IWindowId
  extends IInterface
{
  public abstract boolean isFocused()
    throws RemoteException;
  
  public abstract void registerFocusObserver(IWindowFocusObserver paramIWindowFocusObserver)
    throws RemoteException;
  
  public abstract void unregisterFocusObserver(IWindowFocusObserver paramIWindowFocusObserver)
    throws RemoteException;
  
  public static class Default
    implements IWindowId
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public boolean isFocused()
      throws RemoteException
    {
      return false;
    }
    
    public void registerFocusObserver(IWindowFocusObserver paramIWindowFocusObserver)
      throws RemoteException
    {}
    
    public void unregisterFocusObserver(IWindowFocusObserver paramIWindowFocusObserver)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IWindowId
  {
    private static final String DESCRIPTOR = "android.view.IWindowId";
    static final int TRANSACTION_isFocused = 3;
    static final int TRANSACTION_registerFocusObserver = 1;
    static final int TRANSACTION_unregisterFocusObserver = 2;
    
    public Stub()
    {
      attachInterface(this, "android.view.IWindowId");
    }
    
    public static IWindowId asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IWindowId");
      if ((localIInterface != null) && ((localIInterface instanceof IWindowId))) {
        return (IWindowId)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IWindowId getDefaultImpl()
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
          return "isFocused";
        }
        return "unregisterFocusObserver";
      }
      return "registerFocusObserver";
    }
    
    public static boolean setDefaultImpl(IWindowId paramIWindowId)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIWindowId != null))
      {
        Proxy.sDefaultImpl = paramIWindowId;
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
            paramParcel2.writeString("android.view.IWindowId");
            return true;
          }
          paramParcel1.enforceInterface("android.view.IWindowId");
          paramInt1 = isFocused();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
        paramParcel1.enforceInterface("android.view.IWindowId");
        unregisterFocusObserver(IWindowFocusObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.view.IWindowId");
      registerFocusObserver(IWindowFocusObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IWindowId
    {
      public static IWindowId sDefaultImpl;
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
        return "android.view.IWindowId";
      }
      
      public boolean isFocused()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowId");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(3, localParcel1, localParcel2, 0)) && (IWindowId.Stub.getDefaultImpl() != null))
          {
            bool = IWindowId.Stub.getDefaultImpl().isFocused();
            return bool;
          }
          localParcel2.readException();
          int i = localParcel2.readInt();
          if (i != 0) {
            bool = true;
          }
          return bool;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void registerFocusObserver(IWindowFocusObserver paramIWindowFocusObserver)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowId");
          IBinder localIBinder;
          if (paramIWindowFocusObserver != null) {
            localIBinder = paramIWindowFocusObserver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (IWindowId.Stub.getDefaultImpl() != null))
          {
            IWindowId.Stub.getDefaultImpl().registerFocusObserver(paramIWindowFocusObserver);
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
      
      public void unregisterFocusObserver(IWindowFocusObserver paramIWindowFocusObserver)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowId");
          IBinder localIBinder;
          if (paramIWindowFocusObserver != null) {
            localIBinder = paramIWindowFocusObserver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(2, localParcel1, localParcel2, 0)) && (IWindowId.Stub.getDefaultImpl() != null))
          {
            IWindowId.Stub.getDefaultImpl().unregisterFocusObserver(paramIWindowFocusObserver);
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
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IWindowId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */