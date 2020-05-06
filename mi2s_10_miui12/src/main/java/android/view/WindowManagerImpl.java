package android.view;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.graphics.Region;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.SeempLog;
import com.android.internal.os.IResultReceiver.Stub;

public final class WindowManagerImpl
  implements WindowManager
{
  private final Context mContext;
  private IBinder mDefaultToken;
  @UnsupportedAppUsage
  private final WindowManagerGlobal mGlobal = WindowManagerGlobal.getInstance();
  private final Window mParentWindow;
  
  public WindowManagerImpl(Context paramContext)
  {
    this(paramContext, null);
  }
  
  private WindowManagerImpl(Context paramContext, Window paramWindow)
  {
    this.mContext = paramContext;
    this.mParentWindow = paramWindow;
  }
  
  private void applyDefaultToken(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((this.mDefaultToken != null) && (this.mParentWindow == null)) {
      if ((paramLayoutParams instanceof WindowManager.LayoutParams))
      {
        paramLayoutParams = (WindowManager.LayoutParams)paramLayoutParams;
        if (paramLayoutParams.token == null) {
          paramLayoutParams.token = this.mDefaultToken;
        }
      }
      else
      {
        throw new IllegalArgumentException("Params must be WindowManager.LayoutParams");
      }
    }
  }
  
  public void addView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    SeempLog.record_vg_layout(383, paramLayoutParams);
    applyDefaultToken(paramLayoutParams);
    this.mGlobal.addView(paramView, paramLayoutParams, this.mContext.getDisplay(), this.mParentWindow);
  }
  
  public WindowManagerImpl createLocalWindowManager(Window paramWindow)
  {
    return new WindowManagerImpl(this.mContext, paramWindow);
  }
  
  public WindowManagerImpl createPresentationWindowManager(Context paramContext)
  {
    return new WindowManagerImpl(paramContext, this.mParentWindow);
  }
  
  public Region getCurrentImeTouchRegion()
  {
    try
    {
      Region localRegion = WindowManagerGlobal.getWindowManagerService().getCurrentImeTouchRegion();
      return localRegion;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public Display getDefaultDisplay()
  {
    return this.mContext.getDisplay();
  }
  
  public void removeView(View paramView)
  {
    this.mGlobal.removeView(paramView, false);
  }
  
  public void removeViewImmediate(View paramView)
  {
    this.mGlobal.removeView(paramView, true);
  }
  
  public void requestAppKeyboardShortcuts(final WindowManager.KeyboardShortcutsReceiver paramKeyboardShortcutsReceiver, int paramInt)
  {
    paramKeyboardShortcutsReceiver = new IResultReceiver.Stub()
    {
      public void send(int paramAnonymousInt, Bundle paramAnonymousBundle)
        throws RemoteException
      {
        paramAnonymousBundle = paramAnonymousBundle.getParcelableArrayList("shortcuts_array");
        paramKeyboardShortcutsReceiver.onKeyboardShortcutsReceived(paramAnonymousBundle);
      }
    };
    try
    {
      WindowManagerGlobal.getWindowManagerService().requestAppKeyboardShortcuts(paramKeyboardShortcutsReceiver, paramInt);
    }
    catch (RemoteException paramKeyboardShortcutsReceiver) {}
  }
  
  public void setDefaultToken(IBinder paramIBinder)
  {
    this.mDefaultToken = paramIBinder;
  }
  
  public void setShouldShowIme(int paramInt, boolean paramBoolean)
  {
    try
    {
      WindowManagerGlobal.getWindowManagerService().setShouldShowIme(paramInt, paramBoolean);
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void setShouldShowSystemDecors(int paramInt, boolean paramBoolean)
  {
    try
    {
      WindowManagerGlobal.getWindowManagerService().setShouldShowSystemDecors(paramInt, paramBoolean);
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void setShouldShowWithInsecureKeyguard(int paramInt, boolean paramBoolean)
  {
    try
    {
      WindowManagerGlobal.getWindowManagerService().setShouldShowWithInsecureKeyguard(paramInt, paramBoolean);
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public boolean shouldShowIme(int paramInt)
  {
    try
    {
      boolean bool = WindowManagerGlobal.getWindowManagerService().shouldShowIme(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public boolean shouldShowSystemDecors(int paramInt)
  {
    try
    {
      boolean bool = WindowManagerGlobal.getWindowManagerService().shouldShowSystemDecors(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public void updateViewLayout(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    SeempLog.record_vg_layout(384, paramLayoutParams);
    applyDefaultToken(paramLayoutParams);
    this.mGlobal.updateViewLayout(paramView, paramLayoutParams);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/WindowManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */