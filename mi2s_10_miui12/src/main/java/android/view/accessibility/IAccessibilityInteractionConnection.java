package android.view.accessibility;

import android.graphics.Region;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.MagnificationSpec;

public abstract interface IAccessibilityInteractionConnection
  extends IInterface
{
  public abstract void clearAccessibilityFocus()
    throws RemoteException;
  
  public abstract void findAccessibilityNodeInfoByAccessibilityId(long paramLong1, Region paramRegion, int paramInt1, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt2, int paramInt3, long paramLong2, MagnificationSpec paramMagnificationSpec, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void findAccessibilityNodeInfosByText(long paramLong1, String paramString, Region paramRegion, int paramInt1, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt2, int paramInt3, long paramLong2, MagnificationSpec paramMagnificationSpec)
    throws RemoteException;
  
  public abstract void findAccessibilityNodeInfosByViewId(long paramLong1, String paramString, Region paramRegion, int paramInt1, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt2, int paramInt3, long paramLong2, MagnificationSpec paramMagnificationSpec)
    throws RemoteException;
  
  public abstract void findFocus(long paramLong1, int paramInt1, Region paramRegion, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, int paramInt4, long paramLong2, MagnificationSpec paramMagnificationSpec)
    throws RemoteException;
  
  public abstract void focusSearch(long paramLong1, int paramInt1, Region paramRegion, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, int paramInt4, long paramLong2, MagnificationSpec paramMagnificationSpec)
    throws RemoteException;
  
  public abstract void notifyOutsideTouch()
    throws RemoteException;
  
  public abstract void performAccessibilityAction(long paramLong1, int paramInt1, Bundle paramBundle, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, int paramInt4, long paramLong2)
    throws RemoteException;
  
  public static class Default
    implements IAccessibilityInteractionConnection
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void clearAccessibilityFocus()
      throws RemoteException
    {}
    
    public void findAccessibilityNodeInfoByAccessibilityId(long paramLong1, Region paramRegion, int paramInt1, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt2, int paramInt3, long paramLong2, MagnificationSpec paramMagnificationSpec, Bundle paramBundle)
      throws RemoteException
    {}
    
    public void findAccessibilityNodeInfosByText(long paramLong1, String paramString, Region paramRegion, int paramInt1, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt2, int paramInt3, long paramLong2, MagnificationSpec paramMagnificationSpec)
      throws RemoteException
    {}
    
    public void findAccessibilityNodeInfosByViewId(long paramLong1, String paramString, Region paramRegion, int paramInt1, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt2, int paramInt3, long paramLong2, MagnificationSpec paramMagnificationSpec)
      throws RemoteException
    {}
    
    public void findFocus(long paramLong1, int paramInt1, Region paramRegion, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, int paramInt4, long paramLong2, MagnificationSpec paramMagnificationSpec)
      throws RemoteException
    {}
    
    public void focusSearch(long paramLong1, int paramInt1, Region paramRegion, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, int paramInt4, long paramLong2, MagnificationSpec paramMagnificationSpec)
      throws RemoteException
    {}
    
    public void notifyOutsideTouch()
      throws RemoteException
    {}
    
    public void performAccessibilityAction(long paramLong1, int paramInt1, Bundle paramBundle, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, int paramInt4, long paramLong2)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IAccessibilityInteractionConnection
  {
    private static final String DESCRIPTOR = "android.view.accessibility.IAccessibilityInteractionConnection";
    static final int TRANSACTION_clearAccessibilityFocus = 7;
    static final int TRANSACTION_findAccessibilityNodeInfoByAccessibilityId = 1;
    static final int TRANSACTION_findAccessibilityNodeInfosByText = 3;
    static final int TRANSACTION_findAccessibilityNodeInfosByViewId = 2;
    static final int TRANSACTION_findFocus = 4;
    static final int TRANSACTION_focusSearch = 5;
    static final int TRANSACTION_notifyOutsideTouch = 8;
    static final int TRANSACTION_performAccessibilityAction = 6;
    
    public Stub()
    {
      attachInterface(this, "android.view.accessibility.IAccessibilityInteractionConnection");
    }
    
    public static IAccessibilityInteractionConnection asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.accessibility.IAccessibilityInteractionConnection");
      if ((localIInterface != null) && ((localIInterface instanceof IAccessibilityInteractionConnection))) {
        return (IAccessibilityInteractionConnection)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IAccessibilityInteractionConnection getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return null;
      case 8: 
        return "notifyOutsideTouch";
      case 7: 
        return "clearAccessibilityFocus";
      case 6: 
        return "performAccessibilityAction";
      case 5: 
        return "focusSearch";
      case 4: 
        return "findFocus";
      case 3: 
        return "findAccessibilityNodeInfosByText";
      case 2: 
        return "findAccessibilityNodeInfosByViewId";
      }
      return "findAccessibilityNodeInfoByAccessibilityId";
    }
    
    public static boolean setDefaultImpl(IAccessibilityInteractionConnection paramIAccessibilityInteractionConnection)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIAccessibilityInteractionConnection != null))
      {
        Proxy.sDefaultImpl = paramIAccessibilityInteractionConnection;
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
        Object localObject;
        int j;
        switch (paramInt1)
        {
        default: 
          return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        case 8: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityInteractionConnection");
          notifyOutsideTouch();
          return true;
        case 7: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityInteractionConnection");
          clearAccessibilityFocus();
          return true;
        case 6: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityInteractionConnection");
          l1 = paramParcel1.readLong();
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          performAccessibilityAction(l1, paramInt1, paramParcel2, paramParcel1.readInt(), IAccessibilityInteractionConnectionCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readLong());
          return true;
        case 5: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityInteractionConnection");
          l2 = paramParcel1.readLong();
          paramInt2 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (Region)Region.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          i = paramParcel1.readInt();
          localObject = IAccessibilityInteractionConnectionCallback.Stub.asInterface(paramParcel1.readStrongBinder());
          paramInt1 = paramParcel1.readInt();
          j = paramParcel1.readInt();
          l1 = paramParcel1.readLong();
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (MagnificationSpec)MagnificationSpec.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          focusSearch(l2, paramInt2, paramParcel2, i, (IAccessibilityInteractionConnectionCallback)localObject, paramInt1, j, l1, paramParcel1);
          return true;
        case 4: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityInteractionConnection");
          l1 = paramParcel1.readLong();
          j = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (Region)Region.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          i = paramParcel1.readInt();
          localObject = IAccessibilityInteractionConnectionCallback.Stub.asInterface(paramParcel1.readStrongBinder());
          paramInt1 = paramParcel1.readInt();
          paramInt2 = paramParcel1.readInt();
          l2 = paramParcel1.readLong();
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (MagnificationSpec)MagnificationSpec.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          findFocus(l1, j, paramParcel2, i, (IAccessibilityInteractionConnectionCallback)localObject, paramInt1, paramInt2, l2, paramParcel1);
          return true;
        case 3: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityInteractionConnection");
          l2 = paramParcel1.readLong();
          localObject = paramParcel1.readString();
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (Region)Region.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          paramInt2 = paramParcel1.readInt();
          localIAccessibilityInteractionConnectionCallback = IAccessibilityInteractionConnectionCallback.Stub.asInterface(paramParcel1.readStrongBinder());
          paramInt1 = paramParcel1.readInt();
          i = paramParcel1.readInt();
          l1 = paramParcel1.readLong();
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (MagnificationSpec)MagnificationSpec.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          findAccessibilityNodeInfosByText(l2, (String)localObject, paramParcel2, paramInt2, localIAccessibilityInteractionConnectionCallback, paramInt1, i, l1, paramParcel1);
          return true;
        case 2: 
          paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityInteractionConnection");
          l2 = paramParcel1.readLong();
          localObject = paramParcel1.readString();
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (Region)Region.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          paramInt1 = paramParcel1.readInt();
          localIAccessibilityInteractionConnectionCallback = IAccessibilityInteractionConnectionCallback.Stub.asInterface(paramParcel1.readStrongBinder());
          paramInt2 = paramParcel1.readInt();
          i = paramParcel1.readInt();
          l1 = paramParcel1.readLong();
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (MagnificationSpec)MagnificationSpec.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          findAccessibilityNodeInfosByViewId(l2, (String)localObject, paramParcel2, paramInt1, localIAccessibilityInteractionConnectionCallback, paramInt2, i, l1, paramParcel1);
          return true;
        }
        paramParcel1.enforceInterface("android.view.accessibility.IAccessibilityInteractionConnection");
        long l2 = paramParcel1.readLong();
        if (paramParcel1.readInt() != 0) {
          paramParcel2 = (Region)Region.CREATOR.createFromParcel(paramParcel1);
        } else {
          paramParcel2 = null;
        }
        paramInt2 = paramParcel1.readInt();
        IAccessibilityInteractionConnectionCallback localIAccessibilityInteractionConnectionCallback = IAccessibilityInteractionConnectionCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        int i = paramParcel1.readInt();
        paramInt1 = paramParcel1.readInt();
        long l1 = paramParcel1.readLong();
        if (paramParcel1.readInt() != 0) {
          localObject = (MagnificationSpec)MagnificationSpec.CREATOR.createFromParcel(paramParcel1);
        } else {
          localObject = null;
        }
        if (paramParcel1.readInt() != 0) {
          paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
        } else {
          paramParcel1 = null;
        }
        findAccessibilityNodeInfoByAccessibilityId(l2, paramParcel2, paramInt2, localIAccessibilityInteractionConnectionCallback, i, paramInt1, l1, (MagnificationSpec)localObject, paramParcel1);
        return true;
      }
      paramParcel2.writeString("android.view.accessibility.IAccessibilityInteractionConnection");
      return true;
    }
    
    private static class Proxy
      implements IAccessibilityInteractionConnection
    {
      public static IAccessibilityInteractionConnection sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void clearAccessibilityFocus()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.accessibility.IAccessibilityInteractionConnection");
          if ((!this.mRemote.transact(7, localParcel, null, 1)) && (IAccessibilityInteractionConnection.Stub.getDefaultImpl() != null))
          {
            IAccessibilityInteractionConnection.Stub.getDefaultImpl().clearAccessibilityFocus();
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void findAccessibilityNodeInfoByAccessibilityId(long paramLong1, Region paramRegion, int paramInt1, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt2, int paramInt3, long paramLong2, MagnificationSpec paramMagnificationSpec, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.accessibility.IAccessibilityInteractionConnection");
          localParcel.writeLong(paramLong1);
          if (paramRegion != null) {
            try
            {
              localParcel.writeInt(1);
              paramRegion.writeToParcel(localParcel, 0);
            }
            finally
            {
              break label227;
            }
          } else {
            localParcel.writeInt(0);
          }
          localParcel.writeInt(paramInt1);
          Object localObject;
          if (paramIAccessibilityInteractionConnectionCallback != null) {
            localObject = paramIAccessibilityInteractionConnectionCallback.asBinder();
          } else {
            localObject = null;
          }
          localParcel.writeStrongBinder((IBinder)localObject);
          localParcel.writeInt(paramInt2);
          localParcel.writeInt(paramInt3);
          localParcel.writeLong(paramLong2);
          if (paramMagnificationSpec != null)
          {
            localParcel.writeInt(1);
            paramMagnificationSpec.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if (paramBundle != null)
          {
            localParcel.writeInt(1);
            paramBundle.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IAccessibilityInteractionConnection.Stub.getDefaultImpl() != null))
          {
            localObject = IAccessibilityInteractionConnection.Stub.getDefaultImpl();
            try
            {
              ((IAccessibilityInteractionConnection)localObject).findAccessibilityNodeInfoByAccessibilityId(paramLong1, paramRegion, paramInt1, paramIAccessibilityInteractionConnectionCallback, paramInt2, paramInt3, paramLong2, paramMagnificationSpec, paramBundle);
              return;
            }
            finally
            {
              break label227;
            }
          }
          return;
        }
        finally
        {
          label227:
          localParcel.recycle();
        }
      }
      
      public void findAccessibilityNodeInfosByText(long paramLong1, String paramString, Region paramRegion, int paramInt1, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt2, int paramInt3, long paramLong2, MagnificationSpec paramMagnificationSpec)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.accessibility.IAccessibilityInteractionConnection");
          localParcel.writeLong(paramLong1);
          localParcel.writeString(paramString);
          if (paramRegion != null)
          {
            localParcel.writeInt(1);
            paramRegion.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          localParcel.writeInt(paramInt1);
          IBinder localIBinder;
          if (paramIAccessibilityInteractionConnectionCallback != null) {
            localIBinder = paramIAccessibilityInteractionConnectionCallback.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeInt(paramInt2);
          localParcel.writeInt(paramInt3);
          localParcel.writeLong(paramLong2);
          if (paramMagnificationSpec != null)
          {
            localParcel.writeInt(1);
            paramMagnificationSpec.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(3, localParcel, null, 1)) && (IAccessibilityInteractionConnection.Stub.getDefaultImpl() != null))
          {
            IAccessibilityInteractionConnection.Stub.getDefaultImpl().findAccessibilityNodeInfosByText(paramLong1, paramString, paramRegion, paramInt1, paramIAccessibilityInteractionConnectionCallback, paramInt2, paramInt3, paramLong2, paramMagnificationSpec);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void findAccessibilityNodeInfosByViewId(long paramLong1, String paramString, Region paramRegion, int paramInt1, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt2, int paramInt3, long paramLong2, MagnificationSpec paramMagnificationSpec)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.accessibility.IAccessibilityInteractionConnection");
          localParcel.writeLong(paramLong1);
          localParcel.writeString(paramString);
          if (paramRegion != null)
          {
            localParcel.writeInt(1);
            paramRegion.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          localParcel.writeInt(paramInt1);
          IBinder localIBinder;
          if (paramIAccessibilityInteractionConnectionCallback != null) {
            localIBinder = paramIAccessibilityInteractionConnectionCallback.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeInt(paramInt2);
          localParcel.writeInt(paramInt3);
          localParcel.writeLong(paramLong2);
          if (paramMagnificationSpec != null)
          {
            localParcel.writeInt(1);
            paramMagnificationSpec.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (IAccessibilityInteractionConnection.Stub.getDefaultImpl() != null))
          {
            IAccessibilityInteractionConnection.Stub.getDefaultImpl().findAccessibilityNodeInfosByViewId(paramLong1, paramString, paramRegion, paramInt1, paramIAccessibilityInteractionConnectionCallback, paramInt2, paramInt3, paramLong2, paramMagnificationSpec);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void findFocus(long paramLong1, int paramInt1, Region paramRegion, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, int paramInt4, long paramLong2, MagnificationSpec paramMagnificationSpec)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.accessibility.IAccessibilityInteractionConnection");
          localParcel.writeLong(paramLong1);
          localParcel.writeInt(paramInt1);
          if (paramRegion != null)
          {
            localParcel.writeInt(1);
            paramRegion.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          localParcel.writeInt(paramInt2);
          IBinder localIBinder;
          if (paramIAccessibilityInteractionConnectionCallback != null) {
            localIBinder = paramIAccessibilityInteractionConnectionCallback.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeInt(paramInt3);
          localParcel.writeInt(paramInt4);
          localParcel.writeLong(paramLong2);
          if (paramMagnificationSpec != null)
          {
            localParcel.writeInt(1);
            paramMagnificationSpec.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(4, localParcel, null, 1)) && (IAccessibilityInteractionConnection.Stub.getDefaultImpl() != null))
          {
            IAccessibilityInteractionConnection.Stub.getDefaultImpl().findFocus(paramLong1, paramInt1, paramRegion, paramInt2, paramIAccessibilityInteractionConnectionCallback, paramInt3, paramInt4, paramLong2, paramMagnificationSpec);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void focusSearch(long paramLong1, int paramInt1, Region paramRegion, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, int paramInt4, long paramLong2, MagnificationSpec paramMagnificationSpec)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.accessibility.IAccessibilityInteractionConnection");
          localParcel.writeLong(paramLong1);
          localParcel.writeInt(paramInt1);
          if (paramRegion != null)
          {
            localParcel.writeInt(1);
            paramRegion.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          localParcel.writeInt(paramInt2);
          IBinder localIBinder;
          if (paramIAccessibilityInteractionConnectionCallback != null) {
            localIBinder = paramIAccessibilityInteractionConnectionCallback.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeInt(paramInt3);
          localParcel.writeInt(paramInt4);
          localParcel.writeLong(paramLong2);
          if (paramMagnificationSpec != null)
          {
            localParcel.writeInt(1);
            paramMagnificationSpec.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(5, localParcel, null, 1)) && (IAccessibilityInteractionConnection.Stub.getDefaultImpl() != null))
          {
            IAccessibilityInteractionConnection.Stub.getDefaultImpl().focusSearch(paramLong1, paramInt1, paramRegion, paramInt2, paramIAccessibilityInteractionConnectionCallback, paramInt3, paramInt4, paramLong2, paramMagnificationSpec);
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
        return "android.view.accessibility.IAccessibilityInteractionConnection";
      }
      
      public void notifyOutsideTouch()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.accessibility.IAccessibilityInteractionConnection");
          if ((!this.mRemote.transact(8, localParcel, null, 1)) && (IAccessibilityInteractionConnection.Stub.getDefaultImpl() != null))
          {
            IAccessibilityInteractionConnection.Stub.getDefaultImpl().notifyOutsideTouch();
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void performAccessibilityAction(long paramLong1, int paramInt1, Bundle paramBundle, int paramInt2, IAccessibilityInteractionConnectionCallback paramIAccessibilityInteractionConnectionCallback, int paramInt3, int paramInt4, long paramLong2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 33	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 11
        //   5: aload 11
        //   7: ldc 35
        //   9: invokevirtual 39	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload 11
        //   14: lload_1
        //   15: invokevirtual 61	android/os/Parcel:writeLong	(J)V
        //   18: aload 11
        //   20: iload_3
        //   21: invokevirtual 65	android/os/Parcel:writeInt	(I)V
        //   24: aload 4
        //   26: ifnull +20 -> 46
        //   29: aload 11
        //   31: iconst_1
        //   32: invokevirtual 65	android/os/Parcel:writeInt	(I)V
        //   35: aload 4
        //   37: aload 11
        //   39: iconst_0
        //   40: invokevirtual 84	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   43: goto +9 -> 52
        //   46: aload 11
        //   48: iconst_0
        //   49: invokevirtual 65	android/os/Parcel:writeInt	(I)V
        //   52: aload 11
        //   54: iload 5
        //   56: invokevirtual 65	android/os/Parcel:writeInt	(I)V
        //   59: aload 6
        //   61: ifnull +15 -> 76
        //   64: aload 6
        //   66: invokeinterface 75 1 0
        //   71: astore 12
        //   73: goto +6 -> 79
        //   76: aconst_null
        //   77: astore 12
        //   79: aload 11
        //   81: aload 12
        //   83: invokevirtual 78	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   86: aload 11
        //   88: iload 7
        //   90: invokevirtual 65	android/os/Parcel:writeInt	(I)V
        //   93: aload 11
        //   95: iload 8
        //   97: invokevirtual 65	android/os/Parcel:writeInt	(I)V
        //   100: aload 11
        //   102: lload 9
        //   104: invokevirtual 61	android/os/Parcel:writeLong	(J)V
        //   107: aload_0
        //   108: getfield 21	android/view/accessibility/IAccessibilityInteractionConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   111: bipush 6
        //   113: aload 11
        //   115: aconst_null
        //   116: iconst_1
        //   117: invokeinterface 45 5 0
        //   122: ifne +37 -> 159
        //   125: invokestatic 49	android/view/accessibility/IAccessibilityInteractionConnection$Stub:getDefaultImpl	()Landroid/view/accessibility/IAccessibilityInteractionConnection;
        //   128: ifnull +31 -> 159
        //   131: invokestatic 49	android/view/accessibility/IAccessibilityInteractionConnection$Stub:getDefaultImpl	()Landroid/view/accessibility/IAccessibilityInteractionConnection;
        //   134: lload_1
        //   135: iload_3
        //   136: aload 4
        //   138: iload 5
        //   140: aload 6
        //   142: iload 7
        //   144: iload 8
        //   146: lload 9
        //   148: invokeinterface 112 11 0
        //   153: aload 11
        //   155: invokevirtual 54	android/os/Parcel:recycle	()V
        //   158: return
        //   159: aload 11
        //   161: invokevirtual 54	android/os/Parcel:recycle	()V
        //   164: return
        //   165: astore 4
        //   167: goto +5 -> 172
        //   170: astore 4
        //   172: aload 11
        //   174: invokevirtual 54	android/os/Parcel:recycle	()V
        //   177: aload 4
        //   179: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	180	0	this	Proxy
        //   0	180	1	paramLong1	long
        //   0	180	3	paramInt1	int
        //   0	180	4	paramBundle	Bundle
        //   0	180	5	paramInt2	int
        //   0	180	6	paramIAccessibilityInteractionConnectionCallback	IAccessibilityInteractionConnectionCallback
        //   0	180	7	paramInt3	int
        //   0	180	8	paramInt4	int
        //   0	180	9	paramLong2	long
        //   3	170	11	localParcel	Parcel
        //   71	11	12	localIBinder	IBinder
        // Exception table:
        //   from	to	target	type
        //   12	24	165	finally
        //   29	43	165	finally
        //   46	52	165	finally
        //   52	59	165	finally
        //   64	73	165	finally
        //   79	153	165	finally
        //   5	12	170	finally
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/accessibility/IAccessibilityInteractionConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */