package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IWindowFocusObserver
  extends IInterface
{
  public abstract void focusGained(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void focusLost(IBinder paramIBinder)
    throws RemoteException;
  
  public static class Default
    implements IWindowFocusObserver
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void focusGained(IBinder paramIBinder)
      throws RemoteException
    {}
    
    public void focusLost(IBinder paramIBinder)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IWindowFocusObserver
  {
    private static final String DESCRIPTOR = "android.view.IWindowFocusObserver";
    static final int TRANSACTION_focusGained = 1;
    static final int TRANSACTION_focusLost = 2;
    
    public Stub()
    {
      attachInterface(this, "android.view.IWindowFocusObserver");
    }
    
    public static IWindowFocusObserver asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IWindowFocusObserver");
      if ((localIInterface != null) && ((localIInterface instanceof IWindowFocusObserver))) {
        return (IWindowFocusObserver)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IWindowFocusObserver getDefaultImpl()
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
        return "focusLost";
      }
      return "focusGained";
    }
    
    public static boolean setDefaultImpl(IWindowFocusObserver paramIWindowFocusObserver)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIWindowFocusObserver != null))
      {
        Proxy.sDefaultImpl = paramIWindowFocusObserver;
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
          paramParcel2.writeString("android.view.IWindowFocusObserver");
          return true;
        }
        paramParcel1.enforceInterface("android.view.IWindowFocusObserver");
        focusLost(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.view.IWindowFocusObserver");
      focusGained(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IWindowFocusObserver
    {
      public static IWindowFocusObserver sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void focusGained(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowFocusObserver");
          localParcel1.writeStrongBinder(paramIBinder);
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (IWindowFocusObserver.Stub.getDefaultImpl() != null))
          {
            IWindowFocusObserver.Stub.getDefaultImpl().focusGained(paramIBinder);
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
      
      public void focusLost(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowFocusObserver");
          localParcel1.writeStrongBinder(paramIBinder);
          if ((!this.mRemote.transact(2, localParcel1, localParcel2, 0)) && (IWindowFocusObserver.Stub.getDefaultImpl() != null))
          {
            IWindowFocusObserver.Stub.getDefaultImpl().focusLost(paramIBinder);
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
        return "android.view.IWindowFocusObserver";
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IWindowFocusObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */