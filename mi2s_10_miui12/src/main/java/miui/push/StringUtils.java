package miui.push;

import android.text.TextUtils;
import android.util.Base64;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class StringUtils
{
  private static final char[] AMP_ENCODE;
  private static final char[] APOS_ENCODE;
  private static final char[] GT_ENCODE;
  private static final char[] LT_ENCODE;
  private static final char[] QUOTE_ENCODE = "&quot;".toCharArray();
  private static char[] numbersAndLetters = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
  private static Random randGen;
  
  static
  {
    APOS_ENCODE = "&apos;".toCharArray();
    AMP_ENCODE = "&amp;".toCharArray();
    LT_ENCODE = "&lt;".toCharArray();
    GT_ENCODE = "&gt;".toCharArray();
    randGen = new Random();
  }
  
  public static String encodeBase64(String paramString)
  {
    Object localObject = null;
    try
    {
      paramString = paramString.getBytes("ISO-8859-1");
    }
    catch (UnsupportedEncodingException paramString)
    {
      paramString.printStackTrace();
      paramString = (String)localObject;
    }
    return encodeBase64(paramString);
  }
  
  public static String encodeBase64(byte[] paramArrayOfByte)
  {
    return encodeBase64(paramArrayOfByte, false);
  }
  
  public static String encodeBase64(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 0;
    } else {
      i = 2;
    }
    return Base64.encodeToString(paramArrayOfByte, paramInt1, paramInt2, i);
  }
  
  public static String encodeBase64(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    return encodeBase64(paramArrayOfByte, 0, paramArrayOfByte.length, paramBoolean);
  }
  
  public static String escapeForXML(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = 0;
    int j = 0;
    char[] arrayOfChar = paramString.toCharArray();
    int k = arrayOfChar.length;
    StringBuilder localStringBuilder = new StringBuilder((int)(k * 1.3D));
    while (i < k)
    {
      int m = arrayOfChar[i];
      int n;
      if (m > 62)
      {
        n = j;
      }
      else if (m == 60)
      {
        if (i > j) {
          localStringBuilder.append(arrayOfChar, j, i - j);
        }
        n = i + 1;
        localStringBuilder.append(LT_ENCODE);
      }
      else if (m == 62)
      {
        if (i > j) {
          localStringBuilder.append(arrayOfChar, j, i - j);
        }
        n = i + 1;
        localStringBuilder.append(GT_ENCODE);
      }
      else if (m == 38)
      {
        if (i > j) {
          localStringBuilder.append(arrayOfChar, j, i - j);
        }
        if ((k > i + 5) && (arrayOfChar[(i + 1)] == '#') && (Character.isDigit(arrayOfChar[(i + 2)])) && (Character.isDigit(arrayOfChar[(i + 3)])) && (Character.isDigit(arrayOfChar[(i + 4)])))
        {
          n = j;
          if (arrayOfChar[(i + 5)] == ';') {}
        }
        else
        {
          n = i + 1;
          localStringBuilder.append(AMP_ENCODE);
        }
      }
      else if (m == 34)
      {
        if (i > j) {
          localStringBuilder.append(arrayOfChar, j, i - j);
        }
        n = i + 1;
        localStringBuilder.append(QUOTE_ENCODE);
      }
      else
      {
        n = j;
        if (m == 39)
        {
          if (i > j) {
            localStringBuilder.append(arrayOfChar, j, i - j);
          }
          n = i + 1;
          localStringBuilder.append(APOS_ENCODE);
        }
      }
      i++;
      j = n;
    }
    if (j == 0) {
      return paramString;
    }
    if (i > j) {
      localStringBuilder.append(arrayOfChar, j, i - j);
    }
    return localStringBuilder.toString();
  }
  
  public static byte[] getBytes(String paramString)
  {
    try
    {
      byte[] arrayOfByte = paramString.getBytes("UTF-8");
      return arrayOfByte;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return paramString.getBytes();
  }
  
  public static String getMd5Digest(String paramString)
  {
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
      localMessageDigest.update(getBytes(paramString));
      paramString = new java/math/BigInteger;
      paramString.<init>(1, localMessageDigest.digest());
      paramString = String.format("%1$032X", new Object[] { paramString });
      return paramString;
    }
    catch (NoSuchAlgorithmException paramString)
    {
      throw new RuntimeException(paramString);
    }
  }
  
  public static boolean isValidXmlChar(char paramChar)
  {
    boolean bool;
    if (((paramChar < ' ') || (paramChar > 55295)) && ((paramChar < 57344) || (paramChar > 65533)) && ((paramChar < 65536) || (paramChar > 1114111)) && (paramChar != '\t') && (paramChar != '\n') && (paramChar != '\r')) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static String randomString(int paramInt)
  {
    if (paramInt < 1) {
      return null;
    }
    char[] arrayOfChar = new char[paramInt];
    for (paramInt = 0; paramInt < arrayOfChar.length; paramInt++) {
      arrayOfChar[paramInt] = ((char)numbersAndLetters[randGen.nextInt(71)]);
    }
    return new String(arrayOfChar);
  }
  
  public static final String replace(String paramString1, String paramString2, String paramString3)
  {
    if (paramString1 == null) {
      return null;
    }
    int i = paramString1.indexOf(paramString2, 0);
    if (i >= 0)
    {
      char[] arrayOfChar = paramString1.toCharArray();
      paramString3 = paramString3.toCharArray();
      int j = paramString2.length();
      StringBuilder localStringBuilder = new StringBuilder(arrayOfChar.length);
      localStringBuilder.append(arrayOfChar, 0, i);
      localStringBuilder.append(paramString3);
      int k = i + j;
      for (i = k;; i = k)
      {
        k = paramString1.indexOf(paramString2, k);
        if (k <= 0) {
          break;
        }
        localStringBuilder.append(arrayOfChar, i, k - i);
        localStringBuilder.append(paramString3);
        k += j;
      }
      localStringBuilder.append(arrayOfChar, i, arrayOfChar.length - i);
      return localStringBuilder.toString();
    }
    return paramString1;
  }
  
  public static String stripInvalidXMLChars(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return paramString;
    }
    StringBuilder localStringBuilder = new StringBuilder(paramString.length());
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if (isValidXmlChar(c)) {
        localStringBuilder.append(c);
      }
    }
    return localStringBuilder.toString();
  }
  
  public static final String unescapeFromXML(String paramString)
  {
    return replace(replace(replace(replace(replace(paramString, "&lt;", "<"), "&gt;", ">"), "&quot;", "\""), "&apos;", "'"), "&amp;", "&");
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/push/StringUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */