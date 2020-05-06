package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IOnKeyguardExitResult
  extends IInterface
{
  public abstract void onKeyguardExitResult(boolean paramBoolean)
    throws RemoteException;
  
  public static class Default
    implements IOnKeyguardExitResult
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void onKeyguardExitResult(boolean paramBoolean)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IOnKeyguardExitResult
  {
    private static final String DESCRIPTOR = "android.view.IOnKeyguardExitResult";
    static final int TRANSACTION_onKeyguardExitResult = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.IOnKeyguardExitResult");
    }
    
    public static IOnKeyguardExitResult asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IOnKeyguardExitResult");
      if ((localIInterface != null) && ((localIInterface instanceof IOnKeyguardExitResult))) {
        return (IOnKeyguardExitResult)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IOnKeyguardExitResult getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1) {
        return null;
      }
      return "onKeyguardExitResult";
    }
    
    public static boolean setDefaultImpl(IOnKeyguardExitResult paramIOnKeyguardExitResult)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIOnKeyguardExitResult != null))
      {
        Proxy.sDefaultImpl = paramIOnKeyguardExitResult;
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
        paramParcel2.writeString("android.view.IOnKeyguardExitResult");
        return true;
      }
      paramParcel1.enforceInterface("android.view.IOnKeyguardExitResult");
      boolean bool;
      if (paramParcel1.readInt() != 0) {
        bool = true;
      } else {
        bool = false;
      }
      onKeyguardExitResult(bool);
      return true;
    }
    
    private static class Proxy
      implements IOnKeyguardExitResult
    {
      public static IOnKeyguardExitResult sDefaultImpl;
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
        return "android.view.IOnKeyguardExitResult";
      }
      
      public void onKeyguardExitResult(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IOnKeyguardExitResult");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IOnKeyguardExitResult.Stub.getDefaultImpl() != null))
          {
            IOnKeyguardExitResult.Stub.getDefaultImpl().onKeyguardExitResult(paramBoolean);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IOnKeyguardExitResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */