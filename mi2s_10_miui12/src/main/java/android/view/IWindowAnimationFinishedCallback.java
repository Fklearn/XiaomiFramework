package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IWindowAnimationFinishedCallback
  extends IInterface
{
  @UnsupportedAppUsage
  public abstract void onWindowAnimFinished()
    throws RemoteException;
  
  public static class Default
    implements IWindowAnimationFinishedCallback
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void onWindowAnimFinished()
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IWindowAnimationFinishedCallback
  {
    private static final String DESCRIPTOR = "android.view.IWindowAnimationFinishedCallback";
    static final int TRANSACTION_onWindowAnimFinished = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.IWindowAnimationFinishedCallback");
    }
    
    public static IWindowAnimationFinishedCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IWindowAnimationFinishedCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IWindowAnimationFinishedCallback))) {
        return (IWindowAnimationFinishedCallback)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IWindowAnimationFinishedCallback getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1) {
        return null;
      }
      return "onWindowAnimFinished";
    }
    
    public static boolean setDefaultImpl(IWindowAnimationFinishedCallback paramIWindowAnimationFinishedCallback)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIWindowAnimationFinishedCallback != null))
      {
        Proxy.sDefaultImpl = paramIWindowAnimationFinishedCallback;
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
        paramParcel2.writeString("android.view.IWindowAnimationFinishedCallback");
        return true;
      }
      paramParcel1.enforceInterface("android.view.IWindowAnimationFinishedCallback");
      onWindowAnimFinished();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IWindowAnimationFinishedCallback
    {
      public static IWindowAnimationFinishedCallback sDefaultImpl;
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
        return "android.view.IWindowAnimationFinishedCallback";
      }
      
      public void onWindowAnimFinished()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowAnimationFinishedCallback");
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (IWindowAnimationFinishedCallback.Stub.getDefaultImpl() != null))
          {
            IWindowAnimationFinishedCallback.Stub.getDefaultImpl().onWindowAnimFinished();
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IWindowAnimationFinishedCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */