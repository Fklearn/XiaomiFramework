package android.view.contentcapture;

import android.os.Build;
import android.provider.DeviceConfig;
import android.util.ArraySet;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ContentCaptureHelper
{
  private static final String TAG = ContentCaptureHelper.class.getSimpleName();
  public static boolean sDebug = true;
  public static boolean sVerbose = false;
  
  private ContentCaptureHelper()
  {
    throw new UnsupportedOperationException("contains only static methods");
  }
  
  public static int getDefaultLoggingLevel()
  {
    return Build.IS_DEBUGGABLE;
  }
  
  public static String getLoggingLevelAsString(int paramInt)
  {
    if (paramInt != 0)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("UNKNOWN-");
          localStringBuilder.append(paramInt);
          return localStringBuilder.toString();
        }
        return "VERBOSE";
      }
      return "DEBUG";
    }
    return "OFF";
  }
  
  public static String getSanitizedString(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null)
    {
      paramCharSequence = null;
    }
    else
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramCharSequence.length());
      localStringBuilder.append("_chars");
      paramCharSequence = localStringBuilder.toString();
    }
    return paramCharSequence;
  }
  
  public static void setLoggingLevel()
  {
    setLoggingLevel(DeviceConfig.getInt("content_capture", "logging_level", getDefaultLoggingLevel()));
  }
  
  public static void setLoggingLevel(int paramInt)
  {
    String str = TAG;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Setting logging level to ");
    localStringBuilder.append(getLoggingLevelAsString(paramInt));
    Log.i(str, localStringBuilder.toString());
    sDebug = false;
    sVerbose = false;
    if (paramInt != 0)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          str = TAG;
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("setLoggingLevel(): invalud level: ");
          localStringBuilder.append(paramInt);
          Log.w(str, localStringBuilder.toString());
          return;
        }
        sVerbose = true;
      }
      sDebug = true;
      return;
    }
  }
  
  public static <T> ArrayList<T> toList(Set<T> paramSet)
  {
    if (paramSet == null) {
      paramSet = null;
    } else {
      paramSet = new ArrayList(paramSet);
    }
    return paramSet;
  }
  
  public static <T> ArraySet<T> toSet(List<T> paramList)
  {
    if (paramList == null) {
      paramList = null;
    } else {
      paramList = new ArraySet(paramList);
    }
    return paramList;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/contentcapture/ContentCaptureHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */