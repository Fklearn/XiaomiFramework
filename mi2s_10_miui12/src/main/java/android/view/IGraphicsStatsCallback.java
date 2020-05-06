package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IGraphicsStatsCallback
  extends IInterface
{
  public abstract void onRotateGraphicsStatsBuffer()
    throws RemoteException;
  
  public static class Default
    implements IGraphicsStatsCallback
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void onRotateGraphicsStatsBuffer()
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IGraphicsStatsCallback
  {
    private static final String DESCRIPTOR = "android.view.IGraphicsStatsCallback";
    static final int TRANSACTION_onRotateGraphicsStatsBuffer = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.IGraphicsStatsCallback");
    }
    
    public static IGraphicsStatsCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IGraphicsStatsCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IGraphicsStatsCallback))) {
        return (IGraphicsStatsCallback)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IGraphicsStatsCallback getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1) {
        return null;
      }
      return "onRotateGraphicsStatsBuffer";
    }
    
    public static boolean setDefaultImpl(IGraphicsStatsCallback paramIGraphicsStatsCallback)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIGraphicsStatsCallback != null))
      {
        Proxy.sDefaultImpl = paramIGraphicsStatsCallback;
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
        paramParcel2.writeString("android.view.IGraphicsStatsCallback");
        return true;
      }
      paramParcel1.enforceInterface("android.view.IGraphicsStatsCallback");
      onRotateGraphicsStatsBuffer();
      return true;
    }
    
    private static class Proxy
      implements IGraphicsStatsCallback
    {
      public static IGraphicsStatsCallback sDefaultImpl;
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
        return "android.view.IGraphicsStatsCallback";
      }
      
      public void onRotateGraphicsStatsBuffer()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IGraphicsStatsCallback");
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IGraphicsStatsCallback.Stub.getDefaultImpl() != null))
          {
            IGraphicsStatsCallback.Stub.getDefaultImpl().onRotateGraphicsStatsBuffer();
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IGraphicsStatsCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */