package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IDisplayFoldListener
  extends IInterface
{
  public abstract void onDisplayFoldChanged(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public static class Default
    implements IDisplayFoldListener
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void onDisplayFoldChanged(int paramInt, boolean paramBoolean)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IDisplayFoldListener
  {
    private static final String DESCRIPTOR = "android.view.IDisplayFoldListener";
    static final int TRANSACTION_onDisplayFoldChanged = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.IDisplayFoldListener");
    }
    
    public static IDisplayFoldListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IDisplayFoldListener");
      if ((localIInterface != null) && ((localIInterface instanceof IDisplayFoldListener))) {
        return (IDisplayFoldListener)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IDisplayFoldListener getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1) {
        return null;
      }
      return "onDisplayFoldChanged";
    }
    
    public static boolean setDefaultImpl(IDisplayFoldListener paramIDisplayFoldListener)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIDisplayFoldListener != null))
      {
        Proxy.sDefaultImpl = paramIDisplayFoldListener;
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
        paramParcel2.writeString("android.view.IDisplayFoldListener");
        return true;
      }
      paramParcel1.enforceInterface("android.view.IDisplayFoldListener");
      paramInt1 = paramParcel1.readInt();
      boolean bool;
      if (paramParcel1.readInt() != 0) {
        bool = true;
      } else {
        bool = false;
      }
      onDisplayFoldChanged(paramInt1, bool);
      return true;
    }
    
    private static class Proxy
      implements IDisplayFoldListener
    {
      public static IDisplayFoldListener sDefaultImpl;
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
        return "android.view.IDisplayFoldListener";
      }
      
      public void onDisplayFoldChanged(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IDisplayFoldListener");
          localParcel.writeInt(paramInt);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IDisplayFoldListener.Stub.getDefaultImpl() != null))
          {
            IDisplayFoldListener.Stub.getDefaultImpl().onDisplayFoldChanged(paramInt, paramBoolean);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IDisplayFoldListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */