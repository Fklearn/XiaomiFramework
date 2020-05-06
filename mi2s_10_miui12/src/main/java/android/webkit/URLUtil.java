package android.webkit;

import android.annotation.UnsupportedAppUsage;
import android.net.ParseException;
import android.net.Uri;
import android.net.WebAddress;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class URLUtil
{
  static final String ASSET_BASE = "file:///android_asset/";
  static final String CONTENT_BASE = "content:";
  private static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile("attachment;\\s*filename\\s*=\\s*(\"?)([^\"]*)\\1\\s*$", 2);
  static final String FILE_BASE = "file:";
  private static final String LOGTAG = "webkit";
  static final String PROXY_BASE = "file:///cookieless_proxy/";
  static final String RESOURCE_BASE = "file:///android_res/";
  private static final boolean TRACE = false;
  
  public static String composeSearchUrl(String paramString1, String paramString2, String paramString3)
  {
    int i = paramString2.indexOf(paramString3);
    if (i < 0) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString2.substring(0, i));
    try
    {
      localStringBuilder.append(URLEncoder.encode(paramString1, "utf-8"));
      localStringBuilder.append(paramString2.substring(paramString3.length() + i));
      return localStringBuilder.toString();
    }
    catch (UnsupportedEncodingException paramString1) {}
    return null;
  }
  
  public static byte[] decode(byte[] paramArrayOfByte)
    throws IllegalArgumentException
  {
    if (paramArrayOfByte.length == 0) {
      return new byte[0];
    }
    byte[] arrayOfByte = new byte[paramArrayOfByte.length];
    int i = 0;
    int j = 0;
    while (j < paramArrayOfByte.length)
    {
      int k = paramArrayOfByte[j];
      int m = j;
      int n = k;
      if (k == 37) {
        if (paramArrayOfByte.length - j > 2)
        {
          n = (byte)(parseHex(paramArrayOfByte[(j + 1)]) * 16 + parseHex(paramArrayOfByte[(j + 2)]));
          m = j + 2;
        }
        else
        {
          throw new IllegalArgumentException("Invalid format");
        }
      }
      arrayOfByte[i] = ((byte)n);
      j = m + 1;
      i++;
    }
    paramArrayOfByte = new byte[i];
    System.arraycopy(arrayOfByte, 0, paramArrayOfByte, 0, i);
    return paramArrayOfByte;
  }
  
  public static final String guessFileName(String paramString1, String paramString2, String paramString3)
  {
    String str1 = null;
    Object localObject1 = null;
    Object localObject2 = null;
    String str2 = str1;
    if (0 == 0)
    {
      str2 = str1;
      if (paramString2 != null)
      {
        paramString2 = parseContentDisposition(paramString2);
        str2 = paramString2;
        if (paramString2 != null)
        {
          i = paramString2.lastIndexOf('/') + 1;
          str2 = paramString2;
          if (i > 0) {
            str2 = paramString2.substring(i);
          }
        }
      }
    }
    paramString2 = str2;
    if (str2 == null)
    {
      str1 = Uri.decode(paramString1);
      paramString2 = str2;
      if (str1 != null)
      {
        i = str1.indexOf('?');
        paramString1 = str1;
        if (i > 0) {
          paramString1 = str1.substring(0, i);
        }
        paramString2 = str2;
        if (!paramString1.endsWith("/"))
        {
          i = paramString1.lastIndexOf('/') + 1;
          paramString2 = str2;
          if (i > 0) {
            paramString2 = paramString1.substring(i);
          }
        }
      }
    }
    str2 = paramString2;
    if (paramString2 == null) {
      str2 = "downloadfile";
    }
    int i = str2.indexOf('.');
    if (i < 0)
    {
      paramString2 = (String)localObject2;
      if (paramString3 != null)
      {
        paramString1 = MimeTypeMap.getSingleton().getExtensionFromMimeType(paramString3);
        paramString2 = paramString1;
        if (paramString1 != null)
        {
          paramString2 = new StringBuilder();
          paramString2.append(".");
          paramString2.append(paramString1);
          paramString2 = paramString2.toString();
        }
      }
      localObject1 = str2;
      paramString1 = paramString2;
      if (paramString2 == null) {
        if ((paramString3 != null) && (paramString3.toLowerCase(Locale.ROOT).startsWith("text/")))
        {
          if (paramString3.equalsIgnoreCase("text/html"))
          {
            paramString1 = ".html";
            localObject1 = str2;
          }
          else
          {
            paramString1 = ".txt";
            localObject1 = str2;
          }
        }
        else
        {
          paramString1 = ".bin";
          localObject1 = str2;
        }
      }
    }
    else
    {
      paramString1 = (String)localObject1;
      if (paramString3 != null)
      {
        int j = str2.lastIndexOf('.');
        paramString2 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str2.substring(j + 1));
        paramString1 = (String)localObject1;
        if (paramString2 != null)
        {
          paramString1 = (String)localObject1;
          if (!paramString2.equalsIgnoreCase(paramString3))
          {
            paramString2 = MimeTypeMap.getSingleton().getExtensionFromMimeType(paramString3);
            paramString1 = paramString2;
            if (paramString2 != null)
            {
              paramString1 = new StringBuilder();
              paramString1.append(".");
              paramString1.append(paramString2);
              paramString1 = paramString1.toString();
            }
          }
        }
      }
      paramString2 = paramString1;
      if (paramString1 == null) {
        paramString2 = str2.substring(i);
      }
      localObject1 = str2.substring(0, i);
      paramString1 = paramString2;
    }
    paramString2 = new StringBuilder();
    paramString2.append((String)localObject1);
    paramString2.append(paramString1);
    return paramString2.toString();
  }
  
  public static String guessUrl(String paramString)
  {
    if (paramString.length() == 0) {
      return paramString;
    }
    if (paramString.startsWith("about:")) {
      return paramString;
    }
    if (paramString.startsWith("data:")) {
      return paramString;
    }
    if (paramString.startsWith("file:")) {
      return paramString;
    }
    if (paramString.startsWith("javascript:")) {
      return paramString;
    }
    Object localObject = paramString;
    if (paramString.endsWith(".") == true) {
      localObject = paramString.substring(0, paramString.length() - 1);
    }
    try
    {
      localObject = new WebAddress((String)localObject);
      if (((WebAddress)localObject).getHost().indexOf('.') == -1)
      {
        paramString = new StringBuilder();
        paramString.append("www.");
        paramString.append(((WebAddress)localObject).getHost());
        paramString.append(".com");
        ((WebAddress)localObject).setHost(paramString.toString());
      }
      return ((WebAddress)localObject).toString();
    }
    catch (ParseException localParseException) {}
    return paramString;
  }
  
  public static boolean isAboutUrl(String paramString)
  {
    boolean bool;
    if ((paramString != null) && (paramString.startsWith("about:"))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isAssetUrl(String paramString)
  {
    boolean bool;
    if ((paramString != null) && (paramString.startsWith("file:///android_asset/"))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isContentUrl(String paramString)
  {
    boolean bool;
    if ((paramString != null) && (paramString.startsWith("content:"))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @Deprecated
  public static boolean isCookielessProxyUrl(String paramString)
  {
    boolean bool;
    if ((paramString != null) && (paramString.startsWith("file:///cookieless_proxy/"))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isDataUrl(String paramString)
  {
    boolean bool;
    if ((paramString != null) && (paramString.startsWith("data:"))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isFileUrl(String paramString)
  {
    boolean bool;
    if ((paramString != null) && (paramString.startsWith("file:")) && (!paramString.startsWith("file:///android_asset/")) && (!paramString.startsWith("file:///cookieless_proxy/"))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isHttpUrl(String paramString)
  {
    boolean bool = false;
    if ((paramString != null) && (paramString.length() > 6) && (paramString.substring(0, 7).equalsIgnoreCase("http://"))) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isHttpsUrl(String paramString)
  {
    boolean bool = false;
    if ((paramString != null) && (paramString.length() > 7) && (paramString.substring(0, 8).equalsIgnoreCase("https://"))) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isJavaScriptUrl(String paramString)
  {
    boolean bool;
    if ((paramString != null) && (paramString.startsWith("javascript:"))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isNetworkUrl(String paramString)
  {
    boolean bool = false;
    if ((paramString != null) && (paramString.length() != 0))
    {
      if ((isHttpUrl(paramString)) || (isHttpsUrl(paramString))) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  @UnsupportedAppUsage
  public static boolean isResourceUrl(String paramString)
  {
    boolean bool;
    if ((paramString != null) && (paramString.startsWith("file:///android_res/"))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isValidUrl(String paramString)
  {
    boolean bool = false;
    if ((paramString != null) && (paramString.length() != 0))
    {
      if ((isAssetUrl(paramString)) || (isResourceUrl(paramString)) || (isFileUrl(paramString)) || (isAboutUrl(paramString)) || (isHttpUrl(paramString)) || (isHttpsUrl(paramString)) || (isJavaScriptUrl(paramString)) || (isContentUrl(paramString))) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  @UnsupportedAppUsage
  static String parseContentDisposition(String paramString)
  {
    try
    {
      paramString = CONTENT_DISPOSITION_PATTERN.matcher(paramString);
      if (paramString.find())
      {
        paramString = paramString.group(2);
        return paramString;
      }
    }
    catch (IllegalStateException paramString) {}
    return null;
  }
  
  private static int parseHex(byte paramByte)
  {
    if ((paramByte >= 48) && (paramByte <= 57)) {
      return paramByte - 48;
    }
    if ((paramByte >= 65) && (paramByte <= 70)) {
      return paramByte - 65 + 10;
    }
    if ((paramByte >= 97) && (paramByte <= 102)) {
      return paramByte - 97 + 10;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Invalid hex char '");
    localStringBuilder.append(paramByte);
    localStringBuilder.append("'");
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public static String stripAnchor(String paramString)
  {
    int i = paramString.indexOf('#');
    if (i != -1) {
      return paramString.substring(0, i);
    }
    return paramString;
  }
  
  @UnsupportedAppUsage
  static boolean verifyURLEncoding(String paramString)
  {
    int i = paramString.length();
    if (i == 0) {
      return false;
    }
    int j = paramString.indexOf('%');
    for (;;)
    {
      if ((j < 0) || (j >= i)) {
        break label78;
      }
      if (j < i - 2)
      {
        j++;
        try
        {
          parseHex((byte)paramString.charAt(j));
          j++;
          parseHex((byte)paramString.charAt(j));
          j = paramString.indexOf('%', j + 1);
        }
        catch (IllegalArgumentException paramString)
        {
          return false;
        }
      }
    }
    return false;
    label78:
    return true;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/URLUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */