package miui.util;

import android.text.TextUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMAccountUtils
{
  public static boolean isNumeric(String paramString)
  {
    return Pattern.compile("[0-9]*").matcher(paramString).matches();
  }
  
  public static boolean isXiaomiAccount(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return false;
    }
    return paramString.endsWith("@xiaomi.com");
  }
  
  public static boolean isXiaomiJID(String paramString)
  {
    if (!isXiaomiAccount(paramString)) {
      return false;
    }
    return isNumeric(trimDomainSuffix(paramString));
  }
  
  public static String trimDomainSuffix(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    int i = paramString.indexOf("@");
    if (i > 0) {
      return paramString.substring(0, i);
    }
    return paramString;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/XMAccountUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */