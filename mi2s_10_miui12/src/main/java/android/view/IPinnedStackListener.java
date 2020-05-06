package android.view;

import android.content.pm.ParceledListSlice;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IPinnedStackListener
  extends IInterface
{
  public abstract void onActionsChanged(ParceledListSlice paramParceledListSlice)
    throws RemoteException;
  
  public abstract void onImeVisibilityChanged(boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void onListenerRegistered(IPinnedStackController paramIPinnedStackController)
    throws RemoteException;
  
  public abstract void onMinimizedStateChanged(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onMovementBoundsChanged(Rect paramRect1, Rect paramRect2, Rect paramRect3, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
    throws RemoteException;
  
  public abstract void onShelfVisibilityChanged(boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public static class Default
    implements IPinnedStackListener
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void onActionsChanged(ParceledListSlice paramParceledListSlice)
      throws RemoteException
    {}
    
    public void onImeVisibilityChanged(boolean paramBoolean, int paramInt)
      throws RemoteException
    {}
    
    public void onListenerRegistered(IPinnedStackController paramIPinnedStackController)
      throws RemoteException
    {}
    
    public void onMinimizedStateChanged(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void onMovementBoundsChanged(Rect paramRect1, Rect paramRect2, Rect paramRect3, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
      throws RemoteException
    {}
    
    public void onShelfVisibilityChanged(boolean paramBoolean, int paramInt)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IPinnedStackListener
  {
    private static final String DESCRIPTOR = "android.view.IPinnedStackListener";
    static final int TRANSACTION_onActionsChanged = 6;
    static final int TRANSACTION_onImeVisibilityChanged = 3;
    static final int TRANSACTION_onListenerRegistered = 1;
    static final int TRANSACTION_onMinimizedStateChanged = 5;
    static final int TRANSACTION_onMovementBoundsChanged = 2;
    static final int TRANSACTION_onShelfVisibilityChanged = 4;
    
    public Stub()
    {
      attachInterface(this, "android.view.IPinnedStackListener");
    }
    
    public static IPinnedStackListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IPinnedStackListener");
      if ((localIInterface != null) && ((localIInterface instanceof IPinnedStackListener))) {
        return (IPinnedStackListener)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IPinnedStackListener getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return null;
      case 6: 
        return "onActionsChanged";
      case 5: 
        return "onMinimizedStateChanged";
      case 4: 
        return "onShelfVisibilityChanged";
      case 3: 
        return "onImeVisibilityChanged";
      case 2: 
        return "onMovementBoundsChanged";
      }
      return "onListenerRegistered";
    }
    
    public static boolean setDefaultImpl(IPinnedStackListener paramIPinnedStackListener)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIPinnedStackListener != null))
      {
        Proxy.sDefaultImpl = paramIPinnedStackListener;
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
        switch (paramInt1)
        {
        default: 
          return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        case 6: 
          paramParcel1.enforceInterface("android.view.IPinnedStackListener");
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          onActionsChanged(paramParcel1);
          return true;
        case 5: 
          paramParcel1.enforceInterface("android.view.IPinnedStackListener");
          if (paramParcel1.readInt() != 0) {
            bool3 = true;
          }
          onMinimizedStateChanged(bool3);
          return true;
        case 4: 
          paramParcel1.enforceInterface("android.view.IPinnedStackListener");
          bool3 = bool1;
          if (paramParcel1.readInt() != 0) {
            bool3 = true;
          }
          onShelfVisibilityChanged(bool3, paramParcel1.readInt());
          return true;
        case 3: 
          paramParcel1.enforceInterface("android.view.IPinnedStackListener");
          bool3 = bool2;
          if (paramParcel1.readInt() != 0) {
            bool3 = true;
          }
          onImeVisibilityChanged(bool3, paramParcel1.readInt());
          return true;
        case 2: 
          paramParcel1.enforceInterface("android.view.IPinnedStackListener");
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          Rect localRect1;
          if (paramParcel1.readInt() != 0) {
            localRect1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            localRect1 = null;
          }
          Rect localRect2;
          if (paramParcel1.readInt() != 0) {
            localRect2 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            localRect2 = null;
          }
          if (paramParcel1.readInt() != 0) {
            bool3 = true;
          } else {
            bool3 = false;
          }
          if (paramParcel1.readInt() != 0) {
            bool1 = true;
          } else {
            bool1 = false;
          }
          onMovementBoundsChanged(paramParcel2, localRect1, localRect2, bool3, bool1, paramParcel1.readInt());
          return true;
        }
        paramParcel1.enforceInterface("android.view.IPinnedStackListener");
        onListenerRegistered(IPinnedStackController.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      }
      paramParcel2.writeString("android.view.IPinnedStackListener");
      return true;
    }
    
    private static class Proxy
      implements IPinnedStackListener
    {
      public static IPinnedStackListener sDefaultImpl;
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
        return "android.view.IPinnedStackListener";
      }
      
      public void onActionsChanged(ParceledListSlice paramParceledListSlice)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IPinnedStackListener");
          if (paramParceledListSlice != null)
          {
            localParcel.writeInt(1);
            paramParceledListSlice.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(6, localParcel, null, 1)) && (IPinnedStackListener.Stub.getDefaultImpl() != null))
          {
            IPinnedStackListener.Stub.getDefaultImpl().onActionsChanged(paramParceledListSlice);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onImeVisibilityChanged(boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IPinnedStackListener");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(3, localParcel, null, 1)) && (IPinnedStackListener.Stub.getDefaultImpl() != null))
          {
            IPinnedStackListener.Stub.getDefaultImpl().onImeVisibilityChanged(paramBoolean, paramInt);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onListenerRegistered(IPinnedStackController paramIPinnedStackController)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IPinnedStackListener");
          IBinder localIBinder;
          if (paramIPinnedStackController != null) {
            localIBinder = paramIPinnedStackController.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IPinnedStackListener.Stub.getDefaultImpl() != null))
          {
            IPinnedStackListener.Stub.getDefaultImpl().onListenerRegistered(paramIPinnedStackController);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onMinimizedStateChanged(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IPinnedStackListener");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(5, localParcel, null, 1)) && (IPinnedStackListener.Stub.getDefaultImpl() != null))
          {
            IPinnedStackListener.Stub.getDefaultImpl().onMinimizedStateChanged(paramBoolean);
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
      public void onMovementBoundsChanged(Rect paramRect1, Rect paramRect2, Rect paramRect3, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 38	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 7
        //   5: aload 7
        //   7: ldc 28
        //   9: invokevirtual 42	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: iconst_0
        //   13: istore 8
        //   15: aload_1
        //   16: ifnull +19 -> 35
        //   19: aload 7
        //   21: iconst_1
        //   22: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   25: aload_1
        //   26: aload 7
        //   28: iconst_0
        //   29: invokevirtual 92	android/graphics/Rect:writeToParcel	(Landroid/os/Parcel;I)V
        //   32: goto +9 -> 41
        //   35: aload 7
        //   37: iconst_0
        //   38: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   41: aload_2
        //   42: ifnull +19 -> 61
        //   45: aload 7
        //   47: iconst_1
        //   48: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   51: aload_2
        //   52: aload 7
        //   54: iconst_0
        //   55: invokevirtual 92	android/graphics/Rect:writeToParcel	(Landroid/os/Parcel;I)V
        //   58: goto +9 -> 67
        //   61: aload 7
        //   63: iconst_0
        //   64: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   67: aload_3
        //   68: ifnull +19 -> 87
        //   71: aload 7
        //   73: iconst_1
        //   74: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   77: aload_3
        //   78: aload 7
        //   80: iconst_0
        //   81: invokevirtual 92	android/graphics/Rect:writeToParcel	(Landroid/os/Parcel;I)V
        //   84: goto +9 -> 93
        //   87: aload 7
        //   89: iconst_0
        //   90: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   93: iload 4
        //   95: ifeq +9 -> 104
        //   98: iconst_1
        //   99: istore 9
        //   101: goto +6 -> 107
        //   104: iconst_0
        //   105: istore 9
        //   107: aload 7
        //   109: iload 9
        //   111: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   114: iload 8
        //   116: istore 9
        //   118: iload 5
        //   120: ifeq +6 -> 126
        //   123: iconst_1
        //   124: istore 9
        //   126: aload 7
        //   128: iload 9
        //   130: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   133: aload 7
        //   135: iload 6
        //   137: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   140: aload_0
        //   141: getfield 21	android/view/IPinnedStackListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   144: iconst_2
        //   145: aload 7
        //   147: aconst_null
        //   148: iconst_1
        //   149: invokeinterface 58 5 0
        //   154: ifne +32 -> 186
        //   157: invokestatic 62	android/view/IPinnedStackListener$Stub:getDefaultImpl	()Landroid/view/IPinnedStackListener;
        //   160: ifnull +26 -> 186
        //   163: invokestatic 62	android/view/IPinnedStackListener$Stub:getDefaultImpl	()Landroid/view/IPinnedStackListener;
        //   166: aload_1
        //   167: aload_2
        //   168: aload_3
        //   169: iload 4
        //   171: iload 5
        //   173: iload 6
        //   175: invokeinterface 94 7 0
        //   180: aload 7
        //   182: invokevirtual 67	android/os/Parcel:recycle	()V
        //   185: return
        //   186: aload 7
        //   188: invokevirtual 67	android/os/Parcel:recycle	()V
        //   191: return
        //   192: astore_1
        //   193: goto +8 -> 201
        //   196: astore_1
        //   197: goto +4 -> 201
        //   200: astore_1
        //   201: aload 7
        //   203: invokevirtual 67	android/os/Parcel:recycle	()V
        //   206: aload_1
        //   207: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	208	0	this	Proxy
        //   0	208	1	paramRect1	Rect
        //   0	208	2	paramRect2	Rect
        //   0	208	3	paramRect3	Rect
        //   0	208	4	paramBoolean1	boolean
        //   0	208	5	paramBoolean2	boolean
        //   0	208	6	paramInt	int
        //   3	199	7	localParcel	Parcel
        //   13	102	8	i	int
        //   99	30	9	j	int
        // Exception table:
        //   from	to	target	type
        //   140	180	192	finally
        //   133	140	196	finally
        //   5	12	200	finally
        //   19	32	200	finally
        //   35	41	200	finally
        //   45	58	200	finally
        //   61	67	200	finally
        //   71	84	200	finally
        //   87	93	200	finally
        //   107	114	200	finally
        //   126	133	200	finally
      }
      
      public void onShelfVisibilityChanged(boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IPinnedStackListener");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(4, localParcel, null, 1)) && (IPinnedStackListener.Stub.getDefaultImpl() != null))
          {
            IPinnedStackListener.Stub.getDefaultImpl().onShelfVisibilityChanged(paramBoolean, paramInt);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IPinnedStackListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */