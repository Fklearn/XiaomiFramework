package miui.security;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.List;

public abstract interface IAppRunningControlManager
  extends IInterface
{
  public abstract Intent getBlockActivityIntent(String paramString, Intent paramIntent, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract List<String> getNotDisallowList()
    throws RemoteException;
  
  public abstract boolean matchRule(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void setBlackListEnable(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setDisallowRunningList(List<String> paramList, Intent paramIntent)
    throws RemoteException;
  
  public static class Default
    implements IAppRunningControlManager
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public Intent getBlockActivityIntent(String paramString, Intent paramIntent, boolean paramBoolean, int paramInt)
      throws RemoteException
    {
      return null;
    }
    
    public List<String> getNotDisallowList()
      throws RemoteException
    {
      return null;
    }
    
    public boolean matchRule(String paramString, int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public void setBlackListEnable(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setDisallowRunningList(List<String> paramList, Intent paramIntent)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IAppRunningControlManager
  {
    private static final String DESCRIPTOR = "miui.security.IAppRunningControlManager";
    static final int TRANSACTION_getBlockActivityIntent = 2;
    static final int TRANSACTION_getNotDisallowList = 5;
    static final int TRANSACTION_matchRule = 4;
    static final int TRANSACTION_setBlackListEnable = 3;
    static final int TRANSACTION_setDisallowRunningList = 1;
    
    public Stub()
    {
      attachInterface(this, "miui.security.IAppRunningControlManager");
    }
    
    public static IAppRunningControlManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("miui.security.IAppRunningControlManager");
      if ((localIInterface != null) && ((localIInterface instanceof IAppRunningControlManager))) {
        return (IAppRunningControlManager)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IAppRunningControlManager getDefaultImpl()
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
            if (paramInt != 4)
            {
              if (paramInt != 5) {
                return null;
              }
              return "getNotDisallowList";
            }
            return "matchRule";
          }
          return "setBlackListEnable";
        }
        return "getBlockActivityIntent";
      }
      return "setDisallowRunningList";
    }
    
    public static boolean setDefaultImpl(IAppRunningControlManager paramIAppRunningControlManager)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIAppRunningControlManager != null))
      {
        Proxy.sDefaultImpl = paramIAppRunningControlManager;
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
        boolean bool = false;
        if (paramInt1 != 2)
        {
          if (paramInt1 != 3)
          {
            if (paramInt1 != 4)
            {
              if (paramInt1 != 5)
              {
                if (paramInt1 != 1598968902) {
                  return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
                }
                paramParcel2.writeString("miui.security.IAppRunningControlManager");
                return true;
              }
              paramParcel1.enforceInterface("miui.security.IAppRunningControlManager");
              paramParcel1 = getNotDisallowList();
              paramParcel2.writeNoException();
              paramParcel2.writeStringList(paramParcel1);
              return true;
            }
            paramParcel1.enforceInterface("miui.security.IAppRunningControlManager");
            paramInt1 = matchRule(paramParcel1.readString(), paramParcel1.readInt());
            paramParcel2.writeNoException();
            paramParcel2.writeInt(paramInt1);
            return true;
          }
          paramParcel1.enforceInterface("miui.security.IAppRunningControlManager");
          if (paramParcel1.readInt() != 0) {
            bool = true;
          }
          setBlackListEnable(bool);
          paramParcel2.writeNoException();
          return true;
        }
        paramParcel1.enforceInterface("miui.security.IAppRunningControlManager");
        String str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {
          localObject = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
        } else {
          localObject = null;
        }
        if (paramParcel1.readInt() != 0) {
          bool = true;
        } else {
          bool = false;
        }
        paramParcel1 = getBlockActivityIntent(str, (Intent)localObject, bool, paramParcel1.readInt());
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
      paramParcel1.enforceInterface("miui.security.IAppRunningControlManager");
      Object localObject = paramParcel1.createStringArrayList();
      if (paramParcel1.readInt() != 0) {
        paramParcel1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
      } else {
        paramParcel1 = null;
      }
      setDisallowRunningList((List)localObject, paramParcel1);
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IAppRunningControlManager
    {
      public static IAppRunningControlManager sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public Intent getBlockActivityIntent(String paramString, Intent paramIntent, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.IAppRunningControlManager");
          localParcel1.writeString(paramString);
          int i = 1;
          if (paramIntent != null)
          {
            localParcel1.writeInt(1);
            paramIntent.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if (!paramBoolean) {
            i = 0;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(2, localParcel1, localParcel2, 0)) && (IAppRunningControlManager.Stub.getDefaultImpl() != null))
          {
            paramString = IAppRunningControlManager.Stub.getDefaultImpl().getBlockActivityIntent(paramString, paramIntent, paramBoolean, paramInt);
            return paramString;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            paramString = (Intent)Intent.CREATOR.createFromParcel(localParcel2);
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
      
      public String getInterfaceDescriptor()
      {
        return "miui.security.IAppRunningControlManager";
      }
      
      public List<String> getNotDisallowList()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.IAppRunningControlManager");
          if ((!this.mRemote.transact(5, localParcel1, localParcel2, 0)) && (IAppRunningControlManager.Stub.getDefaultImpl() != null))
          {
            localObject1 = IAppRunningControlManager.Stub.getDefaultImpl().getNotDisallowList();
            return (List<String>)localObject1;
          }
          localParcel2.readException();
          Object localObject1 = localParcel2.createStringArrayList();
          return (List<String>)localObject1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean matchRule(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.IAppRunningControlManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(4, localParcel1, localParcel2, 0)) && (IAppRunningControlManager.Stub.getDefaultImpl() != null))
          {
            bool = IAppRunningControlManager.Stub.getDefaultImpl().matchRule(paramString, paramInt);
            return bool;
          }
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
            bool = true;
          }
          return bool;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setBlackListEnable(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.IAppRunningControlManager");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(3, localParcel1, localParcel2, 0)) && (IAppRunningControlManager.Stub.getDefaultImpl() != null))
          {
            IAppRunningControlManager.Stub.getDefaultImpl().setBlackListEnable(paramBoolean);
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
      
      public void setDisallowRunningList(List<String> paramList, Intent paramIntent)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.IAppRunningControlManager");
          localParcel1.writeStringList(paramList);
          if (paramIntent != null)
          {
            localParcel1.writeInt(1);
            paramIntent.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (IAppRunningControlManager.Stub.getDefaultImpl() != null))
          {
            IAppRunningControlManager.Stub.getDefaultImpl().setDisallowRunningList(paramList, paramIntent);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/security/IAppRunningControlManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */