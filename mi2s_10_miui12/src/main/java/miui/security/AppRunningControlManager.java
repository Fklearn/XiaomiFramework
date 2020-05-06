package miui.security;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Slog;
import java.util.List;

public class AppRunningControlManager
{
  private static final String TAG = "AppRunningControlManager";
  private static AppRunningControlManager sInstance;
  private IAppRunningControlManager mService;
  
  private AppRunningControlManager(IBinder paramIBinder)
  {
    this.mService = IAppRunningControlManager.Stub.asInterface(paramIBinder);
  }
  
  public static Intent getBlockActivityIntent(Context paramContext, String paramString, Intent paramIntent, boolean paramBoolean, int paramInt)
  {
    AppRunningControlManager localAppRunningControlManager = getInstance();
    Context localContext = null;
    if (localAppRunningControlManager != null)
    {
      paramContext = localAppRunningControlManager.getBlockActivityIntentInner(paramContext, paramString, paramIntent, paramBoolean, paramInt);
      localContext = paramContext;
      if (paramContext != null) {
        return paramContext;
      }
    }
    return localContext;
  }
  
  private Intent getBlockActivityIntentInner(Context paramContext, String paramString, Intent paramIntent, boolean paramBoolean, int paramInt)
  {
    try
    {
      paramContext = this.mService.getBlockActivityIntent(paramString, paramIntent, paramBoolean, paramInt);
      return paramContext;
    }
    catch (RemoteException paramContext)
    {
      Slog.e("AppRunningControlManager", "Remote service has died", paramContext);
    }
    return null;
  }
  
  public static AppRunningControlManager getInstance()
  {
    if (sInstance == null) {
      try
      {
        IBinder localIBinder = ISecurityManager.Stub.asInterface(ServiceManager.getService("security")).getAppRunningControlIBinder();
        if (localIBinder == null)
        {
          Slog.d("AppRunningControlManager", "AppRunningControlIBinder is null");
          return null;
        }
        AppRunningControlManager localAppRunningControlManager = new miui/security/AppRunningControlManager;
        localAppRunningControlManager.<init>(localIBinder);
        sInstance = localAppRunningControlManager;
      }
      catch (Exception localException)
      {
        throw new RuntimeException("system service died", localException);
      }
    }
    return sInstance;
  }
  
  public static boolean matchRule(String paramString, int paramInt)
  {
    AppRunningControlManager localAppRunningControlManager = getInstance();
    if (localAppRunningControlManager != null) {
      return localAppRunningControlManager.matchRuleInner(paramString, paramInt);
    }
    return false;
  }
  
  private boolean matchRuleInner(String paramString, int paramInt)
  {
    try
    {
      boolean bool = this.mService.matchRule(paramString, paramInt);
      return bool;
    }
    catch (RemoteException paramString)
    {
      Slog.e("AppRunningControlManager", "Remote service has died", paramString);
    }
    return false;
  }
  
  public List<String> getNotDisallowList()
  {
    try
    {
      List localList = this.mService.getNotDisallowList();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("AppRunningControlManager", "Remote service has died", localRemoteException);
    }
    return null;
  }
  
  public void setBlackListEnable(boolean paramBoolean)
  {
    try
    {
      this.mService.setBlackListEnable(paramBoolean);
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("AppRunningControlManager", "Remote service has died", localRemoteException);
    }
  }
  
  public void setDisallowRunningList(List<String> paramList, Intent paramIntent)
  {
    try
    {
      this.mService.setDisallowRunningList(paramList, paramIntent);
      return;
    }
    catch (RemoteException paramList)
    {
      Slog.e("AppRunningControlManager", "Remote service has died", paramList);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/security/AppRunningControlManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */