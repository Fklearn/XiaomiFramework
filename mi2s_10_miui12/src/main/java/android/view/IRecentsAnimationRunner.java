package android.view;

import android.annotation.UnsupportedAppUsage;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IRecentsAnimationRunner
  extends IInterface
{
  @UnsupportedAppUsage
  public abstract void onAnimationCanceled(boolean paramBoolean)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void onAnimationStart(IRecentsAnimationController paramIRecentsAnimationController, RemoteAnimationTarget[] paramArrayOfRemoteAnimationTarget, Rect paramRect1, Rect paramRect2)
    throws RemoteException;
  
  public static class Default
    implements IRecentsAnimationRunner
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void onAnimationCanceled(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void onAnimationStart(IRecentsAnimationController paramIRecentsAnimationController, RemoteAnimationTarget[] paramArrayOfRemoteAnimationTarget, Rect paramRect1, Rect paramRect2)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IRecentsAnimationRunner
  {
    private static final String DESCRIPTOR = "android.view.IRecentsAnimationRunner";
    static final int TRANSACTION_onAnimationCanceled = 2;
    static final int TRANSACTION_onAnimationStart = 3;
    
    public Stub()
    {
      attachInterface(this, "android.view.IRecentsAnimationRunner");
    }
    
    public static IRecentsAnimationRunner asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IRecentsAnimationRunner");
      if ((localIInterface != null) && ((localIInterface instanceof IRecentsAnimationRunner))) {
        return (IRecentsAnimationRunner)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IRecentsAnimationRunner getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 2)
      {
        if (paramInt != 3) {
          return null;
        }
        return "onAnimationStart";
      }
      return "onAnimationCanceled";
    }
    
    public static boolean setDefaultImpl(IRecentsAnimationRunner paramIRecentsAnimationRunner)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIRecentsAnimationRunner != null))
      {
        Proxy.sDefaultImpl = paramIRecentsAnimationRunner;
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
      if (paramInt1 != 2)
      {
        if (paramInt1 != 3)
        {
          if (paramInt1 != 1598968902) {
            return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
          }
          paramParcel2.writeString("android.view.IRecentsAnimationRunner");
          return true;
        }
        paramParcel1.enforceInterface("android.view.IRecentsAnimationRunner");
        IRecentsAnimationController localIRecentsAnimationController = IRecentsAnimationController.Stub.asInterface(paramParcel1.readStrongBinder());
        RemoteAnimationTarget[] arrayOfRemoteAnimationTarget = (RemoteAnimationTarget[])paramParcel1.createTypedArray(RemoteAnimationTarget.CREATOR);
        if (paramParcel1.readInt() != 0) {
          paramParcel2 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
        } else {
          paramParcel2 = null;
        }
        if (paramParcel1.readInt() != 0) {
          paramParcel1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
        } else {
          paramParcel1 = null;
        }
        onAnimationStart(localIRecentsAnimationController, arrayOfRemoteAnimationTarget, paramParcel2, paramParcel1);
        return true;
      }
      paramParcel1.enforceInterface("android.view.IRecentsAnimationRunner");
      boolean bool;
      if (paramParcel1.readInt() != 0) {
        bool = true;
      } else {
        bool = false;
      }
      onAnimationCanceled(bool);
      return true;
    }
    
    private static class Proxy
      implements IRecentsAnimationRunner
    {
      public static IRecentsAnimationRunner sDefaultImpl;
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
        return "android.view.IRecentsAnimationRunner";
      }
      
      public void onAnimationCanceled(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IRecentsAnimationRunner");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (IRecentsAnimationRunner.Stub.getDefaultImpl() != null))
          {
            IRecentsAnimationRunner.Stub.getDefaultImpl().onAnimationCanceled(paramBoolean);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onAnimationStart(IRecentsAnimationController paramIRecentsAnimationController, RemoteAnimationTarget[] paramArrayOfRemoteAnimationTarget, Rect paramRect1, Rect paramRect2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IRecentsAnimationRunner");
          IBinder localIBinder;
          if (paramIRecentsAnimationController != null) {
            localIBinder = paramIRecentsAnimationController.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeTypedArray(paramArrayOfRemoteAnimationTarget, 0);
          if (paramRect1 != null)
          {
            localParcel.writeInt(1);
            paramRect1.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if (paramRect2 != null)
          {
            localParcel.writeInt(1);
            paramRect2.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(3, localParcel, null, 1)) && (IRecentsAnimationRunner.Stub.getDefaultImpl() != null))
          {
            IRecentsAnimationRunner.Stub.getDefaultImpl().onAnimationStart(paramIRecentsAnimationController, paramArrayOfRemoteAnimationTarget, paramRect1, paramRect2);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IRecentsAnimationRunner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */