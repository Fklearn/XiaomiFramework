package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IApplicationToken
  extends IInterface
{
  public abstract String getName()
    throws RemoteException;
  
  public static class Default
    implements IApplicationToken
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public String getName()
      throws RemoteException
    {
      return null;
    }
  }
  
  public static abstract class Stub
    extends Binder
    implements IApplicationToken
  {
    private static final String DESCRIPTOR = "android.view.IApplicationToken";
    static final int TRANSACTION_getName = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.IApplicationToken");
    }
    
    public static IApplicationToken asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IApplicationToken");
      if ((localIInterface != null) && ((localIInterface instanceof IApplicationToken))) {
        return (IApplicationToken)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IApplicationToken getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1) {
        return null;
      }
      return "getName";
    }
    
    public static boolean setDefaultImpl(IApplicationToken paramIApplicationToken)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIApplicationToken != null))
      {
        Proxy.sDefaultImpl = paramIApplicationToken;
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
        paramParcel2.writeString("android.view.IApplicationToken");
        return true;
      }
      paramParcel1.enforceInterface("android.view.IApplicationToken");
      paramParcel1 = getName();
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      return true;
    }
    
    private static class Proxy
      implements IApplicationToken
    {
      public static IApplicationToken sDefaultImpl;
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
        return "android.view.IApplicationToken";
      }
      
      public String getName()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IApplicationToken");
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (IApplicationToken.Stub.getDefaultImpl() != null))
          {
            str = IApplicationToken.Stub.getDefaultImpl().getName();
            return str;
          }
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IApplicationToken.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */