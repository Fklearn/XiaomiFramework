package android.view.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.accessibilityservice.IAccessibilityServiceClient.Stub;
import android.annotation.UnsupportedAppUsage;
import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.IWindow;
import android.view.IWindow.Stub;
import java.util.List;

public abstract interface IAccessibilityManager
  extends IInterface
{
  public abstract int addAccessibilityInteractionConnection(IWindow paramIWindow, IAccessibilityInteractionConnection paramIAccessibilityInteractionConnection, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract long addClient(IAccessibilityManagerClient paramIAccessibilityManagerClient, int paramInt)
    throws RemoteException;
  
  public abstract String getAccessibilityShortcutService()
    throws RemoteException;
  
  public abstract int getAccessibilityWindowId(IBinder paramIBinder)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(int paramInt)
    throws RemoteException;
  
  public abstract long getRecommendedTimeoutMillis()
    throws RemoteException;
  
  public abstract IBinder getWindowToken(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void interrupt(int paramInt)
    throws RemoteException;
  
  public abstract void notifyAccessibilityButtonClicked(int paramInt)
    throws RemoteException;
  
  public abstract void notifyAccessibilityButtonVisibilityChanged(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void performAccessibilityShortcut()
    throws RemoteException;
  
  public abstract void registerUiTestAutomationService(IBinder paramIBinder, IAccessibilityServiceClient paramIAccessibilityServiceClient, AccessibilityServiceInfo paramAccessibilityServiceInfo, int paramInt)
    throws RemoteException;
  
  public abstract void removeAccessibilityInteractionConnection(IWindow paramIWindow)
    throws RemoteException;
  
  public abstract void sendAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent, int paramInt)
    throws RemoteException;
  
  public abstract boolean sendFingerprintGesture(int paramInt)
    throws RemoteException;
  
  public abstract void setPictureInPictureActionReplacingConnection(IAccessibilityInteractionConnection paramIAccessibilityInteractionConnection)
    throws RemoteException;
  
  public abstract void temporaryEnableAccessibilityStateUntilKeyguardRemoved(ComponentName paramComponentName, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void unregisterUiTestAutomationService(IAccessibilityServiceClient paramIAccessibilityServiceClient)
    throws RemoteException;
  
  public static class Default
    implements IAccessibilityManager
  {
    public int addAccessibilityInteractionConnection(IWindow paramIWindow, IAccessibilityInteractionConnection paramIAccessibilityInteractionConnection, String paramString, int paramInt)
      throws RemoteException
    {
      return 0;
    }
    
    public long addClient(IAccessibilityManagerClient paramIAccessibilityManagerClient, int paramInt)
      throws RemoteException
    {
      return 0L;
    }
    
    public IBinder asBinder()
    {
      return null;
    }
    
    public String getAccessibilityShortcutService()
      throws RemoteException
    {
      return null;
    }
    
    public int getAccessibilityWindowId(IBinder paramIBinder)
      throws RemoteException
    {
      return 0;
    }
    
    public List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int paramInt1, int paramInt2)
      throws RemoteException
    {
      return null;
    }
    
    public List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(int paramInt)
      throws RemoteException
    {
      return null;
    }
    
    public long getRecommendedTimeoutMillis()
      throws RemoteException
    {
      return 0L;
    }
    
    public IBinder getWindowToken(int paramInt1, int paramInt2)
      throws RemoteException
    {
      return null;
    }
    
    public void interrupt(int paramInt)
      throws RemoteException
    {}
    
    public void notifyAccessibilityButtonClicked(int paramInt)
      throws RemoteException
    {}
    
    public void notifyAccessibilityButtonVisibilityChanged(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void performAccessibilityShortcut()
      throws RemoteException
    {}
    
    public void registerUiTestAutomationService(IBinder paramIBinder, IAccessibilityServiceClient paramIAccessibilityServiceClient, AccessibilityServiceInfo paramAccessibilityServiceInfo, int paramInt)
      throws RemoteException
    {}
    
    public void removeAccessibilityInteractionConnection(IWindow paramIWindow)
      throws RemoteException
    {}
    
    public void sendAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent, int paramInt)
      throws RemoteException
    {}
    
    public boolean sendFingerprintGesture(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public void setPictureInPictureActionReplacingConnection(IAccessibilityInteractionConnection paramIAccessibilityInteractionConnection)
      throws RemoteException
    {}
    
    public void temporaryEnableAccessibilityStateUntilKeyguardRemoved(ComponentName paramComponentName, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void unregisterUiTestAutomationService(IAccessibilityServiceClient paramIAccessibilityServiceClient)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IAccessibilityManager
  {
    private static final String DESCRIPTOR = "android.view.accessibility.IAccessibilityManager";
    static final int TRANSACTION_addAccessibilityInteractionConnection = 6;
    static final int TRANSACTION_addClient = 3;
    static final int TRANSACTION_getAccessibilityShortcutService = 16;
    static final int TRANSACTION_getAccessibilityWindowId = 18;
    static final int TRANSACTION_getEnabledAccessibilityServiceList = 5;
    static final int TRANSACTION_getInstalledAccessibilityServiceList = 4;
    static final int TRANSACTION_getRecommendedTimeoutMillis = 19;
    static final int TRANSACTION_getWindowToken = 12;
    static final int TRANSACTION_interrupt = 1;
    static final int TRANSACTION_notifyAccessibilityButtonClicked = 13;
    static final int TRANSACTION_notifyAccessibilityButtonVisibilityChanged = 14;
    static final int TRANSACTION_performAccessibilityShortcut = 15;
    static final int TRANSACTION_registerUiTestAutomationService = 9;
    static final int TRANSACTION_removeAccessibilityInteractionConnection = 7;
    static final int TRANSACTION_sendAccessibilityEvent = 2;
    static final int TRANSACTION_sendFingerprintGesture = 17;
    static final int TRANSACTION_setPictureInPictureActionReplacingConnection = 8;
    static final int TRANSACTION_temporaryEnableAccessibilityStateUntilKeyguardRemoved = 11;
    static final int TRANSACTION_unregisterUiTestAutomationService = 10;
    
    public Stub()
    {
      attachInterface(this, "android.view.accessibility.IAccessibilityManager");
    }
    
    public static IAccessibilityManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.accessibility.IAccessibilityManager");
      if ((localIInterface != null) && ((localIInterface instanceof IAccessibilityManager))) {
        return (IAccessibilityManager)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IAccessibilityManager getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return null;
      case 19: 
        return "getRecommendedTimeoutMillis";
      case 18: 
        return "getAccessibilityWindowId";
      case 17: 
        return "sendFingerprintGesture";
      case 16: 
        return "getAccessibilityShortcutService";
      case 15: 
        return "performAccessibilityShortcut";
      case 14: 
        return "notifyAccessibilityButtonVisibilityChanged";
      case 13: 
        return "notifyAccessibilityButtonClicked";
      case 12: 
        return "getWindowToken";
      case 11: 
        return "temporaryEnableAccessibilityStateUntilKeyguardRemoved";
      case 10: 
        return "unregisterUiTestAutomationService";
      case 9: 
        return "registerUiTestAutomationService";
      case 8: 
        return "setPictureInPictureActionReplacingConnection";
      case 7: 
        return "removeAccessibilityInteractionConnection";
      case 6: 
        return "addAccessibilityInteractionConnection";
      case 5: 
        return "getEnabledAccessibilityServiceList";
      case 4: 
        return "getInstalledAccessibilityServiceList";
      case 3: 
        return "addClient";
      case 2: 
        return "sendAccessibilityEvent";
      }
      return "interrupt";
    }
    
    public static boolean setDefaultImpl(IAccessibilityManager paramIAccessibilityManager)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIAccessibilityManager != null))
      {
        Proxy.sDefaultImpl = paramIAccessibilityManager;
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
        boolean bool1 = false;
        boolean bool2 = false;
        long l;
        Object localObject;
        switch (paramInt1)
        {
        default: 
          return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        case 19: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          l = getRecommendedTimeoutMillis();
          paramParcel2.writeNoException();
          paramParcel2.writeLong(l);
          return true;
        case 18: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          paramInt1 = getAccessibilityWindowId(paramParcel1.readStrongBinder());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 17: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          paramInt1 = sendFingerprintGesture(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 16: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          paramParcel1 = getAccessibilityShortcutService();
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        case 15: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          performAccessibilityShortcut();
          paramParcel2.writeNoException();
          return true;
        case 14: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          if (paramParcel1.readInt() != 0) {
            bool2 = true;
          }
          notifyAccessibilityButtonVisibilityChanged(bool2);
          paramParcel2.writeNoException();
          return true;
        case 13: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          notifyAccessibilityButtonClicked(paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 12: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          paramParcel1 = getWindowToken(paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        case 11: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          if (paramParcel1.readInt() != 0) {
            localObject = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject = null;
          }
          bool2 = bool1;
          if (paramParcel1.readInt() != 0) {
            bool2 = true;
          }
          temporaryEnableAccessibilityStateUntilKeyguardRemoved((ComponentName)localObject, bool2);
          paramParcel2.writeNoException();
          return true;
        case 10: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          unregisterUiTestAutomationService(IAccessibilityServiceClient.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 9: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          IBinder localIBinder = paramParcel1.readStrongBinder();
          IAccessibilityServiceClient localIAccessibilityServiceClient = IAccessibilityServiceClient.Stub.asInterface(paramParcel1.readStrongBinder());
          if (paramParcel1.readInt() != 0) {
            localObject = (AccessibilityServiceInfo)AccessibilityServiceInfo.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject = null;
          }
          registerUiTestAutomationService(localIBinder, localIAccessibilityServiceClient, (AccessibilityServiceInfo)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 8: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          setPictureInPictureActionReplacingConnection(IAccessibilityInteractionConnection.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 7: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          removeAccessibilityInteractionConnection(IWindow.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 6: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          paramInt1 = addAccessibilityInteractionConnection(IWindow.Stub.asInterface(paramParcel1.readStrongBinder()), IAccessibilityInteractionConnection.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 5: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          paramParcel1 = getEnabledAccessibilityServiceList(paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeTypedList(paramParcel1);
          return true;
        case 4: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          paramParcel1 = getInstalledAccessibilityServiceList(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeTypedList(paramParcel1);
          return true;
        case 3: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          l = addClient(IAccessibilityManagerClient.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeLong(l);
          return true;
        case 2: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (AccessibilityEvent)AccessibilityEvent.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          sendAccessibilityEvent(paramParcel2, paramParcel1.readInt());
          return true;
        }
        paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityManager");
        interrupt(paramParcel1.readInt());
        return true;
      }
      paramParcel2.writeString("android.view.accessibility.IAccessibilityManager");
      return true;
    }
    
    private static class Proxy
      implements IAccessibilityManager
    {
      public static IAccessibilityManager sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public int addAccessibilityInteractionConnection(IWindow paramIWindow, IAccessibilityInteractionConnection paramIAccessibilityInteractionConnection, String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          Object localObject1 = null;
          if (paramIWindow != null) {
            localObject2 = paramIWindow.asBinder();
          } else {
            localObject2 = null;
          }
          localParcel1.writeStrongBinder((IBinder)localObject2);
          Object localObject2 = localObject1;
          if (paramIAccessibilityInteractionConnection != null) {
            localObject2 = paramIAccessibilityInteractionConnection.asBinder();
          }
          localParcel1.writeStrongBinder((IBinder)localObject2);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(6, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            paramInt = IAccessibilityManager.Stub.getDefaultImpl().addAccessibilityInteractionConnection(paramIWindow, paramIAccessibilityInteractionConnection, paramString, paramInt);
            return paramInt;
          }
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public long addClient(IAccessibilityManagerClient paramIAccessibilityManagerClient, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          IBinder localIBinder;
          if (paramIAccessibilityManagerClient != null) {
            localIBinder = paramIAccessibilityManagerClient.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(3, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            l = IAccessibilityManager.Stub.getDefaultImpl().addClient(paramIAccessibilityManagerClient, paramInt);
            return l;
          }
          localParcel2.readException();
          long l = localParcel2.readLong();
          return l;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public String getAccessibilityShortcutService()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          if ((!this.mRemote.transact(16, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            str = IAccessibilityManager.Stub.getDefaultImpl().getAccessibilityShortcutService();
            return str;
          }
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getAccessibilityWindowId(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          localParcel1.writeStrongBinder(paramIBinder);
          if ((!this.mRemote.transact(18, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            i = IAccessibilityManager.Stub.getDefaultImpl().getAccessibilityWindowId(paramIBinder);
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
      
      public List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          if ((!this.mRemote.transact(5, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            localObject1 = IAccessibilityManager.Stub.getDefaultImpl().getEnabledAccessibilityServiceList(paramInt1, paramInt2);
            return (List<AccessibilityServiceInfo>)localObject1;
          }
          localParcel2.readException();
          Object localObject1 = localParcel2.createTypedArrayList(AccessibilityServiceInfo.CREATOR);
          return (List<AccessibilityServiceInfo>)localObject1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(4, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            localObject1 = IAccessibilityManager.Stub.getDefaultImpl().getInstalledAccessibilityServiceList(paramInt);
            return (List<AccessibilityServiceInfo>)localObject1;
          }
          localParcel2.readException();
          Object localObject1 = localParcel2.createTypedArrayList(AccessibilityServiceInfo.CREATOR);
          return (List<AccessibilityServiceInfo>)localObject1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.view.accessibility.IAccessibilityManager";
      }
      
      public long getRecommendedTimeoutMillis()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          if ((!this.mRemote.transact(19, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            l = IAccessibilityManager.Stub.getDefaultImpl().getRecommendedTimeoutMillis();
            return l;
          }
          localParcel2.readException();
          long l = localParcel2.readLong();
          return l;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public IBinder getWindowToken(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          if ((!this.mRemote.transact(12, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            localIBinder = IAccessibilityManager.Stub.getDefaultImpl().getWindowToken(paramInt1, paramInt2);
            return localIBinder;
          }
          localParcel2.readException();
          IBinder localIBinder = localParcel2.readStrongBinder();
          return localIBinder;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void interrupt(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            IAccessibilityManager.Stub.getDefaultImpl().interrupt(paramInt);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void notifyAccessibilityButtonClicked(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(13, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            IAccessibilityManager.Stub.getDefaultImpl().notifyAccessibilityButtonClicked(paramInt);
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
      
      public void notifyAccessibilityButtonVisibilityChanged(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(14, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            IAccessibilityManager.Stub.getDefaultImpl().notifyAccessibilityButtonVisibilityChanged(paramBoolean);
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
      
      public void performAccessibilityShortcut()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          if ((!this.mRemote.transact(15, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            IAccessibilityManager.Stub.getDefaultImpl().performAccessibilityShortcut();
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
      
      public void registerUiTestAutomationService(IBinder paramIBinder, IAccessibilityServiceClient paramIAccessibilityServiceClient, AccessibilityServiceInfo paramAccessibilityServiceInfo, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          localParcel1.writeStrongBinder(paramIBinder);
          IBinder localIBinder;
          if (paramIAccessibilityServiceClient != null) {
            localIBinder = paramIAccessibilityServiceClient.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if (paramAccessibilityServiceInfo != null)
          {
            localParcel1.writeInt(1);
            paramAccessibilityServiceInfo.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(9, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            IAccessibilityManager.Stub.getDefaultImpl().registerUiTestAutomationService(paramIBinder, paramIAccessibilityServiceClient, paramAccessibilityServiceInfo, paramInt);
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
      
      public void removeAccessibilityInteractionConnection(IWindow paramIWindow)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(7, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            IAccessibilityManager.Stub.getDefaultImpl().removeAccessibilityInteractionConnection(paramIWindow);
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
      
      public void sendAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          if (paramAccessibilityEvent != null)
          {
            localParcel.writeInt(1);
            paramAccessibilityEvent.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            IAccessibilityManager.Stub.getDefaultImpl().sendAccessibilityEvent(paramAccessibilityEvent, paramInt);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public boolean sendFingerprintGesture(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(17, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            bool = IAccessibilityManager.Stub.getDefaultImpl().sendFingerprintGesture(paramInt);
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
      
      public void setPictureInPictureActionReplacingConnection(IAccessibilityInteractionConnection paramIAccessibilityInteractionConnection)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          IBinder localIBinder;
          if (paramIAccessibilityInteractionConnection != null) {
            localIBinder = paramIAccessibilityInteractionConnection.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(8, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            IAccessibilityManager.Stub.getDefaultImpl().setPictureInPictureActionReplacingConnection(paramIAccessibilityInteractionConnection);
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
      
      public void temporaryEnableAccessibilityStateUntilKeyguardRemoved(ComponentName paramComponentName, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          int i = 1;
          if (paramComponentName != null)
          {
            localParcel1.writeInt(1);
            paramComponentName.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if (!paramBoolean) {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(11, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            IAccessibilityManager.Stub.getDefaultImpl().temporaryEnableAccessibilityStateUntilKeyguardRemoved(paramComponentName, paramBoolean);
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
      
      public void unregisterUiTestAutomationService(IAccessibilityServiceClient paramIAccessibilityServiceClient)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.accessibility.IAccessibilityManager");
          IBinder localIBinder;
          if (paramIAccessibilityServiceClient != null) {
            localIBinder = paramIAccessibilityServiceClient.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(10, localParcel1, localParcel2, 0)) && (IAccessibilityManager.Stub.getDefaultImpl() != null))
          {
            IAccessibilityManager.Stub.getDefaultImpl().unregisterUiTestAutomationService(paramIAccessibilityServiceClient);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/accessibility/IAccessibilityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */