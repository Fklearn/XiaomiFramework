package android.webkit;

import android.content.Context;
import android.util.Log;

class LegacyErrorStrings
{
  private static final String LOGTAG = "Http";
  
  private static int getResource(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Using generic message for unknown error code: ");
      localStringBuilder.append(paramInt);
      Log.w("Http", localStringBuilder.toString());
      return 17040145;
    case 0: 
      return 17040153;
    case -1: 
      return 17040145;
    case -2: 
      return 17040152;
    case -3: 
      return 17040158;
    case -4: 
      return 17040146;
    case -5: 
      return 17040154;
    case -6: 
      return 17040147;
    case -7: 
      return 17040151;
    case -8: 
      return 17040156;
    case -9: 
      return 17040155;
    case -10: 
      return 17039368;
    case -11: 
      return 17040148;
    case -12: 
      return 17039367;
    case -13: 
      return 17040149;
    case -14: 
      return 17040150;
    }
    return 17040157;
  }
  
  static String getString(int paramInt, Context paramContext)
  {
    return paramContext.getText(getResource(paramInt)).toString();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/LegacyErrorStrings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */