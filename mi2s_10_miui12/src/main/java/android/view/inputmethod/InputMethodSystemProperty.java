package android.view.inputmethod;

import android.content.ComponentName;
import android.os.Build;
import android.os.SystemProperties;

public class InputMethodSystemProperty
{
  public static final boolean MULTI_CLIENT_IME_ENABLED;
  public static final boolean PER_PROFILE_IME_ENABLED;
  private static final String PROP_DEBUG_MULTI_CLIENT_IME = "persist.debug.multi_client_ime";
  private static final String PROP_DEBUG_PER_PROFILE_IME = "persist.debug.per_profile_ime";
  private static final String PROP_PROD_MULTI_CLIENT_IME = "ro.sys.multi_client_ime";
  public static final ComponentName sMultiClientImeComponentName = ;
  
  static
  {
    boolean bool;
    if (sMultiClientImeComponentName != null) {
      bool = true;
    } else {
      bool = false;
    }
    MULTI_CLIENT_IME_ENABLED = bool;
    if (MULTI_CLIENT_IME_ENABLED) {
      PER_PROFILE_IME_ENABLED = true;
    } else if (Build.IS_DEBUGGABLE) {
      PER_PROFILE_IME_ENABLED = SystemProperties.getBoolean("persist.debug.per_profile_ime", true);
    } else {
      PER_PROFILE_IME_ENABLED = true;
    }
  }
  
  private static ComponentName getMultiClientImeComponentName()
  {
    if (Build.IS_DEBUGGABLE)
    {
      ComponentName localComponentName = ComponentName.unflattenFromString(SystemProperties.get("persist.debug.multi_client_ime", ""));
      if (localComponentName != null) {
        return localComponentName;
      }
    }
    return ComponentName.unflattenFromString(SystemProperties.get("ro.sys.multi_client_ime", ""));
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/InputMethodSystemProperty.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */