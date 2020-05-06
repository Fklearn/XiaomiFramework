package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IAppTransitionAnimationSpecsFuture
  extends IInterface
{
  public abstract AppTransitionAnimationSpec[] get()
    throws RemoteException;
  
  public static class Default
    implements IAppTransitionAnimationSpecsFuture
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public AppTransitionAnimationSpec[] get()
      throws RemoteException
    {
      return null;
    }
  }
  
  public static abstract class Stub
    extends Binder
    implements IAppTransitionAnimationSpecsFuture
  {
    private static final String DESCRIPTOR = "android.view.IAppTransitionAnimationSpecsFuture";
    static final int TRANSACTION_get = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.IAppTransitionAnimationSpecsFuture");
    }
    
    public static IAppTransitionAnimationSpecsFuture asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IAppTransitionAnimationSpecsFuture");
      if ((localIInterface != null) && ((localIInterface instanceof IAppTransitionAnimationSpecsFuture))) {
        return (IAppTransitionAnimationSpecsFuture)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IAppTransitionAnimationSpecsFuture getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1) {
        return null;
      }
      return "get";
    }
    
    public static boolean setDefaultImpl(IAppTransitionAnimationSpecsFuture paramIAppTransitionAnimationSpecsFuture)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIAppTransitionAnimationSpecsFuture != null))
      {
        Proxy.sDefaultImpl = paramIAppTransitionAnimationSpecsFuture;
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
        paramParcel2.writeString("android.view.IAppTransitionAnimationSpecsFuture");
        return true;
      }
      paramParcel1.enforceInterface("android.view.IAppTransitionAnimationSpecsFuture");
      paramParcel1 = get();
      paramParcel2.writeNoException();
      paramParcel2.writeTypedArray(paramParcel1, 1);
      return true;
    }
    
    private static class Proxy
      implements IAppTransitionAnimationSpecsFuture
    {
      public static IAppTransitionAnimationSpecsFuture sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public AppTransitionAnimationSpec[] get()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IAppTransitionAnimationSpecsFuture");
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (IAppTransitionAnimationSpecsFuture.Stub.getDefaultImpl() != null))
          {
            arrayOfAppTransitionAnimationSpec = IAppTransitionAnimationSpecsFuture.Stub.getDefaultImpl().get();
            return arrayOfAppTransitionAnimationSpec;
          }
          localParcel2.readException();
          AppTransitionAnimationSpec[] arrayOfAppTransitionAnimationSpec = (AppTransitionAnimationSpec[])localParcel2.createTypedArray(AppTransitionAnimationSpec.CREATOR);
          return arrayOfAppTransitionAnimationSpec;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.view.IAppTransitionAnimationSpecsFuture";
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IAppTransitionAnimationSpecsFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */