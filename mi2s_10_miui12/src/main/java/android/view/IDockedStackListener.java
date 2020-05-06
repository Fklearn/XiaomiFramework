package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IDockedStackListener
  extends IInterface
{
  public abstract void onAdjustedForImeChanged(boolean paramBoolean, long paramLong)
    throws RemoteException;
  
  public abstract void onDividerVisibilityChanged(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onDockSideChanged(int paramInt)
    throws RemoteException;
  
  public abstract void onDockedStackExistsChanged(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onDockedStackMinimizedChanged(boolean paramBoolean1, long paramLong, boolean paramBoolean2)
    throws RemoteException;
  
  public static class Default
    implements IDockedStackListener
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void onAdjustedForImeChanged(boolean paramBoolean, long paramLong)
      throws RemoteException
    {}
    
    public void onDividerVisibilityChanged(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void onDockSideChanged(int paramInt)
      throws RemoteException
    {}
    
    public void onDockedStackExistsChanged(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void onDockedStackMinimizedChanged(boolean paramBoolean1, long paramLong, boolean paramBoolean2)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IDockedStackListener
  {
    private static final String DESCRIPTOR = "android.view.IDockedStackListener";
    static final int TRANSACTION_onAdjustedForImeChanged = 4;
    static final int TRANSACTION_onDividerVisibilityChanged = 1;
    static final int TRANSACTION_onDockSideChanged = 5;
    static final int TRANSACTION_onDockedStackExistsChanged = 2;
    static final int TRANSACTION_onDockedStackMinimizedChanged = 3;
    
    public Stub()
    {
      attachInterface(this, "android.view.IDockedStackListener");
    }
    
    public static IDockedStackListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IDockedStackListener");
      if ((localIInterface != null) && ((localIInterface instanceof IDockedStackListener))) {
        return (IDockedStackListener)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IDockedStackListener getDefaultImpl()
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
              return "onDockSideChanged";
            }
            return "onAdjustedForImeChanged";
          }
          return "onDockedStackMinimizedChanged";
        }
        return "onDockedStackExistsChanged";
      }
      return "onDividerVisibilityChanged";
    }
    
    public static boolean setDefaultImpl(IDockedStackListener paramIDockedStackListener)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIDockedStackListener != null))
      {
        Proxy.sDefaultImpl = paramIDockedStackListener;
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
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool3 = false;
      boolean bool4 = false;
      if (paramInt1 != 1)
      {
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
                paramParcel2.writeString("android.view.IDockedStackListener");
                return true;
              }
              paramParcel1.enforceInterface("android.view.IDockedStackListener");
              onDockSideChanged(paramParcel1.readInt());
              return true;
            }
            paramParcel1.enforceInterface("android.view.IDockedStackListener");
            if (paramParcel1.readInt() != 0) {
              bool4 = true;
            }
            onAdjustedForImeChanged(bool4, paramParcel1.readLong());
            return true;
          }
          paramParcel1.enforceInterface("android.view.IDockedStackListener");
          if (paramParcel1.readInt() != 0) {
            bool4 = true;
          } else {
            bool4 = false;
          }
          long l = paramParcel1.readLong();
          if (paramParcel1.readInt() != 0) {
            bool1 = true;
          }
          onDockedStackMinimizedChanged(bool4, l, bool1);
          return true;
        }
        paramParcel1.enforceInterface("android.view.IDockedStackListener");
        bool4 = bool2;
        if (paramParcel1.readInt() != 0) {
          bool4 = true;
        }
        onDockedStackExistsChanged(bool4);
        return true;
      }
      paramParcel1.enforceInterface("android.view.IDockedStackListener");
      bool4 = bool3;
      if (paramParcel1.readInt() != 0) {
        bool4 = true;
      }
      onDividerVisibilityChanged(bool4);
      return true;
    }
    
    private static class Proxy
      implements IDockedStackListener
    {
      public static IDockedStackListener sDefaultImpl;
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
        return "android.view.IDockedStackListener";
      }
      
      public void onAdjustedForImeChanged(boolean paramBoolean, long paramLong)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IDockedStackListener");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          localParcel.writeLong(paramLong);
          if ((!this.mRemote.transact(4, localParcel, null, 1)) && (IDockedStackListener.Stub.getDefaultImpl() != null))
          {
            IDockedStackListener.Stub.getDefaultImpl().onAdjustedForImeChanged(paramBoolean, paramLong);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onDividerVisibilityChanged(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IDockedStackListener");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IDockedStackListener.Stub.getDefaultImpl() != null))
          {
            IDockedStackListener.Stub.getDefaultImpl().onDividerVisibilityChanged(paramBoolean);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onDockSideChanged(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IDockedStackListener");
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(5, localParcel, null, 1)) && (IDockedStackListener.Stub.getDefaultImpl() != null))
          {
            IDockedStackListener.Stub.getDefaultImpl().onDockSideChanged(paramInt);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onDockedStackExistsChanged(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IDockedStackListener");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (IDockedStackListener.Stub.getDefaultImpl() != null))
          {
            IDockedStackListener.Stub.getDefaultImpl().onDockedStackExistsChanged(paramBoolean);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onDockedStackMinimizedChanged(boolean paramBoolean1, long paramLong, boolean paramBoolean2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IDockedStackListener");
          int i = 0;
          if (paramBoolean1) {
            j = 1;
          } else {
            j = 0;
          }
          localParcel.writeInt(j);
          localParcel.writeLong(paramLong);
          int j = i;
          if (paramBoolean2) {
            j = 1;
          }
          localParcel.writeInt(j);
          if ((!this.mRemote.transact(3, localParcel, null, 1)) && (IDockedStackListener.Stub.getDefaultImpl() != null))
          {
            IDockedStackListener.Stub.getDefaultImpl().onDockedStackMinimizedChanged(paramBoolean1, paramLong, paramBoolean2);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IDockedStackListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */