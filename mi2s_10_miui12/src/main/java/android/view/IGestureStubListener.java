package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IGestureStubListener
  extends IInterface
{
  public abstract void onGestureFinish(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onGestureReady()
    throws RemoteException;
  
  public abstract void onGestureStart()
    throws RemoteException;
  
  public abstract void skipAppTransition()
    throws RemoteException;
  
  public static class Default
    implements IGestureStubListener
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void onGestureFinish(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void onGestureReady()
      throws RemoteException
    {}
    
    public void onGestureStart()
      throws RemoteException
    {}
    
    public void skipAppTransition()
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IGestureStubListener
  {
    private static final String DESCRIPTOR = "android.view.IGestureStubListener";
    static final int TRANSACTION_onGestureFinish = 3;
    static final int TRANSACTION_onGestureReady = 1;
    static final int TRANSACTION_onGestureStart = 2;
    static final int TRANSACTION_skipAppTransition = 4;
    
    public Stub()
    {
      attachInterface(this, "android.view.IGestureStubListener");
    }
    
    public static IGestureStubListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IGestureStubListener");
      if ((localIInterface != null) && ((localIInterface instanceof IGestureStubListener))) {
        return (IGestureStubListener)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IGestureStubListener getDefaultImpl()
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
            return "skipAppTransition";
          }
          return "onGestureFinish";
        }
        return "onGestureStart";
      }
      return "onGestureReady";
    }
    
    public static boolean setDefaultImpl(IGestureStubListener paramIGestureStubListener)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIGestureStubListener != null))
      {
        Proxy.sDefaultImpl = paramIGestureStubListener;
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
              paramParcel2.writeString("android.view.IGestureStubListener");
              return true;
            }
            paramParcel1.enforceInterface("android.view.IGestureStubListener");
            skipAppTransition();
            paramParcel2.writeNoException();
            return true;
          }
          paramParcel1.enforceInterface("android.view.IGestureStubListener");
          boolean bool;
          if (paramParcel1.readInt() != 0) {
            bool = true;
          } else {
            bool = false;
          }
          onGestureFinish(bool);
          paramParcel2.writeNoException();
          return true;
        }
        paramParcel1.enforceInterface("android.view.IGestureStubListener");
        onGestureStart();
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.view.IGestureStubListener");
      onGestureReady();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IGestureStubListener
    {
      public static IGestureStubListener sDefaultImpl;
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
        return "android.view.IGestureStubListener";
      }
      
      public void onGestureFinish(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IGestureStubListener");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(3, localParcel1, localParcel2, 0)) && (IGestureStubListener.Stub.getDefaultImpl() != null))
          {
            IGestureStubListener.Stub.getDefaultImpl().onGestureFinish(paramBoolean);
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
      
      public void onGestureReady()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IGestureStubListener");
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (IGestureStubListener.Stub.getDefaultImpl() != null))
          {
            IGestureStubListener.Stub.getDefaultImpl().onGestureReady();
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
      
      public void onGestureStart()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IGestureStubListener");
          if ((!this.mRemote.transact(2, localParcel1, localParcel2, 0)) && (IGestureStubListener.Stub.getDefaultImpl() != null))
          {
            IGestureStubListener.Stub.getDefaultImpl().onGestureStart();
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
      
      public void skipAppTransition()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IGestureStubListener");
          if ((!this.mRemote.transact(4, localParcel1, localParcel2, 0)) && (IGestureStubListener.Stub.getDefaultImpl() != null))
          {
            IGestureStubListener.Stub.getDefaultImpl().skipAppTransition();
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IGestureStubListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */