package android.view.textclassifier;

public final class Log
{
  static final boolean ENABLE_FULL_LOGGING = android.util.Log.isLoggable("androidtc", 2);
  
  public static void d(String paramString1, String paramString2)
  {
    android.util.Log.d(paramString1, paramString2);
  }
  
  public static void e(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if (ENABLE_FULL_LOGGING)
    {
      android.util.Log.e(paramString1, paramString2, paramThrowable);
    }
    else
    {
      if (paramThrowable != null) {
        paramThrowable = paramThrowable.getClass().getSimpleName();
      } else {
        paramThrowable = "??";
      }
      android.util.Log.d(paramString1, String.format("%s (%s)", new Object[] { paramString2, paramThrowable }));
    }
  }
  
  public static void v(String paramString1, String paramString2)
  {
    if (ENABLE_FULL_LOGGING) {
      android.util.Log.v(paramString1, paramString2);
    }
  }
  
  public static void w(String paramString1, String paramString2)
  {
    android.util.Log.w(paramString1, paramString2);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/Log.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */