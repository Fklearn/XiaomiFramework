package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IWindowSessionCallback
  extends IInterface
{
  public abstract void onAnimatorScaleChanged(float paramFloat)
    throws RemoteException;
  
  public static class Default
    implements IWindowSessionCallback
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void onAnimatorScaleChanged(float paramFloat)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IWindowSessionCallback
  {
    private static final String DESCRIPTOR = "android.view.IWindowSessionCallback";
    static final int TRANSACTION_onAnimatorScaleChanged = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.IWindowSessionCallback");
    }
    
    public static IWindowSessionCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IWindowSessionCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IWindowSessionCallback))) {
        return (IWindowSessionCallback)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IWindowSessionCallback getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1) {
        return null;
      }
      return "onAnimatorScaleChanged";
    }
    
    public static boolean setDefaultImpl(IWindowSessionCallback paramIWindowSessionCallback)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIWindowSessionCallback != null))
      {
        Proxy.sDefaultImpl = paramIWindowSessionCallback;
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
        paramParcel2.writeString("android.view.IWindowSessionCallback");
        return true;
      }
      paramParcel1.enforceInterface("android.view.IWindowSessionCallback");
      onAnimatorScaleChanged(paramParcel1.readFloat());
      return true;
    }
    
    private static class Proxy
      implements IWindowSessionCallback
    {
      public static IWindowSessionCallback sDefaultImpl;
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
        return "android.view.IWindowSessionCallback";
      }
      
      public void onAnimatorScaleChanged(float paramFloat)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindowSessionCallback");
          localParcel.writeFloat(paramFloat);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IWindowSessionCallback.Stub.getDefaultImpl() != null))
          {
            IWindowSessionCallback.Stub.getDefaultImpl().onAnimatorScaleChanged(paramFloat);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IWindowSessionCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */