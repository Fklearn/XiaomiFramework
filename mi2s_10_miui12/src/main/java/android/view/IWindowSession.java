package android.view;

import android.annotation.UnsupportedAppUsage;
import android.content.ClipData;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.util.MergedConfiguration;
import java.util.List;

public abstract interface IWindowSession
  extends IInterface
{
  public abstract int addToDisplay(IWindow paramIWindow, int paramInt1, WindowManager.LayoutParams paramLayoutParams, int paramInt2, int paramInt3, Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, DisplayCutout.ParcelableWrapper paramParcelableWrapper, InputChannel paramInputChannel, InsetsState paramInsetsState)
    throws RemoteException;
  
  public abstract int addToDisplayWithoutInputChannel(IWindow paramIWindow, int paramInt1, WindowManager.LayoutParams paramLayoutParams, int paramInt2, int paramInt3, Rect paramRect1, Rect paramRect2, InsetsState paramInsetsState)
    throws RemoteException;
  
  public abstract void cancelDragAndDrop(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void dragRecipientEntered(IWindow paramIWindow)
    throws RemoteException;
  
  public abstract void dragRecipientExited(IWindow paramIWindow)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void finishDrawing(IWindow paramIWindow)
    throws RemoteException;
  
  public abstract void finishMovingTask(IWindow paramIWindow)
    throws RemoteException;
  
  public abstract void getDisplayFrame(IWindow paramIWindow, Rect paramRect)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract boolean getInTouchMode()
    throws RemoteException;
  
  public abstract IWindowId getWindowId(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void insetsModified(IWindow paramIWindow, InsetsState paramInsetsState)
    throws RemoteException;
  
  public abstract void notifyHasSurfaceView(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onRectangleOnScreenRequested(IBinder paramIBinder, Rect paramRect)
    throws RemoteException;
  
  public abstract boolean outOfMemory(IWindow paramIWindow)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract IBinder performDrag(IWindow paramIWindow, int paramInt1, SurfaceControl paramSurfaceControl, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, ClipData paramClipData)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract boolean performHapticFeedback(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void pokeDrawLock(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void prepareToReplaceWindows(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int relayout(IWindow paramIWindow, int paramInt1, WindowManager.LayoutParams paramLayoutParams, int paramInt2, int paramInt3, int paramInt4, int paramInt5, long paramLong, Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5, Rect paramRect6, Rect paramRect7, DisplayCutout.ParcelableWrapper paramParcelableWrapper, MergedConfiguration paramMergedConfiguration, SurfaceControl paramSurfaceControl, InsetsState paramInsetsState)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void remove(IWindow paramIWindow)
    throws RemoteException;
  
  public abstract void reparentDisplayContent(IWindow paramIWindow, SurfaceControl paramSurfaceControl, int paramInt)
    throws RemoteException;
  
  public abstract void reportDropResult(IWindow paramIWindow, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void reportSystemGestureExclusionChanged(IWindow paramIWindow, List<Rect> paramList)
    throws RemoteException;
  
  public abstract Bundle sendWallpaperCommand(IBinder paramIBinder, String paramString, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle, boolean paramBoolean)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void setInTouchMode(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setInsets(IWindow paramIWindow, int paramInt, Rect paramRect1, Rect paramRect2, Region paramRegion)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void setTransparentRegion(IWindow paramIWindow, Region paramRegion)
    throws RemoteException;
  
  public abstract void setWallpaperDisplayOffset(IBinder paramIBinder, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setWallpaperPosition(IBinder paramIBinder, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    throws RemoteException;
  
  public abstract boolean startMovingTask(IWindow paramIWindow, float paramFloat1, float paramFloat2)
    throws RemoteException;
  
  public abstract void updateDisplayContentLocation(IWindow paramIWindow, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void updatePointerIcon(IWindow paramIWindow)
    throws RemoteException;
  
  public abstract void updateTapExcludeRegion(IWindow paramIWindow, int paramInt, Region paramRegion)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void wallpaperCommandComplete(IBinder paramIBinder, Bundle paramBundle)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void wallpaperOffsetsComplete(IBinder paramIBinder)
    throws RemoteException;
  
  public static class Default
    implements IWindowSession
  {
    public int addToDisplay(IWindow paramIWindow, int paramInt1, WindowManager.LayoutParams paramLayoutParams, int paramInt2, int paramInt3, Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, DisplayCutout.ParcelableWrapper paramParcelableWrapper, InputChannel paramInputChannel, InsetsState paramInsetsState)
      throws RemoteException
    {
      return 0;
    }
    
    public int addToDisplayWithoutInputChannel(IWindow paramIWindow, int paramInt1, WindowManager.LayoutParams paramLayoutParams, int paramInt2, int paramInt3, Rect paramRect1, Rect paramRect2, InsetsState paramInsetsState)
      throws RemoteException
    {
      return 0;
    }
    
    public IBinder asBinder()
    {
      return null;
    }
    
    public void cancelDragAndDrop(IBinder paramIBinder, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void dragRecipientEntered(IWindow paramIWindow)
      throws RemoteException
    {}
    
    public void dragRecipientExited(IWindow paramIWindow)
      throws RemoteException
    {}
    
    public void finishDrawing(IWindow paramIWindow)
      throws RemoteException
    {}
    
    public void finishMovingTask(IWindow paramIWindow)
      throws RemoteException
    {}
    
    public void getDisplayFrame(IWindow paramIWindow, Rect paramRect)
      throws RemoteException
    {}
    
    public boolean getInTouchMode()
      throws RemoteException
    {
      return false;
    }
    
    public IWindowId getWindowId(IBinder paramIBinder)
      throws RemoteException
    {
      return null;
    }
    
    public void insetsModified(IWindow paramIWindow, InsetsState paramInsetsState)
      throws RemoteException
    {}
    
    public void notifyHasSurfaceView(IBinder paramIBinder, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void onRectangleOnScreenRequested(IBinder paramIBinder, Rect paramRect)
      throws RemoteException
    {}
    
    public boolean outOfMemory(IWindow paramIWindow)
      throws RemoteException
    {
      return false;
    }
    
    public IBinder performDrag(IWindow paramIWindow, int paramInt1, SurfaceControl paramSurfaceControl, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, ClipData paramClipData)
      throws RemoteException
    {
      return null;
    }
    
    public boolean performHapticFeedback(int paramInt, boolean paramBoolean)
      throws RemoteException
    {
      return false;
    }
    
    public void pokeDrawLock(IBinder paramIBinder)
      throws RemoteException
    {}
    
    public void prepareToReplaceWindows(IBinder paramIBinder, boolean paramBoolean)
      throws RemoteException
    {}
    
    public int relayout(IWindow paramIWindow, int paramInt1, WindowManager.LayoutParams paramLayoutParams, int paramInt2, int paramInt3, int paramInt4, int paramInt5, long paramLong, Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5, Rect paramRect6, Rect paramRect7, DisplayCutout.ParcelableWrapper paramParcelableWrapper, MergedConfiguration paramMergedConfiguration, SurfaceControl paramSurfaceControl, InsetsState paramInsetsState)
      throws RemoteException
    {
      return 0;
    }
    
    public void remove(IWindow paramIWindow)
      throws RemoteException
    {}
    
    public void reparentDisplayContent(IWindow paramIWindow, SurfaceControl paramSurfaceControl, int paramInt)
      throws RemoteException
    {}
    
    public void reportDropResult(IWindow paramIWindow, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void reportSystemGestureExclusionChanged(IWindow paramIWindow, List<Rect> paramList)
      throws RemoteException
    {}
    
    public Bundle sendWallpaperCommand(IBinder paramIBinder, String paramString, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle, boolean paramBoolean)
      throws RemoteException
    {
      return null;
    }
    
    public void setInTouchMode(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setInsets(IWindow paramIWindow, int paramInt, Rect paramRect1, Rect paramRect2, Region paramRegion)
      throws RemoteException
    {}
    
    public void setTransparentRegion(IWindow paramIWindow, Region paramRegion)
      throws RemoteException
    {}
    
    public void setWallpaperDisplayOffset(IBinder paramIBinder, int paramInt1, int paramInt2)
      throws RemoteException
    {}
    
    public void setWallpaperPosition(IBinder paramIBinder, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
      throws RemoteException
    {}
    
    public boolean startMovingTask(IWindow paramIWindow, float paramFloat1, float paramFloat2)
      throws RemoteException
    {
      return false;
    }
    
    public void updateDisplayContentLocation(IWindow paramIWindow, int paramInt1, int paramInt2, int paramInt3)
      throws RemoteException
    {}
    
    public void updatePointerIcon(IWindow paramIWindow)
      throws RemoteException
    {}
    
    public void updateTapExcludeRegion(IWindow paramIWindow, int paramInt, Region paramRegion)
      throws RemoteException
    {}
    
    public void wallpaperCommandComplete(IBinder paramIBinder, Bundle paramBundle)
      throws RemoteException
    {}
    
    public void wallpaperOffsetsComplete(IBinder paramIBinder)
      throws RemoteException
    {}
  }
  
  public static abstract class Stub
    extends Binder
    implements IWindowSession
  {
    private static final String DESCRIPTOR = "android.view.IWindowSession";
    static final int TRANSACTION_addToDisplay = 1;
    static final int TRANSACTION_addToDisplayWithoutInputChannel = 2;
    static final int TRANSACTION_cancelDragAndDrop = 16;
    static final int TRANSACTION_dragRecipientEntered = 17;
    static final int TRANSACTION_dragRecipientExited = 18;
    static final int TRANSACTION_finishDrawing = 10;
    static final int TRANSACTION_finishMovingTask = 28;
    static final int TRANSACTION_getDisplayFrame = 9;
    static final int TRANSACTION_getInTouchMode = 12;
    static final int TRANSACTION_getWindowId = 25;
    static final int TRANSACTION_insetsModified = 33;
    static final int TRANSACTION_notifyHasSurfaceView = 35;
    static final int TRANSACTION_onRectangleOnScreenRequested = 24;
    static final int TRANSACTION_outOfMemory = 6;
    static final int TRANSACTION_performDrag = 14;
    static final int TRANSACTION_performHapticFeedback = 13;
    static final int TRANSACTION_pokeDrawLock = 26;
    static final int TRANSACTION_prepareToReplaceWindows = 5;
    static final int TRANSACTION_relayout = 4;
    static final int TRANSACTION_remove = 3;
    static final int TRANSACTION_reparentDisplayContent = 30;
    static final int TRANSACTION_reportDropResult = 15;
    static final int TRANSACTION_reportSystemGestureExclusionChanged = 34;
    static final int TRANSACTION_sendWallpaperCommand = 22;
    static final int TRANSACTION_setInTouchMode = 11;
    static final int TRANSACTION_setInsets = 8;
    static final int TRANSACTION_setTransparentRegion = 7;
    static final int TRANSACTION_setWallpaperDisplayOffset = 21;
    static final int TRANSACTION_setWallpaperPosition = 19;
    static final int TRANSACTION_startMovingTask = 27;
    static final int TRANSACTION_updateDisplayContentLocation = 31;
    static final int TRANSACTION_updatePointerIcon = 29;
    static final int TRANSACTION_updateTapExcludeRegion = 32;
    static final int TRANSACTION_wallpaperCommandComplete = 23;
    static final int TRANSACTION_wallpaperOffsetsComplete = 20;
    
    public Stub()
    {
      attachInterface(this, "android.view.IWindowSession");
    }
    
    public static IWindowSession asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IWindowSession");
      if ((localIInterface != null) && ((localIInterface instanceof IWindowSession))) {
        return (IWindowSession)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IWindowSession getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return null;
      case 35: 
        return "notifyHasSurfaceView";
      case 34: 
        return "reportSystemGestureExclusionChanged";
      case 33: 
        return "insetsModified";
      case 32: 
        return "updateTapExcludeRegion";
      case 31: 
        return "updateDisplayContentLocation";
      case 30: 
        return "reparentDisplayContent";
      case 29: 
        return "updatePointerIcon";
      case 28: 
        return "finishMovingTask";
      case 27: 
        return "startMovingTask";
      case 26: 
        return "pokeDrawLock";
      case 25: 
        return "getWindowId";
      case 24: 
        return "onRectangleOnScreenRequested";
      case 23: 
        return "wallpaperCommandComplete";
      case 22: 
        return "sendWallpaperCommand";
      case 21: 
        return "setWallpaperDisplayOffset";
      case 20: 
        return "wallpaperOffsetsComplete";
      case 19: 
        return "setWallpaperPosition";
      case 18: 
        return "dragRecipientExited";
      case 17: 
        return "dragRecipientEntered";
      case 16: 
        return "cancelDragAndDrop";
      case 15: 
        return "reportDropResult";
      case 14: 
        return "performDrag";
      case 13: 
        return "performHapticFeedback";
      case 12: 
        return "getInTouchMode";
      case 11: 
        return "setInTouchMode";
      case 10: 
        return "finishDrawing";
      case 9: 
        return "getDisplayFrame";
      case 8: 
        return "setInsets";
      case 7: 
        return "setTransparentRegion";
      case 6: 
        return "outOfMemory";
      case 5: 
        return "prepareToReplaceWindows";
      case 4: 
        return "relayout";
      case 3: 
        return "remove";
      case 2: 
        return "addToDisplayWithoutInputChannel";
      }
      return "addToDisplay";
    }
    
    public static boolean setDefaultImpl(IWindowSession paramIWindowSession)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIWindowSession != null))
      {
        Proxy.sDefaultImpl = paramIWindowSession;
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
        Object localObject1;
        switch (paramInt1)
        {
        default: 
          return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        case 35: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          paramParcel2 = paramParcel1.readStrongBinder();
          if (paramParcel1.readInt() != 0) {
            bool6 = true;
          }
          notifyHasSurfaceView(paramParcel2, bool6);
          return true;
        case 34: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          reportSystemGestureExclusionChanged(IWindow.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.createTypedArrayList(Rect.CREATOR));
          return true;
        case 33: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          localObject1 = IWindow.Stub.asInterface(paramParcel1.readStrongBinder());
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (InsetsState)InsetsState.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          insetsModified((IWindow)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        case 32: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          localObject1 = IWindow.Stub.asInterface(paramParcel1.readStrongBinder());
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (Region)Region.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          updateTapExcludeRegion((IWindow)localObject1, paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        case 31: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          updateDisplayContentLocation(IWindow.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 30: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          localObject2 = IWindow.Stub.asInterface(paramParcel1.readStrongBinder());
          if (paramParcel1.readInt() != 0) {
            localObject1 = (SurfaceControl)SurfaceControl.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject1 = null;
          }
          reparentDisplayContent((IWindow)localObject2, (SurfaceControl)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 29: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          updatePointerIcon(IWindow.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 28: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          finishMovingTask(IWindow.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 27: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          paramInt1 = startMovingTask(IWindow.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readFloat(), paramParcel1.readFloat());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 26: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          pokeDrawLock(paramParcel1.readStrongBinder());
          paramParcel2.writeNoException();
          return true;
        case 25: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          paramParcel1 = getWindowId(paramParcel1.readStrongBinder());
          paramParcel2.writeNoException();
          if (paramParcel1 != null) {
            paramParcel1 = paramParcel1.asBinder();
          } else {
            paramParcel1 = null;
          }
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        case 24: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          localObject1 = paramParcel1.readStrongBinder();
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          onRectangleOnScreenRequested((IBinder)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        case 23: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          localObject1 = paramParcel1.readStrongBinder();
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          wallpaperCommandComplete((IBinder)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        case 22: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          localObject2 = paramParcel1.readStrongBinder();
          localObject3 = paramParcel1.readString();
          paramInt2 = paramParcel1.readInt();
          paramInt1 = paramParcel1.readInt();
          i = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            localObject1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject1 = null;
          }
          if (paramParcel1.readInt() != 0) {
            bool6 = true;
          } else {
            bool6 = false;
          }
          paramParcel1 = sendWallpaperCommand((IBinder)localObject2, (String)localObject3, paramInt2, paramInt1, i, (Bundle)localObject1, bool6);
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
        case 21: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          setWallpaperDisplayOffset(paramParcel1.readStrongBinder(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 20: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          wallpaperOffsetsComplete(paramParcel1.readStrongBinder());
          paramParcel2.writeNoException();
          return true;
        case 19: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          setWallpaperPosition(paramParcel1.readStrongBinder(), paramParcel1.readFloat(), paramParcel1.readFloat(), paramParcel1.readFloat(), paramParcel1.readFloat());
          paramParcel2.writeNoException();
          return true;
        case 18: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          dragRecipientExited(IWindow.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 17: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          dragRecipientEntered(IWindow.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 16: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          localObject1 = paramParcel1.readStrongBinder();
          bool6 = bool1;
          if (paramParcel1.readInt() != 0) {
            bool6 = true;
          }
          cancelDragAndDrop((IBinder)localObject1, bool6);
          paramParcel2.writeNoException();
          return true;
        case 15: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          localObject1 = IWindow.Stub.asInterface(paramParcel1.readStrongBinder());
          bool6 = bool2;
          if (paramParcel1.readInt() != 0) {
            bool6 = true;
          }
          reportDropResult((IWindow)localObject1, bool6);
          paramParcel2.writeNoException();
          return true;
        case 14: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          localObject2 = IWindow.Stub.asInterface(paramParcel1.readStrongBinder());
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            localObject1 = (SurfaceControl)SurfaceControl.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject1 = null;
          }
          paramInt2 = paramParcel1.readInt();
          float f1 = paramParcel1.readFloat();
          float f2 = paramParcel1.readFloat();
          float f3 = paramParcel1.readFloat();
          float f4 = paramParcel1.readFloat();
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (ClipData)ClipData.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          paramParcel1 = performDrag((IWindow)localObject2, paramInt1, (SurfaceControl)localObject1, paramInt2, f1, f2, f3, f4, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        case 13: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          paramInt1 = paramParcel1.readInt();
          bool6 = bool3;
          if (paramParcel1.readInt() != 0) {
            bool6 = true;
          }
          paramInt1 = performHapticFeedback(paramInt1, bool6);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 12: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          paramInt1 = getInTouchMode();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 11: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          bool6 = bool4;
          if (paramParcel1.readInt() != 0) {
            bool6 = true;
          }
          setInTouchMode(bool6);
          paramParcel2.writeNoException();
          return true;
        case 10: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          finishDrawing(IWindow.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 9: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          paramParcel1 = IWindow.Stub.asInterface(paramParcel1.readStrongBinder());
          localObject1 = new Rect();
          getDisplayFrame(paramParcel1, (Rect)localObject1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(1);
          ((Rect)localObject1).writeToParcel(paramParcel2, 1);
          return true;
        case 8: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          localObject3 = IWindow.Stub.asInterface(paramParcel1.readStrongBinder());
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            localObject1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject1 = null;
          }
          if (paramParcel1.readInt() != 0) {
            localObject2 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject2 = null;
          }
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (Region)Region.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          setInsets((IWindow)localObject3, paramInt1, (Rect)localObject1, (Rect)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        case 7: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          localObject1 = IWindow.Stub.asInterface(paramParcel1.readStrongBinder());
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (Region)Region.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          setTransparentRegion((IWindow)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        case 6: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          paramInt1 = outOfMemory(IWindow.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 5: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          localObject1 = paramParcel1.readStrongBinder();
          bool6 = bool5;
          if (paramParcel1.readInt() != 0) {
            bool6 = true;
          }
          prepareToReplaceWindows((IBinder)localObject1, bool6);
          paramParcel2.writeNoException();
          return true;
        case 4: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          localObject2 = IWindow.Stub.asInterface(paramParcel1.readStrongBinder());
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            localObject1 = (WindowManager.LayoutParams)WindowManager.LayoutParams.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject1 = null;
          }
          i = paramParcel1.readInt();
          paramInt2 = paramParcel1.readInt();
          int j = paramParcel1.readInt();
          int k = paramParcel1.readInt();
          long l = paramParcel1.readLong();
          paramParcel1 = new Rect();
          localObject3 = new Rect();
          Rect localRect1 = new Rect();
          localObject4 = new Rect();
          localRect2 = new Rect();
          Rect localRect3 = new Rect();
          localRect4 = new Rect();
          localObject5 = new DisplayCutout.ParcelableWrapper();
          MergedConfiguration localMergedConfiguration = new MergedConfiguration();
          SurfaceControl localSurfaceControl = new SurfaceControl();
          localObject6 = new InsetsState();
          paramInt1 = relayout((IWindow)localObject2, paramInt1, (WindowManager.LayoutParams)localObject1, i, paramInt2, j, k, l, paramParcel1, (Rect)localObject3, localRect1, (Rect)localObject4, localRect2, localRect3, localRect4, (DisplayCutout.ParcelableWrapper)localObject5, localMergedConfiguration, localSurfaceControl, (InsetsState)localObject6);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          paramParcel2.writeInt(1);
          ((Rect)localObject3).writeToParcel(paramParcel2, 1);
          paramParcel2.writeInt(1);
          localRect1.writeToParcel(paramParcel2, 1);
          paramParcel2.writeInt(1);
          ((Rect)localObject4).writeToParcel(paramParcel2, 1);
          paramParcel2.writeInt(1);
          localRect2.writeToParcel(paramParcel2, 1);
          paramParcel2.writeInt(1);
          localRect3.writeToParcel(paramParcel2, 1);
          paramParcel2.writeInt(1);
          localRect4.writeToParcel(paramParcel2, 1);
          paramParcel2.writeInt(1);
          ((DisplayCutout.ParcelableWrapper)localObject5).writeToParcel(paramParcel2, 1);
          paramParcel2.writeInt(1);
          localMergedConfiguration.writeToParcel(paramParcel2, 1);
          paramParcel2.writeInt(1);
          localSurfaceControl.writeToParcel(paramParcel2, 1);
          paramParcel2.writeInt(1);
          ((InsetsState)localObject6).writeToParcel(paramParcel2, 1);
          return true;
        case 3: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          remove(IWindow.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 2: 
          paramParcel1.enforceInterface("android.view.IWindowSession");
          localObject2 = IWindow.Stub.asInterface(paramParcel1.readStrongBinder());
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            localObject1 = (WindowManager.LayoutParams)WindowManager.LayoutParams.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject1 = null;
          }
          paramInt2 = paramParcel1.readInt();
          i = paramParcel1.readInt();
          localObject3 = new Rect();
          localRect4 = new Rect();
          paramParcel1 = new InsetsState();
          paramInt1 = addToDisplayWithoutInputChannel((IWindow)localObject2, paramInt1, (WindowManager.LayoutParams)localObject1, paramInt2, i, (Rect)localObject3, localRect4, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          paramParcel2.writeInt(1);
          ((Rect)localObject3).writeToParcel(paramParcel2, 1);
          paramParcel2.writeInt(1);
          localRect4.writeToParcel(paramParcel2, 1);
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel1.enforceInterface("android.view.IWindowSession");
        Object localObject2 = IWindow.Stub.asInterface(paramParcel1.readStrongBinder());
        paramInt2 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {
          localObject1 = (WindowManager.LayoutParams)WindowManager.LayoutParams.CREATOR.createFromParcel(paramParcel1);
        } else {
          localObject1 = null;
        }
        paramInt1 = paramParcel1.readInt();
        int i = paramParcel1.readInt();
        Rect localRect2 = new Rect();
        paramParcel1 = new Rect();
        Object localObject5 = new Rect();
        Rect localRect4 = new Rect();
        Object localObject3 = new DisplayCutout.ParcelableWrapper();
        Object localObject6 = new InputChannel();
        Object localObject4 = new InsetsState();
        paramInt1 = addToDisplay((IWindow)localObject2, paramInt2, (WindowManager.LayoutParams)localObject1, paramInt1, i, localRect2, paramParcel1, (Rect)localObject5, localRect4, (DisplayCutout.ParcelableWrapper)localObject3, (InputChannel)localObject6, (InsetsState)localObject4);
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        paramParcel2.writeInt(1);
        localRect2.writeToParcel(paramParcel2, 1);
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
        paramParcel2.writeInt(1);
        ((Rect)localObject5).writeToParcel(paramParcel2, 1);
        paramParcel2.writeInt(1);
        localRect4.writeToParcel(paramParcel2, 1);
        paramParcel2.writeInt(1);
        ((DisplayCutout.ParcelableWrapper)localObject3).writeToParcel(paramParcel2, 1);
        paramParcel2.writeInt(1);
        ((InputChannel)localObject6).writeToParcel(paramParcel2, 1);
        paramParcel2.writeInt(1);
        ((InsetsState)localObject4).writeToParcel(paramParcel2, 1);
        return true;
      }
      paramParcel2.writeString("android.view.IWindowSession");
      return true;
    }
    
    private static class Proxy
      implements IWindowSession
    {
      public static IWindowSession sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public int addToDisplay(IWindow paramIWindow, int paramInt1, WindowManager.LayoutParams paramLayoutParams, int paramInt2, int paramInt3, Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, DisplayCutout.ParcelableWrapper paramParcelableWrapper, InputChannel paramInputChannel, InsetsState paramInsetsState)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 13
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 14
        //   10: aload 13
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +18 -> 36
        //   21: aload_1
        //   22: invokeinterface 44 1 0
        //   27: astore 15
        //   29: goto +10 -> 39
        //   32: astore_1
        //   33: goto +341 -> 374
        //   36: aconst_null
        //   37: astore 15
        //   39: aload 13
        //   41: aload 15
        //   43: invokevirtual 47	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   46: aload 13
        //   48: iload_2
        //   49: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   52: aload_3
        //   53: ifnull +19 -> 72
        //   56: aload 13
        //   58: iconst_1
        //   59: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   62: aload_3
        //   63: aload 13
        //   65: iconst_0
        //   66: invokevirtual 57	android/view/WindowManager$LayoutParams:writeToParcel	(Landroid/os/Parcel;I)V
        //   69: goto +9 -> 78
        //   72: aload 13
        //   74: iconst_0
        //   75: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   78: aload 13
        //   80: iload 4
        //   82: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   85: aload 13
        //   87: iload 5
        //   89: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   92: aload_0
        //   93: getfield 21	android/view/IWindowSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   96: iconst_1
        //   97: aload 13
        //   99: aload 14
        //   101: iconst_0
        //   102: invokeinterface 63 5 0
        //   107: istore 16
        //   109: iload 16
        //   111: ifne +63 -> 174
        //   114: invokestatic 67	android/view/IWindowSession$Stub:getDefaultImpl	()Landroid/view/IWindowSession;
        //   117: ifnull +57 -> 174
        //   120: invokestatic 67	android/view/IWindowSession$Stub:getDefaultImpl	()Landroid/view/IWindowSession;
        //   123: astore 15
        //   125: aload 15
        //   127: aload_1
        //   128: iload_2
        //   129: aload_3
        //   130: iload 4
        //   132: iload 5
        //   134: aload 6
        //   136: aload 7
        //   138: aload 8
        //   140: aload 9
        //   142: aload 10
        //   144: aload 11
        //   146: aload 12
        //   148: invokeinterface 69 13 0
        //   153: istore_2
        //   154: aload 14
        //   156: invokevirtual 72	android/os/Parcel:recycle	()V
        //   159: aload 13
        //   161: invokevirtual 72	android/os/Parcel:recycle	()V
        //   164: iload_2
        //   165: ireturn
        //   166: astore_1
        //   167: goto +207 -> 374
        //   170: astore_1
        //   171: goto +203 -> 374
        //   174: aload 14
        //   176: invokevirtual 75	android/os/Parcel:readException	()V
        //   179: aload 14
        //   181: invokevirtual 79	android/os/Parcel:readInt	()I
        //   184: istore_2
        //   185: aload 14
        //   187: invokevirtual 79	android/os/Parcel:readInt	()I
        //   190: istore 4
        //   192: iload 4
        //   194: ifeq +13 -> 207
        //   197: aload 6
        //   199: aload 14
        //   201: invokevirtual 85	android/graphics/Rect:readFromParcel	(Landroid/os/Parcel;)V
        //   204: goto +3 -> 207
        //   207: aload 14
        //   209: astore_1
        //   210: aload_1
        //   211: invokevirtual 79	android/os/Parcel:readInt	()I
        //   214: istore 4
        //   216: iload 4
        //   218: ifeq +12 -> 230
        //   221: aload 7
        //   223: aload_1
        //   224: invokevirtual 85	android/graphics/Rect:readFromParcel	(Landroid/os/Parcel;)V
        //   227: goto +3 -> 230
        //   230: aload_1
        //   231: invokevirtual 79	android/os/Parcel:readInt	()I
        //   234: istore 4
        //   236: iload 4
        //   238: ifeq +12 -> 250
        //   241: aload 8
        //   243: aload_1
        //   244: invokevirtual 85	android/graphics/Rect:readFromParcel	(Landroid/os/Parcel;)V
        //   247: goto +3 -> 250
        //   250: aload_1
        //   251: invokevirtual 79	android/os/Parcel:readInt	()I
        //   254: istore 4
        //   256: iload 4
        //   258: ifeq +12 -> 270
        //   261: aload 9
        //   263: aload_1
        //   264: invokevirtual 85	android/graphics/Rect:readFromParcel	(Landroid/os/Parcel;)V
        //   267: goto +3 -> 270
        //   270: aload_1
        //   271: invokevirtual 79	android/os/Parcel:readInt	()I
        //   274: istore 4
        //   276: iload 4
        //   278: ifeq +12 -> 290
        //   281: aload 10
        //   283: aload_1
        //   284: invokevirtual 88	android/view/DisplayCutout$ParcelableWrapper:readFromParcel	(Landroid/os/Parcel;)V
        //   287: goto +3 -> 290
        //   290: aload_1
        //   291: invokevirtual 79	android/os/Parcel:readInt	()I
        //   294: istore 4
        //   296: iload 4
        //   298: ifeq +12 -> 310
        //   301: aload 11
        //   303: aload_1
        //   304: invokevirtual 91	android/view/InputChannel:readFromParcel	(Landroid/os/Parcel;)V
        //   307: goto +3 -> 310
        //   310: aload_1
        //   311: invokevirtual 79	android/os/Parcel:readInt	()I
        //   314: istore 4
        //   316: iload 4
        //   318: ifeq +16 -> 334
        //   321: aload 12
        //   323: aload_1
        //   324: invokevirtual 94	android/view/InsetsState:readFromParcel	(Landroid/os/Parcel;)V
        //   327: goto +7 -> 334
        //   330: astore_1
        //   331: goto +43 -> 374
        //   334: aload_1
        //   335: invokevirtual 72	android/os/Parcel:recycle	()V
        //   338: aload 13
        //   340: invokevirtual 72	android/os/Parcel:recycle	()V
        //   343: iload_2
        //   344: ireturn
        //   345: astore_1
        //   346: goto +20 -> 366
        //   349: astore_1
        //   350: goto +16 -> 366
        //   353: astore_1
        //   354: goto +12 -> 366
        //   357: astore_1
        //   358: goto +8 -> 366
        //   361: astore_1
        //   362: goto +4 -> 366
        //   365: astore_1
        //   366: goto +8 -> 374
        //   369: astore_1
        //   370: goto +4 -> 374
        //   373: astore_1
        //   374: aload 14
        //   376: invokevirtual 72	android/os/Parcel:recycle	()V
        //   379: aload 13
        //   381: invokevirtual 72	android/os/Parcel:recycle	()V
        //   384: aload_1
        //   385: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	386	0	this	Proxy
        //   0	386	1	paramIWindow	IWindow
        //   0	386	2	paramInt1	int
        //   0	386	3	paramLayoutParams	WindowManager.LayoutParams
        //   0	386	4	paramInt2	int
        //   0	386	5	paramInt3	int
        //   0	386	6	paramRect1	Rect
        //   0	386	7	paramRect2	Rect
        //   0	386	8	paramRect3	Rect
        //   0	386	9	paramRect4	Rect
        //   0	386	10	paramParcelableWrapper	DisplayCutout.ParcelableWrapper
        //   0	386	11	paramInputChannel	InputChannel
        //   0	386	12	paramInsetsState	InsetsState
        //   3	377	13	localParcel1	Parcel
        //   8	367	14	localParcel2	Parcel
        //   27	99	15	localObject	Object
        //   107	3	16	bool	boolean
        // Exception table:
        //   from	to	target	type
        //   21	29	32	finally
        //   56	69	32	finally
        //   125	154	166	finally
        //   114	125	170	finally
        //   321	327	330	finally
        //   301	307	345	finally
        //   310	316	345	finally
        //   281	287	349	finally
        //   290	296	349	finally
        //   261	267	353	finally
        //   270	276	353	finally
        //   241	247	357	finally
        //   250	256	357	finally
        //   221	227	361	finally
        //   230	236	361	finally
        //   197	204	365	finally
        //   210	216	365	finally
        //   174	192	369	finally
        //   10	17	373	finally
        //   39	52	373	finally
        //   72	78	373	finally
        //   78	109	373	finally
      }
      
      /* Error */
      public int addToDisplayWithoutInputChannel(IWindow paramIWindow, int paramInt1, WindowManager.LayoutParams paramLayoutParams, int paramInt2, int paramInt3, Rect paramRect1, Rect paramRect2, InsetsState paramInsetsState)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 9
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 10
        //   10: aload 9
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +14 -> 32
        //   21: aload_1
        //   22: invokeinterface 44 1 0
        //   27: astore 11
        //   29: goto +6 -> 35
        //   32: aconst_null
        //   33: astore 11
        //   35: aload 9
        //   37: aload 11
        //   39: invokevirtual 47	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   42: aload 9
        //   44: iload_2
        //   45: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   48: aload_3
        //   49: ifnull +19 -> 68
        //   52: aload 9
        //   54: iconst_1
        //   55: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   58: aload_3
        //   59: aload 9
        //   61: iconst_0
        //   62: invokevirtual 57	android/view/WindowManager$LayoutParams:writeToParcel	(Landroid/os/Parcel;I)V
        //   65: goto +9 -> 74
        //   68: aload 9
        //   70: iconst_0
        //   71: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   74: aload 9
        //   76: iload 4
        //   78: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   81: aload 9
        //   83: iload 5
        //   85: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   88: aload_0
        //   89: getfield 21	android/view/IWindowSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   92: iconst_2
        //   93: aload 9
        //   95: aload 10
        //   97: iconst_0
        //   98: invokeinterface 63 5 0
        //   103: ifne +43 -> 146
        //   106: invokestatic 67	android/view/IWindowSession$Stub:getDefaultImpl	()Landroid/view/IWindowSession;
        //   109: ifnull +37 -> 146
        //   112: invokestatic 67	android/view/IWindowSession$Stub:getDefaultImpl	()Landroid/view/IWindowSession;
        //   115: aload_1
        //   116: iload_2
        //   117: aload_3
        //   118: iload 4
        //   120: iload 5
        //   122: aload 6
        //   124: aload 7
        //   126: aload 8
        //   128: invokeinterface 99 9 0
        //   133: istore_2
        //   134: aload 10
        //   136: invokevirtual 72	android/os/Parcel:recycle	()V
        //   139: aload 9
        //   141: invokevirtual 72	android/os/Parcel:recycle	()V
        //   144: iload_2
        //   145: ireturn
        //   146: aload 10
        //   148: invokevirtual 75	android/os/Parcel:readException	()V
        //   151: aload 10
        //   153: invokevirtual 79	android/os/Parcel:readInt	()I
        //   156: istore_2
        //   157: aload 10
        //   159: invokevirtual 79	android/os/Parcel:readInt	()I
        //   162: istore 4
        //   164: iload 4
        //   166: ifeq +13 -> 179
        //   169: aload 6
        //   171: aload 10
        //   173: invokevirtual 85	android/graphics/Rect:readFromParcel	(Landroid/os/Parcel;)V
        //   176: goto +3 -> 179
        //   179: aload 10
        //   181: invokevirtual 79	android/os/Parcel:readInt	()I
        //   184: istore 4
        //   186: iload 4
        //   188: ifeq +13 -> 201
        //   191: aload 7
        //   193: aload 10
        //   195: invokevirtual 85	android/graphics/Rect:readFromParcel	(Landroid/os/Parcel;)V
        //   198: goto +3 -> 201
        //   201: aload 10
        //   203: invokevirtual 79	android/os/Parcel:readInt	()I
        //   206: istore 4
        //   208: iload 4
        //   210: ifeq +17 -> 227
        //   213: aload 8
        //   215: aload 10
        //   217: invokevirtual 94	android/view/InsetsState:readFromParcel	(Landroid/os/Parcel;)V
        //   220: goto +7 -> 227
        //   223: astore_1
        //   224: goto +36 -> 260
        //   227: aload 10
        //   229: invokevirtual 72	android/os/Parcel:recycle	()V
        //   232: aload 9
        //   234: invokevirtual 72	android/os/Parcel:recycle	()V
        //   237: iload_2
        //   238: ireturn
        //   239: astore_1
        //   240: goto +20 -> 260
        //   243: astore_1
        //   244: goto +16 -> 260
        //   247: astore_1
        //   248: goto +12 -> 260
        //   251: astore_1
        //   252: goto +8 -> 260
        //   255: astore_1
        //   256: goto +4 -> 260
        //   259: astore_1
        //   260: aload 10
        //   262: invokevirtual 72	android/os/Parcel:recycle	()V
        //   265: aload 9
        //   267: invokevirtual 72	android/os/Parcel:recycle	()V
        //   270: aload_1
        //   271: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	272	0	this	Proxy
        //   0	272	1	paramIWindow	IWindow
        //   0	272	2	paramInt1	int
        //   0	272	3	paramLayoutParams	WindowManager.LayoutParams
        //   0	272	4	paramInt2	int
        //   0	272	5	paramInt3	int
        //   0	272	6	paramRect1	Rect
        //   0	272	7	paramRect2	Rect
        //   0	272	8	paramInsetsState	InsetsState
        //   3	263	9	localParcel1	Parcel
        //   8	253	10	localParcel2	Parcel
        //   27	11	11	localIBinder	IBinder
        // Exception table:
        //   from	to	target	type
        //   213	220	223	finally
        //   191	198	239	finally
        //   201	208	239	finally
        //   169	176	243	finally
        //   179	186	243	finally
        //   81	134	247	finally
        //   146	164	247	finally
        //   74	81	251	finally
        //   42	48	255	finally
        //   52	65	255	finally
        //   68	74	255	finally
        //   10	17	259	finally
        //   21	29	259	finally
        //   35	42	259	finally
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void cancelDragAndDrop(IBinder paramIBinder, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          localParcel1.writeStrongBinder(paramIBinder);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(16, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().cancelDragAndDrop(paramIBinder, paramBoolean);
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
      
      public void dragRecipientEntered(IWindow paramIWindow)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(17, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().dragRecipientEntered(paramIWindow);
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
      
      public void dragRecipientExited(IWindow paramIWindow)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(18, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().dragRecipientExited(paramIWindow);
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
      
      public void finishDrawing(IWindow paramIWindow)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(10, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().finishDrawing(paramIWindow);
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
      
      public void finishMovingTask(IWindow paramIWindow)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(28, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().finishMovingTask(paramIWindow);
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
      
      public void getDisplayFrame(IWindow paramIWindow, Rect paramRect)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(9, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().getDisplayFrame(paramIWindow, paramRect);
            return;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            paramRect.readFromParcel(localParcel2);
          }
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean getInTouchMode()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(12, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            bool = IWindowSession.Stub.getDefaultImpl().getInTouchMode();
            return bool;
          }
          localParcel2.readException();
          int i = localParcel2.readInt();
          if (i != 0) {
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
      
      public String getInterfaceDescriptor()
      {
        return "android.view.IWindowSession";
      }
      
      public IWindowId getWindowId(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          localParcel1.writeStrongBinder(paramIBinder);
          if ((!this.mRemote.transact(25, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            paramIBinder = IWindowSession.Stub.getDefaultImpl().getWindowId(paramIBinder);
            return paramIBinder;
          }
          localParcel2.readException();
          paramIBinder = IWindowId.Stub.asInterface(localParcel2.readStrongBinder());
          return paramIBinder;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void insetsModified(IWindow paramIWindow, InsetsState paramInsetsState)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if (paramInsetsState != null)
          {
            localParcel1.writeInt(1);
            paramInsetsState.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(33, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().insetsModified(paramIWindow, paramInsetsState);
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
      
      public void notifyHasSurfaceView(IBinder paramIBinder, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindowSession");
          localParcel.writeStrongBinder(paramIBinder);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(35, localParcel, null, 1)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().notifyHasSurfaceView(paramIBinder, paramBoolean);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onRectangleOnScreenRequested(IBinder paramIBinder, Rect paramRect)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          localParcel1.writeStrongBinder(paramIBinder);
          if (paramRect != null)
          {
            localParcel1.writeInt(1);
            paramRect.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(24, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().onRectangleOnScreenRequested(paramIBinder, paramRect);
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
      
      public boolean outOfMemory(IWindow paramIWindow)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(6, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            bool = IWindowSession.Stub.getDefaultImpl().outOfMemory(paramIWindow);
            return bool;
          }
          localParcel2.readException();
          int i = localParcel2.readInt();
          if (i != 0) {
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
      
      public IBinder performDrag(IWindow paramIWindow, int paramInt1, SurfaceControl paramSurfaceControl, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, ClipData paramClipData)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          try
          {
            localParcel1.writeInt(paramInt1);
            if (paramSurfaceControl != null)
            {
              localParcel1.writeInt(1);
              paramSurfaceControl.writeToParcel(localParcel1, 0);
            }
            else
            {
              localParcel1.writeInt(0);
            }
            localParcel1.writeInt(paramInt2);
            localParcel1.writeFloat(paramFloat1);
            localParcel1.writeFloat(paramFloat2);
            localParcel1.writeFloat(paramFloat3);
            localParcel1.writeFloat(paramFloat4);
            if (paramClipData != null)
            {
              localParcel1.writeInt(1);
              paramClipData.writeToParcel(localParcel1, 0);
            }
            else
            {
              localParcel1.writeInt(0);
            }
            if ((!this.mRemote.transact(14, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
            {
              paramIWindow = IWindowSession.Stub.getDefaultImpl().performDrag(paramIWindow, paramInt1, paramSurfaceControl, paramInt2, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramClipData);
              localParcel2.recycle();
              localParcel1.recycle();
              return paramIWindow;
            }
            localParcel2.readException();
            paramIWindow = localParcel2.readStrongBinder();
            localParcel2.recycle();
            localParcel1.recycle();
            return paramIWindow;
          }
          finally {}
          localParcel2.recycle();
        }
        finally {}
        localParcel1.recycle();
        throw paramIWindow;
      }
      
      public boolean performHapticFeedback(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          localParcel1.writeInt(paramInt);
          boolean bool = true;
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(13, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            paramBoolean = IWindowSession.Stub.getDefaultImpl().performHapticFeedback(paramInt, paramBoolean);
            return paramBoolean;
          }
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt != 0) {
            paramBoolean = bool;
          } else {
            paramBoolean = false;
          }
          return paramBoolean;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void pokeDrawLock(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          localParcel1.writeStrongBinder(paramIBinder);
          if ((!this.mRemote.transact(26, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().pokeDrawLock(paramIBinder);
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
      
      public void prepareToReplaceWindows(IBinder paramIBinder, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          localParcel1.writeStrongBinder(paramIBinder);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(5, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().prepareToReplaceWindows(paramIBinder, paramBoolean);
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
      
      /* Error */
      public int relayout(IWindow paramIWindow, int paramInt1, WindowManager.LayoutParams paramLayoutParams, int paramInt2, int paramInt3, int paramInt4, int paramInt5, long paramLong, Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5, Rect paramRect6, Rect paramRect7, DisplayCutout.ParcelableWrapper paramParcelableWrapper, MergedConfiguration paramMergedConfiguration, SurfaceControl paramSurfaceControl, InsetsState paramInsetsState)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 21
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 22
        //   10: aload 21
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +18 -> 36
        //   21: aload_1
        //   22: invokeinterface 44 1 0
        //   27: astore 23
        //   29: goto +10 -> 39
        //   32: astore_1
        //   33: goto +472 -> 505
        //   36: aconst_null
        //   37: astore 23
        //   39: aload 21
        //   41: aload 23
        //   43: invokevirtual 47	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   46: aload 21
        //   48: iload_2
        //   49: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   52: aload_3
        //   53: ifnull +19 -> 72
        //   56: aload 21
        //   58: iconst_1
        //   59: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   62: aload_3
        //   63: aload 21
        //   65: iconst_0
        //   66: invokevirtual 57	android/view/WindowManager$LayoutParams:writeToParcel	(Landroid/os/Parcel;I)V
        //   69: goto +9 -> 78
        //   72: aload 21
        //   74: iconst_0
        //   75: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   78: aload 21
        //   80: iload 4
        //   82: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   85: aload 21
        //   87: iload 5
        //   89: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   92: aload 21
        //   94: iload 6
        //   96: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   99: aload 21
        //   101: iload 7
        //   103: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   106: aload 21
        //   108: lload 8
        //   110: invokevirtual 185	android/os/Parcel:writeLong	(J)V
        //   113: aload_0
        //   114: getfield 21	android/view/IWindowSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   117: iconst_4
        //   118: aload 21
        //   120: aload 22
        //   122: iconst_0
        //   123: invokeinterface 63 5 0
        //   128: istore 24
        //   130: iload 24
        //   132: ifne +77 -> 209
        //   135: invokestatic 67	android/view/IWindowSession$Stub:getDefaultImpl	()Landroid/view/IWindowSession;
        //   138: ifnull +71 -> 209
        //   141: invokestatic 67	android/view/IWindowSession$Stub:getDefaultImpl	()Landroid/view/IWindowSession;
        //   144: astore 23
        //   146: aload 23
        //   148: aload_1
        //   149: iload_2
        //   150: aload_3
        //   151: iload 4
        //   153: iload 5
        //   155: iload 6
        //   157: iload 7
        //   159: lload 8
        //   161: aload 10
        //   163: aload 11
        //   165: aload 12
        //   167: aload 13
        //   169: aload 14
        //   171: aload 15
        //   173: aload 16
        //   175: aload 17
        //   177: aload 18
        //   179: aload 19
        //   181: aload 20
        //   183: invokeinterface 187 21 0
        //   188: istore_2
        //   189: aload 22
        //   191: invokevirtual 72	android/os/Parcel:recycle	()V
        //   194: aload 21
        //   196: invokevirtual 72	android/os/Parcel:recycle	()V
        //   199: iload_2
        //   200: ireturn
        //   201: astore_1
        //   202: goto +303 -> 505
        //   205: astore_1
        //   206: goto +299 -> 505
        //   209: aload 22
        //   211: invokevirtual 75	android/os/Parcel:readException	()V
        //   214: aload 22
        //   216: invokevirtual 79	android/os/Parcel:readInt	()I
        //   219: istore_2
        //   220: aload 22
        //   222: invokevirtual 79	android/os/Parcel:readInt	()I
        //   225: istore 4
        //   227: iload 4
        //   229: ifeq +13 -> 242
        //   232: aload 10
        //   234: aload 22
        //   236: invokevirtual 85	android/graphics/Rect:readFromParcel	(Landroid/os/Parcel;)V
        //   239: goto +3 -> 242
        //   242: aload 22
        //   244: astore_1
        //   245: aload_1
        //   246: invokevirtual 79	android/os/Parcel:readInt	()I
        //   249: istore 4
        //   251: iload 4
        //   253: ifeq +12 -> 265
        //   256: aload 11
        //   258: aload_1
        //   259: invokevirtual 85	android/graphics/Rect:readFromParcel	(Landroid/os/Parcel;)V
        //   262: goto +3 -> 265
        //   265: aload_1
        //   266: invokevirtual 79	android/os/Parcel:readInt	()I
        //   269: istore 4
        //   271: iload 4
        //   273: ifeq +12 -> 285
        //   276: aload 12
        //   278: aload_1
        //   279: invokevirtual 85	android/graphics/Rect:readFromParcel	(Landroid/os/Parcel;)V
        //   282: goto +3 -> 285
        //   285: aload_1
        //   286: invokevirtual 79	android/os/Parcel:readInt	()I
        //   289: istore 4
        //   291: iload 4
        //   293: ifeq +12 -> 305
        //   296: aload 13
        //   298: aload_1
        //   299: invokevirtual 85	android/graphics/Rect:readFromParcel	(Landroid/os/Parcel;)V
        //   302: goto +3 -> 305
        //   305: aload_1
        //   306: invokevirtual 79	android/os/Parcel:readInt	()I
        //   309: istore 4
        //   311: iload 4
        //   313: ifeq +12 -> 325
        //   316: aload 14
        //   318: aload_1
        //   319: invokevirtual 85	android/graphics/Rect:readFromParcel	(Landroid/os/Parcel;)V
        //   322: goto +3 -> 325
        //   325: aload_1
        //   326: invokevirtual 79	android/os/Parcel:readInt	()I
        //   329: istore 4
        //   331: iload 4
        //   333: ifeq +12 -> 345
        //   336: aload 15
        //   338: aload_1
        //   339: invokevirtual 85	android/graphics/Rect:readFromParcel	(Landroid/os/Parcel;)V
        //   342: goto +3 -> 345
        //   345: aload_1
        //   346: invokevirtual 79	android/os/Parcel:readInt	()I
        //   349: istore 4
        //   351: iload 4
        //   353: ifeq +12 -> 365
        //   356: aload 16
        //   358: aload_1
        //   359: invokevirtual 85	android/graphics/Rect:readFromParcel	(Landroid/os/Parcel;)V
        //   362: goto +3 -> 365
        //   365: aload_1
        //   366: invokevirtual 79	android/os/Parcel:readInt	()I
        //   369: istore 4
        //   371: iload 4
        //   373: ifeq +12 -> 385
        //   376: aload 17
        //   378: aload_1
        //   379: invokevirtual 88	android/view/DisplayCutout$ParcelableWrapper:readFromParcel	(Landroid/os/Parcel;)V
        //   382: goto +3 -> 385
        //   385: aload_1
        //   386: invokevirtual 79	android/os/Parcel:readInt	()I
        //   389: istore 4
        //   391: iload 4
        //   393: ifeq +12 -> 405
        //   396: aload 18
        //   398: aload_1
        //   399: invokevirtual 190	android/util/MergedConfiguration:readFromParcel	(Landroid/os/Parcel;)V
        //   402: goto +3 -> 405
        //   405: aload_1
        //   406: invokevirtual 79	android/os/Parcel:readInt	()I
        //   409: istore 4
        //   411: iload 4
        //   413: ifeq +12 -> 425
        //   416: aload 19
        //   418: aload_1
        //   419: invokevirtual 191	android/view/SurfaceControl:readFromParcel	(Landroid/os/Parcel;)V
        //   422: goto +3 -> 425
        //   425: aload_1
        //   426: invokevirtual 79	android/os/Parcel:readInt	()I
        //   429: istore 4
        //   431: iload 4
        //   433: ifeq +16 -> 449
        //   436: aload 20
        //   438: aload_1
        //   439: invokevirtual 94	android/view/InsetsState:readFromParcel	(Landroid/os/Parcel;)V
        //   442: goto +7 -> 449
        //   445: astore_1
        //   446: goto +59 -> 505
        //   449: aload_1
        //   450: invokevirtual 72	android/os/Parcel:recycle	()V
        //   453: aload 21
        //   455: invokevirtual 72	android/os/Parcel:recycle	()V
        //   458: iload_2
        //   459: ireturn
        //   460: astore_1
        //   461: goto +44 -> 505
        //   464: astore_1
        //   465: goto +40 -> 505
        //   468: astore_1
        //   469: goto +36 -> 505
        //   472: astore_1
        //   473: goto +32 -> 505
        //   476: astore_1
        //   477: goto +28 -> 505
        //   480: astore_1
        //   481: goto +24 -> 505
        //   484: astore_1
        //   485: goto +20 -> 505
        //   488: astore_1
        //   489: goto +16 -> 505
        //   492: astore_1
        //   493: goto +12 -> 505
        //   496: astore_1
        //   497: goto +8 -> 505
        //   500: astore_1
        //   501: goto +4 -> 505
        //   504: astore_1
        //   505: aload 22
        //   507: invokevirtual 72	android/os/Parcel:recycle	()V
        //   510: aload 21
        //   512: invokevirtual 72	android/os/Parcel:recycle	()V
        //   515: aload_1
        //   516: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	517	0	this	Proxy
        //   0	517	1	paramIWindow	IWindow
        //   0	517	2	paramInt1	int
        //   0	517	3	paramLayoutParams	WindowManager.LayoutParams
        //   0	517	4	paramInt2	int
        //   0	517	5	paramInt3	int
        //   0	517	6	paramInt4	int
        //   0	517	7	paramInt5	int
        //   0	517	8	paramLong	long
        //   0	517	10	paramRect1	Rect
        //   0	517	11	paramRect2	Rect
        //   0	517	12	paramRect3	Rect
        //   0	517	13	paramRect4	Rect
        //   0	517	14	paramRect5	Rect
        //   0	517	15	paramRect6	Rect
        //   0	517	16	paramRect7	Rect
        //   0	517	17	paramParcelableWrapper	DisplayCutout.ParcelableWrapper
        //   0	517	18	paramMergedConfiguration	MergedConfiguration
        //   0	517	19	paramSurfaceControl	SurfaceControl
        //   0	517	20	paramInsetsState	InsetsState
        //   3	508	21	localParcel1	Parcel
        //   8	498	22	localParcel2	Parcel
        //   27	120	23	localObject	Object
        //   128	3	24	bool	boolean
        // Exception table:
        //   from	to	target	type
        //   21	29	32	finally
        //   56	69	32	finally
        //   146	189	201	finally
        //   135	146	205	finally
        //   436	442	445	finally
        //   416	422	460	finally
        //   425	431	460	finally
        //   396	402	464	finally
        //   405	411	464	finally
        //   376	382	468	finally
        //   385	391	468	finally
        //   356	362	472	finally
        //   365	371	472	finally
        //   336	342	476	finally
        //   345	351	476	finally
        //   316	322	480	finally
        //   325	331	480	finally
        //   296	302	484	finally
        //   305	311	484	finally
        //   276	282	488	finally
        //   285	291	488	finally
        //   256	262	492	finally
        //   265	271	492	finally
        //   232	239	496	finally
        //   245	251	496	finally
        //   209	227	500	finally
        //   10	17	504	finally
        //   39	52	504	finally
        //   72	78	504	finally
        //   78	130	504	finally
      }
      
      public void remove(IWindow paramIWindow)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(3, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().remove(paramIWindow);
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
      
      public void reparentDisplayContent(IWindow paramIWindow, SurfaceControl paramSurfaceControl, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if (paramSurfaceControl != null)
          {
            localParcel1.writeInt(1);
            paramSurfaceControl.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(30, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().reparentDisplayContent(paramIWindow, paramSurfaceControl, paramInt);
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
      
      public void reportDropResult(IWindow paramIWindow, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(15, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().reportDropResult(paramIWindow, paramBoolean);
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
      
      public void reportSystemGestureExclusionChanged(IWindow paramIWindow, List<Rect> paramList)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeTypedList(paramList);
          if ((!this.mRemote.transact(34, localParcel, null, 1)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().reportSystemGestureExclusionChanged(paramIWindow, paramList);
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
      public Bundle sendWallpaperCommand(IBinder paramIBinder, String paramString, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 8
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 9
        //   10: aload 8
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 8
        //   19: aload_1
        //   20: invokevirtual 47	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   23: aload 8
        //   25: aload_2
        //   26: invokevirtual 217	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload 8
        //   31: iload_3
        //   32: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   35: aload 8
        //   37: iload 4
        //   39: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   42: aload 8
        //   44: iload 5
        //   46: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   49: iconst_1
        //   50: istore 10
        //   52: aload 6
        //   54: ifnull +20 -> 74
        //   57: aload 8
        //   59: iconst_1
        //   60: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   63: aload 6
        //   65: aload 8
        //   67: iconst_0
        //   68: invokevirtual 220	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   71: goto +9 -> 80
        //   74: aload 8
        //   76: iconst_0
        //   77: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   80: iload 7
        //   82: ifeq +6 -> 88
        //   85: goto +6 -> 91
        //   88: iconst_0
        //   89: istore 10
        //   91: aload 8
        //   93: iload 10
        //   95: invokevirtual 51	android/os/Parcel:writeInt	(I)V
        //   98: aload_0
        //   99: getfield 21	android/view/IWindowSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   102: bipush 22
        //   104: aload 8
        //   106: aload 9
        //   108: iconst_0
        //   109: invokeinterface 63 5 0
        //   114: ifne +41 -> 155
        //   117: invokestatic 67	android/view/IWindowSession$Stub:getDefaultImpl	()Landroid/view/IWindowSession;
        //   120: ifnull +35 -> 155
        //   123: invokestatic 67	android/view/IWindowSession$Stub:getDefaultImpl	()Landroid/view/IWindowSession;
        //   126: aload_1
        //   127: aload_2
        //   128: iload_3
        //   129: iload 4
        //   131: iload 5
        //   133: aload 6
        //   135: iload 7
        //   137: invokeinterface 222 8 0
        //   142: astore_1
        //   143: aload 9
        //   145: invokevirtual 72	android/os/Parcel:recycle	()V
        //   148: aload 8
        //   150: invokevirtual 72	android/os/Parcel:recycle	()V
        //   153: aload_1
        //   154: areturn
        //   155: aload 9
        //   157: invokevirtual 75	android/os/Parcel:readException	()V
        //   160: aload 9
        //   162: invokevirtual 79	android/os/Parcel:readInt	()I
        //   165: ifeq +20 -> 185
        //   168: getstatic 226	android/os/Bundle:CREATOR	Landroid/os/Parcelable$Creator;
        //   171: aload 9
        //   173: invokeinterface 232 2 0
        //   178: checkcast 219	android/os/Bundle
        //   181: astore_1
        //   182: goto +5 -> 187
        //   185: aconst_null
        //   186: astore_1
        //   187: aload 9
        //   189: invokevirtual 72	android/os/Parcel:recycle	()V
        //   192: aload 8
        //   194: invokevirtual 72	android/os/Parcel:recycle	()V
        //   197: aload_1
        //   198: areturn
        //   199: astore_1
        //   200: goto +16 -> 216
        //   203: astore_1
        //   204: goto +12 -> 216
        //   207: astore_1
        //   208: goto +8 -> 216
        //   211: astore_1
        //   212: goto +4 -> 216
        //   215: astore_1
        //   216: aload 9
        //   218: invokevirtual 72	android/os/Parcel:recycle	()V
        //   221: aload 8
        //   223: invokevirtual 72	android/os/Parcel:recycle	()V
        //   226: aload_1
        //   227: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	228	0	this	Proxy
        //   0	228	1	paramIBinder	IBinder
        //   0	228	2	paramString	String
        //   0	228	3	paramInt1	int
        //   0	228	4	paramInt2	int
        //   0	228	5	paramInt3	int
        //   0	228	6	paramBundle	Bundle
        //   0	228	7	paramBoolean	boolean
        //   3	219	8	localParcel1	Parcel
        //   8	209	9	localParcel2	Parcel
        //   50	44	10	i	int
        // Exception table:
        //   from	to	target	type
        //   35	49	199	finally
        //   57	71	199	finally
        //   74	80	199	finally
        //   91	143	199	finally
        //   155	182	199	finally
        //   29	35	203	finally
        //   23	29	207	finally
        //   17	23	211	finally
        //   10	17	215	finally
      }
      
      public void setInTouchMode(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(11, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().setInTouchMode(paramBoolean);
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
      
      public void setInsets(IWindow paramIWindow, int paramInt, Rect paramRect1, Rect paramRect2, Region paramRegion)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          if (paramRect1 != null)
          {
            localParcel1.writeInt(1);
            paramRect1.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if (paramRect2 != null)
          {
            localParcel1.writeInt(1);
            paramRect2.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if (paramRegion != null)
          {
            localParcel1.writeInt(1);
            paramRegion.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(8, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().setInsets(paramIWindow, paramInt, paramRect1, paramRect2, paramRegion);
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
      
      public void setTransparentRegion(IWindow paramIWindow, Region paramRegion)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if (paramRegion != null)
          {
            localParcel1.writeInt(1);
            paramRegion.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(7, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().setTransparentRegion(paramIWindow, paramRegion);
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
      
      public void setWallpaperDisplayOffset(IBinder paramIBinder, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          if ((!this.mRemote.transact(21, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().setWallpaperDisplayOffset(paramIBinder, paramInt1, paramInt2);
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
      
      public void setWallpaperPosition(IBinder paramIBinder, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeFloat(paramFloat1);
          localParcel1.writeFloat(paramFloat2);
          localParcel1.writeFloat(paramFloat3);
          localParcel1.writeFloat(paramFloat4);
          if ((!this.mRemote.transact(19, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().setWallpaperPosition(paramIBinder, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
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
      
      public boolean startMovingTask(IWindow paramIWindow, float paramFloat1, float paramFloat2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeFloat(paramFloat1);
          localParcel1.writeFloat(paramFloat2);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(27, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            bool = IWindowSession.Stub.getDefaultImpl().startMovingTask(paramIWindow, paramFloat1, paramFloat2);
            return bool;
          }
          localParcel2.readException();
          int i = localParcel2.readInt();
          if (i != 0) {
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
      
      public void updateDisplayContentLocation(IWindow paramIWindow, int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          if ((!this.mRemote.transact(31, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().updateDisplayContentLocation(paramIWindow, paramInt1, paramInt2, paramInt3);
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
      
      public void updatePointerIcon(IWindow paramIWindow)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(29, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().updatePointerIcon(paramIWindow);
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
      
      public void updateTapExcludeRegion(IWindow paramIWindow, int paramInt, Region paramRegion)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          IBinder localIBinder;
          if (paramIWindow != null) {
            localIBinder = paramIWindow.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          if (paramRegion != null)
          {
            localParcel1.writeInt(1);
            paramRegion.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(32, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().updateTapExcludeRegion(paramIWindow, paramInt, paramRegion);
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
      
      public void wallpaperCommandComplete(IBinder paramIBinder, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          localParcel1.writeStrongBinder(paramIBinder);
          if (paramBundle != null)
          {
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(23, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().wallpaperCommandComplete(paramIBinder, paramBundle);
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
      
      public void wallpaperOffsetsComplete(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowSession");
          localParcel1.writeStrongBinder(paramIBinder);
          if ((!this.mRemote.transact(20, localParcel1, localParcel2, 0)) && (IWindowSession.Stub.getDefaultImpl() != null))
          {
            IWindowSession.Stub.getDefaultImpl().wallpaperOffsetsComplete(paramIBinder);
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IWindowSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */