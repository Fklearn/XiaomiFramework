package miui.slide;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ISliderViewService
  extends IInterface
{
  public abstract String getDumpContent(String paramString)
    throws RemoteException;
  
  public abstract void playSound(int paramInt)
    throws RemoteException;
  
  public abstract void removeSliderView(int paramInt)
    throws RemoteException;
  
  public abstract void showSliderView(int paramInt)
    throws RemoteException;
  
  public static class Default
    implements ISliderViewService
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public String getDumpContent(String paramString)
      throws RemoteException
    {
      return null;
    }
    
    public void playSound(int paramInt)
      throws RemoteException
    {}
    
    public void removeSliderView(int paramInt)
      throws RemoteException
    {}
    
    public void showSliderView(int paramInt)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements ISliderViewService
  {
    private static final String DESCRIPTOR = "miui.slide.ISliderViewService";
    static final int TRANSACTION_getDumpContent = 4;
    static final int TRANSACTION_playSound = 3;
    static final int TRANSACTION_removeSliderView = 2;
    static final int TRANSACTION_showSliderView = 1;
    
    public Stub()
    {
      attachInterface(this, "miui.slide.ISliderViewService");
    }
    
    public static ISliderViewService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("miui.slide.ISliderViewService");
      if ((localIInterface != null) && ((localIInterface instanceof ISliderViewService))) {
        return (ISliderViewService)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static ISliderViewService getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          if (paramInt != 3)
          {
            if (paramInt != 4) {
              return null;
            }
            return "getDumpContent";
          }
          return "playSound";
        }
        return "removeSliderView";
      }
      return "showSliderView";
    }
    
    public static boolean setDefaultImpl(ISliderViewService paramISliderViewService)
    {
      if ((Proxy.sDefaultImpl == null) && (paramISliderViewService != null))
      {
        Proxy.sDefaultImpl = paramISliderViewService;
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
          if (paramInt1 != 3)
          {
            if (paramInt1 != 4)
            {
              if (paramInt1 != 1598968902) {
                return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
              }
              paramParcel2.writeString("miui.slide.ISliderViewService");
              return true;
            }
            paramParcel1.enforceInterface("miui.slide.ISliderViewService");
            paramParcel1 = getDumpContent(paramParcel1.readString());
            paramParcel2.writeNoException();
            paramParcel2.writeString(paramParcel1);
            return true;
          }
          paramParcel1.enforceInterface("miui.slide.ISliderViewService");
          playSound(paramParcel1.readInt());
          return true;
        }
        paramParcel1.enforceInterface("miui.slide.ISliderViewService");
        removeSliderView(paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("miui.slide.ISliderViewService");
      showSliderView(paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements ISliderViewService
    {
      public static ISliderViewService sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public String getDumpContent(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.slide.ISliderViewService");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(4, localParcel1, localParcel2, 0)) && (ISliderViewService.Stub.getDefaultImpl() != null))
          {
            paramString = ISliderViewService.Stub.getDefaultImpl().getDumpContent(paramString);
            return paramString;
          }
          localParcel2.readException();
          paramString = localParcel2.readString();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "miui.slide.ISliderViewService";
      }
      
      public void playSound(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("miui.slide.ISliderViewService");
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(3, localParcel, null, 1)) && (ISliderViewService.Stub.getDefaultImpl() != null))
          {
            ISliderViewService.Stub.getDefaultImpl().playSound(paramInt);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void removeSliderView(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("miui.slide.ISliderViewService");
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (ISliderViewService.Stub.getDefaultImpl() != null))
          {
            ISliderViewService.Stub.getDefaultImpl().removeSliderView(paramInt);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void showSliderView(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("miui.slide.ISliderViewService");
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (ISliderViewService.Stub.getDefaultImpl() != null))
          {
            ISliderViewService.Stub.getDefaultImpl().showSliderView(paramInt);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/slide/ISliderViewService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */