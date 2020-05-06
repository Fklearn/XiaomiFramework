package miui.security;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ISecurityCallback
  extends IInterface
{
  public abstract boolean checkPreInstallNeeded(String paramString)
    throws RemoteException;
  
  public abstract void preInstallApps()
    throws RemoteException;
  
  public static class Default
    implements ISecurityCallback
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public boolean checkPreInstallNeeded(String paramString)
      throws RemoteException
    {
      return false;
    }
    
    public void preInstallApps()
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements ISecurityCallback
  {
    private static final String DESCRIPTOR = "miui.security.ISecurityCallback";
    static final int TRANSACTION_checkPreInstallNeeded = 1;
    static final int TRANSACTION_preInstallApps = 2;
    
    public Stub()
    {
      attachInterface(this, "miui.security.ISecurityCallback");
    }
    
    public static ISecurityCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("miui.security.ISecurityCallback");
      if ((localIInterface != null) && ((localIInterface instanceof ISecurityCallback))) {
        return (ISecurityCallback)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static ISecurityCallback getDefaultImpl()
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
        return "preInstallApps";
      }
      return "checkPreInstallNeeded";
    }
    
    public static boolean setDefaultImpl(ISecurityCallback paramISecurityCallback)
    {
      if ((Proxy.sDefaultImpl == null) && (paramISecurityCallback != null))
      {
        Proxy.sDefaultImpl = paramISecurityCallback;
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
          paramParcel2.writeString("miui.security.ISecurityCallback");
          return true;
        }
        paramParcel1.enforceInterface("miui.security.ISecurityCallback");
        preInstallApps();
        return true;
      }
      paramParcel1.enforceInterface("miui.security.ISecurityCallback");
      paramInt1 = checkPreInstallNeeded(paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements ISecurityCallback
    {
      public static ISecurityCallback sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public boolean checkPreInstallNeeded(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityCallback");
          localParcel1.writeString(paramString);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(1, localParcel1, localParcel2, 0)) && (ISecurityCallback.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityCallback.Stub.getDefaultImpl().checkPreInstallNeeded(paramString);
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
      
      public String getInterfaceDescriptor()
      {
        return "miui.security.ISecurityCallback";
      }
      
      public void preInstallApps()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("miui.security.ISecurityCallback");
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (ISecurityCallback.Stub.getDefaultImpl() != null))
          {
            ISecurityCallback.Stub.getDefaultImpl().preInstallApps();
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/security/ISecurityCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */