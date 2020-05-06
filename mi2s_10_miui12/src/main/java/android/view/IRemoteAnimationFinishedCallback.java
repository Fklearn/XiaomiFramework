package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IRemoteAnimationFinishedCallback
  extends IInterface
{
  @UnsupportedAppUsage
  public abstract void onAnimationFinished()
    throws RemoteException;
  
  public static class Default
    implements IRemoteAnimationFinishedCallback
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void onAnimationFinished()
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IRemoteAnimationFinishedCallback
  {
    private static final String DESCRIPTOR = "android.view.IRemoteAnimationFinishedCallback";
    static final int TRANSACTION_onAnimationFinished = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.IRemoteAnimationFinishedCallback");
    }
    
    public static IRemoteAnimationFinishedCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IRemoteAnimationFinishedCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IRemoteAnimationFinishedCallback))) {
        return (IRemoteAnimationFinishedCallback)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IRemoteAnimationFinishedCallback getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1) {
        return null;
      }
      return "onAnimationFinished";
    }
    
    public static boolean setDefaultImpl(IRemoteAnimationFinishedCallback paramIRemoteAnimationFinishedCallback)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIRemoteAnimationFinishedCallback != null))
      {
        Proxy.sDefaultImpl = paramIRemoteAnimationFinishedCallback;
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
        paramParcel2.writeString("android.view.IRemoteAnimationFinishedCallback");
        return true;
      }
      paramParcel1.enforceInterface("android.view.IRemoteAnimationFinishedCallback");
      onAnimationFinished();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IRemoteAnimationFinishedCallback
    {
      public static IRemoteAnimationFinishedCallback sDefaultImpl;
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
        return "android.view.IRemoteAnimationFinishedCallback";
      }
      
      public void onAnimationFinished()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IRemoteAnimationFinishedCallback");
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (IRemoteAnimationFinishedCallback.Stub.getDefaultImpl() != null))
          {
            IRemoteAnimationFinishedCallback.Stub.getDefaultImpl().onAnimationFinished();
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IRemoteAnimationFinishedCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */