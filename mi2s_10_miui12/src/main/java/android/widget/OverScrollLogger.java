package android.widget;

import android.util.Log;
import java.util.Locale;

class OverScrollLogger
{
  private static final boolean DEBUG = Log.isLoggable("OverScroll", 3);
  private static final String TAG = "OverScroll";
  private static final boolean VERBOSE = Log.isLoggable("OverScroll", 2);
  
  public static void debug(String paramString)
  {
    if (DEBUG) {
      Log.d("OverScroll", paramString);
    }
  }
  
  public static void debug(String paramString, Object... paramVarArgs)
  {
    if (DEBUG) {
      Log.d("OverScroll", String.format(Locale.US, paramString, paramVarArgs));
    }
  }
  
  public static void verbose(String paramString)
  {
    if (VERBOSE) {
      Log.v("OverScroll", paramString);
    }
  }
  
  public static void verbose(String paramString, Object... paramVarArgs)
  {
    if (VERBOSE) {
      Log.v("OverScroll", String.format(Locale.US, paramString, paramVarArgs));
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/OverScrollLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */