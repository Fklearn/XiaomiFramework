package miui.slide;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ISlideChangeListener
  extends IInterface
{
  public abstract void onSlideChanged(int paramInt)
    throws RemoteException;
  
  public static class Default
    implements ISlideChangeListener
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void onSlideChanged(int paramInt)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements ISlideChangeListener
  {
    private static final String DESCRIPTOR = "miui.slide.ISlideChangeListener";
    static final int TRANSACTION_onSlideChanged = 1;
    
    public Stub()
    {
      attachInterface(this, "miui.slide.ISlideChangeListener");
    }
    
    public static ISlideChangeListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("miui.slide.ISlideChangeListener");
      if ((localIInterface != null) && ((localIInterface instanceof ISlideChangeListener))) {
        return (ISlideChangeListener)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static ISlideChangeListener getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1) {
        return null;
      }
      return "onSlideChanged";
    }
    
    public static boolean setDefaultImpl(ISlideChangeListener paramISlideChangeListener)
    {
      if ((Proxy.sDefaultImpl == null) && (paramISlideChangeListener != null))
      {
        Proxy.sDefaultImpl = paramISlideChangeListener;
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
        paramParcel2.writeString("miui.slide.ISlideChangeListener");
        return true;
      }
      paramParcel1.enforceInterface("miui.slide.ISlideChangeListener");
      onSlideChanged(paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements ISlideChangeListener
    {
      public static ISlideChangeListener sDefaultImpl;
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
        return "miui.slide.ISlideChangeListener";
      }
      
      public void onSlideChanged(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("miui.slide.ISlideChangeListener");
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (ISlideChangeListener.Stub.getDefaultImpl() != null))
          {
            ISlideChangeListener.Stub.getDefaultImpl().onSlideChanged(paramInt);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/slide/ISlideChangeListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */