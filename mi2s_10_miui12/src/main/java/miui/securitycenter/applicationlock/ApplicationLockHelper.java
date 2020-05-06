package miui.securitycenter.applicationlock;

import android.content.Context;
import android.security.MiuiLockPatternUtils;

public class ApplicationLockHelper
{
  private Context mContext;
  private MiuiLockPatternUtils mLockPatternUtils;
  
  public ApplicationLockHelper(Context paramContext)
  {
    this.mContext = paramContext;
    this.mLockPatternUtils = new MiuiLockPatternUtils(paramContext);
  }
  
  public boolean checkLockPattern(String paramString)
  {
    return false;
  }
  
  public void clearAppLock()
  {
    this.mLockPatternUtils.clearLock(null, this.mContext.getUserId());
  }
  
  public long getLockoutAttempt()
  {
    return this.mLockPatternUtils.getLockoutAttemptDeadline(this.mContext.getUserId());
  }
  
  public boolean isVisiblePatternLock()
  {
    return this.mLockPatternUtils.isVisiblePatternEnabled(this.mContext.getUserId());
  }
  
  public boolean saveLockPatternExists()
  {
    return this.mLockPatternUtils.savedMiuiLockPatternExists();
  }
  
  public long setLockoutAttempt()
  {
    return this.mLockPatternUtils.setLockoutAttemptDeadline(this.mContext.getUserId(), 30000);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/applicationlock/ApplicationLockHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */