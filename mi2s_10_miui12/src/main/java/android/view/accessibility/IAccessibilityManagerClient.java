package android.view.accessibility;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IAccessibilityManagerClient
  extends IInterface
{
  public abstract void notifyServicesStateChanged(long paramLong)
    throws RemoteException;
  
  public abstract void setRelevantEventTypes(int paramInt)
    throws RemoteException;
  
  public abstract void setState(int paramInt)
    throws RemoteException;
  
  public static class Default
    implements IAccessibilityManagerClient
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void notifyServicesStateChanged(long paramLong)
      throws RemoteException
    {}
    
    public void setRelevantEventTypes(int paramInt)
      throws RemoteException
    {}
    
    public void setState(int paramInt)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IAccessibilityManagerClient
  {
    private static final String DESCRIPTOR = "android.view.accessibility.IAccessibilityManagerClient";
    static final int TRANSACTION_notifyServicesStateChanged = 2;
    static final int TRANSACTION_setRelevantEventTypes = 3;
    static final int TRANSACTION_setState = 1;
    
    public Stub()
    {
      attachInterface(this, "android.view.accessibility.IAccessibilityManagerClient");
    }
    
    public static IAccessibilityManagerClient asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.accessibility.IAccessibilityManagerClient");
      if ((localIInterface != null) && ((localIInterface instanceof IAccessibilityManagerClient))) {
        return (IAccessibilityManagerClient)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IAccessibilityManagerClient getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          if (paramInt != 3) {
            return null;
          }
          return "setRelevantEventTypes";
        }
        return "notifyServicesStateChanged";
      }
      return "setState";
    }
    
    public static boolean setDefaultImpl(IAccessibilityManagerClient paramIAccessibilityManagerClient)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIAccessibilityManagerClient != null))
      {
        Proxy.sDefaultImpl = paramIAccessibilityManagerClient;
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
            if (paramInt1 != 1598968902) {
              return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
            }
            paramParcel2.writeString("android.view.accessibility.IAccessibilityManagerClient");
            return true;
          }
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManagerClient");
          setRelevantEventTypes(paramParcel1.readInt());
          return true;
        }
        paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManagerClient");
        notifyServicesStateChanged(paramParcel1.readLong());
        return true;
      }
      paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManagerClient");
      setState(paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IAccessibilityManagerClient
    {
      public static IAccessibilityManagerClient sDefaultImpl;
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
        return "android.view.accessibility.IAccessibilityManagerClient";
      }
      
      public void notifyServicesStateChanged(long paramLong)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.accessibility.IAccessibilityManagerClient");
          localParcel.writeLong(paramLong);
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (IAccessibilityManagerClient.Stub.getDefaultImpl() != null))
          {
            IAccessibilityManagerClient.Stub.getDefaultImpl().notifyServicesStateChanged(paramLong);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setRelevantEventTypes(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.accessibility.IAccessibilityManagerClient");
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(3, localParcel, null, 1)) && (IAccessibilityManagerClient.Stub.getDefaultImpl() != null))
          {
            IAccessibilityManagerClient.Stub.getDefaultImpl().setRelevantEventTypes(paramInt);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setState(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.accessibility.IAccessibilityManagerClient");
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IAccessibilityManagerClient.Stub.getDefaultImpl() != null))
          {
            IAccessibilityManagerClient.Stub.getDefaultImpl().setState(paramInt);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/accessibility/IAccessibilityManagerClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */