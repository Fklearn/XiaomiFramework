package android.view;

import android.annotation.UnsupportedAppUsage;
import android.app.IAssistDataReceiver;
import android.app.IAssistDataReceiver.Stub;
import android.graphics.Bitmap;
import android.graphics.GraphicBuffer;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.IRemoteCallback;
import android.os.IRemoteCallback.Stub;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.text.TextUtils;
import com.android.internal.os.IResultReceiver;
import com.android.internal.os.IResultReceiver.Stub;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.policy.IKeyguardDismissCallback.Stub;
import com.android.internal.policy.IShortcutService;
import com.android.internal.policy.IShortcutService.Stub;
import com.miui.internal.transition.IMiuiFreeFormGestureControlHelper;
import com.miui.internal.transition.IMiuiFreeFormGestureControlHelper.Stub;
import com.miui.internal.transition.IMiuiGestureControlHelper;
import com.miui.internal.transition.IMiuiGestureControlHelper.Stub;

public abstract interface IWindowManager
  extends IInterface
{
  public abstract void addWindowToken(IBinder paramIBinder, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void cancelMiuiThumbnailAnimation(int paramInt)
    throws RemoteException;
  
  public abstract void clearForcedDisplayDensityForUser(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void clearForcedDisplaySize(int paramInt)
    throws RemoteException;
  
  public abstract boolean clearWindowContentFrameStats(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void closeSystemDialogs(String paramString)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void createInputConsumer(IBinder paramIBinder, String paramString, int paramInt, InputChannel paramInputChannel)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract boolean destroyInputConsumer(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void disableKeyguard(IBinder paramIBinder, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void dismissKeyguard(IKeyguardDismissCallback paramIKeyguardDismissCallback, CharSequence paramCharSequence)
    throws RemoteException;
  
  public abstract void dontOverrideDisplayInfo(int paramInt)
    throws RemoteException;
  
  public abstract void enableScreenIfNeeded()
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void endProlongedAnimations()
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void executeAppTransition()
    throws RemoteException;
  
  public abstract void exitKeyguardSecurely(IOnKeyguardExitResult paramIOnKeyguardExitResult)
    throws RemoteException;
  
  public abstract void freezeDisplayRotation(int paramInt1, int paramInt2)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void freezeRotation(int paramInt)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract float getAnimationScale(int paramInt)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract float[] getAnimationScales()
    throws RemoteException;
  
  public abstract int getBaseDisplayDensity(int paramInt)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void getBaseDisplaySize(int paramInt, Point paramPoint)
    throws RemoteException;
  
  public abstract float getCurrentAnimatorScale()
    throws RemoteException;
  
  public abstract int getCurrentFreeFormWindowMode()
    throws RemoteException;
  
  public abstract Region getCurrentImeTouchRegion()
    throws RemoteException;
  
  public abstract int getDefaultDisplayRotation()
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract int getDockedStackSide()
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract int getInitialDisplayDensity(int paramInt)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void getInitialDisplaySize(int paramInt, Point paramPoint)
    throws RemoteException;
  
  public abstract int getNavBarPosition(int paramInt)
    throws RemoteException;
  
  public abstract int getPreferredOptionsPanelGravity(int paramInt)
    throws RemoteException;
  
  public abstract int getRemoveContentMode(int paramInt)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void getStableInsets(int paramInt, Rect paramRect)
    throws RemoteException;
  
  public abstract SurfaceControl getTaskStackContainersSurfaceControl()
    throws RemoteException;
  
  public abstract WindowContentFrameStats getWindowContentFrameStats(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract int getWindowingMode(int paramInt)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract boolean hasNavigationBar(int paramInt)
    throws RemoteException;
  
  public abstract boolean injectInputAfterTransactionsApplied(InputEvent paramInputEvent, int paramInt)
    throws RemoteException;
  
  public abstract boolean isDisplayRotationFrozen(int paramInt)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract boolean isKeyguardLocked()
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract boolean isKeyguardSecure(int paramInt)
    throws RemoteException;
  
  public abstract boolean isRotationFrozen()
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract boolean isSafeModeEnabled()
    throws RemoteException;
  
  public abstract boolean isViewServerRunning()
    throws RemoteException;
  
  public abstract boolean isWindowTraceEnabled()
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void lockNow(Bundle paramBundle)
    throws RemoteException;
  
  public abstract IWindowSession openSession(IWindowSessionCallback paramIWindowSessionCallback)
    throws RemoteException;
  
  public abstract void overrideMiuiAnimSupportWinInset(Rect paramRect)
    throws RemoteException;
  
  public abstract void overridePendingActivityTransitionFromRoundedView(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, GraphicBuffer paramGraphicBuffer, IRemoteCallback paramIRemoteCallback1, IRemoteCallback paramIRemoteCallback2, int paramInt7)
    throws RemoteException;
  
  public abstract void overridePendingAppTransitionLaunchFromHome(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void overridePendingAppTransitionMultiThumbFuture(IAppTransitionAnimationSpecsFuture paramIAppTransitionAnimationSpecsFuture, IRemoteCallback paramIRemoteCallback, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void overridePendingAppTransitionRemote(RemoteAnimationAdapter paramRemoteAnimationAdapter, int paramInt)
    throws RemoteException;
  
  public abstract void prepareAppTransition(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void reenableKeyguard(IBinder paramIBinder, int paramInt)
    throws RemoteException;
  
  public abstract void refreshScreenCaptureDisabled(int paramInt)
    throws RemoteException;
  
  public abstract void registerDisplayFoldListener(IDisplayFoldListener paramIDisplayFoldListener)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void registerDockedStackListener(IDockedStackListener paramIDockedStackListener)
    throws RemoteException;
  
  public abstract void registerMiuiFreeFormGestureControlHelper(IMiuiFreeFormGestureControlHelper paramIMiuiFreeFormGestureControlHelper)
    throws RemoteException;
  
  public abstract void registerMiuiGestureControlHelper(IMiuiGestureControlHelper paramIMiuiGestureControlHelper)
    throws RemoteException;
  
  public abstract void registerPinnedStackListener(int paramInt, IPinnedStackListener paramIPinnedStackListener)
    throws RemoteException;
  
  public abstract void registerShortcutKey(long paramLong, IShortcutService paramIShortcutService)
    throws RemoteException;
  
  public abstract void registerSystemGestureExclusionListener(ISystemGestureExclusionListener paramISystemGestureExclusionListener, int paramInt)
    throws RemoteException;
  
  public abstract void registerUiModeAnimFinishedCallback(IWindowAnimationFinishedCallback paramIWindowAnimationFinishedCallback)
    throws RemoteException;
  
  public abstract boolean registerWallpaperVisibilityListener(IWallpaperVisibilityListener paramIWallpaperVisibilityListener, int paramInt)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void removeRotationWatcher(IRotationWatcher paramIRotationWatcher)
    throws RemoteException;
  
  public abstract void removeWindowToken(IBinder paramIBinder, int paramInt)
    throws RemoteException;
  
  public abstract void requestAppKeyboardShortcuts(IResultReceiver paramIResultReceiver, int paramInt)
    throws RemoteException;
  
  public abstract boolean requestAssistScreenshot(IAssistDataReceiver paramIAssistDataReceiver)
    throws RemoteException;
  
  public abstract void requestUserActivityNotification()
    throws RemoteException;
  
  public abstract Bitmap screenshotWallpaper()
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void setAnimationScale(int paramInt, float paramFloat)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void setAnimationScales(float[] paramArrayOfFloat)
    throws RemoteException;
  
  public abstract void setDockedStackDividerTouchRegion(Rect paramRect)
    throws RemoteException;
  
  public abstract void setEventDispatching(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setForceShowSystemBars(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setForcedDisplayDensityForUser(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void setForcedDisplayScalingMode(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setForcedDisplaySize(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void setForwardedInsets(int paramInt, Insets paramInsets)
    throws RemoteException;
  
  public abstract void setFreeformPackageName(String paramString)
    throws RemoteException;
  
  public abstract void setInTouchMode(boolean paramBoolean)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void setNavBarVirtualKeyHapticFeedbackEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setOverscan(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    throws RemoteException;
  
  public abstract void setPipVisibility(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setRecentsVisibility(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setRemoveContentMode(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setResizeDimLayer(boolean paramBoolean, int paramInt, float paramFloat)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void setShelfHeight(boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void setShouldShowIme(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setShouldShowSystemDecors(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setShouldShowWithInsecureKeyguard(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void setStrictModeVisualIndicatorPreference(String paramString)
    throws RemoteException;
  
  public abstract void setSwitchingUser(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setWindowingMode(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract boolean shouldShowIme(int paramInt)
    throws RemoteException;
  
  public abstract boolean shouldShowSystemDecors(int paramInt)
    throws RemoteException;
  
  public abstract boolean shouldShowWithInsecureKeyguard(int paramInt)
    throws RemoteException;
  
  public abstract void showStrictModeViolation(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void startFreezingScreen(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract boolean startViewServer(int paramInt)
    throws RemoteException;
  
  public abstract void startWindowTrace()
    throws RemoteException;
  
  public abstract void statusBarVisibilityChanged(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void stopFreezingScreen()
    throws RemoteException;
  
  public abstract boolean stopViewServer()
    throws RemoteException;
  
  public abstract void stopWindowTrace()
    throws RemoteException;
  
  public abstract void syncInputTransactions()
    throws RemoteException;
  
  public abstract void thawDisplayRotation(int paramInt)
    throws RemoteException;
  
  @UnsupportedAppUsage
  public abstract void thawRotation()
    throws RemoteException;
  
  public abstract void unregisterDisplayFoldListener(IDisplayFoldListener paramIDisplayFoldListener)
    throws RemoteException;
  
  public abstract void unregisterMiuiFreeFormGestureControlHelper()
    throws RemoteException;
  
  public abstract void unregisterMiuiGestureControlHelper()
    throws RemoteException;
  
  public abstract void unregisterSystemGestureExclusionListener(ISystemGestureExclusionListener paramISystemGestureExclusionListener, int paramInt)
    throws RemoteException;
  
  public abstract void unregisterWallpaperVisibilityListener(IWallpaperVisibilityListener paramIWallpaperVisibilityListener, int paramInt)
    throws RemoteException;
  
  public abstract void updateRotation(boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract int watchRotation(IRotationWatcher paramIRotationWatcher, int paramInt)
    throws RemoteException;
  
  public static class Default
    implements IWindowManager
  {
    public void addWindowToken(IBinder paramIBinder, int paramInt1, int paramInt2)
      throws RemoteException
    {}
    
    public IBinder asBinder()
    {
      return null;
    }
    
    public void cancelMiuiThumbnailAnimation(int paramInt)
      throws RemoteException
    {}
    
    public void clearForcedDisplayDensityForUser(int paramInt1, int paramInt2)
      throws RemoteException
    {}
    
    public void clearForcedDisplaySize(int paramInt)
      throws RemoteException
    {}
    
    public boolean clearWindowContentFrameStats(IBinder paramIBinder)
      throws RemoteException
    {
      return false;
    }
    
    public void closeSystemDialogs(String paramString)
      throws RemoteException
    {}
    
    public void createInputConsumer(IBinder paramIBinder, String paramString, int paramInt, InputChannel paramInputChannel)
      throws RemoteException
    {}
    
    public boolean destroyInputConsumer(String paramString, int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public void disableKeyguard(IBinder paramIBinder, String paramString, int paramInt)
      throws RemoteException
    {}
    
    public void dismissKeyguard(IKeyguardDismissCallback paramIKeyguardDismissCallback, CharSequence paramCharSequence)
      throws RemoteException
    {}
    
    public void dontOverrideDisplayInfo(int paramInt)
      throws RemoteException
    {}
    
    public void enableScreenIfNeeded()
      throws RemoteException
    {}
    
    public void endProlongedAnimations()
      throws RemoteException
    {}
    
    public void executeAppTransition()
      throws RemoteException
    {}
    
    public void exitKeyguardSecurely(IOnKeyguardExitResult paramIOnKeyguardExitResult)
      throws RemoteException
    {}
    
    public void freezeDisplayRotation(int paramInt1, int paramInt2)
      throws RemoteException
    {}
    
    public void freezeRotation(int paramInt)
      throws RemoteException
    {}
    
    public float getAnimationScale(int paramInt)
      throws RemoteException
    {
      return 0.0F;
    }
    
    public float[] getAnimationScales()
      throws RemoteException
    {
      return null;
    }
    
    public int getBaseDisplayDensity(int paramInt)
      throws RemoteException
    {
      return 0;
    }
    
    public void getBaseDisplaySize(int paramInt, Point paramPoint)
      throws RemoteException
    {}
    
    public float getCurrentAnimatorScale()
      throws RemoteException
    {
      return 0.0F;
    }
    
    public int getCurrentFreeFormWindowMode()
      throws RemoteException
    {
      return 0;
    }
    
    public Region getCurrentImeTouchRegion()
      throws RemoteException
    {
      return null;
    }
    
    public int getDefaultDisplayRotation()
      throws RemoteException
    {
      return 0;
    }
    
    public int getDockedStackSide()
      throws RemoteException
    {
      return 0;
    }
    
    public int getInitialDisplayDensity(int paramInt)
      throws RemoteException
    {
      return 0;
    }
    
    public void getInitialDisplaySize(int paramInt, Point paramPoint)
      throws RemoteException
    {}
    
    public int getNavBarPosition(int paramInt)
      throws RemoteException
    {
      return 0;
    }
    
    public int getPreferredOptionsPanelGravity(int paramInt)
      throws RemoteException
    {
      return 0;
    }
    
    public int getRemoveContentMode(int paramInt)
      throws RemoteException
    {
      return 0;
    }
    
    public void getStableInsets(int paramInt, Rect paramRect)
      throws RemoteException
    {}
    
    public SurfaceControl getTaskStackContainersSurfaceControl()
      throws RemoteException
    {
      return null;
    }
    
    public WindowContentFrameStats getWindowContentFrameStats(IBinder paramIBinder)
      throws RemoteException
    {
      return null;
    }
    
    public int getWindowingMode(int paramInt)
      throws RemoteException
    {
      return 0;
    }
    
    public boolean hasNavigationBar(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean injectInputAfterTransactionsApplied(InputEvent paramInputEvent, int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean isDisplayRotationFrozen(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean isKeyguardLocked()
      throws RemoteException
    {
      return false;
    }
    
    public boolean isKeyguardSecure(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean isRotationFrozen()
      throws RemoteException
    {
      return false;
    }
    
    public boolean isSafeModeEnabled()
      throws RemoteException
    {
      return false;
    }
    
    public boolean isViewServerRunning()
      throws RemoteException
    {
      return false;
    }
    
    public boolean isWindowTraceEnabled()
      throws RemoteException
    {
      return false;
    }
    
    public void lockNow(Bundle paramBundle)
      throws RemoteException
    {}
    
    public IWindowSession openSession(IWindowSessionCallback paramIWindowSessionCallback)
      throws RemoteException
    {
      return null;
    }
    
    public void overrideMiuiAnimSupportWinInset(Rect paramRect)
      throws RemoteException
    {}
    
    public void overridePendingActivityTransitionFromRoundedView(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, GraphicBuffer paramGraphicBuffer, IRemoteCallback paramIRemoteCallback1, IRemoteCallback paramIRemoteCallback2, int paramInt7)
      throws RemoteException
    {}
    
    public void overridePendingAppTransitionLaunchFromHome(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
      throws RemoteException
    {}
    
    public void overridePendingAppTransitionMultiThumbFuture(IAppTransitionAnimationSpecsFuture paramIAppTransitionAnimationSpecsFuture, IRemoteCallback paramIRemoteCallback, boolean paramBoolean, int paramInt)
      throws RemoteException
    {}
    
    public void overridePendingAppTransitionRemote(RemoteAnimationAdapter paramRemoteAnimationAdapter, int paramInt)
      throws RemoteException
    {}
    
    public void prepareAppTransition(int paramInt, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void reenableKeyguard(IBinder paramIBinder, int paramInt)
      throws RemoteException
    {}
    
    public void refreshScreenCaptureDisabled(int paramInt)
      throws RemoteException
    {}
    
    public void registerDisplayFoldListener(IDisplayFoldListener paramIDisplayFoldListener)
      throws RemoteException
    {}
    
    public void registerDockedStackListener(IDockedStackListener paramIDockedStackListener)
      throws RemoteException
    {}
    
    public void registerMiuiFreeFormGestureControlHelper(IMiuiFreeFormGestureControlHelper paramIMiuiFreeFormGestureControlHelper)
      throws RemoteException
    {}
    
    public void registerMiuiGestureControlHelper(IMiuiGestureControlHelper paramIMiuiGestureControlHelper)
      throws RemoteException
    {}
    
    public void registerPinnedStackListener(int paramInt, IPinnedStackListener paramIPinnedStackListener)
      throws RemoteException
    {}
    
    public void registerShortcutKey(long paramLong, IShortcutService paramIShortcutService)
      throws RemoteException
    {}
    
    public void registerSystemGestureExclusionListener(ISystemGestureExclusionListener paramISystemGestureExclusionListener, int paramInt)
      throws RemoteException
    {}
    
    public void registerUiModeAnimFinishedCallback(IWindowAnimationFinishedCallback paramIWindowAnimationFinishedCallback)
      throws RemoteException
    {}
    
    public boolean registerWallpaperVisibilityListener(IWallpaperVisibilityListener paramIWallpaperVisibilityListener, int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public void removeRotationWatcher(IRotationWatcher paramIRotationWatcher)
      throws RemoteException
    {}
    
    public void removeWindowToken(IBinder paramIBinder, int paramInt)
      throws RemoteException
    {}
    
    public void requestAppKeyboardShortcuts(IResultReceiver paramIResultReceiver, int paramInt)
      throws RemoteException
    {}
    
    public boolean requestAssistScreenshot(IAssistDataReceiver paramIAssistDataReceiver)
      throws RemoteException
    {
      return false;
    }
    
    public void requestUserActivityNotification()
      throws RemoteException
    {}
    
    public Bitmap screenshotWallpaper()
      throws RemoteException
    {
      return null;
    }
    
    public void setAnimationScale(int paramInt, float paramFloat)
      throws RemoteException
    {}
    
    public void setAnimationScales(float[] paramArrayOfFloat)
      throws RemoteException
    {}
    
    public void setDockedStackDividerTouchRegion(Rect paramRect)
      throws RemoteException
    {}
    
    public void setEventDispatching(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setForceShowSystemBars(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setForcedDisplayDensityForUser(int paramInt1, int paramInt2, int paramInt3)
      throws RemoteException
    {}
    
    public void setForcedDisplayScalingMode(int paramInt1, int paramInt2)
      throws RemoteException
    {}
    
    public void setForcedDisplaySize(int paramInt1, int paramInt2, int paramInt3)
      throws RemoteException
    {}
    
    public void setForwardedInsets(int paramInt, Insets paramInsets)
      throws RemoteException
    {}
    
    public void setFreeformPackageName(String paramString)
      throws RemoteException
    {}
    
    public void setInTouchMode(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setNavBarVirtualKeyHapticFeedbackEnabled(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setOverscan(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
      throws RemoteException
    {}
    
    public void setPipVisibility(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setRecentsVisibility(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setRemoveContentMode(int paramInt1, int paramInt2)
      throws RemoteException
    {}
    
    public void setResizeDimLayer(boolean paramBoolean, int paramInt, float paramFloat)
      throws RemoteException
    {}
    
    public void setShelfHeight(boolean paramBoolean, int paramInt)
      throws RemoteException
    {}
    
    public void setShouldShowIme(int paramInt, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setShouldShowSystemDecors(int paramInt, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setShouldShowWithInsecureKeyguard(int paramInt, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setStrictModeVisualIndicatorPreference(String paramString)
      throws RemoteException
    {}
    
    public void setSwitchingUser(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setWindowingMode(int paramInt1, int paramInt2)
      throws RemoteException
    {}
    
    public boolean shouldShowIme(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean shouldShowSystemDecors(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean shouldShowWithInsecureKeyguard(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public void showStrictModeViolation(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void startFreezingScreen(int paramInt1, int paramInt2)
      throws RemoteException
    {}
    
    public boolean startViewServer(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public void startWindowTrace()
      throws RemoteException
    {}
    
    public void statusBarVisibilityChanged(int paramInt1, int paramInt2)
      throws RemoteException
    {}
    
    public void stopFreezingScreen()
      throws RemoteException
    {}
    
    public boolean stopViewServer()
      throws RemoteException
    {
      return false;
    }
    
    public void stopWindowTrace()
      throws RemoteException
    {}
    
    public void syncInputTransactions()
      throws RemoteException
    {}
    
    public void thawDisplayRotation(int paramInt)
      throws RemoteException
    {}
    
    public void thawRotation()
      throws RemoteException
    {}
    
    public void unregisterDisplayFoldListener(IDisplayFoldListener paramIDisplayFoldListener)
      throws RemoteException
    {}
    
    public void unregisterMiuiFreeFormGestureControlHelper()
      throws RemoteException
    {}
    
    public void unregisterMiuiGestureControlHelper()
      throws RemoteException
    {}
    
    public void unregisterSystemGestureExclusionListener(ISystemGestureExclusionListener paramISystemGestureExclusionListener, int paramInt)
      throws RemoteException
    {}
    
    public void unregisterWallpaperVisibilityListener(IWallpaperVisibilityListener paramIWallpaperVisibilityListener, int paramInt)
      throws RemoteException
    {}
    
    public void updateRotation(boolean paramBoolean1, boolean paramBoolean2)
      throws RemoteException
    {}
    
    public int watchRotation(IRotationWatcher paramIRotationWatcher, int paramInt)
      throws RemoteException
    {
      return 0;
    }
  }
  
  public static abstract class Stub
    extends Binder
    implements IWindowManager
  {
    private static final String DESCRIPTOR = "android.view.IWindowManager";
    static final int TRANSACTION_addWindowToken = 16;
    static final int TRANSACTION_cancelMiuiThumbnailAnimation = 22;
    static final int TRANSACTION_clearForcedDisplayDensityForUser = 12;
    static final int TRANSACTION_clearForcedDisplaySize = 8;
    static final int TRANSACTION_clearWindowContentFrameStats = 74;
    static final int TRANSACTION_closeSystemDialogs = 36;
    static final int TRANSACTION_createInputConsumer = 85;
    static final int TRANSACTION_destroyInputConsumer = 86;
    static final int TRANSACTION_disableKeyguard = 29;
    static final int TRANSACTION_dismissKeyguard = 34;
    static final int TRANSACTION_dontOverrideDisplayInfo = 94;
    static final int TRANSACTION_enableScreenIfNeeded = 73;
    static final int TRANSACTION_endProlongedAnimations = 26;
    static final int TRANSACTION_executeAppTransition = 25;
    static final int TRANSACTION_exitKeyguardSecurely = 31;
    static final int TRANSACTION_freezeDisplayRotation = 54;
    static final int TRANSACTION_freezeRotation = 51;
    static final int TRANSACTION_getAnimationScale = 37;
    static final int TRANSACTION_getAnimationScales = 38;
    static final int TRANSACTION_getBaseDisplayDensity = 10;
    static final int TRANSACTION_getBaseDisplaySize = 6;
    static final int TRANSACTION_getCurrentAnimatorScale = 41;
    static final int TRANSACTION_getCurrentFreeFormWindowMode = 111;
    static final int TRANSACTION_getCurrentImeTouchRegion = 87;
    static final int TRANSACTION_getDefaultDisplayRotation = 47;
    static final int TRANSACTION_getDockedStackSide = 76;
    static final int TRANSACTION_getInitialDisplayDensity = 9;
    static final int TRANSACTION_getInitialDisplaySize = 5;
    static final int TRANSACTION_getNavBarPosition = 70;
    static final int TRANSACTION_getPreferredOptionsPanelGravity = 50;
    static final int TRANSACTION_getRemoveContentMode = 97;
    static final int TRANSACTION_getStableInsets = 82;
    static final int TRANSACTION_getTaskStackContainersSurfaceControl = 112;
    static final int TRANSACTION_getWindowContentFrameStats = 75;
    static final int TRANSACTION_getWindowingMode = 95;
    static final int TRANSACTION_hasNavigationBar = 69;
    static final int TRANSACTION_injectInputAfterTransactionsApplied = 105;
    static final int TRANSACTION_isDisplayRotationFrozen = 56;
    static final int TRANSACTION_isKeyguardLocked = 32;
    static final int TRANSACTION_isKeyguardSecure = 33;
    static final int TRANSACTION_isRotationFrozen = 53;
    static final int TRANSACTION_isSafeModeEnabled = 72;
    static final int TRANSACTION_isViewServerRunning = 3;
    static final int TRANSACTION_isWindowTraceEnabled = 92;
    static final int TRANSACTION_lockNow = 71;
    static final int TRANSACTION_openSession = 4;
    static final int TRANSACTION_overrideMiuiAnimSupportWinInset = 21;
    static final int TRANSACTION_overridePendingActivityTransitionFromRoundedView = 20;
    static final int TRANSACTION_overridePendingAppTransitionLaunchFromHome = 19;
    static final int TRANSACTION_overridePendingAppTransitionMultiThumbFuture = 23;
    static final int TRANSACTION_overridePendingAppTransitionRemote = 24;
    static final int TRANSACTION_prepareAppTransition = 18;
    static final int TRANSACTION_reenableKeyguard = 30;
    static final int TRANSACTION_refreshScreenCaptureDisabled = 45;
    static final int TRANSACTION_registerDisplayFoldListener = 88;
    static final int TRANSACTION_registerDockedStackListener = 78;
    static final int TRANSACTION_registerMiuiFreeFormGestureControlHelper = 109;
    static final int TRANSACTION_registerMiuiGestureControlHelper = 107;
    static final int TRANSACTION_registerPinnedStackListener = 79;
    static final int TRANSACTION_registerShortcutKey = 84;
    static final int TRANSACTION_registerSystemGestureExclusionListener = 60;
    static final int TRANSACTION_registerUiModeAnimFinishedCallback = 113;
    static final int TRANSACTION_registerWallpaperVisibilityListener = 58;
    static final int TRANSACTION_removeRotationWatcher = 49;
    static final int TRANSACTION_removeWindowToken = 17;
    static final int TRANSACTION_requestAppKeyboardShortcuts = 81;
    static final int TRANSACTION_requestAssistScreenshot = 62;
    static final int TRANSACTION_requestUserActivityNotification = 93;
    static final int TRANSACTION_screenshotWallpaper = 57;
    static final int TRANSACTION_setAnimationScale = 39;
    static final int TRANSACTION_setAnimationScales = 40;
    static final int TRANSACTION_setDockedStackDividerTouchRegion = 77;
    static final int TRANSACTION_setEventDispatching = 15;
    static final int TRANSACTION_setForceShowSystemBars = 64;
    static final int TRANSACTION_setForcedDisplayDensityForUser = 11;
    static final int TRANSACTION_setForcedDisplayScalingMode = 13;
    static final int TRANSACTION_setForcedDisplaySize = 7;
    static final int TRANSACTION_setForwardedInsets = 83;
    static final int TRANSACTION_setFreeformPackageName = 114;
    static final int TRANSACTION_setInTouchMode = 42;
    static final int TRANSACTION_setNavBarVirtualKeyHapticFeedbackEnabled = 68;
    static final int TRANSACTION_setOverscan = 14;
    static final int TRANSACTION_setPipVisibility = 66;
    static final int TRANSACTION_setRecentsVisibility = 65;
    static final int TRANSACTION_setRemoveContentMode = 98;
    static final int TRANSACTION_setResizeDimLayer = 80;
    static final int TRANSACTION_setShelfHeight = 67;
    static final int TRANSACTION_setShouldShowIme = 104;
    static final int TRANSACTION_setShouldShowSystemDecors = 102;
    static final int TRANSACTION_setShouldShowWithInsecureKeyguard = 100;
    static final int TRANSACTION_setStrictModeVisualIndicatorPreference = 44;
    static final int TRANSACTION_setSwitchingUser = 35;
    static final int TRANSACTION_setWindowingMode = 96;
    static final int TRANSACTION_shouldShowIme = 103;
    static final int TRANSACTION_shouldShowSystemDecors = 101;
    static final int TRANSACTION_shouldShowWithInsecureKeyguard = 99;
    static final int TRANSACTION_showStrictModeViolation = 43;
    static final int TRANSACTION_startFreezingScreen = 27;
    static final int TRANSACTION_startViewServer = 1;
    static final int TRANSACTION_startWindowTrace = 90;
    static final int TRANSACTION_statusBarVisibilityChanged = 63;
    static final int TRANSACTION_stopFreezingScreen = 28;
    static final int TRANSACTION_stopViewServer = 2;
    static final int TRANSACTION_stopWindowTrace = 91;
    static final int TRANSACTION_syncInputTransactions = 106;
    static final int TRANSACTION_thawDisplayRotation = 55;
    static final int TRANSACTION_thawRotation = 52;
    static final int TRANSACTION_unregisterDisplayFoldListener = 89;
    static final int TRANSACTION_unregisterMiuiFreeFormGestureControlHelper = 110;
    static final int TRANSACTION_unregisterMiuiGestureControlHelper = 108;
    static final int TRANSACTION_unregisterSystemGestureExclusionListener = 61;
    static final int TRANSACTION_unregisterWallpaperVisibilityListener = 59;
    static final int TRANSACTION_updateRotation = 46;
    static final int TRANSACTION_watchRotation = 48;
    
    public Stub()
    {
      attachInterface(this, "android.view.IWindowManager");
    }
    
    public static IWindowManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.view.IWindowManager");
      if ((localIInterface != null) && ((localIInterface instanceof IWindowManager))) {
        return (IWindowManager)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static IWindowManager getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return null;
      case 114: 
        return "setFreeformPackageName";
      case 113: 
        return "registerUiModeAnimFinishedCallback";
      case 112: 
        return "getTaskStackContainersSurfaceControl";
      case 111: 
        return "getCurrentFreeFormWindowMode";
      case 110: 
        return "unregisterMiuiFreeFormGestureControlHelper";
      case 109: 
        return "registerMiuiFreeFormGestureControlHelper";
      case 108: 
        return "unregisterMiuiGestureControlHelper";
      case 107: 
        return "registerMiuiGestureControlHelper";
      case 106: 
        return "syncInputTransactions";
      case 105: 
        return "injectInputAfterTransactionsApplied";
      case 104: 
        return "setShouldShowIme";
      case 103: 
        return "shouldShowIme";
      case 102: 
        return "setShouldShowSystemDecors";
      case 101: 
        return "shouldShowSystemDecors";
      case 100: 
        return "setShouldShowWithInsecureKeyguard";
      case 99: 
        return "shouldShowWithInsecureKeyguard";
      case 98: 
        return "setRemoveContentMode";
      case 97: 
        return "getRemoveContentMode";
      case 96: 
        return "setWindowingMode";
      case 95: 
        return "getWindowingMode";
      case 94: 
        return "dontOverrideDisplayInfo";
      case 93: 
        return "requestUserActivityNotification";
      case 92: 
        return "isWindowTraceEnabled";
      case 91: 
        return "stopWindowTrace";
      case 90: 
        return "startWindowTrace";
      case 89: 
        return "unregisterDisplayFoldListener";
      case 88: 
        return "registerDisplayFoldListener";
      case 87: 
        return "getCurrentImeTouchRegion";
      case 86: 
        return "destroyInputConsumer";
      case 85: 
        return "createInputConsumer";
      case 84: 
        return "registerShortcutKey";
      case 83: 
        return "setForwardedInsets";
      case 82: 
        return "getStableInsets";
      case 81: 
        return "requestAppKeyboardShortcuts";
      case 80: 
        return "setResizeDimLayer";
      case 79: 
        return "registerPinnedStackListener";
      case 78: 
        return "registerDockedStackListener";
      case 77: 
        return "setDockedStackDividerTouchRegion";
      case 76: 
        return "getDockedStackSide";
      case 75: 
        return "getWindowContentFrameStats";
      case 74: 
        return "clearWindowContentFrameStats";
      case 73: 
        return "enableScreenIfNeeded";
      case 72: 
        return "isSafeModeEnabled";
      case 71: 
        return "lockNow";
      case 70: 
        return "getNavBarPosition";
      case 69: 
        return "hasNavigationBar";
      case 68: 
        return "setNavBarVirtualKeyHapticFeedbackEnabled";
      case 67: 
        return "setShelfHeight";
      case 66: 
        return "setPipVisibility";
      case 65: 
        return "setRecentsVisibility";
      case 64: 
        return "setForceShowSystemBars";
      case 63: 
        return "statusBarVisibilityChanged";
      case 62: 
        return "requestAssistScreenshot";
      case 61: 
        return "unregisterSystemGestureExclusionListener";
      case 60: 
        return "registerSystemGestureExclusionListener";
      case 59: 
        return "unregisterWallpaperVisibilityListener";
      case 58: 
        return "registerWallpaperVisibilityListener";
      case 57: 
        return "screenshotWallpaper";
      case 56: 
        return "isDisplayRotationFrozen";
      case 55: 
        return "thawDisplayRotation";
      case 54: 
        return "freezeDisplayRotation";
      case 53: 
        return "isRotationFrozen";
      case 52: 
        return "thawRotation";
      case 51: 
        return "freezeRotation";
      case 50: 
        return "getPreferredOptionsPanelGravity";
      case 49: 
        return "removeRotationWatcher";
      case 48: 
        return "watchRotation";
      case 47: 
        return "getDefaultDisplayRotation";
      case 46: 
        return "updateRotation";
      case 45: 
        return "refreshScreenCaptureDisabled";
      case 44: 
        return "setStrictModeVisualIndicatorPreference";
      case 43: 
        return "showStrictModeViolation";
      case 42: 
        return "setInTouchMode";
      case 41: 
        return "getCurrentAnimatorScale";
      case 40: 
        return "setAnimationScales";
      case 39: 
        return "setAnimationScale";
      case 38: 
        return "getAnimationScales";
      case 37: 
        return "getAnimationScale";
      case 36: 
        return "closeSystemDialogs";
      case 35: 
        return "setSwitchingUser";
      case 34: 
        return "dismissKeyguard";
      case 33: 
        return "isKeyguardSecure";
      case 32: 
        return "isKeyguardLocked";
      case 31: 
        return "exitKeyguardSecurely";
      case 30: 
        return "reenableKeyguard";
      case 29: 
        return "disableKeyguard";
      case 28: 
        return "stopFreezingScreen";
      case 27: 
        return "startFreezingScreen";
      case 26: 
        return "endProlongedAnimations";
      case 25: 
        return "executeAppTransition";
      case 24: 
        return "overridePendingAppTransitionRemote";
      case 23: 
        return "overridePendingAppTransitionMultiThumbFuture";
      case 22: 
        return "cancelMiuiThumbnailAnimation";
      case 21: 
        return "overrideMiuiAnimSupportWinInset";
      case 20: 
        return "overridePendingActivityTransitionFromRoundedView";
      case 19: 
        return "overridePendingAppTransitionLaunchFromHome";
      case 18: 
        return "prepareAppTransition";
      case 17: 
        return "removeWindowToken";
      case 16: 
        return "addWindowToken";
      case 15: 
        return "setEventDispatching";
      case 14: 
        return "setOverscan";
      case 13: 
        return "setForcedDisplayScalingMode";
      case 12: 
        return "clearForcedDisplayDensityForUser";
      case 11: 
        return "setForcedDisplayDensityForUser";
      case 10: 
        return "getBaseDisplayDensity";
      case 9: 
        return "getInitialDisplayDensity";
      case 8: 
        return "clearForcedDisplaySize";
      case 7: 
        return "setForcedDisplaySize";
      case 6: 
        return "getBaseDisplaySize";
      case 5: 
        return "getInitialDisplaySize";
      case 4: 
        return "openSession";
      case 3: 
        return "isViewServerRunning";
      case 2: 
        return "stopViewServer";
      }
      return "startViewServer";
    }
    
    public static boolean setDefaultImpl(IWindowManager paramIWindowManager)
    {
      if ((Proxy.sDefaultImpl == null) && (paramIWindowManager != null))
      {
        Proxy.sDefaultImpl = paramIWindowManager;
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
        boolean bool7 = false;
        boolean bool8 = false;
        boolean bool9 = false;
        boolean bool10 = false;
        boolean bool11 = false;
        boolean bool12 = false;
        boolean bool13 = false;
        boolean bool14 = false;
        boolean bool15 = false;
        boolean bool16 = false;
        Object localObject1;
        Object localObject2;
        float f;
        switch (paramInt1)
        {
        default: 
          return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        case 114: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          setFreeformPackageName(paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        case 113: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          registerUiModeAnimFinishedCallback(IWindowAnimationFinishedCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 112: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramParcel1 = getTaskStackContainersSurfaceControl();
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
        case 111: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = getCurrentFreeFormWindowMode();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 110: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          unregisterMiuiFreeFormGestureControlHelper();
          paramParcel2.writeNoException();
          return true;
        case 109: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          registerMiuiFreeFormGestureControlHelper(IMiuiFreeFormGestureControlHelper.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 108: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          unregisterMiuiGestureControlHelper();
          paramParcel2.writeNoException();
          return true;
        case 107: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          registerMiuiGestureControlHelper(IMiuiGestureControlHelper.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 106: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          syncInputTransactions();
          paramParcel2.writeNoException();
          return true;
        case 105: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          if (paramParcel1.readInt() != 0) {
            localObject1 = (InputEvent)InputEvent.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject1 = null;
          }
          paramInt1 = injectInputAfterTransactionsApplied((InputEvent)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 104: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setShouldShowIme(paramInt1, bool16);
          paramParcel2.writeNoException();
          return true;
        case 103: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = shouldShowIme(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 102: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = paramParcel1.readInt();
          bool16 = bool1;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setShouldShowSystemDecors(paramInt1, bool16);
          paramParcel2.writeNoException();
          return true;
        case 101: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = shouldShowSystemDecors(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 100: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = paramParcel1.readInt();
          bool16 = bool2;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setShouldShowWithInsecureKeyguard(paramInt1, bool16);
          paramParcel2.writeNoException();
          return true;
        case 99: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = shouldShowWithInsecureKeyguard(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 98: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          setRemoveContentMode(paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 97: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = getRemoveContentMode(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 96: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          setWindowingMode(paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 95: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = getWindowingMode(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 94: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          dontOverrideDisplayInfo(paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 93: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          requestUserActivityNotification();
          paramParcel2.writeNoException();
          return true;
        case 92: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = isWindowTraceEnabled();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 91: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          stopWindowTrace();
          paramParcel2.writeNoException();
          return true;
        case 90: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          startWindowTrace();
          paramParcel2.writeNoException();
          return true;
        case 89: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          unregisterDisplayFoldListener(IDisplayFoldListener.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 88: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          registerDisplayFoldListener(IDisplayFoldListener.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 87: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramParcel1 = getCurrentImeTouchRegion();
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
        case 86: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = destroyInputConsumer(paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 85: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          localObject2 = paramParcel1.readStrongBinder();
          localObject1 = paramParcel1.readString();
          paramInt1 = paramParcel1.readInt();
          paramParcel1 = new InputChannel();
          createInputConsumer((IBinder)localObject2, (String)localObject1, paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        case 84: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          registerShortcutKey(paramParcel1.readLong(), IShortcutService.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 83: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (Insets)Insets.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          setForwardedInsets(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        case 82: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = paramParcel1.readInt();
          paramParcel1 = new Rect();
          getStableInsets(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        case 81: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          requestAppKeyboardShortcuts(IResultReceiver.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 80: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          bool16 = bool3;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setResizeDimLayer(bool16, paramParcel1.readInt(), paramParcel1.readFloat());
          paramParcel2.writeNoException();
          return true;
        case 79: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          registerPinnedStackListener(paramParcel1.readInt(), IPinnedStackListener.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 78: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          registerDockedStackListener(IDockedStackListener.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 77: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          setDockedStackDividerTouchRegion(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        case 76: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = getDockedStackSide();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 75: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramParcel1 = getWindowContentFrameStats(paramParcel1.readStrongBinder());
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
        case 74: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = clearWindowContentFrameStats(paramParcel1.readStrongBinder());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 73: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          enableScreenIfNeeded();
          paramParcel2.writeNoException();
          return true;
        case 72: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = isSafeModeEnabled();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 71: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          lockNow(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        case 70: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = getNavBarPosition(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 69: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = hasNavigationBar(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 68: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          bool16 = bool4;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setNavBarVirtualKeyHapticFeedbackEnabled(bool16);
          paramParcel2.writeNoException();
          return true;
        case 67: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          bool16 = bool5;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setShelfHeight(bool16, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 66: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          bool16 = bool6;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setPipVisibility(bool16);
          return true;
        case 65: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          bool16 = bool7;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setRecentsVisibility(bool16);
          return true;
        case 64: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          bool16 = bool8;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setForceShowSystemBars(bool16);
          return true;
        case 63: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          statusBarVisibilityChanged(paramParcel1.readInt(), paramParcel1.readInt());
          return true;
        case 62: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = requestAssistScreenshot(IAssistDataReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 61: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          unregisterSystemGestureExclusionListener(ISystemGestureExclusionListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 60: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          registerSystemGestureExclusionListener(ISystemGestureExclusionListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 59: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          unregisterWallpaperVisibilityListener(IWallpaperVisibilityListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 58: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = registerWallpaperVisibilityListener(IWallpaperVisibilityListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 57: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramParcel1 = screenshotWallpaper();
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
        case 56: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = isDisplayRotationFrozen(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 55: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          thawDisplayRotation(paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 54: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          freezeDisplayRotation(paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 53: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = isRotationFrozen();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 52: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          thawRotation();
          paramParcel2.writeNoException();
          return true;
        case 51: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          freezeRotation(paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 50: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = getPreferredOptionsPanelGravity(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 49: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          removeRotationWatcher(IRotationWatcher.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 48: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = watchRotation(IRotationWatcher.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 47: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = getDefaultDisplayRotation();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 46: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          } else {
            bool16 = false;
          }
          if (paramParcel1.readInt() != 0) {
            bool9 = true;
          }
          updateRotation(bool16, bool9);
          paramParcel2.writeNoException();
          return true;
        case 45: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          refreshScreenCaptureDisabled(paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 44: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          setStrictModeVisualIndicatorPreference(paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        case 43: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          bool16 = bool10;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          showStrictModeViolation(bool16);
          paramParcel2.writeNoException();
          return true;
        case 42: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          bool16 = bool11;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setInTouchMode(bool16);
          paramParcel2.writeNoException();
          return true;
        case 41: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          f = getCurrentAnimatorScale();
          paramParcel2.writeNoException();
          paramParcel2.writeFloat(f);
          return true;
        case 40: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          setAnimationScales(paramParcel1.createFloatArray());
          paramParcel2.writeNoException();
          return true;
        case 39: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          setAnimationScale(paramParcel1.readInt(), paramParcel1.readFloat());
          paramParcel2.writeNoException();
          return true;
        case 38: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramParcel1 = getAnimationScales();
          paramParcel2.writeNoException();
          paramParcel2.writeFloatArray(paramParcel1);
          return true;
        case 37: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          f = getAnimationScale(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeFloat(f);
          return true;
        case 36: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          closeSystemDialogs(paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        case 35: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          bool16 = bool12;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setSwitchingUser(bool16);
          paramParcel2.writeNoException();
          return true;
        case 34: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          localObject1 = IKeyguardDismissCallback.Stub.asInterface(paramParcel1.readStrongBinder());
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          dismissKeyguard((IKeyguardDismissCallback)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        case 33: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = isKeyguardSecure(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 32: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = isKeyguardLocked();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 31: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          exitKeyguardSecurely(IOnKeyguardExitResult.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 30: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          reenableKeyguard(paramParcel1.readStrongBinder(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 29: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          disableKeyguard(paramParcel1.readStrongBinder(), paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 28: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          stopFreezingScreen();
          paramParcel2.writeNoException();
          return true;
        case 27: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          startFreezingScreen(paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 26: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          endProlongedAnimations();
          paramParcel2.writeNoException();
          return true;
        case 25: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          executeAppTransition();
          paramParcel2.writeNoException();
          return true;
        case 24: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          if (paramParcel1.readInt() != 0) {
            localObject1 = (RemoteAnimationAdapter)RemoteAnimationAdapter.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject1 = null;
          }
          overridePendingAppTransitionRemote((RemoteAnimationAdapter)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 23: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          localObject2 = IAppTransitionAnimationSpecsFuture.Stub.asInterface(paramParcel1.readStrongBinder());
          localObject1 = IRemoteCallback.Stub.asInterface(paramParcel1.readStrongBinder());
          bool16 = bool13;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          overridePendingAppTransitionMultiThumbFuture((IAppTransitionAnimationSpecsFuture)localObject2, (IRemoteCallback)localObject1, bool16, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 22: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          cancelMiuiThumbnailAnimation(paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 21: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          overrideMiuiAnimSupportWinInset(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        case 20: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          int i = paramParcel1.readInt();
          int j = paramParcel1.readInt();
          paramInt1 = paramParcel1.readInt();
          int k = paramParcel1.readInt();
          int m = paramParcel1.readInt();
          paramInt2 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            localObject1 = (GraphicBuffer)GraphicBuffer.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject1 = null;
          }
          overridePendingActivityTransitionFromRoundedView(i, j, paramInt1, k, m, paramInt2, (GraphicBuffer)localObject1, IRemoteCallback.Stub.asInterface(paramParcel1.readStrongBinder()), IRemoteCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 19: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          overridePendingAppTransitionLaunchFromHome(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 18: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = paramParcel1.readInt();
          bool16 = bool14;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          prepareAppTransition(paramInt1, bool16);
          paramParcel2.writeNoException();
          return true;
        case 17: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          removeWindowToken(paramParcel1.readStrongBinder(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 16: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          addWindowToken(paramParcel1.readStrongBinder(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 15: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          bool16 = bool15;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setEventDispatching(bool16);
          paramParcel2.writeNoException();
          return true;
        case 14: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          setOverscan(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 13: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          setForcedDisplayScalingMode(paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 12: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          clearForcedDisplayDensityForUser(paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 11: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          setForcedDisplayDensityForUser(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 10: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = getBaseDisplayDensity(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 9: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = getInitialDisplayDensity(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 8: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          clearForcedDisplaySize(paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 7: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          setForcedDisplaySize(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 6: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = paramParcel1.readInt();
          paramParcel1 = new Point();
          getBaseDisplaySize(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        case 5: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = paramParcel1.readInt();
          paramParcel1 = new Point();
          getInitialDisplaySize(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        case 4: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramParcel1 = openSession(IWindowSessionCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          if (paramParcel1 != null) {
            paramParcel1 = paramParcel1.asBinder();
          } else {
            paramParcel1 = null;
          }
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        case 3: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = isViewServerRunning();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 2: 
          paramParcel1.enforceInterface("android.view.IWindowManager");
          paramInt1 = stopViewServer();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
        paramParcel1.enforceInterface("android.view.IWindowManager");
        paramInt1 = startViewServer(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel2.writeString("android.view.IWindowManager");
      return true;
    }
    
    private static class Proxy
      implements IWindowManager
    {
      public static IWindowManager sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addWindowToken(IBinder paramIBinder, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          if ((!this.mRemote.transact(16, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().addWindowToken(paramIBinder, paramInt1, paramInt2);
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
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void cancelMiuiThumbnailAnimation(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(22, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().cancelMiuiThumbnailAnimation(paramInt);
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
      
      public void clearForcedDisplayDensityForUser(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          if ((!this.mRemote.transact(12, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().clearForcedDisplayDensityForUser(paramInt1, paramInt2);
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
      
      public void clearForcedDisplaySize(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(8, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().clearForcedDisplaySize(paramInt);
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
      
      public boolean clearWindowContentFrameStats(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeStrongBinder(paramIBinder);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(74, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().clearWindowContentFrameStats(paramIBinder);
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
      
      public void closeSystemDialogs(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(36, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().closeSystemDialogs(paramString);
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
      
      public void createInputConsumer(IBinder paramIBinder, String paramString, int paramInt, InputChannel paramInputChannel)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(85, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().createInputConsumer(paramIBinder, paramString, paramInt, paramInputChannel);
            return;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            paramInputChannel.readFromParcel(localParcel2);
          }
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean destroyInputConsumer(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(86, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().destroyInputConsumer(paramString, paramInt);
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
      
      public void disableKeyguard(IBinder paramIBinder, String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(29, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().disableKeyguard(paramIBinder, paramString, paramInt);
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
      
      public void dismissKeyguard(IKeyguardDismissCallback paramIKeyguardDismissCallback, CharSequence paramCharSequence)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder;
          if (paramIKeyguardDismissCallback != null) {
            localIBinder = paramIKeyguardDismissCallback.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if (paramCharSequence != null)
          {
            localParcel1.writeInt(1);
            TextUtils.writeToParcel(paramCharSequence, localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(34, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().dismissKeyguard(paramIKeyguardDismissCallback, paramCharSequence);
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
      
      public void dontOverrideDisplayInfo(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(94, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().dontOverrideDisplayInfo(paramInt);
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
      
      public void enableScreenIfNeeded()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if ((!this.mRemote.transact(73, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().enableScreenIfNeeded();
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
      
      public void endProlongedAnimations()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if ((!this.mRemote.transact(26, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().endProlongedAnimations();
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
      
      public void executeAppTransition()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if ((!this.mRemote.transact(25, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().executeAppTransition();
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
      
      public void exitKeyguardSecurely(IOnKeyguardExitResult paramIOnKeyguardExitResult)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder;
          if (paramIOnKeyguardExitResult != null) {
            localIBinder = paramIOnKeyguardExitResult.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(31, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().exitKeyguardSecurely(paramIOnKeyguardExitResult);
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
      
      public void freezeDisplayRotation(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          if ((!this.mRemote.transact(54, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().freezeDisplayRotation(paramInt1, paramInt2);
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
      
      public void freezeRotation(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(51, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().freezeRotation(paramInt);
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
      
      public float getAnimationScale(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(37, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            f = IWindowManager.Stub.getDefaultImpl().getAnimationScale(paramInt);
            return f;
          }
          localParcel2.readException();
          float f = localParcel2.readFloat();
          return f;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public float[] getAnimationScales()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if ((!this.mRemote.transact(38, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            arrayOfFloat = IWindowManager.Stub.getDefaultImpl().getAnimationScales();
            return arrayOfFloat;
          }
          localParcel2.readException();
          float[] arrayOfFloat = localParcel2.createFloatArray();
          return arrayOfFloat;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getBaseDisplayDensity(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(10, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            paramInt = IWindowManager.Stub.getDefaultImpl().getBaseDisplayDensity(paramInt);
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
      
      public void getBaseDisplaySize(int paramInt, Point paramPoint)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(6, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().getBaseDisplaySize(paramInt, paramPoint);
            return;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            paramPoint.readFromParcel(localParcel2);
          }
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public float getCurrentAnimatorScale()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if ((!this.mRemote.transact(41, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            f = IWindowManager.Stub.getDefaultImpl().getCurrentAnimatorScale();
            return f;
          }
          localParcel2.readException();
          float f = localParcel2.readFloat();
          return f;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getCurrentFreeFormWindowMode()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if ((!this.mRemote.transact(111, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            i = IWindowManager.Stub.getDefaultImpl().getCurrentFreeFormWindowMode();
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
      
      public Region getCurrentImeTouchRegion()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          Region localRegion;
          if ((!this.mRemote.transact(87, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            localRegion = IWindowManager.Stub.getDefaultImpl().getCurrentImeTouchRegion();
            return localRegion;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            localRegion = (Region)Region.CREATOR.createFromParcel(localParcel2);
          } else {
            localRegion = null;
          }
          return localRegion;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getDefaultDisplayRotation()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if ((!this.mRemote.transact(47, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            i = IWindowManager.Stub.getDefaultImpl().getDefaultDisplayRotation();
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
      
      public int getDockedStackSide()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if ((!this.mRemote.transact(76, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            i = IWindowManager.Stub.getDefaultImpl().getDockedStackSide();
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
      
      public int getInitialDisplayDensity(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(9, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            paramInt = IWindowManager.Stub.getDefaultImpl().getInitialDisplayDensity(paramInt);
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
      
      public void getInitialDisplaySize(int paramInt, Point paramPoint)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(5, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().getInitialDisplaySize(paramInt, paramPoint);
            return;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            paramPoint.readFromParcel(localParcel2);
          }
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
        return "android.view.IWindowManager";
      }
      
      public int getNavBarPosition(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(70, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            paramInt = IWindowManager.Stub.getDefaultImpl().getNavBarPosition(paramInt);
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
      
      public int getPreferredOptionsPanelGravity(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(50, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            paramInt = IWindowManager.Stub.getDefaultImpl().getPreferredOptionsPanelGravity(paramInt);
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
      
      public int getRemoveContentMode(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(97, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            paramInt = IWindowManager.Stub.getDefaultImpl().getRemoveContentMode(paramInt);
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
      
      public void getStableInsets(int paramInt, Rect paramRect)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(82, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().getStableInsets(paramInt, paramRect);
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
      
      public SurfaceControl getTaskStackContainersSurfaceControl()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          SurfaceControl localSurfaceControl;
          if ((!this.mRemote.transact(112, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            localSurfaceControl = IWindowManager.Stub.getDefaultImpl().getTaskStackContainersSurfaceControl();
            return localSurfaceControl;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            localSurfaceControl = (SurfaceControl)SurfaceControl.CREATOR.createFromParcel(localParcel2);
          } else {
            localSurfaceControl = null;
          }
          return localSurfaceControl;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public WindowContentFrameStats getWindowContentFrameStats(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeStrongBinder(paramIBinder);
          if ((!this.mRemote.transact(75, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            paramIBinder = IWindowManager.Stub.getDefaultImpl().getWindowContentFrameStats(paramIBinder);
            return paramIBinder;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            paramIBinder = (WindowContentFrameStats)WindowContentFrameStats.CREATOR.createFromParcel(localParcel2);
          } else {
            paramIBinder = null;
          }
          return paramIBinder;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getWindowingMode(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(95, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            paramInt = IWindowManager.Stub.getDefaultImpl().getWindowingMode(paramInt);
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
      
      public boolean hasNavigationBar(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(69, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().hasNavigationBar(paramInt);
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
      
      public boolean injectInputAfterTransactionsApplied(InputEvent paramInputEvent, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          boolean bool = true;
          if (paramInputEvent != null)
          {
            localParcel1.writeInt(1);
            paramInputEvent.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(105, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().injectInputAfterTransactionsApplied(paramInputEvent, paramInt);
            return bool;
          }
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          if (paramInt == 0) {
            bool = false;
          }
          return bool;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean isDisplayRotationFrozen(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(56, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().isDisplayRotationFrozen(paramInt);
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
      
      public boolean isKeyguardLocked()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(32, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().isKeyguardLocked();
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
      
      public boolean isKeyguardSecure(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(33, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().isKeyguardSecure(paramInt);
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
      
      public boolean isRotationFrozen()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(53, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().isRotationFrozen();
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
      
      public boolean isSafeModeEnabled()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(72, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().isSafeModeEnabled();
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
      
      public boolean isViewServerRunning()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(3, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().isViewServerRunning();
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
      
      public boolean isWindowTraceEnabled()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(92, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().isWindowTraceEnabled();
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
      
      public void lockNow(Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if (paramBundle != null)
          {
            localParcel1.writeInt(1);
            paramBundle.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(71, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().lockNow(paramBundle);
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
      
      public IWindowSession openSession(IWindowSessionCallback paramIWindowSessionCallback)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder;
          if (paramIWindowSessionCallback != null) {
            localIBinder = paramIWindowSessionCallback.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(4, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            paramIWindowSessionCallback = IWindowManager.Stub.getDefaultImpl().openSession(paramIWindowSessionCallback);
            return paramIWindowSessionCallback;
          }
          localParcel2.readException();
          paramIWindowSessionCallback = IWindowSession.Stub.asInterface(localParcel2.readStrongBinder());
          return paramIWindowSessionCallback;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void overrideMiuiAnimSupportWinInset(Rect paramRect)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if (paramRect != null)
          {
            localParcel1.writeInt(1);
            paramRect.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(21, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().overrideMiuiAnimSupportWinInset(paramRect);
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
      public void overridePendingActivityTransitionFromRoundedView(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, GraphicBuffer paramGraphicBuffer, IRemoteCallback paramIRemoteCallback1, IRemoteCallback paramIRemoteCallback2, int paramInt7)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 11
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 12
        //   10: aload 11
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 11
        //   19: iload_1
        //   20: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   23: aload 11
        //   25: iload_2
        //   26: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   29: aload 11
        //   31: iload_3
        //   32: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   35: aload 11
        //   37: iload 4
        //   39: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   42: aload 11
        //   44: iload 5
        //   46: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   49: aload 11
        //   51: iload 6
        //   53: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   56: aload 7
        //   58: ifnull +20 -> 78
        //   61: aload 11
        //   63: iconst_1
        //   64: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   67: aload 7
        //   69: aload 11
        //   71: iconst_0
        //   72: invokevirtual 310	android/graphics/GraphicBuffer:writeToParcel	(Landroid/os/Parcel;I)V
        //   75: goto +9 -> 84
        //   78: aload 11
        //   80: iconst_0
        //   81: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   84: aconst_null
        //   85: astore 13
        //   87: aload 8
        //   89: ifnull +15 -> 104
        //   92: aload 8
        //   94: invokeinterface 313 1 0
        //   99: astore 14
        //   101: goto +6 -> 107
        //   104: aconst_null
        //   105: astore 14
        //   107: aload 11
        //   109: aload 14
        //   111: invokevirtual 41	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   114: aload 13
        //   116: astore 14
        //   118: aload 9
        //   120: ifnull +12 -> 132
        //   123: aload 9
        //   125: invokeinterface 313 1 0
        //   130: astore 14
        //   132: aload 11
        //   134: aload 14
        //   136: invokevirtual 41	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   139: aload 11
        //   141: iload 10
        //   143: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   146: aload_0
        //   147: getfield 21	android/view/IWindowManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   150: bipush 20
        //   152: aload 11
        //   154: aload 12
        //   156: iconst_0
        //   157: invokeinterface 51 5 0
        //   162: ifne +45 -> 207
        //   165: invokestatic 55	android/view/IWindowManager$Stub:getDefaultImpl	()Landroid/view/IWindowManager;
        //   168: ifnull +39 -> 207
        //   171: invokestatic 55	android/view/IWindowManager$Stub:getDefaultImpl	()Landroid/view/IWindowManager;
        //   174: iload_1
        //   175: iload_2
        //   176: iload_3
        //   177: iload 4
        //   179: iload 5
        //   181: iload 6
        //   183: aload 7
        //   185: aload 8
        //   187: aload 9
        //   189: iload 10
        //   191: invokeinterface 315 11 0
        //   196: aload 12
        //   198: invokevirtual 60	android/os/Parcel:recycle	()V
        //   201: aload 11
        //   203: invokevirtual 60	android/os/Parcel:recycle	()V
        //   206: return
        //   207: aload 12
        //   209: invokevirtual 63	android/os/Parcel:readException	()V
        //   212: aload 12
        //   214: invokevirtual 60	android/os/Parcel:recycle	()V
        //   217: aload 11
        //   219: invokevirtual 60	android/os/Parcel:recycle	()V
        //   222: return
        //   223: astore 7
        //   225: goto +5 -> 230
        //   228: astore 7
        //   230: aload 12
        //   232: invokevirtual 60	android/os/Parcel:recycle	()V
        //   235: aload 11
        //   237: invokevirtual 60	android/os/Parcel:recycle	()V
        //   240: aload 7
        //   242: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	243	0	this	Proxy
        //   0	243	1	paramInt1	int
        //   0	243	2	paramInt2	int
        //   0	243	3	paramInt3	int
        //   0	243	4	paramInt4	int
        //   0	243	5	paramInt5	int
        //   0	243	6	paramInt6	int
        //   0	243	7	paramGraphicBuffer	GraphicBuffer
        //   0	243	8	paramIRemoteCallback1	IRemoteCallback
        //   0	243	9	paramIRemoteCallback2	IRemoteCallback
        //   0	243	10	paramInt7	int
        //   3	233	11	localParcel1	Parcel
        //   8	223	12	localParcel2	Parcel
        //   85	30	13	localObject1	Object
        //   99	36	14	localObject2	Object
        // Exception table:
        //   from	to	target	type
        //   17	56	223	finally
        //   61	75	223	finally
        //   78	84	223	finally
        //   92	101	223	finally
        //   107	114	223	finally
        //   123	132	223	finally
        //   132	196	223	finally
        //   207	212	223	finally
        //   10	17	228	finally
      }
      
      public void overridePendingAppTransitionLaunchFromHome(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeInt(paramInt4);
          localParcel1.writeInt(paramInt5);
          if ((!this.mRemote.transact(19, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().overridePendingAppTransitionLaunchFromHome(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
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
      
      public void overridePendingAppTransitionMultiThumbFuture(IAppTransitionAnimationSpecsFuture paramIAppTransitionAnimationSpecsFuture, IRemoteCallback paramIRemoteCallback, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          Object localObject1 = null;
          if (paramIAppTransitionAnimationSpecsFuture != null) {
            localObject2 = paramIAppTransitionAnimationSpecsFuture.asBinder();
          } else {
            localObject2 = null;
          }
          localParcel1.writeStrongBinder((IBinder)localObject2);
          Object localObject2 = localObject1;
          if (paramIRemoteCallback != null) {
            localObject2 = paramIRemoteCallback.asBinder();
          }
          localParcel1.writeStrongBinder((IBinder)localObject2);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(23, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().overridePendingAppTransitionMultiThumbFuture(paramIAppTransitionAnimationSpecsFuture, paramIRemoteCallback, paramBoolean, paramInt);
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
      
      public void overridePendingAppTransitionRemote(RemoteAnimationAdapter paramRemoteAnimationAdapter, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if (paramRemoteAnimationAdapter != null)
          {
            localParcel1.writeInt(1);
            paramRemoteAnimationAdapter.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(24, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().overridePendingAppTransitionRemote(paramRemoteAnimationAdapter, paramInt);
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
      
      public void prepareAppTransition(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(18, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().prepareAppTransition(paramInt, paramBoolean);
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
      
      public void reenableKeyguard(IBinder paramIBinder, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(30, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().reenableKeyguard(paramIBinder, paramInt);
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
      
      public void refreshScreenCaptureDisabled(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(45, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().refreshScreenCaptureDisabled(paramInt);
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
      
      public void registerDisplayFoldListener(IDisplayFoldListener paramIDisplayFoldListener)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder;
          if (paramIDisplayFoldListener != null) {
            localIBinder = paramIDisplayFoldListener.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(88, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().registerDisplayFoldListener(paramIDisplayFoldListener);
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
      
      public void registerDockedStackListener(IDockedStackListener paramIDockedStackListener)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder;
          if (paramIDockedStackListener != null) {
            localIBinder = paramIDockedStackListener.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(78, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().registerDockedStackListener(paramIDockedStackListener);
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
      
      public void registerMiuiFreeFormGestureControlHelper(IMiuiFreeFormGestureControlHelper paramIMiuiFreeFormGestureControlHelper)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder;
          if (paramIMiuiFreeFormGestureControlHelper != null) {
            localIBinder = paramIMiuiFreeFormGestureControlHelper.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(109, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().registerMiuiFreeFormGestureControlHelper(paramIMiuiFreeFormGestureControlHelper);
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
      
      public void registerMiuiGestureControlHelper(IMiuiGestureControlHelper paramIMiuiGestureControlHelper)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder;
          if (paramIMiuiGestureControlHelper != null) {
            localIBinder = paramIMiuiGestureControlHelper.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(107, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().registerMiuiGestureControlHelper(paramIMiuiGestureControlHelper);
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
      
      public void registerPinnedStackListener(int paramInt, IPinnedStackListener paramIPinnedStackListener)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder;
          if (paramIPinnedStackListener != null) {
            localIBinder = paramIPinnedStackListener.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(79, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().registerPinnedStackListener(paramInt, paramIPinnedStackListener);
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
      
      public void registerShortcutKey(long paramLong, IShortcutService paramIShortcutService)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeLong(paramLong);
          IBinder localIBinder;
          if (paramIShortcutService != null) {
            localIBinder = paramIShortcutService.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(84, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().registerShortcutKey(paramLong, paramIShortcutService);
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
      
      public void registerSystemGestureExclusionListener(ISystemGestureExclusionListener paramISystemGestureExclusionListener, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder;
          if (paramISystemGestureExclusionListener != null) {
            localIBinder = paramISystemGestureExclusionListener.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(60, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().registerSystemGestureExclusionListener(paramISystemGestureExclusionListener, paramInt);
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
      
      public void registerUiModeAnimFinishedCallback(IWindowAnimationFinishedCallback paramIWindowAnimationFinishedCallback)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder;
          if (paramIWindowAnimationFinishedCallback != null) {
            localIBinder = paramIWindowAnimationFinishedCallback.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(113, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().registerUiModeAnimFinishedCallback(paramIWindowAnimationFinishedCallback);
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
      
      public boolean registerWallpaperVisibilityListener(IWallpaperVisibilityListener paramIWallpaperVisibilityListener, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if (paramIWallpaperVisibilityListener != null) {
            localIBinder = paramIWallpaperVisibilityListener.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(58, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().registerWallpaperVisibilityListener(paramIWallpaperVisibilityListener, paramInt);
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
      
      public void removeRotationWatcher(IRotationWatcher paramIRotationWatcher)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder;
          if (paramIRotationWatcher != null) {
            localIBinder = paramIRotationWatcher.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(49, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().removeRotationWatcher(paramIRotationWatcher);
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
      
      public void removeWindowToken(IBinder paramIBinder, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(17, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().removeWindowToken(paramIBinder, paramInt);
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
      
      public void requestAppKeyboardShortcuts(IResultReceiver paramIResultReceiver, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder;
          if (paramIResultReceiver != null) {
            localIBinder = paramIResultReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(81, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().requestAppKeyboardShortcuts(paramIResultReceiver, paramInt);
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
      
      public boolean requestAssistScreenshot(IAssistDataReceiver paramIAssistDataReceiver)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if (paramIAssistDataReceiver != null) {
            localIBinder = paramIAssistDataReceiver.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(62, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().requestAssistScreenshot(paramIAssistDataReceiver);
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
      
      public void requestUserActivityNotification()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if ((!this.mRemote.transact(93, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().requestUserActivityNotification();
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
      
      public Bitmap screenshotWallpaper()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          Bitmap localBitmap;
          if ((!this.mRemote.transact(57, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            localBitmap = IWindowManager.Stub.getDefaultImpl().screenshotWallpaper();
            return localBitmap;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            localBitmap = (Bitmap)Bitmap.CREATOR.createFromParcel(localParcel2);
          } else {
            localBitmap = null;
          }
          return localBitmap;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setAnimationScale(int paramInt, float paramFloat)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          localParcel1.writeFloat(paramFloat);
          if ((!this.mRemote.transact(39, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setAnimationScale(paramInt, paramFloat);
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
      
      public void setAnimationScales(float[] paramArrayOfFloat)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeFloatArray(paramArrayOfFloat);
          if ((!this.mRemote.transact(40, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setAnimationScales(paramArrayOfFloat);
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
      
      public void setDockedStackDividerTouchRegion(Rect paramRect)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if (paramRect != null)
          {
            localParcel1.writeInt(1);
            paramRect.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(77, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setDockedStackDividerTouchRegion(paramRect);
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
      
      public void setEventDispatching(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(15, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setEventDispatching(paramBoolean);
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
      
      public void setForceShowSystemBars(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindowManager");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(64, localParcel, null, 1)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setForceShowSystemBars(paramBoolean);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setForcedDisplayDensityForUser(int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          if ((!this.mRemote.transact(11, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setForcedDisplayDensityForUser(paramInt1, paramInt2, paramInt3);
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
      
      public void setForcedDisplayScalingMode(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          if ((!this.mRemote.transact(13, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setForcedDisplayScalingMode(paramInt1, paramInt2);
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
      
      public void setForcedDisplaySize(int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          if ((!this.mRemote.transact(7, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setForcedDisplaySize(paramInt1, paramInt2, paramInt3);
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
      
      public void setForwardedInsets(int paramInt, Insets paramInsets)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if (paramInsets != null)
          {
            localParcel1.writeInt(1);
            paramInsets.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(83, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setForwardedInsets(paramInt, paramInsets);
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
      
      public void setFreeformPackageName(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(114, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setFreeformPackageName(paramString);
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
      
      public void setInTouchMode(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(42, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setInTouchMode(paramBoolean);
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
      
      public void setNavBarVirtualKeyHapticFeedbackEnabled(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(68, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setNavBarVirtualKeyHapticFeedbackEnabled(paramBoolean);
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
      
      public void setOverscan(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeInt(paramInt4);
          localParcel1.writeInt(paramInt5);
          if ((!this.mRemote.transact(14, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setOverscan(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
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
      
      public void setPipVisibility(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindowManager");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(66, localParcel, null, 1)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setPipVisibility(paramBoolean);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setRecentsVisibility(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindowManager");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel.writeInt(i);
          if ((!this.mRemote.transact(65, localParcel, null, 1)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setRecentsVisibility(paramBoolean);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setRemoveContentMode(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          if ((!this.mRemote.transact(98, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setRemoveContentMode(paramInt1, paramInt2);
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
      
      public void setResizeDimLayer(boolean paramBoolean, int paramInt, float paramFloat)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          localParcel1.writeFloat(paramFloat);
          if ((!this.mRemote.transact(80, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setResizeDimLayer(paramBoolean, paramInt, paramFloat);
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
      
      public void setShelfHeight(boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(67, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setShelfHeight(paramBoolean, paramInt);
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
      
      public void setShouldShowIme(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(104, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setShouldShowIme(paramInt, paramBoolean);
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
      
      public void setShouldShowSystemDecors(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(102, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setShouldShowSystemDecors(paramInt, paramBoolean);
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
      
      public void setShouldShowWithInsecureKeyguard(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(100, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setShouldShowWithInsecureKeyguard(paramInt, paramBoolean);
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
      
      public void setStrictModeVisualIndicatorPreference(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(44, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setStrictModeVisualIndicatorPreference(paramString);
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
      
      public void setSwitchingUser(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(35, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setSwitchingUser(paramBoolean);
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
      
      public void setWindowingMode(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          if ((!this.mRemote.transact(96, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().setWindowingMode(paramInt1, paramInt2);
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
      
      public boolean shouldShowIme(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(103, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().shouldShowIme(paramInt);
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
      
      public boolean shouldShowSystemDecors(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(101, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().shouldShowSystemDecors(paramInt);
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
      
      public boolean shouldShowWithInsecureKeyguard(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(99, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().shouldShowWithInsecureKeyguard(paramInt);
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
      
      public void showStrictModeViolation(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(43, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().showStrictModeViolation(paramBoolean);
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
      
      public void startFreezingScreen(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          if ((!this.mRemote.transact(27, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().startFreezingScreen(paramInt1, paramInt2);
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
      
      public boolean startViewServer(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(1, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().startViewServer(paramInt);
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
      
      public void startWindowTrace()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if ((!this.mRemote.transact(90, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().startWindowTrace();
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
      
      public void statusBarVisibilityChanged(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.view.IWindowManager");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          if ((!this.mRemote.transact(63, localParcel, null, 1)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().statusBarVisibilityChanged(paramInt1, paramInt2);
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void stopFreezingScreen()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if ((!this.mRemote.transact(28, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().stopFreezingScreen();
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
      
      public boolean stopViewServer()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(2, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            bool = IWindowManager.Stub.getDefaultImpl().stopViewServer();
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
      
      public void stopWindowTrace()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if ((!this.mRemote.transact(91, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().stopWindowTrace();
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
      
      public void syncInputTransactions()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if ((!this.mRemote.transact(106, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().syncInputTransactions();
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
      
      public void thawDisplayRotation(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(55, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().thawDisplayRotation(paramInt);
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
      
      public void thawRotation()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if ((!this.mRemote.transact(52, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().thawRotation();
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
      
      public void unregisterDisplayFoldListener(IDisplayFoldListener paramIDisplayFoldListener)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder;
          if (paramIDisplayFoldListener != null) {
            localIBinder = paramIDisplayFoldListener.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(89, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().unregisterDisplayFoldListener(paramIDisplayFoldListener);
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
      
      public void unregisterMiuiFreeFormGestureControlHelper()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if ((!this.mRemote.transact(110, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().unregisterMiuiFreeFormGestureControlHelper();
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
      
      public void unregisterMiuiGestureControlHelper()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          if ((!this.mRemote.transact(108, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().unregisterMiuiGestureControlHelper();
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
      
      public void unregisterSystemGestureExclusionListener(ISystemGestureExclusionListener paramISystemGestureExclusionListener, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder;
          if (paramISystemGestureExclusionListener != null) {
            localIBinder = paramISystemGestureExclusionListener.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(61, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().unregisterSystemGestureExclusionListener(paramISystemGestureExclusionListener, paramInt);
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
      
      public void unregisterWallpaperVisibilityListener(IWallpaperVisibilityListener paramIWallpaperVisibilityListener, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder;
          if (paramIWallpaperVisibilityListener != null) {
            localIBinder = paramIWallpaperVisibilityListener.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(59, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().unregisterWallpaperVisibilityListener(paramIWallpaperVisibilityListener, paramInt);
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
      
      public void updateRotation(boolean paramBoolean1, boolean paramBoolean2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
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
          if ((!this.mRemote.transact(46, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            IWindowManager.Stub.getDefaultImpl().updateRotation(paramBoolean1, paramBoolean2);
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
      
      public int watchRotation(IRotationWatcher paramIRotationWatcher, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.view.IWindowManager");
          IBinder localIBinder;
          if (paramIRotationWatcher != null) {
            localIBinder = paramIRotationWatcher.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(48, localParcel1, localParcel2, 0)) && (IWindowManager.Stub.getDefaultImpl() != null))
          {
            paramInt = IWindowManager.Stub.getDefaultImpl().watchRotation(paramIRotationWatcher, paramInt);
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
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/IWindowManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */