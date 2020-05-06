package android.view.contentcapture;

import android.content.ContentCaptureOptions;
import android.content.pm.ParceledListSlice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IContentCaptureDirectManager
  extends IInterface
{
  public abstract void sendEvents(ParceledListSlice paramParceledListSlice, int paramInt, ContentCaptureOptions paramContentCaptureOptions)
    throws RemoteException;
  
  public static class Default
    implements IContentCaptureDirectManager
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void sendEvents(ParceledListSlice paramParceledListSlice, int paramInt, ContentCaptureOptions paramContentCaptureOptions)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IContentCaptureDirectManager
  {
    private static final String DESCRIPTOR = "android.view.contentcapture.IContentCaptureDirectManager";
    static final int TRANSACTION_sendEvents = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.contentcapture.IContentCaptureDirectManager");
    }
    
    public static IContentCaptureDirectManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.contentcapture.IContentCaptureDirectManager");
      if ((localIInterface != null) && ((localIInterface instanceof IContentCaptureDirectManager))) {
        return (IContentCaptureDirectManager)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IContentCaptureDirectManager getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1) {
        return null;
      }
      return "sendEvents";
    }
    
    public static boolean setDefaultImpl(IContentCaptureDirectManager paramIContentCaptureDirectManager)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIContentCaptureDirectManager != null))
      {
        Proxy.sDefaultImpl = paramIContentCaptureDirectManager;
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
        paramParcel2.writeString("android.view.contentcapture.IContentCaptureDirectManager");
        return true;
      }
      paramParcel1.enforceInterface("android.view.contentcapture.IContentCaptureDirectManager");
      if (paramParcel1.readInt() != 0) {
        paramParcel2 = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(paramParcel1);
      } else {
        paramParcel2 = null;
      }
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {
        paramParcel1 = (ContentCaptureOptions)ContentCaptureOptions.CREATOR.createFromParcel(paramParcel1);
      } else {
        paramParcel1 = null;
      }
      sendEvents(paramParcel2, paramInt1, paramParcel1);
      return true;
    }
    
    private static class Proxy
      implements IContentCaptureDirectManager
    {
      public static IContentCaptureDirectManager sDefaultImpl;
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
        return "android.view.contentcapture.IContentCaptureDirectManager";
      }
      
      public void sendEvents(ParceledListSlice paramParceledListSlice, int paramInt, ContentCaptureOptions paramContentCaptureOptions)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.contentcapture.IContentCaptureDirectManager");
          if (paramParceledListSlice != null)
          {
            localParcel.writeInt(1);
            paramParceledListSlice.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          localParcel.writeInt(paramInt);
          if (paramContentCaptureOptions != null)
          {
            localParcel.writeInt(1);
            paramContentCaptureOptions.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IContentCaptureDirectManager.Stub.getDefaultImpl() != null))
          {
            IContentCaptureDirectManager.Stub.getDefaultImpl().sendEvents(paramParceledListSlice, paramInt, paramContentCaptureOptions);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/contentcapture/IContentCaptureDirectManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */