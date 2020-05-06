package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IInputFilter
  extends IInterface
{
  public abstract void filterInputEvent(InputEvent paramInputEvent, int paramInt)
    throws RemoteException;
  
  public abstract void install(IInputFilterHost paramIInputFilterHost)
    throws RemoteException;
  
  public abstract void uninstall()
    throws RemoteException;
  
  public static class Default
    implements IInputFilter
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void filterInputEvent(InputEvent paramInputEvent, int paramInt)
      throws RemoteException
    {}
    
    public void install(IInputFilterHost paramIInputFilterHost)
      throws RemoteException
    {}
    
    public void uninstall()
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IInputFilter
  {
    private static final String DESCRIPTOR = "android.view.IInputFilter";
    static final int TRANSACTION_filterInputEvent = 3;
    static final int TRANSACTION_install = 1;
    static final int TRANSACTION_uninstall = 2;
    
    public Stub()
    {
      attachInterface(this, "android.view.IInputFilter");
    }
    
    public static IInputFilter asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IInputFilter");
      if ((localIInterface != null) && ((localIInterface instanceof IInputFilter))) {
        return (IInputFilter)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IInputFilter getDefaultImpl()
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
          return "filterInputEvent";
        }
        return "uninstall";
      }
      return "install";
    }
    
    public static boolean setDefaultImpl(IInputFilter paramIInputFilter)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIInputFilter != null))
      {
        Proxy.sDefaultImpl = paramIInputFilter;
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
            paramParcel2.writeString("android.view.IInputFilter");
            return true;
          }
          paramParcel1.enforceInterface("android.view.IInputFilter");
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (InputEvent)InputEvent.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          filterInputEvent(paramParcel2, paramParcel1.readInt());
          return true;
        }
        paramParcel1.enforceInterface("android.view.IInputFilter");
        uninstall();
        return true;
      }
      paramParcel1.enforceInterface("android.view.IInputFilter");
      install(IInputFilterHost.Stub.asInterface(paramParcel1.readStrongBinder()));
      return true;
    }
    
    private static class Proxy
      implements IInputFilter
    {
      public static IInputFilter sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void filterInputEvent(InputEvent paramInputEvent, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IInputFilter");
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
          if ((!this.mRemote.transact(3, localParcel, null, 1)) && (IInputFilter.Stub.getDefaultImpl() != null))
          {
            IInputFilter.Stub.getDefaultImpl().filterInputEvent(paramInputEvent, paramInt);
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
        return "android.view.IInputFilter";
      }
      
      public void install(IInputFilterHost paramIInputFilterHost)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IInputFilter");
          IBinder localIBinder;
          if (paramIInputFilterHost != null) {
            localIBinder = paramIInputFilterHost.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IInputFilter.Stub.getDefaultImpl() != null))
          {
            IInputFilter.Stub.getDefaultImpl().install(paramIInputFilterHost);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void uninstall()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IInputFilter");
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (IInputFilter.Stub.getDefaultImpl() != null))
          {
            IInputFilter.Stub.getDefaultImpl().uninstall();
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IInputFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */