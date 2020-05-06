package org.apache.miui.commons.lang3;

public class CharUtils
{
  private static final String[] CHAR_STRING_ARRAY = new String[''];
  public static final char CR = '\r';
  public static final char LF = '\n';
  
  static
  {
    int i = 0;
    for (int j = i;; j = i)
    {
      String[] arrayOfString = CHAR_STRING_ARRAY;
      if (j >= arrayOfString.length) {
        break;
      }
      arrayOfString[j] = String.valueOf(j);
      i = (char)(j + 1);
    }
  }
  
  public static boolean isAscii(char paramChar)
  {
    boolean bool;
    if (paramChar < '') {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isAsciiAlpha(char paramChar)
  {
    boolean bool;
    if (((paramChar >= 'A') && (paramChar <= 'Z')) || ((paramChar >= 'a') && (paramChar <= 'z'))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isAsciiAlphaLower(char paramChar)
  {
    boolean bool;
    if ((paramChar >= 'a') && (paramChar <= 'z')) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isAsciiAlphaUpper(char paramChar)
  {
    boolean bool;
    if ((paramChar >= 'A') && (paramChar <= 'Z')) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isAsciiAlphanumeric(char paramChar)
  {
    boolean bool;
    if (((paramChar >= 'A') && (paramChar <= 'Z')) || ((paramChar >= 'a') && (paramChar <= 'z')) || ((paramChar >= '0') && (paramChar <= '9'))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isAsciiControl(char paramChar)
  {
    boolean bool;
    if ((paramChar >= ' ') && (paramChar != '')) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isAsciiNumeric(char paramChar)
  {
    boolean bool;
    if ((paramChar >= '0') && (paramChar <= '9')) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isAsciiPrintable(char paramChar)
  {
    boolean bool;
    if ((paramChar >= ' ') && (paramChar < '')) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static char toChar(Character paramCharacter)
  {
    if (paramCharacter != null) {
      return paramCharacter.charValue();
    }
    throw new IllegalArgumentException("The Character must not be null");
  }
  
  public static char toChar(Character paramCharacter, char paramChar)
  {
    if (paramCharacter == null) {
      return paramChar;
    }
    return paramCharacter.charValue();
  }
  
  public static char toChar(String paramString)
  {
    if (!StringUtils.isEmpty(paramString)) {
      return paramString.charAt(0);
    }
    throw new IllegalArgumentException("The String must not be empty");
  }
  
  public static char toChar(String paramString, char paramChar)
  {
    if (StringUtils.isEmpty(paramString)) {
      return paramChar;
    }
    return paramString.charAt(0);
  }
  
  @Deprecated
  public static Character toCharacterObject(char paramChar)
  {
    return Character.valueOf(paramChar);
  }
  
  public static Character toCharacterObject(String paramString)
  {
    if (StringUtils.isEmpty(paramString)) {
      return null;
    }
    return Character.valueOf(paramString.charAt(0));
  }
  
  public static int toIntValue(char paramChar)
  {
    if (isAsciiNumeric(paramChar)) {
      return paramChar - '0';
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("The character ");
    localStringBuilder.append(paramChar);
    localStringBuilder.append(" is not in the range '0' - '9'");
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public static int toIntValue(char paramChar, int paramInt)
  {
    if (!isAsciiNumeric(paramChar)) {
      return paramInt;
    }
    return paramChar - '0';
  }
  
  public static int toIntValue(Character paramCharacter)
  {
    if (paramCharacter != null) {
      return toIntValue(paramCharacter.charValue());
    }
    throw new IllegalArgumentException("The character must not be null");
  }
  
  public static int toIntValue(Character paramCharacter, int paramInt)
  {
    if (paramCharacter == null) {
      return paramInt;
    }
    return toIntValue(paramCharacter.charValue(), paramInt);
  }
  
  public static String toString(char paramChar)
  {
    if (paramChar < '') {
      return CHAR_STRING_ARRAY[paramChar];
    }
    return new String(new char[] { paramChar });
  }
  
  public static String toString(Character paramCharacter)
  {
    if (paramCharacter == null) {
      return null;
    }
    return toString(paramCharacter.charValue());
  }
  
  public static String unicodeEscaped(char paramChar)
  {
    if (paramChar < '\020')
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("\\u000");
      localStringBuilder.append(Integer.toHexString(paramChar));
      return localStringBuilder.toString();
    }
    if (paramChar < 'Ā')
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("\\u00");
      localStringBuilder.append(Integer.toHexString(paramChar));
      return localStringBuilder.toString();
    }
    if (paramChar < 'က')
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("\\u0");
      localStringBuilder.append(Integer.toHexString(paramChar));
      return localStringBuilder.toString();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\\u");
    localStringBuilder.append(Integer.toHexString(paramChar));
    return localStringBuilder.toString();
  }
  
  public static String unicodeEscaped(Character paramCharacter)
  {
    if (paramCharacter == null) {
      return null;
    }
    return unicodeEscaped(paramCharacter.charValue());
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/miui/commons/lang3/CharUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */