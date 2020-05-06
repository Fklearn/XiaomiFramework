package android.view.contentcapture;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.android.internal.os.IResultReceiver;
import com.android.internal.os.IResultReceiver.Stub;

public abstract interface IContentCaptureManager
  extends IInterface
{
  public abstract void finishSession(int paramInt)
    throws RemoteException;
  
  public abstract void getContentCaptureConditions(String paramString, IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void getServiceComponentName(IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void getServiceSettingsActivity(IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void isContentCaptureFeatureEnabled(IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public abstract void removeData(DataRemovalRequest paramDataRemovalRequest)
    throws RemoteException;
  
  public abstract void startSession(IBinder paramIBinder, ComponentName paramComponentName, int paramInt1, int paramInt2, IResultReceiver paramIResultReceiver)
    throws RemoteException;
  
  public static class Default
    implements IContentCaptureManager
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void finishSession(int paramInt)
      throws RemoteException
    {}
    
    public void getContentCaptureConditions(String paramString, IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void getServiceComponentName(IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void getServiceSettingsActivity(IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void isContentCaptureFeatureEnabled(IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
    
    public void removeData(DataRemovalRequest paramDataRemovalRequest)
      throws RemoteException
    {}
    
    public void startSession(IBinder paramIBinder, ComponentName paramComponentName, int paramInt1, int paramInt2, IResultReceiver paramIResultReceiver)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IContentCaptureManager
  {
    private static final String DESCRIPTOR = "android.view.contentcapture.IContentCaptureManager";
    static final int TRANSACTION_finishSession = 2;
    static final int TRANSACTION_getContentCaptureConditions = 7;
    static final int TRANSACTION_getServiceComponentName = 3;
    static final int TRANSACTION_getServiceSettingsActivity = 6;
    static final int TRANSACTION_isContentCaptureFeatureEnabled = 5;
    static final int TRANSACTION_removeData = 4;
    static final int TRANSACTION_startSession = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.contentcapture.IContentCaptureManager");
    }
    
    public static IContentCaptureManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.contentcapture.IContentCaptureManager");
      if ((localIInterface != null) && ((localIInterface instanceof IContentCaptureManager))) {
        return (IContentCaptureManager)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IContentCaptureManager getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return null;
      case 7: 
        return "getContentCaptureConditions";
      case 6: 
        return "getServiceSettingsActivity";
      case 5: 
        return "isContentCaptureFeatureEnabled";
      case 4: 
        return "removeData";
      case 3: 
        return "getServiceComponentName";
      case 2: 
        return "finishSession";
      }
      return "startSession";
    }
    
    public static boolean setDefaultImpl(IContentCaptureManager paramIContentCaptureManager)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIContentCaptureManager != null))
      {
        Proxy.sDefaultImpl = paramIContentCaptureManager;
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
      if (paramInt1 != 1598968902)
      {
        switch (paramInt1)
        {
        default: 
          return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        case 7: 
          paramParcel1.enforceInterface("android.view.contentcapture.IContentCaptureManager");
          getContentCaptureConditions(paramParcel1.readString(), IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 6: 
          paramParcel1.enforceInterface("android.view.contentcapture.IContentCaptureManager");
          getServiceSettingsActivity(IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 5: 
          paramParcel1.enforceInterface("android.view.contentcapture.IContentCaptureManager");
          isContentCaptureFeatureEnabled(IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 4: 
          paramParcel1.enforceInterface("android.view.contentcapture.IContentCaptureManager");
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (DataRemovalRequest)DataRemovalRequest.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          removeData(paramParcel1);
          return true;
        case 3: 
          paramParcel1.enforceInterface("android.view.contentcapture.IContentCaptureManager");
          getServiceComponentName(IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        case 2: 
          paramParcel1.enforceInterface("android.view.contentcapture.IContentCaptureManager");
          finishSession(paramParcel1.readInt());
          return true;
        }
        paramParcel1.enforceInterface("android.view.contentcapture.IContentCaptureManager");
        IBinder localIBinder = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {
          paramParcel2 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
        } else {
          paramParcel2 = null;
        }
        startSession(localIBinder, paramParcel2, paramParcel1.readInt(), paramParcel1.readInt(), IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      }
      paramParcel2.writeString("android.view.contentcapture.IContentCaptureManager");
      return true;
    }
    
    private static class Proxy
      implements IContentCaptureManager
    {
      public static IContentCaptureManager sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void finishSession(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.contentcapture.IContentCaptureManager");
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (IContentCaptureManager.Stub.getDefaultImpl() != null))
          {
            IContentCaptureManager.Stub.getDefaultImpl().finishSession(paramInt);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void getContentCaptureConditions(String paramString, IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.contentcapture.IContentCaptureManager");
          localParcel.writeString(paramString);
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(7, localParcel, null, 1)) && (IContentCaptureManager.Stub.getDefaultImpl() != null))
          {
            IContentCaptureManager.Stub.getDefaultImpl().getContentCaptureConditions(paramString, paramIResultReceiver);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.view.contentcapture.IContentCaptureManager";
      }
      
      public void getServiceComponentName(IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.contentcapture.IContentCaptureManager");
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(3, localParcel, null, 1)) && (IContentCaptureManager.Stub.getDefaultImpl() != null))
          {
            IContentCaptureManager.Stub.getDefaultImpl().getServiceComponentName(paramIResultReceiver);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void getServiceSettingsActivity(IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.contentcapture.IContentCaptureManager");
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(6, localParcel, null, 1)) && (IContentCaptureManager.Stub.getDefaultImpl() != null))
          {
            IContentCaptureManager.Stub.getDefaultImpl().getServiceSettingsActivity(paramIResultReceiver);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void isContentCaptureFeatureEnabled(IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.contentcapture.IContentCaptureManager");
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(5, localParcel, null, 1)) && (IContentCaptureManager.Stub.getDefaultImpl() != null))
          {
            IContentCaptureManager.Stub.getDefaultImpl().isContentCaptureFeatureEnabled(paramIResultReceiver);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void removeData(DataRemovalRequest paramDataRemovalRequest)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.contentcapture.IContentCaptureManager");
          if (paramDataRemovalRequest != null)
          {
            localParcel.writeInt(1);
            paramDataRemovalRequest.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(4, localParcel, null, 1)) && (IContentCaptureManager.Stub.getDefaultImpl() != null))
          {
            IContentCaptureManager.Stub.getDefaultImpl().removeData(paramDataRemovalRequest);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void startSession(IBinder paramIBinder, ComponentName paramComponentName, int paramInt1, int paramInt2, IResultReceiver paramIResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.contentcapture.IContentCaptureManager");
          localParcel.writeStrongBinder(paramIBinder);
          if (paramComponentName != null)
          {
            localParcel.writeInt(1);
            paramComponentName.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IContentCaptureManager.Stub.getDefaultImpl() != null))
          {
            IContentCaptureManager.Stub.getDefaultImpl().startSession(paramIBinder, paramComponentName, paramInt1, paramInt2, paramIResultReceiver);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/contentcapture/IContentCaptureManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */