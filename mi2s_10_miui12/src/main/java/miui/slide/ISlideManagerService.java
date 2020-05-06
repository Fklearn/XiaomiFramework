package miui.slide;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface ISlideManagerService
  extends IInterface
{
  public abstract AppSlideConfig getAppSlideConfig(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getCameraStatus()
    throws RemoteException;
  
  public abstract void registerSlideChangeListener(String paramString, ISlideChangeListener paramISlideChangeListener)
    throws RemoteException;
  
  public abstract void unregisterSlideChangeListener(ISlideChangeListener paramISlideChangeListener)
    throws RemoteException;
  
  public static class Default
    implements ISlideManagerService
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public AppSlideConfig getAppSlideConfig(String paramString, int paramInt)
      throws RemoteException
    {
      return null;
    }
    
    public int getCameraStatus()
      throws RemoteException
    {
      return 0;
    }
    
    public void registerSlideChangeListener(String paramString, ISlideChangeListener paramISlideChangeListener)
      throws RemoteException
    {}
    
    public void unregisterSlideChangeListener(ISlideChangeListener paramISlideChangeListener)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements ISlideManagerService
  {
    private static final String DESCRIPTOR = "miui.slide.ISlideManagerService";
    static final int TRANSACTION_getAppSlideConfig = 1;
    static final int TRANSACTION_getCameraStatus = 2;
    static final int TRANSACTION_registerSlideChangeListener = 3;
    static final int TRANSACTION_unregisterSlideChangeListener = 4;
    
    public Stub()
    {
      attachInterface(this, "miui.slide.ISlideManagerService");
    }
    
    public static ISlideManagerService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("miui.slide.ISlideManagerService");
      if ((localIInterface != null) && ((localIInterface instanceof ISlideManagerService))) {
        return (ISlideManagerService)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static ISlideManagerService getDefaultImpl()
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
            return "unregisterSlideChangeListener";
          }
          return "registerSlideChangeListener";
        }
        return "getCameraStatus";
      }
      return "getAppSlideConfig";
    }
    
    public static boolean setDefaultImpl(ISlideManagerService paramISlideManagerService)
    {
      if ((Proxy.sDefaultImpl == null) && (paramISlideManagerService != null))
      {
        Proxy.sDefaultImpl = paramISlideManagerService;
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
              paramParcel2.writeString("miui.slide.ISlideManagerService");
              return true;
            }
            paramParcel1.enforceInterface("miui.slide.ISlideManagerService");
            unregisterSlideChangeListener(ISlideChangeListener.Stub.asInterface(paramParcel1.readStrongBinder()));
            paramParcel2.writeNoException();
            return true;
          }
          paramParcel1.enforceInterface("miui.slide.ISlideManagerService");
          registerSlideChangeListener(paramParcel1.readString(), ISlideChangeListener.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        }
        paramParcel1.enforceInterface("miui.slide.ISlideManagerService");
        paramInt1 = getCameraStatus();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("miui.slide.ISlideManagerService");
      paramParcel1 = getAppSlideConfig(paramParcel1.readString(), paramParcel1.readInt());
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
      implements ISlideManagerService
    {
      public static ISlideManagerService sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public AppSlideConfig getAppSlideConfig(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.slide.ISlideManagerService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (ISlideManagerService.Stub.getDefaultImpl() != null))
          {
            paramString = ISlideManagerService.Stub.getDefaultImpl().getAppSlideConfig(paramString, paramInt);
            return paramString;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            paramString = (AppSlideConfig)AppSlideConfig.CREATOR.createFromParcel(localParcel2);
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
      
      public int getCameraStatus()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.slide.ISlideManagerService");
          if ((!this.mRemote.transact(2, localParcel1, localParcel2, 0)) && (ISlideManagerService.Stub.getDefaultImpl() != null))
          {
            i = ISlideManagerService.Stub.getDefaultImpl().getCameraStatus();
            return i;
          }
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "miui.slide.ISlideManagerService";
      }
      
      public void registerSlideChangeListener(String paramString, ISlideChangeListener paramISlideChangeListener)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.slide.ISlideManagerService");
          localParcel1.writeString(paramString);
          IBinder localIBinder;
          if (paramISlideChangeListener != null) {
            localIBinder = paramISlideChangeListener.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(3, localParcel1, localParcel2, 0)) && (ISlideManagerService.Stub.getDefaultImpl() != null))
          {
            ISlideManagerService.Stub.getDefaultImpl().registerSlideChangeListener(paramString, paramISlideChangeListener);
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
      
      public void unregisterSlideChangeListener(ISlideChangeListener paramISlideChangeListener)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.slide.ISlideManagerService");
          IBinder localIBinder;
          if (paramISlideChangeListener != null) {
            localIBinder = paramISlideChangeListener.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(4, localParcel1, localParcel2, 0)) && (ISlideManagerService.Stub.getDefaultImpl() != null))
          {
            ISlideManagerService.Stub.getDefaultImpl().unregisterSlideChangeListener(paramISlideChangeListener);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/slide/ISlideManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */