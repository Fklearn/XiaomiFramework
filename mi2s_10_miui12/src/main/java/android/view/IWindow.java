package android.view;

import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.util.MergedConfiguration;
import com.android.internal.os.IResultReceiver;
import com.android.internal.os.IResultReceiver.Stub;

public abstract interface IWindow
  extends IInterface
{
  public abstract void closeSystemDialogs(String paramString)
    throws RemoteException;
  
  public abstract void dispatchAppVisibility(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void dispatchDragEvent(DragEvent paramDragEvent)
    throws RemoteException;
  
  public abstract void dispatchGetNewSurface()
    throws RemoteException;
  
  public abstract void dispatchPointerCaptureChanged(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void dispatchSystemUiVisibilityChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws RemoteException;
  
  public abstract void dispatchWallpaperCommand(String paramString, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void dispatchWallpaperOffsets(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void dispatchWindowShown()
    throws RemoteException;
  
  public abstract void executeCommand(String paramString1, String paramString2, ParcelFileDescriptor paramParcelFileDescriptor)
    throws RemoteException;
  
  public abstract void insetsChanged(InsetsState paramInsetsState)
    throws RemoteException;
  
  public abstract void insetsControlChanged(InsetsState paramInsetsState, InsetsSourceControl[] paramArrayOfInsetsSourceControl)
    throws RemoteException;
  
  public abstract void moved(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void notifyCastMode(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void notifyProjectionMode(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void notifyRotationChanged(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void requestAppKeyboardShortcuts(IResultReceiver paramIResultReceiver, int paramInt)
    throws RemoteException;
  
  public abstract void resized(Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5, Rect paramRect6, boolean paramBoolean1, MergedConfiguration paramMergedConfiguration, Rect paramRect7, boolean paramBoolean2, boolean paramBoolean3, int paramInt, DisplayCutout.ParcelableWrapper paramParcelableWrapper)
    throws RemoteException;
  
  public abstract void updatePointerIcon(float paramFloat1, float paramFloat2)
    throws RemoteException;
  
  public abstract void windowFocusChanged(boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException;
  
  public static class Default
    implements IWindow
  {
    public IBinder asBinder()
    {
      return null;
    }
    
    public void closeSystemDialogs(String paramString)
      throws RemoteException
    {}
    
    public void dispatchAppVisibility(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void dispatchDragEvent(DragEvent paramDragEvent)
      throws RemoteException
    {}
    
    public void dispatchGetNewSurface()
      throws RemoteException
    {}
    
    public void dispatchPointerCaptureChanged(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void dispatchSystemUiVisibilityChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      throws RemoteException
    {}
    
    public void dispatchWallpaperCommand(String paramString, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void dispatchWallpaperOffsets(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void dispatchWindowShown()
      throws RemoteException
    {}
    
    public void executeCommand(String paramString1, String paramString2, ParcelFileDescriptor paramParcelFileDescriptor)
      throws RemoteException
    {}
    
    public void insetsChanged(InsetsState paramInsetsState)
      throws RemoteException
    {}
    
    public void insetsControlChanged(InsetsState paramInsetsState, InsetsSourceControl[] paramArrayOfInsetsSourceControl)
      throws RemoteException
    {}
    
    public void moved(int paramInt1, int paramInt2)
      throws RemoteException
    {}
    
    public void notifyCastMode(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void notifyProjectionMode(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void notifyRotationChanged(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void requestAppKeyboardShortcuts(IResultReceiver paramIResultReceiver, int paramInt)
      throws RemoteException
    {}
    
    public void resized(Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5, Rect paramRect6, boolean paramBoolean1, MergedConfiguration paramMergedConfiguration, Rect paramRect7, boolean paramBoolean2, boolean paramBoolean3, int paramInt, DisplayCutout.ParcelableWrapper paramParcelableWrapper)
      throws RemoteException
    {}
    
    public void updatePointerIcon(float paramFloat1, float paramFloat2)
      throws RemoteException
    {}
    
    public void windowFocusChanged(boolean paramBoolean1, boolean paramBoolean2)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IWindow
  {
    private static final String DESCRIPTOR = "android.view.IWindow";
    static final int TRANSACTION_closeSystemDialogs = 9;
    static final int TRANSACTION_dispatchAppVisibility = 6;
    static final int TRANSACTION_dispatchDragEvent = 12;
    static final int TRANSACTION_dispatchGetNewSurface = 7;
    static final int TRANSACTION_dispatchPointerCaptureChanged = 17;
    static final int TRANSACTION_dispatchSystemUiVisibilityChanged = 14;
    static final int TRANSACTION_dispatchWallpaperCommand = 11;
    static final int TRANSACTION_dispatchWallpaperOffsets = 10;
    static final int TRANSACTION_dispatchWindowShown = 15;
    static final int TRANSACTION_executeCommand = 1;
    static final int TRANSACTION_insetsChanged = 3;
    static final int TRANSACTION_insetsControlChanged = 4;
    static final int TRANSACTION_moved = 5;
    static final int TRANSACTION_notifyCastMode = 18;
    static final int TRANSACTION_notifyProjectionMode = 20;
    static final int TRANSACTION_notifyRotationChanged = 19;
    static final int TRANSACTION_requestAppKeyboardShortcuts = 16;
    static final int TRANSACTION_resized = 2;
    static final int TRANSACTION_updatePointerIcon = 13;
    static final int TRANSACTION_windowFocusChanged = 8;
    
    public Stub()
    {
      attachInterface(this, "android.view.IWindow");
    }
    
    public static IWindow asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IWindow");
      if ((localIInterface != null) && ((localIInterface instanceof IWindow))) {
        return (IWindow)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IWindow getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return null;
      case 20: 
        return "notifyProjectionMode";
      case 19: 
        return "notifyRotationChanged";
      case 18: 
        return "notifyCastMode";
      case 17: 
        return "dispatchPointerCaptureChanged";
      case 16: 
        return "requestAppKeyboardShortcuts";
      case 15: 
        return "dispatchWindowShown";
      case 14: 
        return "dispatchSystemUiVisibilityChanged";
      case 13: 
        return "updatePointerIcon";
      case 12: 
        return "dispatchDragEvent";
      case 11: 
        return "dispatchWallpaperCommand";
      case 10: 
        return "dispatchWallpaperOffsets";
      case 9: 
        return "closeSystemDialogs";
      case 8: 
        return "windowFocusChanged";
      case 7: 
        return "dispatchGetNewSurface";
      case 6: 
        return "dispatchAppVisibility";
      case 5: 
        return "moved";
      case 4: 
        return "insetsControlChanged";
      case 3: 
        return "insetsChanged";
      case 2: 
        return "resized";
      }
      return "executeCommand";
    }
    
    public static boolean setDefaultImpl(IWindow paramIWindow)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIWindow != null))
      {
        Proxy.sDefaultImpl = paramIWindow;
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
        boolean bool6 = false;
        switch (paramInt1)
        {
        default: 
          return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        case 20: 
          paramParcel1.enforceInterface("android.view.IWindow");
          if (paramParcel1.readInt() != 0) {
            bool6 = true;
          }
          notifyProjectionMode(bool6);
          return true;
        case 19: 
          paramParcel1.enforceInterface("android.view.IWindow");
          bool6 = bool1;
          if (paramParcel1.readInt() != 0) {
            bool6 = true;
          }
          notifyRotationChanged(bool6);
          return true;
        case 18: 
          paramParcel1.enforceInterface("android.view.IWindow");
          bool6 = bool2;
          if (paramParcel1.readInt() != 0) {
            bool6 = true;
          }
          notifyCastMode(bool6);
          return true;
        case 17: 
          paramParcel1.enforceInterface("android.view.IWindow");
          bool6 = bool3;
          if (paramParcel1.readInt() != 0) {
            bool6 = true;
          }
          dispatchPointerCaptureChanged(bool6);
          return true;
        case 16: 
          paramParcel1.enforceInterface("android.view.IWindow");
          requestAppKeyboardShortcuts(IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          return true;
        case 15: 
          paramParcel1.enforceInterface("android.view.IWindow");
          dispatchWindowShown();
          return true;
        case 14: 
          paramParcel1.enforceInterface("android.view.IWindow");
          dispatchSystemUiVisibilityChanged(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
          return true;
        case 13: 
          paramParcel1.enforceInterface("android.view.IWindow");
          updatePointerIcon(paramParcel1.readFloat(), paramParcel1.readFloat());
          return true;
        case 12: 
          paramParcel1.enforceInterface("android.view.IWindow");
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (DragEvent)DragEvent.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          dispatchDragEvent(paramParcel1);
          return true;
        case 11: 
          paramParcel1.enforceInterface("android.view.IWindow");
          localObject = paramParcel1.readString();
          paramInt1 = paramParcel1.readInt();
          paramInt2 = paramParcel1.readInt();
          int i = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          if (paramParcel1.readInt() != 0) {
            bool6 = true;
          } else {
            bool6 = false;
          }
          dispatchWallpaperCommand((String)localObject, paramInt1, paramInt2, i, paramParcel2, bool6);
          return true;
        case 10: 
          paramParcel1.enforceInterface("android.view.IWindow");
          float f1 = paramParcel1.readFloat();
          float f2 = paramParcel1.readFloat();
          float f3 = paramParcel1.readFloat();
          float f4 = paramParcel1.readFloat();
          if (paramParcel1.readInt() != 0) {
            bool6 = true;
          } else {
            bool6 = false;
          }
          dispatchWallpaperOffsets(f1, f2, f3, f4, bool6);
          return true;
        case 9: 
          paramParcel1.enforceInterface("android.view.IWindow");
          closeSystemDialogs(paramParcel1.readString());
          return true;
        case 8: 
          paramParcel1.enforceInterface("android.view.IWindow");
          if (paramParcel1.readInt() != 0) {
            bool6 = true;
          } else {
            bool6 = false;
          }
          if (paramParcel1.readInt() != 0) {
            bool4 = true;
          }
          windowFocusChanged(bool6, bool4);
          return true;
        case 7: 
          paramParcel1.enforceInterface("android.view.IWindow");
          dispatchGetNewSurface();
          return true;
        case 6: 
          paramParcel1.enforceInterface("android.view.IWindow");
          bool6 = bool5;
          if (paramParcel1.readInt() != 0) {
            bool6 = true;
          }
          dispatchAppVisibility(bool6);
          return true;
        case 5: 
          paramParcel1.enforceInterface("android.view.IWindow");
          moved(paramParcel1.readInt(), paramParcel1.readInt());
          return true;
        case 4: 
          paramParcel1.enforceInterface("android.view.IWindow");
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (InsetsState)InsetsState.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          insetsControlChanged(paramParcel2, (InsetsSourceControl[])paramParcel1.createTypedArray(InsetsSourceControl.CREATOR));
          return true;
        case 3: 
          paramParcel1.enforceInterface("android.view.IWindow");
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (InsetsState)InsetsState.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          insetsChanged(paramParcel1);
          return true;
        case 2: 
          paramParcel1.enforceInterface("android.view.IWindow");
          if (paramParcel1.readInt() != 0) {
            paramParcel2 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel2 = null;
          }
          if (paramParcel1.readInt() != 0) {
            localObject = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject = null;
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
          Rect localRect3;
          if (paramParcel1.readInt() != 0) {
            localRect3 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            localRect3 = null;
          }
          Rect localRect4;
          if (paramParcel1.readInt() != 0) {
            localRect4 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            localRect4 = null;
          }
          if (paramParcel1.readInt() != 0) {
            bool6 = true;
          } else {
            bool6 = false;
          }
          MergedConfiguration localMergedConfiguration;
          if (paramParcel1.readInt() != 0) {
            localMergedConfiguration = (MergedConfiguration)MergedConfiguration.CREATOR.createFromParcel(paramParcel1);
          } else {
            localMergedConfiguration = null;
          }
          Rect localRect5;
          if (paramParcel1.readInt() != 0) {
            localRect5 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            localRect5 = null;
          }
          if (paramParcel1.readInt() != 0) {
            bool4 = true;
          } else {
            bool4 = false;
          }
          if (paramParcel1.readInt() != 0) {
            bool1 = true;
          } else {
            bool1 = false;
          }
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (DisplayCutout.ParcelableWrapper)DisplayCutout.ParcelableWrapper.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          resized(paramParcel2, (Rect)localObject, localRect1, localRect2, localRect3, localRect4, bool6, localMergedConfiguration, localRect5, bool4, bool1, paramInt1, paramParcel1);
          return true;
        }
        paramParcel1.enforceInterface("android.view.IWindow");
        Object localObject = paramParcel1.readString();
        paramParcel2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {
          paramParcel1 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);
        } else {
          paramParcel1 = null;
        }
        executeCommand((String)localObject, paramParcel2, paramParcel1);
        return true;
      }
      paramParcel2.writeString("android.view.IWindow");
      return true;
    }
    
    private static class Proxy
      implements IWindow
    {
      public static IWindow sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void closeSystemDialogs(String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          localParcel.writeString(paramString);
          if ((!this.mRemote.transact(9, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().closeSystemDialogs(paramString);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void dispatchAppVisibility(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(6, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().dispatchAppVisibility(paramBoolean);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void dispatchDragEvent(DragEvent paramDragEvent)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          if (paramDragEvent != null)
          {
            localParcel.writeInt(1);
            paramDragEvent.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(12, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().dispatchDragEvent(paramDragEvent);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void dispatchGetNewSurface()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          if ((!this.mRemote.transact(7, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().dispatchGetNewSurface();
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void dispatchPointerCaptureChanged(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(17, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().dispatchPointerCaptureChanged(paramBoolean);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void dispatchSystemUiVisibilityChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          localParcel.writeInt(paramInt3);
          localParcel.writeInt(paramInt4);
          if ((!this.mRemote.transact(14, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().dispatchSystemUiVisibilityChanged(paramInt1, paramInt2, paramInt3, paramInt4);
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
      public void dispatchWallpaperCommand(String paramString, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 34	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 7
        //   5: aload 7
        //   7: ldc 36
        //   9: invokevirtual 39	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload 7
        //   14: aload_1
        //   15: invokevirtual 42	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   18: aload 7
        //   20: iload_2
        //   21: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   24: aload 7
        //   26: iload_3
        //   27: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   30: aload 7
        //   32: iload 4
        //   34: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   37: iconst_0
        //   38: istore 8
        //   40: aload 5
        //   42: ifnull +20 -> 62
        //   45: aload 7
        //   47: iconst_1
        //   48: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   51: aload 5
        //   53: aload 7
        //   55: iconst_0
        //   56: invokevirtual 91	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   59: goto +9 -> 68
        //   62: aload 7
        //   64: iconst_0
        //   65: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   68: iload 6
        //   70: ifeq +6 -> 76
        //   73: iconst_1
        //   74: istore 8
        //   76: aload 7
        //   78: iload 8
        //   80: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   83: aload_0
        //   84: getfield 21	android/view/IWindow$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   87: bipush 11
        //   89: aload 7
        //   91: aconst_null
        //   92: iconst_1
        //   93: invokeinterface 48 5 0
        //   98: ifne +32 -> 130
        //   101: invokestatic 52	android/view/IWindow$Stub:getDefaultImpl	()Landroid/view/IWindow;
        //   104: ifnull +26 -> 130
        //   107: invokestatic 52	android/view/IWindow$Stub:getDefaultImpl	()Landroid/view/IWindow;
        //   110: aload_1
        //   111: iload_2
        //   112: iload_3
        //   113: iload 4
        //   115: aload 5
        //   117: iload 6
        //   119: invokeinterface 93 7 0
        //   124: aload 7
        //   126: invokevirtual 57	android/os/Parcel:recycle	()V
        //   129: return
        //   130: aload 7
        //   132: invokevirtual 57	android/os/Parcel:recycle	()V
        //   135: return
        //   136: astore_1
        //   137: goto +20 -> 157
        //   140: astore_1
        //   141: goto +16 -> 157
        //   144: astore_1
        //   145: goto +12 -> 157
        //   148: astore_1
        //   149: goto +8 -> 157
        //   152: astore_1
        //   153: goto +4 -> 157
        //   156: astore_1
        //   157: aload 7
        //   159: invokevirtual 57	android/os/Parcel:recycle	()V
        //   162: aload_1
        //   163: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	164	0	this	Proxy
        //   0	164	1	paramString	String
        //   0	164	2	paramInt1	int
        //   0	164	3	paramInt2	int
        //   0	164	4	paramInt3	int
        //   0	164	5	paramBundle	Bundle
        //   0	164	6	paramBoolean	boolean
        //   3	155	7	localParcel	Parcel
        //   38	41	8	i	int
        // Exception table:
        //   from	to	target	type
        //   83	124	136	finally
        //   30	37	140	finally
        //   45	59	140	finally
        //   62	68	140	finally
        //   76	83	140	finally
        //   24	30	144	finally
        //   18	24	148	finally
        //   12	18	152	finally
        //   5	12	156	finally
      }
      
      public void dispatchWallpaperOffsets(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          localParcel.writeFloat(paramFloat1);
          localParcel.writeFloat(paramFloat2);
          localParcel.writeFloat(paramFloat3);
          localParcel.writeFloat(paramFloat4);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(10, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().dispatchWallpaperOffsets(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramBoolean);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void dispatchWindowShown()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          if ((!this.mRemote.transact(15, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().dispatchWindowShown();
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void executeCommand(String paramString1, String paramString2, ParcelFileDescriptor paramParcelFileDescriptor)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          localParcel.writeString(paramString1);
          localParcel.writeString(paramString2);
          if (paramParcelFileDescriptor != null)
          {
            localParcel.writeInt(1);
            paramParcelFileDescriptor.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(1, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().executeCommand(paramString1, paramString2, paramParcelFileDescriptor);
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
        return "android.view.IWindow";
      }
      
      public void insetsChanged(InsetsState paramInsetsState)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          if (paramInsetsState != null)
          {
            localParcel.writeInt(1);
            paramInsetsState.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(3, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().insetsChanged(paramInsetsState);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void insetsControlChanged(InsetsState paramInsetsState, InsetsSourceControl[] paramArrayOfInsetsSourceControl)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          if (paramInsetsState != null)
          {
            localParcel.writeInt(1);
            paramInsetsState.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          localParcel.writeTypedArray(paramArrayOfInsetsSourceControl, 0);
          if ((!this.mRemote.transact(4, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().insetsControlChanged(paramInsetsState, paramArrayOfInsetsSourceControl);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void moved(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          if ((!this.mRemote.transact(5, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().moved(paramInt1, paramInt2);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void notifyCastMode(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(18, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().notifyCastMode(paramBoolean);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void notifyProjectionMode(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(20, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().notifyProjectionMode(paramBoolean);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void notifyRotationChanged(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(19, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().notifyRotationChanged(paramBoolean);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void requestAppKeyboardShortcuts(IResultReceiver paramIResultReceiver, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeInt(paramInt);
          if ((!this.mRemote.transact(16, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().requestAppKeyboardShortcuts(paramIResultReceiver, paramInt);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void resized(Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5, Rect paramRect6, boolean paramBoolean1, MergedConfiguration paramMergedConfiguration, Rect paramRect7, boolean paramBoolean2, boolean paramBoolean3, int paramInt, DisplayCutout.ParcelableWrapper paramParcelableWrapper)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          if (paramRect1 != null) {
            try
            {
              localParcel.writeInt(1);
              paramRect1.writeToParcel(localParcel, 0);
            }
            finally
            {
              break label407;
            }
          } else {
            localParcel.writeInt(0);
          }
          if (paramRect2 != null)
          {
            localParcel.writeInt(1);
            paramRect2.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if (paramRect3 != null)
          {
            localParcel.writeInt(1);
            paramRect3.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if (paramRect4 != null)
          {
            localParcel.writeInt(1);
            paramRect4.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if (paramRect5 != null)
          {
            localParcel.writeInt(1);
            paramRect5.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if (paramRect6 != null)
          {
            localParcel.writeInt(1);
            paramRect6.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          int i;
          if (paramBoolean1) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if (paramMergedConfiguration != null)
          {
            localParcel.writeInt(1);
            paramMergedConfiguration.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if (paramRect7 != null)
          {
            localParcel.writeInt(1);
            paramRect7.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if (paramBoolean2) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if (paramBoolean3) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          localParcel.writeInt(paramInt);
          if (paramParcelableWrapper != null)
          {
            localParcel.writeInt(1);
            paramParcelableWrapper.writeToParcel(localParcel, 0);
          }
          else
          {
            localParcel.writeInt(0);
          }
          if ((!this.mRemote.transact(2, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow localIWindow = IWindow.Stub.getDefaultImpl();
            try
            {
              localIWindow.resized(paramRect1, paramRect2, paramRect3, paramRect4, paramRect5, paramRect6, paramBoolean1, paramMergedConfiguration, paramRect7, paramBoolean2, paramBoolean3, paramInt, paramParcelableWrapper);
              return;
            }
            finally
            {
              break label407;
            }
          }
          return;
        }
        finally
        {
          label407:
          localParcel.recycle();
        }
      }
      
      public void updatePointerIcon(float paramFloat1, float paramFloat2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          localParcel.writeFloat(paramFloat1);
          localParcel.writeFloat(paramFloat2);
          if ((!this.mRemote.transact(13, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().updatePointerIcon(paramFloat1, paramFloat2);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void windowFocusChanged(boolean paramBoolean1, boolean paramBoolean2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindow");
          int i = 0;
          if (paramBoolean1) {
            j = 1;
          } else {
            j = 0;
          }
          localParcel.writeInt(j);
          int j = i;
          if (paramBoolean2) {
            j = 1;
          }
          localParcel.writeInt(j);
          if ((!this.mRemote.transact(8, localParcel, null, 1)) && (IWindow.Stub.getDefaultImpl() != null))
          {
            IWindow.Stub.getDefaultImpl().windowFocusChanged(paramBoolean1, paramBoolean2);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */