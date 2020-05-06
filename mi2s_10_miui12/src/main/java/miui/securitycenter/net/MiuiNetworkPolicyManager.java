package miui.securitycenter.net;

import android.content.Context;
import android.net.NetworkPolicyManager;

public class MiuiNetworkPolicyManager
{
  private NetworkPolicyManager mPolicyService;
  
  public MiuiNetworkPolicyManager(Context paramContext)
  {
    this.mPolicyService = NetworkPolicyManager.from(paramContext);
  }
  
  public int getAppRestrictBackground(int paramInt)
  {
    return this.mPolicyService.getUidPolicy(paramInt);
  }
  
  public boolean getRestrictBackground()
  {
    return this.mPolicyService.getRestrictBackground();
  }
  
  public boolean isAppRestrictBackground(int paramInt)
  {
    paramInt = this.mPolicyService.getUidPolicy(paramInt);
    boolean bool = true;
    if (paramInt != 1) {
      bool = false;
    }
    return bool;
  }
  
  public void setAppRestrictBackground(int paramInt, boolean paramBoolean)
  {
    this.mPolicyService.setUidPolicy(paramInt, paramBoolean);
  }
  
  public void setRestrictBackground(boolean paramBoolean)
  {
    this.mPolicyService.setRestrictBackground(paramBoolean);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/net/MiuiNetworkPolicyManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */