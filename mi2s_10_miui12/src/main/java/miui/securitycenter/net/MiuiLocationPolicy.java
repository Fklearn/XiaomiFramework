package miui.securitycenter.net;

import android.content.Context;
import android.location.LocationPolicyManager;

public class MiuiLocationPolicy
{
  LocationPolicyManager mLocalPolicy;
  
  public MiuiLocationPolicy(Context paramContext)
  {
    this.mLocalPolicy = LocationPolicyManager.from(paramContext);
  }
  
  public boolean getAppRestrictBackground(int paramInt)
  {
    boolean bool;
    if (this.mLocalPolicy.getUidPolicy(paramInt) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void setAppRestrictBackground(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mLocalPolicy.setUidPolicy(paramInt, 255);
    } else {
      this.mLocalPolicy.setUidPolicy(paramInt, 0);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/net/MiuiLocationPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */