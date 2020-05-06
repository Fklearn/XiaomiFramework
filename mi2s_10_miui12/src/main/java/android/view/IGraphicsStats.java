package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IGraphicsStats
  extends IInterface
{
  public abstract ParcelFileDescriptor requestBufferForProcess(String paramString, IGraphicsStatsCallback paramIGraphicsStatsCallback)
    throws RemoteException;
  
  public static class Default
    implements IGraphicsStats
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public ParcelFileDescriptor requestBufferForProcess(String paramString, IGraphicsStatsCallback paramIGraphicsStatsCallback)
      throws RemoteException
    {
      return null;
    }
  }
  
  public static abstract class Stub
    extends Binder
    implements IGraphicsStats
  {
    private static final String DESCRIPTOR = "android.view.IGraphicsStats";
    static final int TRANSACTION_requestBufferForProcess = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.IGraphicsStats");
    }
    
    public static IGraphicsStats asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IGraphicsStats");
      if ((localIInterface != null) && ((localIInterface instanceof IGraphicsStats))) {
        return (IGraphicsStats)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IGraphicsStats getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1) {
        return null;
      }
      return "requestBufferForProcess";
    }
    
    public static boolean setDefaultImpl(IGraphicsStats paramIGraphicsStats)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIGraphicsStats != null))
      {
        Proxy.sDefaultImpl = paramIGraphicsStats;
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
        paramParcel2.writeString("android.view.IGraphicsStats");
        return true;
      }
      paramParcel1.enforceInterface("android.view.IGraphicsStats");
      paramParcel1 = requestBufferForProcess(paramParcel1.readString(), IGraphicsStatsCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      if (paramParcel1 != null)
      {
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
      }
      else
      {
        paramParcel2.writeInt(0);
      }
      return true;
    }
    
    private static class Proxy
      implements IGraphicsStats
    {
      public static IGraphicsStats sDefaultImpl;
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
        return "android.view.IGraphicsStats";
      }
      
      public ParcelFileDescriptor requestBufferForProcess(String paramString, IGraphicsStatsCallback paramIGraphicsStatsCallback)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IGraphicsStats");
          localParcel1.writeString(paramString);
          IBinder localIBinder;
          if (paramIGraphicsStatsCallback != null) {
            localIBinder = paramIGraphicsStatsCallback.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (IGraphicsStats.Stub.getDefaultImpl() != null))
          {
            paramString = IGraphicsStats.Stub.getDefaultImpl().requestBufferForProcess(paramString, paramIGraphicsStatsCallback);
            return paramString;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            paramString = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(localParcel2);
          } else {
            paramString = null;
          }
          return paramString;
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IGraphicsStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */