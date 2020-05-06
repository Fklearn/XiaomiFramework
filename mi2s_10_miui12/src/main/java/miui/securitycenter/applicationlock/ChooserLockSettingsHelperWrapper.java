package miui.securitycenter.applicationlock;

import android.content.Context;
import android.security.ChooseLockSettingsHelper;

public class ChooserLockSettingsHelperWrapper
{
  private ChooseLockSettingsHelper mChooseLockSettingsHelper;
  private MiuiLockPatternUtilsWrapper mMiuiLockPatternUtilsWrapper;
  
  public ChooserLockSettingsHelperWrapper(Context paramContext)
  {
    this.mMiuiLockPatternUtilsWrapper = new MiuiLockPatternUtilsWrapper(paramContext);
    this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(paramContext);
  }
  
  public boolean isACLockEnabled()
  {
    return this.mMiuiLockPatternUtilsWrapper.savedMiuiLockPatternExists();
  }
  
  public void setACLockEnabled(boolean paramBoolean)
  {
    this.mChooseLockSettingsHelper.setACLockEnabled(paramBoolean);
  }
  
  public void setPasswordForPrivacyModeEnabled(boolean paramBoolean)
  {
    this.mChooseLockSettingsHelper.setPasswordForPrivacyModeEnabled(paramBoolean);
  }
  
  public void setPrivacyModeEnabled(boolean paramBoolean)
  {
    this.mChooseLockSettingsHelper.setPrivacyModeEnabled(paramBoolean);
  }
  
  public MiuiLockPatternUtilsWrapper utils()
  {
    return this.mMiuiLockPatternUtilsWrapper;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/applicationlock/ChooserLockSettingsHelperWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */