package android.view;

import android.annotation.UnsupportedAppUsage;
import android.app.ActivityManager.TaskSnapshot;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IRecentsAnimationController
  extends IInterface
{
  public abstract void cleanupScreenshot()
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void finish(boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract void hideCurrentInputMethod()
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract ActivityManager.TaskSnapshot screenshotTask(int paramInt)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void setAnimationTargetsBehindSystemBars(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setCancelWithDeferredScreenshot(boolean paramBoolean)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void setInputConsumerEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setSplitScreenMinimized(boolean paramBoolean)
    throws RemoteException;
  
  public static class Default
    implements IRecentsAnimationController
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void cleanupScreenshot()
      throws RemoteException
    {}
    
    public void finish(boolean paramBoolean1, boolean paramBoolean2)
      throws RemoteException
    {}
    
    public void hideCurrentInputMethod()
      throws RemoteException
    {}
    
    public ActivityManager.TaskSnapshot screenshotTask(int paramInt)
      throws RemoteException
    {
      return null;
    }
    
    public void setAnimationTargetsBehindSystemBars(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setCancelWithDeferredScreenshot(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setInputConsumerEnabled(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setSplitScreenMinimized(boolean paramBoolean)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IRecentsAnimationController
  {
    private static final String DESCRIPTOR = "android.view.IRecentsAnimationController";
    static final int TRANSACTION_cleanupScreenshot = 8;
    static final int TRANSACTION_finish = 2;
    static final int TRANSACTION_hideCurrentInputMethod = 6;
    static final int TRANSACTION_screenshotTask = 1;
    static final int TRANSACTION_setAnimationTargetsBehindSystemBars = 4;
    static final int TRANSACTION_setCancelWithDeferredScreenshot = 7;
    static final int TRANSACTION_setInputConsumerEnabled = 3;
    static final int TRANSACTION_setSplitScreenMinimized = 5;
    
    public Stub()
    {
      attachInterface(this, "android.view.IRecentsAnimationController");
    }
    
    public static IRecentsAnimationController asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IRecentsAnimationController");
      if ((localIInterface != null) && ((localIInterface instanceof IRecentsAnimationController))) {
        return (IRecentsAnimationController)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IRecentsAnimationController getDefaultImpl()
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
        return "cleanupScreenshot";
      case 7: 
        return "setCancelWithDeferredScreenshot";
      case 6: 
        return "hideCurrentInputMethod";
      case 5: 
        return "setSplitScreenMinimized";
      case 4: 
        return "setAnimationTargetsBehindSystemBars";
      case 3: 
        return "setInputConsumerEnabled";
      case 2: 
        return "finish";
      }
      return "screenshotTask";
    }
    
    public static boolean setDefaultImpl(IRecentsAnimationController paramIRecentsAnimationController)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIRecentsAnimationController != null))
      {
        Proxy.sDefaultImpl = paramIRecentsAnimationController;
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
        boolean bool3 = false;
        boolean bool4 = false;
        boolean bool5 = false;
        switch (paramInt1)
        {
        default: 
          return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        case 8: 
          paramParcel1.enforceInterface("android.view.IRecentsAnimationController");
          cleanupScreenshot();
          paramParcel2.writeNoException();
          return true;
        case 7: 
          paramParcel1.enforceInterface("android.view.IRecentsAnimationController");
          if (paramParcel1.readInt() != 0) {
            bool5 = true;
          }
          setCancelWithDeferredScreenshot(bool5);
          paramParcel2.writeNoException();
          return true;
        case 6: 
          paramParcel1.enforceInterface("android.view.IRecentsAnimationController");
          hideCurrentInputMethod();
          paramParcel2.writeNoException();
          return true;
        case 5: 
          paramParcel1.enforceInterface("android.view.IRecentsAnimationController");
          bool5 = bool1;
          if (paramParcel1.readInt() != 0) {
            bool5 = true;
          }
          setSplitScreenMinimized(bool5);
          paramParcel2.writeNoException();
          return true;
        case 4: 
          paramParcel1.enforceInterface("android.view.IRecentsAnimationController");
          bool5 = bool2;
          if (paramParcel1.readInt() != 0) {
            bool5 = true;
          }
          setAnimationTargetsBehindSystemBars(bool5);
          paramParcel2.writeNoException();
          return true;
        case 3: 
          paramParcel1.enforceInterface("android.view.IRecentsAnimationController");
          bool5 = bool3;
          if (paramParcel1.readInt() != 0) {
            bool5 = true;
          }
          setInputConsumerEnabled(bool5);
          paramParcel2.writeNoException();
          return true;
        case 2: 
          paramParcel1.enforceInterface("android.view.IRecentsAnimationController");
          if (paramParcel1.readInt() != 0) {
            bool5 = true;
          } else {
            bool5 = false;
          }
          if (paramParcel1.readInt() != 0) {
            bool4 = true;
          }
          finish(bool5, bool4);
          paramParcel2.writeNoException();
          return true;
        }
        paramParcel1.enforceInterface("android.view.IRecentsAnimationController");
        paramParcel1 = screenshotTask(paramParcel1.readInt());
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
      paramParcel2.writeString("android.view.IRecentsAnimationController");
      return true;
    }
    
    private static class Proxy
      implements IRecentsAnimationController
    {
      public static IRecentsAnimationController sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void cleanupScreenshot()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IRecentsAnimationController");
          if ((!this.mRemote.transact(8, localParcel1, localParcel2, 0)) && (IRecentsAnimationController.Stub.getDefaultImpl() != null))
          {
            IRecentsAnimationController.Stub.getDefaultImpl().cleanupScreenshot();
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
      
      public void finish(boolean paramBoolean1, boolean paramBoolean2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IRecentsAnimationController");
          int i = 1;
          int j;
          if (paramBoolean1) {
            j = 1;
          } else {
            j = 0;
          }
          localParcel1.writeInt(j);
          if (paramBoolean2) {
            j = i;
          } else {
            j = 0;
          }
          localParcel1.writeInt(j);
          if ((!this.mRemote.transact(2, localParcel1, localParcel2, 0)) && (IRecentsAnimationController.Stub.getDefaultImpl() != null))
          {
            IRecentsAnimationController.Stub.getDefaultImpl().finish(paramBoolean1, paramBoolean2);
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
      
      public String getInterfaceDescriptor()
      {
        return "android.view.IRecentsAnimationController";
      }
      
      public void hideCurrentInputMethod()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IRecentsAnimationController");
          if ((!this.mRemote.transact(6, localParcel1, localParcel2, 0)) && (IRecentsAnimationController.Stub.getDefaultImpl() != null))
          {
            IRecentsAnimationController.Stub.getDefaultImpl().hideCurrentInputMethod();
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
      
      public ActivityManager.TaskSnapshot screenshotTask(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IRecentsAnimationController");
          localParcel1.writeInt(paramInt);
          ActivityManager.TaskSnapshot localTaskSnapshot;
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (IRecentsAnimationController.Stub.getDefaultImpl() != null))
          {
            localTaskSnapshot = IRecentsAnimationController.Stub.getDefaultImpl().screenshotTask(paramInt);
            return localTaskSnapshot;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            localTaskSnapshot = (ActivityManager.TaskSnapshot)ActivityManager.TaskSnapshot.CREATOR.createFromParcel(localParcel2);
          } else {
            localTaskSnapshot = null;
          }
          return localTaskSnapshot;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setAnimationTargetsBehindSystemBars(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IRecentsAnimationController");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(4, localParcel1, localParcel2, 0)) && (IRecentsAnimationController.Stub.getDefaultImpl() != null))
          {
            IRecentsAnimationController.Stub.getDefaultImpl().setAnimationTargetsBehindSystemBars(paramBoolean);
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
      
      public void setCancelWithDeferredScreenshot(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IRecentsAnimationController");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(7, localParcel1, localParcel2, 0)) && (IRecentsAnimationController.Stub.getDefaultImpl() != null))
          {
            IRecentsAnimationController.Stub.getDefaultImpl().setCancelWithDeferredScreenshot(paramBoolean);
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
      
      public void setInputConsumerEnabled(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IRecentsAnimationController");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(3, localParcel1, localParcel2, 0)) && (IRecentsAnimationController.Stub.getDefaultImpl() != null))
          {
            IRecentsAnimationController.Stub.getDefaultImpl().setInputConsumerEnabled(paramBoolean);
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
      
      public void setSplitScreenMinimized(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IRecentsAnimationController");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(5, localParcel1, localParcel2, 0)) && (IRecentsAnimationController.Stub.getDefaultImpl() != null))
          {
            IRecentsAnimationController.Stub.getDefaultImpl().setSplitScreenMinimized(paramBoolean);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IRecentsAnimationController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */