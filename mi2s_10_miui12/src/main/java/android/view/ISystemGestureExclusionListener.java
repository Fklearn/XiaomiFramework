package android.view;

import android.graphics.Region;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface ISystemGestureExclusionListener
  extends IInterface
{
  public abstract void onSystemGestureExclusionChanged(int paramInt, Region paramRegion)
    throws RemoteException;
  
  public static class Default
    implements ISystemGestureExclusionListener
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void onSystemGestureExclusionChanged(int paramInt, Region paramRegion)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements ISystemGestureExclusionListener
  {
    private static final String DESCRIPTOR = "android.view.ISystemGestureExclusionListener";
    static final int TRANSACTION_onSystemGestureExclusionChanged = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.ISystemGestureExclusionListener");
    }
    
    public static ISystemGestureExclusionListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.ISystemGestureExclusionListener");
      if ((localIInterface != null) && ((localIInterface instanceof ISystemGestureExclusionListener))) {
        return (ISystemGestureExclusionListener)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static ISystemGestureExclusionListener getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1) {
        return null;
      }
      return "onSystemGestureExclusionChanged";
    }
    
    public static boolean setDefaultImpl(ISystemGestureExclusionListener paramISystemGestureExclusionListener)
    {
      if ((Proxy.sDefaultImpl == null) && (paramISystemGestureExclusionListener != null))
      {
        Proxy.sDefaultImpl = paramISystemGestureExclusionListener;
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
        paramParcel2.writeString("android.view.ISystemGestureExclusionListener");
        return true;
      }
      paramParcel1.enforceInterface("android.view.ISystemGestureExclusionListener");
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {
        paramParcel1 = (Region)Region.CREATOR.createFromParcel(paramParcel1);
      } else {
        paramParcel1 = null;
      }
      onSystemGestureExclusionChanged(paramInt1, paramParcel1);
      return true;
    }
    
    private static class Proxy
      implements ISystemGestureExclusionListener
    {
      public static ISystemGestureExclusionListener sDefaultImpl;
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
        return "android.view.ISystemGestureExclusionListener";
      }
      
      public void onSystemGestureExclusionChanged(int paramInt, Region paramRegion)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.ISystemGestureExclusionListener");
          localParcel.writeInt(paramInt);
          if (paramRegion != null)
          {
            localParcel.writeInt(1);
            paramRegion.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (ISystemGestureExclusionListener.Stub.getDefaultImpl() != null))
          {
            ISystemGestureExclusionListener.Stub.getDefaultImpl().onSystemGestureExclusionChanged(paramInt, paramRegion);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ISystemGestureExclusionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */