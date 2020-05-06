package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IRemoteAnimationRunner
  extends IInterface
{
  @UnsupportedAppUsage
  public abstract void onAnimationCancelled()
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void onAnimationStart(RemoteAnimationTarget[] paramArrayOfRemoteAnimationTarget, IRemoteAnimationFinishedCallback paramIRemoteAnimationFinishedCallback)
    throws RemoteException;
  
  public static class Default
    implements IRemoteAnimationRunner
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void onAnimationCancelled()
      throws RemoteException
    {}
    
    public void onAnimationStart(RemoteAnimationTarget[] paramArrayOfRemoteAnimationTarget, IRemoteAnimationFinishedCallback paramIRemoteAnimationFinishedCallback)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IRemoteAnimationRunner
  {
    private static final String DESCRIPTOR = "android.view.IRemoteAnimationRunner";
    static final int TRANSACTION_onAnimationCancelled = 2;
    static final int TRANSACTION_onAnimationStart = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.IRemoteAnimationRunner");
    }
    
    public static IRemoteAnimationRunner asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IRemoteAnimationRunner");
      if ((localIInterface != null) && ((localIInterface instanceof IRemoteAnimationRunner))) {
        return (IRemoteAnimationRunner)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IRemoteAnimationRunner getDefaultImpl()
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
        return "onAnimationCancelled";
      }
      return "onAnimationStart";
    }
    
    public static boolean setDefaultImpl(IRemoteAnimationRunner paramIRemoteAnimationRunner)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIRemoteAnimationRunner != null))
      {
        Proxy.sDefaultImpl = paramIRemoteAnimationRunner;
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
          paramParcel2.writeString("android.view.IRemoteAnimationRunner");
          return true;
        }
        paramParcel1.enforceInterface("android.view.IRemoteAnimationRunner");
        onAnimationCancelled();
        return true;
      }
      paramParcel1.enforceInterface("android.view.IRemoteAnimationRunner");
      onAnimationStart((RemoteAnimationTarget[])paramParcel1.createTypedArray(RemoteAnimationTarget.CREATOR), IRemoteAnimationFinishedCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
      return true;
    }
    
    private static class Proxy
      implements IRemoteAnimationRunner
    {
      public static IRemoteAnimationRunner sDefaultImpl;
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
        return "android.view.IRemoteAnimationRunner";
      }
      
      public void onAnimationCancelled()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IRemoteAnimationRunner");
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (IRemoteAnimationRunner.Stub.getDefaultImpl() != null))
          {
            IRemoteAnimationRunner.Stub.getDefaultImpl().onAnimationCancelled();
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onAnimationStart(RemoteAnimationTarget[] paramArrayOfRemoteAnimationTarget, IRemoteAnimationFinishedCallback paramIRemoteAnimationFinishedCallback)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IRemoteAnimationRunner");
          localParcel.writeTypedArray(paramArrayOfRemoteAnimationTarget, 0);
          IBinder localIBinder;
          if (paramIRemoteAnimationFinishedCallback != null) {
            localIBinder = paramIRemoteAnimationFinishedCallback.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IRemoteAnimationRunner.Stub.getDefaultImpl() != null))
          {
            IRemoteAnimationRunner.Stub.getDefaultImpl().onAnimationStart(paramArrayOfRemoteAnimationTarget, paramIRemoteAnimationFinishedCallback);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IRemoteAnimationRunner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */