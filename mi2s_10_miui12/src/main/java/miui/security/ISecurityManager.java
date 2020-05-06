package miui.security;

import android.content.Intent;
import android.content.pm.ParceledListSlice;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.android.internal.app.IWakePathCallback;
import com.android.internal.app.IWakePathCallback.Stub;
import java.util.List;

public abstract interface ISecurityManager
  extends IInterface
{
  public abstract int activityResume(Intent paramIntent)
    throws RemoteException;
  
  public abstract void addAccessControlPass(String paramString)
    throws RemoteException;
  
  public abstract void addAccessControlPassForUser(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean addMiuiFirewallSharedUid(int paramInt)
    throws RemoteException;
  
  public abstract boolean areNotificationsEnabledForPackage(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean checkAccessControlPass(String paramString, Intent paramIntent)
    throws RemoteException;
  
  public abstract boolean checkAccessControlPassAsUser(String paramString, Intent paramIntent, int paramInt)
    throws RemoteException;
  
  public abstract boolean checkAccessControlPassword(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract boolean checkAllowStartActivity(String paramString1, String paramString2, Intent paramIntent, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract boolean checkGameBoosterAntimsgPassAsUser(String paramString, Intent paramIntent, int paramInt)
    throws RemoteException;
  
  public abstract boolean checkSmsBlocked(Intent paramIntent)
    throws RemoteException;
  
  public abstract void finishAccessControl(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract String getAccessControlPasswordType(int paramInt)
    throws RemoteException;
  
  public abstract List<String> getAllPrivacyApps(int paramInt)
    throws RemoteException;
  
  public abstract boolean getAppDarkMode(String paramString)
    throws RemoteException;
  
  public abstract boolean getAppDarkModeForUser(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getAppPermissionControlOpen(int paramInt)
    throws RemoteException;
  
  public abstract IBinder getAppRunningControlIBinder()
    throws RemoteException;
  
  public abstract boolean getApplicationAccessControlEnabled(String paramString)
    throws RemoteException;
  
  public abstract boolean getApplicationAccessControlEnabledAsUser(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean getApplicationChildrenControlEnabled(String paramString)
    throws RemoteException;
  
  public abstract boolean getApplicationMaskNotificationEnabledAsUser(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getCurrentUserId()
    throws RemoteException;
  
  public abstract boolean getGameMode(int paramInt)
    throws RemoteException;
  
  public abstract List<String> getIncompatibleAppList()
    throws RemoteException;
  
  public abstract String getPackageNameByPid(int paramInt)
    throws RemoteException;
  
  public abstract int getPermissionFlagsAsUser(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract int getSecondSpaceId()
    throws RemoteException;
  
  public abstract boolean getStickWindowName(String paramString)
    throws RemoteException;
  
  public abstract int getSysAppCracked()
    throws RemoteException;
  
  public abstract IBinder getTopActivity()
    throws RemoteException;
  
  public abstract ParceledListSlice getWakePathCallListLog()
    throws RemoteException;
  
  public abstract ParceledListSlice getWakePathComponents(String paramString)
    throws RemoteException;
  
  public abstract long getWakeUpTime(String paramString)
    throws RemoteException;
  
  public abstract void grantInstallPermission(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void grantRuntimePermission(String paramString)
    throws RemoteException;
  
  public abstract void grantRuntimePermissionAsUser(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract boolean haveAccessControlPassword(int paramInt)
    throws RemoteException;
  
  public abstract boolean isAllowStartService(Intent paramIntent, int paramInt)
    throws RemoteException;
  
  public abstract boolean isAppHide()
    throws RemoteException;
  
  public abstract boolean isAppPrivacyEnabled(String paramString)
    throws RemoteException;
  
  public abstract boolean isFunctionOpen()
    throws RemoteException;
  
  public abstract boolean isPrivacyApp(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean isRestrictedAppNet(String paramString)
    throws RemoteException;
  
  public abstract boolean isValidDevice()
    throws RemoteException;
  
  public abstract void killNativePackageProcesses(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract int moveTaskToStack(int paramInt1, int paramInt2, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean needFinishAccessControl(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void notifyAppsPreInstalled()
    throws RemoteException;
  
  public abstract void offerGoogleBaseCallBack(ISecurityCallback paramISecurityCallback)
    throws RemoteException;
  
  public abstract void pushUpdatePkgsData(List<String> paramList, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void pushWakePathConfirmDialogWhiteList(int paramInt, List<String> paramList)
    throws RemoteException;
  
  public abstract void pushWakePathData(int paramInt1, ParceledListSlice paramParceledListSlice, int paramInt2)
    throws RemoteException;
  
  public abstract void pushWakePathWhiteList(List<String> paramList, int paramInt)
    throws RemoteException;
  
  public abstract boolean putSystemDataStringFile(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract String readSystemDataStringFile(String paramString)
    throws RemoteException;
  
  public abstract void registerWakePathCallback(IWakePathCallback paramIWakePathCallback)
    throws RemoteException;
  
  public abstract void removeAccessControlPass(String paramString)
    throws RemoteException;
  
  public abstract void removeAccessControlPassAsUser(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void removeWakePathData(int paramInt)
    throws RemoteException;
  
  public abstract int resizeTask(int paramInt1, Rect paramRect, int paramInt2)
    throws RemoteException;
  
  public abstract void revokeRuntimePermissionAsUser(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract void revokeRuntimePermissionAsUserNotKill(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract void saveIcon(String paramString, Bitmap paramBitmap)
    throws RemoteException;
  
  public abstract void setAccessControlPassword(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract void setAppDarkModeForUser(String paramString, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract boolean setAppHide(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setAppPermissionControlOpen(int paramInt)
    throws RemoteException;
  
  public abstract void setAppPrivacyStatus(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setApplicationAccessControlEnabled(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setApplicationAccessControlEnabledForUser(String paramString, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void setApplicationChildrenControlEnabled(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setApplicationMaskNotificationEnabledForUser(String paramString, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void setCoreRuntimePermissionEnabled(boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract boolean setCurrentNetworkState(int paramInt)
    throws RemoteException;
  
  public abstract void setGameBoosterIBinder(IBinder paramIBinder, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setIncompatibleAppList(List<String> paramList)
    throws RemoteException;
  
  public abstract boolean setMiuiFirewallRule(String paramString, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void setNotificationsEnabledForPackage(String paramString, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setPrivacyApp(String paramString, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setStickWindowName(String paramString)
    throws RemoteException;
  
  public abstract void setTrackWakePathCallListLogEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setWakeUpTime(String paramString, long paramLong)
    throws RemoteException;
  
  public abstract boolean startInterceptSmsBySender(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract boolean stopInterceptSmsBySender()
    throws RemoteException;
  
  public abstract void updateLauncherPackageNames()
    throws RemoteException;
  
  public abstract void updateLedStatus(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void updatePermissionFlagsAsUser(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void watchGreenGuardProcess()
    throws RemoteException;
  
  public abstract boolean writeAppHideConfig(boolean paramBoolean)
    throws RemoteException;
  
  public static class Default
    implements ISecurityManager
  {
    public int activityResume(Intent paramIntent)
      throws RemoteException
    {
      return 0;
    }
    
    public void addAccessControlPass(String paramString)
      throws RemoteException
    {}
    
    public void addAccessControlPassForUser(String paramString, int paramInt)
      throws RemoteException
    {}
    
    public boolean addMiuiFirewallSharedUid(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean areNotificationsEnabledForPackage(String paramString, int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public IBinder asBinder()
    {
      return null;
    }
    
    public boolean checkAccessControlPass(String paramString, Intent paramIntent)
      throws RemoteException
    {
      return false;
    }
    
    public boolean checkAccessControlPassAsUser(String paramString, Intent paramIntent, int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean checkAccessControlPassword(String paramString1, String paramString2, int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean checkAllowStartActivity(String paramString1, String paramString2, Intent paramIntent, int paramInt1, int paramInt2)
      throws RemoteException
    {
      return false;
    }
    
    public boolean checkGameBoosterAntimsgPassAsUser(String paramString, Intent paramIntent, int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean checkSmsBlocked(Intent paramIntent)
      throws RemoteException
    {
      return false;
    }
    
    public void finishAccessControl(String paramString, int paramInt)
      throws RemoteException
    {}
    
    public String getAccessControlPasswordType(int paramInt)
      throws RemoteException
    {
      return null;
    }
    
    public List<String> getAllPrivacyApps(int paramInt)
      throws RemoteException
    {
      return null;
    }
    
    public boolean getAppDarkMode(String paramString)
      throws RemoteException
    {
      return false;
    }
    
    public boolean getAppDarkModeForUser(String paramString, int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public int getAppPermissionControlOpen(int paramInt)
      throws RemoteException
    {
      return 0;
    }
    
    public IBinder getAppRunningControlIBinder()
      throws RemoteException
    {
      return null;
    }
    
    public boolean getApplicationAccessControlEnabled(String paramString)
      throws RemoteException
    {
      return false;
    }
    
    public boolean getApplicationAccessControlEnabledAsUser(String paramString, int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean getApplicationChildrenControlEnabled(String paramString)
      throws RemoteException
    {
      return false;
    }
    
    public boolean getApplicationMaskNotificationEnabledAsUser(String paramString, int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public int getCurrentUserId()
      throws RemoteException
    {
      return 0;
    }
    
    public boolean getGameMode(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public List<String> getIncompatibleAppList()
      throws RemoteException
    {
      return null;
    }
    
    public String getPackageNameByPid(int paramInt)
      throws RemoteException
    {
      return null;
    }
    
    public int getPermissionFlagsAsUser(String paramString1, String paramString2, int paramInt)
      throws RemoteException
    {
      return 0;
    }
    
    public int getSecondSpaceId()
      throws RemoteException
    {
      return 0;
    }
    
    public boolean getStickWindowName(String paramString)
      throws RemoteException
    {
      return false;
    }
    
    public int getSysAppCracked()
      throws RemoteException
    {
      return 0;
    }
    
    public IBinder getTopActivity()
      throws RemoteException
    {
      return null;
    }
    
    public ParceledListSlice getWakePathCallListLog()
      throws RemoteException
    {
      return null;
    }
    
    public ParceledListSlice getWakePathComponents(String paramString)
      throws RemoteException
    {
      return null;
    }
    
    public long getWakeUpTime(String paramString)
      throws RemoteException
    {
      return 0L;
    }
    
    public void grantInstallPermission(String paramString1, String paramString2)
      throws RemoteException
    {}
    
    public void grantRuntimePermission(String paramString)
      throws RemoteException
    {}
    
    public void grantRuntimePermissionAsUser(String paramString1, String paramString2, int paramInt)
      throws RemoteException
    {}
    
    public boolean haveAccessControlPassword(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean isAllowStartService(Intent paramIntent, int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean isAppHide()
      throws RemoteException
    {
      return false;
    }
    
    public boolean isAppPrivacyEnabled(String paramString)
      throws RemoteException
    {
      return false;
    }
    
    public boolean isFunctionOpen()
      throws RemoteException
    {
      return false;
    }
    
    public boolean isPrivacyApp(String paramString, int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean isRestrictedAppNet(String paramString)
      throws RemoteException
    {
      return false;
    }
    
    public boolean isValidDevice()
      throws RemoteException
    {
      return false;
    }
    
    public void killNativePackageProcesses(int paramInt, String paramString)
      throws RemoteException
    {}
    
    public int moveTaskToStack(int paramInt1, int paramInt2, boolean paramBoolean)
      throws RemoteException
    {
      return 0;
    }
    
    public boolean needFinishAccessControl(IBinder paramIBinder)
      throws RemoteException
    {
      return false;
    }
    
    public void notifyAppsPreInstalled()
      throws RemoteException
    {}
    
    public void offerGoogleBaseCallBack(ISecurityCallback paramISecurityCallback)
      throws RemoteException
    {}
    
    public void pushUpdatePkgsData(List<String> paramList, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void pushWakePathConfirmDialogWhiteList(int paramInt, List<String> paramList)
      throws RemoteException
    {}
    
    public void pushWakePathData(int paramInt1, ParceledListSlice paramParceledListSlice, int paramInt2)
      throws RemoteException
    {}
    
    public void pushWakePathWhiteList(List<String> paramList, int paramInt)
      throws RemoteException
    {}
    
    public boolean putSystemDataStringFile(String paramString1, String paramString2)
      throws RemoteException
    {
      return false;
    }
    
    public String readSystemDataStringFile(String paramString)
      throws RemoteException
    {
      return null;
    }
    
    public void registerWakePathCallback(IWakePathCallback paramIWakePathCallback)
      throws RemoteException
    {}
    
    public void removeAccessControlPass(String paramString)
      throws RemoteException
    {}
    
    public void removeAccessControlPassAsUser(String paramString, int paramInt)
      throws RemoteException
    {}
    
    public void removeWakePathData(int paramInt)
      throws RemoteException
    {}
    
    public int resizeTask(int paramInt1, Rect paramRect, int paramInt2)
      throws RemoteException
    {
      return 0;
    }
    
    public void revokeRuntimePermissionAsUser(String paramString1, String paramString2, int paramInt)
      throws RemoteException
    {}
    
    public void revokeRuntimePermissionAsUserNotKill(String paramString1, String paramString2, int paramInt)
      throws RemoteException
    {}
    
    public void saveIcon(String paramString, Bitmap paramBitmap)
      throws RemoteException
    {}
    
    public void setAccessControlPassword(String paramString1, String paramString2, int paramInt)
      throws RemoteException
    {}
    
    public void setAppDarkModeForUser(String paramString, boolean paramBoolean, int paramInt)
      throws RemoteException
    {}
    
    public boolean setAppHide(boolean paramBoolean)
      throws RemoteException
    {
      return false;
    }
    
    public void setAppPermissionControlOpen(int paramInt)
      throws RemoteException
    {}
    
    public void setAppPrivacyStatus(String paramString, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setApplicationAccessControlEnabled(String paramString, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setApplicationAccessControlEnabledForUser(String paramString, boolean paramBoolean, int paramInt)
      throws RemoteException
    {}
    
    public void setApplicationChildrenControlEnabled(String paramString, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setApplicationMaskNotificationEnabledForUser(String paramString, boolean paramBoolean, int paramInt)
      throws RemoteException
    {}
    
    public void setCoreRuntimePermissionEnabled(boolean paramBoolean, int paramInt)
      throws RemoteException
    {}
    
    public boolean setCurrentNetworkState(int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public void setGameBoosterIBinder(IBinder paramIBinder, int paramInt, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setIncompatibleAppList(List<String> paramList)
      throws RemoteException
    {}
    
    public boolean setMiuiFirewallRule(String paramString, int paramInt1, int paramInt2, int paramInt3)
      throws RemoteException
    {
      return false;
    }
    
    public void setNotificationsEnabledForPackage(String paramString, int paramInt, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setPrivacyApp(String paramString, int paramInt, boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setStickWindowName(String paramString)
      throws RemoteException
    {}
    
    public void setTrackWakePathCallListLogEnabled(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void setWakeUpTime(String paramString, long paramLong)
      throws RemoteException
    {}
    
    public boolean startInterceptSmsBySender(String paramString1, String paramString2, int paramInt)
      throws RemoteException
    {
      return false;
    }
    
    public boolean stopInterceptSmsBySender()
      throws RemoteException
    {
      return false;
    }
    
    public void updateLauncherPackageNames()
      throws RemoteException
    {}
    
    public void updateLedStatus(boolean paramBoolean)
      throws RemoteException
    {}
    
    public void updatePermissionFlagsAsUser(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3)
      throws RemoteException
    {}
    
    public void watchGreenGuardProcess()
      throws RemoteException
    {}
    
    public boolean writeAppHideConfig(boolean paramBoolean)
      throws RemoteException
    {
      return false;
    }
  }
  
  public static abstract class Stub
    extends Binder
    implements ISecurityManager
  {
    private static final String DESCRIPTOR = "miui.security.ISecurityManager";
    static final int TRANSACTION_activityResume = 27;
    static final int TRANSACTION_addAccessControlPass = 6;
    static final int TRANSACTION_addAccessControlPassForUser = 38;
    static final int TRANSACTION_addMiuiFirewallSharedUid = 46;
    static final int TRANSACTION_areNotificationsEnabledForPackage = 56;
    static final int TRANSACTION_checkAccessControlPass = 8;
    static final int TRANSACTION_checkAccessControlPassAsUser = 31;
    static final int TRANSACTION_checkAccessControlPassword = 66;
    static final int TRANSACTION_checkAllowStartActivity = 34;
    static final int TRANSACTION_checkGameBoosterAntimsgPassAsUser = 62;
    static final int TRANSACTION_checkSmsBlocked = 3;
    static final int TRANSACTION_finishAccessControl = 26;
    static final int TRANSACTION_getAccessControlPasswordType = 68;
    static final int TRANSACTION_getAllPrivacyApps = 83;
    static final int TRANSACTION_getAppDarkMode = 40;
    static final int TRANSACTION_getAppDarkModeForUser = 41;
    static final int TRANSACTION_getAppPermissionControlOpen = 21;
    static final int TRANSACTION_getAppRunningControlIBinder = 73;
    static final int TRANSACTION_getApplicationAccessControlEnabled = 9;
    static final int TRANSACTION_getApplicationAccessControlEnabledAsUser = 32;
    static final int TRANSACTION_getApplicationChildrenControlEnabled = 11;
    static final int TRANSACTION_getApplicationMaskNotificationEnabledAsUser = 54;
    static final int TRANSACTION_getCurrentUserId = 30;
    static final int TRANSACTION_getGameMode = 64;
    static final int TRANSACTION_getIncompatibleAppList = 50;
    static final int TRANSACTION_getPackageNameByPid = 2;
    static final int TRANSACTION_getPermissionFlagsAsUser = 88;
    static final int TRANSACTION_getSecondSpaceId = 75;
    static final int TRANSACTION_getStickWindowName = 79;
    static final int TRANSACTION_getSysAppCracked = 35;
    static final int TRANSACTION_getTopActivity = 72;
    static final int TRANSACTION_getWakePathCallListLog = 20;
    static final int TRANSACTION_getWakePathComponents = 51;
    static final int TRANSACTION_getWakeUpTime = 14;
    static final int TRANSACTION_grantInstallPermission = 36;
    static final int TRANSACTION_grantRuntimePermission = 29;
    static final int TRANSACTION_grantRuntimePermissionAsUser = 85;
    static final int TRANSACTION_haveAccessControlPassword = 67;
    static final int TRANSACTION_isAllowStartService = 71;
    static final int TRANSACTION_isAppHide = 58;
    static final int TRANSACTION_isAppPrivacyEnabled = 70;
    static final int TRANSACTION_isFunctionOpen = 59;
    static final int TRANSACTION_isPrivacyApp = 82;
    static final int TRANSACTION_isRestrictedAppNet = 43;
    static final int TRANSACTION_isValidDevice = 61;
    static final int TRANSACTION_killNativePackageProcesses = 1;
    static final int TRANSACTION_moveTaskToStack = 76;
    static final int TRANSACTION_needFinishAccessControl = 25;
    static final int TRANSACTION_notifyAppsPreInstalled = 53;
    static final int TRANSACTION_offerGoogleBaseCallBack = 52;
    static final int TRANSACTION_pushUpdatePkgsData = 80;
    static final int TRANSACTION_pushWakePathConfirmDialogWhiteList = 37;
    static final int TRANSACTION_pushWakePathData = 17;
    static final int TRANSACTION_pushWakePathWhiteList = 18;
    static final int TRANSACTION_putSystemDataStringFile = 15;
    static final int TRANSACTION_readSystemDataStringFile = 16;
    static final int TRANSACTION_registerWakePathCallback = 23;
    static final int TRANSACTION_removeAccessControlPass = 7;
    static final int TRANSACTION_removeAccessControlPassAsUser = 24;
    static final int TRANSACTION_removeWakePathData = 33;
    static final int TRANSACTION_resizeTask = 77;
    static final int TRANSACTION_revokeRuntimePermissionAsUser = 86;
    static final int TRANSACTION_revokeRuntimePermissionAsUserNotKill = 87;
    static final int TRANSACTION_saveIcon = 45;
    static final int TRANSACTION_setAccessControlPassword = 65;
    static final int TRANSACTION_setAppDarkModeForUser = 42;
    static final int TRANSACTION_setAppHide = 60;
    static final int TRANSACTION_setAppPermissionControlOpen = 22;
    static final int TRANSACTION_setAppPrivacyStatus = 69;
    static final int TRANSACTION_setApplicationAccessControlEnabled = 10;
    static final int TRANSACTION_setApplicationAccessControlEnabledForUser = 39;
    static final int TRANSACTION_setApplicationChildrenControlEnabled = 12;
    static final int TRANSACTION_setApplicationMaskNotificationEnabledForUser = 55;
    static final int TRANSACTION_setCoreRuntimePermissionEnabled = 28;
    static final int TRANSACTION_setCurrentNetworkState = 48;
    static final int TRANSACTION_setGameBoosterIBinder = 63;
    static final int TRANSACTION_setIncompatibleAppList = 49;
    static final int TRANSACTION_setMiuiFirewallRule = 47;
    static final int TRANSACTION_setNotificationsEnabledForPackage = 57;
    static final int TRANSACTION_setPrivacyApp = 81;
    static final int TRANSACTION_setStickWindowName = 78;
    static final int TRANSACTION_setTrackWakePathCallListLogEnabled = 19;
    static final int TRANSACTION_setWakeUpTime = 13;
    static final int TRANSACTION_startInterceptSmsBySender = 4;
    static final int TRANSACTION_stopInterceptSmsBySender = 5;
    static final int TRANSACTION_updateLauncherPackageNames = 84;
    static final int TRANSACTION_updateLedStatus = 90;
    static final int TRANSACTION_updatePermissionFlagsAsUser = 89;
    static final int TRANSACTION_watchGreenGuardProcess = 74;
    static final int TRANSACTION_writeAppHideConfig = 44;
    
    public Stub()
    {
      attachInterface(this, "miui.security.ISecurityManager");
    }
    
    public static ISecurityManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("miui.security.ISecurityManager");
      if ((localIInterface != null) && ((localIInterface instanceof ISecurityManager))) {
        return (ISecurityManager)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public static ISecurityManager getDefaultImpl()
    {
      return Proxy.sDefaultImpl;
    }
    
    public static String getDefaultTransactionName(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return null;
      case 90: 
        return "updateLedStatus";
      case 89: 
        return "updatePermissionFlagsAsUser";
      case 88: 
        return "getPermissionFlagsAsUser";
      case 87: 
        return "revokeRuntimePermissionAsUserNotKill";
      case 86: 
        return "revokeRuntimePermissionAsUser";
      case 85: 
        return "grantRuntimePermissionAsUser";
      case 84: 
        return "updateLauncherPackageNames";
      case 83: 
        return "getAllPrivacyApps";
      case 82: 
        return "isPrivacyApp";
      case 81: 
        return "setPrivacyApp";
      case 80: 
        return "pushUpdatePkgsData";
      case 79: 
        return "getStickWindowName";
      case 78: 
        return "setStickWindowName";
      case 77: 
        return "resizeTask";
      case 76: 
        return "moveTaskToStack";
      case 75: 
        return "getSecondSpaceId";
      case 74: 
        return "watchGreenGuardProcess";
      case 73: 
        return "getAppRunningControlIBinder";
      case 72: 
        return "getTopActivity";
      case 71: 
        return "isAllowStartService";
      case 70: 
        return "isAppPrivacyEnabled";
      case 69: 
        return "setAppPrivacyStatus";
      case 68: 
        return "getAccessControlPasswordType";
      case 67: 
        return "haveAccessControlPassword";
      case 66: 
        return "checkAccessControlPassword";
      case 65: 
        return "setAccessControlPassword";
      case 64: 
        return "getGameMode";
      case 63: 
        return "setGameBoosterIBinder";
      case 62: 
        return "checkGameBoosterAntimsgPassAsUser";
      case 61: 
        return "isValidDevice";
      case 60: 
        return "setAppHide";
      case 59: 
        return "isFunctionOpen";
      case 58: 
        return "isAppHide";
      case 57: 
        return "setNotificationsEnabledForPackage";
      case 56: 
        return "areNotificationsEnabledForPackage";
      case 55: 
        return "setApplicationMaskNotificationEnabledForUser";
      case 54: 
        return "getApplicationMaskNotificationEnabledAsUser";
      case 53: 
        return "notifyAppsPreInstalled";
      case 52: 
        return "offerGoogleBaseCallBack";
      case 51: 
        return "getWakePathComponents";
      case 50: 
        return "getIncompatibleAppList";
      case 49: 
        return "setIncompatibleAppList";
      case 48: 
        return "setCurrentNetworkState";
      case 47: 
        return "setMiuiFirewallRule";
      case 46: 
        return "addMiuiFirewallSharedUid";
      case 45: 
        return "saveIcon";
      case 44: 
        return "writeAppHideConfig";
      case 43: 
        return "isRestrictedAppNet";
      case 42: 
        return "setAppDarkModeForUser";
      case 41: 
        return "getAppDarkModeForUser";
      case 40: 
        return "getAppDarkMode";
      case 39: 
        return "setApplicationAccessControlEnabledForUser";
      case 38: 
        return "addAccessControlPassForUser";
      case 37: 
        return "pushWakePathConfirmDialogWhiteList";
      case 36: 
        return "grantInstallPermission";
      case 35: 
        return "getSysAppCracked";
      case 34: 
        return "checkAllowStartActivity";
      case 33: 
        return "removeWakePathData";
      case 32: 
        return "getApplicationAccessControlEnabledAsUser";
      case 31: 
        return "checkAccessControlPassAsUser";
      case 30: 
        return "getCurrentUserId";
      case 29: 
        return "grantRuntimePermission";
      case 28: 
        return "setCoreRuntimePermissionEnabled";
      case 27: 
        return "activityResume";
      case 26: 
        return "finishAccessControl";
      case 25: 
        return "needFinishAccessControl";
      case 24: 
        return "removeAccessControlPassAsUser";
      case 23: 
        return "registerWakePathCallback";
      case 22: 
        return "setAppPermissionControlOpen";
      case 21: 
        return "getAppPermissionControlOpen";
      case 20: 
        return "getWakePathCallListLog";
      case 19: 
        return "setTrackWakePathCallListLogEnabled";
      case 18: 
        return "pushWakePathWhiteList";
      case 17: 
        return "pushWakePathData";
      case 16: 
        return "readSystemDataStringFile";
      case 15: 
        return "putSystemDataStringFile";
      case 14: 
        return "getWakeUpTime";
      case 13: 
        return "setWakeUpTime";
      case 12: 
        return "setApplicationChildrenControlEnabled";
      case 11: 
        return "getApplicationChildrenControlEnabled";
      case 10: 
        return "setApplicationAccessControlEnabled";
      case 9: 
        return "getApplicationAccessControlEnabled";
      case 8: 
        return "checkAccessControlPass";
      case 7: 
        return "removeAccessControlPass";
      case 6: 
        return "addAccessControlPass";
      case 5: 
        return "stopInterceptSmsBySender";
      case 4: 
        return "startInterceptSmsBySender";
      case 3: 
        return "checkSmsBlocked";
      case 2: 
        return "getPackageNameByPid";
      }
      return "killNativePackageProcesses";
    }
    
    public static boolean setDefaultImpl(ISecurityManager paramISecurityManager)
    {
      if ((Proxy.sDefaultImpl == null) && (paramISecurityManager != null))
      {
        Proxy.sDefaultImpl = paramISecurityManager;
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
        Object localObject;
        String str1;
        switch (paramInt1)
        {
        default: 
          return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
        case 90: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          updateLedStatus(bool16);
          paramParcel2.writeNoException();
          return true;
        case 89: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          updatePermissionFlagsAsUser(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 88: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = getPermissionFlagsAsUser(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 87: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          revokeRuntimePermissionAsUserNotKill(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 86: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          revokeRuntimePermissionAsUser(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 85: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          grantRuntimePermissionAsUser(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 84: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          updateLauncherPackageNames();
          paramParcel2.writeNoException();
          return true;
        case 83: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramParcel1 = getAllPrivacyApps(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeStringList(paramParcel1);
          return true;
        case 82: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = isPrivacyApp(paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 81: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          localObject = paramParcel1.readString();
          paramInt1 = paramParcel1.readInt();
          bool16 = bool1;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setPrivacyApp((String)localObject, paramInt1, bool16);
          paramParcel2.writeNoException();
          return true;
        case 80: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          localObject = paramParcel1.createStringArrayList();
          bool16 = bool2;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          pushUpdatePkgsData((List)localObject, bool16);
          paramParcel2.writeNoException();
          return true;
        case 79: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = getStickWindowName(paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 78: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          setStickWindowName(paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        case 77: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            localObject = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject = null;
          }
          paramInt1 = resizeTask(paramInt1, (Rect)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 76: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = paramParcel1.readInt();
          paramInt2 = paramParcel1.readInt();
          bool16 = bool3;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          paramInt1 = moveTaskToStack(paramInt1, paramInt2, bool16);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 75: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = getSecondSpaceId();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 74: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          watchGreenGuardProcess();
          paramParcel2.writeNoException();
          return true;
        case 73: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramParcel1 = getAppRunningControlIBinder();
          paramParcel2.writeNoException();
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        case 72: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramParcel1 = getTopActivity();
          paramParcel2.writeNoException();
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        case 71: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          if (paramParcel1.readInt() != 0) {
            localObject = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject = null;
          }
          paramInt1 = isAllowStartService((Intent)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 70: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = isAppPrivacyEnabled(paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 69: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          localObject = paramParcel1.readString();
          bool16 = bool4;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setAppPrivacyStatus((String)localObject, bool16);
          paramParcel2.writeNoException();
          return true;
        case 68: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramParcel1 = getAccessControlPasswordType(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        case 67: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = haveAccessControlPassword(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 66: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = checkAccessControlPassword(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 65: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          setAccessControlPassword(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 64: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = getGameMode(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 63: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          localObject = paramParcel1.readStrongBinder();
          paramInt1 = paramParcel1.readInt();
          bool16 = bool5;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setGameBoosterIBinder((IBinder)localObject, paramInt1, bool16);
          paramParcel2.writeNoException();
          return true;
        case 62: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          str1 = paramParcel1.readString();
          if (paramParcel1.readInt() != 0) {
            localObject = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject = null;
          }
          paramInt1 = checkGameBoosterAntimsgPassAsUser(str1, (Intent)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 61: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = isValidDevice();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 60: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          bool16 = bool6;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          paramInt1 = setAppHide(bool16);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 59: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = isFunctionOpen();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 58: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = isAppHide();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 57: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          localObject = paramParcel1.readString();
          paramInt1 = paramParcel1.readInt();
          bool16 = bool7;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setNotificationsEnabledForPackage((String)localObject, paramInt1, bool16);
          paramParcel2.writeNoException();
          return true;
        case 56: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = areNotificationsEnabledForPackage(paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 55: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          localObject = paramParcel1.readString();
          bool16 = bool8;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setApplicationMaskNotificationEnabledForUser((String)localObject, bool16, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 54: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = getApplicationMaskNotificationEnabledAsUser(paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 53: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          notifyAppsPreInstalled();
          return true;
        case 52: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          offerGoogleBaseCallBack(ISecurityCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 51: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramParcel1 = getWakePathComponents(paramParcel1.readString());
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
        case 50: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramParcel1 = getIncompatibleAppList();
          paramParcel2.writeNoException();
          paramParcel2.writeStringList(paramParcel1);
          return true;
        case 49: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          setIncompatibleAppList(paramParcel1.createStringArrayList());
          paramParcel2.writeNoException();
          return true;
        case 48: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = setCurrentNetworkState(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 47: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = setMiuiFirewallRule(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 46: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = addMiuiFirewallSharedUid(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 45: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          localObject = paramParcel1.readString();
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (Bitmap)Bitmap.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          saveIcon((String)localObject, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        case 44: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          bool16 = bool9;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          paramInt1 = writeAppHideConfig(bool16);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 43: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = isRestrictedAppNet(paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 42: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          localObject = paramParcel1.readString();
          bool16 = bool10;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setAppDarkModeForUser((String)localObject, bool16, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 41: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = getAppDarkModeForUser(paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 40: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = getAppDarkMode(paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 39: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          localObject = paramParcel1.readString();
          bool16 = bool11;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setApplicationAccessControlEnabledForUser((String)localObject, bool16, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 38: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          addAccessControlPassForUser(paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 37: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          pushWakePathConfirmDialogWhiteList(paramParcel1.readInt(), paramParcel1.createStringArrayList());
          paramParcel2.writeNoException();
          return true;
        case 36: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          grantInstallPermission(paramParcel1.readString(), paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        case 35: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = getSysAppCracked();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 34: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          String str2 = paramParcel1.readString();
          str1 = paramParcel1.readString();
          if (paramParcel1.readInt() != 0) {
            localObject = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject = null;
          }
          paramInt1 = checkAllowStartActivity(str2, str1, (Intent)localObject, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 33: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          removeWakePathData(paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 32: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = getApplicationAccessControlEnabledAsUser(paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 31: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          str1 = paramParcel1.readString();
          if (paramParcel1.readInt() != 0) {
            localObject = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject = null;
          }
          paramInt1 = checkAccessControlPassAsUser(str1, (Intent)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 30: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = getCurrentUserId();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 29: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          grantRuntimePermission(paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        case 28: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          bool16 = bool12;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setCoreRuntimePermissionEnabled(bool16, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 27: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          paramInt1 = activityResume(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 26: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          finishAccessControl(paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 25: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = needFinishAccessControl(paramParcel1.readStrongBinder());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 24: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          removeAccessControlPassAsUser(paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 23: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          registerWakePathCallback(IWakePathCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        case 22: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          setAppPermissionControlOpen(paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 21: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = getAppPermissionControlOpen(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 20: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramParcel1 = getWakePathCallListLog();
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
        case 19: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          bool16 = bool13;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setTrackWakePathCallListLogEnabled(bool16);
          paramParcel2.writeNoException();
          return true;
        case 18: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          pushWakePathWhiteList(paramParcel1.createStringArrayList(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 17: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() != 0) {
            localObject = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(paramParcel1);
          } else {
            localObject = null;
          }
          pushWakePathData(paramInt1, (ParceledListSlice)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        case 16: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramParcel1 = readSystemDataStringFile(paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        case 15: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = putSystemDataStringFile(paramParcel1.readString(), paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 14: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          long l = getWakeUpTime(paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeLong(l);
          return true;
        case 13: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          setWakeUpTime(paramParcel1.readString(), paramParcel1.readLong());
          paramParcel2.writeNoException();
          return true;
        case 12: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          localObject = paramParcel1.readString();
          bool16 = bool14;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setApplicationChildrenControlEnabled((String)localObject, bool16);
          paramParcel2.writeNoException();
          return true;
        case 11: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = getApplicationChildrenControlEnabled(paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 10: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          localObject = paramParcel1.readString();
          bool16 = bool15;
          if (paramParcel1.readInt() != 0) {
            bool16 = true;
          }
          setApplicationAccessControlEnabled((String)localObject, bool16);
          paramParcel2.writeNoException();
          return true;
        case 9: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = getApplicationAccessControlEnabled(paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 8: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          localObject = paramParcel1.readString();
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          paramInt1 = checkAccessControlPass((String)localObject, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 7: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          removeAccessControlPass(paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        case 6: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          addAccessControlPass(paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        case 5: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = stopInterceptSmsBySender();
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 4: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramInt1 = startInterceptSmsBySender(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        case 3: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          if (paramParcel1.readInt() != 0) {
            paramParcel1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          } else {
            paramParcel1 = null;
          }
          paramInt1 = checkSmsBlocked(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
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
        case 2: 
          paramParcel1.enforceInterface("miui.security.ISecurityManager");
          paramParcel1 = getPackageNameByPid(paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
        paramParcel1.enforceInterface("miui.security.ISecurityManager");
        killNativePackageProcesses(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel2.writeString("miui.security.ISecurityManager");
      return true;
    }
    
    private static class Proxy
      implements ISecurityManager
    {
      public static ISecurityManager sDefaultImpl;
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public int activityResume(Intent paramIntent)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          if (paramIntent != null)
          {
            localParcel1.writeInt(1);
            paramIntent.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(27, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            i = ISecurityManager.Stub.getDefaultImpl().activityResume(paramIntent);
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
      
      public void addAccessControlPass(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(6, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().addAccessControlPass(paramString);
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
      
      public void addAccessControlPassForUser(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(38, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().addAccessControlPassForUser(paramString, paramInt);
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
      
      public boolean addMiuiFirewallSharedUid(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(46, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().addMiuiFirewallSharedUid(paramInt);
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
      
      public boolean areNotificationsEnabledForPackage(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(56, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().areNotificationsEnabledForPackage(paramString, paramInt);
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
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public boolean checkAccessControlPass(String paramString, Intent paramIntent)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          boolean bool = true;
          if (paramIntent != null)
          {
            localParcel1.writeInt(1);
            paramIntent.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(8, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().checkAccessControlPass(paramString, paramIntent);
            return bool;
          }
          localParcel2.readException();
          int i = localParcel2.readInt();
          if (i == 0) {
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
      
      public boolean checkAccessControlPassAsUser(String paramString, Intent paramIntent, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          boolean bool = true;
          if (paramIntent != null)
          {
            localParcel1.writeInt(1);
            paramIntent.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(31, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().checkAccessControlPassAsUser(paramString, paramIntent, paramInt);
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
      
      public boolean checkAccessControlPassword(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(66, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().checkAccessControlPassword(paramString1, paramString2, paramInt);
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
      
      /* Error */
      public boolean checkAllowStartActivity(String paramString1, String paramString2, Intent paramIntent, int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 6
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 7
        //   10: aload 6
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 6
        //   19: aload_1
        //   20: invokevirtual 75	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 6
        //   25: aload_2
        //   26: invokevirtual 75	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: iconst_1
        //   30: istore 8
        //   32: aload_3
        //   33: ifnull +19 -> 52
        //   36: aload 6
        //   38: iconst_1
        //   39: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   42: aload_3
        //   43: aload 6
        //   45: iconst_0
        //   46: invokevirtual 48	android/content/Intent:writeToParcel	(Landroid/os/Parcel;I)V
        //   49: goto +9 -> 58
        //   52: aload 6
        //   54: iconst_0
        //   55: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   58: aload 6
        //   60: iload 4
        //   62: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   65: aload 6
        //   67: iload 5
        //   69: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   72: aload_0
        //   73: getfield 21	miui/security/ISecurityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   76: bipush 34
        //   78: aload 6
        //   80: aload 7
        //   82: iconst_0
        //   83: invokeinterface 54 5 0
        //   88: ifne +39 -> 127
        //   91: invokestatic 58	miui/security/ISecurityManager$Stub:getDefaultImpl	()Lmiui/security/ISecurityManager;
        //   94: ifnull +33 -> 127
        //   97: invokestatic 58	miui/security/ISecurityManager$Stub:getDefaultImpl	()Lmiui/security/ISecurityManager;
        //   100: aload_1
        //   101: aload_2
        //   102: aload_3
        //   103: iload 4
        //   105: iload 5
        //   107: invokeinterface 107 6 0
        //   112: istore 8
        //   114: aload 7
        //   116: invokevirtual 63	android/os/Parcel:recycle	()V
        //   119: aload 6
        //   121: invokevirtual 63	android/os/Parcel:recycle	()V
        //   124: iload 8
        //   126: ireturn
        //   127: aload 7
        //   129: invokevirtual 66	android/os/Parcel:readException	()V
        //   132: aload 7
        //   134: invokevirtual 70	android/os/Parcel:readInt	()I
        //   137: istore 4
        //   139: iload 4
        //   141: ifeq +6 -> 147
        //   144: goto +6 -> 150
        //   147: iconst_0
        //   148: istore 8
        //   150: aload 7
        //   152: invokevirtual 63	android/os/Parcel:recycle	()V
        //   155: aload 6
        //   157: invokevirtual 63	android/os/Parcel:recycle	()V
        //   160: iload 8
        //   162: ireturn
        //   163: astore_1
        //   164: goto +20 -> 184
        //   167: astore_1
        //   168: goto +16 -> 184
        //   171: astore_1
        //   172: goto +12 -> 184
        //   175: astore_1
        //   176: goto +8 -> 184
        //   179: astore_1
        //   180: goto +4 -> 184
        //   183: astore_1
        //   184: aload 7
        //   186: invokevirtual 63	android/os/Parcel:recycle	()V
        //   189: aload 6
        //   191: invokevirtual 63	android/os/Parcel:recycle	()V
        //   194: aload_1
        //   195: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	196	0	this	Proxy
        //   0	196	1	paramString1	String
        //   0	196	2	paramString2	String
        //   0	196	3	paramIntent	Intent
        //   0	196	4	paramInt1	int
        //   0	196	5	paramInt2	int
        //   3	187	6	localParcel1	Parcel
        //   8	177	7	localParcel2	Parcel
        //   30	131	8	bool	boolean
        // Exception table:
        //   from	to	target	type
        //   72	114	163	finally
        //   127	139	163	finally
        //   65	72	167	finally
        //   58	65	171	finally
        //   23	29	175	finally
        //   36	49	175	finally
        //   52	58	175	finally
        //   17	23	179	finally
        //   10	17	183	finally
      }
      
      public boolean checkGameBoosterAntimsgPassAsUser(String paramString, Intent paramIntent, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          boolean bool = true;
          if (paramIntent != null)
          {
            localParcel1.writeInt(1);
            paramIntent.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(62, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().checkGameBoosterAntimsgPassAsUser(paramString, paramIntent, paramInt);
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
      
      public boolean checkSmsBlocked(Intent paramIntent)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          boolean bool = true;
          if (paramIntent != null)
          {
            localParcel1.writeInt(1);
            paramIntent.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(3, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().checkSmsBlocked(paramIntent);
            return bool;
          }
          localParcel2.readException();
          if (localParcel2.readInt() == 0) {
            bool = false;
          }
          if (localParcel2.readInt() != 0) {
            paramIntent.readFromParcel(localParcel2);
          }
          return bool;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void finishAccessControl(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(26, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().finishAccessControl(paramString, paramInt);
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
      
      public String getAccessControlPasswordType(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(68, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            str = ISecurityManager.Stub.getDefaultImpl().getAccessControlPasswordType(paramInt);
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
      
      public List<String> getAllPrivacyApps(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(83, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            localObject1 = ISecurityManager.Stub.getDefaultImpl().getAllPrivacyApps(paramInt);
            return (List<String>)localObject1;
          }
          localParcel2.readException();
          Object localObject1 = localParcel2.createStringArrayList();
          return (List<String>)localObject1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean getAppDarkMode(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(40, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().getAppDarkMode(paramString);
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
      
      public boolean getAppDarkModeForUser(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(41, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().getAppDarkModeForUser(paramString, paramInt);
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
      
      public int getAppPermissionControlOpen(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(21, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            paramInt = ISecurityManager.Stub.getDefaultImpl().getAppPermissionControlOpen(paramInt);
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
      
      public IBinder getAppRunningControlIBinder()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          if ((!this.mRemote.transact(73, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            localIBinder = ISecurityManager.Stub.getDefaultImpl().getAppRunningControlIBinder();
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
      
      public boolean getApplicationAccessControlEnabled(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(9, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().getApplicationAccessControlEnabled(paramString);
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
      
      public boolean getApplicationAccessControlEnabledAsUser(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(32, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().getApplicationAccessControlEnabledAsUser(paramString, paramInt);
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
      
      public boolean getApplicationChildrenControlEnabled(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(11, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().getApplicationChildrenControlEnabled(paramString);
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
      
      public boolean getApplicationMaskNotificationEnabledAsUser(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(54, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().getApplicationMaskNotificationEnabledAsUser(paramString, paramInt);
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
      
      public int getCurrentUserId()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          if ((!this.mRemote.transact(30, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            i = ISecurityManager.Stub.getDefaultImpl().getCurrentUserId();
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
      
      public boolean getGameMode(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(64, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().getGameMode(paramInt);
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
      
      public List<String> getIncompatibleAppList()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          if ((!this.mRemote.transact(50, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            localObject1 = ISecurityManager.Stub.getDefaultImpl().getIncompatibleAppList();
            return (List<String>)localObject1;
          }
          localParcel2.readException();
          Object localObject1 = localParcel2.createStringArrayList();
          return (List<String>)localObject1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "miui.security.ISecurityManager";
      }
      
      public String getPackageNameByPid(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(2, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            str = ISecurityManager.Stub.getDefaultImpl().getPackageNameByPid(paramInt);
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
      
      public int getPermissionFlagsAsUser(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(88, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            paramInt = ISecurityManager.Stub.getDefaultImpl().getPermissionFlagsAsUser(paramString1, paramString2, paramInt);
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
      
      public int getSecondSpaceId()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          if ((!this.mRemote.transact(75, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            i = ISecurityManager.Stub.getDefaultImpl().getSecondSpaceId();
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
      
      public boolean getStickWindowName(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(79, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().getStickWindowName(paramString);
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
      
      public int getSysAppCracked()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          if ((!this.mRemote.transact(35, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            i = ISecurityManager.Stub.getDefaultImpl().getSysAppCracked();
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
      
      public IBinder getTopActivity()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          if ((!this.mRemote.transact(72, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            localIBinder = ISecurityManager.Stub.getDefaultImpl().getTopActivity();
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
      
      public ParceledListSlice getWakePathCallListLog()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          ParceledListSlice localParceledListSlice;
          if ((!this.mRemote.transact(20, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            localParceledListSlice = ISecurityManager.Stub.getDefaultImpl().getWakePathCallListLog();
            return localParceledListSlice;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            localParceledListSlice = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(localParcel2);
          } else {
            localParceledListSlice = null;
          }
          return localParceledListSlice;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public ParceledListSlice getWakePathComponents(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(51, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            paramString = ISecurityManager.Stub.getDefaultImpl().getWakePathComponents(paramString);
            return paramString;
          }
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            paramString = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(localParcel2);
          } else {
            paramString = null;
          }
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public long getWakeUpTime(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(14, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            l = ISecurityManager.Stub.getDefaultImpl().getWakeUpTime(paramString);
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
      
      public void grantInstallPermission(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          if ((!this.mRemote.transact(36, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().grantInstallPermission(paramString1, paramString2);
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
      
      public void grantRuntimePermission(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(29, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().grantRuntimePermission(paramString);
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
      
      public void grantRuntimePermissionAsUser(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(85, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().grantRuntimePermissionAsUser(paramString1, paramString2, paramInt);
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
      
      public boolean haveAccessControlPassword(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(67, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().haveAccessControlPassword(paramInt);
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
      
      public boolean isAllowStartService(Intent paramIntent, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          boolean bool = true;
          if (paramIntent != null)
          {
            localParcel1.writeInt(1);
            paramIntent.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(71, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().isAllowStartService(paramIntent, paramInt);
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
      
      public boolean isAppHide()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(58, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().isAppHide();
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
      
      public boolean isAppPrivacyEnabled(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(70, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().isAppPrivacyEnabled(paramString);
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
      
      public boolean isFunctionOpen()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(59, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().isFunctionOpen();
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
      
      public boolean isPrivacyApp(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(82, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().isPrivacyApp(paramString, paramInt);
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
      
      public boolean isRestrictedAppNet(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(43, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().isRestrictedAppNet(paramString);
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
      
      public boolean isValidDevice()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(61, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().isValidDevice();
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
      
      public void killNativePackageProcesses(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(1, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().killNativePackageProcesses(paramInt, paramString);
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
      
      public int moveTaskToStack(int paramInt1, int paramInt2, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(76, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            paramInt1 = ISecurityManager.Stub.getDefaultImpl().moveTaskToStack(paramInt1, paramInt2, paramBoolean);
            return paramInt1;
          }
          localParcel2.readException();
          paramInt1 = localParcel2.readInt();
          return paramInt1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean needFinishAccessControl(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeStrongBinder(paramIBinder);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(25, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().needFinishAccessControl(paramIBinder);
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
      
      public void notifyAppsPreInstalled()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("miui.security.ISecurityManager");
          if ((!this.mRemote.transact(53, localParcel, null, 1)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().notifyAppsPreInstalled();
            return;
          }
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void offerGoogleBaseCallBack(ISecurityCallback paramISecurityCallback)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          IBinder localIBinder;
          if (paramISecurityCallback != null) {
            localIBinder = paramISecurityCallback.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(52, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().offerGoogleBaseCallBack(paramISecurityCallback);
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
      
      public void pushUpdatePkgsData(List<String> paramList, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeStringList(paramList);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(80, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().pushUpdatePkgsData(paramList, paramBoolean);
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
      
      public void pushWakePathConfirmDialogWhiteList(int paramInt, List<String> paramList)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeInt(paramInt);
          localParcel1.writeStringList(paramList);
          if ((!this.mRemote.transact(37, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().pushWakePathConfirmDialogWhiteList(paramInt, paramList);
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
      
      public void pushWakePathData(int paramInt1, ParceledListSlice paramParceledListSlice, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeInt(paramInt1);
          if (paramParceledListSlice != null)
          {
            localParcel1.writeInt(1);
            paramParceledListSlice.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          localParcel1.writeInt(paramInt2);
          if ((!this.mRemote.transact(17, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().pushWakePathData(paramInt1, paramParceledListSlice, paramInt2);
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
      
      public void pushWakePathWhiteList(List<String> paramList, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeStringList(paramList);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(18, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().pushWakePathWhiteList(paramList, paramInt);
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
      
      public boolean putSystemDataStringFile(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(15, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().putSystemDataStringFile(paramString1, paramString2);
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
      
      public String readSystemDataStringFile(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(16, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            paramString = ISecurityManager.Stub.getDefaultImpl().readSystemDataStringFile(paramString);
            return paramString;
          }
          localParcel2.readException();
          paramString = localParcel2.readString();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void registerWakePathCallback(IWakePathCallback paramIWakePathCallback)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          IBinder localIBinder;
          if (paramIWakePathCallback != null) {
            localIBinder = paramIWakePathCallback.asBinder();
          } else {
            localIBinder = null;
          }
          localParcel1.writeStrongBinder(localIBinder);
          if ((!this.mRemote.transact(23, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().registerWakePathCallback(paramIWakePathCallback);
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
      
      public void removeAccessControlPass(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(7, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().removeAccessControlPass(paramString);
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
      
      public void removeAccessControlPassAsUser(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(24, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().removeAccessControlPassAsUser(paramString, paramInt);
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
      
      public void removeWakePathData(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(33, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().removeWakePathData(paramInt);
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
      
      public int resizeTask(int paramInt1, Rect paramRect, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeInt(paramInt1);
          if (paramRect != null)
          {
            localParcel1.writeInt(1);
            paramRect.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          localParcel1.writeInt(paramInt2);
          if ((!this.mRemote.transact(77, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            paramInt1 = ISecurityManager.Stub.getDefaultImpl().resizeTask(paramInt1, paramRect, paramInt2);
            return paramInt1;
          }
          localParcel2.readException();
          paramInt1 = localParcel2.readInt();
          return paramInt1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void revokeRuntimePermissionAsUser(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(86, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().revokeRuntimePermissionAsUser(paramString1, paramString2, paramInt);
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
      
      public void revokeRuntimePermissionAsUserNotKill(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(87, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().revokeRuntimePermissionAsUserNotKill(paramString1, paramString2, paramInt);
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
      
      public void saveIcon(String paramString, Bitmap paramBitmap)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          if (paramBitmap != null)
          {
            localParcel1.writeInt(1);
            paramBitmap.writeToParcel(localParcel1, 0);
          }
          else
          {
            localParcel1.writeInt(0);
          }
          if ((!this.mRemote.transact(45, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().saveIcon(paramString, paramBitmap);
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
      
      public void setAccessControlPassword(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(65, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().setAccessControlPassword(paramString1, paramString2, paramInt);
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
      
      public void setAppDarkModeForUser(String paramString, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(42, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().setAppDarkModeForUser(paramString, paramBoolean, paramInt);
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
      
      public boolean setAppHide(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          boolean bool = true;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(60, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            paramBoolean = ISecurityManager.Stub.getDefaultImpl().setAppHide(paramBoolean);
            return paramBoolean;
          }
          localParcel2.readException();
          int i = localParcel2.readInt();
          if (i != 0) {
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
      
      public void setAppPermissionControlOpen(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(22, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().setAppPermissionControlOpen(paramInt);
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
      
      public void setAppPrivacyStatus(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(69, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().setAppPrivacyStatus(paramString, paramBoolean);
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
      
      public void setApplicationAccessControlEnabled(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(10, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().setApplicationAccessControlEnabled(paramString, paramBoolean);
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
      
      public void setApplicationAccessControlEnabledForUser(String paramString, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(39, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().setApplicationAccessControlEnabledForUser(paramString, paramBoolean, paramInt);
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
      
      public void setApplicationChildrenControlEnabled(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(12, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().setApplicationChildrenControlEnabled(paramString, paramBoolean);
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
      
      public void setApplicationMaskNotificationEnabledForUser(String paramString, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(55, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().setApplicationMaskNotificationEnabledForUser(paramString, paramBoolean, paramInt);
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
      
      public void setCoreRuntimePermissionEnabled(boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          if ((!this.mRemote.transact(28, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().setCoreRuntimePermissionEnabled(paramBoolean, paramInt);
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
      
      public boolean setCurrentNetworkState(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(48, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().setCurrentNetworkState(paramInt);
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
      
      public void setGameBoosterIBinder(IBinder paramIBinder, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(63, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().setGameBoosterIBinder(paramIBinder, paramInt, paramBoolean);
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
      
      public void setIncompatibleAppList(List<String> paramList)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeStringList(paramList);
          if ((!this.mRemote.transact(49, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().setIncompatibleAppList(paramList);
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
      
      public boolean setMiuiFirewallRule(String paramString, int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(47, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().setMiuiFirewallRule(paramString, paramInt1, paramInt2, paramInt3);
            return bool;
          }
          localParcel2.readException();
          paramInt1 = localParcel2.readInt();
          if (paramInt1 != 0) {
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
      
      public void setNotificationsEnabledForPackage(String paramString, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(57, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().setNotificationsEnabledForPackage(paramString, paramInt, paramBoolean);
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
      
      public void setPrivacyApp(String paramString, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(81, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().setPrivacyApp(paramString, paramInt, paramBoolean);
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
      
      public void setStickWindowName(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          if ((!this.mRemote.transact(78, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().setStickWindowName(paramString);
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
      
      public void setTrackWakePathCallListLogEnabled(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(19, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().setTrackWakePathCallListLogEnabled(paramBoolean);
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
      
      public void setWakeUpTime(String paramString, long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString);
          localParcel1.writeLong(paramLong);
          if ((!this.mRemote.transact(13, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().setWakeUpTime(paramString, paramLong);
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
      
      public boolean startInterceptSmsBySender(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(4, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().startInterceptSmsBySender(paramString1, paramString2, paramInt);
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
      
      public boolean stopInterceptSmsBySender()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          IBinder localIBinder = this.mRemote;
          boolean bool = false;
          if ((!localIBinder.transact(5, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            bool = ISecurityManager.Stub.getDefaultImpl().stopInterceptSmsBySender();
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
      
      public void updateLauncherPackageNames()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          if ((!this.mRemote.transact(84, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().updateLauncherPackageNames();
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
      
      public void updateLedStatus(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          int i;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(90, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().updateLedStatus(paramBoolean);
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
      
      public void updatePermissionFlagsAsUser(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          if ((!this.mRemote.transact(89, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().updatePermissionFlagsAsUser(paramString1, paramString2, paramInt1, paramInt2, paramInt3);
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
      
      public void watchGreenGuardProcess()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          if ((!this.mRemote.transact(74, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            ISecurityManager.Stub.getDefaultImpl().watchGreenGuardProcess();
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
      
      public boolean writeAppHideConfig(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("miui.security.ISecurityManager");
          boolean bool = true;
          if (paramBoolean) {
            i = 1;
          } else {
            i = 0;
          }
          localParcel1.writeInt(i);
          if ((!this.mRemote.transact(44, localParcel1, localParcel2, 0)) && (ISecurityManager.Stub.getDefaultImpl() != null))
          {
            paramBoolean = ISecurityManager.Stub.getDefaultImpl().writeAppHideConfig(paramBoolean);
            return paramBoolean;
          }
          localParcel2.readException();
          int i = localParcel2.readInt();
          if (i != 0) {
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
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/security/ISecurityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */