package android.view.accessibility;

import android.annotation.UnsupportedAppUsage;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.List;

public abstract interface IAccessibilityInteractionConnectionCallback
  extends IInterface
{
  @UnsupportedAppUsage
  public abstract void setFindAccessibilityNodeInfoResult(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void setFindAccessibilityNodeInfosResult(List<AccessibilityNodeInfo> paramList, int paramInt)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void setPerformAccessibilityActionResult(boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public static class Default
    implements IAccessibilityInteractionConnectionCallback
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void setFindAccessibilityNodeInfoResult(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt)
      throws RemoteException
    {}
    
    public void setFindAccessibilityNodeInfosResult(List<AccessibilityNodeInfo> paramList, int paramInt)
      throws RemoteException
    {}
    
    public void setPerformAccessibilityActionResult(boolean paramBoolean, int paramInt)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IAccessibilityInteractionConnectionCallback
  {
    private static final String DESCRIPTOR = "android.view.accessibility.IAccessibilityInteractionConnectionCallback";
    static final int TRANSACTION_setFindAccessibilityNodeInfoResult = 1;
    static final int TRANSACTION_setFindAccessibilityNodeInfosResult = 2;
    static final int TRANSACTION_setPerformAccessibilityActionResult = 3;
    
    public Stub()
    {
      attachInterface(this, "android.view.accessibility.IAccessibilityInteractionConnectionCallback");
    }
    
    public static IAccessibilityInteractionConnectionCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.accessibility.IAccessibilityInteractionConnectionCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IAccessibilityInteractionConnectionCallback))) {
        return (IAccessibilityInteractionConnectionCallback)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IAccessibilityInteractionConnectionCallback getDefaultImpl()
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
          return "setPerformAccessibilityActionResult";
        }
        return "setFindAccessibilityNodeInfosResult";
      }
      return "setFindAccessibilityNodeInfoResult";
    }
    
    public static boolean setDefaultImpl(IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIAccessibilityInteractionConnectionCallback != null))
      {
        Proxy.sDefaultImpl = paramIAccessibilityInteractionConnectionCallback;
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
            paramParcel2.writeString("android.view.accessibility.IAccessibilityInteractionConnectionCallback");
            return true;
          }
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityInteractionConnectionCallback");
          boolean bool;
          if (paramParcel1.readInt() != 0) {
            bool = true;
          } else {
            bool = false;
          }
          setPerformAccessibilityActionResult(bool, paramParcel1.readInt());
          return true;
        }
        paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityInteractionConnectionCallback");
        setFindAccessibilityNodeInfosResult(paramParcel1.createTypedArrayList(AccessibilityNodeInfo.CREATOR), paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityInteractionConnectionCallback");
      if (paramParcel1.readInt() != 0) {
        paramParcel2 = (AccessibilityNodeInfo)AccessibilityNodeInfo.CREATOR.createFromParcel(paramParcel1);
      } else {
        paramParcel2 = null;
      }
      setFindAccessibilityNodeInfoResult(paramParcel2, paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IAccessibilityInteractionConnectionCallback
    {
      public static IAccessibilityInteractionConnectionCallback sDefaultImpl;
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
        return "android.view.accessibility.IAccessibilityInteractionConnectionCallback";
      }
      
      public void setFindAccessibilityNodeInfoResult(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.accessibility.IAccessibilityInteractionConnectionCallback");
          if (paramAccessibilityNodeInfo != null)
          {
            localParcel.writeInt(1);
            paramAccessibilityNodeInfo.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IAccessibilityInteractionConnectionCallback.Stub.getDefaultImpl() != null))
          {
            IAccessibilityInteractionConnectionCallback.Stub.getDefaultImpl().setFindAccessibilityNodeInfoResult(paramAccessibilityNodeInfo, paramInt);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setFindAccessibilityNodeInfosResult(List<AccessibilityNodeInfo> paramList, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.accessibility.IAccessibilityInteractionConnectionCallback");
          localParcel.writeTypedList(paramList);
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (IAccessibilityInteractionConnectionCallback.Stub.getDefaultImpl() != null))
          {
            IAccessibilityInteractionConnectionCallback.Stub.getDefaultImpl().setFindAccessibilityNodeInfosResult(paramList, paramInt);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setPerformAccessibilityActionResult(boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.accessibility.IAccessibilityInteractionConnectionCallback");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(3, localParcel, null, 1)) && (IAccessibilityInteractionConnectionCallback.Stub.getDefaultImpl() != null))
          {
            IAccessibilityInteractionConnectionCallback.Stub.getDefaultImpl().setPerformAccessibilityActionResult(paramBoolean, paramInt);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/accessibility/IAccessibilityInteractionConnectionCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */