package miui.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.text.TextUtils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class RecordingNameUtils
{
  private static final int MAX_FILE_NAME_LENGTH = 50;
  private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
  
  static
  {
    sDateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
  }
  
  public static String generatFMRecordName(Context paramContext, String paramString1, String paramString2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramContext.getString(286130308));
    localStringBuilder.append('@');
    localStringBuilder.append(paramString1);
    localStringBuilder.append('_');
    localStringBuilder.append(sDateFormat.format(Calendar.getInstance().getTime()));
    localStringBuilder.append(paramString2);
    return localStringBuilder.toString();
  }
  
  public static String generateCallRecordName(List<String> paramList1, List<String> paramList2, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("_");
    localStringBuilder.append(sDateFormat.format(Calendar.getInstance().getTime()));
    localStringBuilder.append(paramString);
    String str = localStringBuilder.toString();
    int i = 50 - str.length();
    localStringBuilder = new StringBuilder();
    for (int j = 0; j < paramList2.size(); j++)
    {
      if (j > 0) {
        paramString = "_";
      } else {
        paramString = "";
      }
      localStringBuilder.append(paramString);
      localStringBuilder.append((String)paramList2.get(j));
    }
    j = i - localStringBuilder.length();
    int k = 0;
    for (int m = 0; (m < paramList1.size()) && (j > 2); m++)
    {
      paramString = (String)paramList1.get(m);
      paramList2 = paramString;
      if (paramString.length() > j - 2) {
        paramList2 = paramString.substring(0, j - 2);
      }
      if (TextUtils.isEmpty(paramList2))
      {
        k = localStringBuilder.indexOf("_", k + 1) + 1;
      }
      else
      {
        paramString = new StringBuilder();
        paramString.append(paramList2);
        paramString.append("(");
        localStringBuilder.insert(k, paramString.toString());
        int n = localStringBuilder.indexOf("_", k + 1);
        k = n;
        if (n < 0) {
          k = localStringBuilder.length();
        }
        localStringBuilder.insert(k, ')');
        if (localStringBuilder.charAt(k - 1) == '(')
        {
          localStringBuilder.deleteCharAt(k - 1);
          localStringBuilder.deleteCharAt(k - 1);
        }
        else
        {
          k += 2;
          j -= 2;
        }
        j -= paramList2.length();
      }
    }
    if (i < localStringBuilder.length()) {
      j = i;
    } else {
      j = localStringBuilder.length();
    }
    paramList1 = new StringBuilder();
    paramList1.append(localStringBuilder.substring(0, j));
    paramList1.append(str);
    return paramList1.toString().replace(',', 'p').replace("+", "00").replace('*', 's');
  }
  
  public static String[] getCallRecordingCallerNumbers(Context paramContext, String paramString)
  {
    paramString = getRecordingFileTitle(paramString);
    if (paramString != null) {
      return getPhoneNumbers(paramContext, paramString);
    }
    return null;
  }
  
  public static String getCallRecordingTitle(Context paramContext, String paramString)
  {
    String str = getRecordingFileTitle(paramString);
    int i = paramString.indexOf('@');
    if ((i != -1) && (i != paramString.length() - 1) && (str != null))
    {
      paramContext = getCallers(paramContext, str);
      if (paramContext == null) {
        paramContext = str;
      }
      return paramContext;
    }
    return str;
  }
  
  private static String getCallerString(Context paramContext, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((paramString != null) && (!TextUtils.equals(paramString, "")))
    {
      Cursor localCursor = paramContext.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, paramString), null, null, null, null);
      if (localCursor == null) {
        return paramString;
      }
      HashSet localHashSet = new HashSet();
      int i = localCursor.getColumnIndex("display_name");
      int k;
      for (int j = 1; localCursor.moveToNext(); j = k)
      {
        String str = localCursor.getString(i);
        k = j;
        if (!localHashSet.contains(str))
        {
          if (j == 0)
          {
            localStringBuilder.append(' ');
            localStringBuilder.append(paramContext.getString(286130269));
            localStringBuilder.append(' ');
          }
          localStringBuilder.append(str);
          k = 0;
          localHashSet.add(str);
        }
      }
      localCursor.close();
    }
    else
    {
      localStringBuilder.append(paramContext.getString(286130551));
    }
    if (localStringBuilder.length() != 0) {
      paramString = localStringBuilder.toString();
    }
    return paramString;
  }
  
  private static String getCallers(Context paramContext, String paramString)
  {
    paramString = getPhoneNumbers(paramContext, paramString);
    if (paramString.length == 0) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramString.length; i++)
    {
      localStringBuilder.append(getCallerString(paramContext, paramString[i]));
      if (i != paramString.length - 1) {
        localStringBuilder.append(", ");
      }
    }
    return localStringBuilder.toString();
  }
  
  public static String getFMRecordingTitle(String paramString)
  {
    return getRecordingFileTitle(paramString);
  }
  
  private static String getFileNameWithoutExtension(String paramString)
  {
    int i = paramString.lastIndexOf(File.separatorChar);
    if (i != -1) {
      paramString = paramString.substring(i + 1);
    }
    i = paramString.lastIndexOf('.');
    String str = paramString;
    if (i != -1) {
      str = paramString.substring(0, i);
    }
    return str;
  }
  
  private static String[] getPhoneNumbers(Context paramContext, String paramString)
  {
    paramContext = paramString.replace('p', ',').replace('s', '*').split("_");
    for (int i = 0; i < paramContext.length; i++)
    {
      int j = paramContext[i].indexOf('(');
      int k = paramContext[i].indexOf(')');
      if ((j > 0) && (k > j)) {
        paramContext[i] = paramContext[i].substring(j + 1, k);
      }
    }
    return paramContext;
  }
  
  public static long getRecordingCreationTime(Context paramContext, String paramString)
  {
    long l1 = 0L;
    paramString = getFileNameWithoutExtension(paramString);
    int i = paramString.lastIndexOf('_');
    paramContext = paramString;
    if (i != -1) {
      paramContext = paramString.substring(i + 1);
    }
    try
    {
      long l2 = sDateFormat.parse(paramContext).getTime();
      l1 = l2;
    }
    catch (Exception paramContext) {}
    return l1;
  }
  
  public static String getRecordingFileTitle(String paramString)
  {
    int i = paramString.indexOf('@');
    if ((i != -1) && (i != paramString.length() - 1) && (!paramString.substring(0, i).contains("_")))
    {
      int j = paramString.lastIndexOf('_');
      String str = paramString;
      if (j > i) {
        str = paramString.substring(0, j);
      }
      return str.substring(i + 1);
    }
    return paramString;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/RecordingNameUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */