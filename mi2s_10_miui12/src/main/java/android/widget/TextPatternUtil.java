package android.widget;

import android.text.TextUtils;
import android.util.Patterns;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TextPatternUtil
{
  private static final Pattern EMAIL_PATTERN = Patterns.EMAIL_ADDRESS;
  private static final int MAX_EMAIL_ADDR_LENGTH = 256;
  
  public static EmailInfo findEmailAtPos(String paramString, int paramInt)
  {
    int i = paramInt - 256;
    if (i < 0) {
      i = 0;
    }
    int j = paramInt + 256;
    if (j > paramString.length()) {
      j = paramString.length();
    }
    paramString = getEmailList(i, paramInt, paramString.substring(i, j));
    if ((paramString != null) && (paramString.size() > 0))
    {
      Iterator localIterator = paramString.iterator();
      while (localIterator.hasNext())
      {
        paramString = (EmailInfo)localIterator.next();
        if (inRange(paramInt, paramString.start, paramString.start + paramString.email.length())) {
          return paramString;
        }
      }
    }
    return null;
  }
  
  public static List<EmailInfo> getEmailList(int paramInt1, int paramInt2, String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return new ArrayList();
    }
    String str = null;
    Matcher localMatcher = EMAIL_PATTERN.matcher(paramString);
    while (localMatcher.find())
    {
      paramString = str;
      if (str == null) {
        paramString = new ArrayList();
      }
      int i = 0;
      str = localMatcher.group();
      Iterator localIterator = paramString.iterator();
      int j;
      for (;;)
      {
        j = i;
        if (!localIterator.hasNext()) {
          break;
        }
        if (((EmailInfo)localIterator.next()).isContainedIn(str))
        {
          j = 1;
          break;
        }
      }
      if (j == 0) {
        paramString.add(new EmailInfo(localMatcher.start() + paramInt1, paramInt2, str));
      }
      str = paramString;
    }
    return str;
  }
  
  private static boolean inRange(int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool;
    if ((paramInt1 >= paramInt2) && (paramInt1 <= paramInt3)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  static class EmailInfo
  {
    int cursorPos;
    String email;
    int start;
    
    EmailInfo(int paramInt1, int paramInt2, String paramString)
    {
      this.start = paramInt1;
      this.cursorPos = paramInt2;
      this.email = paramString;
    }
    
    boolean isContainedIn(String paramString)
    {
      String str = this.email;
      if ((str != null) && (paramString != null))
      {
        str = str.toLowerCase();
        paramString = paramString.toLowerCase();
        if (str.equals(paramString)) {
          return true;
        }
        return paramString.contains(str);
      }
      return false;
    }
    
    boolean isInList(List<String> paramList)
    {
      if ((paramList != null) && (paramList.size() > 0))
      {
        paramList = paramList.iterator();
        while (paramList.hasNext()) {
          if (isContainedIn((String)paramList.next())) {
            return true;
          }
        }
      }
      return false;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("EmailInfo{start = ");
      localStringBuilder.append(this.start);
      localStringBuilder.append(", email = ");
      localStringBuilder.append(this.email);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/TextPatternUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */