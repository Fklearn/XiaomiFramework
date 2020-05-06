package miui.securitycenter.applicationlock;

import android.content.Context;
import android.security.MiuiLockPatternUtils;
import miui.security.SecurityManager;

public class MiuiLockPatternUtilsWrapper
{
  private MiuiLockPatternUtils mLockPatternUtils;
  private SecurityManager mSecurityManager;
  
  public MiuiLockPatternUtilsWrapper(Context paramContext)
  {
    this.mLockPatternUtils = new MiuiLockPatternUtils(paramContext);
    this.mSecurityManager = ((SecurityManager)paramContext.getSystemService("security"));
  }
  
  public boolean checkMiuiLockPattern(String paramString)
  {
    return false;
  }
  
  public void clearLockoutAttemptDeadline()
  {
    this.mLockPatternUtils.clearLockoutAttemptDeadline();
  }
  
  public boolean isTactileFeedbackEnabled()
  {
    return this.mLockPatternUtils.isTactileFeedbackEnabled();
  }
  
  public void saveMiuiLockPattern(String paramString) {}
  
  public boolean savedMiuiLockPatternExists()
  {
    return this.mSecurityManager.haveAccessControlPassword();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/applicationlock/MiuiLockPatternUtilsWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */